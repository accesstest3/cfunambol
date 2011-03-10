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
package com.funambol.foundation.provider;

import com.funambol.foundation.items.dao.*;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.NoteWrapper;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * 
 */
public class PIMNoteDAOMock extends PIMNoteDAO {

    /** Mocked backend. */
    private HashMap<String, NoteWrapper> notesWrappers =
            new HashMap<String, NoteWrapper>();

    public PIMNoteDAOMock(String userId) {
        super(userId);

    }

    @Override
    public List getAllItems() throws DAOException {
        List<String> items = new ArrayList<String>();
        for (NoteWrapper nw : notesWrappers.values()) {
            items.add(nw.getId());
        }
        return items;
    }

    @Override
    public void removeItem(String uid, Timestamp at) throws DAOException {
        notesWrappers.remove(uid);
    }

    @Override
    public void removeAllItems(Timestamp at) throws DAOException {
        notesWrappers.clear();
    }
  
    @Override
    public void addItem(NoteWrapper nw) throws DAOException {
        nw.setId(getNextID());
        notesWrappers.put(nw.getId(), nw);
    }

    @Override
    public NoteWrapper getItem(String uid) throws DAOException {
        return notesWrappers.get(uid);
    }

    @Override
    public String updateItem(NoteWrapper nw) throws DAOException {

        notesWrappers.remove(nw.getId());
        notesWrappers.put(nw.getId(), nw);
        return nw.getId();
    }

    @Override
    public String getNextID() throws DAOException {
        return String.valueOf(notesWrappers.size());
    }
}
