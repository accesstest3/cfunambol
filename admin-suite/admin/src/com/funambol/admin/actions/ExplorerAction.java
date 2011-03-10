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

package com.funambol.admin.actions;

import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;

import com.funambol.admin.ui.ExplorerUtil;


/**
 * This action is a base class for the NodeActions associated to the explorer
 * modes. It, for example, redefines asynchronous() so that the action is 
 * executed in the right thread context. 
 *
 * It also work around the following problem encountered in NB 5.0 beta: 
 * the BeanNode.performAction(Node[] nodes) is always invoked with a zero
 * length array, regardless how many nodes are selected. We work around it
 * asking the selected nodes directly to the ExplorerManager.
 *
 * @version $Id: ExplorerAction.java,v 1.6 2007-11-28 10:28:16 nichele Exp $
 */
public abstract class ExplorerAction extends NodeAction {
    
    abstract public void performAction(final Node[] nodes);

    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected Node[] getSelectedNodes() {
        return ExplorerUtil.getExplorer().getExplorerManager().getSelectedNodes();
    }
    
    /**
     * Test whether the action should be enabled based on the currently activated nodes.
     *
     * @param nodes nodes that perform this action
     *
     * @return boolean returns alway <code>true</code>
     */
    protected boolean enable(Node[] nodes) {
        return true;
    }

    
}