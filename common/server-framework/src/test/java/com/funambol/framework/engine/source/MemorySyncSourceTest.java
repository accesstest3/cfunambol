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
package com.funambol.framework.engine.source;

import java.io.IOException;
import java.sql.Timestamp;
import junit.framework.*;

import java.util.ArrayList;
import java.util.List;

import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;


import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.FakeStreamingSyncItem;
import com.funambol.framework.engine.FakeSyncSource;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemFactory;
import com.funambol.framework.engine.SyncItemState;

/**
 * MemorySyncSource's test cases.
 *
 * @version $Id$
 */
public class MemorySyncSourceTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public MemorySyncSourceTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of concatByteArray method, of class MemorySyncSource.
     */
    public void testConcatByteArray() throws Throwable {

        List allChunks = new ArrayList();
        allChunks.add("abc".getBytes());
        allChunks.add("123".getBytes());
        allChunks.add("def".getBytes());

        byte[] expResult = "abc123def".getBytes();
        MemorySyncSource instance = new MemorySyncSource();

        byte[] result = (byte[]) PrivateAccessor.invoke(
                     instance,
                     "concatByteArray",
                     new Class[] {List.class, int.class},
                     new Object[] {allChunks, 9});

        ArrayAssert.assertEquals(expResult, result);
        try {
            
            //
            // The total bytes contained in the chunks list are greater than the
            // declared size.
            //
            allChunks = new ArrayList();
            allChunks.add("abc".getBytes());
            allChunks.add("ghi".getBytes());

            expResult = "abcghi".getBytes();
            instance = new MemorySyncSource();

            result = (byte[]) PrivateAccessor.invoke(
                         instance,
                         "concatByteArray",
                         new Class[] {List.class, int.class},
                         new Object[] {allChunks, 5});
            
            fail("Expected an ArrayIndexOutOfBoundsException");

        } catch (ArrayIndexOutOfBoundsException e) {
            // do nothing..it's the expected result
        }
        
        //
        // Test with an empty chunks in the list (it is not possible to have a
        // chunk null)
        //
        allChunks = new ArrayList();
        allChunks.add("abc".getBytes());
        allChunks.add("".getBytes());
        allChunks.add("ghi".getBytes());

        expResult = "abcghi".getBytes();
        instance = new MemorySyncSource();

        result = (byte[]) PrivateAccessor.invoke(
                     instance,
                     "concatByteArray",
                     new Class[] {List.class, int.class},
                     new Object[] {allChunks, 6});

        ArrayAssert.assertEquals(expResult, result);
    }


    public void _testAddingItems_NoLargeObjects() {

        SyncSource syncSource = new FakeSyncSource("MySyncSource", "syncDb");

        List deletedItems = new ArrayList();
        deletedItems.add(new InMemorySyncItem(syncSource, "1"));
        deletedItems.add(new InMemorySyncItem(syncSource, "2"));

        List newItems =     new ArrayList();
        newItems.add(new InMemorySyncItem(syncSource, "101"));
        newItems.add(new InMemorySyncItem(syncSource, "102"));

        List updatedItems = new ArrayList();
        updatedItems.add(new InMemorySyncItem(syncSource, "201"));
        updatedItems.add(new InMemorySyncItem(syncSource, "202"));

        SyncItemFactory syncItemFactory = null;

        MemorySyncSource instance = new MemorySyncSource();

        instance.addingItems(deletedItems, newItems, updatedItems, syncItemFactory);


        assertEquals(deletedItems.get(0), instance.getDeletedSyncItems()[0]);
        assertEquals(((SyncItem)deletedItems.get(1)).getKey(), instance.getDeletedSyncItems()[1].getKey());

    }

    public void _testAddingItems_NullSyncItemFactory() {

        MemorySyncSource instance = new MemorySyncSource();

        SyncSource syncSource = new FakeSyncSource("MySyncSource", "syncDb");


        SyncItemFactory syncItemFactory = null;

        // two deleted items 1 an 2
        // one new complete item 101
        // first chunk for new item 102
        // 2 updated items
        {
        List deletedItems = new ArrayList();
        deletedItems.add(new InMemorySyncItem(syncSource, "1"));
        deletedItems.add(new InMemorySyncItem(syncSource, "2"));

        List newItems =     new ArrayList();
        newItems.add(new InMemorySyncItem(syncSource, "101"));
        AbstractSyncItem firstChunk = new InMemorySyncItem(syncSource, "102");
        firstChunk.setState(SyncItemState.PARTIAL);
        firstChunk.setContent("first-chunk ".getBytes());
        newItems.add(firstChunk);

        List updatedItems = new ArrayList();
        updatedItems.add(new InMemorySyncItem(syncSource, "201"));
        updatedItems.add(new InMemorySyncItem(syncSource, "202"));

        instance.addingItems(deletedItems, newItems, updatedItems, syncItemFactory);

        assertEquals(((SyncItem)deletedItems.get(0)).getKey(), instance.getDeletedSyncItems()[0].getKey());
        assertEquals(((SyncItem)deletedItems.get(1)).getKey(), instance.getDeletedSyncItems()[1].getKey());

        assertEquals(((SyncItem)newItems.get(0)).getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(((SyncItem)newItems.get(1)).getKey(),     instance.getNewSyncItems()[1].getKey());
        assertEquals(firstChunk.getKey(), instance.getNewSyncItems()[1].getKey());

        assertEquals(((SyncItem)updatedItems.get(0)).getKey(), instance.getUpdatedSyncItems()[0].getKey());
        assertEquals(((SyncItem)updatedItems.get(1)).getKey(), instance.getUpdatedSyncItems()[1].getKey());
        }

        {
        // send second chunk for new item 102
        List deletedItems2 = new ArrayList();
        List newItems2 =     new ArrayList();
        AbstractSyncItem secondChunk = new InMemorySyncItem(syncSource, "102");
        secondChunk.setState(SyncItemState.PARTIAL);
        secondChunk.setContent("second-chunk ".getBytes());
        newItems2.add(secondChunk);
        List updatedItems2 = new ArrayList();

        instance.addingItems(deletedItems2, newItems2, updatedItems2, syncItemFactory);

        assertEquals(0, instance.getDeletedSyncItems().length);
        assertEquals(newItems2.size(), instance.getNewSyncItems().length);
        assertEquals(((SyncItem)newItems2.get(0)).getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(secondChunk.getKey(), instance.getNewSyncItems()[0].getKey());
        assertEquals(0, instance.getUpdatedSyncItems().length);
        }

        // send third and last chunk for new item 102
        // send one new complete item 103
        // send one updated item 101
        {
        List deletedItems3 = new ArrayList();
        List newItems3 =     new ArrayList();
        AbstractSyncItem thirdChunk = new InMemorySyncItem(syncSource, "102");
        thirdChunk.setState(SyncItemState.NEW);
        thirdChunk.setContent("third-chunk ".getBytes());
        newItems3.add(thirdChunk);
        newItems3.add(new InMemorySyncItem(syncSource, "103"));
        List updatedItems3 = new ArrayList();
        updatedItems3.add(new InMemorySyncItem(syncSource, "101"));

        instance.addingItems(deletedItems3, newItems3, updatedItems3, syncItemFactory);

        assertEquals(0, instance.getDeletedSyncItems().length);
        assertEquals("Unexpected number of new items", 2, instance.getNewSyncItems().length);
        // the Large Object Chunk is always the last even if it is the last one
        // this due to implementation of method addingItems of MemorySyncSource
        assertEquals(thirdChunk.getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals("first-chunk second-chunk third-chunk ", new String(instance.getNewSyncItems()[0].getContent()));
        assertEquals(((SyncItem)newItems3.get(0)).getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(((SyncItem)newItems3.get(1)).getKey(),     instance.getNewSyncItems()[1].getKey());
        assertEquals(1, instance.getUpdatedSyncItems().length);
        assertEquals(((SyncItem)updatedItems3.get(0)).getKey(), instance.getUpdatedSyncItems()[0].getKey());
        }
    }

    public void testAddingItems_SyncItemFactory() throws IOException {

        MemorySyncSource instance = new MemorySyncSource();

        SyncSource syncSource = new FakeSyncSource("MySyncSource", "syncDb");


        SyncItemFactory syncItemFactory = new SyncItemFactory() {

            private int key = 0;

            public AbstractSyncItem getSyncItem(SyncSource syncSource, Object key) {
                return new FakeStreamingSyncItem(syncSource,  key);
            }

            public AbstractSyncItem getSyncItem(SyncSource syncSource, Object key, Object parentKey, Object mappedKey, char state, byte[] content, String format, String type, Timestamp timestamp) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

            public AbstractSyncItem getSyncItem(SyncSource syncSource, Object key, Object parentKey, Object mappedKey, char state) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        {
        List deletedItems = new ArrayList();
        deletedItems.add(new InMemorySyncItem(syncSource, "1"));
        deletedItems.add(new InMemorySyncItem(syncSource, "2"));

        List newItems =     new ArrayList();
        newItems.add(new InMemorySyncItem(syncSource, "101"));
        InMemorySyncItem firstChunk = new InMemorySyncItem(syncSource, "102");
        firstChunk.setState(SyncItemState.PARTIAL);
        firstChunk.setContent("first-chunk ".getBytes());
        newItems.add(firstChunk);

        List updatedItems = new ArrayList();
        updatedItems.add(new InMemorySyncItem(syncSource, "201"));
        updatedItems.add(new InMemorySyncItem(syncSource, "202"));

        instance.addingItems(deletedItems, newItems, updatedItems, syncItemFactory);

        assertEquals(((AbstractSyncItem)deletedItems.get(0)).getKey(), instance.getDeletedSyncItems()[0].getKey());
        assertEquals(((AbstractSyncItem)deletedItems.get(1)).getKey(), instance.getDeletedSyncItems()[1].getKey());
        // the large object is removed from the original list and is set as
        // last in the instance list
        assertEquals(2, newItems.size());
        assertEquals(2, instance.getNewSyncItems().length);
        assertEquals(((AbstractSyncItem)newItems.get(0)).getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(firstChunk.getKey(),     instance.getNewSyncItems()[1].getKey());
        assertEquals(((AbstractSyncItem)updatedItems.get(0)).getKey(), instance.getUpdatedSyncItems()[0].getKey());
        assertEquals(((AbstractSyncItem)updatedItems.get(1)).getKey(), instance.getUpdatedSyncItems()[1].getKey());
        }

        {
        // send second chunk
        List deletedItems2 = new ArrayList();
        List newItems2 =     new ArrayList();
        AbstractSyncItem secondChunk = new InMemorySyncItem(syncSource, "102");
        secondChunk.setState(SyncItemState.PARTIAL);
        secondChunk.setContent("second-chunk ".getBytes());
        newItems2.add(secondChunk);
        List updatedItems2 = new ArrayList();

        instance.addingItems(deletedItems2, newItems2, updatedItems2, syncItemFactory);

        assertEquals(0, instance.getDeletedSyncItems().length);
        assertEquals(1, newItems2.size());
        assertEquals(1, instance.getNewSyncItems().length);
        assertEquals(secondChunk.getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(0, instance.getUpdatedSyncItems().length);
        }

        // send third chunk
        {
        List deletedItems3 = new ArrayList();
        List newItems3 =     new ArrayList();
        AbstractSyncItem thirdChunk = new InMemorySyncItem(syncSource, "102");
        thirdChunk.setState(SyncItemState.NEW);
        thirdChunk.setContent("third-chunk ".getBytes());
        newItems3.add(thirdChunk);
        newItems3.add(new InMemorySyncItem(syncSource, "103"));
        List updatedItems3 = new ArrayList();
        updatedItems3.add(new InMemorySyncItem(syncSource, "101"));

        instance.addingItems(deletedItems3, newItems3, updatedItems3, syncItemFactory);

        assertEquals(0, instance.getDeletedSyncItems().length);
        assertEquals(2, newItems3.size());
        assertEquals(2, instance.getNewSyncItems().length);
        assertEquals(((SyncItem)newItems3.get(0)).getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals(((SyncItem)newItems3.get(1)).getKey(),     instance.getNewSyncItems()[1].getKey());
        assertEquals(thirdChunk.getKey(),     instance.getNewSyncItems()[0].getKey());
        assertEquals("first-chunk second-chunk third-chunk ", new String(((FakeStreamingSyncItem)instance.getNewSyncItems()[0]).getData()));
        assertEquals(1, instance.getUpdatedSyncItems().length);
        assertEquals(((SyncItem)updatedItems3.get(0)).getKey(), instance.getUpdatedSyncItems()[0].getKey());
        }
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

}
