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
package com.funambol.framework.engine;

import java.security.Principal;
import java.sql.Timestamp;

import com.funambol.framework.engine.source.SyncSource;
import com.funambol.framework.engine.SyncException;
import com.funambol.framework.engine.SyncOperationStatus;
import com.funambol.framework.server.ClientMapping;

/**
 * It defines the interface of a Synchronization Strategy object. <br>
 * It implements the <i>Strategy</i> partecipant of the Strategy Pattern.
 * <p>
 * It is usually called by the synchronization engine when a syncronization
 * action has to be performed.
 * </p>
 * There are two types of synchronization process: slow and fast.
 * <h2>Slow synchronization</h2>
 * A slow synchronization is when the sources to be synchronized must be fully
 * compared in order to reconstruct the right images of the data on both
 * sources. The way the sets of items are compared is implementation specific
 * and can vary from comparing just the key or the entire content of a SyncItem.
 * In fact, in order to decide if two sync items are exactly the same or some
 * filed has changed, all fields might riquire a comparison.<br>
 * A slow sync is prepared calling <i>prepareSlowSync(...)</i>
 *
 * <h2>Fast synchronization</h2>
 * In the case of fast synchronization, the sources are queried only for new,
 * deleted or updated items since a given point in time. In this case the status
 * of the items can be checked in order to decide when a deeper comparison is
 * required.<br>
 * A fast sync is prepared calling <i>prepareFastSync(...)</i>
 *
 * <h2>Synchronization principal</h2>
 * <i>prepareXXXSync()</i> requires an additional <i>java.security.Principal</i>
 * parameter in input. The meaning of this parameter is implementation specific,
 * but as general rule, it is used to operate on the data specific for a given
 * entity such as a user, an application, a device, ecc.
 *
 * Moreover, in order to support new SyncML 1.2, a new parameter
 * <i>com.funambol.framework.filter.Filter</i> is required. This parameter contains
 * the filter to use in the sync process. If <code>null</code> not filters are
 * speficied.
 *
 * @see com.funambol.framework.engine.source.SyncSource
 * @see com.funambol.framework.engine.SyncEngine
 *
 *
 * @version $Id: SyncStrategy.java,v 1.2 2007-11-28 11:15:37 nichele Exp $
 */
public interface SyncStrategy {

    // --------------------------------------------------------------- Constants
    public static final int SYNC_WITHOUT_FILTER        = 0;
    public static final int SYNC_WITH_EXCLUSIVE_FILTER = 1;
    public static final int SYNC_WITH_INCLUSIVE_FILTER = 2;

    public static final int CONFLICT_RESOLUTION_DEFAULT     = -1;
    public static final int CONFLICT_RESOLUTION_SERVER_WINS = 0;
    public static final int CONFLICT_RESOLUTION_CLIENT_WINS = 1;
    public static final int CONFLICT_RESOLUTION_MERGE_DATA  = 2;

    // ---------------------------------------------------------- Public Methods
    /**
     * Fired when a synchronization action must be performed
     *
     * @param sources the syncsources to sync
     * @param slowSync is this sync a slow ?
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param syncOperations the synchronization operations
     * @param syncFilterType the type of the filter used
     */
    SyncOperationStatus[] sync(SyncSource[]    sources,
                               boolean         slowSync,
                               ClientMapping   mapping,
                               String          lastAnchor,
                               SyncOperation[] syncOperations,
                               int             syncFilterType) throws SyncException ;

    /**
     * Fired when a slow synchronization action must be prepared.
     *
     * @param sources the sources to be synchronized
     * @param nextSync timestamp of the beginning of the current synchronization
     * @param principal the entity for which the synchronization is required
     * @param mapping the client mapping
     * @param syncFilterType the type of the filter used
     * @param last is this the last call to prepareSlowSync ?
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     */
    SyncOperation[] prepareSlowSync(SyncSource[] sources       ,
                                    Principal    principal     ,
                                    ClientMapping mapping      ,
                                    int          syncFilterType,
                                    Timestamp    nextSync      ,
                                    boolean      last          )
    throws SyncException;

    /**
     * Fired when a fast synchronization action must be prepared.
     *
     * @param sources the sources to be synchronized
     * @param lastSync timestamp of the last synchronization
     * @param nextSync timestamp of the current synchronization
     * @param principal the entity for which the synchronization is required
     * @param mapping the client mapping
     * @param syncFilterType the type of the filter used
     * @param since look for data earlier than this timestamp
     * @param lastAnchor the last anchor to use in the mapping handling. This is
     *        required in order to check the item already sent with the same
     *        lastAnchor (Suspende and Resume).
     * @param last is this the last call to prepareFastSync ?
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     */
    SyncOperation[] prepareFastSync(SyncSource[]  sources       ,
                                    Principal     principal     ,
                                    ClientMapping mapping       ,
                                    int           syncFilterType,
                                    Timestamp     lastSync      ,
                                    Timestamp     nextSync      ,
                                    String        lastAnchor    ,
                                    boolean       last          )
    throws SyncException;


    
    /**
     * Fired when a Smart-One-Way-From-Client synchronization action must be
     * prepared.
     *
     * @param sources the sources to be synchronized
     * @param principal the entity for which the synchronization is required
     * @param mapping the client mapping
     * @param syncFilterType the type of the filter used
     * @param nextSync timestamp of the current synchronization
     * @param last spcify if this is the last call to prepareFastSync.
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     * @throws SyncException
     */
  public SyncOperation[] prepareSmartOneWayFromClient(
          SyncSource[] sources,
          Principal principal,
          ClientMapping mapping,
          int syncFilterType,
          Timestamp nextSync,
          boolean last)
          throws SyncException;

    /**
     * Fired when a synchronization action must be finished.
     */
    void endSync() throws SyncException ;

    /**
     * Return the conflict resolution for the given SyncSource
     * @param source the SyncSource
     * @return the configured conflict resolution. One between:
     * <ui>
     * <li><code>CONFLICT_RESOLUTION_SERVER_WINS</code></li>
     * <li><code>CONFLICT_RESOLUTION_CLIENT_WINS</code></li>
     * <li><code>CONFLICT_RESOLUTION_MERGE_DATA</code></li>
     * </ui>
     */
    public int getConflictResolution(SyncSource source);
}
