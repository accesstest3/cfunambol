/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.List;
import javax.naming.NamingException;

import junit.framework.TestCase;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.funambol.common.pim.calendar.Calendar;
import com.funambol.common.pim.calendar.CalendarContent;
import com.funambol.common.pim.calendar.Task;
import com.funambol.common.pim.calendar.Event;
import com.funambol.common.pim.common.Property;

import com.funambol.foundation.items.model.CalendarWrapper;
import com.funambol.foundation.util.TestDef;
import com.funambol.tools.database.DBHelper;

/**
 * PimCalendarDAO test cases. All the tests are performed with userid = user_part1
 * @version $Id: PIMCalendarDAOTest.java,v 1.1 2008-06-30 10:47:33 testa Exp $
 */
public class PIMCalendarDAOTest extends TestCase implements TestDef {

    // ----------------------------------------------------- Private static data
    private static final String TEST_DATA_DIR =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMCalendarDAO";

    private static final String SQL_DELETE_FNBL_CALENDAR = "delete from fnbl_pim_calendar;";

    private static final String INITIAL_DB_DATASET_USER_PART1
        = TEST_DATA_DIR + "/initial-db-dataset-part1.xml";

    private static final String EXPECTED_ADD_TASK
        = TEST_DATA_DIR + "/store-task-dataset-part1.xml";

    private static final String EXPECTED_ADD_EVENT
        = TEST_DATA_DIR + "/store-event-dataset-part1.xml";

    private static final String EXPECTED_UPDATE_TASK
        = TEST_DATA_DIR + "/update-task-dataset-part1.xml";

    private static final String EXPECTED_UPDATE_EVENT
        = TEST_DATA_DIR + "/update-event-dataset-part1.xml";

	/**
	 * The user id used for tests
	 */
	private static final String USER_PART1 = DBHelper.USER_PART1;

    // ------------------------------------------------------ Static initializer

    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE, 
                                                      USER_SCHEMA_SOURCE,
                                                      false);

            //assertTrue checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private PIMCalendarDAO calendarDAO = null;

    // ------------------------------------------------------------- Constructor

	/**
	 * Constructor
	 * @param testName
	 */
	public PIMCalendarDAOTest(String testName) {
        super(testName);
        calendarDAO = new PIMCalendarDAO(USER_PART1, Task.class);
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getTwinItems method, of class PIMCalendarDAO.
	 * @throws java.lang.Exception 
	 */
    public void testGetTwinItems_for_Task() throws Exception {

        fillCalendarTableWithTasks();

        // There is a stored Task with same Summary, StartDate and DueDate
        Task task = new Task();
        task.getSummary().setPropertyValue("Test the task twin search");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        Calendar calendar = new Calendar();
        calendar.setCalendarContent(task);
        List<String> twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1001)", "1001", (String)twins.get(0));
		
        // There is a stored Task with same Summary and DueDate but with
        // empty StartDate
        task = new Task();
        task.getSummary().setPropertyValue("Test the task twin search");
        task.getDueDate().setPropertyValue("20080627T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1001)", "1001", (String)twins.get(0));
	
        // There is a stored Task with same Summary and DueDate, and empty
        // StartDate
        task = new Task();
        task.getSummary().setPropertyValue("Use DBUnit to insert items");
        task.getDueDate().setPropertyValue("20080627T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1002)", "1002", (String)twins.get(0));

        // There is a stored Task with same Summary and DueDate, and empty
        // StartDate
        task = new Task();
        task.getSummary().setPropertyValue("Use DBUnit to insert items");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1002)", "1002", (String)twins.get(0));

        // There is a stored Task with same Summary and empty DueDate
        task = new Task();
        task.getSummary().setPropertyValue("Test a task with empty due date");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1003)", "1003", (String)twins.get(0));

        // There is a stored Task with same dueDate
        task = new Task();
        task.getDueDate().setPropertyValue("20080627T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1005)", "1005", (String)twins.get(0));
	
        // There is a stored Task with same Summary and empty DueDate but empty
        // StartDate
        task = new Task();
        task.getSummary().setPropertyValue("Test a task with empty due date");
        task.getDtStart().setPropertyValue("20080623T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 1003)", "1003", (String)twins.get(0));
	
        // There is a stored Task with same Summary but empty DueDate and
        // StartDate
        task = new Task();
        task.getSummary().setPropertyValue("Test a task with empty due date");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // There is a stored Task with same Summary but empty DueDate and
        // StartDate
        task = new Task();
        task.getDtStart().setPropertyValue("20080623T000000");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // Providing a null calendar
        twins = calendarDAO.getTwinItems(null);
        assertTrue("No twin found", twins.isEmpty());

        // Providing a null calendar content
        calendar = new Calendar();
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        task = new Task();
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        task = new Task();
        task.getActualWork().setPropertyValue("actua work");
        task.getComplete().setPropertyValue("false");
        task.getBillingInformation().setPropertyValue("billing information");
        task.getStatus().setPropertyValue("4");
        calendar = new Calendar();
        calendar.setCalendarContent(task);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());
    }
	
    /**
     * This test is mostly about the handling of the status field
     * @throws Throwable
     */
    public void testAddTask_StatusField() throws Throwable {
        try {
            Task task = new Task();
            task.getSummary().setPropertyValue("Test the task adding, default status");
            task.getDtStart().setPropertyValue("20080623T000000");
            task.getDueDate().setPropertyValue("20080627T000000");
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1009", USER_PART1, calendar);
            calendarDAO.addItem(cw);

            // Adding a task with an allowed value set as status property
            task = new Task();
            task.getSummary().setPropertyValue("Test the task adding");
            task.getDtStart().setPropertyValue("20090623T000000");
            task.getDueDate().setPropertyValue("20110927T000000");
            task.getStatus().setPropertyValue("2"); // completed
            calendar = new Calendar();
            calendar.setCalendarContent(task);
            cw = new CalendarWrapper("1010", USER_PART1, calendar);
            calendarDAO.addItem(cw);

            // Adding a task with a bad value as status property
            task = new Task();
            task.getSummary().setPropertyValue("Test the task adding");
            task.getDtStart().setPropertyValue("20090623T000000");
            task.getDueDate().setPropertyValue("20110927T000000");
            task.getStatus().setPropertyValue("bad value");
            calendar = new Calendar();
            calendar.setCalendarContent(task);
            cw = new CalendarWrapper("1011", USER_PART1, calendar);
            calendarDAO.addItem(cw);
    
        } catch (Throwable e ) {
            e.printStackTrace();
            fail("An error occurred adding task item.");
        }
        checkExpected(EXPECTED_ADD_TASK);
    }

    /**
     * This test is only for checking the correct handling of meeting status field
     * for the event after the introduction of the status field of the task.
     *
     * @throws Throwable
     */
    public void testAddEvent_StatusField() throws Throwable {
        try {
            Event event = new Event();
		    event.getSummary().setPropertyValue("Meeting about twin detection");
            event.getDtStart().setPropertyValue("20080627T100000");
		    event.getDtEnd().setPropertyValue("20080627T103000");
		    event.getStatus().setPropertyValue("234");
            event.setMeetingStatus((short)3);
            Calendar calendar = new Calendar();
		    calendar.setCalendarContent(event);
            CalendarWrapper cw = new CalendarWrapper("2010", USER_PART1, calendar);
            calendarDAO.addItem(cw);
        } catch (Throwable e ) {
            e.printStackTrace();
            fail("An error occurred adding event item.");
        }
        checkExpected(EXPECTED_ADD_EVENT);
    }

    public void testUpdateTask_StatusField() throws Throwable {
        // load an initial set
        DBHelper.createDatabaseTester(USER_PART1, INITIAL_DB_DATASET_USER_PART1, true);
        
        try {
            Task task = new Task();
            task.getSummary().setPropertyValue("Test the task updating");
            task.getDtStart().setPropertyValue("20090711T000000");
            task.getDueDate().setPropertyValue("20130927T000000");
            task.getStatus().setPropertyValue("4"); // completed
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1010", USER_PART1, calendar);
            calendarDAO.updateItem(cw);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }

        try {
            Task task = new Task();
            task.setMileage(new Integer(3934));
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1011", USER_PART1, calendar);
            calendarDAO.updateItem(cw);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }

        // check
        checkExpected(EXPECTED_UPDATE_TASK);
    }

    public void testUpdateEvent_StatusField() throws Throwable {
        // load an initial set
        DBHelper.createDatabaseTester(USER_PART1, INITIAL_DB_DATASET_USER_PART1, true);

        try {
            Event event = new Event();
            event.getSummary().setPropertyValue("Test the event updating");
            event.getDtStart().setPropertyValue("20100711T080000");
            event.getDtEnd().setPropertyValue("20100711T083000");
            event.getStatus().setPropertyValue("18"); // completed
            event.setMeetingStatus((short)5);
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(event);
            CalendarWrapper cw = new CalendarWrapper("2010", USER_PART1, calendar);
            calendarDAO.updateItem(cw);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }

        try {
            Event event = new Event();
            event.setMileage(new Integer(3934));
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(event);
            CalendarWrapper cw = new CalendarWrapper("2011", USER_PART1, calendar);
            calendarDAO.updateItem(cw);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }

        // check
        checkExpected(EXPECTED_UPDATE_EVENT);
    }

    public void testGetItem_Task() throws Throwable {
        // load an initial set
        DBHelper.createDatabaseTester(USER_PART1, INITIAL_DB_DATASET_USER_PART1, true);

        try {
            CalendarWrapper result   = calendarDAO.getItem("1010");
            Task expectedTask        = new Task();
            expectedTask.setAllDay(Boolean.FALSE);
            expectedTask.getDuration().setPropertyValue("0");
            expectedTask.getDtStart().setPropertyValue("20090623T000000Z");
            expectedTask.getDueDate().setPropertyValue("20110927T000000Z");
            expectedTask.getStatus().setPropertyValue("2");
            expectedTask.getSummary().setPropertyValue("Test the task adding");
            expectedTask.getAccessClass().setPropertyValue("0");
            expectedTask.getComplete().setPropertyValue("false");
            expectedTask.getPercentComplete().setPropertyValue("0");
            CalendarWrapper expected = new CalendarWrapper("1010", USER_PART1, new Calendar(expectedTask));
            expected.setStatus('N');
            assertEquals(expected,result);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testGetItem_Event() throws Throwable {
        // load an initial set
        DBHelper.createDatabaseTester(USER_PART1, INITIAL_DB_DATASET_USER_PART1, true);

        try {
            CalendarWrapper result   = calendarDAO.getItem("2010");
            Event expectedEvent        = new Event();
            expectedEvent.setAllDay(Boolean.FALSE);
            expectedEvent.getDuration().setPropertyValue("0");
            expectedEvent.getDtStart().setPropertyValue("20080627T100000Z");
            expectedEvent.getDtEnd().setPropertyValue("20080627T103000Z");
            expectedEvent.setMeetingStatus((short)3);
            expectedEvent.getSummary().setPropertyValue("Meeting about dato testing");
            expectedEvent.getAccessClass().setPropertyValue("0");
            CalendarWrapper expected = new CalendarWrapper("2010", USER_PART1, new Calendar(expectedEvent));
            expected.setStatus('N');
            assertEquals(expected,result);
        } catch (Throwable e) {
            e.printStackTrace();
            throw e;
        }
    }

   /**
    *
    * @throws java.lang.Exception
    */
    public void testGetTwinItems_for_Events() throws Exception {
        fillCalendarTableWithEvents();

        // There is a stored Event with same Summary, StartDate and EndDate
	Event event = new Event();
	event.getSummary().setPropertyValue("Meeting about twin detection");
        event.getDtStart().setPropertyValue("20080627T100000");
	event.getDtEnd().setPropertyValue("20080627T103000");
	Calendar calendar = new Calendar();
	calendar.setCalendarContent(event);
        List<String> twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2001)", "2001", (String)twins.get(0));

        // There is a stored Event with same Summary and StartDate but with a not
	// empty EndDate
	event = new Event();
	event.getSummary().setPropertyValue("Meeting about twin detection");
        event.getDtStart().setPropertyValue("20080627T100000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // There is a stored Event with same Summary and EndDate but with a not
	// empty StartDate
	event = new Event();
	event.getSummary().setPropertyValue("Meeting about twin detection");
	event.getDtEnd().setPropertyValue("20080627T103000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // There is a stored Event with same Summary and EndDate and empty
	// StartDate
	event = new Event();
	event.getSummary().setPropertyValue("Twin detection bug review");
	event.getDtEnd().setPropertyValue("20080629T110000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2002)", "2002", (String)twins.get(0));

        // There is a stored Event with same Summary and EndDate but with empty
	// StartDate
	event = new Event();
	event.getSummary().setPropertyValue("Twin detection bug review");
	event.getDtStart().setPropertyValue("20080629T103000");
	event.getDtEnd().setPropertyValue("20080629T110000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());
	
        // There is a stored Event with same Summary and EndDate but with empty
	// StartDate
	event = new Event();
	event.getSummary().setPropertyValue("Twin detection bug review");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // There is a stored Event with same Summary and EndDate but with empty
	// StartDate
	event = new Event();
	event.getDtStart().setPropertyValue("20080629T103000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        
        assertTrue("No twin found", twins.isEmpty());



        // Event with empty summary and not empty dt start
	event = new Event();
        event.getDtStart().setPropertyValue("20090702T100000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
	twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2004)", "2004", (String)twins.get(0));



        // Event with empty summary and not empty dt end
	event = new Event();
	event.getDtEnd().setPropertyValue("20090629T110000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
	twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2005)", "2005", (String)twins.get(0));


        // Event with not empty StartDate and EndDate but empty summary
	event = new Event();
        event.getDtStart().setPropertyValue("20090627T100000");
	event.getDtEnd().setPropertyValue("20090627T103000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
	twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2006)", "2006", (String)twins.get(0));

        // Event with not empty StartDate and EndDate but empty summary
	event = new Event();
        event.getDtStart().setPropertyValue("20080627T100000");
	event.getDtEnd().setPropertyValue("20090627T103000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
	twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        // There is a stored Event with same Summary and EndDate but with empty
	// StartDate
	event = new Event();
	event.getDtEnd().setPropertyValue("20080629T110000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());
	
	// There is a stored Event with same Summary and StartDate and empty
	// EndDate
	// WARNING: due to a Fix for Siemens S56 end date issue only for event
	// the EndDate must be equal to the StartDate
	event = new Event();
	event.getSummary().setPropertyValue("Twin detection release");
        event.getDtStart().setPropertyValue("20080702T100000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue(twins.size() == 1);
        assertEquals("Found twin (id 2003)", "2003", (String)twins.get(0));

        // There is a stored Event with same Summary and StartDate but with empty
	// EndDate
	event = new Event();
	event.getSummary().setPropertyValue("Twin detection release");
        event.getDtStart().setPropertyValue("20080702T100000");
	event.getDtEnd().setPropertyValue("20080629T110000");
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        twins = calendarDAO.getTwinItems(null);
        assertTrue("No twin found", twins.isEmpty());

        calendar = new Calendar();
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        event = new Event();
	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());

        event = new Event();
	event.getDescription().setPropertyValue("Not valid event");
        event.getDuration().setPropertyValue("1D");

	calendar = new Calendar();
	calendar.setCalendarContent(event);
        twins = calendarDAO.getTwinItems(calendar);
        assertTrue("No twin found", twins.isEmpty());
   }


    public void testIsTwinSearchAppliableOn_NullValues() throws ParseException {
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(null));

        Calendar c = new Calendar();
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));
    }

    public void testIsTwinSearchAppliableOn_NotValidTask() throws ParseException {
        Calendar c = new Calendar();
        Task task = new Task();
        task.getComplete().setPropertyValue("false");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getActualWork().setPropertyValue("actua work");
        task.getComplete().setPropertyValue("false");
        task.getBillingInformation().setPropertyValue("billing information");
        task.getStatus().setPropertyValue("4");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));
    
        task = new Task();
        task.getDtStart().setPropertyValue("20080623T000000");
      	c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getDtStart().setPropertyValue("");
	c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
       	task.getDueDate().setPropertyValue("");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("");
        task.getDtStart().setPropertyValue("");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getDtStart().setPropertyValue("");
        task.getDueDate().setPropertyValue("");
	c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("");
       	task.getDueDate().setPropertyValue("");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("");
        task.getDtStart().setPropertyValue("");
        task.getDueDate().setPropertyValue("");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("");
        task.getDtStart().setPropertyValue("");
        task.getDueDate().setPropertyValue("");
        task.getActualWork().setPropertyValue("actua work");
        task.getComplete().setPropertyValue("false");
        task.getBillingInformation().setPropertyValue("billing information");
        task.getStatus().setPropertyValue("4");
        c.setCalendarContent(task);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));
    }


    public void testIsTwinSearchAppliableOn_NotValidEvent() throws ParseException {
        Calendar c = new Calendar();
        Event event = new Event();
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDescription().setPropertyValue("Not valid event");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("Task Summary");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDescription().setPropertyValue("Not valid event");
        event.getDuration().setPropertyValue("1D");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDtStart().setPropertyValue("");
	c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
       	event.getDtEnd().setPropertyValue("");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("");
        event.getDtStart().setPropertyValue("");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDtStart().setPropertyValue("");
        event.getDtEnd().setPropertyValue("");
	c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("");
       	event.getDtEnd().setPropertyValue("");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("");
        event.getDtStart().setPropertyValue("");
        event.getDtEnd().setPropertyValue("");
        c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDuration().setPropertyValue("0");
        event.getDtStart().setPropertyValue("");
        event.getDtEnd().setPropertyValue("");
        event.setMeetingStatus((short)3);
        event.getSummary().setPropertyValue("");
        event.getAccessClass().setPropertyValue("0");
                    c.setCalendarContent(event);
        assertFalse(this.calendarDAO.isTwinSearchAppliableOn(c));
    }

    public void testIsTwinSearchAppliableOn_ValidTask() throws ParseException {
        Calendar c = new Calendar();
        Task task = new Task();
        task.getSummary().setPropertyValue("Task Summary");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));
    
        task = new Task();
       	task.getDueDate().setPropertyValue("20080627T000000");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("Task Summary");
        task.getDtStart().setPropertyValue("20080623T000000");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("Task Summary");
       	task.getDueDate().setPropertyValue("20080627T000000");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("Task Summary");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        task = new Task();
        task.getSummary().setPropertyValue("Task Summary");
        task.getDtStart().setPropertyValue("20080623T000000");
        task.getDueDate().setPropertyValue("20080627T000000");
        task.getActualWork().setPropertyValue("actua work");
        task.getComplete().setPropertyValue("false");
        task.getBillingInformation().setPropertyValue("billing information");
        task.getStatus().setPropertyValue("4");
        c.setCalendarContent(task);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));
    }

    public void testIsTwinSearchAppliableOn_ValidEvent() throws ParseException {
        Calendar c = new Calendar();
        Event event = new Event();
        event.getDtStart().setPropertyValue("20080623T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
       	event.getDtEnd().setPropertyValue("20080627T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("Task Summary");
        event.getDtStart().setPropertyValue("20080623T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDtStart().setPropertyValue("20080623T000000");
        event.getDtEnd().setPropertyValue("20080627T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("Task Summary");
       	event.getDtEnd().setPropertyValue("20080627T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getSummary().setPropertyValue("Task Summary");
        event.getDtStart().setPropertyValue("20080623T000000");
        event.getDtEnd().setPropertyValue("20080627T000000");
        c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));

        event = new Event();
        event.getDuration().setPropertyValue("0");
        event.getDtStart().setPropertyValue("20080627T100000Z");
        event.getDtEnd().setPropertyValue("20080627T103000Z");
        event.setMeetingStatus((short)3);
        event.getSummary().setPropertyValue("Meeting about dato testing");
        event.getAccessClass().setPropertyValue("0");
                    c.setCalendarContent(event);
        assertTrue(this.calendarDAO.isTwinSearchAppliableOn(c));
    }

    // ------------------------------------------------------- Protected methods

	@Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        clearTable();
    }

    // --------------------------------------------------------- Private methods

    public static ITable createITableFromQuery(String tableName,
                                               String query,
                                               DatabaseConnection connection)
    throws DataSetException, SQLException {
        return connection.createQueryTable(tableName, query);
    }

    public static ITable loadITableFromFile(String tableName,
                                            String fileName)
    throws IOException, DataSetException {
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(fileName));
        return expectedDataSet.getTable(tableName);
    }

    private void fillCalendarTableWithTasks() throws Exception {

        {
            // Task with not empty StartDate and DueDate
            Task task = new Task();
            task.getSummary().setPropertyValue("Test the task twin search");
            task.getDtStart().setPropertyValue("20080623T000000");
            task.getDueDate().setPropertyValue("20080627T000000");
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1001", USER_PART1, calendar);
            calendarDAO.addItem(cw);
        }
        {
            // Task with empty StartDate
            Task task = new Task();
            task.getSummary().setPropertyValue("Use DBUnit to insert items");
            task.getDueDate().setPropertyValue("20080627T000000");
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1002", USER_PART1, calendar);
            calendarDAO.addItem(cw);
        }
        {
            // Task with empty StartDate and DueDate
            Task task = new Task();
            task.getSummary().setPropertyValue("Test a task with empty due date");
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1003", USER_PART1, calendar);
            calendarDAO.addItem(cw);
        }
        {
            // Task with empty StartDate
            Task task = new Task();
            task.getDueDate().setPropertyValue("20080627T000000");
            Calendar calendar = new Calendar();
            calendar.setCalendarContent(task);
            CalendarWrapper cw = new CalendarWrapper("1005", USER_PART1, calendar);
            calendarDAO.addItem(cw);
        }
    }

	/**
	 *
	 * @throws java.lang.Exception
	 */
    private void fillCalendarTableWithEvents() throws Exception {
        // Event with not empty StartDate and EndDate
        Event event = new Event();
        event.getSummary().setPropertyValue("Meeting about twin detection");
        event.getDtStart().setPropertyValue("20080627T100000");
        event.getDtEnd().setPropertyValue("20080627T103000");
        Calendar calendar = new Calendar();
        calendar.setCalendarContent(event);
        CalendarWrapper cw = new CalendarWrapper("2001", USER_PART1, calendar);
        calendarDAO.addItem(cw);

        // Event with empty StartDate
        event = new Event();
        event.getSummary().setPropertyValue("Twin detection bug review");
        event.getDtEnd().setPropertyValue("20080629T110000");
        calendar = new Calendar();
        calendar.setCalendarContent(event);
        cw = new CalendarWrapper("2002", USER_PART1, calendar);
        calendarDAO.addItem(cw);

        // Event with empty EndDate
        event = new Event();
        event.getSummary().setPropertyValue("Twin detection release");
        event.getDtStart().setPropertyValue("20080702T100000");
        calendar = new Calendar();
        calendar.setCalendarContent(event);
        cw = new CalendarWrapper("2003", USER_PART1, calendar);
        calendarDAO.addItem(cw);

        // Event with empty summary and not empty dt start
        event = new Event();
        event.getDtStart().setPropertyValue("20090702T100000");
        calendar = new Calendar();
        calendar.setCalendarContent(event);
        cw = new CalendarWrapper("2004", USER_PART1, calendar);
        calendarDAO.addItem(cw);

        // Event with empty summary and not empty dt end
        event = new Event();
        event.getDtEnd().setPropertyValue("20090629T110000");
        calendar = new Calendar();
        calendar.setCalendarContent(event);
        cw = new CalendarWrapper("2005", USER_PART1, calendar);
        calendarDAO.addItem(cw);

        // Event with not empty StartDate and EndDate but empty summary
        event = new Event();
        event.getDtStart().setPropertyValue("20090627T100000");
        event.getDtEnd().setPropertyValue("20090627T103000");
        calendar = new Calendar();
        calendar.setCalendarContent(event);
        cw = new CalendarWrapper("2006", USER_PART1, calendar);
        calendarDAO.addItem(cw);


    }

    private void clearTable() throws NamingException {
        Connection conn = null;
        try {
            conn = DBHelper.getUserConnection(USER_PART1);
            DBHelper.executeStatement(conn, SQL_DELETE_FNBL_CALENDAR);
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            DBHelper.closeConnection(conn);
        }
    }

    private void checkExpected(String fileNameContainingExpectedResult)
    throws Throwable {

        DBHelper.assertEqualsDataSet(USER_PART1, "fnbl_pim_calendar", fileNameContainingExpectedResult, new String[] {"LAST_UPDATE"});

    }

    private void assertEquals(CalendarWrapper expected,
                              CalendarWrapper result) {
        if(expected==null) {
            assertNull(result);
        } else {
            assertNotNull(result);
            assertEquals("Wrong calendar id",
                            expected.getId(),
                            result.getId());
            assertEquals("Wrong calendar status",
                         expected.getStatus(),
                         result.getStatus());
            assertEquals("Wrong calendar userid",
                         expected.getUserId(),
                         result.getUserId());
            if(expected.getCalendar()==null ) {
                assertNull(result.getCalendar());
            } else {
                if(expected.getCalendar().getTask()!=null) {
                    assertEquals(expected.getCalendar().getTask(),
                                 result.getCalendar().getTask());
                } else {
                    assertEquals(expected.getCalendar().getEvent(),
                                 result.getCalendar().getEvent());
                }
            }
        }
    }

    private void assertEquals(Event expected, Event result) {
        assertEquals((CalendarContent) expected, (CalendarContent) result);
        if (expected != null) {
            assertEquals("Wrong reply time property",
                expected.getReplyTime(),
                result.getReplyTime());
            assertEquals("Wrong transp property",
                expected.getTransp(),
                result.getTransp());
        }
    }

    private void assertEquals(Task expected, Task result) {
        assertEquals((CalendarContent)expected,(CalendarContent)result);
        if(expected!=null) {
            assertEquals("Wrong actual work property",
                        expected.getActualWork(),
                        result.getActualWork());
            assertEquals("Wrong billing information property",
                          expected.getBillingInformation(),
                          result.getBillingInformation());
            assertEquals("Wrong complete property",
                         expected.getComplete(),
                         result.getComplete());
            assertEquals("Wrong date completed property",
                         expected.getDateCompleted(),
                         result.getDateCompleted());
            assertEquals("Wrong due date property",
                         expected.getDueDate(),
                         result.getDueDate());
            assertEquals("Wrong importance property",
                         expected.getImportance(),
                         result.getImportance());
            assertEquals("Wrong Owner property",
                         expected.getOwner(),
                         result.getOwner());
            assertEquals("Wrong percent complete property",
                         expected.getPercentComplete(),
                         result.getPercentComplete());
            assertEquals("Wrong sensitivity property",
                        expected.getSensitivity(),
                        result.getSensitivity());
            assertEquals("Wrong team task property",
                         expected.getTeamTask(),
                         result.getTeamTask());
            assertEquals("Wrong total work property",
                         expected.getTotalWork(),
                         result.getTotalWork());
        }
    }

    private void assertEquals(CalendarContent expected, CalendarContent result) {
         if(expected==null) {
            assertNull("Result must be null!",result);
        } else {
          assertNotNull("Result task must not be null.",result);
          assertEquals("Wrong access class property",
                       expected.getAccessClass(),
                       result.getAccessClass());
          assertEquals("Wrong allDay property",
                       expected.getAllDay(),
                       result.getAllDay());
          assertEquals("Wrong busy status property",
                       expected.getBusyStatus(),
                       result.getBusyStatus());
          assertEquals("Wrong categories property",
                        expected.getCategories(),
                        result.getCategories());
          assertEquals("Wrong contact property",
                       expected.getContact(),
                       result.getContact());
          assertEquals("Wrong created property",
                       expected.getCreated(),
                       result.getCreated());
          assertEquals("Wrong description property",
                       expected.getDescription(),
                       result.getDescription());
          assertEquals("Wrong dt end property",
                       expected.getDtEnd(),
                       result.getDtEnd());
          assertEquals("Wrong dt start property",
                       expected.getDtStart(),
                       result.getDtStart());
          assertEquals("Wrong duration property",
                       expected.getDuration(),
                       result.getDuration());
          assertEquals("Wrong folder property",
                       expected.getFolder(),
                       result.getFolder());
          assertEquals("Wrong last modified property",
                       expected.getLastModified(),
                       result.getLastModified());
          assertEquals("Wrong latitude property",
                        expected.getLatitude(),
                        result.getLatitude());
          assertEquals("Wrong location property",
                       expected.getLocation(),
                       result.getLocation());
          assertEquals("Wrong longitude property",
                       expected.getLongitude(),
                       result.getLongitude());
          assertEquals("Wrong meeting status property",
                       expected.getMeetingStatus(),
                       result.getMeetingStatus());
          assertEquals("Wrong mileage property",
                       expected.getMileage(),
                       result.getMileage());
          assertEquals("Wrong organizer property",
                       expected.getOrganizer(),
                       result.getOrganizer());
          assertEquals("Wrong priority property",
                       expected.getPriority(),
                       result.getPriority());
          assertEquals("Wrong sequence property",
                       expected.getSequence(),
                       result.getSequence());
          assertEquals("Wrong status property",
                       expected.getStatus(),
                       result.getStatus());
          assertEquals("Wrong summary property",
                       expected.getSummary(),
                       result.getSummary());
          assertEquals("Wrong uid property",
                       expected.getUid(),
                       result.getUid());
          assertEquals("Wrong url property",
                       expected.getUrl(),
                       result.getUrl());
        }
    }

    private void assertEquals(String message,Property expected, Property result) {
        if(expected==null) {
            assertNull(message,result);
        } else {
            assertEquals(message,
                         expected.getPropertyValueAsString(),
                         result.getPropertyValueAsString());
        }
    }
}