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

package com.funambol.common.pim.calendar;

import java.util.ArrayList;
import java.util.List;

import junit.framework.*;

import com.funambol.framework.tools.merge.MergeResult;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.PropertyWithTimeZone;

/**
 *
 * @version $Id: CalendarTest.java,v 1.5 2008-07-17 15:53:05 luigiafassina Exp $
 */
public class CalendarTest extends TestCase {

    // ------------------------------------------------------------- Constructor

    public CalendarTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void testMerge_Attendee_1() throws Exception {

        /* Initialization */
        Event eventA = new Event();
        Event eventB = new Event();
        Attendee alpha = new Attendee("Alpha", "MAILTO:alpha@email.dom",
                (short)0, (short)0, (short)0, (short)0);
        Attendee beta = new Attendee("Beta", "MAILTO:beta@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee gamma = new Attendee("Gamma", "MAILTO:gamma@email.dom",
                (short)0, (short)2, (short)0, (short)2);
        Attendee delta = new Attendee("Delta", "MAILTO:delta@email.dom",
                (short)2, (short)4, (short)0, (short)4);
        eventA.addAttendee(alpha);
        eventA.addAttendee(beta);
        eventB.addAttendee(gamma);
        eventB.addAttendee(delta);
        Calendar calendarA = new Calendar(eventA);
        Calendar calendarB = new Calendar(eventB);

        /* Merger */
        MergeResult mergeResult = calendarA.merge(calendarB);
        List<Attendee> attendeeListA = calendarA.getCalendarContent().getAttendees();
        List<Attendee> attendeeListB = calendarB.getCalendarContent().getAttendees();

        /* Expected result of the list merger */
        List<Attendee> attendeeListAB = new ArrayList<Attendee>();
        attendeeListAB.add(alpha);
        attendeeListAB.add(beta);
        attendeeListAB.add(gamma);
        attendeeListAB.add(delta);

        /* Test assertions */
        assertTrue(mergeResult.isSetARequired());
        assertTrue(mergeResult.isSetBRequired());

        /**
         * The following checks can failed since the MergeResults use an HashMap
         * and the order of that is not guaranteed so the test sometimes can fail.
         * It must be reviewed using the maps directly instead of the strings
         */
        /*
        assertEquals("<SetA: true, SetB: true, [ListA: Event/Attendees/Attendees({MAILTO:alpha@email.dom,MAILTO:beta@email.dom}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})], [ListB: Event/Attendees/Attendees({MAILTO:gamma@email.dom,MAILTO:delta@email.dom}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})]>",
                mergeResult.toString());
        */
        assertTrue(attendeeListA.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListA));
        assertTrue(attendeeListB.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListB));
    }

    public void testMerge_Attendee_2() throws Exception {

        /* Initialization */
        Event eventA = new Event();
        Event eventB = new Event();
        Attendee alpha1 = new Attendee("Alpha1", "MAILTO:alpha@email.dom",
                (short)0, (short)0, (short)0, (short)0);
        Attendee alpha2 = new Attendee("Alpha2", "MAILTO:alpha@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee beta = new Attendee("Beta", "MAILTO:beta@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee gamma = new Attendee("Gamma", "MAILTO:gamma@email.dom",
                (short)0, (short)2, (short)0, (short)2);
        Attendee delta = new Attendee("Delta", "MAILTO:delta@email.dom",
                (short)2, (short)4, (short)0, (short)4);
        eventA.addAttendee(alpha1);
        eventA.addAttendee(beta);
        eventB.addAttendee(alpha2);
        eventB.addAttendee(gamma);
        eventB.addAttendee(delta);
        Calendar calendarA = new Calendar(eventA);
        Calendar calendarB = new Calendar(eventB);

        /* Merger */
        MergeResult mergeResult = calendarA.merge(calendarB);
        List<Attendee> attendeeListA = calendarA.getCalendarContent().getAttendees();
        List<Attendee> attendeeListB = calendarB.getCalendarContent().getAttendees();

        /* Expected result of the list merger */
        List<Attendee> attendeeListAB = new ArrayList<Attendee>();
        attendeeListAB.add(alpha1);
        attendeeListAB.add(beta);
        attendeeListAB.add(gamma);
        attendeeListAB.add(delta);

        /* Test assertions */
        assertTrue(mergeResult.isSetARequired());
        assertTrue(mergeResult.isSetBRequired());

        /**
         * The following checks can failed since the MergeResults use an HashMap
         * and the order of that is not guaranteed so the test sometimes can fail.
         * It must be reviewed using the maps directly instead of the strings
         */
         /**
        assertEquals("<SetA: true, SetB: true, [ListA: Event/Attendees/Attendees({MAILTO:alpha@email.dom,MAILTO:beta@email.dom}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})], [ListB: Event/Attendees/Attendees({MAILTO:alpha@email.dom,MAILTO:gamma@email.dom,MAILTO:delta@email.dom}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})]>",
                mergeResult.toString());
          */
        assertTrue(attendeeListA.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListA));
        assertTrue(attendeeListB.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListB));
    }

    public void testMerge_Attendee_3() throws Exception {

        /* Initialization */
        Event eventA = new Event();
        Event eventB = new Event();
        Attendee alpha = new Attendee("Alpha", "MAILTO:alpha@email.dom",
                (short)0, (short)0, (short)0, (short)0);
        Attendee beta = new Attendee("Beta", "MAILTO:beta@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee gamma = new Attendee("Gamma", "MAILTO:gamma@email.dom",
                (short)0, (short)2, (short)0, (short)2);
        Attendee delta = new Attendee("Delta", "MAILTO:delta@email.dom",
                (short)2, (short)4, (short)0, (short)4);
        eventA.addAttendee(alpha);
        eventA.addAttendee(beta);
        eventA.addAttendee(gamma);
        eventA.addAttendee(delta);
        Calendar calendarA = new Calendar(eventA);
        Calendar calendarB = new Calendar(eventB);

        /* Merger */
        MergeResult mergeResult = calendarA.merge(calendarB);
        List<Attendee> attendeeListA = calendarA.getCalendarContent().getAttendees();
        List<Attendee> attendeeListB = calendarB.getCalendarContent().getAttendees();

        /* Expected result of the list merger */
        List<Attendee> attendeeListAB = new ArrayList<Attendee>();
        attendeeListAB.add(alpha);
        attendeeListAB.add(beta);
        attendeeListAB.add(gamma);
        attendeeListAB.add(delta);

        /* Test assertions */
        assertFalse(mergeResult.isSetARequired());
        assertTrue(mergeResult.isSetBRequired());

        /**
         * The following checks can failed since the MergeResults use an HashMap
         * and the order of that is not guaranteed so the test sometimes can fail.
         * It must be reviewed using the maps directly instead of the strings
         */
        /**
        assertEquals("<SetA: false, SetB: true, [ListA: ], [ListB: Event/Attendees/Attendees({}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})]>",
                mergeResult.toString());
         */
        assertTrue(attendeeListA.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListA));
        assertTrue(attendeeListB.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListB));
    }

    public void testMerge_Attendee_4() throws Exception {

        /* Initialization */
        Event eventA = new Event();
        Event eventB = new Event();
        Attendee alpha = new Attendee("Alpha", "MAILTO:alpha@email.dom",
                (short)0, (short)0, (short)0, (short)0);
        Attendee beta = new Attendee("Beta", "MAILTO:beta@email.dom",
                (short)1, (short)1, (short)1, (short)1);
        Attendee gamma = new Attendee("Gamma", "MAILTO:gamma@email.dom",
                (short)0, (short)2, (short)0, (short)2);
        Attendee delta = new Attendee("Delta", "MAILTO:delta@email.dom",
                (short)2, (short)4, (short)0, (short)4);
        eventB.addAttendee(alpha);
        eventB.addAttendee(beta);
        eventB.addAttendee(gamma);
        eventB.addAttendee(delta);
        Calendar calendarA = new Calendar(eventA);
        Calendar calendarB = new Calendar(eventB);

        /* Merger */
        MergeResult mergeResult = calendarA.merge(calendarB);
        List<Attendee> attendeeListA = calendarA.getCalendarContent().getAttendees();
        List<Attendee> attendeeListB = calendarB.getCalendarContent().getAttendees();

        /* Expected result of the list merger */
        List<Attendee> attendeeListAB = new ArrayList<Attendee>();
        attendeeListAB.add(alpha);
        attendeeListAB.add(beta);
        attendeeListAB.add(gamma);
        attendeeListAB.add(delta);

        /* Test assertions */
        assertTrue(mergeResult.isSetARequired());
        assertFalse(mergeResult.isSetBRequired());

        /**
         * The following checks can failed since the MergeResults use an HashMap
         * and the order of that is not guaranteed so the test sometimes can fail.
         * It must be reviewed using the maps directly instead of the strings
         */
        /**
        assertEquals("<SetA: true, SetB: false, [ListA: Event/Attendees/Attendees({}, {MAILTO:delta@email.dom,MAILTO:alpha@email.dom,MAILTO:beta@email.dom,MAILTO:gamma@email.dom})], [ListB: ]>",
                mergeResult.toString());
         */
        assertTrue(attendeeListA.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListA));
        assertTrue(attendeeListB.containsAll(attendeeListAB));
        assertTrue(attendeeListAB.containsAll(attendeeListB));
    }


    public void testMerge_EventDatesWithTz() throws Exception {

        Event eventA = new Event();
        Event eventB = new Event();

        eventA.setDtStart(new PropertyWithTimeZone("20080727T060000Z","Europe/Rome"));
        eventA.setDtEnd  (new PropertyWithTimeZone("20080727T063000Z","Europe/Rome"));
        eventA.setCategories(new Property("APPOINTMENT"));
        eventB.setDtStart(new PropertyWithTimeZone("20080727T060000Z",null));
        eventB.setDtEnd  (new PropertyWithTimeZone("20080727T063000Z",null));
        eventB.setCategories(new Property("MEETING"));

        Calendar calendarA = new Calendar(eventA);
        Calendar calendarB = new Calendar(eventB);

        MergeResult mergeResult = calendarA.merge(calendarB);

        assertFalse(mergeResult.isSetARequired());
        assertTrue (mergeResult.isSetBRequired());

        assertEquals(eventA.getDtStart().getPropertyValueAsString(),
                     eventB.getDtStart().getPropertyValueAsString());
        assertEquals(eventA.getDtStart().getTimeZone(),
                     eventB.getDtStart().getTimeZone());

        assertEquals(eventA.getDtEnd().getPropertyValueAsString(),
                     eventB.getDtEnd().getPropertyValueAsString());
        assertEquals(eventA.getDtEnd().getTimeZone(),
                     eventB.getDtEnd().getTimeZone());

        assertEquals("<SetA: false, SetB: true, [ListA: ], [ListB: Event/Categories/Property/string(APPOINTMENT, MEETING), Event/DtStart/PropertyWithTimeZone/string(Europe/Rome, null), Event/DtEnd/PropertyWithTimeZone/string(Europe/Rome, null)]>",
                     mergeResult.toString());

        eventA = new Event();
        eventB = new Event();
        eventA.setDtStart(new PropertyWithTimeZone("20080727T060000Z",null));
        eventB.setDtStart(new PropertyWithTimeZone("20080727T064000Z",null));

        calendarA = new Calendar(eventA);
        calendarB = new Calendar(eventB);

        mergeResult = calendarA.merge(calendarB);

        assertFalse(mergeResult.isSetARequired());
        assertTrue (mergeResult.isSetBRequired());

        assertEquals("<SetA: false, SetB: true, [ListA: ], [ListB: Event/DtStart/PropertyWithTimeZone/Property/string(20080727T060000Z, 20080727T064000Z)]>", mergeResult.toString());

        eventA = new Event();
        eventB = new Event();
        eventA.setDtStart(new PropertyWithTimeZone("20080727T060000Z","US/Pacific"));
        eventB.setDtStart(new PropertyWithTimeZone("20080727T060000Z","Europe/Berlin"));

        calendarA = new Calendar(eventA);
        calendarB = new Calendar(eventB);

        mergeResult = calendarA.merge(calendarB);

        assertFalse(mergeResult.isSetARequired());
        assertTrue (mergeResult.isSetBRequired());

        assertEquals("<SetA: false, SetB: true, [ListA: ], [ListB: Event/DtStart/PropertyWithTimeZone/string(US/Pacific, Europe/Berlin)]>", mergeResult.toString());

    }

    // ------------------------------------------------------- Protected methods

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }
}
