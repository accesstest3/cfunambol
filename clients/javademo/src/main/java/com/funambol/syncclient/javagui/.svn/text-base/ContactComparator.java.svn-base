/*
 * Copyright (C) 2003-2007 Funambol, Inc.
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
package com.funambol.syncclient.javagui;

import java.util.Comparator;

/**
 * Contact comparator.
 * <br/>
 * Compares contacts using displayName
 * <br/>
 *
 * @version $Id: ContactComparator.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class ContactComparator implements Comparator {

    // ------------------------------------------------------------- Constructor
    /**
     * Creates a new ContactComparator.
     */
    public ContactComparator() {}

    // ---------------------------------------------------------- Public methods

    /**
     * Compares its two arguments for order.
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     */
    public int compare(Object o1, Object o2) {

        String value1 = null;
        String value2 = null;

        value1 = ((DemoContact)o1).getContact().getName()
                                  .getDisplayName().getPropertyValueAsString();

        value2 = ((DemoContact)o2).getContact().getName()
                                  .getDisplayName().getPropertyValueAsString();


        if (value1 == null) {

            value1 = ((DemoContact)o1).getContact().getName()
                                .getLastName().getPropertyValueAsString();

            if (value1 != null && !value1.equals("")) {
                value1 += ",";
            }

            value1 += ((DemoContact)o1).getContact().getName()
                                .getFirstName().getPropertyValueAsString();

            if (value1 == null) {
                value1 = "";
            }

        }

        if (value2 == null) {

            value2 =((DemoContact) o2).getContact().getName()
                                .getLastName().getPropertyValueAsString();

            if (value2 != null && !value2.equals("")) {
                value2 += ",";
            }

            value2 += ((DemoContact) o2).getContact().getName()
                                .getFirstName().getPropertyValueAsString();

            if (value2 == null) {
                value2 = "";
            }
        }
        return (value1.toUpperCase()).compareTo(value2.toUpperCase());
    }

}
