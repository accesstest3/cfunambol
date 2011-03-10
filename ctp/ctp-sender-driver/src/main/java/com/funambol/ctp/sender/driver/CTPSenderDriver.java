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

package com.funambol.ctp.sender.driver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Properties;

import org.apache.log4j.Logger;

import com.funambol.framework.core.Alert;
import com.funambol.framework.core.CmdID;
import com.funambol.framework.core.Item;
import com.funambol.framework.core.Meta;
import com.funambol.framework.core.Target;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.builder.DSNotificationMessageBuilder;
import com.funambol.server.notification.builder.DSNotificationMessageBuilderImpl;
import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;
import com.funambol.server.notification.sender.tcp.ctp.ChannelNotificationDispatcher;
import com.funambol.server.notification.sender.tcp.ctp.ChannelNotificationException;

/**
 *
 * @version $Id: CTPSenderDriver.java,v 1.6 2007-11-28 11:26:15 nichele Exp $
 */
public class CTPSenderDriver {

    ChannelNotificationDispatcher notificationDispatcher;

    public Properties prop = new Properties();
    private String dirPath = "config";
    private String nomeFile = "ctpsender.properties";

    private final Logger logger = Logger.getLogger("funambol.ctp.sender.driver");

    public final static String PROPERTY_CFG_FILE_NAME =    "JGroupsConfigFileName";
    public final static String PROPERTY_GROUP_NAME =       "GroupName";
    public final static String PROPERTY_DEVICE_ID_PREFIX = "DEVICEID_";
    public final static String PROPERTY_TARGET_NAME =      "target";

    // ------------------------------------------------------------ Constructors

    public CTPSenderDriver() throws ChannelNotificationException {
        readProperties();
        String jgroupsConfigFileName = prop.getProperty(PROPERTY_CFG_FILE_NAME);
        String groupName = prop.getProperty(PROPERTY_GROUP_NAME);
        notificationDispatcher = 
            ChannelNotificationDispatcher.getInstance(groupName, "config/" + jgroupsConfigFileName);
    }

    // -------------------------------------------------------------- Properties

    // ---------------------------------------------------------- Public Methods

    public void start() {
        logger.debug("start");
        try {
            notificationDispatcher.start();
        } catch (ChannelNotificationException ex) {
            logger.error("Unable to start dispatcher", ex);
        }
    }

    public void sendNotification(String deviceIndex) {
        String deviceId = prop.getProperty(PROPERTY_DEVICE_ID_PREFIX + deviceIndex);
        if (deviceId == null) {
            logger.error("Unable to retrieve device id");
            return;
        }
        try {
            logger.info("sending Notification for " + deviceId);
            byte[] san = buildFakeNotificationMessage();
            CTPNotification notification = new CTPNotification(deviceId, san);
            notificationDispatcher.send(notification, 10000); // 10 seconds
        } catch (NotificationException ex) {
            logger.error("Unable to send notification", ex);
        }
    }

    public void stop() {
        logger.debug("stop");
        notificationDispatcher.stop();
    }

    public void close() {
        logger.debug("close");
        notificationDispatcher.close();
    }

    // --------------------------------------------------------- Private Methods

    private byte[] buildFakeNotificationMessage() throws NotificationException {
        byte[]  fakeServerNonce   = new byte[] {0};
        String  fakeServerId      = "funambol";
        String  fakeServerPw      = "funambol";
        String  fakeDeviceId      = "device";
        int     fakeUIMode        = 0;
        String  fakeDeviceAddress = "";
        int     fakeSessionId     = 0;
        Alert[] fakeAlerts        = null;

        fakeAlerts = createMailAlerts();

        DSNotificationMessageBuilder messageBuilder = new DSNotificationMessageBuilderImpl();
        Message fakeMessage;
        fakeMessage = messageBuilder.buildMessage(fakeSessionId, fakeDeviceId,
                fakeDeviceAddress, fakeServerId, fakeServerPw, fakeServerNonce,
                fakeAlerts, fakeUIMode, 1.2f);
        return fakeMessage.getMessageContent();
    }

    /**
     *
     *
     * @return Alert[]
     */
    private Alert[] createMailAlerts() {

        String targetName = prop.getProperty(PROPERTY_TARGET_NAME);
        if (targetName == null) {
            logger.error("Unable to retrieve targhet name: using default \"card\"");
            targetName = "card";
        }

        Target t1 = new Target(targetName);
        Meta meta1 = new Meta();
        meta1.setType("application/vnd.omads-email+xml");
        Item i1 = new Item(null, t1,meta1, null, false);

        Alert alert1 = new Alert(new CmdID(0), false, null, 206, new Item[]{i1});

        Alert[] alerts = new Alert[1];
        alerts[0] = alert1;

        return alerts;
    }


    private void readProperties() {
        try {
            File propFile = new File(dirPath, nomeFile);
            if (propFile.exists()) {
                prop.load(new FileInputStream(propFile));
            } else {
                logger.warn("Unable to open property file " + propFile.getName());
            }
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
