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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Map;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.store.*;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

/**
 * This is the store for principal information of the Sync4j engine.
 * Currently it persistes the following classes:
 * <ul>
 * <li>com.funambol.framework.security.Sync4jPrincipal</li>
 * </ul>
 *
 * @version $Id: PrincipalPersistentStore.java,v 1.2 2008-05-22 12:25:08 nichele Exp $
 */
public class PrincipalPersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants

    public static final int SQL_INSERT_PRINCIPAL             =  0;
    public static final int SQL_GET_PRINCIPAL                =  1;
    public static final int SQL_SELECT_PRINCIPAL             =  2;
    public static final int SQL_UPDATE_PRINCIPAL             =  3;
    public static final int SQL_SELECT_ALL_PRINCIPALS        =  4;
    public static final int SQL_DELETE_PRINCIPAL             =  5;
    public static final int SQL_DELETE_CLIENT_MAPPING        =  6;
    public static final int SQL_DELETE_LAST_SYNC             =  7;
    public static final int SQL_COUNT_PRINCIPALS             =  8;

    public static final String NS_PRINCIPAL = "principal";

    // -------------------------------------------------------------- Properties

    protected String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Private data
    private DBIDGenerator dbIDGenerator = null;

    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods
    @Override
    public void init() throws BeanInitializationException {
        super.init();
        dbIDGenerator =  DBIDGeneratorFactory.getDBIDGenerator(NS_PRINCIPAL, coreDataSource);
    }


    /**
     * Delete the principal from the data store.
     *
     * @param o The object that must be a Sync4jPrincipal for this method
     *
     * @throws PersistentStoreException in case of error reading the data store
     */
    public boolean delete(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jPrincipal) {
            Sync4jPrincipal p = (Sync4jPrincipal) o;

            Connection coreConn = null;
            Connection userConn = null;
            PreparedStatement coreStmt = null;
            PreparedStatement userStmt = null;

            try {

                if (p.getUsername() == null) {
                    //
                    // For instance deleting a principal with the admin tool, just
                    // its id is available. Since also the username is required,
                    // a read is needed.
                    //        
                    read(p);
                }

                userConn = userDataSource.getRoutedConnection(p.getUsername());

                long principalId = p.getId();

                userStmt = userConn.prepareStatement(sql[SQL_DELETE_CLIENT_MAPPING]);
                userStmt.setLong(1, principalId);
                userStmt.executeUpdate();
                userStmt.close();

                coreConn = coreDataSource.getConnection();

                coreStmt = coreConn.prepareStatement(sql[SQL_DELETE_LAST_SYNC]);
                coreStmt.setLong(1, principalId);
                coreStmt.executeUpdate();
                coreStmt.close();

                coreStmt = coreConn.prepareStatement(sql[SQL_DELETE_PRINCIPAL]);
                coreStmt.setLong(1, principalId);
                coreStmt.executeUpdate();

            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting the principal " + p, e);
            } finally {
                DBTools.close(coreConn, coreStmt, null);
                DBTools.close(userConn, userStmt, null);
            }
            return true;
        }
        return false;
    }

    public boolean store(Object o, String operation) throws PersistentStoreException {
        return false;
    }

    public boolean store(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jPrincipal) {
            Sync4jPrincipal p = (Sync4jPrincipal) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int n = 0;

            try {

                if (p.getId() != -1) {
                    conn = coreDataSource.getConnection();
                    
                    stmt = conn.prepareStatement(sql[SQL_UPDATE_PRINCIPAL]);
                    stmt.setString(1, p.getUsername());
                    stmt.setString(2, p.getDeviceId());
                    stmt.setLong(3, p.getId());
                    n = stmt.executeUpdate();
                    
                    DBTools.close(conn, stmt, null);
                } else {
                    //check if the principal already exist: verify username-deviceid
                    conn = coreDataSource.getConnection();
                    conn.setReadOnly(true);
                    
                    stmt = conn.prepareStatement(sql[SQL_SELECT_PRINCIPAL]);
                    stmt.setString(1, p.getUsername());
                    stmt.setString(2, p.getDeviceId());
                    rs = stmt.executeQuery();

                    if (rs.next() == false) {
                        n = 0;
                    } else {
                        n = 1;
                        p.setId       (rs.getLong(1));
                        p.setUsername (rs.getString(2));
                        p.setDeviceId (rs.getString(3));
                    }
                    //
                    // We close the connection and a new one is created since
                    // the previous one was read-only
                    //
                    DBTools.close(conn, stmt, rs);
                }

                if (n == 0) {
                    // The first time!!!
                    conn = coreDataSource.getConnection();
                    long principalId = getPrincipalId();
                    p.setId(principalId);

                    stmt = conn.prepareStatement(sql[SQL_INSERT_PRINCIPAL]);
                    stmt.setLong(1, principalId);
                    stmt.setString(2, p.getUsername());
                    stmt.setString(3, p.getDeviceId());
                    stmt.executeUpdate();
                }
                return true;
            } catch (Exception e) {
                throw new PersistentStoreException("Error storing principal " + p, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return false;
    }

    /**
     * Read the principal from the data store. If <i>getId()</i> returns null,
     * it tries to read the principal from username/device, otherwise throws id
     * is used for the lookup.
     * @param o The object that must be a Sync4jPrincipal for this method
     * @throws PersistentException in case of error reading the data store
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (o instanceof Sync4jPrincipal) {
            Sync4jPrincipal p = (Sync4jPrincipal) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                if (p.getId() == -1) {
                    stmt = conn.prepareStatement(sql[SQL_SELECT_PRINCIPAL]);
                    stmt.setString(1, p.getUsername());
                    stmt.setString(2, p.getDeviceId());
                } else {
                    stmt = conn.prepareStatement(sql[SQL_GET_PRINCIPAL]);
                    stmt.setLong(1, p.getId());
                }
                rs = stmt.executeQuery();

                if (rs.next() == false) {
                    throw new NotFoundException("Principal not found for "
                    + p.toString());
                }
                p.setId       (rs.getLong(1));
                p.setUsername (rs.getString(2));
                p.setDeviceId (rs.getString(3));

            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading principal " + p, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
            return true;
        }
        return false;
    }

    public Object[] read(Class objClass) throws PersistentStoreException {
        return null;
    }

    /**
     * Read all for the Sync4jPrincipal.
     * @param o The object that must be a Sync4jPrincipal for this method
     * @param clause condition where for select
     *
     * @return Object[] array of Sync4jPrincipals
     */
    public Object[] read(Object o, Clause clause) throws PersistentStoreException {
        if (!(o instanceof Sync4jPrincipal)) {
            return null; // Not me!!
        }
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
            String query = sql[SQL_SELECT_ALL_PRINCIPALS];
            if (where.sql.length() > 0){
                query += " where " + where.sql;
            }
            stmt = conn.prepareStatement(query);
            for (int i=0; i<where.parameters.length; ++i) {
                stmt.setObject(i+1, where.parameters[i]);
            }
            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(
                    Sync4jPrincipal.createPrincipal(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getString(3))
                    );
            }

            return ret.toArray(new Sync4jPrincipal[ret.size()]);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading principals", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }



    /**
     * Count the number of principals that satisfy the conditions specified in input
     * @param o The object of the Sync4jPrincipal type.
     * @param clause The additional where clause.
     * @return int number of principals
     */
    public int count(Object o, Clause clause) throws PersistentStoreException {
        if (!(o instanceof Sync4jPrincipal)) {
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
            String query = sql[SQL_COUNT_PRINCIPALS];
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
            throw new PersistentStoreException("Error reading count principals ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    //---------------------------------------------------------- Private Methods

    /**
     * Return the next principal id reading it by the default server id generator
     * @return the next principal id
     */
    private long getPrincipalId() throws DBIDGeneratorException {
        return dbIDGenerator.next();

    }
}

