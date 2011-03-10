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
package com.funambol.foundation.items.model;

import com.funambol.common.media.file.FileDataObject;

/**
 * Class that represent an entry in the file data object table.
 * @version $Id: FileDataObjectWrapper.java 36000 2010-09-21 15:20:52Z luigiafassina $
 */
public class FileDataObjectWrapper extends EntityWrapper {

    // -------------------------------------------------------------- Properties

    /** 
     * Wrapped file data object. 
     * @invariant fileDataObject != null
     */
    private FileDataObject fileDataObject;
    
    public FileDataObject getFileDataObject() {
        if(fileDataObject==null) {
            throw new IllegalStateException("FileDataObject attribute must not be null.");
        }
        return fileDataObject;
    }

    /** The name of the file on the server file system. */
    private String localName;

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    /** The name of the file on the server file system. */
    private Long sizeOnStorage;

    /**
     * Returns the actual size of the file in the storage system.
     * @postcondition return value is greater or equal to zero
     * @return the size of the file, 0 if the file is empty or ACTUAL_SIZE_NOT_DEFINED if the file is not defined.
     */
    public Long getSizeOnStorage() {
        return sizeOnStorage;
    }

    public void setSizeOnStorage(Long sizeOnStorage) {
        this.sizeOnStorage = sizeOnStorage;
    }

    /**
     *
     * @return the total number of bytes that the item takes on the file system
     * once it's completely uploaded. It may differ from the storage size.
     * Consider the scenario in which the item has been partially uploaded.
     */
    public Long getSize() {
        return getFileDataObject().getSize();
    }


    /**
     * this method allows to check whether this object wraps a complete item,
     * i.e it contains both metadata and item body.
     * It can be used to check if an incoming item has been upload using only syncml
     * or if an http upload will be performed later.
     *
     * @return true in the case this item wraps a complete item (if it represents an
     * incoming object, all the item has been uploaded via syncml) false if it
     * doesn't contain item body (if it represents an incoming object, the body
     * will be uploaded later within an http session)
     */
    public boolean isComplete() {
        return getFileDataObject().hasBodyFile();
    }

    // ------------------------------------------------------------ Constructors

    public FileDataObjectWrapper(String id, String userId,
            FileDataObject fileDataObject) {
        super(id, userId);
        if (fileDataObject == null) {
            throw new IllegalArgumentException("The file data object must be " +
                    "not null");
        }
        this.fileDataObject = fileDataObject;
    }
}
