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
package com.funambol.framework.security;

import java.security.Principal;

import com.funambol.framework.core.Cred;
import com.funambol.framework.server.Sync4jUser;

/**
 * This the interface that defines how credentials are authenticated to a device
 * and how resources are authorized for a given credentials.
 *
 * @version $Id: Officer.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface Officer {

    /**
     * Status used by authorize method
     */
    public enum AuthStatus {
        RESOURCE_NOT_AVAILABLE,
        INVALID_RESOURCE,
        NOT_AUTHORIZED,
        AUTHORIZED,
        PAYMENT_REQUIRED
    }

    /**
     * Authenticates credentials user.
     *
     * @param credentials the credentials to be authenticated
     *
     * @return the Sync4jUser relative to the given credentials, null if no user is found
     *         (credentials not authenticated)
     */
    public Sync4jUser authenticateUser(Cred credentials);

    /**
     * Un-authenticates the given user.
     *
     * @param user the user to be unauthenticated
     */
    public void unAuthenticate(Sync4jUser user);

    /**
     * Authorizes a resource.
     *
     * @param principal the requesting entity
     * @param resource the name (or the identifier) of the resource to be authorized
     * @return an AuthStatus
     */
    public AuthStatus authorize(Principal principal, String resource);

    /**
     * Returns a comma separeted list of the officer supported authentication types
     *
     * @return a comma separeted list of the officer supported authentication types
     */
    public String getClientAuth();

    /**
     * Returns the authentication type that the server must use
     *
     * @return the authentication type that the server must use
     */
    public String getServerAuth();
}

