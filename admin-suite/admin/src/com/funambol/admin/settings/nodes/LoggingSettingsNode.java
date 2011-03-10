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

package com.funambol.admin.settings.nodes;

import javax.swing.Action;

import org.openide.nodes.AbstractNode;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;

import com.funambol.admin.common.RefreshAction;
import com.funambol.admin.common.Refreshable;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.ui.ExplorerUtil;


/**
 * Node associated to the logging node.<br/>
 * Through this node, it is possible to manage server logging.
 *
 * @version $Revision: 1.6 $($Author: nichele $ / $Date: 2007-11-28 10:28:17 $)
 */
public class LoggingSettingsNode extends AbstractNode implements Refreshable {

    // ------------------------------------------------------------ Constructors

    /**
     * Create node with the given name
     * @param name node's name
     * @throws IntrospectionException
     */
    public LoggingSettingsNode() {
        super(new LoggingSettingsChildren());
        setIconBaseWithExtension(Constants.ICON_LOGGING_SETTINGS_NODE);

        String name = Bundle.getMessage(Bundle.LABEL_LOGGING_SETTINGS);
        setName(name);
        setDisplayName(name);
    }

    // ---------------------------------------------------------- Public Methods

    //-------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-------------------------------------------------------
    /**
     * Define actions associated to right button of the mouse for this node.
     *
     * @return SystemAction[] actions associated to right mouse button
     */
    public Action[] getActions(boolean context) {

        SystemAction[] actions = new SystemAction[] {
                                     NodeAction.get(RefreshAction.class)
                                 };

        return actions;

    }

    //-----------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-----------------------------------------------

    /**
     * Returns context help associated with this node
     * @return context help associated with this node
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    /**
     * Reload the children list
     */
    public void refresh() {
        ExplorerUtil.getSyncAdminController()
                    .getServerSettingsController()
                    .removeLoggerPanels();

        ((LoggingSettingsChildren)getChildren()).refreshChildren();
    }

}