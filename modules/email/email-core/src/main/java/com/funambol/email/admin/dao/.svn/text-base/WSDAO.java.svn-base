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
package com.funambol.email.admin.dao;


import com.funambol.framework.filter.Clause;
import com.funambol.framework.tools.beans.BeanException;
import com.funambol.framework.tools.beans.BeanFactory;
import com.funambol.email.model.SyncItemInfoAdmin;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.server.admin.ws.client.AdminWSClient;
import com.funambol.server.admin.ws.client.ServerInformation;
import java.util.HashMap;
import java.util.Map;
import org.apache.axis.client.Call;

/**
 * Contains methods to access to the DB tables.
 *
 * @version $Id: WSDAO.java,v 1.1 2008-03-25 11:25:35 gbmiglia Exp $
 */
public class WSDAO extends AdminWSClient {

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a new instance of WSDAO.
     *
     * @param endPoint
     * @param user
     * @param pwd
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public WSDAO(
            ServerInformation serverInformation,
            String loggerName) {
        super(serverInformation, loggerName);
        
        Map<Class, String> additionalTypesMapping
                = new HashMap<Class, String>();
        
        additionalTypesMapping.put(com.funambol.email.model.MailServerAccount.class, "MailServerAccount");
        additionalTypesMapping.put(com.funambol.email.model.MailServer.class, "MailServer");
        additionalTypesMapping.put(com.funambol.pushlistener.service.registry.RegistryEntry.class, "RegistryEntry");
        additionalTypesMapping.put(com.funambol.email.model.SyncItemInfoAdmin.class, "SyncItemInfoAdmin");
        additionalTypesMapping.put(com.funambol.email.transport.MailServerError.class, "MailServerError");

        this.addTypesMapping(additionalTypesMapping);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Retrieve an array of the MailServerAccount objects that match the given
     * clause.
     *
     * @throws com.funambol.email.exception.InboxListenerException
     * @throws com.funambol.framework.tools.beans.BeanException
     * @return MailServerAccount[]
     */
    public MailServerAccount[]  getAccounts(Clause clause)
    throws InboxListenerException, BeanException {

        MailServerAccount[] accounts = null;

        Object[] params = new Object[] { BeanFactory.marshal(clause) } ;

        try {
            Object obj = super.invoke("getUsers", params);
            accounts = (MailServerAccount[])obj;
        } catch (Exception e)  {
            throw new InboxListenerException("Error getting all the accounts");
        }
        return accounts;

    }

    /**
     * Retrieve an array of the MailServer objects that match the given clause.
     *
     * @throws com.funambol.email.exception.InboxListenerException
     * @throws com.funambol.framework.tools.beans.BeanException
     * @return MailServer[]
     */
    public MailServer[] getMailServers(Clause clause) throws InboxListenerException, BeanException {
        MailServer[] mailServers = null;

        Object[] params = new Object[] { BeanFactory.marshal(clause) } ;

        try {
            Object obj = super.invoke("getPubMailServers", params);
            mailServers = (MailServer[])obj;
        } catch (Exception e)  {
            throw new InboxListenerException("Error getting all the mail Servers");
        }
        return mailServers;
    }

    /**
     * Retrieve the account for the given username.
     *
     * @return account info MailboxAccount
     * @param username String
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public MailServerAccount getAccount(String username)
    throws InboxListenerException {
        MailServerAccount msa = null;
        Object[] params = new Object[] {username};
        try {
            msa = (MailServerAccount)super.invoke("getUser", params);
        } catch (Exception e)  {
            throw new InboxListenerException("Error getting the account");
        }
        return msa;
    }

    /**
     * Insert a given account.
     * @param msa the account to be inserted
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void insertAccount(MailServerAccount msa)
    throws InboxListenerException {
        Object[] params = new Object[] {msa};
        try {
            super.invoke("insertUser", params);
        } catch (Exception e)  {
            throw new InboxListenerException("inserAccount: Error inserting the account");
        }
    }

    /**
     * Insert a given mail server.
     * @param ms the mail server to be inserted
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void insertMailServer(MailServer ms)
    throws InboxListenerException {
        Object[] params = new Object[] {ms};
        try {
            super.invoke("insertPubMailServer", params);
        } catch (Exception e)  {
            throw new InboxListenerException("Error inserting the mail server");
        }
    }

    /**
     * Updates a given account.
     * @param msa account to be updated
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void updateAccount(MailServerAccount msa)
    throws InboxListenerException {
        Object[] params = new Object[] {msa};
        try {
            super.invoke("updateUser", params);
        } catch (Exception e)  {
            throw new InboxListenerException("updateAccount: Error inserting the account");
        }
    }

    /**
     * Updates a given mail server.
     * @param ms mail server to be updated
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void updateMailServer(MailServer ms)
    throws InboxListenerException {
        Object[] params = new Object[] {ms};
        try {
            super.invoke("updatePubMailServer", params);
        } catch (Exception e)  {
            throw new InboxListenerException("Error inserting the mail server");
        }
    }


    /**
     * Delete account with the given ID.
     * @param accountID id for the account to be deleted.
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void deleteAccount(long accountID)
    throws InboxListenerException {
        Object[] params = new Object[] {accountID};
        try {
            super.invoke("deleteAccount", params);
        } catch (Exception e)  {
            throw new InboxListenerException("Error deleting the account");
        }
    }

    /**
     * Delete mail server with the given ID.
     * @param mailserverID id for the mail server to be deleted.
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public int deleteMailServer(String mailserverID)
    throws InboxListenerException {
        Object[] params = new Object[] {mailserverID};
        int num = 0;
        try {
            num = ((Integer)super.invoke("deletePubMailServer", params)).intValue();
        } catch (Exception e)  {
            throw new InboxListenerException("Error deleting the mail server");
        }
        return num;
    }

    /**
     * Retrieve all the cache entries for given username,protocol.
     * @param username
     * @param protocol
     * @throws com.funambol.email.exception.InboxListenerException
     * @return the array of cache entries
     */
    public SyncItemInfoAdmin[] getCachedInfo(String username, String protocol)
    throws InboxListenerException {
        SyncItemInfoAdmin[] info = null;
        Object[] params = new Object[] {username, protocol};
        try {
            Object obj = super.invoke("getCachedInfo", params);
            info = (SyncItemInfoAdmin[])obj;
        } catch (Exception e)  {
            throw new InboxListenerException("Error getting cached info (" + e.getMessage() + ")");
        }
        return info;
    }

    /**
     * Retrieves all the folders names for the given account.
     *
     * @param msa mail server account
     * @return list of the folders names for the given account
     */
    public String[] getImapFolders(MailServerAccount msa)
    throws InboxListenerException {
        Object[] params = new Object[] {msa};
        String[] foldersNames = null;
        try {

            Object[] resultArray = (Object[])super.invoke("getImapFolders", params);

            foldersNames = new String[resultArray.length];
            for (int i = 0; i < resultArray.length; i++) {
                foldersNames[i] = (String)resultArray[i];
            }

        } catch (Exception e) {
            throw new InboxListenerException("Error getting IMAP folders (" + e.getMessage() + ")");
        }
        return foldersNames;
    }
}
