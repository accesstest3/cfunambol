/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.pushlistener.service.registry.dao;

/**
 *
 * Instances of this class hold data about queries of the push listener
 * framework. Each istance defines query for a given table name in order not
 * to duplicate the amount of memory usage.
 *
 * @version $Id: QueryDescriptor.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class QueryDescriptor {


    // -------------------------------------------------------------Private data
    private String readChangedEntriesQuery;
    private String readEntryQuery;
    private String readActiveEntriesQuery;
    private String deleteEntryQuery;
    private String deleteEntriesQuery;
    private String updateEntryQuery;
    private String updateStatusQuery;
    private String insertEntryQuery;
    private short numberOfExtraColumns = 0;
    private String[] extraProperties;

    /**
     * @return the readChangedEntriesQuery
     */
    public String getReadChangedEntriesQuery() {
        return readChangedEntriesQuery;
    }

    /**
     * @param readChangedEntriesQuery the readChangedEntriesQuery to set
     */
    public void setReadChangedEntriesQuery(String readChangedEntriesQuery) {
        this.readChangedEntriesQuery = readChangedEntriesQuery;
    }

    /**
     * @return the readEntryQuery
     */
    public String getReadEntryQuery() {
        return readEntryQuery;
    }

    /**
     * @param readEntryQuery the readEntryQuery to set
     */
    public void setReadEntryQuery(String readEntryQuery) {
        this.readEntryQuery = readEntryQuery;
    }

    /**
     * @return the readActiveEntriesQuery
     */
    public String getReadActiveEntriesQuery() {
        return readActiveEntriesQuery;
    }

    /**
     * @param readActiveEntriesQuery the readActiveEntriesQuery to set
     */
    public void setReadActiveEntriesQuery(String readActiveEntriesQuery) {
        this.readActiveEntriesQuery = readActiveEntriesQuery;
    }

    /**
     * @return the deleteEntryQuery
     */
    public String getDeleteEntryQuery() {
        return deleteEntryQuery;
    }

    /**
     * @param deleteEntryQuery the deleteEntryQuery to set
     */
    public void setDeleteEntryQuery(String deleteEntryQuery) {
        this.deleteEntryQuery = deleteEntryQuery;
    }

    /**
     * @return the deleteEntriesQuery
     */
    public String getDeleteEntriesQuery() {
        return deleteEntriesQuery;
    }

    /**
     * @param deleteEntriesQuery the deleteEntriesQuery to set
     */
    public void setDeleteEntriesQuery(String deleteEntriesQuery) {
        this.deleteEntriesQuery = deleteEntriesQuery;
    }

    /**
     * @return the updateEntryQuery
     */
    public String getUpdateEntryQuery() {
        return updateEntryQuery;
    }

    /**
     * @param updateEntryQuery the updateEntryQuery to set
     */
    public void setUpdateEntryQuery(String updateEntryQuery) {
        this.updateEntryQuery = updateEntryQuery;
    }

    /**
     * @return the updateStatusQuery
     */
    public String getUpdateStatusQuery() {
        return updateStatusQuery;
    }

    /**
     * @param updateStatusQuery the updateStatusQuery to set
     */
    public void setUpdateStatusQuery(String updateStatusQuery) {
        this.updateStatusQuery = updateStatusQuery;
    }

    /**
     * @return the insertEntryQuery
     */
    public String getInsertEntryQuery() {
        return insertEntryQuery;
    }

    /**
     * @param insertEntryQuery the insertEntryQuery to set
     */
    public void setInsertEntryQuery(String insertEntryQuery) {
        this.insertEntryQuery = insertEntryQuery;
    }

    /**
     * @return the numberOfExtraColumns
     */
    public short getNumberOfExtraColumns() {
        return numberOfExtraColumns;
    }

    /**
     * @param numberOfExtraColumns the numberOfExtraColumns to set
     */
    public void setNumberOfExtraColumns(short numberOfExtraColumns) {
        this.numberOfExtraColumns = numberOfExtraColumns;
    }

    /**
     * @return the extraProperties
     */
    public String[] getExtraProperties() {
        return extraProperties;
    }

    /**
     * @param extraProperties the extraProperties to set
     */
    public void setExtraProperties(String[] extraProperties) {
        this.extraProperties = extraProperties;
    }




}
