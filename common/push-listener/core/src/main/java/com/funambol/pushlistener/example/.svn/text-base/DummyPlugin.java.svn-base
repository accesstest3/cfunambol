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

import com.funambol.pushlistener.framework.Task;
import com.funambol.pushlistener.framework.TaskConfiguration;
import com.funambol.pushlistener.framework.TaskException;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginAdapter;
import com.funambol.pushlistener.framework.PushListenerInterface;

/**
 * Simple plugin that at fixed interval calls the pushlistener interface to execute
 * a new task. It executes DummyRunnableTask that just starts and then sleeps for
 * a while.
 *
 * @version $Id: DummyPlugin.java,v 1.3 2007-11-28 11:14:42 nichele Exp $
 */
public class DummyPlugin extends PushListenerPluginAdapter {

    // --------------------------------------------------------------- Constants
    private static final long SLEEP_SECONDS = 5;

    // ------------------------------------------------------------ Private data

    private PushListenerInterface pushListenerInterface = null;

    // ------------------------------------------------------------ Constructors
    public DummyPlugin() {
        System.out.println("Creating DummyPlugin");
    }

    // ---------------------------------------------------------- Public methods

    @Override
    public void startPlugin() {
        System.out.println("Starting DummyPlugin");
        //
        // Every 5 seconds it executes a new DummyRunnableTask
        //
        Thread t = new Thread(new Runnable() {

            public void run() {
                int counter = 0;
                while (true) {
                    counter++;
                    try {
                        System.out.println("Request for execution (id: " + counter + ")...");
                        pushListenerInterface.executeTask(new DummyRunnableTask(String.valueOf(counter)));
                        Thread.sleep(SLEEP_SECONDS * 1000);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        t.start();
    }

    @Override
    public void setPushListenerInterface(PushListenerInterface pushListenerInterface) {
        System.out.println("Setting pushlistener interface in DummyPlugin: " + pushListenerInterface);
        this.pushListenerInterface = pushListenerInterface;
    }

    @Override
    public void stopPlugin() {
        System.out.println("Stopping DummyPlugin");
    }
}

class DummyRunnableTask implements Task {

    private static final long TASK_DURATION = 30;
    private static final long PERIOD = 1;

    private String id = null;

    public DummyRunnableTask(String id) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be not null");
        }
        this.id = id;
    }

    public void execute() {
        try {
            System.out.println("Run in DummyRunnable (id: " + id + ")... wait 30 seconds");
            Thread.sleep(TASK_DURATION * 1000);
        } catch (InterruptedException ex) {
            // ignoring
        }

    }

    public long getPeriod() {
        return PERIOD * 60 * 1000;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof DummyRunnableTask)) {
            return false;
        }
        return id.equals(((DummyRunnableTask)o).id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public void configure(TaskConfiguration conf) throws TaskException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}