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
package com.funambol.common.pim.sif;

import org.w3c.dom.*;
import org.xml.sax.*;

import java.io.*;
import javax.xml.parsers.*;
import java.util.HashMap;

import com.funambol.common.pim.contact.*;
import com.funambol.common.pim.common.*;

/**
 * This objects represents a list of vCard property parameters.
 * The list is based on the informations contained in a list of parser tokens.
 *
 * Ignored SIFC fields:
 * <ul>
 *  <li>COMPUTER_NETWORK_NAME</li>
 *  <li>ORGANIZATIONAL_ID_NUMBER</li>
 *  <li>YOMI_COMPANY_NAME</li>
 *  <li>YOMI_FIRST_NAME</li>
 *  <li>YOMI_LAST_NAME</li>
 * </ul>
 *
 * @version $Id: SIFCParser.java,v 1.5 2007-11-28 11:14:06 nichele Exp $
 */
public class SIFCParser extends XMLParser implements SIFC {

    /**
     * Parse the XML doc the XML parser for a VCard or ICalendar object.
     * @param xmlStream the input stream in XML format
     */
    public SIFCParser(InputStream xmlStream)
    throws SAXException, IOException {
        super(xmlStream);
    }

    /**
     * Parse the xml document and returns a Contact object.
     * @return event the object Event
     */
    public Contact parse() throws SAXException {
        return parse(new Contact());
    }

    /**
     *  parse the xml document and returns a vCard object.
     * @throws SAXException If the root is null or not a Contact tag.
     */
    public Contact parse(Contact contact) throws SAXException {
        if (root == null) {
            throw new SAXException("No root tag available");
        }
        if (!root.getTagName().equals(SIFC.ROOT_TAG)) {
            throw new SAXException("Incorrect root tag, " + root.getTagName() + " expected Contact");
        }

        int j = 0;
        //getting child nodes
        NodeList children=root.getChildNodes();
        Node child=null;
        String content="", nodeName = null;
        Property prop=null;
out:    for (int i=0; i< children.getLength(); i++) {
            child = children.item(i);

            if (!(child instanceof Element)) {
                continue;
            }

            nodeName = child.getNodeName();

            //Getting onlyString properties...
            //Element Uid
            if (UID.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.setUid(content);
                }
                continue;
            }
            //Element Timezone
            if (TIMEZONE.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.setTimezone(content);
                }
                continue;
            }
            //Element Revision
            if (REVISION.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.setRevision(content);
                }
                continue;
            }
            //Element Birthday
            if (BIRTHDAY.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setBirthday(content);
                }
                continue;
            }
            //Element Gender
            if (GENDER.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setGender(content);
                }
                continue;
            }
            //Element Anniversary
            if (ANNIVERSARY.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setAnniversary(content);
                }
                continue;
            }
            //Element Children
            if (CHILDREN.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setChildren(content);
                }
                continue;
            }
            //Element Spouse
            if (SPOUSE.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setSpouse(content);
                }
                continue;
            }

            //Element Mileage
            if (MILEAGE.equals(nodeName)) {
                contact.setMileage(getNodeContent(child));
                continue;
            }

            //Element Importance
            if (IMPORTANCE.equals(nodeName)) {
                contact.setImportance(getShortWrapNodeContent(child));
                continue;
            }

            //Element Folder
            if (FOLDER.equals(nodeName)) {
                contact.setFolder(getNodeContent(child));
                continue;
            }

            //Element Sensitivity
            if (SENSITIVITY.equals(nodeName)) {
                contact.setSensitivity(getShortWrapNodeContent(child));
                continue;
            }

            //Element Subject
            if (SUBJECT.equals(nodeName)) {
                contact.setSubject(getNodeContent(child));
                continue;
            }

            //Element Manager
            if (MANAGER_NAME.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getBusinessDetail().setManager(content);
                }
                continue;
            }
            //Element Assistant
            if (ASSISTANT_NAME.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getBusinessDetail().setAssistant(content);
                }
                continue;
            }

            //Element Categories
            if (CATEGORIES.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getCategories().setPropertyValue(prop.getPropertyValue());
                contact.getCategories().setEncoding(prop.getEncoding());
                contact.getCategories().setGroup(prop.getGroup());
                contact.getCategories().setCharset(prop.getCharset());
                contact.getCategories().setLanguage(prop.getLanguage());
                contact.getCategories().setValue(prop.getValue());
                continue;
            }

            //getting telephone properties
            //Element CompanyMainTelephoneNumber
            if (COMPANY_MAIN_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element CarTelephoneNumber
            if (CAR_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element BusinessFaxNumber
            if (BUSINESS_FAX_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Business" + j + "FaxNumber").equals(nodeName)) {
                    contact.getBusinessDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element HomeFaxNumber
            if (HOME_FAX_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
            }

            for (j = 2; j <= 10; j++) {
                if (("Home" + j + "FaxNumber").equals(nodeName)) {
                    contact.getPersonalDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }

            //Element OtherFaxNumber
            if (OTHER_FAX_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
            }
            for (j = 2; j <= 10; j++) {
                if (("Other" + j + "FaxNumber").equals(nodeName)&&(child.hasChildNodes())) {
                    contact.getPersonalDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element OtherTelephoneNumber
            if (OTHER_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
            }
            for (j = 2; j <= 10; j++) {
                if (("Other" + j + "TelephoneNumber").equals(nodeName)) {
                    contact.getPersonalDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element HomeTelephoneNumber
            if (HOME_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Home" + j + "TelephoneNumber").equals(nodeName)) {
                    contact.getPersonalDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element BusinessTelephoneNumber
            if (BUSINESS_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Business" + j + "TelephoneNumber").equals(nodeName)) {
                    contact.getBusinessDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }

            //Element MobileTelephoneNumber
            if (MOBILE_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element MobileHomeTelephoneNumber
            if (MOBILE_HOME_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element MobileBusinessTelephoneNumber
            if (MOBILE_BUSINESS_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Mobile" + j + "TelephoneNumber").equals(nodeName)) {
                    contact.getPersonalDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element PagerNumber
            if (PAGER_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("PagerNumber" + j).equals(nodeName)) {
                    contact.getBusinessDetail().addPhone(createNewPhone(child));
                    continue out;
                }
            }
            //Element PrimaryTelephoneNumber
            if (PRIMARY_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element AssistantTelephoneNumber
            if (ASSISTANT_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element CallbackTelephoneNumber
            if (CALLBACK_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element RadioTelephoneNumber
            if (RADIO_TELEPHONE_NUMBER.equals(nodeName)) {
                contact.getPersonalDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element TelexNumber
            if (TELEX_NUMBER.equals(nodeName)) {
                contact.getBusinessDetail().addPhone(createNewPhone(child));
                continue;
            }
            //Element Email1Address
            if (EMAIL1_ADDRESS.equals(nodeName)) {
                contact.getPersonalDetail().addEmail(createNewEmail(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("OtherEmail" + j + "Address").equals(nodeName)) {
                    contact.getPersonalDetail().addEmail(createNewEmail(child));
                    continue out;
                }
            }
            //Element Email2Address
            if (EMAIL2_ADDRESS.equals(nodeName)) {
                contact.getPersonalDetail().addEmail(createNewEmail(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("HomeEmail" + j + "Address").equals(nodeName)) {
                    contact.getPersonalDetail().addEmail(createNewEmail(child));
                    continue;
                }
            }
            //Element Email3Address
            if (EMAIL3_ADDRESS.equals(nodeName)) {
                contact.getBusinessDetail().addEmail(createNewEmail(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("BusinessEmail" + j + "Address").equals(nodeName)) {
                    contact.getBusinessDetail().addEmail(createNewEmail(child));
                    continue out;
                }
            }
            //Element InstantMessenger
            if (INSTANT_MESSENGER.equals(nodeName)) {
                contact.getPersonalDetail().addEmail(createNewEmail(child));
                continue;
            }
            //Element WebPage
            if (WEB_PAGE.equals(nodeName)) {
                contact.getPersonalDetail().addWebPage(createNewWebPage(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if ((WEB_PAGE+j).equals(nodeName)) {
                    contact.getPersonalDetail().addWebPage(createNewWebPage(child));
                    continue out;
                }
            }
            //Element HomeWebPage
            if (HOME_WEB_PAGE.equals(nodeName)) {
                contact.getPersonalDetail().addWebPage(createNewWebPage(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Home" + j + "WebPage").equals(nodeName)) {
                    contact.getPersonalDetail().addWebPage(createNewWebPage(child));
                    continue out;
                }
            }
            //Element BusinessWebPage
            if (BUSINESS_WEB_PAGE.equals(nodeName)) {
                contact.getBusinessDetail().addWebPage(createNewWebPage(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Business" + j + "WebPage").equals(nodeName)) {
                    contact.getBusinessDetail().addWebPage(createNewWebPage(child));
                    continue out;
                }
            }

            //Element Title
            if (TITLE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getSalutation().setPropertyValue(prop.getPropertyValue());
                contact.getName().getSalutation().setEncoding(prop.getEncoding());
                contact.getName().getSalutation().setGroup(prop.getGroup());
                contact.getName().getSalutation().setCharset(prop.getCharset());
                contact.getName().getSalutation().setLanguage(prop.getLanguage());
                contact.getName().getSalutation().setValue(prop.getValue());
                continue;
            }

            //Element FirstName
            if (FIRST_NAME.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getFirstName().setPropertyValue(prop.getPropertyValue());
                contact.getName().getFirstName().setEncoding(prop.getEncoding());
                contact.getName().getFirstName().setGroup(prop.getGroup());
                contact.getName().getFirstName().setCharset(prop.getCharset());
                contact.getName().getFirstName().setLanguage(prop.getLanguage());
                contact.getName().getFirstName().setValue(prop.getValue());
                continue;
            }
            //Element MiddleName
            if (MIDDLE_NAME.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getMiddleName().setPropertyValue(prop.getPropertyValue());
                contact.getName().getMiddleName().setEncoding(prop.getEncoding());
                contact.getName().getMiddleName().setGroup(prop.getGroup());
                contact.getName().getMiddleName().setCharset(prop.getCharset());
                contact.getName().getMiddleName().setLanguage(prop.getLanguage());
                contact.getName().getMiddleName().setValue(prop.getValue());
                continue;
            }
            //Element LastName
            if (LAST_NAME.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getLastName().setPropertyValue(prop.getPropertyValue());
                contact.getName().getLastName().setEncoding(prop.getEncoding());
                contact.getName().getLastName().setGroup(prop.getGroup());
                contact.getName().getLastName().setCharset(prop.getCharset());
                contact.getName().getLastName().setLanguage(prop.getLanguage());
                contact.getName().getLastName().setValue(prop.getValue());
                continue;
            }
            //Element Suffix
            if (SUFFIX.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getSuffix().setPropertyValue(prop.getPropertyValue());
                contact.getName().getSuffix().setEncoding(prop.getEncoding());
                contact.getName().getSuffix().setGroup(prop.getGroup());
                contact.getName().getSuffix().setCharset(prop.getCharset());
                contact.getName().getSuffix().setLanguage(prop.getLanguage());
                contact.getName().getSuffix().setValue(prop.getValue());
                continue;
            }
            //Element Nickname
            if (NICK_NAME.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getNickname().setPropertyValue(prop.getPropertyValue());
                contact.getName().getNickname().setEncoding(prop.getEncoding());
                contact.getName().getNickname().setGroup(prop.getGroup());
                contact.getName().getNickname().setCharset(prop.getCharset());
                contact.getName().getNickname().setLanguage(prop.getLanguage());
                contact.getName().getNickname().setValue(prop.getValue());
                continue;
            }
            //Element FileAs
            if (FILE_AS.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getName().getDisplayName().setPropertyValue(prop.getPropertyValue());
                contact.getName().getDisplayName().setEncoding(prop.getEncoding());
                contact.getName().getDisplayName().setGroup(prop.getGroup());
                contact.getName().getDisplayName().setCharset(prop.getCharset());
                contact.getName().getDisplayName().setLanguage(prop.getLanguage());
                contact.getName().getDisplayName().setValue(prop.getValue());
                continue;
            }
            //Element Body
            if (BODY.equals(nodeName)) {
                contact.addNote(createNewNote(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if (("Body" + j).equals(nodeName)) {
                    contact.addNote(createNewNote(child));
                    continue out;
                }
            }

            //Element CompanyName
            if (COMPANY_NAME.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getCompany().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getCompany().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getCompany().setGroup(prop.getGroup());
                contact.getBusinessDetail().getCompany().setCharset(prop.getCharset());
                contact.getBusinessDetail().getCompany().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getCompany().setValue(prop.getValue());
                continue;
            }
            //Element Department
            if (DEPARTMENT.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getDepartment().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getDepartment().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getDepartment().setGroup(prop.getGroup());
                contact.getBusinessDetail().getDepartment().setCharset(prop.getCharset());
                contact.getBusinessDetail().getDepartment().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getDepartment().setValue(prop.getValue());
                continue;
            }
            //Element Profession
            if (PROFESSION.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getRole().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getRole().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getRole().setGroup(prop.getGroup());
                contact.getBusinessDetail().getRole().setCharset(prop.getCharset());
                contact.getBusinessDetail().getRole().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getRole().setValue(prop.getValue());
                continue;
            }
            //Element JobTitle
            if (JOB_TITLE.equals(nodeName)) {
                contact.getBusinessDetail().addTitle(createNewTitle(child));
                continue;
            }
            for (j = 2; j <= 10; j++) {
                if ((JOB_TITLE+j).equals(nodeName)) {
                    contact.getBusinessDetail().addTitle(createNewTitle(child));
                    continue out;
                }
            }
            //AddressProperties
            //Element BusinessAddressCity
            if (BUSINESS_ADDRESS_CITY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getCity().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getCity().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getCity().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getCity().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getCity().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getCity().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressCountry
            if (BUSINESS_ADDRESS_COUNTRY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getCountry().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getCountry().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getCountry().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getCountry().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getCountry().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getCountry().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressPostalCode
            if (BUSINESS_ADDRESS_POSTAL_CODE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getPostalCode().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getPostalCode().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getPostalCode().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getPostalCode().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getPostalCode().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getPostalCode().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressPostOfficeBox
            if (BUSINESS_ADDRESS_POST_OFFICE_BOX.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getPostOfficeAddress().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressPostOfficeBox
            if (BUSINESS_ADDRESS_EXTENDED.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getExtendedAddress().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getExtendedAddress().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getExtendedAddress().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getExtendedAddress().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getExtendedAddress().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getExtendedAddress().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressState
            if (BUSINESS_ADDRESS_STATE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getState().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getState().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getState().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getState().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getState().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getState().setValue(prop.getValue());
                continue;
            }
            //Element BusinessAddressStreet
            if (BUSINESS_ADDRESS_STREET.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getStreet().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getStreet().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getStreet().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getStreet().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getStreet().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getStreet().setValue(prop.getValue());
                continue;
            }
            //Element OfficeLocation
            if (OFFICE_LOCATION.equals(nodeName)) {
                contact.getBusinessDetail().setOfficeLocation(getNodeContent(child));
                continue;
            }
            //Element BusinessLabel
            if (BUSINESS_LABEL.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getBusinessDetail().getAddress().getLabel().setPropertyValue(prop.getPropertyValue());
                contact.getBusinessDetail().getAddress().getLabel().setEncoding(prop.getEncoding());
                contact.getBusinessDetail().getAddress().getLabel().setGroup(prop.getGroup());
                contact.getBusinessDetail().getAddress().getLabel().setCharset(prop.getCharset());
                contact.getBusinessDetail().getAddress().getLabel().setLanguage(prop.getLanguage());
                contact.getBusinessDetail().getAddress().getLabel().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressCity
            if (HOME_ADDRESS_CITY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getCity().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getCity().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getCity().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getCity().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getCity().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getCity().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressCountry
            if (HOME_ADDRESS_COUNTRY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getCountry().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getCountry().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getCountry().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getCountry().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getCountry().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getCountry().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressPostalCode
            if (HOME_ADDRESS_POSTAL_CODE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getPostalCode().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getPostalCode().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getPostalCode().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getPostalCode().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getPostalCode().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getPostalCode().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressPostOfficeBox
            if (HOME_ADDRESS_POST_OFFICE_BOX.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getPostOfficeAddress().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressExtended
            if (HOME_ADDRESS_EXTENDED.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getExtendedAddress().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getExtendedAddress().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getExtendedAddress().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getExtendedAddress().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getExtendedAddress().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getExtendedAddress().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressState
            if (HOME_ADDRESS_STATE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getState().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getState().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getState().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getState().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getState().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getState().setValue(prop.getValue());
                continue;
            }
            //Element HomeAddressStreet
            if (HOME_ADDRESS_STREET.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getStreet().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getStreet().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getStreet().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getStreet().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getStreet().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getStreet().setValue(prop.getValue());
                continue;
            }
            //Element HomeLabel
            if (HOME_LABEL.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getAddress().getLabel().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getAddress().getLabel().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getAddress().getLabel().setGroup(prop.getGroup());
                contact.getPersonalDetail().getAddress().getLabel().setCharset(prop.getCharset());
                contact.getPersonalDetail().getAddress().getLabel().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getAddress().getLabel().setValue(prop.getValue());
                continue;
            }
            //--------
            //Element OtherAddressCity
            if (OTHER_ADDRESS_CITY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getCity().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getCity().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getCity().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getCity().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getCity().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getCity().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressCountry
            if (OTHER_ADDRESS_COUNTRY.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getCountry().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getCountry().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getCountry().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getCountry().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getCountry().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getCountry().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressPostalCode
            if (OTHER_ADDRESS_POSTAL_CODE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getPostalCode().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressPostalCode
            if (OTHER_ADDRESS_EXTENDED.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getExtendedAddress().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressPostOfficeBox
            if (OTHER_ADDRESS_POST_OFFICE_BOX.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getPostOfficeAddress().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressState
            if (OTHER_ADDRESS_STATE.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getState().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getState().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getState().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getState().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getState().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getState().setValue(prop.getValue());
                continue;
            }
            //Element OtherAddressStreet
            if (OTHER_ADDRESS_STREET.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getStreet().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getStreet().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getStreet().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getStreet().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getStreet().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getStreet().setValue(prop.getValue());
                continue;
            }
            //Element OtherLabel
            if (OTHER_LABEL.equals(nodeName)) {
                prop=createPropertyFromTag(child);
                contact.getPersonalDetail().getOtherAddress().getLabel().setPropertyValue(prop.getPropertyValue());
                contact.getPersonalDetail().getOtherAddress().getLabel().setEncoding(prop.getEncoding());
                contact.getPersonalDetail().getOtherAddress().getLabel().setGroup(prop.getGroup());
                contact.getPersonalDetail().getOtherAddress().getLabel().setCharset(prop.getCharset());
                contact.getPersonalDetail().getOtherAddress().getLabel().setLanguage(prop.getLanguage());
                contact.getPersonalDetail().getOtherAddress().getLabel().setValue(prop.getValue());
                continue;
            }

            //Element companies
            if (COMPANIES.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getBusinessDetail().setCompanies(content);
                }
                continue;
            }

            //Element hobbies
            if (HOBBY.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.getPersonalDetail().setHobbies(content);
                }
                continue;
            }

            //Element photo
            if (PHOTO.equals(nodeName)) {
                Property photo = contact.getPersonalDetail().getPhoto();
                prop = createPropertyFromTag(child);
                photo.setPropertyValue(prop.getPropertyValue());
                if (prop.getPropertyValueAsString() == null || 
                    prop.getPropertyValueAsString().length() == 0) {
                    continue;
                }

                if (prop.getEncoding() != null) {
                    photo.setEncoding(prop.getEncoding());
                } else {
                    //
                    // Default encoding in SIF
                    //
                    photo.setEncoding("BASE64");
                }
                photo.setGroup(prop.getGroup());
                photo.setCharset(prop.getCharset());
                photo.setLanguage(prop.getLanguage());
                photo.setValue(prop.getValue());
                if (prop.getType() != null) {
                    photo.setType(prop.getType());
                } else {
                    //
                    // Default type in SIF
                    //
                    photo.setType("JPEG");
                }
                continue;
            }

            //Element initials
            if (INITIALS.equals(nodeName)) {
                Property initials = contact.getName().getInitials();
                prop=createPropertyFromTag(child);
                initials.setPropertyValue(prop.getPropertyValue());
                initials.setEncoding(prop.getEncoding());
                initials.setGroup(prop.getGroup());
                initials.setCharset(prop.getCharset());
                initials.setLanguage(prop.getLanguage());
                initials.setValue(prop.getValue());
                continue;
            }

            //Element language
            if (LANGUAGE.equals(nodeName)) {
                content=getNodeContent(child);
                if (content!=null) {
                    contact.setLanguages(content);
                }
                continue;
            }
            
            //Element InternetFreeBusyAddress
            if (FREEBUSY.equals(nodeName)) {
                content = getNodeContent(child);
                if (content != null) {
                    contact.setFreeBusy(content);
                }
            }
            //Element X-TAG
            if (nodeName.startsWith("X-")) {
                contact.addXTag(createNewXTag(child));
                continue;
            }

        }

        return (contact);
    }

    /**
     * Create a Property object from a XML node
     */
    private Property createPropertyFromTag(Node child) {
        Property prop=new Property();
        prop.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes= child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    prop.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    prop.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    prop.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    prop.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    prop.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("type")) {
                    prop.setType(attribute.getNodeValue());
                }
            }
        }
        return (prop);
    }

    /**
     * create a JobTitle object from a XML node
     */
    private Title createNewTitle (Node child) {
        String nodeName = child.getNodeName();
        Title newTitle = new Title();
        newTitle.setTitleType(child.getNodeName());
        newTitle.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes = child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newTitle.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newTitle.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newTitle.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newTitle.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newTitle.setValue(attribute.getNodeValue());
                }
            }
        }
        return newTitle;
    }

    /**
     * create a Note object from a XML node
     */
    private Note createNewNote (Node child) {
        Note newNote = new Note();
        newNote.setNoteType(child.getNodeName());
        newNote.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes = child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newNote.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newNote.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newNote.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newNote.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newNote.setValue(attribute.getNodeValue());
                }
            }
        }
        return newNote;
    }

    /**
     * create a Note object from a XML node
     */
    private XTag createNewXTag (Node child) {
        XTag newXTag = new XTag();
        newXTag.setXTagValue(child.getNodeName());
        newXTag.getXTag().setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes = child.getAttributes();
        Node attribute;
        HashMap hash = new HashMap();
        for (int i=0;i<attributes.getLength();i++) {

            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newXTag.getXTag().setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newXTag.getXTag().setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newXTag.getXTag().setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newXTag.getXTag().setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newXTag.getXTag().setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().startsWith("X-")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
        if (hash.size() > 0) {
           newXTag.getXTag().setXParams(hash);
        }

        return newXTag;
    }

    /**
     * create a Email object from a XML node
     */
    private Email createNewEmail (Node child) {
        Email newEmail = new Email();
        newEmail.setEmailType(child.getNodeName());
        newEmail.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes = child.getAttributes();
        Node attribute;
        HashMap hash = new HashMap();
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newEmail.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newEmail.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newEmail.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newEmail.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newEmail.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().startsWith("X-")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
        if (hash.size() > 0) {
           newEmail.setXParams(hash);
        }
        return newEmail;
    }

    /**
     * create a WebPage object from a XML node
     */
    private WebPage createNewWebPage (Node child) {
        WebPage newWebPage = new WebPage();
        newWebPage.setWebPageType(child.getNodeName());
        newWebPage.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes = child.getAttributes();
        Node attribute;
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newWebPage.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newWebPage.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newWebPage.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newWebPage.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newWebPage.setValue(attribute.getNodeValue());
                }
            }
        }

        return newWebPage;
    }

    /**
     * create a Phone object from a XML node
     */
    private Phone createNewPhone (Node child) {
        Phone newPhone = new Phone();
        newPhone.setPhoneType(child.getNodeName());
        newPhone.setPropertyValue(getNodeContent(child));

        //setting attributes
        NamedNodeMap attributes= child.getAttributes();
        Node attribute;
        HashMap hash = new HashMap();
        for (int i=0;i<attributes.getLength();i++) {
            if (attributes.item(i) instanceof Attr) {
                attribute=attributes.item(i);
                if (attribute.getNodeName().equalsIgnoreCase("group")) {
                    newPhone.setGroup(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("encoding")) {
                    newPhone.setEncoding(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("charset")) {
                    newPhone.setCharset(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("language")) {
                    newPhone.setLanguage(attribute.getNodeValue());
                }
                if (attribute.getNodeName().equalsIgnoreCase("value")) {
                    newPhone.setValue(attribute.getNodeValue());
                }
                if (attribute.getNodeName().startsWith("X-")) {
                    hash.put(attribute.getNodeName(), attribute.getNodeValue());
                }
            }
        }
        if (hash.size() > 0) {
           newPhone.setXParams(hash);
        }
        return newPhone;
    }
}
