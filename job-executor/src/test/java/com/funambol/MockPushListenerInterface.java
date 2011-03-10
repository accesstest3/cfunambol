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

package com.funambol;

import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.PushListenerInterface;
import com.funambol.pushlistener.framework.Task;

/**
 *
 * @version $Id: MockPushListenerInterface.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class MockPushListenerInterface implements PushListenerInterface{
  
    public String getStatus() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void stop() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean removeScheduledTask(long l) throws PushListenerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isTaskInQueue(Task task) throws PushListenerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void resetStatistics() throws PushListenerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberOfQueuedTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberOfTasksInExecution() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getNumberOfHandledTasks() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public int getNumberOfExecutionThreads() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getCompletedTaskCount() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getLoadFactor() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getInstantLoad() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getNumberOfNotifications() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getInstantNumberOfNotifications() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public double getAverageExecutionTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getNumberOfExecutionsOutOfPeriod() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public long getInstantNumberOfExecutionsOutOfPeriod() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean refreshTaskList() throws PushListenerException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean executeTask(Task task) throws PushListenerException {
        task.execute();
        return true;
    }

    public boolean executeTask(Task task, long l) throws PushListenerException {
        task.execute();
        return true;
    }

}
