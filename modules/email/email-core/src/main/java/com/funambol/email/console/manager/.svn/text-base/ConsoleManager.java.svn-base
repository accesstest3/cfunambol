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
package com.funambol.email.console.manager;


import com.funambol.email.transport.MailServerError;
import com.funambol.email.console.dao.ConsoleDAO;
import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.DBAccessException;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EmailConfigException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.InboxListenerConfigException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfoAdmin;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.MailServerErrorHandler;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;

import java.util.List;

import javax.naming.NamingException;

/**
 *
 * @version $Id: ConsoleManager.java,v 1.3 2008-05-18 15:10:09 nichele Exp $
 */
public class ConsoleManager {

    // ------------------------------------------------------------ Constants
    private static final String CORE_DB_JNDI_NAME = "jdbc/fnblcore";

    // ------------------------------------------------------------ Private data

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**     */
    protected static DBIDGenerator idMailServerSpace = null;

    /**     */
    protected static DBIDGenerator idUserSpace = null;

    /**     */
    protected ConsoleDAO cdao = null;
    
    public ConsoleDAO getConsoleDAO(){
        return this.cdao;
    }

    /**     */
    private String jndiDataSourceName = null;
    
    /** */
    private String sslSocketFactory = null;


    // -------------------------------------------------------------- Properties


    /**
     *
     */
    public ConsoleManager(){

        try {
            this.jndiDataSourceName = EmailConnectorConfig.getConfigInstance().getDataSource();

            boolean checkCertificates = EmailConnectorConfig.getConfigInstance().getCheckCertificates();
            if (checkCertificates){
                this.sslSocketFactory = Def.SSL_SOCKET_FACTORY_STANDARD;
            } else {
                this.sslSocketFactory = Def.SSL_SOCKET_FACTORY_FUNAMBOL;
            }

            idMailServerSpace = DBIDGeneratorFactory.getDBIDGenerator(
                    Def.ID_COUNTER_MAILSERVER,
                    DataSourceTools.lookupDataSource(CORE_DB_JNDI_NAME));

            cdao = new ConsoleDAO();

        } catch (EmailConfigException e) {
            log.error("Error Getting Connector Parameters ", e);
        } catch (NamingException e) {
            log.error("Error Getting ID Generator ", e);
        } catch (InboxListenerConfigException e) {
            log.error("Error creating DAO layer ", e);
        } catch (Exception e) {
            log.error("Error Getting ID Generator ", e);
        }

    }


    // -----------------------------------------------------------Public Methods


    /**
     *
     * insert user and the mail server configuration
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     */
    public int insertUser(MailServerAccount msa)
    throws EntityException {
        int result = 0;
        try {

            if (msa.getMailServer().getMailServerId() == null ||
                    msa.getMailServer().getMailServerId().equals("") ){

                msa.getMailServer().setMailServerId(""+this.idMailServerSpace.next());

            } else {

                String msId = msa.getMailServer().getMailServerId();

                if (msa.getMailServer().getIsPublic()) {

                    MailServer msFromDB = cdao.getPubMailServer(msId);

                    MailServer ms = msa.getMailServer();

                    // replace the imput info with the db info
                    msa.getMailServer().setMailServerId(msId);
                    msa.getMailServer().setMailServerType(msFromDB.getMailServerType());
                    msa.getMailServer().setDescription(msFromDB.getDescription());
                    msa.getMailServer().setIsSoftDelete(msFromDB.getIsSoftDelete());
                    msa.getMailServer().setIsPublic(msFromDB.getIsPublic());
                    msa.getMailServer().setProtocol(msFromDB.getProtocol());
                    msa.getMailServer().setOutServer(msFromDB.getOutServer());
                    msa.getMailServer().setOutPort(msFromDB.getOutPort());
                    msa.getMailServer().setOutAuth(msFromDB.getOutAuth());
                    msa.getMailServer().setInServer(msFromDB.getInServer());
                    msa.getMailServer().setInPort(msFromDB.getInPort());
                    msa.getMailServer().setIsSSLIn(msFromDB.getIsSSLIn());
                    msa.getMailServer().setIsSSLOut(msFromDB.getIsSSLOut());

                    msa.getMailServer().setInboxPath(
                            getValueOrDefault(ms.getInboxPath(), msFromDB.getInboxPath()));

                    msa.getMailServer().setInboxActivation(msFromDB.getInboxActivation());

                    msa.getMailServer().setOutboxPath(
                            getValueOrDefault(ms.getOutboxPath(), msFromDB.getOutboxPath()));

                    msa.getMailServer().setOutboxActivation(msFromDB.getOutboxActivation());

                    msa.getMailServer().setSentPath(
                            getValueOrDefault(ms.getSentPath(), msFromDB.getSentPath()));

                    msa.getMailServer().setSentActivation(msFromDB.getSentActivation());

                    msa.getMailServer().setDraftsPath(
                            getValueOrDefault(ms.getDraftsPath(), msFromDB.getDraftsPath()));

                    msa.getMailServer().setDraftsActivation(msFromDB.getDraftsActivation());

                    msa.getMailServer().setTrashPath(
                            getValueOrDefault(ms.getTrashPath(), msFromDB.getTrashPath()));

                    msa.getMailServer().setTrashActivation(msFromDB.getTrashActivation());

                }

            }

            // from UI we get the minutes but in the DB we save the seconds
            msa.setPeriod(Utility.setMillis(msa.getPeriod()));

            // check serverType
            checkMailServerInfo(msa);

            // check folder names
            checkFoldersName(msa);

            // set the push listener framework properties
            msa.setTaskBeanFile(Def.DEFAULT_INBOX_LISTENER_BEAN_FILE);
            msa.setStatus(RegistryEntryStatus.NEW);
            msa.setLastUpdate(System.currentTimeMillis());

            // insert the account
            result = cdao.insertUser(msa);

        } catch (DBIDGeneratorException ee){
            throw new EntityException("Error handling ID ", ee);
        } catch (EntityException ee){
            throw new EntityException("Error addind Account", ee);
        } catch (Exception ee){
            throw new EntityException("Error checking input values of the Account", ee);
        }

        return result;
    }

    /**
     * the UI cannot delete the entry. it will be marked as deleted.
     * the push listener framework will remove the entry.
     *
     * @param accountID String
     * @return int
     * @throws EntityException
     */
    public int markUserAsDelete(long accountID) throws EntityException {
        int result = 0;
        try {
            MailServerAccount msa = cdao.getUserFromID(accountID);
            if (msa == null) {
                return 0;
            }
            msa.setStatus(RegistryEntryStatus.DELETED);
            msa.setLastUpdate(System.currentTimeMillis());
            result = cdao.markUserAsDelete(msa);
        } catch (DBAccessException ee){
            throw new EntityException("Error marking account as delete.", ee);
        }
        return result;
    }


    /**
     * Deletes the email account with the given id.
     * @param accountID acoount id
     * @throws com.funambol.email.exception.EntityException
     * @return number of deleted rows
     */
    public int deleteAccount(long accountID) throws EntityException {
        int numDeleted = 0;
        try {
            long[] principalIds = cdao.getPrincipals(accountID);
            numDeleted = cdao.deleteUser(accountID, principalIds);
        } catch (DBAccessException e){
            throw new EntityException("Error deleting account with id " + accountID, e);
        }
        return numDeleted;
    }

    /**
     *
     * disable account
     *
     * @param accountID String
     * @return int
     * @throws EntityException
     */
    public int disableUser(long accountID) throws EntityException {
        int result = 0;
        try {
            result = cdao.disableUser(accountID);
        } catch (DBAccessException ee){
            throw new EntityException("Error disabling account", ee);
        }
        return result;
    }

    /**
     *
     * enable account
     *
     * @param accountID String
     * @return int
     * @throws EntityException
     */
    public int enableUser(long accountID) throws EntityException {
        int result = 0;
        try {
            result = cdao.enableUser(accountID);
        } catch (DBAccessException ee){
            throw new EntityException("Error enabling account", ee);
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
     */
    public int updateUser(MailServerAccount msa) throws EntityException {

        int               result           = 0;
        boolean           needCacheRefresh = false;

        try {

            if (msa.getMailServer().getMailServerId() == null ||
                    msa.getMailServer().getMailServerId().equals("") ){

                // this case happens when there is a swith from public
                // to custom in the UI so the cache refresh is required
                needCacheRefresh = true;
                msa.getMailServer().setMailServerId(""+this.idMailServerSpace.next());

            } else {

                //  check if a refresh is required
                needCacheRefresh = needCacheRefreshing(msa);

                //
                // update custom or update public
                // - if custom:
                // leave the input values that the UI sends
                // - if public;
                // get info from DB, replace the input from UI
                // except the folder names settings
                //
                if (msa.getMailServer().getIsPublic()) {

                    String msId = msa.getMailServer().getMailServerId();
                    MailServer msFromDB = cdao.getPubMailServer(msId);
                    msa.getMailServer().setMailServerId(msId);
                    msa.getMailServer().setMailServerType(msFromDB.getMailServerType());
                    msa.getMailServer().setDescription(msFromDB.getDescription());
                    msa.getMailServer().setIsSoftDelete(msFromDB.getIsSoftDelete());
                    msa.getMailServer().setIsPublic(msFromDB.getIsPublic());
                    msa.getMailServer().setProtocol(msFromDB.getProtocol());
                    msa.getMailServer().setOutServer(msFromDB.getOutServer());
                    msa.getMailServer().setOutPort(msFromDB.getOutPort());
                    msa.getMailServer().setOutAuth(msFromDB.getOutAuth());
                    msa.getMailServer().setInServer(msFromDB.getInServer());
                    msa.getMailServer().setInPort(msFromDB.getInPort());
                    msa.getMailServer().setIsSSLIn(msFromDB.getIsSSLIn());
                    msa.getMailServer().setIsSSLOut(msFromDB.getIsSSLOut());

                }

            }

            // set the period (push listener framework)
            msa.setPeriod(Utility.setMillis(msa.getPeriod()));

            // check serverType
            checkMailServerInfo(msa);

            // check folder names
            checkFoldersName(msa);

            msa.setTaskBeanFile(Def.DEFAULT_INBOX_LISTENER_BEAN_FILE);
            msa.setStatus(RegistryEntryStatus.UPDATED);
            msa.setLastUpdate(System.currentTimeMillis());

            // update user
            result = cdao.updateUser(msa);

            // refresh cache if it's required
            if(needCacheRefresh){
                cdao.clearCache(msa.getUsername());
                // get the principal
                long[] principals = cdao.getPrincipals(msa.getUsername());
                for (long principalID : principals){
                    cdao.clearFolder(msa.getUsername(), principalID);
                }
            }

        } catch (DBIDGeneratorException ee){
            throw new EntityException("Error handling ID ", ee);
        } catch (EntityException ee){
            throw new EntityException("Error updating Account", ee);
        } catch (Exception ee){
            throw new EntityException("Error checking the folders name of the Account", ee);
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
     */
    public MailServerAccount[] getUsers(Clause clause) throws EntityException {

        MailServerAccount[] msal = null;
        try {
            msal = cdao.getUsers(clause);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Account List", ee);
        }
        return msal;

    }

    /**
     *
     * get user
     *
     * @param username String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getUser(String username) throws EntityException {
        MailServerAccount msa = null;
        try {
            msa = cdao.getUser(username);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Account ", ee);
        }
        return msa;
    }

    /**
     *
     * get user
     *
     * @param accountID String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws EntityException
     */
    public MailServerAccount getUserFromID(long accountID)
    throws EntityException {
        MailServerAccount msa = null;
        try {
            msa = cdao.getUserFromID(accountID);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Account ", ee);
        }
        return msa;
    }


    /**
     *
     * insert public mail server configuration
     *
     * @param ms PublicMailServer
     * @return int
     * @throws EntityException
     */
    public int insertPubMailServer(MailServer ms) throws EntityException {

        int result = 0;

        try {

            ms.setMailServerId(""+this.idMailServerSpace.next());

            // check serverType
            if (ms.getMailServerType() == null ||
                    ms.getMailServerType().equals("") ||
                    ms.getMailServerType().equals("null")){
                ms.setMailServerType(Def.SERVER_TYPE_OTHER);
            }

            // check description
            if (ms.getDescription() == null ||
                    ms.getDescription().equals("") ||
                    ms.getDescription().equals("null")){
                ms.setDescription(Def.SERVER_TYPE_OTHER);
            }


            // check folder names
            if (ms.getOutboxPath() == null ||
                    ms.getOutboxPath().equals("") ||
                    ms.getOutboxPath().equals("null")){
                ms.setOutboxPath(Def.FOLDER_OUTBOX_ENG);
                ms.setOutboxActivation(true);
            }

            if (ms.getSentPath() == null ||
                    ms.getSentPath().equals("") ||
                    ms.getSentPath().equals("null")){
                if (ms.getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                    ms.setSentPath(Def.FOLDER_SENT_ENG_EXC);
                } else {
                    ms.setSentPath(Def.FOLDER_SENT_ENG);
                }
            }

            if (ms.getDraftsPath() == null ||
                    ms.getDraftsPath().equals("") ||
                    ms.getDraftsPath().equals("null")){
                ms.setDraftsPath(Def.FOLDER_DRAFTS_ENG);
            }

            if (ms.getTrashPath() == null ||
                    ms.getTrashPath().equals("") ||
                    ms.getTrashPath().equals("null")){
                if (ms.getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                    ms.setTrashPath(Def.FOLDER_TRASH_ENG_EXC);
                } else {
                    ms.setTrashPath(Def.FOLDER_TRASH_ENG);
                }
            }

            result = cdao.insertPubMailServer(ms);

        } catch (DBIDGeneratorException ee){
            throw new EntityException("Error handling ID ", ee);
        } catch (DBAccessException ee){
            throw new EntityException("Error inserting Mail Server ", ee);
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
    public int deletePubMailServer(String mailServerId)
    throws EntityException {
        int result = 0;
        try {
            if (noUsers(mailServerId)){
                result = cdao.deletePubMailServer(mailServerId);
            } else {
                // There are some Users related to this Mail Server
                result = Def.ERR_SERVER_DELETION;
            }
        } catch (DBAccessException ee){
            throw new EntityException("Error deleting MailServer ", ee);
        }
        return result;
    }

    /**
     *
     * update public mail server
     *
     * @param ms PublicMailServer
     * @return int
     * @throws EntityException
     */
    public int updatePubMailServer(MailServer ms) throws EntityException {

        int result = 0;

        try {

            if (ms.getMailServerId() == null || ms.getMailServerId().equals("") ){

                throw new EntityException("Error: Mail Server ID is null in an update operation");

            } else {

                // check serverType
                if (ms.getMailServerType() == null ||
                        ms.getMailServerType().equals("") ||
                        ms.getMailServerType().equals("null")){
                    ms.setMailServerType(Def.SERVER_TYPE_OTHER);
                }

                // check description
                if (ms.getDescription() == null ||
                        ms.getDescription().equals("") ||
                        ms.getDescription().equals("null")){
                    ms.setDescription(Def.SERVER_TYPE_OTHER);
                }

                // check folder names
                if (ms.getOutboxPath() == null ||
                        ms.getOutboxPath().equals("") ||
                        ms.getOutboxPath().equals("null")){
                    ms.setOutboxPath(Def.FOLDER_OUTBOX_ENG);
                    ms.setOutboxActivation(true);
                }
                if (ms.getSentPath() == null ||
                        ms.getSentPath().equals("") ||
                        ms.getSentPath().equals("null")){
                    if (ms.getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                        ms.setSentPath(Def.FOLDER_SENT_ENG_EXC);
                    } else {
                        ms.setSentPath(Def.FOLDER_SENT_ENG);
                    }
                }

                if (ms.getDraftsPath() == null ||
                        ms.getDraftsPath().equals("") ||
                        ms.getDraftsPath().equals("null")){
                    ms.setDraftsPath(Def.FOLDER_DRAFTS_ENG);
                }

                if (ms.getTrashPath() == null ||
                        ms.getTrashPath().equals("") ||
                        ms.getTrashPath().equals("null")){
                    if (ms.getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                        ms.setTrashPath(Def.FOLDER_TRASH_ENG_EXC);
                    } else {
                        ms.setTrashPath(Def.FOLDER_TRASH_ENG);
                    }
                }

            }

            result = cdao.updatePubMailServer(ms);

        } catch (DBAccessException ee){
            throw new EntityException("Error updating Mail Server ", ee);
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
    public MailServer[] getPubMailServers(Clause clause) throws EntityException {
        MailServer[] msl = null;
        try {
            msl = cdao.getPubMailServers(clause);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Mail Server List", ee);
        }
        return msl;
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
    throws EntityException {
        MailServer pms        = null;
        try {
            pms = cdao.getPubMailServer(mailServerId);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Mail Server ", ee);
        }
        return pms;
    }

    /**
     *
     * get the cached info for the specified username
     *
     * @param username String
     * @param protocol String
     * @return array qith all the info
     * @throws EntityException
     */
    public SyncItemInfoAdmin[] getCachedInfo(String username, String protocol)
    throws EntityException {
        SyncItemInfoAdmin[] infos = null;
        try {
            infos = cdao.getCachedInfo(username, protocol);
        } catch (DBAccessException ee){
            throw new EntityException("Error getting Cache Info for Administration", ee);
        }
        return infos;

    }


    /**
     * 
     * check the account on the mail server
     * ok                           = 0
     * invalid protocol             = 1
     * invalid username or password = 2
     * cannection failed            = 3
     *
     * @param msa MailServerAccount
     * @param portalCheckTimeout timeout for each attempt
     * @return true if the user is a mail server account boolean
     * @throws EntityException
     */
    public MailServerError guessAccount(MailServerAccount msa, 
                                        int portalCheckTimeout)
    throws EntityException {
        
        MailServerError errorCode = guessAccount(msa, 
                                                 portalCheckTimeout, 
                                                 portalCheckTimeout);        
        return errorCode;
        
    }


    /**
     * 
     * check the account on the mail server
     * ok                           = 0
     * invalid protocol             = 1
     * invalid username or password = 2
     * cannection failed            = 3
     *
     * @param msa MailServerAccount
     * @param timeoutStore timeout for the store
     * @param timeoutConnection timeout for connection
     * @return true if the user is a mail server account boolean
     * @throws com.funambol.email.exception.EntityException
     */
    public MailServerError guessAccount(MailServerAccount msa, 
                                        int timeoutStore, 
                                        int timeoutConnection)
    throws EntityException {

        MailServerError errorCode = MailServerError.ERR_OK;

        try {

            if (msa != null){

                String  login           = msa.getMsLogin();
                String  password        = msa.getMsPassword();
                String  protocol        = msa.getMailServer().getProtocol();
                String  outgoingServer  = msa.getMailServer().getOutServer();
                int     outgoingPort    = msa.getMailServer().getOutPort();
                String  incomingServer  = msa.getMailServer().getInServer();
                int     incomingPort    = msa.getMailServer().getInPort();
                boolean isSSLIn         = msa.getMailServer().getIsSSLIn();

                IMailServerWrapper mswf =
                        MailServerWrapperFactory.getMailServerWrapper(protocol);
                
                if (mswf != null){
                    mswf.checkUser(
                            outgoingServer,
                            Integer.toString(outgoingPort),
                            incomingServer,
                            Integer.toString(incomingPort),
                            login,
                            password,
                            isSSLIn,
                            String.valueOf(timeoutStore),
                            String.valueOf(timeoutConnection),
                            sslSocketFactory);
                } else {
                    errorCode = MailServerError.ERR_INVALID_PROTOCOL;
                }

            } else {
                errorCode = MailServerError.ERR_INVALID_INPUT;
            }

        } catch (EmailAccessException dae) {
            if (dae != null){
                errorCode = MailServerErrorHandler.exceptionHandler(dae);
            } else {
                errorCode = MailServerError.ERR_GENERIC_MAILSERVER;
            }
        }

        return errorCode;
    }
    
    /**
     * Returns all the folders names for the given IMAP account.
     *
     * @param msa mail server account
     * @return list of the folders names for this account
     */
    public List getImapFolders(MailServerAccount msa)
    throws EntityException, InboxListenerException{

        List folders = null;
        try {
            folders = cdao.getImapFolderList(msa);
        } catch (Exception e) {
            throw new EntityException("Error getting IMAP folders", e);
        }
        return folders;
    }

    /**
     * Retrieves date/time of the last received email.
     *
     * @param username the username
     * @return the date/time of the last received email
     * @throws EntityException if an error occurs
     */
    public long getTimeOfLastReceivedEmail(String username)
    throws EntityException {

        long lastReceivedEmailTime = 0;

        try {
            lastReceivedEmailTime = cdao.getTimeOfLastReceivedEmail(username);
        } catch (DBAccessException e){
            throw new EntityException("Error getting date/time of the last received email.", e);
        }
        return lastReceivedEmailTime;
    }

   /**
    *
    * Retrieves the number of the unread emails.
    *
    * @param username is the username
    *
    * @return the number of unread emails.
    *
    * @throws com.funambol.email.exception.EntityException if any error occurs.
    */

    public int getNumUnreadEmail(String username) throws EntityException {
        int numberUnreadEmails = 0;

        try {
            numberUnreadEmails = cdao.getNumUnreadEmail(username);
        } catch (DBAccessException e){
            throw new EntityException("Error getting the number of unread emails.", e);
        }
        return numberUnreadEmails;

    }

    /**
    *
    * Retrieves the number of the unread emails for the given account.
    *
    * @param account is the account associated to the user we are interested in.
    *
    * @return the number of unread emails.
    *
    * @throws com.funambol.email.exception.EntityException if any error occurs.
    */

    public int getNumUnreadEmail(MailServerAccount account) throws EntityException {
        int numberUnreadEmails = 0;

        try {
            numberUnreadEmails = cdao.getNumUnreadEmail(account);
        } catch (DBAccessException e){
            throw new EntityException("Error getting the number of unread emails.", e);
        }
        return numberUnreadEmails;

    }

    //---------------------------------------------------------- Private Methods


    /**
     *
     */
    private boolean noUsers(String mailServerId) throws EntityException {
        boolean check = false;
        int userNum   = 0;
        try {
            userNum = cdao.checkUsersForMailServer(mailServerId);
            if (userNum == 0){
                check = true;
            }
        } catch (DBAccessException ee){
            throw new EntityException("Error checking Users for the Mail Server ", ee);
        }
        return check;
    }

    /**
     * check if the fnbl_email_inbox must be refreshed for the
     * given user
     *
     * @param currMsa
     * @throws com.funambol.email.exception.EntityException
     * @return true if the table must be refreshed
     */
    private boolean needCacheRefreshing(MailServerAccount currMsa)
    throws EntityException {

        boolean check = false;

        MailServerAccount prevMsa          = null;
        String            prevMailServerID = null;
        String            prevProtocol     = null;
        String            currMailServerID = null;
        String            currProtocol     = null;
        try {

            // get all info from DB
            prevMsa = cdao.getUserFromID(currMsa.getId());

            prevMailServerID = prevMsa.getMailServer().getMailServerId();
            prevProtocol     = prevMsa.getMailServer().getProtocol();

            currMailServerID = currMsa.getMailServer().getMailServerId();
            currProtocol     = currMsa.getMailServer().getProtocol();

            if (currMailServerID != null && prevMailServerID != null){
                if (!currMailServerID.equals(prevMailServerID)){
                    check = true;
                }
            }

            // same mail server but differt protocol
            if (currProtocol != null && prevProtocol != null){
                if (!currProtocol.equals(prevProtocol)){
                    check = true;
                }
            }

            // different login or mail address
            String currMsLogin = currMsa.getMsLogin();
            String prevMsLogin = prevMsa.getMsLogin();

            if (currMsLogin != null && prevMsLogin != null) {
                if (!currMsLogin.equals(prevMsLogin)) {
                    check = true;
                }
            }

            String currMsAddress = currMsa.getMsAddress();
            String prevMsAddress = prevMsa.getMsAddress();

            if (currMsAddress != null && prevMsAddress != null) {
                if (!currMsAddress.equals(prevMsAddress)) {
                    check = true;
                }
            }

        } catch (DBAccessException ee){
            throw new EntityException("Error checking if the cache for the " +
                    "account " + currMsa.getId() + " must be freshed", ee);
        }
        return check;
    }


    /**
     * check if the input mail server account has the correct
     * folder name values
     *
     * @param msa Mail server account
     */
    private void checkFoldersName(MailServerAccount msa) throws Exception {
        // check inbox
        if (msa.getMailServer().getInboxPath() == null ||
                msa.getMailServer().getInboxPath().equals("") ||
                msa.getMailServer().getInboxPath().equals("null")){
            msa.getMailServer().setInboxPath(Def.FOLDER_INBOX_ENG);
            msa.getMailServer().setInboxActivation(true);
        }
        // outbox
        if (msa.getMailServer().getOutboxPath() == null ||
                msa.getMailServer().getOutboxPath().equals("") ||
                msa.getMailServer().getOutboxPath().equals("null")){
            msa.getMailServer().setOutboxPath(Def.FOLDER_OUTBOX_ENG);
            msa.getMailServer().setOutboxActivation(true);
        }
        // sent
        if (msa.getMailServer().getSentPath() == null ||
                msa.getMailServer().getSentPath().equals("") ||
                msa.getMailServer().getSentPath().equals("null")){
            if (msa.getMailServer().getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                msa.getMailServer().setSentPath(Def.FOLDER_SENT_ENG_EXC);
            } else {
                msa.getMailServer().setSentPath(Def.FOLDER_SENT_ENG);
            }
            // not enabled by default
        }
        // drafts
        if (msa.getMailServer().getDraftsPath() == null ||
                msa.getMailServer().getDraftsPath().equals("") ||
                msa.getMailServer().getDraftsPath().equals("null")){
            msa.getMailServer().setDraftsPath(Def.FOLDER_DRAFTS_ENG);
            // not enabled by default
        }
        // trash
        if (msa.getMailServer().getTrashPath() == null ||
                msa.getMailServer().getTrashPath().equals("") ||
                msa.getMailServer().getTrashPath().equals("null")){
            if (msa.getMailServer().getMailServerType().equals(Def.SERVER_TYPE_EXCHANGE)){
                msa.getMailServer().setTrashPath(Def.FOLDER_TRASH_ENG_EXC);
            } else {
                msa.getMailServer().setTrashPath(Def.FOLDER_TRASH_ENG);
            }
            // not enabled by default
        }

    }

    /**
     * check if the input mail server account has the correct
     * mail server information (type and description)
     *
     * @param msa Mail server account
     */
    private void checkMailServerInfo(MailServerAccount msa) throws Exception {
        // check serverType
        if (msa.getMailServer().getMailServerType() == null ||
                msa.getMailServer().getMailServerType().equals("") ||
                msa.getMailServer().getMailServerType().equals("null")){
            msa.getMailServer().setMailServerType(Def.SERVER_TYPE_OTHER);
        }
        // check description
        if (msa.getMailServer().getDescription() == null ||
                msa.getMailServer().getDescription().equals("") ||
                msa.getMailServer().getDescription().equals("null")){
            msa.getMailServer().setDescription(Def.SERVER_TYPE_OTHER);
        }
    }

    /**
     * If <code>value</code> is equals to <code>defaultValue</code> then the
     * default value is returned, otherwise the passed value.
     *
     * @param value value to match against <code>defaultValue</code>
     * @param defaultValue
     * @return <code>value</code> if <code>defaultValue.equals(value)</code> is
     * true, <code>defaultValue</code> otherwise.
     */
    private String getValueOrDefault(String value, String defaultValue) {
        if (defaultValue.equals(value)) {
            return defaultValue;
        } else {
            return value;
        }
    }


}
