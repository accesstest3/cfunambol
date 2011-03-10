/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.security;

import java.io.Serializable;

import java.security.Principal;

import com.funambol.framework.core.Authentication;
import com.funambol.framework.core.Cred;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.security.AbstractOfficer;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.Base64;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;

import com.funambol.email.console.manager.ConsoleManager;
import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.EmailConfigException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.OfficerRuntimeException;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.transport.MailServerError;
import com.funambol.email.util.Def;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;

/**
 * This is an implementation of the <i>Officier</i> interface.
 * It always authenticates and authorizes users and resource accesses.
 *
 * @version $Id: EmailOfficer.java,v 1.4 2008-05-18 15:06:13 nichele Exp $
 */
public class EmailOfficer
extends AbstractOfficer
implements Officer, Serializable {

    // ---------------------------------------------------------- Protected data
    
    protected PersistentStore ps             = null;
    
    protected UserManager     userManager    = null;
    
    private ConsoleManager    consoleManager = null;

    // -------------------------------------------------------------- Properties

    private String timeoutStore      = Def.DEFAULT_TIMEOUT_STORE;

    private String timeoutConnection = Def.DEFAULT_TIMEOUT_CONNECTION;
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * 
     * @todo hanlde exception
     */
    public EmailOfficer() {
        
        super();
        
        try {
            
            this.timeoutStore       = EmailConnectorConfig.getConfigInstance().getTimeoutStore();
            this.timeoutConnection  = EmailConnectorConfig.getConfigInstance().getTimeoutConnection();
            this.consoleManager     = new ConsoleManager();        
            
        } catch (EmailConfigException e) {
            log.error("Error Getting Connector Parameters ", e);
        }
        
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the supported authentication type
     *
     * @return the basic authentication type
     */
    public String getClientAuth() {
        return Cred.AUTH_TYPE_BASIC;
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
        userManager = config.getUserManager();
        ps = config.getStore();
        String type = credential.getType();
        if ((Cred.AUTH_TYPE_BASIC).equals(type)) {
            return authenticateBasicCredential(credential);
        }
        return null;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Checks the given B64 credential.
     *
     * @param credential the cred to check
     *
     * @return the Sync4jUser if the credential is autenticated, null otherwise
     */
    private Sync4jUser authenticateBasicCredential(Cred credential) {

        String username, password;

        Authentication auth = credential.getAuthentication();
        String deviceId = auth.getDeviceId();
        String userpwd = new String(Base64.decode(auth.getData()));

        int p = userpwd.indexOf(':');

        if (p == -1) {
            username = userpwd;
            password = "";
        } else {
            username = (p > 0) ? userpwd.substring(0, p) : "";
            password = (p == (userpwd.length() - 1)) ? "" : userpwd.substring(p + 1);
        }

        if (log.isTraceEnabled()) {
            log.trace("Username: " + username);
        }

        Sync4jUser dsuser = null;

        try {

            // check user in the DS-Server Schema
            dsuser = existsUser(username, EncryptionTool.encrypt(password));

            if (dsuser != null) {

                // user exists in the ds-schema
                if (log.isTraceEnabled()) {
                    log.trace("User with '" + username + "' exists.");
                }

                if (existsDevice(deviceId)){

                    // device exists in the ds-schema
                    if (log.isTraceEnabled()) {
                        log.trace("Device with '" + deviceId + "' exists.");
                    }

                    // insert principal if it doesn't exists
                    if (!existsPrincipal(username, deviceId)) {
                        long pri = insertPrincipal(username, deviceId);
                        credential.getAuthentication().setPrincipalId(pri);
                    }

                    // Verify these credentials on Mail Server
                    MailServerAccount msa = this.consoleManager.getUser(username);
                    if (msa == null){
                        return null;
                    }
                    boolean isMailServerUser = checkCredentials(msa);
                    if (!isMailServerUser) {
                        if (log.isTraceEnabled()) {
                            log.trace("User with '" + username +
                                    "' exists, but has an invalid Mail Server Account.");
                        }
                        return null;
                    }

                }

            }

        } catch (AdminException e) {
            log.error("Error inserting a new user / principal for the user "+username+": ", e);
            return null;
        } catch (PersistentStoreException e) {
            log.error("Error inserting a new user / principal for the user "+username+": ", e);
            return null;
        } catch (DeviceInventoryException die) {
            log.error("Error handling the device information for the user "+username+": ", die);
            return null;
        } catch (EntityException dbe) {
            log.error("Error get user account in the local db for the user "+username+": ", dbe);
            return null;
        } catch (EncryptionException ee) {
            log.error("Error handling the password of the user for the user "+username+": ", ee);
            return null;
        }

        return dsuser;

    }


    /**
     *
     * Checks if there is an user with the given username
     * 
     * error code
     * invalid protocol             = 1
     * invalid username or password = 2
     * cannection failed            = 3
     * 
     * @param userName the username
     * @return true if the user exists, false otherwise
     */
    private boolean checkCredentials(MailServerAccount msa) 
       throws OfficerRuntimeException {
            
        MailServerError errorCode = MailServerError.ERR_OK;
                  
        try {
            errorCode = consoleManager.guessAccount(msa, 
                                                   Integer.parseInt(timeoutStore), 
                                                   Integer.parseInt(timeoutConnection));
        } catch (Exception e) {
            throw new OfficerRuntimeException("Generic Error while verifying " +
                    "user " + msa.getUsername() + " on the Mail Server. ", e);
        }

        if (errorCode == MailServerError.ERR_OK) {
            return true;
        } else if (errorCode ==  MailServerError.ERR_INVALID_PROTOCOL){
            throw new OfficerRuntimeException("Error while verifying " +
                                "user " + msa.getUsername() + " - invalid protocol.");        
        } else if (errorCode == MailServerError.ERR_INVALID_INPUT){
            throw new OfficerRuntimeException("Error while verifying " +
                                "user " + msa.getUsername() + " - invalid credentials.");        
        
        } else if (errorCode == MailServerError.ERR_GENERIC_MAILSERVER){
            throw new OfficerRuntimeException("Error while verifying " +
                                "user " + msa.getUsername() + " - error connecting mail server.");                
        } else {
            throw new OfficerRuntimeException("Generic Error while verifying " +
                                "user " + msa.getUsername() + " on the Mail Server");        
        
        }        

    }

    
    
    /**
     * Checks if there is an user with the given username
     *
     * @param username the username
     * @param encPassword the encrypted password of the user
     * @return true if the user exists, false otherwise
     * @throws com.funambol.framework.server.store.PersistentStoreException
     * @throws com.funambol.admin.AdminException 
     */
    private Sync4jUser existsUser(String username, String encPassword) 
    throws PersistentStoreException, AdminException {

        Sync4jUser[] users;
               
        Clause wcUsername;
        String valueUsername[] = new String[]{username};
        wcUsername = new WhereClause("username", valueUsername, WhereClause.OPT_EQ, true);

        Clause wcPassword;
        String valuePassword[] = new String[]{encPassword};
        wcPassword = new WhereClause("password", valuePassword, WhereClause.OPT_EQ, true);

        Clause wcFinal = new LogicalClause(LogicalClause.OPT_AND,
                                  new Clause[]{wcUsername, wcPassword});
        
        users = userManager.getUsers(wcFinal);

        Sync4jUser user = null;
        if (users.length > 0){
            user = users[0];
        }

        return user;

    }

    /**
     * Checks if there is an user with the given username
     *
     * @param userName the username
     * @return true if the user exists, false otherwise
     */
    private boolean existsDevice(String deviceId) throws DeviceInventoryException {

        boolean exists = false;

        DeviceInventory devInventory =
                Configuration.getConfiguration().getDeviceInventory();

        exists = devInventory.getDevice(new Sync4jDevice(deviceId), false);

        return exists;
    }

    /**
     * Verify if there is a principal with the given userName and deviceId in store
     *
     * @param userName the username
     * @param deviceId the device's id
     * @throws PersistentStoreException if an error occurs
     */
    private boolean existsPrincipal(String userName, String deviceId)
    throws PersistentStoreException {
        Principal principal;
        try {
            principal = Sync4jPrincipal.createPrincipal(userName, deviceId);
            ps.read(principal);
            return true;
        } catch (NotFoundException e) {
            if (log.isTraceEnabled()) {
                log.trace("Principal for " + userName + ":" + deviceId + " not found");
            }
            return false;
        }
    }

    /**
     * Insert a principal
     *
     * @param userName the username
     * @param deviceId the device's id
     * @return the id of the principal
     * @throws PersistentStoreException if an error occurs
     */
    private long insertPrincipal(String userName, String deviceId)
    throws PersistentStoreException {
        Sync4jPrincipal principal =
            Sync4jPrincipal.createPrincipal(userName, deviceId);
        try {
            ps.store(principal);
        } catch (PersistentStoreException e) {
            log.error("Error creating new principal: ", e);
            if (log.isTraceEnabled()) {
                log.trace("authenticateBasicCredential", e);
            }
            throw e;
        }
        return principal.getId();
    }

   
}
