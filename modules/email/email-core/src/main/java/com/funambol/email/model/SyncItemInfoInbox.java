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
package com.funambol.email.model;

import com.funambol.framework.engine.SyncItemKey;


/**
 *
 * @version $Id: SyncItemInfoInbox.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class SyncItemInfoInbox extends SyncItemInfo {

    // -------------------------------------------------------------- Properties

    /**    */
    protected int index;

    /**    */
    protected String uid;

    /** Flags associated to this item's message. */
    protected FlagProperties flags;

    // ------------------------------------------------------------ Constructors


    /**
     *
     *
     * @param _index
     * @param _uid
     * @param _guid
     * @param _lastCrc
     */
    public SyncItemInfoInbox(int _index,
                             String _uid,
                             SyncItemKey _guid,
                             long _lastCrc) {
        index   = _index;
        uid     = _uid;
        guid    = _guid;
        lastCrc = _lastCrc;
    }

    /**
     *
     *
     * @param _index
     * @param _uid
     * @param _guid
     * @param _lastCrc
     * @param _flags
     */
    public SyncItemInfoInbox(int _index,
            String _uid,
            SyncItemKey _guid,
            long _lastCrc,
            FlagProperties _flags) {

        this(_index, _uid, _guid, _lastCrc);
        this.flags = _flags;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     *
     */
    public int getIndex(){
        return index;
    }

    /**
     *
     */
    public void setIndex(int _index){
        index = _index;
    }


    /**
     *
     */
    public String getUid() {
        return uid;
    }

    /**
     *
     *
     */
    public void setUid(String _uid) {
        this.uid = _uid;
    }


    /**
     * Gets the <code>flags</code> property value.
     * @return the <code>flags</code> property value.
     */
    public FlagProperties getFlagList() {
        return flags;
    }

    /**
     *
     */
    public String toString() {
        String out =
               "index [" + this.index + "]" +
               " uid [" + this.uid + "]" +
               " guid [" + this.guid + "]" +
               " crc [" + this.lastCrc + "]\n" ;
        return out ;
    }


}
