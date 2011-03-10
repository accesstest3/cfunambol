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
package com.funambol.server.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;

import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ConfigItem;
import com.funambol.framework.server.store.BasePersistentStore;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.server.store.PreparedWhere;
import com.funambol.framework.tools.DBTools;

/**
 * This is the store for device configuration information.
 * It persistes the following classes:
 * <ul>
 * <li>com.funambol.framework.server.ConfigItem</li>
 * </ul>
 *
 * @version $Id: ConfigPersistentStore.java,v 1.2 2008-05-22 12:28:47 nichele Exp $
 */
public class ConfigPersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants
    private static final String SQL_GET_CONFIG     =
        "select uri, value, last_update, status, encrypted " +
        "from fnbl_device_config where username =? and " +
        "(principal = ? or principal = -1) and uri = ?";

    private static final String SQL_GET_ALL_URIs   =
        "select uri from fnbl_device_config where username=? and " +
        "(principal = ? or principal=-1)";

    private static final String SQL_GET_ALL_CONFIG =
        "select username, principal, uri, value, last_update, status, " +
        "encrypted from fnbl_device_config where username = ? and " +
        "(principal = ? or principal=-1)";

    private static final String SQL_INSERT_CONFIG  =
        "insert into fnbl_device_config (username, principal, uri, value, " +
        "last_update, status, encrypted) values (?, ?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE_CONFIG  =
        "update fnbl_device_config set value = ?, last_update = ?, " +
        "status = ? where principal = ? and uri = ?";

    private static final String SQL_DELETE_CONFIG  =
        "update fnbl_device_config set last_update = ?, status = 'D' " +
        "where principal = ? and uri = ?";

    private static final String SQL_DELETE_CONFIGS =
        "delete from fnbl_device_config where principal = ?";

    private static final String SQL_COUNT_CONFIGS  =
        "select count(*) as configs from fnbl_device_config where username = ? " +
        "and (principal = ? or principal= -1) ";

    private static final String STATE_NEW     = "N";
    private static final String STATE_UPDATED = "U";

    // -------------------------------------------------------------- Properties

    private String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Counts all the config items that satisfy the given clause
     * NOTE: this method can not be implemented since the fnbl_device_config table
     * is partitioned and it is accessed using the routing datasource and so a
     * username is required
     *
     * @param o object that will be counted.
     * @param clause The additional where clause.
     * @return int number of config item
     */
    public int count(Object o, Clause clause) throws PersistentStoreException {
        return -1;
    }

    /**
     * Not supported
     *
     * @param objClass the class
     * @throws PersistentStoreException in case of error reading the device
     */
    public Object[] read(Class objClass) throws PersistentStoreException {
        return null;
    }

    /**
     * Read all the ConfigItems that satisfy the given clause
     *
     * NOTE: this method can not be implemented since the fnbl_device_config table
     * is partitioned and it is accessed using the routing datasource and so a
     * username is required
     *
     * @param object whichever object
     * @param clause condition where for select
     * @return null since the method is not applicable
     */
    public Object[] read(Object object, Clause clause)
    throws PersistentStoreException  {
        return null;
    }

    /**
     * Get all the config items that satisfy the given clause.
     * NOTE: this method can not be implemented since the fnbl_device_config table
     * is partitioned and it is accessed using the routing datasource and so a
     * username is required
     *
     * @param clause The object that must be a device for this method
     * @return an array with the ConfigItems that satisfy the given clause
     * @throws PersistentStoreException if an error occurs
     */
    public Object[] read(Clause clause) throws PersistentStoreException {
        return null;
    }

    /**
     * Counts the all ConfigItem from the DataBase that match a given set of conditions.
     *
     * @param principal
     * @param clause condition where for select
     * @return number of found ConfigItem
     * @throws PersistentStoreException
     */
    public int count(Sync4jPrincipal principal, Clause clause) throws PersistentStoreException {

        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }
        long principalId = principal.getId();

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = userDataSource.getRoutedConnection(principal.getUsername());
            conn.setReadOnly(true);

            String query = SQL_COUNT_CONFIGS;
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " and " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);

            stmt.setString(1, principal.getUsername());
            stmt.setLong  (2, principalId);

            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 3, where.parameters[i]);
                }
            }
            rs = stmt.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistentStoreException("Error counting ConfigItem", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    /**
     * Deletes the config items from the data store.
     *
     * @param o The object that must be a device for this method
     *
     * @throws PersistentStoreException in case of error deleting the device
     */
    public boolean delete(Object o) throws PersistentStoreException {
        if (o instanceof ConfigItem) {
            ConfigItem c = (ConfigItem)o;

            Connection conn = null;
            PreparedStatement stmt = null;
            int n = -1;

            try {
                conn = userDataSource.getRoutedConnection(c.getPrincipal().getUsername());

                // soft delete
                stmt = conn.prepareStatement(SQL_DELETE_CONFIG);

                if (c.getLastUpdate() != null) {
                    stmt.setLong (1, c.getLastUpdate().getTime());
                } else {
                    stmt.setLong (1, System.currentTimeMillis());
                }

                stmt.setLong  (2, c.getPrincipal().getId());
                stmt.setString(3, c.getNodeURI());

                n = stmt.executeUpdate();

                return n > 0;

            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting " + c , e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error deleting " + c , e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
        }
        return false;
    }

    /**
     * Insert or Update the device from the data store.
     *
     * @param o The object that must be a device for this method
     *
     * @throws PersistentStoreException in case of error storing the config item
     */
    public boolean store(Object o) throws PersistentStoreException {

        if (o instanceof ConfigItem) {

            ConfigItem configItem = (ConfigItem) o;

            Connection conn        = null;
            PreparedStatement stmt = null;
            int n = 0;

            try {

                if (configItem.getUsername() == null) {
                    throw new PersistentStoreException("The username must be not null");
                }
                if (configItem.getPrincipal() == null) {
                    throw new PersistentStoreException("The principal must be not null");
                }
                if (configItem.getNodeURI() == null || configItem.getNodeURI().equals("")) {
                    throw new PersistentStoreException("The config node URI id must be not null");
                }

                conn = userDataSource.getRoutedConnection(configItem.getUsername());

                stmt = conn.prepareStatement(SQL_UPDATE_CONFIG);

                // try to update the item
                if (configItem.getValue() != null) {
                    stmt.setString(1, configItem.getValue());
                } else {
                    stmt.setNull(1, Types.VARCHAR);
                }

                if (configItem.getLastUpdate() != null) {
                    stmt.setLong (2, configItem.getLastUpdate().getTime());
                } else {
                    stmt.setLong (2, System.currentTimeMillis());
                }

                stmt.setString(3, STATE_UPDATED);

                stmt.setLong  (4, configItem.getPrincipal().getId());
                stmt.setString(5, configItem.getNodeURI());

                n = stmt.executeUpdate();

                if (n == 0) {

                    stmt = conn.prepareStatement(SQL_INSERT_CONFIG);

                    stmt.setString(1, configItem.getUsername());
                    stmt.setLong(2, configItem.getPrincipal().getId());
                    stmt.setString(3, configItem.getNodeURI());
                    if (configItem.getValue() != null) {
                        stmt.setString(4, configItem.getValue());
                    } else {
                        stmt.setNull(4, Types.VARCHAR);
                    }

                    if (configItem.getLastUpdate() != null) {
                        stmt.setLong(5, configItem.getLastUpdate().getTime());
                    } else {
                        stmt.setLong(5, System.currentTimeMillis());
                    }

                    stmt.setString(6, STATE_NEW);

                    stmt.setBoolean(7, configItem.isEncrypted());

                    stmt.executeUpdate();
                }

            } catch (SQLException e) {
                throw new PersistentStoreException("Error storing the item " + configItem, e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error storing the item " + configItem, e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    /**
     * Read a config item from the data store.
     *
     * @param o The object that must be a ConfigItem for this method
     * @throws PersistentStoreException in case of error reading the ConfigItem
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (!(o instanceof ConfigItem)) {
            return false;
        }
        ConfigItem c = (ConfigItem) o;

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {

            conn = userDataSource.getRoutedConnection(c.getPrincipal().getUsername());

            stmt = conn.prepareStatement(SQL_GET_CONFIG);

            stmt.setString(1, c.getUsername());
            stmt.setLong  (2, c.getPrincipal().getId());
            stmt.setString(3, c.getNodeURI());

            rs = stmt.executeQuery();

            if (rs.next() == false) {
                throw new NotFoundException("ConfigItem " + c + " not found");
            }

            c.setValue(rs.getString(2));
            c.setLastUpdate(new Timestamp(rs.getLong(3)));
            c.setStatus(rs.getString(4).charAt(0));
            c.setEncrypted(rs.getBoolean(5));

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the ConfigItem " + c, e);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new PersistentStoreException("Error reading the ConfigItem " + c, e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
        return true;

    }

    /**
     * Get all the config items that satisfy the given clause for the given principal
     *
     *
     * @param principal the principal
     * @param clause The object that must be a device for this method
     * @return an array with the ConfigItems that satisfy the given clause
     * @throws PersistentStoreException if an error occurs
     */
    public Object[] read(Sync4jPrincipal principal, Clause clause) throws PersistentStoreException {

        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }

        long principalId = principal.getId();

        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        List<ConfigItem> ret   = new ArrayList<ConfigItem>();

        try {

            conn = userDataSource.getRoutedConnection(principal.getUsername());

            String query = SQL_GET_ALL_CONFIG;

            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " and " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);

            stmt.setString(1, principal.getUsername());
            stmt.setLong  (2, principalId);

            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 3, where.parameters[i]);
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                ConfigItem item = new ConfigItem(rs.getString(1),
                                                 new Sync4jPrincipal(rs.getLong(2)),
                                                 rs.getString(3),
                                                 rs.getString(4),
                                                 new Timestamp(rs.getLong(5)),
                                                 rs.getString(6).charAt(0),
                                                 rs.getBoolean(7));
                ret.add(item);
            }

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading ConfigItem", e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error reading ConfigItem", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }

        return (ConfigItem[])ret.toArray(new ConfigItem[ret.size()]);
    }

    /**
     * Get all the config item URIs that satisfy the given clause for the given principal
     *
     * @param principal the principal
     * @param clause The object that must be a device for this method
     * @return an array with the ConfigItems that satisfy the given clause
     * @throws PersistentStoreException if an error occurs
     */
    public String[] readURIs(Sync4jPrincipal principal, Clause clause) throws PersistentStoreException {

        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }

        long principalId = principal.getId();

        Connection conn        = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        List<String> ret       = new ArrayList<String>();

        try {

            conn = userDataSource.getRoutedConnection(principal.getUsername());

            String query = SQL_GET_ALL_URIs;

            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " and " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);

            stmt.setString(1, principal.getUsername());
            stmt.setLong  (2, principalId);

            if ( (clause != null) && (! (clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 3, where.parameters[i]);
                }
            }

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading ConfigItem", e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error reading ConfigItem", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }

        return (String[])ret.toArray(new String[ret.size()]);
    }

    /**
     * Removes (hard delete) the config item for the given principal from the data store.
     *
     * @param principal the principal
     * @return the number of deleted items
     * @throws PersistentStoreException in case of error reading the device
     */
    public int removeConfigItems(Sync4jPrincipal principal)
    throws PersistentStoreException {

        if (principal == null) {
            throw new IllegalArgumentException("The given principal must be not null");
        }
        long principalID = principal.getId();

        Connection        conn = null;
        PreparedStatement stmt = null;

        int n = 0;
        try {

            conn = userDataSource.getRoutedConnection(principal.getUsername());

            // hard delete
            stmt = conn.prepareStatement(SQL_DELETE_CONFIGS);

            stmt.setLong  (1, principalID);

            n = stmt.executeUpdate();

            return n;

        } catch (SQLException e) {
            throw new PersistentStoreException("Error removing the config items for " +
                    principal, e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error removing the config items for " +
                    principal, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }

    }

}

