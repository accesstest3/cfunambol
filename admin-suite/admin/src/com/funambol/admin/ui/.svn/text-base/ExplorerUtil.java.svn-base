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

import org.openide.nodes.Node;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.util.Constants;

/**
 * Simplifies the access to the <code>ServerExplorer</code>.
 *
 *
 * @version $Id: ExplorerUtil.java,v 1.8 2007-11-28 10:28:18 nichele Exp $
 */
public class ExplorerUtil {

    // ------------------------------------------------------------ Private data
    
    public static final String EXPLORER_MODE_NAME = "explorer";
    
    private static SyncAdminController syncAdminController = null;
    private static ServerExplorer      serverExplorer      = null;    
    
    // ---------------------------------------------------------- Public Methods
    /**
     * Returns explorer panel
     * @return ExplorerPanel
     */
    public static ServerExplorer getExplorer() {
        
        if (serverExplorer != null) {
            return serverExplorer;
        }
        Mode explorerMode = WindowManager.getDefault().findMode(EXPLORER_MODE_NAME);
 
        if (explorerMode != null) {            
            TopComponent[] components = explorerMode.getTopComponents();
            for (int i = 0; i < components.length; i++) {
                if (components[i] instanceof ServerExplorer) {
                    serverExplorer = (ServerExplorer)components[i];
                    return serverExplorer;
                }
            }
        }
        

        return null;
    }
     
    /**
     * Caches the given ServerExplorer
     * @param explorer the ServerExplorer to cache
     */
    public static void setServerExplorer(ServerExplorer explorer) {
        serverExplorer = explorer;
    }    
    
    /**
     * Returns the main controller.<br/>
     * Corresponds to the root node.
     *
     * @return instance of <code>SyncAdminController</code>
     */
    public static SyncAdminController getSyncAdminController() {
        //
        // We check if the cached controller is null. If so, a new one is created.
        //
        if (syncAdminController == null) {
            syncAdminController = (SyncAdminController) (getExplorer().getExplorerManager().getRootContext());
            return syncAdminController;
        }
        
        return syncAdminController;      
    }
    
    /**
     * Caches the given SyncAdminController
     * @param adminController the SyncAdminController to cache
     */
    public static void setSyncAdminController(SyncAdminController adminController) {
        syncAdminController = adminController;
        if (getExplorer() != null && adminController != null) { 
            getExplorer().setSyncAdminController(adminController);
        }
    }
}
