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

import java.security.Principal;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncOperationImpl;

/**
 * A synchronization conflict reflects a situation where one item has changed
 * in both the sources involved in the synchronization. See SyncProcess for
 * more details.
 *
 *
 * @version $Id: SyncConflict.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncConflict extends SyncOperationImpl {

    // --------------------------------------------------------------- constants

    public final static String STATE_DELETED_DELETED   = "DD";
    public final static String STATE_DELETED_UPDATED   = "DU";
    public final static String STATE_DELETED_NEW       = "DN";
    public final static String STATE_DELETED_NONE      = "DX";
    public final static String STATE_DELETED_CONFLICT  = "DC";
    public final static String STATE_DELETED_SYNCHRONIZED  = "DS";

    public final static String STATE_UPDATED_DELETED   = "UD";
    public final static String STATE_UPDATED_UPDATED   = "UU";
    public final static String STATE_UPDATED_NEW       = "UN";
    public final static String STATE_UPDATED_NONE      = "UX";
    public final static String STATE_UPDATED_CONFLICT  = "UC";
    public final static String STATE_UPDATED_SYNCHRONIZED  = "US";

    public final static String STATE_NEW_DELETED       = "ND";
    public final static String STATE_NEW_UPDATED       = "NU";
    public final static String STATE_NEW_NEW           = "NN";
    public final static String STATE_NEW_NONE          = "NX";
    public final static String STATE_NEW_CONFLICT      = "NC";
    public final static String STATE_NEW_SYNCHRONIZED      = "NS";

    public final static String STATE_NONE_DELETED      = "XD";
    public final static String STATE_NONE_UPDATED      = "XU";
    public final static String STATE_NONE_NEW          = "XN";
    public final static String STATE_NONE_CONFLICT     = "XC";
    public final static String STATE_NONE_SYNCHRONIZED     = "XS";

    public final static String STATE_CONFLICT_DELETED  = "CD";
    public final static String STATE_CONFLICT_UPDATED  = "CU";
    public final static String STATE_CONFLICT_NEW      = "CN";
    public final static String STATE_CONFLICT_NONE     = "CX";
    public final static String STATE_CONFLICT_CONFLICT = "CC";
    public final static String STATE_CONFLICT_SYNCHRONIZED = "CS";

    public final static String STATE_SYNCHRONIZED_DELETED      = "SD";
    public final static String STATE_SYNCHRONIZED_UPDATED      = "SU";
    public final static String STATE_SYNCHRONIZED_NEW          = "SN";
    public final static String STATE_SYNCHRONIZED_NONE         = "SX";
    public final static String STATE_SYNCHRONIZED_CONFLICT     = "SC";
    public final static String STATE_SYNCHRONIZED_SYNCHRONIZED = "SS";

    // -------------------------------------------------------------- Properties

    /**
     * The type of this operation
     */
    private String type;
    public String getType() {
        return this.type;
    }

    // ------------------------------------------------------------ Constructors

    public SyncConflict() {
    }

    public SyncConflict(Principal owner, SyncItem syncItemA, SyncItem syncItemB, String type) {
        super(syncItemA, syncItemB, CONFLICT);
        this.setOwner(owner);
        this.type = type;
    }
}
