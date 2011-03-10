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
package com.funambol.common.pim.converter;

import java.util.TimeZone;

import com.funambol.framework.tools.StringTools;

import com.funambol.common.pim.calendar.*;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * This object is a converter from a Task object model to an XML string (SIF-T).
 *
 * @see Converter
 *
 * @version $Id: TaskToSIFT.java,v 1.9 2008-04-17 17:11:55 mauro Exp $
 */
public class TaskToSIFT extends CalendarToSIFE {

    // ------------------------------------------------------------ Private Data
    // ------------------------------------------------------------- Constructor
    public TaskToSIFT(TimeZone timezone, String charset) {
        super(timezone, charset);
    }
    // ---------------------------------------------------------- Public Methods
    /**
     * Performs the conversion.
     *
     * @param contact the Contact to be converted
     *
     * @return a string containing the converted representation of this Contact
     */
    @Override
    public String convert(Object obj) throws ConverterException {

        Task task = (Task) obj;

        StringBuffer xmlMsg = new StringBuffer(XML_VERSION);

        xmlMsg.append("\n<").append(SIFT.ROOT_TAG).append('>');
        xmlMsg.append("\n<").append(SIFCalendar.SIF_VERSION).append('>')
              .append(SIFCalendar.CURRENT_SIF_VERSION)
              .append("</").append(SIFCalendar.SIF_VERSION).append('>');

        String timeZoneID = getTimeZoneID(task.getDueDate());
        if (timeZoneID == null) {
            timeZoneID = getTimeZoneID(task.getDtStart());
            if (timeZoneID == null) {
                timeZoneID = getTimeZoneID(task.getReminder());
            }
        }
        
        if (timeZoneID == null) {
            if (timezone != null) {
                timeZoneID = timezone.getID();
            }
        } else {
            timezone = TimeZone.getTimeZone(timeZoneID);
        }
        
        if (timeZoneID != null) {
            long[] interval = task.extractInterval();
            CachedTimeZoneHelper helper = new CachedTimeZoneHelper(timeZoneID , 
                                                                   interval[0], 
                                                                   interval[1]);
            xmlMsg.append(helper.getSIF());
        }        
        
        xmlMsg.append(createTagFromProperty(SIFT.ACTUAL_WORK,task.getActualWork()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.CATEGORIES,task.getCategories()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.COMPANIES,task.getOrganizer()));

        // boolean
        if (task.getComplete().getPropertyValue() != null) {
            xmlMsg.append(createTagFromString(SIFT.COMPLETE,booleanToSIFValue((Boolean) task.getComplete().getPropertyValue())));
        }

        // date
        xmlMsg.append(createTagFromProperty(SIFT.START_DATE, task.getDtStart()));
        xmlMsg.append(createTagFromProperty(SIFT.DATE_COMPLETED, task.getDateCompleted()));
        xmlMsg.append(createTagFromProperty(SIFT.DUE_DATE, task.getDueDate()));


        xmlMsg.append(createTagFromProperty(SIFCalendar.IMPORTANCE, task.getImportance()));
        xmlMsg.append(createTagFromNumber(SIFCalendar.MILEAGE, task.getMileage()));
        xmlMsg.append(createTagFromProperty(SIFT.OWNER, task.getOwner()));
        xmlMsg.append(createTagFromProperty(SIFT.PERCENT_COMPLETE, task.getPercentComplete()));

        // boolean
        if (task.getReminder() == null) {
            xmlMsg.append(createTagFromString(SIFCalendar.REMINDER_SET,
                    booleanToSIFValue(false)));
        } else {
            xmlMsg.append(createTagFromString(SIFCalendar.REMINDER_SET,
                    booleanToSIFValue(task.getReminder().isActive())));

            // date
            int minutes = task.getReminder().getMinutes();
            if (minutes < 0) {
                minutes = 0;
            }
            xmlMsg.append(createTagFromString(
                    SIFCalendar.REMINDER_MINUTES_BEFORE_START,
                    String.valueOf(minutes)));

            String reminderTime = task.getReminder().getTime();
            reminderTime = handleConversionToLocalDate(reminderTime, timezone);
            xmlMsg.append(createTagFromString(SIFCalendar.REMINDER_TIME, reminderTime));
        }

        xmlMsg.append(createTagFromObject(SIFCalendar.SENSITIVITY,
                task.getSensitivity().getPropertyValue()));
        xmlMsg.append(createTagFromTaskStatusProperty(SIFCalendar.STATUS,
                task.getStatus()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.SUBJECT,
                task.getSummary()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.BODY,
                task.getDescription()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.FOLDER,
                task.getFolder()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.LOCATION,
                task.getLocation()));
        xmlMsg.append(createTagFromProperty(SIFCalendar.BILLING_INFORMATION,
                task.getBillingInformation()));

        // boolean
        xmlMsg.append(createTagFromProperty(SIFT.TEAM_TASK, task.getTeamTask()));

        xmlMsg.append(createTagFromProperty(SIFT.TOTAL_WORK, task.getTotalWork()));

        String ir = booleanToSIFValue(task.isRecurrent());

        xmlMsg.append(createTagFromObject(SIFCalendar.RECURRENCE_IS_RECURRING, ir));

        xmlMsg.append(createTagsFromRecurrencePattern(task.getRecurrencePattern()));

        xmlMsg.append(createTagsFromAttendeeList(task.getAttendees()));

        xmlMsg.append("\n</").append(SIFT.ROOT_TAG).append('>');


        return xmlMsg.toString();
    }

     protected StringBuffer createTagFromTaskStatusProperty(String tag, Property prop) {
         return createTagFromString(tag, CalendarStatus.getSifStatusFromServerValue(prop));
     }

    /**
     * Returns a string representing the tag with the specified name and the content (value and
     * attributes) fetched from the given Property element
     *
     * @param tag the tag name
     * @param prop the property
     * @return an XML representation of the tag
     */
    @Override
    protected StringBuffer createTagFromProperty(String tag, Property prop)
            throws ConverterException {
        StringBuffer output = new StringBuffer();

        String propValue = null;
        if (prop != null) {
            propValue = (String) prop.getPropertyValue();
            if (propValue == null) {
                return output;
            }
            if (propValue.length() == 0) {
                output.append('<').append(tag).append("/>");
                return output;
            }
        }

        if (SIFT.START_DATE.equalsIgnoreCase(tag) ||
                SIFT.DUE_DATE.equalsIgnoreCase(tag)) {

            TimeZone timezoneIn = null;
            TimeZone timezoneOut = null;
            if (propValue.endsWith("Z")) {
                timezoneIn = TimeUtils.TIMEZONE_UTC;
            } else if (timezone != null) {
                timezoneIn = timezone;
            } else {
                timezoneIn = TimeZone.getDefault();
            }
            if (timezone != null) {
                timezoneOut = timezone;
            } else {
                timezoneOut = TimeZone.getDefault();
            }

            propValue = handleConversionToAllDayDate((String) propValue, timezoneIn, timezoneOut);

        } else if (SIFT.DATE_COMPLETED.equalsIgnoreCase(tag) ||
                SIFCalendar.LAST_MODIFIED.equalsIgnoreCase(tag) ||
                SIFCalendar.DTSTAMP.equalsIgnoreCase(tag)) {
            propValue = handleConversionToLocalDate((String) propValue,
                    timezone);
        } else if (SIFCalendar.IMPORTANCE.equalsIgnoreCase(tag)) {

            try {
                propValue = String.valueOf(
                        importance19To02(Integer.parseInt((String) propValue)));
            } catch (NumberFormatException nfe) {
                return output; // empty
            }
        }

        if (propValue != null) {
            if (propValue.length() > 0) {
                StringBuffer params = getParams(prop);
                if (params.length() > 0) {
                    if (params.indexOf("QUOTED-PRINTABLE") > 0) {
                        //
                        //Decodes a quoted-printable string into its
                        //original form using the specified string charset
                        //
                        output.append('<').append(tag).append(params).append('>').append(StringTools.escapeBasicXml(propValue)).append("</").append(tag).append('>');
                    } else {
                        output.append('<').append(tag).append(params).append('>').append(StringTools.escapeBasicXml(propValue)).append("</").append(tag).append('>');
                    }
                } else {
                    output.append('<').append(tag).append('>').append(StringTools.escapeBasicXml(propValue)).append("</").append(tag).append('>');
                }
            } else {
                output.append('<').append(tag).append("/>");
            }
        //} else {
        //   do nothing! don't write the tag
        }
        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and
     * value for type "short" or "int"
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    protected static StringBuffer createTagFromNumber(String tag, java.lang.Number propertyValue) {
        StringBuffer output = new StringBuffer();

        if (propertyValue != null) {
            output.append('<').append(tag).append('>');
            output.append(propertyValue);
            output.append("</").append(tag).append('>');
        }

        return output;
    }

    private static String getTimeZoneID(PropertyWithTimeZone property) {
        if (property == null) {
            return null;
        }
        return property.getTimeZone();
    }    
}
