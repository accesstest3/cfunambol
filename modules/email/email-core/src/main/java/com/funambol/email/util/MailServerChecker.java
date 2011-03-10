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

package com.funambol.email.util;

import java.io.IOException;
import java.io.InputStream;

import javax.mail.Folder;

import com.sun.mail.pop3.POP3Folder;

import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Def;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Check if the mail server account is accessible
 * @version $Id: $
 */
public class MailServerChecker {
    
	// --------------------------------------------------------------- Constants

    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

	// ---------------------------------------------------------- Public Methods	

    /**
     * Checks if the Mail Server Account is accessible. If it is it does nothing,
     * if it is not accessible it throws an EmailAccessException
     * @param mailServerAccount The Mail Server Account
     * @param folderInboxOpened The Folder to access
     * @throws com.funambol.email.exception.EmailAccessException if the Mail 
     * Server Account is not accessible
     */
    public void checkAccountAccessiblility(MailServerAccount mailServerAccount,
            Folder folderInboxOpened) throws EmailAccessException {
        
        if (Def.PROTOCOL_POP3.equals(mailServerAccount.getMailServer().getProtocol())){
            checkPOP3AccountAccessiblility(mailServerAccount, folderInboxOpened);
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Skipping account accessibility check since " +
                        " protocol is not pop");
            }
        }

    }
    
	// --------------------------------------------------------- Private Methods

    /**
     * Checks if the INBOX folder for this mail server is accessible.
     * <p/>
     * Currently only Hotmail mail server is checked. The Hotmail mail server
     * allows login only every 15 minutes, for free accounts (5 minutes for premium
     * accounts). Within that time interval, only a limited number of good sessions
     * can take place allowed.<br/>
     * When all the attempts have been consumed, the mail server let the user to
     * authenticate, so a session is established, but each command sent to the
     * mail server will get the following error response: <code>-ERR login allowed
     * only every 15 minutes</code>
     * 
     * @param mailServerAccount
     * @param folderInboxOpened
     * @throws com.funambol.email.exception.EmailAccessException if the account
     * is not accessible
     */
    private void checkPOP3AccountAccessiblility(MailServerAccount mailServerAccount,
            Folder folderInboxOpened) throws EmailAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Checking if mail server account is accessible");
        }

        if (!mailServerAccountHasToBeChecked(mailServerAccount)){
            if (log.isTraceEnabled()) {
                log.trace("Skipping account accessibility check: " +
                        "mailServerAccount has not to be checked");
            }
            return;
        }

        if (!(folderInboxOpened instanceof POP3Folder)) {
            throw new EmailAccessException("Inbox folder is not coherent with " +
                    "the server type (should be a POP3Folder)");
        }
        POP3Folder pop3folderInboxOpened = (POP3Folder)folderInboxOpened;
        
        if (!pop3folderInboxOpened.isOpen()) {
            throw new EmailAccessException("Inbox folder is not open");
        }

        //
        // JavaMail methods generally used to access the INBOX folder are not able
        // to recognize the error response described above, so they do not raise
        // any exception but give some response that could be wrongly interpreted:
        //
        // eg:
        // - Folder.getMessageCount() will return 0 even if there are some messages in
        //   the INBOX
        // - Folder.getMessage(int msgId) returns null even if that message is
        //   still present.
        //
        // Note that: method POP3Folder.listCommand() returns a not null object
        // if Hotmail mail server is accessible, null otherwise, so this method
        // is used here to recognize a bad session. In case of bad session a
        // EmailAccessException is thrown, with a suitable message.
        //
        InputStream list = null;
        try {
            list = pop3folderInboxOpened.listCommand();
        } catch (Exception e) {
            throw new EmailAccessException("Error trying to access email server", e);
        } finally {
            try {
                if (list != null){
                    list.close();
                }
            } catch (IOException ex) {
                log.error("Unable to close the InputStream", ex);
            }
        }

        if (list == null){
            throw new EmailAccessException("Mail Server Account is not accessible.");
        }
        if (log.isTraceEnabled()) {
            log.trace("mail server account is accessible");
        }
    }

    /**
     * 
     * @param mailServerAccount
     * @return true if the email server account needs to be checked or false 
     * otherwise.
     */
    private boolean mailServerAccountHasToBeChecked(MailServerAccount mailServerAccount) {
        return Def.SERVER_TYPE_HOTMAIL.equals(
                mailServerAccount.getMailServer().getMailServerType());
        
    }
}
