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
package com.funambol.job;

import java.util.HashMap;
import java.util.Vector;
import org.apache.log4j.Logger;
import org.jgroups.Channel;
import org.jgroups.ChannelException;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.blocks.GroupRequest;
import org.jgroups.blocks.MessageDispatcher;
import org.jgroups.util.RspList;

import com.funambol.job.config.JobExecutorConfiguration;
import com.funambol.job.core.TaskExecutionRequest;
import com.funambol.job.core.TaskExecutionRequestResponse;

/**
 *
 */
public class JobExecutor {

    // -------------------------------------------------------------- Properties
    /**
     * The name and configuration file name of the JGroups group
     */
    private String groupName;
    private String jgroupsConfigFileName;
    /**
     * The reference to the singleton <CODE>JobExecutor</CODE> instance
     */
    private static JobExecutor jobExecutor = null;
    /**
     * The JGroups channel used to send notification messages
     */
    private Channel channel = null;
    /**
     * JGroups message dispatcher
     */
    private MessageDispatcher dispatcher = null;
    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.pushlistener.jobexecutor");
    
    /**
     * Indicates whether the service is initialized;
     */
    private boolean initialized = false;

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of <code>JobExecutor</code>.
     */
    private JobExecutor() throws JobExecutorException {
        if (log.isTraceEnabled()) {
            log.trace("Creating Job Executor");
        }
    }
    
    /**
     * Indicates whether the instance of the service is initialized.
     * @return true if service is initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * <p>Initializes the instance of the JobExecutor using the configuration.
     * <p>Should be run before using the JobExecutor.
     * <p>Creates JGroups channel and JGroups Message Dispatcher.
     * <p>Starts the JobExecutor.
     * @throws Exception
     */
    public void init() throws Exception {
        groupName = JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupName();
        if (groupName == null) {
            throw new IllegalArgumentException("groupName must be not null");
        }

        jgroupsConfigFileName = JobExecutorConfiguration.getJobExecutorConfigBean().getNotificationGroupConfigFileName();
        if (jgroupsConfigFileName == null) {
            throw new IllegalArgumentException("jgroupsConfigFileName must be not null");
        }

        if (log.isTraceEnabled()) {
            log.trace("Creating JGroups channel with name '" + groupName + "' "
                    + "using '" + jgroupsConfigFileName + "'");
        }

        try {
            channel = new JChannel(jgroupsConfigFileName);
        } catch (ChannelException ex) {
            throw new JobExecutorException("Unable to create notification channel", ex);
        }
        channel.setOpt(Channel.LOCAL, Boolean.FALSE);
        channel.setOpt(Channel.AUTO_RECONNECT, Boolean.TRUE);
        dispatcher = new MessageDispatcher(channel, null, null);
        start();
        initialized = true;
    }

    /**
     * Returns singleton instance of the JobExecutor.
     * @return Singleton instance of the JobExecutor
     * @throws JobExecutorException
     */
    public static synchronized JobExecutor getJobExecutor() throws JobExecutorException {
        if (jobExecutor == null) {
            if (log.isTraceEnabled()) {
                log.trace("Instantiating JobExecutor - Creating a new one");
            }
            jobExecutor = new JobExecutor();
        }
        return jobExecutor;
    }
    
    /**
     * Starts the JobExecutor and its JGroups channel.
     * @throws JobExecutorException The exception thrown if
     * it is unable to connect the JGroups channel
     */
    public void start() throws JobExecutorException {
        
        if (channel == null) {
            throw new JobExecutorException("JGroups channel is not created");
        }

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(this));

        if (!channel.isConnected()) {
            try {
                channel.connect(groupName);
            } catch (ChannelException ex) {
                throw new JobExecutorException("Unable to connect to group '"
                        + groupName + "'", ex);
            }
            if (log.isTraceEnabled()) {
                log.trace("Notification channel started [groupName: " + groupName + "]");
            }
        }
    }

    /**
     * Sends a JGroups request to execute the task with specified name and 
     * parameters. 
     * @param taskName Task codename
     * @param taskArgs Task parameters
     * @return Task execution request response
     * @throws JobExecutorException
     * @see com.funambol.job.core.TaskExecutionRequestResponse
     */
    public TaskExecutionRequestResponse executeTask(String id,String taskName, HashMap<String, Object> taskArgs) throws JobExecutorException {
        return executeTask(new TaskExecutionRequest(id,taskName, taskArgs));
    }

    /**
     * Sends a task execution request using the JGroups channel.
     * @param taskExecutionRequest Task Execution Request object
     * @param timeout Maximum amount of time in milliseconds to wait for the responses
     * @return Task execution request response
     * @throws JobExecutorException
     * @see com.funambol.job.core.TaskExecutionRequestResponse
     * @see com.funambol.job.core.TaskExecutionRequest
     */
    public TaskExecutionRequestResponse executeTask(TaskExecutionRequest taskExecutionRequest) throws JobExecutorException {
        long timeout = JobExecutorConfiguration.getJobExecutorConfigBean().getWaitForResponseTimeout();
        if (!isInitialized()) {
            throw new JobExecutorException("Job Executor is not initialized");
        }
        
        if (channel == null) {
            throw new JobExecutorException("JGroups channel is not created");
        }
        
        if (!channel.isOpen() || !channel.isConnected()) {
            throw new JobExecutorException("JGroups channel is not open or connected");
        }
        
        if (dispatcher == null) {
            throw new JobExecutorException("JGroups messsage dispatcher is not created");
        }

        Message msg = new Message(null, null, taskExecutionRequest);
        //
        // OOB messages completely ignore any ordering constraints the stack might have.
        // Any message marked as OOB will be processed by the OOB thread pool.
        // This is necessary in cases where we don't want the message processing
        // to wait until all other messages from the same sender have been processed,
        //
        msg.setFlag(Message.OOB);

        if (log.isTraceEnabled()) {
            log.trace("Sending request to execute task " + taskExecutionRequest.getTaskName());
        }

        long startTime = System.currentTimeMillis();
        RspList rspList = dispatcher.castMessage(null, // dests (null means all)
                msg, // msg to send
                GroupRequest.GET_ALL, // wait for all response
                timeout);               // timeout
        long endTime = System.currentTimeMillis();
        if (log.isTraceEnabled()) {
            log.trace("Request is sent to all Job Executors in "
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
        for (int i = 0; i < num; i++) {
            Object response = results.elementAt(i);
            if (response != null) {
                //
                // A not null response means the message has been delived
                //

                if (response instanceof TaskExecutionRequestResponse) {
                    return (TaskExecutionRequestResponse) response;
                } else {
                    //
                    // Received an invalid response
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("Received '" + response + "' as response to a Task Execution Request");
                    }
                }
            }
        }
        return null;
    }

    /**
     * Stops the JobExecutor and its JGroups channel.
     * @throws JobExecutorException
     */
    public void stop() throws JobExecutorException {
        if (channel == null) {
            throw new JobExecutorException("JGroups channel is not created");
        }
        
        if (channel.isConnected() || channel.isOpen()) {
            channel.disconnect();
            if (log.isTraceEnabled()) {
                log.trace("Notification channel '" + groupName + "' stopped");
            }

        }
    }

    /**
     * Closes the JobExecutor and its JGroups channel and stops the MessageDispatcher.
     * @throws JobExecutorException
     */
    public void close() {
        if (channel != null) {
            if (channel.isConnected() || channel.isOpen()) {
                channel.close();
                if (log.isTraceEnabled()) {
                    log.trace("Notification channel '" + groupName + "' closed");
                }
            }
        }
        
        if (dispatcher != null) {
            dispatcher.stop();
        }
    }
}

/**
 * A virtual-machine shutdown hook
 */
class ShutDownThread extends Thread {

    private JobExecutor service = null;

    public ShutDownThread(JobExecutor service) {
        this.service = service;
    }

    @Override
    public void run() {
        service.close();
    }
}
