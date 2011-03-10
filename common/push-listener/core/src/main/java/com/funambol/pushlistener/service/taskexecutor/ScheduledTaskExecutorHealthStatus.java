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

package com.funambol.pushlistener.service.taskexecutor;

import com.funambol.pushlistener.service.*;

/**
 * The health status of the ScheduledTaskExecutor
 * @version $Id: ScheduledTaskExecutorHealthStatus.java,v 1.4 2007-11-28 11:15:04 nichele Exp $
 */
public class ScheduledTaskExecutorHealthStatus {

    // -------------------------------------------------------------- Properties
    private int poolSize = -1;

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }

    private int activeCount = -1;

    public int getActiveCount() {
        return activeCount;
    }

    private long completedTaskCount = -1;

    public long getCompletedTaskCount() {
        return completedTaskCount;
    }

    public void setCompletedTaskCount(long completedTaskCount) {
        this.completedTaskCount = completedTaskCount;
    }

    private long taskCount = -1;
    public long getTaskCount() {
        return taskCount;
    }

    public void setTaskCount(long taskCount) {
        this.taskCount = taskCount;
    }

    private int  taskQueued = -1;
    public int getTaskQueued() {
        return taskQueued;
    }

    public void setTaskQueue(int taskInQueue) {
        this.taskQueued = taskQueued;
    }

    private boolean shutDown;

    public boolean isShutDown() {
        return shutDown;
    }

    private String scheduledTaskExecutorToString;
    public String getScheduledTaskExecutorToString() {
        return scheduledTaskExecutorToString;
    }

    public void setScheduledTaskExecutorToString(String scheduledTaskExecutorToString) {
        this.scheduledTaskExecutorToString = scheduledTaskExecutorToString;
    }

    // ------------------------------------------------------------- Constructor

    /** Creates a new instance of ScheduledTaskExecutorHealthStatus */
    public ScheduledTaskExecutorHealthStatus(String  scheduledTaskExecutorToString,
                                             int     poolSize,
                                             int     activeCount,
                                             long    completedTaskCount,
                                             long    taskCount,
                                             int     taskQueued,
                                             boolean shutDown) {

        this.scheduledTaskExecutorToString = scheduledTaskExecutorToString;
        this.poolSize                      = poolSize;
        this.activeCount                   = activeCount;
        this.completedTaskCount            = completedTaskCount;
        this.taskCount                     = taskCount;
        this.taskQueued                    = taskQueued;
        this.shutDown                      = shutDown;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ScheduledTaskExecutor status:");
        sb.append("\n\t> instance: ")
          .append(getScheduledTaskExecutorToString())
          .append("\n\t> current number of threads in the pool: ")
          .append(getPoolSize())
          .append("\n\t> number of running tasks: ")
          .append(getActiveCount())
          .append("\n\t> number of scheduled tasks: ")
          .append(getTaskCount())
          .append("\n\t> number of completed tasks: ")
          .append(getCompletedTaskCount())
          .append("\n\t> number of tasks to execute: ")
          .append(getTaskQueued())
          .append("\n\t> is shut down: ")
          .append(isShutDown());
        return sb.toString();
    }
}