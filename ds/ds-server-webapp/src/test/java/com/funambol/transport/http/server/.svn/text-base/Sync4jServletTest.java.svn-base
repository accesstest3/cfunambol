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
package com.funambol.transport.http.server;

import com.funambol.framework.core.Ext;
import java.util.ArrayList;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 *
 */
public class Sync4jServletTest extends TestCase {
    
    public Sync4jServletTest(String testName) {
        super(testName);
    }            

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testFormatExtList_emptyExt() throws Exception, Throwable {
        System.out.println("formatExtList");
        
        ArrayList exts = new ArrayList();
        
        String formattedExtList = (String) PrivateAccessor.invoke(Sync4jServlet.class,
                "formatExtListAsProperties",
                new Class[]{ArrayList.class},
                new Object[]{exts});

        assertEquals("", formattedExtList);
    }

    public void testFormatExtList_oneExtWithOnlyName() throws Exception, Throwable {
        System.out.println("formatExtList");
        
        Ext ext = new Ext("X-funambol-smartslow", new String[0]);
        ArrayList exts = new ArrayList();
        exts.add(ext);
        
        String formattedExtList = (String) PrivateAccessor.invoke(Sync4jServlet.class,
                "formatExtListAsProperties",
                new Class[]{ArrayList.class},
                new Object[]{exts});

        assertEquals("X-funambol-smartslow", formattedExtList);
    }
    
    public void testFormatExtList_twoExt() throws Exception, Throwable {
        System.out.println("formatExtList");
        
        ArrayList exts = new ArrayList();
        exts.add(new Ext("X-funambol-smartslow", new String[0]));
        exts.add(new Ext("X-foo", new String[]{"val1","val2","val3"}));
        
        String formattedExtList = (String) PrivateAccessor.invoke(Sync4jServlet.class,
                "formatExtListAsProperties",
                new Class[]{ArrayList.class},
                new Object[]{exts});

        assertEquals("X-funambol-smartslow,X-foo[val1,val2,val3]", formattedExtList);
    }

    public void testFormatExtList_threeExt() throws Exception, Throwable {
        System.out.println("formatExtList");
        
        ArrayList exts = new ArrayList();
        exts.add(new Ext("X-funambol-smartslow", new String[0]));
        exts.add(new Ext("X-foo", new String[]{"val1","val2","val3"}));
        exts.add(new Ext("X-pippo", new String[]{"val"}));
        
        String formattedExtList = (String) PrivateAccessor.invoke(Sync4jServlet.class,
                "formatExtListAsProperties",
                new Class[]{ArrayList.class},
                new Object[]{exts});

        assertEquals("X-funambol-smartslow,X-foo[val1,val2,val3],X-pippo[val]", formattedExtList);
    }
    
    public void testFormatExtList_replaceChar() throws Exception, Throwable {
        System.out.println("formatExtList");
        
        ArrayList exts = new ArrayList();
        exts.add(new Ext("X-funambol-smartslow", new String[0]));
        exts.add(new Ext("X-fo,o", new String[]{"val1[pippo]","val,2","val3[pip,po]"}));
        exts.add(new Ext("X-pippo[pippo]", new String[]{"val[pippo]"}));
        
        String formattedExtList = (String) PrivateAccessor.invoke(Sync4jServlet.class,
                "formatExtListAsProperties",
                new Class[]{ArrayList.class},
                new Object[]{exts});

        assertEquals("X-funambol-smartslow,X-fo\\,o[val1\\[pippo\\],val\\,2,val3\\[pip\\,po\\]],X-pippo\\[pippo\\][val\\[pippo\\]]", formattedExtList);
    }
}
