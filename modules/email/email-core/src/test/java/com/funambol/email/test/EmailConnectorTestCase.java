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

package com.funambol.email.test;

import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.util.Def;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Properties;
import javax.mail.BodyPart;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMultipart;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

/**
 * <code>TestCase</code> class extension for use with email connector tests.
 * @version $Id: EmailConnectorTestCase.java,v 1.2 2008-06-24 11:14:28 testa Exp $
 */
public class EmailConnectorTestCase extends TestCase {

    // ---------------------------------------------------------- Protected data

    /** Javamail session object used by a test. */
    protected Session session;

    /** Mail server connection properties. */
    protected Properties connectionProperties;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of <code>EmailConnectorTestCase</code>. */
    public EmailConnectorTestCase(String methodName) {
        super(methodName);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Test of convertHtmlBody method, of class MessageCreator.
     */
    public void testDummy() throws Exception {
    }
    
    /**
     * Asserts if an <code>Ext</code> objects is equal to the expected one.
     *
     * @param message a message to be shown in case the assertion fails
     * @param expected expected <code>Ext</code> object
     * @param result <code>Ext</code> object to compare
     */
    public static void assertExtEquals(String message, Ext expected, Ext result) {

        assertEquals(message, expected.getXnam()    , result.getXnam());
        assertEquals(message, expected.getXsize()   , result.getXsize());
        assertEquals(message + " body size", expected.getXvalBody(), result.getXvalBody());
        assertEquals(message + " is truncated", expected.isTruncated(), result.isTruncated());
                
        List<String[]> xvalAttach = expected.getXvalAttach();
        List<String[]> otherXvalAttach = result.getXvalAttach();

        assertEquals(message + ", Xval lists have a different size", xvalAttach.size(), otherXvalAttach.size());

        for (String[] attach : xvalAttach) {
            boolean found = false;

            for (String[] otherAttach : otherXvalAttach) {
                
                //
                // 2 xval are said to be the same if they have the same pair
                // (attachment name, index)
                //
                if(attach[3].equals(otherAttach[3])) {
                    
                    assertEquals(message + ", number of Xval elements for attachment " + attach[0],
                            attach.length, otherAttach.length);
                    assertEquals(message + ", size for attachment " + attach[0], 
                            attach[1], otherAttach[1]);
                    
                    if (attach.length > 2) {
                        assertEquals(message + ", number of Xval elements for attachment " + attach[0],
                                4, attach.length);
                        assertEquals(message + ", mime type for attachment " + attach[0], 
                                attach[2], otherAttach[2]);
                        assertEquals(message + ", the url is different", 
                                attach[3], otherAttach[3]);
                    }
                    
                    found = true;
                    break;
                }
            }

            if (found == false) {
                fail(message + ", expected attachment " + attach[0] + " is not present");
            }
        }
    }

    /**
     * Asserts if given <code>Ext</code> objects are equals.
     *
     * @param ext an <code>Ext</code> object to compare
     * @param otherExt the other <code>Ext</code> object to compare
     */
    public static void assertExtEquals(Ext ext, Ext otherExt) {
        assertExtEquals(null, ext, otherExt);
    }

    /**
     * Asserts if a <code>Message</code> object is equal to the expected one.
     *
     * @param message a message to be shown in case the assertion fails
     * @param expected expected <code>Message</code> object
     * @param otherMsg <code>Message</code> object to compare
     */
    public static void assertJavamessageEquals(
            String message, Message expected, Message result)
            throws Exception {

        // Headers
        assertHeaders(message, expected, result);

        // Flags
        assertFlag(expected, result, Flags.Flag.ANSWERED);
        assertFlag(expected, result, Flags.Flag.DELETED);
        assertFlag(expected, result, Flags.Flag.DRAFT);
        assertFlag(expected, result, Flags.Flag.FLAGGED);
        assertFlag(expected, result, Flags.Flag.RECENT);
        assertFlag(expected, result, Flags.Flag.SEEN);
        assertFlag(expected, result, Flags.Flag.USER);

        // Content
        assertContent(message, expected, result);
    }

    public static void assertContent(String message, Message expected, Message result)
    throws Exception {

        Object expectedContent = expected.getContent();
        Object resultContent = result.getContent();

        if (expectedContent instanceof String) {
            if (resultContent instanceof String) {
//                assertEquals1(message, expectedContent, resultContent);
                assertEqualsStringContent((String)expectedContent, (String)resultContent);
            } else if (resultContent instanceof MimeMultipart) {
                assertEquals(message,
                        expectedContent,
                        ((MimeMultipart)resultContent).getBodyPart(0).getContent());
            }
        }

        if (expectedContent instanceof MimeMultipart) {
            if (resultContent instanceof String) {

//                assertEquals(message,
//                        ((MimeMultipart)expectedContent).getBodyPart(0).getContent(),
//                        resultContent);

                assertEqualsStringContent(
                        (String)((MimeMultipart)expectedContent).getBodyPart(0).getContent(),
                        (String)resultContent);
                
            } else if (resultContent instanceof MimeMultipart) {

                int expectedPartsCount = ((MimeMultipart)expectedContent).getCount();

                for (int i = 0; i < expectedPartsCount; i++) {

                    BodyPart expectedPart = ((MimeMultipart)expectedContent).getBodyPart(i);
                    BodyPart resultPart   = ((MimeMultipart)resultContent).getBodyPart(i);

                    Object expectedPartObject = expectedPart.getContent();
                    Object resultPartObject   = resultPart.getContent();
                    
                    if (expectedPartObject instanceof String) {
                        if (resultPartObject instanceof String) {
                            //assertEquals(expectedPartObject, resultPartObject);
                            assertEqualsStringContent((String)expectedPartObject, (String)resultPartObject);
                        }
                    } else {
                        // verifies attachments
                        byte[] resultBytes = new byte[ManagerUtility.getSize((InputStream)resultPartObject)];
                        ((InputStream)resultPartObject).read(resultBytes);
                        String resultString = new String(resultBytes);

                        byte[] expectedBytes = new byte[ManagerUtility.getSize((InputStream)expectedPartObject)];
                        ((InputStream)expectedPartObject).read(expectedBytes);
                        String expectedString = new String(expectedBytes);
                        
                        assertEquals (expectedString, resultString);
                    }
                }
            }
        }
    }

    private static void assertEqualsStringContent(String expected, String result){
        BufferedReader expectedReader = new BufferedReader(new StringReader(expected));
        BufferedReader resultReader = new BufferedReader(new StringReader(result));

        String resultLine = null;
        String expectedLine = null;

        try {
            while ((resultLine = resultReader.readLine()) != null) {

                expectedLine = expectedReader.readLine();

                if (expectedLine == null) {
                    fail();
                }

                if (!resultLine.equals(expectedLine)) {
                    fail();
                }
            }
            expectedLine = expectedReader.readLine();
            if (expectedLine != null) {
                fail();
            }
        } catch (Exception e) {
            fail();
        }
    }

    public void assertContent(Message expected, Message result)
    throws Exception {
        assertContent(null, expected, result);
    }

    /**
     * Asserts if a <code>Message</code> object is equal to the expected one.
     *
     * @param expected expected <code>Message</code> object
     * @param otherMsg <code>Message</code> object to compare
     */
    public static void assertJavamessageEquals(Message expected, Message result)
    throws Exception {
        assertJavamessageEquals(null, expected, result);
    }

    /**
     * Asserts if for given messages the specified flag is set.
     *
     * @param message a message to be shown in case the assertion fails
     * @param msg an <code>Message</code> object to compare
     * @param otherMsg the other <code>Message</code> object to compare
     * @param flag flag to check
     * @throws javax.mail.MessagingException
     */
    public static void assertFlag(
            String message,
            Message msg,
            Message otherMsg,
            Flags.Flag flag)
            throws MessagingException {

        if (
                (msg.isSet(flag) && !otherMsg.isSet(flag)) ||
                (!msg.isSet(flag) && otherMsg.isSet(flag))) {

            if (message == null) {
                fail(message);
            }
        }
    }

    /**
     * Asserts if for given messages the specified flag is set.
     *
     * @param msg an <code>Message</code> object to compare
     * @param otherMsg the other <code>Message</code> object to compare
     * @param flag flag to check
     * @throws javax.mail.MessagingException
     */
    public static void assertFlag(
            Message msg,
            Message otherMsg,
            Flags.Flag flag)
            throws MessagingException {

        assertFlag(null, msg, otherMsg, flag);
    }

    /**
     * Asserts if headers in expected message are equal to the ones in another
     * message. Assertion fails when an expected header is not found or has a
     * different value.
     *
     * @param message a message to be shown in case the assertion fails
     * @param expected expected message
     * @param result message whose headers have to be compared
     */
    public static void assertHeaders(
            String message, Message expected, Message result)
            throws MessagingException {

        for (String headerName : Def.HEADER_MINIMUM) {

            if (Def.HEADER_MESSAGE_ID.equals(headerName)){
                continue;
            }
            if (expected.getHeader(headerName) != null) {
                assertHeaderEquals(message,
                        expected.getHeader(headerName),
                        result.getHeader(headerName));
            }
        }
    }

    /**
     * Asserts if mail headers in an expected set are equal to the ones in another
     * given set. Assertion fails when an expected header is not found or has a
     * different value.
     *
     * @param expectedHeader expected message
     * @param resultHeader message whose headers have to be compared
     */
    public void assertHeaders(Message expectedMsg, Message resultMsg)
    throws MessagingException {
        assertHeaders(null, expectedMsg, resultMsg);
    }

    /**
     * Asserts if an expected mail header is equal to another one.
     *
     * @param message message to be shown if assertion fails
     * @param expectedHeader expected header
     * @param resultHeader header to be compared
     */
    public static void assertHeaderEquals(
            String message, String[] expectedHeader, String[] resultHeader) {
        assertEquals(message, expectedHeader[0], resultHeader[0]);
    }

    /**
     * Asserts if an expected mail header is equal to another one.
     *
     * @param expectedHeader
     * @param resultHeader
     */
    public void assertHeaderEquals(String[] expectedHeader, String[] resultHeader) {
        assertHeaderEquals(null, expectedHeader, resultHeader);
    }
    
    /**
     * Asserts if a stream of bytes is equal to another one, byte per byte.
     */
    public static void assertEqualsStreams (String message, InputStream exp, InputStream res) 
    throws Exception {
        int e = -1;
        int r = -1;
        while ((e = exp.read()) != -1) {
            r = res.read();
            if (e != r){
                if (message == null){
                    message = "(suitable message here)";
                }
                throw new AssertionFailedError(message);
            }
        }        
    }
    
    public static void assertEqualsStreams(InputStream exp, InputStream res)
    throws Exception {
        assertEqualsStreams(null, exp, res);
    }

    /**
     * Asserts if two lists of email addresses are equals.
     */
    public static void assertEqualsAddressLists(
            InternetAddress[] exp, InternetAddress[] res){
        assertEqualsAddressLists(null, exp, res);
    }
    
    public static void assertEqualsAddressLists(String message,
            InternetAddress[] exp, InternetAddress[] res){
        
        if (exp.length != res.length){
            fail(message);
        }
        
        for (int i = 0; i < exp.length; i++) {
            InternetAddress expAddress = exp[i];
            InternetAddress resAddress = res[i];
            
            assertEquals(message, expAddress.getAddress(), resAddress.getAddress());
            assertEquals(message, expAddress.getPersonal(), resAddress.getPersonal());
        }
    }
    
    // ------------------------------------------------------- Protected methods

    /**
     * Creates and fills <code>connectionProperties</code>.
     */
    protected void setMailserverConnectionProperties() {

        if (connectionProperties == null) {
            connectionProperties = new Properties();
        }

        connectionProperties.setProperty("mail.imap.host"     , "mail.funambol.com");
        connectionProperties.setProperty("mail.imap.port"     , "143");
        connectionProperties.setProperty("mail.store.protocol", "imap");

        connectionProperties.setProperty("mail.smtp.host"         , "mail.funambol.com");
        connectionProperties.setProperty("mail.smtp.port"         , "25");
        connectionProperties.setProperty("mail.transport.protocol", "smtp");

        connectionProperties.setProperty("mail.imap.timeout"          , "300000");
        connectionProperties.setProperty("mail.imap.connectiontimeout", "15000");
    }
}











