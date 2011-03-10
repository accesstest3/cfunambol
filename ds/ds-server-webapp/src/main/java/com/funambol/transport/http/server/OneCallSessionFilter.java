/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
 * This is a servlet filter that closes the session after request processing.
 * It can be usefull for axis request since any request creates a new session and
 * one session is used just for one request.
 *
 * @version $Id: OneCallSessionFilter.java,v 1.1 2008-06-14 17:55:59 nichele Exp $
 */
public class OneCallSessionFilter implements Filter, Constants {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data
    private FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

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

        try {
            // Continue processing the rest of the filter chain.
            chain.doFilter(request, response);
        } finally {
            //
            // From the servlet spec, request.getSession(false) returns null
            // if the session is expired/invalidated, so, if the current session
            // is null, it can not be invalidate (and it doesn't need that, since
            // it is already expired)
            //
            HttpSession currentSession =
                ((HttpServletRequest)request).getSession(false);

            ((HttpServletRequest)request).setAttribute(ATTRIBUTE_LAST_REQUEST, "true");
            
            if (currentSession != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Closing session: " + currentSession.getId());
                }
                currentSession.invalidate();
            }
        }
    }

    /**
     * Destroys the filter
     */
    public void destroy() {
    }
}
