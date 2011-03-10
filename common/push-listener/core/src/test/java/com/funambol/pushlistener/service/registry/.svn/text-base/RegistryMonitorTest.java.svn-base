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

package com.funambol.pushlistener.service.registry;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import junitx.util.PrivateAccessor;

import org.dbunit.IDatabaseTester;

import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.pushlistener.service.registry.dao.QueryDescriptor;
import com.funambol.pushlistener.service.registry.dao.RegistryDao;
import com.funambol.pushlistener.service.registry.dao.RegistryDaoTest;
import com.funambol.pushlistener.util.DBUnitHelper;
import com.funambol.pushlistener.util.DatabaseTester;

/**
 * Test cases for RegistryMonitor class.
 * 
 * @version $id$
 */
public class RegistryMonitorTest extends DatabaseTester {

    // ----------------------------------------------------------- Private data
    /**
     * So far, this registry monitor has been used only to test the conversion
     * between RegistryEntry and TaskConfigurationTask object.
     * The RegistryDao used by this instance is not valid, consider that before
     */
    private RegistryMonitor registryMonitor;
    private IDatabaseTester coreDatabaseTester;

    private static final String INITIAL_DATASET =
        "src/test/resources/data/com/funambol/pushlistener/service/registry/RegistryMonitor/initial-db-dataset-core.xml";
    private static final String UPDATE_DATASET =
        "src/test/resources/data/com/funambol/pushlistener/service/registry/RegistryMonitor/update-db-dataset-core.xml";

    private static final String REGISTRY_TABLE_NAME_BASIC   =
        "FNBL_PUSH_REGISTRY_BASIC";
    private static final String REGISTRY_TABLE_NAME_COMPLEX =
        "FNBL_PUSH_REGISTRY_COMPLEX";

    // ------------------------------------------------------------ Constructors
    public RegistryMonitorTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        coreDatabaseTester =
            DBUnitHelper.createDatabaseTester(INITIAL_DATASET, true);

        registryMonitor = new RegistryMonitor(1, 1, 1000, "", "", "", null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        
        DBUnitHelper.closeConnection(coreDatabaseTester);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of private getTaskConfiguration method, of class RegistryMonitor.
     * @throws Throwable
     */
    public void testGetTaskConfiguration() throws Throwable {
        RegistryEntry source = new RegistryEntry(12);
        source.setPeriod(1000);
        source.setActive(true);
        source.setLastUpdate(1234556666226L);
        source.setStatus(RegistryEntryStatus.NEW);
        source.setTaskBeanFile("com.funambol.task.ScheduledTask");
        TaskConfiguration taskConfiguration = getTaskConfiguration(registryMonitor, source);
        RegistryEntry     result = taskConfiguration2RegistryEntry(taskConfiguration);
        result.setLastUpdate(source.getLastUpdate());
        result.setStatus(source.getStatus());
        RegistryDaoTest.assertEquals(source, result);

        source = new RegistryEntry(12);
        source.setPeriod(1000);
        source.setActive(true);
        source.setLastUpdate(1234556666226L);
        source.setStatus(RegistryEntryStatus.NEW);
        source.setTaskBeanFile("com.funambol.task.ScheduledTask");
        Map<String,Object> expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("username", "angelina");
        source.setProperties(expectedProperties);
        taskConfiguration = getTaskConfiguration(registryMonitor, source);
        result = taskConfiguration2RegistryEntry(taskConfiguration);
        result.setLastUpdate(source.getLastUpdate());
        result.setStatus(source.getStatus());
        RegistryDaoTest.assertEquals(source, result);

        source = new RegistryEntry(12);
        source.setPeriod(1000);
        source.setActive(true);
        source.setLastUpdate(1234556666226L);
        source.setStatus(RegistryEntryStatus.NEW);
        source.setTaskBeanFile("com.funambol.task.ScheduledTask");
        expectedProperties = new HashMap<String, Object>();
        expectedProperties.put("bool_field", Boolean.FALSE);
        expectedProperties.put("old_bool_field", "N");
        expectedProperties.put("long_field", new Long(435245435235L));
        expectedProperties.put("decimal_field", new BigDecimal("1234567899876543221"));
        expectedProperties.put("double_field", new Double(225.67));
        expectedProperties.put("integer_field", "2267");
        expectedProperties.put("date_field", new Date(Date.valueOf("2009-06-17").getTime()));
        expectedProperties.put("timestamp_field", new Timestamp(new GregorianCalendar(2009, 06, 22, 19,30, 14).getTimeInMillis()));
        expectedProperties.put("not_valid_field", "not valid property");
        source.setProperties(expectedProperties);
        taskConfiguration = getTaskConfiguration(registryMonitor, source);
        result = taskConfiguration2RegistryEntry(taskConfiguration);
        result.setLastUpdate(source.getLastUpdate());
        result.setStatus(source.getStatus());
        RegistryDaoTest.assertEquals(source, result);
    }

    public void testSetClusterInformation() throws Exception {

        registryMonitor =
            new RegistryMonitor(1, 1, 1000, REGISTRY_TABLE_NAME_BASIC, "email.registryid", "", null);
        registryMonitor.setClusterInformation(1, 1);

        RegistryDao dao =
            (RegistryDao)PrivateAccessor.getField(registryMonitor, "dao");

        QueryDescriptor queryDesc =
            (QueryDescriptor)PrivateAccessor.getField(dao, "queryDesc");

        String result = queryDesc.getReadChangedEntriesQuery().trim();
        String expected =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  last_update >= ? and status = ?";
        assertEquals("Wrong queryReadChanged returned", expected, result);

        result = queryDesc.getReadActiveEntriesQuery().trim();
        expected =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  status != 'D' and active = ?";
        assertEquals("Wrong queryReadActive returned", expected, result);

        result = queryDesc.getDeleteEntriesQuery().trim();
        expected = "delete from " + REGISTRY_TABLE_NAME_BASIC + " where status = 'D'";
        assertEquals("Wrong queryDeleteEntries returned", expected, result);

        result = queryDesc.getDeleteEntryQuery().trim();
        expected = "delete from " + REGISTRY_TABLE_NAME_BASIC + " where id = ?";
        assertEquals("Wrong queryDeleteEntry returned", expected, result);

        // add a second cluster
        registryMonitor.setClusterInformation(2, 1);

        dao = (RegistryDao)PrivateAccessor.getField(registryMonitor, "dao");

        queryDesc = (QueryDescriptor)PrivateAccessor.getField(dao, "queryDesc");

        result = queryDesc.getReadChangedEntriesQuery().trim();
        expected =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  (id % 2 = 1)  and  last_update >= ? and status = ?";
        assertEquals("Wrong queryReadChanged returned for more cluster",
                     expected, result);

        result = queryDesc.getReadActiveEntriesQuery().trim();
        expected =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  (id % 2 = 1)  and  status != 'D' and active = ?";
        assertEquals("Wrong queryReadActive returned for more cluster",
                     expected, result);

        result = queryDesc.getDeleteEntriesQuery().trim();
        expected = "delete from " + REGISTRY_TABLE_NAME_BASIC +
                   " where status = 'D'  and  (id % 2 = 1)";
        assertEquals("Wrong queryDeleteEntries returned for more cluster",
                     expected, result);

        result = queryDesc.getDeleteEntryQuery().trim();
        expected = "delete from " + REGISTRY_TABLE_NAME_BASIC + " where id = ?";
        assertEquals("Wrong queryDeleteEntry returned for more cluster",
                     expected, result);
    }

    public void testQueryDescriptorHandling_MoreRegistryTables() throws Exception {

        coreDatabaseTester =
            DBUnitHelper.createDatabaseTester(UPDATE_DATASET, true);

        RegistryMonitor registryMonitorBasic =
            new RegistryMonitor(1, 1, 1000, REGISTRY_TABLE_NAME_BASIC, "email.registryid", "", null);
        RegistryMonitor registryMonitorComplex =
            new RegistryMonitor(1, 1, 1000, REGISTRY_TABLE_NAME_COMPLEX, "email.registryid", "", null);

        RegistryDao daoBasic =
            (RegistryDao)PrivateAccessor.getField(registryMonitorBasic, "dao");

        QueryDescriptor queryDescBasic =
            (QueryDescriptor)PrivateAccessor.getField(daoBasic, "queryDesc");

        String resultBasic = queryDescBasic.getReadChangedEntriesQuery().trim();
        String expectedBasic =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  last_update >= ? and status = ?";
        assertEquals("Wrong basic queryReadChanged returned",
                     expectedBasic, resultBasic);

        resultBasic = queryDescBasic.getReadActiveEntriesQuery().trim();
        expectedBasic =
            "select id, period, active, task_bean_file, last_update, status  " +
            "from " + REGISTRY_TABLE_NAME_BASIC +
            " where  status != 'D' and active = ?";
        assertEquals("Wrong basic queryReadActive returned",
                     expectedBasic, resultBasic);

        resultBasic = queryDescBasic.getDeleteEntriesQuery().trim();
        expectedBasic =
            "delete from " + REGISTRY_TABLE_NAME_BASIC + " where status = 'D'";
        assertEquals("Wrong basic queryDeleteEntries returned",
                     expectedBasic, resultBasic);

        resultBasic = queryDescBasic.getDeleteEntryQuery().trim();
        expectedBasic =
            "delete from " + REGISTRY_TABLE_NAME_BASIC + " where id = ?";
        assertEquals("Wrong basic queryDeleteEntry returned",
                     expectedBasic, resultBasic);

        RegistryDao daoComplex =
            (RegistryDao)PrivateAccessor.getField(registryMonitorComplex, "dao");

        QueryDescriptor queryDescComplex =
            (QueryDescriptor)PrivateAccessor.getField(daoComplex, "queryDesc");

        String resultComplex = queryDescComplex.getReadChangedEntriesQuery().trim();
        String expectedComplex =
            "select id, period, active, task_bean_file, last_update, status ," +
            " VAR_FIELD, BOOL_FIELD, OLD_BOOL_FIELD, LONG_FIELD, " +
            "DECIMAL_FIELD, DOUBLE_FIELD, INTEGER_FIELD, DATE_FIELD, " +
            "TIME_FIELD, TIMESTAMP_FIELD " +
            "from " + REGISTRY_TABLE_NAME_COMPLEX +
            " where  last_update >= ? and status = ?";
        assertEquals("Wrong queryReadChanged returned",
                     expectedComplex, resultComplex);

        resultComplex = queryDescComplex.getReadActiveEntriesQuery().trim();
        expectedComplex =
            "select id, period, active, task_bean_file, last_update, status ," +
            " VAR_FIELD, BOOL_FIELD, OLD_BOOL_FIELD, LONG_FIELD, " +
            "DECIMAL_FIELD, DOUBLE_FIELD, INTEGER_FIELD, DATE_FIELD, " +
            "TIME_FIELD, TIMESTAMP_FIELD " +
            "from " + REGISTRY_TABLE_NAME_COMPLEX +
            " where  status != 'D' and active = ?";
        assertEquals("Wrong queryReadActive returned",
                     expectedComplex, resultComplex);

        resultComplex = queryDescComplex.getDeleteEntriesQuery().trim();
        expectedComplex =
            "delete from " + REGISTRY_TABLE_NAME_COMPLEX +" where status = 'D'";
        assertEquals("Wrong queryDeleteEntries returned",
                     expectedComplex, resultComplex);

        resultComplex = queryDescComplex.getDeleteEntryQuery().trim();
        expectedComplex =
            "delete from " + REGISTRY_TABLE_NAME_COMPLEX + " where id = ?";
        assertEquals("Wrong queryDeleteEntry returned",
                     expectedComplex, resultComplex);
    }

    // --------------------------------------------------------- Private methods
    private TaskConfiguration getTaskConfiguration(RegistryMonitor monitor,
                                                   RegistryEntry entry)
    throws Throwable {

        Object obj = PrivateAccessor.invoke(monitor,
            "getTaskConfiguration",
            new Class[]{RegistryEntry.class}, new Object[]{entry});
        if (obj != null && obj instanceof TaskConfiguration) {
            return (TaskConfiguration) obj;
        } else {
            throw new Exception("Result object is not correct.");
        }
    }

    private RegistryEntry taskConfiguration2RegistryEntry(TaskConfiguration tc) {
        RegistryEntry result = new RegistryEntry();
        result.setActive(tc.isActive());
        result.setId(tc.getId());
        result.setPeriod(tc.getPeriod());
        result.setProperties(tc.getProperties());
        result.setTaskBeanFile(tc.getTaskBeanFile());
        return result;
    }
    
}
