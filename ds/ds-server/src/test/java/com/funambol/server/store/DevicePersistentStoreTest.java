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

package com.funambol.server.store;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import junit.framework.TestCase;

import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.ConvertDatePolicy;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.server.db.DataSourceContextHelper;

/**
 * Test cases for the DevicePersistentStore
 * @version $Id: DevicePersistentStoreTest.java,v 1.1 2008-05-22 12:35:15 nichele Exp $
 */
public class DevicePersistentStoreTest extends TestCase {

    // --------------------------------------------------------------- Constants

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

    private static final String[] SQL = new String[] {
        "select id, description, type, client_nonce, server_nonce, server_password, timezone, convert_date, charset, id_caps, address, msisdn, notification_builder, notification_sender, sent_server_caps from fnbl_device",
        "select sd.description, sd.type, sd.client_nonce, sd.server_nonce, sd.server_password, sd.timezone, sd.convert_date, sd.charset, sd.id_caps, sd.address, sd.msisdn, sd.notification_builder, sd.notification_sender, sd.sent_server_caps from fnbl_device sd  where sd.id=?",
        "insert into fnbl_device (id, description, type, client_nonce, server_nonce, server_password, id_caps, timezone, convert_date, charset, address, msisdn, notification_builder, notification_sender, sent_server_caps) values(?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?)",
        "update fnbl_device set description=?, type=?, client_nonce=?, server_nonce=?, server_password=?, timezone=?, convert_date=?, charset=?, id_caps=?, address=?, msisdn=?, notification_builder=?, notification_sender=? where id=?",
        "delete from fnbl_device where id=?",
        "select count(*) as devices from fnbl_device",
        "update fnbl_device set id_caps=? where id=?",
        "update fnbl_device set sent_server_caps=? where id=?"
    };
    
    static {
        try {
            System.setProperty("java.naming.factory.initial",
                               "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            createFnblDevice();            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // ------------------------------------------------------------ Private data
    private DevicePersistentStore devicePersistentStore = null;

    // ------------------------------------------------------------ Constructors

    public DevicePersistentStoreTest(String testName) throws Exception {
        super(testName);

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        devicePersistentStore = new DevicePersistentStore();
        devicePersistentStore.init();
        devicePersistentStore.setSql(SQL);        
    }

    
    // -------------------------------------------------------------- Test cases


    public void testStoreDevice_1() throws Exception {

        Sync4jDevice d = new Sync4jDevice("test_id");
        devicePersistentStore.delete(d);

        Sync4jDevice d1 = new Sync4jDevice("test_id",
                "descr",
                "type",
                new byte[] {01, 02},
                new byte[] {03, 04},
                "pwd",
                "Europe/Rome",
                ConvertDatePolicy.CONVERT_DATE,
                "UTF-8"
                );

        devicePersistentStore.store(d1);

        Sync4jDevice d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

        // Re-storing the same device in order to test the update
        devicePersistentStore.store(d1);
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);
    }

    public void testStoreDevice_2() throws Exception {

        Sync4jDevice d = new Sync4jDevice("test_id");
        devicePersistentStore.delete(d);

        Sync4jDevice d1 = new Sync4jDevice("test_id",
                "descr",
                "type",
                new byte[] {01, 02},
                new byte[] {03, 04},
                "pwd",
                null,
                ConvertDatePolicy.NO_CONVERT_DATE,
                "UTF-8"
                );

        devicePersistentStore.store(d1);

        Sync4jDevice d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

        // Re-storing the same device in order to test the update
        devicePersistentStore.store(d1);
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

    }

    public void testStoreDevice_3() throws Exception {

        Sync4jDevice d = new Sync4jDevice("test_id");
        devicePersistentStore.delete(d);

        Sync4jDevice d1 = new Sync4jDevice("test_id",
                "descr",
                "type",
                new byte[] {01, 02},
                new byte[] {03, 04},
                "pwd",
                "Europe/Rome",
                ConvertDatePolicy.UNSPECIFIED,
                "UTF-8"
                );

        devicePersistentStore.store(d1);

        Sync4jDevice d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

        // Re-storing the same device in order to test the update
        devicePersistentStore.store(d1);
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

    }

    public void testStoreDevice_4() throws Exception {

        Sync4jDevice d = new Sync4jDevice("test_id");
        devicePersistentStore.delete(d);

        Sync4jDevice d1 = new Sync4jDevice("test_id",
                "descr",
                "type",
                new byte[] {01, 02},
                new byte[] {03, 04},
                "pwd",
                "Europe/Rome",
                ConvertDatePolicy.CONVERT_DATE,
                "UTF-8"
                );

        devicePersistentStore.store(d1);

        Sync4jDevice d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

        // Re-storing the same device in order to test the update
        devicePersistentStore.store(d1);
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

    }

    public void testGetDBValueForConvertDate() throws Throwable {
        String result = (String)PrivateAccessor.invoke(
                            devicePersistentStore,
                            "getDBValueForConvertDatePolicy",
                            new Class[] {short.class},
                            new Object[] {(ConvertDatePolicy.CONVERT_DATE)}
        );

        assertEquals("Y", result);

        result = (String)PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getDBValueForConvertDatePolicy",
                     new Class[] {short.class},
                     new Object[] {ConvertDatePolicy.NO_CONVERT_DATE}
        );

        assertEquals("N", result);

        result = (String)PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getDBValueForConvertDatePolicy",
                     new Class[] {short.class},
                     new Object[] {(ConvertDatePolicy.UNSPECIFIED)}
        );

        assertEquals("", result);

        result = (String)PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getDBValueForConvertDatePolicy",
                     new Class[] {short.class},
                     new Object[] {((short)-1)}
        );

        assertEquals("", result);

        result = (String)PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getDBValueForConvertDatePolicy",
                     new Class[] {short.class},
                     new Object[] {((short)100)}
        );

        assertEquals("", result);
    }

    public void testGetConvertDateFromDBValue() throws Throwable {
        Object result = PrivateAccessor.invoke(
                           devicePersistentStore,
                           "getConvertDatePolicyFromDBValue",
                           new Class[] {String.class},
                           new Object[] {"Y"}
        );
        assertEquals(new Short(ConvertDatePolicy.CONVERT_DATE), result);

        result = PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getConvertDatePolicyFromDBValue",
                     new Class[] {String.class},
                     new Object[] {"N"}
        );

        assertEquals(new Short(ConvertDatePolicy.NO_CONVERT_DATE), result);

        result = PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getConvertDatePolicyFromDBValue",
                     new Class[] {String.class},
                     new Object[] {""}
        );

        assertEquals(new Short(ConvertDatePolicy.UNSPECIFIED), result);

        result = PrivateAccessor.invoke(
                     devicePersistentStore,
                     "getConvertDatePolicyFromDBValue",
                     new Class[] {String.class},
                     new Object[] {"C"}  // unexpected value
        );

        assertEquals(new Short(ConvertDatePolicy.UNSPECIFIED), result);
    }

    public void testStoreDevice_SentServerCaps() throws Exception {

        Sync4jDevice d = new Sync4jDevice("test_id");
        devicePersistentStore.delete(d);

        Sync4jDevice d1 = new Sync4jDevice("test_id",
                "descr",
                "type",
                new byte[] {01, 02},
                new byte[] {03, 04},
                "pwd",
                "Europe/Rome",
                ConvertDatePolicy.CONVERT_DATE,
                "UTF-8",
                false);
        devicePersistentStore.store(d1);

        Sync4jDevice d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);
        d2.setCapabilities(new Capabilities());

        compareDevice(d1, d2);

        devicePersistentStore.store("test_id", true);

        d2 = new Sync4jDevice("test_id");
        devicePersistentStore.read(d2);

        assertTrue(d2.isSentServerCaps());
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
             DBTools.close(null, stmt, null);

         } catch (Exception e) {

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
     }

     private void compareDevice(Sync4jDevice device1, Sync4jDevice device2) {

        assertEquals(device1.getDeviceId(), device2.getDeviceId());
        assertEquals(device1.getDescription(), device2.getDescription());
        assertEquals(device1.getType(), device2.getType());
        ArrayAssert.assertEquals(device1.getClientNonce(), device2.getClientNonce());
        ArrayAssert.assertEquals(device1.getServerNonce(), device2.getServerNonce());
        assertEquals(device1.getServerPassword(), device2.getServerPassword());
        assertEquals(device1.getTimeZone(), device2.getTimeZone());
        assertEquals(device1.getConvertDatePolicy(), device2.getConvertDatePolicy());
        assertEquals(device1.getCharset(), device2.getCharset());
        assertEquals(device1.getAddress(), device2.getAddress());
        assertEquals(device1.getMsisdn(), device2.getMsisdn());
        assertEquals(device1.getNotificationBuilder(), device2.getNotificationBuilder());
        assertEquals(device1.getNotificationSender(), device2.getNotificationSender());
        assertEquals(device1.isSentServerCaps(), device2.isSentServerCaps());

        if (device1.getCapabilities() == null) {
            if (device2.getCapabilities() != null) {
                fail();
            } else {
                return ;
            }
        }

        if (device1.getCapabilities() != null) {
            if (device2.getCapabilities() == null) {
                fail();
            }
        }
        assertEquals(device1.getCapabilities().getId(),
                     device2.getCapabilities().getId());
     }

}
