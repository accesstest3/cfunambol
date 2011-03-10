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

import java.util.TimeZone;

import junit.framework.TestCase;

import com.funambol.framework.tools.IOTools;

import com.funambol.common.media.file.*;

/**
 *
 * @version $Id: $
 */
public class FileDataObjectXMLParserTest extends TestCase {

    static final public String WATERFALL_FILENAME =         "waterfall.xml";
    static final public String FLOWER_FILENAME =            "flower.xml";
    static final public String EMPTY_FILENAME =             "empty.xml";
    static final public String SIMPLETEXT_FILENAME =        "simpletext.xml";
    static final public String SIMPLEENCODEDTEXT_FILENAME = "simpleencodedtext.xml";

    /** Test resources path. */
    static private String resourceRootPath = "target/test-classes";

    /** Test resources path. */
    static private String resourcePath;

    public FileDataObjectXMLParserTest(String testName) {
        super(testName);
        resourcePath = resourceRootPath + "/data/com/funambol/common/media/file/parser/FileDataObjectXMLParserTest" ;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void test_foo() throws Exception {
        String emptyPict = IOTools.readFileString(resourcePath + "/" + EMPTY_FILENAME);
        String expected = "<File><name>empty.jpg</name><body/><size>0</size></File>";

        expected = expected.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");
        emptyPict   = emptyPict.replaceAll("\\n", "").replaceAll("\\r", "").replaceAll(" ", "");

        assertEquals(expected, emptyPict);
    }

    public void test_InvalidXML_NoStartingTag() throws Exception {
        String fileDataObjectDoc = "&lt;File&gt;&lt;name&gt;empty.jpg&lt;/name&gt;&lt;body/&gt;&lt;size&gt;0&lt;/size&gt;&lt;/File&gt;";

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        try {
            FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);
        } catch (FileDataObjectParsingException ex) {

            assertEquals(FileDataObjectParsingException.MISSING_START_ELEMENT,
                    ex.getMessage());
            return;
        }
        fail("Should have thrown a FileDataObjectParsingException");
    }

    public void test_InvalidXML_WrongStartingTag() throws Exception {
        String fileDataObjectDoc = "<foo><name>empty.jpg</name><body/><size>0</size></File><File><name>empty.jpg</name><body/><size>0</size></foo>";

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        try {
            FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);
        } catch (FileDataObjectParsingException ex) {

            assertEquals(FileDataObjectParsingException.UNEXPECTED_ROOT_ELEMENT +
                        " 'foo'", ex.getMessage());
            return;
        }
        fail("Should have thrown a FileDataObjectParsingException");
    }

    public void testUnmarshall_Name_EmptyFDO() throws Exception {
        String fdoFileName = EMPTY_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("empty.jpg", fileDataObject.getName());
    }

    public void testUnmarshall_Name_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("Waterfall.jpg", fileDataObject.getName());
    }

    public void testUnmarshall_Created_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("20030807T231830Z", fileDataObject.getCreated());
    }

    public void testUnmarshall_Modified_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("20030809T015500Z", fileDataObject.getModified());
    }

    public void testUnmarshall_Accessed_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        TimeZone berlinTZ = TimeZone.getTimeZone("Europe/Berlin");

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(berlinTZ);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("20030808T235800Z", fileDataObject.getAccessed());
    }

    public void testUnmarshall_Hidden_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(false), fileDataObject.getHidden());
    }

    public void testUnmarshall_System_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(false), fileDataObject.getSystem());
    }

    public void testUnmarshall_Archived_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(true), fileDataObject.getArchived());
    }

    public void testUnmarshall_Deleted_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(false), fileDataObject.getDeleted());
    }

    public void testUnmarshall_Writable_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(true), fileDataObject.getWritable());
    }

    public void testUnmarshall_Readable_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(true), fileDataObject.getReadable());
    }

    public void testUnmarshall_Executable_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Boolean(false), fileDataObject.getExecutable());
    }

    public void testUnmarshall_Cttype_SimpleTextFDO() throws Exception {
        String fdoFileName = SIMPLETEXT_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals("text/plain", fileDataObject.getContentType());
    }

    public void testUnmarshall_Size_EmptyFDO() throws Exception {
        String fdoFileName = EMPTY_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Long(0), fileDataObject.getSize());
    }

    public void testUnmarshall_Size_WaterfallFDO() throws Exception {
        String fdoFileName = WATERFALL_FILENAME;
        String fileDataObjectDoc = IOTools.readFileString(resourcePath + "/" + fdoFileName);

        FileDataObjectXMLParser parser = new FileDataObjectXMLParser(null);
        FileDataObject fileDataObject = parser.parse(fileDataObjectDoc);

        assertEquals(new Long(30124), fileDataObject.getSize());
    }
}
