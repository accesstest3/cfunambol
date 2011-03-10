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

package com.funambol.ctp.server.authentication;

import com.funambol.ctp.server.config.CTPServerConfiguration;
import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.Credential;
import com.funambol.framework.security.SecurityConstants;
import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * AuthenticationManager that calls the method 'authorizedCredential' exposed via
 * webservice by the ds-server.
 *
 * @version $Id: WSAuthenticationManagerImpl.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class WSAuthenticationManagerImpl implements AuthenticationManager {

    // --------------------------------------------------------------- Constants

    private static final String CTP_SERVER_RESOURSE =
            SecurityConstants.RESOURCE_SERVICE + "/ctp-server";

    /** Name of the logger used to track web service call to the ds-server. */
    private static final String WS_CALLS_LOGGER_NAME = "funambol.ctp.server.ws";
    
    // ------------------------------------------------------------ Private data

    private ServerInformation serverInfo;

    private AdminWSClient client;
    
    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>WSAuthenticationManagerImpl</code>.
     */
    public WSAuthenticationManagerImpl() {
        
        client = CTPServerConfiguration.getAdminWSClient();
    }

    // -------------------------------------------------------------- Properties

    // ---------------------------------------------------------- Public methods

    public AuthorizationResponse authenticate(String username,
            String deviceId, String credential) throws AuthenticationException {

        AuthorizationResponse result = new AuthorizationResponse();

        Credential cred = new Credential();
        cred.setAuthType(Credential.AUTH_TYPE_MD5_1_1);
        cred.setUsername(username);
        cred.setDeviceId(deviceId);
        cred.setCredData(credential);
        try {
            result = client.authorizeCredential(cred, CTP_SERVER_RESOURSE);
        } catch (AdminException ex) {
            throw new AuthenticationException("Unable to authenticate username=" +
                    username + ", deviceId=" + deviceId, ex);
        }

        return result;
    }

    // --------------------------------------------------------- Private methods

}
