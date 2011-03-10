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

import java.awt.BorderLayout;

import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.TreeView;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;

import com.funambol.admin.main.SyncAdminController;

import com.funambol.admin.util.Bundle;


/**
 * Extended Explorer Panel.
 *
 *
 * @version $Id: ServerExplorer.java,v 1.10 2007-11-28 10:28:18 nichele Exp $
 **/

public class ServerExplorer
extends TopComponent
implements ExplorerManager.Provider, Lookup.Provider {

    private static String name = Bundle.getMessage(Bundle.EXPLORER_TITLE) +
                                 " v." +
                                 Bundle.getMessage(Bundle.VERSION);

    // ------------------------------------------------------------ Private data

    /** composited view */
    protected TreeView view;

    /** explorer manager */
    private ExplorerManager explorerManager;

    /** mini status bar */
    private MiniStatusBar miniStatusBar;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     */
    public ServerExplorer() {
        explorerManager = new ExplorerManager();
        
        //
        // Now we create the root node
        //
        SyncAdminController baseNode = new SyncAdminController();
        ExplorerUtil.setSyncAdminController(baseNode);
        ExplorerUtil.setServerExplorer(this);
        
        explorerManager.setRootContext(baseNode);
        
        setDisplayName(name);
        setName(name);
        
    }

    // ---------------------------------------------------------- Public methods
    
    /**
     * Sets a new controller
     */
    public void setSyncAdminController(SyncAdminController controller) {
        explorerManager.setRootContext(controller);
    }
    /**
     * Returns this explorer manager
     *
     * @return this explorer manager
     */
    public ExplorerManager getExplorerManager() {
        return explorerManager;
    }

    /**
     * Returns composited view
     * @return view TreeView
     */
    public TreeView getTreeView() {
        return view;
    }

    /**
     * Returns root node
     * @return root node
     */
    public Node getRootContext() {
        return getExplorerManager().getRootContext();
    }
    
    // It is good idea to switch all listeners on and off when the
    // component is shown or hidden. In the case of TopComponent use:
    protected void componentActivated() {
        ExplorerUtils.activateActions(explorerManager, true);
    }
    
    protected void componentDeactivated() {
        ExplorerUtils.activateActions(explorerManager, false);
    }
    
    /** 
     * Overriden to explicitely set persistence type of this component
     * to PERSISTENCE_NEVER */
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }

    //----------------------------------------
    // Overrides javax.swing. method
    //----------------------------------------
    /**
     * Updated accessible description of the tree view
     * @param text String
     */
    public void setToolTipText(String text) {
        super.setToolTipText(text);
        if (view != null) {
            view.getAccessibleContext().setAccessibleDescription(text);
        }
    }

    //----------------------------------------
    // Overrides javax.swing. method
    //----------------------------------------
    /**
     * Checks if explorer panel can be close. We always return false, so that
     * the user cannot close it.
     *
     * @return always false
     */
    public boolean canClose() {
        return false;
    }

    // ------------------------------------------------------- Protected methods
    
    protected String preferredID() {
        return "server";
    }

    /**
     * Initializes gui of this component. Subclasses can override
     * this method to install their own gui.
     * @return Tree view that will serve as main view for this explorer.
     */
    protected TreeView initGui() {
        view = new BeanTreeView();

        setLayout(new BorderLayout());

        add(view, BorderLayout.CENTER);
        view.setRootVisible(true);

        return view;
    }

    //----------------------------------------
    // Overrides javax.swing. method
    //----------------------------------------
    /**
     * Initialize visual content of component
     */
    protected void componentShowing() {
        
        setDisplayName(name);
        setName(name);

        super.componentShowing();
        if (view == null) {
            initGui();
        }
    }

    //----------------------------------------
    // Overrides javax.swing. method
    //----------------------------------------
    /**
     * Sets the title to the name of the root context only
     */
    protected void updateTitle() {
        // set name by the root context
        setName(getExplorerManager().getRootContext().getDisplayName());
    }

    // --------------------------------------------------------- Private methods

}
