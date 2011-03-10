/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.server.notification;

/**
 * The Management Bean interface for the PushManager
 * @version $Id: $
 */
public interface PushManagerMBean {

    /**
     * The amount of queued pending notification requests waiting to be
     * served
     * @return the amount of queued pending notification requests
     */
    public int getQueuedPushCount();

    /**
     * The amount of notification pushes currently under delivery
     * @return The amount of notification pushes currently under delivery
     */
    public int getCurrentPushInProgressCount();

    /**
     * The amount of threads (serving a request or idle) currently in the thread
     * pool
     * @return The amount of threads currently in the thread pool
     */
    public int getThreadPoolSize();

    /**
     * The configured core thread pool size
     * @return The configured core thread pool size
     */
    public int getCoreThreadPoolSize();

    /**
     * Set the configured core thread pool size
     * @param coreThreadPoolSize The core thread pool size to be used
     */
    public void setCoreThreadPoolSize(int coreThreadPoolSize);

    /**
     * The configured maximum thread pool size
     * @return the configured maximum thread pool size
     */
    public int getMaximumThreadPoolSize();

    /**
     * Set the configured maximum thread pool size
     * @param maximumThreadPoolSize the ximum thread pool size to be used
     */
    public void setMaximumThreadPoolSize(int maximumThreadPoolSize);

    /**
     * The configured keep alive time in seconds for idle threads in the thread
     * pool
     * @return the configured keep alive time in seconds for idle threads in the
     * thread pool
     */
    public long getThreadKeepAliveTime();

    /**
     * Set the keep alive time in seconds for idle threads in the thread pool
     * @param threadKeepAliveTime The keep alive time in seconds to be used
     */
    public void setThreadKeepAliveTime(long threadKeepAliveTime);

    /**
     * The task queue capacity
     * @return the task queue capacity
     */
    public int getQueueCapacity();

    /**
     * The total number of push messages sent since the beginning or the last
     * ResetStatistics
     * @return
     */
    public long getSentPushCount();

    /**
     * Reset the statistic values
     */
    public void resetStatistics();
}
