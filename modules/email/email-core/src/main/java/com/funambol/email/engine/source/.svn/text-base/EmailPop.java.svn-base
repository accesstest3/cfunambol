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
import java.sql.Timestamp;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.email.util.Def;
import com.funambol.email.util.Utility;
import com.funambol.email.items.manager.PopEntityManager;
import com.funambol.email.model.SendResult;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.exception.SendingException;
import com.funambol.email.pdi.folder.Folder;
import com.funambol.framework.tools.encryption.EncryptionException;
import com.funambol.framework.tools.encryption.EncryptionTool;
import java.util.Map;

/**
 * This class implements common methods of EmailPOPSyncSource
 *
 * version $Id: EmailPop.java,v 1.3 2008-06-03 09:14:57 testa Exp $
 */
public class EmailPop extends EmailGeneric implements IEmailGeneric {

    // --------------------------------------------------------------- Constants

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // -------------------------------------------------------------- Properties

    /**     */
    protected PopEntityManager  pem  = null;

    /**  */
    protected EmailSyncSource   ess  = null;

    // ------------------------------------------------------------- Constructor

    public EmailPop() {
        if (log.isTraceEnabled()) {
            log.trace("POP3 default constructor");
        }
    }

    /**
     *
     * @param _essc
     * @throws com.funambol.framework.engine.source.SyncSourceException
     */
    public EmailPop(EmailSyncSourceWrapper _essc) throws SyncSourceException {

        super(_essc);

        if (log.isTraceEnabled()) {
            log.trace("POP3 EmailSyncSourceWrapper Constructor");
        }

        this.ess = _essc.getEmailSyncSource();

        try {
            if (this.pem == null) {
                this.pem = new PopEntityManager(this.ess);
            }
        } catch (EmailAccessException e) {
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
            log.trace("POP3 isSyncItemInFilterClause(syncItem)");
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
            throw new SyncSourceException("Error getting status " +
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
            log.trace("POP3 isSyncItemInFilterClause(syncItemKey)");
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
            throw new SyncSourceException("Error checking 'is in filter' for the item " +
                    key, e);
        }

        try {
            String FID  = Utility.getKeyPart(key,1);
            String FMID = Utility.getKeyPart(key,2);
            boolean isemail = isEmailFromKey(FID, FMID,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
            if (isemail) {
                isInFilter = isEmailInFilter(key, status);
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
            throw new SyncSourceException("Error checking 'is in filter' for the item " +
                    key, e);
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
        return true;        
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
        return true;
    }

    /**
     * @param syncItemKey SyncItemKey
     * @param s           Timestamp
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
            throw new SyncSourceException("Error deleting the item " +
                    syncItemKey, e);
        }

    }

    /**
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     * @see com.funambol.framework.engine.source.SyncSource
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getSyncItemFromId start");
        }

        SyncItem syncItem = null;

        String key = (String) syncItemKey.getKeyValue();

        // check if the item is invalid
        boolean isItemValid = this.isValidItem(key);

        if (isItemValid){

            String parentId = Utility.getKeyPart(key,1);
            String objectId = Utility.getKeyPart(key,2);

            if (log.isTraceEnabled()) {
                log.trace("get item [FID: " + parentId + "; FMID: " + objectId + "]");
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
                throw new SyncSourceException("Error getting status " + syncItem, e);
            }

            try {
                boolean isEmail = isEmailFromKey(parentId, objectId,
                        this.ess.getSourceURI(),
                        this.ess.principalId,
                        this.ess.userName);
                if (isEmail) {
                    syncItem = getEmailFromId(syncItemKey, parentId, objectId, status);
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
                throw new SyncSourceException("Error getting the item " + syncItem, e);
            }

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
            // in the map there are the folders ids also
            allIds = this.pem.getAllEmails(this.ess.filter, this.ess.serverItems);
        } catch (Exception e) {
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
            syncItemKeys = new SyncItemKey[allIds.length];
            for (int i = 0; i < allIds.length; i++) {
                syncItemKeys[i] = new SyncItemKey(allIds[i]);
            }
        }

        return syncItemKeys;
    }

    /**
     * @return mail with all mail server SyncItemInfo
     * @throws SyncSourceException
     */
    public Map getAllSyncItemInfo() throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getAllSyncItemInfo start");
        }

        Map allInfo    = null;
        Map emailInfo  = null;
        Map folderInfo = null;

        try {

            emailInfo = this.pem.getAllEmailsInfo(this.ess.filter,
                    this.ess.userName,
                    this.ess.mailServerAccount.getMaxEmailNumber(),                    
                    this.ess.syncMode,
                    this.ess.getSourceURI(),
                    this.ess.principalId);

            folderInfo = this.pem.getAllFoldersInfo(this.ess.filter);

            allInfo = Utility.mergeMap(emailInfo, folderInfo, null, null, null);

        } catch (EntityException e) {
            try {
                this.ess.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error reading folders: " +
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
            this.pem.clearAllItems(this.ess.getSourceURI(), this.ess.principalId);
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
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {

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
     * This method can only send the mail but not
     * append the mail in the outbox and sent folder.
     *
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
                syncItem = addFolder(syncItem);
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
            boolean isEmail = isEmailFromContent(content);
            String key = syncItem.getKey().getKeyAsString();
            String parentId = Utility.getKeyPart(key,1);
            String objectId = Utility.getKeyPart(key,2);
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
            throw new SyncSourceException("Error updating the item " + syncItem, e);
        }

        return syncItem;

    }

    // --------------------------------------------------------- Private Methods


    /**
     * the key is composed by: parentId/objId (mailid or folderid)
     * The key is created by the connector and it always
     * creates the syncItemKey using parentId and mailId
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
            log.trace("remove item in FID: " + parentId + "; FMID: " + objectId);
        }

        boolean isEmail = isEmailFromKey(parentId, objectId,
                this.ess.getSourceURI(),
                this.ess.principalId,
                this.ess.userName);

        if (isEmail) {
            removeEmail(parentId, objectId, key);
        } else {
            removeFolder(key);
        }

    }

    /**
     * the key is composed by: parentId/objId (mailid or folderid)
     * The key is created by the connector and it always
     * creates the syncItemKey using parentId and mailId
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
            log.trace("remove item soft in FID: " + parentId + "; FMID: " + objectId);
        }

        boolean isEmail = isEmailFromKey(parentId, objectId,
                this.ess.getSourceURI(),
                this.ess.principalId,
                this.ess.userName);

        if (isEmail) {
            removeEmailSoft(parentId, objectId, key);
        }

    }

    /**
     *
     * This method can only send the mail but it cannot
     * append the mail in the folders (i.e. inbox, outbox and sent).
     * If the folder ID is different form OUTBOX_ID,
     * the method returns the input syncItem.
     * <p/>
     * We can't insert an email using POP3 protocol, in this case
     * we save the mail in the SERVER LOCAL DB not in the MAIL SERVER DB
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem addEmail(String content, SyncItem syncItem)
    throws EntityException, SendingException, RefusedItemException {

        Email emailNew      = null;
        Email insertedEmail = null;
        String GUID         = null;

        String[] keys = Utility.getKeyParts(syncItem);
        String FID    = keys[0];
        String FMID   = keys[1];

        if (log.isTraceEnabled()) {
            log.trace("addEmail folder: " + FID + " luid: " + FMID);
        }

        // handle only email in 'outbox'
        if (FID.equalsIgnoreCase(Def.FOLDER_OUTBOX_ID)) {

            try {
                emailNew = HelperForIOConversion.getFoundationMailFromXML(content);
            } catch (EntityException e) {
                throw new EntityException("Error converting foundation: " +
                        e.getMessage(), e);
            }

            try {
                //add and send the email
                insertedEmail = this.pem.addEmail(FID,
                        FMID,
                        emailNew,
                        this.ess.serverItems,
                        this.ess.idSentSpace,
                        this.ess.saveOnlyHeader,
                        this.ess.funambolSignature,
                        this.ess.filter,
                        this.ess.mailServerAccount.getMsAddress(),
                        this.ess.firstname,
                        this.ess.lastname,
                        this.ess.getSourceURI(),
                        this.ess.principalId,
                        this.ess.userName);
                if (insertedEmail != null) {
                    // message add and sent without error, set to true the result
                    GUID = (String) insertedEmail.getUID().getPropertyValue();
                    String idtmp = Utility.convertOutboxIDToSentID(GUID);
                    SendResult sr = new SendResult(idtmp, FMID, true, true);
                    this.ess.sentItems.add(sr);
                }
            } catch (EntityException e) {
                String errorMessage = "Error setting the item: " + syncItem;
                log.error(errorMessage, e);
                throw new EntityException(errorMessage, e);
            } catch (SendingException e) {
                // message sent with error, set to false the result
                SendResult sr = new SendResult(GUID, FMID, false, true);
                this.ess.sentItems.add(sr);
                throw new SendingException(e.getMessage());
            }

            // create the output syncItem
            try {
                if (insertedEmail != null) {
                    syncItem = createSyncItem(this.ess,
                            insertedEmail,
                            SyncItemState.NEW,
                            this.ess.deviceTimeZone,
                            this.ess.deviceCharset );
                }
            } catch (Exception e) {
                throw new EntityException("Error creating SyncItem: " + syncItem, e);
            }

        }

        return syncItem;

    }


    /**
     * the email syncItemKey is composed by
     * FID + separator + FMID
     * The FID must be converted in the fullPath
     *
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws EntityException
     */
    private SyncItem getEmailFromId(SyncItemKey syncItemKey,
            String parentId,
            String mailId,
            char status)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("getEmailFromId start");
        }

        SyncItem syncItem = null;
        Email    mail     = null;

        try {
            
            // 
            // Gets the token for this email from the fnbl_inbox_table using:
            // - guid (key)
            // - username
            //
            // Information to be passed to the manager are:
            // - user id
            // - email token
            //
            String key = (String) syncItemKey.getKeyValue();
            String userName = this.ess.userName;
            String encryptedToken = this.pem.ped.getToken(userName, key);
            String token = EncryptionTool.decrypt(encryptedToken);
            String attachProviderUrl = getContentProviderURL();
            ContentProviderInfo contentProviderInfo = new ContentProviderInfo(token, userName, attachProviderUrl);

            mail = this.pem.getEmailFromUID(this.ess.filter, parentId, mailId, status,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName,
                    contentProviderInfo);
        } catch (EntityException e) {
            String errorMessage = e.getMessage();
            if (errorMessage != null){
                if (errorMessage.indexOf(Def.ERR_FILTERING_EMAIL) != -1){
                    // set as invalid the item in the cache, in the next sync will be ignored
                    insertInvalidItem(syncItemKey.getKeyAsString(), null, null, null,
                            null, null, "Y");
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
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     * @see com.funambol.framework.engine.source.SyncSource
     */
    private SyncItem getFolderFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("getSyncItemFromId start");
        }

        SyncItem syncItem = null;
        Folder folder;

        String key = syncItemKey.getKeyAsString();

        char status = getSyncItemStateFromId(syncItemKey);

        try {
            folder = this.pem.getFolderFromUID(key, this.ess.getSourceURI(),
                    this.ess.principalId);
        } catch (Exception e) {
            throw new SyncSourceException("Error in getting Folder ", e);
        }

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
     * @throws EntityException
     */
    private SyncItemKey[] getEmailFromTwin(String content,
            SyncItem syncItem)
            throws EntityException {

        String[] keys = Utility.getKeyParts(syncItem);
        String FID  = keys[0];
        String FMID = keys[1];

        if (log.isTraceEnabled()) {
            log.trace("getEmailFromTwin [FMID: " + FMID + " ; FID: " + FID + "]");
        }

        String GUID = null;
        Email email = null;

        // create the Email object
        try {
            email = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        if (FID.equals(Def.FOLDER_INBOX_ID)) {
            // search in Mail Server Inbox folder
            try {
                GUID = this.pem.getEmailFromClause(this.ess.MSWrapper.getSession(),
                        this.ess.serverItems,
                        FID,
                        email);
            } catch (EntityException e) {
                throw new EntityException("Error getting twin in INBOX Folder", e);
            }
        } else if (FID.equals(Def.FOLDER_OUTBOX_ID) ||
                FID.equals(Def.FOLDER_SENT_ID)) {
            // search in the DB
            try {
                GUID = this.pem.getEmailFromClauseInDB(this.ess.MSWrapper.getSession(),
                        FID,
                        email,
                        this.ess.getSourceURI(),
                        this.ess.principalId,
                        this.ess.userName);
            } catch (EntityException e) {
                throw new EntityException("Error getting twin in SENT/OUTBOX Folder", e);
            }
        }

        // converts Email in SyncItem
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
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws EntityException
     */
    private SyncItemKey[] getFolderFromTwin(String content,
            SyncItem syncItem)
            throws EntityException {


        SyncItemKey[] syncItemKeys;

        String key = (String) syncItem.getKey().getKeyValue();

        if (log.isTraceEnabled()) {
            log.trace("getSyncItemKeysFromTwin with UID: " + key);
        }

        Folder ctmpNew;
        try {
            ctmpNew = HelperForIOConversion.getFoundationFolderFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        // insert folder
        com.funambol.email.pdi.folder.Folder insertedFolder;

        String name = ctmpNew.getName().getPropertyValueAsString();
        String parentId = syncItem.getParentKey().getKeyAsString();

        if (parentId.equalsIgnoreCase("/")) {
            parentId = Def.FOLDER_ROOT_ID;
        }

        try {
            insertedFolder = this.pem.getFolderFromName(name, parentId,
                    this.ess.getSourceURI(), this.ess.principalId);
            if (insertedFolder != null) {
                syncItemKeys = new SyncItemKey [1];
                String GUID = insertedFolder.getUID().getPropertyValueAsString();
                syncItemKeys[0] = new SyncItemKey(GUID);
            } else {
                syncItemKeys = new SyncItemKey[0];
            }
        } catch (EntityException e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage, e);
            //if (log.isLoggable(Level.SEVERE)) {
            //    log.severe(errorMessage);
            //}
            throw new EntityException(errorMessage, e);
        }

        return syncItemKeys;
    }


    /**
     * @param syncItemKey
     * @throws EntityException
     */
    private void removeEmail(String parentId, String mailId, String GUID)
    throws EntityException {
        try {
            this.pem.removeEmail(this.ess.filter, parentId, mailId, GUID,
                    this.ess.serverItems, this.ess.userName,
                    this.ess.getSourceURI(), this.ess.principalId);
        } catch (EntityException e) {
            throw new EntityException("Error removing items", e);
        }

    }

    /**
     * @param syncItemKey
     * @throws EntityException
     */
    private void removeEmailSoft(String parentId, String mailId, String GUID)
    throws EntityException {

        try {
            this.pem.removeEmailSoft(parentId, GUID, this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error removing items", e);
        }

    }

    /**
     * @param GUID String
     * @throws EntityException
     */
    private void removeFolder(String GUID) throws EntityException {
    }

    /**
     *
     * check if the message is included in the filter clause
     *
     * @param content  String
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws EntityException
     */
    private boolean isEmailInFilter(String content,
            SyncItem syncItem,
            char status)
            throws EntityException {

        boolean isInFilter = false;

        // get SyncItem UID
        String key    = syncItem.getKey().getKeyAsString();

        String[] keys = Utility.getKeyParts(syncItem);
        String FID    = keys[0];
        String FMID   = keys[1];

        Email email = null;
        try {
            email = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting Content ", e);
        }

        try {
            isInFilter = this.pem.isEmailInFilter(email, this.ess.filter,
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
    private boolean isEmailInFilter(String key, char status)
    throws EntityException {

        boolean isInFilter = false;

        String FID  = Utility.getKeyPart(key,1);
        String FMID = Utility.getKeyPart(key,2);

        try {
            isInFilter = this.pem.isEmailInFilter(null,
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
     * the pop3-based syncsource can't update the item
     * in the mail server. In this case this method must
     * return the item for internal ds-server purpose
     *
     * @param parentId  String
     * @param mailId  String
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

        Email emailOut;
        Email emailNew;

        try {
            emailNew = HelperForIOConversion.getFoundationMailFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        try {
            emailOut = this.pem.updateEmail(parentId, mailId, emailNew,
                    this.ess.getSourceURI(),
                    this.ess.principalId);
        } catch (Exception e) {
            throw new EntityException("Error setting the item: ", e);
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

        /*
        Folder folderNew;
        try {
            folderNew = HelperForIOConversion.getFoundationFolderFromXML(content);
        } catch (EntityException e) {
            throw new EntityException("Error converting foundation: " +
                    e.getMessage(), e);
        }

        Folder folderOut;
        try {
            folderOut = this.pem.updateFolder(parentId, folderId, folderNew);
        } catch (Exception e) {
            String errorMessage = "Error setting the item: " + syncItem;
            log.error(errorMessage, e);
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
         */
        return syncItem;

    }

    /**
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    private SyncItem addFolder(SyncItem syncItem)
    throws EntityException {
        return syncItem;
    }

}
