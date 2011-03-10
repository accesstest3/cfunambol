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

package com.funambol.phonessupport.bsh.commands;

import com.funambol.tools.test.BeanShellTestCase;

/**
 * ReplaceXMLTagContentTest
 * @version $Id: ReplaceXMLTagContentTest.java 30730 2009-04-11 09:05:22Z nichele $
 */
public class ReplaceXMLTagContentTest extends BeanShellTestCase {

    // ------------------------------------------------------------ Private data

    private String bshFileName =
            "src/main/config/com/funambol/server/engine/pipeline/phones-support/bsh/commands/replaceXMLTagContent.bsh";

    // ---------------------------------------------------------- Public methods
    public ReplaceXMLTagContentTest(String testName) throws Exception {
        super(testName);
        setBshFileName(bshFileName);
    }

    // -------------------------------------------------------------- Test cases

    public void testReplaceXMLTagContentTest() throws Throwable {

        String method = "replaceXMLTagContent";

        //
        // basic test
        //
        String xml = "<SyncHdr>" +
                        "<VerDTD>1.2\n aaaa\r\n \t</VerDTD>" +
                        "<VerProto>SyncML/1.2</VerProto>" +
                        "<SessionID>1238259398244</SessionID>" +
                        "<EmptyTag></EmptyTag>" +
                     "</SyncHdr>";
        String tag = "VerDTD";
        String newContent = "1.1.0\n\r\n \t";

        StringBuilder sb = new StringBuilder();

        boolean found = (Boolean)exec(method, sb, xml, tag, newContent);

        assertTrue("VerDTD had to be found", found);
        String newXML = sb.toString();
        String expectedXML = "<SyncHdr>" +
                                 "<VerDTD>1.1.0\n\r\n \t</VerDTD>" +
                                 "<VerProto>SyncML/1.2</VerProto>" +
                                 "<SessionID>1238259398244</SessionID>" +
                                 "<EmptyTag></EmptyTag>" +
                             "</SyncHdr>";

        assertEquals(expectedXML, newXML);

        //
        // Empty new content
        //
        tag = "VerDTD";
        newContent = "";
        sb = new StringBuilder();
        found = (Boolean)exec(method, sb, xml, tag, newContent);
        assertTrue("VerDTD had to be found", found);
        newXML = sb.toString();
        expectedXML = "<SyncHdr>" +
                          "<VerDTD></VerDTD>" +
                          "<VerProto>SyncML/1.2</VerProto>" +
                          "<SessionID>1238259398244</SessionID>" +
                          "<EmptyTag></EmptyTag>" +
                      "</SyncHdr>";

        assertEquals(expectedXML, newXML);

        //
        // null new content --> tag should be removed
        //
        tag = "VerDTD";
        newContent = null;
        sb = new StringBuilder();
        found = (Boolean)exec(method, sb, xml, tag, newContent);
        assertTrue("VerDTD had to be found", found);
        newXML = sb.toString();
        expectedXML = "<SyncHdr>" +
                          "<VerProto>SyncML/1.2</VerProto>" +
                          "<SessionID>1238259398244</SessionID>" +
                          "<EmptyTag></EmptyTag>" +
                      "</SyncHdr>";

        assertEquals(expectedXML, newXML);

        //
        // missing tag --> 'false' should be returned
        //
        tag = "MissingTag";
        newContent = "value";
        sb = new StringBuilder();
        found = (Boolean)exec(method, sb, xml, tag, newContent);
        assertFalse("MissingTag had to be missing", found);

        //
        // Empty old content --> not empty new content
        //
        tag = "EmptyTag";
        newContent = "new-content";
        sb = new StringBuilder();
        found = (Boolean)exec(method, sb, xml, tag, newContent);
        assertTrue("EmptyTag had to be found", found);
        newXML = sb.toString();
        expectedXML = "<SyncHdr>" +
                          "<VerDTD>1.2\n aaaa\r\n \t</VerDTD>" +
                          "<VerProto>SyncML/1.2</VerProto>" +
                          "<SessionID>1238259398244</SessionID>" +
                          "<EmptyTag>new-content</EmptyTag>" +
                      "</SyncHdr>";

        assertEquals(expectedXML, newXML);

        //
        // Empty old content --> removing tag
        //
        tag = "EmptyTag";
        newContent = null;
        sb = new StringBuilder();
        found = (Boolean)exec(method, sb, xml, tag, newContent);
        assertTrue("EmptyTag had to be found", found);
        newXML = sb.toString();
        expectedXML = "<SyncHdr>" +
                          "<VerDTD>1.2\n aaaa\r\n \t</VerDTD>" +
                          "<VerProto>SyncML/1.2</VerProto>" +
                          "<SessionID>1238259398244</SessionID>" +
                      "</SyncHdr>";

        assertEquals(expectedXML, newXML);
    }

}
