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

package com.funambol.pushlistener.service.registry;

import java.io.Serializable;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Entity class RegistryEntry.
 * This class defines a set of basic properties that each instance will have.
 * They are:
 * - id:     the key used to store data into the table
 * - period: the amount of time is't waited before checking again this entry.
 * - active: when set to true, defines this entry as active one.
 * - lastUpdate:
 * - status: it's the status of this entry.
 * - taskBeanFile: it's the name of the task class that must be istantiated to
 * serve this entry.
 *
 * Besides, according to the extra columns defined in the push registry table,
 * each instance will have a map containing all other columns defined in the push
 * registry table.
 * Each entry of the map (called properties) will have the column name (lowercase)
 * as key and as value the column value, extracted from the ResultSet as object,
 * Keep in mind that it's possible to insert and modify extra colum values, setting
 * the proper value inside the properties. Remember to specify the column name
 * lower case.
 * For further info, please refer to the RegistryDao documentation.
 *
 *
 * @version $Id: RegistryEntry.java,v 1.6 2007-11-28 11:15:04 nichele Exp $
 */
public class RegistryEntry implements Serializable {

    // -------------------------------------------------------------- Properties
    /** The id */
    private long id;

    /** The period */
    private long period;

    /** Is the entry active ? */
    private boolean active;

    /** The time of the last update  */
    private long lastUpdate;

    /** The status of the entries */
    //private RegistryStatus status;
    private String status;

    /** The task bean file */
    private String taskBeanFile;

    /** The map containing the name of the extra column as key, and as value the
     * value the columns have for this entry. Column values will be extracted
     * from the ResultSet as object.
     */
    private Map<String,Object> properties = new LinkedHashMap<String, Object>();

    /** Creates a new instance of RegistryEntry */
    public RegistryEntry() {
    }

    /**
     * Creates a new instance of RegistryEntry with the specified values.
     * @param id the id of the RegistryEntry
     */
    public RegistryEntry(long id) {
        this.id = id;
    }

    /**
     * Gets the id of this RegistryEntry.
     * @return the id
     */
    public long getId() {
        return this.id;
    }

    /**
     * This method is used to establish if a value is set
     * inside the map of properties for the given key.
     * 
     * @param key the column name (lowercase) we're checking
     * 
     * @return true if a column value is available for the given name, false otherwise.
     */
    public boolean hasProperty(String key) {
        return properties!=null && properties.containsKey(key);
    }

    /**
     * Sets the id of this RegistryEntry to the specified value.
     * @param id the new id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Gets the period of this RegistryEntry.
     * @return the period
     */
    public long getPeriod() {
        return this.period;
    }

    /**
     * Sets the period of this RegistryEntry to the specified value.
     * @param period the new period
     */
    public void setPeriod(long period) {
        this.period = period;
    }

    /**
     * Gets the active of this RegistryEntry.
     * @return the active
     */
    public boolean getActive() {
        return this.active;
    }

    /**
     * Sets the active of this RegistryEntry to the specified value.
     * @param active the new active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the lastUpdate of this RegistryEntry.
     * @return the lastUpdate
     */
    public long getLastUpdate() {
        return this.lastUpdate;
    }

    /**
     * Sets the lastUpdate of this RegistryEntry to the specified value.
     * @param lastUpdate the new lastUpdate
     */
    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    /**
     * Gets the status of this RegistryEntry.
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * Sets the status of this RegistryEntry to the specified value.
     * @param status the new status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the taskBeanFile of this RegistryEntry.
     * @return the taskBeanFile
     */
    public String getTaskBeanFile() {
        return this.taskBeanFile;
    }

    /**
     * Sets the taskBeanFile of this RegistryEntry to the specified value.
     * @param taskBeanFile the new taskBeanFile
     */
    public void setTaskBeanFile(String taskBeanFile) {
        this.taskBeanFile = taskBeanFile;
    }

    /**
     * Returns a hash code value for the object.  This implementation computes
     * a hash code value based on the id fields in this object.
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        return new Long(id).hashCode();
    }

    /**
     * Determines whether another object is equal to this RegistryEntry.  The result is
     * <code>true</code> if and only if the argument is not null and is a RegistryEntry object that
     * has the same id field values as this object.
     * @param object the reference object with which to compare
     * @return <code>true</code> if this object is the same as the argument;
     * <code>false</code> otherwise.
     */
    @Override
    public boolean equals(Object object) {
        if (!(object instanceof RegistryEntry)) {
            return false;
        }
        RegistryEntry other = (RegistryEntry)object;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("id", id);
        sb.append("period", period);
        sb.append("active", active);
        sb.append("taskBeanFile", taskBeanFile);
        sb.append("lastUpdate" , lastUpdate);
        sb.append("status" , status);
        if(hasProperties()) {
            for(String key:properties.keySet()) {
                if(key!=null) {
                    Object value = properties.get(key);
                    sb.append(key,value);
                }
            }
        }
        return sb.toString();
    }

    /**
     * @return the whole map of properties
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
     * Allows to set the given object inside the map of properties
     * using the given key.
     * @param key the column name lowercase.
     * @param value the value for the specified column.
     */

    public void addProperty(String key, Object value) {
        properties.put(key, value);
    }

    /**
     * this method allows to retrieve the property bound
     * to the given key if it is defined inside the properties.
     * 
     * @param key the column name (lowercase) we want to obtain the value.
     * 
     * @return the column value loaded from the given column name.
     */
    public Object getProperty(String key) {
        if(properties.containsKey(key)) {
            return properties.get(key);
        }
        return null;
    }

    /**
     * This method allows to remove the property bound to the given
     * column name.
     * @param key the name of the column (lowercase) we want to remove from
     * the map of properties.
     * @return the value of the removed column name if exists, null otherwise.
     */
    public Object removeProperty(String key) {
        if(properties.containsKey(key)) {
            return properties.remove(key);
        }
        return null;
    }

    /**
     * This method allows to establish if this entry contains extra properties
     * or not.
     * 
     * @return true if any extra properti exists, false otherwise.
     */
    public boolean hasProperties() {
        return properties!=null && !properties.isEmpty();
    }

}
