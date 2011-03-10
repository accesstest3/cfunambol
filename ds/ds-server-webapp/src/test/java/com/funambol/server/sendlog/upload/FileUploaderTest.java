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

package com.funambol.server.sendlog.upload;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import junitx.util.PrivateAccessor;

import com.funambol.framework.tools.IOTools;
import com.funambol.server.sendlog.Constants;

import com.funambol.tools.test.FileAssert;
import com.funambol.tools.test.FileSystemResourcesTestCase;

/**
 * Test cases for FileUploader class.
 *
 * @version $Id: FileUploaderTest.java 36132 2010-11-03 14:11:31Z luigiafassina $
 */
public class FileUploaderTest
extends FileSystemResourcesTestCase
implements Constants {
   
    // --------------------------------------------------------------- Constants
    public static final String SOME_DATA                = "Some data";
    public static final String RESOURCES_DIR            =
        "src/test/resources/data/com/funambol/server/sendlog/provider/FileUploader/";
    public static final String TARGET_RESOURCES_DIR     =
        "target/test-classes/data/com/funambol/server/sendlog/provider/FileUploader/";
    public static final String SIMPLE_LOG_INPUT_FILE    =
        RESOURCES_DIR+"synclog.txt";
    public static final String ARCHIVED_LOG_INPUT_FILE  =
        RESOURCES_DIR+"synclog.zip";

    // ------------------------------------------------------------ Constructors
    public FileUploaderTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    public void testFileUploader_NullProvider() throws Exception {
        
        try {

            new FileUploader(null);
            fail("Exception expected on null input parameter");
            
        } catch(IllegalStateException e) {
            assertEquals("Wrong exception message raised upon null provider.",
                         FileUploader.NULL_PROVIDER_ERRMSG,
                         e.getMessage());
        }
    }

    public void testUploadFile_NullInputStream() throws Exception {
        InputStream is             = getSimpleLogFile();
        String      targetFileName = null;
        String      fileName       = null;
        String      contentType    = null;
        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType);
        PrivateAccessor.setField(fakeProvider, "inputStream", null);
        FileUploader uploader = new FileUploader(fakeProvider);

        try {
            uploader.uploadFile();
            fail("Exception expected on null input parameter");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message raised upon null input stream.",
                         NULL_INPUTSTREAM_ERRMSG,
                         e.getMessage());
        }
    }

    public void testUploadFile_simpleLogFile() throws Exception {
        InputStream is = getSimpleLogFile();

        String targetFileName = TARGET_RESOURCES_DIR+"file.zip";
        delete(targetFileName);

        String fileName    = "device.log";
        String contentType = MimeTypeDictionary.TEXT_PLAIN;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType,
                                           "FileUploader",
                                           "devicelog");
        FileUploader uploader = new FileUploader(fakeProvider);

        uploader.uploadFile();

        // the file exists
        FileAssert.assertFileExist(TARGET_RESOURCES_DIR, "file.zip");
        deleteRequestOnTearDown(targetFileName);
        
        FileAssert.assertZipEntryEqualsToFile(targetFileName, 
                                              fileName,
                                              SIMPLE_LOG_INPUT_FILE);
    }

    public void testUploadFile_ArchivedLogFile() throws Exception {
        InputStream is = getArchivedLogFile();

        String targetFileName = TARGET_RESOURCES_DIR+"file.zip";
        delete(targetFileName);

        String fileName    = "device.log";
        String contentType = MimeTypeDictionary.APPLICATION_ZIP;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType,
                                           "FileUploader",
                                           "devicelog");
        FileUploader uploader = new FileUploader(fakeProvider);

        uploader.uploadFile();

        // the file exists
        FileAssert.assertFileExist(TARGET_RESOURCES_DIR, "file.zip");
        deleteRequestOnTearDown(targetFileName);

        FileAssert.assertZipEntryEqualsToFile(targetFileName,
                                              fileName,
                                              SIMPLE_LOG_INPUT_FILE);
    }

    public void testUploadFile_bigLogFile() throws Exception {
        InputStream is = getFile(SIMPLE_LOG_INPUT_FILE);

        String targetFileName = TARGET_RESOURCES_DIR + "bigfile.zip";
        delete(targetFileName);

        String fileName    = "bigdevice.log";
        String contentType = MimeTypeDictionary.TEXT_PLAIN;
        long max_file_size = 20000L;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType,
                                           "FileUploader",
                                           "devicelog",
                                           max_file_size);
        FileUploader uploader = new FileUploader(fakeProvider);

        try {
            uploader.uploadFile();
            fail("Expected an UploadFileException with HTTP status code 413");
        } catch (UploadFileException e) {
            assertEquals("Wrong HTTP status code returned",
                         413, e.getHttpStatusCode());
        }

        // the file exists
        FileAssert.assertFileExist(TARGET_RESOURCES_DIR, "bigfile.zip");
        byte[] read =
            IOTools.readZIPFile(new File(TARGET_RESOURCES_DIR, "bigfile.zip"));

        assertEquals("Uploaded a wrong number of bytes", 
                     (int)max_file_size, read.length);
        deleteRequestOnTearDown(targetFileName);
    }

    public void testGetOutputStream_ContentTypeNull() throws Throwable {
        InputStream is = getSimpleLogFile();

        String targetFileName = TARGET_RESOURCES_DIR+"file.zip";
        delete(targetFileName);

        String fileName    = "device.log";
        String contentType = MimeTypeDictionary.TEXT_PLAIN;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType);
        FileUploader uploader = new FileUploader(fakeProvider);

        try {

            PrivateAccessor.invoke(uploader,
                                   "getOutputStream",
                                   new Class[]{String.class, String.class, String.class},
                                   new Object[]{null, fileName, targetFileName});
            fail("An IllegalArgumentException was expected.");

        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         NULL_CONTENT_TYPE_ERRMSG, e.getMessage());
        }
    }

    public void testGetOutputStream_TargetFilenameNull() throws Throwable {
        InputStream is = getSimpleLogFile();

        String targetFileName = TARGET_RESOURCES_DIR+"file.zip";
        delete(targetFileName);

        String fileName    = "device.log";
        String contentType = MimeTypeDictionary.TEXT_PLAIN;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType);
        FileUploader uploader = new FileUploader(fakeProvider);

        try {

            PrivateAccessor.invoke(uploader,
                                   "getOutputStream",
                                   new Class[]{String.class, String.class, String.class},
                                   new Object[]{contentType, fileName, null});
            fail("An IllegalArgumentException was expected.");

        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         NULL_TARGET_FILENAME_ERRMSG, e.getMessage());
        }
    }

    public void testGetOutputStream_RealFilenameNull() throws Throwable {
        InputStream is = getSimpleLogFile();

        String targetFileName = TARGET_RESOURCES_DIR+"file.zip";
        delete(targetFileName);

        String fileName    = "device.log";
        String contentType = MimeTypeDictionary.TEXT_PLAIN;

        MockLogInformationProvider fakeProvider =
            new MockLogInformationProvider(is,
                                           targetFileName,
                                           fileName ,
                                           contentType);
        FileUploader uploader = new FileUploader(fakeProvider);

        try {

            PrivateAccessor.invoke(uploader,
                                   "getOutputStream",
                                   new Class[]{String.class, String.class, String.class},
                                   new Object[]{contentType, null, targetFileName});
            fail("An IllegalArgumentException was expected.");

        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         NULL_REAL_FILENAME_ERRMSG, e.getMessage());
        }
    }

    // --------------------------------------------------------- Private methods
    private InputStream getSimpleLogFile() throws IOException {
        return getFile(SIMPLE_LOG_INPUT_FILE);
    }

    private InputStream getArchivedLogFile() throws IOException {
        return getFile(ARCHIVED_LOG_INPUT_FILE);
    }

    private InputStream getFile(String fileName) throws IOException {
        File inputFile = new File(fileName);
        if(!inputFile.exists() || !inputFile.isFile()) {
            throw new IOException("Unable to find file "+fileName+"'");
        }
        return new FileInputStream(inputFile);
    }

}
