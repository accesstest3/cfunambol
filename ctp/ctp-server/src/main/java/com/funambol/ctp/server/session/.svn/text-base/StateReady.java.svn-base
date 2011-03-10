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

import com.funambol.ctp.core.Bye;
import com.funambol.ctp.core.Ready;
import com.funambol.ctp.server.config.CTPServerConfiguration;

import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;

/**
 *
 * @version $Id: StateReady.java,v 1.6 2007-11-28 11:26:17 nichele Exp $
 */
public class StateReady extends StateAdapter {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Constructors

    public StateReady() {
        super("Ready",
              CTPServerConfiguration.getCTPServerConfigBean().getClientHeartBeatExpectedTime());
    }

    // ---------------------------------------------------------- Public Methods

    @Override
    public State onReady(SessionManager sessionManager, Ready ready) {
        sessionManager.sendOKMessage();
        return this;
    }

    @Override
    public State onNotification(SessionManager sessionManager, CTPNotification message) {
        sessionManager.sendSyncMessage(message.getNotificationMessage());
        return this;
    }

    @Override
    public State onIdle(SessionManager sessionManager, long previous, long now) {

        State nextState = super.onIdle(sessionManager, previous, now);
        if (!nextState.deepequals(this)) {
            sessionManager.forwardUnsubscription();
        }
        return nextState;
    }

    @Override
    public State onBye(SessionManager sessionManager, Bye bye) {
        sessionManager.forwardUnsubscription();
        return super.onBye(sessionManager, bye);
    }

    @Override
    public void onClose(SessionManager sessionManager) {
        sessionManager.forwardUnsubscription();
    }
}
