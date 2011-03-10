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

import java.util.Vector;

import org.apache.log4j.Logger;

import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.util.RspList;

import com.funambol.framework.notification.NotificationException;

/**
 * Send notification messages to CTPServers
 * Collaborates with Channel.
 * @version $Id: ChannelNotificationDispatcher.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 */
public class ChannelNotificationDispatcher {


    // -------------------------------------------------------------- Properties

    /**
     * The name of the JGroups group
     */
    private String groupName;

    /**
     * The reference to the singleton <CODE>ChannelNotificationDispatcher</CODE> instance
     */
    private static ChannelNotificationDispatcher channelSender = null;

    /**
     * The JGroups channel used to send notification messages
     */
    private Channel channel;

    /**
     * JGroups message dispatcher
     */
    private MessageDispatcher dispatcher = null;

    /**
     * The logger
     */
    private static final Logger logger =
        Logger.getLogger("funambol.server.notification.ctp-sender");

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>ChannelSender</code>
     * @param jgroupsConfigFileName The configuration file name for JGroups
     * initialization
     * @param groupName The name of the JGroups group
     * @throws ChannelNotificationException The exception thrown if
     * it is unable to create a JGroups channel
     */
    private ChannelNotificationDispatcher(String groupName, String jgroupsConfigFileName)
    throws ChannelNotificationException {

        if (logger.isInfoEnabled()) {
            logger.info("Creating JGroups channel with name '" + groupName + "' " +
                        "using '" + jgroupsConfigFileName + "'");
        }

        this.groupName = groupName;

        try {
            channel = new JChannel(jgroupsConfigFileName);
        } catch (ChannelException ex) {
            throw new ChannelNotificationException("Unable to create notification channel", ex);
        }
        channel.setOpt(Channel.LOCAL, Boolean.FALSE);
        channel.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);
        dispatcher = new MessageDispatcher(channel, null, null);

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(this));
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the singleton instance of <CODE>ChannelNotificationDispatcher</CODE>
     * @param groupName the group name
     * @param jgroupsConfigFileName the configuration file
     * @return an instance of ChannelNotificationDispatcher
     * @throws ChannelNotificationException if an error occurs
     */
    public static synchronized ChannelNotificationDispatcher getInstance(String groupName, String jgroupsConfigFileName)
    throws ChannelNotificationException {

        if (groupName == null) {
            throw new IllegalArgumentException("groupName must be not null");
        }

        if (channelSender == null) {
            channelSender = new ChannelNotificationDispatcher(groupName, jgroupsConfigFileName);
            channelSender.start();
        } else {
            if (!groupName.equals(channelSender.groupName)) {
                //
                // Stopping the previous channel and starting a new one
                //
                channelSender.close();
                channelSender = new ChannelNotificationDispatcher(groupName, jgroupsConfigFileName);
                channelSender.start();
            }
        }
        return channelSender;
    }

    /**
     * Starts the JGroups Channel
     * @throws ChannelNotificationException The exception thrown if
     * it is unable to connect the JGroups channel
     */
    public void start() throws ChannelNotificationException {

        if (!channel.isConnected()) {
            try {
                channel.connect(groupName);
            } catch (ChannelException ex) {
                throw new ChannelNotificationException("Unable to connect to group '" +
                        groupName + "'", ex);
            }
            if (logger.isInfoEnabled()) {
                logger.info("Notification channel started [groupName: " + groupName + "]");
            }
        }
    }

    /**
     * Send a notification to the group members
     * @param notification the notification message to send
     * @param timeout how long the dispatcher should wait for the ctpserver responses ?
     * @return a CTPNotificationResponse if the message has been delivered, NULL
     *         if no response is received.
     * @throws NotificationException
     */
    public CTPNotificationResponse send(CTPNotification notification, long timeout) throws NotificationException {

        Message msg = new Message(null, null, notification);
        //
        // OOB messages completely ignore any ordering constraints the stack might have.
        // Any message marked as OOB will be processed by the OOB thread pool.
        // This is necessary in cases where we don't want the message processing
        // to wait until all other messages from the same sender have been processed,
        //
        msg.setFlag(Message.OOB);

        if (logger.isTraceEnabled()) {
            logger.trace("Dispatching notification message...");
        }
        long startTime = System.currentTimeMillis();
        RspList rspList = dispatcher.castMessage(null,                   // dests (null means all)
                                                 msg,                    // msg to send
                                                 GroupRequest.GET_ALL,   // wait for all response
                                                 timeout);               // timeout
        long endTime = System.currentTimeMillis();
        if (logger.isTraceEnabled()) {
            logger.trace("Notification message dispatched to all CTP Servers in "
                         + (endTime - startTime) + " milliseconds");
        }
        Vector results = null;
        if (rspList != null) {
            results = rspList.getResults();
        }
        if (results == null) {
            return null;
        }
        int num = results.size();
        for (int i=0; i<num; i++) {
            Object response = results.elementAt(i);
            if (response != null) {
                //
                // A not null response means the message has been delived
                //
                if (response instanceof CTPNotificationResponse) {
                    return (CTPNotificationResponse)response;
                } else {
                    //
                    // Received an invalid response
                    //
                    logger.warn("Received '" + response + "' as response to a CTPNotification");
                }
            }
        }
        return null;
    }

    /**
     * Stops the JGroups channel
     */
    public void stop() {
        if (channel.isConnected() || channel.isOpen()) {
            channel.disconnect();
            if (logger.isInfoEnabled()) {
                logger.info("Notification channel '" + groupName + "' stopped");
            }
        }
    }

    /**
     * Closes the JGroups channel
     */
    public void close() {
        if (channel.isConnected() || channel.isOpen()) {
            channel.close();
            if (logger.isInfoEnabled()) {
                logger.info("Notification channel '" + groupName + "' closed");
            }
        }
    }
}

/**
 * A virtual-machine shutdown hook
 */
class ShutDownThread extends Thread {

    /** The ChannelNotificationDispatcher instance */
    private ChannelNotificationDispatcher dispatcher = null;

    public ShutDownThread(ChannelNotificationDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void run() {
        dispatcher.close();
    }

}
