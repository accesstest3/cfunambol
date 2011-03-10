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

/**
 * This interface represents a synchronization operation between two items.
 * The possible operations are:
 * <ul>
 * <li>create
 * <li>update
 * <li>delete
 * </ul>
 *
 *
 *
 * @version $Id: SyncOperation.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface SyncOperation {

    public static final char NEW          = 'N';
    public static final char DELETE       = 'D';
    public static final char UPDATE       = 'U';
    public static final char CONFLICT     = 'O';
    public static final char ACCEPT_CHUNK = 'A';
    public static final char NOP          = '-';

    public SyncItem getSyncItemA();
    public void setSyncItemA(SyncItem syncItemA);

    public SyncItem getSyncItemB();
    public void setSyncItemB(SyncItem syncItemB);

    /**
     * Does the operation apply to the client source
     *
     * @return true if the operation applies to the client source or false otherwise.
     */
    public boolean isAOperation();
    public void setAOperation(boolean a);

    /**
     * Does the operation apply to the server source*
     *
     * @return true if the operation applies to the server source or false otherwise.
     */
    public boolean isBOperation();
    public void setBOperation(boolean b);

    public char getOperation();

    /**
     * The principal that requested this operation.
     *
     * @return the principal that requested this operation. It may be null.
     */
    public Principal getOwner();

    /**
     * Is the delete on the client item required ?
     * @return boolean
     */
    public boolean isDeleteForced();

}
