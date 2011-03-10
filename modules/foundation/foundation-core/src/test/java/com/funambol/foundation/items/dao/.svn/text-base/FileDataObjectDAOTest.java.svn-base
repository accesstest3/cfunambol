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

package com.funambol.foundation.items.dao;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.TimeZone;

import junit.framework.TestCase;

import junitx.framework.ArrayAssert;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.media.file.UploadStatus;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.tools.IOTools;

import com.funambol.tools.database.DBHelper;

import com.funambol.foundation.engine.source.FileDataObjectSyncSource;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.exception.MaxFileSizeException;
import com.funambol.foundation.exception.QuotaOverflowException;
import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.FileAssert;
import com.funambol.foundation.util.FileDataObjectNamingStrategy;
import com.funambol.foundation.util.FileSystemDAOHelper;
import com.funambol.foundation.util.TestDef;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;

/**
 * Test class for <code>FileDataObjectDAO</code>.
 *
 * @version $Id$
 */
public class FileDataObjectDAOTest extends TestCase implements TestDef {

    // ------------------------------------------------------------ Private data
    private static String user = DBHelper.USER_PART1;
    private static String sourceURI = "fdo";
    private static String userBase64 = "dXNlcm5hbWUx";

    private static TimeZone defaulTimeZone = TimeZone.getDefault();

    private String jcloudRootPath =
        "target/test-classes/data/com/funambol/foundation/items/dao/FileDataObjectDAO";
    private String localRootPath =
        "target/test-classes/data/com/funambol/foundation/items/dao/FileDataObjectDAO" + File.separator + CONTAINER_NAME;
    private String userPath =
        localRootPath + File.separator + userBase64 + File.separator;

    private static final String testResourcesDir =
        TEST_RESOURCE_BASEDIR +
        "/data/com/funambol/foundation/items/dao/FileDataObjectDAO/";

    private static final String JCLOUDS_PROVIDER = "filesystem";
    private static final String CONTAINER_NAME = "container-test";
    private static final String ACCESS_KEY = "";
    private static final String SECRETE_KEY = "";

    private static final String TABLE_FNBL_FILE_DATA_OBJECT =
        "fnbl_file_data_object";

    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE,
                                                      USER_SCHEMA_SOURCE,
                                                      false);

            //assertTrue checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception e) {
            assertFalse("Cannot arrive here!", true);
        }
    }

    private FileDataObjectDAO daoWithLimits;
    private DatabaseConnection userConn = null;
    private IDatabaseTester userDatabaseTester = null;


    // ------------------------------------------------------------ Constructors
    public FileDataObjectDAOTest(String testName) throws Exception {
        super(testName);

        daoWithLimits = getNewFileDataObjectDao(1000,3000);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        userConn = new DatabaseConnection(DBHelper.getUserConnection(user));
        userDatabaseTester  = DBHelper.createDatabaseTester(
            userConn.getConnection(),
            testResourcesDir + "init-dataset-userdb.xml",
            true);

        // it's needed to reopen the connection since the previous assert
        // close it
        userConn = new DatabaseConnection(DBHelper.getUserConnection(user));

        TimeZone.setDefault(TimeUtils.TIMEZONE_UTC);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        TimeZone.setDefault(defaulTimeZone);

        daoWithLimits.removeAllItems();
    }

    // -------------------------------------------------------------- Test cases
    public void testGetItemMetadata() throws Exception {
        FileDataObjectDAO dao = getNewFileDataObjectDao(100000, 100000);
        dao.removeAllItems(null);

        FileDataObjectWrapper fdow = createDummyFileDataObjectWrapper(0);
        fdow.getFileDataObject().setReadable(true);
        fdow.getFileDataObject().setExecutable(false);
        fdow.getFileDataObject().setContentType("text/plain");
        //add the item
        dao.addItem(fdow);
        String uid = fdow.getId();

        //and retrieve only it's metadata
        FileDataObjectWrapper result = dao.getItemMetadata(uid);
        assertEquals("testfile.txt", result.getFileDataObject().getName());
        assertEquals(new Long(1000L), result.getSize());
        assertEquals("text/plain", result.getFileDataObject().getContentType());
        assertTrue(result.getFileDataObject().getReadable());
        assertFalse(result.getFileDataObject().getExecutable());
        assertNull(result.getFileDataObject().getBodyFile());
    }

    public void testCheckSizeAndQuotaLimit() throws Exception {
        FileDataObjectDAO daoWithNoLimits = getNewFileDataObjectDao(
                FileDataObjectSyncSource.NO_QUOTA_LIMIT, FileDataObjectSyncSource.NO_QUOTA_LIMIT);


        // invokation with null parameters
        try {
            daoWithNoLimits.checkSizeAndQuotaLimit(null, null);
            fail("Expected exception when invoking the method with a null input parameter.");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to check item size and quota limit for a null incoming item.",
                         e.getMessage());
        }


        FileDataObject incomingFdo,persistedFdo;


        incomingFdo = new FileDataObject();
        incomingFdo.setSize(null);

        try {
            daoWithNoLimits.checkSizeAndQuotaLimit(incomingFdo, null);
            fail("Expected exception when invoking the method with an incoming fdo with no size");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "File size is undefined.",
                         e.getMessage());

        }

        // Checking with a bin incoming size
        incomingFdo.setSize(Long.MAX_VALUE);
        daoWithNoLimits.checkSizeAndQuotaLimit(incomingFdo, null);


        persistedFdo = new FileDataObject();


        try {
            daoWithNoLimits.checkSizeAndQuotaLimit(incomingFdo, persistedFdo);
            fail("Expected exception when invoking the method with a persisted fdo with no size");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Old file size is undefined.",
                         e.getMessage());

        }


        // Checking with both incoming and pesisted big size
        persistedFdo.setSize(Long.MAX_VALUE);
        daoWithNoLimits.checkSizeAndQuotaLimit(incomingFdo, persistedFdo);


        // Checking an incoming fdo that breaks the max size limit
        incomingFdo.setSize(1001L);
        try {
            daoWithLimits.checkSizeAndQuotaLimit(incomingFdo, null);
            fail("Expecting DAOException since max file size limit has been exceeded");
        } catch(MaxFileSizeException e) {
            assertEquals("Wrong exception message",
                         "File size exceeds limit The size of this file (1001b) exceeds the 1000-byte limit.",
                         e.getMessage());
        }

        // Adding a fake fdo, just to reduce the amount of free storage
        incomingFdo.setName("testPicture.png");
        incomingFdo.setCreated ("20030807T231830Z");
        incomingFdo.setModified("20030807T012500Z");
        incomingFdo.setAccessed("20030807T015500Z");
        incomingFdo.setHidden(false);
        incomingFdo.setSystem(false);
        incomingFdo.setArchived(true);
        incomingFdo.setDeleted(false);
        incomingFdo.setWritable(true);
        incomingFdo.setReadable(true);
        incomingFdo.setExecutable(false);
        incomingFdo.setContentType("text/plain");
        incomingFdo.setSize(2424L);
        incomingFdo.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        FileDataObjectWrapper wrapper = new FileDataObjectWrapper("1000", user, incomingFdo);
        //..using dao with no limits :-)
        daoWithNoLimits.addItemMetadata(wrapper);

        // Checking an incoming fdo that breaks the quota size limit
        incomingFdo.setSize(800L);
        try {
            daoWithLimits.checkSizeAndQuotaLimit(incomingFdo, null);
            fail("Expecting DAOException since quota limit has been exceeded");
        } catch(QuotaOverflowException e) {
            assertEquals("Wrong exception message",
                         "576/3000",
                         e.getMessage());
        }

        // Checking an incoming fdo that breaks the quota size limit
        persistedFdo.setSize(50L);
        try {
            daoWithLimits.checkSizeAndQuotaLimit(incomingFdo, null);
            fail("Expecting DAOException since quota limit has been exceeded");
        } catch(QuotaOverflowException e) {
            assertEquals("Wrong exception message",
                         "576/3000",
                         e.getMessage());
        }


        // Checking an incoming fdo that is gouing to updare a bigger pesisted fdo
        persistedFdo.setSize(600L);
        daoWithLimits.checkSizeAndQuotaLimit(incomingFdo, persistedFdo);

   }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItem_tooLarge() throws Exception {

        byte[] bytes = new byte[1000];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 78;
        }

        FileDataObjectBody body = createFileDataObjectBody(bytes);

        FileDataObjectMetadata metadata = new FileDataObjectMetadata("testfile.txt");
        metadata.setSize(1000L);
        FileDataObject fdo = new FileDataObject(metadata, body);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(daoWithLimits.getNextID(), "test", fdo);
        FileDataObjectDAO dao = getNewFileDataObjectDao(100, 100000);

        dao.removeAllItems(null);

        try {
            dao.addItem(fdow);
            fail("The item is too large but no MaxFileSizeException has been raised");
        } catch (MaxFileSizeException e) {
            assertEquals(100, e.getLimit());
            assertEquals(1000, e.getSize());
        } catch (Exception e) {
            fail("The item is too large, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a MaxFileSizeException");
        }

    }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItemMetadata_tooLarge() throws Exception {
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("testfile.txt");
        metadata.setSize(1000L);
        FileDataObject fdo = new FileDataObject(metadata, null);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(daoWithLimits.getNextID(), "test", fdo);
        FileDataObjectDAO dao = getNewFileDataObjectDao(100, 100000);

        dao.removeAllItems(null);

        try {
            dao.addItemMetadata(fdow);
            fail("The item is too large but no MaxFileSizeException has been raised");
        } catch (MaxFileSizeException e) {
            assertEquals(100, e.getLimit());
            assertEquals(1000, e.getSize());
        } catch (Exception e) {
            fail("The item is too large, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a MaxFileSizeException");
        }

    }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItem_sizeOK() throws Exception {

        byte[] bytes = new byte[1000];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = 78;
        }

        FileDataObjectBody body = createFileDataObjectBody(bytes);

        FileDataObjectMetadata metadata = new FileDataObjectMetadata("testfile.txt");
        metadata.setSize(1000L);
        FileDataObject fdo = new FileDataObject(metadata, body);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(daoWithLimits.getNextID(), "test", fdo);
        fdow.setLocalName(new FileDataObjectNamingStrategy(8).createFileName(fdow.getId()));
        FileDataObjectDAO dao = getNewFileDataObjectDao(10000, 100000);

        dao.removeAllItems(null);

        dao.addItem(fdow); // No exception should be raised
        FileDataObjectWrapper fdow2 = dao.getItem(fdow.getId());
        assertEquals(1000,
                     fdow2.getFileDataObject().getBodyFile().length());
    }

    /**
     * Test of updateItem method, of class FileDataObjectDAO.
     */
    public void testGetFreeStorageSpace() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        for (int j = 0; j < 5; j++) {
            dao.addItem(createDummyFileDataObjectWrapper(j));
        }
        assertEquals(7000, dao.getAvailableStorageSpace());

    }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItem_quotaOverflow() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 4500);

        dao.removeAllItems(null);

        // N.B each item has size 1000
        dao.addItem(fdows[0]); // No exception should be raised
        dao.addItem(fdows[1]); // No exception should be raised
        dao.addItem(fdows[2]); // No exception should be raised
        dao.addItem(fdows[3]); // No exception should be raised
        try {
            dao.addItem(fdows[4]); // Quota overflow!
            fail("The total size of the items exceeds the user quota but no " +
                 "QuotaOverflowException has been raised");
        } catch (QuotaOverflowException e) {
            assertEquals(4500, e.getMaxQuota());
            assertEquals(4000, e.getUsedQuota());
            assertEquals(1000, e.getItemSize());
        } catch (Exception e) {
            fail("The item is too large, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a QuotaOverflowException");
        }

    }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItemMetadata_quotaOverflow() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapperWithoutBody(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 4500);

        dao.removeAllItems(null);

        // N.B each item has size 1000
        dao.addItemMetadata(fdows[0]); // No exception should be raised
        dao.addItemMetadata(fdows[1]); // No exception should be raised
        dao.addItemMetadata(fdows[2]); // No exception should be raised
        dao.addItemMetadata(fdows[3]); // No exception should be raised
        try {
            dao.addItemMetadata(fdows[4]); // Quota overflow!
            fail("The total size of the items exceeds the user quota but no " +
                 "QuotaOverflowException has been raised");
        } catch (QuotaOverflowException e) {
            assertEquals(4500, e.getMaxQuota());
            assertEquals(4000, e.getUsedQuota());
            assertEquals(1000, e.getItemSize());
        } catch (Exception e) {
            fail("The item is too large, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a QuotaOverflowException");
        }

    }

    /**
     * Test of updateItem method, of class FileDataObjectDAO.
     */
    public void testUpdateItem_quotaOverflow() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(10000, 10000);

        dao.removeAllItems(null);

        // N.B each item has size 1000
        dao.addItem(fdows[0]); // No exception should be raised
        dao.addItem(fdows[1]); // No exception should be raised
        dao.addItem(fdows[2]); // No exception should be raised
        dao.addItem(fdows[3]); // No exception should be raised
        dao.addItem(fdows[4]); // No exception should be raised

        assertEquals(5000, dao.getAvailableStorageSpace());

        byte[] moreBytes = new byte[7000];
        for (int i = 0; i < moreBytes.length; i++) {
            moreBytes[i] = (byte) 99;
        }
        FileDataObjectBody moreBody = createFileDataObjectBody(moreBytes);

        fdows[4].getFileDataObject().setBody(moreBody);
        fdows[4].getFileDataObject().setSize(7000L);
        fdows[4].setLastUpdate(new Timestamp(System.currentTimeMillis()));
        fdows[4].setStatus('U');
        try {
            dao.completeWrapper(fdows[4]); // Quota overflow because
                                      // 5000 - 1000 + 7000 > 10000
            fail("The total size of the items exceeds the user quota but no " +
                 "QuotaOverflowException has been raised");
        } catch (QuotaOverflowException e) {
            assertEquals(10000, e.getMaxQuota());
            assertEquals(4000, e.getUsedQuota());
            assertEquals(7000, e.getItemSize());
        } catch (Exception e) {
            fail("The item is too large, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a QuotaOverflowException");
        }
    }

    public void testAddItem_failWhenBodyNull() throws Exception {
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 100000);

        FileDataObjectWrapper fdow =
            createDummyFileDataObjectWrapperWithoutBody(1);

        try {
            dao.addItem(fdow);
            fail("Cannot add an item without body");
        } catch (DAOException e) {
            //it's ok
        } catch (Exception e) {
            fail("Cannot add an item without body, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a DaoException");
        }
    }

    public void testRemoveItem_NullUid() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // Removing item providing a null uid
        try {
            dao.removeItem(null, null);
            fail("Expected IllegalArgumentException.");
        } catch(IllegalArgumentException e) {
            assertEquals("Wrong exception message",
                         "Unable to remove an item providing a null uid",
                         e.getMessage());
        }
    }

    public void testRemoveItem_NotExistingItem() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // Removing item providing a not existing uid
        try {
            dao.removeItem("4", null);
            fail("Expected DAOException.");
        } catch(DAOException e) {
            assertEquals("Wrong exception message",
                         "No local name found, item with uid '4' doesn't exist.",
                         e.getMessage());
        }

    }

    public void testRemoveItem_DeletedItemNoLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-11.xml",
                                      true);

        // Deleting an item alredy deleted item with no local name
        Timestamp timestamp = new Timestamp(4444444444L);
        dao.removeItem("1000", timestamp);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-deleted-1.xml");
    }


    public void testRemoveItem_DeletedItemNotExistingLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-11.xml",
                                      true);

        // Deleting an item alredy deleted item with not existing local name
        Timestamp timestamp = new Timestamp(5555555555L);
        dao.removeItem("1001", timestamp);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-deleted-2.xml");
    }

    public void testRemoveItem_DeletedItemWithExistingLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-11.xml",
                                      true);

        // Deleting an item alredy deleted item with existing local name

        String realPath = userPath + jcloudRootPath;
        try {
            createFile(realPath, "existingfile");
            FileAssert.assertFileExist(realPath, "existingfile");

            Timestamp timestamp = new Timestamp(6666666666L);
            dao.removeItem("1002", timestamp);

            DBHelper.assertEqualsDataSet(
                userConn,
                TABLE_FNBL_FILE_DATA_OBJECT,
                testResourcesDir + "exp-deleted-3.xml");

            FileAssert.assertFileNotExist(realPath,"existingFile");
        } finally {
            removeQuietly(realPath,"existingfile");
        }
    }

        public void testRemoveItem_NoLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-12.xml",
                                      true);

        // Deleting an item alredy deleted item with no local name
        Timestamp timestamp = new Timestamp(4444444444L);
        dao.removeItem("1000", timestamp);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-deleted-4.xml");
    }


    public void testRemoveItem_NotExistingLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-12.xml",
                                      true);

        // Deleting an item alredy deleted item with not existing local name
        Timestamp timestamp = new Timestamp(5555555555L);
        dao.removeItem("1001", timestamp);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-deleted-5.xml");
    }

    public void testRemoveItem_ExistingLocalName() throws Exception {

        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        // loading fake data
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-12.xml",
                                      true);

        // Deleting an item alredy deleted item with existing local name

        String realPath = userPath + jcloudRootPath;
        try {
            createFile(realPath,"existingfile");
            FileAssert.assertFileExist(realPath,"existingfile");

            Timestamp timestamp = new Timestamp(6666666666L);
            dao.removeItem("1002", timestamp);

            DBHelper.assertEqualsDataSet(
                userConn,
                TABLE_FNBL_FILE_DATA_OBJECT,
                testResourcesDir + "exp-deleted-6.xml");


            FileAssert.assertFileNotExist(realPath,"existingFile");
        } finally {
            removeQuietly(realPath,"existingfile");
        }
    }

    /**
     * Test of addItem method, of class FileDataObjectDAO.
     */
    public void testAddItem_quotaOK() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 100000);

        dao.removeAllItems(null);

        dao.addItem(fdows[0]); // No exception should be raised
        dao.addItem(fdows[1]); // No exception should be raised
        dao.addItem(fdows[2]); // No exception should be raised
        dao.addItem(fdows[3]); // No exception should be raised
        dao.addItem(fdows[4]); // No exception should be raised
        assertEquals(95000, dao.getAvailableStorageSpace());
    }


    public void testAddItemMetadata_quotaOk() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapperWithoutBody(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 100000);

        dao.removeAllItems(null);

        dao.addItemMetadata(fdows[0]);
        dao.addItemMetadata(fdows[1]);
        dao.addItemMetadata(fdows[2]);
        dao.addItemMetadata(fdows[3]);
        dao.addItemMetadata(fdows[4]);
        assertEquals(95000, dao.getAvailableStorageSpace());
    }

    /**
     * Test of updateItem method, of class FileDataObjectDAO.
     */
    public void testUpdateItem_quotaOK() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        dao.addItem(fdows[0]); // No exception should be raised
        dao.addItem(fdows[1]); // No exception should be raised
        dao.addItem(fdows[2]); // No exception should be raised
        dao.addItem(fdows[3]); // No exception should be raised
        dao.addItem(fdows[4]); // No exception should be raised

        assertEquals(7000, dao.getAvailableStorageSpace());

        byte[] moreBytes = new byte[7000];
        for (int i = 0; i < moreBytes.length; i++) {
            moreBytes[i] = (byte) 99;
        }
        FileDataObjectBody moreBody = createFileDataObjectBody(moreBytes);

        fdows[4].getFileDataObject().setBody(moreBody);
        fdows[4].getFileDataObject().setSize(7000L);
        fdows[4].getFileDataObject().setName("testfile4.modified.txt");
        fdows[4].setLastUpdate(new Timestamp(System.currentTimeMillis()));
        fdows[4].setStatus('U');

        dao.updateItem(fdows[4]); // No quota overflow because
                                  // 5000 - 1000 + 7000 < 12000
        assertEquals(1000, dao.getAvailableStorageSpace());

    }

    public void testUpdateItemMetadata_quotaOK() throws Exception {

        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        dao.addItem(fdows[0]); // No exception should be raised
        dao.addItem(fdows[1]); // No exception should be raised
        dao.addItem(fdows[2]); // No exception should be raised
        dao.addItem(fdows[3]); // No exception should be raised
        dao.addItem(fdows[4]); // No exception should be raised

        assertEquals(7000, dao.getAvailableStorageSpace());

        fdows[4].getFileDataObject().setBody(null);
        fdows[4].getFileDataObject().setSize(7000L);
        fdows[4].getFileDataObject().setName("testfile4.modified.txt");
        fdows[4].setLastUpdate(new Timestamp(System.currentTimeMillis()));
        fdows[4].setStatus('U');

        dao.updateItemMetadata(fdows[4]); // No quota overflow because
                                  // 5000 - 1000 + 7000 < 12000
        assertEquals(1000, dao.getAvailableStorageSpace());

    }

    public void testUpdateItemMetadata_LocalNameIsNullAfterUpdatingDeletedItem() throws Exception {

        FileDataObjectWrapper fdow = createDummyFileDataObjectWrapper(1);
        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        // ad an item
        dao.addItem(fdow);

        // remove the item
        dao.removeItem(fdow.getId(), new Timestamp(System.currentTimeMillis()));

        // verify the local file name of the stored item is NOT null
        FileDataObjectWrapper currentFdow = dao.getItem(fdow.getId());
        assertNotNull(currentFdow.getLocalName());
        assertEquals(SyncItemState.DELETED, currentFdow.getStatus());

        FileDataObjectWrapper updatedFdow = createDummyFileDataObjectWrapperWithoutBody(1);
        updatedFdow.setId(fdow.getId());
//        updatedFdow.setLocalName(new FileDataObjectNamingStrategy(8).createFileName(fdow.getId()));

        // update the deleted item
        dao.updateItemMetadata(updatedFdow);

        // verify the local file name of the stored item is NOT null
        currentFdow = dao.getItem(fdow.getId());
        assertNull(currentFdow.getLocalName());
        assertEquals(SyncItemState.UPDATED, currentFdow.getStatus());
    }

    public void testCompleteItemMetadata_LocalNameIsNOTNullAfterUpdatingNewItem() throws Exception {

        FileDataObjectWrapper fdow = createDummyFileDataObjectWrapper(1);
        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        // ad an item
        dao.addItem(fdow);

        // verify the local file name of the stored item is NOT null
        FileDataObjectWrapper currentFdow = dao.getItem(fdow.getId());
        assertNotNull(currentFdow.getLocalName());
        assertEquals(SyncItemState.NEW, currentFdow.getStatus());

        FileDataObjectWrapper updatedFdow = createDummyFileDataObjectWrapper(1);
        updatedFdow.setId(fdow.getId());

        // update the deleted item
        dao.completeWrapper(updatedFdow);
        dao.updateItemMetadata(updatedFdow);

        // verify the local file name of the stored item is NOT null
        currentFdow = dao.getItem(fdow.getId());
        assertNotNull(currentFdow.getLocalName());
        assertEquals(SyncItemState.UPDATED, currentFdow.getStatus());
    }

    public void testUpdateItem_failWhenBodyNull() throws Exception {
        FileDataObjectDAO dao = getNewFileDataObjectDao(1500, 100000);

        FileDataObjectWrapper fdow = createDummyFileDataObjectWrapperWithoutBody(1);

        try {
            dao.updateItem(fdow);
            fail("Cannot update an item without body");
        } catch (DAOException e) {
            //it's ok
        } catch (Exception e) {
            fail("Cannot update an item without body, an exception of class " +
                 e.getClass().toString() + " has been raised but it should " +
                 "have been a DaoException");
        }
    }


    /**
     * Test of updateItem method, of class FileDataObjectDAO, with byte array as
     * parameter.
     */
    public void testUpdateItem_ByteArray() throws Exception {

        // Set up for testing.
        FileDataObjectDAO dao = getNewFileDataObjectDao(12000, 12000);

        dao.removeAllItems(null);

        // add five items. the last one will be than updated
        FileDataObjectWrapper[] fdows = new FileDataObjectWrapper[5];
        for (int j = 0; j < 5; j++) {
            fdows[j] = createDummyFileDataObjectWrapper(j);
        }
        dao.addItem(fdows[0]);
        dao.addItem(fdows[1]);
        dao.addItem(fdows[2]);
        dao.addItem(fdows[3]);
        dao.addItem(fdows[4]);

        assertTrue(fdows[4].getFileDataObject().isUploaded());

        assertEquals(7000, dao.getAvailableStorageSpace()); // 12000 - 5000


        // Call the method being tested.
        byte[] updatedContent = new byte[7000];
        for (int i = 0; i < updatedContent.length; i++) {
            updatedContent[i] = (byte) 99;
        }
        dao.updateItem(fdows[4].getId(), updatedContent);

        // Verify that the results are as expected
        assertEquals(1000, dao.getAvailableStorageSpace());
        // No quota overflow because 5000 - 1000 + 7000 < 12000

        FileDataObjectWrapper result = dao.getItem(fdows[4].getId());
        assertTrue(result.isComplete());
        assertEquals(new Long(7000L), result.getSize());
        assertEquals(new Long(7000L), result.getSizeOnStorage());
        assertNull(result.getFileDataObject().getContentType());
        assertEquals("testfile4.txt", result.getFileDataObject().getName());
        assertTrue(result.getFileDataObject().isUploaded());
        assertFalse(result.getFileDataObject().isPartiallyUploaded());
        assertFalse(result.getFileDataObject().isUploadNotStarted());

        InputStream input = new FileInputStream(result.getFileDataObject().getBodyFile());
        byte[] resultContent = new byte[7000];
        input.read(resultContent);
        ArrayAssert.assertEquals(updatedContent, resultContent);
        input.close();

        // Clean up
        dao.removeAllItems(null);

    }

    // --------------------------------------------------------- Private methods

    private FileDataObjectBody createFileDataObjectBody(byte[] bytes) throws IOException {
        File tmp = File.createTempFile("pre", "post", new File("target"));
        tmp.deleteOnExit();
        IOTools.writeFile(bytes, tmp);

        FileDataObjectBody body = new FileDataObjectBody();
        body.setBodyFile(tmp);

        return body;
    }

    private FileDataObjectWrapper createDummyFileDataObjectWrapper(int j) throws Exception {
        byte[] bytes = new byte[1000];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (79 + j);
        }
        FileDataObjectBody body = createFileDataObjectBody(bytes);

        FileDataObjectMetadata metadata = new FileDataObjectMetadata("testfile" + (j!=0?j:"") + ".txt");
        metadata.setSize(1000L);
        FileDataObject fdo = new FileDataObject(metadata, body);
        fdo.setUploadStatus(UploadStatus.UPLOADED);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(daoWithLimits.getNextID(), "test", fdo);
        fdow.setLocalName(body.getBodyFile().getName());
        return fdow;
    }

    private FileDataObjectWrapper createDummyFileDataObjectWrapperWithoutBody(int j) throws Exception {

        FileDataObjectMetadata metadata = new FileDataObjectMetadata("testfile" + (j!=0?j:"") + ".txt");
        metadata.setSize(1000L);
        FileDataObject fdo = new FileDataObject(metadata, null);
        fdo.setUploadStatus(UploadStatus.NOT_STARTED);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(daoWithLimits.getNextID(), "test", fdo);
        return fdow;
    }

    private void createFile(String parentName, String fileName) throws Exception {
        File parent = new File (parentName);
        if(!parent.exists()) {
            if(!parent.mkdirs()) {
                throw new Exception("Error creating path '"+parentName+"'.");
            }
        }
        File file = new File(parentName, fileName);

        FileOutputStream output = new FileOutputStream(file);
        String fileContent = "no more time to sing";

        output.write(fileContent.getBytes());
        output.flush();
        output.close();
    }

    private void removeQuietly(String realPath, String name) {
        File file = new File(realPath,name);
        if(file.exists() && file.isFile()) {
            file.delete();
        }
    }

    private FileDataObjectDAO getNewFileDataObjectDao(long maxsize, long quotaPerUser)
    throws IOException, DAOException {
        FileDataObjectNamingStrategy namingStrategy = new FileDataObjectNamingStrategy(0);
        FileSystemDAOHelper helper = new FileSystemDAOHelper(user, localRootPath, namingStrategy);
        return new FileDataObjectDAO(
            namingStrategy,
            helper,
            user     ,
            sourceURI,
            jcloudRootPath,
            maxsize,
            quotaPerUser,
            JCLOUDS_PROVIDER, ACCESS_KEY, SECRETE_KEY, CONTAINER_NAME); // dir depth
    }
}
