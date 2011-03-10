/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

import java.util.Hashtable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSessionEvent;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.LogContext;

/**
 * It's a HttpSessionListener used to close the SyncHolder when a session expires
 * (caused by session timeout) or when a session is invalidated by the Sync4jServlet
 * (when the SyncML session is completed)
 * @version $Id: SessionListener.java,v 1.1.1.1 2008-02-21 23:36:08 stefano_fornari Exp $
 */
public class SessionListener implements HttpSessionListener, Constants {

    // ------------------------------------------------------------ Private data

    private FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

    // ---------------------------------------------------------- Public Methods

    /**
     * Called when a session is created.
     */
    public void sessionCreated(HttpSessionEvent evt) {
    }

    /**
     * Called when a session is destroyed(invalidated).<br/>
     * It closes the SyncHolder (if not null). It uses the LogContext in the session
     * to provide useful information about the destroyed session. Note that the
     * log context is not stored in session at the end of the method because
     * the session is destroyed.
     */
    public void sessionDestroyed(HttpSessionEvent evt) {
        HttpSession session = evt.getSession();

        LogContext.clear();

        String             sessionId       = session.getId();

        Hashtable          previousContext =
            (Hashtable)session.getAttribute(SESSION_ATTRIBUTE_LOG_CONTEXT);

        if (previousContext != null) {
            //
            // Sets in the current log context the previous values
            //
            LogContext.setValues(previousContext);
        } else {
            LogContext.setSessionId(sessionId);
        }

        //
        // The thread id is always set
        //
        LogContext.setThreadId(String.valueOf(Thread.currentThread().getId()));

        if (log.isInfoEnabled()) {
            log.info("Closing session " + session.getId());
        }

        SyncHolder holder = (SyncHolder)session.getAttribute(SESSION_ATTRIBUTE_SYNC_HOLDER);
        try {
            if (holder != null) {
                if (log.isTraceEnabled()) {
                    log.trace("Closing holder for " + sessionId);
                }
                holder.close();
            }
        } catch (Exception e) {
            log.error("Error closing the holder", e);
        }

        LogContext.clear();
    }

}
