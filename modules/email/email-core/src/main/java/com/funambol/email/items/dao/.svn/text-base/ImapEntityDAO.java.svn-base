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
package com.funambol.email.items.dao;


import java.util.Date;
import java.util.ArrayList;
import javax.mail.Message;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import com.sun.mail.imap.IMAPFolder;
import javax.mail.search.SearchTerm;
import javax.mail.search.NotTerm;
import javax.mail.search.AndTerm;
import javax.mail.search.HeaderTerm;
import javax.mail.search.MessageIDTerm;
import com.funambol.email.exception.EntityException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.email.transport.ImapMessageCountListener;
import com.funambol.email.transport.ImapSemaphore;
import com.funambol.email.util.Utility;
import com.funambol.email.util.Def;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.UpdatedMessage;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.sun.mail.imap.IMAPMessage;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * This class implements methods to access data
 * in Mail Server datastore.
 *
 * @version $Id: ImapEntityDAO.java,v 1.2 2008-05-18 15:18:42 nichele Exp $
 */
public class ImapEntityDAO extends EntityDAO {


    // -------------------------------------------------------------- Properties

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**   */
    public static ImapSemaphore uid_has_been_set = new ImapSemaphore(false);

    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public ImapEntityDAO() {
    }

    /**
     *
     */
    public ImapEntityDAO(String serverType) throws EntityException {
        super(serverType);
    }
    
    /**
     * @param jndiDataSourceName String
     * 
     * @deprecated Since v66, the db has been splitted in core db and user db so
     * the jndi names are now hardcoded. Use the constructor without jndiDataSourceName
     */
    public ImapEntityDAO(String jndiDataSourceName, String serverType) throws EntityException {
        this(serverType);
    }

    // -----------------------------------------------------------Public Methods

    /**
     * gets all Messages for the caching system
     *
     * @param f      IMAPFolder
     * @param filter EmailFilter
     * @return Message[]
     * @throws EntityException
     */
    public Message[] getAllEmails(IMAPFolder f, String FID, EmailFilter filter)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllEmails " + f.getName());
        }


        Message[] messages;

        try {
            if (FID.equals(Def.FOLDER_SENT_ID)  ||
                FID.equals(Def.FOLDER_TRASH_ID)) {
                int max = filter.getMaxSentNum();
                int mNumber = f.getMessageCount();
                if (mNumber > max){
                    if(serverType.equals(Def.SERVER_TYPE_EXCHANGE)) {
                        SearchTerm st = this.getOnlyMailTerm();
                        Message[] messagesTmp = f.getMessages((mNumber-(max-1)),mNumber);
                        messages = f.search(st, messagesTmp);
                    } else {
                        messages = f.getMessages((mNumber-(max-1)),mNumber);
                    }
                } else {
                    if(serverType.equals(Def.SERVER_TYPE_EXCHANGE)) {
                        SearchTerm st = this.getOnlyMailTerm();
                        messages = f.search(st);
                    } else {
                        messages = f.getMessages();
                    }
                }
            } else {
                if(serverType.equals(Def.SERVER_TYPE_EXCHANGE)) {
                    SearchTerm st = this.getOnlyMailTerm();
                    messages = f.search(st);
                } else {
                    messages = f.getMessages();
                }
            }
        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        if (log.isTraceEnabled()) {
            if (messages != null) {
                log.trace("number of items " + messages.length);
            } else {
                log.trace("number of items 0");
            }
        }

        return messages;

    }

    /**
     * gets all default folder infos
     *
     * @param idFilter   String
     * @param timeFilter Date
     * @return ItemFolder[]
     * @throws EntityException
     */
    public ItemFolder[] getAllDefaultFoldersInfo(String idFilter,
            Date timeFilter)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllDefaultFolders ");
        }

        ItemFolder[] its = {new ItemFolder(Def.FOLDER_INBOX_GUID,
                Def.FOLDER_INBOX_ENG,
                Def.FOLDER_ROOT_ID,
                null, null, null, null, null),
        new ItemFolder(Def.FOLDER_OUTBOX_GUID,
                Def.FOLDER_OUTBOX_ENG,
                Def.FOLDER_ROOT_ID,
                null, null, null, null, null),
        new ItemFolder(Def.FOLDER_SENT_GUID,
                Def.FOLDER_SENT_ENG,
                Def.FOLDER_ROOT_ID,
                null, null, null, null, null),
        new ItemFolder(Def.FOLDER_DRAFTS_GUID,
                Def.FOLDER_DRAFTS_ENG,
                Def.FOLDER_ROOT_ID,
                null, null, null, null, null),
        new ItemFolder(Def.FOLDER_TRASH_GUID,
                Def.FOLDER_TRASH_ENG,
                Def.FOLDER_ROOT_ID,
                null, null, null, null, null)};

        if (log.isTraceEnabled()) {
            log.trace("DAO end getAllDefaultFolders ");
        }

        return its;

    }


    /**
     * gets all folder infos
     *
     * @param filter      EmailFilter
     * @param folder      IMAPFolder
     * @param defFolder   ItemFolder
     * @param source_uri  String
     * @param principalId long
     * @return map with all the info about the folder in the mail server
     * @throws EntityException
     */
    public Map getAllFoldersInfo(EmailFilter filter,
                                 IMAPFolder folder,
                                 ItemFolder defFolder,
                                 String source_uri,
                                 long principalId,
                                 String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllFoldersInfo ");
        }

        // it keeps all subfolders of the input folder
        ArrayList fs = new ArrayList();
        fs.add(defFolder);

        // get all subfolders
        try {
            searchSubFolderInfo(folder, fs);
        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        // create the hashmap with only the good folder
        LinkedHashMap its = new LinkedHashMap();

        if (fs.size() > 0) {

            // get only the folder with the GUID
            SyncItemInfo infofolder;
            ItemFolder ifolder;
            String fullpath;
            String GUID;

            for (int i = 0; i < fs.size(); i++) {

                ifolder = (ItemFolder) fs.get(i);

                // the fullpath is saved temp in this field
                fullpath = ifolder.getParentId();

                GUID = this.getGUIDFromFullPath(fullpath, source_uri,  principalId, username);

                if (GUID != null) {

                    infofolder = new SyncItemInfo(new SyncItemKey(GUID), 0,
                                                  null, null, null, null, null,
                                                  null, null, null, null);

                    its.put(GUID, infofolder);

                }
            }

        }

        return its;

    }


    /**
     * gets all default Folder
     *
     * @param idFilter   String
     * @param timeFilter Date
     * @return String[]
     * @throws EntityException
     */
    public String[] getAllDefaultFolders(String idFilter, Date timeFilter)
    throws EntityException {

        return new String[]{
            Def.FOLDER_INBOX_GUID,
            Def.FOLDER_OUTBOX_GUID,
            Def.FOLDER_SENT_GUID,
            Def.FOLDER_DRAFTS_GUID,
            Def.FOLDER_TRASH_GUID};

    }


    /**
     * gets all Folder
     *
     * @param filter      EmailFilter
     * @param folder      IMAPFolder
     * @param defFullpath String
     * @param source_uri  String
     * @param principalId long
     * @return String[]
     * @throws EntityException
     */
    public String[] getAllFolders(EmailFilter filter,
            IMAPFolder folder,
            String defFullpath,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        String[] allFolder = null;

        // it keeps all subfolders of the input folder
        ArrayList fs = new ArrayList();

        // add the main folder
        fs.add(defFullpath);

        // get all subfolders
        try {
            searchSubFolderFullNames(folder, fs);
        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        //
        String fullpath;
        String GUID;
        int num = fs.size();
        if (num > 0) {
            allFolder = new String[num];
            for (int i = 0; i < num; i++) {
                fullpath = (String) fs.get(i);
                GUID = getGUIDFromFullPath(fullpath, source_uri, principalId, username);
                allFolder[i] = GUID;
            }
        }

        if (allFolder != null) {
            if (log.isTraceEnabled()) {
                log.trace("number of folders " + allFolder.length);
            }
        }

        return allFolder;

    }


    /**
     * serch a document using the header Message-ID
     *
     * @param f          IMAPFolder
     * @param FID        String
     * @param message_id String
     * @return EISMessage
     * @throws EntityException
     */
    public String getEmailFromMessageID(IMAPFolder f,
            String FID,
            String message_id)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getItemByMessageID Header Message-ID: " + message_id);
        }

        Message msg;
        String GUID = null;

        try {

            SearchTerm st = new MessageIDTerm(message_id);
            Message[] messages = f.search(st);

            if (messages != null) {

                if (messages.length > 0) {

                    msg = messages[0];

                    String messid = null;
                    String FMID   = null;
                    String UIDV   = null;

                    if (msg != null) {

                        messid = Utility.getHeaderMessageID(msg);
                        FMID   = String.valueOf(f.getUID(msg));
                        UIDV   = String.valueOf(f.getUIDValidity());

                        if (log.isTraceEnabled()) {
                            log.trace(" found Message-ID: " + messid);
                        }

                        if (message_id.equalsIgnoreCase(messid)) {

                            GUID = Utility.createIMAPGUID(FID, FMID, UIDV);

                            if (log.isTraceEnabled()) {
                                log.trace("Email already Exist with GUID " + GUID);
                            }
                        }
                    }
                }
            }
        } catch (MessagingException me) {
            throw new EntityException(me);
        }

        return GUID;
    }

    /**
     * @param f   IMAPFolder
     * @param uid long
     * @throws EntityException
     */
    public void removeEmail(IMAPFolder f, long uid) throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeEmail ");
        }

        try {
            Message msgToDelete = f.getMessageByUID(uid);
            if (msgToDelete != null) {
                msgToDelete.setFlag(Flags.Flag.DELETED,true);
            }
        } catch (MessagingException e) {
            throw new EntityException(e);
        }

    }


    /**
     * @param f IMAPFolder
     * @throws EntityException
     */
    public void removeAllEmail(IMAPFolder f)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeAllEmail ");
        }

        Message[] messages;
        try {

            messages = f.getMessages();

            for (int i = 0; i < messages.length; i++) {
                messages[i].setFlag(Flags.Flag.DELETED, true);
            }

        } catch (MessagingException e) {
            throw new EntityException(e);
        }

    }


    /**
     * @param f IMAPFolder
     * @throws EntityException
     */
    public void removeFolderInServer(IMAPFolder f)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeFolder ");
        }

        try {
            if (f.delete(true)) {
                if (log.isTraceEnabled()) {
                    log.trace("Folder successfully removed.");
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Folder not removed.");
                }
            }
        } catch (MessagingException e) {
            throw new EntityException(e);
        }

    }


    /**
     * This method handles the update procedure of an email.
     * Maybe the email can change only for DRAFTS.
     * In the rest of the folders we can change only
     * the properties i.e. read, forworded.
     *
     * @param FID  String
     * @param FMID String
     * @param f   IMAPFolder
     * @param msgNew  Message
     * @param fp  FlagProperties
     * @return UpdatedMessage
     * @throws EntityException
     */
    public UpdatedMessage updateEmail(String FID,
                                      String FMID,
                                      IMAPFolder f,
                                      Message msgNew,
                                      FlagProperties fp)
       throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start updateItem FMID: " + FMID);
        }

        long    uid    = -1;
        Message msgTmp = null;
        UpdatedMessage um = new UpdatedMessage();

        try {

            Message msgOld = f.getMessageByUID(Long.parseLong(FMID));

            if (msgOld != null){

                if (FID.equalsIgnoreCase(Def.FOLDER_DRAFTS_ID)) {

                    // delete old message
                    msgOld.setFlags(new Flags(Flags.Flag.DELETED), true);

                    // add new message
                    if(getServerType().equals(Def.SERVER_TYPE_COURIER)) {
                        uid = addEmailByNextUID(f, msgNew);
                    } else {
                        uid = addEmailByListener(f, msgNew);
                    }

                    // get the new document
                    if (uid  >= 0){
                        msgTmp = this.getEmailFromUID(f, FID, uid);
                    } else {
                        msgTmp = msgOld;
                        uid = Long.parseLong(FMID);
                    }

                    um.setJavaMessage(msgOld);
                    um.setFMID(uid);

                } else {

                    msgTmp = msgOld;
                    uid = Long.parseLong(FMID);

                    um.setJavaMessage(msgOld);
                    um.setFMID(uid);
                }

                // flags
                msgTmp.setFlags(new Flags(Flags.Flag.SEEN)    , fp.getSeen());
                msgTmp.setFlags(new Flags(Flags.Flag.ANSWERED), fp.getAnswered());
                msgTmp.setFlags(new Flags(Flags.Flag.FLAGGED) , fp.getFlagged());
                //msgTmp.setFlags(new Flags(Flags.Flag.FORWARDED), fp.getForwarded());

            } else {

                um.setJavaMessage(msgNew);
                um.setFMID(Long.parseLong(FMID));

            }


        } catch (Exception e) {
            throw new EntityException(e);
        }

        return um;

    }


    /**
     * This method insert an given message to an given folder
     * and returns its message UID
     * using com.funambol.email.transport.ImapMessageCountListener
     *
     * @param f   IMAPFolder
     * @param msg Message
     * @return EISMessage
     * @throws EntityException
     */
    public long addEmailByListener(IMAPFolder f, Message msg)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start insertItem");
        }

        long uid = -1;

        try {

            // insert
            Message[] messages = {msg};

            ImapMessageCountListener imcl = new ImapMessageCountListener();

            f.addMessageCountListener(imcl);

            uid_has_been_set = new ImapSemaphore(false);

            f.appendMessages(messages);

            uid_has_been_set.waitForTrue(1000);

            long[] lastUIDs = imcl.getLastUIDs();

            uid = lastUIDs[0];

            // retry
            if (uid == -1){
                Thread.sleep(1000);
                lastUIDs = imcl.getLastUIDs();
                uid = lastUIDs[0];
            }


            if (uid == -1){
                throw new EntityException("Error addind the message in the " +
                        f.getName() + " folder; " + "" +
                        "The connector can't get the message UID");
            }

            f.removeMessageCountListener(imcl);

        } catch (InterruptedException e) {
            throw new EntityException(e);
        } catch (MessagingException e) {
            throw new EntityException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("UID of the new message:" + uid);
        }

        return uid;
    }


    /**
     * This method insert an given message to an given folder
     * and returns its message UID
     * using com.sun.mail.imap.IMAPFolder.getUIDNext()
     *
     * @param f   IMAPFolder
     * @param msg Message
     * @return EISMessage
     * @throws EntityException
     */
    public long addEmailByNextUID(IMAPFolder f, Message msg)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start insertItem");
        }

        long nextUID = -1;

        try {

            // insert
            if(f.isOpen()) {
                f.close(true);
            }

            nextUID = f.getUIDNext();

            f.open(Folder.READ_WRITE);
            Message[] messages = {msg};

            f.appendMessages(messages);

            if (nextUID == -1){
                throw new EntityException("Error addind the message in the " +
                        f.getName() + " folder; " + "" +
                        "The connector can't get the message UID");
            }

            // save
            if(f.isOpen()) {
                f.close(true);
            }
            f.open(Folder.READ_WRITE);

        } catch (MessagingException e) {
            throw new EntityException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("UID of the new message:" + nextUID);
        }

        return nextUID;
    }

    /**
     * This method inserts the folder in the MailServer
     * and the path in the Local DB.
     * Return the ID using the IdSpaceGenerator.
     *
     * @param fullPath       String
     * @param parentId       String
     * @param folderToInsert Folder
     * @param type           int
     * @param idFolderSpace  IdSpaceGenerator
     * @param source_uri     String
     * @param principalId    long
     * @return String
     * @throws EntityException
     */
    public String addFolder(String fullPath,
                            String parentId,
                            Folder folderToInsert,
                            int type,
                            DBIDGenerator idFolderSpace,
                            String source_uri,
                            long principalId,
                            String username)
      throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("ImapEmailDAO addFolder start");
        }

        String GUID;

        try {

            // insert in the MailServer
            folderToInsert.create(type);

            // insert in the DB
            GUID = insertFolder(fullPath, parentId, idFolderSpace,
                                source_uri, principalId, username);

        } catch (Exception e) {
            throw new EntityException(e);
        }

        if (log.isTraceEnabled()) {
            log.trace("DAO end insertFolder UID " + GUID);
        }

        return GUID;

    }


    /**
     * serch a document using UID; for IMAP4 protocol UID is long
     *
     * @param f   IMAP Folder
     * @param uid email imap id
     * @return Message
     * @throws EntityException
     */
    public Message getEmailFromUID(IMAPFolder f, String FID, long uid)
    throws EntityException {
        Message msg;
        try {
            msg = f.getMessageByUID(uid);
        } catch (MessagingException me) {
            throw new EntityException(me);
        }
        return msg;
    }

    /**
     * @param folder starting folder
     * @param fs     all folders container
     * @throws MessagingException
     */
    public void searchSubFolderFullNames(IMAPFolder folder, ArrayList fs)
    throws MessagingException {
        javax.mail.Folder[] folderList = folder.list();
        IMAPFolder f;
        String fullpath;
        for (int i = 0; i < folderList.length; i++) {
            f = ((IMAPFolder) folderList[i]);
            fullpath = f.getFullName();
            fs.add(fullpath);
            searchSubFolderFullNames(f, fs);
        }
    }

    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }

    /**
     * Copy an email from a folder to another.
     * @param sourceFolder 
     * @param destinationFolder
     * @param mailId id of the email to be copied
     */    
    public void copyEmail (
            IMAPFolder sourceFolder, 
            IMAPFolder destinationFolder,
            long mailId) throws MessagingException{

        Message message = sourceFolder.getMessageByUID(mailId);
        if (message != null && !message.isExpunged()) {
            sourceFolder.copyMessages(new Message[]{message}, destinationFolder);
        }
        
    }

    //---------------------------------------------------------- PRIVATE METHODS


    /**
     * @todo: create the GUID for the folder
     *
     * @param folder starting folder
     * @param fs     all folders container
     * @throws MessagingException
     */
    private void searchSubFolderInfo(IMAPFolder folder, ArrayList fs)
    throws MessagingException {
        javax.mail.Folder[] folderList = folder.list();
        IMAPFolder f;
        ItemFolder it;
        String GUID = ""; // @todo
        for (int i = 0; i < folderList.length; i++) {
            f = ((IMAPFolder) folderList[i]);
            it = new ItemFolder(GUID,
                                f.getName(),
                                f.getFullName(), // will be replced by parent id
                                null, null, null, null, null);
            fs.add(it);
            searchSubFolderInfo(f, fs);
        }
    }

    /**
     * get all messages, exclude Exchange Contact/Calendar
     */
    private SearchTerm getOnlyMailTerm() {

        SearchTerm st;

        // exclude contact
        SearchTerm st1 = new NotTerm(new HeaderTerm("content-class",
                "urn:content-classes:person"));
        // exclude calendar
        SearchTerm st2 = new NotTerm(new HeaderTerm("content-class",
                "urn:content-classes:appointment"));
        // exclude task
        SearchTerm st3 = new NotTerm(new HeaderTerm("content-class",
                "urn:content-classes:task"));
        // exclude note
        SearchTerm st4 = new NotTerm(new HeaderTerm("content-class",
                "urn:content-classes:note"));

        SearchTerm cc   = new AndTerm(st1, st2);
        SearchTerm cct  = new AndTerm(cc, st3);
        SearchTerm cctn = new AndTerm(cct, st4);

        return cctn;

    }
}
