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

package com.funambol.admin.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.UIManager;

import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;

/**
 * Extend Explorer Panel adding a status bar used to show node's properties.
 *
 *
 * @version $Id: MiniStatusBar.java,v 1.5 2007-11-28 10:28:18 nichele Exp $
 **/

public class MiniStatusBar extends JLabel {

    // --------------------------------------------------------------- Constants

    /**  constant for */
    final static public String PROP_NODE_DESCRIPTION = "nodeDescription";

    // ------------------------------------------------------------ Private data

    /** ExplorerManager */
    private ExplorerManager explorerMgr;

    /**  Listener for event on the nodes */
    private PropertyChangeListener selectedNodesListener;

    // ------------------------------------------------------------ Constructors
    /**
     * Create a new MiniStatusBar
     * @param mgr ExplorerManager
     */
    public MiniStatusBar(ExplorerManager mgr) {
        init();
        setExplorerManager(mgr);
    }

    // --------------------------------------------------------- Public Methods

    /**
     * Sets a explorer manager on which is listening for the PROP_SELECTED_NODES changes.
     * @param mgr explorer manager whose nodes are described
     */
    public void setExplorerManager(ExplorerManager mgr) {
        if (explorerMgr != null && explorerMgr.equals(mgr)) {
            // notify the explorer tabs to show a description
            selectedNodesListener.propertyChange
                (new PropertyChangeEvent(this,
                                         ExplorerManager.PROP_SELECTED_NODES, null,
                                         mgr.getSelectedNodes()));

            // no change
            return;
        }
        // first remove listeners for old manager
        if (explorerMgr != null) {
            explorerMgr.removePropertyChangeListener(selectedNodesListener);
        }
        // add a listener for selected nodes
        if (mgr != null) {
            if (selectedNodesListener == null) {
                selectedNodesListener = new SelectedNodesListener();
            }
            selectedNodesListener.propertyChange
                (new PropertyChangeEvent(this,
                                         ExplorerManager.PROP_SELECTED_NODES, null,
                                         mgr.getSelectedNodes()));
            mgr.addPropertyChangeListener(selectedNodesListener);
        }
        explorerMgr = mgr;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Init the status bar
     */
    private void init() {
        setOpaque(false);
        setEnabled(true);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, UIManager.getDefaults().getColor("control")),
            BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 1,
                                            UIManager.getDefaults().getColor("controlHighlight")),
            BorderFactory.createLineBorder(UIManager.getDefaults().getColor("controlDkShadow"))
            ))
                  );
    }

    /**
     *
     * <p>Listener for the PROP_SELECTED_NODES changes.</p>
     *
     */
    class SelectedNodesListener implements PropertyChangeListener {

        // -------------------------------------------------------- Private Data

        /** array of described nodes */
        private Node[] describedNodes;

        // ------------------------------------------------------ Public Methods

        //----------------------------------------
        // Overrides javax.swing. method
        //----------------------------------------
        /**
         * This method gets called when a PROP_SELECTED_NODES property is changed.
         * @param evt A PropertyChangeEvent object describing the event source
         *   	and the property that has changed.
         */
        public void propertyChange(PropertyChangeEvent evt) {
            if (ExplorerManager.PROP_SELECTED_NODES.equals(evt.getPropertyName())) {
                Object newVal = evt.getNewValue();
                if (describedNodes != null && describedNodes.equals(newVal) && (evt.getOldValue() != null)) {

                    // no change
                    return;
                }
                if (newVal instanceof Node[]) {
                    describedNodes = (Node[])newVal;
                    String description = null;
                    for (int i = 0; i < describedNodes.length; i++) {
                        String newDesc = getNodeDesc(describedNodes[i]);
                        if (description == null) {
                            description = newDesc;
                        } else if (!description.equals(newDesc)) {
                            // if the types are mixed then no description
                            //description = NbBundle.getMessage(MiniStatusBar.class, "CTL_MixedValues");
                            description = null;
                            break;
                        }
                    }
                    // set node description to status bar
                    setText(description);
                }
            }
        }

        // ----------------------------------------------------- Private Methods

        /**
         * Returns node description
         * @param n number of the node
         * @return node description
         */
        private String getNodeDesc(Node n) {
            String nodeDesc = (String)n.getValue(PROP_NODE_DESCRIPTION);
            if (nodeDesc == null) {
                // if PROP_NODE_DESCRIPTION is null, try Node.PROP_SHORT_DESCRIPTION
                nodeDesc = n.getShortDescription();
                // change: status bar should be visible always
                if (nodeDesc != null && nodeDesc.equals(n.getDisplayName())) {
                    // Tool tip == display name by default; no special information.
                    nodeDesc = null;
                }
            }
            return nodeDesc;
        }

    }

}
