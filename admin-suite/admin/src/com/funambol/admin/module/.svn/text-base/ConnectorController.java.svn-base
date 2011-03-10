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
package com.funambol.admin.module;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.funambol.framework.server.Sync4jConnector;
import com.funambol.framework.server.Sync4jModule;

import com.funambol.admin.AdminException;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.main.SyncAdminController;
import com.funambol.admin.mo.ConnectorManagementObject;
import com.funambol.admin.module.nodes.ConnectorNode;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.ui.ConnectorManagementPanel;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.ErrorManager;
import com.funambol.admin.util.Log;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 * Manages all processing about Coonnectors.
 *
 * @version $Id: ConnectorController.java,v 1.9 2007-11-28 10:28:18 nichele Exp $
 */
public class ConnectorController 
implements ActionListener {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data
    private BusinessDelegate businessDelegate;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new SyncSourceControlles with the given <code>BusinessDelegate</code>
     * @param businessDelegate BusinessDelegate to access to the business methods.
     */
    public ConnectorController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Starts the process to create a new SyncSouce
     * @param node SyncTypeNode involved in the process
     * @throws AdminException if a error occurs during the process
     */
    public void editConnector(ConnectorNode node) throws AdminException {
        Sync4jModule    module    = node.getModule()   ;
        Sync4jConnector connector = node.getConnector();
        
        Log.debug("node:" + node);
        
        final String panelName = module.getModuleName()
                               + " - "
                               + connector.getConnectorName()
                               ;
        
        ConnectorManagementPanel panel = 
            (ConnectorManagementPanel)PanelManager.getPanel(panelName);
        
        ConnectorManagementObject mo = new ConnectorManagementObject(
                                            null, 
                                            module.getModuleId(), 
                                            connector.getConnectorId(), 
                                            connector.getConnectorName()
                                       );

        if (panel != null) {
            //
            // Management object loading and setting
            //
            readConfiguration(mo);
            
            panel.setManagementObject(mo);
            panel.updateForm();
            PanelManager.showPanel(panelName);
        } else {
            String adminClassName = connector.getAdminClass();
        
            Log.debug("admin class name: " + adminClassName);
            
            if ((adminClassName == null) || (adminClassName.trim().length() == 0)) {
                //
                // There is no configuration panel for this connector... just
                // do nothing
                //
                return;
            }
            
            //
            // Admin class loading
            //
            
            Class adminClass = null;
            
            SyncAdminController adminController = ExplorerUtil.getSyncAdminController();

            ClassLoader cl = adminController.getSync4jClassLoader();

            try {
                adminClass = cl.loadClass(adminClassName);
                panel = (ConnectorManagementPanel)adminClass.newInstance();
            } catch (Exception e) {
                String errorMessage = Bundle.getMessage(Bundle.ERROR_LOADING_CONNECTOR_CONFIG_PANEL);                
                Log.error(errorMessage + " (class name: " + adminClassName + ")");                 
                Log.error(e);
                NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                DialogDisplayer.getDefault().notify(desc);
                return;
            }
            
            //
            // Management object loading and setting
            //
            readConfiguration(mo);
            
            panel.setName(panelName);
            panel.setManagementObject(mo);
            panel.addActionListener(this);
            
            //
            // Tell the panel to rewrite its content
            //
            panel.updateForm();
            
            panel.validate();

            PanelManager.addPanel(panel);
        }
    }
    
    // ------------------------------------------- ActionListener implementation
    
    /**
     * Invoked when an action occurs.
     * 
     * @param e the ActionEvent
     */
     public void actionPerformed(ActionEvent event) {
         Object o = event.getSource();
         
         if ((o == null) || !(o instanceof ManagementObjectPanel)) {
             Log.error(Bundle.getMessage(Bundle.ERROR_INVALID_ACTION_EVENT));
             return;
         }         
         
         ConnectorManagementPanel p = (ConnectorManagementPanel)o;
         ConnectorManagementObject mo = (ConnectorManagementObject)p.getManagementObject();
         
         try {
             if (event.getID() == p.ACTION_EVENT_UPDATE) {
                 saveConfiguration(mo);
             }
         } catch (AdminException e) {
             p.notifyError(e);
         }
             
     }

     // -------------------------------------------------------- Private Methods
     
     /**
      * Reads the configuration object from the server. The server bean path is
      * the one returned by mo.getPath().
      *
      * @param mo the ManagementObject to read from the server
      */
     private void readConfiguration(ConnectorManagementObject mo) 
     throws AdminException {
         Log.debug("Reading configuration object " + mo.getPath());
         mo.setObject(businessDelegate.getServerBean(mo.getPath()));
     }
     
     /**
      * Stores the configuration object on the server. The server bean path is
      * the one returned by mo.getPath().
      *
      * @param mo the ManagementObject to read from the server
      */
     private void saveConfiguration(ConnectorManagementObject mo) 
     throws AdminException {
         Log.debug("Saving configuration object " + mo.getPath());
         businessDelegate.setServerBean(mo.getPath(), mo.getObject());
         Log.info("Configuration saved.");
     }
}
