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

import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.AuthorizationStatus;
import java.util.Properties;

/**
 * Mock object used to test classes which need to collaborate with a
 * AuthenticationManager
 * @version $Id: MockAuthenticationManager.java,v 1.3 2007-11-28 11:26:16 nichele Exp $
 */
public class MockAuthenticationManager implements AuthenticationManager {

    private Properties  prop = new Properties();

    // ------------------------------------------------------------ Constructors

    public MockAuthenticationManager() {
    }

    /**
     * Set the property to test
     */
    public void setProperty (String key, String value) {
        prop.setProperty(key, value);
    }

    // ---------------------------------------------------------- Public Methods

    public AuthorizationResponse authenticate(String username,
            String deviceId, String credential) throws AuthenticationException {

        AuthorizationResponse result = authenticate(username);
        return result;
    }

    // --------------------------------------------------------- Private Methods

    private AuthorizationResponse authenticate(final String localUserName) {

        String authentication = prop.getProperty(localUserName);

        int authenticated = 0;
        String nonce = "1234567890";
        if ("AUTHORIZED".equalsIgnoreCase(authentication)) {
            authenticated = AuthorizationStatus.AUTHORIZED;
            nonce = "1234567890";
        } else if ("NOT_AUTHORIZED".equalsIgnoreCase(authentication)) {
            authenticated = AuthorizationStatus.NOT_AUTHORIZED;
            nonce = "1234567890";
        } else if ("NOT_AUTHENTICATED".equalsIgnoreCase(authentication)) {
            authenticated = AuthorizationStatus.NOT_AUTHENTICATED;
            nonce = "1234567890";
        }

        AuthorizationResponse result = new AuthorizationResponse();
        result.setAuthStatus(authenticated);
        result.setNextNonce(nonce.getBytes());
        return result;
    }
}
