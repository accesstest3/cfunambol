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

import java.util.Properties;
import junit.framework.TestCase;

/**
 * DataSourceConfiguration's test cases
 * @version $Id: DataSourceConfigurationTest.java,v 1.3 2008-06-14 14:39:38 nichele Exp $
 */
public class DataSourceConfigurationTest extends TestCase {

    public DataSourceConfigurationTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void test_clone() {
        DataSourceConfiguration config1 = new DataSourceConfiguration();
        config1.setProperty("prop1", "value1");
        config1.setProperty("prop2", "value2");
        config1.setProperty("prop3", "value3");
        
        DataSourceConfiguration config2 = config1.clone();
        assertEquals("value1", config2.getProperty("prop1"));
        assertEquals("value2", config2.getProperty("prop2"));
        assertEquals("value3", config2.getProperty("prop3"));
        assertEquals("Wrong number of properties." , 3, config2.getProperties().size());
        
        config2.setProperty("prop2", "value2-updated");
        
        assertEquals("value2-updated", config2.getProperty("prop2"));
        
        //
        // config1 must contain still the original value
        //        
        assertEquals("value1", config1.getProperty("prop1"));
        assertEquals("value2", config1.getProperty("prop2"));
        assertEquals("value3", config1.getProperty("prop3"));
        assertEquals("Wrong number of properties." , 3, config1.getProperties().size());
        
    }
    
    public void test_getBasicProperties() {
        DataSourceConfiguration config = new DataSourceConfiguration();
        config.setProperty("prop1", "value1");
        config.setProperty("prop2", "value2");
        config.setProperty("username", "this-is-the-user");
        config.setProperty("prop3", "value3");
        config.setProperty("driverClassName", "this-is-the-driver");
        config.setProperty("url", "this-is-the-url");
        config.setProperty("prop4", "value4");
        config.setProperty("password", "this-is-the-password");
        
        Properties basicProperties = config.getBasicProperties();
        assertEquals("Wrong number of properties.", 4, basicProperties.size());
        
        assertEquals("Wrong username value.", "this-is-the-user", basicProperties.getProperty("username"));
        assertEquals("Wrong password value.", "this-is-the-password", basicProperties.getProperty("password"));
        assertEquals("Wrong driverClassName value.", "this-is-the-driver", basicProperties.getProperty("driverClassName"));
        assertEquals("Wrong url value.", "this-is-the-url", basicProperties.getProperty("url"));
        
    }
    
    public void test_getProperties() {
        DataSourceConfiguration config = new DataSourceConfiguration();
        config.setProperty("prop1", "value1");
        config.setProperty("prop2", "value2");
        config.setProperty("username", "this-is-the-user");
        config.setProperty("prop3", "value3");
        config.setProperty("driverClassName", "this-is-the-driver");
        config.setProperty("url", "this-is-the-url");
        config.setProperty("prop4", "value4");
        config.setProperty("password", "this-is-the-password");
        
        Properties properties = config.getProperties();
        assertEquals("Wrong number of properties.", 8, properties.size());
        
        assertEquals("Wrong username value.", "this-is-the-user", properties.getProperty("username"));
        assertEquals("Wrong password value.", "this-is-the-password", properties.getProperty("password"));
        assertEquals("Wrong driverClassName value.", "this-is-the-driver", properties.getProperty("driverClassName"));
        assertEquals("Wrong url value.", "this-is-the-url", properties.getProperty("url"));
        assertEquals("Wrong prop1 value.", "value1", properties.getProperty("prop1"));
        assertEquals("Wrong prop2 value.", "value2", properties.getProperty("prop2"));
        assertEquals("Wrong prop3 value.", "value3", properties.getProperty("prop3"));
        assertEquals("Wrong prop4 value.", "value4", properties.getProperty("prop4"));
    }   
    
    public void test_clear() {
        DataSourceConfiguration config = new DataSourceConfiguration();
        config.setProperty("prop1", "value1");
        config.setProperty("prop2", "value2");
        config.setProperty("username", "this-is-the-user");
        config.setProperty("prop3", "value3");
        config.setProperty("driverClassName", "this-is-the-driver");
        config.setProperty("url", "this-is-the-url");
        config.setProperty("prop4", "value4");
        config.setProperty("password", "this-is-the-password");
        
        config.clear();
                
        assertEquals("Wrong number of properties.", 0, config.getProperties().size());
    }        
}
