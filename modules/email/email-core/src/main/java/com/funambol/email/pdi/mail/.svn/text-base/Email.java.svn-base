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
package com.funambol.email.pdi.mail;


import com.funambol.common.pim.common.Property;

/**
 * An object representing a email.
 * This class can be used to store and retrive all
 * information about email.
 *
 * @version $Id: Email.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class Email {

    private Property  uid;       // String  --> will be used String type
    private Property  parentId;  // String  --> will be used String type
    private Property  read;      // boolean --> will be used String type
    private Property  forwarded; // boolean --> will be used String type
    private Property  replied;   // boolean --> will be used String type
    private Property  received;  // date    --> will be used String type
    private Property  created;   // date    --> will be used String type
    private Property  modified;  // date    --> will be used String type
    private Property  deleted;   // boolean --> will be used String type
    private Property  flagged;   // boolean --> will be used String type
    private EmailItem emailItem;
    private Ext       ext;


    /**
     * Creates an empty mail
     */
    public Email() {
        uid       = new Property();
        parentId  = new Property();
        read      = new Property();
        forwarded = new Property();
        replied   = new Property();
        received  = new Property();
        created   = new Property();
        modified  = new Property();
        deleted   = new Property();
        flagged   = new Property();
        emailItem = new EmailItem();
        ext       = new Ext();
    }

    /**
     * Returns the uid of this mail
     *
     * @return the uid of this mail
     */
    public Property getUID() {
        return uid;
    }

    /**
     * set the uid of this mail
     *
     * @param uid Property
     */
    public void setUID(Property uid) {
        this.uid = uid;
    }

    /**
     * Returns the parentId of this mail (folder id)
     *
     * @return the parentId of this mail (folder id)
     */
    public Property getParentId() {
        return parentId;
    }

    /**
     * set the parentId of this mail (folder id)
     *
     * @param parentId Property
     */
    public void setParentId(Property parentId) {
        this.parentId = parentId;
    }

    /**
     * Returns the read of this mail
     *
     * @return the read of this mail
     */
    public Property getRead() {
        return read;
    }

    /**
     * Returns the forwarded of this mail
     *
     * @return the forwarded of this mail
     */
    public Property getForwarded() {
        return forwarded;
    }

    /**
     * Returns the replied of this mail
     *
     * @return the replied of this mail
     */
    public Property getReplied() {
        return replied;
    }

    /**
     * Returns the received of this mail
     *
     * @return the received of this mail
     */
    public Property getReceived() {
        return received;
    }

    /**
     * Returns the created of this mail
     *
     * @return the created of this mail
     */
    public Property getCreated() {
        return created;
    }

    /**
     * Returns the modified of this mail
     *
     * @return the modified of this mail
     */
    public Property getModified() {
        return modified;
    }

    /**
     * Returns the deleted of this mail
     *
     * @return the deleted of this mail
     */
    public Property getDeleted() {
        return deleted;
    }

    /**
     * Returns the flagged of this mail
     *
     * @return the flagged of this mail
     */
    public Property getFlagged() {
        return flagged;
    }

    /**
     * Returns the mailItem of this mail
     *
     * @return the mailItem of this mail
     */
    public EmailItem getEmailItem() {
        return emailItem;
    }

    /**
     * Returns the ext of this mail
     *
     * @return the ext of this mail
     */
    public Ext getExt() {
        return this.ext ;
    }

    /**
     * set the ext of this mail
     */
    public void setExt(Ext _ext) {
        this.ext = _ext;
    }

    /**
     *
     * @return String
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("Email:");
        sb.append("\nUID: ").append(this.getUID().getPropertyValue());
        sb.append("\nRead: ").append(this.getRead().getPropertyValue());
        sb.append("\nForwarded: ").append(this.getForwarded().getPropertyValue());
        sb.append("\nReplied: ").append(this.getReplied().getPropertyValue());
        sb.append("\nReceived: ").append(this.getReceived().getPropertyValue());
        sb.append("\nCreated: ").append(this.getCreated().getPropertyValue());
        sb.append("\nModified: ").append(this.getModified().getPropertyValue());
        sb.append("\nDeleted: ").append(this.getDeleted().getPropertyValue());
        sb.append("\nDeleted: ").append(this.getFlagged().getPropertyValue());
        sb.append("\nEmailItem: ").append(this.emailItem.toString());
        sb.append("\nEncodingType: ").append(this.emailItem.getEncoding());
        sb.append("\nExt: ").append(this.ext.toString());
        return sb.toString();
    }
}
