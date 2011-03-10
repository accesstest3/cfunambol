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

/**
 *
 *  Corresponds to &lt;Cred&gt; element in SyncML represent DTD
 *
 *
 *
 *  @version $Id: Cred.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Cred
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
   public static final String AUTH_TYPE_MD5   = "syncml:auth-md5"  ;
   public static final String AUTH_TYPE_BASIC = "syncml:auth-basic";
   public static final String AUTH_TYPE_HMAC  = "syncml:auth-MAC"  ;
   public static final String AUTH_NONE       = "none"             ;
   public static final String AUTH_SUPPORTED_TYPES = AUTH_TYPE_BASIC
                                                   + ','
                                                   + AUTH_TYPE_MD5
                                                   + ','
                                                   + AUTH_TYPE_HMAC;

    // ------------------------------------------------------------ Private data
    private Authentication authentication;

    // ------------------------------------------------------------ Constructors
    /** For serialization purposes */
    protected Cred() {}

    /**
     * Creates a new Cred object with the given Authentication
     *
     * @param authentication the authentication object - NOT NULL
     *        This parameter will usually be an instance of
     *        BasicAuthentication or ClearAuthentication
     *
     */
    public Cred(final Authentication authentication) {
        if (authentication == null) {
            throw new IllegalArgumentException("authentication cannot be null");
        }
        this.authentication = authentication;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets type property
     *
     * @return type property
     */
    public String getType() {
        return authentication.getType();
    }

    /**
     * Gets format property
     *
     * @return format property
     */
    public String getFormat() {
        return authentication.getFormat();
    }

    /**
     * Gets data property
     *
     * @return data property
     */
    public String getData() {
        return authentication.getData();
    }

    /**
     * Gets the username stored in this credential
     *
     * @return the username stored in this credential
     */
    public String getUsername() {
        return authentication.getUsername();
    }


    /**
     * Create and return the Authentication object corresponding to the given
     * type and data.
     *
     * @param type the type of the required Authentication object
     * @param data the data to be interpreted based on the type
     *
     * @return the corresponding Authentication object.
     */
    public static Authentication createAuthentication(String data,
                                                      String type) {
        return new Authentication(type,data);
    }

    /**
     * Gets the Authentication object.
     *
     * @return authentication the authentication objects
     */
    public Authentication getAuthentication() {
        return authentication;
    }

    /**
     * Sets the Authentication object.
     *
     * @param auth the new Authentication object
     *
     */
    public void setAuthentication(Authentication auth) {
        this.authentication = auth;
    }

    @Override
    public String toString() {
        StringBuffer res = new StringBuffer();
        res.append(getUsername()).append(" with ").append(getType());
        res.append(" from ").append(getData());
        return res.toString();
    }
}
