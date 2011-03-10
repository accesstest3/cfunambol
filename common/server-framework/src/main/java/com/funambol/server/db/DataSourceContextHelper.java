/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.management.ObjectName;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;

import org.apache.commons.modeler.Registry;

import org.apache.log4j.Logger;


/**
 * It reads all the datasource configuration files, and for any of them, it creates/
 * configures/binds a datasource using as jndiname the path of the configuration file
 *
 * @version $Id: DataSourceContextHelper.java,v 1.4 2008-06-14 09:31:42 nichele Exp $
 */
public class DataSourceContextHelper {


    //------------------------------------------------------------- Private data
    private static final Logger logger = Logger.getLogger("funambol.server.datasource");

    /**
     * Contains all the configured datasources
     */
    private static Map<String, DataSource> dataSources = null;

    // ---------------------------------------------------------- Public methods

    /**
     * It reads all the datasource configurations, and for any of them, it creates/
     * configures/binds a datasource using as jndiname the path of the configuration file
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs reading configurations
     */
    public static void configureAndBindDataSources()
    throws DataSourceConfigurationException {
        
        configureAndBindDataSources(false); // no simple usage
    }


    /**
     * It reads all the datasource configurations, and for any of them, it creates/
     * configures/binds a datasource using as jndiname the path of the configuration file.
     * <br/>
     * Note that this method is usefull in all the cases we need to use the server
     * datasources without concerns about tuning configuration. Indeed just the
     * following configuration parameters are used:
     * <ul>
     * <li>url</li>
     * <li>username</li>
     * <li>password</li>
     * <li>driverClassName</li>
     * </ul>
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs reading configurations
     */
    public static void configureAndBindDataSourcesForSimpleUsage()
    throws DataSourceConfigurationException {

        configureAndBindDataSources(true); // simple usage
    }


    /**
     * Closing all datasources
     */
    public static void closeDataSources() {
        if (dataSources != null) {
            for (String dsName : dataSources.keySet()) {
                try {
                    if (dataSources.get(dsName) instanceof BasicDataSource) {
                        ((BasicDataSource)dataSources.get(dsName)).close();
                    }
                } catch (Throwable t) {
                    //
                    // nothing to do
                    //
                }
            }
        }
    }

    // --------------------------------------------------------- Private methods
    /**
     * Binds the given object with the given name
     * @param obj the object to bind
     * @param jndiName the name
     * @throws javax.naming.NamingException if an error occurs
     */
    private static void bind(Object obj, String jndiName) throws NamingException {
        InitialContext context = new InitialContext();
        createSubcontexts(context, jndiName);
        context.bind(jndiName, obj);
    }

    /**
     * Create all intermediate subcontexts.
     */
    private static void createSubcontexts(javax.naming.Context ctx, String name)
    throws NamingException {
        javax.naming.Context currentContext = ctx;
        StringTokenizer tokenizer = new StringTokenizer(name, "/");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            if ((!token.equals("")) && (tokenizer.hasMoreTokens())) {
                try {
                    currentContext = currentContext.createSubcontext(token);
                } catch (NamingException e) {
                    // Silent catch. Probably an object is already bound in
                    // the context.
                    currentContext =
                        (javax.naming.Context) currentContext.lookup(token);
                }
            }
        }
    }

    /**
     * Registers the given DataSouce as a MBean
     * @param ds the datasource
     * @param name the name
     * @throws java.lang.Exception if an error occurs
     */
    private static void registerMBean(DataSource ds, String name) throws Exception {
        ObjectName on = new ObjectName("com.funambol:type=DataSource,name=" + name);
        Registry.getRegistry(null, null).registerComponent(ds, on, null);
    }

    /**
     * It reads all the datasource configurations, and for any of them, it creates/
     * configures/binds a datasource using as jndiname the path of the configuration file.
     * <br/>
     * If simpleUsage is true, just the following configuration parameters are used:
     * <ul>
     * <li>url</li>
     * <li>username</li>
     * <li>password</li>
     * <li>driverClassName</li>
     * </ul>
     * @param simpleUsage if true, just url, username, password and driverClassName
     * are used.
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs reading configurations
     */
    private static void configureAndBindDataSources(boolean simpleUsage)
    throws DataSourceConfigurationException {

        dataSources = new HashMap();

        //
        // 1. reading all configurations
        //
        Map<String, DataSourceConfiguration> configurations =
                DataSourceConfigurationHelper.getJDBCDataSourceConfigurations();

        if (configurations == null || configurations.size() == 0) {
            //
            // no datasource to configure
            //
            if (logger.isTraceEnabled()) {
                logger.trace("No datasource to configure");
            }
            return;
        }

        Iterator<String> itDataSourceName = configurations.keySet().iterator();
        while (itDataSourceName.hasNext()) {
            String dataSourceName = itDataSourceName.next();
            DataSourceConfiguration configuration = configurations.get(dataSourceName);
            DataSource ds = null;
            //
            // 2. creating the datasource
            //
            if (configuration != null) {

                try {
                    Properties prop = null;

                    if (simpleUsage) {
                        prop = configuration.getBasicProperties();
                    } else {
                        prop = configuration.getProperties();
                    }
                        
                    if (configuration instanceof RoutingDataSourceConfiguration) {
                        //
                        // A RoutingDataSource must be created.
                        //
                        ds = new RoutingDataSource(
                                (RoutingDataSourceConfiguration)configuration, simpleUsage
                             );                        
                        //
                        // note that we can not call the RoutingDataSource.init and
                        // RoutingDataSource.configure since they could use the jdbc/fnblcore that maybe
                        // is not configured yet. See RoutingDataSource.getRoutedConnection. Those
                        // methods are called there.
                        //
                    } else {
                        //
                        // No routing datasource
                        //
                        ds = BasicDataSourceFactory.createDataSource(prop);
                    }
                } catch (Exception ex) {
                    logger.error("Error creation the datasource 'jdbc/" + dataSourceName + "'", ex);
                    continue;
                }
            }

            dataSources.put("jdbc/" + dataSourceName, ds);

            //
            // 3. binding datasource
            //
            try {
                bind(ds, "jdbc/" + dataSourceName);
            } catch (Exception e) {
                logger.error("Error binding the datasource 'jdbc/" + dataSourceName + "'", e);
                continue;
            }

            //
            // 4. registering the MBean
            //
            try {
                registerMBean(ds, "jdbc/" + dataSourceName);
            } catch (Exception e) {
                logger.error("Error registering MBeam for the datasource 'jdbc/" + dataSourceName + "'", e);
                continue;
            }
        }

    }
    
}