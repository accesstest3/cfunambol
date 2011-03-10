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

package com.funambol.pushlistener.service.taskexecutor;

import java.util.concurrent.locks.Lock;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.logging.LogContext;

import com.funambol.pushlistener.framework.ScheduledTask;
import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.pushlistener.framework.TaskException;

/**
 * Represents a task performed by the PushListener. It's a wrapper around a
 * <code>com.funambol.pushlistener.framework.Task</code>
 *
 * @version $Id: ScheduledTaskWrapper.java,v 1.3 2008-05-18 16:23:42 nichele Exp $
 */
public class ScheduledTaskWrapper implements Runnable {

    // --------------------------------------------------------------- Constants
    /** Name of the property used to store the entry id in the MDC context */
    private static final String LOG_MDC_ENTRY_ID = "entryId"; 

    /**
     * Task states
     */
    public enum State {
        CREATED, CONFIGURED, SCHEDULED, RUNNING, SHUTDOWN;
    }

    // ------------------------------------------------------------ Private data
    /**
     * The configuration object
     */
    private TaskConfiguration conf;


    // -------------------------------------------------------------- Properties

    /** The task id */
    private long id;

    public long getId() {
        return id;
    }

    /** The wrapped task */
    private ScheduledTask task;

    /** The end time of the last execution */
    private long lastExecutionEndTime;

    public long getLastExecutionEndTime() {
        return lastExecutionEndTime;
    }

    public void setLastExecutionEndTime(long time) {
        this.lastExecutionEndTime = time;
    }

    /** The start time of the last execution */
    private long lastExecutionStartTime;

    public long getLastExecutionStartTime() {
        return lastExecutionStartTime;
    }

    public void setLastExecutionStartTime(long time) {
        this.lastExecutionStartTime = time;
    }

    /** The elapsed time from the last execution start time*/
    private long lastExecutionElapsedTime;

    public long getLastExecutionElapsedTime() {
        return lastExecutionElapsedTime;
    }

    /** The last execution time */
    private long lastTaskElapsedTime;

    public long getLastTaskElapsedTime() {
        return lastTaskElapsedTime;
    }

    /** The task state */
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    /**
     * The executionLock. The execution doesn't start if the lock is keeped by
     * another thread
     */
    private Lock executionLock = null;

    public Lock getExecutionLock() {
        return executionLock;
    }

    public void setExecutionLock(Lock executionLock) {
        this.executionLock = executionLock;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new ScheduledTaskWrapper
     *
     * @param id the task id
     * @param conf the configuration object
     * @param task the wrapped task
     */
    public ScheduledTaskWrapper(long id, TaskConfiguration conf, ScheduledTask task) {
        this(id);
        this.conf = conf;
        this.task = task;
    }

    /**
     * Creates a new ScheduledTaskWrapper
     *
     * @param id the id
     */
    public ScheduledTaskWrapper(long id) {
        this.id    = id;
        this.state = state.CREATED;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the period
     */
    public long getPeriod() {
        return task.getPeriod();
    }

    /**
     * Shuts down the wrapped task
     * @throws TaskException if an error occurs
     */
    public void shutdown() throws TaskException {
        task.shutdown();
    }

    /**
     * Configures the wrapped task
     * @throws TaskException if an error occurs
     */
    public void configure() throws TaskException {
        task.configure(conf);
    }

    /**
     * Returns true if the task is currently running
     * @return true if the task is currently running, false otherwise
     */
    public boolean isRunning() {
        return State.RUNNING.equals(state);
    }

    // -------------------------------------------------------- Override methods

    /**
     * Runs the wrapped task calling <code>task.execute()</code>
     */
    public void run() {
        LogContext.clear();
        LogContext.set(LOG_MDC_ENTRY_ID, this.getId());

        this.lastExecutionStartTime = System.currentTimeMillis();
        if (this.lastExecutionEndTime != 0) {
            this.lastExecutionElapsedTime = this.lastExecutionStartTime - this.lastExecutionEndTime;
        }

        //
        // We get the current state (storing it in previousState) because before
        // to execute it, we set its status to RUNNING, and after the execution
        // we re-set the original state
        //
        State previousState = this.state;
        try {

            if (executionLock != null) {
                executionLock.lock();
            }

            this.state = State.RUNNING;

            task.execute();
            //
            // Reset previous state
            //
            this.state = previousState;


        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } finally {
            if (executionLock != null) {
                executionLock.unlock();
            }
            this.lastExecutionEndTime = System.currentTimeMillis();
            this.lastTaskElapsedTime  = this.lastExecutionEndTime - this.lastExecutionStartTime;
            this.state                = previousState;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScheduledTaskWrapper)) {
            return false;
        }
        ScheduledTaskWrapper stask = (ScheduledTaskWrapper) o;
        return this.id == stask.id;
    }

    @Override
    public int hashCode() {
        return (new Long(id)).hashCode();
    }

    /**
     * A string representation of this ScheduledTaskWrapper
     *
     * @return a string representation of this ScheduledTaskWrapper
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("task" , task);
        sb.append("id"   , id);
        sb.append("state", state);
        return sb.toString();
    }
}