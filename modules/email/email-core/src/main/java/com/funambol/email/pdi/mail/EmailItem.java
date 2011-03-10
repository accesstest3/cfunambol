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
package com.funambol.email.pdi.mail;



import com.funambol.common.pim.common.Property;


/**
 * An object representing a email.
 * This class can be used to store and retrive all
 * information about email.
 *
 * @version $Id: EmailItem.java,v 1.1 2008-03-25 11:28:19 gbmiglia Exp $
 */
public class EmailItem
        extends Property {

    /**
     * Creates an empty email item
     */
    public EmailItem() {
        super();
    }

    /**
     * Returns the header of this mail
     *
     * @return the header of this mail
     */
    public String getStringRFC2822() {
        return String.valueOf(this.getPropertyValue());
    }


    /**
     * <p>Returns a string representation of the object</p>
     * @return a string representation of the object
     */
    public String toString() {
        StringBuffer sb = new StringBuffer("");
        sb.append(this.getPropertyValue());
        return sb.toString();
    }


}
