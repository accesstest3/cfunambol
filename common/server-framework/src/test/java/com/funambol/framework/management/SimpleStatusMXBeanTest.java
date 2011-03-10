/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.framework.management;

import javax.management.Attribute;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import junit.framework.*;

/**
 *
 * @author ste
 */
public class SimpleStatusMXBeanTest extends TestCase {
    
    private SimpleStatusMXBean status;
    
    private static final String TEST_VALUE_VERSION         = "1.0.2"                      ;
    private static final String TEST_VALUE_VERSION2        = "1.0.5"                      ;
    private static final String TEST_ATTRIBUTE_LOAD_FACTOR = "loadFactor"                 ;
    private static final String TEST_VALUE_LOAD_FACTOR     = "10"                         ;
    private static final String TEST_ATTRIBUTE_CLUSTER     = "cluster"                    ;
    private static final String TEST_VALUE_CLUSTER         = "[192.168.0.10,192.168.0.12]";
    private static final String TEST_ATTRIBUTE_MY_PROPERTY = "myProperty"                 ;
    private static final String TEST_VALUE_MY_VALUE        = "myValue"                    ;

    private static final String TEST_VALUE_SERVICE_NAME_1  = "DS Server"                  ;
    private static final String TEST_VALUE_SERVICE_NAME_2  = "Pim Listener"               ;

    public SimpleStatusMXBeanTest(String testName) {
        super(testName);
    }
    
    public void init() throws Exception {
        status = new SimpleStatusMXBean();
        
        status.setAttribute(new Attribute(status.ATTRIBUTE_VERSION, TEST_VALUE_VERSION));
        status.setVersion(TEST_VALUE_VERSION);
        status.setServiceName(TEST_VALUE_SERVICE_NAME_1);
        status.setProperty(TEST_ATTRIBUTE_LOAD_FACTOR, TEST_VALUE_LOAD_FACTOR);
        status.setProperty(TEST_ATTRIBUTE_CLUSTER, TEST_VALUE_CLUSTER);
    }

    public void testGetVersion() throws Exception {
        init();
        
        assertEquals(TEST_VALUE_VERSION, status.getVersion());
    }

    
    public void testSetVersion() throws Exception {
        init();
        
        status.setVersion(TEST_VALUE_VERSION2);
        
        assertEquals(TEST_VALUE_VERSION2, status.getVersion());
    }
    
    public void testGetServiceName() throws Exception {
        init();
        
        assertEquals(TEST_VALUE_SERVICE_NAME_1, status.getServiceName());
    }

    
    public void testSetServiceName() throws Exception {
        init();
        
        status.setServiceName(TEST_VALUE_SERVICE_NAME_2);
        
        assertEquals(TEST_VALUE_SERVICE_NAME_2, status.getServiceName());
    }
    
    public void testGetStatus() throws Exception {
        init();
        
        String s = status.getStatus();

        if (!s.contains(TEST_VALUE_LOAD_FACTOR) ||
            !s.contains(TEST_ATTRIBUTE_CLUSTER)) {
            fail("The status doesn't contain the loadFactor or the cluster info");
        }
        assertEquals(status.getVersion(), TEST_VALUE_VERSION);

    }

}
