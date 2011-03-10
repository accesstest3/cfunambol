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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is the dictionary of supported MimeTypes.
 *
 * @version $Id: MimeTypeDictionary.java 36089 2010-10-19 14:33:59Z luigiafassina $
 */
public class MimeTypeDictionary {
    public static final String TEXT_PLAIN      =  "text/plain";
    public static final String APPLICATION_ZIP =  "application/zip";
    public static final String APPLICATION_Z   =  "application/x-compress";
    public static final String APPLICATION_TGZ =  "application/x-compressed";
    public static final String APPLICATION_TAR =  "application/x-tar";

    public static final String APPLICATION_ZIP_EXTENSION =  "zip";
    public static final String APPLICATION_Z_EXTENSION   =  "z";
    public static final String APPLICATION_TGZ_EXTENSION =  "tgz";
    public static final String APPLICATION_TAR_EXTENSION =  "tar";

    private static final Map<String,String> mymeTypeMappings  = new HashMap<String, String>();
    private static final Set<String> compressibleContentTypes = new HashSet<String>();
    private static final Set<String> compressedContentTypes   = new HashSet<String>();

    static {
        addMymeType(APPLICATION_TGZ, APPLICATION_TGZ_EXTENSION);
        addMymeType(APPLICATION_Z,   APPLICATION_Z_EXTENSION);
        addMymeType(APPLICATION_ZIP, APPLICATION_ZIP_EXTENSION);
        addMymeType(APPLICATION_TAR, APPLICATION_TAR_EXTENSION);
    }

    private static void addMymeType(String mymeType, String extension) {
        addMymeType(mymeType, extension, false);
    }

    private static void addMymeType(String mymeType, String extension, boolean compressible) {
        mymeTypeMappings.put(mymeType, extension);
        if(compressible) {
            compressibleContentTypes.add(mymeType);
        } else {
            compressedContentTypes.add(mymeType);
        }
    }

    public static String getExtension(String contentType) {
        return mymeTypeMappings.get(contentType);
    }

    /**
     * @param contentType the input content type
     * @return true if the content type is text/plain
     */
    public static boolean isTextPlain(String contentType) {
        return TEXT_PLAIN.equals(contentType);
    }

    public static boolean isCompressible(String contentType) {
        return TEXT_PLAIN.equals(contentType);
    }

    public static boolean isCompressed(String contentType) {
        return compressedContentTypes.contains(contentType);
    }

    static boolean isExtensionKnown(String contentType) {
        return mymeTypeMappings.containsKey(contentType);
    }

}
