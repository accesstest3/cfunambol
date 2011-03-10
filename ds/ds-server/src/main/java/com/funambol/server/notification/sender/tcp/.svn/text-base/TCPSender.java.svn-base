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

package com.funambol.server.notification.sender.tcp;

import java.util.ArrayList;
import java.util.List;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.notification.Message;
import com.funambol.framework.notification.NotificationException;
import com.funambol.framework.notification.NotificationNotSentException;
import com.funambol.framework.notification.sender.Sender;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;

/**
 * This is a sender that uses a STPSender and a CTPSender to notify a device.
 * <p>
 * It tries before to notify a device via STP and, if the device is not reachable
 * or if it doesn't have an address associated, it tries with CTP.
 * The STP notification can be disable setting <code>enableSTP</code> to false.
 * <br/>
 * The CTP notification can be disable setting <code>enableCTP</code> to false.
 *
 *
 * @version $Id: TCPSender.java,v 1.2 2008-03-04 22:13:00 stefano_fornari Exp $
 */
public class TCPSender implements Sender, LazyInitBean {

    // --------------------------------------------------------------- Constants
    /**
     * Logger name
     */
    private static final String LOGGER_NAME = "funambol.server.notification.tcp-sender";

    // ------------------------------------------------------------ Private data

    /**
     * The used logger
     */
    private FunambolLogger logger = FunambolLoggerFactory.getLogger(LOGGER_NAME);

    // -------------------------------------------------------------- Properties

    /**
     * Is STP notification enabled ?
     */
    private boolean enableSTP = true;

    public boolean isEnableSTP() {
        return enableSTP;
    }

    public void setEnableSTP(boolean enableSTP) {
        this.enableSTP = enableSTP;
    }

    /**
     * Is CTP notification enabled ?
     */
    private boolean enableCTP = true;

    public boolean isEnableCTP() {
        return enableCTP;
    }

    public void setEnableCTP(boolean enableCTP) {
        this.enableCTP = enableCTP;
    }

    /**
     * The sender to use to send STP notification
     */
    private Sender stpSender = null;

    public Sender getStpSender() {
        return stpSender;
    }

    public void setStpSender(Sender sender) {
        this.stpSender = sender;
    }

    /**
     * The sender to use to send CTP notification
     */
    private Sender ctpSender = null;

    public Sender getCtpSender() {
        return ctpSender;
    }

    public void setCtpSender(Sender sender) {
        this.ctpSender = sender;
    }

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of TCPSender */
    public TCPSender() {
    }

    // ---------------------------------------------------------- Public methods

    /**
     * See LazyInitBean
     * @throws BeanInitializationException
     */
    public void init() throws BeanInitializationException {
       if (stpSender != null && (stpSender instanceof LazyInitBean)) {
           ((LazyInitBean)stpSender).init();
       }
       if (ctpSender != null && (ctpSender instanceof LazyInitBean)) {
           ((LazyInitBean)ctpSender).init();
       }
    }

    /**
     * Sends the given notification message to the given device.
     * If the tcp/ip notification is enabled it tries to use tcp/ip.
     * If that is disabled or if there is issues in the notication via tcp/ip,
     * the notification message will be sent via sms (if the sms notification is
     * enabled)
     * @param sync4jDevice the device to notify
     * @param message the notification message
     * @throws com.funambol.framework.notification.NotificationException
     */
    public void sendNotificationMessage(Sync4jDevice sync4jDevice, Message message)
    throws NotificationException  {

        if (sync4jDevice == null) {
            throw new NotificationNotSentException("[TCP: Rejected notification to a null device]");
        }

        if (!isEnableCTP() && !isEnableSTP()) {
            throw new NotificationNotSentException("[TCP: CTP and STP notification are disabled]");
        }

        //
        // We collect the error messages in order to throw (at the end) an exception
        // with all information
        //
        List<String> errorMessages = new ArrayList<String>();

        if (isEnableSTP()) {
            try {
                handleSTPNotification(sync4jDevice, message);
                if (logger.isTraceEnabled()) {
                    logger.trace("Notification message sent via STP");
                }
                return;

            } catch (NotificationException ex) {
                errorMessages.add("[STP: " + ex.getMessage() + "]");
            }
        } else {
            errorMessages.add("[STP: disabled]");            
        }

        if (isEnableCTP()) {
            try {
                handleCTPNotification(sync4jDevice, message);
                if (logger.isTraceEnabled()) {
                    logger.trace("Notification message sent via CTP");
                }
                return;

            } catch (NotificationNotSentException ex) {
                String msg = ex.getMessage();
                if (msg == null || msg.length() == 0) {
                    //
                    // In the case the CTPSender doesn't provide a message
                    //
                    msg = "Notification message not delivered by any CTP Server";
            }
                errorMessages.add("[CTP: " + msg + "]");
            } catch (NotificationException ex) {
                errorMessages.add("[CTP: " + ex.getMessage() + "]");
            }
        } else {
            errorMessages.add("[CTP: disabled]");            
        }

        if (logger.isTraceEnabled()) {
            logger.trace("Notification message not sent via TCP");
        }

        StringBuilder errorMessage = new StringBuilder();
        int cont = 0;
        for (String error : errorMessages) {
            if (cont++ != 0) {
                errorMessage.append(" ");
            }

            errorMessage.append(error);
        }

        //
        // This exception is used to notify the caller that the notification message
        // has not been sent
        //
        throw new NotificationNotSentException(errorMessage.toString());
    }


    // --------------------------------------------------------- Private methods

    /**
     * Tries to send the notification message via STP.
     * <br/>
     * The STP sender must be not null and the device must have an address.
     * <br/>
     * If there are errors in tcp/ip notification the device address will be deleted
     * (probably it is not reachable)
     *
     * @param device the device to notify
     * @param message the notification message
     * @throws NotificationException if the notification message has not been sent
     */
    private void handleSTPNotification(Sync4jDevice device, Message message)
    throws NotificationException {

        if (stpSender != null) {
            String address = device.getAddress();
            if (address != null && address.length() != 0) {

                    stpSender.sendNotificationMessage(device, message);
                return ;

            } else {
                throw new NotificationNotSentException("Address not configured for device '"
                                                       + device.getDeviceId() +
                                "'. Unable to send notification via STP");
                }
        } else {
            if (logger.isWarningEnabled()) {
                logger.warn("STPSender is null");
            }
            throw new NotificationNotSentException("STPSender is null");
        }
    }

    /**
     * Tries to send the notification message via sms.
     * <br/>
     * The sms sender must be not null and the device must have a msisdn.
     * <br/>
     *
     * @param device the device to notify
     * @param message the notification message
     * @throws NotificationException if an error occurs or if the message has not
     *         been delivered
     */
    private void handleCTPNotification(Sync4jDevice device, Message message)
    throws NotificationException {
        if (ctpSender != null) {
                ctpSender.sendNotificationMessage(device, message);
            return;
        }
        throw new NotificationException("CTPSender is null");
    }

}
