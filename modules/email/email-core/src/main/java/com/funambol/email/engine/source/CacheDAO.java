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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import javax.sql.DataSource;

import com.funambol.email.util.Def;
import com.funambol.email.util.Query;
import com.funambol.email.util.Utility;
import com.funambol.email.exception.EntityException;
import com.funambol.email.model.SendResult;
import com.funambol.email.model.SyncItemInfo;
import com.funambol.framework.engine.SyncItemKey;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.server.db.RoutingDataSource;

/**
 * Contains methods to access to the table with the state of the items.
 *
 * @version $Id: CacheDAO.java,v 1.3 2008-05-29 07:35:19 gbmiglia Exp $
 *
 */
public class CacheDAO {

    // --------------------------------------------------------------- Constants

    /**     */
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    private static final String USER_DATASOURCE_JNDINAME = "jdbc/fnbluser";

    // ------------------------------------------------------------ Private data

    /** The (Routing) datasource to access to the user db */
    private RoutingDataSource routingDataSource;


    // ------------------------------------------------------------- Constructor

    /**
     *
     */
    public CacheDAO() {
    }

    /**
     *
     *
     * @param jndiDataSourceName String
     * 
     * @deprecated Since v66, the db is partitioned and the datasource name is
     *             hardcoded. Use the constructor without jndiDataSourceName
     */
    public CacheDAO(String jndiDataSourceName) {
    }


    // ---------------------------------------------------------- Public Methods

    /**
     *
     * @throws Exception
     */
    public void init() throws Exception {
        routingDataSource = 
            (RoutingDataSource)DataSourceTools.lookupDataSource(USER_DATASOURCE_JNDINAME);
    }

    /**
     * Delete local items
     *
     * @param sourceURI
     * @param principalId
     * @throws EntityException
     */
    public void deleteSentPop(String sourceURI, long principalId, String username)
    throws EntityException {

        Connection         connection  = null ;
        PreparedStatement  ps          = null ;

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO delete fnbl_email_sentpop "
                    + principalId+ " - " +sourceURI);
        }
        try {
            connection = routingDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.SENT_DELETE_POP);
            ps.setString  (1, sourceURI ) ;
            ps.setLong    (2, principalId ) ;
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EntityException("Error deleting fnbl_email_sentpop", e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     * Delete local items
     *
     * @param username
     * @param protocol
     *
     * @throws Exception
     **/
    public void updateDeletedStatus(String username, String protocol)
    throws EntityException {

        Connection         connection  = null ;
        PreparedStatement  ps          = null ;

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO reset the D status in fnbl_email_inbox "
                    + username + " - " + protocol);
        }
        try {
            connection = routingDataSource.getRoutedConnection(username);

            ps = connection.prepareStatement(Query.INBOX_REMOVE_DELETED_STATUS);

            ps.setString  (1, username ) ;
            ps.setString  (2, protocol ) ;

            ps.executeUpdate();

        } catch (Exception e) {
            throw new EntityException("Error deleting fnbl_email_sentpop", e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     * Delete local items
     *
     *
     * @param sourceURI String
     * @param principal String
     * @throws Exception
     */
    public void deleteLocalItems(String sourceURI, long principal, String username)
    throws EntityException {

        Connection         connection  = null ;
        PreparedStatement  ps          = null ;

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO delete start "+ principal+ " - " +sourceURI);
        }
        try {
            connection = routingDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.CACHE_DELETE_LOCAL_ITEMS);
            ps.setString  (1, sourceURI ) ;
            ps.setLong    (2, principal ) ;
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EntityException("Error deleting local items", e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     * Delete local items
     *
     * @param GUID String
     * @param username String
     * @param protocol String
     * @throws EntityException
     */
    public void deleteLocalItem(String GUID, String username, String protocol)
    throws EntityException {

        Connection         connection  = null ;
        PreparedStatement  ps          = null ;

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO delete id " + GUID + " - " +
                    username+ " - " +protocol);
        }
        try {
            connection = routingDataSource.getRoutedConnection(username);
            ps = connection.prepareStatement(Query.INBOX_DELETE_EMAIL);
            ps.setString  (1, GUID ) ;
            ps.setString  (2, username ) ;
            ps.setString  (3, protocol ) ;
            ps.executeUpdate();
        } catch (Exception e) {
            throw new EntityException("Error deleting local item", e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

    /**
     * Update local items
     *
     * @param items map with all the mail server CrcSyncItemInfo
     * @param itemsSent ArrayList
     * @param invalidItems CrcSyncItemInfo[]
     * @param sourceURI String
     * @param principal String
     * @param username the username
     * @throws Exception
     */
    //public void updateLocalItems(HashMap items,
    public void updateLocalItems(Map items,
            List itemsSent,
            SyncItemInfo[] invalidItems,
            String sourceURI,
            long principal,
            String username)
            throws EntityException {

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO update start");
        }

        this.deleteLocalItems(sourceURI, principal, username);

        this.addLocalItems(items, itemsSent, invalidItems, sourceURI, principal, username);

        if (log.isTraceEnabled()) {
            log.trace("ItemStateDAO update end");
        }
    }

    /**
     * Gets local items
     *
     *
     * @param sourceURI String
     * @param principalId String
     * @return map with CrcSyncItemInfo
     * @throws Exception
     */
    public LinkedHashMap getLocalItems(String sourceURI, long principalId, String username)
    throws EntityException {

        Connection connection   = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        LinkedHashMap items     = null;
        String GUID             = null;
        long   lastCRC          = 0;
        String isInvalid        = null;
        String isInternal       = null;
        String isEmail          = null;
        String messageID        = null;
        java.util.Date headerDate = null;
        java.util.Date received = null;
        String subject          = null;
        String sender           = null;

        try {

            connection = routingDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            items = new LinkedHashMap();

            ps = connection.prepareStatement(Query.CACHE_SELECT_ITEMS_BY_PRINCIPAL_BY_SOURCE);

            ps.setString(1, sourceURI);
            ps.setLong(2, principalId);

            rs = ps.executeQuery();

            while(rs.next()) {

                GUID      = rs.getString(1);
                lastCRC   = rs.getLong(2);
                isInvalid = rs.getString(3);
                isInternal = rs.getString(4);
                messageID  = rs.getString(5);
                headerDate = Utility.UtcToDate(rs.getString(6));
                received   = Utility.UtcToDate(rs.getString(7));
                subject    = rs.getString(8);
                sender     = rs.getString(9);
                isEmail    = rs.getString(10);

                items.put(GUID, new SyncItemInfo(new SyncItemKey(GUID), lastCRC,
                        messageID, headerDate,
                        received, subject, sender,
                        isInvalid, isInternal,
                        isEmail, null));
            }

        } catch (Exception e) {
            throw new EntityException("Error getting local items", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return items;
    }


    /**
     * Gets local items
     *
     *
     * @param sourceURI String
     * @param principalId String
     * @return CrcSyncItemInfo[]
     * @throws Exception
     */
    public SyncItemInfo[] getInvalidItems(String sourceURI, long principalId, String username)
            throws EntityException {

        Connection connection   = null;
        PreparedStatement ps    = null;
        ResultSet rs            = null;
        ArrayList rows          = null;
        SyncItemInfo[] items = null;

        try {

            connection = routingDataSource.getRoutedConnection(username);
            connection.setReadOnly(true);

            rows = new ArrayList();

            ps = connection.prepareStatement(Query.CACHE_SELECT_INVALID_ITEMS);

            ps.setString(1, sourceURI);
            ps.setLong(2, principalId);

            rs = ps.executeQuery();

            String guid = null;
            long lastCrc = 0;
            String messageID = null;
            java.util.Date headerDate = null;
            java.util.Date received = null;
            String subject = null;
            String sender = null;
            String invalid = null;
            String internal = null;
            String isemail = null;

            while(rs.next()) {

                guid       = rs.getString(1);
                lastCrc    = rs.getLong(2);
                invalid    = rs.getString(3);
                internal   = rs.getString(4);
                messageID  = rs.getString(5);
                headerDate = Utility.UtcToDate(rs.getString(6));
                received   = Utility.UtcToDate(rs.getString(7));
                subject    = rs.getString(8);
                sender     = rs.getString(9);
                isemail    = rs.getString(10);

                rows.add(new SyncItemInfo(new SyncItemKey(guid), lastCrc, messageID, headerDate,
                        received, subject, sender,
                        invalid, internal, isemail, null));

            }

            items = (SyncItemInfo[]) rows.toArray(new SyncItemInfo[0]);

        } catch (Exception e) {
            throw new EntityException("Error getting local items", e);
        } finally {
            DBTools.close(connection, ps, rs);
        }

        return items;
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Adds local items
     *
     *
     * @param items map with all the mail server CrcSyncItemInfo
     * @param itemsSent ArrayList
     * @param invalidItems CrcSyncItemInfo[]
     * @param sourceURI String
     * @param principal String
     * @param username 
     * @throws Exception
     */
    //private void addLocalItems(HashMap items,
    private void addLocalItems(Map items,
            List itemsSent,
            SyncItemInfo[] invalidItems,
            String sourceURI,
            long principal,
            String username)
            throws EntityException {

        SendResult         sr            ;
        Connection         connection    = null;
        PreparedStatement  ps            = null;
        String             id            = null;
        String             GUID          = null;
        String             LUID          = null;
        String             isInvalid     = null;
        String             isInternal    = null;
        String             isEmail       = null;
        String             messageId     = null;
        java.util.Date     headerDate    = null;
        java.util.Date     received      = null;
        String             subject       = null;
        String             sender        = null;
        long               lastCrc       ;
        boolean            isSent        ;
        boolean            isPOP         ;


        try {

            connection = routingDataSource.getRoutedConnection(username);

            if (log.isTraceEnabled()) {
                log.trace("ItemStateDAO add start " + items.size());
            }

            int sentLen    = itemsSent.size();
            int invalidLen = invalidItems.length;


            Iterator values = items.values().iterator();

            SyncItemInfo scrcii = null;
            while (values.hasNext()) {

                scrcii = (SyncItemInfo)values.next();

                ps = connection.prepareStatement(Query.CACHE_ADD_LOCAL_ITEM);

                // get id
                id       = scrcii.getGuid().getKeyAsString();

                // get CRC
                lastCrc  = scrcii.getLastCrc() ;

                // set the invalid flag
                isInvalid = null;
                for (int k=0; k<invalidLen; k++){
                    if (invalidItems[k].getGuid().equals(id)) {
                        isInvalid = invalidItems[k].getInvalid();
                        break;
                    }
                }

                // change the id for Sent and Outbox folder
                if (id.startsWith(Def.FOLDER_SENT_ID)){
                    for (int y=0; y<sentLen; y++) {
                        sr = (SendResult) itemsSent.get(y);
                        GUID   = sr.getGUID();
                        isSent = sr.isSent();
                        if (GUID.equals(id) && isSent){
                            id = Utility.convertSentIDToOutboxID(id);
                            break;
                        }
                    }
                }

                // messageID
                messageId  = scrcii.getMessageID();

                // header date
                headerDate = scrcii.getHeaderDate();
                String headerDateS = null;
                if (headerDate != null){
                    headerDateS = Utility.UtcFromDate(headerDate);
                }

                // received
                received = scrcii.getHeaderReceived();
                String receivedS = null;
                if (received != null){
                    receivedS = Utility.UtcFromDate(received);
                }

                subject = scrcii.getSubject();

                sender = scrcii.getSender();

                // internal email
                isInternal = scrcii.getInternal();

                // flag for email or folder
                isEmail = scrcii.getIsEmail();


                ps.setString(1, id       );
                ps.setLong  (2, lastCrc  );
                ps.setString(3, isInvalid);
                ps.setString(4, isInternal);
                ps.setString(5, sourceURI);
                ps.setLong  (6, principal);
                ps.setString(7, messageId);
                ps.setString(8, headerDateS);
                ps.setString(9, receivedS);
                ps.setString(10, subject);
                ps.setString(11, sender);
                ps.setString(12, isEmail);

                ps.executeUpdate();

                ps.close() ;
                ps = null  ;

            }

        } catch (Exception e) {
            throw new EntityException("Error adding local items", e);
        } finally {
            DBTools.close(connection, ps, null);
        }
    }

}
