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
package com.funambol.server.notification.sender.tcp.ctp;

import java.io.Serializable;

/**
 * Contains the result of a notification delivery.
 * <br>At the moment it contains the address of the CTP server that delivered
 * the notification message. NULL means not delivered, but if the notification
 * message is not delivered, no CTPNotificationResponse is sent by a CTP Server.
 * See CTPServer and CTPSender for details.
 * <br>
 * This class is sent by a CTPServer to a CTPSender via JGroups in a
 * serialized form.
 * <p>
 * <bold>IMPORTANT NOTE: since this class is serialized,
 * it is important to consider that any change in this class could
 * have impacts on the serialization and that could be handled carefully
 * </bold>
 * @version $Id: CTPNotificationResponse.java,v 1.1.1.1 2008-02-21 23:35:53 stefano_fornari Exp $
 */
public class CTPNotificationResponse implements Serializable {
    // --------------------------------------------------------------- Constants
    private static final long serialVersionUID = 4966690023219402739L;

    // -------------------------------------------------------------- Properties
    /**
     * Which CTP Server has delivered the notification ?
     * Null means not delivered
     */
    private String deliveredBy = null;

    public String getDeliveredBy() {
        return deliveredBy;
    }

    public void setDeliveredBy(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    // ------------------------------------------------------------- Constructor
    public CTPNotificationResponse(String deliveredBy) {
        this.deliveredBy = deliveredBy;
    }

    public CTPNotificationResponse() {
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append("[deliveredBy: ")
          .append((deliveredBy != null) ? deliveredBy : "NOT DELIVERED")
          .append(']');

        return sb.toString();
    }
}
