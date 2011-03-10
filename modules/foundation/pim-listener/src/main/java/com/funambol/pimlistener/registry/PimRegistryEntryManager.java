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

package com.funambol.pimlistener.registry;

import java.util.List;

import com.funambol.pimlistener.registry.dao.PimRegistryEntryDao;
import com.funambol.pimlistener.service.PimListenerException;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;

/**
 * A pim registry entry manager
 * @version $Id: PimRegistryEntryManager.java,v 1.11 2008-05-18 16:00:54 nichele Exp $
 */
public class PimRegistryEntryManager {

    // ------------------------------------------------------------ Privare data

    /** The PimRegistryEntryDao */
    private PimRegistryEntryDao dao = null;

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of PimRegistryEntryManager */
    public PimRegistryEntryManager() throws PimListenerException {
        dao = new PimRegistryEntryDao();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns a list of the entries with the given userName
     * @param userName the userName
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return a list of the entries with the given userName
     */
    public List<PimRegistryEntry> getEntriesByUserName(String userName)
    throws PimListenerException {
        try {
            return dao.getEntriesByUserName(userName);
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error performing getEntriesByUserName " +
                " with userName '" + userName + "'", ex);
        }
    }

    /**
     * Returns a list with all entries
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return a list with all entries
     */
    public List<PimRegistryEntry> getEntries()
    throws PimListenerException {
        try {
            return dao.getEntries();
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error performing getEntriesBy", ex);
        }
    }

    /**
     * Returns the entry with the given id
     * @param id the entry id
     * @return the entry with the given id. <code>null</code> if not found
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     */
    public PimRegistryEntry getEntryById(long id)
    throws PimListenerException {
        try {
            PimRegistryEntry entry = dao.getEntryById(id);
            return entry;
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error performing getEntry with id: " + id, ex);
        }
    }

    /**
     * Disables the entry with the gived id
     * @param id the entry id
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return the number of updated entries
     */
    public int disableEntry(long id)
    throws PimListenerException {
        try {
            PimRegistryEntry entry = dao.getEntryById(id);
            if (entry == null) {
                return 0;
            }
            entry.setActive(false);
            entry.setStatus(RegistryEntryStatus.UPDATED);
            entry.setLastUpdate(System.currentTimeMillis());
            return dao.updatePimRegistrEntry(entry);
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error disabling entry: " + id, ex);
        }
    }

    /**
     * Enables the entry with the gived id
     * @param id the entry id
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return the number of updated entries
     */
    public int enableEntry(long id)
    throws PimListenerException {
        try {
            PimRegistryEntry entry = dao.getEntryById(id);
            if (entry == null) {
                return 0;
            }
            entry.setActive(true);
            entry.setStatus(RegistryEntryStatus.UPDATED);
            entry.setLastUpdate(System.currentTimeMillis());
            return dao.updatePimRegistrEntry(entry);
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error enabling entry: " + id, ex);
        }
    }

    /**
     * Marks as deleted the entry with the given id
     * @param id the entry id
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return the number of updated entries
     */
    public int markAsDeleted(long id)
    throws PimListenerException {
        try {
            PimRegistryEntry entry = dao.getEntryById(id);
            if (entry == null) {
                return 0;
            }
            entry.setStatus(RegistryEntryStatus.DELETED);
            entry.setLastUpdate(System.currentTimeMillis());
            return dao.updatePimRegistrEntry(entry);
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error marking entry with id '" +
                                           id + "' as deleted", ex);
        }
    }

    /**
     * Updated the given entry.
     * @param entry the entry to update
     * @throws com.funambol.pimlistener.service.PimListenerException if an error occurs
     * @return the number of updated entries.
     */
    public int updateEntry(PimRegistryEntry entry)
    throws PimListenerException {

        PimRegistryEntry previousEntry = null;
        int updatedRows = 0;
        try {

            previousEntry = dao.getEntryById(entry.getId());
            if (previousEntry == null) {
                return 0;
            }

            entry.setStatus(RegistryEntryStatus.UPDATED);
            entry.setLastUpdate(System.currentTimeMillis());

            updatedRows = dao.updatePimRegistrEntry(entry);
            return updatedRows;

        } catch (DataAccessException ex) {
            throw new PimListenerException("Error updating entry (" + entry + ")", ex);
        }
    }

    /**
     * Creates a new entry with the given values.
     *
     * @param userName the userName
     * @param period the period
     * @param active is the entry active ?
     * @param taskBeanFile the taskBeanFile
     * @param pushContacts should the contacts be pushed ?
     * @param pushCalendars should the calendars be pushed ?
     * @param pushNotes  should the notes be pushed ?
     * @throws com.funambol.pimlistener.service.PimListenerException if an error
     *         occurs
     * @return the id of the new entry
     */
    public long createEntry(String  userName,
                            long    period,
                            boolean active,
                            String  taskBeanFile,
                            boolean pushContacts,
                            boolean pushCalendars,
                            boolean pushNotes)
    throws PimListenerException {

        PimRegistryEntry entry = new PimRegistryEntry();
        entry.setUserName(userName);
        entry.setPeriod(period);
        entry.setActive(active);
        entry.setTaskBeanFile(taskBeanFile);
        entry.setStatus(RegistryEntryStatus.NEW);
        entry.setLastUpdate(System.currentTimeMillis());
        entry.setPushContacts(pushContacts);
        entry.setPushCalendars(pushCalendars);
        entry.setPushNotes(pushNotes);

        long idEntry = 0;
        try {
            idEntry = dao.insertRegistryEntry(entry);
        } catch (DataAccessException ex) {
            throw new PimListenerException("Error creating new entry", ex);
        }
        return idEntry;
    }
}
