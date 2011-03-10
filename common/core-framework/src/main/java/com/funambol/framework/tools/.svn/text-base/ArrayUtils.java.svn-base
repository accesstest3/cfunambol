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

import java.lang.reflect.Array;


/**
 * Collection of tools for the server
 *
 *
 * @version $Id: ArrayUtils.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class ArrayUtils {

    /**
     * Merges the two given arrays in a single array, putting the elements of
     * the former array first and the elements of the latter array. The returned
     * array will be of the type specified by <i>elementClass</i>
     *
     * @param a the former array - NULL
     * @param b the latter array - NULL
     * @param elementType the class of the elements
     *
     * @return an array containing the elements of <i>a</i> and <i>b</i>
     */
    public static Object mergeArrays(Object[] a, Object[] b, Class elementType) {

        int size = 0;

        if (a != null) size += a.length;
        if (b != null) size += b.length;

        Object newArray = Array.newInstance(elementType, size);

        int newArrayPos = 0;
        if (a != null) {
            System.arraycopy(a, 0, newArray, newArrayPos, a.length);
            newArrayPos = a.length;
        }

        if (b != null) {
            System.arraycopy(b, 0, newArray, newArrayPos, b.length);
        }

        return newArray;

    }

}
