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
package com.funambol.foundation.engine.source;

import java.sql.Timestamp;

import java.io.IOException;
import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.TimeZone;
import java.util.Map;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.parser.FileDataObjectMarshaller;
import com.funambol.common.media.file.parser.FileDataObjectParsingException;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemFactory;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.StreamingSyncSource;
import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.SyncProperty;
import com.funambol.framework.engine.source.DeviceFullException;
import com.funambol.framework.engine.source.PartialContentException;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.core.AlertCode;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.server.Sync4jSource;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.beans.BeanException;

import com.funambol.foundation.engine.FDOStreamingSyncItemFactory;
import com.funambol.foundation.engine.FDOStreamingSyncItem;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.exception.FileDataObjecySyncSourceConfigException;
import com.funambol.foundation.exception.QuotaOverflowException;
import com.funambol.foundation.items.manager.FileDataObjectManager;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.Def;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;

import com.funambol.server.config.Configuration;

/**
 * A sync source for file data objects as defined by the Open Mobile Alliance.
 *
 * @version $Id$
 */
public class FileDataObjectSyncSource
extends AbstractSyncSource
implements StreamingSyncSource, Serializable, LazyInitBean {

    //---------------------------------------------------------------- Constants

    public static final String FDO_MIME_TYPE =  "application/vnd.omads-file+xml";
    public static final long   NO_QUOTA_LIMIT = -1L;

    //----------------------------------------------------------- Protected data

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    //------------------------------------------------------------- Private data

    protected FileDataObjectSyncSourceConfig config;

    /**
     * The manager to which many operations are delegated
     */
    private FileDataObjectManager manager;

    /**
     * The device timezone used as reference to convert the local dates into UTC
     */
    private TimeZone deviceTimeZone = null;

    /**
     * The factory for StreamingSyncItems
     */
    private FDOStreamingSyncItemFactory streamingSyncItemFactory = null;

    //--------------------------------------------------------------- Properties

    /** The context of the sync. */
    private SyncContext syncContext = null;

    public SyncContext getContext(){
        return syncContext;
    }

    /**
     *
     * @param rootPath
     * @deprecated
     */
    public void setRootPath(String rootPath) {
        config.setRootPath(rootPath);
    }

    /**
     *
     * @return
     * @deprecated
     */
    public String getRootPath() {
        return config.getRootPath();
    }

    public void setStorageFilesystemRootPath(String storageFilesystemRootPath) {
        config.setStorageFilesystemRootPath(storageFilesystemRootPath);
    }

    public String getStorageFilesystemRootPath() {
        return config.getStorageFilesystemRootPath();
    }

    public void setLocalRootPath(String localRootPath) {
        config.setLocalRootPath(localRootPath);
    }

    public String getLocalRootPath() {
        return config.getLocalRootPath();
    }

    public void setMaxSize(String maxSize) {
        config.setMaxSize(maxSize);
    }

    public String getMaxSize() {
        return config.getMaxSize();
    }

    public long getMaxSizeAsLong() {
        return config.getMaxSizeAsLong();
    }

    public void setRoleQuota(Map<String, String> roleQuota) {
        config.setRoleQuota(roleQuota);
    }

    public Map<String, String> getRoleQuota() {
        return config.getRoleQuota();
    }

    public void setFileSysDirDepth(int fileSysDirDepth) {
        config.setFileSysDirDepth(fileSysDirDepth);
    }

    public int getFileSysDirDepth() {
        return config.getFileSysDirDepth();
    }

    public void setStorageProvider(String storageProvider) {
        config.setStorageProvider(storageProvider);
    }

    public String getStorageProvider() {
        return config.getStorageProvider();
    }

    public void setStorageIdentity(String storageIdentity) {
        config.setStorageIdentity(storageIdentity);
    }

    public String getStorageIdentity() {
        return config.getStorageIdentity();
    }

    public void setStorageCredential(String storageCredential) {
        config.setStorageCredential(storageCredential);
    }

    public String getStorageCredential() {
        return config.getStorageCredential();
    }

    public void setStorageContainerName(String storageContainerName) {
        config.setStorageContainerName(storageContainerName);
    }

    public String getStorageContainerName() {
        return config.getStorageContainerName();
    }

    public void setAdditionalProperties(Map<String, String[]> additionalProperties) {
        config.setAdditionalProperties(additionalProperties);
    }

    public Map<String, String[]> getAdditionalProperties() {
        return config.getAdditionalProperties();
    }

    public boolean getPropagateDelete() {
        return config.getPropagateDelete();
    }

    public void setPropagateDelete(boolean propagateDelete) {
        config.setPropagateDelete(propagateDelete);
    }

    // ------------------------------------------------------------ Constructors

    public FileDataObjectSyncSource() {
        this(null);
    }

    public FileDataObjectSyncSource(String name){
        this.config = createFileDataObjectSyncSourceConfig();
        this.name = name;
    }

    //----------------------------------------------------------- Public methods

    @Override
    public void beginSync(SyncContext context)  throws SyncSourceException {
        try {
            this.syncContext = context;
            String username = this.syncContext.getPrincipal().getUsername();
            Sync4jDevice device = this.syncContext.getPrincipal().getDevice();

            this.manager = createNewFileDataObjectManager(context.getPrincipal().getUser(),
                this.sourceURI, this.config);

            deviceTimeZone = retrieveDeviceTimeZone(device);
            if (this.syncContext.getSyncMode() == AlertCode.REFRESH_FROM_CLIENT) {
                if (log.isTraceEnabled()) {
                    log.trace("Perform REFRESH_FROM_CLIENT (203) for user " + username);
                }
                removeAllSyncItems(null);
            }

            //
            // With the new handling of the update item, it's no needed to align
            // the item's size with the value of the storage size, since
            // the item is considered as N, the old file is removed and a new
            // upload is expected.
            //

        } catch (FileDataObjecySyncSourceConfigException ex) {
            throw new SyncSourceException("Error initializing the syncsource", ex);
        } catch (DAOException ex) {
            throw new SyncSourceException("Error initializing the syncsource", ex);
        } catch (IOException ex) {
            throw new SyncSourceException("Error initializing the syncsource", ex);
        } catch (NumberFormatException ex) {
            throw new SyncSourceException("Configuration of quota for roles" +
                " is incorrect", ex);
        }
    }

    @Override
    public void endSync() throws SyncSourceException {
        super.endSync();
        // tmp files older than 24h are removed by this method.
        streamingSyncItemFactory.release();
        try {
            manager.removeExpiredIncompleteItems();
        } catch (EntityException ex) {
            if (log.isErrorEnabled()){
                log.error("Unable to remove expired incomplete items.", ex);
            }
        }
        try {
            manager.close();
        } catch (EntityException ex) {
            if (log.isErrorEnabled()){
                log.error("Error releaseing resources.", ex);
            }
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing the ID(s) of the
     * twin(s) of a given file.
     * At this time we don't perform twin search
     *
     * @param syncItem the SyncItem representing the file whose twin(s) are
     *                 looked for
     * @throws SyncSourceException
     * @return possibly, just one or no key should be in the array, but it can't
     *         be ruled out a priori that several keys get returned by this
     *         method
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {
        return new SyncItemKey[0];
    }

    /**
     * Adds a SyncItem object (representing a file data object).
     *
     * @param syncItem the SyncItem representing the file data object
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.NEW and the GUID retrieved by the
     *         back-end
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

        if (!(syncItem instanceof FDOStreamingSyncItem)) {
            throw new IllegalArgumentException("Unable to handle " + syncItem.getClass());
        }
        FDOStreamingSyncItem fdoSyncItem = (FDOStreamingSyncItem)syncItem;

        try {
            FileDataObject fdo = fdoSyncItem.getFileDataObject();

            Timestamp timeStamp = syncItem.getTimestamp();

            String key = manager.addItem(fdo, timeStamp);

            AbstractSyncItem newSyncItem = new InMemorySyncItem(this,
                                                                key,
                                                                null, // parent key
                                                                SyncItemState.NEW,
                                                                null, // content
                                                                null, // format
                                                                FDO_MIME_TYPE,
                                                                timeStamp);
            if (log.isTraceEnabled()){
                    log.trace("Added " + fdo.toString());
            }

            return newSyncItem;

        } catch (QuotaOverflowException e) {
            if (log.isTraceEnabled()) {
                log.trace("Not able to add an item: the quota has been " +
                          "reached (used " + e.getUsedQuota() + ", max " +
                           e.getMaxQuota() + ", item size " + e.getItemSize() + ")");
            }
            throw new DeviceFullException(e.getMessage(), e.getUsedQuota(), e.getMaxQuota(), e.getItemSize(), true);
        } catch (Exception e) {
            if (log.isErrorEnabled()){
                log.error("SyncSource error adding a new synchronization item.");
            }
            throw new SyncSourceException("Error adding item " + syncItem.getKey(), e);
        }
    }

    /**
     * Updates a SyncItem object (representing a file data object).
     *
     * @param syncItem the SyncItem representing the file data object
     *
     * @return a newly created syncItem based on the input object but with its
     *         status set at SyncItemState.UPDATED and the GUID retrieved by the
     *         back-end
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {

        if (!(syncItem instanceof FDOStreamingSyncItem)) {
            throw new IllegalArgumentException("Unable to handle " + syncItem.getClass());
        }
        FDOStreamingSyncItem fdoSyncItem = (FDOStreamingSyncItem)syncItem;

        try {
            FileDataObject fileDataObject = fdoSyncItem.getFileDataObject();

            String id = syncItem.getKey().getKeyAsString();

            Timestamp timestamp = syncItem.getTimestamp();


            FileDataObjectWrapper fdow = manager.completeAndUpdateItem(id,
                                                                    fileDataObject,
                                                                    timestamp);

            checkResumableUpload(fdow);

            AbstractSyncItem updSyncItem = new InMemorySyncItem(this,
                                                                id,
                                                                null, // parent key
                                                                SyncItemState.UPDATED,
                                                                null, // content
                                                                null, // format
                                                                FDO_MIME_TYPE,
                                                                timestamp);

            if (log.isTraceEnabled()) {
                log.trace(new StringBuilder("Updated file data object with ID ").
                        append(id).append(" on the server, setting ").
                        append(timestamp).append(" as its last update timestamp. ").
                        append(fileDataObject.toString()).toString());
            }

            return updSyncItem;

        } catch (QuotaOverflowException e) {
            if (log.isTraceEnabled()) {
               log.trace("Not able to update an item: the quota has been " +
                         "reached (used " + e.getUsedQuota() + ", max " +
                         e.getMaxQuota() + ", item size " + e.getItemSize() + ")");
            }
            throw new DeviceFullException(e.getMessage(),
                                          e.getUsedQuota(),
                                          e.getMaxQuota(),
                                          e.getItemSize(),
                                          true);
        } catch (PartialContentException e) {
            throw e;
        } catch (Exception e) {
            throw new SyncSourceException("Error updating the item " + syncItem, e);
        }
    }

    /**
     * Deletes the item with a given syncItemKey.
     *
     * @param syncItemKey
     * @param timestamp the registered moment of deletion
     * @param softDelete unused
     * @throws SyncSourceException
     */
    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp   timestamp  ,
                               boolean     softDelete )
    throws SyncSourceException {

        // Call to checkFolderReady() is not strictly necessary in a removal
        if (getPropagateDelete()) {
            try {
                manager.removeItem(syncItemKey.getKeyAsString(), timestamp);
                if (log.isTraceEnabled()) {
                    log.trace("Deleted Item " + syncItemKey.getKeyAsString());
                }

            } catch (EntityException e) {
                if (log.isErrorEnabled()){
                    log.error("Sync source error: could not delete item with key"
                             + syncItemKey, e);
                }
                throw new SyncSourceException("Error deleting item.", e);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Deletion from clients is not propagated");
            }
        }
    }

    /**
     * Gets the item with the given ID.
     *
     * @param syncItemKey the item key
     * @return the file data object as a SyncItem object
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        InMemorySyncItem syncItem;
        String id = syncItemKey.getKeyAsString();

        try {
            // Retrieves the file data object, wraps it in sync information and
            // uses it to create a new SyncItem which is the return value of
            // this method
            FileDataObjectWrapper fdow = manager.getItem(id);
            syncItem = createSyncItem(fdow);

        } catch (EntityException e) {
            throw new SyncSourceException("Error seeking SyncItem with ID: "+ id, e);
        }

        return syncItem;
    }

    public void init() throws BeanInitializationException {
    }

    /**
     * Makes an array of SyncItemKey objects representing all new calendar
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        try {
            List idList = manager.getNewItems(since, to);
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving new item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted calendar
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        try {
            List idList = manager.getUpdatedItems(since, to);
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving updated item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all deleted calendar
     * IDs, filtered according to a given time interval.
     *
     * @param since the earlier limit of the time interval
     * @param to the later limit of the time interval
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {

        try {
            List idList = manager.getDeletedItems(since, to);
            return extractKeyArrayFromIdList(idList);
        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving deleted item keys.", e);
        }
    }

    /**
     * Makes an array of SyncItemKey objects representing all calendar IDs.
     *
     * @return a SyncItemKey array
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {

        try {
            List idList = manager.getAllItems();
            return extractKeyArrayFromIdList(idList);

        } catch(EntityException e) {
            throw new SyncSourceException("Error retrieving all item keys. ", e);
        }
    }

    public void setOperationStatus(String operation, int statusCode,
            SyncItemKey[] keys) {

        if (log.isTraceEnabled()) {
            StringBuilder message = new StringBuilder("Received status code '");
            message.append(statusCode).append("' for a '").append(operation)
                   .append("' command for the following items: ");

            for (int i = 0; i < keys.length; i++) {
                message.append("\n> ").append(keys[i].getKeyAsString());
            }

            log.trace(message.toString());
        }
    }

    /**
     * Returns a SyncItemFactory for file data object large objects.
     *
     * @param username The user name to which the SyncItems belongs.
     * @return a SyncItemFactory
     * @throws SyncSourceException if it is not able to create the
     * SyncItemFactory
     */
    public SyncItemFactory getSyncItemFactory(String username)
            throws SyncSourceException {
        if (username == null) {
            throw new InvalidParameterException("the user name must be not null");
        }
        try {
            if (streamingSyncItemFactory == null) {
                streamingSyncItemFactory = new FDOStreamingSyncItemFactory(username, config.getLocalRootPath(), config.getFileSysDirDepth());
            }
            return streamingSyncItemFactory;
        } catch (IOException ex) {

            throw new SyncSourceException("Unable to create SyncItemFactory", ex);
        }

    }

    /**
     * Returns the quota assigned to the user
     * @param user the user for which the quota as to be retrieved
     * @return the quota assigned to the user
     */
    public long getQuotaPerUser(Sync4jUser user) {
        return config.getQuotaPerUser(user);
    }

    /**
     * Returns an instance of the FileDataObjectSyncSourceConfig class.
     *
     * @param sourceUri the source URI
     * @return an instance of the FileDataObjectSyncSourceConfig
     * @throws FileDataObjecySyncSourceConfigException if an error occurs
     */
    public static FileDataObjectSyncSourceConfig getConfigInstance(String sourceUri)
    throws FileDataObjecySyncSourceConfigException {
        FileDataObjectSyncSource syncsource;

        syncsource = getFileDataObjectSyncSource(sourceUri);

        return syncsource.config;
    }

    /**
     * Factory method for FileDataObjectManager creation.
     *
     * @param sync4jUser the user
     * @param sourceURI the source uri
     * @return the newly created FileDataObjectManager
     * @throws IOException if an error occurs creating the FileDataObjectManager
     * @throws DAOException if an error occurs
     * @throws FileDataObjecySyncSourceConfigException if an error occurs
     */
    public FileDataObjectManager createNewFileDataObjectManager(Sync4jUser sync4jUser,
        String sourceURI,
        FileDataObjectSyncSourceConfig config)
        throws IOException, DAOException, FileDataObjecySyncSourceConfigException {

        FileDataObjectNamingStrategy namingStrategy =
            new FileDataObjectNamingStrategy(config.getFileSysDirDepth());
        FileSystemDAOHelper helper =
            new FileSystemDAOHelper(sync4jUser.getUsername(), config.getLocalRootPath(), namingStrategy);

        return new FileDataObjectManager(sync4jUser, sourceURI, config, namingStrategy, helper);
    }

    /**
     * Factory method for FileDataObjectManager creation.
     *
     * @param sync4jUser the user
     * @param sourceUri the source uri
     * @return the newly created FileDataObjectManager
     * @throws IOException if an error occurs creating the FileDataObjectManager
     * @throws DAOException if an error occurs
     * @throws FileDataObjecySyncSourceConfigException if an error occurs
     */
    public static FileDataObjectManager createNewFileDataObjectManager(Sync4jUser sync4jUser,
        String sourceUri)
        throws IOException, DAOException, FileDataObjecySyncSourceConfigException {

        FileDataObjectSyncSource syncsource = getFileDataObjectSyncSource(sourceUri);

        FileDataObjectNamingStrategy namingStrategy =
            new FileDataObjectNamingStrategy(syncsource.config.getFileSysDirDepth());
        FileSystemDAOHelper helper =
            new FileSystemDAOHelper(sync4jUser.getUsername(), syncsource.config.getLocalRootPath(), namingStrategy);

        return syncsource.createFileDataObjectManager(sync4jUser, sourceUri, namingStrategy, helper);
    }

    //-------------------------------------------------------- Protected methods

    protected void removeAllSyncItems(Timestamp t) {

        try {
            manager.removeAllItems(t);
        } catch (EntityException ee) {
            if (log.isTraceEnabled()) {
                log.trace("Error while performing REFRESH_FROM_CLIENT (203).", ee);
            }
        }
    }

    protected FileDataObjectSyncSourceConfig createFileDataObjectSyncSourceConfig() {
        return new FileDataObjectSyncSourceConfig();
    }

    protected FileDataObjectManager createFileDataObjectManager(Sync4jUser sync4jUser,
        String sourceURI,
        FileDataObjectNamingStrategy namingStrategy,
        FileSystemDAOHelper helper)
        throws DAOException {

        return new FileDataObjectManager(sync4jUser, sourceURI, config, namingStrategy, helper);
    }

    //---------------------------------------------------------- Private methods

    private SyncItemKey[] extractKeyArrayFromIdList(List idList) {

        SyncItemKey[] keyList = new SyncItemKey[idList.size()];
        for (int i = 0; i < idList.size(); i++) {
            keyList[i] = new SyncItemKey((String) idList.get(i));
        }
        return keyList;
    }

    /**
     * Formats a file data object to be placed into a sync item.
     *
     * @param fileDataObject the file data object
     * @throws EntityException if the conversion attempt doesn't succeed.
     * @return the result as an array of bytes
     */
    private byte[] convert(FileDataObject fileDataObject)
            throws EntityException {
        try {

            if (fileDataObject == null) {
                throw new EntityException("Null item, formatting of file data object failed.");
            }

            FileDataObjectMarshaller marshaller = new FileDataObjectMarshaller();
            return marshaller.marshall(fileDataObject);

        } catch (FileDataObjectParsingException ex) {
            throw new EntityException("Unable to marshall the File Data Object " +
                    "into a valid XML document", ex);
        }
    }

    /**
     * Creates a SyncItem for the given FileDataObjectWrapper.
     *
     * @param fileDataObjectWrapper its information are used to create SyncItem
     * @return a SyncItem
     * @throws EntityException if an error occurs
     * @throws SyncSourceException if an error occurs
     */
    private InMemorySyncItem createSyncItem(FileDataObjectWrapper fileDataObjectWrapper)
    throws EntityException, SyncSourceException {

        FileDataObject fileDataObject = fileDataObjectWrapper.getFileDataObject();

        byte[] content = convert(fileDataObject);

        InMemorySyncItem item = new InMemorySyncItem(
                    this,                                 //syncSource
                    fileDataObjectWrapper.getId(),        //key
                    null,                                 //parent key
                    null,                                 //mappedKey
                    fileDataObjectWrapper.getStatus(),    //state
                    content,                              //content
                    null,                                 //format
                    FDO_MIME_TYPE,                        //type
                    fileDataObjectWrapper.getLastUpdate() //timestamp
                );

        // in this ways the server use CData section
        item.setProperty(new SyncProperty(AbstractSyncItem.PROPERTY_OPAQUE_DATA, Boolean.TRUE));
        return item;
    }

    /**
     * Retrieve the device time zone for the given device.
     *
     * @param device the device
     * @return the timezone
     */
    private TimeZone retrieveDeviceTimeZone(Sync4jDevice device) {

        TimeZone timeZone = null;

        String timeZoneStr = device.getTimeZone();
        if (timeZoneStr != null && timeZoneStr.length() > 0) {
            timeZone = TimeZone.getTimeZone(timeZoneStr);
        }

        return timeZone;
    }

    /**
     * Returns the path of the config file for the {@link FileDataObjectSyncSource}
     * based on its source URI.
     *
     * @param sourceUri the source URI
     * @return the path of the config file
     * @throws SyncSourceException if an error occurs
     */
    private static String getSyncSourceFilePath(String sourceUri)
    throws SyncSourceException {
        try {
            if (log.isTraceEnabled()) {
               log.trace("Loading FileDataObjectSyncSources configurations");
            }
            PersistentStore persistentStore = Configuration.getConfiguration().getStore();
            Sync4jSource object = new Sync4jSource();
            Clause clause = new WhereClause("uri", new String[]{sourceUri}, WhereClause.OPT_EQ, true);
            Object[] result = persistentStore.read(object, clause);
            //return an empty string  if a datasource is not found for the source uri
            if (null == result || 0 == result.length) return "";
            //get the file name of the first datasource found. In general,
            //only one datasource is returned
            Sync4jSource sync4jSource = (Sync4jSource) result[0];
            return sync4jSource.getConfig();

        } catch (PersistentStoreException ex) {
            throw new SyncSourceException("Unable to access to PersistentStore and retrieve the syncsource file name", ex);
        }
    }

    /**
     * This method can be used to understand if an attempt to upload the input
     * item has already been performed and if a resume can be run by the client.
     *
     * @param fdow the object we want to understand if it's resumable
     * @throws SyncSourceException if a resumable upload may be performed by
     * the client
     */
    private void checkResumableUpload(FileDataObjectWrapper fdow)
    throws SyncSourceException {
        if(fdow!=null) {
            String uid = fdow.getId();
            FileDataObject fdo = fdow.getFileDataObject();
            if(fdo!=null) {
                if(fdo.isPartiallyUploaded()) {
                    // The object size is null, it's a mandatory field, raising an exception
                    if(fdow.getSize()==null) {
                        String message = "Item with uid '"+uid+
                                         "' has null size, unable to create data "+
                                         "information about resumable item.";
                        log.error(message);
                        throw new NullPointerException(message);
                    }
                    long totalBytes           = fdow.getSize();
                    long alreadyReceivedBytes = fdow.getSizeOnStorage()!=null?fdow.getSizeOnStorage():new Long(0);
                    String message = "An upload attempt for the item "+uid+
                                  " has already been performed, received "+alreadyReceivedBytes+
                                  " of "+totalBytes +" bytes.";
                    if (log.isTraceEnabled()) {
                        log.trace(message);
                    }
                    throw new PartialContentException(alreadyReceivedBytes+"/"+totalBytes,
                                                      alreadyReceivedBytes,
                                                      totalBytes);
                }
            }
        }
    }

    private static String getSyncSourceName(String sourceUri) throws FileDataObjecySyncSourceConfigException, SyncSourceException {
        String syncsourceFileName;
        syncsourceFileName = getSyncSourceFilePath(sourceUri);

        if (null == syncsourceFileName || "".equals(syncsourceFileName)) {
            throw new FileDataObjecySyncSourceConfigException(
                "Unable to retrieve sync source config file path for source URI " + sourceUri);
        }
        return syncsourceFileName;
    }

    private static FileDataObjectSyncSource getFileDataObjectSyncSource(String sourceUri) throws FileDataObjecySyncSourceConfigException {
        try {
            boolean cached = true;
            FileDataObjectSyncSource syncsource =
                (FileDataObjectSyncSource) Configuration.getConfiguration().getBeanInstanceByName(getSyncSourceName(sourceUri), cached);
            return syncsource;
        } catch (BeanException ex) {
            throw new FileDataObjecySyncSourceConfigException(ex.getMessage(), ex);
        } catch (FileDataObjecySyncSourceConfigException ex) {
            throw new FileDataObjecySyncSourceConfigException(ex.getMessage(), ex);
        } catch (SyncSourceException ex) {
            throw new FileDataObjecySyncSourceConfigException(ex.getMessage(), ex);
        }
    }

}
