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

package com.funambol.pushlistener.service.health;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.funambol.pushlistener.service.*;

/**
 * It monitors the pushlistener state
 * @version $Id: HealthThread.java,v 1.7 2007-11-28 11:15:03 nichele Exp $
 */
public class HealthThread implements Runnable {

    // ------------------------------------------------------------ Private data

    /** The logger */
    private static final Logger log = Logger.getLogger("funambol.pushlistener.health");

    /** The prefix name of the health thread */
    private static final String THREAD_PREFIX_NAME = "health";

    /** The ScheduledExecutor used to execute this health thread */
    private ScheduledExecutorService executorService = null;

    /** The push listener */
    private PushListener listener = null;

    /** The polling time */
     private long pollingTime = 0;

    /**
     * Creates a new instance of HealthThread for the given PushListener and the given
     * polling time
     * 
     * @param listener the listener to check
     * @param pollingTime the execution period
     */
    public HealthThread(PushListener listener, long pollingTime) {

        if (log.isInfoEnabled()){
            log.info("Creating HealthThread instance...");
        }
        this.listener    = listener;
        this.pollingTime = pollingTime;

        executorService = Executors.newScheduledThreadPool(
                            1,
                            new BaseThreadFactory(THREAD_PREFIX_NAME)
                          );
    }

    /**
     * Schedules and tarts the execution of the HealthThread wuth fixed delay
     */
    public void scheduleAndStart() {
        if (log.isInfoEnabled()){
            log.info("Starting HealthThread (polling time: " +
                      pollingTime + " milliseconds)");
        }
        executorService.scheduleWithFixedDelay(this, 0, pollingTime, TimeUnit.MILLISECONDS);
    }

    /**
     * Shuts down the underlying executor
     */
    public void shutdown() {
        if (log.isInfoEnabled()) {
            log.info("Shutting down HealthThread...");
        }
        executorService.shutdown();

        if (log.isInfoEnabled()) {
            log.info("HealthThread shut down");
        }

    }

    /**
     * Gets the PushListener status and logs it
     */
    public void run() {

        if (log.isTraceEnabled()) {
            log.trace("Checking health state...");
        }

        PushListenerHealthStatus status = listener.getPushListenerHealthStatus();
        if (log.isInfoEnabled()) {
            log.info(status);
        }
    }
}
