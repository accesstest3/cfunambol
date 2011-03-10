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
package com.funambol.admin.actions;

import java.util.Iterator;
import java.util.Set;

import org.openide.ErrorManager;
import org.openide.modules.ModuleInstall;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.CallableSystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import com.funambol.admin.SyncAdminInstaller;
import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.ServerExplorer;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;

/**
 * Action used to create the Funambol Administration Tool
 *
 * @version $Id: CreateConsole.java,v 1.8 2007-11-28 10:28:16 nichele Exp $
 */
public class CreateConsole extends CallableSystemAction {

    // ---------------------------------------------------------- Public Methods


    // ---------------------------------------------------------------
    // Overrides org.openide.util.actions.CallbackSystemAction method
    //----------------------------------------------------------------

    /**
     * Perform the action
     */
    public void performAction() {
                
        ServerExplorer explorer = ExplorerUtil.getExplorer();

        if (explorer == null) {
            //
            // Load up a new Explorer panel (this is redefined final in order to 
            // be used late by the inner class

            explorer = new ServerExplorer();

            // create root node with children
            SyncAdminController baseNode = new SyncAdminController();
            ExplorerUtil.setSyncAdminController(baseNode);

            explorer.getExplorerManager().setRootContext(baseNode);

            Mode explorerMode = WindowManager.getDefault().findMode("explorer");
            if (explorerMode != null) {
                // Dock the new panel into the right place
                explorerMode.dockInto(explorer);
                explorer.open();
                explorer.requestActive();
                PanelManager.init(null);
            }         

        } else {

            // reset the view
            ( (SyncAdminController) (explorer.getExplorerManager().getRootContext())).close();

            // create root node with children
            SyncAdminController baseNode = new SyncAdminController();
            ExplorerUtil.setSyncAdminController(baseNode);            

            explorer.getExplorerManager().setRootContext(baseNode);
           
            PanelManager.init(null);
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
        return Bundle.getMessage(Bundle.ACTION_CREATE_SYNC_CONSOLE_NAME);
    }

    //---------------------------------------------------------
    // Overrides org.openide.util.actions.SystemAction method
    //---------------------------------------------------------
    /**
     * Returns a help context for the action
     * @return context help associated with this action
     */
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    // ------------------------------------------------------- Protected Methods

    //---------------------------------------------------------
    // Overrides org.openide.util.actions.SystemAction method
    //---------------------------------------------------------
    /**
     * Specify the proper resource name for the action's icon.
     * @return String resource name for the action's icon.
     */
    protected String iconResource() {
        return Constants.ICON_ACTION_CREATE_CONSOLE;
    }
    
    protected boolean asynchronous() {
        return false;
    }

}