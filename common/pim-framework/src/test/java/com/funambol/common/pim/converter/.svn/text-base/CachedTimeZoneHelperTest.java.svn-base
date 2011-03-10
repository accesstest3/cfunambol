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

import java.util.List;

import junit.framework.TestCase;

import com.funambol.common.pim.model.Property;

/**
 * CachedTimeZoneHelper test cases
 * @version $Id: CachedTimeZoneHelperTest.java,v 1.2 2008-06-05 07:12:32 luigiafassina Exp $
 */
public class CachedTimeZoneHelperTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public CachedTimeZoneHelperTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        CachedTimeZoneHelper.clearCaches();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getXVCalendarProperties method, of class CachedTimeZoneHelper.
     */
    public void testGetXVCalendarProperties() {

        assertEquals("text/x-vcalendar:0\ntext/calendar:0",
                CachedTimeZoneHelper.cacheStatus("Europe/Rome"));

        // Tests Europe/Rome 2007-2012
        TimeZoneHelper tzh = new CachedTimeZoneHelper("Europe/Rome",
                        1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                        1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC)
        List<Property> xvCalendarProperties = tzh.getXVCalendarProperties();

        assertEquals(7, xvCalendarProperties.size());
        Property tz = xvCalendarProperties.get(0);
        List<Property> dayLightList = xvCalendarProperties.subList(1,7);

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
        assertEquals("text/x-vcalendar:7\ntext/calendar:0",
                CachedTimeZoneHelper.cacheStatus("Europe/Rome"));

        // Testing Europe/Rome 2007-20012 again
        TimeZoneHelper tzh2 = new CachedTimeZoneHelper("Europe/Rome",
                        1167609600000L, // 01 Jan 2007 @ 00:00:00 UTC
                        1349049600000L); // 01 Oct 2012 @ 00:00:00 UTC)
        xvCalendarProperties = tzh2.getXVCalendarProperties();

        assertEquals(7, xvCalendarProperties.size());
        tz = xvCalendarProperties.get(0);
        dayLightList = xvCalendarProperties.subList(1,7);

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
        assertEquals("text/x-vcalendar:7\ntext/calendar:0",
                CachedTimeZoneHelper.cacheStatus("Europe/Rome"));

    }

    /**
     * Test of getVTimezone method, of class CachedTimeZoneHelper.
     */
    public void testGetVTimezone() {

        // @todo Implement it
    }

}
