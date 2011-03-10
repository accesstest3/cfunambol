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

import com.funambol.pushlistener.framework.TaskConfiguration;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.Task;
import com.funambol.pushlistener.framework.TaskException;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginAdapter;
import com.funambol.pushlistener.framework.PushListenerInterface;

/**
 * Simple plugin that can be triggered via JMX.
 * <p>It executes a simple task with id and duration specifiable via JMX.
 *
 * @version $Id: DummyJMXPlugin.java,v 1.3 2007-11-28 11:14:42 nichele Exp $
 */
public class DummyJMXPlugin extends PushListenerPluginAdapter implements DummyJMXPluginMBean {

    // --------------------------------------------------------------- Constants

    // ------------------------------------------------------------ Private data

    private final Logger logger = Logger.getLogger("funambol.pushlistener.example.dummy-jmx-plugin");

    private PushListenerInterface pushListenerInterface = null;

    // ------------------------------------------------------------ Constructors
    public DummyJMXPlugin() {
        if (logger.isInfoEnabled()) {
            logger.info("Creating JMXDummyPlugin");
        }
    }

    // ---------------------------------------------------------- Public methods

    @Override
    public void startPlugin() {

        if (logger.isInfoEnabled()) {
            logger.info("Starting JMXDummyPlugin");
            logger.info("Registering MBean " + MBEAN_NAME);
        }
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        try {

            // Construct the ObjectName for the MBean we will register
            ObjectName name = new ObjectName(MBEAN_NAME);

            mbs.registerMBean(this, name);
        } catch (Exception ex) {
            logger.error("Error registering mbean '" + MBEAN_NAME + "'", ex);
        }
    }

    @Override
    public void setPushListenerInterface(PushListenerInterface pushListenerInterface) {
        logger.info("Setting pushlistener interface in DummyPlugin: " + pushListenerInterface);
        this.pushListenerInterface = pushListenerInterface;
    }

    public void runTask(String id, long duration) throws PushListenerException {
        this.pushListenerInterface.executeTask(new DummyJMXRunnableTask(id, duration));
    }

    @Override
    public void stopPlugin() {
        logger.info("Stopping DummyJMXPlugin");
    }

}

class DummyJMXRunnableTask implements Task {

    private final Logger logger =
            Logger.getLogger("funambol.pushlistener.example.dummy-jmx-plugin");

    private String id = null;
    private long duration = 0;

    public DummyJMXRunnableTask(String id, long duration) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be not null");
        }
        this.id = id;
        this.duration = duration;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof DummyJMXRunnableTask)) {
            return false;
        }
        return id.equals(((DummyJMXRunnableTask) o).id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public void execute() throws TaskException {
        logger.info("Run in JMXDummyRunnableTask - id: '" + id + "'... wait " + duration + " seconds");
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException ex) {
            // ignoring
        }
        logger.info("Done JMXDummyRunnableTask - id: '" + id + "'");
    }

    public long getPeriod() {
        return  120 * 1000; // 120 secs
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("id", id);
        sb.append("duration", duration);
        return sb.toString();
    }

    public void configure(TaskConfiguration conf) throws TaskException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}