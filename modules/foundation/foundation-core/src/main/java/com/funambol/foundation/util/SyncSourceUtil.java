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

package com.funambol.foundation.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.funambol.framework.core.CTInfo;
import com.funambol.framework.core.DataStore;
import com.funambol.framework.engine.source.SyncContext;

import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.Task;

/**
 * This class contains usefull methods to use in the SyncSources.
 *
 * @version $Id: SyncSourceUtil.java 34865 2010-07-05 15:18:44Z luigiafassina $
 */
public class SyncSourceUtil {

    // --------------------------------------------------------------- Constants
    public static final String SIFE_FORMAT   = "text/x-s4j-sife" ; // SIF-Event
    public static final String SIFT_FORMAT   = "text/x-s4j-sift" ; // SIF-Task
    public static final String VCAL_FORMAT   = "text/x-vcalendar"; // vCal (1.0)
    public static final String ICAL_FORMAT   = "text/calendar"   ; // iCal (2.0)
    public static final String ANYSIF_FORMAT = "text/x-s4j-sif?" ;

    public static final String SIFC_FORMAT  = "text/x-s4j-sifc"; // SIF-Contact
    public static final String VCARD_FORMAT = "text/x-vcard"   ; // vCard

    public static final String SIFN_FORMAT      = "text/x-s4j-sifn"; // SIF-Note
    public static final String TEXTPLAIN_FORMAT = "text/plain"     ; // plain

    // ---------------------------------------------------------- Public methods

   /**
    * Finds the preferred RX content type, looking through all the datastores
    * retrieved by context for Calendar SyncSources.
    *
    * @param context the SyncContext of the current synchronization session.
    * @param entityType the Class that identifies the calendar type
    * @return a string containing the preferred MIME type ("text/x-vcalendar" or
    *         "text/calendar" or "text/x-s4j-sif?"), or null if no preferred
    *         MIME type could be found out. "text/x-s4j-sif?" is a placeholder
    *         for SIF-E and/or SIF-T, the entity type(s) actually used depends
    *         on the entityType field.
    */
    public static String getCalendarPreferredType(SyncContext context   ,
                                                  Class       entityType) {

        List<DataStore> dataStores;
        try {
            dataStores = context.getPrincipal()
                                .getDevice()
                                .getCapabilities()
                                .getDevInf()
                                .getDataStores();

            if (dataStores == null) {
                return null;
            }

            Set<String> rxPrefs = new HashSet<String>();
            for (DataStore dataStore : dataStores) {
                CTInfo rxPref = dataStore.getRxPref();
                if (rxPref != null) {
                    rxPrefs.add(rxPref.getCTType());
                }
            }

            return getCalendarPreferredType(rxPrefs, entityType);

        } catch (NullPointerException e) { // something is missing
            return null;
        }

    }

   /**
    * Finds the preferred RX content type, looking through all the given
    * preferred RX. The entityType of the SyncSource is used to decide which
    * preferred content type suggest.
    *
    * @param rxPrefs the set of preferred RX to analyse.
    * @param entityType the Class that identifies the calendar type
    * @return a string containing the preferred MIME type ("text/x-vcalendar" or
    *         "text/calendar" or "text/x-s4j-sif?"), or null if no preferred
    *         MIME type could be found out. "text/x-s4j-sif?" is a placeholder
    *         for SIF-E and/or SIF-T, the entity type(s) actually used depends
    *         on the entityType field.
    */
    private static String getCalendarPreferredType(Set<String> rxPrefs   ,
                                                   Class       entityType) {

        if (rxPrefs == null || rxPrefs.isEmpty()) {
            return null;
        }

        boolean xvCalendar = false;
        boolean iCalendar = false;

        boolean sife = false;
        boolean sift = false;

        boolean isEvent = false;
        boolean isTask = false;

        //
        // Since CalendarContent is extended by Event and Task classes, the IF
        // sequence is important: an Event or a Task is for sure assignable from
        // CalendarContent, so the check on it should be the first one.
        //
        if (entityType.isAssignableFrom(CalendarContent.class)) {
            isEvent = true;
            isTask = true;
        } else if (entityType.isAssignableFrom(Event.class)) {
            isEvent = true;
        } else if (entityType.isAssignableFrom(Task.class)) {
            isTask = true;
        }

        if (rxPrefs.contains(VCAL_FORMAT)) {
            xvCalendar = true;
        }
        if (rxPrefs.contains(ICAL_FORMAT)) {
            iCalendar = true;
        }
        if (isEvent && rxPrefs.contains(SIFE_FORMAT)) {
            sife = true;
        }
        if (isTask && rxPrefs.contains(SIFT_FORMAT)) {
            sift = true;
        }
        
        if (xvCalendar && !iCalendar && !sife && !sift) {
            return VCAL_FORMAT; // "text/x-vcalendar"
        }
        if (!xvCalendar && iCalendar && !sife && !sift) {
            return ICAL_FORMAT; // "text/calendar"
        }
        if (!xvCalendar && !iCalendar && (sife || sift)) {
            return ANYSIF_FORMAT; // "text/x-s4j-sif?"
        }

        // more than one type  -> ambiguous case
        // no type             -> no information
        return null;
    }

   /**
    * Finds the preferred RX content type, looking through all the datastores
    * retrieved by context for Contact SyncSources.
    *
    * @param context the SyncContext of the current synchronization session.
    * @return a string containing the preferred MIME type ("text/x-vcard" or
    *         "text/x-s4j-sifc"), or null if no preferred MIME type could be
    *         found out.
    */
    public static String getContactPreferredType(SyncContext context) {

        List<DataStore> dataStores;
        try {
            dataStores = context.getPrincipal()
                                .getDevice()
                                .getCapabilities()
                                .getDevInf()
                                .getDataStores();

            if (dataStores == null) {
                return null;
            }

            Set<String> rxPrefs = new HashSet<String>();
            for (DataStore dataStore : dataStores) {
                CTInfo rxPref = dataStore.getRxPref();
                if (rxPref != null) {
                    rxPrefs.add(rxPref.getCTType());
                }
            }

            return getContactPreferredType(rxPrefs);

        } catch (NullPointerException e) { // something is missing
            return null;
        }

    }

   /**
    * Finds the preferred RX content type, looking through all the given contact
    * preferred RX.
    *
    * @param rxPrefs the set of preferred RX to analyse.
    * @return a string containing the preferred MIME type ("text/x-vcard" or
    *         "text/x-s4j-sifc"), or null if no preferred MIME type could be
    *         found out.
    */
    private static String getContactPreferredType(Set<String> rxPrefs) {

        if (rxPrefs == null || rxPrefs.isEmpty()) {
            return null;
        }

        boolean sifC = false;
        boolean vCard = false;

        if (rxPrefs.contains(VCARD_FORMAT)) {
            vCard = true;
        }
        if (rxPrefs.contains(SIFC_FORMAT)) {
            sifC = true;
        }

        if (sifC && !vCard) {
            return SIFC_FORMAT; // "text/x-s4j-sifc"
        }
        if (!sifC && vCard) {
            return VCARD_FORMAT; // "text/x-vcard"
        }

        // sifC  && vCard  -> ambiguous case
        // !sifC && !vCard -> no information
        return null;

    }

   /**
    * Finds the preferred RX content type, looking through all the datastores
    * retrieved by context for Note SyncSources.
    *
    * @param context the SyncContext of the current synchronization session.
    * @return a string containing the preferred MIME type ("text/plain" or
    *         ("text/x-s4j-sifn"), or null if no preferred MIME type could be
    *         found out.
    */
    public static String getNotePreferredType(SyncContext context) {

        List<DataStore> dataStores;
        try {
            dataStores = context.getPrincipal()
                                .getDevice()
                                .getCapabilities()
                                .getDevInf()
                                .getDataStores();

            if (dataStores == null) {
                return null;
            }

            Set<String> rxPrefs = new HashSet<String>();
            for (DataStore dataStore : dataStores) {
                CTInfo rxPref = dataStore.getRxPref();
                if (rxPref != null) {
                    rxPrefs.add(rxPref.getCTType());
                }
            }

            return getNotePreferredType(rxPrefs);

        } catch (NullPointerException e) { // something is missing
            return null;
        }

    }

   /**
    * Finds the preferred RX content type, looking through all the given note
    * preferred RX.
    *
    * @param rxPrefs the set of preferred RX to analyse.
    * @return a string containing the preferred MIME type ("text/plain" or
    *         ("text/x-s4j-sifn"), or null if no preferred MIME type could be
    *         found out.
    */
    private static String getNotePreferredType(Set<String> rxPrefs) {

        if (rxPrefs == null || rxPrefs.isEmpty()) {
            return null;
        }

        boolean sifN      = false;
        boolean textPlain = false;

        if (rxPrefs.contains(TEXTPLAIN_FORMAT)) {
            textPlain = true;
        }
        if (rxPrefs.contains(SIFN_FORMAT)) {
            sifN = true;
        }

        if (sifN && !textPlain) {
            return SIFN_FORMAT; // "text/x-s4j-sifn"
        }
        if (!sifN && textPlain) {
            return TEXTPLAIN_FORMAT; // "text/plain"
        }

        // sifN  && textPlain  -> ambiguous case
        // !sifN && !textPlain -> no information
        return null;
    }

}
