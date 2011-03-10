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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import com.funambol.server.db.RoutingDataSource;

/**
 * This class implements methods to access push history data in the data store.
 *
 * @version $Id$
 */
public class PendingNotificationDAO {

    // --------------------------------------------------------------- Constants
    private static final String JNDI_NAME_CORE_DATASOURCE     = "jdbc/fnblcore";
    private static final String JNDI_NAME_USER_DATASOURCE     = "jdbc/fnbluser";
    private static final String PENDING_NOTIFICATION_ID_SPACE =
        "pending.notification.id";

    private static final String SQL_INSERT =
        "INSERT INTO fnbl_pending_notification (id, username, device, " +
        "sync_source, content_type, sync_type, ui_mode, time) " +
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
        "UPDATE fnbl_pending_notification SET sync_type=?, ui_mode=?, time=? " +
        "WHERE username=? AND device=? AND sync_source=?";

    private static final String SQL_DELETE =
        "DELETE FROM fnbl_pending_notification WHERE username=? AND device=?";

    private static final String SQL_GET_BY_ID = "SELECT id, username, device, " +
        "sync_source, content_type, sync_type, ui_mode, time " +
        "FROM fnbl_pending_notification WHERE id=?";

    private static final String SQL_GET_BY_USERNAME_DEVICE =
        "SELECT id, username, device, sync_source, content_type, sync_type, " +
        "ui_mode, time FROM fnbl_pending_notification " +
        "WHERE username=? AND device=?";

    // ------------------------------------------------------------ Private data

    protected static final FunambolLogger log =
        FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);

    protected DataSource        coreDataSource = null;
    protected RoutingDataSource userDataSource = null;

    protected DBIDGenerator dbIDGenerator;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of PendingNotificationDAO, ready to be linked
     * to a given DataSource.
     *
     * @throws IllegalArgumentException if there are errors looking up the
     *         dataSource with the given jndiDataSourceName
     */
    public PendingNotificationDAO() {
        try {
            this.coreDataSource =
                (DataSource)DataSourceTools.lookupDataSource(JNDI_NAME_CORE_DATASOURCE);
        } catch (NamingException ex) {
            throw new IllegalArgumentException("Error looking up datasource: " +
                                               JNDI_NAME_CORE_DATASOURCE, ex);
        }

        try {
            this.userDataSource =
                (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_NAME_USER_DATASOURCE);
        } catch (NamingException ex) {
            throw new IllegalArgumentException("Error looking up datasource: " +
                                               JNDI_NAME_USER_DATASOURCE, ex);
        }
        this.dbIDGenerator =
            DBIDGeneratorFactory.getDBIDGenerator(PENDING_NOTIFICATION_ID_SPACE,
                                                  coreDataSource);
    }

    //----------------------------------------------------------- Public methods

    /**
     * Adds or updates a not delivered push notification. If necessary, a new ID
     * is generated.
     *
     * @param pendingNotification the not delivered push notification
     * @throws PendingNotificationDBAccessException if an error occurs
     */
    public void add(PendingNotification pendingNotification)
    throws PendingNotificationDBAccessException {
        if (log.isTraceEnabled()) {
            log.trace("Adding not delivered push notification");
        }

        Connection        con = null;
        PreparedStatement ps  = null;
        int n = 0;

        String username   = pendingNotification.getUserId();
        String deviceId   = pendingNotification.getDeviceId();
        String syncSource = pendingNotification.getSyncSource();

        try {

            // Looks up the data source when the first connection is created
            con = userDataSource.getRoutedConnection(username);

            // before trying to update the record: if it not exist, insert it
            ps = con.prepareStatement(SQL_UPDATE);
            ps.setInt   (1, pendingNotification.getSyncType());
            ps.setInt   (2, pendingNotification.getUimode());
            ps.setLong  (3, pendingNotification.getDeliveryTime());
            ps.setString(4, username);
            ps.setString(5, deviceId);
            ps.setString(6, syncSource);

            n = ps.executeUpdate();

            if (n == 0) {
                // insert the record for the first time
                ps = con.prepareStatement(SQL_INSERT);
                ps.setLong  (1, dbIDGenerator.next());
                ps.setString(2, username);
                ps.setString(3, deviceId);
                ps.setString(4, syncSource);
                ps.setString(5, pendingNotification.getContentType());
                ps.setInt   (6, pendingNotification.getSyncType());
                ps.setInt   (7, pendingNotification.getUimode());
                ps.setLong  (8, pendingNotification.getDeliveryTime());

                ps.executeUpdate();
            }

        } catch (Exception e) {
            throw new PendingNotificationDBAccessException(
                "Error storing push notification for username '" + username +
                "', deviceId '" + deviceId + "' and sync source '" +
                syncSource + "'", e);
        } finally {
            DBTools.close(con, ps, null);
        }

        if(log.isTraceEnabled()) {
            log.trace("Added push notification for username '" +
                      username + "', device '" + deviceId +
                      "' and sync source '" + syncSource + "'");
        }
    }

    /**
     * Deletes the push notification that have already been delivered for the
     * given username, device identifier and the array of SyncSources.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws PendingNotificationDBAccessException if an error occurs
     */
    public void deleteBySyncSources(String   username   ,
                                    String   deviceId   ,
                                    String[] syncSources)
    throws PendingNotificationDBAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Deleting the push notifications that have already been delivered..");
        }

        Connection        con  = null;
        PreparedStatement stmt = null;

        try {
            // Looks up the data source when the first connection is created
            con = userDataSource.getRoutedConnection(username);

            StringBuilder query = new StringBuilder(SQL_DELETE);
            if (syncSources != null && syncSources.length > 0) {
                int size = syncSources.length;
                for (int i=0; i<size; i++) {
                    if (i == 0) {
                        query.append(" AND sync_source IN ('").append(syncSources[i]).append("'");
                    } else {
                        query.append(", '").append(syncSources[i]).append("'");
                    }
                }
                query.append(") ");
            }

            stmt = con.prepareStatement(query.toString());
            stmt.setString(1, username);
            stmt.setString(2, deviceId);

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new PendingNotificationDBAccessException(
                "Error deleting push notification", e);
        } finally {
            DBTools.close(con, stmt, null);
        }
    }

    /**
     * Gets the not delivered push notification with given id.
     *
     * @param id the unique identifier of the not delivered push notification
     * @param username the username
     * @return the PendingNotification object that represents the not delivered
     *         push notification with the given id
     * @throws NotFoundException if pending notification is not found
     * @throws PendingNotificationDBAccessException if an error occurs
     */
    public PendingNotification getById(long id, String username)
    throws NotFoundException, PendingNotificationDBAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Getting push notification with id '" + id + "'");
        }

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        PendingNotification pendingNotification = null;

        try {
            // Looks up the data source when the first connection is created
            con = userDataSource.getRoutedConnection(username);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_BY_ID);
            ps.setLong(1, id);

            rs = ps.executeQuery();

            if (!rs.next()) {
                throw new NotFoundException("No push notification found.");
            }

            pendingNotification =
                new PendingNotification(id,
                                        rs.getString(2),
                                        rs.getString(3),
                                        rs.getString(4),
                                        rs.getString(5),
                                        rs.getInt   (6),
                                        rs.getInt   (7),
                                        rs.getLong  (8));

        } catch (NotFoundException e ) {
            throw e;
        } catch (Exception e) {
            throw new PendingNotificationDBAccessException(
                "Error getting push notification with id '" + id + "'", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return pendingNotification;
    }

    /**
     * Retrieves the not delivered push notifications for the given username
     * and device.
     *
     * @param username the user identifier
     * @param deviceId the device identifier
     * @return the array of not delivered push notifications for the given
     *         username and deviceId
     * @throws PendingNotificationDBAccessException if an error occurs
     */
    public List<PendingNotification> getByUsernameDevice(String username,
                                                         String deviceId)
    throws PendingNotificationDBAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Getting push notifications for username '" + username
                    + "' and device '" + deviceId + "'");
        }

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<PendingNotification> pendingNotificationList =
            new ArrayList<PendingNotification>();

        try {
            // Looks up the data source when the first connection is created
            con = userDataSource.getRoutedConnection(username);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_BY_USERNAME_DEVICE);
            ps.setString(1, username);
            ps.setString(2, deviceId);

            rs = ps.executeQuery();

            while(rs.next()) {
                pendingNotificationList.add(new PendingNotification(
                    rs.getInt   (1),
                    rs.getString(2),
                    rs.getString(3),
                    rs.getString(4),
                    rs.getString(5),
                    rs.getInt   (6),
                    rs.getInt   (7),
                    rs.getLong  (8)
                    ));
            }

        } catch (Exception e) {
            throw new PendingNotificationDBAccessException(
                "Error getting push notifications for username '" + username +
                "' and device '" + deviceId + "'", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return pendingNotificationList;
    }
}
