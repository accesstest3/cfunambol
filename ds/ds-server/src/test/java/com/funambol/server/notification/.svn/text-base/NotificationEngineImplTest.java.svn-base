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

package com.funambol.server.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.TestCase;

import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Source;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.Message.Type;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.db.RoutingDataSource;

/**
 * NotificationEngineImpl's test cases.
 *
 * @version $Id: NotificationEngineImplTest.java,v 1.1 2008-05-22 12:35:15 nichele Exp $
 */
public class NotificationEngineImplTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String DROP_FNBL_PENDING_NOTIFICATION =
        "drop table fnbl_pending_notification";

    private static final String CREATE_FNBL_PENDING_NOTIFICATION =
        "create table fnbl_pending_notification (" +
        "id           bigint       not null," +
        "username     varchar(255) not null," +
        "device       varchar(128) not null," +
        "sync_source  varchar(128) not null," +
        "content_type varchar(128) not null," +
        "sync_type    integer      not null," +
        "ui_mode      integer      not null," +
        "time         bigint       not null," +
        "constraint pk_pending_notification primary key (id))";

    private static final String INSERT_PENDING_NOTIFICATION =
        "INSERT INTO fnbl_pending_notification (id, username, device, " +
        "sync_source, content_type, sync_type, ui_mode, time) " +
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String DROP_FNBL_DEVICE = "drop table fnbl_device";

    private static final String CREATE_FNBL_DEVICE =
        "create table fnbl_device (" +
        "id                   varchar(128) not null," +
        "description          varchar(255)," +
        "type                 varchar(255)," +
        "client_nonce         varchar(255)," +
        "server_nonce         varchar(255)," +
        "server_password      varchar(255)," +
        "timezone             varchar(32) ," +
        "convert_date         char(1)     ," +
        "charset              varchar(16) ," +
        "address              varchar(50) ," +
        "msisdn               varchar(50) ," +
        "notification_builder varchar(255)," +
        "notification_sender  varchar(255)," +
        "id_caps              bigint      ," +
        "sent_server_caps     boolean default false," +
        "constraint pk_device primary key (id)" +
        ");";

    private static final String INSERT_DEVICE =
        "INSERT INTO fnbl_device (id, notification_builder, sent_server_caps) " +
        "VALUES(?, ?, ?)";

    private static final String JNDI_USER_DB = "jdbc/fnbluser";
    private static final String USER_ID1     = "user1"        ;

    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            createFnblPendingNotification();
            createFnblDevice();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private NotificationEngineImpl notificationEngine = null;

    // ------------------------------------------------------------ Constructors
    public NotificationEngineImplTest(String testName) {
        super(testName);

        notificationEngine = new NotificationEngineImpl();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of isANotificableDevice method, of class NotificationEngineImpl.
     */
    public void testIsANotificableDevice() throws Throwable {

        Boolean result   = false;
        String  deviceId = null;

        deviceId = "fol-34ferjfiecpec";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "fjg-frifmemwmcrf";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "fip-dio3jdioejfioem";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "fbb-dwdeeferfedefrg";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "fmz-9876543ofjdkgtredweef";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "sc-9876543ofjdkgtredweef";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "sc-9876543ofjdkgtredweef";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "fwm-4rf44o094io4fmo";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertTrue(result);

        deviceId = "fjm-343rijifoiejgif";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertTrue(result);

        deviceId = "fjm-bb-ed3r934j40r";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertTrue(result);

        deviceId = "IMEI:5465545665345653";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);

        deviceId = "2345645765433343";
        result = (Boolean) PrivateAccessor.invoke(
                     notificationEngine,
                     "isANotificableDevice",
                     new Class[] {String.class},
                     new Object[] {(deviceId)});
        assertFalse(result);
    }

    /**
     * Tests the getMessageFromPendingNotifications method, of class
     * NotificationEngineImpl.
     */
    public void testGetMessageFromPendingNotifications() throws Throwable {

        byte[] expectedContent = new byte[] {
            (byte)0xCA, (byte)0x76, (byte)0x72, (byte)0xD2, (byte)0x20, (byte)0xE2,
            (byte)0x16, (byte)0x1E, (byte)0x40, (byte)0x6E, (byte)0x22, (byte)0x92,
            (byte)0xB0, (byte)0x95, (byte)0x88, (byte)0xF7, (byte)0x03, (byte)0x18,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08,
            (byte)0x66, (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
            (byte)0x6F, (byte)0x6C, (byte)0x30, (byte)0x60, (byte)0x00, (byte)0x03,
            (byte)0x06, (byte)0x04, (byte)0x6D, (byte)0x61, (byte)0x69, (byte)0x6C,
            (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x06, (byte)0x03, (byte)0x63,
            (byte)0x61, (byte)0x6C, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x00,
            (byte)0x04, (byte)0x63, (byte)0x61, (byte)0x72, (byte)0x64
        };

        Message expectedMsg =
            new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, expectedContent);
        expectedMsg.setSyncSources(new String[]{"mail", "cal", "card"});

        Message msg = (Message)PrivateAccessor.invoke(
            notificationEngine,
            "getMessageFromPendingNotifications",
            new Class[]{String.class, String.class},
            new Object[]{"user1", "fwm-1234567890"}
        );

        assertEquals(expectedMsg.getType(), msg.getType());
        ArrayAssert.assertEquals("Wrong content", expectedContent, msg.getMessageContent());

        //[mail, cal, card]
        assertTrue("Wrong number of SyncSources", msg.getSyncSources().length == 3);
        ArrayAssert.assertEquivalenceArrays("Wrong list of SyncSources", expectedMsg.getSyncSources(), msg.getSyncSources());

    }

    /**
     * Tests the fixAlertsUsingPendingNotification method, of class
     * NotificationEngineImpl.
     */
    public void testFixAlertsUsingPendingNotification() throws Throwable {

        Alert[] alerts = new Alert[2];

        Source source = new Source("mail");
        Meta meta = new Meta();
        meta.setType("application/vnd.omads-email+xml");
        Item item = new Item(source, null, meta, null, false);
        alerts[0] = new Alert(new CmdID(0), false, null, 206, new Item[]{item});

        source = new Source("cal");
        meta = new Meta();
        meta.setType("text/x-vcalendar");
        item = new Item(source, null, meta, null, false);
        alerts[1] = new Alert(new CmdID(0), false, null, 206, new Item[]{item});

        List<PendingNotification> pendingNotifications =
            getDummyPendingNotificationsForUser2();

        Alert[] mergedAlerts =
            (Alert[])PrivateAccessor.invoke(notificationEngine,
                "fixAlertsUsingPendingNotifications",
                new Class[]{Alert[].class, List.class},
                new Object[]{alerts, pendingNotifications}
            );

        assertTrue("Wrong number of Alert", mergedAlerts.length == 3);
    }

    public void testGetSourceURIsList() throws Throwable {

        Alert[] alerts = new Alert[2];

        Source source = new Source("mail");
        Meta meta = new Meta();
        meta.setType("application/vnd.omads-email+xml");
        Item item = new Item(source, null, meta, null, false);
        alerts[0] = new Alert(new CmdID(0), false, null, 206, new Item[]{item});

        source = new Source("cal");
        meta = new Meta();
        meta.setType("text/x-vcalendar");
        item = new Item(source, null, meta, null, false);
        alerts[1] = new Alert(new CmdID(0), false, null, 206, new Item[]{item});

        String[] expectedSyncSources = new String[2];
        expectedSyncSources[0] = "mail";
        expectedSyncSources[1] = "cal";

        String[] syncSources =
            (String[])PrivateAccessor.invoke(notificationEngine,
            "getSourceURIsList",
            new Class[]{Alert[].class},
            new Object[]{alerts}
        );
        
        assertTrue("Wrong number of SyncSources", syncSources.length == 2);
        ArrayAssert.assertEquivalenceArrays("Wrong list of SyncSources", expectedSyncSources, syncSources);
    }


    // --------------------------------------------------------- Private methods
    private static void createFnblDevice() throws Exception {
        DataSource ds  = DataSourceTools.lookupDataSource("jdbc/fnblcore");
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(DROP_FNBL_DEVICE);
            stmt.execute();

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(CREATE_FNBL_DEVICE);
            stmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        fillDeviceTable();
    }

    private static void fillDeviceTable() throws Exception {

        DataSource ds  = DataSourceTools.lookupDataSource("jdbc/fnblcore");

        Connection        conn = null;
        PreparedStatement ps   = null;

        List<Sync4jDevice> devices = getDummyDevices();

        try {
            conn = ds.getConnection();

            for (Sync4jDevice device: devices) {

                ps = conn.prepareStatement(INSERT_DEVICE);
                ps.setString(1, device.getDeviceId());
                ps.setString(2, "com/funambol/server/notification/DSNotificationBuilder.xml");
                ps.setBoolean(3, true);
                ps.executeUpdate();

                DBTools.close(null, ps, null);
            }

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(conn, ps, null);
        }
    }

    private static void createFnblPendingNotification() throws Exception {

        RoutingDataSource ds =
            (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getRoutedConnection(USER_ID1);

            stmt = conn.prepareStatement(DROP_FNBL_PENDING_NOTIFICATION);
            stmt.execute();
        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getRoutedConnection(USER_ID1);

            stmt = conn.prepareStatement(CREATE_FNBL_PENDING_NOTIFICATION);
            stmt.execute();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        fillPendingNotificationTable();
    }

    private static void fillPendingNotificationTable() throws Exception {
        RoutingDataSource ds =
            (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);

        Connection        con = null;
        PreparedStatement ps  = null;

        List<PendingNotification> pendingNotifications =
            getDummyPendingNotificationsForUser1();

        pendingNotifications.addAll(getDummyPendingNotificationsForUser2());

        try {
            con = ds.getRoutedConnection(USER_ID1);

            for (PendingNotification pendingNotification: pendingNotifications) {

                ps = con.prepareStatement(INSERT_PENDING_NOTIFICATION);

                ps.setLong  (1, pendingNotification.getId());
                ps.setString(2, pendingNotification.getUserId());
                ps.setString(3, pendingNotification.getDeviceId());
                ps.setString(4, pendingNotification.getSyncSource());
                ps.setString(5, pendingNotification.getContentType());
                ps.setInt   (6, pendingNotification.getSyncType());
                ps.setInt   (7, pendingNotification.getUimode());
                ps.setLong  (8, pendingNotification.getDeliveryTime());

                ps.executeUpdate();

                DBTools.close(null, ps, null);
            }

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    private static List<PendingNotification> getDummyPendingNotificationsForUser1() {
        List<PendingNotification> pendingNotifications =
            new ArrayList<PendingNotification>();

        PendingNotification pendingNotification = new PendingNotification();
        pendingNotification.setId(100);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setUimode(1);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(101);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("cal");
        pendingNotification.setContentType("text/x-vcalendar");
        pendingNotification.setUimode(0);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(102);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("card");
        pendingNotification.setContentType("text/vcard");
        pendingNotification.setUimode(0);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(103);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("IMEI:1111444455");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setUimode(0);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        return pendingNotifications;
    }

    private static List<PendingNotification> getDummyPendingNotificationsForUser2() {
        List<PendingNotification> pendingNotifications =
            new ArrayList<PendingNotification>();

        PendingNotification pendingNotification = new PendingNotification();
        pendingNotification.setId(110);
        pendingNotification.setUserId("user2");
        pendingNotification.setDeviceId("fwm-111222333444");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setUimode(0);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(111);
        pendingNotification.setUserId("user2");
        pendingNotification.setDeviceId("fwm-111222333444");
        pendingNotification.setSyncSource("card");
        pendingNotification.setContentType("text/vcard");
        pendingNotification.setUimode(1);
        pendingNotification.setSyncType(206);
        pendingNotification.setDeliveryTime(System.currentTimeMillis());
        pendingNotifications.add(pendingNotification);

        return pendingNotifications;
    }

    private static List<Sync4jDevice> getDummyDevices() {
        List<Sync4jDevice> devices = new ArrayList<Sync4jDevice>();
        Sync4jDevice device = new Sync4jDevice("fwm-1234567890");
        device.setNotificationBuilder("com/funambol/server/notification/DSNotificationBuilder.xml");
        devices.add(device);

        device = new Sync4jDevice("IMEI:1111444455");
        device.setNotificationBuilder("com/funambol/server/notification/DSNotificationBuilder.xml");
        devices.add(device);

        device = new Sync4jDevice("fwm-111222333444");
        device.setNotificationBuilder("com/funambol/server/notification/DS11NotificationBuilder.xml");
        devices.add(device);

        return devices;
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("funambol.home", "src/test/data");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
