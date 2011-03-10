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
package com.funambol.framework.engine;

import java.util.ArrayList;

/**
 *
 *
 * @version $Id: Util.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public final class Util {
    /**
     * Creates an array of Boolean starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of Boolean
     */
    public static boolean[] createArrayOfBooleans(String[] string_values) {
        boolean[] array = new boolean[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = Boolean.getBoolean(string_values[i]);
        }  // next i

        return array;
    } // createArrayOfBooleans

    /**
     * Creates an array of Character starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of Character
     */
    public static char[] createArrayOfCharacters(String[] string_values) {
        char[] array = new char[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = (string_values[i].length()>0) ? string_values[i].charAt(0) : ' ';
        }  // next i

        return array;
    } // createArrayOfCharacters

    /**
     * Creates an array of Double starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of Double
     */
    public static double[] createArrayOfDoubles(String[] string_values) {
        double[] array = new double[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = Double.parseDouble(string_values[i]);
        }  // next i

        return array;
    } // createArrayOfDoubles

    /**
     * Creates an array of Float starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of Float
     */
    public static float[] createArrayOfFloats(String[] string_values) {
        float[] array = new float[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = Float.parseFloat(string_values[i]);
        }  // next i

        return array;
    } // createArrayOfFloats

    /**
     * Creates an array of Integer starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of int
     */
    public static int[] createArrayOfIntegers(String[] string_values) {
        int[] array = new int[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = Integer.parseInt(string_values[i]);
        }  // next i

        return array;
    } // createArrayOfIntegers

    /**
     * Creates an array of Long starting from an array of strings
     *
     * @param       string_values   array of value to be converted
     *
     * @return      an array of Long
     */
    public static long[] createArrayOfLongs(String[] string_values) {
        long[] array = new long[string_values.length];

        for(int i=0; i<string_values.length; ++i) {
            array[i] = Long.parseLong(string_values[i]);
        }  // next i

        return array;
    } // createArrayOfLongs

    // =========================================================================

    /**
     * Makes a string concatenating the capitalized version of the given words.<p>
     *
     * For example:
     * <pre>
     * Util.makeCapitalizedString(new String[] { "bean", "blue" });
     * </pre>
     * will return <pre>BeanBlue</pre>.<p>
     *
     * null strings in the <i>words</i> array will be silenty ignored.
     *
     * @param   words     the strings to capitalize and concatenate
     *
     * @return  the capitalized string
     *
     */
    public static String makeCapitalizedString(String[] words) {
        StringBuffer sb = new StringBuffer();

        for(int i=0; i<words.length; ++i) {
            if (words[i] != null) sb.append(capitalizeWord(words[i]));
        }  // next i

        return sb.toString();
    }

    /**
     * Capitalizes a word.<p>
     *
     * For example:
     * <pre>
     * Util.capitalizeWord("bean");
     * </pre>
     * will return <pre>Bean</pre>.
     *
     * @param   word    the word to capitalize
     *
     * @return  the capitalized word
     */
    public static String capitalizeWord(String word) {
        int len = word.length();

        if (len == 1) return word.toUpperCase();

        return word.substring(0, 1).toUpperCase() +
               word.substring(1   )               ;
    }

    /**
     * Returns a string representation of an array of Objects. It translates
     * the array in an ArrayList and then invokes toString().
     *
     * @param array     the array
     *
     * @return the string representation of the given array
     */
    public static String arrayToString(Object[] array) {
        ArrayList arrayList = new ArrayList();

        for (int i=0; i<array.length; ++i) {
            arrayList.add(array[i]);
        }

        return arrayList.toString();
    }
}
