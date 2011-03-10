/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.server.plugin;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.plugin.Plugin;
import com.funambol.framework.server.plugin.PluginException;
import com.funambol.server.config.Configuration;

/**
 * Handler for plugins instance. It reads all the plugins from
 * com/funambol/server/plugin/xml and handles their life cycle.
 * @version $Id: PluginHandler.java,v 1.3 2008-06-05 07:41:59 nichele Exp $
 */
public class PluginHandler {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data
    /** Plugins map. Contain bean file name - plugin */
    private Map<String, Plugin> plugins = null;

    /** The directory that contains the plugin */
    private File pluginsDirectory = null;

    /** The the path of the plugin directory in the config tree */
    private String pluginsPath = null;

    // ------------------------------------------------------------- Constructor
    /**
     * Creates a new PluginHandler
     * @param pluginsDirectory directory the directory that contains the xml file to use in plugins
     *        initialization
     * @param pluginsPath the path of the plugin directory in the config tree
     */
    public PluginHandler(File pluginsDirectory, String pluginsPath) {
        if (logger.isTraceEnabled()){
            logger.trace("Creating a PluginHandler instance");
        }
        this.pluginsDirectory = pluginsDirectory;
        this.pluginsPath      = pluginsPath;
    }

    /** The logger */
    private FunambolLogger logger = FunambolLoggerFactory.getLogger("funambol.server.plugin");

    // ------------------------------------------------------------ Constructors

    /**
     * Discharges the plugin with the given bean file name
     * @param beanName the bean name of the plugin to discharge
     */
    public void dischargePlugin(String beanName) {
        Plugin plugin = plugins.get(beanName);
        if (plugin == null) {
            return;
        }
        stopPlugin(plugin);
        plugins.remove(beanName);
    }

    /**
     * Uses the given bean file to reload/load a plugin
     * @param beanFile the bean file
     */
    public void reloadPlugin(String beanFile) {
        Plugin plugin = plugins.get(beanFile);
        if (plugin == null) {
            //
            // There is not a plugin initialized with the same bean file
            //
        } else {
            //
            // Stopping the current plugin
            //
            stopPlugin(plugin);
            plugins.remove(beanFile);
        }
        plugin = initPlugin(beanFile);
        if (plugin != null) {
            startPlugin(plugin);
        }
    }

    /**
     * Starts all plugins
     */
    public void startPlugins() {
        initPlugins();

        if (plugins == null || plugins.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("No plugin to start");
            }
            return;
        }

        Collection<Plugin> pluginCollection = plugins.values();
        Iterator<Plugin> itPlugins = pluginCollection.iterator();
        while (itPlugins.hasNext()) {
            Plugin plugin = itPlugins.next();
            boolean started = startPlugin(plugin);
            if (!started) {
                itPlugins.remove();
            }

        }
    }

    /**
     * Stops all plugins
     */
    public void stopPlugins() {

        if (plugins == null || plugins.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("No plugin to stop");
            }
            return;
        }

        Collection<Plugin> pluginCollection = plugins.values();
        Iterator<Plugin> itPlugins = pluginCollection.iterator();
        while (itPlugins.hasNext()) {
            Plugin plugin = itPlugins.next();
            stopPlugin(plugin);
            itPlugins.remove();
        }

    }

    // --------------------------------------------------------- Private methods

    /**
     * Initializes the plugins
     */
    private void initPlugins() {

        plugins = new HashMap<String, Plugin>();

        if (pluginsDirectory.exists()) {
            if (logger.isTraceEnabled()) {
                logger.trace("Plugin directory '" + pluginsDirectory + "' not found");
            }
        }
        String[] beanFiles = pluginsDirectory.list();

        if (beanFiles == null || beanFiles.length == 0) {
            return;
        }

        for (int i=0; i<beanFiles.length; i++) {
            if (!beanFiles[i].toLowerCase().endsWith(".xml")) {
                //
                // No xml file
                //
                continue;
            }
            initPlugin(beanFiles[i]);
        }

    }

    /**
     * Initializes a plugin with the given bean file
     * @param beanFile the bean file
     * @return the initialized plugin
     */
    private Plugin initPlugin(String beanFile) {
        Plugin plugin = null;
        try {
            plugin = createPlugin(beanFile);
            if (plugin == null) {
                return null;
            }
            if (!plugin.isEnabled()) {
                if (logger.isInfoEnabled()) {
                    logger.info("Plugin '" + plugin + "' is disabled and it will be ignored");
                }
                return null;
            }
            if (logger.isTraceEnabled()) {
                logger.trace("Initializing plugin '" + beanFile + "': " + plugin);
            }
            plugins.put(beanFile, plugin);
        } catch (Exception e) {
            logger.error("Error creating plugin", e);
            //
            // Continuing with the other ones
            //
            return null;
        }
        return plugin;
    }

    /**
     * Stops the given plugin
     * @param plugin the plugin to stop
     * @return true if the pluing has been started without issue
     */
    private boolean startPlugin(Plugin plugin) {
        if (logger.isInfoEnabled()) {
            logger.info("Starting plugin: " + plugin);
        }
        try {
            plugin.start();
            return true;
        } catch (Exception ex) {
            logger.error("Error starting '" + plugin + "'. It will be removed from" +
                         "the list of the active plugins", ex);
        }
        return false;
    }

    /**
     * Stops the given plugin
     * @param plugin the plugin to stop
     */
    private void stopPlugin(Plugin plugin) {
        if (logger.isInfoEnabled()) {
            logger.info("Stopping plugin '" + plugin + "'");
        }
        try {
            plugin.stop();
        } catch (Exception ex) {
            logger.error("Error stopping '" + plugin + "'", ex);
        }
    }

    /**
     * Initializes a plugin reading the bean file with the given filename
     * @param beanFileName the file name
     * @throws PluginException if an error occurs
     * @return the plugin created reading the bean file with the given name
     */
    private Plugin createPlugin(String beanFileName) throws PluginException {
        try {
            Plugin plugin =
                (Plugin) Configuration.getConfiguration().getBeanInstanceByName(
                    pluginsPath + File.separator + beanFileName
                );

            return plugin;
        } catch (Exception ex) {
            throw new PluginException("Error creating plugin from '" +
                                       beanFileName + "'", ex);
        }
    }

}
