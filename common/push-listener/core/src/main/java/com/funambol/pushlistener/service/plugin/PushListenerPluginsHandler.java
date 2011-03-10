/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.pushlistener.service.plugin;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.beans.BeanTool;
import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.PushListenerInterface;

import com.funambol.pushlistener.framework.plugin.PushListenerPlugin;
import com.funambol.pushlistener.service.config.PushListenerConfiguration;

/**
 * This class handles the pushlistener plugins initializing them reading the bean
 * files from the configured <code>pluginBeanFilesDirectory</code> (see PushListenerConfigBean)
 * and handling their life cycle.
 *
 * @version $Id: PushListenerPluginsHandler.java,v 1.4 2007-11-28 11:15:03 nichele Exp $
 */
public class PushListenerPluginsHandler {

    // --------------------------------------------------------------- Constants
    // ------------------------------------------------------------ Private date

    /** The PushListenerInterface */
    private PushListenerInterface pushListenerInterface = null;

    /** The configPath (used to read the plugin bean files) */
    private String configPath = null;

    /** The directory where to read the plugin bean files */
    private String pluginBeanFilesDirectory    = null;

    /** Plugins list */
    private List<PushListenerPlugin> plugins = null;

    /** The logger */
    private Logger logger = Logger.getLogger("funambol.pushlistener.pluginshandler");

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new PushListenerPluginsHandler with the given PushListener and
     * PushListenerInterface
     *
     * @param pushListenerInterface the PushListenerInterface instance
     */
    public PushListenerPluginsHandler(PushListenerInterface pushListenerInterface) {

        if (logger.isInfoEnabled()){
            logger.info("Creating a PushListenerPluginsHandler instance");
        }
        this.pushListenerInterface = pushListenerInterface;
        this.configPath = PushListenerConfiguration.getConfigPath();

        this.pluginBeanFilesDirectory =
                PushListenerConfiguration.getPushListenerConfigBean().getPluginDirectory();

        this.pushListenerInterface = pushListenerInterface;
    }

    /**
     * Notifies all plugins that the push listener is starting
     */
    public void notifyStartPushListener() {
        List<PushListenerPlugin> pluginsToDisable = new ArrayList<PushListenerPlugin>();
        for (PushListenerPlugin plugin : plugins) {
            try {
                plugin.startPushListener();
            } catch (PushListenerException ex) {
                logger.error("Error calling startPushListener on '" + plugin + "'. It will be removed from " +
                             "the list of the active plugins", ex);
                pluginsToDisable.add(plugin);
            }
        }
        plugins.removeAll(pluginsToDisable);
    }

    /**
     * Starts all plugins
     */
    public void startPushListenerPlugins() {
        if (plugins == null || plugins.size() == 0) {
            if (logger.isInfoEnabled()) {
                logger.info("No plugin to start");
            }            
        }
        List<PushListenerPlugin> pluginsToDisable = new ArrayList<PushListenerPlugin>();
        for (PushListenerPlugin plugin : plugins) {
            if (logger.isInfoEnabled()) {
                logger.info("Starting plugin: " + plugin);
            }
            try {
                plugin.startPlugin();
            } catch (PushListenerException ex) {
                logger.error("Error starting '" + plugin + "'. It will be removed from" +
                             "the list of the active plugins", ex);
                pluginsToDisable.add(plugin);
            }
        }
        plugins.removeAll(pluginsToDisable);
    }

    /**
     * Stops all plugins
     */
    public void stopPushListenerPlugins() {
        if (plugins == null || plugins.size() == 0) {
            if (logger.isInfoEnabled()) {
                logger.info("No plugin to stop");
            }            
        }
        
        List<PushListenerPlugin> pluginsToDisable = new ArrayList<PushListenerPlugin>();
        for (PushListenerPlugin plugin : plugins) {
            if (logger.isInfoEnabled()) {
                logger.info("Stopping plugin '" + plugin + "'");
            }
            try {
                plugin.stopPlugin();
            } catch (PushListenerException ex) {
                logger.error("Error stopping '" + plugin + "'", ex);
                pluginsToDisable.add(plugin);
            }
        }
        plugins.removeAll(pluginsToDisable);
    }

    /**
     * Notifies all plugins that the push listener is shutting down
     */
    public void notifyShutdownPushListener() {
        for (PushListenerPlugin plugin : plugins) {
            try {
                plugin.shutdownPushListener();
            } catch (PushListenerException ex) {
                logger.error("Error calling shutdownPushListener on '" + plugin + "'",
                             ex);
            }
        }
    }

    /**
     * Initializes the plugins
     * @throws PushListenerException if an error occurs
     */
    public void initPushListenerPlugins() throws PushListenerException {

        plugins = new ArrayList<PushListenerPlugin>();

        String[] beanFiles =
                BeanTool.getBeanTool(configPath).getBeanNames(pluginBeanFilesDirectory);

        if (beanFiles == null || beanFiles.length == 0) {
            if (logger.isInfoEnabled()) {
                logger.info("No PushListenerPlugin");
            }
            return;
        }

        for (int i=0; i<beanFiles.length; i++) {
            PushListenerPlugin plugin = createPlugin(beanFiles[i]);
            if (!plugin.isEnabled()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Plugin '" + beanFiles[i] + "' is disabled and it will be ignored");
                }
                continue;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Initializing plugin '" + beanFiles[i] + "': " + plugin);
            }
            plugin.setPushListenerInterface(pushListenerInterface);
            plugins.add(plugin);
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates a plugin reading the bean file with the given filename
     * @param beanFileName the file name
     * @throws PushListenerException if an error occurs
     * @return the plugin created reading the bean file with the given name
     */
    private PushListenerPlugin createPlugin(String beanFileName) throws PushListenerException {
        try {
            PushListenerPlugin plugin = null;
            plugin =
                (PushListenerPlugin) BeanTool.getBeanTool(configPath).getNewBeanInstance(beanFileName);

            return plugin;
        } catch (Exception ex) {
            throw new PushListenerException("Error creating a PushListenerPlugin from '" +
                                            beanFileName + "'", ex);
        }
    }
}
