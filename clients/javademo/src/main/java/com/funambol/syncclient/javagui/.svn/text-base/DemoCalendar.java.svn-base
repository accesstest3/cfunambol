/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2003-2007 Funambol, Inc.
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
package com.funambol.syncclient.javagui;

import com.funambol.common.pim.calendar.Calendar;

/**
 * An object representing a calendar.
 * Each calendar is identified by its filename and the calendar itself in the 
 * form of a Calendar object.
 *
 * @see com.funambol.common.pim.calendar.Calendar
 *
 * @version $Id: DemoCalendar.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class DemoCalendar {

    //------------------------------------------------------------- Private data
    private String   filename = null;
    private Calendar calendar = null;

    //----------------------------------------------------------- Public methods

    /**
     * Creates a new calendar
     *
     * @param filename the filename for this calendar
     * @param calendar the Calendar object for this calendar
     */
    public DemoCalendar(String filename, Calendar calendar) {
        this.filename = filename;
        this.calendar = calendar;
    }

    /**
     * Returns the filename for this calendar
     *
     * @return the filename for this calendar
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the calendar object for this calendar
     *
     * @return the calendar object for this calendar
     */
    public Calendar getCalendar() {
        return calendar;
    }

    /**
     * Sets the calendar objecy of this calendar
     *
     * @param calendar the calendar object to set
     */
    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

}
