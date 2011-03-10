/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.server.session;

import java.lang.reflect.Method;
import java.util.*;

import com.funambol.framework.engine.source.SyncSourceException;

import com.funambol.framework.core.*;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.*;
import com.funambol.framework.engine.pipeline.MessageProcessingContext;
import com.funambol.framework.engine.source.MemorySyncSource;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.filter.FilterClause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.protocol.*;
import com.funambol.framework.security.*;
import com.funambol.framework.server.*;
import com.funambol.framework.server.error.*;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.*;
import com.funambol.framework.tools.Base64;
import com.funambol.server.config.Configuration;
import com.funambol.server.config.ConfigurationConstants;
import com.funambol.server.engine.Sync4jEngine;
import org.apache.log4j.Logger;

/**
 * This class represents the handler for a SyncML session. It coordinates and
 * handles the packages and messages as dictated by the protocol.
 * <p>
 * The entry point is <i>processMessage()</i>, which determines which message is
 * expected and what processing has to be done (depending on the value of
 * <i>currentState</i>). If an error accours, the session goes to the state
 * <i>STATE_ERROR</i>; in this state no other messages but initialization can be
 * performed.
 * <p>
 * In the current implementation separate initialization is required.
 * <p>
 * <i>SessionHandler</i> makes use of a <i>SyncEngine</i> for all
 * tasks not related to the handling of the protocol.
 * See <i>com.funambol.framework.engine.SyncEngine</i> for more information.
 *
 * LOG NAME: funambol.handler
 *
 * @see com.funambol.framework.engine.SyncEngine
 *
 * @version $Id: SyncSessionHandler.java,v 1.1.1.1 2008-02-21 23:35:57 stefano_fornari Exp $
 */
public class SyncSessionHandler
implements SessionHandler,
           java.io.Serializable,
           SecurityConstants,
           ConfigurationConstants {

    // ------------------------------------------------------------ Private data
    private static final String REQUEST_CONTENT_TYPE = "content-type";
    private Logger    logPush = Logger.getLogger("funambol.push");

    private int currentState = STATE_START;

    //
    // This contains the content-type to use by server if it must request the
    // capabilities to client
    //
    private String contentType = null;

    /**
     * Gets the current state
     *
     * @return the current state
     */
    public int getCurrentState() {
        return currentState;
    }

    private long creationTimestamp = -1;

    /**
     * Gets the creation timestamp of the session
     *
     * @return the creation timestamp
     */
    public long getCreationTimestamp() {
        return creationTimestamp;
    }

    private transient FunambolLogger log = FunambolLoggerFactory.getLogger("handler");

    /**
     * SyncTimestamp for the current synchronization
     */
    private SyncTimestamp nextTimestamp = null;

    private transient SyncInitialization syncInit     = null;
    private transient SyncModifications modifications = null;

    /**
     * Contain the client device Id for the current session
     */
    private String clientDeviceId = null;

    /**
     * The databases that have to be synchronized. It is set in the initialization
     * process.
     */
    private Database[] dbs         = null;

    /**
     * The types of authetication supported from the server
     */
    private String clientAuth = null;

    /**
     * Size of the current response message
     */
    private long messageSize = 0;

    /**
     * Which calculator should be used to calculate message size?
     */
    private MessageSizeCalculator sizeCalculator = null;

    // -------------------------------------------------------------- Properties

    /**
     * The session id - read only
     */
    private String sessionId = null;

    public String getSessionId() {
        return this.sessionId;
    }

    /**
     * The command id generator (it defaults ro a <i>CommandIdGenerator</i> instance)
     */
    private CommandIdGenerator cmdIdGenerator = new CommandIdGenerator();

    public void setCommandIdGenerator(CommandIdGenerator cmdIdGenerator) {
        this.cmdIdGenerator = cmdIdGenerator;
    }

    public CommandIdGenerator getCommandIdGenerator() {
        return this.cmdIdGenerator;
    }

    /**
     * The cmdIdGenerator must be reset each time the process
     * of a message is starting
     */
    private void resetIdGenerator() {
        cmdIdGenerator.reset();
        syncEngine.setCommandIdGenerator(cmdIdGenerator);
    }

    /**
     * The message id generator (it defaults ro a <i>SimpleIdGenerator</i> instance)
     */
    private SimpleIdGenerator msgIdGenerator = new SimpleIdGenerator();

    /**
     * The Last message Id from the client
     */
    private String lastMsgIdFromClient = null;

    /**
     * The <i>SyncEngine</i>
     */
    private SyncEngine syncEngine = null;

    /**
     * The SyncState for the syncronization process
     */
    private SyncState syncState = null;

    private boolean isInInit = true;

    /**
     * The key of this map is msgId|cmdId. The value is a commandDescriptor.
     * So for example, if the command 2 in the messege 3 is an Add for the
     * source uri 'contact', the map will contain:
     * key: 3|2
     * value: the command descriptor
     */
    private java.util.Map commandsDescriptors = null;

    // ---------------------------------------------------------- Public methods
    public SyncEngine getSyncEngine() {
        return this.syncEngine;
    }

    /**
     * Indicates if the session is a new session
     */
    private boolean newSession = true;

    public void setNew(boolean newSession) {
        this.newSession = newSession;
    }

    public boolean isNew() {
        return this.newSession;
    }

    /**
     * Sets the message size calculator
     *
     * @param calculator the message size calculator
     */
    public void setSizeCalculator(MessageSizeCalculator calculator) {
        this.sizeCalculator = calculator;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of SyncSessionHandler
     */
    public SyncSessionHandler() {
        this.creationTimestamp = System.currentTimeMillis();
        this.syncEngine        = Configuration.getConfiguration().getEngine();
    }

    /**
     * Creates a new instance of SyncSessionHandler with a given session id
     */
    public SyncSessionHandler(String sessionId) {
        this();
        this.sessionId = sessionId;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns true if the sessione has been authenticated.
     *
     * @return true if the sessione has been authenticated, false otherwise
     */
    public boolean isAuthenticated() {
        return (syncState != null) && (syncState.authenticationState == AUTH_AUTHENTICATED);
    }

    /**
     * Returns true if the session has not been authenticated because of an
     * expired account.
     * <br/>NOTE: isAccountExpired is declared in v3 officer interface. We call it
     * using reflection just to guarantee backward compatibility
     *
     * @return true if the account is expired. false if the account is not expired
     * or the officer doens't implement isAccountExpired method (new v6 interface)
     */
    public boolean isAccountExpired() {
        //
        // isAccountExpired is declared in v3 officer interface. We call it
        // using reflection just to guarantee backward compatibility
        //
        Officer officer = syncEngine.getOfficer();

        //
        // Using reflection to handle previous officer version (v3)
        //
        Method method = null;
        try {
            method = officer.getClass().getMethod("isAccountExpired", (Class)null);

            Boolean isAccountExpired = (Boolean)method.invoke(officer, (Object)null);
            return isAccountExpired.booleanValue();

        } catch (AbstractMethodError e) {
            //
            // Nothing to do. Maybe there is not the method (new v6 officers)
            //
        } catch (NoSuchMethodException e) {
            //
            // Nothing to do. Maybe there is not the method (new v6 officers)
            //
        } catch(Exception e) {

            log.error("Error invoking 'isAccountExpired' method", e);
        }
        return false;
    }

    /**
     * Indicates that a session with the given id is starting.
     * @param sessionId the session id
     */
    public void beginSession(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * Processes the given message. See the class description for more
     * information.
     *
     * @return the response message
     * @param context
     * @param message the message to be processed
     * @throws InvalidCredentialsException
     * @throws ProtocolException
     */
    public SyncML processMessage(SyncML message, MessageProcessingContext context)
    throws ProtocolException, InvalidCredentialsException, ServerException {
        SyncML response = null;

        //
        // Reset the cmdIdGenerator has specified in the spec
        //
        resetIdGenerator();

        //
        // Set content-type to use by server
        //
        java.util.Map requestProperties =
            (java.util.Map)context.getRequestProperty(context.PROPERTY_REQUEST_HEADERS);

        if ((String)requestProperties.get(REQUEST_CONTENT_TYPE) != null) {
            contentType =
                ((String)requestProperties.get(REQUEST_CONTENT_TYPE)).split(";")[0];
        }

        //
        // Each time a message is received for a particular session adjust the message ID
        //
        msgIdGenerator.next();

        //
        //  We maintain the message Id from client
        //
        lastMsgIdFromClient = message.getSyncHdr().getMsgID();

        //
        // Initialize the device ID from the client request
        //
        clientDeviceId = message.getSyncHdr().getSource().getLocURI();

        //
        // Set the device id in the LogContext in order to
        // have this information in the log
        //
        LogContext.setDeviceId(clientDeviceId);

        //
        // Read the LocName from the client request.
        // If LocName is set then use it as username during authentication
        // only if there is an MD5 authentication
        //
        String locName = message.getSyncHdr().getSource().getLocName();

        if (log.isTraceEnabled()) {
            log.trace("current state: " + getStateName(currentState));
        }

        Chal chal = ProtocolUtil.getStatusChal(message);

        try {
            switch (currentState) {
                case STATE_ERROR: // in case of error you can start a new initialization
                case STATE_START:
                    nextTimestamp = new SyncTimestamp();
                    nextTimestamp.start = System.currentTimeMillis();

                    syncState = new SyncState();

                    syncEngine.setSyncTimestamp(new Date(nextTimestamp.start));

                    //
                    // Set maximum message size
                    //
                    Meta meta = message.getSyncHdr().getMeta();
                    if (meta != null && meta.getMaxMsgSize() != null) {
                        syncState.maxMsgSize = meta.getMaxMsgSize().intValue();
                    }

                    //
                    // read device information from the database. Use this
                    // information with care until the client has been
                    // authenticated.
                    //
                    syncState.device = new Sync4jDevice(clientDeviceId);

                    boolean deviceFound = syncEngine.readDevice(syncState.device);

                    if (!deviceFound) {
                        //
                        // Added device into inventory
                        //
                        syncEngine.storeDevice(syncState.device);
                    }

                    syncState.syncMLVerProto =
                        message.getSyncHdr().getVerProto().getVersion();
                    syncEngine.setSyncMLVerProto(syncState.syncMLVerProto);

                    //
                    // Check if the client has sent the credential with the same
                    // authentication type imposed by the server
                    //
                    Cred cred = message.getSyncHdr().getCred();

                    syncState.clientCredential = cred;
                    boolean authenticationError = false;
                    if (checkClientAuth(cred)) {
                        //
                        // If authentication type is MD5 and LocName is specified
                        // then use it like username to authenticate credentials
                        //
                        if (clientAuth.equals(Cred.AUTH_TYPE_MD5) &&
                            locName != null && !locName.equals("")  ) {
                            cred.getAuthentication().setUsername(locName);
                        }

                        if (login(cred, clientDeviceId)) {
                            if (log.isInfoEnabled()) {
                                log.info( syncState.loggedPrincipal.getUsername()
                                        + '/'
                                        + syncState.loggedPrincipal.getDeviceId()
                                        + " logged in."
                                        );
                            }

                            //
                            // Be carefully when you change this method invokation
                            // because is used to notify a start sync session event
                            //
                            
                            fireStartSyncSessionEvent();

                            //
                            // Reloading the device from the database in order
                            // to update the most up-to-date version
                            // (something, like the officer, can have changed it
                            // in the db)
                            //
                            syncEngine.readDevice(syncState.device);

                            //
                            // If the client is authenticated and the authentication
                            // type is MD5, the given client next nonce must be stored
                            // to be used at the next session.
                            //
                            if (chal != null) {
                                if (Cred.AUTH_TYPE_MD5.equals(chal.getType())) {
                                    syncState.device.setServerNonce(Base64.decode(chal.getNextNonce().getValue()));
                                    syncEngine.storeDevice(syncState.device);
                                }
                            }
                        } else {
                           // authentication failed
                           authenticationError = true;
                        }
                    } else {
                        // missing credentials
                        authenticationError = true;
                    }

                    //
                    // Be carefully when you change this method invokation
                    // because is used to notify a start sync session event
                    // upon authentication failure
                    //
                    if(authenticationError) {
                        fireStartSyncSessionEventOnError();
                    }

                    syncEngine.setPrincipal(syncState.loggedPrincipal);
                    moveTo(STATE_PKG1_RECEIVING);

                case STATE_PKG1_RECEIVING:

                    response = processInitSyncMapMessage(message);

                    if (!isAuthenticated()) {
                        if (message.isLastMessage()) {
                            response.setLastMessage();
                        }
                        moveTo(STATE_START);
                    } else {
                        if (message.isLastMessage()) {
                            moveTo(STATE_PKG3_RECEIVING);
                            if (syncState.cmdCache1.size() == 0) {
                                response.setLastMessage();
                            }
                        }
                    }

                    break;

                case STATE_PKG3_RECEIVING:
                    response = processInitSyncMapMessage(message);

                    if (message.isLastMessage()) {

                        if (syncState.serverAuthenticationState == AUTH_ACCEPTED ||
                            syncState.serverAuthenticationState == AUTH_AUTHENTICATED
                           ) {
                            moveTo(STATE_PKG3_RECEIVED);
                        }
                        if (syncState.cmdCache3.size() == 0) {
                            response.setLastMessage();
                        }
                    }

                    break;

                case STATE_PKG3_RECEIVED:
                    response = processInitSyncMapMessage(message);

                    if (currentState != STATE_SESSION_SUSPENDED) {
                        if (syncState.cmdCache3.size() == 0) {
                            response.setLastMessage();
                            moveTo(STATE_PKG1_RECEIVING);
                        }
                    }

                    break;

                default:
                    logout();
                    throw new ProtocolException("Illegal state: " + currentState);
            }

            if (currentState == STATE_SESSION_SUSPENDED) {
                suspendSynchronization();
                response.setLastMessage();
                nextTimestamp.end = System.currentTimeMillis();
                endSession();
            } else if ((currentState != STATE_ERROR)                 &&
                       (currentState != STATE_START)                 &&
                       (currentState != STATE_ENDED)                 &&
                       response.isLastMessage()                      &&
                       (ProtocolUtil.noMoreResponse(response))       &&
                       (ProtocolUtil.getStatusChal(response) == null)  ) {

                nextTimestamp.end = System.currentTimeMillis();
                moveTo(STATE_END);
                endSession();
            } else if (currentState == STATE_ENDED) {
                //
                // This is needed to close the holder of the current session.
                //
                response.setLastMessage();
            }

        } catch (ProtocolException e) {
            log.error("Protocol Exception processing message", e);
            moveTo(STATE_ERROR, "Protocol Exception");
            throw e;
        }  catch (Throwable t) {
            log.error("Error processing message", t);
            moveTo(STATE_ERROR, "Error processing message");
            throw new ServerFailureException("Server error", t);
        }

        //
        // Reassign ordered cmdId to all commands in the given list.
        //
        List commands = response.getSyncBody().getCommands();
        ProtocolUtil.updateCmdId(commands);

        //
        // In order to handle the Status sent by the client as result of the
        // commands, we have to store the CommandDescriptors for the sent 
        // commands in Sync command.
        //
        storeCommandDescriptorForSentCommands(msgIdGenerator.current(), commands);

        try {
            //
            // If the response contains a Results or a Put command, it's needed to
            // set true the flag sent_server_caps into device table in order to
            // avoid to send again the server's caps.
            //
            if (syncInit.isServerCapsContainedInList()) {
                syncEngine.getDeviceInventory()
                          .setSentServerCaps(syncState.device.getDeviceId(), true);
                syncInit.setServerCapsContainedInList(false);
            }
        } catch(DeviceInventoryException e) {
            log.error("Exception updating the server caps flag in device table", e);
        }

        return response;
    }

    /**
     * Check if the client authentication type is contained in the list of
     * authetication types supported by server.
     *
     * @param cred the client credentials
     *
     * @return true if client auth type is supported by server, false otherwise
     */
    private boolean checkClientAuth(Cred cred) {

        //
        // Set the client authentication that will be used in the other part
        // of the code too
        //
        Officer officer = syncEngine.getOfficer();
        clientAuth = officer.getClientAuth();

        //
        // The clientAuth is a comma separeted list of the supported
        // authentication type. The first type in the list is the default type
        // to use if the client does not send its credentials.
        //
        String defaultClientAuth =
            new StringTokenizer(clientAuth, ",").nextToken();

        if (cred == null) {
            syncState.authenticationState = syncState.AUTH_MISSING_CREDENTIALS;
            clientAuth = defaultClientAuth;
            return false;
        }

        String clientAuthSent = cred.getType();

        if (clientAuth.indexOf(clientAuthSent) != -1) {
            //
            // If the authentication type sent by client is supported by the
            // officer, then using it to authenticate the client
            //
            clientAuth = clientAuthSent;
            return true;
        }

        //
        // If the client requires an authentication type not supported by officer,
        // then the server sends a Chal with the default clientAuth.
        //
        clientAuth = defaultClientAuth;

        syncState.authenticationState = AUTH_INVALID_CREDENTIALS;
        return false;
    }

    /**
     * Processes an error condition. This method is called when the error is
     * is not fatal and is manageable at a protocol/session level. This results
     * in a well formed SyncML message with an appropriete error code.
     * <p>
     * Note that the offending message <i>msg</i> cannot be null, meaning that
     * at least the incoming message was a SyncML message. In this context,
     * <i>RepresentationException</i>s are excluded.
     *
     * @param the offending message - NOT NULL
     * @param the exception representing the error condition - NOT NULL
     *
     * @throws com.funambol.framework.core.Sync4jException in case of unexpected errors
     *
     * @return the response message
     */
    public SyncML processError(SyncML msg, Throwable error)
    throws Sync4jException {
        SyncHdr msgHeader = msg.getSyncHdr();

        Item[] items = new Item[0];
        int status = StatusCode.SERVER_FAILURE;

        items = new Item[1];
        items[0] = new Item(
                       null, // target
                       null, // source
                       null, // meta
                       new ComplexData(error.getMessage()),
                       false //MoreData
                   );

        if (error instanceof ServerException) {
            status = ((ServerException)error).getStatusCode();
        }

        Status statusCommand = new Status(
                cmdIdGenerator.next()               ,
                msgHeader.getMsgID()                ,
                "0" /* command ref */               ,
                "SyncHdr" /* see SyncML specs */    ,
                new TargetRef(msgHeader.getTarget()),
                new SourceRef(msgHeader.getSource()),
                null /* credentials */              ,
                null /* challenge */                ,
                new Data(status)                    ,
                new Item[0]
            );

        String serverURI =
                Configuration.getConfiguration().getServerConfig().getEngineConfiguration().getServerURI();

        //
        // If serverURI is empty then we use the Target LocURI sent by client.
        //
        if (serverURI == null || serverURI.equals("")) {
            serverURI = msgHeader.getTarget().getLocURI();
        }

        SyncHdr syncHeader = new SyncHdr (
                                  msgHeader.getVerDTD()                        ,
                                  msgHeader.getVerProto()                      ,
                                  msgHeader.getSessionID()                     ,
                                  msgHeader.getMsgID()                         ,
                                  new Target(msgHeader.getSource().getLocURI()),
                                  new Source(serverURI)                        ,
                                  null  /* response URI */                     ,
                                  false                                        ,
                                  null /* credentials */                       ,
                                  null /* metadata */
                               );

        SyncBody syncBody = new SyncBody(
                                new AbstractCommand[] { statusCommand },
                                true /* final */
                            );

        if(logPush.isTraceEnabled()) {
            logPush.trace(SyncEvent.createEndSyncEventOnError(LogContext.getUserName(),
                                                           LogContext.getDeviceId(),
                                                           LogContext.getSessionId(),
                                                           null, // sync type
                                                           LogContext.getSourceURI(),
                                                           error.getMessage(),
                                                           Integer.toString(status)));
        }

        moveTo(STATE_ERROR, error.toString());

        return new SyncML(syncHeader, syncBody);
    }

    /**
     * Called by the <i>SessionManager</i> when the session is expired.
     * It logs out the credentials and release acquired resources.
     */
    public void expire() {
        logout();
    }

    /**
     * Called to interrupt the processing in case of errors depending on
     * extenal causes (i.e. the transport). The current implementation just move
     * the session state to the error state.
     * <p>
     * NOTE that the current implementation simply moves the state of the session
     * to <i>STATE_ERROR</i>.
     *
     * @param statusCode the error code
     *
     * @see com.funambol.framework.core.StatusCode for valid status codes
     *
     */
    public void abort(int statusCode) {
        moveTo(STATE_ERROR, "Session aborted with status code: " + StatusCode.getStatusDescription(statusCode));
    }

    /**
     * Called to permanently commit the synchronization. It does the following:
     * <ul>
     *  <li>persists the <i>last</i> timestamp to the database for the sources
     *      successfully synchronized
     * </ul>
     */
    public void commit() {

        assert (syncState.loggedPrincipal != null);

        long id = syncState.loggedPrincipal.getId();

        syncEngine.commit(getCurrentState(), nextTimestamp, id);

    }

    // --------------------------------------------------------- Private methods

    /**
     * Processes the given message applying initialization processing first,
     * and then synchronization processing.
     *
     * @param message the message to be processed
     *
     * @return the response message
     *
     * @throws ProtocolException in case of protocol errors
     */
    private SyncML processInitSyncMapMessage(SyncML message)
    throws ProtocolException {
        SyncInitialization  init     = null;
        SyncModifications   sync     = null;
        SyncMapping         map      = null;
        SyncSuspend         suspend  = null;
        SyncML              response = null;
        SyncHdr             header   = null;

        //
        // The maximum message size can be set in each messages
        //
        Meta metaIn = message.getSyncHdr().getMeta();
        if (metaIn != null &&
            metaIn.getMaxMsgSize() != null &&
            metaIn.getMaxMsgSize().intValue() > 0) {

            syncState.maxMsgSize = metaIn.getMaxMsgSize().intValue();
        }

        //
        // It indicates if the message is processed by the init or by the sync or
        // by the map components. It's used to avoid to process the same message
        // several time.
        //
        boolean messageProcessed = false;

        String msgId = msgIdGenerator.current();

        Cred cred = checkServerAuthentication(message);
        Chal chal = getChal(message);
        String serverAuth = syncEngine.getOfficer().getServerAuth();

        boolean suspendRequired = ProtocolUtil.isSuspendRequired(message);

        if (suspendRequired) {
            if (log.isInfoEnabled()) {
                log.info("Suspend required");
            }
            moveTo(STATE_SESSION_SUSPENDED);
            //
            // We clear the caches because no commands must be send.
            //
            syncState.cmdCache1.clear();
            syncState.cmdCache3.clear();
        }

        //
        // First of all check if this message contains initialization elements.
        //
        if (ProtocolUtil.isInitMessage(message)) {
            isInInit  = true;

            init = processInitMessage(message);

            messageProcessed = true;
            //
            // Now we set server credentials. If the client challenged the server,
            // we use the requested auth mechanism, otherwise, we use the default
            // server authentication scheme.
            // Not that if credentials are not set in server configuration,
            // getServerCredential returns null, which means that no credentials
            // will be returned to the client.
            //
            if (chal == null) {
                if (serverAuth.equalsIgnoreCase(Cred.AUTH_NONE)) {
                    init.setServerCredentials(null);
                    syncState.serverAuthenticationState = AUTH_AUTHENTICATED;
                } else {
                    if (syncState.serverAuthenticationState != AUTH_ACCEPTED) {
                        Meta meta = new Meta();
                        meta.setType(serverAuth);
                        meta.setNextNonce(new NextNonce(Base64.encode(
                            syncState.device.getServerNonce()))
                        );

                        init.setServerCredentials(
                            syncEngine.getServerCredentials(new Chal(meta), syncState.device)
                        );
                    }
                }
            } else {
                if (cred != null) {
                    init.setServerCredentials(cred);
                }
            }

            header = init.getResponseHeader(msgId);
        }

        //
        // If we are still not authenticated, we do not process sync and map
        // commands and we directly return the init response, which will
        // contains proper status for all commands.
        //
        if (!isAuthenticated()) {
            response = init.getResponseMessage(msgId);
        } else {
            //
            // If the client is authenticated and the authentication
            // type is MD5, the given client next nonce must be stored
            // to be used at the next session.
            //
            if (chal != null) {
                if (Cred.AUTH_TYPE_MD5.equals(chal.getType())) {
                    syncState.device.setServerNonce(Base64.decode(chal.getNextNonce().getValue()));
                    syncEngine.storeDevice(syncState.device);
                }
            }

            //
            // Then process any incoming Map command
            // The handling of the Map commands is here since the Funambol
            // clients could sent the Map commands before sending the Sync
            // command when, e.i., the previous sync was interrupted before that
            // the client has finished to send its mapping. In this case, the
            // server has to store the new mapping updating also the last anchor
            // in order to avoid the duplication of the items (sent by the server
            // in the previous sync but of which it has not received the status).
            //
            if (ProtocolUtil.isMapMessage(message) || ProtocolUtil.noMoreResponse(message)) {
                isInInit  = false;

                map = processMapMessage(message);

                messageProcessed = true;

                map.setServerCredentials(cred);
                if (header == null) {
                    header = map.getResponseHeader(msgId);
                }
            }

            //
            // Then check if the message contains synchronization elements.
            //
            if (ProtocolUtil.isSyncMessage(message)) {
                isInInit  = false;

                sync = processSyncMessage(message);

                messageProcessed = true;

                sync.setServerCredentials(cred);
                if (header == null) {
                    header = sync.getResponseHeader(msgId);
                }
            }

            if (!messageProcessed && isInInit) {
                init = processInitMessage(message);

                //
                // Now we set server credentials. If the client challenged the server,
                // we use the requested auth mechanism, otherwise, we use the default
                // server authentication scheme.
                // Not that if credentials are not set in server configuration,
                // getServerCredential returns null, which means that no credentials
                // will be returned to the client.
                //
                if (chal == null) {
                    if (serverAuth.equalsIgnoreCase(Cred.AUTH_NONE)) {
                        init.setServerCredentials(null);
                        syncState.serverAuthenticationState = AUTH_AUTHENTICATED;
                    } else {
                        if (syncState.serverAuthenticationState != AUTH_ACCEPTED) {
                            Meta meta = new Meta();
                            meta.setType(serverAuth);
                            meta.setNextNonce(new NextNonce(Base64.encode(
                                syncState.device.getServerNonce()))
                            );

                            chal = new Chal(meta);
                            init.setServerCredentials(
                                syncEngine.getServerCredentials(chal, syncState.device)
                            );
                        }
                    }
                } else {
                    if (cred != null) {
                        init.setServerCredentials(cred);
                    }
                }
                header = init.getResponseHeader(msgId);
            }

            if (!messageProcessed && suspendRequired) {
                //
                // This happens if the client sends a suspend request after his
                // Sync command. The message isn't recognized as Sync message because
                // it doesn't contain any Sync commands and isn't recognized as
                // Map message because it doesn't contain any Map commands and it
                // isn't processed by SyncInitialization because the server isn't
                // in the initialization phase.
                //
                // In this case the message is processed by the SyncInitialization.
                //
                suspend = processSuspendMessage(message);
                header  = suspend.getResponseHeader(msgId);
                init = null;
                sync = null;
                map  = null;
            }

            if (currentState != STATE_SESSION_SUSPENDED) {
                //
                // If we do not have a separate initialization, we jump to the
                // state STATE_PKG3_RECEIVING.
                //
                if ((init != null) && (sync != null)) {
                    if (message.isLastMessage()) {
                        moveTo(STATE_PKG3_RECEIVED);
                    } else {
                        moveTo(STATE_PKG3_RECEIVING);
                    }
                }
            }

            //
            // We check the status sends by the client as result at the
            // commands send by the server notifying its to the engine
            //
            List statuses = ProtocolUtil.filterCommands(
                message.getSyncBody().getCommands(),
                new String[] { Status.COMMAND_NAME }
            );
            handleReceivedStatuses(statuses);

            //
            // After processing the commands, if any mapping was modified, the
            // change must be persisted
            //
            syncEngine.storeMappings();

            //
            // If client max msg size is too small, do not even try
            //
            AbstractCommand[] cmdToSend = null;
            if (checkMaxMsgSize()) {
                messageSize = sizeCalculator.getSize(header)
                            + sizeCalculator.getSyncBodyOverhead()
                            + sizeCalculator.getRespURIOverhead()
                            ;
                cmdToSend = commandsToSend(init, sync, map, suspend);
            } else {
                cmdToSend = new AbstractCommand[1];

                Status status = (Status)init.getResponseCommands(msgId).get(0);

                status.setData(new Data(StatusCode.SYNCHRONIZATION_FAILED));

                cmdToSend[0] = status;

                if (log.isInfoEnabled()) {
                    log.info( "The MaxMsgSize ("
                            + syncState.maxMsgSize
                            + ") is smaller than the minimum message size "
                            + "supported by the server. The session will abort."
                            );
                }
            }

            try {
                response = new SyncML(
                    header,
                    new SyncBody(cmdToSend, false)
                );
            } catch (RepresentationException e) {
                throw new ProtocolException(e.getMessage());
            }
        }

        return response;
    }

    /**
     * Processes the initialization commands in the given message like if it
     * contained only initialization information. Init commands are:
     * <ul>
     *   <li>SyncHeader (credentials)</li>
     *   <li>Put (client capabilities)</li>
     *   <li>Get (server capabilities)</li>
     *   <li>Alert (database alerting)</li>
     * </ul>
     *
     * @param message the message to be processed
     *
     * @return the SyncInitialization object representing the initialization
     *         request-response associated to the incoming message
     *
     * @throws ProtocolException in the case of violation of the specification
     */
    private SyncInitialization processInitMessage(SyncML message)
    throws ProtocolException {
        if (log.isTraceEnabled()) {
            log.trace("Processing the initialization commands");
        }

        try {

            syncInit = new SyncInitialization(message.getSyncHdr() ,
                                              message.getSyncBody());

            //
            // Set content-type to use by server.
            // For example, if it must request the capabilities to client the
            // server have to know the content type to use (WBXML or XML)
            //
            syncInit.setContentType(contentType);

            //
            // Store the information that will be used in the Syncronization process
            //
            syncState.addClientAlerts(syncInit.getClientAlerts());

            syncInit.setIdGenerator(cmdIdGenerator);

            //
            // Sets the server authentication type so that in the case the client
            // will be challenged accordingly
            //
            syncInit.setClientAuth(clientAuth);

            //
            // If authentication type is MD5 then generate a new NextNonce
            //
            if (clientAuth.equalsIgnoreCase(Cred.AUTH_TYPE_MD5)) {
                NextNonce nonce = ProtocolUtil.generateNextNonce();
                syncInit.setNextNonce(nonce);
                syncState.device.setClientNonce(nonce.getValue());
                syncEngine.storeDevice(syncState.device);
            }

            //
            // Check always if there are the capabilities into inventory
            // for the device
            //
            Long idCaps = null;
            if (syncState.device.getCapabilities() != null) {
                idCaps = syncState.device.getCapabilities().getId();
            }

            //
            // If the client gave its capabilities, process them
            //
            DevInf devInf = syncInit.getClientDeviceInfo();
            processClientCapabilities(devInf, idCaps);

            if (isAuthenticated()) {
                nextTimestamp.tagClient = String.valueOf(System.currentTimeMillis());

                syncInit.setAuthorizedStatusCode(StatusCode.AUTHENTICATION_ACCEPTED);

                if (idCaps == null && devInf == null) {
                    syncInit.setClientCapabilitiesRequired(true);
                }

                //
                // Gets the databases requested for synchronization and for each of
                // them checks if the database exists on the server and if the
                // credentials are allowed to synchronize it
                //
                dbs = syncInit.getDatabasesToBeSynchronized(syncState.loggedPrincipal);

                if(!containsAllDataStores(syncState.device.getCapabilities(), dbs) ) {
                    syncInit.setClientCapabilitiesRequired(true);
                }

                //
                // This will change the status code of the elements of clientDBs to
                // reflect the availability and accessibility of the given databases
                // on the server
                //
                syncEngine.prepareDatabases(syncState.loggedPrincipal, dbs, nextTimestamp);

                if (log.isTraceEnabled()) {
                    log.trace("Requested databases: " + Arrays.asList(dbs));
                }

                List dbList         = new ArrayList();
                Alert alertCommand  = null;
                Item  alertItem     = null;
                FilterClause filter = null;

                for (int i = 0; ((dbs != null) && (i < dbs.length)); ++i) {
                    syncInit.setStatusCodeForCommand(
                            dbs[i].getAlertCommand(),
                            dbs[i].getStatusCode()
                    );

                    if (dbs[i].isOkStatusCode()) {

                        if (syncEngine.getClientSource(dbs[i].getName()) == null) {
                            syncEngine.addClientSource(
                                new MemorySyncSource(
                                    dbs[i].getName(),
                                    dbs[i].getName())
                            );
                        }
                        alertCommand = dbs[i].getAlertCommand();
                        alertItem    = ((Item)alertCommand.getItems().get(0));

                        filter = ProtocolUtil.extractFilterClauseFromItem(alertItem);

                        syncEngine.setFilter(dbs[i].getName(), filter);

                        Meta m = alertItem.getMeta();
                        if (m != null) {
                            Long l = m.getMaxObjSize();
                            if (l != null) {
                                syncState.maxObjSizes.put(dbs[i].getName(), l);
                            } else {
                                syncState.maxObjSizes.put(dbs[i].getName(), 0L);
                            }
                        }
                        dbList.add(dbs[i]);
                    }
                }
                dbs = (Database[])dbList.toArray(new Database[dbList.size()]);

                syncEngine.setDbs(dbs);

                //
                // Setting the databases to synchronize. This will force the relative
                // <Alert>s to be inserted in the response.
                //
                syncInit.setDatabases(dbs);

                //
                // Has the server caps already been sent to the client?
                //
                boolean isSentServerCaps = syncState.device.isSentServerCaps();
                syncInit.setSentServerCaps(isSentServerCaps);

                //
                // Setting server capabilities only if needed
                //
                if (!isSentServerCaps ||
                    syncInit.isServerCapabilitiesRequested()) {

                    syncInit.setServerCapabilities(
                        syncEngine.getServerCapabilities(syncInit.getDTDVersion())
                    );
                }
            } else {
                syncInit.setAuthorizedStatusCode(
                    authenticationState2StatusCode(syncState.authenticationState)
                );
            }

            return syncInit;

        } catch (Sync4jException e) {
            throw new ProtocolException(e);
        }
    }

    /**
     * Check if the client MaxMsgSize is greater than server minmsgsize.
     * If the client MaxMsgSize is smaller than minmsgsize then change
     * the status code of SyncHdr into StatusCode.SYNCHRONIZATION_FAILED.
     *
     * @return true if the client maxmsgsize is ok, false otherwise
     */
    private boolean checkMaxMsgSize() {

        final long minMsgSize =
                Configuration.getConfiguration().getServerConfig().getEngineConfiguration().getMinMaxMsgSize();

        if (log.isTraceEnabled()) {
            log.trace( "Checking if MaxMsgSize is larger than the minimum "
                      + "size supported by the server ("
                      + minMsgSize
                      + ")"
                      );
        }

        return ((syncState.maxMsgSize == 0) || (syncState.maxMsgSize >= minMsgSize));
    }

    /**
     * Check if are required the server capabilities and check their size.
     * If that size added at size of SyncHdr and SyncHdr Status and all
     * Alert command is more than MaxMsgSize then set Get Status with
     * StatusCode.REQUESTED_ENTITY_TOO_LARGE
     */
    private void checkSizeCapabilities() {
        //
        // If server capabilities are required then check their size in order
        // to establish if it will be possible to send them
        //
        AbstractCommand[] cmds =
            (AbstractCommand[])syncState.cmdCache1.toArray(new AbstractCommand[0]);
        Status statusGet = (Status)ProtocolUtil.filterCommands(
            cmds,
            Status.class,
            Get.COMMAND_NAME
        );

        if (statusGet != null) {
            ArrayList results = ProtocolUtil.filterCommands(
                cmds,
                Results.class
            );

            if (!results.isEmpty()) {
                //
                // Calculate size Results
                //
                long sizeCap = sizeCalculator.getSyncMLOverhead()
                             + sizeCalculator.getSyncBodyOverhead()
                             + sizeCalculator.getSize((Results)results.get(0));
                if (sizeCap > syncState.maxMsgSize) {
                    if (log.isInfoEnabled()) {
                        log.info("Capabilities too big!");
                    }
                    statusGet.setData(new Data(StatusCode.REQUESTED_ENTITY_TOO_LARGE));

                    syncState.cmdCache1.remove(results);
                }
            }
        }
    }

    /**
     * Extracts or create a Chal object that represents how server credentials
     * should be created.<br>
     * If the client challenged the server, we must use the requested auth
     * mechanism, otherwise, we use the default server authentication scheme.
     *
     * @param msg the request message
     *
     * @return a Chal object reqpresenting how server credentials should be
     *         formatted.
     */
    private Chal getChal(final SyncML msg) {
        Chal chal = ProtocolUtil.getStatusChal(msg);
        if (chal != null) {
            if (log.isTraceEnabled()) {
                log.trace("Challenged server authentication with scheme " + chal.getType());
            }
        }

        return chal;
    }

    /**
     * Checks if the server authentication succedeed. It throws a
     * ProtocolException if not.
     *
     * @param msg the SyncML message to check
     *
     * @return true if the server should retry authentication, false otherwise
     *
     * @throw ProcotolException if server authentication did not succeed
     */
    private Cred checkServerAuthentication(SyncML msg)
    throws ProtocolException {
        String serverAuth = syncEngine.getOfficer().getServerAuth();
        int headerStatusCode = ProtocolUtil.getHeaderStatusCode(msg);
        if (headerStatusCode == -1) {
            return null;
        }

        if (headerStatusCode == StatusCode.MISSING_CREDENTIALS) {
            //
            // The server has to send the credentials for the first time
            //
            syncState.serverAuthenticationState = AUTH_RETRY_1;
            return syncEngine.getServerCredentials(getChal(msg), syncState.device);
        } else if (headerStatusCode == StatusCode.INVALID_CREDENTIALS) {
            //
            // If server has used basic authentication and the client has
            // required basic authentication, means that the client is unable
            // to authenticate the server
            //
            String authRequiredByClient = Cred.AUTH_NONE;
            Chal chal = getChal(msg);
            if (chal != null) {
                authRequiredByClient = chal.getType();
            }
            if (Cred.AUTH_TYPE_BASIC.equalsIgnoreCase(serverAuth) &&
                Cred.AUTH_TYPE_BASIC.equalsIgnoreCase(authRequiredByClient)
               ) {
                throw new ProtocolException("Unable to authenticate to the client");
            } else if (!authRequiredByClient.equalsIgnoreCase(serverAuth) ||
                       (authRequiredByClient.equalsIgnoreCase(serverAuth) &&
                        syncState.serverAuthenticationState == AUTH_UNAUTHENTICATED)
                      ) {
                   syncState.serverAuthenticationState = AUTH_RETRY_1;
                   return syncEngine.getServerCredentials(getChal(msg), syncState.device);
            } else {
                throw new ProtocolException("Unable to authenticate to the client");
            }
        } else if (headerStatusCode == StatusCode.AUTHENTICATION_ACCEPTED) {
            //
            // Authenticated with code 212
            //
            if (log.isTraceEnabled()) {
                log.trace("Server logged (code 212)");
            }
            syncState.serverAuthenticationState = AUTH_ACCEPTED;
        } else {
            //
            // Authenticated with code 200
            //
            if (log.isTraceEnabled()) {
                log.trace("Server authenticated (code 200)");
            }
            syncState.serverAuthenticationState = AUTH_AUTHENTICATED;

            Chal chal = getChal(msg);
            if (chal != null) {
                return syncEngine.getServerCredentials(chal, syncState.device);
            }
        }

        return null;
    }

    /**
     * Processes the given synchronization message.
     *
     * @param syncRequest the message to be processed
     *
     * @return the SyncModifications object representing the request-response
     *         under processing
     *
     * @throws ProtocolException in case of errors
     */
    private SyncModifications processSyncMessage(SyncML syncRequest)
    throws ProtocolException {

        if (log.isTraceEnabled()) {
            log.trace("Processing the given synchronization message");
            log.trace("client sources: " + syncEngine.getClientSources());
        }

        try {
            modifications =
                    new SyncModifications(syncRequest.getSyncHdr() ,
                                          syncRequest.getSyncBody(),
                                          syncEngine.getDbs());

            //
            // Store eventually capabilities sent by client after a request
            // of capabilities sent by server
            //

            //
            // Check always if there are the capabilities into inventory
            // for the device
            //
            Long idCaps = null;
            if (syncState.device.getCapabilities() != null) {
                idCaps = syncState.device.getCapabilities().getId();
            }

            //
            // If the client gave its capabilities, process them
            //
            DevInf devInf = modifications.getClientDeviceInfo();
            processClientCapabilities(devInf, idCaps);

            Sync[] syncCommands = modifications.getClientSyncCommands();

            //
            // Check if into Sync command there is a MaxObjSize for LargeObject
            //
            checkMaxObjSizeIntoSync(syncCommands);

            //
            // TBD: verify this!!!!
            //
            if (syncRequest.isLastMessage()) {
                syncEngine.setLastMessage(true);
            }

            List responseCommands = processModifications(modifications);

            if (log.isTraceEnabled()) {
                log.trace("responseCommands: " + responseCommands);
            }

            modifications.setIdGenerator(cmdIdGenerator);
            modifications.setFlag(Flags.FLAG_ALL_RESPONSES_REQUIRED);

            Status[] statusSyncs = (Status[])ProtocolUtil.filterCommands(
                responseCommands,
                new String[]{ Status.COMMAND_NAME }
            ).toArray(new Status[0]);

            modifications.setClientModificationsStatus(statusSyncs);

            // .setServerModifications sets all the modification commands
            // required to be send to client from Server

            // cannot block this whole code,as this code may be used
            // by other DBS within same sync but differnt alert code.
            AbstractCommand[] serverModifications =
                    (AbstractCommand[])ProtocolUtil.filterCommands(
                        responseCommands,
                        new String[]{Sync.COMMAND_NAME}
                    ).toArray(new AbstractCommand[0]);

            //
            // The server must send its modifications only after the client sent
            // the last message
            //
            modifications.setServerModifications(serverModifications);

            return modifications;

        } catch (Sync4jException e) {
            throw new ProtocolException(e);
        }
    }

    /**
     * Checks if the given command can be sent in the current message without
     * exceeding maxMsgSize.
     *
     * @param cmd the command to check
     *
     * @return true if the command can be sent, false otherwise
     */
    private boolean checkSize(AbstractCommand cmd) {
        return (syncState.maxMsgSize != 0) &&
               ( (messageSize + sizeCalculator.getCommandSize(cmd)) < syncState.maxMsgSize);
    }

    /**
     * Checks if the given size is available in the current message without
     * exceeding maxMsgSize starting from the given message size.
     *
     * @param size the command to check
     * @param msgSize the message size
     *
     * @return true if the command can be sent, false otherwise
     */
    private boolean checkSize(long size, long msgSize) {
        return (syncState.maxMsgSize != 0) &&
               ( (msgSize + size) < syncState.maxMsgSize);
    }

    /**
     * Checks if into each Sync command there is a MaxObjSize for LargeObject.
     * If it is specified then its value will be cached into SyncState using
     * source URI like key.
     *
     * @param Sync[] the array of Sync command client
     */
    private void checkMaxObjSizeIntoSync(Sync[] syncCmds) {
        if (syncCmds != null && syncCmds.length > 0) {
            //
            // The MaxObjSize could be different for each Sync command
            //
            String sourceName = null;
            long   maxObjSize = 0;
            for (int i=0; i<syncCmds.length; i++) {
                if (syncCmds[i].getMeta() != null &&
                    syncCmds[i].getMeta().getMaxObjSize() != null) {

                    sourceName = syncCmds[i].getTarget().getLocURI();
                    sourceName = stripQueryString(sourceName);
                    maxObjSize = syncCmds[i].getMeta().getMaxObjSize().longValue();

                    syncState.maxObjSizes.put(sourceName, maxObjSize);
                }
            }
        }
    }

    /**
     * Executes the given modifications and for each of them returns a status
     * command. It forwards the execution to the synchronization engine.
     *
     * @param modifications synchronization commands containing the modifications
     *                 that the client requires
     *
     * @return an array of command objects, each containig the result of
     *         a modification or a modification itself
     * @throws Sync4jException
     */
    private List processModifications(SyncModifications modifications)
    throws Sync4jException {
        Sync[] syncCommands = modifications.getClientSyncCommands();

        List responseCommands = new ArrayList();

        if ((syncCommands == null) || (syncCommands.length == 0)) {
            //
            // Nothing to process!
            //
            return responseCommands;
        }

        SyncHdr    header           = modifications.getSyncHeader();
        String     msgId            = header.getMsgID()            ;
        boolean    headerNoResponse = header.isNoResp()            ;

        syncEngine.setCommandIdGenerator(cmdIdGenerator);

        //
        // First of all prepare the memory sources with the modification commands
        // receveid by the client. Sent the Status commands to check if there
        // is a "Refresh required": in this case is necessary to do a slow sync.
        //

        Status[] statusCommands =
            (Status[])ProtocolUtil.filterCommands(
                                             modifications.getClientCommands(),
                                             Status.class
            ).toArray(new Status[0]);

        //
        // Check if in the given list of commands there is data to add
        // at the previous data.
        //
        if ((syncCommands != null) && (syncCommands.length>0)) {
            checkForReceivedLargeObject(syncCommands);
        }

        //
        // First of all prepare the memory sources with the modification commands
        // receveid by the client
        //
        try {
            prepareMemorySources(syncCommands, statusCommands);
        } finally {
            LogContext.setSourceURI(null);
        }

        try {
            syncEngine.sync(syncState.loggedPrincipal);
        } catch (Sync4jException e) {
            log.error("Error performing modification phase", e);
        } finally {
            LogContext.setSourceURI(null);
        }

        //
        // Status code for commands
        //
        if (headerNoResponse == false) {
            //
            // Status for Sync command
            //
            responseCommands.addAll(statusForSyncs(syncCommands));

            //
            // Status for server-side executed modification commands
            //
            /**
             * @todo: the server operation that will be sent to the client
             * must be cached, then the handler should get some items in order
             * to fit the max message size of the client
             */
            Status[] operationStatus = syncEngine.getModificationsStatusCommands(msgId);

            for (int i=0; i<operationStatus.length; ++i) {
                responseCommands.add(operationStatus[i]);
            }
        }

        //
        // SyncCommands sent back to the client
        // No sync sent to client if ONE_WAY_FROM_CLIENT
        //
        Database[] dbs = syncEngine.getDbs();
        ItemizedCommand[] commands = null;
        String uri = null;
        int countErrorDB = 0;
        for (int i = 0; (i < dbs.length); ++i) {
            //
            // If the alert represents a client only action, just ignore it
            //
            if (AlertCode.isClientOnlyCode(dbs[i].getMethod())) {
                continue;
            }

            uri = dbs[i].getName();

            LogContext.setSourceURI(uri);

            SyncOperation[] operations = syncEngine.getSyncOperations(uri);

            commands = syncEngine.operationsToCommands(operations, uri, contentType);

            LogContext.setSourceURI(null);

            Long noc = isNumberOfChangesSupported()
                     ? new Long(commands.length)
                     : null
                     ;

            responseCommands.add(
                    new Sync(
                            cmdIdGenerator.next(),
                            false,
                            null,
                            dbs[i].getTarget(),
                            dbs[i].getSource(),
                            null,
                            noc,
                            commands
                    )
            );
            syncEngine.resetSyncOperations(uri);
            if (!dbs[i].isOkStatusCode()) {
                countErrorDB++;
            }
        } // next i

        //
        // If all databases to sync are in error, then the server ends the
        // current synchronization session.
        //
        if (dbs.length == countErrorDB) {
            moveTo(STATE_ERROR, "All datastores are in error");
            endSession();
        }

        return responseCommands;
    }

    /**
     * Process a client message with a Suspend request.
     * @param message
     * @return the response message
     * @throws ProtocolException
     */
    private SyncSuspend processSuspendMessage(SyncML message)
    throws ProtocolException {
        if (log.isInfoEnabled()) {
            log.info("Handling suspend request ...");
        }

        try {
            SyncSuspend suspend = new SyncSuspend(
                                      message.getSyncHdr(),
                                      message.getSyncBody()
                                  );

            suspend.setIdGenerator(cmdIdGenerator);

            if (log.isInfoEnabled()) {
                log.info("Performing suspend...");
            }

            handleReceivedMaps(suspend.getMapCommands());

            return suspend;

        } catch (Sync4jException e) {
            throw new ProtocolException(e);
        } finally {
            LogContext.setSourceURI(null);
        }
    }

    /**
     * Process the completion message from the client check status for sync
     * command and update client mapping
     * @param message
     * @return the response message
     * @throws ProtocolException
     */
    private SyncMapping processMapMessage(SyncML message)
    throws ProtocolException {
        if (log.isTraceEnabled()) {
            log.trace("Handling mapping ...");
        }

        try {
            SyncMapping mapping = new SyncMapping(
                                      message.getSyncHdr(),
                                      message.getSyncBody()
                                  );

            mapping.setIdGenerator(cmdIdGenerator);

            handleReceivedMaps(mapping.getMapCommands());

            return mapping;

        } catch (Sync4jException e) {
            throw new ProtocolException(e);
        } finally {
            LogContext.setSourceURI(null);
        }
    }

    /**
     * Adds item properties to the persistent mapping and updates the last
     * anchor for each item contained in every Map command sent by client.
     * In this way, it's possible say that a new Item was received by the client
     * since the client has sent the Map command for it. In this case, not only
     * the mapping (LUID - GUID) have to be updated with the information sent by
     * client, but also the last anchor in order to avoid duplicates.
     *
     * @param mapCommands the list of Map command to handle
     */
    private void handleReceivedMaps(com.funambol.framework.core.Map[] mapCommands) {
        String    uri      = null;
        MapItem[] mapItems = null;

        for (int j=0; ((mapCommands != null) && (j<mapCommands.length)); ++j) {
            uri = mapCommands[j].getTarget().getLocURI();

            uri = stripQueryString(uri);

            LogContext.setSourceURI(uri);

            mapItems =
                (MapItem[])mapCommands[j].getMapItems().toArray(
                new MapItem[0]);

            String lastAnchor = syncEngine.getLastAnchor(uri);

            ClientMapping mapping =
                syncEngine.getMapping(syncState.loggedPrincipal, uri, false);

            for (int i = 0; i < mapItems.length; i++) {
                MapItem mapItem = mapItems[i];

                String guid = mapItem.getTarget().getLocURI();
                String luid = mapItem.getSource().getLocURI();
                mapping.updateMapping(guid, luid, lastAnchor);

                mapping.updateLastAnchor(guid, luid, lastAnchor);
            }
        }
    }

    /**
     * Makes a state transition. Very simple implementation at the moment: it
     * changes the value of <i>currentState</i> to the given value only if the
     * <i>currentState</i> is not <i>STATE_ENDED</i> .
     *
     * @param state the new state
     */
    private void moveTo(int state) {
        moveTo(state, null);
    }

    /**
     * Makes a state transition. Very simple implementation at the moment: it
     * changes the value of <i>currentState</i> to the given value only if the
     * <i>currentState</i> is not <i>STATE_ENDED</i>.
     * Moreover the given message is used in logging state change.
     *
     * @param state the new state
     * @param message the message to log
     */
    private void moveTo(int state, String message) {

        if (currentState != SessionHandler.STATE_ENDED) {

            //
            // Be careful....just log messages based on the state and currentState.
            // DON'T add any application code in this block ... since it will be
            // executed just if INFO log is enbled
            //
            if (log.isInfoEnabled()) {

                log.info(getMoveToMessage(state, message));
                
                if (state == SessionHandler.STATE_ENDED &&
                    currentState == SessionHandler.STATE_ERROR) {

                    log.info("Synchronization completed with error(s)");

                } else if (state == SessionHandler.STATE_ENDED) {

                    log.info("Synchronization completed");
                }
            }
            
            currentState = state;
        }
    }


    /**
     * Prepares the memory sources with the modification commands receveid
     * by the client.
     * <p>
     * Note that if the requested synchronization is a slow sync, the items are
     * inserted as "existing" items, regardless the command they belong to.
     * (maybe this will change as soon as the specification becomes clearer)
     * If there is no alert for slow sync, is need to check if exist a status
     * with "Refresh required" (code 508)
     *
     * @param syncCommands the commands used to prepare the source
     * @param statusCommands the status commands sent by the client
     *
     */
    private void prepareMemorySources(Sync[] syncCommands, Status[] statusCommands)
    throws Sync4jException {

        List sources = syncEngine.getClientSources();

        //
        // For efficiency: put the databases in a HashMap
        //
        HashMap dbMap = new HashMap();
        Database databases[] = syncEngine.getDbs();
        for (int i=0; ((databases != null) && (i<databases.length)); ++i) {
            dbMap.put(databases[i].getName(), databases[i]);
        }

        //
        // First of all prepare the memory sources with the modification commands
        // receveid by the client
        //
        int method;
        boolean slow  = false;
        Target target = null;
        MemorySyncSource mss = null;
        AbstractCommand[] modifications = null;
        for (int i = sources.size(); i > 0; --i) {

            mss = (MemorySyncSource) sources.get(i - 1);

            String  uri = mss.getSourceURI();
            LogContext.setSourceURI(uri);

            if (log.isTraceEnabled()) {
                log.trace("Preparing "
                          + syncEngine.getClientSources().get(i - 1)
                          + " with "
                          + Arrays.asList(syncCommands)
                );
            }
            slow  = false;

            modifications = new AbstractCommand[0];
            ArrayList alModifications = new ArrayList();

            //
            // The client device could send more Sync commands for the same source
            //
            String targetURI = null;
            for (int j = 0; ((syncCommands != null) && (j < syncCommands.length)); ++j) {
                target = syncCommands[j].getTarget();

                targetURI = stripQueryString(target.getLocURI());

                if ((target != null) && (uri.equals(targetURI))) {
                    if (syncCommands[j].getCommands() != null) {
                        alModifications.addAll(syncCommands[j].getCommands());
                    }
                }
            }

            modifications = (AbstractCommand[])alModifications.toArray(new AbstractCommand[0]);

            method = ((Database)dbMap.get(uri)).getMethod();
            slow = ((method == AlertCode.SLOW) ||
                    (method == AlertCode.REFRESH_FROM_SERVER) ||
                    (method == AlertCode.REFRESH_FROM_CLIENT)
                   );

            if (!slow) {
                String targetRef = null;
                String statusCode = null;
                ArrayList targetRefs = null;
                for (int k=0; statusCommands!=null && k<statusCommands.length; k++) {
                    targetRefs = statusCommands[k].getTargetRef();
                    if (targetRefs == null || targetRefs.isEmpty()) {
                        continue;
                    }
                    targetRef = ((TargetRef)targetRefs.get(0)).getValue();
                    if ((targetRef != null) && (uri.equals(targetRef))) {
                        statusCode = statusCommands[k].getData().getData();
                        if (statusCode.equals(String.valueOf(StatusCode.REFRESH_REQUIRED))) {
                            ((Database)dbMap.get(uri)).setStatusCode(StatusCode.REFRESH_REQUIRED);
                        }
                        break;
                    }
                }
            }

            prepareMemorySource(mss, modifications, slow);
        }

        LogContext.setSourceURI(null);
    }

    /**
     * Prepares a source that represents the image of the client database. This
     * is done combining the existing client mapping with the modifications sent
     * by the client. Than this source can be compared with the server view of
     * the database.
     *
     * @param source the e source to prepare
     * @param commands the client modifications
     * @param slowSync true if the preparation is for a slow sync, false
     *                    otherwise
     * @throws Sync4jException
     *
     */
    private void prepareMemorySource(MemorySyncSource  source  ,
                                     AbstractCommand[] commands,
                                     boolean           slowSync) throws Sync4jException {

        List deleted  = new ArrayList();
        List created  = new ArrayList();
        List updated  = new ArrayList();

        //
        // First of all, in the case of fast sync, the items already mapped are
        // added as existing items (with GUID as key).
        // In the case of slow sync, the mappings are cleared so that they
        // won't generate conflicts.
        //
        ClientMapping guidluid = (ClientMapping)syncEngine.getMapping(
                       syncState.loggedPrincipal,
                       source.getSourceURI(),
                       slowSync
                   );

        for (int i = 0; ((commands != null) && (i < commands.length)); ++i) {
            if (commands[i] == null) {
                continue;
            }
            char syncItemState = SyncItemState.UNKNOWN;

            if (slowSync && !Delete.COMMAND_NAME.equals(commands[i].getName())) {
                syncItemState = SyncItemState.SYNCHRONIZED;
                List<SyncItem> items = Arrays.asList(
                        syncEngine.itemsToSyncItems(
                        source,
                        (ModificationCommand) commands[i],
                        syncItemState,
                        nextTimestamp.start));
                updated.addAll(items);
            } else if (Add.COMMAND_NAME.equals(commands[i].getName())) {
                syncItemState = SyncItemState.NEW;
                List<SyncItem> items = Arrays.asList(
                        syncEngine.itemsToSyncItems(
                        source,
                        (ModificationCommand) commands[i],
                        syncItemState,
                        nextTimestamp.start));
                created.addAll(items);
            } else if (Delete.COMMAND_NAME.equals(commands[i].getName())) {
                syncItemState = SyncItemState.DELETED;
                List<SyncItem> items = Arrays.asList(
                        syncEngine.itemsToSyncItems(
                        source,
                        (ModificationCommand) commands[i],
                        syncItemState,
                        nextTimestamp.start));
                deleted.addAll(items);
            } else if (Replace.COMMAND_NAME.equals(commands[i].getName())) {
                syncItemState = SyncItemState.UPDATED;
                List<SyncItem> items = Arrays.asList(
                        syncEngine.itemsToSyncItems(
                        source,
                        (ModificationCommand) commands[i],
                        syncItemState,
                        nextTimestamp.start));
                updated.addAll(items);
            }
        }
        try {
            source.addingItems(deleted, created, updated, getSyncItemFactory(source.getSourceURI()));
        } catch (SyncSourceException ex) {
            throw new Sync4jException("unable to get SyncItem Factory", ex);
        }

        if (syncEngine.isLastMessage()) {
            source.setExistingItems(createItemsFromMapping(source));
        }
    }

    /**
     * Create and return the status commands for the executed &lt;Sync&gt;s.
     *
     * @param syncCommands the Sync commands
     *
     * @return the status commands in a List collection
     */
    private List statusForSyncs(Sync[] syncCommands) {
        List      ret           = new ArrayList();
        String    uri           = null;
        Target    target        = null;
        Source    source        = null;
        TargetRef targetRef     = null;
        SourceRef sourceRef     = null;
        int       statusCode    = StatusCode.OK;
        String    statusMessage = null;
        Item[]    items         = null;

        for (int i = 0; (  (syncCommands     != null )
                        && (i < syncCommands.length)); ++i) {

            target = syncCommands[i].getTarget();
            source = syncCommands[i].getSource();

            //
            // A Sync command can be empty....
            //
            uri = (target==null) ? null : target.getLocURI();
            if ((uri == null) || (syncEngine.getClientSource(uri) != null)) {
                statusCode    = syncEngine.getClientSourceStatus(uri);
                statusMessage = syncEngine.getClientStatusMessage(uri);
            } else {
                statusCode = StatusCode.NOT_FOUND;
                statusMessage = null;
            }

            targetRef = (target == null) ? null : new TargetRef(uri);

            sourceRef = (source == null) ? null : new SourceRef(syncCommands[i].getSource());

            //
            // If there is a status message to report, add a new Item to the
            // Status command. Otherwise, no item is appended
            //
            if (statusMessage != null) {
                items = new Item[] {
                            new Item(null, null, null, new ComplexData(statusMessage), false)
                        };
            } else {
                items = new Item[0];
            }

            ret.add(
                new Status(
                        cmdIdGenerator.next(),
                        lastMsgIdFromClient,
                        syncCommands[i].getCmdID().getCmdID(),
                        syncCommands[i].COMMAND_NAME,
                        targetRef,
                        sourceRef,
                        null,
                        null,
                        new Data(statusCode),
                        items
                )
            );
        } // next i

        return ret;
    }

    /**
     * Checks that the credentials of the given message are allowed to start a
     * session.
     * @param credentials the credentials sent by client
     * @param deviceId the deviceId
     * @throws PersistentStoreException if an error occurs
     * @return true if the credetials and the device are authenticated, false
     *         otherwise
     */
    private boolean login(Cred credentials, String deviceId)
    throws PersistentStoreException {
        //
        // May be the credentials are already logged in...
        //
        logout();

        if (credentials == null) {
            syncState.authenticationState = AUTH_MISSING_CREDENTIALS;
            return false;
        }

        Authentication auth = credentials.getAuthentication();
        auth.setDeviceId(deviceId);
        auth.setSyncMLVerProto(syncState.syncMLVerProto);
        NextNonce nn = new NextNonce(syncState.device.getClientNonce());
        auth.setNextNonce(nn);

        Sync4jUser user = syncEngine.login(credentials);

        if (user == null) {
            if (log.isInfoEnabled()) {
                log.info("User not authenticated");
            }
            syncState.authenticationState = AUTH_INVALID_CREDENTIALS;
            return false;
        }

        Sync4jPrincipal principal = new Sync4jPrincipal(user, syncState.device);

        //
        // Added for backward compatibility
        //
        if ((Cred.AUTH_TYPE_BASIC).equalsIgnoreCase(credentials.getType())) {
            principal.setEncodedUserPwd(credentials.getData());
        }

        try {
            syncEngine.readPrincipal(principal);
        } catch (NotFoundException e) {
            if (log.isInfoEnabled()) {
                log.info("Authenticated principal not found:" + principal);
            }
            syncState.authenticationState = AUTH_INVALID_CREDENTIALS;
            syncState.loggedPrincipal     = null                    ;
            return false;
        }

        //
        // We have the logged principal only after the authentication process
        //

        switch (syncEngine.authorizeSession(principal, sessionId)) {
            case AUTHORIZED:
                syncState.loggedPrincipal     = principal         ;
                syncState.authenticationState = AUTH_AUTHENTICATED;
                return true;
            case INVALID_RESOURCE:
                syncState.authenticationState = AUTH_UNAUTHENTICATED;
                return false;
            case NOT_AUTHORIZED:
                syncState.authenticationState = AUTH_NOT_AUTHORIZED;
                return false;
            case PAYMENT_REQUIRED:
                syncState.authenticationState = AUTH_PAYMENT_REQUIRED;
                return false;
            case RESOURCE_NOT_AVAILABLE:
                syncState.authenticationState = AUTH_UNAUTHENTICATED;
                return false;
            default:
        }

        return false;
    }

    /**
     * Logs out the logged in credentials.
     */
    private void logout() {
        if (isAuthenticated()) {
            if (log.isTraceEnabled()) {
                log.trace("Logging out...");
            }
            syncEngine.logout(syncState.loggedPrincipal.getUser(),
                              syncState.clientCredential);
        }
        syncState.authenticationState = AUTH_INVALID_CREDENTIALS;
        syncState.loggedPrincipal  = null;
    }

    /**
     * Called by the <i>SyncBean</i> when the container release the session.
     * It commit the change to the DB, logs out the credentials and
     * release acquired resources.
     */
    public void endSession() {

        int currentState = getCurrentState();
        if (currentState != SessionHandler.STATE_ENDED) {

            try {
                syncEngine.endSync();
            } catch (Sync4jException s4je) {
                log.error("Error ending the sync", s4je);
                moveTo(SessionHandler.STATE_ERROR, "Error ending the sync");
            } finally {
                LogContext.setSourceURI(null);
            }

            switch (currentState) {
                case SessionHandler.STATE_END:
                case SessionHandler.STATE_SESSION_SUSPENDED:
                    commit();
                    break;

                case SessionHandler.STATE_ERROR:
                    abort(StatusCode.PROCESSING_ERROR);
                    break;

                default:
                    abort(StatusCode.SESSION_EXPIRED);
                    break;
            }

            //
            // Be carefully when you change this log because is used to notify
            // the end sync session event.
            // NOTE: the log was here because the endSession is called internally
            // when the last message is received and by the SyncAdapter,
            // so if you log the event outside of this block, the end sync
            // sync session event will be logged twice.
            //
            if (logPush.isTraceEnabled()) {
                int duration = -1;
                String message;
                boolean isSuccessEnding = true;
                switch (currentState) {
                    case SessionHandler.STATE_END:
                    case SessionHandler.STATE_SESSION_SUSPENDED:
                        message = "Close sync session [" + LogContext.getSessionId() + "].";
                        duration = (int)(nextTimestamp.end - nextTimestamp.start);
                        break;
                    case SessionHandler.STATE_ERROR:
                        isSuccessEnding = false;
                        message = "Session ended on error [" + LogContext.getSessionId() + "].";
                        break;
                    default:
                        message = "Session expired [" + LogContext.getSessionId() + "].";
                        break;
                }
                Event event = null;
                if(isSuccessEnding) {
                    event = SyncSessionEvent.createEndSessionEvent(
                            LogContext.getUserName(),
                            LogContext.getDeviceId(),
                            LogContext.getSessionId(),
                            message,
                            duration);
                } else {
                    event = SyncSessionEvent.createEndSessionEventOnError(
                            LogContext.getUserName(),
                            LogContext.getDeviceId(),
                            LogContext.getSessionId(),
                            message,
                            duration);
                }
                logPush.trace(event);
                        
            }
        }
        logout();
        moveTo(SessionHandler.STATE_ENDED);
    }

    private String getStateName(int state) {
        String stateName = "STATE_UNKNOWN";

        switch (state) {
            case STATE_START                      : stateName = "STATE_START"                     ; break;
            case STATE_END                        : stateName = "STATE_END"                       ; break;
            case STATE_ENDED                      : stateName = "STATE_ENDED"                     ; break;
            case STATE_PKG1_RECEIVING             : stateName = "STATE_PKG1_RECEIVING"            ; break;
            case STATE_PKG1_RECEIVED              : stateName = "STATE_PKG1_RECEIVED"             ; break;
            case STATE_PKG3_RECEIVING             : stateName = "STATE_PKG3_RECEIVING"            ; break;
            case STATE_PKG3_RECEIVED              : stateName = "STATE_PKG3_RECEIVED"             ; break;
            case STATE_ERROR                      : stateName = "STATE_ERROR"                     ; break;
            case STATE_SESSION_SUSPENDED          : stateName = "STATE_SESSION_SUSPENDED"         ; break;
            case STATE_SESSION_ABORTED            : stateName = "STATE_SESSION_ABORTED"           ; break;
            default                               : stateName = "STATE_UNKNOWN"                   ; break;
        }

        return stateName;
    }

    public SyncState getSyncState() {
        return this.syncState;
    }

    /**
     * Checks if in the given list of commands there is data to add to
     * previous data. The command with the data to add is the first in the list.
     * If there is a command, merges the previous data with the data contained in
     * the command.
     *
     * @param syncs the command list to check
     *
     */
    private void checkForReceivedLargeObject(Sync[] syncs) {
        if (log.isTraceEnabled()) {
            log.trace("Checking if there are data to add to previous data");
        }

        if (syncState.receivedLargeObject != null) {
            Item item =
                ProtocolUtil.getSyncItem(syncs, syncState.receivedLargeObject);

            if (item == null) {
                if (log.isInfoEnabled()) {
                    log.info("The end of data for a chunked objects has NOT been received!");
                }

                createAlert(AlertCode.NO_END_OF_DATA);

                syncState.receivedLargeObject       = null;
                syncState.sizeOfReceivedLargeObject = null;
                syncState.syncLocURI                = null;
                syncState.itemLocURI                = null;

                return;
            } else {
                if (!item.isMoreData()) {
                    //
                    // This is the last chunk
                    //
                    syncState.receivedLargeObject = null;
                }
            }
        }

        if (syncState.receivedLargeObject == null) {

            Item lo = null;

            int i = 0;
            for (i=0; (lo == null) && (i<syncs.length); ++i) {
                lo = ProtocolUtil.getLargeObject(syncs[i].getCommands());
            }

            if (lo == null) {
                //
                // no LO found
                //
                return;
            }

            String loURI = (lo.getSource() != null)
                         ? lo.getSource().getLocURI()
                         : lo.getTarget().getLocURI()
                         ;

            syncState.receivedLargeObject = syncs[i-1].getTarget().getLocURI()
                                          + '/'
                                          + loURI
                                          ;
            return;
        }
    }

    private void createAlert(int alertCode) {
        if (syncState.receivedLargeObject == null) {
            return;
        }

        int p = syncState.receivedLargeObject.lastIndexOf('/');

        if (p<0) {
            return;
        }

        String uri = syncState.receivedLargeObject.substring(p+1);

        Item item = new Item(null, new Target(uri), null, null, false);
        ArrayList items = new ArrayList(1);
        items.add(item);

        syncState.cmdCache3.add(
            new Alert(
                cmdIdGenerator.next(),
                false,
                null,
                alertCode,
                (Item[])(items.toArray(new Item[0]))
            )
        );
    }

    /**
     * Returns an array of AbstractCommand containing the commands to send in
     * the response message taking into account engine state, sent commands and
     * max message size.
     *
     * @param init a SyncInitialization instance
     * @param sync a SyncModifications instance
     * @param map  a ClientMapping instance
     *
     * @return an array of AbstractCommand containing the commands to send in
     *         the response message
     *
     * @throws ProtocolException in case of protocol errors
     */
    private AbstractCommand[] commandsToSend(SyncInitialization  init,
                                             SyncModifications   sync,
                                             SyncMapping         map ,
                                             SyncSuspend         suspend)
    throws ProtocolException {

        //
        // Step 1: cache all commands to be sent on the proper array list
        //
        if (init != null) {
            syncState.cmdCache1.addAll(init.getResponseCommands(msgIdGenerator.current()));
        }

        if (sync != null) {
            List commands = sync.getResponseCommands(msgIdGenerator.current());
            List status   = ProtocolUtil.filterCommands(commands,
                                                        new String[] { Status.COMMAND_NAME }
                            );
            syncState.cmdCache1.addAll(status);
            commands.removeAll(status);

            syncState.cmdCache3.addAll(commands);
            //
            // Note: this could be done more efficiently when inserting the
            // commands (instead of using addAll...)
            //
            ProtocolUtil.mergeSyncCommands(syncState.cmdCache3);
        }

        if (map != null) {
            List commands = map.getResponseCommands(msgIdGenerator.current());
            List status = ProtocolUtil.filterCommands(commands, new String[] { Status.COMMAND_NAME });
            syncState.cmdCache1.addAll(status);
            commands.removeAll(status);
            syncState.cmdCache3.addAll(commands);
        }

        if (suspend != null) {
            List commands = suspend.getResponseCommands(msgIdGenerator.current());
            List status = ProtocolUtil.filterCommands(commands, new String[] { Status.COMMAND_NAME });
            syncState.cmdCache1.addAll(status);
            commands.removeAll(status);
            syncState.cmdCache3.addAll(commands);
        }

        ProtocolUtil.removeHeaderStatus(syncState.cmdCache1);

        //
        // Step 2: first of all we have to send all PKG#1 responses, status
        //         first and then Alerts and Results
        //

        List cache = new ArrayList();
        cache.addAll(syncState.cmdCache1);
        //
        // Modification commands can be sent only after PKG1 has been completely
        // received
        //

        if (currentState == STATE_PKG3_RECEIVED) {
            cache.addAll(syncState.cmdCache3);
        }

        AbstractCommand[] allCmds = (AbstractCommand[])cache.toArray(new AbstractCommand[cache.size()]);

        ArrayList statusList  = ProtocolUtil.filterCommands(allCmds, Status.class);
        ArrayList commandList = ProtocolUtil.inverseFilterCommands(allCmds, Status.class);

        AbstractCommand[] statuses = ProtocolUtil.sortStatusCommand(statusList);
        AbstractCommand[] commands = (AbstractCommand[])commandList.toArray(new AbstractCommand[commandList.size()]);

        //
        // If we would have to send server capabilities, but they are too big,
        // we remove them from the commands to send and we set a proper state.
        //
        checkSizeCapabilities();

        //
        // Step 3: select the commands we can send without exceding maxMsgSize
        //
        ArrayList commandToSend = new ArrayList();
        for (int i=0; i<statuses.length; ++i) {
            if (!checkSize(statuses[i])) {
                return (AbstractCommand[])commandToSend.toArray(new AbstractCommand[0]);
            }

            messageSize += sizeCalculator.getCommandSize(statuses[i]);
            commandToSend.add(statuses[i]);
            syncState.cmdCache1.remove(statuses[i]);
        }

        for (int i=0; i<commands.length; ++i) {
            if (commands[i] instanceof Sync) {

                Sync newSync = new Sync(cmdIdGenerator.next(),
                                        commands[i].isNoResp(),
                                        commands[i].getCred(),
                                        ((Sync)commands[i]).getTarget(),
                                        ((Sync)commands[i]).getSource(),
                                        ((Sync)commands[i]).getMeta(),
                                        null, // numberOfChanges
                                        new AbstractCommand[0]);

                boolean allDataHandled = splitSyncCommand((Sync)commands[i], newSync);
                commandToSend.add(newSync);
                messageSize += sizeCalculator.getCommandSize(newSync);

                if (!allDataHandled) {
                    return (AbstractCommand[])commandToSend.toArray(new AbstractCommand[0]);
                } else {
                    //
                    // The Sync command will be sent in this message.
                    // We can remove it from the cache
                    //
                    syncState.cmdCache1.remove(commands[i]);
                    syncState.cmdCache3.remove(commands[i]);
                }

            } else {
                if (!checkSize(commands[i])) {
                    return (AbstractCommand[])commandToSend.toArray(new AbstractCommand[0]);
                } else {
                    //
                    // The command will be sent in this message.
                    // We can remove it from the cache
                    //
                    messageSize += sizeCalculator.getCommandSize(commands[i]);
                    commandToSend.add(commands[i]);
                    syncState.cmdCache1.remove(commands[i]);
                    syncState.cmdCache3.remove(commands[i]);
                }
            }
        }

        return (AbstractCommand[])commandToSend.toArray(new AbstractCommand[0]);
    }

    /**
     * Splits the originalCommand in the given splittedCommand.
     * @param originalCommand the original Sync command
     * @param splittedCommand the Sync command to fill with the data from
     *        the originalCommand
     * @return boolean true if all commands (and data) from the originalCommand
     *         is put in the splitted command.
     *         False is the originalCommand contains again some data to send
     */
    private boolean splitSyncCommand(Sync originalCommand, Sync splittedCommand) {

        ArrayList commands = originalCommand.getCommands();

        long     syncSize        = sizeCalculator.getCommandSize(splittedCommand);
        long     tmpMessageSize  = messageSize;

        int      howManyCommands = commands.size();

        String source = originalCommand.getSource().getLocURI();
        source = stripQueryString(source);
        boolean lo = isLargeObjectSupported(source);

        List newCommands        = new ArrayList();
        List commandsToRemove   = new ArrayList();
        List itemsInError       = new ArrayList();

        Iterator i               = commands.iterator();
        while (i.hasNext()) {
            boolean commandToRemove = false;
            AbstractCommand abstractCommand = (AbstractCommand)i.next();

            //
            // Before checking the command size, we have to check if it is an
            // ItemizedCommand and if its items has the data or if its data must
            // be loaded (maybe the data has been loaded previously but then the
            // item has not been sent because too large)
            //
            if (abstractCommand instanceof ItemizedCommand) {
                List items = ((ItemizedCommand)abstractCommand).getItems();
                itemsInError.clear();
                if (items != null) {
                    int numItems = items.size();
                    for (int y=0; y<numItems; y++) {
                        Item item = (Item)items.get(y);
                        if (item.isWithIncompleteInfo()) {

                            try {
                                LogContext.setSourceURI(source);

                                syncEngine.completeItemInfo(contentType, source, item);

                                //
                                // We send the meta in the command so we have to
                                // set it in the command and then remove
                                // it from the item.
                                //
                                abstractCommand.setMeta(item.getMeta());
                                item.setMeta(null);

                            } catch (Sync4jException ex) {
                                log.error("Error filling item with its data. " +
                                          "The item will be removed " +
                                          "from the list of the items to send", ex);
                                itemsInError.add(item);
                            } finally {
                                LogContext.setSourceURI(null);
                            }
                        }
                    }
                    //
                    // Removing items in error
                    //
                    items.removeAll(itemsInError);
                    if (items.isEmpty()) {
                        //
                        // We have to remove the command because it has not items
                        //
                        commandsToRemove.add(abstractCommand);
                        continue;
                    }

                    //
                    // Remove all items that will never be sent; these are:
                    //  - Items bigger then MaxObjSize (if specified)
                    //  - Items bigger then MaxMsgSize-MSG_OVERHEAD if the client does not
                    //    support large object
                    //
                    long maxObjSize = syncState.maxObjSizes.get(source);
                    if (lo && maxObjSize > 0) {
                        if (log.isTraceEnabled()) {
                            log.trace("Checking items bigger than maxObjSize ("
                                + maxObjSize +
                                ") because the client supports large object");
                        }
                        removeTooBigItems(items, maxObjSize, source);
                        if (items.isEmpty()) {
                            //
                            // We have to remove the command because it has not items
                            //
                            commandToRemove = true;
                        }
                    } else if (!lo) {
                        if (log.isTraceEnabled()) {
                            log.trace("Checking items bigger than " +
                                       (syncState.maxMsgSize - sizeCalculator.getMsgSizeOverhead()) +
                                       " [maxMsgSize (" + syncState.maxMsgSize + ")"
                                       + " - fixed message overhead (" +
                                       sizeCalculator.getMsgSizeOverhead() + ")]");
                        }

                        removeTooBigItems(items, syncState.maxMsgSize - sizeCalculator.getMsgSizeOverhead(), source);
                        if (items.isEmpty()) {
                            //
                            // We have to remove the command because it has not items
                            //
                            commandToRemove = true;
                        }
                    }
                }
            }

            if (commandToRemove) {
                commandsToRemove.add(abstractCommand);
                continue;
            }

            long commandSize = getCommandSize(abstractCommand, source);

            if (log.isTraceEnabled()) {
                log.trace("Checking if the current command is too big for this message" +
                           " using syncSize: " + syncSize + ", command size: " +
                           commandSize + ", current message size: " + tmpMessageSize
                        );
            }
            if (!checkSize(syncSize + commandSize, tmpMessageSize )) {
                if (log.isTraceEnabled()) {
                    log.trace("The command is too big. If the large objects are " +
                               "supported, this command will be splitted");
                }

                break;
            } else {
                if (log.isTraceEnabled()) {
                   log.trace("The command is not too big for this message. " +
                              "Adding it to the commands list to send");
                }

                newCommands.add(abstractCommand);
                tmpMessageSize += commandSize;
            }
        }
        commands.removeAll(commandsToRemove);
        commands.removeAll(newCommands);

        //
        // If no commands were sent, let's send them as large objects (if
        // supported by the client).
        //
        if (lo && (howManyCommands > 0) && (howManyCommands == commands.size())) {
            ItemizedCommand c      = (ItemizedCommand)commands.get(0);
            ItemizedCommand chunk = nextLOChunk(originalCommand.getSource().getLocURI(), c, tmpMessageSize);
            //
            // If the returned chunk contains some data we have to add it,
            // otherwise we can ignore it for this message
            //
            if (chunk != null &&
                ((Item)chunk.getItems().get(0)).getData().getData().length() != 0) {

                newCommands.add(chunk);
                //
                // if no item has been left in the original command then the command
                // is removed
                //
                if (((Item) c.getItems().get(0)).getData().getSize() == 0) {
                    commands.remove(c);
                }
            } else {
                syncState.sendingLOURI = null;
            }
        }

        //
        // If the Sync doesn't contain command, we suppose that all data will
        // be sent in this message
        //
        boolean allCommandsSent = (commands.isEmpty());

        //
        // Checking the numberOfChanges and the howManyCommands.
        // If the numberOfChanges is equals to howManyCommands means that this
        // is the first time that the server tries to send the commands in the
        // orginalCommand (originalCommand is a Sync command) so the
        // numberOfChanges is set in the splittedCommand (this contains the
        // commands that the server will send in the current message).
        // In order to ensure that the numberOfChanges is sent just once per
        // source, the numberOfChanges of the originalCommand is set to null.
        //
        if (originalCommand.getNumberOfChanges() != null &&
            originalCommand.getNumberOfChanges().intValue() == howManyCommands) {

            splittedCommand.setNumberOfChanges(new Long(howManyCommands));

            originalCommand.setNumberOfChanges(null);
        }

        splittedCommand.setCommands((AbstractCommand[])newCommands.toArray(new AbstractCommand[0]));

        return allCommandsSent;

    }

    /**
     * Checks if the current mesasge is the last of the package. A message is
     * the last of the package if its corresponding command cache is empty and
     * the state is a "RECEIVED" state.
     *
     * @return true if the message is the last of the package, false otherwise
     */
    private boolean isLastMessage() {
        return ((currentState == STATE_PKG1_RECEIVED) && (syncState.cmdCache1.size() == 0))
            || ((currentState == STATE_PKG3_RECEIVED) && (syncState.cmdCache3.size() == 0))
            ;
    }

    /**
     * Removes the items bigger than the given threshold from the given list.
     *
     * @param commands the command list
     * @param size the threshold size
     */
    private void removeTooBigItems(List items, long size, String sourceURI) {
        List remove = new ArrayList();

        Iterator i = items.iterator();
        while(i.hasNext()) {
            Item item = (Item) i.next();
            try {
                // set locUri
                String locUri = null;
                if (item.getSource() != null) {
                    locUri = item.getSource().getLocURI();
                } else if (item.getTarget() != null) {
                    locUri = item.getTarget().getLocURI();
                }
                // get itemLength
                long itemLength = getLength(item, sourceURI);

                if ((item.getData() != null) && (itemLength > size)) {
                    if (log.isTraceEnabled()) {
                        log.trace("Item '" + locUri + "' is bigger (" + itemLength + ") than the " + "maximum allowed size (" + size + "). It is removed from the list of items to send.");
                    }
                    remove.add(item);
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Item '" + locUri + "' is smaller (" + itemLength + ") than the " + "maximum allowed size (" + size + "). It is not removed from the list of items to send.");
                    }
                }
            } catch (Sync4jException ex) {
                log.error("Unable to get the item size: removing it", ex);
                remove.add(item);
            }

        }
        items.removeAll(remove);
    }

    /**
     * Splits the data of the given command. The new chunk of data will be
     * returned in newly created command; data sent are removed from cmd. The
     * first time, a Meta with the object size is stored in cmd so that we can
     * detect when this is the first chunk or not.
     *
     * @param sourceURI the source URI
     * @param cmd the command containing the object to split
     * @param msgSize the size of the message that will include the chunk
     *
     * @return boolean true if the data is splitted, false otherwise
     */
    private ItemizedCommand nextLOChunk(String sourceURI, ItemizedCommand cmd, long msgSize) {
        if (!(cmd instanceof Add) && !(cmd instanceof Replace)) {
            //
            // This should never happen!!!
            //
            return null;
        }

        //
        // create chunk as a copy of cmd but with an empty item
        //
        ItemizedCommand chunk = null;

        Item item = (Item)cmd.getItems().get(0);

        ComplexData d = item.getData();
        int dataLength = d.getSize();
        
        Item chunkItem = new Item(
                                  item.getSource()   ,
                                  item.getTarget()   ,
                                  item.getSourceParent(),
                                  item.getTargetParent(),
                                  null               ,
                                  new ComplexData(""),
                                  false              );

        String chunkURI = null;
        if (cmd instanceof Add) {
            chunk = new Add(cmd.getCmdID()          ,
                            cmd.isNoResp()          ,
                            cmd.getCred()           ,
                            cmd.getMeta()           ,
                            new Item[] { chunkItem });
            chunkURI = chunkItem.getSource().getLocURI();
        } else if (cmd instanceof Replace) {
            chunk = new Replace(cmd.getCmdID()          ,
                                cmd.isNoResp()          ,
                                cmd.getCred()           ,
                                cmd.getMeta()           ,
                                new Item[] { chunkItem });
            chunkURI = chunkItem.getTarget().getLocURI();
        }

        String loURI = sourceURI //sync.getSource().getLocURI()
                     + '/'
                     + chunkURI
                     ;

        Meta m = cmd.getMeta();
        if (m == null) {
            m =  new Meta();
        }

        Meta chunkMeta = new Meta();
        chunkMeta.setFormat(m.getFormat());
        chunkMeta.setType(m.getType());

        if (!loURI.equals(syncState.sendingLOURI)) {
            chunkMeta.setSize(new Long(dataLength));
            syncState.sendingLOURI = loURI;
        }
        chunk.setMeta(chunkMeta);

        //
        // calculate the chunk length
        //
        int chunkLength =
            (int)(syncState.maxMsgSize - (msgSize + sizeCalculator.getMsgSizeOverhead()));

        //
        // If chunk is in Base64 format, it would be preferable to send each
        // chunk by groups of 4 bytes: this because Base64 works with multiple
        // of 4 bytes.
        //
        if (chunk.getMeta() != null && chunk.getMeta().getFormat() != null) {
            if (Constants.FORMAT_B64.equals(chunk.getMeta().getFormat())) {
                chunkLength = (chunkLength/4)*4;
            }
        }

        byte[] binaryData = d.getBinaryData();

        if (dataLength < chunkLength) {
            if (binaryData != null) {
                chunkItem.getData().setBinaryData(binaryData);
            } else {
                chunkItem.getData().setData(d.getData());
            }
            
            chunkItem.setMoreData(Boolean.FALSE);
            //
            // No data will be left in the original item
            //
            item.getData().setData("");
        } else {
            if (chunkLength > 0) {

                if (binaryData != null) {
                    byte[] chunkData = new byte[chunkLength];
                    System.arraycopy(binaryData, 0, chunkData, 0, chunkLength);

                    CData cdata = new CData();
                    cdata.setData(new String(chunkData));
                    chunkItem.setData(cdata);
                    
                    byte[] remaining = new byte[dataLength - chunkLength];
                    System.arraycopy(binaryData, chunkLength, remaining, 0, remaining.length);
                    item.getData().setBinaryData(remaining);
                } else {
                    String dataValue = d.getData();
                    chunkItem.getData().setData(dataValue.substring(0, chunkLength));
                    item.getData().setData(dataValue.substring(chunkLength));
                }
                chunkItem.setMoreData(Boolean.TRUE);
            }

        }

        return chunk;
    }

    /**
     * Does the client support large object? The returned value is the one
     * in the device object if not differently specified by the client in the
     * device capabilities. If so, the information in the DevInf object
     * overwrites the one stored into the database.
     *
     * @param sourceName the source URI used like key for searching the value of
     *                   MaxObjSize
     * @return true if the client supports large object, false otherwise
     */
    private boolean isLargeObjectSupported(String sourceName) {
        long maxObjSize = syncState.maxObjSizes.get(sourceName);
        boolean isLOSupported =
            (maxObjSize > 0) ||
            ((syncState.devInf != null) && syncState.devInf.isSupportLargeObjs());

        if (!isLOSupported) {
            Capabilities caps = null;
            DevInf devInf = null;

            caps = syncState.device.getCapabilities();

            if (caps != null) {
                devInf = caps.getDevInf();
            }

            if (devInf != null) {
                return devInf.isSupportLargeObjs();
            }
        } else {
            return true;
        }
        return false;

    }

    /**
     * Does the client support large object? The returned value is the one
     * in the device object if not differently specified by the client in the
     * device capabilities. If so, the information in the DevInf object
     * overwrites the one stored into the database.
     */
    private boolean isNumberOfChangesSupported() {
        if (syncState.devInf != null) {
            return syncState.devInf.isSupportNumberOfChanges();
        }
        Capabilities caps   = null;
        DevInf       devInf = null;

        caps = syncState.device.getCapabilities();

        if (caps != null) {
            devInf = caps.getDevInf();
        }

        if (devInf != null) {
            return devInf.isSupportNumberOfChanges();
        }
        return false;
    }

    /**
     * Recreates the device sync sources from the LUID-GUID mapping.
     *
     * @param source the source the items belong to
     *
     * @return a List representing the items on the device database
     */
    private List createItemsFromMapping(SyncSource source) {
        ClientMapping m = syncEngine.getMapping(source.getSourceURI());

        List items = new ArrayList();

        if (m == null) {

            log.error("Client mapping not found for "
                      + source.getSourceURI()
                      + '!'
                      );

            return items;
        }

        java.util.List mapping = m.getMapping();

        Iterator           it         = mapping.iterator();
        ClientMappingEntry entry      = null;

        while (it.hasNext()) {
            entry      = (ClientMappingEntry)it.next();
            if (entry.isValid()) {
                //
                // if the lastAnchor is null or empty means the mapping isn't
                // valid (not a valid status has been received)
                //
                items.add(
                    new InMemorySyncItem(source,
                                         entry.getGuid(),   // key
                                         null,              // parentKey
                                         entry.getLuid(),   // mappedKey
                                         SyncItemState.SYNCHRONIZED)
                    );
            }
        }

        return items;
    }

    /**
     * Stores for each commands in the given list, a new entry in the
     * commandsDescriptors map with:
     * key: msgId|cmdId
     * value: commandDescriptor

     * @param msgId String
     * @param commands List
     */
    private void storeCommandDescriptorForSentCommands(String msgId,
                                                       List commands) {
        if (commandsDescriptors == null) {
            commandsDescriptors = new java.util.HashMap();
        }
        if (commands == null) {
            return;
        }

        Iterator        itCommands = commands.iterator();
        AbstractCommand command    = null;
        String          sourceUri  = null;

        while (itCommands.hasNext()) {

            command = (AbstractCommand)(itCommands.next());
            if (command instanceof Sync) {

                sourceUri = ((Sync)command).getSource().getLocURI();
                sourceUri = stripQueryString(sourceUri);

                storeCommandDescriptorForSentCommands(msgId,
                                                      ((Sync)command).getCommands(),
                                                      sourceUri);
                continue;
            }
        }
    }

    /**
     * Stores for each commands in the given list, a new entry in the
     * commandsDescriptors map with:
     * key: msgId|cmdId
     * value: commandDescriptor
     *
     * @param msgId String
     * @param commands List
     * @param sourceUri String
     */
    private void storeCommandDescriptorForSentCommands(String msgId,
                                                       List commands,
                                                       String sourceUri) {
        if (commandsDescriptors == null) {
            commandsDescriptors = new java.util.HashMap();
        }
        if (commands == null) {
            return;
        }

        Iterator        itCommands = commands.iterator();
        String          cmdId      = null;
        AbstractCommand command    = null;

        CommandDescriptor commandDescriptor = null;

        while (itCommands.hasNext()) {

            command = (AbstractCommand)(itCommands.next());
            if (command instanceof Atomic) {
                storeCommandDescriptorForSentCommands(msgId,
                                                     ((Atomic)command).getCommands(),
                                                     sourceUri);
                continue;
            }
            if (command instanceof Sequence) {
                storeCommandDescriptorForSentCommands(msgId,
                                                     ((Sequence)command).getCommands(),
                                                     sourceUri);
                continue;
            }

            StringBuffer key = new StringBuffer(msgId);
            cmdId = command.getCmdID().getCmdID();
            key.append('|').append(cmdId);
            commandDescriptor = createCommandDescriptor(msgId, sourceUri, command);

            commandsDescriptors.put(key.toString(), commandDescriptor);
        }
    }

    /**
     * Creates a CommandDescriptor with the given data.
     * If the command isn't an Add or a Replace or a Delete or a Put or a
     * Results, returns null
     *
     * @param msgId String
     * @param sourceUri String
     * @param command AbstractCommand
     * @return CommandDescriptor
     */
    private CommandDescriptor createCommandDescriptor(String msgId,
                                                      String sourceUri,
                                                      AbstractCommand command) {

        CommandDescriptor commandDescriptor = null;

        if (command instanceof Add ||
            command instanceof Replace ||
            command instanceof Delete) {
            commandDescriptor = new CommandDescriptor(msgId,
                                                      command.getCmdID().getCmdID(),
                                                      command.getName(),
                                                      sourceUri,
                                                      null,
                                                      null
                                );

            List     items   = ((ItemizedCommand)command).getItems();
            Iterator itItems = items.iterator();
            Item     item    = null;
            String   luid    = null;
            String   guid    = null;
            while (itItems.hasNext()) {
                item = (Item)itItems.next();
                if (item.getSource() != null) {
                    guid = item.getSource().getLocURI();
                }
                if (item.getTarget() != null) {
                    luid = item.getTarget().getLocURI();
                }
                commandDescriptor.addGuid(guid);
                commandDescriptor.addLuid(luid);
            }

        }
        return commandDescriptor;
    }

    /**
     * Get the sourceUri of the command identified with the given msgId and cmdId
     * @param msgId String
     * @param cmdId String
     * @return String
     */
    private CommandDescriptor getCommandDescriptor(String msgId, String cmdId) {
        if (commandsDescriptors == null) {
            return null;
        }
        StringBuffer key = new StringBuffer(msgId);
        key.append('|').append(cmdId);
        CommandDescriptor commandDescriptor =
            (CommandDescriptor)commandsDescriptors.get(key.toString());

        return commandDescriptor;
    }

    /**
     * Checks the given statuses. For each status, if there is a valid 
     * CommandDescriptor, the status is notified to the engine.
     * @param statuses the list of status to handle
     */
    private void handleReceivedStatuses(List statuses) {

        if (statuses == null || statuses.isEmpty()) {
            return ;
        }

        int    size   = statuses.size();
        Status status = null;
        String msgRef = null;
        String cmdRef = null;

        CommandDescriptor commandDescriptor  = null;

        for (int i=0; i<size; i++) {

            status = (Status)statuses.get(i);

            msgRef = status.getMsgRef();
            cmdRef = status.getCmdRef();

            commandDescriptor = getCommandDescriptor(msgRef, cmdRef);
            if (commandDescriptor != null) {
                if (checkCommandDescriptorAndStatus(commandDescriptor, status)) {
                    String sourceUri = commandDescriptor.getSourceUri();

                    sourceUri = stripQueryString(sourceUri);

                    LogContext.setSourceURI(sourceUri);
                    try {
                        syncEngine.handleReceivedStatus(status, commandDescriptor.getSourceUri());
                    } finally {
                        LogContext.setSourceURI(null);
                    }
                }
            }
        }
    }

    /**
     * Checks if the given status corresponds to the given CommandDescriptor.
     * Only status for Add, Replace and Delete commands are checked, for the
     * others command, false is returned.
     * For Add, Replace and Delete, the targetRef (Replace, Delete) and the
     * sourceRef (Add) are checked.
     * Moreover if the Status contains more items, checks if all items are in
     * the lists of the commandDescriptor.
     * @param commandDescriptor
     * @param status Status
     * @return boolean
     */
    private boolean checkCommandDescriptorAndStatus(CommandDescriptor commandDescriptor,
                                                    Status status) {

        if (commandDescriptor == null) {
            return false;
        }
        String commandInDescriptor = commandDescriptor.getCommandName();
        String commandInStatus     = status.getCmd();

        if (commandInDescriptor == null ||
            commandInStatus     == null) {
            return false;
        }

        if (!Add.COMMAND_NAME.equals(commandInStatus) &&
            !Replace.COMMAND_NAME.equals(commandInStatus) &&
            !Delete.COMMAND_NAME.equals(commandInStatus)) {

            return false;
        }

        if (!commandInDescriptor.equals(commandInStatus)) {
            return false;
        }

        List   items = status.getItems();
        String luid  = null;
        String guid  = null;

        if (items == null || items.isEmpty()) {
            //
            // We check the sourceRef and TargetRef
            //
            if (status.getSourceRef() != null &&
                status.getSourceRef().size() > 0) {
                guid = ((SourceRef)status.getSourceRef().get(0)).getValue();
            }

            if (status.getTargetRef() != null &&
                status.getTargetRef().size() > 0) {
                luid = ((TargetRef)status.getTargetRef().get(0)).getValue();
            }

            if (!commandDescriptor.containsGuid(guid) &&
                !commandDescriptor.containsLuid(luid)) {
                return false;
            } else {
                return true;
            }
        }

        if (items == null &&
            commandDescriptor.getGuids() == null &&
            commandDescriptor.getLuids() == null  ) {
            //
            // The status and the commandDescriptor don't have any items.
            // BTW, this is a strange situation
            //
            return true;
        }

        //
        // IMPORTANT NOTE: as specified by the protocol (see OMA-SyncML-RepPro-V1_2-20040601-C.pdf,
        // pg. 28), a Status should contain a SourceRef of a TargetRef and an Item
        // just if additional information is required. BTW, our plug-ins send the
        // status without SourceRef or TargetRef and with an Item specifing the
        // Source. In detail, V3 and V6 plug-ins send Status for Adds, Replaces, Deletes
        // specifing always an item with a Source. Supposing this is wrong because the
        // specs don't specify this case, the server is able to handle Status with
        // Item with Source and Target.
        // The following code is very ugly because it handles Source/Target indistinctly
        // as LUID and GUID but in this way the server is more flexible. In the next
        // releases, when the client will fix their Status and the server must not
        // guarantee the backward compatibility, we can remove this code.
        //
        // See also Sync4jEngine.getGuidsLuids(....)
        //
        Iterator itItems = items.iterator();
        Item     item    = null;

        while (itItems.hasNext()) {
            item = (Item)itItems.next();
            if (item.getTarget() != null) {
                luid = item.getTarget().getLocURI();
            }
            if (item.getSource() != null) {
                guid = item.getSource().getLocURI();
            }
            //
            // That is very very ugly because in the second check we use the guid (source)
            // as luid (checking is the commandDescriptor contains a luid with the guid value)
            // and the luid (target) as guid (checking is the commandDescriptor contains a
            // guid with the luid value)
            //
            if ( (!commandDescriptor.containsGuid(guid) || !commandDescriptor.containsLuid(luid)) &&
                 (!commandDescriptor.containsLuid(guid) || !commandDescriptor.containsGuid(luid))  ) {

                //
                // We have to remove this item from the items list in the status
                //
                itItems.remove(); // the item is removed from the original
                                  // items list taken from the status.
                return false;
            }
        }
        return true;
    }

    /**
     * Processes the client capabilities setting the device object accordingly.
     *
     * @param devInfo NULL - if null, no client caps were given
     * @param idCaps capabilities identifier
     */
    private void processClientCapabilities(DevInf devInf, Long idCaps)
    throws DeviceInventoryException {

        if (devInf != null) {
            syncState.devInf = devInf;
        }

        if (idCaps != null && (-1) != idCaps.intValue()) {
            if (devInf != null) {
                //
                // The device capabilities are already in the db.
                // The device sent new capabilities.
                // The db will be updated with the new caps.
                //
                Capabilities caps = new Capabilities();
                caps.setDevInf(devInf);
                caps.setId(idCaps);
                syncState.device.setCapabilities(caps);
                syncEngine.getDeviceInventory().setCapabilities(caps);
                syncEngine.storeDevice(syncState.device);
            }
        } else {
            if (devInf != null) {
                //
                // There are no device caps in the db.
                // The device sent new capabilities.
                // The new caps will be stored into db.
                //
                Capabilities caps = new Capabilities();
                caps.setDevInf(devInf);
                syncEngine.getDeviceInventory().setCapabilities(caps);
                syncState.device.setCapabilities(caps);
                syncEngine.storeDevice(syncState.device);
            } else {
                //
                // There are no device caps in the db.
                // The server should have already required the capabilities to
                // the client, but the device didn't send them.
                // The server should continue to require the capabilities to
                // the client without set id_caps to -1 in the db.
                //
            }
        }
    }

    /**
     * Called when a synchronization is suspended.
     * Clears the caches and notify the suspend to the engine
     */
    private void suspendSynchronization() {
        syncState.cmdCache1.clear();
        syncState.cmdCache3.clear();
        syncEngine.suspendSynchronization();
    }

    /**
     * NOTE: this method must be moved in the funambol:core-framework
     * Strips the query string from the given uri
     * @param uri the uri
     * @return the uri without the query string
     */
    private String stripQueryString(String uri) {
        int qMark = uri.indexOf('?');
        if (qMark != -1) {
            uri = uri.substring(0, qMark);
        }
        return uri;
    }


    /**
     * Verify if the DataStore capabilities is missing for a database
     * @param deviceCaps The device capabilities to look into
     * @param dbs the databases to be verified
     * @return true if all databases have a corresponding DataStore defined or
     * if the client does not support capabilities, false the client supports
     * capabilities but there is a database for which there are no dataStore
     * capabilities.
     */
    private boolean containsAllDataStores(Capabilities deviceCaps, Database[] dbs) {

        if (deviceCaps == null) {
            return false;
        }

        if (deviceCaps.getId() == null) {
            return false;
        }
        boolean deviceDoesNotSupportCapabilities =
                deviceCaps.getId() != null && -1 == deviceCaps.getId().intValue();
        if (deviceDoesNotSupportCapabilities) {
            return true;
        }

        for (int i = 0; i < dbs.length; i++) {
            if (!deviceCaps.containsDataStore(dbs[i].getTarget().getLocURI())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retrieve the SyncItemFactory belonging to the server sync source
     * @param sourceURI The source URI
     * @return the SyncItemFactory belonging to the server sync source
     */
    private SyncItemFactory getSyncItemFactory(String  sourceURI) throws SyncSourceException {
       return (syncEngine instanceof Sync4jEngine ? ((Sync4jEngine)syncEngine).getSyncItemFactory(sourceURI) : null);
    }

    /**
     * Returns the item length
     * @param item the item to measure
     * @param sourceURI The source URI
     * @return
     * @throws com.funambol.framework.core.Sync4jException
     */
    private long getLength(Item item, String sourceURI) throws Sync4jException {

        Data d = item.getData();
        if (d != null) {
            return d.getSize();
        }
        return 0;
    }

    /**
     * Returns the command size
     * @param abstractCommand
     * @param sourceURI the source URI
     * @return the command size
     */
    private long getCommandSize(AbstractCommand abstractCommand, String sourceURI) {

        long commandSize = sizeCalculator.getCommandSize(abstractCommand);

        return commandSize;
    }

    private String getMoveToMessage(Integer state, String message) {
        StringBuilder sb = new StringBuilder();
        sb.append("moving to state: ")
          .append(getStateName(state));
        
        if (message != null && !"".equals(message)) {
            sb.append(" [").append(message).append(']');
        }
        return sb.toString();
    }

    /**
     * This method allows to translate from the actual authenticationState to
     * the sync status code. If no mapping is found, the forbidden status
     * code (403) is returned as default.
     * @param authenticationState it's the authenticationState we want to translate
     * to a synchronization status
     * @return the synchronization status code bound to the given authenticationState
     */
    private int authenticationState2StatusCode(int authenticationState) {
        int statusCode = StatusCode.FORBIDDEN;

        if (authenticationState == AUTH_MISSING_CREDENTIALS) {
            statusCode = StatusCode.MISSING_CREDENTIALS;
        } else if (authenticationState == AUTH_PAYMENT_REQUIRED) {
            statusCode = StatusCode.PAYMENT_REQUIRED;
        } else if (authenticationState == AUTH_NOT_AUTHORIZED) {
            statusCode = StatusCode.FORBIDDEN;
        } else if (authenticationState == AUTH_INVALID_CREDENTIALS) {
            statusCode = StatusCode.INVALID_CREDENTIALS;
        } else if (isAccountExpired()) {
            statusCode = StatusCode.PAYMENT_REQUIRED;
        }
        return statusCode;
    }

    /**
     * This method fires a StartSessionEvent upon error
     */
    private void fireStartSyncSessionEventOnError() {
       if (log.isInfoEnabled() || logPush.isTraceEnabled()) {
            String msg = new StringBuilder("Authentication failed for device ")
                         .append(clientDeviceId)
                         .append(". Make sure that the client used correct ")
                         .append("username and password and that there ")
                         .append("is a principal associating the user ")
                         .append(" to the device. ").toString();

            if (log.isInfoEnabled()) {
                log.info(msg);
            }
            //
            // Be carefully when you change this log because
            // is used to notify the start sync session event
            // when an error occurs.
            //
            int statusCode =
                    authenticationState2StatusCode(syncState.authenticationState);

            if (logPush.isTraceEnabled()) {
                logPush.trace(
                        SyncSessionEvent.createStartSessionEventOnError(
                            LogContext.getUserName(),
                            LogContext.getDeviceId(),
                            LogContext.getSessionId(),
                            msg,
                            Integer.toString(statusCode))
                );
            }
        }
    }

    /**
     * This method fires a StartSessionEvent when the session is correctly
     * started.
     */
    private void fireStartSyncSessionEvent() {
        //
        // Be carefully when you change this log because is
        // used to notify the start sync session event.
        //
        if (logPush.isTraceEnabled()) {
            logPush.trace(
                    SyncSessionEvent.createStartSessionEvent(
                    LogContext.getUserName(),
                    LogContext.getDeviceId(),
                    LogContext.getSessionId(),
                    "Start sync session [" + LogContext.getSessionId() + "]."));
        }
    }

}
