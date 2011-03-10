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

package com.funambol.pushlistener.service.ws;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;

import com.funambol.framework.core.Alert;

import com.funambol.pushlistener.service.config.PushListenerConfiguration;
import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * Useful wrapper to call ds server notification message. <br/>
 * It implements the singleton pattern so it's enough to call:
 * <code>NotificationWrapper.getInstance().callNotification(...)</code>
 * @version $Id: NotificationWrapper.java,v 1.5 2007-11-28 11:15:04 nichele Exp $
 */
public class NotificationWrapper {

    //---------------------------------------------------------------- Constants 
    
    /** logger name */
    private static final String LOGGER_NAME = "funambol.pushlistener.ws";
    
    //------------------------------------------------------------- Private data

    /** The logger    */
    protected Logger log = Logger.getLogger(LOGGER_NAME);

    /** The WS client bean */
    private AdminWSClient adminWSClient = null;

    /** The WSServerInformation  */
    private ServerInformation serverInformation = null;

    /** The instance in order to implement singleton pattern */
    private static NotificationWrapper instance = null;

    /**
     * The number of notification (i.e. the number of the times the ds has been
     * called to send a notification
     */
    private AtomicLong numberOfNotifications = new AtomicLong();

    //------------------------------------------------------------- Constructors

    /** Private constructor in order to implement singleton pattern */
    private NotificationWrapper() {
        serverInformation =
            PushListenerConfiguration.getPushListenerConfigBean().getServerInformation();
        
        adminWSClient = new AdminWSClient(serverInformation, LOGGER_NAME);
    }

    //----------------------------------------------------------- Public Methods

    /**
     * Returns an instance of a NotificationWrapper.
     * @return an instance of a NotificationWrapper.
     */
    public static synchronized NotificationWrapper getInstance() {
        if (instance == null) {
            instance = new NotificationWrapper();
        }
        return instance;
    }

    /**
     * Returns the number of notifications
     * @return the number of notifications
     */
    public long getNumberOfNotifications() {
        return numberOfNotifications.get();
    }

    /**
     * Call sendNotificationMessage method exposed by the ds server via ws
     * @param userName the user name
     * @param deviceId the device to notify
     * @param alerts the Alerts to send
     * @throws Exception if an error occurs
     * 
     */
    public void notifyDevice(String userName, String deviceId, Alert[] alerts) throws Exception  {
        
        numberOfNotifications.incrementAndGet();

        adminWSClient.sendNotificationMessage(userName, deviceId, alerts, new Integer(1));
    }

    /**
     * Call sendNotificationMessage method exposed by the ds server via ws
     * @param deviceId the device to notify
     * @param alerts the Alerts to send
     * @throws Exception if an error occurs
     * 
     * @deprecated Since v71 use sendNotificationMessage(String username, String deviceId, Alert[] alerts, int uimode)
     */
    public void notifyDevice(String deviceId, Alert[] alerts) throws Exception  {
        
        numberOfNotifications.incrementAndGet();

        adminWSClient.sendNotificationMessage(deviceId, alerts, new Integer(1));
    }

    /**
     * Call sendNotificationMessages method exposed by the ds server via ws
     * @param userName the user to notify
     * @param alerts the Alerts to send
     * @throws Exception if an error occurs
     */
    public void notifyUser(String userName, Alert[] alerts) throws Exception  {
        
        numberOfNotifications.incrementAndGet();

        adminWSClient.sendNotificationMessages(userName, alerts, new Integer(1));
    }

    /**
     * Resets statistic information
     */
    public void resetStatistics() {
        numberOfNotifications.set(0);
    }
}
