/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.framework.engine.transformer;

import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.FakeSyncSource;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSource;
import junit.framework.TestCase;

/**
 *
 * @author testa
 */
public class DataTransformerManagerTest extends TestCase {
    
    public DataTransformerManagerTest(String testName) {
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


    /**
     * Test of transformIncomingItem method, of class DataTransformerManager.
     * @throws Exception
     */
    public void testTransformIncomingItem() throws Exception {
        TransformationInfo info = new TransformationInfo();

        // set up client syncItem
        String clientKeyVal = "1";
        String serverKeyVal = "1001";
        String clientMappedKeyVal = serverKeyVal;
        byte[] clientContent = "client-content".getBytes();
        SyncSource clientSource = new FakeSyncSource("my-client-syncsource", "c-source");
        SyncItemKey clientKey = new SyncItemKey(clientKeyVal);
        AbstractSyncItem clientItem = new InMemorySyncItem(clientSource, clientKey.getKeyAsString());
        clientItem.setContent(clientContent);
        clientItem.setMappedKey(new SyncItemKey(clientMappedKeyVal));


        DataTransformerManager instance = new DataTransformerManager();
        SyncItem expResult = null;
        SyncItem result = instance.transformIncomingItem(info, clientItem);
    }

//    /**
//     * Test of transformOutgoingItem method, of class DataTransformerManager.
//     */
//    public void testTransformOutgoingItem() throws Exception {
//        TransformationInfo info = null;
//        SyncItem item = null;
//        DataTransformerManager instance = new DataTransformerManager();
//        SyncItem expResult = null;
//        SyncItem result = instance.transformOutgoingItem(info, item);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }
//
}
