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
import com.funambol.ctp.server.authentication.AuthenticationException;
import com.funambol.ctp.server.config.CTPServerConfiguration;

import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.AuthorizationStatus;

/**
 * Represent the "Connected" state and the behaviours the SessionManager
 * delegates to it.
 * Responsibilities:
 * Collaborators:
 *
 * @version $Id: StateConnected.java,v 1.5 2007-11-28 11:26:17 nichele Exp $
 */
public class StateConnected extends StateAdapter {

    // --------------------------------------------------------------- Constants
    private static final int EXPIRE_TIME = 30; // in seconds

    // ------------------------------------------------------------ Constructors

    public StateConnected() {
        super("Connected", EXPIRE_TIME);
    }

    // ---------------------------------------------------------- Public Methods

    @Override
    public State onAuthenticationReq(SessionManager sessionManager, Auth auth) {

        if (sessionManager.getAuthenticationRetries() >=
            CTPServerConfiguration.getCTPServerConfigBean().getMaxAuthenticationRetries()) {

            sessionManager.sendErrorMessage("Exceeded authentication attempts");
            return new StateLeaving();
        }
        sessionManager.incrementAuthenticationRetries();

        try {
            AuthorizationResponse authorizationResponse = sessionManager.forwardAuthenticationRequest();

            if (AuthorizationStatus.AUTHORIZED == authorizationResponse.getAuthStatus()) {
                sessionManager.sendOKMessage(authorizationResponse);
                if (sessionManager.getLogger().isTraceEnabled()) {
                    sessionManager.getLogger().trace("Authenticated");
                }
                return new StateAuthenticated();
            } else if (AuthorizationStatus.NOT_AUTHORIZED == authorizationResponse.getAuthStatus() ||
                       AuthorizationStatus.INVALID_RESOURCE == authorizationResponse.getAuthStatus() ||
                       AuthorizationStatus.PAYMENT_REQUIRED == authorizationResponse.getAuthStatus() ||
                       AuthorizationStatus.RESOURCE_NOT_AVAILABLE == authorizationResponse.getAuthStatus() ) {
                sessionManager.sendUnauthorizedMessage(authorizationResponse);
                if (sessionManager.getLogger().isTraceEnabled()) {
                    sessionManager.getLogger().trace("Not Authorized");
                }
                return new StateLeaving();
            } else if (AuthorizationStatus.NOT_AUTHENTICATED == authorizationResponse.getAuthStatus()) {
                sessionManager.sendNotAuthenticatedMessage(authorizationResponse);
                if (sessionManager.getLogger().isTraceEnabled()) {
                    sessionManager.getLogger().trace("Not Authenticated");
                }
                return new StateConnected();
            } else {
                sessionManager.sendErrorMessage("Unable to authenticate: internal error");
                sessionManager.getLogger().error("Unsupported authentication result code: " + authorizationResponse.getAuthStatus() );
                return new StateLeaving();
            }
        } catch (AuthenticationException ex) {
            sessionManager.sendErrorMessage("Unable to authenticate: " + ex.getMessage());
            sessionManager.getLogger().error("Unable to authenticate: ", ex);
            return new StateLeaving();
        }
    }
}
