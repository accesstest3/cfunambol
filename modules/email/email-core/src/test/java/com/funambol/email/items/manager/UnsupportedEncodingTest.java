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
package com.funambol.email.items.manager;

import com.funambol.email.items.manager.ContentProviderInfo;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.ItemMessage;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.email.test.EmailConnectorTestCase;

import com.funambol.email.test.ManagerUtility;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.Session;

/**
 * Tets class for testing <code>EntityManagerFilter</code>.
 *
 * $Id: UnsupportedEncodingTest.java,v 1.2 2008-06-03 09:38:12 testa Exp $
 */
public class UnsupportedEncodingTest extends EmailConnectorTestCase {

    // ------------------------------------------------------------ Private data
    
    /** Folder id. */
    private String FID;
    
    /** Mail id within the folder. */
    private String FMID;
    
    /** Item status. */
    private char status;
    
    /** Locale. */
    private Locale loc;

    // ------------------------------------------------------------ Constructors
    
    public UnsupportedEncodingTest(String testName) {
        super(testName);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * 
     * @throws java.lang.Exception
     */
    public void testGetHB_html_unsupported() throws Exception {

        // get original message and parse it
        Message message = ManagerUtility.getTestMessage("H_B_unsupported_encoding_01.txt", session);
        EmailFilter clientFilter = new EmailFilter();
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = 
                instance.getHB(session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo("token", "userid", "http://www.myfunambol.com"));            
        Message resultMessage = result.getJavaMessage();
        
        // get expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testUnsupportedEncoding_01.txt", session);
        
                
        assertJavamessageEquals (expectedMsg, resultMessage);

    }

    
    
    // ------------------------------------------------------- Protected methods
    
    @Override
    protected void setUp() throws Exception {
        setMailserverConnectionProperties();
        session = Session.getInstance(connectionProperties);
        
        setFixedParameters();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Sets <code>FID, FMID, status, loc</code> to some known value. More precisely:
     * FID    is set to "I"
     * FMID   is set to "123456"
     * status is set to 'N'
     * loc    is set to null
     */
    private void setFixedParameters() {
        FID = "I";
        FMID = "123456";
        status = SyncItemState.NEW;
        loc = null;
    }
}
