/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.pushlistener.service.registry;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;

import com.funambol.pushlistener.framework.ScheduledTask;
import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.pushlistener.framework.TaskException;

import com.funambol.pushlistener.service.BaseThreadFactory;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;
import com.funambol.pushlistener.service.registry.dao.RegistryDao;
import com.funambol.pushlistener.service.taskexecutor.ScheduledTaskExecutor;
import com.funambol.pushlistener.service.taskexecutor.ScheduledTaskWrapper;

/**
 * It's a thread that periodically checks the registry for new/updated/deleted
 * entries.
 *
 * It is created with a clusterSize and a serverIndex; these values are used to
 * segment the  entries in the database in order to distribute the load between different instances.
 * <br>A RegistryMonitor works with the entries with <code>id % clusterSize == serverIndex</code>.
 * <br>In this way, an entry is handle just by an instance and all instances are
 * handled.
 *
 * @version $Id: RegistryMonitor.java,v 1.20 2008-07-16 16:06:07 nichele Exp $
 */
public class RegistryMonitor implements Runnable{

    /**
     * RegistryMonitor states
     */
    public enum State {
        UNDEFINED, // not created yet
        CREATED,   // not running yet
        PAUSED,    // between two execution
        RUNNING;   // in execution
    }

    // ------------------------------------------------------------ Private data

    /** The logger */
    private static final Logger log = Logger.getLogger("funambol.pushlistener.registrymonitor");

    /** The dao to read the registry */
    private RegistryDao dao = null;

    /** The last execution time */
    private long lastExecutionTime = 0;

    /**
     * The ScheduledTaskExecutor used to perform the scheduled tasks.
     * This is needed because the monitor must add/update/remove scheduled task
     */
    private ScheduledTaskExecutor scheduledTaskExecutor = null;

    /** The Executor used to run the RegistryMonitor */
    private RegistryMonitorExecutor executor = null;

    /**
     * The configPath. It's the path where to read the bean file of the task
     * to perform
     */
    private String configPath;

    /** The polling time on the registry table */
    private long pollingTime;

    /** The index of this server in a cluster environment */
    private int serverIndex;

    /** The total number of PushListener in a cluster environment */
    private int clusterSize;

    /**
     * The RegistryMonitor state. Its changes between RUNNING and PAUSED are
     * handled by the RegistryMonitorExecutor in beforeExecute and in afterExecute
     */
    private State state = State.UNDEFINED;

    /**
     * Allows to cancel an execution
     */
    private ScheduledFuture scheduledFuture = null;

    /**
     * Needed when a new DAO must be recreated if the cluster changes
     */
    private String registryTableName = null;

    /**
     * The idSpace to use to create the registry entries
     */
    private String registryEntriesIdSpace = null;


    // -------------------------------------------------------------- Properties

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a RegistryMonitor with the given parameters. See class desccription
     * for more info about clusterSize and serverIndex
     *
     * @param clusterSize the number of the servers in the cluster
     * @param serverIndex the index of this server
     * @param pollingTime the polling time
     * @param registryTableName the registry table name
     * @param registryEntriesIdSpace the idSpace to use to create the registry entries
     * @param configPath the path where to read the bean file
     * @param taskExecutor the task executor (needed to add/update/delete the tasks)
     */
    public RegistryMonitor(int    clusterSize,
                           int    serverIndex,
                           long   pollingTime,
                           String registryTableName,
                           String registryEntriesIdSpace,
                           String configPath,
                           ScheduledTaskExecutor taskExecutor) {

        this.pollingTime            = pollingTime;
        this.scheduledTaskExecutor  = taskExecutor;
        this.configPath             = configPath;
        this.registryTableName      = registryTableName;
        this.registryEntriesIdSpace = registryEntriesIdSpace;
        this.clusterSize            = clusterSize;
        this.serverIndex            = serverIndex;

        if (log.isInfoEnabled()) {
            log.info("Creating a RegistryMonitor instance with polling time: " +
                      pollingTime + ", clusterSize: " + clusterSize + ", serverIndex: " + serverIndex +
                      " [this: " + this + "]");
        }

        dao = new RegistryDao(registryTableName,
                              registryEntriesIdSpace,
                              clusterSize,
                              serverIndex);

        this.state = State.CREATED;

    }

    // ---------------------------------------------------------- Public methods


    /**
     * Sets a new ScheduledTaskExecutor.
     * <br>
     * Note that this method must be synchronized in order to be sure that the
     * scheduledTaskExecutor is not changed while the monitor is checking the entries.
     * (to obtain that, also the method 'run' is synchronized).
     *
     * @param taskExecutor the new ScheduledTaskExecutor
     */
    public synchronized void setScheduledTaskExecutor(ScheduledTaskExecutor taskExecutor) {
        this.scheduledTaskExecutor = taskExecutor;
    }

    /**
     * Returns the polling time
     * @return the polling time
     */
    public long getPollingTime() {
        return pollingTime;
    }

    /**
     * Schedules and start the registry monitor
     */
    public void scheduleAndStart() {
        if (log.isInfoEnabled()){
            log.info("Starting RegistryMonitor (pollingTime: " +
                      pollingTime + " milliseconds, clusterSize: " + clusterSize +
                      ", serverIndex:" + serverIndex +
                      ") [this: " + this + "]");
        }
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
        executor = new RegistryMonitorExecutor(1, // 1 Thread
                                               this,
                                               new BaseThreadFactory("registry-monitor")
                                               );

        scheduledFuture = executor.scheduleWithFixedDelay(this,
                                                          0,
                                                          pollingTime,
                                                          TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the health status of the registry monitor
     * @return the health status of the registry monitor
     */
    public RegistryMonitorHealthStatus getRegistryMonitorHealthStatus() {
        RegistryMonitorHealthStatus status = null;

        int     poolSize              = executor.getPoolSize();
        long    completedTaskCount    = executor.getCompletedTaskCount();
        long    taskCount             = executor.getTaskCount();
        int     activeCount           = executor.getActiveCount();
        Queue   registryMonitorQueue  = executor.getQueue();
        boolean shutDown              = executor.isShutdown();

        int taskQueued             = 0;
        if (registryMonitorQueue != null) {
            taskQueued = registryMonitorQueue.size();
        }

        status =
            new RegistryMonitorHealthStatus(((Object)this).toString(),
                                            poolSize,
                                            activeCount,
                                            completedTaskCount,
                                            taskCount,
                                            taskQueued,
                                            shutDown);

        return status;
    }

    /**
     * Refresh the entries list
     * @return true if the refresh is performed, false otherwise (if the RegistryMonitor
     *         is running)
     */
    public boolean refreshEntries() {
        if (State.RUNNING.equals(state)) {
            //
            // The registry monitor is running, we can not force a new execution
            //
            return false;
        }
        scheduledFuture.cancel(true);
        scheduleAndStart();
        return true;
    }

    /**
     * Performs the check of the registry.
     * <br>
     * Note that this method must be synchronized in order to be sure that no other
     * class can change the scheduledTaskExecutor calling setScheduledTaskExecutor
     * method (also that method is synchronized) in the meantime.
     */
    public synchronized void run() {

        if(log.isTraceEnabled()){
            log.trace("Checking registry...");
            log.trace("Task executor: " + scheduledTaskExecutor + ", num. task: "
                      + scheduledTaskExecutor.getQueue().size());
        }

        List<RegistryEntry> newEntries     = null;
        List<RegistryEntry> updatedEntries = null;
        List<RegistryEntry> deletedEntries = null;

        try {

            if (lastExecutionTime == 0){
                //
                // This is the first time.
                // Extracting all the entries that are active and not D.
                //
                newEntries = geAllEntries();
            } else {
                newEntries     = getNewEntries();
                updatedEntries = getUpdatedEntries();
                deletedEntries = getDeletedEntries();
            }

        } catch (DataAccessException ex) {
            //log.error("Error reading the registry", ex);
            throw new RuntimeException(ex);
        }

        //
        // New tasks must be created only if there is at least one new
        // entry
        //
        if (newEntries != null && !newEntries.isEmpty()) {
            for (RegistryEntry entry : newEntries) {

                //
                // Following algorithm is performed:
                //
                // if (active == true) {
                //   create a new scheduled task configured read the bean file
                //   configure the task
                //   schedule the task
                // } else {
                //   remove the task from the list of the scheduled task
                // }
                //
                long id = entry.getId();

                if (entry.getActive()) {
                    if (log.isTraceEnabled()) {
                        log.trace("Scheduling entry: " + entry + " [this: " + this + "]");
                    }
                    ScheduledTask task = null;
                    try {
                        task = getTask(entry.getTaskBeanFile());
                    } catch (Exception ex) {
                        throw new RuntimeException("Error creating task '" + entry.getId() + "'", ex);
                    }
                    TaskConfiguration configuration = getTaskConfiguration(entry);
                    ScheduledTaskWrapper scheduledTask = new ScheduledTaskWrapper(id, configuration, task);
                    try {
                        scheduledTask.configure();
                    } catch (TaskException ex) {
                        log.error("Unable to configure task: " + scheduledTask +
                                  ". This task will be not scheduled ", ex);
                        continue;
                    }
                    scheduledTask.setState(ScheduledTaskWrapper.State.CONFIGURED);
                    scheduledTaskExecutor.scheduleTask(scheduledTask);

                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Entry '" + entry + "' not active");
                    }
                }
            }
        }

        //
        // Handling modified configurations
        //
        if(updatedEntries != null && !updatedEntries.isEmpty()){
            for (RegistryEntry entry : updatedEntries) {

                long id = entry.getId();
                if (entry.getActive()) {

                    if (log.isTraceEnabled()) {
                        log.trace("Updating entry: " + entry);
                    }

                    ScheduledTask task = null;
                    try {
                        task = getTask(entry.getTaskBeanFile());
                    } catch (Exception ex) {
                        throw new RuntimeException("Error creating task '" + entry.getId() + "'", ex);
                    }
                    TaskConfiguration configuration = getTaskConfiguration(entry);
                    ScheduledTaskWrapper stask = new ScheduledTaskWrapper(id, configuration, task);
                    try {
                        stask.configure();
                    } catch (TaskException ex) {
                        log.error("Unable to configure task: " + stask +
                                  ". This task will be not scheduled ", ex);
                        continue;
                    }
                    stask.setState(ScheduledTaskWrapper.State.CONFIGURED);
                    scheduledTaskExecutor.updateScheduledTask(stask);

                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Entry '" + entry + "' not active, removing it");
                    }
                    scheduledTaskExecutor.removeScheduledTask(id);
                }
            }
        }

        //
        // Handling deleted configurations
        //
        if(updatedEntries != null && !deletedEntries.isEmpty()){
            for (RegistryEntry entry : deletedEntries) {
                if (log.isTraceEnabled()) {
                    log.trace("Entry '" + entry + "' deleted, removing it");
                }

                long id = entry.getId();
                scheduledTaskExecutor.removeScheduledTask(id);

            }
        }

        //
        // Removing (definitely) deleted entries from the db
        //
        try {
            int numDeletedRows = dao.removeAllDeletedRegistryEntry();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex);
        }

        //
        // Sets the last execution time
        //
        lastExecutionTime = System.currentTimeMillis();
        this.state = State.PAUSED;
    }

    /**
     * Shuts down the underlying executor
     */
    public void shutdown() {
        if (log.isInfoEnabled()) {
            log.info("Shutting down RegistryMonitor [" + this + "]...");
        }
        executor.shutdown();
        this.lastExecutionTime = 0;
        if (log.isInfoEnabled()) {
            log.info("RegistryMonitor  [" + this + "] shut down");
        }

    }

    /**
     * Called when the cluster changes
     * @param clusterSize the number of the servers in the cluster
     * @param serverIndex the index of the local member
     */
    public void setClusterInformation(int clusterSize, int serverIndex) {
        this.clusterSize = clusterSize;
        this.serverIndex = serverIndex;

        dao = new RegistryDao(registryTableName, registryEntriesIdSpace, clusterSize, serverIndex);
    }

    // ------------------------------------------------------- Protected methods
    /**
     * Changes the registry monitor state
     * @param newState the new state
     */
    protected void changeState(State newState) {
        this.state = newState;
    }
    // --------------------------------------------------------- Private methods

    /**
     * Creates the ScheduledTask object reading the bean file with the given beanFileName
     * @param beanFileName the bean file name
     * @return the Task object reading the bean file with the given beanFileName
     * @throws com.funambol.framework.tools.beans.BeanException if an error occurs
     */
    private ScheduledTask getTask(String beanFileName) throws BeanException {
        return (ScheduledTask)BeanTool.getBeanTool(configPath).getNewBeanInstance(beanFileName);
    }

    /**
     * Returns a TaskConfiguration created using the data in the given RegistryEntry
     * @param entry the RegistryEntry
     * @return a TaskConfiguration created using the data in the given RegistryEntry
     */
    private TaskConfiguration getTaskConfiguration(RegistryEntry entry) {
        if (entry == null) {
            return null;
        }
        TaskConfiguration conf = new TaskConfiguration();
        conf.setActive(entry.getActive());
        conf.setId(entry.getId());
        conf.setPeriod(entry.getPeriod());
        conf.setTaskBeanFile(entry.getTaskBeanFile());
        conf.setProperties(entry.getProperties());
        return conf;
    }

    /**
     * Returns all active entries
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return a list with all entries
     */
    private List<RegistryEntry> geAllEntries() throws DataAccessException {
        List<RegistryEntry> entries = null;

        if (log.isTraceEnabled()) {
            log.trace("Loading all entries with cluster size: " +
                      clusterSize + ", server index: " + serverIndex + " [this:" + this + "]");
        }

        entries = dao.getActiveEntries();

        if (log.isTraceEnabled()) {
            log.trace("Loaded " + entries.size() + " entries with cluster size: " +
                      clusterSize + ", server index: " + serverIndex + " [this:" + this + "]");
        }
        return entries;
    }

    /**
     * Returns new entries
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occcurs
     * @return a list with the new entries
     */
    private List<RegistryEntry> getNewEntries() throws DataAccessException {
        List<RegistryEntry> entries = null;

        entries = dao.getEntries(lastExecutionTime,
                                 RegistryEntryStatus.NEW);

        if (log.isTraceEnabled()) {
            int size = entries.size();
            if (size > 1) {
                log.trace("Detected " + size + " new entries");
            } else if (size == 1) {
                log.trace("Detected 1 new entry");
            } else {
                log.trace("No new entry detected");
            }
        }

        return entries;
    }

    /**
     * Returns updated entries
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return a list with the updated entries
     */
    private List<RegistryEntry> getUpdatedEntries() throws DataAccessException {
        List<RegistryEntry> entries = null;

        entries = dao.getEntries(lastExecutionTime,
                                 RegistryEntryStatus.UPDATED);

        if (log.isTraceEnabled()) {
            int size = entries.size();
            if (size > 1) {
                log.trace("Detected " + size + " updated entries");
            } else if (size == 1) {
                log.trace("Detected 1 updated entry");
            } else {
                log.trace("No updated entry detected");
            }
        }

        return entries;
    }

    /**
     * Returns deleted tasks
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return a list with the deleted tasks
     */
    private List<RegistryEntry> getDeletedEntries() throws DataAccessException {
        List<RegistryEntry> entries = null;

        entries = dao.getEntries(lastExecutionTime,
                                 RegistryEntryStatus.DELETED);

        if (log.isTraceEnabled()) {
            int size = entries.size();
            if (size > 1) {
                log.trace("Detected " + size + " deleted entries");
            } else if (size == 1) {
                log.trace("Detected 1 deleted entry");
            } else {
                log.trace("No deleted entry detected");
            }
        }
        return entries;
    }

}