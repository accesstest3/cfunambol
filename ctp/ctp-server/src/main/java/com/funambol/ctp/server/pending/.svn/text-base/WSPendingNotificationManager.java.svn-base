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

package com.funambol.ctp.server.pending;

import com.funambol.framework.notification.Message;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;

import com.funambol.ctp.server.config.CTPServerConfiguration;

/**
 * Responsibilities: query the DS Server for pending push notifications.
 * Collaborators: the AdminWSClient in order to send the request via web service
 *
 * @version $Id$
 */
public class WSPendingNotificationManager
implements PendingNotificationManager {

    // --------------------------------------------------------------- Constants

    /** Name of the logger used to track web service call to the ds-server. */
    private static final String WS_CALLS_LOGGER_NAME = "funambol.ctp.server.ws";

    // -------------------------------------------------------- Member Variables

    /**
     * The Admin web service client used to get the pending notifications from
     * the DS Server
     */
    private AdminWSClient client;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>WSPendingNotificationManager</code>.
     */
    public WSPendingNotificationManager() {
        client = CTPServerConfiguration.getAdminWSClient();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Gets the pending notifications message from the DS Server
     *
     * @param username The user name to whom belongs the pending notifications
     * @param deviceId The device id to which  belongs the pending notifications
     * @return The pending notification message
     * @throws com.funambol.ctp.server.pending.PendingNotificationException If
     * an error occurs while getting the pending notification
     */
    public Message getMessageFromPendingNotifications(String username,
                                                      String deviceId)
    throws PendingNotificationException {
        try {

            Message notificationMessage =
                client.getMessageFromPendingNotifications(username, deviceId);

            return notificationMessage;
        } catch (AdminException ex) {
            throw new PendingNotificationException("Error while retrieving " +
                    "pending notifications", ex);
        }
    }

    /**
     * Removes from the storage the pending notifications that have been already
     * delivered.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws com.funambol.ctp.server.pending.PendingNotificationException If
     * an error occurs while removing from the storage the pending notifications
     */
    public void deletePendingNotifications(String   username   ,
                                           String   deviceId   ,
                                           String[] syncSources)
    throws PendingNotificationException {

        try {

            client.deletePendingNotifications(username, deviceId, syncSources);

        } catch (AdminException ex) {
            throw new PendingNotificationException("Error while removing " +
                    "the pending notifications", ex);
        }
    }

}