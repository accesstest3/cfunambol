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
package com.funambol.framework.core;

import com.funambol.framework.tools.Base64;

/**
 * This is a base class for "authentication" classes
 *
 *
 *
 * @version $Id: Authentication.java,v 1.7 2007/06/15 14:43:35 luigiafassina Exp $
 */
public class Authentication {

    // ------------------------------------------------------------ Private data
    private String data     = null;
    private String username = null;
    private String password = null;
    private boolean encode = false;
    private String deviceId       = null ;
    private String syncMLVerProto = null ;
    private long   principalId    = -1;
    private String clientAddress  = null;

    private Meta meta = null;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    public Authentication() {}

    /**
     * Creates a new Authentication object with the given data
     *
     * @param meta the Meta object with authentication type and format
     * @param data the data of authentication
     *
     */
    public Authentication(final Meta meta, final String data) {
        this.meta = meta;
        createAuthentication(meta.getType(),data);
    }

    /**
     * Creates a new Authentication object with the given data
     *
     * @param type the authentication type
     * @param data the data of authentication
     *
     */
    public Authentication(final String type, final String data) {
        createAuthentication(type, data);
    }

    /**
     * Creates a new Authentication object with the given data
     *
     * @param type the authentication type
     * @param data the data of authentication
     * @param encode true if data is encoded, false otherwise
     *
     */
    public Authentication(final String type,
                          final String data,
                          final boolean encode) {

        this.encode = encode;
        createAuthentication(type, data);
    }

    /**
     * Creates a new Authentication object with the given data
     *
     * @param type the authentication type
     * @param username the username
     * @param password the password
     *
     */
    public Authentication(final String type,
                          final String username,
                          final String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException(
            "The authentication username and password cannot be null");
        }

        encode = true;
        createAuthentication(type, username + ":" + password);
    }

    // ---------------------------------------------------------- Public methods

    public void createAuthentication(String type, String data) {

        if (Cred.AUTH_SUPPORTED_TYPES.indexOf(type) < 0) {
            type = Cred.AUTH_TYPE_BASIC;
        }

        if (Cred.AUTH_TYPE_BASIC.equals(type)) {
            this.setType(Cred.AUTH_TYPE_BASIC);
            this.setFormat(Constants.FORMAT_B64);
            this.setData(data);
        } else if (Cred.AUTH_TYPE_MD5.equals(type)) {
            this.setType(Cred.AUTH_TYPE_MD5);
            this.setData(data);
        }
    }

    /**
     * Gets the type property
     *
     * @return the type property
     */
    public String getType() {
        return (meta == null) ? null : meta.getType();
    }

    /**
     * Sets the type property
     *
     * @param type the type property
     */
    public void setType(String type) {
        if (meta == null) {
            meta = new Meta();
        }
        meta.setType(type);
    }

    /**
     * Gets the format property
     *
     * @return the format property
     */
    public String getFormat() {
        return (meta == null) ? null : meta.getFormat();
    };

    /**
     * Sets the format property
     *
     * @param format the format property
     */
    public void setFormat(String format) {
        if (meta == null)  {
            meta = new Meta();
        }
        meta.setFormat(format);
    };

    /**
     * Gets the data property
     *
     * @return the data property
     */
    public final String getData() {
        return data;
    }
    
    /**
     * Gets the hidden data. This is used in the bindingHiddenData.xml in order
     * to avoid to show sensitive data.
     *
     * @return <i>*****</i>
     */
    public final String getHiddenData() {
        return "*****";
    }

    /**
     * Sets the data property
     *
     * @param data the data property
     *
     */
    public void setData(String data) {
        if (data == null) {
            throw new IllegalArgumentException("data cannot be null");
        }

        String type = this.getType();

        if (Cred.AUTH_TYPE_BASIC.equals(type)) {
            String clearData = null;

            if (encode) {
                this.data = new String(Base64.encode(data.getBytes()));
                clearData = data;
            } else {
                clearData = new String(Base64.decode(data.getBytes()));
                this.data = data;
            }

            int p = clearData.indexOf(':');

            if (p == -1) {
                this.setUsername(clearData);
                this.setPassword(null);
            } else {
                this.username = (p>0) ? clearData.substring(0, p) : "";
                this.password = (p<data.length()) ? clearData.substring(p+1) : "";
            }
        }

        if (Cred.AUTH_TYPE_MD5.equals(type)) {
            if (meta.getFormat() == null) {
                this.setFormat(Constants.FORMAT_B64);
            }
            this.username = data;
            this.data     = data;
        }
    }


    /**
     * Gets username property
     *
     * @return the username property
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username property
     *
     * @param username the username property
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Gets password property
     *
     * @return the password property
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the password property
     *
     * @param password the password property
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets the nextNonce property
     *
     * @return nextNonce the nextNonce property
     */
    public NextNonce getNextNonce() {
        return (meta == null) ? null : meta.getNextNonce();
    }

    /**
     * Sets the nextNonce property
     *
     * @param nextNonce the nextNonce property
     *
     */
    public void setNextNonce(NextNonce nextNonce) {
        if (meta == null)  {
            meta = new Meta();
        }
        meta.setNextNonce(nextNonce);
    };

    /**
     * Gets the meta property
     *
     * @return meta the meta property
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the meta property
     *
     * @param meta the meta property
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    };

    /**
     * Gets the device id
     *
     * @return deviceId the device identificator
     */
    public String getDeviceId() {
        return this.deviceId;
    }

    /**
     * Sets the device identificator
     *
     * @param deviceId the device identificator
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * Gets the SyncML Protocol version. It is useful to decide how calculate
     * the digest with MD5 authentication.
     *
     * @return syncMLVerProto the SyncML Protocol version.
     */
    public String getSyncMLVerProto() {
        return this.syncMLVerProto;
     }

    /**
     * Sets the SyncML Protocol version. It is useful to decide how calculate
     * the digest with MD5 authentication.
     *
     * @param syncMLVerProto the SyncML Protocol version.
     *
     */
     public void setSyncMLVerProto(String syncMLVerProto) {
         this.syncMLVerProto = syncMLVerProto;
     }

    /**
     * Gets the principal id
     *
     * @return principalId the principal identificator
     */
    public long getPrincipalId() {
        return this.principalId;
    }

    /**
     * Sets the principal identificator
     *
     * @param principalId the principal identificator
     */
    public void setPrincipalId(long principalId) {
        this.principalId = principalId;
    }
    
    /**
     * Gets the client remote 
     *
     * @return clientAddress the client remote address
     */
    public String getClientAddress() {
        return this.clientAddress;
    }

    /**
     * Sets the client address
     *
     * @param clientAddress the client address
     */
    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }    
}
