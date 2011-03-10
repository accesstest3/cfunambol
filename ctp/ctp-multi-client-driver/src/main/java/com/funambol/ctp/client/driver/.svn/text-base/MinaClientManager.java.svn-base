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

package com.funambol.ctp.client.driver;

import com.funambol.ctp.client.driver.session.StateReady;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoConnector;
import org.apache.mina.common.IoConnectorConfig;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;

import org.apache.log4j.Logger;

import com.funambol.ctp.filter.codec.CTPProtocolCodecFactory;

/**
 * Resposibilities: Manage connections and communications with the server
 * Collaborators: IoConnector, MinaClientHandler
 *
 * @version $Id: MinaClientManager.java,v 1.4 2007-11-28 11:26:14 nichele Exp $
 */
public class MinaClientManager {

    /**
     * The host address
     */
    private InetAddress hostAddress;

    /**
     * The address port
     */
    private int port;

    /**
     * Connect to the server
     */
    private IoConnector connector;

    /**
     * Holds the connector configuration
     */
    private IoConnectorConfig cfg;

    /**
     *
     */
    private List<IoSession> sessions;

    /**
     *
     */
    private int numberOfConnections = 0;

    /**
     *
     */
    private String clientPrefix = "";

    /**
     *
     */
    private int sleepInterval = 0;
    
    private int minaIdletime = 60;

    /**
     *
     */
    private Properties  prop = new Properties();
    private String dirPath = "config";
    private String propertyFileName = "ctpclient.properties";

    /**
     * logger
     */
    private final Logger logger = Logger.getLogger("com.funambol.ctp.client");

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>MinaClientManager</code>
     */
    public MinaClientManager() throws UnknownHostException {

        readProperties();
        logProperties();

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        connector = new SocketConnector();

        cfg = new SocketConnectorConfig();
        cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new CTPProtocolCodecFactory()));
        cfg.getFilterChain().addLast( "logger", new LoggingFilter() );

        cfg.setConnectTimeout(30);

        sessions = new ArrayList();
    }

    // ---------------------------------------------------------- Public Methods

    public void start() {

        for (int i = 1; i <= numberOfConnections; ++i) {
            connect(i);
            try {
                Thread.sleep(sleepInterval);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

        }
    }

    public void stop() {
        for (IoSession session : sessions) {
            if (session != null) {
                session.close();
            }
        }
    }

    /**
     * Connect to the server
     * @return true if connected, false otherwise
     */
    public boolean connect(int clientIndex) {

        if (logger.isTraceEnabled()) {
            logger.trace("connecting to " + this.hostAddress + ":" + this.port);
        }

        ConnectFuture future = connector.connect(
                new InetSocketAddress(this.hostAddress, this.port),
                new MinaClientHandler(clientIndex, clientPrefix, minaIdletime), cfg);
        future.join();
        if (!future.isConnected()) {
            logger.error("Unable to connect to " + this.hostAddress + ":" + this.port);
            return false;
        } else {

            IoSession session = future.getSession();
            sessions.add(session);
            logger.info("Connected to " + this.hostAddress + ":" + this.port);
            return true;
        }
    }

    // --------------------------------------------------------- Private Methods

    private void readProperties() {
        try {
            File propFile = new File(dirPath, propertyFileName);
            if (propFile.exists()) {
                prop.load(new FileInputStream(propFile));
            } else {
                prop.store(new FileOutputStream(propFile), "Client property file");
            }

            String serverAddress = prop.getProperty("HOST");

            try {
                this.port = Integer.parseInt(prop.getProperty("PORT"));
            } catch (NumberFormatException ex) {
                this.port = 4745;
                logger.warn("Using default port number: " + port);
            }
            this.hostAddress = InetAddress.getByName(serverAddress);

            try {
                this.numberOfConnections = Integer.parseInt(prop.getProperty("NUM_CONNECTIONS"));
            } catch(NumberFormatException ex) {
                this.numberOfConnections = 100;
            }

            this.clientPrefix = prop.getProperty("CLIENT_PREFIX");

            try {
                this.sleepInterval = Integer.parseInt(prop.getProperty("SLEEP_INTERVAL"));
            } catch(NumberFormatException ex) {
                this.sleepInterval = 100;
            }

            try {
                this.minaIdletime = Integer.parseInt(prop.getProperty("MINA_IDLE_TIME"));
            } catch(NumberFormatException ex) {
                this.minaIdletime = 60;
            }

            try {
                int readyTimeInterval = Integer.parseInt(prop.getProperty("READY_TIME_INTERVAL"));
                StateReady.setExpireTime(readyTimeInterval);
            } catch(NumberFormatException ex) {
                StateReady.setExpireTime(600);
            }

            
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void logProperties() {
        logger.info(">Server Address: " + hostAddress);
        logger.info(">Server Port: " + port);
        logger.info(">Number Of Connections: " + numberOfConnections);
        logger.info(">Client prefix: " + clientPrefix);
        logger.info(">Sleep interval: " + sleepInterval);
    }

}
