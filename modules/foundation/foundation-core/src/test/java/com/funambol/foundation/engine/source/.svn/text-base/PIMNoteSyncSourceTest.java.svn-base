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
package com.funambol.foundation.engine.source;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.note.Note;
import com.funambol.foundation.items.manager.PIMNoteManager;
import com.funambol.foundation.util.TestDef;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.tools.database.DBHelper;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;
import org.dbunit.IDatabaseTester;

/**
 * Test class for <code>PIMNoteSyncSource</code> .
 * @author $Id: PIMNoteSyncSourceTest.java,v 1.3 2008-08-22 10:47:40 piter_may Exp $
 */
public class PIMNoteSyncSourceTest extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants
    private static final String EMPTY_BODY_STRING = "[empty note]";

    private static final String NOT_EMPTY_NOTE = "<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\"?>" +
            "<note>\n" +
            "<SIFVersion>1.1</SIFVersion><Subject>Not empty note</Subject><Body>r1\r\nr2</Body></note>";

    private static final String USER_ID = DBHelper.USER_PART1;

    private static final String INITIAL_DB_DATASET_CORE =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/engine/source/PIMNoteSyncSourceTest/dataset-coredb.xml";
    private static final String INITIAL_DB_DATASET_USER =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/engine/source/PIMNoteSyncSourceTest/dataset-userdb.xml";

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
    private PIMNoteSyncSource source;
    private PIMNoteManager manager;
    
    // ------------------------------------------------------------ Constructors
    public PIMNoteSyncSourceTest(String testName) throws Exception {
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

        source = new PIMNoteSyncSource();

        //
        // manager used by sync source object under test
        //
        manager = new PIMNoteManager(USER_ID);
        PrivateAccessor.setField(source, "manager", manager);
        
        //
        // SyncSourceInfo object used by testGetSyncItemFromId method
        //
        ContentType[] supportedTypes = {
            new ContentType("text/plain", "1.0"),
        };
        SyncSourceInfo syncSourceInfo = new SyncSourceInfo(supportedTypes, 0);        
        source.setInfo(syncSourceInfo);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        freeDatabaseResources();
    }

    // ------------------------------------------------------------ Test methods 
    
    /**
     * EMPTY_BODY_STRING --> empty body
     */
    public void testAddSyncItem_0() throws Exception {

        SyncItem itemFromClient = new SyncItemImpl(source, "1234");        
        itemFromClient.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        String body = EMPTY_BODY_STRING;
        itemFromClient.setContent(body.getBytes());
        
        //
        // check content of the resulting sync item
        //
        try {
            SyncItem resultSyncItem = source.addSyncItem(itemFromClient);
            fail("Exception expected while adding empty note");
        } catch(Throwable e) {
            assertTrue(e instanceof SyncSourceException);
            assertEquals("Adding a note without any field usable for twin search set is not allowed.",
                         e.getMessage());
        }
        
    }

    /**
     * sif notes are not affected
     */
    public void testAddSyncItem_1() throws Exception {
        
        SyncItem clientItem = new SyncItemImpl(source, "1234");        
        clientItem.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.SIFN]);        
        
        StringBuilder clientItemContent = new StringBuilder();
        clientItemContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        clientItemContent.append("<note>");
        clientItemContent.append("<SIFVersion>1.0</SIFVersion>");
        clientItemContent.append("<Body>").append(EMPTY_BODY_STRING).append("</Body>");
        clientItemContent.append("<Categories/>");
        clientItemContent.append("<Color>3</Color>");
        clientItemContent.append("<Folder>DEFAULT_FOLDER</Folder>");
        clientItemContent.append("<Height>166</Height>");
        clientItemContent.append("<Left>80</Left>");
        clientItemContent.append("<Subject>note 1</Subject>");
        clientItemContent.append("<Top>80</Top>");
        clientItemContent.append("<Width>200</Width>");
        clientItemContent.append("</note>");
        
        clientItem.setContent(clientItemContent.toString().getBytes());
        
        //
        // check content of the resulting sync item
        //
        SyncItem resultSyncItem = source.addSyncItem(clientItem);        
        String resultBody = new String(resultSyncItem.getContent());
        
        StringBuilder expectedBody = new StringBuilder();
        expectedBody.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        expectedBody.append("<note>");
        expectedBody.append("<SIFVersion>1.0</SIFVersion>");
        expectedBody.append("<Body>").append(EMPTY_BODY_STRING).append("</Body>");
        expectedBody.append("<Categories/>");
        expectedBody.append("<Color>3</Color>");
        expectedBody.append("<Folder>DEFAULT_FOLDER</Folder>");
        expectedBody.append("<Height>166</Height>");
        expectedBody.append("<Left>80</Left>");
        expectedBody.append("<Subject>note 1</Subject>");
        expectedBody.append("<Top>80</Top>");
        expectedBody.append("<Width>200</Width>");
        expectedBody.append("</note>");
        
        assertEquals(expectedBody.toString(), resultBody.toString());
        
        //
        // check content of the database
        //
        Note databaseNote = 
                manager.getItem(resultSyncItem.getKey().getKeyAsString()).getNote();
        assertEquals(EMPTY_BODY_STRING, databaseNote.getTextDescription().getPropertyValueAsString());
    }

    /**
     * EMPTY_BODY_STRING --> empty body
     */
    public void testUpdateSyncItem_0() throws Exception {
        
        //
        // add the note that will be updated
        //
        SyncItem itemToAdd = new SyncItemImpl(source, ""); // key passed to the constructor must be not null; it will be overwritten by the add
        itemToAdd.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        itemToAdd.setContent("the body of the note".getBytes());
        
        SyncItem addedItem = source.addSyncItem(itemToAdd);
        String addedItemKey = addedItem.getKey().getKeyAsString();
        
        //
        // update the note
        //        
        SyncItem itemFromClient = new SyncItemImpl(source, addedItemKey);        
        itemFromClient.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        itemFromClient.setContent(EMPTY_BODY_STRING.getBytes());
        
        SyncItem resultSyncItem = source.updateSyncItem(itemFromClient);
        
        //
        // check content of the resulting sync item
        //
        String resultBody = new String(resultSyncItem.getContent());
        String expectedBody = "";
        
        assertEquals(expectedBody, resultBody);

        //
        // check content of the database
        //
        Note databaseNote = manager.getItem(addedItemKey).getNote();
        
        assertEquals("", databaseNote.getTextDescription().getPropertyValueAsString());        
    }

    /**
     * sif notes are not affected
     */
    public void testUpdateSyncItem_1() throws Exception {
        
        //
        // add the note that will be updated
        //
        SyncItem itemToAdd = new SyncItemImpl(source, ""); // key passed to the constructor must be not null; it will be overwritten by the add
        itemToAdd.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.SIFN]);
        
        StringBuilder itemToAddContent = new StringBuilder();
        itemToAddContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        itemToAddContent.append("<note>");
        itemToAddContent.append("<SIFVersion>1.0</SIFVersion>");
        itemToAddContent.append("<Body>hello world!</Body>");
        itemToAddContent.append("<Categories/>");
        itemToAddContent.append("<Color>3</Color>");
        itemToAddContent.append("<Folder>DEFAULT_FOLDER</Folder>");
        itemToAddContent.append("<Height>166</Height>");
        itemToAddContent.append("<Left>80</Left>");
        itemToAddContent.append("<Subject>note 1</Subject>");
        itemToAddContent.append("<Top>80</Top>");
        itemToAddContent.append("<Width>200</Width>");
        itemToAddContent.append("</note>");
        
        itemToAdd.setContent(itemToAddContent.toString().getBytes());
        
        SyncItem addedItem = source.addSyncItem(itemToAdd);
        String addedItemKey = addedItem.getKey().getKeyAsString();
        
        //
        // update the body of the note
        //        
        SyncItem itemFromClient = new SyncItemImpl(source, addedItemKey);        
        itemFromClient.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.SIFN]);
        
        StringBuilder updatedBody = new StringBuilder();
        updatedBody.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        updatedBody.append("<note>");
        updatedBody.append("<SIFVersion>1.0</SIFVersion>");
        updatedBody.append("<Body>").append(EMPTY_BODY_STRING).append("</Body>");
        updatedBody.append("<Categories/>");
        updatedBody.append("<Color>3</Color>");
        updatedBody.append("<Folder>DEFAULT_FOLDER</Folder>");
        updatedBody.append("<Height>166</Height>");
        updatedBody.append("<Left>80</Left>");
        updatedBody.append("<Subject>note 1</Subject>");
        updatedBody.append("<Top>80</Top>");
        updatedBody.append("<Width>200</Width>");
        updatedBody.append("</note>");
        
        itemFromClient.setContent(updatedBody.toString().getBytes());
        
        SyncItem resultSyncItem = source.updateSyncItem(itemFromClient);
        
        //
        // check content of the resulting sync item
        //
        String resultBody = new String(resultSyncItem.getContent());
        String expectedBody = updatedBody.toString();
        
        assertEquals(expectedBody, resultBody);

        //
        // check content of the database
        //
        Note databaseNote = manager.getItem(addedItemKey).getNote();
        
        assertEquals(EMPTY_BODY_STRING, databaseNote.getTextDescription().getPropertyValueAsString());        
    }
    
    /**
     * EMPTY_BODY_STRING --> empty body
     */
    public void testGetSyncItemKeysFromTwin() throws Exception {
        
        //
        // on server, add a note with an empty body
        //
        SyncItem itemToAdd = new SyncItemImpl(source, ""); // key passed to the constructor must be not null; it will be overwritten by the add
        itemToAdd.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        itemToAdd.setContent(NOT_EMPTY_NOTE.getBytes());
        
        SyncItem addedItem = source.addSyncItem(itemToAdd);
        String addedItemKey = addedItem.getKey().getKeyAsString();
        
        //
        // item coming from the client
        //
        SyncItem clientItem = new SyncItemImpl(source, "");
        clientItem.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        clientItem.setContent(NOT_EMPTY_NOTE.getBytes());

        SyncItemKey[] twinItemsKeys = source.getSyncItemKeysFromTwin(clientItem);
        
        //
        // check only if the itemToAdd item is found (more than one item could be 
        // found since the test set may contain empty body item other than itemToAdd).
        //
        boolean found = false;
        for (int i = 0; i < twinItemsKeys.length; i++) {
            if (addedItemKey.equals(twinItemsKeys[i].getKeyAsString())){
                found = true;
                break;
            }
        }
        if (!found) {
            fail();
        }
    }    
    
    public void testMergeSyncItems_0() throws Exception {
        //@TODO
    }    

    public void testGetSyncItemFromId() throws Exception {
        
        //
        // on server, add a note with an empty body
        //
        SyncItem itemToAdd = new SyncItemImpl(source, ""); // key passed to the constructor must be not null; it will be overwritten by the add
        itemToAdd.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        itemToAdd.setContent(NOT_EMPTY_NOTE.getBytes());
        
        SyncItem addedItem = source.addSyncItem(itemToAdd);

        //
        // retrieve the note
        //
        SyncItem resultSyncItem = source.getSyncItemFromId(addedItem.getKey());
        
        assertEquals(NOT_EMPTY_NOTE, new String(resultSyncItem.getContent()));
    }

    public void testGetSyncItemFromId_1() throws Throwable {
        
        //
        // on server, add a note with an empty body
        //
        SyncItem itemToAdd = new SyncItemImpl(source, ""); // key passed to the constructor must be not null; it will be overwritten by the add
        itemToAdd.setType(PIMNoteSyncSource.TYPE[PIMNoteSyncSource.PLAINTEXT]);        
        itemToAdd.setContent(NOT_EMPTY_NOTE.getBytes());
        
        SyncItem addedItem = source.addSyncItem(itemToAdd);

        //
        // retrieve the note
        //
        SyncItem resultSyncItem = source.getSyncItemFromId(addedItem.getKey());
        
        assertEquals(NOT_EMPTY_NOTE, new String(resultSyncItem.getContent()));
    }

    public void testNote2sif() throws Throwable {
        
        Note note = new Note();
        note.setSubject         (new Property("r1"));
        note.setTextDescription (new Property("r1\nr2"));
        
        String SIFNote = (String)PrivateAccessor.invoke(
                this.source, "note2sif", new Class[]{Note.class}, new Object[]{note});
        
        String expectedSIFNote =
            "<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\"?>" +
            "<note>\n" +
            "<SIFVersion>1.1</SIFVersion><Subject>r1</Subject><Body>r1\nr2</Body></note>";       
        
        assertEquals(expectedSIFNote, SIFNote);
        
        note.setTextDescription (new Property("r1\r\nr2"));
        
        SIFNote = (String)PrivateAccessor.invoke(
                this.source, "note2sif", new Class[]{Note.class}, new Object[]{note});
        
        expectedSIFNote =
            "<?xml version=\"1.0\" encoding=\"" + System.getProperty("file.encoding") + "\"?>" +
            "<note>\n" +
            "<SIFVersion>1.1</SIFVersion><Subject>r1</Subject><Body>r1\r\nr2</Body></note>";       
        
        assertEquals(expectedSIFNote, SIFNote);        
    }

    // --------------------------------------------------------- Private Methods

    private void freeDatabaseResources()  {
        DBHelper.closeConnection(coreDbTester);
        DBHelper.closeConnection(userDbTesterPart1);
    }
}









