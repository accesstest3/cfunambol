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

package com.funambol.common.media.file.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.CRC32;

/**
 * Represents a decoder for file data object body part
 * @version $Id: FileDataObjectDecoder.java 33362 2010-01-21 10:37:12Z testa $
 */
public abstract class FileDataObjectDecoder {

    protected OutputStream stream = null;
    protected CRC32 crc32 = null;

    public FileDataObjectDecoder(OutputStream bodyOutputStream) {
        this.stream = bodyOutputStream;
        this.crc32 = new CRC32();
    }
    
    public void setBodyOutputStream(OutputStream newBodyOutputStream) {
        this.stream = newBodyOutputStream;
    }

    public abstract void write(byte b) throws IOException;

    /**
     * Returns current CRC value
     * @return the current CRC value
     */
    public long getCRC() {
        return crc32.getValue();
    }

    /**
     * Updates crc value with the given byte[]
     * @param value the byte[] to use updating crc
     */
    protected void updateCRC(byte[] value) {
        crc32.update(value);
    }
}
