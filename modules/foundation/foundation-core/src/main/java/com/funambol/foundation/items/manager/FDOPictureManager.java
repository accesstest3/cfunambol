/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2011 Funambol, Inc.
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
package com.funambol.foundation.items.manager;

import java.io.File;
import java.sql.Timestamp;
import java.util.Map;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.UploadStatus;

import com.funambol.framework.server.Sync4jUser;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.engine.source.FileDataObjectSyncSourceConfig;
import com.funambol.foundation.engine.source.FDOPictureSyncSourceConfig;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.MediaUtils;
import com.funambol.foundation.util.FileSystemDAOHelper;

/**
 * Manager for file data object representing pictures
 *
 * @version $Id: FDOPictureManager.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FDOPictureManager extends FileDataObjectManager {

    // --------------------------------------------------------------- Constants
    public static final String PICTURE_SOURCE_URI = "picture";

    //------------------------------------------------------------- Constructors

    /**
     * Create a new instance of FileDataObjectManager.
     *
     * @param sync4jUser the user
     * @param sourceURI the source URI
     * @param fdossc the configuration of sync source
     * @throws DAOException if an error occurs
     */
    public FDOPictureManager(Sync4jUser sync4jUser,
                             String sourceURI,
                             FileDataObjectSyncSourceConfig fdossc)
    throws DAOException {

        super(sync4jUser, sourceURI, fdossc);
    }

    /**
     * Create a new instance of FileDataObjectManager.
     *
     * @param sync4jUser the user
     * @param sourceURI the source URI
     * @param fdossc the configuration of sync source
     * @param namingStrategy the naming strategy
     * @param helper the file system helper
     * @throws DAOException if an error occurs
     */
    public FDOPictureManager(Sync4jUser sync4jUser,
                             String sourceURI,
                             FileDataObjectSyncSourceConfig fdossc,
                             FileDataObjectNamingStrategy namingStrategy,
                             FileSystemDAOHelper helper)
    throws DAOException {

        super(sync4jUser, sourceURI, fdossc, namingStrategy, helper);
    }

    // ------------------------------------------------------ Overridded methods

    @Override
    public String addItem(String fileName, byte[] content, String contentType)
    throws DAOException {
        if (fileName == null) {
            throw new IllegalArgumentException("Filename must be not null");
        }

        if (content == null) {
            throw new IllegalArgumentException("Content must be not null");
        }

        FileDataObject fdo =
            createFileDataObject(fileName, content, contentType);
        fdo.setUploadStatus(UploadStatus.UPLOADED);

        return addItem(fdo, new Timestamp(System.currentTimeMillis()));
    }

    @Override
    public String addItem(FileDataObject fdo, Timestamp timeStamp)
    throws DAOException {
        String addedItemId;

        if (!fdo.hasBodyFile()) {
           //improved sync, add only metadata info
            addedItemId = addItemMetadata(fdo, timeStamp);

        } else {
            FileDataObjectWrapper fdow =
                new FileDataObjectWrapper(fileDataObjectDAO.getNextID(), userId, fdo);
            fdow.setLastUpdate(timeStamp);
            fdow.setLocalName(namingStrategy.createFileName(fdow.getId()));

            fileDataObjectDAO.addItemMetadata(fdow);
            uploadBody(fdow);

            addedItemId = fdow.getId();
        }

        return addedItemId;
   }

    @Override
    public FileDataObjectWrapper addItemBody(FileDataObjectWrapper itemToAdd)
    throws DAOException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("FDOPictureManager adding item body...");
            }

            itemToAdd.setLocalName(namingStrategy.createFileName(itemToAdd.getId()));

            uploadBody(itemToAdd);

            return itemToAdd;

        } catch (Exception ex) {
            throw new DAOException("Error adding item body", ex);
        }

    }
    // --------------------------------------------------------- Private Methods
    /**
     * Uploads the file data object on the storage.
     *
     * @param itemToAdd the file data object to upload
     * @param tmpFileName the temporary file name
     * @return the file data object with some more information set
     * @throws DAOException if an error occurs during upload
     */
    private FileDataObjectWrapper uploadBody(FileDataObjectWrapper itemToAdd)
    throws DAOException {
        try {
            if (log.isTraceEnabled()) {
                log.trace("Start uploading file body...");
            }

            // creates thumbnails
            String tmpFileName = itemToAdd.getFileDataObject().getBodyFile().getName();
            String thumbnailFolderPath = helper.getEXTFolderPath(tmpFileName);
            String[] thumbnailsSize =
                ((FDOPictureSyncSourceConfig) config).getThumbnailsSize();
            double thumbnailThreshold =
                ((FDOPictureSyncSourceConfig) config).getThumbnailThreshold();

            MediaUtils utils = new MediaUtils();
            Map<String, File> thumbnails = utils.createThumbnails(
                itemToAdd,
                userId,
                thumbnailFolderPath,
                thumbnailsSize,
                thumbnailThreshold);

            // retrieves EXIF data from FDO body and set it in the properties map
            utils.handleEXIF(itemToAdd);

            String fileNameOnStorage = itemToAdd.getLocalName();
            fileDataObjectDAO.addItemBody(itemToAdd);

            String extFolderPath = helper.getEXTFolderRelativePath(fileNameOnStorage);

            boolean specifyContentDisposition = false;
            fileDataObjectDAO.uploadFilesOnStorageProvider(extFolderPath, thumbnails, specifyContentDisposition);
            // remove the local thumbnail folder
            helper.removeDir(thumbnailFolderPath);

            return itemToAdd;

        } catch (Exception ex) {
            throw new DAOException("Error uploading item body", ex);
        }

    }

}

