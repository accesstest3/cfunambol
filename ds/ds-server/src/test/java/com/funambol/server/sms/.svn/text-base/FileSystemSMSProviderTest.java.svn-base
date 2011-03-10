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
import java.io.File;
import java.io.FileFilter;
import org.apache.commons.io.FileUtils;
import junit.framework.TestCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.io.filefilter.WildcardFilter;

/**
 * FileSystemSMSProvider's test case
 * @version $Id$
 */
public class FileSystemSMSProviderTest extends TestCase {

    private static String EOL = System.getProperty("line.separator");
  
    private static final String DIRECTORY = "target/test/data/runtime/sms";
    
    private FileSystemSMSProvider smsProvider = null;
    
    public FileSystemSMSProviderTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        smsProvider = new FileSystemSMSProvider();
    }

    @Override
    protected void tearDown() throws Exception {

        File f = new File(DIRECTORY);
        FileUtils.deleteDirectory(f);

        FileFilter smsFilter = new WildcardFileFilter("*.sms");
        File root = new File(".");
        File[] files = root.listFiles(smsFilter);
        for (File file : files) {
            FileUtils.deleteQuietly(file);
        }
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    /**
     * Test of getDirectory method, of class FileSystemSMSProvider.
     */
    public void testGetDirectory() {
        smsProvider.setDirectory(DIRECTORY);
        assertEquals("Wrong directory", DIRECTORY, smsProvider.getDirectory());
    }

    /**
     * Test of setDirectory method, of class FileSystemSMSProvider.
     */
    public void testSetDirectory() {
        smsProvider.setDirectory("test-directory");
        assertEquals("Wrong directory", "test-directory", smsProvider.getDirectory());
    }

    /**
     * Test of sendSMS method, of class FileSystemSMSProvider.
     */
    public void testSendSMS_textual() throws Exception {
        smsProvider.setDirectory(DIRECTORY);

        TextualSMSMessage smsMessage = new TextualSMSMessage("simple sms message");
        SMSRecipient recipient = new SMSRecipient("rcpttt");
        SMSSender sender = new SMSSender("sndtt");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", dd.getDeliveryId(), "0");

        long time = dd.getDeliveryTime();

        String fileContent = FileUtils.readFileToString(new File(DIRECTORY, time + "_" + recipient.getRecipient() + ".sms"));

        String expectedContent = "rcpttt" + EOL +
                                 "sndtt" + EOL +
                                 "text" + EOL +
                                 EOL +
                                 "simple sms message";

        assertEquals("Wrong content file", expectedContent, fileContent);
    }

    /**
     * Test of sendSMS method, of class FileSystemSMSProvider.
     */
    public void testSendSMS_textual_Null_dir() throws Exception {
        
        TextualSMSMessage smsMessage = new TextualSMSMessage("simple sms message null dir");
        SMSRecipient recipient = new SMSRecipient("rcpttt2");
        SMSSender sender = new SMSSender("sndtt2");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", dd.getDeliveryId(), "0");

        long time = dd.getDeliveryTime();

        String fileContent = FileUtils.readFileToString(new File(".", time + "_" + recipient.getRecipient() + ".sms"));

        String expectedContent = "rcpttt2" + EOL +
                                 "sndtt2" + EOL +
                                 "text" + EOL +
                                 EOL +
                                 "simple sms message null dir";

        assertEquals("Wrong content file", expectedContent, fileContent);
    }

    /**
     * Test of sendSMS method, of class FileSystemSMSProvider.
     */
    public void testSendSMS_binary() throws Exception {

        smsProvider.setDirectory(DIRECTORY);
        
        byte[] wdp = new byte[] {(byte)0xA3, (byte)0x06};
        byte[] wsp = new byte[] {(byte)0x1A, (byte)0x16, (byte)0xF4, (byte)0xAA};
        byte[] content = new byte[] {(byte)0x0A, (byte)0x12, (byte)0x14, (byte)0xBB, (byte)0x56, (byte)0xF3, (byte)0xFF};
        
        BinarySMSMessage smsMessage = new BinarySMSMessage(wdp, wsp, content);

        SMSRecipient recipient = new SMSRecipient("rcpt");
        SMSSender sender = new SMSSender("snd");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", dd.getDeliveryId(), "0");

        long time = dd.getDeliveryTime();

        String fileContent = FileUtils.readFileToString(new File(DIRECTORY, time + "_" + recipient.getRecipient() + ".sms"));

        String expectedContent = "rcpt" + EOL +
                                 "snd" + EOL +
                                 "binary" + EOL +
                                 EOL +
                                 "A3061A16F4AA0A1214BB56F3FF";

        assertEquals("Wrong content file", expectedContent, fileContent);
    }

    /**
     * Test of getDeliveryStatus method, of class FileSystemSMSProvider.
     */
    public void testGetDeliveryStatus() throws Exception {
        DeliveryStatus ds = smsProvider.getDeliveryStatus(new DeliveryDetail("1234"));

        assertEquals("Wrong delivery status", DeliveryStatus.DELIVERED, ds.getStatus());
    }

}
