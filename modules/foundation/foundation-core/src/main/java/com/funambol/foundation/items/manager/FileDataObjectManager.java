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
package com.funambol.foundation.items.manager ;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.List;

import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.SourceUtils;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.media.file.UploadStatus;

import com.funambol.foundation.engine.source.FileDataObjectSyncSourceConfig;
import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.dao.EntityDAO;
import com.funambol.foundation.items.dao.FileDataObjectDAO;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;

/**
 * Manager for file data object sync
 *
 * @version $Id$
 */
public class FileDataObjectManager extends PIMEntityManager {

    //------------------------------------------------------------- Private data

    /**
     * The DAO to which delegate operations
     */
    protected FileDataObjectDAO fileDataObjectDAO;

    /**
     * manage temporary files stored locally
     */
    protected final FileSystemDAOHelper helper;

    /**
     * Naming strategy for items
     */
    protected final FileDataObjectNamingStrategy namingStrategy;

    /**
     * The user id
     */
    protected String userId = null;

    /**
     * Holds the configuration parameters
     */
    protected FileDataObjectSyncSourceConfig config;

    //------------------------------------------------------------- Constructors


    /**
     * Create a new instance of FileDataObjectManager.
     *
     * @param sync4jUser
     * @param sourceURI the source URI
     * @param config the FDO configuration data
     * @throws DAOException if an error occurs
     */
    public FileDataObjectManager(Sync4jUser sync4jUser,
                                 String sourceURI,
                                 FileDataObjectSyncSourceConfig config)
    throws DAOException {

        try {
            this.config = config;

            this.userId = sync4jUser.getUsername();

            this.namingStrategy = createFileNamingStrategy(config.getFileSysDirDepth());

            this.helper = createFileSystemDAOHelper(userId, config.getLocalRootPath(), this.namingStrategy);

            this.fileDataObjectDAO = new FileDataObjectDAO(
                namingStrategy,
                helper,
                userId,
                sourceURI,
                this.config.getStorageFilesystemRootPath(),
                this.config.getMaxSizeAsLong(),
                this.config.getQuotaPerUser(sync4jUser),
                this.config.getStorageProvider(),
                this.config.getStorageIdentity(),
                this.config.getStorageCredential(),
                this.config.getStorageContainerName());
        } catch (IOException ex) {
            throw new DAOException("Unamble to create FileDataObjectManager", ex);
        }
    }

    /**
     *
     * @param sync4jUser
     * @param sourceURI
     * @param config
     * @param namingStrategy
     * @param helper
     * @throws DAOException
     */
    public FileDataObjectManager(Sync4jUser sync4jUser,
                                 String sourceURI,
                                 FileDataObjectSyncSourceConfig config,
                                 FileDataObjectNamingStrategy namingStrategy,
                                 FileSystemDAOHelper helper)
                                 throws DAOException {

            this.config = config;

            this.userId = sync4jUser.getUsername();

            this.namingStrategy = namingStrategy;

            this.helper = helper;

            this.fileDataObjectDAO = new FileDataObjectDAO(
                namingStrategy,
                helper,
                userId,
                sourceURI,
                this.config.getStorageFilesystemRootPath(),
                this.config.getMaxSizeAsLong(),
                this.config.getQuotaPerUser(sync4jUser),
                this.config.getStorageProvider(),
                this.config.getStorageIdentity(),
                this.config.getStorageCredential(),
                this.config.getStorageContainerName());
    }


    /**
     * @deprecated Use the constructor with the FileDataObjectSyncSourceConfig
     * parameter instead.
     *
     * Create a new instance of FileDataObjectManager.
     *
     * @param userId the user ID
     * @param sourceURI the source URI
     * @param jcloudsFilesystemRootPath
     * @param localRootPath  the root path for the storage of files
     * @param maxSize the max size allowed for a single file
     * @param quotaPerUser the storage quota per user
     * @param fileSysDirDepth the nested directory depth for the user's file
     *        directory
     * @param jcloudsProvider
     * @param jcloudsIdentity
     * @param jcloudsCredential
     * @param jcloudsContainerName
     * @throws DAOException if an error occurs
     */
    public FileDataObjectManager(String userId,
                                 String sourceURI,
                                 String jcloudsFilesystemRootPath,
                                 String localRootPath,
                                 long maxSize,
                                 long quotaPerUser,
                                 int fileSysDirDepth,
                                 String jcloudsProvider,
                                 String jcloudsIdentity,
                                 String jcloudsCredential,
                                 String jcloudsContainerName)
    throws DAOException {
        try {

            this.userId = userId;
            this.namingStrategy = createFileNamingStrategy(fileSysDirDepth);
            this.helper = createFileSystemDAOHelper(userId, localRootPath, this.namingStrategy);
            this.fileDataObjectDAO = new FileDataObjectDAO(
                namingStrategy,
                helper,
                userId,
                sourceURI,
                jcloudsFilesystemRootPath,
                maxSize,
                quotaPerUser,
                jcloudsProvider,
                jcloudsIdentity,
                jcloudsCredential,
                jcloudsContainerName);

        } catch (IOException ex) {
            throw new DAOException("Unable to create FileDataObjectManager", ex);
        }
    }

    //----------------------------------------------------------- Public methods

    @Override
    public List getAllItems() throws EntityException {

        try {
            return fileDataObjectDAO.getAllItems();
        } catch (DAOException dbae) {
            throw new EntityException("Error while getting all items.", dbae);
        }
    }

    /**
     * Add a new FileDataObject item to the server datastore
     * @param fdo The item to add
     * @param timeStamp The time stamp
     * @return the newly added item's id
     * @throws DAOException if an error occurs while adding the new item
     */
    public String addItem(FileDataObject fdo, Timestamp timeStamp)
    throws DAOException {
        String addedItemId;

        if (!fdo.hasBodyFile()) {
           //improved sync, add only metadata info
            addedItemId = addItemMetadata(fdo, timeStamp);

        } else {
            FileDataObjectWrapper fdow = new FileDataObjectWrapper(fileDataObjectDAO.getNextID(), userId, fdo);
            fdow.setLastUpdate(timeStamp);
            fdow.setLocalName(namingStrategy.createFileName(fdow.getId()));

            fileDataObjectDAO.addItem(fdow);
    
            addedItemId = fdow.getId();
        }

        return addedItemId;
   }

    /**
     * Creates a new file using the given filename and content
     * @param fileName the filename
     * @param content the content
     * @return the uid of the create file
     * @throws DAOException if an error occurs
     */
    public String addItem(String fileName, byte[] content) throws DAOException {
        return addItem(fileName, content, null);
    }

    /**
     * Creates a new file using the given filename and content.
     * A new item is created both in its metadata and its content, hence it is
     * added as a standard add and not as a splitted one.
     * @param fileName the filename
     * @param content the content
     * @param contentType the content type
     * @return the uid of the create file
     * @throws DAOException if an error occurs
     */
    public String addItem(String fileName, byte[] content, String contentType)
    throws DAOException {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename must be not null");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content must be not null");
        }

        String uid = fileDataObjectDAO.getNextID();
        String localFileName = namingStrategy.createFileName(uid);

        FileDataObject fdo =
            createFileDataObject(fileName, content, contentType);

        fdo.setUploadStatus(UploadStatus.UPLOADED);

        FileDataObjectWrapper fdow = new FileDataObjectWrapper(uid, userId, fdo);
        fdow.setLastUpdate(new Timestamp(System.currentTimeMillis()));
        fdow.setLocalName(localFileName);

        fileDataObjectDAO.addItem(fdow);

        return uid;
    }

    /**
     * Add a new FileDataObject item to the server datastore, only metadata part
     * @param fdo The item to add
     * @param timestamp The time stamp
     * @return the newly added item's id
     * @throws DAOException if an error occurs while adding the new item
     */
    public String addItemMetadata(FileDataObject fdo, Timestamp timestamp)
    throws DAOException {

        FileDataObjectWrapper fdow = new FileDataObjectWrapper(fileDataObjectDAO.getNextID(), userId, fdo);
        fdow.setLastUpdate(timestamp);
        fileDataObjectDAO.addItemMetadata(fdow);
        return fdow.getId();
    }

   /**
    * Updates a file data object containing only the body part.
    *
    * @param itemToAdd the wrapper of the FileDataObject to update
    * @return the wrapper of the FileDataObject with all updated info
    * @throws DAOException if an error occurs
    */
    public FileDataObjectWrapper addItemBody(FileDataObjectWrapper itemToAdd)
    throws DAOException {
        itemToAdd.setLocalName(namingStrategy.createFileName(itemToAdd.getId()));
        fileDataObjectDAO.addItemBody(itemToAdd);
        return itemToAdd;
    }

    /**
     * Updates a file data object.
     *
     * @param itemToUpdate it is the wrapper of FileDataObject to update
     * @return the wrapper of FileDataObject with all the updated info
     * @throws DAOException There are two subclasses of DAOException:
     * MaxFileSizeException and QuotaOverflowException.
     * MaxFileSizeException if the updated file exceeds the configured
     * max file size.
     * QuotaOverflowException if the total ammount of picture,
     * considering the updated one, exceedes the max file quota.
     */
    public FileDataObjectWrapper updateItem(FileDataObjectWrapper itemToUpdate)
    throws DAOException {

        FileDataObjectWrapper result = null;
        //check for the type of sync..
        if (!itemToUpdate.isComplete()) {
            //...improved sync, just update metadata
            result = updateItemMetadata(itemToUpdate);

        } else {
            //...update both metadata and body
            result = fileDataObjectDAO.updateItem(itemToUpdate);

        }
        return result;
    }

    /**
     * Updates an existing item with the given content.
     * The item is updated both in its metadata and its content, hence it is
     * updated as a standard update and not as a splitted one.
     * @param uid the uid
     * @param content the content
     * @throws DAOException if an error occurs
     */
    public void updateItem(String uid, byte[] content) throws DAOException {
        if (uid == null) {
            throw new IllegalArgumentException("uid must be not null");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content must be not null");
        }

        fileDataObjectDAO.updateItem(uid, content);
    }

    /**
     * Updates a file data object containing only the metadata part.
     *
     * @param itemToUpdate the wrapper of FileDataObject to update
     * @return the wrapper of FileDataObject with updated info
     * @throws DAOException if an error occurs
     */
    public FileDataObjectWrapper updateItemMetadata(FileDataObjectWrapper itemToUpdate)
    throws DAOException {
        return fileDataObjectDAO.updateItemMetadata(itemToUpdate);
    }

    /**
     * Retrieves the FileDataObject wrapper given its uid.
     * If only metadata info are needed, please use the corresponding method,
     * because when body is stored on the cloud getting item body has a price,
     * generally.
     *
     * @param uid the identifier to search
     * @return the found FileDataObject wrapper or null
     * @throws EntityException if an error occurs
     */
    public FileDataObjectWrapper getItem(String uid) throws EntityException {
        try {
            return fileDataObjectDAO.getItem(uid);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting item", dbae);
        }
    }


    /**
     * Retrieves only the FileDataObject metadata given its uid.
     *
     * @param uid the identifier to search
     * @return the found FileDataObject wrapper or null, filled only with item metadata
     * @throws EntityException if an error occurs
     */
    public FileDataObjectWrapper getItemMetadata(String uid) throws EntityException {
        try {
            return fileDataObjectDAO.getItemMetadata(uid);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting item", dbae);
        }
    }

    /**
     * Retrieves the list of twins for the given FileDataObject.
     *
     * @param fdo the FileDataObject to search twins
     * @return the list of found twins
     * @throws EntityException if an error occurs
     */
    public List getTwins(FileDataObject fdo) throws EntityException {
        try {
            return fileDataObjectDAO.getTwinItems(fdo);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting twins of an item.", dbae);
        }
    }

    @Override
    public void removeItem(String uid, Timestamp t) throws EntityException {

        try {
            fileDataObjectDAO.removeItem(uid, t);
        } catch (DAOException dbae) {
            throw new EntityException("Error while deleting item.", dbae);
        }
    }

    /**
     * Removes all the file data objects for this user since <code>lastUpdate</code>.
     *
     * @param lastUpdate if null, the present date is used. NB: if one uses the
     *                   current last update value for this argument, the
     *                   deletion won't be detected! this argument is meant to
     *                   allow the deletion registered moment to be forced.
     * @throws EntityException if an error occurs removing all items
     */
    @Override
    public void removeAllItems(Timestamp lastUpdate) throws EntityException {

        try {
            fileDataObjectDAO.removeAllItems(lastUpdate);
        } catch (DAOException dbae) {
            throw new EntityException("Error while removing all items.", dbae);
        }
    }

    /**
     * Removes all the file data objects for this user since <code>lastUpdate</code>.

     * @throws EntityException if an error occurs removing all items
     */
    public void removeAllItems() throws EntityException {

        removeAllItems(null);
    }

    /**
     * Removes from the repository the incomplete items i.e. items with the
     * metadata but without the corresponding body (no checks is done on local
     * name).
     *
     * @throws EntityException if an error occurs
     */
    public void removeExpiredIncompleteItems() throws EntityException {
        try {
            fileDataObjectDAO.removeExpiredIncompleteItems();
        } catch (DAOException dbae) {
            throw new EntityException("Error while removing expired incomplete items.", dbae);
        }
    }

    /**
     * Reset the size field to the value of the size_on_storage field for the
     * misaligned items i.e. items for which the metadata has been updated but
     * the updated file has not been uploaded.
     * @throws EntityException if an error occurs
     */
    public void resetSizeForMisalignedItems() throws EntityException {

        try {
            fileDataObjectDAO.resetSizeForMisalignedItems();
        } catch (DAOException ex) {
            throw new EntityException("Error while resetting the size for " +
                "misaligned items.", ex);
        }
    }

    /**
     * Release all the resources used to store the file data object bodies
     * belonging to a user
     * @throws EntityException if an error occurs
     * @deprecated use method removeAllItems instead
     */
    public void releaseResources() throws EntityException {

        try {
            fileDataObjectDAO.releaseResources();
        } catch (DAOException dbae) {
            throw new EntityException("Error while removing all items and " +
                    "releasing resources.", dbae);
        }
    }

    /**
     * Release all the resources used to store and retrieve the file data object
     * bodies
     * @throws EntityException if an error occurs releasing all the resources
     */
    public void close() throws EntityException {
        try {
            fileDataObjectDAO.close();
        } catch (DAOException ex) {
            throw new EntityException("Error releasing resources.", ex);
        }
    }

    /**
     * Gets the current free storage space for the user.
     *
     * @return the free storage space in bytes
     * @throws DAOException if an error occurs retrieving the used storage
     * space from database.
     */
    public long getFreeStorageSpace() throws DAOException {
        return fileDataObjectDAO.getFreeStorageSpace();
    }

    /**
     * Returns the configured storage quota assigned to the user
     * @return
     */
    public long getQuotaPerUser() {
        return fileDataObjectDAO.getQuotaPerUser();
    }

    /**
     * This method is called just before invoking the update operation.
     * It completes the item parsed from the syncml messages with the information
     * stored on the database in order to drive the update operation performed
     * soon after and to enable smart features like the resume.
     * Three scenarios are foreseen:
     * 1) old fashion update, the incoming item is shipped with the body
     * 2) resumable update, the persisted item is partially uploaded, the client
     * may try a resume
     * 3) common update, no resume operation is feasible, the old file is deleted
     * if any and metadata are updated in order to allow the brand new upload
     *
     * @param uid it's the guid of the item
     * @param incomingFdo it's the FileDataObject parsed from the syncml message
     * @param lastUpdate it's the timestamp that will be set to the incoming wrapper
     * @param persistedWrapper it's the wrapper retrieved from the database
     *
     * @return the item resulting from the completion performed merging together
     * the information contained into the incoming item with the information
     * contained inside
     * @throws DAOException if an error occurs completing incoming item
     */
    protected FileDataObjectWrapper completeIncomingItem(String uid,
                                                      FileDataObject incomingFdo,
                                                      Timestamp lastUpdate,
                                                      FileDataObjectWrapper persistedWrapper)
    throws DAOException  {

        if(log.isTraceEnabled()) {
            log.trace("Completing information contained into incoming item '"+uid+
                      "' with information contained into persisted item.");
        }

        FileDataObjectWrapper incomingWrapper =
            createWrapperForIncomingItem(uid, incomingFdo, lastUpdate);

        if(incomingWrapper.isComplete()) {

            completeInformationOnFullSyncmlUpdate(incomingWrapper,
                                                  persistedWrapper);

        } else if(checkResumable(incomingWrapper, persistedWrapper)) {

            completeInformationOnResumableUpload(incomingWrapper,
                                                 persistedWrapper);
        } else {
            completeInformationOnUpload(incomingWrapper,
                                        persistedWrapper);
        }

        return incomingWrapper;
    }

    /**
     * This method allows to check if the size of the incoming item fits the
     * max file limit and the quota per user limit
     *
     * @param incoming it's the FileDataObject parsed from the incoming syncml message
     * @param persisted it's the FileDataObject that represents the same item that
     * we stored on the database during a previous synchronization, if any
     *
     * @throws DAOException if the incoming item exceeds one of the limits
     */
    public void checkSizeAndQuotaLimit(FileDataObject incoming,
                                       FileDataObject persisted)
    throws DAOException  {
        this.fileDataObjectDAO.checkSizeAndQuotaLimit(incoming, persisted);
    }

    /**
     * Merge the incoming item to be updated with the information related to the
     * same item found on the database.
     * It checks whether performing this update operation any limit related to the
     * max file size or to the quota per user is broken. In such a case, an exception
     * is thrown
     *
     * @param id it's the id of the incoming item
     * @param fileDataObject it's the FileDataObject received from the client
     * and that must be updated with information stored from the server in any
     * previous synchronization
     * @param timeStamp it's the timestamp related to the operation
     *
     * @return the FileDataObjectWrapper object resulting from the merge and the
     * update operation
     *
     * @throws EntityException if no information about the item to be persisted
     * is found on the database
     * @throws DAOException if performing the update operation, the max file size
     * limit or the quota per user limit is broken
     */
    public FileDataObjectWrapper completeAndUpdateItem(String id,
                                                       FileDataObject fileDataObject,
                                                       Timestamp timeStamp)
    throws EntityException, DAOException {

        if(id==null) {
            throw new IllegalArgumentException("Unable to complete and update item providing a null id");
        }

        if(fileDataObject==null) {
            throw new IllegalArgumentException("Unable to complete and update item providing a null FileDataObject");
        }

        if(timeStamp==null) {
            timeStamp = new Timestamp(System.currentTimeMillis());
        }

        FileDataObjectWrapper persistedItem = getItemMetadata(id);

        checkSizeAndQuotaLimit(fileDataObject,
                               persistedItem.getFileDataObject());

        FileDataObjectWrapper fdow = completeIncomingItem(id,
                                                         fileDataObject,
                                                         timeStamp,
                                                         persistedItem);
        fdow = updateItem(fdow);
        return fdow;
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Returns the EntityDAO.
     *
     * @return the EntityDAO object
     */
    protected EntityDAO getEntityDAO() {
        return fileDataObjectDAO.getEntityDAO();
    }


    /**
     * Checks if the fdow received by the sync and the fdow persisted in a previous
     * synchronization can be considered mergeable, i.e. the item that the client
     * is uploading has the same content that the item the prevuous upload attempt
     * failed. In particular, this method checks the creation date, the modification
     * date and the size
     *
     * @param incomingFdow the fdow object obtained by the sync
     * @param persistedFdow the fdow object loaded from the database
     *
     * @return true if the input fdow can be considered the same item
     */
    protected boolean checkResumable(FileDataObjectWrapper incomingFdow,
                                     FileDataObjectWrapper persistedFdow) {
        if(incomingFdow==null) {
            throw new IllegalArgumentException("Unable to complete item when incoming wrapper is null.");
        }

        if(persistedFdow==null) {
            throw new IllegalArgumentException("Unable to complete item when persisted wrapper is null.");
        }

        // if the persisted item has been deleted, then there's nothing to merge
        if(persistedFdow.isDeleted()) {
            if(log.isTraceEnabled()) {
                log.trace("Items are not mergeble, persisted item has been deleted.");
            }
            return false;
        }

        FileDataObject incomingFdo  = incomingFdow.getFileDataObject();
        FileDataObject persistedFdo = persistedFdow.getFileDataObject();

        if(!persistedFdo.isPartiallyUploaded()) {
            if(log.isTraceEnabled()) {
                log.trace("Persisted item is not partially uploaded, a brand new upload is expected.");
            }
            return false;
        }

        // checking if items are resumable
        // (size is mandatory, while creation and modification date are checked only if not null)
        if(objectEqualsOrNull("creation date",incomingFdo.getCreated(),persistedFdo.getCreated())  &&
           objectEqualsOrNull("modification date",incomingFdo.getModified(),persistedFdo.getModified()) &&
           objectEquals("size",incomingFdo.getSize(),persistedFdo.getSize())) {
           return true;
        }

        return false;
    }

    /**
     * Checks if input objects are equals using the proper equals method.
     * If one or both input objects are null return false.
     *
     * @param fieldName the name of the field whose values will be compared
     * @param firstField the value of the field contained in the incoming item
     * @param secondField the value of the field contained in the second item
     *
     * @return true if and only if both objects are not null and equals.
     */
    protected boolean objectEquals(String  fieldName,
                                   Object  firstField,
                                   Object  secondField) {
        boolean result = false;
        if(firstField!=null && secondField!= null) {
            result = firstField.equals(secondField);
            if(!result) {
                if(log.isTraceEnabled()) {
                    log.trace("Values of the field '"+fieldName +"' are not equals.");
                }
            }
        } else {
            if(log.isTraceEnabled()) {
                log.trace("At least one of the values for the field '"+fieldName +
                          "' is null, so they're not equals.");
            }
        }
        return result;
    }

    /**
     * Checks if input objects are equals using the proper equals method.
     * If one or both input objects are null return true.
     *
     * @param fieldName the name of the field whose values will be compared
     * @param firstField the value of the field contained in the incoming item
     * @param secondField the value of the field contained in the second item
     *
     * @return true if and only if both objects are equals or at least one of them
     * is null
     */
    protected boolean objectEqualsOrNull(String fieldName,
                                         Object firstField,
                                         Object secondField) {
        boolean result = true;
        if(firstField!=null && secondField!= null) {
            result = firstField.equals(secondField);
            if(!result) {
                if(log.isTraceEnabled()) {
                    log.trace("Values of the field '"+fieldName +"' are not equals.");
                }
            }
        } else {
            if(log.isTraceEnabled()) {
                log.trace("At least one of the values for the field '"+
                          fieldName +"' is null, but the fields are not mandatory.");
            }
        }
        return result;
    }

    /**
     * Factory Method for FileDataObjectNamingStrategy
     * @param fileSysDirDepth
     * @return
     */
    private FileDataObjectNamingStrategy createFileNamingStrategy(int fileSysDirDepth) {
        return new FileDataObjectNamingStrategy(fileSysDirDepth);
    }

    /**
     * Factory method for FileSystemDAOHelper
     * @param userName the user name
     * @param rootPath the file system root path
     * @param fileSysDirDepth
     * @return
     * @throws IOException if an error occurs creating the user folder
     */
    private FileSystemDAOHelper createFileSystemDAOHelper(String userName,
            String rootPath,
            FileDataObjectNamingStrategy fileDataObjectNamingStrategy)
            throws IOException {
        return new FileSystemDAOHelper(userName, rootPath, fileDataObjectNamingStrategy);
    }

    /**
     * Creates a FileDataObject.
     *
     * @param fileName the file name to use for searching the metadata
     * @param content the body content
     * @param contentType the content type
     * @return a FileDataObject with its metadata and body
     * @throws DAOException if an error occurs
     */
    protected FileDataObject createFileDataObject(String fileName,
                                                  byte[] content,
                                                  String contentType)
    throws DAOException {

        FileDataObjectMetadata metadata = new FileDataObjectMetadata(fileName);
        metadata.setSize(Long.valueOf(content.length));
        if (contentType != null) {
            metadata.setContentType(contentType);
        }

        FileDataObjectBody body = createFileDataObjectBody(content);

        FileDataObject fdo = new FileDataObject(metadata, body);
        return fdo;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Set the content body for a new FileDataObjectBody.
     *
     * @param content the body content
     * @return a FileDataObjectBody with its body and crc
     * @throws DAOException if an error occurs
     */
    private FileDataObjectBody createFileDataObjectBody(byte[] content)
    throws DAOException {

        FileDataObjectBody body = null;
        try {

            body = new FileDataObjectBody();
            File tmpFile = helper.writeItem(content);
            body.setBodyFile(tmpFile);
            long crc = SourceUtils.computeCRC(content);
            body.setCrc(crc);

        } catch(IOException e) {
            throw new DAOException("", e);
        }

        return body;
    }

    /**
     * Creates a wrapper using the input parameters.
     *
     * @param uid the uid of the wrapper
     * @param incomingFdo the incoming FileDataObject the new wrapper will contain
     * @param lastUpdate the timestamp we want to set to the new wrapper
     *
     * @return the just created wrapper
     */
    private FileDataObjectWrapper createWrapperForIncomingItem(String uid,
                                                               FileDataObject incomingFdo,
                                                               Timestamp lastUpdate) {

        FileDataObjectWrapper incomingWrapper = new FileDataObjectWrapper(uid,
                                                                     userId,
                                                                    incomingFdo);
        incomingWrapper.setLastUpdate(lastUpdate);
        return incomingWrapper;
    }

    /**
     * Completes the incoming item with information loaded from the persisted
     * item in the case the incoming item is fully sent to the server using the
     * syncml protocol.
     * In this case, just the local name is set since it will be used in the
     * updateBody operation to remove the old file before renaming and storing
     * the incoming one
     *
     * @param incomingWrapper it's the incoming wrapper
     * @param persistedWrapper it's the wrapper retrieved from the database
     */
    private void completeInformationOnFullSyncmlUpdate(
                                    FileDataObjectWrapper incomingWrapper,
                                    FileDataObjectWrapper persistedWrapper) {

        if(log.isTraceEnabled()) {
            log.trace("Incoming item '"+incomingWrapper.getId()+
                      "' comes with a body , "+
                      "update operation is going to overwrite previous data.");
        }
        incomingWrapper.setLocalName(persistedWrapper.getLocalName());
    }

    /**
     * Completes the incoming item with information loaded from the persisted
     * item in the case this update means an attempt to resume the upload.
     * In this case, different information are propagated since they are useful
     * to resume the upload.</p>
     * - uploadStatus is set to P</p>
     * - localName</p>
     * - sizeOnStorage</p>
     * When this method is called. the body of the persistedWrapper is stored in
     * a local temp file, so i must get the body to obtain the sizeOnStorage
     *
     * @param incomingWrapper it's the incoming wrapper
     * @param persistedWrapper it's the wrapper retrieved from the database
     */
    private void completeInformationOnResumableUpload(
                                    FileDataObjectWrapper incomingWrapper,
                                    FileDataObjectWrapper persistedWrapper)
    throws DAOException {

        if(log.isTraceEnabled()) {
            log.trace("Incoming item '"+incomingWrapper.getId()+
                      "' is resumable, we're going to return a partial content "+
                      "status code.");
        }

        String localName = persistedWrapper.getLocalName();
        //read the temp file size associated to the partial upload item
        long tmpFileSize = helper.getTmpFileSize(localName);
        incomingWrapper.setLocalName(localName);
        incomingWrapper.setSizeOnStorage(tmpFileSize);
        FileDataObject incomingFdo = incomingWrapper.getFileDataObject();
        incomingFdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
     }

    /**
     * Cleans the incoming item from some information, since the upload operation
     * will overwrite the persisted media content.
     * So:
     * - sizeOnStorage is set to 0
     * - localName is emptied and the previous file is removed quietly (no exception
     * upon error
     * - the uploadStatus is set to 'N'
     *
     * @param incomingWrapper it's the incoming wrapper
     * @param persistedWrapper it's the wrapper retrieved from the database
     */
    private void completeInformationOnUpload(FileDataObjectWrapper incomingWrapper,
                                             FileDataObjectWrapper persistedWrapper) {

        if(log.isTraceEnabled()) {
            log.trace("Incoming item '"+incomingWrapper.getId()+
                      "' will be handled as a simple upload.");
        }

        incomingWrapper.setLocalName(null);
        incomingWrapper.setSizeOnStorage(0L);
        incomingWrapper.getFileDataObject().setUploadStatus(UploadStatus.NOT_STARTED);
        fileDataObjectDAO.removeItemBodyQuietly(persistedWrapper);
    }
}
