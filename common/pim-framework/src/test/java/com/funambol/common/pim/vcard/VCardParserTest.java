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

package com.funambol.common.pim.vcard;

import java.io.ByteArrayInputStream;
import java.util.List;

import junit.framework.TestCase;
import junitx.framework.ArrayAssert;

import com.funambol.common.pim.contact.Contact;
import com.funambol.common.pim.contact.Email;
import com.funambol.common.pim.contact.IMPPAddress;
import com.funambol.common.pim.contact.Note;
import com.funambol.common.pim.contact.Phone;
import com.funambol.common.pim.contact.Photo;
import com.funambol.common.pim.contact.Title;
import com.funambol.framework.tools.IOTools;

/**
 * VCardParser test cases
 * @version $Id: VCardParserTest.java,v 1.4 2008-08-26 15:51:06 luigiafassina Exp $
 */
public class VCardParserTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String PHOTO_GIF =
        "src/test/data/com/funambol/common/pim/vcard/vcard-parser/photo.gif";
    private static final String NOTE_TXT =
        "src/test/data/com/funambol/common/pim/vcard/vcard-parser/note.txt";

    // ------------------------------------------------------------- Constructor
    public VCardParserTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /*
     * Testing photo support
     */
    public void testParse_Photo1() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-1.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        Photo expectedPhoto = new Photo("GIF", "http://www.abc.com/dir_photos/my_photo.gif", null);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_Photo2() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-2.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        byte[] expectedImage = IOTools.readFileBytes(PHOTO_GIF);

        Photo expectedPhoto = new Photo("GIF", null, expectedImage);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());

    }

    public void testParse_Photo3() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-3.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        byte[] expectedImage = IOTools.readFileBytes(PHOTO_GIF);

        Photo expectedPhoto = new Photo("GIF", null, expectedImage);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());

    }

    public void testParse_EmptyPhoto_4() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-4.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertNotNull(c.getPersonalDetail().getPhotoObject());
        assertNull(c.getPersonalDetail().getPhotoObject().getUrl());
        assertNull(c.getPersonalDetail().getPhotoObject().getImage());

    }

    public void testParse_NoPhoto_5() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-5.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertNull(c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_PhotoWithNewLine() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-26.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertNotNull(c.getPersonalDetail().getPhotoObject());
        byte[] expectedImage = IOTools.readFileBytes(PHOTO_GIF);

        Photo expectedPhoto = new Photo("GIF", null, expectedImage);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_PhotoWithMoreNewLines() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-27.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertNotNull(c.getPersonalDetail().getPhotoObject());
        byte[] expectedImage = IOTools.readFileBytes(PHOTO_GIF);

        Photo expectedPhoto = new Photo("GIF", null, expectedImage);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    public void testParse_NoteWithMoreNewLines() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-28.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertNotNull(c.getNotes().get(0));
        Note n = (Note)c.getNotes().get(0);
        String result = n.getPropertyValueAsString();
        result = result.trim().replaceAll("\r\n", "\n");
        String expectedNote = IOTools.readFileString(NOTE_TXT);
        expectedNote = expectedNote.trim().replaceAll("\r\n", "\n");
        assertEquals(expectedNote, result);
    }

    /*
     * Testing line-break parsing
     */
    public void testParse_LineBreaks() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-6.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertEquals("First",
                c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last",
                c.getName().getLastName().getPropertyValueAsString());
        assertEquals("Full Name",
                c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Categories written on several lines and using Quoted-Printable encoding",
                c.getCategories().getPropertyValueAsString());
        assertEquals("Company name written on several lines",
                c.getBusinessDetail().getCompany().getPropertyValueAsString());

        byte[] expectedImage = IOTools.readFileBytes(PHOTO_GIF);

        Photo expectedPhoto = new Photo("GIF", null, expectedImage);
        comparePhoto(expectedPhoto, c.getPersonalDetail().getPhotoObject());
    }

    /**
     * Testing IMAddress email
     */
    public void testParse_IMAddress1() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-7.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];
        Email email = new Email("");
        email.setEmailType("Email1Address");
        emailsExpected[0] = email;

        email = new Email("");
        email.setEmailType("Email2Address");
        emailsExpected[1] = email;

        email = new Email("imaddress.doe");
        email.setEmailType("IMAddress");
        emailsExpected[2] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    public void testParse_IMAddress2() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-8.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];
        Email email = new Email("email1@gmail.com");
        email.setEmailType("Email1Address");
        emailsExpected[0] = email;

        email = new Email("");
        email.setEmailType("Email2Address");
        emailsExpected[1] = email;

        email = new Email("loren.adr");
        email.setEmailType("IMAddress");
        emailsExpected[2] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    public void testParse_IMAddress3() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-9.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];
        Email email = new Email("email1@yahoo.com");
        email.setEmailType("Email1Address");
        emailsExpected[0] = email;

        email = new Email("email2@fun.com");
        email.setEmailType("Email2Address");
        emailsExpected[1] = email;

        email = new Email("messenger.pt");
        email.setEmailType("IMAddress");
        emailsExpected[2] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));

        List<Email> businessEmails = c.getBusinessDetail().getEmails();

        Email[] bEmailsExpected = new Email[1];
        Email bEmail = new Email("email3@yahoo.com");
        bEmail.setEmailType("Email3Address");
        bEmailsExpected[0] = bEmail;

        ArrayAssert.assertEquals(bEmailsExpected, businessEmails.toArray(new Email[businessEmails.size()]));

    }

    public void testParse_IMAddress4() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-10.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];
        Email email = new Email("");
        email.setEmailType("Email1Address");
        emailsExpected[0] = email;

        email = new Email("email2@gmail.com");
        email.setEmailType("Email2Address");
        emailsExpected[1] = email;

        email = new Email("");
        email.setEmailType("IMAddress");
        emailsExpected[2] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    public void testParse_IMAddress5() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-11.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];
        Email email = new Email("miller.lucy");
        email.setEmailType("IMAddress");
        emailsExpected[0] = email;

        email = new Email("");
        email.setEmailType("Email1Address");
        emailsExpected[1] = email;

        email = new Email("email2@gmail.com");
        email.setEmailType("Email2Address");
        emailsExpected[2] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    public void testParse_IMAddress6() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-12.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[2];
        Email email = new Email("");
        email.setEmailType("Email1Address");
        emailsExpected[0] = email;

        email = new Email("email2@gmail.com");
        email.setEmailType("Email2Address");
        emailsExpected[1] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    /*
     * Testing a vcard with 1 not empty instant messenger address.
     * The instant messenger field IMAddress must be populated with that value.
     *
     * [...]
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:email1@gmail.com
     * [...]
     *
     * See: Bug 9162 - [CANONICAL] Istant Messenger field is not managed
     *      correctly in case of multiple IMs.
     */
    public void testParse_IMAddress7() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-22.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[1];

        Email email = new Email("email1@gmail.com");
        email.setEmailType("IMAddress");
        emailsExpected[0] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    /*
     * Testing a vcard with 1 empty instant messenger address.
     * The instant messenger field IMAddress must be populated with the empty
     * string.
     *
     * [...]
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:
     * [...]
     *
     * See: Bug 9162 - [CANONICAL] Istant Messenger field is not managed
     *      correctly in case of multiple IMs.
     */
    public void testParse_IMAddress8() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-23.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[1];

        Email email = new Email("");
        email.setEmailType("IMAddress");
        emailsExpected[0] = email;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    /*
     * Testing a vcard with 3 not empty instant messenger addresses.
     * The instant messenger field IMAddress must be populated with the first
     * value, the instant messenger field IM2Address must be populated with
     * the second value and the instant messenger field IM3Address must be
     * populated with the third value.
     *
     * [...]
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:email1@gmail.com
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:email2@gmail.com
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:email3@gmail.com
     * [...]
     *
     * See: Bug 9162 - [CANONICAL] Istant Messenger field is not managed
     *      correctly in case of multiple IMs.
     */
    public void testParse_IMAddress9() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-24.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];

        Email email1 = new Email("email1@gmail.com");
        email1.setEmailType("IMAddress");
        emailsExpected[0] = email1;

        Email email2 = new Email("email2@gmail.com");
        email2.setEmailType("IM2Address");
        emailsExpected[1] = email2;

        Email email3 = new Email("email3@gmail.com");
        email3.setEmailType("IM3Address");
        emailsExpected[2] = email3;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }

    /*
     * Testing a vcard with 3 instant messenger addresses, the first of which
     * is not empty and the others are empty.
     * The instant messenger field IMAddress must be populated with the first
     * value, the instant messenger fields IM2Address and IM3Address must be
     * populated with the empty string.
     *
     * [...]
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:email1@gmail.com
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:
     * EMAIL;INTERNET;HOME;X-FUNAMBOL-INSTANTMESSENGER:
     * [...]
     *
     * See: Bug 9162 - [CANONICAL] Istant Messenger field is not managed
     *      correctly in case of multiple IMs.
     */
    public void testParse_IMAddress10() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-25.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<Email> emails = c.getPersonalDetail().getEmails();

        Email[] emailsExpected = new Email[3];

        Email email1 = new Email("email1@gmail.com");
        email1.setEmailType("IMAddress");
        emailsExpected[0] = email1;

        Email email2 = new Email("");
        email2.setEmailType("IM2Address");
        emailsExpected[1] = email2;

        Email email3 = new Email("");
        email3.setEmailType("IM3Address");
        emailsExpected[2] = email3;

        ArrayAssert.assertEquals(emailsExpected, emails.toArray(new Email[emails.size()]));
    }
    
    public void testParse_IMPP() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-19.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        List<IMPPAddress> impps = c.getIMPPs();

        assertEquals("aim:carpediem01@aol.com", impps.get(0).getUri());
        assertEquals("ymsgr:carpe_diem@yahoo.com", impps.get(1).getUri());
        assertEquals("xmpp:carpe.diem@gmail.com", impps.get(2).getUri());
        assertEquals("irc:MeSsAgGiAnTe", impps.get(3).getUri());
        assertEquals("msn:i_messaggiante@msn.com", impps.get(4).getUri());
        
        assertEquals(IMPPAddress.PERSONAL, impps.get(0).getType());
        assertEquals(IMPPAddress.PERSONAL, impps.get(1).getType());
        assertEquals(IMPPAddress.PERSONAL, impps.get(2).getType());
        assertNull(impps.get(3).getType());
        assertEquals(IMPPAddress.BUSINESS, impps.get(4).getType());
        
        assertEquals(IMPPAddress.HOME, impps.get(0).getLocation());
        assertNull(impps.get(1).getLocation());
        assertEquals(IMPPAddress.MOBILE, impps.get(2).getLocation());
        assertNull(impps.get(3).getLocation());
        assertEquals(IMPPAddress.WORK, impps.get(4).getLocation());
        
        assertFalse(impps.get(0).isPreferred());
        assertTrue(impps.get(1).isPreferred());
        assertFalse(impps.get(2).isPreferred());
        assertFalse(impps.get(3).isPreferred());
        assertTrue(impps.get(4).isPreferred());

    }

    public void testParse_Title() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-16.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertEquals("Wrong title list size", 1,
                c.getBusinessDetail().getTitles().size());

        assertEquals("Title is not properly set", "Dr.",
                   ((Title) c.getBusinessDetail().getTitles().get(0)).getPropertyValueAsString());
    }

    public void testParse_EmptyTitle() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-17.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertEquals("Wrong title list size", 1,
                c.getBusinessDetail().getTitles().size());

        assertEquals("Title is not empty", "",
                   ((Title) c.getBusinessDetail().getTitles().get(0)).getPropertyValueAsString());
    }

    public void testParse_CustomFields_1() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-13.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact contact = parser.vCard(new Contact());

        assertEquals("Eve",
                     contact.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Adams",
                     contact.getName().getLastName().getPropertyValueAsString());
        assertEquals("1968-01-12",
                     contact.getPersonalDetail().getAnniversary());
        assertEquals("Cain;Abel",
                     contact.getPersonalDetail().getChildren());
        assertEquals("Adam",
                     contact.getPersonalDetail().getSpouse());
        assertEquals("Eden Ltd;Genesis Hospital",
                     contact.getBusinessDetail().getCompanies());
        assertEquals("Mr Snake",
                     contact.getBusinessDetail().getManager());
        assertEquals("Hebrew;Greek;Latin",
                     contact.getLanguages());
        assertEquals("144000",
                     contact.getMileage());
        assertEquals("This is Eve",
                     contact.getSubject());
        assertEquals(1,
                     contact.getPersonalDetail().getPhones().size());
        assertEquals("011-2323-423123",
                     ((Phone) contact.getPersonalDetail().getPhones().get(0))
                      .getPropertyValueAsString());
        assertEquals("RadioTelephoneNumber",
                     ((Phone) contact.getPersonalDetail().getPhones().get(0))
                      .getPhoneType());
        assertEquals(2,
                     contact.getBusinessDetail().getPhones().size());
        assertEquals("098-897-899878689",
                     ((Phone) contact.getBusinessDetail().getPhones().get(0))
                      .getPropertyValueAsString());
        assertEquals("CallbackTelephoneNumber",
                     ((Phone) contact.getBusinessDetail().getPhones().get(0))
                      .getPhoneType());
        assertEquals("0546-34243545656",
                     ((Phone) contact.getBusinessDetail().getPhones().get(1))
                      .getPropertyValueAsString());
        assertEquals("TelexNumber",
                     ((Phone) contact.getBusinessDetail().getPhones().get(1))
                      .getPhoneType());
    }

    public void testParse_CustomFields_2() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-14.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact contact = parser.vCard(new Contact());

        assertEquals("John",
                     contact.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Doe2",
                     contact.getName().getLastName().getPropertyValueAsString());
        assertEquals("654324787602834-213453",
                     contact.getUid());
        assertEquals("20001122",
                     contact.getPersonalDetail().getAnniversary());
        assertEquals("Jim",
                     contact.getPersonalDetail().getChildren());
        assertEquals("Ana",
                     contact.getPersonalDetail().getSpouse());
        assertEquals("",
                     contact.getBusinessDetail().getCompanies());
        assertEquals("Dern",
                     contact.getBusinessDetail().getManager());
        assertEquals("ebglish",
                     contact.getLanguages());
        assertEquals("34567",
                     contact.getMileage());
        assertEquals("John Doe2",
                     contact.getSubject());
        assertEquals(9,
                     contact.getPersonalDetail().getPhones().size());
        assertEquals("1213445",
                     ((Phone) contact.getPersonalDetail().getPhones().get(8))
                      .getPropertyValueAsString());
        assertEquals("RadioTelephoneNumber",
                     ((Phone) contact.getPersonalDetail().getPhones().get(8))
                      .getPhoneType());
        assertEquals(7,
                     contact.getBusinessDetail().getPhones().size());
        assertEquals("1111",
                     ((Phone) contact.getBusinessDetail().getPhones().get(5))
                      .getPropertyValueAsString());
        assertEquals("CallbackTelephoneNumber",
                     ((Phone) contact.getBusinessDetail().getPhones().get(5))
                      .getPhoneType());
        assertEquals("12345",
                     ((Phone) contact.getBusinessDetail().getPhones().get(6))
                      .getPropertyValueAsString());
        assertEquals("TelexNumber",
                     ((Phone) contact.getBusinessDetail().getPhones().get(6))
                      .getPhoneType());
        assertEquals("myemail@mycellphone.com",
                     ((Email) contact.getPersonalDetail().getEmails().get(2))
                      .getPropertyValueAsString());
        assertEquals("MobileEmailAddress",
                     ((Email) contact.getPersonalDetail().getEmails().get(2))
                      .getEmailType());
        assertEquals(Short.valueOf((short)0),
                     contact.getSensitivity());
    }

    public void testParse_Miscellaneous() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-18.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact contact = parser.vCard(new Contact());

        assertEquals("63.102;12.456",
                     contact.getPersonalDetail().getGeo().getPropertyValueAsString());
        assertEquals("Mailer 2.1",
                     contact.getMailer());
        assertEquals("{TYPE=TEXT}uadsakjfgfgdmbncxver",
                     contact.getUid());
    }

    public void testParse_AgentKeyProdID() throws Exception {
        String vcard =
            IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-21.vcf");
        VcardParser parser =
            new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact contact = parser.vCard(new Contact());

        assertEquals("Darth Maul",
                     contact.getBusinessDetail().getAssistant());
        assertFalse(contact.getBusinessDetail().isAssistantURI());
        assertEquals("-//ONLINE DIRECTORY//NONSGML Version 1//EN",
                     contact.getProductID());
        assertEquals("+1-800-SITH",
                     ((Phone) contact.getBusinessDetail().getPhones().get(0))
                      .getPropertyValueAsString());
        assertEquals("AssistantTelephoneNumber",
                     ((Phone) contact.getBusinessDetail().getPhones().get(0))
                      .getPhoneType());
        assertEquals("MIICajCCAdOgAwIBAgICBEUwDQYJKoZIhvcNAQEEBQAwdzELMAkGA1U" +
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
                     "hcUQ==", contact.getKey().getPropertyValueAsString());
    }

    /*
     * Testing case-insensitiveness
     */
    public void testParse_CaseInsensitive() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-15.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertEquals("First",
                c.getName().getFirstName().getPropertyValueAsString());
        assertEquals("Last",
                c.getName().getLastName().getPropertyValueAsString());
        assertEquals("Full Name",
                c.getName().getDisplayName().getPropertyValueAsString());
        assertEquals("Categories written on several lines and using Quoted-Printable encoding",
                c.getCategories().getPropertyValueAsString());
        assertEquals("Company name written on several lines",
                c.getBusinessDetail().getCompany().getPropertyValueAsString());
    }

    /*
     * Testing all three components of property ORG
     */
    public void testParse_Org() throws Exception {
        String vcard = IOTools.readFileString("src/test/data/com/funambol/common/pim/vcard/vcard-parser/vcard-20.vcf");
        VcardParser parser = new VcardParser(new ByteArrayInputStream(vcard.getBytes()));
        Contact c = parser.vCard(new Contact());

        assertEquals("Company",
                c.getBusinessDetail().getCompany().getPropertyValueAsString());
        assertEquals("Department",
                c.getBusinessDetail().getDepartment().getPropertyValueAsString());
        assertEquals("Office",
                c.getBusinessDetail().getOfficeLocation());
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // --------------------------------------------------------- Private methods

    /**
     * Compares two photos
     * @param photo1
     * @param photo2
     */
    private void comparePhoto(Photo photo1, Photo photo2) {
        assertEquals(photo1.getType(), photo2.getType());
        assertEquals(photo1.getUrl(), photo2.getUrl());
        ArrayAssert.assertEquals(photo1.getImage(), photo2.getImage());
    }
}