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
 * This class represents the &lt;CmdID&gt; element specified by the
 * SyncML representation DTD
 *
 * @version $Id: CmdID.java,v 1.6 2007/06/15 14:43:35 luigiafassina Exp $
 */
public class CmdID
implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private String cmdID;

    // ------------------------------------------------------------ Constructors
    /** For serialization purposes */
    public CmdID() {}

    /**
     * Creates a new CmdID object with the given String cmdID
     *
     * @param cmdID the cmdID of CmdID - NOT NULL
     *
     */
    public CmdID(final String cmdID) {
        if ((cmdID == null) || (cmdID.length() == 0)) {
            throw new IllegalArgumentException("cmdID cannot be empty");
        }
        this.cmdID = cmdID;
    }

    /**
     * Creates a new CmdID object with the given numeric cmdID
     *
     * @param cmdID the cmdID of CmdID
     *
     */
    public CmdID(final long cmdID) {
        this(String.valueOf(cmdID));
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets cmdID properties
     *
     * @return cmdID properties
     */
    public String getCmdID() {
        return this.cmdID;
    }

    /**
     * Compares the string cmdID to the specified input object.
     *
     * @param object the object to be compared
     *
     * @return true if the specified input object equals the cmdID of the
     *         CmdID
     *
     */
    public boolean equals(Object object) {
        String id = null;

        if (object instanceof String) {
            id = (String)object;
        } else if (object instanceof CmdID) {
            id = ((CmdID)object).getCmdID();
        }

        return (id == null) ? false : cmdID.equals(id);
    }
}
