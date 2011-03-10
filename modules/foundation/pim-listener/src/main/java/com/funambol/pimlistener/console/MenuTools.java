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

package com.funambol.pimlistener.console;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Contains useful methods to read values form System.in
 * @version $Id: MenuTools.java,v 1.8 2008-03-26 22:22:14 stefano_fornari Exp $
 */
public class MenuTools {

    /**
     * Reads a boolean value showing the given message. It accepts y (true) or
     * n (false)
     * @param message the message to show
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static boolean readBoolean(String message)
    throws Exception {

        message = message + " [y/n]: ";

        System.out.print(message);
        String value = null;

        while (true) {
            value = readValue();

            if ("Y".equalsIgnoreCase(value)){
                return true;
            } else if ("N".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println("Value not valid");
            System.out.print(message);
        }
    }

    /**
     * Reads a boolean value showing the given message and returning the given default
     * value (if no value is inserted)
     * @param message the message to show
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static boolean readBoolean(String message, boolean defaultValue)
    throws Exception {

        message = message + " [y/n] (";

        if (defaultValue) {
            message = message + "y";
        } else {
            message = message + "n";
        }
        message = message + "): ";

        System.out.print(message);
        String value = null;

        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            if (value.equalsIgnoreCase("Y")){
                return true;
            } else if (value.equalsIgnoreCase("N")){
                return false;
            }
            System.out.println("Value not valid");
            System.out.print(message);
        }
    }

    /**
     * Reads an integer value from System.in
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static int readInteger()
    throws Exception {

        String value = null;
        int intValue = 0;
        while (true) {
            value = readValue();
            try {
                intValue = Integer.parseInt(value);
                return intValue;
            } catch (NumberFormatException ne) {
                //
                // Nothing to do
                //
            }
        }
    }

    /**
     * Reads an integer value showing the given message and returning the given default
     * value (if no value is inserted)
     * @param message the message to show
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static int readInteger(String message, int defaultValue)
    throws Exception {

        message = message + " (" + defaultValue + "): ";
        System.out.print(message);
        String value = null;
        int intValue = 0;
        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            try {
                intValue = Integer.parseInt(value);
                return intValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number greater than 0");
                System.out.print(message);
            }
        }
    }

    /**
     * Reads a int value showing the given message and returning the given default
     * value (if no value is inserted). Moreover a check on the inserted value
     * is performed (it must be between minValue and maxValue)
     * @param message the message to show
     * @param minValue the min value of the acceptable values
     * @param maxValue the max value of the acceptable values
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static int readInteger(String message, int minValue, int maxValue, int defaultValue)
    throws Exception {

        message = message + " (" + defaultValue + "): ";
        System.out.print(message);
        String value    = null;
        int    intValue = 0;
        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            try {
                intValue = Integer.parseInt(value);
                if (intValue < minValue || intValue > maxValue) {
                    throw new NumberFormatException();
                }
                return intValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number between "
                                   + minValue + " and " + maxValue);
                System.out.print(message);
            }
        }
    }

    /**
     * Reads a long value showing the given message and returning the given default
     * value (if no value is inserted)
     * @param message the message to show
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static long readLong(String message, long defaultValue)
    throws Exception {

        message = message + " (" + defaultValue + "): ";
        System.out.print(message);
        String value     = null;
        long  longValue = 0;
        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            try {
                longValue = Long.parseLong(value);
                return longValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number greater than 0");
                System.out.print(message);
            }
        }
    }

    /**
     * Reads an long value from System.in showing the given message
     * @param message the message to show
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static long readLong(String message) throws Exception {

        message = message + ": ";
        System.out.print(message);
        String value     = null;
        long  longValue = 0;
        while (true) {
            value = readValue();
            try {
                longValue = Long.parseLong(value);
                return longValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number greater than 0");
                System.out.print(message);
            }
        }
    }

    /**
     * Reads a long value showing the given message and returning the given default
     * value (if no value is inserted). Moreover a check on the inserted value
     * is performed (it must be between minValue and maxValue)
     * @param message the message to show
     * @param minValue the min value of the acceptable values
     * @param maxValue the max value of the acceptable values
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static long readLong(String message, long minValue, long maxValue, long defaultValue)
    throws Exception {

        message = message + " (" + defaultValue + "): ";
        System.out.print(message);
        String value     = null;
        long  longValue = 0;
        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                return defaultValue;
            }
            try {
                longValue = Long.parseLong(value);
                if (longValue < minValue || longValue > maxValue) {
                    throw new NumberFormatException();
                }
                return longValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number between "
                                   + minValue + " and " + maxValue);
                System.out.print(message);
            }
        }
    }

    /**
     * Reads a long value showing the given message and checking that the inserted
     * values is between minValue and maxValue
     * @param message the message to show
     * @param minValue the min value of the acceptable values
     * @param maxValue the max value of the acceptable values
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static long readLong(String message, long minValue, long maxValue)
    throws Exception {

        message = message + ": ";
        System.out.print(message);
        String value     = null;
        long  longValue = 0;
        while (true) {
            value = readValue();
            try {
                longValue = Long.parseLong(value);
                if (longValue < minValue || longValue > maxValue) {
                    throw new NumberFormatException();
                }
                return longValue;
            } catch (NumberFormatException ne) {
                System.out.println("Value not valid. Please insert a number between "
                                   + minValue + " and " + maxValue);
                System.out.print(message);
            }
        }
    }

   /**
     * Read a String from System.in
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static String readValue()
    throws Exception {

        InputStreamReader iReader = new InputStreamReader(System.in);
        BufferedReader    indata  = new BufferedReader(iReader);
        String            value   = null;

        try {
            value = indata.readLine();
        } catch (Exception ex) {
            throw new Exception("Error reading input value (" + ex.getMessage() +
                                ")");
        }

        return value;
    }

    /**
     * Read a String from System.in showing the given message
     * @param message the message to show
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static String readValue(String message)
    throws Exception {
        message = message + ": ";
        System.out.print(message);
        return readValue();
    }

    /**
     * Reads a string value showing the given message and returning the given default
     * value (if no value is inserted)
     * @param message the message to show
     * @param defaultValue the default value to return if no value is inserted
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value or the default one
     */
    public static String readValue(String message, String defaultValue)
    throws Exception {
        message = message + " (" + defaultValue + "): ";
        System.out.print(message);
        String value = readValue();
        if (value == null || value.length() == 0) {
            value = defaultValue;
        }
        return value;
    }

    /**
     * Reads a string value showing the given message. An empty value is not allowed
     * @param message the message to show
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    public static String readNotEmptyValue(String message)
    throws Exception {
        message = message + ": ";
        System.out.print(message);
        String value = null;
        while (true) {
            value = readValue();
            if (value == null || value.length() == 0) {
                System.out.println("Empty value not allowed");
                System.out.print(message);
            } else {
                return value;
            }
        }
    }

}
