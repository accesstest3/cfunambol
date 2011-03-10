/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

import java.util.ArrayList;
import java.sql.*;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.server.store.*;
import com.funambol.framework.server.Sync4jSource;

/**
 * This is the store for SyncSource information of the Sync4j engine.
 * <ul>
 * <li>com.funambol.server.engine.Sync4jSource</li>
 * </ul>
 *
 * @version $Id: SyncSourcePersistentStore.java,v 1.2 2008-05-22 12:26:40 nichele Exp $
 */
public class SyncSourcePersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static final int SQL_GET_SOURCE                   = 0;
    public static final int SQL_SELECT_ALL_SOURCES           = 1;
    public static final int SQL_DELETE_CLIENT_MAPPING        = 2;
    public static final int SQL_DELETE_LAST_SYNC             = 3;
    public static final int SQL_DELETE_SOURCE                = 4;
    public static final int SQL_INSERT_SYNCSOURCE            = 5;
    public static final int SQL_UPDATE_SYNCSOURCE            = 6;
    public static final int SQL_DELETE_SOURCE_CLIENT_MAPPING = 7;
    public static final int SQL_DELETE_SOURCE_LAST_SYNC      = 8;

    private static final String SQL_ORDERBY_URI = " order by uri ";

    // -------------------------------------------------------------- Properties
    protected String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Constructors
    // ---------------------------------------------------------- Public methods

    public boolean delete(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jSource) {
            Sync4jSource s = (Sync4jSource) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = coreDataSource.getConnection();

                stmt = conn.prepareStatement(sql[SQL_DELETE_SOURCE_CLIENT_MAPPING]);
                stmt.setString(1, s.getUri());
                stmt.executeUpdate();
                stmt.close();

                stmt = conn.prepareStatement(sql[SQL_DELETE_SOURCE_LAST_SYNC]);
                stmt.setString(1, s.getUri());
                stmt.executeUpdate();
                stmt.close();

                stmt = conn.prepareStatement(sql[SQL_DELETE_SOURCE]);
                stmt.setString(1, s.getUri());
                stmt.executeUpdate();
                stmt.close();

            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting the source " + s, e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }
    public boolean store(Object o) throws PersistentStoreException {
        if (!(o instanceof Sync4jSource)) {
            return false; // Not me
        }
        Sync4jSource ss = (Sync4jSource) o;

        Connection conn = null;
        PreparedStatement stmt = null;
        int n = 0;

        try {
            conn = coreDataSource.getConnection();

            stmt = conn.prepareStatement(sql[SQL_UPDATE_SYNCSOURCE]);

            stmt.setString(1, ss.getConfig());
            stmt.setString(2, ss.getSourceName());
            stmt.setString(3, ss.getSourceTypeId());
            stmt.setString(4, ss.getUri());

            n = stmt.executeUpdate();
            stmt.close();

            if (n == 0) {
                stmt = conn.prepareStatement(sql[SQL_INSERT_SYNCSOURCE]);
                stmt.setString(1, ss.getUri());
                stmt.setString(2, ss.getConfig());
                stmt.setString(3, ss.getSourceName());
                stmt.setString(4, ss.getSourceTypeId());
                stmt.executeUpdate();
                stmt.close();
            }

        } catch (SQLException e) {
            throw new PersistentStoreException("Error updating the syncsource " + ss, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
        return true;
    }

    /**
     * Read the source from the data store.
     *
     * @param o the object that is an Sync4jSource
     *
     * @throws PersistentException in case of error reading the data store
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jSource) {
            Sync4jSource s = (Sync4jSource) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                stmt = conn.prepareStatement(sql[SQL_GET_SOURCE]);
                stmt.setString(1, s.getUri());
                rs = stmt.executeQuery();
                if (rs.next() == false) {
                    throw new NotFoundException("Source not found for "
                                               + s.getUri());
                }

                s.setUri   (rs.getString(1));
                s.setConfig(rs.getString(2));
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading the source " + s, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
            return true;
        }
        return false;
    }

   /**
     * Read all sources
     *
     * @return an array with the sources (empty if no objects are found)
     *
     * @throws PersistentException in case of error reading the data store
     */
    public Object[] read(Class objClass) throws PersistentStoreException {
        if (objClass.getName().equals(Sync4jSource.class.getName())) {

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            ArrayList ret = new ArrayList();

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                StringBuilder query =
                    new StringBuilder(sql[SQL_SELECT_ALL_SOURCES]);
                query.append(SQL_ORDERBY_URI);

                stmt = conn.prepareStatement(query.toString());

                rs = stmt.executeQuery();
                while (rs.next()) {
                    ret.add(new Sync4jSource(rs.getString(1),
                                             rs.getString(2)));
                }

                return ret.toArray(new Sync4jSource[ret.size()]);
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading sources", e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return null;
    }

    /**
     * Read all informations
     * @param o whichever object Sync
     * @param clause condition where for select
     *
     * @return Object[] array of object
     */
    public Object[] read(Object o, Clause clause)
    throws PersistentStoreException {
        if (o instanceof Sync4jSource) {
            Sync4jSource s = (Sync4jSource)o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            ArrayList ret = new ArrayList();

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);

                StringBuilder query =
                    new StringBuilder(sql[SQL_SELECT_ALL_SOURCES]);

                if (where.sql.length() > 0) {
                    query.append(" where ").append(where.sql);
                }
                query.append(SQL_ORDERBY_URI);

                stmt = conn.prepareStatement(query.toString());

                for (int i=0; i<where.parameters.length; ++i) {
                    stmt.setObject(i+1, where.parameters[i]);
                }

                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    ret.add(new Sync4jSource(
                            rs.getString(1),
                            rs.getString(2),
                            rs.getString(3),
                            rs.getString(4)
                    ));
                }

                return ret.toArray(new Sync4jSource[ret.size()]);
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading sources", e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return null;
    }

    /**
     * Insert a new SyncSource
     */
    protected void insertSyncSource(String sourcetypeid, Sync4jSource ss)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = coreDataSource.getConnection();

            stmt = conn.prepareStatement(sql[SQL_INSERT_SYNCSOURCE]);

            stmt.setString(1, ss.getUri());
            stmt.setString(2, ss.getConfig());
            stmt.setString(3, ss.getSourceName());
            stmt.setString(4, sourcetypeid);

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            throw new PersistentStoreException("Error storing the syncsource " + ss, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    /**
     * Update an existing SyncSource
     */
    protected void updateSyncSource(String sourcetypeid, Sync4jSource ss)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = coreDataSource.getConnection();

            stmt = conn.prepareStatement(sql[SQL_UPDATE_SYNCSOURCE]);

            stmt.setString(1, ss.getConfig());
            stmt.setString(2, ss.getSourceName());
            stmt.setString(3, ss.getSourceTypeId());
            stmt.setString(4, ss.getUri());

            stmt.executeUpdate();
            stmt.close();

        } catch (SQLException e) {
            throw new PersistentStoreException("Error updating the syncsource " + ss, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    public int count(Object o, Clause clause) {
        return -1;
    }
}

