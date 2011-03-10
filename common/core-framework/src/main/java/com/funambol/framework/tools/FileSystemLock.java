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


package com.funambol.framework.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class is used to implement a file system based lock you can use to
 * synchronize threads running into different jvms.
 * The implementation is based on the the atomic operation createNewFile.
 * Pay attention when setting the expiration time. On some environments file
 * can be touched only with seconds granularity, so you'd better not to set
 * a value lower than 2 seconds.
 *
 * @version $Id$
 */
public class FileSystemLock {

    //---------------------------------------------------------------- Constants

    private final static int SLEEP_TIME_ON_DELETION_FAILS = 10;

    public static final int MAX_NUMBER_OF_RELEASE_LOCK_ATTEMPTS = 10;

    public static final String THREAD_OWNERSHIP_ERROR_MSG  =
                                   "The current thread doesn't hold this lock.";
    public static final String LOCK_NOT_ACQUIRED_ERROR_MSG =
                                     "This lock is not acquired by any thread.";
    public static final String RELEASING_LOCK_ERR_MSG =
      "An error occurred releasing the lock, unable to delete the file system lock";

    public static final String ACQUIRING_LOCK_ERR_MSG =
                     "An error occurred acquiring the lock on the file system.";

    public static final String RENEWING_EXPIRED_LOCK_ERROR_MSG =
                     "Unable to renew an expired lock.";

    /**
     * the seed is used to distinguish lock files belonging to different jvms
     */
    private static String seed =
            Long.toString(System.currentTimeMillis()) +
            new Random(System.currentTimeMillis()).nextLong();

    private static final Random randomGenerator =
                                        new Random(System.currentTimeMillis());


    protected static long DEFAULT_EXPIRATION_TIME = 3 * 60 * 1000;

    public final static String DEFAULT_LOCK_EXTENSION  = ".lck";

    public final static String SEED_KEY        = "lock.seed";
    public final static String THREAD_NAME_KEY = "lock.thread-name";
    public final static String THREAD_HASH_KEY = "lock.thread-hash";


    //------------------------------------------------------------ Instance data

    private final File directory;
    private final String lockName;
    private final Map<String,String> extraLockInfo;

    private File lockFile;
    private String lockFileName;
    protected long expirationTime;


    //------------------------------------------------------------- Constructors

    /**
     * Build a FileSystemLock object that will write a lock file with the given
     * name into the given directory.
     *
     * @param directory it's the directory where the file will be
     * @param lockName it's the name of the file that will be create as lock
     * in the given directory
     */
    public FileSystemLock(File directory, String lockName) {
      this(directory, lockName,null,DEFAULT_EXPIRATION_TIME);
    }


    /**
     * Build a FileSystemLock object that will write a lock file with the given
     * name into the given directory, appending the given extra information
     *
     * @param directory it's the directory where the file will be
     * @param lockName it's the name of the file that will be create as lock
     * @param extraLockInfo a map containing extra data appended to the lock
     * info file (maybe some human readable contents)
     */

    public FileSystemLock(File directory,
                          String lockName,
                          long expirationTime) {

        this(directory, lockName, null, expirationTime);
    }

    /**
     * Build a FileSystemLock object that will write a lock file with the given
     * name into the given directory, appending the given extra information
     *
     * @param directory it's the directory where the file will be
     * @param lockName it's the name of the file that will be create as lock
     * @param extraLockInfo a map containing extra data appended to the lock
     * info file (maybe some human readable contents)
     * @param expirationTime the time (expressed in ms) after which a lock expires
     */

    public FileSystemLock(File directory,
                          String lockName,
                          Map<String,String> extraLockInfo,
                          long expirationTime) {
        this.directory      = directory;
        this.lockName       = lockName;
        this.extraLockInfo  = extraLockInfo;
        this.lockFileName   = this.lockName + DEFAULT_LOCK_EXTENSION;
        this.lockFile       = new File(directory,lockFileName);
        this.expirationTime = expirationTime;
    }


    //----------------------------------------------------------- Public Methods

    /**
     * Return true if the creation of a lock file succeeded or if a lock file
     * belonging to this thread already exists. In this case, the lock is also
     * renewed.
     * @return true if the current thread succeded in acquiring the desired lock,
     * false otherwise, this invokation is non blocking
     * @throws FileSystemLockException if any error occurs acquiring the lock
     * on the file system
     */
    
    public boolean acquireLock() throws FileSystemLockException {
        boolean lockAcquired = acquireLockOnFileSystem();

        if(!lockAcquired) {
                if(removeExpiredLock()) {
                    lockAcquired = acquireLockOnFileSystem();
                }
        }

        return lockAcquired;
    }

    /**
     * This method allows to check if the lock is held by the thread invoking it.
     * If the file lock doesn't exist, i.e. the lock hasn't been acquired, then
     * an exception is thrown.
     * @return true if the lock has been acquired and the thread that invokes
     * this method owns it
     * @throws FileSystemLockException if any error occurs parsing the lock information
     * from the lock file or the lock is not acquired by any thread
     */
    
    public boolean isHeldByCurrentThread() throws FileSystemLockException {
        if(getLockFile().exists()) {
            LockInfo parsedLockInfo = null;
            try {
                parsedLockInfo = new LockInfo(getLockFile());
            } catch(FileSystemLockException e) {
                // it may happen that the file holding lock data has been
                // deleted by the owner while another thread is trying to
                // understand if he holds the lock
            }
            LockInfo myLockInfo     = new LockInfo();
            return myLockInfo.equals(parsedLockInfo);
        } else {
            throw new FileSystemLockException(LOCK_NOT_ACQUIRED_ERROR_MSG);
        }
    }

    /**
     * This method allows to check if the lock is held by the thread invoking it.
     * If the file lock doesn't exist, i.e. the lock hasn't been acquired, then
     * no exception is thrown.
     * @return true if the lock has been acquired and the thread that invokes
     * this method owns it
     * @throws FileSystemLockException if any error occurs parsing the lock information
     * from the lock file
     */

    public boolean isHeldByCurrentThreadQuietly() throws FileSystemLockException {
        try {
            return isHeldByCurrentThread();
        } catch(FileSystemLockException e) {
            if(!isLockNotAcquiredException(e)) {
                throw e;
            } else {
                return false;
            }
        }
    }

    /**
     * This method allows to check if the lock is expired.
     * If the file lock doesn't exist, i.e. the lock hasn't been acquired, then
     * no exception is thrown.
     * @return true if the lock is expired
     * @throws FileSystemLockException if any error occurs parsing the lock information
     * from the lock file
     */

    public boolean isLockExpiredQuietly() throws FileSystemLockException {
        try {
            return isLockExpired();
        } catch(FileSystemLockException e) {
            if(!isLockNotAcquiredException(e)) {
                throw e;
            } else {
                return false;
            }
        }
    }


    /**
     * This method allows to reales a lock acquired previously if and only if
     * the current thread holds this lock.
     * If the file lock doesn't exist, i.e. the lock hasn't been acquired, then
     * an exception is thrown.
     *
     * @throws FileSystemLockException if the removal of the lock file failed,
     * it's likely to occur since the fail may be read during the removal. So
     * consider to handle properly this exception
     * @throws IllegalMonitorStateException if the current thread doesn't own
     * the lock
     */
    public void releaseLock() throws FileSystemLockException  {
        if(isLockExpired() || isHeldByCurrentThread()) {
            if (!deleteLockFile()) {
                throw new FileSystemLockException(RELEASING_LOCK_ERR_MSG);
            }
        } else {
            throw new IllegalMonitorStateException(THREAD_OWNERSHIP_ERROR_MSG);
        }
    }

    /**
     * This method allows to reales a lock acquired previously if and only if
     * the current thread holds this lock.
     * The quietly prefix means that if the file lock doesn't exist,
     * no exception will be thrown,
     *
     * @throws FileSystemLockException if the file lock exists and removal of
     * the lock file failed
     * @throws IllegalMonitorStateException if the current thread doesn't own
     * the lock
     */
    public void releaseLockQuietly() throws FileSystemLockException  {
        try {
            releaseLock();
        } catch(FileSystemLockException e) {
            if(!isLockNotAcquiredException(e)) {
                throw e;
            }
        }
    }


    /**
     * This method is called when you want to extend the duration of the lock
     * setting the last modification date to the current time.
     * Only the thread who acquires the lock is enabled to renew it.
     *
     * @throws FileSystemLockException if the lock hasn't been acquired yet or if
     * the current thread doesn't hold the lock
     *
     */
    public void renewLock() throws FileSystemLockException {
        if(isHeldByCurrentThread()) {
            if(isLockExpired()) {
                throw new FileSystemLockException(RENEWING_EXPIRED_LOCK_ERROR_MSG);
            }
            getLockFile().setLastModified(System.currentTimeMillis());
        } else {
            throw new IllegalMonitorStateException(THREAD_OWNERSHIP_ERROR_MSG);
        }
    }


    /**
     *
     * @return true if the lock has been expired and it's no longer valid.
     * @throws FileSystemLockException if the lock hasn't been acquired yet
     */
    public boolean isLockExpired() throws FileSystemLockException {
        if(!getLockFile().exists()) {
            throw new FileSystemLockException(LOCK_NOT_ACQUIRED_ERROR_MSG);
        }

        long lastModificationTime = getLockFile().lastModified();
        long now                  = System.currentTimeMillis();

        return (lastModificationTime > 0) &&
               ( lastModificationTime+expirationTime < now);
    }


    /**
     *
     * @return the LockInfo bean bound to this lock
     */
    public LockInfo getLockInfo() {
        return new LockInfo();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("lock directory",directory);
        builder.append("lock name",lockFileName);
        builder.append("expiration time",expirationTime);
        builder.append("lock info", new LockInfo());
        return builder.toString();
    }

    /**
     * @return the current lock file.
     */
    public File getLockFile() {
        return lockFile;
    }

    /**
     *
     * @return the name that must be used to create the lock file
     */
    public String getLockFileName() {
        return lockFileName;
    }

    /**
     *
     * @return the name of the lock (without the default extension)
     */
    public String getLockName() {
        return lockName;
    }

    /**
     * @return the next expiration time (i.e. the time when the file is touched plus
     * the expiration time) for this lock. It may be 0 in the case there's
     * no lock file
     */
    
    public long getNextExpirationTime() {
        if(getLockFile().exists()) {
            return getLastModified()+expirationTime;
        } else {
            return 0;
        }
    }

    /**
     * @return the lastModified time of the lock file. It may be 0 in the case
     * there's no lock file
     */

    public long getLastModified() {
        return getLockFile().lastModified();
    }

    //-------------------------------------------------------- Protected Methods

    /**
     * Perform all the stuff needed when a lock file is really created on the
     * file system
     */
    protected void lockFileCreated() throws FileSystemLockException {
        new LockInfo().write(getLockFile());
    }

    // --------------------------------------------------------- Private Methods


    /**
     * acquires the lock creating a new file atomically on the file system
     * @return true if the lock is successfully acquired, false otherwise
     * @throws FileSystemLockException if any error occurs acquiring the resource
     * on the file system or persisting the lock information on it
     */
    private boolean acquireLockOnFileSystem() throws FileSystemLockException {
        try {
            if (getLockFile().createNewFile()) {
                lockFileCreated();
                 return true;
            } else if(isHeldByCurrentThreadQuietly()&& !isLockExpiredQuietly()) {
                renewLock();
                return true;
            }
        } catch(IOException e) {
            throw new FileSystemLockException( ACQUIRING_LOCK_ERR_MSG, e);
        }
        return false;
    }

    /**
     * @return true if the lock was expired and successfully removed, false otherwise
     * @throws FileSystemLockException if the lock hasn't been acquired yet
     */
    
    private boolean removeExpiredLock() throws FileSystemLockException {
        if(isLockExpiredQuietly()) {
            return deleteLockFile();
        }
        return false;
    }

    /**
     * @param e the exception we want to check
     * @return true if the exception has been thrown since the lock wasn't yet
     * acquired (the file lock didn't exist)
     */

    private boolean isLockNotAcquiredException(FileSystemLockException e) {
        return LOCK_NOT_ACQUIRED_ERROR_MSG.equals(e.getMessage());
    }


    /**
     * attempts to delete the lock file. Since deletion may be forbidden by other
     * processes accessing the lock in order to extract lock information, the delation
     * it's a weakness of this lock and we try to perform different attempts to
     * delete the file before returning false for the whole operation.
     *
     * @return true if the deletion succeded, false otherwise.
     */
    private boolean deleteLockFile() {
        // since some other threads may attempt to read lock information,
        // it may prevent the owner from releasing it deleting the lock file
        // so, before raising an error upon deletion failure, we try to delete
        // the file again. It's a weakness of this lock but thanks to expiration
        // we may assume that it's just a matter of time before the lock expires
        // even if not released
        for (int i = 0; i < MAX_NUMBER_OF_RELEASE_LOCK_ATTEMPTS; i++) {
            if (!getLockFile().exists() || getLockFile().delete()) {
                return true;
            } else {
                try {
                    Thread.sleep(randomGenerator.nextInt(SLEEP_TIME_ON_DELETION_FAILS));
                } catch(InterruptedException e) {

                }
            }
        }
        return false;
    }


    /**
     * It's a class containing information about the lock using to understand
     * if the lock is own by a particular thread running in a particular jvm
     * Information hold in this bean are persisted on the lock file and read when
     * we need to check which thread holds the lock
     */
    public class LockInfo {
        private String threadName;
        private String threadHash;
        private String seed;

        //--------------------------------------------------------- Constructors
        /**
         * Builds a LockInfo bean for the given  FileSystemLock
         */
        private LockInfo() {
            this.threadName = Thread.currentThread().getName();
            this.threadHash = ""+Thread.currentThread().hashCode();
            this.seed       = FileSystemLock.seed;
        }

        /**
         * @param file it's the file where the lock information have been persisted
         */
        private LockInfo(File file) throws FileSystemLockException {
            buildFrom(file);
        }

        //------------------------------------------------------- Public Methods
      
        @Override
        public boolean equals(Object obj) {
            if(obj != null && obj instanceof LockInfo) {
                LockInfo other = (LockInfo) obj;
                /*
                if(logger.isTraceEnabled()) {
                    logger.trace("Comparing "+this+" and "+other);
                }
                 * 
                 */
                return areStringEquals(this.getThreadName(), other.getThreadName()) &&
                       areStringEquals(this.getThreadHash(), other.getThreadHash()) &&
                       areStringEquals(this.getSeed(), other.getSeed());
            }
            return false;
        }



        @Override
        public String toString() {
            ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("thread hash", this.getThreadHash());
            builder.append("thread name", this.getThreadName());
            builder.append("seed", this.getSeed());
            return builder.toString();
        }

        /**
         * @return the threadName
         */
        public String getThreadName() {
            return threadName;
        }

        /**
         * @return the threadHash
         */
        public String getThreadHash() {
            return threadHash;
        }

        /**
         * @return the seed
         */
        public String getSeed() {
            return seed;
        }



        //------------------------------------------------------ Private Methods

        /**
         *
         * @param file the file object we want to check
         * @return true if the file object exists and rapresents a file
         */
        private boolean checkFile(File file) {
            return file.exists() && file.isFile();
        }

        /**
         * facility method used to restore the LockInfo from a persisted
         * rapresentation
         * @param file is the lock file containing the lock information
         * @throws FileSystemLockException if the parsing of the information fails
         */
        private void buildFrom(File file) throws FileSystemLockException {
            if(checkFile(file)) {
                FileInputStream in     = null;
                Properties      source = null;
                try {
                    in = new FileInputStream(file);
                    source = new Properties();
                    source.load(in);
                    this.threadName = source.getProperty(THREAD_NAME_KEY);
                    this.threadHash = source.getProperty(THREAD_HASH_KEY);
                    this.seed       = source.getProperty(SEED_KEY);
                } catch(IOException e) {
                    throw new FileSystemLockException("An error occurred reading the lock information from the lock file.", e);
                } finally {
                    source = null;
                    if(in!=null) {
                        try {
                            in.close();
                        } catch(IOException e) {
                            //logger.error("An error occurred closing the stream used to read lock information '"+e.getMessage()+"'.");
                        }
                        in = null;
                    }
                }
            }
        }

        /**
         * check if the incoming strings are equals
         * @param first the first string to match
         * @param second the second string to match
         * @return true if both input strings are not null or if they are equals
         */
        private boolean areStringEquals(String first, String second) {
            if(first!=null) {
                if(second!=null) {
                    return first.equals(second);
                } else {
                    return false;
                }
            }
            return second == null;
        }

          /**
         * persist this bean on the given lock file
         * @param file the file where the lock information are stored
         * @throws FileSystemLockException if any error occurs persisting the lock
         * information
         */
        private void write(File file) throws FileSystemLockException {
            write(file, extraLockInfo);

        }

        /**
         * persist this bean on the given lock file appending the following
         * extra information
         * @param file the file where the lock information will be stored
         * @param extraData a map containing extra information
         * @throws FileSystemLockException if any error occurs persisting the lock
         * information
         */
        private void write(File file, Map<String,String> extraData) throws FileSystemLockException {
            if(checkFile(file)) {
                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(file);
                    Properties       properties = new Properties();
                    properties.put(THREAD_NAME_KEY, getThreadName());
                    properties.put(THREAD_HASH_KEY, getThreadHash());
                    properties.put(SEED_KEY, getSeed());
                    if(extraData!=null && !extraData.isEmpty()) {
                        properties.putAll(extraData);
                    }
                    properties.store(out, getSeed());
                }catch(IOException e) {
                    throw new FileSystemLockException("An error occurred persisting the lock information on the lock file.", e);
                } finally {
                    if(out!=null) {
                        try {
                            out.flush();
                            out.close();
                            out = null;
                        } catch(IOException e) {
                            //logger.error("An error occurred closing the stream used to persist lock information '"+e.getMessage()+"'.");
                        }
                    }
                }
            }
        }

    }
}
