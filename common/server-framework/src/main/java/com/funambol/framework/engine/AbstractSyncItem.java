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

package com.funambol.framework.engine;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.framework.engine.source.SyncSource;

/**
 * Base class for SyncItem implementation.
 *
 * <i>SyncItem</i> is the indivisible entity that can be exchanged in a
 * synchronization process and contain data, status and addressing information.
 * <p>
 * The <i>SyncItemKey</i> uniquely identifies the item into the server. Client
 * keys must be translated into server keys before create a <i>SyncItem</i>.
 *
 * NOTE: it doesn't contain any content, getContent returns always null and should
 * be overriden by sub-classes
 * 
 * @version $Id: AbstractSyncItem.java 33737 2010-02-16 14:16:03Z testa $
 */
public abstract class AbstractSyncItem implements java.io.Serializable, SyncItem {

    // --------------------------------------------------------------- Constants
    public static final String PROPERTY_DECLARED_SIZE = "DECLARED_SIZE";
    public static final String PROPERTY_CONTENT_SIZE  = "CONTENT_SIZE";
    public static final String PROPERTY_COMMAND       = "PROPERTY_COMMAND";
    public static final String PROPERTY_OPAQUE_DATA   = "PROPERTY_OPAQUE";

    // -------------------------------------------------------------- Properties

    /**
     * The SyncItem's unique identifier - read only
     */
    protected SyncItemKey key = null;
    public SyncItemKey getKey() {
        return this.key;
    }

    public void setKey(SyncItemKey newKey) {
        this.key = newKey;
    }

    /**
     * The SyncItem's parent identifier - read only
     */
    protected SyncItemKey parentKey = null;
    public SyncItemKey getParentKey() {
        return this.parentKey;
    }

    public void setParentKey(SyncItemKey newParentKey) {
        this.parentKey = newParentKey;
    }

    /**
     * The SyncItem's mapped identifier - read/write
     */
    protected SyncItemKey mappedKey = null;
    public SyncItemKey getMappedKey() {
        return mappedKey;
    }

    public void setMappedKey(SyncItemKey mappedKey) {
        this.mappedKey = mappedKey;
    }

    /**
     *  A <i>SyncItem</i> is mapped if mappedKey is not null.
     *
     * @return <i>true</i> if the item is mapped to another source's item,
     *         <i>false</i> otherwise
     */
    public boolean isMapped() {
        return (mappedKey != null);
    }

    /**
     * The state of this <i>SyncItem</i>
     *
     * @see com.funambol.framework.engine.SyncItemState
     */
    protected char state = SyncItemState.UNKNOWN;

    public char getState(){
        return state;
    }

    public void setState(char state){
        this.state = state;
    }

    /**
     * The format of this <i>SyncItem</i>
     *
     */
    protected String format = null;

    public String getFormat(){
        return format;
    }

    public void setFormat(String format){
        this.format = format;
    }

    /**
     * The type of this <i>SyncItem</i>
     */
    protected String type = null;

    public String getType(){
        return type;
    }

    public void setType(String type){
        this.type = type;
    }

    /**
     * The timestamp of this <i>SyncItem</i>
     */
    protected Timestamp timestamp = null;

    public Timestamp getTimestamp(){
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp){
        this.timestamp = timestamp;
    }

    /**
     * The <i>SyncItem</i>'s properties - read and write
     */
    protected HashMap properties = new HashMap();

    /**
     * Returns the <i>properties</i> property. A cloned copy of the internal map
     * is returned.
     *
     * @return the <i>properties</i> property.
     */
    public Map getProperties() {
        return (Map)this.properties.clone();
    }

    /**
     * Sets the <i>properties</i> property. All items in the given map are added
     * to the internal map. <i>propertis</i> can contain String values or
     * <i>SyncPorperty</i> values. In the former case, <i>new SyncProperty<i/>s are
     * created.
     *
     * @param properties the new values
     */
    public void setProperties(Map properties){
        this.properties.clear();

        addProperties(properties);
    }

    public void addProperties(Map properties){

        Iterator i = properties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry entry = (Map.Entry) i.next();
            String name = (String) entry.getKey();
            Object value = entry.getValue();

            if (!(value instanceof SyncProperty)) {
                value = new SyncProperty(name, value);
            }
            this.properties.put(name, value);
        }
    }

    /** Sets/adds the given property to this <i>SyncItem</i>
     *
     * @param property The property to set/add
     */
    public void setProperty(SyncProperty property) {
        properties.put(property.getName(), property);
    }

    /** Returns the property with the given name
     *
     * @param propertyName The property name
     *
     * @return the property with the given name if exists or null if not
     */
    public SyncProperty getProperty(String propertyName) {
        return (SyncProperty)properties.get(propertyName);
    }

    /**
     * The SyncSource this item belongs to
     */
    protected SyncSource syncSource = null;

    /** Getter for property syncSource.
     * @return Value of property syncSource.
     *
     */
    public SyncSource getSyncSource() {
        return syncSource;
    }

    /** Setter for property syncSource.
     * @param syncSource New value of property syncSource. NOT NULL
     *
     */
    public void setSyncSource(SyncSource syncSource) {
        if (syncSource == null) {
            throw new NullPointerException("syncSource cannot be null");
        }

        this.syncSource = syncSource;
    }

    // ------------------------------------------------------------ Constructors

    protected AbstractSyncItem() {}

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the given
     * key.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     */
    public AbstractSyncItem(SyncSource syncSource, Object key) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             SyncItemState.UNKNOWN,
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the given
     * key and the given state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public AbstractSyncItem(SyncSource syncSource, Object key, char state) {
        this(syncSource,
             key,
             null,  // parentKey
             null,  // mappedKey
             state,
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source and with the given
     * key, parentKey and state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the item's parentKey
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public AbstractSyncItem(SyncSource syncSource, Object key, Object parentKey, char state) {
        this(syncSource,
             key,
             parentKey,
             null,  // mappedKey
             state,
             null,  // format
             null,  // type
             null   // timestamp
             );
    }

    /**
     * Creates a new <i>SyncItem</i> belonging to the given source with the
     * given key, parentKey, mappedKey and state.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the parent key of the item
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     */
    public AbstractSyncItem(SyncSource syncSource,
                        Object     key,
                        Object     parentKey,
                        Object     mappedKey,
                        char       state) {

        this(syncSource,
             key,
             parentKey,
             mappedKey,
             state,
             null, // format
             null, // type
             null  // timestamp
        );

    }

    /**
     * Creates a new <i>SyncItem</i>.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     * @param format the format of the item
     * @param type the type of the item
     * @param timestamp the timestamp of the item
     */
    public AbstractSyncItem(SyncSource syncSource,
                        Object     key,
                        Object     mappedKey,
                        char       state,
                        String     format,
                        String     type,
                        Timestamp  timestamp) {

        this(syncSource,
             key,
             null, // parentKey
             mappedKey,
             state,
             format,
             type,
             timestamp
        );
    }

    /**
     * Creates a new <i>SyncItem</i>.
     *
     * @param syncSource the source this item belongs to
     * @param key the item identifier
     * @param parentKey the item's parentKey
     * @param mappedKey the mapped key
     * @param state one of the state value defined in <i>SyncItemState</i>
     * @param format the format of the item
     * @param type the type of the item
     * @param timestamp the timestamp of the item
     */
    public AbstractSyncItem(SyncSource syncSource,
                            Object     key,
                            Object     parentKey,
                            Object     mappedKey,
                            char       state,
                            String     format,
                            String     type,
                            Timestamp  timestamp) {

        this.syncSource = syncSource          ;
        this.key        = new SyncItemKey(key);
        this.state      = state               ;

        this.parentKey = (parentKey != null)
                         ? new SyncItemKey(parentKey)
                         : null;

        this.mappedKey = (mappedKey != null)
                         ? new SyncItemKey(mappedKey)
                         : null;

        this.format    = format;
        this.type      = type;
        this.timestamp = timestamp;
    }

    // ---------------------------------------------------------- Static Methods
    // ---------------------------------------------------------- Public methods


    /** Sets the value of the property with the given name.
     *
     * @param propertyName The property's name
     * @param propertyValue The new value
     */
    public void setPropertyValue(String propertyName, Object propertyValue) {
        SyncProperty property = (SyncProperty)properties.get(propertyName);

        if (property != null) {
            property.setValue(propertyValue);
        }
    }

    /** Returns the value of the property with the given name.
     *
     * @param propertyName The property's name
     *
     * @return the property value if this <i>SyncItem</i> has the given
     *         property or null otherwise.
     */
    public Object getPropertyValue(String propertyName) {
        SyncProperty property = (SyncProperty)properties.get(propertyName);

        return (property == null) ? null
                                  : property.getValue()
                                  ;
    }

    /**
     * Two <i>SyncItem</i>s are equal if their keys are equal.
     *
     * @param o the object this instance must be compared to.
     *
     * @return the given object is equal to this object
     *
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SyncItem)) return false;

        return ((SyncItem)o).getKey().equals(key);
    }

    /**
     * Returns the hash code of this object
     *
     * @return the hash code of this object
     */
    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    /**
     * Returns a string representation of this SyncItem
     * @return a string representation of this SyncItem for debugging purposes
     */
    @Override
    public String toString() {
        return new ToStringBuilder(this).
                   append("key"       ,
                          (key != null ? key.toString() : "null" )).

                   append("parentKey" ,
                          (parentKey != null ? parentKey.toString() : "null" )).

                   append("mappedKey" ,
                          (mappedKey != null ? mappedKey.toString() : "null" )).

                   append("format"    , format                ).
                   append("type"      , type                  ).
                   append("timestamp" , timestamp             ).

                   append("state"     , state                 ).
                   append("properties",  properties.toString()).
                   toString();
    }

    /**
     * Makes the current item a copy of the item passed as parameter
     * The current item and the parameter one have a reference to the same
     * content and the same syncSource. In other words they share content and
     * syncSource.
     * @param syncItem
     */
    public void copy(SyncItem syncItem) {

        syncSource = syncItem.getSyncSource();
        key = syncItem.getKey() != null ? new SyncItemKey(syncItem.getKey().getKeyAsString()) : null;
        parentKey = syncItem.getParentKey() != null ? new SyncItemKey(syncItem.getParentKey().getKeyAsString()) : null;
        state = syncItem.getState();
        //
        // we need to call setContent since that method could do
        // something handling the content (see sub-classes)
        //
        setContent(syncItem.getContent());
        format = syncItem.getFormat();
        type = syncItem.getType();
        timestamp = syncItem.getTimestamp() != null ? (Timestamp) syncItem.getTimestamp().clone() : null;

        if (syncItem instanceof AbstractSyncItem) {
            mappedKey = ((AbstractSyncItem)syncItem).getMappedKey() != null ? new SyncItemKey(((AbstractSyncItem)syncItem).getMappedKey().getKeyAsString()) : null;
            setProperties(((AbstractSyncItem)syncItem).getProperties());
        }
    }

    /**
     * This implementation doesn't contain any data.
     * Sub-classes should override it.
     * @return always null
     */
    public byte[] getContent() {
        return null;
    }
}
