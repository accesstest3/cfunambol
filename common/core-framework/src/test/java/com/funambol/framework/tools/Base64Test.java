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


package com.funambol.framework.tools;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

/**
 * Base64's test cases
 * @version $Id: Base64Test.java 33303 2010-01-15 14:44:19Z nichele $
 */
public class Base64Test extends TestCase {
    
    public Base64Test(String testName) {
        super(testName);
    }

    /**
     * Test of encode method, of class Base64.
     */
    public void testEncode_InputStream_OutputStream() throws Exception {
        byte[] b = "simple test to verify b64 encoder.".getBytes();
        String b64 = "c2ltcGxlIHRlc3QgdG8gdmVyaWZ5IGI2NCBlbmNvZGVyLg==";

        ByteArrayInputStream in = new ByteArrayInputStream(b);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Base64.encode(in, out);

        String newB64 = new String(out.toByteArray());

        assertEquals(b64, newB64);
    }

    /**
     * Test of encode method, of class Base64.
     */
    public void testDecode() throws Exception {
        byte[] b = "simple test to verify b64 encoder.".getBytes();
        String b64 = "c2ltcGxlIHRlc3QgdG8gdmVyaWZ5IGI2NCBlbmNvZGVyLg==";

        byte[] results = Base64.decode(b64);

        ArrayAssert.assertEquals("Wrong bytes[]", b, results);
    }

    /**
     * Test of encode method, of class Base64.
     */
    public void testEncode() throws Exception {
        byte[] b = "simple test to verify b64 encoder.".getBytes();
        String b64 = "c2ltcGxlIHRlc3QgdG8gdmVyaWZ5IGI2NCBlbmNvZGVyLg==";

        byte[] results = Base64.encode(b);

        assertEquals("Wrong base64", b64, new String(results));
    }
}
