/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
package com.funambol.job.plugin;

import com.funambol.job.utils.Def;
import com.funambol.pushlistener.framework.PushListenerInterface;
import org.jgroups.ChannelException;
import org.apache.log4j.Logger;

/**
 * Receives notification messages broadcasted to the job executor
 * <p>Collaborates with NotificationReceiver.
 *
 * @version $Id: NotificationManager.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class NotificationManager {

    /**
     * Receives notification messages
     */
    private NotificationReceiver receiver;
    /**
     * The logger
     */
    private final Logger logger = Logger.getLogger(Def.LOG_NAME);

    // ------------------------------------------------------------ Constructors
    /**
     * Constructors
     * @throws org.jgroups.ChannelException If an error occurs creating the
     * JGroups Channel
     */
    public NotificationManager(String groupName, String jgroupsConfigFileName) throws ChannelException {
        receiver = new ChannelReceiver(groupName, jgroupsConfigFileName);
    }

    public void setReceiverPushListener(PushListenerInterface pushInt) {

        ((ChannelReceiver) receiver).setPushint(pushInt);
    }
    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the receiver
     * @return the receiver
     */
    public NotificationReceiver getNotificationReceiver() {
        return receiver;
    }

    /**
     * Starts receiving notification messages
     * @throws NotificationProviderException
     */
    public void start() throws NotificationProviderException {
        if (logger.isTraceEnabled()) {
            logger.trace("Starting NotificationManager");
        }
        try {
            receiver.start();
        } catch (ReceiverException ex) {
            logger.error("Unable to start the notification receiver", ex);
            throw new NotificationProviderException("Unable to start the notification manager", ex);
        }
    }

    /**
     * Stops receiving notification messages
     */
    public void stop() {
        if (logger.isTraceEnabled()) {
            logger.trace("Shutting down NotificationMananger");
        }
        receiver.stop();
    }

    /**
     * Closes the communication channel
     */
    public void close() {
        if (logger.isTraceEnabled()) {
            logger.trace("close notification messages channel");
        }
        receiver.close();
    }
//
//    /**
//     * Subscribes the NotificationListener to notification messages
//     * @param deviceId the device id of the notification messages to subscribe
//     * @param listener specifies the NotificationListener interested in the
//     * notification messages
//     */
//    public void subscribe(String deviceId, NotificationListener listener) {
//        dispatcher.subscribe(deviceId, listener);
//    }
//
//    /**
//     * Unsubscribes the NotificationListener from notification messages
//     * @param deviceId
//     * @param listener
//     */
//    public boolean unsubscribe(String deviceId, NotificationListener listener) {
//        return dispatcher.unsubscribe(deviceId, listener);
//    }
}
