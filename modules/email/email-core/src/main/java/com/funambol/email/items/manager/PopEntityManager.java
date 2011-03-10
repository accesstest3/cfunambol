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
package com.funambol.email.items.manager ;


import com.funambol.email.engine.source.EmailSyncSource;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.SendingException;
import com.funambol.email.items.dao.PopEntityDAO;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.transport.PopMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.common.pim.common.Property;
import com.funambol.email.model.FlagProperties;
import com.funambol.framework.core.AlertCode;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.RefusedItemException;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.sun.mail.pop3.POP3Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.mail.internet.MimeMessage;
import org.apache.commons.lang.StringUtils;


/**
 * @version $Id: PopEntityManager.java,v 1.5 2008-06-03 09:25:40 testa Exp $
 */
public class PopEntityManager extends EntityManagerFilter {

    // -------------------------------------------------------------- Properties

    /**     */
    public PopMailServerWrapper pmsw = null;

    /**     */
    public PopEntityDAO ped = null;

    // ------------------------------------------------------------ Costructors


    /**
     *
     */
    public PopEntityManager() {
    }

    /**
     * @param ess EmailSyncSource
     * @throws EmailAccessException
     */
    public PopEntityManager(EmailSyncSource ess)
      throws EmailAccessException {
        pmsw       = (PopMailServerWrapper) ess.getMSWrapper();
        serverType = ess.getMailServerAccount().getMailServer().getMailServerType();
        try {
            ped        = new PopEntityDAO(serverType);
        } catch (EntityException e) {
            throw new EmailAccessException("Errot creating the PopEntityDAO", e);
        }
    }

    // ---------------------------------------------------------- Public Methods


    /**
     *
     * In the pop3 protocol this method is used only for outbox mail
     * this mails will be add to the Local DB and then sent.
     * in this version we have to understand if it's really usefull save the 
     * email in the localDB. 
     *
     * @param FID  parent folder id
     * @param emailToAdd    Email
     * @param idSentSpace manager for the id creation IdSpaceGenerator
     * @param saveOnlyHeader true if all the email is saved in the local DB
     * @param funSignature  String
     * @param from  String
     * @param firstname  String
     * @param lastname  String
     * @param source_uri  String
     * @param principalId long
     * @return Email
     * @throws EntityException, SendingException
     */
    public Email addEmail(String FID,
                          String LUID,
                          Email emailToAdd,
                          Map serverItems,
                          DBIDGenerator idSentSpace,
                          boolean saveOnlyHeader,
                          String funSignature,
                          EmailFilter filter,
                          String from,
                          String firstname,
                          String lastname,
                          String source_uri,
                          long principalId,
                          String username)
      throws EntityException, SendingException, RefusedItemException {

        if (log.isTraceEnabled()) {
            log.trace("PopEmail insertEmail start");
        }

        // when a message is sent it will be save as a sent email in the local DB
        FID = Def.FOLDER_SENT_ID;

        // get flags
        FlagProperties fp = Utility.getFlags(emailToAdd);

        if (emailToAdd != null && emailToAdd.getEmailItem() != null &&
                Def.ENCODE_QUOTED_PRINTABLE.equals(emailToAdd.getEmailItem().getEncoding())) {
            try {
                emailToAdd.getEmailItem().setPropertyValue(
                        Utility.decode(emailToAdd.getEmailItem().getStringRFC2822(),
                                Def.ENCODE_QUOTED_PRINTABLE));
                emailToAdd.getEmailItem().setEncoding(null);
            }
            catch (MessagingException e) {
                throw new EntityException(e);
            }
        }

        try {

            Message msg = createMessage(this.pmsw.getSession(), emailToAdd, FID, false, fp);

            if (msg != null){

                //String messageID = Utility.getHeaderMessageID(msg);

                checkIfItHasToBeRefused(msg);

                Message newMsg = null;
                try {
                   newMsg = sendEmail(msg, this.pmsw.getSession(), funSignature,
                                     from, firstname, lastname);
                } catch (SendingException se) {
                   throw new SendingException(se);
                }

                /*
                if (newMsg != null){
                    if (messageID != null){
                        newMsg.setHeader(Def.HEADER_MESSAGE_ID,messageID);
                    }
                    newMsg.setFlag(Flags.Flag.SEEN, true);
                } else {
                    if (messageID != null){
                        msg.setHeader(Def.HEADER_MESSAGE_ID,messageID);
                    }
                }
                
                if (filter.getIsSentActive()){

                   // the SENT folder could be disable
                   // if it's enabled the system adds the item into local DB.
                   //
                   // in the local db we save the header only (for privacy)
                   // the parameter 'saveOnlyHeader' will set in the
                   // SyncConnectorConfigPanel

                   String GUID = null;

                   if (saveOnlyHeader){
                      Message msgForDB= createMessageForDB(msg, from,
                                                           firstname, lastname);
                      GUID = ped.addSentEmail(FID, messageID, msgForDB,  idSentSpace,
                                                     source_uri, principalId, username);
                   } else {
                      GUID = ped.addSentEmail(FID, messageID, msg,  idSentSpace,
                                                     source_uri, principalId, username);
                   }

                   GUID = Utility.convertSentIDToOutboxID(GUID);

                   emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));

                   emailToAdd.setUID(new Property(GUID));

                    // create the object to add in db and in serverItems
                    SyncItemInfo sii = createInfo(GUID, msg, null, Def.PROTOCOL_POP3);

                    // add email to the servetItems list
                    ped.addItemInServerItems(sii, serverItems);

                   return emailToAdd;

                } else {

                   // don't save the email in the sent
                   emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));

                   String GUID = Utility.createPOPGUID(Def.FOLDER_OUTBOX_ID,
                                                       String.valueOf(LUID));

                   emailToAdd.setUID(new Property(GUID));

                   return emailToAdd;

                }
               */ 
                
                // @todo review the save email in the POP mode
               emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));
               String GUID = Utility.createPOPGUID(Def.FOLDER_OUTBOX_ID, String.valueOf(LUID));
               emailToAdd.setUID(new Property(GUID));
               return emailToAdd;

            }

        //} catch (MessagingException e) {
        //    throw new EntityException("Error settings Message-ID", e);
        } catch (EntityException e) {
            throw new EntityException(e);
        }  catch (SendingException e) {
            throw new SendingException(e);
        }

        return emailToAdd;

    }

    /**
     * clean all folder and email except the 5 main folders
     *
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void clearAllItems(String source_uri, long principalId)
            throws EntityException {

        POP3Folder f = null;
        if (log.isTraceEnabled()) {
            log.trace("Pop3Email Delete all emails in Inbox Folder");
        }

        try {

            // we can remove only the inbox folder email
            f = this.pmsw.getInboxFolder();
            timeStart = System.currentTimeMillis();
            f.open(javax.mail.Folder.READ_WRITE);
            this.ped.removeAllEmail(f);

            // the local sent messages is already deleted.

            // we can't remove all folder

        } catch (MessagingException me) {
            throw new EntityException(me);
        } catch (EmailAccessException e) {
            throw new EntityException(e);
        } finally {
            if (f != null) {
                try {
                    if (f.isOpen()) {
                        f.close(true);
                        timeStop = System.currentTimeMillis();
                        if (log.isTraceEnabled()) {
                            log.trace("insertItem Execution Time: " +
                                    (timeStop - timeStart) + " ms");
                        }
                    }
                } catch (MessagingException me) {
                    throw new EntityException(me);
                }
            }
        }
    }


    /**
     * deletes an email
     *
     * @param parentId  folder id
     * @param mailId  mail id in the folder.
     * @param GUID  GUID in the ds-server.
     * @param serverItems map with all CrcSyncItemInfo
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void removeEmail(EmailFilter filter,
                            String parentId,
                            String mailId,
                            String GUID,
                            Map serverItems,
                            String username,
                            String source_uri,
                            long principalId)
      throws EntityException {


        if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {

            int threshold_1 = filter.getMaxInboxNum();
            int threshold_2 = (filter.getMaxInboxNum() + 40);
            int max = filter.getMaxInboxNum();

            try {

                timeStart = System.currentTimeMillis();

                Message msg = ped.getItem(mailId, max, threshold_1, threshold_2, this.pmsw);

                if (msg != null) {

                    // remove from Mail Server
                    ped.removeEmail(msg);

                    // remove from Inbox Cache Table
                    ped.removeEmailInDB(username, Def.PROTOCOL_POP3, GUID);

                    // remove serverItemsList
                    ped.removeItemInServerItems(GUID, serverItems);

                }

                timeStop = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    log.trace("removeItem Execution Time: " +
                            (timeStop - timeStart) + " ms");
                }

            } catch (EntityException e) {
                throw new EntityException(e);
            }

        }


        if (parentId.equalsIgnoreCase(Def.FOLDER_SENT_ID) ||
            parentId.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)) {
            try {
                ped.removeEmail(GUID, source_uri, principalId, username);
            } catch (Exception me) {
                throw new EntityException(me);
            }
        }

    }

    /**
     * deletes an email
     *
     * @param parentId  folder id
     * @param GUID  GUID in the ds-server.
     * @param username String
     * @throws EntityException
     */
    public void removeEmailSoft(String parentId,
                                String GUID,
                                String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("remove item soft in FID: " + parentId + " with FMID: " + GUID);
        }

        if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {
            try {
                timeStart = System.currentTimeMillis();
                ped.setDeletedEmailInInbox(username, Def.PROTOCOL_POP3, GUID);
                timeStop = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    log.trace("removeItemSoft Execution Time: " +
                              (timeStop - timeStart) + " ms");
                }

            } catch (EntityException e) {
                throw new EntityException(e);
            }
        }

    }


    /**
     * @param filter EmailFilter
     * @param username String
     * @param maxEmailNumber int
     * @param source_uri String
     * @param principalId long
     * @return map with all the mail server SyncItemInfo
     * @throws EntityException
     */
    public Map getAllEmailsInfo(EmailFilter filter,
                                String username,
                                int maxEmailNumber,
                                int syncMode,
                                String source_uri,
                                long principalId)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getAllSyncItemInfo Start");
        }

        Map syncItemInfos = null;
        Map iInfos        = null;
        Map sInfos        = null;

        try {

            iInfos = getAllEmailsInfoInbox(username, maxEmailNumber);

            if        (syncMode == AlertCode.SLOW){
                // don't get the sent items
            } else if (syncMode == AlertCode.REFRESH_FROM_SERVER){
                // don't get the sent items
            } else {
                // send the sent items just if the sent folder is selected
                if (filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                    filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                    filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                       sInfos = getAllEmailsInfoSent(filter, source_uri, principalId, username);
                }

            }

        } catch (Exception e) {
            throw new EntityException("Error Getting all emails info (" +
                    e.getMessage() + ")", e);
        }

        syncItemInfos = Utility.mergeMap(iInfos, null, null, sInfos, null);

        if (log.isTraceEnabled()) {
            log.trace("getAllSyncItemInfo List: " + syncItemInfos.size() + " Document");
        }

        return syncItemInfos;

    }

    /**
     * the pop-like connector gets only the default folders
     * (inbox, outbox, sent)
     *
     * @return The info of the syncitems
     * @throws EntityException
     */
    public LinkedHashMap getAllFoldersInfo(EmailFilter filter)
     throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getAllFolderInfo Start");
        }

        LinkedHashMap syncItemInfos = new LinkedHashMap();
        SyncItemInfo info  = null;

        String[] inbox  = null;
        String[] outbox = null;
        String[] sent   = null;

        int folderFilter = filter.getFolder();

        if (folderFilter == Def.FILTER_FOLDER_I ||
            folderFilter == Def.FILTER_FOLDER_IO ||
            folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            info = new SyncItemInfo(new SyncItemKey(Def.FOLDER_INBOX_GUID), 0,
                                       null,null, null,null, null,
                                       null,null, null, null);
            syncItemInfos.put(Def.FOLDER_INBOX_GUID, info);

        }
        if (folderFilter == Def.FILTER_FOLDER_IO ||
            folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            info = new SyncItemInfo(new SyncItemKey(Def.FOLDER_OUTBOX_GUID), 0,
                                       null,null, null,null, null,
                                       null,null, null, null);
            syncItemInfos.put(Def.FOLDER_OUTBOX_GUID, info);

        }
        if (folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            info = new SyncItemInfo(new SyncItemKey(Def.FOLDER_SENT_GUID), 0,
                                       null,null, null,null, null,
                                       null,null, null, null);
            syncItemInfos.put(Def.FOLDER_SENT_GUID, info);

        }

        return syncItemInfos;

    }

   /**
    *
    * checks if an email is already inserted in the db
    * and returns the GUID
    * we use the header Message-ID as search criteria
    *
    * @param session mail server session
    * @param parentId parent if of the email
    * @param email Email
    * @param source_uri String
    * @param principalId long
    * @return GUID String
    * @throws EntityException
    */
   public String getEmailFromClauseInDB(Session session,
                                        String parentId,
                                        Email email,
                                        String source_uri,
                                        long principalId,
                                        String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getItemByWhereClause start ");
        }

        String      GUID         = null;
        ItemMessage im           = null;
        String      messageIdExt = null;
        Message     msgExt       = null;


        try {

            FlagProperties fp = Utility.getFlags(email);

            msgExt = createMessage(this.pmsw.getSession(), email,  parentId, false, fp);

            // get the Message-ID
            messageIdExt = Utility.getHeaderMessageID(msgExt);

            if (messageIdExt != null) {
                im = ped.getEmailFromMessageID(messageIdExt,
                                               this.pmsw.getSession(),
                                               source_uri,
                                               principalId,
                                               username);
                if (im != null){
                   GUID = im.getGUID();
                }

            }

        } catch (Exception e) {
            throw new EntityException(e);
        }

        return GUID;
    }

    /**
     *
     * checks if a mail match the filter
     * @param email  Email
     * @param filter EmailFilter
     * @param GUID String
     * @param FID String
     * @param FMID String
     * @param status
     * @param source_uri
     * @param principalId
     * @return true if the message match the filter
     * @throws EntityException
     */
    public boolean isEmailInFilter(Email email,
                                   EmailFilter filter,
                                   String GUID,
                                   String FID,
                                   String FMID,
                                   char status,
                                   String source_uri,
                                   long principalId,
                                   String username)
            throws EntityException {


        if (log.isTraceEnabled()) {
            log.trace("isEmailInFilter in folder " + FID +
                       " GUID: " + GUID + " FMID " + FMID);
        }

        boolean isInFilter = false;

        try {

            // get email if null
            if (email == null){
                email = this.getEmailFromUID(filter, FID, FMID, status,
                                             source_uri, principalId, 
                                             username, new ContentProviderInfo("","",""));
            }

            isInFilter = super.isEmailInFilter(email, filter,
                                               GUID, FID, FMID,
                                               this.pmsw.getSession(),
                                               this.pmsw.getLocale(),
                                               source_uri,principalId);


        } catch (Exception e) {
            throw new EntityException(e);
        }

        return isInFilter;
    }


    /**
     * Get the email; called by the SyncSource
     *
     * @param filter      EmailFilter
     * @param parentId    id parent folder (folderid / mailid)
     * @param mailId      mail id (folderid / mailid)
     * @param status      status of the item; char
     * @param source_uri  String
     * @param principalId long
     * @return Email
     * @throws EntityException
     */
    public Email getEmailFromUID(EmailFilter filter,
                                 String parentId,
                                 String mailId,
                                 char status,
                                 String source_uri,
                                 long principalId,
                                 String username,
                                 ContentProviderInfo contentProviderInfo)
       throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getItemByUID in folder " + parentId + " mailid " + mailId);
        }

        Email       emailOut = null;
        ItemMessage im       = null;

        // go in the Mail Server
        if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {
            im = this.getEmailFromUIDInbox(parentId, mailId, filter, status,
                                           source_uri, principalId, username,
                                           contentProviderInfo);
        }

        // go in the local DB
        if (parentId.equalsIgnoreCase(Def.FOLDER_SENT_ID) ||
            parentId.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)) {
            im = this.getEmailFromUIDSent(parentId, mailId,
                                          source_uri, principalId, username);
        }

        if (im != null) {
            try {
                emailOut = createFoundationMail(im);
            } catch (Exception e) {
                throw new EntityException(e);
            }
        }

        return emailOut;

    }

    /**
     * the method doesn't update the item but get the item that must be return
     *
     * @param FID  folder id
     * @param FMID  folder id
     * @param emailToUpdate  Email Object
     * @param source_uri  String
     * @param principalId long
     * @return Email
     * @throws EntityException
     */
    public Email updateEmail(String FID,
                             String FMID,
                             Email emailToUpdate,
                             String source_uri,
                             long principalId)
     throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("updateEmail start FID " + FID + " FMID " + FMID);
        }

        String GUID = Utility.createPOPGUID(FID, FMID);
        emailToUpdate.setUID(new Property(GUID));

        // The pop3 protocol cannot change the flag (i.e. read) of an email in
        // the inbox folder. the system doesn't update the email into the
        // servetItems list. see the IMAP implementation

        return emailToUpdate;

    }


    /**
     * updates a folder defined by folder id and parent id
     *
     * @param parentId  folder id String
     * @param folderId  mail id in folder String
     * @param folderNew Folder
     * @return Folder
     * @throws EntityException
     */
    /*
    public com.funambol.email.pdi.folder.Folder updateFolder(String parentId,
                                                             String folderId,
                                                             Folder folderNew)
      throws EntityException {

        com.funambol.email.pdi.folder.Folder folderOut = null;

        if (log.isTraceEnabled()) {
            log.trace("updateFolder start for FID: " + folderId);
        }

        String name       = folderNew.getName().getPropertyValueAsString();
        String created    = folderNew.getCreated().getPropertyValueAsString();
        String modified   = folderNew.getModified().getPropertyValueAsString();
        String accessed   = folderNew.getAccessed().getPropertyValueAsString();
        String attributes = null;

        ItemFolder ifout = null;

        if        (folderId.equals(Def.FOLDER_INBOX_ID)){
            ifout =  new ItemFolder(Def.FOLDER_INBOX_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_INBOX_ROLE,
                                    attributes);
        } else if (folderId.equals(Def.FOLDER_OUTBOX_ID)){
            ifout =  new ItemFolder(Def.FOLDER_OUTBOX_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_OUTBOX_ROLE,
                                    attributes) ;
        } else if (folderId.equals(Def.FOLDER_SENT_ID)){
            ifout =  new ItemFolder(Def.FOLDER_SENT_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_SENT_ROLE,
                                    attributes) ;
        } else if (folderId.equals(Def.FOLDER_DRAFTS_ID)){
            ifout =  new ItemFolder(Def.FOLDER_DRAFTS_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_DRAFTS_ROLE,
                                    attributes) ;
        } else if (folderId.equals(Def.FOLDER_TRASH_ID)){
            ifout =  new ItemFolder(Def.FOLDER_TRASH_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_TRASH_ROLE,
                                    attributes) ;

        }

        // convert Folder in foundation Folder
        try {
            folderOut = createFoundationFolder(ifout);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("updateFolder end");
        }

        return folderOut;
    }
    */

    /**
     * @param GUID        folder id
     * @param source_uri  String
     * @param principalId long
     * @return Folder
     * @throws EntityException
     */
    public com.funambol.email.pdi.folder.Folder getFolderFromUID(String GUID,
                                                                 String source_uri,
                                                                 long principalId)
            throws EntityException {


        if (log.isTraceEnabled()) {
            log.trace("getFolderByUID start " + GUID);
        }

        com.funambol.email.pdi.folder.Folder folderOut;
        ItemFolder itemFolder = null;
        String creationDate;


        try {

            creationDate = Utility.getFolderCreationDate();

            if (GUID.equalsIgnoreCase(Def.FOLDER_INBOX_GUID)) {
                itemFolder = new ItemFolder(GUID,
                        this.pmsw.getDefaultFolder().getInboxName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_INBOX_ROLE,
                        null);
            } else if (GUID.equalsIgnoreCase(Def.FOLDER_OUTBOX_GUID)) {
                itemFolder = new ItemFolder(GUID,
                        this.pmsw.getDefaultFolder().getOutboxName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_OUTBOX_ROLE,
                        null);

            } else if (GUID.equalsIgnoreCase(Def.FOLDER_SENT_GUID)) {
                itemFolder = new ItemFolder(GUID,
                        this.pmsw.getDefaultFolder().getSentName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_SENT_ROLE,
                        null);
            }

            folderOut = createFoundationFolder(itemFolder);

        } catch (Exception e) {
            throw new EntityException(e);
        }

        return folderOut;

    }

    /**
     * @param name        folder name
     * @param parentId    parent folder id
     * @param source_uri  String
     * @param principalId long
     * @return Folder
     * @throws EntityException
     */
    public com.funambol.email.pdi.folder.Folder getFolderFromName(String name,
                                                                  String parentId,
                                                                  String source_uri,
                                                                  long principalId)
            throws EntityException {


        if (log.isTraceEnabled()) {
            log.trace("getFolderByName start " + name + " FID " + parentId);
        }

        com.funambol.email.pdi.folder.Folder folderOut = null;
        ItemFolder itemFolder = null;
        String creationDate;

        /**
         * we should add some control.
         * We can use the device information
         */
        String nametmp = Utility.convertNokiaToDefault(name,
                this.pmsw.getDefaultFolder());
        // it is a nokia folder
        if (nametmp != null) {
            name = nametmp;
        }

        try {

            creationDate = Utility.getFolderCreationDate();

            if (name.equalsIgnoreCase(this.pmsw.getDefaultFolder().getInboxName())) {
                itemFolder = new ItemFolder(Def.FOLDER_INBOX_GUID,
                        this.pmsw.getDefaultFolder().getInboxName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_INBOX_ROLE,
                        null);
            } else if (name.equalsIgnoreCase(this.pmsw.getDefaultFolder().getOutboxName())) {
                itemFolder = new ItemFolder(Def.FOLDER_OUTBOX_GUID,
                        this.pmsw.getDefaultFolder().getOutboxName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_OUTBOX_ROLE,
                        null);

            } else if (name.equalsIgnoreCase(this.pmsw.getDefaultFolder().getSentName())) {
                itemFolder = new ItemFolder(Def.FOLDER_SENT_GUID,
                        this.pmsw.getDefaultFolder().getSentName(),
                        Def.FOLDER_ROOT_ID,
                        creationDate,
                        null,
                        null,
                        Def.FOLDER_SENT_ROLE,
                        null);
            }

            if (itemFolder != null) {
                try {
                    folderOut = createFoundationFolder(itemFolder);
                } catch (Exception e) {
                    throw new EntityException(e);
                }
            }

        } catch (Exception e) {
            throw new EntityException(e);
        }


        return folderOut;

    }

    /**
     * Used in the stardard process (called by PopEntityManager.getEmailFormUID)
     *
     * @param FID         parent folder id
     * @param FMID        mail id (in the folder, not unique)
     * @param filter      EmailFilter
     * @param status      status of the item; char
     * @param source_uri  String
     * @param principalId long
     * @return ItemMessage
     * @throws EntityException
     */
    private ItemMessage getEmailFromUIDInbox(String FID,
                                             String FMID,
                                             EmailFilter filter,
                                             char status,
                                             String source_uri,
                                             long principalId,
                                             String username,
                                             ContentProviderInfo contentProviderInfo)
     throws EntityException {

        ItemMessage im = null;
        int threshold_1 = 20;
        int threshold_2 = 40;
        int max = filter.getMaxInboxNum();

        try {

            if (this.pmsw.folderInboxOpened == null){
                 this.pmsw.folderInboxOpened =
                    (POP3Folder) this.pmsw.getMailDefaultFolder().getFolder(Def.FOLDER_INBOX_ENG);
            }

            long tStart = System.currentTimeMillis();
            if (!this.pmsw.folderInboxOpened.isOpen()){
               this.pmsw.folderInboxOpened.open(javax.mail.Folder.READ_WRITE);
            }
            long tStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("getEmailInboxFromUID opening pop3 folder Execution Time: " +
                        (tStop - tStart) + " ms");
            }


            // optimization
            tStart = System.currentTimeMillis();
            Message msg = ped.getItem(FMID, max, threshold_1, threshold_2, this.pmsw);
            tStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("getEmailInboxFromUID getting message Execution Time: " +
                        (tStop - tStart) + " ms");
            }

            // handling the filtering error in a different way
            // in order to avoid to analize the same mail in the next sync session
            try {
                tStart = System.currentTimeMillis();
                im = this.getPart(this.pmsw.folderInboxOpened,
                                  FMID, filter, msg, status,
                                  source_uri, principalId, username,
                                  contentProviderInfo);
                tStop = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    log.trace("getEmailInboxFromUID filtering single mail Execution Time: " +
                            (tStop - tStart) + " ms");
                }
            } catch (EntityException e) {
                throw new EntityException(Def.ERR_FILTERING_EMAIL + " " + e.getMessage());
            }

            // The incoming message is logged if and only if the server is going
            // to sent back to the client the entire content of the email.
            // Skipping the scenario in which only flags are sent (update)
            if(im!=null &&StringUtils.isNotEmpty(im.getStreamMessage())) {
                logIncomingMessage(msg);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        }

        return im;

    }

    /**
     * @param FID         String
     * @param FMID        String
     * @param filter      EmailFilter
     * @param source_uri  String
     * @param principalId long
     * @return ItemMessage
     * @throws EntityException
     */
    private ItemMessage getEmailFromUIDSent(String FID,
                                            String FMID,
                                            String source_uri,
                                            long principalId,
                                            String username)
       throws EntityException {

        ItemMessage im;

        try {
            String GUID = Utility.createPOPGUID(FID, FMID);
            Message msg = ped.getEmailSentFromUID(GUID, this.pmsw.getSession(),
                                                  source_uri, principalId, username);

            FlagProperties fp = Utility.getFlags(msg);
            im = setItemMessage(GUID, FID, msg, fp, this.pmsw.getLocale(), null, null);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        return im;
    }

    /**
     * the pop-like connector gets only the default folders
     * (inbox, outbox, sent)
     *
     * @return the array with the folder ids String[]
     * @throws EntityException
     */
    public String[] getAllFolders(EmailFilter filter) throws EntityException {

        String[] allfolders;

        String[] inbox  = null;
        String[] outbox = null;
        String[] sent   = null;

        int folderFilter = filter.getFolder();

        if (folderFilter == Def.FILTER_FOLDER_I ||
            folderFilter == Def.FILTER_FOLDER_IO ||
            folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            inbox    = new String[1];
            inbox[0] = Def.FOLDER_INBOX_GUID;
        }
        if (folderFilter == Def.FILTER_FOLDER_IO ||
            folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            outbox    = new String[1];
            outbox[0] = Def.FOLDER_OUTBOX_GUID;
        }
        if (folderFilter == Def.FILTER_FOLDER_IOS ||
            folderFilter == Def.FILTER_FOLDER_IOSD ||
            folderFilter == Def.FILTER_FOLDER_IOSDT) {

            sent    = new String[1];
            sent[0] = Def.FOLDER_SENT_GUID;

        }


        allfolders = Utility.mergeArray(inbox, outbox, sent, null, null);

        return allfolders;

    }


    /**
     * get messages from the fnbl_email_inbox
     * this table it's a cache for the email in the inbox folder
     *
     * @param username  String
     * @return map with all the mail server CrcSyncItemInfo
     * @throws EntityException
     */
    private Map getAllEmailsInfoInbox(String username, 
            int maxEmailNumber) throws EntityException {

        Map allSyncItemInfo = null;
        try {
            allSyncItemInfo = ped.getAllEmailsInbox(username, 
                    Def.PROTOCOL_POP3, 
                    maxEmailNumber);
        } catch (EntityException e) {
            throw new EntityException(e);
        }

        return allSyncItemInfo;

    }


    /**
     * get messages from folders
     *
     * @param filter
     * @param source_uri  String
     * @param principalId long
     * @return map with all the mail server CrcSyncItemInfo
     * @throws EntityException
     */
    private LinkedHashMap getAllEmailsInfoSent(EmailFilter filter,
                                               String source_uri,
                                               long principalId,
                                               String username)
      throws EntityException {

        LinkedHashMap allSyncItemInfo = new LinkedHashMap();

        try {

            // get message
            String[] ids = ped.getAllEmailsSent(filter, source_uri, principalId, username);

            String GUID      = null;
            String key       = null;
            int itemsNum     = 0;

            if (ids != null) {
                itemsNum = ids.length;
            }

            SyncItemInfo syncItemInfo = null;

            for (int i = 0; i < itemsNum; i++) {
                syncItemInfo = new SyncItemInfo(new SyncItemKey(ids[i]), 1,
                                                null, null, null,null, null,
                                                null, null, "Y", null);
                allSyncItemInfo.put(ids[i], syncItemInfo);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        }

        return allSyncItemInfo;

    }

    /**
     *
     *
     * @param f           POP3Folder
     * @param FMID        mail id in the folder , not unique
     * @param filter      EmailFilter
     * @param msg         Message
     * @param status      status of the item; char
     * @param source_uri  String
     * @param principalId long
     * @return ItemMessage
     * @throws EntityException
     */
    private ItemMessage getPart(POP3Folder f,
                                String FMID,
                                EmailFilter filter,
                                Message msg,
                                char status,
                                String source_uri,
                                long principalId,
                                String username,
                                ContentProviderInfo contentProviderInfo)
      throws EntityException {

        ItemMessage im = null;
        if (msg != null) {

            try {

                String parentfullpath = f.getFullName();
                if (parentfullpath.equals("INBOX")) {
                    parentfullpath = Def.FOLDER_INBOX_ENG;
                }

                String FID = this.ped.getGUIDFromFullPath(parentfullpath,
                                                          source_uri,
                                                          principalId,
                                                          username);
                Locale loc = this.pmsw.getLocale();

                Session session = this.pmsw.getSession();

                if (filter.getSize() == Def.FILTER_SIZE_H) {
                    im = getH(session, FID, FMID, msg, status, loc, filter, contentProviderInfo);
                } else if (filter.getSize() == Def.FILTER_SIZE_H_B) {
                    im = getHB(session, FID, FMID, msg, status, loc, -1, filter, contentProviderInfo);
                } else if (filter.getSize() == Def.FILTER_SIZE_H_B_PERC) {
                    im = getHB(session, FID, FMID, msg, status, loc, filter.getNumBytes(), filter, contentProviderInfo);
                } else if (filter.getSize() == Def.FILTER_SIZE_H_B_A) {
                    im = getHBA(session, FID, FMID, msg, status, loc, -1, filter, contentProviderInfo);
                } else if (filter.getSize() == Def.FILTER_SIZE_H_B_A_PERC) {
                    im = getHBA(session, FID, FMID, msg, status, loc, filter.getNumBytes(), filter, contentProviderInfo);
                }
            } catch (EntityException me) {
                throw new EntityException(me);
            }

        }
        return im;
    }


    /**
     * This method creates an email with only header to save in
     * the local DB.
     *
     * @param session Mail Server session
     * @param msg complete message
     * @return msgForDB Message with only header
     * @throws EntityException
     */
    private Message createMessageForDB(Message msg,
                                       String from,
                                       String firstname,
                                       String lastname)
      throws EntityException {

        Message msgForDB = null;

        try {

            // save old Message-ID
            String mid = Utility.getHeaderMessageID(msg);

            msgForDB = new MimeMessage(this.pmsw.getSession());

            // header
            MessageCreator.setNewHeaderSent(msgForDB, msg, from, firstname, lastname);

            // default empty content
            msgForDB.setContent(Def.CONTENT_BODY, Def.CONTENT_CONTENTTYPE);

            msgForDB.saveChanges();

            // the save message command change the message-ID
            msgForDB.setHeader(Def.HEADER_MESSAGE_ID, mid);

        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        return msgForDB;

    }

}
