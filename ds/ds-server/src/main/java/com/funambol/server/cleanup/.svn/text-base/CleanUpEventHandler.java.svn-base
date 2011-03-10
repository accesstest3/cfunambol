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

package com.funambol.server.cleanup;

import java.io.File;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.ArchivationEventHandler;
import com.funambol.framework.tools.FileSystemLock;
import com.funambol.framework.tools.FileSystemLockException;
import com.funambol.framework.tools.IOTools;

/**
 * It's the class that will handle the events triggered while archiving the
 * log directory and it's responsible to:
 * - delete archived files
 * - delete archived (empty) directory
 * - remove skipped files older than one hour, lock and tmp files
 * 
 * @version $Id$
 */
public class CleanUpEventHandler implements ArchivationEventHandler {

    //---------------------------------------------------------------- Constants

    FunambolLogger logger                                      =
            FunambolLoggerFactory.getLogger(ClientLogCleanUpPlugin.CLEANUP_LOGGER_NAME);
    private final static long ONE_HOUR                         = 1 * 60 * 60 * 1000;
    private final static long TIME_TO_WAIT_BETWEEN_TWO_RENEWAL = 100;

    //------------------------------------------------------------ Instance Data
    FileSystemLock lock        = null;
    private long lastRenewTime = 0L;

    //-------------------------------------------------------------- Constructor
    /**
     *  Builds a new event handler that will renew the given lock
     * @param lock the FileSystemLock  that must be renewed while archiving the
     * folder
     */
    public CleanUpEventHandler(FileSystemLock lock) {
        this.lock = lock;
    }

    //----------------------------------------------------------- Public Methods

    public void directoryAdded(String path, File sourceFile) {
        if(sourceFile==null) {
            throw new IllegalArgumentException("Unable to handle the adding of a null directory.");
        }
        String directoryName = sourceFile.getName();
        if(IOTools.checkEmptyDirectory(sourceFile)) {
            if(sourceFile.delete()) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Empty directory '"+directoryName+"' has been deleted.");
                }
            } else {
               if(logger.isTraceEnabled()) {
                   logger.trace("Empty directory '"+directoryName+"' deletion failed.");
                }
            }
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Directory '"+directoryName+"' deletion failed since it's not empty.");
            }
        }
        renewLock();
    }

    public void fileAdded(String path, File sourceFile, long size) {
        if(sourceFile==null) {
            throw new IllegalArgumentException("Unable to handle the adding of a null file.");
        }
        String fileName = sourceFile.getName();
        
        if(sourceFile.delete()) {
            if(logger.isTraceEnabled()) {
                logger.trace("Archived file '"+fileName+"' has been deleted.");
            }
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Deletion of archived file '"+fileName+"' failed.");
            }
        }
        renewLock();
    }

    public void directorySkipped(String path, File sourceFile) {
        if(sourceFile==null) {
            throw new IllegalArgumentException("Unable to handle the skipping of a null directory.");
        }
        renewLock();
    }

    public void fileSkipped(String path, File sourceFile) {
        if(sourceFile==null) {
            throw new IllegalArgumentException("Unable to handle the skipping of a null file.");
        }
        String fileName = sourceFile.getName();
        long   now          = System.currentTimeMillis();
        long   lastModified = sourceFile.lastModified();

        if(logger.isTraceEnabled()) {
            logger.trace("Checking whether '"+fileName+"' "+
                         "whose lastModification time is '"+
                         lastModified+"' is older than one our '"+now+"'.");
         }

        if(lastModified+ONE_HOUR < now) {
            if(sourceFile.delete()) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Skipped file '"+fileName+"' has been deleted.");
                }
            } else {
               if(logger.isTraceEnabled()) {
                   logger.trace("Deletion of skipped file '"+fileName+"' failed.");
                }
            }
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Skipped file '"+fileName+"' won't be deleted since is not so old.");
            }
        }

       renewLock();
    }

    //---------------------------------------------------------- Private Methods
    /**
     * this method renews the lock taken by this thread
     */
    private void renewLock() {
        if(lastRenewTime + TIME_TO_WAIT_BETWEEN_TWO_RENEWAL < System.currentTimeMillis()) {
            try {
                if(lock!=null && lock.isHeldByCurrentThread()) {
                    if(logger.isTraceEnabled()) {
                        logger.trace("Renewing the lock!");
                    }
                    lock.renewLock();
                }
            } catch(FileSystemLockException e) {
                logger.warn("The renewal of the lock failed while cleaning up the clients log directory.",e);
            }
        }
    }
   
}
