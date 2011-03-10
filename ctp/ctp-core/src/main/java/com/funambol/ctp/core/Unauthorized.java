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

import java.util.Arrays;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.DbgTools;

/**
 * This class represents an <code>Unauthorized</code> status command.
 *
 * @version $Id: Unauthorized.java,v 1.4 2007-11-28 11:26:14 nichele Exp $
 */
public class Unauthorized extends Status {

    // --------------------------------------------------------------- Constants
    public static final String CMD_NAME    = "UNAUTHORIZED";

    public static final String PARAM_NONCE = "NONCE"       ;
    // ------------------------------------------------------------ Private data
    private byte[] nonce = null;

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of <code>Unauthorized</code> status command. */
    public Unauthorized() {
    }

    /**
     * Creates a new instance of <code>Unauthorized</code> status command with
     * nonce to use in the next authentication phase.
     *
     * @param nonce the nonce
     */
    public Unauthorized(byte[] nonce) {
        this.nonce = nonce;
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
     * Sets the nonce to use in the next authentication phase.
     *
     * @param nonce the nonce
     */
    public void setNonce(byte[] nonce) {
        this.nonce = nonce;
    }

    /**
     * Returns the nonce to use in the next authentication phase.
     *
     * @return the nonce
     */
    public byte[] getNonce() {
        return nonce;
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

        Unauthorized cmd = (Unauthorized)obj;
        if (this.getNonce() == null) {
            if (cmd.getNonce() != null) {
                return false;
            }
        } else {
            if (cmd.getNonce() == null) {
                return false;
            }

            if (!(Arrays.equals(this.getNonce(), cmd.getNonce()))) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
     @Override
    public String toString() {
        StringBuilder tmp = new StringBuilder();
        tmp.append(PARAM_NONCE).append("(B64)");
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.appendSuper(super.toString());
        sb.append(PARAM_NONCE, nonce != null ? DbgTools.bytesToHex(nonce) : nonce);
        sb.append(tmp.toString(), nonce != null ? new String(Base64.encode(nonce)) : nonce);
        return sb.toString();
    }
}
