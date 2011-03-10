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
package com.funambol.framework.cluster;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a cluster configuration. Used to initialized a cluster
 * @version $Id: ClusterConfiguration.java 36592 2011-02-01 09:26:08Z nichele $
 */
public class ClusterConfiguration {

    // -------------------------------------------------------------- Properties
    /** The configuration properties */
    private String properties;

    public String getProperties() {
        return properties;
    }

    public void setProperties(String properties) {
        this.properties = properties;
    }

    /** The configuration file */
    private String configurationFile;

    public String getConfigurationFile() {
        return configurationFile;
    }

    public void setConfigurationFile(String configurationFile) {
        this.configurationFile = configurationFile;
    }

    /** The configuration file */
    private String clusterName;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of ClusterConfiguration */
    public ClusterConfiguration() {
    }

    /**
     * Creates a new instance of ClusterConfiguration
     * @param clusterName the cluster name
     * @param configFile the configuration file to use creating the cluster
     */
    public ClusterConfiguration(String clusterName, File configFile) {
        if (configFile != null) {
            this.configurationFile = configFile.getPath();
        } else {
            this.configurationFile = null;
        }
        this.clusterName = clusterName;
    }

    /**
     * Creates a new instance of ClusterConfiguration
     * @param clusterName the cluster name
     * @param properties the properties to use creating the cluster
     */
    public ClusterConfiguration(String clusterName, String properties) {
        this.clusterName = clusterName;
        this.properties  = properties;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Validates the configuration checking if:
     * <ui>
     * <li>the cluster name is not null</li>
     * <li>the configuration file of the properties are not null</li>
     * <li>if the configuration file is not null, it must exist</li>
     * </ui>
     * @throws ClusterException if the configuration file is not null but doesn't exist
     *         or if the configuration file and the properties are null.
     */
    public void validateConfiguration() throws ClusterException {

        if (getClusterName() == null) {
            throw new ClusterException("The cluster name must be not null");
        }

        if (getConfigurationFile() != null) {

            File configFile = new File(getConfigurationFile());
            if (!configFile.exists()) {
                throw new ClusterException("The configuration file '" +
                                           configFile.getAbsolutePath() + "' doesn't exist");
            }
        }

        if (getProperties() == null && getConfigurationFile() == null) {
            throw new ClusterException("Both properties string and configuration file are null");
        }

    }

    /**
     * A string representation of this ClusterConfiguration
     *
     * @return a string representation of this ClusterConfiguration
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("clusterName"      , clusterName);
        sb.append("properties"       , properties);
        sb.append("configurationFile", configurationFile);
        return sb.toString();
    }
}
