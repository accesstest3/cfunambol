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

package com.funambol.server.engine;

import com.funambol.framework.core.ComplexData;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Source;
import com.funambol.framework.engine.InMemorySyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.framework.engine.transformer.TransformationInfo;
import com.funambol.framework.engine.transformer.TransformerException;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.Sync4jUser;
import java.util.HashMap;
import java.util.Map;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * Sync4jEngine's test cases
 * @version $Id$
 */
public class Sync4jEngineTest extends TestCase {
    
    public Sync4jEngineTest(String testName) {
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
     * Test of completeItemInfo method, of class Sync4jEngine.
     */
    public void testCompleteItemInfo() throws Exception {

        String sourceURI = "card";
        String itemLocalURI = "123";

        Sync4jEngine instance = new Sync4jEngine();

        // setup Sync4jEngine's serverSources member variable
        SyncSource serverSyncSource = new FakeStreamingSyncSource(null, sourceURI);
        SyncItem syncItem = new InMemorySyncItem(serverSyncSource, itemLocalURI);
        syncItem.setContent(getStringLong10000Char().getBytes());
        syncItem.setType("text/directory profile=vCard");
        serverSyncSource.addSyncItem(syncItem);
        Map<String, SyncSource> serverSources  = new HashMap<String, SyncSource>();
        serverSources.put(sourceURI, serverSyncSource);
        injectSyncsources(instance, serverSources);

        // setup Sync4jEngine's clientMappings member variable
        ClientMapping clientMapping = new ClientMapping(null, sourceURI);
        Map<String, ClientMapping> clientMappings = new HashMap<String, ClientMapping>();
        clientMappings.put(sourceURI, clientMapping);
        injectClientMappings(instance, clientMappings);

        // setup Sync4jEngine's principal member variable
        Sync4jPrincipal principal = new Sync4jPrincipal();
        Sync4jUser user = new Sync4jUser();
        user.setPassword("password");
        principal.setUser(user);
        injectSync4jPrincipal(instance, principal);

        // setup Sync4jEngine's dataTransformerManager member variable
        DataTransformerManager dataTransformerManager = new DataTransformerManager() {

            @Override
            public SyncItem transformOutgoingItem(TransformationInfo info, SyncItem item)
                    throws TransformerException {
                return item;
            }
        };
        injectDataTransformerManager(instance, dataTransformerManager);

        String mimeType = "";
        Item item = new Item(new Source(itemLocalURI,null), null, null,
                new ComplexData(""), false);

        assertEquals("The data must be initially empty", 0, item.getData().getData().length());
        instance.completeItemInfo(mimeType, sourceURI, item);
        assertEquals("Data not correctly filled: wrong data length",
                10000, item.getData().getData().length());
    }


    // ------------------------------------------------- Private Utility Methods
    private String getStringLong10000Char() {
        StringBuilder content = new StringBuilder();
        for (int i =0; i < 1000; ++i) {
            content.append("1234567890");
        }
        return content.toString();
    }

    private void injectSyncsources(Sync4jEngine instance, 
            Map<String, SyncSource> serverSources) throws NoSuchFieldException {
        PrivateAccessor.setField(instance, "serverSources", serverSources);
    }

    private void injectClientMappings(Sync4jEngine instance, 
            Map<String, ClientMapping> clientMappings) throws NoSuchFieldException {
        PrivateAccessor.setField(instance, "clientMappings", clientMappings);
    }

    private void injectSync4jPrincipal(Sync4jEngine instance,
            Sync4jPrincipal principal) throws NoSuchFieldException {
        PrivateAccessor.setField(instance, "principal", principal);
    }

    private void injectDataTransformerManager(Sync4jEngine instance,
            DataTransformerManager dataTransformerManager) throws NoSuchFieldException {
        PrivateAccessor.setField(instance, "dataTransformerManager", dataTransformerManager);
    }

}
