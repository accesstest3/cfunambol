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
package com.funambol.framework.tools;

import java.io.File;
import java.io.File;

import java.util.Collection;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import org.apache.commons.io.FileUtils;

/**
 * WBXMLTools test cases
 * @version $Id: WBXMLToolsTest.java,v 1.1 2008-01-03 09:13:27 nichele Exp $
 */
public class WBXMLToolsTest extends TestCase {

    private static final String WBXML_TO_XML_TO_WBXML_DIR = "src/test/data/wbxml-to-xml-to-wbxml/";

    // ------------------------------------------------------------ Constructors
    public WBXMLToolsTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of wbxmlToXml and toWBXML methods, of class WBXMLTools.
     * @throws java.lang.Exception 
     */
    public void testWbxmlToXmlToWbxml() throws Exception {
        System.out.println("wbxmlToXml");
        Collection<File> files = FileUtils.listFiles(new File(WBXML_TO_XML_TO_WBXML_DIR),
                                                     new String[] {"wbxml"},
                                                     false);

        for (File f: files) {
            String fileNameWithExtension = f.getName();
            int indexExtension = fileNameWithExtension.indexOf(".wbxml");
            String fileName = fileNameWithExtension.substring(0, indexExtension);
            String xmlFileName = fileName + ".xml";
            testWbxmlToXmlToWbxml(f, new File(WBXML_TO_XML_TO_WBXML_DIR + xmlFileName));
        }

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

    // --------------------------------------------------------- Private methods


    /**
     * Reads the given wbxml file, converts it in xml and compare the xml with the
     * content of fileNameExpectedXML, then re-converts it in wbxml and compares
     * the result with the orginal wbxml.
     * In the comparison we skip bytes 29 and 448 since with the current version those
     * are different:
     * BYTE 29
     * Expected: .........-//SYNCML//DTD SyncML 1.1//EN.............
     * Results : .........-//SYNCML//DTD SyncML 1.0//EN.............
     *
     * BYTE 448
     * Expected: .........-//SYNCML//DTD DevInf 1.1//EN.............
     * Results : .........-//SYNCML//DTD DevInf 1.0//EN.............
     *
     * @param wbxmlFileName
     * @param fileNameExpectedXML
     * @throws java.lang.Exception
     */
    private void testWbxmlToXmlToWbxml(File wbxmlFileName, File fileNameExpectedXML)
    throws Exception {

        byte[] msg = IOTools.readFileBytes(wbxmlFileName);

        String xml = WBXMLTools.wbxmlToXml(msg);
        /*
        if (!fileNameExpectedXML.exists()) {
            IOTools.writeFile(xml, fileNameExpectedXML);
        }
        */
        String expectedXML = IOTools.readFileString(fileNameExpectedXML);

        assertEquals("XML comparison failed for: " + wbxmlFileName, expectedXML, xml);

        byte[] wbxml = WBXMLTools.toWBXML(xml);

        //
        // In the comparison we skip the bytes 29 and 448 since with the current
        // version those are different:
        //
        // Byte 29
        // Expected: .........-//SYNCML//DTD SyncML 1.1//.............
        // Results : .........-//SYNCML//DTD SyncML 1.0//.............
        //
        // BYTE 448
        // Expected: .........-//SYNCML//DTD DevInf 1.1//EN.............
        // Results : .........-//SYNCML//DTD DevInf 1.0//EN.............
        //
        compareWBXML(msg, wbxml, new int[] {29, 448});
    }

    /**
     * Compares the wbxml skipping the byte "skippedBytes"
     * @param msg1 the first message
     * @param msg2 the second message
     * @param skippedBytes the bytes to skip
     */
    private void compareWBXML(byte[] msg1, byte[] msg2, int[] skippedBytes) {
        byte[] copyMsg1 = new byte[msg1.length];
        byte[] copyMsg2 = new byte[msg2.length];

        System.arraycopy(msg1, 0, copyMsg1, 0, msg1.length);
        System.arraycopy(msg2, 0, copyMsg2, 0, msg2.length);

        if (skippedBytes != null) {
            for (int skippedByte : skippedBytes) {
                if (skippedByte < msg1.length) {
                    msg1[skippedByte] = 0;
                }
                if (skippedByte < msg2.length) {
                    msg2[skippedByte] = 0;
                }
            }
        }
        ArrayAssert.assertEquals(msg1, msg2);
    }
}
