/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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

import java.util.*;

/**
 * This class corresponds to the &lt;CTCap&gt; tag in the SyncML devinf
 * DTD v1.1.x
 *
 * @version $Id: CTCapV1.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class CTCapV1 implements java.io.Serializable {

    // ------------------------------------------------------------ Private data
    private ArrayList ctTypes = new ArrayList();

    // ------------------------------------------------------------ Constructors
    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public CTCapV1() {}

    /**
     * Creates a new CTCap object with the given array of information
     *
     * @param ctTypeSupported the array of information on content type
     *                        capabilities - NOT NULL
     */
    public CTCapV1(final CTType[] ctTypes) {
        setCTTypes(ctTypes);
    }
    
    /**
     * Creates a new CTCap object with the given array of information
     *
     * @param ctTypeSupported the array of information on content type
     *                        capabilities - NOT NULL
     */
    public CTCapV1(final ArrayList ctTypes) {
        setCTTypes(ctTypes);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Get an array of content type information objects
     *
     * @return an array of content type information objects
     *
     * @deprecated use getCTTypes() instead
     */
    public ArrayList getCTTypeSupported() {
        return getCTTypes();
    }
    
    /**
     * Get an array of content type information objects
     *
     * @return an array of content type information objects
     *
     */
    public ArrayList getCTTypes() {
        return this.ctTypes;
    }

    /**
     * Sets an array of content type information objects
     *
     * @param types an array of content type information objects
     *
     * @deprecated use setCTTypes() instead
     */
    public void setCTTypeSupported(CTType[] types) {
        setCTTypes(ctTypes);
    }

    /**
     * Sets an array of content type information objects
     *
     * @param types an array of content type information objects
     *
     */
    public void setCTTypes(CTType[] types) {
        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("types cannot be empty");
        }
        this.ctTypes.clear();
        this.ctTypes.addAll(Arrays.asList(types));
    }

    /**
     * Sets an array of content type information objects
     *
     * @param types an array of content type information objects
     *
     * @deprecated use setCTTypes() instead
     */
    public void setCTTypeSupported(ArrayList types) {
        setCTTypes(types);
    }

    /**
     * Sets an array of content type information objects
     *
     * @param types an array of content type information objects
     */
    public void setCTTypes(ArrayList types) {
        if (types == null || types.size() == 0) {
            throw new IllegalArgumentException("types cannot be empty");
        }
        this.ctTypes.clear();
        this.ctTypes.addAll(types);
    }
}
