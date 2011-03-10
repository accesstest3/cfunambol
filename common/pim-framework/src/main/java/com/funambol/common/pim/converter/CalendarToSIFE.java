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

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import com.funambol.framework.tools.StringTools;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.common.XTag;
import com.funambol.common.pim.calendar.*;
import com.funambol.common.pim.utility.TimeUtils;


/**
 * This object is a converter from a Calendar object model to an XML string (SIF-E).
 *
 * @see Converter
 * @version $Id: CalendarToSIFE.java,v 1.9 2008-07-22 14:48:57 nichele Exp $
 */
public class CalendarToSIFE extends BaseConverter {

    // ------------------------------------------------------------ Private Data
    protected static final String XML_VERSION =
            "<?xml version=\"1.0\" encoding=\""
            + System.getProperty("file.encoding")
            + "\"?>";

    // ------------------------------------------------------------- Constructor
    public CalendarToSIFE(TimeZone timezone, String charset) {
        //
        // The SIF format is used by Funambol plug-in that supports UTC, so is
        // not need to convert dates in local time
        //
        super(timezone, charset, false);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Performs the conversion.
     *
     * @param obj the Calendar to be converted
     *
     * @return a string containing the converted representation of this calendar
     */
    public String convert(Object obj) throws ConverterException {

        Calendar calendar = (Calendar)obj;
        StringBuffer output = new StringBuffer(XML_VERSION);

        output.append("\n<").append(SIFE.ROOT_TAG).append('>');
        output.append("\n<").append(SIFCalendar.SIF_VERSION).append('>')
              .append(SIFCalendar.CURRENT_SIF_VERSION)
              .append("</").append(SIFCalendar.SIF_VERSION).append('>');

        output.append(createTagFromProperty(SIFCalendar.PRODID  , calendar.getProdId()));
        output.append(createTagFromProperty(SIFCalendar.VERSION , calendar.getVersion()));
        output.append(createTagFromProperty(SIFCalendar.CALSCALE, calendar.getCalScale()));
        output.append(createTagFromProperty(SIFCalendar.METHOD  , calendar.getMethod()));

        String timeZoneID = getTimeZoneID(calendar.getEvent().getDtStart());
        if (timeZoneID == null) {
            timeZoneID = getTimeZoneID(calendar.getEvent().getDtEnd());
            if (timeZoneID == null) {
                timeZoneID = getTimeZoneID(calendar.getEvent().getReminder());
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
            long[] interval = calendar.getEvent().extractInterval();
            CachedTimeZoneHelper helper = new CachedTimeZoneHelper(timeZoneID ,
                                                                   interval[0],
                                                                   interval[1]);
            output.append(helper.getSIF());
        }

        output.append(createTagFromObject(SIFE.ALL_DAY_EVENT, booleanToSIFValue(calendar.getEvent().isAllDay())));

        String ir = booleanToSIFValue(calendar.getEvent().isRecurrent());

        output.append(createTagFromObject(SIFCalendar.RECURRENCE_IS_RECURRING, ir));

        output.append(createTagFromCalendarXProperty(calendar));

        //
        // Note: we decided not to store the duration but only Start and End
        //
        if (calendar.getEvent().isAllDay()) {
            output.append(
                    createTagFromAllDayProperty(SIFE.START,
                    calendar.getEvent().getDtStart())
                    );

            output.append(
                    createTagFromAllDayProperty(SIFE.END,
                    calendar.getEvent().getDtEnd())
                    );

            output.append(
                    createTagFromAllDayProperty(SIFE.REPLY_TIME,
                    calendar.getEvent().getReplyTime())
                    );
        } else {
            output.append(
                    createTagFromProperty(SIFE.START,
                    calendar.getEvent().getDtStart())
                    );

            output.append(
                    createTagFromProperty(SIFE.END,
                    calendar.getEvent().getDtEnd())
                    );

            output.append(
                    createTagFromProperty(SIFE.REPLY_TIME,
                    calendar.getEvent().getReplyTime())
                    );
        }

        output.append(createTagFromProperty(SIFCalendar.BODY,calendar.getEvent().getDescription()));
        output.append(createTagFromProperty(SIFCalendar.STATUS,calendar.getEvent().getStatus()));
        output.append(createTagFromObject  (SIFCalendar.BUSY_STATUS,calendar.getEvent().getBusyStatus()));
        output.append(createTagFromObject  (SIFCalendar.MEETING_STATUS,calendar.getEvent().getMeetingStatus()));
        output.append(createTagFromProperty(SIFCalendar.CATEGORIES,calendar.getEvent().getCategories()));
        output.append(createTagFromProperty(SIFCalendar.COMPANIES,calendar.getEvent().getOrganizer()));
        output.append(createTagFromProperty(SIFCalendar.IMPORTANCE,calendar.getEvent().getPriority()));
        output.append(createTagFromProperty(SIFCalendar.LOCATION,calendar.getEvent().getLocation()));
        output.append(createTagFromObject  (SIFCalendar.SENSITIVITY,calendar.getEvent().getAccessClass().getPropertyValue()));
        output.append(createTagFromProperty(SIFCalendar.SUBJECT,calendar.getEvent().getSummary()));
        output.append(createTagFromProperty(SIFCalendar.FOLDER,calendar.getEvent().getFolder()));

        output.append(createTagFromEventXProperty(calendar));

        output.append(createTagForAllOtherEventProperty(calendar));

        output.append("</").append(SIFE.ROOT_TAG).append('>');

        return output.toString();
    }

    // ------------------------------------------------------- Protected methods

    /*
     * Returns a string with all X- properties in the format tag - value
     * for Calendar Object
     *
     * @param calendar the object calendar
     * @return an XML representation of the X- properties
     */
    protected StringBuffer createTagFromCalendarXProperty(Calendar calendar)
        throws ConverterException {

        StringBuffer sb = new StringBuffer();
        List xtags = calendar.getXTags();

        if (xtags == null) {
            return sb;
        }

        int size = xtags.size();
        String xtagName = null;
        Property xtagProp = null;
        Map<String, String> hashProperty = new HashMap<String, String>();
        Map<String, String> hashPropertyPlusCal = new HashMap<String, String>();
        for (int i=0; i<size; i++) {
            xtagName  = ((XTag)xtags.get(i)).getXTagValue();
            xtagProp = (((XTag)xtags.get(i)).getXTag());
            hashProperty = xtagProp.getXParams();
            hashPropertyPlusCal.putAll(hashProperty);
            hashPropertyPlusCal.put("cal", "cal");
            xtagProp.setXParams(hashPropertyPlusCal);
            sb.append(createTagFromProperty(xtagName, xtagProp));
        }
        // add the conventional attribute cal="cal" to flag this x-prop is
        // inside calendar object

        return sb;
    }

    /**
     * Returns a string with all X- properties in the format tag - value
     * for Event Object
     *
     * @param calendar the object calendar
     * @return an XML representation of the X- properties
     */
    protected StringBuffer createTagFromEventXProperty(Calendar calendar)
    throws ConverterException {

        StringBuffer sb = new StringBuffer();
        List xtags = calendar.getEvent().getXTags();

        if (xtags == null) {
            return sb;
        }

        int size = xtags.size();
        String xtagName = null;
        Property xtagProp = null;
        HashMap hashProperty = new HashMap();
        HashMap hashPropertyPlusCal = new HashMap();

        for (int i=0; i<size; i++) {
            xtagName  = ((XTag)xtags.get(i)).getXTagValue();
            xtagProp = (Property)(((XTag)xtags.get(i)).getXTag());
            hashProperty = xtagProp.getXParams();
            hashPropertyPlusCal.putAll(hashProperty);
            hashPropertyPlusCal.put("evt", "evt");
            xtagProp.setXParams(hashPropertyPlusCal);
            sb.append(createTagFromProperty(xtagName, xtagProp));
        }
        return sb;
    }

    /**
     * Returns a string with all properties in the format tag - value
     * for Event Object
     *
     * @param calendar the object calendar
     * @return an XML representation of the X- properties
     */
    protected StringBuffer createTagForAllOtherEventProperty(Calendar calendar)
    throws ConverterException {

        StringBuffer sb = new StringBuffer();

        sb.append(createTagForGEOProperty(
                calendar.getEvent().getLatitude(),
                calendar.getEvent().getLongitude()));

        sb.append(createTagFromProperty(SIFE.TRANSP,
                calendar.getEvent().getTransp()));
        sb.append(createTagFromProperty(SIFCalendar.URL,
                calendar.getEvent().getUrl()));
        sb.append(createTagFromProperty(SIFCalendar.UID,
                calendar.getEvent().getUid()));
        sb.append(createTagFromProperty(SIFCalendar.CONTACT,
                calendar.getEvent().getContact()));

        Property created = calendar.getEvent().getCreated();
        if (created != null) {
            sb.append(createTagFromProperty(created.getTag(),created));
        }

        sb.append(createTagFromProperty(SIFCalendar.DTSTAMP,
                calendar.getEvent().getDtStamp()));
        sb.append(createTagFromProperty(SIFCalendar.LAST_MODIFIED,
                calendar.getEvent().getLastModified()));
        sb.append(createTagFromProperty(SIFCalendar.SEQUENCE,
                calendar.getEvent().getSequence()));

        sb.append(createTagFromProperty(SIFCalendar.DALARM,
                calendar.getEvent().getDAlarm()));
        sb.append(createTagFromProperty(SIFCalendar.PALARM,
                calendar.getEvent().getPAlarm()));

        Reminder r = calendar.getEvent().getReminder();

        if (r != null) {
            sb.append(createTagFromString(SIFCalendar.REMINDER_SET,
                    booleanToSIFValue(r.isActive())));
            int minutes = r.getMinutes();
            if (minutes < 0) {
                minutes = 0;
            }
            sb.append(createTagFromString(SIFCalendar.REMINDER_MINUTES_BEFORE_START,
                    String.valueOf(minutes)));
            sb.append(createTagFromString(SIFCalendar.REMINDER_TIME,
                    r.getTime()));
            sb.append(createTagFromString(SIFCalendar.REMINDER_OPTIONS,
                    String.valueOf(r.getOptions())));
            sb.append(createTagFromString(SIFCalendar.REMINDER_SOUND_FILE,
                    r.getSoundFile()));
            sb.append(createTagFromString(SIFCalendar.REMINDER_INTERVAL,
                    String.valueOf(r.getInterval())));
            sb.append(createTagFromString(SIFCalendar.REMINDER_REPEAT_COUNT,
                    String.valueOf(r.getRepeatCount())));
        }

        RecurrencePattern recurrencePattern = calendar.getEvent().getRecurrencePattern();
        sb.append(createTagsFromRecurrencePattern(recurrencePattern));

        List<Attendee> attendees = calendar.getEvent().getAttendees();
        sb.append(createTagsFromAttendeeList(attendees));

        return sb;
    }

    protected StringBuffer createTagsFromRecurrencePattern(
            RecurrencePattern recurrencePattern) throws ConverterException {

        StringBuffer sb = new StringBuffer();

        if (recurrencePattern != null) {

            TimeZone tz = null;
            if (recurrencePattern.getTimeZone() != null) {
                tz = TimeZone.getTimeZone(recurrencePattern.getTimeZone());
            }
            String startDatePattern = recurrencePattern.getStartDatePattern();

            boolean startDatePatternInUTC = false;
            
            if (startDatePattern == null) {
                startDatePattern = "";
            } else {
                if (startDatePattern.endsWith("Z")) {
                    
                    startDatePatternInUTC = true;
                    startDatePattern =
                        handleConversionToLocalDate(startDatePattern, tz);
                }
            }
            
            short dayOfMonth    = recurrencePattern.getDayOfMonth();
            short dayOfWeekMask = recurrencePattern.getDayOfWeekMask();
            short monthOfYear   = recurrencePattern.getMonthOfYear();
            
            if (startDatePatternInUTC && tz != null) {
                //
                // We need to shift/arrange dayOfMonth, dayOfWeekMask and monthOfYear according
                // to the timezone since the patternStartDate was in UTC and was
                // converted in local date and also those properties must be converted.
                // This case can occurs performing a slow sync with  a v65 client 
                // with data (event) created with a v7 client (since in this case we have a timezone)
                // See bug# 5589 and synclet "FixV65RecurrentEventout.bsh" that does
                // the specular thing for the v65 plugin (converting local date in
                // UTC)
                //
                if (dayOfMonth != 0) {
                    dayOfMonth = Short.parseShort(startDatePattern.substring(6,8));
                }
                if (monthOfYear != 0) {
                    monthOfYear = Short.parseShort(startDatePattern.substring(4,6));
                }
            
                dayOfWeekMask = shiftDayOfWeekMask(recurrencePattern.getStartDatePattern(),  // this ends with 'Z'
                                                                                             // since startDatePatternInUTC = true
                                                   startDatePattern,                         // in local
                                                   dayOfWeekMask);
            }
            
            sb.append(createTagFromString(SIFCalendar.RECURRENCE_DAY_OF_MONTH,
                    String.valueOf(dayOfMonth)));

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_DAY_OF_WEEK_MASK,
                    String.valueOf(dayOfWeekMask)));

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_INTERVAL,
                    String.valueOf(recurrencePattern.getInterval())));

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_INSTANCE,
                    String.valueOf(recurrencePattern.getInstance())));

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_MONTH_OF_YEAR,
                    String.valueOf(monthOfYear)));

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_NO_END_DATE,
                    booleanToSIFValue(recurrencePattern.isNoEndDate())));


            sb.append(createTagFromString(SIFCalendar.RECURRENCE_START_DATE_PATTERN,
                    startDatePattern));

            String endDatePattern = recurrencePattern.getEndDatePattern();
            if (endDatePattern == null) {
                endDatePattern = "";
            } else {
                endDatePattern =
                    handleConversionToLocalDate(endDatePattern, tz);
            }
            sb.append(createTagFromString(SIFCalendar.RECURRENCE_END_DATE_PATTERN,
                    endDatePattern));

            if (recurrencePattern.getOccurrences() > 0) {
                sb.append(createTagFromString(SIFCalendar.RECURRENCE_OCCURRENCES,
                        String.valueOf(recurrencePattern.getOccurrences())));
            }

            sb.append(createTagFromString(SIFCalendar.RECURRENCE_TYPE,
                    String.valueOf(recurrencePattern.getTypeId())));

            sb.append('<').append(SIFCalendar.RECURRENCE_EXCEPTIONS).append('>')
            .append(createTagsFromExceptionList(recurrencePattern
                    .getExceptions())).append("</")
                    .append(SIFCalendar.RECURRENCE_EXCEPTIONS).append('>');
        } else {
            sb.append('<').append(SIFCalendar.RECURRENCE_EXCEPTIONS)
            .append("/>");
        }
        return sb;
    }

    protected static StringBuffer createTagsFromAttendeeList(
            List<Attendee> attendees) throws ConverterException {

        StringBuffer sb = new StringBuffer();

        if (attendees.isEmpty()) {
            sb.append('<').append(SIFCalendar.ATTENDEES).append("/>");
            return sb;
        }

        sb.append('<').append(SIFCalendar.ATTENDEES).append('>');

        for (Attendee attendee : attendees) {

            short status;
            switch (attendee.getStatus()) {
                case Attendee.DECLINED:
                case Attendee.DELEGATED:
                    status = SIFCalendar.ATTENDEE_STATUS_DECLINED;
                    break;
                case Attendee.NEEDS_ACTION:
                case Attendee.SENT:
                    status = SIFCalendar.ATTENDEE_STATUS_NO_RESPONSE;
                    break;
                case Attendee.TENTATIVE:
                    status = SIFCalendar.ATTENDEE_STATUS_TENTATIVE;
                    break;
                case Attendee.ACCEPTED:
                case Attendee.IN_PROCESS:
                case Attendee.COMPLETED:
                    status = SIFCalendar.ATTENDEE_STATUS_ACCEPTED;
                    break;
                case Attendee.UNKNOWN:
                default:
                    status = SIFCalendar.ATTENDEE_STATUS_UNKNOWN;
            }

            short type;
            if ((attendee.getKind() == Attendee.RESOURCE) ||
                (attendee.getKind() == Attendee.ROOM)) {
                type = SIFCalendar.ATTENDEE_TYPE_RESOURCE;
            } else switch (attendee.getExpected()) {
                case Attendee.NON_PARTICIPANT:
                case Attendee.OPTIONAL:
                    type = SIFCalendar.ATTENDEE_TYPE_OPTIONAL;
                    break;
                case Attendee.REQUIRED:
                case Attendee.REQUIRED_IMMEDIATE:
                case Attendee.CHAIRMAN:
                    type = SIFCalendar.ATTENDEE_TYPE_REQUIRED;
                    break;
                case Attendee.UNKNOWN:
                default:
                    type = SIFCalendar.ATTENDEE_TYPE_UNKNOWN;
            }

            sb.append('<').append(SIFCalendar.ATTENDEE).append('>')
              .append(createTagFromString(SIFCalendar.ATTENDEE_NAME,attendee.getName()))
              .append(createTagFromString(SIFCalendar.ATTENDEE_EMAIL,attendee.getEmail()))
              .append(createTagFromString(SIFCalendar.ATTENDEE_STATUS, String.valueOf(status)))
              .append(createTagFromString(SIFCalendar.ATTENDEE_TYPE, String.valueOf(type)))
              .append("</").append(SIFCalendar.ATTENDEE).append('>');
        }

        sb.append("</").append(SIFCalendar.ATTENDEES).append('>');
        return sb;
    }

    protected StringBuffer createTagForGEOProperty(Property lat, Property lon) {
        StringBuffer output = new StringBuffer();
        String valLat = null, valLon = null;
        String tag = SIFE.GEO;
        if (lat != null && lat.getPropertyValue() != null) {
            if (((String)lat.getPropertyValue()).length()>0) {
                valLat = (String)lat.getPropertyValue();
            }
        }
        if (lon != null && lon.getPropertyValue() != null) {
            if (((String)lon.getPropertyValue()).length()>0) {
                valLon = (String)lon.getPropertyValue();
            }
        }

        if (valLat != null && valLon != null) {
            StringBuffer params = getParams(lat);
            if (params.length() > 0) {
                output.append('<').append(tag).append(params).append('>')
                      .append(valLat).append(';').append(valLon)
                      .append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append('>')
                      .append(valLat).append(';').append(valLon)
                      .append("</").append(tag).append('>');
            }
        } else if (valLat != null && valLon == null) {
            StringBuffer params = getParams(lat);
            if (params.length() > 0) {
                output.append('<').append(tag).append(params).append('>')
                      .append(valLat)
                      .append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append('>')
                      .append(valLat)
                      .append("</").append(tag).append('>');
            }
        } else if (valLat == null && valLon != null) {
            StringBuffer params = getParams(lon);
            if (params.length() > 0) {
                output.append('<').append(tag).append(params).append('>')
                      .append(valLon)
                      .append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append('>')
                      .append(valLon)
                      .append("</").append(tag).append('>');
            }
        } else {
            // do nothing! don't write the tag
        }

        return output;
    }

   /**
     * Returns a string representing the tag with the specified name and the
     * content (value and attributes) fetched from the given Property element
     * and converted into all-day format ("yyyy-MM-dd").
     *
     * @param tag the tag name
     * @param prop the property
     * @return an XML representation of the tag
     */
    protected static StringBuffer createTagFromAllDayProperty(String   tag ,
                                                              Property prop) {

        String propValue = null;
        if (prop != null) {
            propValue = (String)prop.getPropertyValue();
        }

        try {
            propValue =
                    handleConversionToAllDayDate(propValue);
        } catch (ConverterException ce) {
            return new StringBuffer(); // empty
        }

        return createTagFromStringAndProperty(tag, propValue, prop);
    }

    /**
     * Returns a string representing the tag with the specified name and the
     * content (value and attributes) fetched from the given Property element
     *
     * @param tag the tag name
     * @param prop the property
     * @return an XML representation of the tag
     */
    protected StringBuffer createTagFromProperty(String tag, Property prop)
    throws ConverterException {

        String propValue = null;
        if (prop != null) {
            propValue = (String)prop.getPropertyValue();
        }

        if (SIFCalendar.IMPORTANCE.equalsIgnoreCase(tag)) {
            try {
                propValue = String.valueOf(
                    importance19To02(Integer.parseInt(propValue)));
            } catch (NumberFormatException nfe) {
                return new StringBuffer(); // empty
            }
        }

        return createTagFromStringAndProperty(tag, propValue, prop);
    }

    /**
     * Returns a string representing the attributes fetched from the given Property
     * in an XML-compliant form.
     * This function return a string starting with a space.
     *
     * @param prop the property
     * @return an XML representation of the attributes contained in the Property
     */
    protected static StringBuffer getParams(Property prop) {
        StringBuffer output = new StringBuffer(60);

        if (prop.getEncoding()!=null) {
            output.append(" ENCODING=\"").append(prop.getEncoding().toUpperCase()).append("\"");
        }
        if (prop.getLanguage()!=null) {
            output.append(" LANGUAGE=\"").append(prop.getLanguage().toUpperCase()).append("\"");
        }
        if (prop.getCharset()!=null) {
            output.append(" CHARSET=\"").append(prop.getCharset().toUpperCase()).append("\"");
        }
        if (prop.getAltrep()!=null) {
            if (prop.getAltrep().startsWith("\"")) {
                output.append(" ALTREP=").append(prop.getAltrep().toUpperCase());
            } else {
                output.append(" ALTREP=\"").append(prop.getAltrep().toUpperCase()).append("\"");
            }
        }
        if (prop.getCn()!=null) {
            output.append(" CN=\"").append(prop.getCn().toUpperCase()).append("\"");
        }
        if (prop.getCutype()!=null) {
            output.append(" CUTYPE=\"").append(prop.getCutype().toUpperCase()).append("\"");
        }
        if (prop.getDelegatedFrom()!=null) {
            output.append(" DELEGATED-FROM=\"").append(prop.getDelegatedFrom().toUpperCase()).append("\"");
        }
        if (prop.getDelegatedTo()!=null) {
            output.append(" DELEGATED-TO=\"").append(prop.getDelegatedTo().toUpperCase()).append("\"");
        }
        if (prop.getDir()!=null) {
            output.append(" DIR=\"").append(prop.getDir().toUpperCase()).append("\"");
        }
        if (prop.getGroup()!=null) {
            output.append(" GROUP=\"").append(prop.getGroup().toUpperCase()).append("\"");
        }
        if (prop.getMember()!=null) {
            output.append(" MEMBER=\"").append(prop.getMember().toUpperCase()).append("\"");
        }
        if (prop.getPartstat()!=null) {
            output.append(" PARTSTAT=\"").append(prop.getPartstat().toUpperCase()).append("\"");
        }
        if (prop.getRelated()!=null) {
            output.append(" RELATED=\"").append(prop.getRelated().toUpperCase()).append("\"");
        }
        if (prop.getSentby()!=null) {
            if (prop.getSentby().startsWith("\"")) {
                output.append(" SENT-BY=").append(prop.getSentby().toUpperCase()).append("");
            } else {
                output.append(" SENT-BY=\"").append(prop.getSentby().toUpperCase()).append("\"");
            }
        }
        if (prop.getType()!=null) {
            output.append(" TYPE=\"").append(prop.getType().toUpperCase()).append("\"");
        }
        if (prop.getValue()!=null) {
            output.append(" VALUE=\"").append(prop.getValue().toUpperCase()).append("\"");
        }
        if (prop.getXParams() != null) {
            HashMap hm = prop.getXParams();
            Iterator it = hm.keySet().iterator();
            String tag = null, value = null;
            while(it.hasNext()) {
                tag   = (String)it.next();
                value = (String)hm.get(tag);
                //
                // If the value is null, set it as the tag to create a right XML
                //
                if (value == null) {
                    value = tag;
                }
                output.append(' ')
                      .append(tag.toUpperCase())
                      .append("=\"")
                      .append(value.toUpperCase())
                      .append("\"");
            }
        }

        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and value
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    protected static StringBuffer createTagFromString(String tag          ,
                                                      String propertyValue) {
        StringBuffer output = new StringBuffer();
        if (propertyValue != null) {
            if (propertyValue.length() > 0) {
                output.append('<').append(tag).append('>');
                output.append(StringTools.escapeBasicXml(propertyValue));
                output.append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and value
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    protected static StringBuffer createTagFromStringAndProperty(String tag,
        String propValue, Property prop) {

        StringBuffer output = new StringBuffer();

        if (propValue != null) {
            if (propValue.length() > 0) {
                StringBuffer params = getParams(prop);
                output.append('<').append(tag).append(params).append('>')
                .append(StringTools.escapeBasicXml(propValue))
                .append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and value
     * If the value is null, an empty StringBuffer is returned, otherwise the
     * corresponding tag where the content is <i>String.valueOf(propertyValue)</i>.
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    protected static StringBuffer createTagFromObject(String tag          ,
                                                      Object propertyValue) {
        StringBuffer output = new StringBuffer();
        if (propertyValue != null) {
            String stringValue = String.valueOf(propertyValue);
            if (stringValue.length()>0) {
                output.append('<').append(tag).append('>');
                output.append(StringTools.escapeBasicXml(stringValue));
                output.append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }

    /**
     * Returns the SIF representation of a boolean value.
     *
     * @param value boolean
     * @return "0" if the given boolean is false, "1" otherwise
     */
    protected static String booleanToSIFValue(boolean value) {
        if (value) {
            return "1";
        } else {
            return "0";
        }
    }

    /**
     * Returns the SIF representation of a boolean value.
     *
     * @param value boolean
     * @return "0" if the given boolean is false, "1" otherwise
     */
    protected static String booleanToSIFValue(Boolean value) {
        if (value == null) {
            return "0";
        }
        return booleanToSIFValue(value.booleanValue());
    }

    protected StringBuffer createTagsFromExceptionList(List<ExceptionToRecurrenceRule> list)
    throws ConverterException {
        StringBuffer output = new StringBuffer();
        List<String> excludeDates = new ArrayList<String>();
        List<String> includeDates = new ArrayList<String>();
        for (ExceptionToRecurrenceRule etrr : list) {
            if (etrr.isAddition()) {
                includeDates.add(handleConversionToLocalDate(
                        etrr.getDate(), timezone));
             } else {
                excludeDates.add(handleConversionToLocalDate(
                        etrr.getDate(), timezone));
            }
        }

        if (excludeDates.isEmpty()) {
            output.append('<').append(SIFCalendar.RECURRENCE_EXCLUDE_DATE)
                  .append("/>");
        } else {
            for (String excludeDate : excludeDates) {
                output.append('<').append(SIFCalendar.RECURRENCE_EXCLUDE_DATE)
                        .append('>').append(excludeDate)
                        .append("</").append(SIFCalendar.RECURRENCE_EXCLUDE_DATE)
                        .append('>');
            }
        }

         if (includeDates.isEmpty()) {
            output.append('<').append(SIFCalendar.RECURRENCE_INCLUDE_DATE)
                  .append("/>");
        } else {
            for (String includeDate : includeDates) {
                output.append('<').append(SIFCalendar.RECURRENCE_INCLUDE_DATE)
                      .append('>').append(includeDate)
                      .append("</").append(SIFCalendar.RECURRENCE_INCLUDE_DATE)
                      .append('>');
            }
        }

        return output;
    }

    /**
     * Converts the importance to the Outlook-based scale (zero to two, where
     * zero is the lowest priority) from the iCalendar scale (one to nine, where
     * one is the highest priority) according to RFC 2445.
     *
     * @param oneToNine an int in the [1; 9] range
     * @return an int being 0 or 1 or 2
     * @throws NumberFormatException if the argument is out of range
     */
    protected static int importance19To02(int oneToNine) throws NumberFormatException {
        switch (oneToNine) {
            case 1:
            case 2:
            case 3:
            case 4:
                return 2;
            case 5:
                return 1;
            case 6:
            case 7:
            case 8:
            case 9:
                return 0;
            default:
                throw new NumberFormatException(); // will be caught
        }
    }

    private static String getTimeZoneID(PropertyWithTimeZone property) {
        if (property == null) {
            return null;
        }
        return property.getTimeZone();
    }

    /**
     * Shifts the given dayOfWeekMask according to the patternStartDate in UTC and
     * in local.
     * Basically if there is a change in the day converting the utcPatternStartDate
     * in localPatternStartDate (since the hour and the timezone), the dayOfWeekMask
     * is shifted. If there is not change, the mask is not shifted.
     * 
     * i.e.
     * utcPatternStartDate = 20080718T160000Z
     * localPatternStartDate = 20080719T010000 (with timezone Asia/Tokyo)
     * dayOfWeekMask = 32
     * result: 64 (dayOfWeekMask is shifted to left since the "local" day (19 Jul) 
     * is the day  after the UTC one (18 Jul).
     * 
     * @param utcPatterStartDates the patternStartDate in UTC
     * @param localPatternStartDate the patternStartDate in local time
     * @param dayOfWeekMask the 'UTC' dayOfWeekMask
     * @return the dayOfWeekMask shifted according to the shift of the patternStartDate
     * @throws ConverterException if an error occurs parsing dates
     */
    private static short shiftDayOfWeekMask(String utcPatterStartDates, 
                                            String localPatternStartDate,
                                            short dayOfWeekMask) throws ConverterException {

        if (utcPatterStartDates == null || localPatternStartDate == null) {
            return dayOfWeekMask;
        }
        
        int utcDayOfPatternStartDate   = 
            Integer.parseInt(utcPatterStartDates.substring(6,8));
        int localDayOfPatternStartDate = 
            Integer.parseInt(localPatternStartDate.substring(6,8));
        
        if (utcDayOfPatternStartDate == localDayOfPatternStartDate) {
            // the day is the same in UTC and in local...the mask must no be changed
            return dayOfWeekMask;
        }
        
        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        
        Date utcDate   = null;
        Date localDate = null;        
        
        try {        
            dateFormatter.applyPattern(TimeUtils.getDateFormat(utcPatterStartDates));        
            utcDate = dateFormatter.parse(utcPatterStartDates);
        } catch (ParseException ex) {
            throw new ConverterException("Error parsing utcPatterStartDates (" + utcPatterStartDates + ")", ex);
        }
        try {
            dateFormatter.applyPattern(TimeUtils.getDateFormat(localPatternStartDate));
            localDate = dateFormatter.parse(localPatternStartDate);
        } catch (ParseException ex) {
            throw new ConverterException("Error parsing localPatternStartDate (" + localPatternStartDate + ")", ex);
        }

        int comparison =  localDate.compareTo(utcDate);
        
        if (comparison > 0) {
            // localDate is after utcDate ---> shift the mask to left
            dayOfWeekMask = (short) (dayOfWeekMask << 1);
            if (dayOfWeekMask > 127) {
                dayOfWeekMask -= 127;
            }            
        } else if (comparison < 0) {
            // localDate is before utcDate ---> shift the mask to right
            if ((dayOfWeekMask & 1) == 1) {
                dayOfWeekMask += 127;
            }
            dayOfWeekMask = (short) (dayOfWeekMask >> 1);            
        } else {
            // nothing to do, dates are equals
        }
        return dayOfWeekMask;
    }

}
