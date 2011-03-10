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

import com.funambol.ctp.core.*;
import com.funambol.ctp.core.Error;

/**
 * Converts Message object into byte array.
 *
 * @version $Id: MessageConverter.java,v 1.2 2007-11-28 11:26:15 nichele Exp $
 */
public class MessageConverter {

    // ------------------------------------------------------------ Constructors
    /** Creates a new instance of MessageConverter */
    private MessageConverter() {
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Converts the input Message object in byte array.
     *
     * @param message the Message object to convert
     * @return the message in byte array
     */
    public static byte[] getBytesFromMessage(Message message)
    throws MessageException {

        ByteArrayOutputStream msgBytes = new ByteArrayOutputStream();

        try {
            //
            // Reads and converts the protocol version
            //
            String protocolVersion = message.getProtocolVersion();
            byte version = convertProtocolVersionToByte(protocolVersion);
            msgBytes.write(version);

            GenericCommand genericCmd = message.getCommand();

            int cmdCode = -1;

            //
            // Detects and converts the command and, eventually, its params
            //
            if (genericCmd instanceof Command) {

                if (genericCmd instanceof Auth) {
                    Auth cmd = (Auth)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleAuthParams(msgBytes, cmd);

                } else if (genericCmd instanceof Ready) {
                    Ready cmd = (Ready)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                } else if (genericCmd instanceof Bye) {
                    Bye cmd = (Bye)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);
                }

            } else if (genericCmd instanceof Status) {

                if (genericCmd instanceof Ok) {
                    Ok cmd = (Ok)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleOkParams(msgBytes, cmd);

                } else if (genericCmd instanceof Jump) {
                    Jump cmd = (Jump)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleJumpParams(msgBytes, cmd);

                } else if (genericCmd instanceof Error) {
                    Error cmd = (Error)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleErrorParams(msgBytes, cmd);

                } else if (genericCmd instanceof Unauthorized) {
                    Unauthorized cmd = (Unauthorized)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleUnauthorizedParams(msgBytes, cmd);

                } else if (genericCmd instanceof NotAuthenticated) {
                    NotAuthenticated cmd = (NotAuthenticated)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleNotAuthenticatedParams(msgBytes, cmd);

                } else if (genericCmd instanceof Forbidden) {
                    Forbidden cmd = (Forbidden)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                } else if (genericCmd instanceof Sync) {
                    Sync cmd = (Sync)genericCmd;
                    cmdCode =
                        GenericCommandCode.getCodeFromClass(cmd.getClass());

                    msgBytes.write((byte)cmdCode);

                    handleSyncParams(msgBytes, cmd);

                }
            }

        } catch (Exception e) {
            throw new MessageException("Error converting Message in a byte array",
                                       e.getCause());
        }

        return msgBytes.toByteArray();
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Convert protocol version into byte.
     *
     * @param pv the protocol version
     * @return the protocol version
     */
    private static byte convertProtocolVersionToByte(String pv) {

        if (!pv.contains(".")) {
            return -1;
        }

        String[] numver = pv.split("\\.");
        String major = numver[0];
        String minor = numver[1];

        if (!hasInteger(major)) {
            return -1;
        }

        if (!hasInteger(minor)) {
            return -1;
        }

        byte bMajor  = (byte)((Integer.parseInt(major) << 4) & 0xf0);
        byte bMinor  = (byte)(Integer.parseInt(minor) & 0x0f);
        byte version = (byte)(bMajor | bMinor);

        return version;
    }

    /**
     * Is input an integer?
     *
     * @param the input to check
     * @return true if the input is a integer, false otherwise
     */
    private static boolean hasInteger(String input) {
        for (int j = 0;j < input.length();j++) {
           if (!Character.isDigit(input.charAt(j))) {
               return false;
           }
        }
        return true;
    }

    /**
     * Handles parameters of Auth command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Auth command
     */
    private static void handleAuthParams(ByteArrayOutputStream msgBytes,
                                         Auth                  cmd     )
    throws MessageException {

        try {

            String devid    = cmd.getDevid()   ;
            String username = cmd.getUsername();
            String cred     = cmd.getCred()    ;
            String from     = cmd.getFrom()    ;

            if (devid != null) {
                msgBytes.write((byte)ParameterCode.DEVID_CODE);
                msgBytes.write((byte)devid.getBytes().length);
                msgBytes.write(devid.getBytes());
            }

            if (username != null) {
                msgBytes.write((byte)ParameterCode.USERNAME_CODE);
                msgBytes.write((byte)username.getBytes().length);
                msgBytes.write(username.getBytes());
            }

            if (cred != null) {
                msgBytes.write((byte)ParameterCode.CRED_CODE);
                msgBytes.write((byte)cred.getBytes().length);
                msgBytes.write(cred.getBytes());
            }

            if (from != null) {
                msgBytes.write((byte)ParameterCode.FROM_CODE);
                msgBytes.write((byte)from.getBytes().length);
                msgBytes.write(from.getBytes());
            }
        } catch (IOException e) {
            throw new MessageException("Error handling Auth command params",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of Ok command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Ok command
     */
    private static void handleOkParams(ByteArrayOutputStream msgBytes,
                                       Ok                    cmd     )
    throws MessageException {

        try {

            byte[] nonce = cmd.getNonce();

            if (nonce != null) {
                msgBytes.write((byte)ParameterCode.NONCE_CODE);
                msgBytes.write(nonce.length);
                msgBytes.write(nonce);
            }

        } catch (IOException e) {
            throw new MessageException("Error handling Ok command param",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of Jump command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Jump command
     */
    private static void handleJumpParams(ByteArrayOutputStream msgBytes,
                                         Jump                  cmd     )
    throws MessageException {

        try {

            String from = cmd.getFrom();
            String to   = cmd.getTo()  ;

            if (from != null) {
                msgBytes.write((byte)ParameterCode.FROM_CODE);
                msgBytes.write((byte)from.getBytes().length);
                msgBytes.write(from.getBytes());
            }

            if (to != null) {
                msgBytes.write((byte)ParameterCode.TO_CODE);
                msgBytes.write((byte)to.getBytes().length);
                msgBytes.write(to.getBytes());
            }

        } catch (IOException e) {
            throw new MessageException("Error handling Jump command params",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of Error command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Error command
     */
    private static void handleErrorParams(ByteArrayOutputStream msgBytes,
                                          Error                 cmd     )
    throws MessageException {

        try {

            String description = cmd.getDescription();

            if (description != null) {
                msgBytes.write((byte)ParameterCode.DESCRIPTION_CODE);
                msgBytes.write((byte)description.getBytes().length);
                msgBytes.write(description.getBytes());
            }

        } catch (IOException e) {
            throw new MessageException("Error handling Error command param",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of Unauthorized command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Unauthorized command
     */
    private static void handleUnauthorizedParams(ByteArrayOutputStream msgBytes,
                                                 Unauthorized          cmd   )
    throws MessageException {

        try {

            byte[] nonce = cmd.getNonce();

            if (nonce != null) {
                msgBytes.write((byte)ParameterCode.NONCE_CODE);
                msgBytes.write(nonce.length);
                msgBytes.write(nonce);
            }

        } catch (IOException e) {
            throw new MessageException("Error handling Unauthorized command param",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of NotAuthenticated command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the NotAuthenticated command
     */
    private static void handleNotAuthenticatedParams(ByteArrayOutputStream msgBytes,
                                                     NotAuthenticated      cmd   )
    throws MessageException {

        try {

            byte[] nonce = cmd.getNonce();

            if (nonce != null) {
                msgBytes.write((byte)ParameterCode.NONCE_CODE);
                msgBytes.write(nonce.length);
                msgBytes.write(nonce);
            }

        } catch (IOException e) {
            throw new MessageException("Error handling NotAuthenticated command param",
                                       e.getCause());
        }
    }

    /**
     * Handles parameters of Sync command.
     *
     * @param msgBytes the byte array at which adding the new information
     * @param cmd the Sync command
     */
    private static void handleSyncParams(ByteArrayOutputStream msgBytes,
                                         Sync                  cmd     )
    throws MessageException {

        try {

            byte[] san = cmd.getNotificationMessage();

            if (san != null) {
                msgBytes.write((byte)ParameterCode.SAN_CODE);
                msgBytes.write(san.length);
                msgBytes.write(san);
            }

        } catch (IOException e) {
            throw new MessageException("Error handling Sync command param",
                                       e.getCause());
        }
    }
}
