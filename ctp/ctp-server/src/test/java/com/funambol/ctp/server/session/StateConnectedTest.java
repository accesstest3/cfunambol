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

import com.funambol.ctp.core.Auth;
import com.funambol.ctp.core.GenericCommand;
import com.funambol.ctp.core.Message;
import com.funambol.ctp.core.NotAuthenticated;
import com.funambol.ctp.core.Ok;
import com.funambol.ctp.core.Unauthorized;
import com.funambol.ctp.server.authentication.MockAuthenticationManager;
import com.funambol.ctp.server.notification.MockNotificationDispatcher;
import junit.framework.*;
import junitx.util.PrivateAccessor;

/**
 *
 */
public class StateConnectedTest extends TestCase {

    MockIoSession session = new MockIoSession();
    MockNotificationDispatcher dispatcher = new MockNotificationDispatcher();

    SessionManager sessionManager = new SessionManager(session, dispatcher);

    public StateConnectedTest(String testName) {
        super(testName);
 }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    private SessionManager createSessionManager() throws NoSuchFieldException {
        State sessionState = new StateConnected();
        SessionManager sessionManager = new SessionManager(session, dispatcher);
        PrivateAccessor.setField(sessionManager, "sessionState", sessionState);
        return sessionManager;
    }

    public void testFoo() throws NoSuchFieldException {
        SessionManager sessionManager = createSessionManager();

        assertTrue(true);
    }

    public void testOnAuthenticationReq_AUTHORIZED() throws NoSuchFieldException {

        String devid =    "deviceuser01";
        String username = "user01";
        String cred =     "qwerty";

        SessionManager sessionManager = createSessionManager();
        MockAuthenticationManager authenticationManager =
                (MockAuthenticationManager) PrivateAccessor.getField(sessionManager, "authenticationManager");

        authenticationManager.setProperty(username, "AUTHORIZED");

        Auth auth = new Auth(devid, username, cred);
        sessionManager.onAuthenticationReq(auth);

        Message message = (Message) session.getLastMessageWritten();
        GenericCommand command = message.getCommand();
        assertTrue(command instanceof Ok);
        State nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateAuthenticated);
    }

    public void testOnAuthenticationReq_NOT_AUTHORIZED() throws NoSuchFieldException {

        String devid =    "deviceuser01";
        String username = "user01";
        String cred =     "qwerty";

        SessionManager sessionManager = createSessionManager();
        MockAuthenticationManager authenticationManager =
                (MockAuthenticationManager) PrivateAccessor.getField(sessionManager, "authenticationManager");

        authenticationManager.setProperty(username, "NOT_AUTHORIZED");

        Auth auth = new Auth(devid, username, cred);
        sessionManager.onAuthenticationReq(auth);

        Message message = (Message) session.getLastMessageWritten();
        GenericCommand command = message.getCommand();
        assertTrue(command instanceof Unauthorized);
        State nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateLeaving);
    }

    public void testOnAuthenticationReq_NOT_AUTHENTICATED() throws NoSuchFieldException {

        String devid =    "deviceuser01";
        String username = "user01";
        String cred =     "qwerty";

        SessionManager sessionManager = createSessionManager();
        MockAuthenticationManager authenticationManager =
                (MockAuthenticationManager) PrivateAccessor.getField(sessionManager, "authenticationManager");

        authenticationManager.setProperty(username, "NOT_AUTHENTICATED");

        Auth auth = new Auth(devid, username, cred);
        sessionManager.onAuthenticationReq(auth);

        Message message = (Message) session.getLastMessageWritten();
        GenericCommand command = message.getCommand();
        assertTrue(command instanceof NotAuthenticated);
        State nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateConnected);
    }
    
    /**
     * Parameter "maxAuthenticationRetries" must be set to value "2" in file 
     * ctp-server\src\test\resources\config\com\funambol\ctp\server\CTPServerConfiguration.xml
     */
    public void testAuthenticationRetries() throws NoSuchFieldException {

        String devid =    "deviceuser01";
        String username = "user01";
        String cred =     "qwerty";

        SessionManager sessionManager = createSessionManager();

        MockAuthenticationManager authenticationManager =
                (MockAuthenticationManager) PrivateAccessor.getField(sessionManager, "authenticationManager");
        authenticationManager.setProperty(username, "NOT_AUTHENTICATED");

        // send first authorization
        sessionManager.onAuthenticationReq(new Auth(devid, username, cred));

        GenericCommand command = ((Message) session.getLastMessageWritten()).getCommand();
        assertTrue(command instanceof NotAuthenticated);
        
        State nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateConnected);
        
        // send second authorization
        sessionManager.onAuthenticationReq(new Auth(devid, username, cred));

        command = ((Message) session.getLastMessageWritten()).getCommand();
        assertTrue(command instanceof NotAuthenticated);
        
        nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateConnected);
        
        // send third authorization
        sessionManager.onAuthenticationReq(new Auth(devid, username, cred));

        command = ((Message) session.getLastMessageWritten()).getCommand();
        assertTrue(command instanceof com.funambol.ctp.core.Error);
        
        nextState = (State) PrivateAccessor.getField(sessionManager,
                "sessionState");
        assertTrue(nextState instanceof  StateLeaving);
        
    }
    
}
