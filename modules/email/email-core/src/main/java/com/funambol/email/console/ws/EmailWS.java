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
package com.funambol.email.console.ws;


import com.funambol.email.console.manager.ConsoleManager;
import com.funambol.email.transport.MailServerError;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfoAdmin;
import com.funambol.email.util.Def;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.server.error.ServerException;
import com.funambol.framework.tools.beans.BeanFactory;
import java.util.List;

/**
 *
 * @version $Id: EmailWS.java,v 1.2 2008-04-03 11:13:59 gbmiglia Exp $
 */
public class EmailWS {


    // ------------------------------------------------------------ Private data

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**     */
    protected ConsoleManager manager = null;

    // -------------------------------------------------------------- Properties


    /**
     *
     */
    public EmailWS(){
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - Constructor ");
        }
        manager = new ConsoleManager();
    }


    // -----------------------------------------------------------Public Methods



    /**
     *
     * get users in the filter
     *
     * @param clauses String
     * @return list with all the user in the filter ArrayList
     * @throws EntityException
     */
    public MailServerAccount[] getUsers(String clauses)
    throws ServerException, EntityException {

        MailServerAccount[] out = null;
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getUsers ");
        }
        try {
            Clause clause = (Clause)BeanFactory.unmarshal(clauses);
            out = manager.getUsers(clause);
            if (out != null){
                if (log.isTraceEnabled()) {
                    log.trace("EmailWS - getUsers items: " + out.length);
                }
            }
            return out;
        } catch (EntityException e) {
            log.error("Server error (getUsers) : ", e);
            throw e;
        }  catch (Exception e) {
            log.error("Server error (getUsers) : ", e);
            throw new ServerException(e.getMessage(), e);
        }
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
    throws ServerException, EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getCachedInfo ");
        }
        SyncItemInfoAdmin[] out = null;
        try {
            out = manager.getCachedInfo(username, protocol);
            if (out != null){
                if (log.isTraceEnabled()) {
                    log.trace("EmailWS - getCachedInfo items: " + out.length);
                }
            }
            return out;
        } catch (EntityException e) {
            log.error("Server error (getCachedInfos) : ", e);
            throw e;
        }  catch (Exception e) {
            log.error("Server error (getCachedInfos) : ", e);
            throw new ServerException(e.getMessage(), e);
        }

    }

    /**
     *
     * get public mail servers
     *
     * @return list with all the mail server in the filter ArrayList
     * @throws EntityException
     */
    public MailServer[] getPubMailServers(String clauses)
    throws ServerException, EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getPubMailServers ");
        }
        MailServer[] out = null;
        try {
            Clause clause = (Clause)BeanFactory.unmarshal(clauses);
            out = manager.getPubMailServers(clause);
            if (out != null){
                if (log.isTraceEnabled()) {
                    log.trace("EmailWS - getPubMailServers items: " + out.length);
                }
            }
            return out;
        } catch (EntityException e) {
            log.error("Server error (getPubMailServers)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (getPubMailServers)", e);
            throw new ServerException(e.getMessage(), e);
        }

    }


    /**
     *
     * insert user and the mail server configuration
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     */
    public int insertUser(MailServerAccount msa)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - insertUser ");
        }
        try {
            int result = manager.insertUser(msa);
            return result;
        } catch (EntityException e) {
            log.error("Server error (insertUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (insertUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     *
     * insert public mail server configuration
     *
     * @param pms MailServer
     * @return int
     * @throws ServerException, EntityException
     */
    public int insertPubMailServer(MailServer pms)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - insertPubMailServer ");
        }
        try {
            return manager.insertPubMailServer(pms);
        } catch (EntityException e) {
            log.error("Server error (insertPubMailServer)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (insertPubMailServer)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * update public mail server
     *
     * @param pms MailServer
     * @return int
     * @throws ServerException, EntityException
     */
    public int updatePubMailServer(MailServer pms)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - updatePubMailServer ");
        }
        try {
            return manager.updatePubMailServer(pms);
        } catch (EntityException e) {
            log.error("Server error (updatePubMailServer)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (updatePubMailServer)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     *
     * update user regarding the mail server connection
     *
     * @param msa MailServerAccount
     * @return int
     * @throws EntityException
     */
    public int updateUser(MailServerAccount msa)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - updateUser ");
        }
        try {
            return manager.updateUser(msa);
        } catch (EntityException e) {
            log.error("Server error (updateUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (updateUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * @param accountID long
     * @throws com.funambol.framework.server.error.ServerException
     * @throws com.funambol.email.exception.EntityException
     * @return number of marked rows
     */
    public int markUserAsDelete(long accountID) throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - markUserAsDelete");
        }
        try {
            return manager.markUserAsDelete(accountID);
        } catch (EntityException e) {
            log.error("Server error (markUserAsDelete)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (markUserAsDelete)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * disable account
     *
     * @param accountID String
     * @return int
     * @throws ServerException, EntityException
     */
    public int disableUser(long accountID)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - disableUser ");
        }
        try {
            return manager.disableUser(accountID);
        } catch (EntityException e) {
            log.error("Server error (disableUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (disableUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    public int deleteAccount(long accountID)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - deleteAccount ");
        }
        try {
            return manager.deleteAccount(accountID);
        } catch (EntityException e) {
            log.error("Server error (disableUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (disableUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * enable account
     *
     * @param accountID String
     * @return int
     * @throws ServerException, EntityException
     */
    public int enableUser(long accountID)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - enableUser ");
        }
        try {
            return manager.enableUser(accountID);
        } catch (EntityException e) {
            log.error("Server error (enableUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (enableUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
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
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - deletePubMailServer " + mailServerId);
        }
        try {
            return manager.deletePubMailServer(mailServerId);
        } catch (EntityException e) {
            log.error("Server error (deletePubMailServer)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (deletePubMailServer)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     *
     * get user
     *
     * @param username String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws ServerException, EntityException
     */
    public MailServerAccount getUser(String username)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getUser ");
        }
        try {
            return manager.getUser(username);
        } catch (EntityException e) {
            log.error("Server error (getUser)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (getUser)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * get user
     *
     * @param accountID String
     * @return all the info about the user and the related mail server MailServerAccount
     * @throws ServerException, EntityException
     */
    public MailServerAccount getUserFromID(long accountID)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getUserFromID ");
        }
        try {
            return manager.getUserFromID(accountID);
        } catch (EntityException e) {
            log.error("Server error (getUserFromID)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (getUserFromID)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * get public mail server
     *
     * @param mailServerId String
     * @return all the info about the public mail server MailServer
     * @throws ServerException, EntityException
     */
    public MailServer getPubMailServerFromID(String mailServerId)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getPubMailServerFromID ");
        }
        try {
            return manager.getPubMailServer(mailServerId);
        } catch (EntityException e) {
            log.error("Server error(getPubMailServerFromID)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (getPubMailServerFromID)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }

    /**
     *
     * check the account on the mail server
     *
     * @param msa MailServerAccount
     * @param portalCheckTimeout timeout for each attempt
     * @return true if the account is authenticated
     * @throws EntityException
     */
    public MailServerError checkAccount(MailServerAccount msa, Integer portalCheckTimeout)
    throws ServerException, EntityException {
        if (log.isTraceEnabled()) {
            log.trace("EmailWS - checkAccount ");
        }
        try {
            return manager.guessAccount(msa, portalCheckTimeout.intValue());
        } catch (EntityException e) {
            log.error("Server error (checkAccount)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (checkAccount)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }


    /**
     * Returns all the folders names for the given IMAP account.
     *
     * @param msa mail server account
     * @return list of the folders names for this account.
     */
    public Object[] getImapFolders(MailServerAccount msa)
    throws EntityException, ServerException{

        if (log.isTraceEnabled()) {
            log.trace("EmailWS - getImapFolders ");
        }
        try {
            return manager.getImapFolders(msa).toArray();
        } catch (EntityException e) {
            log.error("Server error (getImapFolders)", e);
            throw e;
        } catch (Exception e) {
            log.error("Server error (getImapFolders)", e);
            throw new ServerException(e.getMessage(), e);
        }
    }
}
