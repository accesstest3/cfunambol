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

package com.funambol.ctp.server.session;

import java.util.List;
import java.util.UUID;

import org.apache.mina.common.IoSession;
import org.apache.mina.util.SessionLog;

import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.Bye;
import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Forbidden;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.NotAuthenticated;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Ready;
import com.funambol.ctp.core.Sync;
import com.funambol.ctp.core.Unauthorized;
import com.funambol.ctp.server.authentication.AuthenticationManager;
import com.funambol.ctp.server.authentication.AuthenticationException;
import com.funambol.ctp.server.config.CTPServerConfiguration;
import com.funambol.ctp.filter.codec.CTPProtocolConstants;
import com.funambol.ctp.server.notification.NotificationListener;
import com.funambol.ctp.server.notification.NotificationProvider;
import com.funambol.ctp.server.pending.PendingNotificationManager;
import com.funambol.ctp.server.pending.PendingNotificationException;

import com.funambol.framework.security.AuthorizationResponse;

import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;

/**
 * <p>Manages events triggered by the DeviceConnectionHandler and by
 * the NotificationProvider. It manages AUTH, READY and BYE messages sent by
 * clients and Notification messages sent by <CODE>CTPSender</CODE>s </p>
 * <p>Collaborates with <CODE>org.apache.mina.common.IoSession</CODE>,
 * <CODE>State</CODE>, <CODE>AuthenticationManager</CODE> and
 * <CODE>NotificationProvider</CODE></p>
 * <p>Participate in the State Pattern holding the "context" role: defines the
 * interface of interest to clients and maintains an instance of a ConcreteState
 * subclass that defines the current state.</p>
 * <p>There is a SessionManager for every IoSession.</p>
 * <p>It is normally created by <CODE>DeviceConnectionHandler</CODE> when a new
 * connection is established.</p>
 * <p>Note that all methods that changes the state are synchronized in order to
 * prevent concurrent issues (remember that there is a SessionManager per connection,
 * so synchronized methods should not be bottlenecks).</p>

 * @version $Id: SessionManager.java,v 1.17 2008-07-08 20:10:21 nichele Exp $
 */
public class SessionManager implements NotificationListener {

    /**
     * The IoSession associated to the SessionManager
     */
    private IoSession session;

    /**
     * Returns the associated session
     * @return The associated session
     */
    public IoSession getSession() {
        return session;
    }

    /**
     * The <CODE>AuthenticationManager</CODE> for client authentication
     */
    private AuthenticationManager authenticationManager;

    /**
     * The <CODE>PendingNotificationManager</CODE> to get the pending
     * notifications
     */
    private PendingNotificationManager pendingNotificationManager;

    /**
     * The class providing notification messages sent by the CTPSenders
     */
    private NotificationProvider dispatcher;

    /**
     * The current state to wich delegate event handling
     */
    private State sessionState;

    /**
     * Creates a new unique session identifier
     * @return A new unique session identifier
     */
    private String createSessionId() {
        UUID uuid = java.util.UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * The unique session id identifying the SessionManager and the associated IoSession
     */
    private String sessionId = "";

    /**
     * Returns the session id
     * @return The session id
     */
    public String getSessionId() {
        return sessionId;
    }

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

    /**
     *
     */
    private int authenticationRetries = 0;

    protected void incrementAuthenticationRetries() {
        ++authenticationRetries;
    }

    public int getAuthenticationRetries() {
        return authenticationRetries;
    }

    /**
     * The session logger used to log events occurring during the session
     */
    private final SessionLogger logger =
            new SessionLogger("funambol.ctp.server.session-manager");

    /**
     * Returns the SessionLogger used to log events occurring during the session
     * @return The SessionLogger used to log events occurring during the session
     */
    public SessionLogger getLogger() {
        return logger;
    }

    public static final String ATTRIBUTE_DEVID    = "DEVID"   ;
    public static final String ATTRIBUTE_USERNAME = "USERNAME";
    public static final String ATTRIBUTE_CRED     = "CRED"    ;

    // ------------------------------------------------------------ Constructors

    /**
     * <p>Creates a new instance of <CODE>SessionManager</CODE></p>
     * <p>The authentication manager is retrieved from the CTPServerConfiguration
     * using parameter specifide in file
     * config/com/funambol/ctp/server/authentication/AuthenticationManager.xml</p>
     * @param session The related IoSession
     * @param dispatcher The NotificationProvider where to subscribe for
     * notification messages.
     */
    public SessionManager(IoSession session,
                          NotificationProvider dispatcher) {
        this.session = session;
        this.dispatcher = dispatcher;
        this.sessionState = new StateConnected();
        this.sessionId = createSessionId();
        lastClientEventTime = System.currentTimeMillis();

        this.authenticationManager =
            CTPServerConfiguration.getCTPServerConfiguration().getAuthenticationManager();

        this.pendingNotificationManager =
            CTPServerConfiguration.getCTPServerConfiguration().getPendingNotificationManager();

        session.setAttribute(SessionLog.LOGGER, logger);

        String prefix = "[" + session.getRemoteAddress() + "] ";
        session.setAttribute(SessionLog.PREFIX, prefix);
        logger.setPrefix(prefix);
    }

    // ---------------------------------------------------------- Public Methods

    public synchronized void handleMessage(Message message) {

        if  (!isSupportedVersion(message.getProtocolVersion())) {
            sendErrorMessage("Version " + message.getProtocolVersion() +
                    " not supported");
            return;
        }

        GenericCommand command = message.getCommand();
        if (command == null) {
            sendErrorMessage("Invalid Message received. Command is null" );
            return;
        }

        if (command instanceof Auth) {
            onAuthenticationReq((Auth)command);
        } else if (command instanceof Bye) {
            onBye((Bye)command);
        } else if (command instanceof Ready) {
            onReady((Ready)command);
        } else {
            sendErrorMessage("Invalid Message received. Unknown command: " +
                    command.getName() );
        }
    }

    /**
     *
     */
     public synchronized void onAuthenticationReq(Auth auth) {

        setLastClientEventTime(System.currentTimeMillis());

        String deviceId = auth.getDevid();
        if (deviceId == null || "".equals(deviceId)) {
            sendErrorMessage(Auth.PARAM_DEVID + " parameter is mandatory for " +
                    auth.getName() + " messages");
            setSessionState(new StateLeaving());
            return;
        }

        String username = auth.getUsername();
        if (username == null || "".equals(username)) {
            sendErrorMessage(Auth.PARAM_USERNAME + " parameter is mandatory for " +
                    auth.getName() + " messages");
            setSessionState(new StateLeaving());
            return;
        }

        String credential = auth.getCred();
        if (credential == null || "".equals(credential)) {
            sendErrorMessage(Auth.PARAM_CRED + " parameter is mandatory for " +
                    auth.getName() + " messages");
            setSessionState(new StateLeaving());
            return;
        }

        setLogPrefix(username, deviceId, session);

        session.setAttribute(ATTRIBUTE_DEVID,    deviceId);
        session.setAttribute(ATTRIBUTE_USERNAME, username);
        session.setAttribute(ATTRIBUTE_CRED,     credential);

        State nextState = sessionState.onAuthenticationReq(this, auth);
        setSessionState(nextState);
    }

    /**
     *
     */
    public synchronized void onReady(Ready ready) {
        setLastClientEventTime(System.currentTimeMillis());
        State nextState = sessionState.onReady(this, ready);
        setSessionState(nextState);
    }

    /**
     *
     */
    public synchronized void onBye(Bye bye) {
        setLastClientEventTime(System.currentTimeMillis());
        State nextState = sessionState.onBye(this, bye);
        setSessionState(nextState);
    }

    /**
     *
     */
    public synchronized void onIdle() {
        State nextState = sessionState.onIdle(this, lastClientEventTime,
                System.currentTimeMillis());
        setSessionState(nextState);
    }

    /**
     *
     */
    public synchronized void onNotification(CTPNotification message) {
        State nextState = sessionState.onNotification(this, message);
        setSessionState(nextState);
    }

    /**
     * Callback called when another <CODE>SessionManager</CODE> subscribes
     * using the same key (deviceId).
     * If another <CODE>SessionManager</CODE> subscribes using the same key, the
     * previously subscribed <CODE>SessionManager</CODE> closes the connection.
     */
    public void onUnsubscription() {
        logger.error("Found previously established connection for the same device id");
        closeSession();
    }

    /**
     *
     */
    public synchronized void onClose() {
        sessionState.onClose(this);
        setSessionState(new StateDiconnected() );
        if (logger.isTraceEnabled()) {
            logger.trace("Connection closed for device '" + getDeviceId() + "'");
        }
    }

    /**
     * Returns the username associated with the session
     * @return the username
     */
    public String getUserName() {
        return (String) session.getAttribute(ATTRIBUTE_USERNAME);
    }

    /**
     * Returns the Device ID associated with the session
     * @return the device id
     */
    public String getDeviceId() {
        return (String) session.getAttribute(ATTRIBUTE_DEVID);
    }

    // --------------------------------------------------------Protected Methods

    /**
     * Send the message "OK" to the connected client device
     */
    protected void sendOKMessage() {
        sendOKMessage(new byte[]{});
    }

    /**
     * Send the message "OK" to the connected client device
     * @param authorizationResponse The AuthorizationResponse used to fill the
     * OK message
     */
    protected void sendOKMessage(final AuthorizationResponse authorizationResponse) {
        sendOKMessage(authorizationResponse.getNextNonce());
    }

    /**
     * Send the message "OK" to the connected client device
     * @param nonce The nonce byte array to send with the OK message
     */
    protected void sendOKMessage(final byte[] nonce) {
        Ok ok = new Ok();
        if (nonce != null && nonce.length > 0 ) {
            ok.setNonce(nonce);
        }
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, ok);
        write(replyMessage);
    }

    protected void sendErrorMessage(final String message) {
        com.funambol.ctp.core.Error error = new com.funambol.ctp.core.Error();
        error.setDescription(message);
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, error);
        write(replyMessage);
    }

    protected void sendUnauthorizedMessage(final AuthorizationResponse authorizationResponse) {
        sendUnauthorizedMessage(authorizationResponse.getNextNonce());
    }

    protected void sendUnauthorizedMessage(final byte[] nonce) {
        Unauthorized unauthorized = new Unauthorized();
        unauthorized.setNonce(nonce);
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, unauthorized);
        write(replyMessage);
    }

    protected void sendNotAuthenticatedMessage(final AuthorizationResponse authorizationResponse) {
        sendNotAuthenticatedMessage(authorizationResponse.getNextNonce());
    }

    protected void sendNotAuthenticatedMessage(final byte[] nonce) {
        NotAuthenticated notAuthenticated = new NotAuthenticated();
        notAuthenticated.setNonce(nonce);
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, notAuthenticated);
        write(replyMessage);
    }

    protected void sendForbiddenMessage() {
        Forbidden forbidden = new Forbidden();
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, forbidden);
        write(replyMessage);
    }

    protected void sendSyncMessage(byte[] notificationMessage) {
        Sync sync = new Sync();
        sync.setNotificationMessage(notificationMessage);
        Message replyMessage = new Message(CTPProtocolConstants.CTP_PROTOCOL_1_0, sync);
        write(replyMessage);
        if (logger.isInfoEnabled()) {
            logger.info("Sending notification message to the device '" + getDeviceId() + "'");
        }
    }

    /**
     * Forward the authentication request to the Authentication Manager
     * @return
     * @throws com.funambol.ctp.server.authentication.AuthenticationException
     */
    protected AuthorizationResponse forwardAuthenticationRequest()
    throws AuthenticationException {

        String username = (String) session.getAttribute(ATTRIBUTE_USERNAME);
        String deviceId = (String) session.getAttribute(ATTRIBUTE_DEVID);
        String credential = (String) session.getAttribute(ATTRIBUTE_CRED);
        if (logger.isTraceEnabled()) {
            logger.trace("Authenticating user '" + username + "', deviceId: '" +
                         deviceId + "'");
        }
        AuthorizationResponse result =
                authenticationManager.authenticate(username, deviceId, credential);
        return result;
    }

    protected void forwardSubscription() {
        String deviceId = (String) session.getAttribute(ATTRIBUTE_DEVID);
        if (deviceId == null || "".equals(deviceId)) {
            logger.error("Unable to retrieve device id from session attributes");
            closeSession() ;
            return;
        }

        dispatcher.subscribe(deviceId, this);
        if (logger.isTraceEnabled()) {
            logger.trace("Subscribed notification for device " + deviceId);
        }
    }

    /**
     *
     */
    protected void forwardUnsubscription() {
        String deviceId = (String) session.getAttribute(ATTRIBUTE_DEVID);
        if (deviceId == null || "".equals(deviceId)) {
            logger.error("Unable to retrieve device id from session attributes");
            closeSession() ;
            return;
        }

        boolean unsubscribed = dispatcher.unsubscribe(deviceId, this);
        if (unsubscribed) {
            if (logger.isTraceEnabled()) {
                logger.trace("Unsubscribed notification for device " + deviceId);
            }
        }
    }

    /**
     *
     */
    protected void closeSession() {
        logger.warn("Closing session");
        session.close();
    }

    /**
     * Called by Status implementations to retrieve the pending notifications
     * and send them to the connected client device
     */
    protected void managePendingNotificationMessage() {
        try {

            String username = (String) session.getAttribute(ATTRIBUTE_USERNAME);
            String deviceId = (String) session.getAttribute(ATTRIBUTE_DEVID);
            if (logger.isTraceEnabled()) {
                logger.trace("Retrieving pending notifications for user '" + username + "', deviceId: '" + deviceId + "'");
            }

            com.funambol.framework.notification.Message message =
                    pendingNotificationManager.getMessageFromPendingNotifications(username, deviceId);

            if (message == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("No pending notification messages present.");
                }
            } else {
                sendSyncMessage(message.getMessageContent());

                pendingNotificationManager.deletePendingNotifications(username,
                        deviceId, message.getSyncSources());
                if (logger.isTraceEnabled()) {
                    logger.trace("Pending notifications sent to device " + deviceId);
                }
            }
        } catch (PendingNotificationException ex) {
            logger.error("Error while retrieving pending notification messages", ex);
        }

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
    private void setLogPrefix(final String userName, final String deviceId, final IoSession session) {

        StringBuilder prefix = new StringBuilder();
        prefix.append("[").append(session.getRemoteAddress()).append("] ");
        prefix.append("[").append(getSessionId()).append("] ");
        prefix.append("[").append(deviceId).append("] ");
        prefix.append("[").append(userName != null ? userName : "").append("] ");
        session.setAttribute(SessionLog.PREFIX, prefix.toString());
        logger.setPrefix(prefix.toString());
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
