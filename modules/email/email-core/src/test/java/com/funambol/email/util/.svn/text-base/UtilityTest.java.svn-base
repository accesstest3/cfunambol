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

package com.funambol.email.util;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import junit.framework.TestCase;

/**
 *
 */
public class UtilityTest extends TestCase {

    private static GregorianCalendar toCalendar(int year, int month, int day, int hour, int minutes, int seconds) {
       GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
       // Resetting ms
       calendar.set(Calendar.MILLISECOND, 0);
       // Setting the expected date
       calendar.set(year, month-1, day, hour, minutes, seconds);

       return calendar;
    }

    private static String[]            UTC_DATES = new String[]{
                                                        "malformedstring",
                                                        null,
                                                        "20090406T103552Z",
                                                        "19791022T060503Z",
                                                        "20081117T093000Z",
                                                        "20090408T094227Z",
                                                        "20090211T000304Z"
                                                        };
    private static GregorianCalendar[] CALENDARS = new GregorianCalendar[]{
                        null,
                        null,
                        toCalendar(2009, 04, 06, 10, 35, 52),
                        toCalendar(1979, 10, 22, 6, 5, 3),
                        toCalendar(2008, 11, 17, 9, 30, 0),
                        toCalendar(2009, 04, 8, 9, 42, 27),
                        toCalendar(2009, 02, 11, 0, 3, 4)
                        };


    public UtilityTest(String testName) {
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
     * Test of getKeyFIDPart method, of class Utility.
     */
    public void testGetKeyFIDPart_FidI() {
        int partIndex = 1;
        String key = "I/1234567";
        String expResult = "I";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFIDPart_FidInbox() {
        int partIndex = 1;
        String key = "Inbox/1234567";
        String expResult = "Inbox";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFIDPart_OnlyFidI() {
        int partIndex = 1;
        String key = "I";
        String expResult = "I";
        String result = Utility.getKeyPart(key, partIndex);
        // it should return "key" but for backward compatibility it 
        // returns null
        // assertEquals(expResult, result);
        assertNull(result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFIDPart_OnlyFidInbox() {
        int partIndex = 1;
        String key = "Inbox";
        String expResult = "Inbox";
        String result = Utility.getKeyPart(key, partIndex);
        // it should return "key" but for backward compatibility it 
        // returns null
        // assertEquals(expResult, result);
        assertNull(result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidIFmidWithSep() {
        int partIndex = 2;
        String key = "I/123-FUN-1234567";
        String expResult = "123";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidIFmidWithoutSep() {
        int partIndex = 2;
        String key = "I/1234567";
        String expResult = "1234567";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }
    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidInboxFmidWithSep() {
        int partIndex = 2;
        String key = "Inbox/123-FUN-1234567";
        String expResult = "123";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidInboxFmidWithoutSep() {
        int partIndex = 2;
        String key = "Inbox/1234567";
        String expResult = "1234567";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidRootFmidI() {
        int partIndex = 2;
        String key = "ROOT/I";
        String expResult = "I";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_FidRootFmidInbox() {
        int partIndex = 2;
        String key = "ROOT/Inbox";
        String expResult = "Inbox";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_NoFidFmidWithSep() {
        int partIndex = 2;
        String key = "123-FUN-1234567";
        String expResult = "123";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_NoFidFmidWithoutSep() {
        int partIndex = 2;
        String key = "1234567";
        String expResult = "1234567";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_NoFidFmidI() {
        int partIndex = 2;
        String key = "I";
        String expResult = "I";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_NoFidFmidInbox() {
        int partIndex = 2;
        String key = "Inbox";
        String expResult = "Inbox";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyFMIDPart_NoFidFmidSlash() {
        int partIndex = 2;
        String key = "/";
        String expResult = "";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyUIDVPart_FidIFmidWithSep() {
        int partIndex = 3;
        String key = "I/123-FUN-1234567";
        String expResult = "1234567";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyUIDVPart_FidIFmidWithoutSep() {
        int partIndex = 3;
        String key = "I/1234567";
        String expResult = null;
        String result = Utility.getKeyPart(key, partIndex);
        assertNull(result);
    }
    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyUIDVPart_FidInboxFmidWithSep() {
        int partIndex = 3;
        String key = "Inbox/123-FUN-1234567";
        String expResult = "1234567";
        String result = Utility.getKeyPart(key, partIndex);
        assertEquals(expResult, result);
    }

    /**
     * Test of getKeyPart method, of class Utility.
     */
    public void testGetKeyUIDVPart_FidInboxFmidWithoutSep() {
        int partIndex = 3;
        String key = "Inbox/1234567";
        String expResult = null;
        String result = Utility.getKeyPart(key, partIndex);
        assertNull( result);
    }
    
    public void testUtcToDate() {
        for(int i=0;i< UTC_DATES.length;i++) {
            String utc                  = UTC_DATES[i];
            GregorianCalendar calendar  = CALENDARS[i]  ;
            Date expectedResult = calendar!=null?calendar.getTime():null;
            assertEquals(expectedResult,Utility.UtcToDate(utc));
        }
    }
    
    public void testUtcToLong() {
        for(int i=0;i< UTC_DATES.length;i++) {
            String utc                  = UTC_DATES[i];
            GregorianCalendar calendar  = CALENDARS[i];
            long expectedResult = calendar!=null?calendar.getTimeInMillis():0;
            assertEquals(expectedResult,Utility.UtcToLong(utc));
        }
    }

    public void testUtcToTimestamp() {
        for(int i=0;i< UTC_DATES.length;i++) {
            String utc                  = UTC_DATES[i];
            GregorianCalendar calendar  = CALENDARS[i]  ;
            Timestamp expectedResult = calendar!=null?new Timestamp(calendar.getTimeInMillis()):null;
            assertEquals(expectedResult,Utility.UtcToTimestamp(utc));
        }
    }

    public void testUtcFromDate() {
        for(int i=1;i< UTC_DATES.length;i++) {
            String expected = UTC_DATES[i];
            GregorianCalendar calendar = CALENDARS[i]  ;
            String result = Utility.UtcFromDate(calendar!=null?calendar.getTime():null);
            assertEquals(expected,result);
        }
    }


}
