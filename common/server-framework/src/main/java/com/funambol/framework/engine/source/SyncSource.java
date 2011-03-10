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

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;


/**
 * A <code>SyncSource</code> is responsible for the storing and retrieving of
 * <code>SyncItem</code> objects. It is used also for getting newly created or removed
 * <code>SyncItem</code>.<br>
 * Note that the <code>SyncSource</code> interface doesn't make any assumption about the
 * underlying data source. Each concrete implementation will use the storage its
 * specific database.
 *
 * @version $Id: SyncSource.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface SyncSource {

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the name of the source
     *
     * @return the name of the source
     */
    public String getName();

    /**
     * Returns the source URI
     *
     * @return the absolute URI of the source
     */
    public String getSourceURI();

    /**
     * Returns the <code>SyncSourceInfo</code> of this source
     *
     * @see SyncSourceInfo
     *
     * @return the <code>SyncSourceInfo</code> of this source
     *
     */
    public SyncSourceInfo getInfo();

    /**
     * Called before any other synchronization method. To interrupt the sync
     * process, throw a SyncSourceException.
     *
     * @param syncContext the context of the sync.
     *
     * @see SyncContext
     *
     * @throws SyncSourceException to interrupt the process with an error
     */
    public void beginSync(SyncContext syncContext) throws SyncSourceException;

    /**
     * Called after the modifications have been applied.
     *
     * @throws SyncSourceException to interrupt the process with an error
     */
    public void endSync() throws SyncSourceException;

    /**
     * Commits the changes applied during the sync session. If the underlying
     * datastore can not commit the changes, a SyncSourceException is thrown.
     *
     * @throws SyncSourceException if the changes cannot be committed
     */
    public void commitSync() throws SyncSourceException;

    /**
     * Called to get the keys of the items updated in the time frame sinceTs - untilTs.
     * <br><code>sinceTs</code> null means all keys of the items updated until <code>untilTs</code>.
     * <br><code>untilTs</code> null means all keys of the items updated since <code>sinceTs</code>.
     *
     * @param sinceTs consider the changes since this point in time.
     * @param untilTs consider the changes until this point in time.
     *
     * @return an array of keys containing the <code>SyncItemKey</code>'s key of the updated
     *         items in the given time frame. It MUST NOT return null for
     *         no keys, but instad an empty array.
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs ,
                                                Timestamp untilTs )
    throws SyncSourceException ;

    /**
     * Called to get the keys of the items deleted in the time frame sinceTs - untilTs.
     * <br><code>sinceTs</code> null means all keys of the items deleted until <code>untilTs</code>.
     * <br><code>untilTs</code> null means all keys of the items deleted since <code>sinceTs</code>.
     *
     * @param sinceTs consider the changes since this point in time.
     * @param untilTs consider the changes until this point in time.
     *
     * @return an array of keys containing the <code>SyncItemKey</code>'s key of the deleted
     *         items in the given time frame. It MUST NOT return null for
     *         no keys, but instad an empty array.
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs ,
                                                Timestamp untilTs )
    throws SyncSourceException ;

    /**
     * Called to get the keys of the items created in the time frame sinceTs - untilTs.
     * <br><code>sinceTs</code> null means all keys of the items created until <code>untilTs</code>.
     * <br><code>untilTs</code> null means all keys of the items created since <code>sinceTs</code>.
     *
     * @param sinceTs consider the changes since this point in time.
     * @param untilTs consider the changes until this point in time.
     *
     * @return an array of keys containing the <code>SyncItemKey</code>'s key of the created
     *         items in the given time frame. It MUST NOT return null for
     *         no keys, but instad an empty array.
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs ,
                                            Timestamp untilTs )
    throws SyncSourceException ;


    /**
     * Adds a new <code>SyncItem</code>.
     * The item is also returned giving the opportunity to the
     * source to modify its content and return the updated item (i.e. updating
     * the id to the GUID).
     *
     * @param syncInstance  the item to add
     *
     * @return the inserted item
     *
     * @throws SyncSourceException in case of error (for instance if the
     *         underlying data store runs into problems)
     */
    public SyncItem addSyncItem(SyncItem syncInstance)
    throws SyncSourceException ;

    /**
     * Update a <code>SyncItem</code>.
     * The item is also returned giving the opportunity to the
     * source to modify its content and return the updated item (i.e. updating
     * the id to the GUID).
     *
     * @param syncInstance the item to replace
     *
     * @return the updated item
     *
     * @throws SyncSourceException in case of error (for instance if the
     *         underlying data store runs into problems)
     */
    public SyncItem updateSyncItem(SyncItem syncInstance)
       throws SyncSourceException;


    /**
     * Removes a SyncItem given its key.
     *
     * @param itemKey the key of the item to remove
     * @param time the time of the deletion
     * @param softDelete is a soft delete ?
     *
     * @throws SyncSourceException in case of error (for instance if the
     *         underlying data store runs into problems)
     */
    public void removeSyncItem(SyncItemKey itemKey, Timestamp time, boolean softDelete)
    throws SyncSourceException;


    /**
     * Called to get the keys of all items accordingly with the parameters
     * used in the beginSync call.
     * @return an array of all <code>SyncItemKey</code>s stored in this source.
     *         If there are no items an empty array is returned.
     *
     * @throws SyncSourceException in case of error (for instance if the
     *         underlying data store runs into problems)
     */
    public SyncItemKey[] getAllSyncItemKeys()
    throws SyncSourceException;

    /**
     * Called to get the item with the given key.
     *
     * @return return the <code>SyncItem</code> corresponding to the given
     *         key. If no item is found, null is returned.
     *
     * @param syncItemKey the key of the SyncItem to return
     *
     * @throws SyncSourceException in case of errors (for instance if the
     *         underlying data store runs into problems)
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException;

    /**
     * Called to retrive the keys of the twins of the given item
     *
     * @param syncItem the twin item
     *
     * @return the keys of the twin. Each source implementation is free to
     *         interpret this as it likes (i.e.: comparing all fields).
     *

     *
     * @throws SyncSourceException in case of errors (for instance if the
     *         underlying data store runs into problems)
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException;


    /**
     * Called by the engine to notify an operation status.
     * @param operationName the name of the operation.
     *        One between:
     *        - Add
     *        - Replace
     *        - Delete
     * @param status the status of the operation
     * @param keys the keys of the items
     */
    public void setOperationStatus(String operationName, int status, SyncItemKey[] keys);

}
