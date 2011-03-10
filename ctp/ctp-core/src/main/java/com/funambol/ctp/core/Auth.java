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
package com.funambol.ctp.core;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * This class represents the <code>Auth</code> command.
 *
 * @version $Id: Auth.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class Auth extends Command {

    // --------------------------------------------------------------- Constants
    public static final String CMD_NAME       = "AUTH"    ;

    public static final String PARAM_DEVID    = "DEVID"   ;
    public static final String PARAM_USERNAME = "USERNAME";
    public static final String PARAM_CRED     = "CRED"    ;
    public static final String PARAM_FROM     = "FROM"    ;

    // ------------------------------------------------------------ Private data
    private String devid    = null;
    private String username = null;
    private String cred     = null;
    private String from     = null;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of <code>Auth</code>.
     */
    public Auth() {
    }

    /**
     * Creates a new instance of <code>Auth</code> with device id, username and
     * user credentials information.
     *
     * @param devid the device id
     * @param username the username
     * @param cred the user credentials
     */
    public Auth(String devid, String username, String cred) {
        this(devid, username, cred, null);
    }

    /**
     * Creates a new instance of <code>Auth</code> with device id, username,
     * user credentials and the server originator information.
     *
     * @param devid the device id
     * @param username the username
     * @param cred the user credentials
     * @param from the server originator
     */
    public Auth(String devid, String username, String cred, String from) {
        this.devid    = devid   ;
        this.username = username;
        this.cred     = cred    ;
        this.from     = from    ;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Returns the command name.
     *
     * @return the command name
     */
    public String getName() {
        return CMD_NAME;
    }

    /**
     * Sets the device id.
     *
     * @param devid the device id
     */
    public void setDevid(String devid) {
        this.devid = devid;
    }

    /**
     * Returns the device id.
     *
     * @return the device id
     */
    public String getDevid() {
        return devid;
    }

    /**
     * Sets the username.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the username.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the user credentials.
     *
     * @param cred the user credentials
     */
    public void setCred(String cred) {
        this.cred = cred;
    }

    /**
     * Returns the user credentials.
     *
     * @return the user credentials
     */
    public String getCred() {
        return cred;
    }

    /**
     * Sets the server originator.
     *
     * @param from the server originator
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Returns the server originator.
     *
     * @return the server originator
     */
    public String getFrom() {
        return from;
    }

    /**
     * Compares <code>this</code> object with input object to establish if are
     * the same object.
     *
     * @param obj the object to compare
     * @return true if the objects are equals, false otherwise
     */
    public boolean deepequals(Object obj) {

        if (obj == null || (!obj.getClass().equals(this.getClass()))) {
            return false;
        }

        if (this == obj) {
            return true;
        }

        Auth cmd = (Auth)obj;
        if (this.getDevid() == null) {
            if (cmd.getDevid() != null) {
                return false;
            }
        } else {
            if (cmd.getDevid() == null) {
                return false;
            }

            if (!(this.getDevid().equals(cmd.getDevid()))) {
                return false;
            }
        }

        if (this.getUsername() == null) {
            if (cmd.getUsername() != null) {
                return false;
            }
        } else {
            if (cmd.getUsername() == null) {
                return false;
            }

            if (!(this.getUsername().equals(cmd.getUsername()))) {
                return false;
            }
        }

        if (this.getCred() == null) {
            if (cmd.getCred() != null) {
                return false;
            }
        } else {
            if (cmd.getCred() == null) {
                return false;
            }

            if (!(this.getCred().equals(cmd.getCred()))) {
                return false;
            }
        }

        if (this.getFrom() == null) {
            if (cmd.getFrom() != null) {
                return false;
            }
        } else {
            if (cmd.getFrom() == null) {
                return false;
            }

            if (!(this.getFrom().equals(cmd.getFrom()))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.appendSuper(super.toString());
        sb.append(PARAM_DEVID                 , devid);
        sb.append(PARAM_USERNAME              , username);
        sb.append(PARAM_CRED                  , cred);
        sb.append(PARAM_FROM                  , from);
        return sb.toString();
    }

}