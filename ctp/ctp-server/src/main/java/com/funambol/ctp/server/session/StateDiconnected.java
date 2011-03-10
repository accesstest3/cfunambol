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

package com.funambol.ctp.server.session;

import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.Bye;
import com.funambol.ctp.core.Ready;

import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;

/**
 * Represent the "Disconnected" state and the behaviours the SessionManager
 * delegates to it.
 * Responsibilities:
 * Collaborators: none
 *
 * @version $Id: StateDiconnected.java,v 1.5 2007-11-28 11:26:17 nichele Exp $
 */
public class StateDiconnected extends StateAdapter {

    // --------------------------------------------------------------- Constants
    private static final int EXPIRE_TIME = 10; // in seconds

    // ------------------------------------------------------------ Constructors

    public StateDiconnected() {
        super("Disconnected", EXPIRE_TIME);
    }

    // ---------------------------------------------------------- Public Methods

    @Override
    public State onAuthenticationReq(SessionManager sessionManager, Auth auth) {
        sessionManager.getLogger().error("Authentication request event while disconneceted");
        return this;
    }

    @Override
    public State onReady(SessionManager sessionManager, Ready ready) {
        sessionManager.getLogger().error("READY event while disconnected");
        return this;
    }

    @Override
    public State onNotification(SessionManager sessionManager, CTPNotification message) {
        sessionManager.getLogger().error("Notification event while disconnected");
        return this;
    }

    @Override
    public State onIdle(SessionManager sessionManager, long previous, long now) {
        sessionManager.getLogger().error("Idle event while disconnected");
        return this;
    }

    @Override
    public State onBye(SessionManager sessionManager, Bye bye) {
        sessionManager.getLogger().error("BYE event while disconnected");
        return this;
    }

    @Override
    public void onClose(SessionManager sessionManager) {
        sessionManager.getLogger().error("CLOSE event while disconnected");
    }
}
