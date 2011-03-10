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

import com.funambol.ctp.server.notification.NotificationProviderException;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.integration.jmx.*;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;

import org.apache.log4j.Logger;

import com.funambol.ctp.filter.codec.CTPProtocolCodecFactory;
import com.funambol.ctp.server.config.CTPServerConfiguration;
import com.funambol.ctp.server.filter.ClientEventsLoggingFilter;
import com.funambol.ctp.server.notification.NotificationManager;


/**
 * Manages connections and communications with client devices
 * Collaborators: DeviceConnectionHandler, NotificationManager
 *
 * @version $Id: DeviceConnectionManager.java,v 1.12 2007-11-28 11:26:15 nichele Exp $
 */
public class DeviceConnectionManager {

    // --------------------------------------------------------------- Constants
    public enum State {
        NOT_EXISTING, CREATED, RUNNING, STOPPED
    }
    // ------------------------------------------------------------ Private data

    private State currentState = State.NOT_EXISTING;

    /**
     * The address port listening on
     */
    private InetSocketAddress addresPort;

    /**
     * accept incoming connections
     */
    private IoAcceptor acceptor;

    /**
     * Holds the acceptor configuration
     */
    private SocketAcceptorConfig acceptorConfig;

    /**
     * Reference to a NotificationManager passed as NotificationProvider to the
     * DeviceConnectionHandler when created. The DeviceConnectionManager starts
     * and stops the NotificationManager.
     */
    private NotificationManager dispatcher;

    /**
     * The Executor used by the ExecutorFilter for the incomming message management.
     */
    private ExecutorService executor;


    private IoServiceManager serviceManager = null;

    /**
     * The Logger used for log messages regarding communication with devices
     */
    final private Logger logger = Logger.getLogger("funambol.ctp.server");

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     * @param dispatcher
     */
    public DeviceConnectionManager(NotificationManager dispatcher) {

        this.dispatcher = dispatcher;

        int port = CTPServerConfiguration.getCTPServerConfigBean().getPortNumber();
        addresPort = new InetSocketAddress(port);

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        int acceptorThreadPoolSize =
            CTPServerConfiguration.getCTPServerConfigBean().getConnectionAcceptorThreadPoolSize();
        if (acceptorThreadPoolSize == 0) {
            acceptorThreadPoolSize = Runtime.getRuntime().availableProcessors() + 1;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Using " + acceptorThreadPoolSize + " acceptor threads.");
        }
        if (acceptorThreadPoolSize == 1) {
            acceptor = new SocketAcceptor();
        } else {
            //
            // We are not able to set the name of the created thread using the BaseThreadFactory
            // because the name is set in the SocketAcceptor code to SocketAcceptorIoProcessor-x.y
            // where x is the SocketAcceptor number, y is the number of the thread in the
            // SocketAcceptor context (max value acceptorThreadPoolSize)
            // E.g. SocketAcceptorIoProcessor-0.1: thread number 1 of the SocketAcceptor
            // 0.
            acceptor = new SocketAcceptor(acceptorThreadPoolSize, Executors.newCachedThreadPool());
        }

        //
        // Mina MBean
        //
        serviceManager = new IoServiceManager( acceptor );
        serviceManager.startCollectingStats(1000);
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {
            ObjectName name = new ObjectName("com.funambol.ctp-server:type=IoServiceManager");
            mbs.registerMBean(serviceManager, name);
        } catch (Exception e) {
            logger.warn("Unable to register Mina MBean (" + e + ")");
        }

        acceptorConfig = new SocketAcceptorConfig();
        acceptorConfig.setThreadModel(ThreadModel.MANUAL);
        acceptorConfig.setReuseAddress( true );

        executor = Executors.newCachedThreadPool(new BaseThreadFactory("CTPServer-processor"));

        acceptorConfig.getFilterChain().addLast("codec", new ProtocolCodecFilter( new CTPProtocolCodecFactory()));

        if (CTPServerConfiguration.getCTPServerConfigBean().isLoggingFilterEnabled()) {
            if (logger.isInfoEnabled()) {
                logger.info("Enabling LoggerFilter");
            }
            acceptorConfig.getFilterChain().addLast("logger", new ClientEventsLoggingFilter() );
        }
        acceptorConfig.getFilterChain().addLast("threadPool", new ExecutorFilter(executor) );

        this.currentState = State.CREATED;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the current state
     * @return the current state
     */
    public State getState() {
        return currentState;
    }

    /**
     * Returns the status of the connectionManager
     * @return the status of the connectionManager
     */
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<String, String>();
        if (serviceManager != null) {

            float loadFactor = getLoadFactor();

            status.put("loadFactor", String.valueOf(loadFactor) );
        }
        return status;
    }

    /**
     * Returns the CTP Server load factor computed as managedSession / maxConnections
     * @return the CTP Server load factor computed as managedSession / maxConnections
     */
    public float getLoadFactor() {
        int managedSession = serviceManager.getManagedSessionCount();
        int maxConnections = CTPServerConfiguration.getCTPServerConfigBean().getMaxConnections();

        float loadFactor = (float)managedSession / maxConnections;

        BigDecimal bd = new BigDecimal(loadFactor);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_EVEN);
        loadFactor = bd.floatValue();

        return loadFactor;
    }


    /**
     * Start accepting connections from devices and start listening to notifications
     * @throws com.funambol.ctp.server.DeviceConnectionException thrown when unable to start accepting connections
     */
    public void start() throws DeviceConnectionException {
        try {
            acceptor.bind(addresPort,
                          new DeviceConnectionHandler(dispatcher),
                          acceptorConfig);
        } catch (IOException ex) {
            throw new DeviceConnectionException("Unable to bind socket.", ex);
        }
        try {
            dispatcher.start();
        } catch (NotificationProviderException ex) {
            throw new DeviceConnectionException("Unable to start Device " +
                    "Connection Manager.", ex);
        }

        this.currentState = State.RUNNING;
    }

    /**
     * Close connections from devices and stop listening to notifications
     */
    public void stop() {
        if (!State.RUNNING.equals(this.currentState)) {
            return;
        }
        if (dispatcher != null) {
            dispatcher.stop();
        }
        acceptor.unbind(addresPort);
        if (!executor.isShutdown()) {
            if (logger.isInfoEnabled()) {
                logger.info("Shutting down server");
            }
            executor.shutdown();
        }

        this.currentState = State.STOPPED;
    }

}
