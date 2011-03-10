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
package com.funambol.framework.engine.pipeline;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;

/**
 * This class allows the pipeline components to share session-scoped and 
 * request-scoped properties.
 * <p>
 * Standard session properties are:
 * <ul>
 *   funambol.session.id - the server session id
 * </ul>
 * Standard request properties are:
 * <ul>
 *   funambol.request.parameters - parameters specified on the query string of
 *                               the current request
 *   funambol.request.headers - headers given with the current message
 * </ul>
 * <p>
 * <b>NOTE:</b> the prefix <i>funambol.</i> for property keys is reserved for
 * Funambol internal use.
 *
 * @version $Id: MessageProcessingContext.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class MessageProcessingContext implements Serializable {

    // --------------------------------------------------------------- Constants
    public static final String PROPERTY_SESSION_ID = "funambol.session.id";
    
    public static final String PROPERTY_REQUEST_HEADERS 
        = "funambol.request.headers";
    
    public static final String PROPERTY_REQUEST_PARAMETERS
        = "funambol.request.parameters";
    
    // ------------------------------------------------------------ Private data
    
    /**
     * Session scoped properties
     */
    private HashMap sessionProperties;
    
    /**
     * Request scoped properties
     */
     private HashMap requestProperties;
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * Creates a new MessageProcessingContext object
     *
     */
    public MessageProcessingContext() {
        sessionProperties = new HashMap();
        requestProperties = new HashMap();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Getter for sessionProperties
     * @return Value of properties
     */
    public Map getSessionProperties() {
        return this.sessionProperties;
    }

    /**
     * Setter for sessionProperties.
     *
     * @param properties New value of properties.
     */
    public void setSessionProperties(Map properties) {
        this.sessionProperties.clear();
        this.sessionProperties.putAll(properties);
    }
    
    /**
     * Getter for requestProperties
     * @return Value of properties
     */
    public Map getRequestProperties() {
        return this.requestProperties;
    }

    /**
     * Setter for sessionProperties.
     *
     * @param properties New value of properties.
     */
    public void setRequestProperties(Map properties) {
        this.requestProperties.clear();
        this.requestProperties.putAll(properties);
    }
    
    /**
     * Returns the value of the session property associated to the given key
     *
     * @param key the context property key
     *
     * @return the property value or null if key was not found
     */
    public Object getSessionProperty(String key) {
        return sessionProperties.get(key);
    }

    /**
     * Sets a given session property
     *
     * @param key the context property key
     * @param value the context property value
     *
     */
    public void setSessionProperty(String key, Object value) {
        sessionProperties.put(key, value);
    }

    
    /**
     * Returns the value of the request property associated to the given key
     *
     * @param key the context property key
     *
     * @return the property value or null if key was not found
     */
    public Object getRequestProperty(String key) {
        return requestProperties.get(key);
    }

    /**
     * Sets a given request property
     *
     * @param key the context property key
     * @param value the context property value
     *
     */
    public void setRequestProperty(String key, Object value) {
        requestProperties.put(key, value);
    }
    
    /**
     * Returns a String representation of request and session properties 
     * (for debug purposes).
     *
     * @return a String representation of request and session properties 
     * (for debug purposes).
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append(super.toString()).append(" {")
          .append("\n\tSession properties: ").append(sessionProperties)
          .append("\n\tRequest properties: ").append(requestProperties)
          .append("\n}");
        
        return sb.toString();
          
    }

}