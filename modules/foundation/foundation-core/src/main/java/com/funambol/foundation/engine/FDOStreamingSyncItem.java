/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.foundation.engine;

import java.io.File;

import java.sql.Timestamp;

import java.util.TimeZone;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.parser.FileDataObjectParsingException;
import com.funambol.common.media.file.parser.FileDataObjectStreamingUnmarshaller;

import com.funambol.foundation.util.FileSystemDAOHelper;

import com.funambol.framework.engine.StreamingSyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemException;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.SyncProperty;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Responsibility: manage the large object's chunks
 * Collaborator: 
 * - FileSystemDAOHelper to manage the temporary file on the file system
 * - FileDataObjectStreamingUnmarshaller to unmarshal the chunks while
 *   receiving them and create the FileDataObject
 *
 * @version $Id$
 */
public class FDOStreamingSyncItem extends StreamingSyncItem {

    // -------------------------------------------------------- Member Variables

    /**
     * used to manage the temporary file on the file system
     */
    private FileSystemDAOHelper fileDAO;

    /**
     * The unmarshaller to use to parse chunk by chunk the item
     */
    private FileDataObjectStreamingUnmarshaller unmarshaller = null;

    /**
     * the total content size of the received chunks
     */
    private long  contentSize = 0   ;

    /**
     * the temporary file name
     */
    private String tmpFileName = null;

    public String getTmpFileName() {
        return tmpFileName;
    }

    /**
     * The FileDataObject resulting from all the chunks unmarshaling
     */
    private FileDataObject fileDataObject;

    /**
     * The logger used by the FDOStreamingSyncItem class
     */
    private static final FunambolLogger logger = FunambolLoggerFactory.getLogger("funambol.engine.fdo");

    // ------------------------------------------------------------- Constructor

    protected FDOStreamingSyncItem(SyncSource syncSource, Object key,
            FileSystemDAOHelper fileDAO) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             SyncItemState.UNKNOWN,
             null,  // format
             null,  // type
             null,  // timestamp
             fileDAO
             );
    }

    protected FDOStreamingSyncItem(SyncSource syncSource,
            Object key,
            Object parentKey,
            Object mappedKey,
            char state,
            String format,
            String type,
            Timestamp timestamp,
            FileSystemDAOHelper fileDAO) {

        super(syncSource, key, parentKey, mappedKey, state, format,type,timestamp );

        this.fileDAO = fileDAO;

        this.tmpFileName = fileDAO.createTmpFileName();

        File tmpBodyFile = fileDAO.getFile(tmpFileName);

        /**
         * @todo: what about timezone ?
         */
        this.unmarshaller = new FileDataObjectStreamingUnmarshaller((TimeZone)null, tmpBodyFile);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Writes chunkData.length bytes from the specified byte array to this output stream
     * @param chunkData the data.
     * @throws SyncItemException if an error occurs.
     */
    public void write(byte[] chunkData) throws SyncItemException {
        if (chunkData == null) {
            // ignoring it
            return;
        }
        if (isComplete()) {
            throw new SyncItemException("Error writing data to large object: item already complete");
        }

        try {
            unmarshaller.unmarshall(chunkData);
        } catch (FileDataObjectParsingException ex) {
            SyncItemException sie = new SyncItemException("Error writing data", ex);
            throw sie;
        }
        contentSize += chunkData.length;
    }

    /**
     * Reset the large object and release resources.
     */
    public void release() {
        File file = fileDAO.getFile(tmpFileName);
        file.delete();
        contentSize = 0;
    }

    /**
     * set as complete the stream
     */
    @Override
    public void setAsComplete() {
        super.setAsComplete();
        setProperty(new SyncProperty(PROPERTY_CONTENT_SIZE, new Long(contentSize)));
    }

    /**
     * Creates a copy of the FDOStreamingSyncItem object
     * The new FDOStreamingSyncItem and the original one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @return
     */
    public Object cloneItem() {

        FDOStreamingSyncItem syncItem = new FDOStreamingSyncItem(this.syncSource,
                                                                 this.key.getKeyValue(),
                                                                 this.fileDAO);

        syncItem.copy(this);

        return syncItem;
    }

    /**
     * Makes the current item a copy of the item passed as parameter
     * The current item and the parameter one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @param syncItem
     */
    @Override
    public void copy(SyncItem syncItem) {

        super.copy(syncItem);

        // member variable content for a FDOStreamingSyncItem is always null,
        // so if the parameter syncItem is not a FDOStreamingSyncItem we set
        // content to null.
        //super.setContent(null);

        if (syncItem instanceof FDOStreamingSyncItem) {
            this.contentSize = ((FDOStreamingSyncItem)syncItem).contentSize;
            this.fileDAO =     ((FDOStreamingSyncItem)syncItem).fileDAO;
            this.tmpFileName = ((FDOStreamingSyncItem)syncItem).tmpFileName;
            this.fileDataObject = ((FDOStreamingSyncItem) syncItem).fileDataObject;
            this.unmarshaller = ((FDOStreamingSyncItem) syncItem).unmarshaller;
        }
    }

    /**
     * Returns the FileDataObject related to this sync item
     * @return the FileDataObject related to this sync item
     * @throws FileDataObjectParsingException
     */
    public FileDataObject getFileDataObject() throws FileDataObjectParsingException {
        if (!isComplete()) {
            return null;
        }
        if (fileDataObject == null) {
            fileDataObject = unmarshaller.getFileDataObject();
        }
        return fileDataObject;
    }

    /**
     * Used when the content is not divided into pieces
     * @param content
     */
    public void setContent(byte[] content){
        try {
             write(content);
        } catch (SyncItemException ex) {
            logger.error("Error writing content", ex);
            this.setProperty(new SyncProperty(StreamingSyncItem.PROPERTY_ERROR_MSG, ex));
        }
    }

}
