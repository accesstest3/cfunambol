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

/**
 * This class represents the information to use during authorization check.
 *
 * @version $Id: Credential.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class Credential implements java.io.Serializable {
    // --------------------------------------------------------------- Constants
   public static final String AUTH_TYPE_MD5_1_0 = "md5-1.0";
   public static final String AUTH_TYPE_MD5_1_1 = "md5-1.1";
   public static final String AUTH_TYPE_BASIC   = "b64"    ;

    // ------------------------------------------------------------ Private data
    private String username = null;
    private String deviceId = null;
    private String authType = null;
    private String credData = null;

    // ------------------------------------------------------------ Constructors
    /** For serialization purposes */
    public Credential() {
    }

    /**
     * Gets the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username.
     *
     * @param username the username to check
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets the device identifier.
     *
     * @return the device identifier
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Sets the device identifier.
     *
     * @param deviceId the device identifier
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the authentication type.
     *
     * @return the authentication type
     */
    public String getAuthType() {
        return authType;
    }

    /**
     * Sets the authentication type.
     *
     * @param authType the authentication type
     */
    public void setAuthType(String authType) {
        this.authType = authType;
    }

    /**
     * Gets the credential.
     *
     * @return the credential
     */
    public String getCredData() {
        return credData;
    }

    /**
     * Sets the credential.
     *
     * @param credential the credential
     */
    public void setCredData(String credData) {
        this.credData = credData;
    }

    /**
     * Returns a string representation.
     *
     * @return a string representation
     */
    public String toString() {
        StringBuilder out = new StringBuilder();
        out.append(super.toString())
           .append("[username=").append(username)
           .append(",deviceId=").append(deviceId)
           .append(",authType=").append(authType)
           .append(",credData=").append(credData)
           .append(']');
        return out.toString();
    }
}
