/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.common.pim.contact;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import com.funambol.framework.tools.merge.MergeResult;

/**
 * Contact testcases
 * @version $Id: ContactTest.java,v 1.2 2007-11-28 11:14:31 nichele Exp $
 */
public class ContactTest extends TestCase {

    // ------------------------------------------------------------- Constructor
    public ContactTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void testMerge_ClientContactWithPhotoAndFirstName_and_ServerContactWithPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo();
        clientPhoto.setType("JPEG");
        byte[] clientImage = new byte[] {0x01, 0x03, 0x05};
        clientPhoto.setImage(clientImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo serverPhoto = new Photo();
        serverPhoto.setType("GIF");
        byte[] serverImage = new byte[] {0x11, 0x23, 0x35};
        serverPhoto.setImage(serverImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);


        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client is required
        assertFalse(mergeResult.isSetARequired());

        // The server must be updated
        assertTrue(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, clientContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", clientContact.getPersonalDetail().getPhotoObject().getType());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, serverContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", serverContact.getPersonalDetail().getPhotoObject().getType());

    }

    public void testMerge_ClientContactWithPhotoAndFirstName_and_ServerContactWithoutPhotoAndWithFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo();
        clientPhoto.setType("JPEG");
        byte[] clientImage = new byte[] {0x01, 0x03, 0x05};
        clientPhoto.setImage(clientImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client is required
        assertFalse(mergeResult.isSetARequired());

        // The server must be updated
        assertTrue(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, clientContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", clientContact.getPersonalDetail().getPhotoObject().getType());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, serverContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", serverContact.getPersonalDetail().getPhotoObject().getType());

    }


    public void testMerge_ClientContactWithPhotoAndFirstName_and_ServerContactEmptyPhotoAndWithFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo();
        clientPhoto.setType("JPEG");
        byte[] clientImage = new byte[] {0x01, 0x03, 0x05};
        clientPhoto.setImage(clientImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");
        Photo serverPhoto = new Photo(null, null, null);
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client is required
        assertFalse(mergeResult.isSetARequired());

        // The server must be updated
        assertTrue(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, clientContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", clientContact.getPersonalDetail().getPhotoObject().getType());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(clientImage, serverContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("JPEG", serverContact.getPersonalDetail().getPhotoObject().getType());

    }

    public void testMerge_ClientContactWithoutPhotoAndWithFirstName_and_ServerContactWithPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo serverPhoto = new Photo();
        serverPhoto.setType("GIF");
        byte[] serverImage = new byte[] {0x11, 0x23, 0x35};
        serverPhoto.setImage(serverImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);


        MergeResult mergeResult = clientContact.merge(serverContact);

        // The client must be updated (on the server there is the photo)
        assertTrue(mergeResult.isSetARequired());

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(serverImage, clientContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("GIF", clientContact.getPersonalDetail().getPhotoObject().getType());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(serverImage, serverContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("GIF", serverContact.getPersonalDetail().getPhotoObject().getType());

    }


    public void testMerge_ClientContactWithoutPhotoAndFirstName_and_ServerContactEmptyPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");
        Photo serverPhoto = new Photo(null, null, null);
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client is required
        assertFalse(mergeResult.isSetARequired());

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(clientContact.getPersonalDetail().getPhotoObject());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getType());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getImage());

    }

    public void testMerge_ClientContactWithoutPhotoAndFirstName_and_ServerContactWithoutPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client is required
        assertFalse(mergeResult.isSetARequired());

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(clientContact.getPersonalDetail().getPhotoObject());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(serverContact.getPersonalDetail().getPhotoObject());
    }

    public void testMerge_ClientContactEmptyPhotoAndFirstName_and_ServerContactWithPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo(null, null, null);
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo serverPhoto = new Photo();
        serverPhoto.setType("GIF");
        byte[] serverImage = new byte[] {0x11, 0x23, 0x35};
        serverPhoto.setImage(serverImage); // we don't use a real image
                                           // since we are interesting
                                           // just in merge
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        // The client must be updated
        assertTrue(mergeResult.isSetARequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(serverImage, clientContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("GIF", clientContact.getPersonalDetail().getPhotoObject().getType());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        ArrayAssert.assertEquals(serverImage, serverContact.getPersonalDetail().getPhotoObject().getImage());
        assertEquals("GIF", serverContact.getPersonalDetail().getPhotoObject().getType());

    }

    public void testMerge_ClientContactEmptyPhotoAndFirstName_and_ServerContactWithoutPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo(null, null, null);
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client
        assertFalse(mergeResult.isSetARequired());

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getType());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getImage());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(serverContact.getPersonalDetail().getPhotoObject());
    }

    public void testMerge_ClientContactEmptyPhotoAndFirstName_and_ServerContactEmptyPhotoAndFirstname() {

        Contact clientContact = new Contact();
        clientContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo clientPhoto = new Photo(null, null, null);
        clientContact.getPersonalDetail().setPhotoObject(clientPhoto);

        Contact serverContact = new Contact();
        serverContact.getName().getFirstName().setPropertyValue("FirstName");

        Photo serverPhoto = new Photo(null, null, null);
        serverContact.getPersonalDetail().setPhotoObject(serverPhoto);


        MergeResult mergeResult = clientContact.merge(serverContact);

        // No updates on the client
        assertFalse(mergeResult.isSetARequired());

        // No updates on the server
        assertFalse(mergeResult.isSetBRequired());

        assertEquals("FirstName", clientContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getType());
        assertNull(clientContact.getPersonalDetail().getPhotoObject().getImage());

        assertEquals("FirstName", serverContact.getName().getFirstName().getPropertyValueAsString());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getType());
        assertNull(serverContact.getPersonalDetail().getPhotoObject().getImage());
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
