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
package com.funambol.email.inboxlistener.msdao;

import com.funambol.email.mocks.MailboxMSDAOCommonMock;
import com.funambol.email.mocks.InboxTable;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.SyncItemInfoInbox;
import com.funambol.email.util.Def;
import java.util.LinkedHashMap;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author $Id: MailboxMSDAOCommonTest.java,v 1.2 2008-04-07 14:53:14 gbmiglia Exp $
 */
public class MailboxMSDAOCommonTest extends TestCase {
    
    private MailServer ms;
    private MailServerAccount msa;
    private MailboxMSDAOCommonMock dao;
    private List<SyncItemInfoInbox> serverInfosTobeUpdated;
    private List<SyncItemInfoInbox> serverInfosTobeAdded;
    private InboxTable inboxTable;
    private LinkedHashMap<String, Integer> uidsToBeAdded;
        
    public MailboxMSDAOCommonTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        ms                     = new MailServer();
        ms.setProtocol(Def.PROTOCOL_IMAP);
        
        msa                    = new MailServerAccount();
        msa.setMailServer(ms);
        
        dao                    = new MailboxMSDAOCommonMock(msa);
        
        inboxTable             = new InboxTable(dao);
        serverInfosTobeUpdated = inboxTable.getServerInfosTobeUpdated();
        serverInfosTobeAdded   = inboxTable.getServerInfosTobeAdded();

        uidsToBeAdded          = new LinkedHashMap<String, Integer>();        
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    

    /**
     * 
     * @throws java.lang.Exception
     */
    public void testDummy() throws Exception {

    }
}





