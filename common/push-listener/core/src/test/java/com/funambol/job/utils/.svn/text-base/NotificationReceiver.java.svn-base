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
package com.funambol.job.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;
import org.jgroups.View;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.blocks.RequestHandler;

import com.funambol.job.core.TaskExecutionRequest;
import com.funambol.job.core.TaskExecutionRequestResponse;


public class NotificationReceiver extends ReceiverAdapter implements RequestHandler {
    private static final String JGROUPS_GROUP_NAME = "jobexecutor-request";
    private static final String JGROUPS_CONFIG_FILENAME = "config/jgroups-jobexecutor-request-group.xml";
    private Channel channel;
    private MessageDispatcher dispatcher;
    private static final Logger log = Logger.getLogger(NotificationReceiver.class);
    
    public NotificationReceiver() {
        log.info("Creating Notifications Receiver from "+JGROUPS_CONFIG_FILENAME);
        try {
            channel = new JChannel(JGROUPS_CONFIG_FILENAME);
            channel.setReceiver(this);
            dispatcher = new MessageDispatcher(channel, null, null, this);
        } catch (ChannelException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        NotificationReceiver receiver = new NotificationReceiver();
        try {
            receiver.start();
            receiver.eventLoop();
        } catch (ChannelException e) {
            e.printStackTrace();
        }
    }
    
    public void receive(Message msg) {
        log.info("Object received "+msg.getSrc());
    }

    public void viewAccepted(View newView) {
        log.info("New view: "+newView.getCreator().toString());
    }
    
    

    public void start() throws ChannelException {
        Runtime.getRuntime().addShutdownHook(new ShutDownThread(this));
        if (!channel.isConnected()) {
            channel.connect(JGROUPS_GROUP_NAME);
            log.info("Starting the channel");
        }
    }
    
    public void stop() {
        if (channel.isConnected() || channel.isOpen()) {
            channel.disconnect();
        }
    }
    
    public void close() {
        if (channel.isConnected() || channel.isOpen()) {
            channel.close();
        }
        dispatcher.stop();
    }
    
    private void eventLoop() {
        BufferedReader in=new BufferedReader(new InputStreamReader(System.in));
        while(true) {
            try {
                String line=in.readLine().toLowerCase();
                if(line.startsWith("quit") || line.startsWith("exit")) {
                    break;
                }
            }
            catch(Exception e) {
            }
        }
    }

    public Object handle(Message message) {
        log.info("Handling received message");
        if (!(message.getObject() instanceof TaskExecutionRequest)) {
            return null;
        }
        TaskExecutionRequest taskExecutionRequest = (TaskExecutionRequest) message.getObject();
        log.info("Received request to run "+taskExecutionRequest.getTaskName());
        if (taskExecutionRequest.getTaskName().equals("generic-successful-task")) {
            return new TaskExecutionRequestResponse(true);
        } else if (taskExecutionRequest.getTaskName().equals("generic-failed-task")) {
            return new TaskExecutionRequestResponse(false);
        }
        return null;
    }
    
}
class ShutDownThread extends Thread {

    private NotificationReceiver receiver = null;

    public ShutDownThread(NotificationReceiver dispatcher) {
        this.receiver = dispatcher;
    }

    @Override
    public void run() {
        receiver.close();
    }
}
