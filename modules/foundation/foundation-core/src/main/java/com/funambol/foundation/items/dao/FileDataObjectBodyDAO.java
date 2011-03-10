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
import java.util.Map;

import com.funambol.common.media.file.FileDataObjectBody;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;

/**
 * Interface implemented by any DAO that deals with the storage and retrieval of
 * FileDataObjectBody information on a back end system.
 *
 * @version $Id$
 */
public interface FileDataObjectBodyDAO {

    /**
     * Gets the content of a file data object from the back end system.
     *
     * @param localFileName unique locator of the file in the back end system.
     * @return body of the file
     * @throws DAOException
     */
    public FileDataObjectBody getItem(String localFileName) throws DAOException;

    /**
     * Adds the content of a file data object to the back end system.
     * Side Effects: modifies the localName of the item with the new local file
     * name.
     *
     * @param item the file data object
     * @throws DAOException
     */
    public void addItem(FileDataObjectWrapper item) throws DAOException;

    /**
     * Creates a new file with the given content
     * @param content the file content
     * @return the created file
     * @throws DAOException if an error occurs
     */
    public File addTmpBodyFile(byte[] content) throws DAOException;

    /**
     * Updates the content of a file data object on the back end system.
     * Side Effects: modifies the localName of the item with the new local file
     * name.
     *
     * @param item the file data object
     * @throws DAOException
     */
    public void updateItem(FileDataObjectWrapper item) throws DAOException;

    /**
     * Removes the content of a file data object.
     *
     * @param localFileName unique locator of the file in the back end system.
     * @throws DAOException
     */
    public void removeItem(String localFileName) throws DAOException;

    /**
     * Removes the bodies of all file data objects for this user.
     *
     * @throws DAOException
     */
    public void removeAllItems() throws DAOException;

    /**
     * Release all the resources used to store the file data object bodies
     * belonging to a user
     * @throws DAOException if an error occurs
     * @deprecated use method removeAllItems instead
     */
    public void releaseResources() throws DAOException;

    /**
     * Release all the resources used to store and retrieve the file data object
     * bodies
     * @throws DAOException if an error occurs
     */
    public void close() throws DAOException;

    /**
     * Uploads the files contained in the map on the storage provider using as
     * file key the map key itself.
     *
     * @param filesToUpload the map with the files to upload
     * @throws DAOException if an error occurs
     */
    public void uploadExtFilesOnStorageProvider(String extFolderPath, Map<String, File> filesToUpload, boolean specifyContentDisposition)
    throws DAOException;

    /**
     *
     * @param extFolderPath
     * @param fileName
     * @param sourceFile
     * @throws DAOException
     */
    public void uploadExtFileOnStorageProvider(String extFolderPath, String fileName, File sourceFile, boolean specifyContentDisposition)
        throws DAOException;
}
