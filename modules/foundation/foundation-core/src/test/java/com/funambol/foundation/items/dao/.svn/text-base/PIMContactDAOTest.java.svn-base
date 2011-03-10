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

package com.funambol.foundation.items.dao;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.ITable;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Photo;

import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.tools.IOTools;

import com.funambol.tools.database.DBHelper;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.model.ContactWrapper;
import com.funambol.foundation.util.Def;
import com.funambol.foundation.util.TestDef;

/**
 * PimContactDAO test cases. All the tests are performed with userid = user_part1
 * so that
 * @version $Id: PIMContactDAOTest.java,v 1.4.2.1 2008-04-04 14:33:39 nichele Exp $
 */
public class PIMContactDAOTest extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants

    private static final String USER_PART1 = DBHelper.USER_PART1;

    private static final String PHOTO_1 =
        "src/test/resources/data/com/funambol/foundation/items/dao/PIMContactDAO/photo_1.jpg";
    private static final String PHOTO_2 =
        "src/test/resources/data/com/funambol/foundation/items/dao/PIMContactDAO/photo_2.jpg";

    private static final String EXPECTED_PHOTO_TYPE_IMAGE =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMContactDAO/expected-photo-type-image.xml";
    private static final String EXPECTED_PHOTO_TYPE_EMPTY =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMContactDAO/expected-photo-type-empty.xml";

    private static final String JPEG_PHOTO_TYPE = "JPEG";

    static {
        try {
            DBHelper.initDataSources(CORE_SCHEMA_SOURCE, USER_SCHEMA_SOURCE, false);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private PIMContactDAO contactDAO = null;
    
    // ------------------------------------------------------------ Constructors
    public PIMContactDAOTest(String testName) throws Exception {
        super(testName);

        contactDAO = new PIMContactDAO(USER_PART1);
    }

    // -------------------------------------------------------------- Test cases

    public void testAddPhoto_1() throws  Throwable {

        Long id = Long.valueOf(1L);

        createDummyContact(id);

        ContactWrapper cw = contactDAO.getItem(String.valueOf(id));
        Timestamp lastUpdate = cw.getLastUpdate();
        assertNull(cw.getPhotoType());

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        // Waiting 5 millis. in order to be sure the last update is updated
        Thread.sleep(5);

        contactDAO.addContactPhoto(id, photo);

        cw = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(ContactWrapper.EMPTY_PHOTO != cw.getPhotoType());
        assertEquals(cw.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw.getLastUpdate().getTime() > lastUpdate.getTime());

        Photo photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Long.class},
                     new Object[] {id}
        );

        comparePhoto(photo, photoFromDB);

        cw = contactDAO.getItem(String.valueOf(id), true);
        comparePhoto(photo, cw.getContact().getPersonalDetail().getPhotoObject());

        // Waiting 5 millis. in order to be sure the last update is updated
        Thread.sleep(5);

        contactDAO.deleteContactPhoto(id);

        ContactWrapper cw2 = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(cw.getPhotoType() != ContactWrapper.EMPTY_PHOTO);
        assertEquals(cw2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw2.getLastUpdate().getTime() > cw.getLastUpdate().getTime());

        photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Long.class},
                     new Object[] {id}
        );

        assertNull(photoFromDB);
    }

    public void testGetItem_1() throws Throwable {
        String id = "87654345";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        //
        // getItem without photo
        //
        ContactWrapper cwFromDBWithoutPhoto = contactDAO.getItem(id);
        Photo photoFromDB = cwFromDBWithoutPhoto.getContact().getPersonalDetail().getPhotoObject();
        assertNull(photoFromDB);
        assertFalse(cwFromDBWithoutPhoto.getPhotoType() == ContactWrapper.EMPTY_PHOTO);

        //
        // getItem with photo
        //
        ContactWrapper cwFromDBWithPhoto = contactDAO.getItem(id, true);
        photoFromDB = cwFromDBWithPhoto.getContact().getPersonalDetail().getPhotoObject();
        assertTrue(cwFromDBWithPhoto.getPhotoType() != ContactWrapper.EMPTY_PHOTO);
        comparePhoto(photo, photoFromDB);
        assertTrue(cwFromDBWithPhoto.getPhotoType() != ContactWrapper.EMPTY_PHOTO);
    }

    public void testUpdatePhoto_1() throws  Throwable {
        Long id = Long.valueOf(1L);

        createDummyContact(id);

        ContactWrapper cw = contactDAO.getItem(String.valueOf(id));
        Timestamp lastUpdate = cw.getLastUpdate();

        assertFalse(cw.hasPhoto());
        assertEquals(cw.getStatus(), Def.PIM_STATE_NEW);

        Thread.sleep(5);

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Connection con = DBHelper.getUserConnection(USER_PART1);

        contactDAO.addContactPhoto(con, id, photo);

        cw = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(cw.hasPhoto());
        assertEquals(cw.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw.getLastUpdate().getTime() > lastUpdate.getTime());

        Photo photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Connection.class, Long.class},
                     new Object[] {con, id}
        );

        comparePhoto(photo, photoFromDB);

        Thread.sleep(50);

        byte[] image2 = IOTools.readFileBytes(PHOTO_2);
        Photo photo2 = new Photo(JPEG_PHOTO_TYPE, null, image2);

        contactDAO.updateContactPhoto(con, id, photo2);

        ContactWrapper cw2 = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(cw2.hasPhoto());
        assertEquals(cw2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw2.getLastUpdate().getTime() > cw.getLastUpdate().getTime());

        photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Connection.class, Long.class},
                     new Object[] {con, id}
        );

        comparePhoto(photo2, photoFromDB);
    }

    public void testSetPhoto_1() throws  Throwable {
        Long id = Long.valueOf(1L);

        createDummyContact(id);

        //
        // we get also the photo just to try also this case
        //
        ContactWrapper cw = contactDAO.getItem(String.valueOf(id), true);
        assertFalse(cw.hasPhoto());
        assertNull(cw.getContact().getPersonalDetail().getPhotoObject());

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Thread.sleep(5);

        Connection con = DBHelper.getUserConnection(USER_PART1);

        contactDAO.setContactPhoto(con, id, photo);

        ContactWrapper cw2 = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(cw2.hasPhoto());
        assertEquals(cw2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw2.getLastUpdate().getTime() > cw.getLastUpdate().getTime());

        Photo photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Connection.class, Long.class},
                     new Object[] {con, id}
        );

        comparePhoto(photo, photoFromDB);

        Thread.sleep(5);

        image = IOTools.readFileBytes(PHOTO_2);
        photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        contactDAO.setContactPhoto(con, id, photo);

        ContactWrapper cw3 = contactDAO.getItem(String.valueOf(id));
        //
        // Verifying that also the contact has been updated
        //
        assertTrue(cw3.hasPhoto());
        assertEquals(cw3.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cw3.getLastUpdate().getTime() > cw2.getLastUpdate().getTime());

        photoFromDB = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Connection.class, Long.class},
                     new Object[] {con, id}
        );

        comparePhoto(photo, photoFromDB);
    }

    public void testAddItem_1() throws Exception {

        String id = "1237545";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertTrue(cwFromDB.hasPhoto());
        assertTrue(isTherePhoto(id));

    }

    public void testAddItem_NoPhoto_2() throws Exception {

        String id = "123456";

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First2");
        contact.getName().getLastName().setPropertyValue("Last2");
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        isPhotoTypeNull(id);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertFalse(cwFromDB.hasPhoto());
        assertFalse(isTherePhoto(id));
    }

    public void testAddItem_EmptyPhoto_3() throws Exception {

        String id = "765452";

        Photo photo = new Photo(null, null, null);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        assertEquals(ContactWrapper.EMPTY_PHOTO, cw.getPhotoType());

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        assertFalse(cwFromDB.hasPhoto());
        assertFalse(isTherePhoto(id));

        //
        // We can not compare the ContactWrappers since the one created has an empty
        // photo that is not stored in the db (we are doing an addItem) so the cwFromDb
        // doesn't have the photo
        //
        //compareContactWrapper(cw, cwFromDB);
        assertEquals("First", cwFromDB.getContact().getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last", cwFromDB.getContact().getName().getLastName().getPropertyValueAsString());
        assertEquals(ContactWrapper.EMPTY_PHOTO, cw.getPhotoType());
    }

    public void testAddItem_WithExtendedAdr() throws Exception {

        String id = "767676";

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);
        assertNotNull(cwFromDB);
        assertEquals("Suite A", cwFromDB.getContact().getBusinessDetail()
                                        .getAddress()
                                        .getExtendedAddress()
                                        .getPropertyValueAsString());
    }

    public void testAddItem_WithNotExtendedAdr() throws Exception {

        String id = "776677";

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);
        assertNotNull(cwFromDB);
        assertEquals(null, cwFromDB.getContact().getBusinessDetail()
                                   .getAddress()
                                   .getExtendedAddress()
                                   .getPropertyValueAsString());
    }

    public void testUpdateItem_WithExtendedAdr() throws Exception {

        String id = "67676";

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite B"));
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);
        assertNotNull(cwFromDB);
        assertEquals("Suite B", cwFromDB.getContact().getBusinessDetail()
                                        .getAddress()
                                        .getExtendedAddress()
                                        .getPropertyValueAsString());

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First-2");
        contact.getName().getLastName().setPropertyValue("Last-2");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite B"));
        cw = new ContactWrapper(id, USER_PART1, contact);

        contactDAO.updateItem(cw);

        cwFromDB = contactDAO.getItem(id, true);
        assertNotNull(cwFromDB);
        assertEquals("Suite B", cwFromDB.getContact().getBusinessDetail()
                                        .getAddress()
                                        .getExtendedAddress()
                                        .getPropertyValueAsString());
    }

    /**
     * 1. Adds a contact with a photo
     * 2. Updates it with a contact without photo
     * Results: the db should contain still the photo
     * @throws Throwable
     */
    public void testUpdateItem_2() throws Throwable {

        String id = "7654";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertTrue(cwFromDB.hasPhoto());

        Photo p1 = (Photo)PrivateAccessor.invoke(
                     contactDAO,
                     "getPhoto",
                     new Class[] {Long.class},
                     new Object[] {Long.valueOf(id)}
        );
        comparePhoto(photo, p1);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First-2");
        contact.getName().getLastName().setPropertyValue("Last-2");
        cw = new ContactWrapper(id, USER_PART1, contact);

        contactDAO.updateItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        cwFromDB = contactDAO.getItem(id, true);

        assertEquals("First-2", cwFromDB.getContact().getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last-2", cwFromDB.getContact().getName().getLastName().getPropertyValueAsString());
        comparePhoto(photo, cwFromDB.getContact().getPersonalDetail().getPhotoObject());

        assertTrue(cwFromDB.hasPhoto());
        assertTrue(isTherePhoto(id));

    }

    /**
     * Updates a contact with a photo with an empty photo in order to verify the
     * previous one is deleted
     * @throws Throwable
     */
    public void testUpdateItem_EmptyPhoto_1() throws Throwable {

        String id = "98765456";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        assertEquals(cw.getPhotoType(), ContactWrapper.PHOTO_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertTrue(cwFromDB.hasPhoto());
        assertTrue(isTherePhoto(id));

        Thread.sleep(5);
        //
        // Updating the contact with an empty photo
        //
        Photo emptyPhoto = new Photo(null, null, null);
        Contact contact2 = new Contact();
        contact2.getName().getFirstName().setPropertyValue("First");
        contact2.getName().getLastName().setPropertyValue("Last");
        contact2.getPersonalDetail().setPhotoObject(emptyPhoto);
        ContactWrapper cw2 = new ContactWrapper(id, USER_PART1, contact2);
        contactDAO.updateItem(cw2);

        assertEquals(cw2.getPhotoType(), ContactWrapper.EMPTY_PHOTO);

        ContactWrapper cwFromDB2 = contactDAO.getItem(id, true);

        //
        // We can not compare the ContactWrappers since the one created has an empty
        // photo that is not stored in the db (we are doing an updateItem) so the cwFromDB2
        // doesn't have the photo
        //
        // compareContactWrapper(cw2, cwFromDB2);
        //
        assertEquals("First", cwFromDB.getContact().getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last", cwFromDB.getContact().getName().getLastName().getPropertyValueAsString());
        assertNull(cwFromDB2.getContact().getPersonalDetail().getPhotoObject().getImage());
        assertNull(cwFromDB2.getContact().getPersonalDetail().getPhotoObject().getUrl());

        assertEquals(cwFromDB2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cwFromDB2.getLastUpdate().getTime() > cwFromDB.getLastUpdate().getTime());
        assertFalse(isTherePhoto(id));
        assertFalse(cwFromDB2.hasPhoto());
    }

    /**
     * Updates a contact with no photo with an empty photo in order to verify the
     * contact is still without photo
     * @throws Throwable 
     */
    public void testUpdateItem_EmptyPhoto_2() throws Throwable {

        String id = "9876435456";

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        isPhotoTypeNull(id);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertFalse(cwFromDB.hasPhoto());
        assertFalse(isTherePhoto(id));
        assertNull(cw.getPhotoType());

        Thread.sleep(50); // sometime waiting less than 50. the test fails, at least
                          // in netbeans
        //
        // Updating the contact with an empty photo
        //
        Photo emptyPhoto = new Photo(null, null, null);
        Contact contact2 = new Contact();
        contact2.getName().getFirstName().setPropertyValue("First");
        contact2.getName().getLastName().setPropertyValue("Last");
        contact2.getPersonalDetail().setPhotoObject(emptyPhoto);
        ContactWrapper cw2 = new ContactWrapper(id, USER_PART1, contact2);
        contactDAO.updateItem(cw2);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_EMPTY);

        ContactWrapper cwFromDB2 = contactDAO.getItem(id, true);

        //
        // We can not compare the ContactWrappers since the one created has an empty
        // photo that is not stored in the db (we are doing an updateItem) so the cwFromDB2
        // doesn't have the photo
        //
        // compareContactWrapper(cw2, cwFromDB2);
        //
        assertEquals("First", cwFromDB.getContact().getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last", cwFromDB.getContact().getName().getLastName().getPropertyValueAsString());
        assertNull(cwFromDB2.getContact().getPersonalDetail().getPhotoObject().getImage());
        assertNull(cwFromDB2.getContact().getPersonalDetail().getPhotoObject().getUrl());

        assertEquals(cwFromDB2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cwFromDB2.getLastUpdate().getTime() > cwFromDB.getLastUpdate().getTime());
        assertFalse(isTherePhoto(id));
        assertFalse(cwFromDB2.hasPhoto());
    }

    /**
     * Updates a contact with a photo with no photo in order to verify the
     * original photo is still in the db
     * @throws Throwable 
     */
    public void testUpdateItem_NoPhoto_1() throws Throwable {

        String id = "987";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);
        assertTrue(cwFromDB.hasPhoto());
        assertTrue(isTherePhoto(id));

        Thread.sleep(5);
        //
        // Updating the contact with an empty photo
        //
        Contact contact2 = new Contact();
        contact2.getName().getFirstName().setPropertyValue("First");
        contact2.getName().getLastName().setPropertyValue("Last");

        ContactWrapper cw2 = new ContactWrapper(id, USER_PART1, contact2);
        contactDAO.updateItem(cw2);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB2 = contactDAO.getItem(id, true);

        //
        // We can not compare the ContactWrappers since the one created has not
        // photo so the original one is still there (we are doing an updateItem)
        //
        // compareContactWrapper(cw2, cwFromDB2);
        //
        assertEquals("First", cwFromDB.getContact().getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last", cwFromDB.getContact().getName().getLastName().getPropertyValueAsString());
        comparePhoto(photo, cwFromDB2.getContact().getPersonalDetail().getPhotoObject());

        assertEquals(cwFromDB2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cwFromDB2.getLastUpdate().getTime() > cwFromDB.getLastUpdate().getTime());
        assertTrue(isTherePhoto(id));
        assertTrue(cwFromDB2.hasPhoto());
    }


    public void testRemoveItem_1() throws Exception {
        String id = "12345678";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id, true);

        compareContactWrapper(cw, cwFromDB);

        contactDAO.removeItem(new ContactWrapper(id, USER_PART1, new Contact()));

        cwFromDB = contactDAO.getItem(id);

        assertEquals(cwFromDB.getStatus(), SyncItemState.DELETED);
    }

    public void testDeletePhoto_1() throws Throwable {
        String id = "45435";

        byte[] image = IOTools.readFileBytes(PHOTO_1);
        Photo photo = new Photo(JPEG_PHOTO_TYPE, null, image);

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getPersonalDetail().setPhotoObject(photo);
        ContactWrapper cw = new ContactWrapper(id, USER_PART1, contact);
        contactDAO.addItem(cw);

        comparePhotoType(id, EXPECTED_PHOTO_TYPE_IMAGE);

        ContactWrapper cwFromDB = contactDAO.getItem(id);
        assertTrue(cwFromDB.hasPhoto());
        assertTrue(isTherePhoto(id));
        Thread.sleep(5);
        contactDAO.deleteContactPhoto(Long.valueOf(id));

        ContactWrapper cwFromDB2 = contactDAO.getItem(id, true);
        assertFalse(cwFromDB2.hasPhoto());
        assertNull(cwFromDB2.getContact().getPersonalDetail().getPhotoObject());
        assertFalse(isTherePhoto(id));
        assertEquals(cwFromDB2.getStatus(), Def.PIM_STATE_UPDATED);
        assertTrue(cwFromDB2.getLastUpdate().getTime() > cwFromDB.getLastUpdate().getTime());
    }

    public void testGetTwinItems() throws Throwable {
        Contact contact;
        Phone phone;
        Email email;

        fillContactTable();

        //
        // The stored contact with this company has set also first name, last
        // name and email address
        //
        contact = new Contact();
        contact.getBusinessDetail().getCompany().setPropertyValue("Morton's Steak House");
        List<String> twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        contact = new Contact();
        contact.getBusinessDetail().getCompany().setPropertyValue("Rhymes's Restaurant");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1006 not found", "1006", twins.get(0));

        contact = new Contact();
        contact.getName().setFirstName(new Property(null));
        contact.getName().setLastName(new Property(null));
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Rhymes's Restaurant");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1006 not found", "1006", twins.get(0));

        contact = new Contact();
        contact.getName().setFirstName(new Property(""));
        contact.getName().setLastName(new Property(""));
        email = new Email("");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Rhymes's Restaurant");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1006 not found", "1006", twins.get(0));

        //
        // The stored contact with this company has the first name empty and
        // the email address not empty.
        // A twin should not be found if client sends a contact with first name
        // set.
        //
        contact = new Contact();
        contact.getName().setFirstName(new Property("Eddy"));
        contact.getBusinessDetail().getCompany().setPropertyValue("Ritherdon's SW House");
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        contact = new Contact();
        email = new Email("ritherdon@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Ritherdon's SW House");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1007 not found)", "1007", twins.get(0));

        //
        // The stored contact (1007) has another email address
        //
        contact = new Contact();
        email = new Email("sw.house@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Ritherdon's SW House");
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Reuben");
        contact.getBusinessDetail().getPhones().add(0, new Phone("2-821-234-5454"));
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1008 not found", "1008", twins.get(0));

        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("Wills");
        contact.getBusinessDetail().getCompany().setPropertyValue("Wills Company");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1009 not found", "1009", twins.get(0));

        contact = new Contact();
        email = new Email("weijers.company@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1010 not found", "1010", twins.get(0));

        //
        // Found two twins because there are two contacts with the same first
        // name and last name, but with different phone numbers.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Dustin");
        contact.getName().getLastName().setPropertyValue("Elmore");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 2, twins.size());
        assertEquals("Twin 1011 not found", "1011", twins.get(0));
        assertEquals("Twin 1012 not found", "1012", twins.get(1));

        //
        // A contact with first name 'Willy' and last name 'Denmore'
        // is not present in twins
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Willy");
        contact.getName().getLastName().setPropertyValue("Denmore");
        contact.getBusinessDetail().getCompany().setPropertyValue("Wills Company");
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //NEW REQUESTS
        //
        // A contact with first name 'Lucy' and last name 'Parker' is present
        // in the database, and has same email address, but in a different
        // position. New algorithm should recognise it as a twin.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Lucy");
        contact.getName().getLastName().setPropertyValue("Parker");
        email = new Email("lucy@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1002 not found", "1002", twins.get(0));

        //
        // A contact with first name 'Denzel' and last name 'Malfoil' is present
        // in the database, and has same email home address and "other" address.
        // home address of the following contact is in a different place than
        // the db, and this place in the db is free, so twin must be detected
        // After the first check, i add "other" address in the
        // following contanct is in a position already took by the home address
        // of db contact. At this point, no twin should be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Denzel");
        contact.getName().getLastName().setPropertyValue("Malfoil");
        email = new Email("malfoil@home.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10001 not found", "10001", twins.get(0));
        //add a common email address but in a position where the db stores a
        //different email address
        email = new Email("malfoil@other.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with first name 'TomBans' is present in the database,
        // but it has no last name.
        // A twin should not be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("TomBans");
        contact.getName().getLastName().setPropertyValue("Mortimers");
        contact.getBusinessDetail().getPhones().add(0, new Phone("2-821-234-5454"));
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with first name 'Dustin' and last name empty is not
        // present in the db even if the phone number is equals.
        // A twin should not be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Dustin");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-801-234-3434"));
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with first name 'Denzel' and last name 'Malfoil' is present
        // in the database, and has same email home and addresses, but swapped.
        // So a twin should not be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Denzel");
        contact.getName().getLastName().setPropertyValue("Malfoil");
        email = new Email("malfoil@home.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("malfoil@other.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with first name 'Marienne' and last name 'Calaflou' is
        // present in the database, and has same business email and phone number,
        // but different home phone.
        // So a twin should not be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Marienne");
        contact.getName().getLastName().setPropertyValue("Calaflou");
        email = new Email("calaflou-inc@gmail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("232-435234");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().getPhones().add(phone);
        phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with first name 'Marienne' and last name 'Calaflou' is
        // present in the database, and has same phone number.
        // So a twin should be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Marienne");
        contact.getName().getLastName().setPropertyValue("Calaflou");
        phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10003 not found", "10003", twins.get(0));

        //
        // A contact with first name 'TomBans' and no last name is present in
        // the database, and has same email address.
        // So a twin should be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("TomBans");
        email = new Email("calaflou-inc@gmail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("232-435234");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().getPhones().add(phone);
        phone = new Phone("2-821-234-5454");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10002 not found", "10002", twins.get(0));

        //
        // A contact with first name 'Sam' and no last name is present in
        // the database, and has same phone number.
        // So a twin should be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Sam");
        email = new Email("sam@yahoo.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("232-435234");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().getPhones().add(phone);
        phone = new Phone("2-821-234-5454");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10004 not found", "10004", twins.get(0));

        //
        // A contact with the same first name and last name (and no more info)
        // is present on db. A twin should be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Steven");
        contact.getName().getLastName().setPropertyValue("Jackson");
        phone = new Phone("232-435234");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().getPhones().add(phone);
        phone = new Phone("2-821-234-5454");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1003 not found", "1003", twins.get(0));

        //
        // A contact with first name 'Marienne' and last name 'Calaflou' is
        // present in the database, and has same phone number but swapped.
        // So a twin should not be found
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Marienne");
        contact.getName().getLastName().setPropertyValue("Calaflou");
        phone = new Phone("987-654321");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        phone = new Phone("012-3456789");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 0, twins.size());

        //
        // A contact with same last name and email1address is present in the db.
        // A twin should be found.
        //
        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("Parker");
        email = new Email("info@company.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10005 not found", "10005", twins.get(0));

        //
        // A contact with a different phone number but with all the other info
        // equal is present on the db.
        // A twin should not be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Bob");
        contact.getBusinessDetail().getCompany().setPropertyValue("Bob's SW House");
        email = new Email("bob@gmail.com");
        email.setEmailType("Email1Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("111-54545454");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 0, twins.size());

        //
        // A contact with a different email address but with all the other info
        // equal is present on the db.
        // A twin should not be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Bob");
        contact.getName().getLastName().setPropertyValue("Brown");
        contact.getBusinessDetail().getCompany().setPropertyValue("Bob's SW House");
        email = new Email("bob.brown@gmail.com");
        email.setEmailType("Email1Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("111-54545454");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 0, twins.size());

        //
        // A contact with first name 'John' and last name 'Smith', but only an
        // Email1Address set to a different value than the ones set in the
        // client contact. 
        // Since there is no at least an email in common, a twin should not
        // be found.
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("John");
        contact.getName().getLastName().setPropertyValue("Smith");
        email = new Email("malfoil@home.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("malfoil@other.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

                // twin with very long first name
        contact = new Contact();
        contact.getName().setFirstName(new Property("Jerome with very long long first name. it must be longer than 64 chars as this."));
        contact.getName().setLastName(new Property("Mullins"));
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Wrong twin number", twins.size() == 1);
        assertEquals("Twin 100021 not found", "100021", (String)twins.get(0));

        // twin with very long last name
        contact = new Contact();
        contact.getName().setFirstName(new Property("Jerome"));
        contact.getName().setLastName(new Property("Mullins with very long long first name. it must be longer than 64 chars as this."));
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Wrong twin number", twins.size() == 1);
        assertEquals("Twin 100022 not found", "100022", (String)twins.get(0));

        // twin with very long email address
        contact = new Contact();
        contact.getName().setFirstName(new Property("JeromeBis"));
        contact.getName().setLastName(new Property("MullinsBis"));
        email = new Email("email address with very long long first name. it must be longer than 255 chars as this.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Wrong twin number", twins.size() == 1);
        assertEquals("Twin 100023 not found", "100023", (String)twins.get(0));

        // twin with very long email address but no name
        contact = new Contact();
        email = new Email("email address with very long long first name. it must be longer than 255 chars as this.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Wrong twin number", twins.size() == 1);
        assertEquals("Twin 100024 not found", "100024", (String)twins.get(0));

    }

    public void testGetTwinItems_WithDisplayName() throws Throwable {

        fillContactTableWithDisplayName();

        //
        // A contact with display name 'Tony Edward'
        // is present in twins
        //
        Contact contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Tony Edward");
        List<String> twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1014 not found", "1014", twins.get(0));

        //
        // A contact with display name 'Lucy Parker'
        // and email generic 'lp@gmail.com'
        // is present in twins
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Lucy Parker");
        Email email = new Email("lp@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1015 not found", "1015", twins.get(0));

        //
        // A contact with first name 'Tony', last name 'Eng'
        // and email generic 'te@gmail.com'
        // is present in twins
        //
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Tony");
        contact.getName().getLastName().setPropertyValue("Eng");
        contact.getName().getDisplayName().setPropertyValue("Tony Eng");
        email = new Email("te@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1016 not found", "1016", twins.get(0));

        //
        // A contact with email generic 'me@gmail.com'
        // and display name 'Mark Edward'
        // is not present in twins
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Mark Edward");
        email = new Email("me@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with email generic 'mg@gmail.com'
        // and company name 'ElextroX'
        // is not present in twins
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Michele Gutemberg");
        email = new Email("mg@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("ElextroX");
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // A contact with email generic 'aa@gmail.com'
        // and company name 'NewSync'
        // is present in twins
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Antony Andry");
        email = new Email("aa@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("NewSync");
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1019 not found", "1019", twins.get(0));

        //
        // A contact with the same display name and phone number is present in
        // the db. A twin should be found.
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("Tony Edward");
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-445566"));
        email = new Email("aa@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1014 not found", "1014", twins.get(0));

        //
        // A contact with same display name and email address is present in the
        // db (no more info).
        // A twin should be found.
        //
        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("John");
        email = new Email("home@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        Phone phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 10020 not found", "10020", twins.get(0));


    }

    /**
     * Tests the search of twins when the given contact has first and last name,
     * company and email address empty, but it has not empty the email home
     * address and/or the email work address.
     * @throws Throwable 
     */
    public void testGetTwinItems_UnnamedContact() throws Throwable {
        Contact contact;
        Phone phone;
        Email email;

        fillContactTableWithUnnamedContact();

        // Contact with email address home and email address work set
        contact = new Contact();
        email = new Email("homeaddress@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("workaddress@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);

        List<String> twins = contactDAO.getTwinItems(contact);
        assertEquals("Not all expected twins were found", 3, twins.size());
        assertEquals("Twin 1021 not found", "1021", twins.get(0));
        assertEquals("Twin 1022 not found", "1022", twins.get(1));
        assertEquals("Twin 1023 not found", "1023", twins.get(2));

        // Contact with email address home set
        contact = new Contact();
        email = new Email("homeaddress@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);

        twins = contactDAO.getTwinItems(contact);
        assertTrue("Not all expected twins were found", twins.size() == 3);
        assertEquals("Twin 1021 not found", "1021", twins.get(0));
        assertEquals("Twin 1023 not found", "1023", twins.get(1));
        assertEquals("Twin 1024 not found", "1024", twins.get(2));

        // Contact with email address work set
        contact = new Contact();
        email = new Email("workaddress@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);

        twins = contactDAO.getTwinItems(contact);
        assertTrue("Not all expected twins were found", twins.size() == 3);
        assertEquals("Twin 1022 not found", "1022", twins.get(0));
        assertEquals("Twin 1023 not found", "1023", twins.get(1));
        assertEquals("Twin 1025 not found", "1025", twins.get(2));

        // Contact with email address work set and not present in twins
        contact = new Contact();
        email = new Email("workaddress_bis@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Found a not expected twin", twins.isEmpty());

        //
        // NEW TWIN SEARCH ALGORITHM
        //

        // a contact with same phone number in same position is present in the
        // db
        contact = new Contact();
        phone = new Phone("010-15768990");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1013 not found", "1013", twins.get(0));

        // An unnamed contact with same phone number, but in different position
        // is present on the db. So a twin must be identified
        contact = new Contact();
        phone = new Phone("010-15768990");
        phone.setPhoneType("CompanyMainTelephoneNumber");
        contact.getBusinessDetail().addPhone(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1013 not found", "1013", twins.get(0));

        // An unnamed contact with same Email1Address is present in the db, so
        // a twin must be found
        contact = new Contact();
        email = new Email("info@myhome.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1027 not found", "1027", twins.get(0));
        // adding a different Email3Address than the one already present in the
        // db, will cause twin search to return no twin
        email = new Email("businessNotEqual@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Unexpected twin found!", twins.isEmpty());

        // An unnamed contact with same email address, but in different position
        // is present on the db. So a twin must be identified
        contact = new Contact();
        email = new Email("tony@pavia.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1028 not found", "1028", twins.get(0));
        // adding a new telephone number
        phone = new Phone("012-312312315");
        phone.setPhoneType("CompanyMainTelephoneNumber");
        contact.getBusinessDetail().addPhone(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1028 not found", "1028", twins.get(0));
        // adding a new phone number in the same position of an already
        // existing phone in the db
        phone = new Phone("011-00000000");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        twins = contactDAO.getTwinItems(contact);
        assertTrue("Unexpected twin found!", twins.isEmpty());

        // A contact with same home phone number exists in the db, and this
        // contact has an empty email address in the same place where server has
        // an email. No problem, twin is detected.
        contact = new Contact();
        phone = new Phone("013-5938232");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        //empty email address
        email = new Email("");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1028 not found", "1028", twins.get(0));

        //
        // A contact with the phone number work equals to the given phone
        // number home.
        // A twin should be found.
        //
        contact = new Contact();
        email = new Email("email3.work@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("010-15768123");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        twins = contactDAO.getTwinItems(contact);
        assertEquals("Wrong twin numbers", 1, twins.size());
        assertEquals("Twin 1029 not found", "1029", twins.get(0));
    }

    public void testGetTwinItems_NotValidContact() throws DAOException {
        List twins = contactDAO.getTwinItems(null);
        assertNotNull(twins);
        assertEquals(0, twins.size());

        Contact contact = new Contact();
        twins = contactDAO.getTwinItems(contact);
        assertNotNull(twins);
        assertEquals(0, twins.size());

        contact = new Contact();
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        twins = contactDAO.getTwinItems(contact);
        assertNotNull(twins);
        assertEquals(0, twins.size());
    }

    public void testIsTwinSearchAppliableOn_NullContact() {
        assertFalse(contactDAO.isTwinSearchAppliableOn(null));
    }

    public void testIsTwinSearchAppliableOn_EmptyContact() {
        Contact contact = new Contact();
        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));
    }

    public void testIsTwinSearchAppliableOn_ValidContact() {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("Last");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("DisplayName");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getBusinessDetail().getCompany().setPropertyValue("CompanyName");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        Email email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email2Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("test@tests.com");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));
    }


    public void testIsTwinSearchAppliableOn_ValidContact_SomeFields() {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("First");
        contact.getName().getLastName().setPropertyValue("Last");
        Email email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("test@tests.com");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("DisplayName");
        contact.getBusinessDetail().getCompany().setPropertyValue("CompanyName");
        email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();

        email.setEmailType("Email2Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("test@tests.com");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));
    }

    public void testIsTwinSearchAppliableOn_NotValidContact() {
        Contact contact = new Contact();
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        Email email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("");
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email.setEmailType("Email2Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email1Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email2Address");
        email.setPropertyValue("");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("");
        contact.getBusinessDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertFalse(contactDAO.isTwinSearchAppliableOn(contact));

        contact = new Contact();
        email = new Email();
        email.setEmailType("Email3Address");
        email.setPropertyValue("test@tests.com");
        contact.getPersonalDetail().addEmail(email);
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getBusinessDetail().getAddress().setExtendedAddress(new Property("Suite A"));
        contact.getPersonalDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getPersonalDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getPersonalDetail().getAddress().setCountry(new Property("CA"));
        contact.getPersonalDetail().getAddress().setPostalCode(new Property("90124"));
        contact.getPersonalDetail().getAddress().setExtendedAddress(new Property("Suite A"));

        assertTrue(contactDAO.isTwinSearchAppliableOn(contact));

    }

    public void testAggregateEmailAddressesRemovingDuplicates() throws Throwable {
        List<Email> contactEmails = new ArrayList<Email>();
        Map<Integer, String> twinEmails = new HashMap<Integer, String>();

        Email email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);
        email = new Email("email2@somewhere.com");
        email.setPropertyType("Email2Address");
        contactEmails.add(email);
        email = new Email("email3@email.com");
        email.setPropertyType("Email3Address");
        contactEmails.add(email);

        twinEmails.put(1, "testes@funambol.com");
        twinEmails.put(2, "email3@email.com");
        twinEmails.put(3, "EMail2@SOMEWHERE.coM");
        twinEmails.put(4, "news@bbc.com");

        List<String> result = (List<String>)PrivateAccessor.invoke(
            contactDAO,
            "aggregateEmailAddressesRemovingDuplicates",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );

        assertEquals("Wrong number of items", 5, result.size());
        assertTrue("Wrong item", result.contains("email1@email.com"));
        assertTrue("Wrong item", result.contains("email2@somewhere.com"));
        assertTrue("Wrong item", result.contains("email3@email.com"));
        assertTrue("Wrong item", result.contains("testes@funambol.com"));
        assertTrue("Wrong item", result.contains("news@bbc.com"));
    }

    public void testGetPropertiesWithValidValues() throws Throwable {
        int[] itemValues;
        Map<Integer, String> result;

        Map<Integer, String> twinValues = new HashMap<Integer, String>();
        twinValues.put(1, "Value1");
        twinValues.put(2, "Value2");
        twinValues.put(3, "Value3");
        twinValues.put(10, "Value10");
        twinValues.put(11, "Value11");
        twinValues.put(12, "");

        itemValues = new int[] {1, 2, 5};
        result  = (Map<Integer, String>)PrivateAccessor.invoke(
                     contactDAO,
                     "getPropertiesWithValidValues",
                     new Class[] {Map.class, int[].class},
                     new Object[] {twinValues, itemValues}
        );
        assertEquals("Wrong number of items", 2, result.size());
        assertEquals("Wrong item", result.get(1), ("Value1"));
        assertEquals("Wrong item", result.get(2), ("Value2"));

        itemValues = new int[] {6};
        result  = (Map<Integer, String>)PrivateAccessor.invoke(
                     contactDAO,
                     "getPropertiesWithValidValues",
                     new Class[] {Map.class, int[].class},
                     new Object[] {twinValues, itemValues}
        );
        assertTrue("Wrong number of items", result.isEmpty());

        itemValues = new int[] {1, 2, 11};
        result  = (Map<Integer, String>)PrivateAccessor.invoke(
                     contactDAO,
                     "getPropertiesWithValidValues",
                     new Class[] {Map.class, int[].class},
                     new Object[] {twinValues, itemValues}
        );
        assertEquals("Wrong number of items", 3, result.size());
        assertEquals("Wrong item", result.get(1), ("Value1"));
        assertEquals("Wrong item", result.get(2), ("Value2"));
        assertEquals("Wrong item", result.get(11), ("Value11"));

        itemValues = new int[] {1, 12};
        result  = (Map<Integer, String>)PrivateAccessor.invoke(
                     contactDAO,
                     "getPropertiesWithValidValues",
                     new Class[] {Map.class, int[].class},
                     new Object[] {twinValues, itemValues}
        );
        assertEquals("Wrong number of items", 1, result.size());
        assertEquals("Wrong item", result.get(1), ("Value1"));
    }

    public void testGetContactPropertiesRemovingNullOrEmptyValues() throws Throwable {
        Contact contact;
        Email email;
        Phone phone;
        List[] parameters;

        contact = new Contact();
        phone = new Phone("013-5938232");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        phone = new Phone("023-134818341");
        phone.setPhoneType("MobileTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        phone = new Phone("");
        phone.setPhoneType("OtherTelephoneNumber");
        contact.getBusinessDetail().addPhone(phone);
        email = new Email("email1@pavia.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().addEmail(email);
        email = new Email("");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().addEmail(email);
        email = new Email("email3@pavia.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().addEmail(email);

        //test with list of emails
        parameters = new List[] {
            contact.getPersonalDetail().getEmails(),
            contact.getBusinessDetail().getEmails() };
        List<Email> emailsResult = (List<Email>)PrivateAccessor.invoke(
                     contactDAO,
                     "getContactPropertiesRemovingNullOrEmptyValues",
                     new Class[] {List[].class},
                     new Object[] {parameters}
        );
        assertEquals("Wrong number of items", 2, emailsResult.size());
        assertEquals("Wrong item", emailsResult.get(0).getPropertyValueAsString(), ("email1@pavia.com"));
        assertEquals("Wrong item", emailsResult.get(1).getPropertyValueAsString(), ("email3@pavia.com"));

        //test with list of phone numbers
        parameters = new List[] {
            contact.getPersonalDetail().getPhones(),
            contact.getBusinessDetail().getPhones() };
        List<Phone> phonesResult = (List<Phone>)PrivateAccessor.invoke(
                     contactDAO,
                     "getContactPropertiesRemovingNullOrEmptyValues",
                     new Class[] {List[].class},
                     new Object[] {parameters}
        );
        assertEquals("Wrong number of items", 2, phonesResult.size());
        assertEquals("Wrong item", phonesResult.get(0).getPropertyValueAsString(), ("013-5938232"));
        assertEquals("Wrong item", phonesResult.get(1).getPropertyValueAsString(), ("023-134818341"));

    }

    public void testHaveContactAndTwinEmailsInCommon() throws Throwable {
        List<Email> contactEmails = new ArrayList<Email>();
        Map<Integer, String> twinEmails = new HashMap<Integer, String>();

        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinEmailsInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertFalse(result);
        
        // number of emails > contact + twin emails
        Email email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);
        email = new Email("email2@somewhere.com");
        email.setPropertyType("Email2Address");
        contactEmails.add(email);
        email = new Email("email3@email.com");
        email.setPropertyType("Email3Address");
        contactEmails.add(email);
        
        twinEmails.put(1, "testes@funambol.com");
        twinEmails.put(2, "email3@email.com");
        twinEmails.put(3, "EMail2@SOMEWHERE.coM");
        twinEmails.put(4, "news@bbc.com");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinEmailsInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertFalse(result);

        // number of emails < contact + twin emails && == 3
        contactEmails.clear();
        twinEmails.clear();
        
        email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);
        email = new Email("email2@somewhere.com");
        email.setPropertyType("Email2Address");
        contactEmails.add(email);
        email = new Email("email3@email.com");
        email.setPropertyType("Email3Address");
        contactEmails.add(email);

        twinEmails.put(1, "email3@email.com");
        twinEmails.put(2, "EMail2@SOMEWHERE.coM");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinEmailsInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertTrue(result);

        // number of emails < contact + twin emails && > 3
        contactEmails.clear();
        twinEmails.clear();

        email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);
        email = new Email("email2@somewhere.com");
        email.setPropertyType("Email2Address");
        contactEmails.add(email);
        email = new Email("email3@email.com");
        email.setPropertyType("Email3Address");
        contactEmails.add(email);

        twinEmails.put(1, "testes@funambol.com");
        twinEmails.put(2, "email3@email.com");
        twinEmails.put(3, "EMail2@SOMEWHERE.coM");
        twinEmails.put(4, "news@bbc.com");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinEmailsInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertFalse(result);
 
    }

    public void testHaveContactAndTwinPhoneNumbersInCommon() throws Throwable {
        List<Phone> contactPhones = new ArrayList<Phone>();
        Map<Integer, String> twinPhones = new HashMap<Integer, String>();

        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinPhoneNumbersInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertFalse(result);

        // no common phones
        Phone phone = new Phone("013-5938232");
        phone.setPhoneType("HomeTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("023-134818341");
        phone.setPhoneType("MobileTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("023-985423465");
        phone.setPhoneType("BusinessTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("");
        phone.setPhoneType("OtherTelephoneNumber");
        contactPhones.add(phone);

        twinPhones.put(1, "123-456456456");
        twinPhones.put(2, "123-987789987");
        twinPhones.put(3, "123-753357159");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinPhoneNumbersInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertFalse(result);

        // some common phones
        twinPhones.put(4, "023-134818341");
        twinPhones.put(5, "023-985423465");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinPhoneNumbersInCommon",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertTrue(result);
    }

    public void testHaveContactAndTwinSameEmailsInSamePosition() throws Throwable {
        List<Email> contactEmails = new ArrayList<Email>();
        Map<Integer, String> twinEmails = new HashMap<Integer, String>();

        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSameEmailsInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertTrue(result);

        // contact and twin have same emails in same position
        Email email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);
        email = new Email("email2@somewhere.com");
        email.setPropertyType("Email2Address");
        contactEmails.add(email);
        email = new Email("email3@email.com");
        email.setPropertyType("Email3Address");
        contactEmails.add(email);
        email = new Email("email4@email.com");
        contactEmails.add(email);

        twinEmails.put(23, "email3@email.com");
        twinEmails.put(16, "EMail2@SOMEWHERE.coM");
        twinEmails.put(5, "news@bbc.com");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSameEmailsInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertTrue(result);

        // twin has a different email1
        email = new Email("email1@email.com");
        email.setPropertyType("Email1Address");
        contactEmails.add(email);

        twinEmails.put(4, "another@email1.com");

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSameEmailsInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertFalse(result);

        // twin does not have emails
        twinEmails.clear();
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSameEmailsInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactEmails, twinEmails}
        );
        assertTrue(result);
    }

    public void testHaveContactAndTwinSamePhonesInSamePosition() throws Throwable {
        List<Phone> contactPhones = new ArrayList<Phone>();
        Map<Integer, String> twinPhones = new HashMap<Integer, String>();

        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSamePhonesInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertTrue(result);

        // twin does not have phones
        Phone phone = new Phone("013-5938232");
        phone.setPhoneType("HomeTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("023-134818341");
        phone.setPhoneType("MobileTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("023-985423465");
        phone.setPhoneType("BusinessTelephoneNumber");
        contactPhones.add(phone);
        phone = new Phone("");
        phone.setPhoneType("OtherTelephoneNumber");
        contactPhones.add(phone);

        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSamePhonesInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertTrue(result);

        // contact and twin have same phones in the same positions
        twinPhones.put(3, "023-134818341");
        twinPhones.put(1, "013-5938232");
        twinPhones.put(30, "");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSamePhonesInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertTrue(result);

        // contact and twin don't have same phones in the same positions
        twinPhones.put(10, "011-415441465");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "haveContactAndTwinSamePhonesInSamePosition",
            new Class[] {List.class, Map.class},
            new Object[] {contactPhones, twinPhones}
        );
        assertFalse(result);

    }

    public void testHasAtLeastOneNonEmptyProperty() throws Throwable {

        List[] properties = null;

        // the list is null
        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertFalse(result);

        // the list is empty
        properties = new List[]{};
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertFalse(result);

        // the list contains a list null
        properties = new List[]{null};
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertFalse(result);

        // the list contains a list of Phone objects
        List<Phone> phones = new ArrayList<Phone>();
        phones.add(new Phone("155-57458745"));
        phones.add(new Phone("155-65465465"));
        properties[0] = phones;
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertTrue(result);

        // the list contains a list of Email objects
        List<Email> emails = new ArrayList<Email>();
        emails.add(null);
        emails.add(new Email("email1@fun.com"));
        emails.add(new Email("null"));
        emails.add(new Email(""));

        properties = new List[]{emails};
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertTrue(result);

        // the list contains a couple of list of Phone and Email objects
        properties = new List[]{emails, phones};
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            "hasAtLeastOneNonEmptyProperty",
            new Class[] {List[].class},
            new Object[] {properties}
        );
        assertTrue(result);
    }

    public void testContactOrTwinHasNoFieldsWhileTheOtherHas() throws Throwable {

        String methodName = "contactOrTwinHasNoFieldsWhileTheOtherHas";

        List<Email> contactEmails = null;
        List<Phone> contactPhones = null;
        Map<Integer, String> twinEmails = null;
        Map<Integer, String> twinPhones = null;

        boolean result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertFalse(result);

        // contact contains emails, twin no
        contactEmails = new ArrayList<Email>();
        contactEmails.add(new Email("email1@fun.com"));
        contactEmails.add(new Email("email2@fun.com"));
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertTrue(result);
        
        // twin contains email, contact no
        contactEmails.clear();
        twinEmails = new HashMap<Integer, String>();
        twinEmails.put(4, "email4@email.com");
        twinEmails.put(16, "email16@email.com");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertTrue(result);

        // contact and twin contain emails
        contactEmails = new ArrayList<Email>();
        contactEmails.add(new Email("email1@fun.com"));
        contactEmails.add(new Email("email2@fun.com"));
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertFalse(result);

        // contact contains phones, twin no
        contactEmails.clear();
        twinEmails.clear();
        
        contactPhones = new ArrayList<Phone>();
        contactPhones.add(new Phone("123-456456456"));
        contactPhones.add(new Phone("789-456456466"));
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertTrue(result);

        // twin contains phones, contact no
        contactPhones.clear();
        twinPhones = new HashMap<Integer, String>();
        twinPhones.put(1, "753-753753753");
        twinPhones.put(10, "951-95195195");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertTrue(result);

        // contact and twin contain phones
        contactPhones = new ArrayList<Phone>();
        contactPhones.add(new Phone("123-456456456"));
        contactPhones.add(new Phone("789-456456466"));
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertFalse(result);

        // contact contains emails, twin contains phones: there is no an
        // element with only names
        contactEmails.add(new Email("email1@fun.com"));
        contactEmails.add(new Email("email2@fun.com"));
        twinEmails.clear();
        contactPhones.clear();
        twinPhones = new HashMap<Integer, String>();
        twinPhones.put(1, "753-753753753");
        twinPhones.put(10, "951-95195195");
        result = (Boolean)PrivateAccessor.invoke(
            contactDAO,
            methodName,
            new Class[] {List.class, List.class, Map.class, Map.class},
            new Object[] {contactEmails, contactPhones, twinEmails, twinPhones}
        );
        assertFalse(result);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        DBHelper.loadDataSet(DBHelper.getUserConnection(USER_PART1),
                             TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/items/dao/PIMContactDAO/initial-db-dataset-part1.xml",
                             true);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }
    // --------------------------------------------------------- Private methods

    /**
     * @todo very simple implementation
     */
    private void compareContactWrapper(ContactWrapper cw1, ContactWrapper cw2) {
        Contact c1 = cw1.getContact();
        Contact c2 = cw2.getContact();

        String firstName1 = c1.getName().getFirstName().getPropertyValueAsString();
        String lastName1  = c1.getName().getLastName().getPropertyValueAsString();
        String company1 = c1.getBusinessDetail().getCompany().getPropertyValueAsString();
        Photo photo1 = c1.getPersonalDetail().getPhotoObject();

        String firstName2 = c2.getName().getFirstName().getPropertyValueAsString();
        String lastName2  = c2.getName().getLastName().getPropertyValueAsString();
        String company2 = c2.getBusinessDetail().getCompany().getPropertyValueAsString();
        Photo photo2 = c2.getPersonalDetail().getPhotoObject();

        assertEquals(firstName1, firstName2);
        assertEquals(lastName1 , lastName2 );
        assertEquals(company1  , company2  );
        comparePhoto(photo1    , photo2    );
    }

    /**
     * Compares two photos
     * @param photo1
     * @param photo2
     */
    private void comparePhoto(Photo photo1, Photo photo2) {
        if (photo1 == null && photo2 == null) {
            return;
        }
        if (photo1 == null && photo2 != null) {
            fail("photo1 is null, photo2 is not null");
        }
        if (photo1 != null && photo2 == null) {
            fail("photo1 is not null, photo2 is null");
        }

        assertEquals(photo1.getType(), photo2.getType());
        assertEquals(photo1.getUrl(), photo2.getUrl());
        ArrayAssert.assertEquals(photo1.getImage(), photo2.getImage());
    }

    private void createDummyContact(Long id) throws Exception {

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Dummy");
        contact.getName().getLastName().setPropertyValue("Contact");

        ContactWrapper cw = new ContactWrapper(String.valueOf(id.longValue()), 
                                               USER_PART1,
                                               contact);
        contactDAO.addItem(cw);
    }

    private boolean isTherePhoto(String id) throws Exception {

        DatabaseConnection connection =
            new DatabaseConnection(DBHelper.getUserConnection(USER_PART1));
        int res = connection.getRowCount("fnbl_pim_contact_photo", "where contact='" + id+"'");
        return (res > 0);
    }

    private void fillContactTable() throws Exception {
        Contact contact;
        Email email;
        Phone phone;

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("John");
        contact.getName().getLastName().setPropertyValue("Smith");
        email = new Email("morton.sh@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Morton's Steak House");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-800-650-3333"));
        contact.getBusinessDetail().getAddress().setStreet(new Property("100 Market St."));
        contact.getBusinessDetail().getAddress().setCity(new Property("San Francisco"));
        contact.getBusinessDetail().getAddress().setCountry(new Property("CA"));
        contact.getBusinessDetail().getAddress().setPostalCode(new Property("90124"));
        ContactWrapper cw = new ContactWrapper("1001", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Lucy");
        contact.getName().getLastName().setPropertyValue("Parker");
        email = new Email("lucy@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1002", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Steven");
        contact.getName().getLastName().setPropertyValue("Jackson");
        cw = new ContactWrapper("1003", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Jerome");
        contact.getName().getLastName().setPropertyValue("Mullins");
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1004", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("dillon@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1005", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getBusinessDetail().getCompany().setPropertyValue("Rhymes's Restaurant");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-800-867-1212"));
        cw = new ContactWrapper("1006", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("ritherdon@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Ritherdon's SW House");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-811-123-3212"));
        cw = new ContactWrapper("1007", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Reuben");
        cw = new ContactWrapper("1008", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("Wills");
        cw = new ContactWrapper("1009", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("weijers.company@mail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1010", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Dustin");
        contact.getName().getLastName().setPropertyValue("Elmore");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-801-234-3434"));
        cw = new ContactWrapper("1011", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Dustin");
        contact.getName().getLastName().setPropertyValue("Elmore");
        contact.getBusinessDetail().getPhones().add(0, new Phone("1-802-567-6767"));
        cw = new ContactWrapper("1012", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Denzel");
        contact.getName().getLastName().setPropertyValue("Malfoil");
        email = new Email("malfoil@home.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("malfoil@other.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("10001", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("TomBans");
        contact.getBusinessDetail().getPhones().add(0, new Phone("2-821-234-5454"));
        cw = new ContactWrapper("10002", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Marienne");
        contact.getName().getLastName().setPropertyValue("Calaflou");
        email = new Email("calaflou-inc@gmail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("012-3456789");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().getPhones().add(phone);
        phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        cw = new ContactWrapper("10003", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Sam");
        email = new Email("sam@yahoo.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        cw = new ContactWrapper("10004", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getLastName().setPropertyValue("Parker");
        email = new Email("info@company.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("home@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("10005", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Bob");
        contact.getBusinessDetail().getCompany().setPropertyValue("Bob's SW House");
        email = new Email("bob@gmail.com");
        email.setEmailType("Email1Address");
        contact.getBusinessDetail().getEmails().add(email);
        phone = new Phone("987-654321");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getBusinessDetail().getPhones().add(phone);
        cw = new ContactWrapper("10006", USER_PART1, contact);
        contactDAO.addItem(cw);

                // Long first name
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Jerome with very long long first name. it must be longer than 64 chars as this.");
        contact.getName().getLastName().setPropertyValue("Mullins");
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("100021", USER_PART1, contact);
        contactDAO.addItem(cw);

        // Long last name
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Jerome");
        contact.getName().getLastName().setPropertyValue("Mullins with very long long first name. it must be longer than 64 chars as this.");
        email = new Email(null);
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("100022", USER_PART1, contact);
        contactDAO.addItem(cw);

        // Long email address
        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("JeromeBis");
        contact.getName().getLastName().setPropertyValue("MullinsBis");
        email = new Email("email address with very long long first name. it must be longer than 255 chars as this.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("100023", USER_PART1, contact);
        contactDAO.addItem(cw);

        // Long email address but no name
        contact = new Contact();
        email = new Email("email address with very long long first name. it must be longer than 255 chars as this.AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("100024", USER_PART1, contact);
        contactDAO.addItem(cw);

    }

    private void fillContactTableWithDisplayName() throws Exception {

        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        contact.getName().getDisplayName().setPropertyValue("Tony Edward");
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-445566"));
        ContactWrapper cw = new ContactWrapper("1014", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        contact.getName().getDisplayName().setPropertyValue("Lucy Parker");
        Email email = new Email("lp@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-445566"));
        cw = new ContactWrapper("1015", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Tony");
        contact.getName().getLastName().setPropertyValue("Eng");
        contact.getName().getDisplayName().setPropertyValue("T Eng");
        email = new Email("te@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        cw = new ContactWrapper("1016", USER_PART1, contact);
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-885533"));
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        contact.getName().getDisplayName().setPropertyValue("Mark Edward");
        email = new Email("medward@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("");
        cw = new ContactWrapper("1017", USER_PART1, contact);
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-885533"));
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        contact.getName().getDisplayName().setPropertyValue("Michele Gutemberg");
        email = new Email("mg@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("Funambol");
        cw = new ContactWrapper("1018", USER_PART1, contact);
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-92345776"));
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("");
        contact.getName().getLastName().setPropertyValue("");
        contact.getName().getDisplayName().setPropertyValue("A Andry");
        email = new Email("aa@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        contact.getBusinessDetail().getCompany().setPropertyValue("NewSync");
        cw = new ContactWrapper("1019", USER_PART1, contact);
        contact.getBusinessDetail().getPhones().add(0, new Phone("555-923321155"));
        contactDAO.addItem(cw);

        contact = new Contact();
        contact.getName().getDisplayName().setPropertyValue("John");
        email = new Email("home@gmail.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("10020", USER_PART1, contact);
        contactDAO.addItem(cw);


    }

    /**
     * Fills the fnbl_pim_contact with unnamed contacts in order to test the
     * search twin the a contact has first, last, company and display names
     * empty, but it has at least an email or a phone number.
     */
    private void fillContactTableWithUnnamedContact() throws Exception {
        Contact contact = null;
        Email email = null;
        Phone phone = null;

        contact = new Contact();
        contact.getBusinessDetail().getPhones().add(0, new Phone("801-622656"));
        ContactWrapper cw = new ContactWrapper("1020", USER_PART1, contact);
        contactDAO.addItem(cw);

        email = new Email("homeaddress@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1021", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("workaddress@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        cw = new ContactWrapper("1022", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("homeaddress@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("workaddress@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        cw = new ContactWrapper("1023", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("homeaddress@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("business@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        cw = new ContactWrapper("1024", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("personal@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        email = new Email("workaddress@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        cw = new ContactWrapper("1025", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("personal@mail.com");
        email.setEmailType("Email2Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1026", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("business@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        email = new Email("info@myhome.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1027", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        phone = new Phone("010-15768990");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        cw = new ContactWrapper("1013", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        phone = new Phone("013-5938232");
        phone.setPhoneType("HomeTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        email = new Email("tony@pavia.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        cw = new ContactWrapper("1028", USER_PART1, contact);
        contactDAO.addItem(cw);

        contact = new Contact();
        email = new Email("email3.work@mail.com");
        email.setEmailType("Email3Address");
        contact.getBusinessDetail().getEmails().add(email);
        email = new Email("email1.home@myhome.com");
        email.setEmailType("Email1Address");
        contact.getPersonalDetail().getEmails().add(email);
        phone = new Phone("010-15768123");
        phone.setPhoneType("BusinessTelephoneNumber");
        contact.getPersonalDetail().addPhone(phone);
        cw = new ContactWrapper("1029", USER_PART1, contact);
        contactDAO.addItem(cw);
    }

    private void comparePhotoType(String id, String expectedPhotoType) throws Exception {
        String query = "select photo_type from fnbl_pim_contact where id='"+id + "'";
        DBHelper.assertEqualsDataSet(USER_PART1,
                                     "expected_contact_photo_type",
                                     query,
                                     expectedPhotoType,
                                     null);

    }

    private void isPhotoTypeNull(String id) throws Exception {

        String query = "select photo_type from fnbl_pim_contact where id='"+id + "'";

        DatabaseConnection connection = null;
        try {
            connection =
                new DatabaseConnection(DBHelper.getUserConnection(USER_PART1));

            ITable actualTable = DBHelper.createITableFromQuery( "expected_contact_photo_type", query, connection);
            Object photoType = actualTable.getValue(0, "PHOTO_TYPE");
            assertNull( photoType);
        } finally {
            DBHelper.closeConnection(connection);
        }

    }

}
