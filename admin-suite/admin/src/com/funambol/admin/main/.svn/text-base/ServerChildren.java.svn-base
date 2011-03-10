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

package com.funambol.admin.main;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import com.funambol.admin.device.DeviceNode;
import com.funambol.admin.module.nodes.ModulesNode;
import com.funambol.admin.principal.PrincipalNode;
import com.funambol.admin.settings.nodes.ServerSettingsNode;
import com.funambol.admin.user.UsersNode;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 * List of children of a <code>ServerNode</code> node.
 * This is a simple Children.Array that loads on addNotify.<br/>
 * <p>Contains a:
 * <ul>
 *   <li><code>RolesNode</code>
 *   <li><code>DeviceNode</code>
 *   <li><code>PrincipalNode</code>
 *   <li><code>ModulesNode</code>
 * </ul>
 *
 *
 * @version $Id: ServerChildren.java,v 1.7 2008-05-22 22:11:24 nichele Exp $
 */

public class ServerChildren extends Children.Array {

    /**
     * States if the connected ds-server is a carrier edition or not
     */
    private boolean isCarrierEditionServer = false;

    private boolean allChildrenLoaded = false;

    /**
     * Constructor
     * @param isCarrierEditionServer true if the connected ds-server is a carrier edition,
     *        false otherwise
     */
    ServerChildren(boolean isCarrierEditionServer) {
        this.isCarrierEditionServer = isCarrierEditionServer;
    }

    public void showAllNodes() {
        //
        // Just in carrier mode some nodes are hidden
        //
        if (allChildrenLoaded) {
            return;
        }
        if (isCarrierEditionServer) {
            if (nodes != null) {
                allChildrenLoaded = true;

                UsersNode     usersNode     = null;
                DeviceNode    deviceNode    = null;
                PrincipalNode principalNode = null;
                
                try {
                    usersNode     = new UsersNode(Bundle.getMessage(Bundle.LABEL_NODE_USERS));
                    deviceNode    = new DeviceNode(Bundle.getMessage(Bundle.LABEL_NODE_DEVICES));
                    principalNode = new PrincipalNode(Bundle.getMessage(Bundle.LABEL_NODE_PRINCIPALS));

                    //
                    // Adding nodes in the right position
                    //
                    ((ArrayList)nodes).add(1, usersNode);
                    ((ArrayList)nodes).add(2, deviceNode);
                    ((ArrayList)nodes).add(3, principalNode);
                
                } catch (Exception e) {
                    Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
                }
                this.refresh();                
                
            }

        }
    }
    // ------------------------------------------------------- Protected Methods

    //-------------------------------------------------------
    // Overrides org.openide.nodes.Children method
    //-------------------------------------------------------

    /**
     * Called when children are first asked for nodes
     */
    protected void addNotify() {
        super.addNotify();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadChildren();
            }
        });

    }

    /**
     * Load children
     */
    protected void loadChildren() {
        ArrayList nodes = new ArrayList();

        nodes.add(new ServerSettingsNode(Bundle.getMessage(Bundle.LABEL_SERVER_SETTINGS)));

        if (!isCarrierEditionServer) {
            allChildrenLoaded = true;
            try {
                nodes.add(new UsersNode(Bundle.getMessage(Bundle.LABEL_NODE_USERS)));
                nodes.add(new DeviceNode(Bundle.getMessage(Bundle.LABEL_NODE_DEVICES)));
                nodes.add(new PrincipalNode(Bundle.getMessage(Bundle.LABEL_NODE_PRINCIPALS)));
            } catch (Exception e) {
                Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
            }
        }

        nodes.add(new ModulesNode(Bundle.getMessage(Bundle.LABEL_NODE_MODULES)));

        add((Node[])nodes.toArray(new Node[nodes.size()]));
    }

}
