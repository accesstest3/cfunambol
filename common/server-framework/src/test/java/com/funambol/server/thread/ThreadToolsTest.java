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

package com.funambol.server.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;

import junit.framework.TestCase;

/**
 * ThreadTools's test case
 */
public class ThreadToolsTest extends TestCase {
    
    public ThreadToolsTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of stopThreadById method, of class ThreadTools.
     */
    public void testStopThreadById() {
        String threadName = "stop-by-id";
        Thread thread = new SimpleThread(threadName);
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.stopThreadById(id);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadById(id) != null );
        assertFalse("Thread still running", threadFound);
    }

    /**
     * Test of stopThreadByName method, of class ThreadTools.
     */
    public void testStopThreadByName() {
        String threadName = "stop-by-name";
        Thread thread = new SimpleThread(threadName);
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.stopThreadByName(threadName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadByName(threadName) != null );
        assertFalse("Thread still running", threadFound);    }

    /**
     * Test of interruptThreadById method, of class ThreadTools.
     */
    public void testInterruptThreadById_not_interruptable() {
        String threadName = "interrupt-by-id-not-interruptable";
        Thread thread = new SimpleThread(threadName, false);
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.interruptThreadById(id);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadById(id) != null );
        assertTrue("Thread should be still alive", threadFound);
    }

    /**
     * Test of interruptThreadById method, of class ThreadTools.
     */
    public void testInterruptThreadById() {
        String threadName = "interrupt-by-id";
        Thread thread = new SimpleThread(threadName, true);
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.interruptThreadById(id);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadById(id) != null );
        assertFalse("Thread still running", threadFound);
    }

    /**
     * Test of interruptThreadByName method, of class ThreadTools.
     */
    public void testInterruptThreadByName() {
        String threadName = "interrupt-by-name";
        Thread thread = new SimpleThread(threadName, true); // interruptable thread
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.interruptThreadByName(threadName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadByName(threadName) != null );
        assertFalse("Thread still running", threadFound);
    }

    /**
     * Test of interruptThreadByName method, of class ThreadTools.
     */
    public void testInterruptThreadByName_not_interruptable() {
        String threadName = "interrupt-by-name-not-interruptable";
        Thread thread = new SimpleThread(threadName, false); // not interruptable thread
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadTools.interruptThreadByName(threadName);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        boolean threadFound = ( ThreadTools.getThreadByName(threadName) != null );
        assertTrue("Thread should be still alive", threadFound);
    }

    /**
     * Test of getThreadById method, of class ThreadTools.
     */
    public void testGetThreadById() {
        String threadName = "get-by-id";
        Thread thread = new SimpleThread(threadName, false);
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        Thread th2 = ThreadTools.getThreadById(id);
        assertEquals("Thread not found", thread, th2);
    }

    /**
     * Test of getThreadIdByName method, of class ThreadTools.
     */
    public void testGetThreadIdByName() {
        String threadName = "get-id-by-name [dev-id] [username]";
        Thread thread = new SimpleThread(threadName, false);
        Long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        Long idTh = ThreadTools.getThreadIdByName("get-id-by-name [] s[]");
        assertEquals("Wrong id", id, idTh);

        threadName = "get-id-by-name-2";
        thread = new SimpleThread(threadName, false);
        id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        idTh = ThreadTools.getThreadIdByName(threadName);
        assertEquals("Wrong id", id, idTh);
    }

    /**
     * Test of getThreadByName method, of class ThreadTools.
     */
    public void testGetThreadByName() {
        String threadName = "get-by-name";
        Thread thread = new SimpleThread(threadName, false);
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        Thread th2 = ThreadTools.getThreadByName(threadName);
        assertEquals("Thread not found", thread, th2);


        String realThreadName = "get-by-name-2 [deviceid] [username]";
        threadName = "get-by-name-2";
        thread = new SimpleThread(realThreadName, false);
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        th2 = ThreadTools.getThreadByName(threadName);
        assertEquals("Thread not found", thread, th2);
    }

    /**
     * Test of getStackTraceAsString method, of class ThreadTools.
     */
    public void testGetStackTraceAsString_Thread() {
        String threadName = "stack-trace";
        Thread thread = new SimpleThread(threadName, true); // interruptable thread
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        String stack = ThreadTools.getStackTraceAsString(thread);

        assertTrue("Missing thread id", stack.indexOf("(id=" + id + ")") != -1);
        assertTrue("Wrong stack trace", stack.indexOf("at java.lang.Thread.sleep") != -1);
        assertTrue("Wrong stack trace", stack.indexOf("at com.funambol.server.thread.ThreadToolsTest$SimpleThread.run(ThreadToolsTest.java") != -1);
    }

    /**
     * Test of getStackTraceAsString method, of class ThreadTools.
     */
    public void testGetStackTraceAsString_ThreadInfo() {
        String threadName = "stack-trace-thread-info";
        Thread thread = new SimpleThread(threadName, true); // interruptable thread
        long id = thread.getId();
        thread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
        }
        ThreadInfo threadInfo = ManagementFactory.getThreadMXBean()
                                                 .getThreadInfo(thread.getId(), Integer.MAX_VALUE);
        
        String stack = ThreadTools.getStackTraceAsString(threadInfo);
        assertTrue("Missing thread id", stack.indexOf("(id=" + id + ")") != -1);
        assertTrue("Wrong stack trace", stack.indexOf("at java.lang.Thread.sleep") != -1);
        assertTrue("Wrong stack trace", stack.indexOf("at com.funambol.server.thread.ThreadToolsTest$SimpleThread.run(ThreadToolsTest.java") != -1);
    }


    class SimpleThread extends Thread {

        private boolean interruptable = false;
        
        public SimpleThread(String name) {
            super(name);
        }

        public SimpleThread(String name, boolean interruptable) {
            super(name);
            this.interruptable = interruptable;
        }

        @Override
        public void run() {
            System.out.println("Start: " + getName());
            do {
                try {
                    System.out.println("sleeping");
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException ex) {
                    System.out.println("Interrupt!");
                }
            } while (!interruptable);
        }
        
    }
}
