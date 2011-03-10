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

package com.funambol.framework.server;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * This class represents a user with username and password and a list of roles.
 *
 *
 *
 * @version $Id: Sync4jUser.java,v 1.2 2007-11-28 11:15:38 nichele Exp $
 *
 */
public class Sync4jUser implements Serializable {

    private String username;
    private String password;
    private String email;
    private String firstname;
    private String lastname;
    private String[] roles;

    /** Creates a new instance of Sync4jUser */
    public Sync4jUser() {
        this(null, null, null, null, null, null);
    }

    public Sync4jUser(String username , 
                      String password , 
                      String email    , 
                      String firstname, 
                      String lastname , 
                      String[] roles) {
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.firstname = firstname;
        this.lastname  = lastname;
        this.roles  = roles;
    }

    /** 
     * Getter for property username.
     * @return Value of property username.
     */
    public String getUsername() {
        return username;
    }

    /** 
     * Setter for property username.
     * @param username New value of property username.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** 
     * Getter for property password.
     * @return Value of property password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter for property password.
     * @param password New value of property password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /** 
     * Getter for property email.
     * @return Value of property email.
     */
    public String getEmail() {
        return email;
    }

    /** 
     * Setter for property email.
     * @param email New value of property email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /** 
     * Getter for property firstname.
     * @return Value of property firstname.
     */
    public String getFirstname() {
        return firstname;
    }

    /** 
     * Setter for property firstname.
     * @param firstname New value of property firstname.
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /** 
     * Getter for property lastname.
     * @return Value of property lastname.
     */
    public String getLastname() {
        return lastname;
    }

    /** 
     * Setter for property lastname.
     * @param lastname New value of property lastname.
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /** 
     * Getter for property roles.
     * @return Value of property roles.
     */
    public String[] getRoles() {
        return roles;
    }
    
    /**
     * Has the user the given role?
     *
     * @param role the role to check
     *
     * @return <i>true</i> if the user has the role <i>role</i> or <i>false</i> otherwise.
     */
    public boolean hasRole(final String role) {
        for (int i=0; (roles != null) && (i<roles.length); ++i) {
            if (roles[i].equals(role)) {
                return true;
            }
        }
        
        return false;
    }

    /** 
     * Setter for property roles.
     * @param roles New value of property roles.
     */
    public void setRoles(String[] roles) {
        this.roles = roles;
    }

    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);

        sb.append("username:",  username);
        sb.append("email:",     email);
        sb.append("firstname:", firstname);
        sb.append("lastname:",  lastname);

        return sb.toString();
    }

}
