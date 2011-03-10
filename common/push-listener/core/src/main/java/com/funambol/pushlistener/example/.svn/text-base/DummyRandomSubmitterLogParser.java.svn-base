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

package com.funambol.pushlistener.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 *
 * @version $Id: DummyRandomSubmitterLogParser.java,v 1.2 2007-11-28 11:14:42 nichele Exp $
 */
public class DummyRandomSubmitterLogParser {

    public static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    private enum State {
        WAIT, SCHEDULED, NOTIFIED_NOT_RUNNING, NOTIFIED_RUNNING, RUNNING, ERROR
    }

    // ------------------------------------------------------------ Private data
    private String logFile = null;

    private Map<String, State> statePerId   = new HashMap<String, State>();
    private Map<String, Date> expectedDates = new HashMap<String, Date>();

    private File dumpingFile = null;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of DummyRandomSubmitterLogParser */
    public DummyRandomSubmitterLogParser(String logFile) {
        this.logFile = logFile;
    }

    // ---------------------------------------------------------- Public methods

    public static void main(String[] args) throws Exception {
        if (args == null || (args.length != 1 && args.length != 3)) {
            System.out.println("Use java DummyRandomSubmitterLogParser <logFile> [dump <id>]");
            System.exit(1);
        }

        DummyRandomSubmitterLogParser parser = new DummyRandomSubmitterLogParser(args[0]);
        String dumpId = null;
        if (args.length > 1) {
            dumpId = args[2];

        }
        parser.parse(dumpId);
    }

    // --------------------------------------------------------- Private methods

    private void parse(String dumpId) throws Exception {
        BufferedWriter writer = null;
        if (dumpId != null) {
            System.out.println("Dumping events for " + dumpId);
            dumpingFile = new File("c:\\" + dumpId + ".events.log");
            if (dumpingFile.exists()) {
                dumpingFile.delete();
            }
            writer = new BufferedWriter(new FileWriter(dumpingFile));
        }

        File f = new File(logFile);
        if (!f.isFile()) {
            throw new IllegalArgumentException("'" + f + "' doesn't exist or is not a file");
        }
        System.out.println("Parsing: " + f);
        BufferedReader reader = new BufferedReader(new FileReader(f));
        String line = null;
        while ( (line = reader.readLine()) != null) {
            Event event = Event.parseLine(line);
            if (event == null) {
                continue;
            }
            if (event.getId().equals(dumpId)) {
                dumpEvent(event, writer);
            }
            analyze(event);
        }
        if (dumpingFile != null) {
            writer.close();
        }
    }

    private void analyze(Event event) throws Exception {
        //System.out.println("Analyzing '" + id + "'");

        String id = event.getId();
        State currentState = statePerId.get(id);
        if (currentState == null) {
            currentState = State.WAIT;
        }
        if (currentState.equals(State.ERROR)) {
            return ;
        }
        Date expectedNextExecutionDate = expectedDates.get(id);

       if (Event.Type.EXECUTION_REQUIRED.equals(event.getType()) ||
           Event.Type.DOUBLE_EXECUTION.equals(event.getType())   ||
           Event.Type.GET_PERIOD.equals(event.getType())          ) {
           if (currentState.equals(State.WAIT)) {
               currentState = State.NOTIFIED_NOT_RUNNING;
           } else if (currentState.equals(State.SCHEDULED)) {
               //
               // Nothing to do
               //
           } else if (currentState.equals(State.RUNNING)) {
               currentState = State.NOTIFIED_RUNNING;
           } else if (currentState.equals(State.NOTIFIED_NOT_RUNNING)) {
               currentState = State.NOTIFIED_NOT_RUNNING;
           } else if (currentState.equals(State.NOTIFIED_RUNNING)) {
               currentState = State.NOTIFIED_RUNNING;
           }

       } else if (Event.Type.RUN.equals(event.getType())) {
           if (currentState.equals(State.WAIT)) {
               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' started but not notification was received (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);
               currentState = State.RUNNING;

           } else if (currentState.equals(State.RUNNING)) {
               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' started but it was already running (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);


           } else if (currentState.equals(State.SCHEDULED)) {

               //
               // We have to check if the period is expired
               //
               if (event.getDate().compareTo(expectedNextExecutionDate) < 0) {
                   System.out.println("At " + event.getFormattedDate() + " the task '" +
                                      id + "' started but it was scheduled to start at " +
                                      dateFormat.format(expectedNextExecutionDate) + " (event: " +
                                      event + ")");
                   System.out.println("Stop analysis for id: " + id);
                   //dumpEvents(events, id);
               }
               currentState = State.RUNNING;
               expectedNextExecutionDate = null;
               expectedDates.remove(id);

           } else if (currentState.equals(State.NOTIFIED_NOT_RUNNING)) {

               currentState = State.RUNNING;

           } else if (currentState.equals(State.NOTIFIED_RUNNING)) {
               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' started but it was already running and notified (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);

           }
       } else if (Event.Type.DONE.equals(event.getType())) {

           if (currentState.equals(State.WAIT)) {

               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' ended but it never started (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);
               currentState = State.WAIT;

           } else if (currentState.equals(State.RUNNING)) {

               currentState = State.WAIT;

           } else if (currentState.equals(State.NOTIFIED_NOT_RUNNING)) {

               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' ended but it never started also if it received a notification (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);
               currentState = State.WAIT;
           }  else if (currentState.equals(State.SCHEDULED)) {

               System.out.println("At " + event.getFormattedDate() + " the task '" +
                                  id + "' ended but it was waiting for a scheduled execution (event: " +
                                  event + ")");
               System.out.println("Stop analysis for id: " + id);
               //dumpEvents(events, id);
               currentState = State.WAIT;
           } else if (currentState.equals(State.NOTIFIED_RUNNING)) {

               currentState = State.SCHEDULED;
               Calendar calEvent = Calendar.getInstance();

               calEvent.setTime(event.getDate());
               //calEvent.add(Calendar.MINUTE, 1);
               calEvent.add(Calendar.SECOND, 58);

               expectedNextExecutionDate = calEvent.getTime();
               expectedDates.put(id, expectedNextExecutionDate);

           } else {
               //
               // Not important event....nothing to do
               //
               return;
           }

       }

        statePerId.put(id, currentState);
        //System.out.println("Events per '" + id + "' are OK");
    }

    private void dumpEvent(Event event, BufferedWriter writer) throws Exception {

        writer.write(event.toLine());
        writer.newLine();

        writer.flush();
    }


}

class Event {

    // --------------------------------------------------------------- Constants
    public static String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss,SSS";

    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    public enum Type {
        UNKNOWN, RUN, DONE, EXECUTION_REQUIRED, GET_PERIOD, EXECUTION_PLANNED, DOUBLE_EXECUTION
    }

    // ------------------------------------------------------------ Private data
    private String id;
    private Date date;
    private Type type;
    private String message;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public String getFormattedDate() {
        return dateFormat.format(date);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    // ------------------------------------------------------------ Constructors
    public Event(String id, Date date, Type type, String message) {
        this.id = id;
        this.date = date;
        this.type = type;
        this.message = message;
    }

    // ---------------------------------------------------------- Public methods

    // [2007-08-12 18:29:57,250] '21bbb' - execution required - [23446]
    // [2007-08-12 18:29:57,250] '21bbb' - execution planned - [23446] [asap]
    // [2007-08-12 18:29:57,250] '21bbb' - run - [9] seconds [com.funambol.pushlistener.example.RandomSubmitterTask@22331[id=21bbb,executionSeconds=9]]
    // [2007-08-12 18:29:59,703] '21bbb' - execution required - [23576]
    // [2007-08-12 18:29:59,703] '21bbb' - execution planned - [23576] [delayed]
    // [2007-08-12 18:30:00,984] '21bbb' - execution required - [23574]
    // [2007-08-12 18:30:00,984] '21bbb' - execution planned - [23574] [delayed]
    // [2007-08-12 18:30:02,953] '21bbb' - execution required - [23493]
    // [2007-08-12 18:30:02,953] '21bbb' - execution planned - [23493] [delayed]
    // [2007-08-12 18:30:06,250] '21bbb' - done - [com.funambol.pushlistener.example.RandomSubmitterTask@22331[id=21bbb,executionSeconds=9]]
    public static Event parseLine(String line) throws Exception {
        String sDate = line.substring(1, DATE_PATTERN.length() + 1);

        Date date = dateFormat.parse(sDate);
        int indexof = line.indexOf('\'');
        int indexofEnd = line.indexOf('\'', indexof + 1);

        if (indexof == -1 || indexofEnd == -1) {
            //
            // no event line
            //
            return null;
        }
        String id = line.substring(indexof + 1, indexofEnd);
        Type type;

        //
        // Very simple implementation
        //
        if (line.contains("- execution required -")) {
            type = Type.EXECUTION_REQUIRED;
        } else if (line.contains("- done -")) {
            type = Type.DONE;
        } else if (line.contains("- run -")) {
           type = Type.RUN;
        } else if (line.contains("- execution planned -")) {
           type = Type.EXECUTION_PLANNED;
        } else if (line.contains("- getperiod -")) {
           type = Type.GET_PERIOD;
        } else if (line.contains("- double execution required -")) {
           type = Type.DOUBLE_EXECUTION;
        }else {
           type = Type.UNKNOWN;
        }

        String msg = line.substring(indexofEnd + 1);

        Event event  = new Event(id, date, type, msg);
        //System.out.println("Event: " + event);
        return event;
    }

    /**
     * A string representation of this ScheduledTaskWrapper
     *
     * @return a string representation of this ScheduledTaskWrapper
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("id"  , id);
        sb.append("date", dateFormat.format(date));
        sb.append("type", type);
        return sb.toString();
    }

    public String toLine() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        sb.append(dateFormat.format(date));
        sb.append(']');
        sb.append(" '").append(id).append("'").append(message);
        return sb.toString();
    }
}
