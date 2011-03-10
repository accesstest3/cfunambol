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

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import com.sun.mail.imap.IMAPFolder;
import com.funambol.framework.tools.DBTools;
import com.funambol.email.transport.IMailServerWrapper;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.ItemFolder;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.email.transport.CommonMailServerWrapper;
import com.funambol.email.util.Def;
import com.funambol.email.util.Query;
import com.funambol.email.util.Utility;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.server.db.RoutingDataSource;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;



/**
 * This class implements methods to access data
 * in Mail Server datastore.
 *
 * @version $Id: EntityDAO.java,v 1.5 2008-06-03 09:18:05 testa Exp $
 */
public class EntityDAO {

    // --------------------------------------------------------------- Constants

    private static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";

    // ---------------------------------------------------------- Protected data

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**      */
    protected String serverType = null;
    
    protected RoutingDataSource userDataSource = null;

    // ------------------------------------------------------------ Constructors

    /**
     *
     */
    public EntityDAO(){
    }

    /**
     *
     */
    public EntityDAO(String serverType) throws EntityException {
        this.serverType         = serverType;
        try {

            userDataSource = (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);

        } catch (Exception e){
            throw new EntityException("Error looking up the datasource", e);
    }

    }

    /**
     *
     * @param jndiDataSourceName String
     *
     * @deprecated Since v66, the db has been splitted in core db and user db so
     * the jndi names are now hardcoded. Use the constructor without jndiDataSourceName
     */
    public EntityDAO(String jndiDataSourceName, String serverType) throws EntityException{
        this(serverType);
    }

    // -----------------------------------------------------------Public Methods

    /**
     *
     * insert a custom folder
     *
     * @param folder IMAPFolder
     * @param defFullpath String
     * @param idFolderSpace IdSpaceGenerator
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void addCustomFolderInDB(IMAPFolder folder,
            String defFullpath,
            DBIDGenerator idFolderSpace,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        String[] allFolder = null;

        // check all subfolders and insert if nedded
        try {
            insertSubFolder(folder, idFolderSpace, source_uri, principalId, username);
        } catch (MessagingException me){
            throw new EntityException(me);
        }
    }

    /**
     *
     * gets the folder fullpath from the local DB
     *
     *
     * @param GUID folder id
     * @param source_uri String
     * @param principalId long
     * @return String
     * @throws EntityException
     */
    public String getFullPathFromGUID(String GUID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        String fullpath       = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.FOLDER_SELECT_FULLPATH);

            ps.setString(1, GUID       );
            ps.setString(2, source_uri );
            ps.setLong  (3, principalId);

            rs = ps.executeQuery();
            while(rs.next()) {
                fullpath = rs.getString(1);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return fullpath;
    }

    /**
     *
     * gets the folder fullpath from the local DB
     * In the local db the GUID is composed by:
     * parentId + separator + objectId
     * This method look up the item with the
     * GUID = '%/FID'
     *
     *
     *
     * @param FID folder id
     * @param source_uri String
     * @param principalId long
     * @return String
     * @throws EntityException
     */
    public String getFullPathFromFID(String FID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        String fullpath       = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;


        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.FOLDER_SELECT_FULLPATH_FROM_FID);

            ps.setString(1, "%" + Def.SEPARATOR_FIRST + FID ) ;
            ps.setString(2, source_uri                ) ;
            ps.setLong   (3, principalId               ) ;

            rs = ps.executeQuery();
            while(rs.next()) {
                fullpath = rs.getString(1);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return fullpath;
    }

    /**
     *
     * gets the Folder id from the local DB
     *
     *
     * @param fullpath folder full path
     * @param source_uri String
     * @param principalId long
     * @return String
     * @throws EntityException
     */
    public String getGUIDFromFullPath(String fullpath,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        String GUID           = null;
        Connection connection = null;
        PreparedStatement  ps = null;
        ResultSet rs          = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.FOLDER_SELECT_FOLDERID);

            ps.setString(1, source_uri );
            ps.setLong  (2, principalId);
            ps.setString(3, fullpath   );

            rs = ps.executeQuery();
            while(rs.next()) {
                GUID     = rs.getString(1);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return GUID;
    }

    /**
     * This method inserts the folder in the MailServer
     *
     * @param cmsw CommonMailServerWrapper
     * @param folderName String
     * @throws EntityException
     */
    public void addDefaultFolderInMailServer(CommonMailServerWrapper cmsw,
            String folderName)
            throws EntityException {

        try {
            Folder folderToInsert = cmsw.getMailDefaultFolder().getFolder(folderName);
            int type = 0; // set the right folder type
            if (!folderToInsert.exists()) {
                folderToInsert.create(Folder.HOLDS_MESSAGES);
            }
        } catch (Exception e) {
            throw new EntityException(e);
        }
    }

    /**
     *
     * serch a folder using the GUID in the local DB
     *
     *
     * @param GUID folder id
     * @param source_uri String
     * @param principalId long
     * @return ItemFolder
     * @throws EntityException
     */
    public ItemFolder getFolderFromUID(String GUID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        ItemFolder eisFolder   = null;
        String     fullpath    = null;
        Connection connection  = null;
        PreparedStatement ps   = null;
        ResultSet rs           = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            ps = connection.prepareStatement(Query.FOLDER_SELECT_FOLDER);
            ps.setString(1, GUID       );
            ps.setString(2, source_uri );
            ps.setLong  (3, principalId);

            rs = ps.executeQuery();

            String FID  = null;
            while(rs.next()) {
                GUID      = rs.getString(1);
                FID       = rs.getString(2);
                fullpath  = rs.getString(3);
            }

            if (fullpath != null) {

                String name = Utility.getFolderNameFromFullpath(fullpath);

                eisFolder = new ItemFolder(GUID, name, FID, null,
                        null, null, null, null);
            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return eisFolder;
    }

    /**
     *
     *
     * @param f IMAPFolder
     * @param cmsw CommonMailServerWrapper
     * @throws EntityException
     */
    public void checkMessageIDforDrafts(IMAPFolder f,
            CommonMailServerWrapper cmsw)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start checkMessageIDforDrafts");
        }

        try {

            Message[] messages = f.getMessages();
            int n = messages.length;

            Message msg = null;
            Message msgNew  = null;

            for (int i=0; i<n; i++) {
                msg = messages[i];

                String message_id = Utility.getHeaderMessageID(msg);

                if (message_id == null){

                    // create a new message id and set the msg
                    java.util.Random generator = new java.util.Random();
                    long random        = generator.nextLong();
                    long validity      = f.getUIDValidity();
                    long id            = f.getUID(msg);
                    String userAddress = cmsw.getUserAddress();
                    String serverOut   = cmsw.getServerOut();
                    message_id = "<" + userAddress + "." +
                            validity    + "." +
                            id          + "." +
                            random      + "@" + serverOut + ">";

                    // clone the message and set the Message-ID
                    Message newMsg = new MimeMessage((MimeMessage)msg);
                    //newMsg.setHeader(Def.HEADER_LABEL_MESSAGE_ID, message_id);
                    newMsg.saveChanges();

                    // delete old
                    Message[] msgsToDelete = new Message[1];
                    msgsToDelete[0] = msg;
                    f.setFlags(msgsToDelete, new Flags(Flags.Flag.DELETED), true);

                    // append new
                    Message[] msgsToAppend = new Message[1];
                    msgsToAppend[0] = newMsg;
                    f.appendMessages(msgsToAppend);

                }
            }

        } catch (MessagingException e) {
            throw new EntityException(e);
        }
    }

    /**
     *
     *
     *
     * @param fullPath folder full path
     * @param parentId parent folder id
     * @param idFolderSpace IdSpaceGenerator
     * @param source_uri String
     * @param principalId long
     * @return String
     * @throws EntityException
     */
    public String insertFolder(String fullPath,
            String parentId,
            DBIDGenerator idFolderSpace,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        Connection connection = null;
        PreparedStatement  ps = null;
        String GUID           = null;
        String FID            = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.FOLDER_INSERT_FOLDER);

            GUID = Utility.createFolderGUID(parentId, ""+idFolderSpace.next());

            FID = parentId;

            ps.setString(1, GUID);
            ps.setString(2, FID);
            ps.setString(3, source_uri);
            ps.setLong(4, principalId);
            ps.setString(5, fullPath);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return GUID;
    }


    /**
     * check if the Folder is in the mail server
     *
     * @param msw - IMailServerWrapper .
     * @param fullpath - String .
     * @return true if the folder is in the Mail Server - boolean.
     * @throws EntityException
     */
    public Folder getDefaultFolder(IMailServerWrapper msw,
            String fullpath)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start isDefaultFolderPresent");
        }

        Folder f = null;

        try {
            f = msw.getMailDefaultFolder().getFolder(fullpath);
            if (f.exists()) {
                return f;
            }
        } catch (MessagingException e) {
            throw new EntityException(e);
        }

        return null;
    }

    /**
     *
     * remove folder in the local fnbl_email_folder
     *
     * @param GUID String
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void removeFolderInDB(String GUID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeFolder UID " + GUID );
        }

        Connection connection = null;
        PreparedStatement  ps = null ;
        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.FOLDER_DELETE_FOLDER);

            ps.setString(1, GUID       );
            ps.setString(2, source_uri );
            ps.setLong  (3, principalId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * @param username String
     * @return The Items Collection with all info
     * @throws EntityException
     */
    public Map getAllEmailsInbox(String username,
            String protocol,
            int maxEmailNumber)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start getAllEmails Inbox");
        }

        LinkedHashMap items   = new LinkedHashMap();
        Connection connection = null;
        PreparedStatement ps  = null;
        ResultSet rs          = null;
        String GUID       = null;
        long lastCRC      = 0;
        String isInvalid  = null;
        String isInternal = null;
        String messageid  = null;
        java.util.Date headerdate = null;
        java.util.Date received   = null;
        String subject  = null;
        String sender   = null;
        String status   = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);
            
            ps = connection.prepareStatement(Query.INBOX_SELECT_UNDELETED_MAILBOX_INFO);

            ps.setString(1, username);
            ps.setString(2, protocol);

            rs = ps.executeQuery();

            int counter = 0;

            while(rs.next()) {

                counter++;

                GUID       = rs.getString(1);
                lastCRC    = rs.getLong(2);
                isInvalid  = rs.getString(3);
                isInternal = rs.getString(4);
                messageid  = rs.getString(5);
                headerdate = Utility.UtcToDate(rs.getString(6));
                received   = Utility.UtcToDate(rs.getString(7));
                subject    = rs.getString(8);
                sender     = rs.getString(9);
                status     = rs.getString(10);
                
                // in the cache the inbox-listener save 2 items over the maxEmailNumber
                // [ see class InboxListenerManager.setInfosInDB() ]
                // but this method must get just 'maxEmailNumber' items
                if (counter <= maxEmailNumber){
                    items.put(GUID, new SyncItemInfo(new SyncItemKey(GUID), lastCRC,
                            messageid, headerdate, received, subject, sender,
                            isInvalid, isInternal, "Y", status)); 
                }

            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, rs);
        }
        if (items != null) {
            if (log.isTraceEnabled()) {
                log.trace("number of items " + items.size());
            }
        }

        return items;
    }

    /**
     *
     * remove the item in the local fnbl_email_inbox
     *
     * @param username String
     * @param protocol String
     * @param GUID String
     * @throws EntityException
     */
    public void removeEmailInDB(String username,
            String protocol,
            String GUID)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeEmailFromInbox UID " + GUID +
                    " username " + username);
        }

        Connection connection = null;
        PreparedStatement  ps = null ;
        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_DELETE_EMAIL);

            ps.setString(1, GUID ) ;
            ps.setString(2, username ) ;
            ps.setString(3, protocol ) ;

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * add the item in the local fnbl_email_inbox
     *
     * @param syncItemInfo syncItemInfo
     * @param username String
     * @param protocol String
     * @throws EntityException
     */
    public void addEmailInDB(SyncItemInfo syncItemInfo,
            String username,
            String protocol)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start addEmailInDB username " + username);
        }

        Connection connection = null;
        PreparedStatement  ps = null ;

        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_INSERT_EMAIL);

            String GUID      = syncItemInfo.getGuid().getKeyAsString();
            long lastCRC     = syncItemInfo.getLastCrc();
            String messageID = syncItemInfo.getMessageID();
            Date headerDate  = syncItemInfo.getHeaderDate();
            String headerDateS = "";
            if (headerDate != null){
                headerDateS = Utility.UtcFromDate(headerDate);
            }
            Date received    = syncItemInfo.getHeaderReceived();
            String receivedS = "";
            if (received != null){
                receivedS = Utility.UtcFromDate(received);
            }
            String subject     = syncItemInfo.getSubject();
            String sender      = syncItemInfo.getSender();
            String isInvalid   = syncItemInfo.getInvalid();
            String isInternal  = syncItemInfo.getInternal();
            String status      = syncItemInfo.getStatus();

            ps.setString(1, username);
            ps.setString(2, protocol);
            ps.setString(3, GUID);
            ps.setLong  (4, lastCRC);
            ps.setString(5, isInvalid);
            ps.setString(6, isInternal);
            ps.setString(7, messageID);
            ps.setString(8, headerDateS);
            ps.setString(9, receivedS);
            ps.setString(10, subject);
            ps.setString(11, sender);
            ps.setString(12, status);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     *
     *
     * @param username String
     * @param protocol String
     * @param GUID String
     * @throws EntityException
     */
    public void setDeletedEmailInInbox(String username,
            String protocol,
            String GUID)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeEmailFromInbox UID " + GUID +
                    " username " + username);
        }

        Connection connection = null;
        PreparedStatement  ps = null ;

        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_SET_DELETED_STATUS);

            ps.setString(1, username);
            ps.setString(2, protocol);
            ps.setString(3, GUID    );

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     * remove the item in the server items map
     *
     * @param GUID String
     * @param serverItems all info in CrcSyncItemInfo
     * @throws EntityException
     */
    public void removeItemInServerItems(String GUID,
            Map serverItems)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start removeEmailFromServerItems UID " + GUID );
        }

        serverItems.remove(GUID);
    }

    /**
     *
     * update the item in the server items map
     *
     * @param GUID String
     * @param serverItems all info in CrcSyncItemInfo
     * @throws EntityException
     */
    public void updateItemInServerItems(SyncItemInfo syncItemInfo,
            String GUID,
            Map serverItems)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start updateItemInServerItems UID " + GUID );
        }

        SyncItemInfo siiToUpdate = (SyncItemInfo)serverItems.get(GUID);

        if (siiToUpdate != null){
            siiToUpdate.setLastCrc(syncItemInfo.getLastCrc());
            siiToUpdate.setMessageID(syncItemInfo.getMessageID());
            siiToUpdate.setHeaderDate(syncItemInfo.getHeaderDate());
            siiToUpdate.setHeaderReceived(syncItemInfo.getHeaderReceived());
            siiToUpdate.setSubject(syncItemInfo.getSubject());
            siiToUpdate.setSender(syncItemInfo.getSender());
            siiToUpdate.setInvalid(syncItemInfo.getInvalid());
            siiToUpdate.setInternal(syncItemInfo.getInternal());
        }

    }

    /**
     *
     * update the item in the server items map
     *
     * @param GUID String
     * @param serverItems all info in CrcSyncItemInfo
     * @throws EntityException
     */
    public void updateCRCInServerItems(long crc,
            String GUID,
            Map serverItems)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("DAO start updateItemInServerItems UID " + GUID );
        }
        SyncItemInfo siiToUpdate = (SyncItemInfo)serverItems.get(GUID);
        if (siiToUpdate != null){
            siiToUpdate.setLastCrc(crc);
        }

    }

    /**
     *
     * add the item in the server items map
     *
     * @param syncItemInfo SyncItemInfo
     * @param serverItems all info in the mail server
     * @throws EntityException
     */
    public void addItemInServerItems(SyncItemInfo syncItemInfo,
            Map serverItems)
            throws EntityException {
        if (log.isTraceEnabled()) {
            log.trace("DAO start addItemInServerItems");
        }
        String GUID = syncItemInfo.getGuid().getKeyAsString();
        serverItems.put(GUID, syncItemInfo);
    }

    /**
     *
     * This method inserts one of the default folders if needed
     *
     *
     * @param GUID String
     * @param parentId String
     * @param fullPath String
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void addDefaultFolderInDB(String GUID,
            String parentId,
            String fullPath,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        Connection connection = null;
        PreparedStatement  ps = null;

        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.FOLDER_INSERT_FOLDER);

            ps.setString(1, GUID        ) ;
            ps.setString(2, parentId    ) ;
            ps.setString(3, source_uri  ) ;
            ps.setLong  (4, principalId ) ;
            ps.setString(5, fullPath    ) ;

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * This method update the CRC for an email in the fnbl_email_inbox
     *
     * @param crc new crc long
     * @param username String
     * @param protocol String
     * @param GUID String
     * @throws EntityException
     */
    public void updateCRCInDB(long crc,
            String username,
            String protocol,
            String GUID)
            throws EntityException {


        if (log.isTraceEnabled()) {
            log.trace("DAO start updateEmailInDB UID " + GUID );
        }

        Connection connection = null;
        PreparedStatement  ps = null;

        try {
            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_UPDATE_CRC);

            ps.setLong  (1, crc     );
            ps.setString(2, username);
            ps.setString(3, protocol);
            ps.setString(4, GUID    );

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * This method inserts one of the default folders if needed
     *
     *
     * @param GUID String
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void insertInvalidItemInCache(String GUID,
            String messageId,
            java.util.Date headerDate,
            java.util.Date received,
            String subject,
            String sender,
            String isEmail,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        Connection connection = null;
        PreparedStatement  ps = null;

        try {

            connection = userDataSource.getRoutedConnection(username);

            // try to update an item in the local db
            ps = connection.prepareStatement(Query.CACHE_INVALID_ITEM);

            ps.setString(1, GUID        ) ;
            ps.setString(2, source_uri  ) ;
            ps.setLong   (3, principalId ) ;

            int num = ps.executeUpdate();

            // if the item is not in the local DB
            if (num==0){

                ps = connection.prepareStatement(Query.CACHE_ADD_LOCAL_ITEM);

                String headerDateS = null;
                if (headerDate != null){
                    headerDateS = Utility.UtcFromDate(headerDate);
                }
                String receivedS = null;
                if (received != null){
                    receivedS = Utility.UtcFromDate(received);
                }

                ps.setString(1, GUID);
                ps.setLong  (2, 1  );
                ps.setString(3, "Y");
                ps.setString(4, null);
                ps.setString(5, source_uri);
                ps.setLong  (6, principalId);
                ps.setString(7, messageId);
                ps.setString(8, headerDateS);
                ps.setString(9, receivedS);
                ps.setString(10, subject);
                ps.setString(11, sender);
                ps.setString(12, isEmail);

                num = ps.executeUpdate();
            }

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     *
     * This method inserts one of the default folders if needed
     *
     *
     * @param GUID String
     * @param source_uri String
     * @param principalId long
     * @throws EntityException
     */
    public void insertInvalidItemInCache(String GUID,
            String source_uri,
            long principalId,
            String username)
            throws EntityException {

        Connection connection = null;
        PreparedStatement  ps = null;

        try {

            connection = userDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.CACHE_INVALID_ITEM_FROM_CLIENT);

            ps.setString(1, GUID        ) ;
            ps.setString(2, source_uri  ) ;
            ps.setLong   (3, principalId ) ;

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

    }

    /**
     * Retreieve the token from given the username and the guid.
     * 
     * @param username
     * @param GUID
     * @return the email token associated to that email
     * @throws com.funambol.email.exception.EntityException
     */
    public String getToken(String username, String GUID)
            throws EntityException {
        
        String token           = null;
        
        Connection connection  = null;
        PreparedStatement ps   = null;
        ResultSet rs           = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);
            ps = connection.prepareStatement(Query.INBOX_GET_TOKEN);
            ps.setString(1, username) ;
            ps.setString(2, GUID) ;
            rs = ps.executeQuery();

            while(rs.next()) {
                token = rs.getString(1);
            }

            ps.close() ;
            ps = null  ;

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return token;
    }
    
    /**
     * Retrieve the email guid given its token.
     * 
     * @param username 
     * @param token
     * @return the email guid, <code>null</code> if the guid has not been found.
     * @throws com.funambol.email.exception.EntityException
     */
    public String getGuidByToken(String username, String token)
            throws EntityException {
        
        String guid            = null;
        
        Connection connection  = null;
        PreparedStatement ps   = null;
        ResultSet rs           = null;

        try {

            connection = userDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);
            ps = connection.prepareStatement(Query.INBOX_GET_GUID_BY_TOKEN);
            ps.setString(1, token);
            rs = ps.executeQuery();

            while(rs.next()) {
                guid = rs.getString(1);
            }

            ps.close() ;
            ps = null  ;

        } catch (Exception e) {
            throw new EntityException(e);
        } finally {
            DBTools.close(connection, ps, null);
        }

        return guid;
    }
    
    //---------------------------------------------------------- Private methods

    /**
     *
     * @param folder starting folder
     * @param fs all folders container
     * @throws MessagingException
     */
    private void insertSubFolder(IMAPFolder folder,
            DBIDGenerator idFolderSpace,
            String source_uri,
            long principalId,
            String username)
            throws MessagingException, EntityException {

        javax.mail.Folder[] folderList = folder.list();

        IMAPFolder f;

        String fullpath = null;
        String GUID = null;
        String parentGUID = null;
        String parentId = null;
        String parentfullpath = null;

        for (int i = 0; i < folderList.length; i++) {

            f = ((IMAPFolder) folderList[i]);

            fullpath = f.getFullName();

            GUID = getGUIDFromFullPath(fullpath, source_uri, principalId, username);


            if (GUID == null){

                parentfullpath = Utility.getParentFullpathFromFullpath(fullpath);

                parentGUID = getGUIDFromFullPath(parentfullpath, source_uri, principalId, username);

                parentId = Utility.getKeyPart(parentGUID,1);

                // insert the missed folder
                insertFolder(fullpath, parentId,  idFolderSpace,
                        source_uri, principalId, username);

            }

            insertSubFolder(f, idFolderSpace, source_uri, principalId, username);
        }

    }

}
