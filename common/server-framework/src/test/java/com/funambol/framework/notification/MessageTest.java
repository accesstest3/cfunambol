/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

package com.funambol.framework.notification;

import junit.framework.*;

import com.funambol.framework.notification.Message.Type;

/**
 * Message's test cases.
 *
 * @version $Id$
 */
public class MessageTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public MessageTest(String testName) {
        super(testName);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Test constructor with type and messageContent parameters
     */
    public void testConstructor_Type12() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_2_TYPE, messageContent);

        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test constructor with type and messageContent parameters
     * variation: different value for type constructor parameter
     */
    public void testConstructor_Type11() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_1_TYPE, messageContent);

        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setType method, of class Message.
     */
    public void testSetType_From12To11() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_2_TYPE, messageContent);

        instance.setType(Message.STANDARD_1_1_TYPE);
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }
    
     /**
     * Test of setType method, of class Message.
     */
    public void testSetType_From11To12() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_1_TYPE, messageContent);

        instance.setType(Message.STANDARD_1_2_TYPE);
        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }
    
    /**
     * Test setMessageType method with parameter null.
     */
    public void testSetMessageType_Null1() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_2_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test setMessageType method with parameter null.
     */
    public void testSetMessageType_Null2() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_1_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setType method starting from a null messageType.
     */
    public void testSetType_WithMessageTypeNull1() {

        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_2_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());

        instance.setType(Message.STANDARD_1_1_TYPE);
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setType method starting from a null messageType.
     */
    public void testSetType_WithMessageTypeNull2() {

        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_1_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());

        instance.setType(Message.STANDARD_1_2_TYPE);
        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test of setMessageTypeToType method, of class Message.
     */
    public void testSetMessageTypeToType1() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_1_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());

        instance.setMessageTypeToType();
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
    }

    /**
     * Test of setMessageTypeToType method, of class Message.
     */
    public void testSetMessageTypeToType2() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Message.STANDARD_1_2_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());

        instance.setMessageTypeToType();
        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
    }

    //
    // tests with deprecated methods
    //
    
    /**
     * Test constructor with messageType and messageContent parameters
     */
    public void testConstructor_MessageType12() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, messageContent);

        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test constructor with messageType and messageContent parameters
     * variation: different value for type constructor parameter
     */
    public void testConstructor_MessageType11() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, messageContent);

        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setMessageType method, of class Message.
     */
    public void testSetMessageType_From12To11() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE);
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setMessageType method, of class Message.
     */
    public void testSetMessageType_From11To12() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE);
        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }
    
    /**
     * Test of setMessageType method with parameter null.
     */
    public void testSetMessageType_Null3() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test of setMessageType method with parameter null.
     */
    public void testSetMessageType_Null4() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setMessageType method starting from a null messageType.
     */
    public void testSetMessageType_WithMessageTypeNull1() {

        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());

        instance.setMessageType(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE);
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());
    }

    /**
     * Test of setMessageType method starting from a null messageType.
     */
    public void testSetMessageType_WithMessageTypeNull2() {

        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());
        assertEquals(Message.STANDARD_1_1_TYPE, instance.getType());

        instance.setMessageType(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE);
        assertEquals(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
        assertEquals(Message.STANDARD_1_2_TYPE, instance.getType());
    }

    /**
     * Test of setMessageTypeToType method, of class Message.
     */
    public void testSetMessageTypeToType3() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());

        instance.setMessageTypeToType();
        assertEquals(Type.STANDARD_1_1_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
    }

    /**
     * Test of setMessageTypeToType method, of class Message.
     */
    public void testSetMessageTypeToType4() {
        byte[] messageContent = "msg".getBytes();
        Message instance = new Message(Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, messageContent);

        instance.setMessageType(null);
        assertNull(instance.getMessageType());

        instance.setMessageTypeToType();
        assertEquals("", Type.STANDARD_1_2_NOTIFICATION_MESSAGE_TYPE, instance.getMessageType());
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

}
