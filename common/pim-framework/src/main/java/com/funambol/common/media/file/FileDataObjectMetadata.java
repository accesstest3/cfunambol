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

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The metadata in a file data object.
 *
 * @version $Id$
 */
public class FileDataObjectMetadata  {

    /* File name */
    private String name = null;

    /* Creation date and time (UTC) in the "yyyyMMdd'T'HHmmss'Z'" format */
    private String created = null;

    /* Last-update date and time (UTC) in the "yyyyMMdd'T'HHmmss'Z'" format */
    private String modified = null;

    /* Last-access date and time (UTC) in the "yyyyMMdd'T'HHmmss'Z'" format */
    private String accessed = null;

    private char uploadStatus = UploadStatus.NOT_STARTED;

    /* File attributes */
    private Boolean hidden = null;
    private Boolean system = null;
    private Boolean archived = null;
    private Boolean deleted = null;
    private Boolean writable = null;
    private Boolean readable = null;
    private Boolean executable = null;

    /* File MIME type */
    private String contentType = null;

    /* File size in bytes */
    private Long size = null;

    /* All name-value properties associated to the file data object */
    private Map<String, String> properties = new LinkedHashMap<String, String>();
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getAccessed() {
        return accessed;
    }

    public void setAccessed(String accessed) {
        this.accessed = accessed;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public boolean isHidden() {
        return (hidden != null && Boolean.TRUE.equals(hidden));
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    public Boolean getSystem() {
        return system;
    }

    public boolean isSystem() {
        return (system != null && Boolean.TRUE.equals(system));
    }

    public void setSystem(Boolean system) {
        this.system = system;
    }

    public Boolean getArchived() {
        return archived;
    }

    public boolean isArchived() {
        return (archived != null && Boolean.TRUE.equals(archived));
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public boolean isDeleted() {
        return (deleted != null && Boolean.TRUE.equals(deleted));
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Boolean getWritable() {
        return writable;
    }

    public boolean isWritable() {
        return (writable != null && Boolean.TRUE.equals(writable));
    }

    public void setWritable(Boolean writable) {
        this.writable = writable;
    }

    public Boolean getReadable() {
        return readable;
    }

    public boolean isReadable() {
        return (readable != null && Boolean.TRUE.equals(readable));
    }

    public void setReadable(Boolean readable) {
        this.readable = readable;
    }

    public Boolean getExecutable() {
        return executable;
    }

    public boolean isExecutable() {
        return (executable != null && Boolean.TRUE.equals(executable));
    }

    public void setExecutable(Boolean executable) {
        this.executable = executable;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

     public char getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(char uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public void setUploadStatus(String uploadStatus) {
        setUploadStatus(UploadStatus.fromString(uploadStatus));
    }

    /**
     * @return true if only metadata has been synced for this item, no upload
     * attempt has yet been started
     */
    public boolean isUploadNotStarted() {
        return this.uploadStatus==UploadStatus.NOT_STARTED;
    }

    /**
     * @return true if only metadata has been synced for this item, and an
     * attempt has alredy been started but it wasn't successfully completed
     */

    public boolean isPartiallyUploaded() {
        return this.uploadStatus==UploadStatus.PARTIALLY_UPLOADED;
    }

    /**
     * @return true if only metadata has been synced for this item, the upload
     * was completed and the media has been exported to a third party service
     */
    public boolean isExported() {
        return this.uploadStatus==UploadStatus.EXPORTED;
    }

    /**
     * @return true if only metadata has been synced for this item and the upload
     * of the media content has been successfully completed.
     */
    public boolean isUploaded() {
        return this.uploadStatus==UploadStatus.UPLOADED;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        if (properties != null) {
            this.properties.clear();
            this.properties.putAll(properties);
        } else {
            this.properties = new LinkedHashMap<String, String>();
        }
    }
    
    // ------------------------------------------------------------- Constructor

    public FileDataObjectMetadata() {
    }
    
    public FileDataObjectMetadata(String name) {
        this.name = name;
    }

    /**
     * Returns the string representation of the FileDataObjectMetadata
     * @return the string representation of the FileDataObjectMetadata
     */
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("[name: ").append(name).append("], ");
        result.append("[created: ").append(created).append("], ");
        result.append("[modified: ").append(modified).append("], ");
        result.append("[accessed: ").append(accessed).append("], ");
        result.append("[hidden: ").append(hidden).append("], ");
        result.append("[system: ").append(system).append("], ");
        result.append("[archived: ").append(archived).append("], ");
        result.append("[deleted: ").append(deleted).append("], ");
        result.append("[writable: ").append(writable).append("], ");
        result.append("[readable: ").append(readable).append("], ");
        result.append("[executable: ").append(executable).append("], ");
        result.append("[contentType: ").append(contentType).append("], ");
        result.append("[size: ").append(size).append("], ");        
        result.append("[upload status: ").append(uploadStatus).append("], ");

        result.append("[properties: ");
        Iterator<Entry<String, String>> entries =
            properties.entrySet().iterator();
        while(entries.hasNext()) {
            Entry<String, String> entry = entries.next();
            result.append("[").append(entry.getKey()).append(" - ").append(entry.getValue()).append("], ");
        }
        result.deleteCharAt(result.length() - 1);
        result.append("] ");

        return result.toString();
    }

}
