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

import java.io.File;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;

/**
 * Helper for DataSource configuration
 * @version $Id: DataSourceConfigurationHelper.java,v 1.4 2008-06-14 14:37:24 nichele Exp $
 */
public class DataSourceConfigurationHelper implements PartitionConfigurationLoader {

    // --------------------------------------------------------------- Constants

    private static final String CONFIGURATION_DIRECTORY = "com/funambol/server/db/";

    private static final String JDBC_CONFIGURATION_DIRECTORY       = "com/funambol/server/db/jdbc";
    private static final String PARTITION_CONFIGURATION_DIRECTORY  = "com/funambol/server/db/partition";

    private static final String DB_CONFIGURATION_FILE  = CONFIGURATION_DIRECTORY + "db.xml";

    // ------------------------------------------------------------- Constructor
    public DataSourceConfigurationHelper() {

    }

    // ------------------------------------ PartitionConfigurationLoader methods

    public void init() throws DataSourceConfigurationException {
        //
        // nothing to do
        //
    }

    /**
     * Returns a map with the name and the datasource configuration for all
     * xml files under <code>PARTITION_CONFIGURATION_DIRECTORY</code>
     * (any file is a datasource)
     * @param defaultConfiguration the default configuration that the caller
     * suggests to the PartitionConfigurationLoader. Note that the usage of
     * this default configuration is up to the PartitionConfigurationLoader implementation.
     * It can be null.
     * @return a map with the name and the datasource configuration for all
     *         xml files under the given directory (any file is a datasource)
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs loading the configurations
     */
    public Map<String, DataSourceConfiguration> getPartitionConfigurations(
            DataSourceConfiguration defaultConfiguration)
    throws DataSourceConfigurationException {

        return getDataSourceConfigurations(PARTITION_CONFIGURATION_DIRECTORY, defaultConfiguration);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns all xml files under <code>JDBC_CONFIGURATION_DIRECTORY</code> directory
     * (any file is a datasource)
     * @return an array with all the xml files under the  <code>JDBC_CONFIGURATION_DIRECTORY</code>
     * directory (any file is a datasource)
     */
    public static String[] getJDBCConfigurationFiles() {
        return getConfigurationFiles(JDBC_CONFIGURATION_DIRECTORY);
    }

        /**
     * Returns all xml files under <code>PARTITION_CONFIGURATION_DIRECTORY</code> directory
     * (any file is a datasource)
     * @return an array with all the xml files under the given directory (any file is a datasource)
     */
    public static String[] getPartitionConfigurationFiles() {
        return getConfigurationFiles(PARTITION_CONFIGURATION_DIRECTORY);
    }

    /**
     * Returns the DataSourceConfiguration for the datasource identified with the
     * given name in <code>JDBC_CONFIGURATION_DIRECTORY</code>, reading the
     * db.xml file and the one with the same name of the
     * datasource (for jdbc/fnbl, fnbl.xml is read; for jdbc/fnbl/core, core.xml is read)
     *
     * @param name the datasource name
     * @return the datasource configuration always not null.
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs
     */
    public static DataSourceConfiguration getJDBCDataSourceConfiguration(String name)
    throws DataSourceConfigurationException {
        DataSourceConfiguration defaultConfiguration = getDBConfiguration();
        return getDataSourceConfiguration(JDBC_CONFIGURATION_DIRECTORY, name, defaultConfiguration);
    }

    /**
     * Returns the common DataSourceConfiguration reading <code>COMMON_CONFIGURATION_FILE</code>.
     * If the file is not found, an empty (not null) DataSourceConfiguration is
     * returned.
     * @return the common DataSourceConfiguration reading <code>COMMON_CONFIGURATION_FILE</code>.
     * If the file is not found, an empty (not null) DataSourceConfiguration is
     * returned.
     * @throws DataSourceConfigurationException if an error occurs
     */
    public static DataSourceConfiguration getDBConfiguration()
    throws DataSourceConfigurationException {

        String configPath = getConfigDirectory();

        File dbFile = new File(configPath, DB_CONFIGURATION_FILE);

        if (!dbFile.exists()) {
            //
            // Return an empty DataSourceConfiguration
            //
            return new DataSourceConfiguration();
        }

        BeanTool beanTool = BeanTool.getBeanTool(configPath );
        DataSourceConfiguration configuration = null;

        try {
            configuration = (DataSourceConfiguration) beanTool.getBeanInstance(DB_CONFIGURATION_FILE);
        } catch (BeanException ex) {
            throw new DataSourceConfigurationException("Error reading '" + DB_CONFIGURATION_FILE + "'", ex);
        }
        return configuration;
    }


    /**
     * Returns a map with the name and the datasource configuration for all
     * xml files under <code>JDBC_CONFIGURATION_DIRECTORY</code>
     * (any file is a datasource).
     * @return a map with the name and the datasource configuration for all
     *         xml files under the given directory (any file is a datasource)
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs loading the configurations
     */
    public static Map<String, DataSourceConfiguration> getJDBCDataSourceConfigurations()
    throws DataSourceConfigurationException {
        
        DataSourceConfiguration defaultConfiguration = getDBConfiguration();
        return getDataSourceConfigurations(JDBC_CONFIGURATION_DIRECTORY, defaultConfiguration);
    }


    /**
     * Merges the given DataSourceConfigurations.
     * The values in the second one overwrite the first ones.
     * <br/>
     * If at least one of the DataSourceConfiguration is a RoutingDataSource, the
     * result is a RoutingDataSource
     * @param configuration1 the first configuration
     * @param configuration2 the second configuration
     * @return the DataSourceConfiguration with all the values
     */
    public static DataSourceConfiguration mergeConfiguration(
            DataSourceConfiguration configuration1,
            DataSourceConfiguration configuration2) {

        DataSourceConfiguration configuration = null;

        if (configuration1 instanceof RoutingDataSourceConfiguration ||
            configuration2 instanceof RoutingDataSourceConfiguration) {
            configuration = new RoutingDataSourceConfiguration();
        } else {
            configuration = new DataSourceConfiguration();
        }

        if (configuration1 instanceof RoutingDataSourceConfiguration) {
            ((RoutingDataSourceConfiguration)configuration).setPartitioningCriteria(
                    ((RoutingDataSourceConfiguration)configuration1).getPartitioningCriteria()
            );
            ((RoutingDataSourceConfiguration)configuration).setPartitionConfigurationLoader(
                    ((RoutingDataSourceConfiguration)configuration1).getPartitionConfigurationLoader()
            );
        }

        if (configuration1 != null) {
            Properties prop1 = configuration1.getProperties();
            if (prop1 != null) {
                Enumeration propEnum1 = prop1.propertyNames();
                while (propEnum1.hasMoreElements()) {
                    String name = (String)propEnum1.nextElement();
                    String value = prop1.getProperty(name);
                    configuration.setProperty(name, value);
                }
            }
        }

        if (configuration2 != null) {
            Properties prop2 = configuration2.getProperties();
            if (prop2 != null) {
                Enumeration propEnum2 = prop2.propertyNames();
                while (propEnum2.hasMoreElements()) {
                    String name = (String)propEnum2.nextElement();
                    String value = prop2.getProperty(name);
                    configuration.setProperty(name, value);
                }
            }
        }

        if (configuration2 instanceof RoutingDataSourceConfiguration) {
            ((RoutingDataSourceConfiguration)configuration).setPartitioningCriteria(
                    ((RoutingDataSourceConfiguration)configuration2).getPartitioningCriteria()
            );

            ((RoutingDataSourceConfiguration)configuration).setPartitionConfigurationLoader(
                    ((RoutingDataSourceConfiguration)configuration2).getPartitionConfigurationLoader()
            );

        }

        return configuration;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns the configuration file for the datasource with the given name.
     * @param directory the directory that should contain the file
     * @param name the datasource name
     * @return the file name obtained concatenating
     *         <code>directory + File.separator + name + .xml</code>
     */
    private static String getConfigFile(String directory, String name) {
        if (name == null || "".equals(name)) {
            return null;
        }
        File f = new File(directory + File.separator + name + ".xml");
        return f.getPath();
    }

    private static String getConfigDirectory() {
        String configPath = System.getProperty("funambol.home");

        if (configPath == null) {
            configPath = System.getProperty("funambol.ds.home");
        }
        return configPath + "/config";
    }

    /**
     * Returns the given file name without extension if and only if the file is
     * an xml (case insensitive) file. If the file is not a .xml file, null is
     * returned.
     *
     * @param fileName the fileName
     * @return the given file name without extension if and only if the file is
     * an .xml (case insensitive) file. If the file is not a .xml file, null is
     * returned.
     */
    private static String stripXMLExtension(String fileName) {
        if (fileName == null) {
            return null;
        }
        String lowFileName = fileName.toLowerCase();
        if (lowFileName.endsWith(".xml")) {
            return fileName.substring(0, lowFileName.length() - 4);
        }
        return null;
    }


    /**
     * Returns the DataSourceConfiguration for the datasource identified with the
     * given name, reading the file with the same name of the
     * datasource (for jdbc/fnbl, fnbl.xml is read; for jdbc/fnbl/core, core.xml is read)
     *
     * @param directory the directory where to read the configuration file
     * @param name the datasource name
     * @param defaultConfiguration the default configuration that the caller
     * suggests. It is merged with the requested datasource configuration
     * @return the datasource configuration always not null.
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs
     */
    private static DataSourceConfiguration getDataSourceConfiguration(
            String directory, String name, DataSourceConfiguration defaultConfiguration)
    throws DataSourceConfigurationException {

        DataSourceConfiguration dataSourceConfiguration = null;

        String configurationFile = getConfigFile(directory, name);

        String configPath = getConfigDirectory();

        File configFile = new File(configPath, configurationFile);
        if (!configFile.exists()) {
            //
            // Returing a copy of the defaultConfiguration
            //
            DataSourceConfiguration configuration = defaultConfiguration.clone();
            return configuration;
        }

        BeanTool beanTool = BeanTool.getBeanTool(configPath );
        try {
            dataSourceConfiguration = (DataSourceConfiguration) beanTool.getBeanInstance(configurationFile);
        } catch (BeanException ex) {
            throw new DataSourceConfigurationException("Error reading '" + configurationFile + "'", ex);
        }

        DataSourceConfiguration configuration =
            mergeConfiguration(defaultConfiguration, dataSourceConfiguration);

        return configuration;
    }

    /**
     * Returns a map with the name and the datasource configuration for all
     * xml files under the given directory (any file is a datasource)
     * @param directory the directory where to look for datasource configurations
     * @param defaultConfiguration the default configuration that the caller
     * suggests. It is merged with the requested datasource configuration
     * @return a map with the name and the datasource configuration for all
     *         xml files under the given directory (any file is a datasource)
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs loading the configurations
     */
    private static Map<String, DataSourceConfiguration> getDataSourceConfigurations(
            String directory, DataSourceConfiguration defaultConfiguration)
    throws DataSourceConfigurationException {

        Map<String, DataSourceConfiguration> configurations = new HashMap();

        File configDir = new File(getConfigDirectory(), directory);
        File[] list = configDir.listFiles();
        if (list == null) {
            return configurations;
        }
        for (File file : list) {
            String datasourceName = null;
            String fileName = null;
            DataSourceConfiguration configuration = null;
            if (file.isFile()) {
                fileName = file.getName();
                if (fileName.toLowerCase().endsWith(".xml")) {
                    datasourceName = fileName.substring(0, fileName.length() - 4);
                    configuration = getDataSourceConfiguration(directory, datasourceName, defaultConfiguration);
                    configurations.put(datasourceName, configuration);
                }
            }
        }
        return configurations;
    }

    /**
     * Returns all xml files under the given directory (any file is a datasource)
     * @return an array with all the xml files under the given directory (any file is a datasource)
     */
    private static String[] getConfigurationFiles(String directory) {

        List fileList = new ArrayList();

        File configDir = new File(getConfigDirectory(), directory);
        File[] list = configDir.listFiles();
        if (list == null) {
            return new String[0];
        }
        for (File file : list) {
            if (file.isFile()) {
                if (file.getName().toLowerCase().endsWith(".xml")) {
                    fileList.add(file.getName());
                }
            }
        }
        return (String[])fileList.toArray(new String[fileList.size()]);
    }

}
