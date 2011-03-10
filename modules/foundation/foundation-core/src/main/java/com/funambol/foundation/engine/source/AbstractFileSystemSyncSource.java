/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

import java.security.Principal;

import java.sql.Timestamp;

import java.util.*;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.funambol.framework.core.AlertCode;
import com.funambol.framework.core.StatusCode;
import com.funambol.framework.engine.*;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;


/**
 * This is a base class for all file system syncsource.
 *
 * IMPORTANT NOTE: on FAT16/32 file system the last modified timestamp of a file
 * is accurate with precision (...) of no more than 2 seconds (all timestamps have
 * even seconds and no milliseconds).
 * For instance, all times between 10:35:02,001 and 10:35:03,999 are rounded to
 * 10:35:04,000. On this filesystem, the risk is to detect a change more times.
 * On EXT2/3 file system the last modified timestamp of a file
 * is accurate with precision (...) of no more than 1 seconds (all timestamps have
 * not milliseconds).
 * For instance, all times between 10:35:03,001 and 10:35:03,999 are truncated to
 * 10:35:03,000. On this filesystem, the risk is to not detect a change because the
 * update time is truncated.
 * To avoid to detect a change more times (on FAT32) and to avoid to not detect
 * a change (on EXT3), we round also the since timestamp according to the
 * underlying FS.
 *
 * LOG NAME: funambol.foundation
 *
 * @version $Id: AbstractFileSystemSyncSource.java,v 1.1.1.1 2008-03-20 21:38:32 stefano_fornari Exp $
 */
public abstract class AbstractFileSystemSyncSource extends AbstractSyncSource
    implements SyncSource, Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants

    public static final String DATABASE_FILE_NAME    = "sync.db";
    public static final String DATABASE_HEADER       = "FileSystemSyncSource file database";
    public static final String FORMAT_BASE64         = "b64";

    public static final String LOG_NAME = "foundation";

    /** It's used just for the id generation so the core db must be used */
    private static final String DEFAULT_DATASOURCE_NAME = "jdbc/fnblcore";

    // ---------------------------------------------------------- Protected data

    protected static DBIDGenerator  dbIDGenerator   = null;

    protected Map            itemStates             = null;

    protected List           updatedItemsKeys       = null;
    protected List           newItemsKeys           = null;
    protected List           deletedItemsKeys       = null;
    protected List           allItemsKeys           = null;

    protected boolean        filesScanned           = false;

    protected Properties     syncDatabase           = null;
    protected long           syncDatabaseTimestamp  = 0;

    protected FunambolLogger log                    = null;

    protected boolean        isFAT32                = false;
    protected boolean        isEXT3                 = false;

    protected Map            crcs                   = null;

    // -------------------------------------------------------------- Properties

    /**
     * The principal to sync
     */
    protected Sync4jPrincipal principal = null;

    /**
     * Getter for property principal
     * @return Sync4jPrincipal
     */
    public Sync4jPrincipal getPrincipal() {
        return principal;
    }

    /**
     * Setter for property principal
     * @param principal Sync4jPrincipal
     */
    public void setPrincipal(Sync4jPrincipal principal) {
        this.principal = principal;
    }

    /** The jndi name of the datasource to use to create a DBIDGenerator */
    private String jndiDataSourceName;

    public String getJndiDataSourceName() {
        return jndiDataSourceName;
    }

    public void setJndiDataSourceName(String jndiDataSourceName) {
        this.jndiDataSourceName = jndiDataSourceName;
    }

    /**
     * The context of the sync
     */
    protected SyncContext syncContext = null;

    /**
     * Getter for property syncContext
     * @return SyncContext
     */
    public SyncContext getSyncContext() {
        return syncContext;
    }

    /**
     * The directory where files are stored (the default is the current
     * directory) - read/write
     */
    protected String sourceDirectory = ".";

    public void setSourceDirectory(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    public String getSourceDirectory() {
        return this.sourceDirectory;
    }

    /**
     * Should the content be encoded?
     */
    protected boolean encode = true;

    /**
     * Getter for property encode.
     * @return Value of property encode.
     */
    public boolean isEncode() {
        return encode;
    }

    /**
     * Setter for property encode.
     * @param encode New value of property encode.
     */
    public void setEncode(boolean encode) {
        this.encode = encode;
    }

    /**
     * Should the items be ordered?
     */
    protected boolean orderSyncItems = false;

    /**
     * Getter for property orderSyncItems.
     * @return Value of property orderSyncItems.
     */
    public boolean isOrderSyncItems() {
        return orderSyncItems;
    }

    /**
     * Setter for property orderSyncItems.
     * @param orderSyncItems New value of property orderSyncItems.
     */
    public void setOrderSyncItems(boolean orderSyncItems) {
        this.orderSyncItems = orderSyncItems;
    }

    protected boolean multiUser = false;

    /**
     * Getter for property multiUser.
     * @return Value of property multiUser.
     */
    public boolean isMultiUser() {
        return multiUser;
    }

    /**
     * Setter for property multiUser.
     * @param multiUser New value of property multiUser.
     */
    public void setMultiUser(boolean multiUser) {
        this.multiUser = multiUser;
    }

    // ------------------------------------------------------------ Constructors

    public AbstractFileSystemSyncSource() {
        this(null, null);
    }

    public AbstractFileSystemSyncSource(String name, String sourceDirectory) {
        super(name);
        this.sourceDirectory = sourceDirectory;
        this.log = FunambolLoggerFactory.getLogger("engine");
    }

    /**
     * The name defaults to the directory name
     */
    public AbstractFileSystemSyncSource(String sourceDirectory) {
        this(new File(sourceDirectory).getName(), sourceDirectory);
    }


    // ---------------------------------------------------------- Public Methods

    public void init() throws BeanInitializationException {
        
        DataSource dataSource = null;
        try {
            if (jndiDataSourceName == null || "".equals(jndiDataSourceName)) {
                jndiDataSourceName = DEFAULT_DATASOURCE_NAME;
            }
            dataSource = DataSourceTools.lookupDataSource(jndiDataSourceName);
        } catch (NamingException ex) {
            throw new BeanInitializationException("Error looking up datasource: " + jndiDataSourceName);
        }
        
        dbIDGenerator = DBIDGeneratorFactory.getDBIDGenerator("guid", dataSource);

        //
        // Why do do this? see class description :)
        //
        //
        // To detect if the file system rounds the last modified timestamp,
        // we create a temporary file and set its last modified timestamp.
        // Then we re-read the timestamp and if it is different from the one
        // set, it means that it was rounded.
        // If the difference is greater than 0, it means we are on EXT3.
        // If the difference is smaller than 0, it means we are on FAT32.
        //
        try {
            //
            // We know that the following timestamp is rounded on FAT32 FS and
            // on EXT2 FS
            //
            final long time = 1114166888033l;
            File f = File.createTempFile("s4j", "", new File(getSourceDirectory()));

            f.setLastModified(time);
            long lastModified = f.lastModified();
            long offset = time - lastModified;
            if ( offset < 0) {
                isFAT32 = true;
            } else if (offset > 0) {
                isEXT3 = true;
            }

            f.delete();
        } catch (IOException ex) {
        }

    }

    /**
     * @see SyncSource
     */
    public SyncItem addSyncItem(SyncItem syncItem)
    throws SyncSourceException {
        try {

            File file = null;
            file      = getNewFile(getUserSourceDirectory(principal));

            ++howManyAdded;

            String state = buildStateString(SyncItemState.NEW,
                                            roundTime(syncItem.getTimestamp().getTime()));
            syncDatabase.setProperty(file.getName(), state);


            return setSyncItem(syncItem, file);

        } catch (Exception e) {
            throw new SyncSourceException( "Error setting the item "
                                          + syncItem
                                          , e
                                          );
        }
    }

    /**
     * @see SyncSource
     */
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {


        File file = null;
        String fileName = null;

        fileName = syncItem.getKey().getKeyAsString();

        file = new File(getUserSourceDirectory(principal), fileName);

        ++howManyUpdated;

        return setSyncItem(syncItem, file);

    }

    /**
     * @see SyncSource
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException{

       File file = new File(getUserSourceDirectory(principal), syncItemKey.getKeyAsString());
       SyncItem item = getSyncItem(file, SyncItemState.UNKNOWN);
       String sItemState = (String) itemStates.get(syncItemKey.getKeyAsString());
       char itemState = SyncItemState.UNKNOWN;
       if (sItemState != null) {
           itemState = sItemState.charAt(0);
       }
       item.setState(itemState);
       return item;
    }

    /**
     * @see SyncSource
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs,
                                            Timestamp untilTs)
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, sinceTs);
        }

        return orderSyncItemKeys((SyncItemKey[])newItemsKeys.toArray(new SyncItemKey[0]));
    }


    /**
     * @see SyncSource
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,
                                               Timestamp untilTs)
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, sinceTs);
        }

        return orderSyncItemKeys((SyncItemKey[])deletedItemsKeys.toArray(new SyncItemKey[0]));
    }

    /**
     * @see SyncSource
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs,
                                                Timestamp untilTs)
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, sinceTs);
        }

        return orderSyncItemKeys((SyncItemKey[])updatedItemsKeys.toArray(new SyncItemKey[0]));
    }

    /**
     * @see SyncSource
     */
    public SyncItemKey[] getAllSyncItemKeys()
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, null);
        }

        return orderSyncItemKeys((SyncItemKey[])allItemsKeys.toArray(new SyncItemKey[0]));
    }


    /**
     * @see SyncSource
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        //
        // Search the twins comparing the crc
        //
        long crc = getItemCRC(syncItem.getContent(), "item: " + syncItem.getKey().getKeyAsString());
        List twinsItem = (List) crcs.get(new Long(crc));
        if (twinsItem == null) {
            return new SyncItemKey[0];
        }
        return (SyncItemKey[]) twinsItem.toArray(new SyncItemKey[0]);
    }


    /**
     * @see SyncSource
     */
    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp   time,
                               boolean     softDelete)
    throws SyncSourceException {
        if (softDelete) {
            //
            // Ignore soft delete
            //
            return ;
        } else {
            removeSyncItem(syncItemKey, time);
        }
    }


    /**
     * A string representation of this object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer(super.toString());

        sb.append(" - { ");
        sb.append("name: ").append(name).append(", ");
        sb.append("sourceDirectory: ").append(sourceDirectory).append(" }");

        return sb.toString();
    }

    /**
     * SyncSource's beginSync()
     *
     * @param syncContext @see SyncSource
     */
    public void beginSync(SyncContext syncContext) throws SyncSourceException {

        super.beginSync(syncContext);

        this.syncContext  = syncContext;
        this.principal    = syncContext.getPrincipal();

        if (syncContext.getSyncMode() == AlertCode.REFRESH_FROM_CLIENT) {
            removeAllFiles();
        }

        //
        // In a slow sync we have to delete the SyncDatabase file
        //
        if (syncContext.getSyncMode() == AlertCode.SLOW) {
            File fileSyncDatabase = getDatabaseFile(principal);
            if (fileSyncDatabase.isFile() && fileSyncDatabase.exists()) {
                fileSyncDatabase.delete();
            }
        }

        if (allItemsKeys != null) {
            allItemsKeys.clear();
        } else {
            allItemsKeys = new ArrayList();
        }

        if (updatedItemsKeys != null) {
            updatedItemsKeys.clear();
        } else {
            updatedItemsKeys = new ArrayList();
        }

        if (newItemsKeys != null) {
            newItemsKeys.clear();
        } else {
            newItemsKeys = new ArrayList();
        }

        if (deletedItemsKeys != null) {
            deletedItemsKeys.clear();
        } else {
            deletedItemsKeys = new ArrayList();
        }

        if (itemStates != null) {
            itemStates.clear();
        } else {
            itemStates = new HashMap();
        }

        if (crcs != null) {
            crcs.clear();
        } else {
            crcs = new HashMap();
        }

    }

    /**
     * SyncSource's endSync()
     */
    public void endSync() throws SyncSourceException {

        super.endSync();

        storeSyncDatabase(principal, syncDatabase);
    }

    /**
     * @see SyncSource
     */
    public void commitSync() throws SyncSourceException {
        storeSyncDatabase(principal, syncDatabase);
    }

    /**
     * @see SyncSource
     * When an error status is received for an item NEW, we remove the item from
     * the syncDatabase so in the next sync, the item is re-detected as NEW.
     */
    public void setOperationStatus(String operation, int statusCode, SyncItemKey[] keys) {
        String sItemState;
        char itemState = 0;

        for (int i = 0; i < keys.length; i++) {

            if (statusCode != StatusCode.OK &&
                statusCode != StatusCode.ITEM_ADDED &&
                statusCode != StatusCode.ALREADY_EXISTS) {

                sItemState = (String) itemStates.get(keys[i].getKeyAsString());
                if (sItemState != null) {
                    itemState = sItemState.charAt(0);
                }
                if (itemState == SyncItemState.NEW) {
                    //
                    // We have received a error status code for a new item.
                    // We have to remark it as New.
                    // Removing it from the syncDatabase the item, in the next
                    // sync, will be recognized as New
                    //
                    syncDatabase.remove(keys[i].getKeyAsString());
                }
            }

        }

        if (log.isTraceEnabled()) {
            StringBuffer message = new StringBuffer("Received status code '");
            message.append(statusCode).append("' for a '").
                    append(operation).append("'").
                    append(" for these items: ");

            for (int i = 0; i < keys.length; i++) {
                message.append("\n- " + keys[i].getKeyAsString());
            }
            log.trace(message.toString());
        }


    }

    // ------------------------------------------------------- Protected methods

    /**
     * Retrieval function for the path to the data storage.
     * The path to the data storage consists of the
     * configured sourceDirectory and the username.
     * In a system where each username is unique this will provide a
     * per user unique storage.
     * @param principal The principal on behalf the path is requested.
     */
    protected String getUserSourceDirectory(Principal principal) {
        File   srcdir    = null;
        String directory = null;
        if (multiUser) {
            directory = getSourceDirectory() + "/" + ((Sync4jPrincipal)principal).getUsername();
        } else {
            directory = getSourceDirectory();
        }
        srcdir = new File(directory);
        if (!srcdir.isDirectory()) {
            srcdir.mkdirs();
        }
        return directory;
    }

    /**
     * Returns the state of the item with the given key.
     * @param key String
     * @return char
     */
    protected char getItemState(String key) {
        String state = (String)itemStates.get(key);
        if (state == null) {
            return SyncItemState.NOT_EXISTING;
        }
        return state.charAt(0);
    }


    /**
     * Retrieval function for file containing information about the synchronized files.#
     * @param principal The principal on behalf the database file is requested.
     */
    protected File getDatabaseFile(Principal principal) {
        String devId = ((Sync4jPrincipal)principal).getDeviceId();
        if (devId == null) {
            devId = DATABASE_FILE_NAME;
        } else {
            devId = devId.replace(':', '_');
        }
        String dbfile = devId + "." + DATABASE_FILE_NAME;
        return new File(getUserSourceDirectory(principal), dbfile);
    }

    protected String buildStateString(char state, long lastModified) {
        return state + String.valueOf(lastModified);
    }

    protected long lastModifiedFromStateString(String state) {
        return Long.parseLong(state.substring(1));
    }

    protected char stateFromStateString(String state) {
        if ((state == null) || (state.length() == 0)) {
            return SyncItemState.UNKNOWN;
        }
        return state.charAt(0);
    }

    // --------------------------------------------------------- Private methods

    /**
     * The synchronization database is stored in a file whose name is given
     * by the value of the constant DATABASE_FILE_NAME.
     * The database is a property file where each entry has the following
     * format:
     * <pre>
     * [filename]=[state][lastmodified_timestamp]
     * </pre>
     * For example:
     * <blockquote>
     * readme.txt=N98928743098094
     * </blockquote>
     * <p>
     * scanFiles works as follows:
     * <ul>
     *   <li>a file is set to NEW if:
     *     <ul>
     *       <li>it isn't in the database file</li>
     *       <li> or if it's in the database with state N and with a timestamp
     *            equal or greater than since</li>
     *     </ul>
     *   </li>
     *   <li>a file is set to DELETED if:
     *     <ul>
     *       <li>it is in the database with state != 'D' but it doesn't exist</li>
     *       <li> or if it's in the database with state D and with a timestamp
     *            equal or greater than since (whatever the file exists or not)</li>
     *     </ul>
     *   </li>
     *   <li>a file is set to UPDATED if:
     *     <ul>
     *       <li>it is not in the previous cases and if his lastmodified timestamp
     *           is greater than since. His lastmodified timestamp is the real
     *           file last timestamp or the timestamp specified in the database
     *           file with state 'U'</li>
     *     </ul>
     *   </li>
     * </ul>
     * <br/>otherwise a file is set to SYNCHRONIZED.
     * @param principal Principal
     * @param sinceTs Timestamp
     * @return Properties
     * @throws SyncSourceException
     */
    protected Properties scanFiles(Principal principal,
                                   Timestamp sinceTs) throws SyncSourceException {

        syncDatabase = new Properties();

        try {
            File fileSyncDatabase = getDatabaseFile(principal);
            syncDatabaseTimestamp = fileSyncDatabase.lastModified();

            if (log.isTraceEnabled()) {
                if (isFAT32) {
                    log.trace("Filesystem FAT32");
                } else if (isEXT3) {
                    log.trace("Filesystem EXT3");
                } else {
                    log.trace("Filesystem not FAT32 and not EXT3");
                }
                log.trace("Reading syncdatabase file (" + fileSyncDatabase.getAbsolutePath() + ")");
            }
            //
            // Reads the existing database
            //
            if (fileSyncDatabase.exists()) {
                FileInputStream fis = new FileInputStream(fileSyncDatabase);
                syncDatabase.load(fis);
                fis.close();
            }

            //
            // Get the list of files in the source directory
            //
            List existingFiles = getExistingFiles(principal);

            //
            // Get the list of the file in the databsae
            //
            Enumeration databaseFiles = syncDatabase.propertyNames();

            String fileName = null;

            long lastModified = 0;
            long since        = 0;

            if (sinceTs != null) {
                since = sinceTs.getTime();
            }

            //
            // Rounds the time according to the underlying FS
            //
            since = roundTime(since);

            File   file           = null;
            char fileStateInDB    = SyncItemState.UNKNOWN;
            String stateString    = null;
            long lastModifiedInDB = 0;

            for (Iterator i = existingFiles.iterator(); i.hasNext();) {

                fileName     = (String)i.next();
                file         = new File(getUserSourceDirectory(principal), fileName);

                if (!file.isFile()) {
                    //
                    // We skip the directories
                    //
                    continue;
                }
                lastModified = file.lastModified();

                stateString  = syncDatabase.getProperty(fileName);
                if (log.isTraceEnabled()) {
                    log.trace("Analyzing file: " + fileName + ", state: " + stateString);
                }
                if (stateString != null) {
                    //
                    // The file is already in the database
                    //
                    fileStateInDB    = stateFromStateString(stateString);
                    lastModifiedInDB = lastModifiedFromStateString(stateString);

                    if (fileStateInDB == SyncItemState.NEW) {
                        if (log.isTraceEnabled()) {
                            log.trace("The file '" + fileName + " is in the db file " +
                                       "with state 'N'. Checking the creation " +
                                       "date (" + lastModifiedInDB + ")"
                            );

                            log.trace("Check if " + lastModifiedInDB +
                                       " > " + since
                            );

                        }

                        //
                        // We have to check if the creation timestamp is greater
                        // or equal than the sinceTs
                        //
                        if (lastModifiedInDB > since) {
                            //
                            // The file has a creation timestamp > sinceTs
                            //
                            if (log.isTraceEnabled()) {
                                log.trace("The file '" + fileName + "' is NEW");
                            }

                            SyncItemKey key = new SyncItemKey(fileName);
                            long crc = getItemCRC(IOTools.readFileBytes(file), 
                                                  "file: " + file.getAbsolutePath());
                            storeCRC(key, crc);

                            //
                            // We set the item as New with since+1. In this way,
                            // if the client suspends the session ant then restart
                            // it, we detect again the item as NEW (there is the
                            // same since).
                            //
                            stateString = buildStateString(SyncItemState.NEW, since + 1);
                            syncDatabase.setProperty(fileName, stateString);
                            newItemsKeys.add(key);
                            allItemsKeys.add(key);
                            itemStates.put(fileName, String.valueOf(SyncItemState.NEW));

                            continue;
                        }

                    } else if (fileStateInDB == SyncItemState.DELETED) {

                        if (log.isTraceEnabled()) {
                            //This happens when a file deleted " +
                            //           "in a previous sync is re-added

                            log.trace("The file '" + fileName + " is in the db file " +
                                       "with state 'D'. We set is as NEW " +
                                       "(it has been deleted in a previous sync");

                        }

                        //
                        // We set the item as New with since+1. In this way,
                        // if the client suspends the session ant then restart
                        // it, we detect again the item as NEW (there is the
                        // same since).
                        //
                        stateString = buildStateString(SyncItemState.NEW, since + 1);
                        syncDatabase.setProperty(fileName, stateString);

                        SyncItemKey key = new SyncItemKey(fileName);

                        newItemsKeys.add(key);
                        allItemsKeys.add(key);
                        itemStates.put(fileName, String.valueOf(SyncItemState.NEW));

                        long crc = getItemCRC(IOTools.readFileBytes(file), 
                                              "file: " + file.getAbsolutePath());
                        storeCRC(key, crc);

                        continue;

                    } else if (fileStateInDB == SyncItemState.UPDATED) {
                        if (log.isTraceEnabled()) {
                            log.trace("The file '" + fileName + " is in the db file " +
                                       "with state 'U'. The last modified is set to: " +
                                       lastModifiedInDB
                            );
                        }
                        lastModified = lastModifiedInDB;
                    }


                    if (log.isTraceEnabled()) {
                        log.trace("lastModified: " + lastModified +
                                   ", since: " + since
                         );
                    }

                    //
                    // Checks the lastModified to find the UPDATED items
                    //
                    if ((lastModified) > since) {

                        if (log.isTraceEnabled()) {
                            log.trace("Set file '" + fileName + "' as UPDATED");
                        }

                        long crc = getItemCRC(IOTools.readFileBytes(file), 
                                              "file: " + file.getAbsolutePath());
                        SyncItemKey key = new SyncItemKey(fileName);
                        storeCRC(key, crc);

                        updatedItemsKeys.add(key);
                        allItemsKeys.add(key);

                        itemStates.put(fileName, String.valueOf(SyncItemState.UPDATED));

                        continue;
                    } else {

                        if (log.isTraceEnabled()) {
                            log.trace("Set file '" + fileName + "' as SYNCHRONIZED");
                        }

                        long crc = getItemCRC(IOTools.readFileBytes(file), 
                                              "file: " + file.getAbsolutePath());
                        SyncItemKey key = new SyncItemKey(fileName);
                        storeCRC(key, crc);

                        allItemsKeys.add(key);
                        itemStates.put(fileName, String.valueOf(SyncItemState.SYNCHRONIZED));

                        continue;
                    }
                } else {
                    //
                    // The file is not in the database. We put it in the db
                    // using the sinceTs
                    //
                    long crc = getItemCRC(IOTools.readFileBytes(file), 
                                          "file: " + file.getAbsolutePath());
                    SyncItemKey key = new SyncItemKey(fileName);
                    storeCRC(key, crc);

                    //
                    // We set the item as New with since+1. In this way,
                    // if the client suspends the session and then restart
                    // it, we detect again the item as NEW (there is the
                    // same since).
                    //
                    stateString = buildStateString(SyncItemState.NEW, since + 1);
                    syncDatabase.setProperty(fileName, stateString);

                    newItemsKeys.add(key);
                    allItemsKeys.add(key);

                    itemStates.put(fileName, String.valueOf(SyncItemState.NEW));

                    continue;
                }
            }  // next i (all existing files)


            for (; databaseFiles.hasMoreElements();) {
                fileName         = (String)databaseFiles.nextElement();
                stateString      = syncDatabase.getProperty(fileName);
                fileStateInDB    = stateFromStateString(stateString);
                lastModifiedInDB = lastModifiedFromStateString(stateString);

                if (!existingFiles.contains(fileName)) {

                    if (fileStateInDB == SyncItemState.DELETED) {
                        //
                        // We have to check the deletion time
                        //
                        if (lastModifiedInDB >= since) {

                            if (log.isTraceEnabled()) {
                                log.trace("The file '" + fileName +
                                           " is in the db file " +
                                           "with state 'D' and the deletion time (" +
                                           lastModifiedInDB + ") is greater of " +
                                           "the since (" + since + ")."
                                        );
                                log.trace("Set file '" + fileName + "' as DELETED");
                            }

                            deletedItemsKeys.add(new SyncItemKey(fileName));
                            itemStates.put(fileName, String.valueOf(SyncItemState.DELETED));

                            continue;
                        }
                    } else {

                        if (log.isTraceEnabled()) {
                            log.trace("The file '" + fileName +
                                       " is in the db file " +
                                       "with state different from 'D'."
                                    );

                            log.trace("Set file '" + fileName + "' as DELETED");
                        }

                        //
                        // We set the item as DELETED with since+1. In this way,
                        // if the client suspends the session ant then restart
                        // it, we detect again the item as DELETED (there is the
                        // same since).
                        //
                        stateString = buildStateString(SyncItemState.DELETED, since + 1);
                        syncDatabase.setProperty(fileName, stateString);

                        deletedItemsKeys.add(new SyncItemKey(fileName));
                        itemStates.put(fileName,
                                       String.valueOf(SyncItemState.DELETED));

                        continue;

                    }
                }
            }  // next
        } catch (IOException e) {
            log.error("Error reading sync database", e);
        }

        filesScanned = true;
        return syncDatabase;
    }

    /**
     * Stores the modified database.
     * The current database is stored only if the syncDatabase file isn't updated
     * in the meantime.
     *
     * @param principal the principal
     * @param sdb state database
     */
    protected void storeSyncDatabase(Principal principal, Properties sdb) {

        File fileSyncDatabase = getDatabaseFile(principal);

        long currentLastModifiedTimestamp = fileSyncDatabase.lastModified();

        if (syncDatabaseTimestamp < currentLastModifiedTimestamp) {
            //
            // Somebody has changed the syncDb file so we can't save it. This
            // happens for example running the test suite. If a test doesn't
            // complete the session, the holder is discharged after a lot of time
            // and the endSync is called. But in the meantime others syncs are performed
            // so we can't write the syncDatabase of a previous sync.
            //
            if (log.isTraceEnabled()) {
                log.trace(
                        "The syncDatabase file has a lastModified time greater " +
                        "of the read time. This means somebody has changed this file, so " +
                        "we can't update it ");
            }
        } else {

            try {
                FileOutputStream fos = new FileOutputStream(fileSyncDatabase);
                sdb.store(fos, DATABASE_HEADER);
                fos.flush();
                fos.close();
                syncDatabaseTimestamp = fileSyncDatabase.lastModified();

                if (log.isTraceEnabled()) {
                    ByteArrayOutputStream bout =
                        new ByteArrayOutputStream();
                    PrintWriter writer = new PrintWriter(bout);
                    sdb.list(writer);
                    writer.flush();
                    byte[] b = bout.toByteArray();
                    writer.close();
                    bout.close();
                    String props = new String(b);

                    log.trace(DATABASE_HEADER + "\n" + props);
                }

            } catch (IOException e) {
                log.error("Error storing sync database", e);
            }
        }
    }

    /**
     * Returns a list of the available files for the given principal
     * @param principal the principal
     * @return the list of files
     * @throws IOException
     */
    protected List getExistingFiles(Principal principal) throws IOException {
        List ret = new ArrayList();

        String[] files = new File(getUserSourceDirectory(principal)).list();

        if (files != null) {
            for (int i = 0; i<files.length; ++i) {
                if (!files[i].endsWith(DATABASE_FILE_NAME)) {
                    ret.add(files[i]);
                }
            }  // next i
        }

        return ret;
    }

    /**
     * Creates and returns a new file in the given directory.
     * @param directory String
     * @return File
     * @throws IOException
     */
    protected File getNewFile(String directory) throws Exception {
        File file = new File(directory, String.valueOf(getGUID()));
        return file;
    }

    /**
     * Returns a new GUID
     * @return int
     */
    protected long getGUID() throws DBIDGeneratorException {
        return dbIDGenerator.next();
    }

    /**
     * Returns the keys of the items in the given list
     * @param items list
     * @return SyncItemKey[]
     */
    protected SyncItemKey[] getKeys(List items) {
        if (items == null) {
            return new SyncItemKey[0];
        }

        int size = items.size();
        SyncItemKey[] keys = new SyncItemKey[size];
        Iterator itItems = items.iterator();
        int i = 0;
        while (itItems.hasNext()) {
            keys[i++] = ((SyncItem)itItems.next()).getKey();
        }

        return keys;
    }

    /**
     * Orders the given keys array
     * @param keys SyncItemKey[]
     * @return SyncItemKey[]
     */
    protected SyncItemKey[] orderSyncItemKeys(SyncItemKey[] keys) {

        if (!orderSyncItems) {
            return keys;
        }

        if (keys == null || keys.length == 0) {
            return keys;
        }

        Arrays.sort(keys);

        return keys;
    }

    /**
     * Removes the item with the given itemKey marking the item deleted with the
     * give time.
     * @param syncItemKey the key of the item to remove
     * @param time the deletion time
     */
    protected void removeSyncItem(SyncItemKey syncItemKey, Timestamp time)
    throws SyncSourceException {
        String fileName = syncItemKey.getKeyAsString();

        new File(getUserSourceDirectory(principal), fileName).delete();

        String state = buildStateString(SyncItemState.DELETED, roundTime(time.getTime()));
        syncDatabase.setProperty(fileName, state);

        ++howManyDeleted;
    }


    /**
     * Removes all files. Used in REFRESH_FROM_CLIENT (see beginSync)
     */
    protected void removeAllFiles() throws SyncSourceException {
        File[] filesToDelete =
            new File(getUserSourceDirectory(principal)).listFiles();
        for (int i = 0; i < filesToDelete.length; i++) {
            if (filesToDelete[i].isFile()) {
                // only the sync items must be deleted
                if(!(filesToDelete[i].getName().endsWith(DATABASE_FILE_NAME))){
                    filesToDelete[i].delete();
                }
            }
        }
    }

    /**
     * Rounds the given time according to the underlying FS.
     * If this is <code>EXT3</code> the time is rounded
     * calling <code>roundTimeEXT3</code> otherwise if this is
     * <code>FAT32</code> the time is rounded calling
     * <code>roundTimeFAT32</code>.
     * @param time the time to round
     * @return long the time rounded
     */
    protected long roundTime(long time) {
        if (isEXT3) {
            return roundTimeEXT3(time);
        } else if (isFAT32) {
            return roundTimeFAT32(time);
        }

        return time;
    }

    /**
     * Rounds the given time according to the approximation done by the EXT3
     * filesystem. The milliseconds are simply truncated.
     * @param time the time to round
     * @return long the time rounded
     */
    protected long roundTimeEXT3(long time) {
        return time / 1000 * 1000;
    }

    /**
     * Rounds the given time according to the approximation done by the FAT32
     * filesystem.
     * In the FAT32 all times haven't milliseconds and have only even seconds.
     * So, for instance, all times between 10:35:32,001 and 10:35:33,999 are
     * rounded in 10:35:34,000.
     * @param time the time to round
     * @return long the time rounded
     */
    protected long roundTimeFAT32(long originalTime) {

        long time = originalTime / 1000;
        long offset;
        if ((time % 2) == 0) {
            offset = 2000;
        } else {
            offset = 1000;
        }

        time = time * 1000;

        if (originalTime != time || offset == 1000) {
            time = time + offset;
        }
        return time;
    }


    /**
     * Stores the crc for the item with the given key
     * @param key SyncItemKey
     * @param crc long
     */
    protected void storeCRC(SyncItemKey key, long crc) {
        List items = (List)crcs.get(new Long(crc));
        if (items == null) {
            items = new ArrayList();
            crcs.put(new Long(crc), items);
        }
        items.add(key);
    }

    // -------------------------------------------------------- Abstract Methods

    /**
     * Returns a SyncItem with the content of the given file and the given state.
     * @param file the file to read
     * @param state the item state
     * @return SyncItem an item with the content of the given file and the given state
     * @throws SyncSourceException if an error occurs
     */
    protected abstract SyncItem getSyncItem(File file, char state)
            throws SyncSourceException;

    /**
     * Returns the CRC value for the given content using the values of the fields
     * contained in </code>targetsToCompare</code>
     *
     * @param value the byte array used in the crc computation
     * @param contentDescription a description of the content (used in exception
     *        message)
     * @return CRC of value
     **/
    protected abstract long getItemCRC(byte[] content, String contentDescription)
            throws SyncSourceException;


    /**
     * Sets the given syncItem in the given file.
     * @param syncItem the item to set
     * @param file the file where set the item
     * @return SyncItem
     * @throws SyncSourceException
     */
    protected abstract SyncItem setSyncItem(SyncItem syncItem,
                                            File file) throws SyncSourceException;




}
