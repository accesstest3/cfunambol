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

import java.security.SecureRandom;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * This class groups utility methods for MD5 computation.
 * <p>
 *
 * NOTE: when this class is first loaded, it creates and initializes a random
 * generator that can be used to create random keys. We create and initialize
 * the generator onece, because it can be a time consuming process. Better
 * policies might be implemented in the future.
 *
 *
 *
 * @version $Id: MD5.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class MD5 {

    // ------------------------------------------------------------ Private data
    private static SecureRandom random = null;

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of MD5 */
    protected MD5() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns a new nonce to be used for MD5 authentication. The nonce bytes
     * are guaranteed to be in the printable range from ascii 32 to 128.
     *
     * @return a new 16 bytes long nonce
     */
    public static byte[] getNextNonce() {
        byte[] nextNonce = new byte[16];
        random.nextBytes(nextNonce);

        int i;
        for (int j=0; j<nextNonce.length; ++j) {
            i = nextNonce[j] & 0x000000ff;
            if ((i<32) || (i>128)) {
                nextNonce[j] = (byte)(32 + (i % 64));
            }
        }

        return nextNonce;
    }

    /**
     * MD5ies the given content
     *
     * @param data the data to be digested
     *
     */
    public static byte[] digest(byte[] data) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to create MessageDigest", e);
        }
        return md.digest(data);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates and initialize the random generator. Called ad class loading
     * time.
     */
    private static void randomGeneratorInit()
    throws java.security.NoSuchAlgorithmException {
        random = SecureRandom.getInstance("SHA1PRNG");
    }

    // ------------------------------------------------------------- Static code

    static {
        try {
            randomGeneratorInit();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
