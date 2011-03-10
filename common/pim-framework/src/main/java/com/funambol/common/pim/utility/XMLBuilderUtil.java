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
package com.funambol.common.pim.utility;

import com.funambol.framework.tools.StringTools;

/**
 * Utility class usefull for write xml tags and element
 * @version $Id: $
 */
public class XMLBuilderUtil {

    // ---------------------------------------------------------- Public Methods
    /**
     * Append to a StringBuilder the XML element with the specified name, value
     * and attribute
     *
     * @param output The StringBuilder where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     * @param attrNames The attribute name of the element (only one attribute is
     * allowed)
     * @param attrVal The attribute value 
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendElement(StringBuilder output,
            String tag, String value, String attrNames, String attrVal) {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }

        if (value == null) {
            return output;
        } else if (value.length() > 0) {
            appendStartTag(output, tag, attrNames, attrVal);
            output.append(StringTools.escapeBasicXml(value));
            appendEndTag(output, tag);
        } else {
            appendEmptyTag(output, tag);
        }
        return output;
    }

    /**
     * Append to a StringBuilder the XML element with the specified name and
     * value
     *
     * @param output The StringBuilder where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendElement(StringBuilder output,
            String tag, String value) {

        return appendElement(output, tag, value, null, null);
    }

    /**
     * Append to a StringBuilder the XML element with the specified name and
     * value
     *
     * @param output The StringBuilder where to append the XML element
     * @param tag the tag name of the XML element. must be not empty
     * @param value the value of the XML elemen
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendElement(StringBuilder output,
            String tag, Object value) {

        if (value == null) {
            return output;
        }

        String stringValue = String.valueOf(value);
        return appendElement(output, tag, stringValue);
    }

    /**
     * Append to a StringBuilder the start tag with the specified name and 
     * attribute
     *
     * @param output The StringBuilder where to append the start tag
     * @param tag the tag name of the XML element. must be not empty
     * @param attrNames The attribute name of the element (only one attribute is
     * allowed)
     * @param attrVal The attribute value 
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendStartTag(StringBuilder output, String tag,
            String attrNames, String attrVal) {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }

        output.append('<').append(tag);

        if (attrNames != null && attrVal != null) {
            output.append(' ').append(attrNames).append('=').append('"').
                    append(attrVal).append('"');
        }
        output.append('>');
        return output;
    }

    /**
     * Append to a StringBuilder the start tag with the specified name and 
     * attribute
     *
     * @param output The StringBuilder where to append the start tag
     * @param tag the tag name of the XML element. must be not empty
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendStartTag(StringBuilder output, String tag) {

        return appendStartTag(output, tag, null, null);
    }

    /**
     * Append to a StringBuilder the end tag with the specified name
     *
     * @param output The StringBuilder where to append the end tag
     * @param tag the tag name of the XML element. must be not empty
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendEndTag(StringBuilder output, String tag) {

        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }

        output.append("</").append(tag).append('>').append('\n');
        return output;
    }

    /**
     * Append to a StringBuilder the empty tag with the specified name
     *
     * @param output The StringBuilder where to append the empty tag
     * @param tag the tag name of the XML element. must be not empty
     * @return The StringBuilder passed as argument
     */
    public static StringBuilder appendEmptyTag(StringBuilder output, String tag) {
        if (tag == null || tag.length() == 0) {
            throw new IllegalArgumentException("Invalid empty tag");
        }
        output.append('<').append(tag).append("/>").append('\n');
        return output;
    }
}
