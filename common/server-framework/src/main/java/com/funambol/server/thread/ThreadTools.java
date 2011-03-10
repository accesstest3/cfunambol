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
import java.lang.management.ThreadMXBean;

/**
 * Simple class that allows to perform some operations on threads
 *
 * @version $Id: ThreadTools.java 33094 2009-12-13 21:42:15Z nichele $
 */
public class ThreadTools {

    private static ThreadMXBean threadMBean = ManagementFactory.getThreadMXBean();

    public static void stopThreadById(long id) {
        Thread th1 = getThreadById(id);
        if (th1 != null) {
            th1.stop();
        }
    }

    public static void stopThreadByName(String name) {
        Thread th1 = getThreadByName(name);
        if (th1 != null) {
            th1.stop();
        }
    }

    public static void interruptThreadById(long id) {
        Thread th1 = getThreadById(id);
        if (th1 != null) {
            th1.interrupt();
        }
    }

    public static void interruptThreadByName(String name) {
        Thread th1 = getThreadByName(name);
        if (th1 != null) {
            th1.interrupt();
        }
    }

    public static Thread getThreadById(long id) {

        //
        // get top ThreadGroup
        //
        ThreadGroup masterGroup = Thread.currentThread().getThreadGroup();
        while (masterGroup.getParent() != null) {
            masterGroup = masterGroup.getParent();
        }

        Thread threads[] = new Thread[masterGroup.activeCount()];
        int numThreads = masterGroup.enumerate(threads);

        for (int i = 0; i < numThreads; i++) {
            if (threads[i] != null && id == threads[i].getId()) {
                return threads[i];
            }
        }

        return null;
    }

    public static Long getThreadIdByName(String name) {
        Thread t = getThreadByName(name);
        if (t == null) {
            return null;
        }
        return t.getId();
    }

    public static Thread getThreadByName(String name) {
        if (name != null) {
            name = cleanupThreadName(name);
            //
            // get top ThreadGroup
            //
            ThreadGroup masterGroup = Thread.currentThread().getThreadGroup();
            while (masterGroup.getParent() != null) {
                masterGroup = masterGroup.getParent();
            }

            Thread threads[] = new Thread[masterGroup.activeCount()];
            int numThreads = masterGroup.enumerate(threads);

            for (int i = 0; i < numThreads; i++) {
                if (threads[i] != null && name.equals(cleanupThreadName(threads[i].getName()))) {
                    return threads[i];
                }
            }
        }
        return null;
    }

    public static String getStackTraceAsString(String threadName) {
        Thread t = getThreadByName(threadName);
        if (t == null) {
            return null;
        }
        ThreadInfo threadInfo = threadMBean.getThreadInfo(t.getId(), Integer.MAX_VALUE);
        return getStackTraceAsString(threadInfo);
    }

    public static String getStackTraceAsString(Thread t) {
        ThreadInfo threadInfo = threadMBean.getThreadInfo(t.getId(), Integer.MAX_VALUE);
        return getStackTraceAsString(threadInfo);
    }
    
    public static String getStackTraceAsString(ThreadInfo threadInfo) {

        if (threadInfo == null) {
            return null;
        }

        StringBuilder info = new StringBuilder("\tName: \"");
        info.append(threadInfo.getThreadName())
            .append("\" (id=").append(threadInfo.getThreadId()).append(")")
            .append("\n\tState: ").append(threadInfo.getThreadState());

        if (threadInfo.getLockName() != null) {
            info.append(" on ").append(threadInfo.getLockName());
        }

        if (threadInfo.isInNative()) {
            info.append(" (running in native code)");
        }
        if (threadInfo.isSuspended()) {
            info.append(" (suspended)");
        }

        long blockedCount = threadInfo.getBlockedCount();
        long blockedTime = threadInfo.getBlockedTime();

        long waitedCount = threadInfo.getWaitedCount();
        long waitedTime = threadInfo.getWaitedTime();

        info.append("\n\tTotal blocked: ").append(blockedCount);
        if (blockedCount > 0) {
            blockedTime = threadInfo.getBlockedTime();
            if (blockedTime >= 0) {
                info.append(" (").append(blockedTime).append(" ms)");
            }
        }

        info.append("  Total waited: ").append(waitedCount);

        if (waitedCount > 0) {
            waitedTime = threadInfo.getWaitedTime();
            if (waitedTime >= 0) {
                info.append(" (").append(waitedTime).append(" ms)");
            }
        }

        if (threadInfo.getLockOwnerName() != null) {
            info.append("\n\tOwned by ").append(threadInfo.getLockOwnerName()).append(" (id=").append(threadInfo.getLockOwnerId()).append(")");
        }

        StackTraceElement[] stackTrace = threadInfo.getStackTrace();

        info.append("\n");
        if (stackTrace != null && stackTrace.length > 0) {
            for (StackTraceElement stack : stackTrace) {
                info.append("\n\t\tat ").append(stack.toString());
            }
        } else {
            info.append("\n\t\tNo available stack trace");
        }

        return info.toString();
    }

    // --------------------------------------------------------- Private methods

    private static String cleanupThreadName(String threadName) {
        if (threadName == null) {
            return null;
        }
        int index = threadName.indexOf('[');
        if (index == -1) {
            return threadName;
        }
        return threadName.substring(0, index).trim();
    }
}
