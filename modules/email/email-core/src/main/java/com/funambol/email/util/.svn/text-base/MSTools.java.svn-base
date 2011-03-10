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
package com.funambol.email.util;


import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EmailConfigException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.transport.IMailServerWrapper;
import javax.mail.MessagingException;
import javax.mail.Store;



/**
 * This class groups some utility methods for Mail Server invoking
 *
 * @version $Id: MSTools.java,v 1.1 2008-03-25 11:25:34 gbmiglia Exp $
 */
public class MSTools {

    // ------------------------------------------------------------ Private data

    /**    */
    private MailServerAccount msa = null;

    /**    */
    private Store  store     = null;

    /**
     * Timeouts for connection and message retrieving from a mail servers.
     * An empty string means that no timeout is set. Thus a timeout should be
     * set to a default non-empty value.
     */
    private String timeoutStore      = Def.DEFAULT_TIMEOUT_STORE;
    private String timeoutConnection = Def.DEFAULT_TIMEOUT_CONNECTION;
    
    private boolean checkCertificates;
    
    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new MSTools with the given MSAccount
     *
     * @param _msa the DBAccount
     */
    public MSTools(MailServerAccount _msa) {
        this.msa = _msa;
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * open the Mail Server connection; Note the IL Tool
     * works only the INBOX folder
     *
     * @param mswf reference to the mail server wrapper IMailServerWrapper
     * @throws InboxListenerException
     */
    public void openConnection(IMailServerWrapper mswf)
    throws InboxListenerException {

        try {
            DefaultFolder df = new DefaultFolder();
            String sslSocketFactory = null;
            if (checkCertificates){
                sslSocketFactory = Def.SSL_SOCKET_FACTORY_STANDARD;
            } else {
                sslSocketFactory = Def.SSL_SOCKET_FACTORY_FUNAMBOL;
            }

            String msLogin = msa.getMsLogin();
            String msPassword = msa.getMsPassword();

            mswf.openConnection(msa.getMailServer().getMailServerType(),
                    msa.getMailServer().getOutServer(),
                    "25",
                    false,
                    msa.getMailServer().getInServer(),
                    "" + msa.getMailServer().getInPort(),
                    df,
                    false,
                    msLogin,
                    msPassword,
                    null,
                    null,
                    msa.getMailServer().getIsSSLIn(),
                    msa.getMailServer().getIsSSLOut(),
                    timeoutStore, timeoutConnection,
                    sslSocketFactory);

            this.store = mswf.getStore();

        } catch (EmailAccessException e) {
            throw new InboxListenerException(
                    "Error opening the Mail Server Connection " + e.getMessage());
        }

    }

    /**
     * close the Mail Server connection; Note the IL Tool
     * works only the INBOX folder
     *
     * @param mswf reference to the mail server wrapper IMailServerWrapper
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public void closeConnection(IMailServerWrapper mswf) throws InboxListenerException {
        try {
            if (mswf != null){
                mswf.releaseConnection();
            }
        } catch (EmailAccessException e) {
            throw new InboxListenerException(
                    "Error closing the Mail Server Connection " + e.getMessage());
        }
    }

    /**
     * return the store
     * @return Store
     */
    public Store getStore() {
        return this.store;
    }

    /**
     * Sets <code>timeoutStore</code> and  <code>timeoutConnection</code> properties.
     */
    public void setTimeouts(String timeoutStore, String timeoutConnection) {
        this.timeoutStore = timeoutStore;
        this.timeoutConnection = timeoutConnection;
    }
    
    /**
     * Sets the <code>checkCertificates</code> property.
     */
    public void setCheckCertificates(boolean checkCertificates){
        this.checkCertificates = checkCertificates;
    }
}
