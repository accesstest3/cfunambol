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
package com.funambol.email.pdi.converter;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.converter.BaseConverter;
import com.funambol.common.pim.converter.ConverterException;
import com.funambol.framework.tools.StringTools;
import com.funambol.email.pdi.folder.Folder;
import com.funambol.email.pdi.folder.IFOLDER;
import com.funambol.email.util.Def;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Iterator;



/**
 * This object is a converter from a Folder object model to an XML string.
 *
 * @version $Id: FolderToXML.java,v 1.1 2008-03-25 11:28:21 gbmiglia Exp $
 */
public class FolderToXML extends BaseConverter implements IFOLDER
{


    // ------------------------------------------------------------- Constructor

    public FolderToXML(TimeZone timezone, String charset) {
        super(timezone, charset);
    }


    // ---------------------------------------------------------- Public Methods

    /**
     * Performs the conversion.
     *
     *
     * @param obj Object. the Mail to be converted in XML
     * @return a string containing the XML representation of this Contact
     * @throws ConverterException
     */
    public String convert(Object obj) throws ConverterException {

        Folder folder = (Folder)obj;

        //StringBuffer output = new StringBuffer(XML_VERSION);
        StringBuffer output = new StringBuffer();
        output.append('<').append(ROOT_TAG).append('>');

        output.append(createTagFromProperty(NAME,     folder.getName()));
        output.append(createTagFromProperty(CREATED,  folder.getCreated()));
        output.append(createTagFromProperty(MODIFIED, folder.getModified()));
        output.append(createTagFromProperty(ACCESSED, folder.getAccessed()));
        output.append(createTagFromProperty(ROLE,     folder.getRole()));

        output.append("</").append(ROOT_TAG).append('>');

        return output.toString();
    }

    //---------------------------------------------------------- PRIVATE METHODS

    /**
     * Returns a string representing the tag with the specified
     * name and the content (value and attributes) fetched from
     * the given Property element
     *
     * @param tag String. the tag name
     * @param prop Property. the property
     * @return StringBuffer. an XML representation of the tag
     * @throws ConverterException
     */
    private StringBuffer createTagFromProperty(String tag, Property prop)
      throws ConverterException {
        StringBuffer output = new StringBuffer("");
        if (prop != null && prop.getPropertyValue() != null) {
            if(!(prop.getPropertyValue().equals(""))) {
                // Create the parameters and use it. NO NEED TO DO THAT 3 times.
                StringBuffer params = getParams(prop);
                if (params.length() > 0) {

                    //if (params.indexOf("QUOTED-PRINTABLE") > 0) {
                    if (params.indexOf(Def.ENCODE_QUOTED_PRINTABLE) > 0) {
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
     * Returns a string represnting the attributes fetched from the given Property
     * in an XML-compliant form
     *
     * @param prop the property
     * @return an XML representation of the attributes contained in the Property
     */
    private StringBuffer getParams (Property prop) {
        StringBuffer output = new StringBuffer();

        //if (prop.getGroup()!=null) {
        //    output.append(" GROUP=\"").append(prop.getGroup().toUpperCase()).append("\"");
        //}
        if (prop.getEncoding()!=null) {
            output.append(" enc=\"").append(prop.getEncoding().toLowerCase()).append("\"");
        }
        //if (prop.getCharset()!=null) {
        //    output.append(" CHARSET=\"").append(prop.getCharset().toUpperCase()).append("\"");
        //}
        //if (prop.getLanguage()!=null) {
        //    output.append(" LANGUAGE=\"").append(prop.getLanguage().toUpperCase()).append("\"");
        //}
        //if (prop.getValue()!=null) {
        //    output.append(" VALUE=\"").append(prop.getValue().toUpperCase()).append("\"");
        //}
        //if (prop.getType()!=null) {
        //    output.append(" TYPE=\"").append(prop.getType().toUpperCase()).append("\"");
        //}
        //if (prop.getXParams() != null) {
        //    HashMap h = prop.getXParams();
        //    Iterator it = h.keySet().iterator();
        //    while(it.hasNext()) {
        //        String tag = (String)it.next();
        //        String value = (String)h.get(tag);
        //        output.append(" ").append(tag.toUpperCase()).append("=\"").append(value.toUpperCase()).append("\"");
        //    }
        //}

        return output;
    }

}