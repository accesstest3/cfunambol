/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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


package com.funambol.email.console.dao;

import com.funambol.email.db.DBHelper;
import java.io.FileInputStream;
import java.util.List;

import junit.framework.TestCase;

import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;


import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Utility;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.encryption.EncryptionTool;
import com.funambol.server.db.DataSourceContextHelper;


/**
 * JUnit class for ConsoleDAO
 *
 * @version $Id$
 */
public class ConsoleDAOTest extends TestCase {

    // --------------------------------------------------------------- Constants

    public static final String GETACCOUNTS_DATASET
        = "src/test/resources/data/com/funambol/email/console/dao/ConsoleDAO/getaccounts.xml";
    public static final String LASTRECEIVEDEMAIL_DATASET
        = "src/test/resources/data/com/funambol/email/console/dao/ConsoleDAO/lastreceivedtime";
    public static final String NUMBEROFUNREADEMAILS_DATASET
        = "src/test/resources/data/com/funambol/email/console/dao/ConsoleDAO/numberofunreademails";
   

    // ----------------------------------------------------------- Instance data

    private ConsoleDAO consoleDAO;
    private IDatabaseTester coreDatabaseTester;
    private IDatabaseTester userDatabasePartition1Tester;
    private IDatabaseTester userDatabasePartition2Tester;
    private IDatabaseTester userDatabasePartition3Tester;
    private IDataSet coreDataSet;
    private IDataSet userDataSet1;
    private IDataSet userDataSet2;
    private IDataSet userDataSet3;

    // ---------------------------------------------------------- Static methods

    static {
        try {

            System.setProperty("java.naming.factory.initial",
					"org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            // Creating email tables belonging to the core
            DBHelper.createCoreEmailTables();

            // Creating email tables belonging to user database
            DBHelper.creareUserPartitionedEmailTables();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    

    // ---------------------------------------------------------- Public methods

    public ConsoleDAOTest(String testName) throws Exception {
        super(testName);

        consoleDAO = null;
        coreDatabaseTester = null;
        userDatabasePartition1Tester = null;
        userDatabasePartition2Tester = null;
        userDatabasePartition3Tester = null;
        coreDataSet = null;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        consoleDAO = new ConsoleDAO();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(coreDatabaseTester!=null) {
            DBTools.close(coreDatabaseTester.getConnection().getConnection(), null, null);
        }
        if(userDatabasePartition1Tester!=null) {
            DBTools.close(userDatabasePartition1Tester.getConnection().getConnection(), null, null);
        }
        if(userDatabasePartition2Tester!=null) {
            DBTools.close(userDatabasePartition1Tester.getConnection().getConnection(), null, null);
        }
        if(userDatabasePartition3Tester!=null) {
            DBTools.close(userDatabasePartition1Tester.getConnection().getConnection(), null, null);
        }


    }

    /**
     * Until multiple email account will be implemented, only one accunt is
     * returned.
     *
     * @throws java.lang.Exception
     */
    public void testGetUserAccounts() throws Exception {
        loadCoreDataSet(GETACCOUNTS_DATASET);

        // Testing when no email accounts exist for a given username
        List<MailServerAccount> results = consoleDAO.getUserAccounts("none");
        assertTrue(results.isEmpty());

        // Testing when one email accounts exist for a given username
        results = consoleDAO.getUserAccounts("username100");
       
        assertTrue(results.size()==1);
        
        //
        // Let's check now that the returned values are correct
        //
        MailServerAccount account = results.get(0);
        ITable pushTable = coreDataSet.getTable(
                EmailConnectorConfig.getConfigInstance().getPushRegistryTableName().toUpperCase()
        );
        ITable accountTable = coreDataSet.getTable(
                ConsoleDAO.ACCOUNT_TABLE_NAME.toUpperCase()
        );

        assertEquals(pushTable.getValue(0, "active").equals("y"), account.getActive());
        assertEquals(pushTable.getValue(0, "last_update"), String.valueOf(account.getLastUpdate()));
        assertEquals(pushTable.getValue(0, "period"), String.valueOf(account.getPeriod()*1000));
        assertEquals(pushTable.getValue(0, "task_bean_file"), account.getTaskBeanFile());
        assertEquals(pushTable.getValue(0, "status"), account.getStatus());
        assertEquals(accountTable.getValue(0, "account_id"), String.valueOf(account.getId()));
        assertEquals(accountTable.getValue(0, "max_num_email"), String.valueOf(account.getMaxEmailNumber()));
        assertEquals(accountTable.getValue(0, "max_imap_email"), String.valueOf(account.getMaxImapEmail()));
        assertEquals(accountTable.getValue(0, "ms_address"), account.getMsAddress());
        assertEquals(accountTable.getValue(0, "ms_login"), account.getMsLogin());
        assertEquals(accountTable.getValue(0, "ms_mailboxname"), account.getMsMailboxname());
        assertEquals(accountTable.getValue(0, "ms_password"), EncryptionTool.encrypt(account.getMsPassword()));
        assertEquals(accountTable.getValue(0, "out_login"), account.getOutLogin());
        assertEquals(accountTable.getValue(0, "out_password"), EncryptionTool.encrypt(account.getOutPassword()));
        assertEquals(accountTable.getValue(0, "push").equals("y"), account.getPush());
        assertEquals(accountTable.getValue(0, "username"), account.getUsername());
    }

    public void testGetTimeOfLastReceivedEmail() throws Exception {
        long result = consoleDAO.getTimeOfLastReceivedEmail("username1");
        assertEquals(0, result);

        loadUserDataSet(LASTRECEIVEDEMAIL_DATASET);

        ITable table = userDataSet1.getTable(
                ConsoleDAO.INBOX_TABLE_NAME.toUpperCase()
        );

        result = consoleDAO.getTimeOfLastReceivedEmail("username1");
        assertEquals(
            Utility.UtcToLong((String)table.getValue(0, "received")),
            result
        );
    }


    /**
     *
     * Tests the method used to retrieve the number of unread emails.
     *
     * @throws java.lang.Exception if any error occurs.
     */

     public void testGetNumUnreadEmail() throws Exception {
         loadUserDataSet(NUMBEROFUNREADEMAILS_DATASET);

         // username 102 has no emails
         int numberOfUnreadEmails = consoleDAO.getNumUnreadEmail("username102");
         assertEquals(0, numberOfUnreadEmails);

         // username100 has 9 emails unread in the inbox and a limit of 7
         numberOfUnreadEmails = consoleDAO.getNumUnreadEmail("username100");
         assertEquals(7, numberOfUnreadEmails);

         // username100 has 10 emails in the inbox and a limit of 8
         // 2 emails inside the limit and 1 outside have been read,
         // the other ones are unread.
         numberOfUnreadEmails = consoleDAO.getNumUnreadEmail("username101");
         assertEquals(6, numberOfUnreadEmails);


         // username103 has 6 mails 3 read and 3 unread and a limit of 8
         numberOfUnreadEmails = consoleDAO.getNumUnreadEmail("username103");
         assertEquals(3, numberOfUnreadEmails);
         
     }
    // --------------------------------------------------------- Private methods

    private void loadCoreDataSet(String source) throws Exception {
        coreDatabaseTester = createDatabaseTester(source);
        coreDataSet        = coreDatabaseTester.getDataSet();
    }

    private void loadUserDataSet(String source) throws Exception {
        userDatabasePartition1Tester = createDatabaseTester(source+"_1.xml", DBHelper.USER_PART1);
        userDataSet1                 = userDatabasePartition1Tester.getDataSet();

        userDatabasePartition2Tester = createDatabaseTester(source+"_2.xml", DBHelper.USER_PART2);
        userDataSet2                 = userDatabasePartition2Tester.getDataSet();

        userDatabasePartition3Tester = createDatabaseTester(source+"_3.xml", DBHelper.USER_PART3);
        userDataSet3                 = userDatabasePartition3Tester.getDataSet();
    }

    private IDatabaseTester createDatabaseTester(String source, boolean settingUp) throws Exception {
        IDatabaseTester databaseTester  = new DefaultDatabaseTester(new DatabaseConnection(DBHelper.getCoreConnection()));
        IDataSet        databaseDataSet = new FlatXmlDataSet(new FileInputStream(source));
        databaseTester.setDataSet(databaseDataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        if(settingUp) {
            databaseTester.onSetup();
        }
        return databaseTester;
    }

    private IDatabaseTester createDatabaseTester(String source) throws Exception {
        return createDatabaseTester(source, true);
    }

    private IDatabaseTester createDatabaseTester(String source, String connectionKey, boolean settingUp) throws Exception {
        IDatabaseTester databaseTester  = new DefaultDatabaseTester(new DatabaseConnection(DBHelper.getUserConnection(connectionKey)));
        IDataSet        databaseDataSet = new FlatXmlDataSet(new FileInputStream(source));
        databaseTester.setDataSet(databaseDataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        if(settingUp) {
            databaseTester.onSetup();
        }
        return databaseTester;
    }

    private IDatabaseTester createDatabaseTester(String source, String connectionKey) throws Exception {
        return createDatabaseTester(source, connectionKey,true);
    }




}
