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
package com.funambol.email.console.dao;

import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.DBAccessException;
import com.funambol.email.exception.InboxListenerConfigException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfoAdmin;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.util.Def;
import com.funambol.email.util.MSTools;
import com.funambol.email.util.Query;
import com.funambol.email.util.Utility;
import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.store.PreparedWhere;
import com.funambol.framework.server.store.SQLHelperClause;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.encryption.EncryptionTool;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;
import com.funambol.pushlistener.service.registry.dao.RegistryDao;
import com.funambol.server.db.RoutingDataSource;

import com.sun.mail.imap.IMAPFolder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.sql.DataSource;

/**
 * This class implements methods to access data
 * in Connector DB Schema.
 *
 * @version $Id: ConsoleDAO.java,v 1.4 2008-05-18 15:12:18 nichele Exp $
 */
public class ConsoleDAO {

    // --------------------------------------------------------------- Constants

    /** The idSpace used to create the registry entries */
    protected static final String REGISTRY_ID_SPACE = "email.registryid";

    /**     */
    protected static final FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    public static final String CORE_DATASOURCE_JNDINAME = "jdbc/fnblcore";

    public static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";
    
    public static final String ACCOUNT_TABLE_NAME = "fnbl_email_account";

    public static final String INBOX_TABLE_NAME   = "fnbl_email_inbox";



    // -------------------------------------------------------------- Properties

    /** Datasource to use to access to the core db  */
    private DataSource coreDataSource    = null;
    
    /** Datasource to use to access to the user db (that is partitioned)  */
    private RoutingDataSource userDataSource    = null;

    /**     */
    private RegistryDao registryDao = null;

    /** Name of the table that stores the registry. */
    private String pushRegistryTableName;

    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public ConsoleDAO() throws InboxListenerConfigException {
        try {
            
            this.pushRegistryTableName = 
                    EmailConnectorConfig.getConfigInstance().getPushRegistryTableName();

            coreDataSource = DataSourceTools.lookupDataSource(CORE_DATASOURCE_JNDINAME);
            userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);

            registryDao = new RegistryDao(pushRegistryTableName, REGISTRY_ID_SPACE, coreDataSource);

        } catch (Exception e){
            throw new InboxListenerConfigException("Error creating ConsoleDAO Object ", e);
        }
    }

    // -----------------------------------------------------------Public Methods

    /**
     *
     * insert user and the mail server configuration
     * insert the entry in the push framework schema
     *
     * @param msa MailServerAccount
     * @return result of insert operation int
     * @throws EntityException
     * @deprecated since version 8; use insertUserAccount() instead
     */
    public int insertUser(MailServerAccount msa) throws DBAccessException {
        return insertUserAccount(msa);
    }

    /**
     *
     * insert user and the mail server configuration
     * insert the entry in the push framework schema
     *
     * @param msa MailServerAccount
     * @return result of insert operation int
     * @throws EntityException
     */
    public int insertUserAccount(MailServerAccount msa) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            // I step
            // push listener framework
            // insert listenerID, active, refreshTime
            long idRegistry = registryDao.insertRegistryEntry(msa);
            msa.setId(idRegistry);

            // email listener schema
            connection = coreDataSource.getConnection();

            // II step
            // insert into fnbl_email_account
            ps = connection.prepareStatement(Query.MS_ACCOUNT_INSERT_USER);

            // account
            ps.setLong  (1, msa.getId());
            ps.setString(2, msa.getUsername());
            ps.setString(3, msa.getMsLogin());

            // handling passwords
            String plain_text = msa.getMsPassword();
            String encryptedPassword =
                    EncryptionTool.encrypt(plain_text);
            ps.setString(4, encryptedPassword);

            String plainPasswordOut = msa.getOutPassword();            
            String encryptedPasswordOut = null;
            if (plainPasswordOut != null){
                    encryptedPasswordOut =
                            EncryptionTool.encrypt(plainPasswordOut);
            }
            
            ps.setString(5, msa.getMsAddress());

            ps.setString(6, Utility.booleanToString(msa.getPush()));
            ps.setInt   (7, msa.getMaxEmailNumber());
            ps.setInt   (8, msa.getMaxImapEmail());

            // mail server
            ps.setString(9, msa.getMailServer().getMailServerId());
            ps.setString(10, msa.getMailServer().getMailServerType());
            ps.setString(11, msa.getMailServer().getDescription());
            ps.setString(12, msa.getMailServer().getProtocol());
            ps.setString(13, msa.getMailServer().getOutServer());
            ps.setInt   (14, msa.getMailServer().getOutPort());
            ps.setString(15, Utility.booleanToString(msa.getMailServer().getOutAuth()));
            ps.setString(16, msa.getMailServer().getInServer());
            ps.setInt   (17, msa.getMailServer().getInPort());
            ps.setString(18, Utility.booleanToString(msa.getMailServer().getIsSSLIn()));
            ps.setString(19, Utility.booleanToString(msa.getMailServer().getIsSSLOut()));
            ps.setString(20, msa.getMailServer().getInboxPath());
            ps.setString(21, Utility.booleanToString(msa.getMailServer().getInboxActivation()));
            ps.setString(22, msa.getMailServer().getOutboxPath());
            ps.setString(23, Utility.booleanToString(msa.getMailServer().getOutboxActivation()));
            ps.setString(24, msa.getMailServer().getSentPath());
            ps.setString(25, Utility.booleanToString(msa.getMailServer().getSentActivation()));
            ps.setString(26, msa.getMailServer().getDraftsPath());
            ps.setString(27, Utility.booleanToString(msa.getMailServer().getDraftsActivation()));
            ps.setString(28, msa.getMailServer().getTrashPath());
            ps.setString(29, Utility.booleanToString(msa.getMailServer().getTrashActivation()));
            ps.setString(30, Utility.booleanToString(msa.getMailServer().getIsSoftDelete()));
            ps.setString(31, Utility.booleanToString(msa.getMailServer().getIsPublic()));
            ps.setString(32, msa.getMsMailboxname());
            ps.setString(33, msa.getOutLogin());
            ps.setString(34, encryptedPasswordOut);

            result = ps.executeUpdate();


            // III step
            // insert into fnbl_email_enable_account
            ps = connection.prepareStatement(Query.MS_ACCOUNT_ENABLER);
            ps.setLong  (1, msa.getId());
            ps.setString(2, msa.getUsername());

            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }

    /**
     *
     * delete user
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     *
     * @deprecated from version 8; use deleteUserAccount() instead
     */
    public int deleteUser(long accountID, long[] principalIds)  throws DBAccessException {
        return deleteUserAccount(accountID, principalIds);
    }

    /**
     *
     * delete user
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     */
    public int deleteUserAccount(long accountID, long[] principalIds)  throws DBAccessException {
        Connection coreConnection = null;
        Connection userConnection = null;
        PreparedStatement  corePs = null;
        PreparedStatement  userPs = null;

        int result            = 0;

        try {

            String username = getUsername(accountID);

            coreConnection = coreDataSource.getConnection();
            userConnection = userDataSource.getRoutedConnection(username);
            
            long principalId = 0;
            for (int i = 0; i < principalIds.length; i++){
                principalId = principalIds[i];

                // delete from fnbl_email_cache
                userPs = userConnection.prepareStatement(Query.CACHE_DELETE_ITEMS);
                userPs.setLong(1, principalId);
                userPs.executeUpdate();

                // delete from fnbl_email_folder
                userPs = userConnection.prepareStatement(Query.FOLDER_DELETE_ITEMS);
                userPs.setLong(1, principalId);
                userPs.executeUpdate();

                // delete from fnbl_email_sentpop
                userPs = userConnection.prepareStatement(Query.SENT_DELETE_POP_BY_PRINCIPAL);
                userPs.setLong(1, principalId);
                userPs.executeUpdate();
            
            }

            // delete from fnbl_email_account            
            corePs = coreConnection.prepareStatement(Query.MS_ACCOUNT_DELETE_USER);
            corePs.setLong(1, accountID);
            result = corePs.executeUpdate();

            // delete from fnbl_email_enable_account
            corePs = coreConnection.prepareStatement(Query.MS_ACCOUNT_DELETE_ENABLE_USER);
            corePs.setLong(1, accountID);
            result = result + corePs.executeUpdate();

            // delete from fnbl_email_inbox
            userPs = userConnection.prepareStatement(Query.INBOX_DELETE_EMAILS);
            userPs.setString(1, username);
            userPs.executeUpdate();

            registryDao.markAsDeleted(accountID);
            
        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(coreConnection, corePs, null);
            DBTools.close(userConnection, userPs, null);
        }

        return result;
    }

    /**
     *
     * removed all the emails from the fnbl_email_inbox for
     * the given username
     *
     * @param username String
     * @return int
     * @throws EntityException
     */
    public int clearCache(String username)  throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = userDataSource.getRoutedConnection(username);

            // delete from fnbl_email_inbox
            ps = connection.prepareStatement(Query.INBOX_DELETE_EMAILS);
            ps.setString(1, username);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }

    /**
     *
     * removed all the folders from the fnbl_email_folder for
     * the given username
     *
     * @param username the username 
     * @param principalID the principal id
     * @return int
     * @throws EntityException
     */
    public int clearFolder(String username, long principalID)  throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = userDataSource.getRoutedConnection(username);

            // delete from fnbl_email_folder
            ps = connection.prepareStatement(Query.FOLDER_DELETE_ITEMS);
            ps.setLong(1, principalID);
            ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }

    /**
     *
     * disable account
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     * @deprecated since version 8; use disableUserAccount instead
     */
    public int disableUser(long accountID) throws DBAccessException {
        return disableUserAccount(accountID);
    }

    /**
     *
     * disable account
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     */
    public int disableUserAccount(long accountID) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = coreDataSource.getConnection();

            // delete from fnbl_email_account
            String query = MessageFormat.format(Query.MS_ACCOUNT_DISABLE_USER,
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);

            ps.setLong   (1, System.currentTimeMillis());
            ps.setString (2, RegistryEntryStatus.UPDATED);
            ps.setLong   (3, accountID);
            
            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }

    /**
     *
     * disable account
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     * @deprecated since version 8; use enableUserAccount() instead
     */
    public int enableUser(long accountID) throws DBAccessException {
        return enableUserAccount(accountID);
    }

    /**
     *
     * disable account
     *
     * @param accountID long
     * @return int
     * @throws EntityException
     */
    public int enableUserAccount(long accountID) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = coreDataSource.getConnection();

            // delete from fnbl_email_account
            String query = MessageFormat.format(Query.MS_ACCOUNT_ENABLE_USER, 
                    new Object[] {pushRegistryTableName});
            ps = connection.prepareStatement(query);
            
            ps.setLong   (1, System.currentTimeMillis());
            ps.setString (2, RegistryEntryStatus.UPDATED);
            ps.setLong   (3, accountID);
            
            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }

    /**
     *
     * mark account as delete
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     * @deprecated since version 8; use markUserAccountAsDelete
     */
    public int markUserAsDelete(MailServerAccount msa) throws DBAccessException {
        return markUserAccountAsDelete(msa);
    }

    /**
     *
     * mark account as delete
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     */
    public int markUserAccountAsDelete(MailServerAccount msa) throws DBAccessException {
        int result = 0;
        try {
            result = registryDao.updateRegistryEntry(msa);
        } catch (Exception e) {
            throw new DBAccessException(e);
        }
        return result;

    }

    /**
     *
     * update user regarding the mail server connection
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     * @deprecated since version 8; use updateUserAccount() instead
     */
    public int updateUser(MailServerAccount msa) throws DBAccessException {
        return updateUserAccount(msa);
    }


    /**
     *
     * update user regarding the mail server connection
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     */
    public int updateUserAccount(MailServerAccount msa) throws DBAccessException {
        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            // I step
            // update listenerID, active, refreshTime
            int updatedRows = registryDao.updateRegistryEntry(msa);

            connection = coreDataSource.getConnection();

            ps = connection.prepareStatement(Query.MS_ACCOUNT_UPDATE_USER);

            // II step
            // account
            ps.setString(1, msa.getMsLogin());

            // handling password
            String plain_text = msa.getMsPassword();
            String plainPasswordOut = msa.getOutPassword();
            String encryptedPassword = EncryptionTool.encrypt(plain_text);
            
            ps.setString(2, encryptedPassword);

            String encryptedPasswordOut = null;
            if (plainPasswordOut != null){
                encryptedPasswordOut =
                        EncryptionTool.encrypt(plainPasswordOut);
            }

            ps.setString(3, msa.getMsAddress());

            ps.setString(4, Utility.booleanToString(msa.getPush()));
            ps.setInt   (5, msa.getMaxEmailNumber());
            ps.setInt   (6, msa.getMaxImapEmail());

            // mail server
            ps.setString(7, msa.getMailServer().getMailServerId());
            ps.setString(8, msa.getMailServer().getMailServerType());
            ps.setString(9, msa.getMailServer().getDescription());
            ps.setString(10, msa.getMailServer().getProtocol());
            ps.setString(11, msa.getMailServer().getOutServer());
            ps.setInt   (12, msa.getMailServer().getOutPort());
            ps.setString(13, Utility.booleanToString(msa.getMailServer().getOutAuth()));
            ps.setString(14, msa.getMailServer().getInServer());
            ps.setInt   (15, msa.getMailServer().getInPort());
            ps.setString(16, Utility.booleanToString(msa.getMailServer().getIsSSLIn()));
            ps.setString(17, Utility.booleanToString(msa.getMailServer().getIsSSLOut()));
            ps.setString(18, msa.getMailServer().getInboxPath());
            ps.setString(19, Utility.booleanToString(msa.getMailServer().getInboxActivation()));
            ps.setString(20, msa.getMailServer().getOutboxPath());
            ps.setString(21, Utility.booleanToString(msa.getMailServer().getOutboxActivation()));
            ps.setString(22, msa.getMailServer().getSentPath());
            ps.setString(23, Utility.booleanToString(msa.getMailServer().getSentActivation()));
            ps.setString(24, msa.getMailServer().getDraftsPath());
            ps.setString(25, Utility.booleanToString(msa.getMailServer().getDraftsActivation()));
            ps.setString(26, msa.getMailServer().getTrashPath());
            ps.setString(27, Utility.booleanToString(msa.getMailServer().getTrashActivation()));
            ps.setString(28, Utility.booleanToString(msa.getMailServer().getIsSoftDelete()));
            ps.setString(29, Utility.booleanToString(msa.getMailServer().getIsPublic()));
            ps.setString(30, msa.getMsMailboxname());
            ps.setString(31, msa.getOutLogin());
            ps.setString(32, encryptedPasswordOut);

            ps.setLong(33, msa.getId());
            ps.setString(34, msa.getUsername());

            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;

    }
    
    /**
     *
     * get users in the filter
     *
     * @param clause Clause
     * @return list with all the user in the filter ArrayList
     * @throws EntityException
     * @deprecated since version 8; use getUserAccounts() instead
     */
    public MailServerAccount[] getUsers(Clause clause) throws DBAccessException {
        return getUserAccounts(clause);
    }


    /**
     *
     * get users in the filter
     *
     * @param clause Clause
     * @return list with all the user in the filter ArrayList
     * @throws EntityException
     */
    public MailServerAccount[] getUserAccounts(Clause clause) throws DBAccessException {

        ArrayList msal         = new ArrayList();
        MailServerAccount[] msalOut = null;
        Connection connection  = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        MailServerAccount msa  = null;
        MailServer        pms  = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String dbProductName = connection.getMetaData().getDatabaseProductName();
            SQLHelperClause sqlHelperClause = new SQLHelperClause(dbProductName);

            String query = MessageFormat.format(Query.MS_ACCOUNT_LIST_USER,
                    new Object[] {pushRegistryTableName});

            if (clause != null){
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    if (where.sql.length() > 0){
                        query += " and " + where.sql;
                    }
                }
                stmt = connection.prepareStatement(query);
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    for (int i=0; i<where.parameters.length; ++i) {
                        stmt.setObject(i+1, where.parameters[i]);
                    }
                }
            } else {
                stmt = connection.prepareStatement(query);
            }

            rs = stmt.executeQuery();

            while(rs.next()) {
                msa = createMailServerAccount(rs);
                msal.add(msa);
            }

            msalOut =  (MailServerAccount[])msal.toArray(new MailServerAccount[0]) ;

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, stmt, rs);
        }

        return msalOut;
    }

    /**
     *
     * Returns the account associated to the given username.
     * This is deprecated in favor getUserAccounts(), which is ready for
     * multiple emails account.
     *
     * @param username
     *
     * @return the account associated to the given username.
     *
     * @throws com.funambol.email.exception.DBAccessException
     * @deprecated from version 8.0, use getUserAccount instead
     */

    public MailServerAccount getUser(String username) throws DBAccessException {
        List<MailServerAccount> accounts = getUserAccounts(username);
        if(accounts!=null && accounts.size()>0) {
            return accounts.get(0);
        }
        return null;
    }
    
    /**
     * Returns the array of the accounts associated to the user.
     * For now it still returns just the only email account associated to the
     * user, but the signature is ready for when we will implement multiple
     * email accounts.
     *
     *
     * @param username String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public List<MailServerAccount> getUserAccounts(String username) throws DBAccessException {

        List<MailServerAccount> accounts = new ArrayList<MailServerAccount>();
        
        Connection connection   = null;
        PreparedStatement  ps   = null;
        ResultSet rs            = null;

        try {
            //
            // TODO: the below should return more than one id when multiple
            // email accounts will be implemented.
            //
            long accountId = getAccountId(username);

            if (accountId != -1){

                connection = coreDataSource.getConnection();
                connection.setReadOnly(true);
                
                String query = MessageFormat.format(Query.MS_ACCOUNT_GET_USER_FROM_ID,
                        new Object[] {pushRegistryTableName});
                ps = connection.prepareStatement(query);

                ps.setLong(1, accountId) ;

                rs = ps.executeQuery();

                while(rs.next()) {
                    accounts.add(0, createMailServerAccount(rs));
                }
            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return accounts;
    }

    /**
     *
     * get emails from the fnbl_email_inbox for given user the
     * array contains the emails order by received_date desc.
     *
     * @param username String
     * @param protocol String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public SyncItemInfoAdmin[] getCachedInfo(String username, String protocol)
    throws DBAccessException {

        SyncItemInfoAdmin[] out        = null;
        ArrayList           infos      = new ArrayList();
        SyncItemInfoAdmin   record     = null;
        Connection          connection = null;
        PreparedStatement   ps         = null;
        ResultSet           rs         = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.INBOX_SELECT_ALL);

            ps.setString(1, username ) ;
            ps.setString(2, protocol ) ;

            rs = ps.executeQuery();

            while(rs.next()) {

                record = new SyncItemInfoAdmin();

                record.setGuid(rs.getString(1)) ;
                record.setMessageID(rs.getString(2)) ;
                record.setHeaderDate(Utility.UtcToDate(rs.getString(3))) ;
                record.setHeaderReceived(Utility.UtcToDate(rs.getString(4))) ;
                record.setSubject(rs.getString(5)) ;

                infos.add(record);

            }

            int len = infos.size();
            out = new SyncItemInfoAdmin[len];
            for (int i=0; i<len; i++){
                out[i] = (SyncItemInfoAdmin)infos.get(i);
            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return out;
    }

    /**
     *
     * get user
     *
     * @param accountID long
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     * @deprecated since version 8; use getUserAccountFromID() instead
     */
    public MailServerAccount getUserFromID(long accountID) throws DBAccessException {
        return getUserAccountFromID(accountID);
    }

    /**
     *
     * get user
     *
     * @param accountID long
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getUserAccountFromID(long accountID) throws DBAccessException {

        MailServerAccount msa = null;
        MailServer pms        = null;
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

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return msa;
    }

    /**
     *
     * insert public mail server configuration
     *
     * @param pms PublicMailServer
     * @return int
     * @throws EntityException
     */
    public int insertPubMailServer(MailServer pms) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = coreDataSource.getConnection();

            ps = connection.prepareStatement(Query.MS_INSERT_MAILSERVER);

            ps.setString(1, pms.getMailServerId());
            ps.setString(2, pms.getMailServerType());
            ps.setString(3, pms.getDescription());
            ps.setString(4, pms.getProtocol());
            ps.setString(5, pms.getOutServer());
            ps.setInt   (6, pms.getOutPort());
            ps.setString(7, Utility.booleanToString(pms.getOutAuth()));
            ps.setString(8, pms.getInServer());
            ps.setInt   (9, pms.getInPort());
            ps.setString(10, Utility.booleanToString(pms.getIsSSLIn()));
            ps.setString(11, Utility.booleanToString(pms.getIsSSLOut()));

            ps.setString(12, pms.getInboxPath());
            ps.setString(13, Utility.booleanToString(pms.getInboxActivation()));
            ps.setString(14, pms.getOutboxPath());
            ps.setString(15, Utility.booleanToString(pms.getOutboxActivation()));
            ps.setString(16, pms.getSentPath());
            ps.setString(17, Utility.booleanToString(pms.getSentActivation()));
            ps.setString(18, pms.getDraftsPath());
            ps.setString(19, Utility.booleanToString(pms.getDraftsActivation()));
            ps.setString(20, pms.getTrashPath());
            ps.setString(21, Utility.booleanToString(pms.getTrashActivation()));

            ps.setString(22, Utility.booleanToString(pms.getIsSoftDelete()));

            ps.setString(23, Utility.booleanToString(true));

            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;
    }

    /**
     *
     * delete mail server
     *
     * @param mailServerId String
     * @return int
     * @throws EntityException
     */
    public int deletePubMailServer(String mailServerId) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        if (log.isTraceEnabled()) {
            log.trace("ConsoleDAO - deletePubMailServer " + mailServerId);
        }

        try {

            connection = coreDataSource.getConnection();

            ps = connection.prepareStatement(Query.MS_DELETE_MAILSERVER);

            ps.setString(1, mailServerId);

            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;
    }

    /**
     *
     * update public mail server
     *
     * @param pms PublicMailServer
     * @return int
     * @throws EntityException
     */
    public int updatePubMailServer(MailServer pms) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        int result            = 0;

        try {

            connection = coreDataSource.getConnection();

            // update the mail server
            ps = connection.prepareStatement(Query.MS_UPDATE_MAILSERVER);

            ps.setString(1, pms.getMailServerType());
            ps.setString(2, pms.getDescription());
            ps.setString(3, pms.getProtocol());
            ps.setString(4, pms.getOutServer());
            ps.setInt   (5, pms.getOutPort());
            ps.setString(6, Utility.booleanToString(pms.getOutAuth()));
            ps.setString(7, pms.getInServer());
            ps.setInt   (8, pms.getInPort());
            ps.setString(9, Utility.booleanToString(pms.getIsSSLIn()));
            ps.setString(10, Utility.booleanToString(pms.getIsSSLOut()));

            ps.setString(11, pms.getInboxPath());
            ps.setString(12, Utility.booleanToString(pms.getInboxActivation()));
            ps.setString(13, pms.getOutboxPath());
            ps.setString(14, Utility.booleanToString(pms.getOutboxActivation()));
            ps.setString(15, pms.getSentPath());
            ps.setString(16, Utility.booleanToString(pms.getSentActivation()));
            ps.setString(17, pms.getDraftsPath());
            ps.setString(18, Utility.booleanToString(pms.getDraftsActivation()));
            ps.setString(19, pms.getTrashPath());
            ps.setString(20, Utility.booleanToString(pms.getTrashActivation()));

            ps.setString(21, Utility.booleanToString(pms.getIsSoftDelete()));

            ps.setString(22, Utility.booleanToString(true));

            ps.setString(23, pms.getMailServerId());

            result = ps.executeUpdate();


            // update all users that use this mail server

            ps = connection.prepareStatement(Query.MS_ACCOUNT_UPDATE_MAILSERVER_4_USERS);

            ps.setString(1, pms.getMailServerType());
            ps.setString(2, pms.getDescription());
            ps.setString(3, pms.getProtocol());
            ps.setString(4, pms.getOutServer());
            ps.setInt   (5, pms.getOutPort());
            ps.setString(6, Utility.booleanToString(pms.getOutAuth()));
            ps.setString(7, pms.getInServer());
            ps.setInt   (8, pms.getInPort());
            ps.setString(9, Utility.booleanToString(pms.getIsSSLIn()));
            ps.setString(10, Utility.booleanToString(pms.getIsSSLOut()));
            ps.setString(11, Utility.booleanToString(pms.getIsSoftDelete()));
            ps.setString(12, Utility.booleanToString(true));

            ps.setString(13, pms.getMailServerId());

            result = ps.executeUpdate();

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return result;
    }

    /**
     *
     * get public mail servers
     *
     * @return list with all the mail server in the filter ArrayList
     * @throws EntityException
     */
    public MailServer[] getPubMailServers(Clause clause) throws DBAccessException {

        ArrayList msl          = new ArrayList();
        MailServer[] mslOut    = null;
        Connection connection  = null;
        PreparedStatement stmt = null;
        ResultSet rs           = null;
        MailServer pms   = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            String dbProductName = connection.getMetaData().getDatabaseProductName();
            SQLHelperClause sqlHelperClause = new SQLHelperClause(dbProductName);

            StringBuilder query = new StringBuilder(Query.MS_LIST_MAILSERVER);
            if (clause != null){
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    if (where.sql.length() > 0){
                        query.append(" where ").append(where.sql);
                    }
                }
                query.append(Query.ORDERBY_MAILSERVER_ID);

                stmt = connection.prepareStatement(query.toString());
                if ( (clause != null) && (! (clause instanceof AllClause))) {
                    for (int i=0; i<where.parameters.length; ++i) {
                        stmt.setObject(i+1, where.parameters[i]);
                    }
                }
            } else {
                stmt = connection.prepareStatement(query.toString());
            }

            rs = stmt.executeQuery();

            while(rs.next()) {

                pms = new MailServer();

                pms.setMailServerId(rs.getString(1));
                pms.setMailServerType(rs.getString(2));
                pms.setDescription(rs.getString(3));
                pms.setProtocol(rs.getString(4));
                pms.setIsPublic(true);
                pms.setOutServer(rs.getString(5));
                pms.setOutPort(rs.getInt(6));
                pms.setOutAuth(Utility.booleanFromString(rs.getString(7)));
                pms.setInServer(rs.getString(8));
                pms.setInPort(rs.getInt(9));
                pms.setIsSSLIn(Utility.booleanFromString(rs.getString(10)));
                pms.setIsSSLOut(Utility.booleanFromString(rs.getString(11)));

                pms.setInboxPath(rs.getString(12));
                pms.setInboxActivation(Utility.booleanFromString(rs.getString(13)));
                pms.setOutboxPath(rs.getString(14));
                pms.setOutboxActivation(Utility.booleanFromString(rs.getString(15)));
                pms.setSentPath(rs.getString(16));
                pms.setSentActivation(Utility.booleanFromString(rs.getString(17)));
                pms.setDraftsPath(rs.getString(18));
                pms.setDraftsActivation(Utility.booleanFromString(rs.getString(19)));
                pms.setTrashPath(rs.getString(20));
                pms.setTrashActivation(Utility.booleanFromString(rs.getString(21)));

                pms.setIsSoftDelete(Utility.booleanFromString(rs.getString(22)));
                pms.setIsPublic(Utility.booleanFromString(rs.getString(23)));

                msl.add(pms);

            }

            mslOut = (MailServer[])msl.toArray(new MailServer[0]) ;

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, stmt, rs);
        }

        return mslOut;
    }

    /**
     *
     * get public mail server
     *
     * @param mailServerId String
     * @return all the info about the public mail server MailServer
     * @throws EntityException
     */
    public MailServer getPubMailServer(String mailServerId)
    throws DBAccessException {

        MailServer pms        = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.MS_GET_MAILSERVER);

            ps.setString(1, mailServerId ) ;

            rs = ps.executeQuery();

            while(rs.next()) {

                pms = new MailServer();

                pms.setMailServerId(rs.getString(1));
                pms.setMailServerType(rs.getString(2));
                pms.setDescription(rs.getString(3));
                pms.setProtocol(rs.getString(4));
                pms.setOutServer(rs.getString(5));
                pms.setOutPort(rs.getInt(6));
                pms.setOutAuth(Utility.booleanFromString(rs.getString(7)));
                pms.setInServer(rs.getString(8));
                pms.setInPort(rs.getInt(9));
                pms.setIsSSLIn(Utility.booleanFromString(rs.getString(10)));
                pms.setIsSSLOut(Utility.booleanFromString(rs.getString(11)));
                pms.setInboxPath(rs.getString(12));
                pms.setInboxActivation(Utility.booleanFromString(rs.getString(13)));
                pms.setOutboxPath(rs.getString(14));
                pms.setOutboxActivation(Utility.booleanFromString(rs.getString(15)));
                pms.setSentPath(rs.getString(16));
                pms.setSentActivation(Utility.booleanFromString(rs.getString(17)));
                pms.setDraftsPath(rs.getString(18));
                pms.setDraftsActivation(Utility.booleanFromString(rs.getString(19)));
                pms.setTrashPath(rs.getString(20));
                pms.setTrashActivation(Utility.booleanFromString(rs.getString(21)));
                pms.setIsSoftDelete(Utility.booleanFromString(rs.getString(22)));
                pms.setIsPublic(Utility.booleanFromString(rs.getString(23)));

            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return pms;
    }

    /**
     *
     * check if there are related users to the specific Mail Server
     *
     * @param mailServerId String
     * @return number of user for the specific mail server
     * @throws EntityException
     */
    public int checkUsersForMailServer(String mailServerId) throws DBAccessException {
        int resultNum         = 0;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.MS_ACCOUNT_CHECK_USER);

            ps.setString(1, mailServerId) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                resultNum = rs.getInt(1);
            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return resultNum;
    }

    /**
     * Returns all the folders names for the given IMAP account.
     *
     * @param msa mail server account for which to retrieve folders
     * @return list of the folders names for this IMAP account
     */
    public List getImapFolderList(MailServerAccount msa) throws InboxListenerException {

        try {

            IMailServerWrapper mswf =
                    MailServerWrapperFactory.getMailServerWrapper(msa.getMailServer().getProtocol());

            MSTools mstools = new MSTools(msa);

            mstools.openConnection(mswf);

            ArrayList<String> folderNames = new ArrayList<String>();

            IMAPFolder folder = (IMAPFolder) mswf.getStore().getDefaultFolder();

            searchSubFolder(folder, folderNames);

            if (log.isTraceEnabled()) {

                log.trace("IMAP Folders in the Account " + msa.getUsername());
                for (String folderName : folderNames) {
                    log.trace("folder name: " + folderName);
                }
            }

            mstools.closeConnection(mswf);

            return folderNames;

        } catch (NoSuchProviderException e) {
            throw new InboxListenerException("Error opening Account - Check the protocol");
        } catch (MessagingException e) {
            throw new InboxListenerException("Error connecting Account - Check the user");
        }
    }

    /**
     * Get all the principals' ids for given username's account.
     * @param accountID account id
     * @return array of all the principals' ids
     */
    public long[] getPrincipals(long accountID) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        String username                = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            //
            // Gets the username for this account
            //
            ps = connection.prepareStatement(Query.MS_ACCOUNT_GET_USER_NAME);

            ps.setLong(1, accountID) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                username = rs.getString(1);
            }

            return getPrincipals(username);

        } catch (SQLException e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }
    }

    /**
     * Get all the principals' ids for given username's account.
     * @param username username
     * @return array of all the principals' ids
     */
    public long[] getPrincipals(String username) throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        List<Long> principalsLong = new ArrayList<Long>();

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            //
            // Gets all the principals for this username
            //
            ps = connection.prepareStatement(Query.GET_PRINCIPAL_IDS_BY_USERNAME);

            ps.setString(1, username) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                principalsLong.add(new Long(rs.getLong(1)));
            }

        } catch (SQLException e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        int numPrincipals   = principalsLong.size();
        long[] principalIds = new long[numPrincipals];
        for (int i = 0; i < numPrincipals; i++) {
            principalIds[i] = principalsLong.get(i);
        }

        return principalIds;
    }

    /**
     * Retrieves date/time of the last received email.
     *
     * @param username the username
     * @return the date/time of the last received email, 0 if no mail was  found.
     * @throws com.funambol.email.exception.DBAccessException
     */
    public long getTimeOfLastReceivedEmail(String username)
    throws DBAccessException {

        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        long lastReceivedEmailTime = 0;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.INBOX_GET_MAX_RECEIVED);

            ps.setString(1, username) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                lastReceivedEmailTime = Utility.UtcToLong(rs.getString(1));
            }

        } catch (SQLException e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }


        return lastReceivedEmailTime;
    }

    /**
     *
     * Retrieves the number of unread emails, checking the table fnbl_email_inbox,
     * filtering for all the values of the crc in which the read flag is not set.
     * Moreover, emails are ordered by received column and limited to the max number
     * of emails the user wants to have in his own inbox.
     * In order to do that, this method performs one more query to retrieve the
     * email account associated to the given username.
     *
     * @param username is the username
     *
     * @return the number of unread emails, 0 if no account or email was found.
     *
     * @throws com.funambol.email.exception.DBAccessException if any error occurs
     * accessing database.
     */
     public int getNumUnreadEmail(String username) throws DBAccessException {
         // Retrieving accounts associated to the user
         List<MailServerAccount> accounts = getUserAccounts(username);


         if(accounts==null || accounts.size()==0)
             return 0;

         return getNumUnreadEmail(accounts.get(0));
     }

    /**
     *
     * Retrieves the number of unread emails, checking the table fnbl_email_inbox,
     * filtering for all the values of the crc in which the read flag is not set.
     * Moreover, emails are ordered by received column and limited to the max number
     * of emails the user wants to have in his own inbox.
     *
     * @param account a not null account containing info about the user we are interested in.
     *
     * @return the number of unread emails, 0 if no mail was found.
     *
     * @throws com.funambol.email.exception.DBAccessException if something goes
     * wrong accessing the database.
     *
     */

    public int getNumUnreadEmail(MailServerAccount account) throws DBAccessException {

        if(account==null)
            throw new IllegalArgumentException("It's impossible to retrieve the number of unread emails providing a null account.");

        String username          = account.getUsername();
        int    maxNumberOfEmails = account.getMaxEmailNumber();
        
        if(log.isTraceEnabled()) {
            log.trace("Retrieving the number of unread emails for the user ["
                         +username+"] with limit ["+maxNumberOfEmails+"].");
        }

        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        int numberUnreadEmails = 0;

        try {

            connection = userDataSource.getRoutedConnection(username);

            connection.setReadOnly(true);


                                                     

            ps = connection.prepareStatement(Query.INBOX_GET_NUM_UNREAD_EMAIL);

            ps.setString(1, username) ;
            ps.setInt(2, maxNumberOfEmails);

            rs = ps.executeQuery();

            while(rs.next()) {
                numberUnreadEmails = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return numberUnreadEmails;
    }

    //---------------------------------------------------------- Private Methods

    /**
     *
     * get userid
     *
     * @param username ds-server user String
     * @return serId
     * @throws EntityException
     */
    private long getAccountId(String username)  throws DBAccessException {

        long userId = -1;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.MS_ACCOUNT_GET_USER_ID);

            ps.setString(1, username ) ;

            rs = ps.executeQuery();

            while(rs.next()) {
                userId = (rs.getLong(1));
            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return userId;
    }


    /**
     *
     * get userid
     *
     * @param username ds-server user String
     * @return serId
     * @throws EntityException
     */
    private String getUsername(long accountID) throws DBAccessException {
        String username = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = coreDataSource.getConnection();
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.MS_ACCOUNT_GET_USER_NAME);

            ps.setLong(1, accountID);

            rs = ps.executeQuery();

            while(rs.next()) {
                username = (rs.getString(1));
            }

        } catch (Exception e) {
            throw new DBAccessException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return username;
    }

    /**
     * TODO
     * @param folder IMAPFolder
     * @throws MessagingException
     */
    private static void searchSubFolder(IMAPFolder folder, ArrayList fs)
    throws MessagingException {

        Folder[] folderList = folder.list();

        IMAPFolder f;
        String s = "";
        for (int i = 0; i < folderList.length; i++) {
            f = ((IMAPFolder) folderList[i]);
            s = f.getFullName(); //s = "folder name: " + f.getName() + " - full name: " + f.getFullName();
            fs.add(s);
            searchSubFolder(f, fs);
        }

    }

    /**
     * Fills a <code>MailServerAccount</code> object with the data in a given
     * resultset.
     * <p/>
     * Resultset must contains 1 row and all the data required.
     * 
     * @param rs the resultset from which to extract data
     * @throws java.sql.SQLException 
     * @throws java.lang.Exception 
     * @return the mail server account with data filled
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
        String encryptedPassword = rs.getString(9); 
        String plain_text = null;
        try {                                                           
            plain_text = EncryptionTool.decrypt(encryptedPassword);                                     
        } catch (Exception e){                                          
            throw new Exception("Error handling password decryption");  
        }                                                               
        String encryptedPasswordOut = rs.getString(39); 
        String plainPasswordOut = null;
        if (encryptedPasswordOut != null) {
            try {
                plainPasswordOut = EncryptionTool.decrypt(encryptedPasswordOut);
            } catch (Exception e) {
                throw new Exception("Error handling out password decryption");
            }
        }
        msa.setMsPassword(plain_text);
        msa.setMsAddress(rs.getString(10));
        msa.setPush(Utility.booleanFromString(rs.getString(11)));
        msa.setMaxEmailNumber(rs.getInt(12));
        msa.setMaxImapEmail(rs.getInt(13));
        msa.setOutLogin(rs.getString(38));
        msa.setOutPassword(plainPasswordOut);

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
    

}