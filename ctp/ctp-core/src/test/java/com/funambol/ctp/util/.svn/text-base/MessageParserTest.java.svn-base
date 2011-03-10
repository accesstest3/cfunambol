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
package com.funambol.ctp.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.*;

import com.funambol.ctp.core.*;
import com.funambol.ctp.core.Error;

/**
 * This class tests the <code>MessageParser</code> class.
 *
 * @version $Id: MessageParserTest.java,v 1.2 2007-11-28 11:26:16 nichele Exp $
 */
public class MessageParserTest extends TestCase {

    public MessageParserTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getMessageFromBytes method when the Message contains an Auth
     * command.
     */
    public void testGetMessageFromBytesAuthCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 0));

            bytesTosend.write((byte)GenericCommandCode.AUTH_CODE);

            bytesTosend.write((byte)ParameterCode.DEVID_CODE);
            bytesTosend.write((byte)"device_id".length());
            bytesTosend.write("device_id".getBytes());
            bytesTosend.write((byte)ParameterCode.USERNAME_CODE);
            bytesTosend.write((byte)"username".length());
            bytesTosend.write("username".getBytes());
            bytesTosend.write((byte)ParameterCode.CRED_CODE);
            bytesTosend.write((byte)"credential".length());
            bytesTosend.write("credential".getBytes());
            bytesTosend.write((byte)ParameterCode.FROM_CODE);
            bytesTosend.write((byte)"from".length());
            bytesTosend.write("from".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.0");
        Auth cmd = new Auth();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setDevid("device_id");
        cmd.setCred("credential");
        cmd.setUsername("username");
        cmd.setFrom("from");
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains a Ready
     * command.
     */
    public void testGetMessageFromBytesReadyCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        bytesTosend.write(calculateProtocolVersion(1, 1));

        bytesTosend.write((byte)GenericCommandCode.READY_CODE);

        Message expResult = new Message();
        expResult.setProtocolVersion("1.1");
        Ready cmd = new Ready();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());

        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains a Bye
     * command.
     */
    public void testGetMessageFromBytesByeCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        bytesTosend.write(calculateProtocolVersion(1, 2));

        bytesTosend.write((byte)GenericCommandCode.BYE_CODE);

        Message expResult = new Message();
        expResult.setProtocolVersion("1.2");
        Bye cmd = new Bye();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());

        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains an Ok
     * command.
     */
    public void testGetMessageFromBytesOkCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 3));

            bytesTosend.write((byte)GenericCommandCode.OK_CODE);

            bytesTosend.write((byte)ParameterCode.NONCE_CODE);
            bytesTosend.write((byte)"nonce".length());
            bytesTosend.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.3");
        Ok cmd = new Ok();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setNonce("nonce".getBytes());
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains a Jump
     * command.
     */
    public void testGetMessageFromBytesJumpCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 4));

            bytesTosend.write((byte)GenericCommandCode.JUMP_CODE);

            bytesTosend.write((byte)ParameterCode.FROM_CODE);
            bytesTosend.write((byte)"from".length());
            bytesTosend.write("from".getBytes());
            bytesTosend.write((byte)ParameterCode.TO_CODE);
            bytesTosend.write((byte)"to".length());
            bytesTosend.write("to".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.4");
        Jump cmd = new Jump();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setFrom("from");
        cmd.setTo("to");
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains an Error
     * command.
     */
    public void testGetMessageFromBytesErrorCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 5));

            bytesTosend.write((byte)GenericCommandCode.ERROR_CODE);

            bytesTosend.write((byte)ParameterCode.DESCRIPTION_CODE);
            bytesTosend.write((byte)"description".length());
            bytesTosend.write("description".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.5");
        Error cmd = new Error();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setDescription("description");
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains an
     * Unauthorized command.
     */
    public void testGetMessageFromBytesUnauthorizedCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 6));

            bytesTosend.write((byte)GenericCommandCode.UNAUTHORIZED_CODE);

            bytesTosend.write((byte)ParameterCode.NONCE_CODE);
            bytesTosend.write((byte)"nonce".length());
            bytesTosend.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.6");
        Unauthorized cmd = new Unauthorized();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setNonce("nonce".getBytes());
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains an
     * NotAuthenticated command.
     */
    public void testGetMessageFromBytesNotAuthenticatedCmd() 
    throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 6));

            bytesTosend.write((byte)GenericCommandCode.NOT_AUTHENTICATED_CODE);

            bytesTosend.write((byte)ParameterCode.NONCE_CODE);
            bytesTosend.write((byte)"nonce".length());
            bytesTosend.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.6");
        NotAuthenticated cmd = new NotAuthenticated();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setNonce("nonce".getBytes());
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains a
     * Forbidden command.
     */
    public void testGetMessageFromBytesForbiddenCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        bytesTosend.write(calculateProtocolVersion(1, 7));

        bytesTosend.write((byte)GenericCommandCode.FORBIDDEN_CODE);

        Message expResult = new Message();
        expResult.setProtocolVersion("1.7");
        Forbidden cmd = new Forbidden();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());

        assertTrue("same object", expResult.deepequals(result));
    }

    /**
     * Test of getMessageFromBytes method when the Message contains a Sync
     * command.
     */
    public void testGetMessageFromBytesSyncCmd() throws MessageException {

        ByteArrayOutputStream bytesTosend = new ByteArrayOutputStream();

        try {

            bytesTosend.write(calculateProtocolVersion(1, 8));

            bytesTosend.write((byte)GenericCommandCode.SYNC_CODE);

            bytesTosend.write((byte)ParameterCode.SAN_CODE);
            bytesTosend.write((byte)"san".length());
            bytesTosend.write("san".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Message expResult = new Message();
        expResult.setProtocolVersion("1.8");
        Sync cmd = new Sync();
        expResult.setCommand(cmd);

        Message result = MessageParser.getMessageFromBytes(bytesTosend.toByteArray());
        assertFalse("different object", expResult.deepequals(result));

        cmd.setNotificationMessage("san".getBytes());
        expResult.setCommand(cmd);
        assertTrue("same object", expResult.deepequals(result));
    }

    // --------------------------------------------------------- Private methods
    /**
     * Calculate protocol version with major and minor numbers.
     *
     * @param major the major version number
     * @param minor the minor version number
     * @return the byte of the protocol version
     */
    private byte calculateProtocolVersion(int major, int minor) {
        byte bMajor = (byte)((major << 4) & 0xf0);
        byte bMinor = (byte)(minor & 0x0f);

        return (byte)(bMajor | bMinor);
    }
}
