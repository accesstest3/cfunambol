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
package com.funambol.server.admin.ws.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.utils.Messages;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;

import com.funambol.server.admin.UserManager;

import com.funambol.server.config.Configuration;
import com.funambol.server.config.ConfigurationConstants;

/**
 * This is an Axis authentication handler used to authenticate the user trying
 * to perform administration tasks on the Admin web service. Authentication is
 * currently performed with the Basic scheme.
 *
 *
 * @version $Id: AdminAuthHandler.java,v 1.1.1.1 2008-02-21 23:36:07 stefano_fornari Exp $
 */
public class AdminAuthHandler
extends BasicHandler
implements ConfigurationConstants {

    // --------------------------------------------------------------- Constants
    public static final String LOG_NAME   = "admin";
    public static final String ROLE_ADMIN = "sync_administrator";

    public static final String FAULT_UNAUTHORIZED = "Server.Unauthenticated";

    private static final String AUTH_ERROR_MESSAGE =
        "Authentication failed, because the server is unable to access to the datastore. Please see server log for the causes.";

    // ------------------------------------------------------------ Private data
    private FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);
    private UserManager userManager;

    // ------------------------------------------------------------ Constructors
    public AdminAuthHandler() {
        try {
            Configuration c = Configuration.getConfiguration();
            userManager = (UserManager)c.getUserManager();
        } catch (Exception e) {
            log.error("Error retrieving the user manager", e);
        }
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Redefines the BasicHandler's invoke method.
     *
     * @param msgContext the Axis message context
     *
     * @throws AxisFault if authentication fails.
     */
    public void invoke(MessageContext msgContext) throws AxisFault {
        if (log.isTraceEnabled()) {
            log.trace("Authenticating admin action.");
        }

        String username = msgContext.getUsername();
        if (username == null) {
            //
            // Hey! no auth here!
            // This will make axis return a 401
            //
            if (log.isTraceEnabled()) {
                log.trace("Credentials not provided.");
            }
            AxisFault f = new AxisFault();
            f.setFaultCodeAsString("Server.Unauthenticated");
            throw f;
        }

        String password = msgContext.getPassword();
        if (password == null) {
            password = "";
        }

        Sync4jUser user = null;
        try {
            //
            // First of all: is the user authenticated?
            //
            user = new Sync4jUser();
            user.setUsername(username);

            userManager.getUser(user);

            if (!password.equals(user.getPassword())) {
                //
                // Authentication failed!
                //
                notAuthorized();
            }

            //
            // Is the user a sync admin user?
            //
            userManager.getUserRoles(user);

            if (!user.hasRole(ROLE_ADMIN)) {
                //
                // The user is not an administrator
                //
                if (log.isInfoEnabled()) {
                    log.info( "Authorization denied: user "
                            + user
                            + " is not an administrator."
                            );
                }
                notAuthorized();
            }
        } catch (NotFoundException e) {

                if (log.isInfoEnabled()) {
                    log.info( "Authorization denied: user "
                            + user
                            + " not found."
                            );
                }
            notAuthorized();

        } catch (AxisFault f) {
            //
            // this happens, for example, when the user is no more authorized
            //
            throw f;
        } catch (Exception e) {
            log.error("Error retrieving the user", e);

            AxisFault f = new AxisFault();
            f.setFaultString("The server is unable to authenticate the user. Check the server log for details.");
            throw f;
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Throws the AxisFault representing "not authorized".
     *
     * @throws AxisFault
     */
    private void notAuthorized()
    throws AxisFault {
        AxisFault f = new AxisFault();
        
        f.setFaultCodeAsString(FAULT_UNAUTHORIZED);
        throw f;
    }
}
