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
 * GetXMLTagContentTest
 * @version $Id: GetXMLTagContentTest.java 30730 2009-04-11 09:05:22Z nichele $
 */
public class GetXMLTagContentTest extends BeanShellTestCase {

    // ------------------------------------------------------------ Private data

    private String bshFileName = 
            "src/main/config/com/funambol/server/engine/pipeline/phones-support/bsh/commands/getXMLTagContent.bsh";

    // ---------------------------------------------------------- Public methods
    public GetXMLTagContentTest(String testName) throws Exception {
        super(testName);
        setBshFileName(bshFileName);
    }

    // -------------------------------------------------------------- Test cases

    public void testGetXMLTagContent() throws Throwable {

        String method = "getXMLTagContent";


        String xml = "<SyncHdr>" +
                        "<VerDTD>1.2</VerDTD>" +
                        "<VerProto>SyncML/1.2</VerProto>" +
                        "<SessionID>1238259398244</SessionID>" +
                        "<MsgID>1</MsgID>" +
                        "<EmptyTag></EmptyTag>" +
                        "<Target>" +
                        "<LocURI>fbb-1111111</LocURI>" +
                        "</Target>" +
                        "<Source>" +
                        "<LocURI>http://server.funambol.com/sync</LocURI>" +
                        "</Source>" +
                        "<RespURI>http://server.funambol.com/sync;jsessionid=11111111111111111111</RespURI>" +
                        "<LongNode>foj4rog o gitj rgorigjro w\n\r\n\t" +
                        "frefre e fpl rtgreogkrpokfoekf \n \n \n" +
                        "a</LongNode>" +
                        "<NotClosedTag>" +
                     "</SyncHdr>";

        String tag = "LocURI";
        String expResult = "fbb-1111111";

        Object result = exec(method, xml, tag);
        
        assertEquals("Wrong LocURI content", expResult, result);

        tag = "LongNode";
        expResult = "foj4rog o gitj rgorigjro w\n\r\n\t" +
                    "frefre e fpl rtgreogkrpokfoekf \n \n \n" +
                    "a";
        result = exec(method, xml, tag);
        assertEquals("Wrong LongNode content", expResult, result);

        tag = "EmptyTag";
        expResult = "";
        result = exec(method, xml, tag);
        assertEquals("Wrong EmptyTag content", expResult, result);

        tag = "NotExistingTag";
        expResult = null;
        result = exec(method, xml, tag);
        assertEquals("Expected null for a not existing tag", expResult, result);

        tag = "NotClosedTag";
        expResult = null;
        result = exec(method, xml, tag);
        assertEquals("Expected null for a not closed tag", expResult, result);

        tag = null;
        boolean illArg = false;
        try {
            result = exec(method, xml, tag);
        } catch (IllegalArgumentException e) {
            illArg = true;
        }
        assertTrue("Expected an IllegalArgumentException since tag was null", illArg);

        tag = "LocUri";
        xml = null;
        illArg = false;
        try {
            result = exec(method, xml, tag);
        } catch (IllegalArgumentException e) {
            illArg = true;
        }
        assertTrue("Expected an IllegalArgumentException since xml was null", illArg);
    }
}
