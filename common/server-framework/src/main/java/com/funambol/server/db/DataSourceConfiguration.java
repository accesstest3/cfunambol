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

import java.util.Enumeration;
import java.util.Properties;

/**
 * Contains all the properties that should be used to configure a DataSource
 * @version $Id: DataSourceConfiguration.java,v 1.4 2008-06-14 14:38:21 nichele Exp $
 */
public class DataSourceConfiguration implements Cloneable {

    // --------------------------------------------------------------- Constants

    public static final String PROPERTY_URL               = "url";
    public static final String PROPERTY_USERNAME          = "username";
    public static final String PROPERTY_PASSWORD          = "password";
    public static final String PROPERTY_DRIVER_CLASS_NAME = "driverClassName";


    // ------------------------------------------------------------ Private data
    protected Properties configurationProperties = new Properties();

    // ------------------------------------------------------------ Constructors

    public DataSourceConfiguration() {
    }

    public DataSourceConfiguration(Properties prop) {
        this.configurationProperties = prop;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Returns the properties to use configuring a DataSource
     * @return the properties to use configuring a DataSource
     */
    public Properties getProperties() {
        return (Properties) configurationProperties.clone();
    }

    /**
     * Returns the basic properties (url, username, password, driverClassName)
     * to use configuring a DataSource
     * @return the basic properties to use configuring a DataSource
     */
    public Properties getBasicProperties() {
        Properties basicProperties = new Properties();
        basicProperties.setProperty(PROPERTY_URL,
                                    configurationProperties.getProperty(PROPERTY_URL));
        basicProperties.setProperty(PROPERTY_USERNAME,
                                    configurationProperties.getProperty(PROPERTY_USERNAME));
        basicProperties.setProperty(PROPERTY_PASSWORD,
                                    configurationProperties.getProperty(PROPERTY_PASSWORD));
        basicProperties.setProperty(PROPERTY_DRIVER_CLASS_NAME,
                                    configurationProperties.getProperty(PROPERTY_DRIVER_CLASS_NAME));

        return basicProperties;
    }

    /**
     * Returns the property with the given name
     * @param key the property name
     * @return the property value or null if not found
     */
    public String getProperty(String key) {
        return configurationProperties.getProperty(key);
    }

    /**
     * Sets a property
     * @param key the property name
     * @param value the property value
     */
    public void setProperty(String key, String value) {
        configurationProperties.setProperty(key, value);
    }

    /**
     * Returns an enumeration of all the properties in this configuration
     * @return an enumeration of all the keys in this property list
     */
    public Enumeration propertyNames() {
        return configurationProperties.propertyNames();
    }

    /**
     * Clears this configuration so that it contains no keys.
     */
    public void clear() {
        configurationProperties.clear();
    }

    @Override
    public DataSourceConfiguration clone() {
        if (configurationProperties == null) {
            return null;
        }
        return new DataSourceConfiguration((Properties) configurationProperties.clone());
    }

    @Override
    public boolean equals(Object o) {
        return configurationProperties.equals(o);
    }

    @Override
    public int hashCode() {
        return configurationProperties.hashCode();
    }
}