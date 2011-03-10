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
package com.funambol.admin.module.nodes;

import javax.swing.SwingUtilities;

import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;
import com.funambol.admin.AdminException;
import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jModule;

import org.openide.nodes.Children;
import org.openide.nodes.Children.Array;
import org.openide.nodes.Node;

/**
 * List of children of a containing node.
 * This is a simple Children.Array that loads on addNotify.<br/>
 * Rapresents a list of Sync4jConnector.
 * 
 * @version $Id: ModuleChildren.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class ModuleChildren extends Children.Array {

    // ------------------------------------------------------------ Private data

    /** Sync4jModule associated with this list of nodes */
    private Sync4jModule module;

    /** Set Sync4jModule */
    public void setSync4jModule(Sync4jModule module) {
        this.module = module;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Reload children
     */
    public void refreshChild() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                loadChildren();
            }
        });
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

        this.remove(getNodes());

        try {
            module = ExplorerUtil.getSyncAdminController().getBusinessDelegate().
                               getModule(module.getModuleId());

            Sync4jConnector[] connectors = null;

            connectors = module.getConnectors();

            int numConnectors = connectors.length;
            Node[] nodes = new Node[numConnectors];

            for (int i = 0; i < numConnectors; i++) {
                nodes[i] = new ConnectorNode(module, connectors[i]);
            }

            this.add(nodes);
        } catch (AdminException e) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
        }

    }

}
