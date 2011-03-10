/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.tools.DbgTools;
import com.funambol.framework.tools.IOTools;

/**
 * This is a simple implementation that just writes the message in a file and/or
 * on the output console
 * @version $Id: SimpleWAPSenderImpl.java,v 1.1.1.1 2008-02-21 23:35:52 stefano_fornari Exp $
 */
public class SimpleWAPSenderImpl extends WAPSender {

    // ------------------------------------------------------------ Private data
    /**
     * The logger
     */
    private FunambolLogger log =
            FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);

    // -------------------------------------------------------------- Properties
    /**
     * Should the messages be stored on files ?
     */
    private boolean logOnFiles    = false;

    public boolean isLogOnFiles() {
        return logOnFiles;
    }

    public void setLogOnFiles(boolean logOnFiles) {
        this.logOnFiles = logOnFiles;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Sends the notification message to a device.
     * @param deviceAddress address of the device where message should be sent
     * @param message notification message with wap header that has to be sent
     * @throws NotificationException
     */
    @Override
    public void sendMessage(String deviceAddress, byte[] message)
    throws NotificationException {

        if (log.isInfoEnabled()) {
            StringBuilder sb = new StringBuilder("Sending message '");
            sb.append(DbgTools.bytesToHex(message));
            sb.append("' to '").append(deviceAddress).append("'");

            log.info(sb.toString());
        }

        if (logOnFiles) {
            writeMessageOnFile(deviceAddress, message);
        }
    }

    // --------------------------------------------------------- Private methods
    /**
     * Writes an array of bytes into a file
     * @param deviceAddress the device address
     * @param message bytes to be written in file
     */
    private void writeMessageOnFile(String deviceAddress, byte[] message) {
        if (deviceAddress == null || deviceAddress.length() == 0) {
            deviceAddress = "Unknown";
        }
        try {
            IOTools.writeFile(message, deviceAddress + "_" + System.currentTimeMillis() + ".message");
        } catch (Exception ex) {
            // ignore this exception
        }
    }

}
