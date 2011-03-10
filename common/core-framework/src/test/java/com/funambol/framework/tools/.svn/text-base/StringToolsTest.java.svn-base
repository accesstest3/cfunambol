/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test cases for StringTools class.
 *
 * @version $Id$
 */
public class StringToolsTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String TEST1 =
        "${var1} some text${var2} some more text${var2} some final text ${var3}";
    private static final String EXPECTED11 =
        "value1 some textvalue2 some more textvalue2 some final text 123";
    private static final String EXPECTED12 =
        "${var1} some text${var2} some more text${var2} some final text ${var3}";
    private static final String TEST2 =
        "this should be null: ${var1}, this is empty:${var2}";
    private static final String EXPECTED2 =
        "this should be null: null, this is empty:";
    private static final String EXPECTED3 =
        "this should be null: <>, this is empty:";

    private final Map<String, Object> MAP1;
    private final Map<String, Object> MAP2;

    // ------------------------------------------------------------ Constructors
    public StringToolsTest(String testName) {
        super(testName);

        MAP1 = new HashMap<String, Object>();
        MAP1.put("var1", "value1");
        MAP1.put("var2", "value2");
        MAP1.put("var3", Integer.valueOf(123));

        MAP2 = new HashMap<String, Object>();
        MAP2.put("var1", null);
        MAP2.put("var2", "");
        MAP2.put("var3", Integer.valueOf(123));
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

    // -------------------------------------------------------------- Test cases
    public void testReplace() {
        String ret;

        try {
            StringTools.replace(null, MAP1);
            fail("str must not be null");
        } catch (IllegalArgumentException e){
            //
            // this is ok!
            //
        }

        try {
            StringTools.replace(TEST1, null, MAP1);
            fail("nullValue must not be null");
        } catch (IllegalArgumentException e){
            //
            // this is ok!
            //
        }

        try {
            StringTools.replace(TEST1, null);
            fail("variables must not be null");
        } catch (IllegalArgumentException e){
            //
            // this is ok!
            //
        }

        ret = StringTools.replace(TEST1, MAP1);
        assertEquals(EXPECTED11, ret);

        MAP1.clear();
        ret = StringTools.replace(TEST1, MAP1);
        assertEquals(EXPECTED12, ret);
        
        ret = StringTools.replace(TEST2, MAP2);
        assertEquals(EXPECTED2, ret);

        ret = StringTools.replace(TEST2, "<>", MAP2);
        assertEquals(EXPECTED3, ret);
    }

    public void testConverterStringSizeInBytes() throws Throwable {
        String sizeAsString = null;
        long size = 0;
        long expectedSize = 0;
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "123    ";
        expectedSize = 123;
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "12.33    ";
        expectedSize = 12;
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "1.4 G ";
        expectedSize = (long) (1.4 * 1024 * 1024 * 1024);
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "123k";
        expectedSize = 123 * 1024;
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "1.5     M   ";
        expectedSize = (long) (1.5 * 1024 * 1024);
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "1,5  g   ";
        expectedSize = (long) (1.5 * 1024 * 1024 * 1024);
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "1,521  K   ";
        expectedSize = (long) (1.521 * 1024);
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "";
        expectedSize = 0;
        size = StringTools.converterStringSizeInBytes(sizeAsString);
        assertEquals("Wrong size in bytes", expectedSize, size);

        sizeAsString = "10GByte";
        try {
        size = StringTools.converterStringSizeInBytes(sizeAsString);
            fail("Should throw NumberFormatException");
        } catch (NumberFormatException ex) {
            // it's the expected result
        }

    }
}
