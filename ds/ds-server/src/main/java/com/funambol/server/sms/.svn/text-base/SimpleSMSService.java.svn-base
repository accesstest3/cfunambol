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
import com.funambol.common.sms.core.SMSMessage;
import com.funambol.common.sms.core.SMSRecipient;
import com.funambol.common.sms.core.SMSSender;

import com.funambol.common.sms.core.TextualSMSMessage;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.sms.SMSProvider;
import com.funambol.framework.sms.SMSProviderException;
import com.funambol.framework.sms.SMSService;
import com.funambol.framework.sms.SMSServiceException;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.server.config.Configuration;

/**
 * Simple SMSService implementation that forwards the requests to a SMSProvider
 * obtained reading com/funambol/server/sms/SMSProvider.xml.
 * <br/>
 * Sending SMS, the checkForPendingMessage parameter is ignored so SMSs are always
 * sent.
 * @version $Id: SimpleSMSService.java,v 1.2 2008-07-03 12:48:00 nichele Exp $
 */
public class SimpleSMSService implements SMSService, LazyInitBean {

    //  -------------------------------------------------------------- Constants

    /** The default SMSProvider  */
    protected static final String DEFAULT_SMS_PROVIDER =
        "com/funambol/server/sms/SMSProvider.xml";

    // -------------------------------------------------------------- Properties

    /** The default sender to use */
    private String sender = "funambol"; // default value

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }
    
    /** The SMS Provider to use */
    private String smsProvider = DEFAULT_SMS_PROVIDER;

    public String getSmsProvider() {
        return smsProvider;
    }

    public void setSmsProvider(String smsProvider) {
        this.smsProvider = smsProvider;
    }


    // ------------------------------------------------------------ Private data
    protected static FunambolLogger logger =
            FunambolLoggerFactory.getLogger("funambol.server.sms-service");

    // ---------------------------------------------------------- Public methods

    public void init() throws BeanInitializationException {
        if (logger.isInfoEnabled()) {
            logger.info("Initializing SimpleSMSService with SMSProvider: " + smsProvider);
        }
        //
        // Trying to obtain a SMSProvider just to see if there are errors
        //

        SMSProvider smsProviderInstance = null;

        try {
            smsProviderInstance = getSMSProvider();
        } catch (Exception ex) {
            throw new BeanInitializationException("Error creating the SMSProvider", ex);
        }
    }

    /**
     * Sends a SMS
     * @param recipient the SMSRecipient
     * @param sender the SMSSender
     * @param message the message to send
     * @param checkForPendingMessage if true, the caller would want to send the sms
     * if there are not pending old SMS to deliver to the same recipient. This
     * implementation ignores this value
     * @return a DeliveryDetail instance
     * @throws SMSServiceException if an error occurs
     * @see com.funambol.common.sms.core.TextualSMSMessage
     * @see com.funambol.common.sms.core.BinarySMSMessage
     */
    public DeliveryDetail sendSMS(SMSRecipient recipient,
                                  SMSSender sender,
                                  TextualSMSMessage message,
                                  boolean checkForPendingMessage)
    throws SMSServiceException {

        if (sender == null) {
            sender = new SMSSender(this.sender);
        }
        SMSProvider smsProviderInstance = getSMSProvider();
        try {
            return smsProviderInstance.sendSMS(recipient, sender, message);
        } catch (SMSProviderException ex) {
            throw new SMSServiceException(ex);
        }
    }

    /**
     * Sends a binary SMS
     * @param recipient the SMSRecipient
     * @param sender the SMSSender
     * @param message the message to send
     * @param checkForPendingMessage if true, the caller would want to send the sms
     * if there are not pending old SMS to deliver to the same recipient. This
     * implementation ignores this value
     * @return a DeliveryDetail instance
     * @throws SMSServiceException if an error occurs
     * @see com.funambol.common.sms.core.TextualSMSMessage
     * @see com.funambol.common.sms.core.BinarySMSMessage
     */
    public DeliveryDetail sendSMS(SMSRecipient recipient,
                                  SMSSender sender,
                                  BinarySMSMessage message,
                                  boolean checkForPendingMessage)
    throws SMSServiceException {

        if (sender == null) {
            sender = new SMSSender(this.sender);
        }        
        SMSProvider smsProviderInstance = getSMSProvider();
        try {
            return smsProviderInstance.sendSMS(recipient, sender, message);
        } catch (SMSProviderException ex) {
            throw new SMSServiceException(ex);
        }
    }


    /**
     * Called to retrieve the status of a previously sent sms
     * @param deliveryDetail the details of the sent sms
     * @throws SMSServiceException if an error occurs
     * @return the status of the sent sms
     */
    public DeliveryStatus getDeliveryStatus(DeliveryDetail deliveryDetail)
    throws SMSServiceException {

        SMSProvider smsProviderInstance = getSMSProvider();
        try {
            return smsProviderInstance.getDeliveryStatus(deliveryDetail);
        } catch (SMSProviderException ex) {
            throw new SMSServiceException(ex);
        }
    }

    // --------------------------------------------------------- Private method

    private SMSProvider getSMSProvider() throws SMSServiceException  {
        
        SMSProvider smsProviderInstance = null;
        try {
            //
            // Using just one instance of SMSProvider using always the cached instance
            //
            smsProviderInstance =
                (SMSProvider) Configuration.getConfiguration().getBeanInstanceByName(DEFAULT_SMS_PROVIDER, true);
        } catch (BeanException ex) {
            logger.error("Error obtaining the SMSProvider", ex);
            throw new SMSServiceException("Error obtaining the SMSProvider", ex);
        }      
        return smsProviderInstance;
    }
}
