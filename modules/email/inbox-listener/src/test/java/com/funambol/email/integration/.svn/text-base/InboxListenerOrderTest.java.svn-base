/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
package com.funambol.email.integration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import junit.framework.TestCase;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.email.inboxlistener.msdao.MailboxMSDAOCommon;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.SyncItemInfoInbox;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.util.Def;

/**
 * Test cases for InboxListenerOrder class.
 *
 * @version $Id$
 */
public class InboxListenerOrderTest extends TestCase {

    // ------------------------------------------------------------ Private data
    private static String gmailUsername    = "fnbltest@gmail.com";
    private static String gmailPassword    = "funambol123";

    private static String gmailPopUsername = "recent:fnbltestpop@gmail.com";
    private static String gmailPopPassword = "funambol123";

    private static String aolUsername      = "fnbltest";
    private static String aolPassword      = "funambol123";

    private static String hotmailUsername  = "fnbltest@hotmail.com";
    private static String hotmailPassword  = "funambol123";

    private static String yahooUsername    = "fnbltest";
    private static String yahooPassword    = "funambol123";

    private static String funambolUsername    = "fnbltest@funambol.net";
    private static String funambolPassword    = "funambol123";

    private static int maxEmailNumberForAll    = 50;
    private static int maxEmailNumberForRange  = 5;

    private static boolean saveSubject       = true;
    private static boolean saveSender        = false;
    private static String  timeoutStore      = "10000";
    private static String  timeoutConnection = "10000";
    private static boolean checkCertificates = false;

    private static DataSource dataSource = null;

    private static final String JNDI_DS_NAME = "jdbc/fnblds";

    static {
        try {
            System.setProperty("java.naming.factory.initial", "org.osjava.sj.SimpleContextFactory");
            System.setProperty("org.osjava.sj.root", "src/test/simple-jndi-config");
            System.setProperty("org.osjava.sj.delimiter", "/");

            dataSource = DataSourceTools.lookupDataSource(JNDI_DS_NAME);
            createDummyTable();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Constructors
    public InboxListenerOrderTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases
    /**
     *
     * @throws java.lang.Exception
     */
    public void testDummy() throws Exception {

    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Gmail_imap_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForGmail();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("gmail ["+gmailUsername+"] imap-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Gmail_imap_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForGmail();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("gmail ["+gmailUsername+"] imap-all", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Gmail_pop_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForGmailPop();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("gmail ["+gmailPopUsername+"] pop3-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Gmail_pop_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForGmailPop();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("gmail ["+gmailPopUsername+"] pop3-all", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_AOL_imap_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForAOL();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("aol ["+aolUsername+"] imap-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_AOL_imap_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForAOL();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("aol ["+aolUsername+"] imap-all", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Hotmail_pop_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForHotmail();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("hotmail ["+hotmailUsername+"] pop3-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Hotmail_pop_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForHotmail();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("hotmail ["+hotmailUsername+"] pop3-all", msa);
        assertEquals(result, true);
    }


    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Yahoo_pop_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForYahoo();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("yahoo ["+yahooUsername+"] pop3-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Yahoo_pop_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForYahoo();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("yahoo ["+yahooUsername+"] pop3-all", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Funambol_imap_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForFunambol();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("funambol ["+funambolUsername+"] imap-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Funambol_imap_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForFunambol();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("funambol ["+funambolUsername+"] imap-all", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Funambol_pop_range() throws Exception {
        MailServerAccount msa = createMailServerAccountForFunambolPop();
        msa.setMaxEmailNumber(maxEmailNumberForRange);
        boolean result = commonTestSteps("funambol ["+funambolUsername+"] pop-range", msa);
        assertEquals(result, true);
    }

    /**
     *
     * @throws java.lang.Exception
     */
    public void getUIDS_Funambol_pop_all() throws Exception {
        MailServerAccount msa = createMailServerAccountForFunambolPop();
        msa.setMaxEmailNumber(maxEmailNumberForAll);
        boolean result = commonTestSteps("funambol ["+funambolUsername+"] pop-all", msa);
        assertEquals(result, true);
    }

    //---------------------------------------------------------- Private Methods

    /**
     *
     */
    private static void createDummyTable() throws Exception {
        Connection conn = null;
        PreparedStatement stmt = null;

        String CREATE_FNBL_DUMMY = "create table fnbl_dummy (" +
                                   "   id           bigint,   " +
                                   "   description  varchar(64)," +
                                   " PRIMARY KEY (id)" +
                                   ");";

        try {
            conn = dataSource.getConnection();
            stmt = conn.prepareStatement(CREATE_FNBL_DUMMY);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    /**
     *
     * @param label
     * @param msa
     * @throws java.lang.Exception
     */
    private boolean commonTestSteps(String label, MailServerAccount msa)
      throws Exception {

        System.out.println("Start test "+label+";");
        MailboxMSDAOCommon msdao = new MailboxMSDAOCommon(msa, saveSubject, saveSender, timeoutStore, timeoutConnection, checkCertificates);
        IMailServerWrapper mswf = MailServerWrapperFactory.getMailServerWrapper(msa.getMailServer().getProtocol());
        msdao.open(mswf);
        List<SyncItemInfo> serverInfo = getServerInfo(msa, msdao);
        System.out.println("test "+label+"; email in the list: [" + serverInfo.size() + "]");


        boolean isCorrect = checkListOrder(msa, serverInfo);
        System.out.println("test "+label+"; is the list correct: [" + isCorrect + "]");
        msdao.close(mswf);
        System.out.println("End test "+label+"");
        return isCorrect;

    }


    /**
     *
     */
    private boolean checkListOrder(MailServerAccount msa, List<SyncItemInfo> serverInfo)
    throws Exception {

        long recPrev = 0;
        boolean isCorrect = false;

        if (msa.getMailServer().getMailServerType().equals(Def.SERVER_TYPE_HOTMAIL)){
            for (int i=serverInfo.size()-1; i>=0; i--){
                //System.out.println(" --> " + serverInfo.get(i).getHeaderDate());
                //System.out.println(" --> " + serverInfo.get(i).getHeaderReceived());
                //System.out.println("     --> " + serverInfo.get(i).getSubject());
                Date d = serverInfo.get(i).getHeaderReceived();
                if (d == null){
                    d =  serverInfo.get(i).getHeaderDate();
                }
                if (d != null){
                   long rec = d.getTime();
                   if (rec > recPrev){
                       recPrev = rec;
                       isCorrect = true;
                   } else {
                       isCorrect = false;
                       break;
                   }
                }
            }
        } else {
            for (int i=0; i<serverInfo.size(); i++){
                //System.out.println(" --> " + serverInfo.get(i).getHeaderDate());
                //System.out.println(" --> " + serverInfo.get(i).getHeaderReceived());
                //System.out.println("     --> " + serverInfo.get(i).getSubject());
                Date d = serverInfo.get(i).getHeaderReceived();
                if (d == null){
                    d =  serverInfo.get(i).getHeaderDate();
                }
                if (d != null){
                   long rec = d.getTime();
                   if (rec > recPrev){
                       recPrev = rec;
                       isCorrect = true;
                   } else {
                       isCorrect = false;
                       break;
                   }
                }
            }
        }

        return isCorrect;

    }

    /**
     *
     */
    private List<SyncItemInfo> getServerInfo(MailServerAccount msa,
                                             MailboxMSDAOCommon msdao)
    throws Exception {

        String[] validity = new String[1] ;

        List<SyncItemInfoInbox> serverUIDS = msdao.getUids(msa.getMailServer().getProtocol(),
                                                           msa.getMaxEmailNumber(),
                                                           validity);

        String UIDV = validity[0];
        LinkedHashMap uids = new LinkedHashMap();
        int    index      = -1;
        String uid        = null;

        for (int i=0; i<serverUIDS.size(); i++){
            SyncItemInfoInbox syncItemInfoInbox = serverUIDS.get(i);
            uid   = syncItemInfoInbox.getUid();
            index = syncItemInfoInbox.getIndex();
            uids.put(uid, new Integer(index));
        }

        List<SyncItemInfo> serverInfo = msdao.getNewEmails(uids, UIDV);

        return serverInfo;

    }



    /**
     *
     */
    private MailServerAccount createMailServerAccountForGmail() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        //
        msa.setUsername(gmailUsername);
        msa.setMsLogin(gmailUsername);
        msa.setMsPassword(gmailPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("GMail");
        pms.setDescription("Gmail");
        pms.setProtocol("imap");
        pms.setOutServer("smtp.gmail.com");
        pms.setOutPort(465);
        pms.setOutAuth(true);
        pms.setInServer("imap.gmail.com");
        pms.setInPort(993);
        pms.setIsSSLIn(true);
        pms.setIsSSLOut(true);
        pms.setInboxPath("INBOX");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("[Gmail]/Sent Mail");
        pms.setSentActivation(false);
        pms.setDraftsPath("[Gmail]/Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("[Gmail]/Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);


        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForGmailPop() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        //
        msa.setUsername(gmailPopUsername);
        msa.setMsLogin(gmailPopUsername);
        msa.setMsPassword(gmailPopPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("GMail");
        pms.setDescription("Gmail");
        pms.setProtocol("pop3");
        pms.setOutServer("smtp.gmail.com");
        pms.setOutPort(465);
        pms.setOutAuth(true);
        pms.setInServer("pop.gmail.com");
        pms.setInPort(995);
        pms.setIsSSLIn(true);
        pms.setIsSSLOut(true);
        pms.setInboxPath("INBOX");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);


        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForHotmail() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        msa.setUsername(hotmailUsername);
        msa.setMsLogin(hotmailUsername);
        msa.setMsPassword(hotmailPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("Hotmail");
        pms.setDescription("Hotmail");
        pms.setProtocol("pop3");
        pms.setOutServer("smtp.live.com");
        pms.setOutPort(25);
        pms.setOutAuth(true);
        pms.setInServer("pop3.live.com");
        pms.setInPort(995);
        pms.setIsSSLIn(true);
        pms.setIsSSLOut(true);
        pms.setInboxPath("Inbox");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent Items");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForYahoo() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        msa.setUsername(yahooUsername);
        msa.setMsLogin(yahooUsername);
        msa.setMsPassword(yahooPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("Other");
        pms.setDescription("Yahoo");
        pms.setProtocol("pop3");
        pms.setOutServer("smtp.mail.yahoo.com");
        pms.setOutPort(465);
        pms.setOutAuth(true);
        pms.setInServer("pop.mail.yahoo.com");
        pms.setInPort(995);
        pms.setIsSSLIn(true);
        pms.setIsSSLOut(true);
        pms.setInboxPath("Inbox");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent Items");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForFunambol() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        msa.setUsername(funambolUsername);
        msa.setMsLogin(funambolUsername);
        msa.setMsPassword(funambolPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("Other");
        pms.setDescription("Funambol");
        pms.setProtocol("imap");
        pms.setOutServer("mail.funambol.com");
        pms.setOutPort(25);
        pms.setOutAuth(true);
        pms.setInServer("mail.funambol.com");
        pms.setInPort(143);
        pms.setIsSSLIn(false);
        pms.setIsSSLOut(false);
        pms.setInboxPath("INBOX");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForFunambolPop() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        msa.setUsername(funambolUsername);
        msa.setMsLogin(funambolUsername);
        msa.setMsPassword(funambolPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("Other");
        pms.setDescription("Funambol");
        pms.setProtocol("pop3");
        pms.setOutServer("mail.funambol.com");
        pms.setOutPort(25);
        pms.setOutAuth(true);
        pms.setInServer("mail.funambol.com");
        pms.setInPort(110);
        pms.setIsSSLIn(false);
        pms.setIsSSLOut(false);
        pms.setInboxPath("INBOX");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private MailServerAccount createMailServerAccountForAOL() {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // we don't care about these fields
        createMailServerAccountCommon(msa);

        msa.setUsername(aolUsername);
        msa.setMsLogin(aolUsername);
        msa.setMsPassword(aolPassword);

        // mail server
        pms.setMailServerId("1");
        pms.setIsPublic(true);
        pms.setMailServerType("AOL");
        pms.setDescription("AOL");
        pms.setProtocol("imap");
        pms.setOutServer("smtp.aol.com");
        pms.setOutPort(587);
        pms.setOutAuth(true);
        pms.setInServer("imap.aol.com");
        pms.setInPort(143);
        pms.setIsSSLIn(false);
        pms.setIsSSLOut(false);
        pms.setInboxPath("INBOX");
        pms.setInboxActivation(true);
        pms.setOutboxPath("Outbox");
        pms.setOutboxActivation(true);
        pms.setSentPath("Sent Items");
        pms.setSentActivation(false);
        pms.setDraftsPath("Drafts");
        pms.setDraftsActivation(false);
        pms.setTrashPath("Trash");
        pms.setTrashActivation(false);
        pms.setIsSoftDelete(false);

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     * @param msa
     */
    private void createMailServerAccountCommon(MailServerAccount msa) {
        // we don't care about these fields
        msa.setId(1);
        msa.setPeriod(1);
        msa.setActive(true);
        msa.setTaskBeanFile("");
        msa.setLastUpdate(0);
        msa.setStatus("N");
        msa.setPush(false);
        msa.setMaxImapEmail(5);
        msa.setMsAddress("");
        msa.setMsMailboxname("");
        msa.setOutLogin("");
        msa.setOutPassword("");
        msa.setMaxEmailNumber(0); // it will be set in each method
    }

}





