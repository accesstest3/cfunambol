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

import com.funambol.pushlistener.service.*;

/**
 * The health status of the RegistryMonitor
 * @version $Id: RegistryMonitorHealthStatus.java,v 1.4 2007-11-28 11:15:04 nichele Exp $
 */
public class RegistryMonitorHealthStatus {

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

    private long taskCount = -1;
    public long getTaskCount() {
        return taskCount;
    }

    private int  taskQueued = -1;
    public int getTaskQueued() {
        return taskQueued;
    }

    private boolean shutDown;

    public boolean isShutDown() {
        return shutDown;
    }

    private String registryMonitorToString;

    public String getRegistryMonitorToString() {
        return registryMonitorToString;
    }

    // ------------------------------------------------------------- Constructor

    /** Creates a new instance of RegistryMonitorHealthStatus */
    public RegistryMonitorHealthStatus(String  registryMonitorToString,
                                       int     poolSize,
                                       int     activeCount,
                                       long    completedTaskCount,
                                       long    taskCount,
                                       int     taskQueued,
                                       boolean shutDown) {

        this.registryMonitorToString = registryMonitorToString;
        this.poolSize                = poolSize;
        this.activeCount             = activeCount;
        this.completedTaskCount      = completedTaskCount;
        this.taskCount               = taskCount;
        this.taskQueued              = taskQueued;
        this.shutDown                = shutDown;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("RegistryMonitor status:");
        sb.append("\n\t> instance: ")
          .append(getRegistryMonitorToString())
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
