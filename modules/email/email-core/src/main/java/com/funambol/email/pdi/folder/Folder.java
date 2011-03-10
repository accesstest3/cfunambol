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
package com.funambol.email.pdi.folder;


import com.funambol.common.pim.common.Property;

/**
 * An object representing a folder.
 * This class can be used to store and retrive all
 * information about folder.
 *
 * @version $Id: Folder.java,v 1.1 2008-03-25 11:28:21 gbmiglia Exp $
 */
public class Folder {

    private Property  uid;       // String  --> will be used String type
    private Property  parentId;  // String  --> will be used String type
    private Property  name;      // String  --> will be used String type
    private Property  created;   // date    --> will be used String type
    private Property  modified;  // date    --> will be used String type
    private Property  accessed;  // date    --> will be used String type
    private Property  role;      // String  --> will be used String type
    // @todo Ext


    /**
     * Creates an empty Folder
     */
    public Folder() {
        uid       = new Property();
        parentId  = new Property();
        name      = new Property();
        created   = new Property();
        modified  = new Property();
        accessed  = new Property();
        role      = new Property();
    }

    /**
     * Returns the uid of this folder
     *
     * @return the uid of this folder
     */
    public Property getUID() {
        return uid;
    }

    /**
     * set the uid of this folder
     *
     */
    public void setUID(Property uid) {
        this.uid = uid;
    }

    /**
     * Returns the parentId of this folder
     *
     * @return the parentId of this folder
     */
    public Property getParentId() {
        return parentId;
    }

    /**
     * set the parentId of this folder
     *
     */
    public void setParentId(Property parentId) {
        this.parentId = parentId;
    }


    /**
     * Returns the name of this folder
     *
     * @return the name of this folder
     */
    public Property getName() {
        return name;
    }


    /**
     * Returns the created of this folder
     *
     * @return the created of this folder
     */
    public Property getCreated() {
        return created;
    }

    /**
     * Returns the modified of this folder
     *
     * @return the modified of this folder
     */
    public Property getModified() {
        return modified;
    }

    /**
     * Returns the accessed of this folder
     *
     * @return the accessed of this folder
     */
    public Property getAccessed() {
        return accessed;
    }


    /**
     * Returns the role of this folder
     *
     * @return the role of this folder
     */
    public Property getRole() {
        return role;
    }

    /**
     *
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Email:");
        sb.append("\nUID       : " + this.getUID().getPropertyValue());
        sb.append("\nName      : " + this.getName().getPropertyValue());
        sb.append("\nCreated   : " + this.getCreated().getPropertyValue());
        sb.append("\nModified  : " + this.getModified().getPropertyValue());
        sb.append("\nAccessed  : " + this.getAccessed().getPropertyValue());
        sb.append("\nRole      : " + this.getRole().getPropertyValue());
        return sb.toString();
    }
}
