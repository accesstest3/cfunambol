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
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * Simple MBean that allows to perform some operations on threads (like kill)
 * that are not vailable by default in the jvm
 *
 * @version $Id: ThreadToolsMBean.java 33094 2009-12-13 21:42:15Z nichele $
 */
public class ThreadToolsMBean implements ThreadToolsMBeanMBean {

    private static final String MBEAN_NAME = "com.funambol:type=ThreadTools";
    
    public ThreadToolsMBean(){
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        try {
            ObjectName name = new ObjectName(MBEAN_NAME);
            try {
                mbs.unregisterMBean(name); // just to be sure to have one instance
            } catch (InstanceNotFoundException e) {
                // nothing to do, it means there is not another instance
            }
            mbs.registerMBean(this, name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopThreadById(long id) {
        ThreadTools.stopThreadById(id);
    }

    public void stopThreadByName(String name) {
        ThreadTools.stopThreadByName(name);
    }

    public void interruptThreadById(long id) {
        ThreadTools.interruptThreadById(id);
    }

    public void interruptThreadByName(String name) {
        ThreadTools.interruptThreadByName(name);
    }

    public String getStackTraceAsString(String name) {
        return ThreadTools.getStackTraceAsString(name);
    }

    public Long getThreadIdByName(String name) {
        return ThreadTools.getThreadIdByName(name);
    }

}


