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
package com.funambol.foundation.items.dao;

import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.UploadStatus;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.SourceUtils;

import com.funambol.foundation.engine.source.FileDataObjectSyncSource;
import com.funambol.foundation.engine.source.FileDataObjectSyncSourceConfig;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.exception.MaxFileSizeException;
import com.funambol.foundation.exception.QuotaOverflowException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.Def;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;

/**
 * Responsibility: to access file data objects in the backend.
 * Collaborators:
 *   - EntityDAO
 *   - FileDataObjectMetadataDAO
 *   - FileDataObjectBodyDAO
 * @version $Id: FileDataObjectDAO.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FileDataObjectDAO {

    // --------------------------------------------------------------- Constants

    public static final String ERROR_ADDING_FDO =
            "Error adding file data object.";
    public static final String ERROR_REMOVING_FDO =
            "Error removing file data object.";
    public static final String ERROR_UPDATING_FDO =
            "Error updating file data object.";
    public static final String ERROR_GETTING_FDO =
            "Error retrieving file data object.";

    // ---------------------------------------------------------- Protected data

    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Private data

    /**
     * The DAO delegated to store the sync info data on the database.
     */
    private EntityDAO syncInfoDAO;

    /**
     * The DAO delegated to store the metadata on the proper back end system
     * (usually, the data base).
     */
    private FileDataObjectMetadataDAO metadataDAO;

    /**
     * The DAO delegated to store the file content on the proper back end system
     * (usually, the file system).
     */
    private FileDataObjectBodyDAO bodyDAO;

    /** Max file size */
    private long maxSize;

    /** Storage quota per user */
    private long quotaPerUser;

    public long getQuotaPerUser() {
        return quotaPerUser;
    }

    // -------------------------------------------------------------- Properties

    /** User id. */
    private String userId;

    public String getUserId() {
        return userId;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new DAO instance.
     *
     * @param userId the user identifier
     * @param sourceURI the source uri
     * @param maxSize the maximum allowed size for a file
     * @param quotaPerUser the space quota assigned to the user
     *        (based on his role)
     * @param dbDAO the metadata dao
     * @param bodyDAO the content dao
     * @throws DAOException if an error occurs
     */
    protected FileDataObjectDAO(String userId,
                                String sourceURI,
                                long maxSize,
                                long quotaPerUser,
                                DataBaseFileDataObjectMetadataDAO dbDAO,
                                FileDataObjectBodyDAO bodyDAO)
    throws DAOException {

        this.userId = userId;
        this.maxSize = maxSize;
        this.quotaPerUser = quotaPerUser;

        this.metadataDAO = dbDAO;
        this.syncInfoDAO = dbDAO;

        this.bodyDAO = bodyDAO;
    }

    /**
     * Creates a new DAO instance.
     *
     * @param sync4jUser the user
     * @param sourceURI the source uri
     * @param config the sync source configuration
     * @param helper the file system helper
     * @param namingStrategy the naming strategy
     * @throws DAOException if an error occurs
     */
    public FileDataObjectDAO(Sync4jUser sync4jUser,
                             String sourceURI,
                             FileDataObjectSyncSourceConfig config,
                             FileSystemDAOHelper helper,
                             FileDataObjectNamingStrategy namingStrategy)
    throws DAOException {

        this(sync4jUser.getUsername(),
            sourceURI,
            config.getMaxSizeAsLong(),
            config.getQuotaPerUser(sync4jUser),
            new DataBaseFileDataObjectMetadataDAO(sync4jUser.getUsername(), sourceURI),
            new JcloudsFileDataObjectBodyDAO(sync4jUser.getUsername(), config, helper, namingStrategy));
    }

    /**
     * Creates a new DAO instance.
     *
     * @param namingStrategy the naming strategy
     * @param helper the file system helper
     * @param userId the user identifier
     * @param sourceURI the sync source URI
     * @param jcloudsFilesystemRootPath the root path for the file bodies 
     *        (the actual path for each file will be a subfolder of this path)
     * @param maxSize the maximum allowed size for a file
     * @param quotaPerUser the storage quota per user
     * @param jcloudsProvider the key name of the storage provider on which
     *        store the file data object
     * @param jcloudsIdentity the storage identity (like secret)
     * @param jcloudsCredential the storage credential for authetication
     * @param jcloudsContainerName the container name in which store the file
     *        data object
     * @throws DAOException if an error occurs
     */
    public FileDataObjectDAO(FileDataObjectNamingStrategy namingStrategy,
                             FileSystemDAOHelper helper,
                             String userId,
                             String sourceURI,
                             String jcloudsFilesystemRootPath,
                             long maxSize,
                             long quotaPerUser,
                             String jcloudsProvider,
                             String jcloudsIdentity,
                             String jcloudsCredential,
                             String jcloudsContainerName)
   throws DAOException {

        this(userId,
            sourceURI,
            maxSize,
            quotaPerUser,
            new DataBaseFileDataObjectMetadataDAO(userId, sourceURI),
            new JcloudsFileDataObjectBodyDAO(
                namingStrategy,
                helper,
                jcloudsProvider,
                jcloudsIdentity,
                jcloudsCredential,
                jcloudsContainerName,
                userId,
                jcloudsFilesystemRootPath));

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Adds a file data object to the backend for this user.
     * Side Effects:
     * - modifies the localName of the item with the new local file name;
     * - sets the id.
     * @param fileDataObjectWrapper file data object to be added
     * @throws DAOException There are two subclasses of DAOException:
     * MaxFileSizeException and QuotaOverflowException.
     * MaxFileSizeException if the updated file exceeds the configured
     * max file size.
     * QuotaOverflowException if the total ammount of picture,
     * considering the updated one, exceedes the max file quota.
     *
     */
    public void addItem(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        checkSizeAndQuotaLimit(fileDataObjectWrapper.getFileDataObject(), null);

        if (fileDataObjectWrapper.getId() == null) {
            throw new DAOException("Unable to add item: id should be defined");
        }
        if (fileDataObjectWrapper.getLocalName() == null) {
            throw new DAOException("Unable to add item: local name should be defined");
        }
        bodyDAO.addItem(fileDataObjectWrapper);
        metadataDAO.addItem(fileDataObjectWrapper);
    }

    /**
     * Adds the metadata for the given file data object.
     *
     * @param fileDataObjectWrapper the FileDataObjectWrapper containing the
     *                              new metadata
     * @throws DAOException if an error occurs
     */
    public void addItemMetadata(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        checkSizeAndQuotaLimit(fileDataObjectWrapper.getFileDataObject(), null);

        metadataDAO.addItem(fileDataObjectWrapper);
    }

    /**
     * Adds the body of the given FileDataObjectWrapper.
     * Side effects: changes the the sizeOnStorage in the fileDtaObjectWrapper
     *
     * @param fileDataObjectWrapper the FileDataObjectWrapper containing the new
     *                              body file
     * @throws DAOException if an error occurs
     */
    public void addItemBody(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        if (fileDataObjectWrapper.getId() == null) {
            throw new DAOException("Unable to add item: id should be defined");
        }
        if (fileDataObjectWrapper.getLocalName() == null) {
            throw new DAOException("Unable to add item: local name should be defined");
        }
        // sequence dependency: the addItem method as side effects changes the
        // the sizeOnStorage in the fileDtaObjectWrapper and thus
        // it must be executed before the updateItemWhenBodyChanges method.
        bodyDAO.addItem(fileDataObjectWrapper);
        metadataDAO.updateItemWhenBodyChanges(fileDataObjectWrapper);

    }

    /**
     * Updates an existing item with the given content.
     * The item is updated both in its metadata and its content, hence it is
     * updated as a standard update and not as a splitted one.
     * @param uid the uid
     * @param content the content
     * @return an updated FileDataObjectWrapper
     * @throws DAOException if an error occurs
     */
    public FileDataObjectWrapper updateItem(String uid, byte[] content)
    throws DAOException {
        if (uid == null)  {
            throw new IllegalArgumentException("uid must be not null");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content must be not null");
        }

        //only metadata are used, so no need to retrieve also the body of the file
        FileDataObjectWrapper fdow = getItemMetadata(uid);
        FileDataObject fdo = fdow.getFileDataObject();

        FileDataObjectBody body = createFileDataObjectBody(content);
        fdo.setBody(body);
        fdo.setSize(Long.valueOf(content.length));
        fdo.setUploadStatus(UploadStatus.UPLOADED);
        fdow.setLastUpdate(new Timestamp(System.currentTimeMillis()));

        FileDataObjectWrapper result = updateItem(fdow);
        return result;
    }

    /**
     * Updates a file data object.
     *
     * @param fileDataObjectWrapper item containing the new data
     * @return unique identifier of the item
     * @throws com.funambol.foundation.exception.DAOException
     */
    public FileDataObjectWrapper updateItem(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        bodyDAO.updateItem(fileDataObjectWrapper);
        //after the call to bodyDAO.updateItem, localName is updated with new name of the item
        metadataDAO.updateItem(fileDataObjectWrapper);

        return fileDataObjectWrapper;
    }

    /**
     * Updates only metadata information of an item
     *
     * @param fileDataObjectWrapper
     * @return
     * @throws DAOException
     */
    public FileDataObjectWrapper updateItemMetadata(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        metadataDAO.updateItem(fileDataObjectWrapper);

        return fileDataObjectWrapper;
    }

    /**
     * Retrieves the list of the ids of all the file data objects for this user.
     * @return the list of the ids
     * @throws com.funambol.foundation.exception.DAOException
     */
    public List getAllItems() throws DAOException {
        return syncInfoDAO.getAllItems();
    }

    /**
     * Removes a file data object for this user.
     *
     * @param uid file data object unique identifier
     * @param lastUpdate
     * @throws com.funambol.foundation.exception.DAOException
     */
    public void removeItem(String uid, Timestamp lastUpdate)
    throws DAOException {
        /*
         * TODO:
         * There are some aspects in the delete operation that can be improved
         * - no way to check if the metadata exists and are really deleted
         * - metadata are marked as deleted but the local name is not emptied, so
         * multiple deletion attempts are allowed at any time
         * - a log is prompted even if the file is not deleted
         * - if the deletion of the file fails, the metadata are marked as deleted
         * the same
         */

        if(uid==null || uid.length()==0) {
            throw new IllegalArgumentException("Unable to remove an item providing a null uid");
        }

        // The item can now be marked as deleted.
        syncInfoDAO.removeItem(uid, lastUpdate);

        // The properties associated to the item must be deleted
        syncInfoDAO.removeAllProperties(uid);

        // The file body can now be deleted.
        String localName = metadataDAO.getLocalName(uid);

        if (localName != null && localName.length() > 0) {
            bodyDAO.removeItem(localName);
            if (log.isTraceEnabled()) {
                log.trace("Deleted file '" + localName + "' for item " + uid);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("File not deleted: no local file name defined" +
                        " for item " + uid);
            }
        }
    }

    /**
     * This method allows you just to remove the item body from the storage
     * The localName is used to find the item that must be removed.
     * @param fdow it's the wrapper representing the item we want to remove the
     * body for
     * @throws DAOException if any error occurs while removing item body
     */
    public void removeItemBody(FileDataObjectWrapper fdow) throws DAOException {
        String uid       = fdow.getId();
        String localName = fdow.getLocalName();
        if(localName!=null && localName.length()>0) {
            bodyDAO.removeItem(localName);
            if (log.isTraceEnabled()) {
                log.trace("Deleted file '" + localName + "' for item " + uid);
            }
        } else {
            if (log.isTraceEnabled()) {
                log.trace("File not deleted: no local file name defined" +
                        " for item " + uid);
            }
        }
    }

    /**
     * This method allows you to remove the item body from the storage using the
     * localName attribute contained into the incoming wrapper
     *
     * @param fdow it's the wrapper representing the item we want to remove the
     * body for
     */
    public void removeItemBodyQuietly(FileDataObjectWrapper fdow) {
        try {
            this.removeItemBody(fdow);
        } catch(DAOException e) {
            if(log.isTraceEnabled()) {
                log.trace("An error occurred removing item body for '"+fdow.getId()+"'");
            }
        }
    }

    /**
     * Removes all the file data objects for this user since <code>lastUpdate</code>.
     *
     * @param lastUpdate if null, the present date is used. NB: if one uses the
     *                   current last update value for this argument, the
     *                   deletion won't be detected! this argument is meant to
     *                   allow the deletion registered moment to be forced.
     * @throws com.funambol.foundation.exception.DAOException
     */
    public void removeAllItems(Timestamp lastUpdate) throws DAOException {
        syncInfoDAO.removeAllItems(lastUpdate);
        syncInfoDAO.removeAllPropertiesByUserID();
        bodyDAO.removeAllItems();
    }

    /**
     * Removes all the file data objects for this user since <code>lastUpdate</code>.
     *
     * @throws com.funambol.foundation.exception.DAOException
     */
    public void removeAllItems() throws DAOException {
        removeAllItems(null);
    }

    /**
     * Removes from the repository the incomplete items i.e. items with the
     * metadata but without the corresponding body.
     *
     * @throws DAOException if an error occurs
     */
    public void removeExpiredIncompleteItems() throws DAOException {
        metadataDAO.removeExpiredIncompleteItems();
    }

    /**
     * Reset the size field to the value of the size_on_storage field for the
     * misaligned items i.e. items for which the metadata has been updated but
     * the updated file has not been uploaded.
     *
     * @throws DAOException if an error occurs
     */
    public void resetSizeForMisalignedItems() throws DAOException {
        metadataDAO.resetSizeForMisalignedItems();
    }

    /**
     * Release all the resources used to store the file data object bodies
     * belonging to a user.
     *
     * @throws DAOException if an error occurs
     * @deprecated use method removeAllItems instead
     */
    public void releaseResources() throws DAOException {
        removeAllItems(null);
        bodyDAO.releaseResources();
    }

    /**
     * Release all the resources used to store and retrieve the file data object
     * bodies
     */
    public void close() throws DAOException {
        bodyDAO.close();
    }

    /**
     * Retrieves the status of an item.
     *
     * @param uid file data object unique identifier
     * @param since the lower bound time stamp
     * @return the item status
     * @throws DAOException if an error occurs
     */
    public char getItemState(String uid, Timestamp since) throws DAOException {
        return syncInfoDAO.getItemState(uid, since);
    }

    /**
     * Returns an array of Lists with the NEW, UPDATED, DELETED item ids.
     * <p>
     * <ui>
     * <li>list[0]: new item ids</li>
     * <li>list[1]: updated item ids</li>
     * <li>list[2]: deleted item ids</li>
     * </ui>
     *
     * @param since the earliest allowed last-update Timestamp
     * @param to the latest allowed last-update Timestamp
     * @return an array of List with the NEW, UPDATED, DELETED item ids.
     * @throws DAOException if an error occurs
     */
    public List<String>[] getChangedItems(Timestamp since, Timestamp to)
    throws DAOException {
        return syncInfoDAO.getChangedItemsByLastUpdate(since, to);
    }

    /**
     * Retrieves a file data object.
     * @param uid
     * @return
     * @throws com.funambol.foundation.exception.DAOException
     */
    public FileDataObjectWrapper getItem(String uid) throws DAOException {

        FileDataObjectWrapper fdow = getItemMetadata(uid);
        String fileName = fdow.getLocalName();
        //item has not body (improved sync, for example)
        if (fileName == null || fileName.length() == 0 ||
            fdow.getStatus() == SyncItemState.DELETED) {

            fdow.getFileDataObject().setBody(null);
        } else {
            FileDataObjectBody body = bodyDAO.getItem(fileName);
            FileDataObject fdo = fdow.getFileDataObject();
            fdo.setBody(body);
            // if the item has been partially uploaded, the size on storage
            // must be read from the file system itself since it holds the most
            // up to date information
            if(fdo.isPartiallyUploaded()) {
                fdow.setSizeOnStorage(body.getSize());
            }
        }

        return fdow;
    }

    /**
     * Retrieves only the metadata of a file data object.
     * @param uid
     * @return
     * @throws com.funambol.foundation.exception.DAOException
     */
    public FileDataObjectWrapper getItemMetadata(String uid)
    throws DAOException {
        FileDataObjectWrapper fdow = metadataDAO.getItem(uid);
        fdow.getFileDataObject().setBody(null);
        return fdow;
    }

    /**
     * Retrieves the list of the ids of all the twin items of the given
     * file data object.
     *
     * @param fdo item to look for the twin items
     * @return the list of all the twin items
     * @throws com.funambol.foundation.exception.DAOException
     */
    public List getTwinItems(FileDataObject fdo) throws DAOException {
        return metadataDAO.getTwinItems(fdo);
    }

    /**
     * Gets the current storage space available for new item.
     *
     * @return the available storage space in bytes
     * @throws DAOException if an error occurs retrieving the used storage
     * space from database.
     */
    public long getAvailableStorageSpace() throws DAOException {

        if (quotaPerUser == FileDataObjectSyncSource.NO_QUOTA_LIMIT) {
            return Long.MAX_VALUE; // It means "as much as you like"
        }

        return quotaPerUser - metadataDAO.getReservedStorageSpace();
    }

    /**
     * Gets the current free storage space for the user.
     *
     * @return the free storage space in bytes
     * @throws DAOException if an error occurs retrieving the used storage
     * space from database.
     */
    public long getFreeStorageSpace() throws DAOException {

        if (quotaPerUser == FileDataObjectSyncSource.NO_QUOTA_LIMIT) {
            return Long.MAX_VALUE; // It means "as much as you like"
        }

        return quotaPerUser - metadataDAO.getStorageSpaceUsage();
    }

    /**
     * Returns the EntityDAO.
     *
     * @return the EntityDAO object
     */
    public EntityDAO getEntityDAO() {
        return syncInfoDAO;
    }

    /**
     * This method inspect an incoming item, checking whether it satisfies:
     * - the max file size limit (if any)
     * - the quota per user usage limit (if any)
     * Performing such a check, the method needs to know size informazion already
     * stored for the incoming item on the database (in the case the incoming
     * item has already been storend and contribute to the used quota).
     * The check is performed using size attribute contained in the input FileDataObject
     *
     * @param incoming it's the FileDataObject item parsed from the syncml message,
     * it must not be null
     * @param persisted it's the item persisted on the database
     * @throws DAOException if the item exceeds the max file size or if the quota it's
     * exceeded
     */
    public void checkSizeAndQuotaLimit(FileDataObject incoming, FileDataObject persisted)
    throws DAOException {
        if(incoming == null) {
           throw new IllegalArgumentException("Unable to check item size and quota limit for a null incoming item.");
        }

        Long oldSize = persisted==null?new Long(0):persisted.getSize();
        Long size    = incoming.getSize();
        if (size == null) {
            throw new IllegalArgumentException("File size is undefined.");
        }

        if (oldSize == null) {
            throw new IllegalArgumentException("Old file size is undefined.");
        }

        // Checking the item size against the max file size limit
        if ((maxSize != FileDataObjectSyncSource.NO_QUOTA_LIMIT) &&
            (size > maxSize)) {
            throw new MaxFileSizeException("File size exceeds limit", size, maxSize);
        }

        // Checking the quota used by the user including the incoming item against
        // the max quota per user limit
        if (quotaPerUser != FileDataObjectSyncSource.NO_QUOTA_LIMIT) {
            long freeStorageSpace = getAvailableStorageSpace() + oldSize;
            if (size > freeStorageSpace) {
                long usedQuota = quotaPerUser - freeStorageSpace;
                throw new QuotaOverflowException(
                    (freeStorageSpace > 0L ? Long.toString(freeStorageSpace) : 0L)
                    + "/" + Long.toString(quotaPerUser),
                    usedQuota,
                    quotaPerUser,
                    size);
            }
        }
    }

    public void uploadFilesOnStorageProvider(String extFolderPath,
        Map<String, File> filesToUpload,
        boolean specifyContentDisposition)
    throws DAOException {

        if (filesToUpload == null) return;

        bodyDAO.uploadExtFilesOnStorageProvider(extFolderPath, filesToUpload, specifyContentDisposition);


    }

    public String getNextID() throws DAOException {
        return syncInfoDAO.getNextID();
    }

    //---------------------------------------------------------- Private methods

    /**
     * Set the content body for a new FileDataObjectBody.
     *
     * @param content the body content
     * @return a FileDataObjectBody with its body and crc
     * @throws DAOException if an error occurs
     */
    private FileDataObjectBody createFileDataObjectBody(byte[] content)
    throws DAOException {

        FileDataObjectBody body = new FileDataObjectBody();
        File tmpFile = bodyDAO.addTmpBodyFile(content);
        body.setBodyFile(tmpFile);
        long crc = SourceUtils.computeCRC(content);
        body.setCrc(crc);

        return body;
    }

   /**
    * This method completes the incoming item during an update operation,
    * merging it with the information persisted on the database during a
    * previous synchronization process.
    *
    * First it checks if this operation breaks the quota limit. Then complete
    * the incoming item with the local name.
    *
    * @param fileDataObjectWrapper it's the item parsed from the incoming syncml
    *
    * @throws DAOException if the item exceeds the user quota or any error occurs
    * completing incoming fdow item with information persisted on the database
    *
    * @deprecated since it performs a merge of the incoming item with data
    * persisted on the database, use completeAndUpdateItem instead
    */
    protected void completeWrapper(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        FileDataObjectWrapper currentFdow = getItem(fileDataObjectWrapper.getId());

        checkSizeAndQuotaLimit(fileDataObjectWrapper.getFileDataObject(),
                               currentFdow.getFileDataObject());

        if (currentFdow.getStatus() != SyncItemState.DELETED) {
            fileDataObjectWrapper.setLocalName(currentFdow.getLocalName());
            fileDataObjectWrapper.setSizeOnStorage(currentFdow.getSizeOnStorage());
            FileDataObject currentFdo = currentFdow.getFileDataObject();
            FileDataObject fdo        = fileDataObjectWrapper.getFileDataObject();
            if(fdo!=null && currentFdo!=null && currentFdo.getSize().equals(fdo.getSize())) {
                fdo.setUploadStatus(currentFdo.getUploadStatus());
            }
        } else {
            fileDataObjectWrapper.setSizeOnStorage(null);
        }
    }

}