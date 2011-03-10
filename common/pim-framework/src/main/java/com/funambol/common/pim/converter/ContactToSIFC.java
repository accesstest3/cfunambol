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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimeZone;

import com.funambol.framework.tools.StringTools;

import com.funambol.common.pim.contact.*;
import com.funambol.common.pim.common.*;
import com.funambol.common.pim.utility.TimeUtils;

/**
 * This object is a converter from a Contact object model to an XML string (SIF-C).
 *
 * @see Converter
 *
 * @version $Id: ContactToSIFC.java,v 1.2 2007-11-28 11:14:04 nichele Exp $
 */
public class ContactToSIFC
extends BaseConverter
implements SIFC {

    // ------------------------------------------------------------ Private Data
    private static final String XML_VERSION =
            "<?xml version=\"1.0\" encoding=\""
            + System.getProperty("file.encoding")
            + "\"?>";

    // ------------------------------------------------------------- Constructor
    public ContactToSIFC(TimeZone timezone, String charset) {
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
        Contact contact = (Contact)obj;
        StringBuffer output = new StringBuffer(XML_VERSION);
        output.append('<').append(ROOT_TAG).append('>');
        output.append('\n').append("<" + SIFC.SIF_VERSION + ">")
        .append(SIFC.CURRENT_SIF_VERSION).append("</" + SIFC.SIF_VERSION + ">");
        
        // Contact base informations
        List tmpBody = contact.getNotes();
        Note note = null;
        for (int i = 0; ((tmpBody != null) && (i < tmpBody.size())); i++) {
            note = (Note) tmpBody.get(i);
            output.append(createTagFromProperty(note.getNoteType(),note));
        }
        output.append(createTagFromString(REVISION,contact.getRevision()));
        output.append(createTagFromString(UID,contact.getUid()));
        output.append(createTagFromString(MILEAGE, contact.getMileage()));
        output.append(createTagFromNumber(IMPORTANCE, contact.getImportance()));
        output.append(createTagFromString(FOLDER, contact.getFolder()));
        output.append(createTagFromNumber(SENSITIVITY, contact.getSensitivity()));
        output.append(createTagFromString(SUBJECT, contact.getSubject()));
        output.append(createTagFromProperty(CATEGORIES, contact.getCategories()));
        output.append(createTagFromString(LANGUAGE, contact.getLanguages()));
        output.append(createTagFromString(TIMEZONE, contact.getTimezone()));

        // Name
        output.append(createTagFromProperty(TITLE,contact.getName().getSalutation()));
        output.append(createTagFromProperty(FIRST_NAME,contact.getName().getFirstName()));
        output.append(createTagFromProperty(MIDDLE_NAME,contact.getName().getMiddleName()));
        output.append(createTagFromProperty(LAST_NAME,contact.getName().getLastName()));
        output.append(createTagFromProperty(INITIALS,contact.getName().getInitials()));
        output.append(createTagFromProperty(SUFFIX,contact.getName().getSuffix()));
        output.append(createTagFromProperty(FILE_AS,contact.getName().getDisplayName()));
        output.append(createTagFromProperty(NICK_NAME,contact.getName().getNickname()));
        output.append(createTagFromString(FREEBUSY, contact.getFreeBusy()));
        // Base Personal Detail
        output.append(createTagForDate(BIRTHDAY,
                                       contact.getPersonalDetail().getBirthday())
            );
        output.append(createTagForDate(ANNIVERSARY,
                                       contact.getPersonalDetail().getAnniversary())
            );

        output.append(createTagFromString(CHILDREN,contact.getPersonalDetail().getChildren()));
        output.append(createTagFromString(SPOUSE,contact.getPersonalDetail().getSpouse()));
        output.append(createTagFromString(HOBBY,contact.getPersonalDetail().getHobbies()));
        output.append(createTagFromProperty(PHOTO,contact.getPersonalDetail().getPhoto()));

        // Personal Address
        output.append(createTagFromProperty(HOME_ADDRESS_POST_OFFICE_BOX,contact.getPersonalDetail().getAddress().getPostOfficeAddress()));
        output.append(createTagFromProperty(HOME_ADDRESS_EXTENDED,contact.getPersonalDetail().getAddress().getExtendedAddress()));
        output.append(createTagFromProperty(HOME_ADDRESS_STREET,contact.getPersonalDetail().getAddress().getStreet()));
        output.append(createTagFromProperty(HOME_ADDRESS_CITY,contact.getPersonalDetail().getAddress().getCity()));
        output.append(createTagFromProperty(HOME_ADDRESS_STATE,contact.getPersonalDetail().getAddress().getState()));
        output.append(createTagFromProperty(HOME_ADDRESS_POSTAL_CODE,contact.getPersonalDetail().getAddress().getPostalCode()));
        output.append(createTagFromProperty(HOME_ADDRESS_COUNTRY,contact.getPersonalDetail().getAddress().getCountry()));

         // Other Address
        output.append(createTagFromProperty(OTHER_ADDRESS_POST_OFFICE_BOX,contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress()));
        output.append(createTagFromProperty(OTHER_ADDRESS_STREET,contact.getPersonalDetail().getOtherAddress().getStreet()));
        output.append(createTagFromProperty(OTHER_ADDRESS_EXTENDED,contact.getPersonalDetail().getOtherAddress().getExtendedAddress()));
        output.append(createTagFromProperty(OTHER_ADDRESS_CITY,contact.getPersonalDetail().getOtherAddress().getCity()));
        output.append(createTagFromProperty(OTHER_ADDRESS_STATE,contact.getPersonalDetail().getOtherAddress().getState()));
        output.append(createTagFromProperty(OTHER_ADDRESS_POSTAL_CODE,contact.getPersonalDetail().getOtherAddress().getPostalCode()));
        output.append(createTagFromProperty(OTHER_ADDRESS_COUNTRY,contact.getPersonalDetail().getOtherAddress().getCountry()));

        // Personal Contact Detail
        List tmpweb = contact.getPersonalDetail().getWebPages();
        for (int i = 0; ((tmpweb != null) && (i < tmpweb.size())); i++) {
            output.append(createTagFromProperty(((WebPage) tmpweb.get(i)).getWebPageType(),((WebPage) tmpweb.get(i))));
        }

        List tmphone = contact.getPersonalDetail().getPhones();
        for (int i = 0; ((tmphone !=null) && (i < tmphone.size())); i++) {
            output.append(createTagFromProperty(((Phone) tmphone.get(i)).getPhoneType(), (Phone) tmphone.get(i)));
        }

        // Base Businnes Detail
        output.append(createTagFromProperty(PROFESSION,contact.getBusinessDetail().getRole()));

        // Job Title
        List tmptitle = contact.getBusinessDetail().getTitles();
        Title title;
        for (int i = 0; ((tmptitle != null) && (i < tmptitle.size())); i++) {
            title = (Title) tmptitle.get(i);
            output.append(createTagFromProperty(title.getTitleType(),title));
        }

        output.append(createTagFromProperty(COMPANY_NAME, contact.getBusinessDetail().getCompany()));
        output.append(createTagFromString  (COMPANIES, 	  contact.getBusinessDetail().getCompanies()));
        output.append(createTagFromProperty(DEPARTMENT,  contact.getBusinessDetail().getDepartment()));
        output.append(createTagFromString  (MANAGER_NAME,   contact.getBusinessDetail().getManager()));
        output.append(createTagFromString  (ASSISTANT_NAME, contact.getBusinessDetail().getAssistant()));

        // Business Address
        output.append(createTagFromProperty(BUSINESS_ADDRESS_POST_OFFICE_BOX, contact.getBusinessDetail().getAddress().getPostOfficeAddress()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_STREET,        contact.getBusinessDetail().getAddress().getStreet()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_EXTENDED,      contact.getBusinessDetail().getAddress().getExtendedAddress()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_CITY,          contact.getBusinessDetail().getAddress().getCity()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_STATE,         contact.getBusinessDetail().getAddress().getState()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_POSTAL_CODE,    contact.getBusinessDetail().getAddress().getPostalCode()));
        output.append(createTagFromProperty(BUSINESS_ADDRESS_COUNTRY,       contact.getBusinessDetail().getAddress().getCountry()));
        output.append(createTagFromString  (OFFICE_LOCATION,               contact.getBusinessDetail().getOfficeLocation()));

        // Business Contact Detail
        List tmpwebBusi = contact.getBusinessDetail().getWebPages();
        for (int i = 0; ((tmpwebBusi != null) && (i < tmpwebBusi.size())); i++) {
            output.append(createTagFromProperty(((WebPage) tmpwebBusi.get(i)).getWebPageType(),(WebPage) tmpwebBusi.get(i)));
        }

        // personalEmail
        List tmpemail = contact.getPersonalDetail().getEmails();
        for (int i = 0; ((tmpemail != null) && (i < tmpemail.size())); i++) {
            output.append(createTagFromProperty(((Email) tmpemail.get(i)).getEmailType(),(Email) tmpemail.get(i)));
        }

        // Business Email
        List tmpemailBusi = contact.getBusinessDetail().getEmails();
        for (int i = 0; ((tmpemailBusi != null) && (i < tmpemailBusi.size())); i++) {
            output.append(createTagFromProperty(((Email) tmpemailBusi.get(i)).getEmailType(),(Email) tmpemailBusi.get(i)));
        }

        List tmphone2 = contact.getBusinessDetail().getPhones();
        for (int i = 0; ((tmphone2 != null) && (i < tmphone2.size())); i++) {
            output.append(createTagFromProperty(((Phone) tmphone2.get(i)).getPhoneType(),(Phone) tmphone2.get(i)));
        }

        //Address Label
        output.append(createTagFromProperty(HOME_LABEL,contact.getPersonalDetail().getAddress().getLabel()));
        output.append(createTagFromProperty(BUSINESS_LABEL,contact.getBusinessDetail().getAddress().getLabel()));
        output.append(createTagFromProperty(OTHER_LABEL,contact.getPersonalDetail().getOtherAddress().getLabel()));

        List tmpXTag = contact.getXTags();
        for (int i = 0; ((tmpXTag != null) && (i < tmpXTag.size())); i++) {

            output.append(createTagFromProperty(((XTag) tmpXTag.get(i)).getXTagValue(),(Property) ((XTag) tmpXTag.get(i)).getXTag()));
        }
        output.append(createTagFromString(FREEBUSY, contact.getFreeBusy()));
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
