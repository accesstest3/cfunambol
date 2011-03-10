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

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import com.funambol.framework.tools.IOTools;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.RecurrencePattern;
import com.funambol.common.pim.model.VEvent;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.icalendar.ICalendarParser;
import com.funambol.common.pim.model.VCalendar;
import com.funambol.common.pim.model.VCalendarContent;
import com.funambol.common.pim.sif.SIFCalendarParser;
import java.util.ArrayList;

/**
 * VCalendarContentConverter test cases.
 *
 * @versione $Id: VCalendarContentConverterTest.java,v 1.3 2008-08-20 12:18:16 mauro Exp $
 */
public class VCalendarContentConverterTest extends TestCase {

    // --------------------------------------------------------------- Constants
    public static final String SIFE_WITHOUT_SENSITIVITY =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-without-sensitivity.xml";
    public static final String SIFE_WITHOUT_SENSITIVITY_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-without-sensitivity-expected-vcal.vcs";
    public static final String SIFE_WITHOUT_SENSITIVITY_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-without-sensitivity-result-vcal.vcs";
    public static final String SIFE_WITHOUT_SENSITIVITY_EXPECTED_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-without-sensitivity-expected-ical.ics";
    public static final String SIFE_WITHOUT_SENSITIVITY_RESULT_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-without-sensitivity-result-ical.ics";

    public static final String SIFE_WITH_REMINDER =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-reminder.xml";
    public static final String SIFE_WITH_REMINDER_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-reminder-expected-vcal.vcs";
    public static final String SIFE_WITH_REMINDER_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-with-reminder-result-vcal.vcs";
    public static final String SIFE_WITH_REMINDER_EXPECTED_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-reminder-expected-ical.ics";
    public static final String SIFE_WITH_REMINDER_RESULT_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-with-reminder-result-ical.ics";

    public static final String SIFE_WITH_RECURRENCE =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-recurrence.xml";
    public static final String SIFE_WITH_RECURRENCE_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-recurrence-expected-vcal.vcs";
    public static final String SIFE_WITH_RECURRENCE_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-with-recurrence-result-vcal.vcs";
    public static final String SIFE_WITH_RECURRENCE_EXPECTED_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/sife-with-recurrence-expected-ical.ics";
    public static final String SIFE_WITH_RECURRENCE_RESULT_ICAL =
        "src/test/data/com/funambol/common/pim/converter/sife-to-calendar/result/sife-with-recurrence-result-ical.ics";

    public static final String SIFT_NOTSTARTED =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-notstarted.xml";
    public static final String SIFT_NOTSTARTED_EXPECTED_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-accepted-expected-vcal.vcs";
    public static final String SIFT_NOTSTARTED_RESULT_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-accepted-result-vcal.vcs";
    public static final String SIFT_NOTSTARTED_EXPECTED_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-accepted-expected-ical.ics";
    public static final String SIFT_NOTSTARTED_RESULT_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-accepted-result-ical.ics";

    public static final String SIFT_INPROCESS =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-inprocess.xml";
    public static final String SIFT_INPROCESS_EXPECTED_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-inprocess-expected-vcal.vcs";
    public static final String SIFT_INPROCESS_RESULT_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-inprocess-result-vcal.vcs";
    public static final String SIFT_INPROCESS_EXPECTED_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-inprocess-expected-ical.ics";
    public static final String SIFT_INPROCESS_RESULT_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-inprocess-result-ical.ics";

    public static final String SIFT_COMPLETED =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-completed.xml";
    public static final String SIFT_COMPLETED_EXPECTED_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-completed-expected-vcal.vcs";
    public static final String SIFT_COMPLETED_RESULT_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-completed-result-vcal.vcs";
    public static final String SIFT_COMPLETED_EXPECTED_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-completed-expected-ical.ics";
    public static final String SIFT_COMPLETED_RESULT_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-completed-result-ical.ics";
    public static final String SIFT_WAITING =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-waiting.xml";
    public static final String SIFT_WAITING_EXPECTED_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-needsaction-expected-vcal.vcs";
    public static final String SIFT_WAITING_RESULT_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-needsaction-result-vcal.vcs";
    public static final String SIFT_WAITING_EXPECTED_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-needsaction-expected-ical.ics";
    public static final String SIFT_WAITING_RESULT_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-needsaction-result-ical.ics";
    public static final String SIFT_DEFERRED =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-waiting.xml";
    public static final String SIFT_DEFERRED_EXPECTED_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-needsaction-expected-vcal.vcs";
    public static final String SIFT_DEFERRED_RESULT_VCAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-needsaction-result-vcal.vcs";
    public static final String SIFT_DEFERRED_EXPECTED_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/sift-needsaction-expected-ical.ics";
    public static final String SIFT_DEFERRED_RESULT_ICAL =
            "src/test/data/com/funambol/common/pim/converter/sift-to-calendar/result/sift-needsaction-result-ical.ics";


    public static final String ALLDAY_EVENT_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-18-input.ics";
    public static final String ALLDAY_EVENT_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-18-output.vcs";
    public static final String ALLDAY_EVENT_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-18-result-vcal.vcs";


    public static final String ALLDAY_EVENT_WITH_DURATION_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-18-input.ics";
    public static final String ALLDAY_EVENT_WITH_DURATION_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-18-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DURATION_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-18-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DURATION_2DAYS_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-19-input.ics";
    public static final String ALLDAY_EVENT_WITH_DURATION_2DAYS_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-19-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DURATION_2DAYS_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-19-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-20-input.ics";
    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-20-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-20-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_02_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-24-input.ics";
    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_02_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-24-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DURATION_3DAYS_02_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-24-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DTEND_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-21-input.ics";
    public static final String ALLDAY_EVENT_WITH_DTEND_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-21-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DTEND_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-21-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DTEND_2DAYS_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-22-input.ics";
    public static final String ALLDAY_EVENT_WITH_DTEND_2DAYS_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-22-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DTEND_2DAYS_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-22-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-23-input.ics";
    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-23-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-23-result-vcal.vcs";

    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_02_ICAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/icalendar-25-input.ics";
    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_02_EXPECTED_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/vcalendar-25-output.vcs";
    public static final String ALLDAY_EVENT_WITH_DTEND_3DAYS_02_RESULT_VCAL =
        "src/test/data/com/funambol/common/pim/converter/vcalendar-converter/result/vcalendar-25-result-vcal.vcs";

    // ------------------------------------------------------------ Constructors
    public VCalendarContentConverterTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        // 01 Jan 2008 @ 00:00:00 UTC
        TimeZoneHelper.setReferenceTime(1199145600000L);
    }

    @Override
    protected void tearDown() throws Exception {
        // Resets the reference time
        TimeZoneHelper.setReferenceTime(-1);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of extractInterval method.
     */
    public void testExtractInterval_FromCCWith100Occurrences() {
        
        CalendarContent cc = new Event();
        cc.setDtStart(new PropertyWithTimeZone("20071129T163305", "Europe/Rome"));
        cc.setDtEnd  (new PropertyWithTimeZone("20071129T235010", "Europe/Rome"));
        cc.setRecurrencePattern(
                new RecurrencePattern(RecurrencePattern.TYPE_WEEKLY         ,
                                      3                                     ,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      RecurrencePattern.DAY_OF_WEEK_THURSDAY,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      "20071129T163305"                     ,
                                      null                                  ,
                                      true                                  ,
                                      100                                   )
                );
        
        VCalendarContentConverter vccc = 
                new VCalendarContentConverter(null, "UTF-8", false);
        long[] interval = cc.extractInterval();
        
        assertEquals(1167523200000L, // 31 Dec 2006 @ 00:00:00 UTC
                interval[0]);
        assertEquals(1377734400000L, // 29 Aug 2013 @ 00:00:00 UTC
                interval[1]);
    }
    
    /**
     * Test of extractInterval method.
     */
    public void testExtractInterval_FromVCCWith100Occurrences() {
        
        VCalendarContent vcc = new VEvent();
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("DTSTART", "20071129T163305"));
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("DTEND"  , "20071129T235010"));        
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("RRULE"  , "FREQ=WEEKLY;INTERVAL=3;COUNT=100"));        
        
        VCalendarContentConverter vccc = 
                new VCalendarContentConverter(null, "UTF-8", false);
        long[] interval = vccc.extractInterval(vcc);
        
        assertEquals(1167523200000L, // 31 Dec 2006 @ 00:00:00 UTC
                interval[0]);
        assertEquals(1377734400000L, // 29 Aug 2013 @ 00:00:00 UTC
                interval[1]);
    }        
    
    /**
     * Test of extractInterval method.
     */
    public void testExtractInterval_FromCCWithEndDate() {
        
        CalendarContent cc = new Event();
        cc.setDtStart(new PropertyWithTimeZone("20071029T163305", "Europe/Rome"));
        cc.setDtEnd  (new PropertyWithTimeZone("20071029T235010", "Europe/Rome"));
        cc.setRecurrencePattern(
                new RecurrencePattern(RecurrencePattern.TYPE_MONTHLY        ,
                                      2                                     ,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      (short) 29                            ,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      RecurrencePattern.UNSPECIFIED         ,
                                      "20071029T163305"                     ,
                                      "20090815T163305"                     ,
                                      false                                 )
                );
        
        VCalendarContentConverter vccc = 
                new VCalendarContentConverter(null, "UTF-8", false);
        long[] interval = cc.extractInterval();
        
        assertEquals(1167523200000L, // 31 Dec 2006 @ 00:00:00 UTC
                interval[0]);
        assertEquals(1262390400000L, // 02 Jan 2010 @ 00:00:00 UTC
                interval[1]);
    }
    
   /**
    * Test of extractInterval method.
    */
    public void testExtractInterval_FromVCCWithEndDate() {
        
        VCalendarContent vcc = new VEvent();
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("DTSTART", "20071029T163305"));
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("DTEND"  , "20071029T235010"));        
        vcc.setProperty(
                new com.funambol.common.pim.model.Property("RRULE"  , "FREQ=MONTHLY;INTERVAL=2;BYDAY=29;UNTIL=20090815T163305"));        
        
        VCalendarContentConverter vccc = 
                new VCalendarContentConverter(null, "UTF-8", false);
        long[] interval = vccc.extractInterval(vcc);
        
        assertEquals(1167523200000L, // 31 Dec 2006 @ 00:00:00 UTC
                interval[0]);
        assertEquals(1262390400000L, // 02 Jan 2010 @ 00:00:00 UTC
                interval[1]);
    }    

    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifEWithoutSensitivityToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITHOUT_SENSITIVITY);
        String expectedVCal =
            IOTools.readFileString(SIFE_WITHOUT_SENSITIVITY_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFE_WITHOUT_SENSITIVITY_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifEWithReminderToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITH_REMINDER);
        String expectedVCal =
            IOTools.readFileString(SIFE_WITH_REMINDER_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFE_WITH_REMINDER_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifEWithRecurrenceToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITH_RECURRENCE);
        String expectedVCal =
            IOTools.readFileString(SIFE_WITH_RECURRENCE_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFE_WITH_RECURRENCE_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }
/**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifTInProgressToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_INPROCESS);
        String expectedVCal =
            IOTools.readFileString(SIFT_INPROCESS_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFT_INPROCESS_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifTNotStartedToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_NOTSTARTED);
        String expectedVCal =
            IOTools.readFileString(SIFT_NOTSTARTED_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFT_NOTSTARTED_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifTCompletedToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_COMPLETED);
        String expectedVCal =
            IOTools.readFileString(SIFT_COMPLETED_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFT_COMPLETED_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }
/**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifTWaitingToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_WAITING);
        String expectedVCal =
            IOTools.readFileString(SIFT_WAITING_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFT_WAITING_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }
    /**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/x-vcalendar 1.0)
     */
    public void testCc2vcc_SifTDeferredToVCal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_DEFERRED);
        String expectedVCal =
            IOTools.readFileString(SIFT_DEFERRED_EXPECTED_VCAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), true);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultVCal = writer.toString(vcalContent);
        IOTools.writeFile(resultVCal, SIFT_DEFERRED_RESULT_VCAL);

        assertEquals(expectedVCal.replaceAll("\\r", ""),
                     resultVCal.replaceAll("\\r", ""));
    }
    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/calendar 2.0)
     */
    public void testCc2vcc_SifEWithoutSensitivityToICal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITHOUT_SENSITIVITY);
        String expectedICal =
            IOTools.readFileString(SIFE_WITHOUT_SENSITIVITY_EXPECTED_ICAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), false);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultICal = writer.toString(vcalContent);
        IOTools.writeFile(resultICal, SIFE_WITHOUT_SENSITIVITY_RESULT_ICAL);

        assertEquals(expectedICal.replaceAll("\\r", ""),
                     resultICal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/calendar 2.0)
     */
    public void testCc2vcc_SifEWithReminderToICal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITH_REMINDER);
        String expectedICal =
            IOTools.readFileString(SIFE_WITH_REMINDER_EXPECTED_ICAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), false);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultICal = writer.toString(vcalContent);
        IOTools.writeFile(resultICal, SIFE_WITH_REMINDER_RESULT_ICAL);

        assertEquals(expectedICal.replaceAll("\\r", ""),
                     resultICal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-E --> Calendat object --> VCalendarContent (text/calendar 2.0)
     */
    public void testCc2vcc_SifEWithRecurrenceToICal() throws Exception {
        String sifcal = IOTools.readFileString(SIFE_WITH_RECURRENCE);
        String expectedICal =
            IOTools.readFileString(SIFE_WITH_RECURRENCE_EXPECTED_ICAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), false);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultICal = writer.toString(vcalContent);
        IOTools.writeFile(resultICal, SIFE_WITH_RECURRENCE_RESULT_ICAL);

        assertEquals(expectedICal.replaceAll("\\r", ""),
                     resultICal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/calendar 2.0)
     */
    public void testCc2vcc_SifTInProgressToICal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_INPROCESS);
        String expectedICal =
            IOTools.readFileString(SIFT_INPROCESS_EXPECTED_ICAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), false);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultICal = writer.toString(vcalContent);
        IOTools.writeFile(resultICal, SIFT_INPROCESS_RESULT_ICAL);

        assertEquals(expectedICal.replaceAll("\\r", ""),
                     resultICal.replaceAll("\\r", ""));
    }

    /**
     * Test of cc2vcc method.
     * SIF-T --> Calendat object --> VCalendarContent (text/calendar 2.0)
     */
    public void testCc2vcc_SifTCompletedToICal() throws Exception {
        String sifcal = IOTools.readFileString(SIFT_COMPLETED);
        String expectedICal =
            IOTools.readFileString(SIFT_COMPLETED_EXPECTED_ICAL);

        ByteArrayInputStream buffer =
            new ByteArrayInputStream(sifcal.getBytes());
        SIFCalendarParser parser = new SIFCalendarParser(buffer);
        Calendar cal = parser.parse();

        VCalendarContent vcalContent = null;
        VCalendarContentConverter vccc =
            new VCalendarContentConverter(null, "UTF-8", false);

        vcalContent = vccc.cc2vcc(cal.getCalendarContent(), false);

        VComponentWriter writer =
                new VComponentWriter(VComponentWriter.NO_FOLDING);
        String resultICal = writer.toString(vcalContent);
        IOTools.writeFile(resultICal, SIFT_COMPLETED_RESULT_ICAL);

        assertEquals(expectedICal.replaceAll("\\r", ""),
                     resultICal.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDay() throws Exception {
        String iCalendar = IOTools.readFileString(ALLDAY_EVENT_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal= new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
                "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);
       
        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

       IOTools.writeFile(vCalResult, ALLDAY_EVENT_RESULT_VCAL);

       String expectedVCal = IOTools.readFileString(ALLDAY_EVENT_EXPECTED_VCAL);
       assertEquals(expectedVCal.replaceAll("\\r", ""),
                    vCalResult.replaceAll("\\r", ""));

   }


      public void test_ICalendarToVCalendarAllDayWithDuration() throws Exception {
        String iCalendar = IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DURATION_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDuration2Days() throws Exception {
        String iCalendar = IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_2DAYS_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DURATION_2DAYS_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_2DAYS_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDuration3Days() throws Exception {
        String iCalendar =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_3DAYS_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DURATION_3DAYS_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_3DAYS_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDuration3Days_02() throws Exception {
        // The duration of this event falls long from when there is the change
        // of hour (timezone Europe/Rome)
        String iCalendar =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_3DAYS_02_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DURATION_3DAYS_02_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DURATION_3DAYS_02_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDtEnd() throws Exception {
        String iCalendar = IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DTEND_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDtEnd2Days() throws Exception {
        String iCalendar =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_2DAYS_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DTEND_2DAYS_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_2DAYS_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDtEnd3Days() throws Exception {
        String iCalendar =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_3DAYS_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DTEND_3DAYS_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_3DAYS_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

    public void test_ICalendarToVCalendarAllDayWithDtEnd3Days_02() throws Exception {
        String iCalendar =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_3DAYS_02_ICAL);

        // iCalendar (text item) to VCalendar (half-parsed Java object):
        ByteArrayInputStream buffer = new ByteArrayInputStream(iCalendar.getBytes());
        ICalendarParser parser = new ICalendarParser(buffer);
        VCalendar vcalendar = parser.ICalendar();

        // VCalendar (transitional Java object) to Calendar (model Java object):
        VCalendarContentConverter vcc =
            new VCalendarContentConverter(null, "UTF-8", false);
        CalendarContent calContent =
            vcc.vcc2cc(vcalendar.getVCalendarContent(), false); // text/calendar

        // Calendar (model Java object) to VCalendar (transitional Java object):
        VCalendarContent vcalContent =
            vcc.cc2vcc(calContent, true); // text/x-vcalendar

        // VCalendar (transitional Java object) to vCalendar 1.0 (text item):
        VCalendar vcal = new VCalendar();
        vcal.setProperty(new com.funambol.common.pim.model.Property(
            "VERSION", false, new ArrayList(), "1.0"));
        vcal.addComponent(vcalContent);

        VComponentWriter writer =
            new VComponentWriter(VComponentWriter.NO_FOLDING);
        String vCalResult = writer.toString(vcal);

        IOTools.writeFile(vCalResult, ALLDAY_EVENT_WITH_DTEND_3DAYS_02_RESULT_VCAL);

        String expectedVCal =
            IOTools.readFileString(ALLDAY_EVENT_WITH_DTEND_3DAYS_02_EXPECTED_VCAL);
        assertEquals(expectedVCal.replaceAll("\\r", ""),
            vCalResult.replaceAll("\\r", ""));
    }

}
