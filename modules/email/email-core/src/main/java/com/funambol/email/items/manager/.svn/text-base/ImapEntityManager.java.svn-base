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
package com.funambol.email.items.manager;


import com.funambol.common.pim.common.Property;
import com.funambol.email.engine.source.EmailSyncSource;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.SendingException;
import com.funambol.email.items.dao.ImapEntityDAO;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.UpdatedMessage;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.transport.ImapMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.email.util.UtilityDate;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.RefusedItemException;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.sun.mail.imap.IMAPFolder;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import javax.mail.FetchProfile;
import javax.mail.Flags;
import org.apache.commons.lang.StringUtils;

/**
 * @version $Id: ImapEntityManager.java,v 1.4 2008-06-03 09:22:51 testa Exp $
 */
public class ImapEntityManager extends EntityManagerFilter {

    // -------------------------------------------------------------- Properties

    /**     */
    public ImapMailServerWrapper imsw = null;

    /**     */
    public ImapEntityDAO ied = null;

    // ------------------------------------------------------------ Costructors

    /**
     *
     */
    public ImapEntityManager() {
    }

    /**
     * @param ess EmailSyncSource
     */
    public ImapEntityManager(EmailSyncSource ess)
     throws EmailAccessException {
        imsw       = (ImapMailServerWrapper) ess.getMSWrapper();
        serverType = ess.getMailServerAccount().getMailServer().getMailServerType();
        try {
            ied = new ImapEntityDAO(serverType);
        } catch (EntityException e) {
            throw new EmailAccessException("Error creating the ImapEntityDAO", e);
        }
    }



    // ---------------------------------------------------------- Public Methods

    /**
     * deletes an email
     *
     * @param parentId String
     * @param mailId String
     * @param GUID String
     * @param serverItems map with all in the Mail Server CrcSyncItemsInfo
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void removeEmail(String parentId,
                            String mailId,
                            String GUID,
                            Map serverItems,
                            String username,
                            String source_uri,
                            long principalId)
            throws EntityException {

        String fullpath = null;

        try {
            fullpath = this.ied.getFullPathFromFID(parentId, source_uri, principalId, username);
        } catch (EntityException e) {
            throw new EntityException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("remove item in FID: " + fullpath + " with FMID: " + mailId);
        }

        if (fullpath != null) {
            if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {
                try {

                    // INBOX

                    timeStart = System.currentTimeMillis();

                    IMAPFolder inbox = this.imsw.folderInboxOpened;
                    long uid = Long.parseLong(mailId);

                    if (!inbox.isOpen()) {
                        inbox.open(javax.mail.Folder.READ_WRITE);
                    }

                    Message message = inbox.getMessageByUID(uid);
                    IMAPFolder trashFolder = this.imsw.getTrashFolder();
                    
                    //
                    // If the trash folder does not exists, then create it.
                    //
                    if (!trashFolder.exists()){
                        
                        if (log.isTraceEnabled()){
                            log.trace("Trash folder does not exist. Creating Trash folder with path "
                                    + trashFolder.getFullName());
                        }
                        
                        if (trashFolder.create(Folder.HOLDS_MESSAGES)) {
                            trashFolder.open(Folder.READ_WRITE);
                        }
                    }
                    
                    if (message != null) {
                        if (message.isExpunged()) {
                            //
                            // GMail mail server sometimes says an email is expunged
                            // even if it has not been previously deleted.
                            //
                            // Workaround:
                            //
                            // - such behaviour seems to appear after the first time
                            //   IMAPFolder.copyMessage method is executed on an open 
                            //   folder.
                            //
                            //   More precisely: the first time method is executed,
                            //   the given message is moved correctly; each of 
                            //   the subsequent execution could work correctly
                            //   or not.
                            // 
                            // - So: reopen the inbox folder (as: "nothing has
                            //   happened"), extract the message, call copyMessage
                            //   with that message.
                            //

                            if (inbox.isOpen()) {
                                //
                                // close folder expunging all emails to be expunged 
                                // in order to reset state of each email
                                //
                                inbox.close(true);
                                inbox.open(Folder.READ_WRITE);
                            }

                            message = ((IMAPFolder) inbox).getMessageByUID(uid);
                        }
                        
                        //
                        // Following check: message != null is needed in the case
                        // message was expunged (see above).
                        //
                        if (message != null) {
                            
                            // if TRASH folder exists, then copy email to TRASH folder
                            if (trashFolder.exists()) {
                                this.ied.copyEmail(inbox, trashFolder, uid);
                            }
                            
                            //
                            // - With some mail servers the copyEmail method
                            //   copies the original message in the destination
                            //   folder and leaves the original one in the source
                            //   one.
                            // - Other mail servers move the message from the
                            //   source folder to the destination folder; thus
                            //   the original message cannot be deleted from the
                            //   source folder, since it has been already deleted. 
                            //
                            // For example: consider the Gmail mail server; when
                            // moving an email from the INBOX folder to the default
                            // trash folder, the copy operation will automatically 
                            // delete and expunge the original message from the
                            // source folder; note also that this behavior does
                            // not happen when destination folder is not the default
                            // trash folder).
                            //
                            if (!message.isExpunged()) {
                                // remove email from inbox folder
                                this.ied.removeEmail(inbox, uid);
                            }
                        }
                    }

                    // remove from Cache Inbox Table
                    this.ied.removeEmailInDB(username, Def.PROTOCOL_IMAP, GUID);

                    // remove serverItemsList
                    this.ied.removeItemInServerItems(GUID, serverItems);

                    timeStop = System.currentTimeMillis();
                    if (log.isTraceEnabled()) {
                        log.trace("removeItem Execution Time: " +
                                (timeStop - timeStart) + " ms");
                    }
                } catch (MessagingException e) {
                    throw new EntityException(e);
                } catch (EntityException e) {
                    throw new EntityException(e);
                } catch (EmailAccessException e) {
                    throw new EntityException(e);
                }
            } else {

                // SENT - OUTBOX - DRAFTS - TRASH

                IMAPFolder f = null;

                EntityException finalExc = null;

                try {

                    timeStart = System.currentTimeMillis();
                    // rest of the folders
                    f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);

                    if (f.exists()) {

                        if (!f.isOpen()) {
                            f.open(Folder.READ_WRITE);
                        }

                        // remove email from mail server
                        this.ied.removeEmail(f, Long.parseLong(mailId));

                        // remove serverItemsList
                        this.ied.removeItemInServerItems(GUID, serverItems);

                    }

                } catch (MessagingException me) {
                    throw new EntityException(me);
                } catch (EntityException e) {
                    throw new EntityException(e);
                } finally {
                    if (f != null) {
                        try {
                            if (f.isOpen()) {
                                f.close(true);
                                timeStop = System.currentTimeMillis();
                                if (log.isTraceEnabled()) {
                                    log.trace("removeItem Execution Time: " +
                                            (timeStop - timeStart) + " ms");
                                }
                            }
                        } catch (MessagingException me) {
                            throw new EntityException(me);
                        }
                    }
                }
            }
        }
    }

    /**
     * deletes an email
     *
     * @param parentId String
     * @param GUID String
     * @param username String
     * @throws EntityException
     */
    public void removeEmailSoft(String parentId,
                                String GUID,
                                String username)
      throws EntityException {


        if (log.isTraceEnabled()) {
            log.trace("remove item soft in FID: " + parentId +
                    " with FMID: " + GUID);
        }

        if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {
           try {
                timeStart = System.currentTimeMillis();
                this.ied.setDeletedEmailInInbox(username, Def.PROTOCOL_IMAP, GUID);
                timeStop = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    log.trace("removeItemSoft Execution Time: " +
                            (timeStop - timeStart) + " ms");
                }
            } catch (EntityException e) {
                throw new EntityException(e);
            }
        } else {
            // @todo
            // do nothing
        }

    }

    /**
     * deletes a Folder
     *
     * @param folderId    String
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void removeFolder(String folderId,
                             Map serverItems,
                             String source_uri,
                             long principalId,
                             String username)
      throws EntityException {

        String fullpath;
        IMAPFolder f;

        if (folderId.equalsIgnoreCase(Def.FOLDER_INBOX_ID) ||
            folderId.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID) ||
            folderId.equalsIgnoreCase(Def.FOLDER_SENT_ID) ||
            folderId.equalsIgnoreCase(Def.FOLDER_DRAFTS_ID) ||
            folderId.equalsIgnoreCase(Def.FOLDER_TRASH_ID)) {

            throw new EntityException("You can't delete this Folder");

        } else {

            try {

                fullpath = this.ied.getFullPathFromGUID(folderId, source_uri, principalId, username);

                if (fullpath != null) {

                    // remove folder from MailServer
                    f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);
                    if (f.exists()) {
                        this.ied.removeFolderInServer(f);
                    }

                    // remove folder from Local DB
                    this.ied.removeFolderInDB(folderId, source_uri, principalId, username);

                    // remove folder from serverItems
                    this.ied.removeItemInServerItems(folderId, serverItems);

                }

            } catch (MessagingException me) {
                throw new EntityException(me);
            }
        }
    }

    /**
     * deletes a Folder
     *
     * @param folderId    String
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void removeFolderSoft(String folderId,
                                 String source_uri,
                                 long principalId)
      throws EntityException {

         // @todo
         // do nothing

    }

    /**
     * updates a document defined by UID
     *
     * @param FID String
     * @param FMID String
     * @param emailToUpdate Email
     * @param source_uri  String
     * @param principalId long
     * @return Email
     * @throws EntityException
     */
    public Email updateEmail(String FID,
                             String FMID,
                             Email emailToUpdate,
                             Map serverItems,
                             String username,
                             String protocol,
                             String source_uri,
                             long principalId)
      throws EntityException {

        String fullpath = null;

        try {
            fullpath = this.ied.getFullPathFromFID(FID, source_uri, principalId, username);
        } catch (Exception me) {
            throw new EntityException(me);
        }

        if (log.isTraceEnabled()) {
            log.trace("updateItem fullpath " + fullpath + "FID: " + FID);
        }

        if (fullpath != null) {

            // the emailItem could be null
            // i.e. the client send only the read property get flags
            FlagProperties fp = Utility.getFlags(emailToUpdate);

            Message msgNew = null;
            if (emailToUpdate.getEmailItem().getStringRFC2822() != null) {
                msgNew = createMessage(this.imsw.getSession(), emailToUpdate, FID, false, fp);
            }

            if (FID.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {

                // INBOX

                try {

                    timeStart = System.currentTimeMillis();

                    if (!this.imsw.folderInboxOpened.isOpen()){
                        this.imsw.folderInboxOpened.open(javax.mail.Folder.READ_WRITE);
                    }

                    // update in the Mail Server
                    UpdatedMessage um = this.ied.updateEmail(FID, FMID,
                                                    this.imsw.folderInboxOpened,
                                                    msgNew, fp);

                    long uid = um.getFMID();

                    long uidv = this.imsw.folderInboxOpened.getUIDValidity();

                    String GUID = Utility.createIMAPGUID(FID,
                                                         String.valueOf(uid),
                                                         String.valueOf(uidv));

                    emailToUpdate.setUID(new Property(GUID));

                    timeStop = System.currentTimeMillis();
                    if (log.isTraceEnabled()) {
                        log.trace("updateItem Execution Time: " +
                                (timeStop - timeStart) + " ms");
                    }

                    // set the values for the post update operation
                    long crc = createInfo(um, protocol);

                    // update the fnbl_email_inbox
                    this.ied.updateCRCInDB(crc, username, protocol, GUID);

                    // update email to the servetItems list
                    this.ied.updateCRCInServerItems(crc, GUID, serverItems);

                } catch (MessagingException me) {
                    throw new EntityException(me);
                } catch (EntityException e) {
                    throw new EntityException(e);
                }

            } else {

                // SENT - DRFATS - TRASH - OUTBOX

                IMAPFolder f = null;

                try {

                    timeStart = System.currentTimeMillis();

                    f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);

                    if (f.exists()) {

                        if (!f.isOpen()){
                           f.open(Folder.READ_WRITE);
                        }

                        // update in the Mail Server
                        UpdatedMessage um = this.ied.updateEmail(FID, FMID, f,
                                                                 msgNew, fp);

                        long uid = um.getFMID();

                        long uidv = f.getUIDValidity();

                        String GUID = Utility.createIMAPGUID(FID,
                                                             String.valueOf(uid),
                                                             String.valueOf(uidv));

                        emailToUpdate.setUID(new Property(GUID));


                        // set the values for the post update operation
                        SyncItemInfo sii = createInfo(GUID, null, um, Def.PROTOCOL_IMAP);

                        // update email to the servetItems list
                        this.ied.updateItemInServerItems(sii, GUID, serverItems);

                    }

                } catch (MessagingException me) {
                    throw new EntityException(me);
                } catch (EntityException e) {
                    throw new EntityException(e);
                } finally {
                    if (f != null) {
                        try {
                            if (f.isOpen()) {
                                f.close(true);
                                timeStop = System.currentTimeMillis();
                                if (log.isTraceEnabled()) {
                                    log.trace("updateItem Execution Time: " +
                                               (timeStop - timeStart) + " ms");
                                }
                            }
                        } catch (MessagingException me) {
                            throw new EntityException(me);
                        }
                    }
                }
            }
        }

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
    public com.funambol.email.pdi.folder.Folder updateFolder(
            String parentId,
            String folderId,
            com.funambol.email.pdi.folder.Folder folderNew,
            Map serverItems)
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
            // set the values for the post update operation
            SyncItemInfo sii = createInfo(Def.FOLDER_INBOX_GUID);
            // update email to the servetItems list
            this.ied.updateItemInServerItems(sii, Def.FOLDER_INBOX_GUID, serverItems);
        } else if (folderId.equals(Def.FOLDER_OUTBOX_ID)){
            ifout =  new ItemFolder(Def.FOLDER_OUTBOX_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_OUTBOX_ROLE,
                                    attributes) ;
            // set the values for the post update operation
            SyncItemInfo sii = createInfo(Def.FOLDER_OUTBOX_ID);
            // update email to the servetItems list
            this.ied.updateItemInServerItems(sii, Def.FOLDER_OUTBOX_ID, serverItems);
        } else if (folderId.equals(Def.FOLDER_SENT_ID)){
            ifout =  new ItemFolder(Def.FOLDER_SENT_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_SENT_ROLE,
                                    attributes) ;
            // set the values for the post update operation
            SyncItemInfo sii = createInfo(Def.FOLDER_SENT_ID);
            // update email to the servetItems list
            this.ied.updateItemInServerItems(sii, Def.FOLDER_SENT_ID, serverItems);
        } else if (folderId.equals(Def.FOLDER_DRAFTS_ID)){
            ifout =  new ItemFolder(Def.FOLDER_DRAFTS_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_DRAFTS_ROLE,
                                    attributes) ;
            // set the values for the post update operation
            SyncItemInfo sii = createInfo(Def.FOLDER_DRAFTS_ID);
            // update email to the servetItems list
            this.ied.updateItemInServerItems(sii, Def.FOLDER_DRAFTS_ID, serverItems);
        } else if (folderId.equals(Def.FOLDER_TRASH_ID)){
            ifout =  new ItemFolder(Def.FOLDER_TRASH_GUID,
                                    name,
                                    Def.FOLDER_ROOT_ID,
                                    created,
                                    accessed,
                                    modified,
                                    Def.FOLDER_TRASH_ROLE,
                                    attributes) ;
            // set the values for the post update operation
            SyncItemInfo sii = createInfo(Def.FOLDER_TRASH_GUID);
            // update email to the servetItems list
            this.ied.updateItemInServerItems(sii, Def.FOLDER_TRASH_GUID, serverItems);

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


    /**
     * clean all folder and email except the 5 main folders
     *
     * @param allMailboxActivation boolean
     * @param source_uri           String
     * @param principalId          long
     * @throws EntityException
     */
    public void clearAllItems(boolean allMailboxActivation,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        IMAPFolder f = null;
        EntityException finalExc = null;

        String[] folders = {this.imsw.getDefaultFolder().getInboxName(),
        this.imsw.getDefaultFolder().getOutboxName(),
        this.imsw.getDefaultFolder().getSentName(),
        this.imsw.getDefaultFolder().getDraftsName(),
        this.imsw.getDefaultFolder().getTrashName()};

        String folderName;

        // clean all emails in the main folders
        for (int y = 0; y < folders.length; y++) {
            folderName = folders[y];
            try {
                f = (IMAPFolder)
                this.imsw.getMailDefaultFolder().getFolder(folderName);
                if (log.isTraceEnabled()) {
                    log.trace("ImapEmail Delete all emails in " + folderName);
                }
                if (f.exists()) {
                    timeStart = System.currentTimeMillis();
                    f.open(Folder.READ_WRITE);
                    this.ied.removeAllEmail(f);
                }
            } catch (MessagingException me) {
                throw new EntityException(me);
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
                        finalExc = new EntityException(me);
                    }
                }
            }
            if (finalExc != null) {
                throw finalExc;
            }
        }

        // clean all folders in the main folders
        // only if allMalboxActivation is on
        if (allMailboxActivation) {

            ArrayList fs;
            String tmpname;
            String GUID;
            IMAPFolder tmpfolder;

            for (int y = 0; y < folders.length; y++) {

                folderName = folders[y];

                try {
                    f = (IMAPFolder)
                    this.imsw.getMailDefaultFolder().getFolder(folderName);
                    if (log.isTraceEnabled()) {
                        log.trace("ImapEmail Delete all folders in " + folderName);
                    }
                    if (f.exists()) {
                        timeStart = System.currentTimeMillis();
                        f.open(Folder.READ_WRITE);

                        fs = new ArrayList();
                        this.ied.searchSubFolderFullNames(f, fs);

                        for (int i = 0; i < fs.size(); i++) {

                            tmpname = (String) fs.get(i);

                            if (log.isTraceEnabled()) {
                                log.trace("remove folder " + tmpname);
                            }

                            // remove folder from Mail Server
                            tmpfolder = (IMAPFolder)
                            this.imsw.getMailDefaultFolder().getFolder(tmpname);
                            if (tmpfolder.exists()) {
                                // the folder must be closed
                                this.ied.removeFolderInServer(tmpfolder);
                            }

                            // remove folder from Local DB
                            GUID = this.ied.getGUIDFromFullPath(Def.SEPARATOR_FIRST + tmpname,
                                    source_uri,
                                    principalId,
                                    username);

                            this.ied.removeFolderInDB(GUID, source_uri, principalId, username);

                        }

                    }
                } catch (MessagingException me) {
                    throw new EntityException(me);
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
                            finalExc = new EntityException(me);
                        }
                    }
                }
                if (finalExc != null) {
                    throw finalExc;
                }
            }
        }


    }


    /**
     *
     *
     * @param FID folder id String
     * @param LUID  String
     * @param emailToAdd  Email
     * @param filter EmailFilter
     * @param funSignature  String
     * @param from  String
     * @param firstname  String
     * @param lastname  String
     * @param username  String
     * @param source_uri  String
     * @param principalId long
     * @return Email
     * @throws EntityException
     */
    public Email addEmail(String FID,
                          String LUID,
                          Email emailToAdd,
                          Map serverItems,
                          EmailFilter filter,
                          String funSignature,
                          String from,
                          String firstname,
                          String lastname,
                          String username,
                          String source_uri,
                          long principalId)
      throws EntityException, SendingException, RefusedItemException {


        IMAPFolder      f        = null;
        String          fullpath = null;

        // the emailItem could be null
        // i.e. the client send only the read property
        FlagProperties fp = Utility.getFlags(emailToAdd);

        if (emailToAdd != null &&
            emailToAdd.getEmailItem() != null &&
            Def.ENCODE_QUOTED_PRINTABLE.equals(emailToAdd.getEmailItem().getEncoding())) {
            try {
                emailToAdd.getEmailItem().setPropertyValue(
                        Utility.decode(emailToAdd.getEmailItem().getStringRFC2822(),
                        Def.ENCODE_QUOTED_PRINTABLE));
                emailToAdd.getEmailItem().setEncoding(null);
            } catch (MessagingException e) {
                throw new EntityException(e);
            }
        }

        try {

            if (FID.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)){

                // OUTBOX - sending procedure (email with id starts with "O")

                FID = Def.FOLDER_SENT_ID;

                // convert Email into Message (javaMail)
                Message msg = createMessage(this.imsw.getSession(), emailToAdd,
                                            FID, false, fp);

                // save message-ID
                String messageID = Utility.getHeaderMessageID(msg);

                // send message
                Message newMsg = null;
                try {
                   newMsg = sendEmail(msg, this.imsw.getSession(), funSignature,
                                      from, firstname, lastname);
                } catch (SendingException se) {
                   throw new SendingException(se);
                }

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

                if (Def.SERVER_TYPE_AOL.equals(this.serverType) ||
                    Def.SERVER_TYPE_GMAIL.equals(this.serverType)) {
                    
                    //
                    // The email is stored in the SENT folder by the AOL server
                    // and Gmail imap.
                    //
                    
                    // don't save the email in the sent folder
                    emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));

                    String GUID = Utility.createIMAPGUID(Def.FOLDER_OUTBOX_ID,
                                                         String.valueOf(LUID),
                                                         String.valueOf(0) );
                    emailToAdd.setUID(new Property(GUID));

                    // if the item is not saved in the sent folder the system
                    // doesn't add it into the servetItems list

                    return emailToAdd;
                    
                } else {

                    // the SENT folder could be disable
                    // if it's enabled the system adds the item in the sent
                    // folder of the Mail Server
                    
                    // get Sent folder name
                    fullpath = this.ied.getFullPathFromFID(FID, source_uri, principalId, username);
                    if (fullpath == null) {
                        emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));
                        String GUID = Utility.createIMAPGUID(Def.FOLDER_OUTBOX_ID,
                                                             String.valueOf(LUID),
                                                             String.valueOf(0) );
                        emailToAdd.setUID(new Property(GUID));
                        return emailToAdd;
                    }
                    
                    // open Sent Folder on mail server
                    f = (IMAPFolder)  this.imsw.getMailDefaultFolder().getFolder(fullpath);
                    
                    if (!f.exists()) {
                        emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));
                        String GUID = Utility.createIMAPGUID(Def.FOLDER_OUTBOX_ID,
                                                             String.valueOf(LUID),
                                                             String.valueOf(0) );
                        emailToAdd.setUID(new Property(GUID));
                        return emailToAdd;
                    }
                    
                    timeStart = System.currentTimeMillis();
                    
                    f.open(Folder.READ_WRITE);
                    
                    long uid = -1;
                    if(this.serverType.equals(Def.SERVER_TYPE_COURIER)) {
                        if (newMsg != null){
                            uid = this.ied.addEmailByNextUID(f, newMsg);
                        } else {
                            uid = this.ied.addEmailByNextUID(f, msg);                            
                        }
                    } else {
                        if (newMsg != null){
                            uid = this.ied.addEmailByListener(f, newMsg);
                        } else {
                            uid = this.ied.addEmailByListener(f, msg);                            
                        }                        
                    }
                    
                    emailToAdd.setParentId(new Property(Def.FOLDER_OUTBOX_ID));
                    
                    long uidv = f.getUIDValidity();
                    
                    String GUID = Utility.createIMAPGUID(Def.FOLDER_OUTBOX_ID,
                            String.valueOf(uid),
                            String.valueOf(uidv) );
                    
                    emailToAdd.setUID(new Property(GUID));
                    
                    // create the object to add in db and in serverItems
                    SyncItemInfo sii = createInfo(GUID, msg, null, Def.PROTOCOL_IMAP);
                    
                    // add email to the servetItems list
                    this.ied.addItemInServerItems(sii, serverItems);
                    
                    return emailToAdd;
                }
                
            } else {

                /**
                 * this procedure should be tested with client that syncs
                 * items into the the folders: INBOX - SENT - DRAFT - TRASH
                 * At the moment the Funambol client doesn't sync the
                 * previous folders.
                 */

                // INBOX - SENT - DRAFT - TRASH

                if (this.isFolderActive(FID, filter)){

                    fullpath = this.ied.getFullPathFromFID(FID, source_uri, principalId, username);

                    if (fullpath == null) {
                        return emailToAdd;
                    }

                    f = (IMAPFolder)this.imsw.getMailDefaultFolder().getFolder(fullpath);

                    if (!f.exists()) {
                        //emailToAdd.setUID(new Property(LUID));
                        return emailToAdd;
                    }

                    // convert Email into Message (javaMail)
                    Message msg = createMessage(this.imsw.getSession(),
                                                emailToAdd, FID, false, fp);
                    
                    checkIfItHasToBeRefused(msg);

                    // add in the folder of the Mail Server
                    timeStart = System.currentTimeMillis();

                    f.open(Folder.READ_WRITE);

                    long uid = -1;
                    if(this.serverType.equals(Def.SERVER_TYPE_COURIER)) {
                        uid = this.ied.addEmailByNextUID(f, msg);
                    } else {
                        uid = this.ied.addEmailByListener(f, msg);
                    }

                    long uidv = f.getUIDValidity();

                    String GUID = Utility.createIMAPGUID(FID,
                                                         String.valueOf(uid),
                                                         String.valueOf(uidv) );

                    emailToAdd.setUID(new Property(GUID));


                    // create the object to add in db and in serverItems
                    SyncItemInfo sii = createInfo(GUID, msg, null, Def.PROTOCOL_IMAP);

                    if (FID.equalsIgnoreCase(Def.FOLDER_INBOX_ID)){
                       // add email into the inbox table
                       this.ied.addEmailInDB(sii, username, Def.PROTOCOL_IMAP);
                    }

                    // add email to the servetItems list
                    this.ied.addItemInServerItems(sii, serverItems);

                    return emailToAdd;

                } else {

                    return emailToAdd;

                }

            }

        } catch (MessagingException me) {
            throw new EntityException(me);
        } catch (SendingException e) {
            throw new SendingException(e);
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
     * todo understand the type of a folder (holds folders and mails)
     *
     * @param name          String
     * @param parentId      String
     * @param df            DefaultFolder
     * @param idFolderSpace IdSpaceGenerator
     * @param source_uri    String
     * @param principalId   long
     * @return Folder
     * @throws EntityException
     */
    public com.funambol.email.pdi.folder.Folder addFolder(String name,
                                                          String parentId,
                                                          DefaultFolder df,
                                                          DBIDGenerator idFolderSpace,
                                                          Map serverItems,
                                                          String source_uri,
                                                          long principalId,
                                                          String username)
      throws EntityException {

        com.funambol.email.pdi.folder.Folder folderOut;
        ItemFolder itemFolder = null;
        String GUID = null;
        IMAPFolder f = null; // parent folder
        String parentFullpath;
        String fullpath;
        String role;
        String creationDate;
        EntityException finalExc = null;

        try {

            parentFullpath = this.ied.getFullPathFromGUID(parentId, source_uri, principalId, username);

            if (parentFullpath != null) {
                fullpath = parentFullpath + Def.SEPARATOR_FIRST + name;
            } else {
                fullpath = Def.SEPARATOR_FIRST + name;
            }

            if (log.isTraceEnabled()) {
                log.trace("addFolder FID:" + parentFullpath + " FMID:" + fullpath);
            }

            boolean isDefFolder = Utility.defaultFolderChecker(df, fullpath);

            if (isDefFolder) {

                GUID = Utility.getDefaultFolderId(df, fullpath);

            } else {

                f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(parentFullpath);

                timeStart = System.currentTimeMillis();

                f.open(Folder.READ_WRITE);

                Folder folderToInsert = f.getFolder(name);

                int type = 0; // set the right folder type

                if (!folderToInsert.exists()) {
                    GUID = this.ied.addFolder(fullpath,
                                              parentId,
                                              folderToInsert,
                                              type,
                                              idFolderSpace,
                                              source_uri,
                                              principalId,
                                              username);
                }

                // set the values for the post update operation
                SyncItemInfo sii = createInfo(GUID);

                // update email to the servetItems list
                this.ied.addItemInServerItems(sii, serverItems);

            }

            role = Utility.getFolderRole(name, this.imsw.getDefaultFolder());

            creationDate = Utility.getFolderCreationDate();

            itemFolder = setItemFolder(GUID, name, parentId, role, creationDate);

        } catch (MessagingException me) {
            throw new EntityException("Error searching folder", me);
        } catch (EntityException ee) {
            throw new EntityException("Error creating folder", ee);
        } finally {
            if (f != null) {
                try {
                    if (f.isOpen()) {
                        f.close(true);
                        timeStop = System.currentTimeMillis();
                        if (log.isTraceEnabled()) {
                            log.trace("insertFolder Execution Time: " +
                                    (timeStop - timeStart) + " ms");
                        }
                    }
                } catch (MessagingException me) {
                    finalExc = new EntityException(me);
                }
            }
        }

        if (finalExc != null) {
            throw finalExc;
        }

        try {
            folderOut = createFoundationFolder(itemFolder);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        return folderOut;

    }


    /**
     * if allMailboxActivation is true the method gets the
     * 5 main folders (inbox, outbox, sent, draft, trash)
     * and recursively all the subforlders
     * <p/>
     * if allMailboxActivation is false the method gets only the
     * 5 main folders (inbox, outbox, sent, draft, trash)
     *
     * @param filter               EmailFilter
     * @param allMailboxActivation boolean
     * @param source_uri           String
     * @param principalId          long
     * @return String[]
     * @throws EntityException
     */
    public String[] getAllFolders(EmailFilter filter,
            boolean allMailboxActivation,
            String source_uri,
            long principalId, 
            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("imap getAllFolders() start");
        }

        String[] allfolders;
        int folderFilter = filter.getFolder();
        boolean isFolderIn = false;

        if (!allMailboxActivation) {

            // get only the default folders

            String[] inboxfolders = null;
            String[] outboxfolders = null;
            String[] sentfolders = null;
            String[] draftfolders = null;
            String[] trashfolders = null;

            if (folderFilter == Def.FILTER_FOLDER_I ||
                    folderFilter == Def.FILTER_FOLDER_IO ||
                    folderFilter == Def.FILTER_FOLDER_IOS ||
                    folderFilter == Def.FILTER_FOLDER_IOSD ||
                    folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isInboxInMailServer();
                if (isFolderIn){
                    inboxfolders = new String[1];
                    inboxfolders[0] = Def.FOLDER_INBOX_GUID;
                }
            }
            if (folderFilter == Def.FILTER_FOLDER_IO ||
                    folderFilter == Def.FILTER_FOLDER_IOS ||
                    folderFilter == Def.FILTER_FOLDER_IOSD ||
                    folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();
                if (isFolderIn){
                    outboxfolders = new String[1];
                    outboxfolders[0] = Def.FOLDER_OUTBOX_GUID;
                }
            }
            if (folderFilter == Def.FILTER_FOLDER_IOS ||
                    folderFilter == Def.FILTER_FOLDER_IOSD ||
                    folderFilter == Def.FILTER_FOLDER_IOSDT) {
                isFolderIn = this.imsw.getDefaultFolder().isSentInMailServer();
                if (isFolderIn){
                    sentfolders = new String[1];
                    sentfolders[0] = Def.FOLDER_SENT_GUID;
                }
            }
            if (folderFilter == Def.FILTER_FOLDER_IOSD ||
                    folderFilter == Def.FILTER_FOLDER_IOSDT) {
                isFolderIn = this.imsw.getDefaultFolder().isDraftsInMailServer();
                if (isFolderIn){
                    draftfolders = new String[1];
                    draftfolders[0] = Def.FOLDER_DRAFTS_GUID;
                }
            }
            if (folderFilter == Def.FILTER_FOLDER_IOSDT) {
                trashfolders = new String[1];
                isFolderIn = this.imsw.getDefaultFolder().isTrashInMailServer();
                if (isFolderIn){
                    trashfolders[0] = Def.FOLDER_TRASH_GUID;
                }
            }

            allfolders = Utility.mergeArray(inboxfolders, outboxfolders,
                    sentfolders, draftfolders,
                    trashfolders);

        } else {
            // get all folders in the mailbox
            String[] inboxfolders = null;
            String[] outboxfolders = null;
            String[] sentfolders = null;
            String[] draftfolders = null;
            String[] trashfolders = null;

            String defFullpath;
            try {
                // get all subfolders in Inbox + inbox folder
                if (folderFilter == Def.FILTER_FOLDER_I ||
                        folderFilter == Def.FILTER_FOLDER_IO ||
                        folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isInboxInMailServer();
                    if (isFolderIn){
                        defFullpath = this.imsw.getDefaultFolder().getInboxName();
                        inboxfolders = this.ied.getAllFolders(filter,
                                this.imsw.getInboxFolder(),
                                defFullpath,
                                source_uri,
                                principalId, 
                                username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IO ||
                        folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();
                    if (isFolderIn){
                        defFullpath = this.imsw.getDefaultFolder().getOutboxName();
                        outboxfolders = this.ied.getAllFolders(filter,
                                this.imsw.getOutboxFolder(),
                                defFullpath,
                                source_uri,
                                principalId, 
                                username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isSentInMailServer();
                    if (isFolderIn){
                        defFullpath = this.imsw.getDefaultFolder().getSentName();
                        sentfolders = this.ied.getAllFolders(filter,
                                this.imsw.getSentFolder(),
                                defFullpath,
                                source_uri,
                                principalId, 
                                username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isDraftsInMailServer();
                    if (isFolderIn){
                        defFullpath = this.imsw.getDefaultFolder().getDraftsName();
                        draftfolders = this.ied.getAllFolders(filter,
                                this.imsw.getDraftsFolder(),
                                defFullpath,
                                source_uri,
                                principalId, 
                                username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isTrashInMailServer();
                    if (isFolderIn){
                        defFullpath = this.imsw.getDefaultFolder().getTrashName();
                        draftfolders = this.ied.getAllFolders(filter,
                                this.imsw.getTrashFolder(),
                                defFullpath,
                                source_uri,
                                principalId, 
                                username);
                    }
                }

                allfolders = Utility.mergeArray(inboxfolders, outboxfolders,
                        sentfolders, draftfolders,
                        trashfolders);

            } catch (EmailAccessException eae) {
                throw new EntityException("Error getting all Folders (" +
                        eae.getMessage() + ")");
            }


        }

        return allfolders;
    }


    /**
     * todo get all emails info from all folders
     * (not only from the 5 main folder)
     *
     * @param filter EmailFilter
     * @param username String
     * @param maxEmailNumber int
     * @param allMailboxActivation boolean
     * @return map with all mail server SyncItemInfo
     * @throws EntityException
     */
    public Map getAllEmailsInfo(EmailFilter filter,
                                String username,
                                int maxEmailNumber,
                                boolean allMailboxActivation)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getAllEmailsInfo start");
        }

        // all
        Map syncItemInfos = new LinkedHashMap() ;
        //
        Map inboxInfos  = new LinkedHashMap();
        Map outboxInfos = new LinkedHashMap();
        Map draftsInfos = new LinkedHashMap();
        Map sentInfos   = new LinkedHashMap();
        Map trashInfos  = new LinkedHashMap();

        String path;

        if (!allMailboxActivation) {
            // get only the default folder emails info
            try {
                if (filter.getFolder() == Def.FILTER_FOLDER_I ||
                        filter.getFolder() == Def.FILTER_FOLDER_IO ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path       = this.imsw.getDefaultFolder().getInboxName();
                    inboxInfos = getAllEmailsInfoInbox(username, maxEmailNumber);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IO ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path        = this.imsw.getDefaultFolder().getOutboxName();
                    outboxInfos = getAllEmailsInfoRest(path, Def.FOLDER_OUTBOX_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path      = this.imsw.getDefaultFolder().getSentName();
                    sentInfos = getAllEmailsInfoRest(path, Def.FOLDER_SENT_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path = this.imsw.getDefaultFolder().getDraftsName();
                    draftsInfos = getAllEmailsInfoRest(path, Def.FOLDER_DRAFTS_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path       = this.imsw.getDefaultFolder().getTrashName();
                    trashInfos = getAllEmailsInfoRest(path, Def.FOLDER_TRASH_ID, filter);
                }
                // put the list togheter
                syncItemInfos = Utility.mergeMap(inboxInfos, outboxInfos,
                                                 draftsInfos, sentInfos,
                                                 trashInfos);

            } catch (Exception e) {
                throw new EntityException("Error getting all Emails Info (" +
                        e.getMessage() + ")", e);
            }

        } else {
            // get all emails info
            try {
                if (filter.getFolder() == Def.FILTER_FOLDER_I ||
                        filter.getFolder() == Def.FILTER_FOLDER_IO ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path       = this.imsw.getDefaultFolder().getInboxName();
                    inboxInfos = this.getAllEmailsInfoInbox(username, maxEmailNumber);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IO ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path        = this.imsw.getDefaultFolder().getOutboxName();
                    outboxInfos = getAllEmailsInfoRest(path, Def.FOLDER_OUTBOX_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOS ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path      = this.imsw.getDefaultFolder().getSentName();
                    sentInfos = getAllEmailsInfoRest(path, Def.FOLDER_SENT_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                        filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path        = this.imsw.getDefaultFolder().getDraftsName();
                    draftsInfos = getAllEmailsInfoRest(path, Def.FOLDER_DRAFTS_ID, filter);
                }
                if (filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                    path       = this.imsw.getDefaultFolder().getTrashName();
                    trashInfos = getAllEmailsInfoRest(path, Def.FOLDER_TRASH_ID, filter);
                }
                // put the list togheter
                syncItemInfos = Utility.mergeMap(inboxInfos, outboxInfos,
                                                 draftsInfos, sentInfos,
                                                 trashInfos);

            } catch (Exception e) {
                throw new EntityException("Error getting all Emails Info (" +
                        e.getMessage() + ")", e);
            }
        }

        if (log.isTraceEnabled()) {
            log.trace("Email List with " + syncItemInfos.size() + " Document");
        }

        return syncItemInfos;

    }

    /**
     * @param filter               EmailFilter
     * @param allMailboxActivation boolean
     * @param source_uri           String
     * @param principalId          long
     * @return map with all mail server CrcSyncItemInfo (about folders)
     * @throws EntityException
     */
    public Map getAllFoldersInfo(EmailFilter filter,
                                 boolean allMailboxActivation,
                                 String source_uri,
                                 long principalId,
                                 String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getAllFolderInfo start");
        }

        int folderFilter   = filter.getFolder();
        boolean isFolderIn = false;

        Map syncItemInfos = new LinkedHashMap();

        Map inboxfolders  = new LinkedHashMap();
        Map outboxfolders = new LinkedHashMap();
        Map sentfolders   = new LinkedHashMap();
        Map draftfolders  = new LinkedHashMap();
        Map trashfolders  = new LinkedHashMap();

        SyncItemInfo infofolder = null;
        ItemFolder      ifolder    = null;

        if (!allMailboxActivation) {

            // get only the default folders

            if (folderFilter == Def.FILTER_FOLDER_I ||
                folderFilter == Def.FILTER_FOLDER_IO ||
                folderFilter == Def.FILTER_FOLDER_IOS ||
                folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isInboxInMailServer();

                if (isFolderIn){
                    infofolder = new SyncItemInfo(new SyncItemKey(Def.FOLDER_INBOX_GUID),  0,
                                                     null, null,null, null, null,
                                                     null, null, null, null);
                    inboxfolders.put(Def.FOLDER_INBOX_GUID, infofolder);
                }
            }
            if (folderFilter == Def.FILTER_FOLDER_IO ||
                folderFilter == Def.FILTER_FOLDER_IOS ||
                folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();

                if (isFolderIn){
                    infofolder = new SyncItemInfo(new SyncItemKey(Def.FOLDER_OUTBOX_GUID), 0,
                                                     null, null, null, null, null,
                                                     null, null, null, null);
                    outboxfolders.put(Def.FOLDER_OUTBOX_GUID, infofolder);
                }

            }
            if (folderFilter == Def.FILTER_FOLDER_IOS ||
                folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isSentInMailServer();

                if (isFolderIn){
                    infofolder = new SyncItemInfo(new SyncItemKey(Def.FOLDER_SENT_GUID), 0,
                                                     null, null, null, null, null,
                                                     null, null, null, null);
                    sentfolders.put(Def.FOLDER_SENT_GUID, infofolder);
                }

            }
            if (folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isDraftsInMailServer();

                if (isFolderIn){
                    infofolder = new SyncItemInfo(new SyncItemKey(Def.FOLDER_DRAFTS_GUID), 0,
                                                     null, null, null, null, null,
                                                     null, null, null, null);
                    draftfolders.put(Def.FOLDER_DRAFTS_GUID, infofolder);
                }

            }
            if (folderFilter == Def.FILTER_FOLDER_IOSDT) {

                isFolderIn = this.imsw.getDefaultFolder().isTrashInMailServer();

                if (isFolderIn){
                    infofolder = new SyncItemInfo(new SyncItemKey(Def.FOLDER_TRASH_GUID), 0,
                                                     null, null, null, null, null,
                                                     null, null, null, null);
                    trashfolders.put(Def.FOLDER_TRASH_GUID, infofolder);
                }

            }

            syncItemInfos = Utility.mergeMap(inboxfolders, outboxfolders,
                                             sentfolders, draftfolders,
                                             trashfolders);

        } else {

            // get all folders info in he mailbox

            try {

                if (folderFilter == Def.FILTER_FOLDER_I ||
                        folderFilter == Def.FILTER_FOLDER_IO ||
                        folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isInboxInMailServer();
                    if (isFolderIn){
                        ifolder = new ItemFolder(Def.FOLDER_INBOX_GUID,
                                this.imsw.getDefaultFolder().getInboxName(),
                                Def.FOLDER_ROOT_ID,
                                null, null, null, null, null);
                        inboxfolders = this.ied.getAllFoldersInfo(filter,
                                this.imsw.getInboxFolder(), ifolder,
                                source_uri, principalId, username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IO ||
                        folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();
                    if (isFolderIn){
                        ifolder = new ItemFolder(Def.FOLDER_OUTBOX_GUID,
                                this.imsw.getDefaultFolder().getOutboxName(),
                                Def.FOLDER_ROOT_ID,
                                null, null, null, null, null);
                        outboxfolders = this.ied.getAllFoldersInfo(filter,
                                this.imsw.getOutboxFolder(), ifolder,
                                source_uri, principalId, username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOS ||
                        folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isSentInMailServer();
                    if (isFolderIn){
                        ifolder = new ItemFolder(Def.FOLDER_SENT_GUID,
                                this.imsw.getDefaultFolder().getSentName(),
                                Def.FOLDER_ROOT_ID,
                                null, null, null, null, null);
                        sentfolders = this.ied.getAllFoldersInfo(filter,
                                this.imsw.getSentFolder(), ifolder,
                                source_uri, principalId, username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOSD ||
                        folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();
                    if (isFolderIn){
                        ifolder = new ItemFolder(Def.FOLDER_DRAFTS_GUID,
                                this.imsw.getDefaultFolder().getDraftsName(),
                                Def.FOLDER_ROOT_ID,
                                null, null, null, null, null);
                        draftfolders = this.ied.getAllFoldersInfo(filter,
                                this.imsw.getDraftsFolder(), ifolder,
                                source_uri, principalId, username);
                    }
                }
                if (folderFilter == Def.FILTER_FOLDER_IOSDT) {
                    isFolderIn = this.imsw.getDefaultFolder().isOutboxInMailServer();
                    if (isFolderIn){
                        ifolder = new ItemFolder(Def.FOLDER_TRASH_GUID,
                                this.imsw.getDefaultFolder().getTrashName(),
                                Def.FOLDER_ROOT_ID,
                                null, null, null, null, null);
                        trashfolders = this.ied.getAllFoldersInfo(filter,
                                this.imsw.getTrashFolder(), ifolder,
                                source_uri, principalId, username);
                    }
                }

                syncItemInfos = Utility.mergeMap(inboxfolders, outboxfolders,
                                                 sentfolders, draftfolders,
                                                 trashfolders);

            } catch (EmailAccessException eae) {
                throw new EntityException("Error getting all Folders (" +
                        eae.getMessage() + ")");
            }

        }

        if (log.isTraceEnabled()) {
            log.trace("Folder List with " + syncItemInfos.size() + " Document");
        }

        return syncItemInfos;

    }


    /**
     *
     * checks if a mail match the filter
     *
     * @param email Email
     * @param filter EmailFilter
     * @param GUID String
     * @param FID String
     * @param FMID String
     * @param status char
     * @param source_uri String
     * @param principalId long
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
                email = this.getEmailFromUID(filter, FMID, FID, status,
                                             source_uri, principalId, username, new ContentProviderInfo("","",""));
            }

            isInFilter = super.isEmailInFilter(email, filter,
                                               GUID, FID, FMID,
                                               this.imsw.getSession(),
                                               this.imsw.getLocale(),
                                               source_uri, principalId);

        } catch (Exception e) {
            throw new EntityException(e);
        }

        return isInFilter;
    }


    /**
     * @param filter EmailFilter
     * @param mailId  mail id; String
     * @param parentId parent id; String
     * @param status status of the item; char
     * @param source_uri  String
     * @param principalId princiapal ID long
     * @return Email
     * @throws EntityException
     */
    public Email getEmailFromUID(EmailFilter filter,
                                 String mailId,
                                 String parentId,
                                 char status,
                                 String source_uri,
                                 long principalId,
                                 String username,
                                 ContentProviderInfo contentProviderInfo)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getItemByUID in folder " + parentId +
                       " mail: " + mailId + " status " + status );
        }

        Email       emailOut = null;
        ItemMessage im       = null;
        String      fullpath = null;
        long        FMID     = 0;

        try {
            FMID     = Long.parseLong(mailId);
            fullpath = this.ied.getFullPathFromFID(parentId, source_uri, principalId, username);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        timeStart = System.currentTimeMillis();

        if (fullpath != null && FMID != 0) {

            if (parentId.equalsIgnoreCase(Def.FOLDER_INBOX_ID)) {
                // inbox folder
                try {
                    if (!this.imsw.folderInboxOpened.isOpen()){
                        this.imsw.folderInboxOpened.open(javax.mail.Folder.READ_WRITE);
                    }
                    im = this.getEmailFromUID(filter, this.imsw.folderInboxOpened,
                                              parentId, FMID, status,
                                              source_uri, principalId, username, contentProviderInfo);
                } catch (MessagingException e) {
                    throw new EntityException(e);
                } catch (EntityException e) {
                    throw new EntityException(e);
                }

            } else {
                // rest of the folders
                IMAPFolder f = null;
                try {
                    f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);
                    if (f.exists()) {
                        if (!f.isOpen()){
                           f.open(Folder.READ_WRITE);
                        }
                        im = this.getEmailFromUID(filter, f,
                                                 parentId, FMID, status,
                                                 source_uri, principalId, username,
                                                 contentProviderInfo);
                    } else {
                        return emailOut;
                    }
                } catch (Exception e) {
                    throw new EntityException(e);
                } finally {
                    if (f != null) {
                        try {
                            if (f.isOpen()) {
                                f.close(true);
                                timeStop = System.currentTimeMillis();
                                if (log.isTraceEnabled()) {
                                    log.trace("getItem Total Execution Time: " +
                                            (timeStop - timeStart) + " ms");
                                }
                            }
                        } catch (MessagingException me) {
                            throw new EntityException(me);
                        }
                    }
                }
            }
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
     * get folder from GUID
     *
     * @param filter      EmailFilter
     * @param GUID        folder id (parentid + / + folderid)
     * @param source_uri  String
     * @param principalId long
     * @return Folder
     * @throws EntityException
     */
    public com.funambol.email.pdi.folder.Folder getFolderFromUID(EmailFilter filter,
            String GUID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getFolderFromUID start " + GUID);
        }

        com.funambol.email.pdi.folder.Folder folderOut = null;
        ItemFolder im;
        IMAPFolder f;
        String fullpath;
        String name;
        String parentFullpath;
        String FID;
        String role;
        String creationDate = Utility.getFolderCreationDate();;

        try {

            fullpath = this.ied.getFullPathFromGUID(GUID, source_uri, principalId, username);

            if (fullpath != null) {

                f = (IMAPFolder)this.imsw.getMailDefaultFolder().getFolder(fullpath);

                if (!f.exists()) {
                    // if folder doesn't exists
                    if (GUID.equalsIgnoreCase(Def.FOLDER_OUTBOX_GUID)) {
                        // some IMAP servers don't allow to insert folder (i.e. AOL)
                        // but the OUTBOX folder must be returned
                        if (log.isTraceEnabled()) {
                            log.trace("cannot get the " + GUID + ", but return the defualt value");
                        }
                        im = new ItemFolder(GUID,
                            this.imsw.getDefaultFolder().getOutboxName(),
                            Def.FOLDER_ROOT_ID,
                            creationDate,
                            null,
                            null,
                            Def.FOLDER_OUTBOX_ROLE,
                            null);
                    } else {
                        return folderOut;
                    }
                } else {
                    // if folder exists
                    name = f.getName();
                    //parentFullpath = f.getParent().getFullName();
                    FID = this.ied.getGUIDFromFullPath(fullpath,source_uri,principalId, username);
                    role = Utility.getFolderRole(fullpath, this.imsw.getDefaultFolder());
                    im = setItemFolder(GUID, name, FID, role, creationDate);
                }


            } else {
                return folderOut;
            }

        } catch (Exception e) {
            throw new EntityException(e);
        }

        // convert ServerFolder in Folder
        if (im != null) {
            try {
                folderOut = createFoundationFolder(im);
            } catch (Exception e) {
                throw new EntityException(e);
            }
        }

        return folderOut;

    }

    /**
     * search the twin of the folder "name".
     * if the folder already exists the method returns the GUID
     *
     * @param name        String
     * @param parentId    String
     * @param source_uri  String
     * @param principalId long
     * @return com.funambol.email.pdi.folder.Folder
     * @throws EntityException
     */
    public String getFolderFromName(String name,
            String parentId,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getFolderFromName start Name: " + name +
                    " parent ID: " + parentId);
        }

        /**
         * we should add some control.
         * We can use the device information
         */
        String nametmp = Utility.convertNokiaToDefault(name,
                this.imsw.getDefaultFolder());
        // it is a nokia folder
        if (nametmp != null) {
            name = nametmp;
        }

        try {

            String parentFullpath;
            if (parentId.equalsIgnoreCase(Def.FOLDER_ROOT_ID)) {
                parentFullpath = Def.FOLDER_ROOT_ENG;
            } else {
                parentFullpath = this.ied.getFullPathFromGUID(parentId,
                        source_uri,
                        principalId, 
                        username);
            }

            String fullpath = parentFullpath + Def.SEPARATOR_FIRST + name;

            if (fullpath.startsWith("/")) {
                fullpath = fullpath.substring(1, fullpath.length());
            }

            // check in the Mail Server
            IMAPFolder f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);

            if (!f.exists()) {
                return null;
            } else {
                fullpath = f.getFullName();
                // if folder exists get GUID from DB
                // if GUID is null there is inconsistent data in the DB
                String GUID = this.ied.getGUIDFromFullPath(fullpath,
                        source_uri,
                        principalId,
                        username);
                
                if (GUID == null) {
                    log.error("Unable to get the GIUD from full path "+fullpath);
                }

                return GUID;
            }

        } catch (Exception e) {
            throw new EntityException(e);
        }

    }

    //---------------------------------------------------------- PRIVATE METHODS


    /**
     * get messages from folders.
     * the cache is not filter dependent. This method
     * return all the items in the server without filter
     *
     * @param fullpath path of the folder
     * @param folderId parent id
     * @param filter   EmailFilter
     * @return map with all mail server CrcSyncItemInfo
     * @throws EntityException
     */
    private LinkedHashMap getAllEmailsInfoRest(String fullpath,
                                               String FID,
                                               EmailFilter filter)
       throws EntityException {

        LinkedHashMap syncItemInfos = new LinkedHashMap();
        IMAPFolder      f           = null;
        Message         msg         = null;
        long            lastCRC     = 0;
        String          messageID   = null;
        java.util.Date  headerDate  = null;
        String          internal    = null;
        String          internalS   = null;
        int             itemsNum    = 0;
        String          GUID        = null;
        String          FMID        = null;
        String          UIDV        = null;
        EntityException finalExc    = null;

        try {

            f = (IMAPFolder) this.imsw.getMailDefaultFolder().getFolder(fullpath);

            // check folder
            if (!f.exists()) {
                return syncItemInfos;
            }

            timeStart = System.currentTimeMillis();

            f.open(Folder.READ_WRITE);

            Message[] items = this.ied.getAllEmails(f, FID, filter);

            // we use a fetch method in the imap protocol (not in the pop)
            if (items != null) {

               FetchProfile fp = new FetchProfile();
               fp.add(FetchProfile.Item.ENVELOPE);
               fp.add(FetchProfile.Item.FLAGS);
               f.fetch(items, fp);

               // create the info
               itemsNum = items.length;

               for (int i = 0; i < itemsNum; i++) {

                   msg = items[i];

                   messageID = Utility.getHeaderMessageID(msg);

                   try {
                       // REVIEWED THE FILTER TIME STRATEGY
                       //headerDate = UtilityDate.getHeaderDate(msg,null);
                       headerDate = UtilityDate.getDateForTimeFilter(msg,null);
                   } catch (Exception e){
                       log.error("Error parsing header date for Caching System ", e);
                   }

                   //String dateForCRC = Utility.getHeaderDateForCRC(msg);
                   //lastCRC   = Utility.createCRC(msg, messageID, dateForCRC);
                   String flagList = Utility.createFlagsList(msg);
                   lastCRC = Utility.createCRC(flagList, Def.PROTOCOL_IMAP);

                   FMID      = String.valueOf(f.getUID(msg));
                   UIDV      = String.valueOf(f.getUIDValidity());
                   GUID      = Utility.createIMAPGUID(FID, FMID, UIDV);

                   internalS  = Utility.getHeaderSyncLabel(msg);
                   if (internalS != null){
                       internal = "Y";
                   }


                   SyncItemInfo sii = new SyncItemInfo(new SyncItemKey(GUID), lastCRC,
                                                       messageID, headerDate,
                                                       null, null, null,
                                                       null, internal,
                                                       "Y", null);

                   syncItemInfos.put(GUID, sii);

               }

            }


        } catch (MessagingException e) {
            throw new EntityException(e);
        } finally {
            if (f != null) {
                try {
                    if (f.isOpen()) {
                        f.close(true);
                        timeStop = System.currentTimeMillis();
                        if (log.isTraceEnabled()) {
                            log.trace("getEmailsInfo Execution Time: " +
                                    (timeStop - timeStart) + " ms");
                        }
                    }
                } catch (MessagingException me) {
                    finalExc = new EntityException(me);
                }
            }
        }

        if (finalExc != null) {
            throw finalExc;
        }

        return syncItemInfos;

    }

    /**
     * get messages from folders.
     * the cache is not filter dependent. This method
     * return all the items in the server without filter
     *
     * @param  username String
     * @param  maxEmailNumber int
     * @return map with all mail server CrcSyncItemInfo
     * @throws EntityException
     */
    private Map getAllEmailsInfoInbox(String username,
            int maxEmailNumber) throws EntityException {

        Map allSyncItemInfo = null;
        try {
            allSyncItemInfo = ied.getAllEmailsInbox(username,
                    Def.PROTOCOL_IMAP,
                    maxEmailNumber);
        } catch (EntityException e) {
            throw new EntityException(e);
        }

        return allSyncItemInfo;

    }


    /**
     *
     * @param f IMAPFolder
     * @param FMID mail id in the folder
     * @param filter EmailFilter
     * @param msg Message
     * @param status char
     * @param source_uri String
     * @param principalId long
     * @return ItemMessage
     * @throws EntityException
     */
    private ItemMessage getPart(IMAPFolder f,
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

                String FID = this.ied.getGUIDFromFullPath(parentfullpath,
                        source_uri,
                        principalId, 
                        username);

                Locale loc = this.imsw.getLocale();

                Session session = this.imsw.getSession();

                //
                // At the end of execution of the getH/getHB/getHBA method the
                // attachmentInvfos list is filled with the ContentProviderInfo object
                // related to this message attachments (or is empty if this message
                // has no attachments).
                // 
                // Each item in the contentProviderInfos list is then stored into the 
                // content provider.
                //
                
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
     * @param filter EmailFilter
     * @param mailId  mail id; String
     * @param parentId parent id; String
     * @param status status of the item; char
     * @param source_uri  String
     * @param principalId princiapal ID long
     * @param contentProviderInfo The attachment infos 
     * @return Email
     * @throws EntityException
     */
    private ItemMessage getEmailFromUID(EmailFilter filter,
                                        IMAPFolder f,
                                        String FID,
                                        long FMID,
                                        char status,
                                        String source_uri,
                                        long principalId,
                                        String username,
                                        ContentProviderInfo contentProviderInfo)
      throws EntityException {

        ItemMessage     im       = null;
        Message msg = null;

        try {

            long tStart = System.currentTimeMillis();

            msg = this.ied.getEmailFromUID(f, FID, FMID);

            long tStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("getEmailFromUID getting single mail Execution Time: " +
                        (tStop - tStart) + " ms");
            }

            try {
                tStart = System.currentTimeMillis();
                im = getPart(f, String.valueOf(FMID),
                             filter, msg, status,
                             source_uri, principalId, username,
                             contentProviderInfo);
                tStop = System.currentTimeMillis();
                if (log.isTraceEnabled()) {
                    log.trace("getEmailFromUID filtering single mail Execution Time: " +
                            (tStop - tStart) + " ms");
                }
            } catch (EntityException e) {
                throw new EntityException(Def.ERR_FILTERING_EMAIL, e);
            }

            // The incoming message is logged if and only if the server is going
            // to sent back to the client the entire content of the email.
            // Skipping the scenario in which only flags are sent (update)
            if(im!=null &&StringUtils.isNotEmpty(im.getStreamMessage())) {
                logIncomingMessage(msg);
            }

        } catch (EntityException e) {
            throw new EntityException(e);
        }

        return im;
    }


    /**
     * check if the email can insert in the specific folder
     *
     * @param FID folder id String
     * @param filter EmailFilter
     * @return boolean
     */
    private boolean isFolderActive(String FID, EmailFilter filter){
        boolean check = true;

        int folderFilter = filter.getFolder();

        if (FID.equalsIgnoreCase(Def.FOLDER_INBOX_ID) || FID.equalsIgnoreCase(Def.FOLDER_INBOX_ENG)){
            return true;
        }
        if (FID.equalsIgnoreCase(Def.FOLDER_SENT_ID)){
            if (folderFilter == Def.FILTER_FOLDER_IOS ||
                folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {
                return true;
            }
        }
        if (FID.equalsIgnoreCase(Def.FOLDER_DRAFTS_ID)){
            if (folderFilter == Def.FILTER_FOLDER_IOSD ||
                folderFilter == Def.FILTER_FOLDER_IOSDT) {
                return true;
            }
        }
        if (FID.equalsIgnoreCase(Def.FOLDER_TRASH_ID)){
            if (folderFilter == Def.FILTER_FOLDER_IOSDT) {
                return true;
            }
            return true;
        }

        return check;

    }

}
