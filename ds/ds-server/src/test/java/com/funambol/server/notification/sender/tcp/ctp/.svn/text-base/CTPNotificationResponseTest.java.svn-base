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

/**
 *
 * @version $Id: CTPNotificationResponseTest.java,v 1.2 2008-02-23 22:52:42 stefano_fornari Exp $
 */
public class CTPNotificationResponseTest extends TestCase {

    private static final String DELIVERED_BY_1 = "address";
    private static final String DELIVERED_BY_2 = "192.168.0.15";

    public CTPNotificationResponseTest(String testName) {
        super(testName);
    }

    public void testGetSet() throws Exception {
        CTPNotificationResponse resp = new CTPNotificationResponse(DELIVERED_BY_1);
        assertEquals(CTPNotificationResponseTest.DELIVERED_BY_1, resp.getDeliveredBy());
        resp.setDeliveredBy(DELIVERED_BY_2);
        assertEquals(CTPNotificationResponseTest.DELIVERED_BY_2, resp.getDeliveredBy());
    }

    public void testSerialization_1() throws Exception {
        ObjectInputStream in = null;
        String fileName = "src/test/data/ctp-notification/ctpnotificationresponse_1.ser";
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));

            CTPNotificationResponse not1 = (CTPNotificationResponse) in.readObject();

            CTPNotificationResponse not2 = new CTPNotificationResponse(DELIVERED_BY_1);

            assertEquals(not1.getDeliveredBy(), not2.getDeliveredBy());

        } finally {
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
        String fileName = "src/test/data/ctp-notification/ctpnotificationresponse_2.ser";
        try {
            in = new ObjectInputStream(new FileInputStream(fileName));

            CTPNotificationResponse not1 = (CTPNotificationResponse) in.readObject();

            CTPNotificationResponse not2 = new CTPNotificationResponse(null);
            CTPNotificationResponse not3 = new CTPNotificationResponse();

            assertEquals(not1.getDeliveredBy(), not2.getDeliveredBy());
            assertEquals(not1.getDeliveredBy(), not3.getDeliveredBy());

        } finally {
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
    private void createCTPNotificationResponse_ser_1() throws Exception  {
        CTPNotificationResponse resp = new CTPNotificationResponse(DELIVERED_BY_1);
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("ctpnotificationresponse_1.ser"));
        out.writeObject(resp);
        out.close();
    }

    /**
     * Used offline to create the serialization file for testSerialization_2
     */
    private void createCTPNotificationResponse_ser_2() throws Exception  {
        CTPNotificationResponse resp = new CTPNotificationResponse(null);
        ObjectOutput out = new ObjectOutputStream(
            new FileOutputStream("ctpnotificationresponse_2.ser"));
        out.writeObject(resp);
        out.close();
    }
}
