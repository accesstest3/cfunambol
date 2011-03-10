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
import javax.sql.DataSource;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.server.store.*;

/**
 * This class is base class for SQL storage. Each extension of this
 * class MUST respect the usage of the first 2 queries are for the
 * counter.
 *
 * @version $Id: UtilsPersistentStore.java,v 1.1.1.1 2008-02-21 23:36:02 stefano_fornari Exp $
 *
 * @deprecated starting from v6, this class is deprecated (maybe no more used in the
 * server starting from v3, surely no more used in v6)
 *
 */
public class UtilsPersistentStore {

    // --------------------------------------------------------------- Constants

    public static final int SQL_GET_COUNTER                  = 0;
    public static final int SQL_UPDATE_COUNTER               = 1;

    // -------------------------------------------------------------- Properties

    protected String idSpace = null;

    public void setIdSpace(String idSpace) {
        this.idSpace = idSpace;
    }

    public String getIdSpace() {
        return this.idSpace;
    }

    protected String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Private data
    // ------------------------------------------------------------ Constructors
    // ---------------------------------------------------------- Public methods

    /**
     * Read the counter of the specific id in order to use it like primary key
     * in case of inserting a new record into data store.
     *
     * @throws PersistentException in case of error reading the data store
     */
    protected int readCounter(Connection conn) throws PersistentStoreException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        int counter = 0;

        try {
            stmt = conn.prepareStatement(sql[SQL_GET_COUNTER]);
            rs = stmt.executeQuery();

            if (rs.next() == false) {
                throw new NotFoundException("Counter not found for "
                + idSpace);
            }
            counter = rs.getInt(1) + 1;

            rs.close(); rs = null;
            stmt.close(); stmt = null;

            stmt = conn.prepareStatement(sql[SQL_UPDATE_COUNTER]);
            stmt.setInt(1, counter);
            stmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new PersistentStoreException("Error reading the counter " + counter, e);
        } finally {
            DBTools.close(null, stmt, rs);
        }
        return counter;
    }
}

