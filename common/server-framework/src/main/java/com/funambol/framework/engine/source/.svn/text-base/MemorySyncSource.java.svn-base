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
package com.funambol.framework.engine.source;

import java.io.IOException;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.sql.Timestamp;

import com.funambol.framework.engine.AbstractSyncItem;
import com.funambol.framework.engine.InMemorySyncItem;

import com.funambol.framework.engine.SyncProperty;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.StreamingSyncItem;
import com.funambol.framework.engine.SyncItemException;
import com.funambol.framework.engine.SyncItemFactory;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/*
 * This source represents a <i>SyncSource</i> where all items are kept in memory.
 * A <i>MemorySyncSource</i> can be created passing in the constructor the name
 * of the source and the source URI. The items list to handle will be added to
 * the internal lists using <i>addingItems(...)</i> method.
 * <p>
 * <code>existingSyncItems</code> represents the items on the device.
 * <p>
 * <code>newSyncItems</code>, <code>updatedSyncItems</code> and
 * <code>deletedSyncItems</code> are instead the modifications reported with
 * the last message.
 *
 * @version $Id: MemorySyncSource.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class MemorySyncSource
extends AbstractSyncSource
implements SyncSource, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static final String NAME = "MemorySyncSource";

    // ----------------------------------------------------- Private data member

    // the content of the SyncML client
    private List existingSyncItems;

    private List newSyncItems     ;
    private List updatedSyncItems ;
    private List deletedSyncItems ;

    /**
     * LO SyncItem - not null if any LO is being retrieved
     */
    private AbstractSyncItem lo;

    private List allChunks     = null;
    private int  loContentSize = 0   ;

    private FunambolLogger logger = FunambolLoggerFactory.getLogger("funambol.engine.memory-sync-source");
    
    // ------------------------------------------------------------ Constructors

    public MemorySyncSource() {
        lo = null;
    }

    public MemorySyncSource(String name, String sourceURI) {
        super(name, sourceURI);
        lo = null;
        existingSyncItems   = new ArrayList();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sets the name of the source.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * @see SyncSource
     *
     */
    public SyncItem[] getSyncItemsFromIds(SyncItemKey[] syncItemKeys)
    throws SyncSourceException {
        List ret           = new ArrayList();
        SyncItem  syncItem = null;

        for (int i=0; ((syncItemKeys != null) && (i<syncItemKeys.length)); ++i) {
            syncItem = getSyncItemFromId(syncItemKeys[i]);

            if (syncItem != null) {
                ret.add(syncItem);
            }
        }

        return (SyncItem[])ret.toArray(new SyncItem[ret.size()]);
    }

    /*
     * @see SyncSource
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs,
                                            Timestamp untilTs)
    throws SyncSourceException {
        return extractKeys(newSyncItems);
    }

    /**
     * Returns the new items
     * @return SyncItem[]
     */
    public SyncItem[] getNewSyncItems() {
        return (SyncItem[])newSyncItems.toArray(new SyncItem[0]);
    }

    /*
     * @see SyncSource
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,
                                                Timestamp untilTs)
    throws SyncSourceException {
        return extractKeys(deletedSyncItems);
    }

    /**
     * Returns the deleted items
     * @return SyncItem[]
     */
    public SyncItem[] getDeletedSyncItems() {
        return (SyncItem[])deletedSyncItems.toArray(new SyncItem[0]);
    }

    /**
     * @return an array of keys containing the <i>SyncItem</i>'s key of the updated
     *        items after the last synchronizazion.
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs,
                                                Timestamp untilTs)
    throws SyncSourceException {
        return extractKeys(updatedSyncItems);
    }

    /**
     * Returns the updated items
     * @return SyncItem[]
     */
    public SyncItem[] getUpdatedSyncItems() {
        return (SyncItem[])updatedSyncItems.toArray(new SyncItem[0]);
    }

    /**
     * @see SyncSource
     */
    public SyncItem[] getAllSyncItems()
    throws SyncSourceException {
        return (SyncItem[])existingSyncItems.toArray(new SyncItem[0]);
    }

    /**
     * @see SyncSource
     */
    public SyncItemKey[] getAllSyncItemKeys()
    throws SyncSourceException {
        return extractKeys(existingSyncItems);
    }


    /**
     * @see SyncSource
     */
    public boolean isModified() {

        return (getDeletedSyncItems().length > 0)
                || (getNewSyncItems().length     > 0)
                || (getUpdatedSyncItems().length > 0);

    }

    /**
     * Adds items to the internal lists.
     *
     * @param deletedItems
     * @param newItems
     * @param updatedItems
     * @param syncItemFactory The SyncItemFactory to get
     * the StreamingSyncItem where to write the large object chunks. If null,
     * the chunks will be merged by the MemorySuncSource
     */
    public void addingItems(List deletedItems,
            List newItems,
            List updatedItems,
            SyncItemFactory syncItemFactory) {

        //
        // Large object handling
        // =====================
        // If the server is still retrieving a large object, LO will be set to
        // the item being retrieved and the new piece of data must be added at
        // the chunk list. The partial item must be then removed by the
        // list that contains it. When the LO is fully retrieved, the chunks
        // contained in the list will be chained in a unique byte[] in order to
        // set the content of the LO. The item will be updated in the containing
        // list and from now on it will like any other item. The property
        // PROPERTY_SIZE will be set to the really content size.
        //
        if (deletedSyncItems == null) {
            deletedSyncItems = new ArrayList();
        } else {
            deletedSyncItems.clear();
        }

        if (newSyncItems == null) {
            newSyncItems = new ArrayList();
        } else {
            newSyncItems.clear();
        }

        if (updatedSyncItems == null) {
            updatedSyncItems = new ArrayList();
        } else {
            updatedSyncItems.clear();
        }

        deletedSyncItems.addAll(deletedItems);
        newSyncItems.addAll(newItems);
        updatedSyncItems.addAll(updatedItems);

        // manage the expected next chunk if any
        if (expectingNextChunk()) {
            List listContainingExpectedChunk = getListContainingExpectedChunk();
            if (listContainingExpectedChunk != null) {

                int chunkIdx = listContainingExpectedChunk.indexOf(lo);
                InMemorySyncItem chunk = (InMemorySyncItem) listContainingExpectedChunk.remove(chunkIdx);

                // assert (chunk != null) {
                if (isAlreadyMergedBySynclet(chunk)) {
                    manageMergedBySyncletChunk(lo, chunk);
                } else {
                    mergeLOChunk(lo, chunk);
                }
                listContainingExpectedChunk.add(chunkIdx, lo);
                if (isExpectedLastChunk(chunk)) {
                    lo = null;
                }
            } else {
                manageAlert223();
            }
        }

        // manage the first chunk if any
        // N.B. There could be a first chunk even if there was already a chunk
        // ending a previous large object but it must be indeed the last one,
        // so the syncsource is not expecting a chunk.
        List listContainingFirstChunk = getListContainingFirstChunk();
        if (listContainingFirstChunk != null && !(expectingNextChunk())) {
            AbstractSyncItem chunk = removeFirstChunkFrom(listContainingFirstChunk);
            
            lo = getNewLO(syncItemFactory, chunk);
            
            listContainingFirstChunk.add(lo);
        }

        //
        // We have to make sure that the complete view is consistent so that:
        // - new items are not in deleted and updated lists
        // - updated items are not in new and deleted list
        // - deleted items are not in new and updated lists
        //
        SyncItem syncItem = null;
        Iterator i = deletedSyncItems.iterator();
        while (i.hasNext()) {
            syncItem = (SyncItem) i.next();
            newSyncItems.remove(syncItem);
            updatedSyncItems.remove(syncItem);
        }

        i = updatedSyncItems.iterator();
        while (i.hasNext()) {
            syncItem = (SyncItem) i.next();
            newSyncItems.remove(syncItem);
            deletedSyncItems.remove(syncItem);
        }

        i = newSyncItems.iterator();
        while (i.hasNext()) {
            syncItem = (SyncItem) i.next();
            updatedSyncItems.remove(syncItem);
            deletedSyncItems.remove(syncItem);
        }


        if (syncItemFactory != null) {
            //
            // we need to convert all new and updated items in the format
            // (class) returned by the factory
            //
            for (int j = 0; j<newSyncItems.size(); j++) {
                SyncItem item = (SyncItem) newSyncItems.get(j);
                if (item == null) {
                    continue;
                }
                if (item.getState() != SyncItemState.PARTIAL) {
                    AbstractSyncItem newItem = syncItemFactory.getSyncItem(item.getSyncSource(), item.getKey());

                    newItem.copy(item);
                    if (newItem instanceof StreamingSyncItem) {
                        ((StreamingSyncItem)newItem).setAsComplete();
                    }
                    //
                    // replacing old instance with the new one
                    //
                    newSyncItems.remove(j);
                    newSyncItems.add(j, newItem);
                }
            }
            for (int j = 0; j<updatedSyncItems.size(); j++) {
                SyncItem item = (SyncItem) updatedSyncItems.get(j);
                if (item == null) {
                    continue;
                }
                if (item.getState() != SyncItemState.PARTIAL) {
                    AbstractSyncItem newItem = syncItemFactory.getSyncItem(item.getSyncSource(), item.getKey());

                    newItem.copy(item);
                    if (newItem instanceof StreamingSyncItem) {
                        ((StreamingSyncItem)newItem).setAsComplete();
                    }
                    //
                    // replacing old instance with the new one
                    //
                    updatedSyncItems.remove(j);
                    updatedSyncItems.add(j, newItem);
                }
            }
        }


    }

    /**
     * Sets the existing items
     *
     * @param items existing items
     *
     */
    public void setExistingItems(List items) {
        existingSyncItems = items;
    }

    /**
     * Search the items for an item with exactly the same binary content.
     *
     * @return an item from a twin item. Each source implementation is free to
     *         interpret this as it likes (i.e.: comparing all fields).
     *
     * @param syncItemTwin the twin item
     *
     * @throws SyncSourceException in case of errors (for instance if the
     *         underlying data store runs into problems)
     *
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItemTwin)
    throws SyncSourceException {

        String contentToSearch = null;

        contentToSearch = new String(syncItemTwin.getContent());

        SyncItem[] all = getAllSyncItems();

        String content    = null;
        List itemKeys     = new ArrayList();

        for (int i = 0; i < all.length; ++i) {
            content = new String(all[i].getContent());
            if (content.equals(contentToSearch)) {
                itemKeys.add(all[i].getKey());
            }
        }

        return (SyncItemKey[])itemKeys.toArray(new SyncItemKey[itemKeys.size()]);
    }

    /**
     * see SyncSource
     */
    public boolean isSyncItemInFilterClause(SyncItem item) throws SyncSourceException {
        return true;
    }

    /**
     * see SyncSource
     */
    public boolean isSyncItemInFilterClause(SyncItemKey key) throws SyncSourceException {
        return true;
    }


    // --------------------------------------------------------- Private methods

    private SyncItemKey[] extractKeys(Collection syncItems) {
        SyncItemKey[] keys = new SyncItemKey[syncItems.size()];

        SyncItem syncItem = null;
        int j = 0;
        for(Iterator i = syncItems.iterator(); i.hasNext(); ++j) {
           syncItem = (SyncItem)i.next();

           keys[j] = syncItem.getKey();
        } // next i, j

        return keys;
    }

    private SyncItemKey[] extractKeys(SyncItem[] syncItems) {
        SyncItemKey[] keys = new SyncItemKey[syncItems.length];

        for(int j = 0; j<syncItems.length; ++j) {
           keys[j] = syncItems[j].getKey();
        } // next i, j

        return keys;
    }

    public void commitSync() throws SyncSourceException {
        // nothing to do
    }

    // --------------------------------------------------------- Private Methods
    private SyncItem setSyncItem(SyncItem syncItem)
        throws SyncSourceException {

        newSyncItems.remove(syncItem);
        deletedSyncItems.add(syncItem);
        updatedSyncItems.add(syncItem);

        AbstractSyncItem newSyncItem = (AbstractSyncItem) syncItem.cloneItem();
        newSyncItem.setSyncSource(this);
        newSyncItem.setState(SyncItemState.NEW);

        return newSyncItem;
    }

    /**
     * Merges the given <code>chunk</code> with the existing <code>lo</code>
     * item if it is the last chunk. Otherwise adds the chunk to the list of
     * chunks.
     *
     * @param largeObject the SyncItem representing the large object
     * @param chunk the chunk to handle
     */
    private void mergeLOChunk(AbstractSyncItem largeObject, InMemorySyncItem chunk) {
        // assert largeObject != null

        // it has to be called for every chunk and not just the last one
        setPropertiesEndedLO(largeObject, chunk);

        // set content
        if (!(largeObject instanceof StreamingSyncItem)) {

            if (chunk.getContent() == null) {
                return;
            }

            //
            // Adds the new chunk to the chunks list and set the real large object
            // content size in order to use it to set the size of the byte[] that
            // will contain the large object.
            //
            if (allChunks == null) {
                allChunks = new ArrayList();
            }

            allChunks.add(chunk.getContent());
            loContentSize += chunk.getContent().length;

            if (isExpectedLastChunk(chunk)) {
                //
                // Only when the last chunk is received, the byte[] will be chained in
                // order to create a single byte[]: it will be the content of the LO.
                //
                // NOTE: the first chunk is not stored in allChunks but as content
                // of the 'largeObject' and so we add it as first element of allChunks
                //
                loContentSize += largeObject.getContent().length;
                allChunks.add(0, largeObject.getContent());
                byte[] content = concatByteArray(allChunks, loContentSize);
                largeObject.setContent(content);

                largeObject.setProperty(
                        new SyncProperty(AbstractSyncItem.PROPERTY_CONTENT_SIZE,
                        new Long(loContentSize)));

                allChunks = null;
                loContentSize = 0;
            }
        } else if (largeObject instanceof StreamingSyncItem) {
            try {
                byte[] chunkData = chunk.getContent();
                ((StreamingSyncItem) largeObject).write(chunkData);
                if (isExpectedLastChunk(chunk)) {
                    ((StreamingSyncItem) largeObject).setAsComplete();
                }
            } catch (SyncItemException ex) {
                logger.error("Error writing chunk in the StreamingSyncItem", ex);
                /**
                 * @todo: handle PROPERTY_ERROR_MSG
                 */
                largeObject.setProperty(new SyncProperty(StreamingSyncItem.PROPERTY_ERROR_MSG, ex));
            }
        }

        // set state
        if (isExpectedLastChunk(chunk)) {
            largeObject.setState(chunk.getState());
        }
    }

    /**
     * Sets the properties of the large object, stored in lo, with the property
     * of the chunk.
     *
     * @param largeObject
     * @param lastChunk the chunk used to set the properties of LO
     */
    private void setPropertiesEndedLO(AbstractSyncItem largeObject, InMemorySyncItem lastChunk) {
        largeObject.setFormat    (lastChunk.getFormat());
        largeObject.setType      (lastChunk.getType());
        largeObject.setTimestamp (lastChunk.getTimestamp());
        largeObject.addProperties(lastChunk.getProperties());
    }

    /**
     * Concats the list of chunks in a single byte[].
     * Designed in this way for junit testing.
     *
     * @param allChunks the list of chunks to concat
     * @param loContentSize the sum of the chunk lenght contained in the list
     */
    private byte[] concatByteArray(List allChunks, int loContentSize) {
        byte[] result = new byte[loContentSize];

        byte[] partialChunk = null;
        int pos = 0;
        for (int i=0; i<allChunks.size(); i++) {
            partialChunk = (byte[])allChunks.get(i);
            System.arraycopy(partialChunk, 0, result, pos, partialChunk.length);
            pos += partialChunk.length;
            partialChunk = null;
            allChunks.set(i, null);
        }

        return result;
    }

    /**
     * Updates the given item
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException {
        return setSyncItem(syncItem);
    }


    /**
     * Adds the given item
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem addSyncItem(SyncItem syncItem)
    throws SyncSourceException {
        return setSyncItem(syncItem);
    }

    /**
     * Removes the given item
     * @param syncItem SyncItem
     * @param time Timestamp
     * @throws SyncSourceException
     */
    public void removeSyncItem(SyncItemKey syncItemKey, Timestamp time)
    throws SyncSourceException {
        if (syncItemKey == null) {
            return;
        }
        //
        // We create a dummyItem just to remove the item with the same key
        SyncItem dummyItem = new InMemorySyncItem(this, syncItemKey.getKeyAsString());

        newSyncItems.remove      (dummyItem);
        updatedSyncItems.remove  (dummyItem);
        deletedSyncItems.add     (dummyItem);
    }

    /**
     * Removes the given item
     * @param syncItem SyncItem
     * @param time Timestamp
     * @param softDelete is a soft delete
     * @throws SyncSourceException
     */
    public void removeSyncItem(SyncItemKey syncItemKey,
                               Timestamp   time,
                               boolean     softDelete)
    throws SyncSourceException {

        //
        // Here, we handle all deletions as hard deletion
        //
        removeSyncItem(syncItemKey, time);
    }

    /*
     * @see SyncSource
     *
     * This implementation cycles through all SyncItems looking for the
     * specified key.
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {
        SyncItem[] all = this.getAllSyncItems();

        for (int i=0; ((all != null) && (i<all.length)); ++i) {
            if (syncItemKey.equals(all[i].getKey())) {
                return all[i];
            }
        }

        return null;
    }

    /**
     * @see SyncSource
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {
        SyncItem[] all = this.getAllSyncItems();

        for (int i = 0; ( (all != null) && (i < all.length)); ++i) {
            if (syncItemKey.equals(all[i].getKey())) {
                return all[i].getState();
            }
        }

        return SyncItemState.NOT_EXISTING;
    }

    /**
     * @see SyncSource
     */
    public void setOperationStatus(String        operationName,
                                   int           status,
                                   SyncItemKey[] keys) {

    }

    private void manageAlert223() {
        // @todo ERROR expecting a chunk but it is not present

        // If LO is not null and updatedItems and newItems lists don't contain
        // the LO, this means that there is no the end of data of the LO.
        lo = null;
        allChunks = null;
        loContentSize = 0;
    }

    /**
     *
     * @return true if the large object is not complete and a next chunk is
     * expected
     */
    private boolean expectingNextChunk() {
        return lo != null;
    }

    /**
     *
     * @param syncItems the list of SyncItems where to look into to see if it
     * contains the expected large object chunk.
     * @return true if the expected chunk is in the list passed as parameter
     */
    private boolean expectedChunkIsIn(List<AbstractSyncItem> syncItems) {
        // assert(expectingAChunk())
        return (syncItems.indexOf(lo) != -1);
    }

    /**
     *
     * @param syncItem the SyncItem to check.
     * @return true if the syncItem parameter is the first chunk of a large
     * object
     */
    private boolean isFirstChunk(AbstractSyncItem syncItem) {
        return (!expectingNextChunk()) && (syncItem.getState() == SyncItemState.PARTIAL);
    }

    /**
     *
     * @param syncItem the SyncItem to check.
     * @return true if the syncitem parameter is a chunk of a rage object and it
     * is not the first.
     */
    private boolean isExpectedNextChunk(AbstractSyncItem syncItem) {
        return expectingNextChunk() && (lo.getKey().equals(syncItem.getKey()));
    }

    /**
     *
     * @param syncItem the SyncItem to check.
     * @return true if the SyncItem parameter is the last chunk of a large
     * object
     */
    private boolean isExpectedLastChunk(AbstractSyncItem syncItem) {
        return isExpectedNextChunk(syncItem) && (syncItem.getState() != SyncItemState.PARTIAL);
    }

    /**
     *
     * @param syncItems the list of syncItems to check
     * @return true if the list contains the first chunk of a large object
     */
    private boolean aFirstChunkIsIn(List<AbstractSyncItem> syncItems) {
        return getFirstChunkIndex(syncItems) != -1;
    }

    /**
     *
     * @param syncItems the list of syncItems to check
     * @return the index in the list corresponding to the first chunk of a large
     * object
     */
    private int getFirstChunkIndex(List<AbstractSyncItem> syncItems) {

        for (int index = 0; index < syncItems.size(); ++index) {
            AbstractSyncItem syncItem = syncItems.get(index);
            if (isFirstChunk(syncItem)) {
                return index;
            }
        }
        return -1;

    }

    /**
     * removes from the list passed as parameter the syncItem corresponding to
     * the expected large object chunk.
     * @param syncItems the SyncItem list from which remove the chunk
     * @return the SyncItem removed from the list, representing the large object
     * chunk.
     */
    private AbstractSyncItem removeExpectedChunkFrom(List<AbstractSyncItem> syncItems) {
        int chunkIdx = syncItems.indexOf(lo);
        return syncItems.remove(chunkIdx);
    }

    /**
     * removes from the syncItem list the first chunk of a large object
     * @param syncItems
     * @return the SyncItem removed from the list
     */
    private AbstractSyncItem removeFirstChunkFrom(List<AbstractSyncItem> syncItems) {
        return syncItems.remove(getFirstChunkIndex(syncItems));
    }

    /**
     * Creates a new SyncItem cloning it from chunk
     * @param syncItemFactory the factory where to get the SyncItem
     * if the SyncSource is a Streaming Sync Source
     * @param chunk the SyncItem used to cloneItem
     * @return a new SyncItem cloned from chunk
     */
    private AbstractSyncItem getNewLO(SyncItemFactory syncItemFactory,
                                      AbstractSyncItem chunk) {

        AbstractSyncItem syncItem;
        if (syncItemFactory == null) {
            syncItem = new InMemorySyncItem(chunk.getSyncSource(), chunk.getKey().getKeyAsString());
        } else {
            syncItem = syncItemFactory.getSyncItem(chunk.getSyncSource(), chunk.getKey().getKeyAsString());
        }
        syncItem.copy(chunk);
        return syncItem;
    }

    /**
     *
     * @param chunk the syncitem to check
     * @return true if the syncItem parameter is a large object already merged
     * by the synclets.
     * A syncItem is considered to be already merged by a synclet if it is not
     * the first chunk but it has the property DECLARED_SIZE defined.
     */
    private boolean isAlreadyMergedBySynclet(InMemorySyncItem chunk) {
        SyncProperty newSize =
                chunk.getProperty(AbstractSyncItem.PROPERTY_DECLARED_SIZE);

        return (newSize != null && !isFirstChunk(chunk) && isExpectedLastChunk(chunk));
    }

    /**
     *
     * @param largeObject
     * @param chunk
     */
    private void manageMergedBySyncletChunk(AbstractSyncItem largeObject,
                                            InMemorySyncItem chunk) {
        // assert largeObject != null

        // set content
        if (!(largeObject instanceof StreamingSyncItem)) {

            byte[] chunkData = chunk.getContent();

            largeObject.setContent(chunkData);

            largeObject.setProperty(
                    new SyncProperty(AbstractSyncItem.PROPERTY_CONTENT_SIZE,
                    new Long(chunkData.length)));

            allChunks = null;
            loContentSize = 0;

        } else if (largeObject instanceof StreamingSyncItem) {

            try {
                ((StreamingSyncItem) largeObject).release();
            } catch (SyncItemException ex) {
                logger.error("Error releasing sync item", ex);
                largeObject.setProperty(new SyncProperty(StreamingSyncItem.PROPERTY_ERROR_MSG, ex));
            }
            
            try {
                byte[] chunkData = chunk.getContent();
                ((StreamingSyncItem) largeObject).write(chunkData);
                if (isExpectedLastChunk(chunk)) {
                    ((StreamingSyncItem) largeObject).setAsComplete();
                }
            } catch (SyncItemException ex) {
                logger.error("Error writing data", ex);
                largeObject.setProperty(new SyncProperty(StreamingSyncItem.PROPERTY_ERROR_MSG, ex));
            }

        }

        // set state
        if (isExpectedLastChunk(chunk)) {
            largeObject.setState(chunk.getState());
        }

        // it has to be called for every chunk and not just the last one
        setPropertiesEndedLO(largeObject, chunk);
    }

    /**
     * Select between newSyncItems and updatedSyncItems the one containing the
     * expected chunk.
     * @return the reference to the selected list if any or null.
     */
    private List getListContainingExpectedChunk() {
        List listContainingChunk = null;
        if (expectedChunkIsIn(newSyncItems)) {
            listContainingChunk = newSyncItems;
        } else if (expectedChunkIsIn(updatedSyncItems)) {
            listContainingChunk = updatedSyncItems;
        }
        return listContainingChunk;
    }

    /**
     * Select between newSyncItems and updatedSyncItems the one containing the
     * a SyncItem representing the firs chunk of a large object.
     * @return the reference to the selected list if any or null.
     */
    private List getListContainingFirstChunk() {
        List listContainingChunk = null;
        if (aFirstChunkIsIn(newSyncItems)) {
            listContainingChunk = newSyncItems;
        } else if (aFirstChunkIsIn(updatedSyncItems)) {
            listContainingChunk = updatedSyncItems;
        }
        return listContainingChunk;
    }
    
}
