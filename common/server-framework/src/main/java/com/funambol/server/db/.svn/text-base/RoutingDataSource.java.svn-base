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
package com.funambol.server.db;

/**
 * IMPORTANT NOTE ABOUT IMPORT:
 * This class is loaded at startup phase by Tomcat. Any imported classes must be in the
 * tomcat classpath.
 */

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.management.ObjectName;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;


/**
 * The Routing DataSource datasource works as proxy versus several databases;
 * it wraps the real datasources and when the getConnection() method is called,
 * it uses a PartitioningCriteria to know the name of the datasource to use
 * based upon a partitioning key.
 * Since the getConnection() method doesn't provide any sort of information to
 * the datasource (that actually must know the username in order to select the
 * right database/partition) a new getRoutedConnection(partitioningKey: String)
 * method is added to the routing datasource.
 * Moreover, the getConnection methods are override in order to avoid their usage.
 *
 * @version $Id: RoutingDataSource.java,v 1.6 2008-06-26 12:43:08 nichele Exp $
 */
public class RoutingDataSource implements DataSource, LazyInitBean {

    // --------------------------------------------------------------- Constants

    /**
     * Why two factories ?
     * If the RoutingDataSource is created in tomcat, the STANDARD_DATASOURCE_FACTORY
     * is not available. In this case we should use TOMCAT_DATASOURCE_FACTORY that
     * is a STANDARD_DATASOURCE_FACTORY with a different package (as per tomcat documentation).
     * We use reflection to handle the factory since we don't want a tomcat dependency.
     */
    private static final String TOMCAT_DATASOURCE_FACTORY   = "org.apache.tomcat.dbcp.dbcp.BasicDataSourceFactory";
    private static final String STANDARD_DATASOURCE_FACTORY = "org.apache.commons.dbcp.BasicDataSourceFactory";

    private static final String TOMCAT_REGISTRY_MODELER   = "org.apache.tomcat.util.modeler.Registry";
    private static final String STANDARD_REGISTRY_MODELER = "org.apache.commons.modeler.Registry";

    // -------------------------------------------------------------- Properties

    /** The PartitioningCriteria to use */
    private PartitioningCriteria partitioningCriteria = null;

    public PartitioningCriteria getPartitioningCriteria() {
        return partitioningCriteria;
    }

    public void setPartitioningCriteria(PartitioningCriteria partitioningCriteria) {
        this.partitioningCriteria = partitioningCriteria;
    }

    /** The PartitionConfigurationLoader */
    private PartitionConfigurationLoader partitionConfigurationLoader = null;

    public PartitionConfigurationLoader getPartitionConfigurationLoader() {
        return partitionConfigurationLoader;
    }

    public void setPartitionConfigurationLoader(PartitionConfigurationLoader partitionConfigurationLoader) {
        this.partitionConfigurationLoader = partitionConfigurationLoader;
    }

    // ------------------------------------------------------------ Private data
    /**
     * Is the datasource initialized ?
     * It's used to block data access in configuration phase
     * and when the configuration must be reload.
     */
    private boolean initialized = false;

    /**
     * Is the datasource configured ?
     */
    private boolean configured = false;

    /**
     * Is the datasource locked (during configuration)? If so, any getRoutedConnection calls throw a
     * SQLException (after a predefined timeout)
     */
    private ReentrantLock lock = new ReentrantLock();

    /**
     * If simpleUsage just the
     * following configuration parameters are used:
     * <ul>
     * <li>url</li>
     * <li>username</li>
     * <li>password</li>
     * <li>driverClassName</li>
     * </ul>
     * to configure all the datasources.
     */
    private boolean simpleUsage = false;

    /**
     * Contains the couples partitionName / datasource
     */
    private Map<String, DataSource> dataSources = null;

    public Map<String, DataSource> getDataSources() {
        return dataSources;
    }

    /**
     * Contains the couples partitionName / DataSourceConfiguration
     */
    private Map<String, DataSourceConfiguration> dataSourcesConfiguration = null;

    public Map<String, DataSourceConfiguration> getDataSourcesConfiguration() {
        return dataSourcesConfiguration;
    }
    
    /**
     * The logger
     */
    private final Logger logger = Logger.getLogger("funambol.db");

    /**
     * Used if the partition returned by the partitioning criteria is null or
     * if the partition is not found in the dataSources map
     */
    private DataSource defaultDataSource = null;
    
    /**
     * The configuration used to created the RoutingDataSource
     */
    private RoutingDataSourceConfiguration configuration = null;


    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new RoutingDataSource using the given configuration.
     * @param configuration the RoutingDataSource configuration to use
     * @throws com.funambol.server.db.DataSourceConfigurationException
     */
    public RoutingDataSource(RoutingDataSourceConfiguration configuration)
    throws DataSourceConfigurationException {

        this(configuration, false);
    }

    /**
     * Creates a new RoutingDataSource using the given configuration.
     * <br/>
     * Note that this constructor is usefull in all the cases we need to use the server
     * datasources without concerns about tuning configuration. Indeed just the
     * following configuration parameters are used:
     * <ul>
     * <li>url</li>
     * <li>username</li>
     * <li>password</li>
     * <li>driverClassName</li>
     * </ul>
     * to configure all the datasources.
     * @param configuration the RoutingDataSource configuration to use
     * @param simpleUsage must the datasources be configured just with url, username
     *        password and driverClassName parameters ?
     * @throws com.funambol.server.db.DataSourceConfigurationException
     */
    public RoutingDataSource(RoutingDataSourceConfiguration configuration,
                             boolean simpleUsage)

    throws DataSourceConfigurationException {

        if (configuration == null) {
            throw new IllegalArgumentException(
                "The given RoutingDataSourceConfiguration must be not null"
            );
        }
        
        this.configuration = configuration;
        
        Properties prop = null;
        if (simpleUsage) {
            prop = configuration.getBasicProperties();
        } else {
            prop = configuration.getProperties();
        }
        try {
            this.defaultDataSource = createDataSource(prop);
        } catch (Exception e) {
            throw new DataSourceConfigurationException("Error creating default datasource", e);
        }
        
        this.partitioningCriteria         = configuration.getPartitioningCriteria();
        this.partitionConfigurationLoader = configuration.getPartitionConfigurationLoader();
        
        this.simpleUsage = simpleUsage;
    }

    // ------------------------------------------------------ DataSource methods

    public Connection getConnection() throws SQLException {
        throw new UnsupportedOperationException("Use getRoutedConnection(String partitioningKey)");
    }

    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Use getRoutedConnection(String partitioningKey)");
    }

    public PrintWriter getLogWriter() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public int getLoginTimeout() throws SQLException {
        throw new UnsupportedOperationException();
    }

    public <T> T unwrap(Class<T> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        throw new UnsupportedOperationException();
    }

    // ---------------------------------------------------------- Public methods

    public synchronized void init() throws BeanInitializationException {

        if (partitionConfigurationLoader == null) {
            partitionConfigurationLoader = new DataSourceConfigurationHelper();
        }

        if (partitioningCriteria != null) {
            try {
                partitioningCriteria.init();
            } catch (PartitioningCriteriaException ex) {
                throw new BeanInitializationException("Error initializing the partition criteria ("
                                                      + partitioningCriteria + ")", ex);
            }
        }

        try {
            partitionConfigurationLoader.init();
        } catch (DataSourceConfigurationException ex) {
            throw new BeanInitializationException("Error initializing the partition configuration loader ("
                                                  + partitionConfigurationLoader + ")", ex);
        }

        try {
            registerMBean(defaultDataSource, "defaultDataSource");
        } catch (Exception e) {
            System.err.println("Error registering MBean for the default DataSource");
            e.printStackTrace();
        }
        initialized = true;

    }


    /**
     * Reloads the configuration if the instance is already configured.
     * @throws com.funambol.server.db.RoutingDataSourceException
     */
    public synchronized void reloadConfiguration() throws RoutingDataSourceException, BeanInitializationException {
        if (logger.isTraceEnabled()) {
            logger.info("Reloading configuration of " + this);
        }
        if (!configured) {
            return;
        }
        configure();
    }

    /**
     * Configures the datasource. As first thing, the datasource is locked; at the
     * end (also if errors occurred) the datasource is unlocked.
     * @throws com.funambol.server.db.RoutingDataSourceException if an error occurss
     */
    public synchronized void configure() throws RoutingDataSourceException, BeanInitializationException {

        if (!initialized) {
            //
            // If the instance is not initialized, configure method can not be
            // performed. As possibility, the init() method could be called here,
            // but in tomcat, calling this method via JMX, an expection is thrown
            // (java.lang.ClassNotFoundException: org.apache.naming.java.javaURLContextFactory)
            // In any case this behaviour is acceptable since if the datasource is not
            // initialized, it will be initialized and configured the first time the
            // getRoutedConnection method is called.
            return;
        }
        lock.lock();
        
        try {
            if (partitioningCriteria != null) {
                try {
                    partitioningCriteria.configure();
                } catch (Exception ex) {
                    lock.unlock();
                    throw new RoutingDataSourceException("Error configuring the partition criteria ("
                                                         + partitioningCriteria + ")", ex);
                }
            }

            try {
                initPartitions();
            } catch (Exception ex) {
                lock.unlock();
                throw new RoutingDataSourceException("Error initializing the datasources", ex);
            }

            configured = true;

        } finally {
            //
            // In any case we don't want the datasource locked
            //
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }

    }


    /**
     * Returns a routed connection based on the given partitioniningKey.
     * <br>
     * The defaultDataSource is used if:
     * <ui>
     *   <li>the partitioning criteria is null</li>
     *   <li>the partition returned by the partitioning criteria is null</li>
     *   <li>the partition returned by the partitioning criteria is unknown</li>
     *   <li>the partitioning criteria is null</li>
     * </ui>
     * If the used partitioning criteria throws a PartitioningCriteriaException
     * (see also LockedPartitionException) a SQLException is thrown to the caller.
     *
     * @param partitioningKey the partition key
     * @return a connection to the partition to use
     * @throws java.sql.SQLException if an error occurs
     */
    public Connection getRoutedConnection(String partitioningKey) throws SQLException {
        if (lock.isLocked()) {
            //
            // If the datasource is locked, we try to acquire the lock with 20 seconds
            // as timeout. If we are able to get the lock, it means the datasource 
            // has been unlocked in the meantime. See configure method to see 
            // where the lock is acquired.
            // If we are not able to acquire the lock in 20 seconds an exception is
            // thrown. This means the configure() method requires more than 20 seconds
            // ...really bad case.
            //
            try {
                if (!lock.tryLock(20, TimeUnit.SECONDS)) {
                    //
                    // we don't have the lock !
                    //
                    throw new SQLException("Timeout expired waiting for a locked datasource");
                }
                lock.unlock();
            } catch (SQLException e) {
                throw e;
            } catch (Throwable e) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
                SQLException sqlEx = new SQLException("Error waiting for unlocking event");
                sqlEx.initCause(e);
                throw sqlEx;
            }
        }

        if (!configured) {
            if (!initialized) {
                try {
                    init();
                } catch (Exception e) {
                    SQLException sqlEx = new SQLException("Error in initialization");
                    sqlEx.initCause(e);
                    throw sqlEx;
                }
            }
            try {
                configure();
            } catch (Exception e) {
                SQLException sqlEx = new SQLException("Error in configuration");
                sqlEx.initCause(e);
                throw sqlEx;
            }
        }

        if (partitioningCriteria == null) {
            return defaultDataSource.getConnection();
        }

        Partition partition = null;
        try {
            partition = partitioningCriteria.getPartition(partitioningKey);
            if (partition != null) {
                if (!partition.isActive()) {
                    throw new SQLException("The partition for '" + partitioningKey + "' is locked");
                }
            }
        } catch (LockedPartitionException e) {
            SQLException sqlException = new SQLException("The partition for '" + partitioningKey + "' is locked");
            sqlException.initCause(e);
            throw sqlException;
        }catch (PartitioningCriteriaException e) {
            SQLException sqlException = new SQLException("Unable to identify the target partition for '" + partitioningKey + "'");
            sqlException.initCause(e);
            throw sqlException;
        }

        if (partition == null) {
            return defaultDataSource.getConnection();
        }
        String partitionName = partition.getName();
        DataSource ds = dataSources.get(partitionName);

        if (ds == null) {
            return defaultDataSource.getConnection();
        }

        return ds.getConnection();
    }

    /**
     * Returns the key used to register this instance to the jmx server
     * @return the key used to register this instance to the jmx server
     */
    public String getInstanceKey() {
        return this.toString();
    }

    
    // --------------------------------------------------------- Private methods

    private synchronized void initPartitions() throws DataSourceConfigurationException {

        dataSources = new HashMap();

        //
        // 1. Reading partition configuration using the PartitionConfigurationLoader
        //
         
        //
        // configuration object is used as default configuration
        //
        dataSourcesConfiguration =
                partitionConfigurationLoader.getPartitionConfigurations(configuration);


        if (dataSourcesConfiguration == null || dataSourcesConfiguration.size() == 0) {
            //
            // no datasource to configure
            //
            return;
        }

        Iterator<String> itDataSourceName = dataSourcesConfiguration.keySet().iterator();
        while (itDataSourceName.hasNext()) {
            String dataSourceName = itDataSourceName.next();
            DataSourceConfiguration partitionConfiguration = dataSourcesConfiguration.get(dataSourceName);
            DataSource ds = null;
            //
            // 2. creating the datasource
            //
            if (partitionConfiguration != null) {
                try {
                    Properties configurationProperties = null;
                    if (simpleUsage) {
                        configurationProperties = partitionConfiguration.getBasicProperties();
                    } else {
                        configurationProperties = partitionConfiguration.getProperties();
                    }

                    ds = createDataSource(configurationProperties);
                } catch (Exception ex) {
                    //
                    // This error can occurs during tomcat startup so it's better
                    // to log the error using System.err and ex.printStackTrace()
                    // since the funambol webapp is not deployed yet.
                    //
                    System.err.println("Error creation the datasource 'jdbc/" + dataSourceName + "'");
                    ex.printStackTrace();
                    continue;
                }
            }

            dataSources.put(dataSourceName, ds);

            //
            // 3. registering the MBean
            //
            try {
                registerMBean(ds, "partition/" + dataSourceName);
            } catch (Exception e) {
                System.err.println("Error registering MBean for the datasource 'jdbc/" + dataSourceName + "'");
                e.printStackTrace();
                continue;
            }
        }
    }

    /**
     * Creates a ObjectFactory trying to create an instance of
     * <code>TOMCAT_DATASOURCE_FACTORY</code> or of
     * <code>STANDARD_DATASOURCE_FACTORY</code> if the tomcat one fails.
     * @return a ObjectFactory created as <code>TOMCAT_DATASOURCE_FACTORY</code>
     * or as <code>STANDARD_DATASOURCE_FACTORY</code> if the tomcat one fails.
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     * occurs.
     */
    private static ObjectFactory getObjectFactory()
    throws DataSourceConfigurationException {

        ObjectFactory factory = null;

        try {
            Class factoryClass = Class.forName(TOMCAT_DATASOURCE_FACTORY);
            factory = (ObjectFactory)factoryClass.newInstance();
            return factory;
        } catch (Exception ex) {
            //
            // Trying with the STANDARD_DATASOURCE_FACTORY
            //
            try {
                Class factoryClass = Class.forName(STANDARD_DATASOURCE_FACTORY);
                factory = (ObjectFactory)factoryClass.newInstance();
                return factory;
            } catch (Exception e) {
                //
                // Ignoring
                //
                e.printStackTrace();
            }
        }
        throw new DataSourceConfigurationException("Unable to create a BasicDataSourceFactory");
    }

    /**
     * Registers the given DataSouce as a MBean
     * @param ds the datasource
     * @param name the name
     * @throws java.lang.Exception if an error occurs
     */
    private void registerMBean(DataSource ds, String name) throws Exception {

        ObjectName on = null;
        if (name != null && name.startsWith("partition/")) {
            name = name.substring(10);
            on = new ObjectName("com.funambol:type=RoutingDataSource,instance=" + getInstanceKey() + ",routing=partitions,name=" + name);
        } else {
            on = new ObjectName("com.funambol:type=RoutingDataSource,instance=" + getInstanceKey() + ",name=" + name);
        }

        if (ds == null) {
            registry("NOT AVAILABLE", on);
        } else {
            registry(ds, on);
        }

    }

    private void registry(Object o, ObjectName on) {
        Class  registryClass     = null;
        Method getRegistry       = null;
        Method registerComponent = null;
        Object registry          = null;

        try {
            registryClass = Class.forName(TOMCAT_REGISTRY_MODELER);
            getRegistry = registryClass.getMethod("getRegistry", new Class[] {java.lang.Object.class, java.lang.Object.class});
            registry = getRegistry.invoke(null, new Object[] {null, null});
            registerComponent = registry.getClass().getMethod("registerComponent", new Class[] {java.lang.Object.class, ObjectName.class, String.class});
            registerComponent.invoke(registry, new Object[] {o, on, null});
        } catch (Exception ex) {
            //
            // Trying with the STANDARD_REGISTRY_MODELER
            //
            try {
                registryClass = Class.forName(STANDARD_REGISTRY_MODELER);
                getRegistry = registryClass.getMethod("getRegistry", new Class[] {java.lang.Object.class, java.lang.Object.class});
                registry = getRegistry.invoke(null, new Object[] {null, null});
                registerComponent = registry.getClass().getMethod("registerComponent", new Class[] {java.lang.Object.class, ObjectName.class, String.class});
                registerComponent.invoke(registry, new Object[] {o, on, null});
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }
    
    /**
     * Returns a DataSource instance created using the given properties
     * @param properties the properties to use
     * @return a DataSource instance created using the given properties
     * @throws DataSourceConfigurationException if an error occurs
     * @throws Exception if an error occurs
     */
    private DataSource createDataSource(Properties properties) throws DataSourceConfigurationException, Exception {
        ObjectFactory dataSourceFactory = getObjectFactory();

        //
        // The DataSourceFactory is a TOMCAT_DATASOURCE_FACTORY or
        // a STANDARD_DATASOURCE_FACTORY. In both cases the method
        // createDataSource should be used. In order to avoid a dependecy
        // from tomcat, the method is called using reflection.
        //
        Method m = dataSourceFactory.getClass().getDeclaredMethod("createDataSource", Properties.class);
        return (DataSource) m.invoke(dataSourceFactory, properties);        
    }

}
