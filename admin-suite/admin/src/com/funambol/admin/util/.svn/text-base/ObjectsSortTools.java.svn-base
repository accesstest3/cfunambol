/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
 * Copyright (C) 2004 - 2007 Funambol, Inc.
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
package com.funambol.admin.util;

import java.util.Arrays;
import java.util.Comparator;
import java.lang.reflect.Method;

/**
 * Sort objects using a <code>ObjectsComparator</code>
 *
 * @version $Id: ObjectsSortTools.java,v 1.5 2007-11-28 10:28:17 nichele Exp $
 */
public class ObjectsSortTools {

        // --------------------------------------------------------------- Constants
        public static int TYPE_DEFAULT = 0;
        public static int TYPE_INTEGER = 1;
        public static int TYPE_LONG    = 2;
        public static int TYPE_STRING  = 3;
    // --------------------------------------------------------- Public Methods

    /**
     * Sort objects using a <code>ObjectsComparator</code>
     *
     * @param paramName property name used for sort the objects
     * @param objectsToSort object array to sort
     */
    public static void sort(String paramName, Object[] objectsToSort) {
        ObjectsComparator comparator = new ObjectsComparator(paramName);
        Arrays.sort(objectsToSort, comparator);
    }

    /**
     * Sort objects using a <code>ObjectsComparator</code>
     *
     * @param paramName property name used for sort the objects
     * @param type the type of property
     * @param objectsToSort object array to sort
     */
    public static void sort(String paramName, int type, Object[] objectsToSort) {
        ObjectsComparator comparator = new ObjectsComparator(paramName, type);
        Arrays.sort(objectsToSort, comparator);
    }

    /**
     * Generic comparator.
     * <br/>
     * Compares objects using the value of the property specified in the constructor.
     * <br/>
     * Uses reflection for get property value
     *
     */
    private static class ObjectsComparator implements Comparator {

        // ------------------------------------------------------------ Private data
        private String propertyName = null;
        private int    type         = TYPE_DEFAULT;

        // ------------------------------------------------------------- Constructor
        /**
         * Creates a new ObjectsComparator.
         *
         * @param propertyName property's name used during the comparison
         */
        public ObjectsComparator(String propertyName) {
            this.propertyName = propertyName;
        }

        /**
         * Creates a new ObjectsComparator.
         *
         * @param propertyName property's name used during the comparison
         * @param type the property's type
         */
        public ObjectsComparator(String propertyName, int type) {
            this.propertyName = propertyName;
            this.type         = type;
        }

        // ---------------------------------------------------------- Public methods

        /**
         * Compares its two arguments for order.
         *
         * @param o1 the first object to be compared.
         * @param o2 the second object to be compared.
         * @return a negative integer, zero, or a positive integer as the first
         *         argument is less than, equal to, or greater than the second.
         */
        public int compare(Object o1, Object o2) {
            Object value1 = null;
            Object value2 = null;

            value1 = getProperty(o1, propertyName);
            value2 = getProperty(o2, propertyName);

            if (type == TYPE_DEFAULT) {
                if (value1 instanceof String) {
                    return ( (String)value1).compareTo( (String)value2);
                } else if (value1 instanceof Integer) {
                    return ( (Integer)value1).compareTo( (Integer)value2);
                }
            } else if (type == TYPE_INTEGER) {
                value1 = new Integer(String.valueOf(value1));
                value2 = new Integer(String.valueOf(value2));
                return ( (Integer)value1).compareTo( (Integer)value2);
            } else if (type == TYPE_LONG) {
                value1 = new Long(String.valueOf(value1));
                value2 = new Long(String.valueOf(value2));
                return ( (Long)value1).compareTo( (Long)value2);
            } else if (type == TYPE_STRING) {
                value1 = String.valueOf(value1);
                value2 = String.valueOf(value2);
                return ( (String)value1).compareTo( (String)value2);
            }

            return 0;
        }

        // --------------------------------------------------------- Private methods

        /**
         * Gets property value of the given propertyName of the given object using reflection.
         * <br/>
         * Calls the method getXXXX to get property value.
         *
         * @param object object of which to get to the value of the given property
         * @param propertyName name of the property
         * @return Object value of the property. <code>null</code> if a error occurs during
         * reflection invocation
         */
        private Object getProperty(Object object, String propertyName) {
            String methodName = "";
            Object oValue = null;
            char firstLetter = propertyName.toUpperCase().charAt(0);
            if (propertyName.length() > 1) {
                methodName = propertyName.substring(1);
            }
            methodName = "get" + firstLetter + methodName;
            Class sourceClass = object.getClass();
            try {
                Method m = sourceClass.getMethod(methodName, new Class[0]);
                oValue = m.invoke(object, new String[0]);

            } catch (Exception e) {
                Log.error(Bundle.getMessage(Bundle.UNEXPECTED_ERROR), e);
            }
            return oValue;
        }

    }

}
