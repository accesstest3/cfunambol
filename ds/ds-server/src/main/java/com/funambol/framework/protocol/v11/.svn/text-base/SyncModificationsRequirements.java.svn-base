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
 * This class groups utility methods used for checking that a client modifications
 * massage follows the requirements that the protocol mandates.
 *
 *
 * @version $Id: SyncModificationsRequirements.java,v 1.1.1.1 2008-02-21 23:35:41 stefano_fornari Exp $
 */
public class SyncModificationsRequirements extends BasicRequirements implements Errors {

    // --------------------------------------------------------------- Constants

    // ---------------------------------------------------------- Public methods

    /**
     * Checks if the given command contains a valid request for server capablities.
     *
     * @param cmd the command containing the request
     *
     * @throws ProtocolException
     */
    static public void checkSync(Sync cmd) throws ProtocolException {
        //
        // Checks command id
        //
        try {
            checkCommandId(cmd.getCmdID());
        } catch (ProtocolException e) {
            String[] args = new String[] { e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_SYNC_COMMAND, args);
        }

        //
        // Checks modifications
        //
        AbstractCommand[] modifications =
           (AbstractCommand[])cmd.getCommands().toArray(new AbstractCommand[0]);

        for (int i=0; ((modifications != null) && (i<modifications.length)); ++i) {
            checkModification((ItemizedCommand)modifications[i]);
        }  // next i
    }

    /**
     * Checks the requirements for a modification command.
     *
     * @param cmd the modification command
     *
     * @throws ProtocolException
     */
    static public void checkModification(ItemizedCommand cmd)
    throws ProtocolException {
        //
        // Checks command id
        //
        try {
            checkCommandId(cmd.getCmdID());


            Item[] items = (Item[])cmd.getItems().toArray(new Item[0]);

            //
            // The type of each single item can be specified at command level
            // or at item level. If the type is specified at command level, items
            // without type will take the command type as default. If the type
            // is not specified at command level, each single item MUST specify
            // its type.
            //
            Meta meta = cmd.getMeta();

            boolean checkType = false;
            if (meta.getType() != null) {
                checkType = true;
            }

            for(int i=0; ((items != null) && (i<items.length)); ++i) {
                //
                // NOTE: all commands but delete use <Data> to carry data about
                // the modification
                //
                checkModificationItem(items[i], checkType, !cmd.getName().equals("Delete"));
            }

        } catch (ProtocolException e) {
            String[] args = new String[] { e.getMessage() };
            throw new ProtocolException(ERRMSG_INVALID_MODIFICATION_COMMAND, args);
        }
    }

    /**
     * Checks an item included into a modification command
     *
     * @param item the item to be checked
     * @param checkType indicats if the type specified in the <Meta> tag is
     *                  mandatory and must be checked
     * @param checkData indicates when check for the existance of the <Data> tag
     *
     * @throws ProtocolException
     */
    static public void checkModificationItem(final Item    item     ,
                                             final boolean checkType,
                                             final boolean checkData)
    throws ProtocolException {
        checkSource(item.getSource());

        if (checkType) {
            Meta meta = item.getMeta();
            if (meta.getType() == null) {
               String[] args = new String[] { item.toString() };
               throw new ProtocolException(ERRMSG_MISSING_TYPE, args);
            }
        }

        if (checkData) {
            if (item.getData() == null) {
                String[] args = new String[] { item.toString() };
                throw new ProtocolException(ERRMSG_MISSING_DATA, args);
            }
        }
    }

}
