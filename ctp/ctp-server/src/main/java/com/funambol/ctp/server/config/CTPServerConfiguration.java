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

package com.funambol.ctp.server.config;

import java.io.File;

import org.apache.log4j.Logger;

import com.funambol.ctp.server.authentication.AuthenticationManager;
import com.funambol.ctp.server.pending.PendingNotificationManager;

import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanTool;
import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;

/**
 * Configuration class for a PushListener instance.
 * It implements singleton pattern so use CTPServerConfiguration.getInstance()
 * to obtain an instance.
 *
 * @version $Id: CTPServerConfiguration.java,v 1.4 2008-04-22 05:52:36 nichele Exp $
 */
public class CTPServerConfiguration {

    // --------------------------------------------------------------- Constants
    /**
     * The bean file name used to create an instance of a CTPServerConfiguration
     */
    public static final String CTP_SERVER_CONFIGURATION_BEAN_FILE =
            "com/funambol/ctp/server/CTPServerConfiguration.xml";

    /** The system property used to configure the config path */
    public static final String CONFIG_PATH_PROPERTY_NAME = "funambol.home";

    // ------------------------------------------------------------- Private data

    /** The logger */
    private final static Logger logger = Logger.getLogger("funambol.ctp.server.config");

    /** The instance */
    private static CTPServerConfiguration instance;

    /**
     * The configBean object
     */
    private CTPServerConfigBean configBean;

    /**
     * The configuration path
     */
    private final static String configPath = getConfigPath();

    /**
     * Private constructor. An instance is created using a CTPServerConfigBean
     * obtained reading <CODE>PUSH_LISTENER_CONFIGURATION_BEAN</CODE>
     */
    private CTPServerConfiguration()  {
        try {

            configBean = (CTPServerConfigBean)BeanTool.getBeanTool(configPath).
                    getNewBeanInstance(CTP_SERVER_CONFIGURATION_BEAN_FILE);

        } catch (BeanException e) {
            throw new CTPServerConfigurationException(CTP_SERVER_CONFIGURATION_BEAN_FILE, e);
        }

        try {
            configBean.fixConfigPath(configPath);
            configBean.validate();
        } catch (CTPServerConfigurationException e) {
            throw new CTPServerConfigurationException("Invalid configuration file "
                    + CTP_SERVER_CONFIGURATION_BEAN_FILE + " (" + e.getMessage()+ ")", e);
        }

        if (logger.isTraceEnabled()) {
            logger.trace("CTPServerConfiguration object created reading: " +
                    configPath + File.separator  + CTP_SERVER_CONFIGURATION_BEAN_FILE);
        }
        if (logger.isInfoEnabled()) {
            logger.info("CTPServer configuration:\n" + configBean.toFormattedString());
        }
    }

    /**
     * Returns an instance of CTPServerConfiguration object
     * @return the CTPServerConfiguration
     */
    public static synchronized CTPServerConfiguration getCTPServerConfiguration()  {

        if (instance == null) {
            instance = new CTPServerConfiguration();
        }

        return instance;
    }

    /**
     * Returns the configuration bean
     *
     * @return the CTPServerConfigBean
     */
    public static CTPServerConfigBean getCTPServerConfigBean() {
        return getCTPServerConfiguration().configBean;
    }
    
    static private ServerInformation serverInfo = null;

    static private AdminWSClient client = null;

    /** Name of the logger used to track web service call to the ds-server. */
    private static final String WS_CALLS_LOGGER_NAME = "funambol.ctp.server.ws";
    
    public static AdminWSClient getAdminWSClient() {
        if (client == null) {
            serverInfo =
                    CTPServerConfiguration.getCTPServerConfigBean().getWSServerInformation();
            client = new AdminWSClient(serverInfo, WS_CALLS_LOGGER_NAME);
        }

        return client;
    }

    /**
     * Returns the configuration path reading the system property
     * <code>CONFIG_PATH_PROPERTY_NAME</code>
     * @return the configuration path. CONFIG_PATH_PROPERTY_NAME + "/config"
     */
    public static String getConfigPath() {
        String path = System.getProperty(CONFIG_PATH_PROPERTY_NAME);
        if (path == null || path.length() == 0) {
            path = ".";
        }
        path = path + File.separator + "config";
        return path;
    }

    /**
     * Returns the AuthenticationManager
     *
     * @return the AuthenticationManager
     */
    public AuthenticationManager getAuthenticationManager() {

        String authManager = configBean.getAuthenticationManager();

        if (authManager == null || authManager.equals("")) {
            logger.fatal("AuthenticationManager not set");
            return null;
        }

        try {
            return (AuthenticationManager)BeanTool.getBeanTool(configPath).getBeanInstance(
                authManager
            );
        } catch (Exception e) {
            logger.fatal("Error creating the AuthenticationManager object", e);
        }

        return null;
    }

    /**
     * Creates and returns the PendingNotificationManager
     *
     * @return the AuthenticationManager
     */
    public PendingNotificationManager getPendingNotificationManager() {

        String pendingNotificationManager = configBean.getPendingNotificationManager();

        if (pendingNotificationManager == null || pendingNotificationManager.equals("")) {
            logger.fatal("PendingNotificationManager not set");
            return null;
        }

        try {
            return (PendingNotificationManager) BeanTool.getBeanTool(configPath)
                    .getBeanInstance(pendingNotificationManager);
        } catch (Exception ex) {
            logger.fatal("Error creating the PendingNotificationManager", ex);
        }

        return null;
    }


}
