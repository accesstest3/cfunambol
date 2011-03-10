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


package com.funambol.common.pim.converter;

import com.funambol.common.pim.common.Property;
import java.util.Map;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 *
 * @version $Id
 */
public class CalendarStatusTest extends TestCase {
    
    public CalendarStatusTest(String testName) {
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

    public void testMapServerStatus() {
        Property property = new Property();

        CalendarStatus expected = CalendarStatus.ACCEPTED;
        String serverValue      = "0";
        property.setPropertyValue(serverValue);
        CalendarStatus result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);
        assertEquals("ACCEPTED",result.getVCalICalValue(true));

        expected = CalendarStatus.SENT;
        serverValue      = "1";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.TENTATIVE;
        serverValue      = "2";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);
        
        expected = CalendarStatus.IN_PROCESS;
        serverValue      = "3";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.CONFIRMED;
        serverValue      = "4";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.COMPLETED;
        serverValue      = "5";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.NEEDS_ACTION;
        serverValue      = "6";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.DELEGATED;
        serverValue      = "7";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        expected = CalendarStatus.DECLINED;
        serverValue      = "8";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNotNull(result);
        assertEquals(expected, result);

        serverValue      = null;
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNull(result);

        serverValue      = "24";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNull(result);


        serverValue      = "hello";
        property.setPropertyValue(serverValue);
        result = CalendarStatus.mapServerStatus(property);
        assertNull(result);
    }

    public void testConfersionFromVCalICalToSIF() {
        String inputStatus = "ACCEPTED";
        CalendarStatus parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        short serverSideValue       = parsed.serverValue;
        assertEquals(0,serverSideValue);
        CalendarStatus retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("ACCEPTED", retranslated.getVCalICalValue(true));
        assertEquals("ACCEPTED", retranslated.getVCalICalValue(false));
        assertEquals("0", retranslated.getSifValue());

        inputStatus = "SENT";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(1,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("SENT", retranslated.getVCalICalValue(true));
        assertEquals(null, retranslated.getVCalICalValue(false));
        assertEquals("0", retranslated.getSifValue());

        inputStatus = "TENTATIVE";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(2,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("TENTATIVE", retranslated.getVCalICalValue(true));
        assertEquals("TENTATIVE", retranslated.getVCalICalValue(false));
        assertEquals("0", retranslated.getSifValue());

        inputStatus = "IN-PROCESS";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(3,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("CONFIRMED", retranslated.getVCalICalValue(true));
        assertEquals("IN-PROCESS", retranslated.getVCalICalValue(false));
        assertEquals("1", retranslated.getSifValue());


        inputStatus = "CONFIRMED";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(4,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("CONFIRMED", retranslated.getVCalICalValue(true));
        assertEquals("IN-PROCESS", retranslated.getVCalICalValue(false));
        assertEquals("1", retranslated.getSifValue());

        inputStatus = "COMPLETED";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(5,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("COMPLETED", retranslated.getVCalICalValue(true));
        assertEquals("COMPLETED", retranslated.getVCalICalValue(false));
        assertEquals("2", retranslated.getSifValue());

        inputStatus = "NEEDS ACTION";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(6,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("NEEDS ACTION", retranslated.getVCalICalValue(true));
        assertEquals("NEEDS-ACTION", retranslated.getVCalICalValue(false));
        assertEquals("3", retranslated.getSifValue());

        inputStatus = "NEEDS-ACTION";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(6,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("NEEDS ACTION", retranslated.getVCalICalValue(true));
        assertEquals("NEEDS-ACTION", retranslated.getVCalICalValue(false));
        assertEquals("3", retranslated.getSifValue());


        inputStatus = "DELEGATED";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(7,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("DELEGATED", retranslated.getVCalICalValue(true));
        assertEquals("DELEGATED", retranslated.getVCalICalValue(false));
        assertEquals("3", retranslated.getSifValue());

        inputStatus = "DECLINED";
        parsed       = CalendarStatus.mapVcalIcalStatus(inputStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(8,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("DECLINED", retranslated.getVCalICalValue(true));
        assertEquals("DECLINED", retranslated.getVCalICalValue(false));
        assertEquals("4", retranslated.getSifValue());
    }

    public void testConfersionFromSIFToVCalICal() {
        String inputSifStatus = "0";
        CalendarStatus parsed       = CalendarStatus.mapSifStatus(inputSifStatus);
        short serverSideValue       = parsed.serverValue;
        assertEquals(0,serverSideValue);
        CalendarStatus retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("ACCEPTED", retranslated.getVCalICalValue(true));
        assertEquals("ACCEPTED", retranslated.getVCalICalValue(false));
        assertEquals(inputSifStatus, retranslated.getSifValue());

        inputSifStatus = "1";
        parsed       = CalendarStatus.mapSifStatus(inputSifStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(4,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("CONFIRMED", retranslated.getVCalICalValue(true));
        assertEquals("IN-PROCESS", retranslated.getVCalICalValue(false));
        assertEquals(inputSifStatus, retranslated.getSifValue());

        inputSifStatus = "2";
        parsed       = CalendarStatus.mapSifStatus(inputSifStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(5,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("COMPLETED", retranslated.getVCalICalValue(true));
        assertEquals("COMPLETED", retranslated.getVCalICalValue(false));
        assertEquals(inputSifStatus, retranslated.getSifValue());

        inputSifStatus = "3";
        parsed       = CalendarStatus.mapSifStatus(inputSifStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(6,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("NEEDS ACTION", retranslated.getVCalICalValue(true));
        assertEquals("NEEDS-ACTION", retranslated.getVCalICalValue(false));
        assertEquals(inputSifStatus, retranslated.getSifValue());

        inputSifStatus = "4";
        parsed       = CalendarStatus.mapSifStatus(inputSifStatus);
        serverSideValue       = parsed.serverValue;
        assertEquals(8,serverSideValue);
        retranslated = CalendarStatus.mapServerStatus(new Property(""+serverSideValue));
        assertEquals("DECLINED", retranslated.getVCalICalValue(true));
        assertEquals("DECLINED", retranslated.getVCalICalValue(false));
        assertEquals(inputSifStatus, retranslated.getSifValue());
    }


    public void testMapVcalIcalStatus() throws NoSuchFieldException {
        Object obj = PrivateAccessor.getField(CalendarStatus.class, "vcalIcalMappings");
        if(obj!=null && obj instanceof Map) {
            Map<String,CalendarStatus> mappings = (Map) obj;
            assertNotNull(mappings);
            assertEquals(10,mappings.size());
        } else
            fail("Unable to find the mapping!");


        String vcalIcalValue = "ACCEPTED";
        CalendarStatus expected = CalendarStatus.ACCEPTED;
        CalendarStatus result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "SENT";
        expected = CalendarStatus.SENT;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        
        vcalIcalValue = "TENTATIVE";
        expected = CalendarStatus.TENTATIVE;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "IN-PROCESS";
        expected = CalendarStatus.IN_PROCESS;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "CONFIRMED";
        expected = CalendarStatus.CONFIRMED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "COMPLETED";
        expected = CalendarStatus.COMPLETED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "NEEDS-ACTION";
        expected = CalendarStatus.NEEDS_ACTION;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "NEEDS ACTION";
        expected = CalendarStatus.NEEDS_ACTION;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "DELEGATED";
        expected = CalendarStatus.DELEGATED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = "DECLINED";
        expected = CalendarStatus.DECLINED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNotNull(result);
        assertEquals(expected, result);

        vcalIcalValue = null;
        expected = CalendarStatus.ACCEPTED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNull(result);

        vcalIcalValue = "HELLO";
        expected = CalendarStatus.ACCEPTED;
        result =   CalendarStatus.mapVcalIcalStatus(vcalIcalValue);
        assertNull(result);
    }

    public void testMapSifStatus() {
        String sifStatus = "0";
        CalendarStatus expected = CalendarStatus.ACCEPTED;
        CalendarStatus result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNotNull(result);
        assertEquals(expected, result);

        sifStatus = "1";
        expected = CalendarStatus.CONFIRMED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNotNull(result);
        assertEquals(expected, result);

        sifStatus = "2";
        expected = CalendarStatus.COMPLETED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNotNull(result);
        assertEquals(expected, result);

        sifStatus = "3";
        expected = CalendarStatus.NEEDS_ACTION;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNotNull(result);
        assertEquals(expected, result);

        sifStatus = "4";
        expected = CalendarStatus.DECLINED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNotNull(result);
        assertEquals(expected, result);

        sifStatus = null;
        expected = CalendarStatus.ACCEPTED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNull(result);
        

        sifStatus = "18";
        expected = CalendarStatus.ACCEPTED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNull(result);

        sifStatus = "hello";
        expected = CalendarStatus.ACCEPTED;
        result =   CalendarStatus.mapSifStatus(sifStatus);
        assertNull(result);
        
    }

    public void testGetICalVCalValue() {
        CalendarStatus calendarStatus = CalendarStatus.ACCEPTED;
        String expectedIcal = "ACCEPTED";
        String expectedVcal = "ACCEPTED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.SENT;
        expectedIcal = null;
        expectedVcal = "SENT";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.TENTATIVE;
        expectedIcal = "TENTATIVE";
        expectedVcal = "TENTATIVE";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.IN_PROCESS;
        expectedIcal = "IN-PROCESS";
        expectedVcal = "CONFIRMED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.CONFIRMED;
        expectedIcal = "IN-PROCESS";
        expectedVcal = "CONFIRMED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.COMPLETED;
        expectedIcal = "COMPLETED";
        expectedVcal = "COMPLETED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.NEEDS_ACTION;
        expectedIcal = "NEEDS-ACTION";
        expectedVcal = "NEEDS ACTION";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.DELEGATED;
        expectedIcal = "DELEGATED";
        expectedVcal = "DELEGATED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));

        calendarStatus = CalendarStatus.DECLINED;
        expectedIcal = "DECLINED";
        expectedVcal = "DECLINED";
        assertEquals(expectedVcal, calendarStatus.getVCalValue());
        assertEquals(expectedIcal, calendarStatus.getICalValue());
        assertEquals(expectedVcal, calendarStatus.getVCalICalValue(true));
        assertEquals(expectedIcal, calendarStatus.getVCalICalValue(false));
    }

}
