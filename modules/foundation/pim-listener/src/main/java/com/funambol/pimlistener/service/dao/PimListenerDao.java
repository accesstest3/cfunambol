/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.pimlistener.service.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.tools.DBTools;

import com.funambol.framework.tools.DataSourceTools;
import com.funambol.pimlistener.service.PimListenerException;

import com.funambol.server.db.RoutingDataSource;

/**
 * This class implements database access method to be used by the pim listener.
 * Note that it implements the methods to handle pim data not to handle the pim
 * registry entries (those are handled by
 * com.funambol.pimlistener.registry.dao.PimRegistryEntryDao).
 *
 * @version $Id: PimListenerDao.java,v 1.10 2008-05-18 16:03:06 nichele Exp $
 */
public class PimListenerDao {

    // ----------------------------------------------------------------- Queries
    private static final String SQL_READ_PRINCIPALS =
        "select id, username, device from fnbl_principal where username = ? ";

    private static final String SQL_READ_LASTSYNC =
        "select sync_source, start_sync from fnbl_last_sync where principal = ?";

    private static final String SQL_EXISTS_OUT_OF_SYNC_CONTACTS = 
        "select last_update from fnbl_pim_contact " +
        "where userid = ? and last_update > ? limit 1";

    private static final String SQL_EXISTS_OUT_OF_SYNC_NOTES =
        "select last_update from fnbl_pim_note " +
        "where userid = ? and last_update > ? limit 1";

    private static final String SQL_EXISTS_OUT_OF_SYNC_CALENDARS = 
        "select last_update from fnbl_pim_calendar " +
        "where userid = ? and last_update > ? ";

    private static final String SQL_LIMIT = " limit 1";

    private static final String SQL_EXISTS_OUT_OF_SYNC_EVENTS =
        SQL_EXISTS_OUT_OF_SYNC_CALENDARS + " and type = '1'";

    private static final String SQL_EXISTS_OUT_OF_SYNC_TASKS =
        SQL_EXISTS_OUT_OF_SYNC_CALENDARS + " and type = '2'";

    // ------------------------------------------------------------ Private data

    /** The logger */
    private Logger log = Logger.getLogger("funambol.pushlistener.pim.dao");

    /** The core datasource */
    private DataSource coreDataSource = null;

    /** The user datasource */
    private RoutingDataSource userDataSource = null;

    // ------------------------------------------------------------- Constructor

    public PimListenerDao() throws PimListenerException {
        try {

            coreDataSource =
                DataSourceTools.lookupDataSource("jdbc/fnblcore");
            userDataSource =
                (RoutingDataSource)DataSourceTools.lookupDataSource("jdbc/fnbluser");

        } catch (Exception e){
            throw new PimListenerException("Error creating PimListenerDAO Object ", e);
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Reads all the records from table fnbl_principals with given username.
     * 
     * @param username the username
     * @return a list of all the principals for the given user
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public List<Sync4jPrincipal> readPrincipalsByUserName(String username)
    throws PimListenerException {

        if (log.isTraceEnabled()){
            log.trace("Reading principals with username '" + username + "'");
        }

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<Sync4jPrincipal> principals = new ArrayList<Sync4jPrincipal>();
        Sync4jPrincipal principal = null;

        try {

            con = coreDataSource.getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_READ_PRINCIPALS);

            ps.setString(1, username);

            rs = ps.executeQuery();

            while (rs.next()) {

                principal = new Sync4jPrincipal();
                principal.setId      (rs.getInt("id"));
                principal.setDeviceId(rs.getString("device"));
                principal.setUsername(rs.getString("username"));

                principals.add(principal);

            }

        } catch (Exception e) {

            String errmsg = "Error reading principals for username: " + username;
            log.error(errmsg, e);
            throw new PimListenerException(errmsg, e);

        } finally {
            DBTools.close(con, ps, rs);

            if (log.isTraceEnabled()){

                StringBuilder sb = new StringBuilder("sql: ");
                sb.append(SQL_READ_PRINCIPALS)
                  .append("\n> ").append("parameter 1: ").append(username)
                  .append("\n> ").append("result     : ");
                if (principals != null && !principals.isEmpty()) {
                    for (Sync4jPrincipal tmp : principals) {
                        sb.append("\n  - principal: " + tmp);
                    }
                } else {
                    sb.append("No principal");
                }
                log.trace(sb.toString());
            }
        }

        return principals;
    }

    /**
     * Checks if exists at least one contact for the given username such that
     * the last update time is greater than the given reference time.
     *
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if at least one row exists, <code>false</code>
     *         otherwise.
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public boolean existContactsOutOfSync(String username     ,
                                          long   referenceTime)
    throws PimListenerException {

        try {
            return existItemsOutOfSync(SQL_EXISTS_OUT_OF_SYNC_CONTACTS,
                                       username,
                                       referenceTime);
        } catch(Exception e) {
            String msg = "Error getting contacts out of sync";
            log.error(msg);
            throw new PimListenerException(msg, e);
        }
    }

    /**
     * Checks if exists at least one calendar (event or task) for the given
     * username such that the last update time is greater than the given
     * reference time.
     *
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if at least one row exists, <code>false</code>
     *         otherwise.
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public boolean existCalendarsOutOfSync(String username     ,
                                           long   referenceTime)
    throws PimListenerException {
        try {

            StringBuilder sb =
                new StringBuilder(SQL_EXISTS_OUT_OF_SYNC_CALENDARS).append(SQL_LIMIT);

            return existItemsOutOfSync(sb.toString(), username, referenceTime);

        } catch(Exception e) {
            String msg = "Error getting calendars out of sync";
            log.error(msg);
            throw new PimListenerException(msg, e);
        }
    }

    /**
     * Checks if exists at least one event for the given username such that
     * the last update time is greater than the given reference time.
     *
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if at least one row exists, <code>false</code>
     *         otherwise.
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public boolean existEventsOutOfSync(String username     ,
                                        long   referenceTime)
    throws PimListenerException {
        try {

            StringBuilder sb =
                new StringBuilder(SQL_EXISTS_OUT_OF_SYNC_EVENTS).append(SQL_LIMIT);

            return existItemsOutOfSync(sb.toString(), username, referenceTime);

        } catch(Exception e) {
            String msg = "Error getting events out of sync";
            log.error(msg);
            throw new PimListenerException(msg, e);
        }
    }

    /**
     * Checks if exists at least one task for the given username such that
     * the last update time is greater than the given reference time.
     *
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if at least one row exists, <code>false</code>
     *         otherwise.
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public boolean existTasksOutOfSync(String username     ,
                                       long   referenceTime)
    throws PimListenerException {
        try {

            StringBuilder sb =
                new StringBuilder(SQL_EXISTS_OUT_OF_SYNC_TASKS).append(SQL_LIMIT);

            return existItemsOutOfSync(sb.toString(), username, referenceTime);

        } catch(Exception e) {
            String msg = "Error getting tasks out of sync";
            log.error(msg);
            throw new PimListenerException(msg, e);
        }
    }

    /**
     * Checks if exists at least one note for the given username such that the
     * last update time is greater than the given reference time.
     *
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if at least one row exists, <code>false</code>
     *         otherwise.
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public boolean existNotesOutOfSync(String username     ,
                                       long   referenceTime)
    throws PimListenerException {
        try {

            return existItemsOutOfSync(SQL_EXISTS_OUT_OF_SYNC_NOTES,
                                       username,
                                       referenceTime);
        } catch(Exception e) {
            String msg = "Error getting notes out of sync";
            log.error(msg);
            throw new PimListenerException(msg, e);
        }
    }

    /**
     * Retrieves the map of the last sync time for each syncsource for the given
     * principal.
     *
     * @param principal the principal
     * @return a map with syncsource uri as key and last sync time as value
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     */
    public Map<String, Long> readPimLastSync(Sync4jPrincipal principal) 
    throws PimListenerException {

        if (log.isTraceEnabled()){
            log.trace("Reading the time of last sync for all syncsources for " +
                      "principal '"+principal+"'");
        }

        Connection        con = null;
        PreparedStatement ps = null;
        ResultSet         rs = null;

        Map<String, Long> pimLastSyncs = new HashMap<String, Long>();
        try {

            con = coreDataSource.getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_READ_LASTSYNC);
            ps.setLong(1, principal.getId());

            rs = ps.executeQuery();

            while (rs.next()) {
                pimLastSyncs.put(rs.getString(1), rs.getLong(2));
            }
            
        } catch (SQLException e) {
            String errmsg = "Error reading the time of the last sync";
            log.error(errmsg, e);
            throw new PimListenerException(errmsg, e);
        } finally {
            DBTools.close(con, ps, rs);

            if (log.isTraceEnabled()){
                StringBuilder sb = new StringBuilder("sql: ");
                sb.append(SQL_READ_LASTSYNC)
                  .append("\n> ").append("parameter 1: ").append(principal.getId())
                  .append("\n> ").append("results    : ");
                
                Iterator<String> keys = pimLastSyncs.keySet().iterator();
                int cont = 0;
                while(keys.hasNext()) {
                    String key = keys.next();
                    if (cont++ != 0) {
                        sb.append(", ");
                    }
                    sb.append(key).append(" - ")
                      .append(pimLastSyncs.get(key));
                }

                log.trace(sb.toString());
            }
        }

        return pimLastSyncs;
    }
    
    // --------------------------------------------------------- Private Methods

    /**
     * Checks, performing the given query for the input username and reference
     * time, if there is at least one record out of sync.
     *
     * @param query the query to perform
     * @param username the username
     * @param referenceTime the reference time
     * @return <code>true</code> if such row exists, <code>false</code>
     *         otherwise.
     * @throws SQLException if an error occurs
     */
    private boolean existItemsOutOfSync(String query        ,
                                        String username     ,
                                        long   referenceTime)
    throws SQLException {

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        boolean exists = false;
        try {

            con = userDataSource.getRoutedConnection(username);
            con.setReadOnly(true);

            ps = con.prepareStatement(query);

            ps.setString(1, username);
            ps.setLong  (2, referenceTime);

            rs = ps.executeQuery();
            
            while (rs.next()) {
                exists = true;
            }

        } finally {

            DBTools.close(con, ps, rs);

            if (log.isTraceEnabled()){
                StringBuilder sb = new StringBuilder("sql: ");
                sb.append(query)
                  .append("\n> ").append("parameter 1: ").append(username)
                  .append("\n> ").append("parameter 2: ").append(referenceTime)
                  .append("\n> ").append("result: ").append(exists);
                log.trace(sb.toString());
            }
        }

        return exists;
    }
}
