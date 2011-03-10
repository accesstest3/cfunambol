/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.mocks;

import com.funambol.email.model.SyncItemInfoInbox;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author $Id: InboxTable.java,v 1.3 2008-04-08 08:38:09 gbmiglia Exp $
 */
public class InboxTable {

    // ------------------------------------------------------------ Private data
    private MailboxMSDAOCommonMock dao;
    private Session session;
    
    // -------------------------------------------------------------- Properties
    private List<SyncItemInfoInbox> serverInfosTobeUpdated = new ArrayList<SyncItemInfoInbox>();
    private List<SyncItemInfoInbox> serverInfosTobeAdded = new ArrayList<SyncItemInfoInbox>();

    // ---------------------------------------------------- Properties accessors
    public List<SyncItemInfoInbox> getServerInfosTobeAdded() {
        return serverInfosTobeAdded;
    }

    public List<SyncItemInfoInbox> getServerInfosTobeUpdated() {
        return serverInfosTobeUpdated;
    }

    // ------------------------------------------------------------ Constructors
    public InboxTable(MailboxMSDAOCommonMock dao) {
        session = Session.getDefaultInstance(new Properties());        
        this.dao = dao;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * 
     * @param index
     * @param addressString
     * @throws java.lang.Exception
     */
    public void addUpdatedItem(int index, String addressString) throws Exception {
        addRow(index, addressString);
    }
    
    /**
     * 
     * @param index
     * @param addressString
     * @throws java.lang.Exception
     */
    public void addNewItem(int index, String addressString) throws Exception {
        addRow(index, addressString);
    }
    
    
    /**
     * uid(String),index(Integer)
     * 
     * @return
     */
    public LinkedHashMap<String, Integer> getUidsToBeAdded(){
        
        LinkedHashMap<String, Integer> uidsToBeAdded = 
                new LinkedHashMap<String, Integer>();
        
        for (int i = 0; i < serverInfosTobeAdded.size(); i++) {
            uidsToBeAdded.put(Integer.toString(i), i);
        }

        return uidsToBeAdded;
    }

    // --------------------------------------------------------- Private methods
    
    /**
     * 
     * @param index
     * @param addressString
     * @param isInMyContacts
     * @throws java.lang.Exception
     */
    private void addRow(int index, String addressString)
     throws Exception {
        
        SyncItemInfoInbox item = createItem(index, addressString);
        serverInfosTobeUpdated.add(index, item);
    }
    
    /**
     * 
     * @param index
     * @param addressString
     * @return
     * @throws java.lang.Exception
     */
    private SyncItemInfoInbox createItem (int index, String addressString) 
            throws Exception{
        // messages
        Message message = new MimeMessage(session);
        InternetAddress internetAddress = new InternetAddress(addressString);

        message.addFrom(new Address[]{internetAddress});
        dao.addMessage(index, message);

        // items corresponding to the messages
        SyncItemInfoInbox item = new SyncItemInfoInbox(index, null, null, -1);
        
        return item;
    }
}












