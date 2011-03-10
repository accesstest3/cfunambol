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
package com.funambol.pushlistener.service.registry.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junitx.util.PrivateAccessor;

import org.dbunit.IDatabaseTester;

import com.funambol.pushlistener.service.registry.RegistryEntry;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;
import com.funambol.pushlistener.util.DBHelper;
import com.funambol.pushlistener.util.DBUnitHelper;
import com.funambol.pushlistener.util.DatabaseTester;

/**
 * Test cases for RegistryDAO class.
 *
 * @version $id$
 */
public class RegistryDaoTest extends DatabaseTester {

    // --------------------------------------------------------------- Constants
    public static final String INITIAL_DATASET =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/initial-db-dataset-core.xml";

     // Insert dataset
     public static final String STORE_ITEM_DATASET_BASIC =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/store-item-db-dataset-basic.xml";

     public static final String STORE_ITEM_DATASET_USERNAME =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/store-item-db-dataset-username.xml";

     public static final String STORE_ITEM_DATASET_COMPLEX =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/store-item-db-dataset-complex.xml";

     // update dataset
     public static final String UPDATE_ITEM_DATASET_BASIC =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/update-item-db-dataset-basic.xml";

     public static final String UPDATE_ITEM_DATASET_USERNAME =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/update-item-db-dataset-username.xml";

     public static final String UPDATE_ITEM_DATASET_COMPLEX =
        "src/test/resources/data/com/funambol/pushlistener/service/dao/RegistryDao/update-item-db-dataset-complex.xml";

     public static final String BASIC_REGISTRY_TABLE_NAME =
         "FNBL_PUSH_REGISTRY_BASIC";

     public static final String SQL_SELECT_ENTRIES_INTO_RANGE =
         "SELECT * FROM {0} WHERE ID >= {1} AND ID <= {2} ORDER BY ID";

     public static final String USERNAME_REGISTRY_TABLE_NAME =
         "FNBL_PUSH_REGISTRY_USERNAME";

     public static final String COMPLEX_REGISTRY_TABLE_NAME =
         "FNBL_PUSH_REGISTRY_COMPLEX";

     public static final String ID_SPACE = "email.registryid";

    // ----------------------------------------------------------- Instance data

     RegistryDao basicRegistryDao;
     RegistryDao usernameRegistryDao;
     RegistryDao complexRegistryDao;
     private IDatabaseTester coreDatabaseTester;

     public RegistryDaoTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        coreDatabaseTester =
            DBUnitHelper.createDatabaseTester(INITIAL_DATASET, true);

        basicRegistryDao    = new RegistryDao(BASIC_REGISTRY_TABLE_NAME,
                                              ID_SPACE);
        usernameRegistryDao = new RegistryDao(USERNAME_REGISTRY_TABLE_NAME,
                                              ID_SPACE);
        complexRegistryDao  = new RegistryDao(COMPLEX_REGISTRY_TABLE_NAME,
                                              ID_SPACE);

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        DBUnitHelper.closeConnection(coreDatabaseTester);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getEntryById method, of class RegistryDao.
     * @throws Exception
     */
    public void testGetEntryById() throws Exception {

        // BASIC REGISTRY DAO (table without extra properties)
        long id = -5;
        RegistryEntry result = basicRegistryDao.getEntryById(id);
        assertTrue(result==null);

        id =4;
        result = basicRegistryDao.getEntryById(id);

        RegistryEntry expected = new RegistryEntry(4);
        expected.setPeriod(1000);
        expected.setActive(true);
        expected.setLastUpdate(3234556666666L);
        expected.setStatus("N");
        expected.setTaskBeanFile("com.funambol.task.ScheduledTask");
        assertEquals(expected, result);

        id =5;
        result = basicRegistryDao.getEntryById(id);

        expected = new RegistryEntry(5);
        expected.setPeriod(1005);
        expected.setActive(false);
        expected.setLastUpdate(43389765432L);
        expected.setStatus("D");
        expected.setTaskBeanFile("com.funambol.task.OneShotTask");
        assertEquals(expected, result);

        // USERNAME REGISTRY DAO (table without only one more column:USERNAME)
        id=34;
        result = usernameRegistryDao.getEntryById(id);
        assertTrue(result==null);

        id=5;
        result = usernameRegistryDao.getEntryById(id);
        Map<String,Object> expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("username", "andrew");
        expected.setProperties(expectedProperties);
        expected.setLastUpdate(33389765432L);
        expected.setActive(false);
        assertEquals(expected, result);

        id=2;
        result = usernameRegistryDao.getEntryById(id);
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("username", "john");
        expected.setProperties(expectedProperties);
        expected.setId(2);
        expected.setPeriod(1000);
        expected.setActive(true);
        expected.setStatus("N");
        expected.setTaskBeanFile("com.funambol.task.MagicTask");
        expected.setLastUpdate(1234556665555L);
        assertEquals(expected, result);

        // COMPLEX REGISTRY DAO (table with a lot of extra columns)
        id = 20;
        result = complexRegistryDao.getEntryById(id);
        assertTrue(result==null);

        id =2;
        result = complexRegistryDao.getEntryById(id);
        Map<String,Object> properties = result.getProperties();
        checkPropertyClasses(properties);

        expected = new RegistryEntry(2);
        expected.setActive(false);
        expected.setLastUpdate(1234556666677L);
        expected.setPeriod(1000);
        expected.setStatus("U");
        expected.setTaskBeanFile("com.funambol.tasks.OneShotTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("var_field", "It's the value for 2");
        expectedProperties.put("bool_field", Boolean.TRUE);
        expectedProperties.put("old_bool_field", "N");
        expectedProperties.put("long_field", new Long(2321423231231L));
        expectedProperties.put("decimal_field",new BigDecimal("556432"));
        expectedProperties.put("double_field",new Double(37.5));
        expectedProperties.put("integer_field",new Integer(123));
        expectedProperties.put("date_field",new Date(61222345200000L));
        expectedProperties.put("time_field",new Time(5530000));
        expectedProperties.put("timestamp_field",new Timestamp(61208248980000L));
        assertEquals(expected, result);

        id =3;
        result = complexRegistryDao.getEntryById(id);
        properties = result.getProperties();
        checkPropertyClasses(properties);
        expected = new RegistryEntry(id);
        expected.setActive(false);
        expected.setLastUpdate(6578556661333L);
        expected.setPeriod(1023);
        expected.setStatus("N");
        expected.setTaskBeanFile("com.funambol.task.MagicTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("var_field", "3rd string value");
        expectedProperties.put("bool_field", Boolean.FALSE);
        expectedProperties.put("old_bool_field", "N");
        expectedProperties.put("long_field", new Long(12222224444L));
        expectedProperties.put("decimal_field",new BigDecimal("2453"));
        expectedProperties.put("double_field",new Double(56.86));
        expectedProperties.put("integer_field",new Integer(5686));
        expectedProperties.put("date_field",getDate(2008,10,17,0,0,0));
        expectedProperties.put("time_field",getTime(2,32,10));
        expectedProperties.put("timestamp_field",getTimestamp(2009,5,22,12,34,23));
        expected.setProperties(expectedProperties);
        assertEquals(expected, result);
    }

    /**
     * Test of getEntries method, of class RegistryDao.
     */
    public void testGetEntries() throws Exception {
        List<RegistryEntry> result = basicRegistryDao.getEntries(1234556667778L,
                                             RegistryEntryStatus.UPDATED);

        assertTrue(result!=null);
        assertEquals(0, result.size());

        result = basicRegistryDao.getEntries(0L,
                                             RegistryEntryStatus.NEW);

        assertTrue(result!=null);
        Set<Long> expectedIds = new HashSet<Long>();
        expectedIds.add(2L);
        expectedIds.add(3L);
        expectedIds.add(4L);

        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(true,entry.getActive());
        }

        result = basicRegistryDao.getEntries(1234556667778L,
                                             RegistryEntryStatus.NEW);

        expectedIds = new HashSet<Long>();
        expectedIds.add(3L);
        expectedIds.add(4L);

        assertTrue(result!=null);
        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(RegistryEntryStatus.NEW,entry.getStatus());
            assertTrue  ("Error, last update lower!",entry.getLastUpdate()>=1234556667778L);
        }


        result = usernameRegistryDao.getEntries(0L,
                                             RegistryEntryStatus.NEW);

        assertTrue(result!=null);
        expectedIds = new HashSet<Long>();
        expectedIds.add(2L);
        expectedIds.add(3L);

        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(true,entry.getActive());
        }

        result = usernameRegistryDao.getEntries(1233556666666L,
                                             RegistryEntryStatus.NEW);

        expectedIds.clear();
        expectedIds.add(1L);
        expectedIds.add(2L);
        expectedIds.add(3L);

        assertTrue(result!=null);
        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(RegistryEntryStatus.NEW,entry.getStatus());
            assertTrue  ("Error, last update lower!",entry.getLastUpdate()>=1233556666666L);
        }

        result = complexRegistryDao.getEntries(0L, RegistryEntryStatus.NEW);

        assertTrue(result!=null);
        expectedIds = new HashSet<Long>();
        expectedIds.add(1L);
        expectedIds.add(4L);

        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(true,entry.getActive());
        }

        result = complexRegistryDao.getEntries(1234556666665L,
                                             RegistryEntryStatus.UPDATED);

        expectedIds.clear();
        expectedIds.add(1L);
        expectedIds.add(2L);

        assertTrue(result!=null);
        assertEquals(expectedIds.size(), result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertEquals(RegistryEntryStatus.UPDATED,entry.getStatus());
            assertTrue  ("Error, last update lower!",entry.getLastUpdate()>=1233556666666L);
        }

    }

    /**
     * Test of getActiveEntries method, of class RegistryDao.
     */
    public void testGetActiveEntries() throws Exception {
        Set<Long> expectedIds = new HashSet<Long>();
        expectedIds.add(2L);
        expectedIds.add(3L);
        expectedIds.add(4L);
        List<RegistryEntry> result = basicRegistryDao.getActiveEntries();
        assertTrue(result!=null);
        assertEquals(expectedIds.size(),result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertTrue(entry.getActive());
        }

        expectedIds.clear();
        expectedIds.add(2L);
        expectedIds.add(3L);
        result = usernameRegistryDao.getActiveEntries();
        assertTrue(result!=null);
        assertEquals(expectedIds.size(),result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertTrue(entry.getActive());
        }

        expectedIds.clear();
        expectedIds.add(1L);
        expectedIds.add(4L);
        result = complexRegistryDao.getActiveEntries();
        assertTrue(result!=null);
        assertEquals(expectedIds.size(),result.size());
        for(RegistryEntry entry:result) {
            if(!expectedIds.contains(entry.getId())) {
                fail("Id ["+entry.getId()+"] not returned");
            }
            assertTrue(entry.getActive());
        }
    }

    /**
     * Test of deleteRegistryEntry method, of class RegistryDao.
     */
    public void testDeleteRegistryEntry() throws Exception {
        assertEquals(false,basicRegistryDao.deleteRegistryEntry(12));

        RegistryEntry entry = new RegistryEntry();
        entry.setPeriod(1000);
        entry.setActive(true);
        entry.setLastUpdate(1234556666666L);
        entry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        long idToCheck = basicRegistryDao.insertRegistryEntry(entry);

        RegistryEntry expected = basicRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(true,basicRegistryDao.deleteRegistryEntry(idToCheck));
        expected = basicRegistryDao.getEntryById(idToCheck);
        assertTrue(expected==null);

        assertEquals(false,usernameRegistryDao.deleteRegistryEntry(12));

        entry = new RegistryEntry();
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("username", "Mary");
        idToCheck = usernameRegistryDao.insertRegistryEntry(entry);

        expected = usernameRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(true,usernameRegistryDao.deleteRegistryEntry(idToCheck));
        expected = usernameRegistryDao.getEntryById(idToCheck);
        assertTrue(expected==null);

        assertEquals(false,complexRegistryDao.deleteRegistryEntry(12));

        entry = new RegistryEntry();
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("notvalid_field", "don't consider me");
        entry.addProperty("var_field", "string fields");
        entry.addProperty("old_bool_field", "N");
        entry.addProperty("long_field", new Long(435245435235L));
        entry.addProperty("double_field", new Double(225.67));
        entry.addProperty("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));
        idToCheck = complexRegistryDao.insertRegistryEntry(entry);

        expected = complexRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(true,complexRegistryDao.deleteRegistryEntry(idToCheck));
        expected = complexRegistryDao.getEntryById(idToCheck);
        assertTrue(expected==null);
    }

    /**
     * Test of removeAllDeletedRegistryEntry method, of class RegistryDao.
     */
    public void testRemoveAllDeletedRegistryEntry() throws Exception {
        List<RegistryEntry> toDelete = basicRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);
        assertTrue(toDelete!=null);
        assertTrue(toDelete.size()>0);
        for(RegistryEntry entry:toDelete) {
            assertEquals(RegistryEntryStatus.DELETED, entry.getStatus());
        }

        long howMany = basicRegistryDao.removeAllDeletedRegistryEntry();
        assertEquals(toDelete.size(), howMany);

        toDelete = basicRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);

        assertTrue(toDelete!=null);
        assertTrue(toDelete.isEmpty());

        toDelete = usernameRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);
        assertTrue(toDelete!=null);
        assertTrue(toDelete.size()>0);
        for(RegistryEntry entry:toDelete) {
            assertEquals(RegistryEntryStatus.DELETED, entry.getStatus());
        }

        howMany = usernameRegistryDao.removeAllDeletedRegistryEntry();
        assertEquals(toDelete.size(), howMany);

        toDelete = usernameRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);

        assertTrue(toDelete!=null);
        assertTrue(toDelete.isEmpty());

            toDelete = basicRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);

        assertTrue(toDelete!=null);
        assertTrue(toDelete.isEmpty());

        toDelete = complexRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);
        assertTrue(toDelete!=null);
        assertTrue(toDelete.size()>0);
        for(RegistryEntry entry:toDelete) {
            assertEquals(RegistryEntryStatus.DELETED, entry.getStatus());
        }

        howMany = complexRegistryDao.removeAllDeletedRegistryEntry();
        assertEquals(toDelete.size(), howMany);

        toDelete = complexRegistryDao.getEntries(1L, RegistryEntryStatus.DELETED);

        assertTrue(toDelete!=null);
        assertTrue(toDelete.isEmpty());
    }

    /**
     * Test of markAsDeleted method, of class RegistryDao.
     */
    public void testMarkAsDeleted() throws Exception {
        assertEquals(0,basicRegistryDao.markAsDeleted(12));

        RegistryEntry entry = new RegistryEntry();
        entry.setPeriod(1000);
        entry.setActive(true);
        entry.setLastUpdate(1234556666666L);
        entry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        long idToCheck = basicRegistryDao.insertRegistryEntry(entry);

        RegistryEntry expected = basicRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(1,basicRegistryDao.markAsDeleted(idToCheck));
        expected = basicRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.DELETED, expected.getStatus());

        assertEquals(0,usernameRegistryDao.markAsDeleted(12));

        entry = new RegistryEntry();
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("username", "Mary");
        idToCheck = usernameRegistryDao.insertRegistryEntry(entry);

        expected = usernameRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(1,usernameRegistryDao.markAsDeleted(idToCheck));
        expected = usernameRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.DELETED, expected.getStatus());

        assertEquals(0,complexRegistryDao.markAsDeleted(12));

        entry = new RegistryEntry();
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("notvalid_field", "don't consider me");
        entry.addProperty("var_field", "string fields");
        entry.addProperty("old_bool_field", "N");
        entry.addProperty("long_field", new Long(435245435235L));
        entry.addProperty("double_field", new Double(225.67));
        entry.addProperty("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));
        idToCheck = complexRegistryDao.insertRegistryEntry(entry);

        expected = complexRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.NEW, expected.getStatus());

        assertEquals(1,complexRegistryDao.markAsDeleted(idToCheck));
        expected = complexRegistryDao.getEntryById(idToCheck);
        assertTrue(expected!=null);
        assertEquals(RegistryEntryStatus.DELETED, expected.getStatus());

    }

    /**
     * Test of updateRegistryEntry method, of class RegistryDao.
     */
    public void testUpdateRegistryEntry() throws Exception {
        // BASIC DAO
        RegistryEntry entry = new RegistryEntry(12);
        assertEquals(0,basicRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1000);
        entry.setActive(true);
        entry.setLastUpdate(1234556666666L);
        entry.setStatus("N");
        entry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        long firstId = basicRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(firstId);
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        assertEquals(1,basicRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1005);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setStatus("D");
        entry.setTaskBeanFile("com.funambol.task.OneShotTask");

        long lastId = basicRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(lastId);
        entry.setPeriod(3334542333L);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setTaskBeanFile("com.funambol.task.CustomTask");
        assertEquals(1,basicRegistryDao.updateRegistryEntry(entry));

        String query =
            DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                              BASIC_REGISTRY_TABLE_NAME,
                                              firstId,
                                              lastId);

        DBUnitHelper.compareDataSet(query,
                                    BASIC_REGISTRY_TABLE_NAME,
                                    UPDATE_ITEM_DATASET_BASIC,
                                    new String[] {"id"});

        // USERNAME DAO
        entry = new RegistryEntry(12);
        assertEquals(0,usernameRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1000);
        entry.setActive(true);
        entry.setLastUpdate(1234556666666L);
        entry.setStatus("N");
        entry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        firstId = usernameRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(firstId);
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("username", "Mary");
        assertEquals(1,usernameRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1005);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setStatus("D");
        entry.setTaskBeanFile("com.funambol.task.OneShotTask");
        entry.addProperty("username", "Jacob");

        lastId = usernameRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(lastId);
        entry.setPeriod(3334542333L);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setTaskBeanFile("com.funambol.task.CustomTask");
        entry.addProperty("username", "Andrew");
        assertEquals(1,usernameRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1005);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setStatus("D");
        entry.setTaskBeanFile("com.funambol.task.OneShotTask");
        entry.addProperty("username", "Angel");

        lastId = usernameRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(lastId);
        entry.setPeriod(3334542332223L);
        entry.setActive(true);
        entry.setLastUpdate(33389111765432L);
        entry.setTaskBeanFile("com.funambol.task.SimpleJoinTask");

        assertEquals(1,usernameRegistryDao.updateRegistryEntry(entry));

        query =
            DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                              USERNAME_REGISTRY_TABLE_NAME,
                                              firstId,
                                              lastId);

        DBUnitHelper.compareDataSet(query,
                                    USERNAME_REGISTRY_TABLE_NAME,
                                    UPDATE_ITEM_DATASET_USERNAME,
                                    new String[] {"id"});

        // COMPLEX DAO
        entry = new RegistryEntry(12);
        assertEquals(0,complexRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1000);
        entry.setActive(true);
        entry.setLastUpdate(1234556666666L);
        entry.setStatus("N");
        entry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        firstId = complexRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(firstId);
        entry.setPeriod(4543432);
        entry.setActive(false);
        entry.setLastUpdate(3432421321342L);
        entry.setTaskBeanFile("com.funambol.task.SmartTask");
        entry.addProperty("notvalid_field", "don't consider me");
        entry.addProperty("var_field", "string fields");
        entry.addProperty("old_bool_field", "N");
        entry.addProperty("long_field", new Long(435245435235L));
        entry.addProperty("double_field", new Double(225.67));
        entry.addProperty("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));

        assertEquals(1,complexRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1005);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setStatus("D");
        entry.setTaskBeanFile("com.funambol.task.OneShotTask");
        entry.addProperty("notvalid_field", "don't consider me");
        entry.addProperty("var_field", "string fields");
        entry.addProperty("old_bool_field", "N");
        entry.addProperty("long_field", new Long(435245435235L));
        entry.addProperty("double_field", new Double(225.67));
        entry.addProperty("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));

        lastId = complexRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(lastId);
        entry.setPeriod(3334542333L);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setTaskBeanFile("com.funambol.task.CustomTask");
        entry.addProperty("username", "Andrew");
        assertEquals(1,complexRegistryDao.updateRegistryEntry(entry));

        entry = new RegistryEntry();
        entry.setPeriod(1005);
        entry.setActive(false);
        entry.setLastUpdate(33389765432L);
        entry.setStatus("D");
        entry.setTaskBeanFile("com.funambol.task.OneShotTask");
        entry.addProperty("notvalid_field", "don't consider me");
        entry.addProperty("var_field", "string fields");
        entry.addProperty("old_bool_field", "N");
        entry.addProperty("long_field", new Long(435245435235L));
        entry.addProperty("double_field", new Double(225.67));
        entry.addProperty("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));

        lastId = complexRegistryDao.insertRegistryEntry(entry);

        entry = new RegistryEntry(lastId);
        entry.setPeriod(3334542332223L);
        entry.setActive(true);
        entry.setLastUpdate(33389111765432L);
        entry.setTaskBeanFile("com.funambol.task.SimpleJoinTask");
        entry.addProperty("bool_field", Boolean.FALSE);
        entry.addProperty("old_bool_field", "Y");
        entry.addProperty("long_field", new Long(435245434445235L));
        entry.addProperty("decimal_field", new BigDecimal("1234567899876543221"));
        entry.addProperty("double_field", new Double(22445.67));
        entry.addProperty("integer_field", "226788");
        // 2009-16-11 = 1270936800000
        entry.addProperty("date_field", new Date(1270936800000L));
        entry.addProperty("time_field", new Time(Time.valueOf("23:43:11").getTime()));
        entry.addProperty("timestamp_field", new Timestamp(new GregorianCalendar(2009, 06, 22, 19,30, 14).getTimeInMillis()));
        entry.addProperty("not_valid_field", "not valid property");

        assertEquals(1,complexRegistryDao.updateRegistryEntry(entry));

        query =
            DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                              COMPLEX_REGISTRY_TABLE_NAME,
                                              firstId,
                                              lastId);

        DBUnitHelper.compareDataSet(query,
                                    COMPLEX_REGISTRY_TABLE_NAME,
                                    UPDATE_ITEM_DATASET_COMPLEX,
                                    new String[] {"id"});

    }

    /**
     * Test of insertRegistryEntry method, of class RegistryDao.
     * @throws Throwable
     */
    public void testInsertRegistryEntry() throws Throwable {

        // BASIC REGISTRY DAO TEST
        RegistryEntry newEntry = new RegistryEntry();
        newEntry.setPeriod(1000);
        newEntry.setActive(true);
        newEntry.setLastUpdate(1234556666226L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        Map<String,Object> expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("USERNAME", "angelina");
        newEntry.setProperties(expectedProperties);
        long expectedId = getNextId(basicRegistryDao);
        long resultId = basicRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId, resultId);
        long firstId = expectedId;

        newEntry = new RegistryEntry();
        newEntry.setPeriod(12000000);
        newEntry.setActive(false);
        newEntry.setLastUpdate(3294857323495L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedId = getNextId(basicRegistryDao);
        resultId = basicRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);
        long lastId = expectedId;

        String query =
            DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                              BASIC_REGISTRY_TABLE_NAME,
                                              firstId,
                                              lastId);

        DBUnitHelper.compareDataSet(query,
                                    BASIC_REGISTRY_TABLE_NAME,
                                    STORE_ITEM_DATASET_BASIC,
                                    new String[] {"id"});

        // USERNAME REGISTRY DAO
        newEntry.setPeriod(1000);
        newEntry.setActive(true);
        newEntry.setLastUpdate(1234556666226L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("username", "angelina");
        newEntry.setProperties(expectedProperties);
        expectedId = getNextId(usernameRegistryDao);
        resultId = usernameRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);
        firstId = expectedId;

        newEntry = new RegistryEntry();
        newEntry.setPeriod(12000000);
        newEntry.setActive(false);
        newEntry.setLastUpdate(3294857323495L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("username", "john");
        expectedProperties.put("notvalid", 18);
        newEntry.setProperties(expectedProperties);
        expectedId = getNextId(usernameRegistryDao);
        resultId = usernameRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);

        newEntry = new RegistryEntry();
        newEntry.setPeriod(9999999);
        newEntry.setActive(false);
        newEntry.setLastUpdate(3294857323495L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.OneShotTask");
        expectedId = getNextId(usernameRegistryDao);
        resultId = usernameRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);
        lastId = expectedId;

        query = DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                                  USERNAME_REGISTRY_TABLE_NAME,
                                                  firstId,
                                                  lastId);

        DBUnitHelper.compareDataSet(query,
                                    USERNAME_REGISTRY_TABLE_NAME,
                                    STORE_ITEM_DATASET_USERNAME,
                                    new String[] {"id"});

        // COMPLEX REGISTRY DAO

        newEntry.setPeriod(20455383);
        newEntry.setActive(true);
        newEntry.setLastUpdate(1234556666226L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("var_field", "string fields");
        expectedProperties.put("bool_field", Boolean.FALSE);
        expectedProperties.put("old_bool_field", "N");
        expectedProperties.put("long_field", new Long(435245435235L));
        expectedProperties.put("decimal_field", new BigDecimal("1234567899876543221"));
        expectedProperties.put("double_field", new Double(225.67));
        expectedProperties.put("integer_field", new Integer(2267));
        // 2009-06-06 = 1244239200000
        expectedProperties.put("date_field", new Date(1244239200000L));
        expectedProperties.put("time_field", new Time(Time.valueOf("09:42:12").getTime()));
        expectedProperties.put("timestamp_field", new Timestamp(new GregorianCalendar(2009, 11, 20, 12,34, 34).getTimeInMillis()));
        newEntry.setProperties(expectedProperties);
        expectedId = getNextId(complexRegistryDao);
        resultId = complexRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);
        firstId = expectedId;

        newEntry = new RegistryEntry();
        newEntry.setPeriod(12000000);
        newEntry.setActive(false);
        newEntry.setLastUpdate(3294857323495L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("bool_field", Boolean.FALSE);
        expectedProperties.put("old_bool_field", "N");
        expectedProperties.put("long_field", new Long(435245435235L));
        expectedProperties.put("decimal_field", new BigDecimal("1234567899876543221"));
        expectedProperties.put("double_field", new Double(225.67));
        expectedProperties.put("integer_field", "2267");
        // 2009-06-17 = 1245189600000
        expectedProperties.put("date_field",new Date(1245189600000L));
        expectedProperties.put("timestamp_field", new Timestamp(new GregorianCalendar(2009, 06, 22, 19,30, 14).getTimeInMillis()));
        expectedProperties.put("not_valid_field", "not valid property");
        newEntry.setProperties(expectedProperties);
        expectedId = getNextId(complexRegistryDao);
        resultId = complexRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);

        newEntry = new RegistryEntry();
        newEntry.setPeriod(9999999);
        newEntry.setActive(false);
        newEntry.setLastUpdate(3294857323495L);
        newEntry.setStatus(RegistryEntryStatus.NEW);
        newEntry.setTaskBeanFile("com.funambol.task.OneShotTask");
        newEntry.getProperties().clear();
        expectedId = getNextId(complexRegistryDao);
        resultId = complexRegistryDao.insertRegistryEntry(newEntry);
        assertEquals(expectedId,resultId);
        lastId = expectedId;

        query = DBHelper.replaceQueryPlaceholders(SQL_SELECT_ENTRIES_INTO_RANGE,
                                                  COMPLEX_REGISTRY_TABLE_NAME,
                                                  firstId,
                                                  lastId);

        DBUnitHelper.compareDataSet(query,
                                    COMPLEX_REGISTRY_TABLE_NAME,
                                    STORE_ITEM_DATASET_COMPLEX,
                                    new String[] {"id"});
    }

    // --------------------------------------------------------- Private methods
    private void checkPropertyClasses(Map<String, Object> properties) {
        Object obj = properties.get("var_field");
        assertTrue(obj!=null);
        assertEquals(String.class, obj.getClass());

        obj = properties.get("bool_field");
        assertTrue(obj!=null);
        assertEquals(Boolean.class, obj.getClass());

        obj = properties.get("old_bool_field");
        assertTrue(obj!=null);
        assertEquals(String.class, obj.getClass());

        obj = properties.get("long_field");
        assertTrue(obj!=null);
        assertEquals(Long.class, obj.getClass());

        obj = properties.get("decimal_field");
        assertTrue(obj!=null);
        assertEquals(BigDecimal.class, obj.getClass());

        obj = properties.get("double_field");
        assertTrue(obj!=null);
        assertEquals(Double.class, obj.getClass());

        obj = properties.get("integer_field");
        assertTrue(obj!=null);
        assertEquals(Integer.class, obj.getClass());

        obj = properties.get("date_field");
        assertTrue(obj!=null);
        assertEquals(Date.class, obj.getClass());

        obj = properties.get("time_field");
        assertTrue(obj!=null);
        assertEquals(Time.class, obj.getClass());

        obj = properties.get("timestamp_field");
        assertTrue(obj!=null);
        assertEquals(Timestamp.class, obj.getClass());
    }

    private long getNextId(RegistryDao basicRegistryDao) throws Throwable {
        return (Long) PrivateAccessor.invoke(basicRegistryDao,
                                                 "getNextEntryId",
                                                 new Class[0],
                                                 new Object[0])+1;
    }

    public static void assertEquals(RegistryEntry expected, RegistryEntry result) {
        if(expected==null) {
            assertTrue("Expected null but result not null.",result==null);
            return;
        }
        assertTrue("Result is null!",result!=null);

        assertEquals("Id don't match!",
                     expected.getId(),
                     result.getId());

        assertEquals("Active don't match!",
                     expected.getActive(),
                     result.getActive());

        assertEquals("Last update don't match!",
                     expected.getLastUpdate(),
                     result.getLastUpdate());

        assertEquals("Period update don't match!",
                     expected.getPeriod(),
                     result.getPeriod());

        assertEquals("Status update don't match!",
                     expected.getStatus(),
                     result.getStatus());

        assertEquals("TaskBeanFile update don't match!",
                     expected.getTaskBeanFile(),
                     result.getTaskBeanFile());


        Map<String,Object> expectedProperties = expected.getProperties();
        Map<String,Object> resultProperties   = result.getProperties();
        if(expectedProperties==null) {
            assertTrue(resultProperties == null);
        }
        else {
            assertTrue(resultProperties!=null);
            for(String key:expectedProperties.keySet()) {
                assertTrue("Error on key ["+key+"]",resultProperties.containsKey(key));
                Object expectedObject = expectedProperties.get(key);
                Object resultObject   = resultProperties.get(key);

                assertEquals("Error matching property ["+key+"] ["+resultObject+"].",
                             expectedObject,
                             resultObject);
            }
        }
    }

    private Time getTime(int hours, int minutes, int seconds) {
        GregorianCalendar gc = new GregorianCalendar(1970,0,1,hours,minutes,seconds);
        return  new Time(gc.getTimeInMillis());
    }

    private Timestamp getTimestamp(int year,
                                   int month,
                                   int days,
                                   int hours,
                                   int minutes,
                                   int seconds) {
        GregorianCalendar gc = new GregorianCalendar(year,
                                                     month,
                                                     days,
                                                     hours,
                                                     minutes,
                                                     seconds);
        return new Timestamp(gc.getTimeInMillis());
    }

    private Date getDate(int year,
                         int month,
                         int days,
                         int hours,
                         int minutes,
                         int seconds) {
        GregorianCalendar gc = new GregorianCalendar(year,
                                                     month,
                                                     days,
                                                     hours,
                                                     minutes,
                                                     seconds);
        return new Date(gc.getTimeInMillis());
    }


}