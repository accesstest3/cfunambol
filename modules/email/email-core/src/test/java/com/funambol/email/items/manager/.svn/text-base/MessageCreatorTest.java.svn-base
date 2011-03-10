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

import com.funambol.email.exception.EntityException;
import com.funambol.email.model.InternalPart;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.test.ManagerUtility;
import com.funambol.email.test.EmailConnectorTestCase;
import com.funambol.email.util.Def;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map.Entry;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import junit.framework.AssertionFailedError;
import junitx.util.PrivateAccessor;

/**
 *
 * @author $Id: MessageCreatorTest.java,v 1.4 2008-09-04 09:33:42 testa Exp $
 */
public class MessageCreatorTest extends EmailConnectorTestCase {

    // --------------------------------------------------------------- Constants
    private static final String USER_ID = "userId";
    private static final String TOKEN = "token";
    private static final String ATTACH_URL = "http://www.myfunambol.com";

    // ------------------------------------------------------------ Constructors
    public MessageCreatorTest(String methodName) {
        super(methodName);
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Test of getHB_GetPart method, of class MessageCreator.
     *
     * What to test:
     * - Ext.xvalAttach, how attachments are handled
     * - Ext.xsize
     *   (xsize = returned part size + attchaments size; is set only if truncation needed)
     *
     * Parameter ext is always a new Ext object passed to getHB_GetPart by
     * EntityManagerFilter.getHB.
     */


    /**
     * Tests correctness of the returned part type.
     * Parameters headerDimension, truncationSize are indifferent.
     */
    public void testGetHB_GetPart_returnedPartType() throws Exception {

        // message has 1 part, part is text

        Ext partInfo = new Ext();
        int headerDimension = 100;
        int truncationSize = -1;

        Message message = ManagerUtility.getTestMessage("H_B_2Kb_text_A_no.txt", session);
        ArrayList<InternalPart> parts = MessageParser.getAllPartsOfMessage(message,true);

        InternalPart resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertTrue(resultPart.isTextBody());

        // message has 1 part, part is html

        partInfo = new Ext();
        message = ManagerUtility.getTestMessage("H_B_2Kb_html_A_no.txt", session);
        parts = MessageParser.getAllPartsOfMessage(message, true);

        resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertTrue(resultPart.isHtmlBody());

        // message has 2 part, one part is text, one part is html

        partInfo = new Ext();
        message = ManagerUtility.getTestMessage("H_B_2Kb_part1_text_part2_html_A_no.txt", session);
        parts = MessageParser.getAllPartsOfMessage(message, true);

        resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension,  new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertTrue(resultPart.isTextBody());
    }

    /**
     * Tests correctness of xsize.
     */
    public void testGetHB_GetPart_xsize() throws Exception {

        //
        // if no truncation is needed:
        // - headerDimansion is indifferent
        //
        int truncationSize = -1;
        int headerDimension = 100;
        Ext partInfo = new Ext();

        Message message = ManagerUtility.getTestMessage("H_B_2Kb_html_A_no.txt", session);
        ArrayList<InternalPart> parts = MessageParser.getAllPartsOfMessage(message, true);

        InternalPart resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertEquals(partInfo.getXsize(), "");

        //
        // message has 1 part, part is text, truncation needed
        //
        truncationSize = 10;
        headerDimension = 100;
        partInfo = new Ext();

        message = ManagerUtility.getTestMessage("H_B_2Kb_text_A_no.txt", session);
        parts = MessageParser.getAllPartsOfMessage(message, true);
        int originalSize = parts.get(0).getSize();

        resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertEquals(partInfo.getXsize(), Integer.toString(
                originalSize + headerDimension));

        //
        // message has 1 part, part is html, truncation needed
        //
        truncationSize = 10;
        headerDimension = 100;
        partInfo = new Ext();

        message = ManagerUtility.getTestMessage("H_B_2Kb_html_A_no.txt", session);
        parts = MessageParser.getAllPartsOfMessage(message, true);

        int contentTransformedSize =
                convertHtmlBody(parts.get(0)).getBytes().length;

        resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension, new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertEquals(partInfo.getXsize(), Integer.toString(
                contentTransformedSize + headerDimension));
    }

    /**
     * Tests correctness of attachments handling.
     */
    public void testGetHB_GetPart_attachments() throws Exception {

        //
        // message has 2 part, part are both text
        //
        int truncationSize = -1;
        int headerDimension = 100;
        Ext partInfo = new Ext();

        Message message = ManagerUtility.getTestMessage("H_B_part1_text_part2_text.txt", session);
        ArrayList<InternalPart> parts = MessageParser.getAllPartsOfMessage(message, true);

        InternalPart resultPart = MessageCreator.getHB_GetPart(
                parts, truncationSize, partInfo, headerDimension,
                new ContentProviderInfo(TOKEN, USER_ID, ATTACH_URL));

        assertTrue(resultPart.isTextBody());
        assertEquals(partInfo.getXvalAttach().size(), 1);

        String expectedAttachmentName = "Part001.txt";
        int expectedAttachmentSize = ((String)parts.get(1).getPartValue()).getBytes().length;
        assertEquals(((String[])partInfo.getXvalAttach().get(0))[0], expectedAttachmentName);
        assertEquals(((String[])partInfo.getXvalAttach().get(0))[1], Integer.toString(expectedAttachmentSize));
    }

    /**
     * Test of truncBody method, of class MessageCreator.
     */
    public void testTruncBody() throws Exception {

        // Method truncBody affects:
        // - xvalBody, truncated flag in the Ext object parameter
        // - new internal part content size and content value

        //
        // Tests usage of truncBody inside the the getHB_GetPart method.
        //

        // Parameter ext is a new Ext object passed to truncBody by getHB_GetPart
        // and is filled by truncBody itself.

        // Test message.
        Message message = ManagerUtility.getTestMessage("H_B_2Kb.txt", session);
        ArrayList<InternalPart> parts = MessageParser.getAllPartsOfMessage(message, true);

        InternalPart part = parts.get(0);
        String originalContent = (String) part.getPartValue();

        Ext partInfo = new Ext();
        int truncationSize = 100;
        int originalSize = message.getSize();

        //
        // truncation size > original size
        //
        InternalPart resultPart = MessageCreator.truncBody(
                part, truncationSize, partInfo, originalSize);

        assertEquals(resultPart.getSize(), originalSize);
        assertEquals(resultPart.getPartValue(), originalContent);

        assertFalse(partInfo.isTruncated());
        assertEquals(partInfo.getXvalBody(), "");

        //
        // truncation size == original size
        //
        truncationSize = originalSize;

        resultPart = MessageCreator.truncBody(
                part, truncationSize, partInfo, originalSize);

        assertEquals(resultPart.getSize(), originalSize);
        assertEquals(resultPart.getPartValue(), originalContent);

        assertFalse(partInfo.isTruncated());
        assertEquals(partInfo.getXvalBody(), "");

        //
        // truncation size < original size
        //
        truncationSize = 10;
        String truncatedContent = originalContent.substring(0, truncationSize);

        resultPart = MessageCreator.truncBody(
                part, truncationSize, partInfo, originalSize);

        assertEquals(resultPart.getSize(), truncationSize);
        assertEquals(resultPart.getPartValue(), truncatedContent);

        assertTrue(partInfo.isTruncated());
        assertEquals(partInfo.getXvalBody(), Integer.toString(
                originalSize - truncationSize));
    }

    /**
     * Test of convertHtmlBody method, of class MessageCreator.
     */
    public void testConvertHtmlBody() throws Exception {
    }

    /**
     * Test correctness of the attachment url.
     */
    public void testGetAttachmentUrl() throws Throwable {

        String token = "token";
        String username = "username";
        int index = 1;
        InternalPart internalPart = new InternalPart();
        internalPart.setIndex(index);
        String filename = "filename.txt";
        internalPart.setFileName(filename);
        String attachUrl = "http://www.myfunambol.com/contentservice";

        String resultUrl = (String) PrivateAccessor.invoke(MessageCreator.class, "getAttachmentUrl",
                new Class[] {ContentProviderInfo.class, InternalPart.class},
                new Object[] {new ContentProviderInfo(token, username, attachUrl), internalPart});

        StringBuilder expectedUrl = new StringBuilder();
        expectedUrl.append(attachUrl).append("/").append(filename).append("?");
        expectedUrl.append(MessageCreator.TOKEN_PARAM_NAME).append('=').append(URLEncoder.encode(token, "utf-8")).append('&');
        expectedUrl.append(MessageCreator.USERID_PARAM_NAME).append('=').append(URLEncoder.encode(username, "utf-8")).append('&');
        expectedUrl.append(MessageCreator.INDEX_PARAM_NAME).append('=').append(index);

        assertEquals(expectedUrl.toString(), resultUrl);
    }
    /**
     * Test correctness of the attachment url. URLs are url-encoded.
     */
    public void testGetAttachmentUrlEncoded() throws Throwable {

        //
        // parameter strings (token, username) contains all the chars that could
        // be encoded.
        //
        String token = "token;?/:#&=+$, %<>~";
        String username = "username;?/:#&=+$, %<>~";
        String attachUrl = "http://www.myfunambol.com/contentservice";
        int index = 1;
        InternalPart internalPart = new InternalPart();
        internalPart.setIndex(index);
        String filename = "filename.txt";
        internalPart.setFileName(filename);

        String resultUrl = (String) PrivateAccessor.invoke(MessageCreator.class, "getAttachmentUrl",
                new Class[] {ContentProviderInfo.class, InternalPart.class},
                new Object[] {new ContentProviderInfo(token, username, attachUrl), internalPart});

        StringBuilder expectedUrl = new StringBuilder();
        expectedUrl.append(attachUrl).append("/").append(filename).append("?");
        expectedUrl.append(MessageCreator.TOKEN_PARAM_NAME).append('=').append(URLEncoder.encode(token, "utf-8")).append('&');
        expectedUrl.append(MessageCreator.USERID_PARAM_NAME).append('=').append(URLEncoder.encode(username, "utf-8")).append('&');
        expectedUrl.append(MessageCreator.INDEX_PARAM_NAME).append('=').append(index);

        assertEquals(expectedUrl.toString(), resultUrl);
    }

    public void testGetMimeType() throws Throwable {

        String[] testContentTypes = {
            null, "",
            "text/plain;\n\tcharset=\"iso-8859-1\"",
            "text/html;\n\tcharset=\"iso-8859-1\""
        };

        String[] expectedMimeTypes = {
            MessageCreator.UNKNOWN_MIME_TYPE, MessageCreator.UNKNOWN_MIME_TYPE,
            "text/plain",
            "text/html"
        
        
        
        };

        for (int i = 0; i < testContentTypes.length; i++) {

            String testContentType = null;
            try {
                testContentType = testContentTypes[i];

                String resultMimetype = (String) PrivateAccessor.invoke(MessageCreator.class, "getMimeType",
                        new Class[]{String.class}, new String[]{testContentType});
                String expectedMimetype = expectedMimeTypes[i];

                assertEquals(testContentType, expectedMimetype, resultMimetype);
            } catch (Exception exception) {
                throw new AssertionFailedError(testContentType);
            }
        }

    }

    /**
     * Test if the a long file name is truncated
     * @throws java.lang.Throwable 
     */
    public void testTruncNameWithoutExtention() throws Throwable {
        String fileName = "filenameveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryverylong";
        String trunName = (String) PrivateAccessor.invoke(MessageCreator.class, "truncName", new Class[]{String.class}, new Object[]{fileName});
        String expectedName = "filenameveryveryveryveryveryveryveryveryveryveryve";
        
        assertEquals(expectedName, trunName);
    }

    /**
     * Test if the a long file name is truncated preserving the extention
     * @throws java.lang.Throwable 
     */
    public void testTruncNameWithExtention() throws Throwable {
        String fileName = "filenameveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryveryverylong.txt";
        String trunName = (String) PrivateAccessor.invoke(MessageCreator.class, "truncName", new Class[]{String.class}, new Object[]{fileName});
        String expectedName = "filenameveryveryveryveryveryveryveryveryveryve.txt";
        
        assertEquals(expectedName, trunName);
    }
                    
    // -------------------------------------------------------------------------
    
    public void testHeaderEncoding_0() throws Throwable {
        
        Message testMessage = new MimeMessage(session);
        
        String testAddressList =
                "\"Jhon,Doe\" <garrr@test.com>, " +       
                "simple ascii text 1 <ascii.text.1@test.com>, " +
                "=?UTF-8?B?cGlwcG/jgqxwYXBwbyDYtCDDqMOoIHRlc3Qg44K5IHR0INi0?= <japanese.arabic.latin@test.com>, " +
                "simple ascii text 2 <ascii.text.2@test.com>, " +
                "=?ISO-8859-1?Q?Keld_J=F8rn_Simonsen?= <quoted.printable@test.com>, " +
                "only.address@test.com, " +
                "=?UTF-8?B?44Ks44K044K5?= <japanese.only@test.com>";

        String expectedAddressList =
                "\"Jhon,Doe\" <garrr@test.com>, " +           
                "simple ascii text 1 <ascii.text.1@test.com>, " +
                
                "=?utf-8?Q?pippo=E3=82=ACpappo_=D8=B4_?=\n" +
                "=?utf-8?Q?=C3=A8=C3=A8_test_=E3=82=B9_tt_=D8=B4?= <japanese.arabic.latin@test.com>, " +
                
                "simple ascii text 2 <ascii.text.2@test.com>, " +
                "=?utf-8?Q?Keld_J=C3=B8rn_Simonsen?= <quoted.printable@test.com>, " +
                "only.address@test.com, " +
                "=?UTF-8?B?44Ks44K044K5?= <japanese.only@test.com>";
        
        String expectedSubject =
                "=?UTF-8?B?cGlwcG/jgqxwYXBwbyDYtCDDqMOoIHRlc3Qg44K5IHR0INi0?=";
        
        String[][] stringHeaders = {
            new String[]{"To", testAddressList},
            new String[]{"Subject", expectedSubject}
        };
        
        for (int i = 0; i < stringHeaders.length; i++) {
            String name = stringHeaders[i][0];
            String value = stringHeaders[i][1];
            testMessage.addHeader(name, value);
        }

        Enumeration<Header> headers = testMessage.getAllHeaders();
        while (headers.hasMoreElements()) {
            
            Header header = (Header) headers.nextElement();            
            Object newHeaderValue =
                    MessageCreatorMethodWrappers.createNewHeader(testMessage, header);
            
            if (Def.HEADER_TO.equals(header.getName())) {
                
                //
                // Asserts adress lists
                //
                InternetAddress[] newAddressList =
                        (InternetAddress[]) newHeaderValue;

                assertEqualsAddressLists (
                        InternetAddress.parseHeader(expectedAddressList, false),
                        newAddressList);
                
            } else if (Def.HEADER_SUBJECT.equals(header.getName())) {
                
                //
                // Asserts text values
                //
                String newValue = (String)newHeaderValue;
                
                assertEquals(expectedSubject, newValue);
            }
        }
    }

    /**
     * Test for bug #6508
     */
    public void testHeaderEncoding_1() throws Throwable {

        Message testMessage = new MimeMessage(session);

        String testCCAddressList =
                "\"'Ata Ras'\" <ata@test.com>,\n" +
                "=?ks_c_5601-1987?B?J8DMseK/tSc=?= <just4test@lggggge.com>";
        String testFrom = "=?ks_c_5601-1987?B?udq5/MH4W0J1bWppbl0=?= <bum@lgggggge.com>";

        String expectedCCAddressList =
                "\"'Ata Ras'\" <ata@test.com>, =?utf-8?B?J+ydtOq4sOyYgSc=?= <just4test@lggggge.com>";

        String expectedFrom = "=?utf-8?Q?=EB=B0=95=EB=B2=94=EC=A7=84=5BBumjin=5D?= <bum@lgggggge.com>";
        
        HashMap<String, String> stringTestHeaders = new HashMap<String, String>();
        stringTestHeaders.put("Cc", testCCAddressList);
        stringTestHeaders.put("From", testFrom);
        
        HashMap<String, String> stringExpectedHeaders = new HashMap<String, String>();
        stringExpectedHeaders.put("Cc", expectedCCAddressList);
        stringExpectedHeaders.put("From", expectedFrom);

        for (Entry entry : stringTestHeaders.entrySet()) {
            String name = (String) entry.getKey();
            String value = (String) entry.getValue();
            testMessage.addHeader(name, value);
        }

        Enumeration<Header> testHeaders = testMessage.getAllHeaders();
        while (testHeaders.hasMoreElements()) {

            Header testHeader = (Header) testHeaders.nextElement();
            Object newHeaderValue =
                    MessageCreatorMethodWrappers.createNewHeader(testMessage, testHeader);

            if (Def.HEADER_CC.equals(testHeader.getName().toUpperCase())) {

                InternetAddress[] newAddressList =
                        (InternetAddress[]) newHeaderValue;

                assertEqualsAddressLists (
                        InternetAddress.parseHeader(stringExpectedHeaders.get("Cc"), false),
                        newAddressList);
                
            } else if (Def.HEADER_FROM.equals(testHeader.getName())) {

                InternetAddress[] newAddressList =
                        (InternetAddress[]) newHeaderValue;

                assertEqualsAddressLists (
                        InternetAddress.parseHeader(stringExpectedHeaders.get("From"), false),
                        newAddressList);
            }

        }
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        setMailserverConnectionProperties();
        session = Session.getInstance(connectionProperties);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Wrapper around <code>com.funambol.email.items.manager.MessageCreator</code>
     */
    private static String convertHtmlBody(InternalPart part)
    throws EntityException {
        try {

            Class<?> c = Class.forName("com.funambol.email.items.manager.MessageCreator");
            Method[] allMethods = c.getDeclaredMethods();

            String contentConverted = null;

            for (Method m : allMethods) {
                String methodName = m.getName();
                if (methodName.equals("convertHtmlBody")) {
                    m.setAccessible(true);
                    contentConverted = (String) m.invoke(null, part);
                    break;
                }
            }
            return contentConverted;

        } catch (Exception e) {
            throw new EntityException(e);
        }
    }
}
