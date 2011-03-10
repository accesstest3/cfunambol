/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.io.IOUtils;

import com.google.inject.Module;

import org.jclouds.blobstore.BlobStore;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.blobstore.BlobStoreContextFactory;
import org.jclouds.blobstore.domain.Blob;
import org.jclouds.blobstore.domain.PageSet;
import org.jclouds.blobstore.domain.StorageMetadata;
import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.filesystem.reference.FilesystemConstants;
import org.jclouds.io.Payload;
import org.jclouds.io.payloads.FilePayload;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.domain.CannedAccessPolicy;
import org.jclouds.s3.domain.S3Object;
import static org.jclouds.s3.options.PutObjectOptions.Builder.withAcl;

import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.foundation.engine.source.FileDataObjectSyncSourceConfig;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;

/**
 * Responsibility: this Data Access Object is used to access an online storage
 * web service in order to store, retrieve, update and remove files.
 * Collaborators:
 * - jclouds BlobStore and BlobStoreContext to manage the file stored on the
 *   online storage web service;
 * - FileSystemDAOHelper to manage temporary files stored locally
 * - FileDataObjectNamingStrategy for blob's key management
 *
 * This class should be used only inside FileDataObjectDao class, and not used or
 * inherited directly.
 *
 * @version $Id: JcloudsFileDataObjectBodyDAO.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class JcloudsFileDataObjectBodyDAO implements FileDataObjectBodyDAO {

    // --------------------------------------------------------------- Constants

    public static final String CONTENT_DISPOSITION = " attachment;  filename=";

    // ------------------------------------------------------------------ Fields

    /**
     * manage temporary files stored locally
     */
    private final FileSystemDAOHelper helper;

    /**
     * Naming strategy for items
     */
    private final FileDataObjectNamingStrategy namingStrategy;

    /**
     * The name of the user performing the sync
     */
    private final String userName;

    /**
     * The storage provider's store
     */
    private final BlobStore blobStore;

    /**
     * The storage provider's store context
     */
    private final BlobStoreContext context;

    /**
     * The container's name on the storage provider
     */
    private final String containerName;

    /**
     * A Set containing the temporary files created while geting the objects
     * from the storage provider. These files have to be deleted at the end of
     * the session.
     */
    private final List<File> tmpFiles = new ArrayList<File>();


    //------------------------------------------------------------- Constructors
    protected JcloudsFileDataObjectBodyDAO(String userName,
        FileDataObjectSyncSourceConfig config,
        FileSystemDAOHelper helper,
        FileDataObjectNamingStrategy namingStrategy)
    throws DAOException {

        this(
            namingStrategy,
            helper,
            config.getStorageProvider(),
            config.getStorageIdentity(),
            config.getStorageCredential(),
            config.getStorageContainerName(),
            userName,
            config.getStorageFilesystemRootPath());
    }

    /**
     * @param provider
     * @param identity
     * @param credential
     * @param containerName
     * @param userName the user name
     * @param jcloudFilesystemRootPath the root path where the files are stored
     * @param localRootPath
     * @param fileSysDirDepth the depth of the branch where the user folder is
     * stored
     * @throws DAOException if an error occurs creating the user folder
     */
    protected JcloudsFileDataObjectBodyDAO(
            FileDataObjectNamingStrategy namingStrategy,
            FileSystemDAOHelper helper,
            String provider,
            String identity,
            String credential,
            String containerName,
            String userName,
            String jcloudFilesystemRootPath)
    throws DAOException {

        try {

            this.namingStrategy = namingStrategy;

            this.helper = helper;

            //jclouds parameters
            this.containerName = containerName;

            //DAO parameters
            this.userName = userName;

            // TODO determine the correct value for modules and properties
            List<Module> modules = new ArrayList<Module>();
            Properties properties = new Properties();

            // The base dir property is used by the file system provider, and
            // will be ignored by other providers
            properties.setProperty(FilesystemConstants.PROPERTY_BASEDIR, jcloudFilesystemRootPath);
            properties.setProperty(FilesystemConstants.PROPERTY_OPTIMIZED, FilesystemConstants.VALUE_OPTIMIZED);

            // TODO verify the usefulness of s3service.https-only property
//            Jets3tProperties prop = new Jets3tProperties();
//            prop.setProperty("s3service.https-only", "true");

            context = new BlobStoreContextFactory().createContext(
                provider, identity, credential, modules, properties);

            blobStore = context.getBlobStore();

            // The container must already exist. The connector does not create it

        } catch (Throwable ex) {
            //remembre, jclouds throws Throwable, and not Exception
            throw new DAOException("Error creating JcloudsFileDataObjectBodyDAO ", ex);
        }
    }

    //----------------------------------------------------------- Public methods

    public void releaseResources() throws DAOException {
        removeAllItems();
    }

    public void close() throws DAOException {
        try {
            deleteTmpFiles();
            context.close();
        } catch (Throwable ex) {
            throw new DAOException("Error closing context ", ex);
        }
    }

    public FileDataObjectBody getItem(String fileNameOnStorage)
    throws DAOException {

        String blobKey = namingStrategy.getFilePath(userName, fileNameOnStorage);
        File file = getFileFromStorageProvider(blobKey);

        FileDataObjectBody fileDataObjectBody = new FileDataObjectBody();
        fileDataObjectBody.setBodyFile(file);
        return fileDataObjectBody;
    }

    public void addItem(FileDataObjectWrapper fdow)
        throws DAOException {

        if (fdow.getLocalName() == null) {
            throw new DAOException("Unable to add item: local name should be already defined");
        }

        uploadOnStorageProvider(fdow);
    }

    public File addTmpBodyFile(byte[] content) throws DAOException {
        try {
            return helper.writeItem(content);
        } catch (IOException ex) {
            throw new DAOException("Error adding new file", ex);
        }
    }

    public void updateItem(FileDataObjectWrapper fileDataObjectWrapper)
    throws DAOException {

        String previousLocalName = fileDataObjectWrapper.getLocalName();
        if (previousLocalName == null || previousLocalName.length() == 0) {
            throw new DAOException("Error updating file data object: "
                + "previous local file name must be not empty");
        }

        // Generate a new name for file on the storage and update the
        // FileDataObjectWrapper
        String fileNameOnStorage = namingStrategy.createFileName(fileDataObjectWrapper.getId());
        fileDataObjectWrapper.setLocalName(fileNameOnStorage);

        uploadOnStorageProvider(fileDataObjectWrapper);

        removeItem(previousLocalName);
    }

    public void removeItem(String localFileName) throws DAOException {
        removeExtFolderFromStorageProvider(localFileName);
        removeObjectFromStorageProvider(localFileName);
    }

    public void removeAllItems() throws DAOException {
        String userDirPath = namingStrategy.getUserFolderPath(userName);

        removeDirFromStorageProvider(userDirPath);
    }

    public void uploadExtFileOnStorageProvider(String extFolderPath,
        String fileName,
        File sourceFile,
        boolean specifyContentDisposition)
    throws DAOException {

        if (sourceFile == null) return;

        String key = new StringBuilder(extFolderPath).append("/").append(fileName).toString();
        uploadBlobOnStorageProvider(null, key, sourceFile, specifyContentDisposition);
    }

    public void uploadExtFilesOnStorageProvider(String extFolderPath,
        Map<String, File> filesToUpload,
        boolean specifyContentDisposition)
    throws DAOException {
        if (filesToUpload == null) return;

        Iterator<Entry<String, File>> entries = filesToUpload.entrySet().iterator();
        while(entries.hasNext()) {
            Entry<String, File> entry = entries.next();

            String fileName = entry.getKey();
            File sourceFile = entry.getValue();

            uploadExtFileOnStorageProvider(extFolderPath, fileName, sourceFile, specifyContentDisposition);
        }

    }
    //---------------------------------------------------------- Private methods

    /**
     * Delete all the temporary files
     */
    private void deleteTmpFiles() {
        for (File tmpFile : tmpFiles) {
            tmpFile.delete();
        }
    }

    /**
     * Removes all the files contained in the ext folder corresponding to the
     * local file.
     * @param fileNameOnStorage name of the file data object content file for which
     * the ext folder must be removed
     * @throws DAOException
     */
    private void removeExtFolderFromStorageProvider(String fileNameOnStorage) throws DAOException {
        String extDirPath = namingStrategy.getEXTFolderPath(userName, fileNameOnStorage);
        removeDirFromStorageProvider(extDirPath);
    }

    /**
     * Removes the local file.
     * @param fileNameOnStorage name of the file data object content file
     * @throws DAOException
     */
    private void removeObjectFromStorageProvider(String fileNameOnStorage) throws DAOException {
        try {
            String blobKey = namingStrategy.getFilePath(userName, fileNameOnStorage);
            blobStore.removeBlob(containerName, blobKey);
        } catch (Throwable ex) {
            throw new DAOException("Error removing object ", ex);
        }
    }

    /**
     * Remove file contained in the folder on the storage provider.
     * @param dirPath
     * @throws DAOException
     */
    private void removeDirFromStorageProvider(String dirPath) throws DAOException {
        try {
            ListContainerOptions options = ListContainerOptions.Builder.recursive();
            options.inDirectory(dirPath);
            PageSet objects = null;
            objects = blobStore.list(containerName, options);

            Iterator iter = objects.iterator();
            while (iter.hasNext()) {
                StorageMetadata object = (StorageMetadata) iter.next();
                blobStore.removeBlob(containerName, object.getName());
            }
        } catch (Throwable ex) {
            throw new DAOException("Error removing diretory ", ex);
        }
    }

    /**
     * Uploads the file to the cloud
     * @param itemName
     * @param itemKey
     * @param sourceFile
     */
    private void uploadBlobOnStorageProvider(
        String itemName,
        String itemKey,
        File sourceFile,
        boolean specifyContentDisposition)
        throws DAOException {

        if (context.getProviderSpecificContext().getApi() instanceof S3Client) {
            uploadBlobOnS3StorageProvider(context,
                containerName,
                itemName,
                itemKey,
                sourceFile,
                specifyContentDisposition);
        } else {
            uploadBlobOnJcloudStorageProvider(blobStore,
                containerName,
                itemName,
                itemKey,
                sourceFile,
                specifyContentDisposition);
        }

    }

    /**
     *
     * @param key
     * @return
     * @throws Exception
     */
    private Blob getBlobFromStorageProvider(String key) throws DAOException {
        try {
            Blob object = blobStore.getBlob(containerName, key);
            return object;
        } catch (Throwable ex) {
            throw new DAOException("Error geting object ", ex);
        }
    }

    private InputStream getInputStreamFromStorageProvider(String key) throws DAOException {
        try {
            Blob object = getBlobFromStorageProvider(key);
            object.getPayload();
            InputStream input = object.getPayload().getInput();

            return input;
        } catch (Exception ex) {
            throw new DAOException("Error geting inputstream from object ", ex);
        }
    }

    private File getFileFromStorageProvider(String key) throws DAOException {
        try {
            File file;
            Blob object = getBlobFromStorageProvider(key);
            Payload payload = object.getPayload();
            if (payload instanceof FilePayload) {
                FilePayload filePayload = (FilePayload) object.getPayload();
                file = filePayload.getRawContent();
            } else {
                file = createTmpFileFromPayload(payload);
            }

            return file;
        } catch (Exception ex) {
            throw new DAOException("Error geting inputstream from object ", ex);
        }
    }

    /**
     * Side effects: updates the FileDataObjectWrapper parameter, adding the
     * size on storag and setting the body file to null.
     *
     * @param fileDataObjectWrapper The FileDataObjectWrapper containing the
     * FileDataObject to upload on the file storage provider.
     *
     * @throws DAOException
     */
    private void uploadOnStorageProvider(FileDataObjectWrapper fileDataObjectWrapper)
        throws DAOException {

        try {
            // name of the temporary file (the one used during unmarshalling)
            File sourceFile = fileDataObjectWrapper.getFileDataObject().getBodyFile();
            if (sourceFile == null || !sourceFile.exists()) {
                throw new DAOException("Error uploading file data object on "
                    + "storage: file must be present");
            }

            String fileNameOnStorage = fileDataObjectWrapper.getLocalName();
            fileDataObjectWrapper.setSizeOnStorage(new Long(sourceFile.length()));

            //upload the file
            String fileName = fileDataObjectWrapper.getFileDataObject().getName();
            String blobKey = namingStrategy.getFilePath(userName, fileNameOnStorage);
            uploadBlobOnStorageProvider(fileName, blobKey, sourceFile, true);

            // delete the local temporary file
            boolean deleted = sourceFile.delete();

            //the previous body was the temporary file, and now that the temp
            //file is deleted, also the reference to it become invalid
            fileDataObjectWrapper.getFileDataObject().setBodyFile(null);
        } catch (Throwable ex) {
            throw new DAOException("Error uploading file data object on storage", ex);
        }
    }

    public void uploadBlobOnS3StorageProvider(
        BlobStoreContext context,
        String bucketName,
        String itemName,
        String itemKey,
        File sourceFile,
        boolean specifyContentDisposition)
        throws DAOException {

        S3Client s3Client = S3Client.class.cast(context.getProviderSpecificContext().getApi());

        S3Object object = s3Client.newS3Object();
        object.getMetadata().setKey(itemKey);
        object.setPayload(sourceFile);
        if (specifyContentDisposition) {
            object.getMetadata().getContentMetadata().setContentDisposition(
                CONTENT_DISPOSITION + itemName);
        }
        s3Client.putObject(bucketName, object, withAcl(CannedAccessPolicy.PUBLIC_READ));
    }

    public void uploadBlobOnJcloudStorageProvider(
        BlobStore blobStore,
        String containerName,
        String itemName,
        String itemKey,
        File sourceFile,
        boolean specifyContentDisposition)
        throws DAOException {

        try {
            Blob blob = blobStore.newBlob(itemKey);
            FilePayload payload = new FilePayload(sourceFile);
            if (specifyContentDisposition) {
                payload.getContentMetadata().setContentDisposition(
                    CONTENT_DISPOSITION + itemName);
            }
            blob.setPayload(payload);
            blobStore.putBlob(containerName, blob);

        } catch (Throwable ex) {
            throw new DAOException("Error uploading object ", ex);
        }
    }

    private File createTmpFileFromPayload(Payload payload) throws DAOException {
        InputStream input = null;
        try {
            input = payload.getInput();

            File file = helper.saveTmpFile(input);
            tmpFiles.add(file);
            return file;

        } catch (IOException ex) {
            throw new DAOException("Unable to save locally the file retrieved from the cloud.", ex);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

}
