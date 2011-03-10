/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.server.update;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.StringTokenizer;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * Provides useful methods to handle the components
 * @version $Id: Util.java,v 1.2 2008-05-16 16:28:16 nichele Exp $
 */
public class Util {

    // ---------------------------------------------------------- Public methods

    /**
     * Parses the given xml string in order to obtain a ComponentList
     * @param xml the xml to parse
     * @return a ComponentList
     * @throws UpdateException if an error occurs
     */
    public static ComponentList fromXML(String xml) throws UpdateException {
        Object list;
        try {
            IBindingFactory f = BindingDirectory.getFactory(ComponentList.class);
            IUnmarshallingContext c = f.createUnmarshallingContext();
            list = c.unmarshalDocument(new StringReader(xml));
        } catch (JiBXException ex) {
            throw new UpdateException(ex);
        }

        return (ComponentList)list;
    }

    /**
     * Converts the given ComponentList in xml
     * @param list the component list
     * @return a xml representation of the given list
     * @throws UpdateException if an error occurs
     */
    public static String toXML(ComponentList list) throws UpdateException {
        String message = null;

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        try {

            IBindingFactory f = BindingDirectory.getFactory(ComponentList.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(list, "UTF-8", null, bout);
        } catch (JiBXException ex) {
            throw new UpdateException(ex);
        }

        message = new String(bout.toByteArray());

        return message;
    }

    /**
     * Compares the given version in order to check if v1 is greater than v2.
     * <br/>
     * The versions must be in the form:
     * <code>releaseMajor.releaseMinor.releaseBuildNumber</code>
     * <br/>Examples: 6.0.1, 6.0.12, 6.1.0, 7.0.0.
     * <br/>Also SNAPSHOT versions are supported and for instance 7.0.0 is greater
     * than 7.0.0-SNAPSHOT
     * @param v1 the first version
     * @param v2 the second version
     * @return  true is v1 is greater than v2
     */
    public static boolean compareVersionNumber(String v1, String v2) {

        if (v1 == null || v2 == null) {
            return false;
        }

        boolean v1Snap = false;
        boolean v2Snap = false;

        if (v1.endsWith("-SNAPSHOT") || v1.endsWith("-snapshot")) {
            v1 = v1.substring(0, v1.length() - "-SNAPSHOT".length());
            v1Snap = true;
        }

        if (v2.endsWith("-SNAPSHOT") || v2.endsWith("-snapshot")) {
            v2 = v2.substring(0, v2.length() - "-SNAPSHOT".length());
            v2Snap = true;
        }

        StringTokenizer sz1 = new StringTokenizer(v1, ".");
        StringTokenizer sz2 = new StringTokenizer(v2, ".");

        int countToken1 = sz1.countTokens();
        int countToken2 = sz2.countTokens();

        int numTokens = (countToken1 > countToken2 ? countToken1 : countToken2);

        int[] versionNumbers1 = new int[numTokens];
        int[] versionNumbers2 = new int[numTokens];
        int i = 0;
        while (sz1.hasMoreTokens()) {
            versionNumbers1[i++] = Integer.parseInt(sz1.nextToken());
        }
        i = 0;
        while (sz2.hasMoreTokens()) {
            versionNumbers2[i++] = Integer.parseInt(sz2.nextToken());
        }

        for (int j = 0; j < numTokens; j++) {
            if (versionNumbers1[j] > versionNumbers2[j]) {
                return true;
            } else if (versionNumbers1[j] < versionNumbers2[j]) {
                return false;
            }
        }
        //
        // The versions are equals...checking the snapshot
        //
        if (v1Snap && !v2Snap) {
            return false;
        } else if (v1Snap && v2Snap) {
            return false;
        } else if (!v1Snap && v2Snap) {
            return true; // the no snapshot one is greater than the snapshot one
        }
        return false;
    }

}
