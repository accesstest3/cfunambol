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
package com.funambol.framework.protocol.v11;

import com.funambol.framework.core.*;

import com.funambol.framework.protocol.ProtocolException;

/**
 *
 * @version $Id: BasicRequirements.java,v 1.1.1.1 2008-02-21 23:35:40 stefano_fornari Exp $
 */
public class BasicRequirements implements Errors {
    // ---------------------------------------------------- Contraint values

    public static final VerDTD[] SUPPORTED_DTD_VERSIONS =
        new VerDTD[] {Constants.DTD_1_0, Constants.DTD_1_1, Constants.DTD_1_2};

    public static final VerProto[] SUPPORTED_PROTOCOL_VERSIONS =
        new VerProto[] {Constants.PROT_1_0, Constants.PROT_1_1,
                        Constants.PROT_1_1_1, Constants.PROT_1_2};

    public static final String[] CAPABILITIES_SOURCE
		= new String[] {"./devinf12", "./devinf11", "./devinf10"};

    public static final String CAPABILITIES_TARGET_10 = "./devinf10";
    public static final String CAPABILITIES_TARGET_11 = "./devinf11";
    public static final String CAPABILITIES_TARGET_12 = "./devinf12";

    public static final String SERVER_CAPABILITIES = "server";
    public static final String CLIENT_CAPABILITIES = "client";

    //----------------------------------------------------------- Public methods

    static public void checkDTDVersion(VerDTD version)
    throws ProtocolException {
        for (int i=0; i<SUPPORTED_DTD_VERSIONS.length; ++i) {
            if (SUPPORTED_DTD_VERSIONS[i].equals(version)) {
                return;  // OK!
            }
        }
        String[] args = new String[] { version.toString() };
        throw new ProtocolException(ERRMSG_DTD_VER_NOT_SUPPORTED, args);
    }

    static public void checkProtocolVersion(VerProto version)
    throws ProtocolException {
        for (int i=0; i<SUPPORTED_PROTOCOL_VERSIONS.length; ++i) {
            if (SUPPORTED_PROTOCOL_VERSIONS[i].equals(version)) {
                return;  // OK!
            }
        }
        String[] args = new String[] { version.toString() };
        throw new ProtocolException(ERRMSG_PROTOCOL_VER_NOT_SUPPORTED, args);
    }

    static public void checkSessionId(SessionID id)
    throws ProtocolException {
        if ((id == null) || (id.toString().trim().length() == 0)) {
            throw new ProtocolException(ERRMSG_NO_SESSION_ID);
        }
    }

    static public void checkMessageId(String id)
    throws ProtocolException {
        if ((id == null) || (id.trim().length() == 0)) {
            throw new ProtocolException(ERRMSG_NO_MESSAGE_ID);
        }
    }

    /**
     * A target is valid if it is not null and one of <i>location name</i> or
     * <i>URI</i> is specified.
     *
     * @param target the target to be checked - NULL
     *
     * @throws ProtocolException
     */
    static public void checkTarget(Target target) throws ProtocolException {
        boolean valid    = target != null;
        boolean location = false;
        boolean uri      = false;

        if (valid) {
            location = (  (target.getLocName() != null)
                       && (target.getLocName().trim().length() != 0) );
            uri = (target.getLocURI() != null);

            valid = location || uri;
        }

        if (!valid) {
            String[] args = { ((target == null) ? "null" : target.getLocURI()) };
            throw new ProtocolException(ERRMSG_INVALID_TARGET, args);
        }
    }

    /**
     * A source is valid if it is not null and one of <i>location name</i> or
     * <i>URI</i> is specified.
     *
     * @param source the source to be checked - NULL
     *
     * @throws ProtocolException
     */
    static public void checkSource(Source source) throws ProtocolException {
        boolean valid    = source != null;
        boolean location = false;
        boolean uri      = false;

        if (valid) {
            location = (  (source.getLocName() != null)
                       && (source.getLocName().trim().length() != 0) );
            uri = (source.getLocURI() != null);
            valid = location || uri;
        }

        if (!valid) {
            String[] args = { ((source == null) ? "null" : source.getLocURI()) };
            throw new ProtocolException(ERRMSG_INVALID_SOURCE, args);
        }
    }

    static public void checkCommandId(CmdID commandId)
    throws ProtocolException {
        String cmdValue = null;

        if (  ( commandId == null                       )
           || ((cmdValue = commandId.getCmdID()) == null)
           || ( cmdValue.trim().length() == 0)          ) {
            throw new ProtocolException(ERRMSG_NO_MESSAGE_ID);
        }
    }

    static public void checkAlertCommand(Alert alert) throws ProtocolException {
        //
        // Check the command id
        //
        String[] args = new String[] {"alert is null!"};

        if (alert == null) {
            throw new ProtocolException(ERRMSG_INVALID_ALERT, args);
        }

        try {
            checkCommandId(alert.getCmdID());
        } catch (ProtocolException e) {
            args = new String[] { e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_ALERT, args);
        }
    }

    /**
     * Checks if the given command contains valid device information.
     *
     * @param cmd the command containing data
     * @param device specifies if the command should contain client or server
     *        capabilities
     *
     * @throws ProtocolException
     */
    static public void checkCapabilities(ItemizedCommand cmd, String device)
    throws ProtocolException {
        //
        // Checks command id
        //
        try {
            checkCommandId(cmd.getCmdID());
        } catch (ProtocolException e) {
            String[] args = new String[] { device, e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES, args);
        }

        //
        // Checks source
        //
        Item[] items = (Item[])cmd.getItems().toArray(new Item[0]);

        if ((items == null) || (items.length ==0)) {
            String[] args = new String[] { device, ERRMSG_MISSING_ITEM};
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES, args);
        }

        try {
            checkSource(items[0].getSource());
        } catch (ProtocolException e) {
            String[] args = new String[] { device, "missing source" };
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES, args);
        }

        boolean isOk = false;
        for (int i=0; i<CAPABILITIES_SOURCE.length; ++i) {
            if (CAPABILITIES_SOURCE[i].equals(items[0].getSource().getLocURI().toString())) {
                isOk = true;
                break;
            }
        }
        if (!isOk) {
            String[] args = new String[] { device, "URI not " + CAPABILITIES_SOURCE.toString()};
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES, args);
        }
    }
}
