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

package com.funambol.transport.http.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicLong;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * This filter exposes via JMX some basic metric about server performance
 * like average response time, number of processed requests, average sync time.
 * See SyncResponseTimeFilterMBean for a complete list of the exposed metrics.
 *
 * @version $Id: SyncResponseTimeFilter.java 36077 2010-10-13 12:42:35Z luigiafassina $
 */
public class SyncResponseTimeFilter implements Filter, SyncResponseTimeFilterMBean, Constants {

    // --------------------------------------------------------------- Constants
    
    private static final String PARAM_STARTING_SYNC_TIME = "funambol.starting-sync-time";
    private static final String PARAM_SYNC_LATENCY = "funambol.sync-latency";

    // -------------------------------------------------------------- Properties

    private AtomicLong numberOfRequests = new AtomicLong();
    private AtomicLong numberOfCurrentRequests = new AtomicLong();
    private AtomicLong numberOfSyncs = new AtomicLong();

    private long maxResponseTime = 0;

    public long getNumberOfRequests() {
        return numberOfRequests.get();
    }

    public long getNumberOfCurrentRequests() {
        return numberOfCurrentRequests.get();
    }

    public long getNumberOfSyncs() {
        return numberOfSyncs.get();
    }

    public long getMaxResponseTime() {
        return maxResponseTime;
    }

    // ------------------------------------------------------------ Private data

    private FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);
    
    private AtomicLong cumulativeResponseTime = new AtomicLong();
    private AtomicLong cumulativeSyncTime = new AtomicLong();
    private AtomicLong cumulativeSyncLatency = new AtomicLong();

    private String webappName = null;

    private final Timer timer = new Timer(
        "SyncResponseTimeFilter-" + Integer.toHexString(this.hashCode())
    );

    private List<InstantInfo> lastInstantInfo = null;
    
    // ------------------------------------------------------------ Constructors
    public SyncResponseTimeFilter() {
       lastInstantInfo = new ArrayList<InstantInfo>();
    } 

    // ---------------------------------------------------------- Public methods
    
    /**
     *
     * @param request The servlet request we are processing
     * @param response The servlet response we are creating
     * @param chain The filter chain we are processing
     *
     * @exception IOException if an input/output error occurs
     * @exception ServletException if a servlet error occurs
     */
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain)
	throws IOException, ServletException {

	long startingProcessingTime = System.currentTimeMillis();

        numberOfCurrentRequests.incrementAndGet();
        
        //
        // if the request session id is not valid (ie, missing) it means the session
        // is new --> a new sync is starting
        //
        boolean newSession = !((HttpServletRequest)request).isRequestedSessionIdValid();
        if (newSession) {
            ((HttpServletRequest)request).getSession().setAttribute(PARAM_STARTING_SYNC_TIME, startingProcessingTime);
        }
        HttpSession session = ((HttpServletRequest)request).getSession();
        Object syncStartingTime = session.getAttribute(PARAM_STARTING_SYNC_TIME);
        Object syncLatency = session.getAttribute(PARAM_SYNC_LATENCY);
        
        try {
            chain.doFilter(request, response);
        } finally {

            long processingTime = System.currentTimeMillis() - startingProcessingTime;

            numberOfCurrentRequests.decrementAndGet();

            if (processingTime > maxResponseTime) {
                maxResponseTime = processingTime;
            }

            //
            // The following two lines should be synchronized but for performance
            // reason I prefer avoid it since in the worse case calling
            // getAverageResponseTime the value of cumulativeResponseTime has been updated
            // and numberOfRequests not yet.
            //
            cumulativeResponseTime.addAndGet(processingTime);
            numberOfRequests.incrementAndGet();

            Object lastRequest = request.getAttribute("funambol.last-request");
            if ("true".equals(lastRequest)) {

                if (syncStartingTime != null) {
                    long syncProcessingTime = System.currentTimeMillis() - (Long)syncStartingTime;
                    long tmp = (Long)syncLatency;
                    tmp += processingTime;

                    //
                    // The following three lines should be synchronized but for performance
                    // reason I prefer avoid it since in the worse case calling
                    // getAverageSyncTime/getAverageSyncLatency the value of
                    // numberOfSyncs is not updated yet
                    // (cumulativeSyncTime/cumulativeSyncLatency/numberOfSyncs could
                    // be not consistent for a bit. No big issue working with big numbers)
                    //
                    cumulativeSyncTime.addAndGet(syncProcessingTime);
                    cumulativeSyncLatency.addAndGet(tmp);
                    numberOfSyncs.incrementAndGet();
                }
            } else {
                if (syncLatency != null) {
                    long tmp = (Long)syncLatency;
                    tmp += processingTime;
                    session.setAttribute(PARAM_SYNC_LATENCY, tmp);
                } else {
                    // first message
                    session.setAttribute(PARAM_SYNC_LATENCY, processingTime);
                }
            }

        }
    }
    
    /**
     * Destroy method for this filter 
     */
    public void destroy() {
        stop();
    }

    /**
     * Init method for this filter 
     * @param filterConfig the filter configuration
     */
    public void init(FilterConfig filterConfig) {
        if (log.isInfoEnabled()) {
            log.info("Initializing SyncResponseTimeFilter");
        }

        webappName = filterConfig.getInitParameter("webapp");
        if (webappName == null || webappName.equals("")) {
            webappName = "<not-specified>";
        }
        
        String mbean = MBEAN_NAME + ",path=" + webappName;
        
        if (log.isTraceEnabled()) {
            log.trace("Registering MBean: " + mbean);
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            // Construct the ObjectName for the MBean we will register
            ObjectName name = new ObjectName(mbean);

            mbs.registerMBean(this, name);

        } catch (Exception ex) {
            log.error("Error registering mbean '" + mbean + "'", ex);
        }

        start();
    }

    public synchronized void reset() {
        cumulativeResponseTime.set(0);
        cumulativeSyncTime.set(0);
        cumulativeSyncLatency.set(0);
        numberOfRequests.set(0);
        numberOfSyncs.set(0);
        maxResponseTime = 0;
        lastInstantInfo = new ArrayList<InstantInfo>();
    }

    public double getRecentAverageResponseTime() {

        long num = 0;
        long time = 0;

        //
        // it's sync on the timer since just the timer can update the list
        // (see method exec() )
        //
        synchronized (timer) {

            if (lastInstantInfo == null || lastInstantInfo.isEmpty()) {
                return 0;
            }
            int size = lastInstantInfo.size();

            InstantInfo first = lastInstantInfo.get(0);
            InstantInfo last = lastInstantInfo.get(size - 1);
            
            num = last.numberOfRequests - first.numberOfRequests;
            time = last.cumulativeResponseTime - first.cumulativeResponseTime;
        }
        if (num == 0) {
            return 0;
        }
        return round((double)time / num, 2);
    }
    
    public double getAverageResponseTime() {
        if (numberOfRequests.get() == 0) {
            return 0;
        }

        //
        // The following two lines should be synchronized but for performance
        // reason I prefer avoid it since in the worse case calling
        // getAverageResponseTime the value of cumulativeResponseTime has been updated
        // and numberOfRequests not yet
        // (cumulativeResponseTime and numberOfRequests could
        // be not consistent for a bit. No big issue working with big numbers)
        //
        long rt = cumulativeResponseTime.get();
        long num = numberOfRequests.get();

        return round((double)rt / num, 2);
    }

    public double getAverageSyncTime() {
        if (numberOfSyncs.get() == 0) {
            return 0;
        }

        //
        // The following two lines should be synchronized but for performance
        // reason I prefer avoid it since in the worse case calling
        // this method the value of cumulativeSyncTime has been updated
        // and numberOfSyncs not yet
        // (cumulativeSyncTime and numberOfSyncs could
        // be not consistent for a bit. No big issue working with big numbers)
        //
        long st = cumulativeSyncTime.get();
        long num = numberOfSyncs.get();

        return round((double)st / num, 2);
    }


    public double getAverageSyncLatency() {
        if (numberOfSyncs.get() == 0) {
            return 0;
        }

        //
        // The following two lines should be synchronized but for performance
        // reason I prefer avoid it since in the worse case calling
        // this method the value of cumulativeSyncLatency has been updated
        // and numberOfSyncs not yet
        // (cumulativeSyncLatency and numberOfSyncs could
        // be not consistent for a bit. No big issue working with big numbers)
        //
        long sl = cumulativeSyncLatency.get();
        long num = numberOfSyncs.get();

        return round((double)sl / num, 2);
    }

    
    // --------------------------------------------------------- Private methods

    private static double round(double d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Double.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.doubleValue();
    }

    private void start() {
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                exec();
            }
        }, 0, 60000);
    }

    private void stop() {
        timer.cancel();
    }

    private void exec() {
        InstantInfo info = new InstantInfo(numberOfRequests.get(), cumulativeResponseTime.get());

        synchronized (timer) {
            lastInstantInfo.add(info);
        
            if (lastInstantInfo.size() > 10) {
                lastInstantInfo.remove(0);
            }
        }
    }


    class InstantInfo {
        public long numberOfRequests = 0;
        public long cumulativeResponseTime = 0;

        private InstantInfo(long req, long time) {
            this.numberOfRequests = req;
            this.cumulativeResponseTime = time;
        }
    }
}

