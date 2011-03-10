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
package com.funambol.common.pim.calendar;

/**
 * This class is for all common SIF-E and SIF-T constants.
 *
 * @version $Id: SIFCalendar.java,v 1.7 2008-06-04 17:21:33 luigiafassina Exp $
 */
public class SIFCalendar {

    public static final String PRODID                        = "Prodid"             ;
    public static final String VERSION                       = "Version"            ;
    public static final String CALSCALE                      = "Calscale"           ;
    public static final String METHOD                        = "Method"             ;
    public static final String UID                           = "Uid"                ;
    public static final String DTSTAMP                       = "Dtstamp"            ;
    public static final String LAST_MODIFIED                 = "Last-Modified"      ;
    public static final String BODY                          = "Body"               ;
    public static final String CATEGORIES                    = "Categories"         ;
    public static final String COMPANIES                     = "Companies"          ;
    public static final String IMPORTANCE                    = "Importance"         ;
    public static final String MILEAGE                       = "Mileage"            ;
    public static final String SENSITIVITY                   = "Sensitivity"        ;
    public static final String SUBJECT                       = "Subject"            ;
    public static final String BILLING_INFORMATION           = "BillingInformation" ;
    public static final String NO_AGING                      = "NoAging"            ;
    public static final String UN_READ                       = "UnRead"             ;
    public static final String URL                           = "Url"                ;
    public static final String SEQUENCE                      = "Sequence"           ;
    public static final String DALARM                        = "DAlarm"             ;
    public static final String PALARM                        = "PAlarm"             ;
    public static final String GEO                           = "Geo"                ;
    public static final String CONTACT                       = "Contact"            ;
    public static final String BUSY_STATUS                   = "BusyStatus"         ;
    public static final String MEETING_STATUS                = "MeetingStatus"      ;
    public static final String DURATION                      = "Duration"           ;
    public static final String STATUS                        = "Status"             ;
    public static final String LOCATION                      = "Location"           ;
    public static final String FOLDER                        = "Folder"             ;

    // Recurrence properties:
    public static final String RECURRENCE_IS_RECURRING       = "IsRecurring"        ;
    public static final String RECURRENCE_NO_END_DATE        = "NoEndDate"          ;
    public static final String RECURRENCE_TYPE               = "RecurrenceType"     ;
    public static final String RECURRENCE_INTERVAL           = "Interval"           ;
    public static final String RECURRENCE_MONTH_OF_YEAR      = "MonthOfYear"        ;
    public static final String RECURRENCE_DAY_OF_MONTH       = "DayOfMonth"         ;
    public static final String RECURRENCE_DAY_OF_WEEK_MASK   = "DayOfWeekMask"      ;
    public static final String RECURRENCE_INSTANCE           = "Instance"           ;
    public static final String RECURRENCE_START_DATE_PATTERN = "PatternStartDate"   ;
    public static final String RECURRENCE_END_DATE_PATTERN   = "PatternEndDate"     ;
    public static final String RECURRENCE_OCCURRENCES        = "Occurrences"        ;
    public static final String RECURRENCE_EXCEPTIONS         = "Exceptions"         ;
    public static final String RECURRENCE_EXCLUDE_DATE       = "ExcludeDate"        ;
    public static final String RECURRENCE_INCLUDE_DATE       = "IncludeDate"        ;

    // Reminder properties:
    public static final String REMINDER_SET                  = "ReminderSet"        ;
    public static final String REMINDER_SOUND_FILE           = "ReminderSoundFile"  ;
    public static final String REMINDER_OPTIONS              = "ReminderOptions"    ;
    public static final String REMINDER_INTERVAL             = "ReminderInterval"   ;
    public static final String REMINDER_REPEAT_COUNT         = "ReminderRepeatCount";
    public static final String REMINDER_TIME                 = "ReminderTime"       ;
    // No more used:
    public static final String REMINDER_MINUTES_BEFORE_START = "ReminderMinutesBeforeStart";

    // Attendee properties (and related values):
    public static final String ATTENDEES                     = "Attendees"          ;
    public static final String ATTENDEE                      = "Attendee"           ;
    public static final String ATTENDEE_NAME                 = "AttendeeName"       ;
    public static final String ATTENDEE_EMAIL                = "AttendeeEmail"      ;
    public static final String ATTENDEE_STATUS               = "AttendeeStatus"     ;
    public static final short  ATTENDEE_STATUS_UNKNOWN       = 0                    ;
    public static final short  ATTENDEE_STATUS_ACCEPTED      = 1                    ;
    public static final short  ATTENDEE_STATUS_DECLINED      = 2                    ;
    public static final short  ATTENDEE_STATUS_TENTATIVE     = 3                    ;
    public static final short  ATTENDEE_STATUS_NO_RESPONSE   = 4                    ;
    public static final String ATTENDEE_TYPE                 = "AttendeeType"       ;
    public static final short  ATTENDEE_TYPE_UNKNOWN         = 0                    ;
    public static final short  ATTENDEE_TYPE_REQUIRED        = 1                    ;
    public static final short  ATTENDEE_TYPE_OPTIONAL        = 2                    ;
    public static final short  ATTENDEE_TYPE_RESOURCE        = 3                    ;

    // Time zone properties:
    public static final String TIME_ZONE                      = "Timezone"           ;
    public static final String TIME_ZONE_BASIC_OFFSET         = "BasicOffset"        ;
    public static final String TIME_ZONE_DAYLIGHT             = "DayLight"           ;
    public static final String TIME_ZONE_DST_OFFSET           = "DSTOffset"          ;
    public static final String TIME_ZONE_DST_START            = "DSTStart"           ;
    public static final String TIME_ZONE_DST_END              = "DSTEnd"             ;
    public static final String TIME_ZONE_STANDARD_NAME        = "StandardName"       ;
    public static final String TIME_ZONE_DST_NAME             = "DSTName"            ;

    // SIF version:
    public static final String SIF_VERSION                   = "SIFVersion";
    public static final String CURRENT_SIF_VERSION           = "1.1";
}
