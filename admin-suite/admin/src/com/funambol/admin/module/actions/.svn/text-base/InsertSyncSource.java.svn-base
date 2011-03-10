/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.module.actions;

import com.funambol.admin.actions.ExplorerAction;
import com.funambol.admin.module.nodes.SyncTypeNode;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;

import org.openide.nodes.Node;

/**
 * Used to start the process to create a new SyncSouce
 *
 * @version $Id: InsertSyncSource.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 */
public class InsertSyncSource extends ExplorerAction {

    // ---------------------------------------------------------- Public methods

    //-----------------------------------------------------
    // Overrides org.openide.util.actions.NodeAction method
    //-----------------------------------------------------

    /**
     * Perform the action based on the currently activated nodes
     * @param nodes current activated nodes
     */
    public void performAction(Node[] nodes) {
        Log.debug("InsertSyncSource - performAction");
        
        //
        // NOTE: this should be removed as soon as the problem with the nodes
        //       is fixed in NB (see ExplorerAction for details)
        nodes = getSelectedNodes();
        
        if (nodes.length == 0) {
            return;
        }
        
        SyncTypeNode node = (SyncTypeNode)nodes[0];
        try {
            ExplorerUtil.getSyncAdminController().getSyncSourcesController().
                startInsertNewSourceProcess(node);
        } catch (AdminException e) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
        }
    }

    //---------------------------------------------------------
    // Overrides org.openide.util.actions.SystemAction method
    //---------------------------------------------------------
    /**
     * Returns name of the action
     * @return String the name of this action
     */
    public String getName() {
        return Bundle.getMessage(Bundle.ACTION_ADD_SYNC_SOURCE_NAME);
    }
}
