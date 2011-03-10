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

package com.funambol.framework.server;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.SourceRef;

/**
 *
 */
public class CapabilitiesTest extends TestCase {

    public CapabilitiesTest(String testName) {
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

    // ------------------------------------------------------------------- Tests

    /**
     * Test of getDataStoreNames method, of class Capabilities.
     * @throws Throwable
     */
    public void testContainsDataStore() throws Throwable {

        Capabilities capabilities = createCapabilities(new Long(123),
                    new String[] {"CONTACTS", "CALENDAR"});

        assertTrue(capabilities.containsDataStore("CONTACTS"));
        assertTrue(capabilities.containsDataStore("CALENDAR"));
        assertFalse(capabilities.containsDataStore("EVENT"));
    }

    public void testContainsDataStore_CaseSensitive() throws Throwable {

        Capabilities capabilities = createCapabilities(new Long(123),
                    new String[] {"Contacts", "CALENDAR"});

        assertTrue(capabilities.containsDataStore("CALENDAR"));
        assertFalse(capabilities.containsDataStore("Calendar"));
        assertFalse(capabilities.containsDataStore("calendar"));
        assertTrue(capabilities.containsDataStore("Contacts"));
        assertFalse(capabilities.containsDataStore("contacts"));
        assertFalse(capabilities.containsDataStore("CONTACTS"));
    }

    // -------------------------------------------------- Public Utility Methods

    /**
     * Create a simple Capabilities
     * @param id The Capabilities id
     * @param dataStoreNames the datastore names
     * @return a Capabilities
     */
    private Capabilities createCapabilities(Long id, String[] dataStoreNames) {
        Capabilities capabilities = new Capabilities();
        capabilities.setId(id);
        capabilities.setDevInf(createDevInf(dataStoreNames));
        return capabilities;
    }

    /**
     * Create a simple DevInf containing an array of simple DataStores
     * @param dataStoreNames
     * @return a DevInf
     */
    private DevInf createDevInf(String[] dataStoreNames) {
        DevInf devInf = new DevInf();

        List<DataStore> datastores = new ArrayList();
        for (String dataStoreName : dataStoreNames) {
            DataStore dataStore = new DataStore();
            dataStore.setSourceRef(new SourceRef(dataStoreName));

            datastores.add(dataStore);
        }
        devInf.setDataStores(datastores.toArray(new DataStore[datastores.size()]));
        return devInf;
    }

}
