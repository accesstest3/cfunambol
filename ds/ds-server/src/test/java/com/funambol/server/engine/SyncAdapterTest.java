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

package com.funambol.server.engine;

import java.util.HashMap;
import java.util.Map;

import junit.framework.*;
import junitx.util.PrivateAccessor;

import com.funambol.framework.core.*;
import com.funambol.server.config.Configuration;

/**
 * Test cases for SyncAdapter class.
 *
 * @version $Id: SyncAdapterTest.java,v 1.3 2008-04-22 08:20:00 nichele Exp $
 */
public class SyncAdapterTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public SyncAdapterTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Tests if return the charset encoding based on the header information.
     */
    public void testGetCharSet() throws Throwable {

        Map headers = new HashMap();
        headers.put(SyncAdapter.HEADER_CONTENT_TYPE,
                    "application/vnd.syncml+wbxml; charset=uft-8");
        String result = null;
        SyncAdapter sa = new SyncAdapter(Configuration.getConfiguration());

        result = (String) PrivateAccessor.invoke(
            sa,
            "getCharSet",
            new Class[] {Map.class},
            new Object[] {(headers)}
        );
        assertEquals("UTF-8", result);

        headers.put(SyncAdapter.HEADER_CONTENT_TYPE,
                    "application/vnd.syncml+wbxml; charset=ISO-8859-1");
        result = (String) PrivateAccessor.invoke(
            sa,
            "getCharSet",
            new Class[] {Map.class},
            new Object[] {(headers)}
        );
        assertEquals("ISO-8859-1", result);

        headers.put(SyncAdapter.HEADER_CONTENT_TYPE,
                    "application/vnd.syncml+wbxml; charset=UTF-8");
        result = (String) PrivateAccessor.invoke(
            sa,
            "getCharSet",
            new Class[] {Map.class},
            new Object[] {(headers)}
        );
        assertEquals("UTF-8", result);
    }

    /**
     * Tests if return the charset encoding if it was found in the prolog.
     */
    public void testGetCharSetOfProlog() throws Throwable {

        String result = null;

        SyncAdapter sa = new SyncAdapter(Configuration.getConfiguration());

        byte[] msg1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SyncML></SyncML>".getBytes();
        result = (String)PrivateAccessor.invoke(
            sa,
            "getCharSetOfProlog",
            new Class[] {byte[].class},
            new Object[] {(msg1)}
        );
        assertEquals("UTF-8", result);

        byte[] msg2 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><SyncML></SyncML>".getBytes();
        result = (String)PrivateAccessor.invoke(
            sa,
            "getCharSetOfProlog",
            new Class[] {byte[].class},
            new Object[] {(msg2)}
        );
        assertEquals("ISO-8859-1", result);

        byte[] msg3 = "<foo></foo>".getBytes();
        Object result3 = PrivateAccessor.invoke(
            sa,
            "getCharSetOfProlog",
            new Class[] {byte[].class},
            new Object[] {msg3}
        );
        assertNull(result3);
    }

    public void testgetResponseUri() throws Throwable {

        SyncAdapter sa = new SyncAdapter(Configuration.getConfiguration());

        String expected  = "http://dogfood.funambol.com/funambol/ds;jsessionid=83BA449905C620FAF86B5AEAF46B1841";
        String url       = "http://dogfood.funambol.com/funambol/ds";
        String sessionId = "83BA449905C620FAF86B5AEAF46B1841";
        String result = (String)PrivateAccessor.invoke(
            sa,
            "getResponseUri",
            new Class[] {String.class,String.class},
            new Object[] {url, sessionId}
        );
        assertEquals(expected, result);

        expected  = "http://dogfood.funambol.com/funambol/ds;jsessionid=11BA223344C555FAF66B7AEAF88B9900";
        url       = "http://dogfood.funambol.com/funambol/ds;jsessionid=83BA449905C620FAF86B5AEAF46B1841";
        sessionId = "11BA223344C555FAF66B7AEAF88B9900";
        result = (String)PrivateAccessor.invoke(
            sa,
            "getResponseUri",
            new Class[] {String.class,String.class},
            new Object[] {url, sessionId}
        );
        assertEquals(expected, result);

        try {
            url       = "//abcdef?que]ry";
            sessionId = "11BA223344C555FAF66B7AEAF88B9900";
            result = (String)PrivateAccessor.invoke(
                sa,
                "getResponseUri",
                new Class[] {String.class,String.class},
                new Object[] {url, sessionId}
            );
        } catch (Exception e) {
            return;
        }
        fail("Expected a MalformedURLException");
    }

    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        System.setProperty("funambol.home", "src/test/data");
    }

    protected void tearDown() throws Exception {
    }

}
