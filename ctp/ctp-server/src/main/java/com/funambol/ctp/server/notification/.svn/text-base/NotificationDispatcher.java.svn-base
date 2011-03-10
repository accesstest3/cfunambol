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

package com.funambol.ctp.server.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;

/**
 * Manages subscriptions and dispatch messages to subscribers
 *
 * @version $Id: NotificationDispatcher.java,v 1.12 2007-11-28 11:26:16 nichele Exp $
 */
public class NotificationDispatcher {

    /**
     * Associate the deviceId with the NotificationListener interested on
     * notification messages addressed to the deviceId
     */
    private Map<String, NotificationListener> listeners = new ConcurrentHashMap();

    /**
     * The Logger used for log messages regarding notification messages to be
     * delivered to clients.
     */
    private final Logger logger = Logger.getLogger("funambol.ctp.server.notification");

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new instance of <CODE>NotificationDispatcher</CODE>
     */
    public NotificationDispatcher() {
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * <p>Subscribes the NotificationListener to notification messages.</p>
     * <p>There could be only one listener interested to messages addressed to
     * a device id.</p>
     * @param deviceId the device id of the notification messages to subscribe
     * @param listener specifies the NotificationListener interested in the
     * notification messages
     */
    public void subscribe(String deviceId, NotificationListener listener) {
        NotificationListener previousListener = listeners.put(deviceId, listener);
        if (previousListener != null) {
            previousListener.onUnsubscription();
        }
    }

    /**
     * <p>Unsubscribes the NotificationListener from notification messages.</p>
     * @param deviceId the device id of the notification messages to unsubscribe
     * @param listener The NotificationListener no more interested in
     * notification messages
     */
    public boolean unsubscribe(String deviceId, NotificationListener listener) {
        NotificationListener subscribedListener = listeners.get(deviceId);
        if (subscribedListener == null) {
            logger.warn("Unable to unsubscribe a not subscribed listener.");
            return false;
        }
        if (subscribedListener.equals(listener)) {
            listeners.remove(deviceId);
            return true;
        } else {
            // there is already an other listener subscribed with the same deviceId
            // so the current listener is already unsubscribed and no action is
            // required
           return false;
        }
    }

    /**
     * Dispatch a notification message to subscribers
     * @param message The Notification to dispatch to listeners
     * @return true if the message is dispatched
     * folse otherwise
     */
    public boolean dispatch(CTPNotification message) {
        String deviceId = message.getDeviceId();
        if (deviceId == null ||  "".equals(deviceId)) {

            logger.warn("Invalid notification message: missing deviceid");

            return false;
        }

        NotificationListener listener = listeners.get(deviceId);
        if (listener == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Device '" + deviceId + "' not connected. Ignoring notification message");
            }
            return false;
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Processing notification message for '" + deviceId + "'");
            }
            listener.onNotification(message);
            return true;
        }
    }
}

