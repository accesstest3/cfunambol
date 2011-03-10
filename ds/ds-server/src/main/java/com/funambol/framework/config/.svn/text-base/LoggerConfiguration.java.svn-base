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
package com.funambol.framework.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

/**
 * This class is just a container of logger configuration information.
 *
 * @version $Id: LoggerConfiguration.java,v 1.1.1.1 2008-02-21 23:35:37 stefano_fornari Exp $
 */
public class LoggerConfiguration
implements java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static final String LEVEL_OFF    = "OFF"   ;
    public static final String LEVEL_FATAL  = "FATAL" ;
    public static final String LEVEL_ERROR  = "ERROR" ;
    public static final String LEVEL_WARN   = "WARN"  ; 
    public static final String LEVEL_INFO   = "INFO"  ;
    public static final String LEVEL_DEBUG  = "DEBUG" ;
    public static final String LEVEL_TRACE  = "TRACE" ;
    public static final String LEVEL_ALL    = "ALL"   ;

    // ------------------------------------------------------------ Private data

    /**
     * The logger name
     */
    private String name;

    /**
     * The logging level
     */
    private String level;

    /**
     * Does this logger inheriths from its parent logger?
     */
    private boolean inherit;

    /**
     * List of appender names
     */
    private List<String> appenders;

    /**
     * Contains a list of the users with Level.ALL
     */
    private List<String> usersWithLevelALL;

    /**
     * shall the output be written to the console too?
     * @deprecated Use new appenders architecture
     */
    private boolean consoleOutput;

    /**
     * shall the output be written to a file too?
     * @deprecated Use new appenders architecture
     */
    private boolean fileOutput;

    /**
     * the filename pattern; it follows the FileHandler's pattern conventions.
     * @deprecated Use new appenders architecture
     */
    private String pattern;

    /**
     * specifies an approximate maximum amount to write (in bytes) to any one
     * file. If this is zero, then there is no limit.
     * @deprecated Use new appenders architecture
     */
    private int limit;

    /**
     * specifies how many output files to cycle through (defaults to 1).
     * @deprecated Use new appenders architecture
     */
    private int count;

    /**
     * specifies whether the FileHandler should append onto any existing files
     * (defaults to false).
     * @deprecated Use new appenders architecture
     */
    private boolean append;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of LoggingConfiguration */
    public LoggerConfiguration() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Getter for property level.
     * @return Value of property level.
     */
    public String getLevel() {
        return level;
    }

    /**
     * Setter for property level.
     * @param level New value of property level. If a java.util.logging debug
     * level string is specified, then it is mapped to a log4j level string as
     * follows:
     * <p/>
     * <table>
     *     <tr><td>java.util.logging</td><td>log4j</td></tr>
     *     <tr><td>SEVERE</td><td>ERROR</td></tr>
     *     <tr><td>WARNING</td><td>WARN</td></tr>
     *     <tr><td>FINE/FINER</td><td>DEBUG</td></tr>
     *     <tr><td>FINEST</td><td>TRACE</td></tr>
     * </table>
     * ALL, OFF and INFO levels are not changed.
     */
    public void setLevel(String level) {
        
        if (java.util.logging.Level.SEVERE.toString().equals(level)){
            this.level = org.apache.log4j.Level.ERROR.toString();
        } else if (java.util.logging.Level.WARNING.toString().equals(level)){
            this.level = org.apache.log4j.Level.WARN.toString();
        } else if (java.util.logging.Level.FINE.toString().equals(level) || 
                   java.util.logging.Level.FINER.toString().equals(level)){
            this.level = org.apache.log4j.Level.DEBUG.toString();
        } else if (java.util.logging.Level.FINEST.toString().equals(level)) {
            this.level = org.apache.log4j.Level.TRACE.toString();
        } else {
            this.level = level;
        }
    }

    /**
     * Getter for property consoleOutput.
     * @return Value of property consoleOutput.
     * @deprecated Use new appenders architecture
     */
    public boolean isConsoleOutput() {
        return consoleOutput;
    }

    /**
     * Setter for property consoleOutput.
     * @param consoleOutput New value of property consoleOutput.
     * @deprecated Use new appenders architecture
     */
    public void setConsoleOutput(boolean consoleOutput) {
        this.consoleOutput = consoleOutput;
    }

    /**
     * Getter for property fileOutput.
     * @return Value of property fileOutput.
     * @deprecated Use new appenders architecture
     */
    public boolean isFileOutput() {
        return fileOutput;
    }

    /**
     * Setter for property fileOutput.
     * @param fileOutput New value of property fileOutput.
     */
    public void setFileOutput(boolean fileOutput) {
        this.fileOutput = fileOutput;
    }

    /**
     * Getter for property pattern.
     * @return Value of property pattern.
     */
    public java.lang.String getPattern() {
        return pattern;
    }

    /**
     * Getter for property limit.
     * @return Value of property limit.
     * @deprecated Use new appenders architecture
     */
    public int getLimit() {
        return limit;
    }

    /**
     * Setter for property limit.
     * @param limit New value of property limit.
     * @deprecated Use new appenders architecture
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

    /**
     * Setter for property pattern.
     * @param pattern New value of property pattern.
     * @deprecated Use new appenders architecture
     */
    public void setPattern(java.lang.String pattern) {
        this.pattern = pattern;
    }

    /**
     * Getter for property count.
     * @return Value of property count.
     * @deprecated Use new appenders architecture
     */
    public int getCount() {
        return count;
    }

    /**
     * Setter for property count.
     * @param count New value of property count.
     * @deprecated Use new appenders architecture
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Getter for property append.
     * @return Value of property append.
     * @deprecated Use new appenders architecture
     */
    public boolean isAppend() {
        return append;
    }

    /**
     * Setter for property append.
     * @param append New value of property append.
     * @deprecated Use new appenders architecture
     */
    public void setAppend(boolean append) {
        this.append = append;
    }

    /**
     * Getter for property name.
     * @return Value of property name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for property name.
     * @param name New value of property name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for property appenders.
     * @return Value of property appenders.
     */
    public List getAppenders() {
        return appenders;
    }

    /**
     * Setter for property appenders.
     * @param appenders New value of property appenders.
     */
    public void setAppenders(List appenders) {
        this.appenders = appenders;
    }

    /**
     * Getter for property inherit.
     * @return Value of property inherit.
     */
    public boolean isInherit() {
        return inherit;
    }

    /**
     * Setter for property inherit.
     * @param inherit New value of property inherit.
     */
    public void setInherit(boolean inherit) {
        this.inherit = inherit;
    }

    /**
     * Getter for property usersLevel
     * @return the usersLevel
     */
    public List<String> getUsersWithLevelALL() {
        return usersWithLevelALL;
    }

    /**
     * Setter for property usersWithLevelALL
     * @param users the users to set
     */
    public void setUsersWithLevelALL(List<String> users) {
        this.usersWithLevelALL = users;
    }

}
