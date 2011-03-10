/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.server.sendlog.authentication;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.AuthorizationStatus;
import com.funambol.framework.security.Credential;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.tools.Base64;
import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.AdminManager;
import com.funambol.server.config.Configuration;

import com.funambol.server.sendlog.Constants;

/**
 * This class implements tha http basic authentication like schema based
 * on the Authorization header.
 * Besides, this authenticator makes some values contained into the request
 * header available, they are:
 * - username (the login contained in the Authorization header)
 * - deviceId (the value x-funambol-deviceId contained in the request)
 * 
 * @version $Id: HttpBasicAuthenticator.java 36089 2010-10-19 14:33:59Z luigiafassina $
 */
public class HttpBasicAuthenticator implements Constants {

    // ----------------------------------------------------------- Instance Data
    private static final FunambolLogger logger =
        FunambolLoggerFactory.getLogger(AUTHENTICATOR_SENDLOG_LOGGER_NAME);

    private AdminManager manager = null;

    private String credentialHeader = null;
    private String deviceId         = null;
    private String username         = null;

    // ------------------------------------------------------------- Constructor
    public HttpBasicAuthenticator(String credentialHeader, String deviceId) {
        this.credentialHeader = credentialHeader;
        this.deviceId         = deviceId;
    }
    
    // ------------------------------------------------------- Protected Methods

    /**
     * Authenticates the incoming request using an http basic like schema.
     * 
     * @throws UnauthorizedException if an error occurs
     */
    public void authenticate() throws UnauthorizedException {
        Credential credential = extractCredentials();
        authenticate(credential);
    }

    public String getUsername() {
        return this.username;
    }
    
    // --------------------------------------------------------- Private Methods

    /**
     * Extracts the credential information contained in the request object as
     * stated by the Http Basic Authentication:
     * http://www.w3.org/Protocols/HTTP/1.0/spec.html#BasicAA
     *
     * @param request the request object
     * @return the filled Credential
     * @throws UnauthorizedException
     */
    private Credential extractCredentials() throws UnauthorizedException {

        if(logger.isTraceEnabled()) {
            logger.trace("Extracting credentials...");
        }

        Credential credentials = parseCredentials();

        if (credentials == null) {
            throw new UnauthorizedException(AUTHORIZATION_BAD_FORMAT_ERRMSG);
        }

        fillCredentialsWithDeviceId(credentials);

        return credentials;
    }

    /**
     * Calls the officer for the authentication.
     *
     * @param credential the Credential to authenticate
     * @throws UnauthorizedException if an error occurs
     */
    private void authenticate(Credential credential) throws UnauthorizedException {

        if(logger.isTraceEnabled()) {
            logger.trace("Authenticating user...");
        }

        credential.setAuthType(Credential.AUTH_TYPE_BASIC);
        
        AuthorizationResponse response = null;
        try {
            response = getAdminManager().authorizeCredential(credential, Constants.RESOURCE_TO_AUTHORIZE);
            if(logger.isTraceEnabled()) {
                logger.trace("Authentication response read '"+response.getAuthStatus()+"'.");
            }
        } catch(AdminException e) {
           throw new UnauthorizedException("An error occured while sending authorization request to the server.", e);
        } catch(ServerException e ) {
            throw new UnauthorizedException("An error occured while sending authorization request to the server.", e);
        }

        int responseStatus = response.getAuthStatus();

        if(AuthorizationStatus.AUTHORIZED==responseStatus) {
            if(logger.isTraceEnabled()) {
                logger.trace("User authorized '"+responseStatus+"'");
            }
            this.username = credential.getUsername();
            this.deviceId = credential.getDeviceId();
        } else {
            if(logger.isTraceEnabled()) {
                logger.trace("Authorization failed.");
            }
            throw new UnauthorizedException("Authentication fails with response code '"+responseStatus+"'.");
        }
    }

    /**
     * Parses the Header authrization in order to fill the Credential object.
     *
     * @return the credentials parsed from the authorization header
     */
    private Credential parseCredentials() throws UnauthorizedException {
        String credentialsEncoded = credentialHeader.substring(HEADER_AUTHORIZATION_LENGHT);
        String credentialsPlain;
        Credential credentials = null;
        try {
            credentialsPlain = new String(Base64.decode(credentialsEncoded));
        } catch (Throwable e) {
            throw new UnauthorizedException(DECODING_TOKEN_ERRMSG, e);
        }
        int index = 0;
        if (StringUtils.isNotEmpty(credentialsPlain)
            && (index = credentialsPlain.indexOf(':')) >= 0) {
            
            credentials = new Credential();
            this.username = credentialsPlain.substring(0, index);
            //if needed the password is credentialsPlain.substring(index+1);
            credentials.setUsername(username);
            credentials.setDeviceId(deviceId);
            credentials.setCredData(credentialsEncoded);
        }
        return credentials;
    }

    /**
     * Set the device id in the Credential. If the device id does not exist, a
     * new device is stored. Since the device id is a mandatory parameter, if
     * it is not specified in the HTTP Header, an UnauthorizedException is
     * thrown.
     *
     * @param credential the Credential to fill
     * @param request the HTTP request from which retrieve the device id
     * @throws UnauthorizedException if an error occurs
     */
    private void fillCredentialsWithDeviceId(Credential credential)
    throws UnauthorizedException {
        //
        // If the device does not exist, it should be inserted.
        //
        DeviceInventory devInv = (DeviceInventory)Configuration
                                                 .getConfiguration()
                                                 .getDeviceInventory();

        Sync4jDevice device = new Sync4jDevice(deviceId);
        boolean deviceFound = false;
        try {
            //
            //Get device without capabilities
            //
            deviceFound = devInv.getDevice(device, false);
        } catch (DeviceInventoryException e) {
            throw new UnauthorizedException("Error reading device '" + deviceId +"'");
        }

        if (!deviceFound) {
            try {
                //
                // Creating a new device
                //
                devInv.setDevice(device);
            } catch (Exception e) {
                throw new UnauthorizedException("Error storing the new device '" + deviceId + "'");
            }
        }

        credential.setDeviceId(deviceId);
    }

    private AdminManager getAdminManager() throws UnauthorizedException {
        if(this.manager==null) {
            try {
                manager = new AdminManager();
            } catch(Throwable t) {
                throw new UnauthorizedException("An error occurred creating the AdminManager.", t);
            }
        }
        return manager;
    }
}
