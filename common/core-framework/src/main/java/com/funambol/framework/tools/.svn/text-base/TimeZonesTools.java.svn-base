/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.framework.tools;

import java.util.*;

/**
 * Provides the timezones list to show creating/updating a device.
 * <p>The timezone id is usually formed by an first token + '/' + others token.
 * (i.e.: Europe/London, America/Chicago).<br>
 * There are also the ids as 'CET' or 'EST' formed by one token.
 * <p>This implementation returns a timezones list with the timezones have
 * the first token included in the <code>TIMEZONE_TO_INCLUDE</code>.
 * The timezone formed by only one token are excluded.
 *
 *
 * @version $Id: TimeZonesTools.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class TimeZonesTools {

    // ------------------------------------------------------- Private Constants

    private static String TIMEZONE_TO_INCLUDE =
        ",PACIFIC,US,AMERICA,CANADA,MEXICO,NAVAJO,CANADA,CHILE,BRAZIL," +
        "CUBA,JAMAICA,ATLANTIC,AFRICA,EUROPE,ARCTIC,POLAND,ASIA,EGYPT,ISRAEL,"+
        "LIBYA,TURKEY,INDIAN,MIDEAST,IRAN,ANTARCTICA,AUSTRALIA,HONGKONG,SINGAPORE,"+
        "JAPAN,KWAJALEIN,";

    private static String TIMEZONE_TO_EXCLUDE = ",US/PACIFIC-NEW,";

    private static String[] availablesTimeZonesDescr = null;

    private static Map availablesTimeZones   = null;

    static {
        availablesTimeZones = new HashMap();
        List tzsDescr = new ArrayList();
        String[] ids  = TimeZone.getAvailableIDs();
        int index     = -1;
        String tmp    = null;
        TimeZone tz   = null;
        String descr  = null;

        for (int i = 0; i < ids.length; i++) {
            index = ids[i].indexOf('/');
            if (index == -1) {
                tmp = ids[i];
            } else {
                tmp = ids[i].substring(0, index);
            }

            if (TIMEZONE_TO_INCLUDE.indexOf("," + tmp.toUpperCase() + ",") != -1) {
                if (TIMEZONE_TO_EXCLUDE.indexOf("," + ids[i].toUpperCase() + ",") == -1) {
                    descr = getTimeZoneDescr(ids[i]);
                    tzsDescr.add(descr);
                    availablesTimeZones.put(descr, ids[i]);
                }
            }
        }
        Collections.sort(tzsDescr);
        availablesTimeZonesDescr = (String[])tzsDescr.toArray(new String[0]);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns a map of the availables timezones.
     * <p>The map key is the description of the timezone and the value
     * is the timezone id.
     * <p>The description is in the following format:
     * <br><code>id (GMT+/-Hours:Minutes)<code>
     * @return Map
     */
    public static Map getAvailablesTimeZones() {
        return availablesTimeZones;
    }

    /**
     * Returns an array of availables timezones description
     * @return String[]
     */
    public static String[] getAvailablesTimeZonesDescr() {
        return availablesTimeZonesDescr;
    }

    /**
     * Returns the description of the given timezone.
     * <p>The description is in the following format:
     * <br><code>id (GMT+/-Hours:Minutes)<code>
     *
     * @param tz TimeZone
     * @return String
     */
    public static String getTimeZoneDescr(TimeZone tz) {
        if (tz == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer(tz.getID().replace('_', ' '));
        sb.append(" (").append(getGMT(tz)).append(")");
        return sb.toString();
    }

    /**
     * Returns the description of the given timezone id.
     * <p>The description is in the following format:
     * <br><code>id (GMT+/-Hours:Minutes)<code>
     *
     * @param tz TimeZone
     * @return String
     */
    public static String getTimeZoneDescr(String timeZoneId) {
        if (timeZoneId == null) {
            return null;
        }
        return getTimeZoneDescr(TimeZone.getTimeZone(timeZoneId));
    }
    // --------------------------------------------------------- Private Methods


    /**
     * Returns the GMT of the given timezones.
     * <p>The GMT is in the following format:
     * <br>(GMT+/-Hours:Minutes)<code>
     *
     * @param tz TimeZone
     * @return String
     */
    private static String getGMT(TimeZone tz) {
        int offset = tz.getRawOffset();

        String sign = (offset >= 0) ? "+" : "-";

        offset = Math.abs(offset);

        int hours = offset / (60 * 60 * 1000);
        int minutes = (offset - (hours * 60 * 60 * 1000)) / (60 * 1000);

        String padHours   = ( (hours < 10) && (hours > -10))     ? "0" : "";
        String padMinutes = ( (minutes < 10) && (minutes > -10)) ? "0" : "";

        StringBuffer description = new StringBuffer("GMT");
        description.append(sign).append(padHours).append(hours).append(":").append(padMinutes).append(minutes);

        return description.toString();
    }

}
