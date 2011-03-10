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
package com.funambol.foundation.items.manager ;

import java.sql.Timestamp;

import java.text.ParseException;
import java.util.List;

import com.funambol.framework.tools.merge.MergeResult;

import com.funambol.server.config.Configuration;

import com.funambol.common.pim.calendar.Calendar;

import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.dao.EntityDAO;
import com.funambol.foundation.items.dao.PIMCalendarDAO;
import com.funambol.foundation.items.model.CalendarWrapper;

/**
 * Manager for calendar sync
 * @version $Id: PIMCalendarManager.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public class PIMCalendarManager extends PIMEntityManager {

    //------------------------------------------------------------- Private data

    private PIMCalendarDAO dao;

    //------------------------------------------------------------- Constructors

    /**
     * @see PIMEntityDAO#PIMEntityDAO(String, String)
     */
    public PIMCalendarManager(
            String userId,
            Class allowedContent) {
        this.dao = new PIMCalendarDAO(userId, allowedContent);
        if (log.isTraceEnabled()) {
            log.trace("Created new PIMCalendarManager for user ID " + userId);
        }
    }
    
    /**
     * @see PIMEntityDAO#PIMEntityDAO(String, String)
     * @deprecated Since v66 the constructor without jndiDataSourceName should be used
     */
    public PIMCalendarManager(
            String jndiDataSourceName,
            String userId,
            Class allowedContent) {
        
            this(userId, allowedContent);
    }

    //----------------------------------------------------------- Public methods

    /**
     * Updates a calendar.
     *
     * @param uid the UID of the calendar
     * @param c a Calendar object
     * @param t the last update time that will be forced as the last update time
     *          of the updated calendar
     * @return the UID of the calendar (it should be the same as the argument)
     * @throws EntityException
     */
    public String updateItem(String uid, Calendar c, Timestamp t)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Updating calendar with ID " + uid + " on the server, "
                    + "setting " + t + " as its last update timestamp.");
        }

        CalendarWrapper cw = new CalendarWrapper(uid, dao.getUserId(), c);
        cw.setLastUpdate(t);

        try {
            dao.updateItem(cw);
            return cw.getId();
        } catch (Exception e) {
            throw new EntityException("Error updating item.", e);
        }
    }

    /**
     * @see PIMCalendarDAO#addItem(Calendar)
     */
    public String addItem(Calendar c, Timestamp t)
            throws EntityException {

        try {
            CalendarWrapper cw = createCalendarWrapper(c);
            cw.setLastUpdate(t);
            dao.addItem(cw);
            return cw.getId();
        } catch (DAOException dbae) {
            throw new EntityException("Error adding item.", dbae);
        }
    }

    /**
     * @see PIMCalendarDAO#getItem(String)
     */
    public CalendarWrapper getItem(String id)
            throws EntityException {
        try {
            return dao.getItem(id);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting item. ", dbae);
        }
    }

    /**
     * @see PIMCalendarDAO#getTwinItems(Calendar)
     */
    public List getTwins(Calendar c)
            throws EntityException {
        try {
            return dao.getTwinItems(c);
        } catch (DAOException dbae) {
            throw new EntityException("Error getting twins of an item.", dbae);
        }
    }

    public boolean mergeItems(String serverCalendarID, Calendar clientCalendar,
            Timestamp t) throws EntityException {

        log.trace("PIMCalendarManager mergeItems begin");
        if (log.isTraceEnabled()) {
            log.trace("Merging server item " + serverCalendarID
                    + " with its client counterpart.");
        }

        try {

            CalendarWrapper serverCalendarWrapper =
                    dao.getItem(serverCalendarID);
            Calendar serverCalendar = serverCalendarWrapper.getCalendar();
            MergeResult mergeResult = clientCalendar.merge(serverCalendar);
            if (mergeResult.isSetBRequired()) {
                updateItem(serverCalendarID, serverCalendar, t);
            }

            //
            // If funambol is not in the debug mode is not possible to print the
            // merge result because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {
                if (log.isTraceEnabled()) {
                    log.trace("MergeResult: " + mergeResult);
                }
            }

            return mergeResult.isSetARequired();

        } catch (Exception e) {

            log.error("Error merging items.", e);

            throw new EntityException("Error merging items. ", e);
        }
    }

    /**
     *
     * @param c the calendar we want to check.
     *
     * @return true if at least one field used for the twin search in the given
     * calendar contains meaningful data, false otherwise
     */

    public boolean isTwinSearchAppliableOn(Calendar c) {
       return this.dao.isTwinSearchAppliableOn(c);
    }

    //-------------------------------------------------------- Protected methods

    /**
     *
     * returns the PIMCalendarEntityDAO
     * @return
     */
    protected EntityDAO getEntityDAO() {
        return dao;
    }

    //---------------------------------------------------------- Private methods

    private CalendarWrapper createCalendarWrapper(Calendar c) {

        if (log.isTraceEnabled()) {
            log.trace(
                    "Created a new CalendarWrapper (UID not yet generated).");
        }
        return new CalendarWrapper(null, dao.getUserId(), c);
    }
}


