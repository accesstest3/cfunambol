/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2005 - 2007 Funambol, Inc.
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
package com.funambol.email.transport  ;

import java.util.Properties;
import java.security.Security;
import com.sun.net.ssl.internal.ssl.Provider;
import com.sun.mail.imap.IMAPFolder;
import javax.mail.MessagingException;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.model.DefaultFolder;
import javax.mail.Message;

/**
 *
 * <p>
 * Create a connection with a Mail Server
 * using the IMAP protocol
 * </p>
 *
 * @version $Id: ImapMailServerWrapper.java,v 1.1 2008-03-25 11:25:31 gbmiglia Exp $
 */
public class ImapMailServerWrapper
        extends CommonMailServerWrapper
        implements IMailServerWrapper {

    // -------------------------------------------------------------- Properties

    /**
     * used in the getMessageByUID method. in the POP protocol
     * there is no the method "folder.getMessageByUID(String)
     * At the end of the sync session the server calls the getSyncItem
     * In this method we open and close the folder every time.
     * Using this propreties we can leave the folder open for alle the
     * calls to the getSyncItem method.
     * In the EndSync we close this folder if it's open
     *
     */
    public Message[]  messagesInboxOpened  = null;

    public IMAPFolder folderInboxOpened    = null;

    // ---------------------------------------------------------- PUBLIC METHODS

    /**
     * opens the connection with the mail server
     *
     * @param openInboxFolder
     * @param df
     * @throws EmailAccessException
     */
    public void openInboxFolder(boolean openInboxFolder,
            DefaultFolder df)
        throws EmailAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Open IMAP Inbox Folder and manage the other main folders");
        }

        // open the Inbox folder
        if (openInboxFolder){
            try {
                this.defaultFolder     = df;
                this.folderInboxOpened =
                        (IMAPFolder)this.mailDefaultFolder.getFolder(df.getInboxName());
                this.folderInboxOpened.open(javax.mail.Folder.READ_WRITE);
            } catch (Exception e) {
                log.error("Error getting Folders: ", e);
                throw new EmailAccessException("Error getting Folders (" + e.getMessage() + ")");
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("IMAP Inbox opened");
        }

    }


    /**
     * closes the connection with the mail server
     *
     * @throws EmailAccessException
     */
    public void releaseConnection() throws EmailAccessException {
        if (log.isTraceEnabled()) {
            log.trace("IMAP Start Connection Closing");
        }
        try {
            if (this.folderInboxOpened != null) {
                if (this.folderInboxOpened.isOpen()) {
                    this.folderInboxOpened.close(true);
                }
            }
            if (this.store != null){
                if (this.store.isConnected()){
                    this.store.close();
                }
            }
            this.store = null;
            this.session = null;
        } catch (Exception e) {
            throw new EmailAccessException("Error Closing Store ", e);
        }

    }

    /**
     *
     * returns Inbox Folder Instance
     *
     * @return IMAPFolder
     * @throws EmailAccessException
     */
    public IMAPFolder getInboxFolder() throws EmailAccessException {
        IMAPFolder inbox;
        try {
            inbox = (IMAPFolder)mailDefaultFolder.getFolder(
                    defaultFolder.getInboxName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting Inbox Folder " +
                    me.toString());
        }
        return inbox;
    }


    /**
     *
     * returns Outbox Folder Instance
     *
     * @return IMAPFolder
     * @throws EmailAccessException
     */
    public IMAPFolder getOutboxFolder() throws EmailAccessException {
        IMAPFolder outbox;
        try {
            outbox = (IMAPFolder)mailDefaultFolder.getFolder(
                    defaultFolder.getOutboxName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting outbox Folder " +
                    me.toString());
        }
        return outbox;
    }


    /**
     *
     * returns Sent Folder Instance
     *
     * @return IMAPFolder
     * @throws EmailAccessException
     */
    public IMAPFolder getSentFolder() throws EmailAccessException {
        IMAPFolder sent;
        try {
            sent = (IMAPFolder)mailDefaultFolder.getFolder(
                    defaultFolder.getSentName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting sent Folder " +
                    me.toString());
        }
        return sent;
    }

    /**
     *
     * returns Drafts Folder Instance
     *
     * @return IMAPFolder
     * @throws EmailAccessException
     */
    public IMAPFolder getDraftsFolder() throws EmailAccessException {
        IMAPFolder drafts;
        try {
            drafts = (IMAPFolder)mailDefaultFolder.getFolder(
                    defaultFolder.getDraftsName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting drafts Folder " +
                    me.toString());
        }
        return drafts;
    }

    /**
     *
     * returns Trash Folder Instance
     *
     * @return IMAPFolder
     * @throws EmailAccessException
     */
    public IMAPFolder getTrashFolder() throws EmailAccessException {
        IMAPFolder trash;
        try {
            trash = (IMAPFolder)mailDefaultFolder.getFolder(
                    defaultFolder.getTrashName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting trash Folder " +
                    me.toString());
        }
        return trash;
    }

    //---------------------------------------------------------- PRIVATE METHODS

    /**
     *
     * @param props
     * @param incomingServer
     * @param incomingPort
     * @param provider
     * @param portalCheck
     * @param timeoutStore
     * @param timeoutConnection
     * @param portalCheckTimeout
     */
    protected void setConnectionSSLIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory) {

        if (log.isTraceEnabled()) {
            log.trace("IMAP set a SSL connection Input");
        }

        props.setProperty("mail.imap.host"                  , incomingServer);
        props.setProperty("mail.imap.port"                  , incomingPort);
        props.setProperty("mail.store.protocol"             , "imap");
        props.setProperty("mail.imap.timeout"               , timeoutStore);
        props.setProperty("mail.imap.connectiontimeout"     , timeoutConnection);
        props.setProperty("mail.imap.socketFactory.class"   , sslSocketFactory);
        props.setProperty("mail.imap.socketFactory.fallback", "false");
        props.setProperty("mail.imap.socketFactory.port"    , incomingPort);

    }

    /**
     *
     *
     * @param props Properties
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     * @param provider Provider
     */
    protected void setConnectionSSLOut(Properties props,
            String outgoingServer,
            String outgoingPort,
            boolean smtpAuth,
            String sslSocketFactory) {

        if (log.isTraceEnabled()) {
            log.trace("IMAP set a SSL connection Output");
        }

        props.setProperty("mail.transport.protocol" , "smtp");
        props.setProperty("mail.smtp.host" , outgoingServer);
        props.setProperty("mail.smtp.port" , outgoingPort);
        props.setProperty("mail.smtp.socketFactory.port", outgoingPort);
        if (smtpAuth){
            props.setProperty("mail.smtp.auth", "true");
        }
        props.setProperty("mail.smtp.socketFactory.class", sslSocketFactory);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port", outgoingPort);

    }

    /**
     *
     *
     *
     * @param props Properties
     * @param incomingServer String
     * @param incomingPort String
     * @param portalCheck
     * @param portalCheckTimeout
     */
    protected void setConnectionNormalIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection) {

        if (log.isTraceEnabled()) {
            log.trace("IMAP set a normal connection Input");
        }
        props.setProperty("mail.imap.host"      , incomingServer);
        props.setProperty("mail.imap.port"      , incomingPort);
        props.setProperty("mail.store.protocol" , "imap");
        props.setProperty("mail.imap.timeout"            , timeoutStore);
        props.setProperty("mail.imap.connectiontimeout"  , timeoutConnection);

    }

    /**
     *
     *
     *
     * @param props Properties
     * @param outgoingServer String
     * @param outgoingPort String
     * @param smtpAuth boolean
     */
    protected void setConnectionNormalOut(Properties props,
            String outgoingServer,
            String outgoingPort,
            boolean smtpAuth){

        if (log.isTraceEnabled()) {
            log.trace("IMAP set a normal connection Output");
        }

        props.setProperty("mail.smtp.host", outgoingServer);
        props.setProperty("mail.smtp.port", outgoingPort);
        props.setProperty("mail.transport.protocol", "smtp");
        if (smtpAuth){
            props.setProperty("mail.smtp.auth", "true");
        }

    }

}
