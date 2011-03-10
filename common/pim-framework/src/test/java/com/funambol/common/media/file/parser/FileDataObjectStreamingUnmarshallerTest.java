/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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

import com.funambol.common.media.file.FileDataObject;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SourceUtils;
import java.io.File;
import java.util.TimeZone;
import junit.framework.TestCase;
import junitx.framework.ArrayAssert;
import junitx.util.PrivateAccessor;

/**
 * FileDataObjectStreamingUnmarshaller's test cases
 * @version $Id: FileDataObjectStreamingUnmarshallerTest.java 35942 2010-09-09 17:08:30Z filmac $
 */
public class FileDataObjectStreamingUnmarshallerTest extends TestCase {

    static final public String WATERFALL_FILENAME = "waterfall.xml";

    /** Test resources path. */
    static final public String resourceRootPath = "target/test-classes";

    /** Test resources path. */
    private String resourcePath;

    // ------------------------------------------------------------- Constructor

    public FileDataObjectStreamingUnmarshallerTest(String testName) {
        super(testName);
        resourcePath = resourceRootPath + "/data/com/funambol/common/media/file" +
                "/parser/FileDataObjecStreamingUnmarshallerTest/" ;
    }

    // -------------------------------------------------------------- Test cases

    public void testUnmarshall_SingleMessage() throws Exception {

        // setup
        String incomingMessageFileName = WATERFALL_FILENAME;
        String tmpFileName = "00000000.tmp";
        String expectedFileName = "waterfall.jpg";

        TimeZone berlinTZ = TimeZone.getTimeZone("Europe/Berlin");

        File bodyFile =  new File(resourcePath, tmpFileName);

        FileDataObjectStreamingUnmarshaller unmarshaller =
                new FileDataObjectStreamingUnmarshaller(berlinTZ, bodyFile);

        byte[] input = IOTools.readFileBytes(resourcePath + incomingMessageFileName);

        // execute
        unmarshaller.unmarshall(input);

        // check
        FileDataObject fileDataObject = unmarshaller.getFileDataObject();

        assertEquals("Wrong body file", bodyFile, fileDataObject.getBodyFile());
        assertEquals("Waterfall.jpg", fileDataObject.getName());

        assertEquals("20030807T211830Z", fileDataObject.getCreated());
        assertEquals("20030808T235500Z", fileDataObject.getModified());
        assertEquals("20030808T235800Z", fileDataObject.getAccessed());

        assertEquals(new Boolean(false), fileDataObject.getHidden());
        assertEquals(new Boolean(false), fileDataObject.getSystem());
        assertEquals(new Boolean(true), fileDataObject.getArchived());
        assertEquals(new Boolean(false), fileDataObject.getDeleted());
        assertEquals(new Boolean(true), fileDataObject.getWritable());
        assertEquals(new Boolean(true), fileDataObject.getReadable());
        assertEquals(new Boolean(false), fileDataObject.getExecutable());
        assertEquals(new Long(30124), fileDataObject.getSize());
        assertTrue("Wrong upload status",fileDataObject.isUploaded());


        byte[] expectedFDOContentBytes = IOTools.readFileBytes(resourcePath + expectedFileName);
        long expectedCrc = SourceUtils.computeCRC(expectedFDOContentBytes);

        assertEquals("Wrong CRC", expectedCrc, fileDataObject.getCrc());

        byte[] actualFDOContentBytes = IOTools.readFileBytes(bodyFile);

        ArrayAssert.assertEquals(expectedFDOContentBytes, actualFDOContentBytes);

        assertEquals(fileDataObject.getSize(), new Long(actualFDOContentBytes.length));
    }

    public void testUnmarshall_SingleMessageEmptyBody() throws Exception {

        // setup
        String incomingMessageFileName = "emptybody.xml";
        String tmpFileName = "00000001.tmp";

        TimeZone berlinTZ = TimeZone.getTimeZone("Europe/Berlin");

        File bodyFile =  new File(resourcePath, tmpFileName);

        FileDataObjectStreamingUnmarshaller unmarshaller =
                new FileDataObjectStreamingUnmarshaller(berlinTZ, bodyFile);

        byte[] input = IOTools.readFileBytes(resourcePath + incomingMessageFileName);

        // execute
        unmarshaller.unmarshall(input);

        // check
        FileDataObject fileDataObject = unmarshaller.getFileDataObject();

        assertNull("The body file must be null", fileDataObject.getBodyFile());
        assertEquals("Waterfall.jpg", fileDataObject.getName());

        assertEquals("20030807T211830Z", fileDataObject.getCreated());
        assertEquals("20030808T235500Z", fileDataObject.getModified());
        assertEquals("20030808T235800Z", fileDataObject.getAccessed());

        assertEquals(new Boolean(false), fileDataObject.getHidden());
        assertEquals(new Boolean(false), fileDataObject.getSystem());
        assertEquals(new Boolean(true), fileDataObject.getArchived());
        assertEquals(new Boolean(false), fileDataObject.getDeleted());
        assertEquals(new Boolean(true), fileDataObject.getWritable());
        assertEquals(new Boolean(true), fileDataObject.getReadable());
        assertEquals(new Boolean(false), fileDataObject.getExecutable());
        assertEquals(new Long(30124), fileDataObject.getSize());
        assertTrue("Wrong upload status",fileDataObject.isUploadNotStarted());

        long expectedCrc = FileDataObject.CRC_NOT_DEFINED;
        assertEquals("Wrong CRC", expectedCrc, fileDataObject.getCrc());

        assertFalse(bodyFile.exists());
    }

    public void testUnmarshall_multichunk() throws Exception {

        // setup
        String fdoFileName = WATERFALL_FILENAME;
        String tmpFileName = "00000002.tmp";
        String expectedFileName = "waterfall.jpg";

        TimeZone berlinTZ = TimeZone.getTimeZone("Europe/Berlin");

        File bodyFile =  new File(resourcePath, tmpFileName);

        FileDataObjectStreamingUnmarshaller unmarshaller =
                new FileDataObjectStreamingUnmarshaller(berlinTZ, bodyFile);

        byte[] input = IOTools.readFileBytes(resourcePath + fdoFileName);

        byte[] input1 = new byte[10];
        byte[] input2 = new byte[100];
        byte[] input3 = new byte[1000];
        byte[] input4 = new byte[1000];
        byte[] input5 = new byte[1000];
        byte[] input6 = new byte[input.length - 3110];

        System.arraycopy(input, 0,    input1, 0, 10);
        System.arraycopy(input, 10,   input2, 0, 100);
        System.arraycopy(input, 110,  input3, 0, 1000);
        System.arraycopy(input, 1110, input4, 0, 1000);
        System.arraycopy(input, 2110, input5, 0, 1000);
        System.arraycopy(input, 3110, input6, 0, input.length - 3110);

        // execute
        unmarshaller.unmarshall(input1);
        unmarshaller.unmarshall(input2);
        unmarshaller.unmarshall(input3);
        unmarshaller.unmarshall(input4);
        unmarshaller.unmarshall(input5);
        unmarshaller.unmarshall(input6);

        // check
        FileDataObject fileDataObject = unmarshaller.getFileDataObject();

        assertEquals("Wrong body file", bodyFile, fileDataObject.getBodyFile());
        assertEquals("Waterfall.jpg", fileDataObject.getName());

        assertEquals("20030807T211830Z", fileDataObject.getCreated());
        assertEquals("20030808T235500Z", fileDataObject.getModified());
        assertEquals("20030808T235800Z", fileDataObject.getAccessed());

        assertEquals(new Boolean(false), fileDataObject.getHidden());
        assertEquals(new Boolean(false), fileDataObject.getSystem());
        assertEquals(new Boolean(true), fileDataObject.getArchived());
        assertEquals(new Boolean(false), fileDataObject.getDeleted());
        assertEquals(new Boolean(true), fileDataObject.getWritable());
        assertEquals(new Boolean(true), fileDataObject.getReadable());
        assertEquals(new Boolean(false), fileDataObject.getExecutable());
        assertEquals(new Long(30124), fileDataObject.getSize());
        assertTrue("Wrong upload status",fileDataObject.isUploaded());

        byte[] expectedFDOContentBytes = IOTools.readFileBytes(resourcePath + expectedFileName);

        long expectedCrc = SourceUtils.computeCRC(expectedFDOContentBytes);
        assertEquals("Wrong CRC", expectedCrc, fileDataObject.getCrc());

        byte[] actualFDOContentBytes = IOTools.readFileBytes(bodyFile);

        ArrayAssert.assertEquals(expectedFDOContentBytes, actualFDOContentBytes);

        assertEquals(fileDataObject.getSize(), new Long(actualFDOContentBytes.length));
    }

    public void testUnmarshall_multichunkEmptyBody() throws Exception {

        // setup
        String fdoFileName = "emptybody.xml";
        String tmpFileName = "00000002.tmp";
        String expectedFileName = "waterfall.jpg";

        TimeZone berlinTZ = TimeZone.getTimeZone("Europe/Berlin");

        File bodyFile =  new File(resourcePath, tmpFileName);

        FileDataObjectStreamingUnmarshaller unmarshaller =
                new FileDataObjectStreamingUnmarshaller(berlinTZ, bodyFile);

        byte[] input = IOTools.readFileBytes(resourcePath + fdoFileName);

        byte[] input1 = new byte[10];
        byte[] input2 = new byte[100];
        byte[] input3 = new byte[input.length - 110];

        System.arraycopy(input, 0,    input1, 0, 10);
        System.arraycopy(input, 10,   input2, 0, 100);
        System.arraycopy(input, 110, input3, 0, input.length - 110);

        // execute
        unmarshaller.unmarshall(input1);
        unmarshaller.unmarshall(input2);
        unmarshaller.unmarshall(input3);

        // check
        FileDataObject fileDataObject = unmarshaller.getFileDataObject();

        assertNull("The body file must be null", fileDataObject.getBodyFile());
        assertEquals("Waterfall.jpg", fileDataObject.getName());

        assertEquals("20030807T211830Z", fileDataObject.getCreated());
        assertEquals("20030808T235500Z", fileDataObject.getModified());
        assertEquals("20030808T235800Z", fileDataObject.getAccessed());

        assertEquals(new Boolean(false), fileDataObject.getHidden());
        assertEquals(new Boolean(false), fileDataObject.getSystem());
        assertEquals(new Boolean(true), fileDataObject.getArchived());
        assertEquals(new Boolean(false), fileDataObject.getDeleted());
        assertEquals(new Boolean(true), fileDataObject.getWritable());
        assertEquals(new Boolean(true), fileDataObject.getReadable());
        assertEquals(new Boolean(false), fileDataObject.getExecutable());
        assertEquals(new Long(30124), fileDataObject.getSize());
        assertTrue("Wrong upload status",fileDataObject.isUploadNotStarted());

        long expectedCrc = FileDataObject.CRC_NOT_DEFINED;
        assertEquals("Wrong CRC", expectedCrc, fileDataObject.getCrc());

        assertFalse(bodyFile.exists());
    }

    // ---------------------------------------------------- Private Method tests

    public void testGetBodyIndex() throws Exception, Throwable {
        byte[] chunk = null;
        int index = -1;
        FileDataObjectStreamingUnmarshaller unmarshaller = null;

        String enc = null;

        chunk = null;
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        chunk = "this is a simple test <body> with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should be found", 27, index);

        chunk = "<body> with the tag at the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should be found", 5, index);

        chunk = "tag at the end <body>".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should be found", 20, index);

        chunk = "this is a simple test <body   enc=\"base64\"  > with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should be found", 44, index);
        assertEquals("Wrong encoding", "base64", enc);

        chunk = "<body enc=\"\"> with the tag and the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should be found", 12, index);
        assertEquals("Wrong encoding", "", enc);

        chunk = "tag and the end <body enc = \"quoted\"  >".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should be found", 38, index);
        assertEquals("Wrong encoding", "quoted", enc);

        //
        // In the following tests the tag should not be found
        //
        chunk = "this is a simple test <body with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        chunk = "body> with the tag and the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        chunk = "tag and the end <bdy>".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        chunk = "this is a simple test <body   elc=\"base64\"  > with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        chunk = "<body enc=\"\" with the tag and the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should not be found", -1, index);
        assertEquals("Wrong encoding", "", enc);

        chunk = "tag and the end <body enc  \"quoted\"  >".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should not be found", -1, index);
        assertEquals("Wrong encoding", "", enc);

        chunk = "tag at the end <bbbody enc = \"quoted\"  >".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        enc = (String)PrivateAccessor.getField(unmarshaller, "sbEncoding").toString();
        assertEquals("Body should not be found", -1, index);
        assertEquals("Wrong encoding", "", enc);

    }

    public void testGetBodyIndex_with_multi_chunk() throws Exception, Throwable {
        byte[] chunk = null;
        byte[] chunk1 = null;
        byte[] chunk2 = null;
        byte[] chunk3 = null;
        byte[] chunk4 = null;
        byte[] chunk5 = null;

        int index = -1;
        FileDataObjectStreamingUnmarshaller unmarshaller = null;

        // ---------------------------------------------------------------------
        chunk = null;
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk});
        assertEquals("Body should not be found", -1, index);

        // ---------------------------------------------------------------------
        chunk1 = "this i".getBytes();
        chunk2 = "s a ".getBytes();
        chunk3 = "simple test <bo".getBytes();
        chunk4 = "dy> with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should be found", 2, index);
        // ---------------------------------------------------------------------

        chunk1 = null;
        chunk2 = "<".getBytes();
        chunk3 = "b".getBytes();
        chunk4 = "ody> with the tag and the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should be found", 3, index);
        // ---------------------------------------------------------------------

        chunk1 = "tag ".getBytes();
        chunk2 = "at the end <b".getBytes();
        chunk3 = "ody>".getBytes();

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should be found", 3, index);
        // ---------------------------------------------------------------------

        chunk1 = "this is a simple te".getBytes();
        chunk2 = "st <body  ".getBytes();
        chunk3 = " enc=\"bas".getBytes();
        chunk4 = "e64\" ".getBytes();
        chunk5 = " > with the tag".getBytes();

        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk5});
        assertEquals("Body should be found", 1, index);
        // ---------------------------------------------------------------------


        //
        // In the following tests the tag should not be found
        //
        // ---------------------------------------------------------------------
        chunk1 = "this i".getBytes();
        chunk2 = "s a ".getBytes();
        chunk3 = "simple test <bo".getBytes();
        chunk4 = "dy with the tag".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should not be found", -1, index);
        // ---------------------------------------------------------------------

        chunk1 = null;
        chunk2 = "<".getBytes();
        chunk3 = "b".getBytes();
        chunk4 = "oay> with the tag and the beginning".getBytes();
        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should not be found", -1, index);
        // ---------------------------------------------------------------------

        chunk1 = "tag ".getBytes();
        chunk2 = "at the end <b".getBytes();
        chunk3 = "ody".getBytes();

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        // ---------------------------------------------------------------------

        chunk1 = "this is a simple te".getBytes();
        chunk2 = "st <body  ".getBytes();
        chunk3 = " enc=\"bas".getBytes();
        chunk4 = "e64 ".getBytes();
        chunk5 = " > with the tag".getBytes();

        unmarshaller = new FileDataObjectStreamingUnmarshaller(null, null);

        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk1});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk2});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk3});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk4});
        assertEquals("Body should not be found", -1, index);
        index = (Integer)PrivateAccessor.invoke(unmarshaller, "getBodyIndex", new Class[] {byte[].class}, new Object[] {chunk5});
        assertEquals("Body should not be found", -1, index);
        // ---------------------------------------------------------------------
    }
}
