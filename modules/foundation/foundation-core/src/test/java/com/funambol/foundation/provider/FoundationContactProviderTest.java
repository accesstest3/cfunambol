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
package com.funambol.foundation.provider;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.funambol.common.pim.contact.Contact;
import com.funambol.foundation.util.TestDef;
import com.funambol.tools.database.DBHelper;

/**
 * <code>FoundationContactProvider</code>'s test cases.
 *
 * @author $Id: FoundationContactProviderTest.java,v 1.1.1.1 2008-03-20 21:39:14 stefano_fornari Exp $
 */
public class FoundationContactProviderTest
extends ProviderTestCase
implements TestDef {

    // --------------------------------------------------- Static initialization
    static {
        try {

            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE,
                                                      USER_SCHEMA_SOURCE,
                                                      false);
            assertTrue("Error initializing the database", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private PIMContactManagerMock manager;
    private FoundationContactProvider provider;
    private static final String userId = DBHelper.USER_PART1;

    // ------------------------------------------------------------ Constructors
    public FoundationContactProviderTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        manager = new PIMContactManagerMock(userId);
        provider = new FoundationContactProvider(manager);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // ---------------------------------------------------------- Public methods
    // getAddressesForUser returns an empty array if no emails are associated
    // to any contact
    public void testGetAddressesForUser_0() throws Exception {

        // expected email addresses
        InternetAddress[] expected = new InternetAddress[0];

        // initialize mock backend
        Contact c1 = manager.addContact();
        Contact c2 = manager.addContact();

        // test
        InternetAddress[] result = provider.getAddressesForUser(userId);

        assertEquals(0, result.length);
        assertEqualsAddressesArray(expected, result);

    }

    // some contacts could have emails, other could have no emails
    public void testGetAddressesForUser_1() throws Exception {

        // expected email addresses
        String s1 = "piter@funambol.com";
        String s2 = "pit <piter@funambol.com>";
        String s3 = "=?ISO-8859-15?Q?p=E8=E9=F2TYU?= <piter@funambol.com>";

        InternetAddress[] expected =
                createExpected(new String[] {s1, s2, s3});

        // initialize mock backend
        Contact c1 = manager.addContact();
        manager.addPersonalEmail(c1, s1, "Email1Address");
        manager.addPersonalEmail(c1, s2, "Email2Address");
        manager.addBusinessEmail(c1, s3, "Email3Address");

        // test
        InternetAddress[] result = provider.getAddressesForUser(userId);

        assertEqualsAddressesArray(expected, result);
    }

    // all the contacts have some emails
    public void testGetAddressesForUser_2() throws Exception {

        // expected email addresses
        String s1 = "piter@funambol.com";
        String s2 = "pit <piter@funambol.com>";
        String s3 = "=?ISO-8859-15?Q?p=E8=E9=F2TYU?= <piter@funambol.com>";

        InternetAddress[] expected =
                createExpected(new String[] {s1, s2, s3});

        // initialize mock backend
        Contact c1 = manager.addContact();
        manager.addPersonalEmail(c1, s1, "Email1Address");
        manager.addPersonalEmail(c1, s2, "Email2Address");

        Contact c2 = manager.addContact();
        manager.addBusinessEmail(c1, s3, "Email3Address");

        // test
        InternetAddress[] result = provider.getAddressesForUser(userId);

        assertEqualsAddressesArray(expected, result);
    }

    // --------------------------------------------------------- Private methods
    private InternetAddress[] createExpected(String[] addressStrings)
    throws AddressException {

        if (addressStrings == null){
            return null;
        }

        List<InternetAddress> expected = new ArrayList();
        for (String addressString : addressStrings) {
            expected.add(new InternetAddress(addressString));
        }
        return expected.toArray(new InternetAddress[expected.size()]);
    }

}






