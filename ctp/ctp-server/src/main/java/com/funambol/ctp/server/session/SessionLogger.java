/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.ctp.server.session;

import org.apache.log4j.Logger;

/**
 * A special logger used to append session specific informations
 * to log messages
 * @version $Id: SessionLogger.java,v 1.2 2007-11-28 11:26:17 nichele Exp $
 */
public class SessionLogger {

    /**
     * The underlying logger used to log messages
     */
    private Logger logger = null;

    /**
     * The prefix string appended to every log messages
     */
    private String prefix = "";

    /**
     * Returns the prefix message
     * @return The prefix message
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Sets the prefix message appended to all log messages
     * @param prefix The prefix message appended to all log messages
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * <p>Creates a new instance of <CODE>SessionLogger</CODE></p>
     * @param loggerName The name used by the logger
     */
    public SessionLogger(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Writes a trace message appending the prefix string
     * @param message The message to log
     */
    public void trace(String message) {
        logger.trace(prefix + message);
    }

    /**
     * Writes a trace message appending the prefix string
     * @param message The message to log
     * @param cause The throwable to log
     */
    public void trace(String message, Throwable cause) {
        logger.trace(prefix + message, cause);
    }

    /**
     * Writes a debug message appending the prefix string
     * @param message The message to log
     */
    public void debug(String message) {
        logger.debug(prefix + message);
    }

    /**
     * Writes a debug message appending the prefix string
     * @param message The message to log
     * @param cause The throwable to log
     */
    public void debug(String message, Throwable cause) {
        logger.debug(prefix + message, cause);
    }

    /**
     * Writes an info message appending the prefix string
     * @param message The message to log
     */
    public void info(String message) {
        logger.info(prefix + message);
    }

    /**
     * Writes an info message appending the prefix string
     * @param message The message to log
     * @param cause The throwable to log
     */
    public void info(String message, Throwable cause) {
        logger.info(prefix + message, cause);
    }

    /**
     * Writes a warn message appending the prefix string
     * @param message The message to log
     */
    public void warn(String message) {
        logger.warn(prefix + message);
    }

    /**
     * Writes a warn message appending the prefix string
     * @param message The message to log
     * @param cause The throwable to log
     */
    public void warn(String message, Throwable cause) {
        logger.warn(prefix + message, cause);
    }

    /**
     * Writes an error message appending the prefix string
     * @param message The message to log
     */
    public void error(String message) {
        logger.error(prefix + message);
    }

    /**
     * Writes an error message appending the prefix string
     * @param message The message to log
     * @param cause The throwable to log
     */
    public void error(String message, Throwable cause) {
        logger.error(prefix + message, cause);
    }

    /**
     * Returns true if the logger is enabled for trace messages
     * @return True if the logger is enabled for trace messages, false otherwise
     */
    public boolean isTraceEnabled() {
        return logger.isTraceEnabled();
    }

    /**
     * Returns true if the logger is enabled for debug messages
     * @return True if the logger is enabled for debug messages, false otherwise
     */
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    /**
     * Returns true if the logger is enabled for info messages
     * @return True if the logger is enabled for info messages, false otherwise
     */
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }
}
