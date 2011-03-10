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

package com.funambol.server.db;

import java.util.Map;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * DataSourceConfigurationHelper test cases
 * @version $Id: DataSourceConfigurationHelperTest.java,v 1.4 2008-06-14 14:40:01 nichele Exp $
 */
public class DataSourceConfigurationHelperTest extends TestCase {

    public DataSourceConfigurationHelperTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of getDBConfiguration method, of class DataSourceConfigurationHelper.
     */
    public void testGetDBConfiguration() throws Exception {
        DataSourceConfiguration config = DataSourceConfigurationHelper.getDBConfiguration();

        assertEquals("db", config.getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));

        assertEquals("Wrong number of configuration properties.", 3, config.getProperties().size());
    }


    /**
     * Test of getDataSourceConfiguration method, of class DataSourceConfigurationHelper.
     */
    public void testGetDataSourceConfiguration() throws Exception {

        DataSourceConfiguration config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("fnblcore");

        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", config.getProperty("url"));
        assertEquals("db", config.getProperty("username"));
        assertEquals("ss", config.getProperty("password"));
        assertEquals("org.hsqldb.jdbcDriver", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 5, config.getProperties().size());

        config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("fnbluser");

        assertEquals("jdbc:hsqldb:hsql://localhost/user", config.getProperty("url"));
        assertEquals("fnbluserpwd", config.getProperty("username"));
        assertEquals("fnblpwd", config.getProperty("password"));
        assertEquals("org.fnbluser", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 5, config.getProperties().size());

        config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("fnblds");

        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", config.getProperty("url"));
        assertEquals("db", config.getProperty("username"));
        assertEquals("", config.getProperty("password"));
        assertEquals("org.hsqldb.jdbcDriver", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 5, config.getProperties().size());

        config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("not-existing");
        //
        // Expected the dbConfiguration (db.xml)
        //
        assertEquals("db", config.getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 3, config.getProperties().size());

        config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("fnbluser-routing-nopartition");

        assertTrue(config instanceof RoutingDataSourceConfiguration);

        assertEquals("jdbc:hsqldb:hsql://localhost/user", config.getProperty("url"));
        assertEquals("fnbluserpwd", config.getProperty("username"));
        assertEquals("fnblpwd", config.getProperty("password"));
        assertEquals("org.fnbluser", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 5, config.getProperties().size());

        assertNull(((RoutingDataSourceConfiguration)config).getPartitioningCriteria());

        config =
            DataSourceConfigurationHelper.getJDBCDataSourceConfiguration("fnbluser-routing-with-partition");

        assertTrue(config instanceof RoutingDataSourceConfiguration);

        assertEquals("jdbc:hsqldb:hsql://localhost/user", config.getProperty("url"));
        assertEquals("fnbluserpartition", config.getProperty("username"));
        assertEquals("fnblpwd", config.getProperty("password"));
        assertEquals("org.fnbluser", config.getProperty("driverClassName"));
        assertEquals("15", config.getProperty("numTestsPerEvictionRun"));
        assertEquals("Wrong number of configuration properties.", 5, config.getProperties().size());
        assertTrue(((RoutingDataSourceConfiguration)config).getPartitioningCriteria() instanceof
                   MockPartitioningCriteria);

    }

    /**
     * Test of getConfigurationFiles method, of class DataSourceConfigurationHelper.
     */
    public void testGetConfigurationFiles() {
        String[] expResult = new String[] {"fnblds.xml",
                                           "fnblcore.xml",
                                           "fnbluser.xml",
                                           "UPPERCASE.xml",
                                           "fnbluser-routing-nopartition.xml",
                                           "fnbluser-routing-with-partition.xml"};


        String[] result = DataSourceConfigurationHelper.getJDBCConfigurationFiles();

        assertEquals(expResult.length, result.length);

        boolean found = false;
        for (int i=0; i<expResult.length; i++) {
            found = false;
            for (int j=0; j<result.length; j++) {
                if (expResult[i].equals(result[j])) {
                    found = true;
                    continue;
                }
            }
            if (!found) {
                fail(expResult[i] + " not found");
            }
        }
    }

    /**
     * Test of getConfigurationFiles method, of class DataSourceConfigurationHelper.
     */
    public void testGetConfigurationFiles_in_partition_directory() {

        String[] expResult = new String[] {"user_part1.xml",
                                           "user_part2.xml",
                                           "user_part3.xml"};
        String[] result = DataSourceConfigurationHelper.getPartitionConfigurationFiles();

        assertEquals(expResult.length, result.length);

        boolean found = false;
        for (int i=0; i<expResult.length; i++) {
            found = false;
            for (int j=0; j<result.length; j++) {
                if (expResult[i].equals(result[j])) {
                    found = true;
                    continue;
                }
            }
            if (!found) {
                fail(expResult[i] + " not found");
            }
        }
    }

    public void testGetJDBCDataSourceConfigurations() throws DataSourceConfigurationException {
        Map<String, DataSourceConfiguration> configurations =
            DataSourceConfigurationHelper.getJDBCDataSourceConfigurations();

        assertTrue(configurations.size() == 6);

        DataSourceConfiguration fnblcoreConfig = configurations.get("fnblcore");
        assertNotNull(fnblcoreConfig);
        assertFalse(fnblcoreConfig instanceof RoutingDataSourceConfiguration);
        assertEquals("db", fnblcoreConfig.getProperties().getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", fnblcoreConfig.getProperties().getProperty("driverClassName"));
        assertEquals("ss", fnblcoreConfig.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", fnblcoreConfig.getProperties().getProperty("url"));

        DataSourceConfiguration fnbldsConfig = configurations.get("fnblds");
        assertNotNull(fnbldsConfig);
        assertFalse(fnbldsConfig instanceof RoutingDataSourceConfiguration);
        assertEquals("db", fnbldsConfig.getProperties().getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", fnbldsConfig.getProperties().getProperty("driverClassName"));
        assertEquals("", fnbldsConfig.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", fnbldsConfig.getProperties().getProperty("url"));

        DataSourceConfiguration fnbluserConfig = configurations.get("fnbluser");
        assertNotNull(fnbluserConfig);
        assertFalse(fnbluserConfig instanceof RoutingDataSourceConfiguration);
        assertEquals("fnbluserpwd", fnbluserConfig.getProperties().getProperty("username"));
        assertEquals("org.fnbluser", fnbluserConfig.getProperties().getProperty("driverClassName"));
        assertEquals("fnblpwd", fnbluserConfig.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/user", fnbluserConfig.getProperties().getProperty("url"));

        DataSourceConfiguration fnbluserRoutingNoPartitionConfig = configurations.get("fnbluser-routing-nopartition");
        assertNotNull(fnbluserRoutingNoPartitionConfig);
        assertTrue(fnbluserRoutingNoPartitionConfig instanceof RoutingDataSourceConfiguration);
        assertEquals("fnbluserpwd", fnbluserRoutingNoPartitionConfig.getProperties().getProperty("username"));
        assertEquals("org.fnbluser", fnbluserRoutingNoPartitionConfig.getProperties().getProperty("driverClassName"));
        assertEquals("fnblpwd", fnbluserRoutingNoPartitionConfig.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/user", fnbluserRoutingNoPartitionConfig.getProperties().getProperty("url"));

        DataSourceConfiguration fnbluserRoutingWithPartitionConfig = configurations.get("fnbluser-routing-with-partition");
        assertNotNull(fnbluserRoutingWithPartitionConfig);
        assertTrue(fnbluserRoutingNoPartitionConfig instanceof RoutingDataSourceConfiguration);
        assertTrue(((RoutingDataSourceConfiguration)(fnbluserRoutingWithPartitionConfig)).getPartitioningCriteria()
                    instanceof MockPartitioningCriteria);
        assertEquals("db", fnblcoreConfig.getProperties().getProperty("username"));
        assertEquals("org.fnbluser", fnbluserRoutingWithPartitionConfig.getProperties().getProperty("driverClassName"));
        assertEquals("fnbluserpartition", fnbluserRoutingWithPartitionConfig.getProperties().getProperty("username"));
        assertEquals("org.fnbluser", fnbluserRoutingWithPartitionConfig.getProperties().getProperty("driverClassName"));
        assertEquals("fnblpwd", fnbluserRoutingWithPartitionConfig.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/user", fnbluserRoutingWithPartitionConfig.getProperties().getProperty("url"));

        DataSourceConfiguration UPPERCASE = configurations.get("UPPERCASE");
        assertNotNull(UPPERCASE);
        assertFalse(UPPERCASE instanceof RoutingDataSourceConfiguration);
        assertEquals("fnbluserpwd", UPPERCASE.getProperties().getProperty("username"));
        assertEquals("org.fnbluser", UPPERCASE.getProperties().getProperty("driverClassName"));
        assertEquals("fnblpwd", UPPERCASE.getProperties().getProperty("password"));
        assertEquals("jdbc:hsqldb:hsql://localhost/user", UPPERCASE.getProperties().getProperty("url"));

        DataSourceConfiguration uppercase = configurations.get("uppercase");
        assertNull(uppercase);
    }


    public void testGetPartitionDataSourceConfigurations() throws DataSourceConfigurationException {
        DataSourceConfigurationHelper helper = new DataSourceConfigurationHelper();

        DataSourceConfiguration defaultConfig = helper.getDBConfiguration();

        Map<String, DataSourceConfiguration> configurations =
            helper.getPartitionConfigurations(defaultConfig);

        assertTrue(configurations.size() == 3);

        DataSourceConfiguration part1Config = configurations.get("user_part1");
        assertNotNull(part1Config);
        assertFalse(part1Config instanceof RoutingDataSourceConfiguration);
        assertEquals("sa", part1Config.getProperties().getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", part1Config.getProperties().getProperty("driverClassName"));
        assertEquals("jdbc:hsqldb:mem:user_part1", part1Config.getProperties().getProperty("url"));

        DataSourceConfiguration part2Config = configurations.get("user_part2");
        assertNotNull(part2Config);
        assertFalse(part1Config instanceof RoutingDataSourceConfiguration);
        assertEquals("sa", part2Config.getProperties().getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", part2Config.getProperties().getProperty("driverClassName"));
        assertEquals("jdbc:hsqldb:mem:user_part2", part2Config.getProperties().getProperty("url"));

        DataSourceConfiguration part3Config = configurations.get("user_part3");
        assertNotNull(part3Config);
        assertFalse(part1Config instanceof RoutingDataSourceConfiguration);
        assertEquals("sa", part3Config.getProperties().getProperty("username"));
        assertEquals("org.hsqldb.jdbcDriver", part2Config.getProperties().getProperty("driverClassName"));
        assertEquals("jdbc:hsqldb:mem:user_part3", part3Config.getProperties().getProperty("url"));
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     */
    public void testMergeConfiguration_1() {

        DataSourceConfiguration configuration1 = new DataSourceConfiguration();
        configuration1.setProperty("key11", "value11");
        configuration1.setProperty("key12", "value12");
        configuration1.setProperty("common", "value1");

        DataSourceConfiguration configuration2 = new DataSourceConfiguration();
        configuration2.setProperty("key21", "value21");
        configuration2.setProperty("key22", "value22");
        configuration2.setProperty("common", "value2");

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertFalse(result instanceof RoutingDataSourceConfiguration);

        DataSourceConfiguration expResult = new DataSourceConfiguration();
        expResult.setProperty("key11", "value11");
        expResult.setProperty("key12", "value12");
        expResult.setProperty("key21", "value21");
        expResult.setProperty("key22", "value22");
        expResult.setProperty("common", "value2");

        assertEquals(expResult.getProperties(), result.getProperties());
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     */
    public void testMergeConfiguration_withConfiguration1NULL() {

        DataSourceConfiguration configuration1 = null;

        DataSourceConfiguration configuration2 = new DataSourceConfiguration();
        configuration2.setProperty("key21", "value21");
        configuration2.setProperty("key22", "value22");

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertFalse(result instanceof RoutingDataSourceConfiguration);

        DataSourceConfiguration expResult = new DataSourceConfiguration();
        expResult.setProperty("key21", "value21");
        expResult.setProperty("key22", "value22");

        assertEquals(expResult.getProperties(), result.getProperties());
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     */
    public void testMergeConfiguration_withConfiguration2NULL() {

        DataSourceConfiguration configuration1 = new DataSourceConfiguration();
        configuration1.setProperty("key11", "value11");
        configuration1.setProperty("key12", "value12");

        DataSourceConfiguration configuration2 = null;

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertFalse(result instanceof RoutingDataSourceConfiguration);

        DataSourceConfiguration expResult = new DataSourceConfiguration();
        expResult.setProperty("key11", "value11");
        expResult.setProperty("key12", "value12");

        assertEquals(expResult.getProperties(), result.getProperties());
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     * configuration 1 --> RoutingDataSourceConfiguration
     * configuration 2 --> DataSourceConfiguration
     */
    public void testMergeConfiguration_with_RoutingDataSourceConfiguration1() {

        PartitioningCriteria partitioningCriteria = new MockPartitioningCriteria();
        RoutingDataSourceConfiguration configuration1 = new RoutingDataSourceConfiguration();
        configuration1.setProperty("key11", "value11");
        configuration1.setProperty("key12", "value12");
        configuration1.setProperty("common", "value1");
        configuration1.setPartitioningCriteria(partitioningCriteria);

        DataSourceConfiguration configuration2 = new DataSourceConfiguration();
        configuration2.setProperty("key21", "value21");
        configuration2.setProperty("key22", "value22");
        configuration2.setProperty("common", "value2");

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertTrue(result instanceof RoutingDataSourceConfiguration);

        RoutingDataSourceConfiguration expResult = new RoutingDataSourceConfiguration();
        expResult.setProperty("key11", "value11");
        expResult.setProperty("key12", "value12");
        expResult.setProperty("key21", "value21");
        expResult.setProperty("key22", "value22");
        expResult.setProperty("common", "value2");


        assertEquals(expResult.getProperties(), result.getProperties());

        assertSame(((RoutingDataSourceConfiguration)result).getPartitioningCriteria(),
                   configuration1.getPartitioningCriteria());
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     * configuration 1 --> DataSourceConfiguration
     * configuration 2 --> RoutingDataSourceConfiguration
     */
    public void testMergeConfiguration_with_RoutingDataSourceConfiguration2() {

        DataSourceConfiguration configuration1 = new DataSourceConfiguration();
        configuration1.setProperty("key11", "value11");
        configuration1.setProperty("key12", "value12");
        configuration1.setProperty("common", "value1");

        PartitioningCriteria partitioningCriteria = new MockPartitioningCriteria();
        RoutingDataSourceConfiguration configuration2 = new RoutingDataSourceConfiguration();
        configuration2.setProperty("key21", "value21");
        configuration2.setProperty("key22", "value22");
        configuration2.setProperty("common", "value2");
        configuration2.setPartitioningCriteria(partitioningCriteria);

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertTrue(result instanceof RoutingDataSourceConfiguration);

        RoutingDataSourceConfiguration expResult = new RoutingDataSourceConfiguration();
        expResult.setProperty("key11", "value11");
        expResult.setProperty("key12", "value12");
        expResult.setProperty("key21", "value21");
        expResult.setProperty("key22", "value22");
        expResult.setProperty("common", "value2");

        assertEquals(expResult.getProperties(), result.getProperties());

        assertSame(((RoutingDataSourceConfiguration)result).getPartitioningCriteria(),
                   configuration2.getPartitioningCriteria());
    }

    /**
     * Test of mergeConfiguration method, of class DataSourceConfiguration.
     * configuration 1 --> RoutingDataSourceConfiguration
     * configuration 2 --> RoutingDataSourceConfiguration
     */
    public void testMergeConfiguration_with_RoutingDataSourceConfiguration3() {

        PartitioningCriteria partitioningCriteria1 = new MockPartitioningCriteria();
        RoutingDataSourceConfiguration configuration1 = new RoutingDataSourceConfiguration();
        configuration1.setProperty("key11", "value11");
        configuration1.setProperty("key12", "value12");
        configuration1.setProperty("common", "value1");
        configuration1.setPartitioningCriteria(partitioningCriteria1);

        PartitioningCriteria partitioningCriteria2 = new MockPartitioningCriteria();
        RoutingDataSourceConfiguration configuration2 = new RoutingDataSourceConfiguration();
        configuration2.setProperty("key21", "value21");
        configuration2.setProperty("key22", "value22");
        configuration2.setProperty("common", "value2");
        configuration2.setPartitioningCriteria(partitioningCriteria2);

        DataSourceConfiguration result =
            DataSourceConfigurationHelper.mergeConfiguration(configuration1, configuration2);

        assertTrue(result instanceof RoutingDataSourceConfiguration);

        RoutingDataSourceConfiguration expResult = new RoutingDataSourceConfiguration();
        expResult.setProperty("key11", "value11");
        expResult.setProperty("key12", "value12");
        expResult.setProperty("key21", "value21");
        expResult.setProperty("key22", "value22");
        expResult.setProperty("common", "value2");

        assertEquals(expResult.getProperties(), result.getProperties());

        assertNotSame(((RoutingDataSourceConfiguration)result).getPartitioningCriteria(),
                   configuration1.getPartitioningCriteria());
        assertSame(((RoutingDataSourceConfiguration)result).getPartitioningCriteria(),
                   configuration2.getPartitioningCriteria());
    }

    /**
     * Test of StripXMLExtension
     */
    public void testStripXMLExtension() throws Throwable {

        DataSourceConfigurationHelper helper = new DataSourceConfigurationHelper();

        String fileName = "test.xml";

        String fileNameWithoutExtension = null;

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertEquals(fileNameWithoutExtension, "test");

        fileName = "test.XmL";

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertEquals(fileNameWithoutExtension, "test");

        fileName = "test";

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertNull(fileNameWithoutExtension);

        fileName = "test.txt";

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertNull(fileNameWithoutExtension);

        fileName = null;

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertNull(fileNameWithoutExtension);

        fileName = "";

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertNull(fileNameWithoutExtension);

        fileName = ".xml";

        fileNameWithoutExtension = (String) PrivateAccessor.invoke(
                     helper,
                     "stripXMLExtension",
                     new Class[] {String.class},
                     new Object[] {(fileName)});

        assertEquals(fileNameWithoutExtension, "");

    }

}
