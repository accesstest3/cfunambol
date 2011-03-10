/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.ctp.client.driver.session;

import com.funambol.ctp.core.NotAuthenticated;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Sync;
import com.funambol.ctp.core.Unauthorized;

/**
 * Defines behaviours associated with a particular state
 *
 * @version $Id: State.java,v 1.3 2007-11-28 11:26:17 nichele Exp $
 */
public interface State {

    /**
     * Returns the specific state name
     * @return the specific state name
     */
    public String getName();

    /**
     * Verifies if two states are equals
     * @param obj the state to compare
     * @return true if are the same state
     * false otherwise
     */
    public boolean deepequals(Object obj);

    /**
     * Defines behavior for "Ok" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     * @param ok The Ok message
     * @return The new State
     */
    public State onOk(SessionManager sessionManager,
            Ok ok);

    /**
     * Defines behavior for "Error" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     * @param error The Error message
     * @return The new State
     */
    public State onError(SessionManager sessionManager,
            com.funambol.ctp.core.Error error);

    /**
     * Defines behavior for "Unauthorized" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     * @param unauthorized the Unauthorized message
     * @return The new State
     */
    public State onUnauthorized(SessionManager sessionManager,
            Unauthorized unauthorized);

    /**
     * Defines behavior for "NotAuthenticated" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     * @param notAuthenticated The NOtAuthenticated message
     * @return The new State
     */
    public State onNotAuthenticated(SessionManager sessionManager,
            NotAuthenticated notAuthenticated);

    /**
     * Defines behavior for "Sync" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     * @param sync The Sync message
     * @return The new State
     */
    public State onSync(SessionManager sessionManager,
            Sync sync);

    /**
     * Defines behavior for "connection close" event
     * @param sessionManager SessionManager to call in order to acctually
     * performs actions
     */
    public void onClose(SessionManager sessionManager);

    /**
     *
     */
    public State onIdle(SessionManager sessionManager, long previous, long now);
}
