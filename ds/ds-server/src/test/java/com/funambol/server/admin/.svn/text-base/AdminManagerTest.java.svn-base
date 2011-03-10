/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.server.admin;

import com.funambol.framework.core.Alert;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.notification.PushManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.sql.DataSource;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 *
 * @author gibi
 */
public class AdminManagerTest extends TestCase {

    private static final String JNDI_NAME_CORE_DATASOURCE     = "jdbc/fnblcore";
    protected static DataSource        coreDataSource = null;

    static {
        try {
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            coreDataSource = (DataSource)DataSourceTools.lookupDataSource(JNDI_NAME_CORE_DATASOURCE);

            setUpFakeCoreDatabase();
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }



    public AdminManagerTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of sendNotificationMessage method, of class AdminManager.
     */
    public void testSendNotificationMessage_4args() throws Exception {

        System.out.println("sendNotificationMessage");

        String  username = "";
        String  deviceId = "";
        Alert[] alerts   = null;
        int     uimode   = 0;
        
        AdminManager adminManager = new AdminManager();

        try {

            // NOTE:
            // the Configuration constructor configures the ps
            Object ps = PrivateAccessor.getField(adminManager, "ps");
            if (ps == null){
               fail("ERROR PersistentStore is null!");
            }

            // NOTE:
            // the Configuration.getUserManager needs to look up the fnbl/core
            Object userManager = PrivateAccessor.getField(adminManager, "userManager");
            if (userManager == null){
               fail("ERROR userManager is null!");
            }

            // NOTE:
            // the Configuration.getDeviceInventory needs to look up the fnbl/core
            Object deviceInventory = PrivateAccessor.getField(adminManager, "deviceInventory");
            if (deviceInventory == null){
               fail("ERROR deviceInventory is null!");
            }

            // NOTE:
            // the pushManager inizialize needs the DB
            Object pushManager = PrivateAccessor.getField(adminManager, "pushManager");
            if (pushManager == null){
               fail("ERROR pushManager is null!");
            } else {
               int value = ((PushManager)pushManager).getCoreThreadPoolSize();
               assertEquals(PushManager.DEFAULT_CORE_POOL_SIZE, value);
            }

        } catch (Throwable t){
             fail("ERROR " + t.toString());
        }
    }


    //---------------------------------------------------------- Private Methods


    /**
     * create a fake table for a fake DB
     *
     * @throws java.lang.Exception
     */
    private static void setUpFakeCoreDatabase() throws Exception {


        String CREATE_CUSTOM_USER_TABLE =
                "create table custom_user (" +
                "   username      varchar(255) not null," +
                "   password      varchar(255) not null," +
                "   sync_begin    bigint," +
                "   last_op_time  bigint," +
                "   constraint pk_custom_user primary key (username)" +
                " );";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {

            conn = coreDataSource.getConnection();

            stmt = conn.prepareStatement(CREATE_CUSTOM_USER_TABLE);
            stmt.execute();
            DBTools.close(null, stmt, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

    }


}
