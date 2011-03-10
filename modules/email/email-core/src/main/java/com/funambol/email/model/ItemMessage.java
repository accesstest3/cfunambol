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

import com.funambol.email.pdi.mail.Ext;
import javax.mail.Message;

/**
 *
 * @version $Id: ItemMessage.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class ItemMessage {

    /**   id  */
    private String  GUID = null ; // folder_id + separator + uid

    /**  parent id  */
    private String  parentId = null ; // folder id

    /**   header message-id  */
    private String  message_id = null ;

    /** javaMail Message */
    private Message javaMessage   = null ;

    /**   message  */
    private String  streamMessage = null ;

    /** message's encoding type */
    private String encodingType = null;

    /**  origination date   */
    private String created = null;
    /**  received date   */
    private String received = null;
    /**  modified date   */
    private String modified = null;

    /** read  (boolean)*/
    private String read = null;
    /** forwarded  (boolean)*/
    private String forwarded = null;
    /** replied  (boolean)*/
    private String replied = null;
    /** deleted  (boolean)*/
    private String deleted = null;
    /** flagged  (boolean)*/
    private String flagged = null;


    private Ext ext        = null;

    /**
     * the uid contains:
     * -  FID : folder id
     * -  SEPARATOR : "/" default
     * -  FMID : message id considering folder
     *
     * @param GUID String
     * @param parentId String
     * @param message_id String
     * @param javaMessage Message
     * @param streamMessage String
     * @param created String
     * @param received String
     * @param modified String
     * @param read String
     * @param forwarded String
     * @param replied String
     * @param deleted String
     * @param flagged String
     */
    public ItemMessage(String GUID,
                       String parentId,
                       String message_id,
                       Message javaMessage,
                       String streamMessage,
                       String encodingType,
                       String created,
                       String received,
                       String modified,
                       String read,
                       String forwarded,
                       String replied,
                       String deleted,
                       String flagged,
                       Ext ext) {

        this.GUID          = GUID ;
        this.parentId      = parentId ;
        this.message_id    = message_id ;

        this.javaMessage   = javaMessage;
        this.streamMessage = streamMessage;
        this.encodingType = encodingType;

        this.created       = created;
        this.received      = received;
        this.modified      = modified;

        this.read          = read;
        this.forwarded     = forwarded;
        this.replied       = replied;
        this.deleted       = deleted;
        this.flagged       = flagged;

        this.ext           = ext;
    }

    /**
     * Returns the id of this Message
     * @return the id of this Message
     */
    public String getGUID() {
        return this.GUID;
    }

    /**
     * Returns the id of this Message
     * @return the id of this Message
     */
    public String getParentId() {
        return this.parentId;
    }

    /**
     * Returns the message-id of this Message
     * @return the message-id of this Message
     */
    public String getMessage_id() {
        return this.message_id;
    }

    /**
     * Returns the javaMessage of this Message
     * @return the javaMessage of this Message
     */
    public Message getJavaMessage() {
        return this.javaMessage;
    }

    /**
     * Returns the message of this Message
     * @return the message of this Message
     */
    public String getStreamMessage() {
        return this.streamMessage;
    }

    /**
     * Sets the message of this Message.
     */
    public void setStreamMessage(String _streamMessage) {
        this.streamMessage = _streamMessage;
    }

    /**
     * Returns the encoding type of this Message
     * @return the encoding type of this Message
     */
    public String getEncodingType()
    {
        return encodingType;
    }

    //--------------------------------------------------------------------- DATE

    /**
     * Returns the origination date of this Message
     * @return the origination date of this Message
     */
    public String getCreated() {
        return this.created;
    }

    /**
     * Returns the received date of this Message
     * @return the received date of this Message
     */
    public String getReceived() {
        return this.received;
    }

    /**
     * Returns the modified date of this Message
     * @return the modified date of this Message
     */
    public String getModified() {
        return this.modified;
    }

    //------------------------------------------------------------------ BOOLEAN

    /**
     * Returns if the Message has been read
     * @return true if Message has been read
     */
    public String getRead() {
        return this.read;
    }

    /**
     * Returns if the Message has been forwarded
     * @return true if Message has been forwarded
     */
    public String getForwarded() {
        return this.forwarded;
    }

    /**
     * Returns if the Message has been replied
     * @return true if Message has been replied
     */
    public String getReplied() {
        return this.replied;
    }

    /**
     * Returns if the Message has been deleted
     * @return true if Message has been deleted
     */
    public String getDeleted() {
        return this.deleted;
    }

    /**
     * Returns if the Message has been flagged
     * @return true if Message has been flagged
     */
    public String getFlagged() {
        return this.flagged;
    }

    //---------------------------------------------------------------------- Ext

    /**
     * Returns Ext
     * @return Ext
     */
    public Ext getExt() {
        return this.ext;
    }

}
