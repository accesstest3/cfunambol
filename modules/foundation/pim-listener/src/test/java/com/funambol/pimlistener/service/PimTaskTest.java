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
package com.funambol.pimlistener.service;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.db.RoutingDataSource;

/**
 * Test cases for PimTask class.
 *
 * @version $Id$
 */
public class PimTaskTest extends TestCase implements FnblDatabaseConstants {

    // --------------------------------------------------------------- Constants
    private static DataSource coreDs = null;
    private static RoutingDataSource userRds = null;

    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    protected IDatabaseTester userDatabaseTester = null;
    protected IDatabaseTester coreDatabaseTester = null;
    protected IDatabaseConnection userConn = null;
    protected IDatabaseConnection coreConn = null;
    protected IDataSet userDataSet = null;
    protected IDataSet coreDataSet = null;

    private static final String RESOURCES_DIR =
        "src/test/resources/data/com/funambol/pimlistener/service/PimTask/";
    private static final String DATASET_CORE =
            RESOURCES_DIR + "initial-db-dataset-core.xml";
    private static final String DATASET_USER =
            RESOURCES_DIR + "initial-db-dataset-user.xml";

    private static final String DATASET_CORE_STEP_A = 
        RESOURCES_DIR + "db-dataset-core-step-A.xml";
    private static final String DATASET_CORE_STEP_B =
        RESOURCES_DIR + "db-dataset-core-step-B.xml";
    private static final String DATASET_USER_STEP_B = 
        RESOURCES_DIR + "db-dataset-user-step-B.xml";
    private static final String DATASET_USER_STEP_C =
        RESOURCES_DIR + "db-dataset-user-step-C.xml";

    // ------------------------------------------------------------ Constructors
    public PimTaskTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getSourcesToNotify method when only the push for contact
     * syncsources is enabled.
     */
    public void testGetSourcesToNotify_ContactNeverSync() throws Throwable {
        System.out.println("testGetSourcesToNotify_NeverSync");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        List<String> syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "getSourcesToNotify",
                new Class[]{Sync4jPrincipal.class},
                new Object[]{principal});

        // A contact is changed, but neither 'card' or 'scard' have never been
        // synchronized
        assertTrue("Wrong number of syncsources to sync (" +
                   syncSources.size() + ")", syncSources.isEmpty());
    }

    /**
     * Test of getSourcesToNotify method when only the push for contact
     * syncsources is enabled.
     */
    public void testGetSourcesToNotify_ContactInSync() throws Throwable {
        System.out.println("testGetSourcesToNotify_ContactInSync");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        coreDataSet = new FlatXmlDataSet(new File(DATASET_CORE_STEP_A));
        DatabaseOperation.INSERT.execute(coreConn, coreDataSet);

        List<String> syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "getSourcesToNotify",
                new Class[]{Sync4jPrincipal.class},
                new Object[]{principal});

        // A contact is changed and the 'card' sync source has been synchronized
        assertTrue("Wrong number of syncsource to sync (" +
                   syncSources.size() + ")", syncSources.size() == 1);

        PrivateAccessor.setField(instance, "timeLastCheck", System.currentTimeMillis());

        //
        // Same instace of PimTask: in this case, the timestamp of the last check
        // should be used to find the sync source to notify
        //
        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "getSourcesToNotify",
                new Class[]{Sync4jPrincipal.class},
                new Object[]{principal});

        //
        // The user has not synchronized the changes, but the 'card' syncsource
        // should not be notified for the same changes.
        //
        assertTrue("Found a syncsource to sync", syncSources.isEmpty());
    }

    /**
     * Test of getSourcesToNotify method when only the push for contact
     * syncsources is enabled.
     */
    public void testGetSourcesToNotify_ContactInSyncAfterACheck() throws Throwable {
        System.out.println("testGetSourcesToNotify_ContactInSyncAfterACheck");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        PrivateAccessor.setField(instance, "timeLastCheck", System.currentTimeMillis());

        coreDataSet = new FlatXmlDataSet(new File(DATASET_CORE_STEP_B));
        DatabaseOperation.INSERT.execute(coreConn, coreDataSet);

        userDataSet = new FlatXmlDataSet(new File(DATASET_USER_STEP_B));
        DatabaseOperation.INSERT.execute(userConn, userDataSet);

        List<String> syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "getSourcesToNotify",
                new Class[]{Sync4jPrincipal.class},
                new Object[]{principal});

        // A contact is changed and the 'card' sync source has been synchronized
        assertTrue("Wrong number of syncsource to sync (" +
                   syncSources.size() + ")", syncSources.size() == 1);
    }

    /**
     * Test of handleContactSyncSources method
     */
    public void testHandleContactSyncSources() throws Throwable {
        System.out.println("testHandleContactSyncSources");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        // The contact syncsources have never been synchronized
        Map<String, Long> pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("note", 1006317537670L);
        pimLastSyncs.put("cal", 1006317537670L);

        String syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleContactSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertNull("The syncsource is not null ("+syncSource+ ")", syncSource);

        // 'card' has been synchronized
        // use last sync time as reference time
        pimLastSyncs.put("card", 1006317537671L);

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleContactSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'card' syncsource using last sync time", "card", syncSource);

        // use the last check time as reference time
        PrivateAccessor.setField(instance, "timeLastCheck", 1226317537669L);

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleContactSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'card' syncsource using last check time", "card", syncSource);

        // 'scard' has been synchronized (after the sync of 'card')
        // use the last check time as reference time
        PrivateAccessor.setField(instance, "timeLastCheck", 1226317537669L);

        pimLastSyncs.put("scard", 1006317537674L);

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleContactSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'scard' syncsource using the last check time", "scard", syncSource);
    }

    /**
     * Test of handleNoteSyncSources method
     */
    public void testHandleNoteSyncSources() throws Throwable {
        System.out.println("testHandleNoteSyncSources");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        // The note syncsources have never been synchronized
        Map<String, Long> pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("card", 1006317537670L);
        pimLastSyncs.put("cal", 1006317537670L);

        String syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleNoteSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertNull("The syncsource is not null ("+syncSource+ ")", syncSource);

        // 'note' has been synchronized
        // use last sync time as reference time
        pimLastSyncs.put("note", 1006317537671L);

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleNoteSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'note' syncsource", "note", syncSource);

        // use the last check time as reference time
        PrivateAccessor.setField(instance, "timeLastCheck", System.currentTimeMillis());

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleNoteSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'note' syncsource", "note", syncSource);

        // 'snote' has been synchronized (after the sync of 'note')
        // use the last check time as reference time
        PrivateAccessor.setField(instance, "timeLastCheck", System.currentTimeMillis());

        pimLastSyncs.put("snote", 1006317537674L);

        syncSource = (String) PrivateAccessor.invoke(
                instance,
                "handleNoteSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("No found 'snote' syncsource", "snote", syncSource);
    }

    /**
     * Test of handleCalendarSyncSources method
     */
    public void testHandleCalendarSyncSources() throws Throwable {
        System.out.println("testHandleCalendarSyncSources");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        // The calendar syncsources have never been synchronized
        Map<String, Long> pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("note", 1006317537670L);
        pimLastSyncs.put("card", 1006317537670L);

        List<String> syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("The list of syncsources is not empty ("+syncSources.size()+
                   ")", syncSources.isEmpty());

        // 'cal' has been synchronized
        // use last sync time as reference time
        pimLastSyncs.put("cal", 1006317537671L);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or a wrong number of syncsources",
                   syncSources != null && syncSources.size() == 1);
        assertEquals("No found 'cal' syncsource using last sync time",
                     "cal", syncSources.get(0));

        // use the last check time as reference time
        PrivateAccessor.setField(instance, "timeLastCheck", 1226317537669L);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or a wrong number of syncsources using last check time",
                   syncSources != null && syncSources.size() == 1);
        assertEquals("No found 'cal' syncsource using last check time",
                     "cal", syncSources.get(0));

        //
        // 'cal' and 'task' syncsources have been synchronized togheter (even
        // if this has no more sense). Set timeLastCheck -1 in order to use the
        // last sync time as check time
        //
        PrivateAccessor.setField(instance, "timeLastCheck", -1);

        pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("cal" , 1226317537600L);
        pimLastSyncs.put("task", 1226317537600L);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertEquals("Found '"+syncSources.get(0)+"' instead of 'cal'",
                     "cal", syncSources.get(0));
    }

    /**
     * Test of handleCalendarSyncSources method
     */
    public void testHandleCalendarSyncSources_NoSif() throws Throwable {
        System.out.println("testHandleCalendarSyncSources_NoSif");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        Sync4jPrincipal principal = new Sync4jPrincipal(100L);
        principal.setDeviceId("fwm-1234567890");
        principal.setUsername("user1");

        // 'cal' is the last syncsource synchronized
        Map<String, Long> pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("cal"  , 1006317537677L);
        pimLastSyncs.put("event", 1006317537671L);
        pimLastSyncs.put("task" , 1006317537670L);

        List<String> syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or more than 1 syncsource",
                   syncSources != null && syncSources.size() == 1);
        assertEquals("No found 'cal' syncsource", "cal", syncSources.get(0));

        // 'event' is the last syncsource synchronized
        pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("cal"  , 1006317537600L);
        pimLastSyncs.put("event", 1006317537675L);
        pimLastSyncs.put("stask", 1006317537601L);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or more than 1 syncsource " + syncSources,
                   syncSources.size() == 1);
        assertEquals("No found 'event' syncsource", "event", syncSources.get(0));

        // 'event' and 'task' are the last syncsources synchronized
        pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("cal"  , 1006317537670L);
        pimLastSyncs.put("event", 1006317537671L);
        pimLastSyncs.put("task" , 1006317537675L);

        userDataSet = new FlatXmlDataSet(new File(DATASET_USER_STEP_C));
        DatabaseOperation.INSERT.execute(userConn, userDataSet);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or not 2 syncsources",
                   syncSources != null && syncSources.size() == 2);
        assertEquals("No found 'event' syncsource", "event", syncSources.get(0));
        assertEquals("No found 'task' syncsource", "task", syncSources.get(1));

        // 'stask' is the last syncsource synchronized
        pimLastSyncs = new HashMap<String, Long>();
        pimLastSyncs.put("cal"  , 1006317537670L);
        pimLastSyncs.put("event", 1006317537671L);
        pimLastSyncs.put("stask", 1006317537675L);

        syncSources = (List<String>) PrivateAccessor.invoke(
                instance,
                "handleCalendarSyncSources",
                new Class[]{Sync4jPrincipal.class, Map.class},
                new Object[]{principal, pimLastSyncs});

        assertTrue("Return null or not 1 syncsource",
                   syncSources != null && syncSources.size() == 1);
        assertEquals("No found 'stask' syncsource", "stask", syncSources.get(0));
    }

    /**
     * Test of checkSIFCalendarSyncSources method
     */
    public void testCheckSIFCalendarSyncSources() throws Throwable {
        System.out.println("testCheckSIFCalendarSyncSources");

        PimTask instance = new PimTask();
        TaskConfiguration taskConfig = new TaskConfiguration();
        taskConfig.setId(1L);
        taskConfig.setActive(true);
        instance.configure(taskConfig);

        long sifEventLastSync = 2L;
        long sifTaskLastSync  = -1L;
        long eventLastSync    = 2L;
        long taskLastSync     = 3L;

        boolean isSIF = (Boolean) PrivateAccessor.invoke(
                instance,
                "checkSIFCalendarSyncSources",
                new Class[]{long.class, long.class, long.class, long.class},
                new Object[]{sifEventLastSync, sifTaskLastSync, eventLastSync, taskLastSync});

        assertFalse("Last sync should be 'task'", isSIF);

        sifEventLastSync = 5L;
        sifTaskLastSync  = -1L;
        eventLastSync    = 2L;
        taskLastSync     = 3L;

        isSIF = (Boolean) PrivateAccessor.invoke(
                instance,
                "checkSIFCalendarSyncSources",
                new Class[]{long.class, long.class, long.class, long.class},
                new Object[]{sifEventLastSync, sifTaskLastSync, eventLastSync, taskLastSync});

        assertTrue("Last sync should be 'scal'", isSIF);

        sifEventLastSync = 5L;
        sifTaskLastSync  = 4L;
        eventLastSync    = 6L;
        taskLastSync     = 3L;

        isSIF = (Boolean) PrivateAccessor.invoke(
                instance,
                "checkSIFCalendarSyncSources",
                new Class[]{long.class, long.class, long.class, long.class},
                new Object[]{sifEventLastSync, sifTaskLastSync, eventLastSync, taskLastSync});

        assertFalse("Last sync should be 'event'", isSIF);

        sifEventLastSync = 5L;
        sifTaskLastSync  = 5L;
        eventLastSync    = -1L;
        taskLastSync     = -1L;

        isSIF = (Boolean) PrivateAccessor.invoke(
                instance,
                "checkSIFCalendarSyncSources",
                new Class[]{long.class, long.class, long.class, long.class},
                new Object[]{sifEventLastSync, sifTaskLastSync, eventLastSync, taskLastSync});

        assertTrue("Last sync should be a SIF source", isSIF);

        sifEventLastSync = -1L;
        sifTaskLastSync  = -1L;
        eventLastSync    = 5L;
        taskLastSync     = 5L;

        isSIF = (Boolean) PrivateAccessor.invoke(
                instance,
                "checkSIFCalendarSyncSources",
                new Class[]{long.class, long.class, long.class, long.class},
                new Object[]{sifEventLastSync, sifTaskLastSync, eventLastSync, taskLastSync});

        assertFalse("Last sync should be a not SIF source", isSIF);
    }

    // --------------------------------------------------------- Private methods
    private static void createDB() throws Exception {

        Connection connCore = null;
        Connection connUser = null;
        PreparedStatement stmt = null;

        try {

            dropDBCore();

            connCore = coreDs.getConnection();

            stmt = connCore.prepareStatement(CREATE_FNBL_LAST_SYNC);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connCore.prepareStatement(CREATE_FNBL_PIM_LISTENER_REGISTRY);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connCore.prepareStatement(CREATE_FNBL_PUSH_LISTENER_REGISTRY);
            stmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(connCore, stmt, null);
        }

        try {

            dropDBUser();

            connUser = userRds.getRoutedConnection("user1");

            stmt = connUser.prepareStatement(CREATE_FNBL_PIM_CONTACT);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connUser.prepareStatement(CREATE_FNBL_PIM_CALENDAR);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connUser.prepareStatement(CREATE_FNBL_PIM_NOTE);
            stmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(connUser, stmt, null);
        }
    }

    private static void dropDB() {
        dropDBCore();
        dropDBUser();
    }

    private static void dropDBCore() {
        Connection connCore = null;
        PreparedStatement stmt = null;
        try {

            connCore = coreDs.getConnection();
            stmt = connCore.prepareStatement(DROP_FNBL_LAST_SYNC);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connCore.prepareStatement(DROP_FNBL_PIM_LISTENER_REGISTRY);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connCore.prepareStatement(DROP_FNBL_PUSH_LISTENER_REGISTRY);
            stmt.execute();

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(connCore, stmt, null);
        }
    }

    private static void dropDBUser() {

        Connection connUser = null;
        PreparedStatement stmt = null;
        try {

            connUser = userRds.getRoutedConnection("user1");

            stmt = connUser.prepareStatement(DROP_FNBL_PIM_CONTACT);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connUser.prepareStatement(DROP_FNBL_PIM_CALENDAR);
            stmt.execute();
            DBTools.close(null, stmt, null);

            stmt = connUser.prepareStatement(DROP_FNBL_PIM_NOTE);
            stmt.execute();

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(connUser, stmt, null);
        }
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        coreDs = DataSourceTools.lookupDataSource("jdbc/fnblcore");
        coreConn = new DatabaseConnection(coreDs.getConnection());
        coreDatabaseTester = new DefaultDatabaseTester(coreConn);

        userRds = (RoutingDataSource) DataSourceTools.lookupDataSource("jdbc/fnbluser");
        userConn = new DatabaseConnection(userRds.getRoutedConnection("user1"));
        userDatabaseTester = new DefaultDatabaseTester(userConn);

        createDB();

        coreDataSet = new FlatXmlDataSet(new File(DATASET_CORE));
        DatabaseOperation.CLEAN_INSERT.execute(coreConn, coreDataSet);

        userDataSet = new FlatXmlDataSet(new File(DATASET_USER));
        DatabaseOperation.CLEAN_INSERT.execute(userConn, userDataSet);
    }

    @Override
    protected void tearDown() throws Exception {

        DBTools.close(coreDatabaseTester.getConnection().getConnection(), null, null);
        DBTools.close(userDatabaseTester.getConnection().getConnection(), null, null);

        dropDB();

        super.tearDown();
    }
}
