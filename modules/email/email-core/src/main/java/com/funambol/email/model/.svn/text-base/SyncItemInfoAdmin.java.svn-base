/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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


/**
 *
 * @version $Id: SyncItemInfoAdmin.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class SyncItemInfoAdmin implements java.io.Serializable {


    // -------------------------------------------------------------- Properties

    /**    */
    private String guid;

    /**    */
    private String messageID;

    /**    */
    private java.util.Date headerDate;

    /**    */
    private java.util.Date headerReceived;

    /**     */
    private String subject;


    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public SyncItemInfoAdmin() {
    }


    // ---------------------------------------------------------- Public Methods

    /**
     *
     * @return String
     */
    public String getGuid() {
        return guid;
    }

    /**
     *
     *
     * @param _guid
     */
    public void setGuid(String _guid) {
        this.guid = _guid;
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
     */
    public String toString() {
        String out =
               "GUID [" + this.guid + "]" +
               " Message-ID [" + this.messageID  + "]" +
               " Date [" + this.headerDate  + "]" +
               " Received [" + this.headerReceived  + "]" +
               " Subject [" + this.subject + "]" ;
        return out ;
    }


}
