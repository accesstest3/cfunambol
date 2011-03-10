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

import java.util.HashMap;
import java.util.ResourceBundle;

import org.apache.log4j.*;
import org.apache.log4j.spi.LoggingEvent;

/**
 * Wrapper around standard log4j logger adding user levels feature.
 * <br/>This logger keeps a Map with username/level couples so for wanted users
 * it's possible to have specific level. If there is not level specified for the
 * user, the logger level is used. (see Design Document for details)
 *
 * @version $Id: FunambolLogger.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class FunambolLogger {

    // Fully qualified class name.
    static String FQCN = FunambolLogger.class.getName();

    // -------------------------------------------------------------- Properties

    /**
     * Contains specific user levels.
     * That is intentionally an HashMap an not just a Map becauce for performance
     * considertaion
     */
    private HashMap<String, Level> userLevels = null;

    /**
     * Sets the Map with user/level couples
     * @param userLevels the map with user/level couples
     */
    public void setUserLevels(HashMap<String, Level> userLevels) {
        this.userLevels = userLevels;
    }

    // ------------------------------------------------------------ Private Data

    /** Wrapped logger */
    private Logger logger;

    /**
     * Creates a new logger with the given name
     * @param name the name of the logger
     */
    public FunambolLogger(String name) {
        this.logger = Logger.getLogger(name);
    }

    /**
     * Creates a new logger using clazz.getName() as logger name
     * @param clazz the class
     */
    public FunambolLogger(Class clazz) {
        this(clazz.getName());
    }

    // --- TRACE

    /**
     * Log a message object with TRACE level
     *
     * @param msg the object message
     */
    public void trace(Object msg) {
        log(FQCN, Level.TRACE, msg, null);
    }

    /**
     * Log a message object with TRACE level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void trace(Object msg, Throwable t) {
        log(FQCN, Level.TRACE, msg, t);
    }

    /**
     * Check whether this logger is enabled for the TRACE Level
     * @return <CODE>true</CODE> if TRACE level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isTraceEnabled() {
        return Level.TRACE.isGreaterOrEqual(this.getEffectiveLevel());
    }

    // -- DEBUG

    /**
     * Log a message object with DEBUG level
     *
     * @param msg the object message
     */
    public void debug(Object msg) {
        log(FQCN, Level.DEBUG, msg, null);
    }

    /**
     * Log a message object with DEBUG level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void debug(Object msg, Throwable t) {
        log(FQCN, Level.DEBUG, msg, t);
    }

    /**
     * Check whether this logger is enabled for the DEBUG Level
     * @return <CODE>true</CODE> if DEBUG level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isDebugEnabled() {
        return Level.DEBUG.isGreaterOrEqual(this.getEffectiveLevel());
    }

    // -- INFO

    /**
     * Log a message object with INFO level
     *
     * @param msg the object message
     */
    public void info(Object msg) {
        log(FQCN, Level.INFO, msg, null);
    }

    /**
     * Log a message object with INFO level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void info(Object msg, Throwable t) {
        log(FQCN, Level.INFO, msg, t);
    }

    /**
     * Check whether this logger is enabled for the INFO Level
     * @return <CODE>true</CODE> if INFO level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isInfoEnabled() {
        return Level.INFO.isGreaterOrEqual(this.getEffectiveLevel());
    }

    // -- WARNING

    /**
     * Log a message object with WARN level
     *
     * @param msg the object message
     */
    public void warn(Object msg) {
        log(FQCN, Level.WARN, msg, null);
    }

    /**
     * Log a message object with WARN level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void warn(Object msg, Throwable t) {
        log(FQCN, Level.WARN, msg, t);
    }

    /**
     * Check whether this logger is enabled for the WARN Level
     * @return <CODE>true</CODE> if WARN level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isWarningEnabled() {
        return Level.WARN.isGreaterOrEqual(this.getEffectiveLevel());
    }

    // -- ERROR

    /**
     * Log a message object with ERROR level
     *
     * @param msg the object message
     */
    public void error(Object msg) {
        log(FQCN, Level.ERROR, msg, null);
    }

    /**
     * Log a message object with ERROR level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void error(Object msg, Throwable t) {
        log(FQCN, Level.ERROR, msg, t);
    }

    /**
     * Check whether this logger is enabled for the ERROR Level
     * @return <CODE>true</CODE> if ERROR level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isErrorEnabled() {
        return Level.ERROR.isGreaterOrEqual(this.getEffectiveLevel());
    }

    // -- FATAL

    /**
     * Log a message object with FATAL level
     *
     * @param msg the object message
     */
    public void fatal(Object msg) {
        log(FQCN, Level.FATAL, msg, null);
    }

    /**
     * Log a message object with FATAL level including the stack trace of the
     * given Throwable
     *
     * @param msg the message object
     * @param t the throwable object to log
     */
    public void fatal(Object msg, Throwable t) {
        log(FQCN, Level.FATAL, msg, t);
    }

    /**
     * Check whether this logger is enabled for the FATAL Level
     * @return <CODE>true</CODE> if FATAL level is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isFatalEnabled() {
        return Level.FATAL.isGreaterOrEqual(this.getEffectiveLevel());
    }


    /**
     * Check whether this logger is enabled for the given Level
     * @param level the lvel to check
     * @return <CODE>true</CODE> if level is enabled for the given level, <CODE>false</CODE> otherwise
     */
    public boolean isEnabledFor(Level level) {
        return level.isGreaterOrEqual(this.getEffectiveLevel());
    }

    /**
     * Removes all appenders
     */
    public void removeAllAppenders() {
        logger.removeAllAppenders();
    }

    /**
     * Adds a new appender
     * @param newAppender the appender to add
     */
    public void addAppender(Appender newAppender) {
        logger.addAppender(newAppender);
    }

    /**
     * Sets the additivity flag for this logger instance.
     * @param isAdditivity the additivity value
     */
    public void setAdditivity(boolean isAdditivity) {
        logger.setAdditivity(isAdditivity);
    }

    /**
     * Sets the level for this logger
     * @param level the level to set
     */
    public void setLevel(Level level) {
        logger.setLevel(level);
    }

    /**
     * Sets the resource bundle for this logger
     * @param bundle the resource bundle to set
     */
    public void setResourceBundle(ResourceBundle bundle) {
        logger.setResourceBundle(bundle);
    }

    // -------------------------------------------------------- Protected method
    /**
     * This is the most generic logging method.
     * @param callerFQCN the wrapper class' fully qualified class name
     * @param level the level of the logging request
     * @param message the object message
     * @param t the throwable to log
     */
    protected void log(String callerFQCN, Priority level, Object message, Throwable t) {
        if(level.isGreaterOrEqual(this.getEffectiveLevel())) {
          forcedLog(callerFQCN, level, message, t);
      }
    }

    /**
     * This method creates a new logging event and logs the event
     * without further checks.
     * @param fqcn the wrapper class' fully qualified class name
     * @param level the level of the logging request
     * @param message the object message
     * @param t the throwable to log
     */
    protected void forcedLog(String fqcn,
                             Priority level,
                             Object message,
                             Throwable t) {
        logger.callAppenders(new LoggingEvent(fqcn, logger, level, message, t));
    }


    // ---------------------------------------------------------- Private method

    /**
     * Returns the effective level of the logger based on the username in the
     * LogContext. If there is not a specific level for the user, the logger level
     * is returned
     * @return the effective logger level
     */
    private Level getEffectiveLevel() {
        String userName = LogContext.getUserName();

        for(Category c = logger; c != null; c=c.getParent()) {
            if(c.getLevel() != null) {
                //
                // Before the userLevels are checked
                //
                FunambolLogger fnblLogger = FunambolLoggerFactory.getLogger(c.getName());

                userLevels = fnblLogger.userLevels;

                if (userLevels != null) {
                    Level userLevel = getUserLevel(userName);
                    if (userLevel != null) {
                        return userLevel;
                    }

                }
                return c.getLevel();                
            }
        }
        return null; // If reached will cause an NullPointerException.
    }


    /**
     * Checks if in the userLevels map there is a specific level for the given user
     * @return the level for the specified user or null if no level is specified in
     *         the userLevels map.
     */
    private Level getUserLevel(String userName) {
        if (userName != null && userLevels != null) {
            return userLevels.get(userName);
        }
        return null;
    }

}
