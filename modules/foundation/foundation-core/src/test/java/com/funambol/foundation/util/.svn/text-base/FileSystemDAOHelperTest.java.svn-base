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

package com.funambol.foundation.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import junit.framework.TestCase;

import junitx.util.PrivateAccessor;
import junitx.framework.ArrayAssert;

import org.apache.log4j.Level;

import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.IOTools;

/**
 * Test cases for FileSystemDAOHelper class.
 * @version $Id$
 */
public class FileSystemDAOHelperTest extends TestCase {

    // --------------------------------------------------------------- Constants

    public static final int FILE_SYSTEM_DIR_DEPTH_8 = 8;
    public static final int FILE_SYSTEM_DIR_DEPTH_0 = 0;
    public static final String FS = FileDataObjectNamingStrategy.FS;

    // ---------------------------------------------------------- Private Fileds

    private final String rootPath =
            "target/test-classes/data/com/funambol/foundation/items/dao/FileSystemDAOHelper";

    public FileSystemDAOHelperTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        IOTools.removeDir(rootPath+"/usr");
        FunambolLoggerFactory.getLogger(Def.LOGGER_NAME).setLevel(Level.TRACE);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        IOTools.removeDir(rootPath+"/usr");
    }

    // -------------------------------------------------------------- Test cases
    public void testConstructor_FileSysDirDepth_10_ThrowsIllegalArgumentException() throws Throwable {
        String userName = "test-user1";
        int fileSysDirDepth = 10;

        try {
            FileSystemDAOHelper item =
                    new FileSystemDAOHelper(userName, rootPath, fileSysDirDepth);
            fail("Should have thrown a IllegalArgumentException");
        } catch (IllegalArgumentException ex) {

        }
    }

    public void testCreateUserDirs_RootWithDots() throws Exception {
        int depth = 8;
        String parentDir = rootPath+"/usr/local/Funambol/tools/tomcat/bin/../../../ds-server/db/picture";
        String[] users = new String[]{"test",
                                      "user",
                                      "guest",
                                      "john",
                                      "alice"};

        int     failures = 0;
        for(int i=0;i<200;i++) {
            try {
               String user = users[i%users.length];
               FileSystemDAOHelper helper = new FileSystemDAOHelper(user,
                                                                    parentDir,
                                                                    depth);
               File folder = helper.getFolder();

               System.out.println("Created ["+folder.getAbsolutePath()+"]");

            } catch(IOException e) {
                e.printStackTrace();
                failures++;
            }
        }
        if(failures>0) {
            fail("Directories creation when root contains dots failed '"+failures+"' times.");
        }
    }

    public void testGetFileRelativePath() throws Exception {

        String userName = "test-user1";
        int fileSysDirDepth = 8;
        FileSystemDAOHelper item =
                new FileSystemDAOHelper(userName, rootPath, fileSysDirDepth);

        String filename = "seaside.jpg";
        String expectedFilePath = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==" + FS + filename;

        String filePath = item.getFileRelativePath(filename);

        assertEquals(expectedFilePath, filePath);
    }

    public void testGetEXTFolderRelativePath() throws Exception {

        String userName = "test-user1";
        int fileSysDirDepth = 8;
        FileSystemDAOHelper item =
                new FileSystemDAOHelper(userName, rootPath, fileSysDirDepth);

        String filename = "seaside.jpg";
        String expectedExtFolderPath = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==" + FS + filename +
                FileDataObjectNamingStrategy.EXT_FOLDER_SUFFIX;

        String extFolderPath = item.getEXTFolderRelativePath(filename);

        assertEquals(expectedExtFolderPath, extFolderPath);
    }

    public void testSaveTmpFile() throws Exception {

        String userName = "username1";
        FileSystemDAOHelper helper =
                new FileSystemDAOHelper(userName, rootPath, FILE_SYSTEM_DIR_DEPTH_0);

        byte[] inputContent = "test test test".getBytes("UTF-8");
        InputStream in = new ByteArrayInputStream(inputContent);
        
        File tmpFile = helper.saveTmpFile(in, inputContent.length);

        String tmpFileName = tmpFile.getName();

        byte[] savedContent = helper.readData(tmpFileName);

        ArrayAssert.assertEquals(inputContent, savedContent);
    }


    public void testGetUserFolderRelativePath_SpecialCharacters() throws Throwable {
        String expected = "dd/vs/eg/cf/gb/bl/jh/mx/dHxlIHM6dFx1KnM_ZSJyPDE+Mnwz";
        String userName = "t|e s:t\\u*s?e\"r<1>2|3";
        int fileSysDirDepth = 8;

        FileSystemDAOHelper helper =
                new FileSystemDAOHelper(userName, rootPath, fileSysDirDepth);

        String result = (String) PrivateAccessor.invoke(
            helper,
            "getUserFolderRelativePath",
            new Class[]{},
            new Object[]{});
        assertEquals(expected, result);
    }


   public void testIsLogEnabledForFolderCreation() throws Throwable  {
        int depth           = 8;
        String parentDir    = rootPath+"/usr/local/Funambol/tools/tomcat/bin/../../../ds-server/db/picture";
        String user         = "Martin";
        FileSystemDAOHelper helper = new FileSystemDAOHelper(user,
                                                             parentDir,
                                                             depth);

        Throwable exception = null;
        boolean result = (Boolean) PrivateAccessor.invoke(
                helper,
                "isLogForFolderCreationEnabled",
                new Class[]{Throwable.class},
                new Object[]{exception}
        );

        assertTrue("Log at trace level no exception occurred, expecting true",
                   result);


        exception = new Exception("Here is the exception.");
        result = (Boolean) PrivateAccessor.invoke(
                helper,
                "isLogForFolderCreationEnabled",
                new Class[]{Throwable.class},
                new Object[]{exception}
        );
        
        assertTrue("Log at trace level and an exception occurred, expecting true",
                    result);

        // when the log is not trace enabled and no exception occurred, the log
        // is disabled
        FunambolLoggerFactory.getLogger(Def.LOGGER_NAME).setLevel(Level.OFF);
        exception = null;
        result = (Boolean) PrivateAccessor.invoke(
                helper,
                "isLogForFolderCreationEnabled",
                new Class[]{Throwable.class},
                new Object[]{exception}
        );

        assertFalse(result);

        // exception not null, an error occurred log about directory creation is
        // required
        exception = new Exception("Here is the exception.");
        result = (Boolean) PrivateAccessor.invoke(
                helper,
                "isLogForFolderCreationEnabled",
                new Class[]{Throwable.class},
                new Object[]{exception}
        );

        assertTrue("Log not at trace level but an exception occurred, expecting true",
                   result);
        FunambolLoggerFactory.getLogger(Def.LOGGER_NAME).setLevel(Level.ALL);

   }

    public void testCreateLogMessageAboutFolderCreation() throws Throwable  {
        File root  = new File(rootPath);
        File dirA  = new File(root,"/usr/local/Funambol/ds-server/db/picture/hp/uu/gm/em/eg/mm/qk/wl/TWFydGlu");
        File dirB  = new File(root,"/usr/local/Funambol/ds-server/db/picture/eg/ek/qx/ll/ob/hl/um/lb/am9obg==");

        String DIR_CREATED_MESSAGE                         =
                "Directory '"+dirA.getCanonicalPath()+"' created in '234' ms.";
        String DIR_ALREADY_EXISTED                         =
                "Directory '"+dirA.getCanonicalPath()+"' already existed in '234' ms.";
        String DIR_CREATION_FAILED_BUT_EXISTS              =
                "Directory '"+dirA.getCanonicalPath()+"' creation failed in '234' ms but the folder does exist.";
        String DIR_CREATION_FAILED_UNKNOWN_RESULT_1 =
                "Creation of folder '"+dirB.getCanonicalPath()+"' failed due to an uknown result '-1' in '234' ms. Dumping folder data: exists 'false', isFile 'false', isDirectory 'false'.";
        String DIR_CREATION_FAILED_UNKNOWN_RESULT_2 =
                "Creation of folder '"+dirB.getCanonicalPath()+"' failed due to an uknown result '5' in '234' ms. Dumping folder data: exists 'false', isFile 'false', isDirectory 'false'.";
        String DIR_CREATION_FAILED_EXCEPTION =
                "Creation of folder '"+dirB.getCanonicalPath()+"' failed due to an exception in '234' ms. Dumping folder data: exists 'false', isFile 'false', isDirectory 'false'.";

        String DIR_CREATED_MESSAGE_NULL_FILE               =
                "Directory 'null folder' created in '234' ms.";
        String DIR_ALREADY_EXISTED_NULL_FILE               =
                "Directory 'null folder' already existed in '234' ms.";
        String DIR_CREATION_FAILED_BUT_EXISTS_NULL_FILE    =
                "Directory 'null folder' creation failed in '234' ms but the folder does exist.";
        String DIR_CREATION_FAILED_UNKNOWN_RESULT_1_NULL_FILE =
                "Creation of folder 'null folder' failed due to an uknown result '-1' in '234' ms. Folder is null.";
        String DIR_CREATION_FAILED_UNKNOWN_RESULT_2_NULL_FILE =
                "Creation of folder 'null folder' failed due to an uknown result '5' in '234' ms. Folder is null.";
        String DIR_CREATION_FAILED_EXCEPTION_NULL_FILE =
                "Creation of folder 'null folder' failed due to an exception in '234' ms. Folder is null.";

        short result        = IOTools.DIRECTORY_CREATED;
        long elapsed        = 234;
        Throwable exception = null;

        int depth           = 8;
        String parentDir    = rootPath+"/usr/local/Funambol/tools/tomcat/bin/../../../ds-server/db/picture";
        String user         = "Martin";
        FileSystemDAOHelper helper = new FileSystemDAOHelper(user,
                                                             parentDir,
                                                             depth);
        File  folder        = null;
        File  notExistingFolder = new File(
            ""+root.getCanonicalPath(),
            "/usr/local/Funambol/ds-server/db/picture/eg/ek/qx/ll/ob/hl/um/lb/am9obg==");

        //
        // Tests for null folder
        //
        String resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir created message",
                     DIR_CREATED_MESSAGE_NULL_FILE, resultMessage);

        result        = IOTools.DIRECTORY_ALREDY_EXISTS;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir alredy exists message",
                     DIR_ALREADY_EXISTED_NULL_FILE, resultMessage);

        result        = IOTools.DIRECTORY_CREATION_FAILED_BUT_EXISTS;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed but the alredy exists message",
                     DIR_CREATION_FAILED_BUT_EXISTS_NULL_FILE, resultMessage);

        result        = -1;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht bad result 1",
                     DIR_CREATION_FAILED_UNKNOWN_RESULT_1_NULL_FILE,
                     resultMessage);

        result        = 5;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht bad result 2",
                     DIR_CREATION_FAILED_UNKNOWN_RESULT_2_NULL_FILE,
                     resultMessage);

        exception = new IOException("An error occurred creating the folder.");
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht bad result 2",
                     DIR_CREATION_FAILED_EXCEPTION_NULL_FILE, resultMessage);

        exception = null;

        //
        // Tests for a not null folder
        //
        folder = helper.getFolder();

        result = IOTools.DIRECTORY_CREATED;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir created message",
                     DIR_CREATED_MESSAGE, resultMessage);

        result        = IOTools.DIRECTORY_ALREDY_EXISTS;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir alredy exists message",
                     DIR_ALREADY_EXISTED, resultMessage);

        result        = IOTools.DIRECTORY_CREATION_FAILED_BUT_EXISTS;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{folder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed but the alredy exists message",
                     DIR_CREATION_FAILED_BUT_EXISTS, resultMessage);

        result        = -1;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{notExistingFolder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht bad result 1",
                     DIR_CREATION_FAILED_UNKNOWN_RESULT_1, resultMessage);

        result        = 5;
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{notExistingFolder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht bad result 2",
                     DIR_CREATION_FAILED_UNKNOWN_RESULT_2, resultMessage);

        exception = new IOException("An error occurred creating the folder.");
        resultMessage = (String) PrivateAccessor.invoke(
                helper,
                "createLogMessageAboutFolderCreation",
                new Class[]{File.class,short.class,long.class,Throwable.class},
                new Object[]{notExistingFolder,result,elapsed,exception}
        );
        assertEquals("Wrong dir creation failed wiht exception",
                     DIR_CREATION_FAILED_EXCEPTION, resultMessage);
    }

    public void testGetTmpFileSize() throws IOException {
        String userName = "username1";
        FileSystemDAOHelper helper =
            new FileSystemDAOHelper(userName, rootPath, FILE_SYSTEM_DIR_DEPTH_0);

        //create temp file
        byte[] inputContent = "test test test test test test".getBytes("UTF-8");
        InputStream in = new ByteArrayInputStream(inputContent);
        long expectedSize = inputContent.length;
        File tmpFile = helper.saveTmpFile(in, expectedSize);
        //get file size
        String tmpFileName = tmpFile.getName();
        long currentSize = helper.getTmpFileSize(tmpFileName);

        assertEquals("Size mismatch", expectedSize, currentSize);
        helper.delete(tmpFileName);
    }
}
