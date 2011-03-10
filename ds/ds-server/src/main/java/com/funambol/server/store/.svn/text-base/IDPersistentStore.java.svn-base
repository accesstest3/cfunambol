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
import java.util.Map;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.server.store.ConfigPersistentStoreException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.BasePersistentStore;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.server.ID;

/**
 * <br/>This is the store for persistent information regarding ID
 * It persists the following classes:
 * <ul>
 * <li>com.funambol.framework.server.GUID</li>
 * </ul>
 *
 * @deprecated This store was used from <code>com.funambol.server.tools.IdSpaceGenerator</code>
 *             to read the ids. This generator is replaced starting with v6 by
 *             <code>com.funambol.server.tools.id.DBIDGenerator</code>
 *             See the design document for the right way to use this new generator
 *
 * @version $Id: IDPersistentStore.java,v 1.2 2008-05-22 10:52:37 nichele Exp $
 */
public class IDPersistentStore 
extends BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static final int SQL_GET_ID    = 0;
    public static final int SQL_UPDATE_ID = 1;

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
        return false;
    }

    public boolean store(Object o, String operation) throws PersistentStoreException {
        return false;
    }

    /**
    * Store the given ID.
    *
    * @param o the Object that must be a ID object
    * @throws PersistentStoreException
    */
    public boolean store(Object o) throws PersistentStoreException {
        if (o instanceof ID) {

            ID id = (ID) o;
            String idSpace = id.getIdSpace();
            int counter    = id.getValue();

            if (idSpace == null) {
                throw new PersistentStoreException("Id space must be not null");
            }

            Connection conn        = null;
            PreparedStatement stmt = null;
            ResultSet rs           = null;

            try {
                conn = coreDataSource.getConnection();

                stmt = conn.prepareStatement(sql[SQL_UPDATE_ID]);
                stmt.setInt   (1, counter);
                stmt.setString(2, idSpace);
                stmt.executeUpdate();

            } catch (SQLException e) {
                e.printStackTrace();
                throw new PersistentStoreException("Error storing the counter " + idSpace, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }

            return true;
        }
        return false;

    }

    /**
     * Read the id from the data store.
     * @param o The object that must be a ID for this method
     * @throws PersistentException in case of error reading the data store
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (o instanceof ID) {

            ID id = (ID) o;
            String idSpace = id.getIdSpace();

            if (idSpace == null) {
                throw new PersistentStoreException("Id space must be not null");
            }

            Connection conn        = null;
            PreparedStatement stmt = null;
            ResultSet rs           = null;

            try {
                conn = coreDataSource.getConnection();

                int counter = 0;

                stmt = conn.prepareStatement(sql[SQL_GET_ID]);
                stmt.setString(1, id.getIdSpace());
                rs = stmt.executeQuery();

                if (rs.next() == false) {
                    throw new NotFoundException("Counter not found for "
                                                + idSpace);
                }

                counter = rs.getInt(1) + 1;

                DBTools.close(null, stmt, rs);

                stmt = conn.prepareStatement(sql[SQL_UPDATE_ID]);
                stmt.setInt   (1, counter);
                stmt.setString(2, idSpace);
                stmt.executeUpdate();

                id.setValue(counter);

            } catch (SQLException e) {
                e.printStackTrace();
                throw new PersistentStoreException("Error reading the counter " + idSpace, e);
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

    public Object[] read(Object o, Clause clause) throws PersistentStoreException {
        return null;
    }

    public int count(Object o, Clause clause) throws PersistentStoreException {
        if (o instanceof ID) {
            return 0;
        }
        return -1;
    }

}

