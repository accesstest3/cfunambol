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

import com.funambol.common.pim.contact.*;
import com.funambol.foundation.exception.EntityException;
import com.funambol.foundation.items.manager.PIMContactManager;
import com.funambol.foundation.items.model.ContactWrapper;
import com.funambol.framework.tools.DataSourceTools;
import java.util.ArrayList;
import java.util.List;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Foundation connector specific <code>ContactProvider</code>.
 * @author $Id: FoundationContactProvider.java,v 1.1.1.1 2008-03-20 21:38:41 stefano_fornari Exp $
 */
public class FoundationContactProvider implements ContactProvider {

    // ------------------------------------------------------------ Private data

    /** Manager used to access the backend. */
    private PIMContactManager manager;

    // ------------------------------------------------------------ Constructors

    public FoundationContactProvider (PIMContactManager manager){
        this.manager = manager;
    }

    public FoundationContactProvider (String userId){
        manager = new PIMContactManager(userId);
    }

    // ---------------------------------------------------------- Public methods

    /**
     * @see ContactProvider
     *
     * @throws javax.mail.internet.AddressException if at least one of the addresses
     * is not well formed
     * @TODO is a good handling of this case simply throw the AddressException ?
     */
    public InternetAddress[] getAddressesForUser(String userId)
    throws EntityException, AddressException {

        if (userId == null){
            throw new IllegalArgumentException("userId: " + userId);
        }

        ArrayList<InternetAddress> addresses = new ArrayList<InternetAddress>();

        // gets all the contacts ids
        List<String> contactWrappersIds = manager.getAllItems();

        Contact contact = null;
        List<Email> personalAddresses = null;
        List<Email> businessAddresses = null;

        for (String contactWrapperId : contactWrappersIds) {

            // get the entire contact
            contact = manager.getItem(contactWrapperId).getContact();

            if (contact == null){
                continue;
            }

            // iterate among the contact's email addresses to create InternetAddress objects

            PersonalDetail personalDetails = contact.getPersonalDetail();
            if (personalDetails != null) {
                personalAddresses = personalDetails.getEmails();

                for (Email email : personalAddresses) {
                    addresses.add(new InternetAddress(email.getPropertyValueAsString()));
                }
            }

            BusinessDetail businessDetails = contact.getBusinessDetail();
            if (businessDetails != null) {
                businessAddresses = contact.getBusinessDetail().getEmails();

                for (Email email : businessAddresses) {
                    addresses.add(new InternetAddress(email.getPropertyValueAsString()));
                }
            }

        }

        return addresses.toArray(new InternetAddress[addresses.size()]);
    }

}








