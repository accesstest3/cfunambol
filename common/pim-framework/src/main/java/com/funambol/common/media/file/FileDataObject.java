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
import java.util.Map;

/**
 * Represents a file data object.
 * Collaborators: FileDataObjectBody and FileDataObjectMetadata
 * Invariant conditions:
 *   body != null
 *   metadata != null
 * The body variable is always not null, even if the FileDataObjectMetadata
 * represent a file data object with no body (for example an update only of the
 * metadata).
 * @version $Id:FileDataObject.java 30767 2009-04-17 12:26:26Z testa $
 */
public class FileDataObject {

    // --------------------------------------------------------------- Constants

    public static final long CRC_NOT_DEFINED = -1;
    public static final long ACTUAL_SIZE_NOT_DEFINED = -1;

    // -------------------------------------------------------------- Properties

    /**
     * File body
     */
    private FileDataObjectBody body;

    /**
     * @param body The body of the file data object. If null an empty body is
     * created
     */
    public void setBody(FileDataObjectBody body) {
        if (body == null) {
            body = new FileDataObjectBody();
        }
        this.body = body;
    }

    /**
     * @deprecated Use specific property setters instead.
     */
    public FileDataObjectBody getBody() {
        return body;
    }

    /**
     * File metadata
     * @invariant metadata != null
     */
    private FileDataObjectMetadata metadata;

    /**
     * @deprecated Use specific property setters instead.
     */
    public FileDataObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     *
     * @param metadata The metadata of the file data object.
     */
    public void setMetadata(FileDataObjectMetadata metadata) {
        if (metadata == null) {
            metadata = new FileDataObjectMetadata();
        }
        this.metadata = metadata;
    }

    //------------------------------------------------------------- Constructors

    /**
     * Creates an empty file data object.
     */
    public FileDataObject () {
        metadata = new FileDataObjectMetadata();
        body = new FileDataObjectBody();
    }


    /**
     * Creates an empty file data object.
     * @param metadata The FileDataObjectMetadata holding the file data object
     * metadata
     * @param body The FileDataObjectBody holding the file
     */
    public FileDataObject (FileDataObjectMetadata metadata, FileDataObjectBody body) {
        setMetadata(metadata);
        setBody(body);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Gets the data file
     *
     * @return the body file. It the FileDataObject doesn't have the boby element
     * than the getBodyFile() method returns null.
     */
    public File getBodyFile() {
        return this.body.getBodyFile();
    }

    /**
     * Set the body file
     * @param bodyFile
     */
    public void setBodyFile(File bodyFile) {
        this.body.setBodyFile(bodyFile);
    }

    /**
     * returns the content CRC
     * @return the content CRC
     */
    public long getCrc() {
        return this.body.getCrc();
    }

    /**
     * Sets the CRC
     * @param crc the CRC to set
     */
    public void setCrc(long crc) {
        this.body.setCrc(crc);
    }

    public Long getSize() {
        return this.metadata.getSize();
    }

    public void setSize(Long value)
    {
        this.metadata.setSize(value);
    }

    public void setName(String name) {
        this.metadata.setName(name);
    }

    public String getName() {
        return this.metadata.getName();
    }

    public String getCreated() {
        return this.metadata.getCreated();
    }

    public void setCreated(String created) {
        this.metadata.setCreated(created);
    }

    public String getModified() {
        return this.metadata.getModified();
    }

    public void setModified(String modified) {
        this.metadata.setModified(modified);
    }

    public String getAccessed() {
        return this.metadata.getAccessed();
    }

    public void setAccessed(String accessed) {
        this.metadata.setAccessed(accessed);
    }

    public Boolean getHidden() {
        return this.metadata.getHidden();
    }

    public boolean isHidden() {
        return this.metadata.isHidden();
    }

    public void setHidden(Boolean hidden) {
        this.metadata.setHidden(hidden);
    }

    public Boolean getSystem() {
        return this.metadata.getSystem();
    }

    public boolean isSystem() {
        return this.metadata.isSystem();
    }

    public void setSystem(Boolean system) {
        this.metadata.setSystem(system);
    }

    public Boolean getArchived() {
        return this.metadata.getArchived();
    }

    public boolean isArchived() {
        return this.metadata.isArchived();
    }

    public void setArchived(Boolean archived) {
        this.metadata.setArchived(archived);
    }

    public Boolean getDeleted() {
        return this.metadata.getDeleted();
    }

    public boolean isDeleted() {
        return this.metadata.isDeleted();
    }

    public void setDeleted(Boolean deleted) {
        this.metadata.setDeleted(deleted);
    }

    public Boolean getWritable() {
        return this.metadata.getWritable();
    }

    public boolean isWritable() {
        return this.metadata.isWritable();
    }

    public void setWritable(Boolean writable) {
        this.metadata.setWritable(writable);
    }

    public Boolean getReadable() {
        return this.metadata.getReadable();
    }

    public boolean isReadable() {
        return this.metadata.isReadable();
    }

    public void setReadable(Boolean readable) {
        this.metadata.setReadable(readable);
    }

    public Boolean getExecutable() {
        return this.metadata.getExecutable();
    }

    public boolean isExecutable() {
        return this.metadata.isExecutable();
    }

    public void setExecutable(Boolean executable) {
        this.metadata.setExecutable(executable);
    }

    public String getContentType() {
        return this.metadata.getContentType();
    }

    public void setContentType(String contentType) {
        this.metadata.setContentType(contentType);
    }

    public boolean hasBodyFile()
    {
        return null != getBodyFile();
    }

    /**
     * Compute the file size if the size is not defined in the metadata sent by
     * the client. If the client sends the size than it is compared with the
     * actual one.
     * @return True if the declared size match the actual size or the size is
     * not declared or the actual size is zero.
     * False if the actual size is not zero and the size is declared but does
     * not match the actual one.
     * @sideeffect If the size is not declared it is set to the actual size
     */
    public boolean checkSize() {

        boolean declaredSizeMatchActualSize;
        Long actualSize = this.body.getSize();
        Long declaredSize = getSize();

        if (declaredSize == null) {
            if (!actualSize.equals(ACTUAL_SIZE_NOT_DEFINED)) {
                this.setSize(actualSize);
                declaredSizeMatchActualSize = true;
            } else {
                declaredSizeMatchActualSize = false;
            }
        } else if (!declaredSize.equals(actualSize)) {
            //ok only if body size is not defined
            declaredSizeMatchActualSize = actualSize.equals(ACTUAL_SIZE_NOT_DEFINED);
        } else {
            declaredSizeMatchActualSize = true;
        }

        return declaredSizeMatchActualSize;
    }


    public char getUploadStatus() {
        return this.metadata.getUploadStatus();
    }

    public void setUploadStatus(char uploadStatus) {
        this.metadata.setUploadStatus(uploadStatus);
    }

    public void setUploadStatus(String uploadStatus) {
        this.metadata.setUploadStatus(uploadStatus);
    }

    public boolean isPartiallyUploaded() {
        return this.metadata.isPartiallyUploaded();
    }

     public boolean isUploadNotStarted() {
        return this.metadata.isUploadNotStarted();
    }

     public boolean isExported() {
        return this.metadata.isExported();
    }

     public boolean isUploaded() {
        return this.metadata.isUploaded();
    }

    public Map<String, String> getProperties() {
        return this.metadata.getProperties();
    }

    public void setProperties(Map<String, String> properties) {
        this.metadata.setProperties(properties);
    }

    /**
     * Returns the string representation of the FileDataObject
     * @return the string representation of the FileDataObject
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder(super.toString());
        result.append(": ");
        result.append(this.metadata.toString());
        return result.toString();
    }

}
