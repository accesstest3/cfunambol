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
package com.funambol.email.inboxlistener.engine;


import com.funambol.email.exception.AccountNotFoundException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.inboxlistener.dbdao.InboxListenerDAO;
import com.funambol.email.inboxlistener.plugin.parser.KeyAccount;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Def;
import com.funambol.pushlistener.service.registry.dao.DataAccessException;
import org.apache.log4j.Logger;

/**
 *
 *
 * @version $Id: EmailAddressAccountProvider.java,v 1.1.1.1 2007-12-11 08:17:19 nichele Exp $
 */
public class EmailAddressAccountProvider implements IMailServerAccountProvider {

    // --------------------------------------------------------------- Constants
    
    private String pushRegistryTableName;
    
    /**
     * The logger
     */
    private Logger log = Logger.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Constructors
    
    public EmailAddressAccountProvider(String pushRegistryTableName){
        this.pushRegistryTableName = pushRegistryTableName;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * return the account information of the user with the given id
     *
     * @param key with the given user id
     * @return account object
     */
    public MailServerAccount getAccount(KeyAccount key)
    throws InboxListenerException, AccountNotFoundException {

        String emailaddress = key.getMsAddress();

        MailServerAccount msa = null;
        if (log.isTraceEnabled()) {
            log.trace("Starting get account for emailaddress " + emailaddress);
        }
        try {
            InboxListenerDAO dbdao = new InboxListenerDAO(pushRegistryTableName);
            msa = dbdao.getAccountByEmailaddress(emailaddress);
        } catch (AccountNotFoundException ex) {
            throw new AccountNotFoundException(ex);
        } catch (DataAccessException ex) {
            throw new InboxListenerException("Unable to load the inboxlistener " +
                    "configuration with emailaddress: " + emailaddress, ex);
        } catch (Exception ex) {
            throw new InboxListenerException("Unable to load the inboxlistener " +
                    "configuration with emailaddress: " + emailaddress, ex);
        }
        if (log.isTraceEnabled()) {
            log.trace("Account for emailaddress " + emailaddress + "done!");
        }
        return msa;
    }

}
