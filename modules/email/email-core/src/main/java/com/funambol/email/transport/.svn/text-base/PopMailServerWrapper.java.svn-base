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
package com.funambol.email.transport;


import java.util.Properties;
import com.sun.mail.pop3.POP3Folder;
import javax.mail.MessagingException;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.util.Def;
import javax.mail.Message;

/**
 *
 * <p>Create a connection with a Mail Server </p>
 *
 * @version $Id: PopMailServerWrapper.java,v 1.1 2008-03-25 11:25:31 gbmiglia Exp $
 */
public class PopMailServerWrapper
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

    public POP3Folder folderInboxOpened    = null;

    // ---------------------------------------------------------- PUBLIC METHODS

    /**
     * opens the Inbox Folder
     *
     * @param openInboxFolder
     * @param df
     * @throws com.funambol.email.exception.EmailAccessException
     */
    public void openInboxFolder(boolean openInboxFolder,
            DefaultFolder df)
        throws EmailAccessException {

        if (log.isTraceEnabled()) {
            log.trace("Open POP3 Inbox Folder and manage the other main folders");
        }

        // open the Inbox folder
        if (openInboxFolder){
            try {
                this.defaultFolder     = df;
                this.folderInboxOpened =
                        (POP3Folder)this.mailDefaultFolder.getFolder(df.getInboxName());
                this.folderInboxOpened.open(javax.mail.Folder.READ_WRITE);
            } catch (Exception e) {
                log.error("Error getting Folders: ", e);
                throw new EmailAccessException("Error getting Folders (" + e.getMessage() + ")");
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("POP3 Inbox opened");
        }

    }

    /**
     * closes the connection with the mail server
     *
     * @throws EmailAccessException
     */
    public void releaseConnection() throws EmailAccessException {
        if (log.isTraceEnabled()) {
            log.trace("POP3 Start Connection Closing");
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
            throw new EmailAccessException("Error Closing Store " , e);
        }

    }


    /**
     *
     * returns Inbox Folder Instance
     *
     * @return POP3Folder
     * @throws EmailAccessException
     */
    public POP3Folder getInboxFolder() throws EmailAccessException {
        POP3Folder inbox;
        try {
            inbox = (POP3Folder)mailDefaultFolder.getFolder(
                    defaultFolder.getInboxName());
        } catch (MessagingException me){
            throw new EmailAccessException("Error getting Inbox Folder " +
                    me.toString());
        }
        return inbox;
    }

    //---------------------------------------------------------- PRIVATE METHODS

    /**
     *
     *
     *
     *
     * @param props Properties
     * @param incomingServer String
     * @param incomingPort String
     * @param provider Provider
     * @param portalCheck
     * @param portalCheckTimeout
     */
    protected void setConnectionSSLIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection,
            String sslSocketFactory) {

        if (log.isTraceEnabled()) {
            log.trace("POP3 set a SSL connection Incoming");
        }

        props.setProperty("mail.store.protocol"             , "pop3");
        props.setProperty("mail.pop3.host"                  , incomingServer);
        props.setProperty("mail.pop3.port"                  , incomingPort);
        props.setProperty("mail.pop3.timeout"               , timeoutStore);
        props.setProperty("mail.pop3.connectiontimeout"     , timeoutConnection);
        props.setProperty("mail.pop3.socketFactory.class"   , sslSocketFactory);
        props.setProperty("mail.pop3.socketFactory.fallback","false");
        props.setProperty("mail.pop3.socketFactory.port"    ,incomingPort);

    }

    /**
     *
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
            log.trace("POP3 set a SSL connection Outgoing");
        }

        props.setProperty("mail.transport.protocol" , "smtp");
        props.setProperty("mail.smtp.host"          , outgoingServer);
        props.setProperty("mail.smtp.port"          , outgoingPort);
        if (smtpAuth){
            props.setProperty("mail.smtp.auth", "true");
        }
        props.setProperty("mail.smtp.socketFactory.class"   , sslSocketFactory);
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.socketFactory.port"    , outgoingPort);

        if (this.serverType.equals(Def.SERVER_TYPE_HOTMAIL)){
            props.setProperty("mail.smtp.starttls.enable","true");
        }

    }

    /**
     *
     *
     *
     * @param props Properties
     * @param incomingServer String
     * @param incomingPort String
     */
    protected void setConnectionNormalIn(Properties props,
            String incomingServer,
            String incomingPort,
            String timeoutStore,
            String timeoutConnection){

        if (log.isTraceEnabled()) {
            log.trace("POP3 set a normal connection Input");
        }
        props.setProperty("mail.pop3.host"              , incomingServer);
        props.setProperty("mail.pop3.port"              , incomingPort);
        props.setProperty("mail.store.protocol"         , "pop3");
        props.setProperty("mail.pop3.timeout"           , timeoutStore);
        props.setProperty("mail.pop3.connectiontimeout" , timeoutConnection);

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
            log.trace("POP3 set a normal connection Output");
        }
        props.setProperty("mail.smtp.host"          , outgoingServer);
        props.setProperty("mail.smtp.port"          , outgoingPort);
        props.setProperty("mail.transport.protocol" , "smtp");
        if (smtpAuth){
            props.setProperty("mail.smtp.auth", "true");
        }
        if (this.serverType.equals(Def.SERVER_TYPE_HOTMAIL)){
            props.setProperty("mail.smtp.starttls.enable","true");
        }

    }

}
