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
package com.funambol.common.pim.contact;

import com.funambol.framework.tools.merge.MergeResult;

import com.funambol.common.pim.common.*;
import com.funambol.common.pim.utility.PDIMergeUtils;

/**
 * An object representing a name
 *
 * @version $Id: Name.java,v 1.2 2007-11-28 11:14:04 nichele Exp $
 */
public class Name {

    private Property salutation;
    private Property firstName;
    private Property middleName;
    private Property lastName;
    private Property suffix;
    private Property displayName;
    private Property nickname;
    private Property initials;

    /**
     * Creates an empty name
     */
    public Name () {
        this.setSalutation(new Property());
        this.setFirstName(new Property());
        this.setMiddleName(new Property());
        this.setLastName(new Property());
        this.setSuffix(new Property());
        this.setDisplayName(new Property());
        this.setNickname(new Property());
        this.setInitials(new Property());
    }

    /**
     * Returns the salutation for this name
     *
     * @return the salutation for this name
     */
    public Property getSalutation () {
        return salutation;
    }

    /**
     * Returns the first name for this name
     *
     * @return the first name for this name
     */
    public Property getFirstName () {
        return firstName;
    }

    /**
     * Returns the middle name for this name
     *
     * @return the middle name for this name
     */
    public Property getMiddleName () {
        return middleName;
    }

    /**
     * Returns the last name for this name
     *
     * @return the last name for this name
     */
    public Property getLastName () {
        return lastName;
    }

    /**
     * Returns the suffix for this name
     *
     * @return the suffix for this name
     */
    public Property getSuffix () {
        return suffix;
    }

    /**
     * Returns the display name for this name
     *
     * @return the display name for this name
     */
    public Property getDisplayName () {
        return displayName;
    }

    /**
     * Returns the nickname for this name
     *
     * @return the nickname for this name
     */
    public Property getNickname () {
        return nickname;
    }

    /**
     * Getter for property initials.
     * @return Value of property initials.
     */
    public Property getInitials() {
        return initials;
    }

    /**
     * Setter for property initials.
     * @param initials New value of property initials.
     */
    public void setInitials(Property initials) {
        this.initials = initials;
    }

    /**
     * Merges this name with the given name
     * @param name Name
     * @return true if the original Name is changed
     */
    public MergeResult merge(Name otherName) {

        MergeResult nameMergeResult = new MergeResult("Name");

        if (otherName == null) {
            throw new IllegalStateException("The given Name must be not null");
        }

        //
        // MergeResult used for each fields
        //
        MergeResult result = null;

        // salutation
        result = PDIMergeUtils.compareProperties(this.getSalutation(), otherName.getSalutation());
        if (result.isSetARequired()) {
            this.setSalutation(otherName.getSalutation());
        } else if (result.isSetBRequired()) {
            otherName.setSalutation(this.getSalutation());
        }
        nameMergeResult.addMergeResult(result, "Salutation");

        // first name
        result = PDIMergeUtils.compareProperties(this.getFirstName(), otherName.getFirstName());
        if (result.isSetARequired()) {
            this.setFirstName(otherName.getFirstName());
        } else if (result.isSetBRequired()) {
            otherName.setFirstName(this.getFirstName());
        }
        nameMergeResult.addMergeResult(result, "FirstName");


        // middle name
        result = PDIMergeUtils.compareProperties(this.getMiddleName(), otherName.getMiddleName());
        if (result.isSetARequired()) {
            this.setMiddleName(otherName.getMiddleName());
        } else if (result.isSetBRequired()) {
            otherName.setMiddleName(this.getMiddleName());
        }
        nameMergeResult.addMergeResult(result, "MiddleName");


        // last name
        result = PDIMergeUtils.compareProperties(this.getLastName(), otherName.getLastName());
        if (result.isSetARequired()) {
            this.setLastName(otherName.getLastName());
        } else if (result.isSetBRequired()) {
            otherName.setLastName(this.getLastName());
        }
        nameMergeResult.addMergeResult(result, "LastName");


        // suffix
        result = PDIMergeUtils.compareProperties(getSuffix(), otherName.getSuffix());
        if (result.isSetARequired()) {
            this.setSuffix(otherName.getSuffix());
        } else if (result.isSetBRequired()) {
            otherName.setSuffix(this.getSuffix());
        }
        nameMergeResult.addMergeResult(result, "Suffix");


        // nickname
        result = PDIMergeUtils.compareProperties(getNickname(), otherName.getNickname());
        if (result.isSetARequired()) {
            this.setNickname(otherName.getNickname());
        } else if (result.isSetBRequired()) {
            otherName.setNickname(this.getNickname());
        }
        nameMergeResult.addMergeResult(result, "NickName");


        // display name
        result = PDIMergeUtils.compareProperties(this.getDisplayName(), otherName.getDisplayName());
        if (result.isSetARequired()) {
            this.setDisplayName(otherName.getDisplayName());
        } else if (result.isSetBRequired()) {
            otherName.setDisplayName(this.getDisplayName());
        }
        nameMergeResult.addMergeResult(result, "DisplayName");


        // initials
        result = PDIMergeUtils.compareProperties(this.getInitials(), otherName.getInitials());
        if (result.isSetARequired()) {
            this.setInitials(otherName.getInitials());
        } else if (result.isSetBRequired()) {
            otherName.setInitials(this.getInitials());
        }
        nameMergeResult.addMergeResult(result, "Initials");


        return nameMergeResult;
    }

    public void setSalutation(Property salutation) {
        this.salutation = salutation;
    }

    public void setFirstName(Property firstName) {
        this.firstName = firstName;
    }

    public void setMiddleName(Property middleName) {
        this.middleName = middleName;
    }

    public void setLastName(Property lastName) {
        this.lastName = lastName;
    }

    public void setSuffix(Property suffix) {
        this.suffix = suffix;
    }

    public void setDisplayName(Property displayName) {
        this.displayName = displayName;
    }

    public void setNickname(Property nickname) {
        this.nickname = nickname;
    }

}
