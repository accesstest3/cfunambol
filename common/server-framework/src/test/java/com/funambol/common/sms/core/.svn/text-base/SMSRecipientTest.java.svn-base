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

package com.funambol.common.sms.core;

import junit.framework.TestCase;

/**
 * SMSRecipient's test cases
 * @version $Id: SMSRecipientTest.java,v 1.1 2008-06-30 14:22:16 nichele Exp $
 */
public class SMSRecipientTest extends TestCase {
    
    public SMSRecipientTest(String testName) {
        super(testName);
    }            

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getRecipient method, of class SMSRecipient.
     */
    public void testSetGetRecipient() {
       SMSRecipient rec = new SMSRecipient("test-recipient");
       assertEquals("Wrong recipient", "test-recipient", rec.getRecipient());
       
       rec.setRecipient("test-recipient2");
       assertEquals("Wrong recipient", "test-recipient2", rec.getRecipient());
    }

    /**
     * Test of geCarrier method, of class SMSRecipient.
     */
    public void testSetGetCarrier() {
       SMSRecipient rec = new SMSRecipient("test-recipient", "test-carrier");
       assertEquals("Wrong carrier", "test-carrier", rec.getCarrier());
       
       rec.setCarrier("test-carrier2");
       assertEquals("Wrong carrier", "test-carrier2", rec.getCarrier());        
    }

    /**
     * Test of getCountryA2Code method, of class SMSRecipient.
     */
    public void testSetGetCountryA2Code() {
       SMSRecipient rec = new SMSRecipient("test-recipient", "test-carrier", "IT");
       assertEquals("Wrong coutry code", "IT", rec.getCountryA2Code());
       
       rec.setCountryA2Code("US");
       assertEquals("Wrong coutry code", "US", rec.getCountryA2Code());        
        
    }

    /**
     * Test of constructors
     */
    public void testConstructors() {
        SMSRecipient rec = new SMSRecipient("test-recipient", "test-carrier", "IT");
        assertEquals("Wrong recipient", "test-recipient", rec.getRecipient());
        assertEquals("Wrong carrier", "test-carrier", rec.getCarrier());
        assertEquals("Wrong coutry code", "IT", rec.getCountryA2Code());

        rec = new SMSRecipient(null, "test-carrier", "IT");
        assertNull("Wrong recipient", rec.getRecipient());
        assertEquals("Wrong carrier", "test-carrier", rec.getCarrier());
        assertEquals("Wrong coutry code", "IT", rec.getCountryA2Code());        
        
        rec = new SMSRecipient("test-recipient", null, "IT");
        assertEquals("Wrong recipient", "test-recipient", rec.getRecipient());
        assertNull("Wrong carrier", rec.getCarrier());
        assertEquals("Wrong coutry code", "IT", rec.getCountryA2Code());        
    
        rec = new SMSRecipient("test-recipient", "test-carrier", null);
        assertEquals("Wrong recipient", "test-recipient", rec.getRecipient());
        assertEquals("Wrong carrier", "test-carrier", rec.getCarrier());
        assertNull("Wrong coutry code", rec.getCountryA2Code());         
    }

    /**
     * Test of toString method, of class SMSRecipient.
     */
    public void testToString() {
        SMSRecipient rec = new SMSRecipient("test-recipient", "test-carrier", "IT");
        String t = rec.toString();
        System.out.println("toString: " + t);
        assertTrue(t.startsWith("com.funambol.common.sms.core.SMSRecipient@"));
        assertTrue(t.endsWith("[recipient=test-recipient,carrier=test-carrier,countryA2Code=IT]"));
        
        rec = new SMSRecipient(null, "test-carrier", "IT");
        t = rec.toString();
        System.out.println("toString: " + t);
        assertTrue(t.startsWith("com.funambol.common.sms.core.SMSRecipient@"));
        assertTrue(t.endsWith("[recipient=<null>,carrier=test-carrier,countryA2Code=IT]"));
        
        rec = new SMSRecipient("test-recipient", null, "IT");
        t = rec.toString();
        System.out.println("toString: " + t);
        assertTrue(t.startsWith("com.funambol.common.sms.core.SMSRecipient@"));
        assertTrue(t.endsWith("[recipient=test-recipient,carrier=<null>,countryA2Code=IT]"));
        
        rec = new SMSRecipient("test-recipient", "test-carrier", null);
        t = rec.toString();
        System.out.println("toString: " + t);
        assertTrue(t.startsWith("com.funambol.common.sms.core.SMSRecipient@"));
        assertTrue(t.endsWith("[recipient=test-recipient,carrier=test-carrier,countryA2Code=<null>]"));
        
        rec = new SMSRecipient(null, null, null);
        t = rec.toString();
        System.out.println("toString: " + t);
        assertTrue(t.startsWith("com.funambol.common.sms.core.SMSRecipient@"));
        assertTrue(t.endsWith("[recipient=<null>,carrier=<null>,countryA2Code=<null>]"));        
    }

}
