/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.server.config;

import java.io.File;
import java.io.IOException;

import java.net.URL;

import java.lang.management.ManagementFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Appender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.OptionHandler;

import com.funambol.framework.config.ConfigClassLoader;
import com.funambol.framework.config.ConfigurationException;
import com.funambol.framework.config.LoggerConfiguration;
import com.funambol.framework.config.LoggingConfiguration;

import com.funambol.framework.engine.SyncEngine;
import com.funambol.framework.engine.SyncStrategy;
import com.funambol.framework.engine.pipeline.InputMessageProcessor;
import com.funambol.framework.engine.pipeline.OutputMessageProcessor;
import com.funambol.framework.engine.pipeline.PipelineManager;
import com.funambol.framework.engine.transformer.DataTransformerManager;

import com.funambol.framework.logging.AppenderType;
import com.funambol.framework.logging.AppendersCache;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.framework.security.Officer;

import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.store.PersistentStore;

import com.funambol.framework.sms.SMSService;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;

import com.funambol.server.admin.UserManager;
import com.funambol.server.notification.PushManager;
import com.funambol.server.plugin.PluginHandler;
import com.funambol.server.session.SessionHandler;
import com.funambol.server.store.PersistentStoreManager;
import com.funambol.server.tools.directorymonitor.DirectoryMonitor;
import com.funambol.server.tools.directorymonitor.FileChangeEvent;
import com.funambol.server.tools.directorymonitor.FileChangeListener;
import com.funambol.server.update.UpdateDiscovery;

/**
 * Incapsulates all the configuration information about the Sync4j Engine.
 * Configuration parameters are stored as properties in a Map structure. The
 * key is the name of the parameter and it is strucured in dotted separated
 * namespaces. The value of the parameter is the value of the key and can be of
 * any type. Accessor methods are provided to get the value of the parameter in
 * a particular type (such as String, int, double, boolean, ...).<br>
 * Access to getXXX() and setXXX() methods are synchronized.
 * <p>
 * This class follows the Singleton pattern, therefore it cannot be directly
 * instantiated; an internal instance is created just once and returned at any
 * getConfiguration() call.
 *
 * @version $Id: Configuration.java,v 1.10 2008-06-30 14:42:58 nichele Exp $
 */
public class Configuration
implements java.io.Serializable, ConfigurationConstants, FileChangeListener {

    // --------------------------------------------------------------- Constants

    /** The logging directory */
    public static final File DIRECTORY_LOGGING =
        new File(getConfigPath() + File.separator + PATH_LOGGING);

    /** The loggers directory */
    public static final File DIRECTORY_LOGGER =
        new File(getConfigPath() + File.separator + PATH_LOGGER);

    /** The appenders directory */
    public static final File DIRECTORY_APPENDER =
        new File(getConfigPath() + File.separator + PATH_APPENDER);

    /** The persistent stores directory */
    public static final File DIRECTORY_PERSISTENT_STORE =
        new File(getConfigPath() + File.separator + PATH_PERSISTENT_STORE);

    /** The input synclets directory */
    public static final File DIRECTORY_SYNCLET_INPUT =
        new File(getConfigPath() + File.separator + PATH_SYNCLET_INPUT);

   /** The output synclets directory */
    public static final File DIRECTORY_SYNCLET_OUTPUT =
        new File(getConfigPath() + File.separator + PATH_SYNCLET_OUTPUT);

   /** The plugins directory */
    public static final File DIRECTORY_PLUGINS =
        new File(getConfigPath() + File.separator + PATH_PLUGIN);

    private static final boolean DEBUG_MODE =
        Boolean.parseBoolean(System.getProperty(PROPERTY_SYSTEM_FUNAMBOL_DEBUG, "false"));

    /**
     * This property is used to avoid exceptions in all getXXX methods (like
     * getUserManager or getSessionHandler) since otherwise the Configuration
     * object can not be used in junit test cases.
     */
    private static final boolean QUIET_INITIALIZATION =
        Boolean.parseBoolean(System.getProperty("funambol.configuration.quiet-initialization", "false"));

    // ------------------------------------------------------------ Private data

    /**
     * The server configuration
     */
    private static Configuration singleton;

    /** The configured PersistentStoreManager */
    private PersistentStoreManager persistentStoreManager = null;

    /** The configured PipelineManager */
    private PipelineManager pipelineManager = null;

    /** The logger */
    private static transient FunambolLogger log =
        FunambolLoggerFactory.getLogger("configuration");

    /** The monitor of the configuration directory  */
    private DirectoryMonitor directoryMonitor = null;

    /** The update checker */
    private UpdateDiscovery updateDiscovery = null;

    /** The PluginHandler */
    private PluginHandler pluginHandler = null;

    /** The PushManager */
    private PushManager pushManager = null;

    /** The BeanTool to use to read configuation bean */
    private BeanTool beanTool = null;

    // -------------------------------------------------------------- properties
    
    /** A class loader for the configuration directory */
    private transient ClassLoader classLoader = null;

    /**
     * Getter for property classLoader.
     * @return Value of property classLoader.
     */
    public ClassLoader getClassLoader() {

        return classLoader;
    }

    /**
     * Setter for property classLoader.
     * @param classLoader New value of property classLoader.
     */
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    /**
     * Boolean used to indicate if the engine components are initialized. The
     * engine components are the components (e.g. UpdateDiscovery) handled by the
     * Configuration that must be initialized only if the Configuration instance
     * is used in the engine (in the ds-server application)
     * (bear in mind that the webdemo and the ds-server are two different
     * (web)applications and are handled by two different classloaders that
     * means we have two instances of the Configuration).
     *
     * The check for updates for instance must not be performed if the Configuration
     * is instantiated by the webdemo; that means that the engine components initialization
     * must be performed (or started) by a component that is run only in the
     * ds-server application...like the Sync4jServlet (see bootstrap method)
     *
     * Same think about PipeLineManager that should be initialized only during
     * engine components initialization.
     */
    private boolean engineComponentsInitialized = false;

    // ------------------------------------------------------------ Constructors

    /**
     * This is disabled for normal use since this class is a singleton.
     */
    protected Configuration() {

        String configPath = getConfigPath();

        directoryMonitor = new DirectoryMonitor(configPath,
                                                DIRECTORY_MONITOR_SCAN_PERIOD);

        directoryMonitor.addFileChangeListener(this);

        DIRECTORY_LOGGER.mkdirs();
        DIRECTORY_SYNCLET_INPUT.mkdirs();
        DIRECTORY_SYNCLET_OUTPUT.mkdirs();
    }

    /**
     * Returns the singleton instance. It created the instance the first time it
     * is called.
     *
     * @return the singleton instance.
     */
    public static synchronized Configuration getConfiguration() {

        if (singleton == null) {
            singleton = new Configuration();

            try {
                singleton.beanTool = BeanTool.getBeanTool(getConfigPath());
                URL configPath = new File(getConfigPath()).toURL();
                ConfigClassLoader cl         =
                    new ConfigClassLoader(new URL[] {configPath},
                                          Configuration.class.getClassLoader());

                singleton.setClassLoader(cl);
                singleton.configurePersistentStoreManager();
            } catch (Exception e) {

                String msg = "Fatal error creating the configuration object";
                log.fatal(msg, e);
                if (!QUIET_INITIALIZATION) {
                    throw new ConfigurationException("Unable to initialize the Configuration object", e);
                }
            }
            singleton.enableDirectoryMonitor();
        }

        return singleton;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Initializes the engine components. At the moment, just the UpdateDiscovery
     * is initialized.
     * @throws ConfigurationException if an error occurs
     */
    public synchronized void initializeEngineComponents()
    throws ConfigurationException {

        if (this.engineComponentsInitialized) {
            //
            // The engine components are already initialized
            //
            return;
        }
        this.engineComponentsInitialized = true;

        try {
            singleton.configureLogging();
        } catch (Exception e) {
            //
            // This is a fatal error
            //
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error configuring the logging", e);
            }
        }

        try {
            singleton.configurePipelineManager();
        } catch (Exception e) {
            //
            // This is a fatal error
            //
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error configuring the PipelineManager", e);
            }
        }

        try {
            singleton.configureUpdateDiscovery();
        } catch (Exception e) {
            //
            // Note that an error initializing the updateDiscovery is just logged.
            //
            log.error("Error initializing UpdateDiscovery", e);
        }

        try {
            singleton.configurePluginHandler();
        } catch (Exception e) {
            //
            // This is a fatal error
            //
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error configuring the PluginHandler", e);
            }
        }

        try {
            singleton.configurePushManager();
        } catch (Exception e) {
            //
            // This is a fatal error
            //
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error configuring the PushManager", e);
            }
        }
    }

    /**
     * Getter for property serverConfig.
     * @return Value of property serverConfig.
     */
    public ServerConfiguration getServerConfig() {
        ServerConfiguration serverConfig = null;
        
        try {
            serverConfig = (ServerConfiguration) beanTool.getBeanInstance(BEAN_SERVER_CONFIGURATION);
        } catch (BeanException ex) {
            log.fatal("Error creating the ServerConfiguration object", ex);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the ServerConfiguration object", ex);
            }
        }

        return serverConfig;
    }

    /**
     * Creates and returns a new SyncStrategy instance from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().strategy</i> property
     *
     * @return the newly created instance
     */
    public SyncStrategy getStrategy() {
        try {
            return (SyncStrategy)beanTool.getNewBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getStrategy()
            );
        } catch (Exception e) {
            log.fatal("Error creating the SyncStrategy object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the SyncStrategy object", e);
            }
        }
        return null;
    }

    /**
     * Creates and returns a new SyncEngine instance from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().engine</i> property
     *
     * @return the newly created instance
     */
    public SyncEngine getEngine() {
        try {
            return (SyncEngine)beanTool.getNewBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getEngine()
            );
        } catch (Exception e) {
            log.fatal("Error creating the SyncEngine object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the SyncEngine object", e);
            }
        }
        return null;
    }

    /**
     * Returns the configured PersistentStoreManager.
     * @return the configured PersistentStoreManager.
     */
    public PersistentStore getStore() {
        return persistentStoreManager;
    }

    /**
     * Creates and returns a new Officer instance from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().getOfficer</i> property
     *
     * @return the newly created instance
     */
    public Officer getOfficer() {
        try {
            return (Officer)beanTool.getBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getOfficer()
            );
        } catch (Exception e) {
            log.fatal("Error creating the Officer object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the Officer object", e);
            }
        }
        return null;
    }


    /**
     * Creates and returns a new SessionHandler instance from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().sessionHandler</i> property
     *
     * @return the newly created instance
     */
    public SessionHandler getSessionHandler() {
        try {
            return (SessionHandler)beanTool.getNewBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getSessionHandler()
            );
        } catch (Exception e) {
            log.fatal("Error creating the SessionHandler object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the SessionHandler object", e);
            }
        }
        return null;
    }

    /**
     * Returns the configured PipelineManager
     *
     * @return the configured PipelineManager
     */
    public PipelineManager getPipelineManager() {
        return pipelineManager;
    }

    /**
     * Creates and returns a new UserManager instance from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().userManager</i> property
     *
     * @return the newly created instance
     */
    public UserManager getUserManager() {
        try {
            return (UserManager)beanTool.getNewBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getUserManager()
            );
        } catch (Exception e) {
            log.fatal("Error creating the UserManager object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the UserManager object", e);
            }
        }
        return null;
    }

    /**
     * Returns the SMSService instance created from the configured
     * <i>singleton.getServerConfig().getEngineConfiguration().smsService</i> property
     *
     * @return the newly created instance
     */
    public SMSService getSMSService() {
        try {
            return (SMSService)beanTool.getBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getSmsService()
            );
        } catch (Exception e) {
            log.fatal("Error creating the SMSService", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the SMSService", e);
            }
        }
        return null;
    }

    /**
     * Returns the used UpdateDiscovery
     *
     * @return the UpdateDiscovery instance
     */
    public UpdateDiscovery getUpdateDiscovery() {
        return updateDiscovery;
    }

    /**
     * Creates and returns a new DeviceInventory instance from the configured
     * <i>serverConfig.getEngineConfiguration().getDeviceInventory()</i> property
     *
     * @return the newly created instance
     */
    public DeviceInventory getDeviceInventory() {
        try {
            return (DeviceInventory)beanTool.getBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getDeviceInventory()
            );
        } catch (Exception e) {
            log.fatal("Error creating the DeviceInventory object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the DeviceInventory object", e);
            }
        }
        return null;
    }

    /**
     * Creates and returns a new PipelineManager instance from the configured
     * <i>serverConfig.getEngineConfiguration().pipelineManager</i> property
     *
     * @return the newly created instance
     */
    public DataTransformerManager getDataTransformerManager() {
        String dataTransformerManager =
            singleton.getServerConfig().getEngineConfiguration().getDataTransformerManager();

        if (dataTransformerManager == null || dataTransformerManager.equals("")) {
            if (log.isTraceEnabled()) {
                log.trace("DataTransformerManager not set");
            }
            return null;
        }

        try {
            return (DataTransformerManager)beanTool.getBeanInstance(
                dataTransformerManager
            );
        } catch (Exception e) {
            log.fatal("Error creating the DataTransformerManager object", e);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the DataTransformerManager object", e);
            }
        }
        return null;
    }

    /**
     * Creates and returns an array of the available LoggerConfiguration
     *
     * @return an array of the available LoggerConfiguration
     */
    public LoggerConfiguration[] getLoggers() {

        String[] loggerBeanNames = getBeanNames(PATH_LOGGER);

        if ((loggerBeanNames == null) || (loggerBeanNames.length == 0)) {
            if (log.isTraceEnabled()) {
                log.trace("No logger bean found");
            }

            return new LoggerConfiguration[0];
        }

        LoggerConfiguration[] loggers =
            new LoggerConfiguration[loggerBeanNames.length];

        for (int i = 0; i < loggerBeanNames.length; i++) {

            try {
                loggers[i] = (LoggerConfiguration)getBeanInstanceByName(
                        loggerBeanNames[i]);
            } catch (BeanException ex) {
                continue;
            }
        }

        return loggers;
    }

    /**
     * Creates and returns a Map with the available appenders
     *
     * @return a Map with the available appenders
     */
    public Map<String, Appender> getAppenders() {

        Map<String, Appender> appendersMap = new HashMap();
        appendersMap = AppendersCache.getAppenders();
        return appendersMap;
    }

    /**
     * Sets the given appender
     * @param appender the appender to set
     * @throws ConfigurationException if an error occurs
     */
    public void setAppender(Appender appender)
    throws ConfigurationException {
        if (appender == null) {
            return ;
        }

        String beanName = PATH_APPENDER + File.separator + appender.getName() + ".xml";

        try {

            disableDirectoryMonitor();

            beanTool.saveBeanInstance(beanName,
                appender);

            configureLogging();

        } catch (Exception ex) {
            throw new ConfigurationException("Error saving appender", ex);
        } finally {
            enableDirectoryMonitor();
        }


        if (log.isInfoEnabled()) {
            log.info("Appender '" + appender.getName() + "' saved successfully");
        }
    }

    /**
     * Returns the class name of the management panel to configure an appender
     * with the given class name. The management class is retrieved searching an
     * appender type with the given appenderClassName
     * @return the class name of the management panel of the appender with the
     *         given class name or null if not found
     * @param appenderClassName the appender class name
     * @throws ConfigurationException if an error occurs
     */
    public String getAppenderManagementPanel(String appenderClassName)
    throws ConfigurationException {

        if (appenderClassName == null) {
            return null;
        }
        String[] appenderTypeBeanNames = getBeanNames(PATH_APPENDER_TYPE);

        if ((appenderTypeBeanNames == null) ||
            (appenderTypeBeanNames.length == 0)) {

            if (log.isTraceEnabled()) {
                log.trace("No appender type bean found");
            }

            return null;
        }

        AppenderType appenderType = null;
        for (int i = 0; i < appenderTypeBeanNames.length; i++) {

            try {
                appenderType = (AppenderType)getBeanInstanceByName(
                        appenderTypeBeanNames[i]);
            } catch (BeanException ex) {
                continue;
            }
            if (appenderClassName.equals(appenderType.getClassName())) {
                return appenderType.getManagementPanel();
            }
        }

        return null;
    }

    /**
     * Saves new server configuration
     *
     * @param serverConfig the new server configuration
     *
     * @throws ConfigurationException
     */
    public void setServerConfiguration(ServerConfiguration serverConfig)
        throws ConfigurationException {

        try {

            beanTool.saveBeanInstance(BEAN_SERVER_CONFIGURATION, serverConfig);

            //
            // @TODO reload the configuration automalically when saved
            //
            // Since the server configuration is not automatically reloaded
            // we need to udpate the UpdateDiscovery instance
            //
            updateUpdateDiscoveryInstance();

        } catch (Exception e) {
            throw new ConfigurationException("Error saving server configuration", e);
        } finally {
            enableDirectoryMonitor();
        }
    }

    /**
     * Saves the given logger configuration
     * @param config the logger to set
     * @throws com.funambol.framework.config.ConfigurationException
     */
    public void setLoggerConfiguration(LoggerConfiguration config)
    throws ConfigurationException {

        String beanName = PATH_LOGGER + File.separator + config.getName() + ".xml";

        try {
            disableDirectoryMonitor();

            beanTool.saveBeanInstance(beanName,
                config);

            configureLogging();

        } catch (Exception ex) {
            throw new ConfigurationException("Error saving logger configuration", ex);
        } finally {
            enableDirectoryMonitor();
        }


        if (log.isInfoEnabled()) {
            log.info("Logger '" + config.getName() + "' saved successfully");
        }
    }

    /**
     * Configures the logging system accordingly to the
     * bean files in the logger/appender directory.
     *
     * @throws ConfigurationException in case of errors setting up the logging system
     */
    public synchronized void configureLogging()
    throws ConfigurationException {
        disableDirectoryMonitor();

        try {

            resetLog4jLogging();

            handleLoggingFile();

            configureAppenders();

            configureLoggers();

        } catch (Exception t) {
            log.error("Error setting the logging", t);
            throw new ConfigurationException("Error setting the logging", t);
        } finally {
            enableDirectoryMonitor();
        }
    }

    /**
     * Loads and instantiate a bean by its config name. In this case the bean
     * is not looked up in the internal configuration map, but is created
     * directly by. A new instance is create at each call.
     *
     * @param beanName the complete beanName
     *
     * @return the bean instance
     *
     * @throws BeanException in case of instantiation error
     */
    public Object getBeanInstanceByName(String beanName)
    throws BeanException {
        return getBeanInstanceByName(beanName, false);
    }

    /**
     * Loads bean by its config name. In this case the bean
     * is not looked up in the internal configuration map, but is created
     * directly by. A new instance is create at each call.
     * The bean is not initialized.
     *
     * @param beanName the complete beanName
     *
     * @return the bean instance
     *
     * @throws BeanException in case of instantiation error
     */
    public Object getNoInitNewBeanInstance(String beanName)
    throws BeanException {
        return beanTool.getNoInitNewBeanInstance(beanName);
    }
    
    /**
     * Loads and instantiate a bean by its config name. In this case the bean
     * is not looked up in the internal configuration map, but is created
     * directly by.
     *
     * If <code>cachedInstance</code> is true, this methods tries to return the
     * last instance created with the giveng server bean name. A new instance is
     * returned only when the server bean configuration has changed.
     *
     * @param beanName the complete beanName
     * @param cachedInstance should the instance be extracted from the cache?
     *
     * @return the bean instance
     *
     * @throws BeanException in case of instantiation error
     */
    public Object getBeanInstanceByName(String beanName, boolean cachedInstance)
    throws BeanException {
        if (cachedInstance) {
            return beanTool.getBeanInstance(beanName);
        }

        return beanTool.getNewBeanInstance(beanName);
    }

    /**
     * Saves an instantiate of a server bean to the given name under the config
     * path.
     * @param o the object to save
     * @param beanName the complete beanName
     * @throws BeanException in case of instantiation error
     */
    public void setBeanInstance(String beanName, Object o)
    throws BeanException {
        beanTool.saveBeanInstance(beanName, o);
    }

    /**
     * Returns the Funambol home as set as follows (the first value found is
     * taken):
     * <ul>
     *  <li>the system property funambol.home</li>
     *  <li>the current directory</i>
     * </ul>
     *
     * @return the Funambol home as set as discribed above.
     * @deprecated Since v66, use getFunambolHome()
     */
    public static String getSync4jHome() {
        return getFunambolHome();
    }

    /**
     * Returns the Funambol home as set as follows (the first value found is
     * taken):
     * <ul>
     *  <li>the system property funambol.home</li>
     *  <li>the current directory</i>
     * </ul>
     *
     * @return the Funambol home as set as described above.
     */
    public static String getFunambolHome() {
        //
        // If PROPERTY_SYSTEM_FUNAMBOL_HOME (funambol.home) is not defined,
        // the PROPERTY_SYSTEM_FUNAMBOL_DS_HOME (funambol.ds.home) is used.
        // This is not really usefull for backward compatibility since the config
        // directory has been moved, but at least for the junit test case it can
        // be convenient (some components may specify the funambol.ds.home property
        // in the pom.xml file about test cases)
        //
        return System.getProperty(PROPERTY_SYSTEM_FUNAMBOL_HOME,
                                  System.getProperty(PROPERTY_SYSTEM_FUNAMBOL_DS_HOME, "."));
    }

    /**
     * Returns the config path calculated as getSync4jHome() + CONFIG_PATH
     *
     * @return the config path calculated as getSync4jHome() + CONFIG_PATH
     */
    public static String getConfigPath() {

        return getFunambolHome() + CONFIG_PATH;
    }

    /**
     * Returns the push manager
     * @return the push manager
     */
    public PushManager getPushManager() {
        configurePushManager();
        return pushManager;
    }

    /**
     * Redefined to print an understandable view of the singleton object.
     *
     * @return the string representing the singleton instance
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        ServerConfiguration serverConfig = singleton.getServerConfig();

        sb.append(getClass().getName())
          .append('\n')
          .append("serverInfo: ")
          .append(serverConfig.getServerInfo())
          .append('\n')
          .append("engineConfiguration: ")
          .append(serverConfig.getEngineConfiguration())
          .append('\n');

        return sb.toString();
    }

    /**
     * Called when the DirectoryMonitor detects a change in the configuration
     * directory. See {@link com.funambol.server.tool.directorymonitor.FileChangeListener}
     *                 com.funambol.server.tool.directorymonitor.FileChangeListener
     * @param fileChangeEvent the event fired to notify a change
     */
    public void fileChange(FileChangeEvent fileChangeEvent) {
        if (fileChangeEvent == null) {
            return;
        }
        File fileChanged = fileChangeEvent.getFile();

        //
        // We have to check if the updated file is a logging bean or a synclet or
        // the update discovery configuration file just if the engine components
        // have been initialized
        //
        if (engineComponentsInitialized) {

            if (isALoggingConfigurationFile(fileChanged)) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("Logging configuration changed ('" + fileChanged.getPath() +
                            "'). Configuring logging");
                    }

                    configureLogging();

                    if (log.isInfoEnabled()) {
                        log.info("Logging configured");
                    }
                } catch (Exception ex) {
                    log.fatal("Error configuring the logging", ex);
                }
            }

            if (isASyncletConfigurationFile(fileChanged)) {
                try {
                    if (log.isInfoEnabled()) {
                        log.info("Pipeline configuration changed ('" + fileChanged.getPath() +
                            "'). Configuring the PipelineManager");
                    }

                    configurePipelineManager();

                    if (log.isInfoEnabled()) {
                        log.info("PipelineManager configured");
                    }
                } catch (Exception ex) {
                    log.fatal("Error configuring the PipelineManager", ex);
                }
                return;
            }

            if (isTheUpdateDiscoveryConfigurationFile(fileChanged)) {
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("UpdateDiscovery configuration changed");
                    }

                    configureUpdateDiscovery();

                    if (log.isTraceEnabled()) {
                        log.trace("UpdateDiscovery configured");
                    }
                } catch (Exception ex) {
                    log.fatal("Error configuring the UpdateDiscovery", ex);
                }
                return;
            }

        }

        if (isTheServerConfigurationFile(fileChanged)) {
            //
            // @TODO reload all configuration automalically when saved
            //
            // Since the server configuration is not automatically reloaded
            // we need to udpate the UpdateDiscovery instance
            //
            if (engineComponentsInitialized) {
                if (log.isTraceEnabled()) {
                    log.trace("Updating the UpdateDiscovery instance");
                }
                updateUpdateDiscoveryInstance();
            }
            return;
        }

        if (isAPersistentStoreConfigurationFile(fileChanged)) {
            try {
                if (log.isInfoEnabled()) {
                    log.info("Persistent stores configuration changed ('" + fileChanged.getPath() +
                        "'). Configuring the PersistentStoreManager");
                }

                configurePersistentStoreManager();

                if (log.isInfoEnabled()) {
                    log.info("PersistentStoreManager configured");
                }
            } catch (Exception ex) {
                log.fatal("Error configuring the PersistentStoreManager", ex);
            }
            return;
        }

        if (isAPluginConfigurationFile(fileChanged)) {
            if (log.isInfoEnabled()) {
                log.info("Plugin configuration changed ('" + fileChanged.getPath() +
                    "').");
            }

            if (FileChangeEvent.EventType.FILE_ADDED == fileChangeEvent.getEventType() ||
                FileChangeEvent.EventType.FILE_CHANGED == fileChangeEvent.getEventType()) {
                if (pluginHandler != null) {
                    pluginHandler.reloadPlugin(fileChanged.getName());
                }
            } else if (FileChangeEvent.EventType.FILE_DELETED == fileChangeEvent.getEventType()) {
                if (pluginHandler != null) {
                    pluginHandler.dischargePlugin(fileChanged.getName());
                }
            }
            return;
        }
    }

    /**
     * Releases the resources
     */
    public void release() {

        if (pushManager != null) {
            if (log.isInfoEnabled()) {
                log.info("Stopping push manager");
            }
            pushManager.shutdown();
        }

        if (directoryMonitor != null) {
            if (log.isInfoEnabled()) {
                log.info("Stopping configuration monitor [" + directoryMonitor.toString() + "]");
            }
            directoryMonitor.stopMonitor();
            directoryMonitor = null;
        }

        if (updateDiscovery != null) {
            if (log.isInfoEnabled()) {
                log.info("Stopping update discovery thread [" + updateDiscovery.toString() + "]");
            }
            updateDiscovery.stopCheck();
            //
            // Saving upadteDiscovery status (we need to store the lastCheckTimestamp
            // and the lastDSServerUpdate in order to not lose this information)
            //
            try {
                setBeanInstance(getServerConfig().getEngineConfiguration().getUpdateDiscovery(), updateDiscovery);
            } catch (BeanException ex) {
                log.error("Error storing UpdateDiscovery status", ex);
            }
            updateDiscovery = null;
        }

        if (pluginHandler != null) {
            if (log.isInfoEnabled()) {
                log.info("Stopping plugins");
            }
            pluginHandler.stopPlugins();
        }
        engineComponentsInitialized = false;
        singleton = null;
    }

    /**
     * Returns true if funambol is in debug mode, false otherwise.
     * This is needed to determine whether the sensitive data should be
     * displayed or hidden: if funambol is not in debug mode, then the sensitive
     * data are hidden.
     *
     * @return true if funambol is in debug mode, false otherwise
     */
    public boolean isDebugMode() {
        return DEBUG_MODE;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Configures the PersistentStoreManager accordingly to the
     * bean files in <code>DIRECTORY_PERSISTENT_STORE</code> directory.
     *
     * @throws Exception in case of errors setting up the
     *         persistent store manager
     */
    private synchronized void configurePersistentStoreManager()
    throws Exception {

        PersistentStore persistentStore = null;

        String storeBeanName =
                singleton.getServerConfig().getEngineConfiguration().getStore();

        File   storeBeanFile =
            new File(getConfigPath() + File.separator + storeBeanName);

        if (storeBeanFile.exists()) {
            try {
                persistentStore = (PersistentStore)beanTool.getBeanInstance(
                                      storeBeanName
                                  );
            } catch (Exception e) {
                if (log.isTraceEnabled()) {
                    log.trace("Error creating the PersistentStoreManager (" +
                              e.toString() + "). A default one will be created.");
                }
            }
        }

        if (persistentStore == null) {
            persistentStore = new PersistentStoreManager();
        } else {
            if (!(persistentStore instanceof PersistentStoreManager)) {
                persistentStore = new PersistentStoreManager();
            }
        }
        this.persistentStoreManager = (PersistentStoreManager)persistentStore;

        String[] alreadyUsedStoreFileNames = persistentStoreManager.getStores();
        if (alreadyUsedStoreFileNames == null) {
            alreadyUsedStoreFileNames = new String[0];
        }
        File[]   availableStoreFiles       = DIRECTORY_PERSISTENT_STORE.listFiles();
        String   fileName                  = null;

        boolean storeAlreadyUsed = false;

        if (availableStoreFiles != null) {

            for (int i=0; i<availableStoreFiles.length; i++) {
                storeAlreadyUsed = false;
                fileName = PATH_PERSISTENT_STORE + "/" + availableStoreFiles[i].getName();

                if (!fileName.toLowerCase().endsWith(".xml")) {
                    continue;
                }
                for (int j=0; j<alreadyUsedStoreFileNames.length; j++) {
                    if (alreadyUsedStoreFileNames[j].equals(fileName)) {
                        //
                        // The store is already used
                        //
                        storeAlreadyUsed = true;
                        break;
                    }
                }
                if (!storeAlreadyUsed) {
                    //
                    // We have to add it to the PersistentStoreManager
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("Adding '" + fileName + "' to the list of available stores");
                    }

                    persistentStore = (PersistentStore) getBeanInstanceByName(fileName);
                    if (!(persistentStore instanceof PersistentStoreManager)) {
                        persistentStoreManager.addPersistentStore(persistentStore);
                    }

                }
            }
        }
    }

    /**
     * Configures the PipelineManager accordingly to the
     * bean files in <code>DIRECTORY_SYNCLET</code> directory.
     *
     * @throws ConfigurationException in case of errors setting up the persistent
     *         store manager
     */
    private synchronized void configurePipelineManager()
    throws ConfigurationException {

        //
        // Checks if there is a PipelineManager.xml
        //
        try {
            handlePipelineManager();
        } catch (Exception e) {
            log.error("Error handling old PipelineManager configuration",
                      e);
            throw new ConfigurationException("Error handling old PipelineManager configuration",
                                             e);
        }

        pipelineManager = new PipelineManager();

        //
        // Reads the input processors from the input directory
        //
        String[] inputProcessorsBeanNames = getBeanNames(PATH_SYNCLET_INPUT);

        if (log.isTraceEnabled()) {

            if ((inputProcessorsBeanNames.length == 0)) {
                log.trace("No configured input processors");
            }
        }

        List inputProcessorsList = new ArrayList();
        Object o = null;
        for (int i = 0; i < inputProcessorsBeanNames.length; i++) {
            if (log.isTraceEnabled()) {
                log.trace("Adding input processor: " + inputProcessorsBeanNames[i]);
            }
            try {
                o = getBeanInstanceByName(inputProcessorsBeanNames[i]);
            } catch (BeanException ex) {
                log.error("Error creating input processor '" + inputProcessorsBeanNames[i] + "'", ex);
                continue;
            }
            if (o instanceof InputMessageProcessor) {
                inputProcessorsList.add((InputMessageProcessor)o);
            } else {
                if (log.isWarningEnabled()) {
                    log.warn(
                        "The directory '" + PATH_SYNCLET_INPUT + "' contains '"
                        + inputProcessorsBeanNames[i]
                        + "' that is not an InputMessageProcessor object");
                }
            }
        }
        pipelineManager.addInputProcessors(inputProcessorsList);

        //
        // Reads the output processors from the input directory
        //
        String[] outputProcessorsBeanNames = getBeanNames(PATH_SYNCLET_OUTPUT);

        if (log.isTraceEnabled()) {

            if ((outputProcessorsBeanNames.length == 0)) {
                log.trace("No configured output processors");
            }
        }

        List outputProcessorsList = new ArrayList();
        for (int i = 0; i < outputProcessorsBeanNames.length; i++) {
            if (log.isTraceEnabled()) {
                log.trace("Adding output processor: " + outputProcessorsBeanNames[i]);
            }
            try {
                o = getBeanInstanceByName(outputProcessorsBeanNames[i]);
            } catch (BeanException ex) {
                log.error("Error creating output processor '" + outputProcessorsBeanNames[i] + "'", ex);
                continue;
            }
            if (o instanceof OutputMessageProcessor) {
                outputProcessorsList.add((OutputMessageProcessor)o);
            } else {
                if (log.isWarningEnabled()) {
                    log.warn(
                        "The directory '" + PATH_SYNCLET_OUTPUT + "' contains '"
                        + outputProcessorsBeanNames[i]
                        + "' that is not an OutputMessageProcessor object");
                }
            }
        }
        pipelineManager.addOutputProcessors(outputProcessorsList);
    }

    /**
     * Configures the PluginHandler accordingly to the
     * bean files in <code>com/funambol/server/plugin</code> directory.
     *
     * @throws ConfigurationException in case of errors
     */
    private synchronized void configurePluginHandler()
    throws ConfigurationException {

        if (!engineComponentsInitialized) {
            return ;
        }

        pluginHandler = new PluginHandler(DIRECTORY_PLUGINS, PATH_PLUGIN);
        pluginHandler.startPlugins();

    }

    /**
     * Configures the PushManager accordingly to the
     * bean files in <code>com/funambol/server/notification</code> directory.
     *
     * @throws ConfigurationException in case of errors
     */
    private synchronized void configurePushManager()throws ConfigurationException {

        if (pushManager != null){
            return;
        }
        try {
            pushManager = (PushManager) beanTool.getBeanInstance(
                singleton.getServerConfig().getEngineConfiguration().getPushManager());
        } catch (BeanException ex) {
            log.fatal("Error creating the PushManager object", ex);
            if (!QUIET_INITIALIZATION) {
                throw new ConfigurationException("Error creating the PushManager object", ex);
            }
        }

    }


    /**
     * Configures the UpdateDiscovery
     *
     * @throws ConfigurationException in case of errors
     */
    private synchronized void configureUpdateDiscovery()
    throws ConfigurationException {

        if (!engineComponentsInitialized) {
            return ;
        }

        String updateDiscoveryBeanName =
                singleton.getServerConfig().getEngineConfiguration().getUpdateDiscovery();

        File   updateDiscoveryBeanFile =
            new File(getConfigPath() + File.separator + updateDiscoveryBeanName);

        UpdateDiscovery newUpdateDiscovery = null;

        if (updateDiscoveryBeanFile.exists()) {
            try {

                newUpdateDiscovery = (UpdateDiscovery)beanTool.getBeanInstance(
                                         updateDiscoveryBeanName
                                     );
            } catch (Exception e) {
                throw new ConfigurationException("Error creating the UpdateDiscovery", e);
            }
        } else {
            throw new ConfigurationException("Unable to find the UpdateDiscovery configuration file [" +
                                             updateDiscoveryBeanName + "]");
        }
        if (updateDiscovery != null) {
            //
            // There is already an UpdateDiscovery. We have to stop it, and start
            // the new one
            //
            updateDiscovery.stopCheck();
            //
            // Moreover we need to set the lastCheckTimestamp of the new updateDiscovery
            // to the one of the old in order to keep this information, otherwise,
            // every time that the bean file is reloaded a check is performed
            // (the lastCheckTimeStamp is 0)
            //
            newUpdateDiscovery.setLastCheckTimestamp(updateDiscovery.getLastCheckTimestamp());
            newUpdateDiscovery.setLatestDSServerUpdate(updateDiscovery.getLatestDSServerUpdate());
        }
        updateDiscovery = newUpdateDiscovery;
        updateUpdateDiscoveryInstance();
    }

    /**
     * Is the given file a plugin configuration file ?
     * @param fileChanged the file to check
     * @return Is the given file a plugin configuration file ?
     */
    private boolean isAPluginConfigurationFile(File fileChanged) {
        if (isABeanFile(fileChanged)) {
            if (fileChanged.getParentFile().equals(DIRECTORY_PLUGINS)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is the given file the server configuration file ?
     * @param f the file to check
     * @return Is the given file the server configuration file ?
     */
    private boolean isTheServerConfigurationFile(File f) {
        File serverConfigurationFile =
            new File(getConfigPath() +
                     File.separator  +
                     BEAN_SERVER_CONFIGURATION);

        return serverConfigurationFile.equals(f);
    }

    /**
     * Is the given file a logging configuration file ?
     * @param f the file to check
     * @return Is the given file a logging configuration file ?
     */
    private boolean isALoggingConfigurationFile(File f) {
        if (isABeanFile(f)) {
            if (f.getParentFile().equals(DIRECTORY_LOGGING)) {
                return true;
            }
            if (f.getParentFile().equals(DIRECTORY_LOGGER)) {
                return true;
            }
            if (f.getParentFile().equals(DIRECTORY_APPENDER)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is the given file the UpdateDiscovery configuration file ?
     * @param f the file to check
     * @return Is the given file the UpdateDiscovery configuration file ?
     */
    private boolean isTheUpdateDiscoveryConfigurationFile(File f) {
        File updateDiscoveryConfigurationFile =
            new File(getConfigPath() +
                     File.separator  +
                     getServerConfig().getEngineConfiguration().getUpdateDiscovery());

        return f.equals(updateDiscoveryConfigurationFile);
    }

    /**
     * Is the given file a persistent store configuration file ?
     * @param f the file to check
     * @return Is the given file a persistent store configuration file ?
     */
    private boolean isAPersistentStoreConfigurationFile(File f) {
        if (isABeanFile(f)) {
            if (f.getParentFile().equals(DIRECTORY_PERSISTENT_STORE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is the given file a synclet ?
     * @param f the file to check
     * @return Is the given file a synclet ?
     */
    private boolean isASyncletConfigurationFile(File f) {
        if (isABeanFile(f)) {
            if (f.getParentFile().equals(DIRECTORY_SYNCLET_INPUT)) {
                return true;
            }
            if (f.getParentFile().equals(DIRECTORY_SYNCLET_OUTPUT)) {
                return true;
            }
        }
        return false;
    }


    /**
     * Configures log4j logging based on the given LoggerConfiguration object
     *
     * @param conf the LoggerConfiguration object
     */
    private void configureLog4jLogger(LoggerConfiguration conf) {

        //
        // If this logger inherits from its parent, just skip logger
        // configuration.
        //
        if (conf.isInherit()) {
            return;
        }
        String loggerName = conf.getName();

        FunambolLoggerFactory.resetConfiguration(loggerName);

        FunambolLogger logger = FunambolLoggerFactory.getLogger(loggerName);

        //
        // Configuring appenders. First remove all old appenders
        //
        logger.removeAllAppenders();
        logger.setAdditivity(false);
        //
        // Converts the list of the users with Level.ALL to a Map needed to the
        // FunambolLogger
        //
        List<String> usersWithLevelALL = conf.getUsersWithLevelALL();
        logger.setUserLevels(null);
        if (usersWithLevelALL != null) {
            HashMap<String, org.apache.log4j.Level> usersLevel =
                new HashMap<String, org.apache.log4j.Level>(usersWithLevelALL.size());
            for (String user : usersWithLevelALL) {
                usersLevel.put(user, org.apache.log4j.Level.ALL);
            }
            logger.setUserLevels(usersLevel);
        }

        List<String> appenderList = conf.getAppenders();
        Appender     appender;

        if (appenderList != null) {
            for (String appenderName : appenderList) {
                appender = AppendersCache.getAppender(appenderName);

                if (appender == null) {

                    if (log.isWarningEnabled()) {
                        log.warn(
                            "Logger '" + loggerName
                            + "' is configured to use appender '" + appenderName
                            + "' but this appender is not available."
                            + "Check directory '<FUNAMBOL_HOME>/config/" + PATH_APPENDER
                            + "'");
                    }
                } else {
                    logger.addAppender(appender);
                }
            }
        }

        if (LoggerConfiguration.LEVEL_OFF.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.OFF);
        } else if (LoggerConfiguration.LEVEL_FATAL.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.FATAL);
        } else if (LoggerConfiguration.LEVEL_ERROR.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.ERROR);
        } else if (LoggerConfiguration.LEVEL_WARN.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.WARN);
        } else if (LoggerConfiguration.LEVEL_INFO.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.INFO);
        }  else if (LoggerConfiguration.LEVEL_DEBUG.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.DEBUG);
        } else if (LoggerConfiguration.LEVEL_TRACE.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.TRACE);
        } else if (LoggerConfiguration.LEVEL_ALL.equalsIgnoreCase(conf.getLevel())) {
            logger.setLevel(org.apache.log4j.Level.ALL);
        }
    }

    /**
     * Returns a sorted array of String that contains the bean name of the bean files
     * under the given directory.
     * @return a sorted array of String that contains the bean name of the bean files
     *         under the given directory
     * @param directory the directory
     */
    private String[] getBeanNames(String directory) {

        File          dir   = new File(getConfigPath(),
                                       directory);
        File[]        files = dir.listFiles();
        if (files == null) {
            return new String[0];
        }
        String[]      ret   = new String[files.length];
        int           i     = 0;
        StringBuilder sb    = null;
        List          beanList = new ArrayList();
        for (; i < files.length; ++i) {
            if (files[i].isFile() && files[i].getName().endsWith(".xml")) {
                sb = new StringBuilder(directory);
                sb.append(File.separator);
                sb.append(files[i].getName());
                beanList.add(sb.toString());
            }
        }

        Collections.sort(beanList);
        return (String[])beanList.toArray(new String[0]);
    }

    /**
     * Reads the logging configuration file and splits it in logger files (under logger directory).
     * After that, the logging configuration file is deleted (deprecated starting from v6)
     * @throws com.funambol.framework.tools.beans.BeanException if an error occurs
     */
    private void handleLoggingFile()
    throws BeanException {

        //
        // we configure Funambol logging using logging.xml file
        //
        String loggingBeanName =
            getServerConfig().getEngineConfiguration().getLoggingConfiguration();

        File   loggingFile     =
            new File(getConfigPath() + File.separator + loggingBeanName);

        if (!loggingFile.exists()) {
            return;
        }

        LoggingConfiguration lc = (LoggingConfiguration)beanTool.getNewBeanInstance(
                loggingBeanName
        );

        if (lc == null) {
            return;
        }

        LoggerConfiguration conf;
        Iterator            it = lc.getLoggers().iterator();

        while (it.hasNext()) {
            conf = (LoggerConfiguration)it.next();
            //
            // By default, we set FunambolConsole and FunambolLogFile as appender
            //
            List a = new ArrayList();
            a.add("FunambolConsole");
            a.add("FunambolLogFile");
            conf.setAppenders(a);
            //
            // We store the conf object in the logger directory. The loggig.xml
            // file is deprecated starting from v6 but it is still read and
            // any configuration objects in that file are written in the
            // logger directory using the "logger name".xml as file name.
            // Note that if a logger "logger name".xml already exists, this is
            // not overwritten.
            // After that, the logging.xml file is deleted
            //
            String beanName = PATH_LOGGER + File.separator + conf.getName() + ".xml";
            File   beanFile = new File(getConfigPath() + File.separator + beanName);
            if (!beanFile.exists()) {
                beanTool.saveBeanInstance(beanName, conf);
            }
        }

        beanTool.deleteBeanInstance(loggingBeanName);

        if (log.isTraceEnabled()) {
            log.trace("Logging configuration file deleted");
        }
    }

    /**
     * Reads the PipelineManager configuration file and splits it in input processor files
     * (under pipeline/input directory) and in output processor files
     * (under pipeline/output directory).
     * After that, the PipelineManager configuration file is deleted (deprecated starting from v6)
     */
    private void handlePipelineManager() throws BeanException {

        String pipelineManagerBeanName =
            getServerConfig().getEngineConfiguration().getPipelineManager();

        File   pipelineManagerFile     =
            new File(getConfigPath() + File.separator + pipelineManagerBeanName);

        if (!pipelineManagerFile.exists()) {
            return;
        }

        PipelineManager configuredPipelineManager = null;

        try {
            configuredPipelineManager =
                (PipelineManager)getBeanInstanceByName(pipelineManagerBeanName);
        } catch (Exception e) {
            log.error("Error creating the PipelineManager reading '" +
                      pipelineManagerBeanName + "'. That file will be ingored", e);
            return;
        }

        if (configuredPipelineManager == null) {
            return ;
        }

        //
        // We store the inputProcessor and outputProcessor objects
        // in pipeline/input and pipeline/output directories.
        // The PipelineManager.xml file is deprecated starting from v6 but
        // it is still read and:
        // - any configured inputProcessor in that file is
        //   written in the "input" directory using "0000.000x.classname.xml"
        //   as file name.
        // - any configured outputProcessor in that file is
        //   written in the "output" directory using "0000.000x.classname.xml"
        //   as file name.
        // After that, the pipelineManager.xml file is deleted
        //

        //
        // Handling InputMessageProcessors
        //
        InputMessageProcessor[] inputProcessors
            = configuredPipelineManager.getInputProcessors();

        String sCont = null;
        if (inputProcessors != null)  {
            for (int i=0; i<inputProcessors.length; i++) {
                sCont = String.valueOf(i+1);
                if (i < 9) {
                    sCont = "000" + sCont;
                } else if (i < 99) {
                    sCont = "00" + sCont;
                } else if (i < 999) {
                    sCont = "0" + sCont;
                }

                String className = inputProcessors[i].getClass().getName();
                String beanName = PATH_SYNCLET_INPUT + File.separator +
                                  "0000." + sCont + "." + className + ".xml";
                File   beanFile = new File(getConfigPath() + File.separator + beanName);

                if (!beanFile.exists()) {
                    beanTool.saveBeanInstance(beanName, inputProcessors[i]);
                }

            }
        }


        //
        // Handling OutputMessageProcessors
        //
        OutputMessageProcessor[] outputProcessors
            = configuredPipelineManager.getOutputProcessors();

        sCont = null;
        if (outputProcessors != null)  {
            for (int i=0; i<outputProcessors.length; i++) {
                sCont = String.valueOf(i+1);
                if (i < 9) {
                    sCont = "000" + sCont;
                } else if (i < 99) {
                    sCont = "00" + sCont;
                } else if (i < 999) {
                    sCont = "0" + sCont;
                }

                String className = inputProcessors[i].getClass().getName();
                String beanName = PATH_SYNCLET_OUTPUT + File.separator +
                                  "0000." + sCont + "." + className + ".xml";
                File   beanFile = new File(getConfigPath() + File.separator + beanName);

                if (!beanFile.exists()) {
                    beanTool.saveBeanInstance(beanName, outputProcessors[i]);
                }

            }
        }

        beanTool.deleteBeanInstance(pipelineManagerBeanName);

        if (log.isTraceEnabled()) {
            log.trace("PipelineManager configuration file deleted");
        }
    }

    /**
     * Resets log4j loggers
     */
    private void resetLog4jLogging() {
        FunambolLoggerFactory.resetConfiguration();
    }

    /**
     * Disables the directory monitor
     */
    private void disableDirectoryMonitor() {
        if (directoryMonitor.isAlive()) {
            directoryMonitor.disable();
        }
    }

    /**
     * Enables the directory monitor
     */
    private void enableDirectoryMonitor() {
        if (directoryMonitor.isAlive()) {
            directoryMonitor.reset();
            directoryMonitor.enable();
        } else {
            System.out.println("Starting configuration monitor" +
                               " [" + directoryMonitor.toString() + "] on: " +
                               getConfigPath() );
            directoryMonitor.runMonitor();
        }
    }

    /**
     * Reads the configuration bean files under 'appender' directory and return a map
     * with the loaded appenders.
     * @return a map with the loaded appenders. As key, the appender name is used
     */
    private Map<String, Appender> configureAppenders() {

        AppendersCache.clear();

        String[] appenderBeanNames = getBeanNames(PATH_APPENDER);
        if (log.isTraceEnabled()) {
             if (appenderBeanNames == null || appenderBeanNames.length == 0) {
                log.trace("No configured appenders");
            }
        }

        Map<String, Appender> appenders = new HashMap(appenderBeanNames.length);
        Appender              appender  = null;
        Object                o         = null;
        for (int i = 0; i < appenderBeanNames.length; i++) {
            if (log.isTraceEnabled()) {
                log.trace("Configuring appender: " + appenderBeanNames[i]);
            }
            try {
                o = getBeanInstanceByName(appenderBeanNames[i]);
            } catch (Exception ex) {
                //
                // We log exception that occurs configuring the logging on the
                // System.out
                //
                ex.printStackTrace(System.out);
                log.error("Error instantiating '" + appenderBeanNames[i] + "'", ex);
                continue;
            }
            if (!(o instanceof Appender)) {
                if (log.isWarningEnabled()) {
                    log.warn(
                            "The directory '" + PATH_APPENDER + "' contains '"
                            + appenderBeanNames[i]
                            + "' that is not an Appender object");
                }
                continue;
            }
            appender = (Appender)o;
            if (appender instanceof FileAppender) {
                String fileName = ((FileAppender)appender).getFile();
                File file = new File(fileName);
                if (!file.isAbsolute()) {
                    fileName = getSync4jHome() + File.separator + fileName;
                    ((FileAppender)appender).setFile(fileName);
                }
            }
            if (appender instanceof OptionHandler) {
                ((OptionHandler)appender).activateOptions();
            }
            AppendersCache.cacheAppender(appender);

            appenders.put(appender.getName(),
                          appender);
        }

        return appenders;
    }

    /**
     * Reads the configuration bean files under 'logger' directory and for each
     * file, configures a new logger.
     * @throws java.io.IOException
     */
    private void configureLoggers()
    throws IOException {

        String[] loggerBeanNames = getBeanNames(PATH_LOGGER);

        if (log.isTraceEnabled()) {

            if ((loggerBeanNames == null) || (loggerBeanNames.length == 0)) {
                log.trace("No configured loggers");
            }
        }

        Object o = null;
        for (int i = 0; i < loggerBeanNames.length; i++) {
            if (log.isTraceEnabled()) {
                log.trace("Configuring logger: " + loggerBeanNames[i]);
            }
            try {
                o = getBeanInstanceByName(loggerBeanNames[i]);
            } catch (Exception ex) {
                //
                // We log exception that occurs configuring the logging on the
                // System.out
                //
                ex.printStackTrace(System.out);
                log.error("Error creating logger '" + loggerBeanNames[i] + "'", ex);
                continue;
            }
            if (o instanceof LoggerConfiguration) {
                configureLog4jLogger((LoggerConfiguration)o);
            } else {
                if (log.isWarningEnabled()) {
                    log.warn(
                        "The directory '" + PATH_LOGGER + "' contains '"
                        + loggerBeanNames[i]
                        + "' that is not a LoggerConfiguration object");
                }
            }
        }
    }

    /**
     * Updates the properties of the UpdateDiscovery instance as its state
     * (based on the checkForUpdates flag), the server version and the server uri
     */
    private void updateUpdateDiscoveryInstance() {

        if (!engineComponentsInitialized) {
            return ;
        }

        boolean checkForUpdates = getServerConfig().getEngineConfiguration().getCheckForUpdates();
        if (checkForUpdates) {
            if (updateDiscovery != null && !updateDiscovery.isEnabled()) {
                updateDiscovery.startCheck();
            }
        } else {
            if (updateDiscovery != null) {
                updateDiscovery.setLatestDSServerUpdate(null); // In this way also
                                                               // the already detected
                                                               // updates are discharged
                if (updateDiscovery.isEnabled()) {
                    updateDiscovery.stopCheck();
                }
            }
        }
        updateDiscovery.setServerVersion(getServerConfig().getServerInfo().getSwV());
    }

    /**
     * Returns true if the given file is a bean configuration file
     * @param file the file to check.
     * @return true if the given file is a bean configuration file (in this version,
     *         juut the extension is checked [it must be .xml]), false otherwise
     */
    private boolean isABeanFile(File f) {
        if (f == null) {
            return false;
        }
        String name = f.getName();
        if (name.endsWith(".xml") || name.endsWith(".XML")) {
            return true;
        }
        return false;
    }
}
