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

package com.funambol.email.transport;

import com.funambol.email.util.Def;

/**
 * This class represent errors given by a mail server while accessing it.
 *
 * @version $Id: MailServerError.java,v 1.1 2008-04-03 11:14:42 gbmiglia Exp $
 */
public class MailServerError implements java.io.Serializable {
    
    // --------------------------------------------------------------- Constants
    
    public static final MailServerError ERR_OK = 
            new MailServerError(Def.ERR_OK, "");

    public static final MailServerError ERR_INVALID_PROTOCOL = 
            new MailServerError(Def.ERR_INVALID_PROTOCOL, "");
    
    public static final MailServerError ERR_INVALID_INPUT = 
            new MailServerError(Def.ERR_INVALID_INPUT, "");
    
    public static final MailServerError ERR_GENERIC_MAILSERVER = 
            new MailServerError(Def.ERR_GENERIC_MAILSERVER, "");
    
    // ------------------------------------------------------------ Constructors
    
    /** 
     * Creates a new instance of <code>MailServerError</code>. 
     */
    public MailServerError(int code, String description) {
        this.code = code;
        this.description = description;
    }
    
    // -------------------------------------------------------------- Properties
    
    /** Error code. */
    private int code;
    
    /** Error description. */
    private String description;
    
    // ---------------------------------------------------- Properties accessors

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
    
    // ---------------------------------------------------------- Public methods
    
    @Override
    public String toString() {
        
        StringBuilder builder = new StringBuilder();
        builder.append("code: ").
                append(code).
                append(", ").
                append("description: ").
                append(description);
        return builder.toString();
    }
}
