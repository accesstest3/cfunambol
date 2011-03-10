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

package com.funambol.framework.sms;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.common.sms.core.DeliveryDetail;
import com.funambol.common.sms.core.DeliveryStatus;
import com.funambol.common.sms.core.SMSRecipient;
import com.funambol.common.sms.core.SMSSender;
import com.funambol.common.sms.core.TextualSMSMessage;

/**
 * Represents a class able to send SMS messages.
 * It seems pretty similar to the SMSService but the responsability of the SMSProvider
 * is just to send a SMS and to retrieve its status; the SMSService can add more
 * logic/functionalities like to use different SMSProviders basing on the recipient.
 * @version $Id: SMSProvider.java,v 1.1 2008-06-30 14:03:24 nichele Exp $
 */
public interface SMSProvider {

    /**
     * Sends a SMS
     * @param recipient the SMSRecipient
     * @param sender the SMSSender
     * @param message the message to send
     * @return a DeliveryDetail instance
     * @throws SMSProviderException if an error occurs
     */
    public DeliveryDetail sendSMS(SMSRecipient recipient,
                                  SMSSender sender,
                                  TextualSMSMessage message)
    throws SMSProviderException;


    /**
     * Sends a SMS
     * @param recipient the SMSRecipient
     * @param sender the SMSSender
     * @param message the message to send
     * @return a DeliveryDetail instance
     * @throws SMSProviderException if an error occurs
     */
    public DeliveryDetail sendSMS(SMSRecipient recipient,
                                  SMSSender sender,
                                  BinarySMSMessage message)
    throws SMSProviderException;


    /**
     * Called to retrivied the status of a previously sent sms
     * @param deliveryDetail the details of the sent sms
     * @throws SMSProviderException if an error occurs
     * @return the status of the sent sms
     */
    public DeliveryStatus getDeliveryStatus(DeliveryDetail deliveryDetail)
    throws SMSProviderException;
}
