/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

package com.funambol.common.media.file;

import java.io.File;

/**
 * The body in a file data object.
 *
 * @version $Id$
 */
public class FileDataObjectBody {

    // -------------------------------------------------------------- Properties

    /**
     * The data file
     */
    private File bodyFile = null;

    /**
     * Gets the data file
     *
     * @return the body. It the FileDataObject doesn't have the boby element
     * than the getBodyFile() method returns null.
     */
    public File getBodyFile() {
        return bodyFile;
    }

    public void setBodyFile(File bodyFile) {
        this.bodyFile = bodyFile;
    }

    
    /**
     * The content CRC
     */
    private long crc = FileDataObject.CRC_NOT_DEFINED;

    /**
     * returns the content CRC
     * @return the content CRC
     */
    public long getCrc() {
        return this.crc;
    }

    /**
     * Sets the CRC
     * @param crc the CRC to set
     */
    public void setCrc(long crc) {
        this.crc = crc;
    }

    // ------------------------------------------------------------ Constructors

    public FileDataObjectBody() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the size of the file. Returns 0 if the file is empty.
     * @postcondition return value is greater or equal to zero
     * @return the size of the file or 0 if the file is empty or not defined.
     */
    public long getSize() {
        if (this.bodyFile == null) {
            return FileDataObject.ACTUAL_SIZE_NOT_DEFINED;
        }
        return bodyFile.length();
    }

    // --------------------------------------------------------- Private Methods

}
