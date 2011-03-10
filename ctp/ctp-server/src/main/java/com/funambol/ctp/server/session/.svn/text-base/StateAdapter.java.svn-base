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
 * Defines default behaviours for events. Concrete States have to override the
 * methods they wants to manage.
 * @version $Id: StateAdapter.java,v 1.7 2007-11-28 11:26:17 nichele Exp $
 */
public abstract class StateAdapter implements State {

    // --------------------------------------------------------------- Constants
    private static final int DEFAULT_EXPIRE_TIME = 30; // 30 seconds

    // ------------------------------------------------------------ Private data
    /**
     * The name of the concrete state
     */
    private String name = null;

    /**
     * The maximum idle time in milliseconds after which the connection is closed.
     */
    private long expireTime = -1;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     *
     * @param name the name of the state
     * @param expireTime the expire time of the state
     */
    public StateAdapter(String name, long expireTime) {
        this.name = name;
        this.expireTime = expireTime * 1000;
    }

    /**
     * Creates a StateAdapter with the given state and with:
     * <br><code>CTPServerConfiguration.getCTPServerConfigBean().getClientHeartBeatExpectedTime()</code>
     * as expireTime.
     *
     * @param name the name of the state
     */
    public StateAdapter(String name) {
        this(name, DEFAULT_EXPIRE_TIME);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the expire time
     * @return the expire time
     */
    public long getExpireTime() {
        return expireTime;
    }

    /**
     * Returns the concrete state name
     * @return The concrete state name
     */
    public String getName() {
        return this.name;
    }

    /**
     *
     * @param obj
     * @return
     */
    public boolean deepequals(Object obj) {
        if (obj == null || (!obj.getClass().equals(this.getClass())) ) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param sessionManager
     * @param auth
     * @return
     */
    public State onAuthenticationReq(SessionManager sessionManager, Auth auth) {
        sessionManager.sendErrorMessage("Unable to manage authentication " +
                "message while " + getName());
        return new StateLeaving();
    }

    /**
     *
     * @param sessionManager
     * @param ready
     * @return
     */
    public State onReady(SessionManager sessionManager, Ready ready) {
        sessionManager.sendErrorMessage("Unable to manage message READY while " +
                getName());
        return new StateLeaving();
    }

    /**
     *
     * @param sessionManager
     * @param message
     * @return
     */
    public State onNotification(SessionManager sessionManager, CTPNotification message){
        sessionManager.getLogger().warn("Unable to manage notification message " +
                "while " + getName());
        return this;
    }

    /**
     *
     * @param sessionManager
     * @param previous
     * @param now
     * @return
     */
    public State onIdle(SessionManager sessionManager, long previous, long now) {

        if (expireTime > 0 &&
            (now - previous) > expireTime) {
            sessionManager.getLogger().warn("\"" + getName() + "\" max idle time" +
                    " expired (" + expireTime + ")");
            sessionManager.closeSession();
            return new StateLeaving();
        } else {
            return this;
        }
    }

    /**
     *
     * @param sessionManager
     * @param bye
     * @return
     */
    public State onBye(SessionManager sessionManager, Bye bye) {
        sessionManager.sendOKMessage();
        return new StateLeaving();
    }

    /**
     *
     * @param sessionManager
     */
    public void onClose(SessionManager sessionManager) {
    }
}
