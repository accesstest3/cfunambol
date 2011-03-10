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
import com.funambol.framework.protocol.v11.BasicRequirements;
import com.funambol.framework.protocol.v11.Errors;

/**
 * This class groups utility methods used for checking that an initialization
 * massage follows the requirements that the protocol mandates.
 *
 *
 *
 * @version $Id: InitializationRequirements.java,v 1.1.1.1 2008-02-21 23:35:41 stefano_fornari Exp $
 */
public class InitializationRequirements
extends BasicRequirements
implements Errors {

    // --------------------------------------------------------------- Constants

    // ---------------------------------------------------------- Public methods

    static public void checkAlertCommand(Alert alert) throws ProtocolException {
        try {
            BasicRequirements.checkAlertCommand(alert);
        } catch (ProtocolException e) {
            String[] args = new String[] { e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_ALERT, args);
        }

        //
        // Checks the alert code
        //
        if (!AlertCode.isInitializationCode(alert.getData())) {
            String[] args = new String[] { String.valueOf(alert.getData()) };
            throw new ProtocolException(ERRMSG_INVALID_ALERT_CODE, args);
        }

        //
        // Checks source and target
        //
        Item[] items = (Item[])alert.getItems().toArray(new Item[0]);

        if ((items == null) || (items.length ==0)) {
            throw new ProtocolException(ERRMSG_MISSING_ITEM);
        }

        try {
            checkSource(items[0].getSource());
        } catch (ProtocolException e) {
            String[] args = new String[] { "source" };
            throw new ProtocolException(ERRMSG_INVALID_ALERT, args);
        }

        try {
            checkTarget(items[0].getTarget());
        } catch (ProtocolException e) {
            String[] args = new String[] { "target" };
            throw new ProtocolException(ERRMSG_INVALID_ALERT, args);
        }

        //
        // The Alert 224 (Suspend) doesn't contain the anchor, so we mustn't check it
        //
        if (alert.getData() != AlertCode.SUSPEND) {
            //
            // Checks sync anchors
            //
            Meta meta = items[0].getMeta();

            if (meta == null || meta.getAnchor() == null) {
                throw new ProtocolException(ERRMSG_MISSING_SYNC_ANCHOR);
            }
        }
    }

    /**
     * Checks if the given command contains a valid request for server capablities.
     *
     * @param cmd the command containing the request
     *
     * @throws ProtocolException
     */
    static public void checkCapabilitiesRequest(Get cmd)
    throws ProtocolException {
        //
        // Checks command id
        //
        try {
            checkCommandId(cmd.getCmdID());
        } catch (ProtocolException e) {
            String[] args = new String[] { e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES, args);
        }

        Item[] items = (Item[])cmd.getItems().toArray(new Item[0]);

        if ((items == null) || (items.length ==0)) {
            String[] args = new String[] {ERRMSG_MISSING_ITEM};
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES_REQUEST, args);
        }

        try {
            checkTarget(items[0].getTarget());
        } catch (ProtocolException e) {
            String[] args = new String[] { "missing target" };
            throw new ProtocolException(ERRMSG_INVALID_CAPABILITIES_REQUEST, args);
        }
    }
}
