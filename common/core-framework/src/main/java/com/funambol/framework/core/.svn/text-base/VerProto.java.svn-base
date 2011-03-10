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

/**
 * This class represents the &lt;VerProto&gt; tag as defined by the SyncML r
 * epresentation specifications.
 *
 * @version $Id: VerProto.java,v 1.2 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class VerProto
implements java.io.Serializable {

    // ------------------------------------------------------------ Private data

    private String version;

    // ------------------------------------------------------------ Constructors

    /**
     * For serialization purposes
     */
    protected VerProto() {}

    /**
     * Creates a new VerProto object from its version.
     *
     * @param version the protocol version - NOT NULL
     *
     */
    public VerProto(final String version) {
        setVersion(version);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Returns the protocol version.
     *
     * @return the protocol version - NOT NULL
     *
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the protol version.
     *
     * @param version the protocol version - NOT NULL
     *
     * @throws IllegalArgumentException if version is null
     */
    public void setVersion(String version) {
        if (version == null) {
            throw new IllegalArgumentException("version cannot be null");
        }
        this.version = version;
    }

    /**
     * Compares this VerProto with a given object. If obj is a VerProto,
     * versions are compared as stored internally. if obj is null, false is
     * returned. If obj is a String, the string representation of this VerProto
     * is used for the comparison. If obj is neither a VerProto nor a String,
     * String.valueOf(obj) is used for the comparison
     *
     * @param obj the obect to be compared
     *
     * @return true if the two object represent the same information
     */
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        String s = null;
        if (obj instanceof VerProto) {
            s = ((VerProto)obj).getVersion();
        } else if (obj instanceof String) {
            s = (String)obj;
        } else {
            s = String.valueOf(obj);
        }

        return (s.equals(version));
    }
}
