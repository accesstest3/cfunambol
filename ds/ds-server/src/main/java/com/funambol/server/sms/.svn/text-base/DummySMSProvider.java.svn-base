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
package com.funambol.server.sms;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.common.sms.core.DeliveryDetail;
import com.funambol.common.sms.core.DeliveryStatus;
import com.funambol.common.sms.core.SMSRecipient;
import com.funambol.common.sms.core.SMSSender;

import com.funambol.common.sms.core.TextualSMSMessage;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.sms.SMSProvider;
import com.funambol.framework.sms.SMSProviderException;

/**
 * Dummy SMSProvider that just logs the requests at TRACE level
 * @version $Id: DummySMSProvider.java,v 1.1 2008-07-02 15:56:36 nichele Exp $
 */
public class DummySMSProvider implements SMSProvider {

    private FunambolLogger logger  = FunambolLoggerFactory.getLogger("funambol.server.sms.dummy-sms-provider");

    public DeliveryDetail sendSMS(SMSRecipient recipient, SMSSender sender, TextualSMSMessage message)
    throws SMSProviderException {
        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sending textual message: ").append(message);
            sb.append(", to: ").append(recipient);
            sb.append(", from: ").append(sender);
            logger.trace(sb.toString());
        }
        return new DeliveryDetail("0");
    }

    public DeliveryDetail sendSMS(SMSRecipient recipient, SMSSender sender, BinarySMSMessage message)
    throws SMSProviderException {
        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sending binary message: ").append(message);
            sb.append(", to: ").append(recipient);
            sb.append(", from: ").append(sender);
            logger.trace(sb.toString());
        }
        return new DeliveryDetail("0");
    }

    public DeliveryStatus getDeliveryStatus(DeliveryDetail deliveryDetail)
    throws SMSProviderException {
        if (logger.isTraceEnabled()) {
            logger.trace("Get delivery status for: " + deliveryDetail);
        }
        return new DeliveryStatus(DeliveryStatus.DELIVERED);
    }

}
