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
package com.funambol.admin.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.InetAddress;
import java.net.UnknownHostException;

import java.util.Properties;

import com.funambol.admin.util.Bundle;
import com.funambol.admin.util.Constants;
import com.funambol.admin.util.Log;

/**
 * Maintains the information about the connection with the server.
 * Store the information in a properties file.
 *
 * @version $Id: AdminConfig.java,v 1.6 2007-11-28 10:28:16 nichele Exp $
 */
public class AdminConfig implements Constants {

    // --------------------------------------------------------------- Constants

    // Properties name
    private static final String CONFIGURATION_PROPERTIES_USER              = "user"         ;
    private static final String CONFIGURATION_PROPERTIES_PASSWORD          = "password"     ;
    private static final String CONFIGURATION_PROPERTIES_SERVER            = "server"       ;
    private static final String CONFIGURATION_PROPERTIES_PORT              = "port"         ;
    private static final String CONFIGURATION_PROPERTIES_USE_PROXY         = "useProxy"     ;
    private static final String CONFIGURATION_PROPERTIES_PROXY_USER        = "proxyUser"    ;
    private static final String CONFIGURATION_PROPERTIES_PROXY_PASSWORD    = "proxyPassword";
    private static final String CONFIGURATION_PROPERTIES_PROXY_HOST        = "proxyHost"    ;
    private static final String CONFIGURATION_PROPERTIES_PROXY_PORT        = "proxyPort"    ;
    private static final String CONFIGURATION_LAST_NOTIFIED_SERVER_VERSION = "lastNotifiedServerVersion";

    private static final String CONFIGURATION_PROPERTIES_MAX_RESULTS   =
        "maxResults"       ;
    private static final String CONFIGURATION_PROPERTIES_DEBUG_ENABLED =
        "debugEnabled"    ;
    private static final String CONFIGURATION_PROPERTIES_CONTEXT_PATH  =
        "serverContextPath" ;

    // name of the file used for store the information
    private static final String PROPERTIES_FILE_NAME   = "admin.properties";
    private static final String PROPERTIES_FILE_HEADER = "Funambol Administration Tool";

    // ------------------------------------------------------------ Private data
    private static AdminConfig singleton;

    private Properties props = null;

    // -------------------------------------------------------------- Properties
    private static boolean rememberPassword = false;

    /** Returns property rememberPassword */
    public boolean getRememberPassword() {
        return rememberPassword;
    }

    /** Set rememberPassword */
    public void setRememberPassword(boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }

    // ------------------------------------------------------------ Constructors
    private AdminConfig() {
        loadInitValue();
    }

    /**
     * Returns the singleton instance. It creates the instance the first time it
     * is called.
     *
     * @return the singleton instance.
     */
    public static synchronized AdminConfig getAdminConfig() {
        if (singleton == null) {
            singleton = new AdminConfig();
        }

        return singleton;
    }

    // ---------------------------------------------------------- Public Methods
    /**
     * Load properties
     */
    private void loadInitValue() {
        props = new Properties();
        try {
            props.load(new FileInputStream(PROPERTIES_FILE_NAME));
        } catch (IOException ex) {
            // if error, set value to void
            try {
                props.setProperty(CONFIGURATION_PROPERTIES_SERVER,
                    InetAddress.getLocalHost().getCanonicalHostName());
            } catch (UnknownHostException e) {
                Log.debug("Unable to get local address: " + e);
                //
                // We have no other choice ...
                //
                props.setProperty(CONFIGURATION_PROPERTIES_SERVER, "localhost");
            }
            props.setProperty(CONFIGURATION_PROPERTIES_PORT, DEFAULT_PORT);
            props.setProperty(CONFIGURATION_PROPERTIES_USER, DEFAULT_USER);
            props.setProperty(CONFIGURATION_PROPERTIES_PASSWORD, DEFAULT_PASSWORD);
            props.setProperty(CONFIGURATION_PROPERTIES_USE_PROXY, "false");
            props.setProperty(CONFIGURATION_PROPERTIES_PROXY_USER, "");
            props.setProperty(CONFIGURATION_PROPERTIES_PROXY_HOST, "");
            props.setProperty(CONFIGURATION_PROPERTIES_PROXY_PORT,
                              String.valueOf(DEFAULT_PROXY_PORT));

            props.setProperty(CONFIGURATION_PROPERTIES_MAX_RESULTS,
                              String.valueOf(DEFAULT_MAX_RESULTS) );
            props.setProperty(CONFIGURATION_PROPERTIES_DEBUG_ENABLED, "false");
            props.setProperty(CONFIGURATION_PROPERTIES_CONTEXT_PATH,
                              DEFAULT_CONTEXT_PATH                 );

        }

        // if, in the file, there is the password, then set rememberPassword = true
        if (props.getProperty(CONFIGURATION_PROPERTIES_PASSWORD) != null) {
            rememberPassword = true;
        }
    }

    /**
     * Save properties
     */
    public void saveConfiguration() {
        Properties propsToSave = new Properties();
        propsToSave.putAll(props);

        // if rememberPassword != true not save the password
        if (!rememberPassword) {
            propsToSave.remove(CONFIGURATION_PROPERTIES_PASSWORD);
        }

        try {
            propsToSave.store(new FileOutputStream(PROPERTIES_FILE_NAME),
                              PROPERTIES_FILE_HEADER);
        } catch (IOException e) {
            Log.error(Bundle.getMessage(Bundle.ERROR_SAVING_SERVER_CONFIGURATION), e);
        }

    }

    /** Returns user */
    public String getUser() {
        return props.getProperty(this.CONFIGURATION_PROPERTIES_USER, "");
    }

    /**
     * Set user
     * @param user to set
     */
    public void setUser(String user) {
        props.setProperty(this.CONFIGURATION_PROPERTIES_USER, user);
    }

    /** Returns password */
    public String getPassword() {
        return props.getProperty(this.CONFIGURATION_PROPERTIES_PASSWORD, "");
    }

    /**
     * Set password
     * @param password to set
     */
    public void setPassword(String password) {
        props.setProperty(this.CONFIGURATION_PROPERTIES_PASSWORD, password);
    }

    /** Returns hostName */
    public String getHostName() {
        return props.getProperty(this.CONFIGURATION_PROPERTIES_SERVER, "");
    }

    /**
     * Set hostName
     * @param hostName to set
     */
    public void setHostName(String hostName) {
        props.setProperty(this.CONFIGURATION_PROPERTIES_SERVER, hostName);
    }

    /** Returns serverPort */
    public String getServerPort() {
        return props.getProperty(this.CONFIGURATION_PROPERTIES_PORT, DEFAULT_PORT);
    }

    /**
     * Set serverPort
     * @param serverPort to set
     */
    public void setServerPort(final String serverPort) {
        props.setProperty(CONFIGURATION_PROPERTIES_PORT, serverPort);
    }

    /** Returns proxy user */
    public String getProxyUser() {
        return props.getProperty(CONFIGURATION_PROPERTIES_PROXY_USER, "");
    }

    /**
     * is use proxy set?
     *
     * @return use proxy
     */
    public boolean isUseProxy() {
        String useProxy = props.getProperty(CONFIGURATION_PROPERTIES_USE_PROXY, "false");

        return useProxy.equalsIgnoreCase("true");
    }

    /**
     * Set use proxy
     *
     * @param useProxy
     */
    public void setUseProxy(boolean useProxy) {
        props.setProperty(CONFIGURATION_PROPERTIES_USE_PROXY, String.valueOf(useProxy));
    }

    /**
     * Set proxy user
     * @param user to set
     */
    public void setProxyUser(String user) {
        props.setProperty(CONFIGURATION_PROPERTIES_PROXY_USER, user);
    }

    /** Returns proxy password */
    public String getProxyPassword() {
        return props.getProperty(CONFIGURATION_PROPERTIES_PROXY_PASSWORD, "");
    }

    /**
     * Set proxy password
     * @param password to set
     */
    public void setProxyPassword(String password) {
        props.setProperty(CONFIGURATION_PROPERTIES_PROXY_PASSWORD, password);
    }

    /** Returns proxy hostName */
    public String getProxyHostName() {
        return props.getProperty(CONFIGURATION_PROPERTIES_PROXY_HOST, "");
    }

    /**
     * Set proxy host name
     * @param hostName to set
     */
    public void setProxyHostName(String hostName) {
        props.setProperty(CONFIGURATION_PROPERTIES_PROXY_HOST, hostName);
    }

    /** Returns proxy serverPort */
    public String getProxyPort() {
        return props.getProperty(CONFIGURATION_PROPERTIES_PROXY_PORT, DEFAULT_PROXY_PORT);
    }

    /**
     * Set proxy serverPort
     * @param serverPort to set
     */
    public void setProxyPort(final String serverPort) {
        props.setProperty(CONFIGURATION_PROPERTIES_PROXY_PORT, serverPort);
    }

    /** Returns max results */
    public int getMaxResults() {
        String mr = props.getProperty(CONFIGURATION_PROPERTIES_MAX_RESULTS,
                                      String.valueOf(DEFAULT_MAX_RESULTS) );
        return Integer.parseInt(mr);
    }

    /**
     * Set max results
     * @param maxResults to set
     */
    public void setMaxResults(final int maxResults) {
        props.setProperty(CONFIGURATION_PROPERTIES_MAX_RESULTS,
                          String.valueOf(maxResults)          );
    }

    /**
     * Is debug enabled?
     * @return true if the debug is enabled, false otherwise
     */
    public boolean isDebugEnabled() {
        String debugEnabled =
            props.getProperty(CONFIGURATION_PROPERTIES_DEBUG_ENABLED, "false");
        return debugEnabled.equalsIgnoreCase("true");
    }

    /**
     * Set enable debug
     * @param enableDebug
     */
    public void setEnableDebug(boolean enableDebug) {
        props.setProperty(CONFIGURATION_PROPERTIES_DEBUG_ENABLED,
                          String.valueOf(enableDebug)           );
    }

    /**
     * Returns the context path to use to connect to the server.
     * If it isn't set, returns Constants.DEFAULT_CONTEXT_PATH.
     * If it is sets to empty string, returns '/'
     * @return String
     */
    public String getContextPath() {
        String contextPath =
            props.getProperty(CONFIGURATION_PROPERTIES_CONTEXT_PATH,
                              DEFAULT_CONTEXT_PATH                 );

        if (contextPath.equalsIgnoreCase("")) {
            contextPath = "/";
        } else if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() -1);
        } else if (!contextPath.startsWith("/")) {
            contextPath = "/" + contextPath;
        }
        return contextPath;
    }

    /**
     * Set context path
     * @param contextPath to set
     */
    public void setContextPath(final String contextPath) {
        props.setProperty(CONFIGURATION_PROPERTIES_CONTEXT_PATH, contextPath);
    }

    /**
     * Returns the WS endpoint
     */
    public String getWSEndpoint() {

        String contextPath = getContextPath();

        return "http://"
             + getHostName()
             + ':'
             + getServerPort()
             + contextPath
             + "/"
             + WS_ENDPOINT_URL
             ;
    }

    /**
     * Returns the Funambol data synchronization server codebase
     *
     * @return the Funambol data synchronization server codebase
     */
    public String getSync4jCodeBase() {

        String contextPath = getContextPath();

        return "http://"
             + getHostName()
             + ':'
             + getServerPort()
             + contextPath
             + "/"
             + RMI_BASE_URL
             ;
    }

    /**
     * Returns latestNotifiedServerVersion
     */
    public String getLatestNotifiedServerVersion() {
        return props.getProperty(CONFIGURATION_LAST_NOTIFIED_SERVER_VERSION, "");
    }

    /**
     * Set latestNotifiedServerVersion
     * @param latestNotifiedServerVersion to set
     */
    public void setLatestNotifiedServerVersion(final String latestNotifiedServerVersion) {
        props.setProperty(CONFIGURATION_LAST_NOTIFIED_SERVER_VERSION, latestNotifiedServerVersion);
    }

}
