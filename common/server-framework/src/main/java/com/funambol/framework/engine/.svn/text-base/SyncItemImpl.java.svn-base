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

package com.funambol.framework.engine;

import java.sql.Timestamp;

import com.funambol.framework.engine.source.SyncSource;

/**
 * <i>SyncItem</i> is the indivisible entity that can be exchanged in a
 * synchronization process and contain data, status and addressing information.
 * <p>
 * The <i>SyncItemKey</i> uniquely identifies the item into the server. Client
 * keys must be translated into server keys before create a <i>SyncItem</i>.
 *
 * @deprecated Since v85 InMemorySyncItem should be used
 *
 * @version $Id: SyncItemImpl.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncItemImpl extends InMemorySyncItem implements java.io.Serializable {

    // ------------------------------------------------------------ Constructors

    protected SyncItemImpl() {
        super();
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the given
     * key.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     */
    public SyncItemImpl(SyncSource syncSource, Object key) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             SyncItemState.UNKNOWN,
             null,  // content
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the given
     * key and the given state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public SyncItemImpl(SyncSource syncSource, Object key, char state) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             state,
             null,  // content
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source and with the given
     * key, parentKey and state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the item's parentKey
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public SyncItemImpl(SyncSource syncSource, Object key, Object parentKey, char state) {
        this(syncSource,
             key,
             parentKey,
             null,  // mappedKey
             state,
             null,  // content
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the
     * given key, parentKey, mappedKey and state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the parent key of the item
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public SyncItemImpl(SyncSource syncSource,
                        Object     key,
                        Object     parentKey,
                        Object     mappedKey,
                        char       state) {

        this(syncSource,
             key,
             parentKey,
             mappedKey,
             state,
             null, // content
             null, // format
             null, // type
             null  // timestamp
        );

    }

    /**
     * Creates a new <i>SyncItem</i>.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     * @param content the content of the item
     * @param format the format of the item
     * @param type the type of the item
     * @param timestamp the timestamp of the item
     */
    public SyncItemImpl(SyncSource syncSource,
                        Object     key,
                        Object     mappedKey,
                        char       state,
                        byte[]     content,
                        String     format,
                        String     type,
                        Timestamp  timestamp) {

        this(syncSource,
             key,
             null, // parentKey
             mappedKey,
             state,
             content,
             format,
             type,
             timestamp
        );
    }

    /**
     * Creates a new <i>SyncItem</i>.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the item's parentKey
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     * @param content the content of the item
     * @param format the format of the item
     * @param type the type of the item
     * @param timestamp the timestamp of the item
     */
    public SyncItemImpl(SyncSource syncSource,
                        Object     key,
                        Object     parentKey,
                        Object     mappedKey,
                        char       state,
                        byte[]     content,
                        String     format,
                        String     type,
                        Timestamp  timestamp) {

        super(syncSource,
              key,
              parentKey,
              mappedKey,
              state,
              content,
              format,
              type,
              timestamp
         );
    }

    // ---------------------------------------------------------- Static Methods

    /**
     * Creates and returns a "not-existing" <i>SyncItem</i>. It is used internally to
     * represent an item which has not a physical correspondance in a source.
     *
     * @param syncSource the <i>SyncSource</i> the not existing item belongs to
     * @return the a "not-exisiting" <i>SyncItem</i>
     */
    public static SyncItemImpl getNotExistingSyncItem(SyncSource syncSource) {
        SyncItemImpl notExisting = new SyncItemImpl(syncSource, "NOT_EXISTING");

        notExisting.setState(SyncItemState.NOT_EXISTING);

        return notExisting;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Creates a copy of the InMemorySyncItem object
     * The new InMemorySyncItem and the original one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @return
     */
    @Override
    public Object cloneItem() {

        SyncItemImpl syncItem = new SyncItemImpl();
        syncItem.copy(this);

        return syncItem;
    }


}
