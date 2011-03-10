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
 * SyncSessionEvent test cases.
 *
 * @version $Id: SyncSessionEventTest.java 34823 2010-06-24 17:29:35Z luigiafassina $
 */
public class SyncSessionEventTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String START_SYNC_SESSION = "START_SYNC_SESSION";
    private static final String END_SYNC_SESSION   = "END_SYNC_SESSION"  ;

    // ------------------------------------------------------------ Constructors
    public SyncSessionEventTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of createStartSessionEvent method, of class SyncSessionEvent.
     */
    public void testCreateStartSessionEvent() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String message   = null;

        Event expResult = 
            new SyncSessionEvent(START_SYNC_SESSION, userName, deviceId,
                                 sessionId, message, 0, null, false);

        Event result = 
            SyncSessionEvent.createStartSessionEvent(userName, deviceId,
                                                     sessionId, message);
        
        compareEvent(expResult, result);
    }

    /**
     * Test of createStartSessionEventOnError method, of class SyncSessionEvent.
     */
    public void testCreateStartSessionEventOnError() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String message   = "Error occurred during Start Sync Session Event";
        String statusCode = "401";
        Event expResult =
            new SyncSessionEvent(START_SYNC_SESSION, userName, deviceId,
                                 sessionId, message, 0, statusCode, true);

        Event result = 
            SyncSessionEvent.createStartSessionEventOnError(userName,
                                                            deviceId,
                                                            sessionId, 
                                                            message,
                                                            statusCode);

        compareEvent(expResult, result);
    }

    /**
     * Test of createEndSessionEvent method, of class SyncSessionEvent.
     */
    public void testCreateEndSessionEvent() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String message   = null;

        Event expResult =
            new SyncSessionEvent(END_SYNC_SESSION, userName, deviceId,
                                 sessionId, message, 0, null, false);

        Event result =
            SyncSessionEvent.createEndSessionEvent(userName, deviceId,
                                                   sessionId, message, 0);

        compareEvent(expResult, result);
    }

    /**
     * Test of createEndSessionEventOnError method, of class SyncSessionEvent.
     */
    public void testCreateEndSessionEventOnError() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String message   = "Error occurred during End Sync Session Event";

        Event expResult =
            new SyncSessionEvent(END_SYNC_SESSION, userName, deviceId,
                                 sessionId, message, 0, null, true);

        Event result =
            SyncSessionEvent.createEndSessionEventOnError(userName, deviceId,
                                                          sessionId, message, 0);

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
