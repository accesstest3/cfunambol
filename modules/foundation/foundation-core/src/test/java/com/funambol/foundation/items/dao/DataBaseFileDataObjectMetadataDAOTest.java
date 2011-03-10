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
import java.text.ParseException;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.media.file.UploadStatus;
import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SourceUtils;

import com.funambol.tools.database.DBHelper;

import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.TestDef;

/**
 * Test class for <code>FileDataObjectDAO</code>.
 * @version $Id: DataBaseFileDataObjectMetadataDAOTest.java 36704 2011-03-07 11:50:01Z ubertu $
 */
public class DataBaseFileDataObjectMetadataDAOTest
extends TestCase
implements TestDef {

    public static final String ONE = "1";

    // ------------------------------------------------------------ Private data
    private static final String testResourcesDir =
        TEST_RESOURCE_BASEDIR +
        "/data/com/funambol/foundation/items/dao/FileDataObjectDAO/";

    private DatabaseConnection userConn = null;
    private IDatabaseTester userDatabaseTester = null;

    private static final String TABLE_FNBL_FILE_DATA_OBJECT =
        "fnbl_file_data_object";
    private static final String TABLE_FNBL_FILE_DATA_OBJECT_PROPERTY =
        "fnbl_file_data_object_property";

    private DataBaseFileDataObjectMetadataDAO fileDataObjectMetadataDAO = null;
    private static String user = DBHelper.USER_PART1;
    private static String sourceURI = "fdo";

    private static TimeZone defaulTimeZone = TimeZone.getDefault();

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

    // ------------------------------------------------------------ Constructors
    public DataBaseFileDataObjectMetadataDAOTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        userConn = new DatabaseConnection(DBHelper.getUserConnection(user));
        userDatabaseTester = DBHelper.createDatabaseTester(
            userConn.getConnection(),
            testResourcesDir + "init-dataset-userdb.xml",
            true);

        // it's needed to reopen the connection since the previous assert
        // close it
        userConn = new DatabaseConnection(DBHelper.getUserConnection(user));

        TimeZone.setDefault(TimeUtils.TIMEZONE_UTC);

        fileDataObjectMetadataDAO = 
            new DataBaseFileDataObjectMetadataDAO(user, sourceURI);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        TimeZone.setDefault(defaulTimeZone);
        DBHelper.closeConnection(userConn);
        userDatabaseTester = null;
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Add a file data object with every field filled.
     * Add a file data object with minimum not null field filled.
     * @throws Throwable
     */
    public void testAddItem() throws Throwable {

        FileDataObject fdo1 = new FileDataObject();
        fdo1.setName("truename1.jpg");
        fdo1.setCrc(SourceUtils.computeCRC("content".getBytes()));
        fdo1.setCreated("20090422T091942Z");
        fdo1.setModified("20090422T104232Z");
        fdo1.setAccessed("20090422T232400Z");
        fdo1.setHidden(true);
        fdo1.setSystem(false);
        fdo1.setArchived(true);
        fdo1.setDeleted(false);
        fdo1.setWritable(true);
        fdo1.setReadable(false);
        fdo1.setExecutable(true);
        fdo1.setContentType("image/jpeg");
        fdo1.setSize(1234L);

        FileDataObject fdo2 = new FileDataObject();
        fdo2.setName("truename2.jpg");
        fdo2.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        assertFalse(fdo2.hasBodyFile());

        FileDataObjectWrapper fdow1 = new FileDataObjectWrapper("0", user, fdo1);
        fdow1.setLastUpdate(new Timestamp(123456));
        fdow1.setLocalName("localname1");

        FileDataObjectWrapper fdow2 = new FileDataObjectWrapper( ONE, user, fdo2);
        fdow2.setLastUpdate(new Timestamp(789000));

        fileDataObjectMetadataDAO.addItem(fdow1);
        fileDataObjectMetadataDAO.addItem(fdow2);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-0.xml",
            new String[]{"created","modified"});
        
        FileDataObjectWrapper fdow2res =
            fileDataObjectMetadataDAO.getItem("1");
        assertTrue("Created date not set",
                    !fdow2res.getFileDataObject().getCreated().isEmpty());

        assertTrue("Modified date not set",
                   !fdow2res.getFileDataObject().getCreated().isEmpty());
    }

    /**
     * Add a file data object with empty/null local file name.
     * @throws Throwable
     */
    public void testAddItem_LocalNameNullEmpty() throws Throwable {

        FileDataObject fdo1 = new FileDataObject();
        fdo1.setName("file01.jpg");

        FileDataObjectWrapper fdow1 = new FileDataObjectWrapper("0",user, fdo1);
        fdow1.setLastUpdate(new Timestamp(12340397603375L));
        fdow1.setLocalName(null);

        FileDataObject fdo2 = new FileDataObject();
        fdo2.setName("file02.jpg");

        FileDataObjectWrapper fdow2 = new FileDataObjectWrapper(ONE, user, fdo2);
        fdow2.setLastUpdate(new Timestamp(12340397603375L));
        fdow2.setLocalName("");

        fileDataObjectMetadataDAO.addItem(fdow1);
        fileDataObjectMetadataDAO.addItem(fdow2);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-1.xml",
            new String[]{"created","modified"});

        FileDataObjectWrapper fdow2res =
            fileDataObjectMetadataDAO.getItem("1");
        assertTrue("Created date not set",
                    !fdow2res.getFileDataObject().getCreated().isEmpty());

        assertTrue("Modified date not set",
                   !fdow2res.getFileDataObject().getCreated().isEmpty());
    }

    public void testAddItem_WithProperties() throws Throwable {

        FileDataObject fdo1 = new FileDataObject();
        fdo1.setName("picture.jpg");
        fdo1.setCrc(SourceUtils.computeCRC("content".getBytes()));
        fdo1.setCreated("20110422T091942Z");
        fdo1.setModified("20110422T104232Z");
        fdo1.setAccessed("20110422T232400Z");
        fdo1.setHidden(true);
        fdo1.setSystem(false);
        fdo1.setArchived(true);
        fdo1.setDeleted(false);
        fdo1.setWritable(true);
        fdo1.setReadable(false);
        fdo1.setExecutable(true);
        fdo1.setContentType("image/jpeg");
        fdo1.setSize(1234L);
        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("Make", "Funambol");
        properties.put("Model", "Cloud");
        properties.put("Orientation", "8");
        properties.put("XResolution", "72");
        fdo1.setProperties(properties);

        FileDataObject fdo2 = new FileDataObject();
        fdo2.setName("ship.jpg");
        fdo2.setUploadStatus(UploadStatus.UPLOADED);
        properties = new LinkedHashMap<String, String>();
        properties.put("Make", "FunambolMake");
        properties.put("Model", "InCloud");
        properties.put("Software", "Ver.1.0");
        properties.put("Print IM", "80, 114, 105, 110, 116");
        fdo2.setProperties(properties);
        
        FileDataObjectWrapper fdow1 = new FileDataObjectWrapper("20", user, fdo1);
        fdow1.setLastUpdate(new Timestamp(123456));
        fdow1.setLocalName("localname1");

        FileDataObjectWrapper fdow2 = new FileDataObjectWrapper("21", user, fdo2);
        fdow2.setLastUpdate(new Timestamp(789000));

        fileDataObjectMetadataDAO.addItem(fdow1);
        fileDataObjectMetadataDAO.addItem(fdow2);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-13.xml",
            new String[]{"created","modified"});

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT_PROPERTY,
            "select * from fnbl_file_data_object_property order by id",
            testResourcesDir + "dataset-userdb-13.xml",
            null);
    }

    public void testGetItem() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-2.xml",
                                      true);
        IDataSet expectedDataSet =
            new FlatXmlDataSet(new File(testResourcesDir + "dataset-userdb-2.xml"));

        FileDataObjectWrapper result = fileDataObjectMetadataDAO.getItem("0");
        FileDataObjectWrapper expected =
            createFileDataObjectWrapper(expectedDataSet, 0);
        assertEqualsFDOWrapper(expected, result);
        assertNull(result.getFileDataObject().getBodyFile());

        result = fileDataObjectMetadataDAO.getItem("2");
        expected = createFileDataObjectWrapper(expectedDataSet, 2);
        assertEqualsFDOWrapper(expected, result);
        assertNull(result.getFileDataObject().getBodyFile());
    }

    public void testGetItem_WithProperties() throws Throwable {
        DBHelper.createDatabaseTester(
            user, testResourcesDir + "dataset-userdb-14.xml", true);
        IDataSet expectedDataSet = new FlatXmlDataSet(
            new File(testResourcesDir + "dataset-userdb-14.xml")
        );

        FileDataObjectWrapper result = fileDataObjectMetadataDAO.getItem("0");
        FileDataObjectWrapper expected =
            createFileDataObjectWrapper(expectedDataSet, 0);
        assertEqualsFDOWrapper(expected, result);

        FileDataObject fdoResult = result.getFileDataObject();
        assertNull(fdoResult.getBodyFile());

        Map<String, String> propsResult = fdoResult.getProperties();
        assertNotNull("FDO properties cannot be null", propsResult);
        assertEquals("Wrong number of properties returned",
                     2, propsResult.size());

        String expPropKey = "exif";
        String expPropValue =
            "{\"Make\":\"'Panasonic'\",\"Model\":\"'DMC-FX3'\"}";
        assertTrue("Property '"+expPropKey+"' not found",
                   propsResult.containsKey(expPropKey));
        assertEquals("Wrong value for '"+expPropKey+"'",
                     expPropValue, propsResult.get(expPropKey));

        expPropKey = "Orientation";
        expPropValue = "8";
        assertTrue("Property '"+expPropKey+"' not found",
                   propsResult.containsKey(expPropKey));
        assertEquals("Wrong value for '"+expPropKey+"'",
                     expPropValue, propsResult.get(expPropKey));

        expPropKey = "notExist";
        assertFalse("Property '"+expPropKey+"' found",
                    propsResult.containsKey(expPropKey));

        result = fileDataObjectMetadataDAO.getItem("1");
        expected = createFileDataObjectWrapper(expectedDataSet, 1);
        assertEqualsFDOWrapper(expected, result);
        fdoResult = result.getFileDataObject();
        assertNull(fdoResult.getBodyFile());
        
        propsResult = fdoResult.getProperties();
        assertNotNull("FDO properties cannot be null", propsResult);
        assertEquals("Wrong number of properties returned",
                     3, propsResult.size());
    }

    public void testGetAllItems() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-2.xml",
                                      true);
        List items = fileDataObjectMetadataDAO.getAllItems();
        String[] resultIds = (String[])items.toArray(new String[items.size()]);
        String[] expectedIds = {"4", "3", ONE};

        ArrayAssert.assertEquals(expectedIds, resultIds);
    }

    public void testGetAllItemsCheckSortUsingLastUpdate() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-9.xml",
                                      true);
        List items = fileDataObjectMetadataDAO.getAllItems();
        String[] resultIds = (String[])items.toArray(new String[items.size()]);
        String[] expectedIds = {ONE,"0", "4", "3", "2"};

        ArrayAssert.assertEquals(expectedIds, resultIds);
    }

    public void testGetLocalName() throws Throwable {

        FileDataObject fdo1 = new FileDataObject();
        fdo1.setName("file01.jpg");

        FileDataObjectWrapper fdow1 = new FileDataObjectWrapper("0",user, fdo1);
        fdow1.setLastUpdate(new Timestamp(12340397603375L));
        fdow1.setLocalName(null);

        FileDataObject fdo2 = new FileDataObject();
        fdo2.setName("file02.jpg");

        FileDataObjectWrapper fdow2 = new FileDataObjectWrapper("1", user, fdo2);
        fdow2.setLastUpdate(new Timestamp(12340397603375L));
        fdow2.setLocalName("");

        FileDataObject fdo3 = new FileDataObject();
        fdo3.setName("file03.jpg");

        FileDataObjectWrapper fdow3 = new FileDataObjectWrapper("2", user, fdo3);
        fdow3.setLastUpdate(new Timestamp(12340397603375L));
        fdow3.setLocalName("local-name");

        fileDataObjectMetadataDAO.addItem(fdow1);
        fileDataObjectMetadataDAO.addItem(fdow2);
        fileDataObjectMetadataDAO.addItem(fdow3);

        assertNull("Localname not null",
                   fileDataObjectMetadataDAO.getLocalName("0"));
        assertNull("Localname not null",
                   fileDataObjectMetadataDAO.getLocalName("1"));
        assertEquals("Wrong localname returned", fdow3.getLocalName(), 
                     fileDataObjectMetadataDAO.getLocalName("2"));
    }

    public void testGetItemSize() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-6.xml",
                                      true);

        long expected = 0;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("1001"));
        expected = 100;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("2001"));
        expected = 100;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("2003"));
        expected = 0;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("1003"));
        expected = 100;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("4"));
        expected = 0;
        assertEquals("Size not " + expected, expected,
                     fileDataObjectMetadataDAO.getItemSize("1005"));
    }

    public void testRemoveItem() throws Exception {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-0.xml",
                                      true);
        fileDataObjectMetadataDAO.removeItem("0", null);

        // Fetch database data after executing insert
        IDataSet actualDataSet = userConn.createDataSet();

        // Load expected data from an XML dataset
        IDataSet expectedDataSet =
            new FlatXmlDataSet(new File(testResourcesDir + "dataset-userdb-4.xml"));

        DBHelper.compareColumns(expectedDataSet,
                                actualDataSet,
                                TABLE_FNBL_FILE_DATA_OBJECT,
                                new String[]{"status"});
    }

    public void testRemoveAllItems() throws Exception {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-2.xml",
                                      true);
        fileDataObjectMetadataDAO.removeAllItems(null);

        // Fetch database data after executing insert
        IDataSet actualDataSet = userConn.createDataSet();

        // Load expected data from an XML dataset
        IDataSet expectedDataSet =
            new FlatXmlDataSet(new File(testResourcesDir + "dataset-userdb-5.xml"));

        DBHelper.compareColumns(expectedDataSet,
                                actualDataSet,
                                TABLE_FNBL_FILE_DATA_OBJECT,
                                new String[]{"status"});
    }

    public void testGetChangedItems() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-6.xml",
                                      true);
        List items = null;
        String[] resultIds = null;
        String[] expectedIds = null;

        Timestamp since = new Timestamp(1);
        Timestamp to = new Timestamp(3);

        List<String>[] changedItems =
            fileDataObjectMetadataDAO.getChangedItemsByLastUpdate(since, to);

        items = changedItems[0];
        resultIds = (String[])items.toArray(new String[items.size()]);
        expectedIds = new String[]{"0", "1"};
        ArrayAssert.assertEquals(expectedIds, resultIds);

        items = changedItems[1];
        resultIds = (String[])items.toArray(new String[items.size()]);
        expectedIds = new String[]{"2", "3"};
        ArrayAssert.assertEquals(expectedIds, resultIds);

        items = changedItems[2];
        resultIds = (String[])items.toArray(new String[items.size()]);
        expectedIds = new String[]{"4", "5", "2005"};
        ArrayAssert.assertEquals(expectedIds, resultIds);
    }

    public void testUpdateItem() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-0.xml",
                                      true);

        FileDataObjectMetadata meta =
            new FileDataObjectMetadata("truename1updated.png");
        meta.setCreated("20090426T091942Z");
        meta.setModified("20090426T104232Z");
        meta.setAccessed("20090426T232400Z");
        meta.setHidden(false);
        meta.setSystem(true);
        meta.setArchived(false);
        meta.setDeleted(true);
        meta.setWritable(false);
        meta.setReadable(true);
        meta.setExecutable(false);
        meta.setContentType("image/png");
        meta.setSize(5678L);
        meta.setUploadStatus("P");

        FileDataObjectBody body = createFileDataObjectBody("content".getBytes());

        FileDataObject fdo01 = new FileDataObject(meta, body);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper("0", user, fdo01);
        fdow.setLastUpdate(new Timestamp(123456));
        fdow.setLocalName("localname1updated");

        fileDataObjectMetadataDAO.updateItem(fdow);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-7.xml");
    }

    public void testUpdateItem_UpdateProperties() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-15.xml",
                                      true);

        FileDataObjectWrapper fdow = fileDataObjectMetadataDAO.getItem("20");
        FileDataObject fdo = fdow.getFileDataObject();
        Map<String, String> props = fdo.getProperties();
        assertNotNull(props);
        assertFalse(props.isEmpty());
        assertEquals(2, props.size());

        // remove all properties
        fdow.getFileDataObject().setProperties(null);
        fileDataObjectMetadataDAO.updateItem(fdow);

        fdow = fileDataObjectMetadataDAO.getItem("20");
        fdo = fdow.getFileDataObject();
        props = fdo.getProperties();
        assertNotNull(props);
        assertTrue("Found some properties", props.isEmpty());

        // add some new properties
        Map<String, String> newProps = new LinkedHashMap<String, String>();
        newProps.put("exif", "{&quot;Make&quot;:&quot;'Funambol'&quot;,&quot;Model&quot;:&quot;'FUN-V91'&quot;}");
        newProps.put("XOrientation", "20");
        newProps.put("YOrientation", "30");
        newProps.put("Dimension", "175x175");
        fdow.getFileDataObject().setProperties(newProps);
        fileDataObjectMetadataDAO.updateItem(fdow);

        fdow = fileDataObjectMetadataDAO.getItem("20");
        fdo = fdow.getFileDataObject();
        props = fdo.getProperties();
        assertNotNull(props);
        assertEquals(newProps.size(), props.size());
    }

    public void testUpdateItemWhenBodyChanges() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-0.xml",
                                      true);

        FileDataObjectMetadata meta =
            new FileDataObjectMetadata("truename1updated.png");
        meta.setCreated("20090426T091942Z");
        meta.setModified("20090426T104232Z");
        meta.setAccessed("20090426T232400Z");
        meta.setHidden(false);
        meta.setSystem(true);
        meta.setArchived(false);
        meta.setDeleted(true);
        meta.setWritable(false);
        meta.setReadable(true);
        meta.setExecutable(false);
        meta.setContentType("image/png");
        meta.setSize(5678L);
        meta.setUploadStatus("P");

        FileDataObjectBody body = createFileDataObjectBody("content".getBytes());
        body.setCrc(44444L);
        FileDataObject fdo01 = new FileDataObject(meta, body);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper("0", user, fdo01);
        fdow.setLastUpdate(new Timestamp(123456789));
        fdow.setLocalName("localname1updated");

        fileDataObjectMetadataDAO.updateItemWhenBodyChanges(fdow);

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-10.xml",
            new String[]{"created", "modified"});

        FileDataObjectWrapper fdowres = fileDataObjectMetadataDAO.getItem("0");
        assertTrue("Created date not set",
                    !fdowres.getFileDataObject().getCreated().isEmpty());

        assertTrue("Modified date not set",
                   !fdowres.getFileDataObject().getCreated().isEmpty());

    }

    public void testUpdateItemWhenBodyChanges_UpdateProperties() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-0.xml",
                                      true);

        FileDataObjectWrapper fdow = fileDataObjectMetadataDAO.getItem("0");
        FileDataObject fdo = fdow.getFileDataObject();
        assertNotNull(fdo);
        assertTrue(fdo.getProperties().isEmpty());

        // update body
        FileDataObjectMetadata meta =
            new FileDataObjectMetadata("truename1updated.png");
        meta.setCreated("20090426T091942Z");
        meta.setModified("20090426T104232Z");
        meta.setAccessed("20090426T232400Z");
        meta.setHidden(false);
        meta.setSystem(true);
        meta.setArchived(false);
        meta.setDeleted(true);
        meta.setWritable(false);
        meta.setReadable(true);
        meta.setExecutable(false);
        meta.setContentType("image/png");
        meta.setSize(5678L);
        meta.setUploadStatus("P");

        FileDataObjectBody body = createFileDataObjectBody("content".getBytes());
        body.setCrc(44444L);
        fdo = new FileDataObject(meta, body);
        fdow = new FileDataObjectWrapper("0", user, fdo);
        fdow.setLastUpdate(new Timestamp(123456789));
        fdow.setLocalName("localname1updated");

        // add some new properties
        Map<String, String> newProps = new LinkedHashMap<String, String>();
        newProps.put("exif", "{&quot;Make&quot;:&quot;'Funambol'&quot;,&quot;Model&quot;:&quot;'FUN-V91'&quot;}");
        newProps.put("XOrientation", "20");
        newProps.put("YOrientation", "30");
        newProps.put("Dimension", "175x175");
        fdow.getFileDataObject().setProperties(newProps);

        fileDataObjectMetadataDAO.updateItemWhenBodyChanges(fdow);

        FileDataObjectWrapper fdowRes = fileDataObjectMetadataDAO.getItem("0");
        FileDataObject fdoRes = fdowRes.getFileDataObject();
        Map<String, String> props = fdoRes.getProperties();
        assertNotNull(props);
        assertEquals(newProps.size(), props.size());

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "dataset-userdb-10.xml",
            new String[]{"created", "modified"});

        assertTrue("Created date not set",
                    !fdowRes.getFileDataObject().getCreated().isEmpty());

        assertTrue("Modified date not set",
                   !fdowRes.getFileDataObject().getCreated().isEmpty());
    }

    public void testGetTwinItems_0() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-8.xml",
                                      true);

        FileDataObject fdo = new FileDataObject();
        fdo.setName("file01.txt");

        FileDataObjectBody body = createFileDataObjectBody("content".getBytes());
        fdo.setBody(body);

        List items = fileDataObjectMetadataDAO.getTwinItems(fdo);
        String[] resultIds = (String[])items.toArray(new String[items.size()]);
        String[] expectedIds = {"0", ONE};

        ArrayAssert.assertEquals(expectedIds, resultIds);
    }

    public void testGetTwinItems_1() throws Throwable {
        DBHelper.createDatabaseTester(user,
                                      testResourcesDir + "dataset-userdb-8.xml",
                                      true);

        FileDataObject fdo = new FileDataObject();
        fdo.setName("file01.txt");

        List items = fileDataObjectMetadataDAO.getTwinItems(fdo);
        String[] resultIds = (String[])items.toArray(new String[items.size()]);
        String[] expectedIds = {"3", "4"};

        ArrayAssert.assertEquals(expectedIds, resultIds);
    }

    public void testRemoveExpiredIncompleteItems() throws Throwable {

        // fill database
        FileDataObject expiredIncompleteFDO = new FileDataObject();
        FileDataObject notExpiredIncompleteFDO = new FileDataObject();
        FileDataObject oldCompleteFDO = new FileDataObject();
        FileDataObject oldCompleteFDO2 = new FileDataObject();
        FileDataObject recentCompleteFDO = new FileDataObject();
        FileDataObject expiredIncompleteFDO2 = new FileDataObject();
        FileDataObject expiredIncompleteFDO3 = new FileDataObject();

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h
        expiredIncompleteFDO.setName("expiredIncompleteFDO");
        expiredIncompleteFDO.setSize(333333L);
        expiredIncompleteFDO.setUploadStatus(UploadStatus.NOT_STARTED);
        FileDataObjectWrapper expiredIncompleteFDOWrapper =
            new FileDataObjectWrapper("0", user, expiredIncompleteFDO);
        expiredIncompleteFDOWrapper.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper.setLocalName("expiredIncompleteFDO.jpg");
        expiredIncompleteFDOWrapper.setStatus('N');

        // not expired incomplete means item with upload_status <> 'U' and
        // last_update set within 24H
        notExpiredIncompleteFDO.setName("notExpiredIncompleteFDO");
        notExpiredIncompleteFDO.setSize(444444L);
        notExpiredIncompleteFDO.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        FileDataObjectWrapper notExpiredIncompleteFDOWrapper =
            new FileDataObjectWrapper( ONE, user, notExpiredIncompleteFDO);
        notExpiredIncompleteFDOWrapper.setLastUpdate(
            new Timestamp(System.currentTimeMillis())
        );
        notExpiredIncompleteFDOWrapper.setLocalName("notExpiredIncompleteFDO.jpg");
        notExpiredIncompleteFDOWrapper.setStatus('N');

        // old complete means item with upload_status set to 'U' and last_update
        // before than 24h
        oldCompleteFDO.setName("oldCompleteFDO");
        oldCompleteFDO.setSize(4545454L);
        oldCompleteFDO.setUploadStatus(UploadStatus.UPLOADED);
        FileDataObjectWrapper oldCompleteFDOWrapper =
            new FileDataObjectWrapper("2", user, oldCompleteFDO);
        oldCompleteFDOWrapper.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        oldCompleteFDOWrapper.setLocalName("oldCompleteFDO.jpg");
        oldCompleteFDOWrapper.setStatus('N');

        // old complete means item with upload_status set to 'U' and last_update
        // before than 24h (local_name null)
        oldCompleteFDO2.setName("oldCompleteFDO2");
        oldCompleteFDO2.setSize(4545454L);
        oldCompleteFDO2.setUploadStatus(UploadStatus.UPLOADED);
        FileDataObjectWrapper oldCompleteFDOWrapper2 =
            new FileDataObjectWrapper("3", user, oldCompleteFDO2);
        oldCompleteFDOWrapper2.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        oldCompleteFDOWrapper2.setStatus('N');

        // recent complete means item with upload_status set to 'U' and
        // last_update within 24h
        recentCompleteFDO.setName("recentCompleteFDO");
        recentCompleteFDO.setSize(4545454L);
        recentCompleteFDO.setUploadStatus(UploadStatus.UPLOADED);
        FileDataObjectWrapper recentCompleteFDOWrapper =
            new FileDataObjectWrapper("4", user, recentCompleteFDO);
        recentCompleteFDOWrapper.setLastUpdate(
            new Timestamp(System.currentTimeMillis())
        );
        recentCompleteFDOWrapper.setLocalName("recentCompleteFDO.jpg");
        recentCompleteFDOWrapper.setStatus('N');

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h
        expiredIncompleteFDO2.setName("expiredIncompleteFDO2");
        expiredIncompleteFDO2.setSize(333333L);
        expiredIncompleteFDO2.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        FileDataObjectWrapper expiredIncompleteFDOWrapper2 =
            new FileDataObjectWrapper("20", user, expiredIncompleteFDO2);
        expiredIncompleteFDOWrapper2.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper2.setLocalName("expiredIncompleteFDO2.jpg");
        expiredIncompleteFDOWrapper2.setStatus('N');

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h (local_name null)
        expiredIncompleteFDO3.setName("expiredIncompleteFDO3");
        expiredIncompleteFDO3.setSize(333333L);
        expiredIncompleteFDO3.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        FileDataObjectWrapper expiredIncompleteFDOWrapper3 =
            new FileDataObjectWrapper("21", user, expiredIncompleteFDO3);
        expiredIncompleteFDOWrapper3.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper3.setLocalName("expiredIncompleteFDO3.jpg");
        expiredIncompleteFDOWrapper3.setStatus('N');
        
        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper);
        fileDataObjectMetadataDAO.addItem(notExpiredIncompleteFDOWrapper);
        fileDataObjectMetadataDAO.addItem(oldCompleteFDOWrapper);
        fileDataObjectMetadataDAO.addItem(oldCompleteFDOWrapper2);
        fileDataObjectMetadataDAO.addItem(recentCompleteFDOWrapper);
        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper2);
        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper3);

        assertEquals(3, fileDataObjectMetadataDAO.getAllItems().size());

        // execute the expired incomplete items removal
        fileDataObjectMetadataDAO.removeExpiredIncompleteItems();

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-expired-incomplete-delete.xml",
            new String[]{"last_update",
                         "crc",
                         "created", "modified", "accessed",
                         "h", "s", "a", "d", "w","r","x",
                         "cttype"});
    }

    public void testRemoveExpiredIncompleteItems_WithProperties() throws Throwable {

        // fill database
        FileDataObject expiredIncompleteFDO1 = new FileDataObject();
        FileDataObject expiredIncompleteFDO2 = new FileDataObject();
        FileDataObject expiredIncompleteFDO3 = new FileDataObject();
        FileDataObject notExpiredIncompleteFDO = new FileDataObject();

        Map<String, String> props1 = new LinkedHashMap<String, String>();
        Map<String, String> props2 = new LinkedHashMap<String, String>();
        Map<String, String> props3 = new LinkedHashMap<String, String>();
        Map<String, String> props4 = new LinkedHashMap<String, String>();

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h - not started
        expiredIncompleteFDO1.setName("expiredIncompleteFDO");
        expiredIncompleteFDO1.setSize(333333L);
        expiredIncompleteFDO1.setUploadStatus(UploadStatus.NOT_STARTED);
        expiredIncompleteFDO1.setProperties(props1);
        FileDataObjectWrapper expiredIncompleteFDOWrapper1 =
            new FileDataObjectWrapper("0", user, expiredIncompleteFDO1);
        expiredIncompleteFDOWrapper1.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper1.setLocalName("expiredIncompleteFDO.jpg");
        expiredIncompleteFDOWrapper1.setStatus('N');

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h - partially uploaded
        expiredIncompleteFDO2.setName("expiredIncompleteFDO2");
        expiredIncompleteFDO2.setSize(333333L);
        expiredIncompleteFDO2.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        props2.put("props21", "value21");
        props2.put("props22", "value22");
        props2.put("props23", "value23");
        props2.put("props24", "value24");
        expiredIncompleteFDO2.setProperties(props2);

        FileDataObjectWrapper expiredIncompleteFDOWrapper2 =
            new FileDataObjectWrapper("20", user, expiredIncompleteFDO2);
        expiredIncompleteFDOWrapper2.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper2.setLocalName("expiredIncompleteFDO2.jpg");
        expiredIncompleteFDOWrapper2.setStatus('N');

        // expired incomplete means item with upload_status <> 'U' and
        // last_update set before than 24h - local_name null
        expiredIncompleteFDO3.setName("expiredIncompleteFDO3");
        expiredIncompleteFDO3.setSize(333333L);
        expiredIncompleteFDO3.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        props3.put("props31", "value31");
        props3.put("props32", "value32");
        props3.put("props33", "value33");
        expiredIncompleteFDO3.setProperties(props3);

        FileDataObjectWrapper expiredIncompleteFDOWrapper3 =
            new FileDataObjectWrapper("21", user, expiredIncompleteFDO3);
        expiredIncompleteFDOWrapper3.setLastUpdate(
            new Timestamp(System.currentTimeMillis() - 2*(24 * 60 * 60 * 1000))
        );
        expiredIncompleteFDOWrapper3.setLocalName("expiredIncompleteFDO3.jpg");
        expiredIncompleteFDOWrapper3.setStatus('N');

        // not expired incomplete means item with upload_status <> 'U' and
        // last_update set within 24H
        notExpiredIncompleteFDO.setName("notExpiredIncompleteFDO");
        notExpiredIncompleteFDO.setSize(444444L);
        notExpiredIncompleteFDO.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        props4.put("exif", "{\"Make\":\"'Panasonic'\",\"Model\":\"'DMC-FX3'\"}");
        notExpiredIncompleteFDO.setProperties(props4);
        
        FileDataObjectWrapper notExpiredIncompleteFDOWrapper =
            new FileDataObjectWrapper("1", user, notExpiredIncompleteFDO);
        notExpiredIncompleteFDOWrapper.setLastUpdate(
            new Timestamp(System.currentTimeMillis())
        );
        notExpiredIncompleteFDOWrapper.setLocalName("notExpiredIncompleteFDO.jpg");
        notExpiredIncompleteFDOWrapper.setStatus('N');

        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper1);
        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper2);
        fileDataObjectMetadataDAO.addItem(expiredIncompleteFDOWrapper3);
        fileDataObjectMetadataDAO.addItem(notExpiredIncompleteFDOWrapper);

        List<String> ids = fileDataObjectMetadataDAO.getAllItems();
        assertEquals("Returned some items", 0, ids.size());
        
        FileDataObjectWrapper fdow = fileDataObjectMetadataDAO.getItem("0");
        assertEquals("Found properties", props1.size(),
                     fdow.getFileDataObject().getProperties().size());
        fdow = fileDataObjectMetadataDAO.getItem("20");
        assertEquals("Wrong number of properties found", props2.size(),
                     fdow.getFileDataObject().getProperties().size());
        fdow = fileDataObjectMetadataDAO.getItem("21");
        assertEquals("Wrong number of properties found", props3.size(),
                     fdow.getFileDataObject().getProperties().size());
        fdow = fileDataObjectMetadataDAO.getItem("1");
        assertEquals("Wrong number of properties found", props4.size(),
                     fdow.getFileDataObject().getProperties().size());

        // execute the expired incomplete items removal
        fileDataObjectMetadataDAO.removeExpiredIncompleteItems();

        DBHelper.assertEqualsDataSet(
            userConn,
            TABLE_FNBL_FILE_DATA_OBJECT,
            testResourcesDir + "exp-expired-incomplete-with-properties-delete.xml",
            new String[]{"last_update",
                         "crc",
                         "created", "modified", "accessed",
                         "h", "s", "a", "d", "w","r","x",
                         "cttype"});
    }

    public void testGetReservedStorageSpace() throws Throwable {

        long expectedReservedSpace = 4936;
        DBHelper.createDatabaseTester(
            user, testResourcesDir + "init-reserved-storage-space.xml", true);

        long reservedSpace = fileDataObjectMetadataDAO.getReservedStorageSpace();
        assertEquals(expectedReservedSpace, reservedSpace);
    }

    public void testGetReservedStorageSpaceUpTo2GB() throws Throwable {

        long expectedReservedSpace = 4294972231L;
        DBHelper.createDatabaseTester(
            user, testResourcesDir + "init-reserved-storage-space-2GB.xml", true);

        long reservedSpace = fileDataObjectMetadataDAO.getReservedStorageSpace();
        assertEquals(expectedReservedSpace, reservedSpace);
    }

    public void testGetStorageSpaceUsage() throws Throwable {

        long expectedSpaceUsage = 2468;
        DBHelper.createDatabaseTester(
            user, testResourcesDir + "init-storage-space-usage.xml", true);

        long spaceUsage = fileDataObjectMetadataDAO.getStorageSpaceUsage();
        assertEquals(expectedSpaceUsage, spaceUsage);
    }

    public void testResetSizeForMisalignedItems() throws Throwable {
        FileDataObject fdo = new FileDataObject();
        fdo.setName("picture.jpg");
        fdo.setSize(4545454L);
        fdo.setUploadStatus(UploadStatus.UPLOADED);
        FileDataObjectWrapper fdow = new FileDataObjectWrapper("15", user, fdo);
        fdow.setLastUpdate(new Timestamp(1234656));
        fdow.setLocalName("localQWEQWETRET.jpg");
        fdow.setStatus('N');
        fdow.setSizeOnStorage(7878787L);

        fileDataObjectMetadataDAO.addItem(fdow);
        fileDataObjectMetadataDAO.resetSizeForMisalignedItems();

        FileDataObjectWrapper result = fileDataObjectMetadataDAO.getItem("15");
        assertEquals("Wrong size found", 
                     fdow.getSizeOnStorage(), result.getSize());
    }

    public void testAddProperties() throws Throwable {

        FileDataObject fdo = new FileDataObject();
        fdo.setName("picture.jpg");
        fdo.setSize(1234L);

        FileDataObjectWrapper fdow = new FileDataObjectWrapper("23", user, fdo);
        fdow.setLastUpdate(new Timestamp(123456));
        fdow.setLocalName("localname");

        fileDataObjectMetadataDAO.addItem(fdow);

        Map<String, String> properties = new LinkedHashMap<String, String>();

        fdo.setProperties(null);
        fileDataObjectMetadataDAO.addProperties(fdow);
        FileDataObjectWrapper expFDOW = fileDataObjectMetadataDAO.getItem("23");

        assertNotNull("Properties map is null",
                      expFDOW.getFileDataObject().getProperties());
        assertTrue("Properties map should be empty",
                   expFDOW.getFileDataObject().getProperties().isEmpty());

        fdo.setProperties(properties);
        fileDataObjectMetadataDAO.addProperties(fdow);
        expFDOW = fileDataObjectMetadataDAO.getItem("23");

        assertNotNull("Properties map is null",
                      expFDOW.getFileDataObject().getProperties());
        assertTrue("Properties map should be empty",
                   expFDOW.getFileDataObject().getProperties().isEmpty());

        properties.put("Make", "Funambol");
        properties.put("Model", "Cloud");
        properties.put("Orientation", "8");
        properties.put("XResolution", "72");
        fdo.setProperties(properties);
        fileDataObjectMetadataDAO.addProperties(fdow);
        expFDOW = fileDataObjectMetadataDAO.getItem("23");

        Map<String, String> expProps =
            expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertFalse("Properties map should not be empty", expProps.isEmpty());
        assertEquals("Wrong number of properties returned",
                     properties.size(), expProps.size());
    }

    public void testRemoveAllProperties() throws Throwable {

        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("Make", "Funambol");
        properties.put("Model", "Cloud");
        properties.put("Orientation", "8");
        properties.put("XResolution", "72");

        FileDataObject fdo = new FileDataObject();
        fdo.setName("picture.jpg");
        fdo.setSize(1234L);
        fdo.setProperties(properties);

        FileDataObjectWrapper fdow = new FileDataObjectWrapper("24", user, fdo);
        fdow.setLastUpdate(new Timestamp(123456));
        fdow.setLocalName("localname");

        fileDataObjectMetadataDAO.addItem(fdow);

        FileDataObjectWrapper expFDOW = fileDataObjectMetadataDAO.getItem("24");
        Map<String, String> expProps =
            expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertFalse("Properties map should not be empty", expProps.isEmpty());
        assertEquals("Wrong number of properties returned",
                     properties.size(), expProps.size());

        fileDataObjectMetadataDAO.removeAllProperties("24");
        expFDOW = fileDataObjectMetadataDAO.getItem("24");
        expProps = expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertTrue("Properties map should be empty", expProps.isEmpty());
    }

    public void removeAllPropertiesByUserID() throws Throwable {
        Map<String, String> properties = new LinkedHashMap<String, String>();
        properties.put("Make", "Funambol");
        properties.put("Model", "Cloud");
        properties.put("Orientation", "8");
        properties.put("XResolution", "72");

        FileDataObject fdo1 = new FileDataObject();
        fdo1.setName("fdo1.jpg");
        fdo1.setSize(1234L);
        fdo1.setProperties(properties);

        FileDataObjectWrapper fdow1 =
            new FileDataObjectWrapper("24", user, fdo1);
        fdow1.setLastUpdate(new Timestamp(123456));
        fdow1.setLocalName("localname1");

        fileDataObjectMetadataDAO.addItem(fdow1);

        FileDataObject fdo2 = new FileDataObject();
        fdo2.setName("fdo2.jpg");
        fdo2.setSize(1234L);
        fdo2.setProperties(properties);

        FileDataObjectWrapper fdow2 =
            new FileDataObjectWrapper("25", user, fdo2);
        fdow2.setLastUpdate(new Timestamp(123456));
        fdow2.setLocalName("localname2");

        fileDataObjectMetadataDAO.addItem(fdow2);

        FileDataObject fdo3 = new FileDataObject();
        fdo3.setName("fdo3.jpg");
        fdo3.setSize(1234L);
        fdo3.setProperties(properties);

        FileDataObjectWrapper fdow3 =
            new FileDataObjectWrapper("26", "username2", fdo3);
        fdow3.setLastUpdate(new Timestamp(123456));
        fdow3.setLocalName("localname3");

        fileDataObjectMetadataDAO.addItem(fdow3);

        fileDataObjectMetadataDAO.removeAllPropertiesByUserID();

        FileDataObjectWrapper expFDOW = fileDataObjectMetadataDAO.getItem("24");
        Map<String, String> expProps = expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertTrue("Properties map should not be empty", expProps.isEmpty());
        expFDOW = fileDataObjectMetadataDAO.getItem("25");
        expProps = expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertTrue("Properties map should not be empty", expProps.isEmpty());
        expFDOW = fileDataObjectMetadataDAO.getItem("26");
        expProps = expFDOW.getFileDataObject().getProperties();
        assertNotNull("Properties map is null", expProps);
        assertFalse("Properties map should not be empty", expProps.isEmpty());
        assertEquals("Wrong number of properties returned",
                     properties.size(), expProps.size());
    }

    public void testGetProperties() throws Throwable {

        Map<String, String> properties = new LinkedHashMap<String, String>();

        FileDataObject fdo = new FileDataObject();
        fdo.setName("picture.jpg");
        fdo.setSize(1234L);
        fdo.setProperties(properties);

        FileDataObjectWrapper fdow = new FileDataObjectWrapper("27", user, fdo);
        fdow.setLastUpdate(new Timestamp(123456));
        fdow.setLocalName("localname");

        fileDataObjectMetadataDAO.addItem(fdow);

        fileDataObjectMetadataDAO.getProperties(fdow);
        assertTrue(fdow.getFileDataObject().getProperties().isEmpty());

        properties.put("Make", "Funambol");
        properties.put("Model", "Cloud");
        properties.put("Orientation", "8");
        properties.put("XResolution", "72");

        fdo = new FileDataObject();
        fdo.setName("image.jpg");
        fdo.setSize(1234L);
        fdo.setProperties(properties);

        fdow = new FileDataObjectWrapper("28", user, fdo);
        fdow.setLastUpdate(new Timestamp(123456));
        fdow.setLocalName("localname");

        fileDataObjectMetadataDAO.addItem(fdow);
        fileDataObjectMetadataDAO.getProperties(fdow);
        assertEquals("Wrong number of properties returned",
                     properties.size(),
                     fdow.getFileDataObject().getProperties().size());

        fileDataObjectMetadataDAO.removeAllProperties("28");
        fileDataObjectMetadataDAO.getProperties(fdow);
        assertTrue("No properties should be returned",
                   fdow.getFileDataObject().getProperties().isEmpty());
    }

    // --------------------------------------------------------- Private methods
    private FileDataObjectWrapper createFileDataObjectWrapper(IDataSet dataset,
                                                              int      row)
    throws Exception {

        ITable table = dataset.getTable(TABLE_FNBL_FILE_DATA_OBJECT);

        // source_type, crc cannot be set

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata meta = new FileDataObjectMetadata();
        fdo.setMetadata(meta);
        fdo.setName((String)dataset.getTable(TABLE_FNBL_FILE_DATA_OBJECT)
                                   .getValue(row, "true_name"));

        meta.setAccessed((String)table.getValue(row, "accessed"));
        meta.setCreated((String)table.getValue(row, "created"));
        meta.setModified((String)table.getValue(row, "modified"));

        meta.setHidden(ONE.equals((String)table.getValue(row, "h")));
        meta.setSystem(ONE.equals((String)table.getValue(row, "s")));
        meta.setArchived(ONE.equals((String)table.getValue(row, "a")));
        meta.setDeleted(ONE.equals((String)table.getValue(row, "d")));
        meta.setWritable(ONE.equals((String)table.getValue(row, "w")));
        meta.setReadable(ONE.equals((String)table.getValue(row, "r")));
        meta.setExecutable(ONE.equals((String)table.getValue(row, "x")));

        meta.setContentType((String)table.getValue(row, "cttype"));

        String size = (String) table.getValue(row, "object_size");
        meta.setSize(size == null ? null : Long.parseLong(size));
        
        FileDataObjectWrapper fdow = new FileDataObjectWrapper(
                table.getValue(row, "id").toString(),
                (String) table.getValue(row, "userid"),
                fdo);

        fdow.setLocalName((String) table.getValue(row, "local_name"));
        fdow.setStatus(((String) table.getValue(row, "status")).charAt(0));
        fdow.setLastUpdate(new Timestamp(1234));

        return fdow;
    }

    private FileDataObjectBody createFileDataObjectBody(byte[] bytes)
    throws IOException {
        File tmp = File.createTempFile("pre", "post", new File("target"));
        tmp.deleteOnExit();
        IOTools.writeFile(bytes, tmp);

        FileDataObjectBody body = new FileDataObjectBody();
        body.setBodyFile(tmp);
        body.setCrc(SourceUtils.computeCRC(bytes));

        return body;
    }

    /**
     * Compares an expected FileDataObjectWrapper object built from a dataset file
     * and a result FileDataObjectWrapper object built from the database.
     *
     * @param expected FileDataObjectWrapper object built from a dataset file
     * @param result FileDataObjectWrapper object built from the database
     * @throws java.text.ParseException
     */
    private static void assertEqualsFDOWrapper(FileDataObjectWrapper expected,
                                               FileDataObjectWrapper result)
    throws ParseException {

        assertEquals(expected.getId(), result.getId());
        assertEquals(expected.getUserId(), result.getUserId());

        // source uri cannot be extracted

        assertEquals(expected.getLastUpdate(), result.getLastUpdate());
        assertEquals(expected.getStatus(), result.getStatus());
        assertEquals(expected.getLocalName(), result.getLocalName());

        FileDataObject expectedFDO = expected.getFileDataObject();
        FileDataObject resultFDO   = result.getFileDataObject();

        assertEquals(expectedFDO.getName(), resultFDO.getName());

        assertEqualsTimestamp(expectedFDO.getCreated(), resultFDO.getCreated());
        assertEqualsTimestamp(expectedFDO.getAccessed(), resultFDO.getAccessed());
        assertEqualsTimestamp(expectedFDO.getModified(), resultFDO.getModified());

        assertEquals(expectedFDO.getHidden(), resultFDO.getHidden());
        assertEquals(expectedFDO.getSystem(), resultFDO.getSystem());
        assertEquals(expectedFDO.getArchived(), resultFDO.getArchived());
        assertEquals(expectedFDO.getDeleted(), resultFDO.getDeleted());
        assertEquals(expectedFDO.getWritable(), resultFDO.getWritable());
        assertEquals(expectedFDO.getReadable(), resultFDO.getReadable());
        assertEquals(expectedFDO.getExecutable(), resultFDO.getExecutable());

        assertEquals(expectedFDO.getContentType(), resultFDO.getContentType());
        assertEquals(expectedFDO.getSize(), resultFDO.getSize());
    }

    /**
     * Compares an expected timestamp column taken built from a dataset file
     * and a result timestamp column taken built from the database.
     *
     * @param expected timestamp taken from a dataset file
     * @param result timestamp taken from database
     * @throws java.text.ParseException if a timestamp column read from the dataset
     * file is not in the format "yyyy-MM-dd hh:mm:ss" or a timestamp column read
     * from the database is not in the UTC format.
     */
    private static void assertEqualsTimestamp(String expected, String result)
    throws ParseException {

        Timestamp resultTimestamp = null;
        if (result != null){
            SimpleDateFormat utcFormatter =
                new SimpleDateFormat(TimeUtils.PATTERN_UTC);
            Date resultCreatedDate = utcFormatter.parse(result);
            resultTimestamp = new Timestamp(resultCreatedDate.getTime());
        }

        Timestamp expectedTimestamp = null;
        if (expected != null){
            SimpleDateFormat standardFormatter =
                new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date expectedCreatedDate = standardFormatter.parse(expected);
            expectedTimestamp = new Timestamp(expectedCreatedDate.getTime());
        }
        assertEquals(expectedTimestamp, resultTimestamp);
    }

}
