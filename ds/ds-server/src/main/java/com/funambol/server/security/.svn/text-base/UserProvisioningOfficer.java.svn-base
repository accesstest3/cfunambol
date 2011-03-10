/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.server.security;

import com.funambol.framework.core.Authentication;
import com.funambol.framework.core.Cred;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;

/**
 * This is an implementation of the <i>Officier</i> interface. It provides
 * the user provisioning so if an user is not in the database he will be added.
 * It requires basic authentication
 *
 * @version $Id: UserProvisioningOfficer.java,v 1.4 2008-06-24 12:50:06 piter_may Exp $
 */
public class UserProvisioningOfficer
extends DBOfficer
implements LazyInitBean {

    // ------------------------------------------------------------ Constructors
    public UserProvisioningOfficer() {
        super();
    }

    // ---------------------------------------------------------- Public methods

    public void init() {
        super.init();
    }

    /**
     * Authenticates a credential.
     *
     * @param credential the credential to be authenticated
     *
     * @return the Sync4jUser if the credential is autenticated, null otherwise
     */
    public Sync4jUser authenticateUser(Cred credential) {

        Configuration config = Configuration.getConfiguration();
        ps = config.getStore();

        userManager = (UserManager)config.getUserManager();

        String type = credential.getType();

        if ((Cred.AUTH_TYPE_BASIC).equals(type)) {
            return authenticateBasicCredential(credential);
        } else if ((Cred.AUTH_TYPE_MD5).equals(type)) {
            return authenticateMD5Credential(credential);
        }

        return null;
    }

    /**
     * Gets the supported authentication type
     *
     * @return the basic authentication type
     */
    public String getClientAuth() {
        return Cred.AUTH_TYPE_BASIC;
    }

    // ------------------------------------------------------- Protected Methods
    /**
     * Checks the given credential. If the user or the principal isn't found,
     * they are created.
     *
     * @param credential the credential to check
     *
     * @return the Sync4jUser if the credential is autenticated, null otherwise
     */
    protected Sync4jUser authenticateBasicCredential(Cred credential) {
        String username = null, password = null;

        Authentication auth = credential.getAuthentication();
        String deviceId = auth.getDeviceId();

        String userpwd = new String(Base64.decode(auth.getData()));

        int p = userpwd.indexOf(':');

        if (p == -1) {
            username = userpwd;
            password = "";
        } else {
            username = (p > 0) ? userpwd.substring(0, p) : "";
            password = (p == (userpwd.length() - 1)) ? "" :
                       userpwd.substring(p + 1);
        }

        if (log.isTraceEnabled()) {
            log.trace("User to check: " + username);
        }

        //
        // Gets the user without checking the password
        //
        Sync4jUser user = getUser(username, null);

        if (user == null) {

            if (log.isTraceEnabled()) {
                log.trace("User '" +
                           username +
                          "' not found. A new user will be created");
            }

            try {
                user = insertUser(username, password);
                if (log.isTraceEnabled()) {
                    log.trace("User '" + username + "' created");
                }
            } catch (Exception e) {
                log.error("Error inserting a new user", e);
                return null;
            }

        } else {
            if (log.isTraceEnabled()) {
                log.trace("User '" + username + "' found");
            }
            //
            // Check the password
            //
            String storedPassword = user.getPassword();
            if (!password.equals(storedPassword)) {
                //
                // The user isn't authenticated
                //
                if (log.isTraceEnabled()) {
                    log.trace( "The sent password is different from the stored "
                             + "one. User not authenticated");
                }
                return null;
            } else {
                //
                // Check the roles
                //
                boolean isASyncUser = isASyncUser(user);

                if (isASyncUser) {
                    //
                    // User authenticated
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("User authenticated");
                    }
                } else {
                    //
                    // User not authenticated
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("The user is not a '" + ROLE_USER + "'");
                    }
                    return null;
                }
            }
        }

        //
        // Verify that the principal for the specify deviceId and username exists
        // Otherwise a new principal will be created
        //
        
        try {
            handlePrincipal(user.getUsername(), deviceId);
        } catch (PersistentStoreException e) {
            log.error("Error handling the principal", e);
            return null;
        }

        return user;
    }

    /**
     * Insert a new user with the given username and password
     *
     * @param userName the username
     * @param password the password
     *
     * @return the new user
     *
     * @throws AdminException in case of admin errors
     * @throws PersistentStoreException if an error occurs
     */
    protected Sync4jUser insertUser(String userName, String password)
    throws AdminException, PersistentStoreException {

        Sync4jUser user = new Sync4jUser();
        user.setUsername(userName);
        user.setPassword(password);
        user.setRoles(new String[] {ROLE_USER});
        userManager.insertUser(user);
        return user;
    }

    /**
     * Returns the principal with the given username and deviceId.
     * <code>null</code> if not found
     * @param userName the username
     * @param deviceId the device id
     * @return the principal found or null.
     * @throws PersistentStoreException if an error occurs
     */
    protected Sync4jPrincipal getPrincipal(String userName, String deviceId)
    throws PersistentStoreException {

        Sync4jPrincipal principal = null;

        //
        // Verify that exist the principal for the specify deviceId and username
        //
        principal = Sync4jPrincipal.createPrincipal(userName, deviceId);

        try {
            ps.read(principal);
        } catch (NotFoundException ex) {
            return null;
        }

        return principal;
    }

    /**
     * Inserts a new principal with the given userName and deviceId
     * @param userName the username
     * @param deviceId the device id
     * @return the principal created
     * @throws PersistentStoreException if an error occurs creating the principal
     */
    protected Sync4jPrincipal insertPrincipal(String userName, String deviceId)
    throws PersistentStoreException {

        //
        // We must create a new principal
        //
        Sync4jPrincipal principal =
            Sync4jPrincipal.createPrincipal(userName, deviceId);

        ps.store(principal);

        return principal;
    }


    /**
     * Searchs if there is a principal with the given username and device id.
     * if no principal is found, a new one is created.
     * @param userName the user name
     * @param deviceId the device id
     * @return the found principal or the new one
     */
    protected Sync4jPrincipal handlePrincipal(String username, String deviceId)
    throws PersistentStoreException {

        Sync4jPrincipal principal = null;

        //
        // Verify if the principal for the specify deviceId and username exists
        //

        principal = getPrincipal(username, deviceId);

        if (log.isTraceEnabled()) {
            log.trace("Principal '" + username +
                       "/" +
                       deviceId + "' " +
                       ((principal != null) ?
                        "found"             :
                        "not found. A new principal will be created")
                       );
        }

        if (principal == null) {
            principal = insertPrincipal(username, deviceId);
            if (log.isTraceEnabled()) {
                log.trace("Principal '" + username +
                           "/" +
                           deviceId + "' created");
            }
        }

        return principal;
    }
}
