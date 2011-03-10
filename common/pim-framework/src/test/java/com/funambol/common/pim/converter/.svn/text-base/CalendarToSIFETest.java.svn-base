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
package com.funambol.common.pim.converter;

import java.util.TimeZone;

import junit.framework.*;
import junitx.util.PrivateAccessor;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.calendar.*;

import com.funambol.common.pim.utility.TimeUtils;
import com.funambol.framework.tools.IOTools;


/**
 * CalendarToSIFE test cases
 *
 * @version $Id: CalendarToSIFETest.java,v 1.5 2008-07-22 14:49:24 nichele Exp $
 */
public class CalendarToSIFETest extends TestCase {

    CalendarToSIFE converterRome, converterUTC;

    // ------------------------------------------------------------ Constructors
    public CalendarToSIFETest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        TimeZone europeRome = TimeZone.getTimeZone("Europe/Rome");
        converterRome = new CalendarToSIFE(europeRome, "UTF-8");
        converterUTC = new CalendarToSIFE(null, "UTF-8");
        // Sets noon, January 1st, 2009 as reference time, thus making the tests
        // time-independent
        TimeZoneHelper.setReferenceTime(1230811200000L);
    }

    @Override
    protected void tearDown() throws Exception {
        // Restores current time as reference time
        TimeZoneHelper.setReferenceTime(-1);
    }

    //--------------------------------------------------------------- Test cases
    public void testConvert() throws Exception {
        Calendar c = new Calendar(new Event());
        Attendee alpha = new Attendee("Alpha", "MAILTO:alpha@email.dom",
                (short)0, (short)0, (short)0, (short)0);
        Attendee beta = new Attendee("Beta", "MAILTO:beta@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee gamma = new Attendee("Gamma", "MAILTO:gamma@email.dom",
                (short)0, (short)2, (short)0, (short)2);
        Attendee delta = new Attendee("Delta", "MAILTO:delta@email.dom",
                (short)2, (short)4, (short)0, (short)4);
        c.getCalendarContent().setDtStart(new Property("20070101T120000Z"));
        c.getCalendarContent().setDtEnd(new Property("20070101T130000Z"));
        c.getCalendarContent().setAllDay(false);
        c.getCalendarContent().addAttendee(alpha);
        c.getCalendarContent().addAttendee(beta);
        c.getCalendarContent().addAttendee(gamma);
        c.getCalendarContent().addAttendee(delta);

        String sifeRome = converterRome.convert(c);
        IOTools.writeFile(sifeRome,
                "src/test/data/com/funambol/common/pim/converter/calendar-to-sife/result/sife-1-rome-output.xml");
        String expectedSifeRome =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/calendar-to-sife/sife-1-rome.xml");

        assertEquals(expectedSifeRome, sifeRome);

        String sifeUTC = converterUTC.convert(c);
        IOTools.writeFile(sifeUTC,
                "src/test/data/com/funambol/common/pim/converter/calendar-to-sife/result/sife-1-utc-output.xml");
        String expectedSifeUTC =
                IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/calendar-to-sife/sife-1-utc.xml");

        assertEquals(expectedSifeUTC, sifeUTC);
    }

    public void testShiftDayOfWeekMask_shiftleft_Asia_Tokyo() throws Throwable {
        short mask = 0;
        short newMask = 0;
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        
        String utcPatternStartDate = null;
        String localPatternStartDate = null;
        
        utcPatternStartDate = "20080718T160000Z";
        
        localPatternStartDate = TimeUtils.convertUTCDateToLocal(utcPatternStartDate, tz);
        assertEquals("20080719T010000", localPatternStartDate); // just to check
        
        mask = 1;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(2, newMask); 
        
        mask = 2;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(4, newMask); 
        
        mask = 4;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(8, newMask);   
        
        mask = 16;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(32, newMask);           
        
        mask = 32;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(64, newMask);
        
        mask = 64;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(1, newMask);      
        
        mask = 127;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(127, newMask);        
        
        mask = 81;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(35, newMask);           
    }

    public void testShiftDayOfWeekMask_no_shiftleft_Asia_Tokyo() throws Throwable {
        short mask = 0;
        short newMask = 0;
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        
        String utcPatternStartDate = null;
        String localPatternStartDate = null;
        
        utcPatternStartDate = "20080718T010000Z";
        
        localPatternStartDate = TimeUtils.convertUTCDateToLocal(utcPatternStartDate, tz);
        assertEquals("20080718T100000", localPatternStartDate); // just to check
        
        mask = 1;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(1, newMask); 
        
        mask = 2;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(2, newMask); 
        
        //
        // we don't need to check all the cases since there is not shifting
        // 
    }

    public void testShiftDayOfWeekMask_shift_right_US_Pacific() throws Throwable {
        short mask = 0;
        short newMask = 0;
        TimeZone tz = TimeZone.getTimeZone("US/Pacific");
        
        String utcPatternStartDate = null;
        String localPatternStartDate = null;
        
        utcPatternStartDate = "20080719T010000Z";
        
        localPatternStartDate = TimeUtils.convertUTCDateToLocal(utcPatternStartDate, tz);
        assertEquals("20080718T180000", localPatternStartDate); // just to check
        
        mask = 1;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(64, newMask); 
        
        mask = 64;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(32, newMask); 
        
        mask = 32;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(16, newMask);   
        
        mask = 16;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(8, newMask);           
        
        mask = 8;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(4, newMask);   
        
        mask = 4;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(2, newMask);
        
        mask = 2;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(1, newMask);      
        
        mask = 127;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(127, newMask);        
        
        mask = 81;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(104, newMask);           
    }

    public void testShiftDayOfWeekMask_no_shift_right_US_Pacific() throws Throwable {
        short mask = 0;
        short newMask = 0;
        TimeZone tz = TimeZone.getTimeZone("US/Pacific");
        
        String utcPatternStartDate = null;
        String localPatternStartDate = null;
        
        utcPatternStartDate = "20080719T160000Z";
        
        localPatternStartDate = TimeUtils.convertUTCDateToLocal(utcPatternStartDate, tz);
        assertEquals("20080719T090000", localPatternStartDate); // just to check
        
        mask = 1;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(1, newMask); 
        
        mask = 64;
        newMask = (Short)PrivateAccessor.invoke(CalendarToSIFE.class, 
                               "shiftDayOfWeekMask", 
                               new Class[] {String.class, String.class, short.class},
                               new Object[] {utcPatternStartDate, localPatternStartDate, mask});
        assertEquals(64, newMask); 
        
        //
        // we don't need to check all the cases since there is not shifting
        //         
    }

    /* @todo

    public void testCreateTagsFromRecurrencePattern() throws Exception {

    }

    public void testCreateTagsFromAttendeeList() throws Exception {

    }

    public void testCreateTagFromAllDayProperty() {

    }

    public void testCreateTagFromProperty() throws Exception {

    }

    public void testCreateTagFromString() {

    }

    public void testCreateTagFromStringAndProperty() {

    }

    public void testCreateTagFromObject() {

    }

    public void testBooleanToSIFValue() {

    }

    public void testCreateTagsFromExceptionList() throws Exception {

    }

    public void testImportance19To02() {

    }

    */

}
