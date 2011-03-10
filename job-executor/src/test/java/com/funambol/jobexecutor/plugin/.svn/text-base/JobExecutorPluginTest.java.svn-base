/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
package com.funambol.jobexecutor.plugin;

import com.funambol.MockJobExecutorTask;
import com.funambol.job.plugin.JobExecutorPlugin;
import com.funambol.MockPushListenerInterface;

import com.funambol.framework.notification.NotificationException;
import com.funambol.job.JobExecutor;
import com.funambol.job.core.TaskExecutionRequestResponse;


import com.funambol.pushlistener.framework.plugin.PushListenerPluginException;
import java.io.File;
import java.util.HashMap;
import junit.framework.TestCase;

/**
 *
 * @version $Id: JobExecutorPluginTest.java 35046 2010-07-14 14:22:58Z pfernandez $
 */
public class JobExecutorPluginTest extends TestCase {

    public JobExecutorPluginTest(String testName) {
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

    /**
     * Test of getEnabled method, of class JobExecutorPlugin.
     */
    public void test_successfullTask() throws PushListenerPluginException, NotificationException, Exception {
        System.setProperty("funambol.home", System.getProperty("user.dir") + File.separatorChar + "src/test/resources");
        JobExecutorPlugin plugin = new JobExecutorPlugin();
        plugin.setPushListenerInterface(new MockPushListenerInterface());
        plugin.startPlugin();
        JobExecutor.getJobExecutor().init();

        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("teste", "olá");
        TaskExecutionRequestResponse resp = JobExecutor.getJobExecutor().executeTask("paulo", "job1", map);
        assertTrue(resp.isAccepted());
        plugin.stopPlugin();
    }
    
    /**
     * Test of getEnabled method, of class JobExecutorPlugin.
     */
    public void test_task_not_in_classpath() throws PushListenerPluginException, NotificationException, Exception {
        System.setProperty("funambol.home", System.getProperty("user.dir") + File.separatorChar + "src/test/resources");
        JobExecutorPlugin plugin = new JobExecutorPlugin();
        plugin.setPushListenerInterface(new MockPushListenerInterface());
        plugin.startPlugin();
        JobExecutor.getJobExecutor().init();

        TaskExecutionRequestResponse resp = JobExecutor.getJobExecutor().executeTask("paulo", "job2", null);
        assertFalse(resp.isAccepted());
        assertEquals("JOB-1002",resp.getErrorCode());
        assertEquals("Task to be executed cannot be instantiated" , resp.getErrrorMessage());
        plugin.stopPlugin();
    }
    /**
     * Test of getEnabled method, of class JobExecutorPlugin.
     */
    public void test_task_not_configured() throws PushListenerPluginException, NotificationException, Exception {
        System.setProperty("funambol.home", System.getProperty("user.dir") + File.separatorChar + "src/test/resources");
        JobExecutorPlugin plugin = new JobExecutorPlugin();
        plugin.setPushListenerInterface(new MockPushListenerInterface());
        plugin.startPlugin();
        JobExecutor.getJobExecutor().init();

        TaskExecutionRequestResponse resp = JobExecutor.getJobExecutor().executeTask("paulo", "job3", null);
        assertFalse(resp.isAccepted());
        assertEquals("JOB-1001",resp.getErrorCode());
        assertEquals("Task to be executed is not configured" , resp.getErrrorMessage());
        plugin.stopPlugin();
    }


    /**
     * Test of getEnabled method, of class JobExecutorPlugin.
     */
    public void test_task_with_properties() throws PushListenerPluginException, NotificationException, Exception {
        System.setProperty("funambol.home", System.getProperty("user.dir") + File.separatorChar + "src/test/resources");
        JobExecutorPlugin plugin = new JobExecutorPlugin();
        plugin.setPushListenerInterface(new MockPushListenerInterface());
        plugin.startPlugin();
        JobExecutor.getJobExecutor().init();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("test", "job4");
        TaskExecutionRequestResponse resp = JobExecutor.getJobExecutor().executeTask("paulo", "job4", map);
        assertTrue(resp.isAccepted());
        plugin.stopPlugin();
        assertTrue(MockJobExecutorTask.map.get("test").equals("job4"));
        assertTrue(MockJobExecutorTask.executed);
    }

}
