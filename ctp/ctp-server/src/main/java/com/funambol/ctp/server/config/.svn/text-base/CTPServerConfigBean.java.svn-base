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

package com.funambol.ctp.server.config;

import java.io.File;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.cluster.ClusterConfiguration;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * Configuration parameters for a CTPServer instance.
 * @version $Id: CTPServerConfigBean.java,v 1.10 2007-11-28 11:26:16 nichele Exp $
 */
public class CTPServerConfigBean implements LazyInitBean {

    /**
     * Constructor
     */
    public CTPServerConfigBean() {
    }

    // -------------------------------------------------------------- Properties

    /**
     * The port number
     */
    private int portNumber = -1;

    public int getPortNumber() {
        return portNumber;
    }

    public void setPortNumber(int portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * The size of the buffer used by mina to read the data sent by the client
     */
    private int receiveBufferSize = 0;

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    /**
     * The number of the threads used to accept connections
     */
    private int connectionAcceptorThreadPoolSize = 0;

    public int getConnectionAcceptorThreadPoolSize() {
        return connectionAcceptorThreadPoolSize;
    }

    public void setConnectionAcceptorThreadPoolSize(int connectionAcceptorThreadPoolSize) {
        this.connectionAcceptorThreadPoolSize = connectionAcceptorThreadPoolSize;
    }

    /**
     * How many authentication attempts are allowed on a connection ?
     */
    private int maxAuthenticationRetries = 1;

    public int getMaxAuthenticationRetries() {
        return maxAuthenticationRetries;
    }

    public void setMaxAuthenticationRetries(int maxAuthenticationRetries) {
        this.maxAuthenticationRetries = maxAuthenticationRetries;
    }

    /**
     *  The JGroups configuration file for the notification group
     */
    private String notificationGroupConfigFileName;

    public String getNotificationGroupConfigFileName() {
        return notificationGroupConfigFileName;
    }

    public void setNotificationGroupConfigFileName(String notificationGroupConfigFileName) {
        this.notificationGroupConfigFileName = notificationGroupConfigFileName;
    }

    /**
     *  The JGroups name of the notification group
     */
    private String notificationGroupName;

    public String getNotificationGroupName() {
        return notificationGroupName;
    }

    public void setNotificationGroupName(String notificationGroupName) {
        this.notificationGroupName = notificationGroupName;
    }

    /**
     * How often does Mina notify the ctp-server <code>minaIdleTime</code>s
     * for idle connection ?
     */
    private int minaIdleTime = -1;

    public int getMinaIdleTime() {
        return minaIdleTime;
    }

    public void setMinaIdleTime(int minaIdleTime) {
        this.minaIdleTime = minaIdleTime;
    }

    /**
     * How often should the client send the heartbeat command (READY) ?
     */
    private int clientHeartBeatExpectedTime = -1;

    public int getClientHeartBeatExpectedTime() {
        return clientHeartBeatExpectedTime;
    }

    public void setClientHeartBeatExpectedTime(int clientHeartBeatExpectedTime) {
        this.clientHeartBeatExpectedTime = clientHeartBeatExpectedTime;
    }

    /**
     * The serverInformation used to call ds-server webservices
     */
    private ServerInformation wsServerInformation;

    public ServerInformation getWSServerInformation() {
        return wsServerInformation;
    }

    public void setWSServerInformation(ServerInformation wsServerInformation) {
        this.wsServerInformation = wsServerInformation;
    }

    /**
     * The authentication manager bean file
     */
    private String authenticationManager;

    public String getAuthenticationManager() {
        return authenticationManager;
    }

    public void setAuthenticationManager(String authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    /**
     * The pending notification manager bean file
     */
    private String pendingNotificationManager;

    public String getPendingNotificationManager() {
        return pendingNotificationManager;
    }

    public void setPendingNotificationManager(String pendingNotificationManager) {
        this.pendingNotificationManager = pendingNotificationManager;
    }

    /**
     * Should the logging filter be enabled ?
     */
    private boolean loggingFilterEnabled = false;

    public boolean getLoggingFilterEnabled() {
        return loggingFilterEnabled;
    }

    public boolean isLoggingFilterEnabled() {
        return loggingFilterEnabled;
    }

    public void setLoggingFilterEnabled(boolean loggingFilterEnabled) {
        this.loggingFilterEnabled = loggingFilterEnabled;
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

    /**
     * The maximum number of open connections allowed
     */
    private int maxConnections = 20000;

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * LazyInitBean
     */
    public void init() throws BeanInitializationException {
        //
        // Nothing to init
        //
    }


    /**
     * Checks the config bean values
     * @throws CTPServerConfigurationException if the set values ar not valid
     */
    public void validate() throws CTPServerConfigurationException {

        if (portNumber <= 0) {
            throw new CTPServerConfigurationException("Invalid port number ("+
                portNumber + "), it must be greater than 0");
        }
        if (clientHeartBeatExpectedTime < 0) {
            throw new CTPServerConfigurationException("Invalid clientHeartBeatExpectedTime ("+
                clientHeartBeatExpectedTime + "), it must be greater or equal than 0");
        }
        if (receiveBufferSize <= 0) {
            throw new CTPServerConfigurationException("Invalid receiveBufferSize (" +
                receiveBufferSize + "), it must be greater than 0");
        }
        if (minaIdleTime <= 0) {
            throw new CTPServerConfigurationException("Invalid minaIdleTime (" +
                minaIdleTime + "), it must be greater than 0");
        }
        if (connectionAcceptorThreadPoolSize < 0) {
            throw new CTPServerConfigurationException("Invalid connectionAcceptorThreadPoolSize (" +
                connectionAcceptorThreadPoolSize + "), it must be greater or equal than 0");
        }
        if (maxAuthenticationRetries < 1) {
            throw new CTPServerConfigurationException("Invalid maxAuthenticationRetries (" +
                maxAuthenticationRetries + "), it must be greater than 1");
        }
        if (notificationGroupConfigFileName == null) {
            throw new CTPServerConfigurationException("Invalid notificationGroupConfigFileName, " +
                "it must be not null");
        }
        if (notificationGroupConfigFileName.length() == 0) {
            throw new CTPServerConfigurationException("Invalid notificationGroupConfigFileName, " +
                "it must be not empty");
        }
        if (notificationGroupName == null) {
            throw new CTPServerConfigurationException("Invalid notificationGroupName, " +
                "it must be not null");
        }
        if (notificationGroupName.length() == 0) {
            throw new CTPServerConfigurationException("Invalid notificationGroupName, " +
                "it must be not empty");
        }
        if (wsServerInformation == null) {
            throw new CTPServerConfigurationException("Invalid dsServerInformation, " +
                "it must be not null");
        }
        if (authenticationManager == null) {
            throw new CTPServerConfigurationException("Invalid authenticationManager, " +
                "it must be not null");
        }
        if (getClusterConfiguration() != null) {
            try {
                getClusterConfiguration().validateConfiguration();
            } catch (Exception e) {
                throw new CTPServerConfigurationException("Error validating cluster configuration", e);
            }
        }
        if (maxConnections < 1) {
            throw new CTPServerConfigurationException("Invalid maxConnections, " +
                "it must be greater than zero");
        }
    }

    /**
     * Called by CTPServerConfiguration to say to the bean the config path.
     * In this waym the config bean can arrange relative path.
     * @param configPath the configuration path
     */
    public void fixConfigPath(String configPath) {
        //
        // We updated the jgroups configuration files in order to have them
        // under config dir
        //
        if (notificationGroupConfigFileName != null) {
            File file = new File(notificationGroupConfigFileName);
            String filePath = file.getPath();
            if (!file.isAbsolute()) {
                File newFile = new File(configPath + File.separator + filePath);
                notificationGroupConfigFileName = newFile.getPath();
            }
        }

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
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("portNumber"                            , portNumber);
        sb.append("clientHeartBeatExpectedTime"           , clientHeartBeatExpectedTime);
        sb.append("connectionAcceptorThreadPoolSize"      , connectionAcceptorThreadPoolSize);
        sb.append("clusterConfiguration"                  , clusterConfiguration);
        sb.append("notificationGroupConfigFileName"       , notificationGroupConfigFileName);
        sb.append("notificationGroupName"                 , notificationGroupName);
        sb.append("loggingFilterEnabled"                  , loggingFilterEnabled);
        sb.append("receiveBufferSize"                     , receiveBufferSize);
        sb.append("minaIdleTime"                          , minaIdleTime);
        sb.append("WSServerInformation"                   , wsServerInformation);
        sb.append("authenticationManager"                 , authenticationManager);
        sb.append("maxConnections"                        , maxConnections);
        return sb.toString();
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toFormattedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("> portNumber                             : ").append(portNumber).append("\n");
        sb.append("> clientHeartBeatExpectedTime            : ").append(clientHeartBeatExpectedTime).append("\n");
        sb.append("> connectionAcceptorThreadPoolSize       : ").append(connectionAcceptorThreadPoolSize).append("\n");
        sb.append("> clusterConfiguration                   : ")
          .append(clusterConfiguration != null ? clusterConfiguration : "<N/A>").append("\n");
        sb.append("> notificationGroupConfigFileName        : ").append(notificationGroupConfigFileName).append("\n");
        sb.append("> notificationGroupName                  : ").append(notificationGroupName).append("\n");
        sb.append("> loggingFilterEnabled                   : ").append(loggingFilterEnabled).append("\n");
        sb.append("> receiveBufferSize                      : ").append(receiveBufferSize).append("\n");
        sb.append("> minaIdleTime                           : ").append(minaIdleTime).append("\n");
        sb.append("> WSServerInformation                    : ").append(wsServerInformation).append("\n");
        sb.append("> authenticationManager                  : ").append(authenticationManager).append("\n");
        sb.append("> maxConnections                         : ").append(maxConnections).append("\n");
        return sb.toString();
    }

}
