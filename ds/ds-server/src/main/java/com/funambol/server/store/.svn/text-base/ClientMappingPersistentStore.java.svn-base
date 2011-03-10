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

import java.io.Serializable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import java.util.LinkedHashMap;
import java.util.Map;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.ClientMappingEntry;
import com.funambol.framework.server.store.BasePersistentStore;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.DBTools;

/**
 * This is the store for information regarding the synchronization process and
 * status. It persists the following classes:
 *
 * <ul>
 * <li>com.funambol.framework.server.ClientMapping</li>
 * </ul>
 *
 * @version $Id: ClientMappingPersistentStore.java,v 1.2 2008-05-22 10:58:06 nichele Exp $
 */
public class ClientMappingPersistentStore
extends    BasePersistentStore
implements PersistentStore, Serializable {

    // --------------------------------------------------------------- Constants
    public static final int SQL_INSERT_CLIENT_MAPPING     = 0;
    public static final int SQL_UPDATE_CLIENT_MAPPING     = 1;
    public static final int SQL_SELECT_CLIENT_MAPPING     = 2;
    public static final int SQL_DELETE_CLIENT_MAPPING     = 3;
    public static final int SQL_DELETE_ALL_CLIENT_MAPPING = 4;

    // -------------------------------------------------------------- Properties

    private String[] sql = {
        "insert into fnbl_client_mapping (principal, sync_source, guid, luid, last_anchor) values(?, ?, ?, ?,?)",
        "update fnbl_client_mapping set luid=?, last_anchor=? where principal=? and sync_source=? and guid=?",
        "select luid,guid,last_anchor from fnbl_client_mapping where principal=? and sync_source=?",
        "delete from fnbl_client_mapping where principal=? and sync_source=? and luid=?",
        "delete from fnbl_client_mapping where principal=? and sync_source=?"
    };

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Private data
    
    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    public boolean store(Object o)
    throws PersistentStoreException {
        if (o instanceof ClientMapping) {
            ClientMapping clientMapping = (ClientMapping) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            long principal = -1;
            String dbURI     = clientMapping.getDbURI();

            principal = clientMapping.getPrincipal().getId();

            assert(principal >= 0);
            assert(dbURI != null);

            try {
                if (clientMapping.isDeleted()) {

                    String[] luids = clientMapping.getDeletedLuids();
                    for (int i=0; i < luids.length; ++i) {

                        conn = userDataSource.getRoutedConnection(
                           clientMapping.getPrincipal().getUsername()
                        );

                        stmt = conn.prepareStatement(sql[SQL_DELETE_CLIENT_MAPPING]);
                        stmt.setLong(1, principal);
                        stmt.setString(2, dbURI  );
                        stmt.setString(3, luids[i]);
                        stmt.executeUpdate();

                        DBTools.close(conn, stmt, null);
                    }
                }

                if (clientMapping.isAdded()) {

                    String[] luids = clientMapping.getAddedLuids();

                    String guid = null;
                    String lastAnchor = null;
                    for (int i = 0; i < luids.length; ++i) {
                        guid = clientMapping.getMappedValueForLuid(luids[i]);
                        lastAnchor = clientMapping.getLastAnchor(guid);

                        add(clientMapping.getPrincipal(), dbURI, guid, luids[i], lastAnchor);
                    }
                }

                if (clientMapping.isModified()) {

                    String[] luids = clientMapping.getModifiedLuids();

                    String guid       = null;
                    String lastAnchor = null;
                    for (int i=0; i < luids.length; ++i) {
                        int n = -1;

                        guid       = clientMapping.getMappedValueForLuid(luids[i]);
                        lastAnchor = clientMapping.getLastAnchor(guid);

                        conn = userDataSource.getRoutedConnection(
                           clientMapping.getPrincipal().getUsername()
                        );
                        
                        stmt = conn.prepareStatement(sql[SQL_UPDATE_CLIENT_MAPPING]);
                        stmt.setLong  (3, principal);
                        stmt.setString(4, dbURI    );

                        stmt.setString(1, luids[i]  );
                        stmt.setString(2, lastAnchor);
                        stmt.setString(5, guid      );
                        n = stmt.executeUpdate();

                        DBTools.close(conn, stmt, null);

                        if (n == 0) {
                            //
                            // insert new mapping
                            //
                            add(clientMapping.getPrincipal(), dbURI, guid, luids[i], lastAnchor);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new PersistentStoreException("Error storing client mapping", e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    public boolean read(Object o)
    throws PersistentStoreException {
        if (o instanceof ClientMapping) {
            ClientMapping clientMapping = (ClientMapping) o;

            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;

            try {
                conn = userDataSource.getRoutedConnection(
                           clientMapping.getPrincipal().getUsername()
                       );
                conn.setReadOnly(true);

                stmt = conn.prepareStatement(sql[SQL_SELECT_CLIENT_MAPPING]);
                stmt.setLong  (1, clientMapping.getPrincipal().getId());
                stmt.setString(2, clientMapping.getDbURI());
                rs = stmt.executeQuery();

                Map<String, ClientMappingEntry> mapping =
                    new LinkedHashMap<String, ClientMappingEntry>();
                String guid = null;
                ClientMappingEntry mappingEntry = null;
                while (rs.next()) {
                    guid = rs.getString("guid");
                    mappingEntry = new ClientMappingEntry(guid,
                                                          rs.getString("luid"),
                                                          rs.getString("last_anchor"));
                    mapping.put(guid, mappingEntry);
                }
                clientMapping.initializeFromMapping(mapping);
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading mapping", e);
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

    public boolean delete(Object o) throws PersistentStoreException  {
        if (o instanceof ClientMapping) {
            ClientMapping clientMapping = (ClientMapping) o;

            Connection conn = null;
            PreparedStatement stmt = null;

            long principal = -1;
            String dbURI     = clientMapping.getDbURI();

            principal = clientMapping.getPrincipal().getId();

            assert(principal >= 0);
            assert(dbURI != null);

            try {

                conn = userDataSource.getRoutedConnection(
                   clientMapping.getPrincipal().getUsername()
                );

                stmt = conn.prepareStatement(sql[SQL_DELETE_ALL_CLIENT_MAPPING]);
                stmt.setLong(1, principal);
                stmt.setString(2, dbURI  );
                stmt.executeUpdate();
                    
            } catch (SQLException e) {
                throw new PersistentStoreException("Error deleting all client mapping", e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
            return true;
        }
        return false;
    }

    public Object[] read(Object o, Clause clause) throws PersistentStoreException
    {
        return null;
    }

    public int count(Object o, Clause clause) throws PersistentStoreException
    {
        return -1;
    }

    public boolean store(Object o, String operation) throws PersistentStoreException
    {
        return false;
    }

    private void add(Sync4jPrincipal principal, String dbURI, String guid, String luid, String lastAnchor)
    throws PersistentStoreException {

        Connection conn = null;
        PreparedStatement stmt = null;

        long principalId = principal.getId();
        String username = principal.getUsername();

        try {
            //
            // insert new mapping
            //
            conn = userDataSource.getRoutedConnection(username);

            stmt = conn.prepareStatement(sql[SQL_INSERT_CLIENT_MAPPING]);
            stmt.setLong  (1, principalId);
            stmt.setString(2, dbURI);
            stmt.setString(3, guid);
            stmt.setString(4, luid);
            if (lastAnchor == null) {
                stmt.setNull  (5, Types.VARCHAR);
            } else {
                stmt.setString(5, lastAnchor);
            }
            stmt.executeUpdate();

        } catch (SQLException e) {
            // [guid=133497546,luid=1096405329,anchor=0]
            throw new PersistentStoreException("Error adding client mapping [guid=" + guid + ",luid=" + luid + ",anchor=" + lastAnchor + "]", e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

}
