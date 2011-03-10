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
package com.funambol.pimlistener.registry.dao;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

import junit.framework.TestCase;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.pimlistener.registry.PimRegistryEntry;
import com.funambol.pimlistener.registry.PimRegistryEntryAsserts;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.tools.test.DbTestUtils;

/**
 * Test class for <code>PimRegistryEntryDao</code> .
 * @version $Id: PimRegistryEntryDaoTest.java,v 1.2 2008-05-18 17:21:08 nichele Exp $
 */
public class PimRegistryEntryDaoTest extends TestCase {

    // --------------------------------------------------------------- Constants
    protected static final String USER_ID = "username";
    
    protected static final String TABLE_FNBL_PIM_LISTENER_REGISTRY =
        "fnbl_pim_listener_registry";

    protected static final String DATASET_CORE =
        "src/test/resources/database/PimRegistryEntryDao/dataset-coredb.xml";
    
    static {
        try {

            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private PimRegistryEntryDao pimRegistryEntryDao;
    
    protected IDatabaseTester dbTester = null;
    protected IDataSet dbDataSet = null;
    
    private static String resourcesDir = System.getProperty("funambol.home");

    // ------------------------------------------------------------ Constructors
    public PimRegistryEntryDaoTest(String testName) {
        super(testName);
    }            

    // -------------------------------------------------- setUp/tearDown methods
    @Override   
    protected void setUp() throws Exception {    
        super.setUp();

        DataSource ds = DataSourceTools.lookupDataSource("jdbc/fnblcore");
        Connection conn = ds.getConnection();
        dbTester = new DefaultDatabaseTester(new DatabaseConnection(conn));

        createAndInitDB(conn);

        dbDataSet = new FlatXmlDataSet(new File(DATASET_CORE));
        dbTester.setDataSet(dbDataSet);
        dbTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        dbTester.onSetup();

        pimRegistryEntryDao = new PimRegistryEntryDao();
    }

    @Override
    protected void tearDown() throws Exception {

        DBTools.close(dbTester.getConnection().getConnection(), null, null);

        dropDB(dbTester.getConnection().getConnection());

        super.tearDown();
    }
    
    // ------------------------------------------------------------ Test methods
    public void testUpdatePimRegistrEntry() throws Exception {
        
        ITable entries = dbDataSet.getTable(TABLE_FNBL_PIM_LISTENER_REGISTRY);
        PimRegistryEntry entry = createPimRegistryEntry(entries, 0);
        
        entry.setUserName("username 0");
        entry.setPushContacts(true);
        entry.setPushCalendars(true);
        entry.setPushNotes(true);
        
        pimRegistryEntryDao.updatePimRegistrEntry(entry);
        
        PimRegistryEntry result = pimRegistryEntryDao.getEntryById(entry.getId());
        PimRegistryEntry expected = entry;
        
        PimRegistryEntryAsserts.assertEquals(expected, result);
    }
    
    public void testGetEntryById() throws Exception {
        
        ITable expectedEntries = dbDataSet.getTable(TABLE_FNBL_PIM_LISTENER_REGISTRY);
        PimRegistryEntry expectedEntry = createPimRegistryEntry(expectedEntries, 0);
        
        PimRegistryEntry resultEntry = pimRegistryEntryDao.getEntryById(0);
        
        PimRegistryEntryAsserts.assertEquals(expectedEntry, resultEntry);
    }
    
    public void testGetEntriesByUserName() throws Exception {
        
        ITable tableTestSet = dbDataSet.getTable(TABLE_FNBL_PIM_LISTENER_REGISTRY);
        
        PimRegistryEntry expectedEntry1 = createPimRegistryEntry(tableTestSet, 0);
        PimRegistryEntry expectedEntry2 = createPimRegistryEntry(tableTestSet, 1);
        PimRegistryEntry expectedEntry3 = createPimRegistryEntry(tableTestSet, 2);
        
        List<PimRegistryEntry> expectedEntries = new ArrayList<PimRegistryEntry>();
        expectedEntries.add(expectedEntry1);
        expectedEntries.add(expectedEntry2);
        expectedEntries.add(expectedEntry3);
        
        List<PimRegistryEntry> resultEntries = pimRegistryEntryDao.getEntriesByUserName(USER_ID);
        
        for (int i = 0; i < expectedEntries.size(); i++) {
            PimRegistryEntry expectedEntry = expectedEntries.get(i);
            PimRegistryEntry resultEntry = resultEntries.get(i);
            
            PimRegistryEntryAsserts.assertEquals(expectedEntry, resultEntry);
        }
    }

    public void testInsertRegistryEntry() throws Exception {
        
        long id = 200;
        
        PimRegistryEntry expectedEntry = new PimRegistryEntry(id);
        
        expectedEntry.setPeriod(1001);
        expectedEntry.setActive(false);
        expectedEntry.setStatus("N");
        expectedEntry.setTaskBeanFile("com/funambol/pimlistener/task/PimListenerTask.xml");
        
        expectedEntry.setUserName(USER_ID);
        expectedEntry.setPushContacts (true);
        expectedEntry.setPushCalendars(true);
        expectedEntry.setPushNotes    (true);
        
        pimRegistryEntryDao.insertRegistryEntry(expectedEntry);
        PimRegistryEntry resultEntry = pimRegistryEntryDao.getEntryById(id);
        
        PimRegistryEntryAsserts.assertEquals(expectedEntry, resultEntry);
    }
    
    // --------------------------------------------------------- Private methods
    private PimRegistryEntry createPimRegistryEntry(ITable expectedEntries, int rowIndex) 
            throws DataSetException{
       
        PimRegistryEntry entry = new PimRegistryEntry(); 
        entry.setId(Integer.parseInt(
                (String)expectedEntries.getValue(rowIndex, "id")));
        entry.setUserName(
                (String)expectedEntries.getValue(rowIndex, "username"));
        entry.setPushContacts(
                expectedEntries.getValue(rowIndex, "push_contacts").equals("Y") ? true : false);
        entry.setPushCalendars(
                expectedEntries.getValue(rowIndex, "push_calendars").equals("Y") ? true : false);
        entry.setPushNotes(
                expectedEntries.getValue(rowIndex, "push_notes").equals("Y") ? true : false);
        
        return entry;
    }

    private static void createAndInitDB(Connection conn) {

        String[] scriptsToLoad = {
            "engine/drop_engine.ddl",
            "engine/create_engine.ddl",
            "engine/init_engine.sql",
            "foundation-connector/drop_pimlistener.sql",
            "foundation-connector/create_pimlistener.sql"
        };

        for (int i = 0; i < scriptsToLoad.length; i++) {
            String script = resourcesDir
                          + File.separatorChar + "database"
                          + File.separatorChar + scriptsToLoad[i];
            try {
                DbTestUtils.executeSqlFile(conn, script);
            } catch (Exception e) {
            }
        }
    }

    private static void dropDB(Connection conn) throws Exception {

        String[] scriptsToLoad = {
            "engine/drop_engine.ddl",
            "foundation-connector/drop_pimlistener.sql",
        };

        for (int i = 0; i < scriptsToLoad.length; i++) {
            String script = resourcesDir 
                          + File.separatorChar + "database"
                          + File.separatorChar + scriptsToLoad[i];
            try {
                DbTestUtils.executeSqlFile(conn, script);
            } catch (Exception e) {
            }
        }
    }
}