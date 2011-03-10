/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License version 3 as published by
 * the Free Software Foundation with the addition of the following permission
 * added to Section 15 as permitted in Section 7(a): FOR ANY PART OF THE COVERED
 * WORK IN WHICH THE COPYRIGHT IS OWNED BY FUNAMBOL, FUNAMBOL DISCLAIMS THE
 * WARRANTY OF NON INFRINGEMENT  OF THIRD PARTY RIGHTS.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301 USA.
 *
 * You can contact Funambol, Inc. headquarters at 643 Bair Island Road, Suite
 * 305, Redwood City, CA 94063, USA, or at email address info@funambol.com.
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License version 3.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License
 * version 3, these Appropriate Legal Notices must retain the display of the
 * "Powered by Funambol" logo. If the display of the logo is not reasonably
 * feasible for technical reasons, the Appropriate Legal Notices must display
 * the words "Powered by Funambol".
 */
package com.funambol.foundation.engine.source;

import java.io.*;

import java.sql.Timestamp;

import java.util.*;

import com.funambol.framework.engine.*;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SourceUtils;
import com.funambol.framework.tools.merge.MergeResult;
import com.funambol.framework.tools.merge.MergeUtils;

import com.funambol.server.config.Configuration;

/**
 * This is a SyncSource to sync items in SIF format.
 *
 * @version $Id: SIFSyncSource.java,v 1.1.1.1 2008-03-20 21:38:35 stefano_fornari Exp $
 */
public class SIFSyncSource extends AbstractSIFSyncSource
    implements MergeableSyncSource, Serializable {

    // --------------------------------------------------------------- Constants
    public static final String DATABASE_HEADER = "SIFSyncSource file database";

    // ------------------------------------------------------------ Private Data
    // -------------------------------------------------------------- Properties

    // ------------------------------------------------------------ Constructors
    public SIFSyncSource() {
        this(null, null);
    }

    public SIFSyncSource(String name, String sourceDirectory) {
        super(name, sourceDirectory);
    }

    /**
     * The name defaults to the directory name
     */
    public SIFSyncSource(String sourceDirectory) {
        this(new File(sourceDirectory).getName(), sourceDirectory);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Merges serverItem and clientItem
     * @param serverItem the item on the server
     * @param clientItem the item on the client
     * @return SyncItem the mergedItem
     * @throws SyncSourceException
     */
    public boolean mergeSyncItems(SyncItemKey serverKey,
                                  SyncItem clientItem) throws SyncSourceException {

        SyncItem serverItem = getSyncItemFromId(serverKey);

        String fileName = serverKey.getKeyAsString();

        File file   = new File(getUserSourceDirectory(principal), fileName);

        Map mapA, mapB = null;

        try {

            mapA = getMapFromItem(clientItem);
            mapB = getMapFromItem(serverItem);

        } catch (Exception ex) {
            throw new SyncSourceException("Error getting item properties", ex);
        }


        MergeResult result = MergeUtils.mergeMap(mapA, mapB);

        if (result.isSetBRequired()) {
            //
            // The serverMap is changed. We have to store it.
            //
            try {
                setFile(mapB,
                        file,
                        clientItem.getTimestamp());
                //
                // We have also to change the item in cache
                //
                SyncItem tmp = getSyncItem(file, SyncItemState.UPDATED);
                serverItem.setContent(tmp.getContent());

            } catch (Exception ex) {
                throw new SyncSourceException("Error updating the file '" + file + "'", ex);
            }
        }

        if (result.isSetARequired()) {
            //
            // The item on the client has to be updated
            //
            String xml = null;
            try {
                xml = SourceUtils.hashMapToXml(new TreeMap(mapB));
            } catch (Exception e) {
                throw new SyncSourceException("Error converting the merged map into xml", e);
            }

            if (encode) {
                clientItem.setContent(Base64.encode(xml.getBytes()));
            } else {
                clientItem.setContent(xml.getBytes());
            }

        }

        //
        // If funambol is not in the debug mode is not possible to print the
        // merge result because it contains sensitive data.
        //
        if (Configuration.getConfiguration().isDebugMode()) {
            if (log.isTraceEnabled()) {
                log.trace("MergeResult: " + result);
            }
        }
        return result.isSetARequired();
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Sets the given item in the given file
     * @param principal Principal
     * @param syncItem SyncItem
     * @param file File
     * @return SyncItem
     * @throws SyncSourceException
     */
    protected SyncItem setSyncItem(SyncItem  syncItem,
                                   File      file)
    throws SyncSourceException {
        try {

            Map itemMap = getMapFromItem(syncItem);

            String fileName   = file.getName();

            Map fileMap = null;

            if (syncContext.getConflictResolution() ==
                    SyncContext.CONFLICT_RESOLUTION_MERGE_DATA){
                if (file.exists() && file.isFile()) {
                    fileMap = SourceUtils.xmlToHashMap(
                                  IOTools.readFileString(file)
                              );
                    fileMap.putAll(itemMap);
                } else {
                    fileMap = itemMap;
                }
            } else {
                fileMap = itemMap;
            }

            Timestamp t = syncItem.getTimestamp();

            setFile(fileMap, file, t);

            SyncItem newSyncItem =
                    new SyncItemImpl(this,
                                     fileName,
                                     null,
                                     SyncItemState.NEW,
                                     syncItem.getContent(),
                                     syncItem.getFormat(),
                                     syncItem.getType(),
                                     syncItem.getTimestamp()
                    );


            return newSyncItem;

        } catch (IOException e) {
            throw new SyncSourceException( "Error setting the item "
                                           + syncItem
                                           , e
                    );
        } catch (Exception e) {
             throw new SyncSourceException( "Error setting the hashmap in item "
                                           + syncItem
                                           , e
                                           );
         }
    }

    /**
     * This method is implemented just to use the filterSyncItem in this class
     * also in the FilterableSyncSource. In this call, it returns always true;
     * @param id String
     * @param xml String
     * @param clause FilterClause
     * @throws Exception
     */
    protected boolean checkItem(String id,
                                String xml,
                                FilterClause clause) throws Exception {

        return true;
    }

    /**
     * This method is implemented just to use the filterSyncItem in this class
     * also in the FilterableSyncSource. In this call, it returns always true;
     * @param id String
     * @param properties Map
     * @param clause FilterClause
     * @return boolean
     * @throws Exception
     */
    protected boolean checkItem(String id,
                                Map properties,
                                FilterClause clause,
                                boolean justIDClause)  {

        return true;
    }



    // --------------------------------------------------------- Private methods

    /**
     * Return a map from the content item
     * @param item SyncItem
     * @return Map
     */
    private Map getMapFromItem(SyncItem item) throws Exception  {
        byte[] itemContentA = item.getContent();

        if (itemContentA == null) {
            itemContentA = new byte[0];
        }

        HashMap map = null;

        if (encode && itemContentA.length > 0) {
            itemContentA = Base64.decode(itemContentA);
        }

        if (itemContentA.length > 0) {
            map = SourceUtils.xmlToHashMap(new String(itemContentA));
        } else {
            map = new HashMap();
        }

        return map;
    }

    /**
     * Stores the given properties map in the given file
     * @param map Map
     * @param file File
     * @param time Timestamp
     * @throws Exception
     */
    private void setFile(Map map,
                         File file,
                         Timestamp time) throws Exception {

        IOTools.writeFile(SourceUtils.hashMapToXml(new TreeMap(map)), file);

        if (log.isTraceEnabled()) {
            log.trace("Original timestamp: " + time.getTime());
            log.trace("Set file last modified for '" + file.getName() + "' to: " +
                       (time.getTime()));
        }
        file.setLastModified(time.getTime());

        if (log.isTraceEnabled()) {
            log.trace("New last modified for '" + file.getName() + "': " +
                       file.lastModified());
        }

    }


    protected SyncItem getSyncItem(File file, char state) throws SyncSourceException {

        SyncItem syncItem = new SyncItemImpl(this, file.getName(), state);

        syncItem.setType(this.getInfo().getPreferredType().getType());

        try {
            byte[] content = IOTools.readFileBytes(file);

            if (encode) {
                content = Base64.encode(content);
                syncItem.setFormat(FORMAT_BASE64);
            }

            syncItem.setContent(content);

        } catch (Exception e) {
            throw new SyncSourceException("Error reading "
                                          + file.getName()
                                          , e
                    );

        }

        return syncItem;
    }

    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        long crc = getItemCRC(syncItem.getContent(), "item: " + syncItem.getKey().getKeyAsString());
        List twinsItem = (List) crcs.get(new Long(crc));
        if (twinsItem == null) {
            return new SyncItemKey[0];
        }
        return (SyncItemKey[]) twinsItem.toArray(new SyncItemKey[0]);

    }

}
