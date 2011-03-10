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
package com.funambol.framework.security;

import java.security.Principal;

import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;

/**
 * This class implements the <i>Pricipal</i> interface and represents a Sync4j
 * principal. In Sync4j a principal is represented by a couple
 * <i>(username, device id)</i> and is associated to a unique id.
 *
 * @version $Id: Sync4jPrincipal.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class Sync4jPrincipal implements Principal, java.io.Serializable {
    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data

    private long id             = -1; // principalid
    private Sync4jUser   user   = null;
    private Sync4jDevice device = null;

    private String encodedUserPwd = null;

    // ------------------------------------------------------------ Constructors
    /**
     * This should not be used. It is here for serialization purposes only.
     */
    public Sync4jPrincipal() {};

    /**
     * Creates a new instance of Principal
     *
     * @param id principal id
     * @param user the user
     * @param device the device
     */
    public Sync4jPrincipal(final long         id,
                           final Sync4jUser   user,
                           final Sync4jDevice device) {
        this.id     = id      ;
        this.user   = user;
        this.device = device;
    }


    /**
     * Creates a new instance of Principal
     *
     * @param user the user
     * @param deviceId the device
     *
     */
    public Sync4jPrincipal(final Sync4jUser user, final Sync4jDevice device) {
        this(-1, user, device);
    }

    /**
     * Creates a new instance of Principal
     *
     * @param id principal id
     */
    public Sync4jPrincipal(final long id) {
        this(id, null, null);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Creates a new principal with the given information
     * @param userName String
     * @param deviceId String
     * @return Sync4jPrincipal
     */
    public static Sync4jPrincipal createPrincipal(String userName,
                                                  String deviceId) {
        Sync4jUser user = new Sync4jUser();
        Sync4jDevice device = new Sync4jDevice();
        user.setUsername(userName);
        device.setDeviceId(deviceId);

        Sync4jPrincipal principal = new Sync4jPrincipal(-1, user, device);
        return principal;
    }

    /**
     * Creates a new principal with the given information
     * @param id the principal id
     * @param userName the username
     * @param deviceId the device id
     * @return Sync4jPrincipal
     */
    public static Sync4jPrincipal createPrincipal(long id,
                                                  String userName,
                                                  String deviceId) {
        Sync4jUser user = new Sync4jUser();
        Sync4jDevice device = new Sync4jDevice();
        user.setUsername(userName);
        device.setDeviceId(deviceId);

        Sync4jPrincipal principal = new Sync4jPrincipal(id, user, device);
        return principal;
    }

    /**
     * The principal name that is concatenation of the deviceId, '/' and
     * username or only the username if the deviceId is not defined.
     *
     * @return the principal's name
     */
    public String getName() {
        StringBuffer sbName = new StringBuffer();
        if (device != null) {
            sbName.append(device.getDeviceId()).append("/");
        }
        if (user != null) {
            sbName.append(user.getUsername());
        }

        return sbName.toString();
    }

    /**
     * The username part of the principal name.
     *
     * @return the username part of the principal name.
     */
    public String getUsername() {
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }

    /**
     * Returns the user.
     *
     * @return the user.
     */
    public Sync4jUser getUser() {
        return user;
    }

    /**
     * Set the user.
     *
     * @param user the user
     */
    public void setUser(Sync4jUser user) {
        this.user = user;
    }


    /**
     * Sets the username. If there is already an user with different username,
     * or if there isn't a user, a new user is created.
     *
     * @param username the username
     */
    public void setUsername(String username) {
        if (this.user != null) {
            if (this.user.getUsername().equals(username)) {
                return ;
            } else {
                throw new IllegalArgumentException(
                        "This principal has already an user with a different username"
                );
            }
        }
        this.user = new Sync4jUser();
        this.user.setUsername(username);
    }

    /**
     * Returns the device id of the device.
     *
     * @return the device id.
     */
    public String getDeviceId() {
        if (device != null) {
            return device.getDeviceId();
        }
        return null;
    }

    /**
     * Returns the device.
     *
     * @return the device.
     */
    public Sync4jDevice getDevice() {
        return device;
    }

    /**
     * Set the device.
     *
     * @param deviceId the device
     */
    public void setDevice(Sync4jDevice device) {
        this.device = device;
    }

    /**
     * Set the device id. If there is already a device with different deviceId,
     * or if there isn't a device, a new device is created.
     *
     * @param deviceId the device id
     */
    public void setDeviceId(String deviceId) {
        if (this.device != null) {
            if (this.device.getDeviceId().equals(deviceId)) {
                return ;
            } else {
                throw new IllegalArgumentException(
                        "This principal has already a device with a different id"
                );
            }

        }
        this.device = new Sync4jDevice();
        this.device.setDeviceId(deviceId);
    }


    /**
     * The principal unique id.
     *
     * @return the unique principal id.
     */
    public long getId() {
        return id;
    }

    /**
     * Set the principal unique id.
     *
     * @param id the principal unique id.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Two Sync4jPrincipals are equals if and only if:
     * <ul>
     *   <li>o is null or
     *   <li>o is not an instance of Sync4jPrincipal or
     *   <li>the two getName()s do not match
     * </ul>
     *
     * @param o the reference object with which to compare.
     *
     * @return true if this object is the same as the obj argument; false otherwise.
     */
    public boolean equals(Object o) {
        if ((o == null) || !(o instanceof Sync4jPrincipal)) {
            return false;
        }

        return (getName().equals(((Sync4jPrincipal)o).getName()));
    }

    /**
     * Returns the hashCode of this principal using the hashCode of the name.
     * @return int
     */
    public int hashCode() {
        return getName().hashCode();
    }

    /**
     * Returns a string representation of the principal
     * @return String
     */
    public String toString() {
        return getName();
    }

    /**
     * Returns the encodedUserPwd
     * @return encodedUserPwd user and password in the form <user>:<password>
     */
    public String getEncodedCredentials() {
        return encodedUserPwd;
    }

    /**
     * Set the encodedUserPwd
     *
     * @param newEncodedUserPwd the encodedUserPwd.
     */
    public void setEncodedUserPwd(String newEncodedUserPwd) {
        this.encodedUserPwd = newEncodedUserPwd;
    }
}
