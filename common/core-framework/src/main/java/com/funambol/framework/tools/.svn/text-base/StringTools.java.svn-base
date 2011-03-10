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
package com.funambol.framework.tools;

import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utility class that groups string manipulation functions.
 *
 *
 * @version $Id: StringTools.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class StringTools {

    /**
     * Splits a comma separated values string into an array of strings.
     *
     * @param s the comma separated values list - NOT NULL
     *
     * @return the elements in the list as an array
     */
    public static String[] split(String s) {
        StringTokenizer st = new StringTokenizer(s, ", ");

        String[] values = new String[st.countTokens()];

        for (int i=0; i<values.length; ++i) {
            values[i] = st.nextToken();
        }

        return values;
    }

    /**
     * Joins the given Strin[] in a comma separated String
     *
     * @param array the String[] to join - NOT NULL
     *
     * @return a comma separated list as a single string
     */
    public static String join(String[] array) {
        StringBuffer sb = new StringBuffer();
        for (int i=0; (i<array.length); ++i) {
            if (i == 0) {
                sb.append(array[i]);
            } else {
                sb.append(',').append(array[i]);
            }
        }

        return sb.toString();
    }

    /**
     * Returns true if the given string is null or zero-length, false otherwise.
     *
     * @param s the string to check
     *
     * @return true if the given string is null or zero-length, false otherwise.
     */
    public static boolean isEmpty(String s) {
        return (s == null) || (s.length() == 0);
    }

    /**
     * Replaces special characters from the given string with an underscore ('_').
     *
     * @param s the string to replace.
     *
     * @return the replaced string.
     */
    public static String replaceSpecial(String s) {

        char[] chars = s.toCharArray();
        for (int i=0; i < chars.length; ++i) {
            if ((chars[i] < '0' || chars[i]  > '9')  &&
                (chars[i]  < 'a' || chars[i]  > 'z') &&
                (chars[i]  < 'A' || chars[i]  > 'Z')) {
                chars[i] = '_';
            }
        }

        return new String(chars);
    }


    /**
     * <p>Escapes the characters in a <code>String</code> using XML entities.</p>
     *
     *
     * <p>Supports only the three basic XML entities (gt, lt, amp).
     * Does not support DTDs or external entities.</p>
     *
     * @param str  the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeXml(java.lang.String)
     **/
    public static String escapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.escape(str);
    }

    /**
     * <p>Escapes only &, >, < characters in a <code>String</code> using XML entities.</p>
     *
     * @param str  the <code>String</code> to escape, may be null
     * @return a new escaped <code>String</code>, <code>null</code> if null string input
     * @see #unescapeXml(java.lang.String)
     **/
    public static String escapeBasicXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.escapeBasic(str);
    }

    /**
     * <p>Unescapes a string containing XML entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes.</p>
     *
     * <p>Supports only the three basic XML entities (gt, lt, amp).
     * Does not support DTDs or external entities.</p>
     *
     * @param str  the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     * @see #escapeXml(String)
     **/
    public static String unescapeXml(String str) {
        if (str == null) {
            return null;
        }
        return Entities.XML.unescape(str);
    }
    
    /**
     * Replaces all occurrences of ${variable} with the corresponding value if
     * variable is a key of the map variables. If variable is not found, the
     * placeholder is not replaced. If the value to replace is null, nullValue
     * is replaced instead.
     *
     * @param str the string to replace - NOT NULL
     * @param nullValue the value to replace if the real value is null - NUT NULL
     * @param variables the map with the (variable,value) pairs - NOT NULL
     *
     * @return the replaced string
     */
    public static String replace(final String str,
                                 final String nullValue,
                                 final Map<String,Object> variables) {
        if (str == null) {
            throw new IllegalArgumentException("str cannot be null");
        }

        if (nullValue == null) {
            throw new IllegalArgumentException("nullValue cannot be null");
        }

        if (variables == null) {
            throw new IllegalArgumentException("variables cannot be null");
        }

        String ret = str;
        Object value = null;
        for (String v: variables.keySet()) {
            value = variables.get(v);

            ret = ret.replaceAll(
                      "\\$\\{" + v + "}",
                      (value != null) ? value.toString() : nullValue
            );
        }

        return ret;

    }

    /**
     * Replaces all occurrences of ${variable} with the corresponding value if
     * variable is a key of the map variables. If variable is not found, the
     * placeholder is not replaced.<br>
     * It is equivalent of <code>replace(str, "null", variables)</code>
     *
     * @param str the string to replace - NOT NULL
     * @param variables the map with the (variable,value) pairs - NOT NULL
     *
     * @return the replaced string
     */
    public static String replace(final String str, final Map<String,Object> variables) {
        return replace(str, "null", variables);
    }

    /**
     * Converts a string expressing a size into the number of bytes.
     *
     * Examples:
     *  "100K" -->     102400
     *   "50M" -->   52428800
     *    "1G" --> 1073741824
     * @param size the string expressing a size
     * @return the corresponding number of bytes
     * @throws NumberFormatException if an error occurs
     */
    public static long converterStringSizeInBytes(String size)
    throws NumberFormatException {

        long sizeInBytes = 0;

        if (size == null || size.length() == 0) {
            return 0;
        }

        size = size.toLowerCase();
        size = size.replace(',', '.');
        size = size.trim();

        int m = 0;

        if (size.endsWith("b")) {
            size = size.substring(0, size.length() - 1);
        }

        if (size.endsWith("g")) {
            size = size.substring(0, size.length() - 1);
            size = size.trim();
            m = 1024 * 1024 * 1024;

        } else if (size.endsWith("m")) {
            size = size.substring(0, size.length() - 1);
            size = size.trim();
            m = 1024 * 1024;

        } else if (size.endsWith("k")) {
            size = size.substring(0, size.length() - 1);
            size = size.trim();
            m = 1024;

        } else {
            m = 1;
        }
        sizeInBytes = (long) (Double.parseDouble(size) * m);

        return sizeInBytes;
    }

}
