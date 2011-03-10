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
 * @version $Id: UpdatedMessage.java,v 1.1 2008-03-25 11:25:32 gbmiglia Exp $
 */
public class UpdatedMessage {

    /**   id  */
    private long  FMID = 0 ; // mail uid in the IMAP folder

    /** javaMail Message */
    private Message javaMessage   = null ;

    /**
     *
     */
    public UpdatedMessage() {
    }

    /**
     * Returns the id of this Message
     * @return the id of this Message
     */
    public long getFMID() {
        return this.FMID;
    }

    /**
     * Returns the javaMessage of this Message
     * @return the javaMessage of this Message
     */
    public Message getJavaMessage() {
        return this.javaMessage;
    }

    /**
     * @param _mailid mail id in the folder
     */
    public void setFMID(long _mailid) {
        this.FMID = _mailid;
    }

    /**
     * @param _msg message
     */
    public void setJavaMessage(Message _msg) {
        this.javaMessage = _msg;
    }

}
