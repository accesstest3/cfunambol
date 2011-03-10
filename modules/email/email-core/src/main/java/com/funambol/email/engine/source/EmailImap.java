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
package com.funambol.email.engine.source;

import com.funambol.email.items.manager.ContentProviderInfo;
import com.funambol.framework.engine.source.RefusedItemException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.util.ArrayList;
import java.sql.Timestamp;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.email.pdi.mail.*;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.email.items.manager.ImapEntityManager;
import com.funambol.email.model.SendResult;
import com.funambol.email.exception.SendingException;
import com.funambol.email.pdi.folder.Folder;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;
import java.util.Map;

/**
 * This class implements common methods of EmailIMAPSyncSource
 *
 * version $Id: EmailImap.java,v 1.3 2008-06-03 09:14:57 testa Exp $
 */
public class EmailImap extends EmailGeneric implements IEmailGeneric {

    // --------------------------------------------------------------- Constants

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // -------------------------------------------------------------- Properties

    /**  */
    protected ImapEntityManager   iem   = null;

    /**  */
    protected EmailSyncSource     ess   = null;

    // ------------------------------------------------------------- Constructor

    public EmailImap() {
    }

    /**
     *
     * @param _essc
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public EmailImap(EmailSyncSourceWrapper _essc) throws SyncSourceException {

        super(_essc);

        this.ess = _essc.getEmailSyncSource();

        try {
            if (this.iem == null) {
                this.iem = new ImapEntityManager(this.ess);
            }
        } catch (Exception e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error connecting mails: " +
                    e.getMessage(), e);
        }
    }


    // ---------------------------------------------------------- Public Methods


    /**
     * @param syncItem SyncItem
     * @return boolean
     * @throws SyncSourceException
     */
    public boolean isSyncItemInFilterClause(SyncItem syncItem)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("IMAP isSyncItemInFilterClause(syncItem)");
        }

        boolean isInFilter = false;

        char status = ' ';
        try {
            status = getSyncItemStateFromId(syncItem.getKey());
        } catch (SyncSourceException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error checking 'is in filter' for the item " +
                    syncItem, e);
        }

        try {
            String content = Utility.getContentFromSyncItem(syncItem);
            boolean isemail = isEmailFromContent(content);
            if (isemail) {
                isInFilter = isEmailInFilter(content, syncItem, status);
            } else {
                isInFilter = isFolderInFilter(content, syncItem);
            }
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error checking 'is in filter' for the item " +
                    syncItem, e);
        }

        return isInFilter;
    }

    /**
     * @param syncItemKey SyncItemKey
     * @return boolean
     * @throws SyncSourceException
     */
    public boolean isSyncItemInFilterClause(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("IMAP isSyncItemInFilterClause(syncItemKey)");
        }

        boolean isInFilter = false;

        String key  = (String) syncItemKey.getKeyValue();

        char status = ' ';
        try {
            status = getSyncItemStateFromId(new SyncItemKey(key));
        } catch (SyncSourceException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error checking 'is in filter' for the item " + key, e);
        }

        try {
            String FID  = Utility.getKeyPart(key,1);
            String FMID = Utility.getKeyPart(key,2);
            String UIDV = Utility.getKeyPart(key,3);
            boolean isemail = isEmailFromKey(FID, FMID,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
            if (isemail) {
                isInFilter = isEmailInFilter(key, FID, FMID, status);
            } else {
                isInFilter = isFolderInFilter(key);
            }
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error checking 'is in filter' for the item " + key, e);
        }

        return isInFilter;
    }


    /**
     * @param syncItemKey SyncItemKey
     * @param s Timestamp
     * @param engineSoftDelete boolean
     * @throws SyncSourceException
     */
    public void removeSyncItem(SyncItemKey syncItemKey,
            Timestamp s,
            boolean engineSoftDelete)
            throws SyncSourceException {

        boolean configurationSoftDelete =
                this.ess.mailServerAccount.getMailServer().getIsSoftDelete();

        if (log.isTraceEnabled()) {
            log.trace("remove item engineSoftDelete: " + engineSoftDelete +
                    " config. soft delete " + configurationSoftDelete);
        }

        try {
            if (engineSoftDelete || configurationSoftDelete) {
                removeItemSoft(syncItemKey);
            } else {
                removeItem(syncItemKey);
            }
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error deleting the item " + syncItemKey, e);
        }

    }


    /**
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getSyncItemFromId start");
        }

        SyncItem syncItem = null;

        String key = (String) syncItemKey.getKeyValue();

        // check if the item is invalid
        boolean isItemValid = isValidItem(key);

        if (isItemValid){

            String FID  = Utility.getKeyPart(key,1);
            String FMID = Utility.getKeyPart(key,2);
            String UIDV = Utility.getKeyPart(key,3);

            if (log.isTraceEnabled()) {
                log.trace("getSyncItemFromId with [FID: " + FID +
                        " ; FMID: " + FMID + " ; UIDV: " + UIDV + "]");
            }

            char status = ' ';

            try {
                status = getSyncItemStateFromId(syncItemKey);
            } catch (SyncSourceException e) {
                try {
                    this.ess.MSWrapper.releaseConnection();
                } catch (EmailAccessException e1){
                    throw new SyncSourceException("Error closing store connection: " +
                            e1.getMessage(), e1);
                }
                throw new SyncSourceException("Error adding the item " + syncItem, e);
            }


            try {
                boolean isEmail = isEmailFromKey(FID, FMID,
                        this.ess.getSourceURI(),
                        this.ess.principalId,
                        this.ess.userName);
                if (isEmail) {
                    syncItem = getEmailFromId(syncItemKey, status);
                } else {
                    syncItem = getFolderFromId(syncItemKey);
                }
            } catch (EntityException e) {
                try {
                    this.ess.MSWrapper.releaseConnection();
                } catch (EmailAccessException e1){
                    throw new SyncSourceException("Error closing store connection: " +
                            e1.getMessage(), e1);
                }
                throw new SyncSourceException("Error adding the item " + syncItem, e);
            }

        } else {
            log.error("Item " + key + " is invalid!");
        }

        return syncItem;

    }


    /**
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getAllSyncItems start ");
        }

        String[]   allIds;

        try {
            //NOTE: in the serverItems there are the folders id also
            allIds = iem.getAllEmails(
                    this.ess.filter, 
                    this.ess.serverItems);
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error reading getAllSyncItems : " +
                    e.getMessage(), e);
        }

        SyncItemKey[] syncItemKeys = null;
        if (allIds != null) {
            // remove the GUID null (this is the case of the new folders)
            ArrayList onlyGoodIds = new ArrayList();
            String key;
            for (int i = 0; i < allIds.length; i++) {
                key = allIds[i];
                if (key != null) {
                    onlyGoodIds.add(key);
                }
            }
            int numfin = onlyGoodIds.size();
            // create the syncItemKeys without null
            syncItemKeys = new SyncItemKey[numfin];
            for (int i = 0; i < numfin; i++) {
                syncItemKeys[i] = new SyncItemKey(allIds[i]);
            }
        }

        return syncItemKeys;
    }


    /**
     * @return map with all Mail Server CrcSyncItemInfo
     * @throws SyncSourceException
     */
    public Map getAllSyncItemInfo()
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getAllSyncItemInfo start");
        }

        Map allInfo    = null;
        Map emailInfo  = null;
        Map folderInfo = null;

        try {
            emailInfo = this.iem.getAllEmailsInfo(this.ess.filter,
                    this.ess.userName,
                    this.ess.mailServerAccount.getMaxEmailNumber(),
                    this.ess.allMailboxActivation);
            folderInfo = this.iem.getAllFoldersInfo(this.ess.filter,
                    this.ess.allMailboxActivation,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
            allInfo = Utility.mergeMap(emailInfo, folderInfo, null, null, null);
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error reading emails/folders: " +
                    e.getMessage(), e);
        }

        return allInfo;
    }

    /**
     * @throws SyncSourceException
     */
    public void clearServerInfo() throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("cleanServerInfo start");
        }

        try {
            this.iem.clearAllItems(this.ess.allMailboxActivation,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error deleting all emails/folders: " +
                    e.getMessage(), e);
        }

    }

    /**
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException {

        SyncItemKey[] syncItemKeys;

        try {
            String content = Utility.getContentFromSyncItem(syncItem);
            boolean isemail = isEmailFromContent(content);
            if (isemail) {
                syncItemKeys = getEmailFromTwin(content, syncItem);
            } else {
                syncItemKeys = getFolderFromTwin(content, syncItem);
            }
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error getting the item for twin" +
                    syncItem, e);
        }

        return syncItemKeys;
    }

    /**
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

        try {
            String content = Utility.getContentFromSyncItem(syncItem);
            boolean isEmail = isEmailFromContent(content);
            if (isEmail) {
                syncItem = addEmail(content, syncItem);
            } else {
                syncItem = addFolder(content, syncItem);
            }
        } catch (EntityException e) {

            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            String msgErr = "Error adding item: ";
            if (syncItem != null){
                msgErr = msgErr + syncItem.getKey().getKeyAsString() ;
            }
            throw new SyncSourceException(msgErr, 500);

        } catch (SendingException e) {

            // in this case we don't close the store because we could have more 
            // than one email to send. if the error occurs in the first sending
            // email procedure the rest of the emails cannot be sent because
            // the store is closed
            //try {
            //    this.ess.MSWrapper.releaseConnection();
            //} catch (EmailAccessException e1){
            //    throw new SyncSourceException("Error closing store connection: " +
            //            e1.getMessage(), e1);
            //}
            String msgErr = e.toString();
            msgErr = msgErr.replaceAll("com.funambol.email.exception.SendingException:", "");
            msgErr = msgErr.trim();
            //String msgErr = "Error sending item: ";
            //if (syncItem != null){
            //    msgErr = msgErr + syncItem.getKey().getKeyAsString() ;
            //}
            throw new SyncSourceException(msgErr, 500);

        }

        return syncItem;
    }


    /**
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem updateSyncItem(SyncItem syncItem) throws SyncSourceException {

        try {
            String content = Utility.getContentFromSyncItem(syncItem);
            String key = syncItem.getKey().getKeyAsString();
            String parentId = Utility.getKeyPart(key,1);
            String objectId = Utility.getKeyPart(key,2);
            boolean isEmail = isEmailFromContent(content);
            if (isEmail) {
                syncItem = updateEmail(parentId, objectId, content, syncItem);
            } else {
                syncItem = updateFolder(parentId, objectId, content, syncItem);
            }
        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error updateing the item " + syncItem, e);
        }

        return syncItem;

    }

    // --------------------------------------------------------- Private Methods

    /**
     *
     * @param syncItemKey SyncItemKey
     * @throws SyncSourceException
     */
    private void removeItem(SyncItemKey syncItemKey)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("removeItem  GUID:" + syncItemKey );
        }

        String key      = syncItemKey.getKeyAsString();
        String parentId = Utility.getKeyPart(key,1);
        if (parentId == null){
            // I have to set the parent
            parentId = "O";
        }
        String objectId = Utility.getKeyPart(key,2);

        if (log.isTraceEnabled()) {
            log.trace("remove item FID: " + parentId +" FMID: " + objectId);
        }

        boolean isEmail = isEmailFromKey(parentId, objectId,
                this.ess.getSourceURI(),
                this.ess.principalId,
                this.ess.userName);

        if (isEmail) {
            removeEmail(parentId, objectId, key);
        } else {
            removeFolder(parentId, objectId);
        }

    }

    /**
     *
     * @param syncItemKey SyncItemKey
     * @throws SyncSourceException
     */
    private void removeItemSoft(SyncItemKey syncItemKey)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("removeItemSoft  GUID:" + syncItemKey );
        }

        String key      = syncItemKey.getKeyAsString();
        String parentId = Utility.getKeyPart(key,1);
        if (parentId == null){
            // I have to set the parent
            parentId = "O";
        }
        String objectId = Utility.getKeyPart(key,2);

        if (log.isTraceEnabled()) {
            log.trace("remove item soft FID: " + parentId +" FMID: " + objectId);
        }

        boolean isEmail = isEmailFromKey(parentId, objectId,
                this.ess.getSourceURI(),
                this.ess.principalId,
                this.ess.userName);

        if (isEmail) {
            removeEmailSoft(parentId, objectId, key);
        } else {
            removeFolderSoft(parentId, objectId);
        }

    }

    /**
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem addEmail(String content, SyncItem syncItem)
    throws EntityException, SendingException, RefusedItemException {

        String[] keys = Utility.getKeyParts(syncItem);
        String FID    = keys[0];
        String FMID   = keys[1];

        if (log.isTraceEnabled()) {
            log.trace("addEmail [FID: " + FID + " luid: " + FMID + "]");
        }

        Email  emailNew      = null;
        Email  insertedEmail = null;
        String GUID          = null;

        try {
            emailNew = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        //
        // If syncItem is already stored on the server then it has not to be 
        // added to the server itself, because it was really a twin item.
        //
        String val = emailNew.getEmailItem().getPropertyValueAsString();        
        if (val == null || val.equals("")) {
            throw new RefusedItemException("Trying to add a mail item with only flags: "+FMID);
        }
        
        try {

            // add and send the mail
            insertedEmail = this.iem.addEmail(FID,
                    FMID,
                    emailNew,
                    this.ess.serverItems,
                    this.ess.filter,
                    this.ess.funambolSignature,
                    this.ess.mailServerAccount.getMsAddress(),
                    this.ess.firstname,
                    this.ess.lastname,
                    this.ess.userName,
                    this.ess.getSourceURI(),
                    this.ess.principalId);

            // message add and eventually sent without error, set to true the result
            if (FID.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)){
                if (insertedEmail != null) {
                    GUID = (String) insertedEmail.getUID().getPropertyValue();
                    String idtmp = Utility.convertOutboxIDToSentID(GUID);
                    SendResult sr = new SendResult(idtmp, FMID, true, false);
                    this.ess.sentItems.add(sr);
                }
            }

        } catch (EntityException e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage);
            throw new EntityException(errorMessage, e);
        } catch (SendingException e) {
            // message with error in sending procedure
            if (FID.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)){
                SendResult sr = new SendResult(GUID, FMID, false, false);
                this.ess.sentItems.add(sr);
            }
            throw new SendingException(e.getMessage());
        }


        // create the output syncItem
        try {
            if (insertedEmail != null) {
                syncItem = createSyncItem(this.ess,
                        insertedEmail,
                        SyncItemState.NEW,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            }
        } catch (Exception e) {
            throw new EntityException("Error creating SyncItem: " +  syncItem, e);
        }

        return syncItem;

    }


    /**
     *
     * check if the message is included in the filter clause
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private boolean isEmailInFilter(String key,
            String FID,
            String FMID,
            char status)
            throws EntityException {

        boolean isInFilter = false;

        try {
            isInFilter = this.iem.isEmailInFilter(null,
                    this.ess.filter,
                    key, FID, FMID, status,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error checking the item: " +
                    e.getMessage() , e);
        }


        return isInFilter;

    }

    /**
     *
     * check if the message is included in the filter clause
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private boolean isEmailInFilter(String content,
            SyncItem syncItem,
            char status)
            throws EntityException {

        boolean isInFilter = false;

        String key    = syncItem.getKey().getKeyAsString();
        String[] keys = Utility.getKeyParts(syncItem);
        String FID    = keys[0];
        String FMID   = keys[1];

        Email email = null;
        try {
            email = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting Item Content", e);
        }
        
        try {
            isInFilter = this.iem.isEmailInFilter(email,
                    this.ess.filter,
                    key, FID, FMID, status,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error matching mail with filter ", e);
        }

        return isInFilter;

    }

    /**
     *
     * check if the message is included in the filter clause
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private boolean isFolderInFilter(String key)
    throws EntityException {
        boolean isInFilter = true;
        return isInFilter;

    }

    /**
     *
     * check if the message is included in the filter clause
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private boolean isFolderInFilter(String content, SyncItem syncItem)
    throws EntityException {
        boolean isInFilter = true;
        return isInFilter;

    }

    /**
     * Before inserting the folder it checks if the folder
     * already exists. This step is necessary because the
     * "twin" method returns always "item not found"
     * <p/>
     * the server in the slow sync will always call the Add method
     * (there is no filter provided by the Twin Methods)
     * <p/>
     * The add method must choose between
     * 1) the folder already exists (returns the old GUID)
     * 2) the folder must be created (returns the new GUID)
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem addFolder(String content, SyncItem syncItem)
    throws EntityException {
        Folder ctmpNew;
        try {
            ctmpNew = HelperForIOConversion.getFoundationFolderFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        Folder insertedFolder = null;
        String GUID;

        String name     = ctmpNew.getName().getPropertyValueAsString();
        String parentId = syncItem.getParentKey().getKeyAsString();

        if (parentId.equalsIgnoreCase("/")) {
            parentId = Def.FOLDER_ROOT_ID;
        }

        try {
            // check if the folder already exists
            GUID = this.iem.getFolderFromName(name,
                    parentId,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);

            // if null I have to insert the folder otherwise
            // the folder already exists
            if (GUID == null) {
                insertedFolder = this.iem.addFolder(name,
                        parentId,
                        this.ess.defaultFolder,
                        this.ess.idFolderSpace,
                        this.ess.serverItems,
                        this.ess.getSourceURI(),
                        this.ess.principalId,
                        this.ess.userName);
            }

        } catch (Exception e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage);
            throw new EntityException(errorMessage, e);
        }

        if (log.isTraceEnabled()) {
            log.trace("End insert folder. start create SyncItem");
        }

        // create the output syncItem
        try {
            if (insertedFolder != null) {
                syncItem = createSyncItem(this.ess,
                        insertedFolder,
                        SyncItemState.NEW,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            }
        } catch (Exception e) {
            throw new EntityException("Error creating SyncItem: " +
                    syncItem, e);
        }
        return syncItem;
    }

    /**
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem getEmailFromId(SyncItemKey syncItemKey, char status)
    throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getEmailFromId start");
        }

        SyncItem syncItem = null;
        Email    mail     = null;

        String key = syncItemKey.getKeyAsString();

        try {
            String parentId = Utility.getKeyPart(key,1);
            String mailId   = Utility.getKeyPart(key,2);
            
            // 
            // Gets the token for this email from the fnbl_inbox_table using:
            // - guid (key)
            // - username
            //
            // Information to be passed to the manager are:
            // - user id
            // - email token
            //
            String userName = this.ess.userName;
            String encryptedToken = this.iem.ied.getToken(userName, key);
            String token = EncryptionTool.decrypt(encryptedToken);
            String attachProviderUrl = getContentProviderURL();
            
            ContentProviderInfo contentProviderInfo = new ContentProviderInfo(token, userName, attachProviderUrl);

            mail = this.iem.getEmailFromUID(this.ess.filter,
                    mailId,
                    parentId,
                    status,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName,
                    contentProviderInfo);
        } catch (EntityException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null){
                if (errorMessage.indexOf(Def.ERR_FILTERING_EMAIL) != -1){
                    // set as invalid the item in the cache, in the next sync will be ignored
                    insertInvalidItem(key, null, null, null, null, null, "Y");
                    throw new EntityException(e.getMessage(), e);
                } else {
                    throw new EntityException("Error in getting Mail " + e.getMessage(), e);
                }
            } else {
                throw new EntityException("Error in getting Mail " + e.getMessage(), e);
            }
        } catch (EncryptionException e){
            throw new EntityException("Error while decrypting token", e);
        }

        if (mail != null) {
            try {
                syncItem = createSyncItem(this.ess,
                        mail,
                        status,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            } catch (Exception e) {
                throw new EntityException("Error in creating SyncItem ", e);
            }
        }

        return syncItem;

    }

    /**
     * the folder syncItemKey is the GUID in the
     * local DB.
     *
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem getFolderFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getFolderFromId start");
        }

        SyncItem syncItem = null;
        Folder   folder   = null;

        String GUID = syncItemKey.getKeyAsString();

        try {
            folder = this.iem.getFolderFromUID(this.ess.filter,
                    GUID,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (Exception e) {
            throw new SyncSourceException("Error in getting Folder ", e);
        }

        // get item status
        char status = getSyncItemStateFromId(syncItemKey);

        if (folder != null) {
            try {
                syncItem = createSyncItem(this.ess,
                        folder,
                        status,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            } catch (Exception e) {
                throw new SyncSourceException("Error in creating SyncItem ", e);
            }
        }

        return syncItem;

    }

    /**
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    private SyncItemKey[] getEmailFromTwin(String content,
            SyncItem syncItem)
            throws EntityException {

        String[] keys = Utility.getKeyParts(syncItem);
        String FID  = keys[0];
        String FMID = keys[1];

        String GUID;

        if (log.isTraceEnabled()) {
            log.trace("getEmailFromTwin with FID " + FID + " FMID " + FMID);
        }

        Email email;
        try {
            email = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        try {
            GUID = this.iem.getEmailFromClause(this.ess.MSWrapper.getSession(),
                    this.ess.serverItems,
                    FID,
                    email);
        } catch (EntityException e) {
            throw new EntityException("Error getting twin", e);
        }

        SyncItemKey[] syncItemKeys;
        if (GUID != null) {
            syncItemKeys = new SyncItemKey[1];
            syncItemKeys[0] = new SyncItemKey(GUID);
        } else {
            syncItemKeys = new SyncItemKey[0];
        }

        return syncItemKeys;
    }

    /**
     * this method return always "twin not found"
     * the server will always call the Add method
     * The add method must choose between
     * 1) the folder already exists (returns the old GUID)
     * 2) the folder must be created (returns the new GUID)
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    private SyncItemKey[] getFolderFromTwin(String content,
            SyncItem syncItem)
            throws EntityException {

        SyncItemKey[] syncItemKeys;
        String GUID;

        String key = (String) syncItem.getKey().getKeyValue();
        if (log.isTraceEnabled()) {
            log.trace("getFolderFromTwin with UID: " + key);
        }

        Folder ctmpNew;
        try {
            ctmpNew = HelperForIOConversion.getFoundationFolderFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        String name = ctmpNew.getName().getPropertyValueAsString();
        String parentId = syncItem.getParentKey().getKeyAsString();

        if (parentId.equalsIgnoreCase("/")) {
            parentId = Def.FOLDER_ROOT_ID;
        }

        try {
            // check if the folder already exists
            GUID = this.iem.getFolderFromName(name, parentId,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);

            if (GUID != null) {
                syncItemKeys = new SyncItemKey [1];
                syncItemKeys[0] = new SyncItemKey(GUID);
            } else {
                syncItemKeys = new SyncItemKey[0];
            }


        } catch (Exception e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage);
            throw new EntityException(errorMessage, e);
        }

        return syncItemKeys;
    }

    /**
     * @param parentId String
     * @param mailId   mail id in the folder not unique
     * @throws SyncSourceException
     */
    private void removeEmail(String parentId, String mailId, String GUID)
    throws EntityException {

        try {
            this.iem.removeEmail(parentId,  mailId, GUID,  this.ess.serverItems,
                    this.ess.userName,  this.ess.getSourceURI(),
                    this.ess.principalId);
        } catch (EntityException e) {
            throw new EntityException("Error removing email", e);
        }
    }

    /**
     * @param parentId String
     * @param mailId   mail id in the folder not unique
     * @throws SyncSourceException
     */
    private void removeEmailSoft(String parentId, String mailId, String GUID)
    throws EntityException {

        try {
            this.iem.removeEmailSoft(parentId, GUID, this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error removing email", e);
        }

    }

    /**
     * @param parentId String
     * @param folderId String
     * @throws SyncSourceException
     */
    private void removeFolder(String parentId, String folderId)
    throws EntityException {

        try {
            this.iem.removeFolder(folderId,
                    this.ess.serverItems,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error removing folder", e);
        }

    }

    /**
     * @param parentId String
     * @param folderId String
     * @throws SyncSourceException
     */
    private void removeFolderSoft(String parentId, String folderId)
    throws EntityException {

        try {
            this.iem.removeFolderSoft(folderId,
                    this.ess.getSourceURI(),
                    this.ess.principalId);
        } catch (EntityException e) {
            throw new EntityException("Error removing folder", e);
        }

    }

    /**
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem updateEmail(String parentId,
            String mailId,
            String content,
            SyncItem syncItem)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("updateSyncItem FID " + parentId + " FMID " + mailId);
        }

        Email emailNew;
        try {
            emailNew = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        Email emailOut;

        try {
            emailOut = this.iem.updateEmail(parentId,
                    mailId,
                    emailNew,
                    this.ess.serverItems,
                    this.ess.userName,
                    this.ess.mailServerAccount.getMailServer().getProtocol(),
                    this.ess.getSourceURI(),
                    this.ess.principalId);
        } catch (Exception e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage);
            throw new EntityException(errorMessage, e);
        }

        if (log.isTraceEnabled()) {
            log.trace("End update. start create SyncItem");
        }

        // create the output syncItem
        try {
            if (emailOut != null) {
                syncItem = createSyncItem(this.ess,
                        emailOut,
                        SyncItemState.UPDATED,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            }
        } catch (Exception e) {
            throw new EntityException("Error creating SyncItem: " + syncItem, e);
        }

        return syncItem;

    }

    /**
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem updateFolder(String parentId,
            String folderId,
            String content,
            SyncItem syncItem)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("updateFolder FID " + parentId + " and FMID " + folderId);
        }
        Folder folderNew;
        try {
            folderNew = HelperForIOConversion.getFoundationFolderFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        Folder folderOut;
        try {
            folderOut = this.iem.updateFolder(parentId,
                    folderId,
                    folderNew,
                    this.ess.serverItems);
        } catch (Exception e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage);
            throw new EntityException(errorMessage, e);
        }

        if (log.isTraceEnabled()) {
            log.trace("End update folder. start create SyncItem");
        }

        // create the output syncItem
        try {
            if (folderOut != null) {
                syncItem = createSyncItem(this.ess,
                        folderOut,
                        SyncItemState.UPDATED,
                        this.ess.deviceTimeZone,
                        this.ess.deviceCharset);
            }
        } catch (Exception e) {
            throw new EntityException("Error creating SyncItem: " + syncItem, e);
        }

        return syncItem;

    }

}
