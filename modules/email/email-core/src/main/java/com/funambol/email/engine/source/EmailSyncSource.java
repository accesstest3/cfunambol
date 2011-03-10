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

import java.io.Serializable;

import java.sql.Timestamp;

import java.util.TimeZone;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.activation.MailcapCommandMap;
import javax.activation.MimetypesFileTypeMap;

import javax.naming.NamingException;

import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.engine.source.AbstractSyncSource;
import com.funambol.framework.engine.source.ContentType;
import com.funambol.framework.engine.source.FilterableSyncSource;
import com.funambol.framework.engine.source.SyncContext;
import com.funambol.framework.engine.source.SyncSourceException;
import com.funambol.framework.engine.source.SyncSourceInfo;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.engine.SyncItem;

import com.funambol.email.console.manager.ConsoleManager;
import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EmailConfigException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.util.Def;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.model.DefaultFolder;
import com.funambol.email.model.EmailFilter;
import com.funambol.email.model.FlagProperties;
import com.funambol.email.model.MailServer;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.pdi.mail.Email;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.transport.MailServerWrapperFactory;
import com.funambol.email.transport.PopMailServerWrapper;
import com.funambol.email.util.Utility;

import com.funambol.email.util.MailServerChecker;
import com.funambol.framework.core.AlertCode;
import com.funambol.framework.core.Status;
import com.funambol.framework.core.StatusCode;
import com.funambol.framework.engine.SyncItemState;
import com.funambol.framework.engine.source.MergeableSyncSource;
import com.funambol.framework.engine.source.RefusedItemException;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import com.funambol.server.config.Configuration;

/**
 * <p>This class implements common methods of MultiEmailSyncSource<p/>
 *
 * version $Id: EmailSyncSource.java,v 1.8 2008-06-03 09:16:05 testa Exp $
 */
public class EmailSyncSource
        extends AbstractSyncSource
        implements FilterableSyncSource, MergeableSyncSource, Serializable, LazyInitBean {

    //
    {
        setInfo(
            new SyncSourceInfo(
                new ContentType[]{
                    new ContentType("application/vnd.omads-email+xml", "1.2"),
                    new ContentType("application/vnd.omads-folder+xml", "1.2")
                }, 0)
        );
    }

    // information for the mimetype text/calendar
    static {
        MimetypesFileTypeMap mimetypes =  (MimetypesFileTypeMap)MimetypesFileTypeMap.getDefaultFileTypeMap();
        mimetypes.addMimeTypes("text/calendar ics ICS");
        MailcapCommandMap mailcap = (MailcapCommandMap)MailcapCommandMap.getDefaultCommandMap();
        mailcap.addMailcap("text/calendar;; x-java-content-handler=com.sun.mail.handlers.text_plain");
    }

    // --------------------------------------------------------------- Constants

    protected static final String CORE_DB_JNDI_NAME = "jdbc/fnblcore";

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);


    // -------------------------------------------------------------- Properties

    protected String jndiDataSourceName = null;

    /**
     * @return jndiDataSourceName String
     */
    public String getJndiDataSourceName(){
        return this.jndiDataSourceName;
    }


    protected String contentProviderURL = "";

    /**
     * @return contentProviderURL String
     */
    public String getContentProviderURL(){
        return this.contentProviderURL;
    }

    /**
     * @param _contentProviderURL String
     */
    public void setContentProviderURL(String _contentProviderURL){
        this.contentProviderURL = _contentProviderURL;
    }


    protected String funambolSignature = Def.TRAILER;

    /**
     * @return funambolSignature String
     */
    public String getFunambolSignature(){
        return this.funambolSignature;
    }

    /**
     * @param _funambolSignature String
     */
    public void setFunambolSignature(String _funambolSignature){
        this.funambolSignature = _funambolSignature;
    }

    protected boolean saveOnlyHeader = false;

    protected String timeoutStore;

    protected String timeoutConnection;

    protected boolean checkCertificates;

    protected String sslSocketFactory;

    protected long principalId ;

    protected int syncMode;

    protected String userName;

    protected String firstname;

    protected String lastname;

    /**
     * Property for sync session
     */
    protected TimeZone deviceTimeZone = null;

    /**
     * Property for sync session
     */
    protected String deviceCharset = null;

    /**
     * wrapper for the Mail Server
     */
    protected IMailServerWrapper MSWrapper = null;

    /**
     *
     * @return IMailServerWrapper
     */
    public IMailServerWrapper getMSWrapper(){
        return this.MSWrapper;
    }

    /**
     *
     */
    protected EmailFilter filter = new EmailFilter();

    /**
     * wrapper for the Email Helper
     */
    protected IEmailGeneric emailhelper = null;

    /**
     * wrapper for the Mail Server Account Wrapper
     */
    protected MailServerAccount mailServerAccount = null;

    /**
     *
     * @return MailServerAccount
     */
    public MailServerAccount getMailServerAccount(){
        return this.mailServerAccount;
    }

    protected CacheDAO cacheDao = null;

    protected static DBIDGenerator idSentSpace = null;

    protected static DBIDGenerator idFolderSpace = null;

    /**
     * List containing the items key for items in the serverItems map but not in
     * the localItems map accordingly with the filters
     * It is filled by the setItemKeys() method
     */
    protected List newItemKeys     = null;

    protected List deletedItemKeys = null;

    protected List updatedItemKeys = null;

    /**
     * keep the items id and the result of send operation
     */
    protected List sentItems = new ArrayList();

    /**
     * list of local cached Items
     */
    protected Map localItems  = null;

    /**
     * Map of server's SyncItemInfo (with SyncItemKey(GUID)) indexed by String GUID
     */
    protected Map serverItems = null;

    /**
     * save the invalid items during the cache updating procedure
     */
    protected SyncItemInfo[] invalidItems = null;

    /**
     * Should allMailbox sync?
     */
    protected boolean allMailboxActivation = false;

    /**
     * Should allMailbox sync?
     */
    protected boolean isSlowOrRefresh = false;

    protected DefaultFolder defaultFolder = new DefaultFolder(Def.FOLDER_INBOX_ENG,
            Def.FOLDER_OUTBOX_ENG,
            Def.FOLDER_SENT_ENG,
            Def.FOLDER_DRAFTS_ENG,
            Def.FOLDER_TRASH_ENG);


    // ------------------------------------------------------------- Constructor

    public EmailSyncSource(){
    }

    // ---------------------------------------------------------- Public Methods


    /**
     *
     * @throws com.funambol.framework.tools.beans.BeanInitializationException
     */
    public void init() throws BeanInitializationException {

        try {
            this.jndiDataSourceName =
                    EmailConnectorConfig.getConfigInstance().getDataSource();
            this.saveOnlyHeader     =
                    EmailConnectorConfig.getConfigInstance().getSaveOnlyHeader();
            this.timeoutStore =
                    EmailConnectorConfig.getConfigInstance().getTimeoutStore();
            this.timeoutConnection =
                    EmailConnectorConfig.getConfigInstance().getTimeoutConnection();
            this.checkCertificates =
                    EmailConnectorConfig.getConfigInstance().getCheckCertificates();
        } catch (EmailConfigException e) {
            log.error("Error Getting Connector Parameters ", e);
        }

        try {
            cacheDao = new CacheDAO();
            cacheDao.init();
        } catch (Exception e) {
            throw new BeanInitializationException("Error initializing ItemStateDAO", e);
        }

        try {
            idSentSpace   = DBIDGeneratorFactory.getDBIDGenerator(
                    Def.ID_COUNTER_SENT,
                    DataSourceTools.lookupDataSource(CORE_DB_JNDI_NAME)
                    );
            idFolderSpace = DBIDGeneratorFactory.getDBIDGenerator(
                    Def.ID_COUNTER_FOLDER,
                    DataSourceTools.lookupDataSource(CORE_DB_JNDI_NAME)
                    );
        } catch (NamingException e) {
            throw new BeanInitializationException("Error initializing ID Generator", e);
        }
    }


    /**
     * Begin Sync Method
     * before starting sync the method sets the filter
     * <p>
     * main folder filter
     * <ul>
     * <li> T D S O I
     * <li> 0 0 0 0 1 -->  1   inbox
     * <li> 0 0 0 1 1 -->  3   inbox + outbox
     * <li> 0 0 1 1 1 -->  7   inbox + outbox + sent
     * <li> 0 1 1 1 1 -->  15  inbox + outbox + sent + draft
     * <li> 1 1 1 1 1 -->  31  inbox + outbox + sent + draft + trash
     * </ul>
     * <p>
     * time filter (timeFilter)
     * <ul>
     * <li> null              --> all
     * <li> "i.e. 20-10-2005" --> filter
     * </ul>
     * <p>
     * size filter (sizeFilter / numbytes)
     * <ul>
     * <li> 000001 --> 1  FILTER_SIZE_H
     * <li> 000011 --> 3  FILTER_SIZE_H_PERC
     * <li> 000101 --> 5  FILTER_SIZE_H_B
     * <li> 001111 --> 15 FILTER_SIZE_H_B_PERC
     * <li> 010101 --> 21 FILTER_SIZE_H_B_A
     * <li> 111111 --> 63 FILTER_SIZE_H_B_A_PERC
     * </ul>
     *
     * @param context SyncContext
     * @throws SyncSourceException
     */
    public void beginSync(SyncContext context) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - Start beginSync [" + this.getSourceURI() + "]");
        }

        // set common properties
        Sync4jPrincipal principal = context.getPrincipal();
        this.syncMode             = context.getSyncMode();
        this.principalId          = principal.getId();
        this.userName             = principal.getUsername();
        Sync4jUser user           = principal.getUser();
        this.firstname            = user.getFirstname();
        this.lastname             = user.getLastname();

        try {
            // set device info
            setDeviceInfo(context);

            // set session mode
            setSyncType();

            // set the MailServerAccount Object
            setMailServerAccount();

            // set filter
            this.filter = HelperForFilter.setFilter(context,
                    this.mailServerAccount.getMaxEmailNumber(),
                    this.mailServerAccount.getMaxImapEmail(),
                    this.mailServerAccount.getMailServer().getInboxActivation(),
                    this.mailServerAccount.getMailServer().getOutboxActivation(),
                    this.mailServerAccount.getMailServer().getSentActivation(),
                    this.mailServerAccount.getMailServer().getDraftsActivation(),
                    this.mailServerAccount.getMailServer().getTrashActivation());

            // main folders
            setDefaultFolder(this.mailServerAccount.getMailServer());


        } catch (EntityException e) {
            throw new SyncSourceException("Error setting preliminary info: " +
                    e.getMessage(), e);
        }


        String protocol = this.mailServerAccount.getMailServer().getProtocol();

        boolean isDraftsActive = this.mailServerAccount.getMailServer().getDraftsActivation();

        if (log.isTraceEnabled()) {
            log.trace(this.printIntro());
        }

        try {

            //
            this.MSWrapper = MailServerWrapperFactory.getMailServerWrapper(protocol);

            // imap or pop3 helper
            EmailSyncSourceWrapper essc = new EmailSyncSourceWrapper();
            essc.setEmailSyncSource(this);
            this.emailhelper = EmailFactory.getEmailSyncSource(protocol, essc);

            // open Mail Server connection
            if (checkCertificates){
                this.sslSocketFactory = Def.SSL_SOCKET_FACTORY_STANDARD;
            } else {
                this.sslSocketFactory = Def.SSL_SOCKET_FACTORY_FUNAMBOL;
            }

            String msLogin = this.mailServerAccount.getMsLogin();
            String msPassword = this.mailServerAccount.getMsPassword();

            boolean outAuth = this.mailServerAccount.getMailServer().getOutAuth();
            String outLogin = null;
            String outPassword = null;
            if (outAuth) {
                outLogin = this.mailServerAccount.getOutLogin();
                outPassword = this.mailServerAccount.getOutPassword();

                if (outLogin == null) {
                    outLogin = msLogin;
                }
                if (outPassword == null) {
                    outPassword = msPassword;
                }
            }

            this.MSWrapper.openConnection(this.mailServerAccount.getMailServer().getMailServerType(),
                    this.mailServerAccount.getMailServer().getOutServer(),
                    "" + this.mailServerAccount.getMailServer().getOutPort(),
                    outAuth,
                    this.mailServerAccount.getMailServer().getInServer(),
                    "" + this.mailServerAccount.getMailServer().getInPort(),
                    getDefaultFolder(),
                    true,
                    msLogin,
                    msPassword,
                    outLogin,
                    outPassword,
                    this.mailServerAccount.getMailServer().getIsSSLIn(),
                    this.mailServerAccount.getMailServer().getIsSSLOut(),
                    this.timeoutStore, this.timeoutConnection,
                    this.sslSocketFactory);

        } catch (EmailAccessException e) {
            try {
                this.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error Connecting Mail Server:" +
                    e.getMessage(), e);
        }

        //
        // If the mail server is accessed via pop then checks if it is accessible.
        //
        if (this.MSWrapper instanceof PopMailServerWrapper) {
            try {
                MailServerChecker mailServerChecker = new MailServerChecker();
                mailServerChecker.checkAccountAccessiblility(this.mailServerAccount,
                        ((PopMailServerWrapper)MSWrapper).folderInboxOpened);
            } catch (EmailAccessException e) {
                try {
                    this.MSWrapper.releaseConnection();
                } catch (EmailAccessException e1) {
                    if (log.isTraceEnabled()){
                        this.log.trace("Error closing store connection: " +
                            e1.getMessage());
                    }
                }
                throw new SyncSourceException(
                        e.getMessage(), StatusCode.SERVICE_UNAVAILABLE);
            }
        }

        // standard operation
        try {

            // insert the 5 main folder if needed
            insertDefaultFolder();

            // only for IMAP-based syncsource
            // check item without Message-ID in the draft folder
            // the draft created with a client i.e. outlook has no message-id
            if (protocol.equals(Def.PROTOCOL_IMAP) && isDraftsActive){
                checkMessageIDforDrafts();
            }

        } catch (EntityException e) {
            try {
                this.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error inserting Default Folder: " +
                    e.getMessage(), e);
        }


        // call cache
        try {
            cacheAtBegin();
        } catch (EntityException e) {
            try {
                this.MSWrapper.releaseConnection();
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error Handling the Cache Info: " +
                    e.getMessage(), e);
        }

        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - End beginSync [" + this.getSourceURI() + "]");
        }

    }

    /**
     * End Sync Method
     *
     * @throws SyncSourceException
     */
    public void endSync() throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - start EndSync [" + this.getSourceURI() + "]");
        }

        // reset the cache
        try {
            cacheAtEnd();
        } catch (EntityException e) {
            try {
                if (this.MSWrapper != null) {
                    this.MSWrapper.releaseConnection();
                }
            } catch (EmailAccessException e1){
                throw new SyncSourceException("Error closing store connection: " +
                        e1.getMessage(), e1);
            }
            throw new SyncSourceException("Error Handling the Cache Info:" +
                    e.getMessage(), e);
        }

        // close Mail Server connection
        try {
            if (this.MSWrapper != null) {
                this.MSWrapper.releaseConnection();
            }
        } catch (EmailAccessException e) {
            throw new SyncSourceException("Error Closing Email:" +
                    e.getMessage(), e);
        }

        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - end EndSync [" + this.getSourceURI() + "]");
        }

    }


    /**
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getAllSyncItemKeys() throws SyncSourceException {
        return emailhelper.getAllSyncItemKeys();
    }


    /**
     * @param syncItemKey SyncItemKey
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem getSyncItemFromId(SyncItemKey syncItemKey) throws SyncSourceException {
        SyncItem si = emailhelper.getSyncItemFromId(syncItemKey);
        return si;
    }


    /**
     * @param syncItem SyncItem
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getSyncItemKeysFromTwin(SyncItem syncItem) throws SyncSourceException {
        return emailhelper.getSyncItemKeysFromTwin(syncItem);
    }


    /**
     * @param syncItem SyncItem
     * @return boolean
     * @throws SyncSourceException
     */
    public boolean isSyncItemInFilterClause(SyncItem syncItem) throws SyncSourceException {
        return emailhelper.isSyncItemInFilterClause(syncItem);
    }


    /**
     * @param syncItemKey SyncItemKey
     * @return boolean
     * @throws SyncSourceException
     */
    public boolean isSyncItemInFilterClause(SyncItemKey syncItemKey) throws SyncSourceException {
        return emailhelper.isSyncItemInFilterClause(syncItemKey);
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
        emailhelper.removeSyncItem(syncItemKey, s, engineSoftDelete);
    }

    /**
     * @param operation  String
     * @param statusCode int
     * @param keys       SyncItemKey[]
     */
    public void setOperationStatus(String operation,
                                   int statusCode,
                                   SyncItemKey[] keys) {
        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - start setOperationStatus [" + operation + "," +
                    statusCode + "]");
        }
        emailhelper.setOperationStatus(operation, statusCode, keys);
        if (log.isTraceEnabled()) {
            log.trace("EmailSyncSource - end setOperationStatus");
        }
    }



    /**
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException
     */
    public SyncItem updateSyncItem(SyncItem syncItem) throws SyncSourceException {
        return emailhelper.updateSyncItem(syncItem);
    }

    /**
     *
     * @param syncItem SyncItem
     * @return SyncItem
     * @throws SyncSourceException (RefusedItemException) if the email to be
     * added has to be added in the Inbox folder and it is a slow sync
     */
    public SyncItem addSyncItem(SyncItem syncItem) throws SyncSourceException {

        String[] keys = Utility.getKeyParts(syncItem);
        String FID = keys[0];
        String FMID = keys[1];

        // If an add is required during a slow sync it has to be refused, but
        // this is not the only case: for example if the item is updated client
        // side but deleted server side, in this case also the addSyncItem thows
        // a RefusedItemException.
        if (isSlowOrRefresh && (FID.equalsIgnoreCase(Def.FOLDER_INBOX_ID)||FID.equalsIgnoreCase(Def.FOLDER_INBOX_ENG))) {
            if (log.isTraceEnabled()) {
                log.trace("Email add refused: the email has to be deleted client side");
            }
            throw new RefusedItemException("delete Email client side [FID: " +
                    FID + " luid: " + FMID + "]");
        }

        return emailhelper.addSyncItem(syncItem);
    }



    /**
     *
     *
     * @param since Timestamp
     * @param to Timestamp
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getDeletedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return emailhelper.getDeletedSyncItemKeys(since, to);
    }


    /**
     *
     *
     * @param since Timestamp
     * @param to Timestamp
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getNewSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return emailhelper.getNewSyncItemKeys(since, to);
    }

    /**
     *
     *
     * @param since Timestamp
     * @param to Timestamp
     * @return SyncItemKey[]
     * @throws SyncSourceException
     */
    public SyncItemKey[] getUpdatedSyncItemKeys(Timestamp since, Timestamp to)
    throws SyncSourceException {
        return emailhelper.getUpdatedSyncItemKeys(since, to);
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
        return emailhelper.getSyncItemStateFromId(syncItemKey);
    }



    /**
     * Actually this method does't merges the server item and the client item.
     * If the CRCs (the flags) are different than the server item is sent to the
     * client, otherwise nothing is done.
     * 
     * @param syncItemKey the item's key on the server
     * @param syncItem the item on the client
     * @throws SyncSourceException
     * @return true is the content of the client item is changed and has to be 
     * sent back to the client
     */
    public boolean mergeSyncItems(SyncItemKey syncItemKey, SyncItem syncItem)
            throws SyncSourceException {

        SyncItemInfo serverSyncItemInfo = (SyncItemInfo) serverItems.get(syncItemKey.getKeyAsString());
        if (serverSyncItemInfo == null) {
            return false;
        }

        if (!emailhelper.isEmail(syncItem)) {
            return false;
        }
        
        try {
            long serverItemCrc = serverSyncItemInfo.getLastCrc();
            long clientItemCrc = retrieveSyncItemCrc(syncItem);
            if (serverItemCrc == clientItemCrc) {
                return false;
            } else {

                if (log.isTraceEnabled()) {
                    log.trace("Different flags for Emails with GUID " + syncItemKey.getKeyAsString());
                }

                SyncItem serverItem = getSyncItemFromId(syncItemKey);

                syncItem.setContent(serverItem.getContent());
                syncItem.setFormat(serverItem.getFormat());
                syncItem.setType(serverItem.getType());
                syncItem.setTimestamp(serverItem.getTimestamp());
                syncItem.setState(SyncItemState.UPDATED);

                if (!updatedItemKeys.contains(syncItemKey) ) {
                    updatedItemKeys.add(syncItemKey);
                    newItemKeys.remove(syncItemKey);
                }

                return true;
            }
            
        } catch (EntityException ex) {
            log.error("Error retrieving email while merging it", ex);
            return false;
        }
    }

    //---------------------------------------------------------- Private Methods

    /**
     * Calculate the Item's crc
     * @param syncItem the SyncItem for which the crc is calculated
     * @return the items's crc
     * @throws com.funambol.email.exception.EntityException
     */
    private long retrieveSyncItemCrc(SyncItem syncItem) throws EntityException {
        String content = Utility.getContentFromSyncItem(syncItem);
        Email email = HelperForIOConversion.getFoundationMailFromXML(content);
        FlagProperties fp = Utility.getFlags(email);
        String flagList = fp.toString();
        String protocol = mailServerAccount.getMailServer().getProtocol();
        long clientItemCrc = Utility.createCRC(flagList, protocol);
        return clientItemCrc;
    }

    /**
     *
     */
    private void cacheAtBegin() throws EntityException {

        try {

            // refresh the inbox; remove the D status for Slow or refresh sync session
            if (this.syncMode == AlertCode.SLOW ||
                    this.syncMode == AlertCode.REFRESH_FROM_SERVER){
                this.cacheDao.updateDeletedStatus(this.userName,
                        this.mailServerAccount.getMailServer().getProtocol());
            }

            this.serverItems = this.emailhelper.getAllSyncItemInfo();

            if (this.syncMode == AlertCode.SLOW){

                this.cacheDao.deleteLocalItems(this.getSourceURI(),this.principalId,this.userName);
                this.cacheDao.deleteSentPop(this.getSourceURI(),this.principalId,this.userName);
                this.localItems = new LinkedHashMap();
                this.newItemKeys     = HelperForGetter.setNewItemKeys(
                        this.localItems, this.serverItems);
                this.updatedItemKeys = new ArrayList();
                this.deletedItemKeys = new ArrayList();

            } else if (this.syncMode == AlertCode.REFRESH_FROM_SERVER){

                this.cacheDao.deleteLocalItems(this.getSourceURI(),this.principalId,this.userName);
                this.cacheDao.deleteSentPop(this.getSourceURI(),this.principalId,this.userName);
                this.localItems = new LinkedHashMap();
                setItemKeys();

            } else {

                this.localItems = this.cacheDao.getLocalItems(this.getSourceURI(), this.principalId, this.userName);
                setItemKeys();

            }

            if (log.isTraceEnabled()) {
                String tmp = "";
                if (this.newItemKeys != null){
                    log.trace("NEW num:" + this.newItemKeys.size());
                    for (int i=0; i<this.newItemKeys.size(); i++){
                        tmp = tmp + " " + (this.newItemKeys.get(i));
                    }
                    log.trace("NEW List : [" + tmp + "]");
                } else {
                    log.trace("NO NEW ITEMS");
                }

                tmp = "";
                if (this.updatedItemKeys != null){
                    log.trace("UPDATED num:" + this.updatedItemKeys.size());
                    for (int i=0; i<this.updatedItemKeys.size(); i++){
                        tmp = tmp + " " + (this.updatedItemKeys.get(i));
                    }
                    log.trace("UPDATED List : [" + tmp + "]");
                } else {
                    log.trace("NO UPDATED ITEMS");
                }

                tmp = "";
                if (this.deletedItemKeys != null){
                    log.trace("DELETED num:" + this.deletedItemKeys.size());
                    for (int i=0; i<this.deletedItemKeys.size(); i++){
                        tmp = tmp + " " + (this.deletedItemKeys.get(i));
                    }
                    log.trace("DELETED List : [" + tmp + "]");
                } else {
                    log.trace("NO DELETED ITEMS");
                }
            }

        } catch (Exception e) {
            throw new EntityException("Error handling Items In Cache: " +
                    e.getMessage(), e);
        }

    }


    /**
     *
     */
    private void setSyncType() throws EntityException {

        try {
            if        (this.syncMode == AlertCode.SLOW){
                this.isSlowOrRefresh = true;
            } else if (this.syncMode == AlertCode.REFRESH_FROM_SERVER){
                this.isSlowOrRefresh = true;
            } else {
                this.isSlowOrRefresh = false;
            }
        } catch (Exception e){
            throw new EntityException("Error setting SyncSession Mode");
        }
    }

    /**
     * Sets the the <code>newItemKeys</code>, <code>updatedItemKeys</code>,
     * <code>deletedItemKeys</code> lists depending on filter.
     *
     * @param syncItemKey SyncItemKey
     * @throws SyncSourceException
     */
    private void setItemKeys() throws EntityException {

        if (this.filter.getFilterType().equalsIgnoreCase(Def.FILTER_TYPE_INC)){
            // inclusive
            if (this.filter.getId() != null){
                // id filter
                String idfilter = this.filter.getId();
                this.newItemKeys     = new ArrayList();
                this.updatedItemKeys = HelperForGetter.setUpdatedIDFilter(
                        idfilter, this.serverItems);
                this.deletedItemKeys = new ArrayList();
            } else {
                if (this.filter.getTime() != null) {

                    Map<String, SyncItemInfo> newItems = HelperForGetter.setNewItems(
                            this.localItems, this.serverItems);
                    this.newItemKeys = HelperForGetter.setFilteredItemsKeys(
                            this.filter, newItems);

                    deletedItemKeys = new ArrayList();
                    Map<String, SyncItemInfo> deletedItems = HelperForGetter.setDeletedItems(
                            this.localItems, this.serverItems);
                    for (SyncItemInfo item : deletedItems.values()) {
                        this.deletedItemKeys.add(item.getGuid());
                    }

                    Map<String, SyncItemInfo> updatedItems = HelperForGetter.setUpdatedItems(
                            this.localItems, this.serverItems);
                    this.updatedItemKeys = HelperForGetter.setFilteredItemsKeys(
                            this.filter, updatedItems);

                } else {
                    // no filter
                    this.newItemKeys     = HelperForGetter.setNewItemKeys(
                            this.localItems, this.serverItems);
                    this.updatedItemKeys = HelperForGetter.setUpdatedItemKeys(
                            this.localItems, this.serverItems);
                    this.deletedItemKeys = HelperForGetter.setDeletedItemKeys(
                            this.localItems, this.serverItems);
                }
            }
        } else {
            // exclusive
            if (this.filter.getId() != null){
                // id filter
                String idfilter = this.filter.getId();
                this.newItemKeys     = HelperForGetter.setNewItemKeysWithIdFilter(
                        idfilter, this.localItems, this.serverItems);
                this.updatedItemKeys = HelperForGetter.setUpdatedItemKeysWithIdFilter(
                        idfilter, this.localItems, this.serverItems);
                this.deletedItemKeys = HelperForGetter.setDeletedItemKeysWithIdFilter(
                        idfilter, this.localItems, this.serverItems);

            } else {
                if (this.filter.getTime() != null) {

                    Map<String, SyncItemInfo> newItems = HelperForGetter.setNewItems(
                            this.localItems, this.serverItems);

                    this.newItemKeys = HelperForGetter.setFilteredItemsKeys(
                            this.filter, newItems);

                    Map<String, SyncItemInfo> updatedItems = HelperForGetter.setUpdatedItems(
                            this.localItems, this.serverItems);

                    this.updatedItemKeys = HelperForGetter.setFilteredItemsKeys(
                            this.filter, updatedItems);

                    Map<String, SyncItemInfo> deletedItems = HelperForGetter.setDeletedItems(
                            this.localItems, this.serverItems);

                    this.deletedItemKeys = HelperForGetter.setFilteredItemsKeys(
                            this.filter, deletedItems);

                } else {
                    // no filter
                    this.newItemKeys     = HelperForGetter.setNewItemKeys(
                            this.localItems, this.serverItems);
                    this.updatedItemKeys = HelperForGetter.setUpdatedItemKeys(
                            this.localItems, this.serverItems);
                    this.deletedItemKeys = HelperForGetter.setDeletedItemKeys(
                            this.localItems, this.serverItems);
                }
            }
        }
    }


    /**
     * refresh the cache in the End Sync Session
     * Note that during the sync session the system proformes the
     * following operations:
     * <br>
     * - add items in the serverItems (email and folder / IMAP)
     * <br>
     * - remove items in the serverItems (email and folder / IMAP and POP)
     * <br>
     * - update items in the serverItems (email and folder / IMAP)
     * <br>
     * So that the system doesn't need to read all the items info
     * from the mail server at the end of the sync
     *
     *
     * @throws SyncSourceException
     */
    private void cacheAtEnd() throws EntityException {

        try {


            if (this.serverItems != null){
                // after the the sync session expires the ds-server calls
                // the endSync.
                // this.serverItems must be null in the following cases (see beginSync):
                //  - account not creted: Error Setting Preliminary Info
                //  - Error Connecting Mail Server
                //  - Error inserting Default Folder

                this.invalidItems = this.cacheDao.getInvalidItems(this.getSourceURI(),
                                                                  this.principalId,
                                                                  this.userName);

                this.cacheDao.updateLocalItems(this.serverItems,
                                               this.sentItems,
                                               this.invalidItems,
                                               this.getSourceURI(),
                                               this.principalId,
                                               this.userName);
            }

        } catch (Exception e) {
            throw new EntityException("Error caching items:" +
                    e.getMessage(), e);
        }

    }


    /**
     * set the device info for the session
     *
     * @param context
     * @throws EntityException
     */
    private void setDeviceInfo(SyncContext context) throws EntityException {

        String deviceId = null;
        String timezone = null;
        String deviceTimeZoneDescr = null;
        Sync4jDevice device = null;
        try {
            deviceId = context.getPrincipal().getDeviceId();
            device   = getDevice(deviceId);
            timezone = device.getTimeZone();
            if (device.getConvertDate()) {
                if (timezone != null && timezone.length() > 0) {
                    deviceTimeZoneDescr = timezone;
                    this.deviceTimeZone = TimeZone.getTimeZone(deviceTimeZoneDescr);
                }
            }
            this.deviceCharset = device.getCharset();
        } catch (PersistentStoreException ex) {
            throw new EntityException("Error setting device '" + deviceId + "'", ex);
        }

    }

    /**
     * set the mail server account object
     */
    private void setMailServerAccount() throws EntityException  {

        MailServerAccount msa = null;
        try {
            ConsoleManager consoleManager = new ConsoleManager();
            msa = consoleManager.getUser(this.userName);
        } catch (Exception e){
            throw new EntityException("Error getting account information ", e);
        }

        if (msa != null){
            this.mailServerAccount = msa;
            // add the prefix 'recent:' for a gmail server
            msa.setMsLogin(Utility.getLogin(msa));
        } else {
            throw new EntityException("Error: Account information not found!");
        }

    }


    /**
     * insert the default folder if needed
     *
     * @throws EntityException
     */
    private void insertDefaultFolder() throws EntityException {

        try {
            this.emailhelper.insertDefaultFolder(this.defaultFolder,
                                                 this.filter,
                                                 this.mailServerAccount.getMailServer().getProtocol(),
                                                 this.getSourceURI(),
                                                 this.principalId,
                                                 this.userName);
        } catch (EntityException e) {
            throw new EntityException("Error inserting default folders", e);
        }

    }


    /**
     * check if in the Drafts folder therse is an item
     * without message-id
     *
     * @throws SyncSourceException
     */
    private void checkMessageIDforDrafts()
    throws EntityException {
        try {
            this.emailhelper.checkMessageIDforDrafts(this.getSourceURI(),
                    this.principalId,
                    this.userName);
        } catch (EntityException e) {
            throw new EntityException("Error inserting default folders", e);
        }

    }

    /**
     * Return the device with the given deviceId
     *
     * @param deviceId String
     * @return Sync4jDevice
     * @throws PersistentStoreException
     */
    private Sync4jDevice getDevice(String deviceId) throws PersistentStoreException {
        Sync4jDevice device = new Sync4jDevice(deviceId);
        PersistentStore store = Configuration.getConfiguration().getStore();
        store.read(device);
        return device;
    }

    /**
     * Getter for property defaultFolder.
     *
     * @return Value of property defaultFolder.
     */
    private DefaultFolder getDefaultFolder() {
        return this.defaultFolder;
    }

    /**
     * Setter for property defaultFolder.
     *
     * @param defaultFolder DefaultFolder
     */
    private void setDefaultFolder(MailServer ms)
    throws EntityException {
        try {
            this.defaultFolder.setInboxName(ms.getInboxPath());
            this.defaultFolder.setOutboxName(ms.getOutboxPath());
            this.defaultFolder.setSentName(ms.getSentPath());
            this.defaultFolder.setDraftsName(ms.getDraftsPath());
            this.defaultFolder.setTrashName(ms.getTrashPath());
        } catch (Exception e) {
            throw new EntityException("Error setting default folders paths", e);
        }
    }

    /**
     *
     */
    private String printFolders() {
        String out = this.defaultFolder.getInboxName() + " (" +
                this.mailServerAccount.getMailServer().getInboxActivation() + "); " +
                     this.defaultFolder.getOutboxName() + " (" +
                this.mailServerAccount.getMailServer().getOutboxActivation() + "); " +
                     this.defaultFolder.getSentName()   + " (" +
                this.mailServerAccount.getMailServer().getSentActivation() + "); " +
                     this.defaultFolder.getDraftsName() + " (" +
                this.mailServerAccount.getMailServer().getDraftsActivation() + "); " +
                     this.defaultFolder.getTrashName()  + " (" +
                this.mailServerAccount.getMailServer().getTrashActivation() + "); ";
        return out;
    }

    /**
     *
     */
    private String printIntro() {
        String out =
                "\nCOORDS " +
                "\nprincipalId  " + this.principalId +
                "\nlogin        " + this.userName +
                "\ninHost       " + this.mailServerAccount.getMailServer().getInServer() +
                "\noutHost      " + this.mailServerAccount.getMailServer().getOutServer() +
                "\nprotocol     " + this.mailServerAccount.getMailServer().getProtocol() +
                "\nfolders      [" + this.printFolders() + "]" +
                "\nFILTER\n" + this.filter;
        return out;
    }

}
