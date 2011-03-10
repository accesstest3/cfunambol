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
package org.clapper.util.html;

import com.funambol.email.test.EmailConnectorTestCase;
import com.funambol.email.test.ManagerUtility;
import javax.mail.Session;

/**
 *
 * @version $Id: HTMLUtilTest.java,v 1.4 2008-06-16 13:38:01 piter_may Exp $
 */
public class HTMLUtilTest extends EmailConnectorTestCase {
    
    // ------------------------------------------------------------ Constructors
    
    public HTMLUtilTest(String testName) {
        super(testName);
    }            
    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Test of HTMLToText method, of class HTMLUtil.
     */
    public void testConvertHtmlBodyWithSlash() throws Exception {

            String htmlbody = ManagerUtility.getContentFromFile("html_body_and_slash.txt");   
                        
            String result = HTMLUtil.textFromHTML(htmlbody); 
            
            String expected = "ciao\r\nhttp://www.funambol.com/funambol/ds \r\ngibi ";
                        
            assertEquals(result, expected);
    }
    
    /*
     * If the value of an attribute of a tag contains a '>' char, this char must
     * not be considered as the end of the tag.
     * 
     * Example: <a link="aaaa > bbbb"> the '>' within string "aaaa > bbbb" does
     * not close the tag.
     */ 
    public void testGreaterThanIssue_0() throws Exception {

        String testHtml =
                "These are \"good quotes\" as you can see." +
                "<a link=\"bad quotes > aaaa > bbbb >\">" +
                "Something \"to link between quotes\".</a>" +
                "<a link=\"bad major sign\">Some other things \"to link between quotes\".</a>" +
                "<a link=\"bad major sign > aaaa > bbbb >\">No more things \"to link between quotes\".</a>";
        String result = HTMLUtil.textFromHTML(testHtml);

        String expected =
                "These are \"good quotes\" as you can see.Something " +
                "\"to link between quotes\". Some other things \"to link between quotes\"." +
                " No more things \"to link between quotes\". ";

        assertEquals(expected, result);
    }

    public void testGreaterThanIssue_1() throws Exception {

        String testHtml =
                "<style type=\"text/css\">" +
                "body {font-family: Arial, \"Helvetica Neue\", \"Lucida Grande\", Helvetica;}" +
                "</style>";
                
        String result = HTMLUtil.textFromHTML(testHtml);

        String expected = "";
            
        assertEquals(expected, result);
    }
    
    // ------------------------------------------------------- Protected methods
    
    @Override
    protected void setUp() throws Exception {
        setMailserverConnectionProperties();
        session = Session.getInstance(connectionProperties);
    }
}
