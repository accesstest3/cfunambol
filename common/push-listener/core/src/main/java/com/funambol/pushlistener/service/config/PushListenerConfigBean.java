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

package com.funambol.pushlistener.service.config;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

import org.apache.log4j.Logger;

import com.funambol.framework.cluster.ClusterConfiguration;

import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.pushlistener.service.ws.WSServerInformation;

import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * Configuration parameters for a PushListener instance.
 * @version $Id: PushListenerConfigBean.java,v 1.16 2008-05-18 16:23:42 nichele Exp $
 */
public class PushListenerConfigBean implements LazyInitBean {

    // --------------------------------------------------------------- Constants
    /** The default max thread pool size */
    public static final int DEFAULT_MAX_THREAD_POOL_SIZE = 20;

    /** The default task period tolerance */
    public static final double DEFAULT_TASK_PERIOD_TOLERANCE = 0.1;

    /** The default polling time of the health thread (10 minutes) */
    public static final long DEFAULT_HEALTH_THREAD_POLLING_TIME = 600000;

    /** The default polling time of the registry monitor (1 minute) */
    public static final long DEFAULT_REGISTRY_MONITOR_POLLING_TIME = 60000;

    /** The default registry table name*/
    public static final String DEFAULT_REGISTRY_TABLE_NAME = "fnbl_push_listener_registry";

    /** The default registry entries idSpace */
    public static final String DEFAULT_REGISTRY_ENTRIES_ID_SPACE = "pushlistener.id";

    public static final String DEFAULT_PUSH_LISTENER_PLUGIN_DIRECTORY =
            "com/funambol/pushlistener/plugin";

    // ------------------------------------------------------------ Private data
    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.pushlistener.config");

    /**
     * The max number of threads in the pool
     */
    private int maxThreadPoolSize = -1;

    public int getMaxThreadPoolSize() {
        return maxThreadPoolSize;
    }

    public void setMaxThreadPoolSize(int maxThreadPoolSize) {
        this.maxThreadPoolSize = maxThreadPoolSize;
    }

    /**
     * The HealthThread polling time
     */
    private long healthThreadPollingTime = -1;

    public long getHealthThreadPollingTime() {
        return healthThreadPollingTime;
    }

    public void setHealthThreadPollingTime(long pollingTime) {
        this.healthThreadPollingTime = pollingTime;
    }

    /**
     * The RegistryMonitor polling time
     */
    private long registryMonitorPollingTime = -1;

    public long getRegistryMonitorPollingTime() {
        return registryMonitorPollingTime;
    }

    public void setRegistryMonitorPollingTime(long pollingTime) {
        this.registryMonitorPollingTime = pollingTime;
    }

    /**
     * The tolerance accepted on the task period
     */
    private double taskPeriodTolerance = -1;

    public double getTaskPeriodTolerance() {
        return taskPeriodTolerance;
    }

    public void setTaskPeriodTolerance(double periodTolerance) {
        this.taskPeriodTolerance = periodTolerance;
    }

    /**
     * The registry table
     */
    private String registryTableName;

    public String getRegistryTableName() {
        return registryTableName;
    }

    public void setRegistryTableName(String registryTableName) {
        this.registryTableName = registryTableName;
    }

    /**
     * The registry entries idspace
     */
    private String registryEntriesIdSpace = null;

    public String getRegistryEntriesIdSpace() {
        return registryEntriesIdSpace;
    }

    public void setRegistryEntriesIdSpace(String registryEntriesIdSpace) {
        this.registryEntriesIdSpace = registryEntriesIdSpace;
    }

    /**
     * The directory containing the PushListenerPlugin bean files
     */
    private String pluginDirectory;

    public String getPluginDirectory() {
        return pluginDirectory;
    }

    public void setPluginDirectory(String pluginDirectory) {
        this.pluginDirectory = pluginDirectory;
    }

    /**
     * The WSServerInformation to use in the ws access.
     * @deprecated From version 7.0.6, replaced by {@link #serverInformation}
     */
    @Deprecated
    private WSServerInformation wsServerInformation;

    /**
     * @deprecated use {@link #getServerInformation}
     */
    @Deprecated
    public WSServerInformation getWSServerInformation() {
        return wsServerInformation;
    }

    /**
     * @deprecated use {@link #setServerInformation}
     */
    @Deprecated
    public void setWSServerInformation(WSServerInformation wsServerInformation) {
        this.wsServerInformation = wsServerInformation;
    }

    /**
     * The ServerInformation to use in the ws access.
     */
    private ServerInformation serverInformation;

    public ServerInformation getServerInformation() {
        return serverInformation;
    }

    public void setServerInformation(ServerInformation serverInformation) {
        this.serverInformation = serverInformation;
    }

    /**
     * The cluster configuration to use creating a cluster in an HA environment
     * (it can be null)
     */
    private ClusterConfiguration clusterConfiguration;

    public ClusterConfiguration getClusterConfiguration() {
        return clusterConfiguration;
    }

    public void setClusterConfiguration(ClusterConfiguration clusterConfiguration) {
        this.clusterConfiguration = clusterConfiguration;
    }


    // ------------------------------------------------------------ Constructors
    /**
     * Constructor
     */
    public PushListenerConfigBean() {
    }

    // ---------------------------------------------------------- Public methods

    public void init() {
    }

    /**
     * Called by PushListenerConfiguration to say to the bean the config path.
     * In this waym the config bean can arrange relative path.
     * @param configPath the configuration path
     */
    void fixConfigPath(String configPath) {
        //
        // We updated the clusterConfiguration objecct in order to have the
        // cluster configuration file under config dir
        //
        if (clusterConfiguration != null) {
            if (clusterConfiguration.getConfigurationFile() != null) {
                File file = new File(clusterConfiguration.getConfigurationFile());
                String filePath = file.getPath();
                if (!file.isAbsolute()) {
                    File newFile = new File(configPath + File.separator + filePath);
                    clusterConfiguration.setConfigurationFile(newFile.getPath());
                }
            }
        }
    }

    /**
     * Checks the config bean values
     * @throws PushListenerConfigurationException if the configuration object is
     *         not valid
     */
    protected void validateConfigBean() throws PushListenerConfigurationException {

        if (serverInformation == null) {
            throw new PushListenerConfigurationException("The configuration bean doesn't " +
                "contain an instance of a ServerInformation class");
        }

        if (getMaxThreadPoolSize() == -1) {
            log.warn("The configuration bean " +
                     "doesn't contain the maxThreadPoolSize...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_MAX_THREAD_POOL_SIZE);

            setMaxThreadPoolSize(DEFAULT_MAX_THREAD_POOL_SIZE);
        }

        if (getTaskPeriodTolerance() == -1) {
            log.warn("The configuration bean " +
                     "doesn't contain the taskPeriodTolerance...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_TASK_PERIOD_TOLERANCE);

            setTaskPeriodTolerance(DEFAULT_TASK_PERIOD_TOLERANCE);
        }

        if (getHealthThreadPollingTime() == -1) {
            log.warn("The configuration bean " +
                     "doesn't contain the healthThreadPollingTime...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_HEALTH_THREAD_POLLING_TIME);

            setHealthThreadPollingTime(DEFAULT_HEALTH_THREAD_POLLING_TIME);
        }

        if (getRegistryMonitorPollingTime() == -1) {
            log.warn("The configuration bean " +
                     "doesn't contain the registryMonitorPollingTime...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_REGISTRY_MONITOR_POLLING_TIME);

            setRegistryMonitorPollingTime(DEFAULT_REGISTRY_MONITOR_POLLING_TIME);
        }

        if (getRegistryTableName() == null ||
            getRegistryTableName().length() == 0) {
            log.warn("The configuration bean " +
                     "doesn't contain the registryTableName...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_REGISTRY_TABLE_NAME);

            setRegistryTableName(DEFAULT_REGISTRY_TABLE_NAME);
        }

        if (getRegistryEntriesIdSpace() == null ||
            getRegistryEntriesIdSpace().length() == 0) {
            log.warn("The configuration bean " +
                     "doesn't contain the registryEntriesIdSpace...using the default " +
                     "value: " + PushListenerConfigBean.DEFAULT_REGISTRY_ENTRIES_ID_SPACE);

            setRegistryEntriesIdSpace(DEFAULT_REGISTRY_ENTRIES_ID_SPACE);
        }

        if (getPluginDirectory() == null ||
            getPluginDirectory().length() == 0) {
            log.warn("The configuration bean " +
                     "doesn't contain the pluginDirectory...using default " +
                     "value: " + PushListenerConfigBean.DEFAULT_PUSH_LISTENER_PLUGIN_DIRECTORY);

            setPluginDirectory(DEFAULT_PUSH_LISTENER_PLUGIN_DIRECTORY);
        }

        if (getClusterConfiguration() != null) {
            try {
                getClusterConfiguration().validateConfiguration();
            } catch (Exception e) {
                throw new PushListenerConfigurationException("Error validating cluster configuration", e);
            }
        }
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("maxThreadPoolSize"          , maxThreadPoolSize);
        sb.append("taskPeriodTolerance"        , taskPeriodTolerance);
        sb.append("healthThreadPollingTime"    , healthThreadPollingTime);
        sb.append("registryMonitorPollingTime" , registryMonitorPollingTime);
        sb.append("registryTableName"          , registryTableName);
        sb.append("registryEntriesIdSpace"     , registryEntriesIdSpace);
        sb.append("pluginDirectory"            , pluginDirectory);
        sb.append("wsServerInformation"        , wsServerInformation);
        sb.append("clusterConfiguration"       , clusterConfiguration);
        return sb.toString();
    }

    // --------------------------------------------------------- Private methods

}
