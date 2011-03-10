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
package com.funambol.email.engine.source;

import com.funambol.email.exception.EntityException;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.SyncItemInfo;
import java.util.ArrayList;
import com.funambol.email.util.Utility;
import com.funambol.framework.engine.SyncItemKey;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.mail.search.ComparisonTerm;


/**
 * This class includes the methods to set the New Items List,
 * Updated Items List and Daleted Items List, in according with
 * the filter settings
 *
 * @version $Id: HelperForGetter.java,v 1.2 2008-04-07 14:53:06 gbmiglia Exp $
 *
 */
public class HelperForGetter {


    // ---------------------------------------------------------- Public Methods

    /**
     * Creates new items id array
     *
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all server SyncItemInfo
     * @return new items id array ArrayList
     * @throws EntityException
     */
    public static ArrayList setNewItemKeys(Map localItems,
                                           Map serverItems)
     throws EntityException {

        ArrayList       newRows = new ArrayList();
        SyncItemInfo scrcii  = null;
        SyncItemInfo lcrcii  = null;
        String          sGUID   = null;

        try {
            Iterator values = serverItems.values().iterator();

            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID = scrcii.getGuid().getKeyAsString();
               lcrcii = (SyncItemInfo)localItems.get(sGUID);

               if (lcrcii == null){
                   newRows.add(scrcii.getGuid());
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting New Item List " + e.getMessage());
        }

        return newRows;

    }

    public static Map setNewItems(
            Map localItems,
            Map serverItems)
            throws EntityException {

        Map<String, SyncItemInfo> newItems = new LinkedHashMap<String, SyncItemInfo>();

        try {
            Iterator items = serverItems.values().iterator();

            while (items.hasNext()) {
               SyncItemInfo serverItem = (SyncItemInfo)items.next();
               String       sGUID      = serverItem.getGuid().getKeyAsString();
               SyncItemInfo localItem  = (SyncItemInfo)localItems.get(sGUID);

               if (localItem == null){
                   newItems.put(sGUID, serverItem);
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting new items list: " + e.getMessage());
        }
        return newItems;
    }
    /**
     * Creates new items id array
     *
     * @param timefilter Date
     * @param comparison Ge or LT
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all server SyncItemInfo
     * @return new items id array
     * @throws EntityException
     */
    public static ArrayList setNewItemKeysWithTimeFilter(java.util.Date timefilter,
                                                         int comparison,
                                                         LinkedHashMap localItems,
                                                         LinkedHashMap serverItems)
     throws EntityException {

        ArrayList       newRows = new ArrayList();
        SyncItemInfo scrcii  = null;
        SyncItemInfo lcrcii  = null;
        String          sGUID   = null;
        java.util.Date  demail  = null;

        try {
            Iterator values = serverItems.values().iterator();

            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID = scrcii.getGuid().getKeyAsString();;
               lcrcii = (SyncItemInfo)localItems.get(sGUID);
               
               if (lcrcii == null){
                    if (comparison == ComparisonTerm.GE){
                        demail = scrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Afterd2(demail, timefilter)){
                                newRows.add(scrcii.getGuid());
                            }
                        }
                    } else {
                        demail = scrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Befored2(demail, timefilter)){
                                newRows.add(scrcii.getGuid());
                            }
                        }
                    }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting NewItem List " + e.getMessage());
        }


        return newRows;

    }

    /**
     *
     * Creates new items id array
     *
     * @param idfilter String
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all server SyncItemInfo
     * @return new items id array ArrayList
     * @throws EntityException
     */
    public static ArrayList setNewItemKeysWithIdFilter(String idfilter,
                                                       Map localItems,
                                                       Map serverItems)
     throws EntityException {

        ArrayList       newRows = new ArrayList();
        SyncItemInfo scrcii  = null;
        SyncItemInfo lcrcii  = null;
        String          sGUID   = null;

        try {
            Iterator values = serverItems.values().iterator();

            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID = scrcii.getGuid().getKeyAsString();;
               lcrcii = (SyncItemInfo)localItems.get(sGUID);
               if (lcrcii == null){
                   if (idfilter.equals(sGUID)){
                       newRows.add(scrcii.getGuid());
                   }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting New Item List " + e.getMessage());
        }


        return newRows;

    }

    
    /**
     * Creates deleted items id array
     *
     * @param localItems map with all local CrcSyncItemInfo
     * @param serverItems map with all Mail Server CrcSyncItemInfo
     * @return deleted items id array
     */
    public static ArrayList setDeletedItemKeys(Map localItems,
                                               Map serverItems)
      throws EntityException {

        ArrayList       deleteRows = new ArrayList();
        SyncItemInfo scrcii     = null;
        SyncItemInfo lcrcii     = null;
        String          lGUID      = null;

        try {
            Iterator values = localItems.values().iterator();

            while (values.hasNext()) {
               lcrcii = (SyncItemInfo)values.next();
               lGUID = lcrcii.getGuid().getKeyAsString();
               scrcii = (SyncItemInfo)serverItems.get(lGUID);
               if (scrcii == null){
                   deleteRows.add(lcrcii.getGuid());
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Deleted Item List " + e.getMessage());
        }


        return deleteRows;

    }

    public static Map<String, SyncItemInfo> setDeletedItems(
            Map localItems,
            Map serverItems)
            throws EntityException {

        Map<String, SyncItemInfo> deletedItems = new LinkedHashMap<String, SyncItemInfo>();

        try {
            Iterator items = localItems.values().iterator();

            while (items.hasNext()) {

                SyncItemInfo localItem = (SyncItemInfo) items.next();
                String lGUID = localItem.getGuid().getKeyAsString();
                SyncItemInfo serverItem = (SyncItemInfo) serverItems.get(lGUID);
                
                if (serverItem == null) {
                    deletedItems.put(lGUID, localItem);
                }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting deleted items list " + e.getMessage());
        }
        return deletedItems;
    }
    /**
     * Creates deleted items id array
     *
     * @param timefilter Date
     * @param comparison GE or LT
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all Mail Server SyncItemInfo
     * @return deleted items id array ArrayList
     * @throws EntityException
     */
    public static ArrayList setDeletedItemKeysWithTimeFilter(java.util.Date timefilter,
                                                             int comparison,
                                                             LinkedHashMap localItems,
                                                             LinkedHashMap serverItems)
     throws EntityException {

        ArrayList       deleteRows = new ArrayList();
        SyncItemInfo scrcii     = null;
        SyncItemInfo lcrcii     = null;
        String          lGUID      = null;
        java.util.Date  demail     = null;

        try {
            Iterator values = localItems.values().iterator();

            while (values.hasNext()) {
               lcrcii = (SyncItemInfo)values.next();
               lGUID = lcrcii.getGuid().getKeyAsString();;
               scrcii = (SyncItemInfo)serverItems.get(lGUID);
               if (scrcii == null){
                    if (comparison == ComparisonTerm.GE){
                        demail = lcrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Afterd2(demail, timefilter)){
                                deleteRows.add(lcrcii.getGuid());
                            }
                        }
                    } else {
                        demail = lcrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Befored2(demail, timefilter)){
                                deleteRows.add(lcrcii.getGuid());
                            }
                        }
                    }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Deleted Item List " + e.getMessage());
        }


        return deleteRows;

    }

    /**
     * Creates deleted items id array
     *
     * @param idfilter String
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all Mail Server SyncItemInfo
     * @return deleted items id array
     * @throws EntityException
     */
    public static ArrayList setDeletedItemKeysWithIdFilter(String idfilter,
                                                           Map localItems,
                                                           Map serverItems)
      throws EntityException {

        ArrayList       deleteRows = new ArrayList();
        SyncItemInfo scrcii     = null;
        SyncItemInfo lcrcii     = null;
        String          lGUID      = null;

        try {
            Iterator values = localItems.values().iterator();

            while (values.hasNext()) {
               lcrcii = (SyncItemInfo)values.next();
               lGUID = lcrcii.getGuid().getKeyAsString();;
               scrcii = (SyncItemInfo)serverItems.get(lGUID);
               if (scrcii == null){
                    if (idfilter.equals(lGUID)){
                       deleteRows.add(lcrcii.getGuid());
                    }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Deleted Item List " + e.getMessage());
        }


        return deleteRows;

    }

    /**
     * Creates updated items id array
     *
     * @param localItems map with all local CrcSyncItemInfo
     * @param serverItems map with all Mail Server CrcSyncItemInfo
     * @return updated items id array
     */
    public static ArrayList setUpdatedItemKeys(Map localItems,
                                               Map serverItems)
     throws EntityException {

        ArrayList updateRows         = new ArrayList();
        long serverItemLastCrc ;
        long localItemLastCrc  ;
        SyncItemInfo scrcii = null;
        SyncItemInfo lcrcii = null;
        String sGUID = null;

        try {
            Iterator values = serverItems.values().iterator();
            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID = scrcii.getGuid().getKeyAsString();
               lcrcii = (SyncItemInfo)localItems.get(sGUID);
               if (lcrcii != null){
                  serverItemLastCrc = scrcii.getLastCrc () ;
                  localItemLastCrc  = lcrcii.getLastCrc () ;
                  if (serverItemLastCrc != localItemLastCrc) {
                      updateRows.add(scrcii.getGuid());
                  }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Updated Item List " + e.getMessage());
        }

        return updateRows ;
    }

    public static Map<String, SyncItemInfo> setUpdatedItems(
            Map localItems,
            Map serverItems)
            throws EntityException {

        Map<String, SyncItemInfo> updatedItems =
                new LinkedHashMap<String, SyncItemInfo>();

        try {
            Iterator items = serverItems.values().iterator();
            while (items.hasNext()) {
                
               SyncItemInfo serverItem = (SyncItemInfo)items.next();
               String       sGUID      = serverItem.getGuid().getKeyAsString();
               SyncItemInfo localItem  = (SyncItemInfo)localItems.get(sGUID);
               
               if (localItem != null){
                   
                  long serverItemLastCrc = serverItem.getLastCrc();
                  long localItemLastCrc  = localItem.getLastCrc();
                  
                  if (serverItemLastCrc != localItemLastCrc) {
                      updatedItems.put(sGUID, serverItem);
                  }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting updated items list " + e.getMessage());
        }
        return updatedItems;
    }

    /**
     * Creates updated items id array
     *
     * @param timefilter Date
     * @param comparison GE or LT
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all server SyncItemInfo
     * @return updated items id array
     * @throws EntityException
     */
    public static ArrayList setUpdatedItemKeysWithTimeFilter(java.util.Date timefilter,
                                                             int comparison,
                                                             LinkedHashMap localItems,
                                                             LinkedHashMap serverItems)
      throws EntityException {

        ArrayList       updateRows = new ArrayList();
        SyncItemInfo scrcii     = null;
        SyncItemInfo lcrcii     = null;
        String          sGUID      = null;
        long serverItemLastCrc ;
        long localItemLastCrc  ;
        java.util.Date demail = null;

        try {
            Iterator values = serverItems.values().iterator();

            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID  = scrcii.getGuid().getKeyAsString();
               lcrcii = (SyncItemInfo)localItems.get(sGUID);
               if (lcrcii != null){
                  serverItemLastCrc = scrcii.getLastCrc () ;
                  localItemLastCrc  = lcrcii.getLastCrc () ;
                  if (serverItemLastCrc != localItemLastCrc) {
                     if (comparison == ComparisonTerm.GE){
                        demail = scrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Afterd2(demail, timefilter)){
                                updateRows.add(scrcii.getGuid());
                            }
                        }
                     } else {
                        demail = scrcii.getHeaderDate();
                        if (demail != null){
                            if (Utility.d1Befored2(demail, timefilter)){
                                 updateRows.add(scrcii.getGuid());
                            }
                        }
                     }
                  }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Updated Item List " + e.getMessage());
        }


        return updateRows;

    }

    /**
     * Creates updated items id array
     *
     * @param idfilter String
     * @param localItems map with all local SyncItemInfo
     * @param serverItems map with all server SyncItemInfo
     * @return updated items id array
     * @throws EntityException
     */
    public static ArrayList setUpdatedItemKeysWithIdFilter(String idfilter,
                                                           Map localItems,
                                                           Map serverItems)
      throws EntityException {

        ArrayList       updateRows = new ArrayList();
        SyncItemInfo scrcii     = null;
        SyncItemInfo lcrcii     = null;
        String          sGUID      = null;
        long serverItemLastCrc ;
        long localItemLastCrc  ;

        try {
            Iterator values = serverItems.values().iterator();

            while (values.hasNext()) {
               scrcii = (SyncItemInfo)values.next();
               sGUID  = scrcii.getGuid().getKeyAsString();
               lcrcii = (SyncItemInfo)localItems.get(sGUID);
               if (lcrcii != null){
                  serverItemLastCrc = scrcii.getLastCrc () ;
                  localItemLastCrc  = lcrcii.getLastCrc () ;
                  if (serverItemLastCrc != localItemLastCrc) {
                      if (idfilter.equals(sGUID)){
                         updateRows.add(scrcii.getGuid());
                      }
                  }
               }
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Updated Item List " + e.getMessage());
        }


        return updateRows;

    }
    
    /**
     * Filters a list of server items given a filter.
     * 
     * @param emailFilter filter object
     * @param serverItems list of server items tobe filtered
     * @return list of <code>SyncItemKey</code> that matches filter
     * @throws com.funambol.email.exception.EntityException
     */
    public static ArrayList<SyncItemKey> setFilteredItemsKeys(
            EmailFilter emailFilter,
            Map<String, SyncItemInfo> serverItems)
            throws EntityException {

        ArrayList filteredList = new ArrayList();

        try {        
            Iterator items = serverItems.values().iterator();

            while (items.hasNext()){
                
                SyncItemInfo serverItem = (SyncItemInfo) items.next();
                SyncItemKey serverItemKey = serverItem.getGuid();
                
                if (!Utility.booleanFromString(serverItem.getIsEmail())){
                    filteredList.add(serverItemKey);
                    continue;
                }
                
                boolean filterPassed = true;
                
                // time filtering
                filterPassed = isItemInTimeFilter(serverItem, emailFilter);
                                

                if (filterPassed){
                    filteredList.add(serverItemKey);
                }                
            }
        } catch (Exception e) {
            throw new EntityException("Error setting filtered item list " + e.getMessage());
        }
        return filteredList;
    }
    

    /**
     *
     * in the updated list there is just the id in the filter
     *
     * @param idfilter String
     * @param serverItems map with all the CrcSyncItemInfo
     * @return ArrayList
     * @throws EntityException
     */
    public static ArrayList setUpdatedIDFilter(String idfilter,
                                               Map serverItems)
      throws EntityException {

        ArrayList       updateRows = new ArrayList();
        SyncItemInfo scrcii     = null ;
        String          key        = null;

        try {
            scrcii = (SyncItemInfo)serverItems.get(idfilter);
            if (scrcii != null){
                updateRows.add(scrcii.getGuid());
            }
        } catch (Exception e){
            throw new EntityException ("Error setting Updated Item List " + e.getMessage());
        }

        return updateRows;

    }

    //---------------------------------------------------------- Private Methods

    /**
     * Checks if item is included in array of items
     *
     * @param items items array
     * @param item item to find
     * @return <p>true</p> if the item is included
     *         <p>false</p> if the item is not included
     **/
    private static boolean isItemInArray(SyncItemInfo[] items,
                                         SyncItemInfo   item) {

        return isIdInArray(items, item.getGuid().getKeyAsString());

    }

    /**
     * Checks if item id is included in ids from array of items
     *
     * @param items items array
     * @param id id to find
     * @return <p>true</p> if the item is included
     *         <p>false</p> if the item is not included
     **/
    private static boolean isIdInArray(SyncItemInfo[] items, String id) {
        boolean isFind = false;
        int l = items.length;
        for (int i = 0; i < l; i++) {
            if (items[i].getGuid().equals(id)) {
                isFind = true;
                break;
            }
        }
        return isFind;
    }

    /**
     * Does <code>serverItem</code> match the time filter clause ?
     * 
     * @param serverItem item to be matched
     * @param filter filter object which contains the time filter clause
     * @return <code>true</code> if filtering by time is not needed or <code>serverItem</code>
     * matches time filter clause, <code>false</code> otherwise.
     */
    public static boolean isItemInTimeFilter(SyncItemInfo serverItem, EmailFilter filter){
        
        if (filter.getTime() != null) {
            
            int comparison  = Utility.getComparisonTerm(filter.getTimeClause());
            Date headerDate = serverItem.getHeaderDate();
            Date timefilter = filter.getTime();
            
            if (headerDate != null) {
                if (comparison == ComparisonTerm.GE) {
                    if (Utility.d1Afterd2(headerDate, timefilter)) {
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    if (Utility.d1Befored2(headerDate, timefilter)) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }
    

}

