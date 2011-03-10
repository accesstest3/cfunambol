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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.funambol.framework.notification.NotificationException;

/**
 * Represents the header of the Notification message in according with the 
 * Notification specification.
 *
 * @version $Id: HeaderMessage.java,v 1.2 2007-11-28 11:15:50 nichele Exp $
 */
public class HeaderMessage {
    
    // --------------------------------------------------------------- Constants
    public static final byte   UI_MODE_NOT_SPECIFIED     = 0;
    public static final byte   UI_MODE_BACKGROUND        = 1;
    public static final byte   UI_MODE_INFORMATIVE       = 2;
    public static final byte   UI_MODE_USER_INTERACTION  = 3;
    
    public static final byte   INITIATOR_CLIENT          = 0;
    public static final byte   INITIATOR_SERVER          = 1;
    
    private static final float MAX_VERSION_VALUE         = 102.3f;
    
    private static final int   NUM_BIT_VERSION_FIELD           = 10;
    private static final int   NUM_BIT_FUTURE_USE_FIELD        = 27;
    private static final int   NUM_BIT_SESSION_ID_FIELD        = 16;
    private static final int   NUM_BIT_SERVER_ID_LENGTH_FIELD  =  8;
    
    
    
    // ------------------------------------------------------------ Private data
    private float  version;
    private int    uiMode;
    private int    initiator;
    private int    sessionId;
    private String serverIdentifier;
    
    // ------------------------------------------------------------ Constructors
    
    public HeaderMessage() {}
    
    /**
     * Creates a new TriggerHeaderNotificationMessage object with the given parameters
     *
     * @param version the version of the SyncML DM protocol
     * @param uiMode the user interaction mode
     * @param initiator the initiator of the notification process
     * @param sessionId the session id
     * @param serverIdentifier the server id
     */
    public HeaderMessage(final float version,
                         final int uiMode,
                         final int initiator,
                         final int sessionId,
                         final String serverIdentifier) {
        this.version                  = version;
        this.uiMode                   = uiMode;
        this.initiator                = initiator;
        this.sessionId                = sessionId;
        this.serverIdentifier         = serverIdentifier;
    }
    
    // ---------------------------------------------------------- Public methods
    /**
     * Gets the version
     *
     * @return  the version
     */
    public float getVersion() {
        return version;
    }
    
    /**
     * Sets the version
     *
     * @param  the version
     * @throws NotificationException if the given value is bigger of <code>MAX_VERSION_VALUE</code>
     */
    public void setVersion(float version) throws NotificationException {
        
        if (version > MAX_VERSION_VALUE) {
            throw new NotificationException("Value specified for version information must be small of " + MAX_VERSION_VALUE);
        }
        this.version = version;
    }
    
    /**
     * Gets the uiMode
     *
     * @return the uiMode
     */
    public int getUiMode() {
        return uiMode;
    }
    
    /**
     * Sets the uiMode
     *
     * @param the uiMode
     *
     */
    public void setUiMode(int uiMode) {
        this.uiMode = uiMode;
    }
    
    /**
     * Gets the initiator
     *
     * @return the initiator
     */
    public int getInitiator() {
        return initiator;
    }
    
    /**
     * Sets the initiator
     *
     * @param the initiator
     *
     */
    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }
    
    /**
     * Gets the sessionId
     *
     * @return the sessionId
     */
    public int getSessionId() {
        return sessionId;
    }
    
    /**
     * Sets the sessionId
     *
     * @param the sessionId
     *
     */
    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Gets the server Identifier
     *
     * @return the server Identifier
     */
    public String getServerIdentifier() {
        return serverIdentifier;
    }
    
    /**
     * Sets the server Identifier
     *
     * @param the server Identifier
     *
     */
    public void setServerIdentifier(String serverIdentifier) {
        this.serverIdentifier = serverIdentifier;
    }
    
    /**
     * Builds header message
     * @throws NotificationException
     * @return byte[]
     */
    public byte[] buildByteMessageValue() throws NotificationException {
        
        byte[] toReturn = null;
        
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        BitOutputStream bout = new BitOutputStream(byteOut);
        
        
        byte[] bServerId = null;
        try {
            // write version field (NUM_BIT_VERSION_FIELD bit)
            int newVersion = (int) (version * 10);
            int[] intRepresentation = getIntBinaryRepresentation(newVersion, NUM_BIT_VERSION_FIELD);
            for (int i = 0; i < NUM_BIT_VERSION_FIELD; i++) {
                bout.writeBit(intRepresentation[i]);
            }
            
            // write ui mode field (2 bit)
            switch (uiMode) {
                case UI_MODE_NOT_SPECIFIED:
                    bout.writeBit(0);
                    bout.writeBit(0);
                    break;
                case UI_MODE_BACKGROUND:
                    bout.writeBit(0);
                    bout.writeBit(1);
                    break;
                case UI_MODE_INFORMATIVE:
                    bout.writeBit(1);
                    bout.writeBit(0);
                    break;
                case UI_MODE_USER_INTERACTION:
                    bout.writeBit(1);
                    bout.writeBit(1);
                    break;
                default:
                    bout.close();
                    throw new NotificationException("User interaction mode '" + uiMode +
                        "' isn't valid");
            }
            
            // write initiator field (1 bit)
            switch (initiator) {
                case INITIATOR_CLIENT:
                    bout.writeBit(0);
                    break;
                case INITIATOR_SERVER:
                    bout.writeBit(1);
                    break;
                    
                default:
                    bout.close();
                    throw new NotificationException("Initiator value '" + uiMode + "' isn't valid");
            }
            
            // write future use field (NUM_BIT_FUTURE_USE_FIELD bit)
            // in this version this field isn't used. Then write NUM_BIT_FUTURE_USE_FIELD bit = 0
            for (int i = 0; i < NUM_BIT_FUTURE_USE_FIELD; i++) {
                bout.writeBit(0);
            }
            
            
            int[] intRepresentationSessionId = getIntBinaryRepresentation(sessionId,
                NUM_BIT_SESSION_ID_FIELD);
            
            for (int i = 0; i < NUM_BIT_SESSION_ID_FIELD; i++) {
                bout.writeBit(intRepresentationSessionId[i]);
            }
            
            // write server identifier length
            int length = serverIdentifier.length();
            int[] intRepresentationLength = getIntBinaryRepresentation(length,
                NUM_BIT_SERVER_ID_LENGTH_FIELD);
            
            for (int i = 0; i < NUM_BIT_SERVER_ID_LENGTH_FIELD; i++) {
                bout.writeBit(intRepresentationLength[i]);
            }
            
            // server identifier
            bServerId = serverIdentifier.getBytes("UTF-8");
            
            bout.close();
        } catch (IOException ex) {
            throw new NotificationException("Error during message building", ex);
        }
        
        byte[] tmp = byteOut.toByteArray();
        
        toReturn = new byte[tmp.length + serverIdentifier.length()];
        
        System.arraycopy(tmp, 0, toReturn, 0, tmp.length);
        System.arraycopy(bServerId, 0, toReturn, tmp.length, bServerId.length);
                
        return toReturn;
    }
        
    // -------------------------------------------------------- Private Methods
    
    /**
     * Convert a string as 1010011 into int array with '0' or '1' element
     *
     * @param binaryRepresentation the binary representation string
     *
     * @return int[] the int array with '0' or '1' element
     */
    private static int[] converteBinaryRepresentation(String binaryRepresentation) {
        char[] c = binaryRepresentation.toCharArray();
        int[] intRepresentation = new int[c.length];
        
        for (int i = 0; i < c.length; i++) {
            intRepresentation[i] = Integer.parseInt(String.valueOf(c[i]));
        }
        return intRepresentation;
    }
    
    
    /**
     * Given a integer, returns a int array with '0' or '1' elements 
     * correspondent to the binary representation of the integer. If the binary 
     * representation is smaller that n bit then the binary representation 
     * is padding
     *
     * @param value the integer value
     * @param n the length of the binary representation
     *
     * @return int[] the binary representation
     */
    private int[] getIntBinaryRepresentation(int value, int n) {
        String sBinaryValue = Integer.toBinaryString(value);
        sBinaryValue = paddingString(sBinaryValue, n, '0', true);
        
        int[] intRepresentation = converteBinaryRepresentation(sBinaryValue);
        
        return intRepresentation;
    }
        
    /**
     * Pad a string S with a size of N with char C on the left (True) or 
     * on the right(False)
     *
     * @param s           the string to paddind
     * @param n           int the size
     * @param c           char the char used to padding
     * @param paddingLeft indicates if padding on the left (true) or 
     *                    on the right (false)
     *
     * @return the string after padding
     */
    private static String paddingString(String s, int n, char c, boolean paddingLeft) {
        StringBuffer str = new StringBuffer(s);
        int strLength = str.length();
        if (n > 0 && n > strLength) {
            for (int i = 0; i <= n; i++) {
                if (paddingLeft) {
                    if (i < n - strLength)str.insert(0, c);
                } else {
                    if (i > strLength)str.append(c);
                }
            }
        }
        return str.toString();
    }
    
}
