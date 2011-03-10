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

import com.funambol.framework.tools.DBTools;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import javax.sql.DataSource;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/**
 * RoutingDataSource's testcases.
 * We use 3 in-memory hsqldb to simulate 3 partition.
 *
 * @version $Id: RoutingDataSourceTest.java,v 1.3 2008-06-14 09:40:14 nichele Exp $
 */
public class RoutingDataSourceTest extends TestCase {

    static {
        try {
            DriverManager.registerDriver(new org.hsqldb.jdbcDriver());

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Unable to load hsql driver");
        }
        try {
            initDBs();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError("Unable to init the DBs (partitions simulation)");
        }
    }

    public RoutingDataSourceTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test Cases

    /**
     * Test of getRoutedConnection method, of class RoutingDataSource.
     */
    public void testGetRoutedConnection() throws Exception {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:default");
        config.setProperty("username", "sa");
        config.setProperty("password", "");
        config.setPartitioningCriteria(new MockPartitioningCriteria());

        RoutingDataSource rds = new RoutingDataSource(config);
        try {
            rds.init();
            rds.configure();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error initializing/configuring the RoutingDataSource");
        }

        Connection con = null;
        PreparedStatement pStmt = null;

        //
        // Testing first partition
        //
        boolean partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part1");
            pStmt = con.prepareStatement("select * from user_part1;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);

        //
        // Testing second partition
        //
        partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part2");
            pStmt = con.prepareStatement("select * from user_part2;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);

        //
        // Testing third partition
        //
        partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part3");
            pStmt = con.prepareStatement("select * from user_part3;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);

    }

    /**
     * Test of getRoutedConnection method, of class RoutingDataSource.
     */
    public void testGetRoutedConnection_with_null_partitioning_criteria() throws Exception {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:core");
        config.setProperty("username", "sa");
        config.setProperty("password", "");

        //
        // With a null partitioning criteria the default datasource must be always used
        //
        RoutingDataSource rds = new RoutingDataSource(config);

        Connection con = null;
        PreparedStatement pStmt = null;

        //
        // Testing first partition
        //
        boolean partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part1");

            pStmt = con.prepareStatement("select * from core;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);

        //
        // Testing second partition
        //
        partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part2");
            pStmt = con.prepareStatement("select * from core;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);

        //
        // Testing third partition
        //
        partitionOK = true;
        try {
            con = rds.getRoutedConnection("user_part3");
            pStmt = con.prepareStatement("select * from core;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);
    }

    /**
     * Test of getRoutedConnection method, of class RoutingDataSource.
     */
    public void testGetRoutedConnection_with_a_null_partition() throws Exception {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:core");
        config.setProperty("username", "sa");
        config.setProperty("password", "");
        config.setPartitioningCriteria(new MockPartitioningCriteria());

        RoutingDataSource rds = new RoutingDataSource(config);

        Connection con = null;
        PreparedStatement pStmt = null;

        //
        // With a null partition the default datasource must be always used
        //
        boolean partitionOK = true;
        try {
            con = rds.getRoutedConnection(null);
            pStmt = con.prepareStatement("select * from core;");
            pStmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
            partitionOK = false;
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue(partitionOK);
    }

    /**
     * Test of getRoutedConnection method, of class RoutingDataSource.
     */
    public void testGetRoutedConnectionWithPartitioningCriteriaException() throws Exception {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:default");
        config.setProperty("username", "sa");
        config.setProperty("password", "");
        config.setPartitioningCriteria(new MockPartitioningCriteria());

        RoutingDataSource rds = new RoutingDataSource(config);

        Connection con = null;
        PreparedStatement pStmt = null;

        boolean partitioningCriteriaException = false;
        try {
            con = rds.getRoutedConnection("exception");
            pStmt = con.prepareStatement("select * from user_part1;");
            pStmt.execute();
        } catch (SQLException e) {
            if (e.getCause() instanceof PartitioningCriteriaException &&
                !(e.getCause() instanceof LockedPartitionException)) {
                partitioningCriteriaException = true;
            }
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue("Expected PartitioningCriteriaException not caught", partitioningCriteriaException);
    }

    /**
     * Test of getRoutedConnection method, of class RoutingDataSource.
     */
    public void testGetRoutedConnectionWithLockedPartitionException() throws Exception {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:default");
        config.setProperty("username", "sa");
        config.setProperty("password", "");
        config.setPartitioningCriteria(new MockPartitioningCriteria());

        RoutingDataSource rds = new RoutingDataSource(config);

        Connection con = null;
        PreparedStatement pStmt = null;

        boolean lockedException = false;
        try {
            con = rds.getRoutedConnection("locked");
            pStmt = con.prepareStatement("select * from user_part1;");
            pStmt.execute();
        } catch (SQLException e) {
            if (e.getCause() instanceof LockedPartitionException) {
                lockedException = true;
            }
        } finally {
            DBTools.close(con, pStmt, null);
        }
        assertTrue("Expected PartitioningCriteriaException not caught", lockedException);
    }

    public void testInitPartitions()
    throws DataSourceConfigurationException, NoSuchFieldException {

        RoutingDataSourceConfiguration config = new RoutingDataSourceConfiguration();
        config.setProperty("url", "jdbc:hsqldb:mem:default");
        config.setProperty("username", "sa");
        config.setProperty("password", "");

        RoutingDataSource rds = new RoutingDataSource(config);
        try {
            rds.init();
            rds.configure();
        } catch (Exception e) {
            e.printStackTrace();
            fail("Error initializing/configuring the RoutingDataSource");
        }

        Map<String, DataSource> dataSources =
            (Map)PrivateAccessor.getField(rds, "dataSources");

        assertNotNull(dataSources);

        assert(dataSources.size() == 3);

        DataSource part1Ds = dataSources.get("user_part1");
        DataSource part2Ds = dataSources.get("user_part2");
        DataSource part3Ds = dataSources.get("user_part3");

        assertNotNull(part1Ds);
        assertTrue(part1Ds instanceof BasicDataSource);
        BasicDataSource bPart1Ds = (BasicDataSource)part1Ds;
        assertEquals("sa", bPart1Ds.getUsername());
        assertEquals("", bPart1Ds.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part1", bPart1Ds.getUrl());

        assertNotNull(part2Ds);
        assertTrue(part2Ds instanceof BasicDataSource);
        BasicDataSource bPart2Ds = (BasicDataSource)part2Ds;
        assertEquals("sa", bPart2Ds.getUsername());
        assertEquals("", bPart2Ds.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part2", bPart2Ds.getUrl());

        assertNotNull(part3Ds);
        assertTrue(part3Ds instanceof BasicDataSource);
        BasicDataSource bPart3Ds = (BasicDataSource)part3Ds;
        assertEquals("sa", bPart3Ds.getUsername());
        assertEquals("", bPart3Ds.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part3", bPart3Ds.getUrl());

    }

    // --------------------------------------------------------- Private methods

    private static void initDBs() throws Exception {

        BasicDataSource defaultDS = new BasicDataSource();
        defaultDS.setUrl("jdbc:hsqldb:mem:core");
        defaultDS.setUsername("sa");
        defaultDS.setPassword("");
        Connection con = defaultDS.getConnection();
        PreparedStatement pStmt = con.prepareStatement("create table core(id int, name varchar);");
        pStmt.execute();
        DBTools.close(con, pStmt, null);

        BasicDataSource ds1 = new BasicDataSource();
        ds1.setUrl("jdbc:hsqldb:mem:user_part1");
        ds1.setUsername("sa");
        ds1.setPassword("");
        Connection con1 = ds1.getConnection();
        PreparedStatement pStmt1 = con1.prepareStatement("create table user_part1(id int, name varchar);");
        pStmt1.execute();
        DBTools.close(con1, pStmt1, null);

        BasicDataSource ds2 = new BasicDataSource();
        ds2.setUrl("jdbc:hsqldb:mem:user_part2");
        ds2.setUsername("sa");
        ds2.setPassword("");
        Connection con2 = ds2.getConnection();
        PreparedStatement pStmt2 = con2.prepareStatement("create table user_part2(id int, name varchar);");
        pStmt2.execute();
        DBTools.close(con1, pStmt1, null);

        BasicDataSource ds3 = new BasicDataSource();
        ds3.setUrl("jdbc:hsqldb:mem:user_part3");
        ds3.setUsername("sa");
        ds3.setPassword("");
        Connection con3 = ds3.getConnection();
        PreparedStatement pStmt3 = con3.prepareStatement("create table user_part3(id int, name varchar);");
        pStmt3.execute();
        DBTools.close(con1, pStmt1, null);
    }

}
