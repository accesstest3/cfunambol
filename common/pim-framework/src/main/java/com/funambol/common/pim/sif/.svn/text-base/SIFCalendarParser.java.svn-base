/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.common.pim.sif;

import java.io.*;
import java.text.ParseException;
import java.util.HashMap;
import java.util.ArrayList;

import org.w3c.dom.*;
import org.xml.sax.*;

import com.funambol.common.pim.common.*;
import com.funambol.common.pim.calendar.*;
import com.funambol.common.pim.converter.CachedTimeZoneHelper;
import com.funambol.common.pim.converter.CalendarStatus;
import com.funambol.common.pim.utility.TimeUtils;
import com.funambol.framework.tools.StringTools;

/**
 * This objects represents a list of iCalendar property parameters.
 * The list is based on the informations contained in a list of parser tokens.
 *
 * @version $Id: SIFCalendarParser.java,v 1.7 2008-04-17 17:02:17 mauro Exp $
 */
public class SIFCalendarParser extends XMLParser {

    /**
     * Parse the XML doc the XML parser for a VCard or ICalendar object.
     * @param xmlStream the input stream in XML format
     */
    public SIFCalendarParser(InputStream xmlStream)
    throws SAXException, IOException {
        super(xmlStream);
    }

    /**
     * Parses the XML document containing either SIF-E or SIF-E data.
     *
     * @return a newly-created Calendar object
     */
    public Calendar parse() throws SAXException {

        if (root == null) {
            throw new SAXException("No root tag available");
        }

        Calendar calendar;
        if (root.getTagName().equals(SIFE.ROOT_TAG) ||
            root.getTagName().equals(SIFE.ALTERNATE_ROOT_TAG)) {

            calendar = new Calendar(new Event());
        } else if (root.getTagName().equals(SIFT.ROOT_TAG)) {
            calendar = new Calendar(new Task());
            calendar.getTask().setAllDay(true); // SIF-T are always
                                                // all-day
        } else {
            throw new SAXException("Incorrect root tag (" + root.getTagName()
                    + "), expected " + SIFE.ROOT_TAG
                    + " or " + SIFE.ALTERNATE_ROOT_TAG
                    + " or " + SIFT.ROOT_TAG);
        }

        //
        // Recurrence pattern properties
        //
        boolean isRecurring      = false;
        short   type             = 0    ;
        int     interval         = 0    ;
        short   monthOfYear      = 0    ;
        short   dayOfMonth       = 0    ;
        short   dayOfWeekMask    = 0    ;
        short   instance         = 0    ;
        String  startDatePattern = null ;
        String  endDatePattern   = null ;
        int     occurrences      = -1   ;
        boolean noEndDatePattern = false;
        
        ArrayList<ExceptionToRecurrenceRule> exceptions = new ArrayList();

        //getting child nodes
        NodeList children = root.getChildNodes();
        Node child = null;
        String content  = null;
        String nodeName = null;
        Property prop = null;
        PropertyWithTimeZone propTZ = null;
        Reminder reminder = null;
        String timeZone = null;
        
        boolean reminderMBFset = false; // is ReminderMinutesBeforeStart set?
        
        /*
         * First pass (only time zone)
         */
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);
            
            if (!(children.item(i) instanceof Element)) {
                continue;
            }
            
            if (SIFCalendar.TIME_ZONE.equals(child.getNodeName())) {
                try {
                    CachedTimeZoneHelper helper = 
                            new CachedTimeZoneHelper(child);
                    timeZone = helper.toID();
                } catch (Exception e) {
                    timeZone = null;
                }
                break;
            }
        }

        /*
         * Second pass (all the rest)
         */
        for (int i = 0; i < children.getLength(); i++) {
            child = children.item(i);

            if (!(children.item(i) instanceof Element)) {
                continue;
            }

            prop   = null;
            propTZ = null;

            nodeName = child.getNodeName();

            if (SIFCalendar.PRODID.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.setProdId(prop);
                }
            } else if (SIFCalendar.VERSION.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.setVersion(prop);
                }
            } else if (SIFCalendar.CALSCALE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.setCalScale(prop);
                }
            } else if (SIFCalendar.METHOD.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.setMethod(prop);
                }
            } else if (SIFCalendar.BODY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setDescription(prop);
                }
            } else if (SIFCalendar.DURATION.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setDuration(prop);
                }
            } else if (SIFCalendar.STATUS.equals(nodeName)) {
                String mappedStatus =
                    CalendarStatus.getServerValueFromSifStatus(getNodeContent(child));
                prop = new Property();
                prop.setPropertyValue(mappedStatus);
                calendar.getCalendarContent().setStatus(prop);

            } else if (SIFCalendar.CATEGORIES.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                   calendar.getCalendarContent().setCategories(prop);
                }
            } else if (SIFCalendar.COMPANIES.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setOrganizer(prop);
                }
            } else if (SIFCalendar.IMPORTANCE.equals(nodeName)) {
                String zeroToTwo = getNodeContent(child);
                if (zeroToTwo != null && zeroToTwo.length() != 0) {
                    try {
                        String oneToNine = String.valueOf(
                            importance02To19(Integer.parseInt(zeroToTwo)));
                        prop = new Property();
                        prop.setPropertyValue(oneToNine);
                        prop.setTag(SIFCalendar.IMPORTANCE);
                        calendar.getCalendarContent().setPriority(prop);
                    } catch (NumberFormatException e) {
                    }
                }

            } else if (SIFCalendar.LOCATION.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setLocation(prop);
                }
            } else if (SIFCalendar.SENSITIVITY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setAccessClass(prop);
                }
            } else if (SIFCalendar.SUBJECT.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setSummary(prop);
                }
            } else if (SIFCalendar.URL.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setUrl(prop);
                }
            } else if (SIFCalendar.UID.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setUid(prop);
                }
            } else if (SIFCalendar.DTSTAMP.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setDtStamp(prop);
                }
            } else if (SIFCalendar.LAST_MODIFIED.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setLastModified(prop);
                }
            } else if (SIFCalendar.SEQUENCE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setSequence(prop);
                }
            } else if (SIFCalendar.DALARM.equals(nodeName)) {
                prop=createPropertyWithTZFromTag(child, timeZone);
                if (prop!=null) {
                    calendar.getCalendarContent().setDAlarm(prop);
                }
            } else if (SIFCalendar.PALARM.equals(nodeName)) {
                prop=createPropertyWithTZFromTag(child, timeZone);
                if (prop!=null) {
                    calendar.getCalendarContent().setPAlarm(prop);
                }
            } else if (SIFCalendar.GEO.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setLatitude(prop);
                }
            } else if (SIFCalendar.FOLDER.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                if (prop!=null) {
                    calendar.getCalendarContent().setFolder(prop);
                }
            }
            //
            // Reminder properties
            //
            else if (SIFCalendar.REMINDER_SET.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setActive(sifValueToBoolean(getNodeContent(child)).booleanValue());
            } else if (SIFCalendar.REMINDER_MINUTES_BEFORE_START.equals(nodeName)) {
                reminderMBFset = true;
                reminder = getReminder(reminder);
                reminder.setMinutes(getIntNodeContent(child));
            } else if (SIFCalendar.REMINDER_TIME.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setTime(getNodeContent(child));
            } else if (SIFCalendar.REMINDER_OPTIONS.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setOptions(getIntNodeContent(child));
            } else if (SIFCalendar.REMINDER_SOUND_FILE.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setSoundFile(getNodeContent(child));
            } else if (SIFCalendar.REMINDER_INTERVAL.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setInterval(getIntNodeContent(child));
            } else if (SIFCalendar.REMINDER_REPEAT_COUNT.equals(nodeName)) {
                reminder = getReminder(reminder);
                reminder.setRepeatCount(getIntNodeContent(child));
            }

            //
            //status
            //
            else if (SIFCalendar.BUSY_STATUS.equals(nodeName)) {
                calendar.getCalendarContent().setBusyStatus(new Short(getShortNodeContent(child)));
            } else if (SIFCalendar.MEETING_STATUS.equals(nodeName)) {
                calendar.getCalendarContent().setMeetingStatus(new Short(getShortNodeContent(child)));
            }
            
            //
            // Let's store recurrence elements for later use
            //
            else if (SIFCalendar.RECURRENCE_IS_RECURRING.equals(nodeName)) {
                isRecurring = sifValueToBoolean(getNodeContent(child)).booleanValue();
            } else if (SIFCalendar.RECURRENCE_TYPE.equals(nodeName)) {
                type = getShortNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_INTERVAL.equals(nodeName)) {
                interval = getIntNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_MONTH_OF_YEAR.equals(nodeName)) {
                monthOfYear = getShortNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_DAY_OF_MONTH.equals(nodeName)) {
                dayOfMonth = getShortNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_DAY_OF_WEEK_MASK.equals(nodeName)) {
                dayOfWeekMask = getShortNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_INSTANCE.equals(nodeName)) {
                instance = getShortNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_START_DATE_PATTERN.equals(nodeName)) {
                startDatePattern = getNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_END_DATE_PATTERN.equals(nodeName)) {
                endDatePattern = getNodeContent(child);
            } else if (SIFCalendar.RECURRENCE_NO_END_DATE.equals(nodeName)) {
                noEndDatePattern = sifValueToBoolean(getNodeContent(child)).booleanValue();
            } else if (SIFCalendar.RECURRENCE_EXCEPTIONS.equals(nodeName)) {
                NodeList grandchildren = child.getChildNodes();
                exceptions.ensureCapacity(grandchildren.getLength());
                for (int j = 0; j < grandchildren.getLength(); j++) {
                   Node grandchild = grandchildren.item(j);
                   String subnodeName = grandchild.getNodeName();
                   String date = getNodeContent(grandchild);
                   if (SIFCalendar.RECURRENCE_EXCLUDE_DATE.equals(subnodeName)) {
                       if (date.length() > 0) {
                           try {
                            exceptions.add(
                                       new ExceptionToRecurrenceRule(false, date));
                           } catch (ParseException e) {
                               // Skips this one
                           }
                       }
                   } else if (SIFCalendar.RECURRENCE_INCLUDE_DATE.equals(subnodeName)) {
                       if (date.length() > 0) {
                           try {
                            exceptions.add(
                                       new ExceptionToRecurrenceRule(true, date));
                           } catch (ParseException e) {
                               // Skips this one
                           }
                       }
                   }
                }
            } else if (SIFCalendar.ATTENDEES.equals(nodeName)) {
                NodeList grandchildren = child.getChildNodes();
                for (int j = 0; j < grandchildren.getLength(); j++) {
                   Node grandchild = grandchildren.item(j);
                   String subnodeName = grandchild.getNodeName();
                   if (SIFCalendar.ATTENDEE.equals(subnodeName)) {
                       calendar.getCalendarContent().addAttendee(
                               getAttendee(grandchild));
                   }
                }
            }

            // Event-only
            else if (calendar.getCalendarContent() instanceof Event) {
                if (SIFE.ALL_DAY_EVENT.equals(nodeName)) {
                    calendar.getEvent().setAllDay(sifValueToBoolean(getNodeContent(child)));
                } else if (SIFE.START.equals(nodeName)) {
                    propTZ = createPropertyWithTZFromTag(child, timeZone);
                    if (propTZ != null) {
                        calendar.getEvent().setDtStart(propTZ);
                    }
                } else if (SIFE.END.equals(nodeName)) {
                    propTZ = createPropertyWithTZFromTag(child, timeZone);
                    if (propTZ!=null) {
                        calendar.getEvent().setDtEnd(propTZ);
                    }
                } else if (SIFE.REPLY_TIME.equals(nodeName)) {
                    prop=createPropertyWithTZFromTag(child, timeZone);
                    if (prop!=null) {
                        calendar.getEvent().setReplyTime(prop);
                    }
                } else if (SIFE.TRANSP.equals(nodeName)) {
                    prop=createPropertyFromTag(child);
                    if (prop!=null) {
                        calendar.getEvent().setTransp(prop);
                    }
                } else if (SIFCalendar.RECURRENCE_OCCURRENCES.equals(nodeName)) {
                    // This is to be done only for events, because in the tasks
                    // the pattern start date can differ from the due date and
                    // using occurrences could affect interoperability.
                    occurrences = getIntNodeContent(child);
                } 
            }

            // Task-only
            else if (calendar.getCalendarContent() instanceof Task) {
                boolean percentCompleteIsPresent = false;
                boolean completeIsPresent = false;
                if (SIFT.DATE_COMPLETED.equals(nodeName)) {
                    prop=createPropertyWithTZFromTag(child, timeZone);
                    if (prop!=null) {
                        calendar.getTask().setDateCompleted(prop);
                    }
                } else if (SIFT.START_DATE.equals(nodeName)) {
                    prop=createPropertyWithTZFromTag(child, timeZone);
                    if (prop!=null) {
                        calendar.getTask().setDtStart(prop);
                    }
                } else if (SIFT.DUE_DATE.equals(nodeName)) {
                    prop=createPropertyWithTZFromTag(child, timeZone);
                    if (prop!=null) {
                        calendar.getTask().setDueDate(prop); // same as dtEnd
                    }
                } else if (SIFCalendar.BILLING_INFORMATION.equals(nodeName)) {
                    prop=createPropertyFromTag(child);
                    if (prop!=null) {
                        calendar.getTask().setBillingInformation(prop);
                    }
                } else if (SIFT.OWNER.equals(nodeName)) {
                    prop=createPropertyFromTag(child);
                    if (prop!=null) {
                        calendar.getTask().setOwner(prop);
                    }
                } else if (SIFT.PERCENT_COMPLETE.equals(nodeName)) {
                    prop=createPropertyFromTag(child);
                    if (prop!=null) {
                        calendar.getTask().setPercentComplete(prop);
                    }
                    percentCompleteIsPresent = true;
                } else if (SIFT.COMPLETE.equals(nodeName)) {
                    prop=createPropertyFromTag(child);
                    if (prop!=null) {
                        calendar.getTask().setComplete(prop);
                    }
                    completeIsPresent = true;
                } else if  (SIFT.TEAM_TASK.equals(nodeName)) {
                    prop = createPropertyFromTag(child);
                    if (prop != null) {
                        calendar.getTask().setTeamTask(prop);
                    }
                } else if (SIFT.TOTAL_WORK.equals(nodeName)) {
                    prop = createPropertyFromTag(child);
                    if (prop != null) {
                        calendar.getTask().setTotalWork(prop);
                    }
                } else if (SIFT.ACTUAL_WORK.equals(nodeName)) {
                    prop = createPropertyFromTag(child);
                    if (prop != null) {
                        calendar.getTask().setActualWork(prop);
                    }
                } else if (SIFCalendar.MILEAGE.equals(nodeName)) {
                    String mileage = getNodeContent(child);
                    if (mileage != null && mileage.length() != 0) {
                        try {
                            calendar.getTask().setMileage(Integer.valueOf(mileage));
                        } catch (NumberFormatException nfe) {
                        // Go on
                        }
                    }
                }
                
                if (completeIsPresent && !percentCompleteIsPresent) {
                    if ("1".equals(calendar.getTask()
                                           .getComplete()
                                           .getPropertyValueAsString())) {
                        calendar.getTask()
                                .setPercentComplete(new Property("100"));
                    }
                } else if (!completeIsPresent && percentCompleteIsPresent) {
                    if ("100".equals(calendar.getTask()
                                             .getPercentComplete()
                                             .getPropertyValueAsString())) {
                        calendar.getTask().setComplete(new Property("1"));
                    } else {
                        calendar.getTask().setComplete(new Property("0"));
                    }
                }
            }

            //Element X-TAG
            //
            // Element Calendar or Event: they are the X- properties. A priori I don't know if they are
            // referred to Calendar Object or Event Object. Search into HashMap if there is key "cal" or "evt"
            // If there is, delete it and build the property object
            //
            else if (child.getNodeName().startsWith("X-")&&(child.hasChildNodes())) {
                prop = createPropertyFromTag(child);
                XTag newXTag = new XTag();

                if (prop != null)  {
                    newXTag.setXTagValue(child.getNodeName());

                    newXTag.getXTag().setPropertyValue(getNodeContent(child));

                    if (prop.getXParams().size() > 0)
                    newXTag.getXTag().setXParams(prop.getXParams());

                    if (prop.getXParams().get("cal") != null &&
                        ((String)(prop.getXParams()).get("cal")).equals("cal")) {
                            prop.getXParams().remove("cal");
                            newXTag.getXTag().setXParams(prop.getXParams());
                            calendar.addXTag(newXTag);

                    }
                    else if (prop.getXParams().get("evt") != null
                        && ((String)(prop.getXParams()).get("evt")).equals("evt")) {
                            prop.getXParams().remove("evt");
                            newXTag.getXTag().setXParams(prop.getXParams());
                            calendar.getCalendarContent().addXTag(newXTag);
                    }
                }
            }
        } // for

        if (reminder != null) {
            reminder.setTimeZone(timeZone);
            if (!reminderMBFset // it's not been explicitly set
                    && reminder.isActive()) { // otherwise, who cares?
                if (calendar.getCalendarContent().getDtStart() != null) {
                    reminder.setMinutes(
                            TimeUtils.getAlarmMinutes(
                                    calendar.getCalendarContent().getDtStart()
                                            .getPropertyValueAsString(),
                                    reminder.getTime(),
                                    null // unused logger
                                    )
                            );
                } else {
                    reminder.setMinutes(0);
                }
            }
            calendar.getCalendarContent().setReminder(reminder);
        }

        if (isRecurring) {
            try {
                calendar.getCalendarContent().setRecurrencePattern(
                    getRecurrencePattern(type             ,
                                         interval         ,
                                         monthOfYear      ,
                                         dayOfMonth       ,
                                         dayOfWeekMask    ,
                                         instance         ,
                                         startDatePattern ,
                                         endDatePattern   ,
                                         occurrences      ,
                                         noEndDatePattern ,
                                         timeZone         )
                );
            } catch (RecurrencePatternException e) {
                throw new SAXException(e.getMessage());
            }
            if (exceptions.size() > 0) {
                calendar.getCalendarContent().getRecurrencePattern()
                        .setExceptions(exceptions);
            }
        } else {
            calendar.getCalendarContent().setRecurrencePattern(null);
        }

        return (calendar);
    }

   /**
    * create a Property object from a XML node
    */
    private Property createPropertyFromTag(Node child) {
        Property prop=new Property();
        prop.setPropertyValue(getNodeContent(child));
        prop.setTag(child.getNodeName());
        HashMap hash = new HashMap();
        //setting attributes
        NamedNodeMap attributes= child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().startsWith("X-")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("cal")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
                if (attribute.getNodeName().equals("evt")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
        if (hash.size() > 0) {
           prop.setXParams(hash);
        }
        return (prop);
    }
    
    /**
     * Creates a PropertyWithTimeZone object from a XML node.
     */
    private PropertyWithTimeZone createPropertyWithTZFromTag(Node child     , 
                                                             String timeZone) {
        PropertyWithTimeZone prop=new PropertyWithTimeZone();
        prop.setPropertyValue(getNodeContent(child));
        prop.setTag(child.getNodeName());
        prop.setTimeZone(timeZone);
        return (prop);
    }

    /**
     * Creates and returns a RecurrencePattern object according to the given
     * parameters.
     *
     * @param elements the SIF-E document elemetns
     * @param calendar the Calendar object fed so far
     *
     * @throws RecurrencePatternException
     */
    private RecurrencePattern getRecurrencePattern( short   type             ,
                                                    int     interval         ,
                                                    short   monthOfYear      ,
                                                    short   dayOfMonth       ,
                                                    short   dayOfWeekMask    ,
                                                    short   instance         ,
                                                    String  startDatePattern ,
                                                    String  endDatePattern   ,
                                                    int     occurrences      ,
                                                    boolean noEndDate        ,
                                                    String  timeZone         )
    throws RecurrencePatternException {

        RecurrencePattern pattern = null;
        switch (type) {
            case RecurrencePattern.TYPE_DAILY:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getDailyRecurrencePattern(interval, startDatePattern, dayOfWeekMask);
                } else {
                    pattern = RecurrencePattern.getDailyRecurrencePattern(interval, startDatePattern, endDatePattern, noEndDate, occurrences, dayOfWeekMask);
                }
                break;
            case RecurrencePattern.TYPE_WEEKLY:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getWeeklyRecurrencePattern(interval, dayOfWeekMask, startDatePattern);
                } else {
                    pattern = RecurrencePattern.getWeeklyRecurrencePattern(interval, dayOfWeekMask, startDatePattern, endDatePattern, noEndDate, occurrences);
                }
                break;
            case RecurrencePattern.TYPE_MONTHLY:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getMonthlyRecurrencePattern(interval, dayOfMonth, startDatePattern);
                } else {
                    pattern = RecurrencePattern.getMonthlyRecurrencePattern(interval, dayOfMonth, startDatePattern, endDatePattern, noEndDate, occurrences);
                }
                break;
            case RecurrencePattern.TYPE_MONTH_NTH:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getMonthNthRecurrencePattern(interval, dayOfWeekMask, instance, startDatePattern);
                } else {
                    pattern = RecurrencePattern.getMonthNthRecurrencePattern(interval, dayOfWeekMask, instance, startDatePattern, endDatePattern, noEndDate, occurrences);
                }
                break;
            case RecurrencePattern.TYPE_YEARLY:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getYearlyRecurrencePattern(interval, dayOfMonth, monthOfYear, startDatePattern);
                } else {
                    pattern = RecurrencePattern.getYearlyRecurrencePattern(interval, dayOfMonth, monthOfYear, startDatePattern, endDatePattern, noEndDate, occurrences);
                }
                break;
            case RecurrencePattern.TYPE_YEAR_NTH:
                if (StringTools.isEmpty(endDatePattern) && occurrences < 1) {
                    pattern = RecurrencePattern.getYearNthRecurrencePattern(interval, dayOfWeekMask, monthOfYear, instance, startDatePattern);
                } else {
                    pattern = RecurrencePattern.getYearNthRecurrencePattern(interval, dayOfWeekMask, monthOfYear, instance, startDatePattern, endDatePattern, noEndDate, occurrences);
                }
                break;

            default:
                throw new RecurrencePatternException("Unknown recurrence type :" + type);
        }

        if (pattern != null) {
            pattern.setNoEndDate(noEndDate);
            pattern.setTimeZone(timeZone);
        }

        return pattern;
    }

    /**
     * If the given Reminder is null, a new instance is created and returned,
     * otherwise the same instance is returned
     *
     * @param r the Reminder instance (can be null)
     *
     */
    private Reminder getReminder(Reminder r) {
        return (r == null) ? new Reminder() : r;
    }

    /**
     * If the given string is "1" returns true, otherwise returns false
     * @param value String
     * @return Boolean
     */
    private Boolean sifValueToBoolean(String value) {
        if (value != null && value.equals("1")) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    
    /**
     * Converts the importance in the Outlook-based scale (zero to two, where
     * zero is the lowest priority) to the iCalendar scale (one to nine, where
     * one is the highest priority) according to RFC 2445.
     *
     * @param zeroToTwo an int being 0 or 1 or 2
     * @return an int in the [1; 9] range
     * @throws NumberFormatException if the argument is out of range
     */
    private int importance02To19(int zeroToTwo) throws NumberFormatException {
        switch (zeroToTwo) {
            case 0:
                return 9;
            case 1:
                return 5;
            case 2:
                return 1;
            default:
                throw new NumberFormatException(); // will be caught
        }
    }
    
    private Attendee getAttendee(Node node) {
        
        Attendee attendee = new Attendee();
        attendee.setRole(Attendee.ATTENDEE);
        
        NodeList attendeeProperties = node.getChildNodes();
        for (int i = 0; i < attendeeProperties.getLength(); i++) {
            Node attendeeProperty = attendeeProperties.item(i);
            String tag = attendeeProperty.getNodeName();
            String value = getNodeContent(attendeeProperty);
            
            if (SIFCalendar.ATTENDEE_NAME.equals(tag)) {
                attendee.setName(value);
            } else if (SIFCalendar.ATTENDEE_EMAIL.equals(tag)) {
                attendee.setEmail(value);
            } else if (SIFCalendar.ATTENDEE_STATUS.equals(tag)) {
                if(value==null || "".equals(value)){
                    attendee.setStatus(Attendee.UNKNOWN);
                } else {
                    try {
                        short status = Short.parseShort(value);
                        switch (status) {
                            case SIFCalendar.ATTENDEE_STATUS_ACCEPTED:
                                attendee.setStatus(Attendee.ACCEPTED);
                                break;
                            case SIFCalendar.ATTENDEE_STATUS_DECLINED:
                                attendee.setStatus(Attendee.DECLINED);
                                break;
                            case SIFCalendar.ATTENDEE_STATUS_TENTATIVE:
                                attendee.setStatus(Attendee.TENTATIVE);
                                break;
                            case SIFCalendar.ATTENDEE_STATUS_NO_RESPONSE:
                                attendee.setStatus(Attendee.NEEDS_ACTION);
                                break;
                            case SIFCalendar.ATTENDEE_STATUS_UNKNOWN:
                            default:
                                attendee.setStatus(Attendee.UNKNOWN);
                        }
                    } catch (NumberFormatException nfe){
                        attendee.setStatus(Attendee.UNKNOWN);
                    }     
                }
            } else if (SIFCalendar.ATTENDEE_TYPE.equals(tag)) {
                if(value==null || "".equals(value)){
                    attendee.setExpected(Attendee.UNKNOWN);
                    attendee.setKind(Attendee.UNKNOWN);
                } else {
                    try {
                        short type = Short.parseShort(value);
                        switch (type) {
                            case SIFCalendar.ATTENDEE_TYPE_REQUIRED:
                                attendee.setExpected(Attendee.REQUIRED);
                                attendee.setKind(Attendee.INDIVIDUAL);                        
                                break;
                            case SIFCalendar.ATTENDEE_TYPE_OPTIONAL:
                                attendee.setExpected(Attendee.OPTIONAL);
                                attendee.setKind(Attendee.INDIVIDUAL);                        
                                break;
                            case SIFCalendar.ATTENDEE_TYPE_RESOURCE:
                                attendee.setExpected(Attendee.REQUIRED);
                                attendee.setKind(Attendee.RESOURCE);
                                break;
                            case SIFCalendar.ATTENDEE_TYPE_UNKNOWN:
                            default:
                                attendee.setExpected(Attendee.UNKNOWN);
                                attendee.setKind(Attendee.UNKNOWN);
                        }
                        
                    } catch (NumberFormatException nfe){
                      attendee.setExpected(Attendee.UNKNOWN);
                        attendee.setKind(Attendee.UNKNOWN);
                    }
                }    
                
            }
        }
    
        return attendee;
    }

}
