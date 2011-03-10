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
package com.funambol.framework.tools.encryption;

import java.lang.reflect.Method;

import java.security.spec.KeySpec;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import com.funambol.framework.tools.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Provides usefull methods to encrypt and to decrypt a text
 * @version $Id: EncryptionTool.java,v 1.3 2008-05-15 05:53:59 nichele Exp $
 */
public class EncryptionTool {

    /**
     * The encryption algo
     */
    private static final String ALGORITHM     = "DESEde";
    /**
     * The value used to pad the key
     */
    private static final byte   PADDING_VALUE = 0x00;
    /**
     * The length of the key
     */
    private static final byte   KEY_LENGTH    = 24;

    /**
     * The default key
     */
    private static final byte[] DEFAULT_KEY;

    /**
     * The default key as string
     */
    private static final String DEFAULT_STRING_KEY = "Omnia Gallia in tres partes divida est";

    private static FunambolLogger logger = FunambolLoggerFactory.getLogger("funambol.framework.encryption");
    //
    // static initialization code to define the default key
    //
    static {
        logger.info("Initializing encryption key");
        String d = DEFAULT_STRING_KEY;
        byte[] key = d.getBytes();
        Class  cls = null;
        Object c   = null;
        Method m   = null;

        boolean providerClassFound  = false;
        boolean providerMethodFound = false;

        try {
            cls = Class.forName("com.funambol.framework.tools.encryption.EncryptionKeyProvider");
            c   = cls.newInstance();
            providerClassFound = true;
        } catch (Exception e){
            if (logger.isInfoEnabled()) {
                logger.info("EncryptionKeyProvider not found, using default key");
            }
        }

        if (providerClassFound) {
            try {
                Class parameters[] = null;
                m = cls.getMethod("get", parameters);
                providerMethodFound = true;
            } catch (Exception e){
                if (logger.isWarningEnabled()) {
                    logger.warn("EncryptionKeyProvider is not a valid key provider, using default key", e);
                }
            }
        }

        if (providerMethodFound) {
            byte[] retval = null;
            try {
                Object arglist[] = null;
                Object retobj = m.invoke(c, arglist);
                retval = (byte[])retobj;
                key = retval;
                if (logger.isInfoEnabled()) {
                    logger.info("EncryptionKeyProvider found. Using strong encryption");
                }
            } catch (Exception e){
                if (logger.isWarningEnabled()) {
                    logger.warn("Unable to retrieve the encryption key using the found EncryptionKeyProvider, using default key", e);
                }
            }
        }

        DEFAULT_KEY = paddingKey(key, KEY_LENGTH, PADDING_VALUE);
    }

    /**
     * Encrypts a byte array.
     * @param plainBytes the bytes to encrypt
     * @param encryptionKey the key to use in the encryption phase
     * @return the encrypted bytes.
     * @exception EncryptionException if an error occurs
     */
    public static byte[] encrypt(byte[] plainBytes,
                                 byte[] encryptionKey) throws EncryptionException {

        if (plainBytes == null) {
            return null;
        }
        if (encryptionKey == null) {
            throw new IllegalArgumentException("The key can not be null");
        }

        byte[] cipherBytes = null;
        try {
            encryptionKey = paddingKey(encryptionKey, KEY_LENGTH, PADDING_VALUE);

            KeySpec keySpec = new DESedeKeySpec(encryptionKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            cipherBytes = cipher.doFinal(plainBytes);
        } catch (Exception e) {
             throw new EncryptionException("Error encrypting", e);
        }

        return cipherBytes;
    }

    /**
     * Encrypts a byte array.
     * @return the encrypted bytes.
     * @param plainBytes the bytes to encrypt
     * @exception EncryptionException if an exception occurs
     */
    public static byte[] encrypt(byte[] plainBytes) throws EncryptionException {
        return encrypt(plainBytes, DEFAULT_KEY);
    }

    /**
     * Encrypts a byte array.
     * @return the encrypted bytes.
     * @param plainText the plain text
     * @param key the key to use
     * @exception EncryptionException if an exception occurs
     */
    public static String encrypt(String plainText, byte[] key) throws EncryptionException {
        if (plainText == null) {
            return null;
        }
        if (key == null) {
            throw new IllegalArgumentException("The key can not be null");
        }

        return new String(Base64.encode(encrypt(plainText.getBytes(), key)));
    }

    /**
     * Encrypts a byte array.
     * @param plainText the plain text to encrypt.
     * @return the encrypted bytes.
     * @exception EncryptionException if an exception occurs
     */
    public static String encrypt(String plainText) throws EncryptionException {
        return encrypt(plainText, DEFAULT_KEY);
    }

    /**
     * Decrypt a text.
     * @return the original plain text
     * @param encryptedBytes the encrypted text
     * @param encryptionKey the key to used decrypting
     * @exception EncryptionException if an error occurs
     */
    public static byte[] decrypt(byte[] encryptedBytes,
                                 byte[] encryptionKey) throws EncryptionException {

        if (encryptedBytes == null) {
            return null;
        }
        if (encryptionKey == null) {
            throw new IllegalArgumentException("The key can not be null");
        }
        byte[] plainBytes = null;
        try {
            encryptionKey = paddingKey(encryptionKey, KEY_LENGTH, PADDING_VALUE);

            KeySpec keySpec = new DESedeKeySpec(encryptionKey);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);

            SecretKey key = keyFactory.generateSecret(keySpec);
            cipher.init(Cipher.DECRYPT_MODE, key);

            plainBytes  = cipher.doFinal(encryptedBytes);
        } catch (Exception e) {
             throw new EncryptionException("Error decrypting", e);
        }

        return plainBytes;
    }

    /**
     * Decrypt a text.
     * @return the original plain text
     * @param encryptedBytes the encrypted bytes
     * @exception EncryptionException if an error occurs
     */
    public static byte[] decrypt(byte[] encryptedBytes) throws EncryptionException {
        return decrypt(encryptedBytes, DEFAULT_KEY);
    }

    /**
     * Decrypt a text.
     * @return the original, plain text password
     * @param encryptedText the encrypted text
     * @exception EncryptionException if an error occurs
     */
    public static String decrypt(String encryptedText) throws EncryptionException {
        if (encryptedText == null) {
            return null;
        }
        return new String(decrypt(Base64.decode(encryptedText), DEFAULT_KEY));
    }

    /**
     * Decrypt a text.
     * @return the original, plain text password
     * @param encryptedText if the encrypted text
     * @param key the key to use decrypting
     * @exception EncryptionException if an error occurs
     */
    public static String decrypt(String encryptedText, byte[] key) throws EncryptionException {
        if (encryptedText == null) {
            return null;
        }
        return new String(decrypt(Base64.decode(encryptedText), key));
    }

    //----------------------------------------------------------- Private Method

    /**
     * Returns a padded copy of the given bytes array using to the given lenght using the given value.
     * @param b the bytes array
     * @param len the required length
     * @param paddingValue the value to use padding the given bytes array
     * @return the padded copy of the given bytes array
     */
    private static byte[] paddingKey(byte[] b, int len, byte paddingValue) {

        byte[] newValue = new byte[len];

        if (b == null) {
            //
            // The given byte[] is null...returning a new byte[] with the required
            // length
            //
            return newValue;
        }

        if (b.length >= len) {
            System.arraycopy(b, 0, newValue, 0, len);
            return newValue;
        }

        System.arraycopy(b, 0, newValue, 0, b.length);
        Arrays.fill(newValue, b.length, len, paddingValue);
        return newValue;

    }
}
