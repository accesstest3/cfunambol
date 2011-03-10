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

package com.funambol.common.pim.sif;

import junit.framework.*;
import junitx.util.PrivateAccessor;
import java.io.*;
import com.funambol.common.pim.calendar.*;
import com.funambol.framework.tools.IOTools;
import java.util.List;

/**
 * SIFCalendarParser test cases
 * @version $Id: SIFCalendarParserTest.java,v 1.6 2008-04-17 17:17:31 mauro Exp $
 */
public class SIFCalendarParserTest extends TestCase {


    // ------------------------------------------------------------- Constructor
    public SIFCalendarParserTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Tests the attendees.
     */
    public void testParse_1() throws Exception {
        String xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sife-1.xml");
        SIFCalendarParser parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        Calendar c = parser.parse();

        Attendee[] attendeeArray = { null, null, null, null };
        List<Attendee> attendeeList = c.getCalendarContent().getAttendees();
        assertEquals(4, attendeeList.size());
        for (Attendee attendee : attendeeList) {
            if (attendee.getName().equals("OneOne")) {
                attendeeArray[0] = attendee;
            }
            if (attendee.getName().equals("TwoTwo")) {
                attendeeArray[1] = attendee;
            }
            if (attendee.getName().equals("ThreeThree")) {
                attendeeArray[2] = attendee;
            }
            if (attendee.getName().equals("FourOne")) {
                attendeeArray[3] = attendee;
            }
        }
        assertEquals("OneOne", attendeeArray[0].getName());
        assertEquals("name@email.dom", attendeeArray[0].getEmail());
        assertEquals("MAILTO:name@email.dom", attendeeArray[0].getUri());
        assertEquals(Attendee.ATTENDEE, attendeeArray[0].getRole());
        assertEquals(Attendee.REQUIRED, attendeeArray[0].getExpected());
        assertEquals(Attendee.INDIVIDUAL, attendeeArray[0].getKind());
        assertEquals(Attendee.ACCEPTED, attendeeArray[0].getStatus());

        assertEquals("TwoTwo", attendeeArray[1].getName());
        assertEquals("name@email.dom", attendeeArray[1].getEmail());
        assertEquals("MAILTO:name@email.dom", attendeeArray[1].getUri());
        assertEquals(Attendee.ATTENDEE, attendeeArray[1].getRole());
        assertEquals(Attendee.OPTIONAL, attendeeArray[1].getExpected());
        assertEquals(Attendee.INDIVIDUAL, attendeeArray[1].getKind());
        assertEquals(Attendee.DECLINED, attendeeArray[1].getStatus());

        assertEquals("ThreeThree", attendeeArray[2].getName());
        assertEquals("name@email.dom", attendeeArray[2].getEmail());
        assertEquals("MAILTO:name@email.dom", attendeeArray[2].getUri());
        assertEquals(Attendee.ATTENDEE, attendeeArray[2].getRole());
        assertEquals(Attendee.REQUIRED, attendeeArray[2].getExpected());
        assertEquals(Attendee.RESOURCE, attendeeArray[2].getKind());
        assertEquals(Attendee.TENTATIVE, attendeeArray[2].getStatus());

        assertEquals("FourOne", attendeeArray[3].getName());
        assertEquals("name@email.dom", attendeeArray[3].getEmail());
        assertEquals("MAILTO:name@email.dom", attendeeArray[3].getUri());
        assertEquals(Attendee.ATTENDEE, attendeeArray[3].getRole());
        assertEquals(Attendee.REQUIRED, attendeeArray[3].getExpected());
        assertEquals(Attendee.INDIVIDUAL, attendeeArray[3].getKind());
        assertEquals(Attendee.NEEDS_ACTION, attendeeArray[3].getStatus());
    }
    
    /**
     * Tests the attendees in which attendee_type is empty value instead of integer
     */
    public void testParse_2() throws Exception {

        try {
            String xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/bb722.xml");

            SIFCalendarParser parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));

            Calendar c = parser.parse();

            List<Attendee> attendeeList = c.getCalendarContent().getAttendees();

            assertEquals(1, attendeeList.size());

            Attendee[] attendeeArray = new Attendee[1];

            attendeeArray[0] = attendeeList.get(0);

            assertEquals(""                     , attendeeArray[0].getName());
            assertEquals("mail@mail.com"        , attendeeArray[0].getEmail());
            assertEquals("MAILTO:mail@mail.com" , attendeeArray[0].getUri());
            assertEquals(Attendee.ATTENDEE      , attendeeArray[0].getRole());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getExpected());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getKind());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getStatus());

        } catch (Exception e){
            fail("Error in the parser for the Attendees! '" + e.toString() + "'");
        }

    }  
    
    /**
     * Tests the attendees in which attendee_type, attendee_status have a String value and not integer value
     */
    public void testParse_attendee_string() throws Exception {

        try {
            String xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/bb_attendee1.xml");

            SIFCalendarParser parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));

            Calendar c = parser.parse();

            List<Attendee> attendeeList = c.getCalendarContent().getAttendees();

            assertEquals(1, attendeeList.size());

            Attendee[] attendeeArray = new Attendee[1];

            attendeeArray[0] = attendeeList.get(0);

            assertEquals(""                     , attendeeArray[0].getName());
            assertEquals("mail@mail.com"        , attendeeArray[0].getEmail());
            assertEquals("MAILTO:mail@mail.com" , attendeeArray[0].getUri());
            assertEquals(Attendee.ATTENDEE      , attendeeArray[0].getRole());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getExpected());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getKind());
            assertEquals(Attendee.UNKNOWN       , attendeeArray[0].getStatus());

        } catch (Exception e){
            fail("Error in the parser for the Attendees! '" + e.toString() + "'");
        }

    }     
    
    
    /**
     * Tests the time zone.
     */
    public void testParse_3() throws Exception {
        String xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sife-2.xml");
        SIFCalendarParser parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        Calendar c = parser.parse();
        
        assertEquals("Europe/Berlin", c.getEvent().getDtStart().getTimeZone());
        assertEquals("Europe/Berlin", c.getEvent().getDtEnd().getTimeZone());
    }

    public void testParseStatus() throws Exception {
        // task accepted
        String xml =
            readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sift-accepted.xml");
        SIFCalendarParser parser =
            new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        Calendar c = parser.parse();
        assertEquals("0", c.getTask().getStatus().getPropertyValueAsString());

        // task in-process
        xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sift-inprocess.xml");
        parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        c = parser.parse();
        assertEquals("4", c.getTask().getStatus().getPropertyValueAsString());

        // task completed
        xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sift-completed.xml");
        parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        c = parser.parse();
        assertEquals("5", c.getTask().getStatus().getPropertyValueAsString());

        // task needs action
        xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sift-needsaction.xml");
        parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        c = parser.parse();
        assertEquals("6", c.getTask().getStatus().getPropertyValueAsString());

        // task declined
        xml = readFile("src/test/data/com/funambol/common/pim/sif/sifcalendar-parser/sift-declined.xml");
        parser = new SIFCalendarParser(new ByteArrayInputStream(xml.getBytes()));
        c = parser.parse();
        assertEquals("8", c.getTask().getStatus().getPropertyValueAsString());
    }

    // ------------------------------------------------------- Protected methods

    @Override
    protected void setUp() throws Exception {
    }
    @Override
    protected void tearDown() throws Exception {
    }


    // --------------------------------------------------------- Private methods

    private String readFile(String file) throws IOException {
        return IOTools.readFileString(file);
    }
}
