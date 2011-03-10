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

package com.funambol.ctp.server.authentication.mock;

import com.funambol.ctp.server.authentication.AuthenticationManager;
import com.funambol.ctp.util.AuthenticationUtils;
import com.funambol.framework.security.AuthorizationResponse;
import com.funambol.framework.security.AuthorizationStatus;
import com.funambol.framework.tools.MD5;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @version $Id: MockAuthenticationManager.java,v 1.3 2007-11-28 11:26:16 nichele Exp $
 */
public class MockAuthenticationManager implements AuthenticationManager, Runnable {

    // run() method input/output parameter (I know it's horrible)
    private String username;
    private String credential;
    private AuthorizationResponse result;

    public Properties  prop = new Properties();
    private String dirPath = "config";
    private String nomeFile = "authentication.properties";

    // ------------------------------------------------------------ Constructors

    public MockAuthenticationManager() {
        readProperties();
    }

    // ---------------------------------------------------------- Public Methods

    public AuthorizationResponse authenticate(String username,
            String deviceId, String credential) {

        this.username = username;
        this.credential = credential;

        Thread t = new Thread(this, "Authentication thread for user " + username);
        t.start();

        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        return result;
    }

    public void run() {
        String localUserName = this.username;
        String localCredential = this.credential;

        try {
            Thread.sleep(10000);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        this.result = authenticate(localUserName, localCredential);
    }

    // --------------------------------------------------------- Private Methods

    private AuthorizationResponse authenticate(final String username,
            final String clientDigest) {

        String nonce = retrieveNonce(username);
        if (nonce == null) {
            int authorizationStatus = AuthorizationStatus.NOT_AUTHORIZED;
            byte[] nextNonce = MD5.getNextNonce();
            storeNonce(username, nextNonce);
            AuthorizationResponse result = new AuthorizationResponse();
            result.setAuthStatus(authorizationStatus);
            result.setNextNonce(nextNonce);
            return result;
        } else {
            String password = retrievePassword(username);

            if (password == null) {
                int authorizationStatus = AuthorizationStatus.NOT_AUTHENTICATED;
                byte[] nextNonce = MD5.getNextNonce();
                storeNonce(username, nextNonce);
                AuthorizationResponse result = new AuthorizationResponse();
                result.setAuthStatus(authorizationStatus);
                result.setNextNonce(nextNonce);
                return result;
            } else {

                String serverDigestNonceB64 = AuthenticationUtils.createDigest(username, password, nonce.getBytes());

                if (clientDigest.equals(serverDigestNonceB64)) {
                    int authorizationStatus = AuthorizationStatus.AUTHORIZED;
                    byte[] nextNonce = MD5.getNextNonce();
                    storeNonce(username, nextNonce);
                    AuthorizationResponse result = new AuthorizationResponse();
                    result.setAuthStatus(authorizationStatus);
                    result.setNextNonce(nextNonce);
                    return result;
                } else {
                    int authorizationStatus = AuthorizationStatus.NOT_AUTHORIZED;
                    byte[] nextNonce = MD5.getNextNonce();
                    storeNonce(username, nextNonce);
                    AuthorizationResponse result = new AuthorizationResponse();
                    result.setAuthStatus(authorizationStatus);
                    result.setNextNonce(nextNonce);
                    return result;
                }
            }
        }
    }

    /**
     * @return null if not found
     */
    private String retrievePassword(String username){

        String password = prop.getProperty("PASSWORD_" + username);
        return password;
    }

    /**
     * @return null if not found
     */
    private String retrieveNonce(String username){
        String password = prop.getProperty("NONCE_" + username);
        return password;
    }

    private void storeNonce(String username, byte[] nonce) {
        prop.setProperty("NONCE_" + username, new String(nonce));
        try {
            File propFile = new File(dirPath, nomeFile);
            prop.store(new FileOutputStream(propFile), "Authenticated users");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void readProperties() {
        try {
            File propFile = new File(dirPath, nomeFile);
            if (propFile.exists()) {
                prop.load(new FileInputStream(propFile));
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
