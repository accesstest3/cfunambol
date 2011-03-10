/* 
 * Copyright (c) 2004 Harrie Hazewinkel. All rights reserved.
 */

/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.framework.tools.codec;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.BitSet;

/**
 * <p>
 * Codec for the Quoted-Printable section of <a href="http://www.ietf.org/rfc/rfc1521.txt">RFC 1521 </a>.
 * </p>
 * <p>
 * The Quoted-Printable encoding is intended to represent data that largely consists of octets
 * that correspond to printable characters in the ASCII character set. It encodes the data in
 * such a way that the resulting octets are unlikely to be modified by mail transport. If the
 * data being encoded are mostly ASCII text, the encoded form of the data remains largely
 * recognizable by humans. A body which is entirely ASCII may also be encoded in Quoted-Printable
 * to ensure the integrity of the data should the message pass through a character- translating,
 * and/or line-wrapping gateway.
 * </p>
 * @see <a href="http://www.ietf.org/rfc/rfc1521.txt"> RFC 1521 MIME (Multipurpose Internet Mail Extensions) Part One:
 *          Mechanisms for Specifying and Describing the Format of Internet Message Bodies </a>
 *
 *
 * @version $Id: QuotedPrintableCodec.java,v 1.2 2007-11-28 11:13:37 nichele Exp $
 */
public class QuotedPrintableCodec implements CodecInterface {
    private static final byte ESCAPE_CHAR = '=';
    private static final byte TAB = 9;
    private static final byte SPACE = 32;
    /**
     * BitSet of printable characters as defined in RFC 1521.
     */
    private static final BitSet SAFE_CHARS = new BitSet(256);
    static {
        for (int i = 33; i <= 60; i++) {
            SAFE_CHARS.set(i);
        }
        for (int i = 62; i <= 126; i++) {
            SAFE_CHARS.set(i);
        }
        SAFE_CHARS.set(TAB);
        SAFE_CHARS.set(SPACE);
    }
    private final static String DEFAULT_CHARSET_NAME = "UTF-8";
    
    private String charSetName;

    /**
     * Default constructor (using the default character set, ie UTF-8).
     */
    public QuotedPrintableCodec() {
        this.charSetName = DEFAULT_CHARSET_NAME;
    }
    
    /**
     * Creates a new instance of QuotedPrintableCodec based on a given charSet.
     *
     * @param charSetName the name of the character set to be used by the codec
     */
    public QuotedPrintableCodec(String charSetName) {
        this.charSetName = charSetName;
    }
    
    public String getCharSetName() {
        return charSetName;
    }
    
    public void setCharSetName(String charSetName) {
        this.charSetName = charSetName;
    }
    
    /**
     * Encodes byte into its quoted-printable representation.
     * @param b Byte to encode
     * @param buffer The buffer to write to
     */
    private static final void encode(int b, ByteArrayOutputStream buffer) {
        buffer.write(ESCAPE_CHAR);
        char hex1 = Character.toUpperCase(Character.forDigit((b >> 4) & 0xF, 16));
        char hex2 = Character.toUpperCase(Character.forDigit(b & 0xF, 16));
        buffer.write(hex1);
        buffer.write(hex2);
    }

    /**
     * Encodes an array of bytes into an array of quoted-printable 7-bit characters.
     * @param bytes Array of bytes to be encoded
     * @return Array of bytes containing quoted-printable representation of the input
     */
    public final byte[] encode(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b < 0) {
                b = 256 + b;
            }
            if (SAFE_CHARS.get(b)) {
                buffer.write(b);
            } else {
                encode(b, buffer);
            }
        }
        return buffer.toByteArray();
    }
    /**
     * Encodes a String into a String of quoted-printable 7-bit characters.
     * @param bytes Array of bytes to be encoded
     * @return Array of bytes containing quoted-printable representation of the input
     */
    public final String encode(String str) throws UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return new String(encode(str.getBytes(charSetName)));
    }


    /**
     * Decodes an array quoted-printable characters into an array of original bytes.
     * @param bytes Array of quoted-printable characters
     * @return array of decoded bytes
     * @throws CodecException Thrown if quoted-printable decoding is unsuccessful
     */
    public final byte[] decode(byte[] bytes) throws CodecException {
        if (bytes == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        for (int i = 0; i < bytes.length; i++) {
            int b = bytes[i];
            if (b == ESCAPE_CHAR) {
                try {
                    if (bytes[i+1] == '\n') {
                        // It is a soft line break '=' LF
                        i++;
                    } else if ((bytes[i+1] == '\r') && (bytes[i+2] == '\n')) {
                        // It is a soft line break '=' CR LF
                        i+=2;
                    } else {
                        // It is not a soft line break
                        int u = Character.digit((char)bytes[++i], 16);
                        int l = Character.digit((char)bytes[++i], 16);
                        if (u == -1 || l == -1) {
                            throw new CodecException("Invalid quoted-printable encoding");
                        }
                        buffer.write((char) ((u << 4) + l));
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    throw new CodecException("Invalid quoted-printable encoding");
                }
            } else {
                buffer.write(b);
            }
        }
        return buffer.toByteArray();
    }
    /**
     * Decodes a String of quoted-printable 7-bit characters into a Java String.
     * @param bytes Array of bytes to be decoded
     * @return Array of bytes containing quoted-printable representation of the input
     */
    public final String decode(String str) throws CodecException, UnsupportedEncodingException {
        if (str == null) {
            return null;
        }
        return new String(decode(str.getBytes()), charSetName);
    }

}
