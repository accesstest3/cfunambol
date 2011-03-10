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

package com.funambol.foundation.util;

import java.util.Random;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.MD5;

/**
 * Manage how to create names for FileDataObject items
 * @version $Id: FileDataObjectNamingStrategy.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FileDataObjectNamingStrategy {

    // --------------------------------------------------------------- Constants

    /** The suffix of the directory related to the file data object file */
    public static final String EXT_FOLDER_SUFFIX = "-ext";

    /** The suffix used for temporary files */
    public static final String TMP_FILE_SUFFIX = ".tmp";


    /** The length of the dir names created in the directory tree */
    private static final int DIR_NAME_LENGTH = 2;

    /**
     * The number of subdirectories composing the user's folder path
     */
    private static final int MAX_FILE_SYS_DIR_DEPTH = 8;

    /** File separator */
    protected static final String FS = "/";

    // -------------------------------------------------------- Member Variables
    /**
     * The depth of the directory path where the files are located
     */
    private final int fileSysDirDepth;

    /**
     * A randum generator
     */
    private static Random random = null;

    static {
        random = new Random();
        random.setSeed(System.currentTimeMillis());
    }

    // ------------------------------------------------------------ Constructors
    /**
     * @param fileSysDirDepth The depth of the directory path where the files
     * are located
     */
    public FileDataObjectNamingStrategy(int fileSysDirDepth) {
        if (fileSysDirDepth < 0 || fileSysDirDepth > MAX_FILE_SYS_DIR_DEPTH) {
            throw new IllegalArgumentException("Invalid File System Dir Depth: " +
                    fileSysDirDepth);
        }
        this.fileSysDirDepth = fileSysDirDepth;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Generates a new random local file name for the file corresponding to the
     * file data object item
     * @param itemId The item id
     * @return
     */
    public String createFileName(String itemId) {
        StringBuilder itemKey = new StringBuilder();
        itemKey.append(getRandomString());
        if (itemId != null && itemId.length() > 0) {
               itemKey.append("-")
               .append(itemId);
        }
        return itemKey.toString();
    }

    /**
     * Create the name for the temporary file.
     *
     * @param filename the tmp filename
     * @return the name for the temporary file
     */
    public String createTmpFileName(String filename) {
        String tmpName = createFileName(filename) + TMP_FILE_SUFFIX;
        return tmpName;
    }

    /**
     * Create the name for the temporary file
     * @return the name for the temporary file
     */
    public String createTmpFileName() {
        return createTmpFileName(null);
    }

    /**
     * Given a user name creates the corresponding path.
     * For example given the user "test-user1" it returns the path
     * "om/fo/ly/hx/ko/ye/ff/ej/test-user1".
     *
     * @param username the username
     * @return the relative path corresponding to the user's folder
     */
    public String getUserFolderPath(String username) {
        return computeUserFolderPath(username, fileSysDirDepth);
    }

    /**
     * Returns the path on the storage provider of the file
     * item
     * @param username
     * @param localFilename
     * @return
     */
    public String getFilePath(String username, String localFilename) {
        StringBuilder sbPrefix =
            new StringBuilder(computeUserFolderPath(username, fileSysDirDepth));
        sbPrefix.append(FS).append(localFilename);

        return sbPrefix.toString();
    }

    /**
     * Returns the path on the storage provider of the ext folder.
     *
     * @param username the username
     * @param localFileName The local file name of the item
     * @return
     */
    public String getEXTFolderPath(String username, String localFileName) {
        return getFilePath(username, getEXTFolderName(localFileName));
    }

    /**
     * Given the file name computes the corresponding ext folder name
     * @param fileName
     * @return
     */
    public String getEXTFolderName(String fileName) {
        return fileName + EXT_FOLDER_SUFFIX;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Given a user name creates the corresponding path.
     * For example given the user "test-user1" it returns the path
     * "om/fo/ly/hx/ko/ye/ff/ej/dGVzdC11c2VyMQ=="
     * @param userName
     * @param fileSysDirDepth
     * @return the relative path corresponding to the user's folder
     */
    private String computeUserFolderPath(String userName, int fileSysDirDepth) {

        String hiddenUserName = hide(userName);

        byte[] digest = MD5.digest(hiddenUserName.getBytes()); // 16 byte
        StringBuilder path = new StringBuilder();
        convertToLowerChar(digest);
        for (int j = 0; j < digest.length && j < (fileSysDirDepth * DIR_NAME_LENGTH); ++j) {
            if (j > 0 && j % DIR_NAME_LENGTH == 0) {
                path.append(FS);
            }
            path.append((char) digest[j]);
        }
        if (path.length() > 0) {
            path.append(FS);
        }
        path.append(hiddenUserName);

        return path.toString();
    }

    private void convertToLowerChar(byte[] digest) {
        int first = 97;
        int last = 122;
        int i;
        for (int j = 0; j < digest.length; ++j) {
            i = digest[j] & 0x000000ff;
            if ((i < first) || (i > last)) {
                digest[j] = (byte) (first + (i % (last-first)));
            }
        }
    }

    /**
     * Generates an alphanumeric random string of 13 characters.
     * 
     * @return the generated random string
     */
    private String getRandomString() {
        // 36 = 10 digits + 26 letters
        String randomString = Long.toString(Math.abs(random.nextLong()), 36); 
        randomString = StringUtils.leftPad(randomString, 13, '0');

        return randomString;
    }

    /**
     * Hides a string behind it's base64 hash
     *
     * @param valueToHide the string to convert in b64
     * @return the converted string
     */
    private String hide(String valueToHide) {
        String result = new String(Base64.encode(valueToHide.getBytes()));
        return result.replace("/", "_");
    }

}
