/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.server.store;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ConfigItem;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.server.config.Configuration;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.db.RoutingDataSource;
import com.funambol.server.inventory.PSDeviceInventory;

/**
 * ConfigPersistentStore test cases
 * @version $Id: ConfigPersistentStoreTest.java,v 1.1 2008-05-22 13:11:53 nichele Exp $
 */
public class ConfigPersistentStoreTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String DROP_FNBL_DEVICE_CONFIG =
        "drop table fnbl_device_config if exists";

    private static final String CREATE_FNBL_DEVICE_CONFIG =
        "create table fnbl_device_config (" +
        "  username     varchar(255) not null," +
        "  principal    bigint       not null," +
        "  uri          varchar(128) not null," +
        "  value        varchar(255) not null," +
        "  last_update  bigint       not null," +
        "  status       char         not null," +
        "  encrypted    boolean              ," +
        "  primary key (username, principal, uri)"  +
        ")";

    private static final String JNDI_USER_DB = "jdbc/fnbluser";

    private static final String USER_PART1 = "user_part1";
    private static final String USER_PART2 = "user_part2";
    private static final String USER_PART3 = "user_part3";

    private static final String DATASET_PART_1 =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/initial-db-dataset-part-1.xml";
    private static final String DATASET_PART_2 =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/initial-db-dataset-part-2.xml";
    private static final String DATASET_PART_3 =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/initial-db-dataset-part-3.xml";

    private static final String DELETE_ITEM_1_DATASET_PART_1  =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/delete-item-1-db-dataset-part-1.xml";
    private static final String DELETE_ITEM_2_DATASET_PART_1  =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/delete-item-2-db-dataset-part-1.xml";
    private static final String STORE_ITEM_1_DATASET_PART_2   =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/store-item-1-db-dataset-part-2.xml";
    private static final String STORE_ITEM_2_DATASET_PART_2   =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/store-item-2-db-dataset-part-2.xml";
    private static final String STORE_ITEM_3_DATASET_PART_2   =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/store-item-3-db-dataset-part-2.xml";
    private static final String REMOVE_CONFIGS_DATASET_PART_3 =
        "src/test/data/com/funambol/server/store/ConfigPersistentStore/remove-configs-db-dataset-part-3.xml";

    // ---------------------------------------------------------- Protected data
    protected IDatabaseTester userPart1DatabaseTester = null;
    protected IDatabaseTester userPart2DatabaseTester = null;
    protected IDatabaseTester userPart3DatabaseTester = null;

    protected IDatabaseConnection userPart1Conn = null;
    protected IDatabaseConnection userPart2Conn = null;
    protected IDatabaseConnection userPart3Conn = null;

    protected IDataSet userPart1DataSet = null;
    protected IDataSet userPart2DataSet = null;
    protected IDataSet userPart3DataSet = null;

    protected ConfigPersistentStore store = null;

    // ----------------------------------------------------------- Static method
    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            createConfigTable(USER_PART1);
            createConfigTable(USER_PART2);
            createConfigTable(USER_PART3);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------- Constructor
    public ConfigPersistentStoreTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        PSDeviceInventory inventory =
            (PSDeviceInventory)Configuration.getConfiguration().getDeviceInventory();
        store = inventory.getConfigPersistentStore();

        RoutingDataSource rds =
            (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);

        userPart1Conn = new DatabaseConnection(rds.getRoutedConnection(USER_PART1));
        userPart2Conn = new DatabaseConnection(rds.getRoutedConnection(USER_PART2));
        userPart3Conn = new DatabaseConnection(rds.getRoutedConnection(USER_PART3));

        userPart1DatabaseTester = new DefaultDatabaseTester(userPart1Conn);
        userPart2DatabaseTester = new DefaultDatabaseTester(userPart2Conn);
        userPart3DatabaseTester = new DefaultDatabaseTester(userPart3Conn);

        userPart1DataSet = new FlatXmlDataSet(new File(DATASET_PART_1));
        userPart1DatabaseTester.setDataSet(userPart1DataSet);
        userPart1DatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

        userPart2DataSet = new FlatXmlDataSet(new File(DATASET_PART_2));
        userPart2DatabaseTester.setDataSet(userPart2DataSet);
        userPart2DatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

        userPart3DataSet = new FlatXmlDataSet(new File(DATASET_PART_3));
        userPart3DatabaseTester.setDataSet(userPart3DataSet);
        userPart3DatabaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);

        DatabaseOperation.CLEAN_INSERT.execute(userPart1Conn, userPart1DataSet);
        DatabaseOperation.CLEAN_INSERT.execute(userPart2Conn, userPart2DataSet);
        DatabaseOperation.CLEAN_INSERT.execute(userPart3Conn, userPart3DataSet);
    }

    @Override
    protected void tearDown() throws Exception {

        DBTools.close(userPart1DatabaseTester.getConnection().getConnection(), null, null);
        DBTools.close(userPart2DatabaseTester.getConnection().getConnection(), null, null);
        DBTools.close(userPart3DatabaseTester.getConnection().getConnection(), null, null);

        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of count method, of class ConfigPersistentStore.
     */
    public void testCount_Object_Clause() throws Exception {
        Object o = null;
        com.funambol.framework.filter.Clause clause = null;
        assertEquals(-1, store.count(o, clause));
    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Class() throws Exception {
        assertNull(store.read((Class)null));
    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Object_Clause() throws Exception {
        Object o = null;
        com.funambol.framework.filter.Clause clause = null;
        assertNull(store.read(o, clause));
    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Clause() throws Exception {
        com.funambol.framework.filter.Clause clause = null;
        assertNull(store.read(clause));
    }

    /**
     * Test of count method, of class ConfigPersistentStore.
     */
    public void testCount_Sync4jPrincipal_Clause() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART1);
        Sync4jPrincipal principal = new Sync4jPrincipal(1, user, null);

        Clause all = new AllClause();
        int count = store.count(principal, all);
        assertEquals("Wrong number of ConfigItems,", 5, count);

        Clause uriClause = new WhereClause("uri", new String[] {"./Node"}, WhereClause.OPT_START_WITH, true);
        count = store.count(principal, uriClause);
        assertEquals("Wrong number of ConfigItems,", 4, count);

        Clause valueClause = new WhereClause("value", new String[] {"value"}, WhereClause.OPT_CONTAINS, true);
        count = store.count(principal, valueClause);
        assertEquals("Wrong number of ConfigItems,", 4, count);

        Clause lastUpdateClause = new WhereClause("last_update", new String[] {"1"}, WhereClause.OPT_LE, false);
        count = store.count(principal, lastUpdateClause);
        assertEquals("Wrong number of ConfigItems,", 3, count);

        Clause statusClause = new WhereClause("status", new String[] {"N"}, WhereClause.OPT_EQ, false);
        count = store.count(principal, statusClause);
        assertEquals("Wrong number of ConfigItems,", 3, count);

        LogicalClause combinedClauses = new LogicalClause(LogicalClause.OPT_AND,
                                                          new Clause[] {uriClause,
                                                                        valueClause,
                                                                        lastUpdateClause,
                                                                        statusClause});
        count = store.count(principal, combinedClauses);
        assertEquals("Wrong number of ConfigItems", 1, count);
    }

    /**
     * Test of delete method, of class ConfigPersistentStore.
     */
    public void testDelete() throws Exception {

        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART1);
        Sync4jPrincipal principal = new Sync4jPrincipal(1, user, null);

        boolean deleted = store.delete(new ConfigItem(USER_PART1, principal, "not-existing"));
        assertFalse(deleted);

        Clause all = new AllClause();
        int count = store.count(principal, all);
        assertEquals("Wrong number of ConfigItems,", 5, count);

        deleted = store.delete(new ConfigItem(USER_PART1, principal, "./Node1"));
        assertTrue(deleted);

        // Fetch database data after executing the delete
        IDataSet actualDataSet = null;
        FlatXmlDataSet expectedDataSet = null;

        actualDataSet = userPart1Conn.createDataSet();

        // Load expected data from an XML dataset
        expectedDataSet = new FlatXmlDataSet(new File(DELETE_ITEM_1_DATASET_PART_1));
        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {"last_update"});

        deleted = store.delete(new ConfigItem(USER_PART1, principal, "./Node1", null, new Timestamp(123)));
        assertTrue(deleted);

        // Load expected data from an XML dataset.
        expectedDataSet = new FlatXmlDataSet(new File(DELETE_ITEM_2_DATASET_PART_1));
        //
        // Since we set the last_update, we can include it in the comparison
        //
        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {});
    }

    /**
     * Test of store method, of class ConfigPersistentStore.
     */
    public void testStore() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART2);  // partition 2
        Sync4jPrincipal principal = new Sync4jPrincipal(20, user, null);

        ConfigItem item1 = new ConfigItem(USER_PART2, principal, "./NodeA", "ValueA", new Timestamp(1234), 'N');
        boolean ok = store.store(item1);

        assertTrue(ok);

        // Fetch database data
        IDataSet actualDataSet = null;
        FlatXmlDataSet expectedDataSet = null;
        actualDataSet = userPart2Conn.createDataSet();
        expectedDataSet = new FlatXmlDataSet(new File(STORE_ITEM_1_DATASET_PART_2));
        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {"last_update"});

        //
        // Updating the value
        //
        item1.setValue("ValueB");

        ok = store.store(item1);
        assertTrue(ok);

        // Fetch database data
        actualDataSet = userPart2Conn.createDataSet();
        expectedDataSet = new FlatXmlDataSet(new File(STORE_ITEM_2_DATASET_PART_2));

        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {"last_update"});

        //
        // Updating the value
        //
        item1.setValue("ValueC");
        item1.setLastUpdate(new Timestamp(1234567));

        ok = store.store(item1);
        assertTrue(ok);

        //
        // Updating also ./Email/Address (value and last_update)
        //
        principal.setId(10);
        ConfigItem item2 = new ConfigItem(USER_PART2, principal, "./Email/Address", "email2@address.com", new Timestamp(12345678), 'N');
        ok = store.store(item2);
        assertTrue(ok);

        ConfigItem item3 = new ConfigItem(USER_PART2, principal, "./NewURI", "new-value", new Timestamp(12345), 'N');
        ok = store.store(item3);
        assertTrue(ok);

        //
        // Updating also ./Email/Password (encrypted value)
        //
        ConfigItem item4 = new ConfigItem(USER_PART2, principal, "./Email/Password", "wg/6YJpKvvc=", new Timestamp(12345678), 'N', true);
        ok = store.store(item4);
        assertTrue(ok);

        // Fetch database data
        actualDataSet = userPart2Conn.createDataSet();
        expectedDataSet = new FlatXmlDataSet(new File(STORE_ITEM_3_DATASET_PART_2));

        //
        // Since in the previous store operations we set also the last update, we can include
        // that table in the comparison
        //
        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {});
    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Object_Part_1() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART1);
        Sync4jPrincipal principal = new Sync4jPrincipal(2, user, null);        
        ConfigItem item = new ConfigItem(USER_PART1, principal, "./Node2");
        boolean processed = store.read(item);
        assertTrue(processed);
        
        //
        // Comparing other values
        //
        assertEquals("Wrong value,", "value5", item.getValue());
        assertEquals("Wrong status,", 'U', item.getStatus());
        assertEquals("Wrong last update,", new Timestamp(1230), item.getLastUpdate());
       
        boolean notFound = false;
        item = new ConfigItem(USER_PART1, principal, "not-existing");
        try {
            processed = store.read(item);
            assertTrue(processed);
        } catch (NotFoundException e) {
            notFound = true;
        }
        
        assertTrue("item not expected, but NotFoundException not thrown", notFound);
        
    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Object_Part_3_DeletedItem() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART3);
        Sync4jPrincipal principal = new Sync4jPrincipal(100, user, null);        
        ConfigItem item = new ConfigItem(USER_PART3, principal, "./Node2");
        boolean processed = store.read(item);
        assertTrue(processed);
        
        //
        // Comparing other values
        //
        assertEquals("Wrong value,", "value2", item.getValue());
        assertEquals("Wrong status,", 'D', item.getStatus());
        assertEquals("Wrong last update,", new Timestamp(0), item.getLastUpdate());
       
        boolean notFound = false;
        item = new ConfigItem(USER_PART3, principal, "not-existing");
        try {
            processed = store.read(item);
            assertTrue(processed);
        } catch (NotFoundException e) {
            notFound = true;
        }
        
        assertTrue("item not expected, but NotFoundException not thrown", notFound);
        
    }
    
    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testRead_Sync4jPrincipal_Clause() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART1);
        Sync4jPrincipal principal = new Sync4jPrincipal(1, user, null);

        Clause all = new AllClause();
        ConfigItem[] items = (ConfigItem[])store.read(principal, all);
        assertEquals("Wrong number of ConfigItems,", 5, items.length);
        ConfigItem[] expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Email/Address", "email@address.com", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node2", "value2", new Timestamp(0), 'D'),
            new ConfigItem(USER_PART1, principal, "./Node3", "value3", new Timestamp(999999999), 'U'),
            new ConfigItem(USER_PART1, principal, "./Node4", "another value", new Timestamp(999999999), 'N')
        };
        assertEquivalenceArrays(expected, items);

        Clause uriClause = new WhereClause("uri", new String[] {"./Node"}, WhereClause.OPT_START_WITH, true);
        items = (ConfigItem[])store.read(principal, uriClause);
        assertEquals("Wrong number of ConfigItems,", 4, items.length);
        expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node2", "value2", new Timestamp(0), 'D'),
            new ConfigItem(USER_PART1, principal, "./Node3", "value3", new Timestamp(999999999), 'U'),
            new ConfigItem(USER_PART1, principal, "./Node4", "another value", new Timestamp(999999999), 'N')
        };
        assertEquivalenceArrays(expected, items);

        Clause valueClause = new WhereClause("value", new String[] {"value"}, WhereClause.OPT_CONTAINS, true);
        items = (ConfigItem[])store.read(principal, valueClause);
        assertEquals("Wrong number of ConfigItems,", 4, items.length);
        expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node2", "value2", new Timestamp(0), 'D'),
            new ConfigItem(USER_PART1, principal, "./Node3", "value3", new Timestamp(999999999), 'U'),
            new ConfigItem(USER_PART1, principal, "./Node4", "another value", new Timestamp(999999999), 'N')
        };
        assertEquivalenceArrays(expected, items);

        Clause lastUpdateClause = new WhereClause("last_update", new String[] {"1"}, WhereClause.OPT_LE, false);
        items = (ConfigItem[])store.read(principal, lastUpdateClause);
        assertEquals("Wrong number of ConfigItems,", 3, items.length);
        expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Email/Address", "email@address.com", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node2", "value2", new Timestamp(0), 'D')
        };
        assertEquivalenceArrays(expected, items);

        Clause statusClause = new WhereClause("status", new String[] {"N"}, WhereClause.OPT_EQ, false);
        items = (ConfigItem[])store.read(principal, statusClause);
        assertEquals("Wrong number of ConfigItems,", 3, items.length);
        expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Email/Address", "email@address.com", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N'),
            new ConfigItem(USER_PART1, principal, "./Node4", "another value", new Timestamp(999999999), 'N')
        };
        assertEquivalenceArrays(expected, items);

        LogicalClause combinedClauses = new LogicalClause(LogicalClause.OPT_AND,
                                                          new Clause[] {uriClause,
                                                                        valueClause,
                                                                        lastUpdateClause,
                                                                        statusClause});
        items = (ConfigItem[])store.read(principal, combinedClauses);
        assertEquals("Wrong number of ConfigItems", 1, items.length);
        expected = new ConfigItem[] {
            new ConfigItem(USER_PART1, principal, "./Node1", "value1", new Timestamp(0), 'N')
        };
        assertEquivalenceArrays(expected, items);

    }

    /**
     * Test of read method, of class ConfigPersistentStore.
     */
    public void testReadURIs_Sync4jPrincipal_Clause() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART1);
        Sync4jPrincipal principal = new Sync4jPrincipal(1, user, null);

        Clause all = new AllClause();
        String[] items = (String[])store.readURIs(principal, all);
        assertEquals("Wrong number of ConfigItems,", 5, items.length);
        String[] expectedURIs = new String[] {"./Email/Address", "./Node1", "./Node2", "./Node3", "./Node4"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);

        Clause uriClause = new WhereClause("uri", new String[] {"./Node"}, WhereClause.OPT_START_WITH, true);
        items = (String[])store.readURIs(principal, uriClause);
        assertEquals("Wrong number of ConfigItems,", 4, items.length);
        expectedURIs = new String[] {"./Node1", "./Node2", "./Node3", "./Node4"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);

        Clause valueClause = new WhereClause("value", new String[] {"value"}, WhereClause.OPT_CONTAINS, true);
        items = (String[])store.readURIs(principal, valueClause);
        assertEquals("Wrong number of ConfigItems,", 4, items.length);
        expectedURIs = new String[] {"./Node1", "./Node2", "./Node3", "./Node4"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);

        Clause lastUpdateClause = new WhereClause("last_update", new String[] {"1"}, WhereClause.OPT_LE, false);
        items = (String[])store.readURIs(principal, lastUpdateClause);
        assertEquals("Wrong number of ConfigItems,", 3, items.length);
        expectedURIs = new String[] {"./Email/Address", "./Node1", "./Node2"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);

        Clause statusClause = new WhereClause("status", new String[] {"N"}, WhereClause.OPT_EQ, false);
        items = (String[])store.readURIs(principal, statusClause);
        assertEquals("Wrong number of ConfigItems,", 3, items.length);
        expectedURIs = new String[] {"./Email/Address", "./Node1", "./Node4"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);

        LogicalClause combinedClauses = new LogicalClause(LogicalClause.OPT_AND,
                                                          new Clause[] {uriClause,
                                                                        valueClause,
                                                                        lastUpdateClause,
                                                                        statusClause});
        items = (String[])store.readURIs(principal, combinedClauses);
        assertEquals("Wrong number of ConfigItems", 1, items.length);
        expectedURIs = new String[] {"./Node1"};
        ArrayAssert.assertEquivalenceArrays(expectedURIs, items);
    }

    /**
     * Test of removeConfigItems method, of class ConfigPersistentStore.
     */
    public void testRemoveConfigItems() throws Exception {
        Sync4jUser user = new Sync4jUser();
        user.setUsername(USER_PART3);
        Sync4jPrincipal principal = new Sync4jPrincipal(200, user, null);        
        int count = store.removeConfigItems(principal);
        
        assertEquals("Wrong count of the removed rows,", 3, count);
        
        // Fetch database data after executing the delete
        IDataSet actualDataSet = null;
        FlatXmlDataSet expectedDataSet = null;

        actualDataSet = userPart3Conn.createDataSet();

        // Load expected data from an XML dataset
        expectedDataSet = new FlatXmlDataSet(new File(REMOVE_CONFIGS_DATASET_PART_3));
        compareTable(expectedDataSet, actualDataSet, "fnbl_device_config", new String[] {});        
    }

    // --------------------------------------------------------- Private methods
    private static void createConfigTable(String userId) throws Exception {
        //
        // fnbl_device_config is in the userdb
        //
        RoutingDataSource ds =
            (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getRoutedConnection(userId);

            stmt = conn.prepareStatement(DROP_FNBL_DEVICE_CONFIG);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getRoutedConnection(userId);
            stmt = conn.prepareStatement(CREATE_FNBL_DEVICE_CONFIG);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

    }

    private void assertEquivalenceArrays(ConfigItem[] expected, ConfigItem[] actual) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null) {
            fail("expected array is null");
        }
        if (actual == null) {
            fail("actual array is null");
        }
        assertEquals("Wrong size:", expected.length, actual.length);

        int size = expected.length;
        for (int i=0; i<size; i++) {
            ConfigItem expectedItem = expected[i];
            String expectedUri = expectedItem.getNodeURI();
            boolean found = false;
            for (int j=0; j<size; j++) {
                ConfigItem actualItem = actual[j];

                String actualUri = actualItem.getNodeURI();
                if (expectedUri.equals(actualUri)) {
                    found = true;
                    //
                    // Comparing other values
                    //
                    assertEquals("Wrong value for node '" + expectedUri + "',", expectedItem.getValue(), actualItem.getValue());
                    assertEquals("Wrong status for node '" + expectedUri + "',", expectedItem.getStatus(), actualItem.getStatus());
                    assertEquals("Wrong last update for node '" + expectedUri + "',", expectedItem.getLastUpdate(), actualItem.getLastUpdate());
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                fail("ConfigItem '" + expectedUri + "' not found");
            }
        }
    }

    private static void compareDatabase(IDataSet expectedDataSet,
                                        IDataSet actualDataSet )
    throws DataSetException, DatabaseUnitException {

        // do not continue if same instance
        if (expectedDataSet == actualDataSet) {
            return;
        }
        int expectedCount = expectedDataSet.getTableNames().length;
        int actualCount   = actualDataSet.getTableNames().length;

        if (expectedCount != actualCount) {
            System.out.println("Expected dataset: " + expectedDataSet);
            System.out.println("Actual dataset: " + actualDataSet);
        }
        // tables count
        assertEquals("table count", expectedCount, actualCount);

        String tableName = null;
        String[] excludedColumns = null;

        // fnbl_device_config table
        tableName = "fnbl_device_config";
        excludedColumns = new String[] {"last_update"};

        compareTable(expectedDataSet, actualDataSet, tableName, excludedColumns);

    }

    private static void compareTable(IDataSet expectedDataSet,
                                     IDataSet actualDataSet,
                                     String tableName,
                                     String[] excludedColumns)
    throws DataSetException, DatabaseUnitException {

        ITable expectedTable = null;
        ITable actualTable   = null;

        expectedTable = new SortedTable(
            DefaultColumnFilter.excludedColumnsTable(expectedDataSet.getTable(tableName),
                                                     excludedColumns));
        actualTable   = new SortedTable(
            DefaultColumnFilter.excludedColumnsTable(actualDataSet.getTable(tableName),
                                                     excludedColumns));

        Assertion.assertEquals(expectedTable, actualTable);
    }
}
