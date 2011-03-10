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

package com.funambol.framework.management;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.ReflectionException;
import javax.management.RuntimeOperationsException;

/**
 * An abstract implementation of the StatusMXBean interface
 *
 * @version $Id: AbstractStatusMXBean.java,v 1.6 2007-11-28 11:15:37 nichele Exp $
 */
public abstract class AbstractStatusMXBean
implements DynamicMBean, StatusMXBean {

    // --------------------------------------------------------------- constants

    // ------------------------------------------------------------ private data

    /**
     * The serviceName of the server being instrumented
     */
    protected String serviceName;

    /**
     * The version of the server being instrumented
     */
    protected String version;

    /**
     * A map representing the status of the server being instrumented
     */
    protected Map status;

    /**
     * The bean info
     */
    protected MBeanInfo info;

    // ------------------------------------------------------------ constructors

    /** Creates a new instance of SimpleStatusMXBean */
    public AbstractStatusMXBean() {
        status = new HashMap();
        info   = null;

        buildMBeanInfo();
    }

    // ---------------------------------------------------------- public methods

    /**
     * Returns the serviceName of the server being instrumented (key ATTRIBUT_SERVICE_NAME).
     *
     * @return the version of the server being instrumented
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Sets the serviceName property.
     *
     * @param serviceName the serviceName
     */
    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * Returns the version of the server being instrumented (key ATTRIBUT_VERSION).
     *
     * @return the version of the server being instrumented
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the version property.
     *
     * @param version the version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Returns the status of the server as a string in the following form:
     *
     * <code>
     * {property}={value}\n
     * ...
     * </code>
     *
     * @return the status of the server
     */
    public String getStatus() {
        StringBuffer sb = new StringBuffer();

        Iterator i = status.keySet().iterator();
        String key = null;
        int cont = 0;
        while(i.hasNext()) {
            key = (String)i.next();
            if (cont++ > 0) {
                sb.append('\n');
            }
            sb.append(key).append('=').append(status.get(key));
        }

        return sb.toString();
    }

    /**
     * Sets an arbitrary property and value
     *
     * @param property the property - NOT NULL
     * @param value the value
     */
    public void setProperty(String property, String value) {
        status.put(property, value);
    }
    
    /**
     * Sets the given proerties
     *
     * @param properties the properties
     */
    public void setProperties(Map properties) {
        status.putAll(properties);
    }    

    /**
     * Gets the value of the given property
     *
     * @param property the property - NOT NULL
     *
     * @return the property value
     */
    public String getProperty(String property) {
        return (String)status.get(property);
    }

    /**
     * Removes all properties from the status map
     */
    public void cleanStatus() {
        status.clear();
    }

    /**
     * This method provides the exposed attributes and operations of the
     * Dynamic MBean. It provides this information using an MBeanInfo object.
     */
    public MBeanInfo getMBeanInfo() {

        // Return the information we want to expose for management:
        // the dMBeanInfo private field has been built at instanciation time
        //
        return info;
    }

    /**
     * Allows an operation to be invoked on the Dynamic MBean.
     */
    public Object invoke(String name,
                         Object params[],
                         String signature[])
        throws MBeanException, ReflectionException {

        if (name == null) {
            throw new RuntimeOperationsException(
                 new IllegalArgumentException("Operation name cannot be null"),
                 "Cannot invoke a null operation in " + getClass().getName());
        }

        //
        // No operations are currently implemented....
        //
        throw new ReflectionException(
                                new NoSuchMethodException(name),
                                "Cannot find the operation " + name +
                                " in " + getClass().getName());
    }

    /**
     * Sets the values of several attributes of the Dynamic MBean, and returns
     * the list of attributes that have been set.
     */
    public AttributeList setAttributes(AttributeList attributes) {

        if (attributes == null) {
            throw new RuntimeOperationsException(
                      new IllegalArgumentException(
                                 "AttributeList attributes cannot be null"),
                                 "Cannot invoke a setter of " + getClass().getName());
        }
        AttributeList resultList = new AttributeList();

        //
        // If attributeNames is empty, nothing more to do
        //
        if (attributes.isEmpty())
            return resultList;

        //
        // For each attribute, try to set it and add to the result list if
        // successfull
        //
        for (Iterator i = attributes.iterator(); i.hasNext();) {
            Attribute attr = (Attribute) i.next();
            try {
                setAttribute(attr);
                String name = attr.getName();
                Object value = getAttribute(name);
                resultList.add(new Attribute(name,value));
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * Enables the to get the values of several attributes of the Dynamic MBean.
     */
    public AttributeList getAttributes(String[] attributeNames) {

        if (attributeNames == null) {
            throw new RuntimeOperationsException(
                new IllegalArgumentException("attributeNames[] cannot be null"),
                "Cannot invoke a getter of " + getClass().getName());
        }
        AttributeList resultList = new AttributeList();

        //
        // If attributeNames is empty, return an empty result list
        //
        if (attributeNames.length == 0)
            return resultList;

        //
        // Build the result attribute list
        //
        for (int i = 0 ; i < attributeNames.length ; i++) {
            try {
                Object value = getAttribute((String) attributeNames[i]);
                resultList.add(new Attribute(attributeNames[i],value));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return resultList;
    }

    /**
     * Allows the value of the specified attribute of the Dynamic MBean to be
     * obtained.
     */
    public Object getAttribute(String attribute_name)
        throws AttributeNotFoundException,
               MBeanException,
               ReflectionException {

        // Check attribute_name is not null to avoid NullPointerException
        // later on
        //
        if (attribute_name == null) {
            throw new RuntimeOperationsException(
                  new IllegalArgumentException("Attribute name cannot be null"),
                  "Cannot invoke a getter of " + getClass().getName() +
                  " with null attribute name");
        }
        // Check for a recognized attribute_name and call the corresponding
        // getter
        //
        if (ATTRIBUTE_SERVICE_NAME.equals(attribute_name)) {
            return getServiceName();
        }
        if (ATTRIBUTE_VERSION.equals(attribute_name)) {
            return getVersion();
        }
        if (ATTRIBUTE_STATUS.equals(attribute_name)) {
            return getStatus();
        }

        // If attribute_name has not been recognized throw an
        // AttributeNotFoundException
        //
        throw new AttributeNotFoundException("Cannot find " +
                                             attribute_name +
                                             " attribute in " +
                                             getClass().getName());
    }

    /**
     * Sets the value of the specified attribute of the Dynamic MBean.
     */
    public void setAttribute(Attribute attribute)
        throws AttributeNotFoundException,
               InvalidAttributeValueException,
               MBeanException,
               ReflectionException {

        // Check attribute is not null to avoid NullPointerException later on
        //
        if (attribute == null) {
            throw new RuntimeOperationsException(
                  new IllegalArgumentException("Attribute cannot be null"),
                  "Cannot invoke a setter of " + getClass().getName() +
                  " with null attribute");
        }
        String name = attribute.getName();
        Object value = attribute.getValue();
        if (name == null) {
            throw new RuntimeOperationsException(
                  new IllegalArgumentException("Attribute name cannot be null"),
                  "Cannot invoke the setter of " + getClass().getName() +
                  " with null attribute name");
        }
        // Check for a recognized attribute name and call the corresponding
        // setter
        //
        if (name.equals(ATTRIBUTE_SERVICE_NAME)) {
            //
            // if null value, try and see if the setter returns any exception
            //
            if (value == null) {
                try {
                    setServiceName( null );
                } catch (Exception e) {
                    throw new InvalidAttributeValueException(
                              "Cannot set attribute " + name + " to null");
                }
            }
            // if non null value, make sure it is assignable to the attribute
            else {
                try {
                    if (Class.forName("java.lang.String").isAssignableFrom(
                                                          value.getClass())) {
                        setServiceName((String) value);
                    } else {
                        throw new InvalidAttributeValueException(
                                  "Cannot set attribute " + name + " to a " +
                                  value.getClass().getName() +
                                  " object, String expected");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else if (name.equals(ATTRIBUTE_VERSION)) {
            //
            // if null value, try and see if the setter returns any exception
            //
            if (value == null) {
                try {
                    setVersion( null );
                } catch (Exception e) {
                    throw new InvalidAttributeValueException(
                              "Cannot set attribute " + name + " to null");
                }
            }
            // if non null value, make sure it is assignable to the attribute
            else {
                try {
                    if (Class.forName("java.lang.String").isAssignableFrom(
                                                          value.getClass())) {
                        setVersion((String) value);
                    } else {
                        throw new InvalidAttributeValueException(
                                  "Cannot set attribute " + name + " to a " +
                                  value.getClass().getName() +
                                  " object, String expected");
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (name.equals(ATTRIBUTE_STATUS)) {
            //
            // recognize an attempt to set "status" attribute (read-only):
            //
            throw new AttributeNotFoundException(
                  "Cannot set attribute " + name + " because it is read-only");
        }
        // unrecognized attribute name:
        else {
            throw new AttributeNotFoundException("Attribute " + name +
                                                 " not found in " +
                                                 this.getClass().getName());
        }
    }


    // ------------------------------------------------------- protected methods

    /**
     * Populates the bean info
     */
    protected void buildMBeanInfo() {
        MBeanAttributeInfo[] attributes = new MBeanAttributeInfo[3];

        attributes[0] =
            new MBeanAttributeInfo(ATTRIBUTE_SERVICE_NAME,  // name
                                   "java.lang.String",      // type
                                   "The service name.",     // description
                                   false,                   // isReadable
                                   false,                   // isWritable
                                   false);                  // isIs

        attributes[1] =
            new MBeanAttributeInfo(ATTRIBUTE_VERSION,       // name
                                   "java.lang.String",      // type
                                   "The server version.",   // description
                                   false,                   // isReadable
                                   false,                   // isWritable
                                   false);                  // isIs

        attributes[2] =
            new MBeanAttributeInfo(ATTRIBUTE_STATUS,        // name
                                   "java.lang.Integer",     // type
                                   "The server status.",    // description
                                   false,                   // isReadable
                                   false,                   // isWritable
                                   false);                  // isIs

        MBeanConstructorInfo[] constructors = new MBeanConstructorInfo[1];

        constructors[0] =
            new MBeanConstructorInfo("Constructs a SimpleStatusMXBean",
                                     this.getClass().getConstructors()[0]);

        info = new MBeanInfo(
                   getClass().getName(),
                   "Server status information.",
                   attributes,
                   constructors,
                   new MBeanOperationInfo[0],
                   new MBeanNotificationInfo[0]
               );
    }
}
