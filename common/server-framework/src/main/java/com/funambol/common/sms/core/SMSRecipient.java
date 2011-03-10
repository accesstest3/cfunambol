/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.common.sms.core;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * Represents a sms recipient
 *
 * @version $Id: SMSRecipient.java,v 1.3 2008-06-30 14:08:23 nichele Exp $
 */
public class SMSRecipient {

    // ------------------------------------------------------------ Private Data

    /** e.g. the phone number */
    private String recipient;

    /**
     * Returns the recipient
     * @return the recipient.
     */
    public String getRecipient() {
        return recipient;
    }

    /**
     * Set the recipient
     * @param recipient the recipient to set.
     */
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    /** The carrier of the recipient */
    private String carrier;

    /**
     * Returns the carrier
     * @return the carrier.
     */
    public String getCarrier() {
        return carrier;
    }

    /**
     * Set the carrier
     * @param carrier the carrier to set.
     */
    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    /** The country code of the recipient */
    private String countryA2Code;

    /**
     * Returns the countryA2Code
     * @return the countryA2Code.
     */
    public String getCountryA2Code() {
        return countryA2Code;
    }

    /**
     * Set the countryA2Code
     * @param countryA2Code the countryA2Code to set.
     */
    public void setCountryA2Code(String countryA2Code) {
        this.countryA2Code = countryA2Code;
    }

    // ------------------------------------------------------------ Constructors

    public SMSRecipient(String recipient) {
        this(recipient, null, null);
    }

    public SMSRecipient(String recipient, String carrier) {
        this(recipient, carrier, null);
    }

    public SMSRecipient(String recipient, String carrier, String countryA2Code) {
        this.recipient = recipient;
        this.carrier = carrier;
        this.countryA2Code = countryA2Code;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * Returns a string representation of this recipient
     * @return String
     */
    @Override
    public String toString() {
        ToStringBuilder sb = new ToStringBuilder(this);
        sb.append("recipient",     recipient);
        sb.append("carrier",       carrier);
        sb.append("countryA2Code", countryA2Code);
        return sb.toString();
    }

}
