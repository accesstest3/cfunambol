/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.util;

import java.io.PrintWriter;
import java.io.StringWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

import org.openide.ErrorManager;
import org.openide.windows.IOProvider;
import org.openide.windows.InputOutput;
import org.openide.windows.OutputWriter;

import com.funambol.admin.config.AdminConfig;

/**
 * Logs info message, debug message and throwable
 *
 *
 * @version $Id: Log.java,v 1.7 2007-11-28 10:28:17 nichele Exp $
 */
public class Log {

    // ----------------------------------------------------------- Private Data
    private static final DateFormat dateFormat = 
        new SimpleDateFormat("dd MMM yyyy HH:mm");

    // ---------------------------------------------------------- Public Methods

    /**
     * Log info message in the output windows and in the ide.log file
     * @param s message to log
     */
    public static void info(String s) {

        s = getDate() + " [INFO] " + s;

        // log info message in the ide.log file
        ErrorManager.getDefault().log(ErrorManager.ERROR, s);

        // log info message in the output windows
        getOW().println(s);
    }

    /**
     * Log debug message in the output windows and in the ide.log file
     * @param s message to log
     */
    public static void debug(String s) {
        if (isDebugEnabled()) {
            s = getDate() + " [DEBUG] " + s;
        
            // log info message in the ide.log file
            ErrorManager.getDefault().log(ErrorManager.ERROR, s);

            // log info message in the output windows
            getOW().println(s);
        }
    }

    /**
     * Log throwable.<br/>
     * If debug in enabled, the entire stacktrace is written  in the output 
     * windows and in the ide.log file.
     * <p/>
     * Otherwise only the error message is written.
     *
     * @param th throwable to log
     */
    public static void error(final Throwable th) {
        String errorMessage = th.toString();

        errorMessage = getDate() + " [ERROR] " + errorMessage;
        getOW().println(errorMessage);

        if (isDebugEnabled()) {
            String stackTrace = captureStackTrace(th);
            ErrorManager.getDefault().log(ErrorManager.ERROR, stackTrace);
            getOW().println(stackTrace);
        }
    }

    /**
     * Log throwable.<br/>
     * If debug in enabled, the entire stacktrace is written  in the output 
     * windows and in the ide.log file.
     * <p/>
     * Otherwise only the error message is written.
     *
     * @param th throwable to log
     */
    public static void error(final String msg) {
        getOW().println(getDate() + " [ERROR] " + msg);
    }

    /**
     * Log an error displaying the error message followed by the exception
     * message. If debug is enabled the stack trace is displayed too.
     *
     * @param msg the error message
     * @param cause the Throwable causing the error condition
     */
    public static void error(final String msg, final Throwable cause) {
        error(msg + ":");
        error(cause);
    }

    /**
     * Checks if the debug is enabled.
     *
     * @return true if the debug is enabled, false otherwise
     */
    public static boolean isDebugEnabled() {
        return AdminConfig.getAdminConfig().isDebugEnabled();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Returns the current date formatted
     * @return the current date formatted
     */
    private static String getDate() {
        long t = System.currentTimeMillis();
        String time = dateFormat.format(new Date(t));
        return time;
    }

    /**
     * Returns the stack trace information in the form of a string.
     *
     * @param throwable the throwable for which the trace should be captured
     * @return a string listing of the stack trace for the supplied throwable
     */
    public static String captureStackTrace(Throwable throwable) {
        if (throwable == null) {
            return "";
        } else {

            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw, true));

            return sw.toString();
        }
    }
    
    /**
     * It makes sure the output window is visible.
     *
     * @return the messages OutputWriter
     */
    public static InputOutput showLogWindow() {
        final String tab = Bundle.getMessage(Bundle.LABEL_MESSAGES_TAB);
        
        InputOutput io = IOProvider.getDefault().getIO(tab, false);
        
        if (io == null) {
            //
            // The tab has been never created
            //
            io = IOProvider.getDefault().getIO(tab, true);
        }
        io.select();
        
        return io;
    }
    
    /**
     * Returns the IOWindows's OutputWriter into which write logging messages. 
     * It also makes sure the output window is visible.
     *
     * @return the messages OutputWriter
     */
    private static OutputWriter getOW() {
        return showLogWindow().getOut();
    }

}
