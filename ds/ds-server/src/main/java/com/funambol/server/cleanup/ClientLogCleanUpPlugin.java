/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

package com.funambol.server.cleanup;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.plugin.Plugin;
import com.funambol.framework.server.plugin.PluginException;

/**
 * It's the plugin class for the log cleanup feature.
 * Periodically it awakes, checks if the client logs directory contains more files
 * than a configured parameter.
 * In that case it move all the available log files into an archive in case the
 * archivation directory has been specified, otherwise clients log files are just
 * removed.
 *
 * @version $Id$
 */
public class ClientLogCleanUpPlugin implements Plugin {

     // --------------------------------------------------------------- Constants
    public static final String CLEANUP_LOGGER_NAME = "funambol.cleanup-agent.clientlog";

    private static final String THREAD_NAME = "funambol-clientlog-cleanup-agent";
    private static final int THREAD_PRIORITY = Thread.MIN_PRIORITY;

    /** Logger */
    private static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(CLEANUP_LOGGER_NAME);

    // -------------------------------------------------------------- Properties
    /** ClientLogCleanUpPlugin Thread. */
    private Thread thread;
    private ClientLogCleanUpAgent agent;

    private String clientsLogBaseDir                            = null;
    private String clientsLogArchivationDir                     = null;
    private int    clientsLogMaxNumberOfArchivedFiles           = 30000;
    private int    clientsLogNumberOfArchivedFilesToBeDeleted   = 8000;
    private int    clientsLogMaxNumberOfFiles                   = 16000;
    private long   clientsLogLockExpirationTime                 = 5*60*1000;
    private long   clientsLogTimeToRest                         = 1*60*60*1000;


    // ---------------------------------------------------- Properties accessors

    /**
     * @return the clientsLogBaseDir
     */
    public String getClientsLogBaseDir() {
        return clientsLogBaseDir;
    }

    /**
     * @param clientsLogBaseDir the clientsLogBaseDir to set
     */
    public void setClientsLogBaseDir(String clientsLogBaseDir) {
        this.clientsLogBaseDir = clientsLogBaseDir;
    }

    /**
     * @return the clientsLogArchivationDir
     */
    public String getClientsLogArchivationDir() {
        return clientsLogArchivationDir;
    }

    /**
     * @param clientsLogArchivationDir the clientsLogArchivationDir to set
     */
    public void setClientsLogArchivationDir(String clientsLogArchivationDir) {
        this.clientsLogArchivationDir = clientsLogArchivationDir;
    }

    /**
     * @return the clientsLogMaxNumberOfFiles
     */
    public int getClientsLogMaxNumberOfFiles() {
        return clientsLogMaxNumberOfFiles;
    }

    /**
     * @param clientsLogMaxNumberOfFiles the clientsLogMaxNumberOfFiles to set
     */
    public void setClientsLogMaxNumberOfFiles(int clientsLogMaxNumberOfFiles) {
        this.clientsLogMaxNumberOfFiles = clientsLogMaxNumberOfFiles;
    }

    /**
     * @return the clientsLogLockExpirationTime
     */
    public long getClientsLogLockExpirationTime() {
        return clientsLogLockExpirationTime;
    }

    /**
     * @param clientsLogLockExpirationTime the clientsLogLockExpirationTime to set
     */
    public void setClientsLogLockExpirationTime(long clientsLogLockExpirationTime) {
        this.clientsLogLockExpirationTime = clientsLogLockExpirationTime;
    }

    /**
     * @return the clientsLogTimeToRest
     */
    public long getClientsLogTimeToRest() {
        return clientsLogTimeToRest;
    }

    /**
     * @param clientsLogTimeToRest the clientsLogTimeToRest to set
     */
    public void setClientsLogTimeToRest(long clientsLogTimeToRest) {
        this.clientsLogTimeToRest = clientsLogTimeToRest;
    }


    // ---------------------------------------------------------- Public methods
    public boolean isEnabled() throws PluginException {
        return true;
    }

    /**
     * Starts the ClientLogCleanUpAgent thread.
     */
    public void start() {
        if (log.isTraceEnabled()) {
            log.trace("Starting ClientLogCleanUpAgent thread [" + toString() + "]");
        }

        if (initializationRequired()) {
            initializeThread();
            thread.start();
            return;
        }

        if(agent!=null && !agent.isEnabled()) {
                agent.setEnabled(true);
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
     * Stops the ClientLogCleanUpAgent thread.
     */
    public void stop() {
        if (log.isTraceEnabled()) {
            log.trace("Stopping ClientLogCleanUpAgent thread [" + toString() + "]");
        }


        if(agent!=null && agent.isEnabled()) {
            agent.setEnabled(false);
        }

        //
        // Stops thread.
        //
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * Returns a string representation of the agent
     * @return a string representation of the agent
     */
    @Override
    public String toString() {
        if (agent != null) {
            return agent.toString();
        }
        return super.toString();
    }

    /**
     * @return the clientsLogMaxNumberOfArchivedFiles
     */
    public int getClientsLogMaxNumberOfArchivedFiles() {
        return clientsLogMaxNumberOfArchivedFiles;
    }

    /**
     * @param clientsLogMaxNumberOfArchivedFiles the clientsLogMaxNumberOfArchivedFiles to set
     */
    public void setClientsLogMaxNumberOfArchivedFiles(int clientsLogMaxNumberOfArchivedFiles) {
        this.clientsLogMaxNumberOfArchivedFiles = clientsLogMaxNumberOfArchivedFiles;
    }

    /**
     * @return the clientsLogNumberOfArchivedFilesToBeDeleted
     */
    public int getClientsLogNumberOfArchivedFilesToBeDeleted() {
        return clientsLogNumberOfArchivedFilesToBeDeleted;
    }

    /**
     * @param clientsLogNumberOfArchivedFilesToBeDeleted the clientsLogNumberOfArchivedFilesToBeDeleted to set
     */
    public void setClientsLogNumberOfArchivedFilesToBeDeleted(int clientsLogNumberOfArchivedFilesToBeDeleted) {
        this.clientsLogNumberOfArchivedFilesToBeDeleted = clientsLogNumberOfArchivedFilesToBeDeleted;
    }

    // --------------------------------------------------------- Private methods
    private boolean initializationRequired() {
        return thread == null || agent == null;
    }

    /**
     * Initializes the ClientLogCleanUpAgent
     */
    private void initializeThread() {
        if (log.isTraceEnabled()) {
            log.trace("Initializing ClientLogCleanUpAgent thread");
        }
        agent  = createAgent();

        if(log.isTraceEnabled()) {
            log.trace("Created Cleanup agent '"+agent+"'.");
        }
        thread = new Thread(agent,getThreadName());
        thread.setPriority(THREAD_PRIORITY);
    }

    protected ClientLogCleanUpAgent createAgent() {
        return new ClientLogCleanUpAgent(clientsLogBaseDir,
                                        clientsLogArchivationDir,
                                        clientsLogMaxNumberOfFiles,
                                        clientsLogMaxNumberOfArchivedFiles,
                                        clientsLogNumberOfArchivedFilesToBeDeleted,
                                        clientsLogTimeToRest,
                                        "lock",
                                        clientsLogLockExpirationTime);
    }

    private String getThreadName() {
        return THREAD_NAME + "-" + Integer.toString(this.hashCode(), 16);
    }

}
