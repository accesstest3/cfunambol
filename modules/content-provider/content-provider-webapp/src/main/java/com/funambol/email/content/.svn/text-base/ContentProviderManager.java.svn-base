/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.email.content;

import javax.mail.Message;

import com.funambol.email.console.dao.ConsoleDAO;
import com.funambol.email.engine.source.EmailConnectorConfig;
import com.funambol.email.exception.DBAccessException;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EmailConfigException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.InboxListenerConfigException;
import com.funambol.email.items.dao.EntityDAO;
import com.funambol.email.items.dao.ImapEntityDAO;
import com.funambol.email.items.dao.PopEntityDAO;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.ImapMailServerWrapper;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.transport.PopMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;

import com.funambol.framework.tools.encryption.EncryptionTool;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * Manage the attachment requests
 */
public class ContentProviderManager {

    /**
     * max number of emails in the inbox cache
     */
    private int maxMessageNumber = Def.MAX_INBOX_EMAIL_NUMBER;

    /**
     * The first threshold as specified in the PopEntityManager 
     * getEmailFromUIDInbox method
     */
    private int threshold_1 = 20;
    
    /**
     * The second threshold as specified in the PopEntityManager 
     * getEmailFromUIDInbox method
     */
    private int threshold_2 = 40;
            
    // ------------------------------------------------------------ Private data
    
    /**
     * The EntityDAO used to authorize the token and to access the mail server
     */
    private EntityDAO entityDAO;
    
    /**
     * wrapper for the Mail Server
     */
    private IMailServerWrapper msWrapper = null;
    
    /**
     * The email server protocol
     */
    private String protocol = null;
    
    /**
     * The email server type
     */
    private String serverType = null;
    
    /**
     * The FunambolLogger
     */
    private FunambolLogger log = FunambolLoggerFactory.getLogger(ContentProviderServlet.LOGGER_NAME);

    // ------------------------------------------------------------- Constructor

    /**
     * Create a ContentProviderManager
     */
    public ContentProviderManager() {

        try {
            entityDAO = new EntityDAO(null);
        } catch (EntityException ex) {
            log.error("Error creating ContentProviderManager ", ex);
        }
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Open the connection to the Mail Server
     * @param mailServerAccount The account to use to connect to the mail server
     * @throws com.funambol.email.exception.EmailAccessException
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public void openConnection(MailServerAccount mailServerAccount)
            throws EmailAccessException, ContentProviderException {
        try {

            maxMessageNumber = mailServerAccount.getMaxEmailNumber();

            this.protocol = mailServerAccount.getMailServer().getProtocol();
            this.serverType = mailServerAccount.getMailServer().getMailServerType();

            this.msWrapper = MailServerWrapperFactory.getMailServerWrapper(this.protocol);

            entityDAO = entityDAOFactoryMethod(this.serverType, this.protocol);                        
            
            String msLogin = mailServerAccount.getMsLogin();

            String msPassword = mailServerAccount.getMsPassword();

            boolean outAuth = mailServerAccount.getMailServer().getOutAuth();

            String outLogin = null;
            if (outAuth) {
                outLogin = mailServerAccount.getOutLogin();
                if (outLogin == null) {
                    outLogin = msLogin;
                }
            }

            String outPassword = null;
            if (outAuth) {
                outPassword = mailServerAccount.getOutPassword();
                if (outPassword == null) {
                    outPassword = msPassword;
                }
            }

            boolean openInboxFolder = true;

            MailServer mailServer = mailServerAccount.getMailServer();
            DefaultFolder defaultFolder = new DefaultFolder(mailServer.getInboxPath(),
                    mailServer.getOutboxPath(),
                    mailServer.getSentPath(),
                    mailServer.getDraftsPath(),
                    mailServer.getTrashPath());

            String timeoutStore = EmailConnectorConfig.getConfigInstance().getTimeoutStore();

            String timeoutConnection = EmailConnectorConfig.getConfigInstance().getTimeoutConnection();

            boolean checkCertificates = EmailConnectorConfig.getConfigInstance().getCheckCertificates();

            String sslSocketFactory;
            if (checkCertificates) {
                sslSocketFactory = Def.SSL_SOCKET_FACTORY_STANDARD;
            } else {
                sslSocketFactory = Def.SSL_SOCKET_FACTORY_FUNAMBOL;
            }

            this.msWrapper.openConnection(
                    mailServerAccount.getMailServer().getMailServerType(),
                    mailServerAccount.getMailServer().getOutServer(),
                    Integer.toString(mailServerAccount.getMailServer().getOutPort()),
                    outAuth, mailServerAccount.getMailServer().getInServer(),
                    Integer.toString(mailServerAccount.getMailServer().getInPort()),
                    defaultFolder,
                    openInboxFolder,
                    msLogin,
                    msPassword,
                    outLogin,
                    outPassword,
                    mailServerAccount.getMailServer().getIsSSLIn(),
                    mailServerAccount.getMailServer().getIsSSLOut(),
                    timeoutStore,
                    timeoutConnection,
                    sslSocketFactory);

        } catch (EmailAccessException ex) {
            try {
                msWrapper.releaseConnection();
            } catch (EmailAccessException e1) {
                throw new EmailAccessException("Error closing store connection", e1);
            }
            throw new EmailAccessException("Error Connecting Mail Server:" +
                    ex.getMessage(), ex);
        } catch (EmailConfigException ex) {
            try {
                msWrapper.releaseConnection();
            } catch (EmailAccessException e1) {
                throw new EmailAccessException("Error closing store connection", e1);
            }
            throw new EmailAccessException("Error Getting Configuration Parameters: " +
                    ex.getMessage(), ex);
        }
    }
    
    /**
     * Close the connection to the Mail server holded by the Mail Server Wrapper
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public void closeConnection() throws ContentProviderException {
        try {
            if (this.msWrapper != null){
                this.msWrapper.releaseConnection();
            }
        } catch (EmailAccessException e) {
            throw new ContentProviderException("Error closing the Mail Server Connection ", e);
        }
    }
    

    /**
     * Retrieve the mail server account object from the db
     * @param userName The user name related to the mail server account
     * @return The mail server account 
     * @throws com.funambol.email.exception.EntityException If it is unable to
     * retrive the email server account
     */
    public MailServerAccount retrieveMailServerAccount(String userName)
            throws EntityException {
        try {

            ConsoleDAO consoleDao = new ConsoleDAO();
            MailServerAccount msa = consoleDao.getUser(userName);
            if (msa != null) {
                msa.setMsLogin(Utility.getLogin(msa));
            } else {
                throw new EntityException("Error: Account information not found!");
            }
            return msa;
        } catch (DBAccessException ex) {
            throw new EntityException("Error getting Account information ", ex);
        } catch (InboxListenerConfigException ex) {
            throw new EntityException("Error getting Account information ", ex);
        }
    }

    /**
     * Retrieve the mail server account object from the db
     * @param accountID The id of the mail server account stored item 
     * @return The mail server account corresponding to the account id or null
     * if the id doesnt refer to any account
     * @throws com.funambol.email.exception.EntityException
     */
    public MailServerAccount retrieveMailServerAccount(long accountID)
            throws EntityException {
        try {

            MailServerAccount msa = null;
            ConsoleDAO consoleDao = new ConsoleDAO();
            msa = consoleDao.getUserFromID(accountID);
            if (msa != null) {
                msa.setMsLogin(Utility.getLogin(msa));
            } else {
                throw new EntityException("Error: Account information not found!");
            }
            return msa;
        } catch (DBAccessException ex) {
            throw new EntityException("Error getting Account information ", ex);
        } catch (InboxListenerConfigException ex) {
            throw new EntityException("Error getting Account information ", ex);
        }
    }    
    
    /**
     * Get the Message corresponding to the given UID from then mail server.
     * If no such message exists, null  is returned.
     * @param messageId UID for the desired message
     * @return the Message object. null is returned if no message corresponding 
     * to this UID is obtained.
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public Message getMessage(String messageId) throws ContentProviderException {
        Message message = null;
        
        if (Def.PROTOCOL_IMAP.equals(this.protocol) ){
            message =  getMessageFromImapServer(messageId);
        } else if (Def.PROTOCOL_POP3.equals(this.protocol) ){
            message = getMessageFromPopServer(messageId);
        } else {
            throw new ContentProviderException("Unknown protocol specified: " + this.protocol);
        }
        
        return message;
    }
    
    /**
     * Get the Message corresponding to the given UID. If no such message exists,
     * null  is returned.
     * @param messageId UID for the desired message
     * @return the Message object. null is returned if no message corresponding 
     * to this UID is obtained.
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public Message getMessageFromPopServer(String messageId) throws ContentProviderException {
   
        if (!(msWrapper instanceof PopMailServerWrapper)) {
            throw new ContentProviderException("Invalid PopMailServerWrapper");
        }
        PopMailServerWrapper popMailServerWrapper = (PopMailServerWrapper) msWrapper;
        
        if (!(entityDAO instanceof PopEntityDAO)) {
            throw new ContentProviderException("Invalid PopEntityDAO");
        }
        PopEntityDAO popEntityDAO = (PopEntityDAO) entityDAO;

        try {
            Message message = popEntityDAO.getItem(messageId, maxMessageNumber, threshold_1, threshold_2, popMailServerWrapper);
            return message;
        } catch (EntityException ex) {
            throw new ContentProviderException("Unabel to retrieve message from pop mail server", ex);
        }
    }   
    
    
    /**
     * Get the Message corresponding to the given UID. If no such message exists,
     * null  is returned.
     * @param messageId UID for the desired message
     * @return the Message object. null is returned if no message corresponding 
     * to this UID is obtained.
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public Message getMessageFromImapServer(String messageId) throws ContentProviderException {

        if (!(msWrapper instanceof ImapMailServerWrapper)) {
            throw new ContentProviderException("Invalid ImapMailServerWrapper");
        }
        ImapMailServerWrapper imapMailServerWrapper = (ImapMailServerWrapper) msWrapper;
        
        if (!(entityDAO instanceof ImapEntityDAO)) {
            throw new ContentProviderException("Invalid ImapEntityDAO");
        }
        ImapEntityDAO imapEntityDAO = (ImapEntityDAO) entityDAO;

        long uid;
        try {
            uid = Long.parseLong(messageId);
        } catch (NumberFormatException ex) {
            throw new ContentProviderException("Invalid message id parameter: " + messageId, ex);
        }

        try {
            Message message = imapEntityDAO.getEmailFromUID(imapMailServerWrapper.folderInboxOpened, "", uid);
            return message;
        } catch (EntityException ex) {
            throw new ContentProviderException("Unabel to retrieve message from imap mail server", ex);
        }
    }
    
    /**
     * Performs auhtorization.
     * 
     * @param token a token coming from a request for an attachment
     * @return the guid of the email associated to the token, <code>null</code>
     * if authorization fails
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public String authorize(String username, String token) throws ContentProviderException {

        String guid = null;
        try {
            
            //
            // Tokens in the database are encrypted, while token coming from a 
            // request are not. Thus token parameter has to be encrypted before
            // serching in the database
            //
            String encryptedToken = EncryptionTool.encrypt(token);

            // search in the database for the guid
            guid = entityDAO.getGuidByToken(username, encryptedToken);

        } catch (Exception e) {
            throw new ContentProviderException("Error while authorizing token: " + token, e);
        }
        
        return guid;
    }
    
    
    /**
     * Depending from the protocol create a ImapEntityDAO or a PopEntityDAO 
     * @param serverType 
     * @param protocol
     * @return
     * @throws com.funambol.email.content.ContentProviderException 
     */
    public EntityDAO entityDAOFactoryMethod(String serverType,
            String protocol) throws ContentProviderException {
        EntityDAO entity = null;
        try {
            if (Def.PROTOCOL_IMAP.equals(protocol)) {
                entity = new ImapEntityDAO(serverType);
            } else if (Def.PROTOCOL_POP3.equals(protocol)) {
                entity = new PopEntityDAO(serverType);
            } else {
                throw new ContentProviderException("Unable to create EntityDAO. " +
                        "Unknown protocol: "+protocol);
            }
            return entity;
        } catch (EntityException ex) {
            throw new ContentProviderException("", ex);
        }
    }
}
