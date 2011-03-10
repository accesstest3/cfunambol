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

import java.io.IOException;
import org.apache.log4j.Logger;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.common.TransportType;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

import com.funambol.ctp.core.Message;
import com.funambol.ctp.server.config.CTPServerConfiguration;
import com.funambol.ctp.server.notification.NotificationProvider;
import com.funambol.ctp.server.session.SessionManager;

/**
 * Resposibilities: handles events triggered by Mina Framework
 * Collaborators: SessionManager.
 * Creates new SessionManager(s) when new connections are created and delegates
 * to them the incomming events.
 * @version $Id: DeviceConnectionHandler.java,v 1.5 2007-11-28 11:26:15 nichele Exp $
 */
public class DeviceConnectionHandler extends IoHandlerAdapter {

    // ------------------------------------------------------------ Private data

    /**
     * It is only passed as parameter to newly created SessionManager(s)
     */
    private NotificationProvider dispatcher;

    /**
     * The Logger used for log messages regarding communication with devices
     */
    final private Logger logger = Logger.getLogger("funambol.ctp.server");

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     * @param dispatcher
     */
    public DeviceConnectionHandler(NotificationProvider dispatcher) {
        this.dispatcher = dispatcher;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when a new session is created.
     * @param session
     */
    @Override
    public void sessionCreated(IoSession session) {

        if (TransportType.SOCKET.equals(session.getTransportType()) ) {
            ((SocketSessionConfig)session.getConfig() ).setReceiveBufferSize(
                    CTPServerConfiguration.getCTPServerConfigBean().getReceiveBufferSize() );
        }

        session.setIdleTime(IdleStatus.BOTH_IDLE,
                            CTPServerConfiguration.getCTPServerConfigBean().getMinaIdleTime());

        session.setAttachment(
                new SessionManager(session, dispatcher) );
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when a new session is opened.
     * @param session
     * @throws java.lang.Exception
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        SessionManager sessionManager = retrieveSessionManager(session);
        if (sessionManager.getLogger().isTraceEnabled()) {
            sessionManager.getLogger().trace("New session opened.");
        }
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when a new session is closed.
     * @param session
     * @throws java.lang.Exception
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        SessionManager sessionManager = retrieveSessionManager(session);
        sessionManager.onClose();
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when ....
     * @param session
     * @param status
     * @throws java.lang.Exception
     */
    @Override
    public void sessionIdle(IoSession session, IdleStatus status)
    throws Exception {

        SessionManager sessionManager = retrieveSessionManager(session);
        sessionManager.onIdle();
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when an exception occurs.
     * @param session
     * @param t
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable t) {
        if (t instanceof DeviceConnectionException)  {
            session.close();
        } else if (t instanceof IOException) {
            session.close();
        } else {
            session.close();
        }
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina framework when a new message arrives.
     * @param session
     * @param msg
     * @throws com.funambol.ctp.server.DeviceConnectionException
     */
    @Override
    public void messageReceived(IoSession session, Object msg)
    throws DeviceConnectionException {

        if (!(msg instanceof Message)) {
            throw new DeviceConnectionException("Received object is not a " +
                    " Message [received: " + msg.getClass().getName() + "]");
        }
        Message message = (Message)msg;

        SessionManager sessionManager = retrieveSessionManager(session);
        sessionManager.handleMessage( message);
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Retrieve the SessionManager associated to the IoSession
     * @param session The IoSession for which retrieve the SessionManager
     * @throws com.funambol.ctp.server.DeviceConnectionException
     * @return The asociated SessionManager
     */
    private SessionManager retrieveSessionManager(IoSession session)
    throws DeviceConnectionException {
        SessionManager sessionManager = (SessionManager) session.getAttachment();
        if (sessionManager == null) {
            throw new DeviceConnectionException("Unable to retrieve SessionManager for session: " + session);
        }
        return sessionManager;
    }
}
