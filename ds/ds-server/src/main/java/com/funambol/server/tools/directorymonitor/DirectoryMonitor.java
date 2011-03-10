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

package com.funambol.server.tools.directorymonitor;


import java.io.File;

import java.util.*;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.server.tools.directorymonitor.FileChangeEvent.EventType;

/**
 * It's a director  monitor able to monitor a directory and its sub directories.
 * An <code>IllegalArgumentException</code> is thrown if the monitor is created
 * with a file that doesn't exist or if is not a directory.
 * <br/>
 * It's a Thread that every <code>scanPeriod</code> checks the directory and its
 * sub directories in order to detect new/updated/deleted files (not directory) and
 * fires a <code>FileChangeEvent</code> for any changes. Note that jsut the files
 * are monitored.
 * It notifies the registered <code>FileChangeListeners</code> when a modification
 * on the monitored directory occurs.
 *
 * @version $Id: DirectoryMonitor.java,v 1.6 2008-05-22 19:54:54 nichele Exp $
 */
public class DirectoryMonitor extends Thread {

    // --------------------------------------------------------------- Constants
    private static final String THREAD_NAME = "funambol-directory-monitor";
    private static final String LOGGER_NAME = "funambol.directorymonitor";

    // ------------------------------------------------------------ Private data

    /** List of FileChangeListener */
    private List<FileChangeListener> fileChangeListeners = null;

    /** The period between two scans */
    private long scanPeriod;

    /** Is it the monitoring enabled */
    private boolean enabled = true;

    /** The directory to monitor */
    private File directory = null;

    /** The contained files */
    private Map<File, Long> containedFiles = null;

    /** The logger */
    private FunambolLogger log = null;

    // -------------------------------------------------------------- Properties


    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new FileSystemMonitor with the given scanPeriod
     * @param directory the directory to monitor
     * @param scanPeriod the scan period
     */
    public DirectoryMonitor(File directory, long scanPeriod) {

        if (directory == null) {
            throw new IllegalArgumentException("The directory must be not null");
        }

        if (!directory.exists()) {
            throw new IllegalArgumentException("The given file doesn't exist (" + directory.getAbsolutePath() + ")");
        }        

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("The given file is not a directory (" + directory.getAbsolutePath() + ")");
        }        
        
        this.log = FunambolLoggerFactory.getLogger(LOGGER_NAME);
        
        this.setPriority(Thread.MIN_PRIORITY);
        this.setName(THREAD_NAME + "-" + Integer.toString(this.hashCode(), 16));
        this.fileChangeListeners = new ArrayList();

        this.scanPeriod = scanPeriod;
        this.directory  = directory;
    }

    /**
     * Creates a new FileSystemMonitor with the given scanPeriod
     * @param directory the directory to monitor
     * @param scanPeriod the scan period
     */
    public DirectoryMonitor(String directory, long scanPeriod) {

        this(new File(directory), scanPeriod);
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * Is the monitor enabled ?
     * @return <CODE>true</CODE> if the monitor is enabled, <CODE>false</CODE> otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Runs the monitor loading the current state of the monitored files.
     * If the monitor is already alive, it's just enabled (if disabled).
     */
    public void runMonitor() {
        if (!this.isAlive()) {
            this.containedFiles = getContainedFiles(this.directory);
            this.start();
        }
        if (!this.enabled) {
            this.enabled = true;
        }
    }

    /**
     * Disables the monitoring
     */
    public void disable() {
        this.enabled = false;
    }

    /**
     * Enables the monitoring
     */
    public void enable() {
        this.enabled = true;
    }

    /**
     * Resets the monitor reloading the state of the monitored directory.
     */
    public void reset() {
        this.containedFiles = getContainedFiles(this.directory);
    }

    /**
     * Stops the monitor
     */
    public void stopMonitor() {
        if (this.isAlive()) {
            this.interrupt();
        }
    }

    /**
     * Adds a new <CODE>FileChangeListener</CODE>. If the listener is already registered,
     * nothing is done.
     * @param listener the <CODE>FileChangeListener</CODE> to add
     */
    public void addFileChangeListener(FileChangeListener listener) {
        if (listener == null) {
            return ;
        }
        synchronized (fileChangeListeners) {
            if (fileChangeListeners.indexOf(listener) == -1) {
                fileChangeListeners.add(listener);
            }
        }
    }

    /**
     * Removes the given <CODE>FileChangeListener<CODE>
     * @param listener the <CODE>FileChangeListener</CODE> to remove
     */
    public void removeFileChangeListener(FileChangeListener listener) {
        if (listener == null) {
            return ;
        }
        synchronized (fileChangeListeners) {
            fileChangeListeners.remove(listener);
        }
    }

    /**
     * Starts the monitoring
     */
    public void run() {

        try {
            while (true) {
                if (enabled) {
                    checkFiles();
                }
                Thread.currentThread().sleep(scanPeriod);
            }
        } catch (InterruptedException ex) {
            //
            // nothing to do
            //
        }
    }


    /**
     * Returns a string representation of this directory monitor
     * @return a string representation of this directory monitor
     */
    public String toString() {
        return this.getName();
    }
    // --------------------------------------------------------- Private methods

    /**
     * Returns a map with the contained files in the given directory and the
     * respective last modified timestamp
     * @param directory the directory
     * @return a Map containing all files and respective last modified timestamp.
     *         This map contains just the last modified timestamp for the file
     *         that are not directory. Also the subdirectory are scanned and the
     *         content is added to the Map. If the given file is not a directory
     *         a null map is returned.
     */
    private Map<File, Long> getContainedFiles(File directory) {
        if (directory == null || !directory.isDirectory()) {
            return null;
        }

        File[] files = directory.listFiles();
        Map containedFiles = new HashMap(files.length);

        Long lastModified = null;
        for (File file : files) {

           if (file.isDirectory()) {
               containedFiles.putAll(getContainedFiles(file));
               continue;
           }
           lastModified = new Long(file.lastModified());
           containedFiles.put(file, lastModified);

        }
        return containedFiles;
    }

    /**
     * Checks if the state of the file in the given map is changes.
     * @param files the files to check
     */
    private void checkFiles() {

        Map<File, Long> currentContainedFiles = null;

        List<FileChangeEvent> events = new ArrayList();

        FileChangeEvent fileChangeEvent = null;

        long scanTimeStamp = System.currentTimeMillis();

        currentContainedFiles = getContainedFiles(directory);

        if (currentContainedFiles == null && containedFiles == null) {

            //
            // This happens only is the monitored file is not a directory
            //

        } else if (currentContainedFiles != null && containedFiles != null) {

            Long lastScanlastModified = null;
            Long currentLastModified = null;

            for (File file : currentContainedFiles.keySet()) {
                lastScanlastModified = containedFiles.get(file);
                currentLastModified = currentContainedFiles.get(file);
                if (lastScanlastModified == null) {
                    //
                    // The file is new
                    //
                    fileChangeEvent = new FileChangeEvent(scanTimeStamp,
                                                          file,
                                                          EventType.FILE_ADDED);
                    events.add(fileChangeEvent);
                    continue;
                } else if (lastScanlastModified.equals(currentLastModified)) {
                    //
                    // The file is not changed
                    //
                } else {
                    //
                    // The file is changed
                    //
                    fileChangeEvent = new FileChangeEvent(scanTimeStamp,
                                                          file,
                                                          EventType.FILE_CHANGED);
                    events.add(fileChangeEvent);
                }
            }

            //
            // Checking deleted files
            //
            for (File file : containedFiles.keySet()) {
                if (currentContainedFiles.get(file) == null) {
                    //
                    // The file has been deleted
                    //
                    fileChangeEvent = new FileChangeEvent(scanTimeStamp,
                                                          file,
                                                          EventType.FILE_DELETED);
                    events.add(fileChangeEvent);
                }
            }
        }
        containedFiles = currentContainedFiles;
        fireFileChangeEvents(events);

    }

    /**
     * Fires the given events notifying the registered listeners
     * @param events the events to fire
     */
    private void fireFileChangeEvents(List<FileChangeEvent> events) {
        synchronized (fileChangeListeners) {
            for (FileChangeEvent event : events) {
                Iterator itListeners = fileChangeListeners.iterator();
                while (itListeners.hasNext()) {
                    try {
                        ((FileChangeListener) itListeners.next()).fileChange(event);
                    } catch (Throwable e) {
                        //
                        // Nothing to do
                        //
                        log.error("Error notifying the event: " + event, e);
                    }
                }
            }
        }
    }

}
