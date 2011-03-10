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

import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * It's a factory of <code>FunambolLogger</code>
 *
 * @version $Id: FunambolLoggerFactory.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class FunambolLoggerFactory  {

    // --------------------------------------------------------------- Constants
    /**
     * The prefix of all loggers
     */
    public static final String LOGGER_BASE_NAME = "funambol";

    // ----------------------------------------------------------- Constructors

    /**
     * Creates a new factory
     */
    public FunambolLoggerFactory() {
        super();
    }


    // ----------------------------------------------------- Manifest Constants


    // ----------------------------------------------------- Instance Variables


    /**
     * The logger instances that have already been created, keyed by logger name.
     */
    protected static Hashtable<String, FunambolLogger> instances = new Hashtable();


    // --------------------------------------------------------- Public Methods


    /**
     * Returns the logger based on the name of the given class
     * @param clazz Class for which a suitable Log name will be derived
     * @return the logger
     */
    public FunambolLogger getLogger(Class clazz) {

        return (getLogger(clazz.getName()));

    }


    /**
     * <p>Construct (if necessary) and return a <code>Log</code> instance,
     * using the factory's current set of configuration attributes.</p>
     *
     * <p><strong>NOTE</strong> - Depending upon the implementation of
     * the <code>LogFactory</code> you are using, the <code>Log</code>
     * instance you are returned may or may not be local to the current
     * application, and may or may not be returned again on a subsequent
     * call with the same name argument.</p>
     * @param name the name of the <code>FunambolLogger</code> instance to be
     *  returned
     * @return the <CODE>FunambolLogger</CODE> with the given name
     */
    public static FunambolLogger getLogger(String name)  {
        if (name != null && !name.equals("root") && !name.startsWith(LOGGER_BASE_NAME)) {
            StringBuffer sb = new StringBuffer(LOGGER_BASE_NAME);
            sb.append('.').append(name);
            name = sb.toString();
        }
        FunambolLogger instance = instances.get(name);
        if (instance == null) {
            instance = newInstance(name);
            synchronized (instances) {
                instances.put(name, instance);
            }
        }
        return (instance);
    }

    /**
     * Returns the logger identified with <code>LOGGER_BASE_NAME</code>
     */
    public static FunambolLogger getLogger() {
        return getLogger(LOGGER_BASE_NAME);
    }

    /**
     * Release any internal references to previously created FunambolLogger.
     */
    public static void release() {
        instances.clear();
    }

    /**
     * Reset the configuration of the loggers.
     * <br/>
     * Note: like in log4j, this method doesn't remove the loggers but just reset
     *       their configuration
     */
    public static void resetConfiguration() {
        if (instances == null) {
            return;
        }
        synchronized  (instances) {
            Collection<FunambolLogger> loggers = instances.values();
            for (FunambolLogger logger : loggers) {
                logger.setLevel(null);
                logger.setAdditivity(true);
                logger.setResourceBundle(null);
                logger.setUserLevels(null);
            }
        }
    }

    /**
     * Reset the configuration of the logger with the given name
     * <br/>
     * Note: like in log4j, this method doesn't remove the logger but just reset
     *       its configuration
     */
    public static void resetConfiguration(String name) {
        if (instances == null) {
            return;
        }
        FunambolLogger l = getLogger(name);
        l.setLevel(null);
        l.setAdditivity(true);
        l.setResourceBundle(null);
        l.setUserLevels(null);
    }

    // ------------------------------------------------------ Protected Methods


    //  ------------------------------------------------------ Private Methods

    /**
     * Create and return a new {@link org.apache.commons.logging.Log}
     * instance for the specified name.
     *
     * @param name Name of the new logger
     *
     * @exception LogConfigurationException if a new instance cannot
     *  be created
     */
    private static FunambolLogger newInstance(String name) {
        FunambolLogger instance = new FunambolLogger(name);
        return instance;
    }
}
