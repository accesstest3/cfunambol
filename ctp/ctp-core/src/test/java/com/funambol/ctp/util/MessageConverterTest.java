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

import junitx.framework.ArrayAssert;

import com.funambol.ctp.core.*;
import com.funambol.ctp.core.Error;

/**
 * This class tests the <code>MessageConverter</code> class.
 *
 * @version $Id: MessageConverterTest.java,v 1.2 2007-11-28 11:26:16 nichele Exp $
 */
public class MessageConverterTest extends TestCase {

    Message message = new Message();

    public MessageConverterTest(String testName) {
        super(testName);
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getBytes method when the byte array contains an Auth command.
     */
    public void testGetBytesAuthCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 0));
            expres.write((byte)GenericCommandCode.AUTH_CODE);
            expres.write((byte)ParameterCode.DEVID_CODE);
            expres.write((byte)"device_id".length());
            expres.write("device_id".getBytes());
            expres.write((byte)ParameterCode.USERNAME_CODE);
            expres.write((byte)"username".length());
            expres.write("username".getBytes());
            expres.write((byte)ParameterCode.CRED_CODE);
            expres.write((byte)"credential".length());
            expres.write("credential".getBytes());
            expres.write((byte)ParameterCode.FROM_CODE);
            expres.write((byte)"from".length());
            expres.write("from".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.0");
        message.setCommand(
            new Auth("device_id", "username", "credential", "from"));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a Ready command.
     */
    public void testGetBytesReadyCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        expres.write(calculateProtocolVersion(1, 1));

        expres.write((byte)GenericCommandCode.READY_CODE);

        message.setProtocolVersion("1.1");
        message.setCommand(new Ready());

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a Bye command.
     */
    public void testGetBytesByeCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        expres.write(calculateProtocolVersion(1, 2));

        expres.write((byte)GenericCommandCode.BYE_CODE);

        message.setProtocolVersion("1.2");
        message.setCommand(new Bye());

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains an Ok command.
     */
    public void testGetBytesOkCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 3));
            expres.write((byte)GenericCommandCode.OK_CODE);
            expres.write((byte)ParameterCode.NONCE_CODE);
            expres.write((byte)"nonce".length());
            expres.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.3");
        message.setCommand(new Ok("nonce".getBytes()));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a Jump command.
     */
    public void testGetBytesJumpCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 4));
            expres.write((byte)GenericCommandCode.JUMP_CODE);
            expres.write((byte)ParameterCode.FROM_CODE);
            expres.write((byte)"from".length());
            expres.write("from".getBytes());
            expres.write((byte)ParameterCode.TO_CODE);
            expres.write((byte)"to".length());
            expres.write("to".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.4");
        message.setCommand(new Jump("from", "to"));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains an Error command.
     */
    public void testGetBytesErrorCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 5));
            expres.write((byte)GenericCommandCode.ERROR_CODE);
            expres.write((byte)ParameterCode.DESCRIPTION_CODE);
            expres.write((byte)"description".length());
            expres.write("description".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.5");
        message.setCommand(new Error("description"));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains an Unauthorized
     * command.
     */
    public void testGetBytesUnauthorizedCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 6));
            expres.write((byte)GenericCommandCode.UNAUTHORIZED_CODE);
            expres.write((byte)ParameterCode.NONCE_CODE);
            expres.write((byte)"nonce".length());
            expres.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.6");
        message.setCommand(new Unauthorized("nonce".getBytes()));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a NotAuthenticated
     * command.
     */
    public void testGetBytesNotAuthenticatedCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 6));
            expres.write((byte)GenericCommandCode.NOT_AUTHENTICATED_CODE);
            expres.write((byte)ParameterCode.NONCE_CODE);
            expres.write((byte)"nonce".length());
            expres.write("nonce".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.6");
        message.setCommand(new NotAuthenticated("nonce".getBytes()));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a Forbidden command.
     */
    public void testGetBytesForbiddenCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        expres.write(calculateProtocolVersion(1, 7));

        expres.write((byte)GenericCommandCode.FORBIDDEN_CODE);

        message.setProtocolVersion("1.7");
        message.setCommand(new Forbidden());

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
    }

    /**
     * Test of getBytes method when the byte array contains a Sync command.
     */
    public void testGetBytesSyncCmd() throws MessageException {

        ByteArrayOutputStream expres = new ByteArrayOutputStream();

        try {

            expres.write(calculateProtocolVersion(1, 8));
            expres.write((byte)GenericCommandCode.SYNC_CODE);
            expres.write((byte)ParameterCode.SAN_CODE);
            expres.write((byte)"san".length());
            expres.write("san".getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        }

        message.setProtocolVersion("1.8");
        message.setCommand(new Sync("san".getBytes()));

        byte[] expResult = expres.toByteArray();
        byte[] result    = MessageConverter.getBytesFromMessage(message);

        ArrayAssert.assertEquals(expResult, result);
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