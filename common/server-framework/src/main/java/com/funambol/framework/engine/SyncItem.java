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

import java.sql.Timestamp;

import com.funambol.framework.engine.source.SyncSource;

/**
 * <i>SyncItem</i> is the indivisible entity that can be exchanged in a
 * synchronization process. It is similar to a <i>com.funambol.framework.core.Item</i>,
 * but this one is more generic, not related to any protocol.<br>
 * A <i>SyncItem</i> is uniquely identified by its <i>SyncItemKey</i>
 * A <i>SyncItem</i> is also associated with a state, which can be one of the
 * values defined in <i>SyncItemState</i>.<p>
 * A <i>SyncItem</i> can be <i>mapped</i>, that is assiciated to an item belonging
 * to another source, meaning that the item represents the same entity in either
 * sources.
 * Moreover a <i>SyncItem</i> has this properties:
 * <ui>
 * <li>format</li>
 * <li>type</li>
 * <li>content</li>
 * <li>timestamp</li>
 * </ui>
 *
 * @version $Id: SyncItem.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 *
 */
public interface SyncItem {

    // -------------------------------------------------------------- Properties

    /**
     * Returns the item's key
     * @return the SyncItem's unique identifier
     */
    public SyncItemKey getKey();

    /**
     * Returns the item's parent key
     * @return the SyncItem's parent identifier
     */
    public SyncItemKey getParentKey();

    /**
     * Returns the item's state
     * @return the state of the item. @see SyncItemState
     */
    public char getState();

    /**
     * Sets the item's state
     * @param state the state to set
     */
    public void setState(char state);

    /**
     * Returns the item's syncSource.
     * @return the syncsouce of the item.
     */
    public SyncSource getSyncSource();

    /**
     * Gets the conter
     * @return byte[]
     */
    public byte[] getContent();

    /**
     * Sets the content
     * @param content the content to set
     */
    public void setContent(byte[] content);

    /**
     * Gets the format
     * @return String
     */
    public String getFormat();

    /**
     * Sets the format
     * @param format the format to set
     */
    public void setFormat(String format);

    /**
     * Gets the type of the content
     * @return String
     */
    public String getType();

    /**
     * Sets the type
     * @param type the type to set
     */
    public void setType(String type);

    /**
     * Sets the timestamp
     * @return Timestamp
     */
    public Timestamp getTimestamp();

    /**
     * Sets the timestamp
     * @param timestamp the timestamp to set
     */
    public void setTimestamp(Timestamp timestamp);


    /**
     * Creates a copy of the SyncItem object
     * @return the cloned SyncItem
     */
    public Object cloneItem();

}
