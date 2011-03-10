/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.common.media.file;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test cases for FileDataObject class.
 *
 * @version $Id$
 */
public class FileDataObjectTest extends TestCase {

    /** Test resources path. */
    static final public String resourceRootPath = "target/test-classes";

    /** Test resources path. */
    private String resourcePath;

    // ------------------------------------------------------------- Constructor

    public FileDataObjectTest(String testName) {
        super(testName);
        resourcePath = resourceRootPath +
            "/data/com/funambol/common/media/file/FileDataObjectTest/" ;
    }

    // ------------------------------------------------------- Protected methods

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test constructor of class FileDataObject.
     */
    public void test_Constructor() {

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = fdo.getMetadata();
        FileDataObjectBody body = fdo.getBody();

        assertNotNull(metadata);
        assertNull("Metadata's name is not null", metadata.getName());
        assertNotNull(body);

        FileDataObject fdo2 =
            new FileDataObject(new FileDataObjectMetadata("name2"),
                               new FileDataObjectBody());
       assertEquals("Wrong metadata's name", "name2", fdo2.getName());
       assertFalse("The FDO contains a body", fdo2.hasBodyFile());

    }

    /**
     * Test get/set
     */
    public void test_GetSet() {

        FileDataObject fdo = new FileDataObject();

        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        FileDataObjectBody body = new FileDataObjectBody();

        fdo.setBody(body);
        fdo.setMetadata(metadata);

        assertEquals("Wrong metadata", metadata, fdo.getMetadata());
        assertEquals("Wrong body", body, fdo.getBody());
    }

    public void test_GetSetMetadataProperties() {

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("exif", "Model:FUN,Manufacturer:Funambol");
        properties.put("prop1", "firstprop");

        FileDataObjectMetadata metadata =
            new FileDataObjectMetadata("picture-metadata");
        metadata.setProperties(properties);

        FileDataObjectBody body = new FileDataObjectBody();

        FileDataObject fdo = new FileDataObject(metadata, body);

        Map<String, String> result = fdo.getProperties();
        assertNotNull("FDO has no any properties", result);
        assertEquals("Wrong number of properties returned",
                     properties.size(), result.size());
    }

    public void test_Set_NullParam() {

        FileDataObject fdo = new FileDataObject();
        fdo.setBody(null);
        fdo.setMetadata(null);
        assertNotNull(fdo.getMetadata());
        assertNotNull(fdo.getBody());
    }

    public void test_checkSize_BodyWithNullBodyFile() {

        long declaredFileSize = 100L;

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        fdo.setSize(Long.valueOf(declaredFileSize));
        fdo.setBody(null);
        assertEquals("checkSize fails with null body.",
                true, fdo.checkSize());

        fdo.setBody(new FileDataObjectBody());
        assertEquals("checkSize fails with null bodyFile inside the body",
                true, fdo.checkSize());
    }

    public void test_checkSize_NotDeclaredSize() {

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        File bodyFile =  new File(resourcePath, "waterfall.jpg");
        fdo.setBodyFile(bodyFile);
        assertEquals("checkSize should succedes if size has not been declared " +
                "and bodyFile has been set.",
                true, fdo.checkSize());
    }

    public void test_checkSize_NullActualSizeAndNotDeclaredSize() {

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        fdo.setBody(new FileDataObjectBody());
        assertEquals("checkSize should fail if bodyFile inside body i null and " +
                "no size has been declared.",
                false, fdo.checkSize());
    }

    public void test_checkSize_ActaulSizeMismatchDeclaredSize() {

        long declaredFileSize = 100L;

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        File bodyFile =  new File(resourcePath, "waterfall.jpg");
        fdo.setBodyFile(bodyFile);
        fdo.setSize(Long.valueOf(declaredFileSize));
        assertEquals("checkSize fails with metadata and body size values " +
                "mismatch.", false, fdo.checkSize());
    }

    public void test_checkSize_ActaulSizeMismatchDeclaredZeroSize() {

        long declaredFileSize = 0;

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        File bodyFile =  new File(resourcePath, "waterfall.jpg");
        fdo.setBodyFile(bodyFile);
        fdo.setSize(Long.valueOf(declaredFileSize));
        assertEquals("Metadata and body size values mismatched", false, fdo.checkSize());
    }

    public void test_checkSize_ActaulSizeMatchDeclaredSize() {

        long declaredFileSize = 30124;

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        File bodyFile =  new File(resourcePath, "waterfall.jpg");
        fdo.setBodyFile(bodyFile);
        fdo.setSize(new Long(declaredFileSize));
        assertEquals("Metadata and body declared size values matched.",
                     true, fdo.checkSize());
    }

    public void test_checkSize_ActaulZeroSizeMatchDeclaredSize() {

        long declaredFileSize = 0;

        FileDataObject fdo = new FileDataObject();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata("the-name");
        fdo.setMetadata(metadata);

        File bodyFile =  new File(resourcePath, "newFile.jpg");
        fdo.setBodyFile(bodyFile);
        fdo.setSize(Long.valueOf(declaredFileSize));
        assertEquals("Metadata and body size values matched.",
                     true, fdo.checkSize());
    }
}
