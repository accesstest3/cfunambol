/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.pimlistener.registry.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;

import com.funambol.pushlistener.service.config.PushListenerConfiguration;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;
import com.funambol.pushlistener.service.registry.dao.RegistryDao;

import com.funambol.pimlistener.registry.PimRegistryEntry;
import com.funambol.pimlistener.service.PimListenerException;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;

import com.funambol.server.db.RoutingDataSource;

/**
 * DAO to handle pim registry entries.
 *
 * @version $Id: PimRegistryEntryDao.java,v 1.13 2008-05-18 16:01:11 nichele Exp $
 */
public class PimRegistryEntryDao {

    // --------------------------------------------------------------- Constants

    private static final String TABLE_PIM_REGISTRY = "fnbl_pim_listener_registry";

    private static final String SQL_READ_ENTRIES = "select r.id, r.period, r.active, "
            + "r.task_bean_file, r.last_update, r.status, "
            + "pr.username, pr.push_contacts, pr.push_calendars, pr.push_notes "
            + "from {0} r, " + TABLE_PIM_REGISTRY + " pr where r.id = pr.id";

    private static final String SQL_READ_ENTRIES_BY_USERNAME = SQL_READ_ENTRIES
            + " and pr.username = ? ";

    private static final String SQL_READ_ENTRY_BY_ID = SQL_READ_ENTRIES + " and r.id = ? ";

    private static final String SQL_DELETE_ENTRY = "delete from " +
            TABLE_PIM_REGISTRY + " where id = ?";

    private static final String SQL_UPDATE_ENTRY = "update " + TABLE_PIM_REGISTRY +
            " set username = ?, push_contacts = ?, push_calendars = ?, push_notes = ? where id = ?";

    private static final String SQL_INSERT_ENTRY = "insert into " +
            TABLE_PIM_REGISTRY + " (id, username, push_contacts, push_calendars, push_notes)" +
            " values (?,?,?,?,?)";

    private static final String SQL_ORDER_CLAUSE = " order by id";

    /** JNDI Name for core datasource */
    private static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";

    // -------------------------------------------------------------Private data

    /**
     * The query to read all entries
     */
    private String queryReadEntries                 = null;

    /**
     * The query to read all entries associated to an user
     */
    private String queryReadEntriesByUserName       = null;

    /**
     * The query to read an entry
     */
    private String queryReadEntry                   = null;

    /**
     * The RegistryDao
     */
    private RegistryDao registryDao = null;

    /** The core datasource */
    private DataSource coreDataSource = null;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a PimRegistryEntryDao
     */
    public PimRegistryEntryDao() throws PimListenerException {
        super();

        String registryTableName = PushListenerConfiguration.getPushListenerConfigBean().getRegistryTableName();

        registryDao = new RegistryDao(registryTableName, 
                                      PushListenerConfiguration.getPushListenerConfigBean().getRegistryEntriesIdSpace());

        queryReadEntries = MessageFormat.format(SQL_READ_ENTRIES,
                new Object[] {registryTableName})
                +  SQL_ORDER_CLAUSE;

        queryReadEntriesByUserName = MessageFormat.format(SQL_READ_ENTRIES_BY_USERNAME,
                new Object[] {registryTableName})
                +  SQL_ORDER_CLAUSE;

        queryReadEntry   = MessageFormat.format(SQL_READ_ENTRY_BY_ID,
                new Object[] {registryTableName})
                +  SQL_ORDER_CLAUSE;

        try {

            coreDataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);

        } catch (Exception e){
            throw new PimListenerException("Error creating PimRegistryEntryDAO Object ", e);
        }
    }
    // ---------------------------------------------------------- Public Methods

    /**
     * Returns a list with all pim registry entries
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return the read entries
     */
    public List<PimRegistryEntry> getEntries()
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<PimRegistryEntry> entries = new ArrayList<PimRegistryEntry>();

        try {

            con = coreDataSource.getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(queryReadEntries);

            rs = ps.executeQuery();

            while (rs.next()) {
                entries.add(resultSetToPimRegistryEntry(rs));
            }

        } catch (Exception e) {

            throw new DataAccessException("Error reading entries", e);

        } finally {
            DBTools.close(con, ps, rs);
        }

        return entries;
    }

    /**
     * Returns a list with all pim registry entries with the given userName
     * @param userName the userName
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return the read entries
     */
    public List<PimRegistryEntry> getEntriesByUserName(String userName)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<PimRegistryEntry> entries = new ArrayList<PimRegistryEntry>();

        try {

            con = coreDataSource.getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(queryReadEntriesByUserName);

            ps.setString(1, userName);
            rs = ps.executeQuery();

            while (rs.next()) {
                entries.add(resultSetToPimRegistryEntry(rs));
            }

        } catch (Exception e) {

            throw new DataAccessException("Error reading entries with userName: "
                    + userName, e);

        } finally {
            DBTools.close(con, ps, rs);
        }

        return entries;
    }

    /**
     * Returns the pim registry entry with the given id
     * @return the read entry
     * @param id the id of the pim registry entry
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public PimRegistryEntry getEntryById(long id)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        PimRegistryEntry entry = null;

        try {

            con = coreDataSource.getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(queryReadEntry);
            ps.setLong(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                entry = resultSetToPimRegistryEntry(rs);
            }

        } catch (Exception e) {

            throw new DataAccessException("Error reading entry with id: " + id, e);

        } finally {
            DBTools.close(con, ps, rs);
        }

        return entry;
    }

    /**
     * Deletes the pim entry with the given id
     * @return true if the entry is deleted, false otherwise
     * @param id the id of the entry to delete
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public boolean deletePimRegistryEntry(long id) throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsDeleted = 0;

        try {

            con = coreDataSource.getConnection();

            ps = con.prepareStatement(SQL_DELETE_ENTRY);
            ps.setLong(1, id);
            rowsDeleted = ps.executeUpdate();

            if (rowsDeleted > 0) {
                registryDao.deleteRegistryEntry(id);
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {

            throw new DataAccessException("Error deleting the entry with id: " + id, e);

        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Updates the given entry
     * @param entry the entry to updated
     * @return the number of updated rows
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public int updatePimRegistrEntry(PimRegistryEntry entry) throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsUpdated = 0;
        try {

            con = coreDataSource.getConnection();

            ps = con.prepareStatement(SQL_UPDATE_ENTRY);

            ps.setString(1, entry.getUserName());
            ps.setString(2, entry.isPushContacts()  ? "Y" : "N" );
            ps.setString(3, entry.isPushCalendars() ? "Y" : "N" );
            ps.setString(4, entry.isPushNotes()     ? "Y" : "N" );
            ps.setLong  (5, entry.getId());

            rowsUpdated = ps.executeUpdate();

            registryDao.updateRegistryEntry(entry);

        } catch (Exception e) {

            throw new DataAccessException(e);

        } finally {

            DBTools.close(con, ps, null);
        }
        return rowsUpdated;
    }

    /**
     * Inserts the given entry. The entry is also updated with the generated id
     * used to insert the entry
     * @param entry the entry to updated
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return the generated id to insert the new entry
     */
    public long insertRegistryEntry(PimRegistryEntry entry) throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;

        long              idRegistry = 0;

        try {
            idRegistry = registryDao.insertRegistryEntry(entry);

            con = coreDataSource.getConnection();

            ps = con.prepareStatement(SQL_INSERT_ENTRY);

            ps.setLong   (1, idRegistry);
            ps.setString(2, entry.getUserName());
            ps.setString(3, entry.isPushContacts()  ? "Y" : "N" );
            ps.setString(4, entry.isPushCalendars() ? "Y" : "N" );
            ps.setString(5, entry.isPushNotes()     ? "Y" : "N" );

            ps.executeUpdate();

            entry.setId(idRegistry);
            return idRegistry;

        } catch (Exception e) {
            //
            // Resetting the id
            //
            entry.setId(0);
            throw new DataAccessException(e);

        } finally {
            DBTools.close(con, ps, null);
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates a PimRegistryEntry reading the given ResultSet
     * @param rs the resultSet
     * @return the PimRegistryEntry
     * @throws java.sql.SQLException if an error occurs
     */
    private PimRegistryEntry resultSetToPimRegistryEntry(ResultSet rs)
    throws SQLException {

        PimRegistryEntry entry = new PimRegistryEntry();

        entry.setId            (rs.getLong(1));
        entry.setPeriod        (rs.getLong(2));
        entry.setActive        ("Y".equalsIgnoreCase(rs.getString(3)));
        entry.setTaskBeanFile  (rs.getString(4));
        entry.setLastUpdate    (rs.getLong(5));

        String s = rs.getString(6);
        if (s == null || s.length() == 0) {
            entry.setStatus(RegistryEntryStatus.UNKNOWN);
        }
        if (RegistryEntryStatus.NEW.equals(s)) {
            entry.setStatus(RegistryEntryStatus.NEW);
        } else if (RegistryEntryStatus.UPDATED.equals(s)) {
            entry.setStatus(RegistryEntryStatus.UPDATED);
        } else if (RegistryEntryStatus.DELETED.equals(s)) {
            entry.setStatus(RegistryEntryStatus.DELETED);
        } else {
            entry.setStatus(RegistryEntryStatus.UNKNOWN);
        }

        entry.setUserName     (rs.getString(7));
        entry.setPushContacts("Y".equalsIgnoreCase(rs.getString(8)));
        entry.setPushCalendars("Y".equalsIgnoreCase(rs.getString(9)));
        entry.setPushNotes("Y".equalsIgnoreCase(rs.getString(10)));

        return entry;
    }

}
