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
package com.funambol.framework.core;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents the filtering capabilities
 *
 * @version $Id: FilterCap.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class FilterCap implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private CTInfo    ctInfo                          ;
    private ArrayList       filterKeywords = new ArrayList();
    private ArrayList       propNames      = new ArrayList();

    // ------------------------------------------------------------ Constructors
    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public FilterCap() {}

    /**
     * Creates a new FilterCap object with the given input information
     *
     * @param ctInfo The type and version of a supported content type - NOT NULL
     * @param filterKeyword The record level filter keyword
     * @param propName      The name of a supported property
     */
    public FilterCap(final CTInfo   ctInfo        ,
                     final String[]        filterKeywords,
                     final String[]        propNames     ) {
        setCTInfo(ctInfo);
        setFilterKeywords(filterKeywords);
        setPropNames(propNames);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Get a CTInfo object
     *
     * @return a CTInfo object
     */
    public CTInfo getCTInfo() {
        return this.ctInfo;
    }

    /**
     * Sets a CTInfo object
     *
     * @param ctInfo The CTInfo object
     */
    public void setCTInfo(CTInfo ctInfo) {
        if (ctInfo == null) {
            throw new IllegalArgumentException("ctInfo cannot be null");
        }
        this.ctInfo = ctInfo;
    }

    /**
     * Get the record level filter keyword
     *
     * @return the record level filter keyword
     */
    public ArrayList getFilterKeywords() {
        return this.filterKeywords;
    }

    /**
     * Sets the record level filter keyword
     *
     * @param filterKeywords The record level filter keyword
     */
    public void setFilterKeywords(String[] filterKeywords) {
        if (filterKeywords != null) {
            this.filterKeywords.clear();
            this.filterKeywords.addAll(Arrays.asList(filterKeywords));
        } else {
            this.filterKeywords = null;
        }
    }

    /**
     * Sets the record level filter keyword
     *
     * @param filterKeywords The record level filter keyword
     */
    public void setFilterKeywords(ArrayList filterKeywords) {
        if (filterKeywords != null) {
            this.filterKeywords.clear();
            this.filterKeywords.addAll(filterKeywords);
        } else {
            this.filterKeywords = null;
        }
    }

    /**
     * Get the name of a supported property
     *
     * @return the name of a supported property
     */
    public ArrayList getPropNames() {
        return this.propNames;
    }

    /**
     * Sets the name of a supported property
     *
     * @param propNames The name of a supported property
     */
    public void setPropNames(String[] propNames) {
        if (propNames != null) {
            this.propNames.clear();
            this.propNames.addAll(Arrays.asList(propNames));
        } else {
            this.propNames = null;
        }
    }

    /**
     * Sets the name of a supported property
     *
     * @param propNames The name of a supported property
     */
    public void setPropNames(ArrayList propNames) {
        if (propNames != null) {
            this.propNames.clear();
            this.propNames.addAll(propNames);
        } else {
            this.propNames = null;
        }
    }
}
