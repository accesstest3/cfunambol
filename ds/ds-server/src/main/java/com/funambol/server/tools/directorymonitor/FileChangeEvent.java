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

/**
 * It's an event used to notify changes in a file.
 *
 * @version $Id: FileChangeEvent.java,v 1.1.1.1 2008-02-21 23:36:02 stefano_fornari Exp $
 */
public class FileChangeEvent {

    // --------------------------------------------------------------- Constants

    /** The event type */
    public static enum EventType {FILE_ADDED,
                                  FILE_CHANGED,
                                  FILE_DELETED};

    // -------------------------------------------------------------- Properties

    /**
     * The file that the event is relative to
     */
    private File file = null;

    /**
     * the file added/deleted/updated
     * @return the file added/deleted/updated
     */
    public File getFile() {
        return file;
    }

    /**
     * The scan timestamp the event is relative to
     */
    private long scanTimeStamp = 0;

    public long getScanTimeStamp() {
        return scanTimeStamp;
    }

    /**
     *  The type of the event
     */
    private EventType eventType = null;


    /**
     * Returns the type of the event. See {@link EventType}
     * @return the type of the event
     */
    public EventType getEventType() {
        return eventType;
    }


    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new FileChangeEvent with the given file and eventType
     * @param scanTimeStamp the scan timestamp the event is relative to
     * @param file the file that the event is relative to
     * @param eventType the type of the event
     */
    public FileChangeEvent(long      scanTimeStamp,
                           File      file,
                           EventType eventType) {

        this.scanTimeStamp = scanTimeStamp;
        this.file = file;
        this.eventType = eventType;
    }


    /**
     * Returns a string representation of this event
     * @return a string representation of this event
     */
    public String toString() {
        StringBuilder buf = new StringBuilder(128);

        buf.append(super.toString())
           .append("[scanTimeStamp=").append(scanTimeStamp)
           .append(",file=").append(file)
           .append(",eventType=").append(eventType)
           .append(']');

        return buf.toString();
    }


}
