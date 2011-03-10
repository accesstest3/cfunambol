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
package com.funambol.foundation.items.model;

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItemState;

/**
 * This abstract class represents a synchronizable item and its synchronization 
 * details.
 *
 * @version $Id$
 */
public abstract class EntityWrapper {

    /** ID of the corresponding row in the proper DB table */
    protected String id = null;

    /** The user ID */
    protected String userId = null;

    /** The timestamp of the last modification */
    protected Timestamp lastUpdate;

    /** The synchronization status (new, updated, deleted...) */
    protected char status;

    public String getId() {
        return id;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public char getStatus() {
        return status;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isConflict() {
        return SyncItemState.CONFLICT==status;
    }

    public boolean isDeleted() {
        return SyncItemState.DELETED==status;
    }

    public boolean isNew() {
        return SyncItemState.NEW==status;
    }

    public boolean isNotExisting() {
        return SyncItemState.NOT_EXISTING==status;
    }

    public boolean isPartial() {
        return SyncItemState.PARTIAL==status;
    }

    // ------------------------------------------------------------- Constructor

    public EntityWrapper(String id, String userId) {
        this.id = id;
        this.userId = userId;
    }
}
