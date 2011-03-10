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
package com.funambol.admin.settings;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.RollingFileAppender;

import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

import com.funambol.framework.config.LoggerConfiguration;
import com.funambol.framework.config.LoggingConfiguration;

import com.funambol.server.config.ServerConfiguration;

import com.funambol.admin.AdminException;
import com.funambol.admin.main.BusinessDelegate;
import com.funambol.admin.mo.ManagementObject;
import com.funambol.admin.settings.nodes.AppenderSettingsNode;
import com.funambol.admin.settings.nodes.LoggerSettingsNode;
import com.funambol.admin.settings.nodes.ServerSettingsNode;
import com.funambol.admin.settings.panels.DataTransformerManagerPanel;
import com.funambol.admin.settings.panels.DefaultAppenderConfigPanel;
import com.funambol.admin.settings.panels.EditLoggerPanel;
import com.funambol.admin.settings.panels.EditServerConfigurationPanel;
import com.funambol.admin.settings.panels.Sync4jStrategyPanel;
import com.funambol.admin.ui.ExplorerUtil;
import com.funambol.admin.ui.ManagementObjectPanel;
import com.funambol.admin.ui.PanelManager;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Log;

/**
 * Manages all tasks related to server settings.
 *
 * @version $Id: ServerSettingsController.java,v 1.11 2007-11-28 10:28:19 nichele Exp $
 */
public class ServerSettingsController implements Constants, ActionListener {

    // ------------------------------------------------------- Private Constants
    private static final String EDITING_PANEL_CONSOLE_APPENDER
        = "com.funambol.admin.settings.panels.EditConsoleAppender";

    private static final String EDITING_PANEL_ROLLING_FILE_APPENDER
        = "com.funambol.admin.settings.panels.EditRollingFileAppender";

    private static final String EDITING_PANEL_DAILY_ROLLING_FILE_APPENDER
        = "com.funambol.admin.settings.panels.EditDailyRollingFileAppender";

    // ------------------------------------------------------------ Private data

    private BusinessDelegate businessDelegate = null;

    // ---------------------------------------------------------- Constructors
    /**
     * Creates a new <code>UsersController</code>.
     */
    public ServerSettingsController(BusinessDelegate businessDelegate) {
        this.businessDelegate = businessDelegate;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the logging configuration in a LoggingConfiguration object
     *
     * @return the logging configuration in a LoggingConfiguration object
     *
     * @throws AdminException if a error occurs during the logging configuration
     *                        retrieving
     */
    public LoggingConfiguration getLoggingSettings()
    throws AdminException {

        Log.debug("Starting getLoggingSettings");

        LoggingConfiguration config = businessDelegate.getLoggingSettings();

        Log.debug("Logging configuration: " + config);

        return config;
    }

    /**
     * Returns the available logger configuration objects on the server
     *
     * @return the available logger configuration objects on the server
     *
     * @throws AdminException if a error occurs during the loggers configuration
     *                        retrieving
     */
    public LoggerConfiguration[] getLoggers()
    throws AdminException {

        Log.debug("Starting getLoggers");

        LoggerConfiguration[] loggers = businessDelegate.getLoggers();

        Log.debug("Retrieved " + loggers.length + " loggers");

        return loggers;
    }

    /**
     * Returns the available appenders on the server
     *
     * @return the available appenders on the server
     *
     * @throws AdminException if a error occurs during the appenders retrieving
     */
    public Map getAppenders()
    throws AdminException {

        Log.debug("Starting getAppenders");

        Map appenders = businessDelegate.getAppenders();

        Log.debug("Retrieved " + appenders.size() + " appenders");

        return appenders;
    }

    /**
     * Returns a sorted list of the appender names
     * @return a sorted list of the appender names
     *
     */
    public List getAppenderNames() throws AdminException {
        Map appenders = getAppenders();
        List appenderNames = new ArrayList(appenders.size());
        Iterator itAppenders =  appenders.values().iterator();
        while (itAppenders.hasNext()) {
            appenderNames.add(((Appender)itAppenders.next()).getName());
        }
        Collections.sort(appenderNames);
        return appenderNames;
    }

    /**
     * Saves the given logging configuration
     *
     * @param config the LoggingConfiguration object
     *
     * @throws AdminException if a error occurs during the logging configuration
     *                        retrieving
     */
    public void setLoggingSettings(LoggingConfiguration config)
    throws AdminException {

        Log.debug("Starting setLoggingSettings");

        businessDelegate.setLoggingSettings(config);
    }

    /**
     * Saves the given logger configuration
     *
     * @param config the LoggerConfiguration object
     *
     * @throws AdminException if a error occurs saving the logger configuration
     *
     */
    public void setLoggerConfiguration(LoggerConfiguration config)
    throws AdminException {

        Log.debug("Starting setLoggerSettings");

        businessDelegate.setLoggerConfiguration(config);

    }

    /**
     * Returns the server configuration in a ServerConfiguration object
     *
     * @return the server configuration in a ServerConfiguration object
     *
     * @throws AdminException if a error occurs during the server configuration
     *                        retrieving
     */
    public ServerConfiguration getServerConfiguration()
    throws AdminException {

        Log.debug("Starting getServerConfiguration");

        ServerConfiguration config = businessDelegate.getServerConfiguration();

        Log.debug("Server configuration: " + config);

        return config;
    }

    /**
     *  Reads the dataTransformer configuration from server
     *
     *  @param mo management object in wich the configuration will be put
     *
     *  @throws AdminException if a error occurs during the server configuration
     *                         retrieving
     */
    public void readDataTransformerConfiguration(ManagementObject mo)
     throws AdminException {

         Log.debug("Reading dataTransformerManager configuration object " + mo.getPath());

         mo.setObject(businessDelegate.getServerBean(mo.getPath()));

         Log.debug("DataTransformerManager configuration object read successfuly");
     }

    /**
     *  Saves the dataTransformer configuration to server
     *
     *  @param mo management object containing the dataTransformerManager configuration and path
     *
     *  @throws AdminException if a error occurs during the server configuration
     *                         retrieving
     */
    public void setDataTransformerConfiguration(ManagementObject mo)
     throws AdminException {

         Log.debug("Saving dataTransformerManager configuration object " + mo.getPath());

         businessDelegate.setServerBean(mo.getPath(),mo.getObject());

         Log.debug("DataTransformerManager Configuration object saved successfully");
     }

    /**
     *  Reads the Sync4jStrategy configuration from server
     *
     *  @param mo management object in wich the configuration will be put
     *
     *  @throws AdminException if a error occurs during the server configuration
     *                         retrieving
     */
    public void readStrategyConfiguration(ManagementObject mo)
     throws AdminException {

         Log.debug("Reading Sync4jStrategy configuration object " + mo.getPath());

         mo.setObject(businessDelegate.getServerBean(mo.getPath()));

         Log.debug("Sync4jStrategy configuration object read successfuly");
     }

    /**
     *  Saves the dataTransformer configuration to server
     *
     *  @param mo management object containing the dataTransformerManager configuration and path
     *
     *  @throws AdminException if a error occurs during the server configuration
     *                         retrieving
     */
    public void setStrategyConfiguration(ManagementObject mo)
     throws AdminException {

         Log.debug("Saving Sync4jStrategy configuration object " + mo.getPath());

         businessDelegate.setServerBean(mo.getPath(),mo.getObject());

         Log.debug("Sync4jStrategy Configuration object saved successfuly");
     }

    /**
     * Saves the given server configuration
     *
     * @param config the ServerConfiguration object
     *
     * @throws AdminException if a error occurs during the server configuration
     *                        setting
     */
    public void setServerConfiguration(ServerConfiguration config)
    throws AdminException {

        Log.debug("Starting setServerConfiguration");

        businessDelegate.setServerConfiguration(config);
    }


    /**
     * Starts the process of edititing a logger
     *
     * @param config the node associated to the LoggingConfiguration object to
     *               edit
     *
     * @throws AdminException if a error occurs during start the edit process
     */
    public void startLoggerEditing(LoggerSettingsNode config)
    throws AdminException {
        //
        // If the panel was already created, reuse the old copy. Otherwise,
        // create a new panel.
        //
        EditLoggerPanel panel =
        (EditLoggerPanel)PanelManager.getPanel(
        Bundle.getMessage(Bundle.EDIT_LOGGER_PANEL_NAME));

        if (panel != null) {
            panel.setAvailableAppenders(getAppenderNames());
            panel.loadSettings(config);
            PanelManager.showPanel(Bundle.getMessage(Bundle.EDIT_LOGGER_PANEL_NAME));
        } else {
            panel = new EditLoggerPanel();
            panel.setAvailableAppenders(getAppenderNames());
            panel.loadSettings(config);
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }


    /**
     * Starts the process of edititing an appender
     *
     * @param config the node associated to the appender object to edit
     *
     * @throws AdminException if a error occurs during start the edit process
     */
    public void startAppenderEditing(AppenderSettingsNode config)
    throws AdminException {

        ClassLoader cl = ExplorerUtil.getSyncAdminController()
                                     .getSync4jClassLoader();

        Appender appender          = config.getAppender();
        String   appenderClassName = appender.getName();
        String   panelName         = appenderClassName;

        ManagementObjectPanel panel = null;

        ManagementObject mo = new ManagementObject(appender, "");

        Class managementPanelClass = null;

        String managementPanelClassName =
            businessDelegate.getAppenderManagementPanel(appender.getClass().getName());
        Log.debug("ManagementPanelClassName: " + managementPanelClassName);

        if (managementPanelClassName == null) {
            //
            // Trying loading appenderClass + "ManagementPanel"
            // Example: If the appender is a xxx.yyy.MyAppender, try to load
            // xxx.yyy.MyAppenderManagementPanel
            //
            managementPanelClassName = appender.getClass().getName() + "ManagementPanel";
            try {
                Log.debug("Trying to load: " + managementPanelClassName);
                managementPanelClass = cl.loadClass(managementPanelClassName);
                panel = (ManagementObjectPanel)managementPanelClass.newInstance();
            } catch (Exception e) {
                managementPanelClassName = null;
            }
        }
        if (managementPanelClassName == null) {
            //
            // No managementPanelClassName specified on the server.
            // If the appender is a ConsoleAppender or a RollingFileAppender,
            // the default one is used
            //
            if (appender instanceof ConsoleAppender) {
                managementPanelClassName = EDITING_PANEL_CONSOLE_APPENDER;
                Log.debug("Using built-in management panel: " + managementPanelClassName);
                //
                // The default panel is not obtained from the server so the
                // local classloader is used
                //
                cl = this.getClass().getClassLoader();
            } else if (appender instanceof RollingFileAppender) {
                managementPanelClassName =
                    EDITING_PANEL_ROLLING_FILE_APPENDER;
                Log.debug("Using built-in management panel: " + managementPanelClassName);
                //
                // The default panel is not obtained from the server so the
                // local classloader is used
                //
                cl = this.getClass().getClassLoader();
            } else if (appender instanceof DailyRollingFileAppender) {
                managementPanelClassName =
                    EDITING_PANEL_DAILY_ROLLING_FILE_APPENDER;
                Log.debug("Using built-in management panel: " + managementPanelClassName);
                //
                // The default panel is not obtained from the server so the
                // local classloader is used
                //
                cl = this.getClass().getClassLoader();
            } else {
                panel = (ManagementObjectPanel)PanelManager.getPanel(panelName);
                if (panel != null) {
                    PanelManager.showPanel(panelName);
                } else {
                    panel = new DefaultAppenderConfigPanel();
                    panel.setName(panelName);
                }
            }
        }

        if (managementPanelClassName != null) {
            try {
                managementPanelClass = cl.loadClass(managementPanelClassName);
                panel = (ManagementObjectPanel)managementPanelClass.newInstance();
            } catch (Exception e) {
                String errorMessage = Bundle.getMessage(Bundle.ERROR_LOADING_APPENDER_CONFIG_PANEL);
                Log.error(errorMessage + " (class name:" + managementPanelClassName + ")");
                Log.error(e);
                NotifyDescriptor desc = new NotifyDescriptor.Message(errorMessage);
                DialogDisplayer.getDefault().notify(desc);
                return;
            }
        }

        Log.debug("ManagementPanel: " + panel);

        panel.setManagementObject(mo);
        panel.setName(panelName);
        panel.addActionListener(this);
        panel.updateForm();
        panel.validate();

        PanelManager.addPanel(panel);
    }

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

         ManagementObjectPanel panel = (ManagementObjectPanel)o;
         Object managementObject = panel.getManagementObject().getObject();

         try {
             switch (event.getID()) {
                 case ManagementObjectPanel.ACTION_EVENT_INSERT:

                     break;

                 case ManagementObjectPanel.ACTION_EVENT_UPDATE:
                     if (managementObject instanceof Appender) {
                         //
                         // We are updating an Appender
                         //
                         businessDelegate.setAppender((Appender)managementObject);
                     }

                     break;

                 case ManagementObjectPanel.ACTION_EVENT_DELETE:
                     break;
             }
         } catch (AdminException e) {
             panel.notifyError(e);
         }

     }

    /**
     * Removes all logger panels.
     */
    public void removeLoggerPanels() {
        JPanel p = null;

        while((p = PanelManager.getPanel(Bundle.getMessage(Bundle.EDIT_LOGGER_PANEL_NAME))) != null) {
            PanelManager.removePanel(p);
        }
    }


    /**
     * Starts the process of edititing the server configuration
     *
     * @param config the node associated to the ServerConfiguration object to
     *               edit
     *
     * @throws AdminException if a error occurs during start the edit process
     */
    public void startServerConfigurationEditing(ServerSettingsNode node)
        throws AdminException {

        if (node.getServerConfiguration() == null) {
            node.setServerConfiguration(getServerConfiguration());
        }

        //
        // If the panel was already created, reuse the old copy. Otherwise,
        // create a new panel.
        //
        EditServerConfigurationPanel panel =
        (EditServerConfigurationPanel)PanelManager.getPanel(
        Bundle.getMessage(Bundle.EDIT_SERVER_CONFIGURATION_PANEL_NAME));

        if (panel != null) {
            panel.loadSettings(node.getServerConfiguration());
            PanelManager.showPanel(Bundle.getMessage(Bundle.EDIT_SERVER_CONFIGURATION_PANEL_NAME));
        } else {
            panel = new EditServerConfigurationPanel();
            panel.loadSettings(node.getServerConfiguration());
            panel.validate();
            PanelManager.addPanel(panel);
        }
    }

    /**
     * Reload server configuration from the server and start the editing of it
     * @param node ServerConfigurationNode
     * @throws AdminException
     */
    public void refreshServerConfiguration(ServerSettingsNode node)
        throws AdminException {

        node.setServerConfiguration(getServerConfiguration());

        startServerConfigurationEditing(node);
    }

    /**
     *  Reads from server current data transformer configuration
     *  and opens the screen to edit this configuration
     */
    public void editDataTrasnformerConfiguration(String path)
        throws AdminException{
        Log.debug("editDataTransformerConfiguration");

        //
        // First of all, perform input validation
        //
        if(path.equals("")) {
            String msg = Bundle.getMessage(Bundle.ERROR_GETTING_DTM_CONFIGURATION);

            Log.error(msg);
            return;
        }

        try {
            ManagementObject mo=new ManagementObject(null,path);
            ExplorerUtil.getSyncAdminController()
                .getServerSettingsController()
                .readDataTransformerConfiguration(mo);

            //open the panel for displaying the properties of data transformers
            Log.debug("Start edit data transformers properties");


            DataTransformerManagerPanel panel=(DataTransformerManagerPanel)
                PanelManager.getPanel(Bundle.getMessage(
                    Bundle.DATA_TRANSFORMERS_MANAGER_PANEL_NAME));

            if (panel != null) {
                panel.setManagementObject(mo);
                panel.updateForm();
                panel.validate();
                PanelManager.showPanel(Bundle.getMessage(
                        Bundle.DATA_TRANSFORMERS_MANAGER_PANEL_NAME));
            } else {
                panel = new DataTransformerManagerPanel();
                panel.setName(Bundle.getMessage(
                        Bundle.DATA_TRANSFORMERS_MANAGER_PANEL_NAME));
                panel.setManagementObject(mo);
                panel.updateForm();
                panel.validate();
                PanelManager.addPanel(panel);
            }
        } catch (AdminException e) {
            throw e;
        }
    }

    /**
     *  Reads from server current Sync4jStrategy configuration
     *  and opens the screen to edit this configuration
     */
    public void editStrategyConfiguration(String path)
        throws AdminException{
        Log.debug("editStrategyConfiguration");

        //
        // First of all, perform input validation
        //
        if(path.equals("")) {
            String msg = Bundle.getMessage(Bundle.ERROR_GETTING_STRATEGY_CONFIGURATION);

            Log.error(msg);
            return;
        }

        try {
            ManagementObject mo=new ManagementObject(null,path);
            ExplorerUtil.getSyncAdminController()
                .getServerSettingsController()
                .readDataTransformerConfiguration(mo);

            //open the panel for displaying the properties of Sync4jStrategy
            Log.debug("Start edit Sync4jStrategy properties");


            Sync4jStrategyPanel panel=(Sync4jStrategyPanel)
                PanelManager.getPanel(
                    Bundle.getMessage(Bundle.SYNC4JSTRATEGY_PANEL_NAME)
                );

            if (panel != null) {
                panel.setManagementObject(mo);
                panel.updateForm();
                panel.validate();
                PanelManager.showPanel(Bundle.getMessage(
                        Bundle.SYNC4JSTRATEGY_PANEL_NAME));
            } else {
                panel = new Sync4jStrategyPanel();
                panel.setName(Bundle.getMessage(
                        Bundle.SYNC4JSTRATEGY_PANEL_NAME));
                panel.setManagementObject(mo);
                panel.updateForm();
                panel.validate();
                PanelManager.addPanel(panel);
            }
        } catch (AdminException e) {
            throw e;
        }
    }

    // --------------------------------------------------------- Private Methods




}
