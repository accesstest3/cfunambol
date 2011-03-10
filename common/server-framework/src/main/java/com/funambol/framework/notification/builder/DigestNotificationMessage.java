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
package com.funambol.framework.notification.builder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.funambol.framework.tools.Base64;

/**
 * Represents the digest of the Notification message in according with the
 * Notification specification.
 * <p>
 * Compute the digest message in the following form:
 * <p>
 *      <ui>Digest = H(B64(H(serverId:pw)):nonce:B64(H(trigger)))</ui>
 * <p>
 * using MD5 algorithm.
 *
 * @version $Id: DigestNotificationMessage.java,v 1.2 2007-11-28 11:15:50 nichele Exp $
 */
public class DigestNotificationMessage {
    
    // --------------------------------------------------------------- Constants
    private static final String ALGORITHM_NAME = "MD5";
    private static final String SEPARATOR      = ":"  ;
    
    // -------------------------------------------------------------- Properties
    private String serverId;
    private String serverPw;
    private byte[] nonce   ;
    
    // ------------------------------------------------------------ Constructors
    
    public DigestNotificationMessage() {}
    
    /**
     * Creates a new DigestNotificationMessage object with the given parameters
     *
     * @param serverId
     * @param serverPw
     * @param nonce
     */
    public DigestNotificationMessage(final String serverId,
                                     final String serverPw,
                                     final byte[] nonce   ) {
        setServerId(serverId);
        setServerPw(serverPw);
        setNonce   (nonce   );
    }
    
    // ---------------------------------------------------------- Public methods
    /**
     * Gets the server identifier
     *
     * @return  the server identifier
     */
    public String getServerId() {
        return serverId;
    }
    
    /**
     * Sets the server identifier
     *
     * @param  the server identifier
     */
    public void setServerId(String serverId) {
        this.serverId = serverId;
    }
    
    /**
     * Gets the server password
     *
     * @return  the server password
     */
    public String getServerPw() {
        return serverPw;
    }
    
    /**
     * Sets the server password
     *
     * @param  the server password
     */
    public void setServerPw(String serverPw) {
        this.serverPw = serverPw;
    }
    
    /**
     * Gets the nonce
     *
     * @return  the nonce
     */
    public byte[] getNonce() {
        return nonce;
    }
    
    /**
     * Sets the nonce
     *
     * @param  the nonce; if null, the property nonce is set to an empty array
     */
    public void setNonce(byte[] nonce) {
        this.nonce = (nonce == null) ? new byte[0] : nonce;
    }
    
    /**
     * Compute the digest for the triggerMessage.
     * <p>
     * The digest message is calculate as:
     * <p>
     * Digest = H(B64(H(serverId:pw)):nonce:B64(H(trigger)))
     *
     * @param triggerMessage the message of which calculate the digest
     *
     * @throws NoSuchAlgorithmException if MD5 algorithm is not available
     * @return byte[] the digest messge
     */
    public byte[] computeDigestMessage(byte[] triggerMessage) 
    throws NoSuchAlgorithmException {
        
        String cred = serverId + SEPARATOR + serverPw;
        
        MessageDigest md =  MessageDigest.getInstance(ALGORITHM_NAME);
        
        byte[] digestTriggerMessage = null;
        byte[] b64DigestTriggerMessage = null;
        
        byte[] digestCred = null;
        byte[] b64DigestCred = null;
        byte[] digest = null;
        
        // H(trigger)
        digestTriggerMessage = md.digest(triggerMessage);
        
        // B64(H(trigger))
        b64DigestTriggerMessage = Base64.encode(digestTriggerMessage);
        
        md.reset();
        
        // H(serverId:pw)
        digestCred = md.digest(cred.getBytes());
        
        // B64(H(serverId:pw))
        b64DigestCred = Base64.encode(digestCred);
        
        md.reset();
        
        // Creates a unique buffer containing the bytes to digest
        //
        byte[] buf = new byte[b64DigestCred.length + 2 + nonce.length + b64DigestTriggerMessage.length];
        
        System.arraycopy(b64DigestCred, 0, buf, 0, b64DigestCred.length);
        buf[b64DigestCred.length] = (byte)':';
        System.arraycopy(nonce, 0, buf, b64DigestCred.length+1, nonce.length);
        buf[b64DigestCred.length + nonce.length + 1] = (byte)':';
        System.arraycopy(b64DigestTriggerMessage, 0, buf, b64DigestCred.length + nonce.length + 2, b64DigestTriggerMessage.length);
        
        digest = md.digest(buf);
        
        return digest;
    }
    
}
