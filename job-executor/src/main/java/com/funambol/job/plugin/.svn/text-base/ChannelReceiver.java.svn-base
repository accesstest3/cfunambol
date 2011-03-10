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

import com.funambol.framework.cluster.Cluster;
import com.funambol.framework.cluster.Utility;
import java.lang.management.ManagementFactory;

import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;

import org.apache.log4j.Logger;

import org.jgroups.*;
import org.jgroups.blocks.RequestHandler;
import org.jgroups.jmx.JmxConfigurator;



import com.funambol.job.config.JobExecutorConfiguration;
import com.funambol.job.core.TaskExecutionRequest;
import com.funambol.job.core.TaskExecutionRequestResponse;
import com.funambol.job.utils.Def;

import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.framework.Task;
import com.funambol.pushlistener.framework.TaskConfiguration;
import java.lang.reflect.Method;

import org.jgroups.blocks.MessageDispatcher;

/**
 * Receives messages sent to the JGroups channel.
 *
 * @version $Id: ChannelReceiver.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class ChannelReceiver
        extends ReceiverAdapter
        implements NotificationReceiver, RequestHandler {

    // ------------------------------------------------------------ Private data
    /**
     * The Logger
     */
    private final static Logger log = Logger.getLogger(Def.LOG_NAME);
    /**
     * The JGroups channel used to receive notification messages
     */
    private Channel channel;
    private Address myAddress;
    private PushListenerInterface pushListenerInterface;
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
            String jgroupsConfigFileName)
            throws ChannelException {
        this.groupName = groupName;


        if (log.isTraceEnabled()) {
            log.trace("Creating notification group reading: " + jgroupsConfigFileName);
        }

        channel = new JChannel(jgroupsConfigFileName);

        channel.setOpt(Channel.LOCAL, Boolean.FALSE);
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
                        groupName, // clusterName
                        true);      // register protocol
            } catch (Exception ex) {
                log.warn("Unable to register JGroups MBean (" + ex + ")");
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
            if (log.isInfoEnabled()) {
                log.info("Detected change in the group '" + groupName + "'. New member list: "
                        + Utility.getMemberList(newView));
            }
        }
        if (log.isTraceEnabled()) {
            log.trace("View details: " + newView);
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
    * @throws ReceiverException
    */
    public void start() throws ReceiverException {
        try {
            channel.connect(groupName);
            myAddress = channel.getLocalAddress();
        } catch (ChannelException ex) {
            throw new ReceiverException("Unable to connect to the group \"" + groupName + "\"", ex);
        }
        if (log.isInfoEnabled()) {
            log.info("Connected to the group '" + groupName + "'. Member list: "
                    + Utility.getMemberList(channel));
            log.info("Local address: " + myAddress);
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
        if (log.isTraceEnabled()) {
            log.trace("Handling notification ");
        }
        if (!(message.getObject() instanceof TaskExecutionRequest)) {
            //
            // We are not interested on that...returning error
            //
            return new TaskExecutionRequestResponse(false, Def.GENERIC_ERROR_CODE, Def.getErrorMessage(Def.GENERIC_ERROR_CODE));
        }


        TaskExecutionRequest taskExecutionRequest = (TaskExecutionRequest) message.getObject();

        if (log.isTraceEnabled()) {
            log.trace("task Name" + taskExecutionRequest.getTaskName());
        }



        // check
        boolean isMyMessage = com.funambol.job.utils.Utility.isMyRequest(taskExecutionRequest.getId(),
                Cluster.getCluster().getClusterSize(), Cluster.getCluster().getServerIndex());

        if (log.isTraceEnabled()) {
            log.trace("is my request:" + isMyMessage);
        }
        HashMap<String, String> classmap = JobExecutorConfiguration.getJobExecutorConfigBean().getJobList();

        if (log.isTraceEnabled()) {
            log.trace("Tasks mapped:" + classmap.size());
            log.trace("Task name received:" + taskExecutionRequest.getTaskName());
        }

        if (taskExecutionRequest.getTaskName() == null || !classmap.containsKey(taskExecutionRequest.getTaskName())) {
            if (log.isTraceEnabled()) {
                log.trace("Error: Task \"" + taskExecutionRequest.getTaskName() + "\" is not configured in Job Executor");
            }
            return new TaskExecutionRequestResponse(false, Def.NOT_CONFIGURED_TASK_CODE, Def.getErrorMessage(Def.NOT_CONFIGURED_TASK_CODE));

        }
        String fullClassName = classmap.get(taskExecutionRequest.getTaskName());

        if (log.isTraceEnabled()) {
            log.trace("Task to be instantiated: " + fullClassName);
        }

        try {
            Class c = Class.forName(fullClassName);


            Task task = (Task) c.newInstance();

            TaskConfiguration conf = new TaskConfiguration();


            conf.setProperties(taskExecutionRequest.getPropertiesMap());


            Method method = null;
            try {
                method = task.getClass().getMethod("configure", TaskConfiguration.class);
                method.invoke(task, conf);
            } catch (AbstractMethodError e) {
                //
                // Nothing to do. This Task can't be configured
                //
                log.error("Error AbstractMethodError 'configure' method", e);
            } catch (NoSuchMethodException e) {
                //
                // Nothing to do. This Task can't be configured
                //
                log.error("Error NoSuchMethodException 'configure' method", e);
            } catch (Exception e) {
                log.error("Error invoking 'configure' method", e);
            }

            this.pushListenerInterface.executeTask(task, 1000);

            return new TaskExecutionRequestResponse(true);
        } catch (ClassNotFoundException ex) {
            return new TaskExecutionRequestResponse(false, Def.NOT_INSTANTIATABLE_TASK_CODE,
                    Def.getErrorMessage(Def.NOT_INSTANTIATABLE_TASK_CODE));
        } catch (InstantiationException ex) {
          return new TaskExecutionRequestResponse(false, Def.NOT_INSTANTIATABLE_TASK_CODE,
                    Def.getErrorMessage(Def.NOT_INSTANTIATABLE_TASK_CODE));
        } catch (IllegalAccessException ex) {
               return new TaskExecutionRequestResponse(false, Def.NOT_INSTANTIATABLE_TASK_CODE,
                    Def.getErrorMessage(Def.NOT_INSTANTIATABLE_TASK_CODE));
        } catch (Exception ex) {
               return new TaskExecutionRequestResponse(false, Def.GENERIC_ERROR_CODE,
                    Def.getErrorMessage(Def.GENERIC_ERROR_CODE));
        }
    }

    /**
     * @return the pushint
     */
    public PushListenerInterface getPushint() {
        return pushListenerInterface;
    }

    /**
     * @param pushint the pushint to set
     */
    public void setPushint(PushListenerInterface pushint) {
        this.pushListenerInterface = pushint;
    }
}
