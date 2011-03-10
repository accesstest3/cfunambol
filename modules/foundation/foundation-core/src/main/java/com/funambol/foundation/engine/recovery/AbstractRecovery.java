/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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
package com.funambol.foundation.engine.recovery;

import java.security.Principal;

import java.sql.*;

import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import com.funambol.framework.engine.*;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.tools.DBTools;

/**
 * This class implements SyncSource for recovery information.
 *
 * @version $Id: AbstractRecovery.java,v 1.1.1.1 2008-03-20 21:38:31 stefano_fornari Exp $
 */
public abstract class AbstractRecovery
extends AbstractSyncSource
implements SyncSource, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public final static String DATA_SOURCE_NAME = "java:/jdbc/fnblds";
    public final static String SQL_DELETE_CLIENT_MAPPING =
        "delete from fnbl_client_mapping where principal=?";
    public final static String SQL_DELETE_LAST_SYNC =
        "delete from fnbl_last_sync where principal=?";
    public final static String SQL_UPDATE_LAST_SYNC =
        "update fnbl_last_sync set last_anchor_server=?, start_sync=?, end_sync=? " +
        " where principal=? and sync_source=?";
    public final static String SQL_INSERT_LAST_SYNC =
        "insert into fnbl_last_sync(principal,sync_source,last_anchor_server,start_sync,end_sync) " +
        " values(?,?,?,?,?)";

    public static final String SOURCE_SEPARATOR = ",";
    public static final String KEY_ITEM = "KEYRECOVERY";
    public static final String LOG_NAME = "engine.recovery";

    // ---------------------------------------------------------- Protected data

    protected String uri        = null;
    protected String last    = null;
    protected String sourceList = null;
    protected FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

    // ------------------------------------------------------------ Constructors

    /** Creates a new instance of AbstractRecovery */
    protected AbstractRecovery() {
    }

    public AbstractRecovery(String uri, String last, String sourceList) {

        this.uri        = uri;
        this.last       = last;
        this.sourceList = sourceList;
    }

    // ---------------------------------------------------------- Public methods

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return this.uri;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getLast() {
        return this.last;
    }

    public void setSourceList(String sourceList) {
        this.sourceList = sourceList;
    }

    public String getSourceList() {
        return this.sourceList;
    }

    // -------------------------------------------------------- Public methods
    public abstract void recovery(Principal principal) throws SyncSourceException;

    public SyncItem[] getAllSyncItems(Principal principal) throws SyncSourceException {
        return performRecovery(principal);
    }

    public SyncItem[] getNewSyncItems(Principal principal, Timestamp since)
    throws SyncSourceException {
        return performRecovery(principal);
    }

    public SyncItem[] getDeletedSyncItems(Principal principal, Timestamp since)
    throws SyncSourceException {
        return new SyncItem[0];
    }

    public SyncItem[] getUpdatedSyncItems(Principal principal, Timestamp since)
    throws SyncSourceException {
        return new SyncItem[0];
    }

    public SyncItemKey[] getDeletedSyncItemKeys(Principal principal, Timestamp since)
    throws SyncSourceException {
        return new SyncItemKey[0];
    }

    public SyncItemKey[] getNewSyncItemKeys(Principal principal, Timestamp since)
    throws SyncSourceException {
        return new SyncItemKey[0];
    }

    public SyncItem getSyncItemFromId(Principal principal, SyncItemKey syncItemKey)
    throws SyncSourceException {
        return null;
    }

    public SyncItem[] getSyncItemsFromIds(Principal principal, SyncItemKey[] syncItemKeys)
    throws SyncSourceException {
        return new SyncItem[0];
    }

    public void removeSyncItem(Principal principal, SyncItem syncItem)
    throws SyncSourceException {}

    public void removeSyncItems(Principal principal, SyncItem[] syncItems)
    throws SyncSourceException {}

    public SyncItem setSyncItem(Principal principal, SyncItem syncInstance)
    throws SyncSourceException {
        return syncInstance;
    }

    public SyncItem[] setSyncItems(Principal principal, SyncItem[] syncItems)
    throws SyncSourceException {
        return syncItems;
    }

    public SyncItem getSyncItemFromTwin(Principal principal, SyncItem syncItem)
    throws SyncSourceException {
        return syncItem;
    }

    public SyncItem[] getSyncItemsFromTwins(Principal principal, SyncItem[] syncItems)
    throws SyncSourceException {
        return syncItems;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Perform recovery of information:
     * - reset client mapping for the principal and source's list,
     * - set last sync with the timestamp of the creation of the datastore,
     * - create the SyncItem with the binary_content in xml format.
     *
     * @param p Principal
     * @return SyncItem[] array with only one SyncItem
     */
    private SyncItem[] performRecovery(Principal p) throws SyncSourceException {
        ArrayList ret = new ArrayList();

        deleteMapping(p);

        recovery(p);

        StringTokenizer stUp = new StringTokenizer(sourceList,SOURCE_SEPARATOR);
        while (stUp.hasMoreElements()) {
            String source = (String)stUp.nextElement();
            setLastSync(p, source);
        }


        SyncItem syncItem = new SyncItemImpl(this, KEY_ITEM);
        StringBuffer obj = new StringBuffer("(recovery-details)");
        obj.append("(source-list)").append(this.sourceList).append("(/source-list)");
        obj.append("(last)").append(this.last).append("(/last)");
        obj.append("(uri)").append(this.uri).append("(/uri)");
        obj.append("(/recovery-details)");

        syncItem.setContent(obj.toString().getBytes());
        syncItem.setState(SyncItemState.UPDATED);

        ret.add(syncItem);

        return (SyncItem[])ret.toArray(new SyncItem[1]);
    }

    /**
     * Reset client mapping and last sync
     * @param p Principal
     * @param uri source uri
     */
    private void deleteMapping(Principal p) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Sync4jPrincipal sp = (Sync4jPrincipal)p;

            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(DATA_SOURCE_NAME);

            conn = ds.getConnection();

            stmt = conn.prepareStatement(SQL_DELETE_CLIENT_MAPPING);
            stmt.setLong(1, sp.getId());

            int n = stmt.executeUpdate();

            stmt.close();

            stmt = conn.prepareStatement(SQL_DELETE_LAST_SYNC);
            stmt.setLong(1, sp.getId());

            n = stmt.executeUpdate();

            stmt.close();
            conn.close();

        } catch (Exception e) {
            log.error("Error deleting the mapping", e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

    private void setLastSync(Principal p, String uri) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            Sync4jPrincipal sp = (Sync4jPrincipal)p;

            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup(DATA_SOURCE_NAME);

            conn = ds.getConnection();

            stmt = conn.prepareStatement(SQL_UPDATE_LAST_SYNC);
            stmt.setString(1, last);
            stmt.setTimestamp(2, new java.sql.Timestamp(Long.parseLong(last)));
            stmt.setTimestamp(3, new java.sql.Timestamp(Long.parseLong(last)));
            stmt.setLong(4, sp.getId());
            stmt.setString(5, uri);

            int n = stmt.executeUpdate();

            if (n == 0) {
                //
                // The first time!!!
                //
                stmt.close();

                stmt = conn.prepareStatement(SQL_INSERT_LAST_SYNC);

                stmt.setLong(1, sp.getId());
                stmt.setString(2, uri);
                stmt.setString(3, last);
                stmt.setTimestamp(4, new java.sql.Timestamp(Long.parseLong(last)));
                stmt.setTimestamp(5, new java.sql.Timestamp(Long.parseLong(last)));

                stmt.executeUpdate();
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            log.error("Error setting last sync", e);
        } finally {
            DBTools.close(conn, stmt, null);
        }
    }

}
