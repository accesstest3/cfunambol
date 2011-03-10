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
package com.funambol.framework.tools;

import java.io.ByteArrayOutputStream;

import java.io.StringReader;

import org.jibx.runtime.*;

import com.funambol.framework.core.SyncML;


/**
 * This class contains some helper methods to translate a tree of objects into
 * a SyncML message and to be used by JiBX.
 *
 * @version $Id: SyncMLUtil.java,v 1.3 2008-02-25 13:08:09 luigiafassina Exp $
 */
public class SyncMLUtil {
    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    /**
     * Serialize Long value to string.
     *
     * @param value Long value to be serialized
     *
     * @return the representation of value
     */
    public static String serializeWrapLong(Long value) {
        return String.valueOf(value);
    }

    /**
     * Deserialize Long from string
     *
     * @param value string to be parsed
     *
     * @return the representation of value
     */
    public static Long deserializeWrapLong(String value) {
        if (value == null || value.equals("")) {
        return null;
        }
        return Long.valueOf(value.trim());
    }

    public static Boolean deserializeBoolean(String value) {
        if (value != null &&
        (value.equals("") || value.equalsIgnoreCase("true"))) {
            return Boolean.TRUE;
        }
        return null;
    }

    public static String serializeBoolean(Boolean value) {
        return value.booleanValue() ? "" : null;
    }

    /**
     * Uses marshall to create the representation XML of the object SyncML. If
     * the input boolean is true, the sensitive data are shown, else they are
     * hidden.
     *
     * @param syncML the object SyncML
     * @param showSensitiveData if true, the sensitive data are shown,
     *                          otherwise they are hidden
     *
     * @return the representation XML of the message
     */
    public static String toXML(SyncML syncML, boolean showSensitiveData) {
        String bindingName = null;
        if (showSensitiveData) {
            bindingName = "binding";
        } else {
            bindingName = "bindingHiddenData";
        }
        String message = null;
        try {

            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            IBindingFactory f = BindingDirectory.getFactory(bindingName, SyncML.class);
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(syncML, "UTF-8", null, bout);

            message = new String(bout.toByteArray());

        } catch(Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Uses marshall to create the representation XML of the object SyncML.
     * For backward compatibility, as default the sensitive data are shown.
     *
     * @param syncML the object SyncML
     *
     * @return the representation XML of the message
     */
    public static String toXML(SyncML syncML) {
        return toXML(syncML, true);
    }

    /**
     * Uses marshall to create the representation XML of the object, therefore
     * must exist the mapping in the binding file. If the input boolean is true,
     * the sensitive data are shown, else they are hidden.
     *
     * @param obj the object
     * @param showSensitiveData if true, the sensitive data are shown,
     *                          otherwise they are hidden
     *
     * @return the representation XML of the object
     */
    public static String toXML(Object obj, boolean showSensitiveData) {
        String bindingName = null;
        if (showSensitiveData) {
            bindingName = "binding";
        } else {
            bindingName = "bindingHiddenData";
        }
        String message = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            IBindingFactory f = BindingDirectory.getFactory(bindingName, obj.getClass());
            IMarshallingContext c = f.createMarshallingContext();
            c.setIndent(0);
            c.marshalDocument(obj, "UTF-8", null, bout);

            message = new String(bout.toByteArray());
        } catch(Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    /**
     * Uses marshall to create the representation XML of the object,
     * therefore must exist the mapping in the binding file.
     * For backward compatibility, as default the sensitive data are shown.
     *
     * @param obj the object
     *
     * @return the representation XML of the object
     */
    public static String toXML(Object obj) {
        return toXML(obj, true);
    }

    /**
     * Converts the given xml in a SyncML object
     * @param xml the xml to parse
     * @return the SyncML object
     * @throws JiBXException if an error occurs parsing xml
     */
    public static SyncML fromXML(String xml) throws JiBXException {

        IUnmarshallingContext c = 
                BindingDirectory.getFactory("binding", SyncML.class).createUnmarshallingContext();
        
        return  (SyncML) c.unmarshalDocument(new StringReader(xml));
    }
}
