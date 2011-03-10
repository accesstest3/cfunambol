/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IUnmarshallingContext;

import com.funambol.framework.core.Add;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.ComplexData;
import com.funambol.framework.core.Cred;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.core.DevInf;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.ItemizedCommand;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Source;
import com.funambol.framework.core.SourceRef;
import com.funambol.framework.core.StatusCode;
import com.funambol.framework.core.SyncML;
import com.funambol.framework.core.Target;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.pipeline.MessageProcessingContext;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.security.SecurityConstants;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.Event;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.SyncSessionEvent;
import com.funambol.framework.tools.XMLSizeCalculator;

import com.funambol.server.config.Configuration;
import com.funambol.server.engine.Sync4jEngine;

/**
 * This class was created to test the logging of the start/end sync session.
 * That is we're checking that the event, fired by the software component
 * (SyncSessionHandler), correctly arrives to the appender.
 *
 * @version $Id: SyncSessionHandlerTest.java 34831 2010-06-26 20:35:05Z nichele $
 */
public class SyncSessionHandlerTest extends TestCase {

    // ------------------------------------------------------------ Private data
    private final static Logger LOG = Logger.getLogger("funambol.push");
    private final static String DATA_HOME = "src/test/data/com/funambol/server/session";
    private static final String START_SYNC_SYNCML_MESSAGE_FILE =
        "start-sync-session-event.xml";
    private static Sync4jEngine mockEngine = null;

    // TEST DATA
    private final static String DEVICE_ID = "IMEI:23432432";
    private final static String START_SYNC_SESSION_MESSAGE = "Start sync session [34234132].";
    private final static String END_SYNC_SESSION_MESSAGE = "Session expired [34234132].";
    private final static String START_SYNC_SESSION_ERROR_MESSAGE = "Authentication failed for device IMEI:23432432. Make sure that the client used correct username and password and that there is a principal associating the user  to the device. ";
    private static final String USER_NAME = "guest";
    private static final String SESSION_ID = "34234132";
    private static final Sync4jUser FAKE_USER = new Sync4jUser();

    static {
        FAKE_USER.setUsername(USER_NAME);
    }
    SyncSessionHandler handler;
    SyncML message;
    MessageProcessingContext context;
    private TestAppender appender;

    // ------------------------------------------------------------ Constructors
    public SyncSessionHandlerTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        // loading default syncml message
        message = loadSyncmlMessage(START_SYNC_SYNCML_MESSAGE_FILE);

        // creating fake context
        context = setupContext();

        // creating SyncSessionHandler
        handler = setupHandler();

        SyncState syncState = new SyncState();
        syncState.device = new Sync4jDevice(DEVICE_ID);
        injectSyncState(handler, syncState);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // releasing testing resources
        message = null;
        context = null;
        handler = null;

        // cleaning log resources
        if (appender != null) {
            LOG.removeAppender(appender);
            appender = null;
        }
        LogContext.setSessionId(null);
        LogContext.setUserName(null);

        // Restoring authentication policy for the mock
        restoreMockAuthenticationPolicy();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * This method tests the logging of the start sync session event,
     * the log is set to level trace.
     */
    public void testFireStartSyncSessionEventTrace() throws Exception {
        // setting up the log framework, the logger level is set to trace
        appender = setupLogFramework(Level.TRACE);
        
        // process the message in order to trigger the desired event
        handler.processMessage(message, context);

        Event loggedEvent = appender.getLastEvent();
        Event expectedEvent =
            SyncSessionEvent.createStartSessionEvent(USER_NAME, DEVICE_ID, SESSION_ID, START_SYNC_SESSION_MESSAGE);

        assertTrue(EventHelper.compareEvent(loggedEvent, expectedEvent));

    }

    /**
     * This method tests the logging of the start sync session event,
     * the log is set to level info.
     * In this case, the appender must contains no events.
     *
     */
    public void testFireStartSyncSessionEventInfo() throws Exception {
        // setting up the log framework, the logger level is set to info
        appender = setupLogFramework(Level.INFO);

        // process the message in order to trigger the desired event
        handler.processMessage(message, context);

        // checking that no event has been handled by the appender
        Event loggedEvent = appender.getLastEvent();
        assertTrue(loggedEvent == null);
    }

    /**
     * This method tests the logging of the start sync session event when the
     * user isn't authenticate (on error), the log level is set to trace.
     */
    public void testFireStartSyncSessionEventOnError() throws Exception {
        // setting up the log framework, the logger level is set to trace
        appender = setupLogFramework(Level.TRACE);

        // Forcing the fake Sync4jEngine not to authenticate
        // in this way a start sync session error event will be
        // triggered
        forceMockAuthenticationToFail();

        // process the message in order to trigger the desired event
        handler.processMessage(message, context);

        Event expectedEvent =
                SyncSessionEvent.createStartSessionEventOnError(null,
                DEVICE_ID,
                null,
                START_SYNC_SESSION_ERROR_MESSAGE,
                "401");
        Event loggedEvent = appender.getLastEvent();
        assertTrue(EventHelper.compareEvent(loggedEvent, expectedEvent));
    }

    /**
     *
     * This method tests the logging of the end sync session event,
     * with the level of the appender set to trace.
     *
     * @throws java.lang.Exception if any error occurs
     */
    public void testFireEndSyncSessionEventTrace() throws Exception {
        // setting up the log framework, the logger level is set to trace
        appender = setupLogFramework(Level.TRACE);

        // simulating the login process (otherwise exiting session will cause
        // NullPointerException)
        handler.processMessage(message, context);
        // ending session
        handler.endSession();

        Event expectedEvent = SyncSessionEvent.createEndSessionEvent(USER_NAME,
                DEVICE_ID, SESSION_ID,
                END_SYNC_SESSION_MESSAGE);
        Event loggedEvent = appender.getLastEvent();
        assertTrue(EventHelper.compareEvent(loggedEvent, expectedEvent));

    }

    /**
     * This method tests the logging of the end sync session event,
     * with the level of the appender set to trace.
     *
     * @throws java.lang.Exception if any error occurs.
     */
    public void testFireEndSyncSessionEventInfo() throws Exception {
        // setting up the log framework, the logger level is set to info
        appender = setupLogFramework(Level.INFO);

        // simulating the login process (otherwise exiting session will cause
        // NullPointerException)
        handler.processMessage(message, context);
        // ending session
        handler.endSession();

        // checking that no event has been handled by the appender
        Event loggedEvent = appender.getLastEvent();
        assertTrue(loggedEvent == null);
    }

    // ---------------------------------------------------- Test private Methods

    public void testContainsAllDataStores_RequiredDatabaseHasDataStoreCaps() throws Throwable {

        Capabilities capabilities =
                createCapabilities(new Long(5235), new String[] {"CALENDAR","CONTACT"});
        Database[] databases =
                new Database[] {createDataBase("Contact","CONTACT"),
                                createDataBase("Calendar","CALENDAR")};
        boolean result = (Boolean)PrivateAccessor.invoke(
            handler,
            "containsAllDataStores",
            new Class[] {Capabilities.class, Database[].class},
            new Object[] {capabilities, databases}
        );

        assertTrue(result);
    }

    public void testContainsAllDataStores_RequiredDatabaseHasNoDataStoreCaps() throws Throwable {

        Capabilities capabilities =
                createCapabilities(new Long(5235), new String[] {"CALENDAR","CONTACT"});
        Database[] databases =
                new Database[] {createDataBase("Mail","MAIL"),
                                createDataBase("Calendar","CALENDAR")};
        boolean result = (Boolean)PrivateAccessor.invoke(
            handler,
            "containsAllDataStores",
            new Class[] {Capabilities.class, Database[].class},
            new Object[] {capabilities, databases}
        );

        assertFalse(result);
    }

    public void testContainsAllDataStores_DeviceDoesNotSupportCapabilities()
    throws Throwable {

        Capabilities capabilities =
                createCapabilities(new Long(-1), new String[] {});
        Database[] databases =
                new Database[] {createDataBase("Mail","MAIL"),
                                createDataBase("Calendar","CALENDAR")};
        boolean result = (Boolean)PrivateAccessor.invoke(
            handler,
            "containsAllDataStores",
            new Class[] {Capabilities.class, Database[].class},
            new Object[] {capabilities, databases}
        );

        assertTrue(result);
    }

    public void testNextLOChunk() throws Throwable {

        final long msgSizeAlreadyUsed = 200;
        final long msgSizeAvailable   = 213;

        // setup the SyncSessionHandler's SyncState
        SyncState syncState = new SyncState();
        syncState.sendingLOURI  = null;
        syncState.maxMsgSize = new XMLSizeCalculator().getMsgSizeOverhead() + 
                msgSizeAlreadyUsed + msgSizeAvailable;
        injectSyncState(handler, syncState);

        String sourceURI = "card";

        // set up ItemizedCommand
        CmdID cmdID = new CmdID("2");
        boolean noResp = false;
        Cred cred = null;
        Meta meta = null;
        String itemLocalURI = "123";
        Item item = new Item(new Source(itemLocalURI,null), null, null,
                new ComplexData(getStringLong1000Char()), false);
        Item[] items = new Item[]{item };
        ItemizedCommand cmd = new Add(cmdID,noResp, cred, meta, items);

        ItemizedCommand command = (ItemizedCommand) PrivateAccessor.invoke(
                     handler,
                     "nextLOChunk",
                     new Class[] {String.class, ItemizedCommand.class, long.class},
                     new Object[] {sourceURI, cmd, msgSizeAlreadyUsed});

        int dataLength = ((Item)command.getItems().get(0)).getData().getData().length();
        assertEquals(msgSizeAvailable, dataLength);
        assertEquals(sourceURI+"/"+itemLocalURI, syncState.sendingLOURI);
    }

    /**
     * This method tests the private method for the conversion of
     * authentication state to synchronization status code.
     */
    public void testAuthenticationState2StatusCode() throws Exception {
        int[] inputValues = new int[] { -231,
                    SecurityConstants.AUTH_UNAUTHENTICATED    ,
                    SecurityConstants.AUTH_MISSING_CREDENTIALS,
                    SecurityConstants.AUTH_INVALID_CREDENTIALS,
                    SecurityConstants.AUTH_PAYMENT_REQUIRED   ,
                    SecurityConstants.AUTH_NOT_AUTHORIZED     ,
                    SecurityConstants.AUTH_RETRY_1            ,
                    234
        };

        int[] expectedValues = new int[]{
                                    StatusCode.FORBIDDEN,
                                    StatusCode.FORBIDDEN,
                                    StatusCode.MISSING_CREDENTIALS,
                                    StatusCode.INVALID_CREDENTIALS,
                                    StatusCode.PAYMENT_REQUIRED,
                                    StatusCode.FORBIDDEN,
                                    StatusCode.FORBIDDEN,
                                    StatusCode.FORBIDDEN
                               };

        assertEquals("Input data and expected result sizes don't match",
                     expectedValues.length,
                     inputValues.length);

        StringBuffer errMsg = new StringBuffer("Authentication state 2 status code conversion test failed for:");
        boolean      error  = false;
        for(int i=0;i<inputValues.length;i++) {
            int input    = inputValues[i];
            int expected = expectedValues[i];
            int result   = invokeAuthenticationState2StatusCode(handler,input);
            if(expected!=result) {
                error = true;
                errMsg.append(" [");
                errMsg.append(input);
                errMsg.append(" -> ");
                errMsg.append(expected);
                errMsg.append("] ");
            }
        }

        if(error) {
            fail(errMsg.toString());
        }
    }

    public void testGetMoveToMessage() throws Throwable {

        int state = SessionHandler.STATE_START;
        String stateMessage = "Starting session";
        String expectedResult = "moving to state: STATE_START [" + stateMessage + "]";


        String result = (String)PrivateAccessor.invoke(
            handler,
            "getMoveToMessage",
            new Class[] {Integer.class, String.class},
            new Object[] {state, stateMessage}
        );

        assertEquals("Wrong message", expectedResult, result);
    }

    public void testGetMoveToMessage_null_message() throws Throwable {

        int state = SessionHandler.STATE_ERROR;
        String stateMessage = null;
        String expectedResult = "moving to state: STATE_ERROR";


        String result = (String)PrivateAccessor.invoke(
            handler,
            "getMoveToMessage",
            new Class[] {Integer.class, String.class},
            new Object[] {state, stateMessage}
        );

        assertEquals("Wrong message", expectedResult, result);
    }

    public void testGetMoveToMessage_empty_message() throws Throwable {

        int state = SessionHandler.STATE_ERROR;
        String stateMessage = "";
        String expectedResult = "moving to state: STATE_ERROR";

        String result = (String)PrivateAccessor.invoke(
            handler,
            "getMoveToMessage",
            new Class[] {Integer.class, String.class},
            new Object[] {state, stateMessage}
        );

        assertEquals("Wrong message", expectedResult, result);
    }

    // --------------------------------------------------------- Private methods

    /**
     * This method allows to change the authentication
     * policy of the Sync4jEngine Mock in order to unauthenticate
     * the user.
     */
    private void forceMockAuthenticationToFail() {
        setMockAuthenticationPolicy(true);
    }

    /**
     * This method allows to restore the authentication policy of
     * the Sync4jEngine Mock in order to authenticate the user.
     */
    private void restoreMockAuthenticationPolicy() {
        setMockAuthenticationPolicy(false);
    }

    /**
     * This method allows to change the authentication policy
     * of the mock Sync4jEngine according to the input parameter.
     *
     * @param value
     */
    private void setMockAuthenticationPolicy(boolean value) {
        if (mockEngine != null && mockEngine instanceof Sync4jEngineMock) {
            Sync4jEngineMock mock = (Sync4jEngineMock) mockEngine;
            mock.setFailAuthentication(value);
        } else {
            throw new RuntimeException("Mock Sync4jEngine object is null");
        }
    }

    /**
     * This method allows to inject a mock Sync4jEngine in the
     * SyncSessionHandler using PrivateAccessor.
     *
     * @param handler is the handler that needs to be injected
     * @throws java.lang.NoSuchFieldException if any errors occcur.
     */
    private void injectMockEngine(SyncSessionHandler handler)
    throws NoSuchFieldException {

        if (mockEngine == null) {
            mockEngine = new Sync4jEngineMock(Configuration.getConfiguration(),
                    FAKE_USER, SESSION_ID, false);
        }

        PrivateAccessor.setField(handler, "syncEngine", mockEngine);
    }

    private void injectSyncState(SyncSessionHandler handler, SyncState syncState)
    throws NoSuchFieldException {
        PrivateAccessor.setField(handler, "syncState", syncState);
    }

    /**
     * This is a facility method to load the SyncML message.
     *
     * @return the SyncML object loaded from the input file.
     *
     * @throws java.lang.Exception if something goes wrong while loading the SyncML message.
     */
    private SyncML loadSyncmlMessage(String fileName) throws Exception {
        File inputFile = new File(DATA_HOME, fileName);
        return unmarshal(inputFile);
    }

    /**
     * This method is used to create a fake context.
     *
     * @return the context to use for the SyncSessionHandler.
     */
    private MessageProcessingContext setupContext() {
        MessageProcessingContext context = new MessageProcessingContext();
        context.setRequestProperty(context.PROPERTY_REQUEST_HEADERS, new HashMap());
        return context;
    }

    /**
     * This method is used to create the SyncSessionHandler used
     * to log the events.
     *
     * @return a SyncSessionHandler object.
     */
    private SyncSessionHandler setupHandler() throws NoSuchFieldException {
        SyncSessionHandler syncSessionHandler = new SyncSessionHandler();

        injectMockEngine(syncSessionHandler);

        syncSessionHandler.setSizeCalculator(new XMLSizeCalculator());
        return syncSessionHandler;
    }

    /**
     * Converts the input file's content in SyncML object using binding.xml.
     *
     * @param f the file
     * @return the SyncML object
     */
    private SyncML unmarshal(File f) throws Exception {
        FileReader r = new FileReader(f);

        IUnmarshallingContext c =
            BindingDirectory.getFactory("binding", SyncML.class).createUnmarshallingContext();

        return (SyncML) c.unmarshalDocument(r);
    }

    /**
     * This method is used to set up the logging framework.
     * That is, a new test appender is created, is bound to the event
     * logger.
     * The level of the event logger is set to the input log level.
     *
     * @param logLevel it's the level to which the event logger will be set.
     *
     * @return the appender containing the last logged event.
     */
    private TestAppender setupLogFramework(Level logLevel) {
        TestAppender appender = new TestAppender();
        LOG.addAppender(appender);
        LOG.setLevel(logLevel);
        return appender;
    }

    /**
     * Create a simple Capabilities
     * @param id The Capabilities id
     * @param dataStoreNames the datastore names
     * @return a Capabilities
     */
    private Capabilities createCapabilities(Long id, String[] dataStoreNames) {
        Capabilities capabilities = new Capabilities();
        capabilities.setId(id);
        capabilities.setDevInf(createDevInf(dataStoreNames));
        return capabilities;
    }

    /**
     * Create a simple DevInf containing an array of simple DataStores
     * @param dataStoreNames
     * @return a DevInf
     */
    private DevInf createDevInf(String[] dataStoreNames) {
        DevInf devInf = new DevInf();

        List<DataStore> datastores = new ArrayList();
        for (String dataStoreName : dataStoreNames) {

            DataStore dataStore = new DataStore();
            dataStore.setSourceRef(new SourceRef(dataStoreName));
            datastores.add(dataStore);
        }
        devInf.setDataStores(datastores.toArray(new DataStore[datastores.size()]));
        return devInf;
    }

    /**
     * Create a simple Database
     * @param name The database's name
     * @param targetURI the target URI
     * @return
     */
    private Database createDataBase(String name, String targetURI) {
        Database database = new Database(name) ;
        database.setTarget(new Target(targetURI));
        return database;
    }

    private String getStringLong1000Char() {
        StringBuilder content = new StringBuilder();
        for (int i =0; i < 1000; ++i) {
            content.append("1234567890");
        }
        return content.toString();
    }

    private int invokeAuthenticationState2StatusCode(SyncSessionHandler handler,
                                                        int inputAuthState) {
        Object result = null;
        try {
            result = PrivateAccessor.invoke(handler, "authenticationState2StatusCode", new Class[]{int.class}, new Object[]{inputAuthState});
        } catch (Throwable ex) {
            fail("Error invokin private method.");
        }

        if(result!=null && result instanceof Integer) {
            return (Integer) result;
        }

        return -1;
    }

}
