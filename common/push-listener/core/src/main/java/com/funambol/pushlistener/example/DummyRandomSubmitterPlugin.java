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
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.funambol.pushlistener.framework.PushListenerException;
import com.funambol.pushlistener.framework.Task;
import com.funambol.pushlistener.framework.TaskException;
import com.funambol.pushlistener.framework.plugin.PushListenerPluginAdapter;
import com.funambol.pushlistener.framework.PushListenerInterface;

/**
 * It runs a prefixed number of threads that any seconds submit a task with an id
 * randomly selected from a set of ids.
 *
 * @version $Id: DummyRandomSubmitterPlugin.java,v 1.3 2007-11-28 11:14:42 nichele Exp $
 */
public class DummyRandomSubmitterPlugin extends PushListenerPluginAdapter {

    // --------------------------------------------------------------- Constants

    // -------------------------------------------------------------- Properties

    /** Number of submitter threads */
    private int submitterThreadNumber = 20;

    public int getSubmitterThreadNumber() {
        return submitterThreadNumber;
    }

    public void setSubmitterThreadNumber(int submitterThreadNumber) {
        this.submitterThreadNumber = submitterThreadNumber;
    }

    /** Number of distinct ids */
    private int idNumber = 1;

    public int getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(int idNumber) {
        this.idNumber = idNumber;
    }

    // ------------------------------------------------------------ Private data

    private final Logger logger =
            Logger.getLogger("funambol.pushlistener.example.dummy-randomsubmitter-plugin");

    private PushListenerInterface pushListenerInterface = null;

    private String[] ids = null;

    // ------------------------------------------------------------ Constructors
    public DummyRandomSubmitterPlugin() {
        if (logger.isInfoEnabled()) {
            logger.info("Creating DummyRandomSubmitterPlugin");
        }
    }

    // ---------------------------------------------------------- Public methods

    @Override
    public void startPlugin() {
        if (logger.isInfoEnabled()) {
            logger.info("Starting DummyRandomSubmitterPlugin");
        }

        initIds();

        for (int i = 0; i<submitterThreadNumber; i++) {

            Thread th = new Thread(new Runnable() {
                public void run() {
                    int counter = 0;
                    while (true) {
                        try {
                            counter++;
                            boolean doubleExecution = false;
                            if (counter % 50 == 0) {
                                doubleExecution = true;
                            } else {
                                doubleExecution = false;
                            }
                            //
                            // Task duration
                            //
                            int duration = getRandomInt(1, 10); // 1-10 seconds

                            int idIndex = getRandomInt(0, ids.length);
                            final String id = ids[idIndex];

                            final RandomSubmitterTask task = new RandomSubmitterTask(id, duration);

                            if (doubleExecution) {
                                //
                                // New thread used to simulate two very close task
                                // submissions
                                //
                                Thread fast = new Thread(new Runnable() {
                                    public void run() {

                                        try {
                                            logger.info("'" + id + "' - double execution required - ");
                                            boolean asap = pushListenerInterface.executeTask(task);
                                            logger.info("'" + id + "' - double - execution planned - [" + (asap ? "asap" : "delayed") + "]");;
                                        } catch (PushListenerException ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                                fast.start();
                            }

                            logger.info("'" + id+ "' - execution required - [" + counter + "]");
                            boolean asap = pushListenerInterface.executeTask(task);
                            logger.info("'" + id + "' - execution planned - [" + counter + "] [" + (asap ? "asap" : "delayed") + "]");

                            int interval = getRandomInt(1, 1000); // 1 mills-1 seconds
                            Thread.sleep(interval);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });
            th.start();
        }
    }

    @Override
    public void setPushListenerInterface(PushListenerInterface pushListenerInterface) {
        logger.info("Setting pushlistener interface in DummyPlugin: " + pushListenerInterface);
        this.pushListenerInterface = pushListenerInterface;
    }

    @Override
    public void stopPlugin() {
        logger.info("Stop plugin in DummyRandomSubmitterPlugin");
    }

    // --------------------------------------------------------- Private methods


    private static int getRandomInt(int min, int max) {

        while (true) {
            double value = Math.random();
            if ((value * max) >= min) {
                return (int)(value * max);
            }
        }
    }

    private void initIds() {
        ids = new String[idNumber];

        for (int i=0; i<idNumber; i++) {
            ids[i] = String.valueOf(i);
        }
    }

}

class RandomSubmitterTask implements Task {

    private final Logger logger =
            Logger.getLogger("funambol.pushlistener.example.dummy-randomsubmitter-plugin");

    private String id = null;
    private long duration = 0;

    public RandomSubmitterTask(String id, long executionSeconds) {
        if (id == null) {
            throw new IllegalArgumentException("Id must be not null");
        }
        this.id = id;
        this.duration = executionSeconds;
    }

    @Override
    public boolean equals(Object o) {
        if (! (o instanceof RandomSubmitterTask)) {
            return false;
        }
        return id.equals(((RandomSubmitterTask) o).id);
    }

    @Override
    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    public void execute() throws TaskException {
        logger.info("'" + id + "' - run - [" + duration + "] seconds [" + this + "]");
        try {
            Thread.sleep(duration * 1000);
        } catch (InterruptedException ex) {
            // ignoring
        }
        logger.info("'" + id + "' - done - [" + this + "]");
    }

    public long getPeriod() {
        logger.info("'" + id + "' - getperiod - [" + this + "]");
        return  1 * 60 * 1000; // 1 minute
    }

    public String getId() {
        return id;
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

    public void setExecutionSeconds(int duration) {
        this.duration = duration;
    }

    public void configure(TaskConfiguration conf) throws TaskException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}