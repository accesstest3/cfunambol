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

import java.sql.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.LastTimestamp;
import com.funambol.framework.server.store.ConfigPersistentStoreException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.BasePersistentStore;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.server.store.PreparedWhere;
import com.funambol.framework.tools.DBTools;

/**
 * This is the store for persistent information regarding the last time
 * stamp of the synchronization process.
 * It persists the following classes:
 * <ul>
 * <li>com.funambol.framework.server.LastTimestamp</li>
 * </ul>
 *
 * @version $Id: LastTimestampPersistentStore.java,v 1.2 2008-05-22 10:52:25 nichele Exp $
 */
public class LastTimestampPersistentStore
extends    BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static final int SQL_INSERT_LAST_SYNC     = 0;
    public static final int SQL_UPDATE_LAST_SYNC     = 1;
    public static final int SQL_SELECT_LAST_SYNC     = 2;
    public static final int SQL_SELECT_ALL_LAST_SYNC = 3;
    public static final int SQL_DELETE_LAST_SYNC     = 4;
    public static final int SQL_COUNT_LAST_SYNC      = 5;

    // -------------------------------------------------------------- Properties

    private String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    /**
     * Store the last timestamp.
     * Into tagServer there is the Next anchor sent by client: now this become
     * a Last anchor of the server.
     * Into tagClient there is the Next anchor sent by server: now this become
     * a Last anchor of the client.
     *
     * @param o the Object that must be a LastTimestamp object with all
     * information about start and end sync timestamp
     *
     * @throws PersistentStoreException
     */
    public boolean store(Object o)
    throws PersistentStoreException {
        if (o instanceof LastTimestamp) {
            LastTimestamp l = (LastTimestamp) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = coreDataSource.getConnection();

                stmt = conn.prepareStatement(sql[SQL_UPDATE_LAST_SYNC]);
                stmt.setInt      (1, l.syncType                 );
                if (l.status != -1) {
                    stmt.setInt  (2, l.status);
                } else {
                    stmt.setNull (2, Types.INTEGER);
                }
                stmt.setString   (3, l.tagServer                );
                stmt.setString   (4, l.tagClient                );
                stmt.setLong     (5, l.start                    );
                stmt.setLong     (6, l.end                      );
                stmt.setLong     (7, l.principalId              );
                stmt.setString   (8, l.database                 );

                int n = stmt.executeUpdate();

                if (n == 0) {
                    //
                    // The first time!!!
                    //
                    stmt.close();
                    stmt = conn.prepareStatement(sql[SQL_INSERT_LAST_SYNC]);
                    stmt.setLong     (1, l.principalId              );
                    stmt.setString   (2, l.database                 );
                    stmt.setInt      (3, l.syncType                 );
                    if (l.status != -1) {
                        stmt.setInt  (4, l.status);
                    } else {
                        stmt.setNull (4, Types.INTEGER);
                    }
                    stmt.setString   (5, l.tagServer                );
                    stmt.setString   (6, l.tagClient                );
                    stmt.setLong     (7, l.start                    );
                    stmt.setLong     (8, l.end                      );
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                throw new PersistentStoreException("Error storing last timestamp", e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    public boolean read(Object o)
    throws PersistentStoreException {
        if (o instanceof LastTimestamp) {
            LastTimestamp l = (LastTimestamp) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                stmt = conn.prepareStatement(sql[SQL_SELECT_LAST_SYNC]);
                stmt.setLong  (1, l.principalId);
                stmt.setString(2, l.database);
                rs = stmt.executeQuery();

                if (rs.next() == false) {
                    throw new NotFoundException("Last timestamp not found for "
                    + l.toString()
                    );
                }
                l.syncType  = rs.getInt   (1);
                l.status    = rs.getInt   (2);
                l.tagServer = rs.getString(3);
                l.tagClient = rs.getString(4);
                l.start     = rs.getLong  (5);
                l.end       = rs.getLong  (6);

            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading last timestamp", e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
            return true;
        }
        return false;
    }

    /** Read all objects stored the persistent media.
     *
     * @param objClass the object class handled by the persistent store
     *
     * @return an array containing the objects read. If no objects are found an
     *         empty array is returned. If the persistent store has not
     *         processed the quest, null is returned.
     *
     * @throws PersistentStoreException
     *
     */
    public Object[] read(Class objClass) throws PersistentStoreException {
        //
        // TO DO (not used yet)
        //
        return null;
    }


    public boolean delete(Object o) throws PersistentStoreException
    {
        if (o instanceof LastTimestamp) {
            LastTimestamp t = (LastTimestamp) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            try {
                conn = coreDataSource.getConnection();

                stmt = conn.prepareStatement(sql[SQL_DELETE_LAST_SYNC]);
                stmt.setLong  (1, t.principalId);
                stmt.setString(2, t.database);
                stmt.executeUpdate();
                stmt.close();

            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting the principal " + t, e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    public Object[] read(Object o, Clause clause)
    throws PersistentStoreException {
        if (!(o instanceof LastTimestamp)) {
            return null; // Not me!!
        }
        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;

        List ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
            StringBuffer query = new StringBuffer(sql[SQL_SELECT_ALL_LAST_SYNC]);
            if (where.sql.length() > 0){
                query.append(" where ").append(where.sql);
            }
            stmt = conn.prepareStatement(query.toString());
            for (int i=0; i<where.parameters.length; ++i) {
                stmt.setObject(i+1, where.parameters[i]);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {

                ret.add(new LastTimestamp(
                            rs.getLong  (1),
                            rs.getString(2),
                            rs.getInt   (3),
                            rs.getInt   (4),
                            rs.getString(5),
                            rs.getString(6),
                            rs.getLong(7)  ,
                            rs.getLong(8)
                        )
                );

            }

            return ret.toArray(new LastTimestamp[ret.size()]);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading last timestamps", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public int count(Object o, Clause clause)
    throws PersistentStoreException {
        if (!(o instanceof LastTimestamp)) {
            return -1; // Not me!
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int n = 0;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
            String query = sql[SQL_COUNT_LAST_SYNC];
            if (where.sql.length()>0) {
                query += " where " + where.sql;
            }
            stmt = conn.prepareStatement(query);
            for (int i=0; i<where.parameters.length; ++i) {
                stmt.setObject(i+1, where.parameters[i]);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                n = rs.getInt(1);
            }
            return n;
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading count last timestamps ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public boolean store(Object o, String operation) throws PersistentStoreException
    {
        return false;
    }

}
