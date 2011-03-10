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

package com.funambol.ctp.server.notification.receiver;

import java.lang.management.ManagementFactory;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

import org.apache.log4j.Logger;

import org.jgroups.*;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.jmx.JmxConfigurator;

import com.funambol.ctp.server.notification.NotificationDispatcher;

import com.funambol.framework.cluster.Utility;

import com.funambol.server.notification.sender.tcp.ctp.CTPNotification;
import com.funambol.server.notification.sender.tcp.ctp.CTPNotificationResponse;

/**
 * Receives messages sent to the JGroups channel.
 * <p>Collaborates with <CODE>org.jgroups.Channel</CODE> and
 * <CODE>com.funambol.ctp.server.notification.NotificationDispatcher</CODE>.</p>
 *
 * @version $Id: ChannelReceiver.java,v 1.10 2007-11-28 11:26:15 nichele Exp $
 */
public class ChannelReceiver
extends ReceiverAdapter
implements NotificationReceiver, RequestHandler {

    // ------------------------------------------------------------ Private data
    /**
     * The Logger
     */
    final private Logger logger = Logger.getLogger("funambol.ctp.server.notification.receiver");

    /**
     * The JGroups channel used to receive notification messages
     */
    private Channel channel;

    /**
     * dispatch messages to subscribed listeners
     */
    private NotificationDispatcher dispatcher;

    /**
     * The address of this instance. This is obtained from JGroups
     */
    private Address myAddress = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Constructor
     *
     * @param groupName JGroups group name
     * @param dispatcher reference to the NotificationDispatcher to use
     * @param jgroupsConfigFileName the jgroups config file name
     * @throws org.jgroups.ChannelException
     */
    public ChannelReceiver(String groupName,
                           NotificationDispatcher dispatcher,
                           String jgroupsConfigFileName)
    throws ChannelException {
        this.groupName = groupName;
        this.dispatcher = dispatcher;

        if (logger.isTraceEnabled()) {
            logger.trace("Creating notification group reading: " + jgroupsConfigFileName);
        }
        channel = new JChannel(jgroupsConfigFileName);
        channel.setOpt(Channel.LOCAL,          Boolean.FALSE);
        channel.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);

        //
        // We instantiate a MessageDispatcher in order to handle the channel and
        // the requests it receives, so, even if the local variable disp seems
        // to be unused, the referenced instance is accually called.
        // The  MessageDispatcher has a UpHandler and sets it to the Channel.
        // When a UpHandler is set, the Channel will pass all events directly
        // to it, so all messages are actually managed by the MessageDispatcher.
        // The ChannelReceiver subscribe itself as RequestHandler and as
        // MembershipListener to the  MessageDispatcher, so the callback methods
        // handle and viewAccepted are called.
        //
        MessageDispatcher disp = new MessageDispatcher(channel, null, this, this);

        if (channel instanceof JChannel) {
            //
            // Publishing JGroups MBeans
            //
            try {
                MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
                JmxConfigurator.registerChannel((JChannel) channel,
                                                mbs,
                                                "com.funambol.channel",
                                                groupName,  // clusterName
                                                true);      // register protocol
            } catch (Exception ex) {
                logger.warn("Unable to register JGroups MBean (" + ex + ")");
            }
        }
    }

    // -------------------------------------------------------------- Properties

    /**
     * The JGroups group name
     */
    private String groupName;

    /**
     * Get the JGroups group name
     * @return The JGroups group name
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * Set the JGroup group name
     * @param groupName The JGroup group name to use
     */
    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    // ---------------------------------------------------------- Public Methods


    @Override
    public void viewAccepted(View newView) {
        if (newView != null && newView.size() > 0) {
            if (logger.isInfoEnabled()) {
                logger.info("Detected change in the group '" + groupName + "'. New member list: " +
                            Utility.getMemberList(newView));
            }
        }
        if (logger.isTraceEnabled()) {
            logger.trace("View details: " + newView);
        }
        myAddress = channel.getLocalAddress();
    }

    /**
     * Returns the status of the receiver
     * @return the status of the receiver
     */
    public Map<String, String> getStatus() {
        Map<String, String> status = new HashMap<String, String>();
        status.put("notification-group", groupName + Utility.getMemberList(channel));
        return status;
    }

    /**
     * Start listening to JGroups messages
     * @throws com.funambol.ctp.server.notification.receiver.ReceiverException
     */
    public void start() throws ReceiverException {
        try {
            channel.connect(groupName);
            myAddress = channel.getLocalAddress();
        } catch (ChannelException ex) {
            throw new ReceiverException("Unable to connect to the group \"" + groupName + "\"", ex);
        }
        if (logger.isInfoEnabled()) {
            logger.info("Connected to the group '" + groupName + "'. Member list: " +
                        Utility.getMemberList(channel));
            logger.info("Local address: " + myAddress);
        }
    }

    /**
     * Stop listening JGroups messages
     */
    public void stop() {
        channel.disconnect();
    }

    /**
     * Close JGroup channel
     */
    public void close() {
        channel.close();
    }

    // --------------------------------------------------------- Private Methods

    /**
     * See JGroups RequestHandler interface
     * @param message The new arrived message
     * @return a NotificationResponse if the message has been delivered, NULL
     *         otherwise
     */
    public Object handle(Message message) {

        if (!(message.getObject() instanceof CTPNotification)) {
            //
            // We are not interested on that...ignoring it
            //
            return null;
        }

        CTPNotification notification = (CTPNotification) message.getObject();

        boolean dispatched = dispatcher.dispatch(notification);

        if (dispatched) {
            CTPNotificationResponse notificationResponse =
                new CTPNotificationResponse(myAddress.toString());

            return notificationResponse;
        }
        return null;
    }
}
