/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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
package com.funambol.email.inboxlistener.msdao;


import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.InboxListenerException;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.SyncItemInfoInbox;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.MSTools;
import com.funambol.email.util.MailServerChecker;
import com.funambol.email.util.Utility;
import com.funambol.email.util.UtilityDate;
import com.funambol.framework.engine.SyncItemKey;
import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.pop3.POP3Folder;
import java.io.IOException;
import java.util.LinkedHashMap;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Address;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.UIDFolder;

/**
 * This class groups some utility methods for Mail Server invoking
 *
 * @version $Id: MailboxMSDAOCommon.java,v 1.9 2008-05-19 15:39:21 gbmiglia Exp $
 */
public class MailboxMSDAOCommon  {

    // --------------------------------------------------------------- Constants
    
    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // ------------------------------------------------------------ Private data

    /**     */
    protected MailServerAccount msa = null;

    /**    */
    protected Folder inbox          = null;

    /**    */
    protected MSTools mstools       = null;

    /**    */
    protected boolean saveSubject   = false;

    /**    */
    protected boolean saveSender    = false;
    
    // ------------------------------------------------------------ Constructors

    public MailboxMSDAOCommon() {
    }

    /**
     *
     * 
     * @param _msa
     * @param _saveSubject
     * @param _saveSender
     * @param timeoutStore
     * @param timeoutConnection
     * @param checkCertificates
     */
    public MailboxMSDAOCommon(
            MailServerAccount _msa, 
            boolean _saveSubject, 
            boolean _saveSender,
            String timeoutStore,
            String timeoutConnection,
            boolean checkCertificates) {

        this.msa = _msa;

        this.saveSubject = _saveSubject;

        this.saveSender  = _saveSender;

        this.mstools = new MSTools(_msa);
        this.mstools.setTimeouts(timeoutStore, timeoutConnection);
        this.mstools.setCheckCertificates(checkCertificates);        
    }

    // ---------------------------------------------------------- Public Methods

    /**
     *
     */
    public void open(IMailServerWrapper mswf) throws InboxListenerException {
        try {
            mstools.openConnection(mswf);
            openInboxFolder();
        } catch (MessagingException e) {
            throw new InboxListenerException(e.getMessage());
        }
    }

    /**
     *
     */
    public void close(IMailServerWrapper mswf) throws InboxListenerException{
        try {
            closeInboxFolder();
            mstools.closeConnection(mswf);
        } catch (MessagingException e) {
            mstools.closeConnection(mswf);
            if (log.isTraceEnabled()) {
                log.trace("Closed Connection for user " + msa.getUsername());
            }
            throw new InboxListenerException(e.getMessage());
        }
    }

    /**
     * the method gets all list of the messages from the server and put in the hashmap
     * the the uid of the message and the index of the message in the folder
     * Further this method calculates the initial CRC using the flags. 
     *
     * @param protocol
     * @param max email number
     * @param validity
     * @return list of GUIDS
     * @throws com.funambol.email.exception.InboxListenerException
     */
    public List<SyncItemInfoInbox> getUids(String protocol, int max, String[] validity)
      throws InboxListenerException {

        List<SyncItemInfoInbox> uids = new ArrayList<SyncItemInfoInbox>();

        try {

            if (log.isTraceEnabled()) {
                log.trace("get Messages from Inbox Mailbox number : " + max);
            }

            // get info from MS

            Message[] messages = null;
            int mNumber  = inbox.getMessageCount();
            if (mNumber == 0){
                MailServerChecker mailServerChecker = new MailServerChecker();
                mailServerChecker.checkAccountAccessiblility(this.msa,  this.inbox);
            }
            
            if (mNumber > max){
                messages = inbox.getMessages((mNumber-(max-1)),mNumber);
            } else {
                messages = inbox.getMessages();
            }
            // Fetch the UID's and only the UIDs using the cool JavaMail FetchProfile.
            FetchProfile fp = new FetchProfile();
            fp.add(UIDFolder.FetchProfileItem.UID);
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                fp.add(UIDFolder.FetchProfileItem.FLAGS);
            }
            inbox.fetch(messages, fp);

            // get validity of the imap folder
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                validity[0] = ""+((IMAPFolder)inbox).getUIDValidity();
            }

            // save giuds
            String guid          = null;
            String uid           = null;
            int    index         = -1;
            long   crc           = 0;
            String flagList      = "";
            FlagProperties flags = null;
            Message message      = null;

            for (int i=0; i<messages.length; i++) {
                message = messages[i];
                index = message.getMessageNumber();
                if (protocol.equals(Def.PROTOCOL_IMAP)){
                    uid   = ""+((IMAPFolder)inbox).getUID(message);
                    guid  = Utility.createIMAPGUID(Def.FOLDER_INBOX_ID, uid, validity[0]);
                    flagList = Utility.createFlagsList(message);
                    crc      = Utility.createCRC(flagList, protocol);
                    flags    = Utility.createFlagProperties(message);
                    if (uid != null){
                        if (!flags.getDeleted()){
                            uids.add(new SyncItemInfoInbox(index, uid,
                                    new SyncItemKey(guid), crc, flags));
                        }
                    }
                } else {
                    uid   = ""+((POP3Folder)inbox).getUID(message);
                    guid  = Utility.createPOPGUID(Def.FOLDER_INBOX_ID, uid);
                    if (uid != null){
                        uids.add(new SyncItemInfoInbox(index, uid,
                                new SyncItemKey(guid), crc, flags));
                    }
                }

            }

        } catch (MessagingException e) {
            throw new InboxListenerException(e.getMessage(), e);
        } catch (EmailAccessException e) {
            throw new InboxListenerException("Error checking mail server " +
                    "accessibility", e);
        }

        return uids;

    }


    /**
     *
     */
    public List<SyncItemInfo> getNewEmails(LinkedHashMap uids, String UIDV)
     throws InboxListenerException {

        String protocol = this.msa.getMailServer().getProtocol();

        ArrayList<SyncItemInfo> allSyncItemInfo = new ArrayList<SyncItemInfo>();

        String GUID  = null;
        String uid   = null;
        int index    = -1;
        Message msg  = null;
        SyncItemInfo syncItemInfo = null;

        try {

            Iterator keys = uids.keySet().iterator();
            while (keys.hasNext()) {

                // get Message from mail server
                uid   = (String)keys.next();
                index = ((Integer)uids.get(uid)).intValue();

                GUID  = Utility.createGUID(protocol, uid, UIDV);

                // get the email
                if (protocol.equals(Def.PROTOCOL_IMAP)){
                    msg = getEmailFromUID((IMAPFolder)this.inbox, index);
                } else {
                    msg = getEmailFromUID((POP3Folder)this.inbox, index);
                }

                // create info from message
                if (msg != null){

                    long    totTimeStart = 0;
                    long    totTimeStop  = 0;
                    // this method can return null if the email is a SentEmail
                    if (log.isTraceEnabled()) {
                        log.trace("Create local info for Email with id: " + GUID);
                    }
                    totTimeStart = System.currentTimeMillis();
                    syncItemInfo = createNewInfo(msg, GUID);                    
                    totTimeStop = System.currentTimeMillis();
                    if (log.isTraceEnabled()) {
                        log.trace("Created local info for Email with id: " + GUID +
                                  ", execution time: " + (totTimeStop - totTimeStart)  +
                                  " ms.");
                    }
                    
                    if (syncItemInfo != null){
                        // insert in the list if there is some condiftion
                        if (!isMessageIDPresent(syncItemInfo.getMessageID(), allSyncItemInfo)){
                            allSyncItemInfo.add(syncItemInfo);
                        }
                    }
                }
            }

        } catch(MessagingException me){
            throw new InboxListenerException("Error creating SyncItemInfo", me);
        }

        return allSyncItemInfo;

    }

    //---------------------------------------------------------- Private Methods

    /**
     *  open the inbox folder
     */
    private void openInboxFolder() throws MessagingException {
        // open inbox folder
        String inboxName = msa.getMailServer().getInboxPath();
        if (inboxName == null){
            throw new MessagingException("Icorrect Inbox Folder Name");
        }
        this.inbox = this.mstools.getStore().getFolder(inboxName);
        if (!inbox.exists()){
            throw new MessagingException("Inbox Folder doesn't exists");
        }
        this.inbox.open(Folder.READ_ONLY);
    }

    /**
     *  close the inbox folder
     */
    private void closeInboxFolder() throws MessagingException {
        if (inbox != null){
            if (inbox.isOpen()){
                inbox.close(true);
            }
        }
    }

    /**
     *
     * create the info that will be insert in the DB
     *
     * @param message Message
     * @param localInfos map with all the loacal SyncItemInfo
     * @return SyncItemInfo
     * @throws MessagingException
     */
    private SyncItemInfo createNewInfo(Message message, String GUID)
    throws MessagingException {

        SyncItemInfo syncItemInfo    = null;

        long         lastCRC         = 0;
        String       messageID       = null;
        Date         headerDate      = null;
        Date         received        = null;
        String       subject         = null;
        String       sender          = null;
        
        String userAddress = this.msa.getMsAddress();
        String type        = this.msa.getMailServer().getMailServerType();
        String protocol    = this.msa.getMailServer().getProtocol();

        boolean skipMsg = false;

        if (Def.SERVER_TYPE_GMAIL.equals(type) && 
            Def.PROTOCOL_POP3.equals(protocol) && 
            isSentEmail(userAddress, message, GUID)) {
            
            skipMsg = true;
        }

        if (isCorruptedEmail(message, GUID)){
            skipMsg = true;
        }

        if (!skipMsg){

            // create the new values
            // Message-ID
            try {
                messageID  = Utility.getHeaderMessageID(message);
            } catch (Exception e){
                log.error("Error parsing creating Message-ID for Caching System ", e);
            }
            // Date
            try {
                headerDate = UtilityDate.getDateForTimeFilter(message, null);
            } catch (Exception e){
                log.error("Error parsing creating new Date for Caching System ", e);
            }
            // CRC
            try {
                String flagList = Utility.createFlagsList(message);
                lastCRC = Utility.createCRC(flagList, protocol);
            } catch (Exception e){
                log.error("Error parsing creatingCRC for Caching System ", e);
            }
            // Received
            try {
                received   = UtilityDate.getHeaderReceived(message, null);
            } catch (Exception e){
                log.error("Error parsing creating Received for Caching System ", e);
            }
            // Subject
            try {
                if (this.saveSubject){
                    subject    = Utility.getHeaderSubject(message);
                } else {
                    subject    = "";
                }
            } catch (Exception e){
                log.error("Error parsing creating Subject for Caching System ", e);
            }
            // Sender
            try {
                if (this.saveSender){
                    sender = Utility.getHeaderSender(message);
                } else {
                    sender = "";
                }
            } catch (Exception e){
                log.error("Error parsing creating Sender for Caching System ", e);
            }


            syncItemInfo = new SyncItemInfo(new SyncItemKey(GUID), lastCRC, messageID, headerDate,
                    received, subject, sender, null, null, "Y", "N");

        }

        return syncItemInfo;

    }
    
    

    /**
     * the message is a sent email if the userAddress is in the From list
     * and is not in the To list
     *
     * @param userAddress String
     * @param message Message
     * @return true if is a sent email
     */
    private boolean isSentEmail(String userAddress, Message message, String GUID)
    throws MessagingException {

        boolean isSentEmail = false;
        boolean isInFROM    = false;
        boolean isInTO      = false;
        int index = -1;

        String sbj = "sorry, subject NOT available!";

        Address[] froms = null;
        try {
            froms = message.getFrom();
        } catch (Exception e) {
            //
            // An exception is thrown when, for example, when a non accettable
            // string is specified as from address.
            //            
        }
        
        if (froms != null){
            String from = "";
            index = -1;
            for (int i=0; i<froms.length; i++){
                from  = froms[i].toString();
                index = from.indexOf(userAddress);
                if (index != -1){
                    isInFROM = true;
                    break;
                }
            }
        } else {
            // if there is somethig wrong .. anyways it returns the email
            try {
                sbj = Utility.getHeaderSubject(message);
            } catch (Exception e) {
                // do nothing
            }
            log.error("Error checking FORM list for the email: " + sbj);
            isInFROM = true;
        }
        
        Address[] tos = null;
        try {
            tos = message.getRecipients(Message.RecipientType.TO);
        } catch (Exception e) {
            //
            // An exception is thrown when, for example, an "Undiclosed recipient"
            // is specified as recipient.
            //
        }

        if (tos != null){
            String to = "";
            index = -1;
            for (int i=0; i<tos.length; i++){
                to = tos[i].toString();
                index = to.indexOf(userAddress);
                if (index != -1){
                    isInTO = true;
                }
            }
        } else {
            //
            // if there is somethig wrong (tos is null or exception is thrown
            // while reading recipients)..anyways it returns the email
            //
            try {
                sbj = Utility.getHeaderSubject(message);
            } catch (Exception e) {
                // do nothing
            }
            log.error("Error checking TO list for the email: " + sbj);
            isInTO = false;
        }

        if (isInFROM && !isInTO){
            isSentEmail = true;
            if (log.isTraceEnabled()) {
                log.trace("Email with id: " + GUID + " is a sent email");
            }
        }

        return isSentEmail;
    }

    /**
     * the message can be all the part empty. it's a problem happened with
     * the Zimbra Mail Server
     *
     * @param message Message
     * @return true if is a corrupted email
     */
    private boolean isCorruptedEmail(Message message, String GUID)
    throws MessagingException {

        boolean isCorruptedEmail = false;
        String from    = null;
        String to      = null;
        String id      = null;
        String date    = null;
        String subject = null;
        try {
            from    = Utility.getHeaderSender(message);      // ok returns null
            to      = Utility.getHeaderReceiver(message);    // ok returns null
            id      = Utility.getHeaderMessageID(message);   // ok returns null
            date    = Utility.getHeaderDateForCRC(message);  //    returns ""
            if (date != null){
                if (date.equals("")){
                    date = null;
                }
            }
            subject = Utility.getHeaderSubject(message);   // ok returns null
            if (from    == null &&
                    to      == null &&
                    id      == null &&
                    subject == null &&
                    date    == null ){
                isCorruptedEmail = true;
            }
        } catch (Exception e){
            // do nothing
        }
        
        if (isCorruptedEmail){
            if (log.isTraceEnabled()) {
                log.trace("Email with id: " + GUID + " is corrupted");
            }
        }
        
        return isCorruptedEmail;

    }

    /**
     *
     */
    private boolean isMessageIDPresent(String messageID,
            List allSyncItemInfo) {

        String messageIDTmp = null;
        boolean check = false;

        if (messageID != null) {
            for (int i=0; i<allSyncItemInfo.size(); i++){
                messageIDTmp = ((SyncItemInfo)allSyncItemInfo.get(i)).getMessageID();
                if (messageID.equals(messageIDTmp)){
                    check = true;
                    break;
                }
            }
        }

        return check;
    }


    /**
     * serch a document using UID; for IMAP4 protocol UID is long
     *
     * @param f   IMAP Folder
     * @param uid email imap id
     * @return Message
     * @throws EntityException
     */
    protected Message getEmailFromUID(IMAPFolder f, int index)
    throws MessagingException {
        Message msg;
        try {
            msg = f.getMessage(index);
        } catch (MessagingException me) {
            throw new MessagingException("Error getting the message.", me);
        }
        return msg;
    }

    /**
     * serch a document using index; for POP3 protocol
     *
     * @param f   IMAP Folder
     * @param uid email imap id
     * @return Message
     * @throws EntityException
     */
    private Message getEmailFromUID(POP3Folder f, int index)
    throws MessagingException {
        Message msg;
        try {
            msg = f.getMessage(index);
        } catch (MessagingException me) {
            throw new MessagingException("Error getting the message.", me);
        }
        return msg;
    }


    /**
     *
     */
    private long createCrcFromFlags(Message message){

        StringBuffer flagString = new StringBuffer();

        try {
            flagString.append("seen: " + message.isSet(Flags.Flag.SEEN));
            flagString.append("answered: " + message.isSet(Flags.Flag.ANSWERED));
            flagString.append("recent: " + message.isSet(Flags.Flag.RECENT));
            flagString.append("user: " + message.isSet(Flags.Flag.USER));
            flagString.append("draft: " + message.isSet(Flags.Flag.DRAFT));
            flagString.append("deleted: " + message.isSet(Flags.Flag.DELETED));
            flagString.append("flagged: " + message.isSet(Flags.Flag.FLAGGED));
        } catch (MessagingException me){
            // do nothing
        }

        if (log.isTraceEnabled()) {
            log.trace("Flags " + flagString.toString());
        }

        return 0;
    }
}
