/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.foundation.engine.source;

import java.io.File;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.*;

import com.funambol.framework.engine.*;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.filter.*;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.SourceUtils;

/**
 *
 * @version $Id: FilterableSIFSyncSource.java,v 1.1.1.1 2008-03-20 21:38:33 stefano_fornari Exp $
 */
public class FilterableSIFSyncSource extends SIFSyncSource
    implements FilterableSyncSource, MergeableSyncSource, Serializable {


    // --------------------------------------------------------------- Constants
    public static final String DATABASE_HEADER = "FilterableSIFSyncSource file database";

    // -------------------------------------------------------------- Properties

    // ------------------------------------------------------------ Constructors
    public FilterableSIFSyncSource() {
        this(null, null);
    }

    public FilterableSIFSyncSource(String name, String sourceDirectory) {
        super(name, sourceDirectory);
    }

    /**
     * The name defaults to the directory name
     */
    public FilterableSIFSyncSource(String sourceDirectory) {
        this(new File(sourceDirectory).getName(), sourceDirectory);
    }

    // ---------------------------------------------------------- Public Methods


    /**
     * @see SyncSource
     */
    public SyncItemKey[] getAllSyncItemKeys()
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, null);
        }

        List items = checkFilter(allItemsKeys, false);

        return orderSyncItemKeys(getKeys(items));
    }


    /*
    * @see SyncSource
    */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp sinceTs,
                                            Timestamp untilTs)
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, sinceTs);
        }

        List items = checkFilter(newItemsKeys, false);

        return orderSyncItemKeys(getKeys(items));
    }

    /*
    * @see SyncSource
    */
   public SyncItemKey[] getDeletedSyncItemKeys(Timestamp sinceTs,
                                               Timestamp untilTs)
   throws SyncSourceException {

       if (!filesScanned) {
           scanFiles(principal, sinceTs);
       }

       List items = checkFilter(deletedItemsKeys, true);

       return orderSyncItemKeys(getKeys(items));

   }


    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp sinceTs,
                                                Timestamp untilTs)
    throws SyncSourceException {

        if (!filesScanned) {
            scanFiles(principal, sinceTs);
        }

        List items = checkFilter(updatedItemsKeys, false);

        return orderSyncItemKeys(getKeys(items));
    }

    /*
     * @see FilterableSyncSource
     */
    public boolean isSyncItemInFilterClause(SyncItem item) throws SyncSourceException {

        return checkSyncItem(item, syncContext.getFilterClause(), false);
    }

    /*
     * @see FilterableSyncSource
     */
    public boolean isSyncItemInFilterClause(SyncItemKey key) throws SyncSourceException {

        SyncItem item = getSyncItemFromId(key);

        return isSyncItemInFilterClause(item);
    }


    /*
    * @see FilterableSyncSource
    */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {
        return getItemState(syncItemKey.getKeyAsString());
    }

    // ------------------------------------------------------- Protected methods
    /**
     * Checks if the item with the given id and the given xml satisfies the given
     * filter clause. The id is required to check the IDClause.
     * @param id String
     * @param xml String
     * @param clause FilterClause
     * @return boolean
     * @throws Exception
     */
    protected boolean checkItem(String id,
                                String xml,
                                FilterClause clause) throws Exception {

        if (clause == null) {
            return true;
        }

        Map properties = SourceUtils.xmlToHashMap(xml);

        return checkItem(id, properties, clause);
    }

    /**
     * Checks if the item with the given id and the given properties
     * satisfies the given filter clause. The id is required to check the IDClause.
     * If justIDClause is true, only the IDClauses are checked. This is used
     * when a deleted item is checked.
     * @param id String
     * @param properties Map
     * @param clause FilterClause
     * @param justIDClause if true only the IDClauses are checked
     * @return boolean
     */
    protected boolean checkItem(String id,
                                Map properties,
                                FilterClause clause,
                                boolean justIDClause) {
        if (clause == null) {
            return true;
        }
        boolean ok = true;

        Clause recordClause = clause.getClause().getOperands()[1];
        //
        // Check the record clause
        //
        ok = checkRecordClause(id, properties, recordClause, justIDClause);
        //
        // The fields clauses are not checked
        //
        return ok;
    }


    // --------------------------------------------------------- Private methods
    /**
     * Return a map from the content item
     * @param item SyncItem
     * @return Map
     */
    private Map getMapFromItem(SyncItem item) throws Exception  {
        byte[] itemContentA = item.getContent();

        if (itemContentA == null) {
            itemContentA = new byte[0];
        }

        HashMap map = null;

        if (encode && itemContentA.length > 0) {
            itemContentA = Base64.decode(itemContentA);
        }

        if (itemContentA.length > 0) {
            map = SourceUtils.xmlToHashMap(new String(itemContentA));
        } else {
            map = new HashMap();
        }

        return map;
    }

    /**
     * Checks if the item with the given id and the given properties
     * satisfies the given filter clause. The id is required to check the IDClause.
     * @param id String
     * @param properties Map
     * @param clause FilterClause
     * @return boolean
     */
    private boolean checkItem(String id,
                              Map properties,
                              FilterClause clause) {

        return checkItem(id, properties, clause, false);
    }



    /**
     * Checks if the item with the given id and the given properties
     * satisfies the given clause. The id is required to check the IDClause.
     * @param id String
     * @param properties Map
     * @param clause Clause
     * @param justIDClause if true only the IDClauses are checked
     * @return boolean
     */
    private boolean checkRecordClause(String id,
                                      Map properties,
                                      Clause recordClause,
                                      boolean justIdClause) {

        if (recordClause == null || recordClause instanceof AllClause) {
            return true;
        } else if (recordClause instanceof LogicalClause) {
            return checkLogicalClause(id,
                                      properties,
                                      (LogicalClause)recordClause,
                                      justIdClause);
        } else if (recordClause instanceof IDClause) {
            return checkIDClause(id, (IDClause)recordClause);
        } else if (recordClause instanceof WhereClause  && !justIdClause) {
            return checkWhereClause(properties, (WhereClause)recordClause);
        } else {
            if (!justIdClause) {
                throw new IllegalArgumentException("Unknown clause: " + recordClause);
            } else {
                //
                // We have to check only the IDClause but there aren't IDClause
                //
                return true;
            }
        }

    }

    /**
     * Checks if the item with the given id and the given properties
     * satisfies the given logical clause. The id is required to check the IDClause.
     * @param id String
     * @param properties Map
     * @param clause LogicalClause
     * @param justIDClause if true only the IDCLauses are checked
     * @return boolean
     */
    private boolean checkLogicalClause(String id,
                                       Map properties,
                                       LogicalClause clause,
                                       boolean justIDClause) {
        String operator = clause.getOperator();

        Clause[] operands    = clause.getOperands();
        boolean totalResults = false;
        boolean tmp          = false;
        for (int i=0; i<operands.length; i++) {
            if (operands[i] instanceof IDClause) {
                tmp = checkIDClause(id, (IDClause)operands[i]);
            } else if (operands[i] instanceof WhereClause && !justIDClause) {
                tmp = checkWhereClause(properties, (WhereClause)operands[i]);
            } else if (operands[i] instanceof LogicalClause) {
                tmp = checkLogicalClause(id,
                                         properties,
                                         (LogicalClause)operands[i],
                                         justIDClause);
            } else {
                tmp = true;
            }

            if (operator.equals(LogicalClause.OPT_AND)) {
                totalResults = totalResults && tmp;
            } else if (operator.equals(LogicalClause.OPT_OR)) {
                totalResults = totalResults || tmp;
            } else if (operator.equals(LogicalClause.OPT_NOT)) {
                totalResults = !tmp;
            }
        }
        return totalResults;
    }

    /**
     * Checks if the item with the given id satisfies the given IDClause.
     * @param id String
     * @param clause IDClause
     * @return boolean
     */
    private boolean checkIDClause(String id,
                                  IDClause clause) {
        if (id == null) {
            return false;
        }
        String[] values = clause.getValues();
        if (values == null || values.length == 0) {
            //
            // We haven't values to check.
            //
            return true;
        }
        return id.equals(values[0]);
    }

    /**
     * Checks if the item with the given properties satisfies the given where clause.
     * @param properties Map
     * @param clause WhereClause
     * @return boolean
     */
    private boolean checkWhereClause(Map properties, WhereClause clause) {

        if (clause == null) {
            return true;
        }

        boolean caseSensitive = clause.isCaseSensitive();
        String property       = clause.getProperty();
        String valueReq       = clause.getValues()[0];

        String operator       = clause.getOperator();

        String propValue      = null;
        if (properties != null) {
            propValue = (String)properties.get(property);
        }
        if (propValue == null) {
            return false;
        }

        if (!caseSensitive) {
            valueReq  = valueReq.toUpperCase();
            propValue = propValue.toUpperCase();
        }

        if (WhereClause.OPT_CONTAINS.equalsIgnoreCase(operator)) {

            if (propValue.indexOf(valueReq) != -1) {
                return true;
            }
            return false;

        } else if (WhereClause.OPT_END_WITH.equalsIgnoreCase(operator)) {

            return propValue.endsWith(valueReq);

        } else if (WhereClause.OPT_EQ.equalsIgnoreCase(operator)) {

            return propValue.equals(valueReq);

        } else if (WhereClause.OPT_GE.equalsIgnoreCase(operator)) {

            int compare = propValue.compareTo(valueReq);

            if (compare >= 0) {
                return true;
            }
            return false;

        } else if (WhereClause.OPT_GT.equalsIgnoreCase(operator)) {

            int compare = propValue.compareTo(valueReq);

            if (compare > 0) {
                return true;
            }
            return false;

        } else if (WhereClause.OPT_LE.equalsIgnoreCase(operator)) {

            int compare = propValue.compareTo(valueReq);

            if (compare <= 0) {
                return true;
            }
            return false;

        } else if (WhereClause.OPT_LT.equalsIgnoreCase(operator)) {

            int compare = propValue.compareTo(valueReq);

            if (compare < 0) {
                return true;
            }
            return false;

        } else if (WhereClause.OPT_START_WITH.equalsIgnoreCase(operator)) {

            return propValue.startsWith(valueReq);

        }

        throw new IllegalArgumentException("Unsupported operator '" + operator +
                                           "' in the WhereClause");
    }

    /*
     * @see FilterableSyncSource
     */
    protected boolean checkSyncItem(SyncItem item,
                                    FilterClause clause,
                                    boolean justIdClause) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Check if " + item + " is in the filter criteria");
        }

        Map itemProperties = null;

        try {
            itemProperties = getMapFromItem(item);
        } catch (Exception ex) {
            throw new SyncSourceException("Error converting the SyncItem into a map of properties",
                                          ex);
        }

        boolean ok = true;

        try {
            ok = checkItem(item.getKey().getKeyAsString(),
                           itemProperties,
                           clause,
                           justIdClause);
        } catch (Exception ex) {
            throw new SyncSourceException("Error checking the SyncItem '" +
                                          item.getKey().getKeyAsString() + "'",
                                          ex);
        }

        if (log.isTraceEnabled()) {
            log.trace("Item " + item + " is " + (ok ? "" : "not ")+  "in the filter criteria");
        }

        return ok;
    }

    /**
     * Returns a list with just the items that satisfy the filterClause used in
     * the sync
     * @param itemsKeys the keys of the items to check
     * @return List the items in the filter criteria
     */
    protected List checkFilter(List itemsKeys, boolean justIdClause) throws SyncSourceException {
        if (itemsKeys == null) {
            return new ArrayList();
        }
        if (itemsKeys.size() == 0) {
            return new ArrayList();
        }
        List     itemsToReturn = new ArrayList();
        Iterator itItemsKeys   = itemsKeys.iterator();
        SyncItem item          = null;
        SyncItemKey key        = null;

        while (itItemsKeys.hasNext()) {
            key = (SyncItemKey)itItemsKeys.next();
            if (justIdClause) {
                item = new SyncItemImpl(this, key.getKeyValue());
            } else {
                item = getSyncItemFromId(key);
            }
            if (checkSyncItem(item, syncContext.getFilterClause(), justIdClause)) {
                itemsToReturn.add(item);
            }
        }
        return itemsToReturn;
    }

}
