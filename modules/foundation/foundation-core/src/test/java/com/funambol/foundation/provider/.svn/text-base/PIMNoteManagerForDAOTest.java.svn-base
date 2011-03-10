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
import com.funambol.foundation.util.TestDef;
import com.funambol.tools.database.DBHelper;
import junit.framework.TestCase;
import org.dbunit.IDatabaseTester;

/**
 * Test class for <code>PIMNoteManager</code> .
 * This class relies upon the <code>PIMNoteDAOImpl</code> .
 * 
 * @author $Id: PIMNoteManagerForDAOTest.java,v 1.4 2008-05-18 11:36:42 nichele Exp $
 */
public class PIMNoteManagerForDAOTest  extends TestCase implements TestDef {
    
    // --------------------------------------------------------------- Constants

    private static final String USER_ID = DBHelper.USER_PART1;

    private static final String INITIAL_DB_DATASET_CORE =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/provider/PIMNoteManagerForDAOTest/dataset-coredb.xml";
    private static final String INITIAL_DB_DATASET_USER =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/provider/PIMNoteManagerForDAOTest/dataset-userdb.xml";

    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE,
                                                      USER_SCHEMA_SOURCE);

            //assertTrue checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private IDatabaseTester coreDbTester = null;
    private IDatabaseTester userDbTesterPart1 = null;

    private PIMNoteManager manager;

    // ------------------------------------------------------------ Constructors
    public PIMNoteManagerForDAOTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------- setUp/tearDown methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try {
            coreDbTester = DBHelper.createDatabaseTester(INITIAL_DB_DATASET_CORE, true);

            userDbTesterPart1 =
                DBHelper.createDatabaseTester(DBHelper.USER_PART1,
                                              INITIAL_DB_DATASET_USER,
                                              true);

        } catch(Exception ex) {
            freeDatabaseResources();
            throw new Exception("An error occurred while configuring database for the DBTest class.", ex);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        freeDatabaseResources();
    }
    
    // ------------------------------------------------------------ Test methods    
    // testing add Notes
    public void testAddNotes() throws Exception {    
        
        manager = new PIMNoteManager(USER_ID);

        int initialSize =
                manager.getAllItems().size();
       
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

        assertEquals(initialSize + 3, manager.getAllItems().size());
    }

    // testing remove notes
    public void testRemoveNotes() throws Exception {
        manager = new PIMNoteManager(USER_ID);
        int initialSize = manager.getAllItems().size();

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

        assertEquals(initialSize + 3, manager.getAllItems().size());
        manager.removeItem("1", new Timestamp(System.currentTimeMillis()));
        assertEquals(initialSize + 2, manager.getAllItems().size());
        manager.removeItem("2", new Timestamp(System.currentTimeMillis()));
        assertEquals(initialSize + 1, manager.getAllItems().size());
        manager.removeItem("3", new Timestamp(System.currentTimeMillis()));
        assertEquals(initialSize, manager.getAllItems().size());
    }
    // testing remove all notes
    public void testRemoveAllNotes() throws Exception {

        manager = new PIMNoteManager(DBHelper.USER_PART2);
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

        manager = new PIMNoteManager(USER_ID);
        
        Note expectedNote = new Note();
        expectedNote.getSubject().setPropertyValue("subject1");
        expectedNote.getTextDescription().setPropertyValue("textdescription1");
        expectedNote.getCategories().setPropertyValue("cat1, cat2, cat3");
        expectedNote.getFolder().setPropertyValue("folder1/folder2");
        expectedNote.getColor().setPropertyValue("0");
        expectedNote.getHeight().setPropertyValue("1");
        expectedNote.getWidth().setPropertyValue("2");
        expectedNote.getTop().setPropertyValue("3");
        expectedNote.getLeft().setPropertyValue("4");
        
        String id = manager.addItem(expectedNote, new Timestamp(System.currentTimeMillis()));
        NoteWrapper resultNoteWrapper = manager.getItem(id);
        
        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    // testing update Note
    public void testUpdateNote() throws Exception {

        manager = new PIMNoteManager(USER_ID);

        Note expectedNote = new Note();
        expectedNote.getSubject().setPropertyValue("subject1");
        expectedNote.getTextDescription().setPropertyValue("textdescription1");
        expectedNote.getCategories().setPropertyValue("cat1, cat2, cat3");
        expectedNote.getFolder().setPropertyValue("folder1/folder2");
        expectedNote.getColor().setPropertyValue("0");
        expectedNote.getHeight().setPropertyValue("1");
        expectedNote.getWidth().setPropertyValue("2");
        expectedNote.getTop().setPropertyValue("3");
        expectedNote.getLeft().setPropertyValue("4");

        String id = manager.addItem(expectedNote, new Timestamp(System.currentTimeMillis()));
        NoteWrapper resultNoteWrapper = manager.getItem(id);

        assertEquals(resultNoteWrapper.getNote(), expectedNote);
        
        expectedNote.getSubject().setPropertyValue("subject2");
        manager.updateItem(id, expectedNote, new Timestamp(System.currentTimeMillis()));
        resultNoteWrapper = manager.getItem(id);

       assertEquals(resultNoteWrapper.getNote(), expectedNote);
    }

    // --------------------------------------------------------- Private methods
    private static void assertEquals(Note expected, Note result){
        if (!NoteUtils.deepEquals(expected, result)) {
            fail();
        }
    }

    private void freeDatabaseResources()  {
        DBHelper.closeConnection(coreDbTester);
        DBHelper.closeConnection(userDbTesterPart1);
    }

}
