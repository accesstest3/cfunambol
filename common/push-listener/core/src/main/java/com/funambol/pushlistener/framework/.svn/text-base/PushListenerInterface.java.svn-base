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

package com.funambol.pushlistener.framework;

/**
 * This is the interface exposed by the PushListener
 *
 * @version $Id: PushListenerInterface.java,v 1.4 2008-02-25 15:48:25 luigiafassina Exp $
 */
public interface PushListenerInterface {

    // ---------------------------------------------------------- Public methods
    /**
     * Returns a string representation of the health status of a PushListener
     * @return the health status
     */
    public String getStatus();

    /**
     * Stops the pushlistener
     */
    public void stop();

    /**
     * Cancels execution of the task identified by the given id and removes its
     * scheduled execution
     * @param taskId the id of the task to cancel
     * @return true if the task has been removed successfully, false otherwise (for instance
     *         if the task doesn't exist)
     * @throws PushListenerException if an error occurs
     */
    public boolean removeScheduledTask(long taskId) throws PushListenerException;

    /**
     * Verifies if the task with the given id is in the queue
     * @param task the task
     * @return true if the task is in the queue
     * @throws PushListenerException if an error occurs
     */
    public boolean isTaskInQueue(Task task) throws PushListenerException;

    /**
     * Resets statistic information
     * @throws PushListenerException if an error occurs
     */
    public void resetStatistics() throws PushListenerException;

    /**
     * Returns the number of the tasks that are in the execution queue
     * @return the number of the tasks that are in the execution queue, that is waiting
     * for execution
     */
    public int getNumberOfQueuedTasks();

    /**
     * Returns the number of the tasks that are in execution
     * @return the number of the tasks that are in execution
     * for execution
     */
    public int getNumberOfTasksInExecution();

    /**
     * Returns the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     * @return the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     */
    public long getNumberOfHandledTasks();

    /**
     * Returns the number of the threads in the pool of the ScheduledTaskExecutor
     * @return the number of the threads in the pool of the ScheduledTaskExecutor
     */
    public int getNumberOfExecutionThreads();

    /**
     * Returns the number of the completed tasks
     * @return the number of the completed tasks
     */
    public long getCompletedTaskCount();

    /**
     * Returns the push listener load factor computed as completedTaskCount/runningTime
     * @return the push listener load factor computed as completedTaskCount/runningTime
     */
    public double getLoadFactor();

    /**
     * Returns the push listener load (number of tasks completed in the last minute)
     * @return the push listener load (number of tasks completed in the last minute) 
     */
    public long getInstantLoad();

    /**
     * Returns the number of notifications
     * @return the number of notifications
     */
    public long getNumberOfNotifications();

    /**
     * Returns the number of notifications in the last minute
     * @return the number of notifications in the last minute
     */
    public long getInstantNumberOfNotifications();

    /**
     * Returns the average execution time
     * @return the average execution time
     */
    public double getAverageExecutionTime();

    /**
     * Returns the number of executions out-of-period
     * @return the number of executions out-of-period
     */
    public long getNumberOfExecutionsOutOfPeriod();

    /**
     * Returns the number of executions out-of-period in the last minute
     * @return the number of executions out-of-period in the last minute
     */
    public long getInstantNumberOfExecutionsOutOfPeriod();
    
    /**
     * Refresh the tasks list checking the db for changes.
     * @return true if the refresh has been scheduled for an ASAP execution
     *         successfully, false otherwise (if the RegistryMonitor is just now
     *         refreshing the list)
     * @throws PushListenerException if an error occurs
     */
    public boolean refreshTaskList() throws PushListenerException;

    /**
     * Executes the given task.
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod().
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     *
     * @param task the task to execute
     * @throws PushListenerException if an error occurs
     *
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (the same task or another equal task is waiting for its execution or
     *          is already running)
     */
    public boolean executeTask(Task task) throws PushListenerException;

    /**
     * Executes the given task with the given delay
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod().
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     *
     * @param task the task to execute
     * @param delay the delay in the task execution
     * @throws PushListenerException if an error occurs
     *
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (the same task or another equal task is waiting for its execution or
     *          is already running)
     */
    public boolean executeTask(Task task, long delay) throws PushListenerException;
}
