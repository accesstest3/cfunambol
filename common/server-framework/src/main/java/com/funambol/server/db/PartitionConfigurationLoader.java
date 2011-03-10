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

import java.util.Map;

/**
 * Represents a component that can be used to obtain the DataSourceConfiguration
 * object to access to the partitions.
 * It declares two methods:
 * <ul>
 * <li><code>init</code>: called to initialize the instance. It's called just once</li>
 * <li><code>getPartitionConfigurations</code>: called to obtain the configurations.
 * This method can be call more than once and the expected behavior is that the configuration
 * must be always reload (not cached) since it's called also in reconfiguring the 
 * RoutingDataSource</li>
 * <ul>
 *
 * @version $Id: PartitionConfigurationLoader.java,v 1.4 2008-06-15 16:17:01 nichele Exp $
 */
public interface PartitionConfigurationLoader {

    /**
     * Returns a map that contains for each well known partion, the name and the
     * <code>DataSourceConfiguration</code> to use to create a DataSource to access
     * to the partition.
     * <br/>
     * <b>IMPORTANT:</b> This method can be called more than once and the expected behavior
     * is that the configuration  must be always reload (not cached) since it's 
     * called also in reconfiguring the RoutingDataSource
     *
     * @param defaultDataSourceConfiguration the default configuration that the caller
     * suggests to the PartitionConfigurationLoader. Note that the usage of
     * this default configuration is up to the PartitionConfigurationLoader implementation.
     * It can be null.
     * @return  a map that contains for each well known partion, the name and the
     * <code>DataSourceConfiguration</code> to use to create a DataSource to access
     * to the partition.
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error
     *         occurs obtaining the configuration
     */
    public Map<String, DataSourceConfiguration> getPartitionConfigurations(
            DataSourceConfiguration defaultDataSourceConfiguration)
    throws DataSourceConfigurationException;

    /**
     * Called to initialize the instance.
     * All the getPartition calls are performed after the init()
     * @throws com.funambol.server.db.DataSourceConfigurationException if an error occurs
     */
    public void init() throws DataSourceConfigurationException;

}
