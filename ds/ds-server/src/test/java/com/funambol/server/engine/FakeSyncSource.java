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

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.SyncSourceInfo;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @version $Id: $
 */
public class FakeSyncSource implements SyncSource {

    private String name;
    private String sourceURI;

    private Map<String, SyncItem> addedItems =   new HashMap<String, SyncItem>();
    private Map<String, SyncItem> updatedItems = new HashMap<String, SyncItem>();
    private Map<String, SyncItem> deletedItems = new HashMap<String, SyncItem>();

    private Map<String, SyncItem> allItems =     new HashMap<String, SyncItem>();


    // ------------------------------------------------------------- Constructor

    public FakeSyncSource(String name, String sourceURI) {
        this.name = name;
        this.sourceURI = sourceURI;
    }

    // ----------------------------------------------------- Implemented Methods

    public String getName() {
        return this.name;
    }

    public String getSourceURI() {
        return this.sourceURI;
    }

    public SyncSourceInfo getInfo() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void beginSync(SyncContext syncContext) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void endSync() throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void commitSync() throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs, Timestamp untilTs) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem addSyncItem(SyncItem syncInstance) throws SyncSourceException {
        allItems.put(syncInstance.getKey().getKeyAsString(), syncInstance);
        return syncInstance;
    }

    public SyncItem updateSyncItem(SyncItem syncInstance) throws SyncSourceException {
        allItems.put(syncInstance.getKey().getKeyAsString(), syncInstance);
        return syncInstance;
    }

    public void removeSyncItem(SyncItemKey itemKey, Timestamp time, boolean softDelete) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {
        return allItems.get(syncItemKey.getKeyAsString());
    }

    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setOperationStatus(String operationName, int status, SyncItemKey[] keys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    // --------------------------------------------------------- Private Methods
}
