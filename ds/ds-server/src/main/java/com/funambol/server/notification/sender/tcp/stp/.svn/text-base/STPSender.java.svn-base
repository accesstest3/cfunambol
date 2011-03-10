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
package com.funambol.server.notification.sender.tcp.stp;

import com.funambol.server.config.Configuration;
import java.io.*;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.sender.Sender;

import com.funambol.framework.server.Sync4jDevice;


/**
 * This class is an implementation of Sender interface. It sends the
 * notification message over TCP/IP.
 *
 * @see Sender
 *
 * @version  $Id: STPSender.java,v 1.1.1.1 2008-02-21 23:35:53 stefano_fornari Exp $
 */
public class STPSender implements Sender {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data
    /**
     * The logger
     */
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger("funambol.server.notification.stp-sender");

    // -------------------------------------------------------------- Properties

    /** Default port used if no port is provided */
    private int  defaultPort = 745;

    public int getDefaultPort() {
        return this.defaultPort;
    }

    public void setDefaultPort(int port) {
        this.defaultPort = port;
    }

    /** Default connection timeout in seconds*/
    private int timeout = 60 * 1000;  // 1 minute

    public int getTimeout() {
        return this.timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    /** Number of attempts to perform */
    private int attempts = 0;

    public int getAttempts() {
        return this.attempts;
    }

    public void setAttempts(int attempts) {
        if (attempts == 0) {
            this.attempts = 1;
        } else {
            this.attempts = attempts;
        }
    }

    /** Time to wait between attempts in seconds */
    private int waitingTime = 0;

    public int getWaitingTime() {
        return this.waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }


    // ------------------------------------------------------------- Constructor
    public STPSender() {
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sends the given message to the device with the given phone number.
     *
     * @param device the recipient of the message
     * @param message the message to be sent
     *
     * @throws NotificationException
     */
    public void sendNotificationMessage(Sync4jDevice device, Message message)
    throws NotificationException {

        if (message == null) {
            throw new NotificationException("Unable to send a null message");
        }
        if (message.getMessageContent() == null) {
            throw new NotificationException("The message content is null...unable to send it");
        }

        if (log.isTraceEnabled()) {
            log.trace("Attempts: "      + attempts +
                      ", timeout: "     + timeout +
                      ", waitingTime: " + waitingTime);
        }

        Socket           sock    = null;
        DataOutputStream out     = null;
        BufferedReader   in      = null;
        String           address = null;
        int              port    = defaultPort;

        for (int i=1; i<=attempts; i++) {
            try {

                //
                // After the first failed tentative in sending the notification, 
                // the device is read from database because is it possible that
                // its address is changed.
                //
                boolean deviceFound = false;
                if (i>1) {
                    deviceFound =
                        Configuration.getConfiguration().getDeviceInventory().getDevice(device, false);
                    if (!deviceFound || device == null) {
                        if (log.isTraceEnabled()) {
                            log.trace("Device not found");
                        }
                    }
                }

                String deviceAddress= device.getAddress();

                if (deviceAddress == null || deviceAddress.equals("")) {
                    if (log.isInfoEnabled()) {
                        log.info("Address not configured for device '" +
                                 device.getDeviceId() + "'");
                    }
                    return;
                }

                //try to get the connection port from address
                if (deviceAddress.indexOf(':') == -1) {
                    //use default Funambol port
                    address = deviceAddress;
                    port = defaultPort;
                } else {
                    //get port from address
                    address = deviceAddress.substring(0, deviceAddress.indexOf(':'));
                    port = Integer.parseInt(
                               deviceAddress.substring(deviceAddress.indexOf(':') + 1)
                           );
                }

                if (log.isInfoEnabled()) {
                    log.info("Try to notify '" + address + "' on port '" +
                             port + "'");
                }

                if (log.isTraceEnabled()) {
                    log.trace("Attempt n. " + i);
                }

                //create a socket and connect to device
                sock = new Socket();
                SocketAddress sockAddress = new InetSocketAddress(address, port);

                sock.setSoTimeout(timeout * 1000);
                sock.connect(sockAddress, timeout * 1000);

                out = new DataOutputStream(sock.getOutputStream());

                //send message through socket
                out.write(message.getMessageContent());

                //flsuh the message in stream
                out.flush();

                //read status response
                in = new BufferedReader(
                        new InputStreamReader(sock.getInputStream())
                     );
                String response = in.readLine();

                String code = response.substring(0, 3);

                if (log.isInfoEnabled()) {
                    log.info("Response status code for notification message sent via STP:" +
                             code);
                }

                if (!"200".equals(code)) {
                        throw new NotificationException(
                                "Status code received for notification message sent via STP: " +
                                response);
                } else {
                    if (log.isInfoEnabled()) {
                        log.info("Notification message to '" + address +
                                 ":" + port + "' sent");

                    }
                    return;
                }

            } catch (Exception e) {

                if (i == attempts) {
                    if (log.isInfoEnabled()) {
                        log.info("Sending notification to '" + address +
                                 ":" + port + "' failed (" + e.toString() + ")");
                    }

                    throw new NotificationException(e.getMessage());

                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Attempt n. " + i + " failed for '" + address +
                                   ":" + port + "'");
                        log.trace("Wait " + waitingTime + " seconds");
                    }
                    try {
                        Thread.currentThread().sleep(waitingTime * 1000);
                    } catch (Exception ex) {
                        log.error("Error waiting between the attempts", e);
                        throw new NotificationException(ex.getMessage());
                    }
                }
            } finally {

                //close connection to device
                try {
                    if (out != null) {
                        out.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                    if (sock != null) {
                        if (!sock.isClosed()) {
                            sock.close();
                        }
                    }
                } catch (IOException e) {
                    if (log.isTraceEnabled()) {
                        log.trace("Error closing connection (" + e.toString() + ")");
                    }
                }
            }
        }
    }

}
