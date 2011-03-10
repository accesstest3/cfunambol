/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

package com.funambol.framework.core;

import java.util.*;

/**
 * This class represents the content type property such as property name, value,
 * display name, size and the content type parameters
 *
 * @deprecated use CTProperty instead
 *
 *  @version $Id: CTPropParam.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class CTPropParam implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private String    propName;
    private ArrayList valEnum;
    private String    displayName;
    private String    dataType;
    private Long      size;
    private ArrayList ctParameters;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected CTPropParam() {
        propName     = null;
        valEnum      = null;
        displayName  = null;
        dataType     = null;
        size         = null;
        ctParameters = null;
    }
    
    /**
     * Creates a new PropParam object given its name
     *
     * @param name - the PropParam name
     */
    public CTPropParam(String name) {
        this();
        this.propName = name;
    }

    /**
     * Creates a new ContentTypeProperty object with the given name, value and
     * display name
     *
     * @param propName corresponds to &lt;PropName&gt; element in the SyncML
     *                  specification - NOT NULL
     * @param valEnum   corresponds to &lt;ValEnum&gt; element in the SyncML
     *                  specification
     * @param displayName corresponds to &lt;DisplayName&gt; element in the SyncML
     *                  specification
     * @param ctParameters the array of content type parameters - NOT NULL
     *
     */
    public CTPropParam(final String propName,
                       final String[] valEnum,
                       final String displayName,
                       final CTParameter[] ctParameters) {
        setPropName(propName);
        setValEnum(valEnum);
        setContentTypeParameters(ctParameters);

        this.displayName  = displayName;
    }


    /**
     * Creates a new ContentTypeProperty object with the given name, value and
     * display name
     *
     * @param propName corresponds to &lt;PropName&gt; element in the SyncML
     *                  specification - NOT NULL
     * @param dataType corresponds to &lt;DataType&gt; element in the SyncML
     *                  specification
     * @param size corresponds to &lt;Size&gt; element in the SyncML
     *                  specification
     * @param displayName corresponds to &lt;DisplayName&gt; element in the SyncML
     *                  specification
     * @param ctParameters the array of content type parameters - NOT NULL
     *
     */
    public CTPropParam(final String propName,
                       final String dataType,
                       final Long size,
                       final String displayName,
                       final CTParameter[] ctParameters) {

        this(propName);

        setContentTypeParameters(ctParameters);

        this.dataType     = dataType;
        this.size         = size;
        this.displayName  = displayName;
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the property name
     *
     * @return the property name
     */
    public String getPropName() {
        return propName;
    }

    /**
     * Sets the property name
     *
     * @param propName the property name
     */
    public void setPropName(String propName) {
        if (propName == null){
            throw new IllegalArgumentException("propName cannot be null");
        }
        this.propName = propName;
    }

    /**
     * Gets the array of value for the property
     *
     * @return the array of value for the property
     */
    public ArrayList getValEnum() {
        return this.valEnum;
    }

    /**
     * Sets the array of enumerated value property
     *
     * @param valEnum the array of enumerated value property
     */
    public void setValEnum(String[] valEnum) {
        if (valEnum != null) {
            this.valEnum.clear();
            this.valEnum.addAll(Arrays.asList(valEnum));
        } else {
            this.valEnum = null;
        }
    }

    /**
     * Sets the array of enumerated value property
     *
     * @param valEnum the array of enumerated value property
     */
    public void setValEnum(ArrayList valEnum) {
        if (valEnum != null) {
            this.valEnum.clear();
            this.valEnum.addAll(valEnum);
        } else {
            this.valEnum = null;
        }
    }

    /**
     * Adds an enum value to the list of enums. If the internal enum is null,
     * it gets created. Null values are ignored.
     *
     * @param value - the value to add
     */
    public void addValue(String value) {
        if (valEnum == null) {
            valEnum = new ArrayList();
        }
        
        if (value != null) {
            valEnum.add(value);
        }
    }

    /**
     * Gets the display name property
     *
     * @return the display name property
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of a given content type property
     *
     * @param displayName the display name of a given content type property
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the data type propeties
     *
     * @return the data type propeties
     */
    public String getDataType() {
        return dataType;
    }

    /**
     * Sets the data type of a given content type property
     *
     * @param dataType the data type of a given content type property
     */
    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    /**
     * Gets the array of ContentTypeParameter
     *
     * @return the size propeties
     */
    public ArrayList getContentTypeParameters() {
        return this.ctParameters;
    }

    /**
     * Sets an array of content type properties
     *
     * @param ctParameters array of content type properties
     *
     */
    public void setContentTypeParameters(CTParameter[] ctParameters) {
        if (ctParameters != null) {
            this.ctParameters.clear();
            this.ctParameters.addAll(Arrays.asList(ctParameters));
        } else {
            this.ctParameters = null;
        }
    }

    /**
     * Sets an array of content type properties
     *
     * @param ctParameters array of content type properties
     *
     */
    public void setContentTypeParameters(ArrayList ctParameters) {
        if (ctParameters != null) {
            this.ctParameters.clear();
            this.ctParameters.addAll(ctParameters);
        } else {
            this.ctParameters = null;
        }
    }
    
    /**
     * Returns parameter size
     *
     * @return return the parameter size
     */
    public Long getSize() {
        return size;
    }

    /**
     * Sets the parameter size
     *
     * @param size - new value
     */
    public void setSize(Long size) {
        this.size = size;
    }
    
    /**
     * Sets the parameter size
     *
     * @param size - new value
     */
    public void setSize(long size) {
        this.size = new Long(size);
    }
}
