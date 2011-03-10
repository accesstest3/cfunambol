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
package com.funambol.common.pim.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTimeZone;

import junit.framework.TestCase;

import com.funambol.common.pim.model.Property;
import com.funambol.common.pim.model.TzDaylightComponent;
import com.funambol.common.pim.model.TzStandardComponent;
import com.funambol.common.pim.model.VComponent;
import com.funambol.common.pim.model.VTimezone;

/**
 * TimeZoneHelper test cases
 * @version $Id: TimeZoneHelperTest.java,v 1.4 2008-08-20 12:31:28 mauro Exp $
 */
public class TimeZoneHelperTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public TimeZoneHelperTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    //--------------------------------------------------------------- Test cases
    public void testTimeZoneHelperFromXVCalendarRome() throws Exception {
        Property tz = new Property("TZ", "+01");
        List<Property> dayLightList = new ArrayList<Property>();
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;+02;20070325T010000Z;20071028T010000Z;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;+02;20080330T010000Z;20081026T010000Z;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;+02;20090329T010000Z;20091025T010000Z;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;+02;20100328T010000Z;20101031T010000Z;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;+02;20110327T010000Z;20111030T010000Z;;"));
        TimeZoneHelper vctz = new TimeZoneHelper(tz, dayLightList);
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals(10, transitions.size());
        assertEquals(3600000, vctz.getBasicOffset());
        for (TimeZoneTransition transition : transitions) {
            assertEquals("", transition.getName());
        }
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());
        assertEquals(7200000, transitions.get(2).getOffset());
        assertEquals(1206838800000L, transitions.get(2).getTime());
        assertEquals(3600000, transitions.get(3).getOffset());
        assertEquals(1224982800000L, transitions.get(3).getTime());
        assertEquals(7200000, transitions.get(4).getOffset());
        assertEquals(1238288400000L, transitions.get(4).getTime());
        assertEquals(3600000, transitions.get(5).getOffset());
        assertEquals(1256432400000L, transitions.get(5).getTime());
        assertEquals(7200000, transitions.get(6).getOffset());
        assertEquals(1269738000000L, transitions.get(6).getTime());
        assertEquals(3600000, transitions.get(7).getOffset());
        assertEquals(1288486800000L, transitions.get(7).getTime());
        assertEquals(7200000, transitions.get(8).getOffset());
        assertEquals(1301187600000L, transitions.get(8).getTime());
        assertEquals(3600000, transitions.get(9).getOffset());
        assertEquals(1319936400000L, transitions.get(9).getTime());

        assertEquals("Europe/Berlin", vctz.toID()); // First run
        assertEquals("Europe/Berlin", vctz.toID()); // Second run (cached)
    }

    public void testTimeZoneHelperFromXVCalendarBoston() throws Exception {
        Property tz = new Property("TZ", "-05");
        List<Property> dayLightList = new ArrayList<Property>();
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;-04;20070311T020000;20071104T020000;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;-04;20080309T020000;20081102T020000;;"));
        dayLightList.add(new Property("DAYLIGHT",
                "TRUE;-04;20090308T020000;20091101T020000;;"));
        TimeZoneHelper vctz = new TimeZoneHelper(tz, dayLightList);
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals(6, transitions.size());
        assertEquals(-18000000, vctz.getBasicOffset());
        for (TimeZoneTransition transition : transitions) {
            assertEquals("", transition.getName());
        }
        assertEquals(-14400000, transitions.get(0).getOffset());
        assertEquals(1173596400000L, transitions.get(0).getTime());
        assertEquals(-18000000, transitions.get(1).getOffset());
        assertEquals(1194159600000L, transitions.get(1).getTime());
        assertEquals(-14400000, transitions.get(2).getOffset());
        assertEquals(1205046000000L, transitions.get(2).getTime());
        assertEquals(-18000000, transitions.get(3).getOffset());
        assertEquals(1225609200000L, transitions.get(3).getTime());
        assertEquals(-14400000, transitions.get(4).getOffset());
        assertEquals(1236495600000L, transitions.get(4).getTime());
        assertEquals(-18000000, transitions.get(5).getOffset());
        assertEquals(1257058800000L, transitions.get(5).getTime());

        assertEquals("America/New_York", vctz.toID()); // First run
        assertEquals("America/New_York", vctz.toID()); // Second run (cached)
    }

    public void testTimeZoneHelperFromICalendarBerlin() throws Exception {

        VTimezone vtz = new VTimezone();
        vtz.addProperty("TZID", "Berlino"); // No hint
        TzStandardComponent standardC = new TzStandardComponent();
        standardC.addProperty("TZOFFSETFROM", "+0200");
        standardC.addProperty("TZOFFSETTO", "+0100");
        standardC.addProperty("TZNAME", "CET");
        standardC.addProperty("DTSTART", "19701025T030000");
        standardC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10");
        TzDaylightComponent dayLightC = new TzDaylightComponent();
        dayLightC.addProperty("TZOFFSETFROM", "+0100");
        dayLightC.addProperty("TZOFFSETTO", "+0200");
        dayLightC.addProperty("TZNAME", "CEST");
        dayLightC.addProperty("DTSTART", "19700329T020000");
        dayLightC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3");
        vtz.addStandardc(standardC);
        vtz.addDaylightc(dayLightC);

        TimeZoneHelper vctz = new TimeZoneHelper(vtz,
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("Berlino", vctz.getName());
        assertEquals(10, transitions.size());
        assertEquals(3600000, vctz.getBasicOffset());
        boolean dst = true;
        for (TimeZoneTransition transition : transitions) {
            if (dst) {
                assertEquals("CEST", transition.getName());
            } else {
                assertEquals("CET", transition.getName());
            }
            dst = !dst;
        }
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());
        assertEquals(7200000, transitions.get(2).getOffset());
        assertEquals(1206838800000L, transitions.get(2).getTime());
        assertEquals(3600000, transitions.get(3).getOffset());
        assertEquals(1224982800000L, transitions.get(3).getTime());
        assertEquals(7200000, transitions.get(4).getOffset());
        assertEquals(1238288400000L, transitions.get(4).getTime());
        assertEquals(3600000, transitions.get(5).getOffset());
        assertEquals(1256432400000L, transitions.get(5).getTime());
        assertEquals(7200000, transitions.get(6).getOffset());
        assertEquals(1269738000000L, transitions.get(6).getTime());
        assertEquals(3600000, transitions.get(7).getOffset());
        assertEquals(1288486800000L, transitions.get(7).getTime());
        assertEquals(7200000, transitions.get(8).getOffset());
        assertEquals(1301187600000L, transitions.get(8).getTime());
        assertEquals(3600000, transitions.get(9).getOffset());
        assertEquals(1319936400000L, transitions.get(9).getTime());

        assertEquals("Europe/Berlin", vctz.toID()); // First run
        assertEquals("Europe/Berlin", vctz.toID()); // Second run (cached)
    }

    public void testTimeZoneHelperFromICalendarBerlinUntil() throws Exception {

        VTimezone vtz = new VTimezone();
        vtz.addProperty("TZID", "Berlino"); // No hint
        TzStandardComponent standardC = new TzStandardComponent();
        standardC.addProperty("TZOFFSETFROM", "+0200");
        standardC.addProperty("TZOFFSETTO", "+0100");
        standardC.addProperty("TZNAME", "CET");
        standardC.addProperty("DTSTART", "19701025T030000");
        standardC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10;UNTIL=20091231T000113Z");
        TzDaylightComponent dayLightC = new TzDaylightComponent();
        dayLightC.addProperty("TZOFFSETFROM", "+0100");
        dayLightC.addProperty("TZOFFSETTO", "+0200");
        dayLightC.addProperty("TZNAME", "CEST");
        dayLightC.addProperty("DTSTART", "19700329T020000");
        dayLightC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3;UNTIL=20091231T000113Z");
        vtz.addStandardc(standardC);
        vtz.addDaylightc(dayLightC);

        TimeZoneHelper vctz = new TimeZoneHelper(vtz,
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("Berlino", vctz.getName());
        assertEquals(6, transitions.size());
        assertEquals(3600000, vctz.getBasicOffset());
        boolean dst = true;
        for (TimeZoneTransition transition : transitions) {
            if (dst) {
                assertEquals("CEST", transition.getName());
            } else {
                assertEquals("CET", transition.getName());
            }
            dst = !dst;
        }
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());
        assertEquals(7200000, transitions.get(2).getOffset());
        assertEquals(1206838800000L, transitions.get(2).getTime());
        assertEquals(3600000, transitions.get(3).getOffset());
        assertEquals(1224982800000L, transitions.get(3).getTime());
        assertEquals(7200000, transitions.get(4).getOffset());
        assertEquals(1238288400000L, transitions.get(4).getTime());
        assertEquals(3600000, transitions.get(5).getOffset());
        assertEquals(1256432400000L, transitions.get(5).getTime());

        assertEquals("Europe/Berlin", vctz.toID()); // First run
        assertEquals("Europe/Berlin", vctz.toID()); // Second run (cached)
    }

    public void testTimeZoneHelperFromICalendarBerlinCount() throws Exception {

        VTimezone vtz = new VTimezone();
        vtz.addProperty("TZID", "Berlino"); // No hint
        TzStandardComponent standardC = new TzStandardComponent();
        standardC.addProperty("TZOFFSETFROM", "+0200");
        standardC.addProperty("TZOFFSETTO", "+0100");
        standardC.addProperty("TZNAME", "CET");
        standardC.addProperty("DTSTART", "19701025T030000");
        standardC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10;COUNT=40");
        TzDaylightComponent dayLightC = new TzDaylightComponent();
        dayLightC.addProperty("TZOFFSETFROM", "+0100");
        dayLightC.addProperty("TZOFFSETTO", "+0200");
        dayLightC.addProperty("TZNAME", "CEST");
        dayLightC.addProperty("DTSTART", "19700329T020000");
        dayLightC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3;COUNT=40");
        vtz.addStandardc(standardC);
        vtz.addDaylightc(dayLightC);

        TimeZoneHelper vctz = new TimeZoneHelper(vtz,
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("Berlino", vctz.getName());
        assertEquals(6, transitions.size());
        assertEquals(3600000, vctz.getBasicOffset());
        boolean dst = true;
        for (TimeZoneTransition transition : transitions) {
            if (dst) {
                assertEquals("CEST", transition.getName());
            } else {
                assertEquals("CET", transition.getName());
            }
            dst = !dst;
        }
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());
        assertEquals(7200000, transitions.get(2).getOffset());
        assertEquals(1206838800000L, transitions.get(2).getTime());
        assertEquals(3600000, transitions.get(3).getOffset());
        assertEquals(1224982800000L, transitions.get(3).getTime());
        assertEquals(7200000, transitions.get(4).getOffset());
        assertEquals(1238288400000L, transitions.get(4).getTime());
        assertEquals(3600000, transitions.get(5).getOffset());
        assertEquals(1256432400000L, transitions.get(5).getTime());

        assertEquals("Europe/Berlin", vctz.toID()); // First run
        assertEquals("Europe/Berlin", vctz.toID()); // Second run (cached)
    }
    
    public void testTimeZoneHelperFromICalendarBerlinUsingTZID() throws Exception {

        VTimezone vtz = new VTimezone();
        vtz.addProperty("TZID", "/mozilla.org/20050126_1/Europe/Berlin");
        TzStandardComponent standardC = new TzStandardComponent();
        standardC.addProperty("TZOFFSETFROM", "+0201"); // Wrong on purpose
        standardC.addProperty("TZOFFSETTO", "+0113"); // Wrong on purpose
        standardC.addProperty("TZNAME", "CET");
        standardC.addProperty("DTSTART", "19701025T030100"); // Wrong on purpose
        standardC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-2SU;BYMONTH=10");  // Wrong on purpose
        TzDaylightComponent dayLightC = new TzDaylightComponent();
        dayLightC.addProperty("TZOFFSETFROM", "+0100");
        dayLightC.addProperty("TZOFFSETTO", "+0200");
        dayLightC.addProperty("TZNAME", "CEST");
        dayLightC.addProperty("DTSTART", "19700329T020000");
        dayLightC.addProperty("RRULE", "FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3");
        vtz.addStandardc(standardC);
        vtz.addDaylightc(dayLightC);

        TimeZoneHelper vctz = new TimeZoneHelper(vtz,
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("/mozilla.org/20050126_1/Europe/Berlin", vctz.getName());
        assertEquals(10, transitions.size());
        assertEquals(3600000, vctz.getBasicOffset());
        boolean dst = true;
        for (TimeZoneTransition transition : transitions) {
            if (dst) {
                assertEquals("Europe/Berlin", transition.getName());
            } else {
                assertEquals("Europe/Berlin", transition.getName());
            }
            dst = !dst;
        }
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());
        assertEquals(7200000, transitions.get(2).getOffset());
        assertEquals(1206838800000L, transitions.get(2).getTime());
        assertEquals(3600000, transitions.get(3).getOffset());
        assertEquals(1224982800000L, transitions.get(3).getTime());
        assertEquals(7200000, transitions.get(4).getOffset());
        assertEquals(1238288400000L, transitions.get(4).getTime());
        assertEquals(3600000, transitions.get(5).getOffset());
        assertEquals(1256432400000L, transitions.get(5).getTime());
        assertEquals(7200000, transitions.get(6).getOffset());
        assertEquals(1269738000000L, transitions.get(6).getTime());
        assertEquals(3600000, transitions.get(7).getOffset());
        assertEquals(1288486800000L, transitions.get(7).getTime());
        assertEquals(7200000, transitions.get(8).getOffset());
        assertEquals(1301187600000L, transitions.get(8).getTime());
        assertEquals(3600000, transitions.get(9).getOffset());
        assertEquals(1319936400000L, transitions.get(9).getTime());

        assertEquals("Europe/Berlin", vctz.toID()); // First run
        assertEquals("Europe/Berlin", vctz.toID()); // Second run (cached)
    }    

    public void testTimeZoneHelperFromOlsonIDRome() {

        TimeZoneHelper vctz = new TimeZoneHelper("Europe/Rome",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("Europe/Rome", vctz.toID());
        assertEquals(3600000, vctz.getBasicOffset());
        assertEquals("Europe/Rome", transitions.get(0).getName());
        assertEquals(7200000, transitions.get(0).getOffset());
        assertEquals(1174784400000L, transitions.get(0).getTime());
        assertEquals("Europe/Rome", transitions.get(1).getName());
        assertEquals(3600000, transitions.get(1).getOffset());
        assertEquals(1193533200000L, transitions.get(1).getTime());

    }
    
    public void testTimeZoneHelperFromOlsonIDCanberra() {

        TimeZoneHelper vctz = new TimeZoneHelper("Australia/Canberra",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("Australia/Canberra", vctz.toID());
        assertEquals(36000000, vctz.getBasicOffset());
        assertEquals("Australia/Canberra", transitions.get(0).getName());
        assertEquals(39600000, transitions.get(0).getOffset());
        assertEquals(1162051200000L, transitions.get(0).getTime());
        assertEquals("Australia/Canberra", transitions.get(1).getName());
        assertEquals(36000000, transitions.get(1).getOffset());
        assertEquals(1174752000000L, transitions.get(1).getTime());

    }

    public void testTimeZoneHelperFromOlsonIDBogota() {

        TimeZoneHelper vctz = new TimeZoneHelper("America/Bogota",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        List<TimeZoneTransition> transitions = vctz.getTransitions();

        assertEquals("America/Bogota", vctz.toID());
        assertEquals(-18000000, vctz.getBasicOffset());
        assertEquals(0, transitions.size());
    }

    public void testTimeZoneHelperToXVCalendarRome() {

        TimeZoneHelper vctz = new TimeZoneHelper("Europe/Rome",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        Property tz = vctz.getTZ();
        List<Property> dayLightList = vctz.getDaylightList();

        assertEquals(6, dayLightList.size());
        assertEquals("TZ", tz.getName());
        assertEquals("+0100", tz.getValue());
        assertEquals("DAYLIGHT", dayLightList.get(0).getName());
        assertEquals("TRUE;+0200;20070325T020000;20071028T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(0).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(1).getName());
        assertEquals("TRUE;+0200;20080330T020000;20081026T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(1).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(2).getName());
        assertEquals("TRUE;+0200;20090329T020000;20091025T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(2).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(3).getName());
        assertEquals("TRUE;+0200;20100328T020000;20101031T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(3).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(4).getName());
        assertEquals("TRUE;+0200;20110327T020000;20111030T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(4).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(5).getName());
        assertEquals("TRUE;+0200;20120325T020000;20121028T030000;Europe/Rome;Europe/Rome",
                dayLightList.get(5).getValue());
    }

    public void testTimeZoneHelperToXVCalendarBogota() {

        TimeZoneHelper vctz = new TimeZoneHelper("America/Bogota",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        Property tz = vctz.getTZ();
        List<Property> dayLightList = vctz.getDaylightList();

        assertEquals(1, dayLightList.size());
        assertEquals("TZ", tz.getName());
        assertEquals("-0500", tz.getValue());
        assertEquals("DAYLIGHT", dayLightList.get(0).getName());
        assertEquals("FALSE", dayLightList.get(0).getValue());
    }

    public void testTimeZoneHelperToXVCalendarCanberra() {

        TimeZoneHelper vctz = new TimeZoneHelper("Australia/Canberra",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        Property tz = vctz.getTZ();
        List<Property> dayLightList = vctz.getDaylightList();

        assertEquals(6, dayLightList.size());
        assertEquals("TZ", tz.getName());
        assertEquals("+1000", tz.getValue());
        assertEquals("DAYLIGHT", dayLightList.get(0).getName());
        assertEquals("TRUE;+1100;20061029T020000;20070325T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(0).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(1).getName());
        assertEquals("TRUE;+1100;20071028T020000;20080330T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(1).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(2).getName());
        assertEquals("TRUE;+1100;20081026T020000;20090329T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(2).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(3).getName());
        assertEquals("TRUE;+1100;20091025T020000;20100328T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(3).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(4).getName());
        assertEquals("TRUE;+1100;20101031T020000;20110327T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(4).getValue());
        assertEquals("DAYLIGHT", dayLightList.get(5).getName());
        assertEquals("TRUE;+1100;20111030T020000;20120325T030000;Australia/Canberra;Australia/Canberra",
                dayLightList.get(5).getValue());

    }

    /* @todo Reactivate this test when the iCalendar issues are solved

    public void testTimeZoneHelper_ToICalendar_Rome() {

        TimeZoneHelper vctz = new TimeZoneHelper("Europe/Rome",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC

        VTimezone vtz = vctz.getVTimezone();
        List<VComponent> dayLightComponents = vtz.getComponents("DAYLIGHT");
        List<VComponent> standardComponents = vtz.getComponents("STANDARD");

        assertEquals("Europe/Rome", vtz.getProperty("TZID").getValue());
        assertEquals(1, dayLightComponents.size());
        TzDaylightComponent dayLight =
                (TzDaylightComponent) dayLightComponents.get(0);
        assertEquals("+0100",
                dayLight.getProperty("TZOFFSETFROM").getValue());
        assertEquals("+0200",
                dayLight.getProperty("TZOFFSETTO").getValue());
        assertEquals("20070325T020000",
                dayLight.getProperty("DTSTART").getValue());
        assertEquals("FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3",
                dayLight.getProperty("RRULE").getValue());
        assertEquals(1, standardComponents.size());
        TzStandardComponent standard =
                (TzStandardComponent) standardComponents.get(0);
        assertEquals("+0200",
                standard.getProperty("TZOFFSETFROM").getValue());
        assertEquals("+0100",
                standard.getProperty("TZOFFSETTO").getValue());
        assertEquals("20071028T030000",
                standard.getProperty("DTSTART").getValue());
        assertEquals("FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10",
                standard.getProperty("RRULE").getValue());
    }
     */

    public void testTimeZoneHelperToICalendarBogota() {

        TimeZoneHelper vctz = new TimeZoneHelper("America/Bogota",
                0L,              // 01 Jan 1970 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC

        VTimezone vtz = vctz.getVTimezone();
        List<VComponent> dayLightComponents = vtz.getComponents("DAYLIGHT");
        List<VComponent> standardComponents = vtz.getComponents("STANDARD");

        assertEquals("America/Bogota", vtz.getProperty("TZID").getValue());
        assertEquals(0, dayLightComponents.size());
        assertEquals(2, standardComponents.size());
        TzStandardComponent standard =
                (TzStandardComponent) standardComponents.get(0);
        assertEquals("-0500",
                standard.getProperty("TZOFFSETFROM").getValue());
        assertEquals("-0400",
                standard.getProperty("TZOFFSETTO").getValue());
        assertEquals("19920503T000000",
                standard.getProperty("DTSTART").getValue());
        assertEquals("19920503T000000",
                standard.getProperty("RDATE").getValue());

        standard = (TzStandardComponent) standardComponents.get(1);
        assertEquals("-0400",
                standard.getProperty("TZOFFSETFROM").getValue());
        assertEquals("-0500",
                standard.getProperty("TZOFFSETTO").getValue());
        assertEquals("19930404T000000",
                standard.getProperty("DTSTART").getValue());
        assertEquals("19930404T000000",
                standard.getProperty("RDATE").getValue());
    }

    public void testTimeZoneHelperToICalendarJohannesburg() {

        TimeZoneHelper vctz = new TimeZoneHelper("Africa/Johannesburg",
                0L,              // 01 Jan 1970 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC

        VTimezone vtz = vctz.getVTimezone();
        List<VComponent> dayLightComponents = vtz.getComponents("DAYLIGHT");
        List<VComponent> standardComponents = vtz.getComponents("STANDARD");

        assertEquals("Africa/Johannesburg", vtz.getProperty("TZID").getValue());
        assertEquals(0, dayLightComponents.size());
        assertEquals(1, standardComponents.size());
        TzStandardComponent standard =
                (TzStandardComponent) standardComponents.get(0);
        assertEquals("+0200",
                standard.getProperty("TZOFFSETFROM").getValue());
        assertEquals("+0200",
                standard.getProperty("TZOFFSETTO").getValue());
        assertEquals("19700101T000000",
                standard.getProperty("DTSTART").getValue());
        assertEquals("19700101T000000",
                standard.getProperty("RDATE").getValue());
    }
    
    public void testTimeZoneHelperToICalendarCanberra() {

        TimeZoneHelper vctz = new TimeZoneHelper("Australia/Canberra",
                1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC
        VTimezone vtz = vctz.getVTimezone();
        List<VComponent> dayLightComponents = vtz.getComponents("DAYLIGHT");
        List<VComponent> standardComponents = vtz.getComponents("STANDARD");

        assertEquals("Australia/Canberra", vtz.getProperty("TZID").getValue());
        assertEquals(1, dayLightComponents.size());
        TzDaylightComponent dayLight =
                (TzDaylightComponent) dayLightComponents.get(0);
        assertEquals("+1000",
                dayLight.getProperty("TZOFFSETFROM").getValue());
        assertEquals("+1100",
                dayLight.getProperty("TZOFFSETTO").getValue());
        assertEquals("20061029T020000",
                dayLight.getProperty("DTSTART").getValue());
        assertEquals("FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=10",
                dayLight.getProperty("RRULE").getValue());

        assertEquals(1, standardComponents.size());
        TzStandardComponent standard =
                (TzStandardComponent) standardComponents.get(0);
        assertEquals("+1100",
                standard.getProperty("TZOFFSETFROM").getValue());
        assertEquals("+1000",
                standard.getProperty("TZOFFSETTO").getValue());
        assertEquals("20070325T030000",
                standard.getProperty("DTSTART").getValue());
        assertEquals("FREQ=YEARLY;INTERVAL=1;BYDAY=-1SU;BYMONTH=3",
                standard.getProperty("RRULE").getValue());
    }

    @SuppressWarnings("unchecked")
    public void testTimeZoneHelperCheckAllOlsonIDsWithXVCalendar() throws Exception {

        int mistakes = 0; // counter for mistakes in time zone handling
        
        /*
         * Max number of mistakes allowed. This value should be lowered as soon
         * as improvements in the code that handles time zones reduce the
         * number of mistakes. In this way, this test can be used to certify
         * that the number of properly handled time zones is being constantly
         * raised. 
         */
        final int MAX_MISTAKES = 16;

        System.out.println("================================= vCalendar (1.0)");
        for (String id : (Set<String>) DateTimeZone.getAvailableIDs()) {

            TimeZoneHelper vctzOut = new TimeZoneHelper(id,
              //0L,              // 01 Jan 1970 @ 00:00:00 UTC
                 915148800000L,  // 01 Jan 1999 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
            List<Property> dayLightList = vctzOut.getDaylightList();
            Property tz = vctzOut.getTZ();

            TimeZoneHelper vctzIn = new TimeZoneHelper(tz, dayLightList);
            vctzIn.setName(""); // The name should not be used as a hint for 
                                // this "test"
            System.out.print('\n' + id + " --> " + vctzIn.toID());
            vctzIn.clearCachedID();            
            String guess = vctzIn.toID(id);
            if (!id.equals(guess)) {
                System.out.print(" (WRONG!)");
                mistakes++;
            }
        }
        System.out.println("\n\n" + mistakes + " time zones were not properly handled.");
        assertTrue(mistakes <= MAX_MISTAKES);
        System.out.println();
    }

    @SuppressWarnings("unchecked")
    public void testTimeZoneHelperCheckAllOlsonIDsWithICalendar() throws Exception {

        int mistakes = 0; // counter for mistakes in time zone handling

        /*
         * Max number of mistakes allowed. This value should be lowered as soon
         * as improvements in the code that handles time zones reduce the
         * number of mistakes. In this way, this test can be used to certify
         * that the number of properly handled time zones is being constantly
         * raised.
         */
        final int MAX_MISTAKES = 125;

        System.out.println("================================= iCalendar (2.0)");
        for (String id : (Set<String>) DateTimeZone.getAvailableIDs()) {

            TimeZoneHelper vctzOut = new TimeZoneHelper(id,
              //0L,              // 01 Jan 1970 @ 00:00:00 UTC
                 915148800000L,  // 01 Jan 1999 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
            VTimezone vTimezone = vctzOut.getVTimezone();
             
            // Uncomment this to debug the iCalendar items referring to one 
            // continent:
            /*
            String continent = "Australia"; // or whatever you want to watch
            if (id.startsWith(continent)) {
                System.out.println("\n\n" + vTimezone);
            }
            */
            
            vTimezone.getProperty("TZID").setValue(""); // The name should not be used as a hint for 
                                                        // this "test"
            
            TimeZoneHelper vctzIn = new TimeZoneHelper(vTimezone,
                 915148800000L,  // 01 Jan 1999 @ 00:00:00 UTC
                1324771200000L); // 25 Dec 2011 @ 00:00:00 UTC
            System.out.print('\n' + id + " --> " + vctzIn.toID());
            vctzIn.clearCachedID();
            String guess = vctzIn.toID(id);
            if (!id.equals(guess)) {
                System.out.print(" (WRONG!)");
                mistakes++;
             }
        }
        System.out.println("\n\n" + mistakes + " time zones were not properly handled.");
        assertTrue(mistakes <= MAX_MISTAKES);
        System.out.println();
    }
}