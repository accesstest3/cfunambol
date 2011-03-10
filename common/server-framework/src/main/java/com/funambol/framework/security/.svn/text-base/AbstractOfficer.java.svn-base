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
package com.funambol.framework.security;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.security.Principal;

import com.funambol.framework.core.Cred;
import com.funambol.framework.server.Sync4jUser;

/**
 * Implements the basic functionalities of a <i>Officer</i>.
 *
 * @version $Id: AbstractOfficer.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public abstract class AbstractOfficer implements Officer, java.io.Serializable {

    // --------------------------------------------------------------- Constants
    protected static final String AUTH_B64_MD5 =
        Cred.AUTH_TYPE_BASIC + "," + Cred.AUTH_TYPE_MD5;

    protected FunambolLogger log =
        FunambolLoggerFactory.getLogger("funambol.auth");

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of AbstractOfficer */
    public AbstractOfficer() {
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Which authetication types does the server support?
     * By default, the server supports both b64 and MD5.
     */
    private String clientAuth = AUTH_B64_MD5;

    /**
     * Returns the authentication type that the client must use
     *
     * @return the authentication type that the client must use
     */
    public String getClientAuth() {
        return this.clientAuth;
    }

    /**
     * Sets the authentication type that the client must use
     *
     * @param clientAuth the authentication type that the client must use
     */
    public void setClientAuth(String clientAuth) {
        this.clientAuth = clientAuth;
    }

    /**
     * Which type of authetication use the server to send his credential?
     */
    private String serverAuth = Cred.AUTH_NONE;

    /**
     * Returns the authentication type that the server must use
     *
     * @return the authentication type that the server must use
     */
    public String getServerAuth() {
        return this.serverAuth;
    }

    /**
     * Sets the authentication type that the server must use
     *
     * @param serverAuth the authentication type that the server must use
     */
    public void setServerAuth(String serverAuth) {
        this.serverAuth = serverAuth;
    }

    /**
     * Un-authenticates the given user.
     *
     * @param user the user to be unauthenticated
     *
     */
    public void unAuthenticate(Sync4jUser user) {
        //
        // Do nothing. In the current implementation, the authentication is
        // discarde as soon as the LoginContext is garbage collected.
        //
    }

    /**
     * Authorizes a resource.
     *
     * @param principal the requesting entity
     * @param resource the name (or the identifier) of the resource to be
     *                 authorized
     * @return an AuthStatus the authentication status
     */
    public Officer.AuthStatus authorize(Principal principal, String resource) {
        return AuthStatus.AUTHORIZED;
    }

    // -------------------------------------------------------- Abstract methods
    /**
     * Authenticates credentials user.
     *
     * @param credentials the credentials to be authenticated
     *
     * @return the Sync4jUser relative to the given credentials, null if no
     *         user is found (credentials not authenticated)
     */
    public abstract Sync4jUser authenticateUser(Cred credentials);

}
