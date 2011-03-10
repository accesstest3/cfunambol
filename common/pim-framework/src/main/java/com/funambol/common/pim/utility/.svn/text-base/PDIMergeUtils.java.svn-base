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
package com.funambol.common.pim.utility;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.tools.merge.MergeUtils;
import com.funambol.framework.tools.merge.MergeResult;

import com.funambol.common.pim.calendar.Attendee;
import com.funambol.common.pim.calendar.RecurrencePattern;
import com.funambol.common.pim.calendar.Reminder;
import com.funambol.common.pim.common.MergeableWithURI;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.PropertyWithTimeZone;
import com.funambol.common.pim.common.TypifiedProperty;
import com.funambol.common.pim.contact.IMPPAddress;

/**
 * @version $Id: PDIMergeUtils.java,v 1.7 2008-07-17 15:51:45 luigiafassina Exp $
 */
public class PDIMergeUtils extends MergeUtils {


    /**
     * Compares two reminders and gives the result in the form of a MergeResult 
     * object.
     * 
     * @param reminderA the first Reminder object to compare
     * @param reminderB the second Reminder object to compare
     * @return a MergeResult object
     * @deprecated Since version 7.0.5, this method has been renamed to 
     *             compareReminders
     */
    public static MergeResult compareReminder(Reminder reminderA,
                                              Reminder reminderB) {
        return compareReminders(reminderA, reminderB);
    }
    
    /**
     * Compares two reminders and gives the result in the form of a MergeResult 
     * object.
     * 
     * @param reminderA the first Reminder object to compare
     * @param reminderB the second Reminder object to compare
     * @return a MergeResult object
     */    
    public static MergeResult compareReminders(Reminder reminderA,
                                               Reminder reminderB) {

        MergeResult result = null;

        if (reminderA == null && reminderB == null) {
            return new MergeResult(false, false);
        }

        if (reminderA != null && reminderB == null) {
            result = new MergeResult();
            result.addPropertyB("Reminder", reminderA.toString() + ", null");
            return result;
        }

        if (reminderA == null && reminderB != null) {
            result = new MergeResult();
            result.addPropertyA("Reminder", "null, " + reminderB.toString());
            return result;
        }

        //
        // reminderA and reminderB aren't null
        //
        String soundA = "";
        String soundB = "";
        if (reminderA.getSoundFile() != null) {
            soundA = reminderA.getSoundFile();
        }
        if (reminderB.getSoundFile() != null) {
            soundB = reminderB.getSoundFile();
        }

        if ( (reminderA.isActive()       != reminderB.isActive())    ||
             (reminderA.getMinutes()     != reminderB.getMinutes())  ||
             (!soundA.equals(soundB))                                ||
             (reminderA.getOptions()     != reminderB.getOptions())  ||
             (reminderA.getInterval()    != reminderB.getInterval()) ||
             (reminderA.getRepeatCount() != reminderB.getRepeatCount())) {

            // The reminders are different, the A wins
            result = new MergeResult();
            result.addPropertyB("Reminder", reminderA.toString() + ", " + reminderB.toString());
            return result;
        }

        return new MergeResult(false, false);
    }


    /**
     * Merges the given two lists of TypifiedProperty objects
     *
     * @param list1 the first list (must be not null)
     * @param list2 the second list
     * @return true if the first list is changed
     */
    public static MergeResult mergeTypifiedPropertiestList(List list1,
                                                           List list2) {

        if (list1 == null || list2 == null) {
            throw new IllegalStateException("The lists must not be null");
        }

        List typeAlreadyHandled = new ArrayList();

        MergeResult listMergeResult = new MergeResult();

        if (list2.size() > 0) {
            Iterator it        = list2.iterator();
            TypifiedProperty p = null;
            int pos2           = -1;

            while (it.hasNext()) {
                p    = (TypifiedProperty) it.next();
                pos2 = list2.indexOf(p);

                typeAlreadyHandled.add(p.getPropertyType());

                //
                // We add/set a property only if his value isn't empty (or null)
                //
                int pos1 = list1.indexOf(p);
                if (pos1 < 0) {
                    if (StringUtils.isNotEmpty(p.getPropertyValueAsString())) {
                        list1.add(p);
                        listMergeResult.addPropertyA(p.getPropertyType());
                    }
                } else {
                    //
                    // There is already a property with the same type
                    //
                    MergeResult mergePropertyResult = compareProperties(
                            (TypifiedProperty) list1.get(pos1),
                            p);

                    if (mergePropertyResult.isSetARequired()) {
                        list1.set(pos1, p);
                        listMergeResult.addPropertyA(p.getPropertyType());
                    }

                    if (mergePropertyResult.isSetBRequired()) {
                        list2.set(pos2, (TypifiedProperty) list1.get(pos1));
                        listMergeResult.addPropertyB(((TypifiedProperty) list1.get(pos1)).getPropertyType());
                    }
                }
            }
        }

        //
        // Now we loop on list1 ignoring the properties already handled
        // The properties not handled are sure only in list1
        //
        if (list1.size() > 0) {
            Iterator it        = list1.iterator();
            TypifiedProperty p = null;

            while (it.hasNext()) {
                p    = (TypifiedProperty) it.next();

                if (typeAlreadyHandled.indexOf(p.getPropertyType()) != -1) {
                    //
                    // This property is already handled
                    //
                    continue;
                }

                //
                // We add/set a property only if his value isn't empty (or null)
                //
                if (StringUtils.isNotEmpty(p.getPropertyValueAsString())) {
                    list2.add(p);
                    listMergeResult.addPropertyB(p.getPropertyType());
                }
            }
        }

        return listMergeResult;
    }

    /**
     * Merges two lists of IMPPAddress objects.
     *
     * @param listA the first list (must not be null)
     * @param listB the second list (must not be null)
     * @return the merge outcome as a MergeResult object
     */
    /*
    public static MergeResult mergeIMPPAddressLists(List<IMPPAddress> listA,
                                                    List<IMPPAddress> listB) {

        if (listA == null || listB == null) {
            throw new IllegalStateException("The lists must not be null");
        }

        //@todo ALL

        return null;
    }
     */

    /**
     * Compares two recurrence patterns and gives the result in the form of a
     * MergeResult object.
     * 
     * @param patternA the first RecurrencePattern object to compare
     * @param patternB the second RecurrencePattern object to compare
     * @return a MergeResult object
     * @deprecated Since version 7.0.5, this method has been renamed to 
     *             compareRecurrencePatterns
     */
    public static MergeResult compareRecurrencePattern(
            RecurrencePattern patternA, RecurrencePattern patternB) {
        return compareRecurrencePatterns(patternA, patternB);
    }

    /**
     * Compares two recurrence patterns and gives the result in the form of a
     * MergeResult object.
     * 
     * @param patternA the first RecurrencePattern object to compare
     * @param patternB the second RecurrencePattern object to compare
     * @return a MergeResult object
     */
    public static MergeResult compareRecurrencePatterns(
            RecurrencePattern patternA, RecurrencePattern patternB) {

        MergeResult result = null;

        if (patternA == null && patternB == null) {
            return new MergeResult(false, false);
        }

        if (patternA != null && patternB == null) {
            result = new MergeResult();
            result.addPropertyB("RecurrencePattern", patternA.toString() + ", null");
            return result;
        }

        if (patternA == null && patternB != null) {
            result = new MergeResult();
            result.addPropertyA("RecurrencePattern", "null, " + patternB.toString());
            return result;
        }

        //
        // patternA and patternB aren't null
        //
        if ( (patternA.getTypeId()           != patternB.getTypeId())         ||
             (patternA.getInterval()         != patternB.getInterval())       ||
             (patternA.getMonthOfYear()      != patternB.getMonthOfYear())    ||
             (patternA.getDayOfMonth()       != patternB.getDayOfMonth())     ||
             (patternA.getDayOfWeekMask()    != patternB.getDayOfWeekMask())  ||
             (patternA.getInstance()         != patternB.getInstance())       ||
             (!StringUtils.equals(patternA.getStartDatePattern(), patternB.getStartDatePattern())) ||
             (!StringUtils.equals(patternA.getEndDatePattern(), patternB.getEndDatePattern())) ||
             (patternA.getOccurrences() != patternB.getOccurrences())         ||
             (patternA.isNoEndDate()    != patternB.isNoEndDate()) ||
             (!patternA.getExceptions().equals(patternB.getExceptions()))
           ) {

            // The patterns are different, the first one wins
            result = new MergeResult();
            result.addPropertyB("RecurrencePattern", patternA.toString() + ", " + patternB.toString());
            return result;
        }

        return new MergeResult(false, false);
    }



    /**
     *
     */
    public static MergeResult compareProperties(Property propA, Property propB) {

        MergeResult result = null;

        if (propA == null && propB == null) {
            return new MergeResult(false, false);
        }

        if (propA != null && propB == null) {
            if (StringUtils.isNotEmpty(propA.getPropertyValueAsString())) {
                result = new MergeResult();
                result.addPropertyB("Property", propA.getPropertyValueAsString() + ", null");
                return result;
            } else {
                return new MergeResult(false, false);
            }
        }

        if (propA == null && propB != null) {

            if (StringUtils.isNotEmpty(propB.getPropertyValueAsString())) {
                result = new MergeResult();
                result.addPropertyA("Property", "null, " + propB.getPropertyValueAsString());
                return result;
            } else {
                return new MergeResult(false, false);
            }

        }

        //
        // propA and propb aren't null
        //
        String valueA = null;
        String valueB = null;

        valueA = propA.getPropertyValueAsString();
        valueB = propB.getPropertyValueAsString();

        result = compareStrings(valueA, valueB);

        MergeResult finalResult = new MergeResult();
        finalResult.addMergeResult(result, "Property");

        return finalResult;
    }

    /**
     * Compares properties that have the specified timezone like the dates.
     *
     */
    public static MergeResult comparePropertiesWithTZ(PropertyWithTimeZone propA,
                                                      PropertyWithTimeZone propB) {

        MergeResult result = compareProperties(propA, propB);

        if(!result.isSetARequired() && !result.isSetBRequired()) {

            String valueA = propA.getTimeZone();
            String valueB = propB.getTimeZone();

            result = compareStrings(valueA, valueB);
        }

        MergeResult finalResult = new MergeResult();
        finalResult.addMergeResult(result, "PropertyWithTimeZone");

        return finalResult;
    }

    /**
     * Merges attendee lists.
     *
     * @param listA the first list to merge (it could prevail in some conflicts)
     * @param listB the second list to merge
     * @return a MergeResult object
     *
     * @deprecated Since version 8.2.4, mergeLists can always be used instead.
     */
    public static MergeResult mergeAttendeeLists(
            List<Attendee> listA, List<Attendee> listB) {

        return mergeLists(listA, listB, "Attendees");

    }

    /**
     * Merges two lists of any mergeable item that has a URI.
     *
     * @param <T> a class like Attendee or IMPPAddress
     * @param listA the first list to merge (it could prevail in some conflicts)
     * @param listB the second list to merge
     * @param description the description of the item class, to be used in the
     *                    text output incorporated in the MergeResult object
     * @return a MergeResult object
     */
    public static <T extends MergeableWithURI> MergeResult mergeLists(List<T> listA,
                                                                      List<T> listB,
                                                                      String description) {

        Map<String, T> mergedMap = new HashMap<String, T>();

        boolean aNeedsBeChanged = false;
        boolean bNeedsBeChanged = false;
        for (T item : listB) {
            mergedMap.put(item.getUri(), item);
        }
        for (T item : listA) {
            /*
             * If an item with a certain URI is already present in listB,
             * it will be overwritten in mergedMap by the listA element with the
             * same URI
             */
            String uri = item.getUri();
            if (mergedMap.containsKey(uri)) {

                 /*
                 * Merges the items with the same URI in both lists; one of the
                 * lists may need to be updated as a consequence of the merge.
                 */
                MergeResult itemMergeResult = item.merge(mergedMap.get(uri));
                aNeedsBeChanged = itemMergeResult.isSetARequired();
                bNeedsBeChanged = itemMergeResult.isSetBRequired();

                if (aNeedsBeChanged || bNeedsBeChanged) {
                    mergedMap.put(uri, item); // element must be updated
                } // else, no need to update mergedMap: element already there

            } else { // this element was not present in listB
                mergedMap.put(uri, item); // mergedMap needs be augmented
                bNeedsBeChanged = true;
            }
        }
        /*
         * If listA has the same size as mergedMap, they must contain the same
         * elements
         */
        if (!aNeedsBeChanged) {
            aNeedsBeChanged = (listA.size() < mergedMap.size());
        }

        MergeResult mergeResult = new MergeResult(aNeedsBeChanged, bNeedsBeChanged);
        if (aNeedsBeChanged) {
            mergeResult.addPropertyA(description,
                                     uriListToString(listA) +
                                     ", " +
                                     uriListToString(mergedMap.values()));
            listA.clear();
            listA.addAll(mergedMap.values());
        }
        if (bNeedsBeChanged) {
            mergeResult.addPropertyB(description,
                                     uriListToString(listB) +
                                     ", " +
                                     uriListToString(mergedMap.values()));
            listB.clear();
            listB.addAll(mergedMap.values());
        }
        return mergeResult;
    }

    private static <T extends MergeableWithURI> String uriListToString(Collection<T> items) {

        if(items.size() == 0) {
            return "{}";
        }

        StringBuffer description = new StringBuffer("");
        char separator = '{';
        for (T attendee : items) {
            description.append(separator).append(attendee.getUri());
            separator = ',';
        }
        return description.append('}').toString();
    }
}
