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

import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;
import org.apache.mina.util.SessionUtil;

import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.NotAuthenticated;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Unauthorized;

/**
 * Resposibilities: handles events triggered by Mina Framework, in particular
 * handles incoming messages.
 *
 * @version $Id: MinaClientHandler.java,v 1.3 2008-04-22 05:56:30 nichele Exp $
 */
public class MinaClientHandler extends IoHandlerAdapter {

    /**
     * Stores the nonces sent by the server
     */
    private Storager storager;


    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>MinaClientHandler</code>
     * @param storager The storager to use
     */
    public MinaClientHandler(Storager storager) {
        this.storager = storager;
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
    }

    /**
     * IoHandlerAdapter callback
     * Method called from framework Mina when an exception occurs.
     * @param cause the throwed exception
     * @param session the connection session which has triggered the exception
     */
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) {
        if (SessionLog.isWarnEnabled(session)) {
            SessionLog.warn(session, "EXCEPTION, please implement "
                    + getClass().getName()
                    + ".exceptionCaught() for proper handling:", cause);
        }
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
            throw new MinaClientException("Received message is not a valid Message");
        }

        Message message = (Message)msg;

        GenericCommand command = message.getCommand();

        if (command instanceof Ok) {
            onOk((Ok)command);
        } else if (command instanceof Unauthorized) {
            onUnauthorized((Unauthorized)command);
        } else if (command instanceof NotAuthenticated) {
            onNotAuthenticated((NotAuthenticated)command);
        }
        
        MinaClient client = (MinaClient) session.getAttribute("client");
        client.received(command);
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Manage Ok messages
     * @param ok The incoming Ok message
     */
    private void onOk(Ok ok) {

        byte[] nonce = ok.getNonce();

        if (nonce != null && nonce.length > 0 ) {
            this.storager.storeNonce(nonce);
        }

    }

    /**
     * Manages Unauthorized messages
     * @param unauthorized the incoming Unauthorized message
     */
    private void onUnauthorized(Unauthorized unauthorized) {

        byte[] nonce = unauthorized.getNonce();

        if (nonce != null && nonce.length > 0 ) {
            this.storager.storeNonce(nonce);
        }
    }
    
    /**
     * Manages NotAuthenticated messages
     * @param notAuthenticated the incoming NotAuthenticated message
     */
    private void onNotAuthenticated(NotAuthenticated notAuthenticated) {

        byte[] nonce = notAuthenticated.getNonce();

        if (nonce != null && nonce.length > 0 ) {
            this.storager.storeNonce(nonce);
        }
    }    
}
