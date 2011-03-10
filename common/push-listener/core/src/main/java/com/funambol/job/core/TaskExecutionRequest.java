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
package com.funambol.job.core;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Represents an object which is sent to the JobExecutor listener in order
 * to execute specific task.
 * @version $Id: TaskExecutionRequest.java 35930 2010-09-08 11:25:14Z onufryk $
 */
public class TaskExecutionRequest implements Serializable {
    private static final long serialVersionUID = -2732460129599282105L;

    /**
     * Task id can be the username, the purpose of this attribute is to split 
     * the message across the nodes of the cluster
     */
    private String id;
    /**
     * Unique codename of the task. Not classname.
     */
    private String taskName;
    /**
     * Map with arguments need for task execution.
     * E.g. userid, service name etc.
     */
    private HashMap<String, Object> propertiesMap = null;

    /**
     * Constructs a task execution request with specified task name.
     * @param taskName Name of the task
     */
    public TaskExecutionRequest(String id,String taskName) {
        this.id=id;
        this.taskName = taskName;
        this.propertiesMap = new HashMap<String, Object>();
    }
    
    /**
     * Constructs a task execution request with specified task name 
     * and properties.
     * @param taskName Name of the task
     * @param taskProperties Map with task arguments
     */
    public TaskExecutionRequest(String id,String taskName, HashMap<String, Object> taskProperties) {
        this.id=id;
        this.taskName = taskName;
        this.propertiesMap = taskProperties;
    }

    /**
     * Returns name of the task.
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * Sets name of the task.
     * @param taskName the task name to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Returns map with task arguments.
     * @return the propertiesMap
     */
    public HashMap<String, Object> getPropertiesMap() {
        return propertiesMap;
    }

    /**
     * Adds new property/argument to the task properties.
     * @param key name of the property
     * @param value value of the property
     */
    public void addProperty(String key, String value) {
        this.propertiesMap.put(key, value);
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
