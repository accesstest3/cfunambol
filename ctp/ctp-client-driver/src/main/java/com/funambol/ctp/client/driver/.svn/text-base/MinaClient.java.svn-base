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

import java.net.InetAddress;
import java.net.InetSocketAddress;

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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.filter.codec.CTPProtocolCodecFactory;

/**
 * Resposibilities: Manage connections and communications with the server
 * Collaborators: IoConnector, MinaClientHandler, Storager
 *
 * @version $Id: MinaClient.java,v 1.4 2008-04-22 05:57:16 nichele Exp $
 */
public class MinaClient {

    /**
     * The host address
     */
    private InetAddress hostAddress;

    /**
     * The address port
     */
    private int port;

    /**
     * It is only passed as parameter to newly created MinaClientHandler(s)
     */
    private Storager storager;

    /**
     * Connect to the server
     */
    private IoConnector connector;

    /**
     * Holds the connector configuration
     */
    private IoConnectorConfig cfg;
    
    /**
     * The connection session
     */
    private IoSession session =  null;

    /**
     * logger
     */
    private final Logger logger = Logger.getLogger("com.funambol.ctp.client");

    private MinaClientHandler clientHandler = null;
    
    /** used to wait a message from the server */
    private Object semaphore = new Object();   
    
    private GenericCommand lastResponse = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>MinaClient</code>
     * @param hostAddress the host address to connect to
     * @param port the address port
     * @param storager the storager used to store nonces sent by server
     */
    public MinaClient(InetAddress hostAddress, int port, Storager storager) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.storager = storager;

        ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());

        connector = new SocketConnector();

        cfg = new SocketConnectorConfig();
        cfg.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new CTPProtocolCodecFactory()));
        cfg.getFilterChain().addLast( "logger", new LoggingFilter() );

        cfg.setConnectTimeout(30);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Connect to the server
     * @return true if connected, false otherwise
     */
    public boolean connect() {

        logger.debug("connecting on: '" + this.hostAddress + ":" + this.port);
        
        clientHandler = new MinaClientHandler(storager);
        
        ConnectFuture future = connector.connect(
                new InetSocketAddress(this.hostAddress, this.port), clientHandler, cfg);
        future.join();
        if (!future.isConnected()) {
            logger.error("Unable to Connect");
            return false;
        }
        session = future.getSession();
        //
        // We store the client so that the handler can notify the received commands
        //
        session.setAttribute("client", this);
        
        logger.info("Connected");
        return true;
    }

    /**
     * Send a message
     * @param message
     */
    public void send(Object message) {

        if (session == null) {
            logger.error("Unable to send message: session non created.");
            return;
        }
        session.write(message);
    }

    /**
     * Send a message
     * @param message
     */
    public GenericCommand sendAndWait(Message message) {

        if (session == null) {
            logger.error("Unable to send message: session non created.");
            return null;
        }
        lastResponse = null;
        
        session.write(message);
        
        synchronized(semaphore) {

            try {
                semaphore.wait();
            } catch (InterruptedException e) {
            }

        }
        return lastResponse;
    }    

    public void received(GenericCommand command) {
        this.lastResponse = command;
        synchronized(semaphore) {
            semaphore.notifyAll();
        }
        
    }
    /**
     * Close the connection
     */
    public void quit() {
        if (session != null) {
            session.close();
        }
    }
}
