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
package com.funambol.foundation.engine.source;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import java.sql.Timestamp;

import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.SourceUtils;
import com.funambol.framework.tools.beans.LazyInitBean;

/**
 * This is a AbstractFileSystemSyncSource to sync all types of files.
 *
 * @version $Id: FileSystemSyncSource.java,v 1.1.1.1 2008-03-20 21:38:33 stefano_fornari Exp $
 */
public class FileSystemSyncSource extends AbstractFileSystemSyncSource
    implements SyncSource, Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants
    public static final String DATABASE_HEADER = "FileSystemSyncSource file database";


    // ------------------------------------------------------------ Constructors

    public FileSystemSyncSource() {
        this(null, null);
    }

    public FileSystemSyncSource(String name, String sourceDirectory) {
        super(name, sourceDirectory);
    }

    /**
     * The name defaults to the directory name
     */
    public FileSystemSyncSource(String sourceDirectory) {
        this(new File(sourceDirectory).getName(), sourceDirectory);
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the crc of the given content
     * @param value the byte array used in the crc computation
     * @param contentDescription a description of the content (used in exception
     *        message)
     * @return long
     */
    protected long getItemCRC(byte[] content, String contentDescription) {
        return SourceUtils.computeCRC(content);
    }

    // ------------------------------------------------------- Protected Methods

    /**
     * @see AbstractFileSystemSyncSource
     */
    protected SyncItem setSyncItem(SyncItem syncItem,
                                   File file) throws SyncSourceException {
        try {
            String fileName = file.getName();

            byte[] itemContent = syncItem.getContent();

            if (itemContent == null) {
                itemContent = new byte[0];
            }

            if (encode && itemContent.length > 0) {
                IOTools.writeFile(Base64.decode(itemContent), file);
            } else {
                IOTools.writeFile(itemContent, file);
            }

            Timestamp t = syncItem.getTimestamp();

            file.setLastModified(t.getTime());

            SyncItem newSyncItem =
                    new SyncItemImpl(this,
                                     fileName,
                                     null,
                                     SyncItemState.NEW,
                                     syncItem.getContent(),
                                     syncItem.getFormat(),
                                     syncItem.getType(),
                                     syncItem.getTimestamp()
                    );

            return newSyncItem;
        } catch (IOException e) {
            throw new SyncSourceException("Error setting the item "
                                          + syncItem
                                          , e
            );
        }
    }

    /**
     * @see AbstractFileSystemSyncSource
     */
    protected SyncItem getSyncItem(File file, char state) throws SyncSourceException {

        SyncItem syncItem = new SyncItemImpl(this, file.getName(), state);

        syncItem.setType(this.getInfo().getPreferredType().getType());

        try {

            byte[] fileContent = IOTools.readFileBytes(file);
            if (encode) {
                fileContent = Base64.encode(fileContent);
                syncItem.setFormat(FORMAT_BASE64);
            }
            syncItem.setContent(fileContent);

        } catch (IOException ioe) {
            throw new SyncSourceException("Error reading "
                                          + file.getName()
                                          , ioe
                    );

        }
        return syncItem;
    }

}
