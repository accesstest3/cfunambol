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
 * @version $Id: PushSMSEventTest.java 30513 2009-03-13 10:00:06Z luigiafassina $
 */
public class PushSMSEventTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String PUSH_COP          = "PUSH_COP"         ;
    private static final String PUSH_CLP          = "PUSH_CLP"         ;
    private static final String PUSH_SMS          = "PUSH_SMS"         ;
    private static final String OTA               = "OTA"              ;
    private static final String SMS_DOWNLOAD_LINK = "SMS_DOWNLOAD_LINK";

    // ------------------------------------------------------------ Constructors
    public PushSMSEventTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of createPushCOPEvent method, of class PushSMSEvent.
     */
    public void testCreatePushCOPEvent() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "COP-SENDER";
        String message    = null;

        Event expResult =
            new PushSMSEvent(PUSH_COP, userName, deviceId, sessionId,
                             originator, message, false);

        Event result =
            PushSMSEvent.createPushCOPEvent(userName, deviceId, sessionId,
                                            originator, message);
        compareEvent(expResult, result);
    }

    /**
     * Test of createPushCOPEventOnError method, of class PushSMSEvent.
     */
    public void testCreatePushCOPEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "COP-SENDER";
        String message    = "Error occurred during Push COP Event";

        Event expResult =
            new PushSMSEvent(PUSH_COP, userName, deviceId, sessionId,
                             originator, message, true);

        Event result =
            PushSMSEvent.createPushCOPEventOnError(userName, deviceId,
                                                sessionId, originator, message);
        
        compareEvent(expResult, result);
    }

    /**
     * Test of createPushCLPEvent method, of class PushSMSEvent.
     */
    public void testCreatePushCLPEvent() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "CLP-SENDER";
        String message    = null;

        Event expResult =
            new PushSMSEvent(PUSH_CLP, userName, deviceId, sessionId,
                             originator, message, false);

        Event result =
            PushSMSEvent.createPushCLPEvent(userName, deviceId, sessionId,
                                            originator, message);
        
        compareEvent(expResult, result);
    }

    /**
     * Test of createPushCLPEventOnError method, of class PushSMSEvent.
     */
    public void testCreatePushCLPEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "CLP-SENDER";
        String message    = "Error occurred during Push CLP Event";

        Event expResult =
            new PushSMSEvent(PUSH_CLP, userName, deviceId, sessionId,
                             originator, message, true);

        Event result =
            PushSMSEvent.createPushCLPEventOnError(userName, deviceId,
                                                sessionId, originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createPushSMSEvent method, of class PushSMSEvent.
     */
    public void testCreatePushSMSEvent() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "SMS-SENDER";
        String message    = null;

        Event expResult =
            new PushSMSEvent(PUSH_SMS, userName, deviceId, sessionId,
                             originator, message, false);

        Event result =
            PushSMSEvent.createPushSMSEvent(userName, deviceId, sessionId,
                                            originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createPushSMSEventOnError method, of class PushSMSEvent.
     */
    public void testCreatePushSMSEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "SMS-SENDER";
        String message    = "Error occurred during Push SMS Event";

        Event expResult =
            new PushSMSEvent(PUSH_SMS, userName, deviceId, sessionId,
                             originator, message, true);

        Event result =
            PushSMSEvent.createPushSMSEventOnError(userName, deviceId,
                                                sessionId, originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createOTAEvent method, of class PushSMSEvent.
     */
    public void testCreateOTAEvent() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "OTA-SENDER";
        String message    = null;

        Event expResult =
            new PushSMSEvent(OTA, userName, deviceId, sessionId, originator,
                            message, false);

        Event result =
            PushSMSEvent.createOTAEvent(userName, deviceId, sessionId,
                                        originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createOTAEventOnError method, of class PushSMSEvent.
     */
    public void testCreateOTAEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "OTA-SENDER";
        String message    = "Error occurred during OTA Event";

        Event expResult =
            new PushSMSEvent(OTA, userName, deviceId, sessionId,
                             originator, message, true);

        Event result =
            PushSMSEvent.createOTAEventOnError(userName, deviceId, sessionId,
                                               originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createSMSDownloadLinkEvent method, of class PushSMSEvent.
     */
    public void testCreateSMSDownloadLinkEvent() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "SMS-SENDER";
        String message    = null;

        Event expResult =
            new PushSMSEvent(SMS_DOWNLOAD_LINK, userName, deviceId, sessionId,
                             originator, message, false);

        Event result =
            PushSMSEvent.createSMSDownloadLinkEvent(userName, deviceId,
                                                sessionId, originator, message);

        compareEvent(expResult, result);
    }

    /**
     * Test of createSMSDownloadLinkEventOnError method, of class PushSMSEvent.
     */
    public void testCreateSMSDownloadLinkEventOnError() {
        String userName   = "guest";
        String deviceId   = "IMEI:1234567890";
        String sessionId  = "2E1D5981D8151F3F04372E5D55C5D168";
        String originator = "SMS-SENDER";
        String message    = "Error occurred during SMS Download Link Event";

        Event expResult =
            new PushSMSEvent(SMS_DOWNLOAD_LINK, userName, deviceId, sessionId,
                             originator, message, true);

        Event result =
            PushSMSEvent.createSMSDownloadLinkEventOnError(userName, deviceId,
                                                sessionId, originator, message);

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