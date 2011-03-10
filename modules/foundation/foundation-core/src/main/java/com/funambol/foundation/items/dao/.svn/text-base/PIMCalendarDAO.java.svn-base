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
package com.funambol.foundation.items.dao;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.server.store.NotFoundException;
import com.funambol.framework.tools.DBTools;

import com.funambol.server.config.Configuration;

import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.utility.TimeUtils;
import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.calendar.ExceptionToRecurrenceRule;
import com.funambol.common.pim.calendar.RecurrencePattern;
import com.funambol.common.pim.calendar.Reminder;
import com.funambol.common.pim.calendar.Task;

import com.funambol.common.pim.common.Property;
import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.CalendarWrapper;
import com.funambol.foundation.util.Def;

/*
 * This class implements methods to access contacts data
 * in domino server datastore
 *
 * @version $Id: PIMCalendarDAO.java,v 1.1.1.1 2008-03-20 21:38:36 stefano_fornari Exp $
 **/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.SortedSet;
import java.util.TreeSet;

public class PIMCalendarDAO extends EntityDAO {

    // --------------------------------------------------------------- Constants

    private static final int CALENDAR_BOTH_TYPES = 0;
    private static final int CALENDAR_EVENT_TYPE = 1;
    private static final int CALENDAR_TASK_TYPE = 2;
    private static final String[] SQL_FILTER_BY_TYPE = {
        "",                                          // any CalendarContent
        "AND type = '" + CALENDAR_EVENT_TYPE + "' ", // Event
        "AND type = '" + CALENDAR_TASK_TYPE + "' "   // Task
    };

    private static final String SQL_PIM_CALENDAR_TABLE = "fnbl_pim_calendar";
    private static final String SQL_PIM_CALENDAR_EXCEPTION_TABLE =
            "fnbl_pim_calendar_exception";
    private static final String SQL_ORDER_BY_ID = "ORDER BY id";
    private static final String SQL_GET_FNBL_PIM_CALENDAR_ID_LIST =
            "SELECT id FROM " + SQL_PIM_CALENDAR_TABLE + " ";
    private static final String SQL_GET_FNBL_PIM_CALENDAR_ID_LIST_BY_USER =
            "SELECT id, dend FROM " + SQL_PIM_CALENDAR_TABLE
            + " WHERE userid = ? "
            + "AND status <> 'D' ";
    private static final String
            SQL_GET_FNBL_PIM_CALENDAR_ID_LIST_BY_USER_TIME_STATUS =
            SQL_GET_FNBL_PIM_CALENDAR_ID_LIST
            + "WHERE "
            + "userid = ? "
            + "AND last_update > ? "
            + "AND last_update < ? "
            + "AND status = ? ";
    private static final String SQL_AND_NO_SUBJECT_IS_SET =
            "AND ((subject IS null) OR (subject = '')) ";
    private static final String SQL_AND_SUBJECT_EQUALS_QUESTIONMARK =
            "AND LOWER(subject) = ? ";
    private static final String SQL_AND_NO_DSTART_IS_SET =
            "AND dstart IS null ";
    private static final String SQL_AND_DSTART_EQUALS_QUESTIONMARK =
            "AND dstart = ? ";
    private static final String SQL_AND_NO_DEND_IS_SET =
            "AND dend IS null ";
    private static final String SQL_AND_DEND_EQUALS_QUESTIONMARK =
            "AND dend = ? ";
    private static final String SQL_AND_DEND_IN_INTERVAL =
            "AND dend >= ? AND dend <= ? ";
    private static final String SQL_AND_DSTART_IN_INTERVAL =
            "AND dstart >= ? AND dstart <= ? ";
    private static final String SQL_GET_STATUS_BY_ID_USER_TIME =
            "SELECT status FROM " + SQL_PIM_CALENDAR_TABLE
            + " WHERE id = ? AND userid = ? AND last_update > ? ";
    private static final String SQL_GET_FNBL_PIM_CALENDAR =
            "SELECT id, userid, last_update, status, all_day, body, busy_status, " +
            "categories, companies, duration, dend, importance, " +
            "location, meeting_status, mileage, reminder_time," +
            "reminder_repeat_count, reminder, " +
            "reminder_sound_file, reminder_options, reply_time, sensitivity, " +
            "dstart, subject, rec_type, rec_interval, rec_month_of_year, " +
            "rec_day_of_month, rec_day_of_week_mask, rec_instance, rec_start_date_pattern, " +
            "rec_no_end_date, rec_end_date_pattern, rec_occurrences, type, " +
            "completed, percent_complete, folder, " +
            "dstart_tz, dend_tz, reminder_tz " +
            "FROM " + SQL_PIM_CALENDAR_TABLE + " ";
    private static final String SQL_CLAUSE_GET_CALENDARS_BY_ID_USERID =
            "WHERE id = ? AND userid = ? ";
    private static final String
            SQL_GET_FNBL_PIM_CALENDAR_BY_ID_USERID =
            SQL_GET_FNBL_PIM_CALENDAR +
            SQL_CLAUSE_GET_CALENDARS_BY_ID_USERID;
    private static final String
            SQL_GET_FNBL_PIM_CALENDAR_EXCEPTION_BY_CALENDAR =
            "SELECT calendar, addition, occurrence_date FROM "
            + SQL_PIM_CALENDAR_EXCEPTION_TABLE + " WHERE calendar = ? ";
    private static final String SQL_INSERT_INTO_FNBL_PIM_CALENDAR =
            "INSERT INTO " + SQL_PIM_CALENDAR_TABLE + " " +
            "(id, userid, last_update, status, all_day, body, busy_status, " +
            "categories, companies, duration, dend, importance, location, " +
            "meeting_status, mileage, reminder_time, reminder_repeat_count, " +
            "reminder, reminder_sound_file, reminder_options, reply_time, " +
            "sensitivity, dstart, subject, rec_type, rec_interval, " +
            "rec_month_of_year, rec_day_of_month, rec_day_of_week_mask, " +
            "rec_instance, rec_start_date_pattern, rec_no_end_date, " +
            "rec_end_date_pattern, rec_occurrences, type, completed, " +
            "percent_complete, folder, " +
            "dstart_tz, dend_tz, reminder_tz) " +
            "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String SQL_INSERT_INTO_FNBL_PIM_CALENDAR_EXCEPTION =
            "INSERT INTO " + SQL_PIM_CALENDAR_EXCEPTION_TABLE + " (calendar, " +
            "addition, occurrence_date) " +
            "VALUES(?, ?, ?)";
    private static final String SQL_DELETE_CALENDAR_BY_ID_USERID =
            "UPDATE " + SQL_PIM_CALENDAR_TABLE + " SET status = 'D', " +
            "last_update = ? WHERE id = ? AND userid = ? ";
    private static final String SQL_DELETE_CALENDARS_BY_USERID =
             "UPDATE " + SQL_PIM_CALENDAR_TABLE + " SET status = 'D', " +
             "last_update = ? WHERE status <> 'D' AND userid = ? ";
     private static final String SQL_DELETE_CALENDAR_EXCEPTIONS_BY_CALENDAR =
             "DELETE FROM " + SQL_PIM_CALENDAR_EXCEPTION_TABLE
             + " WHERE calendar = ? ";
    private static final String SQL_UPDATE_FNBL_PIM_CALENDAR_BEGIN =
            "UPDATE " + SQL_PIM_CALENDAR_TABLE + " SET ";
    private static final String SQL_UPDATE_FNBL_PIM_CALENDAR_END =
            " WHERE id = ? AND userid = ? ";
    private static final String SQL_GET_CHANGED_CALENDARS_BY_USER_AND_LAST_UPDATE =
            "select id,status from fnbl_pim_calendar where userid=? and last_update>? and last_update<? ";

    private static final String SQL_EQUALS_QUESTIONMARK       = " = ?"     ;
    private static final String SQL_EQUALS_QUESTIONMARK_COMMA = " = ?, "   ;
    private static final String SQL_EQUALS_NULL_COMMA         = " = null, ";
    private static final String SQL_EQUALS_ONE_COMMA          = " = '1', " ;
    private static final String SQL_EQUALS_ZERO_COMMA         = " = '0', " ;
    private static final String SQL_EQUALS_EMPTY_COMMA        = " = '', "  ;
    private static final String SQL_PERCENT_COMPLETE_FORMULA  =
            " = MOD(percent_complete, 100), ";

    protected static final String SQL_FIELD_ID = "id";
    protected static final String SQL_FIELD_USERID = "userid";
    protected static final String SQL_FIELD_LAST_UPDATE = "last_update";
    protected static final String SQL_FIELD_STATUS = "status";
    protected static final String SQL_FIELD_ALL_DAY = "all_day";
    protected static final String SQL_FIELD_BODY = "body";
    protected static final String SQL_FIELD_BUSY_STATUS = "busy_status";
    protected static final String SQL_FIELD_CATEGORIES = "categories";
    protected static final String SQL_FIELD_COMPANIES = "companies";
    protected static final String SQL_FIELD_DURATION = "duration";
    protected static final String SQL_FIELD_DATE_END = "dend";
    protected static final String SQL_FIELD_LOCATION = "location";
    protected static final String SQL_FIELD_MEETING_STATUS = "meeting_status";
    protected static final String SQL_FIELD_MILEAGE = "mileage";
    protected static final String SQL_FIELD_REMINDER_TIME = "reminder_time";
    protected static final String SQL_FIELD_REMINDER = "reminder";
    protected static final String SQL_FIELD_REMINDER_REPEAT_COUNT
            = "reminder_repeat_count";
    protected static final String SQL_FIELD_REMINDER_SOUND_FILE
            = "reminder_sound_file";
    protected static final String SQL_FIELD_REMINDER_OPTIONS =
            "reminder_options";
    protected static final String SQL_FIELD_REPLY_TIME = "reply_time";
    protected static final String SQL_FIELD_SENSITIVITY = "sensitivity";
    protected static final String SQL_FIELD_DATE_START = "dstart";
    protected static final String SQL_FIELD_SUBJECT = "subject";
    protected static final String SQL_FIELD_RECURRENCE_TYPE = "rec_type";
    protected static final String SQL_FIELD_INTERVAL = "rec_interval";
    protected static final String SQL_FIELD_MONTH_OF_YEAR = "rec_month_of_year";
    protected static final String SQL_FIELD_DAY_OF_MONTH = "rec_day_of_month";
    protected static final String SQL_FIELD_DAY_OF_WEEK_MASK =
            "rec_day_of_week_mask";
    protected static final String SQL_FIELD_INSTANCE = "rec_instance";
    protected static final String SQL_FIELD_IMPORTANCE = "importance";
    protected static final String SQL_FIELD_START_DATE_PATTERN
            = "rec_start_date_pattern";
    protected static final String SQL_FIELD_NO_END_DATE = "rec_no_end_date";
    protected static final String SQL_FIELD_END_DATE_PATTERN =
            "rec_end_date_pattern";
    protected static final String SQL_FIELD_OCCURRENCES = "rec_occurrences";
    protected static final String SQL_FIELD_TYPE = "type";
    protected static final String SQL_FIELD_COMPLETED = "completed";
    protected static final String SQL_FIELD_PERCENT_COMPLETE = "percent_complete";
    protected static final String SQL_FIELD_ADDITION = "addition";
    protected static final String SQL_FIELD_OCCURRENCE_DATE = "occurrence_date";
    protected static final String SQL_FIELD_FOLDER = "folder";
    protected static final String SQL_FIELD_START_DATE_TIME_ZONE = "dstart_tz";
    protected static final String SQL_FIELD_END_DATE_TIME_ZONE = "dend_tz";
    protected static final String SQL_FIELD_REMINDER_TIME_ZONE = "reminder_tz";

    protected static final String NAMESPACE_KEY = "calendar";
    protected static final String COMPANY_ID = "funambol.com";

    protected static final int SQL_BODY_DIM = 4096;
    protected static final int SQL_CATEGORIES_DIM = 255;
    protected static final int SQL_COMPANIES_DIM = 255;
    protected static final int SQL_DAYOFWEEKMASK_DIM = 16;
    protected static final int SQL_ENDDATEPATTERN_DIM = 32;
    protected static final int SQL_LOCATION_DIM = 255;
    protected static final int SQL_SOUNDFILE_DIM = 255;
    protected static final int SQL_STARTDATEPATTERN_DIM = 32;
    protected static final int SQL_SUBJECT_DIM = 1000;
    protected static final int SQL_FOLDER_DIM = 255;
    protected static final int SQL_TIME_ZONE_DIM = 255;

    //------------------------------------------------------------- Private data
    private int type;

    //------------------------------------------------------------- Constructors

    /*
     * @see PIMEntityDAO#PIMEntityDAO(String, String)
     */
    public PIMCalendarDAO(String userId, Class allowedContent) {
        super(userId, Def.ID_COUNTER);

        if (allowedContent == Event.class) {
            type = CALENDAR_EVENT_TYPE;
        } else if (allowedContent == Task.class) {
            type = CALENDAR_TASK_TYPE;
        } else {
            type = CALENDAR_BOTH_TYPES;
        }

        if (log.isTraceEnabled()) {
            log.trace("Created new PIMCalendarDAO for user ID " + userId + ". "
                    + "It will retrieve content of " + allowedContent);
        }
    }

    //----------------------------------------------------------- Public methods

    /**
     * Adds a calendar. If necessary, a new ID is generated and set in the
     * CalendarWrapper.
     *
     * @param c as a CalendarWrapper object, usually without an ID set.
     * @throws DAOException
     *
     * @see CalendarWrapper
     */
    public void addItem(CalendarWrapper cw) throws DAOException {

        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarDAO addItem begin");
        }

        Connection        con = null;
        PreparedStatement ps  = null;

        long              id               = 0   ;
        String            allDay           = null;
        String            body             = null;
        Short             busyStatus       = null;
        String            categories       = null;
        String            companies        = null;
        int               duration         = 0   ;
        Date              dend             = null;
        short             importance       = 0   ;
        String            location         = null;
        Short             meetingStatus    = null;
        String            mileage          = null;
        Date              replyTime        = null;
        short             sensitivity      = 0   ;
        Date              dstart           = null;
        String            subject          = null;
        int               interval         = 0   ;
        short             monthOfYear      = 0   ;
        short             dayOfMonth       = 0   ;
        String            dayOfWeekMask    = null;
        short             instance         = 0   ;
        String            startDatePattern = null;
        String            endDatePattern   = null;
        Reminder          reminder         = null;
        RecurrencePattern rp               = null;
        short             recurrenceType   = -1  ;
        String            sId              = null;
        int               occurrences      = -1  ;
        String            folder           = null;
        String            dstartTimeZone   = null;
        String            dendTimeZone     = null;
        String            reminderTimeZone = null;
        Date              completed        = null;
        short             percentComplete  = -1  ;

        Timestamp lastUpdate = cw.getLastUpdate();
        if (lastUpdate == null) {
            lastUpdate = new Timestamp(System.currentTimeMillis());
        }

        try {

            sId = cw.getId();
            if (sId == null || sId.length() == 0) { // ...as it should be
                sId = getNextID();
                cw.setId(sId);
            }
            id = Long.parseLong(sId);

            CalendarContent c = cw.getCalendar().getCalendarContent();

            rp = c.getRecurrencePattern();

            boolean allDayB;
            if (c.getAllDay() != null && c.getAllDay().booleanValue()) {
                allDayB = true;
                allDay = "1";
            } else {
                allDayB = false;
                allDay = "0";
            }

            String sd = null;
            if (c.getDtStart() != null) {
                sd = c.getDtStart().getPropertyValueAsString();
                dstart = getDateFromString(allDayB, sd, "000000");
            }

            String ed = null;
            if ((sd != null && sd.length() > 0) || c.getDtEnd() != null) {
                ed = c.getDtEnd().getPropertyValueAsString();

                //
                // Fix for Siemens S56 end date issue only for event
                // @todo: verify if is really need to do this
                //
                if (c instanceof Event) {
                    if (ed == null || ed.length()==0) {
                        ed = sd;
                    }
                }

                dend = getDateFromString(allDayB, ed, "235900");
            }

            body = Property.stringFrom(c.getDescription());

            if (c.getBusyStatus() != null) {
                busyStatus = new Short(c.getBusyStatus().shortValue());
            }

            categories       = Property.stringFrom  (c.getCategories());
            companies        = Property.stringFrom  (c.getOrganizer() );
            location         = Property.stringFrom  (c.getLocation()  );
            folder           = Property.stringFrom  (c.getFolder()    );
            dstartTimeZone   = timeZoneFrom(c.getDtStart()   );
            dendTimeZone     = timeZoneFrom(c.getDtEnd()     );
            reminderTimeZone = timeZoneFrom(c.getReminder()  );
            meetingStatus    = c.getMeetingStatus();

            Integer mileageInteger = c.getMileage(); // NB: not an int...
            if (mileageInteger != null) { // ...therefore it may be null
                mileage = String.valueOf(mileageInteger);
            }

            if (c instanceof Event) {
                replyTime =
                    getDateFromString(allDayB, // @todo or false?
                                      Property.stringFrom(((Event) c).getReplyTime()),
                                      "000000");
            }

            try {
                sensitivity = Short.parseShort(Property.stringFrom(c.getAccessClass()));
            } catch (NumberFormatException nfe) {
                sensitivity = 0;
                // The short must go on
            }

            if ((subject = Property.stringFrom(c.getSummary())) == null && body != null) {
                int endOfSentence = body.indexOf('.');
                if (endOfSentence == -1) {
                    endOfSentence = body.length();
                }
                if (endOfSentence > SQL_SUBJECT_DIM) {
                    endOfSentence = SQL_SUBJECT_DIM;
                }
                subject = body.substring(0, endOfSentence);
            }

            String isInfinite = "0";
            if (rp != null) {
                interval         = rp.getInterval()                     ;
                recurrenceType   = rp.getTypeId()                       ;
                monthOfYear      = rp.getMonthOfYear()                  ;
                dayOfMonth       = rp.getDayOfMonth()                   ;
                dayOfWeekMask    = String.valueOf(rp.getDayOfWeekMask());
                instance         = rp.getInstance()                     ;
                startDatePattern = rp.getStartDatePattern()             ;
                endDatePattern   = rp.getEndDatePattern()               ;
                if (rp.isNoEndDate()) {
                    isInfinite = "1";
                }
                occurrences      = rp.getOccurrences()                  ;
            }

            if (c instanceof Task) {
                Task t = (Task) c;
                if (t.getDateCompleted() != null) {
                     completed = getDateFromString(
                         allDayB,
                         ((Task) c).getDateCompleted().getPropertyValueAsString(),
                         "000000"
                     );
                }

                try {
                    String complete = Property.stringFrom(t.getComplete());
                    if (complete != null && complete.equals("1")) {
                        percentComplete = 100;
                    } else {
                        percentComplete = Short.parseShort(
                                Property.stringFrom(t.getPercentComplete())
                            );
                    }
                    if (percentComplete < 0 || percentComplete > 100) {
                        throw new NumberFormatException("A percentage can't be "
                                + percentComplete);
                    }
                } catch (NumberFormatException nfe) {
                    percentComplete = -1; // the short must go on
                }

                
                meetingStatus = getTaskStatus(t);
            }

            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(SQL_INSERT_INTO_FNBL_PIM_CALENDAR);

            ps.setLong  (1, id                               );
            ps.setString(2, userId                           );
            ps.setLong  (3, lastUpdate.getTime()             );
            ps.setString(4, String.valueOf(Def.PIM_STATE_NEW));
            ps.setString(5, allDay                           );
            ps.setString(6, StringUtils.left(body, SQL_BODY_DIM)     );
            if (busyStatus != null) {
                ps.setShort(7, busyStatus.shortValue());
            } else {
                ps.setNull (7, Types.SMALLINT);
            }
            ps.setString(8 , StringUtils.left(categories, SQL_CATEGORIES_DIM));
            ps.setString(9 , StringUtils.left(companies , SQL_COMPANIES_DIM ));
            ps.setInt   (10, duration);
            if (dend != null) {
                ps.setTimestamp(11, new Timestamp(dend.getTime()));
            } else {
                ps.setNull     (11, Types.TIMESTAMP);
            }

            if (c.getPriority() != null) {

                String priority = c.getPriority().getPropertyValueAsString();

                if (priority != null && priority.length() > 0) {
                    importance = Short.parseShort(priority);
                    ps.setShort(12, importance);
                } else {
                    ps.setNull(12, Types.SMALLINT);
                }

            } else {
                ps.setNull(12, Types.SMALLINT);
            }

            ps.setString(13, StringUtils.left(location, SQL_LOCATION_DIM));

            if (meetingStatus != null) {
                ps.setShort(14, meetingStatus.shortValue());
            } else {
                ps.setNull (14, Types.SMALLINT);
            }

            ps.setString(15, mileage);

            reminder = c.getReminder();
            if (reminder != null && reminder.isActive()) {
                Timestamp reminderTime = getReminderTime(dstart, reminder);
                if (reminderTime == null) {
                    ps.setNull     (16, Types.TIMESTAMP);
                } else {
                    ps.setTimestamp(16, reminderTime   );
                }
                ps.setInt   (17, reminder.getRepeatCount()        );
                ps.setString(18, (reminder.isActive()) ? "1" : "0");

                String soundFileValue = reminder.getSoundFile();
                ps.setString(19, StringUtils.left(soundFileValue, SQL_SOUNDFILE_DIM));
                ps.setInt   (20, reminder.getOptions()                      );
            } else {
                ps.setNull  (16, Types.TIMESTAMP);
                ps.setInt   (17, 0              );
                ps.setString(18, "0"            );
                ps.setString(19, null           );
                ps.setInt   (20, 0              );
            }

            if (replyTime != null) {
                ps.setTimestamp(21, new Timestamp(replyTime.getTime()));
            } else {
                ps.setNull     (21, Types.TIMESTAMP);
            }

            ps.setShort(22, sensitivity);

            if (dstart != null) {
                ps.setTimestamp(23, new Timestamp(dstart.getTime()));
            } else {
                ps.setNull     (23, Types.TIMESTAMP);
            }
            ps.setString(24, StringUtils.left(subject, SQL_SUBJECT_DIM));
            ps.setShort (25, recurrenceType);
            ps.setInt   (26, interval      );
            ps.setShort (27, monthOfYear   );
            ps.setShort (28, dayOfMonth    );
            ps.setString(29, StringUtils.left(dayOfWeekMask, SQL_DAYOFWEEKMASK_DIM));
            ps.setShort (30, instance      );
            ps.setString(31,
                         StringUtils.left(startDatePattern, SQL_STARTDATEPATTERN_DIM));
            ps.setString(32, isInfinite    );
            ps.setString(33, StringUtils.left(endDatePattern, SQL_ENDDATEPATTERN_DIM));
            ps.setInt   (34, occurrences   );

            if (c instanceof Event) {
                ps.setInt (35, CALENDAR_EVENT_TYPE);
                ps.setNull(36, Types.TIMESTAMP    ); // completed
                ps.setNull(37, Types.SMALLINT     ); // percent_complete
            } else {
                ps.setInt (35, CALENDAR_TASK_TYPE);

                if (completed != null) {
                    ps.setTimestamp(36, new Timestamp(completed.getTime()));
                } else {
                    ps.setNull     (36, Types.TIMESTAMP);
                }

                if (percentComplete != -1) {
                    ps.setShort(37, percentComplete);
                } else {
                    ps.setNull (37, Types.SMALLINT);
                }
            }

            ps.setString(38, StringUtils.left(folder,           SQL_FOLDER_DIM   ));
            ps.setString(39, StringUtils.left(dstartTimeZone,   SQL_TIME_ZONE_DIM));
            ps.setString(40, StringUtils.left(dendTimeZone,     SQL_TIME_ZONE_DIM));
            ps.setString(41, StringUtils.left(reminderTimeZone, SQL_TIME_ZONE_DIM));
            
            ps.executeUpdate();
            DBTools.close(null, ps, null);

            if (recurrenceType != -1) {
                List<ExceptionToRecurrenceRule> exceptions = 
                        rp.getExceptions();
                for (ExceptionToRecurrenceRule etrr : exceptions) {
                    ps = con.prepareStatement(
                            SQL_INSERT_INTO_FNBL_PIM_CALENDAR_EXCEPTION
                         );

                    ps.setLong     (1, id);
                    ps.setString   (2, (etrr.isAddition() ? "1" : "0"));
                    ps.setTimestamp(3, new Timestamp(
                                        getDateFromString(allDayB,
                                                          etrr.getDate(),
                                                          "000000").getTime())
                                    );

                    ps.executeUpdate();
                    DBTools.close(null, ps, null);
                }
            }

        } catch (Exception e) {
            throw new DAOException(
                    "Error adding a calendar item: " + e.getMessage(), e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Updates a calendar.
     *
     * @param cw as a CalendarWrapper object. If its last update time is null,
     *          then it's set to the current time.
     * @return the UID of the contact
     * @throws DAOException
     * @throws java.lang.Exception 
     * @see CalendarWrapper
     */
    public String updateItem(CalendarWrapper cw)
    throws DAOException, Exception {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        RecurrencePattern rp               = null;
        String            id               = null;
        String            allDay           = null;
        String            body             = null;
        Boolean           allDayBoolean    = null;
        Short             busyStatus       = null;
        String            categories       = null;
        String            companies        = null;
        int               duration         = 0   ;
        Date              dend             = null;
        short             importance       = 0   ;
        String            location         = null;
        Short             meetingStatus    = null;
        String            mileage          = null;
        Date              replyTime        = null;
        short             sensitivity      = 0   ;
        Date              dstart           = null;
        String            subject          = null;
        short             recurrenceType   = -1  ;
        int               interval         = 0   ;
        short             monthOfYear      = 0   ;
        short             dayOfMonth       = 0   ;
        String            dayOfWeekMask    = null;
        String            priority         = null;
        short             instance         = 0   ;
        String            startDatePattern = null;
        String            noEndDate        = null;
        String            endDatePattern   = null;
        int               occurrences      = -1  ;
        Reminder          reminder         = null;
        CalendarContent   c                = null;
        Date              completed        = null;
        String            complete         = null;
        short             percentComplete  = -1  ;
        String            folder           = null;
        String            dstartTimeZone   = null;
        String            dendTimeZone     = null;
        String            reminderTimeZone = null;

        StringBuffer queryUpdateFunPimCalendar = null;

        try {

            Timestamp lastUpdate = (cw.getLastUpdate() == null)
                                 ? new Timestamp(System.currentTimeMillis())
                                 : cw.getLastUpdate()
                                 ;

            c = cw.getCalendar().getCalendarContent();

            rp = c.getRecurrencePattern();

            id = cw.getId();

            boolean allDayB;
            allDayBoolean = c.getAllDay();
            if (allDayBoolean != null && allDayBoolean.booleanValue()) {
                allDayB = true;
                allDay = "1";
            } else {
                allDayB = false;
                allDay = "0";
            }

            body = Property.stringFrom(c.getDescription());

            if (c.getBusyStatus() != null) {
                busyStatus = new Short(c.getBusyStatus().shortValue());
            }
            categories = Property.stringFrom(c.getCategories());
            companies  = Property.stringFrom(c.getOrganizer() );
            if (c.getPriority() != null) {
                priority = c.getPriority().getPropertyValueAsString();
                if (priority != null && priority.length() > 0) {
                    importance = Short.parseShort(priority);
                } // priority / importance ??
            }
            location      = Property.stringFrom(c.getLocation());
            meetingStatus = c.getMeetingStatus();
            if(c.getMileage()!=null) {
                mileage       = String.valueOf(c.getMileage());
            }

            reminder = c.getReminder();

            String rt = null;
            if (c instanceof Event) {
                rt = Property.stringFrom(((Event)c).getReplyTime());
                replyTime =
                    getDateFromString(allDayB , // @todo or false?
                                      rt      ,
                                      "000000");
            }

            if (c.getAccessClass() != null) {

                String classEvent = null;
                classEvent = c.getAccessClass().getPropertyValueAsString();

                if (classEvent != null && classEvent.length() > 0) {
                    sensitivity = Short.parseShort(classEvent);
                }
            }

            if (c.getSummary() != null) {

                subject = c.getSummary().getPropertyValueAsString();

            } else if (body != null && body.length() > 0) {

                String tmpBody = body;

                if (tmpBody.indexOf('.') != -1) {
                    tmpBody = tmpBody.substring(0, tmpBody.indexOf('.'));
                }

                if (tmpBody.length() > SQL_SUBJECT_DIM) {
                    tmpBody = tmpBody.substring(0, SQL_SUBJECT_DIM);
                }

                subject = tmpBody;
            }

            folder           = Property.stringFrom  (c.getFolder()  );
            dstartTimeZone   = timeZoneFrom(c.getDtStart() );
            dendTimeZone     = timeZoneFrom(c.getDtEnd()   );
            reminderTimeZone = timeZoneFrom(c.getReminder());

            String sd = null;
            if (c.getDtStart() != null) {
                sd     = c.getDtStart().getPropertyValueAsString();
                dstart = getDateFromString(allDayB, sd, "000000");
            }

            String ed = null;
            if ((sd != null && sd.length() > 0) || (c.getDtEnd() != null)) {

                ed = c.getDtEnd().getPropertyValueAsString();

                //
                // Fix for Siemens S56 end date issue only for event
                // @todo: verify if is really need to do this
				// Due to this fix, in  method getTwinItems() if the incoming 
				// Event has an empty EndDate we seek into the db for Events 
				// with EndDate equal to the StartDate value.
                //
                if (c instanceof Event) {
                    if (ed == null || ed.length() == 0) {
                        ed = sd;
                    }
                }

                dend = getDateFromString(allDayB, ed, "235900");
            }

            if (rp != null) {

                recurrenceType   = rp.getTypeId()                       ;
                interval         = rp.getInterval()                     ;
                monthOfYear      = rp.getMonthOfYear()                  ;
                dayOfMonth       = rp.getDayOfMonth()                   ;
                dayOfWeekMask    = String.valueOf(rp.getDayOfWeekMask());
                instance         = rp.getInstance()                     ;
                startDatePattern = rp.getStartDatePattern()             ;

                boolean noEndDateB = rp.isNoEndDate();
                if (noEndDateB) {
                    noEndDate = "1";
                } else {
                    noEndDate = "0";
                }

                endDatePattern = rp.getEndDatePattern();
                occurrences    = rp.getOccurrences();
            }

            String dc = null;
            if (c instanceof Task) {

                Task t = (Task)c;
                if (t.getDateCompleted() != null) {
                    dc        = t.getDateCompleted().getPropertyValueAsString();
                    completed = getDateFromString(allDayB, dc, "000000");
                }

                complete = Property.stringFrom(t.getComplete());
                if (complete != null && "1".equals(complete)) {
                    percentComplete = 100;
                } else {
                    try {
                        percentComplete = Short.parseShort(
                                Property.stringFrom(t.getPercentComplete()));
                        if (percentComplete < 0 || percentComplete > 100) {
                            throw new NumberFormatException("A percentage can't be "
                                    + percentComplete);
                        }
                    } catch (NumberFormatException nfe) {
                        percentComplete = -1; // the short must go on
                    }
                }

                meetingStatus = getTaskStatus(t);

            }

            queryUpdateFunPimCalendar = new StringBuffer();

            queryUpdateFunPimCalendar.append(SQL_UPDATE_FNBL_PIM_CALENDAR_BEGIN)
                                     .append(SQL_FIELD_LAST_UPDATE             )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA    );

            if (allDayBoolean != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_ALL_DAY            )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (body != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_BODY               )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_BUSY_STATUS        )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (categories != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_CATEGORIES         )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (companies != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_COMPANIES          )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_DURATION           )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (dend != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_DATE_END           )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else if (ed != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_DATE_END    )
                                         .append(SQL_EQUALS_NULL_COMMA);
            }

            if (priority != null && priority.length() > 0) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_IMPORTANCE         )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (location != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_LOCATION           )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (meetingStatus != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_MEETING_STATUS     )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (mileage != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_MILEAGE            )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (reminder != null) {
                if (reminder.isActive()) {
                    queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER  )
                                             .append(SQL_EQUALS_ONE_COMMA);
                } else {
                    queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER   )
                                             .append(SQL_EQUALS_ZERO_COMMA);
                }
                queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER_TIME      )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
                queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER_REPEAT_COUNT)
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
                queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER_SOUND_FILE)
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
                queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER_OPTIONS   )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (replyTime != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_REPLY_TIME         )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else if (rt != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_REPLY_TIME )
                                         .append(SQL_EQUALS_NULL_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_SENSITIVITY        )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (dstart != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_DATE_START         )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else if (sd != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_DATE_START )
                                         .append(SQL_EQUALS_NULL_COMMA);
            }

            if (subject != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_SUBJECT            )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_RECURRENCE_TYPE)
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            queryUpdateFunPimCalendar.append(SQL_FIELD_INTERVAL           )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            queryUpdateFunPimCalendar.append(SQL_FIELD_MONTH_OF_YEAR      )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            queryUpdateFunPimCalendar.append(SQL_FIELD_DAY_OF_MONTH       )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (dayOfWeekMask != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_DAY_OF_WEEK_MASK   )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_INSTANCE           )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (startDatePattern != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_START_DATE_PATTERN )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (noEndDate != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_NO_END_DATE        )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            if (endDatePattern != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_END_DATE_PATTERN   )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else {
                //
                // When NoEndDate is true, the PatterEndDate must be empty.
                //
                if ("1".equals(noEndDate)) {
                    queryUpdateFunPimCalendar.append(SQL_FIELD_END_DATE_PATTERN)
                                             .append(SQL_EQUALS_EMPTY_COMMA   );
                }
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_OCCURRENCES        )
                                     .append(SQL_EQUALS_QUESTIONMARK_COMMA);

            if (completed != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_COMPLETED          )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else if (dc != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_COMPLETED  )
                                         .append(SQL_EQUALS_NULL_COMMA);
            }

            if (percentComplete != -1) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_PERCENT_COMPLETE   )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            } else if ("0".equals(complete)) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_PERCENT_COMPLETE   )
                                         .append(SQL_PERCENT_COMPLETE_FORMULA  );
            }

            if (folder != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_FOLDER             )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            if (dstartTimeZone != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_START_DATE_TIME_ZONE)
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            if (dendTimeZone != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_END_DATE_TIME_ZONE )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }
            
            if (reminderTimeZone != null) {
                queryUpdateFunPimCalendar.append(SQL_FIELD_REMINDER_TIME_ZONE )
                                         .append(SQL_EQUALS_QUESTIONMARK_COMMA);
            }

            queryUpdateFunPimCalendar.append(SQL_FIELD_STATUS                )
                                     .append(SQL_EQUALS_QUESTIONMARK         )
                                     .append(SQL_UPDATE_FNBL_PIM_CALENDAR_END);

            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(queryUpdateFunPimCalendar.toString());

            int k = 1;

            //
            // lastUpdate
            //
            ps.setLong(k++, lastUpdate.getTime());
            //
            // allDay
            //
            if (allDayBoolean != null) {
                ps.setString(k++, allDay);
            }
            //
            // body
            //
            if (body != null) {
                if (body.length() > SQL_BODY_DIM) {
                    body = body.substring(0, SQL_BODY_DIM);
                }
                ps.setString(k++, body);
            }
            //
            // busy status
            //
            if (busyStatus != null) {
                ps.setShort(k++, busyStatus.shortValue());
            } else {
                ps.setNull(k++, Types.SMALLINT);
            }
            //
            // categories
            //
            if (categories != null) {
                if (categories.length() > SQL_CATEGORIES_DIM) {
                    categories = categories.substring(0, SQL_CATEGORIES_DIM);
                }
                ps.setString(k++, categories);
            }
            //
            // companies
            //
            if (companies != null) {
                if (companies.length() > SQL_COMPANIES_DIM) {
                    companies = companies.substring(0, SQL_COMPANIES_DIM);
                }
                ps.setString(k++, companies);
            }
            //
            // duration
            //
            ps.setInt(k++, duration);
            //
            // date End
            //
            if (dend != null) {
                ps.setTimestamp(k++, new Timestamp(dend.getTime()));
            }
            //
            // priority
            //
            if (priority != null && priority.length() > 0) {
                ps.setShort(k++, importance);
            }
            //
            // location
            //
            if (location != null) {
                if (location.length() > SQL_COMPANIES_DIM) {
                    location = location.substring(0, SQL_LOCATION_DIM);
                }
                ps.setString(k++, location);
            }
            //
            // meeting status
            //
            if (meetingStatus != null) {
                ps.setShort(k++, meetingStatus.shortValue());
            }
            //
            // mileage
            //
            if (mileage != null) {
                ps.setString(k++, mileage);
            }
            //
            // reminder
            //
            if (reminder != null) {
                if (reminder.isActive()) {
                    ps.setTimestamp(k++, getReminderTime(dstart, reminder));
                    ps.setInt      (k++, reminder.getRepeatCount()        );
                    String soundFileValue = reminder.getSoundFile();
                    if (soundFileValue != null &&
                        soundFileValue.length() > SQL_SOUNDFILE_DIM) {

                        soundFileValue =
                            soundFileValue.substring(0, SQL_SOUNDFILE_DIM);
                    }
                    ps.setString(k++, soundFileValue       );
                    ps.setInt   (k++, reminder.getOptions());
                } else {
                    ps.setNull  (k++, Types.TIMESTAMP);
                    ps.setInt   (k++, 0              );
                    ps.setString(k++, null           );
                    ps.setInt   (k++, 0              );
                }
            }
            //
            // reply time
            //
            if (replyTime != null) {
                ps.setTimestamp(k++, new Timestamp(replyTime.getTime()));
            }
            //
            // sensitivity
            //
            ps.setShort(k++, sensitivity);
            //
            // date start
            //
            if (dstart != null) {
                ps.setTimestamp(k++, new Timestamp(dstart.getTime()));
            }
            //
            // subject
            //
            if (subject != null) {
                if (subject.length() > SQL_SUBJECT_DIM) {
                    subject = subject.substring(0, SQL_SUBJECT_DIM);
                }
                ps.setString(k++, subject);
            }
            //
            // recurrence Type
            //
            ps.setShort(k++, recurrenceType);
            //
            // interval
            //
            ps.setInt(k++, interval);
            //
            // month of year
            //
            ps.setShort(k++, monthOfYear);
            //
            // day of month
            //
            ps.setShort(k++, dayOfMonth);
            //
            // day of week mask
            //
            if (dayOfWeekMask != null) {
                if (dayOfWeekMask.length() > SQL_DAYOFWEEKMASK_DIM) {
                    dayOfWeekMask = dayOfWeekMask.substring(0, SQL_DAYOFWEEKMASK_DIM);
                }
                ps.setString(k++, dayOfWeekMask);
            }
            //
            // instance
            //
            ps.setShort(k++, instance);
            //
            // start date pattern
            //
            if (startDatePattern != null) {
                if (startDatePattern.length() > SQL_STARTDATEPATTERN_DIM) {
                    startDatePattern = startDatePattern.substring(0,
                            SQL_STARTDATEPATTERN_DIM);
                }
                ps.setString(k++, startDatePattern);
            }
            //
            // no end date
            //
            if (noEndDate != null) {
                ps.setString(k++, noEndDate);
            }
            //
            // end date pattern
            //
            if (endDatePattern != null) {
                if (endDatePattern.length() > SQL_ENDDATEPATTERN_DIM) {
                    endDatePattern = endDatePattern.substring(0,
                            SQL_ENDDATEPATTERN_DIM);
                }
                ps.setString(k++, endDatePattern);
            }
            //
            // occurrences
            //
            ps.setInt(k++, occurrences);
            //
            // completed
            //
            if (completed != null) {
                ps.setTimestamp(k++, new Timestamp(completed.getTime()));
            }
            //
            // percent completed
            //
            if (percentComplete != -1) {
                ps.setShort(k++, percentComplete);
            }
            //
            // folder
            //
            if (folder != null) {
                if (folder.length() > SQL_FOLDER_DIM) {
                    folder = folder.substring(0, SQL_FOLDER_DIM);
                }
                ps.setString(k++, folder);
            }
            //
            // time zones
            //
            if (dstartTimeZone != null) {
                if (dstartTimeZone.length() > SQL_TIME_ZONE_DIM) {
                    dstartTimeZone = dstartTimeZone.substring(0, SQL_TIME_ZONE_DIM);
                }
                ps.setString(k++, dstartTimeZone);
            }
            if (dendTimeZone != null) {
                if (dendTimeZone.length() > SQL_TIME_ZONE_DIM) {
                    dendTimeZone = dendTimeZone.substring(0, SQL_TIME_ZONE_DIM);
                }
                ps.setString(k++, dendTimeZone);
            }
            if (reminderTimeZone != null) {
                if (reminderTimeZone.length() > SQL_TIME_ZONE_DIM) {
                    reminderTimeZone = reminderTimeZone.substring(0, SQL_TIME_ZONE_DIM);
                }
                ps.setString(k++, reminderTimeZone);
            }
            //
            // status
            //
            ps.setString(k++, String.valueOf('U') ) ;
            //
            // id
            //
            ps.setLong(k++, Long.parseLong(id) ) ;
            //
            // user id
            //
            ps.setString(k++, cw.getUserId() ) ;

            ps.executeUpdate();

            DBTools.close(null, ps, null);

            ps = con.prepareStatement(SQL_DELETE_CALENDAR_EXCEPTIONS_BY_CALENDAR);

            ps.setLong(1, Long.parseLong(id));

            ps.executeUpdate();

            DBTools.close(null, ps, null);

            if (recurrenceType != -1) {
                List<ExceptionToRecurrenceRule> exceptions = 
                        rp.getExceptions();
                for (ExceptionToRecurrenceRule etrr : exceptions) {

                    ps = con.prepareStatement(
                            SQL_INSERT_INTO_FNBL_PIM_CALENDAR_EXCEPTION);

                    ps.setLong(1, Long.parseLong(id));
                    ps.setString(2, (etrr.isAddition() ? "1" : "0"));
                    ps.setTimestamp(3, new Timestamp(
                            getDateFromString(allDayB, etrr.getDate(),
                            "000000")
                            .getTime()));

                    ps.executeUpdate();

                    DBTools.close(null, ps, null);
                }
            }

        } catch (Exception e) {
            throw new DAOException("Error updating a calendar item: "
                    + e.getMessage());
        } finally {
            DBTools.close(con, ps, rs);
        }

        return id;
    }
    
    public CalendarWrapper getItem(String uid) throws DAOException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getItem " + uid);
        }

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        CalendarWrapper cw = null;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(SQL_GET_FNBL_PIM_CALENDAR_BY_ID_USERID);

            ps.setLong(1, Long.parseLong(uid));
            ps.setString(2, userId);

            rs = ps.executeQuery();

            cw = createCalendar(uid, rs);

            DBTools.close(null, ps, rs);

            ps = con.prepareStatement(
                    SQL_GET_FNBL_PIM_CALENDAR_EXCEPTION_BY_CALENDAR);

            ps.setLong(1, Long.parseLong(uid));

            rs = ps.executeQuery();

            try {
                cw = addPIMCalendarExceptions(cw, rs);
            } catch (SQLException sqle) {
                throw new SQLException("Error while adding PIM calendar "
                        + "exceptions. " + sqle,
                        sqle.getSQLState());
            }

        } catch (Exception e) {
            throw new DAOException(
                    "Error retrieving a calendar item: " + e, e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return cw;
    }

    /**
     * Removes a calendar, provided it has the same userId as this DAO.
     * The deletion is soft (reversible).
     *
     * @param calendar whence the UID and the last update Date are extracted
     * @throws DAOException
     */
    public void removeItem(CalendarWrapper calendar)
            throws DAOException {
        removeItem(calendar.getId(), null);
    }

    /**
     * Retrieves the UID list of the calendars considered to be "twins" of a
     * given calendar.
     *
     * @param c the Calendar object representing the calendar whose twins
     *          need be found. In the present implementation, only the following
     *          data matter: 
     *          <BR>for events <UL><LI>date start<LI>date end<LI>subject</UL>
     *          for tasks <UL><LI>date end<LI>subject</UL>
     * @throws DAOException
     * @return a List of UIDs (as String objects) that may be empty but not null
     */
    public List getTwinItems(Calendar c) throws DAOException {

        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarDAO getTwinItems begin");
        }

        LinkedList<String> twins = new LinkedList<String>();
        Connection         con   = null;
        PreparedStatement  ps    = null;
        ResultSet          rs    = null;

        if(!isTwinSearchAppliableOn(c)) {
            if (log.isTraceEnabled()) {
                    log.trace("Item with no dtStart, dtEnd, summary: twin search skipped.");
            }
            return twins;
        }

        try {

            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);
            
            Date dtStart;
            Date dtEnd;
            Date dueTomorrowNoon = null;
            Date dueYesterdayNoon = null;

            dtStart = getDateFromString(c.getCalendarContent().isAllDay(),
                    Property.stringFrom(c.getCalendarContent().getDtStart()), "000000");
            dtEnd = getDateFromString(c.getCalendarContent().isAllDay(),
                    Property.stringFrom(c.getCalendarContent().getDtEnd()), "235900");

            if ((dtEnd != null) && (c.getCalendarContent() instanceof Task)) {
                java.util.Calendar noon =
                        new GregorianCalendar(TimeZone.getTimeZone("UTC"));
                noon.setTime(dtEnd);
                noon.set(java.util.Calendar.HOUR_OF_DAY, 12);
                noon.set(java.util.Calendar.MINUTE, 0);
                noon.set(java.util.Calendar.MILLISECOND, 0);
                noon.add(java.util.Calendar.DATE, +1);
                dueTomorrowNoon = noon.getTime();
                noon.add(java.util.Calendar.DATE, -2); // go back and another -1
                dueYesterdayNoon = noon.getTime();
            }

            StringBuffer sqlGetCalendarTwinList =
                    new StringBuffer(SQL_GET_FNBL_PIM_CALENDAR_ID_LIST_BY_USER);

            String subject =
                    Property.stringFrom(c.getCalendarContent().getSummary(), true); // Empty implies null;

            if ("null".equals(subject)) {
                subject = null;
            }
            if (subject == null) {
                sqlGetCalendarTwinList.append(SQL_AND_NO_SUBJECT_IS_SET);
            } else {
                sqlGetCalendarTwinList.append(SQL_AND_SUBJECT_EQUALS_QUESTIONMARK);
            }
            if (c.getCalendarContent() instanceof Event) {
                if (dtStart == null) {
                    sqlGetCalendarTwinList.append(SQL_AND_NO_DSTART_IS_SET);
                } else {
                    sqlGetCalendarTwinList.append(SQL_AND_DSTART_EQUALS_QUESTIONMARK);
                }
            }
            if (dtEnd == null) {
				// In method updateItems() while storing the Event in the db, if
				// the End Date is empty it is filled with the Start Date.
				// Filling the empty EndDate with the StartDate is done only for
				// Events and not for Tasks.
				// See "Fix for Siemens S56 end date issue" in method 
				// updateItems().
				// So in order to find the twins, if the incoming Event has an 
				// empty EndDate we seek into the db for Events with EndDate 
				// equal to the StartDate value.
                if (c.getCalendarContent() instanceof Task) {
                    sqlGetCalendarTwinList.append(SQL_AND_NO_DEND_IS_SET);
                }else {
                    sqlGetCalendarTwinList.append(SQL_AND_DEND_EQUALS_QUESTIONMARK);
                }
            } else {
                if (c.getCalendarContent() instanceof Task) {
                    sqlGetCalendarTwinList.append(SQL_AND_DEND_IN_INTERVAL);
                } else {
                    sqlGetCalendarTwinList.append(SQL_AND_DEND_EQUALS_QUESTIONMARK);
                }
            }

            if (c.getCalendarContent() instanceof Event) {
                sqlGetCalendarTwinList.append(SQL_FILTER_BY_TYPE[CALENDAR_EVENT_TYPE]);
            } else {
                sqlGetCalendarTwinList.append(SQL_FILTER_BY_TYPE[CALENDAR_TASK_TYPE]);
            }

            //
            // If funambol is not in the debug mode it is not possible to print 
            // the calendar info because it contains sensitive data.
            //
            if (Configuration.getConfiguration().isDebugMode()) {

                if (log.isTraceEnabled()) {

                    StringBuilder sb = new StringBuilder(100);
                    sb.append("Looking for items having: ");

                    if (subject == null || subject.length() == 0) {
                        sb.append("\n> subject: <N/A>");
                    } else {
                        sb.append("\n> subject: '").append(subject).append('\'');
                    }
                    if (c.getCalendarContent() instanceof Event) {
                        if (dtStart == null) {
                            sb.append("\n> start date: <N/A>");
                        } else {
                            sb.append("\n> start date: ").append(dtStart);
                        }
                        if (dtEnd == null) {
                            sb.append("\n> end date: <N/A>");
                        } else {
                            sb.append("\n> end date: ").append(dtEnd);
                        }
                    } else { // It's a task
                        if (dtEnd == null) {
                            sb.append("\n> due date: <N/A>");
                        } else {
                            sb.append("\n> due date: between ")
                            .append(dueYesterdayNoon)
                            .append(  "\n>           and ").append(dueTomorrowNoon)
                            .append( ",\n>           possibly ").append(dtEnd);
                        }
                    }

                    log.trace(sb.toString());
                }
            }

            sqlGetCalendarTwinList.append(SQL_ORDER_BY_ID);

            ps = con.prepareStatement(sqlGetCalendarTwinList.toString());

            int k = 1;
            ps.setString(k++, userId);
            if (subject != null) {
                ps.setString(k++, subject.toLowerCase(Locale.ENGLISH));
            }
            if (dtStart != null) {
                if (c.getCalendarContent() instanceof Event) {
                    ps.setTimestamp(k++, new Timestamp(dtStart.getTime()));
                }
            }
            if (dtEnd != null) {
                if (c.getCalendarContent() instanceof Task) {
                    ps.setTimestamp(k++, new Timestamp(dueYesterdayNoon.getTime()));
                    ps.setTimestamp(k++, new Timestamp(dueTomorrowNoon.getTime()));
                } else {
                    ps.setTimestamp(k++, new Timestamp(dtEnd.getTime()));
                }
            } else {
				// In method updateItems() while storing the Event in the db, if
				// the End Date is empty it is filled with the Start Date.
				// Filling the empty EndDate with the StartDate is done only for
				// Events and not for Tasks.
				// See "Fix for Siemens S56 end date issue" in method 
				// updateItems().
				// So in order to find the twins, if the incoming Event has an 
				// empty EndDate we seek into the db for Events with EndDate 
				// equal to the StartDate value.
                if (c.getCalendarContent() instanceof Event) {
                    ps.setTimestamp(k++, new Timestamp(dtStart.getTime()));
                }
            }

            rs = ps.executeQuery();
            long twinId;
            Timestamp twinDueDate;
            while(rs.next()) {
                if (c.getCalendarContent() instanceof Event) {
                    twinId = rs.getLong(1);
                    // dend is not relevant in this case
                    if (log.isTraceEnabled()) {
                        log.trace("Twin event found: " + twinId);
                    }

                    twins.add(Long.toString(twinId));

                } else { // it's a Task
                    twinId = rs.getLong(1);
                    twinDueDate = rs.getTimestamp(2);
                    if (log.isTraceEnabled()) {
                        log.trace("Twin task found: " + twinId);
                    }

                    if ((dtEnd != null) && (twinDueDate != null) &&
                            twinDueDate.getTime() == dtEnd.getTime()) {
                        twins.addFirst(Long.toString(twinId));
                        if (log.isTraceEnabled()) {
                            log.trace("Item " + twinId
                                    + " is an exact due-date match.");
                        }
                    } else {
                        twins.addLast(Long.toString(twinId));
                    }
                }
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving twin. ",e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        if (log.isTraceEnabled()) {
            log.trace("PIMCalendarDAO getTwinItems end");
        }

        return twins;
    }


    /**
     * This method allows to understand if is possible to run the twin search
     * on the given calendar.
     * Fields used in the twin search are summary, dtstart and dtend (or due date
     * for the task) but they are used differently according to the type of calendar.
     * If the calendare contains an event, then the summary is used to find the twins
     * but the search requires that at least one among dtStart and dtEnd is not null.
     * If the calendar is a task, then the search requires that at least one among
     * due date and the summary is not null) while the dtStart is ignored.
     *
     * @param c the calendar we want to check.
     *
     * @return true if at least one field used for twin search contains meaningful
     * data, false otherwise.
     *
     */
    public boolean isTwinSearchAppliableOn(Calendar c) {
        if(c!=null) {

            CalendarContent cc = c.getCalendarContent();

            if(cc==null)
                return false;

            boolean isEvent = cc instanceof Event;
            boolean dtEnd   = cc.getDtEnd()!= null &&
                              cc.getDtEnd().getPropertyValue()!=null &&
                              cc.getDtEnd().getPropertyValueAsString().length()>0;

            if(isEvent) {
                boolean dtStart = cc.getDtStart()!= null &&
                                  cc.getDtStart().getPropertyValue()!=null &&
                                  cc.getDtStart().getPropertyValueAsString().length()>0;
                return (dtEnd||dtStart);
            } else  {

                 boolean subject = cc.getSummary()!= null &&
                                   cc.getSummary().getPropertyValue()!=null &&
                                   cc.getSummary().getPropertyValueAsString().length()>0;

                return (dtEnd ||subject);
            }
        }
        return false;
    }


    //---------------------------------------------------------- Private methods


    /**
     * Creates a CalendarWrapper object of Calendar type from a ResultSet.
     *
     * @param wrapperId the UID of the wrapper object to be returned
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the fnbl_PIM_CALENDAR table, with the cursor before its first row
     * @return a newly created CalendarWrapper initialized with the fields in
     *         the result set
     * @throws Exception
     * @throws NotFoundException
     */
    protected static CalendarWrapper createCalendar(String    wrapperId,
                                                    ResultSet rs       )
    throws NotFoundException, Exception {

        if (!rs.next()) {
            throw new NotFoundException("No calendar found.");
        }
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        RecurrencePattern rp = null;

        String column = null;

        String uid = null;
        String userId = null;

        short recurrenceType = -1;
        int interval = 0;
        short monthOfYear = 0;
        short dayOfMonth = 0;
        short dayOfWeekMask = 0;
        short instance = 0;
        String startDatePattern = null;
        String endDatePattern = null;
        int occurrences = -1;
        boolean noEndDate = false;
        boolean aDay = false;
        Reminder r = null;
        Date replyTime = null;
        Date dstart = null;
        Date dend = null;
        Date completed = null;
        Date reminderTime = null;

        uid = String.valueOf(rs.getLong(SQL_FIELD_ID));
        userId = rs.getString(SQL_FIELD_USERID);

        Calendar cal = new Calendar();

        r = new Reminder();

        CalendarContent c;
        boolean isAnEvent = true;
        for (int i = 1; i <= columnCount; i++) {
            if (SQL_FIELD_TYPE.equalsIgnoreCase(rsmd.getColumnName(i))) {
                if (rs.getShort(i) == CALENDAR_TASK_TYPE) {
                    isAnEvent = false;
                }
                break;
            }
        }
        if (isAnEvent) {
            c = new Event();
            cal.setEvent((Event) c);
        } else {
            c = new Task();
            cal.setTask((Task) c);
        }

        c.setReminder(r);
        CalendarWrapper cw = new CalendarWrapper(wrapperId, userId, cal);

        for (int i = 1; i <= columnCount; i++) {

            column = rsmd.getColumnName(i);

            if (SQL_FIELD_ID.equalsIgnoreCase(column)) {
                // Does nothing: field already set at construction time
            } else if (SQL_FIELD_LAST_UPDATE.equalsIgnoreCase(column)) {
                cw.setLastUpdate(new Timestamp(rs.getLong(i)));
            } else if (SQL_FIELD_USERID.equalsIgnoreCase(column)) {
                // Does nothing: field already set at construction time
            } else if (SQL_FIELD_STATUS.equalsIgnoreCase(column)) {
                cw.setStatus(rs.getString(i).charAt(0));
            } else if (SQL_FIELD_ALL_DAY.equalsIgnoreCase(column)) {
                String allDay = null;
                allDay = rs.getString(i);
                if (allDay != null && allDay.length() > 0 ) {
                    if(allDay.charAt(0) == '1') {
                        aDay = true;
                        c.setAllDay(Boolean.TRUE);
                    } else if(allDay.charAt(0) == '0') {
                        c.setAllDay(Boolean.FALSE);
                    }
                }
            } else if (SQL_FIELD_BODY.equalsIgnoreCase(column)) {
                c.getDescription().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_BUSY_STATUS.equalsIgnoreCase(column)) {
                short bs = rs.getShort(i);
                if (rs.wasNull()) {
                    c.setBusyStatus(null);
                } else {
                    c.setBusyStatus(new Short(bs));
                }
            } else if (SQL_FIELD_CATEGORIES.equalsIgnoreCase(column)) {
                c.getCategories().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_COMPANIES.equalsIgnoreCase(column)) {
                c.getOrganizer().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_DURATION.equalsIgnoreCase(column)) {
                c.getDuration().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_DATE_END.equalsIgnoreCase(column)) {
                if (rs.getTimestamp(i) != null) {
                    dend = new Date(rs.getTimestamp(i).getTime());
                }
            } else if (SQL_FIELD_IMPORTANCE.equalsIgnoreCase(column)) {
                c.getPriority().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_LOCATION.equalsIgnoreCase(column)) {
                c.getLocation().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_MEETING_STATUS.equalsIgnoreCase(column)) {
                short meetingStatus = rs.getShort(i);
                if (!rs.wasNull()) {
                    if(c instanceof Task) {
                        c.getStatus().setPropertyValue(Short.toString(meetingStatus));
                    } else {
                        c.setMeetingStatus(new Short(meetingStatus));
                    }
                }
            } else if (SQL_FIELD_MILEAGE.equalsIgnoreCase(column)) {
                String mileage = null;
                mileage = rs.getString(i);
                if (mileage != null && mileage.length() > 0 && !("null".equals(mileage))) {
                    c.setMileage(Integer.valueOf(mileage));
                }
            } else if (SQL_FIELD_REMINDER_TIME.equalsIgnoreCase(column)) {
                 if (rs.getTimestamp(i) != null) {
                    reminderTime = new Date(rs.getTimestamp(i).getTime());
                }
            } else if (SQL_FIELD_REMINDER_REPEAT_COUNT.equalsIgnoreCase(column)) {
                r.setRepeatCount(rs.getInt(i));
            } else if (SQL_FIELD_REMINDER.equalsIgnoreCase(column)) {
                String reminder = null;
                reminder = rs.getString(i);
                if (reminder != null && reminder.length() > 0) {
                    r.setActive(reminder.charAt(0) == '1');
                } else {
                    r.setActive(false);
                }
            } else if (SQL_FIELD_REMINDER_SOUND_FILE.equalsIgnoreCase(column)) {
                r.setSoundFile(rs.getString(i));
            } else if (SQL_FIELD_REMINDER_OPTIONS.equalsIgnoreCase(column)) {
                r.setOptions(rs.getInt(i));
            } else if (SQL_FIELD_REPLY_TIME.equalsIgnoreCase(column)) {
                if (rs.getTimestamp(i) != null) {
                    replyTime = new Date(rs.getTimestamp(i).getTime());
                }
            } else if (SQL_FIELD_SENSITIVITY.equalsIgnoreCase(column)) {
                Short sensitivity = rs.getShort(i);
                if (sensitivity == null) {
                    c.getAccessClass().setPropertyValue(new Short((short) 0));
                } else {
                    c.getAccessClass().setPropertyValue(sensitivity);
                }
            } else if (SQL_FIELD_DATE_START.equalsIgnoreCase(column)) {
                if (rs.getTimestamp(i) != null) {
                    dstart = new Date(rs.getTimestamp(i).getTime());
                }
            } else if (SQL_FIELD_SUBJECT.equalsIgnoreCase(column)) {
                c.getSummary().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_RECURRENCE_TYPE.equalsIgnoreCase(column)) {
                recurrenceType = rs.getShort(i);
            } else if (SQL_FIELD_INTERVAL.equalsIgnoreCase(column)) {
                interval = rs.getInt(i);
            } else if (SQL_FIELD_MONTH_OF_YEAR.equalsIgnoreCase(column)) {
                monthOfYear = rs.getShort(i);
            } else if (SQL_FIELD_DAY_OF_MONTH.equalsIgnoreCase(column)) {
                dayOfMonth = rs.getShort(i);
            } else if (SQL_FIELD_DAY_OF_WEEK_MASK.equalsIgnoreCase(column)) {
                String dayOfWeekMaskStr = rs.getString(i);
                if (dayOfWeekMaskStr != null && dayOfWeekMaskStr.length() > 0) {
                    dayOfWeekMask = Short.parseShort(dayOfWeekMaskStr);
                }
            } else if (SQL_FIELD_INSTANCE.equalsIgnoreCase(column)) {
                instance = rs.getShort(i);
            } else if (SQL_FIELD_START_DATE_PATTERN.equalsIgnoreCase(column)) {
                startDatePattern = rs.getString(i);
            } else if (SQL_FIELD_NO_END_DATE.equalsIgnoreCase(column)) {
                String noEndDateStr = null;
                noEndDateStr = rs.getString(i);
                if (noEndDateStr != null && noEndDateStr.length() > 0 ) {
                    if(noEndDateStr.charAt(0) == '1') {
                        noEndDate = true;
                    } else if(noEndDateStr.charAt(0) == '0') {
                        noEndDate = false;
                    }
                }
            } else if (SQL_FIELD_END_DATE_PATTERN.equalsIgnoreCase(column)) {
                endDatePattern = rs.getString(i);
            } else if (SQL_FIELD_OCCURRENCES.equalsIgnoreCase(column)) {
                occurrences = rs.getShort(i);
            } else if (SQL_FIELD_TYPE.equalsIgnoreCase(column)) {
                // Already handled
            } else if (SQL_FIELD_COMPLETED.equalsIgnoreCase(column)) {
                if (rs.getTimestamp(i) != null) {
                    completed = new Date(rs.getTimestamp(i).getTime());
                }
            } else if (SQL_FIELD_PERCENT_COMPLETE.equalsIgnoreCase(column)) {
                if (c instanceof Task) {
                    short percentage = rs.getShort(i);
                    ((Task) c).getPercentComplete().setPropertyValue(
                            String.valueOf(percentage));
                    if (percentage == 100) {
                        ((Task) c).getComplete().setPropertyValue(Boolean.TRUE);
                    } else {
                        ((Task) c).getComplete().setPropertyValue(Boolean.FALSE);
                    }
                }
            } else if (SQL_FIELD_FOLDER.equalsIgnoreCase(column)) {
                c.getFolder().setPropertyValue(rs.getString(i));
            } else if (SQL_FIELD_START_DATE_TIME_ZONE.equalsIgnoreCase(column)) {
                c.getDtStart().setTimeZone(rs.getString(i));
                // @todo Set the same time zone for other non-UTC and 
                //       non-all-day date-time properties
            } else if (SQL_FIELD_END_DATE_TIME_ZONE.equalsIgnoreCase(column)) {
                c.getDtEnd().setTimeZone(rs.getString(i));
            } else if (SQL_FIELD_REMINDER_TIME_ZONE.equalsIgnoreCase(column)) {
                c.getReminder().setTimeZone(rs.getString(i));
            }
            // Unhandled columns are just ignored
        }

        if (dstart != null) {
            c.getDtStart().setPropertyValue(getStringFromDate(aDay, dstart));
        }

        if (dend != null) {
            c.getDtEnd().setPropertyValue(getStringFromDate(aDay, dend));
        }

        if (replyTime != null && (c instanceof Event)) {
            ((Event) c).getReplyTime().setPropertyValue(getStringFromDate(
                    aDay, // @todo or false?
                    replyTime));
        }

        if (completed != null && (c instanceof Task)) {
            ((Task) c).getDateCompleted().setPropertyValue(getStringFromDate(
                    aDay, completed));
        }

        if (reminderTime != null) {
            //
            // Also the reminder time follows the start and end dates convention
            // relative to the all day format: if the all day flag is on then it
            // has to be interpreted as a local time (else in UTC).
            //
            r.setTime(getStringFromDate(aDay, reminderTime));
            if (dstart != null) {
                r.setMinutes((int)((dstart.getTime() - reminderTime.getTime())
                        / (60000))); // 60 seconds in a minutes *
                                     // 1000 millis in a second = 60000
            } else {
                r.setMinutes(0);
            }
        }

        switch (recurrenceType) {

            case RecurrencePattern.TYPE_DAILY:
                rp = RecurrencePattern.getDailyRecurrencePattern(interval,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences,
                        dayOfWeekMask);
                c.setRecurrencePattern(rp);
                break;

            case RecurrencePattern.TYPE_WEEKLY:
                rp = RecurrencePattern.getWeeklyRecurrencePattern(interval ,
                        dayOfWeekMask,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences);
                c.setRecurrencePattern(rp);
                break;

            case RecurrencePattern.TYPE_MONTHLY:
                rp = RecurrencePattern.getMonthlyRecurrencePattern(interval,
                        dayOfMonth,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences);
                c.setRecurrencePattern(rp);
                break;

            case RecurrencePattern.TYPE_MONTH_NTH:
                rp = RecurrencePattern.getMonthNthRecurrencePattern(interval,
                        dayOfWeekMask,
                        instance,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences);
                c.setRecurrencePattern(rp);
                break;

            case RecurrencePattern.TYPE_YEARLY:
                rp = RecurrencePattern.getYearlyRecurrencePattern(interval,
                        dayOfMonth,
                        monthOfYear,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences);
                c.setRecurrencePattern(rp);
                break;

            case RecurrencePattern.TYPE_YEAR_NTH:
                rp = RecurrencePattern.getYearNthRecurrencePattern(interval,
                        dayOfWeekMask,
                        monthOfYear,
                        instance,
                        startDatePattern,
                        endDatePattern,
                        noEndDate,
                        occurrences);
                c.setRecurrencePattern(rp);
                break;

            default:
                //
                // ignore any unknown recurrence pattern type
                //
                break;
        }

        //
        // Set timezone in the RecurrencePattern using the dtStart timezone.
        // Note: the tasks could have only the due date (or neither that)
        //
        if (rp != null) {
            if (!isAnEvent && dend != null) {
                rp.setTimeZone(c.getDtEnd().getTimeZone());
            } else {
                rp.setTimeZone(c.getDtStart().getTimeZone());
            }
            c.setRecurrencePattern(rp);
        }

        return cw;
    }

    private static String getStringFromDate(boolean allDay, Date date)
    throws Exception {
        return (allDay ?
            getStringFromAllDayDateUTC(date) :
            getStringFromDateUTC(date));
    }

    private static String getStringFromAllDayDateUTC(Date date)
    throws Exception {

        SimpleDateFormat allDayUtcDateFormatter =
            new SimpleDateFormat(TimeUtils.PATTERN_UTC_WOZ); // without Z
        allDayUtcDateFormatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        return allDayUtcDateFormatter.format(date);

    }

    private static String getStringFromDateUTC(Date date)
    throws Exception {

        SimpleDateFormat utcDateFormatter =
            new SimpleDateFormat(TimeUtils.PATTERN_UTC);
        utcDateFormatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        return utcDateFormatter.format(date);

    }

    /**
     * Converts a String object representing a date into a corresponding Date
     * object apt to represent a date in the DB.
     *
     * @param allDay true if the information is expected to be in the all-day
     *                    format
     * @param date if allDay is true, it should be in the "yyyy-MM-dd" format:
     *             the proper time will be attached for DB-saving purposes; if
     *             allDay is false, it should be in the "yyyyMMdd'T'HHmmss'Z'"
     *             format, but will be forced into the right format also if it's
     *             in the "yyyyMMdd'T'HHmmss" format (a 'Z' will be appended) or
     *             in the "yyyy-MM-dd" format (in this case, as for all-day
     *             dates, the time will be considered to be equal to the second
     *             parameter)
     * @param time a String object representing the default time in the "HHmmss"
     *             format, to be used when the date is in an untimed format but
     *             this non-all-day is nevertheless used (see above); if it's
     *             null, the "yyyy-MM-dd" format can't be used and if the date
     *             is in that format a null value will be returned
     * @return a Date object
     */
    private static Date getDateFromString(boolean allDay,
                                          String date   ,
                                          String time   )
    throws ParseException {

        if (date == null) { // Preserves null content which in this context is
            return null;    // meaningful
        }
        if (date.length() == 0) { // Considers "" as a null but meaningful value
            return null;
        }

        //
        // Trimming the given date because some devices send it with a space at
        // the beginning
        //
        date = date.trim();

        SimpleDateFormat dateFormatter = new SimpleDateFormat();
        dateFormatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        if (allDay) {
            dateFormatter.applyPattern(TimeUtils.PATTERN_UTC_WOZ);
        } else {
            dateFormatter.applyPattern(TimeUtils.PATTERN_UTC);
        }

        String dateOK;

        String format = TimeUtils.getDateFormat(date);

        if (format.equals(TimeUtils.PATTERN_YYYY_MM_DD) || 
            format.equals(TimeUtils.PATTERN_YYYYMMDD)) {
            if (time == null) { // that means that untimed dates mustn't be
                return null;    // accepted, so a null value is returned
            }
            dateOK = TimeUtils.convertDateFromInDayFormat(date, time, !allDay);
        } else if (format.equals(TimeUtils.PATTERN_UTC_WOZ)) {
            if (allDay) {
                dateOK = date;
            } else {
                dateOK = date + 'Z'; // the non-all-day formatter wants a 'Z'
            }
        } else { // then format should be = TimeUtils.PATTERN_UTC
            dateOK = date;
        }

        // At this point dateOK will be either in the "yyyyMMdd'T'HHmmss'Z'"
        // (non-all-day) or in the "yyyyMMdd'T'HHmmss" (all-day) format.

        return dateFormatter.parse(dateOK);
    }

    /**
     * Converts a String object representing a date into a corresponding Date
     * object apt to represent an all-day date in the DB.
     *
     * @param date should be in the "yyyy-MM-dd" format
     * @param time a String object representing the default time in the "HHmmss"
     *             format
     * @return a Date object
     *
     * @deprecated use getDateFromString(true, date, time) instead
     */
    private static Date getAllDayDateUTCFromString(String date, String time)
            throws Exception {

        if (date == null) { // Preserves null content which in this context is
            return null;    // meaningful
        }
        if (date.length() == 0) { // Considers "" as a null but meaningful value
            return null;
        }

        SimpleDateFormat allDayUtcDateFormatter =
            new SimpleDateFormat(TimeUtils.PATTERN_UTC_WOZ); // without Z
        allDayUtcDateFormatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        return allDayUtcDateFormatter.parse(
                TimeUtils.convertDateFromInDayFormat(date, time, false));
    }

    /**
     * Converts a String object representing a date into a corresponding Date
     * object apt to represent a non-all-day date in the DB.
     *
     * @param date should be in the "yyyyMMdd'T'HHmmss'Z'" format, but will be
     *             forced into the right format also if it's in the
     *             "yyyyMMdd'T'HHmmss" format (a 'Z' will be appended) or in the
     *             "yyyy-MM-dd" format (in this case, the time will be
     *             considered to be equal to the second parameter)
     * @param time a String object representing the default time in the "HHmmss"
     *             format, to be used when the date is in an untimed format but
     *             this non-all-day is nevertheless used (see above); if it's
     *             null, the "yyyy-MM-dd" format can't be used and if the date
     *             is in that format a null value will be returned
     * @return a Date object
     *
     * @deprecated use getDateFromString(true, date, time) instead
     */
    private static Date getDateUTCFromString(String date, String time)
            throws Exception {

        if (date == null) { // Preserves null content which in this context is
            return null;    // meaningful
        }
        if (date.length() == 0) { // Considers "" as a null but meaningful value
            return null;
        }

        Date newDate;
        String format = TimeUtils.getDateFormat(date);

        SimpleDateFormat utcDateFormatter =
            new SimpleDateFormat(TimeUtils.PATTERN_UTC);
        utcDateFormatter.setTimeZone(TimeUtils.TIMEZONE_UTC);

        if (format.equals(TimeUtils.PATTERN_YYYY_MM_DD)) {
            if (time == null) { // That means non-lenient conversion
                return null;
            }
            newDate = utcDateFormatter.parse(
                    TimeUtils.convertDateFromInDayFormat(date, time, true));
        } else if (format.equals(TimeUtils.PATTERN_UTC_WOZ)) {
            newDate = utcDateFormatter.parse(date + "Z");
        } else {
            newDate = utcDateFormatter.parse(date);
        }

        return newDate;
    }

    /**
     * Infers the reminder trigger time from the available information:
     * <ul>
     *  <li>if no start date is set, then the absolute reminder time must be
     *      used;</li>
     *  <li>if a start date is set, then:
     *   <ul>
     *    <li>if the reminder is set to be triggered with a certain advance
     *        relative to the start date, then the trigger time must be computed
     *        using this information;
     *    <li>otherwise, the absolute reminder time must be used.
     *   </ul>
     * </ul>
     *
     * @param dstart the start date of the calendar item (may be null)
     * @param reminder the Reminder object representing the alarm information
     *                 of the calendar item
     * @return the trigger time of the alarm or null if no sufficient
     *         information is available
     */
    private static Timestamp getReminderTime(Date dstart, Reminder reminder) {
        int alarmMinutes = reminder.getMinutes();
        String alarmTime = reminder.getTime();
        if (dstart != null) {
            // Computes the time using the minutes
            java.util.Calendar calAlarm = java.util.Calendar.getInstance();
            calAlarm.setTime(dstart);
            calAlarm.add(java.util.Calendar.MINUTE, -alarmMinutes);
            return new Timestamp(calAlarm.getTimeInMillis());
        } else {
            if (alarmTime == null) {
                return null;
            }
            try {
                return new Timestamp(getDateFromString(false, alarmTime,
                        null) // an alarm must be timed!
                        .getTime());
            } catch (Exception e) {
                return null;
            }
        }
    }

    /**
     * Attaches the exception(s) to the recurrence rule of a calendar on the
     * basis of a ResultSet.
     *
     * @param cw the calendar (as a CalendarWrapper) still lacking information
     *           on the exceptions
     * @param rs the result of the execution of a proper SQL SELECT statement on
     *           the fnbl_pim_calendar_exception table, with the cursor before
     *           its first row
     * @return the CalendarWrapper object with address information attached
     * @throws Exception
     */
    private CalendarWrapper addPIMCalendarExceptions(CalendarWrapper cw,
            ResultSet rs) throws Exception {

        if (cw.getCalendar().getCalendarContent().getRecurrencePattern()
                == null) {
            return cw;
        }

        SortedSet<ExceptionToRecurrenceRule> exceptions = 
                new TreeSet<ExceptionToRecurrenceRule>();
        boolean allDay = cw.getCalendar().getCalendarContent()
                .getAllDay().booleanValue();

        while (rs.next()) {

            String addition = rs.getString(SQL_FIELD_ADDITION);
            boolean isAddition = false;
            if (addition != null && addition.equals("1")) {
                isAddition = true;
            }
            String occurrenceDate;
            try {
                 occurrenceDate = getStringFromDate(allDay, new Date(
                            rs.getTimestamp(SQL_FIELD_OCCURRENCE_DATE)
                            .getTime()));
            } catch (Exception e) {
                throw new SQLException(e.getLocalizedMessage());
            }

            ExceptionToRecurrenceRule etrr =
                    new ExceptionToRecurrenceRule(isAddition, occurrenceDate);
            exceptions.add(etrr);
        }

        cw.getCalendar().getCalendarContent().getRecurrencePattern()
                .setExceptions(exceptions);
        return cw;
    }

    /**
     * Extract the time zone from a date-time property in a safe way.
     *
     * @param property may be null
     * @return if existing, the property's time zone will be returned as a 
     *         String object
     */
    private static String timeZoneFrom(PropertyWithTimeZone property) {
        
        if (property == null) {
            return null;
        }
        return property.getTimeZone();
    }    
    
    /**
     * Return the query string to use to retrieve all the Items belonging to a user
     * @return the query string to use to retrieve all the Items belonging to a user
     */
    @Override     
    protected String getAllItemsQuery() {
        StringBuilder sb = new StringBuilder(SQL_GET_FNBL_PIM_CALENDAR_ID_LIST_BY_USER);
        sb.append(SQL_FILTER_BY_TYPE[type]).append(SQL_ORDER_BY_ID);
        return sb.toString();
    }

    /**
     * Return the query string to use to remove the Item belonging to a user
     * @return the query string to use to remove the Item belonging to a user
     */
    @Override     
    protected String getRemoveItemQuery() {
        return SQL_DELETE_CALENDAR_BY_ID_USERID;
    }
    
    /**
     * Return the query string to use to remove the all Items belonging to a user
     * @return the query string to use to remove the all Items belonging to a user
     */
    @Override     
    protected String getRemoveAllItemsQuery(){
        return SQL_DELETE_CALENDARS_BY_USERID + SQL_FILTER_BY_TYPE[type];
    }
    
    /**
     * Return the query string to use to retrieve the status of an Items 
     * belonging to a user
     * @return the query string to use to retrieve the status of an Items 
     * belonging to a user
     */
    @Override     
    protected String getItemStateQuery() {
        return SQL_GET_STATUS_BY_ID_USER_TIME;
    }
    
    @Override
    protected String getChangedItemsQuery() {
        StringBuilder sb = new StringBuilder(SQL_GET_CHANGED_CALENDARS_BY_USER_AND_LAST_UPDATE);
        sb.append(SQL_FILTER_BY_TYPE[type]).append(" order by id");

        return sb.toString();
    }


    /**
     * Extracts a Short value from the status property of a task object.
     * If any error occurs or the object/property is missing, 0 is returned.
     *
     * @param task the object containing the status property as String
     *
     * @return the short corresponding to the status property.
     */
    private Short getTaskStatus(Task task) {
        Short status = null;
        if(task!=null && task.getStatus()!=null) {
           String tmp = task.getStatus().getPropertyValueAsString();
           if(tmp!=null && tmp.length()>0) {
                try {
                    status = Short.decode(tmp);
                } catch(NumberFormatException e) {

                }
           }
        }
        return status;
    }
    
}
