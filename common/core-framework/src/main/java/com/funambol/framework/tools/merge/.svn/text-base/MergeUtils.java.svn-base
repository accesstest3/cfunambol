/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.framework.tools.merge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * @version $Id: MergeUtils.java,v 1.2 2007-11-28 11:13:37 nichele Exp $
 */
public class MergeUtils {

    
    /**
     *
     */
    public static MergeResult compareStrings(String stringA, String stringB) {

        MergeResult result =  new MergeResult(false, false);

        if (stringA == null && stringB == null) {
            result = new MergeResult(false, false);
        }

        if (stringA != null && stringB != null) {

            if (stringA.trim().equals(stringB.trim())) {
                result = new MergeResult(false, false);
                return result;
            }

            if (stringA.trim().length() > 0) {
                result = new MergeResult(false, true);
                result.addPropertyB("string", stringA + ", " + stringB);
                return result;
            }

            if (stringB.trim().length() > 0) {
                result = new MergeResult(true, false);
                result.addPropertyA("string", stringA + ", " + stringB);
                return result;
            }

            //
            // This must not hap
            //
            result = new MergeResult(false, false);
        }

        if (stringA == null && stringB != null) {

            if (stringB.trim().length() > 0) {
                result = new MergeResult(true, false);
                result.addPropertyA("string", "null, " + stringB);
            } else {
                result = new MergeResult(false, false);
            }

        }

        if (stringA != null && stringB == null) {

            if (stringA.trim().length() > 0) {
                result = new MergeResult(false, true);
                result.addPropertyB("string", stringA + ", null");
            } else {
                result = new MergeResult(false, false);
            }
        }

        return result;
    }


    /**
     *
     */
    public static MergeResult compareIntegers(Integer integerA, Integer integerB) {

        MergeResult result = null;

        if (integerA == null && integerB == null) {
            return  new MergeResult(false, false);
        }

        if (integerA != null && integerB == null) {
            result = new MergeResult();
            result.addPropertyB("Integer", integerA.intValue() + ", null");
            return result;
        }

        if (integerA == null && integerB != null) {
            result = new MergeResult();
            result.addPropertyA("Integer", "null, " + integerB.intValue());
            return result;
        }

        if (integerA.compareTo(integerB) != 0) {
            //
            // The values are different so
            // an update on integerB is required
            //
            result = new MergeResult();
            result.addPropertyB("Integer", integerA.intValue() + ", " + integerB.intValue());
            return result;
        }

        return new MergeResult(false, false);
    }


    /**
     *
     */
    public static MergeResult compareShorts(Short shortA, Short shortB) {

        MergeResult result = null;

        if (shortA == null && shortB == null) {
            return  new MergeResult(false, false);
        }

        if (shortA != null && shortB == null) {
            result = new MergeResult();
            result.addPropertyB("Short", shortA.shortValue() + ", null");
            return result;
        }

        if (shortA == null && shortB != null) {
            result = new MergeResult();
            result.addPropertyA("Short", "null, " + shortB.shortValue());
            return result;
        }

        if (shortA.compareTo(shortB) != 0) {
            //
            // The values are different so
            // an update on shortB is required
            //
            result = new MergeResult();
            result.addPropertyB("Short", shortA.shortValue() + ", " + shortB.shortValue());
            return result;
        }

        return new MergeResult(false, false);
    }


    /**
     *
     */
    public static MergeResult compareBoolean(Boolean booleanA, Boolean booleanB) {

        MergeResult result = null;

        if (booleanA == null && booleanB == null) {
            return  new MergeResult(false, false);
        }

        if (booleanA != null && booleanB == null) {
            result = new MergeResult();
            result.addPropertyB("Boolean", booleanA.booleanValue() + ", null");
            return result;
        }

        if (booleanA == null && booleanB != null) {
            result = new MergeResult();
            result.addPropertyA("Boolean", "null, " + booleanB.booleanValue());
            return result;
        }

        if (booleanA.booleanValue() != booleanB.booleanValue()) {
            //
            // The values are different so
            // an update on booleanB is required
            //
            result = new MergeResult();
            result.addPropertyB("Boolean",
                                booleanA.booleanValue() + ", " + booleanB.booleanValue());
            return result;
        }

        return new MergeResult(false, false);
    }


    /**
     *
     */
    public static MergeResult mergeMap(Map mapA, Map mapB) {

        MergeResult result = new MergeResult();

        if (mapA == null && mapB != null) {
            mapA = new HashMap();
            mapA.putAll(mapB);
            result.addPropertyA("MapA", "MapA is null, MapB isn't null");
            return result;
        }

        if (mapA != null && mapB == null) {
            mapB = new HashMap();
            mapB.putAll(mapA);
            result.addPropertyB("MapB", "MapA isn't null, MapB is null");
            return result;
        }

        if (mapA == null && mapB == null) {
            return result;
        }

        Iterator itA          = mapA.keySet().iterator();
        String key            = null;
        String valueA         = null;
        String valueB         = null;
        MergeResult tmpResult = null;

        while (itA.hasNext()) {
            key = (String) itA.next();
            valueA = (String) mapA.get(key);
            valueB = (String) mapB.get(key);

            tmpResult = MergeUtils.compareStrings(valueA, valueB);
            if (tmpResult.isSetARequired()) {
                mapA.put(key, valueB);
            } else if (tmpResult.isSetBRequired()) {
                mapB.put(key, valueA);
            }

            result.addMergeResult(tmpResult, key);
        }

        Iterator itB          = mapB.keySet().iterator();

        while (itB.hasNext()) {
            key = (String) itB.next();
            valueA = (String) mapA.get(key);

            if (valueA != null) {
                //
                // The property is already in MapA so we can continue
                // (we already have handled this prop in the previous cycle)
                //
                continue;
            }

            valueB = (String) mapB.get(key);

            tmpResult = MergeUtils.compareStrings(valueA, valueB);
            if (tmpResult.isSetARequired()) {
                mapA.put(key, valueB);
            } else if (tmpResult.isSetBRequired()) {
                mapB.put(key, valueA);
            }

            result.addMergeResult(tmpResult, key);
        }

        return result;
    }
    
}
