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

package com.funambol.ctp.server;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;

import java.lang.management.ManagementFactory;

import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.jgroups.ChannelException;

import com.funambol.ctp.server.config.CTPServerConfiguration;
import com.funambol.ctp.server.management.StatusMXBeanImpl;
import com.funambol.ctp.server.notification.NotificationManager;

import com.funambol.framework.cluster.ClusterException;
import com.funambol.framework.cluster.Cluster;
import com.funambol.framework.cluster.ClusterConfiguration;
import com.funambol.framework.management.StatusMXBean;


/**
 * Main class of CTPServer
 * @version $Id: CTPServer.java,v 1.7 2008-04-22 05:58:11 nichele Exp $
 */
public class CTPServer implements CTPServerMBean {

    private static final String POM_PROPERTIES_FILE =
            "META-INF/maven/funambol/ctp-server/pom.properties";

    private final String VERSION = initVersion();

    // ------------------------------------------------------------ Private data
    private NotificationManager  notificationDispatcher = null;

    public NotificationManager getNotificationDispatcher() {
        return notificationDispatcher;
    }

    private DeviceConnectionManager connectionManager      = null;

    public DeviceConnectionManager getConnectionManager() {
        return connectionManager;
    }

    private static final Logger logger = Logger.getLogger("funambol.ctp.server");

    // ------------------------------------------------------------- Constructor
    public CTPServer() {

        //
        // Configuring log4j
        //
        String configPath = CTPServerConfiguration.getConfigPath();
        String log4jFile = configPath + File.separator + "log4j-ctpserver.xml";

        DOMConfigurator.configureAndWatch(log4jFile, 30000); // 30 sec.

        ClusterConfiguration clusterConfiguration =
                CTPServerConfiguration.getCTPServerConfigBean().getClusterConfiguration();

        if (clusterConfiguration != null) {
            try {
                Cluster.getCluster().init(clusterConfiguration);
            } catch (ClusterException ex) {
                logger.error("Error initializing the cluster", ex);
                System.exit(-1);
            }
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Cluster configuration not found, no cluster will be initialized");
            }
        }

        try {
            notificationDispatcher = new NotificationManager();
        } catch (ChannelException ex) {
            logger.error("Error creation NotificationManager", ex);
        }

        connectionManager =
            new DeviceConnectionManager(notificationDispatcher);

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(connectionManager));

        registerMBean(this);

    }

    // ---------------------------------------------------------- Public methods
    /**
     * Starts the server
     * @param args the args
     */
    public static void main(String[] args) {
        CTPServer server = new CTPServer();
        server.start();
    }

    /**
     * Starts the CTPServer
     */
    public void start() {
        try {
            connectionManager.start();
        } catch (DeviceConnectionException ex) {
            logger.error("Error starting Server", ex);
            System.exit(-1);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Server started");
        }
    }

    /**
     * Stops the CTPServer
     * See CTPServerMBean interface
     */
    public void stop()  {
        //
        // This call forces in any case the ShutDownThread execution so the CTPServer
        // is not stopped abruptly
        //
        if (logger.isInfoEnabled()) {
            logger.info("Server stopped");
        }
        System.exit(-1);
    }


    /**
     * Returns the version
     * @return the version
     */
    public String getVersion() {
        return VERSION;
    }

    // --------------------------------------------------------- Private methods
    private static void registerMBean(CTPServer ctpServer) {

        if (logger.isTraceEnabled()) {
            logger.trace("Registering MBean: " + MBEAN_NAME);
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            // Construct the ObjectName for the MBean we will register
            ObjectName name = new ObjectName(MBEAN_NAME);

            mbs.registerMBean(ctpServer, name);

        } catch (Exception ex) {
            logger.error("Error registering mbean '" + MBEAN_NAME + "'", ex);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Registering MBean: " + StatusMXBean.MBEAN_NAME);
        }

        try {
            StatusMXBean statusBean = new StatusMXBeanImpl(ctpServer);
            mbs.registerMBean(statusBean, new ObjectName(StatusMXBean.MBEAN_NAME));
        } catch (Exception ex) {
            logger.error("Error registering mbean '" + StatusMXBean.MBEAN_NAME + "'", ex);
        }

    }

    /**
     * Sets the version reading POM_PROPERTIES_FILE
     * @return the version reading POM_PROPERTIES_FILE
     */
    private String initVersion() {
        try {
            Properties properties = new Properties();

            InputStream resourceAsStream =
                    getClass().getClassLoader().getResourceAsStream(POM_PROPERTIES_FILE);

            if ( resourceAsStream == null ) {
                return "unknown";
            }

            properties.load( resourceAsStream );
            return properties.getProperty( "version", "unknown");
        } catch (IOException e ) {
            return "unknown";
        }
    }
}

/**
 * A virtual-machine shutdown hook
 */
class ShutDownThread extends Thread {

    /** The DeviceConnectionManager instance */
    private DeviceConnectionManager connectionManager = null;

    public ShutDownThread(DeviceConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    @Override
    public void run() {
        connectionManager.stop();
        Cluster.getCluster().shutdown();
    }

}
