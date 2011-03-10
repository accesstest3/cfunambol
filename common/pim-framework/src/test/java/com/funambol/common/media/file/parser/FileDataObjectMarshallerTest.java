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
package com.funambol.common.media.file.parser;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import com.funambol.framework.tools.IOTools;
import com.funambol.common.media.file.*;


/**
 * FileDataObjectMarshaller's test cases
 * @version $Id$
 */
public class FileDataObjectMarshallerTest extends TestCase {

    // --------------------------------------------------------------- Constants

    static final public String EMPTY_FILENAME =             "empty.xml";
    static final public String SIMPLEENCODEDTEXT_FILENAME = "simpleencodedtext.xml";

    /** Test resources path. */
    static private String resourceRootPath;

    /** Test resources path. */
    static private String resourcePath;

    /** Resources path system property name. */
    static final public String RESOURCE_PATH_PROPERTY_NAME = "test.resources.dir";

    // -------------------------------------------------------- Member Variables

    /* FileDataObject with null body */
    private FileDataObject nullFDO;
    /* FileDataObject with empty body */
    private FileDataObject emptyFDO;
    /* FileDataObject with a generic body */
    private FileDataObject simpleEncodedTextFDO;

    public FileDataObjectMarshallerTest(String testName) {
        super(testName);
        resourceRootPath = System.getProperty(RESOURCE_PATH_PROPERTY_NAME);
        resourcePath = resourceRootPath +
                "/data/com/funambol/common/media/file/parser/FileDataObjectMarshallerTest" ;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        initEmptyFDO();
        initSimpleEncodedTextFDO();
        initNullFDO();
    }
        
    private void initSimpleEncodedTextFDO() {
        String fileName = "simpleencodedtext.txt";
        File bodyFile = new File(resourcePath, fileName);
        
        simpleEncodedTextFDO = new FileDataObject();
        
        simpleEncodedTextFDO.setBodyFile(bodyFile);

        simpleEncodedTextFDO.setName(fileName);
        simpleEncodedTextFDO.setCreated("20030807T231830");
        simpleEncodedTextFDO.setModified("20030809T015500");
        simpleEncodedTextFDO.setAccessed("20030809T016500");
        simpleEncodedTextFDO.setHidden(false);
        simpleEncodedTextFDO.setSystem(false);
        simpleEncodedTextFDO.setArchived(true);
        simpleEncodedTextFDO.setDeleted(false);
        simpleEncodedTextFDO.setWritable(true);
        simpleEncodedTextFDO.setReadable(true);
        simpleEncodedTextFDO.setExecutable(false);
        simpleEncodedTextFDO.setContentType("text/plain");
        simpleEncodedTextFDO.setSize(11L);
    }

    private void initEmptyFDO() {
        String fileName = "empty.txt";
        emptyFDO = new FileDataObject();
        emptyFDO.setBodyFile(new File(resourcePath, fileName));
        emptyFDO.setSize(0L);
        emptyFDO.setName("empty.jpg");
    }


    /**
     * Create a FileDataObject with null body
     */
    private void initNullFDO() {
        nullFDO = new FileDataObject();
        nullFDO.setSize(1234L);
        nullFDO.setName("null.jpg");
        nullFDO.setBody(null);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /**
     * Test of marshall method, of class FileDataObjectMarshaller.
     */
    public void testMarshall_SimpleEncodedText() throws FileDataObjectParsingException, IOException {


        FileDataObjectMarshaller instance = new FileDataObjectMarshaller();
        String expResult = FileDataObjectMarshaller.XML_VERSION +
                IOTools.readFileString(resourcePath + "/" + SIMPLEENCODEDTEXT_FILENAME);
        String result = new String(instance.marshall(simpleEncodedTextFDO));

        expResult = expResult.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        result   = result.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        assertEquals("The expected result and the marshalled message do not match", expResult, result);
    }

    /**
     * Test of marshall method, of class FileDataObjectMarshaller.
     */
    public void testMarshall_EmptyBody() throws FileDataObjectParsingException, IOException {

        FileDataObjectMarshaller instance = new FileDataObjectMarshaller();
        String expResult = FileDataObjectMarshaller.XML_VERSION +
                IOTools.readFileString(resourcePath + "/" + EMPTY_FILENAME);
        String result = new String(instance.marshall(emptyFDO));

        expResult = expResult.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        result   = result.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        assertEquals("The expected result and the marshalled message do not match", expResult, result);
    }

    public void testMarshall_nullBody() throws FileDataObjectParsingException, IOException {
        FileDataObjectMarshaller instance = new FileDataObjectMarshaller();

        String result = new String(instance.marshall(nullFDO));
        String expResult = FileDataObjectMarshaller.XML_VERSION +
                "<File><name>null.jpg</name><body/><size>1234</size></File>";

        expResult = expResult.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        result   = result.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        assertEquals("The expected result and the marshalled message do not match", expResult, result);
    }

}
