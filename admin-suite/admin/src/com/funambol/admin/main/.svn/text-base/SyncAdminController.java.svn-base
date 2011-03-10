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

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Action;
import javax.swing.JButton;

import java.text.MessageFormat;

import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.actions.NodeAction;
import org.openide.util.actions.SystemAction;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import com.funambol.framework.core.DevInf;

import com.funambol.server.config.ServerConfiguration;
import com.funambol.server.update.Component;

import com.funambol.admin.UnauthorizedException;
import com.funambol.admin.actions.LoginAction;
import com.funambol.admin.config.AdminConfig;
import com.funambol.admin.device.DevicesController;
import com.funambol.admin.login.LoginPanel;
import com.funambol.admin.module.ConnectorController;
import com.funambol.admin.module.SyncSourcesController;
import com.funambol.admin.principal.PrincipalsController;
import com.funambol.admin.settings.ServerSettingsController;
import com.funambol.admin.ui.NodeTools;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.user.UsersController;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Represents the main controller of the application and the root node of the moe.
 * Manages the main process of the application (as the login process).
 *
 * @version $Id: SyncAdminController.java,v 1.12 2008-05-22 22:11:24 nichele Exp $
 */
public class SyncAdminController
extends AbstractNode implements ActionListener {

    // --------------------------------------------------------------- Constants
    private static final String COMMAND_OK_UPDATE_NOTIFICATION_PANEL = "OK_UPDATE_PANEL";

    // ------------------------------------------------------------ Private data

    /** The node correspondent to the server */
    private ServerNode serverNode = null;

    /** Server configuration */
    private AdminConfig config = null;

    /**
     * Controllers for users, principals, devices, sync sources, server settings
     * processing.
     */
    private UsersController          usersController          = null;
    private PrincipalsController     principalsController     = null;
    private DevicesController        devicesController        = null;
    private SyncSourcesController    syncSourcesController    = null;
    private ServerSettingsController serverSettingsController = null;
    private ConnectorController      connectorController      = null;

    /** BusinessDelegate to access the business methods */
    private BusinessDelegate businessDelegate = null;

    /** Login dialog */
    private Dialog loginDialog = null;
    private LoginPanel ld = null;

    /** DS Server Update notification panel/dialog */
    private Dialog updateNotificationDialog = null;
    private DSServerUpdateNotificationPanel updateNotificationPanel = null;

    /**
     * The class loader able to download classes from the network
     */
    private Sync4jClassLoader cl = null;

    private boolean isCarrierEditionServer = false;

    /**
     * Create a new SyncAdminController with the given name
     *
     * @param name the name to show
     */
    public SyncAdminController() {
        super(new Children.Array());
        setIconBaseWithExtension(Constants.ICON_SYNC_ADMIN_CONSOLE_NODE);
        setName(Constants.NODE_NAME_ROOT);
        setDisplayName(Constants.NODE_NAME_ROOT);

        // load admin configuration
        config = AdminConfig.getAdminConfig();

        initDialogPanel();

        initUpdateNotificationDialog();
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
        return new SystemAction[] {
            NodeAction.get(LoginAction.class)
        };
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

    //-------------------------------------------------------
    // Overrides org.openide.nodes.AbstractNode method
    //-------------------------------------------------------
    /**
     * Set action to perform on double click on this node
     * @return Action search action
     */
    public Action getPreferredAction() {
        return NodeAction.get(LoginAction.class);
    }

    /**
     * Add a server node to the moe.
     * @param name name of the server
     * @return ServerNode serverNode added
     */
    public ServerNode loadSyncServerChild(String name, boolean isCarrierEditionServer) {
        serverNode = new ServerNode(isCarrierEditionServer);
        serverNode.setDisplayName(name);

        Node[] nodes = new Node[] {serverNode};

        // if there are panels opened, close that
        // Uses this check for don't close the panels in startup
        if (this.getChildren().getNodes().length > 0) {
            closeAllPanel();
        }

        // remove the previous server node
        this.getChildren().remove(this.getChildren().getNodes());

        this.getChildren().add(nodes);
        return serverNode;
    }

    /**
     * Release the resources.
     */
    public void close() {
        closeAllPanel();
    }

    /**
     * Shows ds update notification dialog.
     */
    public void showUpdateNotificationDialog(String version) {
        updateNotificationPanel.deselectDontShowAgainTheMessage();
        updateNotificationPanel.setVersionToNotify(version);
        updateNotificationDialog.setVisible(true);
    }

    /**
     * Hiddens ds update notification dialog.
     */
    public void hideUpdateNotificationDialog() {
        updateNotificationDialog.setVisible(false);
    }

    /**
     * Shows the login panel.
     */
    public void showLoginDialog() {
        ld.loadValues();
        loginDialog.setVisible(true);
    }

    /**
     * Hiddens the login panel
     */
    public void hideLoginDialog() {
        loginDialog.setVisible(false);
    }

    /**
     * Starts the login process. <br/>
     * Release previous <code>BusinessDelegate</code> (if exits) and re-init
     * the <code>PanelManager</code>.<p>
     * During the login process, all actions of this node are disabled.
     */
    public void startLoginProcess() {

        PanelManager.init(null);

        NodeTools.disableAllAction(this);

        getChildren().remove(getChildren().getNodes());

        showLoginDialog();
    }

    /**
     * Start the login process after reading the <code>AdminConfig</code>
     */
    public void login() {
        AdminConfig config = AdminConfig.getAdminConfig();

        Log.info("Connecting to " + config.getHostName() + "...");

        hideLoginDialog();

        this.setDisplayName("connecting...");

        try {
            cl = new Sync4jClassLoader(config.getSync4jCodeBase(), getClass().getClassLoader());
        } catch (Exception e) {
            Log.error("Unable to create the remote class loader (" + e.getMessage() + ')');
        }

        connect();
    }

    /**
     * Hidden login panel and start the thread to connect to the server
     */
    public synchronized void connect() {

        loginDialog.setVisible(false);

        ConnectionThread cThread = new ConnectionThread();
        cThread.start();
    }

    /**
     * Called when the login process is termined with a error.
     *
     * @param e error occurs during the login process.
     */
    public void loginTermined(Exception e) {

        setDisplayName(Constants.NODE_NAME_ROOT);

        NodeTools.enableAllAction(this);

        String errorMessage = null;

        if (e instanceof com.funambol.server.admin.UnauthorizedException) {
            errorMessage = Bundle.getMessage(Bundle.AUTHENTICATION_FAILED_ERROR_MESSAGE);
        } else {
            errorMessage = Bundle.getMessage(Bundle.CONNECTION_ERROR_MESSAGE);
        }
        NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);

        DialogDisplayer.getDefault().notify(desc);

        showLoginDialog();
    }

    /**
     * Called when the login process is termined correctly.
     *
     * @param config HostConfiguration of the connected server
     */
    public void loginTermined(boolean isCarrierEditionServer) {
        Log.info("connected");

        setDisplayName(Constants.NODE_NAME_ROOT);
        NodeTools.enableAllAction(this);

        ServerNode sn = loadSyncServerChild(config.getHostName(), isCarrierEditionServer);
    }

    /**
     * Called to create main resource
     * (BusinessDelegate, UsersController, DevicesController, PrincipalsController, SyncSourcesController).
     */
    public void startLogin() {

        try {
            businessDelegate = new BusinessDelegate();
            businessDelegate.init(cl);
            businessDelegate.login();

            ServerConfiguration serverConfiguration = businessDelegate.getServerConfiguration();

            isCarrierEditionServer = isCarrierEditionServer(serverConfiguration);

            loginTermined(isCarrierEditionServer);

            usersController          = new UsersController         (businessDelegate);
            devicesController        = new DevicesController       (businessDelegate);
            principalsController     = new PrincipalsController    (businessDelegate);
            syncSourcesController    = new SyncSourcesController   (businessDelegate);
            serverSettingsController = new ServerSettingsController(businessDelegate);
            connectorController      = new ConnectorController     (businessDelegate);

            //
            // Checks if this version of the SyncAdmin is compatible with the
            // server
            //
            checkServerVersion(serverConfiguration);
            
            //
            // Check if there is an update of the ds server already detected by
            // the server
            //
            checkServerUpdate();
            
        } catch (Exception e) {
            loginTermined(e);
        }
    }

    /** Returns BusinessDelegate */
    public BusinessDelegate getBusinessDelegate() {
        return businessDelegate;
    }

    /** Returns UsersController */
    public UsersController getUsersController() {
        return usersController;
    }

    /** Returns PrincipalsController */
    public PrincipalsController getPrincipalsController() {
        return principalsController;
    }

    /** Returns DevicesController */
    public DevicesController getDevicesController() {
        return devicesController;
    }

    /** Returns SyncSourcesController */
    public SyncSourcesController getSyncSourcesController() {
        return syncSourcesController;
    }

    /** Returns ConnectorController */
    public ConnectorController getConnectorController() {
        return connectorController;
    }

    /**
     * Getter for property serverSettingsController.
     * @return Value of property serverSettingsController.
     */
    public ServerSettingsController getServerSettingsController() {
        return serverSettingsController;
    }

    /** Returns host name */
    public String getHostName() {
        return config.getHostName();
    }

    /**
     * Gets the remote class loader
     *
     * @return the remote class loader
     */
    public ClassLoader getSync4jClassLoader() {
        return cl;
    }


    //-----------------------------------------------
    // Overrides java.awt.event.ActionListener method
    //-----------------------------------------------
    /**
     * Perform action based on button pressed in the login dialog. Cases are:<br>
     * @param event event occurs
     */
    public void actionPerformed(ActionEvent event) {
        String command = event.getActionCommand();
        if (command.equals(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL))) {
            NodeTools.enableAllAction(this);
            hideLoginDialog();
        } else if (command.equals(Bundle.getMessage(Bundle.LABEL_BUTTON_LOGIN))) {
            try {
                ld.validateInput();
            } catch (IllegalArgumentException e) {
                NotifyDescriptor desc = new NotifyDescriptor.Message(e.getMessage());
                DialogDisplayer.getDefault().notify(desc);

                return;
            }
            ld.saveOptions();
            login();
            hideLoginDialog();
        } else if (command.equals(COMMAND_OK_UPDATE_NOTIFICATION_PANEL)) {
            boolean dontShowAgainTheMessage = updateNotificationPanel.isDontShowAgainTheMessageSelected();
            if (dontShowAgainTheMessage) {
                AdminConfig.getAdminConfig().setLatestNotifiedServerVersion(
                        updateNotificationPanel.getVersionToNotify()
                );
                AdminConfig.getAdminConfig().saveConfiguration();
            }
            hideUpdateNotificationDialog();
        }
    }

    // --------------------------------------------------------- Private methods
    /**
     * Init login dialog.
     */
    private void initDialogPanel() {
        ld = new LoginPanel();

        Object[] options = {
                           new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_LOGIN)),
                           new JButton(Bundle.getMessage(Bundle.LABEL_BUTTON_CANCEL))
        };

        DialogDescriptor dd =
            new DialogDescriptor(ld, Bundle.getMessage(Bundle.LOGIN_PANEL_TITLE),
                                 true, options, null,
                                 DialogDescriptor.DEFAULT_ALIGN, null, this);

        loginDialog = DialogDisplayer.getDefault().createDialog(dd);
        loginDialog.setResizable(false);
        loginDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                enableAllAction();
            }
        });
        ld.requestFocus();

    }

    /**
     * Init login dialog.
     */
    private void initUpdateNotificationDialog() {
        updateNotificationPanel = new DSServerUpdateNotificationPanel();

        JButton okButton = new JButton("OK");
        okButton.setActionCommand(COMMAND_OK_UPDATE_NOTIFICATION_PANEL);
        Object[] options = {okButton};

        DialogDescriptor dd =
            new DialogDescriptor(updateNotificationPanel,
                                 Bundle.getMessage(Bundle.UPDATE_NOTIFICATION_PANEL_TITLE),
                                 true, options, null,
                                 DialogDescriptor.DEFAULT_ALIGN, null, this);

        updateNotificationDialog = DialogDisplayer.getDefault().createDialog(dd);
        updateNotificationDialog.setResizable(false);
        updateNotificationDialog.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                enableAllAction();
            }
        });
        updateNotificationDialog.requestFocus();
    }

    /**
     * Enables the login
     */
    private void enableAllAction() {
        NodeTools.enableAllAction(this);
    }

    /**
     * Closes all <code>TopComponents</code> in the source mode.
     */
    private void closeAllPanel() {
        Mode mySourceMode = WindowManager.getDefault().findMode(Constants.MODE_NAME_SOURCE);

        if (mySourceMode != null) {
            TopComponent[] allTc = mySourceMode.getTopComponents();
            for (int i = 0; i < allTc.length; i++) {
                allTc[i].close();
            }
        }

    }

    /**
     * Checks if this SyncAdmin is compatible with the server. If not, a warning
     * is logged in the output panel.
     * @param serverConfiguration The ServerConfiguration
     */
    private void checkServerVersion(ServerConfiguration serverConfiguration) {
        try {
            DevInf devInf = serverConfiguration.getServerInfo();
            String serverVersion = devInf.getSwV();

            //
            // Removing the build number if the server version is composed by:
            // releaseMajor, releaseMinor and buildNumber
            //
            if (serverVersion != null) {
                int index = serverVersion.indexOf('.');
                index = serverVersion.indexOf('.', index + 1);
                if (index != -1) {
                    serverVersion = serverVersion.substring(0, index);
                }
            }

            if (Constants.SUPPORTED_SERVER_VERSIONS.indexOf(',' + serverVersion + ',') < 0)  {
                Log.info(
                    MessageFormat.format(
                        Bundle.getMessage(Bundle.WARNING_SERVER_VERSION),
                        new Object[] { serverVersion }
                    )
                );
            }

        } catch (Exception e) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
        }
    }

    /**
     * Verifies if the ServerConfiguration corresponds to a carrier edition ds-server.
     * @param serverConfiguration The ServerConfiguration
     * @return true if the ServerConfiguration corresponds to a carrier edition ds-server
     */
    public boolean isCarrierEditionServer(ServerConfiguration serverConfiguration) {

        DevInf devInf = serverConfiguration.getServerInfo();
        String serverModel = devInf.getMod();

        if (serverModel.indexOf(Constants.CARRIER_EDITION_MODEL_FOOTPRINT) >=0 ) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * @return true if the ServerConfiguration corresponds to a carrier edition
     *              ds-server, false otherwise.
     */
    public boolean isCarrierEditionServer() {
        return this.isCarrierEditionServer;
    }

    /**
     * Checks if there is a ds server update already detected by the server
     */
    private void checkServerUpdate() {
        Component dsServerUpdate = null;
        try {

            dsServerUpdate = businessDelegate.getLatestDSServerUpdate();
            if (dsServerUpdate != null) {
                if (!AdminConfig.getAdminConfig()
                                .getLatestNotifiedServerVersion().equals(dsServerUpdate.getVersion())) {
                    showUpdateNotificationDialog(dsServerUpdate.getVersion());
                }
            }
        } catch (Exception ex) {
            Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), ex);
        }

    }
    
    /**
     * Shows all the server children
     */
    public void showAllNodes() {
        if (serverNode == null) {
            return;
        }
        ServerChildren children = (ServerChildren)serverNode.getChildren();
        children.showAllNodes();
    }

    /**
     * Thread used for login process.
     */
    private class ConnectionThread extends Thread {

        public void run() {
            startLogin();
        }
    }
}
