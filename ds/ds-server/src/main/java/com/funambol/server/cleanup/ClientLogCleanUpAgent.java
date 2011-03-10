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
import java.io.FileFilter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.FileArchiverException;
import com.funambol.framework.tools.FileSystemLock;
import com.funambol.framework.tools.FileSystemLockException;
import com.funambol.framework.tools.IOTools;

/**
 * It's the agent responsible to perform the archivation/cleanup of the directory
 * containing the log files uploaded by the client using the send log service.
 *
 * @version $Id$
 */
public class ClientLogCleanUpAgent implements Runnable {

    //---------------------------------------------------------------- Constants

    protected final static FunambolLogger logger =
        FunambolLoggerFactory.getLogger(ClientLogCleanUpPlugin.CLEANUP_LOGGER_NAME);

    private final static String   ZIP_FILE_PATTERN    = "yyyyMMdd_HHmmss";
    private final DateFormat      formatter           =
        new SimpleDateFormat(ZIP_FILE_PATTERN);
    private final static String   TMP_FILE_EXTENSION  = ".tmp";
    private final static String[] EXTENSION_TO_SKIP   =
        new String[]{TMP_FILE_EXTENSION, FileSystemLock.DEFAULT_LOCK_EXTENSION};
    private final static FileFilter fileFilter           =
        new NotFileFilter(new SuffixFileFilter(EXTENSION_TO_SKIP));

    //------------------------------------------------------------ Instance data

    private boolean enabled                     = true;
    private long timeToRest                     = 60 * 1000;
    private File directoryToCheck               = null;
    private int activationThreshold             = 30000;
    private int maxNumberOfArchivedFiles        = 30000;
    private int numberOfArchivedFilesToDelete   = 50;
    FileSystemLock lock                         = null;
    private String  nameOfDirectoryToCheck      = null;
    private String targetArchivationDirectory   = null;
    private String threadName                   = null;

    //------------------------------------------------------------- Constructors

    /**
     * Creates a new agent that will monitor the clients log dir in order to archive
     * log files when the activation threshold is reached.
     * The activation threshold means the max number of files that the log dir
     * may contain. When the limit is exceeded, the cleanup agent starts in order
     * to free file system resources.
     * If the targetArchivationDirectory is specified, then all log files are 
     * compressed and moved into an archive iside this dir, if the
     * targetArchivationDirectory is null, they are just deleted.
     *
     * @param clientsLogDir is the directory that contains the log files uploaded
     * by different clients
     * @param targetArchivationDirectory is the target directory where archived
     * log files will be stored
     * @param activationThreshold is the max number of files that the client
     * log directory may contain, when this limit is reached, the cleanup agent
     * starts in order to free file system resources
     * @param maxNumberOfArchivedFiles is the max number of the archived files
     * contained inside the archivation directory
     * @param numberOfArchivedFilesToDelete if the number of files that must be
     * deleted if the number of archived files reaches the maxNumberOfArchivedFiles
     * value
     * @param timeToRest it's the time the agent rests before checking the log
     * folder, expressed in milliseconds.
     * @param lockName it's the name of the lock file while the lock file will
     * be created in the client log dirs.
     */
    public ClientLogCleanUpAgent(String clientsLogDir,
                                 String targetArchivationDirectory,
                                 int activationThreshold,
                                 int maxNumberOfArchivedFiles,
                                 int numberOfArchivedFilesToDelete,
                                 long   timeToRest,
                                 String lockName) {
        this(clientsLogDir,
             targetArchivationDirectory,
             activationThreshold,
             maxNumberOfArchivedFiles,
             numberOfArchivedFilesToDelete,
             timeToRest,
             lockName,
             0L);
    }

    /**
     * Creates a new agent that will monitor the clients log dir in order to archive
     * log files when the activation threshold is reached.
     * The activation threshold means the max number of files that the log dir
     * may contain. When the limit is exceeded, the cleanup agent starts in order
     * to free file system resources.
     * If the targetArchivationDirectory is specified, then all log files are
     * compressed and moved into an archive iside this dir, if the
     * targetArchivationDirectory is null, they are just deleted.
     *
     * @param clientsLogDir is the directory that contains the log files uploaded
     * by different clients
     * @param targetArchivationDirectory is the target directory where archived
     * log files will be stored
     * @param activationThreshold is the max number of files that the client
     * log directory may contain, when this limit is reached, the cleanup agent
     * starts in order to free file system resources
     * @param maxNumberOfArchivedFiles is the max number of the archived files
     * contained inside the archivation directory
     * @param numberOfArchivedFilesToDelete if the number of files that must be
     * deleted if the number of archived files reaches the maxNumberOfArchivedFiles
     * value
     * @param timeToRest it's the time the agent rests before checking the log
     * folder, expressed in milliseconds.
     * @param lockName it's the name of the lock file while the lock file will
     * be created in the client log dirs.
     * @param lockExpirationTime is the time, expressed in ms, after which a
     * file system lock expires
     */
    public ClientLogCleanUpAgent(String clientsLogDir,
                                 String targetArchivationDirectory,
                                 int activationThreshold,
                                 int maxNumberOfArchivedFiles,
                                 int numberOfArchivedFilesToDelete,
                                 long   timeToRest,
                                 String lockName,
                                 long lockExpirationTime) {
        if(clientsLogDir==null) {
            throw new IllegalArgumentException(
                "Unable to create a cleanup agent monitoring a null log directory.");
        }
        this.nameOfDirectoryToCheck        = clientsLogDir;
        this.directoryToCheck              = new File(nameOfDirectoryToCheck);

        checkNotNegative("timeToRest",timeToRest);
        this.timeToRest                    = timeToRest;
        checkNotNegative("activationThreshold",activationThreshold);
        this.activationThreshold           = activationThreshold;
        checkNotNegative("maxNumberOfArchivedFiles",maxNumberOfArchivedFiles);
        this.maxNumberOfArchivedFiles      = maxNumberOfArchivedFiles;
        checkNotNegative("numberOfArchivedFilesToDelete",numberOfArchivedFilesToDelete);
        this.numberOfArchivedFilesToDelete = numberOfArchivedFilesToDelete;

        if(numberOfArchivedFilesToDelete>maxNumberOfArchivedFiles) {
            throw new IllegalArgumentException("The number of archived files to be deleted is "+
                                               "larger than the max number of archived files allowed.");
        }


        this.targetArchivationDirectory    = targetArchivationDirectory;

        if(lockExpirationTime<=0) {
            this.lock                      = new FileSystemLock(directoryToCheck,
                                                                lockName);
        } else {
            this.lock                      = new FileSystemLock(directoryToCheck, 
                                                                lockName,
                                                                lockExpirationTime);
        }
    }


    //----------------------------------------------------------- Public Methods

    public void run() {
        this.threadName = Thread.currentThread().getName();

        if(logger.isTraceEnabled()) {
            logger.trace("Cleanup agent '"+this+"' monitoring '"+
                         directoryToCheck.getAbsolutePath()+"'.");
        }
        do {
            try {

                internalRun();
                rest();

            } catch(Throwable t) {
               logger.error("An error occurred while thread '"+
                            threadName+"' was cleaning the log directory",t);
            } finally {
                releaseLock();
            }
        } while(enabled);

        if(logger.isTraceEnabled()) {
            logger.trace("Cleanup agent stopping.");
        }
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("activation threshold", this.activationThreshold);
        builder.append("directory to check", this.directoryToCheck.getAbsolutePath());
        builder.append("enabled", this.enabled);
        builder.append("lock", this.lock);
        builder.append("max number of archived files", this.maxNumberOfArchivedFiles);
        builder.append("name of the directory to check", this.nameOfDirectoryToCheck);
        builder.append("name of the target archivation directory", this.targetArchivationDirectory);
        builder.append("number of archived files to delete", this.numberOfArchivedFilesToDelete);
        builder.append("thread name", this.threadName);
        builder.append("time to rest", this.timeToRest);
        return builder.toString();
    }

    /**
     * @return the enabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled the enabled to set
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return the timeToRest
     */
    public long getTimeToRest() {
        return timeToRest;
    }

    /**
     * @return the directoryToCheck
     */
    public File getDirectoryToCheck() {
        return directoryToCheck;
    }

    /**
     * @return the activationThreshold
     */
    public int getActivationThreshold() {
        return activationThreshold;
    }

    /**
     * @return the maxNumberOfArchivedFiles
     */
    public int getMaxNumberOfArchivedFiles() {
        return maxNumberOfArchivedFiles;
    }

    /**
     * @return the numberOfArchivedFilesToDelete
     */
    public int getNumberOfArchivedFilesToDelete() {
        return numberOfArchivedFilesToDelete;
    }

    /**
     * @return the sourceFolder
     */
    public String getNameOfDirectoryToCheck() {
        return nameOfDirectoryToCheck;
    }

    /**
     * @return the targetArchivationDirectory
     */
    public String getTargetArchivationDirectory() {
        return targetArchivationDirectory;
    }

    //---------------------------------------------------------- Private Methods

    /**
     * this method will check if the archivation is needed and will start the
     * archivation process if and only if this thread succeeds in acquiring the
     * file system lock.
     */
    private void internalRun() throws Throwable {
        if (archivationNeeded()) {
            if(logger.isTraceEnabled()) {
                logger.trace("Thread '"+threadName+"' is starting archivation process.");
            }
            if (acquireLock()) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Thread '"+threadName+"' acquires the lock.");
                }
                if(isArchivationRequired()) {
                    archive(nameOfDirectoryToCheck, targetArchivationDirectory);
                } else {
                    cleanup(nameOfDirectoryToCheck);
                }
            } else {
                
            }
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Thread '"+threadName+
                             "' is going to sleep archivation process is not required.");
            }
        }
    }

    /**
     * it forces the current thread to sleep for a timeToRest ms time
     */
    private void rest() {
        if(logger.isTraceEnabled()) {
            logger.trace("Thread '"+threadName+"' is going to sleep for '"+
                         timeToRest+"' ms.");
        }
        try {
            // sleeping only if enabled
            if(this.enabled) {
                Thread.sleep(timeToRest);
            }
        } catch(InterruptedException e) {
            if(logger.isTraceEnabled()) {
                logger.trace("Thread '"+threadName+"' has been interrupted while resting.");
            }
            this.enabled = false;
        }
        if(logger.isTraceEnabled()) {
            logger.trace("Thread '"+threadName+"' is awake.");
        }
    }

    /**
     * check if the this thread needs to start the archivation process,
     * i.e. the directory containing log files contains more the activationThreshold
     * number of files/directories
     * 
     * @return true if the archivation process must be performed
     */
    private boolean archivationNeeded() {
        boolean validDirectory   = IOTools.checkDirectory(directoryToCheck);
        boolean thresholdReached = false;
        File[]  children         = null;
        if(validDirectory) {
            children         = directoryToCheck.listFiles();
            thresholdReached = children!=null && children.length>activationThreshold;
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("The directory to check '"+directoryToCheck+
                             "' is not valid, archivation process skipped.");
            }
        }

        if(thresholdReached) {
            if(logger.isTraceEnabled()) {
                logger.trace("The directory to check '"+directoryToCheck+
                             "' contains '"+children.length+
                             "' files and exceeds the threshold '"+
                             activationThreshold+
                             "'!Archivation process started.");
            }
            return true;
        }
        return false;

    }

    /**
     * tries to acquire the lock without throwing any exception in case an
     * exception is thrown
     *
     * @return true in the case the lock is acquired, false in any other case
     */
    private boolean acquireLock() {
        boolean lockAcquired = false;
        try {
            lockAcquired = lock.acquireLock();
        } catch(Throwable e) {
            logger.error("An error occurred while thread '"+threadName+
                         "' tried to acquire the lock.",e);
        }
        return lockAcquired;
    }

    /**
     * perform the archivation of the given sourceFolder in a zip file whose
     * name is specified by the targetArchivationDirectory input parameter
     * If the targetArchivationDirectory is null or empty
     * @param sourceFolder is the foulder to compress
     * @param targetArchivationDirectory is the target file that will contained all the folder
     * structure archived
     * @throws Throwable if any error occcurs during the archivation process
     */
    private void archive(String sourceFolder, String targetDirectory) throws Throwable {
        if(logger.isTraceEnabled()) {
            logger.trace("Thread '"+threadName+
                         "' is archiving folder '"+
                         sourceFolder+"' into archivation directory '"+
                         targetDirectory+"'.");
        }

        checkAndCreateTargetFolder(targetDirectory);

        String targetFile = buildTargetFileName(targetDirectory,new Date());

        try {
            IOTools.compressFolder(sourceFolder,
                                   targetFile,
                                   fileFilter,
                                   new CleanUpEventHandler(lock));
        } catch(FileArchiverException e) {
            logger.error("An error occurred while thread '"+threadName+
                         "' was archiving '"+directoryToCheck+"'.", e);
        }
    }

    private void releaseLock() {
        try {
            if(lock!=null && lock.isHeldByCurrentThreadQuietly()) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Thread '"+threadName+"' is releasing the lock.");
                }
                lock.releaseLock();
                
                if(logger.isTraceEnabled()) {
                    logger.trace("Thread '"+threadName+"' releases the lock.");
                }
            }
        } catch(FileSystemLockException e) {
            logger.error("An error occurred while thread '"+
                         threadName+"' was releasing the lock.",e);
        }
    }

    private boolean isArchivationRequired() {
        return targetArchivationDirectory!=null &&
               targetArchivationDirectory.length()>0;
    }

    /**
     * removes recursively all the content of the given directory without deleting
     * not empty directory and tmp files.
     *
     * @param sourceFolder it's the directory whose content must be removed.
     */
    private void cleanup(String sourceFolder) {
        if(logger.isTraceEnabled()) {
            logger.trace("Cleaning up the clients log directory '"+sourceFolder+"'.");
        }
        try {
            IOTools.deleteDirectory(sourceFolder,fileFilter);
        } catch(Throwable e) {
            logger.error("An error occurred while thread '"+
                         threadName+"' was removing the content of '"+
                         directoryToCheck+"'.",e);
        }
    }

    /**
     * Check if the folder that contains all the archived files exists and has
     * reached the maximum number of archived file.
     * In that case the oldest numberOfArchivedFilesToDeleted will be deleted in
     * order to free some resources.
     * If the target folder doesn't exist, this method tries to create it
     *
     * @param targetFolderName the name of the target file that the process is archiving
     * @throws IOException if the deletion of any of the oldest files failed.
     */
    private void checkAndCreateTargetFolder(String targetFolderName) throws IOException {
        if(targetFolderName==null) {
            throw new IllegalArgumentException("Unable to check the target folder for a null target file.");
        }

        if(logger.isTraceEnabled()) {
            logger.trace("Checking if we need to release some resources of the archivation directory '"+targetArchivationDirectory+"'.");
        }

        File targetFolder   = new File(targetFolderName);

        if(!targetFolder.exists()) {
            IOTools.createNotExistingDirectoryOnRaceCondition(targetFolder,
                                                              timeToRest,
                                                              TimeUnit.MILLISECONDS);
            return;
        }

        if( targetFolder.isDirectory()) {
            File[] children = IOTools.listFilesSortingByTime(targetFolder);
            // Adding 1 to the number of files since we're going to add a new
            // archive
            if(children.length+1>=this.maxNumberOfArchivedFiles) {
                if(logger.isTraceEnabled()) {
                    logger.trace("Archivation directory '"+targetArchivationDirectory+
                                 "' contains more files than allowed '"+
                                 maxNumberOfArchivedFiles+"'! Removing oldest '"+
                                 numberOfArchivedFilesToDelete+"' files.");
                }
                for(int i=0;i<numberOfArchivedFilesToDelete && i<children.length;i++) {
                    String filename = children[i].getName();
                    if(logger.isTraceEnabled()) {
                        logger.trace("Deleting archive '"+filename+"'.");
                    }
                    if(!children[i].delete()) {
                        throw new IOException("Unable to delete archive '"+filename+
                                              "' when the maximum number of archived file is reached '"+
                                              maxNumberOfArchivedFiles+"'.");
                    }
                }
            }
        } else {
            throw new IOException("Archivation directory '"+targetFolderName+
                                  "' isn't recognized as a directory");
        }

    }

    /**
     * builds the path where to store the archive using as root the targetDirectory
     * @param targetDirectory the name of the target directory where to archives
     * the files.
     * @return
     */
    private String buildTargetFileName(String targetDirectory,Date when) {
        String fileName = formatter.format(when)+".zip";
        if(targetDirectory!=null && targetDirectory.length()>0F) {
            if(targetDirectory.endsWith("\\") || targetDirectory.endsWith("/")) {
                return targetDirectory.substring(0,targetDirectory.length()-1)+"/"+fileName;
            } else {
                return targetDirectory+"/"+fileName;
            }

        }
        return fileName;
    }

    private void checkNotNegative(String parameterName, long parameterValue) {
        if(parameterValue<0) {
            throw new IllegalArgumentException("Unable to create a cleanup agent "+
                                               "setting a negative value '"+
                                               parameterValue+"' for parameter '"+
                                               parameterName+"'.");
        }
    }

}
