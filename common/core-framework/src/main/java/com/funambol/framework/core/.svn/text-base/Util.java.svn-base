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
package com.funambol.framework.core;

/**
 * This class contains methods for serialize and deserialize
 *
 * @version $Id: Util.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class Util {
    // ------------------------------------------------------------ Constructors

    private Util() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the size of the given Item, null if no size is specified
     *
     * @param item the item to check
     * @return the size of the given Item, null if no size is specified
     */
    public static Long getItemSize(Item item) {
        if (item.getMeta() == null) {
            return null;
        }
        return item.getMeta().getSize();
    }

    /**
     * Returns the size of the contained data into command, null if no size
     * is specified
     *
     * @param cmd the cmd to check
     *
     * @return the size of the contained data, null if no size is specified
     */
    public static Long getCmdDataSize(AbstractCommand cmd) {
        if (cmd.getMeta() == null) {
            return null;
        }
        return cmd.getMeta().getSize();
    }


    /**
     * Returns the real dimension of the data in the given Item
     *
     * @param item the item to check
     * @return the real dimension of the data in the given Item,
     *         null if the item not contains data
     */
    public static Long getRealItemSize(Item item) {
        Data data = item.getData();
        if (data == null) {
            return null;
        }
        String sData = data.getData();
        if (sData == null) {
            return null;
        }
        return new Long(sData.length());
    }

    // ------------------------------------------------------ Deprecated methods

    //
    // NOTE: the methods below will be removed in the next major version (V7)
    //

    /**
     * Serialize Long value to string.
     *
     * @param value Long value to be serialized
     *
     * @return the representation of value
     *
     * @deprecated Use com.funambol.framework.tools.SyncMLUtil.serializeWrapLong() instead
     */
    public static String serializeWrapLong(Long value) {
        return com.funambol.framework.tools.SyncMLUtil.serializeWrapLong(value);
    }

    /**
     * Deserialize Long from string
     *
     * @param value string to be parsed
     *
     * @return the representation of value
     *
     * @deprecated Use com.funambol.framework.tools.SyncMLUtil.deserializeWrapLong() instead
     */
    public static Long deserializeWrapLong(String value) {
        return com.funambol.framework.tools.SyncMLUtil.deserializeWrapLong(value);
    }

    /**
     * Serialize a Boolean value to a string.
     *
     * @param value Long value to be serialized
     *
     * @return the representation of value
     *
     * @deprecated Use com.funambol.framework.tools.SyncMLUtil.serializeBoolean() instead
     */
    public static String serializeBoolean(Boolean value) {
        return com.funambol.framework.tools.SyncMLUtil.serializeBoolean(value);
    }

    /**
     * Deserialize Boolean from string
     *
     * @param value string to be parsed
     *
     * @return the representation of value
     *
     * @deprecated Use com.funambol.framework.tools.SyncMLUtil.deserializeBoolean() instead
     */
    public static Boolean deserializeBoolean(String value) {
        return com.funambol.framework.tools.SyncMLUtil.deserializeBoolean(value);
    }

    /**
     * Use marshall to create the representation XML of the object SyncML
     *
     * @param syncML the object SyncML
     *
     * @return the representation XML of the message
     *
     *  @deprecated Use com.funambol.framework.tools.SyncMLUtil.toXML() instead
     */
    public static String toXML(SyncML syncML) {
        return com.funambol.framework.tools.SyncMLUtil.toXML(syncML);
    }
}
