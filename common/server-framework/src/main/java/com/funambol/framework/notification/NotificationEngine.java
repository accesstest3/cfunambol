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
package com.funambol.framework.notification;

import java.util.List;

import com.funambol.framework.core.Alert;

/**
 * This class is the principal component of the server alerted synchronization
 * architecture.
 * It is responsible for: creating the binary package and  sending the message
 * over the protocol and medium to be used.
 *
 * @version  $Id: NotificationEngine.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface NotificationEngine {

    // ---------------------------------------------------------- Public Methods
    /**
     * Sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws NotificationException if an error occurs
     *
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    void sendNotificationMessage(String  deviceId,
                                 Alert[] alerts  ,
                                 int     uimode  )
    throws NotificationException;

    /**
     * Sends a notification message to the given device.
     *
     * @param username the username
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws NotificationException if an error occurs
     */
    void sendNotificationMessage(String  username,
                                 String  deviceId,
                                 Alert[] alerts  ,
                                 int     uimode  )
    throws NotificationException;

    /**
      * Sends a notification message to all devices of the principals with the
      * given username.
      *
      * @param username the username
      * @param alerts   array of Alert command for the datastore to sync, used
      *                 to create the binary message to send
     * @param uimode   user intraction mode
     * @throws NotificationException if an error occurs
      */
    void sendNotificationMessages(String  username,
                                  Alert[] alerts  ,
                                  int     uimode  )
    throws NotificationException;

    /**
     * Retrieves a notification message created with the pending notifications
     * stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @return the notification message
     * @throws NotificationException if an error occurs
     */
    Message getMessageFromPendingNotifications(String username,
                                               String deviceId)
    throws NotificationException;

    /**
     * Deletes the notification stored in the datastore after that they are
     * delivered rightly.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     */
    public void deletePendingNotifications(String   username   ,
                                           String   deviceId   ,
                                           String[] syncSources)
    throws NotificationException;
}
