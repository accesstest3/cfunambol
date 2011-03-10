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

import java.security.NoSuchAlgorithmException;

import com.funambol.framework.notification.NotificationException;

/**
 * Represents the Notification message in according with the Notification 
 * specification
 *
 * @version $Id: NotificationMessage.java,v 1.2 2007-11-28 11:15:50 nichele Exp $
 */
public class NotificationMessage {
    
    // ------------------------------------------------------------ Private data
    private DigestNotificationMessage digest;
    private HeaderMessage             header;
    private BodyMessage               body;
        
    // ------------------------------------------------------------ Constructors        
    public NotificationMessage() {}
    
    public NotificationMessage(DigestNotificationMessage digest,
                               HeaderMessage             header,
                               DSBodyMessage             body  ) {
        this.digest = digest;
        this.header = header;
        this.body   = body;
    }
    
    public NotificationMessage(DigestNotificationMessage digest,
                               HeaderMessage             header) {
        this.digest = digest;
        this.header = header;
    }    
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Gets the digest
     *
     * @return the digest
     */
    public DigestNotificationMessage getDigest() {
        return digest;
    }
    
    /**
     * Sets the digest
     *
     * @param digest the digest
     */
    public void setDigest(DigestNotificationMessage digest) {
        this.digest = digest;
    }
    
    /**
     * Gets the header
     *
     * @return the header
     */
    public HeaderMessage getHeader() {
        return header;
    }
    
    /**
     * Sets the header
     *
     * @param header the header message
     */
    public void setHeader(HeaderMessage header) {
        this.header = header;
    }
    
    /**
     * Gets the body
     *
     * @return the body
     */
    public BodyMessage getBody() {
        return body;
    }
    
    /**
     * Sets the body
     *
     * @param body the body message
     */
    public void setBody(BodyMessage body) {
        this.body = body;
    }
    
    public byte[] computeTriggerNotificationMessage() 
    throws NotificationException {
    
        byte[] toReturn = null;
        
        byte[] headerMessage = null;
        byte[] digestMessage = null;
        byte[] bodyMessage = null;
        
        /* Checks values */        
        if (header == null) {
            throw new NotificationException(
                "Header of the notification message could not be null");
        }
        
        if (digest == null) {
            throw new NotificationException(
                "Digest of the notification message could not be null");
        }
                
        headerMessage = header.buildByteMessageValue();
        
        if (body != null) {
            bodyMessage = body.getBytes();
        }
                
        try {
            digestMessage = digest.computeDigestMessage(mergeMessage(headerMessage, bodyMessage, null));
        } catch (NoSuchAlgorithmException ex) {
            throw new NotificationException("Error during digest computing", ex);
        }
                
        toReturn = mergeMessage(digestMessage, headerMessage, bodyMessage);
        
        return toReturn;
    }
    
    
    // --------------------------------------------------------- Private methods
    
    /**
     * Merge the given byte array into a new byte array
     *
     * @param msg1 byte[]
     * @param msg2 byte[]
     * @param msg3 byte[]
     *
     * @return byte[]
     */
    private byte[] mergeMessage(byte[] msg1, byte[] msg2, byte[] msg3) {
        
        byte[] toReturnMsg = null;
        
        int lengthMsg1 = 0;
        int lengthMsg2 = 0;
        int lengthMsg3 = 0;
        
        if (msg1 != null) {
            lengthMsg1 = msg1.length;
        } else {
            msg1 = new byte[0];
        }
        
        if (msg2 != null) {
            lengthMsg2 = msg2.length;
        } else {
            msg2 = new byte[0];
        }
        
        if (msg3 != null) {
            lengthMsg3 = msg3.length;
        } else {
            msg3 = new byte[0];
        }
        
        int toReturnLength = lengthMsg1 + lengthMsg2 + lengthMsg3;
        
        toReturnMsg = new byte[toReturnLength];
        
        System.arraycopy(msg1, 0, toReturnMsg, 0, lengthMsg1);
        System.arraycopy(msg2, 0, toReturnMsg, lengthMsg1, lengthMsg2);
        System.arraycopy(msg3, 0, toReturnMsg, lengthMsg1 + lengthMsg2, lengthMsg3);
        
        return toReturnMsg;
    }
    
}
