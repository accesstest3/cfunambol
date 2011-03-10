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

import com.funambol.framework.tools.DbgTools;

import java.io.Serializable;


/**
 * Associates a notification message to the device id to which it must be
 * delivered. This is sent by the CTPSender to the CTPServers via JGroups in a
 * serialized form.
 * <p>
 * <bold>IMPORTANT NOTE: since this class is serialized between the CTPSender and
 * the CTPServers, it is important to consider that any change in this class could
 * have impacts on the serialization and that could be handled carefully
 * </bold>
 *
 * @version $Id: CTPNotification.java,v 1.1.1.1 2008-02-21 23:35:53 stefano_fornari Exp $
 */
public class CTPNotification implements Serializable {

    private static final long serialVersionUID = 5863716553987224620L;

    // -------------------------------------------------------------- Properties
    /**
     * The device id
     */
    private String deviceId;

    /**
     * The notification message
     */
    private byte[] notificationMessage;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructs an instance of <code>Notification</code>
     * @param deviceId The device id
     * @param notificationMessage The notification message sent to the device
     */
    public CTPNotification(String deviceId, byte[] notificationMessage) {
        this.deviceId = deviceId;
        this.notificationMessage = notificationMessage;
    }

    /**
     * get the device id
     * @return the device id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * set the device id
     * @param deviceId the device id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    /**
     * get the notification message
     * @return the notification message
     */
    public byte[] getNotificationMessage() {
        return notificationMessage;
    }

    /**
     * set the notification message
     * @param notificationMessage the notification message
     */
    public void setNotificationMessage(byte[] notificationMessage) {
        this.notificationMessage = notificationMessage;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[deviceId: ").append(deviceId)
          .append(", notificationMessage: ")
          .append((notificationMessage != null) ? DbgTools.bytesToHex(notificationMessage) : "null")
          .append(']');

        return sb.toString();
    }
}
