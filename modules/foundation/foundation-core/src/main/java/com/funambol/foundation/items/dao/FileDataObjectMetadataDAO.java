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

import com.funambol.common.media.file.FileDataObject;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import java.util.List;

/**
 * Interface implemented by any DAO that deals with the storage and retrieval of
 * FileDataObjectMetadata information on a back end system.
 *
 * @version $Id$
 */
public interface FileDataObjectMetadataDAO {

    /**
     * Gets the synchronization information and the metadata of a file data
     * object from the back end system.
     *
     * @param id unique locator of the file data object in the back end system.
     * @return a FileDataObjectWrapper with a null body
     * @throws DAOException
     */
    public FileDataObjectWrapper getItem(String id) throws DAOException;

    /**
     * Adds the metadata of a file data object to the back end system.
     *
     * @param item the file data object
     * @return unique locator of the content
     * @throws DAOException
     */
    public String addItem(FileDataObjectWrapper item) throws DAOException;

    /**
     * Updates the metadata of a file data object on the back end system.
     *
     * @param item the file data object
     * @throws DAOException
     */
    public void updateItem(FileDataObjectWrapper item) throws DAOException;


    /**
     * Updates only metadata fields that are present in body, like crc, size, 
     * localname etc
     *
     * @param item the file data object
     * @throws DAOException
     */
    public void updateItemWhenBodyChanges(FileDataObjectWrapper item) throws DAOException;

    /**
     * Gets the unique locator of the file content in the back end system where
     * file bodies are stored (it may be different from the one used for the
     * metadata).
     *
     * @param id the unique locator of the file data object in the back end
     *           system devoted to the storage of the metadata
     * @return the unique locator of the file data object in the back end system
     *         devoted to the storage of file bodies
     * @throws com.funambol.foundation.exception.DAOException
     */
    public String getLocalName(String id) throws DAOException;

    /* Gets just the size of a file by extracting that information from its
     * metadata.
     *
     * @param id the unique locator of the file data object in the back end
     *           system devoted to the storage of the metadata
     * @return the file size or 0 if the item cannot be found
     * @throws com.funambol.foundation.exception.DAOException
     */
    public long getItemSize(String id) throws DAOException;

    /**
     * Retrieves the list of the IDs of all the twin items of a file data
     * object.
     *
     * @param item the item whose twins are to be looked for
     * @return the list of all the twin items' IDs
     * @throws com.funambol.foundation.exception.DAOException
     */
    public List getTwinItems(FileDataObject item) throws DAOException;

    /**
     * Gets the storage space currently used or reserved by the user fo files to
     * bee uploaded..
     *
     * @return the storage space used or reserved for uploads in bytes
     * @throws DAOException if the space used cannot be calculated
     */
    public long getReservedStorageSpace() throws DAOException;

    /**
     * Gets the storage space occupied by uploaded files.
     *
     * @return the used storage space in bytes
     * @throws DAOException if the space used cannot be calculated
     */
    public long getStorageSpaceUsage() throws DAOException;

    /**
     * Removes from the database the expired incomplete items.
     * Items are incomplete if they don't have a corresponding file on
     * the file system i.e. if the local_name field is null.
     * Incomplete items are considered expired if older than 24h.
     * 
     */
    public void removeExpiredIncompleteItems()  throws DAOException;

    /**
     * Reset the size field to the value of the size_on_storage field for the
     * misaligned items i.e. items for which the metadata has been updated but
     * the updated file has not been uploaded.
     * @throws DAOException
     */
    public void resetSizeForMisalignedItems()  throws DAOException;
}