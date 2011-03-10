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

package com.funambol.framework.logging;

import java.util.Hashtable;
import java.util.Set;

import org.apache.log4j.MDC;

/**
 * It's the repository of the information used by the Logging architecture to store
 * thread variables. Uses org.apache.log4j.MDC as real implementation of the
 * repository, providing just some useful methods to hidden the MDC use.
 * <br/>
 * See log4j documentation for detail about MDC class.
 *
 * @version $Id: LogContext.java,v 1.3 2008-05-15 05:53:08 nichele Exp $
 */
public class LogContext {

    // --------------------------------------------------------------- Constants
    private static final String PARAMETER_USER_NAME       = "userName" ;
    private static final String PARAMETER_SESSION_ID      = "sessionId";
    private static final String PARAMETER_DEVICE_ID       = "deviceId" ;
    private static final String PARAMETER_SOURCE_URI      = "sourceURI";
    private static final String PARAMETER_THREAD_ID       = "threadId" ;
    private static final String PARAMETER_REMOTE_ADDRESS  = "remoteAddress" ;

    /**
     * The username is stored also in 'username' since some components like the
     * pimlistener and the inboxlistener use 'username'.
     * Its usage is deprecated.
     * @deprecated Use PARAMETER_USER_NAME
     */
    private static final String PARAMETER_USER_NAME_LOWERCASE  = "username" ;

    // ------------------------------------------------------------- Constructor

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the userName stored in the thread space
     * @return the userName stored in the thread space
     */
    public static String getUserName() {
        return (String)MDC.get(PARAMETER_USER_NAME);
    }

    /**
     * Stores in the thread space the given userName.
     * If the given userName is null, the userName in the thread space is removed
     * @param userName the userName to set
     */
    public static void setUserName(String userName) {
        if (userName == null) {
            MDC.remove(PARAMETER_USER_NAME);
            MDC.remove(PARAMETER_USER_NAME_LOWERCASE);
        } else {
            MDC.put(PARAMETER_USER_NAME, userName);
            MDC.put(PARAMETER_USER_NAME_LOWERCASE, userName);
        }
    }

    /**
     * Removes from the thread space the username
     */
    public static void removeUserName() {
        MDC.remove(PARAMETER_USER_NAME);
        MDC.remove(PARAMETER_USER_NAME_LOWERCASE);
    }

    /**
     * Returns the thread id stored in the thread space
     * @return the thread id stored in the thread space
     */
    public static String getThreadId() {
        return (String)MDC.get(PARAMETER_THREAD_ID);
    }

    /**
     * Stores in the thread space the given thread id.
     * If the given thread id is null, the thread id in the thread space is removed
     * @param threadId the thread id to set
     */
    public static void setThreadId(String threadId) {
        if (threadId == null) {
            MDC.remove(PARAMETER_THREAD_ID);
        } else {
            MDC.put(PARAMETER_THREAD_ID, threadId);
        }
    }

    /**
     * Removes from the thread space the threadId
     */
    public static void removeThreadId() {
        MDC.remove(PARAMETER_THREAD_ID);
    }

    /**
     * Returns the device id stored in the thread space
     * @return the device id stored in the thread space
     */
    public static String getDeviceId() {
        return (String)MDC.get(PARAMETER_DEVICE_ID);
    }

    /**
     * Stores in the thread space the given deviceId.
     * If the given deviceId is null, the deviceId in the thread space is removed.
     * @param deviceId the device id to set
     */
    public static void setDeviceId(String deviceId) {
        if (deviceId == null) {
            MDC.remove(PARAMETER_DEVICE_ID);
        } else {
            MDC.put(PARAMETER_DEVICE_ID, deviceId);
        }
    }

    /**
     * Removes from the thread space the deviceId
     */
    public static void removeDeviceId() {
        MDC.remove(PARAMETER_DEVICE_ID);
    }

    /**
     * Returns the session id stored in the thread space
     * @return the session id stored in the thread space
     */
    public static String getSessionId() {
        return (String)MDC.get(PARAMETER_SESSION_ID);
    }

    /**
     * Stores in the thread space the given sessionId.
     * If the given sessionId is null, the sessionId in the thread space is removed.
     * @param sessionId the session id to set
     */
    public static void setSessionId(String sessionId) {
        if (sessionId == null) {
            MDC.remove(PARAMETER_SESSION_ID);
        } else {
            MDC.put(PARAMETER_SESSION_ID, sessionId);
        }
    }

    /**
     * Removes from the thread space the deviceId
     */
    public static void removeSessionId() {
        MDC.remove(PARAMETER_SESSION_ID);
    }

    /**
     * Returns the source uri stored in the thread space
     * @return the source uri stored in the thread space
     */
    public static String getSourceURI() {
        return (String)MDC.get(PARAMETER_SOURCE_URI);
    }

    /**
     * Stores in the thread space the given sourceURI.
     * If the given sourceURI is null, the sourceURI in the thread space is removed.
     * @param sourceURI the source uri to set
     */
    public static void setSourceURI(String sourceURI) {
        if (sourceURI == null) {
            MDC.remove(PARAMETER_SOURCE_URI);
        } else {
            MDC.put(PARAMETER_SOURCE_URI, sourceURI);
        }
    }

    /**
     * Removes from the thread space the sourceURI
     */
    public static void removeSourceURI() {
        MDC.remove(PARAMETER_SOURCE_URI);
    }

    /**
     * Returns the remote address stored in the thread space
     * @return the remote address stored in the thread space
     */
    public static String getRemoteAddress() {
        return (String)MDC.get(PARAMETER_REMOTE_ADDRESS);
    }

    /**
     * Stores in the thread space the given remote address.
     * If the given remote address is null, the remote address in the thread space is removed.
     * @param the remote address to set
     */
    public static void setRemoteAddress(String remoteAddress) {
        if (remoteAddress == null) {
            MDC.remove(PARAMETER_REMOTE_ADDRESS);
        } else {
            MDC.put(PARAMETER_REMOTE_ADDRESS, remoteAddress);
        }
    }

    /**
     * Removes from the thread space the remote address
     */
    public static void removeRemoteAddress() {
        MDC.remove(PARAMETER_REMOTE_ADDRESS);
    }

    /**
     * Stores in the thread space the given value with the given key.
     * If the given value is null, the given key in the thread space is removed.
     * @param key the key of the object to store
     * @param value the object to store
     */
    public static void set(String key, Object value) {
        if (value == null) {
            MDC.remove(key);
        } else {
            MDC.put(key, value);
        }
    }

    /**
     * Removes from the thread space the object with the given key
     * @param key the key of the object to remove
     */
    public static void remove(String key) {
        MDC.remove(key);
    }

    /**
     * Removes from the thread space all values
     */
    public static void clear() {
        Hashtable context = MDC.getContext();
        if (context != null) {
            context.clear();
        }

    }

    /**
     * Returns the value of the variable in the thread space with the given key
     * @param key the key of the value
     * @return the value of the given key
     */
    public static Object getValue(String key) {
        return MDC.get(key);
    }

    /**
     * Returns all values in the thread space
     * @return all values in the thread space
     */
    public static Hashtable getValues() {
        return MDC.getContext();
    }

    /**
     * Sets in the thread space all values contained in the given Hashtable deleting
     * the old ones (a <code>clear()</code> is performed before to set the values)
     * @param values the values to set in the thread space
     */
    public static void setValues(Hashtable values) {
        clear();
        if (values == null) {
            return;
        }
        Set keys = values.keySet();
        for (Object key : keys) {
            MDC.put((String)key, values.get(key));
        }
    }

}
