/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.foundation.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.Task;

import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.SourceRef;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;

/**
 * Test cases for SyncSourceUtil class.
 *
 * @version $Id: SyncSourceUtilTest.java 34865 2010-07-05 15:18:44Z luigiafassina $
 */
public class SyncSourceUtilTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public SyncSourceUtilTest(String testName) {
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

    // -------------------------------------------------------------- Test cases

    // ---------------------------------------------------------------- Calendar
    public void testGetCalendarPreferredType_Null()
    throws Exception, Throwable {

        Set rxPrefs = null;

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_Empty()
    throws Exception, Throwable {

        Set rxPrefs = new HashSet();

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SIFE_Event()
    throws Exception, Throwable {

        Set rxPrefs = new HashSet();
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("application/*");

        String expected = "text/x-s4j-sif?";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_VCal_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/x-vcalendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/calendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/calendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCal_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/calendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_Cal_VCal_SIFE_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("text/calendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_VCal_SIFT_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");

        String expected = "text/x-vcalendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_VCal_SIFE_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sife");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SIFE_SIFT_VCard_Event()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");

        String expected = "text/x-s4j-sif?";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Event.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_VCal_SIFT_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SIFE_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("application/*");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SIFT_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-s4j-sift");
        rxPrefs.add("application/*");

        String expected = "text/x-s4j-sif?";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_VCal_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/x-vcalendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/calendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/calendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCal_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/calendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_Cal_VCal_SIFE_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("text/calendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_VCal_SIFE_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sife");

        String expected = "text/x-vcalendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_SIFE_SIFT_VCard_Task()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-s4j-sife");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");

        String expected = "text/x-s4j-sif?";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, Task.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCard_CalContent()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/calendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/calendar";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, CalendarContent.class}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_Cal_VCard_SIFT_CalContent()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/calendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, CalendarContent.class}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SIFE_SIFT_VCard_CalContent()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sift");
        rxPrefs.add("text/x-s4j-sife");

        String expected = "text/x-s4j-sif?";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getCalendarPreferredType",
            new Class[] {Set.class, Class.class},
            new Object[] {rxPrefs, CalendarContent.class}
        );
        assertEquals("Wrong content type", expected, result);

    }

    public void testGetCalendarPreferredType_SyncContext_VCal_VCard_Event()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-vcalendar";
        String result = 
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_SyncContext_VCal_SIFT_VCard_Event()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("stask"));
        dataStore.setDisplayName("stask");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-vcalendar";
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);

        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_SyncContext_SIFE_SIFT_VCard_Event()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("scal"));
        dataStore.setDisplayName("scal");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sife", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sife", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("stask"));
        dataStore.setDisplayName("stask");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-s4j-sif?";
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_SyncContext_Cal_VCal_VCard_Event()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("task"));
        dataStore.setDisplayName("task");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("event"));
        dataStore.setDisplayName("event");
        dataStore.setRxPref(new CTInfo("text/calendar", "2.1"));
        dataStore.setTxPref(new CTInfo("text/calendar", "2.1"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SyncContext_Cal_SIFE_VCard_Event()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("event"));
        dataStore.setDisplayName("event");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("scal"));
        dataStore.setDisplayName("scal");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sife", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sife", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SyncContext_SIFT_VCard_Task()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("stask"));
        dataStore.setDisplayName("stask");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-s4j-sif?";
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Task.class);
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetCalendarPreferredType_SyncContext_VCal_SIFT_VCard_CalContent()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("stask"));
        dataStore.setDisplayName("stask");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sift", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, CalendarContent.class);
        assertNull("Wrong content type", result);
    }

    public void testGetCalendarPreferredType_SyncContext_VCal_VCard_CalContent()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-vcalendar";
        String result =
            SyncSourceUtil.getCalendarPreferredType(syncContext, Event.class);
        assertEquals("Wrong content type", expected, result);
    }

    // ----------------------------------------------------------------- Contact
    public void testGetContactPreferredType_Null()
    throws Exception, Throwable {

        Set rxPrefs = null;

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetContactPreferredType_Empty()
    throws Exception, Throwable {

        Set rxPrefs = new HashSet();

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetContactPreferredType_Cal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetContactPreferredType_VCard_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");

        String expected = "text/x-vcard";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetContactPreferredType_SIFC_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-s4j-sifc");

        String expected = "text/x-s4j-sifc";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetContactPreferredType_VCard_SIFC_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-vcard");
        rxPrefs.add("text/x-s4j-sifc");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getContactPreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetContactPreferredType_SyncContext_VCard_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-vcard";
        String result = SyncSourceUtil.getContactPreferredType(syncContext);
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetContactPreferredType_SyncContext_VCard_SIFC_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("card"));
        dataStore.setDisplayName("card");
        dataStore.setRxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcard", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("scard"));
        dataStore.setDisplayName("scard");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sifc", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sifc", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String result = SyncSourceUtil.getContactPreferredType(syncContext);
        assertNull("Wrong content type", result);
    }

    public void testGetContactPreferredType_SyncContext_SIFC_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("scard"));
        dataStore.setDisplayName("scard");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sifc", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sifc", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-s4j-sifc";
        String result = SyncSourceUtil.getContactPreferredType(syncContext);
        assertEquals("Wrong content type", expected, result);
    }

    // -------------------------------------------------------------------- Note
    public void testGetNotePreferredType_Null()
    throws Exception, Throwable {

        Set rxPrefs = null;

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetNotePreferredType_Empty()
    throws Exception, Throwable {

        Set rxPrefs = new HashSet();

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetNotePreferredType_Cal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetNotePreferredType_Plain_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/plain");

        String expected = "text/plain";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetNotePreferredType_SIFN_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/x-s4j-sifn");

        String expected = "text/x-s4j-sifn";
        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetNotePreferredType_Plain_SIFN_VCal()
    throws Exception, Throwable {

        Set<String> rxPrefs = new HashSet<String>();
        rxPrefs.add("text/x-vcalendar");
        rxPrefs.add("text/plain");
        rxPrefs.add("text/x-s4j-sifn");

        String result = (String)PrivateAccessor.invoke(
            SyncSourceUtil.class,
            "getNotePreferredType",
            new Class[] {Set.class},
            new Object[] {rxPrefs}
        );
        assertNull("Wrong content type", result);
    }

    public void testGetNotePreferredType_SyncContext_Plain_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("note"));
        dataStore.setDisplayName("note");
        dataStore.setRxPref(new CTInfo("text/plain", "1.0"));
        dataStore.setTxPref(new CTInfo("text/plain", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/plain";
        String result = SyncSourceUtil.getNotePreferredType(syncContext);
        assertEquals("Wrong content type", expected, result);
    }

    public void testGetNotePreferredType_SyncContext_Plain_SIFN_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("note"));
        dataStore.setDisplayName("note");
        dataStore.setRxPref(new CTInfo("text/plain", "1.0"));
        dataStore.setTxPref(new CTInfo("text/plain", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("snote"));
        dataStore.setDisplayName("snote");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sifn", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sifn", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String result = SyncSourceUtil.getNotePreferredType(syncContext);
        assertNull("Wrong content type", result);
    }

    public void testGetNotePreferredType_SyncContext_SIFN_VCal()
    throws Exception, Throwable {

        List<DataStore> dataStores = new ArrayList<DataStore>();

        DataStore dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("snote"));
        dataStore.setDisplayName("snote");
        dataStore.setRxPref(new CTInfo("text/x-s4j-sifn", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-s4j-sifn", "1.0"));
        dataStores.add(dataStore);

        dataStore = new DataStore();
        dataStore.setSourceRef(new SourceRef("cal"));
        dataStore.setDisplayName("cal");
        dataStore.setRxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStore.setTxPref(new CTInfo("text/x-vcalendar", "1.0"));
        dataStores.add(dataStore);

        SyncContext syncContext = createSyncContext(dataStores);

        String expected = "text/x-s4j-sifn";
        String result = SyncSourceUtil.getNotePreferredType(syncContext);
        assertEquals("Wrong content type", expected, result);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns a fake SyncContext object in which the device capabilities are
     * set using the given DataStore list.
     *
     * @param dataStores the list of DataStore to set to the device
     * @return the SyncContext object
     */
    private SyncContext createSyncContext(List<DataStore> dataStores) {
        Sync4jUser user = new Sync4jUser();
        user.setUsername("guest");
        DevInf devInf = new DevInf();
        devInf.setDataStores(new ArrayList(dataStores));
        Capabilities caps = new Capabilities(10L, devInf);
        Sync4jDevice device = new Sync4jDevice("100");
        device.setCapabilities(caps);
        Sync4jPrincipal principal = new Sync4jPrincipal(user, device);
        return new SyncContext(principal, 200, null, null, 1);
    }
}
