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

import java.util.Hashtable;
import java.util.Map;
import javax.naming.CompositeName;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.sql.DataSource;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;

/**
 * DataSourceFactory's test cases
 * @version $Id: DataSourceFactoryTest.java,v 1.3 2008-06-14 09:28:37 nichele Exp $
 */
public class DataSourceFactoryTest extends TestCase {

    static {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
    }

    public DataSourceFactoryTest(String testName) {
        super(testName);
    }


    // -------------------------------------------------------------- Test cases
    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_WrongClass() throws Exception {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnblds");

        //
        // not javax.sql.DataSource --> exception
        //
        Reference ref = new Reference("javax.sql.DataSourceeeee");
        Object result = null;
        boolean exception = false;

        try {
            result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        } catch (NamingException e) {
            exception = true;
        }
        assertTrue(exception);
    }


    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_WitWrongJNDIName() throws Exception {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnbl");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr url = new StringRefAddr("url", "jdbc:hsqldb:hsql://localhost/funambol");
        ref.add(url);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource);

        BasicDataSource bds = (BasicDataSource)result;
        assertNull(bds.getPassword());
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", bds.getUrl());
        //
        // These are read from the db.xml since there is not the fnbl.xml file
        //
        assertEquals("db", bds.getUsername());
        assertEquals("org.hsqldb.jdbcDriver", bds.getDriverClassName());

    }

    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnblds() throws Exception {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnblds");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr minIdle = new StringRefAddr("minIdle", "3");
        ref.add(minIdle);

        //
        // At the end this should be overwritten by the value in the fnblds.xml file
        //
        RefAddr url = new StringRefAddr("url", "this-is-the-url");
        ref.add(url);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource);

        BasicDataSource bds = (BasicDataSource)result;
        assertEquals(3, bds.getMinIdle());
        //
        // These are read from the db.xml
        //
        assertEquals("db", bds.getUsername());
        assertEquals("org.hsqldb.jdbcDriver", bds.getDriverClassName());

        //
        // These are read from the fnblds.xml
        //
        assertEquals("", bds.getPassword());
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", bds.getUrl());
    }


    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnblcore() throws Exception {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnblcore");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr minIdle = new StringRefAddr("minIdle", "3");
        ref.add(minIdle);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource);

        BasicDataSource bds = (BasicDataSource)result;
        assertEquals(3, bds.getMinIdle());
        //
        // These are read from the db.xml
        //
        assertEquals("db", bds.getUsername());
        assertEquals("org.hsqldb.jdbcDriver", bds.getDriverClassName());

        //
        // These are read from the fnblds.xml
        //
        assertEquals("ss", bds.getPassword());
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", bds.getUrl());
    }

    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnbluser() throws Exception {
        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnbluser");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr userName = new StringRefAddr("minIdle", "7");
        ref.add(userName);

        //
        // At the end this should be overwritten by the value in the fnblds.xml file
        //
        RefAddr url = new StringRefAddr("url", "this-is-the-url");
        ref.add(url);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof org.apache.tomcat.dbcp.dbcp.BasicDataSource);

        BasicDataSource bds = (BasicDataSource)result;
        assertEquals(7, bds.getMinIdle());

        //
        // These are read from the fnbluser.xml
        //
        assertEquals("fnbluserpwd", bds.getUsername());
        assertEquals("fnblpwd", bds.getPassword());
        assertEquals("org.fnbluser", bds.getDriverClassName());
        assertEquals("jdbc:hsqldb:hsql://localhost/user", bds.getUrl());

    }

    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnblcore_with_wrappedFactory() throws Exception {

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnblcore");

        Reference ref = new Reference("javax.sql.DataSource");

        RefAddr wrFactory = new StringRefAddr("wrappedFactory", "org.apache.commons.dbcp.BasicDataSourceFactory");
        ref.add(wrFactory);

        RefAddr minIdle = new StringRefAddr("minIdle", "3");
        ref.add(minIdle);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof org.apache.commons.dbcp.BasicDataSource);

        org.apache.commons.dbcp.BasicDataSource bds = (org.apache.commons.dbcp.BasicDataSource)result;
        assertEquals(3, bds.getMinIdle());
        //
        // These are read from the db.xml
        //
        assertEquals("db", bds.getUsername());
        assertEquals("org.hsqldb.jdbcDriver", bds.getDriverClassName());

        //
        // These are read from the fnblds.xml
        //
        assertEquals("ss", bds.getPassword());
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", bds.getUrl());
    }

    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnbluser_routing_no_partition() throws Exception {

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnbluser-routing-nopartition");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr userName = new StringRefAddr("minIdle", "7");
        ref.add(userName);

        //
        // At the end this should be overwritten by the value in the fnblds.xml file
        //
        RefAddr url = new StringRefAddr("url", "this-is-the-url");
        ref.add(url);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof RoutingDataSource);

        BasicDataSource bds = (BasicDataSource)PrivateAccessor.getField(result, "defaultDataSource");

        assertEquals(7, bds.getMinIdle());

        //
        // These are read from the fnbluser.xml
        //
        assertEquals("fnbluserpwd", bds.getUsername());
        assertEquals("fnblpwd", bds.getPassword());
        assertEquals("org.fnbluser", bds.getDriverClassName());
        assertEquals("jdbc:hsqldb:hsql://localhost/user", bds.getUrl());
        
        assertEquals(15, bds.getNumTestsPerEvictionRun()); // coming from db.xml
    }

    /**
     * Test of getObjectInstance method, of class DataSourceFactory.
     */
    public void testGetObjectInstance_fnbluser_routing_with_partition() throws Exception {

        DataSourceFactory dataSourceFactory = new DataSourceFactory();
        InitialContext context = new InitialContext();
        Hashtable environment = new Hashtable();

        Name name = new CompositeName("fnbluser-routing-with-partition");

        Reference ref = new Reference("javax.sql.DataSource");
        RefAddr userName = new StringRefAddr("minIdle", "7");
        ref.add(userName);

        //
        // At the end this should be overwritten by the value in the fnblds.xml file
        //
        RefAddr url = new StringRefAddr("url", "this-is-the-url");
        ref.add(url);

        Object result = dataSourceFactory.getObjectInstance(ref, name, context, environment);
        assertTrue(result instanceof RoutingDataSource);
        
        RoutingDataSource rds = (RoutingDataSource)result;
        rds.init();
        rds.configure();
        Map<String, DataSource> datasources = rds.getDataSources();
        assertEquals("Wrong number of datasources", 3, datasources.size());
        
        BasicDataSource ds1 = (BasicDataSource)datasources.get("user_part1");
        assertNotNull("user_part1 must be not null", ds1);
        assertEquals("sa", ds1.getUsername());
        assertEquals("", ds1.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part1", ds1.getUrl());
        assertEquals(15, ds1.getNumTestsPerEvictionRun());  // from db.xml
        assertEquals(7, ds1.getMinIdle()); // set with StringRefAddr
                
        BasicDataSource ds2 = (BasicDataSource)datasources.get("user_part2");
        assertNotNull("user_part2 must be not null", ds2);
        assertEquals("sa", ds2.getUsername());
        assertEquals("", ds2.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part2", ds2.getUrl());
        assertEquals(15, ds2.getNumTestsPerEvictionRun());  // from db.xml
        assertEquals(7, ds2.getMinIdle()); // set with StringRefAddr
        
        BasicDataSource ds3 = (BasicDataSource)datasources.get("user_part3");
        assertNotNull("user_part3 must be not null", ds3);
        assertEquals("sa", ds3.getUsername());
        assertEquals("", ds3.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part3", ds3.getUrl());
        assertEquals(15, ds3.getNumTestsPerEvictionRun());  // from db.xml
        assertEquals(7, ds3.getMinIdle()); // set with StringRefAddr
        
        BasicDataSource bds = (BasicDataSource)PrivateAccessor.getField(result, "defaultDataSource");

        assertEquals(7, bds.getMinIdle());

        //
        // These are read from the fnbluser.xml
        //
        assertEquals("fnbluserpartition", bds.getUsername());
        assertEquals("fnblpwd", bds.getPassword());
        assertEquals("org.fnbluser", bds.getDriverClassName());
        assertEquals("jdbc:hsqldb:hsql://localhost/user", bds.getUrl());
        
        assertTrue(rds.getPartitioningCriteria() instanceof MockPartitioningCriteria);
    }

}
