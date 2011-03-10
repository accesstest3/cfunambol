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
package com.funambol.foundation.items.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.RowFilterTable;


import org.apache.commons.lang.StringUtils;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.note.Note;
import com.funambol.common.pim.utility.NoteUtils;
import com.funambol.foundation.items.model.NoteWrapper;
import com.funambol.foundation.items.model.NoteWrapperAsserts;
import com.funambol.foundation.util.TestDef;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.tools.database.DBHelper;
import junit.framework.TestCase;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.IRowValueProvider;
import org.dbunit.dataset.filter.IRowFilter;

/**
 * Test class for <code>PIMNoteDAO</code> .
 * @author $Id: PIMNoteDAOTest.java,v 1.7 2008-08-22 10:47:40 piter_may Exp $
 */
public class PIMNoteDAOTest extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants
    private static final String TABLE_FNBL_PIM_NOTE = "fnbl_pim_note";

    protected static final String USER_ID = DBHelper.USER_PART1;

    protected static final String INITIAL_DB_DATASET_CORE =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMNoteDAO/dataset-coredb.xml";
    protected static final String INITIAL_DB_DATASET_USER =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMNoteDAO/dataset-userdb.xml";

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
   
    // ---------------------------------------------------------- Protected data
   
    // ------------------------------------------------------------ Private data
    private int BIG_SUBJECT_SIZE    = PIMNoteDAO.SQL_SUBJECT_DIM * 2;
    private int BIG_BODY_SIZE       = PIMNoteDAO.SQL_TEXTDESCRIPTION_DIM * 2;
    private int BIG_FOLDER_SIZE     = PIMNoteDAO.SQL_FOLDER_DIM * 2;
    private int BIG_CATEGORIES_SIZE = PIMNoteDAO.SQL_CATEGORIES_DIM * 2;

    private PIMNoteDAO pimNoteDao;
    protected IDatabaseTester coreDbTester = null;
    protected IDatabaseTester userDbTesterPart1 = null;

    // ------------------------------------------------------------ Constructors
    public PIMNoteDAOTest(String testName) {
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

        pimNoteDao = new PIMNoteDAO(USER_ID);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        freeDatabaseResources();
    }
    // ------------------------------------------------------------ Test methods

    public void testGetItem() throws Exception {

        Note note = new Note();
        note.setSubject         (new Property("subject 2"));
        note.setTextDescription (new Property("This is the body of the note 2."));
        note.setCategories      (new Property("Business, Competition, Favorites"));
        note.setFolder          (new Property("DEFAULT_FOLDER"));
        note.setColor           (new Property("0"));
        note.setHeight          (new Property("100"));
        note.setWidth           (new Property("200"));
        note.setTop             (new Property("10"));
        note.setLeft            (new Property("20"));

        NoteWrapper expected = new NoteWrapper("2", USER_ID, note);
        expected.setStatus('N');

        NoteWrapper result = pimNoteDao.getItem("2");

        NoteWrapperAsserts.assertEquals(expected, result);
    }

    public void testGetAllItems() throws Exception {

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));

        List<String> resultItemsIds = pimNoteDao.getAllItems();

        assertEquals(expectedItems.getRowCount(), resultItemsIds.size());

        for (int i = 0; i < expectedItems.getRowCount(); i++) {
            String expectedId = (String) expectedItems.getValue(i, "id");
            String resultId = resultItemsIds.get(i);
            assertEquals(expectedId, resultId);
        }

    }

    public void testRemoveItem() throws Exception {

        int index = 100;

        Note note = new Note();
        note.setSubject(new Property("subject " + 100));
        note.setTextDescription(new Property("This is the body of the note " + index));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);

        pimNoteDao.addItem(nw);
        pimNoteDao.removeItem(uid, null);
        nw = pimNoteDao.getItem(uid);

        assertEquals(SyncItemState.DELETED, nw.getStatus());
    }

    public void testRemoveAllItems() throws Exception {
        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));

        pimNoteDao.removeAllItems(null);

        for (int i = 0; i < expectedItems.getRowCount(); i++) {
            String id = (String) expectedItems.getValue(i, PIMNoteDAO.SQL_FIELD_ID);
            NoteWrapper expected = pimNoteDao.getItem(id);
            assertEquals(expected.getStatus(), SyncItemState.DELETED);
        }

    }

    public void testGetItemState() throws Exception {
        // @TODO
    }

    public void testGetChangedItems() throws Exception {

       Timestamp since = new Timestamp(100);
       Timestamp to = new Timestamp(200);

       List<String>[] changedItems = pimNoteDao.getChangedItemsByLastUpdate(since, to);
       assertNotNull(changedItems);
       assertEquals(3, changedItems.length);

       assertEquals(6, changedItems[0].size());
       assertEquals(2, changedItems[1].size());
       assertEquals(1, changedItems[2].size());

        for (int i = 0; i < changedItems[1].size(); i++) {
            NoteWrapper resultNoteWrapper = pimNoteDao.getItem(changedItems[1].get(i));
            assertEquals('U', resultNoteWrapper.getStatus());
        }
    }
    
    /**
     * Add a note with every fields filled.
     */
    public void testAddItem_1() throws Exception {

        int index = 100;

        Note note = new Note();
        note.setSubject         (new Property("subject " + index));
        note.setTextDescription (new Property("This is the body of the note " + index));
        note.setCategories      (new Property("cat1, cat2, cat3"));
        note.setFolder          (new Property("DEFAULT_FOLDER\\aaaa\\\\bbbb"));
        note.setColor           (new Property("1"));
        note.setHeight          (new Property("11"));
        note.setWidth           (new Property("22"));
        note.setTop             (new Property("110"));
        note.setLeft            (new Property("220"));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        NoteWrapper result = pimNoteDao.getItem(uid);
        NoteWrapper expected = nw;

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));
        assertEquals(expectedItems.getRowCount() + 1, pimNoteDao.getAllItems().size());

        NoteWrapperAsserts.assertEquals(expected, result);
    }

    /**
     * Add a note with one filed empty.
     *
     * NOTE: "empty field" means "property with no value given"
     *   Example:
     *     subject = new Property();
     *     subject = new Property(null);
     */
    public void testAddItem_2() throws Exception {

        int index = 100;

        Note note = new Note();
        note.setSubject         (new Property());
        note.setTextDescription (new Property("This is the body of the note " + index));
        note.setCategories      (new Property("cat1, cat2, cat3"));
        note.setFolder          (new Property("DEFAULT_FOLDER\\aaaa\\\\bbbb"));
        note.setColor           (new Property("1"));
        note.setHeight          (new Property("11"));
        note.setWidth           (new Property());
        note.setTop             (new Property("110"));
        note.setLeft            (new Property("220"));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        NoteWrapper result = pimNoteDao.getItem(uid);
        NoteWrapper expected = nw;

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));
        assertEquals(expectedItems.getRowCount() + 1, pimNoteDao.getAllItems().size());

        NoteWrapperAsserts.assertEquals(expected, result);
    }

    /**
     * Add a note with null values for properties.
     */
    public void testAddItem_3() throws Exception {

        Note note = new Note();
        note.setSubject         (new Property(null));
        note.setTextDescription (new Property(null));
        note.setCategories      (new Property(null));
        note.setFolder          (new Property(null));
        note.setColor           (new Property(null));
        note.setHeight          (new Property(null));
        note.setWidth           (new Property(null));
        note.setTop             (new Property(null));
        note.setLeft            (new Property(null));

        String uid = Long.toString(100);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        NoteWrapper result = pimNoteDao.getItem(uid);
        NoteWrapper expected = nw;

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));
        assertEquals(expectedItems.getRowCount() + 1, pimNoteDao.getAllItems().size());

        NoteWrapperAsserts.assertEquals(expected, result);
    }

    /**
     * Add a note whose body is bigger than the max size of the body.
     */
    public void testAddItemBigContent() throws Throwable {

        int index = 100;

        Note note = new Note();
        note.setSubject         (new Property(createBigString(BIG_SUBJECT_SIZE, "subject ")));
        note.setTextDescription (new Property(createBigString(BIG_BODY_SIZE, "body ")));
        note.setCategories      (new Property(createBigString(BIG_CATEGORIES_SIZE, "cat,")));
        note.setFolder          (new Property(createBigString(BIG_FOLDER_SIZE, "folder\\")));
        note.setColor           (new Property("1"));
        note.setHeight          (new Property("11"));
        note.setWidth           (new Property("22"));
        note.setTop             (new Property("110"));
        note.setLeft            (new Property("220"));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        NoteWrapper result = pimNoteDao.getItem(uid);
        
        NoteWrapper expected = nw;
        Note expectedNote = nw.getNote();
        
        String expectedSubject         = StringUtils.left(
                expectedNote.getSubject().getPropertyValueAsString(), PIMNoteDAO.SQL_SUBJECT_DIM);
        String expectedTextDescription = StringUtils.left(
                expectedNote.getTextDescription().getPropertyValueAsString(), PIMNoteDAO.SQL_TEXTDESCRIPTION_DIM);
        
        String expectedFolder = (String) PrivateAccessor.invoke(
                    pimNoteDao, "truncateFolderField", new Class[]{String.class, int.class}, 
                    new Object[]{expectedNote.getFolder().getPropertyValueAsString(), PIMNoteDAO.SQL_FOLDER_DIM});
        String expectedCategories = (String) PrivateAccessor.invoke(
                    pimNoteDao, "truncateCategoriesField", new Class[]{String.class, int.class}, 
                    new Object[]{expectedNote.getCategories().getPropertyValueAsString(), PIMNoteDAO.SQL_CATEGORIES_DIM});

        expectedNote.setSubject         (new Property(expectedSubject));
        expectedNote.setTextDescription (new Property(expectedTextDescription));
        expectedNote.setFolder          (new Property(expectedFolder));
        expectedNote.setCategories      (new Property(expectedCategories));

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));
        assertEquals(expectedItems.getRowCount() + 1, pimNoteDao.getAllItems().size());
        
        NoteWrapperAsserts.assertEquals(expected, result);
    }

    public void testAddItem_NullChar() throws Throwable {

        int index = 100;

        Note note = new Note();
        note.setSubject         (new Property("subject " + index));
        note.setTextDescription (new Property("This is the body of the note " + index+" with a null \0 character"));
        note.setCategories      (new Property("cat1, cat2, cat3"));
        note.setFolder          (new Property("DEFAULT_FOLDER\\aaaa\\\\bbbb"));
        note.setColor           (new Property("1"));
        note.setHeight          (new Property("11"));
        note.setWidth           (new Property("22"));
        note.setTop             (new Property("110"));
        note.setLeft            (new Property("220"));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        NoteWrapper result = pimNoteDao.getItem(uid);
        
        NoteWrapper expected = nw;
        expected.getNote().setTextDescription(new Property("This is the body of the note " + index+" with a null   character"));

        ITable expectedItems = new RowFilterTable(getUserDBDataSet().getTable(TABLE_FNBL_PIM_NOTE),
                                                  getRowFilter(USER_ID, new char[] {'N', 'U'}));
        assertEquals(expectedItems.getRowCount() + 1, pimNoteDao.getAllItems().size());

        NoteWrapperAsserts.assertEquals(expected, result);
        
    }

    /**
     * Modify none of the fields of a note.
     */
    public void testUpdateItem_1() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String expectedSubject         = note.getSubject().getPropertyValueAsString();
        String expectedTextDescription = note.getTextDescription().getPropertyValueAsString();
        String expectedCategories      = note.getCategories().getPropertyValueAsString();
        String expectedFolder          = note.getFolder().getPropertyValueAsString();
        String expectedColor           = note.getColor().getPropertyValueAsString();
        String expectedHeight          = note.getHeight().getPropertyValueAsString();
        String expectedWidth           = note.getWidth().getPropertyValueAsString();
        String expectedTop             = note.getTop().getPropertyValueAsString();
        String expectedLeft            = note.getLeft().getPropertyValueAsString();

        note.setSubject         (new Property());
        note.setTextDescription (new Property());
        note.setCategories      (new Property());
        note.setFolder          (new Property());
        note.setColor           (new Property());
        note.setHeight          (new Property());
        note.setWidth           (new Property());
        note.setTop             (new Property());
        note.setLeft            (new Property());

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (new Property(expectedSubject));
        expectedNote.setTextDescription (new Property(expectedTextDescription));
        expectedNote.setCategories      (new Property(expectedCategories));
        expectedNote.setFolder          (new Property(expectedFolder));
        expectedNote.setColor           (new Property(expectedColor));
        expectedNote.setHeight          (new Property(expectedHeight));
        expectedNote.setWidth           (new Property(expectedWidth));
        expectedNote.setTop             (new Property(expectedTop));
        expectedNote.setLeft            (new Property(expectedLeft));

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    /**
     * Modify all the fields of a note.
     */
    public void testUpdateItem_2() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        String upd = " UPD";
        Property expectedSubject         = new Property(subject + upd);
        Property expectedTextDescription = new Property(textDescription + upd);
        Property expectedCategories      = new Property(categories + ", " + upd);
        Property expectedFolder          = new Property(folder + "\\folder3");
        Property expectedColor           = new Property("1");
        Property expectedHeight          = new Property(height + "1");
        Property expectedWidth           = new Property(width + "1");
        Property expectedTop             = new Property(top + "1");
        Property expectedLeft            = new Property(left + "1");

        note.setSubject         (expectedSubject);
        note.setTextDescription (expectedTextDescription);
        note.setCategories      (expectedCategories);
        note.setFolder          (expectedFolder);
        note.setColor           (expectedColor);
        note.setHeight          (expectedHeight);
        note.setWidth           (expectedWidth);
        note.setTop             (expectedTop);
        note.setLeft            (expectedLeft);

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject(expectedSubject);
        expectedNote.setTextDescription(expectedTextDescription);
        expectedNote.setCategories(expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }
    
    /**
     * If the content of properties: color, height, width, top, left is null (that is:
     * sif tags for that properties are not presesnt) then their values must not be updated
     */
    public void testUpdateItem_3() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        String upd = " UPD";
        Property expectedSubject         = new Property(subject + upd);
        Property expectedTextDescription = new Property(textDescription + upd);
        Property expectedCategories      = new Property(categories + ", " + upd);
        Property expectedFolder          = new Property(folder + "\\folder3");
        Property expectedColor           = new Property(color);
        Property expectedHeight          = new Property(height);
        Property expectedWidth           = new Property(width);
        Property expectedTop             = new Property(top);
        Property expectedLeft            = new Property(left);

        note.setSubject         (expectedSubject);
        note.setTextDescription (expectedTextDescription);
        note.setCategories      (expectedCategories);
        note.setFolder          (expectedFolder);
        note.setColor           (new Property(null));
        note.setHeight          (new Property(null));
        note.setWidth           (new Property(null));
        note.setTop             (new Property(null));
        note.setLeft            (new Property(null));

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (expectedSubject);
        expectedNote.setTextDescription (expectedTextDescription);
        expectedNote.setCategories      (expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    /**
     * If the content of properties: color, height, width, top, left is the empty  
     * string then their values must be set at null.
     */
    public void testUpdateItem_4() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        String upd = " UPD";
        Property expectedSubject         = new Property(subject + upd);
        Property expectedTextDescription = new Property(textDescription + upd);
        Property expectedCategories      = new Property(categories + ", " + upd);
        Property expectedFolder          = new Property(folder + "\\folder3");
        Property expectedColor           = new Property(null);
        Property expectedHeight          = new Property(null);
        Property expectedWidth           = new Property(null);
        Property expectedTop             = new Property(null);
        Property expectedLeft            = new Property(null);

        note.setSubject         (expectedSubject);
        note.setTextDescription (expectedTextDescription);
        note.setCategories      (expectedCategories);
        note.setFolder          (expectedFolder);
        note.setColor           (new Property(""));
        note.setHeight          (new Property(""));
        note.setWidth           (new Property(""));
        note.setTop             (new Property(""));
        note.setLeft            (new Property(""));

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (expectedSubject);
        expectedNote.setTextDescription (expectedTextDescription);
        expectedNote.setCategories      (expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    /**
     * Setting empty string for properties: subject, textDescription, categories,
     * folder will update the database value to the empty string
     */
    public void testUpdateItem_5() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        Property expectedSubject         = new Property("");
        Property expectedTextDescription = new Property("");
        Property expectedCategories      = new Property("");
        Property expectedFolder          = new Property("");
        Property expectedColor           = new Property(color);
        Property expectedHeight          = new Property(height);
        Property expectedWidth           = new Property(width);
        Property expectedTop             = new Property(top);
        Property expectedLeft            = new Property(left);

        note.setSubject         (new Property(""));
        note.setTextDescription (new Property(""));
        note.setCategories      (new Property(""));
        note.setFolder          (new Property(""));

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (expectedSubject);
        expectedNote.setTextDescription (expectedTextDescription);
        expectedNote.setCategories      (expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    /**
     * Setting null for properties: subject, textDescription, categories,
     * folder will not update database value
     */
    public void testUpdateItem_6() throws Exception {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        Property expectedSubject         = new Property(subject);
        Property expectedTextDescription = new Property(textDescription);
        Property expectedCategories      = new Property(categories);
        Property expectedFolder          = new Property(folder);
        Property expectedColor           = new Property(color);
        Property expectedHeight          = new Property(height);
        Property expectedWidth           = new Property(width);
        Property expectedTop             = new Property(top);
        Property expectedLeft            = new Property(left);

        note.setSubject         (new Property(null));
        note.setTextDescription (new Property(null));
        note.setCategories      (new Property(null));
        note.setFolder          (new Property(null));

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (expectedSubject);
        expectedNote.setTextDescription (expectedTextDescription);
        expectedNote.setCategories      (expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    /**
     * Update fields with a big content.
     */
    public void testUpdateItemBigContent() throws Throwable {

        NoteWrapper nw = pimNoteDao.getItem("1");
        Note note = nw.getNote();

        String subject         = note.getSubject().getPropertyValueAsString();
        String textDescription = note.getTextDescription().getPropertyValueAsString();
        String categories      = note.getCategories().getPropertyValueAsString();
        String folder          = note.getFolder().getPropertyValueAsString();
        String color           = note.getColor().getPropertyValueAsString();
        String height          = note.getHeight().getPropertyValueAsString();
        String width           = note.getWidth().getPropertyValueAsString();
        String top             = note.getTop().getPropertyValueAsString();
        String left            = note.getLeft().getPropertyValueAsString();

        String upd = " UPD";
        String bigString = createBigString(1000 * 2, "a");

        Property expectedSubject         = new Property(
                StringUtils.left(bigString, PIMNoteDAO.SQL_SUBJECT_DIM));
        Property expectedTextDescription = new Property(
                StringUtils.left(bigString, PIMNoteDAO.SQL_TEXTDESCRIPTION_DIM));
        Property expectedCategories      = new Property(
                (String) PrivateAccessor.invoke(
                pimNoteDao, "truncateCategoriesField", new Class[]{String.class, int.class},
                new Object[]{bigString, PIMNoteDAO.SQL_FOLDER_DIM}));
        Property expectedFolder          = new Property(
                (String) PrivateAccessor.invoke(
                pimNoteDao, "truncateFolderField", new Class[]{String.class, int.class},
                new Object[]{bigString, PIMNoteDAO.SQL_FOLDER_DIM}));
        
        Property expectedColor           = new Property("1");
        Property expectedHeight          = new Property(height + "1");
        Property expectedWidth           = new Property(width + "1");
        Property expectedTop             = new Property(top + "1");
        Property expectedLeft            = new Property(left + "1");

        note.setSubject         (expectedSubject);
        note.setTextDescription (expectedTextDescription);
        note.setCategories      (expectedCategories);
        note.setFolder          (expectedFolder);
        note.setColor           (expectedColor);
        note.setHeight          (expectedHeight);
        note.setWidth           (expectedWidth);
        note.setTop             (expectedTop);
        note.setLeft            (expectedLeft);

        pimNoteDao.updateItem(nw);

        NoteWrapper resultNoteWrapper = pimNoteDao.getItem("1");
        assertEquals ("1"    , resultNoteWrapper.getId());
        assertEquals (USER_ID, resultNoteWrapper.getUserId());
        assertEquals ('U'    , resultNoteWrapper.getStatus());

        Note expectedNote = new Note();
        expectedNote.setSubject         (expectedSubject);
        expectedNote.setTextDescription (expectedTextDescription);
        expectedNote.setCategories      (expectedCategories);
        expectedNote.setFolder          (expectedFolder);
        expectedNote.setColor           (expectedColor);
        expectedNote.setHeight          (expectedHeight);
        expectedNote.setWidth           (expectedWidth);
        expectedNote.setTop             (expectedTop);
        expectedNote.setLeft            (expectedLeft);

        assertEquals(expectedNote, resultNoteWrapper.getNote());
    }

    public void testCalculateCrc() throws Exception, Throwable {

        Note note = new Note();
        note.setTextDescription(new Property("TeSt for\r\nthe\nTWIN detection"));
        
        Long hashval = (Long) PrivateAccessor.invoke(pimNoteDao, 
                "calculateCrc",
                new Class[]{String.class}, 
                new Object[] {note.getTextDescription().getPropertyValueAsString()});
        assertEquals(new Long(1645968316), hashval);
    }

    public void testGetTwinItems_1() throws Exception {

        Note note = new Note();
        note.setTextDescription(new Property("TeSt for\r\nthe\nTWIN detection"));

        List resultTwinsIds = pimNoteDao.getTwinItems(note);
        
        String[] expectedTwinIds = {"40", "41"};

        ArrayAssert.assertEquals(expectedTwinIds, resultTwinsIds.toArray());
    }
    
    /**
     * Windows Mobile plugin and some version of the Outlook plugin add a "\n" at
     * the end of the content of the body of a note.
     * 
     * This may lead to item duplications in case of slow sync (see bug ...). 
     * In order to avoid this behavior, bodies containing '\n' or '\r' chars are
     * considered the same as they do not have such chars. Thus: any string that
     * ends with a '\n' is the same as if it does not ends with '\n'.
     * 
     * This is taken into account while calculating the hash of a note.
     */
    public void testGetTwinItems_2() throws Exception {

        Note note = new Note();
        note.setTextDescription(new Property("TeSt for\r\nthe\nTWIN detection\n"));

        List resultTwinsIds = pimNoteDao.getTwinItems(note);
        
        String[] expectedTwinIds = {"40", "41"};

        ArrayAssert.assertEquals(expectedTwinIds, resultTwinsIds.toArray());
    }

    /**
     * The incoming note has body null
     */
    public void testGetTwinItems_3() throws Exception {

        Note note = new Note();
        note.setTextDescription(new Property());

        List resultTwinsIds = pimNoteDao.getTwinItems(note);
        
        String[] expectedTwinIds = {};

        ArrayAssert.assertEquals(expectedTwinIds, resultTwinsIds.toArray());
    }

    /**
     * The incoming note has empty
     */
    public void testGetTwinItems_4() throws Exception {

        Note note = new Note();
        note.setTextDescription(new Property(""));

        List resultTwinsIds = pimNoteDao.getTwinItems(note);
        
        String[] expectedTwinIds = {};

        ArrayAssert.assertEquals(expectedTwinIds, resultTwinsIds.toArray());
    }
    
    public void testGetTwinItemsBigContent() throws Exception {
        
        Note incomingNote = new Note();
        incomingNote.setTextDescription(new Property(createBigString(BIG_BODY_SIZE, "body\r\n")));
        
        //
        // Add the expected twin notes
        //
        int index = 100;

        Note note = new Note();
        note.setSubject         (new Property(createBigString(BIG_SUBJECT_SIZE   , "subject ")));
        note.setTextDescription (new Property(createBigString(BIG_BODY_SIZE      , "body\r\n")));
        note.setCategories      (new Property(createBigString(BIG_CATEGORIES_SIZE, "cat,")));
        note.setFolder          (new Property(createBigString(BIG_FOLDER_SIZE    , "folder\\")));
        note.setColor           (new Property("1"));
        note.setHeight          (new Property("11"));
        note.setWidth           (new Property("22"));
        note.setTop             (new Property("110"));
        note.setLeft            (new Property("220"));

        String uid = Long.toString(index);
        NoteWrapper nw = new NoteWrapper(uid, USER_ID, note);
        nw.setStatus('N');

        pimNoteDao.addItem(nw);
        
        List resultTwinsIds = pimNoteDao.getTwinItems(incomingNote);
        
        String[] expectedTwinIds = {"100"};

        ArrayAssert.assertEquals(expectedTwinIds, resultTwinsIds.toArray());
    }

    // -------------------------------------------------------------------------
    /**
     * Check truncateTextField.
     * 
     * 0 if truncationSize < textField size then text field is not truncated
     * 1 if truncationSize = textField size then text field is not truncated
     * 2 if truncationSize > textField size then text field is truncated at truncationSize
     */    
    public void testTruncate() throws Throwable {
        
        String truncatedTextField = null;
        int truncationSize = 2;
        
        String[][] tests = {
            new String[]{"a"     , "a"},    // 0
            new String[]{"aa"    , "aa"},   // 1
            new String[]{"aabbbb", "aa"},   // 2
        };
                
        for (String[] test : tests) {
            truncatedTextField = StringUtils.left(test[0], truncationSize);
            assertEquals(test[1], truncatedTextField);
        }
    }
    
    /**
     * Check truncateFolderField.
     * 
     * 0 /aaa/bbb/ccc/ddd/eee....             --> /aaa/bbb/ccc/ddd/eee
     * 1 /aaa/bbb/ccc/ddd/eee/fff             --> /aaa/bbb/ccc/ddd/eee/fff
     * 2 /aaa/bbb/ccc/ddd/eee/ffff            --> /aaa/bbb/ccc/ddd/eee
     * 3 /aaa/bbb/ccc/ddd/eee/fff/            --> /aaa/bbb/ccc/ddd/eee/fff
     * 4 /aaa/bbb/ccc/ddd/eee/ffffff/ggg      --> /aaa/bbb/ccc/ddd/eee
     * 5 aaaaaaaaaaaaaaaaaaaaaaaaaaaa         --> "" (empty string)
     * 
     */    
    public void testTruncateFolderField() throws Throwable {
        
        String truncatedFolderField = null;
        int truncationSize = 24;
        
        String[][] tests = {
            new String[]{"/aaa/bbb/ccc/ddd/eee"           , "/aaa/bbb/ccc/ddd/eee"},        // 0
            new String[]{"/aaa/bbb/ccc/ddd/eee/fff"       , "/aaa/bbb/ccc/ddd/eee/fff"},    // 1
            new String[]{"/aaa/bbb/ccc/ddd/eee/ffff"      , "/aaa/bbb/ccc/ddd/eee"},        // 2
            new String[]{"/aaa/bbb/ccc/ddd/eee/fff/"      , "/aaa/bbb/ccc/ddd/eee/fff"},    // 3
            new String[]{"/aaa/bbb/ccc/ddd/eee/ffffff/ggg", "/aaa/bbb/ccc/ddd/eee"},        // 4
            new String[]{"aaaaaaaaaaaaaaaaaaaaaaaaaaaa"   , ""}                             // 5
        };
        
        for (String[] test : tests) {
            truncatedFolderField = (String) PrivateAccessor.invoke(
                    pimNoteDao, "truncateFolderField", new Class[]{String.class, int.class},
                    new Object[]{test[0], truncationSize});
            assertEquals(test[1], truncatedFolderField);
        }
    }

    /**
     * Check truncateCategoriesField.
     * 
     * 0 aaa, bbb, ccc, ddd, eee....          --> aaa, bbb, ccc, ddd, eee....
     * 1 aaa, bbb, ccc, ddd, eee, ff          --> aaa, bbb, ccc, ddd, eee, ff
     * 2 aaa, bbb, ccc, ddd, eee, ffff        --> aaa, bbb, ccc, ddd, eee
     * 3 aaa, bbb, ccc, ddd, eee, f,          --> aaa, bbb, ccc, ddd, eee, f    (this case should not occur)
     * 4 aaa, bbb,ccc, ddd, eee,ffffff        --> aaa, bbb,ccc, ddd, eee
     * 5 aaaaaaaaaaaaaaaaaaaaaaaaaaaa         --> "" (empty string)
     */    
    public void testTruncateCategoriesField() throws Throwable {
        
        String truncatedFolderField = null;
        int truncationSize = 27;
        
        String[][] tests = {
            new String[]{"aaa, bbb, ccc, ddd, eee"      , "aaa, bbb, ccc, ddd, eee"},     // 0
            new String[]{"aaa, bbb, ccc, ddd, eee, ff"  , "aaa, bbb, ccc, ddd, eee, ff"}, // 1
            new String[]{"aaa, bbb, ccc, ddd, eee, ffff", "aaa, bbb, ccc, ddd, eee"},     // 2
            new String[]{"aaa, bbb, ccc, ddd, eee, f,"  , "aaa, bbb, ccc, ddd, eee, f"},  // 3
            new String[]{"aaa, bbb,ccc, ddd, eee,ffffff", "aaa, bbb,ccc, ddd, eee"},      // 4
            new String[]{"aaaaaaaaaaaaaaaaaaaaaaaaaaaa" , ""}                             // 5
        };
        
        for (String[] test : tests) {
            truncatedFolderField = (String) PrivateAccessor.invoke(
                    pimNoteDao, "truncateCategoriesField", new Class[]{String.class, int.class},
                    new Object[]{test[0], truncationSize});
            assertEquals(test[1], truncatedFolderField);
        }
    }


    public void testIsTwinSearchAppliableOn_NullNote() {
        assertFalse(this.pimNoteDao.isTwinSearchAppliableOn(null));
    }

    public void testIsTwinSearchAppliableOn_EmptyNote() {
        Note note = new Note();
        assertFalse(this.pimNoteDao.isTwinSearchAppliableOn(note));
    }

       public void testIsTwinSearchAppliableOn_EmptyTextDescriptionSet() {
        Note note = new Note();
        note.getTextDescription().setPropertyValue("");
        note.getFolder().setPropertyValue("private");
        assertFalse(this.pimNoteDao.isTwinSearchAppliableOn(note));
    }

    public void testIsTwinSearchAppliableOn_OnlyTextDescriptionSet() {
        Note note = new Note();
        note.getTextDescription().setPropertyValue("Hello! It's me.");
        assertTrue(this.pimNoteDao.isTwinSearchAppliableOn(note));
    }

   public void testIsTwinSearchAppliableOn_TextDescriptionAndSomeFieldsSet() {
        Note note = new Note();
        note.getTextDescription().setPropertyValue("Hello! It's me.");
        note.getSubject().setPropertyValue("Subject for the note.");
        note.getColor().setPropertyValue("Red");
        assertTrue(this.pimNoteDao.isTwinSearchAppliableOn(note));
   }

   public void testIsTwinSearchAppliableOn_AllFieldsSet() {
        Note note = new Note();
        note.getCategories().setPropertyValue("categories");
        note.getColor().setPropertyValue("Red");
        note.getDate().setPropertyValue("date");
        note.getFolder().setPropertyValue("folder");
        note.getHeight().setPropertyValue("height");
        note.getLeft().setPropertyValue("left");
        note.getSubject().setPropertyValue("subject");
        note.getTextDescription().setPropertyValue("text description");
        note.getTop().setPropertyValue("top");
        note.getUid().setPropertyValue("uid");
        note.getWidth().setPropertyValue("width");
        note.setXTags(new ArrayList());
        assertTrue(this.pimNoteDao.isTwinSearchAppliableOn(note));
   }


   public void testIsTwinSearchAppliableOn_AllFieldsButTextDescriptionSet() {
        Note note = new Note();
        note.getCategories().setPropertyValue("categories");
        note.getColor().setPropertyValue("Red");
        note.getDate().setPropertyValue("date");
        note.getFolder().setPropertyValue("folder");
        note.getHeight().setPropertyValue("height");
        note.getLeft().setPropertyValue("left");
        note.getSubject().setPropertyValue("subject");
        note.getTextDescription().setPropertyValue(null);
        note.getTop().setPropertyValue("top");
        note.getUid().setPropertyValue("uid");
        note.getWidth().setPropertyValue("width");
        note.setXTags(new ArrayList());
        assertFalse(this.pimNoteDao.isTwinSearchAppliableOn(note));
    }

    // --------------------------------------------------------- Private methods
    protected IDataSet getCoreDBDataSet() {
        return coreDbTester.getDataSet();
    }

    protected IDataSet getUserDBDataSet() {
        return userDbTesterPart1.getDataSet();
    }

    protected IRowFilter getRowFilter(final String user, final char[] status) throws DataSetException{
        IRowFilter rowFilter = new IRowFilter() {
            public boolean accept(IRowValueProvider rowValueProvider) {
                try {
                    Object u = rowValueProvider.getColumnValue("USERID");
                    Object s = rowValueProvider.getColumnValue("STATUS");

                    if (((String) u).equalsIgnoreCase(user)) {
                        for (int i = 0; i < status.length; i++) {
                            if (((String) s).equalsIgnoreCase(String.valueOf(status[i]))) {
                                return true;
                            }
                        }
                    }
                } catch (DataSetException ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        };
        return rowFilter;
    }

    /**
     * Creates a string of at least <code>size</code> chars.
     * 
     * @param size
     * @param stringToBeRepeated a string used to generate the content
     * @return a string of at least <code>size</code> chars
     */
    private static String createBigString (int size, String stringToBeRepeated){
        StringBuilder bigString = new StringBuilder();
        while (true) {
            bigString.append(stringToBeRepeated);
            if (bigString.length() >= size){
                break;
            }
        }

        return bigString.toString();
    }

    private static void assertEquals(Note expected, Note result){
        if (!NoteUtils.deepEquals(expected, result)) {
            fail();
        }
    }

    protected void freeDatabaseResources()  {
        DBHelper.closeConnection(coreDbTester);
        DBHelper.closeConnection(userDbTesterPart1);
    }

}
