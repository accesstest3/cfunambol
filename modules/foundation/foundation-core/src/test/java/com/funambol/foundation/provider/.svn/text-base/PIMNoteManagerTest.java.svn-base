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

import java.sql.Timestamp;

import com.funambol.common.pim.note.Note;
import com.funambol.common.pim.utility.NoteUtils;
import com.funambol.foundation.items.manager.PIMNoteManager;
import com.funambol.foundation.items.model.NoteWrapper;

/**
 * PIMNoteManager test cases
 * @version $Id$
 */
public class PIMNoteManagerTest extends PIMNoteManagerForDAOTest {
    // --------------------------------------------------- Static initialization

    private static final String USERNAME = "userId";


    // ------------------------------------------------------------ Private data
    private PIMNoteManager manager;


    // ------------------------------------------------------------ Constructors
    public PIMNoteManagerTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    public void testAddNotes() throws Exception {
        manager = new PIMNoteManager(USERNAME);
        manager.setDAO(new PIMNoteDAOMock(USERNAME));
        Note note = new Note();
        note.getSubject().setPropertyValue("subject1");
        note.getTextDescription().setPropertyValue("textdescription1");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        note = new Note();
        note.getSubject().setPropertyValue("subject2");
        note.getTextDescription().setPropertyValue("textdescription2");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        note = new Note();
        note.getSubject().setPropertyValue("subject3");
        note.getTextDescription().setPropertyValue("textdescription4");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        assertEquals(3, manager.getAllItems().size());

    }

    // testing remove notes
    public void testRemoveNotes() throws Exception {

        manager = new PIMNoteManager(USERNAME);
        manager.setDAO(new PIMNoteDAOMock(USERNAME));
        Note note = new Note();
        note.getSubject().setPropertyValue("subject1");
        note.getTextDescription().setPropertyValue("textdescription1");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        note = new Note();
        note.getSubject().setPropertyValue("subject2");
        note.getTextDescription().setPropertyValue("textdescription2");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        note = new Note();
        note.getSubject().setPropertyValue("subject3");
        note.getTextDescription().setPropertyValue("textdescription3");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        assertEquals(3, manager.getAllItems().size());
        manager.removeItem("0", new Timestamp(System.currentTimeMillis()));
        assertEquals(2, manager.getAllItems().size());
        manager.removeItem("1", new Timestamp(System.currentTimeMillis()));
        assertEquals(1, manager.getAllItems().size());
        manager.removeItem("2", new Timestamp(System.currentTimeMillis()));
        assertEquals(0, manager.getAllItems().size());
    }
    // testing remove all notes
    public void testRemoveAllNotes() throws Exception {

        manager = new PIMNoteManager(USERNAME);
        manager.setDAO(new PIMNoteDAOMock(USERNAME));
        Note note = new Note();
        note.getSubject().setPropertyValue("subject1");
        note.getTextDescription().setPropertyValue("textdescription1");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));
        note = new Note();
        note.getSubject().setPropertyValue("subject2");
        note.getTextDescription().setPropertyValue("textdescription2");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        note = new Note();
        note.getSubject().setPropertyValue("subject3");
        note.getTextDescription().setPropertyValue("textdescription3");
        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        assertEquals(3, manager.getAllItems().size());
        manager.removeAllItems(new Timestamp(System.currentTimeMillis()));
        assertEquals(0, manager.getAllItems().size());
    }
    // testing get Note
    public void testGetNote() throws Exception {

        manager = new PIMNoteManager("userId");
        manager.setDAO(new PIMNoteDAOMock(USERNAME));
        Note note = new Note();
        note.getSubject().setPropertyValue("subject1");
        note.getTextDescription().setPropertyValue("textdescription1");
        note.getCategories().setPropertyValue("cat1, cat2, cat3");
        note.getFolder().setPropertyValue("folder1/folder2");
        note.getColor().setPropertyValue("0");
        note.getHeight().setPropertyValue("1");
        note.getWidth().setPropertyValue("2");
        note.getTop().setPropertyValue("3");
        note.getLeft().setPropertyValue("4");

        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        NoteWrapper notew = manager.getItem("0");

        assertEquals(notew.getNote(), note);
    }

    // testing update Note
    public void testUpdateNote() throws Exception {

        manager = new PIMNoteManager(USERNAME);
        manager.setDAO(new PIMNoteDAOMock(USERNAME));
        Note note = new Note();

        note.getSubject().setPropertyValue("subject1");
        note.getTextDescription().setPropertyValue("textdescription1");
        note.getCategories().setPropertyValue("cat1, cat2, cat3");
        note.getFolder().setPropertyValue("folder1/folder2");
        note.getColor().setPropertyValue("0");
        note.getHeight().setPropertyValue("1");
        note.getWidth().setPropertyValue("2");
        note.getTop().setPropertyValue("3");
        note.getLeft().setPropertyValue("4");

        manager.addItem(note, new Timestamp(System.currentTimeMillis()));

        NoteWrapper notew = manager.getItem("0");

        assertEquals(notew.getNote(), note);

        note.getSubject().setPropertyValue("subject2");
        manager.updateItem("0", note, new Timestamp(System.currentTimeMillis()));
        notew = manager.getItem("0");

        assertEquals(notew.getNote(), note);
    }

    // --------------------------------------------------------- Private methods
    private void assertEquals(Note expected, Note result){
        if (!NoteUtils.deepEquals(expected, result)) {
            fail();
        }
    }

}
