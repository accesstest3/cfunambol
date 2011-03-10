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

import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;
import java.util.Properties;
import junit.framework.TestCase;

/**
 * RoutingDataSourceConfiguration test cases
 * @version $Id: RoutingDataSourceConfigurationTest.java,v 1.3 2008-06-14 14:38:34 nichele Exp $
 */
public class RoutingDataSourceConfigurationTest extends TestCase {

    public RoutingDataSourceConfigurationTest(String testName) {
        super(testName);
    }

    public void test_read_from_xml_file() throws BeanException {
        String xmlFile = "com/funambol/server/db/routingds.xml";
        BeanTool beanTool =
            BeanTool.getBeanTool(System.getProperty("funambol.home") + "/config");
        RoutingDataSourceConfiguration config = (RoutingDataSourceConfiguration)beanTool.getBeanInstance(xmlFile);
        assertNotNull(config);
        assertTrue(config.getPartitioningCriteria() instanceof MockPartitioningCriteria);
        assertTrue(config.getPartitionConfigurationLoader() instanceof DataSourceConfigurationHelper);
    }
    
    public void test_clone() {
        
        Properties prop = new Properties();
        prop.setProperty("prop1", "value1");
        prop.setProperty("prop2", "value2");
        prop.setProperty("prop3", "value3");        
        
        PartitioningCriteria criteria = new MockPartitioningCriteria();
        PartitionConfigurationLoader loader = new DataSourceConfigurationHelper();
        
        RoutingDataSourceConfiguration config1 = new RoutingDataSourceConfiguration(prop, criteria, loader);
                
        RoutingDataSourceConfiguration config2 = config1.clone();
        
        assertSame(criteria, config2.getPartitioningCriteria());
        assertSame(loader, config2.getPartitionConfigurationLoader());
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
    
}
