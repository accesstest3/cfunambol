/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2010 Funambol, Inc.
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
/* $Id: JobExecutorTest.java 35930 2010-09-08 11:25:14Z onufryk $ */

package com.funambol.job;

import org.apache.log4j.Logger;

import com.funambol.job.core.TaskExecutionRequest;
import com.funambol.job.core.TaskExecutionRequestResponse;
import com.funambol.job.utils.NotificationReceiver;

import junit.framework.TestCase;

public class JobExecutorTest extends TestCase {
    private static final Logger log = Logger.getLogger(JobExecutorTest.class);
    NotificationReceiver receiver;

    public JobExecutorTest(String testName) {
        super(testName);
        try {
            JobExecutor.getJobExecutor().init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setUp() throws Exception {
        log.info("Starting JobExecutorServiceTest");
        super.setUp();
        receiver = new NotificationReceiver();
        receiver.start();
    }

    protected void tearDown() throws Exception {
        log.info("Stopping JobExecutorServiceTest");
        super.tearDown();
        receiver.stop();
    }
    
    public void testJobExecutorStopped() throws Exception {
        JobExecutor.getJobExecutor().stop();
        try {
            JobExecutor.getJobExecutor().executeTask("id","generic-successful-task", null);
        } catch (Exception e) {
            assertTrue(e instanceof JobExecutorException);
        }
        JobExecutor.getJobExecutor().start();
    }
    
    public void testJobExecutorServiceInitialized() throws Exception {
        assertTrue(JobExecutor.getJobExecutor().isInitialized());
    }
    
    public void testJobExecutorSendTaskObject() throws Exception {
        String id = "user1";
        String taskName = "generic-successful-task";
        
        TaskExecutionRequest taskExecutionRequest = new TaskExecutionRequest(id, taskName);
        
        TaskExecutionRequestResponse response = JobExecutor.getJobExecutor().executeTask(taskExecutionRequest);
        assertNotNull("Expected non-null response", response);
        assertTrue("Expected request to be inserted", response.isAccepted());
    }
    
    public void testJobExecutorServiceSuccess() throws Exception {
        String taskName = "generic-successful-task";
        log.info("Executing a "+taskName+" task");
        TaskExecutionRequestResponse response = JobExecutor.getJobExecutor().executeTask("id",taskName, null);
        // TODO: Here we expect to receive response from Real service or stub
        assertNotNull("Expected non-null response", response);
        assertTrue("Expected request to be inserted", response.isAccepted());
    }
    
    public void testJobExecutorServiceFailure() throws Exception {
        String taskName = "generic-failed-task";
        log.info("Executing a "+taskName+" task");
        TaskExecutionRequestResponse response = JobExecutor.getJobExecutor().executeTask("id",taskName, null);
        // TODO: Here we expect to receive response from Real service or stub
        assertNotNull("Expected non-null response", response);
        assertFalse("Expected request NOT to be inserted", response.isAccepted());
    }
    
    public void testExecuteTaskOnServiceNotInitialized() throws Exception {
        String expClass = null;
        String expMessage = null;
        try {
            JobExecutor.getJobExecutor().stop();
            TaskExecutionRequestResponse response = JobExecutor.getJobExecutor().executeTask("id", "test", null);
        } catch (Exception ex) {
            expClass = ex.getClass().getName();
            expMessage = ex.getMessage();
        }
        assertEquals("com.funambol.job.JobExecutorException", expClass);
        assertEquals("JGroups channel is not open or connected", expMessage);
    }

}

