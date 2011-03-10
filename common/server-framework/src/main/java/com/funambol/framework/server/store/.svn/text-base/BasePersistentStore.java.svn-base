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

package com.funambol.framework.server.store;

import java.util.Map;

import java.sql.*;

import javax.naming.NamingException;
import javax.sql.DataSource;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.db.RoutingDataSource;


/**
 * This is a base class for <i>PersistenStore</i> objects. It does not persist
 * any object, but it provides services common to concrete implementations and
 * two different datasources to access to the core and to the user database
 *
 * @version $Id: BasePersistentStore.java,v 1.4 2008-05-22 13:17:43 nichele Exp $
 *
 */
public abstract class BasePersistentStore implements LazyInitBean {

    // --------------------------------------------------------------- Constants

    public static final String CONFIG_JNDI_DATA_SOURCE_NAME = "jndi-data-source-name";

    /** Logger */
    protected transient final FunambolLogger log = FunambolLoggerFactory.getLogger();

    /** Core datasource jndi name */
    private static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";

    /** User datasource jndi name */
    private static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";

    // -------------------------------------------------------------- Properties

    /**
     * The JNDI name of the datasource to be used
     * @deprecated Since v66, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */
    protected String jndiDataSourceName = null;

    /**
     * @deprecated Since v66, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */
    public String getJndiDataSourceName() {
        return this.jndiDataSourceName;
    }

    /**
     * @deprecated Since v66, the jndiDataSourceNames used are jdbc/fnblcore and
     * jdbc/fnbluser and they are hardcoded
     */
    public void setJndiDataSourceName(String jndiDataSourceName) throws PersistentStoreException {
        this.jndiDataSourceName = jndiDataSourceName;

        if (jndiDataSourceName == null) {
            dataSource = null;
        }

        try {
            dataSource = DataSourceTools.lookupDataSource(jndiDataSourceName);
        } catch (NamingException e) {
            throw new PersistentStoreException("Data source "
            + jndiDataSourceName
            + " not found"
            , e
            );
        }

        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            sqlHelperClause = new SQLHelperClause(conn.getMetaData().getDatabaseProductName());
        } catch(SQLException e) {
            log.error("Error getting connection", e);
            throw new PersistentStoreException("Error getting connection (" +
                                               e.getMessage() + ")", e);
        } finally {
            DBTools.close(conn, null, null);
        }
    }

    // ---------------------------------------------------------- Protected data

    protected transient DataSource        dataSource     = null;
    protected transient DataSource        coreDataSource = null;
    protected transient RoutingDataSource userDataSource = null;

    protected SQLHelperClause sqlHelperClause = null;

    // ------------------------------------------------------------ Constructors

    public BasePersistentStore() {
    }
    // ---------------------------------------------------------- Public methods

    /** Configures the persistent store
     *
     * @param config an <i>Map</i> containing configuration parameters.
     *
     * @throws ConfigPersistentStoreException
     *
     */
    public void configure(Map config) throws ConfigPersistentStoreException {

        checkConfigParams(config);

        if (config != null) {
            if ((String) config.get(CONFIG_JNDI_DATA_SOURCE_NAME) != null) {
                try {
                    setJndiDataSourceName((String) config.get(CONFIG_JNDI_DATA_SOURCE_NAME));
                } catch (PersistentStoreException e) {
                    throw new ConfigPersistentStoreException( "Error creating the datasource: "
                                                            + e.getMessage()
                                                            , e
                                                            );
                }
            }
        }
    }

    public void init() throws BeanInitializationException {
        try {
            coreDataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);
        } catch (Exception e){
            throw new BeanInitializationException("Error looking up datasource '" +
                                                  CORE_DATASOURCE_JNDINAME + "'", e);
        }

        try {
            userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);
        } catch (Exception e){
            throw new BeanInitializationException("Error looking up datasource '" +
                                                  USER_DATASOURCE_JNDINAME + "'", e);
        }  

        //
        // The sqlHelperClause could be not null if setJndiDataSourceName is not called.
        // (since v66 it is deprecated)
        // 
        if (sqlHelperClause == null) {
            Connection conn = null;
            try {
                conn = coreDataSource.getConnection();
                sqlHelperClause = new SQLHelperClause(conn.getMetaData().getDatabaseProductName());
            } catch(SQLException e) {
                log.error("Error getting connection", e);
                throw new BeanInitializationException("Error getting connection (" +
                                                      e.getMessage() + ")", e);
            } finally {
                DBTools.close(conn, null, null);
            }
        }
    }


    // ------------------------------------------------------- Protected methods

    // ------------------------------------------------------- Protected methods

    // --------------------------------------------------------- Private methods

    /**
     * Checks if the given configuration parameters contain all required
     * parameters. If not a <i>ConfigPersistentStoreException</i> is thrown.
     *
     * @param config the <i>Map</i> containing the configuration parameters
     *
     * @throws ConfigPersistentStoreException in case of missing parameters
     */
    private void checkConfigParams(Map config)
    throws ConfigPersistentStoreException {
        //
        // Nothing to check
        // 
    }
}

