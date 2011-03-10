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
 * This class represents the <code>Error</code> status command.
 *
 * @version $Id: Error.java,v 1.2 2007-11-28 11:26:14 nichele Exp $
 */
public class Error extends Status {

    // --------------------------------------------------------------- Constants
    public static final String CMD_NAME          = "ERROR"      ;

    public static final String PARAM_DESCRIPTION = "DESCRIPTION";

    // ------------------------------------------------------------ Private data
    private String description = null;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of <code>Error</code> status command.
     */
    public Error() {
    }

    /**
     * Create a new instance of <code>Error</code> status command with the error
     * description.
     *
     * @param description the description of the error
     */
    public Error(String description) {
        this.description = description;
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
     * Sets the description of the error.
     *
     * @param description the description of the error
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the description of the error.
     *
     * @return the description of the error
     */
    public String getDescription() {
        return description;
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

        Error cmd = (Error)obj;
        if (this.getDescription() == null) {
            if (cmd.getDescription() != null) {
                return false;
            }
        } else {
            if (cmd.getDescription() == null) {
                return false;
            }

            if (!(this.getDescription().equals(cmd.getDescription()))) {
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
        sb.append(PARAM_DESCRIPTION           , description);
        return sb.toString();
    }
}
