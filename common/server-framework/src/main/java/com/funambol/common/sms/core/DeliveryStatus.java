/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

/**
 * Represents the status of a delivery
 * @version $Id: DeliveryStatus.java,v 1.1 2008-06-30 14:22:16 nichele Exp $
 */
public class DeliveryStatus {

    // --------------------------------------------------------------- Constants

    /** Available status */
    public static final char UNDEFINED = 'U';
    public static final char NOT_SENT  = 'N';
    public static final char SENT      = 'S';
    public static final char DELIVERED = 'D';
    public static final char REFUSED   = 'R';
    public static final char EXPIRED   = 'E';

    // ------------------------------------------------------------ Private data
    private char status = UNDEFINED;

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of DeliveyStatus
     * @param status the status
     */
    public DeliveryStatus(char status) {
        setStatus(status);
    }
    // ---------------------------------------------------------- Public methods

    /**
     * Returns the status
     * @return the status
     */
    public char getStatus() {
        return status;
    }

    /**
     * Sets the status
     * @param status the status to set
     */
    public void setStatus(char status) {
        if (status == UNDEFINED ||
            status == NOT_SENT  ||
            status == SENT      ||
            status == DELIVERED ||
            status == REFUSED ||
            status == EXPIRED    ) {

            this.status = status;
        } else {
            this.status = UNDEFINED;
        }
    }
    /**
     * Returns a String representation of this SendingStatus
     *
     * @return a String representation of this SendingStatus
     */
    @Override
    public String toString() {
        switch (this.status) {
            case UNDEFINED:
                return "undefined";
            case NOT_SENT:
                return "not sent";
            case SENT:
                return "sent";
            case DELIVERED:
                return "delivered";
            case REFUSED:
                return "refused";
            case EXPIRED:
                return "expired";
            default:
                return "undefined";
        }
    }

    @Override
    public boolean equals(Object otherStatus) {
        if (!(otherStatus instanceof DeliveryStatus)) {
            return false;
        }
        return status == ((DeliveryStatus)otherStatus).getStatus();
    }

    @Override
    public int hashCode() {
        return status;
    }

}
