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

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Date;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import com.funambol.common.pim.calendar.*;
import com.funambol.common.pim.common.*;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * This object is a converter from a Calendar object model to a iCalendar string
 *
 * @see Converter
 * @version $Id: CalendarToIcalendar.java,v 1.3 2007-11-28 11:14:04 nichele Exp $
 *
 * @deprecated Unused since version 6.5, replaced by methods in 
 *             {@link #com.funambol.common.pim.converter.VCalendarConverter}
 */
public class CalendarToIcalendar extends BaseConverter {

    // --------------------------------------------------------------- Constants
    private static final String BEGIN_VCALEN = "BEGIN:VCALENDAR\r\n";
    private static final String BEGIN_VEVENT = "BEGIN:VEVENT\r\n"   ;
    private static final String END_VEVENT   = "END:VEVENT\r\n"     ;
    private static final String END_VCALEN   = "END:VCALENDAR\r\n"  ;

    private Calendar calendar = null;

    // ------------------------------------------------------------- Constructor
    public CalendarToIcalendar(TimeZone timezone, String charset) {
        super(timezone, charset);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Performs the conversion.
     *
     * @param calendar the Calendar to be converted in iCalendar format
     *
     * @return a string containing the iCalendar representation of this Calendar
     */
    public String convert(Object obj) throws ConverterException {

        calendar = (Calendar)obj;

        StringBuffer output = new StringBuffer(BEGIN_VCALEN);

        if (calendar.getCalScale()!=null) {
            output.append(composeFieldCalscale(calendar.getCalScale()));
        }
        if (calendar.getMethod()!=null) {
            output.append(composeFieldMethod(calendar.getMethod()));
        }
        if (calendar.getProdId()!=null) {
            output.append(composeFieldProdId(calendar.getProdId()));
        }
        if (calendar.getVersion()!=null) {
            output.append(composeFieldVersion(calendar.getVersion()));
        }
        // X-PROP
        output.append(composeFieldXTag(calendar.getXTags()));

        //
        // VEVENT
        //
        if (calendar.getEvent()!=null) {
            output.append(BEGIN_VEVENT);

            if (calendar.getEvent().getCategories()!=null){
                output.append(composeFieldCategories(calendar.getEvent().getCategories()));
            }
            if (calendar.getEvent().getAccessClass()!=null){
                output.append(composeFieldClass(calendar.getEvent().getAccessClass()));
            }
            if (calendar.getEvent().getDescription()!=null){
                output.append(composeFieldDescription(calendar.getEvent().getDescription()));
            }
            if (calendar.getEvent().getLatitude() != null ||
                calendar.getEvent().getLongitude()!= null  ){
                output.append(composeFieldGeo(calendar.getEvent().getLatitude(),
                                        calendar.getEvent().getLongitude()
                                       ));
            }
            if (calendar.getEvent().getLocation()!=null){
                output.append(composeFieldLocation(calendar.getEvent().getLocation()));
            }
            if (calendar.getEvent().getPriority()!=null){
                output.append(composeFieldPriority(calendar.getEvent().getPriority()));
            }
            if (calendar.getEvent().getStatus()!=null){
                output.append(composeFieldStatus(calendar.getEvent().getStatus()));
            }
            if (calendar.getEvent().getSummary()!=null){
                output.append(composeFieldSummary(calendar.getEvent().getSummary()));
            }
            if (calendar.getEvent().getDtEnd()!=null){
                output.append(composeFieldDtEnd(
                        calendar.getEvent().getDtEnd()));
            }
            if (calendar.getEvent().getDtStart()!=null){
                output.append(composeFieldDtStart(
                        calendar.getEvent().getDtStart()));
            }

            //
            // NOTE: We decided not to store the duration but only Start and End
            //

            if (calendar.getEvent().getTransp()!=null){
                output.append(composeFieldTransp(calendar.getEvent().getTransp()));
            }
            if (calendar.getEvent().getOrganizer()!=null){
                output.append(composeFieldOrganizer(calendar.getEvent().getOrganizer()));
            }
            if (calendar.getEvent().getUrl()!=null){
                output.append(composeFieldUrl(calendar.getEvent().getUrl()));
            }
            if (calendar.getEvent().getUid()!=null){
                output.append(composeFieldUid(calendar.getEvent().getUid()));
            }
            if (calendar.getEvent().getRecurrencePattern()!=null){
                output.append(composeFieldRrule(calendar.getEvent().getRecurrencePattern()));
            }
            if (calendar.getEvent().getCreated()!=null){
                output.append(composeFieldCreated(
                        calendar.getEvent().getCreated()));
            }
            if (calendar.getEvent().getDtStamp()!=null){
                output.append(composeFieldDtstamp(
                        calendar.getEvent().getDtStamp()));
            }
            if (calendar.getEvent().getLastModified()!=null){
                output.append(composeFieldLastModified(
                    calendar.getEvent().getLastModified()));
            }
            if (calendar.getEvent().getSequence()!=null){
                output.append(composeFieldSequence(calendar.getEvent().getSequence()));
            }
            if (calendar.getEvent().getContact()!=null){
                output.append(this.composeFieldContact(calendar.getEvent().getContact()));
            }
            if (calendar.getEvent().getDAlarm()!=null){
                output.append(composeFieldDAlarm(calendar.getEvent().getDAlarm()));
            }
            if (calendar.getEvent().getPAlarm()!=null){
                output.append(composeFieldPAlarm(calendar.getEvent().getPAlarm()));
            }
            if (calendar.getEvent().getReminder()!=null     &&
                calendar.getEvent().getReminder().isActive()  ){
                output.append(
                    composeFieldReminder(calendar.getEvent().getDtStart(),
                                         calendar.getEvent().getReminder()
                    )
                );
            }

            // X-PROP
            List eventXTag = calendar.getEvent().getXTags();
            output.append(composeFieldXTag(eventXTag));

            output.append(END_VEVENT);
        }
        output.append(END_VCALEN);

        return output.toString();
    }

    /**
     * @return a representation of the iCalendar field X-PROP:
     */
    private StringBuffer composeFieldXTag(List xTags) throws ConverterException {
        StringBuffer output  = new StringBuffer();

        if ((xTags == null) || xTags.isEmpty()) {
            return output;
        }

        Property xtag = null;
        String value  = null;

        int size = xTags.size();
        for (int i=0; i<size; i++) {

            XTag xtagObj = (XTag)xTags.get(i);

            xtag  = xtagObj.getXTag();
            value = (String)xtag.getPropertyValue();

            output.append(
                composeICalTextComponent(xtag, (String)xtagObj.getXTagValue())
                );
        }
        return output;
    }

    /**
     * Added X-Param to the input list of the property parameters
     * The buffer iterates throguh the parameters and adds the
     * start parameter char ';' and then the parameter.
     * Avoids the add the starting ';' by the caller and delete
     * the trailing ';' here.
     *
     * @param paramList the list of standard param
     * @param prop the property object
     *
     */
    private void addXParams(StringBuffer paramList, Property prop) {
        if (prop.getXParams() != null && prop.getXParams().size() > 0) {
            HashMap h = prop.getXParams();
            Iterator it = h.keySet().iterator();
            String tag   = null;
            String value = null;
            while(it.hasNext()) {
                tag   = (String)it.next();
                value = (String)h.get(tag);
                //
                // If tag is the same as value then this tag is handle as
                // param without value
                //
                if (tag.equals(value)) {
                    paramList.append(";").append(tag);
                } else {
                    paramList.append(";")
                             .append(tag)
                             .append("=\"")
                             .append(value)
                             .append("\"");
                }
            }
        }
    }

    /**
     * @return a representation of the iCalendar field CALSCALE:
     */
    private StringBuffer composeFieldCalscale(Property calscale) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (calscale.getPropertyValue()!=null) {
            result.append("CALSCALE");
            addXParams(result, calscale);
            result.append(':')
                  .append(escapeSeparator((String)calscale.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the iCalendar field METHOD:
     */
    private StringBuffer composeFieldMethod(Property method) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (method.getPropertyValue()!=null) {
            result.append("METHOD");
            addXParams(result, method);
            result.append(':')
                  .append(escapeSeparator((String)method.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the iCalendar field PRODID:
     */
    private StringBuffer composeFieldProdId(Property prodid)
    throws ConverterException {
        if (prodid.getPropertyValue() != null) {
            return composeICalTextComponent(prodid, "PRODID");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the iCalendar field VERSION:
     */
    private StringBuffer composeFieldVersion(Property version) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (version.getPropertyValue()!=null) {
            result.append("VERSION");
            addXParams(result, version);
            result.append(":")
                  .append(escapeSeparator((String)version.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field CATEGORIES:
     */
    private StringBuffer composeFieldCategories(Property categories)
    throws ConverterException {
        if (categories.getPropertyValue() != null) {
            return composeICalTextComponent(categories,"CATEGORIES");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field CLASS:
     */
    private StringBuffer composeFieldClass(Property classEvent)
    throws ConverterException {
        if (classEvent.getPropertyValue() != null) {
            return composeICalTextComponent(classEvent, "CLASS");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field DESCRIPTION:
     */
    private StringBuffer composeFieldDescription(Property description)
    throws ConverterException {
        if (description.getPropertyValue() != null) {
            return composeICalTextComponent(description, "DESCRIPTION");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field GEO:
     */
    private StringBuffer composeFieldGeo(Property latitude, Property longitude) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed

        String outLat=null, outLon=null;

        outLat=(String)latitude.getPropertyValue();
        outLon=(String)longitude.getPropertyValue();

        if (outLat!=null && outLon!=null) {
            result.append("GEO");
            addXParams(result, latitude);
            addXParams(result, longitude);
            result.append(":").append(outLat).append(";").append(outLon).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field LOCATION:
     */
    private StringBuffer composeFieldLocation(Property location)
    throws ConverterException {
        if (location.getPropertyValue() != null) {
            return composeICalTextComponent(location, "LOCATION");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the iCalendar field PRIORITY:
     */
    private StringBuffer composeFieldPriority(Property priority) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (priority.getPropertyValue()!=null) {
            result.append("PRIORITY");
            addXParams(result, priority);
            result.append(":").append((String)priority.getPropertyValue()).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field STATUS:
     */
    private StringBuffer composeFieldStatus(Property status) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (status.getPropertyValue()!=null) {
            result.append("STATUS");
            addXParams(result, status);
            result.append(":")
                  .append(escapeSeparator((String)status.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the Event field SUMMARY:
     */
    private StringBuffer composeFieldSummary(Property summary)
    throws ConverterException {
        if (summary.getPropertyValue() != null) {
            return composeICalTextComponent(summary, "SUMMARY");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the Event field DTEND:
     */
    private StringBuffer composeFieldDtEnd(Property dtEnd) throws ConverterException {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (dtEnd.getPropertyValue()!=null) {
            result.append("DTEND");

            String valueParam=dtEnd.getValue();
            if (valueParam!=null) {
                result.append(";VALUE=").append(valueParam);
            }
            addXParams(result, dtEnd);

            String dtEndVal=(String)dtEnd.getPropertyValue();

            if (TimeUtils.isInAllDayFormat(dtEndVal)) {
                try {
                    dtEndVal = TimeUtils.convertDateFromInDayFormat(dtEndVal, "235900");
                } catch (java.text.ParseException e) {
                    throw new ConverterException(e);
                }
            }
            dtEndVal = handleConversionToLocalDate(dtEndVal, timezone);
            result.append(":").append(dtEndVal).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the Event field DTSTART:
     */
    private StringBuffer composeFieldDtStart(Property dtStart) throws ConverterException {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (dtStart.getPropertyValue()!=null) {
            result.append("DTSTART");

            String valueParam=dtStart.getValue();
            if (valueParam!=null) {
                result.append(";VALUE=").append(valueParam);
            }
            addXParams(result, dtStart);

            String dtStartVal=(String)dtStart.getPropertyValue();

            if (TimeUtils.isInAllDayFormat(dtStartVal)) {
                try {
                    dtStartVal = TimeUtils.convertDateFromInDayFormat(dtStartVal, "000000");
                } catch (java.text.ParseException e) {
                    throw new ConverterException(e);
                }
            }
            dtStartVal = handleConversionToLocalDate(dtStartVal, timezone);
            result.append(":").append(dtStartVal).append("\r\n");
        }
        return result;
    }


    /**
     * @return a representation of the event field TRANSP:
     */
    private StringBuffer composeFieldTransp(Property transp)
    throws ConverterException {
        if (transp.getPropertyValue() != null) {
            return composeICalTextComponent(transp, "TRANSP");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field ORGANIZER:
     */
    private StringBuffer composeFieldOrganizer(Property org) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (org.getPropertyValue()!=null) {
            result.append("ORGANIZER");

            //building a properties string
            String cnParam       = org.getCn()      ;
            String dirParam      = org.getDir()     ;
            String sentbyParam   = org.getSentby()  ;
            String languageParam = org.getLanguage();
            if (cnParam!=null) {
                result.append(";CN=").append(cnParam);
            }
            if (dirParam!=null) {
                result.append(";DIR=").append(dirParam);
            }
            if (sentbyParam!=null) {
                result.append(";SENT-BY=\"").append(sentbyParam).append("\"");
            }
            if (languageParam!=null) {
                result.append(";LANGUAGE=").append(languageParam);
            }
            addXParams(result, org);
            result.append(":")
                  .append(escapeSeparator((String)org.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field URL:
     */
    private StringBuffer composeFieldUrl(Property url) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (url.getPropertyValue()!=null) {
            result.append("URL");
            addXParams(result, url);
            result.append(":")
                  .append(escapeSeparator((String)url.getPropertyValue()))
                  .append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field UID:
     */
    private StringBuffer composeFieldUid(Property uid) throws ConverterException {
        if (uid.getPropertyValue() != null) {
            return composeICalTextComponent(uid, "UID");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field RRULE:
     */
    private StringBuffer composeFieldRrule(RecurrencePattern rrule) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (rrule != null) {
            result.append("RRULE");
            addXParams(result, rrule);
            result.append(":");
            result.append(rrule.getTypeDesc()).append(rrule.getInterval());

            if (rrule.getInstance() != 0) {
                //is possible return only positive instance (number+)
                result.append(" " + rrule.getInstance() + "+");
            }
            for (int i=0; i<rrule.getDayOfWeek().size(); i++) {
                result.append(" " + rrule.getDayOfWeek().get(i));
            }
            if (rrule.getDayOfMonth() != 0 && !"YM".equals(rrule.getTypeDesc())) {
                result.append(" " + rrule.getDayOfMonth());
            }
            if (rrule.getMonthOfYear() != 0) {
                result.append(" " + rrule.getMonthOfYear());
            }
            if (rrule.getOccurrences() != -1 && rrule.isNoEndDate()) {
                result.append(" #" + rrule.getOccurrences());
            } else {
                if (rrule.isNoEndDate()) {
                    result.append(" #0"); //forever
                }
            }
            if (!rrule.isNoEndDate()               &&
                 rrule.getEndDatePattern() != null &&
                !rrule.getEndDatePattern().equals("")) {
                result.append(" " + rrule.getEndDatePattern());
            }
            result.append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field CONTACT:
     */
    private StringBuffer composeFieldContact(Property contact)
    throws ConverterException {
        if (contact.getPropertyValue() != null) {
            return composeICalTextComponent(contact, "CONTACT");
        }
        return new StringBuffer(0);
    }

    /**
     * @return a representation of the event field CREATED or DCREATED:
     */
    private StringBuffer composeFieldCreated(Property created)
    throws ConverterException {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (created.getPropertyValue()!=null) {
            result.append(created.getTag());
            addXParams(result, created);

            String createdVal=(String)created.getPropertyValue();
            createdVal = handleConversionToLocalDate(createdVal, timezone);

            result.append(":").append(createdVal).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field DTSTAMP:
     */
    private StringBuffer composeFieldDtstamp(Property dtStamp)
    throws ConverterException {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (dtStamp.getPropertyValue()!=null) {
            result.append("DTSTAMP");
            addXParams(result, dtStamp);

            String dtStampVal=(String)dtStamp.getPropertyValue();
            dtStampVal = handleConversionToLocalDate(dtStampVal, timezone);

            result.append(":").append(dtStampVal).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field LAST-MODIFIED:
     */
    private StringBuffer composeFieldLastModified(Property lastModified)
    throws ConverterException {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (lastModified.getPropertyValue()!=null) {
            result.append("LAST-MODIFIED");
            addXParams(result, lastModified);
            result.append(":");
            String lastModifiedVal=(String)lastModified.getPropertyValue();
            lastModifiedVal = handleConversionToLocalDate(lastModifiedVal, timezone);
            result.append(lastModifiedVal);
            result.append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field SEQUENCE:
     */
    private StringBuffer composeFieldSequence(Property sequence) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (sequence.getPropertyValue()!=null) {
            result.append("SEQUENCE");
            addXParams(result, sequence);
            result.append(":").append((String)sequence.getPropertyValue()).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field DALARM
     */
    private StringBuffer composeFieldDAlarm(Property dalarm) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (dalarm.getPropertyValue()!=null) {
            result.append("DALARM");
            addXParams(result, dalarm);
            result.append(":").append((String)dalarm.getPropertyValue()).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field PALARM
     */
    private StringBuffer composeFieldPAlarm(Property palarm) {
        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        if (palarm.getPropertyValue()!=null) {
            result.append("PALARM");
            addXParams(result, palarm);
            result.append(":").append((String)palarm.getPropertyValue()).append("\r\n");
        }
        return result;
    }

    /**
     * @return a representation of the event field AALARM
     */
    private StringBuffer composeFieldReminder(Property dtStart ,
                                              Reminder reminder)
    throws ConverterException {

        StringBuffer result = new StringBuffer(60); // Estimate 60 is needed
        result.append("AALARM");

        String typeParam = reminder.getType();
        if (typeParam != null) {
            result.append(";TYPE=").append(typeParam);
        }
        String valueParam=reminder.getValue();
        if (valueParam!=null) {
            result.append(";VALUE=").append(valueParam);
        }
        addXParams(result, reminder);

        Date dateStart = null;
        SimpleDateFormat formatter = new SimpleDateFormat();

        String dtStartVal=(String)dtStart.getPropertyValue();
        dtStartVal = handleConversionToLocalDate(dtStartVal, timezone);

        try {
            formatter.applyPattern(TimeUtils.getDateFormat(dtStartVal));
            dateStart = formatter.parse(dtStartVal);
        } catch (ParseException e) {
            //is not possible
        }

        java.util.Calendar calAlarm = java.util.Calendar.getInstance();
        calAlarm.setTime(dateStart);
        calAlarm.add(java.util.Calendar.MINUTE, -reminder.getMinutes());

        Date dtAlarm = calAlarm.getTime();
        formatter.applyPattern("yyyyMMdd'T'HHmmss'Z'");
        String dtAlarmVal = formatter.format(dtAlarm);

        result.append(":").append(dtAlarmVal).append(";");
        if (reminder.getInterval() != 0) {
            result.append(TimeUtils.getIso8601Duration(String.valueOf(reminder.getInterval())));
        }
        result.append(";").append(reminder.getRepeatCount());

        result.append(";");
        if (reminder.getSoundFile() != null) {
            result.append(reminder.getSoundFile());
        }
        result.append("\r\n");

        return result;
    }
}
