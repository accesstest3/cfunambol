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
package com.funambol.common.pim.converter;

import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import com.funambol.framework.tools.StringTools;

import com.funambol.common.pim.note.*;
import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.common.XTag;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * This object is a converter from a Note object model to an XML string (SIF-N).
 *
 * @see Converter
 *
 * @version $Id: NoteToSIFN.java,v 1.3 2008-04-28 15:56:11 piter_may Exp $
 */
public class NoteToSIFN extends BaseConverter implements SIFN {

    // ------------------------------------------------------------ Private Data
    private static final String XML_VERSION =
            "<?xml version=\"1.0\" encoding=\""
            + System.getProperty("file.encoding")
            + "\"?>";

    // ------------------------------------------------------------- Constructor
    public NoteToSIFN(TimeZone timezone, String charset) {
        super(timezone, charset);
    }
    // ---------------------------------------------------------- Public Methods

    /**
     * Performs the conversion.
     *
     * @param contact the Contact to be converted
     *
     * @return a string containing the converted representation of this Contact
     */
    public String convert(Object obj) throws ConverterException {
        
        Note note = (Note)obj;
        
        StringBuffer output = new StringBuffer(XML_VERSION);
        output.append('<').append(ROOT_TAG).append('>');
        output.append('\n').append("<" + SIFN.SIF_VERSION + ">")
        .append(SIFN.CURRENT_SIF_VERSION).append("</" + SIFN.SIF_VERSION + ">");
        
        // Note base informations
        /*
        output.append(createTagFromString(REVISION,contact.getRevision()));
        output.append(createTagFromString(UID,contact.getUid()));
        output.append(createTagFromString(MILEAGE, contact.getMileage()));
        output.append(createTagFromNumber(IMPORTANCE, contact.getImportance()));
        output.append(createTagFromString(FOLDER, contact.getFolder()));
        output.append(createTagFromNumber(SENSITIVITY, contact.getSensitivity()));
        output.append(createTagFromString(SUBJECT, contact.getSubject()));
        output.append(createTagFromProperty(CATEGORIES, contact.getCategories()));
        output.append(createTagFromString(LANGUAGE, contact.getLanguages()));
        */
        // 
        output.append(createTagFromProperty(SUBJECT,note.getSubject()));
        output.append(createTagFromProperty(BODY,note.getTextDescription()));
        output.append(createTagFromProperty(DATE,note.getDate()));
        output.append(createTagFromProperty(CATEGORIES,note.getCategories()));
        output.append(createTagFromProperty(FOLDER,note.getFolder()));
        output.append(createTagFromProperty(COLOR,note.getColor()));
        output.append(createTagFromProperty(HEIGHT,note.getHeight()));
        output.append(createTagFromProperty(WIDTH,note.getWidth()));
        output.append(createTagFromProperty(TOP,note.getTop()));
        output.append(createTagFromProperty(LEFT,note.getLeft()));

        List tmpXTag = note.getXTags();
        for (int i = 0; ((tmpXTag != null) && (i < tmpXTag.size())); i++) {
            output.append(createTagFromProperty(((XTag) tmpXTag.get(i)).getXTagValue(),(Property) ((XTag) tmpXTag.get(i)).getXTag()));
        }

        output.append("</").append(ROOT_TAG).append('>');
        
        return output.toString();
    }

    /**
     * Returns a string representing the tag with the specified name and the content (value and
     * attributes) fetched from the given Property element
     *
     * @param tag the tag name
     * @param prop the property
     * @return an XML representation of the tag
     */
    private StringBuffer createTagFromProperty(String tag, Property prop)
    throws ConverterException {
        StringBuffer output = new StringBuffer("");
        if (prop != null && prop.getPropertyValue() != null) {
            if(!(prop.getPropertyValue().equals(""))) {
                // Create the parameters and use it. NO NEED TO DO THAT 3 times.
                StringBuffer params = getParams(prop);
                if (params.length() > 0) {

                    if (params.indexOf("QUOTED-PRINTABLE") > 0) {
                        output.append('<').append(tag).append(params).append('>')
                              .append(StringTools.escapeBasicXml(
                                         (String)prop.getPropertyValue())
                                     )
                              .append("</").append(tag).append('>');
                    } else {
                        output.append('<').append(tag).append(params).append('>').append(StringTools.escapeBasicXml((String)prop.getPropertyValue())).append("</").append(tag).append('>');
                    }
                } else {
                    output.append('<').append(tag).append('>').append(StringTools.escapeBasicXml((String)prop.getPropertyValue())).append("</").append(tag).append('>');
                }
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and value
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    private StringBuffer createTagFromString(String tag, String propertyValue) {
        StringBuffer output = new StringBuffer();
        if (propertyValue != null) {
            if (propertyValue.length() > 0) {
                output.append('<').append(tag).append('>');
                output.append(StringTools.escapeBasicXml(propertyValue));
                output.append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }

    /**
     * Returns a string representing the tag with the specified name and
     * value for type "short" or "int"
     *
     * @param tag the tag name
     * @param propertyValue the tag value
     * @return an XML representation of the tag
     */
    private StringBuffer createTagFromNumber(String tag, java.lang.Number propertyValue) {
        StringBuffer output = new StringBuffer();

        if (propertyValue != null) {
            output.append('<').append(tag).append('>');
        output.append(propertyValue);
            output.append("</").append(tag).append('>');
        }

        return output;
    }

    /**
     * Returns a string represnting the attributes fetched from the given Property
     * in an XML-compliant form
     *
     * @param prop the property
     * @return an XML representation of the attributes contained in the Property
     */
    private StringBuffer getParams (Property prop) {
        StringBuffer output = new StringBuffer();

        if (prop.getGroup()!=null) {
            output.append(" GROUP=\"").append(prop.getGroup().toUpperCase()).append("\"");
        }
        if (prop.getEncoding()!=null) {
            output.append(" ENCODING=\"").append(prop.getEncoding().toUpperCase()).append("\"");
        }
        if (prop.getCharset()!=null) {
            output.append(" CHARSET=\"").append(prop.getCharset().toUpperCase()).append("\"");
        }
        if (prop.getLanguage()!=null) {
            output.append(" LANGUAGE=\"").append(prop.getLanguage().toUpperCase()).append("\"");
        }
        if (prop.getValue()!=null) {
            output.append(" VALUE=\"").append(prop.getValue().toUpperCase()).append("\"");
        }
        if (prop.getType()!=null) {
            output.append(" TYPE=\"").append(prop.getType().toUpperCase()).append("\"");
        }
        if (prop.getXParams() != null) {
            HashMap h = prop.getXParams();
            Iterator it = h.keySet().iterator();
            while(it.hasNext()) {
                String tag = (String)it.next();
                String value = (String)h.get(tag);
                //
                // If the value is null, set it as the tag to create a right XML
                //
                if (value == null) {
                    value = tag;
                }
                output.append(" ").append(tag.toUpperCase()).append("=\"").append(value.toUpperCase()).append("\"");
            }
        }

        return output;
    }

    /**
     * Returns a string representing the date in tag. The date usually
     * arrives as "yyyy-MM-dd". If there are no "-" char, it copy the same date.
     * This is used for Birthday and Anniversary date.
     *
     * @param tag the tag name
     * @param prop the value
     * @return an XML representation of the tag
     */
    private StringBuffer createTagForDate(String tag,
                                          String dateValue)
    throws ConverterException {

        StringBuffer output = new StringBuffer();

        if (dateValue != null) {
        String isoDate = null;

        dateValue = StringTools.escapeBasicXml(dateValue);

        try {
            isoDate = TimeUtils.normalizeToISO8601(dateValue, timezone);
        } catch (Exception ex) {
            throw new ConverterException("Error parsing date", ex);
        }

            if (dateValue.length()>0) {
                output.append('<').append(tag).append('>');
                output.append(isoDate);
                output.append("</").append(tag).append('>');
            } else {
                output.append('<').append(tag).append("/>");
            }
        }
        return output;
    }
}
