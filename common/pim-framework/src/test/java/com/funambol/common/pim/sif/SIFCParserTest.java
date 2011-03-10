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

package com.funambol.common.pim.sif;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import junitx.framework.ArrayAssert;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Photo;

import com.funambol.framework.tools.IOTools;

/**
 * SIFCParser test cases
 * @version $Id: SIFCParserTest.java,v 1.4 2007-11-28 11:14:31 nichele Exp $
 */
public class SIFCParserTest extends TestCase {

    public SIFCParserTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    public void testParse_1() throws Exception {
        String xml = IOTools.readFileString("src/test/data/com/funambol/common/pim/sif/sifc-parser/sifc-1.xml");
        SIFCParser parser = new SIFCParser(new ByteArrayInputStream(xml.getBytes()));
        Contact c = parser.parse(new Contact());

        assertEquals("BusinessCity100", c.getBusinessDetail().getAddress().getCity().getPropertyValueAsString());
        assertEquals("Italy", c.getBusinessDetail().getAddress().getCountry().getPropertyValueAsString());
        assertEquals("BusinessZip100", c.getBusinessDetail().getAddress().getPostalCode().getPropertyValueAsString());
        assertEquals("BusinessState100", c.getBusinessDetail().getAddress().getState().getPropertyValueAsString());
        assertEquals("BusinessStreet100", c.getBusinessDetail().getAddress().getStreet().getPropertyValueAsString());
        assertEquals("Funambol", c.getBusinessDetail().getCompany().getPropertyValueAsString());
        assertEquals("Dep100", c.getBusinessDetail().getDepartment().getPropertyValueAsString());
        assertEquals("Dr. Contact100 I", c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Contact100", c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("DEFAULT_FOLDER", c.getFolder());
        assertEquals("0", c.getPersonalDetail().getGender());
        assertEquals("CityHome100", c.getPersonalDetail().getAddress().getCity().getPropertyValueAsString());
        assertEquals("Italy", c.getPersonalDetail().getAddress().getCountry().getPropertyValueAsString());
        assertEquals("HomeZip100", c.getPersonalDetail().getAddress().getPostalCode().getPropertyValueAsString());
        assertEquals("HomeState100", c.getPersonalDetail().getAddress().getState().getPropertyValueAsString());
        assertEquals("HomeStreet100", c.getPersonalDetail().getAddress().getStreet().getPropertyValueAsString());
        assertEquals("1", String.valueOf(c.getImportance()));
        assertEquals("C.", c.getName().getInitials().getPropertyValueAsString());
        assertEquals("Dr. Contact100 I", c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Dep100", c.getBusinessDetail().getOfficeLocation());
        assertEquals("OtherCity100", c.getPersonalDetail().getOtherAddress().getCity().getPropertyValueAsString());
        assertEquals("Italy", c.getPersonalDetail().getOtherAddress().getCountry().getPropertyValueAsString());
        assertEquals("OtherZip100", c.getPersonalDetail().getOtherAddress().getPostalCode().getPropertyValueAsString());
        assertEquals("OtherState100", c.getPersonalDetail().getOtherAddress().getState().getPropertyValueAsString());
        assertEquals("OtherStreet100", c.getPersonalDetail().getOtherAddress().getStreet().getPropertyValueAsString());
        assertEquals("Profession100", c.getBusinessDetail().getRole().getPropertyValueAsString());
        assertEquals("0", String.valueOf(c.getSensitivity()));
        assertEquals("Contact100 I", c.getSubject());
        assertEquals("I", c.getName().getSuffix().getPropertyValueAsString());

        assertNotNull(c.getPersonalDetail().getPhotoObject());
        assertNull(c.getPersonalDetail().getPhotoObject().getType());
        assertNull(c.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(c.getPersonalDetail().getPhotoObject().getImage());

        // @todo Add all properties belonging to a list (webpages, titles, emails, phones)
    }

    public void testParse_Photo2() throws Exception {
        String xml = IOTools.readFileString("src/test/data/com/funambol/common/pim/sif/sifc-parser/sifc-2.xml");
        SIFCParser parser = new SIFCParser(new ByteArrayInputStream(xml.getBytes()));
        Contact c = parser.parse(new Contact());

        byte[] expectedImage = IOTools.readFileBytes("src/test/data/com/funambol/common/pim/sif/sifc-parser/photo-2.jpg");
        Photo expectedPhoto = new Photo("JPG", null, expectedImage);

        assertEquals("Test Photo", c.getSubject());
        assertEquals("Photo, Test", c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Test", c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Photo", c.getName().getLastName().getPropertyValueAsString());

        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_EmptyPhoto_3() throws Exception {
        String xml = IOTools.readFileString("src/test/data/com/funambol/common/pim/sif/sifc-parser/sifc-3.xml");
        SIFCParser parser = new SIFCParser(new ByteArrayInputStream(xml.getBytes()));
        Contact c = parser.parse(new Contact());

        Photo expectedPhoto = new Photo(null, null, null);

        assertEquals("Test Photo", c.getSubject());
        assertEquals("Photo, Test", c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Test", c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Photo", c.getName().getLastName().getPropertyValueAsString());

        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_NoPhoto_4() throws Exception {
        String xml = IOTools.readFileString("src/test/data/com/funambol/common/pim/sif/sifc-parser/sifc-4.xml");
        SIFCParser parser = new SIFCParser(new ByteArrayInputStream(xml.getBytes()));
        Contact c = parser.parse(new Contact());

        assertEquals("Test Photo", c.getSubject());
        assertEquals("Photo, Test", c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Test", c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Photo", c.getName().getLastName().getPropertyValueAsString());

        assertNull(c.getPersonalDetail().getPhotoObject());
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Compares two photos
     * @param photo1
     * @param photo2
     */
    private void comparePhoto(Photo photo1, Photo photo2) {
        assertEquals(photo1.getType(), photo2.getType());
        assertEquals(photo1.getUrl(), photo2.getUrl());
        ArrayAssert.assertEquals(photo1.getImage(), photo2.getImage());
    }
}
