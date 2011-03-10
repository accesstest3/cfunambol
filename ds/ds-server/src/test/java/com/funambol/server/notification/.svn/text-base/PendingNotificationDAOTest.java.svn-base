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

package com.funambol.server.notification;

import junit.framework.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;

import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.db.RoutingDataSource;

/**
 * PendingNotificationDAO's test cases.
 *
 * @version $Id$
 */
public class PendingNotificationDAOTest extends TestCase {

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
        "insert into fnbl_pending_notification (id, username, device, " +
        "sync_source, content_type, sync_type, ui_mode, time) " +
        "values(?, ?, ?, ?, ?, ?, ?, ?)";

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
        "insert into fnbl_device (id, notification_builder, sent_server_caps) values(?, ?, ?)";

    private static final String DROP_FNBL_ID = "drop table fnbl_id";

    private static final String CREATE_FNBL_ID = "create table fnbl_id (" +
        "idspace      varchar(30) not null," +
        "counter      bigint      not null," +
        "increment_by int         default 100," +
        "constraint pk_id primary key (idspace));";

    private static final String INSERT_ID =
        "insert into fnbl_id values('pending.notification.id', 0, 100)";

    private static final String JNDI_USER_DB = "jdbc/fnbluser";
    private static final String USER_ID1     = "user1"        ;

    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            createFnblPendingNotification();
            createFnblDevice();
            createFnblId();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Constructors
    public PendingNotificationDAOTest(String testName) {
        super(testName);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Test of add method, of class PendingNotificationDAO.
     */
    public void testAdd() throws Exception {

        PendingNotification pendingNotification =
            new PendingNotification(0, "user1", "fwm-6789678900", "mail", "application/vnd.omads-email+xml", 206, 1, 1226317537593L);
        PendingNotification[] expResult = new PendingNotification[] {
            pendingNotification
        };

        PendingNotificationDAO instance = new PendingNotificationDAO();
        instance.add(pendingNotification);

        List<PendingNotification> pn =
            instance.getByUsernameDevice("user1", "fwm-6789678900");

        PendingNotification[] result =
            (PendingNotification[])pn.toArray(new PendingNotification[pn.size()]);

        // only one pending notification must be found
        comparePendingNotifications(expResult, result);
    }

    /**
     * Test of deleteBySyncSources method, of class PendingNotificationDAO.
     */
    public void testDeleteBySyncSources() throws Exception {

        PendingNotificationDAO instance = new PendingNotificationDAO();

        String[] syncSources = new String[2];
        syncSources[0] = "mail";
        syncSources[1] = "card";

        PendingNotification pendingNotification =
            new PendingNotification(0, "user2", "fwm-111222333444", "note", "text/plain", 206, 1, 1226317537593L);
        PendingNotification[] expResult = new PendingNotification[] {
            pendingNotification
        };

        instance.deleteBySyncSources("user2", "fwm-111222333444", syncSources);

        List<PendingNotification> pn =
            instance.getByUsernameDevice("user2", "fwm-111222333444");

        PendingNotification[] result =
            (PendingNotification[])pn.toArray(new PendingNotification[pn.size()]);

        // only one pending notification must be found for syncSource 'note'
        comparePendingNotifications(expResult, result);
    }

    /**
     * Test of getById method, of class PendingNotificationDAO.
     */
    public void testGetById() throws Exception {

        PendingNotificationDAO instance = new PendingNotificationDAO();

        PendingNotification expResult =
            new PendingNotification(100, "user1", "fwm-1234567890", "mail", "application/vnd.omads-email+xml", 206, 1, 1226317537593L);

        PendingNotification result = instance.getById(100, "user1");
        comparePendingNotification(expResult, result);

        try {
            result = instance.getById(800, "user1");
            fail("Pending notification with id 800 found");
        } catch(NotFoundException e) {
            return;
        }
    }

    /**
     * Test of getByUsernameDevice method, of class PendingNotificationDAO.
     */
    public void testGetByUsernameDevice() throws Exception {

        PendingNotificationDAO instance = new PendingNotificationDAO();

        PendingNotification[] expResult = new PendingNotification[]{
            new PendingNotification(100, "user1", "fwm-1234567890", "mail", "application/vnd.omads-email+xml", 206, 1, 1226317537593L),
            new PendingNotification(101, "user1", "fwm-1234567890", "cal", "text/x-vcalendar", 206, 0, 1226317537593L),
            new PendingNotification(102, "user1", "fwm-1234567890", "card", "text/vcard", 206, 0, 1226317537593L),
        };

        List<PendingNotification> pn =
            instance.getByUsernameDevice("user1", "fwm-1234567890");

        PendingNotification[] result =
            (PendingNotification[])pn.toArray(new PendingNotification[pn.size()]);

        comparePendingNotifications(expResult, result);
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
            getDummyPendingNotifications();

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

    private static List<PendingNotification> getDummyPendingNotifications() {
        List<PendingNotification> pendingNotifications =
            new ArrayList<PendingNotification>();

        PendingNotification pendingNotification = new PendingNotification();
        pendingNotification.setId(100);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(1);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(101);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("cal");
        pendingNotification.setContentType("text/x-vcalendar");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(0);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(102);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("fwm-1234567890");
        pendingNotification.setSyncSource("card");
        pendingNotification.setContentType("text/vcard");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(0);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(103);
        pendingNotification.setUserId("user1");
        pendingNotification.setDeviceId("IMEI:1111444455");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(0);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(110);
        pendingNotification.setUserId("user2");
        pendingNotification.setDeviceId("fwm-111222333444");
        pendingNotification.setSyncSource("mail");
        pendingNotification.setContentType("application/vnd.omads-email+xml");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(0);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(111);
        pendingNotification.setUserId("user2");
        pendingNotification.setDeviceId("fwm-111222333444");
        pendingNotification.setSyncSource("card");
        pendingNotification.setContentType("text/vcard");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(1);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        pendingNotification = new PendingNotification();
        pendingNotification.setId(112);
        pendingNotification.setUserId("user2");
        pendingNotification.setDeviceId("fwm-111222333444");
        pendingNotification.setSyncSource("note");
        pendingNotification.setContentType("text/plain");
        pendingNotification.setSyncType(206);
        pendingNotification.setUimode(1);
        pendingNotification.setDeliveryTime(1226317537593L);
        pendingNotifications.add(pendingNotification);

        return pendingNotifications;
    }

    private static void createFnblId() throws Exception {
        DataSource ds  = DataSourceTools.lookupDataSource("jdbc/fnblcore");
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(DROP_FNBL_ID);
            stmt.execute();

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(CREATE_FNBL_ID);
            stmt.execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(INSERT_ID);
            stmt.executeUpdate();

        } catch (Exception e) {
            // do nothing
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    private void comparePendingNotifications(PendingNotification[] expResult,
                                             PendingNotification[] result) {

        assertTrue("Wrong count pending notifications", expResult.length == result.length);
        for (int i=0; i<expResult.length; i++) {
            comparePendingNotification(expResult[i], result[i]);
        }
    }

    private void comparePendingNotification(PendingNotification expResult,
                                            PendingNotification result   ) {

        assertEquals("Wrong user identifier", expResult.getUserId(), result.getUserId());
        assertEquals("Wrong device identifier", expResult.getDeviceId(), result.getDeviceId());
        assertEquals("Wrong sync source", expResult.getSyncSource(), result.getSyncSource());
        assertEquals("Wrong content type", expResult.getContentType(), result.getContentType());
        assertEquals("Wrong sync type", expResult.getSyncType(), result.getSyncType());
        assertEquals("Wrong uimode", expResult.getUimode(), result.getUimode());
        assertEquals("Wrong delivery time", expResult.getDeliveryTime(), result.getDeliveryTime());
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("funambol.home", "src/test/data");
    }

    protected void tearDown() throws Exception {
    }
}
