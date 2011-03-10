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

import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.TestCase;

/**
 *
 * @author Filippo
 */
public class RegistryEntryTest extends TestCase {
    
    public RegistryEntryTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }



    /**
     * Test of hasProperty method, of class RegistryEntry.
     */
    public void testHasProperty() {
           RegistryEntry entry = new RegistryEntry();
           assertFalse(entry.hasProperty("someproperty"));

           entry.addProperty("property", "propertyValue");
           assertTrue(entry.hasProperty("property"));

           entry.addProperty("entryitself", entry);
           assertTrue(entry.hasProperty("entryitself"));
   }



    /**
     * Test of toString method, of class RegistryEntry.
     */
    public void testToString() {
           RegistryEntry entry = new RegistryEntry();
           entry.setActive(false);
           entry.setId(345);
           entry.setLastUpdate(843643242);
           entry.setPeriod(213213123);
           entry.setStatus("N");
           entry.setTaskBeanFile("com.funambol.tasks.TaskBeanFile");

           String toString = entry.toString();
           String expectedValue    = entry.getClass().getName()+"@"+Integer.toHexString(System.identityHashCode(entry))+"[id=345,period=213213123,active=false,taskBeanFile=com.funambol.tasks.TaskBeanFile,lastUpdate=843643242,status=N]";
           assertEquals(expectedValue,toString);

           entry.addProperty("username", "angelina");
           entry.addProperty("address", "localhost");
           entry.addProperty("port", 8080);

           toString = entry.toString();
           expectedValue    = entry.getClass().getName()+"@"+Integer.toHexString(System.identityHashCode(entry))+"[id=345,period=213213123,active=false,taskBeanFile=com.funambol.tasks.TaskBeanFile,lastUpdate=843643242,status=N,username=angelina,address=localhost,port=8080]";
           assertEquals(expectedValue,toString);


    }

    /**
     * Test of getProperties method, of class RegistryEntry.
     */
    public void testGetProperties() {
           RegistryEntry entry = new RegistryEntry();
           entry.setActive(false);
           entry.setId(345);
           entry.setLastUpdate(843643242);
           entry.setPeriod(213213123);
           entry.setStatus("N");
           entry.setTaskBeanFile("com.funambol.tasks.TaskBeanFile");
           entry.addProperty("username", "angelina");
           entry.addProperty("address", "localhost");
           entry.addProperty("port", 8080);

           assertTrue(entry.getProperty("Username")==null);
           assertEquals(8080, entry.getProperty("port"));
           assertEquals("localhost",entry.getProperty("address"));
    }

    /**
     * Test of setProperties method, of class RegistryEntry.
     */
    public void testSetProperties() {
           RegistryEntry entry = new RegistryEntry();
           entry.setActive(false);
           entry.setId(345);
           entry.setLastUpdate(843643242);
           entry.setPeriod(213213123);
           entry.setStatus("N");
           entry.setTaskBeanFile("com.funambol.tasks.TaskBeanFile");
           Map<String,Object> properties = new LinkedHashMap<String,Object>();
           properties.put("username", "angelina");
           properties.put("address", "localhost");
           properties.put("port", 8080);
           entry.setProperties(properties);

           assertTrue(entry.getProperties()!=null);
           assertEquals(3,entry.getProperties().size());
           assertTrue(entry.getProperty("Username")==null);
           assertEquals(8080, entry.getProperty("port"));
           assertEquals("localhost",entry.getProperty("address"));
    }

    /**
     * Test of addProperty method, of class RegistryEntry.
     */
    public void testAddProperty() {
       RegistryEntry entry = new RegistryEntry();
       assertFalse(entry.hasProperties());
       entry.addProperty("server", "Gmail");
       entry.addProperty("protocol", "pop3");
       entry.addProperty("address", "pop3.gmail.com");
       entry.addProperty("port", 587);
       assertTrue(entry.hasProperty("server"));
       assertTrue(entry.hasProperty("protocol"));
       assertTrue(entry.hasProperty("address"));
       assertTrue(entry.hasProperty("port"));
       assertEquals("Gmail",entry.getProperty("server"));
       assertEquals("pop3",entry.getProperty("protocol"));
       assertEquals("pop3.gmail.com",entry.getProperty("address"));
       assertEquals(587,entry.getProperty("port"));
    }

    /**
     * Test of getProperty method, of class RegistryEntry.
     */
    public void testGetProperty() {
       RegistryEntry entry = new RegistryEntry();
       assertTrue(entry.getProperty("name")==null);
       entry.addProperty("name", "Abigail");
       assertEquals("Abigail",entry.getProperty("name"));
    }

    /**
     * Test of removeProperty method, of class RegistryEntry.
     */
    public void testRemoveProperty() {
       RegistryEntry entry = new RegistryEntry();
       assertFalse(entry.hasProperties());

       entry.addProperty("model", "sv 650");
       assertTrue(entry.hasProperties());
       assertTrue(entry.hasProperty("model"));

       assertTrue(null==entry.removeProperty("age"));
       assertEquals("sv 650", entry.removeProperty("model"));

       assertFalse(entry.hasProperties());
       assertFalse(entry.hasProperty("model"));
    }

    /**
     * Test of hasProperties method, of class RegistryEntry.
     */
    public void testHasProperties() {
       RegistryEntry entry = new RegistryEntry();
       assertFalse(entry.hasProperties());

       entry.addProperty("age", 36.5);
       assertTrue(entry.hasProperties());

       entry.removeProperty("age");
       assertFalse(entry.hasProperties());
    }

}
