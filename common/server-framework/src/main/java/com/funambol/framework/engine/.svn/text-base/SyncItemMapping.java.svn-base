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

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;

import org.apache.commons.lang.builder.ToStringBuilder;


/**
 * <i>SyncItem</i> is the indivisible entity that can be exchanged in a
 * synchronization process. It is similar to an Item, but it doesn't contain
 * any data, only status and addressing information. The idea is that a
 * <i>SyncItem</i> represents status information about an item. Only if an item
 * must be synchronized it needs also the real data.
 * <p>
 * The <i>SyncItemKey</i> uniquely identifies the item into the server. Client
 * keys must be translated into server keys before create a <i>SyncItem</i>.
 *
 *
 * @version $Id: SyncItemMapping.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 *
 */
public class SyncItemMapping {
    // ------------------------------------------------------------ Private data

    /**
     * The SyncItem's unique identifier
     */
    private SyncItemKey key = null;

    // ------------------------------------------------------------ Constructors

    /**
     * Create an instance of <i>SyncItemMapping</i> from the mapped key.
     *
     * @param key the mapped key; if it is a <i>SyncItemKey</i> already, it is
     *        taken as key, otherwise a new <i>SyncItemKey</i> is created.
     */
    public SyncItemMapping(Object key) {
        if (key instanceof SyncItemKey) {
            this.key = (SyncItemKey)key;
        } else {
            this.key = new SyncItemKey(key);
        }
    }


    // ---------------------------------------------------------- Public methods

    public SyncItemKey getKey() {
        return this.key;
    }

    public void setMapping(SyncItem syncItemA, SyncItem syncItemB) {
        this.syncItemA = syncItemA;
        this.syncItemB = syncItemB;
    }

    /**
     * @return a string representation of this SyncItemMapping for debugging purposes
     */
    public String toString() {
        return new ToStringBuilder(this).
                   append("key"      , key.toString()    ).
                   append("syncItemA", syncItemA         ).
                   append("syncItemB", syncItemB         ).
                   toString();
    }

    /**
     * Two <i>SyncItemMapping</i>s are equal if their keys are equal.
     *
     * @param o the object this instance must be compared to.
     *
     * @return the given object is equal to this object
     *
     */
    public boolean equals(Object o) {
        if (!(o instanceof SyncItemMapping)) return false;

        return ((SyncItemMapping)o).getKey().equals(key);
    }

    /**
     * Returns the hash code of this object
     *
     * @return the hash code of this object
     */
    public int hashCode() {
        return getKey().hashCode();
    }
    // -------------------------------------------------------------- Properties

    /**
     * The A item
     */
    private SyncItem syncItemA = null;

    /** Getter for property syncItemA.
     * @return Value of property syncItemA.
     *
     */
    public SyncItem getSyncItemA() {
        return syncItemA;
    }

    /** Setter for property syncItemA.
     * @param syncItemA New value of property syncItemA.
     *
     */
    public void setSyncItemA(SyncItem syncItemA) {
        this.syncItemA = syncItemA;
    }

    /**
     * The B item
     */
    private SyncItem syncItemB = null;

    /** Getter for property syncItemA.
     * @return Value of property syncItemA.
     *
     */
    public SyncItem getSyncItemB() {
        return syncItemB;
    }

    /** Setter for property syncItemA.
     * @param syncItemB New value of property syncItemB.
     *
     */
    public void setSyncItemB(SyncItem syncItemB) {
        this.syncItemB = syncItemB;
    }
}
