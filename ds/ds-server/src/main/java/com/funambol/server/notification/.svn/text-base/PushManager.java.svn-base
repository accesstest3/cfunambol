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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.funambol.framework.core.Alert;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.Sync4jLoggerName;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationEngine;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.NotificationNotSentException;
import com.funambol.framework.tools.beans.LazyInitBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Manage the notification requests
 * Collaborate with the NotificationEngine
 *
 * @version $Id: $
 */
public class PushManager implements PushManagerMBean, LazyInitBean {

    // ---------------------------------------------------------------- Constant

    /**
     * The default core size of the thread pool
     */
    public static final int DEFAULT_CORE_POOL_SIZE = 10;

    /**
     * The default maximum size of the thread pool
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 30;

    /**
     * The default keep alive time in seconds for idle threads in the thread pool
     */
    public static final long DEFAULT_KEEP_ALIVE_TIME = 60L;

    /**
     * The keep alive time unit
     */
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    /**
     * The default queue capacity
     */
    public static final int DEFAULT_QUEUE_CAPACITY = Integer.MAX_VALUE;

    /**
     * The name prefix for the threads managing notification requests
     */
    public static final String THREAD_NAME_PREFIX = "funambol-push-thread-";

    // -------------------------------------------------------- Member Variables

    /**
     * The collaborator class to which delegate the notification delivery
     */
    private NotificationEngine notificationEngine;

    /**
     * Pool of threads which are responsable for extracting tasks from the queue
     * and execute them.
     */
    private ThreadPoolExecutor executor;

    /**
     * The number of completed tasks that has to be subtracted from the total
     * number.
     */
    private long previousCompletedTaskCount = 0;

    /**
     * The logger used to log push notification events
     */
    private static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Sync4jLoggerName.SERVER_NOTIFICATION);

    // -------------------------------------------------------------- Parameters

    /**
     * The task queue capacity.
     */
    private int queueCapacity = DEFAULT_QUEUE_CAPACITY;

    public int getQueueCapacity() {
        return queueCapacity;
    }

    public void setQueueCapacity(int queueCapacity) {
        if (queueCapacity < 0) {
            this.queueCapacity = Integer.MAX_VALUE;
        } else {
            this.queueCapacity = queueCapacity;
        }
    }

    /**
     * The configured core thread pool size
     */
    private int coreThreadPoolSize = DEFAULT_CORE_POOL_SIZE;

    /**
     * The configured core thread pool size
     * @return The configured core thread pool size
     */
    public int getCoreThreadPoolSize() {
        return this.executor.getCorePoolSize();
    }

    /**
     * Set the configured core thread pool size
     * @param coreThreadPoolSize The core thread pool size to be used
     */
    public void setCoreThreadPoolSize(int coreThreadPoolSize) {
        this.coreThreadPoolSize = coreThreadPoolSize;
        this.executor.setCorePoolSize(coreThreadPoolSize);
    }

    /**
     * The configured maximum thread pool size
     */
    private int maximumThreadPoolSize = DEFAULT_MAX_POOL_SIZE;

    /**
     * The configured maximum thread pool size
     * @return the configured maximum thread pool size
     */
    public int getMaximumThreadPoolSize() {
        return this.executor.getMaximumPoolSize();
    }

    /**
     * Set the configured maximum thread pool size
     * @param maximumThreadPoolSize the ximum thread pool size to be used
     */
    public void setMaximumThreadPoolSize(int maximumThreadPoolSize) {
        this.maximumThreadPoolSize = maximumThreadPoolSize;
        this.executor.setMaximumPoolSize(this.maximumThreadPoolSize);
    }

    /**
     * The configured keep alive time in seconds for idle threads in the thread
     * pool
     */
    private long threadKeepAliveTime = DEFAULT_KEEP_ALIVE_TIME;

    /**
     * The configured keep alive time in seconds for idle threads in the thread
     * pool
     * @return the configured keep alive time in seconds for idle threads in the
     * thread pool
     */
    public long getThreadKeepAliveTime() {
        return this.executor.getKeepAliveTime(DEFAULT_TIME_UNIT);
    }

    /**
     * Set the keep alive time in seconds for idle threads in the thread pool
     * @param threadKeepAliveTime The keep alive time in seconds to be used
     */
    public void setThreadKeepAliveTime(long threadKeepAliveTime) {
        this.threadKeepAliveTime = threadKeepAliveTime;
        this.executor.setKeepAliveTime(this.threadKeepAliveTime, DEFAULT_TIME_UNIT);
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Default Constructor
     */
    public PushManager() {
        initialize();
    }

    /**
     * Initialize the member fileds
     */
    private void initialize() {
        registerPushManagerMBean();
        notificationEngine = createNotificationEngine();
        executor = createExecutor();
    }

    /**
     * Called as LazyInitBean.
     * Reinitialize the executor if the queueCapacity has been changed
     */
    public void init() {

        int actualQueueCapacity = executor.getQueue().size() + executor.getQueue().remainingCapacity();
        if (actualQueueCapacity != queueCapacity) {
            log.warn("Changing PushManager queue capacity from " +
                    actualQueueCapacity + " to " + queueCapacity);
            executor.shutdown();
            try {
                executor.awaitTermination(DEFAULT_KEEP_ALIVE_TIME, DEFAULT_TIME_UNIT);
            } catch (InterruptedException ex) {
                log.warn("Unable to shutdown the already existing executor, " +
                        "while initializing a new one", ex);
            }
            executor = createExecutor();
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Sends a notification message to the given device.
     *
     * @param username the username. Note that this parameter is not actively
     *                 used at the moment; it's just used in the log and it
     *                 doesn't affect the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @param asynchronous if true the notification request is performed
     *        asynchronously and the method call returns straightaway.
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs or if the Executor uses finite bounds for
     *         both maximum threads and work queue capacity, and is saturated.
     */
    public void sendNotificationMessage(String username,
            String deviceId,
            Alert[] alerts,
            int uimode,
            boolean asynchronous) throws NotificationException {

        if (asynchronous) {
            try {
                asynchronouslySendNotificationMessage(username, deviceId, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                new NotificationException("Notification message delivery rejected", ex);
            }
        } else {
            synchronouslySendNotificationMessage(username, deviceId, alerts, uimode);
        }
    }

    /**
     * Sends a notification message to all devices of the principals with the
     * given username.
     *
     * @param username the username
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @param asynchronous if true the notification request is performed
     *        asynchronously and the method call returns straightaway.
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs or if the Executor uses finite bounds for
     *         both maximum threads and work queue capacity, and is saturated.
     */
    public void sendNotificationMessages(String username,
            Alert[] alerts,
            int uimode,
            boolean asynchronous)
            throws NotificationException {

        if (asynchronous) {
            try {
                asynchronouslySendNotificationMessages(username, alerts, uimode);
            } catch (RejectedExecutionException ex) {
                new NotificationException("Notification messages delivery rejected", ex);
            }
        } else {
            synchronouslySendNotificationMessages(username, alerts, uimode);
        }
    }

    /**
     * Retrieves a notification message created with the pending notifications
     * stored in the datastore.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @return the notification message
     * @throws NotificationException if an error occurs
     */
    public Message getMessageFromPendingNotifications(String username,
            String deviceId)
            throws NotificationException {

        return notificationEngine.getMessageFromPendingNotifications(username,
                deviceId);
    }

    /**
     * Deletes the notification stored in the datastore after that they are
     * delivered rightly.
     *
     * @param username the username
     * @param deviceId the device identifier
     * @param syncSources the array of SyncSources to notify
     * @throws NotificationException if an error occurs
     */
    public void deletePendingNotifications(String username,
            String deviceId,
            String[] syncSources)
            throws NotificationException {

        notificationEngine.deletePendingNotifications(username, deviceId, syncSources);
    }

    /**
     * Synchronuously sends a notification message to the given device.
     *
     * @param username the username. Note that this parameter is not actively
     *                 used at the moment; it's just used in the log and it
     *                 doesn't affect the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    public void synchronouslySendNotificationMessage(final String username,
            final String deviceId,
            final Alert[] alerts,
            final int uimode) throws NotificationException {

        if (log.isTraceEnabled()) {
            log.trace("Sending notification message to user '" +
                    username + "' and device '" + deviceId + "'");
        }
        notificationEngine.sendNotificationMessage(username, deviceId, alerts, uimode);
    }

    /**
     * Synchronuously sends a notification message to the given device.
     *
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     * @deprecated Since v71 use sendNotificationMessage(String username,
     *             String deviceId, Alert[] alerts, int uimode)
     */
    public void synchronouslySendNotificationMessage(final String deviceId,
            final Alert[] alerts,
            final int uimode) throws NotificationException {

        if (log.isTraceEnabled()) {
            log.trace("Sending notification message to evice '" + deviceId + "'");
        }
        notificationEngine.sendNotificationMessage(deviceId, alerts, uimode);
    }

    /**
     * Asynchronuously sends a notification message to the given device.
     *
     * @param username the username. Note that this parameter is not actively
     *                 used at the moment; it's just used in the log and it
     *                 doesn't affect the behaviour.
     * @param deviceId device identifier
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @return the Future object useful to verify if the notification message
     * has been  sent.
     * @throws RejectedExecutionException If the Executor uses finite bounds for
     * both maximum threads and work queue capacity, and is saturated.
     */
    public Future<?> asynchronouslySendNotificationMessage(final String username,
            final String deviceId,
            final Alert[] alerts,
            final int uimode) throws RejectedExecutionException {

        if (log.isTraceEnabled()) {
            log.trace("Asynchronously sending notification message to user '" +
                    username + "' and device '" + deviceId + "'");
        }

        Future<?> future = executor.submit(
                new Runnable() {

                    public void run() {
                        try {
                            notificationEngine.sendNotificationMessage(
                                    username, deviceId, alerts, uimode);
                        } catch (NotificationNotSentException ex) {
                            if (log.isTraceEnabled()) {
                                log.trace("Notification message not sent", ex);
                            }
                        } catch (NotificationException ex) {
                            log.error("Error asynchronously sending " +
                                    "notification message", ex);
                        }
                    }
                });
        return future;
    }

    /**
     * Synchronously sends a notification message to all devices of the
     * principals with the given username.
     *
     * @param username the username
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws com.funambol.framework.notification.NotificationException if an
     *         error occurs
     */
    public void synchronouslySendNotificationMessages(final String username,
            final Alert[] alerts,
            final int uimode) throws NotificationException {

        if (log.isTraceEnabled()) {
            log.trace("Sending notification message to user '" +
                    username + "'");
        }
        notificationEngine.sendNotificationMessages(username, alerts, uimode);
    }

    /**
     * Asynchronously sends a notification message to all devices of the
     * principals with the given username.
     *
     * @param username the username
     * @param alerts   array of Alert command for the datastore to sync, used
     *                 to create the binary message to send
     * @param uimode   user interaction mode
     * @throws RejectedExecutionException If the Executor uses finite bounds for
     * both maximum threads and work queue capacity, and is saturated.
     * @return the Future object useful to verify if the notification message
     * has been  sent
     */
    public Future<?> asynchronouslySendNotificationMessages(final String username,
            final Alert[] alerts,
            final int uimode)
            throws RejectedExecutionException {

        if (log.isTraceEnabled()) {
            log.trace("Asynchronously sending notification messages to user '" +
                    username + "'");
        }

        Future<?> future = executor.submit(
                new Runnable() {

                    public void run() {
                        try {
                            notificationEngine.sendNotificationMessages(
                                    username, alerts, uimode);
                        } catch (NotificationNotSentException ex) {
                            if (log.isTraceEnabled()) {
                                log.trace("Notification messages not sent", ex);
                            }
                        } catch (NotificationException ex) {
                            log.error("Error asynchronously sending " +
                                    "notification messages", ex);
                        }
                    }
                });

         return future;
    }

    /**
     * Shutdown all the threads and release the resources
     */
    public void shutdown() {
        executor.shutdown();
    }

    // ----------------------------------------------------------- MBean methods

    /**
     * The amount of queued pending notification requests waiting to be
     * served
     * @return the amount of queued pending notification requests
     */
    public int getQueuedPushCount() {
        return executor.getQueue().size();
    }

    /**
     * The amount of notification pushes currently under delivery
     * @return The amount of notification pushes currently under delivery
     */
    public int getCurrentPushInProgressCount() {
        return executor.getActiveCount();
    }

    /**
     * The amount of threads (serving a request or idle) currently in the thread
     * pool
     * @return The amount of threads currently in the thread pool
     */
    public int getThreadPoolSize() {
        return executor.getPoolSize();
    }

    /**
     * The total number of push messages sent since the beginning or the last
     * ResetStatistics
     * @return
     */
    public long getSentPushCount() {
        return (executor.getCompletedTaskCount() - previousCompletedTaskCount);
    }

    /**
     * Reset the statistic values
     */
    public void resetStatistics() {
        previousCompletedTaskCount = executor.getCompletedTaskCount();
    }

    // ------------------------------------------------------- Protected Methods

    /**
     * Factory Method creating a NotificationEngine object
     * @return a NotificationEngine object
     */
    protected NotificationEngine createNotificationEngine() {
        return new NotificationEngineImpl();
    }

    /**
     * Factory Method creating a ThreadPoolExecutor object
     * @return a ThreadPoolExecutor object
     */
    protected ThreadPoolExecutor createExecutor() {

        BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(queueCapacity);
        ThreadFactory threadFactory = new BaseThreadFactory();

        return new ThreadPoolExecutor(
                coreThreadPoolSize,
                maximumThreadPoolSize,
                threadKeepAliveTime,
                DEFAULT_TIME_UNIT,
                workQueue,
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
    }

    // ------------------------------------------------------------- Inner Class

    /**
     * Thread Factory creating threads with a meaningful name
     */
    public class BaseThreadFactory implements ThreadFactory {

        final ThreadGroup group;
        final AtomicInteger threadNumber = new AtomicInteger(1);

        /**
         * Creates a new ThreadFactory
         */
        public BaseThreadFactory() {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(group, r,
                    THREAD_NAME_PREFIX + threadNumber.getAndIncrement(),
                    0);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }
            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Register the PushManager management bean
     */
    private void registerPushManagerMBean() {
       MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
       try {
           ObjectName name = new ObjectName("com.funambol:type=PushManager,instance=" + this.toString());
           mbs.registerMBean(this, name);
       } catch (Exception e) {
           log.error("Unable to register PushManager MBean", e);
       }
    }

}
