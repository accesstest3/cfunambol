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
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JndiDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;

import com.funambol.common.sms.core.BinarySMSMessage;
import com.funambol.common.sms.core.DeliveryDetail;
import com.funambol.common.sms.core.DeliveryStatus;
import com.funambol.common.sms.core.SMSRecipient;
import com.funambol.common.sms.core.SMSSender;
import com.funambol.common.sms.core.TextualSMSMessage;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.server.db.DataSourceContextHelper;

/**
 * DBSMSProvider's test cases
 * @version $Id$
 */
public class DBSMSProviderTest extends DBTestCase {

    private static final String INITIAL_DATASET = "src/test/data/com/funambol/server/sms/DBSMSProvider/initial-db-dataset.xml";

    private static final String DATASET_TEXTUAL_1 = "src/test/data/com/funambol/server/sms/DBSMSProvider/dataset-textual-1.xml";
    private static final String DATASET_BINARY_1  = "src/test/data/com/funambol/server/sms/DBSMSProvider/dataset-binary-1.xml";

    private static final String DATASET_TEXTUAL_2 = "src/test/data/com/funambol/server/sms/DBSMSProvider/dataset-textual-2.xml";
    private static final String DATASET_BINARY_2  = "src/test/data/com/funambol/server/sms/DBSMSProvider/dataset-binary-2.xml";

    private static DataSource dsCore = null;

    private static String DROP_FNBL_SMS = "drop table FNBL_SMS";

    public static final String CREATE_FNBL_SMS =
        "CREATE TABLE FNBL_SMS (" +
        "   id              bigint NOT NULL primary key," +
        "   time            bigint NOT NULL," +
        "   sender          varchar," +
        "   recipient       varchar," +
        "   format          varchar," +
        "   udh             varchar," +
        "   body            varchar"  +
        ")";

    private static String DROP_FNBL_OTHER_SMS = "drop table FNBL_OTHER_SMS";

    public static final String CREATE_FNBL_OTHER_SMS =
        "CREATE TABLE FNBL_OTHER_SMS (" +
        "   id              bigint NOT NULL primary key," +
        "   time            bigint NOT NULL," +
        "   sender          varchar," +
        "   recipient       varchar," +
        "   format          varchar," +
        "   udh             varchar," +
        "   body            varchar"  +
        ")";

    private static String DROP_FNBL_ID = "drop table FNBL_ID";

    public static final String CREATE_FNBL_ID =
        "CREATE TABLE FNBL_ID (" +
        "   idspace          varchar NOT NULL primary key," +
        "   counter          bigint NOT NULL," +
        "   increment_by     integer" +
        ")";


    protected DBSMSProvider smsProvider = null;

    static {
        try {
            System.setProperty("funambol.home", "src/test/data");
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            dsCore = DataSourceTools.lookupDataSource("jdbc/fnblcore");

            createDB(dsCore);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------- Constructor
    public DBSMSProviderTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods


    @Override
    protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSet(new File(INITIAL_DATASET));
    }

    @Override
    protected IDatabaseTester getDatabaseTester() throws Exception {
        return new JndiDatabaseTester("jdbc/fnblcore");
    }
 
    /**
     * Test of setJndiDataSourceName method, of class DBSMSProvider.
     */
    public void test_SetGetJndiDataSourceName() throws BeanInitializationException {
        smsProvider = new DBSMSProvider();
        smsProvider.init();

        smsProvider.setJndiDataSourceName("testDS");
        assertEquals("testDS", smsProvider.getJndiDataSourceName());
    }

    /**
     * Test of sendSMS method, of class DBSMSProvider.
     */
    public void testSendSMS_textualMessage() throws Exception {
        smsProvider = new DBSMSProvider();
        smsProvider.init();

        TextualSMSMessage smsMessage = new TextualSMSMessage("simple sms message");
        SMSRecipient recipient = new SMSRecipient("rcpttt");
        SMSSender sender = new SMSSender("sndtt");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "0", dd.getDeliveryId());

        smsMessage = new TextualSMSMessage("another sms message");
        recipient = new SMSRecipient("12345");
        sender = new SMSSender("67890");

        dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "1", dd.getDeliveryId());

        // Fetch database data
        IDataSet currentDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(DATASET_TEXTUAL_1));

        compareDB(expectedDataSet, currentDataSet, "fnbl_sms", new String[] {"id", "time"});
    }

    /**
     * Test of sendSMS method, of class DBSMSProvider.
     */
    public void testSendSMS_binaryMessage() throws Exception {

        smsProvider = new DBSMSProvider();
        smsProvider.init();

        byte[] wdp = new byte[] {(byte)0xA3, (byte)0x06};
        byte[] wsp = new byte[] {(byte)0x1A, (byte)0x16, (byte)0xF4, (byte)0xAA};
        byte[] content = new byte[] {(byte)0x0A, (byte)0x12, (byte)0x14, (byte)0xBB, (byte)0x56, (byte)0xF3, (byte)0xFF};

        BinarySMSMessage smsMessage = new BinarySMSMessage(wdp, wsp, content);

        SMSRecipient recipient = new SMSRecipient("rcpt");
        SMSSender sender = new SMSSender("snd");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "0", dd.getDeliveryId());

        // Fetch database data
        IDataSet currentDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(DATASET_BINARY_1));

        compareDB(expectedDataSet, currentDataSet, "fnbl_sms", new String[] {"time"});
    }

    /**
     * Test of sendSMS method, of class DBSMSProvider.
     */
    public void testSendSMS_textualMessage_onDifferentTable() throws Exception {
        smsProvider = new DBSMSProvider();
        smsProvider.setTableName("fnbl_other_sms");
        smsProvider.init();

        TextualSMSMessage smsMessage = new TextualSMSMessage("simple sms message");
        SMSRecipient recipient = new SMSRecipient("rcpttt");
        SMSSender sender = new SMSSender("sndtt");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "0", dd.getDeliveryId());

        smsMessage = new TextualSMSMessage("another sms message");
        recipient = new SMSRecipient("12345");
        sender = new SMSSender("67890");

        dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "1", dd.getDeliveryId());

        // Fetch database data
        IDataSet currentDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(DATASET_TEXTUAL_2));

        compareDB(expectedDataSet, currentDataSet, "fnbl_sms", new String[] {"id", "time"});
    }

    /**
     * Test of sendSMS method, of class DBSMSProvider.
     */
    public void testSendSMS_binaryMessage_onDifferentTable() throws Exception {

        smsProvider = new DBSMSProvider();
        smsProvider.setTableName("fnbl_other_sms");
        smsProvider.init();

        byte[] wdp = new byte[] {(byte)0xA3, (byte)0x06};
        byte[] wsp = new byte[] {(byte)0x1A, (byte)0x16, (byte)0xF4, (byte)0xAA};
        byte[] content = new byte[] {(byte)0x0A, (byte)0x12, (byte)0x14, (byte)0xBB, (byte)0x56, (byte)0xF3, (byte)0xFF};

        BinarySMSMessage smsMessage = new BinarySMSMessage(wdp, wsp, content);

        SMSRecipient recipient = new SMSRecipient("rcpt");
        SMSSender sender = new SMSSender("snd");

        DeliveryDetail dd = smsProvider.sendSMS(recipient, sender, smsMessage);
        assertEquals("Wrong delivery details", "0", dd.getDeliveryId());

        // Fetch database data
        IDataSet currentDataSet = getConnection().createDataSet();
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(DATASET_BINARY_2));

        compareDB(expectedDataSet, currentDataSet, "fnbl_sms", new String[] {"time"});
    }


    /**
     * Test of getDeliveryStatus method, of class DBSMSProvider.
     */
    public void testGetDeliveryStatus() throws Exception {
        smsProvider = new DBSMSProvider();
        smsProvider.setTableName("fnbl_other_sms");
        smsProvider.init();
        
        DeliveryStatus ds = smsProvider.getDeliveryStatus(new DeliveryDetail("1234"));

        assertEquals("Wrong delivery status", DeliveryStatus.DELIVERED, ds.getStatus());
    }

    // --------------------------------------------------------- Private methods
    // --------------------------------------------------------- Private methods

    private static void createDB(DataSource ds) throws NamingException {

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = ds.getConnection();
            
            stmt = conn.prepareStatement(DROP_FNBL_SMS);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(CREATE_FNBL_SMS);
            stmt.execute();

            DBTools.close(null, stmt, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(DROP_FNBL_OTHER_SMS);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(CREATE_FNBL_OTHER_SMS);
            stmt.execute();

            DBTools.close(null, stmt, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(DROP_FNBL_ID);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getConnection();

            stmt = conn.prepareStatement(CREATE_FNBL_ID);
            stmt.execute();

            DBTools.close(null, stmt, null);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

    }

    private void compareDB(IDataSet expectedDataSet,
                           IDataSet currentDataSet,
                           String tableName,
                           String[] excludedCols) throws DataSetException, DatabaseUnitException {
        ITable currentTable = null;
        ITable expectedTable = null;
        expectedTable =
            new SortedTable(DefaultColumnFilter.excludedColumnsTable(expectedDataSet.getTable(tableName), excludedCols));
        currentTable =
            new SortedTable(DefaultColumnFilter.excludedColumnsTable(currentDataSet.getTable(tableName), excludedCols));

        //printTable("expected", expectedTable);
        //printTable("actual", actualTable);

        Assertion.assertEquals(expectedTable, currentTable);
    }


}
