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

import java.io.File;

import com.funambol.framework.notification.Message;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.NotificationNotSentException;
import com.funambol.framework.notification.sender.Sender;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.DbgTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

import com.funambol.server.config.Configuration;

/**
 * Notification sender that delivers notification messages via CTP
 * @version $Id: CTPSender.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 */
public class CTPSender implements Sender, LazyInitBean {

    // ------------------------------------------------------------ Private data

    /**
     * The ChannelNotificationDispatcher used to send notifications.
     */
    private ChannelNotificationDispatcher notificationDispatcher = null;

    private static final FunambolLogger logger =
        FunambolLoggerFactory.getLogger("funambol.server.notification.ctp-sender");


    // -------------------------------------------------------------- Properties

    /**
     * The notification group name
     */
    private String groupName = null;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    /**
     * The JGroups configuration file name
     */
    private String jgroupsConfigFileName = null;

    public String getJgroupsConfigFileName() {
        return jgroupsConfigFileName;
    }

    public void setJgroupsConfigFileName(String jgroupsConfigFileName) {
        this.jgroupsConfigFileName = jgroupsConfigFileName;
    }

    /**
     * How long the sender should wait for the ctpserver responses ?
     * 0 means no wait
     */
    private long notificationResponseTimeout = 0;

    public long getNotificationResponseTimeout() {
        return notificationResponseTimeout;
    }

    public void setNotificationResponseTimeout(long notificationResponseTimeout) {
        this.notificationResponseTimeout = notificationResponseTimeout;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>CTPSender</code>.
     */
    public CTPSender() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * LazyInitBean
     */
    public void init() throws BeanInitializationException {
        this.jgroupsConfigFileName =
            Configuration.getConfigPath() + File.separator + jgroupsConfigFileName;
        try {
            notificationDispatcher = ChannelNotificationDispatcher.getInstance(groupName, jgroupsConfigFileName);
        } catch (ChannelNotificationException ex) {
            throw new BeanInitializationException("Unable to create the ChannelNotificationDispatcher", ex);
        }
    }

    /**
     *
     * @param device
     * @param message
     * @throws com.funambol.framework.notification.NotificationException
     */
    public void sendNotificationMessage(Sync4jDevice device,
                                        Message      message)
            throws NotificationException {

        if (message == null) {
            throw new NotificationException("Unable to send a null message");
        }
        if (message.getMessageContent() == null) {
            throw new NotificationException("The message content is null..." +
                    "unable to send it");
        }

        CTPNotification notification = new CTPNotification(device.getDeviceId(),
                                                           message.getMessageContent());

        CTPNotificationResponse response =
                notificationDispatcher.send(notification, notificationResponseTimeout);

        if (response == null) {
            if (logger.isTraceEnabled()) {
                logger.trace("Notification message not delivered by any CTP Server");
            }
            throw new NotificationNotSentException("Notification message not delivered by any CTP Server");
        } else {
            if (logger.isTraceEnabled()) {
                logger.trace("Notification message delivered by " + response.getDeliveredBy());
            }
        }
    }
}
