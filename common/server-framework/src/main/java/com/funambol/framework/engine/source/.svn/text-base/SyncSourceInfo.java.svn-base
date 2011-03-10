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
package com.funambol.framework.engine.source;

import com.funambol.framework.engine.source.ContentType;

/**
 * This class wraps the following information about a <code>SyncSource</code>
 * <table>
 * <tr>
 * <td>supportedTypes</td>
 * <td>Types supported by a source</td>
 * </tr>
 * <tr>
 * <td>preferredType</td>
 * <td>The preferred source content type</td>
 * </tr>
 * <tr>
 * <td>filterInfo</td>
 * <td>The info about the filters supported</td>
 * </tr>
 * </table>
 *
 * @version $Id: SyncSourceInfo.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncSourceInfo implements java.io.Serializable {

    // -------------------------------------------------------------- Properties

    private ContentType[] supportedTypes = null;

    /**
     * Getter for property supportedTypes.
     * @return Value of property supportedTypes.
     */
    public ContentType[] getSupportedTypes() {
        return this.supportedTypes;
    }

    /**
     * Setter for property supportedTypes.
     * @param supportedTypes New value of property supportedTypes.
     */
    public void setSupportedTypes(ContentType[] supportedTypes) {
        this.supportedTypes = supportedTypes;
        this.preferred  = 0;
    }

    /**
     * The preferred content type index
     **/
    private int preferred = 0;

    /**
     * Getter for property preferred.
     * @return Value of property preferred.
     */
    public int getPreferred() {
        return preferred;
    }

    /**
     * Setter for property preferredType.
     * @param preferred New value of property preferredType.
     */
    public void setPreferred(int preferred) {
        this.preferred = preferred;
    }

    /**
     * The info about the filters supported
     */
    private FilterInfo filterInfo = null;

    /**
     * Getter for property filterInfo.
     * @return Value of property filterInfo.
     */
    public FilterInfo getFilterInfo() {
        return this.filterInfo;
    }

    /**
     * Setter for property filterInfo.
     * @param filterInfo New value of property filterInfo.
     */
    public void setFilterInfo(FilterInfo filterInfo) {
        this.filterInfo = filterInfo;
    }

    /**
     * Returns the preferred content type among the supported ones.
     *
     * @return the preferred content type among the supported ones.
     */
    public ContentType getPreferredType() {
        assert ((supportedTypes != null) && (preferred >= 0) && (preferred < supportedTypes.length));
        return supportedTypes[preferred];
    }

    /**
     * Is the hierarchicalSync supported ?
     */
    private boolean supportHierarchicalSync = false;

    /**
     * Getter for property supportHierarchicalSync.
     * @return value of property supportHierarchicalSync.
     */
    public boolean getSupportHierarchicalSync() {
        return supportHierarchicalSync;
    }

    /**
     * Setter for property supportHierarchicalSync.
     * @param supportHierarchicalSync boolean
     */
    public void setSupportHierarchicalSync(boolean supportHierarchicalSync) {
        this.supportHierarchicalSync = supportHierarchicalSync;
    }

    // ------------------------------------------------------------ Constructors
    /**
     * Creates a new instance of SyncSuurceInfo
     *
     * @param supportedTypes - NOT NULL or EMPTY
     * @param preferred the position in the <i>supportedTypes</i> array of the
     *                  preferred type
     *
     * @throws IllegalArgumentException if the passed in parameters are not correct
     */
    public SyncSourceInfo(ContentType[] supportedTypes, int preferred) {
        if ((supportedTypes == null) || (supportedTypes.length == 0)) {
            throw new IllegalArgumentException("supportedTypes cannot be null");
        }

        if ((preferred<0) || (preferred >= supportedTypes.length)) {
            throw new IllegalArgumentException( "preferred is "
                                              + preferred
                                              + " when the supported type are "
                                              + supportedTypes.length);
        }

        this.supportedTypes = supportedTypes;
        this.preferred = preferred;
    }

    public SyncSourceInfo() {
    }
}
