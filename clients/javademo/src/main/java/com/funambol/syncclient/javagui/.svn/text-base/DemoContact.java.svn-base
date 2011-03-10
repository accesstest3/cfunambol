/*
 * Funambol is a mobile platform developed by Funambol, Inc. 
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

import com.funambol.common.pim.contact.Contact;

/**
 * An object representing a contact.
 * Each contact is identified by its filename and the contact itself in the 
 * form of a Contact object.
 *
 * @see com.funambol.common.pim.contact.Contact
 *
 * @version $Id: DemoContact.java,v 1.2 2007-12-22 14:01:20 nichele Exp $
 */
public class DemoContact {

    //------------------------------------------------------------- Private data
    private Contact contact  = null;
    private String  filename = null;
    private int     type     = 0   ;

    //----------------------------------------------------------- Public methods

    /**
     * Creates a new contact without a specified type
     *
     * @param filename the filename for this contact
     * @param contact the Contact object for this contact
     */
    public DemoContact(String filename, Contact contact) {
        this.filename = filename;
        this.contact = contact;
        this.type = -1;
    }

    /**
     * Creates a new contact
     *
     * @param filename the filename for this contact
     * @param type the type for this contact
     * @param contact the Contact object for this contact
     */
    public DemoContact(String filename, int type, Contact contact) {
        this.filename = filename;
        this.contact = contact;
        this.type = type;
    }

    /**
     * Returns the filename for this contact
     *
     * @return the filename for this contact
     */
    public String getFilename() {
        return filename;
    }

    /**
     * Returns the type for this contact
     *
     * @return the type for this contact
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the Contact object for this contact
     *
     * @return the Contact object for this contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * Sets the type of this contact
     *
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Sets the Contact objecy of this contact
     *
     * @param contact the Contact object to set
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

}
