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
 * @version $Id: SyncItemInfo.java,v 1.3 2008-06-03 09:36:25 testa Exp $
 */
public class SyncItemInfo {

    // -------------------------------------------------------------- Properties

    /**    */
    protected SyncItemKey guid;

    /**    */
    protected long lastCrc;

    /**    */
    protected String messageID;

    /**    */
    protected java.util.Date headerDate;

    /**    */
    protected java.util.Date headerReceived;

    /**     */
    protected String subject;

    /**     */
    protected String sender;

    /**
     * define if the email has a problem in the parsing steps
     * in the next sync session this mail will be skipped
     */
    protected String invalid;

    /**
     * define if the email is handled by the sync process.
     * Used by the InboxListener in order to notify to the Server
     * if there is a new email in the inbox
     */
    protected String internal;

    /**
     * define if the item is an email or a folder.
     */
    protected String isEmail;

    /**
     * status of the item : N, D, U
     */
    protected String status;
    
    /**
     * mail token used for attachent retrieving
     */
    protected String token;
    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public SyncItemInfo() {
    }

    /**
     *
     * @param _guid
     * @param _lastCrc
     * @param _messageID
     * @param _headerDate
     * @param _received
     * @param _subject
     * @param _sender
     * @param _invalid
     * @param _internal
     * @param _isEmail
     * @param _status
     */
    public SyncItemInfo(SyncItemKey _guid,
                        long _lastCrc,
                        String _messageID,
                        java.util.Date _headerDate,
                        java.util.Date _received,
                        String _subject,
                        String _sender,
                        String _invalid,
                        String _internal,
                        String _isEmail,
                        String _status) {
        this.guid       = _guid;
        this.lastCrc    = _lastCrc;
        this.messageID  = _messageID;
        this.headerDate = _headerDate;
        this.headerReceived = _received;
        this.subject    = _subject;
        this.sender     = _sender;
        this.invalid    = _invalid;
        this.internal   = _internal;
        this.isEmail    = _isEmail;
        this.status     = _status;
    }


    // ---------------------------------------------------------- Public Methods

    /**
     *
     */
    public SyncItemKey getGuid() {
        return guid;
    }

    /**
     *
     *
     * @param _guid
     */
    public void setGuid(SyncItemKey _guid) {
        this.guid = _guid;
    }

    /**
     *
     * @return lastCrc long
     */
    public long getLastCrc() {
        return lastCrc;
    }

    /**
     *
     *
     * @param _lastCrc
     */
    public void setLastCrc(long _lastCrc) {
        this.lastCrc = _lastCrc;
    }

    /**
     *
     * @return headerDate java.util.Date
     */
    public java.util.Date getHeaderDate() {
        return headerDate;
    }

    /**
     *
     *
     * @param _headerDate
     */
    public void setHeaderDate(java.util.Date _headerDate) {
        this.headerDate = _headerDate;
    }

    /**
     *
     * @return headerDate java.util.Date
     */
    public java.util.Date getHeaderReceived() {
        return headerReceived;
    }

    /**
     *
     *
     * @param _headerReceived
     */
    public void setHeaderReceived(java.util.Date _headerReceived) {
        this.headerReceived = _headerReceived;
    }

    /**
     *
     * @return messageID String
     */
    public String getMessageID() {
        return this.messageID;
    }

    /**
     *
     * @param _messageID String
     */
    public void setMessageID(String _messageID) {
        this.messageID = _messageID;
    }

    /**
     *
     * @return subject String
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     *
     * @param _subject String
     */
    public void setSubject(String _subject) {
        this.subject = _subject;
    }

    /**
     *
     * @return sender String
     */
    public String getSender() {
        return this.sender;
    }

    /**
     *
     * @param _sender String
     */
    public void setSender(String _sender) {
        this.sender = _sender;
    }

    /**
     *
     * @return invalid String
     */
    public String getInvalid() {
        return this.invalid;
    }

    /**
     *
     * @param _invalid String
     */
    public void setInvalid(String _invalid) {
        this.invalid = _invalid;
    }

    /**
     *
     * @return internal String
     */
    public String getInternal() {
        return this.internal;
    }

    /**
     *
     * @param _internal String
     */
    public void setInternal(String _internal) {
        this.internal = _internal;
    }

    /**
     *
     * @return isEmail String
     */
    public String getIsEmail() {
        return this.isEmail;
    }

    /**
     *
     * @param _isEmail String
     */
    public void setIsEmail(String _isEmail) {
        this.isEmail = _isEmail;
    }


    /**
     *
     * @return status String
     */
    public String getStatus() {
        return this.status;
    }

    /**
     *
     * @param _status String
     */
    public void setStatus(String _status) {
        this.status = _status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    /**
     *
     */
    public String toString() {
        String out =
               "GUID [" + this.guid + "]" +
               " CRC [" + this.lastCrc  + "]" +
               " Message-ID [" + this.messageID  + "]" +
               " Date [" + this.headerDate  + "]" +
               " Received [" + this.headerReceived  + "]" +
               " Sender [" + this.sender + "]" +
               " Subject [" + this.subject + "]" +
               " Status [" + this.status + "]" +
               " Invalid [" + this.invalid + "]" +
               " Token [" + this.token + "]" +
               " IsMail [" + this.isEmail + "]" + "]\n" ;
        return out ;
    }


}
