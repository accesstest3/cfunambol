/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.pushlistener.framework;

import java.util.Map;

/**
 * Represents the configuration properties of a task.
 * @version $Id: TaskConfiguration.java,v 1.6 2007-11-28 11:14:42 nichele Exp $
 */
public class TaskConfiguration {

    // -------------------------------------------------------------- Properties

    /**
     * The id of the task
     */
    private long id;

    /**
     * Getter for the id
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * Sets the property id
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Is the task active ?
     */
    private boolean active;

    /**
     * Is the task active ?
     * @return Is the task active ?
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the property active
     *
     * @param active the value to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }


    /**
     * The period (time between two execution) of the task
     */
    private long period;

    /**
     * Getter for the period
     * @return the period
     */
    public long getPeriod() {
        return period;
    }

    /**
     * Sets the period
     * @param period the value to set
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * The path of the bean file that represents the task
     */
    private String taskBeanFile;

    /**
     * Getter for the taskBeanFile
     * @return the taskBeanFile
     */
    public String getTaskBeanFile() {
        return taskBeanFile;
    }

    /**
     * Sets the taskBeanFile
     * @param taskBeanFile the value to set
     */
    public void setTaskBeanFile(String taskBeanFile) {
        this.taskBeanFile = taskBeanFile;
    }

    /**
     * A map containing extra column values loaded from the registry table.
     */
    private Map<String,Object> properties;

    /**
     * @return the properties
     */
    public Map<String, Object> getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     */
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    /**
     * Return the value of the given column.
     *
     * @param key the name of the column (lower case) we want
     *
     * @return the object contained in the given column.
     */
    public Object getProperty(String key) {
        return properties==null?null:properties.get(key);
    }

}
