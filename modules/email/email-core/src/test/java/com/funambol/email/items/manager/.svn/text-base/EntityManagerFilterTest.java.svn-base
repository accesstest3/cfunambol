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

import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.email.test.EmailConnectorTestCase;
import com.funambol.email.test.ManagerUtility;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.internet.ContentType;
import javax.mail.internet.MimeMultipart;
import org.clapper.util.html.HTMLUtil;

/**
 * Tets class for testing <code>EntityManagerFilter</code>.
 *
 * $Id: EntityManagerFilterTest.java,v 1.6 2008-06-24 11:13:23 testa Exp $
 */
public class EntityManagerFilterTest extends EmailConnectorTestCase {

    // --------------------------------------------------------------- Constants
    private static final String USER_ID = "jhondoe";
    private static final String MAIL_ID = "1234567890";
    private static final String TOKEN = "ABCDEFGHI1234567890";
    private static final String ATTACH_URL = "http://my.funambol.com/content";
    private static final long   MAIL_SERVER_ACCOUNT_ID = 5678;
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

    public EntityManagerFilterTest(String testName) {
        super(testName);
    }

    // ---------------------------------------------------------- Public methods
    /*
     * Test of getH method, of class <code>com.funambol.email.items.manager.EntityManagerFilter</code>.
     */
    public void testGetH_H_B_2Kb() throws Exception {

        // Test message.
        Message testMsg =
                ManagerUtility.getTestMessage("H_B_2Kb.txt", session);
        String part0 = (String)testMsg.getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("H_B_2Kb.txt", session);

        // Expected ext
        int xsize = ManagerUtility.getHeadersSize(testMsg) + part0.length();
        int xvalbody = part0.length();
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, new ArrayList());

        // No client filter
        EmailFilter clientFilter = null;

        // Method result
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, testMsg, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals     (expectedMsg, result.getJavaMessage());
        assertExtEquals             (expectedExt, result.getExt());
    }

    public void testGetH_H_B_10Kb() throws Exception {

        // Test message.
        Message testMsg =
                ManagerUtility.getTestMessage("H_B_10Kb.txt", session);
        String part0 = (String)testMsg.getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("H_B_10Kb.txt", session);

        // Expected ext
        int xsize       = ManagerUtility.getHeadersSize(testMsg) + part0.length();
        int xvalbody    = part0.length();
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, new ArrayList());

        // No client filter
        EmailFilter clientFilter = null;

        // Method result
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, testMsg, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    public void testGetH_H_B_10Kb_A1_text_10Kb() throws Exception {

        // Test message.
        Message testMsg =
                ManagerUtility.getTestMessage("H_B_10Kb_A1_text_10Kb.txt", session);
        Part part0 = ((Multipart)testMsg.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)testMsg.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("H_B_10Kb_A1_text_10Kb.txt", session);

        // Expected ext
        int xsize =
                ManagerUtility.getHeadersSize(expectedMsg) +
                part0.getSize() +
                part1.getSize();
        int xvalbody = part0.getSize();

        List xvalAttach = new ArrayList();

        xvalAttach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(),
                part1.getSize(), new ContentType(part1.getContentType()).getBaseType(), ManagerUtility.contentProviderURL(ATTACH_URL,
                part1.getFileName(), TOKEN, USER_ID, 1)));
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalAttach);

        // No client filter
        EmailFilter clientFilter = null;

        // Method result
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, testMsg, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    public void testGetH_H_B_10Kb_A1_text_10Kb_A2_text_20Kb() throws Exception {

        // Test message.
        Message testMsg =
                ManagerUtility.getTestMessage("H_B_10Kb_A1_text_10Kb_A2_text_20Kb.txt", session);
        Part part0 = ((Multipart)testMsg.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)testMsg.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)testMsg.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("H_B_10Kb_A1_text_10Kb_A2_text_20Kb.txt", session);

        // Expected ext
        int xsize =
                ManagerUtility.getHeadersSize(expectedMsg) +
                part0.getSize() +
                part1.getSize() +
                part2.getSize();
        int xvalbody = part0.getSize();

        List xvalAttach = new ArrayList();

        xvalAttach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );

        xvalAttach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL, part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalAttach);

        // No client filter
        EmailFilter clientFilter = null;

        // Method result
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, testMsg, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    /*
     * Test of getHB method, of class <code>com.funambol.email.items.manager.EntityManagerFilter</code>.
     */

    // no truncation
    public void testGetHB_H_B_2Kb() throws Exception {

        // Test message.
        Message testMsg = ManagerUtility.getTestMessage("H_B_2Kb.txt", session);

        // Expected message.
        Message expectedMsg = ManagerUtility.getExpectedMessage("testGetHB_H_B_2Kb.txt", session);

        // No client filter
        int truncationSize = -1;
        EmailFilter clientFilter = null;

        // Expected ext
        int  xsize       = -1;
        int  xvalbody    = -1;
        List xvalAttach  = new ArrayList();
        Ext  expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalAttach);

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, testMsg, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_H_B_2Kb() throws Exception {

        // Test message.
        Message testMsg = ManagerUtility.getTestMessage("H_B_2Kb.txt", session);
        String part0 = (String)testMsg.getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_H_B_2Kb.txt", session);

        // Client size filtering.
        int truncationSize = 4;
        EmailFilter clientFilter = new EmailFilter();
        clientFilter.setSize(truncationSize);

        // Expected ext
        int  xsize      = ManagerUtility.getHeadersSize(expectedMsg) + part0.length();
        int  xvalbody   = part0.length() - truncationSize;
        List XvalAttach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, XvalAttach);

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, testMsg, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // -------------------------------------------------------------------------

    public void testGetH_issue_4245() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_P1_text_P2_html_P3_empty.txt", session);
        String body = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_4245.txt", session);

        // Expected ext
        int  xsize      = ManagerUtility.getHeadersSize(expectedMsg) + body.length();
        int  xvalbody   = body.length();
        List XvalAttach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, XvalAttach);

        // Client size filtering.
        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    public void testGetHB_issue_4245() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_P1_text_P2_html_P3_empty.txt", session);
        String body = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_issue_4245.txt", session);

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_issue_4245() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_P1_text_P2_html_P3_empty.txt", session);
        String body = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_issue_4245.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 4;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Expected ext
        int  xsize      =
                ManagerUtility.getHeadersSize(resultMessage)  +
                body.length();
        int  xvalbody   = body.length() - truncationSize;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // no truncation
    public void testGetHBA_issue_4245() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_P1_text_P2_html_P3_empty.txt", session);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_4245.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_4245() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_P1_text_P2_html_P3_empty.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_4245.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 4;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        // Expected ext
        int  xsize      = ManagerUtility.getHeadersSize(message) + part0.length();
        int  xvalbody   = part0.length() - truncationSize;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, result.getJavaMessage());
        assertExtEquals         (expectedExt, result.getExt());
    }

    // -------------------------------------------------------------------------

    /**
     * The emailconnector will generate an empty body part for this test email.
     * The two InternalPart objects creted for this email have indexes -1 (the
     * one created from scratch) and 0 (the part from  the original message).
     */
    public void testGetH_issue_038() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("038_Email_image_inside_apple_7.txt", session);
        Part part = ((MimeMultipart)message.getContent()).getBodyPart(0);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_038.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by an empty text body (size 0) and an attachment,
        // which is derived from the original only message part.
        //
        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = ManagerUtility.getHeadersSize(message) + 0 + part.getSize();
        int  xvalbody   = 0;
        List xvalAttach = new ArrayList();

        xvalAttach.add(ManagerUtility.getXvalAttachItem(part.getFileName(), part.getSize(),
                new ContentType(part.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part.getFileName(), TOKEN, USER_ID, 1)) );


        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalAttach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_issue_038() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("038_Email_image_inside_apple_7.txt", session);
        Part part = ((MimeMultipart)message.getContent()).getBodyPart(0);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_038.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by an empty text body (size 0) and an attachment,
        // which is derived from the original only message part.
        //
        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part.getFileName(), part.getSize(),
                new ContentType(part.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part.getFileName(), TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_issue_038() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("038_Email_image_inside_apple_7.txt", session);
        Part part = ((MimeMultipart)message.getContent()).getBodyPart(0);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_issue_038.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by an empty text body and an attachment (which
        // contains the original message part)
        //
        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part.getFileName(), part.getSize(),
                new ContentType(part.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part.getFileName(), TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_issue_038() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("038_Email_image_inside_apple_7.txt", session);
        Part part = ((MimeMultipart)message.getContent()).getBodyPart(0);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_038.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by an empty text body and an attachment (which
        // contains the original message part)
        //
        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_038() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("038_Email_image_inside_apple_7.txt", session);
        Part part = ((MimeMultipart)message.getContent()).getBodyPart(0);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_038.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by an empty text body and an attachment (which
        // contains the original message part)
        //
        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = ManagerUtility.getHeadersSize(message) + part.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part.getFileName(), part.getSize(),
                new ContentType(part.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part.getFileName(), TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // -------------------------------------------------------------------------

    public void testGetH_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter,
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize() +
                part1.getSize();
        int  xvalbody   = part0.getSize();
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Att002.htm", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Att002.htm", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Att002.htm", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Att002.htm", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize() +
                part1.getSize();
        int  xvalbody   = part0.getSize() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Att002.htm", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Att002.htm", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext
        int  xsize =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize() +
                part1.getSize();
        int  xvalbody   = part0.getSize() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Att002.htm", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Att002.htm", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation2_issue_020() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("020_Email_greg_error_grand_estate.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation2_issue_020.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 8000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize() +
                part1.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Att002.htm", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Att002.htm", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // -------------------------------------------------------------------------

    public void testGetH_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize();
        int  xvalbody   = part0.getSize();
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize();
        int  xvalbody   = part0.getSize() - truncationSize;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 1000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize();
        int  xvalbody   = part0.getSize() - truncationSize;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation2_issue_027() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("027_Email_body_with_calendar.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation2_issue_027.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 8000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);

        //
        // Because the second part of the original message is a text/html but
        // text body has already been extracted, this part is ignored, so it does
        // not even appear as an attachment.
        // See testGetHBA_with_truncation_2_issue_027 to ensure what happen if
        // the first part is text/html and the second one is text/plain (in that
        // case first part becomes the mail body and the second part becomes a
        // text attachment).
        //
    }

    // -------------------------------------------------------------------------

    public void testGetH_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize()                 +
                part1.getSize();
        int  xvalbody   = part0.getSize();
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Part002.txt", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Part002.txt", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Part002.txt", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Part002.txt", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    public void testGetHB_truncation1_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_with_truncation_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        //
        // Result message is formed by a text body (html part translated) and a
        // text attachment (whose name is assigned by the connector)
        //
        Message resultMessage         = result.getJavaMessage();
        String convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String convertedTruncatedPart = (String)resultMessage.getContent();
        Ext resultExt                 = result.getExt();

        // Expected ext.
        int  xsize      =
            ManagerUtility.getHeadersSize(message) +
            convertedPart.getBytes().length  +
            part1.getSize();
        int  xvalbody   = convertedPart.getBytes().length - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Part002.txt", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Part002.txt", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        String resultPart0    = (String)((Multipart)resultMessage.getContent()).getBodyPart(0).getContent();
        String resultPart1    = (String)((Multipart)resultMessage.getContent()).getBodyPart(1).getContent();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage         = result.getJavaMessage();
        String convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String convertedTruncatedPart = (String)resultMessage.getContent();
        Ext    resultExt              = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.getBytes().length +
                part1.getSize();
        int  xvalbody   = convertedPart.getBytes().length - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Part002.txt", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Part002.txt", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation2_issue_028() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("028_Email_html_yahoo.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation2_issue_028.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage         = result.getJavaMessage();
        String convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String convertedTruncatedPart = (String)resultMessage.getContent();
        Ext     resultExt             = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.getBytes().length +
                part1.getSize();
        int  xvalbody   = convertedPart.getBytes().length - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem("Part002.txt", part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"Part002.txt", TOKEN, USER_ID, 1)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // -------------------------------------------------------------------------

    public void testGetH_issue_030() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("030_Email_calendar_attendee_1.txt", session);
        String content  = ManagerUtility.getBodyForTextCalendar(message);
        int contentSize = ManagerUtility.getSize(content);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_issue_030.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();

        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                contentSize +
                contentSize; // we added an attachment
        int  xvalbody   = contentSize;
        List xvalattach = new ArrayList();
        String[] value = new String[4];
        value[0] = "calendar.ics";
        value[1] = ""+contentSize; // the attachment is the same content of the body
        value[2] = "text/calendar";
        value[3] = ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 1);

        xvalattach.add(value);

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_issue_030() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("030_Email_calendar_attendee_1.txt", session);
        String content  = ManagerUtility.getBodyForTextCalendar(message);
        int contentSize = ManagerUtility.getSize(content);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_issue_030.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        String[] value = new String[4];
        value[0] = "calendar.ics";
        value[1] = "" + contentSize; // the attachment is the same content of the body
        value[2] = "text/calendar";
        value[3] = ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 1);
        xvalattach.add(value);

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation1_issue_030() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("030_Email_calendar_attendee_1.txt", session);
        String content  = ManagerUtility.getBodyForTextCalendar(message);
        int contentSize = ManagerUtility.getSize(content);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation1_issue_030.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int humanReadableCalendarSize =
                MessageCreatorMethodWrappers.vCal2HumanReadableCalendar(content).length();
        
        int  xsize      = ManagerUtility.getHeadersSize(message) +
                          humanReadableCalendarSize +   // text part
                          contentSize;                  // invitation as attachment
        
        int  xvalbody   = humanReadableCalendarSize - truncationSize;
        
        List xvalattach = new ArrayList();
        
        String[] value = new String[4];
        value[0] = "calendar.ics";
        value[1] = "" + contentSize; // the attachment is the same content of the body
        value[2] = "text/calendar";
        value[3] = ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 1);
        
        xvalattach.add(value);

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_issue_030() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("030_Email_calendar_attendee_1.txt", session);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_issue_030.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation1_issue_030() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("030_Email_calendar_attendee_1.txt", session);
        String content  = ManagerUtility.getBodyForTextCalendar(message);
        int contentSize = ManagerUtility.getSize(content);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 100;
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected message.
        Message expectedMsg = ManagerUtility.getExpectedMessage("testGetHBA_truncation1_issue_030.txt", session);

        // Expected ext.
        int humanReadableCalendarSize =
                MessageCreatorMethodWrappers.vCal2HumanReadableCalendar(content).length();
        
        int  xsize      = ManagerUtility.getHeadersSize(message) +
                          humanReadableCalendarSize + // size of the text part (vcal in a human readable format)
                          contentSize;                // size of the attachment
        
        int  xvalbody   = humanReadableCalendarSize - truncationSize; //(dimensione del content trasformato) - truncationsize; //contentSize - truncationSize;

        List xvalattach = new ArrayList();
        
        String[] value = new String[4];
        value[0] = "calendar.ics";
        value[1] = "" + contentSize; // the attachment is the same content of the body
        value[2] = "text/calendar";
        value[3] = ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 1);
        xvalattach.add(value);

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }


    // -------------------------------------------------------------------------

    public void testGetH_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize()                 +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = part0.getSize();
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int xsize       =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize() +
                part1.getSize() +
                part2.getSize();
        int  xvalbody   = part0.getSize() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHB_truncation2_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation2_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int xsize       = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size + part1 size < Truncation size < part0 size + part1 size + part2 size
    public void testGetHB_truncation3_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation3_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 20000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int xsize       = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_truncation_H_B_text_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage  = result.getJavaMessage();
        String  resultPart0    = (String)((Multipart)resultMessage.getContent()).getBodyPart(0).getContent();
        Part    resultPart1    = ((Multipart)resultMessage.getContent()).getBodyPart(1);
        Part    resultPart2    = ((Multipart)resultMessage.getContent()).getBodyPart(2);
        Ext     resultExt      = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation2_H_B_text_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation2_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = part0.length() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation3_H_B_text_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation3_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation4_H_B_text_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation4_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size + part1 size < Truncation size < part0 size + part1 size + part2 size
    public void testGetHBA_truncation5_H_B_text_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pdf_A2_pic.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation4_H_B_text_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 20000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // -------------------------------------------------------------------------

    public void testGetH_H_B_html() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html.txt", session);
        String htmlContent = (String)message.getContent();

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_H_B_html.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                htmlContent.length();
        int  xvalbody   = htmlContent.length();
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);

        //
        // getH does not convert the html to text, while getHB and getHBA do.
        // This is for optimization, as getH must not involve any content read
        // (translate th html only to have a correct size is a waste).
        //
    }

    // -------------------------------------------------------------------------

    public void testGetH_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.getSize()                 +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = part0.getSize();
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHB_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHB_truncation_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage          = result.getJavaMessage();
        String  convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String  convertedTruncatedPart = (String)resultMessage.getContent();
        Ext     resultExt              = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.length()        +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = convertedPart.length() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHB_truncation_2_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation_2_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext resultExt         = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size + part1 size < Truncation size < part0 size + part1 size + part2 size
    public void testGetHB_truncation3_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation3_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 20000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHB(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // no truncation
    public void testGetHBA_H_B_html_A1_pdf_A2_pic() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation_2_H_B_html_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_truncation_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage          = result.getJavaMessage();
        String  convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String  convertedTruncatedPart = (String)resultMessage.getContent();
        Ext     resultExt              = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.length()         +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = convertedPart.length() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation3_H_B_html_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation3_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 1000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage          = result.getJavaMessage();
        String  convertedPart          = HTMLUtil.textFromHTML((String)part0.getContent());
        String  convertedTruncatedPart = (String)resultMessage.getContent();
        Ext resultExt                  = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.length()          +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation4_H_B_html_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation3_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        String  convertedPart = HTMLUtil.textFromHTML((String)part0.getContent());
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.length()         +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size + part1 size < Truncation size < part0 size + part1 size + part2 size
    public void testGetHBA_truncation5_H_B_html_A1_pdf_A2_pic () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_html_A1_pdf_A2_pic.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation5_H_B_html_A1_pdf_A2_pic.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 20000;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        String  convertedPart = HTMLUtil.textFromHTML((String)part0.getContent());
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                convertedPart.length()         +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // -------------------------------------------------------------------------

    // no truncation
    public void testGetHBA_H_B_text_A1_pic_1Mb_A2_pdf() throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pic_1Mb_A2_pdf.txt", session);
        Part part0 = ((Multipart)message.getContent()).getBodyPart(0);
        Part part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_H_B_text_A1_pic_1Mb_A2_pdf.txt", session);

        EmailFilter clientFilter = new EmailFilter();

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, -1, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // 0 < Truncation size < part0 size
    public void testGetHBA_truncation_2_H_B_text_A1_pic_1Mb_A2_pdf () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pic_1Mb_A2_pdf.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation_2_H_B_text_A1_pic_1Mb_A2_pdf.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 10;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = part0.length() - truncationSize;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }

    // part0 size < Truncation size < part0 size + part1 size
    public void testGetHBA_truncation3_H_B_text_A1_pic_1Mb_A2_pdf () throws Exception {

        // Test message.
        Message message =
                ManagerUtility.getTestMessage("H_B_text_A1_pic_1Mb_A2_pdf.txt", session);
        String part0 = (String)((Multipart)message.getContent()).getBodyPart(0).getContent();
        Part   part1 = ((Multipart)message.getContent()).getBodyPart(1);
        Part   part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_truncation3_H_B_text_A1_pic_1Mb_A2_pdf.txt", session);

        EmailFilter clientFilter = new EmailFilter();
        int truncationSize = 1024 * 600;

        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(
                session, FID, FMID, message, status, loc, truncationSize, clientFilter, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        Message resultMessage = result.getJavaMessage();
        Ext     resultExt     = result.getExt();

        // Expected ext.
        int  xsize      =
                ManagerUtility.getHeadersSize(message) +
                part0.length()                  +
                part1.getSize()                 +
                part2.getSize();
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();
        xvalattach.add(ManagerUtility.getXvalAttachItem(part1.getFileName(), part1.getSize(),
                new ContentType(part1.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part1.getFileName(), TOKEN, USER_ID, 1)) );
        xvalattach.add(ManagerUtility.getXvalAttachItem(part2.getFileName(), part2.getSize(),
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,part2.getFileName(), TOKEN, USER_ID, 2)) );

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals (expectedMsg, resultMessage);
        assertExtEquals         (expectedExt, resultExt);
    }



    // no truncation
    public void testGetHBA_Blocked_Tag() throws Exception {

        // Test message.
        Message inputMessage = ManagerUtility.getTestMessage("email_with_blocked_signature.txt", session);
        EmailFilter clientFilter = new EmailFilter();
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getHBA(session, FID, FMID,
                inputMessage, status, loc, -1, clientFilter,
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        // result message.
        Message resultMsg   = result.getJavaMessage();

        // Expected message.
        Message expectedMsg = ManagerUtility.getExpectedMessage("r_email_with_blocked_signature.txt", session);

        assertJavamessageEquals (expectedMsg, resultMsg);

    }

    // -------------------------------------------------------------------------

    /**
     * Test the getH method using a "multipart/alternative" email formed by 
     * three parts all containing the same calendar but having different types: 
     * a "text/plain", a "text/html" and a "text/calendar".
     * @throws java.lang.Exception
     */
    public void testGetH_multipart_alternative_plain_html_calendar() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("multipart_alternative_plain_html_calendar.txt", session);
        
        Part text     = ((Multipart)message.getContent()).getBodyPart(0);
        Part html     = ((Multipart)message.getContent()).getBodyPart(1);
        Part calendar = ((Multipart)message.getContent()).getBodyPart(2);        

        String textPartContent     = (String)text.getContent();
        String calendarPartContent = (String)calendar.getContent();
        
        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetH_multipart_alternative_plain_html_calendar.txt", session);

        // Result message.
        EmailFilter clientFilter = new EmailFilter();
        EntityManagerFilter instance = new EntityManagerFilter();
        ItemMessage result = instance.getH(
                session, FID, FMID, message, status, loc, clientFilter,
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext.
        Ext resultExt = result.getExt();

        // Expected ext.
        int resultTextPartSize = MessageCreatorMethodWrappers.vCal2HumanReadableCalendar(
                calendarPartContent).length();
        
        //
        // xsize is the sum of:
        //
        // - header size
        // - size of: text part + vcal string (not converted) + separator (a \n char)
        //   
        //   Note that this is not strictly correct: this addendum should be the
        //   size of the final (transformed) part. In order not to perform any
        //   transformation (which are not needed, since only headers are retrieved)
        //   a different size is taken.
        //
        // - calendar as attachment
        //
        int  xsize    =
                ManagerUtility.getHeadersSize(message) +                        // header size
                textPartContent.length() + 1 + calendarPartContent.length() +   // size of: text part + vcal string (not converted) + separator (a \n char)
                calendarPartContent.length();                                   // calendar as attachment
        
        int  xvalbody = textPartContent.length() + 1 + calendarPartContent.length();
        
        List xvalattach = new ArrayList();
        String[] value = ManagerUtility.getXvalAttachItem(
                "calendar.ics", 
                calendarPartContent.length(),
                new ContentType(calendar.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL, "calendar.ics", TOKEN, USER_ID, 2)) ;

        xvalattach.add(value);
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, true, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }
    
    /**
     * Test the getHB method using a "multipart/alternative" email formed by 
     * three parts all containing the same calendar but having different types: 
     * a "text/plain", a "text/html" and a "text/calendar".
     * @throws java.lang.Exception
     */
    public void testGetHB_multipart_alternative_plain_html_calendar() throws Exception {

        // Test message.
        Message message = ManagerUtility.getTestMessage("multipart_alternative_plain_html_calendar.txt", session);
        Part part2 = ((Multipart)message.getContent()).getBodyPart(2);

        // Expected message.
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_multipart_alternative_plain_html_calendar.txt", session);
        
        // Result message.
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter,
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext.
        Ext resultExt = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        
        boolean truncated = false;
        
        List xvalattach = new ArrayList();
        
        int part2Size = ManagerUtility.getSize(ManagerUtility.getBodyForTextCalendar(part2));
        String[] value = ManagerUtility.getXvalAttachItem("calendar.ics", part2Size,
                new ContentType(part2.getContentType()).getBaseType(),
                ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 2)) ;
        
        xvalattach.add(value);
        
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, truncated, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt); 
 
    }

    /**
     * Invitation email sent by outlook 2003.
     * 
     * The email is formed by:
     * - text/calendar part that contains the VCALENDAR
     */

    public void testGetHB_text_calendar_0() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("text_calendar.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_text_calendar.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        
        List xvalattach = new ArrayList();
        
        int contentSize = message.getSize();
        String mimeType = new ContentType(message.getContentType()).getBaseType();
        String[] value = ManagerUtility.getXvalAttachItem(
                "calendar.ics", 
                contentSize,
                mimeType, 
                ManagerUtility.contentProviderURL(ATTACH_URL, "calendar.ics", TOKEN, USER_ID, 0)) ;
        
        xvalattach.add(value);
        
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }

    public void testGetHBA_text_calendar_0() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("text_calendar.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_text_calendar.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHBA(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }

    /**
     * Invitation email sent by thunderbird, with outlook compatibility option.
     * 
     * The email is formed by:
     * - text/calendar part that contains the VCALENDAR
     * 
     * The email produced by thunderbird in this case is the same as outlook.
     */
    public void testGetHB_text_calendar_1() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("invitation_thunderbird_outlook_compatible.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_invitation_thunderbird_outlook_compatible.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt         = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        
        List xvalattach = new ArrayList();
        
        int contentSize = message.getSize();
        String mimeType = new ContentType(message.getContentType()).getBaseType();
        String[] value = ManagerUtility.getXvalAttachItem(
                "calendar.ics", 
                contentSize,
                mimeType, 
                ManagerUtility.contentProviderURL(ATTACH_URL,"calendar.ics", TOKEN, USER_ID, 0)) ;
        
        xvalattach.add(value);
        
        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }    

    public void testGetHBA_text_calendar_1() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("invitation_thunderbird_outlook_compatible.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_invitation_thunderbird_outlook_compatible.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHBA(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt         = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }    
    
    /**
     * Invitation email sent by thunderbird, without outlook compatibility option.
     * 
     * The email is formed by:
     * - text/plain part
     * - text/calendar part that contains the VCALENDAR
     * - application/ics that contains the VCALENDAR, and it is an attachment
     */
    public void testGetHB_text_calendar_2() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("invitation_thunderbird.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_invitation_thunderbird.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt = result.getExt();
        
        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
    }

    /**
     * Invitation email sent by apple.
     * 
     * The email is formed by:
     * - text/plain part
     * - application/ics that contains the VCALENDAR, and it is an attachment
     */
    public void testGetHB_text_calendar_3() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("invitation_apple.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_invitation_apple.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
    }

    public void testGetHBA_text_calendar_3() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("invitation_apple.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_invitation_apple.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHBA(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt         = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
    }

    /**
     * Email sent by thunderbird with a ics file and a picture attachments.
     * 
     * The email is formed by:
     * - text/plain part
     * - application/ics that contains the VCALENDAR
     * - image/jpeg inline part
     */
    public void testGetHB_text_calendar_4() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("thunderbird_ics_picture_attachment.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHB_thunderbird_ics_picture_attachment.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHB(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
    }

    public void testGetHBA_text_calendar_4() throws Exception {

        // Test message
        Message message = ManagerUtility.getTestMessage("thunderbird_ics_picture_attachment.txt", session);

        // Expected message
        Message expectedMsg =
                ManagerUtility.getExpectedMessage("testGetHBA_thunderbird_ics_picture_attachment.txt", session);

        // Result message
        EntityManagerFilter instance = new EntityManagerFilter();
        int truncationSize = -1;  // No client filter
        EmailFilter clientFilter = null;  // No client filter
        ItemMessage result = instance.getHBA(session, FID, FMID, message, status,
                loc, truncationSize, clientFilter, 
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));
        Message resultMessage = result.getJavaMessage();

        // Result Ext
        Ext resultExt         = result.getExt();
        
        // Expected ext.
        int  xsize      = -1;
        int  xvalbody   = -1;
        List xvalattach = new ArrayList();

        Ext expectedExt = ManagerUtility.getExt(xsize, xvalbody, false, xvalattach);

        assertJavamessageEquals ("Message", expectedMsg, resultMessage);
        assertExtEquals         ("Ext", expectedExt, resultExt);
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
     * Sets <code>FID, FMID, status, loc</code> to some known value01. More precisely:
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
