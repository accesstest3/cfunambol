/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

import com.funambol.common.pim.converter.CalendarStatus;
import java.util.Date;
import junit.framework.TestCase;

/**
 *
 * @version $Id: TaskTest.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class TaskTest extends TestCase {
    
    public TaskTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testTaskCompleted() {
        Task task = new Task();
        assertTaskNotCompleted(task);

        // Checking only one values
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        assertTaskCompleted(task);

        task = new Task();
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        assertTaskCompleted(task);


        // Checking completed date when not completed
        task = new Task();
        task.getDateCompleted().setPropertyValue(new Date());
        assertTaskNotCompleted(task);


        // STATUS
        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getComplete().setPropertyValue(Boolean.FALSE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue("80");
        task.getComplete().setPropertyValue(Boolean.FALSE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.setPercentComplete(null);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.setComplete(null);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.setPercentComplete(null);
        task.setComplete(null);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.setComplete(null);
        assertTaskCompleted(task);



        // COMPLETE
        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue("80");
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.ACCEPTED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.NEEDS_ACTION.getServerValue());
        task.getPercentComplete().setPropertyValue("11");
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);


        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.DELEGATED.getServerValue());
        task.setPercentComplete(null);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.setStatus(null);
        task.getPercentComplete().setPropertyValue("99");
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.setPercentComplete(null);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.setStatus(null);
        task.getPercentComplete().setPropertyValue("100");
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(null);
        task.setPercentComplete(null);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);



        // PERCENT COMPLETE
        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.getComplete().setPropertyValue(Boolean.FALSE);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getStatus().setPropertyValue(CalendarStatus.TENTATIVE.getServerValue());
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getStatus().setPropertyValue(CalendarStatus.CONFIRMED.getServerValue());
        task.getComplete().setPropertyValue(Boolean.FALSE);
        assertTaskCompleted(task);


        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getStatus().setPropertyValue(CalendarStatus.TENTATIVE.getServerValue());
        task.setComplete(null);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.getStatus().setPropertyValue(CalendarStatus.COMPLETED.getServerValue());
        task.setComplete(null);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.setStatus(null);
        task.getComplete().setPropertyValue(Boolean.FALSE);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.setStatus(null);
        task.getComplete().setPropertyValue(Boolean.TRUE);
        assertTaskCompleted(task);

        task = new Task();
        task.getPercentComplete().setPropertyValue(Task.HUNDRED_PERCENT);
        task.setStatus(null);
        task.setComplete(null);
        assertTaskCompleted(task);



    }

    private void assertTaskCompleted(Task task) {
        assertNotNull(task.getStatus());
        assertEquals(CalendarStatus.COMPLETED.getServerValue(), task.getStatus().getPropertyValue());
        assertNotNull(task.getComplete());
        assertEquals(Boolean.TRUE, task.getComplete().getPropertyValue());
        assertNotNull(task.getPercentComplete());
        assertEquals(Task.HUNDRED_PERCENT, task.getPercentComplete().getPropertyValue());
    }

    private void assertTaskNotCompleted(Task task) {
        assertNotNull(task.getStatus());
        if(task.getStatus().getPropertyValue()!=null) {
            assertNotSame(CalendarStatus.COMPLETED.getServerValue(), task.getStatus().getPropertyValue());
        }
        assertNotNull(task.getComplete());
        if(task.getComplete().getPropertyValue()!=null) {
            assertNotSame(Boolean.TRUE, task.getComplete().getPropertyValue());
        }
        assertNotNull(task.getPercentComplete());
        if(task.getPercentComplete().getPropertyValue()!=null) {
            assertNotSame(Task.HUNDRED_PERCENT, task.getPercentComplete().getPropertyValue());
        }
        assertNull(task.getDateCompleted().getPropertyValue());
    }


    

}
