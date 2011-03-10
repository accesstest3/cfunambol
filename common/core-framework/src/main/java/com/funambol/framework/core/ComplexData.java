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
package com.funambol.framework.core;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Corresponds to the &lt;Data&gt; tag in the SyncML represent DTD when
 * delivering an Anchor or a DevInf or a Property
 *
 * @version $Id: ComplexData.java,v 1.6 2007/06/15 14:43:36 luigiafassina Exp $
 */
public class ComplexData
extends Data
implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    protected Anchor   anchor  ;
    protected DevInf   devInf  ;
    protected ArrayList properties = new ArrayList();
    protected byte[] binaryData;


    // ------------------------------------------------------------ Constructors
    /** For serialization purposes */
    public ComplexData() {}

    /**
     * Creates a Data object from the given anchors string.
     *
     * @param data the data
     *
     */
    public ComplexData(String data) {
        super(data);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the Anchor object property
     *
     * @return anchor the Anchor object
     */
    public Anchor getAnchor() {
        return anchor;
    }

    /**
     * Sets the Anchor object property
     *
     * @param anchor the Anchor object
     */
    public void setAnchor(Anchor anchor) {
        if (null == anchor) {
            throw new IllegalArgumentException("anchor cannot be null");
        }
        this.anchor = anchor;
    }

    /**
     * Gets the DevInf object property
     *
     * @return devInf the DevInf object property
     */
    public DevInf getDevInf() {
        return devInf;
    }

    /**
     * Sets the DevInf object property
     *
     * @param devInf the DevInf object property
     *
     */
    public void setDevInf(DevInf devInf) {
        if (null == devInf) {
            throw new IllegalArgumentException("devInf cannot be null");
        }
        this.devInf = devInf;
    }

    /**
     * Get an array of supported properties of a given content type
     *
     * @return an array of supported properties
     */
    public ArrayList getProperties() {
        return this.properties;
    }

    /**
     * Sets an array of supported properties of a given content type
     *
     * @param properties an array of supported properties
     */
    public void setProperties(Property[] properties) {

        if (properties == null || properties.length == 0) {
            throw new IllegalArgumentException("properties cannot be null");
        }
        this.properties.clear();
        this.properties.addAll(Arrays.asList(properties));
    }

    /**
     * Gets the data
     *
     * @return the data
     */
    public String getData() {
        if (binaryData != null) {
            return new String(binaryData);
        }
        return ((data == null) ? "" : data);
    }

    /**
     * Returns the size of the data in bytes
     * @return the size of the data in bytes
     */
    @Override
    public int getSize() {
        if (binaryData != null) {
            return binaryData.length;
        }
        return ((data == null) ? 0 : data.getBytes().length);
    }
    
    /**
     * Returns binay data
     * @return the binary data if any
     */
    public byte[] getBinaryData() {
        return binaryData;
    }

    /**
     * Sets the binary data
     * @param binaryData the binary data to set
     */
    public void setBinaryData(byte[] binaryData) {
        this.binaryData = binaryData;
    }
}
