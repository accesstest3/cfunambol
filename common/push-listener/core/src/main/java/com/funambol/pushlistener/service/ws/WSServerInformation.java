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

package com.funambol.pushlistener.service.ws;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Contains the server information to access to the webservices exposed by the
 * ds server.
 * @deprecated From version 7.1.0 use {@link #com.funambol.server.admin.ws.client.ServerInformation}
 * @version $Id: WSServerInformation.java,v 1.5 2007-11-28 11:15:04 nichele Exp $
 */
@Deprecated
public class WSServerInformation {

    // -------------------------------------------------------------- Properties

    /** The url to access to the webservices */
    private URL url         = null;

    public URL getUrl() {
        return url;
    }

    public void setUrl(String serverUrl) {
        try {
            this.url = new URL(serverUrl);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL '" + serverUrl + "'", e);
        }
    }

    /**  The username to access to the webservices */
    private String  username         = null;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**  The userName to access to the webservices */
    private String  password         = null;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // -------------------------------------------------------------- Properties
    // ------------------------------------------------------------- Constructor

    /** Empty constructor */
    public WSServerInformation() {

    }

    /**
     * Creates a new WSServerInformation with the given info
     * @param url the web services url
     * @param user the username to use
     * @param pwd the passwork to use
     */
    public WSServerInformation(String url, String user, String pwd) {
        this.setUrl(url);
        this.username = user;
        this.password = pwd;
    }    
    // ---------------------------------------------------------- Public methods
    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("serverUrl" , url);
        sb.append("username"  , username);
        sb.append("password"  , "******");
        return sb.toString();
    }
}

