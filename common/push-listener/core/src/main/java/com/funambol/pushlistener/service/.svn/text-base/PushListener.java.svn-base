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
package com.funambol.pushlistener.service;

import java.io.File;

import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;


import com.funambol.framework.cluster.Cluster;
import com.funambol.framework.cluster.ClusterConfiguration;
import com.funambol.framework.cluster.ClusterListener;

import com.funambol.framework.management.StatusMXBean;

import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.framework.ServiceDescriptor;
import com.funambol.pushlistener.framework.Task;

import com.funambol.pushlistener.service.config.PushListenerConfiguration;
import com.funambol.pushlistener.service.health.HealthThread;
import com.funambol.pushlistener.service.management.StatusMXBeanImpl;
import com.funambol.pushlistener.service.plugin.PushListenerPluginsHandler;
import com.funambol.pushlistener.service.registry.RegistryMonitor;
import com.funambol.pushlistener.service.registry.RegistryMonitorHealthStatus;
import com.funambol.pushlistener.service.taskexecutor.ScheduledTaskExecutor;
import com.funambol.pushlistener.service.taskexecutor.ScheduledTaskExecutorHealthStatus;
import com.funambol.pushlistener.service.taskexecutor.TaskWrapper;

import com.funambol.pushlistener.service.ws.NotificationWrapper;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.thread.ThreadToolsMBean;

/**
 * Main class to run a PushListener instance
 * @version $Id: PushListener.java,v 1.26 2008-05-18 16:23:42 nichele Exp $
 */
public class PushListener implements PushListenerMBean, ClusterListener {

    static {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
    }
        
    // ------------------------------------------------------------ Private data

    /** The logger */
    private final static Logger log = Logger.getLogger("funambol.pushlistener");

    /** The task executor */
    private ScheduledTaskExecutor taskExecutor;

    /** The tasks monitor */
    private RegistryMonitor registryMonitor;

    /** The HealthThread */
    private HealthThread healthThread;

    /** The PushListenerPluginHandler */
    private PushListenerPluginsHandler pushPluginsHandler;

    /** The statistics collector */
    private StatisticsCollector statisticsCollector;

    /**
     * The PushListenerInterface
     */
    private PushListenerInterface pushListenerInterface;
    
   

    // -------------------------------------------------------------- Properties

    private ServiceDescriptor serviceDescriptor = null;

    public ServiceDescriptor getServiceDescriptor() {
        return serviceDescriptor;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new PushListener
     * @param service the service descriptor
     */
    public PushListener(ServiceDescriptor service) {

        if (service == null) {
            throw new IllegalArgumentException("The ServiceDescriptor can not be null");
        }
        if (log.isInfoEnabled()) {
            log.info("Creating push listener instance");
            log.info("Configuration object: " + PushListenerConfiguration.getPushListenerConfigBean());
        }
        this.serviceDescriptor = service;

    }

    // ---------------------------------------------------------- Public methods

    /**
     * Main method to run a PushListener
     * @param args the arguments
     */
    public static void main(String[] args) {

        String configPath = PushListenerConfiguration.getConfigPath();
        String log4jFile = configPath + File.separator + "log4j.xml";

        DOMConfigurator.configureAndWatch(log4jFile, 30000); // 30 sec.

        if (log.isInfoEnabled()) {
            log.info("Starting push listener");
        }

        ServiceDescriptor service =
                new ServiceDescriptor("PushFramework Example",
                                      "6.5.0",
                                      "com.funambol.pushframework.example:type=PushListenerExample");

        try {
            DataSourceContextHelper.configureAndBindDataSources();
        } catch (Exception ex) {
            log.error("Error initializing the datasources", ex);
            System.exit(-1);
        }
        
        PushListener pushListener = new PushListener(service);

        try {
            pushListener.init();
        } catch (Exception ex) {
            log.error("Error initializing the PushListener", ex);
            pushListener.shutdown();
            System.exit(-1);
        }

        try {
            pushListener.start();
        } catch (Exception ex) {
            log.error("Error starting the PushListener", ex);
            pushListener.shutdown();
            System.exit(-1);
        }

    }

    /**
     * Returns a string representation of the pushlistener health status
     * @return a string representation of the pushlistener health status
     */
    public String getStatus() {
        return getPushListenerHealthStatus().toString();
    }

    /**
     * Returns the pushlistener health status
     * @return the pushlistener health status
     */
    public PushListenerHealthStatus getPushListenerHealthStatus() {
        PushListenerHealthStatus pushListenerStatus = null;

        RegistryMonitorHealthStatus monitorStatus =
            registryMonitor.getRegistryMonitorHealthStatus();

        ScheduledTaskExecutorHealthStatus taskExecutorStatus =
            taskExecutor.getScheduledTaskExecutorHealthStatus();

        pushListenerStatus = new PushListenerHealthStatus(monitorStatus,
                                                          taskExecutorStatus);

        return pushListenerStatus;
    }

    /**
     * Stops the push listener
     */
    public void stop() {
        //
        // This call forces in any case the ShutDownThread execution so the PushListener
        // is not stopped abruptly
        //
        System.exit(-1);
    }

    /**
     * Cancels execution of the task identified by the given id and removes its
     * scheduled execution
     * @param taskId the id of the task to cancel
     * @return true if the task has been removed successfully, false otherwise (for instance
     *         if the task doesn't exist)
     * @throws PushListenerException if an error occurs
     */
    public boolean removeScheduledTask(long taskId) throws PushListenerException {
        return taskExecutor.removeScheduledTask(taskId);
    }

    /**
     * Verifies if the task with the given id is
     * in the queue
     * @param task the task
     * @return true if the task is in the queue
     * @throws PushListenerException if an error occurs
     */
    public boolean isTaskInQueue(Task task) throws PushListenerException {
        return taskExecutor.isTaskWrapperInQueue(new TaskWrapper(task));
    }

    /**
     * Resets statistic information
     */
    public void resetStatistics() {
        taskExecutor.resetStatistics();
        NotificationWrapper.getInstance().resetStatistics();
        statisticsCollector.resetStatistics();
    }
    
    /**
     * Returns the number of the tasks in the execution queue, that is waiting for execution
     * @return the number of the tasks in the execution queue, that is waiting for execution
     */
    public int getNumberOfQueuedTasks() {
        return taskExecutor.getNumberOfQueuedTasks();
    }

    /**
     * Returns the number of the threads in the pool of the ScheduledTaskExecutor
     * @return the number of the threads in the pool of the ScheduledTaskExecutor
     */
    public int getNumberOfExecutionThreads() {
        return taskExecutor.getCorePoolSize();
    }

    /**
     * Returns the number of the completed tasks
     * @return the number of the completed tasks
     */
    public long getCompletedTaskCount() {
        return taskExecutor.getCompletedTaskCount();
    }

    /**
     * Returns the push listener load factor computed as completedTaskCount/runningTime
     * @return the push listener load factor computed as completedTaskCount/runningTime
     */
    public double getLoadFactor() {
        return taskExecutor.getLoadFactor();
    }
    
    /**
     * Returns the average execution time
     * @return the average execution time
     */
    public double getAverageExecutionTime() {
        return taskExecutor.getAverageExecutionTime();
    }
    
    /**
     * Returns the push listener load (number of tasks completed in the last minute)
     * @return the push listener load (number of tasks completed in the last minute) 
     */
    public long getInstantLoad() {
        return statisticsCollector.getInstantLoad();
    }    

    /**
     * Returns the number of the tasks that are in execution
     * @return the number of the tasks that are in execution
     * for execution
     */
    public int getNumberOfTasksInExecution() {
        return taskExecutor.getActiveCount();
    }

    /**
     * Returns the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     * @return the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     */
    public long getNumberOfHandledTasks() {
        return taskExecutor.getActiveCount() + taskExecutor.getNumberOfQueuedTasks();
    }

    /**
     * Returns the number of notifications
     * @return the number of notifications
     */
    public long getNumberOfNotifications() {
        return NotificationWrapper.getInstance().getNumberOfNotifications();
    }

    /**
     * Returns the number of notifications in the last minute
     * @return the number of notifications in the last minute
     */
    public long getInstantNumberOfNotifications() {
        return statisticsCollector.getInstantNumberOfNotifications();
    }

    /**
     * Returns the number of executions out-of-period
     * @return the number of executions out-of-period
     */
    public long getNumberOfExecutionsOutOfPeriod() {
        return taskExecutor.getNumberOfExecutionsOutOfPeriod();
    }

    /**
     * Returns the number of executions out-of-period in the last minute
     * @return the number of executions out-of-period in the last minute
     */
    public long getInstantNumberOfExecutionsOutOfPeriod() {
        return statisticsCollector.getInstantNumberOfExecutionsOutOfPeriod();
    }

    /**
     * Refresh the task list checking the db for changes
     * @return true if the refresh is performed, false otherwise (if the RegistryMonitor
     *         is refreshing the list)
     * @throws PushListenerException if an error occurs
     */
    public boolean refreshTaskList() throws PushListenerException {
        return registryMonitor.refreshEntries();
    }

    /**
     * Executes the given task.
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod().
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     *
     * @param task the task to execute
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (the same task or another equal task is waiting for its execution or
     *          is already running)
     */
    public boolean executeTask(Task task, long delay) {
        return taskExecutor.executeTaskWrapper(new TaskWrapper(task), delay);
    }

    /**
     * Executes the given task.
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod().
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     *
     * @param task the task to execute
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (the same task or another equal task is waiting for its execution or
     *          is already running)
     */
    public boolean executeTask(Task task) {
        return taskExecutor.executeTaskWrapper(new TaskWrapper(task));
    }
    
    /**
     * Called by the Cluster when the cluster view changes
     * @param clusterSize the number of the servers in the cluster
     * @param serverIndex the inxed of this server
     */
    public synchronized void clusterChanged(int clusterSize, int serverIndex) {

        if (log.isInfoEnabled()) {
            log.info("Reconfiguring the PushListener with the new cluster information [" +
                    "clusterSize: " + clusterSize + ", serverIndex: " + serverIndex + "]");
        }
        if (log.isTraceEnabled()) {
            log.trace("All plugins will be stopped, the ScheduledTaskExecutor and " +
                    "the RegistryMonitor will shutdown" +
                    " and will be restarted with the new cluster information. After that," +
                    " the plugins will be restarted");
        }

        pushPluginsHandler.stopPushListenerPlugins();

        registryMonitor.shutdown();

        taskExecutor.shutdown();

        registryMonitor.setClusterInformation(clusterSize, serverIndex);

        int maxThreadPoolSize            =
            PushListenerConfiguration.getPushListenerConfigBean().getMaxThreadPoolSize();

        double periodTolerance           =
            PushListenerConfiguration.getPushListenerConfigBean().getTaskPeriodTolerance();

        //
        // Creating a new ScheduledTaskExecutor, there is not way to restart the
        // previous one
        //
        taskExecutor = new ScheduledTaskExecutor(maxThreadPoolSize,
                                                 periodTolerance);

        registryMonitor.setScheduledTaskExecutor(taskExecutor);
        registryMonitor.scheduleAndStart();

        pushPluginsHandler.startPushListenerPlugins();

        //
        // This is required since when the cluster changes, a new executor is created
        // so the completedTaskCount restarts from 0 and so also the previouslyCompletedTask
        // must be set to 0.
        //
        statisticsCollector.resetExecutorInformation();
    }

    // ------------------------------------------------------- Protected methods

    /**
     * Initializes the PushListener
     * @throws java.lang.Exception if an error occurs
     */
    protected void init() throws Exception {

        if (log.isInfoEnabled()) {
            log.info("Initializing PushListener...");
        }

        ClusterConfiguration clusterConfiguration =
                PushListenerConfiguration.getPushListenerConfigBean().getClusterConfiguration();
        
        if (clusterConfiguration != null) {
            Cluster.getCluster().init(clusterConfiguration);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Cluster configuration not found, no cluster will be initialized");
            }
        }

        int maxThreadPoolSize            =
            PushListenerConfiguration.getPushListenerConfigBean().getMaxThreadPoolSize();

        double periodTolerance           =
            PushListenerConfiguration.getPushListenerConfigBean().getTaskPeriodTolerance();

        long registryMonitorPollingTime  =
            PushListenerConfiguration.getPushListenerConfigBean().getRegistryMonitorPollingTime();

        long healthThreadPollingTime =
            PushListenerConfiguration.getPushListenerConfigBean().getHealthThreadPollingTime();

        String registryTableName        =
            PushListenerConfiguration.getPushListenerConfigBean().getRegistryTableName();

        String registryEntriesIdSpace   =
            PushListenerConfiguration.getPushListenerConfigBean().getRegistryEntriesIdSpace();

        taskExecutor = new ScheduledTaskExecutor(maxThreadPoolSize,
                                                 periodTolerance);

        registryMonitor = new RegistryMonitor(Cluster.getCluster().getClusterSize(),
                                              Cluster.getCluster().getServerIndex(),
                                              registryMonitorPollingTime,
                                              registryTableName,
                                              registryEntriesIdSpace,
                                              PushListenerConfiguration.getConfigPath(),
                                              taskExecutor);

        healthThread = new HealthThread(this, healthThreadPollingTime);

        pushPluginsHandler =
                new PushListenerPluginsHandler(this);

        pushPluginsHandler.initPushListenerPlugins();

        statisticsCollector = new StatisticsCollector(this);       
    
        
        Cluster.getCluster().addClusterListener(this);

        registerMBeans();

        if (log.isTraceEnabled()) {
            log.trace("RegistryMonitor: "       + registryMonitor);
            log.trace("ScheduledTaskExecutor: " + taskExecutor);
            log.trace("PushPluginsHandler: "    + pushPluginsHandler);
        }

        //
        // In this way a MBean for thread handling is registered and could be
        // used by the management tools
        //
        ThreadToolsMBean threadTools = new ThreadToolsMBean();
    }

    protected void registerMBeans() {

        if (log.isTraceEnabled()) {
            log.trace("Registering MBean: " + serviceDescriptor.getMbeanName());
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            // Construct the ObjectName for the MBean we will register
            ObjectName name = new ObjectName(serviceDescriptor.getMbeanName());

            mbs.registerMBean(this, name);

        } catch (Exception ex) {
            log.error("Error registering mbean '" + serviceDescriptor.getMbeanName() + "'",
                      ex);
        }

        if (log.isTraceEnabled()) {
            log.trace("Registering MBean: " + StatusMXBean.MBEAN_NAME);
        }

        try {
            StatusMXBean statusBean = new StatusMXBeanImpl(this);
            mbs.registerMBean(statusBean, new ObjectName(StatusMXBean.MBEAN_NAME));
        } catch (Exception ex) {
            log.error("Error registering mbean '" + StatusMXBean.MBEAN_NAME + "'", ex);
        }
    }

    /**
     * Starts the listener
     * @throws java.lang.Exception if an error occurs
     */
    protected void start() throws Exception {

        Runtime.getRuntime().addShutdownHook(new ShutDownThread(this));

        //
        // Notifies the plugins that the push listener is starting
        //
        this.pushPluginsHandler.notifyStartPushListener();

        this.healthThread.scheduleAndStart();
        this.registryMonitor.scheduleAndStart();
        //
        // This must be done after the other components because
        // otherwise the plugins could interact with the PushListener that is
        // in a dirty state (not fully started)
        //
        this.pushPluginsHandler.startPushListenerPlugins();

        this.statisticsCollector.start();
    }

    /**
     * Shuts down the pushlistener and its components
     */
    protected void shutdown() {

        if (log.isInfoEnabled()) {
            log.info("Shutting down PushListener...");
        }

        if (statisticsCollector != null) {
            statisticsCollector.stop();
        }

        if (pushPluginsHandler != null) {
            //
            // This must be done before shutting down the other components because
            // otherwise the plugins could interact with the PushListener that is
            // in a dirty state
            //
            pushPluginsHandler.stopPushListenerPlugins();
        }

        if (registryMonitor != null) {
            registryMonitor.shutdown();
        }
        if (taskExecutor != null) {
            taskExecutor.shutdown();
        }
        if (healthThread != null) {
            healthThread.shutdown();
        }

        if (pushPluginsHandler != null) {
            pushPluginsHandler.notifyShutdownPushListener();
        }

        DataSourceContextHelper.closeDataSources();
    }

}

/**
 * A virtual-machine shutdown hook
 */
class ShutDownThread extends Thread {

    /** The PushListener instance */
    private PushListener listener = null;

    public ShutDownThread(PushListener listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        listener.shutdown();
    }

}


