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
package com.funambol.server.config;

/**
 * This interface groups configuration related constants.
 *
 *
 * @version $Id: ConfigurationConstants.java,v 1.5 2008-06-05 07:33:43 nichele Exp $
 */
public interface ConfigurationConstants {
    public static final String CONFIG_PATH = java.io.File.separator + "config";

    /**
     * @deprecated Since v66 use PROPERTY_SYSTEM_FUNAMBOL_HOME
     */
    public static final String PROPERTY_SYSTEM_FUNAMBOL_DS_HOME = "funambol.ds.home";    
    public static final String PROPERTY_SYSTEM_FUNAMBOL_HOME    = "funambol.home";
    public static final String PROPERTY_SYSTEM_FUNAMBOL_DEBUG   = "funambol.debug";
    public static final String BEAN_SERVER_CONFIGURATION        = "Funambol.xml";

    /** The scan period of the directory monitor */
    public static final long DIRECTORY_MONITOR_SCAN_PERIOD = 10000;  // 30 seconds

    /** The path of the logging configuration */
    public static final String PATH_LOGGING = "com/funambol/server/logging";

    /** The path of the loggers */
    public static final String PATH_LOGGER = PATH_LOGGING + "/logger";

    /** The path of the appenders */
    public static final String PATH_APPENDER = PATH_LOGGING + "/appender";

    /** The path of the appender type*/
    public static final String PATH_APPENDER_TYPE = PATH_APPENDER + "/appender-type";

    /** The path of the persistent stores */
    public static final String PATH_PERSISTENT_STORE = "com/funambol/server/store";

    /** The path of the synclets */
    public static final String PATH_SYNCLET = "com/funambol/server/engine/pipeline";

    /** The path of the input processor */
    public static final String PATH_SYNCLET_INPUT = PATH_SYNCLET + "/input";

    /** The path of the output processor */
    public static final String PATH_SYNCLET_OUTPUT = PATH_SYNCLET + "/output";
    
    /** The path of the pluging */
    public static final String PATH_PLUGIN = "com/funambol/server/plugin";
}
