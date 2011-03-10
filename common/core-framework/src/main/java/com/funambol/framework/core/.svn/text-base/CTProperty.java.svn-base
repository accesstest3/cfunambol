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
 *  @version $Id: CTProperty.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class CTProperty 
extends CTParameter
implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    
    private ArrayList parameters;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected CTProperty() {
        super();
        parameters = new ArrayList();
    }
    
    /**
     * Creates a new PropParam object given its name
     *
     * @param name - the PropParam name
     */
    public CTProperty(String name) {
        super(name);
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
    public CTProperty(final String propName,
                      final String[] valEnum,
                      final String displayName,
                      final CTParameter[] parameters) {
        super(propName, valEnum, displayName);
        
        setParameters(parameters);
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
    public CTProperty(final String propName,
                      final String dataType,
                      final Long size,
                      final String displayName,
                      final CTParameter[] parameters) {

        super(propName, dataType, size, displayName);
        
        setParameters(parameters);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the array of ContentTypeParameter
     *
     * @return the size propeties
     */
    public ArrayList getParameters() {
        return parameters;
    }

    /**
     * Sets an array of content type properties
     *
     * @param ctParameters array of content type properties
     *
     */
    public void setParameters(CTParameter[] parameters) {
        setParameters((parameters != null) ? Arrays.asList(parameters) : null);
    }

    /**
     * Sets an array of content type properties
     *
     * @param ctParameters array of content type properties
     *
     */
    public void setParameters(List parameters) {
        if (parameters != null) {
            if (this.parameters == null) {
                this.parameters = new ArrayList();
            } else {
                this.parameters.clear();
            }
            this.parameters.addAll(parameters);
        } else {
            this.parameters = null;
        }
    }
    
   
}
