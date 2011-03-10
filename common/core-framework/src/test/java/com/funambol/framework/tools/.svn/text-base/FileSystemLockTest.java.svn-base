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
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Random;
import java.util.Set;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 *
 * @version $Id$
 */
public class FileSystemLockTest extends TestCase {
    public static final int TIME_TO_TOUCH_A_FILE                  = 100;
    public static final int DEFAULT_EXPIRATION_TIME               = 2500;
    public static final long TIME_TO_REST_FOR_LOCK_EXPIRATION     = 3000L;
    public static final long TIME_TO_REST_WITHOUT_LOCK_EXPIRATION = 1000L;
    public static final String LOCK_COMPLETE_FILENAME             = "lock.lck";
    public static final String LOCK_DIRECTORY                     = ".";
    public static final String LOCK_NAME                          = "lock";
    
    
    // It's the seed used by the FileSystemLock class
    final private String seed = getSeed();

    final File directory      = new File(LOCK_DIRECTORY);
    final String lockFileName = LOCK_COMPLETE_FILENAME;
    final File lockFile  = new File(directory,lockFileName);
        


    public FileSystemLockTest(String testName) {
        super(testName);
    }




    @Override
    protected void setUp() throws Exception {
        super.setUp();

        restoreSeed();

        removeLock();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    public void testAcquireLock_AcquirableLock_SingleThread() throws FileSystemLockException {
        // testing when the lock is available to be acquired and we have only
        // one threat trying
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testAcquireLock_AlreadyAcquiredLock_SingleThread() throws FileSystemLockException {
        // testing that each time the owner of a lock tries to acquire it,
        // it renew the lock moving forward the expiration time
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        assertTrue("Lock should be acquired",lock.acquireLock());

        long originalNextExpirationTime = lock.getNextExpirationTime();

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        rest(TIME_TO_REST_WITHOUT_LOCK_EXPIRATION);

        assertTrue("Lock should be acquired",lock.acquireLock());

        long newNextExpirationTime = lock.getNextExpirationTime();

        assertTrue("Next expiration time modified by different accesses",
                     newNextExpirationTime>originalNextExpirationTime);


        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }


    public void testAcquireLock_UnacquirableLock_DifferentThreads() throws FileSystemLockException {
        // testing when the lock is not available to be acquired and we have a
        // second thread trying
        final FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        Thread t = acquireLockOtherThread(lock);

        long nextExpirationTime = lock.getNextExpirationTime();

        assertLockInfoEquals(lockFile, seed, t);

        assertFalse("Lock shouldn't be acquired",lock.acquireLock());

        long newNextExpirationTime = lock.getNextExpirationTime();

        assertEquals("Expiration time shouldn't be moved when reading lock info",
                     nextExpirationTime,
                     newNextExpirationTime);

        

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testAcquireLock_UnacquirableLock_DifferentProcesses() throws FileSystemLockException {
        // testing when the lock is not available to be acquired and we have a
        // second thread (different seed) trying
        final FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);
        
        setSeed("notValideSeed");

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile, "notValideSeed", Thread.currentThread());

        assertTrue("Wrong lock owner",lock.isHeldByCurrentThread());

        restoreSeed();

        assertFalse("Wrong lock owner",lock.isHeldByCurrentThread());
        
        assertFalse("Lock shouldn't be acquired",lock.acquireLock());

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testAcquireLock_UnacquirableLock_SingleThread() throws IOException, FileSystemLockException {
        // testing when a lock file exists but it doesn't contain lock information
        assertTrue("Unable to create fake lock file",lockFile.createNewFile());
        assertTrue("Lock file doesn't exist after fake creation",lockFile.exists());

        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        assertFalse("Lock shouldn't be acquired",lock.acquireLock());
        
        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());

    }

     public void testFileSystemLockUsage_AcquirableLock_MultipleThread() throws FileSystemLockException {
        // test the acquisition of a lock when many threads are fighting for it
        // test parameters
        int numberOfThreads = 5;
        int numberOfRounds  = 10;
        
        final Random random = new Random(System.currentTimeMillis());

        final FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        Thread[] threads = new Thread[numberOfThreads];

        final Set<String> failures 
                           = Collections.synchronizedSet(new HashSet<String>());

        for(int j=0;j<numberOfRounds && failures.isEmpty();j++) {
            System.out.println("Round "+j+" started");
            final GameKeeper keeper = new GameKeeper();
            
            for(int i=0;i<numberOfThreads;i++) {
                threads[i] = new Thread(new Runnable() {

                    public void run() {
                            String threadName = Thread.currentThread().getName();
                            boolean lockAcquired = false;
                            rest(random.nextInt(1100)+500);
                            do {
                                try {
                                    lockAcquired = lock.acquireLock();
                                } catch(FileSystemLockException e) {
                                    failures.add("Error acquiring lock "+threadName+" '"+e.getMessage()+"'.");
                                }

                        
                                if(lockAcquired) {
                                    // mark the round as won
                                    if(!keeper.iAmTheWinner()) {
                                        failures.add("Thread "+threadName+" entered when another thread was inside.");
                                    }

                                    // checking whether lock data are written correctly
                                    try {
                                        assertLockInfoEquals(lockFile, seed, Thread.currentThread());
                                    } catch(Throwable e) {
                                        failures.add(e.getMessage());
                                    }

                                    // rest for a while within a critical section
                                    rest(TIME_TO_REST_WITHOUT_LOCK_EXPIRATION);

                                    // make the round over
                                    keeper.gameOver();

                                    rest(500);

                                    try {
                                        System.out.println("Thread "+threadName+ " releasing lock");
                                        lock.releaseLock();

                                        assertFalse("Lock file still exists",lockFile.exists());

                                    } catch(Throwable e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    rest(500);
                                    try {
                                        assertFalse("Wrong thread owner",lock.isHeldByCurrentThreadQuietly());
                                    }  catch(Throwable e) {
                                        failures.add("Errror checking lock owner, thread "+threadName+", '"+e.getMessage()+"'.");
                                    }
                                }

                            } while(!keeper.isGameOver() && failures.isEmpty());
                        }
                });

                threads[i].start();
            }
            // waiting all threads for ending
            for(int i=0;i<numberOfThreads;i++) {
                try {
                    threads[i].join();
                } catch(InterruptedException e) {
                    
                }
            }
            System.out.println("Round "+j+ " ended and the winner is '"+keeper.getTheWinner());
        }

        // the test has failed!!!
       checkFailures(failures, "Multithread test failed!");

        assertTrue("Lock should be acquired",lock.acquireLock());
        assertTrue("Lock file doesn't exist after lock has been acquired",lockFile.exists());
        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testReleaseLock_ExistingEmptyLockFile() throws IOException, FileSystemLockException {
        // testing the release of the lock when the lock file exists but it's
        // empty, no lock information are stored in it
        assertTrue("Unable to create fake lock file",lockFile.createNewFile());
        assertTrue("Lock file doesn't exist after fake creation",lockFile.exists());

        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        assertFalse("Lock shouldn't be acquired",lock.acquireLock());
        try {
            lock.releaseLock();
            fail("Unacquired lock released");
        } catch(IllegalMonitorStateException e) {
            assertEquals("Wrong IllegalMonitorStateException",
                          "The current thread doesn't hold this lock.",
                          e.getMessage());
        }

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }


    public void testReleaseLock_AcquirableLock_SingleThread() throws FileSystemLockException {
        // testing when the lock is available to be released and we have only
        // one threat trying
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());
        
        assertTrue("Lock file doesn't exist after acquiring it",lockFile.exists());
        assertTrue("Wrong lock information stored",lock.isHeldByCurrentThread());


        lock.releaseLock();

        assertFalse("Lock file still existing after releasing",lockFile.exists());
    }

    public void testReleaseLock_ExpiredLock_SingleThread() throws FileSystemLockException {
        // testing when the lock is available to be released and we have only
        // one threat trying
        final FileSystemLock lock = new FileSystemLock(directory,
                                                       LOCK_NAME,
                                                       DEFAULT_EXPIRATION_TIME);

        Thread t = acquireLockOtherThread(lock);

        assertLockInfoEquals(lockFile,seed,t);

        assertFalse("Wrong lock owner",lock.isHeldByCurrentThread());

        try {
            lock.releaseLock();
        } catch(IllegalMonitorStateException e) {
            assertEquals("Wrong exception message",
                         FileSystemLock.THREAD_OWNERSHIP_ERROR_MSG,
                         e.getMessage());
        }

        rest(TIME_TO_REST_FOR_LOCK_EXPIRATION);

        lock.releaseLock();

        assertFalse("Lock file exists after releasing it",lockFile.exists());
    }


    public void testReleaseLock_DifferentThreads() throws IOException, FileSystemLockException {
        // testing the release of the lock when the lock file is owned by another
        // thread
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);
        
        acquireLockOtherThread(lock);

        assertFalse("Lock shouldn't be acquired",lock.acquireLock());
        try {
            lock.releaseLock();
            fail("Unacquired lock released");
        } catch(IllegalMonitorStateException e) {
            assertEquals("Wrong IllegalMonitorStateException",
                          "The current thread doesn't hold this lock.",
                          e.getMessage());
        }

        assertTrue("Unable to remove existing lock file",lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }



    public void testIsHeldByCurrentThread_NotAcquiredLock() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        try {
            lock.isHeldByCurrentThread();
            fail("Exception expected while releasing a not acquired lock");
        } catch(FileSystemLockException e) {
            assertEquals("wrong exception message",
                         "This lock is not acquired by any thread.",
                         e.getMessage());

        }
    }

    public void testIsHeldByCurrentThread_AcquiredLock_SameThread() throws FileSystemLockException {
         FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,500);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        assertTrue("Lock is hold by this thread",lock.isHeldByCurrentThread());
        
        lock.releaseLock();

        try {
            lock.isHeldByCurrentThread();
            fail("Exception expected while releasing a not acquired lock");
        } catch(FileSystemLockException e) {
            assertEquals("wrong exception message",
                         "This lock is not acquired by any thread.",
                         e.getMessage());

        }

        assertTrue("Unable to remove existing lock file",!lockFile.exists() || lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }


    public void testIsHeldByCurrentThread_AcquiredLock_DifferentThreads() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        Thread t = acquireLockOtherThread(lock);

        assertLockInfoEquals(lockFile,seed,t);

        assertFalse("Lock isn't hold by this thread",lock.isHeldByCurrentThread());

        assertTrue("Unable to remove existing lock file",!lockFile.exists() || lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testIsHeldByCurrentThread_AcquiredLock_DifferentProcesses() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,500);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        assertTrue("Lock is hold by this thread",lock.isHeldByCurrentThread());

        setSeed("notvalidseed");

        assertFalse("Lock is hold by this thread",lock.isHeldByCurrentThread());

        restoreSeed();

        assertTrue("Lock is hold by this thread",lock.isHeldByCurrentThread());

        lock.releaseLock();

        try {
            lock.isHeldByCurrentThread();
            fail("Exception expected while releasing a not acquired lock");
        } catch(FileSystemLockException e) {
            assertEquals("wrong exception message",
                         "This lock is not acquired by any thread.",
                         e.getMessage());

        }

        assertTrue("Unable to remove existing lock file",!lockFile.exists() || lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }



    public void testReleaseLock_notAcquiredLock() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME);

        try {
            lock.releaseLock();
            fail("Exception expected while releasing a not acquired lock");
        } catch(FileSystemLockException e) {
            assertEquals("wrong exception message",
                         "This lock is not acquired by any thread.",
                         e.getMessage());
        }
    }

    public void testLockExpired_NotAcquiredLock() throws FileSystemLockException {
       FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        try {
            lock.isLockExpired();
            fail("Expected exception when checking if a not acquired lock has been expired");
        } catch(FileSystemLockException e) {
            assertEquals("Wrong exception message",
                        FileSystemLock.LOCK_NOT_ACQUIRED_ERROR_MSG,
                         e.getMessage());
        }
    }

    public void testLockExpired_AcquiredLock_NotExpired() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());
        assertFalse("Lock expired soon",lock.isLockExpired());

        lock.releaseLock();
        
        try {
            lock.isLockExpired();
            fail("Expected exception when checking if a not acquired lock has been expired");
        } catch(FileSystemLockException e) {
            assertEquals("Wrong exception message",
                         FileSystemLock.LOCK_NOT_ACQUIRED_ERROR_MSG,
                         e.getMessage());
        }



        assertTrue("Unable to remove existing lock file",!lockFile.exists() || lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }


    public void testLockExpired_AcquiredLock_Expired() throws FileSystemLockException {

        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile,seed,Thread.currentThread());

        rest(TIME_TO_REST_FOR_LOCK_EXPIRATION);

        assertTrue("Lock doesn't expire",lock.isLockExpired());

        lock.releaseLock();

        try {
            lock.isLockExpired();
            fail("Expected exception when checking if a not acquired lock has been expired");
        } catch(FileSystemLockException e) {
            assertEquals("Wrong exception message",
                         FileSystemLock.LOCK_NOT_ACQUIRED_ERROR_MSG,
                         e.getMessage());
        }



        assertTrue("Unable to remove existing lock file",!lockFile.exists() || lockFile.delete());
        assertFalse("Lock file still existing after deletion",lockFile.exists());
    }

    public void testRenewLock_LockNotAcquired() {
      FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        try {
            lock.renewLock();
            fail("Expected exception when checking if a not acquired lock has been expired");
        } catch(FileSystemLockException e) {
            assertEquals("Wrong exception message",
                        FileSystemLock.LOCK_NOT_ACQUIRED_ERROR_MSG,
                         e.getMessage());
        }
    }

    public void testRenewLock_LockExpired() throws FileSystemLockException {
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,100);

        assertTrue("Unable to acquire am available lock",lock.acquireLock());

        assertLockInfoEquals(lockFile, seed, Thread.currentThread());

        rest(200);

        assertTrue("Expected an expired lock",lock.isLockExpired());

        try {
            lock.renewLock();
            fail("Expected exception when renewing an expired lock");
        } catch(FileSystemLockException e) {
            assertEquals("Wrong exception message",
                        FileSystemLock.RENEWING_EXPIRED_LOCK_ERROR_MSG,
                         e.getMessage());
        }
    }

    public void testRenewLock_LockRenewable() throws FileSystemLockException, Throwable {
        // test the renewing of a lock belonging to the thread
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        assertTrue("Unable to acquire am available lock",lock.acquireLock());

        long originalExpirationTime = lock.getLastModified();

        assertLockInfoEquals(lockFile, seed, Thread.currentThread());

        rest(TIME_TO_REST_WITHOUT_LOCK_EXPIRATION);

        lock.renewLock();

        long now = System.currentTimeMillis();

        // We added an epsilon (the touch time) since on some environments this operation
        // takes a while
        try {
            assertTrue("Wrong expitation time",originalExpirationTime<now &&
                                               ((lock.getNextExpirationTime()-now)<=DEFAULT_EXPIRATION_TIME+TIME_TO_TOUCH_A_FILE));
        } catch(Throwable e) {
            System.out.println("Test failed!!!");
            System.out.println("Now is '"+now+"'");
            System.out.println("Original expiration time is '"+originalExpirationTime+"'");
            System.out.println("Lock (current) next expiration time is '"+lock.getNextExpirationTime()+"'");
            System.out.println("Lock expiration time minus now is '"+(lock.getNextExpirationTime()-now)+"'");
            System.out.println("First check '"+(originalExpirationTime<now)+"'.");
            System.out.println("Second check '"+((lock.getNextExpirationTime()-now)<=DEFAULT_EXPIRATION_TIME+TIME_TO_TOUCH_A_FILE)+"'.");
            throw e;

        }
    }

    public void testRenewLock_LockUnrenewable_DifferentThreads() throws FileSystemLockException {
        // test the renewing of a lock belonging to the thread
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        Thread t = acquireLockOtherThread(lock);

        assertLockInfoEquals(lockFile, seed, t);

        rest(TIME_TO_REST_WITHOUT_LOCK_EXPIRATION);

        assertFalse("Expecting lock not to expire.",lock.isLockExpired());

        try {
            lock.renewLock();
            fail("Expected exception when renewing an expired lock");
        } catch(IllegalMonitorStateException e) {
            assertEquals("Wrong exception message",
                        FileSystemLock.THREAD_OWNERSHIP_ERROR_MSG,
                         e.getMessage());
        }

        rest(TIME_TO_REST_FOR_LOCK_EXPIRATION);

        assertTrue("Expecting the lock to expire",lock.isLockExpired());

    }

    public void testRenewLock_LockUnrenewable_DifferentProcesses() throws FileSystemLockException {
        // test the renewing of a lock belonging to the thread
        FileSystemLock lock = new FileSystemLock(directory, LOCK_NAME,DEFAULT_EXPIRATION_TIME);

        setSeed("notValideSeed");

        assertTrue("Lock should be acquired",lock.acquireLock());

        assertLockInfoEquals(lockFile, "notValideSeed", Thread.currentThread());

        assertTrue("Wrong lock owner",lock.isHeldByCurrentThread());

        restoreSeed();

        assertFalse("Wrong lock owner",lock.isHeldByCurrentThread());

        rest(TIME_TO_REST_WITHOUT_LOCK_EXPIRATION);

        assertFalse("Expecting lock not to expire.",lock.isLockExpired());

        try {
            lock.renewLock();
            fail("Expected exception when renewing an expired lock");
        } catch(IllegalMonitorStateException e) {
            assertEquals("Wrong exception message",
                        FileSystemLock.THREAD_OWNERSHIP_ERROR_MSG,
                         e.getMessage());
        }

        rest(TIME_TO_REST_FOR_LOCK_EXPIRATION);

        assertTrue("Expecting the lock to expire",lock.isLockExpired());

    }



    private void rest(long how) {
        try {
            Thread.sleep(how);
        } catch(InterruptedException e) {

        }
    }


    private void assertLockInfoEquals(File lockFile, String seed, Thread thread) {
        assertTrue("Lock file doesn't exist after lock has been acquired",lockFile.exists());
        Properties properties = new Properties();
        FileInputStream is = null;
        try {
            is = new FileInputStream(lockFile);
            properties.load(is);
            assertEquals("Wrong seed",seed,properties.get(FileSystemLock.SEED_KEY));
            assertEquals("Wrong thread name",thread.getName(),properties.get(FileSystemLock.THREAD_NAME_KEY));
            assertEquals("Wrong thread hash",""+thread.hashCode(),properties.get(FileSystemLock.THREAD_HASH_KEY));
        } catch(IOException e) {
            fail("Unable to read LockInfo properties frome file.");
        } finally {
            if(is!=null) {
                try {
                    is.close();
                } catch(IOException e) {
                    
                }
            }
            is = null;
        }
    }

    private String getSeed()  {
        try {
           return (String) PrivateAccessor.getField(FileSystemLock.class, "seed");
        } catch(NoSuchFieldException e) {
            return null;
        }
    }

    private Thread acquireLockOtherThread(final FileSystemLock lock) {
        final Set<String> failures = new HashSet<String>();
        Thread t = new Thread(new Runnable() {

            public void run() {
                try {
                    lock.acquireLock();

                    if(!lock.isHeldByCurrentThread()) {
                        failures.add("Lock isn't hold by the right thread.");
                    }

                } catch(Throwable e) {
                    failures.add(e.getMessage());
                }
            }
        });
        t.start();

        try {
            t.join();
        } catch(InterruptedException e) {
            
        }

        checkFailures(failures, "Error occurred acquiring the lock with another thread");

        return t;

    }

    private void checkFailures(Collection<String> failures, String errorMessage) {
        if(!failures.isEmpty()) {
            Iterator<String> failuresIterator = failures.iterator();
            while(failuresIterator.hasNext()) {
                String failure = failuresIterator.next();
                System.err.println(failure);
            }
            fail(errorMessage);
        }
    }

    private void restoreSeed() {
        setSeed(seed);
    }

    private void removeLock() {
       if(lockFile.exists() && lockFile.isFile()) {
           if(!lockFile.delete())  {
               System.err.println("Error deleting the lock file.");
           }
           if(lockFile.exists()) {
               System.err.println("Lock file still exists.");
           }
        }

    }

    private void setSeed(String newSeed) {
        try {
            PrivateAccessor.setField(FileSystemLock.class, 
                                     "seed",
                                     newSeed);
        } catch(Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * This object is responsible to rule the game for enter in critical section
     * among different thread
     */
    private class GameKeeper {

        boolean gameOver = false;
        String  theWinner   = null;

        public synchronized boolean isGameOver() {
            return gameOver;
        }

        public synchronized void gameOver() {
            this.gameOver = true;
        }

        public synchronized boolean iAmTheWinner() {
            if(theWinner==null) {
                theWinner = Thread.currentThread().getName();
                return true;
            } else {
                System.err.println("The game was already won by "+theWinner);
                return false;
            }
        }

        public String getTheWinner() {
                return theWinner;
        }

    }

}
