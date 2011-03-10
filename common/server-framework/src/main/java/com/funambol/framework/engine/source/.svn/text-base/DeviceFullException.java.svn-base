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
package com.funambol.framework.engine.source;

import com.funambol.framework.core.StatusCode;

/**
 * This exception represents the case where the server is full, i.e. the size
 * quota for the users is exceeded.
 *
 * @version $Id$
 */
public class DeviceFullException extends SyncSourceException {

    /**
     * The total size of the files already stored
     */
    private long usedQuota;

    /**
     * The assigned max quota
     */
    private long maxQuota;

    /**
     * The size of the item that caused the exception
     */
    private long itemSize;

    // ------------------------------------------------------------- Constructor

    public DeviceFullException(String msg) {
        this(msg, -1, -1, -1, true);
    }

    public DeviceFullException(String message, long usedQuota, long maxQuota, long itemSize) {
        this(message, usedQuota, maxQuota, itemSize, true);
    }

    public DeviceFullException(String message, long usedQuota, long maxQuota, long itemSize, boolean silent) {
        super(message, StatusCode.DEVICE_FULL, silent);
        this.usedQuota = usedQuota;
        this.maxQuota = maxQuota;
        this.itemSize = itemSize;
    }

    // --------------------------------------------------------------- Accessors

    /**
     * Returns The quota already used
     * @return The quota already used
     */
    public long getUsedQuota() {
        return usedQuota;
    }

    /**
     * Returns the max quota
     * @return  the max quota
     */
    public long getMaxQuota() {
        return maxQuota;
    }

    /**
     * Returns the size of the item causing the eception
     * @return the size of the item causing the eception, or -1 if the size is
     * unknown
     */
    public long getItemSize() {
        return itemSize;
    }
}