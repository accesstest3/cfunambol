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

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.regex.Pattern;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.server.Sync4jDevice;

/**
 * Creates a (Binary)SMSMessage from a notification Message object
 *
 * @version $Id: SMSMessageCreator.java,v 1.4 2008-07-03 08:49:42 nichele Exp $
 */
public class SMSMessageCreator {

    // --------------------------------------------------------------- Constants

    /** Standard sms destination port */
    private static final int STANDARD_NOTIFICATION_DESTINATION_PORT = 2948;

    /** Logger name */
    private static final String LOGGER_NAME = "funambol.server.notification.sms-notification-builder";

    /** Default deviceid - port mapping table */
    private static final Map<String, Integer> DEFAULT_PORT_MAPPING = new HashMap<String, Integer>();

    static {
        DEFAULT_PORT_MAPPING.put("fjm-[\\p{ASCII}]*", 50001);
    }

    // -------------------------------------------------------------- Properties

    /**
     * The sms destination port
     */
    private int defaultDestinationPort = STANDARD_NOTIFICATION_DESTINATION_PORT;

    /**
     * Returns the defaultDestinationPort
     * @return int the defaultDestinationPort
     */
    public int getDefaultDestinationPort() {
        return defaultDestinationPort;
    }

    /**
     * Sets the defaultDestinationPort
     * @param defaultDestinationPort the default destination port to set
     */
    public void setDefaultDestinationPort(int defaultDestinationPort) {
        this.defaultDestinationPort = defaultDestinationPort;
    }

    /**
     * Contains a map between a regular expression to apply on the deviceId and the
     * destination port to use. In this way, for a set of devices, we can set a specific
     * destination port
     */
    private Map<String, Integer> deviceIdPortMapping = DEFAULT_PORT_MAPPING;

    /**
     * Returns the deviceIdPortMapping
     * @return the deviceIdPortMapping
     */
    public Map<String, Integer> getDeviceIdPortMapping() {
        return deviceIdPortMapping;
    }

    /**
     * Sets the deviceIdPortMapping
     * @param deviceIdPortMapping the mapping
     */
    public void setDeviceIdPortMapping(Map<String, Integer> deviceIdPortMapping) {
        this.deviceIdPortMapping = deviceIdPortMapping;
    }

    // ------------------------------------------------------------ Private data

    /**
     * The used logger
     */
    private FunambolLogger log = null;

    // ------------------------------------------------------------- Constructor

    /** Creates a new instance of SMSNotificationBuilder */
    public SMSMessageCreator() {
        log = FunambolLoggerFactory.getLogger(LOGGER_NAME);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Creates a SMSMessage given a Message
     * @param device the message receipinet device
     * @param msg The message to be sent as a sms
     * @return The sms to send
     */
    public BinarySMSMessage createSMSMessage(Sync4jDevice device, Message msg) {
        BinarySMSMessage sms = null;

        byte[] wdp = getWdpNotificationMessage(getDestinationPort(device));
        byte[] wsp = getWspNotificationMessage(msg.getMessageType());

        sms = new BinarySMSMessage(wdp, wsp, msg.getMessageContent());

        return sms;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Creates the wdp to use to send notification message
     *
     * @param destinationPort the sms destination port
     * @return byte[] the wdp for notification message
     */
    protected byte[] getWdpNotificationMessage(int destinationPort) {
        WDPHeader wdpHeader = null;
        if (destinationPort == -1) {
            //
            // Using the default destination port
            //
            destinationPort = defaultDestinationPort;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Creating wdp with destination port: " + destinationPort);
            }
        }

        wdpHeader = new WDPHeader(destinationPort);

        byte[] wdp = wdpHeader.getHeader();
        return wdp;
    }

    /**
     * Returns the destination port to use to send the notification message on the given
     * device. The destination port is retrievied checking the data contained in the map
     * <code>deviceIdPortMapping</code> that contains some mapping between a regular expr.
     * (that must check with the device id) and the port.
     * @param device the device to notify
     * @return the destination port to use
     */
    protected int getDestinationPort(Sync4jDevice device) {
        String deviceId = device.getDeviceId();
        if (deviceIdPortMapping == null) {
            return -1;
        }
        Iterator<String> it = deviceIdPortMapping.keySet().iterator();
        int destinationPort = -1;
        while (it.hasNext()) {

            String regExp = it.next();
            if (Pattern.matches(regExp, deviceId)) {
                destinationPort = deviceIdPortMapping.get(regExp);
                return destinationPort;
            }
        }
        return -1;
    }

    /**
     * Creates the wsp to use to send notifcation message
     * @param messageType Specifies the message type version
     * @return byte[] the wsp for notification message
     */
    protected byte[] getWspNotificationMessage(Message.Type messageType) {

        byte[] contentType = null;
        if (messageType == Message.Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE) {
            contentType = new byte[] {WSPHeader.CONTENT_TYPE_CODE_NOTIFICATION};
        } else if (messageType == Message.Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE) {
            //
            // 0x03: Content type length
            // 0x02: Short-length
            // 0x03: Content-Type
            //       (see http://www.openmobilealliance.org/tech/omna/omna-wsp-content-type.htm)
            contentType = new byte[] {0x03, 0x02, 0x03, 0x03};
        }

        WSPHeader wspHeader = new WSPHeader(contentType);

        byte[] wsp = wspHeader.getHeader();

        return wsp;
    }

}
