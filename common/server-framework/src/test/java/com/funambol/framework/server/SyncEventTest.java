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
 * SyncEvent test cases.
 *
 * @version $Id: SyncEventTest.java 34823 2010-06-24 17:29:35Z luigiafassina $
 */
public class SyncEventTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String START_SYNC = "START_SYNC";
    private static final String END_SYNC   = "END_SYNC"  ;

    // ------------------------------------------------------------ Constructors
    public SyncEventTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of createStartSyncEvent method, of class SyncEvent.
     */
    public void testCreateStartSyncEvent() {

        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String syncType  = "200";
        String source    = "card";
        String message   = null;

        Event expResult  =
            new SyncEvent(START_SYNC, userName, deviceId, sessionId, syncType,
                          source, 0, 0, 0, 0, message, false);
        Event result =
            SyncEvent.createStartSyncEvent(userName, deviceId, sessionId,
                                           syncType, source, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createStartSyncEventOnError method, of class SyncEvent.
     */
    public void testCreateStartSyncEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String syncType   = "200";
        String source     = "card";
        String message    = "Error occurred during Start Sync Event";
        String statusCode = "404";

        Event expResult  =
            new SyncEvent(START_SYNC, userName, deviceId, sessionId, syncType,
                          source, message, statusCode);

        Event result = SyncEvent.createStartSyncEventOnError(
            userName, deviceId, sessionId, syncType, source,
            message, statusCode
        );
        compareEvent(expResult, result);
    }

    /**
     * Test of createEndSyncEvent method, of class SyncEvent.
     */
    public void testCreateEndSyncEvent() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String syncType  = "200";
        String source    = "card";
        String status    = "200";
        int    numAddedItems   = 2;
        int    numDeletedItems = 3;
        int    numUpdatedItems = 4;
        String message   = null;


        Event expResult  =
            new SyncEvent(END_SYNC, userName, deviceId, sessionId, syncType,
                          source,status, numAddedItems, numDeletedItems,
                          numUpdatedItems, 0, message, false);

        Event result = SyncEvent.createEndSyncEvent(userName, deviceId,
            sessionId, syncType, source,status, numAddedItems, numDeletedItems,
            numUpdatedItems, 0, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createEndSyncEventOnError method, of class SyncEvent.
     */
    public void testCreateEndSyncEventOnError() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String syncType  = "200";
        String source    = "card";
        String message   = "Error occurred during End Sync Event";

        Event expResult  =
            new SyncEvent(END_SYNC, userName, deviceId, sessionId, syncType,
                          source, 0, 0, 0, 0, message, true);

        Event result = SyncEvent.createEndSyncEventOnError(userName, deviceId,
            sessionId, syncType, source, message);

        compareEvent(expResult, result);
    }

    public void testCreateEndSyncEventOnError_status() {
        String userName  = "guest";
        String deviceId  = "IMEI:1234567890";
        String sessionId = "2E1D5981D8151F3F04372E5D55C5D168";
        String syncType  = "200";
        String source    = "card";
        String message   = "Error occurred during End Sync Event";
        String status    = "506";
        Event result = SyncEvent.createEndSyncEventOnError(userName,
                deviceId,
                sessionId,
                syncType,
                source,
                message,
                status);
       Event expexctedEvent  =
            new SyncEvent(END_SYNC,
                          userName,
                          deviceId,
                          sessionId,
                          syncType,
                          source,
                          message,
                          status);

        compareEvent(expexctedEvent, result);


    }

    // --------------------------------------------------------- Private methods
    private void compareEvent(Event ev1, Event ev2) {
        assertEquals(ev1.getEventType()          , ev2.getEventType());
        assertEquals(ev1.getUserName()           , ev2.getUserName());
        assertEquals(ev1.getDeviceId()           , ev2.getDeviceId());
        assertEquals(ev1.getSessionId()          , ev2.getSessionId());
        assertEquals(ev1.getSyncType()           , ev2.getSyncType());
        assertEquals(ev1.getSource()             , ev2.getSource());
        assertEquals(ev1.getNumAddedItems()      , ev2.getNumAddedItems());
        assertEquals(ev1.getNumDeletedItems()    , ev2.getNumDeletedItems());
        assertEquals(ev1.getNumUpdatedItems()    , ev2.getNumUpdatedItems());
        assertEquals(ev1.getNumTransferredItems(), ev2.getNumTransferredItems());
        assertEquals(ev1.getOriginator()         , ev2.getOriginator());
        assertEquals(ev1.getMessage()            , ev2.getMessage());
        assertEquals(ev1.isError()               , ev2.isError());
        assertEquals(ev1.getStatusCode()         , ev2.getStatusCode());
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
