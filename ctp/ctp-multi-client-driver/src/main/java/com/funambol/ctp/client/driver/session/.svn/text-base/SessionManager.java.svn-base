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

package com.funambol.ctp.client.driver.session;

import com.funambol.ctp.core.NotAuthenticated;
import org.apache.mina.common.IoSession;
import org.apache.log4j.Logger;
import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.Bye;
import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Ready;
import com.funambol.ctp.core.Sync;
import com.funambol.ctp.core.Unauthorized;
import com.funambol.ctp.filter.codec.CTPProtocolConstants;
import com.funambol.ctp.util.AuthenticationUtils;
import com.funambol.framework.tools.Base64;

/**
 * <p>Manages events triggered by the MinaClientHandler. It manages Ok, Error
 * Unauthorized, NotAuthenticated and Notification messages sent by the
 * <CODE>CTPSender</CODE>s </p>
 * <p>Collaborates with <CODE>org.apache.mina.common.IoSession</CODE>,
 * <CODE>State</CODE>></p>
 * <p>Participate in the State Pattern holding the "context" role: defines the
 * interface of interest to clients and maintains an instance of a ConcreteState
 * subclass that defines the current state.</p>
 * <p>There is a SessionManager for every IoSession.</p>
 * <p>It is normally created by <CODE>MinaClientHandler</CODE> when a new
 * connection is established.</p>
 * <p>Note that all methods that change the state are synchronized in order to
 * prevent concurrent issues (remember that there is a SessionManager per connection,
 * so synchronized methods should not be bottlenecks).</p>
 *
 * @version $Id: SessionManager.java,v 1.3 2007-11-28 11:26:17 nichele Exp $
 */
public class SessionManager {

    /**
     * The IoSession associated to the SessionManager
     */
    private IoSession session;

    /**
     * The current state to wich delegate event handling
     */
    private State sessionState;
    /**
     * The last time the client contacted the server sending a message.
     */
    private long lastClientEventTime;

    protected long getLastClientEventTime() {
        return lastClientEventTime;
    }

    protected void setLastClientEventTime(long lastClientEventTime) {
        this.lastClientEventTime = lastClientEventTime;
    }

    private String deviceid;
    private String username;
    private String password;

    /**
     *
     */
    private final Logger logger =
            Logger.getLogger("funambol.ctp.client.driver.session-manager");

    public static final String DEVICE =   "device"   ;
    public static final String USER =     "user";
    public static final String PASSWORD = "password"    ;

    // ------------------------------------------------------------ Constructors

    /**
     * <p>Creates a new instance of <CODE>SessionManager</CODE></p>
     * @param session The associated IoSession
     * @param clientIndex The index of the client
     * @param clientPrefix The prefix for user names and device ids
     */
    public SessionManager(IoSession session, int clientIndex, String clientPrefix) {
        this.session = session;
        this.sessionState = new StateConnected();

        lastClientEventTime = System.currentTimeMillis();

        deviceid = clientPrefix + DEVICE + String.valueOf(clientIndex);
        username = clientPrefix + USER + String.valueOf(clientIndex);
        password = PASSWORD;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Handles connection Opened event
     */
    public void onSessionOpened() {

        byte[] nonce = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
        sendAuthMessage(nonce);
    }

    /**
     * Handles the incoming message
     * @param message The message sent by the CTP-Server
     */
    public synchronized void handleMessage(Message message) {

        if  (!isSupportedVersion(message.getProtocolVersion())) {
            logger.error("Version " + message.getProtocolVersion() +
                    " not supported");
            return;
        }

        GenericCommand command = message.getCommand();
        if (command == null) {
            logger.error("Invalid Message received. Command is null." );
            closeSession();
            return;
        }

        if (command instanceof Ok) {
            onOk( (Ok) command);
        } else if (command instanceof com.funambol.ctp.core.Error) {
            onError( (com.funambol.ctp.core.Error) command);
        } else if (command instanceof Unauthorized) {
            onUnauthorized( (Unauthorized) command);
        } else if (command instanceof NotAuthenticated) {
            onNotAuthenticated( (NotAuthenticated) command);
        } else if (command instanceof Sync) {
            onSync( (Sync) command);
        } else {
            logger.error("Invalid Message received. Unknown command: " +
                    command.getName() );
            closeSession();
            return;
        }
    }


    /**
     * Handles Ok messages sent by the CTP-Sender
     * @param ok The Ok command
     */
    public void onOk(Ok ok) {
        State nextState = sessionState.onOk(this, ok);
        setSessionState(nextState);
    }

    /**
     * Handles Error status sent by the CTP-Sender
     * @param error The Error status sent by the CTP-Sender
     */
    private void onError(com.funambol.ctp.core.Error error) {
        State nextState = sessionState.onError(this, error);
        setSessionState(nextState);
    }

    /**
     * Handles Unauthorized status sent by the CTP-Sender
     * @param unauthorized The Unauthorized status sent by the CTP-Server
     */
    private void onUnauthorized(Unauthorized unauthorized) {
        State nextState = sessionState.onUnauthorized(this, unauthorized);
        setSessionState(nextState);
    }

    /**
     * Handles NotAuthenticated status sent by the CTP-Sender
     * @param notAuthenticated The NotAuthenticated status sent by the CTP-Server
     */
    private void onNotAuthenticated(NotAuthenticated notAuthenticated) {
        State nextState = sessionState.onNotAuthenticated(this, notAuthenticated);
        setSessionState(nextState);
    }

    /**
     * Handles Sync status sent by the CTP-Sender
     * @param sync The Sync status sent by the CTP-Server
     */
    private void onSync(Sync sync) {
        State nextState = sessionState.onSync(this, sync);
        setSessionState(nextState);
    }

    /**
     * Handles connection Close event
     */
    public synchronized void onClose() {
        sessionState.onClose(this);
        setSessionState(new StateDiconnected() );
    }

    /**
     * Handles connection Close event
     */
    public synchronized void onIdle() {
        State nextState = sessionState.onIdle(this, lastClientEventTime,
                System.currentTimeMillis());
        setSessionState(nextState );
    }

    // --------------------------------------------------------Protected Methods

    /**
     * Sends an Auth message to the CTP-Server
     * @param nonce The nonce used to create the digest
     */
    protected void sendAuthMessage(final byte[] nonce) {

        if (nonce == null || nonce.length == 0 ) {
            throw new IllegalArgumentException("The nonce must be not null and not empty");
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Sending auth. request  with: '" +
                    username + "', '" +
                    password + "', '" +
                    new String(Base64.encode(nonce)) + "'");
        }
        String cred = AuthenticationUtils.createDigest(username, password, nonce);

        Auth auth = new Auth(deviceid, username, cred);
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, auth);
        write(message);
        
        setLastClientEventTime(System.currentTimeMillis());
    }

    /**
     * Send the message Ready to the CTP-Server
     */
    public void sendReady() {

        if (logger.isTraceEnabled()) {
            logger.trace("Sending Ready");
        }
        Ready ready = new Ready();
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, ready);
        write(message);
        
        setLastClientEventTime(System.currentTimeMillis());
    }

    /**
     * Sends the message Bye to the CTP-Server
     */
    public void sendBye() {
        if (logger.isTraceEnabled()) {
            logger.trace("Sending Bye");
        }
        Bye bye = new Bye();
        Message message = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, bye);
        write(message);
        
        setLastClientEventTime(System.currentTimeMillis());
    }

    /**
     * Closes the connection sesssion
     */
    protected void closeSession() {
        logger.warn("Closing session");
        session.close();
    }

    // --------------------------------------------------------- Private Methods

    /**
     *
     */
    private void setSessionState(State sessionState) {

        if (this.sessionState.deepequals(sessionState)) {
            return;
        }
        if (this.sessionState.deepequals(new StateDiconnected()) &&
                !(sessionState.deepequals(new StateDiconnected()))) {
            logger.error("status transition from " + this.sessionState.getName() +
                    " to " + sessionState.getName() + " in not allowed");
            return;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Changing status from " + this.sessionState.getName() +
                    " to " + sessionState.getName());
        }
        this.sessionState = sessionState;
    }

    /**
     *
     */
    private void write(Message message) {
        session.write(message);
    }

    /**
     *
     */
    private boolean isSupportedVersion(String version) {
        if (CTPProtocolConstants.CTP_PROTOCOL_1_0.equals(version)) {
            return true;
        }
        return false;
    }

}
