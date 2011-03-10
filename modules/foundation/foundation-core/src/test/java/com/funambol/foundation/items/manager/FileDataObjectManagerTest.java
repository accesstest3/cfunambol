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

package com.funambol.foundation.items.manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.media.file.UploadStatus;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.tools.IOTools;

import com.funambol.tools.database.DBHelper;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileAssert;
import com.funambol.foundation.util.TestDef;

/**
 * FileDataObjectManager's test cases.
 * 
 * @version $Id: FileDataObjectManagerTest.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FileDataObjectManagerTest extends TestCase implements TestDef {

    // ------------------------------------------------------------ Private data

    private String jcloudRootPath =
        "./target/test-classes/data/com/funambol/foundation/items/manager/FileDataObjectManager";
    private static final String JCLOUDS_PROVIDER = "filesystem";
    private static final String JCLOUDS_CONTAINER_NAME = "container-test";
    private static final String JCLOUDS_IDENTITY = "";
    private static final String JCLOUDS_CREDENTIAL = "";

    private String localRootPath =
        jcloudRootPath + File.separator + JCLOUDS_CONTAINER_NAME;

    private static final String RESOURCES_DIR
            = "./src/test/resources/data/com/funambol/foundation/items/manager/FileDataObjectManager/";
    private static final String WATERFALL_IMG    = "waterfall.jpg";
    private static final String WATERFALL_IMG_1  = "waterfall1.jpg";
    private static final String FLOWER_IMG       = "flower.jpg";
    private static final String FLOWER_IMG_1     = "flower1.jpg";
    private static final String IMG_00  = RESOURCES_DIR+WATERFALL_IMG;
    private static final String IMG_00_TMP = RESOURCES_DIR+WATERFALL_IMG_1;
    private static final String IMG_01  = RESOURCES_DIR+FLOWER_IMG;
    private static final String IMG_01_TMP = RESOURCES_DIR+FLOWER_IMG_1;

    private static final String TABLE_FNBL_FILE_DATA_OBJECT =
        "fnbl_file_data_object";

    private static String userId = DBHelper.USER_PART1;
    //this is the base64 enconding for DBHelper.USER_PART1 string
    private static String userIdBase64 = "dXNlcm5hbWUx";

    private static String sourceURI = "picture";

    // this user folder is based on the userid and on directory depth...
    //if one of the two changes, also this dir should change
    private final String userFolderPath = 
        localRootPath + File.separator + userIdBase64;

    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE,
                                                      USER_SCHEMA_SOURCE,
                                                      false);

            // checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    DatabaseConnection userConn = null;

    // ------------------------------------------------------------ Constructors
    public FileDataObjectManagerTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        userConn = new DatabaseConnection(DBHelper.getUserConnection(userId));
        IDatabaseTester userDatabaseTester  = DBHelper.createDatabaseTester(
            userConn.getConnection(),
            RESOURCES_DIR + "init-dataset-userdb.xml",
            true);

        // it's needed to reopen the connection since the previous assert
        // close it
        userConn = new DatabaseConnection(DBHelper.getUserConnection(userId));
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        FileDataObjectManager manager = getNewFileDataObjectManager();

        //remove all the user's files on storage provider
        manager.removeAllItems();
        //delete also all jclouds temporary files
        manager.close();
        DBHelper.closeConnection(userConn);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of addItem method, of class FileDataObjectManager.
     * @throws Exception if an error occurs
     */
    public void testAddItem_FileDataObject_Timestamp() throws Exception {
        String sourceDataFileName = "waterfall.jpg";
        String tmpFileName = "1234567890.tmp";

        File sourceDataFile = new File(RESOURCES_DIR, sourceDataFileName);
        byte[] sourceData = IOTools.readFileBytes(sourceDataFile);
        File tmpFile = new File(userFolderPath, tmpFileName);
        FileUtils.copyFile(sourceDataFile, tmpFile);

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject fdo = createFileDataObject(sourceDataFileName, tmpFileName);
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // execute
        String uid = manager.addItem(fdo, now);

        // check results
        FileDataObjectWrapper wrapper = manager.getItem(uid);
        FileDataObject result = wrapper.getFileDataObject();

        assertTrue("File doesn't exist", result.getBodyFile().isFile());

        byte[] storedContent = IOTools.readFileBytes(result.getBodyFile());
        ArrayAssert.assertEquals("Wrong stored content", sourceData, storedContent);

        assertEquals("Wrong name", sourceDataFileName, result.getName());
        assertEquals("Wrong size", sourceData.length, (result.getSize()).intValue());
    }

    public void testAddItemMetadata_FullMetadataTest() throws Exception {

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setAccessed("20030807T015500Z");
        fdo.setHidden(false);
        fdo.setSystem(false);
        fdo.setArchived(true);
        fdo.setDeleted(false);
        fdo.setWritable(true);
        fdo.setReadable(true);
        fdo.setExecutable(false);
        fdo.setContentType("text/plain");
        fdo.setSize(11201L);
        //body
        fdo.setBody(null);

        // execute
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String uid = manager.addItemMetadata(fdo, now);

        // check results
        FileDataObjectWrapper fdoWrapper = manager.getItem(uid);
        FileDataObject fdoResult = fdoWrapper.getFileDataObject();

        //body
        assertFalse("Body is not empty", fdoResult.hasBodyFile());

        //metadata
        assertEquals("Metadata Name wrong", "testPicture.png", fdoResult.getName());
        assertEquals("Metadata Created wrong", 
                     TimeUtils.UTCToLocalTime("20030807T231830Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getCreated(), null));
        assertEquals("Metadata Modified wrong", 
                     TimeUtils.UTCToLocalTime("20030807T012500Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getModified(), null));
        assertEquals("Metadata Accessed wrong",
                     TimeUtils.UTCToLocalTime("20030807T015500Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getAccessed(), null));
        assertFalse("Metadata Hidden wrong", fdoResult.getHidden());
        assertFalse("Metadata System wrong", fdoResult.getSystem());
        assertTrue("Metadata Archived wrong", fdoResult.getArchived());
        assertFalse("Metadata Deleted wrong", fdoResult.getDeleted());
        assertTrue("Metadata Writable wrong", fdoResult.getWritable());
        assertTrue("Metadata Readable wrong", fdoResult.getReadable());
        assertFalse("Metadata Executable wrong", fdoResult.getExecutable());
        assertEquals("Metadata Content wrong", "text/plain", fdoResult.getContentType());
        assertEquals("Metadata Declared Size wrong",
                     new Long(11201), fdoResult.getSize());
    }

    public void testUpdateItemMetadata_FullMetadataTest() throws Exception {

        FileDataObjectWrapper fdow;
        FileDataObject fdo;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setAccessed("20030807T015500Z");
        fdo.setHidden(false);
        fdo.setSystem(false);
        fdo.setArchived(true);
        fdo.setDeleted(false);
        fdo.setWritable(true);
        fdo.setReadable(true);
        fdo.setExecutable(false);
        fdo.setContentType("text/plain");
        fdo.setSize(11201L);
        //body
        fdo.setBody(null);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // execute
        String uid = manager.addItemMetadata(fdo, now);

        //get the element results
        fdow = manager.getItem(uid);
        fdo = fdow.getFileDataObject();

        //modify results
        fdo.setModified("20100807T041000Z");
        fdo.setAccessed("20100807T042000Z");
        fdo.setSize(12000L);
        now = new Timestamp(System.currentTimeMillis());
        manager.completeAndUpdateItem(uid, fdo, now);

        //get the element results
        fdow = manager.getItem(uid);
        FileDataObject fdoResult = fdow.getFileDataObject();

        //body
        assertFalse("Body is not empty", fdoResult.hasBodyFile());

        //metadata
        assertEquals("Metadata Name wrong", "testPicture.png", fdoResult.getName());
        assertEquals("Metadata Created wrong", 
                     TimeUtils.UTCToLocalTime("20030807T231830Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getCreated(), null));
        assertEquals("Metadata Modified wrong",
                     TimeUtils.UTCToLocalTime("20100807T041000Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getModified(), null));
        assertEquals("Metadata Accessed wrong",
                     TimeUtils.UTCToLocalTime("20100807T042000Z", null),
                     TimeUtils.UTCToLocalTime(fdoResult.getAccessed(), null));
        assertFalse("Metadata Hidden wrong", fdoResult.getHidden());
        assertFalse("Metadata System wrong", fdoResult.getSystem());
        assertTrue("Metadata Archived wrong", fdoResult.getArchived());
        assertFalse("Metadata Deleted wrong", fdoResult.getDeleted());
        assertTrue("Metadata Writable wrong", fdoResult.getWritable());
        assertTrue("Metadata Readable wrong", fdoResult.getReadable());
        assertFalse("Metadata Executable wrong", fdoResult.getExecutable());
        assertEquals("Metadata Content wrong", "text/plain", fdoResult.getContentType());
        assertEquals("Metadata Declared Size wrong",
                     new Long(12000), fdoResult.getSize());
    }

    public void testUpdateItem_PartialContent() throws Exception {
        String partialFileName = RESOURCES_DIR+"partial_file.jpg";
        splitFile(IMG_00, partialFileName, 0, 1289);
        File   partialFile     = new File(partialFileName);
        // add partially added

        FileDataObjectWrapper fdow = null;
        FileDataObject fdo;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setAccessed("20030807T015500Z");
        fdo.setHidden(false);
        fdo.setSystem(false);
        fdo.setArchived(true);
        fdo.setDeleted(false);
        fdo.setWritable(true);
        fdo.setReadable(true);
        fdo.setExecutable(false);
        fdo.setContentType("text/plain");
        fdo.setSize(30124L);
        //body
        fdo.setBodyFile(partialFile);
        fdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);

        // when parsing the xml this status is set to U
        fdo.setUploadStatus(UploadStatus.UPLOADED);
        String uid = manager.addItem(fdo, null);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Now, the item has upload_status set to U since it was added using the
        // classic add method (added via syncml)
        fdow = manager.getItem(uid);
        assertTrue("Wrong status",fdow.getFileDataObject().isUploaded());

        fdow.getFileDataObject().setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        fdow.getFileDataObject().setBodyFile(null);
        manager.updateItem(fdow);

        // a partial item has been added in a previous sync
        // try to perform the update
        //modify results
        fdo.setAccessed("20100807T042000Z");
        fdo.setBodyFile(null);
        fdo.setUploadStatus("N");
        now = new Timestamp(System.currentTimeMillis());

        FileDataObjectWrapper fdow2 =
            manager.completeAndUpdateItem(uid, fdo, now);
        
        FileDataObject        fdo2   =  fdow2.getFileDataObject();
        assertTrue("Partial item expected",fdo2.isPartiallyUploaded());
        assertEquals("Wrong size",new Long(30124L), fdo2.getSize());
        assertEquals("Wrong size",new Long(1289L), fdow2.getSizeOnStorage());
        assertEquals("Wrong upload status",UploadStatus.PARTIALLY_UPLOADED,fdo2.getUploadStatus());
    }

    public void testUpdateItem_PartialContentNotMergeable_1() throws Exception {
        String partialFileName = RESOURCES_DIR+"partial_file.jpg";
        splitFile(IMG_00, partialFileName, 0, 1289);
        File   partialFile     = new File(partialFileName);
        // add partially added

        FileDataObjectWrapper fdow = null;
        FileDataObject fdo;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setAccessed("20030807T015500Z");
        fdo.setHidden(false);
        fdo.setSystem(false);
        fdo.setArchived(true);
        fdo.setDeleted(false);
        fdo.setWritable(true);
        fdo.setReadable(true);
        fdo.setExecutable(false);
        fdo.setContentType("text/plain");
        fdo.setSize(30124L);
        //body
        fdo.setBodyFile(partialFile);
        fdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);

        fdo.setUploadStatus(UploadStatus.UPLOADED);
        String uid = manager.addItem(fdo, null);

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // Now, the item has upload_status set to U since it was added using the
        // classic add method (added via syncml)
        fdow = manager.getItem(uid);
        assertTrue("Wrong status",fdow.getFileDataObject().isUploaded());

        fdow.getFileDataObject().setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        fdow.getFileDataObject().setBodyFile(null);
        manager.updateItem(fdow);

        // modify results
        fdo.setModified("20100807T041000Z");
        fdo.setAccessed("20100807T042000Z");
        fdo.setBodyFile(null);
        fdo.setUploadStatus("N");
        now = new Timestamp(System.currentTimeMillis());

        // a partial item has been added in a previous sync
        // try to perform the update but items are not MERGEABLE due to the modification
        // date, so the upload status is reset to N

        FileDataObjectWrapper fdow2 =
            manager.completeAndUpdateItem(uid, fdo, now);

        //get the element results
        FileDataObject fdo2 = fdow2.getFileDataObject();
        assertTrue("Partial item expected",fdo2.isUploadNotStarted());
        assertEquals("Wrong size",new Long(30124L), fdo2.getSize());
        assertEquals("Wrong size on storage",new Long(0), fdow2.getSizeOnStorage());
        assertNull("Wrong local name", fdow2.getLocalName());
        assertEquals("Wrong upload status",UploadStatus.NOT_STARTED,fdo2.getUploadStatus());

    }

    public void testCompleteAndUpdateItem_NullInputValues() throws Exception {
        FileDataObjectManager manager = getNewFileDataObjectManager();
        try {
            manager.completeAndUpdateItem(null, null, null);
            fail("Expected IllegalArgumentException when uid is null");
        } catch (IllegalArgumentException e) {
            assertEquals("Wrong exception message.",
                         "Unable to complete and update item providing a null id",
                         e.getMessage());
        }

        try {
            manager.completeAndUpdateItem("id", null, null);
            fail("Expected IllegalArgumentException when uid is null");
        } catch (IllegalArgumentException e) {
            assertEquals("Wrong exception message.",
                         "Unable to complete and update item providing a null FileDataObject",
                         e.getMessage());
        }
    }

    public void testCompleteAndUpdateItem_CompleteIncomingItem() throws Exception {
        FileDataObjectManager manager = getNewFileDataObjectManager();

          // loading fake data
        DBHelper.createDatabaseTester(userId,
                                      RESOURCES_DIR + "dataset-userdb-0.xml",
                                      true);

        copyResouceFileToRootDir("partially_uploaded.tmp");
        copyResouceFileToRootDir("completely_uploaded");

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setCreated ("20100917T121830Z");
        incomingFdo.setModified("20100917T121830Z");
        incomingFdo.setAccessed("20100917T121830Z");
        incomingFdo.setHidden(false);
        incomingFdo.setSystem(false);
        incomingFdo.setArchived(true);
        incomingFdo.setDeleted(false);
        incomingFdo.setWritable(true);
        incomingFdo.setReadable(true);
        incomingFdo.setExecutable(false);
        incomingFdo.setContentType("text/plain");
        incomingFdo.setSize(30124L);

        copyFile(RESOURCES_DIR,
                 WATERFALL_IMG,
                 RESOURCES_DIR,
                 WATERFALL_IMG_1);
        incomingFdo.setBodyFile(new File(IMG_00_TMP));

        // when parsing the xml this status is set to U
        incomingFdo.setUploadStatus(UploadStatus.UPLOADED);

        Timestamp timestamp = new Timestamp(985432L);

        // Updating an item partially uploaded
        FileDataObjectWrapper item = manager.completeAndUpdateItem("1",
                                                                   incomingFdo,
                                                                   timestamp);

        // checking if the temporary file has been renamed
        FileAssert.assertFileNotExist(RESOURCES_DIR, WATERFALL_IMG_1);
        FileAssert.assertFileExist(userFolderPath, item.getLocalName());

        // restoring the old image
        copyFile(RESOURCES_DIR, FLOWER_IMG, RESOURCES_DIR, FLOWER_IMG_1);
        incomingFdo.setBodyFile(new File(IMG_01_TMP));
        timestamp = new Timestamp(9995432L);

        // on the database there is a complete uploaded item
        incomingFdo.setSize(26364L);
        item = manager.completeAndUpdateItem("2", incomingFdo, timestamp);

         // checking if the temporary file has been renamed
        FileAssert.assertFileNotExist(RESOURCES_DIR, FLOWER_IMG_1);
        FileAssert.assertFileExist(userFolderPath, item.getLocalName());

        DBHelper.logTableContent(userId, TABLE_FNBL_FILE_DATA_OBJECT);

        DBHelper.assertEqualsDataSet(
                userConn,
                TABLE_FNBL_FILE_DATA_OBJECT,
                RESOURCES_DIR + "expected-completed-0.xml",
                new String[]{"local_name"});

    }

    public void testCompleteAndUpdateItem_ResumableUpload() throws Exception {
        FileDataObjectManager manager = getNewFileDataObjectManager();

        // loading fake data
        DBHelper.createDatabaseTester(userId,
                                      RESOURCES_DIR + "dataset-userdb-1.xml",
                                      true);

        copyResouceFileToRootDir("partially_uploaded.tmp");
        String partialFileName = userFolderPath + File.separator + "partially_uploaded.tmp";
        splitFile(IMG_00, partialFileName, 0, 2238);

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setHidden(false);
        incomingFdo.setSystem(false);
        incomingFdo.setArchived(true);
        incomingFdo.setDeleted(false);
        incomingFdo.setWritable(true);
        incomingFdo.setReadable(true);
        incomingFdo.setExecutable(false);
        incomingFdo.setContentType("text/plain");
        incomingFdo.setSize(26364L);
        incomingFdo.setBodyFile(null);

        incomingFdo.setUploadStatus(UploadStatus.NOT_STARTED);

        Timestamp timestamp = new Timestamp(985432L);

        // Updating an item partially uploaded
        FileDataObjectWrapper item = 
            manager.completeAndUpdateItem("1", incomingFdo, timestamp);
        assertEquals("Wrong local name",
                     "partially_uploaded.tmp",
                     item.getLocalName());
        assertEquals("Wrong size on storage",
                     new Long(2238),
                     item.getSizeOnStorage());
        assertTrue("Wrong upload status",
                    item.getFileDataObject().isPartiallyUploaded());
        FileAssert.assertFileExist(userFolderPath, item.getLocalName());

        //
        // update the accessed date of the item (even if this is not used for
        // checking if the item is resumable)
        //
        incomingFdo.setAccessed("20100917T121830Z");
        item = manager.completeAndUpdateItem("1", incomingFdo, timestamp);
        assertEquals("Wrong local name",
                     "partially_uploaded.tmp",
                     item.getLocalName());
        assertEquals("Wrong size on storage",
                     new Long(2238),
                     item.getSizeOnStorage());
        assertTrue("Wrong upload status",
                    item.getFileDataObject().isPartiallyUploaded());
        FileAssert.assertFileExist(userFolderPath, item.getLocalName());

        // update the last update timestamp
        timestamp = new Timestamp(9854324444L);
        item = manager.completeAndUpdateItem("2", incomingFdo, timestamp);
        assertEquals("Wrong local name",
                     "partially_uploaded.tmp",
                     item.getLocalName());
        assertEquals("Wrong size on storage",
                     new Long(2238),
                     item.getSizeOnStorage());
        assertTrue("Wrong upload status",
                    item.getFileDataObject().isPartiallyUploaded());
        FileAssert.assertFileExist(userFolderPath, item.getLocalName());

        DBHelper.assertEqualsDataSet(
                userConn,
                TABLE_FNBL_FILE_DATA_OBJECT,
                RESOURCES_DIR + "expected-completed-1.xml",
                new String[]{"local_name","created","modified"});
        
        //
        // update the creation date of the item
        // - if incoming FDO has no set the date, this date is stored using the
        //   current time
        // - if incoming FDO has set the date, this should be the same sent
        //   previously
        //
        incomingFdo.setCreated("20100917T121830Z");
        item = manager.completeAndUpdateItem("1", incomingFdo, timestamp);
        assertEquals("Wrong local name", null, item.getLocalName());

        //
        // update the modification date of the item
        // - if incoming FDO has no set the date, this date is stored using the
        //   current time
        // - if incoming FDO has set the date, this should be the same sent
        //   previously
        //
        incomingFdo.setModified("20100917T121830Z");
        item = manager.completeAndUpdateItem("0", incomingFdo, timestamp);
        assertEquals("Wrong local name", null, item.getLocalName());
    }

    /**
     * Tests method when the incoming item is not complete and is not
     * mergeable.
     *
     * @throws Exception if an error occurs
     */
    public void testCompleteAndUpdateItem_NotResumableUpload()
    throws Exception {

        FileDataObjectManager manager = getNewFileDataObjectManager();

        // loading fake data
        DBHelper.createDatabaseTester(userId,
                                      RESOURCES_DIR + "dataset-userdb-2.xml",
                                      true);

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setHidden(false);
        incomingFdo.setSystem(false);
        incomingFdo.setArchived(true);
        incomingFdo.setDeleted(false);
        incomingFdo.setWritable(true);
        incomingFdo.setReadable(true);
        incomingFdo.setExecutable(false);
        incomingFdo.setContentType("text/plain");
        incomingFdo.setSize(26364L);
        incomingFdo.setBodyFile(null);
        incomingFdo.setUploadStatus(UploadStatus.NOT_STARTED);

        Timestamp timestamp = new Timestamp(985432L);

        // Updating an item completely uploaded but then deleted
        FileDataObjectWrapper item =
            manager.completeAndUpdateItem("10", incomingFdo, timestamp);

        assertNull("Not null local name", item.getLocalName());
        assertEquals("Size on storage not 0",
                     Long.valueOf(0), item.getSizeOnStorage());
        assertEquals("Wrong upload status", 'N',
                     item.getFileDataObject().getUploadStatus());
        FileAssert.assertFileNotExist(userFolderPath, "completely_uploaded");

        // Updating a new item without body
        item = manager.completeAndUpdateItem("11", incomingFdo, timestamp);

        assertNull("Not null local name", item.getLocalName());
        assertEquals("Size on storage not 0",
                     new Long(0), item.getSizeOnStorage());
        assertEquals("Wrong upload status", 'N',
                     item.getFileDataObject().getUploadStatus());
        FileAssert.assertFileNotExist(userFolderPath, "completely_uploaded");

        String partialFileName = userFolderPath + File.separator + "partially_uploaded.tmp";
        copyResouceFileToRootDir("partially_uploaded.tmp");
        //
        // Updating a new item with body partially uploaded: the size specified
        // in the fdo does not match with the one stored
        //
        incomingFdo.setSize(26000L);
        splitFile(IMG_00, partialFileName, 0, 5000);

        item = manager.completeAndUpdateItem("12", incomingFdo, timestamp);

        assertNull("Not null local name", item.getLocalName());
        assertEquals("Size on storage not 0",
                     new Long(0), item.getSizeOnStorage());
        assertEquals("Wrong upload status", 'N',
                     item.getFileDataObject().getUploadStatus());
        FileAssert.assertFileNotExist(userFolderPath, partialFileName);

        //
        // Updating a new item with body partially uploaded: the creation date
        // specified in the fdo does not match with the one stored
        //
        incomingFdo.setSize(5000L);
        incomingFdo.setCreated ("20100920T153030Z");
        copyResouceFileToRootDir("partially_uploaded.tmp");
        splitFile(IMG_00, partialFileName, 0, 5000);

        item = manager.completeAndUpdateItem("13", incomingFdo, timestamp);

        assertNull("Not null local name", item.getLocalName());
        assertEquals("Size on storage not 0",
                     new Long(0), item.getSizeOnStorage());
        assertEquals("Wrong upload status", 'N',
                     item.getFileDataObject().getUploadStatus());
        FileAssert.assertFileNotExist(userFolderPath, partialFileName);

        incomingFdo.setCreated("20100917T121830Z");
        //
        // Updating a new item with body partially uploaded: the modification
        // date specified in the fdo does not match with the one stored
        //
        incomingFdo.setModified("20100919T123030Z");
        copyResouceFileToRootDir("partially_uploaded.tmp");
        splitFile(IMG_00, partialFileName, 0, 5000);

        item = manager.completeAndUpdateItem("14", incomingFdo, timestamp);

        assertNull("Not null local name", item.getLocalName());
        assertEquals("Size on storage not 0",
                     new Long(0), item.getSizeOnStorage());
        assertEquals("Wrong upload status", 'N',
                     item.getFileDataObject().getUploadStatus());
        FileAssert.assertFileNotExist(userFolderPath, partialFileName);

        DBHelper.assertEqualsDataSet(
                userConn,
                TABLE_FNBL_FILE_DATA_OBJECT,
                RESOURCES_DIR + "expected-noresumed-2.xml",
                new String[]{"last_update", "crc",
                             "created", "modified", "accessed",
                             "h", "s", "a", "d", "w","r","x", "cttype"});
    }

    /**
     * Test of addItem method, of class FileDataObjectManager.
     * @throws Exception 
     */
    public void testAddItem_String_ByteArray() throws Exception {
        String sourceDataFileName = "waterfall.jpg";
        File sourceDataFile = new File(RESOURCES_DIR, sourceDataFileName);
        byte[] sourceData = IOTools.readFileBytes(sourceDataFile);

        FileDataObjectManager manager = getNewFileDataObjectManager();

        // execute
        String uid = manager.addItem(sourceDataFileName, sourceData);

        // check results
        FileDataObjectWrapper wrapper = manager.getItem(uid);
        FileDataObject result = wrapper.getFileDataObject();

        assertTrue("File doesn't exist", result.getBodyFile().isFile());

        byte[] storedContent = IOTools.readFileBytes(result.getBodyFile());
        ArrayAssert.assertEquals("Wrong stored content", sourceData, storedContent);

        assertEquals("Wrong name", sourceDataFileName, result.getName());
        assertEquals("Wrong size", sourceData.length, (result.getSize()).intValue());
    }

    /**
     * Test of addItem method, of class FileDataObjectManager.
     * @throws Exception 
     */
    public void testAddItem_String_ByteArray_String() throws Exception {
        String sourceDataFileName = "testfile.txt";
        byte[] content = new byte[1000];
        for (int i = 0; i < content.length; i++) {
            content[i] = 78;
        }
        String contentType = "text/plain";

        FileDataObjectManager manager = getNewFileDataObjectManager();
        
        // execute
        String uid = 
            manager.addItem(sourceDataFileName, content, contentType);

        // check results
        FileDataObjectWrapper wrapper = manager.getItem(uid);
        FileDataObject result = wrapper.getFileDataObject();

        assertTrue("File doesn't exist", result.getBodyFile().isFile());

        byte[] storedContent = IOTools.readFileBytes(result.getBodyFile());
        ArrayAssert.assertEquals("Wrong stored content", content, storedContent);

        assertEquals("Wrong name", sourceDataFileName, result.getName());
        assertEquals("Wrong size", content.length, (result.getSize()).intValue());
        assertEquals("Wrong content type", contentType, result.getContentType());

    }

    public void testUpdateItem_String_FileDataObject_Timestamp() throws Exception {

        String sourceDataFileName = "waterfall.jpg";
        String modifSourceDataFileName = "flower.jpg";
        String tmpFileName = "1234567890.tmp";

        File sourceDataFile = new File(RESOURCES_DIR, sourceDataFileName);
        File modifSourceDataFile = new File(RESOURCES_DIR, modifSourceDataFileName);
        byte[] modifSourceData = IOTools.readFileBytes(modifSourceDataFile);

        File userDir = new File(userFolderPath);
        File tmpFile = new File(userDir, tmpFileName);
        FileUtils.copyFile(sourceDataFile, tmpFile);

        FileDataObjectManager manager = getNewFileDataObjectManager();

        // add item first time
        FileDataObject fdo = createFileDataObject(sourceDataFileName, tmpFileName);
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setAccessed("20030807T015500Z");

        Timestamp now = new Timestamp(System.currentTimeMillis());
        String uid = manager.addItem(fdo, now);
        File previousLocalFile = manager.getItem(uid).getFileDataObject().getBodyFile();

        // execute
        FileUtils.copyFile(modifSourceDataFile, tmpFile);
        FileDataObject modifFdo = createFileDataObject(sourceDataFileName, tmpFileName);
        modifFdo.setCreated ("20030807T231830Z");
        modifFdo.setModified("20030807T012500Z");
        modifFdo.setAccessed("20030807T015500Z");

        Timestamp modifNow = new Timestamp(System.currentTimeMillis());
        manager.completeAndUpdateItem(uid, modifFdo, modifNow);

        // check results
        FileDataObjectWrapper wrapper = manager.getItem(uid);
        FileDataObject result = wrapper.getFileDataObject();

        File modifLocalFile = result.getBodyFile();
        byte[] storedContent = IOTools.readFileBytes(modifLocalFile);
        ArrayAssert.assertEquals("Wrong stored content", modifSourceData, storedContent);

        assertEquals("Wrong name", sourceDataFileName, result.getName());
        assertEquals("Wrong size", modifSourceData.length, (result.getSize()).intValue());

        manager.close();
        assertFalse("Previous temp file still exists", previousLocalFile.isFile());
    }

    public void testUpdateItem_String_byteArr() throws Exception {

        String sourceDataFileName = "waterfall.jpg";
        String modifSourceDataFileName = "flower.jpg";
        String tmpFileName = "1234567890.tmp";

        File sourceDataFile = new File(RESOURCES_DIR, sourceDataFileName);
        File modifSourceDataFile = new File(RESOURCES_DIR, modifSourceDataFileName);
        byte[] modifSourceData = IOTools.readFileBytes(modifSourceDataFile);

        File tmpFile = new File(userFolderPath, tmpFileName);
        FileUtils.copyFile(sourceDataFile, tmpFile);

        FileDataObjectManager manager = getNewFileDataObjectManager();

        // add item first time
        FileDataObject fdo = createFileDataObject(sourceDataFileName, tmpFileName);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        String uid = manager.addItem(fdo, now);

        // execute the update
         manager.updateItem(uid, modifSourceData);

        //check results
        FileDataObject result = manager.getItem(uid).getFileDataObject();

        File modifLocalFile = result.getBodyFile();
        byte[] storedContent = IOTools.readFileBytes(modifLocalFile);
        ArrayAssert.assertEquals("Wrong stored content", modifSourceData, storedContent);

        assertEquals("Wrong name", sourceDataFileName, result.getName());
        assertEquals("Wrong size", modifSourceData.length, (result.getSize()).intValue());
    }

    public void testCheckResumable_BothWrapperNull() throws Throwable {
        FileDataObjectWrapper wrapper1 = null;
        FileDataObjectWrapper wrapper2 = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        try {
            invokeCheckResumable(manager, wrapper1, wrapper2);
            fail("Expected IllegalArgumentException.");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to complete item when incoming wrapper is null.",
                         e.getMessage());
        }
    }

    public void testCheckResumable_IncomingWrapperNull() throws Throwable {
        FileDataObjectWrapper notNullWrapper = null;
        FileDataObjectWrapper wrapper2 = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setSize(11201L);
        //body
        fdo.setBody(null);

        notNullWrapper = new FileDataObjectWrapper("0", userId, fdo);

        try {
            invokeCheckResumable(manager, wrapper2, notNullWrapper);
            fail("Expected IllegalArgumentException.");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to complete item when incoming wrapper is null.",
                         e.getMessage());
        }

    }

    public void testCheckResumable_PersistedWrapperNull() throws Throwable {
        FileDataObjectWrapper notNullWrapper = null;
        FileDataObjectWrapper wrapper2 = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setSize(11201L);
        //body
        fdo.setBody(null);

        notNullWrapper = new FileDataObjectWrapper("0", userId, fdo);

        try {
            invokeCheckResumable(manager,notNullWrapper, wrapper2);
            fail("Expected IllegalArgumentException.");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to complete item when persisted wrapper is null.",
                         e.getMessage());
        }

    }

    public void testCheckResumable_PersistedIsDeleted() throws Throwable {
        FileDataObjectWrapper incomingWrapper = null;
        FileDataObjectWrapper persistedWrapper = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject fdo = new FileDataObject();
        //metadata
        fdo.setName("testPicture.png");
        fdo.setCreated ("20030807T231830Z");
        fdo.setModified("20030807T012500Z");
        fdo.setSize(11201L);
        //body
        fdo.setBody(null);

        incomingWrapper = new FileDataObjectWrapper("0", userId, fdo);

        FileDataObject fdo2 = new FileDataObject();
        //metadata
        fdo2.setName("testPicture.png");
        fdo2.setCreated ("20030807T231830Z");
        fdo2.setModified("20030807T012500Z");
        fdo2.setSize(11201L);
        persistedWrapper = new FileDataObjectWrapper("0", userId, fdo2);
        persistedWrapper.setStatus(SyncItemState.DELETED);

        boolean result = invokeCheckResumable(manager,
                                              incomingWrapper,
                                              persistedWrapper);

        assertFalse("Wrong check resumable result, deleted persisted item",
                    result);
    }

    public void testCheckResumable_PersistedNotPartiallyUploaded() throws Throwable {
        FileDataObjectWrapper incomingWrapper = null;
        FileDataObjectWrapper persistedWrapper = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setCreated ("20030807T231830Z");
        incomingFdo.setModified("20030807T012500Z");
        incomingFdo.setSize(11201L);
        //body
        incomingFdo.setBody(null);

        incomingWrapper = new FileDataObjectWrapper("0", userId, incomingFdo);

        FileDataObject persistedFdo = new FileDataObject();
        //metadata
        persistedFdo.setName("testPicture.png");
        persistedFdo.setCreated ("20030807T231830Z");
        persistedFdo.setModified("20030807T012500Z");
        persistedFdo.setSize(11201L);
        persistedFdo.setUploadStatus(UploadStatus.UPLOADED);
        persistedWrapper = new FileDataObjectWrapper("0", userId, persistedFdo);
        persistedWrapper.setStatus(SyncItemState.NEW);

        boolean result = invokeCheckResumable(manager,
                                              incomingWrapper,
                                              persistedWrapper);

        assertFalse("Wrong check resumable result, persisted item is not partially uploaded",
                    result);
    }

    public void testCheckResumable_ResumableItems() throws Throwable {
        FileDataObjectWrapper incomingWrapper = null;
        FileDataObjectWrapper persistedWrapper = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setSize(11201L);
        //body
        incomingFdo.setBody(null);

        incomingWrapper = new FileDataObjectWrapper("0", userId, incomingFdo);

        FileDataObject persistedFdo = new FileDataObject();
        //metadata
        persistedFdo.setName("testPicture.png");
        persistedFdo.setSize(11201L);
        persistedFdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        persistedWrapper = new FileDataObjectWrapper("0", userId, persistedFdo);
        persistedWrapper.setStatus(SyncItemState.NEW);

        boolean result = invokeCheckResumable(manager,
                                              incomingWrapper,
                                              persistedWrapper);

        assertTrue("Wrong check resumable result, persisted item is partially uploaded and sizes match",
                    result);

        incomingFdo.setCreated ("20030807T231830Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertTrue("Wrong check resumable result, persisted item is partially uploaded and sizes match",
                    result);
        
        persistedFdo.setCreated ("20030807T231830Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertTrue("Wrong check resumable result, persisted item is partially uploaded and sizes match",
                    result);

        incomingFdo.setModified("20030807T012500Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertTrue("Wrong check resumable result, persisted item is partially uploaded and sizes match",
                    result);
        
        persistedFdo.setModified("20030807T012500Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertTrue("Wrong check resumable result, persisted item is partially uploaded and sizes match",
                    result);
    }

    public void testCheckResumable_NotResumableItems() throws Throwable {
        FileDataObjectWrapper incomingWrapper = null;
        FileDataObjectWrapper persistedWrapper = null;

        FileDataObjectManager manager = getNewFileDataObjectManager();

        FileDataObject incomingFdo = new FileDataObject();
        //metadata
        incomingFdo.setName("testPicture.png");
        incomingFdo.setSize(11201L);
        //body
        incomingFdo.setBody(null);

        incomingWrapper = new FileDataObjectWrapper("0", userId, incomingFdo);

        FileDataObject persistedFdo = new FileDataObject();
        //metadata
        persistedFdo.setName("testPicture.png");
        persistedFdo.setSize(11200L);
        persistedFdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        persistedWrapper = new FileDataObjectWrapper("0", userId, persistedFdo);
        persistedWrapper.setStatus(SyncItemState.NEW);

        boolean result = invokeCheckResumable(manager,
                                              incomingWrapper,
                                              persistedWrapper);
        assertFalse("Wrong check resumable result, persisted item is partially uploaded but sizes mismatch",
                    result);

        incomingFdo.setCreated ("20030807T231830Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertFalse("Wrong check resumable result, persisted item is partially uploaded but sizes mismatch",
                    result);

        persistedFdo.setSize(11201L);
        persistedFdo.setCreated ("20050807T231830Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertFalse("Wrong check resumable result, persisted item is partially uploaded but creation date mismatch",
                    result);

        incomingFdo.setModified("20030807T012500Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertFalse("Wrong check resumable result, persisted item is partially uploaded but creation date mismatch",
                    result);

        incomingFdo.setCreated ("20050807T231830Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertTrue("Wrong check resumable result, persisted item is partially uploaded and resumable",
                    result);

        persistedFdo.setModified("20050807T012500Z");
        result = invokeCheckResumable(manager,
                                      incomingWrapper,
                                      persistedWrapper);
        assertFalse("Wrong check resumable result, persisted item is partially uploaded and modification date mismatch",
                    result);
    }
    
    // --------------------------------------------------------- Private methods
    /**
     * Creates a FileDataObject for the given file name.
     * @param fileName
     * @param tmpFileName
     * @return
     */
    private FileDataObject createFileDataObject(String fileName,
                                                String tmpFileName) {

        File pictureFile = new File(userFolderPath, tmpFileName);
        FileDataObjectBody body = new FileDataObjectBody();
        body.setBodyFile(pictureFile);
        FileDataObjectMetadata metadata = new FileDataObjectMetadata(fileName);
        metadata.setSize(Long.valueOf(pictureFile.length()));
        FileDataObject fdo = new FileDataObject(metadata, body);

        return fdo;
    }

    /**
     * Reads the source file from the given lower bound to the upper bound and
     * writes those bytes in the destination file.
     *
     * @param source the source file
     * @param dest the destination file
     * @param from the lower bound
     * @param to the upper bound
     * @throws IOException if the source file cannot be read or the destination
     *                     file cannot be create or write
     */
    private void splitFile(String source, 
                           String dest,
                           long from,
                           long to)
    throws IOException {
        FileInputStream inputFile = new FileInputStream(source);
        FileOutputStream outputFile = new FileOutputStream(dest,false);

        if(from>0) {
            if(inputFile.skip(from)!=from) {
                throw new IOException("Skipped few bytes than expected.");
            }
        }

        byte[] buffer = new byte[1024];
        int  bytesRead   = 0;
        long totalAmount = from;

        while((bytesRead=inputFile.read(buffer))>0) {
            int reallyWrittenBytes = buffer.length;

            if(to<=0) {
                outputFile.write(buffer);
            } else {
                if(totalAmount+bytesRead > to) {
                    reallyWrittenBytes = (int)((to-totalAmount) );
                    outputFile.write(buffer,0,reallyWrittenBytes);
                } else {
                    outputFile.write(buffer);
                }
            }

            totalAmount+=reallyWrittenBytes;
            
            if(totalAmount>=to) {
                break;
            }

        }

        outputFile.flush();
        outputFile.close();
    }

    private boolean invokeCheckResumable(FileDataObjectManager manager,
                                         FileDataObjectWrapper wrapperA,
                                         FileDataObjectWrapper wrapperB)
    throws Throwable {

        Object result = PrivateAccessor.invoke(manager,
                                               "checkResumable" ,
                                               new Class[]{FileDataObjectWrapper.class,
                                                           FileDataObjectWrapper.class},
                                               new Object[]{wrapperA,
                                                            wrapperB});
        if(result!=null && result instanceof Boolean) {
            return (Boolean)result;
        } else {
            throw new RuntimeException("Unexpected result object.");
        }
    }

    private void copyResouceFileToRootDir(String fileName)
    throws IOException {

        String destDir = userFolderPath;
        copyFile(RESOURCES_DIR, fileName, destDir, fileName);
    }

    private void copyFile(String sourceDirName,
                          String sourceFileName,
                          String targetDirName,
                          String targetFileName)
    throws IOException {

        File sourceFile = new File(sourceDirName, sourceFileName);
        if(!sourceFile.exists() || ! sourceFile.isFile()) {
            throw new IOException("Source file '"+sourceFileName+"' doesn't exist.");
        }

        File targetDir = new File(targetDirName);
        if(!targetDir.exists()) {
            if(!targetDir.mkdirs()) {
                throw new IOException("Error creating target directory '"+targetDirName+"'.");
            }
        }

        File targetFile = new File(targetDir, targetFileName);

        InputStream inputStream = new FileInputStream(sourceFile);
        OutputStream outputStream = new FileOutputStream(targetFile,false);
        try {
            IOUtils.copy(inputStream, outputStream);
        } finally {
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }
    }

    private FileDataObjectManager getNewFileDataObjectManager()
    throws IOException, DAOException {
        return new FileDataObjectManager(
            userId,
            sourceURI,
            jcloudRootPath ,
            localRootPath ,
            1000000,
            100000,
            0,
            JCLOUDS_PROVIDER,
            JCLOUDS_IDENTITY,
            JCLOUDS_CREDENTIAL,
            JCLOUDS_CONTAINER_NAME);
    }

}