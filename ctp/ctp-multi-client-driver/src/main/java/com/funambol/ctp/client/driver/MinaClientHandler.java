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

import org.apache.log4j.Logger;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionUtil;
import com.funambol.ctp.client.driver.session.SessionManager;
import com.funambol.ctp.core.Message;

/**
 * Resposibilities: handles events triggered by Mina Framework, in particular
 * handles incoming messages.
 *
 * @version $Id: MinaClientHandler.java,v 1.3 2007-11-28 11:26:14 nichele Exp $
 */
public class MinaClientHandler extends IoHandlerAdapter {

    private int clientIndex = 0;
    
    private int minaIdletime = 60;

    private String clientPrefix = "";

    /**
     * The Logger used for log messages regarding communication with devices
     */
    private final Logger logger = Logger.getLogger("funambol.ctp.client.driver");

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>MinaClientHandler</code>
     */
    public MinaClientHandler(int clientIndex, String clientPrefix, int minaIdletime) {
        this.clientIndex = clientIndex;
        this.clientPrefix = clientPrefix;
        this.minaIdletime = minaIdletime;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when a new session is created.
     * @param session The newly created session
     */
    @Override
    public void sessionCreated(IoSession session) {
        SessionUtil.initialize(session);

        session.setIdleTime(IdleStatus.BOTH_IDLE, minaIdletime);

        session.setAttachment(new SessionManager(session, clientIndex, clientPrefix));
    }

    /**
     *
     */
    @Override
    public void sessionOpened(IoSession session) throws Exception {
        if (logger.isTraceEnabled()) {
            logger.trace("New session opened.");
        }

        SessionManager sessionManager = retrieveSessionManager(session);
        sessionManager.onSessionOpened();
    }

    /**
     *
     */
    @Override
    public void sessionClosed(IoSession session) throws Exception {
        SessionManager sessionManager = retrieveSessionManager(session);
        sessionManager.onClose();
    }

    /**
     *
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
     * @param cause the throwed exception
     * @param session the connection session which has triggered the exception
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        logger.warn("EXCEPTION, please implement "
                + getClass().getName()
                + ".exceptionCaught() for proper handling:", cause);

        session.close();
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina framework when a new message arrives.
     * @param session the connection session the incoming message belongs to
     * @param msg the incoming message
     * @throws com.funambol.ctp.client.driver.MinaClientException
     */
    @Override
    public void messageReceived(IoSession session, Object msg)
            throws MinaClientException {

        if (!(msg instanceof Message)) {
            throw new MinaClientException("Received object is not a " +
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
    throws MinaClientException {
        SessionManager sessionManager = (SessionManager) session.getAttachment();
        if (sessionManager == null) {
            throw new MinaClientException("Unable to retrieve SessionManager for session: " + session);
        }
        return sessionManager;
    }

}
