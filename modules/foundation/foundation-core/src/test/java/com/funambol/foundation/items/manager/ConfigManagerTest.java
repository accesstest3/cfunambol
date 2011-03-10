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
package com.funambol.foundation.items.manager;

import junit.framework.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.IDatabaseTester;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ConfigItem;
import com.funambol.framework.server.Sync4jUser;

import com.funambol.tools.database.DBHelper;

import com.funambol.foundation.util.TestDef;

/**
 * ConfigManager test cases.
 * @version $Id: ConfigManagerTest.java,v 1.1 2008-06-11 15:19:09 luigiafassina Exp $
 */
public class ConfigManagerTest extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants
    private static final String userId = DBHelper.USER_PART1;
    private static final String INIT_DATASET_PART_1 =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/server/inventory/PSDeviceInventory/initial-db-dataset-part-1.xml";

    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE,
                                                      USER_SCHEMA_SOURCE,
                                                      false);

            //assertTrue checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private IDatabaseTester userDbTesterPart1;

    // ------------------------------------------------------------ Constructors
    public ConfigManagerTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        userDbTesterPart1 =
            DBHelper.createDatabaseTester(userId,
                                          INIT_DATASET_PART_1,
                                          true);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        DBHelper.closeConnection(userDbTesterPart1);
    }

    // -------------------------------------------------------------- Test cases
    public void testGetItems() throws Exception {
        char state = 'N';
        Timestamp since = new Timestamp(1213103224421L);
        Timestamp to    = new Timestamp(1213103224468L);
        Sync4jUser user = new Sync4jUser();
        user.setUsername(userId);

        Sync4jPrincipal sp = new Sync4jPrincipal(1, user, null);
        ConfigManager instance = new ConfigManager(sp);

        List<ConfigItem> expResult = new ArrayList<ConfigItem>(2);
        expResult.add(new ConfigItem(userId, sp, "./Node1", "value1", new Timestamp(1213103224422L), 'N'));
        expResult.add(new ConfigItem(userId, sp, "./Node4", "another value", new Timestamp(1213103224460L), 'N'));

        List<ConfigItem> result = instance.getItems(state, since, to);
        assertEquivalenceArrays(expResult, result);

        expResult = new ArrayList<ConfigItem>(1);
        expResult.add(new ConfigItem(userId, sp, "./Node3", "value3", new Timestamp(999999999L), 'U'));

        result = instance.getItems('U', null, null);
        assertEquivalenceArrays(expResult, result);

        expResult = new ArrayList<ConfigItem>(4);
        expResult.add(new ConfigItem(userId, sp, "./Email/Address", "email@address.com", new Timestamp(0L), 'N'));
        expResult.add(new ConfigItem(userId, sp, "./Node0", "value0", new Timestamp(111111111L), 'N'));
        expResult.add(new ConfigItem(userId, sp, "./Node1", "value1", new Timestamp(1213103224422L), 'N'));
        expResult.add(new ConfigItem(userId, sp, "./Node4", "another value", new Timestamp(1213103224460L), 'N'));

        result = instance.getItems('N', null, new Timestamp(1213103224490L));
        assertEquivalenceArrays(expResult, result);
    }

    // --------------------------------------------------------- Private methods
    private void assertEquivalenceArrays(List<ConfigItem> expected,
                                         List<ConfigItem> actual  ) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected == null) {
            fail("expected array is null");
        }
        if (actual == null) {
            fail("actual array is null");
        }
        assertEquals("Wrong size:", expected.size(), actual.size());

        int size = expected.size();
        for (int i=0; i<size; i++) {
            ConfigItem expectedItem = expected.get(i);
            String expectedUri = expectedItem.getNodeURI();
            boolean found = false;
            for (int j=0; j<size; j++) {
                ConfigItem actualItem = actual.get(j);

                String actualUri = actualItem.getNodeURI();
                if (expectedUri.equals(actualUri)) {
                    found = true;
                    //
                    // Comparing other values
                    //
                    assertEquals("Wrong value for node '" + expectedUri + "',", expectedItem.getValue(), actualItem.getValue());
                    assertEquals("Wrong status for node '" + expectedUri + "',", expectedItem.getStatus(), actualItem.getStatus());
                    assertEquals("Wrong last update for node '" + expectedUri + "',", expectedItem.getLastUpdate(), actualItem.getLastUpdate());
                }
                if (found) {
                    break;
                }
            }
            if (!found) {
                fail("ConfigItem '" + expectedUri + "' not found");
            }
        }
    }

}
