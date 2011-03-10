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
package com.funambol.email.inboxlistener.dbdao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.funambol.email.exception.AccountNotFoundException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.SyncItemInfoInbox;
import com.funambol.email.util.Def;
import com.funambol.email.util.Query;
import com.funambol.email.util.Utility;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.pushlistener.service.registry.RegistryEntryStatus;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;

import com.funambol.server.db.RoutingDataSource;

/**
 * This class implements database access method to be used by the pim listener.
 * Note that it implements the methods to handle pim data not to handle the pim
 * registry entries
 * (those are handled by com.funambol.pimlistener.registry.dao.InboxRegistryEntryDao)
 *
 * @version $Id: InboxListenerDAO.java,v 1.8 2008-06-03 10:19:04 testa Exp $
 */
public class InboxListenerDAO {

    // --------------------------------------------------------------- Constants

    private String pushRegistryTableName;

    /** The logger */
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    private static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";
    
    private static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";

    // ------------------------------------------------------------ Private data
    
    private DataSource coreDataSource = null;
    private RoutingDataSource userDataSource = null;

    // ------------------------------------------------------------- Constructor

    public InboxListenerDAO(String pushRegistryTableName) 
    throws InboxListenerException 
    {
        this.pushRegistryTableName = pushRegistryTableName;
        
        try {

            coreDataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);
            userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);

        } catch (Exception e){
            throw new InboxListenerException("Error creating InboxListenerDAO Object ", e);
    }

    }

    // ---------------------------------------------------------- Public Methods


    /**
     *
     * get user
     *
     * @param accountID long
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getAccountByID(long accountID)
    throws DataAccessException, AccountNotFoundException {

        MailServerAccount msa = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String query = MessageFormat.format(Query.MS_ACCOUNT_GET_USER_FROM_ID,
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);

            ps.setLong(1, accountID) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                msa = createMailServerAccount(rs);
            }

            if (msa == null){
                throw new AccountNotFoundException("account with accountID " +
                        accountID + " not found");
            }

        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException("Error getting Account ", e);
        } catch (SQLException e) {
            throw new DataAccessException("Error (sql) getting Account from DB ", e);
        } catch (Exception e) {
            throw new DataAccessException("Error (generic) getting Account from DB ", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return msa;
    }

    /**
     *
     * get user
     *
     * @param emailaddress user email address
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getAccountByEmailaddress(String emailaddress)
    throws DataAccessException, AccountNotFoundException {

        MailServerAccount msa = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String query = MessageFormat.format(Query.MS_ACCOUNT_GET_USER_FROM_EMAILADDRESS,
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);

            ps.setString(1, emailaddress) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                msa = createMailServerAccount(rs);
            }

            if (msa == null){
                throw new AccountNotFoundException("Account with emailaddress " +
                        emailaddress + " not found");
            }

        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException("Error getting Account ", e);
        } catch (SQLException e) {
            throw new DataAccessException("Error (sql) getting Account from DB ", e);
        } catch (Exception e) {
            throw new DataAccessException("Error (generic) getting Account from DB ", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return msa;
    }

    /**
     *
     * get user
     *
     * @param emailaddress user email address
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getAccountByUsername(String username)
    throws DataAccessException, AccountNotFoundException {

        MailServerAccount msa = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String query = MessageFormat.format(Query.MS_ACCOUNT_GET_USER_FROM_USERNAME,
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);

            ps.setString(1, username) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                msa = createMailServerAccount(rs);
            }

            if (msa == null){
                throw new AccountNotFoundException("account with username " +
                        username + " not found");
            }

        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException(e.getMessage());
        } catch (SQLException e) {
            throw new DataAccessException("Error (sql) getting Account from DB ", e);
        } catch (Exception e) {
            throw new DataAccessException("Error (generic) getting Account from DB ", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return msa;
    }

    /**
     * Retrieve a mail server account given its mailboxname.
     * @param mailboxname mailboxname of the mail server account
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException
     * @return the mail server account
     */
    public MailServerAccount getAccountByMailboxname(String mailboxname)
    throws DataAccessException, AccountNotFoundException {

        MailServerAccount msa = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String query = MessageFormat.format(Query.MS_ACCOUNT_GET_USER_FROM_MAILBOXNAME,
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);

            ps.setString(1, mailboxname) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                msa = createMailServerAccount(rs);
            }

            if (msa == null){
                throw new AccountNotFoundException("account with mailboxname " +
                        mailboxname + " not found");
            }

        } catch (AccountNotFoundException e) {
            throw new AccountNotFoundException("Error getting Account ", e);
        } catch (SQLException e) {
            throw new DataAccessException("Error (sql) getting Account from DB ", e);
        } catch (Exception e) {
            throw new DataAccessException("Error (generic) getting Account from DB ", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return msa;
    }

    /**
     *
     * Get the Items (Emails) from DB order by received_date desc
     *
     * @param username
     * @param protocol
     * @return map with all local CrcSyncItemInfo
     *
     */
    public LinkedHashMap getItems(String username,
            String protocol,
            String[] firstGUIDArray)
    throws DataAccessException {

        LinkedHashMap     items          = new LinkedHashMap();
        Connection        connection     = null;
        PreparedStatement ps             = null;
        ResultSet         rs             = null;
        String            guid           = null;
        long              last_crc       = 0;
        String            isInvalid      = null;
        String            isInternal     = null;
        String            messageid      = null;
        Date              headerdate     = null;
        Date              received       = null;
        String            subject        = null;
        String            sender         = null;
        String            status         = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.INBOX_SELECT_ALL);

            ps.setString(1, username);
            ps.setString(2, protocol);

            rs = ps.executeQuery();

            int counter = 0;

            while(rs.next()) {

                guid           = rs.getString(1);
                messageid      = rs.getString(2);
                headerdate     = Utility.UtcToDate(rs.getString(3));
                received       = Utility.UtcToDate(rs.getString(4));
                subject        = rs.getString(5);
                sender         = rs.getString(6);
                last_crc       = rs.getLong(7);
                isInvalid      = rs.getString(8);
                isInternal     = rs.getString(9);
                status         = rs.getString(10);

                items.put(guid, new SyncItemInfo(new SyncItemKey(guid), last_crc,
                        messageid, headerdate, received, subject, sender,
                        isInvalid, isInternal, "Y", status));

                if (counter == 0){
                    // keep the last guid - the newest email
                    firstGUIDArray[0] = guid;
                }
                counter++;

            }

        } catch (SQLException e) {
            throw new DataAccessException("Error setting ServerItems into DataBase", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return items;
    }

    /**
     *
     * Set the ServerItems into DB
     *
     */
    public void setItems(String username,
            String protocol,
            List<SyncItemInfo> serverInfoTobeAdded,
            List<SyncItemInfoInbox> serverInfoTobeUpdated,
            List<SyncItemInfo> deletedGUIDS)
    throws DataAccessException {

        Connection connection        = null;
        PreparedStatement ps         = null;

        String          GUID         = null;
        long            lastCRC      = 0;
        String          messageID    = null;
        Date            headerDate   = null;
        String          headerDateS  = null;
        Date            received     = null;
        String          receivedS    = null;
        String          subject      = null;
        String          sender       = null;
        String          isInvalid    = null;
        String          isInternal   = null;
        String          status       = null;
        String          token        = null;

        try {

            connection = userDataSource.getRoutedConnection(username);

            connection.setAutoCommit(false);

            // remove data
            for (int i=0; i<deletedGUIDS.size(); i++ ){
                ps = connection.prepareStatement(Query.INBOX_DELETE_EMAIL);
                GUID = deletedGUIDS.get(i).getGuid().getKeyAsString();
                ps.setString  (1, GUID) ;
                ps.setString  (2, username) ;
                ps.setString  (3, protocol) ;
                ps.executeUpdate();
                DBTools.close(null, ps, null);
            }

            // insert data
            SyncItemInfo syncItemInfo    = null;

            for (int i=0; i<serverInfoTobeAdded.size(); i++ ){

                ps = connection.prepareStatement(Query.INBOX_INSERT_EMAIL);
                syncItemInfo = (SyncItemInfo)serverInfoTobeAdded.get(i);

                GUID        = syncItemInfo.getGuid().getKeyAsString();
                lastCRC     = syncItemInfo.getLastCrc();
                messageID   = syncItemInfo.getMessageID();
                headerDate  = syncItemInfo.getHeaderDate();
                if (headerDate != null){
                    headerDateS = Utility.UtcFromDate(headerDate);
                }
                received    = syncItemInfo.getHeaderReceived();
                if (received != null){
                    receivedS = Utility.UtcFromDate(received);
                }
                subject     = syncItemInfo.getSubject();
                sender      = syncItemInfo.getSender();
                isInvalid   = syncItemInfo.getInvalid();
                isInternal  = syncItemInfo.getInternal();
                status      = syncItemInfo.getStatus();
                token       = syncItemInfo.getToken();
                
                ps.setString(1, username);
                ps.setString(2, protocol);
                ps.setString(3, GUID);
                ps.setLong  (4, lastCRC);
                ps.setString(5, isInvalid);
                ps.setString(6, isInternal);
                ps.setString(7, messageID);
                ps.setString(8, headerDateS);
                ps.setString(9, receivedS);
                ps.setString(10, subject);
                ps.setString(11, sender);
                ps.setString(12, status);
                ps.setString(13, token);

                ps.executeUpdate();

                DBTools.close(null, ps, null);
            }

            // update data
            SyncItemInfoInbox syncItemInfoInbox    = null;

            for (int i=0; i<serverInfoTobeUpdated.size(); i++ ){

                ps = connection.prepareStatement(Query.INBOX_UPDATE_EMAIL);
                syncItemInfoInbox = serverInfoTobeUpdated.get(i);

                GUID        = syncItemInfoInbox.getGuid().getKeyAsString();
                lastCRC     = syncItemInfoInbox.getLastCrc();

                ps.setLong   (1, lastCRC );
                
                ps.setString (2, GUID    );
                ps.setString (3, username);
                ps.setString (4, protocol);

                ps.executeUpdate();
                DBTools.close(null, ps, null);
            }

            connection.commit();

        } catch (SQLException sqle) {
            try {
                connection.rollback();
            } catch (SQLException sqlee){
                throw new DataAccessException("Error rollbacking", sqlee);
            }
            throw new DataAccessException("Error refreshing Local Items", sqle);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * Removes the ServerItems from the DB
     *
     */
    public void removeItem(String username, String protocol, String GUID)
    throws DataAccessException {

        Connection        connection = null;
        PreparedStatement ps         = null;

        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_DELETE_EMAIL);

            ps.setString(1, GUID    );
            ps.setString(2, username);
            ps.setString(3, protocol);

            ps.executeUpdate();


        } catch (SQLException sqle) {
            throw new DataAccessException("Error removing Local Item", sqle);
        } finally {
            DBTools.close(connection, ps, null);
        }

    }
    

  //------------------------------------------------------------ Private Methods

    /**
     *
     */
    private MailServerAccount createMailServerAccount(ResultSet rs)
    throws SQLException, Exception {

        MailServerAccount msa = new MailServerAccount();
        MailServer        pms = new MailServer();

        // like getId() from fnbl_email_push_registry
        msa.setId(rs.getLong(1));

        // check unit measure
        // in the UI we show the minutes but in the DB we save the seconds
        msa.setPeriod(Utility.setMinutes(rs.getLong(2)));

        msa.setActive(Utility.booleanFromString(rs.getString(3)));
        msa.setTaskBeanFile(rs.getString(4));
        msa.setLastUpdate(rs.getLong(5));

        String s = rs.getString(6);
        if (s == null || s.length() == 0) {
            msa.setStatus(RegistryEntryStatus.UNKNOWN);
        }
        if        ("N".equals(s)) {
            msa.setStatus(RegistryEntryStatus.NEW);
        } else if ("U".equals(s)) {
            msa.setStatus(RegistryEntryStatus.UPDATED);
        } else if ("D".equals(s)) {
            msa.setStatus(RegistryEntryStatus.DELETED);
        } else {
            msa.setStatus(RegistryEntryStatus.UNKNOWN);
        }

        msa.setUsername(rs.getString(7));
        msa.setMsLogin(rs.getString(8));
        msa.setMsPassword(getTextPlainPassword(rs.getString(9)));
        msa.setMsAddress(rs.getString(10));
        msa.setPush(Utility.booleanFromString(rs.getString(11)));
        msa.setMaxEmailNumber(rs.getInt(12));
        msa.setMaxImapEmail(rs.getInt(13));

        // mail server
        pms.setMailServerId(rs.getString(14));
        pms.setMailServerType(rs.getString(15));
        pms.setDescription(rs.getString(16));
        pms.setProtocol(rs.getString(17));
        pms.setOutServer(rs.getString(18));
        pms.setOutPort(rs.getInt(19));
        pms.setOutAuth(Utility.booleanFromString(rs.getString(20)));
        pms.setInServer(rs.getString(21));
        pms.setInPort(rs.getInt(22));
        pms.setIsSSLIn(Utility.booleanFromString(rs.getString(23)));
        pms.setIsSSLOut(Utility.booleanFromString(rs.getString(24)));

        pms.setInboxPath(rs.getString(25));
        pms.setInboxActivation(Utility.booleanFromString(rs.getString(26)));
        pms.setOutboxPath(rs.getString(27));
        pms.setOutboxActivation(Utility.booleanFromString(rs.getString(28)));
        pms.setSentPath(rs.getString(29));
        pms.setSentActivation(Utility.booleanFromString(rs.getString(30)));
        pms.setDraftsPath(rs.getString(31));
        pms.setDraftsActivation(Utility.booleanFromString(rs.getString(32)));
        pms.setTrashPath(rs.getString(33));
        pms.setTrashActivation(Utility.booleanFromString(rs.getString(34)));
        pms.setIsSoftDelete(Utility.booleanFromString(rs.getString(35)));
        pms.setIsPublic(Utility.booleanFromString(rs.getString(36)));
        msa.setMsMailboxname(rs.getString(37));

        msa.setMailServer(pms);

        return msa;
    }

    /**
     *
     */
    private String getTextPlainPassword(String encryptedPassword)
    throws Exception{

        String plain_text = null;
        try {
            plain_text = EncryptionTool.decrypt(encryptedPassword);
        } catch (Exception e){
            throw new Exception("Error handling password decryption");
        }

        return plain_text;

    }

    /**
     * Checks if the ds username with given usesrname exists.
     *
     * @param username username for a ds user
     * @return <code>true</code> if username exists, <code>false</code> otherwise
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException
     * @throws com.funambol.email.exception.AccountNotFoundException
     */
    public boolean dsUserExists(String username)
    throws DataAccessException, AccountNotFoundException {

        boolean userExists = false;

        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.DS_USER_EXISTS);

            ps.setString(1, username) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                userExists = true;
            }

        } catch (SQLException e) {
            throw new DataAccessException(
                    "Error (sql) checking for ds user from DB ", e);
        } catch (Exception e) {
            throw new DataAccessException(
                    "Error (generic) checking for ds user DB ", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return userExists;
    }
    
    // --------------------------------------------------------- Private methods
    
    private String getIsInMyContactsAsString(boolean isInMyContacts){
        return (isInMyContacts == true ? "Y" : "N");
    }
}
