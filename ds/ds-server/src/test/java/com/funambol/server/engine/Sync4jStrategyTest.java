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

package com.funambol.server.engine;

import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.SyncOperation;
import com.funambol.framework.engine.SyncOperationImpl;
import com.funambol.framework.engine.SyncOperationStatus;
import com.funambol.framework.engine.SyncStrategy;
import com.funambol.framework.engine.source.ClientWinsSyncSource;
import com.funambol.framework.engine.source.MergeableSyncSource;
import com.funambol.framework.engine.source.ServerWinsSyncSource;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.server.ClientMapping;
import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

/**
 * Sync4jStrategy's test cases
 * @version $Id$
 */
public class Sync4jStrategyTest extends TestCase {

    public Sync4jStrategyTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // ------------------------------------------------------------ Test methods



    // ---------------------------------------------------- Test private methods

    public void testResolveConflict_DefaultStrategy() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncSource serverSource = new FakeSyncSource("my-server-syncsource", "s-source");
        SyncItemKey serverKey = new SyncItemKey(serverKeyVal);
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setContent(serverContent);
        serverSource.addSyncItem(serverItem);

        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        SyncItemKey clientKey = new SyncItemKey(clientKeyVal);
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKey.getKeyAsString());
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));

        // resolvingItem
        AbstractSyncItem resolvingItem = new InMemorySyncItem(serverSource, "0");

        // invoke method
        Boolean result = (Boolean) PrivateAccessor.invoke(
                     instance,
                     "resolveConflict",
                     new Class[] {SyncSource.class, SyncItemKey.class,
                     AbstractSyncItem.class, AbstractSyncItem.class},
                     new Object[] {serverSource, serverKey, clientItem, resolvingItem});

        // test results
        assertTrue(result);
        assertEquals(clientKey.getKeyAsString(), resolvingItem.getKey().getKeyAsString());
        assertEquals("resolvingItem state should be UPDATED", SyncItemState.UPDATED, resolvingItem.getState());
        assertEquals(clientMappedKeyVal, resolvingItem.getMappedKey().getKeyAsString());
        ArrayAssert.assertEquals(serverContent, resolvingItem.getContent());
    }

    public void testResolveConflict_ClientWins() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncSource serverSource = new FakeClientWinsSyncSource("my-server-syncsource", "s-source");
        SyncItemKey serverKey = new SyncItemKey(serverKeyVal);
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setContent(serverContent);
        serverSource.addSyncItem(serverItem);

        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        SyncItemKey clientKey = new SyncItemKey(clientKeyVal);
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKey.getKeyAsString());
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));

        // resolvingItem
        AbstractSyncItem resolvingItem = new InMemorySyncItem(serverSource, "0");

        // invoke method
        Boolean result = (Boolean) PrivateAccessor.invoke(
                     instance,
                     "resolveConflict",
                     new Class[] {SyncSource.class, SyncItemKey.class,
                     AbstractSyncItem.class, AbstractSyncItem.class},
                     new Object[] {serverSource, serverKey, clientItem, resolvingItem});

        // test results
        assertFalse(result);
        assertEquals(clientKey.getKeyAsString(), resolvingItem.getKey().getKeyAsString());
        assertEquals("resolvingItem state should be SYNCHRONIZED", SyncItemState.SYNCHRONIZED, resolvingItem.getState());
        assertEquals(clientMappedKeyVal, resolvingItem.getMappedKey().getKeyAsString());
        ArrayAssert.assertEquals(clientContent, resolvingItem.getContent());
    }

    public void testResolveConflict_Merge() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncSource serverSource = new FakeMergeableSyncSource("my-server-syncsource", "s-source");
        SyncItemKey serverKey = new SyncItemKey(serverKeyVal);
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setContent(serverContent);
        serverSource.addSyncItem(serverItem);

        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        SyncItemKey clientKey = new SyncItemKey(clientKeyVal);
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKey.getKeyAsString());
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));

        // resolvingItem
        AbstractSyncItem resolvingItem = new InMemorySyncItem(serverSource, "0");

        // invoke method
        Boolean result = (Boolean) PrivateAccessor.invoke(
                     instance,
                     "resolveConflict",
                     new Class[] {SyncSource.class, SyncItemKey.class,
                     AbstractSyncItem.class, AbstractSyncItem.class},
                     new Object[] {serverSource, serverKey, clientItem, resolvingItem});

        // test results
        assertTrue(result);
        assertEquals(clientKey.getKeyAsString(), resolvingItem.getKey().getKeyAsString());
        assertEquals("resolvingItem state should be UPDATED", SyncItemState.UPDATED, resolvingItem.getState());
        assertEquals(clientMappedKeyVal, resolvingItem.getMappedKey().getKeyAsString());
        ArrayAssert.assertEquals(concatByteArray(serverContent, clientContent), resolvingItem.getContent());
    }



    public void testExecConflictNU_ServerWins_IsItemInFilter() throws Throwable {
        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        SyncSource serverSource = new FakeServerWinsSyncSource("my-server-syncsource", "s-source");
        // set up server syncsitem
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setState('U');
        serverItem.setContent(serverContent);
        // add server syncitem to server syncsource
        serverSource.addSyncItem(serverItem);

        // set up client syncsource
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKeyVal);
        clientItem.setState('N');
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));
        // add client syncItem to client syncsource
        clientSource.addSyncItem(clientItem);

        //  sources
        SyncSource[] sources = new SyncSource[] {serverSource, clientSource};

        // operation
        SyncOperationImpl operation = new SyncOperationImpl(clientItem,
                serverItem, SyncOperation.CONFLICT);

        // syncFilterType
        // since the syncsource is not filterable the item is always in filter
        // thus the syncFilterType value is not relevant
        int syncFilterType = SyncStrategy.SYNC_WITH_INCLUSIVE_FILTER;

        SyncOperationStatus[] syncOperationStatus =
                (SyncOperationStatus[]) PrivateAccessor.invoke(
                instance,
                "execConflictNU",
                new Class[]{SyncSource[].class, ClientMapping.class, String.class,
                SyncOperationImpl.class, int.class},
                new Object[]{sources, null, null, operation, syncFilterType});

        assertEquals(1, syncOperationStatus.length);
        assertTrue(syncOperationStatus[0] instanceof Sync4jOperationStatusConflict);
        assertTrue("Expecting changes to the client",
                syncOperationStatus[0].getOperation().isAOperation());
        ArrayAssert.assertEquals("Content differs from expected", 
                serverContent,
                syncOperationStatus[0].getOperation().getSyncItemA().getContent());
        assertEquals("syncItemA state should be UPDATED", SyncItemState.UPDATED, syncOperationStatus[0].getOperation().getSyncItemA().getState());
    }

    public void testExecConflictNU_Merged_IsItemInFilter_ClientItemChanged() throws Throwable {
        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        SyncSource serverSource = new FakeMergeableSyncSource("my-server-syncsource", "s-source");
        // set up server syncsitem
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setState('U');
        serverItem.setContent(serverContent);
        // add server syncitem to server syncsource
        serverSource.addSyncItem(serverItem);

        // set up client syncsource
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKeyVal);
        clientItem.setState('N');
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));
        // add client syncItem to client syncsource
        clientSource.addSyncItem(clientItem);

        //  sources
        SyncSource[] sources = new SyncSource[] {serverSource, clientSource};

        // operation
        SyncOperationImpl operation = new SyncOperationImpl(clientItem,
                serverItem, SyncOperation.CONFLICT);

        // syncFilterType
        // since the syncsource is not filterable the item is always in filter
        // thus the syncFilterType value is not relevant
        int syncFilterType = SyncStrategy.SYNC_WITH_INCLUSIVE_FILTER;

        SyncOperationStatus[] syncOperationStatus =
                (SyncOperationStatus[]) PrivateAccessor.invoke(
                instance,
                "execConflictNU",
                new Class[]{SyncSource[].class, ClientMapping.class, String.class, SyncOperationImpl.class, int.class},
                new Object[]{sources, null, null, operation, syncFilterType});

        assertEquals(1, syncOperationStatus.length);
        assertTrue(syncOperationStatus[0] instanceof Sync4jOperationStatusConflict);
        assertTrue("Expecting changes to the client",
                syncOperationStatus[0].getOperation().isAOperation());
        ArrayAssert.assertEquals("Content differs from expected",
                concatByteArray(serverContent, clientContent),
                syncOperationStatus[0].getOperation().getSyncItemA().getContent());
        assertEquals("syncItemA state should be UPDATED", SyncItemState.UPDATED, syncOperationStatus[0].getOperation().getSyncItemA().getState());
    }

    public void testExecConflictNU_ClientWins_IsItemInFilter() throws Throwable {
        Sync4jStrategy instance = new Sync4jStrategy();

        // set up server syncsource
        SyncSource serverSource = new FakeClientWinsSyncSource("my-server-syncsource", "s-source");
        // set up server syncsitem
        String serverKeyVal = "1001";
        byte[] serverContent = "server-content".getBytes();
        SyncItem serverItem = new InMemorySyncItem(serverSource, serverKeyVal);
        serverItem.setState('U');
        serverItem.setContent(serverContent);
        // add server syncitem to server syncsource
        serverSource.addSyncItem(serverItem);

        // set up client syncsource
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        // set up client syncItem
        String clientKeyVal = "1";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKeyVal);
        clientItem.setState('N');
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));
        // add client syncItem to client syncsource
        clientSource.addSyncItem(clientItem);

        //  sources
        SyncSource[] sources = new SyncSource[] {serverSource, clientSource};

        // operation
        SyncOperationImpl operation = new SyncOperationImpl(clientItem,
                serverItem, SyncOperation.CONFLICT);

        // syncFilterType
        // since the syncsource is not filterable the item is always in filter
        // thus the syncFilterType value is not relevant
        int syncFilterType = SyncStrategy.SYNC_WITH_INCLUSIVE_FILTER;

        SyncOperationStatus[] syncOperationStatus =
                (SyncOperationStatus[]) PrivateAccessor.invoke(
                instance,
                "execConflictNU",
                new Class[]{SyncSource[].class, ClientMapping.class, String.class, SyncOperationImpl.class, int.class},
                new Object[]{sources, null, null, operation, syncFilterType});

        assertEquals(1, syncOperationStatus.length);
        assertTrue(syncOperationStatus[0] instanceof Sync4jOperationStatusConflict);
        assertFalse("Not expecting changes to the client",
                syncOperationStatus[0].getOperation().isAOperation());
        ArrayAssert.assertEquals("Content differs from expected",
                clientContent,
                syncOperationStatus[0].getOperation().getSyncItemA().getContent());
        assertEquals("syncItemA state should be NEW", SyncItemState.NEW, syncOperationStatus[0].getOperation().getSyncItemA().getState());
        assertEquals("operation differs from expecetd", SyncOperation.CONFLICT,syncOperationStatus[0].getOperation().getOperation());
    }



    public void testGetConflictResolution_Default() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();
        SyncSource serverSource = new FakeSyncSource("my-server-syncsource", "s-source");

        Integer result = (Integer) PrivateAccessor.invoke(
                     instance,
                     "getConflictResolution",
                     new Class[] {SyncSource.class},
                     new Object[] {serverSource});

        assertEquals(Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS, result.intValue());
    }

    public void testGetConflictResolution_ClientWins() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();
        SyncSource serverSource = new FakeClientWinsSyncSource("my-server-syncsource", "s-source");

        Integer result = (Integer) PrivateAccessor.invoke(
                     instance,
                     "getConflictResolution",
                     new Class[] {SyncSource.class},
                     new Object[] {serverSource});

        assertEquals(Sync4jStrategy.CONFLICT_RESOLUTION_CLIENT_WINS, result.intValue());
    }

    public void testGetConflictResolution_ServerWins() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();
        SyncSource serverSource = new FakeServerWinsSyncSource("my-server-syncsource", "s-source");

        Integer result = (Integer) PrivateAccessor.invoke(
                     instance,
                     "getConflictResolution",
                     new Class[] {SyncSource.class},
                     new Object[] {serverSource});

        assertEquals(Sync4jStrategy.CONFLICT_RESOLUTION_SERVER_WINS, result.intValue());
    }

    public void testGetConflictResolution_MergeableWins() throws Throwable {

        Sync4jStrategy instance = new Sync4jStrategy();
        SyncSource serverSource = new FakeMergeableSyncSource("my-server-syncsource", "s-source");

        Integer result = (Integer) PrivateAccessor.invoke(
                     instance,
                     "getConflictResolution",
                     new Class[] {SyncSource.class},
                     new Object[] {serverSource});

        assertEquals(Sync4jStrategy.CONFLICT_RESOLUTION_MERGE_DATA, result.intValue());
    }


    // --------------------------------------------------------- Private Methods

    private byte[] concatByteArray(byte[] first, byte[] second) {
        byte[] result = new byte[first.length + second.length];
        System.arraycopy(first, 0, result, 0, first.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }

    // -------------------------------------------------------- Internal Classes

    class FakeClientWinsSyncSource extends FakeSyncSource implements ClientWinsSyncSource {

        FakeClientWinsSyncSource(String name, String sourceURI) {
            super(name, sourceURI);
        }
    }

    class FakeServerWinsSyncSource extends FakeSyncSource implements ServerWinsSyncSource {

        FakeServerWinsSyncSource(String name, String sourceURI) {
            super(name, sourceURI);
        }
    }

    class FakeMergeableSyncSource extends FakeSyncSource implements MergeableSyncSource {

        FakeMergeableSyncSource(String name, String sourceURI) {
            super(name, sourceURI);
        }

        public boolean mergeSyncItems(SyncItemKey serverKey, SyncItem clientItem) throws SyncSourceException {
            SyncItem serverItem = getSyncItemFromId(serverKey);
            byte[] mergedContent = concatByteArray(serverItem.getContent(), clientItem.getContent());
            clientItem.setContent(mergedContent);
            return true;
        }
    }
}
