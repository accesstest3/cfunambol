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

package com.funambol.server.notification.sender.tcp.ctp;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 *
 * @version $Id: CTPNotificationTest.java,v 1.2 2008-02-23 22:52:42 stefano_fornari Exp $
 */
public class CTPNotificationTest extends TestCase {

    private static final String DEVICE_ID_1 = "fwm-123456789012345";
    private static final String DEVICE_ID_2 = "fwm-device2";

    private static final  byte[] FAKE_MESSAGE_1 = new byte[] {
                (byte)0xF6, (byte)0xE2, (byte)0xD3, (byte)0x51, (byte)0xE1,
                (byte)0xDC, (byte)0x51, (byte)0xA5, (byte)0x86, (byte)0x96,
                (byte)0xE3, (byte)0xC8, (byte)0x06, (byte)0x53, (byte)0x60,
                (byte)0xCF, (byte)0x03, (byte)0x08, (byte)0x00, (byte)0x00,
                (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x08, (byte)0x66,
                (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
                (byte)0x6F, (byte)0x6C, (byte)0x00
            };

    private static final  byte[] FAKE_MESSAGE_2 = new byte[] {
                (byte)0xDC, (byte)0x51, (byte)0xA5, (byte)0x86, (byte)0x96,
                (byte)0xCF, (byte)0x03, (byte)0x08, (byte)0x00, (byte)0x00,
                (byte)0x75, (byte)0x6E, (byte)0x61, (byte)0x6D, (byte)0x62,
            };

    public CTPNotificationTest(String testName) {
        super(testName);
    }

    public void testGetDeviceId() {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);
        assertEquals(DEVICE_ID_1, not.getDeviceId());
    }

    public void testSetDeviceId() {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);
        not.setDeviceId(DEVICE_ID_2);
        assertEquals(DEVICE_ID_2, not.getDeviceId());
    }

    public void testGetNotificationMessage() {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);
        assertEquals(FAKE_MESSAGE_1, not.getNotificationMessage());
    }

    public void testSetNotificationMessage() {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);
        not.setNotificationMessage(FAKE_MESSAGE_2);
        assertEquals(FAKE_MESSAGE_2, not.getNotificationMessage());
    }

    public void testSerialization_1() throws Exception {

        ObjectInputStream in = null;
        String fileName = "src/test/data/ctp-notification/ctpnotification_1.ser";
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));

            CTPNotification not1 = (CTPNotification) in.readObject();

            CTPNotification not2 = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);

            assertEquals(not1.getDeviceId(), not2.getDeviceId());
            ArrayAssert.assertEquals(not1.getNotificationMessage(), not2.getNotificationMessage());

        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    public void testSerialization_2() throws Exception {

        ObjectInputStream in = null;
        String fileName = "src/test/data/ctp-notification/ctpnotification_2.ser";
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));

            CTPNotification not1 = (CTPNotification) in.readObject();

            CTPNotification not2 = new CTPNotification(null, FAKE_MESSAGE_1);

            assertEquals(not1.getDeviceId(), not2.getDeviceId());
            ArrayAssert.assertEquals(not1.getNotificationMessage(), not2.getNotificationMessage());

        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    public void testSerialization_3() throws Exception {

        ObjectInputStream in = null;
        String fileName = "src/test/data/ctp-notification/ctpnotification_3.ser";
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));

            CTPNotification not1 = (CTPNotification) in.readObject();

            CTPNotification not2 = new CTPNotification(DEVICE_ID_1, null);

            assertEquals(not1.getDeviceId(), not2.getDeviceId());
            ArrayAssert.assertEquals(not1.getNotificationMessage(), not2.getNotificationMessage());

        }finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {

            }
        }
    }

    /**
     * Used offline to create the serialization file for testSerialization_1
     */
    private void createCTPNotification_ser_1() throws Exception  {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, FAKE_MESSAGE_1);
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("ctpnotification_1.ser"));
        out.writeObject(not);
        out.close();
    }

    /**
     * Used offline to create the serialization file for testSerialization_2
     */
    private void createCTPNotification_ser_2() throws Exception  {
        CTPNotification not = new CTPNotification(null, FAKE_MESSAGE_1);
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("ctpnotification_2.ser"));
        out.writeObject(not);
        out.close();
    }

    /**
     * Used offline to create the serialization file for testSerialization_3
     */
    private  void createCTPNotification_ser_3() throws Exception  {
        CTPNotification not = new CTPNotification(DEVICE_ID_1, null);
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("ctpnotification_3.ser"));
        out.writeObject(not);
        out.close();
    }
}
