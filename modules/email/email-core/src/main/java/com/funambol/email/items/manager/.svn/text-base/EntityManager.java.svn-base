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
import com.funambol.email.engine.source.HelperForGetter;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.sun.mail.smtp.SMTPAddressFailedException;
import java.util.Locale;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Folder;
import javax.mail.Flags;
import javax.mail.Transport;
import com.sun.mail.imap.IMAPFolder;
import javax.mail.internet.MimeMessage;
import javax.mail.MessagingException;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.pdi.mail.EmailItem;
import com.funambol.email.items.dao.EntityDAO;
import com.funambol.email.model.ItemMessage;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.CommonMailServerWrapper;
import com.funambol.email.transport.ImapMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.email.exception.*;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.UpdatedMessage;
import com.funambol.email.pdi.mail.Ext;
import com.funambol.email.util.UtilityDate;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.RefusedItemException;
import com.funambol.framework.tools.id.DBIDGenerator;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.AuthenticationFailedException;
import javax.mail.internet.MimeUtility;

/**
 *
 * @version $Id: EntityManager.java,v 1.7 2008-05-18 15:20:34 nichele Exp $
 */
public class EntityManager {

    // -------------------------------------------------------------- Properties

    /**     */
    protected FunambolLogger log =
        FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    protected final static FunambolLogger trafficLog =
        FunambolLoggerFactory.getLogger(Def.TRAFFIC_LOGGER_NAME);

    /**     */
    protected CommonMailServerWrapper cmsw = null;

    /**     */
    public EntityDAO ed       = null;

    /**      */
    protected String serverType = null;

    /**     */
    protected long timeStart = 0;
    protected long timeStop  = 0;

    protected String userName;
    protected EmailSyncSource ess;
    
    
    {
       System.setProperty("mail.mime.base64.ignoreerrors","true");
    }

    // ------------------------------------------------------------ Costructors

    public EntityManager(){
    }

    /**
     *
     *
     * @param ess EmailSyncSource
     * @throws EmailAccessException
     */
    public EntityManager(EmailSyncSource ess)
            throws EmailAccessException {
        cmsw = (CommonMailServerWrapper) ess.getMSWrapper();
        serverType = ess.getMailServerAccount().getMailServer().getMailServerType();
        try {
            ed         = new EntityDAO(serverType);
        } catch (EntityException e) {
            throw new EmailAccessException("Error creating the EntityDAO", e);
        }        
        
        this.ess = ess;
        this.userName = ess.getMailServerAccount().getUsername();
    }

   // ----------------------------------------------------------- Public Methods

    /**
     * Return all email ids. it uses the CrcSyncItemInfo[], this array
     * is set by the caching system method in the beginSync method.
     * the si param includes inbox/sent folders info for the POP3 SyncSource
     * and inbox/outbox/sent/drafts/trash folders info for the IMAP
     * SyncSource.
     *
     * @param filter
     * @param serverItems all server items
     * @return list of the guid of all the items that match filter
     * @throws EntityException
     */
    public String[] getAllEmails(
            EmailFilter filter, 
            Map<String,SyncItemInfo> serverItems)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("Start - getAllItems() ");
        }
            
        List<SyncItemKey> filteredKeys = null;
        String[] allItemsKeys  = null;

        try {
            if (filter.getId() != null) {
                
                Set<String> keySet =
                        getFilteredItemsByID(serverItems, filter.getId()).keySet();
                allItemsKeys = keySet.toArray(new String[keySet.size()]);

            } else {
                
                if (filter.getTime() != null) {                                        
                    filteredKeys = HelperForGetter.setFilteredItemsKeys(filter, serverItems);                                        
                    allItemsKeys = new String[filteredKeys.size()];
                    for (int i = 0; i < filteredKeys.size(); i++) {
                        String guid = filteredKeys.get(i).getKeyAsString();
                        allItemsKeys[i] = guid;
                    }
                    
                } else {                    
                    // get all items
                    allItemsKeys = new String[serverItems.size()];
                    Iterator keys = serverItems.keySet().iterator();
                    int i = 0;
                    while (keys.hasNext()) {
                        allItemsKeys[i] = (String)keys.next();
                        i++;
                    }
                  
                }
            }
                        
            
        } catch (Exception e) {
            throw new EntityException("Error getting all emails", e);
        }
                
        if (log.isTraceEnabled()) {
            log.trace("End - getAllItems() ");
        }

        return allItemsKeys;
    }


    /**
     *
     * create a javaMail Message from a funambol foundation Email
     *
     * @param msgIn Message
     * @param session Session
     * @param sign String
     * @return Message
     * @throws EntityException
     */
    public Message createMessageToBeSent(Message msgIn,
                                         Session session,
                                         String trailer,
                                         String from,
                                         String firstname,
                                         String lastname)
     throws EntityException {

        Message newMsg = new MimeMessage(session);
        try {

            MessageCreator.setNewHeaderOut(newMsg, msgIn, from, firstname, lastname);

            if (trailer == null){
                MessageCreator.setNewContentToBeSent(newMsg, msgIn);
            } else {
                MessageCreator.setNewContent(newMsg, msgIn, trailer);
            }

            newMsg.saveChanges();
            
        } catch (Exception ee){
            throw new EntityException("" + ee.getMessage());
        }

        return newMsg;

    }



    /**
     *
     * create a javaMail Message from a funambol foundation Email
     *
     * @param s Session
     * @param ctmp Email
     * @param FID String
     * @param toBeSend boolean
     * @param fp all flags info FlagProperties
     * @return Message
     * @throws EntityException
     */
    public Message createMessage(Session s,
                                 Email ctmp,
                                 String FID,
                                 boolean toBeSend,
                                 FlagProperties fp)
      throws EntityException {

        Message doc;

        try {

            EmailItem emailItem = ctmp.getEmailItem();

            String rfc2822 = emailItem.getStringRFC2822();

            String messageID = Utility.getHeaderMessageID(rfc2822);
            
            byte[] b = rfc2822.getBytes("UTF-8");
            doc = new MimeMessage(s, new ByteArrayInputStream(b));
            
            // if draft set the FLAG
            if (FID.equalsIgnoreCase(Def.FOLDER_DRAFTS_ID)){
               doc.setFlag(Flags.Flag.DRAFT, true);
            }

            // for all folders set eventually the Flags
            doc.setFlags(new Flags(Flags.Flag.SEEN)     , fp.getSeen());
            doc.setFlags(new Flags(Flags.Flag.ANSWERED) , fp.getAnswered());
            doc.setFlags(new Flags(Flags.Flag.FLAGGED)  , fp.getFlagged());
            //doc.setFlags(new Flags(Flags.Flag.FORWARDED), false);
            
            // @todo: improve forward handling:
            // 1. by now neither jme client nor wm client seem to set correctly 
            //    the forwarded flag.
            // 2. by now the same user flag is set for different mail servers 
            if (fp.getForwarded()) {
                Flags flags = doc.getFlags();
                flags.add(Def.IS_FORWARDED_FLAG);
                doc.setFlags(flags, true);
            }

            if (toBeSend){
               // don't set the HEADER_LABEL_SYNCMESSAGE
            } else {
               // we must set the message as "sync message"
               doc.setHeader(Def.HEADER_SYNCMESSAGE, Def.HEADER_VALUE_SYNCMESSAGE);
            }

            if (messageID != null){
               doc.setHeader(Def.HEADER_MESSAGE_ID,messageID);
            }
            
        } catch (Exception e){
            throw new EntityException(e);
        }

        return doc;
    }


    /**
     *
     *
     * @param im ItemMessage
     * @return Email
     * @throws EntityException
     */
    public Email createFoundationMail(ItemMessage im)
     throws EntityException {

        Email mtmp       = new Email();

        String guid      = null;  // parentId + separator + mailId
        String parentId  = null;

        String emailItem = null;
        String encodingType = null;

        String created   = null; // Date created
        String received  = null; // Date received
        String modified  = null; // Date modified
        String read      = null; // boolean read
        String forwarded = null; // boolean forwarded
        String replied   = null; // boolean replied
        String deleted   = null; // boolean deleted
        String flagged   = null; // boolean flagged

        Ext    ext       = null;

        try {

           guid      = im.getGUID();
           parentId  = im.getParentId();

           emailItem = im.getStreamMessage();
           encodingType = im.getEncodingType();

           created   = im.getCreated();
           received  = im.getReceived();
           modified  = im.getModified();
           read      = im.getRead();
           forwarded = im.getForwarded();
           replied   = im.getReplied();
           deleted   = im.getDeleted();
           flagged   = im.getFlagged();

           ext       = im.getExt();

        } catch (Exception e) {
           throw new EntityException("Error creating the foundation email " + e.getMessage());
        }

        mtmp.getUID().setPropertyValue(guid);

        mtmp.getParentId().setPropertyValue(parentId);

        mtmp.getEmailItem().setPropertyValue(emailItem);
        mtmp.getEmailItem().setEncoding(encodingType);

        if (created != null){
          mtmp.getCreated().setPropertyValue(created);
        }
        if (received != null){
          mtmp.getReceived().setPropertyValue(received);
        }
        if (modified != null){
          mtmp.getModified().setPropertyValue(modified);
        }

        if (read != null){
          mtmp.getRead().setPropertyValue(read);
        }
        if (forwarded != null){
          mtmp.getForwarded().setPropertyValue(forwarded);
        }
        if (replied != null){
          mtmp.getReplied().setPropertyValue(replied);
        }
        if (deleted != null){
          mtmp.getDeleted().setPropertyValue(deleted);
        }
        if (flagged != null){
          mtmp.getFlagged().setPropertyValue(flagged);
        }

        if (ext != null){
            mtmp.setExt(ext);
        }

        return mtmp;
    }


    /**
     *
     * create the foundation folder object
     *
     * @param f ItemFolder
     * @return Folder
     * @throws EntityException
     */
    public com.funambol.email.pdi.folder.Folder createFoundationFolder(
                                                               ItemFolder f)
      throws EntityException {

        com.funambol.email.pdi.folder.Folder fout = new
                                         com.funambol.email.pdi.folder.Folder();

        fout.getUID().setPropertyValue(f.getGUID());

        fout.getParentId().setPropertyValue(f.getParentId());

        fout.getName().setPropertyValue(f.getName());

        fout.getCreated().setPropertyValue(f.getCreated());

        fout.getAccessed().setPropertyValue(f.getAccessed());

        fout.getModified().setPropertyValue(f.getModified());

        fout.getRole().setPropertyValue(f.getRole());

        //fout.getAttributes().setPropertyValue(f.getAttributes);

        return fout;
    }

    /**
     *
     * insert the default folder if needed
     *
     *
     * @param df DefaultFolder
     * @param source_uri String
     * @param pID principal ID long
     * @throws EntityException
     */
    public void  insertDefaultFolder(DefaultFolder df,
                                     EmailFilter filter,
                                     String protocol,
                                     String source_uri,
                                     long pID,
                                     String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertDefaultFolder start");
        }

        String GUID       = null;
        String folderName = null;
        String parentId   = Def.FOLDER_ROOT_ID ;

        try {

            // input

            GUID       = Utility.createFolderGUID(parentId, df.getInboxId());
            folderName = df.getInboxName();
            addDefaultFolder(df, "I", GUID, folderName, parentId, protocol,
                                   source_uri, pID, username);

            // outbox
            GUID       = Utility.createFolderGUID(Def.FOLDER_ROOT_ID, df.getOutboxId());
            folderName = df.getOutboxName();
            addDefaultFolder(df, "O", GUID, folderName, parentId, protocol,
                    source_uri, pID, username);

            // sent
            GUID       = Utility.createFolderGUID(Def.FOLDER_ROOT_ID, df.getSentId());
            folderName = df.getSentName();
            addDefaultFolder(df, "S", GUID, folderName, parentId, protocol,
                    source_uri, pID, username);

            // drafts
            if (filter.getFolder() == Def.FILTER_FOLDER_IOSD ||
                filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                  GUID       = Utility.createFolderGUID(Def.FOLDER_ROOT_ID, df.getDraftsId());
                  folderName = df.getDraftsName();
                  addDefaultFolder(df, "D", GUID, folderName, parentId, protocol,
                                   source_uri, pID, username);
            }

            // trash
            if (filter.getFolder() == Def.FILTER_FOLDER_IOSDT) {
                  GUID       = Utility.createFolderGUID(Def.FOLDER_ROOT_ID, df.getTrashId());
                  folderName = df.getTrashName();
                  addDefaultFolder(df, "T", GUID, folderName, parentId, protocol,
                                   source_uri, pID, username);
            }

       } catch (EntityException ee){
           throw new EntityException("Error inserting the default folder", ee);
       }

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertDefaultFolder end" );
       }

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
     * @param session Session
     * @param loc Locale
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
                                   Session session,
                                   Locale loc,
                                   String source_uri,
                                   long principalId)
      throws EntityException {

        boolean isInFilter = true;

        try {

            // get Properties
            FlagProperties fp = Utility.getFlags(email);
            Message msg = createMessage(session, email, FID, false, fp);

            // check if this message is included in the filter

            if (filter.getId() != null) {
                // check GUID
                if (filter.getId().equals(GUID)){
                    isInFilter = true;
                } else {
                    isInFilter = false;
                }
            } else {
                
                // time filtering
                if ((filter.getTime()!=null)){
                                        
                    Date msgdate = UtilityDate.getDateForTimeFilter(msg, loc);
                    if (msgdate != null){                        
                        if (filter.getTime() != null) {

                            if (hasMatchedDate(msgdate, filter)) {
                                isInFilter = true;
                            } else {
                                isInFilter = false;
                            }
                        }
                    }
                                                            
                } else {
                    // check filter size
                    if (hasMatchedSize(msg,filter)){
                        isInFilter = true;
                    } else {
                        isInFilter = false;
                    }
                }
            }

        } catch (Exception e) {
                throw new EntityException(e);
        }

        return isInFilter;
    }

    /**
     *
     * insert invalid item
     *
     * @param GUID String
     * @param messageId String
     * @param headerDate java.util.Date
     * @param received java.util.Date
     * @param subject String
     * @param sender String
     * @param isEmail String
     * @param source_uri String
     * @param pID principal ID long
     * @throws EntityException
     */
    public void insertInvalidItem(String GUID,
                                  String messageId,
                                  java.util.Date headerDate,
                                  java.util.Date received,
                                  String subject,
                                  String sender,
                                  String isEmail,
                                  String source_uri,
                                  long pID,
                                  String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertInvalidItem for GUID " + GUID);
        }

        try {
           this.ed.insertInvalidItemInCache(GUID, messageId, headerDate,
                                            received, subject, sender,
                                            isEmail, source_uri, pID, username);
       } catch (EntityException ee){
           throw new EntityException("Error inserting the invalid item", ee);
       }

    }

    /**
     *
     * insert invalid item
     *
     * @param GUID String
     * @param source_uri String
     * @param pID principal ID long
     * @throws EntityException
     */
    public void insertInvalidItem(String GUID,
                                  String source_uri,
                                  long pID,
                                  String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertInvalidItem for GUID " + GUID);
        }

        try {
           this.ed.insertInvalidItemInCache(GUID, source_uri, pID, username);
       } catch (EntityException ee){
           throw new EntityException("Error inserting the invalid item", ee);
       }

    }

   /**
    * Checks if an email is already inserted in the db
    * and returns the GUID.
    *
    * @param session mail server session
    * @param serverItems all server items info CrcSyncItemInfo[] (I need the Message-ID)
    * @param parentId parent if of the email
    * @param ctmp the Email to check
    * @return the GUID
    * @throws EntityException
    */
    public String getEmailFromClause(Session session,
                                     Map serverItems,
                                     String parentId,
                                     Email ctmp)
      throws EntityException {

        String messageIdExt = null;
        String messageIdInt = null;
        String GUID         = null;
        Message msgExt      = null;

        // create message
        try {
            FlagProperties fp = Utility.getFlags(ctmp);
            msgExt = createMessage(session, ctmp, parentId, false, fp);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        try {
            messageIdExt = Utility.getHeaderMessageID(msgExt);
            if (messageIdExt != null) {
                SyncItemInfo syncItemInfo = null;

                Iterator values = serverItems.values().iterator();
                while (values.hasNext()) {
                    syncItemInfo = (SyncItemInfo)values.next();
                    messageIdInt = syncItemInfo.getMessageID();
                    if (messageIdInt != null){
                        if (messageIdExt.equals(messageIdInt)){
                            GUID = syncItemInfo.getGuid().getKeyAsString();
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new EntityException(e);
        }

        return GUID;

    }


    /**
     * check if in the Drafts folder there is an item without
     * the message-id
     *
     *
     * @param source_uri  String
     * @param principalId long
     * @throws EntityException
     */
    public void checkMessageIDforDrafts(String source_uri,
                                        long principalId,
                                        String username)
      throws EntityException {

        IMAPFolder f        = null;
        String     fullpath = null;
        EntityException finalExc = null;

        try {

            fullpath = this.ed.getFullPathFromFID(Def.FOLDER_DRAFTS_ID,
                                                  source_uri,
                                                  principalId,
                                                  username);

            if (fullpath != null) {
               f = (IMAPFolder)  this.cmsw.getMailDefaultFolder().getFolder(fullpath);
               if (f.exists()) {
                    timeStart = System.currentTimeMillis();
                    f.open(Folder.READ_WRITE);
                    this.ed.checkMessageIDforDrafts(f, this.cmsw);
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
                            log.trace("checkMessageIDforDrafts Execution Time: " +
                                    (timeStop - timeStart) + " ms");
                        }
                    }
                }
                catch (MessagingException me) {
                    finalExc = new EntityException(me);
                }
            }
        }
        if (finalExc != null) {
            throw finalExc;
        }

    }


   /**
     *
     * Check if the local DB folders are consistent with the MailBox folders
     *
     *
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void  insertCustomFolder(IMailServerWrapper msw,
                                    boolean allMailboxActivation,
                                    DBIDGenerator idFolderSpace,
                                    String source_uri,
                                    long principalId,
                                    String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertCustomFolder start");
        }

        try {

            if (msw instanceof ImapMailServerWrapper){

               ImapMailServerWrapper imsw = (ImapMailServerWrapper)msw;

                if (allMailboxActivation){
                    String     defFullpath;
                    IMAPFolder imapf;

                    // insert all folders in Inboxbox + inboxbox and so on
                    defFullpath = imsw.getDefaultFolder().getInboxName();
                    imapf       = imsw.getInboxFolder();
                    this.ed.addCustomFolderInDB(imapf, defFullpath, idFolderSpace,
                                               source_uri, principalId, username);

                    defFullpath = imsw.getDefaultFolder().getOutboxName();
                    imapf       = imsw.getOutboxFolder();
                    this.ed.addCustomFolderInDB(imapf, defFullpath, idFolderSpace,
                                               source_uri, principalId, username);

                    defFullpath = imsw.getDefaultFolder().getSentName();
                    imapf       = imsw.getSentFolder();
                    this.ed.addCustomFolderInDB(imapf, defFullpath, idFolderSpace,
                                               source_uri, principalId, username);

                    defFullpath = imsw.getDefaultFolder().getDraftsName();
                    imapf       = imsw.getDraftsFolder();
                    this.ed.addCustomFolderInDB(imapf, defFullpath, idFolderSpace,
                                               source_uri, principalId, username);

                    defFullpath = imsw.getDefaultFolder().getTrashName();
                    imapf       = imsw.getTrashFolder();
                    this.ed.addCustomFolderInDB(imapf, defFullpath, idFolderSpace,
                                               source_uri, principalId, username);

                }

            }

        } catch (EmailAccessException ee){
           throw new EntityException("Error getting all folder", ee);
        } catch (EntityException ee){
           throw new EntityException("Error inserting the custom folder", ee);
        }

        if (log.isTraceEnabled()) {
            log.trace("EntityManager insertCustomFolder end" );
        }

    }


    /**
     *
     * Check if the item is a folder or an email
     * using the SyncItemKey
     *
     *
     * @param parentId String
     * @param objectId String
     * @param source_uri String
     * @param principalId long
     * @return boolean
     * @throws EntityException
     */
    public boolean isEmail(String parentId,
                           String objectId,
                           String source_uri,
                           long principalId,
                           String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager isEmail start");
        }

        boolean ismail = false;
        ItemFolder it;


        try {

            String guid = Utility.createFolderGUID(parentId, objectId);

            it = this.ed.getFolderFromUID(guid, source_uri, principalId, username);

            if (it == null){
               ismail = true;
           }

        } catch (EntityException ee){
           throw new EntityException("Error determinig object type (mail/folder)",
                                     ee);
        }

        if (log.isTraceEnabled()) {
            log.trace("EntityManager isEmail end (isEmail :" + ismail + ")" );
        }

        return ismail;

    }

     /**
      * convert Message into ServerMessages
      *
      *
      * @param GUID unique folder id
      * @param name folder name
      * @param FID parent id
      * @return ItemFolder
      * @throws EntityException
      */
     public ItemFolder setItemFolder(String GUID,
                                        String name,
                                        String FID,
                                        String folderRole,
                                        String creationDate)
       throws EntityException {

         if (log.isTraceEnabled()) {
             log.trace("setItemFolder() start " + GUID);
         }

         ItemFolder    clientFoler;

         try {
             clientFoler = new ItemFolder(GUID,
                                         name,
                                         FID,
                                         creationDate,
                                         null, // accessed
                                         null, // modified
                                         folderRole,
                                         null // attributes
                                         );

         } catch (Exception e){
             throw new EntityException(e);
         }

         if (log.isTraceEnabled()) {
            log.trace("setItemFolder() - end ");
         }

         return clientFoler;

     }

     /**
      *
      * convert Message into ServerMessages
      *
      * @param GUID unique mail id
      * @param parentId String
      * @param msg Message
      * @param fp flags of the email FlagProperties
      * @param loc Locale
      * @param encodingType encodig for the <emailitem enc="quoted-printable">
      * @param ext extension fields Ext
      * @return ItemMessage
      * @throws EntityException
      */
     public ItemMessage setItemMessage(String GUID,
                                       String parentId,
                                       Message msg,
                                       FlagProperties fp,
                                       Locale loc,
                                       String encodingType,
                                       Ext ext)
       throws EntityException {

         ItemMessage   clientMsg;
         String        message_id;
         String        stream;
         String        created;
         String        received;
         String        modified;
         String        read;
         String        forwarded;
         String        replied;
         String        deleted;
         String        flagged;

         try {

             // header message-id
             message_id = Utility.getHeaderMessageID(msg);

             // message rfc2822
             stream = StringFromMessage(msg, encodingType);

             //origination date
             created = Utility.UtcFromDate(UtilityDate.getHeaderDate(msg, loc));
             //received date
             received = Utility.getHeaderReceived(msg, loc);
             //modified date
             modified = Utility.UtcFromDate(UtilityDate.getHeaderDate(msg, loc)); // @todo: tmp

             // flags
             read      = String.valueOf(fp.getSeen());
             replied   = String.valueOf(fp.getAnswered());
             flagged   = String.valueOf(fp.getFlagged());
             forwarded = String.valueOf(fp.getForwarded());
             deleted   = String.valueOf(msg.isSet(Flags.Flag.DELETED));

             clientMsg = new ItemMessage(GUID,
                                         parentId,
                                         message_id,
                                         msg,
                                         stream,
                                         encodingType,
                                         created,
                                         received,
                                         modified,
                                         read,
                                         forwarded,
                                         replied,
                                         deleted,
                                         flagged,
                                         ext);

         } catch (MessagingException e){
             throw new EntityException(e);
         }

         return clientMsg;

     }

   /**
    * this method creates the message with the Funambol
    * 'signature' (ads at the bottom of the email) and it sends the email
    * 
    * @param msg javaMail message to send; Message
    * @param session Mail Server opened Session
    * @param funSignature ads at the bottom of the email String
    * @param from
    * @param firstname
    * @param lastname
    * @return message that is sent (with from, signature, ...)
    * @throws com.funambol.email.exception.SendingException
    * @throws com.funambol.email.exception.EntityException
    */
    public Message sendEmail(Message msg,
                             Session session,
                             String funSignature,
                             String from,
                             String firstname,
                             String lastname)
     throws SendingException, EntityException {

       Message msgNew = null;

       if (funSignature != null && !funSignature.equals("")){
           // add signature and chek from header field
           try {
               msgNew = createMessageToBeSent(msg, 
                                              session,
                                              funSignature, 
                                              from,
                                              firstname, 
                                              lastname);
               if (msgNew != null){
                  sendEmail(msgNew);
               }
           } catch (SendingException e) {
              throw new SendingException(e);
           } catch (EntityException e) {
              throw new EntityException("Error creating new message to send" + e);
           }
       } else {
           // chek from header field
           try {
               msgNew = createMessageToBeSent(msg, 
                                              session,
                                              null,
                                              from,
                                              firstname, 
                                              lastname);
               if (msgNew != null){
                  sendEmail(msgNew);
               }
           } catch (SendingException e) {
              throw new SendingException(e);
           } catch (Exception e) {
              throw new EntityException("Error creating new message to send" + e);
           }
       }
       
       return msgNew;

    }

   /**
    * sends the email
    *
    * @param msg javaMail message to send; Message
    * @throws EntityException
    */
    private void sendEmail(Message msg) throws SendingException {

        String subject = null;

        try {
            subject = msg.getSubject();
            timeStart = System.currentTimeMillis();

            Transport.send(msg);

            timeStop = System.currentTimeMillis();
            if (log.isTraceEnabled()) {
                log.trace("Sending Execution Time: " + (timeStop - timeStart) + " ms");
            }

            logOutgoingMessage(msg);

       } catch (MessagingException me) {

            if (subject.length() > 30){
               subject = subject.substring(0,30) + "...";
            }

            String errorMessage = "Error sending email with subject: '" + subject + "'";

            Throwable innerException     = getInnerException(me);

            String innerExceptionMessage = innerException.getMessage();

            int innerExceptionCode       = getInnerExceptionCode(innerExceptionMessage);

            if (innerException instanceof SMTPAddressFailedException) {
                if (innerExceptionMessage != null){
                    errorMessage += " - Error Code: [" + innerExceptionCode +
                            "] - " + innerExceptionMessage;
                } else {
                    errorMessage += " - Error Code: [0] - Unknown error";
                }
            } else if (innerException instanceof AuthenticationFailedException) {
                errorMessage += " - Error Code: [401] - User authentication failed on SMTP server";
            } else if (innerException instanceof javax.net.ssl.SSLException) {
                errorMessage += " - Error Code: [601] - Incorrect user configuration";
            } else {
                errorMessage += " - Error Code: [0] - Unknown error";
            }

            log.error(errorMessage);
            log.error(innerException);
            //throw new SendingException(innerException);
            throw new SendingException(errorMessage);
        }
    }



  //---------------------------------------------------------- PROTECTED METHODS

    /**
     *
     * @param msgdate Message
     * @param filter EmailFilter
     * @return true if message date match the filter date
     */
    protected boolean hasMatchedDate(Date msgdate, EmailFilter filter){

        boolean check = false;

        Date filterDate = filter.getTime();

        String timeFilterClause = filter.getTimeClause();

        if (timeFilterClause.equalsIgnoreCase("EQ") ||
            timeFilterClause.equalsIgnoreCase("GT") ||
            timeFilterClause.equalsIgnoreCase("GE")) {

            if (Utility.d1Afterd2(msgdate, filterDate)){
                check = true;
            }


        } else if (timeFilterClause.equalsIgnoreCase("LT") ||
                   timeFilterClause.equalsIgnoreCase("LE")) {

            if (Utility.d1Befored2(msgdate, filterDate)){
                check = true;
            }

        }

        return check;
    }


   /**
     *
     * @param msg Message
     * @param filter EmailFilter
     * @return true if message date match the filter date
     */
    protected boolean hasMatchedSize(Message msg, EmailFilter filter){

        boolean check = false;

        // if get all message return true
        if (filter.getSize() == Def.FILTER_SIZE_H_B_A){
            return true;
        } else {
            /**
             * @todo
             */

        }

        // tmp
        check = true;

        return check;
    }    
    
    /**
     * create the email object to add in db and in serverItems
     *
     */
    protected SyncItemInfo createInfo(
            String GUID, 
            Message msg, 
            UpdatedMessage um,
            String protocol)
     throws EntityException {

        SyncItemInfo sii = null;

        // create the new values
        try {

            if (msg == null){
                msg = um.getJavaMessage();
            }

            // Message-ID
            String messageID  = Utility.getHeaderMessageID(msg);
            // header Date
            Date headerDate = UtilityDate.getDateForTimeFilter(msg, null);
            // CRC
            //String dateForCRC = Utility.getHeaderDateForCRC(msg);
            String flagList = Utility.createFlagsList(msg);
            long crc = Utility.createCRC(flagList, protocol);
            // Received
            Date received   = UtilityDate.getHeaderReceived(msg, null);
            // Subject
            String subject = ""; //Utility.getHeaderSubject(msg);
            // sender
            String sender = ""; //Utility.getHeaderSender(msg);
            // status
            String status = "N";
            if (msg == null){
                // update
                status = "U";
            } else {
                status = "N";
            }

            //
            sii = new SyncItemInfo(new SyncItemKey(GUID), crc, messageID,
                                   headerDate, received,
                                   subject, sender,
                                   null, "Y", "Y", status);

        } catch (Exception e){
            log.error("Error creating Item Info for Caching System ", e);
        }

        return sii;

    }

    /**
     * create the email object to update in db and in serverItems
     *
     */
    protected long createInfo(UpdatedMessage um, String protocol)
     throws EntityException {

        long crc = 0;
        try {
            Message msg = um.getJavaMessage();
            //String messageID  = Utility.getHeaderMessageID(msg);
            //String dateForCRC = Utility.getHeaderDateForCRC(msg);
            //crc = Utility.createCRC(msg, messageID, dateForCRC);
            String flagList = Utility.createFlagsList(msg);
            crc = Utility.createCRC(flagList, protocol);
        } catch (Exception e){
            log.error("Error creating Item Info for Caching System ", e);
        }

        return crc;

    }

    /**
     * create the folder object to add in db and in serverItems
     *
     */
    protected SyncItemInfo createInfo(String GUID){

        SyncItemInfo sii = new SyncItemInfo(new SyncItemKey(GUID), 0,
                                            null, null,null,
                                            null, null, null,
                                            null, null, null);
        return sii;
    }

    /**
     * Check if the message is a valid message and can be added to the Mail 
     * Server or in stead has to be refused.
     * The message has to be refused if it does not have subject, body and sent 
     * date.
     * @param msg The message to be chacked
     * @throws com.funambol.framework.engine.source.RefusedItemException If the 
     * message has to be refused
     * @throws com.funambol.email.exception.EntityException If an error occurs 
     * while chacking the message validity
     */
    protected void checkIfItHasToBeRefused(Message msg)
            throws RefusedItemException, EntityException {
        try {
            Date sentDate = msg.getSentDate();
            String subject = msg.getSubject();
            Object content = msg.getContent();
            if ((sentDate == null) &&
                    (subject == null || subject.length() == 0) &&
                    (content == null || (content instanceof String && ((String) content).length() == 0))) {
                throw new RefusedItemException("Unable to add empty email " +
                        "server side, delete Email client side ");
            }
        } catch (IOException ex) {
            throw new EntityException("Error verifying email content", ex);
        } catch (MessagingException ex) {
            throw new EntityException("Error verifying email content", ex);
        }
    }

     /**
     * this method is called when you want to log traffic info about outgoing
     * messages.
     * A log event related to an outgoing email message is just logged once.
     *
     * @param message the message that has been sent.
     */
    protected void logOutgoingMessage(Message message) {
        if(message!=null && trafficLog.isTraceEnabled()) {
                trafficLog.trace("[OUTGOING] Email with Message-ID "+
                                 Utility.getHeaderMessageID(message)+
                                 " has been successfully sent by ["+
                                 Utility.getHeaderSender(message)+"].");
        }
    }

    /**
     * this method is called when you want to log traffic info about incoming
     * messages.
     * Since an incoming message may be sent more than once to the client, a log
     * event related to the same incoming email message may be triggered many times.
     *
     * @param message the message that has been retrieved from the mail server
     */
    protected void logIncomingMessage(Message message) {
        if(message!=null && trafficLog.isTraceEnabled()) {
                trafficLog.trace("[INCOMING] Email with Message-ID "+
                                 Utility.getHeaderMessageID(message)+
                                 " sent by ["+
                                 Utility.getHeaderSender(message)+
                                 "] has been successfully downloaded from the mailserver.");
        }
    }

  //------------------------------------------------------------ PRIVATE METHODS


    /**
     * convert Message in a String (RFC2822)
     *
     * @param msg Message
     * @param encodingType encodig for the <emailitem enc="quoted-printable">
     * @param applyCalendarPatch patch for text/calendar email
     * @return String
     */
  private String StringFromMessage(Message msg, String encodingType)
      throws MessagingException {

        OutputStream outStream;
        String streamContentAsString = null;

        if (msg == null) {
            return null;
        }

        outStream = new ByteArrayOutputStream();

        try {
            if (Def.ENCODE_QUOTED_PRINTABLE.equalsIgnoreCase(encodingType)) {
                msg.writeTo(MimeUtility.encode(outStream, Def.ENCODE_QUOTED_PRINTABLE));
            } else {
                msg.writeTo(outStream);
            }
        } catch (IOException e) {
            throw new MessagingException(e.getMessage(), e);
        }

        streamContentAsString = outStream.toString();

        return streamContentAsString;
    }


    /**
     *
     * insert the default folder if needed
     *
     *
     * @param df DefaultFolder
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    private void  addDefaultFolder(DefaultFolder df,
                                   String flag,
                                   String GUID,
                                   String folderName,
                                   String parentId,
                                   String protocol,
                                   String source_uri,
                                   long pID,
                                   String username)
      throws EntityException {

        try {

          if (protocol.equals(Def.PROTOCOL_IMAP)){
              // IMAP PROTOCOL
              addDefaultFolderIMAP(df, flag, GUID, folderName, parentId, source_uri, pID, username);
          } else {
              // POP3 PROTOCOL
              addDefaultFolderPOP3(df, flag, GUID, folderName, parentId, source_uri, pID, username);
          }
       } catch (EntityException ee){
           throw new EntityException(ee);
       }

    }

    /**
     *
     * insert the default folder if needed
     *
     *
     * @param df DefaultFolder
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    private void  addDefaultFolderIMAP(DefaultFolder df,
                                       String flag,
                                       String GUID,
                                       String folderName,
                                       String parentId,
                                       String source_uri,
                                       long pID,
                                       String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager addDefaultFolderIMAP start");
        }

        String folderInDB     = null;
        Folder folderInMS     = null;
        Folder folderToInsert = null;

        try {

           folderInDB = this.ed.getFullPathFromGUID(GUID, source_uri, pID, username);

           if (folderInDB != null && GUID.equals(Def.FOLDER_INBOX_GUID)){

               // INBOX
               // is in the mail server and is INBOX do nothing
               // (the inbox is already in the mail server)

           } else {

               // inbox at the first time
               // rest of the folders

               folderInMS = this.ed.getDefaultFolder(this.cmsw, folderName);

               if (folderInMS == null){

                   // is not in the mail server
                   if (folderInDB == null) {

                       // it is not in the DB
                       try {
                           this.ed.addDefaultFolderInMailServer(this.cmsw, folderName);
                       } catch (Exception e){
                           // some IMAP servers don't allow to insert folder (i.e. AOL)
                           if (log.isTraceEnabled()) {
                               log.trace("EntityManager Error adding folder "+folderName+" in mail server");
                           }
                       }

                       this.ed.addDefaultFolderInDB(GUID, parentId, folderName,
                                                    source_uri, pID, username);

                       setDefaultFolderPresence(df, flag, Def.PROTOCOL_IMAP);

                   } else{
                      // ... but is in the DB --> error
                   }

               } else {

                   // is in the mail server
                   if (folderInDB != null){
                      // is in the DB --> do nothing
                      setDefaultFolderPresence(df, flag, Def.PROTOCOL_IMAP);
                   } else {
                      // is not in the DB --> insert in the DB
                      this.ed.addDefaultFolderInDB(GUID, parentId,  folderName,
                                                      source_uri, pID, username);
                      setDefaultFolderPresence(df, flag, Def.PROTOCOL_IMAP);
                   }

               }

           }


       } catch (EntityException ee){
           throw new EntityException("Error inserting the IMAP default folder", ee);
       }

    }


    /**
     *
     * insert the default folder if needed
     *
     *
     * @param df DefaultFolder
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    private void  addDefaultFolderPOP3(DefaultFolder df,
                                       String flag,
                                       String GUID,
                                       String folderName,
                                       String parentId,
                                       String source_uri,
                                       long pID,
                                       String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("EntityManager addDefaultFolderPOP3 start");
        }

        String folderInDB = null;

        try {

             folderInDB = this.ed.getFullPathFromGUID(GUID, source_uri, pID, username);

             if (folderInDB == null) {
                // it is not in the DB --> insert in the DB
                this.ed.addDefaultFolderInDB(GUID, parentId,  folderName,
                                                source_uri, pID, username);
                setDefaultFolderPresence(df, flag, Def.PROTOCOL_POP3);
             }

       } catch (EntityException ee){
           throw new EntityException("Error inserting the POP3 default folder", ee);
       }

    }

    /**
     *
     * insert the default folder if needed
     *
     *
     * @param df DefaultFolder
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    private void  setDefaultFolderPresence(DefaultFolder df,
                                           String flag,
                                           String protocol)
      throws EntityException {


        if        (flag.equals("I")){
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                df.setIsInboxInMailServer(true);
            } else {
                df.setIsInboxInMailServer(true);
            }
        } else if (flag.equals("O")){
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                df.setIsOutboxInMailServer(true);
            } else {
                df.setIsOutboxInMailServer(true);
            }
        } else if (flag.equals("S")){
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                df.setIsSentInMailServer(true);
            } else {
                df.setIsSentInMailServer(true);
            }
        } else if (flag.equals("D")){
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                df.setIsDraftsInMailServer(true);
            } else {
                df.setIsDraftsInMailServer(false);
            }
        } else if (flag.equals("T")){
            if (protocol.equals(Def.PROTOCOL_IMAP)){
                df.setIsTrashInMailServer(true);
            } else {
                df.setIsTrashInMailServer(false);
            }
        }

    }


    /**
     * 
     * @param serverItems
     * @param filterID
     * @return
     */
    private Map<String, SyncItemInfo> getFilteredItemsByID(
            Map serverItems, 
            String filterID){
        
        Map<String, SyncItemInfo> newServerItems = new HashMap<String, SyncItemInfo>();

        String GUID = null;
        boolean isEmail = false;
        SyncItemInfo crcii = null;

        Iterator values = serverItems.values().iterator();
        while (values.hasNext()) {
            crcii    = (SyncItemInfo)values.next();
            GUID     = crcii.getGuid().getKeyAsString();
            isEmail  = Utility.booleanFromString(crcii.getIsEmail());
            if (GUID != null){
                if (!isEmail){
                    // folder
                    newServerItems.put(GUID, crcii);
                } else {
                    // email
                    if (GUID.equals(filterID)){
                        newServerItems.put(GUID, crcii);
                    }
                }
            }
        }
        return newServerItems;
    }
        

    /**
     * Gets the innermost exception that causes a given exception.
     * @param e given exception
     * @return the exception that causes <code>e</code>
     */
    private Throwable getInnerException (Throwable e) {
        
        Throwable prec = e;

        Throwable t = e.getCause();
        while (t != null) {
            prec = t;
            t = t.getCause();
        }
        
        return prec;
    }
    
   /**
     * Gets the error code of the given exception.
     * SMTP 421 - Service not available, closing transmission channel.
     * SMTP 450 - Requested mail action not taken: mailbox unavailable.
     *            Mailbox is locked and busy.
     * SMTP 451 - Requested action aborted: local error in processing.
     *            Error processing request.
     * SMTP 452 - Requested action not taken: insufficient system storage.
     * SMTP 500 - Syntax error, command unrecognized.
     * SMTP 501 - Syntax error in parameters or arguments.
     * SMTP 502 - Command not implemented.
     * SMTP 503 - Bad sequence of commands.
     * SMTP 504 - Command parameter not implemented.
     * SMTP 550 - Requested action not taken: mailbox unavailable.
     *            Mailbox not found.
     * SMTP 551 - User not local; please try <forward-path>.
     *            User mailbox is known, but mailbox not on this server
     * SMTP 552 - Requested mail action aborted: exceeded storage allocation.
     *            Storage limit exceeded.
     * SMTP 553 - Requested action not taken: mailbox name not allowed.
     *            Invalid mailbox name syntax.
     * SMTP 554 - Transaction failed.
     *
     * @param innerMessage error message
     * @return the error code
     */
    private int getInnerExceptionCode (String innerMessage) {
        if (innerMessage == null){
            return 0;
        }
        int[] errorList = {421, 450, 451, 452, 500, 501, 502, 503,
        504, 550, 551, 552, 553, 554 };
        int indexOfError = -1;
        for (int i=0; i<errorList.length; i++){
            indexOfError = innerMessage.indexOf(String.valueOf(errorList[i]));
            if (indexOfError == -1){
                continue;
            } else {
                return errorList[i];
            }
        }
        return 0;
    }


}
