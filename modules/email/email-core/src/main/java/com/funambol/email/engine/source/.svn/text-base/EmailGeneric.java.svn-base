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


import com.funambol.email.exception.EmailAccessException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import java.sql.Timestamp;
import com.funambol.framework.engine.SyncItem;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.email.util.Def;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.exception.EntityException;
import com.funambol.email.items.manager.ContentProviderInfo;
import com.funambol.email.items.manager.EntityManager;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.pdi.folder.Folder;
import com.funambol.email.util.Utility;
import com.funambol.framework.core.AlertCode;
import com.funambol.framework.engine.SyncItemImpl;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.server.config.Configuration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.TimeZone;
import org.apache.commons.lang.StringUtils;

/**
 * This class implements common methods of EmailPOPSyncSource
 *
 * version $Id: EmailGeneric.java,v 1.3 2008-06-03 09:12:37 testa Exp $
 */
public abstract class EmailGeneric implements IEmailGeneric {

    // --------------------------------------------------------------- Constants

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    // -------------------------------------------------------------- Properties

    /**  */
    protected EntityManager   em   = null;

    /**  */
    protected EmailSyncSource ess   = null;

    // ------------------------------------------------------------- Constructor

    /**
     *
     */
    public EmailGeneric() {
    }

    /**
     *
     */
    public EmailGeneric(EmailSyncSourceWrapper _essc) throws SyncSourceException {

        this.ess = _essc.getEmailSyncSource();

        try {
            if (this.em == null) {
                this.em = new EntityManager(this.ess);
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
     *
     */
    public abstract boolean isSyncItemInFilterClause(SyncItem syncItem)
    throws SyncSourceException;

    /**
     *
     */
    public abstract boolean isSyncItemInFilterClause(SyncItemKey syncItemKey)
    throws SyncSourceException;


    /**
     * @param operation  String
     * @param statusCode int
     * @param keys       SyncItemKey[]
     */
    public void setOperationStatus(String operation,
            int statusCode,
            SyncItemKey[] keys) {

        if (log.isTraceEnabled()) {
            StringBuffer message = new StringBuffer("Received status code '");
            message.append(statusCode).append("' for a '").append(operation).append("'").
                    append(" for this items: ");

            for (int i = 0; i < keys.length; i++) {
                message.append("\n- ").append(keys[i].getKeyAsString());
            }
            log.trace(message.toString());
        }

        // ready for the client that sends the 500 for
        // the optimized email with only flags
        /*
        if (statusCode == StatusCode.COMMAND_FAILED){
            String GUID = null;
            for (int i = 0; i < keys.length; i++) {
                GUID = keys[i].getKeyAsString();
                try {
                    this.em.insertInvalidItem(GUID,
                                              this.ess.getSourceURI(),
                                              this.ess.principalId);
                } catch (Exception e){
                    log.trace("Error adding the invalid status for the item " + GUID);
                }
            }
        }
         */

    }

    /**
     *
     */
    public abstract void removeSyncItem(SyncItemKey syncItemKey,
            Timestamp s,
            boolean engineSoftDelete)
            throws SyncSourceException ;

    /**
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     * @see com.funambol.framework.engine.source.SyncSource
     */
    public abstract SyncItem getSyncItemFromId(SyncItemKey syncItemKey)
    throws SyncSourceException ;


    /**
     *
     */
    public abstract SyncItemKey[] getAllSyncItemKeys()
    throws SyncSourceException ;


    /**
     *
     */
    public abstract Map getAllSyncItemInfo()
    throws SyncSourceException ;


    /**
     *
     */
    public abstract void clearServerInfo() throws SyncSourceException ;

    /**
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public abstract SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem)
    throws SyncSourceException ;

    /**
     *
     */
    public abstract SyncItem addSyncItem(SyncItem syncItem)
    throws SyncSourceException ;

    /**
     *
     */
    public abstract SyncItem updateSyncItem(SyncItem syncItem)
    throws SyncSourceException ;


    /**
     *
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return (SyncItemKey[])this.ess.updatedItemKeys.toArray(new SyncItemKey[0]) ;
    }

    /**
     *
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return (SyncItemKey[])this.ess.newItemKeys.toArray(new SyncItemKey[0]);
    }


    /**
     *
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return (SyncItemKey[])this.ess.deletedItemKeys.toArray(new SyncItemKey[0]) ;
    }


    /**
     *
     *
     * @param syncItemKey SyncItemKey
     * @return char
     * @throws SyncSourceException
     */
    public char getSyncItemStateFromId(SyncItemKey syncItemKey)
    throws SyncSourceException {
        return getSyncItemStateFromId(syncItemKey.getKeyAsString());
    }



    /**
     * check if in the Drafts folder therse is an item
     * without message-id
     *
     * @throws SyncSourceException
     */
    public void checkMessageIDforDrafts(String sURI, long pID, String username)
    throws EntityException {
        try {
            this.em.checkMessageIDforDrafts(sURI, pID, username);
        } catch (EntityException e) {
            throw new EntityException("Error inserting default folders", e);
        }

    }


    /**
     * insert the default folder if needed
     *
     * @throws EntityException
     */
    public void insertDefaultFolder(DefaultFolder df,
            EmailFilter filter,
            String protocol,
            String sURI,
            long pID,
            String username)
            throws EntityException {
        try {
            this.em.insertDefaultFolder(df, filter, protocol, sURI, pID, username);
        } catch (EntityException e) {
            throw new EntityException("Error inserting default folders", e);
        }
    }
    
    /**
     * Verify if the SyncItem is an email or a folder
     * @param syncItem The SyncItem to verify
     * @return True if the SyncItem is an email, false otherwise
     */
    public boolean isEmail(SyncItem syncItem) {
        try {

            String content = Utility.getContentFromSyncItem(syncItem);
            boolean isEmail = isEmailFromContent(content);
            return isEmail;
        } catch (EntityException ex) {
            log.error("Unamble to get the content from Sync Item while " +
                    "verifying if it is an email or not", ex);
            return false;
        }
    }


    //---------------------------------------------------------Protected Methods

    /**
     * @param folder Folder
     * @param status char
     * @return SyncItem
     * @throws EntityException
     */
    protected SyncItem createSyncItem(EmailSyncSource ess,
            Folder folder,
            char status,
            TimeZone deviceTimeZone,
            String deviceCharset)
            throws EntityException {

        SyncItem syncItem;
        String stream;

        stream = HelperForIOConversion.getXMLFromFoundationFolder(folder,
                deviceTimeZone,
                deviceCharset);

        String uid = (String) folder.getUID().getPropertyValue();
        String parentId = (String) folder.getParentId().getPropertyValue();

        try {
            syncItem = new SyncItemImpl(ess, uid, parentId, status);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        syncItem.setType(Def.TYPE_FOLDER);

        syncItem.setContent(stream.getBytes());

        return syncItem;
    }


    /**
     * @param email  Email
     * @param status char
     * @return SyncItem
     * @throws EntityException
     */
    protected SyncItem createSyncItem(EmailSyncSource ess,
            Email email,
            char status,
            TimeZone deviceTimeZone,
            String deviceCharset)
            throws EntityException {

        SyncItem syncItem;
        String stream;

        stream = HelperForIOConversion.getXMLFromFoundationMail(email,
                deviceTimeZone,
                deviceCharset);

        String uid = (String) email.getUID().getPropertyValue();
        String parentId = (String) email.getParentId().getPropertyValue();

        try {
            syncItem = new SyncItemImpl(ess, uid, parentId, status);
        } catch (Exception e) {
            throw new EntityException(e);
        }

        syncItem.setType(Def.TYPE_EMAIL);

        syncItem.setContent(stream.getBytes());

        return syncItem;
    }


    /**
     * @param key GUID  String
     * @return true if the item is valid boolean
     */
    protected boolean isValidItem(String key){

        boolean isValid      = true;
        SyncItemInfo item = null;

        Map items = this.ess.localItems;

        item = (SyncItemInfo)items.get(key);

        if (item != null){
            String valid = item.getInvalid();
            if (valid != null){
                if (valid.equals("Y")){
                    isValid = false;
                }
            }
        }

        return isValid;
    }


    /**
     * Returns true if the syncitem is a Mail
     * false if the sycItem is a Folder
     * <p/>
     * the key is composed by: parentId/objId (mailid or folderid)
     * The key is created by the connector and it always
     * creates the syncItemKey using parentId and mailId
     *
     * @param parentId String
     * @param objectId String
     * @return boolean
     * @throws SyncSourceException
     */
    protected boolean isEmailFromKey(String parentId,
            String objectId,
            String sourceURI,
            long principalId,
            String username)
            throws EntityException {

        boolean isEmail;
        try {
            isEmail = this.em.isEmail(parentId, objectId,
                    sourceURI, principalId, username);
        } catch (EntityException e) {
            throw new EntityException("Error checking if items is Email or Folder", e);
        }

        return isEmail;
    }



    /**
     * Returns true if the syncitem is a Mail,
     * false if the sycItem is a Folder
     *
     * @param content String
     * @return boolean
     */
    protected boolean isEmailFromContent(String content) {

        boolean isEmail = true;

        if (content != null) {
            int emailIndex = content.indexOf("<Email>");
            int folderIndex = content.indexOf("<Folder>");
            if (log.isTraceEnabled()) {
                log.trace("isEmail - emailIndex [" + emailIndex +
                        "] - folderIndex [ " + folderIndex + "]");
            }
            if ((folderIndex >= 0) && (emailIndex < 0)) {
                isEmail = false;
            }
        }

        return isEmail;
    }

    /**
     * insert the invalid item in the cache.
     * in the next syncs this item will not be handled
     *
     * @throws SyncSourceException
     */
    protected void insertInvalidItem(String GUID,
            String messageId,
            java.util.Date headerDate,
            java.util.Date received,
            String subject,
            String sender,
            String isEmail)
            throws EntityException {

        try {
            this.em.insertInvalidItem(GUID, messageId, headerDate, received,
                    subject, sender, isEmail,
                    this.ess.getSourceURI(),
                    this.ess.principalId,
                    this.ess.userName);
        } catch (EntityException e) {
            throw new EntityException("Error inserting custom folders", e);
        }

    }


    //---------------------------------------------------------- Private Methods


    /**
     *
     *
     * @param syncItemKey SyncItemKey
     * @return char
     * @throws SyncSourceException
     */
    private char getSyncItemStateFromId(String key) throws SyncSourceException {

        char status       = SyncItemState.UNKNOWN;
        boolean isNew     = false;
        boolean isUpdated = false;
        boolean isDeleted = false;

        //
        if (this.ess.syncMode == AlertCode.REFRESH_FROM_SERVER) {
            return SyncItemState.NEW;
        }

        // search in new items list
        SyncItemKey tmp = null;
        for (int i=0; i<this.ess.newItemKeys.size(); i++){

            tmp = (SyncItemKey)this.ess.newItemKeys.get(i);

            if (tmp.getKeyAsString().equals(key)) {
                isNew  = true;
                status = SyncItemState.NEW;
                break;
            }

        }

        if (!isNew){

            // search in updated items list
            for (int i=0; i<this.ess.updatedItemKeys.size(); i++){

                tmp = (SyncItemKey)this.ess.updatedItemKeys.get(i);

                if (tmp.getKeyAsString().equals(key)) {
                    isUpdated = true;
                    status    = SyncItemState.UPDATED;
                    break;
                }
            }


            if (!isUpdated){
                // search in deleted items list
                for (int i=0; i<this.ess.deletedItemKeys.size(); i++){

                    tmp = (SyncItemKey)this.ess.deletedItemKeys.get(i);

                    if (tmp.getKeyAsString().equals(key)) {
                        isDeleted = true;
                        status    = SyncItemState.DELETED;
                        break;
                    }
                }

                if (!isDeleted){
                    status    = SyncItemState.SYNCHRONIZED;
                }

            }

        }

        return status;

    }

    
    /**
     * Retrieve the Content Provider URL from the Email SynSource configuration
     * or from the SereverURI configuration
     * @return The Content Provider URL
     */
    protected String getContentProviderURL() {
        String attachProviderUrl = ess.getContentProviderURL();
        if (StringUtils.isBlank(attachProviderUrl)) {
            Configuration config = Configuration.getConfiguration();
            String serverURI = config.getServerConfig().getEngineConfiguration().getServerURI();
            if (StringUtils.isBlank(serverURI)) {
                attachProviderUrl = ContentProviderInfo.CONTENT_PROVIDER_PATH;
            } else {
                try {
                    URL baseUrl = new URL(serverURI);
                    attachProviderUrl = new URL(baseUrl.getProtocol(),
                            baseUrl.getHost(),
                            baseUrl.getPort(),
                            ContentProviderInfo.CONTENT_PROVIDER_PATH).toString();
                } catch (MalformedURLException ex) {
                    log.error("Invalid Server URI", ex);
                    attachProviderUrl = ContentProviderInfo.CONTENT_PROVIDER_PATH;
                }
            }
        }
        return attachProviderUrl;
    }
    
}
