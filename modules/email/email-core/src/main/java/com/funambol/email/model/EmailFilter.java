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

import java.util.Date;

/**
 * this class contains all the info about the filter that the client sends
 *
 * @version $Id: EmailFilter.java,v 1.2 2008-04-07 14:53:04 gbmiglia Exp $
 */
public class EmailFilter {

    /**
     * filter type (INCLUSIVE/EXCLUSIVE); default: exclusive
     */
    private String filterType  = Def.FILTER_TYPE_EXC;
    /**
     * max email in cache ; default: 100
     */
    private int    maxInboxNum = Def.MAX_INBOX_EMAIL_NUMBER;
    /**
     * max email in the IMAP folder (sent/trash/drafts) ; default 20
     */
    private int    maxSentNum  = Def.MAX_SENT_EMAIL_NUMBER;
    /**
     * filter on ID in the inclusive filter ; default: no id filter
     */
    private String Id          = null;
    /**
     * filter on time; default: no time filter
     */
    private Date   time        = null;
    /**
     * filter on time; default: great/equals
     */
    private String timeClause  = "GE";
    /**
     * filter on folder; wich folder is active
     * <p>
     * folder filter values
     * <p>
     * 0 0 0 0 1 -->  1  inbox
     * <p>
     * 0 0 0 1 1 -->  3  inbox + outbox
     * <p>
     * 0 0 1 1 1 -->  7  inbox + outbox + sent
     * <p>
     * 0 1 1 1 1 -->  15 inbox + outbox + sent + draft
     * <p>
     * 1 1 1 1 1 -->  31 inbox + outbox + sent + draft + trash
     * <p>
     *
     * default is inbox + outbox
     */
    private int     folder         = Def.FILTER_FOLDER_IO;
    /**
     * shortcut for the sent activation; default is true
     */
    private boolean isOutboxActive = true;
    /**
     * shortcut for the sent activation; default is false
     */
    private boolean isSentActive   = false;
    /**
     * filter on size
     * <p>
     *  0 0 0 0 0 1 = 1   FILTER_SIZE_H         (get only header)
     * <p>
     *  0 0 0 1 0 1 = 5   FILTER_SIZE_H_B       (get header and body)
     * <p>
     *  0 0 1 1 1 1 = 15  FILTER_SIZE_H_BPERC
     * <p>
     *  0 1 0 1 0 1 = 21  FILTER_SIZE_H_B_A     (get all message)
     * <p>
     *  1 1 1 1 1 1 = 63  FILTER_SIZE_H_B_APERC
     * <p>
     * default is get all message
     */
    private int    size        = Def.FILTER_SIZE_H_B_A;
    /**
     * filter on bytes number; // def. no limit
     */
    private int    numBytes    = -1;

    //-------------------------------------------------------------- Constructor

    /**
     *
     */
    public EmailFilter(){
    }


    /**
     *
     * @param _filterType
     * @param _maxInboxNum
     * @param _maxSentNum
     * @param _Id
     * @param _time
     * @param _timeClause
     * @param _folder
     * @param _size
     * @param _numBytes
     */
    public EmailFilter(String _filterType,
                       int _maxInboxNum,
                       int _maxSentNum,
                       String _Id,
                       Date   _time,
                       String _timeClause,
                       int    _folder,
                       boolean _isOutboxActive,
                       boolean _isSentActive,
                       int    _size,
                       int    _numBytes){

         this.filterType     = _filterType;
         this.maxInboxNum    = _maxInboxNum;
         this.maxSentNum     = _maxSentNum;
         this.Id             = _Id;
         this.time           = _time;
         this.timeClause     = _timeClause;
         this.folder         = _folder;
         this.isOutboxActive = _isOutboxActive;
         this.isSentActive   = _isSentActive;
         this.size           = _size;
         this.numBytes       = _numBytes;

    }


    //----------------------------------------------------------- Public Methods

    /**
     *
     * @return the value of the property filterType
     */
    public String getFilterType() {
        return filterType;
    }

    /**
     * @param filterType
     */
    public void setFilterType(String filterType) {
        this.filterType = filterType;
    }

    /**
     *
     * @return the value of the property maxEmailNum
     */
    public int getMaxInboxNum() {
        return this.maxInboxNum;
    }

    /**
     *
     *
     * @param _maxInboxNum
     */
    public void setMaxInboxNum(int _maxInboxNum) {
        this.maxInboxNum = _maxInboxNum;
    }

    /**
     *
     * @return the value of the property maxSentNum
     */
    public int getMaxSentNum() {
        return this.maxSentNum;
    }

    /**
     *
     *
     * @param _maxSentNum
     */
    public void setMaxSentNum(int _maxSentNum) {
        this.maxSentNum = _maxSentNum;
    }

    /**
     *
     * @return the value of the property id
     */
    public String getId() {
        return Id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.Id = id;
    }

    /**
     * @return the value of the property time
     */
    public Date getTime() {
        return time;
    }

    /**
     *
     * @param time
     */
    public void setTime(Date time) {
        this.time = time;
    }

    /**
     *
     * @return the value of the property timeClause
     */
    public String getTimeClause() {
        return timeClause;
    }

    /**
     *
     * @param timeClause
     */
    public void setTimeClause(String timeClause) {
        this.timeClause = timeClause;
    }

    /**
     *
     * @return the value of the property folder
     */
    public int getFolder() {
        return folder;
    }

    /**
     *
     * @param folder
     */
    public void setFolder(int folder) {
        this.folder = folder;
    }

    /**
     *
     * @return the value of the property isOutboxActive
     */
    public boolean getIsOutboxActive() {
        return this.isOutboxActive;
    }

    /**
     *
     * @param _isOutboxActive
     */
    public void setIsOutboxActive(boolean _isOutboxActive) {
        this.isOutboxActive = _isOutboxActive;
    }

    /**
     *
     * @return the value of the property isSentActive
     */
    public boolean getIsSentActive() {
        return this.isSentActive;
    }

    /**
     *
     * @param _isSentActive
     */
    public void setIsSentActive(boolean _isSentActive) {
        this.isSentActive = _isSentActive;
    }

    /**
     *
     * @return the value of the property size
     */
    public int getSize() {
        return size;
    }

    /**
     *
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *
     * @return the value of the property numBytes
     */
    public int getNumBytes() {
        return numBytes;
    }

    /**
     *
     * @param numBytes
     */
    public void setNumBytes(int numBytes) {
        this.numBytes = numBytes;
    }

    
    /**
     * <p>Returns a string representation of the object</p>
     * @return String
     */
    @Override
    public String toString() {
        return "folderFilter             " + this.folder +
               "\nidFilter               " + this.Id +
               "\ntimeFilter             " + this.time +
               "\nsizeFilter             " + this.size +
               "\nnumBytes               " + this.numBytes;
    }
}
