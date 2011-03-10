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

package com.funambol.pimlistener.registry;

import org.apache.commons.lang.builder.ToStringBuilder;

import com.funambol.pushlistener.service.registry.RegistryEntry;

/**
 * A pim registry entry. It is represented by the data in the pim listener registry
 * table joined with the push listener registry
 * @version $Id: PimRegistryEntry.java,v 1.9 2008-06-14 09:41:35 nichele Exp $
 */
public class PimRegistryEntry extends RegistryEntry {

    // -------------------------------------------------------------- Properties

   /** The user's username to which this entry is associated. */
    private String userName;

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    /** Must the contacts be pushed ? */
    private boolean pushContacts;

    public boolean isPushContacts() {
        return pushContacts;
    }

    public void setPushContacts(boolean pushContacts) {
        this.pushContacts = pushContacts;
    }

    /** Must the calendars be pushed ? */
    private boolean pushCalendars;

    public boolean isPushCalendars() {
        return pushCalendars;
    }

    public void setPushCalendars(boolean pushCalendars) {
        this.pushCalendars = pushCalendars;
    }

    /** Must the notes be pushed ? */
    private boolean pushNotes;

    public boolean isPushNotes() {
        return pushNotes;
    }

    public void setPushNotes(boolean pushNotes) {
        this.pushNotes = pushNotes;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of PimRegistryEntry */
    public PimRegistryEntry() {
    }

    /**
     * Creates a new instance of PimRegistryEntry with the specified id value.
     * @param id the id of the PimRegistryEntry
     */
    public PimRegistryEntry(long id) {
        super(id);
    }

    /**
     * Returns a string representation of the object.
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("id"            , getId());
        sb.append("period"        , getPeriod());
        sb.append("active"        , getActive());
        sb.append("taskBeanFile"  , getTaskBeanFile());
        sb.append("username"      , getUserName());
        sb.append("pushContacts"  , isPushContacts());
        sb.append("pushCalendars" , isPushCalendars());
        sb.append("pushNotes"     , isPushNotes());
        sb.append("lastUpdate"    , getLastUpdate());
        sb.append("status"        , getStatus());
        return sb.toString();
    }

    /**
     * Returns a string representation of the object useful to show the entry
     * in the console.
     * @return a string representation of the object useful to show the entry
     * in the console.
     */
    public String toStringForCommandLine() {
        StringBuilder sb = new StringBuilder();

        sb.append("id: ").append(getId())
          .append(" [")
          .append("userName: ").append(getUserName()).append(", ")
          .append("active: ").append(getActive()).append(", ")
          .append("status: ").append(getStatus()).append(", ")
          .append("refresh time: ").append(getPeriod() / 60000).append(" min., ")
          .append("task bean file: ").append(getTaskBeanFile()).append(", ")
          .append("push contacts: ").append(isPushContacts()).append(", ")
          .append("push calendars: ").append(isPushCalendars()).append(", ")
          .append("push notes: ").append(isPushNotes())
          .append("]");

        return sb.toString();
    }

}
