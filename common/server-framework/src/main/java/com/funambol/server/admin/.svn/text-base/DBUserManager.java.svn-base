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
package com.funambol.server.admin;

import java.util.ArrayList;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.PreparedWhere;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.server.store.SQLHelperClause;

import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.CloneBeanException;
import com.funambol.framework.tools.beans.CloneableBean;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.db.RoutingDataSource;

/**
 * This class implements the UserManager: store and read users and roles from datastore.
 *
 * @version $Id: DBUserManager.java,v 1.6 2008-06-24 12:49:06 piter_may Exp $
 *
 */
public class DBUserManager implements UserManager, 
                                      java.io.Serializable,
                                      LazyInitBean,
                                      CloneableBean {

    // --------------------------------------------------------------- Constants
    public final static int SQL_SELECT_ROLES           = 0;
    public final static int SQL_SELECT_USERS           = 1;
    public final static int SQL_SELECT_USER_ROLES      = 2;
    public final static int SQL_UPDATE_USER            = 3;
    public final static int SQL_INSERT_USER            = 4;
    public final static int SQL_DELETE_USER            = 5;
    public final static int SQL_DELETE_USER_ROLES      = 6;
    public final static int SQL_INSERT_USER_ROLES      = 7;
    public final static int SQL_DELETE_PRINCIPAL       = 8;
    public final static int SQL_SELECT_USER_PRINCIPALS = 9;
    public final static int SQL_DELETE_CLIENT_MAPPING  = 10;
    public final static int SQL_DELETE_LAST_SYNC       = 11;
    public final static int SQL_GET_USER               = 12;
    public final static int SQL_COUNT_USERS            = 13;
    public final static int SQL_COUNT_ADMINISTRATORS   = 14;
    public final static int SQL_DELETE_DEVICE_CONFIG   = 15;

    public static final String LOG_NAME = "admin";

    /** Core datasource jndi name */
    protected static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";

    /** User datasource jndi name */
    protected static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";

    // -------------------------------------------------------------- Properties
    protected String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    protected String[] defaultRoles = null;

    public void setDefaultRoles(String[] defaultRoles) {
        this.defaultRoles = defaultRoles;
    }

    public String[] getDefaultRoles() {
        return this.defaultRoles;
    }

    protected String[] mandatoryRoles = null;

    public void setMandatoryRoles(String[] mandatoryRoles) {
        this.mandatoryRoles = mandatoryRoles;
    }

    public String[] getMandatoryRoles() {
        return this.mandatoryRoles;
    }

    // ---------------------------------------------------------- Protected data
    protected transient DataSource ds = null;

    protected transient DataSource        coreDataSource = null;
    protected transient RoutingDataSource userDataSource = null;

    protected SQLHelperClause sqlHelperClause = null;

    protected boolean initialized = false;
    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    public DBUserManager cloneBean() throws CloneBeanException {
        DBUserManager newInstance = new DBUserManager();
        newInstance.sql = this.sql;
        if (initialized) {
            try {
                newInstance.init();
            } catch (BeanInitializationException ex) {
                throw new CloneBeanException("Unable to initialize new instance", ex);
            }
        }
        return newInstance;
    }
    
    public void init() throws BeanInitializationException {
        try {
            coreDataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);
        } catch (Exception e){
            throw new BeanInitializationException("Error looking up datasource '" +
                                                  CORE_DATASOURCE_JNDINAME + "'", e);
        }

        try {
            userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);
        } catch (Exception e){
            throw new BeanInitializationException("Error looking up datasource '" +
                                                  USER_DATASOURCE_JNDINAME + "'", e);

        }

        if (sqlHelperClause == null) {
            Connection conn = null;
            try {
                conn = coreDataSource.getConnection();
                sqlHelperClause = new SQLHelperClause(conn.getMetaData().getDatabaseProductName());
            } catch(SQLException e) {
                throw new BeanInitializationException("Error getting connection in SQLHelperClause initialization", e);
            } finally {
                DBTools.close(conn, null, null);
            }
        }

        //
        // In the code we use "ds". With this statements, if ds is null, coreDataSource
        // is used.
        //
        if (ds == null) {
            ds = coreDataSource;
        }
        initialized = true;
    }


    /*
     * Search all roles available.
     *
     * @return array of roles
     */
    public String[] getRoles() throws AdminException, PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList roles = new ArrayList();
        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_ROLES]);

            rs = stmt.executeQuery();
            while (rs.next()) {
                //role then space then description
                roles.add(rs.getString(1) + ' ' + rs.getString(2));
            }
            return (String[])roles.toArray(new String[roles.size()]);

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading roles ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public Sync4jUser[] getUsers(Clause clause) throws AdminException, PersistentStoreException {
        PreparedWhere pw = sqlHelperClause.getPreparedWhere(clause);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList users = new ArrayList();
        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_SELECT_USERS];
            if (pw.sql.length() > 0) {
                query += " where " + pw.sql;
            }

            stmt = conn.prepareStatement(query);

            for(int i=0; i<pw.parameters.length; ++i) {
                stmt.setObject(i+1, pw.parameters[i]);
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                users.add(
                 new Sync4jUser(
                    rs.getString(1),  // username
                    EncryptionTool.decrypt(rs.getString(2)),  // password (the password is encrypted in the DB)
                    rs.getString(3),  // first name
                    rs.getString(4),  // last name
                    rs.getString(5),  // email
                    null              // roles
                 )
                );
            }
            return (Sync4jUser[])users.toArray(new Sync4jUser[users.size()]);

        } catch (EncryptionException e) {
            throw new PersistentStoreException("Error decrypting the password", e);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading users", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public void getUser(Sync4jUser user) throws AdminException, PersistentStoreException {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);
            
            stmt = conn.prepareStatement(sql[SQL_GET_USER]);
            
            stmt.setString(1, user.getUsername());

            rs = stmt.executeQuery();

            while (rs.next()) {
                user.setUsername (rs.getString(1));
                user.setEmail    (rs.getString(2));
                user.setFirstname(rs.getString(3));
                user.setLastname (rs.getString(4));
                user.setPassword (EncryptionTool.decrypt(rs.getString(5))); // The password is encrypted in the db
            }

        } catch (EncryptionException e) {
            throw new PersistentStoreException("Error decrypting a password", e);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading user ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public void getUserRoles(Sync4jUser user) throws AdminException, PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList ret = new ArrayList();
        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_USER_ROLES]);
            stmt.setString(1, user.getUsername());

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(rs.getString(1));
            }

            user.setRoles((String[])ret.toArray(new String[ret.size()]));

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading roles ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public void setUser(Sync4jUser user) throws AdminException, PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(sql[SQL_UPDATE_USER]);
            
            // username is always stored lowercase
            String username = user.getUsername().toLowerCase();
            
            // the password in the db must be encrypted
            stmt.setString(1, EncryptionTool.encrypt(user.getPassword()));
            stmt.setString(2, user.getEmail());
            stmt.setString(3, user.getFirstname());
            stmt.setString(4, user.getLastname());
            stmt.setString(5, username          );

            stmt.executeUpdate();

            stmt.close();

            //
            // To update the roles it's necessary to delete old roles and
            // to insert the new ones
            //
            stmt = conn.prepareStatement(sql[SQL_DELETE_USER_ROLES]);

            stmt.setString(1, username);

            stmt.executeUpdate();

            stmt.close();

            String[] roles = user.getRoles();
            for (int i=0; i<roles.length; i++) {
                stmt = conn.prepareStatement(sql[SQL_INSERT_USER_ROLES]);
                stmt.setString(1, roles[i]);
                stmt.setString(2, username);

                stmt.executeUpdate();
            }


        } catch (EncryptionException e) {
            throw new PersistentStoreException("Error encrypting the password for: " + user, e);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error updating user: " + user, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    public void insertUser(Sync4jUser user) throws AdminException, PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        int n = 0;

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(sql[SQL_UPDATE_USER]);
            
            // username is always stored lowercase
            user.setUsername(user.getUsername().toLowerCase());

            // the password in the db must be encrypted
            stmt.setString(1, EncryptionTool.encrypt(user.getPassword()));
            stmt.setString(2, user.getEmail()    );
            stmt.setString(3, user.getFirstname());
            stmt.setString(4, user.getLastname() );
            stmt.setString(5, user.getUsername() );

            n = stmt.executeUpdate();

            if (n == 0) {

                stmt = conn.prepareStatement(sql[SQL_INSERT_USER]);
                                
                // the password in the db must be encrypted
                stmt.setString(1, user.getUsername() );
                stmt.setString(2, EncryptionTool.encrypt(user.getPassword()));
                stmt.setString(3, user.getEmail()    );
                stmt.setString(4, user.getFirstname());
                stmt.setString(5, user.getLastname() );

                stmt.executeUpdate();

                stmt.close();

                String[] roles = user.getRoles();
                for (int i=0; i<roles.length; i++) {
                    stmt = conn.prepareStatement(sql[SQL_INSERT_USER_ROLES]);
                    stmt.setString(1, roles[i]);
                    stmt.setString(2, user.getUsername());

                    stmt.executeUpdate();
                }
            } else {
                stmt.close();

                //
                // To update the roles it's necessary to delete old roles and
                // to insert the new ones
                //
                stmt = conn.prepareStatement(sql[SQL_DELETE_USER_ROLES]);

                stmt.setString(1, user.getUsername());

                stmt.executeUpdate();

                stmt.close();

                String[] roles = user.getRoles();
                for (int i=0; i<roles.length; i++) {
                    stmt = conn.prepareStatement(sql[SQL_INSERT_USER_ROLES]);
                    stmt.setString(1, roles[i]);
                    stmt.setString(2, user.getUsername());

                    stmt.executeUpdate();
                }
            }

        } catch (EncryptionException e) {
            throw new PersistentStoreException("Error encrypting the password for: " + user, e);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error inserting user: " + user, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }
    
    public void deleteUser(Sync4jUser user) throws AdminException, PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(sql[SQL_DELETE_USER]);

            stmt.setString(1, user.getUsername());

            stmt.executeUpdate();

            stmt.close();

            stmt = conn.prepareStatement(sql[SQL_DELETE_USER_ROLES]);

            stmt.setString(1, user.getUsername());

            stmt.executeUpdate();

            stmt.close();

            //delete pricipal associated
            deletePrincipal(user);

        } catch (SQLException e) {
            throw new PersistentStoreException("Error deleting user " + user, e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    private void deletePrincipal(Sync4jUser user) throws PersistentStoreException {
        Connection coreConn = null;
        Connection userConn = null;
        PreparedStatement coreStmt = null;

        PreparedStatement stmtCM = null;
        PreparedStatement stmtLS = null;
        PreparedStatement stmtDC = null;

        ResultSet coreRs = null;

        try {

            coreConn = ds.getConnection();

            coreStmt = coreConn.prepareStatement(sql[SQL_SELECT_USER_PRINCIPALS]);

            coreStmt.setString(1, user.getUsername());

            coreRs = coreStmt.executeQuery();

            userConn = userDataSource.getRoutedConnection(user.getUsername());

            while (coreRs.next()) {
                String principal = coreRs.getString(1);

                stmtCM = userConn.prepareStatement(sql[SQL_DELETE_CLIENT_MAPPING]);
                stmtCM.setString(1, principal);
                stmtCM.executeUpdate();

                DBTools.close(null, stmtCM, null);

                stmtLS = coreConn.prepareStatement(sql[SQL_DELETE_LAST_SYNC]);
                stmtLS.setString(1, principal);
                stmtLS.executeUpdate();
                
                DBTools.close(null, stmtLS, null);
                
                stmtDC = coreConn.prepareStatement(sql[SQL_DELETE_DEVICE_CONFIG]);
                stmtDC.setString(1, principal);

                stmtDC.executeUpdate();
                
                DBTools.close(null, stmtDC, null);     
            }
            DBTools.close(null, coreStmt, coreRs);

            coreStmt = coreConn.prepareStatement(sql[SQL_DELETE_PRINCIPAL]);

            coreStmt.setString(1, user.getUsername());

            coreStmt.executeUpdate();


        } catch (SQLException e) {
            throw new PersistentStoreException("Error deleting principal ", e);
        } finally {
            DBTools.close(coreConn, coreStmt, coreRs);
            DBTools.close(null, stmtCM, null);
            DBTools.close(null, stmtLS, null);
            DBTools.close(null, stmtDC, null);
            DBTools.close(userConn, null, null);
        }
    }

    /**
     * Select the number of users that satisfy the conditions specified in input
     *
     * @return int number of user
     */
    public int countUsers(Clause clause) throws AdminException, PersistentStoreException {
        PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int n = 0;

        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_COUNT_USERS];
            if (where.sql.length() > 0) {
                query += " where " + where.sql;
            }
            stmt = conn.prepareStatement(query);

            for(int i=0; i<where.parameters.length; ++i) {
                stmt.setObject(i+1, where.parameters[i]);
            }

            rs = stmt.executeQuery();
            while (rs.next()) {
                n = rs.getInt(1);
            }
            return n;

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading count users ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }
    
    /**
     * Verify if the user is the only administrator
     *
     * @return boolean true if the user is the only administrator
     */
    public boolean isUniqueAdministrator(Sync4jUser user) throws AdminException, PersistentStoreException {
        Connection        conn = null;
        PreparedStatement stmt = null;
        ResultSet         rs   = null;
        
        int n = 0;

        try {
            conn = ds.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_COUNT_ADMINISTRATORS];

            stmt = conn.prepareStatement(query);
            stmt.setString(1, user.getUsername());
            
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                n = rs.getInt(1);
            }
            
            if (n == 0){
                return true;            	
            } else {
            	return false;
            }


        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading count administrators ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }    	
    }

}
