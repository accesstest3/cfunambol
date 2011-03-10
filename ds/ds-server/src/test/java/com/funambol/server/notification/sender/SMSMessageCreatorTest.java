/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.server.notification.sender;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.Message.Type;
import com.funambol.framework.server.Sync4jDevice;
import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 * SMSMessageCreator test cases
 * @version $Id: SMSMessageCreatorTest.java,v 1.1 2008-07-03 09:56:03 nichele Exp $
 */
public class SMSMessageCreatorTest extends TestCase {

    public SMSMessageCreatorTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of createSMSMessage method (SyncML 1.2 message to a IMEI device)
     */
    public void testCreateSMSMessage__1_2_IMEI() {
        byte[] content = new byte[] {
            (byte)0xA1, (byte)0xA2, (byte)0xD7, (byte)0x55, (byte)0xE6, (byte)0x82,
            (byte)0xA3, (byte)0x6B, (byte)0x36, (byte)0xD6, (byte)0xB9, (byte)0xD3,
            (byte)0x13, (byte)0x84, (byte)0x0D, (byte)0xCE, (byte)0x03, (byte)0x18,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08,
            (byte)0x66, (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
            (byte)0x6F, (byte)0x6C, (byte)0x30, (byte)0x60, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x72,
            (byte)0x64, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04,
            (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x6C, (byte)0x60, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x74, (byte)0x61,
            (byte)0x73, (byte)0x6B
        };
        Message message = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, content);
        SMSMessageCreator creator = new SMSMessageCreator();
        BinarySMSMessage sms = creator.createSMSMessage(new Sync4jDevice("IMEI:123456"), message);

        byte[] expectedWdp = new byte[] {(byte)0x06, (byte)0x05, (byte)0x04, (byte)0x0B, (byte)0x84, (byte)0xC0, (byte)0x02};
        byte[] expectedWsp = new byte[] {(byte)0x01, (byte)0x06, (byte)0x03, (byte)0xCE, (byte)0xAF, (byte)0x85};
        byte[] expectedContent = content;

        ArrayAssert.assertEquals("Wrong wdp", expectedWdp, sms.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", expectedWsp, sms.getWsp());
        ArrayAssert.assertEquals("Wrong content", expectedContent, sms.getContent());
    }

    /**
     * Test of createSMSMessage method (SyncML 1.2 message to a fjm- device)
     */
    public void testCreateSMSMessage__1_2_fjm() {
        byte[] content = new byte[] {
            (byte)0xA1, (byte)0xA2, (byte)0xD7, (byte)0x55, (byte)0xE6, (byte)0x82,
            (byte)0xA3, (byte)0x6B, (byte)0x36, (byte)0xD6, (byte)0xB9, (byte)0xD3,
            (byte)0x13, (byte)0x84, (byte)0x0D, (byte)0xCE, (byte)0x03, (byte)0x18,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08,
            (byte)0x66, (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
            (byte)0x6F, (byte)0x6C, (byte)0x30, (byte)0x60, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x72,
            (byte)0x64, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04,
            (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x6C, (byte)0x60, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x74, (byte)0x61,
            (byte)0x73, (byte)0x6B
        };
        Message message = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, content);
        SMSMessageCreator creator = new SMSMessageCreator();
        BinarySMSMessage sms = creator.createSMSMessage(new Sync4jDevice("fjm-1244534"), message);

        byte[] expectedWdp = new byte[] {(byte)0x06, (byte)0x05, (byte)0x04, (byte)0xC3, (byte)0x51, (byte)0xC0, (byte)0x02};
        byte[] expectedWsp = new byte[] {(byte)0x01, (byte)0x06, (byte)0x03, (byte)0xCE, (byte)0xAF, (byte)0x85};
        byte[] expectedContent = content;

        ArrayAssert.assertEquals("Wrong wdp", expectedWdp, sms.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", expectedWsp, sms.getWsp());
        ArrayAssert.assertEquals("Wrong content", expectedContent, sms.getContent());
    }

    /**
     * Test of createSMSMessage method (SyncML 1.2 message to a fwm- device)
     */
    public void testCreateSMSMessage__1_2_fwm() {
        byte[] content = new byte[] {
            (byte)0xA1, (byte)0xA2, (byte)0xD7, (byte)0x55, (byte)0xE6, (byte)0x82,
            (byte)0xA3, (byte)0x6B, (byte)0x36, (byte)0xD6, (byte)0xB9, (byte)0xD3,
            (byte)0x13, (byte)0x84, (byte)0x0D, (byte)0xCE, (byte)0x03, (byte)0x18,
            (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08,
            (byte)0x66, (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
            (byte)0x6F, (byte)0x6C, (byte)0x30, (byte)0x60, (byte)0x00, (byte)0x00,
            (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x72,
            (byte)0x64, (byte)0x60, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x04,
            (byte)0x73, (byte)0x63, (byte)0x61, (byte)0x6C, (byte)0x60, (byte)0x00,
            (byte)0x00, (byte)0x00, (byte)0x05, (byte)0x73, (byte)0x74, (byte)0x61,
            (byte)0x73, (byte)0x6B
        };
        Message message = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, content);
        SMSMessageCreator creator = new SMSMessageCreator();
        BinarySMSMessage sms = creator.createSMSMessage(new Sync4jDevice("fwm-1244534"), message);

        byte[] expectedWdp = new byte[] {(byte)0x06, (byte)0x05, (byte)0x04, (byte)0x0B, (byte)0x84, (byte)0xC0, (byte)0x02};
        byte[] expectedWsp = new byte[] {(byte)0x01, (byte)0x06, (byte)0x03, (byte)0xCE, (byte)0xAF, (byte)0x85};
        byte[] expectedContent = content;

        ArrayAssert.assertEquals("Wrong wdp", expectedWdp, sms.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", expectedWsp, sms.getWsp());
        ArrayAssert.assertEquals("Wrong content", expectedContent, sms.getContent());
    }


    /**
     * Test of createSMSMessage method (SyncML 1.2 message to a fjm device)
     */
    public void testCreateSMSMessage__1_1_fjm() {

        //
        // Note that this content is not a real SyncML 1.1 notification message
        //
        byte[] content = new byte[] {
            (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x6A, (byte)0x1D, (byte)0x2D,
            (byte)0x2F, (byte)0x2F, (byte)0x53, (byte)0x59, (byte)0x4E, (byte)0x43,
            (byte)0x4D, (byte)0x4C, (byte)0x2F, (byte)0x2F, (byte)0x44, (byte)0x54,
            (byte)0x44, (byte)0x20, (byte)0x53, (byte)0x79, (byte)0x6E, (byte)0x63,
            (byte)0x4D, (byte)0x4C, (byte)0x20, (byte)0x31, (byte)0x2E, (byte)0x31,
            (byte)0x2F, (byte)0x2F, (byte)0x45, (byte)0x4E, (byte)0x6D, (byte)0x6C,
            (byte)0x71, (byte)0x03, (byte)0x31, (byte)0x2E, (byte)0x31, (byte)0x00,
            (byte)0x01, (byte)0x72, (byte)0x03, (byte)0x53, (byte)0x79, (byte)0x6E,
            (byte)0x63, (byte)0x4D, (byte)0x4C, (byte)0x2F, (byte)0x31, (byte)0x2E,
            (byte)0x31, (byte)0x00, (byte)0x01, (byte)0x65, (byte)0x03, (byte)0x31,
            (byte)0x00, (byte)0x01, (byte)0x5B, (byte)0x03, (byte)0x31, (byte)0x00,
            (byte)0x01, (byte)0x6E, (byte)0x57, (byte)0x03, (byte)0x66, (byte)0x77
        };
        Message message = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, content);
        SMSMessageCreator creator = new SMSMessageCreator();
        BinarySMSMessage sms = creator.createSMSMessage(new Sync4jDevice("fjm-12314"), message);

        byte[] expectedWdp = new byte[] {(byte)0x06, (byte)0x05, (byte)0x04, (byte)0xC3, (byte)0x51, (byte)0xC0, (byte)0x02};
        byte[] expectedWsp = new byte[] {(byte)0x01, (byte)0x06, (byte)0x06, (byte)0x03, (byte)0x02, (byte)0x03, (byte)0x03, (byte)0xAF, (byte)0x85};
        byte[] expectedContent = content;

        ArrayAssert.assertEquals("Wrong wdp", expectedWdp, sms.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", expectedWsp, sms.getWsp());
        ArrayAssert.assertEquals("Wrong content", expectedContent, sms.getContent());
    }

    /**
     * Test of createSMSMessage method (SyncML 1.2 message to a fwm device)
     */
    public void testCreateSMSMessage__1_1_fwm() {

        //
        // Note that this content is not a real SyncML 1.1 notification message
        //
        byte[] content = new byte[] {
            (byte)0x02, (byte)0x00, (byte)0x00, (byte)0x6A, (byte)0x1D, (byte)0x2D,
            (byte)0x2F, (byte)0x2F, (byte)0x53, (byte)0x59, (byte)0x4E, (byte)0x43,
            (byte)0x4D, (byte)0x4C, (byte)0x2F, (byte)0x2F, (byte)0x44, (byte)0x54,
            (byte)0x44, (byte)0x20, (byte)0x53, (byte)0x79, (byte)0x6E, (byte)0x63,
            (byte)0x4D, (byte)0x4C, (byte)0x20, (byte)0x31, (byte)0x2E, (byte)0x31,
            (byte)0x2F, (byte)0x2F, (byte)0x45, (byte)0x4E, (byte)0x6D, (byte)0x6C,
            (byte)0x71, (byte)0x03, (byte)0x31, (byte)0x2E, (byte)0x31, (byte)0x00,
            (byte)0x01, (byte)0x72, (byte)0x03, (byte)0x53, (byte)0x79, (byte)0x6E,
            (byte)0x63, (byte)0x4D, (byte)0x4C, (byte)0x2F, (byte)0x31, (byte)0x2E,
            (byte)0x31, (byte)0x00, (byte)0x01, (byte)0x65, (byte)0x03, (byte)0x31,
            (byte)0x00, (byte)0x01, (byte)0x5B, (byte)0x03, (byte)0x31, (byte)0x00,
            (byte)0x01, (byte)0x6E, (byte)0x57, (byte)0x03, (byte)0x66, (byte)0x77
        };
        Message message = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, content);
        SMSMessageCreator creator = new SMSMessageCreator();
        BinarySMSMessage sms = creator.createSMSMessage(new Sync4jDevice("IMEI:123432"), message);

        byte[] expectedWdp = new byte[] {(byte)0x06, (byte)0x05, (byte)0x04, (byte)0x0B, (byte)0x84, (byte)0xC0, (byte)0x02};
        byte[] expectedWsp = new byte[] {(byte)0x01, (byte)0x06, (byte)0x06, (byte)0x03, (byte)0x02, (byte)0x03, (byte)0x03, (byte)0xAF, (byte)0x85};
        byte[] expectedContent = content;

        ArrayAssert.assertEquals("Wrong wdp", expectedWdp, sms.getWdp());
        ArrayAssert.assertEquals("Wrong wsp", expectedWsp, sms.getWsp());
        ArrayAssert.assertEquals("Wrong content", expectedContent, sms.getContent());
    }
}
