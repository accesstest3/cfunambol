/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import junit.framework.TestCase;

/**
 * Junit test for the IOTools class.
 *
 * @version $Id$
 */
public class IOToolsTest extends TestCase {

    //---------------------------------------------------------------- Constants
    static long average = 0;
    static long n       = 0;
    private final String ROOT_PATH =
            "target/test-classes/data/com/funambol/foundation/util/IOTools";
    private final String PICTURE_PATH =
            "/usr/local/Funambol/tools/tomcat/bin/../../../ds-server/db/picture";

    private final static String TEST_DIR =
                 "src/test/resources/data/com/funambol/framework/tools/IOTools";

    private final File PICTURE_BASE_DIR = new File(ROOT_PATH,PICTURE_PATH);

    //-------------------------------------------------------------- Constructor
    public IOToolsTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IOTools.removeDir(ROOT_PATH);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        IOTools.removeDir(ROOT_PATH);
    }


    //------------------------------------------------------------- test methods
    public void testCreateUserDirs_RaceConditions() throws Exception {
        int     failures         = 0;
        int     numberOfThreads  = 20;
        int     numberOfAttempts = 4;
        long[]  timeouts         = new long[]{3L,
                                              3L,
                                              3L,
                                              3L};

        final File[] paths   = new File[]{
            path2FileObject("db/picture/eg/ek/qx/ll/ob/hl/um/lb/am9obg=="),
            path2FileObject("db/picture/lh/yq/bv/fe/bw/ho/kj/ae/YWxpY2U="),
            path2FileObject("db/picture/uq/rf/xo/fu/um/dq/ki/le/dGVzdA=="),
            path2FileObject("db/picture/wx/dy/fk/rb/ro/ci/kh/vv/dXNlcg=="),
            path2FileObject("db/picture/de/kj/qs/bg/qn/kn/xv/jr/Z3Vlc3Q=")
        };


        List<DirectoryCreatorTask> runnables = new ArrayList<DirectoryCreatorTask>();
        List<Thread>               threads   = new ArrayList<Thread>();


        for(int j=0;j<10;j++) {
            long timeout  = timeouts[j%timeouts.length];
            TimeUnit unit = timeout>10?TimeUnit.MILLISECONDS:TimeUnit.SECONDS;
            n=0;
            average=0;
            runnables.clear();
            threads.clear();
            IOTools.removeDir(ROOT_PATH);
            for(int i=0;i<numberOfThreads;i++) {
                DirectoryCreatorTask r = new DirectoryCreatorTask(paths,
                                                                  numberOfAttempts,
                                                                  timeout,
                                                                  unit);
                runnables.add(r);
                Thread               t = new Thread(r);
                threads.add(t);
                t.start();
            }

            for(Thread t:threads) {
                t.join();
            }


            for(DirectoryCreatorTask d:runnables) {
                failures+=d.getFailuresNumber();
            }

            System.out.println("On ["+n+"] attempts with timeout ["+timeout+"] average time is ["+(average/n)+"] ["+failures+"]");
        }

        if(failures>0) {
            fail("Directories creation when root contains dots failed ["+
                    failures+"] times.");
        }
    }
    public void testListFilesSortingByTime_NullDirectory() throws IOException {
        try {
            IOTools.listFilesSortingByTime(null);
            fail("IllegalArgumentException expected while listing a null directory");
        } catch(IllegalArgumentException e) {

        }
    }

     public void testListFilesSortingByTime_NotExistingDirectory() throws IOException {

        File testingDir = new File(TEST_DIR+"/dirs");

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }

        assertNull("Expecting null when listing a not existing directory",
                   IOTools.listFilesSortingByTime(testingDir));

    }


    public void testListFilesSortingByTime_ValidButEmptyDirectory() throws IOException {

        File testingDir = new File(TEST_DIR+"/dirs");

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }

        assertTrue("Error creating testing dir",testingDir.mkdirs());

        File[] result = IOTools.listFilesSortingByTime(testingDir);

        assertNotNull("Expected empty array when listing an empty directory",
                      result);

        assertEquals("Expected an empty array",0,result.length);

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }
    }

    public void testListFilesSortingByTime_File() throws IOException {

        File testingDir = new File(TEST_DIR+"/dir.txt");

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }

        assertTrue("Error creating testing file",testingDir.createNewFile());

        File[] result = IOTools.listFilesSortingByTime(testingDir);

        assertNull("Expected null array when listing a file",
                    result);

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }
    }
    
    public void testListFilesSortingByTime_ValidDirectory() throws IOException {

        File testingDir = new File(TEST_DIR+"/dirs");

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }

        String[] fileNames  = new String[]{"first",
                                           "second",
                                           "third.txt",
                                           "fourth",
                                           "fifth.txt",
                                           "sixth"};

        assertTrue("Error creating testing dir",testingDir.mkdirs());

        long now = System.currentTimeMillis();

        for(int i=0;i<fileNames.length;i++) {
            String fileName = fileNames[i];
            File   file     = new File(testingDir,fileName);
            if(fileName.contains(".")) {
                assertTrue("Error creating file '"+fileName+"'",file.createNewFile());
            } else {
                assertTrue("Error creating dir '"+fileName+"'.",file.mkdirs());
            }
            file.setLastModified(now - 25000 + i*3000);
        }


        File[] result       = IOTools.listFilesSortingByTime(testingDir);
        
        assertEquals("Array sizes mismatch",
                     fileNames.length,
                     result.length);
        for(int i=0;i<fileNames.length;i++) {
            assertEquals("Wrong file position",
                         fileNames[i],
                         result[i].getName());
        }

        if(testingDir.exists()) {
           assertTrue("An error occurred removing test folders",
                      IOTools.deleteDirectory(testingDir));
        }
    }


    //---------------------------------------------------------- Private Methods
    private File path2FileObject(String directory) throws IOException {
        String normalizedPath = PICTURE_BASE_DIR.getCanonicalPath();
        File normalized       = new File(normalizedPath,directory) ;
        return normalized;
    }

    //--------------------------------------------------------------------------
    class DirectoryCreatorTask implements Runnable {

        File[] paths              = null;
        int      failures         = 0;
        int      numberOfAttempts = 0;
        long     timeout          = 0;
        TimeUnit unit             = null;
        String   threadName       = null;

        public DirectoryCreatorTask(File [] paths,
                                    int numberOfAttempts,
                                    long timeout,
                                    TimeUnit unit) {
            this.paths            = paths;
            this.numberOfAttempts = numberOfAttempts;
            this.timeout          = timeout;
            this.unit             = unit;
        }

        public void run() {
            threadName = Thread.currentThread().getName();
            for(int i=0;i<numberOfAttempts;i++) {
                try {
                    File directoryToCreate = new File(paths[i%paths.length].getCanonicalPath());
                    long started = System.currentTimeMillis();

                    IOTools.createDirectoryOnRaceCondition(directoryToCreate,
                                                           timeout,
                                                           unit);

                    addElapsed(System.currentTimeMillis()-started);

                    /*System.out.println("["+threadName+
                                       "] creates ["+
                                       directoryToCreate.getAbsolutePath()+"]");
                     */
                } catch(Throwable e) {
                    e.printStackTrace();
                    failures++;
                }
            }
        }

        public int getFailuresNumber() {
            return failures;
        }


        public boolean hasFailures() {
            return failures>0;
        }

        private synchronized void addElapsed(long elapsed) {
            n++;
            average+=elapsed;
        }

   }

}
