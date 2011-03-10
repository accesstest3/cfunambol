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

import java.util.Iterator;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.framework.security.Sync4jPrincipal;

/**
 * Test cases for ClientMapping class.
 * @version $Id: ClientMappingTest.java 36691 2011-02-25 15:57:57Z nichele $
 */
public class ClientMappingTest extends TestCase {

    // ------------------------------------------------------------ Private data
    private static final ClientMapping instance =
        new ClientMapping(new Sync4jPrincipal(1), "card");
    private Map<String, ClientMappingEntry> mapping =
        new LinkedHashMap<String, ClientMappingEntry>();

    // ------------------------------------------------------------ Constructors
    public ClientMappingTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // key = GUID
        // ClientMappingEntry(GUID, LUID, LAST-ANCHOR)
        mapping.put("1234", new ClientMappingEntry("1234", "4321", "1234567890"));
        mapping.put("2345", new ClientMappingEntry("2345", "5432", "2345678910"));
        mapping.put("3456", new ClientMappingEntry("3456", "6543", "3456789120"));
        mapping.put("4567", new ClientMappingEntry("4567", "7654", "4567891230"));
        mapping.put("5678", new ClientMappingEntry("5678", "8765", ""));
        mapping.put("6789", new ClientMappingEntry("6789", "9876", null));

        instance.initializeFromMapping(mapping);

        PrivateAccessor.setField(instance, "mappingToDelete", 
                                 new LinkedHashMap<String, ClientMappingEntry>());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of initializeFromMapping method, of class ClientMapping.
     */
    public void testInitializeFromMapping() {

        List mappings = instance.getMapping();
        assertTrue("Wrong number of returned mappings", mappings.size() == 6);
        assertFalse(instance.isDeleted());
        assertFalse(instance.isModified());
        assertFalse(instance.isAdded());
    }

    /**
     * Test of getDeletedLuids method, of class ClientMapping.
     */
    public void testGetDeletedLuids() throws Exception {

        String[] result = instance.getDeletedLuids();
        assertEquals("Wrong number of deleted LUID", 0, result.length);

        instance.removeMappedValuesForGuid("1234", false);
        result = instance.getDeletedLuids();
        assertEquals("Found 0 or more than 1 deleted LUID", 1, result.length);

        // this GUID does not exist
        instance.removeMappedValuesForGuid("929292", false);
        result = instance.getDeletedLuids();
        assertEquals("Found 0 or more than 1 deleted LUID", 1, result.length);

        // remove a second GUID
        instance.removeMappedValuesForGuid("3456", false);
        result = instance.getDeletedLuids();
        assertEquals("Found 0 or more than 2 deleted LUID", 2, result.length);

        instance.removeMappedValuesForGuid("4567", true);
        Map<String, ClientMappingEntry> mappingToDelete =
            (LinkedHashMap<String, ClientMappingEntry>)
                PrivateAccessor.getField(instance, "mappingToDelete");
        assertEquals("Wrong number of mapping to delete", 1, mappingToDelete.size());

    }

    /**
     * Test of getModifiedLuids method, of class ClientMapping.
     */
    public void testGetModifiedLuids() {

        String[] result = instance.getModifiedLuids();
        assertEquals("Wrong number of modified LUID", 0, result.length);

        instance.updateLastAnchor("1234", "4321", "98778900");
        result = instance.getModifiedLuids();
        assertEquals("Found 0 or more than 1 modified LUID", 1, result.length);

        instance.updateMapping("2345", "543211", "77665500");
        result = instance.getModifiedLuids();
        assertEquals("Found 0 or more than 2 modified LUID", 2, result.length);

        // this GUID does not exist
        instance.updateLastAnchor("8764257", null, null);
        result = instance.getModifiedLuids();
        assertEquals("Found 0 or more than 2 modified LUID", 2, result.length);

    }

    /**
     * Test of getLastAnchor method, of class ClientMapping.
     */
    public void testGetLastAnchor() {
        String expResult = "4567891230";
        String result = instance.getLastAnchor("4567");
        assertEquals(expResult, result);

        // this GUID is not mapped
        result = instance.getLastAnchor("notmapped");
        assertNull("Found last anchor for a not mapped GUID", result);

        // this GUID has an empty last anchor
        result = instance.getLastAnchor("5678");
        assertEquals("Wrong last anchor found", "", result);

        // this GUID has a last anchor null
        result = instance.getLastAnchor("6789");
        assertNull("Found a last anchor not null", result);
    }

    /**
     * Test of getValidMapping method, of class ClientMapping.
     */
    public void testGetValidMapping() {

        // map of GUID LUID
        Map<String, String> expResult = new LinkedHashMap<String, String>();
        expResult.put("1234", "4321");
        expResult.put("2345", "5432");
        expResult.put("3456", "6543");
        expResult.put("4567", "7654");

        Map<String, String> result = instance.getValidMapping();
        assertEquals("Wrong map size", expResult.size(), result.size());
        
        Iterator<String> resultKeys = result.keySet().iterator();
        while(resultKeys.hasNext()) {
            String guid = resultKeys.next();
            assertTrue("No GUID found", expResult.containsKey(guid));
            assertEquals("Wrong LUID mapped with GUID",
                         expResult.get(guid), result.get(guid)); // LUID
        }
    }

    /**
     * Test of getMappedValueForLuid method, of class ClientMapping.
     */
    public void testGetMappedValueForLuid() {
        
        String expResult = "1234";
        String result = instance.getMappedValueForLuid("4321");
        assertEquals("Wrong GUID found", expResult, result);

        // this LUID is not mappend
        result = instance.getMappedValueForLuid("12345789987");
        assertNull("Found a GUID for not existing LUID", result);
    }

    /**
     * Test of getMappedValueForGuid method, of class ClientMapping.
     */
    public void testGetMappedValueForGuid() {

        String expResult = "7654";
        String result = instance.getMappedValueForGuid("4567");
        assertEquals("Wrong LUID found", expResult, result);

        // this GUID is not mapped
        result = instance.getMappedValueForGuid("notexist");
        assertNull("Found a LUID for not existing GUID", result);
    }

    /**
     * Test of removeMappedValuesForGuid method, of class ClientMapping.
     */
    public void testRemoveMappedValuesForGuid() throws Exception {

        boolean isDiffered = true;

        String guid = "1234";
        instance.removeMappedValuesForGuid(guid, isDiffered);
        String luid = instance.getMappedValueForGuid(guid);
        assertNotNull("Not found LUID", luid);
        Map<String, ClientMappingEntry> mappingToDelete =
            (LinkedHashMap<String, ClientMappingEntry>)
                PrivateAccessor.getField(instance, "mappingToDelete");
        assertEquals("Wrong number of mapping to delete", 1, mappingToDelete.size());

        // this GUID does not exist
        guid = "notexist";
        instance.removeMappedValuesForGuid(guid, isDiffered);
        luid = instance.getMappedValueForGuid(guid);
        assertNull("Found LUID", luid);
        mappingToDelete = (LinkedHashMap<String, ClientMappingEntry>)
            PrivateAccessor.getField(instance, "mappingToDelete");
        assertEquals("Wrong number of mapping to delete", 1, mappingToDelete.size());

        isDiffered = false;

        guid = "3456";
        instance.removeMappedValuesForGuid(guid, isDiffered);
        luid = instance.getMappedValueForGuid(guid);
        assertNull("Found LUID", luid);
        assertTrue("GUID not deleted", instance.isDeleted());

        // this GUID does not exist
        guid = "notexist";
        instance.removeMappedValuesForGuid(guid, isDiffered);
        luid = instance.getMappedValueForGuid(guid);
        assertNull("Found LUID", luid);
    }

    /**
     * Test of confirmRemoveMappedValuesForGuid method, of class ClientMapping.
     */
    public void testConfirmRemoveMappedValuesForGuid() throws Exception {
        
        String guid = "1234";
        instance.removeMappedValuesForGuid(guid, true);
        instance.confirmRemoveMappedValuesForGuid(guid);
        Map<String, ClientMappingEntry> mappingToDelete =
            (LinkedHashMap<String, ClientMappingEntry>)
                PrivateAccessor.getField(instance, "mappingToDelete");
        assertEquals("Wrong number of mapping to delete", 1, mappingToDelete.size());
        
        // this GUID does not exist
        guid = "notexist";
        instance.removeMappedValuesForGuid(guid, true);
        instance.confirmRemoveMappedValuesForGuid(guid);
        mappingToDelete =
            (LinkedHashMap<String, ClientMappingEntry>)
                PrivateAccessor.getField(instance, "mappingToDelete");
        assertEquals("Wrong number of mapping to delete", 1, mappingToDelete.size());

    }

    /**
     * Test of updateMapping method, of class ClientMapping.
     */
    public void testUpdateMapping() {

        String guid = "1234";
        String luid = "432111111";
        String lastAnchor = "989898989898";
        instance.updateMapping(guid, luid, lastAnchor);

        ClientMappingEntry expectedResult =
            new ClientMappingEntry(guid, luid, lastAnchor);
        ClientMappingEntry result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expectedResult, result);
        assertTrue("LUID not modified", instance.isModified());
        assertFalse("LUID not added", instance.isAdded());
        String[] modifiedLuids = instance.getModifiedLuids();
        String[] addedLuids = instance.getModifiedLuids();
        assertTrue("Wrong number of modified LUID", modifiedLuids.length == 1);
        assertTrue("Wrong number of added LUID", addedLuids.length == 1);

        // this GUID does not exist
        guid = "33445566";
        luid = "11223344";
        lastAnchor = "5577889900";
        instance.updateMapping(guid, luid, lastAnchor);

        expectedResult = new ClientMappingEntry(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expectedResult, result);

        modifiedLuids = instance.getModifiedLuids();
        addedLuids = instance.getAddedLuids();
        assertTrue("Wrong number of modified LUID", modifiedLuids.length == 1);
        assertTrue("Wrong number of added LUID", addedLuids.length == 1);

        // update empty last anchor
        guid = "5678";
        luid = "8765";
        lastAnchor = "7722334400";
        instance.updateMapping(guid, luid, lastAnchor);
        expectedResult = new ClientMappingEntry(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expectedResult, result);

        // update last anchor null
        guid = "6789";
        luid = "9876";
        lastAnchor = "8822334411";
        instance.updateMapping(guid, luid, lastAnchor);
        expectedResult = new ClientMappingEntry(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expectedResult, result);

        // update luid
        guid = "5678";
        luid = "246810";
        lastAnchor = "7722334400";
        instance.updateMapping(guid, luid, lastAnchor);
        expectedResult = new ClientMappingEntry(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expectedResult, result);

    }

    /**
     * Test of updateLastAnchor method, of class ClientMapping.
     */
    public void testUpdateLastAnchor() {

        ClientMappingEntry expected = null;
        ClientMappingEntry result   = null;

        // update the last anchor of an existing GUID
        String guid = "3456";
        String luid = "6543";
        String lastAnchor = "newanchor";
        expected = new ClientMappingEntry(guid, luid, lastAnchor);

        instance.updateLastAnchor(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expected, result);
        assertTrue("No mapping modified", instance.isModified());

        // this GUID does not exist in the mapping: do nothing
        instance.resetModifiedKeys();
        guid = "123123";
        luid = "321321";
        lastAnchor = "112233";
        instance.updateLastAnchor(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        assertNull("Found a mapping", result);
        assertFalse("Mapping modified", instance.isModified());

        // the GUID is mapped with a different LUID: no update last anchor
        instance.resetModifiedKeys();
        guid = "3456";
        luid = "1298";
        lastAnchor = "newanchor";
        expected = new ClientMappingEntry(guid, "6543", "newanchor");

        instance.updateLastAnchor(guid, luid, lastAnchor);
        result = instance.getClientMappingEntry(guid);
        compareClientMappingEntry(expected, result);
        assertFalse("Mapping modified", instance.isModified());

    }

    /**
     * Test of isValid method, of class ClientMapping.
     */
    public void testIsValid() {

        assertTrue("The given GUID is not valid", instance.isValid("2345"));

        // this GUID is not mapped
        assertFalse("Found a not mapped GUID", instance.isValid("guidnotexist"));

        // this GUID has an empty last anchor
        assertFalse("Found a valid GUID with empty last anchor",
                    instance.isValid("5678"));

        // this GUID has a null last anchor
        assertFalse("Found a valid GUID with last anchor null",
                    instance.isValid("6789"));
    }

    /**
     * Test of clearMappings method, of class ClientMapping.
     */
    public void testClearMappings() {
        
        instance.clearMappings();
        assertTrue("No all mappings has been removed",
                   instance.getMapping().isEmpty());
        assertFalse(instance.isModified());
        assertFalse(instance.isAdded());
        assertTrue(instance.isDeleted());
        String[] deletedLuids = instance.getDeletedLuids();
        assertTrue("Wrong number of deleted LUID", deletedLuids.length == 6);
        String[] modifiedLuids = instance.getModifiedLuids();
        assertTrue("Wrong number of modified LUID", modifiedLuids.length == 0);
    }

    /**
     * Test of resetModifiedKeys method, of class ClientMapping.
     */
    public void testResetModifiedKeys() {
        instance.resetModifiedKeys();
        assertFalse("Found key to delete", instance.isDeleted());
        assertFalse("Found key to modify", instance.isModified());
        assertFalse("Found key to added",  instance.isAdded());

        instance.removeMappedValuesForGuid("1234", false);
        instance.removeMappedValuesForGuid("2345", false);
        instance.updateLastAnchor("3456", "6543", "98778901");
        instance.updateLastAnchor("4567", "7654", "98778901");
        assertTrue("Not found key to delete", instance.isDeleted());
        assertTrue("Not found key to modify", instance.isModified());
        assertFalse("Found key to add", instance.isAdded());
        assertEquals("Found 0 or more than 2 LUID to delete",
                     2, instance.getDeletedLuids().length);
        assertEquals("Found 0 or more than 2 LUID to modify",
                     2, instance.getModifiedLuids().length);

        instance.resetModifiedKeys();
        assertFalse("Found key to delete", instance.isDeleted());
        assertFalse("Found key to modify", instance.isModified());
    }

    /**
     * Test of getClientMappingEntry method, of class ClientMapping.
     */
    public void testGetClientMappingEntry() {

        String guid = "3456";
        ClientMappingEntry expResult =
            new ClientMappingEntry(guid, "6543", "3456789120");
        ClientMappingEntry result = instance.getClientMappingEntry(guid);
        assertEquals(expResult, result);
        
        // this GUID is not mapped
        result = instance.getClientMappingEntry("notexist");
        assertNull("Found a ClientMappingEntry for no mapped GUID", result);

        // GUID = null
        result = instance.getClientMappingEntry(null);
        assertNull("Found a ClientMappingEntry for GUID null", result);
    }

    /**
     * Test of getClientMappingEntryByLuid method, of class ClientMapping.
     */
    public void testGetClientMappingEntryByLuid() {

        String luid = "7654";
        ClientMappingEntry expResult = 
            new ClientMappingEntry("4567", luid, "4567891230");
        ClientMappingEntry result = instance.getClientMappingEntryByLuid(luid);
        compareClientMappingEntry(expResult, result);

        // this LUID is not mapped
        result = instance.getClientMappingEntryByLuid("notexist");
        assertNull("Found a ClientMappingEntry of an existing LUID", result);

    }

    // --------------------------------------------------------- Private methods
    private void compareClientMappingEntry(ClientMappingEntry expected,
                                           ClientMappingEntry result  ) {

        if (result == null) {
            fail("Result to check is null");
        }

        assertEquals("Wrong GUID found", expected.getGuid(), result.getGuid());
        assertEquals("Wrong LUID found", expected.getLuid(), result.getLuid());
        assertEquals("Wrong last anchor found",
                     expected.getLastAnchor(), result.getLastAnchor());
    }
}
