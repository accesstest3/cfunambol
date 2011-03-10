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

import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.funambol.common.pim.utility.TimeUtils;

import com.funambol.syncclient.spdm.DMException;
import com.funambol.syncclient.spdm.SimpleDeviceManager;
import com.funambol.syncclient.spdm.ManagementNode;

/**
 * Fields helper methods.
 *
 * @version $Id: FieldsHelper.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public abstract class FieldsHelper implements ConfigurationParameters {

    //---------------------------------------------------------------- Constants

    public static final String DATE_FORMAT           = "dd/MM/yyyy"          ;
    public static final String DATE_FORMAT_AND_TIME  = "dd/MM/yyyy HH:mm:ss" ;
    public static final String UTC_DATE_FORMAT       = "yyyyMMdd'T'HHmmss'Z'";
    public static final String NO_UTC_DATE_FORMAT    = "yyyyMMdd'T'HHmmss"   ;
    public static final String YYYY_MM_DD_FORMAT     = "yyyy-MM-dd"          ;

    //------------------------------------------------------------- Private data
    private static Language ln = new Language();

    //-------------------------------------------------------- Protected methods
    protected static boolean checkDate(String date, String dateFormat) {

        StringTokenizer  st        = null;
        SimpleDateFormat formatter = null;
        int k = 0;

        if (date != null && date.length() > 0) {

            try {
                String pattern = TimeUtils.getDateFormat(date);
                if (pattern == null) {
                    throw new NumberFormatException(
                        ln.getString("error_number_format"));
                }
                if (dateFormat.equals(DATE_FORMAT)) {
                    formatter = new SimpleDateFormat(DATE_FORMAT);
                } else if (dateFormat.equals(DATE_FORMAT_AND_TIME)) {
                    formatter = new SimpleDateFormat(DATE_FORMAT_AND_TIME);
                }
                formatter.setLenient(false);

                if (dateFormat.equals(DATE_FORMAT)) {
                    st = new StringTokenizer(date, "/");
                    while (st.hasMoreTokens()) {
                        k = Integer.parseInt(st.nextToken());
                        if (k > 9999) {
                            throw new NumberFormatException(
                                ln.getString("error_number_format"));
                        }
                    }
                } else if (dateFormat.equals(DATE_FORMAT_AND_TIME)) {
                    String sTemp =  date.substring(0, 10);
                    st = new StringTokenizer(sTemp, "/");
                    while (st.hasMoreTokens()) {

                        k = Integer.parseInt(st.nextToken());

                        if (k > 9999) {
                            throw new NumberFormatException(
                                ln.getString ("error_number_format"));
                        }

                    }
                    sTemp = date.substring(11);

                    st = new StringTokenizer(sTemp, ":");
                    int i = 0;
                    while (st.hasMoreTokens()) {
                        String s = st.nextToken();

                        if (s.length() > 2) {
                            throw new NumberFormatException(
                                ln.getString ("error_number_format"));
                        }
                        k = Integer.parseInt(s);

                        // HH
                        if (i == 0 && (k < 0 || k > 24)) {
                            throw new NumberFormatException(
                                ln.getString ("error_number_format"));
                        }
                        // MM
                        if (i == 1 && (k < 0 || k > 60)) {
                            throw new NumberFormatException(
                                ln.getString ("error_number_format"));
                        }
                        // ss
                        if (i == 2 && (k < 0 || k > 60)) {
                            throw new NumberFormatException(
                                ln.getString ("error_number_format"));
                        }

                        i++;
                    }
                }
            } catch (NullPointerException e) {
               return false;
            } catch(NumberFormatException e) {
               return false;
            } catch(Exception e) {
               return false;
            }
        }

        return true;
    }

    protected static String convertDateToUTC(String stringDate) {

        ManagementNode rootNode = null;

        if (stringDate == null || stringDate.length() == 0) {
            return "";
        }

        rootNode = SimpleDeviceManager.getDeviceManager().getManagementTree();

        try {
            Hashtable parameters = 
                rootNode.getNodeValues(MainWindow.DM_VALUE_PATH);
        } catch (DMException e) {
            e.printStackTrace();
        }
        
        return TimeUtils.localTimeToUTC(stringDate, null);
    }

    protected static String convertDateFromUTC (String stringDate) {
        if (stringDate == null || stringDate.length() == 0) {
            return "";
        }

        return TimeUtils.UTCToLocalTime(stringDate, null);
    }

    /**
     * Convert date from yyyy-MM-dd format into display format dd/MM/yyyy.
     * This conversion is necessary for birthday field and for start and end 
     * date when the event is an all day event.
     *
     * @param stringDate the date to convert
     * @return String the date into proper format
     * @throws ParseException 
     */
    protected static String convertInDayFormatFrom(String stringDate) 
    throws ParseException {
        if (stringDate == null || stringDate.length() == 0) {
            return "";
        }

        return TimeUtils.convertDateFromTo(stringDate, DATE_FORMAT);
    }

    /**
     * Convert date from dd/MM/yyyy format into display format yyyy-MM-dd.
     * This conversion is necessary for birthday field and for start and end 
     * date when the event is an all day event.
     *
     * @param stringDate the date to convert
     * @return String the date into proper format
     * @throws ParseException 
     */
    protected static String convertInDayFormatTo(String stringDate) 
    throws ParseException {

        if (stringDate == null || stringDate.length() == 0) {
            return "";
        }

        stringDate = TimeUtils.convertDateFromTo(stringDate, YYYY_MM_DD_FORMAT);

        return TimeUtils.normalizeToISO8601(stringDate, null);
    }

    /**
     * Transforms a date in format d/M/yyyy into 0d/0M/yyyy.
     * If the year starts with 0 all the starting 0 are removed
     * e.g. year 0022 even if it has format yyyy is not valid
     *
     * @param date the input date to be normalized
     * @return normalized date
     */
    protected static String dateNormalize(String date) {
        if (date == null || date.equals("")) {
            return date;
        }
        StringBuffer result = new StringBuffer();
        //if day or month is <10 (length=1) then add 0 at the begining of the string
        String temp = "";
        StringTokenizer st = new StringTokenizer(date, "/");
        int i = 0;
        while ((i < 2) && (st.hasMoreElements())) {
            temp = (String) st.nextElement();
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            result.append(temp).append("/");
            i++;
        }

        String year = "";
        if (st.hasMoreElements()) {
            year = (String) st.nextElement();
        }
        // remove the starting 0 from the year
        // year of type 0010 are not accepted even they are of format yyyy
        while (year.startsWith("0")) {
            year = year.substring(1);
        }
        result = result.append(year);
        return result.toString();                
    }

    /**
     * Transforms a time in format h:m into 0h:0m
     *
     * @param time the input time to be normalized
     * @return normalized time
     */
    protected static String timeNormalize(String time) {
        if (time == null || time.equals("")) {
            return time;
        }
        StringBuffer result = new StringBuffer();
        //if day or month is <10 (length=1) then add 0 at the begining of the string
        String temp = "";
        StringTokenizer st = new StringTokenizer(time, ":");
        while (st.hasMoreElements()) {
            temp = (String) st.nextElement();
            if (temp.length() == 1) {
                temp = "0" + temp;
            }
            result.append(temp).append(":");
        }
        if ((result.length() > 0)&&(result.charAt(result.length()-1)==':')) {
            result.deleteCharAt(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * Compares two dates given as Strings. If end date is before the start date
     * false is returned, true is returned otherwise.
     * @param startDate start date
     * @param endDate end date
     * @return false if end date is before start date, true otherwise
     */
    protected static boolean compareStartDateEndDate(String startDate, String endDate) {
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT_AND_TIME);
        Date sDate = null;
        Date eDate = null;
        try {
            sDate = formatter.parse(startDate);
            eDate = formatter.parse(endDate);
        } catch(ParseException e) {
            e.printStackTrace();
            return false;
        }

        return  eDate.getTime() >= sDate.getTime();
    }
}
