/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
 * This class represents the &lt;Move&gt; tag as defined by the SyncML
 * representation specifications.
 *
 * @version $Id: Move.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Move
extends ItemizedCommand
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants
    public static String COMMAND_NAME = "Move";

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected Move() {}

    /**
     * Creates a new Move object given its elements.
     *
     * @param cmdID the command identifier - NOT NULL
     * @param noResp is &lt;NoResponse/&gt; required?
     * @param cred the authentication credentials
     * @param meta the meta information
     * @param items an array of Item element - NOT NULL
     *
     * @throws IllegalArgumentException if any NOT NULL parameter is null
     */
    public Move(final CmdID cmdID   ,
                final boolean noResp,
                final Cred cred     ,
                final Meta meta     ,
                final Item[] items  ) {
        super(cmdID, meta, items);

        setCred(cred);
        this.noResp = (noResp) ? new Boolean(noResp) : null;
    }

   // ----------------------------------------------------------- Public methods

    /**
     * Returns the command name
     *
     * @return the command name
     */
    public String getName() {
        return Put.COMMAND_NAME;
    }
}
