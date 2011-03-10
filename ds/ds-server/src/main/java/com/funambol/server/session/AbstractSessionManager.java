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
package com.funambol.server.session;

import java.io.Serializable;

import java.util.Collection;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.server.session.SessionHandler;
import com.funambol.server.session.SessionExpiredException;

/**
 * This is a basic implementation of a session manager. Subclasses have to
 * extend it in order to get and create sessions.
 *
 * @version $Id: AbstractSessionManager.java,v 1.1.1.1 2008-02-21 23:35:55 stefano_fornari Exp $
 */
public abstract class AbstractSessionManager implements Serializable {

    // -------------------------------------------------------------- Properties

    /**
     * The lifetime of a session in minutes
     */
    private int lifeTime = 5;

    /** Getter for property lifeTime.
     * @return Value of property lifeTime.
     */
    public int getLifeTime() {
        return lifeTime;
    }

    /** Setter for property lifeTime.
     * @param lifeTime New value of property lifeTime.
     */
    public void setLifeTime(int lifeTime) {
        this.lifeTime = lifeTime;
    }


    // ------------------------------------------------------------ Private data

    /**
     * The logger
     */
    private static final FunambolLogger log = FunambolLoggerFactory.getLogger();

    // ---------------------------------------------------------- Public methods

    public SessionHandler getSessionHandler(String sessionId)
    throws SessionExpiredException {
        if (log.isTraceEnabled()) {
            log.trace("Looking for session " + sessionId);
        }

        SessionHandler handler = getSessionFromBag(sessionId);

        if (handler == null) {
            handler  = createNewSession(sessionId);
            putSessionInBag(sessionId, handler);
        } else {
            if (handler.isNew()) {
                handler.setNew(false);
                putSessionInBag(sessionId, handler);
            }
        }

        return handler;
    }

    public void removeSession(String sessionId) {
        if (log.isTraceEnabled()) {
            log.trace("Removing session " + sessionId);
        }

        try {
            SessionHandler oldSession = getSessionFromBag(sessionId);
            oldSession.expire();
            removeSessionFromBag(sessionId);
        } catch (SessionExpiredException e) {
            if (log.isTraceEnabled()) {
                log.trace("Session already expired");
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Remaining sessions: " + getSessionBag());
        }
    }

    public void removeSession(SessionHandler handler) {
        if (log.isTraceEnabled()) {
            log.trace("Removing session " + ((handler!=null) ? handler.getSessionId() : "null"));
        }

        handler.expire();
        removeSessionFromBag(handler.getSessionId());

        if (log.isTraceEnabled()) {
            log.trace("Remaining sessions: " + getSessionBag());
        }
    }

    public void storeSessionHandler(SessionHandler handler) {
        if (log.isTraceEnabled()) {
            log.trace("Storing session " + handler);
        }

        putSessionInBag(handler.getSessionId(), handler);
    }


    // -------------------------------------------------------- Abstract methods

    abstract protected SessionHandler createNewSession(String sessionId);
    abstract protected SessionHandler getSessionFromBag(String sessionId) throws SessionExpiredException;
    abstract protected void putSessionInBag(String sessionId, SessionHandler handler);
    abstract protected void removeSessionFromBag(String sessionId);
    abstract protected Collection getSessionBag();
}

