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

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.engine.SyncItemFactory;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.SyncItemState;

import com.funambol.foundation.engine.source.FileDataObjectSyncSource;
import com.funambol.foundation.util.FileSystemDAOHelper;

/**
 * Responsibility: creates StreamingSyncItems. It creates and provides to the
 * newly created StreamingSyncItems the FileSystemDAOHelper.
 * Collaborator:
 * - FileSystemDAOHelper to manage the temporary file on the file system
 * @version $Id$
 */
public class FDOStreamingSyncItemFactory implements SyncItemFactory {

    /**
     * The FileSystemDAOHelper provided to the new AbstractSyncItem
     */
    private FileSystemDAOHelper fileDAO;

    /**
     * The list of all temporary files associated to the StreamingSyncItems
     * created by the factory.
     */
    private List<String> tmpFileNames = new ArrayList<String>();

    // ------------------------------------------------------------- Constructor

    public FDOStreamingSyncItemFactory(String userName, String rootPath,
            int fileSysDirDepth) throws IOException {

        fileDAO =  createFileSystemDAOHelper(userName, rootPath, fileSysDirDepth);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Factory method to create a StreamingSyncItem
     * @param syncSource The SyncSource referred to the SyncItem
     * @param key The SyncItem key
     * @return a StreamingSyncItem based on chunk
     */
    public AbstractSyncItem getSyncItem(SyncSource syncSource, Object key) {

        return getSyncItem(syncSource ,
             key,
             null, // parentKey
             null, // mappedKey
             SyncItemState.UNKNOWN, // state,
             null, // format
             FileDataObjectSyncSource.FDO_MIME_TYPE, // type
             null  // timestamp
        );
    }


    /**
     * Factory method to create a StreamingSyncItem
     *
     * @param syncSource
     * @param key
     * @param parentKey
     * @param mappedKey
     * @param state
     * @param format
     * @param type
     * @param timestamp
     * @return
     */
    public AbstractSyncItem getSyncItem(SyncSource syncSource,
            Object key,
            Object parentKey,
            Object mappedKey,
            char state,
            String format,
            String type,
            Timestamp timestamp) {

        FDOStreamingSyncItem syncItem = new FDOStreamingSyncItem(syncSource, key,
                parentKey,
                mappedKey,
                state,
                format,
                type,
                timestamp,
                fileDAO);

        tmpFileNames.add(syncItem.getTmpFileName());

        return syncItem;
    }

    /**
     * Deletes from the file system all the temporary files, related to the
     * SyncItems created by the factory, still existing at the end of the sync
     * session.
     * Deletes also tmp files created in other sync sessions older than 24h
     */
    public void release() {
        deleteSyncSessionTmpFiles();
        deleteOldTmpFiles();
    }

    // --------------------------------------------------------- Private Methods

    private FileSystemDAOHelper createFileSystemDAOHelper(String userName,
            String rootPath, int fileSysDirDepth) throws IOException {
        return new FileSystemDAOHelper(userName, rootPath, fileSysDirDepth);
    }

    private void deleteSyncSessionTmpFiles() {
        for (String tmpFileName : tmpFileNames) {
            delete(tmpFileName);
        }
    }

    private void delete(String tmpFileName) {
        fileDAO.delete(tmpFileName);
    }

    private void deleteOldTmpFiles() {
        fileDAO.deleteOldTmpFiles();
    }

}
