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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.StreamingSyncItem;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemException;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSource;

/**
 * Fake streaming item used in junit
 * @version $Id$
 */
public class FakeStreamingSyncItem extends StreamingSyncItem {


    // -------------------------------------------------------- Member Variables

    /**
     * holds the received chunks
     */
    private List<byte[]> allChunks = new ArrayList<byte[]>();

    // ------------------------------------------------------------- Constructor

    public FakeStreamingSyncItem(SyncSource syncSource, Object key) {
        super(syncSource, key);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Writes chunkData.length bytes from the specified byte array to this output stream
     * @param chunkData the data.
     * @throws IOException if an I/O error occurs.
     */
    public void write(byte[] chunkData) throws SyncItemException {
        if (isComplete()) {
            throw new SyncItemException("Error adding data to large objet: already " +
                    "complete");
        }
        allChunks.add(chunkData);
    }

    /**
     * Creates a copy of this item
     * The new item and the original one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @return
     */
    public Object cloneItem() {

        FakeStreamingSyncItem syncItem =
                new FakeStreamingSyncItem(this.syncSource, this.key.getKeyValue());

        syncItem.copy(this);

        return syncItem;
    }

    /**
     * Makes the current item a copy of the item passed as parameter
     * The current item and the parameter one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @param syncItem
     */
    @Override
    public void copy(SyncItem syncItem) {

        syncSource = syncItem.getSyncSource();
        key = syncItem.getKey() != null ? new SyncItemKey(syncItem.getKey().getKeyAsString()) : null;
        parentKey = syncItem.getParentKey() != null ? new SyncItemKey(syncItem.getParentKey().getKeyAsString()) : null;
        state = syncItem.getState();

        format = syncItem.getFormat();
        type = syncItem.getType();
        timestamp = syncItem.getTimestamp() != null ? (Timestamp) syncItem.getTimestamp().clone() : null;

        if (syncItem instanceof AbstractSyncItem) {
            mappedKey = ((AbstractSyncItem)syncItem).getMappedKey() != null ? new SyncItemKey(((AbstractSyncItem)syncItem).getMappedKey().getKeyAsString()) : null;
            setProperties(((AbstractSyncItem)syncItem).getProperties());
        }

        if (syncItem instanceof StreamingSyncItem) {
            if (((StreamingSyncItem)syncItem).isComplete()) {
                setAsComplete();
            }
        }
        if (syncItem instanceof FakeStreamingSyncItem) {
            allChunks = ((FakeStreamingSyncItem)syncItem).allChunks;
        }
        if (syncItem.getContent() != null) {
            allChunks.add(0, syncItem.getContent());
        }
    }

    public byte[] getData() throws IOException {
        return concatByteArray(allChunks);
    }

    public void setContent(byte[]content) {
        try {
            write(content);
        } catch (SyncItemException ex) {
            // ignoring
        }
    }
    // --------------------------------------------------------- Private Methods

    /**
     * Concats the list of chunks in a single byte[].
     * Designed in this way for junit testing.
     *
     * @param allChunks the list of chunks to concat
     * @return
     */
    private byte[] concatByteArray(List<byte[]> allChunks) throws IOException {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        for (int i=0; i < allChunks.size(); ++i ) {
            byte[] chunk = allChunks.get(i);
            bout.write(chunk);
        }

        return bout.toByteArray();
    }


    @Override
    public void release() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
