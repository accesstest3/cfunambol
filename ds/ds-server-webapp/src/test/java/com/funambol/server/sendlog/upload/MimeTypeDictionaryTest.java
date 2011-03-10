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

package com.funambol.server.sendlog.upload;

import junit.framework.TestCase;

/**
 * Test cases for MimeTypeDictionary class.
 *
 * @version $Id: MimeTypeDictionaryTest.java 36089 2010-10-19 14:33:59Z luigiafassina $
 */
public class MimeTypeDictionaryTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String[] INPUT_VALUES =
        new String[]{"unknown",
                     "",
                     MimeTypeDictionary.APPLICATION_TAR,
                     MimeTypeDictionary.APPLICATION_TGZ,
                     MimeTypeDictionary.APPLICATION_Z,
                     MimeTypeDictionary.APPLICATION_ZIP,
                     MimeTypeDictionary.TEXT_PLAIN
    };

    // ------------------------------------------------------------ Constructors
    public MimeTypeDictionaryTest(String testName) {
        super(testName);
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
    public void testGetExtension() {
        String[] expectedValues =
            new String[]{null,
                        null,
                        MimeTypeDictionary.APPLICATION_TAR_EXTENSION,
                        MimeTypeDictionary.APPLICATION_TGZ_EXTENSION,
                        MimeTypeDictionary.APPLICATION_Z_EXTENSION,
                        MimeTypeDictionary.APPLICATION_ZIP_EXTENSION,
                        null
            };

        for(int i=0;i<INPUT_VALUES.length;i++)  {
            String inputValue   = INPUT_VALUES[i];
            String expecteValue = expectedValues[i];
            String result = MimeTypeDictionary.getExtension(inputValue);
            assertEquals("Wrong extension for mime type '"+i+"'",
                         expecteValue, result);
       }
    }

    public void testIsTextPlain() {
        boolean[] expectedValues = new boolean[]{false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                false,
                                                true
                                                };

        for(int i=0;i<INPUT_VALUES.length;i++)  {
            String  inputValue   = INPUT_VALUES[i];
            boolean expecteValue = expectedValues[i];
            boolean result = MimeTypeDictionary.isTextPlain(inputValue);
            assertEquals("Wrong result for text plain '"+i+"'",
                         expecteValue, result);
       }
    }

    public void testIsCompressible() {
         boolean[] expectedValues = new boolean[]{false,
                                                  false,
                                                  false,
                                                  false,
                                                  false,
                                                  false,
                                                  true
                                                };

        for(int i=0;i<INPUT_VALUES.length;i++)  {
            String  inputValue   = INPUT_VALUES[i];
            boolean expecteValue = expectedValues[i];
            boolean result = MimeTypeDictionary.isCompressible(inputValue);
            assertEquals("Wrong result for isCompressible '"+i+"'", 
                         expecteValue, result);
       }
    }

    public void testIsCompressed() {
         boolean[] expectedValues = new boolean[]{false,
                                                  false,
                                                  true,
                                                  true,
                                                  true,
                                                  true,
                                                  false
                                                };

        for(int i=0;i<INPUT_VALUES.length;i++)  {
            String  inputValue   = INPUT_VALUES[i];
            boolean expecteValue = expectedValues[i];
            boolean result = MimeTypeDictionary.isCompressed(inputValue);
            assertEquals("Wrong result for isCompressed '"+i+"'",
                         expecteValue, result);
       }
    }

    public void testIsExtensionKnown() {
         boolean[] expectedValues = new boolean[]{false,
                                                  false,
                                                  true,
                                                  true,
                                                  true,
                                                  true,
                                                  false
                                                };

        for(int i=0;i<INPUT_VALUES.length;i++)  {
            String  inputValue   = INPUT_VALUES[i];
            boolean expecteValue = expectedValues[i];
            boolean result = MimeTypeDictionary.isExtensionKnown(inputValue);
            assertEquals("Wrong result for isExtensionKnown '"+i+"'",
                         expecteValue, result);
       }
    }

}
