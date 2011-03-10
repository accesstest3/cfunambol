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

/**
 * This class represents a unique identifier of a <i>SyncItem</i> item in a repository.
 *
 * @version $Id: SyncItemKey.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public class SyncItemKey implements Comparable , java.io.Serializable {

    // -------------------------------------------------------------- Properties

    /**
     * The key value
     */
    private Object keyValue = null;
    public Object getKeyValue(){
        return keyValue;
    }

    /**
     * @param keyValue the key - NOT NULL (assert checked)
     */
    public void setKeyValue(Object keyValue){
        assert (keyValue != null);
        this.keyValue = keyValue;
    }

    // ------------------------------------------------------------ Constructors

    /**
     * @param keyValue the key - NOT NULL (assert checked)
     */
    public SyncItemKey(Object keyValue) {
        assert (keyValue != null);
        this.keyValue = keyValue;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * @return this key's value as a String object (calling toString() on the
     *         value object)
     */
    public String getKeyAsString() {
        return keyValue.toString();
    }

    /**
     * Two keys are equal if their keyValues are equal.
     * The keys are objects but they have to be considered as String object
     * to be compared.
     *
     * @param o the object this instance must be compared to.
     *
     * @return the given object is equal to this object
     */
    public boolean equals(Object o) {
        if (!(o instanceof SyncItemKey)) return false;

        return ((SyncItemKey)o).getKeyAsString().equals(getKeyAsString());
    }

    /**
     * Returns the hash code of this object
     *
     * @return the hash code of this object
     */
    public int hashCode() {
        return getKeyAsString().hashCode();
    }

    /**
     * @return a string representation for debugging purposes of this <i>SyncItemKey</i>.
     */
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append( " { keyValue: "  );
        sb.append( getKeyAsString() );
        sb.append( " } "            );

        return sb.toString();
    }

    /**
     * Compares two SyncItemKey based on the string representation of the key value
     * @param o Object
     * @return int
     */
    public int compareTo(Object o) {

        if (!(o instanceof SyncItemKey)) {
            throw new ClassCastException("A SyncItemKey object expected.");
        }
        SyncItemKey otherKey = (SyncItemKey)o;

        if (keyValue != null && otherKey.getKeyValue() != null) {
            return keyValue.toString().compareTo(otherKey.getKeyValue().toString());
        }
        if (keyValue == null && otherKey.getKeyValue() == null) {
            return 0;
        }

        return -1;
    }
}
