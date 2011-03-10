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
package com.funambol.foundation.engine.source;

import java.util.TimeZone;

import junit.framework.TestCase;

import junitx.util.PrivateAccessor;

import com.funambol.framework.core.DevInf;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.ConvertDatePolicy;
import com.funambol.framework.server.Sync4jDevice;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.foundation.items.model.CalendarWrapper;

/**
 * PIMSyncSource's test cases
 * @version $Id: PIMCalendarSyncSourceTest.java,v 1.1.1.1 2008-03-20 21:39:12 stefano_fornari Exp $
 */
public class PIMCalendarSyncSourceTest extends TestCase {

    private PIMCalendarSyncSource syncSource = null;

    // ------------------------------------------------------------ Constructors
    public PIMCalendarSyncSourceTest(String testName) {
        super(testName);
        syncSource = new PIMCalendarSyncSource();
    }

    //--------------------------------------------------------------- Test cases
    /**
     * Test handleConvertDate with default values
     */
    public void testHandleConvertDate1() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice();

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        String deviceTimeZone =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = CONVERT_DATE
     * - timezone = Europe/Rome
     */
    public void testHandleConvertDate2() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.CONVERT_DATE);
        device.setTimeZone("Europe/Rome");

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      TimeZone.getTimeZone("Europe/Rome"));
        assertEquals(deviceTimeZoneDescr, "Europe/Rome");
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = NO_CONVERT_DATE
     * - timezone = Europe/Rome
     */
    public void testHandleConvertDate3() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.NO_CONVERT_DATE);
        device.setTimeZone("Europe/Rome");

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone     , TimeZone.getTimeZone("Europe/Rome"));
        assertEquals(deviceTimeZoneDescr, "Europe/Rome");
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = CONVERT_DATE
     * - timezone = null
     */
    public void testHandleConvertDate4() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.CONVERT_DATE);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = NO_CONVERT_DATE
     * - timezone = null
     */
    public void testHandleConvertDate5() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.NO_CONVERT_DATE);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = NO_CONVERT_DATE
     * - timezone = null
     * - utc = FALSE
     */
    public void testHandleConvertDate6() throws Exception, Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.NO_CONVERT_DATE);

        DevInf devInf = new DevInf();
        devInf.setUTC(false);
        Capabilities cap = new Capabilities(new Long(100), devInf);
        device.setCapabilities(cap);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = CONVERT_DATE
     * - timezone = Europe/Rome
     * - utc = TRUE
     */
    public void testHandleConvertDate7() throws Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.CONVERT_DATE);
        device.setTimeZone("Europe/Rome");

        DevInf devInf = new DevInf();
        devInf.setUTC(true);
        Capabilities cap = new Capabilities(new Long(100), devInf);
        device.setCapabilities(cap);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      TimeZone.getTimeZone("Europe/Rome"));
        assertEquals(deviceTimeZoneDescr, "Europe/Rome");
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = UNSPECIFIED
     * - timezone = Europe/Rome
     * - utc = TRUE
     */
    public void testHandleConvertDate8() throws Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.UNSPECIFIED);
        device.setTimeZone("Europe/Rome");

        DevInf devInf = new DevInf();
        devInf.setUTC(true);
        Capabilities cap = new Capabilities(new Long(100), devInf);
        device.setCapabilities(cap);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = UNSPECIFIED
     * - timezone = Europe/Rome
     * - utc = FALSE
     */
    public void testHandleConvertDate9() throws Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.UNSPECIFIED);
        device.setTimeZone("Europe/Rome");

        DevInf devInf = new DevInf();
        devInf.setUTC(false);
        Capabilities cap = new Capabilities(new Long(100), devInf);
        device.setCapabilities(cap);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      TimeZone.getTimeZone("Europe/Rome"));
        assertEquals(deviceTimeZoneDescr, "Europe/Rome");
    }

    /**
     * Test handleConvertDate with:
     * - convertDate = UNSPECIFIED
     * - timezone = null
     * - utc = FALSE
     */
    public void testHandleConvertDate10() throws Throwable {
        Sync4jDevice device = new Sync4jDevice("deviceId");
        device.setConvertDatePolicy(ConvertDatePolicy.UNSPECIFIED);

        DevInf devInf = new DevInf();
        devInf.setUTC(false);
        Capabilities cap = new Capabilities(new Long(100), devInf);
        device.setCapabilities(cap);

        PrivateAccessor.invoke(
            syncSource,
            "handleConvertDate",
            new Class[] {Sync4jDevice.class},
            new Object[] {device}
        );
        TimeZone deviceTimeZone = (TimeZone)PrivateAccessor.getField(syncSource, "deviceTimeZone");

        String deviceTimeZoneDescr =
            (String)PrivateAccessor.getField(syncSource, "deviceTimeZoneDescription");

        assertEquals(deviceTimeZone,      null);
        assertEquals(deviceTimeZoneDescr, null);
    }

    public void testNullPropertyConversion() throws Throwable {

        Calendar cal = new Calendar();
        CalendarContent calContent = new Event();
        cal.setCalendarContent(calContent);
        CalendarWrapper cw = new CalendarWrapper("uid", "userId", cal);

        String location = null;
        calContent.getLocation().setPropertyValue(location);

        String vcal = (String)PrivateAccessor.invoke(syncSource, "convert",
                new Class[] {Calendar.class, String.class},
                new Object[]{cal, "text/x-vcalendar"});

        assertEquals(-1, vcal.indexOf("LOCATION"));
    }

    public void testEmptyPropertyConversion() throws Throwable {

        Calendar cal = new Calendar();
        CalendarContent calContent = new Event();
        cal.setCalendarContent(calContent);
        CalendarWrapper cw = new CalendarWrapper("uid", "userId", cal);
        String location = "";
        calContent.getLocation().setPropertyValue(location);

        String vcal = (String)PrivateAccessor.invoke(syncSource, "convert",
                new Class[] {Calendar.class, String.class},
                new Object[]{cal, "text/x-vcalendar"});

        assertTrue(vcal.indexOf("LOCATION") != -1);
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
}
