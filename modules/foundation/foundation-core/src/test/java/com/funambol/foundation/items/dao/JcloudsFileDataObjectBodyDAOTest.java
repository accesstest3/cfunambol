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

package com.funambol.foundation.items.dao;

import java.io.File;

import junit.framework.TestCase;

import junitx.framework.ArrayAssert;

import com.funambol.framework.tools.IOTools;

import com.funambol.common.media.file.FileDataObjectBody;

import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileDataObjectHelper;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;
import com.funambol.foundation.util.TestDef;

/**
 * Test class for <code>FileSystemFileDataObjectBodyDAO</code>.
 *
 * @version $Id: JcloudsFileDataObjectBodyDAOTest.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class JcloudsFileDataObjectBodyDAOTest
        extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants
    private static final String PACKAGE_PATH = "/com/funambol/foundation/items/dao/JcloudsFileDataObjectBodyDAO/";

    private static final String RESOURCE_PATH = TEST_RESOURCE_BASEDIR + "/data" + PACKAGE_PATH;
    private static final String ROOT_TARGET_PATH = TEST_DATA_BASEDIR + PACKAGE_PATH;

    private static final String JCLOUDS_PROVIDER = "filesystem";
    private static final String CONTAINER_NAME = "container-test";

    private static final int FILE_SYS_DIR_DEPTH = 8;
    private static final String USER_NAME_1 = "test-user1";
    private static final String USER1_FOLDER_PATH = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==/";
    private static final String USER_NAME_2 = "test-user2";
    private static final String USER2_FOLDER_PATH = "go/aj/eb/xv/es/xx/qn/tf/dGVzdC11c2VyMg==";

    private static final String FILE_NAME_1 =  "item1.jpg";
    private static final String FILE_NAME_2 =  "item2.jpg";

    private final FileDataObjectHelper FDOHelper;

    // ------------------------------------------------------------ Private data

    private FileDataObjectNamingStrategy namingStrategy =
        new FileDataObjectNamingStrategy(FILE_SYS_DIR_DEPTH);

    // ------------------------------------------------------------ Constructors

    public JcloudsFileDataObjectBodyDAOTest(String testName) {
        super(testName);
        FDOHelper = new FileDataObjectHelper(RESOURCE_PATH, ROOT_TARGET_PATH);

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

    /**
     * Test of getItem method, of class FileDAO.
     */
    public void testGetItem() throws Exception {
        String userName = USER_NAME_1;
        // define expected results values
        byte[] expectedBody = "Random text\n".getBytes();

        // Set up for testing.
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);

        //create the item
        FileDataObjectWrapper fdow = FDOHelper.createFileDataObjectWrapperFromByteArray(
                userName, "getitem.txt", expectedBody);
        dao.addItem(fdow);
        String localName = fdow.getLocalName();

        // Call the method being tested.
        FileDataObjectBody body = dao.getItem(localName);

        // Verify that the results are as expected: the resulting path is as
        File file = body.getBodyFile();
        assertTrue("File does not exist: " + file.getPath(), file.exists());
//        assertEquals("", file.getPath());
        byte[] retrievedBody = IOTools.readFileBytes(file);
        ArrayAssert.assertEquals(expectedBody, retrievedBody);
    }


    /**
     * Test of addItem method, of class FileDAO.
     */
    public void testAddItem_PayloadFromMemory() throws Exception {

        // define initial parameter values
        String userName = USER_NAME_1;
        String fileName = "general.txt";
        FileDataObjectWrapper fdow = FDOHelper.createFileDataObjectWrapperFromRandomByteArray(
                userName, fileName, 256);
        File sourceFile = fdow.getFileDataObject().getBodyFile();

        // define expected results values
        byte[] expectedContent = IOTools.readFileBytes(sourceFile);
        String expectedPath = USER1_FOLDER_PATH + "xxxxxxxxxxxxx-0";

        // Set up for testing.
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);

        // Call the method being tested.
        dao.addItem(fdow);

        // Verify that the results are as expected: the resulting path is as
        // expected (cannot compare random generated part)
        String localName = fdow.getLocalName();
        String objectPath = namingStrategy.getFilePath(userName, localName);
        assertEquals("Wrong item name prefix", expectedPath.substring(0, 40), objectPath.substring(0, 40));
        assertEquals("Wrong item name suffix", expectedPath.substring(54), objectPath.substring(54));

        // Verify that the results are as expected: the file really exists on the cloud
        String completeFilePath = getCloudFilePath(userName, localName);
        File file = new File(completeFilePath);
        assertTrue("File doesn't exist", file.exists());
        // Verify that the results are as expected: the content is the one expected.
        byte[] retrievedContent = IOTools.readFileBytes(completeFilePath);
        ArrayAssert.assertEquals("Different content", expectedContent, retrievedContent);
        // Verify if the local temporary file has been removed
        assertFalse("Original tmp file still exist", sourceFile.exists());

        // Clean up
        dao.removeItem(fdow.getLocalName());
    }

    /**
     * Test of addItem method, of class FileDAO.
     */
    public void testAddItem_PayloadFromFile() throws Exception {
        // define initial parameter values
        String userName = USER_NAME_1;
        String tempFileName = "aaaaaaa.tmp";

        File sourceFile = FDOHelper.createTemporaryFileFromFileInResource(FILE_NAME_1, tempFileName);

        // define expected results
        String expectedPath = USER1_FOLDER_PATH + "xxxxxxxxxxxxx-0";
        byte[] expectedContent = IOTools.readFileBytes(sourceFile);

        // Set up for testing.
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);
        FileDataObjectWrapper fdow =
                FDOHelper.createFileDataObjectWrapper(userName, "generic.txt", sourceFile);

        // Call the method being tested.
        dao.addItem(fdow);

        // Verify that the results are as expected
        // Verify if the resulting path is as expected (cannot compare random generated part)
        String localName = fdow.getLocalName();
        String objectPath = namingStrategy.getFilePath(userName, localName);
        assertEquals("Wrong item name prefix", expectedPath.substring(0, 40), objectPath.substring(0, 40));
        assertEquals("Wrong item name suffix", expectedPath.substring(54), objectPath.substring(54));

        //check if file really exists
        String completeFilePath = getCloudFilePath(userName, localName);
        File file = new File(completeFilePath);
        assertTrue("File doesn't exist", file.exists());

        //compare the content of the file with the expected content
        byte[] retrievedContent = IOTools.readFileBytes(completeFilePath);
        ArrayAssert.assertEquals("Different content", expectedContent, retrievedContent);

        // check if the local file has been removed
        assertFalse("Original tmp file still exist", sourceFile.exists());

        // Clean up
        dao.removeItem(fdow.getLocalName());
    }

    /**
     * Test of updateItem method, of class FileDAO.
     */
    public void testUpdateItem_PayloadFromMemory() throws Exception {

        // define initial parameter values
        final String userName = USER_NAME_1;
        final byte[] initialContent = "Version 1.0".getBytes();
        final byte[] modifiedContent = "Version 2.0".getBytes();

        // define expected results values
        byte[] expectedContent = "Version 2.0".getBytes();

        // Set up for testing.
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);
        // Add an item that will be than updated
        FileDataObjectWrapper firstFdow = FDOHelper.createFileDataObjectWrapperFromByteArray(
                userName, "dummyInitial.tmp", initialContent);
        File initialFile = firstFdow.getFileDataObject().getBodyFile();
        dao.addItem(firstFdow);

        File firstFile = new File(getCloudFilePath(userName, firstFdow.getLocalName()));
        assertTrue("Missing file", firstFile.isFile());
        assertFalse("Original tmp file still exist", initialFile.exists());

//        // simulating thumbnail uplaod
//        String completeFirstExtFolderPath = getCloudExtFolderPath(userName, firstFdow.getLocalName());
//        File extDir = new File(completeFirstExtFolderPath);
//        extDir.mkdir();
//        File ext1 = new File(extDir, "ext1.info");
//        File ext2 = new File(extDir, "ext2.info");
//        ext1.createNewFile();
//        ext2.createNewFile();

        // define the item to update
        FileDataObjectWrapper updatedFdow = FDOHelper.createFileDataObjectWrapperFromByteArray(
                userName, "dummyModified.tmp", modifiedContent);
        updatedFdow.setLocalName(firstFdow.getLocalName());
        File modifiedFile = updatedFdow.getFileDataObject().getBodyFile();

        // Call the method being tested.
        dao.updateItem(updatedFdow);

        // Verify that the results are as expected
        // Verify if the file really exists on the cloud
        File newFile = new File(getCloudFilePath(userName, updatedFdow.getLocalName()));
        assertTrue("Missing file: " + newFile.getPath(), newFile.isFile());
        // Verify if the content of the file is the one expected.
        byte[] retrieved = IOTools.readFileBytes(newFile);
        ArrayAssert.assertEquals(expectedContent, retrieved);
        // Verify if the previous uploaded file and exif dir has been removed
        assertFalse("Previous file still exists", firstFile.isFile());
//        assertFalse("Previous ext dir still exists", extDir.isDirectory());
        // Verify if the local temporary file has been removed
        assertFalse("Original tmp file still exist", initialFile.exists());
        assertFalse("Modified tmp file still exist", modifiedFile.exists());

        // Clean up
        dao.removeItem(updatedFdow.getLocalName());
    }

    /**
     * Test of removeItem method, of class FileDAO.
     */
    public void testRemoveItem() throws Exception {

        // define initial parameter values
        String userName = USER_NAME_1;

        // Set up for testing: add the item that will be removed
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);
        FileDataObjectWrapper fdow = FDOHelper.createFileDataObjectWrapperFromRandomByteArray(userName, "general.txt", 300);
        dao.addItem(fdow);
        // check if the added file actually exists
        File file = new File(getCloudFilePath(userName, fdow.getLocalName()));
        assertTrue("File doesn't exists: "+file.getPath(), file.exists());

//        // Set up for testing: simulating thumbnail uplaod
//        File extFolder = new File(getCloudExtFolderPath(userName, fdow.getLocalName()));
//        extFolder.mkdir();
//        File ext1 = new File(extFolder, "ext1.info");
//        File ext2 = new File(extFolder, "ext2.info");
//        ext1.createNewFile();
//        ext2.createNewFile();
//        // check if the added ext dir and files actually exist
//        assertTrue("ext dir does not exist on the cloud: "+extFolder.getPath(), extFolder.isDirectory());
//        assertTrue("ext file does not exist on the cloud: "+ext1.getPath(), ext1.exists());
//        assertTrue("ext file does not exist on the cloud: "+ext2.getPath(), ext2.exists());

        // Call the method being tested.
        dao.removeItem(fdow.getLocalName());

        // Verify that the results are as expected
        assertFalse("File still exist on the cloud", file.exists());
//        assertFalse("ext dir still exist on the cloud: "+extFolder.getPath(), extFolder.isDirectory());
    }

    /**
     * Test of addItem method, of class FileDAO.
     */
    public void testAddItem_User2() throws Exception {

        // define initial parameter values
        String userName = USER_NAME_2;
        FileDataObjectWrapper fdow =
                FDOHelper.createFileDataObjectWrapperFromRandomByteArray(userName, "genericfile.txt", 300);

        File sourceFile = fdow.getFileDataObject().getBodyFile();

        // define expected results values
        byte[] expectedContent = IOTools.readFileBytes(sourceFile);
        String expectedPath = USER2_FOLDER_PATH + "/" + "xxxxxxxxxxxxx-0";

        // Set up for testing.
        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);

        // Call the method being tested.
        dao.addItem(fdow);

        // Verify that the results are as expected: the resulting path is as
        // expected (cannot compare random generated part)
        String localName = fdow.getLocalName();
        String currentPath = namingStrategy.getFilePath(userName, localName);
        assertEquals("Wrong item name prefix", expectedPath.substring(0, 40), currentPath.substring(0, 40));
        assertEquals("Wrong item name suffix", expectedPath.substring(54), currentPath.substring(54));

        // Verify that the results are as expected: the file really exists on the cloud
        String completeFilePath = getCloudFilePath(userName, fdow.getLocalName());
        File file = new File(completeFilePath);
        assertTrue("File doesn't exist", file.exists());
        // Verify that the results are as expected: the content is the one expected.
        byte[] retrievedContent = IOTools.readFileBytes(completeFilePath);
        ArrayAssert.assertEquals("Different content", expectedContent, retrievedContent);
        // Verify if the local temporary file has been removed
        assertFalse("Original tmp file still exist", sourceFile.exists());

        // Clean up
        dao.removeItem(fdow.getLocalName());
    }

    public void _testRemoveAllItems() throws Exception {

        // define initial parameter values
        String userName = USER_NAME_2;

        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);
        for (int i = 0; i < 4; i++) {
            FileDataObjectWrapper fdow = FDOHelper.createFileDataObjectWrapperFromRandomByteArray(
                    userName, "dummy" + i + ".tmp", 45 + i * 2);
            dao.addItem(fdow);
            assertTrue("File doesn't exist",
                new File(getCloudFilePath(userName, fdow.getLocalName())).exists());
       }

        File userDir = new File(getCloudUserFolderPath(userName));
        assertTrue(userDir.getPath() + " not exists", userDir.exists());

        // Call the method being tested.
        dao.removeAllItems();

        // Verify that the results are as expected
        assertFalse(userDir.getPath() + " still exists", userDir.exists());
    }

    public void _testReleaseResources() throws Exception {

        // define initial parameter values
        String userName = USER_NAME_2;

        JcloudsFileDataObjectBodyDAO dao = createInstance(userName);
        for (int i = 0; i < 4; i++) {
            FileDataObjectWrapper fdow = FDOHelper.createFileDataObjectWrapperFromRandomByteArray(
                    userName, "dummy" + i + ".tmp", 45 + i * 2);
            dao.addItem(fdow);
            assertTrue("File doesn't exist",
                new File(getCloudFilePath(userName, fdow.getLocalName())).exists());
       }

        File userDir = new File(getCloudUserFolderPath(userName));
        assertTrue(userDir.getPath() + " not exists", userDir.exists());

        // Call the method being tested.
        dao.releaseResources();

        // Verify that the results are as expected
        assertFalse(userDir.getPath() + " still exists", userDir.exists());
    }

    // --------------------------------------------------------- Private Methods
    private JcloudsFileDataObjectBodyDAO createInstance(String userName) throws Exception {
        FileSystemDAOHelper helper = new FileSystemDAOHelper(userName, ROOT_TARGET_PATH + "/local", namingStrategy);

        return new JcloudsFileDataObjectBodyDAO(
            namingStrategy,
            helper,
            JCLOUDS_PROVIDER,
            "",
            "",
            CONTAINER_NAME,
            userName,
            ROOT_TARGET_PATH);
    }

    /**
     * Returns the path where the user's files are stored by the jclouds file
     * system provider.
     * @param userName
     * @return
     */
    private String getCloudUserFolderPath(String userName) {
        String filePath = namingStrategy.getUserFolderPath(userName);
        String completeFilePath = ROOT_TARGET_PATH + File.separator + CONTAINER_NAME + File.separator + filePath;
        return completeFilePath;
    }

    /**
     * Returns the path where the file has been stored by the jclouds file
     * system provider.
     * @param userName
     * @param localName
     * @return
     */
    private String getCloudFilePath(String userName, String localName) {
        String filePath = namingStrategy.getFilePath(userName, localName);
        String completeFilePath = ROOT_TARGET_PATH + File.separator + CONTAINER_NAME + File.separator + filePath;
        return completeFilePath;
    }

    /**
     * Returns the path of the ext directory on the file system managed by the
     * jclouds file system provider
     * @param userName
     * @param localName
     * @return
     */
    private String getCloudExtFolderPath(String userName, String localName) {
        String extFolderPath = namingStrategy.getEXTFolderPath(userName, localName);
        String completeExtFolderPath = ROOT_TARGET_PATH + File.separator + CONTAINER_NAME + File.separator + extFolderPath;
        return completeExtFolderPath;
    }

}
