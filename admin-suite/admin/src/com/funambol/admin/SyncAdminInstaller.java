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

package com.funambol.admin;

import javax.swing.SwingUtilities;

import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.ServerExplorer;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

import org.openide.modules.ModuleInstall;
import org.openide.nodes.Node;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import java.util.Set;
import java.util.Iterator;

/**
 * The main SyncAdmin class
 * <p>This class, according to NetBeans framework, extends the ModuleInstall.</p>
 * <p>Initialization and finalization of the module are done in this class</p>
 *
 * @version $Id: SyncAdminInstaller.java,v 1.5 2007-11-28 10:28:16 nichele Exp $
 */
public class SyncAdminInstaller extends ModuleInstall {

    /**
     * The name of the workspace mode into which this root node
     * will be installed
     */
    public static final String EXPLORER_MODE_NAME = "explorer";

    /**
     * Retrieves a reference to the root node of this module if it has been
     * installed.
     * @return Node the root node of hte Explorer panel
     */
    public static Node getInstalledRoot() {
        ServerExplorer installedExplorer = ExplorerUtil.getExplorer();
        if (installedExplorer != null) {
            return installedExplorer.getExplorerManager().getRootContext();
        }

        return null;
    }

    /**
     * Installs and load the root explorer pane and a root node for this module,
     * dock the new panel into the right place and reset the view.
     */
    public void restored() {
        Runtime.getRuntime().addShutdownHook(new java.lang.Thread() {
            public void run() {
                closing();
            }
        });
    }

    /**
     * Closes the root panel for this module
     */
    public void uninstalled() {
        Log.debug("Funambol Administration Tool uninstalled.");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                ServerExplorer syncAdminExplorer = ExplorerUtil.getExplorer();

                if (syncAdminExplorer != null) {
                    syncAdminExplorer.close();
                }
            }
        });
    }

    /**
     * Release the resources for this module when the application is closing.
     * @return always true
     */
    public boolean closing() {
        Log.debug("Closing, releasing allocated resources");
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                SyncAdminController syncNode = (SyncAdminController)ExplorerUtil.getExplorer().
                                               getExplorerManager().getRootContext();
                if (syncNode != null) {
                    syncNode.close();
                }

                ServerExplorer syncAdminExplorer = ExplorerUtil.getExplorer();

                if (syncAdminExplorer != null) {
                    syncAdminExplorer.close();
                }
            }
        });

        return true;
    }


}
