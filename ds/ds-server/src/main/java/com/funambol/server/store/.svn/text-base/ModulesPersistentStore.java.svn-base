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
import java.util.Map;
import java.sql.*;

import com.funambol.framework.filter.Clause;

import com.funambol.framework.server.Sync4jSource;
import com.funambol.framework.server.Sync4jSourceType;
import com.funambol.framework.server.store.*;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.server.Sync4jModule;
import com.funambol.framework.server.Sync4jConnector;

/**
 *
 *
 *
 * @version $Id: ModulesPersistentStore.java,v 1.2 2008-05-22 10:57:31 nichele Exp $
 *
 */
public class ModulesPersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static final int SQL_SELECT_ALL_MODULES_NAME          = 0;
    public static final int SQL_GET_MODULE                       = 1;
    public static final int SQL_SELECT_MODULE_CONNECTOR          = 2;
    public static final int SQL_SELECT_CONNECTOR_SYNCSOURCETYPE  = 3;
    public static final int SQL_SELECT_SYNCSOURCETYPE_SYNCSOURCE = 4;
    public static final int SQL_SELECT_SYNCSOURCETYPE            = 5;

    // -------------------------------------------------------------- Properties

    private String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ------------------------------------------------------------ Private data
    // ------------------------------------------------------------ Constructors
    // ---------------------------------------------------------- Public methods

    public boolean delete(Object o)
    throws PersistentStoreException {
        return false;
    }

    public Object[] delete(Class objClass) throws PersistentStoreException
    {
        return new Object[0];
    }

    public boolean store(Object o, String operation) throws PersistentStoreException {
        return false;
    }

    public boolean store(Object o)
    throws PersistentStoreException {
        return false;
    }

    public boolean read(Object o)
    throws PersistentStoreException {
        if (o instanceof Sync4jModule) {
            readModule((Sync4jModule) o);
            return true;
        } else if (o instanceof Sync4jSourceType) {
            readSyncSourceType((Sync4jSourceType)o);
        }

        return false;
    }

    public Object[] read(Class objClass) throws PersistentStoreException {
        if (objClass.getName().equals(Sync4jModule.class.getName())) {
            return readModulesName();
        }
        return null;
    }

    /**
     * Read all informations
     * @param o whichever object Sync
     * @param clause condition where for select - NOT USED
     *
     * @return Object[] array of object
     */
    public Object[] read(Object o, Clause clause) throws PersistentStoreException {
        if (o instanceof Sync4jSourceType) {
            return readSyncSource((Sync4jSourceType) o);
        }

        return null;
    }

    public int count(Object o, Clause clause) throws PersistentStoreException {
        return -1;
    }

    // --------------------------------------------------------- Private methods
    private Object[] readModulesName()
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_ALL_MODULES_NAME]);

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(
                    new Sync4jModule(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3)
                    )
                );
            }

            return ret.toArray(new Sync4jModule[ret.size()]);
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading modules name", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    private void readModule(Sync4jModule module)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_GET_MODULE]);
            stmt.setString(1, module.getModuleId());

            rs = stmt.executeQuery();

            if (rs.next() == false) {
                throw new NotFoundException("Module not found for "
                + module.getModuleId()
                );
            }
            module.setModuleId(rs.getString(1));
            module.setModuleName(rs.getString(2));
            module.setDescription(rs.getString(3));

            //get all connectors for this module
            Sync4jConnector[] syncConnectors = (Sync4jConnector[])this.readModuleConnectors(module);
            module.setConnectors(syncConnectors);

            //for each SyncConnector select the SyncSourceType
            for (int i=0; (syncConnectors != null) && i<syncConnectors.length; i++) {
                Sync4jConnector sc = syncConnectors[i];
                Sync4jSourceType[] sst = (Sync4jSourceType[])this.readSyncSourceType(sc);

                sc.setSourceTypes(sst);
            }

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the module " + module, e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    private Object[] readModuleConnectors(Sync4jModule module)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_MODULE_CONNECTOR]);
            stmt.setString(1, module.getModuleId());

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(
                    new Sync4jConnector(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                    )
                );
            }

            return ret.toArray(new Sync4jConnector[ret.size()]);

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the connectors ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    private Object[] readSyncSourceType(Sync4jConnector sc)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_CONNECTOR_SYNCSOURCETYPE]);
            stmt.setString(1, sc.getConnectorId());

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(
                    new Sync4jSourceType(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                    )
                );
            }

            return ret.toArray(new Sync4jSourceType[ret.size()]);

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the SyncSourceType ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    private Object[] readSyncSource(Sync4jSourceType sst)
    throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        ArrayList ret = new ArrayList();

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_SYNCSOURCETYPE_SYNCSOURCE]);
            stmt.setString(1, sst.getSourceTypeId());

            rs = stmt.executeQuery();

            while (rs.next()) {
                ret.add(
                    new Sync4jSource(
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4)
                    )
                );
            }

            return ret.toArray(new Sync4jSource[ret.size()]);

        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the SyncSource ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }
    
    /**
     * Read the Sync4jSourceType with the specificated id type.
     *
     */
    private void readSyncSourceType(Sync4jSourceType sst)
    throws PersistentStoreException {
        
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            stmt = conn.prepareStatement(sql[SQL_SELECT_SYNCSOURCETYPE]);
            stmt.setString(1, sst.getSourceTypeId());
            rs=stmt.executeQuery();

            if (rs.next() == false) {
                throw new NotFoundException("Sync4jSourceType not found for "
                + sst.getSourceTypeId()
                );
            }
            
            sst.setSourceTypeId(rs.getString(1));            
            sst.setDescription (rs.getString(2));
            sst.setClassName   (rs.getString(3));
            sst.setAdminClass  (rs.getString(4));
            
        } catch (SQLException e) {
            throw new PersistentStoreException("Error reading the Sync4jSourceType ", e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }
}
