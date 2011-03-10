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

package com.funambol.framework.server;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a synchronization timestamp for a particular database and principal.
 * <i>tag</i> stores the next (when used as a next timestamp) or the last (when
 * used as a last timstamp) value as it appears in <i>SyncAnchor</i>.
 *
 * @version $Id: SyncTimestamp.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 */
public class SyncTimestamp implements Serializable {

    // ------------------------------------------------------------- Public data

    public long   start     = -1  ;  // timestamp of when sync started
    public long   end       = -1  ;  // timestamp of when sync ended
    public String tagServer = null;  // the last/next tag of the server
    public String tagClient = null;  // the last/next tag of the client
    public int    syncType  = -1  ;  // the alert code of the sync
    public int    status    = -1  ;  // the status code of the sync

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of LastTimestamp */
    public SyncTimestamp() { }

    /**
     * Creates a new instance of SyncTimestamp when the only starting timestamp
     * is known. <i>end</i> is set to -1 and tag is set to the string value
     * of the start.
     *
     * @param start the starting timestamp
     */
    public SyncTimestamp(final long start) {
        this(String.valueOf(start), String.valueOf(start), start, -1, -1, -1);
    }

    /**
     * Creates a new instance of LastTimestamp.
     * It is need to cache the next anchor of the server and the next anchor
     * of the client.
     *
     * @param tagServer the last/next tag of the server
     * @param tagClient the last/next tag of the client
     * @param start the timestamp of when sync started
     * @param end the timestamp of when sync ended
     * @param syncType the alert code of the sync
     *
     */
    public SyncTimestamp(final String tagServer,
                         final String tagClient,
                         final long   start    ,
                         final long   end      ,
                         final int    syncType ,
                         final int    status) {
        this.tagServer = tagServer;
        this.tagClient = tagClient;
        this.start     = start    ;
        this.end       = end      ;
        this.syncType  = syncType ;
        this.status    = status   ;
    }

    // ---------------------------------------------------------- Public methods

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

        sb.append("tagServer", tagServer).
           append("tagClient", tagClient).
           append("start"    , new java.sql.Timestamp(start)).
           append("end"      , new java.sql.Timestamp(end)).
           append("syncType" , syncType).
           append("status"   , status);

        return sb.toString();
    }
}
