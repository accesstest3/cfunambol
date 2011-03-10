/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;


/**
 * A <code>SyncSource</code> that supports the filtering.
 *
 * @version $Id: FilterableSyncSource.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface FilterableSyncSource extends SyncSource {

    // ---------------------------------------------------------- Public Methods

    /**
     * Called to get the state of the item with the given key.
     *
     * @return char the state of the item with the given
     *         key. If no item is found, <code>SyncItemState.NOT_EXISTING</code>
     *         has to be returned.
     *
     * @param syncItemKey the key of the SyncItem to return
     *
     * @throws SyncSourceException in case of errors (for instance if the
     *         underlying data store runs into problems)
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey) throws SyncSourceException;

    /**
     * Called to check if the given item satisfies the filter clause specified
     * in the beginSync
     *
     * @param item the item to check
     * @return true is the item satisfies the filter, false otherwise
     * @throws SyncSourceException
     */
    public boolean isSyncItemInFilterClause(SyncItem item) throws SyncSourceException;


    /**
     * Called to check if the item with the given key satisfies the filter
     * clause specified in the beginSync
     * @param item the item to check
     * @return true is the item satisfies the fiter, false otherwise
     * @throws SyncSourceException if the item with the given key doesn't exist or
     *                             if an error occurs
     */
    public boolean isSyncItemInFilterClause(SyncItemKey key) throws SyncSourceException;

}
