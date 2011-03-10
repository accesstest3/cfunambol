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
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 *
 * This class may be used to test how lock works when a lock is shared among
 * different process.
 * In order to achieve this goal, this main should be launched in different
 * java instances sharing the same current directory.
 * In this way, all those processes will use the same file as lock.
 * A lock file called lock.lck will be created, touched and removed by all those
 * threads in order to synchronize different accesses to the critical section.
 * A log file named log.txt will contain data about the execution of the processes.
 * First of all, you're supposed to cleanup the test directory removing any .cs,
 *  .lck or failures.txt files. Optionally, you may want to remove log.txt file 
 * too even if log data will be appended to it.
 * The test works as follows, you are requested to launch at least two different
 * instances of this main:
 * - On linux environment:
 * java -classpath "core-framework-9.0.0-SNAPSHOT.jar:."  com.funambol.framework.tools.FileSystemLockTestMain
 * - On windows environment:
 * java  -classpath "core-framework-9.0.0-SNAPSHOT.jar;."  com.funambol.framework.tools.FileSystemLockTestMain
 * Each process has a unique name obtained using the timestamp the process was started.
 * Once started, a process tries to acquire a lock, accessing the critical section.
 * Inside the critical section, the process will create a unique_process_id.cs
 * file in order to mark its access and it will sleep for a while.
 * Other processes should check that they are rejected by the critical section.
 * They check that only one .cs file has been created in the directory.
 * Before exiting the critical section, the process who holds the lock, deletes
 * the .cs files and release the lock.
 * At the end, non faiulurs.txt files should be found in the current directory,
 * otherwise the test is failed.
 * 
 * @version $Id$
 */
public class FileSystemLockTestMain {
     //---------------------------------------------------------------- Constants
    public static final int NUMBER_OF_TRIALS                     = 200;
    public static final int TIME_TO_REST_EACH_ROUND              = 1*1000;
    public static final int TIME_TO_REST_INSIDE_CRITICAL_SECTION = 2*1000;
    public static final Random randomGenerator                   =
                                         new Random(System.currentTimeMillis());
    public static final String CURRENT_DIRECTORY = ".";
    public static final int LOCK_EXPIRATION_TIME = 1 * 60 * 1000;
    public static final String LOCK_NAME = "lock";


    
    private static PrintWriter      logWriter;
    private static PrintWriter      failWriter;
    private final static long started = System.currentTimeMillis();
    public static final String FILE_MARKER_EXTENSION = ".cs";
    private final static File marker  = new File(""+started+FILE_MARKER_EXTENSION);


    //-------------------------------------------------------------- Main Method

    /**
     * the main method who simulates the continous access to enter the critical
     * section
     * @param args command line args (not handled yet)
     */
    public static void main(String[] args) {
        
        init();

        FileSystemLock lock = new FileSystemLock(new File(CURRENT_DIRECTORY), 
                                                 LOCK_NAME,
                                                 LOCK_EXPIRATION_TIME);
        for(int i=0;i<NUMBER_OF_TRIALS;i++) {
            try {
                run(i,lock);
            }catch(Throwable t) {
                  fail("An error occurred running '"+i+"'",t);
            }
        }

        exit(lock);
     
    }

    /**
     * tries to acquire the lock for the round with the given number, if it succeed
     * a marker is created.
     * Then the thread rest for a while and leaves the critical section deleting
     * the file marker and releasing the lock.
     * If the thread din't succeed in accessing the critical section, then it checks
     * if at most one marker exists, if the check failed the test is failed
     *
     * @param round the round we're running currently
     * @param lock the file system lock
     * @throws Throwable if any error occurs
     */
    private static void run(int round,FileSystemLock lock) throws Throwable {
        if(lock.acquireLock()) {
            if(!lock.isHeldByCurrentThread()) {
                log("Failed: this thread should be the owner of the lock");
            }
            log("Entering critical section in round '"+round+"'");
            mark();
            rest(TIME_TO_REST_INSIDE_CRITICAL_SECTION);
            log("Leaving critical section in round '"+round+"'");
            unmark();
            releaseLock(lock);
            rest(2000L);
        } else {
            log("Rejected from critical section");
            if(lock.isHeldByCurrentThreadQuietly()) {
                log("Failed: this thread shouldn't be the owner of the lock");
            }
            checkMarkers();
        }
        rest(200);
    }

    private static void init() {
        try {
            logWriter = new PrintWriter(new FileOutputStream("log.txt",true));
        } catch(Throwable t) {
            log("Error creating the writer.",t);
        }
    }

    private static void exit(FileSystemLock lock) {
        for(PrintWriter writer:new PrintWriter[]{failWriter,logWriter}) {
            if(writer!=null) {
                writer.flush();
                writer.close();
            }
        }
    }


    private static void releaseLock(FileSystemLock lock) { 
        if(lock!=null) {
            try {
                lock.releaseLockQuietly();
            } catch(Throwable t) {
                log("Error releasing lock",t);
            }
        }
    }



    /**
     * log the given message on the failure file shared among different instances
     * with the purpose to make the test fail.
     * Any message logged using this method will cause the test to fail since it will be
     * logged on the failure file.
     * If the proper writer is null then the System.err is used.
     *
     * @param message the message to be logged
     */

    private static void fail(String message) {
        try {
            createFailureWriter();
        } catch(IOException e) {
            log("FAILED: unable to log failure message.",e);
            log(message);
        }
        StringBuilder builder = createMessageBuilder(message);
        if(message!=null) {
            if(failWriter!=null ) {
                failWriter.println(builder.toString());
                failWriter.flush();
            } else {
                System.err.println(builder.toString());
            }
        }
    }


    /**
     * log the given message and the given exception on the failure file shared
     * among different instances with the purpose to make the test fail.
     * Any message logged using this method will cause the test to fail since it will be
     * logged on the failure file.
     * If the proper writer is null then the System.err is used.
     *
     * @param message the message to be logged
     * @param e the exception to be logged
     */
    private final static void fail(String message, Throwable e) {
        fail(message);
        if(e!=null) {
            if(failWriter!=null) {
                e.printStackTrace(failWriter);
                failWriter.flush();
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * log the given message on the log file shared among different instances.
     * Even if we log an error, it don't cause the test to fail since it will be
     * logged on the log file and not into the failure file (use the fail method
     * otherwise)
     * If the proper writer is null then the System.out is used.
     *
     * @param message the message to be logged
     */
    private static void log(String message) {
        StringBuilder builder = createMessageBuilder(message);
        if(message!=null) {
            if(logWriter!=null ) {
                logWriter.println(builder.toString());
                logWriter.flush();
            } else {
                System.out.println(builder.toString());
            }
        }
    }

    /**
     * log the given message and the stack trace of the exception on the log file
     * shared among different instances.
     * Even if we log an error, it don't cause the test to fail since it will be
     * logged on the log file and not into the failure file (use the fail method
     * otherwise)
     * If the proper writer is null then the System.out is used.
     *
     * @param message the message to be logged
     * @param e the exception to be logged
     */
    private final static void log(String message, Throwable e) {
        log(message);
        if(e!=null) {
            if(logWriter!=null) {
                e.printStackTrace(logWriter);
                logWriter.flush();
            } else {
                e.printStackTrace();
            }
        }
    }

    /**
     * creates the message that will be logged using the thread name, the timestamp
     * the process was launched, and the provided message itself
     * @param message the message to be logged
     * @return the StringBuilder object holding the message to be logged
     */
    private static StringBuilder createMessageBuilder(String message) {
        StringBuilder builder = new StringBuilder(""+System.currentTimeMillis());
        builder.append(" - ");
        builder.append(Thread.currentThread().getName());
        builder.append("_");
        builder.append(Long.toString(started));
        builder.append(" - ");
        builder.append(message);
        return builder;
    }

    private static void createFailureWriter() throws IOException {
        if(failWriter == null) {
            failWriter = new PrintWriter(new FileOutputStream("failures.txt",true));
        }
    }

    /**
     * make the thread sleep for the given time expressed in ms.
     * calling this method you don't have to care about any exception raised while
     * the thread is sleeping.
     *
     * @param ms the time to sleep expressed in ms
     */
    private static void rest(long ms) {
        try {
            Thread.sleep(ms);
        } catch(Throwable t) {
            log("Thread waiting interrupted",t);
        }
    }

    /**
     * mark the entering of this process in the critical section creating a
     * .cs file whose name is obtained by the timestamp when this process has been
     * started
     *
     * @throws RuntimeException if the creation of the marker fails
     */
    private static void mark() {
        try {
            if(!marker.createNewFile()) {
                throw new RuntimeException("Marker creation failed!");
            }
        } catch(Throwable e) {
            fail("Marker creation fails.",e);
            throw new RuntimeException("Unable to mark critical section",e);
        }
    }

    /**
     * unmark the entering of this process in the critical section deleting any
     * .cs file whose name is obtained by the timestamp when this process has been
     * started.
     *
     * @throws RuntimeException if the deletion of the marker fails
     *
     */
    private static void unmark() {
        try {
            if(!marker.delete()) {
             throw new RuntimeException("Marker deletion failed!");
            }
        } catch(Throwable e) {
            fail("Marker deletion fails.",e);
            throw new RuntimeException("Unable to unmark critical section",e);
        }
    }

    /**
     * this method allows to check that you can find at the most one marker
     * for a process who succeded in enter the critical section
     */
    private static void checkMarkers() {
        File dir = new File(CURRENT_DIRECTORY);
        String[] names =dir.list(new FilenameFilter() {

            public boolean accept(File dir, String name) {
                return name!=null && name.endsWith(FILE_MARKER_EXTENSION);
            }
        });
        if(names!=null && names.length>1) {
            fail("FAILED: more than one markers found in critical section");
         }
    }

}
