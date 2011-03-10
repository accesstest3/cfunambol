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

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import com.funambol.ctp.core.*;
import com.funambol.ctp.core.Error;

/**
 * Parses byte array in order to create a Message object.
 *
 * @version $Id: MessageParser.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class MessageParser {

    // ------------------------------------------------------------ Private data
    private static char hexDigit[] = {'0', '1', '2', '3', '4', '5', '6', '7',
                                      '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of MessageParser */
    private MessageParser() {
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Converts the byte array in a Message object.
     *
     * @param msgToParse the byte array to parse and convert
     * @return the Message object
     */
    public static Message getMessageFromBytes(byte[] msgToParse)
    throws MessageException {

        Message msg = new Message();

        DataInputStream input =
            new DataInputStream(new ByteArrayInputStream(msgToParse));

        try {
            byte[] b = new byte[msgToParse.length];
            input.read(b);

            //
            // Reads protocol version
            //
            String pv = getProtocolVersion(b[0]);
            msg.setProtocolVersion(pv);

            //
            // Detects and converts the command and, eventually, its params
            //
            Class classObj = GenericCommandCode.getClassFromCode(b[1]);

            if (classObj.newInstance() instanceof Auth) {
                Auth cmd = (Auth)classObj.newInstance();

                handleAuthParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Ready) {
                Ready cmd = (Ready)classObj.newInstance();

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Bye) {
                Bye cmd = (Bye)classObj.newInstance();

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Ok) {
                Ok cmd = (Ok)classObj.newInstance();

                handleOkParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Jump) {
                Jump cmd = (Jump)classObj.newInstance();

                handleJumpParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Error) {
                Error cmd = (Error)classObj.newInstance();

                handleErrorParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Unauthorized) {
                Unauthorized cmd = (Unauthorized)classObj.newInstance();

                handleUnauthorizedParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof NotAuthenticated) {
                NotAuthenticated cmd = (NotAuthenticated)classObj.newInstance();

                handleNotAuthenticatedParams(msgToParse, cmd);

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Forbidden) {
                Forbidden cmd = (Forbidden)classObj.newInstance();

                msg.setCommand(cmd);

            } else if (classObj.newInstance() instanceof Sync) {
                Sync cmd = (Sync)classObj.newInstance();

                handleSyncParams(msgToParse, cmd);

                msg.setCommand(cmd);
            }
        } catch (Exception e) {
            throw new MessageException("Error parsing byte array in order to create Message",
                                       e.getCause());
        }
        return msg;
    }

    // --------------------------------------------------------- Private methods
    /**
     * Returns the protocol version.
     *
     * @param b the byte of the protocol version
     * @return the string of protocol version
     */
    private static String getProtocolVersion(byte b) {

        char major = hexDigit[(b >> 4) & 0x0f];
        char minor = hexDigit[b & 0x0f];

        StringBuilder pv = new StringBuilder();
        pv.append(Integer.valueOf("" + major, 16))
          .append('.')
          .append(Integer.valueOf("" + minor, 16));

        return pv.toString();
    }

    /**
     * Handles parameters of Auth command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Auth command
     */
    private static void handleAuthParams(byte[] msgToParse, Auth cmd) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = (int)msgToParse[++ind];
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_DEVID.equals(paramName)) {
                cmd.setDevid(new String(paramValue));
            } else if (cmd.PARAM_USERNAME.equals(paramName)) {
                cmd.setUsername(new String(paramValue));
            } else if (cmd.PARAM_CRED.equals(paramName)) {
                cmd.setCred(new String(paramValue));
            } else if (cmd.PARAM_FROM.equals(paramName)) {
                cmd.setFrom(new String(paramValue));
            }
        }
    }

    /**
     * Handles parameters of Ok command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Ok command
     */
    private static void handleOkParams(byte[] msgToParse, Ok cmd) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_NONCE.equals(paramName)) {
                cmd.setNonce(paramValue);
            }
        }
    }

    /**
     * Handles parameters of Jump command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Jump command
     */
    private static void handleJumpParams(byte[] msgToParse, Jump cmd) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_FROM.equals(paramName)) {
                cmd.setFrom(new String(paramValue));
            } else if (cmd.PARAM_TO.equals(paramName)) {
                cmd.setTo(new String(paramValue));
            }
        }
    }

    /**
     * Handles parameters of Error command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Error command
     */
    private static void handleErrorParams(byte[] msgToParse, Error cmd) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_DESCRIPTION.equals(paramName)) {
                cmd.setDescription(new String(paramValue));
            }
        }
    }

    /**
     * Handles parameters of Unauthorized command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Unauthorized command
     */
    private static void handleUnauthorizedParams(byte[]       msgToParse,
                                                 Unauthorized cmd       ) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_NONCE.equals(paramName)) {
                cmd.setNonce(paramValue);
            }
        }
    }

    /**
     * Handles parameters of NotAuthenticated command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the NotAuthenticated command
     */
    private static void handleNotAuthenticatedParams(byte[]       msgToParse,
                                                     NotAuthenticated cmd       ) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_NONCE.equals(paramName)) {
                cmd.setNonce(paramValue);
            }
        }
    }

    /**
     * Handles parameters of Sync command.
     * The byte array has in sequence:
     * - the parameter code
     * - the parameter length
     * - the parameter value
     *
     * @param msgToParse the byte array to read
     * @param cmd the Sync command
     */
    private static void handleSyncParams(byte[] msgToParse, Sync cmd) {

        int ind = 2;
        while (msgToParse.length > ind) {
            byte paramCode    = msgToParse[ind];
            String paramName  = ParameterCode.getNameFromCode(paramCode);
            int paramLength   = getParamLength(msgToParse[++ind]);
            byte[] paramValue = readParamValue(paramLength, msgToParse, ++ind);
            ind += paramLength;

            if (cmd.PARAM_SAN.equals(paramName)) {
                cmd.setNotificationMessage(paramValue);
            }
        }
    }

    /**
     * Returns parameter length.
     *
     * @param b the byte to read
     * @return the parameter lenght
     */
    private static int getParamLength(byte b) {
        return b;
    }

    /**
     * Reads the parameter value.
     *
     * @param paramLength the length of the parameter
     * @param msgToParse the byte array to read
     * @param ind an index start reading in the byte array
     * @return the parameter value as byte array
     */
    private static byte[] readParamValue(int    paramLength,
                                         byte[] msgToParse ,
                                         int    ind        ) {
        byte[] b = new byte[paramLength];
        int i = 0;
        while (i <= paramLength - 1) {
            b[i] = msgToParse[ind + i];
            i++;
        }
        return b;
    }
}
