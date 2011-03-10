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

import java.util.Arrays;
import java.util.ArrayList;

/**
 * This is a base class for "command" classes
 *
 *
 *
 * @version $Id: ItemizedCommand.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public abstract class ItemizedCommand
extends AbstractCommand
implements java.io.Serializable {
    // ---------------------------------------------------------- Protected data

    //
    // subclasses must have access the following properties
    //
    protected ArrayList items = new ArrayList();
    protected Meta   meta ;

    // ------------------------------------------------------------ Constructors

    /** For serialization purposes */
    protected ItemizedCommand() {}

    /**
     * Create a new ItemizedCommand object with the given commandIdentifier,
     * meta object and an array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param meta the meta object
     * @param items an array of item - NOT NULL
     *
     */
    public ItemizedCommand(CmdID cmdID, Meta meta, Item[] items) {
        super(cmdID);

        if (cmdID == null) {
            throw new IllegalArgumentException("cmdID cannot be null or empty");
        }

        if (items == null) {
            items = new Item[0];
        }

        this.meta  = meta;
        setItems(items);
    }

    /**
     * Create a new ItemizedCommand object with the given commandIdentifier
     * and an array of item
     *
     * @param cmdID the command identifier - NOT NULL
     * @param items an array of item - NOT NULL
     *
     */
    public ItemizedCommand(final CmdID  cmdID, final Item[] items) {
        this(cmdID, null, items);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the array of items
     *
     * @return the array of items
     */
    public java.util.ArrayList getItems() {
        return this.items;
    }

    /**
     * Sets an array of Item object
     *
     * @param items an array of Item object
     */
    public void setItems(Item[] items) {
        if (items != null) {
            this.items.clear();
            this.items.addAll(Arrays.asList(items));
        } else {
            this.items = null;
        }
    }

    /**
     * Sets a list of Item object
     *
     * @param items a list of Item object
     */
    public void setItems(ArrayList items) {
        if (items != null) {
            this.items.clear();
            this.items.addAll(items);
        } else {
            this.items = null;
        }
    }

    /**
     * Gets the Meta object
     *
     * @return the Meta object
     */
    public Meta getMeta() {
        return meta;
    }

    /**
     * Sets the Meta object
     *
     * @param meta the Meta object
     *
     */
    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    /**
     * Gets the name of the command
     *
     * @return the name of the command
     */
    public abstract String getName();
}
