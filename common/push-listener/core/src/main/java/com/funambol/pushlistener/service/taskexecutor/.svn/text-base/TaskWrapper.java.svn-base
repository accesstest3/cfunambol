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

import com.funambol.pushlistener.framework.Task;
import java.util.concurrent.locks.Lock;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a task performed by the PushListener. It's a wrapper around a
 * <code>com.funambol.pushlistener.framework.Task</code>
 *
 * @version $Id: TaskWrapper.java,v 1.2 2007-11-28 11:15:04 nichele Exp $
 */
public class TaskWrapper implements Runnable {

    /**
     * Task states
     */
    public enum State {
        CREATED, RUNNING;
    }

    // -------------------------------------------------------------- Properties

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

    /** The last execution time */
    private long lastTaskElapsedTime;

    public long getLastTaskElapsedTime() {
        return lastTaskElapsedTime;
    }

    // ------------------------------------------------------------ Private data
    /** The wrapped task */
    private Task task;

    /** Must the task be re-done ? */
    private boolean mustBeRedone = false;

    /** 
     * Is the mustBeRedone already verified ? If so, no other requests will be
     * accepted.
     */
    private boolean alreadyVerifiedForRedo = false;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new TaskWrapper
     *
     * @param task the wrapped task
     */
    public TaskWrapper(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("The task must be not null");
        }
        this.task = task;
        this.state = state.CREATED;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Called to ask a new execution. If the task is not completed and the variable
     * alreadyVerifiedForRedo is still false, the method returns true that is the
     * new execution request has been accept. Note that if the task is not running
     * (still waits its execution) this method accepts the request must this request
     * will not generate a new execution (there is already one pending).
     *
     *<p>Note that this method should synchronized with <code>needRedo()</code>.
     * BTW, they are not, because the caller methods use a taskLock to handle them
     * (see ScheduledTaskExecutor.afterTaskExecution and ScheduledTaskExecutor.executeTask)
     *
     * @return true if the request has been accepted, false otherwise.
     */
    public boolean queueNewExecution() {
        if (mustBeRedone) {
            return true;
        }
        if (alreadyVerifiedForRedo) {
            return false;
        }
        mustBeRedone = true;
        return true;
    }


    /**
     * Must the given task be redone ?
     * <p>Note that this method should synchronized with <code>queueNewExecution()</code>.
     * BTW, they are not, because the caller methods use a taskLock to handle them
     * (see ScheduledTaskExecutor.afterTaskExecution and ScheduledTaskExecutor.executeTask)
     * @return true, if the task must be redone, false othrwise
     */
    public boolean needRedo() {
        alreadyVerifiedForRedo = true;
        return mustBeRedone;
    }

    /**
     * Returns true if the task is currently running
     * @return true if the task is currently running, false otherwise
     */
    public boolean isRunning() {
        return State.RUNNING.equals(state);
    }

    /**
     * Returns the execution period. This is the period between two executions
     * (two executions are needed if a request for an execution has been received
     * when another previous/old execution has been running)
     *
     * @return the execution period
     */
    public long getPeriod() {
        return task.getPeriod();
    }

    /**
     * Runs the wrapped task calling <code>task.execute()</code>
     */
    public void run() {

        this.lastExecutionStartTime = System.currentTimeMillis();
        
        //
        // We get the current state (storing it in previousState) because before
        // to execute it, we set its status to RUNNING, and after the execution
        // we re-set the original state
        //
        State previousState = this.state;

        alreadyVerifiedForRedo = false;
        try {

            if (executionLock != null) {
                executionLock.lock();
            }
            this.mustBeRedone = false;
            this.state = State.RUNNING;

            task.execute();

            //
            // Re-set previous state
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
            this.state = previousState;
        }
    }

    // ------------------------------------------------------- Overrided methods

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TaskWrapper)) {
            return false;
        }
        TaskWrapper stask = (TaskWrapper) o;
        return this.task.equals(stask.task);
    }

    @Override
    public int hashCode() {
        return task.hashCode();
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
        sb.append("state", state);
        return sb.toString();
    }
}