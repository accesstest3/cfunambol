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

import com.funambol.email.util.Def;


/**
 *
 * @version $Id: ItemFolder.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class ItemFolder {

    /**   id  */
    private String  GUID = null ; // folder id (FID: stored in the local DB)

    /**  name  */
    private String  name = null ; // folder name

    /**  parent id  */
    private String  parentId = null ; // parent folder id (FID)


    /**  origination date   */
    private String created = null;
    /**  accessed date   */
    private String accessed = null;
    /**  modified date   */
    private String modified = null;

    /** role  */
    private String role = null;
    /** attributes  */
    private String attributes = null;



    /**
     * the uid contains:
     * -  FID : parent folder id
     * -  separator (/)
     * -  UID : folder id
     *
     *
     * @param GUID folder id
     * @param name String
     * @param parentId String
     * @param created String
     * @param accessed String
     * @param modified String
     * @param role String
     * @param attributes String
     */
    public ItemFolder(String GUID,
                      String name,
                      String parentId,
                      String created,
                      String accessed,
                      String modified,
                      String role,
                      String attributes) {

        this.GUID          = GUID ;
        this.name          = name ;
        this.parentId      = parentId ;

        this.created       = created;
        this.accessed      = accessed;
        this.modified      = modified;

        this.role          = role;
        this.attributes    = attributes;
    }

    /**
     * Returns the id of this Folder
     * @return the id of this Folder
     */
    public String getGUID() {
        return this.GUID;
    }

    /**
     * Sets the id of this Folder
     *
     * @param uid String
     */
    public void setGUID(String uid) {
        this.GUID = GUID;
    }


    /**
     * Returns the name of this Folder
     * @return the name of this Folder
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the parent id of this Folder
     * @return the parent id of this Folder
     */
    public String getParentId() {
        return this.parentId;
    }

    /**
     * sets the parent id of this Folder
     *
     * @param parentId String
     */
    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    /**
     * Returns the origination date of this Folder
     * @return the origination date of this Folder
     */
    public String getCreated() {
        return this.created;
    }

    /**
     * Returns the accessed date of this Folder
     * @return the accessed date of this Folder
     */
    public String getAccessed() {
        return this.accessed;
    }

    /**
     * Returns the modified date of this Folder
     * @return the modified date of this Folder
     */
    public String getModified() {
        return this.modified;
    }

    /**
     * Returns the role of this Folder
     * @return the role of this Folder
     */
    public String getRole() {
        return this.role;
    }

    /**
     * Returns the attributes of this Folder
     * @return the attributes of this Folder
     */
    public String getAttributes() {
        return this.attributes;
    }

}
