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
import java.io.IOException;

import junit.framework.TestCase;

/**
 * Test cases for CleanUpEventHandler class.
 * 
 * @version $Id$
 */
public class CleanUpEventHandlerTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private final static String RESOURCES_DIRECTORY_NAME =
        "src/test/data/com/funambol/server/cleanup";

    private final static String TEST_DIRECTORY_NAME =
        RESOURCES_DIRECTORY_NAME+"/directory";

    private final static String TEST_FILE_NAME =
        RESOURCES_DIRECTORY_NAME+"/file.txt";

    CleanUpEventHandler instance      = null;
    File                testFile      = null;
    File                testDirectory = null;

    // ------------------------------------------------------------ Constructors
    public CleanUpEventHandlerTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        instance = new CleanUpEventHandler(null);
        testFile = new File(TEST_FILE_NAME);
        testDirectory = new File(TEST_DIRECTORY_NAME);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(testFile.exists()) {
            assertTrue("Unable to delete test file", testFile.delete());
        }
    }

    // -------------------------------------------------------------- Test cases
    public void testDirectoryAdded_NullDirectory() {
        try {
            instance.directoryAdded(null, null);
            fail("Exception expected");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to handle the adding of a null directory.",
                         e.getMessage());
        }
    }

    public void testDirectoryAdded_File() {

        createFile(testFile);

        instance.directoryAdded(null, testFile);

        assertTrue("Expected the file to still exist",testFile.exists());

    }

    public void testDirectoryAdded_Directory() {

        createDirectory(testDirectory);

        instance.directoryAdded(null, testDirectory);

        assertFalse("Expected the directory not to exists any more",testDirectory.exists());

    }

    public void testFileAdded_NullFile() {
        try {
            instance.fileAdded(null, null, 0L);
            fail("Exception expected");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to handle the adding of a null file.",
                         e.getMessage());

        }
    }

    public void testFileAdded_File() {

        createFile(testFile);

        instance.fileAdded(null, testFile,0L);

        assertFalse("Expected the file not to exist any more",testFile.exists());

    }

    public void testDirectorySkipped_NullDirectory() {
        try {
            instance.directorySkipped(null, null);
            fail("Exception expected");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to handle the skipping of a null directory.",
                         e.getMessage());
        }
    }

    public void testFileSkipped_Null() {
        try {
            instance.fileSkipped(null, null);
            fail("Exception expected");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to handle the skipping of a null file.",
                         e.getMessage());
        }
    }

    public void testFileSkipped_RecentFile() {

        createFile(testFile);

        instance.fileSkipped(null, testFile);

        assertTrue("Expected the file still exists",testFile.exists());

    }

    public void testFileSkipped_FileOlderThanOneOur() {

        createFile(testFile);

        assertTrue("Unable to set last modified time for the file",
            testFile.setLastModified(System.currentTimeMillis()-((1*60*60*1000)+5000)));

        instance.fileSkipped(null, testFile);

        assertFalse("Expect the file not to exist any more",testFile.exists());
    }

    //---------------------------------------------------------- Private Methods

    private void createFile(File file) {
        assertNotNull("Expected not null file to create",file);

        if(file.exists()) {
            assertTrue("Existing file system resource is not a file",file.isFile());
        } else {
            try {
                assertTrue("Error creating file",file.createNewFile());
            } catch(IOException e) {
                e.printStackTrace();
                fail("Error creating a file '"+e.getMessage()+"'.");
            }
        }
    }

    private void createDirectory(File file) {
        assertNotNull("Expected not null directory to create",file);
        if(file.exists()) {
            assertTrue("Existing file system resource is not a directory",file.isDirectory());
        } else {
            assertTrue("Error creating directory",file.mkdirs());
        }
    }

}
