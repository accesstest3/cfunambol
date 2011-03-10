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

import com.funambol.framework.tools.DataSourceTools;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;

import javax.sql.DataSource;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;
import org.apache.commons.dbcp.BasicDataSource;

/**
 * DataSourceContextHelper's test cases
 * @version $Id: DataSourceContextHelperTest.java,v 1.3 2008-06-14 09:29:15 nichele Exp $
 */
public class DataSourceContextHelperTest extends TestCase {


    static {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
    }

    public DataSourceContextHelperTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void testconfigureAndBindDataSources() throws Throwable {
        DataSourceContextHelper.configureAndBindDataSources();

        BasicDataSource dsFnblds = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/fnblds");
        assertNotNull(dsFnblds);
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", dsFnblds.getUrl());
        assertEquals("db", dsFnblds.getUsername());
        assertEquals("", dsFnblds.getPassword());
        assertEquals("org.hsqldb.jdbcDriver", dsFnblds.getDriverClassName());

        BasicDataSource dsCore = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/fnblcore");
        assertNotNull(dsCore);
        assertEquals("jdbc:hsqldb:hsql://localhost/funambol", dsCore.getUrl());
        assertEquals("db", dsCore.getUsername());
        assertEquals("ss", dsCore.getPassword());
        assertEquals("org.hsqldb.jdbcDriver", dsCore.getDriverClassName());

        BasicDataSource dsUser = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/fnbluser");
        assertNotNull(dsUser);
        assertEquals("jdbc:hsqldb:hsql://localhost/user", dsUser.getUrl());
        assertEquals("fnbluserpwd", dsUser.getUsername());
        assertEquals("fnblpwd", dsUser.getPassword());
        assertEquals("org.fnbluser", dsUser.getDriverClassName());

        //
        // This test is to verify that the case (lower or upper) of the configuration 
        // file is taken in consideration
        //
        boolean found = true;
        BasicDataSource dsUserds2 = null;
        try {
            dsUserds2 = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/fnblUSER");
        } catch (NameNotFoundException e) {
            found = false;
        }

        assertFalse(found);

        BasicDataSource dsUPPER = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/UPPERCASE");
        assertNotNull(dsUPPER);
        assertEquals("jdbc:hsqldb:hsql://localhost/user", dsUPPER.getUrl());
        assertEquals("fnbluserpwd", dsUPPER.getUsername());
        assertEquals("fnblpwd", dsUPPER.getPassword());
        assertEquals("org.fnbluser", dsUPPER.getDriverClassName());
        //
        // This test is to verify that the case of the configuration file is taken
        // in consideration
        //
        found = true;
        BasicDataSource dsUPPER2 = null;
        try {
            dsUPPER2 = (BasicDataSource)DataSourceTools.lookupDataSource("jdbc/uppercase");
        } catch (NameNotFoundException e) {
            found = false;
        }
        assertFalse(found);
        
        RoutingDataSource routingDS =
            (RoutingDataSource)DataSourceTools.lookupDataSource("jdbc/fnbluser-routing-with-partition");
        routingDS.init();
        routingDS.configure();
        
        assertNotNull(routingDS);
        assertTrue(routingDS.getPartitioningCriteria() instanceof MockPartitioningCriteria);
                
        Map<String, DataSource> dataSources = 
                (Map<String, DataSource>) PrivateAccessor.getField(routingDS, "dataSources");
        assertTrue(dataSources.size() == 3);
        
        org.apache.tomcat.dbcp.dbcp.BasicDataSource ds1 = 
            (org.apache.tomcat.dbcp.dbcp.BasicDataSource)dataSources.get("user_part1");
        
        assertNotNull("user_part1 must be not null", ds1);
        assertEquals("sa", ds1.getUsername());
        assertEquals("", ds1.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part1", ds1.getUrl());
        assertEquals(15, ds1.getNumTestsPerEvictionRun());  // from db.xml
                
        org.apache.tomcat.dbcp.dbcp.BasicDataSource ds2 = 
            (org.apache.tomcat.dbcp.dbcp.BasicDataSource)dataSources.get("user_part2");

        assertNotNull("user_part2 must be not null", ds2);
        assertEquals("sa", ds2.getUsername());
        assertEquals("", ds2.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part2", ds2.getUrl());
        assertEquals(15, ds2.getNumTestsPerEvictionRun());  // from db.xml
        
        org.apache.tomcat.dbcp.dbcp.BasicDataSource ds3 = 
            (org.apache.tomcat.dbcp.dbcp.BasicDataSource)dataSources.get("user_part3");

        assertNotNull("user_part3 must be not null", ds3);
        assertEquals("sa", ds3.getUsername());
        assertEquals("", ds3.getPassword());
        assertEquals("jdbc:hsqldb:mem:user_part3", ds3.getUrl());
        assertEquals(15, ds3.getNumTestsPerEvictionRun());  // from db.xml
        
    }

    public void testBind() throws Throwable {
        Object objToBind = "test-bind";

        DataSourceContextHelper helper = new DataSourceContextHelper();

        String jndiName = "test";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});

        Object objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

        jndiName = "jdbc/test";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);

        jndiName = "jdbc/fnbl/core";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

        jndiName = "jdbc/fnbl/user";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

        jndiName = "jdbc/fnblcore2";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

        jndiName = "jdbc/fnbluser2";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

        jndiName = "jdbc/fnblds2";
        PrivateAccessor.invoke(
                     helper,
                     "bind",
                     new Class[] {Object.class, String.class},
                     new Object[] {objToBind, jndiName});
        objInContext = lookup(jndiName);
        assertEquals(objInContext, objToBind);

    }


    /**
     * Usefull method for troubleshooting
     * @throws java.lang.Exception
     */
    private static void printContext(String contextName) throws Exception {
        System.out.println("---------- Listing '" + contextName + "' ------------");
        InitialContext initialContext = new InitialContext();

        NamingEnumeration<NameClassPair> nameEnum = initialContext.list(contextName);
        if (nameEnum != null) {
            while (nameEnum.hasMore()) {
                NameClassPair name = nameEnum.next();
                String nameInSpace = name.getName();
                String className = name.getClassName();
                System.out.println("NameInSpace: " + nameInSpace + ", class: " + className);
            }
        }
        System.out.println("--------------------------------------------");
    }


    /**
     * Looks up a resource in the context
     * @param jndiName the jndi name
     * @return the resource
     * @throws java.lang.Exception if an error occurs
     */
    private Object lookup(String jndiName) throws Exception {
        InitialContext context = new InitialContext();
        return context.lookup(jndiName);
    }
}