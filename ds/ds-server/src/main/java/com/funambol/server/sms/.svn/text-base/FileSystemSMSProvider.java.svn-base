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

import java.io.File;
import java.io.IOException;

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
import com.funambol.framework.tools.DbgTools;
import com.funambol.framework.tools.IOTools;

/**
 * Simple SMSProvider that just creates a file for any sent sms.
 * Any created file has this structure:
 * <ul>
 * <li>first row: the recipient + '\r\n'</li>
 * <li>second row: the sender + '\r\n'</li>
 * <li>third row: format: text/binary + '\r\n'</li>
 * <li>fourth row: empty row + '\r\n'</li>
 * <li>fifth  row: the message + '\r\n'. If the message is binary, it's represented as string
 * (example: {0x12, 0xEF, 0xA4} is saved as 12EFA4).
 * </li>
 * </ul>
 * <br/>The file name is created as <i>timestamp</i>_<i>recipient</i>.sms
 * <br/>If a file with that name already exists, it's replaced.
 *
 * <br/><br/>It's important to keep this format: this sender is usually used for
 * testing and changing the format, some junit tests could fail
 *
 * @version $Id: DummySMSProvider.java,v 1.1 2008-07-02 15:56:36 nichele Exp $
 */
public class FileSystemSMSProvider implements SMSProvider {

    private static String EOL = System.getProperty("line.separator");

    private FunambolLogger logger  = FunambolLoggerFactory.getLogger("funambol.server.sms.filesystem-sms-provider");

    // -------------------------------------------------------------- Properties

    /** The directory where to save sms */
    private String directory = null;

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }


    public DeliveryDetail sendSMS(SMSRecipient recipient, SMSSender sender, TextualSMSMessage message)
    throws SMSProviderException {

        if (logger.isTraceEnabled()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Sending textual message: ").append(message);
            sb.append(", to: ").append(recipient);
            sb.append(", from: ").append(sender);
            logger.trace(sb.toString());
        }
        
        long time = createSMSFile(recipient, sender, message);
        return new DeliveryDetail("0", time);
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
        
        long time = createSMSFile(recipient, sender, message);
        return new DeliveryDetail("0", time);
    }

    public DeliveryStatus getDeliveryStatus(DeliveryDetail deliveryDetail)
    throws SMSProviderException {
        if (logger.isTraceEnabled()) {
            logger.trace("Get delivery status for: " + deliveryDetail);
        }
        return new DeliveryStatus(DeliveryStatus.DELIVERED);
    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates the file with the sms content
     * @param recipient the recipient
     * @param sender the sender
     * @param message the message
     * @return the timestamp used in the file name
     */
    private long createSMSFile(SMSRecipient recipient, SMSSender sender, SMSMessage message) throws SMSProviderException {
        StringBuilder sb = new StringBuilder();
        sb.append(recipient.getRecipient()).append(EOL);
        sb.append(sender.getSender()).append(EOL);
        if (message instanceof TextualSMSMessage) {
            sb.append("text").append(EOL);
            sb.append(EOL);
            sb.append(((TextualSMSMessage)message).getText());
        } else if (message instanceof BinarySMSMessage) {
            sb.append("binary").append(EOL);
            sb.append(EOL);
            sb.append(DbgTools.bytesToHex(((BinarySMSMessage)message).getWdp()));
            sb.append(DbgTools.bytesToHex(((BinarySMSMessage)message).getWsp()));
            sb.append(DbgTools.bytesToHex(((BinarySMSMessage)message).getContent()));
        } else {
            sb.append("unknow").append(EOL);
            sb.append(EOL);
            sb.append(message);
        }

        checkDirectory();
        
        long time = System.currentTimeMillis();
        File destFile = new File(directory, time + "_" + recipient.getRecipient() + ".sms");
        if (logger.isTraceEnabled()) {
            logger.trace("Storing SMS in '" + destFile + "'");
        }
        try {
            IOTools.writeFile(sb.toString(), destFile);
        } catch (IOException ex) {
            logger.error("Error storing sms file", ex);
            throw new SMSProviderException("Unable to create sms file (" + destFile + ")", ex);
        }

        return time;
    }

    /**
     * Checks if the configured directory is null and creates it.
     */
    private void checkDirectory() {
        if (directory == null) {
            directory = ".";
        }
        File f = new File(directory);
        f.mkdirs();

    }
}
