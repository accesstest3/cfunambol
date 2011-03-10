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
package com.funambol.framework.server;

import junit.framework.TestCase;

/**
 * PushSMSEvent test cases.
 *
 * @version $Id: PushFlowEventTest.java 30613 2009-03-25 08:38:17Z luigiafassina $
 */
public class PushFlowEventTest extends TestCase {

    // --------------------------------------------------------------- Constants
    public static final String IL_CHECK     = "IL_CHECK"    ;
    public static final String IL_NEW_MAIL  = "IL_NEW_MAIL" ;
    public static final String IL_PUSH      = "IL_PUSH"     ;
    public static final String DS_PUSH_REQ  = "DS_PUSH_REQ" ;
    public static final String DS_PUSH_SENT = "DS_PUSH_SENT";

    // ------------------------------------------------------------ Constructors
    public PushFlowEventTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of createILCheckEvent method, of class PushFlowEvent.
     */
    public void testCreateILCheckEvent() {

        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "Start inboxlistener task for : id: 0 [accountId: 0, task bean file: com/funambol/email/inboxlistener/task/InboxListenerTask.xml, active: true, status: U, refresh time: 1 min., userName: dip, MSLogin: recent:doesitpush, MSAddress: doesitpush@gmail.com, MSMailboxname: null, push: true, Max Email: 20, ] (Server id: 100 - Server: custom - protocol: pop3)]]";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_CHECK, userName, deviceId, sessionId, originator, message, false);

        Event result =
            PushFlowEvent.createILCheckEvent(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createILCheckEventOnError method, of class PushFlowEvent.
     */
    public void testCreateILCheckEventOnError() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "Inbox Listener does not start";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_CHECK, userName, deviceId, sessionId, originator, message, true);

        Event result =
            PushFlowEvent.createILCheckEventOnError(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createILNewEmailEvent method, of class PushFlowEvent.
     */
    public void testCreateILNewEmailEvent() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "Sending Notification: true for user: dip";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_NEW_MAIL, userName, deviceId, sessionId, originator, message, false);

        Event result =
            PushFlowEvent.createILNewEmailEvent(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createILNewEmailEventOnError method, of class PushFlowEvent.
     */
    public void testCreateILNewEmailEventOnError() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "Push is disable for user: dip";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_NEW_MAIL, userName, deviceId, sessionId, originator, message, true);

        Event result =
            PushFlowEvent.createILNewEmailEventOnError(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createILPushEvent method, of class PushFlowEvent.
     */
    public void testCreateILPushEvent() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "Notifying User 'dip' for 'mail' [application/vnd.omads-email+xml]";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_PUSH, userName, deviceId, sessionId, originator, message, false);

        Event result =
            PushFlowEvent.createILPushEvent(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createILPushEventOnError method, of class PushFlowEvent.
     */
    public void testCreateILPushEventOnError() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = null;
        String originator = PushFlowEvent.IL_ORIGINATOR;
        String message = "No User 'dip' found for 'mail' [application/vnd.omads-email+xml]";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.IL_PUSH, userName, deviceId, sessionId, originator, message, true);

        Event result =
            PushFlowEvent.createILPushEventOnError(userName, deviceId, sessionId, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createDSPushReqEvent method, of class PushFlowEvent.
     */
    public void testCreateDSPushReqEvent() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = PushFlowEvent.DS_ORIGINATOR;
        String source = "card|mail";
        String message = "Try to notify user 'dip'";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.DS_PUSH_REQ, userName, deviceId, sessionId, originator, source, message, false);

        Event result =
            PushFlowEvent.createDSPushReqEvent(userName, deviceId, sessionId, source, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createDSPushReqEventOnError method, of class PushFlowEvent.
     */
    public void testCreateDSPushReqEventOnError() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = PushFlowEvent.DS_ORIGINATOR;
        String source = "card|mail";
        String message = "Unable to notify user 'dip'";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.DS_PUSH_REQ, userName, deviceId, sessionId, originator, source, message, true);

        Event result =
            PushFlowEvent.createDSPushReqEventOnError(userName, deviceId, sessionId, source, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createDSPushSentEvent method, of class PushFlowEvent.
     */
    public void testCreateDSPushSentEvent() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = "8359E4F2C86835ED66E7B2130577D2A2";
        String originator = PushFlowEvent.DS_ORIGINATOR;
        String source = null;
        String message = "Notification delivered.";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.DS_PUSH_SENT, userName, deviceId, sessionId, originator, source, message, false);

        Event result =
            PushFlowEvent.createDSPushSentEvent(userName, deviceId, sessionId, source, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createDSPushSentEventOnError method, of class PushFlowEvent.
     */
    public void testCreateDSPushSentEventOnError() {
        String userName = "dip";
        String deviceId = null;
        String sessionId = "8359E4F2C86835ED66E7B2130577D2A2";
        String originator = PushFlowEvent.DS_ORIGINATOR;
        String source = null;
        String message = "Error during the Notification delivering.";

        Event expResult =
            new PushFlowEvent(PushFlowEvent.DS_PUSH_SENT, userName, deviceId, sessionId, originator, source, message, true);

        Event result =
            PushFlowEvent.createDSPushSentEventOnError(userName, deviceId, sessionId, source, message);

        compareEvent(expResult, result);
    }

    // --------------------------------------------------------- Private methods
    private void compareEvent(Event ev1, Event ev2) {
        assertEquals(ev1.getEventType() , ev2.getEventType());
        assertEquals(ev1.getUserName()  , ev2.getUserName());
        assertEquals(ev1.getDeviceId()  , ev2.getDeviceId());
        assertEquals(ev1.getSessionId() , ev2.getSessionId());
        assertEquals(ev1.getOriginator(), ev2.getOriginator());
        assertEquals(ev1.getMessage()   , ev2.getMessage());
        assertEquals(ev1.isError()      , ev2.isError());
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
}
