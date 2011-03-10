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
package com.funambol.foundation.engine.source;

import java.io.File;
import java.io.Serializable;

import java.util.List;
import java.util.Map;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.tools.SourceUtils;
import com.funambol.framework.tools.beans.LazyInitBean;

/**
 * This is an abstract syncsource for the SIFXXX syncsource.
 *
 * @version $Id: AbstractSIFSyncSource.java,v 1.1.1.1 2008-03-20 21:38:32 stefano_fornari Exp $
 */
public abstract class AbstractSIFSyncSource extends AbstractFileSystemSyncSource
    implements SyncSource, Serializable, LazyInitBean {

    // -------------------------------------------------------------- Properties

    // The list of properties to compare in order to find the twins
    protected List targetsToCompare = null;

    public void setTargetsToCompare(List list) {
        this.targetsToCompare = list;
    }

    public List getTargetsToCompare() {
        return this.targetsToCompare;
    }


    // ------------------------------------------------------------ Constructors

    public AbstractSIFSyncSource() {
        this(null, null);
    }

    public AbstractSIFSyncSource(String name, String sourceDirectory) {
        super(name, sourceDirectory);
    }

    /**
     * The name defaults to the directory name
     */
    public AbstractSIFSyncSource(String sourceDirectory) {
        this(new File(sourceDirectory).getName(), sourceDirectory);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the CRC value for the given content using the values of the fields
     * contained in </code>targetsToCompare</code>
     *
     * @param value the byte array used in the crc computation
     * @param contentDescription a description of the content (used in exception
     *        message)
     * @return CRC of value
     **/
    public long getItemCRC(byte[] content, String contentDescription) throws SyncSourceException {

        Map hashMapItem = null;

        try {
            hashMapItem = SourceUtils.xmlToHashMap(new String(content));
        } catch (Exception e) {
            throw new SyncSourceException("Error creating the HashMap for " +
                                          "crc computation (" + contentDescription + ")", e);
        }
        String[] values = null;
        if (targetsToCompare != null) {
            int size = targetsToCompare.size();
            values = new String[size];
            String target   = null;
            String objIn    = null;
            for (int i=0; i<size; i++) {
                target = (String)targetsToCompare.get(i);

                objIn    = (String)hashMapItem.get(target);

                if (objIn == null) {
                    objIn = "";
                }
                values[i] = objIn.trim().toLowerCase(); // the twins search is case insensitive
            }
        }

        return getCRCFromString(values);
    }

    /**
     * Returns the CRC value for the given string array concatenating the strings
     * with '|' as separator
     *
     * @param value
     * @return CRC of value
     **/
    public long getCRCFromString(String[] values) {

        StringBuffer sb = new StringBuffer(128);
        int numValues = values.length;
        int cont = 0;
        for (int i=0; i<numValues; i++) {
            sb.append(values[i]);

            if (cont++ > 0) {
                sb.append('|');
            }
        }

        byte[] bytesMsg = sb.toString().getBytes();
        return SourceUtils.computeCRC(bytesMsg);
    }

}
