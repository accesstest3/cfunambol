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
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.pdi.mail.IEMAIL;
import com.funambol.email.util.Def;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.Iterator;
import java.util.List;


/**
 * This object is a converter from a Mail object model to an XML string.
 *
 * @version $Id: MailToXML.java,v 1.3 2008-07-17 10:10:28 testa Exp $
 */
public class MailToXML extends BaseConverter implements IEMAIL
{

    // ------------------------------------------------------------ Private Data

    public static final int FLAG_GROUP    = 1;
    public static final int FLAG_ENCODING = 2;
    public static final int FLAG_CHARSET  = 4;
    public static final int FLAG_LANGUAGE = 8;
    public static final int FLAG_VALUE    = 16;
    public static final int FLAG_TYPE     = 32;
    public static final int FLAG_XPARAMS  = 64;


    // ------------------------------------------------------------- Constructor

    public MailToXML(TimeZone timezone, String charset) {
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

        Email mail = (Email)obj;

        //StringBuffer output = new StringBuffer(XML_VERSION);
        StringBuffer output = new StringBuffer();
        output.append('<').append(ROOT_TAG).append('>');


        output.append(createTagFromProperty(READ,     mail.getRead()));
        output.append(createTagFromProperty(FORWARDED,mail.getForwarded()));
        output.append(createTagFromProperty(REPLIED,  mail.getReplied()));
        output.append(createTagFromProperty(RECEIVED, mail.getReceived()));
        output.append(createTagFromProperty(CREATED,  mail.getCreated()));
        output.append(createTagFromProperty(MODIFIED, mail.getModified()));
        output.append(createTagFromProperty(DELETED,  mail.getDeleted()));
        output.append(createTagFromProperty(FLAGGED,  mail.getFlagged()));

        output.append(createTagFromCDATA(EMAILITEM, mail.getEmailItem()));

        output.append(createTagFromExt(mail.getExt()));

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
     */
    private StringBuffer createTagFromProperty(String tag, Property prop)
    {
        StringBuffer output = new StringBuffer("");
        if (prop != null && prop.getPropertyValue() != null) {
            if(!(prop.getPropertyValue().equals(""))) {
                // Create the parameters and use it. NO NEED TO DO THAT 3 times.
                StringBuffer params = getParams(prop, FLAG_VALUE|FLAG_TYPE|FLAG_XPARAMS);
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
     * Returns a Ext representing
     *
     * @return Ext. an Ext Object representation of the extension fields
     */
    private StringBuffer createTagFromExt(Ext ext) {
        StringBuffer output = new StringBuffer();

        if (ext == null){

            return output;

        } else {

            // set the size of the email
            String xvalSize = ext.getXsize();
            if (xvalSize != null){
                if (!xvalSize.equals("")){
                    output.append("<Ext>");
                    output.append("<XNam>").append("x-funambol-size").append("</XNam>");
                    output.append("<XVal>").append(xvalSize).append("</XVal>");
                    output.append("</Ext>");
                }
            }

            if (ext.isTruncated()){
                // set the body size
                String xvalBody = ext.getXvalBody();
                if (xvalBody != null){
                    if (!xvalBody.equals("")){
                        output.append("<Ext>");
                        output.append("<XNam>").append("x-funambol-body").append("</XNam>");
                        output.append("<XVal>").append(xvalBody).append("</XVal>");
                        output.append("</Ext>");
                    }
                }
            }

            // attachment
            List xvalAttach = ext.getXvalAttach();
            int num = xvalAttach.size();
            String name = null;
            String dim  = null;
            if (num > 0){

                output.append("<Ext>");
                output.append("<XNam>").append("x-funambol-attach-n").append("</XNam>");
                output.append("<XVal>").append(""+num).append("</XVal>");
                output.append("</Ext>");

                String[] value = null;
                for (int i=0; i<xvalAttach.size(); i++){
                    value = (String[])xvalAttach.get(i);
                    name = value[0];
                    dim  = value[1];
                    output.append("<Ext>");
                    output.append("<XNam>").append("x-funambol-attach").append("</XNam>");
                    output.append("<XVal>").append(name).append("</XVal>");
                    output.append("<XVal>").append(dim).append("</XVal>");
                    if (value.length >= 4 && value[3] != null) {
                        output.append("<XVal>").append(value[2] != null ? value[2] : "").append("</XVal>");  // value[2] is the mime type
                        output.append("<XVal>").append(value[3]).append("</XVal>");  // value[3] is the attachment Url
                    }
                    output.append("</Ext>");
                }
            }

        }



        return output;
    }

    /**
     * Returns a string representing the tag with the specified
     * name and the content (value and attributes) fetched from
     * the given Property element
     *
     * @param tag String. tag the tag name
     * @param property the property
     * @return StringBuffer. an XML representation of the tag
     */
    private StringBuffer createTagFromCDATA(String tag, Property property)
    {
        StringBuffer output = new StringBuffer();
        if (property != null){
            String stringValue = String.valueOf(property.getPropertyValue());

            if (stringValue.length()>0){

                output.append('<').append(tag).append(getParams(property, FLAG_ENCODING)).append('>');

                output.append("<![CDATA[");
                output.append(stringValue);
                output.append("]]>");

                output.append("</").append(tag).append('>');

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
    private StringBuffer getParams (Property prop, int flags)
    {
        StringBuffer output = new StringBuffer();

        //if (prop.getGroup()!=null && (FLAG_GROUP&flags)==FLAG_GROUP){
        //    output.append(" GROUP=\"").append(prop.getGroup().toUpperCase()).append("\"");
        //}

        if (prop.getEncoding()!=null && (FLAG_ENCODING&flags)==FLAG_ENCODING){
            //output.append(" ENC=\"").append(prop.getEncoding().toUpperCase()).append("\"");
            //output.append(" enc=\"").append(prop.getEncoding().toUpperCase()).append("\"");
            output.append(" enc=\"").append(prop.getEncoding().toLowerCase()).append("\"");
        }

        //if (prop.getCharset()!=null && (FLAG_CHARSET&flags)==FLAG_CHARSET){
        //    output.append(" CHARSET=\"").append(prop.getCharset().toUpperCase()).append("\"");
        //}

        //if (prop.getLanguage()!=null && (FLAG_LANGUAGE&flags)==FLAG_LANGUAGE){
        //    output.append(" LANGUAGE=\"").append(prop.getLanguage().toUpperCase()).append("\"");
        //}

        //if (prop.getValue()!=null && (FLAG_VALUE&flags)==FLAG_VALUE){
        //    output.append(" VALUE=\"").append(prop.getValue().toUpperCase()).append("\"");
        //}

        //if (prop.getType()!=null && (FLAG_TYPE&flags)==FLAG_TYPE){
        //    output.append(" TYPE=\"").append(prop.getType().toUpperCase()).append("\"");
        //}

        //if (prop.getXParams()!=null && (FLAG_XPARAMS&flags)==FLAG_XPARAMS){
        //    HashMap h = prop.getXParams();
        //    Iterator it = h.keySet().iterator();
        //    while(it.hasNext()){
        //        String tag = (String)it.next();
        //        String value = (String)h.get(tag);
        //        output.append(" ").append(tag.toUpperCase()).append("=\"").append(value.toUpperCase()).append("\"");
        //    }
        //}

        return output;
    }

}
