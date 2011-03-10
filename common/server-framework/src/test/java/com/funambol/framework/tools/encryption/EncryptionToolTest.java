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

package com.funambol.framework.tools.encryption;

import junit.framework.TestCase;

/**
 * EncryptionTool test cases
 * @version $Id: EncryptionToolTest.java,v 1.1 2008-01-02 21:29:52 nichele Exp $
 */
public class EncryptionToolTest extends TestCase {

    // ------------------------------------------------------------ Constructors
    public EncryptionToolTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of encrypt method, of class EncryptionTool.
     * @throws java.lang.Exception
     */
    public void testEncryptDecrypt_with_default_key() throws Exception {
        String pwd = null;
        String encrypted = null;
        String decrypted = null;

        encrypted = EncryptionTool.encrypt(pwd);
        assertNull(encrypted);

        pwd = "";
        encrypted = EncryptionTool.encrypt(pwd);
        decrypted = EncryptionTool.decrypt(encrypted);
        assertEquals(pwd, decrypted);

        pwd = "guest";
        encrypted = "65GUmi03K6o=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd));
        decrypted = EncryptionTool.decrypt(encrypted);
        assertEquals(pwd, decrypted);

        pwd = "sa";
        encrypted = "lltUbBHM7oA=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd));
        decrypted = EncryptionTool.decrypt(encrypted);
        assertEquals(pwd, decrypted);

        encrypted = "guest";
        try {
            //
            // This must fail since the text is not encrypted
            //
            decrypted = EncryptionTool.decrypt(encrypted);
            fail();
        } catch (EncryptionException e) {
        }
    }

    /**
     * Test of encrypt method, of class EncryptionTool.
     * @throws java.lang.Exception
     */
    public void testEncryptDecrypt_with_key() throws Exception {
        byte[] key = new byte[] {0x3,0x0,0x8,0x2,0x0,0x1,0xF};

        String pwd = null;
        String encrypted = null;
        String decrypted = null;

        encrypted = EncryptionTool.encrypt(pwd, key);
        assertNull(encrypted);

        pwd = "";
        encrypted = EncryptionTool.encrypt(pwd, key);
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        pwd = "guest";
        encrypted = "KGtu7fIiREg=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd, key));
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        pwd = "sa";
        encrypted = "B+fs/kjpLNM=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd, key));
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        encrypted = "lltUbBHM7oA=";
        try {
            //
            // This must fail since the text is encrypted with the default key
            //
            decrypted = EncryptionTool.decrypt(encrypted, key);
            fail();
        } catch (EncryptionException e) {
        }
    }

    /**
     * Test of encrypt method, of class EncryptionTool.
     * @throws java.lang.Exception
     */
    public void testEncryptDecrypt_with_empty_key() throws Exception {
        byte[] key = new byte[] {};

        String pwd = null;
        String encrypted = null;
        String decrypted = null;

        encrypted = EncryptionTool.encrypt(pwd, key);
        assertNull(encrypted);

        pwd = "";
        encrypted = EncryptionTool.encrypt(pwd, key);
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        pwd = "guest";
        encrypted = "Q8GMj3ZQ9to=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd, key));
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        pwd = "sa";
        encrypted = "A+Cm08hOnAQ=";
        assertEquals(encrypted, EncryptionTool.encrypt(pwd, key));
        decrypted = EncryptionTool.decrypt(encrypted, key);
        assertEquals(pwd, decrypted);

        encrypted = "lltUbBHM7oA=";
        try {
            //
            // This must fail since the text is encrypted with the default key
            //
            decrypted = EncryptionTool.decrypt(encrypted, key);
            fail();
        } catch (EncryptionException e) {
        }
    }

    /**
     * Test of encrypt method, of class EncryptionTool.
     * @throws java.lang.Exception
     */
    public void testEncryptDecrypt_with_null_key() throws Exception {
        byte[] key = null;

        String pwd = null;

        pwd = "guest";
        try {
            EncryptionTool.encrypt(pwd, key);
            fail();
        } catch (IllegalArgumentException e) {

        }

        try {
            EncryptionTool.decrypt("lltUbBHM7oA=", key);
            fail();
        } catch (IllegalArgumentException e) {

        }
    }

    /**
     * Test of encrypt method, of class EncryptionTool.
     * @throws java.lang.Exception
     */
    public void testEncryptDecrypt_with_long_key() throws Exception {
        byte[] key1 = new byte[] {0x3,0x0,0x8,0x2,0x0,0x1,0xF,0x3,0x0,0x8,0x2,0x0,0x1,0xF,
                                  0x3,0x0,0x8,0x2,0x0,0x1,0xF,0x3,0x0,0x8,0x2,0x0,0x1,0xF};

        byte[] key2 = new byte[] {0x3,0x0,0x8,0x2,0x0,0x1,0xF,0x3,0x0,0x8,0x2,0x0,0x1,0xF,
                                  0x3,0x0,0x8,0x2,0x0,0x1,0xF,0x3,0x0,0x8};
        
        //
        // Since the key is truncated to 24 bytes, the results with key1 and with
        // key2 must be equals
        // 
        String pwd = "guest";

        assertEquals(EncryptionTool.encrypt(pwd, key1), EncryptionTool.encrypt(pwd, key2));
        
        pwd = "sa";
        assertEquals(EncryptionTool.encrypt(pwd, key1), EncryptionTool.encrypt(pwd, key2));
    }

    /**
     * Test of paddingKey method, of class EncryptionTool.
     */
    public void testPaddingKey() {
        
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

}
