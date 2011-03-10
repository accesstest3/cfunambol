/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.sender.Sender;
import com.funambol.framework.server.*;
import com.funambol.framework.tools.DbgTools;

/**
 * This class is an implementation of Sender interface. It sends the
 * notification message over WAP
 *
 * @version $Id: WAPSender.java,v 1.1.1.1 2008-02-21 23:35:52 stefano_fornari Exp $
 */
public abstract class WAPSender implements Sender {

    // ------------------------------------------------------------ Private data
    private FunambolLogger log = null;

    // ------------------------------------------------------------- Constructor
    public WAPSender() {
        log = FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sends the notification message to a device.
     * Implementation should provide a way for the message to be delivered
     *
     * @param device the recipient of the message
     * @param message notification message with wap header that has to be sent
     * @throws NotificationException
     */
    public abstract void sendMessage(String deviceAddress, byte[] message)
    throws NotificationException;

    /**
     * Prepares the notification message (adds the wap header) and calls
     * abstract method sendMessage() to send the message
     *
     * @param deviceAddress address of the device where message should be sent
     * @param message       the notification message that has to be sent
     */
    public void sendNotificationMessage(Sync4jDevice device, Message message)
    throws NotificationException {

        if (message == null) {
            throw new NotificationException("Unable to send a null message");
        }
        if (message.getMessageContent() == null) {
            throw new NotificationException("The message content is null...unable to send it");
        }

        String deviceAddress = device.getMsisdn();

        if (log.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("Sending message '");
            sb.append(DbgTools.bytesToHex(message.getMessageContent()));
            sb.append("' to '").append(deviceAddress).append("'");
            log.info(sb.toString());
        }

        // Uncomment if yuo want to save the message to send
        //writeByte(message, phoneNumber);

        byte[] contentType = null;
        if (message.getMessageType() == Message.Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE) {
            contentType = new byte[] {WSPHeader.CONTENT_TYPE_CODE_NOTIFICATION};
        } else if (message.getMessageType() == Message.Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE) {
            //
            // 0x03: Content type length
            // 0x02: Short-length
            // 0303: Content-Type 
            //       (see http://www.openmobilealliance.org/tech/omna/omna-wsp-content-type.htm)
            contentType = new byte[] {0x03, 0x02, 0x03, 0x03};
        }
        
        byte[] udh = createUserDataHeader();
        byte[] bMessage = null;

        //add header to message
        bMessage = addWapHeader(udh, message.getMessageContent());

        // here we have to call an abstract methods
        sendMessage(deviceAddress, bMessage);

        if (log.isInfoEnabled()) {
            log.info("Message to '" + deviceAddress + "' sent");
        }
    }

    /**
     * Creates the user data header
     *
     * @return byte[] the user data header
     */
    private byte[] createUserDataHeader() {
        WDPHeader wdpHeader = new WDPHeader();
        WSPHeader wspHeader = null;

        wspHeader = new WSPHeader(WSPHeader.CONTENT_TYPE_CODE_NOTIFICATION);

        byte[] wdpHeaderContent = wdpHeader.getHeader();
        byte[] wspHeaderContent = wspHeader.getHeader();

        byte[] udh = new byte[wdpHeaderContent.length + wspHeaderContent.length];

        System.arraycopy(wdpHeaderContent, 0, udh, 0, wdpHeaderContent.length);
        System.arraycopy(wspHeaderContent, 0, udh, wdpHeaderContent.length, wspHeaderContent.length);

        return udh;
    }

    /**
     * Adds (appends) the wap header to the message
     *
     * @param wapHeader array of bytes containing the wap header to be added to
     *                  the notification message
     * @param message   array of bytes contaiing
     * @return byte[]   array of bytes with the wap header added to the
     *                  notification message
     */
    private byte[] addWapHeader(byte[] wapHeader, byte[] message) {

        int lengthCompleteMessage = wapHeader.length + message.length;
        byte[] completeMessage = new byte[lengthCompleteMessage];

        System.arraycopy(wapHeader, 0, completeMessage, 0, wapHeader.length);
        System.arraycopy(message, 0, completeMessage, wapHeader.length, message.length);

        return completeMessage;
    }

}
