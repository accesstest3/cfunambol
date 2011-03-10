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



package com.funambol.framework.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * This is a very basic logger that traces only <i>level, timestamp and message</i>.
 * The use of this logger has two advantages:
 * <ul>
 *  <li>it is less verbose (it does not display class and method name)</li>
 *  <li>it is quicker (it does not look into the stack trace in order to
 *      infer class and method name</li>
 * </ul>
 *
 *
 *
 * @version $Id: SimpleFormatter.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */

public class SimpleFormatter extends Formatter {

    // --------------------------------------------------------------- Constants

    private final static String NL = System.getProperty("line.separator");
    private final static String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss,SSS";

    // ---------------------------------------------------------- Private fields

    private SimpleDateFormat dateFormatter;

    // ---------------------------------------------------------- Public methids

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     *
     * @return a formatted log record as String
     */
    public synchronized String format(LogRecord record) {

        StringBuffer sb = new StringBuffer();

        if (dateFormatter == null) {
            dateFormatter = new SimpleDateFormat(DATE_FORMAT);
        }

        // Date
        sb.append('[');
        sb.append(dateFormatter.format(new Date(record.getMillis())));
        sb.append(']');

        // Logger name. It's logged only if the logger level is greater than INFO
        String loggerName = record.getLoggerName();
        //if (LogManager.getLogManager().getLogger(loggerName).getLevel().intValue() < Level.INFO.intValue()) {
            sb.append(" [").append(record.getLoggerName()).append(']');
        //}

        // Logger level
        sb.append(" [");
        sb.append(record.getLevel().getName());
        sb.append(']');

        // SessionId
        sb.append(" [");
        String sessionId = LogContext.getSessionId();
        if (sessionId != null && !sessionId.equals("")) {
            sb.append(sessionId);
        }
        sb.append(']');
        sessionId = null;

        // ThreadId
        sb.append(" [");
        String threadId = (String)LogContext.getValue("thread-id");
        if (threadId != null && !threadId.equals("")) {
            sb.append(threadId);
        }
        sb.append(']');
        threadId = null;

        // Device
        sb.append(" [");
        String device = LogContext.getDeviceId();
        if (device != null && !device.equals("")) {
            sb.append(device);
        }
        sb.append(']');
        device = null;

        // User
        sb.append(" [");
        String user = LogContext.getUserName();
        if (user != null && !user.equals("")) {
            sb.append(user);
        }
        sb.append(']');
        user = null;

        // SourceURI
        sb.append(" [");
        String sourceURI = LogContext.getSourceURI();
        if (sourceURI != null && !sourceURI.equals("")) {
            sb.append(sourceURI);
        }
        sb.append("] ");
        sourceURI = null;

        sb.append(formatMessage(record));
        sb.append(NL);

        if (record.getThrown() != null) {
            StringWriter sw = new StringWriter(  );
            PrintWriter  pw = new PrintWriter (sw);
            try {
                record.getThrown().printStackTrace(pw);
                sb.append(sw.toString());
                sb.append(NL);
            } catch (Exception ex) {
                //
                // What to do here???
                //
            } finally {
                try { pw.close(); } catch (Exception e) {}
            }
        }

        return sb.toString();
    }
}
