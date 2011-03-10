/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.foundation.items.manager;

import java.util.List;

import java.sql.Timestamp;

import com.funambol.common.pim.note.Note;

import com.funambol.framework.tools.merge.MergeResult;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.exception.DAOException;

import com.funambol.foundation.items.dao.EntityDAO;
import com.funambol.foundation.items.dao.PIMNoteDAO;

import com.funambol.foundation.items.model.NoteWrapper;

import com.funambol.server.config.Configuration;

/**
 * Manager for note sync
 *
 * @version $Id$
 */
public class PIMNoteManager extends PIMEntityManager {

    //------------------------------------------------------------- protected data
    protected PIMNoteDAO dao;


    //------------------------------------------------------------- Constructors
    /**
     * @see PIMEntityDAO#PIMEntityDAO(String, String)
     */
    public PIMNoteManager(String userId) {
        this.dao = new PIMNoteDAO(userId);
        if (log.isTraceEnabled()) {
            log.trace("Created new PIMNoteManager for user ID " + userId);
        }
    }


    public void setDAO(PIMNoteDAO dao) {
        this.dao = dao;
    }
    //----------------------------------------------------------- Public methods
    /**
     * Updates a note.
     *
     * @param uid the UID of the note
     * @param c a Note object
     * @param t the last update time that will be forced as the last update time
     *          of the updated note
     * @return the UID of the note (it should be the same as the argument)
     * @throws EntityException
     */
    public String updateItem(String uid, Note note, Timestamp t)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Updating note with ID " + uid + " on the server, " + "setting " + t + " as its last update timestamp.");
        }

        NoteWrapper nw = new NoteWrapper(uid, dao.getUserId(), note);
        nw.setLastUpdate(t);

        try {
            return dao.updateItem(nw);
        } catch (DAOException dbae) {
            throw new EntityException("Error updating item.", dbae);
        }
    }

    /**
     * @see PIMNoteDAO#addItem(Note)
     */
    public String addItem(Note c, Timestamp t)
            throws EntityException {

        try {
            NoteWrapper nw = createNoteWrapper(c);
            nw.setLastUpdate(t);
            dao.addItem(nw);
            return nw.getId();
        } catch (DAOException dbae) {
            throw new EntityException("Error adding item.", dbae);
        }
    }

    /**
     * @see PIMNoteDAO#getItem(String)
     */
    public NoteWrapper getItem(String uid) throws EntityException {
        try {
            return dao.getItem(uid);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting item", dbae);
        }
    }

    /**
     * @see PIMNoteDAO#getTwinItems(Note)
     */
    public List getTwins(Note note)
            throws EntityException {
        try {
            return dao.getTwinItems(note);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting twins of an item.", dbae);
        }
    }

    public boolean mergeItems(String serverNoteID, Note clientNote,
            Timestamp t) throws EntityException {

        log.trace("PIMContactManager mergeItems begin");
        if (log.isTraceEnabled()) {
            log.trace("Merging server item " + serverNoteID
                    + " with its client counterpart.");
        }

        try {

            NoteWrapper serverNoteWrapper = dao.getItem(serverNoteID);
            Note serverNote = serverNoteWrapper.getNote();
            MergeResult mergeResult = clientNote.merge(serverNote);
            if (mergeResult.isSetBRequired()) {
                updateItem(serverNoteID, serverNote, t);
            }

            //
            // If funambol is not in the debug mode is not possible to print the
            // merge result because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    log.trace("MergeResult: " + mergeResult);
                }
            }

            return mergeResult.isSetARequired();

        } catch (Exception e) {

            log.error("SyncSource error merging the items", e);

            throw new EntityException("Error merging items. ", e);
        }
    }

    /**
     *
     * @param note the note we want to check.
     *
     * @return true if at least one field used for the twin search in the given
     * note contains meaningful data, false otherwise
     */

    
    public boolean isTwinSearchAppliableOn(Note note) {
        return this.dao.isTwinSearchAppliableOn(note);
    }


    //-------------------------------------------------------- Protected methods

    /**
     *
     * returns the PIMNoteEntityDAO
     * @return
     */
    protected EntityDAO getEntityDAO() {
        return dao;
    }

    //---------------------------------------------------------- Private methods
    private NoteWrapper createNoteWrapper(Note c) {

        if (log.isTraceEnabled()) {
            log.trace("Created a new NoteWrapper (UID not yet generated).");
        }
        return new NoteWrapper(null, dao.getUserId(), c);
    }

}
