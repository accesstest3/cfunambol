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

package com.funambol.server.update;

import java.io.UnsupportedEncodingException;

import java.net.InetAddress;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import java.util.ArrayList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

/**
 * It is the component the periodically checks on www.funambol.com for new version
 * of the ds server. It runs a Thread (with name
 * <code>funambol-update-discovery-thread-</code><i>hashcode</i>)
 * that every <code>checkIntervalInHours</code> performs an http request on:
 * <br>
 * <code>updateCenter</code>?version=<serverVersion>&ip=<serverIp>&uri=<serverURI>
 * downloading a <code>ComponentList</code> of the released component.
 * @version $Id: UpdateDiscovery.java,v 1.1.1.1 2008-02-21 23:36:03 stefano_fornari Exp $
 */
public class UpdateDiscovery implements LazyInitBean {

    // --------------------------------------------------------------- Constants
    private static final String LOGGER_NAME       = "funambol.server.updatediscovery";
    private static final String THREAD_NAME       = "funambol-update-discovery-thread";
    private static final int    THREAD_PRPIORITY  = Thread.MIN_PRIORITY;

    /**
     * Name used to identify the ds-server components from the released component list
     */
    private static final String COMPONENT_DS_SERVER = "ds-server";

    /**
     * Parameters used in the http request
     */
    private static final String PARAMETER_VERSION   = "version";
    private static final String PARAMETER_COMPONENT = "component";


    // ------------------------------------------------------------ Private data

    /**
     * The logger
     */
    private FunambolLogger log = null;

    /**
     * The server version
     */
    private String serverVersion = null;

    /**
     * The Thread used to verify the updates
     */
    private Thread thread        = null;

    /**
     * The sleeping time of the Thread
     */
    private long sleepingTime   = 0;

    // -------------------------------------------------------------- Properties

    /**
     * The timestamp of the last check
     */
    private long lastCheckTimestamp = 0;

    public long getLastCheckTimestamp() {
        return lastCheckTimestamp;
    }

    public void setLastCheckTimestamp(long lastCheckTimestamp) {
        this.lastCheckTimestamp = lastCheckTimestamp;
    }

    /**
     * The url to use to download the released component list
     */
    private String updateCenter = null;

    public String getUpdateCenter() {
        return updateCenter;
    }

    public void setUpdateCenter(String updateCenter) {
        this.updateCenter = updateCenter;
    }

    /**
     * The interval between two checks
     */
    private double checkIntervalInHours = Long.MAX_VALUE;  // basically this means check disabled.
                                                           // By default we set this value in order
                                                           // to have always the used value in
                                                           // the xml file that otherwise doesn't contain
                                                           // the default one.

    public double getCheckIntervalInHours() {
        return checkIntervalInHours;
    }

    public void setCheckIntervalInHours(double checkIntervalInHours) {
        this.checkIntervalInHours = checkIntervalInHours;
    }

    /**
     * Latest ds server available update
     */
    private Component latestDSServerUpdate = null;

    public Component getLatestDSServerUpdate() {
        return latestDSServerUpdate;
    }

    public void setLatestDSServerUpdate(Component dsServerComponent) {
        this.latestDSServerUpdate = dsServerComponent;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of UpdateDiscovery
     */
    public UpdateDiscovery() {
        log = FunambolLoggerFactory.getLogger(LOGGER_NAME);
        initializeThread();
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * Starts the thread to check the updateCenter for ds server updates
     */
    public void startCheck() {
        if (log.isTraceEnabled()) {
            log.trace("Starting update discovery thread [" + toString() + "]");
        }

        if (thread == null) {
            initializeThread();
            thread.start();
            return;
        }
        if (!thread.isAlive() &&
            !thread.isInterrupted() &&
            !(Thread.State.TERMINATED.equals(thread.getState()))) {
            //
            // in this case we can start the thread
            //
            thread.start();
        } else {
            //
            // in this case we have to initialize a new thread
            //
            initializeThread();
            thread.start();
        }

    }

    /**
     * Stops the thread used to check for ds server updates
     */
    public void stopCheck() {
        if (log.isTraceEnabled()) {
            log.trace("Stopping update discovery thread [" + toString() + "]");
        }
        if (thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * Is the thread alive ?
     * @return true if the thread is alive, otherwise false
     */
    public boolean isEnabled() {
        if (thread == null) {
            return false;
        }
        return thread.isAlive();
    }

    /**
     * Sets the server version to the given version
     * @param version the version to set
     */
    public void setServerVersion(String version) {
        this.serverVersion = version;
    }

    /**
     * LazyInitBean
     */
    public void init() throws BeanInitializationException {
        if (updateCenter == null || "".equals(updateCenter)) {
            throw new BeanInitializationException("The updateCenter must not be null");
        }
    }

    /**
     * Returns a string representation of this update discovery
     * @return a string representation of this update discovery
     */
    public String toString() {
        if (thread != null) {
            return thread.getName();
        }
        return super.toString();
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Initializes the UpdateDiscoveryThread
     */
    private void initializeThread() {
        thread = new Thread(new UpdateDiscoveryThread());
        thread.setName(THREAD_NAME + "-" + Integer.toString(this.hashCode(), 16));
        thread.setPriority(THREAD_PRPIORITY);
    }

    // ------------------------------------------------------------- Inner class

    /**
     * The Thread used to discover the ds server update
     */
    class UpdateDiscoveryThread implements Runnable  {

        public void run() {
            while (true) {
                //
                // Verify if from the last check, at least checkIntervalInHours are gone
                //
                boolean check = isCheckRequired();
                if (check) {
                    if (log.isInfoEnabled()) {
                        log.info("Checking for updates on " + updateCenter);
                    }
                    lastCheckTimestamp = System.currentTimeMillis();
                    //
                    // Download component list
                    //
                    ComponentList list = getComponentList();
                    latestDSServerUpdate = getLatestServerComponent(list);
                    if (latestDSServerUpdate != null) {
                        if (log.isInfoEnabled()) {
                            log.info("Detected new DS Server version: " + latestDSServerUpdate);
                        }
                    } else {
                        if (log.isInfoEnabled()) {
                            log.info("No new DS Server version found");
                        }
                    }
                }
                try {
                    Thread.currentThread().sleep(sleepingTime);
                } catch (InterruptedException ex) {
                    //
                    // Stopping the thread
                    //
                    break;
                }
            }
        }

        /**
         * Verify if from the last check, at least checkIntervalInHours are gone
         * @return true is a check is required, false otherwise
         */
        private boolean isCheckRequired() {
            long currentTime = System.currentTimeMillis();
            long interval    = (long)(checkIntervalInHours * 3600000); // interval in milliseconds
            if ((currentTime - lastCheckTimestamp) >= interval) {
                //
                // This is done to have always checkIntervalInHours between two
                // checks also if the server has been stopped/restarted
                //
                sleepingTime = interval;
                return true;
            }
            long nextCheckTime = lastCheckTimestamp + interval;
            sleepingTime = nextCheckTime - currentTime;
            return false;
        }

        /**
         * Returns the release component list building the url to use for the updates
         * and performing a post request to that url
         * @return the release component list (or an empty list in case of an
         *         error occurs checking on the updateCenter)
         */
        private ComponentList getComponentList()  {
            String xml = null;
            ComponentList list = null;
            try {
                xml = getReleasedComponents();
                list = Util.fromXML(xml);
                return list;
            } catch (UpdateException ex) {
                log.error("Error checking for updates [" + ex.getMessage() + "]");
            }
            return new ComponentList();
        }

        /**
         * Returns the xml representing a component list building the url to use for the updates
         * and performing a post request to that url
         * @return the xml representing a component list
         */
        private String getReleasedComponents() throws UpdateException {
            StringBuilder sb = new StringBuilder(updateCenter);
            if (sb.indexOf("?") == -1) {
                sb.append('?');
            } else {
                sb.append('&');
            }
            try {
                sb.append(PARAMETER_VERSION).append('=').append(URLEncoder.encode(serverVersion, "utf-8"));
                sb.append('&').append(PARAMETER_COMPONENT).append('=').append(URLEncoder.encode(COMPONENT_DS_SERVER, "utf-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new UpdateException("Error creating the url");
            }

            return downloadReleaseComponentList(sb.toString());
        }

        /**
         * Performs a post request to the given url returning the result as string
         * @param url the url
         * @throws UpdateException if an error occurs
         * @return the downloaded released component list
         */
        private String downloadReleaseComponentList(String url) throws UpdateException {
            if (log.isTraceEnabled()) {
                log.trace("Performing a post request to: " + url);
            }

            try {
                //
                // Using commons-httpclient
                //
                PostMethod post = new PostMethod(url);
                HttpClient httpClient = new HttpClient();
                int statusCode = httpClient.executeMethod(post);

                return post.getResponseBodyAsString();
            } catch (Exception ex) {
                throw new UpdateException("Error downloading the updates file from '" +
                                          url + "'", ex);
            }
        }

        /**
         * Searches in the given ComponentList the latest ds server version
         * @return the latest ds server version
         */
        private Component getLatestServerComponent(ComponentList list) {
            if (list == null) {
                return null;
            }
            ArrayList<Component> components = list.getComponents();
            if (components == null || components.isEmpty()) {
                return null;
            }

            String    version       = null;
            Component latestServer  = null;
            String    latestVersion = serverVersion;

            if (components != null && components.size() > 0) {
                for (Component component : components) {
                    if (COMPONENT_DS_SERVER.equals(component.getName())) {
                        version = component.getVersion();
                        if (version != null) {
                            if (Util.compareVersionNumber(version, latestVersion)) {
                                latestVersion = version;
                                latestServer  = component;
                            }
                        }
                    }
                }
            }
            return latestServer;
        }
    }
}
