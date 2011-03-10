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

package com.funambol.transport.http.server;

import java.io.IOException;

import java.util.Hashtable;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.funambol.framework.logging.LogContext;


/**
 * This is a servlet filter that handles LogContext lifespan and session.
 * When a new request is processed, the LogContext is cleaned and then it's
 * valorized with the values in the session.
 * <br/>
 * After processing, the LogContext values are stored in the session
 * so the next time a request with the same session is processed, those values are
 * restored.
 * <p>
 * Moreovew the thread name is changed added the username (if any) as postfix so that
 * for instance 'TP-Processor128' is renamed in 'TP-Processor128 [mark]'.
 * In this way we are able to know which user a thread is processing (for troubleshooting
 * long request)
 *
 * @version $Id: LogContextFilter.java,v 1.1.1.1 2008-02-21 23:36:08 stefano_fornari Exp $
 */
public class LogContextFilter implements Filter, Constants {

    // --------------------------------------------------------------- Constants

    // ---------------------------------------------------------- Public Methods
    /**
     * Initializes the filter
     * @param arg the configuration
     * @throws javax.servlet.ServletException if an error occurs
     */
    public void init(FilterConfig arg) throws ServletException {
    }

    /**
     * Filters the request
     * @param request the request
     * @param response the response
     * @param chain the filter chain
     * @throws java.io.IOException if an IO error occurs
     * @throws javax.servlet.ServletException if a Servlet error occurs
     */
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        LogContext.clear();

        HttpServletRequest httprequest     = (HttpServletRequest)request;

        HttpSession        session         = httprequest.getSession();
        String             sessionId       = session.getId();
        String             remoteAddr      = httprequest.getRemoteAddr();

        Hashtable          previousContext =
            (Hashtable)session.getAttribute(SESSION_ATTRIBUTE_LOG_CONTEXT);

        String deviceId = null;
        String userName = null;

        String originalThreadName = Thread.currentThread().getName();

        if (previousContext != null) {
            //
            // Sets in the current log context the previous values
            //
            LogContext.setValues(previousContext);

            //
            // Changing the thread name adding deviceid and username as postfix
            //
            deviceId = LogContext.getDeviceId();
            userName = LogContext.getUserName();
            String newThreadName = getNewThreadName(originalThreadName,
                                                    deviceId,
                                                    userName);
            Thread.currentThread().setName(newThreadName);
        } else {
            LogContext.setSessionId(sessionId);
        }
        //
        // The thread id and the remote address are always set
        //
        LogContext.setThreadId(String.valueOf(Thread.currentThread().getId()));
        LogContext.setRemoteAddress(remoteAddr != null ? remoteAddr : "");

        try {
            // Continue processing the rest of the filter chain.
            chain.doFilter(request, response);
        } finally {

            //
            // Removing the username from the thread name
            // (setting original thread name)
            //
            Thread.currentThread().setName(originalThreadName);

            //
            // Using Tomcat 5.5, processing the last client message, at this point
            // the session is invalidate (the Sync4jServlet invalidates the http session
            // if the syncml is completed).
            // If a session is invalidated, the setAttribute method throws an
            // exception.
            // From the servlet spec, request.getSession(false) returns null
            // if the session is expired/invalidated, so, if the current session
            // is null, the log context is not stored.
            //
            HttpSession currentSession =
                ((HttpServletRequest)request).getSession(false);

            if (currentSession != null) {
                //
                // Setting the current LogContext in the session in order to have the
                // values in the next request. We have to store a clone of that because
                // after that, the context is cleaned
                //
                session.setAttribute(SESSION_ATTRIBUTE_LOG_CONTEXT,
                                     LogContext.getValues().clone());
                LogContext.clear();
            }
        }
    }

    /**
     * Destroys the filter
     */
    public void destroy() {
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns the new thread name based on the current name and the current username
     * @param threadName the current thread name
     * @param deviceId the deviceId
     * @param userName the username
     * @return the new thread name =  threadName [deviceId] [username]
     */
    private static String getNewThreadName(String threadName,
                                           String deviceId,
                                           String userName) {
        if (threadName == null) {
            return threadName;
        }
        if (deviceId == null) {
            deviceId = "";
        }
        if (userName == null) {
            userName = "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(threadName);
        sb.append(" [").append(deviceId).append(']').append(" [").append(userName).append(']');
        return sb.toString();
    }

}
