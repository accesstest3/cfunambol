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

import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

/**
 * It's the executor of the RegistryMonitor.
 * @version $Id: RegistryMonitorExecutor.java,v 1.5 2007-11-28 11:15:04 nichele Exp $
 */
public class RegistryMonitorExecutor extends ScheduledThreadPoolExecutor {

    /** The logger */
    private static final Logger log = Logger.getLogger("funambol.pushlistener.registrymonitor");

    /** The registry monitor */
    private RegistryMonitor registryMonitor;

    /**
     * Creates a new registry monitor executor
     */
    public RegistryMonitorExecutor(int maxThreadPoolSize,
                                   RegistryMonitor registryMonitor,
                                   ThreadFactory threadFactory) {

        super(maxThreadPoolSize, threadFactory);
        this.registryMonitor = registryMonitor;
    }

    @Override
    protected void beforeExecute(Thread t, Runnable r) {
        registryMonitor.changeState(RegistryMonitor.State.RUNNING);
        super.beforeExecute(t, r);
    }

    @Override
    protected void afterExecute(Runnable r, Throwable t) {

        super.afterExecute(r, t);

        registryMonitor.changeState(RegistryMonitor.State.PAUSED);
        if (r instanceof Future<?>) {
            try {
                if (((Future<?>) r).isDone()) {
                    ((Future<?>) r).get();
                }
            } catch (InterruptedException ie) {
            } catch (ExecutionException ee) {

                Throwable realThrowable = ee.getCause();

                log.error("RegistryMonitor throws an uncaught exception", realThrowable);
                if (log.isInfoEnabled()) {
                    log.info("Trying to re-scheduling RegistryMonitor ", realThrowable);
                }
                long pollingTime = registryMonitor.getPollingTime();
                scheduleWithFixedDelay(registryMonitor,
                                       pollingTime,
                                       pollingTime,
                                       TimeUnit.MILLISECONDS);

            } catch (CancellationException ce) {
            }
        }
    }
}

