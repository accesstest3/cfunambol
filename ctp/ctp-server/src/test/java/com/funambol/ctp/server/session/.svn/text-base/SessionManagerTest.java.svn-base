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

import com.funambol.ctp.server.authentication.AuthenticationManager;
import com.funambol.ctp.server.authentication.mock.MockAuthenticationManager;

import com.funambol.ctp.server.notification.MockNotificationDispatcher;
import com.funambol.ctp.server.notification.NotificationProvider;
import junit.framework.*;
import junitx.util.PrivateAccessor;
import org.apache.mina.common.IoSession;

/**
 *
 */
public class SessionManagerTest extends TestCase {

    IoSession session = new MockIoSession();
    AuthenticationManager authenticationManager = new MockAuthenticationManager();
    NotificationProvider dispatcher = new MockNotificationDispatcher();

    public SessionManagerTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getSessionState method, of class com.funambol.ctp.server.SessionManager.
     */
    public void testGetSessionState() throws NoSuchFieldException {

        SessionManager instance = new SessionManager(session,
                                                     dispatcher);

        State expResult = new StateConnected();
        State result = (State) PrivateAccessor.getField(instance, "sessionState");
        assertTrue(expResult.deepequals(result));
    }

    /**
     * Test of setSessionState method, of class com.funambol.ctp.server.SessionManager.
     */
    public void testSetSessionState_same_state() throws Throwable {

        State sessionState = new StateConnected();
        SessionManager instance = new SessionManager(session,
                                                     dispatcher);

        PrivateAccessor.invoke(instance, "setSessionState",
                new Class[] {State.class}, new Object[] {sessionState} );
        State instanceState = (State) PrivateAccessor.getField(instance, "sessionState");
        assertTrue(sessionState.deepequals(instanceState));
        assertFalse(sessionState == instanceState);
    }

    /**
     * Test of setSessionState method, of class com.funambol.ctp.server.SessionManager.
     */
    public void testSetSessionState_other_state() throws Throwable {

        State sessionState = new StateAuthenticated();
        SessionManager instance = new SessionManager(session,
                                                     dispatcher);
        State instanceState = (State) PrivateAccessor.getField(instance, "sessionState");
        assertFalse(sessionState.deepequals(instanceState));

        PrivateAccessor.invoke(instance, "setSessionState",
                new Class[] {State.class}, new Object[] {sessionState} );

        instanceState = (State) PrivateAccessor.getField(instance, "sessionState");
        assertFalse(new StateConnected().deepequals(instanceState));
        assertTrue(new StateAuthenticated().deepequals(instanceState));
    }
}
