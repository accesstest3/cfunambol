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

package com.funambol.common.pim.converter;

import com.funambol.common.pim.common.Property;
import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.IMPPAddress;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Photo;
import com.funambol.framework.tools.IOTools;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 * ContactToVcard test cases
 * @version $Id: ContactToVcardTest.java,v 1.3 2007-11-28 11:14:31 nichele Exp $
 */
public class ContactToVcardTest extends TestCase {

    public ContactToVcardTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases
    public void testConvert_Photo1() throws Exception {
        Contact contactWithPhoto = new Contact();
        byte[] image = IOTools.readFileBytes("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/photo-1.gif");
        Photo photo = new Photo();
        photo.setImage(image);
        photo.setType("GIF");
        contactWithPhoto.getPersonalDetail().setPhotoObject(photo);
        contactWithPhoto.getName().getFirstName().setPropertyValue("John");
        contactWithPhoto.getName().getLastName().setPropertyValue("Brown");
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contactWithPhoto);

        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-1.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }

    public void testConvert_Photo2() throws Exception {
        Contact contactWithPhoto = new Contact();

        Photo photo = new Photo();
        photo.setUrl("http://www.myphotos.com/abc.jpg");
        photo.setType("JPG");
        contactWithPhoto.getPersonalDetail().setPhotoObject(photo);
        contactWithPhoto.getName().getFirstName().setPropertyValue("John");
        contactWithPhoto.getName().getLastName().setPropertyValue("Brown");
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contactWithPhoto);

        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-2.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }

    public void testConvert_SimpleContact() throws Exception {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("John");
        contact.getName().getLastName().setPropertyValue("Brown");
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contact);
        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-3.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }
    
    public void testConvert_CustomFields() throws Exception {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Eve");
        contact.getName().getLastName().setPropertyValue("Adams");
        contact.setUid("219839123sajn095657");
        contact.getPersonalDetail().setAnniversary("1968-01-12");
        contact.getPersonalDetail().setChildren("Cain;Abel");
        contact.getPersonalDetail().setSpouse("Adam");
        contact.getBusinessDetail().setCompanies("Eden Ltd;Genesis Hospital");
        contact.getBusinessDetail().setManager("Mr Snake");
        contact.setLanguages("Hebrew;Greek;Latin");
        contact.setMileage("144000");
        contact.setSubject("This is Eve");
        Phone callback = new Phone("098-897-899878689");
        callback.setPhoneType("CallbackTelephoneNumber");
        Phone radio = new Phone("011-2323-423123");
        radio.setPhoneType("RadioTelephoneNumber");
        Phone telex = new Phone("0546-34243545656");
        telex.setPhoneType("TelexNumber");
        List<Phone> personalPhones = new ArrayList<Phone>(1);
        personalPhones.add(radio);
        contact.getPersonalDetail().setPhones(personalPhones);
        List<Phone> businessPhones = new ArrayList<Phone>(2);
        businessPhones.add(callback);
        businessPhones.add(telex);
        contact.getBusinessDetail().setPhones(businessPhones);
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contact);
        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-4.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }

    public void testConvert_OtherFields() throws Exception {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Berta");
        contact.getName().getLastName().setPropertyValue("Aliorum");
        contact.setUid("{TYPE=TEXT}UHSDAKGJGFN");
        contact.setTimezone("Europe/Rome");
        contact.setMailer("Mozilla Thunderbird");
        Email mobileEmail = new Email("myemail@mycellphone.com");
        mobileEmail.setEmailType("MobileEmailAddress");
        contact.getPersonalDetail().addEmail(mobileEmail);
        contact.getPersonalDetail().setGeo(new Property("45.005;46.324"));
        contact.getBusinessDetail().setOfficeLocation("Ufficio Reclami");
        contact.getBusinessDetail().setAssistant("Assistant");
        Phone assistantPhone = new Phone("12312323123123");
        assistantPhone.setPhoneType("AssistantTelephoneNumber");
        contact.getBusinessDetail().addPhone(assistantPhone);
        contact.setProductID("Product-ID");
        Property publicKey = new Property(
                     "MIICajCCAdOgAwIBAgICBEUwDQYJKoZIhvcNAQEEBQAwdzELMAkGA1U" +
                     "EBhMCVVMxLDAqBgNVBAoTI05ldHNjYXBlIENbW11bmljYXRpb25zIEN" +
                     "vcnBvcmF0aW9uMRwwGgYDVQQLExNJbmZvcm1hdGlvbiBTeXN0ZW1zMR" +
                     "wwGgYDVQQDExNyb290Y2EubmV0c2NhcGUuY29tMB4XDTk3MDYwNjE5N" +
                     "Dc1OVoXDTk3MTIwMzE5NDc1OVowgYkxCzAJBgNVBAYTAlVTMSYwJAYD" +
                     "VQQKEx1OZXRzY2FwZSBDb21tdW5pY2F0aW9ucyBDb3JwLjEYMBYGA1U" +
                     "EAxMPVGltb3RoeSBBIEhvd2VzMSEwHwYJKoZIhvcNAQkBFhJob3dlc0" +
                     "BuZXRzY2FwZS5jb20xFTATBgoJkiaJk/IsZAEBEwVob3dlczBcMA0GC" +
                     "SqGSIb3DQEBAQUAA0sAMEgCQQC0JZf6wkg8pLMXHHCUvMfL5H6zjSk4" +
                     "vTTXZpYyrdN2dXcoX49LKiOmgeJSzoiFKHtLOIboyludF90CgqcxtwK" +
                     "nAgMBAAGjNjA0MBEGCWCGSAGG+EIBAQQEAwIAoDAfBgNVHSMEGDAWgB" +
                     "T84FToB/GV3jr3mcau+hUMbsQukjANBgkqhkiG9w0BAQQFAAOBgQBex" +
                     "v7o7mi3PLXadkmNP9LcIPmx93HGp0Kgyx1jIVMyNgsemeAwBM+MSlhM" +
                     "fcpbTrONwNjZYW8vJDSoi//yrZlVt9bJbs7MNYZVsyF1unsqaln4/vy" +
                     "6Uawfg8VUMk1U7jt8LYpo4YULU7UZHPYVUaSgVttImOHZIKi4hlPXBO" +
                     "hcUQ==");
        publicKey.setEncoding("BASE64");
        contact.setKey(publicKey);
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contact);
        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-5.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }

public void testConvert_IMPP() throws Exception {
        Contact contact = new Contact();
        contact.getName().getFirstName().setPropertyValue("Istantaneo");
        contact.getName().getLastName().setPropertyValue("Messaggiante");
        contact.addIMPPAddress(new IMPPAddress("aim:carpediem01@aol.com",
                                               IMPPAddress.PERSONAL,
                                               IMPPAddress.HOME,
                                               false));
        contact.addIMPPAddress(new IMPPAddress("ymsgr:carpe_diem@yahoo.com",
                                               IMPPAddress.PERSONAL,
                                               null,
                                               true));
        contact.addIMPPAddress(new IMPPAddress("xmpp:carpe.diem@gmail.com",
                                               IMPPAddress.PERSONAL,
                                               IMPPAddress.MOBILE,
                                               false));
        contact.addIMPPAddress(new IMPPAddress("msn:i_messaggiante@msn.com",
                                               IMPPAddress.BUSINESS,
                                               IMPPAddress.WORK,
                                               true));
        contact.addIMPPAddress(new IMPPAddress("irc:MeSsAgGiAnTe",
                                               null,
                                               null,
                                               false));
        ContactToVcard converter = new ContactToVcard(null, "UTF-8");
        String vCard = converter.convert(contact);
        String expectedVCard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/converter/contact-to-vcard/vcard-6.vcf");

        assertEquals(expectedVCard.replaceAll("\\r", ""), vCard.replaceAll("\\r", ""));
    }
    
    // ------------------------------------------------------- Protected methods
    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }



}
