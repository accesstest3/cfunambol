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

package com.funambol.framework.engine;

import java.sql.Timestamp;

import com.funambol.framework.engine.source.SyncSource;


/**
 * Responsibility: manage the large objects chunk by chunk.
 *
 * No content is kept by this implementation. getContect is not overriden
 * and return null as per AbstractSyncItem implementation.
 *
 * It adds write method that must be used to write content in this item.
 * Concrete implementations should handle the written data as stream.
 *
 * @version $Id$
 */
public abstract class StreamingSyncItem extends AbstractSyncItem {

    // --------------------------------------------------------------- Constants

    /**
     * property used to hold the error message if an error occurs
     */
    public static final String PROPERTY_ERROR_MSG = "ERROR_MSG";

    // -------------------------------------------------------- Member Variables

    /**
     * specify if all the chunks were received and thus the item is complete.
     */
    private boolean complete = false;

    // ------------------------------------------------------------- Constructor

    /**
     * Constructor
     * @param syncSource The syncsource to which the SyncItems belongs
     * @param key the item identifier
     */
    public StreamingSyncItem(SyncSource syncSource, Object key) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             SyncItemState.UNKNOWN,
             null,  // format
             null,  // type
             null   // timestamp
             );

    }

    /**
     * Constructor
     * @param syncSource
     * @param key
     * @param parentKey
     * @param mappedKey
     * @param state
     * @param format
     * @param type
     * @param timestamp
     */
    public StreamingSyncItem(SyncSource syncSource,
                             Object key,
                             Object parentKey,
                             Object mappedKey,
                             char state,
                             String format,
                             String type,
                             Timestamp timestamp) {

        super(syncSource,
              key,
              parentKey,
              mappedKey,
              state,
              format,
              type,
              timestamp);
    }

    // ------------------------------------------------- Public Abstract Methods

    /**
     * Writes buffer to this output stream
     * @param buffer the data to write
     * @throws SyncItemException if an error occurs.
     */
    abstract public void write(byte[] buffer) throws SyncItemException;

    /**
     * Reset the large object and release resources.
     * @throws SyncItemException if an error occurs
     */
    abstract public void release() throws SyncItemException;

    // ---------------------------------------------------------- Public Methods

    /**
     * set as complete the stream
     */
    public void setAsComplete() {
        complete = true;
    }

    /**
     * returns if all the chunks were received and thus the items is complete
     * @return returns true if all the chunks were received and thus the items
     * is complete
     */
    public boolean isComplete() {
        return complete;
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

        super.copy(syncItem);

        if (syncItem instanceof StreamingSyncItem) {
            complete = ((StreamingSyncItem)syncItem).isComplete();
        } 
    }

}
