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

import java.math.BigDecimal;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;

import org.apache.log4j.Logger;

import com.funambol.framework.logging.LogContext;

import com.funambol.pushlistener.framework.TaskException;
import com.funambol.pushlistener.service.BaseThreadFactory;


/**
 * This is the executor of <code>TaskWrapper</code> and of
 * <code>ScheduledTaskWrapper</code>.
 *
 * @version $Id: ScheduledTaskExecutor.java,v 1.14 2008-05-18 16:23:42 nichele Exp $
 */
public class ScheduledTaskExecutor extends ScheduledThreadPoolExecutor {

    // --------------------------------------------------------------- Constants   

    // ------------------------------------------------------------ Private data

    /** The logger */
    private static final Logger log = Logger.getLogger("funambol.pushlistener.taskexecutor");

    /**
     * Contains couples ScheduledTaskWrapper - ScheduledFuture
     */
    private BidiMap scheduledFutures = new DualHashBidiMap();

    /**
     * Contains couples TaskWrapper - ScheduledFuture
     */
    private BidiMap taskFutures = new DualHashBidiMap();

    /**
     * If the real period of scheduled execution is bigger than the required
     * period + the tolerance, a warning is logged.
     */
    private double taskPeriodTolerance;

    /** These locks are used to sync the taskLocks accesses */
    private ReentrantReadWriteLock lockForTaskLocks = new ReentrantReadWriteLock();
    private Lock readLock  = lockForTaskLocks.readLock();
    private Lock writeLock = lockForTaskLocks.writeLock();
    /**
     * Contains for any task, the relative handling lock. The handling lock must be
     * obtained by any thread that must handle the task (like remove it or re-schedule it).
     * See getHandlingTaskLock method.
     */
    private Map<Object, Lock> taskLocks = new ConcurrentHashMap<Object, Lock>();

    /**
     * The PushListener starting time
     */
    private long startingTime = 0;

    /**
     * It's used to compute the load factor
     */
    private long lastResetCompletedTaskCount = 0;
    
    /**
     * Sum of all execution times
     */
    private AtomicLong totalExecutionTime = null;

    /**
     * Number of executions out-of-period
     */
    private AtomicLong numberOfExecutionsOutOfPeriod = null;
    
    // -------------------------------------------------------------- Properties

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new ScheduledTaskExecutor
     *
     * @param maxThreadPoolSize the size of the thread pool
     * @param periodTolerance the tolerance on the period
     */
    public ScheduledTaskExecutor(int maxThreadPoolSize,
                                 double periodTolerance){

        super(maxThreadPoolSize, new BaseThreadFactory("task-executor-pool"));

        this.taskPeriodTolerance = periodTolerance;

        if (log.isInfoEnabled()){
            log.info("Creating a ScheduledTaskExecutor instance with maxThreadPoolSize: " +
                      maxThreadPoolSize);
        }
        if (log.isTraceEnabled()){
            log.trace("ScheduledTaskExecutor instance: " + this);

        }
        startingTime = System.currentTimeMillis();
        
        totalExecutionTime = new AtomicLong();
        numberOfExecutionsOutOfPeriod = new AtomicLong();
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Initializes this ScheduledTaskExecutor
     */
    public void init() {
        if (log.isInfoEnabled()){
            log.info("Initializing ScheduledTaskExecutor with maxThreadPoolSize: " +
                      getCorePoolSize());
        }
    }
    
    /**
     * Returns the number of the tasks in the execution queue
     * @return the number of the tasks in the execution queue
     */
    public int getNumberOfQueuedTasks() {
        return getQueue().size();
    }

    /**
     * Returns the approximate total number of tasks that have
     * completed execution. 
     *
     * @return the number of tasks
     */
    @Override
    public long getCompletedTaskCount() {
        return super.getCompletedTaskCount() - lastResetCompletedTaskCount;
    }

    /**
     * Returns the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     * @return the number of the handled tasks (it is the sum between the number
     * of tasks in queue and the number of tasks in execution)
     */
    public long getNumberOfHandledTasks() {
        return getActiveCount() + getNumberOfQueuedTasks();
    }

    /**
     * Returns the push listener load factor computed as completedTaskCount/runningTime
     * in minutes (that is number of completed tasks in a minute)
     * @return the push listener load factor computed as completedTaskCount/runningTime
     */
    public double getLoadFactor() {

        double runningTime =
            ((double)System.currentTimeMillis() - startingTime)/60000;  // in minutes

        if (runningTime == 0) {
            return 0;
        }

        long completedTask = getCompletedTaskCount();

        if (completedTask == 0) {
            return 0;
        }

        double loadFactor = (double)completedTask / runningTime;

        BigDecimal bd = new BigDecimal(loadFactor);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_EVEN);

        return bd.doubleValue();
    }

    /**
     * Returns the average execution time
     * @return the average execution time
     */
    public double getAverageExecutionTime() {

        long completedTask = getCompletedTaskCount();

        if (completedTask == 0) {
            return 0;
        }

        double average = totalExecutionTime.doubleValue() / completedTask;

        BigDecimal bd = new BigDecimal(average);
        bd = bd.setScale(1, BigDecimal.ROUND_HALF_EVEN);

        return bd.doubleValue();
    }

    /**
     * Returns the number of executions out-of-period
     * @return the number of executions out-of-period
     */
    public long getNumberOfExecutionsOutOfPeriod() {
        return numberOfExecutionsOutOfPeriod.longValue();
    }

    /**
     * Executes the given task.
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod()).
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (maybe the same task or another equal task is waiting for its execution or
     *         is already running)
     * @param newTask the task to execute
     */
    public boolean executeTaskWrapper(TaskWrapper newTask) {
       return executeTaskWrapper(newTask, 0);
    }

    /**
     * Executes the given task scheduling its execution to run after a given delay.
     * <p>Note that the task will be executed as soon as a thread
     * is available. Moreover, if the same task (or another equal task) is already
     * running, a new execution is planned with a delay of task.getPeriod()).
     * If the same task (or another equal task) is already waiting for its execution
     * this method doesn't have effect.
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (maybe the same task or another equal task is waiting for its execution or
     *         is already running)
     * @param newTask the task to execute
     * @param delay the execution delay
     */
    public boolean executeTaskWrapper(TaskWrapper newTask, long delay) {
        if (newTask == null) {
            throw new IllegalArgumentException("Task must be not null");
        }
        Lock handlingTaskLock = getHandlingTaskLock(newTask);
        handlingTaskLock.lock();
        try {
            return submitTaskWrapper(newTask, delay);
        } finally {
            handlingTaskLock.unlock();
        }

    }

    /**
     * Updates the task stopping it and rescheduling it
     * @param task the task to update
     */
    public void updateScheduledTask(ScheduledTaskWrapper task) {
        if (task == null) {
            if (log.isTraceEnabled()) {
                log.trace("Trying to update a null task. Request rejected");
            }
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Updating task '" + task + "'. The task will be cancelled and " +
                     "then re-scheduled");
        }
        //
        // Locking the lock for this task so no other thread can handle it avoiding
        // conflict (what happens if a thread is removing it an another threead is
        // updating it ?)
        //
        Lock handlingTaskLock = getHandlingTaskLock(task.getId());
        handlingTaskLock.lock();
        try {
            ScheduledFuture      scheduledFuture  = null;
            ScheduledTaskWrapper oldScheduledTask = null;

            synchronized (scheduledFutures) {
                scheduledFuture  = (ScheduledFuture)scheduledFutures.get(task);
                oldScheduledTask = (ScheduledTaskWrapper) scheduledFutures.getKey(scheduledFuture);
            }

            boolean cancelled = false;
            if (scheduledFuture != null) {
                cancelled = scheduledFuture.isCancelled();
                if (!cancelled) {
                    cancelled = scheduledFuture.cancel(true);  // if we can, we stop its
                                                               // execution. Remember that
                                                               // cancel means cancel its
                                                               // scheduled execution and
                                                               // not just the running one
                    if (cancelled) {
                        if (log.isTraceEnabled()) {
                            log.trace("Task '" + task.getId() + "' cancelled successfully");
                        }
                    } else {
                        if (log.isTraceEnabled()) {
                            log.trace("Task '" + task.getId() + "' not cancelled for unknown reasons." +
                                    "Is it already cancelled ? " + ((scheduledFuture.isCancelled() ? "YES" : "NO")));
                        }
                    }

                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Task '" + task.getId() + "' has been already cancelled");
                    }
                }
                if (!ScheduledTaskWrapper.State.SHUTDOWN.equals(oldScheduledTask.getState())) {
                    if (log.isTraceEnabled()) {
                        log.trace("Shutting down task '" + task.getId() + "'");
                    }
                    try {
                        oldScheduledTask.shutdown();
                    } catch (TaskException ex) {
                        log.error("Error shutting down scheduled task '" + oldScheduledTask + "'", ex);
                    }
                    oldScheduledTask.setState(ScheduledTaskWrapper.State.SHUTDOWN);
                }

                synchronized (scheduledFutures) {
                    //
                    // Any time we remove the scheduledTask from scheduledFutures,
                    // we try to remove the scheduledFuture from the queue. This
                    // is not really needed because after a while this is performed
                    // automatically but in this way we keep scheduledFutures and
                    // the queue in sync
                    //
                    if (scheduledFuture instanceof Runnable) {
                        super.remove((Runnable)scheduledFuture);
                    }
                    scheduledFutures.remove(oldScheduledTask);
                }

            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Task '" + task + "' seems not scheduled");
                }
            }
            scheduleTask(task, 0);
        } finally {
            handlingTaskLock.unlock();
        }
    }

    /**
     * Schedules a new task
     *
     * @param task the <code>ScheduledTaskWrapper</code> to schedule
     */
    public void scheduleTask(ScheduledTaskWrapper task) {
        if (task == null) {
            if (log.isTraceEnabled()) {
                log.trace("Trying to schedule a null task. Request rejected");
            }
            return;
        }
        //
        // Locking the lock for this task so no other thread can handle it avoiding
        // conflict (what happens if a thread is removing it an another threead is
        // updating it ?)
        //
        Lock handlingTaskLock = getHandlingTaskLock(task.getId());
        handlingTaskLock.lock();
        try {
            scheduleTask(task, 0);
        } finally {
            handlingTaskLock.unlock();
        }
    }

    /**
     * Cancels execution of the task identified by the given id and removes its
     * scheduled execution
     * it will be interrupted.
     * @param taskId the id of task to stop/remove
     * @return true if the task has been removed successfully, false otherwise (for instance
     *         if the task doesn't exist)
     */
    public boolean removeScheduledTask(long taskId) {
        return removeScheduledTask(taskId, true);
    }


    /**
     * Verifies if the task with the given id is in the queue
     * @param newTask the searched task
     * @return true if the task is in the queue
     */
    public boolean isTaskWrapperInQueue(TaskWrapper newTask) {

        if (newTask == null) {
            throw new IllegalArgumentException("Task must be not null");
        }

        //
        // Locking the lock for this task so no other thread can handle it avoiding
        // conflict (what happens if a thread is removing it an another threead is
        // updating it ?)
        //
        Lock handlingTaskLock = getHandlingTaskLock(newTask);
        handlingTaskLock.lock();

        try {
            synchronized (taskFutures) {
                Object o = taskFutures.get(newTask);
                if (o != null) {
                    return true;
                }
            }
        } finally {
            handlingTaskLock.unlock();
        }

        return false;
    }

    /**
     * Resets statistic information
     */
    public void resetStatistics() {
        this.lastResetCompletedTaskCount = super.getCompletedTaskCount();
        this.totalExecutionTime.set(0);
        this.numberOfExecutionsOutOfPeriod.set(0);
        this.startingTime = System.currentTimeMillis();
    }
    
    /**
     * Cancels execution of the task identified by the given id and removes its
     * scheduled execution
     * @param taskId the id of task to stop/remove
     * @param mayInterruptIfRunning true if the thread executing this task should
     *        be interrupted; otherwise, in-progress tasks are allowed to complete
     * @return true if the task has been removed successfully, false otherwise (for instance
     *         if the task doesn't exist)
     */
    public boolean removeScheduledTask(long taskId, boolean mayInterruptIfRunning) {

        ScheduledTaskWrapper dummyTask = new ScheduledTaskWrapper(taskId);

        if (log.isTraceEnabled()) {
            log.trace("Removing task '" + taskId + "'");
        }
        //
        // Locking the lock for this task so no other thread can handle it avoiding
        // conflict (what happens if a thread is removing it an another threead is
        // updating it ?)
        //
        Lock handlingTaskLock = getHandlingTaskLock(taskId);
        handlingTaskLock.lock();
        try {
            ScheduledFuture      scheduledFuture = null;
            ScheduledTaskWrapper scheduledTask   = null;

            synchronized (scheduledFutures) {
                scheduledFuture  = (ScheduledFuture)scheduledFutures.get(dummyTask);
                scheduledTask = (ScheduledTaskWrapper) scheduledFutures.getKey(scheduledFuture);
            }

            if (scheduledFuture != null) {

                scheduledFuture.cancel(mayInterruptIfRunning);

                if (scheduledTask != null) {
                    try {
                        scheduledTask.shutdown();
                    } catch (TaskException ex) {
                        log.error("Error shutting down scheduled task '" + scheduledTask + "'", ex);
                    }
                    scheduledTask.setState(ScheduledTaskWrapper.State.SHUTDOWN);
                }

                synchronized (scheduledFutures) {
                    //
                    // Any time we remove the scheduledTask from scheduledFutures,
                    // we try to remove the scheduledFuture from the queue. This
                    // is not really needed because after a while this is performed
                    // automatically but in this way we keep scheduledFutures and
                    // the queue in sync
                    //
                    if (scheduledFuture instanceof Runnable) {
                        super.remove((Runnable)scheduledFuture);
                    }
                    scheduledFutures.remove(scheduledTask);
                }

                if (log.isTraceEnabled()) {
                    log.trace("Task '" + taskId + "' cancelled successfully");
                }
                return true;

            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Task '" + taskId + "' seems not scheduled");
                }
                return false;
            }
        } finally {
            handlingTaskLock.unlock();
        }
    }

    /**
     * Shuts down the underlying ScheduledThreadPoolExecutor
     */
    @Override
    public void shutdown() {

        if (super.isShutdown()) {
            return;
        }
        if (log.isInfoEnabled()) {
            log.info("Shutting down ScheduledTaskExecutor [this: " + this + "]");
        }
        super.shutdownNow();
        if (log.isInfoEnabled()) {
            log.info("ScheduledTaskExecutor shut down [this: " + this + "]");
        }
    }

    /**
     * Returns the health status of the executor
     * @return the health status of the executor
     */
    public ScheduledTaskExecutorHealthStatus getScheduledTaskExecutorHealthStatus() {
        ScheduledTaskExecutorHealthStatus status = null;

        int     poolSize           = getPoolSize();
        long    completedTaskCount = getCompletedTaskCount();
        int     activeCount        = getActiveCount();
        long    taskCount          = getTaskCount();
        Queue   queue              = getQueue();
        boolean shutDown           = isShutdown();
        int     taskQueued         = 0;

        if (queue != null) {
            taskQueued = queue.size();
        }

        status =
            new ScheduledTaskExecutorHealthStatus(((Object)this).toString(),
                                                  poolSize,
                                                  activeCount,
                                                  completedTaskCount,
                                                  taskCount,
                                                  taskQueued,
                                                  shutDown);

        return status;
    }

    // ------------------------------------------------------- Protected methods
                                                          
    @Override
    protected void afterExecute(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        if (r instanceof ScheduledFuture<?>) {
            ScheduledFuture scheduledFuture = (ScheduledFuture<?>)r;
            Object task = null;
            synchronized (taskFutures) {
                task = taskFutures.getKey(scheduledFuture);
            }
            if (task != null) {
                afterTaskExecution(r, t);
            } else {
                afterScheduledTaskExecution(r, t);
            }
        }
    }

    /**
     * Called when a ScheduledTask ends its execution. See afterExecute.
     */
    protected void afterScheduledTaskExecution(Runnable r, Throwable t) {
        super.afterExecute(r, t);

        ScheduledTaskWrapper scheduledTask    = null;
        Lock                 handlingTaskLock = null;

        if (r instanceof ScheduledFuture) {
            ScheduledFuture scheduledFuture = (ScheduledFuture)r;
            synchronized (scheduledFutures) {
                scheduledTask = (ScheduledTaskWrapper)scheduledFutures.getKey(scheduledFuture);

                if (scheduledTask != null) {
                    handlingTaskLock = getHandlingTaskLock(scheduledTask.getId());
                    handlingTaskLock.lock();
                }
            }
            //
            // Bear in mind that here the scheduledTask could be null if the scheduledFuture
            // has been cancelled and removed from the scheduledFutures map.
            //
            if (log.isTraceEnabled()) {
                if (scheduledTask == null) {
                    log.trace("Scheduled task null for: " + r +
                              ". Is it cancelled ? " + scheduledFuture.isCancelled());
                }
            }

            try {
                if (scheduledFuture.isDone()) {
                    scheduledFuture.get();
                }
            } catch (InterruptedException ie) {
            } catch (ExecutionException ee) {
                //
                // This is done to retrieve the possible exception thrown by the
                // task
                //
                Throwable realThrowable = ee.getCause();

                if (scheduledTask != null) {

                    log.error("Task '" + scheduledTask + "' throws an uncaught exception. " +
                              "The task will be rescheduled", realThrowable);
                    try {
                        scheduledTask.shutdown();
                    } catch (TaskException ex) {
                        log.error("Error shutting down scheduled task '" + scheduledTask + "'", ex);
                    }

                    scheduledTask.setState(ScheduledTaskWrapper.State.SHUTDOWN);

                    synchronized (scheduledFutures) {
                        //
                        // Any time we remove the scheduledTask from scheduledFutures,
                        // we try to remove the scheduledFuture from the queue. This
                        // is not really needed because after a while this is performed
                        // automatically but in this way we keep scheduledFutures and
                        // the queue in sync
                        //
                        if (scheduledFuture instanceof Runnable) {
                            super.remove((Runnable)scheduledFuture);
                        }
                        scheduledFutures.remove(scheduledTask);
                    }

                    //
                    // The task will be rescheduled using the period as delay
                    // because otherwise a new execution is performed asap
                    //
                    scheduleTask(scheduledTask, scheduledTask.getPeriod());

                } else {
                    log.error("Uncaught exception thrown by: " + scheduledFuture     +
                              ". This ScheduledFuture seems not relative to a ScheduleTask" +
                              " so nothing will be rescheduled (it could be about " +
                              " to an already cancelled task)", realThrowable);
                }

            } catch (CancellationException ce) {
            } finally {
                if (handlingTaskLock != null) {
                    handlingTaskLock.unlock();
                }
                handlingScheduledExecutionTimeInformation(scheduledTask);
                LogContext.clear();
            }
        }
    }

    /**
     * Called when a TaskWrapper ends its execution. See afterExecute.
     */
    protected void afterTaskExecution(Runnable r, Throwable t) {

        TaskWrapper taskWrapper = null;

        if (r instanceof ScheduledFuture) {
            ScheduledFuture scheduledFuture = (ScheduledFuture)r;

            Lock handlingTaskLock = null;

            synchronized (taskFutures) {
                taskWrapper = (TaskWrapper)taskFutures.getKey(scheduledFuture);
                if (taskWrapper != null) {
                    handlingTaskLock = getHandlingTaskLock(taskWrapper);
                    handlingTaskLock.lock();
                }
            }

            try {
                logTaskWrapperExecutionError(taskWrapper, scheduledFuture);
                if (taskWrapper == null) {
                    return ;
                }

                //
                // we don't need to remove the task from the (ScheduledFuture)queue
                // because it is already removed since is a one shot task (not scheduled)
                //
                synchronized (taskFutures) {
                    taskFutures.remove(taskWrapper);
                }

                boolean needRedo = false;

                //synchronized (taskWrapper) {
                    needRedo = taskWrapper.needRedo();
                //}
                if (needRedo) {
                    long period = taskWrapper.getPeriod();
                    if (log.isTraceEnabled()) {
                        log.trace("Task '" + taskWrapper + "' must be redone. " +
                                  "It will be executed in " + period + " milliseconds");
                    }
                    ScheduledFuture f = schedule(taskWrapper, period, TimeUnit.MILLISECONDS);

                    synchronized (taskFutures) {
                        taskFutures.put(taskWrapper, f);
                    }

                }
            } finally {
                if (handlingTaskLock != null) {
                    handlingTaskLock.unlock();
                }
                handlingOneShotExecutionTimeInformation(taskWrapper);
                LogContext.clear();
            }

        }
    }

    /**
     * Schedules a new task
     *
     * @param task the <code>ScheduledTaskWrapper</code> to schedule
     * @param delay the delay in the scheduling (it must be greater than 1 second
     *        otherwise the delay is set automatically to 1 sec)
     */
    protected void scheduleTask(ScheduledTaskWrapper task, long delay) {
        if (task == null) {
            if (log.isTraceEnabled()) {
                log.trace("Trying to schedule a null task. Request rejected");
            }
            return;
        }
        //
        // We don't get any task lock, but the caller should do (see updateTask or
        // removeTask or scheduleTask(ScheduledTaskWrapper))
        //

        //
        // This is used below...see there for a description
        //
        Lock taskExecutionLock = null;
        try {

            if (!ScheduledTaskWrapper.State.CONFIGURED.equals(task.getState()) &&
                !ScheduledTaskWrapper.State.SCHEDULED.equals(task.getState())) {
                //
                // We log the error creating an empty exception in order to have
                // the stacktrace
                //
                log.error("Trying to schedule a not configured or scheduled task. Request rejected",
                          new Exception());

                return ;
            }
            long period = task.getPeriod();
            TimeUnit timeUnit = TimeUnit.MILLISECONDS;
            if (period == 0) {
                period = 1;
                timeUnit = TimeUnit.NANOSECONDS;
            }

            if (log.isTraceEnabled()) {
                log.trace("Scheduling task: " + task);
            }
            //
            // We use the execution lock to avoid the task execution before putting
            // it in the scheduledFutures map.
            // See ScheduledTaskWrapper.execute
            //
            taskExecutionLock = new ReentrantLock();
            taskExecutionLock.lock();
            task.setExecutionLock(taskExecutionLock);
            ScheduledFuture scheduledFuture =
                scheduleWithFixedDelay(task, delay, period, timeUnit);

            task.setState(ScheduledTaskWrapper.State.SCHEDULED);

            //
            // Since DualHashBidiMap is not synchronized we need to sync the write
            // access
            //
            synchronized (scheduledFutures) {
                scheduledFutures.put(task, scheduledFuture);
            }
        } finally {
            taskExecutionLock.unlock();
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns the lock associated to the given object. Any thread that
     * must handle a task must, first of all, lock the handling task lock.
     * Locking a ScheduledTaskWrapper the key must be its id.
     * Locking a TaskWrapper the key is the task itself (it doesn't have an id).
     *
     * Note that maybe also for the ScheduledTaskWrapper we could use the task
     * itself but at the moment this change requires a lot of regression test.
     * The next one that reads this comment should try to review it in order to use
     * always the task as key.
     *
     * @param key the key of the lock (could be the task itself or its id)
     * @return the lock associated to the key
     */
    private Lock getHandlingTaskLock(Object key) {

        if (key != null) {
            if (key instanceof ScheduledTaskWrapper) {
                key = ((ScheduledTaskWrapper)key).getId();
            }
        }

        Lock taskLock = null;
        readLock.lock();
        try {
            taskLock = taskLocks.get(key);
        } finally {
            readLock.unlock();
        }
        if (taskLock == null) {
            writeLock.lock();
            try {
                taskLock = taskLocks.get(key);
                if (taskLock == null) {
                    taskLock = new ReentrantLock();
                    taskLocks.put(key, taskLock);
                }
            }
            finally {
                writeLock.unlock();
            }
        }
        return taskLock;
    }

    /**
     * Plans an execution of the given TaskWrapper (the execution will be performed as soon
     * as a thread is available and the delay expires).
     * <p>IMPORTANT NOTE: the caller must lock the task before calling this method
     * using getHandlingTaskLock to obtain a lock instance.
     * @return true if the task execution has been planned to be performed as soon as
     *         possible, false otherwise
     *         (the same task or another equal task is waiting for its execution or
     *         is already running)
     * @param newTask the task to execute
     * @param delay the delay in the execution
     */
    private boolean submitTaskWrapper(TaskWrapper newTask, long delay) {

        if (newTask == null) {
            throw new IllegalArgumentException("Task must be not null");
        }

        ScheduledFuture future = null;
        TaskWrapper     task   = null;

        //
        // The caller must lock the task
        //
        synchronized (taskFutures) {
            future = (ScheduledFuture)taskFutures.get(newTask);
            task   = (TaskWrapper)taskFutures.getKey(future);
        }

        //
        // Task null means that in taskFutures there is not the task yet (first
        // execution ?)
        //
        if (task == null) {
            task = newTask;
        } else {
            //
            // There is already an equals task in the taskFutures map. We try
            // to force queue a new execution
            //
            boolean queued = false;

            queued = task.queueNewExecution();

            if (queued) {
                if (log.isTraceEnabled()) {
                    log.trace("Execution of '" + task + "' queued");
                }
                return false;
            }

        }

        //
        // We use the execution lock to avoid the task execution before putting
        // it in the taskFutures map.
        // See TaskWrapper.execute
        //
        Lock taskExecutionLock = new ReentrantLock();
        taskExecutionLock.lock();
        try {
            task.setExecutionLock(taskExecutionLock);

            future = schedule(task, delay, TimeUnit.MILLISECONDS);

            synchronized (taskFutures) {
                taskFutures.put(task, future);
            }
        } finally {
            taskExecutionLock.unlock();
        }
        return true;
    }

    /**
     * Logs some infomration about a scheduled execution, like the execution time, the
     * period from the last execution and the expected period. Moreover if the current
     * period is > expectedPeriod * (1 + taskPeriodTolerance) a warning message is logged.
     * Moreover add the execution time to totalExecutionTime.
     * @param scheduledTask the executed scheduled task wrapper
     */
    private void handlingScheduledExecutionTimeInformation(ScheduledTaskWrapper scheduledTask) {

        if (scheduledTask != null) {
            //
            // Checking period and execution time
            //
            long period            = scheduledTask.getPeriod();
            long lastElapsedTime   = scheduledTask.getLastExecutionElapsedTime();
            long lastExecutionTime = scheduledTask.getLastTaskElapsedTime();

            totalExecutionTime.addAndGet(lastExecutionTime);

            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Execution details: ");
                sb.append("\n\t> task: " + scheduledTask)
                .append("\n\t> execution time: ")
                .append(lastExecutionTime).append(" ms")
                .append("\n\t> elapsed time from last execution: ")
                .append(lastElapsedTime).append(" ms")
                .append("\n\t> expected period: ")
                .append(period).append(" ms");

                log.trace(sb.toString());
            }

            if (lastElapsedTime > period * (1 + taskPeriodTolerance)) {

                numberOfExecutionsOutOfPeriod.incrementAndGet();

                StringBuilder sb = new StringBuilder();
                sb.append("Task '").append(scheduledTask)
                  .append("' was scheduled with period '").append(period)
                  .append(" ms' but the elapsed time from the last execution")
                  .append(" is '").append(lastElapsedTime).append(" ms'");
                log.warn(sb.toString());
            }
        }
    }

    /**
     * Logs some information about a one-shot task execution, like the execution time.
     * Moreover add the execution time to totalExecutionTime.
     * @param task the task
     */
    private void handlingOneShotExecutionTimeInformation(TaskWrapper task) {

        if (task != null) {
            //
            // Checking execution time
            //
            long lastExecutionTime = task.getLastTaskElapsedTime();

            totalExecutionTime.addAndGet(lastExecutionTime);

            if (log.isTraceEnabled()) {
                StringBuilder sb = new StringBuilder("Execution details: ");
                sb.append("\n\t> task: " + task)
                .append("\n\t> execution time: ")
                .append(lastExecutionTime).append(" ms");
                        
                log.trace(sb.toString());
            }
        }
    }

    /**
     * If there is, logs the exception thrown by the taskWrapper execution
     * @param taskWrapper the executed task wrapper
     * @param scheduledFuture the scheduledFuture with the exception (if there is)
     */
    private void logTaskWrapperExecutionError(TaskWrapper taskWrapper,
                                              ScheduledFuture scheduledFuture) {
        try {
            if (scheduledFuture.isDone()) {
                scheduledFuture.get();
            }
        } catch (InterruptedException ie) {
        } catch (ExecutionException ee) {
            //
            // This is done to retrieve the possible exception thrown by the
            // task
            //
            Throwable realThrowable = ee.getCause();

            if (taskWrapper != null) {
                log.error("Task '" + taskWrapper + "' throws an uncaught exception. ",
                           realThrowable);
            } else {
                log.error("Uncaught exception thrown by: " + scheduledFuture, realThrowable);
            }
        }
    }
}