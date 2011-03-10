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

package com.funambol.email.items.manager;

import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.SyncItemInfo;
import javax.mail.Message;
import javax.mail.Session;
import com.funambol.email.test.EmailConnectorTestCase;
import com.funambol.email.test.ManagerUtility;
import com.funambol.email.mocks.InboxTable;
import com.funambol.framework.engine.source.RefusedItemException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

/**
 *
 * @author $Id: EntityManagerTest.java,v 1.5 2008-06-16 08:49:28 gbmiglia Exp $
 */
public class EntityManagerTest extends EmailConnectorTestCase {

    // ------------------------------------------------------------ Private data
    private EntityManager entityManager;
    
    // ------------------------------------------------------------ Constructors
    public EntityManagerTest(String testName) {
        super(testName);
    }
    
    // -------------------------------------------- Setup/Tear down test methods
    @Override
    protected void setUp() throws Exception {
        setMailserverConnectionProperties();
        session = Session.getInstance(connectionProperties);
        entityManager = new EntityManager();
    }

    // ------------------------------------------------------------ Test methods
    public void testGetAllEmails() throws Exception {
        
        EmailFilter emailFilter = new EmailFilter();
        
        emailFilter.setId(null);
        Calendar calendar = new GregorianCalendar(2008, 1, 1);
        emailFilter.setTime(calendar.getTime());
        
        InboxTable inboxTable = new InboxTable();

        calendar.add(Calendar.DATE, -10);
        inboxTable.addItem("0", true,  calendar.getTime());
        inboxTable.addItem("1", true,  calendar.getTime());
        inboxTable.addItem("2", true,  new Date());
        inboxTable.addItem("3", true,  new Date());
        
        Map<String,SyncItemInfo> serverItems = inboxTable.getServerItems();
        
        entityManager.getAllEmails(emailFilter, serverItems);
    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_TXT_only() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();


        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B.txt", session);
        assertJavamessageEquals(expectedMsg, result);

    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_TXT_HTML() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();


        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B_txt_html.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B_txt_html.txt", session);
        assertJavamessageEquals(expectedMsg, result);
    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_HTML_only() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();

        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B_html.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B_html.txt", session);
        assertJavamessageEquals(expectedMsg, result);

    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_PDA_1() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();

        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B_from_PDA.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B_from_PDA.txt", session);
        assertJavamessageEquals(expectedMsg, result);

    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_PDA_2() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();

        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B_A_from_PDA.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B_A_from_PDA.txt", session);
        assertJavamessageEquals(expectedMsg, result);

    }

    /**
     * Test of createMessageWithSignature method,
     * of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithSignature_CELL_1() throws Exception {

        String sign      = com.funambol.email.util.Def.TRAILER;
        String from      = "smith@funambol.com";
        String firstname = "Anne";
        String lastname  = "Smith";
        EntityManager instance = new EntityManager();

        // Test message
        Message testMsg = ManagerUtility.getTestMessage("sign_H_B_from_CELL.txt", session);
        // result
        Message result = instance.createMessageToBeSent(testMsg,
                session, sign, from, firstname, lastname);
        Message expectedMsg = ManagerUtility.getExpectedMessage("sign_H_B_from_CELL.txt", session);
        assertJavamessageEquals(expectedMsg, result);

    }

    /**
     * Test of createMessageWithoutSignature method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessageWithoutSignature() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of createMessage method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateMessage() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of createFoundationMail method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateFoundationMail() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of createFoundationFolder method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateFoundationFolder() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of insertDefaultFolder method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testInsertDefaultFolder() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of isEmailInFilter method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testIsEmailInFilter() throws Exception {
    }

    /**
     * Test of insertInvalidItem method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testInsertInvalidItem() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of getEmailFromClause method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testGetEmailFromClause() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of checkMessageIDforDrafts method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCheckMessageIDforDrafts() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of insertCustomFolder method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testInsertCustomFolder() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of isEmail method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testIsEmail() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of setItemFolder method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testSetItemFolder() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of setItemMessage method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testSetItemMessage() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of sendEmail method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testSendEmail() throws Exception {
        // TODO add your test code.
    }

    /**
     * Test of hasMatchedDate method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testHasMatchedDate() {
        // TODO add your test code.
    }

    /**
     * Test of hasMatchedSize method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testHasMatchedSize() {
        // TODO add your test code.
    }

    /**
     * Test of createInfo method, of class com.funambol.email.items.manager.EntityManager.
     */
    public void testCreateInfo() throws Exception {
        // TODO add your test code.
    }

    public void testCreateMessageWithSignature() throws Exception {
    }

    /**
     * Test of checkIfItHasToBeRefused method, of class EntityManager.
     * @throws java.lang.Exception 
     */
    public void testCheckIfItHasToBeRefused_toBeRefused() throws Exception {
        Message msg = ManagerUtility.getTestMessage("invalidEmail.txt", session);
        EntityManager instance = new EntityManager();
        try {
            instance.checkIfItHasToBeRefused(msg);
        } catch (RefusedItemException ex) {
            assertTrue("The email is not valid thus the exception is correct", true);
            return;
        }
        fail("Should throw a RefusedItemException");
    }
}
