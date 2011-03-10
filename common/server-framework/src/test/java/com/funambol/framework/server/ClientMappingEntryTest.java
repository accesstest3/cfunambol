/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.funambol.framework.server;

import junit.framework.TestCase;


/**
 * Unit test for ClientMappingEntry
 * 
 * @author ste
 */
public class ClientMappingEntryTest extends TestCase {
    
    private final String TEST_GUID    = "1234567890";
    private final String TEST_GUID2   = "abcdefghij";
    private final String TEST_LUID    = "0987654321";
    private final String TEST_LUID2   = "jihgfedcba";
    private final String TEST_ANCHOR  = "1217989219";
    private final String TEST_ANCHOR2 = "0000000000";
    
    public ClientMappingEntryTest(String testName) {
        super(testName);
    }     
    
    
    public void testConstructorsGettersSetters() {
        ClientMappingEntry e = 
            new ClientMappingEntry(TEST_GUID, TEST_LUID, TEST_ANCHOR);
        
        assertEquals(TEST_GUID  , e.getGuid()      );
        assertEquals(TEST_LUID  , e.getLuid()      );
        assertEquals(TEST_ANCHOR, e.getLastAnchor());
        
        e.setGuid(TEST_GUID2);
        e.setLuid(TEST_LUID2);
        e.setLastAnchor(TEST_ANCHOR2);
        
        assertEquals(TEST_GUID2  , e.getGuid()      );
        assertEquals(TEST_LUID2  , e.getLuid()      );
        assertEquals(TEST_ANCHOR2, e.getLastAnchor());
        
        e = new ClientMappingEntry(TEST_GUID);
        
        assertEquals(TEST_GUID  , e.getGuid()     );
        assertNull(e.getLuid());
        assertNull(e.getLastAnchor());
    }

}
