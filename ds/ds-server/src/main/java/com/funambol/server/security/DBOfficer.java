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
package com.funambol.server.security;

import com.funambol.framework.core.Authentication;
import com.funambol.framework.core.Cred;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.security.AbstractOfficer;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.MD5;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;

/**
 * This is an implementation of the <i>Officier</i> interface. It always
 * authenticates and authorizes users and resource accesses.
 *
 * @version $Id: DBOfficer.java,v 1.4 2008-06-24 18:24:47 piter_may Exp $
 */
public class DBOfficer
extends AbstractOfficer
implements Officer, java.io.Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants
    protected static final String ROLE_USER = "sync_user";

    // ---------------------------------------------------------- Protected data
    protected PersistentStore ps          = null;
    protected UserManager     userManager = null;

    // ------------------------------------------------------------ Constructors
    public DBOfficer() {
        super();
    }

    // ---------------------------------------------------------- Public methods

    public void init() {
        Configuration config = Configuration.getConfiguration();
        ps = config.getStore();

        userManager = (UserManager)config.getUserManager();
    }

    /**
     * Authenticates a credential.
     *
     * @param credential the credential to be authenticated
     *
     * @return user the authenticated user, null otherwise
     */
    public Sync4jUser authenticateUser(Cred credential) {

        Sync4jUser user = null;

        String type = credential.getType();

        if ((Cred.AUTH_TYPE_BASIC).equals(type)) {

            user = authenticateBasicCredential(credential);

        } else if ( (Cred.AUTH_TYPE_MD5).equals(type)  ) {

            user = authenticateMD5Credential(credential);

        }
        return user;
    }

    // ------------------------------------------------------- Protected Methods
    /**
     * Returns the user (with the own roles) with the given username and
     * password (or null if it is not found).
     * The password could be null and in this case the user with the given
     * username is returned (or null if it is not found).
     * Search of the username is not case sensitive.
     * @param userName the username
     * @param password the password
     * @return the user with the own roles
     */
    protected Sync4jUser getUser(String userName, String password) {

        Sync4jUser[] users = null;

        try {
            Clause      verifyUserClause = null;
            WhereClause usernameClause   = null;
            WhereClause passwordClause   = null;

            String value[] = new String[1];
            value[0] = userName;
            usernameClause =
                new WhereClause("username", value, WhereClause.OPT_EQ, false);

            if (password != null) {
                //
                // The password is encrypted in the DB, so we have to encrypt the
                // searched value
                //
                value = new String[1];
                value[0] = EncryptionTool.encrypt(password);

                passwordClause =
                    new WhereClause("password", value, WhereClause.OPT_EQ, true);
                verifyUserClause =
                    new LogicalClause(LogicalClause.OPT_AND,
                                      new WhereClause[] {usernameClause,
                                                         passwordClause}
                    );
            } else {
                verifyUserClause = usernameClause;
            }

            users = userManager.getUsers(verifyUserClause);
            if (users.length == 1) {
                userManager.getUserRoles(users[0]);
                return users[0];
            }

        } catch (EncryptionException e) {
            log.error("Error encryption the searched password", e);
            return null;
        }  catch (AdminException e) {
            log.error("Error reading user", e);
            return null;
        }  catch (PersistentStoreException e) {
            log.error("Error reading user", e);
            return null;
        }

        return null;
    }

    /**
     * Checks if the given user is a sync_user
     * @param user the user to check
     * @return true if the given user is a sync_user, false otherwise
     */
    protected boolean isASyncUser(Sync4jUser user) {
        //
        // Check the roles
        //
        String[] roles = user.getRoles();
        if (roles == null || roles.length == 0) {
            return false;
        }
        for (int i = 0; i < roles.length; i++) {
            if (ROLE_USER.equals(roles[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the given MD5 credentials using the appropriate algorithm in
     * according to the given parameter SyncML VerProto
     *
     * @param credentials the cred to check
     *
     * @return the Sync4jUser if the credentials are autenticated, null otherwise
     */
    protected Sync4jUser authenticateMD5Credential(Cred credentials) {

        Sync4jPrincipal principals[] = null;
        boolean isAuthenticate = false;

        Authentication auth = credentials.getAuthentication();
        String deviceId = auth.getDeviceId();

        //
        // If the client sends the LocName in the SyncHdr, then use it as
        // username to authenticate the MD5 credential.
        //
        String clientUsername = auth.getUsername();
        Sync4jUser user = null;

        if (clientUsername != null) {
            
            user = getUser(clientUsername, null);
            
            if (user == null) {
                return null;
            }

            isAuthenticate = isCredAuthenticate(user, credentials, clientUsername);
            
        } else {
            //
            // If the username is not set, it is need to find it searching
            // a principal based on deviceId. For each found principal, get
            // the user. Then calculate the MD5 digest with this user and the
            // specified clientNonce. Compare the result with the digest sent
            // by client to establish if the credentials are authenticated.
            //
            //
            // Set WhereClause to find principals for the specified deviceId
            //
            WhereClause whereClause = new WhereClause("device",
                                                      new String[] {deviceId},
                                                      WhereClause.OPT_EQ,
                                                      true);
            try {
                principals = (Sync4jPrincipal[]) ps.read(
                                 Sync4jPrincipal.createPrincipal(null, deviceId),
                                 whereClause
                             );
            } catch (PersistentStoreException e) {
                log.error("Error reading principals", e);
                return null;
            }

            //
            // Search the user, verify and set it into credential if exists
            //
            int size = principals.length;
            for (int i = 0; i < size; i++) {
                user = getUser(principals[i].getUsername(), null);
                isAuthenticate = isCredAuthenticate(user, credentials, clientUsername);
                if (isAuthenticate) {
                    auth.setUsername(principals[i].getUsername());
                    break;
                }
            }
        }

        if (!isAuthenticate) {
            return null;
        }

        return user;
    }

    // --------------------------------------------------------- Private Methods

    private boolean isProtocolSyncML10(Cred credential) {
        String syncMLVerProto =
            credential.getAuthentication().getSyncMLVerProto();

        if (log.isTraceEnabled()) {
            log.trace("Check MD5 credential with protocol " + syncMLVerProto);
         }

        //
        // The modality in order to calculate the digest is different to second
        // of the used version of SyncML
        //
        if (syncMLVerProto.indexOf("1.0") != -1) {
            return true;
        }
        return false;
   }


    /**
     * Checks the given B64 credential.
     *
     * @param credential the cred to check
     *
     * @return the Sync4jUser if the credential is autenticated, null otherwise
     */
    private Sync4jUser authenticateBasicCredential(Cred credential) {
        String username = null, password = null;

        String userpwd =
            new String(Base64.decode(credential.getAuthentication().getData()));

        int p = userpwd.indexOf(':');

        if (p == -1) {
            username = userpwd;
            password = "";
        } else {
            username = (p>0) ? userpwd.substring(0, p) : "";
            password = (p == (userpwd.length()-1)) ? "" : userpwd.substring(p+1);
        }

        if (log.isTraceEnabled()) {
            log.trace("Username: " + username);
        }

        //
        // Verify the user
        //
        Sync4jUser user = getUser(username, password);

        if (user == null) {

            if (log.isTraceEnabled()) {
                log.trace("User '" + username + "' not found.");
            }
            return null;

        } else {

            if (log.isTraceEnabled()) {
                log.trace("User '" + username + "' found");
            }
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

        return user;
    }

    /**
     * Checks if the digest computed with the given clientNonce, userName,
     * password and the syncml 10 algorithm is equal to the given clientDigest.
     * @param clientDigest the client digest
     * @param clientNonce the nonce to use in the digest computation
     * @param userName the username
     * @param password the password
     * @return true if the clientDigest is right, false otherwise
     */
    private boolean checkMD5Credential10(String clientDigest,
                                         byte[] clientNonce,
                                         String userName,
                                         String password) {
        //
        // Calculate digest
        //
        String usernamepwd = userName + ':' + password;

        byte[] usernamepwdBytes = usernamepwd.getBytes();

        if (log.isTraceEnabled()) {
            log.trace("username: " + userName);
            log.trace("password: " + password);
            log.trace("clientNonce: " + new String(Base64.encode(clientNonce)));
        }

        //
        // computation of the MD5 digest
        //
        // Creates a unique buffer containing the bytes to digest
        //
        byte[] buf = new byte[usernamepwdBytes.length + 1 + clientNonce.length];

        System.arraycopy(usernamepwdBytes, 0, buf, 0, usernamepwdBytes.length);
        buf[usernamepwdBytes.length] = (byte) ':';
        System.arraycopy(clientNonce, 0, buf, usernamepwdBytes.length + 1,
                         clientNonce.length);

        byte[] digest = MD5.digest(buf);

        //
        // encoding digest in Base64 for comparation with digest sent by client
        //
        String serverDigestNonceB64 = new String(Base64.encode(digest));

        if (log.isTraceEnabled()) {
            log.trace("serverDigestNonceB64: " + serverDigestNonceB64);
            log.trace("clientDigest: " + clientDigest);
        }

        return clientDigest.equals(serverDigestNonceB64);
    }

    /**
     * Checks if the digest computed with the given clientNonce, userName,
     * password and the syncml 11 algorithm is equal to the given clientDigest.
     * @param clientDigest the client digest
     * @param clientNonce the nonce to use in the digest computation
     * @param userName the username
     * @param password the password
     * @return true if the clientDigest is right, false otherwise
     */
    private boolean checkMD5Credential11(String clientDigest,
                                         byte[] clientNonce,
                                         String userName,
                                         String password) {
        //
        // Calculate digest
        //
        byte[] userDigest = MD5.digest((new String(userName + ':' + password)).
                                       getBytes());
        byte[] userDigestB64 = Base64.encode(userDigest);

        if (log.isTraceEnabled()) {
            log.trace("username: " + userName);
            log.trace("userDigestB64: " + new String(userDigestB64));
            log.trace("clientNonce: " + new String(Base64.encode(clientNonce)));
        }

        //
        // computation of the MD5 digest
        //
        // Creates a unique buffer containing the bytes to digest
        //
        byte[] buf = new byte[userDigestB64.length + 1 + clientNonce.length];

        System.arraycopy(userDigestB64, 0, buf, 0, userDigestB64.length);
        buf[userDigestB64.length] = (byte) ':';
        System.arraycopy(clientNonce, 0, buf, userDigestB64.length + 1,
                         clientNonce.length);

        byte[] digest = MD5.digest(buf);

        //
        // encoding digest in Base64 for comparation with digest sent by client
        //
        String serverDigestNonceB64 = new String(Base64.encode(digest));

        if (log.isTraceEnabled()) {
            log.trace("serverDigestNonceB64: " + serverDigestNonceB64);
            log.trace("clientDigest: " + clientDigest);
        }

        return clientDigest.equals(serverDigestNonceB64);
    }

    /**
     * Checks if the credentials are authenticated
     *
     * @param user the Sync4jUser to check
     * @param cred the credentials to check
     *
     * @return true if the user is a sync_user and if the credentials are
     *         authenticated, false otherwise
     */
    private boolean isCredAuthenticate(Sync4jUser user, Cred cred, String clientUsername) {
        if (!isASyncUser(user)) {
            //
            // This user is not a sync_user
            //
            return false;
        }

        Authentication auth = cred.getAuthentication();

        String password = user.getPassword();

        //
        // digest sent by client in format b64
        //
        String msgDigestNonceB64 = auth.getData();
        byte[] clientNonce = auth.getNextNonce().getValue();

        boolean digestOk = false;

        if (isProtocolSyncML10(cred)) {
            digestOk = checkMD5Credential10(msgDigestNonceB64,
                                            clientNonce,
                                            clientUsername,
                                            password);
        } else {
            digestOk = checkMD5Credential11(msgDigestNonceB64,
                                            clientNonce,
                                            clientUsername,
                                            password);
        }
        if (digestOk) {
            return true;
        }
        return false;
    }

}
