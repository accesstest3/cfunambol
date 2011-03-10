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
package com.funambol.server.engine;

import java.security.Principal;
import java.sql.Timestamp;
import java.util.*;
import java.util.Map;

import com.funambol.framework.core.*;
import com.funambol.framework.engine.*;
import com.funambol.framework.engine.Util;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.ClientMappingEntry;

import org.apache.commons.collections.ListUtils;

/**
 * This class represents a synchronization process.
 *
 * The base concrete implementation of a synchronization strategy. It implements
 * the <i>SyncStrategy</i> partecipant of the Strategy Pattern.
 * <p>
 * The synchronization process is implemented in this class as follows:
 * <p>
 * Given a set of sources A, B, C, D, etc, the synchronization process takes place
 * between two sources at a time: A is first synchronzed with B, then AB with
 * C, then ABC with D and so on. <br>
 * The synchronization process is divided in three phases: preparation, synchronization,
 * finalization.
 * <br>
 * prepareSync returns an array of SyncOperation, in which each element represents
 * a particular synchronization action, ie. create an item in the source A,
 * delete the item X from the source B, etc. Sometime it is not possible decide
 * what to do, thus a SyncConflict operation is used. A conflict must be solved
 * by something external the synchronization process, for instance by a user
 * action. Below is a table of all possible situations.
 * <pre>
 *
 * | -------- | --- | --- | --- | --- | --- |
 * | Source A |     |     |     |     |     |
 * |    /     |  N  |  D  |  U  |  S  |  X  |     N : item new
 * | Source B |     |     |     |     |     |     D : item deleted
 * | -------- | --- | --- | --- | --- | --- |     U : item updated
 * |       N  |  O  |  O  |  O  |  O  |  B  |     S : item synchronized/unchanged
 * | -------- | --- | --- | --- | --- | --- |     X : item not existing
 * |       D  |  O  |  X  |  O  |  X  |  X  |     O : conflict
 * | -------- | --- | --- | --- | --- | --- |     A : item A replaces item B
 * |       U  |  O  |  O  |  O  |  B  |  B  |     B : item B replaces item A
 * | -------- | --- | --- | --- | --- | --- |
 * |       S  |  O  |  X  |  A  |  =  |  B  |
 * | -------- | --- | --- | --- | --- | --- |
 * |       X  |  A  |  X  |  A  |  A  |  X  |
 * | -------- | --- | --- | --- | --- | --- |
 *
 * </pre>
 * When a conflict is detected, it's resolved using one between this methods:
 * <ui>
 * <li><code>CONFLICT_RESOLUTION_SERVER_WINS</code></li>
 * <li><code>CONFLICT_RESOLUTION_CLIENT_WINS</code></li>
 * <li><code>CONFLICT_RESOLUTION_MERGE_DATA</code>: this is applicable only with a
 * <code>MergeableSyncSource</code></li>
 * </ui>.
 * <br/>
 * If a syncsource is a <code>ClientWinsSyncSource</code> the conflict
 * resolution used is <code>CONFLICT_RESOLUTION_CLIENT_WINS</code>.
 * <br/>
 * If a syncsource is a <code>ServerWinsSyncSource</code> the conflict resolution
 * used is <code>CONFLICT_RESOLUTION_SERVER_WINS</code>.
 * <br/>
 * If a syncsource is a <code>MergeableSyncSource</code> the conflict resolution
 * used is <code>CONFLICT_RESOLUTION_MERGE_DATA</code>.
 * <p/>
 * In the other cases, it's possible to set a different conflict resolution setting
 * the property <code>sourceUriConflictsResolution</code>. This object contains
 * for each syncsource the conflict resolution to use. If for a syncSource, no
 * conflict resolution is set, the default value (<code>defaultConflictResolution</code>)
 * is used. By default, <code>defaultConflictResolution</code> is
 * <code>CONFLICT_RESOLUTION_CLIENT_WINS</code>.
 *
 * @version  $Id: Sync4jStrategy.java,v 1.2 2008-06-18 08:23:43 nichele Exp $
 */
public class Sync4jStrategy implements SyncStrategy, java.io.Serializable {

    // --------------------------------------------------------------- Constants

    public static final String LOG_NAME = "funambol.engine.strategy";

    protected transient static FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

    // -------------------------------------------------------------- Properties

    /**
     * Contains a map of source uri and the resolution to use if a conflict is detected
     */
    protected Map sourceUriConflictsResolution = null;

    public void setSourceUriConflictsResolution(Map conflictsResolution) {
        this.sourceUriConflictsResolution = conflictsResolution;
    }

    /**
     *  Gets the maps with the resolution defined in Sync4jStrategy
     */
    public Map getSourceUriConflictsResolution() {
        return this.sourceUriConflictsResolution;
    }

    /** The default conflict resolution  */
    protected int defaultConflictResolution = CONFLICT_RESOLUTION_SERVER_WINS;

    /**
     * Sets the default conflict resolution.
     * @param conflictResolution the resolution to set as default
     */
    public void setDefaultConflictResolution(int conflictResolution) {
        this.defaultConflictResolution = conflictResolution;
    }

    /**
     * Gets the value of the default conflict resolution
     * @return the value of the default conflict resolution
     */
    public int getDefaultConflictResolution() {
        return defaultConflictResolution;
    }

    /**
     * The process' name
     */
    public String getName() {
        return name;
    }

    private String name;
    public void setName(String name) {
        this.name = name;
    }

    // ------------------------------------------------------------ Private data

    //
    // NOTE: server modification MUST be detected and stored at the beginning of
    // the sync process (i.e. in the first modification message), otherwise in
    // the case of multi message, likely the server will detect items modified
    // by the current sync as server modifications.
    // Reading server modifications at the synchronization start does not
    // loose items since. Let's suppose we have the following events axis:
    //
    //  s1            e1    s2              e2             s3          e3
    //  |-------------|....|---------------|...............|-----------|
    //
    // where s<x> is the beginning of a sync and e<x> represents its end.
    // In s2 we collect all modified items since s1; if any other process
    // modifies an item after s2, it won't be detected during the second
    // synchronization. However, it will be detected at s3, where we collect all
    // modified items since s2.
    //
    protected Map newBAll;
    protected Map updatedBAll;
    protected Map deletedBAll;
    protected Map BmAll; //used into Slow Sync

    /**
     * Collects statistics like the number of added/updated/deleted items
     */
    protected Map<String, SyncStatistic> syncStatistics;
    public SyncStatistic getSyncStatistics(String uri) {
        return syncStatistics.get(uri);
    }

    // ------------------------------------------------------------ Constructors

    public Sync4jStrategy() {
        newBAll     = new HashMap();
        updatedBAll = new HashMap();
        deletedBAll = new HashMap();
        BmAll       = new HashMap();
        sourceUriConflictsResolution = new HashMap();
        syncStatistics = new HashMap();
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Preparation for the synchronization. If <i>sources</i> is not null,
     * the preparation works on the given sources. Otherwise it works on the
     * sources as they were set in the constructor.
     * Note that in the case of slow sync, the sync analysis to determine the
     * new items on the server that must be returned to the client, we need to
     * wait for the last call to this method. This is used for protocols that
     * supports a multi message session. At the contrary, client side
     * modifications can be processed immediately.
     *
     *
     * @param sources the sources to be synchronized
     * @param nextSync the timestamp of the beginning of this synchronization
     * @param principal the entity for which the synchronization is required
     * @param clientMapping the current mapping
     * @param syncFilterType the type of the filter used
     * @param last is this the last call to prepareSlowSync ?
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     *
     * @throws SyncException
     * @see com.funambol.framework.engine.SyncStrategy
     */
    public SyncOperation[] prepareSlowSync(SyncSource[]  sources,
                                           Principal     principal,
                                           ClientMapping clientMapping,
                                           int           syncFilterType,
                                           Timestamp     nextSync,
                                           boolean       last) throws SyncException {

        SyncItem[] Am, Bm;
        List syncOperations = new ArrayList();

        FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

        if (log.isInfoEnabled()) {
            log.info("Preparing slow synchronization");
        }

        if (log.isTraceEnabled()) {
            if (last) {
                log.trace("Last message in the package");
            } else {
                log.trace("Not the last message in the package");
            }
        }

        //
        // First process the items provided by the client
        //
        Am = ( (MemorySyncSource)sources[1]).getUpdatedSyncItems();

        String uri = sources[0].getSourceURI();
        if (BmAll.get(uri) == null) {
            SyncItemKey[] allKeys = sources[0].getAllSyncItemKeys();

            if (log.isTraceEnabled()) {
                log.trace("allItemsKeys: " + Util.arrayToString(allKeys));
            }

            Bm = EngineHelper.createSyncItems(allKeys,
                                              SyncItemState.NEW,
                                              sources[0],
                                              null,
                                              null);
            BmAll.put(uri, Bm);
        } else {
            Bm = (SyncItem[])BmAll.get(uri);
        }

        //
        // Just in case...
        //
        if (Am == null) {
            Am = new SyncItem[0];
        }

        if (Bm == null) {
            Bm = new SyncItem[0];
        }

        //
        // If any item from the client has not a corresponding mapping, the
        // server source must be queried for the item, since the client item
        // could be the same of an existing server item. In this case, the old
        // unmapped item is replaced in Map by the newly mapped item.
        //
        List newlyMappedItems = new ArrayList();


        //
        // The validMapping is used by the fixMappedItems to know the mapping. It has
        // to know that because in the twins search we need to handle the parentKey
        // and moreover, knowing the mapping, the fixMappedItems is able to ignore
        // the twins already mapped. We can't use the object mapping (of type ClientMapping)
        // because the fixMappedItems has to handle also the mapping created by itself
        //
        Map validMapping = null;
        if (clientMapping != null) {
            validMapping = clientMapping.getValidMapping();
        } else {
            validMapping = new HashMap();
        }

        fixMappedItems(newlyMappedItems,
                       Am,
                       sources[0],
                       validMapping, // in a slow sync, we haven't valid mapping
                       principal);

        //
        // Because it is a slow sync, items state must be reset to N
        //
        EngineHelper.setState(Am, SyncItemState.NEW);
        EngineHelper.setState(Bm, SyncItemState.NEW);

        for (int i = 0; i < Am.length; ++i) {
            if (! ( (AbstractSyncItem)Am[i]).isMapped()) {
                syncOperations.add(checkSyncOperation(principal, nextSync, Am[i], null));
            }
        }

        Iterator        j            = newlyMappedItems.iterator();
        SyncItemMapping mapping      = null;

        while (j.hasNext()) {
            mapping     = (SyncItemMapping)j.next();

            syncOperations.add(checkSyncOperation(principal,
                                                  nextSync,
                                                  mapping.getSyncItemA(),
                                                  mapping.getSyncItemB()));
        }

        //
        // We have to remove from Bm the items already used in some operations because if
        // we have an item there, it's already handled
        //
        Bm = EngineHelper.removeItemsInOperations(Bm, syncOperations, false);
        BmAll.put(uri, Bm);

        //
        // If it is the last call, we have also to consider what is in source
        // B but not in A
        //
        if (last) {

            for (int i = 0; i < Bm.length; ++i) {

                syncOperations.add(checkSyncOperation(principal,
                                                      nextSync, null,
                                                      Bm[i]));

            }

        }

        if (log.isTraceEnabled()) {
            log.trace("operations: " + syncOperations);
        }

        if (log.isInfoEnabled()) {
            if (last) {
                log.info("Preparation completed (last message in the package)");
            } else {
                log.info("Preparation completed (not last message in the package)");
            }
        }

        return (SyncOperation[])syncOperations.toArray(new SyncOperationImpl[] {});
    }

    /**
     * Preparation for fast synchronization. If <i>sources</i> is not null,
     * the preparation operates on the given sources. Otherwise it works on the
     * sources as they were set in the constructor.
     * <p>
     * Refer to the architecture document for details about the algoritm applied.
     *
     * @param sources the sources to be synchronized
     * @param principal the entity for which the synchronization is required
     * @param mapping the current mapping
     * @param syncFilterType the type of the filter used
     * @param lastSync timestamp of the last synchronization
     * @param nextSync timestamp of the current synchronization
     * @param lastAnchor the last anchor to use in the mapping handling. This is
     *        required in order to check the item already sent with the same
     *         lastAnchor (Suspende and Resume).
     * @param last is this the last call to prepareFastSync ?
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     *
     * @see com.funambol.framework.engine.SyncStrategy
     */
    public SyncOperation[] prepareFastSync(SyncSource[]  sources,
                                           Principal     principal,
                                           ClientMapping mapping,
                                           int           syncFilterType,
                                           Timestamp     lastSync,
                                           Timestamp     nextSync,
                                           String        lastAnchor,
                                           boolean       last) throws SyncException {

        // ---------------------------------------------------------------------

        List Am,    // items modified in A
             Bm,    // items modified in B
             AmBm,  // Am intersect Bm
             AAmBm, // items unmodified in A, but modified in B
             AmBBm; // items unmodified in B, but modified in A

        List syncOperations = null;

        SyncItem[] newA = null, newB = null,
                   updatedA = null, updatedB = null,
                   deletedA = null, deletedB = null;

        if (log.isInfoEnabled()) {
            log.info("Preparing fast synchronization since " + lastSync);
        }

        if (log.isTraceEnabled()) {
            if (last) {
                log.trace("Last message in the package");
            } else {
                log.trace("Not the last message in the package");
            }
        }

        // ---------------------------------------------------------------------

        //
        // NOTE: simplified version - only two sources, the second one of which
        // is the client
        //
        newA     = ( (MemorySyncSource)sources[1]).getNewSyncItems();
        updatedA = ( (MemorySyncSource)sources[1]).getUpdatedSyncItems();
        deletedA = ( (MemorySyncSource)sources[1]).getDeletedSyncItems();

        if (log.isTraceEnabled()) {
            log.trace("newA: " + Util.arrayToString(newA));
            log.trace("updatedA: " + Util.arrayToString(updatedA));
            log.trace("deletedA: " + Util.arrayToString(deletedA));
        }

        //
        // If there are some updated items not mapped, we have to handle them
        // as New item.
        // If there are some new items mapped, we have to handle them
        // as Updated item
        //
        EngineHelper.checkItemsState(newA);
        EngineHelper.checkItemsState(updatedA);

        String uri = sources[0].getSourceURI();
        if (newBAll.get(uri) == null) {

            if (log.isTraceEnabled()) {
                log.trace("Detecting server changes...");
            }

            SyncItemKey[] newBItemKeys =
                sources[0].getNewSyncItemKeys(lastSync, nextSync);
            SyncItemKey[] updateBItemKeys =
                sources[0].getUpdatedSyncItemKeys(lastSync, nextSync);
            SyncItemKey[] deleteBItemKeys =
                sources[0].getDeletedSyncItemKeys(lastSync, nextSync);

            if (newBItemKeys == null) {
                newBItemKeys = new SyncItemKey[0];
            }
            if (updateBItemKeys == null) {
                updateBItemKeys = new SyncItemKey[0];
            }
            if (deleteBItemKeys == null) {
                deleteBItemKeys = new SyncItemKey[0];
            }

            if (log.isTraceEnabled()) {
                log.trace("newBItemKeys: "     + Util.arrayToString(newBItemKeys));
                log.trace("updateBItemKeys: "  + Util.arrayToString(updateBItemKeys));
                log.trace("deleteBItemKeys: "  + Util.arrayToString(deleteBItemKeys));
            }

            newB = EngineHelper.createSyncItems(
                newBItemKeys,
                SyncItemState.NEW,
                sources[0],
                mapping,
                lastAnchor);

            updatedB = EngineHelper.createSyncItems(
                updateBItemKeys,
                SyncItemState.UPDATED,
                sources[0],
                mapping,
                lastAnchor);

            deletedB = EngineHelper.createSyncItems(
                deleteBItemKeys,
                SyncItemState.DELETED,
                sources[0],
                mapping,
                lastAnchor);

            //
            // The SyncSource should return empty arrays... but just in case...
            //
            if (newB == null) {
                newB = new InMemorySyncItem[0];
            }
            if (updatedB == null) {
                updatedB = new InMemorySyncItem[0];
            }
            if (deletedB == null) {
                deletedB = new InMemorySyncItem[0];
            }

            if (log.isTraceEnabled()) {
                log.trace("newB: "     + Util.arrayToString(newB));
                log.trace("updatedB: " + Util.arrayToString(updatedB));
                log.trace("deletedB: " + Util.arrayToString(deletedB));
            }

            newBAll.put    (uri, newB    );
            updatedBAll.put(uri, updatedB);
            deletedBAll.put(uri, deletedB);
        } else {
            newB     = (SyncItem[])newBAll.get(uri);
            updatedB = (SyncItem[])updatedBAll.get(uri);
            deletedB = (SyncItem[])deletedBAll.get(uri);
        }

        //
        // If any item from the client has not a corresponding mapping, the
        // server source must be queried for the item, since the client item
        // could be the same of an existing server item. In this case,  the old
        // unmapped item is replaced by the newly mapped item.
        //
        List newlyMappedItems = new ArrayList();

        //
        // The validMapping is used by the fixMappedItems to know the mapping. It has
        // to know that because in the twins search we need to handle the parentKey
        // and moreover, knowing the mapping, the fixMappedItems is able to ignore
        // the twins already mapped. We can't use the object mapping (of type ClientMapping)
        // because the fixMappedItems has to handle also the mapping created by itself
        //
        // prepareFastSync doesn't use the validMapping.
        //
        Map validMapping = null;
        if (mapping != null) {
            validMapping = mapping.getValidMapping();
        } else {
            validMapping = new HashMap();
        }

        fixMappedItems(newlyMappedItems, newA,     sources[0], validMapping, principal);
        fixMappedItems(newlyMappedItems, updatedA, sources[0], validMapping, principal);
        fixMappedItems(newlyMappedItems, deletedA, sources[0], validMapping, principal);

        if (log.isTraceEnabled()) {
            log.trace("Newly mapped items: " + newlyMappedItems);
        }

        Am = new ArrayList();
        Bm = new ArrayList();
        Am.addAll(Arrays.asList(newA));
        Am.addAll(Arrays.asList(updatedA));
        Am.addAll(Arrays.asList(deletedA));
        Bm.addAll(Arrays.asList(newB));
        Bm.addAll(Arrays.asList(updatedB));
        Bm.addAll(Arrays.asList(deletedB));

        if (log.isTraceEnabled()) {
            log.trace("Am: " + Am);
            log.trace("Bm: " + Bm);
            log.trace("Am-Bm: " + ListUtils.subtract(Am, Bm));
            log.trace("Bm-Am: " + ListUtils.subtract(Bm, Am));
        }

        //
        // Now calculate subsets: AmBm, AmBBm, AAmBm.
        // Note that Bm and AAmBm must be calculated only when we got the
        // last message. For the others, it must be empty.
        //
        AmBm = EngineHelper.intersect(Am, Bm);

        //
        // We have to remove from AmBm the newlyMappedItems and then re-add them.
        // This is done because otherwise we can have the same mapping in AmBm and
        // in newlyMappedItems. However, we have to keep the mapping in the
        // newlyMappedItems replacing the one in the AmBm
        // because here we could have a merged item.
        //
        // Example: Conflict NEW-NEW: the server has to merge the items but in Bm
        // we have the item with the original data (before the merge operation)
        //
        //
        // The remove is based on SyncItemMapping.equals method.
        //
        AmBm.removeAll(newlyMappedItems);

        AmBm.addAll(newlyMappedItems);

        AmBBm = EngineHelper.buildAmBBm(ListUtils.subtract(Am, Bm), sources[0], principal);

        //
        // When a conflict is detected finding a twin, AmBm contains the mapping
        // with client item and server item. The server item is set with state 'U'
        // because otherwise we don't handle this situation as a conflict
        // (see fixMappedItems) also if indeed the server item is not updated (it
        // isn't in Bm).
        // But in this way, we have a wrong mapping in AmBBm because in Am with have
        // the client item with state 'U' and the server item isn't changed (it isn't
        // in Bm so the method buildAmBBm considers it as 'S').
        // So, from AmBBM we remove the mapping already in AmBm
        //
        AmBBm.removeAll(AmBm);

        AAmBm = new ArrayList();
        if (last) {

            //
            // If the server source is a FilterableSyncSource, we have to check
            // if there are some items outside the filter criteria to delete on
            // the client (if the filter is exclusive) or if there are some
            // server item to add on the client (maybe because the filter is changed)
            // For example, if the client syncs just the contacts with CompanyName
            // equals to "MyCompany" and then it changes the filter to CompanyName
            // equals to "MyCompany" or "YourCompany", maybe the server has to add
            // some items.
            //
            if (sources[0] instanceof FilterableSyncSource) {
                SyncItemKey[] allItemsKeys = sources[0].getAllSyncItemKeys();

                if (log.isTraceEnabled()) {
                    log.trace("allItemsKeys: " + Util.arrayToString(allItemsKeys));
                }

                checkForItemsToDelete(sources, syncFilterType, allItemsKeys, mapping, Bm);

                checkForItemsToAdd(sources, allItemsKeys, mapping, Bm);
            }

            AAmBm = EngineHelper.buildAAmBm(ListUtils.subtract(Bm, Am), sources[1], principal);
        } else {
            Bm.clear();
        }

        if (log.isTraceEnabled()) {
            log.trace("AmBm:  " + AmBm);
            log.trace("AmBBm: " + AmBBm);
            log.trace("AAmBm: " + AAmBm);
        }

        //
        // Ready for conflict detection!
        //
        syncOperations =
            checkSyncOperations(principal, nextSync, Am, Bm, AmBm, AmBBm, AAmBm);

        if (log.isTraceEnabled()) {
            log.trace("operations: " + syncOperations);
        }

        //
        // We have to remove from xxxB the items in newlyMappedItems because if
        // we have an item here, it's already handled
        //
        newB     = EngineHelper.removeItemsInOperations(newB,     syncOperations, false);
        updatedB = EngineHelper.removeItemsInOperations(updatedB, syncOperations, false);
        deletedB = EngineHelper.removeItemsInOperations(deletedB, syncOperations, false);

        newBAll.put    (uri, newB    );
        updatedBAll.put(uri, updatedB);
        deletedBAll.put(uri, deletedB);

        if (log.isInfoEnabled()) {
            if (last) {
                log.info("Preparation completed (last message in the package)");
            } else {
                log.info("Preparation completed (not last message in the package)");
            }
        }

        return (SyncOperation[])syncOperations.toArray(new SyncOperationImpl[] {});
    }


    /**
     * Preparation for Smart-One-Way-From-Client synchronization.
     * <p>
     * Refer to the architecture document for details about the algoritm applied.
     *
     * @param sources the sources to be synchronized
     * @param principal the entity for which the synchronization is required
     * @param mapping the current mapping
     * @param syncFilterType the type of the filter used
     * @param nextSync timestamp of the current synchronization
     * @param last is this the last call to prepareFastSync ?
     *
     * @return an array of SyncOperation, one for each SyncItem that must be
     *         created/updated/deleted or in conflict.
     *
     * @see com.funambol.framework.engine.SyncStrategy
     */
    public SyncOperation[] prepareSmartOneWayFromClient(
            SyncSource[] sources,
            Principal principal,
            ClientMapping mapping,
            int syncFilterType,
            Timestamp nextSync,
            boolean last)
            throws SyncException {

        // ---------------------------------------------------------------------
        MemorySyncSource clientSynSource = (MemorySyncSource) sources[1];
        SyncSource serverSyncSource = sources[0];

        if (log.isInfoEnabled()) {
            log.info("Preparing smart one way from client synchronization");
        }

        if (log.isTraceEnabled()) {
            if (last) {
                log.trace("Last message in the package");
        } else {
                log.trace("Not the last message in the package");
            }
        }

        // ---------------------------------------------------------------------

        // getClientNewItems()
        SyncItem[] newA     = clientSynSource.getNewSyncItems();
        if (log.isTraceEnabled()) {
            log.trace("newA: " + Util.arrayToString(newA));
        }
        // If there are some new items mapped, we have to handle them
        // as Updated item
        EngineHelper.checkItemsState(newA);


        // getClientUpdatedItems()
        SyncItem[] updatedA = clientSynSource.getUpdatedSyncItems();
        if (log.isTraceEnabled()) {
            log.trace("updatedA: " + Util.arrayToString(updatedA));
        }
        // If there are some updated items not mapped, we have to handle them
        // as New item.
        EngineHelper.checkItemsState(updatedA);


        // getClientDeletedItems()
        SyncItem[] deletedA = clientSynSource.getDeletedSyncItems();
        if (log.isTraceEnabled()) {
            log.trace("deletedA: " + Util.arrayToString(deletedA));
        }


        String uri = serverSyncSource.getSourceURI();


        if (newBAll.get(uri) == null) {
            if (log.isTraceEnabled()) {
                log.trace("Detecting server changes...");
            }
        }

        // get Server New Items
        SyncItem[] newB = null;
        if (newBAll.get(uri) == null) {
            newB = new InMemorySyncItem[0];
            newBAll.put(uri, newB);
        } else {
            newB = (SyncItem[]) newBAll.get(uri);
        }


        // get Server Updated Items
        SyncItem[] updatedB = null;
        if (updatedBAll.get(uri) == null) {
            updatedB = new InMemorySyncItem[0];
            updatedBAll.put(uri, updatedB);
        } else {
            updatedB = (SyncItem[]) updatedBAll.get(uri);
        }


        // get Server Deleted Items
        SyncItem[] deletedB = null;
        if (deletedBAll.get(uri) == null) {
            deletedB = new InMemorySyncItem[0];
            deletedBAll.put(uri, deletedB);
        } else {
            deletedB = (SyncItem[]) deletedBAll.get(uri);
        }

        //
        // calculate Am
        //
        List Am = new ArrayList();
        Am.addAll(Arrays.asList(newA));
        Am.addAll(Arrays.asList(updatedA));
        Am.addAll(Arrays.asList(deletedA));
        if (log.isTraceEnabled()) {
            log.trace("Am: " + Am);
        }

        //
        // calculate Bm
        //
        List Bm = new ArrayList();

        //
        // Calculate AmBm
        //
        List AmBm;
        {
            AmBm = new ArrayList();
            //
            // If any item from the client has not a corresponding mapping, the
            // server source must be queried for the item, since the client item
            // could be the same of an existing server item. In this case,  the old
            // unmapped item is replaced by the newly mapped item.
            //
            // List of SyncItemMapping
            //
            List newlyMappedItems;
            {
                newlyMappedItems = new ArrayList();
                //
                // The validMapping is used by the fixMappedItems to know the mapping. It has
                // to know that because in the twins search we need to handle the parentKey
                // and moreover, knowing the mapping, the fixMappedItems is able to ignore
                // the twins already mapped. We can't use the object mapping (of type ClientMapping)
                // because the fixMappedItems has to handle also the mapping created by itself
                //
                Map validMapping = null;
                if (mapping != null) {
                    validMapping = mapping.getValidMapping();
                } else {
                    validMapping = new HashMap();
                }

                fixMappedItems(newlyMappedItems, newA, serverSyncSource, validMapping, principal);
                fixMappedItems(newlyMappedItems, updatedA, serverSyncSource, validMapping, principal);
                fixMappedItems(newlyMappedItems, deletedA, serverSyncSource, validMapping, principal);

                if (log.isTraceEnabled()) {
                    log.trace("Newly mapped items: " + newlyMappedItems);
                }
            }

            // All the newly mapped items are considered modified (are set to
            // UPDATED in fixMappedItems) and hince have to be added to AmBm
            //
            // We have to remove from AmBm the newlyMappedItems and then re-add them.
            // This is done because otherwise we can have the same mapping in AmBm and
            // in newlyMappedItems. However, we have to keep the mapping in the
            // newlyMappedItems replacing the one in the AmBm
            // because here we could have a merged item.
            //
            // Example: Conflict NEW-NEW: the server has to merge the items but in Bm
            // we have the item with the original data (before the merge operation)
            //
            //
            // The remove is based on SyncItemMapping.equals method.
            //
            AmBm.removeAll(newlyMappedItems);
            AmBm.addAll(newlyMappedItems);
            if (log.isTraceEnabled()) {
                log.trace("AmBm:  " + AmBm);
            }
        }

        //
        // calculate AmBBm
        // items modified in A (client) but not modified in B (server)
        //
        List AmBBm = EngineHelper.buildAmBBm(ListUtils.subtract(Am, Bm), serverSyncSource, principal);
        //
        // When a conflict is detected finding a twin, AmBm contains the mapping
        // with client item and server item. The server item is set with state 'U'
        // because otherwise we don't handle this situation as a conflict
        // (see fixMappedItems) also if indeed the server item is not updated (it
        // isn't in Bm).
        // But in this way, we have a wrong mapping in AmBBm because in Am with have
        // the client item with state 'U' and the server item isn't changed (it isn't
        // in Bm so the method buildAmBBm considers it as 'S').
        // So, from AmBBM we remove the mapping already in AmBm
        //
        AmBBm.removeAll(AmBm);
        if (log.isTraceEnabled()) {
            log.trace("AmBBm: " + AmBBm);
        }

        //
        // calculate AAmBm
        // items not modified in A (client) but modified in B (server)
        // Note. AAmBm must be calculated only when we got the
        // last message. For the other messages it must be empty.
        List AAmBm = new ArrayList();

        //
        // Ready for conflict detection!
        //
        List syncOperations =
            checkSyncOperations(principal, nextSync, Am, Bm, AmBm, AmBBm, AAmBm);

        if (log.isTraceEnabled()) {
            log.trace("operations: " + syncOperations);
        }

        //
        // We have to remove from xxxB the items in newlyMappedItems because if
        // we have an item here, it's already handled
        //
        newB     = EngineHelper.removeItemsInOperations(newB,     syncOperations, false);
        updatedB = EngineHelper.removeItemsInOperations(updatedB, syncOperations, false);
        deletedB = EngineHelper.removeItemsInOperations(deletedB, syncOperations, false);

        newBAll.put    (uri, newB    );
        updatedBAll.put(uri, updatedB);
        deletedBAll.put(uri, deletedB);

        if (log.isInfoEnabled()) {
            if (last) {
                log.info("Preparation completed (last message in the package)");
            } else {
                log.info("Preparation completed (not last message in the package)");
            }
        }

        return (SyncOperation[])syncOperations.toArray(new SyncOperationImpl[] {});
    }

    /**
     * Implements Synchronizable.sync
     */
    public SyncOperationStatus[] sync(SyncSource[]    sources,
                                      boolean         slowSync,
                                      ClientMapping   mapping,
                                      String          lastAnchor,
                                      SyncOperation[] syncOperations,
                                      int             syncFilterType) {

        if (log.isInfoEnabled()) {
            log.info("Synchronizing...");
        }

        if ( (syncOperations == null) || (syncOperations.length == 0)) {
            return new SyncOperationStatus[0];
        }

        List status = new ArrayList();

        SyncOperationStatus[] operationStatus = null;
        for (int i = 0; i < syncOperations.length; ++i) {
            if (log.isTraceEnabled()) {
                log.trace("Executing " + syncOperations[i]);
            }

            //
            // execSyncOperation can return more than one status for one
            // operation when more than one source are involved
            //
            operationStatus = execSyncOperation(sources,
                                                slowSync,
                                                mapping,
                                                lastAnchor,
                                                (SyncOperationImpl)syncOperations[i],
                                                syncFilterType);

            for (int j = 0; j < operationStatus.length; ++j) {
                status.add(operationStatus[j]);
                if (log.isTraceEnabled()) {
                    log.trace("status: " + operationStatus[j]);
                }
            } // next j
        } // next i


        return (SyncOperationStatus[])status.toArray(new SyncOperationStatus[0]);
    }

    /**
     * Implements Synchronizable.endSync
     */
    public void endSync() throws SyncException {
        // nothing to do
    }

    /**
     * Returns the conflict resolution to apply on the given source.
     * <p>
     * If the source is a <code>ServerWinsSyncSource</code>,
     * CONFLICT_RESOLUTION_SERVER_WINS is returned.
     * <br/>
     * If the source is a <code>ClientWinsSyncSource</code>,
     * CONFLICT_RESOLUTION_CLIENT_WINS is returned.
     * <br/>
     * If the source is a <code>MergeableSyncSource</code>,
     * CONFLICT_RESOLUTION_MERGE_DATA is returned.
     * <br/>
     * If the source is "just" a <code>SyncSource</code>, the configured conflict
     * resolution is returned. If the configured value is CONFLICT_RESOLUTION_MERGE_DATA,
     * the default conflict resolution is returned (CONFLICT_RESOLUTION_MERGE_DATA is
     * applicable jsut with MergeableSyncSource).
     * <br/>
     * <p>
     * @param sourceUri the source uri
     * @return the conflict resolution to apply.
     */
    public int getConflictResolution(SyncSource serverSource) {

        String sourceUri = serverSource.getSourceURI();

        if (serverSource instanceof MergeableSyncSource) {
            if (log.isTraceEnabled()) {
                log.trace("The syncsource '" + sourceUri +
                           "' is a MergeableSyncSource, so " +
                           getConflictResolutionDescription(CONFLICT_RESOLUTION_MERGE_DATA) +
                           " is used");
            }
            return CONFLICT_RESOLUTION_MERGE_DATA;
        } else if (serverSource instanceof ServerWinsSyncSource) {
            if (log.isTraceEnabled()) {
                log.trace("The syncsource '" + sourceUri +
                           "' is a ServerWinsSyncSource, so " +
                           getConflictResolutionDescription(CONFLICT_RESOLUTION_SERVER_WINS) +
                           " is used");
            }
            return CONFLICT_RESOLUTION_SERVER_WINS;
        } else if (serverSource instanceof ClientWinsSyncSource) {
            if (log.isTraceEnabled()) {
                log.trace("The syncsource '" + sourceUri +
                           "' is a ClientWinsSyncSource, so " +
                           getConflictResolutionDescription(CONFLICT_RESOLUTION_CLIENT_WINS) +
                           " is used");
            }
            return CONFLICT_RESOLUTION_CLIENT_WINS;
        }

        Integer conflictResolution =
                (Integer) sourceUriConflictsResolution.get(sourceUri);

        int value = -2; // we use -2 because -1 means DEFAULT_VALUE

        if (conflictResolution != null) {

            value = conflictResolution.intValue();

            if (value != CONFLICT_RESOLUTION_CLIENT_WINS &&
                value != CONFLICT_RESOLUTION_SERVER_WINS &&
                value != CONFLICT_RESOLUTION_MERGE_DATA  &&
                value != CONFLICT_RESOLUTION_DEFAULT      ) {

                value = getDefaultConflictResolution();

                if (log.isTraceEnabled()) {
                    log.trace("The configured value (" + value + ") is not valid, so the default value '" +
                               getConflictResolutionDescription(value) +
                               "' is used");
                }

                return value;

            } else if (value == CONFLICT_RESOLUTION_MERGE_DATA &&
                       !(serverSource instanceof MergeableSyncSource)) {

                value = getDefaultConflictResolution();

                if (log.isTraceEnabled()) {
                    log.trace("The " + getConflictResolutionDescription(value) +
                               " is applicable only with a MergeableSyncSource and '" +
                               sourceUri +
                               "' is not MergeableSyncSource. The default value " +
                               getConflictResolutionDescription(value) +
                               "' is used");
                }

                return value;

            } else if (value == CONFLICT_RESOLUTION_DEFAULT) {

                value = getDefaultConflictResolution();
                if (log.isTraceEnabled()) {
                    log.trace("Default conflict resolution configured for '" +
                               sourceUri +
                               "'. The default value is " +
                               getConflictResolutionDescription(value));
                }

                return value;

            } else {

                if (log.isTraceEnabled()) {
                    log.trace("Configured conflict resolution for '" + sourceUri +
                               "': " + getConflictResolutionDescription(value));
                }

                return value;

            }
        }  // conflictResolution != null

        value = getDefaultConflictResolution();
        if (log.isTraceEnabled()) {
            log.trace("Conflict resolution not configured for '" + sourceUri +
                       "'. The default value is used (" +
                       getConflictResolutionDescription(value) + ")");
        }

        return value;
    }

    // -------------------------------------------------------- Protected methds

    /**
     * Checks the given SyncItem lists and creates the needed SyncOperations
     * following the rules described in the class description and in the
     * architecture document.
     *
     * @param principal who has requested the synchronization
     * @param nextSync timestamp of the current synchronization
     * @param Am the Am set
     * @param Bm the Bm set
     * @param AmBm the AmBm
     * @param AmBBm the AmBBm
     * @param AAmBm the AAmBm
     *
     * @return an List containing all the collected sync operations
     */
    protected List checkSyncOperations(Principal principal,
                                       Timestamp nextSync,
                                       List Am,
                                       List Bm,
                                       List AmBm,
                                       List AmBBm,
                                       List AAmBm) {
        SyncItemMapping mapping = null;
        SyncItem syncItemA = null, syncItemB = null;

        List all = new ArrayList();
        List operations = new ArrayList();

        // ---------------------------------------------------------------------

        all.addAll(AmBm);
        all.addAll(AmBBm);
        all.addAll(AAmBm);

        //
        // 1st check: items in both sources
        //
        Iterator i = all.iterator();
        while (i.hasNext()) {
            mapping = (SyncItemMapping)i.next();

            syncItemA = mapping.getSyncItemA();
            syncItemB = mapping.getSyncItemB();

            operations.add(
                checkSyncOperation(
                    principal,
                    nextSync,
                    syncItemA,
                    syncItemB
                )
            );
            Am.remove(syncItemA);
            Bm.remove(syncItemB);
        }

        //
        // 2nd check: items in source A and not in source B
        //
        i = Am.iterator();
        while (i.hasNext()) {
            syncItemA = (SyncItem)i.next();

            operations.add(checkSyncOperation(principal, nextSync, syncItemA, null));
        } // next i

        //
        // 3rd check: items in source B and not in source A
        //
        i = Bm.iterator();
        while (i.hasNext()) {
            syncItemB = (SyncItem)i.next();

            operations.add(checkSyncOperation(principal, nextSync, null, syncItemB));
        } // next i

        return operations;
    }

    /**
     * Create a SyncOperation based on the state of the given SyncItem couple.
     *
     * @param principal     the entity that wnats to do the operation
     * @param nextSync      timestamp of the current synchronization (used for "B" operation)
     * @param syncItemA     the SyncItem of the source A - NULL means <i>not existing</i/
     * @param syncItemB     the SyncItem of the source B - NULL means <i>not existing</i/
     *
     * @return the SyncOperation object
     */
    protected SyncOperation checkSyncOperation(Principal principal,
                                               Timestamp nextSync,
                                               SyncItem syncItemA,
                                               SyncItem syncItemB) {
        if (log.isTraceEnabled()) {
            log.trace("check: syncItemA: "
                       + syncItemA
                       + " syncItemB: "
                       + syncItemB
            );
        }

        if (syncItemA == null) {
            syncItemA = InMemorySyncItem.getNotExistingSyncItem(null);
        }
        if (syncItemB == null) {
            syncItemB = InMemorySyncItem.getNotExistingSyncItem(null);
        }

        switch (syncItemA.getState()) {
            //
            // NEW
            //
            case SyncItemState.NEW:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_NEW_NEW);
                    case SyncItemState.UPDATED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_NEW_UPDATED);
                    case SyncItemState.DELETED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_NEW_DELETED);

                    case SyncItemState.SYNCHRONIZED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_NEW_SYNCHRONIZED);

                    case SyncItemState.NOT_EXISTING:
                        syncItemA.setTimestamp(nextSync);
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NEW, false, true);
                } // end inner switch

                //
                // DELETED
                //
            case SyncItemState.DELETED:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                        return new SyncConflict(principal,
                                                syncItemA,
                                                syncItemB,
                                                SyncConflict.STATE_DELETED_NEW);

                    case SyncItemState.UPDATED:
                        syncItemB.setState(SyncItemState.NEW);
                        return new SyncConflict(principal,
                                                syncItemA,
                                                syncItemB,
                                                SyncConflict.STATE_DELETED_UPDATED);

                    case SyncItemState.SYNCHRONIZED:
                        return new SyncOperationImpl(principal,
                            syncItemA,
                            syncItemB,
                            SyncOperation.DELETE,
                            false,
                            true);

                    case SyncItemState.DELETED:
                        return new SyncConflict(principal,
                                                syncItemA,
                                                syncItemB,
                                                SyncConflict.STATE_DELETED_DELETED);

                    case SyncItemState.NOT_EXISTING:
                        return new SyncOperationImpl(principal,
                            syncItemA,
                            syncItemB,
                            SyncOperation.NOP,
                            false,
                            false);
                } // end inner switch

                //
                // UPDATED
                //
            case SyncItemState.UPDATED:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_UPDATED_NEW);

                    case SyncItemState.UPDATED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_UPDATED_UPDATED);

                    case SyncItemState.DELETED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_UPDATED_DELETED);
                    case SyncItemState.SYNCHRONIZED:
                        syncItemA.setTimestamp(nextSync);
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.UPDATE, false, true);
                    case SyncItemState.NOT_EXISTING:
                        syncItemA.setTimestamp(nextSync);
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NEW, false, true);
                } // end inner switch

                //
                // SYNCHRONIZED
                //
            case SyncItemState.SYNCHRONIZED:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                    case SyncItemState.UPDATED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.UPDATE, true, false);
                    case SyncItemState.DELETED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.DELETE, true, false);
                    case SyncItemState.SYNCHRONIZED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NOP, false, false);
                    case SyncItemState.NOT_EXISTING:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NEW, false, true);
                } // end inner switch

                //
                // NOTEXISITNG
                //
            case SyncItemState.NOT_EXISTING:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                    case SyncItemState.UPDATED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NEW, true, false);
                    case SyncItemState.SYNCHRONIZED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NEW, true, false);
                    case SyncItemState.NOT_EXISTING:
                    case SyncItemState.DELETED:
                        return new SyncOperationImpl(principal, syncItemA, syncItemB,
                            SyncOperation.NOP, false, false);
                } // end inner switch

                //
                // PARTIAL
                //
            case SyncItemState.PARTIAL:
                switch (syncItemB.getState()) {
                    case SyncItemState.NEW:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_UPDATED_NEW);

                    case SyncItemState.UPDATED:
                        return new SyncOperationImpl(principal, syncItemA, null,
                            SyncOperation.ACCEPT_CHUNK, false, true);

                    case SyncItemState.DELETED:
                        return new SyncConflict(principal, syncItemA, syncItemB,
                                                SyncConflict.STATE_UPDATED_DELETED);

                    case SyncItemState.SYNCHRONIZED:
                        return new SyncOperationImpl(principal, syncItemA, null,
                            SyncOperation.ACCEPT_CHUNK, false, true);
                    case SyncItemState.NOT_EXISTING:
                        return new SyncOperationImpl(principal, syncItemA, null,
                            SyncOperation.ACCEPT_CHUNK, false, true);
                } // end inner switch

                //
                // CONFLICT
                // In this case itemA has a mapped twin and itemB not existing
                //
            case SyncItemState.CONFLICT:
                return new SyncConflict(principal, syncItemA, syncItemB,
                                        SyncConflict.STATE_CONFLICT_NONE);
        } // end switch

        return new SyncOperationImpl(principal, syncItemA, syncItemB, SyncOperation.NOP, false, false);
    }

    /**
     * Executes the given SyncOperation. Note that conflicts are ignored!
     * <p>
     * Note also that the number of status returned is equal to the number of
     * sources affected by the operation (0 for NOP, 1 for NEW and UPDATE and
     * 1 or 2 for DELETE).
     *
     * @param sources the syncsources relative to the operation
     * @param slowSync is this sync a slow sync ?
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation the SyncOperation to execute
     * @param syncFilterType the type of the filter used in the sync
     *
     * @return an array of <i>SyncOperationStatus</i> objects representing the
     *         status of the executed operation. For instance, in case of error,
     *         <i>SyncOperation.error</i> will be set to the catched exception.
     */
    protected SyncOperationStatus[] execSyncOperation(SyncSource[]      sources,
                                                      boolean           slowSync,
                                                      ClientMapping     mapping,
                                                      String            lastAnchor,
                                                      SyncOperationImpl operation,
                                                      int               syncFilterType) {

        SyncOperationStatus[] status = null;

        if (operation.getOperation() != SyncOperation.ACCEPT_CHUNK) {
            //
            // Converts the parent luid in parent guid if the parent is mapped
            //
            fixParent(mapping, operation);
        }

        switch (operation.getOperation()) {
            case SyncOperation.NEW:

                status = execNewOperation(sources,
                                          mapping,
                                          lastAnchor,
                                          operation,
                                          syncFilterType);
                break;

            case SyncOperation.UPDATE:

                status = execUpdateOperation(sources,
                                             mapping,
                                             lastAnchor,
                                             operation,
                                             syncFilterType);

                break;

            case SyncOperation.DELETE:

                status = execDeleteOperation(sources,
                                             mapping,
                                             lastAnchor,
                                             operation,
                                             syncFilterType);

                break;

            case SyncOperation.NOP:

                status = execNOPOperation(sources,
                                          mapping,
                                          lastAnchor,
                                          operation,
                                          syncFilterType);

                break;

            case SyncOperation.ACCEPT_CHUNK:

                status = execAcceptChunkOperation(sources,
                                                  mapping,
                                                  lastAnchor,
                                                  operation,
                                                  syncFilterType);

                break;

            case SyncOperation.CONFLICT:

                status = execConflict(sources,
                                      mapping,
                                      lastAnchor,
                                      operation,
                                      syncFilterType);

                break;

        } // end switch

        updateMapping(slowSync, mapping, lastAnchor, operation);

        return status;
    }

    // --------------------------------------------------------- Private methods

    /**
     * If any item from the client has not a corresponding mapping, the
     * server source must be queried for the item, since the client item
     * could be the same of an existing server item. In this case, the old
     * unmapped item is replaced in Am by the newly mapped item.
     *
     * BTW, we have to know the valid client mapping because in the twins search
     * we need to handle the parentKey and moreover, knowing the mapping,
     * we are able to ignore the twins already mapped.
     *
     *
     * @param newlyMappedItems the collection that will contain the newly mapped
     *        items. If null, it won't be used.
     * @param syncItems the items to be searched if not mapped. If an item is
     * newly mapped then the old unmapped item is replaced by the newly mapped
     * item.
     * @param source the source that has to be queried for twins
     * @param validClientMapping the valid client mapping. The newly mapped items
     * found are added to the validClientMapping
     *
     * @throws SyncSourceException if an error occurs
     */
     private void fixMappedItems(Collection    newlyMappedItems,
                                 SyncItem[]    syncItems,
                                 SyncSource    source,
                                 Map           validClientMapping,
                                 Principal     principal) throws SyncSourceException {


        if (validClientMapping == null) {
            validClientMapping = new HashMap();
        }

        SyncItem itemB    = null;

        SyncItemKey[] twinsKey = null;
        for (int i = 0; ( (syncItems != null) && (i < syncItems.length)); ++i) {
            itemB    = null;
            twinsKey = null;
            //
            // We have to exclude the item already mapped, the item not complete
            // and the item deleted (this items don't contain data)
            //
            if ( (! ( (AbstractSyncItem)syncItems[i]).isMapped()) &&
                (SyncItemState.PARTIAL != syncItems[i].getState()) &&
                (SyncItemState.DELETED != syncItems[i].getState())) {

               try {

                   //
                   // In order to check the twins, we have to fix the parentKey.
                   // In order to avoid to change the client item directly, we
                   // create a cloneItem of the item. In this way the original client
                   // item doesn't change.
                   //
                   AbstractSyncItem clone = (AbstractSyncItem) (syncItems[i].cloneItem());
                   fixParent(validClientMapping, clone);
                   twinsKey = source.getSyncItemKeysFromTwin(clone);
                   if (twinsKey == null || twinsKey.length == 0) {
                       //
                       // No twin found
                       //
                       continue;
                   } else {
                       //
                       // Search for an item not mapped yet.
                       //
                       SyncItemKey twinKey = findNotMappedItem(validClientMapping,
                                                               twinsKey);

                       if (twinKey == null) {
                           //
                           // No twin found
                           //
                           continue;
                       }

                       itemB = new InMemorySyncItem(source,
                                                    twinKey.getKeyAsString());
                   }

               } catch (Exception e) {
                   if (log.isWarningEnabled()) {
                       String msg = "Error retrieving the twin item of "
                                    + syncItems[i].getKey()
                                    + " from source "
                                    + source;

                       log.warn(msg, e);
                   }
                   continue;
               }

               if (log.isTraceEnabled()) {
                   log.trace("Hey, client item "
                              + syncItems[i].getKey().getKeyAsString()
                              + " is the same of server item "
                              + itemB.getKey().getKeyAsString()
                              + "! We have to resolve the conflict"
                   );
               }

               //
               // We add the mapping in the validClientMapping because we are in
               // a "for" cycle and in this way we are able to handle the mapping found
               // previously.
               //
               validClientMapping.put(itemB.getKey().getKeyAsString(),
                                      syncItems[i].getKey().getKeyAsString());

               InMemorySyncItem newItemB = new InMemorySyncItem(itemB.getSyncSource(),
                                                                itemB.getKey().getKeyAsString(),
                                                                null,
                                                                syncItems[i].getKey().getKeyAsString(),
                                                                syncItems[i].getState());

               //
               // We set the newItemB's state to UPDATED because in this way we
               // have a conflict (and so we can resolve the conflict). If
               // we set the state to 'S', we have an U-S and this isn't a conflict
               // and the client data replaces the server data (execUpdateOperation).
               //
               newItemB.setState(SyncItemState.UPDATED);

               SyncItemMapping mapping = new SyncItemMapping(newItemB.getKey());
               syncItems[i] = SyncItemHelper.newMappedSyncItem(newItemB.getKey(),
                       (AbstractSyncItem)syncItems[i]);

               mapping.setMapping(syncItems[i], newItemB);
               if (newlyMappedItems != null) {
                   newlyMappedItems.add(mapping);
               }
           }
       } // next i
    }

    /**
     * Verifies that the given item the size of its content metches the
     * specified size, if specified. In the case of a mismatch, a
     * <code>SyncSourceException</code> is thrown with status code
     * OBJECT_SIZE_MISMATCH (424).
     *
     * @param item item to check
     *
     * @throws ObjectSizeMismatchException if the content size does not match
               the declared size (if declared)
     */
    private void checkSize(SyncItem item) throws ObjectSizeMismatchException {

        if (log.isTraceEnabled()) {
            log.trace("Check item size");
        }

        Long size = (Long)((AbstractSyncItem)item).getPropertyValue(AbstractSyncItem.PROPERTY_DECLARED_SIZE);

        if (size == null) {
            if (log.isTraceEnabled()) {
                log.trace("Declared size is null...skip the check");
            }

            return;
        }

        if (log.isTraceEnabled()) {
            log.trace("Declared size: " + size);
        }

        Long contentSize =
            (Long)((AbstractSyncItem)item).getPropertyValue(AbstractSyncItem.PROPERTY_CONTENT_SIZE);

        if (contentSize == null) {
            contentSize = new Long(item.getContent().length);
        }

        if (log.isTraceEnabled()) {
            log.trace("Content size: " + contentSize);
        }


        //
        // We cannot check the content lenght (content.lenght) because the items
        // are already handled by the DataTransformationManager in the Sync4jEngine
        // See Sync4jEngine.applyDataTransformationOnIngoingItems
        //
        if (contentSize == null || !size.equals(contentSize)) {

            if (log.isTraceEnabled()) {
                log.trace("Size mismatch. The size of the received object (" +
                        contentSize + ") does not match the declared size (" +
                        size + ")");
            }

            throw new ObjectSizeMismatchException(
                "The size of the received object does not match the given size");
        }
    }


    /**
     * Executes a conflict and returns the right status.
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     *
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflict(SyncSource[]      sources,
                                               ClientMapping     mapping,
                                               String            lastAnchor,
                                               SyncOperationImpl operation,
                                               int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = null;

        try {
            SyncConflict sc = (SyncConflict)operation;

            //
            //Type CX means that itemA is in state CONFLICT and itemB is in state NOT_EXISTING
            //This situation happens when itemA has already a twin mapped
            //
            if (SyncConflict.STATE_CONFLICT_NONE.equals(sc.getType())) {

                status = execConflictCX(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);

            } else if (SyncConflict.STATE_DELETED_DELETED.equals(sc.getType())) {

                status = execConflictDD(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else if (SyncConflict.STATE_NEW_NEW.equals(sc.getType()) ||
                       SyncConflict.STATE_NEW_UPDATED.equals(sc.getType())) {

                status = execConflictNU(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else if (SyncConflict.STATE_UPDATED_UPDATED.equals(sc.getType()) ||
                       SyncConflict.STATE_UPDATED_NEW.equals(sc.getType())) {

                status = execConflictUU(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else if (SyncConflict.STATE_UPDATED_DELETED.equals(sc.getType())) {

                status = execConflictUD(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else if (SyncConflict.STATE_NEW_SYNCHRONIZED.equals(sc.getType())) {

                status = execConflictNS(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else if (SyncConflict.STATE_DELETED_NEW.equals(sc.getType()) ||
                       SyncConflict.STATE_DELETED_UPDATED.equals(sc.getType())) {

                status = execConflictDU(sources,
                                        mapping,
                                        lastAnchor,
                                        operation,
                                        syncFilterType);


            } else {

                status = execConflictNotSpecified(sources,
                                                  mapping,
                                                  lastAnchor,
                                                  operation,
                                                  syncFilterType);

            }

        } catch (SyncException e) {
            log.error("Error executing sync operation", e);

            status[0] = new Sync4jOperationStatusError(operation, sources[1], cmd, e);
            operation.setAOperation(false);
            operation.setBOperation(false);
        }


        return status;
    }


    /**
     * Executes a conflict DU and returns the right status
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictDU(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        SyncItemKey luid = ( (AbstractSyncItem)syncItemA).getMappedKey();
        if (luid == null) {
            throw new SyncException("Item A with key "
                                    + syncItemA.getKey()
                                    + " should have a mapping, but the mapped key is null"
            );
        }

        int conflictResolution = getConflictResolution(syncItemB.getSyncSource());

        if (conflictResolution == CONFLICT_RESOLUTION_CLIENT_WINS) {
            //
            // We have to delete the item on the server because the client has
            // deleted the item. The changes on the server item will be lose
            //
            sources[0].removeSyncItem(syncItemB.getKey(),
                                      syncItemA.getTimestamp(),
                                      false // softDelete ?
                       );

            status[0] = new Sync4jOperationStatusConflict(
                            operation,
                            sources[1],
                            cmd,
                            StatusCode.OK
                        );

            operation.setAOperation(false);
            operation.setBOperation(true);
            incrementDeletedItemsServer(sources[0].getSourceURI());
            return status;
        }

        //
        // If the conflict resolution is not CONFLICT_RESOLUTION_CLIENT_WINS,
        // the server has to re-add the item on the client
        // If we are using some filter, the server has to re-add the item ONLY if
        // it is in the filter criteria
        //
        boolean isItemInFilter = true;

        if (sources[0] instanceof FilterableSyncSource) {
            if (syncFilterType != SYNC_WITHOUT_FILTER) {
                //
                // We have to use the key because we don't have the content
                //
                isItemInFilter = ((FilterableSyncSource)sources[0]).
                                     isSyncItemInFilterClause(syncItemB.getKey());

                if (!isItemInFilter) {

                    status[0] = new Sync4jOperationStatusConflict(
                                    operation,
                                    sources[1],
                                    cmd,
                                    StatusCode.ITEM_NOT_DELETED
                                );

                    operation.setAOperation(false);
                    operation.setBOperation(false);

                    return status;

                }
            }
        }

        //
        // In all others cases we re-add the item on the client
        // (with status code 419)
        //
        AbstractSyncItem resolvingItem = SyncItemHelper.newResolvingClientItem((AbstractSyncItem)syncItemB, syncItemA);

        if (resolvingItem.isMapped()) {
            syncItemA = (AbstractSyncItem) (sources[1].updateSyncItem(resolvingItem));
            incrementUpdatedItemsClient(sources[1].getSourceURI());
        } else {
            syncItemA = (AbstractSyncItem) (sources[1].addSyncItem(resolvingItem));
            incrementAddedItemsOnClient(sources[1].getSourceURI());
        }

        operation.setSyncItemA(syncItemA);
        operation.setAOperation(true);

        status[0] = new Sync4jOperationStatusConflict(
            operation,
            sources[1],
            cmd,
            StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA
        );

        return status;
    }

    /**
     * Executes a conflict DD and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictDD(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        status[0] = new Sync4jOperationStatusConflict(
            operation,
            sources[1],
            cmd,
            StatusCode.ITEM_NOT_DELETED
        );

        return status;
    }

    /**
     * Executes a conflict CX and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictCX(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        sources[1].removeSyncItem(syncItemA.getMappedKey(),
                                  syncItemA.getTimestamp(),
                                  false); // it isn't a softDelete

        status[0] = new Sync4jOperationStatusConflict(
            operation,
            sources[1],
            cmd,
            StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA
        );
        incrementDeletedItemsClient(sources[1].getSourceURI());

        return status;
    }

    /**
     * Executes a conflict NS and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictNS(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        //
        // This happens when the Client sent an item that have a twin
        // synchronized on server.
        //

        SyncItemKey luid = ( (AbstractSyncItem)syncItemA).getMappedKey();
        if (luid == null) {
            throw new SyncException("Item A with key "
                + syncItemA.getKey()
                + " should have a mapping, but the mapped key is null"
                );
        }

        if (sources[0] instanceof FilterableSyncSource) {
            if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER &&
                !((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemA)) {

                sources[1].removeSyncItem(luid,
                                          syncItemB.getTimestamp(),
                                          false); // it isn't a softDelete

                operation.setDeleteForced(true);
                operation.setAOperation(true);
                incrementDeletedItemsClient(sources[1].getSourceURI());

            } else {
                operation.setAOperation(false);
            }
        } else {
            operation.setAOperation(false);
        }

        status[0] = new Sync4jOperationStatusConflict(
            operation,
            sources[1],
            cmd,
            StatusCode.ALREADY_EXISTS
        );

        return status;
    }

    /**
     * Executes a conflict not handle by the others cases
     * (this is the default conflict handling)
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictNotSpecified(SyncSource[]      sources,
                                                           ClientMapping     mapping,
                                                           String            lastAnchor,
                                                           SyncOperationImpl operation,
                                                           int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        AbstractSyncItem resolvingItem = null;

        SyncItemKey luid = ( (AbstractSyncItem)syncItemA).getMappedKey();
        if (luid == null) {
            throw new SyncException("Item A with key "
                + syncItemA.getKey()
                + " should have a mapping, but the mapped key is null"
                );
        }

        if (sources[0] instanceof FilterableSyncSource &&
            syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER &&
            !((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemA)) {

            sources[1].removeSyncItem(luid,
                                      syncItemB.getTimestamp(),
                                      false); // it isn't a softDelete

            operation.setDeleteForced(true);
            operation.setAOperation(true);
            incrementDeletedItemsClient(sources[1].getSourceURI());

        } else {

            resolvingItem = SyncItemHelper.newResolvingClientItem((AbstractSyncItem)syncItemB, syncItemA);

            if (resolvingItem.isMapped()) {
                syncItemA = (AbstractSyncItem) (sources[1].updateSyncItem(resolvingItem));
                incrementUpdatedItemsClient(sources[1].getSourceURI());
            } else {
                syncItemA = (AbstractSyncItem) (sources[1].addSyncItem(resolvingItem));
                incrementAddedItemsOnClient(sources[1].getSourceURI());
            }

            operation.setSyncItemA(syncItemA);
            operation.setAOperation(true);

        }

        status[0] = new Sync4jOperationStatusConflict(
            operation,
            sources[1],
            cmd,
            StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA
        );

        return status;
    }


    /**
     * Executes a conflict NU and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictNU(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {


        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
                (ModificationCommand) syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncItemKey luid = syncItemA.getMappedKey();
        if (luid == null) {
            throw new SyncException("Item A with key "
                                    + syncItemA.getKey()
                                    +
                                    " should have a mapping, but the mapped key is null"
            );
        }

        AbstractSyncItem resolvingItem = new InMemorySyncItem(syncItemB.getSyncSource(), syncItemA.getKey().getKeyAsString());

        boolean clientItemChanged = resolveConflict(syncItemB.getSyncSource(),
                                                    syncItemB.getKey(),
                                                    syncItemA,
                                                    resolvingItem
                                    );

        operation.setSyncItemB(resolvingItem);

        // set statusCode
        int statusCode = StatusCode.OK;
        if (clientItemChanged) {
            int conflictResolution = getConflictResolution(syncItemB.getSyncSource());
            switch (conflictResolution) {
                //
                // We haven't to check CONFLICT_RESOLUTION_CLIENT_WINS because
                // in this case the client item doesn't change
                //
                case CONFLICT_RESOLUTION_MERGE_DATA:
                    statusCode = StatusCode.CONFLICT_RESOLVED_WITH_MERGE;
                    break;
                case CONFLICT_RESOLUTION_SERVER_WINS:
                    statusCode = StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA;
                    break;
                default:
            }
        } else {
            //
            // Or the conflit resolution is CLIENT_WINS or is MERGE_DATA and
            // the client content isn't changed. BTW, the item on the server
            // already exists, so an ALREADY_EXISTS (418) is returned
            //
            statusCode = StatusCode.ALREADY_EXISTS;
        }
        //
        // In all cases we have to check if it's in the filter
        // criteria or not.
        //
        boolean isItemInFilter = true;

        if (sources[0] instanceof FilterableSyncSource &&
            syncFilterType != SYNC_WITHOUT_FILTER) {
                isItemInFilter =
                    ((FilterableSyncSource) sources[0]).
                    isSyncItemInFilterClause(resolvingItem.getKey());
        }

        if (isItemInFilter && clientItemChanged) {
            //
            // The client item is in the filter criteria and his content is
            // changed resolving the conflict. The new contect has to be sent
            // to the client.
            //

            if (resolvingItem.isMapped()) {
                syncItemA = (AbstractSyncItem) (sources[1].updateSyncItem(
                                resolvingItem));
                incrementUpdatedItemsClient(sources[1].getSourceURI());
            } else {
                syncItemA = (AbstractSyncItem) (sources[1].addSyncItem(
                                resolvingItem));
                incrementAddedItemsOnClient(sources[1].getSourceURI());
            }

            operation.setSyncItemA(syncItemA);
            operation.setAOperation(true);

        } else if (isItemInFilter && !clientItemChanged) {
            //
            // The client item is in the filter criteria but his content isn't
            // changed resolving the conflict. So nothing is required
            //
            operation.setAOperation(false);
        } else {
            if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {
                //
                // The client item is out the filter so the server has to delete
                // it on the client
                //
                operation.setDeleteForced(true);
                incrementDeletedItemsClient(sources[1].getSourceURI());

            } else if (syncFilterType == SYNC_WITH_INCLUSIVE_FILTER) {
                //
                // The client item is out the filter so the server mustn't
                // send it to the client
                //
                operation.setAOperation(false);
            }
        }

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        status[0] = new Sync4jOperationStatusConflict(
                        operation,
                        sources[1],
                        cmd,
                        statusCode
                    );

        return status;
    }

    /**
     * Executes a conflict UU and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictUU(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        SyncItemKey luid = syncItemA.getMappedKey();
        if (luid == null) {
            throw new SyncException("Item A with key "
                + syncItemA.getKey()
                + " should have a mapping, but the mapped key is null"
            );
        }

        AbstractSyncItem resolvingItem = new InMemorySyncItem(syncItemB.getSyncSource(), syncItemA.getKey().getKeyAsString());

        boolean clientItemChanged = resolveConflict(syncItemB.getSyncSource(),
                                                    syncItemB.getKey(),
                                                    syncItemA,
                                                    resolvingItem
                                    );

        if (clientItemChanged) {

            int statusCode = StatusCode.OK;
            int conflictResolution = getConflictResolution(syncItemB.getSyncSource());
            switch (conflictResolution) {
                //
                // We haven't to check CONFLICT_RESOLUTION_CLIENT_WINS because
                // in this case the client item doesn't change
                //
                case CONFLICT_RESOLUTION_MERGE_DATA:
                    statusCode = StatusCode.CONFLICT_RESOLVED_WITH_MERGE;
                    break;
                case CONFLICT_RESOLUTION_SERVER_WINS:
                    statusCode = StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA;
                    break;
                default:
            }

            //
            // Or the conflit resolution is SERVER_WINS or is MERGE_DATA and
            // the client content is changed.
            //
            // The client content is changed, we have to check if it's in the filter
            // criteria or not.
            //
            boolean isItemInFilter = true;

            if (sources[0] instanceof FilterableSyncSource &&
                syncFilterType != SYNC_WITHOUT_FILTER) {
                isItemInFilter =
                    ((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemB.getKey());
            }

            if (isItemInFilter) {

                if (resolvingItem.isMapped()) {
                    syncItemA = (AbstractSyncItem) (sources[1].updateSyncItem(resolvingItem));
                    incrementUpdatedItemsClient(sources[1].getSourceURI());
                } else {
                    syncItemA = (AbstractSyncItem) (sources[1].addSyncItem(resolvingItem));
                    incrementAddedItemsOnClient(sources[1].getSourceURI());
                }

                operation.setSyncItemA(syncItemA);
                operation.setAOperation(true);

                status[0] = new Sync4jOperationStatusConflict(
                    operation,
                    sources[1],
                    cmd,
                    statusCode
                );
            } else {
                if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {
                    //
                    // The resolved item is out the filter so the server has to delete
                    // it on the client
                    //
                    operation.setDeleteForced(true);
                    status[0] = new Sync4jOperationStatusConflict(
                        operation,
                        sources[1],
                        cmd,
                        statusCode
                    );
                    incrementDeletedItemsClient(sources[1].getSourceURI());
                } else if (syncFilterType == SYNC_WITH_INCLUSIVE_FILTER) {
                    //
                    // The resolved item is out the filter so the server mustn't
                    // send it to the client
                    //
                    operation.setAOperation(false);
                    status[0] = new Sync4jOperationStatusConflict(
                        operation,
                        sources[1],
                        cmd,
                        statusCode
                    );
                }
            }
        } else {

            //
            // Or the conflit resolution is CLIENT_WINS or is MERGE_DATA and
            // the client content isn't changed.
            //
            status[0] = new Sync4jOperationStatusConflict(
                operation,
                sources[1],
                cmd,
                StatusCode.OK
            );
        }

        return status;
    }

    /**
     * Executes a conflict UD and returns the status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execConflictUD(SyncSource[]      sources,
                                                 ClientMapping     mapping,
                                                 String            lastAnchor,
                                                 SyncOperationImpl operation,
                                                 int               syncFilterType)
    throws SyncException {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        SyncItemKey luid = syncItemA.getMappedKey();
        if (luid == null) {
            throw new SyncException( "Item A with key "
                                   + syncItemA.getKey()
                                   + " should have a mapping, but the mapped key is null"
                                   );
        }

        int conflictResolution = getConflictResolution(syncItemB.getSyncSource());

        if (conflictResolution == CONFLICT_RESOLUTION_CLIENT_WINS ||
            conflictResolution == CONFLICT_RESOLUTION_MERGE_DATA) {
            //
            // In this case the item on the server has been deleted
            // so we have to call the addSyncItem in order to re-add it.
            //
            try {
                syncItemB = sources[0].addSyncItem(syncItemA);
                operation.setSyncItemB(syncItemB);
                operation.setBOperation(true);
                incrementAddedItemsOnServer(sources[0].getSourceURI());

                status[0] = new Sync4jOperationStatusConflict(
                                operation,
                                sources[1],  // TODO should not be 0?
                                cmd,
                                StatusCode.OK
                            );
            } catch (RefusedItemException ex) {
                operation.setDeleteForced(true);
                status[0] = new Sync4jOperationStatusConflict(operation, sources[0], cmd, StatusCode.OK);
            }
            return status;
        }

        //
        // With CONFLICT_RESOLUTION_SERVER_WINS the item has to be deleted
        // on the client
        //
        sources[1].removeSyncItem(syncItemA.getKey(),
                                  syncItemA.getTimestamp(),
                                  false // is softDelete ?
                   );

        operation.setAOperation(true);
        operation.setBOperation(true);
        incrementDeletedItemsClient(sources[1].getSourceURI());

        status[0] = new Sync4jOperationStatusConflict(
                        operation,
                        sources[1],
                        cmd,
                        StatusCode.CONFLICT_RESOLVED_WITH_SERVER_DATA
                    );

        return status;


    }

    /**
     * Executes an ACCEPT_CHUNK operation and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execAcceptChunkOperation(SyncSource[]      sources,
                                                           ClientMapping     mapping,
                                                           String            lastAnchor,
                                                           SyncOperationImpl operation,
                                                           int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        cmd = (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        status[0] = new Sync4jOperationStatusOK(
            operation, sources[0], cmd, StatusCode.CHUNKED_ITEM_ACCEPTED
        );

        return status;
    }


    /**
     * Executes a NEW operation and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     */
    private SyncOperationStatus[] execNewOperation(SyncSource[]      sources,
                                                   ClientMapping     mapping,
                                                   String            lastAnchor,
                                                   SyncOperationImpl operation,
                                                   int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];

        if (operation.isAOperation()) {

            try {

                if ( ( (AbstractSyncItem)syncItemB).isMapped()) {
                    syncItemA = (AbstractSyncItem)sources[1].updateSyncItem(syncItemB);
                } else {
                    syncItemA = (AbstractSyncItem)sources[1].addSyncItem(syncItemB);
                }
                incrementAddedItemsOnClient(sources[1].getSourceURI());

                operation.setSyncItemA(syncItemA);

                status[0] = new Sync4jOperationStatusOK(operation,
                    sources[1],
                    cmd,
                    StatusCode.ITEM_ADDED);

            } catch (SyncException e) {
                if (!e.isSilent()) {
                    log.error("Error executing sync operation", e);
                }
                status[0] = new Sync4jOperationStatusError(operation, sources[1], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        } else if (operation.isBOperation()) {

            syncItemB.setTimestamp(syncItemA.getTimestamp()); // this contains the
                                                              // current sync
            try {
                checkSize(syncItemA);

                if (syncItemA.isMapped()) {
                    syncItemB = sources[0].updateSyncItem(syncItemA);
                    incrementUpdatedItemsServer(sources[0].getSourceURI());
                } else {
                    syncItemB = sources[0].addSyncItem(syncItemA);
                    incrementAddedItemsOnServer(sources[0].getSourceURI());
                }

                operation.setSyncItemB(syncItemB);

                if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {
                    //
                    // We check the syncsource type, but if sources[0] isn't a
                    // FilterableSyncSource the syncFilterType is SYNC_WITHOUT_FILTER
                    //
                    if (sources[0] instanceof FilterableSyncSource) {
                        boolean itemInFilter =
                            ((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemB);

                        if (!itemInFilter) {
                            operation.setDeleteForced(true);
                        }
                    } else {
                        //
                        // This cases should never happen
                        //
                        throw new SyncSourceException("The syncFilterType is different " +
                                "from SYNC_WITHOUT_FILTER but the SyncSource isn't " +
                                "a FilterableSyncSource. This means the engine hasn't " +
                                "handled correctly the filter type");
                    }
                }

                if (status[0] == null) {
                    status[0] = new Sync4jOperationStatusOK(operation,
                        sources[1], // TODO should not be 0?
                        cmd,
                        StatusCode.ITEM_ADDED);
                }
            } catch (ObjectSizeMismatchException e) {
                log.info(e.getMessage());
                status[0] = new Sync4jOperationStatusError(operation, sources[0], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            } catch (RefusedItemException ex) {
                operation.setDeleteForced(true);
                status[0] = new Sync4jOperationStatusOK(operation, sources[0], cmd, StatusCode.OK);
            } catch (SyncException e) {
                if (!e.isSilent()) {
                    log.error("Error executing sync operation", e);
                }
                status[0] = new Sync4jOperationStatusError(operation, sources[0], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        }

        return status;
    }

    /**
     * Executes an UPDATE operation and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execUpdateOperation(SyncSource[]      sources,
                                                      ClientMapping     mapping,
                                                      String            lastAnchor,
                                                      SyncOperationImpl operation,
                                                      int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = new SyncOperationStatus[1];
        if (operation.isAOperation()) {
            try {

                if ( ( (AbstractSyncItem)syncItemB).isMapped()) {
                    syncItemA = (AbstractSyncItem) (sources[1].updateSyncItem(syncItemB));
                } else {
                    syncItemA = (AbstractSyncItem) (sources[1].addSyncItem(syncItemB));
                }
                incrementUpdatedItemsClient(sources[1].getSourceURI());

                operation.setSyncItemA(syncItemA);
                status[0] = new Sync4jOperationStatusOK(operation, sources[1], cmd);
            } catch (SyncException e) {
                if (!e.isSilent()) {
                    log.error("Error executing sync operation", e);
                }
                status[0] = new Sync4jOperationStatusError(operation, sources[1], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        } else if (operation.isBOperation()) {

            syncItemB.setTimestamp(syncItemA.getTimestamp()); // this contains the
                                                              // current sync
            try {
                checkSize(syncItemA);

                if ( ( (AbstractSyncItem)syncItemA).isMapped()) {
                    syncItemB = sources[0].updateSyncItem(syncItemA);
                    incrementUpdatedItemsServer(sources[0].getSourceURI());
                } else {
                    syncItemB = sources[0].addSyncItem(syncItemA);
                    incrementAddedItemsOnServer(sources[0].getSourceURI());
                }

                operation.setSyncItemB(syncItemB);

                if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {
                    if (sources[0] instanceof FilterableSyncSource) {
                        //
                        // We check the syncsource type, but if sources[0] isn't a
                        // FilterableSyncSource the syncFilterType is SYNC_WITHOUT_FILTER
                        //
                        boolean itemInFilter =
                                ((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemB);

                        if (!itemInFilter) {
                            operation.setDeleteForced(true);
                        }
                    } else {
                        //
                        // This cases should never happen
                        //
                        throw new SyncSourceException("The syncFilterType is different " +
                                "from SYNC_WITHOUT_FILTER but the SyncSource isn't " +
                                "a FilterableSyncSource. This means the engine hasn't " +
                                "handled correctly the filter type");
                    }

                }
                if (status[0] == null) {
                    status[0] = new Sync4jOperationStatusOK(operation, sources[0], cmd);
                }
            } catch (ObjectSizeMismatchException e) {
                log.info(e.getMessage());
                status[0] = new Sync4jOperationStatusError(operation, sources[0], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            } catch (RefusedItemException ex) {
                operation.setDeleteForced(true);
                status[0] = new Sync4jOperationStatusOK(operation, sources[0], cmd, StatusCode.OK);
            } catch (SyncException e) {
                if (!e.isSilent()) {
                    log.error("Error executing sync operation", e);
                }
                status[0] = new Sync4jOperationStatusError(operation, sources[0], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        }
        return status;
    }


    /**
     * Executes a DELETE operation and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execDeleteOperation(SyncSource[]      sources,
                                                      ClientMapping     mapping,
                                                      String            lastAnchor,
                                                      SyncOperationImpl operation,
                                                      int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = null;

        //
        // How many status we need? One if we have to delete the item
        // from only one source, two if we have to delete it from both
        // sources
        //
        int size = 1;
        if (operation.isAOperation() && operation.isBOperation()) {
            size = 2;
        }
        status = new SyncOperationStatus[size];

        int s = 0;
        if (operation.isBOperation()) {
            syncItemB.setTimestamp(syncItemA.getTimestamp()); // this contains the
                                                              // timestamp of the
                                                              // current sync

            boolean softDelete = false;

            if (cmd instanceof Delete) {
                softDelete = ( (Delete)cmd).isSftDel();
            }
            try {
                sources[0].removeSyncItem(syncItemB.getKey(),
                                          syncItemB.getTimestamp(),
                                          softDelete);
                status[s++] = new Sync4jOperationStatusOK(operation, sources[0], cmd);
                incrementDeletedItemsServer(sources[0].getSourceURI());
            } catch (SyncException e) {
                log.error("Error executing sync operation", e);

                status[s++] = new Sync4jOperationStatusError(operation, sources[0], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        }
        if (operation.isAOperation()) {

            try {
                sources[1].removeSyncItem(syncItemA.getKey(),
                                          syncItemA.getTimestamp(),
                                          false); // it isn't a softDelete

                status[s++] = new Sync4jOperationStatusOK(operation, sources[1], cmd);
                incrementDeletedItemsClient(sources[1].getSourceURI());
            } catch (SyncException e) {
                log.error("Error executing sync operation", e);
                status[s++] = new Sync4jOperationStatusError(operation, sources[1], cmd, e);
                operation.setAOperation(false);
                operation.setBOperation(false);
            }
        }

        return status;
    }


    /**
     * Executes a NOP operation and returns the right status
     *
     * @param sources the syncsources relative to the operation
     * @param mapping the mapping
     * @param lastAnchor the lastAnchor of this sync
     * @param operation SyncOperationImpl
     * @param syncFilterType int
     * @return SyncOperationStatus
     * @throws SyncException
     */
    private SyncOperationStatus[] execNOPOperation(SyncSource[]      sources,
                                                   ClientMapping     mapping,
                                                   String            lastAnchor,
                                                   SyncOperationImpl operation,
                                                   int               syncFilterType) {

        AbstractSyncItem syncItemA = (AbstractSyncItem)operation.getSyncItemA();
        SyncItem     syncItemB = operation.getSyncItemB();

        ModificationCommand cmd =
            (ModificationCommand)syncItemA.getPropertyValue(AbstractSyncItem.PROPERTY_COMMAND);

        SyncOperationStatus[] status = null;

        //
        // A NOP operation can be due by one of the following conditions
        // (see checkOperation(...)):
        //
        // 1. both items are flagged as "Deleted"
        // 2. both items are flagged "Synchronized"
        // 3. itemA is "Deleted" and itemB is "Not existing" or "Deleted"
        //
        // In case 1. we want a status is returned, thought no real
        // action is performed.
        //
        int s    = 0;
        int size = 0;

        if (operation.isAOperation()) {
            ++size;
        }
        if (operation.isBOperation()) {
            ++size;
        }

        status = new SyncOperationStatus[size];

        if (operation.isBOperation()) {
            status[s++] = new Sync4jOperationStatusOK(operation, sources[0], cmd);
        }

        if (operation.isAOperation()) {
            status[s++] = new Sync4jOperationStatusOK(operation, sources[1], cmd);
        }

        if (!operation.isAOperation() && !operation.isBOperation()) {
            char stateItemA = syncItemA.getState();
            char stateItemB = syncItemB.getState();

            switch (stateItemA) {
                case SyncItemState.SYNCHRONIZED:
                    if (stateItemB == SyncItemState.SYNCHRONIZED) {
                        status = new SyncOperationStatus[1];

                        if (cmd instanceof Add) {
                            status[0] = new Sync4jOperationStatusOK(operation, sources[1],
                                cmd, StatusCode.ALREADY_EXISTS);
                        } else {
                            status[0] = new Sync4jOperationStatusOK(operation, sources[1],
                                cmd);
                        }

                        try {
                            if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {
                                if (sources[0] instanceof FilterableSyncSource) {
                                    //
                                    // We check the syncsource type, but if sources[0] isn't a
                                    // FilterableSyncSource the syncFilterType is SYNC_WITHOUT_FILTER
                                    //
                                    boolean itemInFilter =
                                        ((FilterableSyncSource)sources[0]).isSyncItemInFilterClause(syncItemA);
                                    if (!itemInFilter) {
                                        operation.setDeleteForced(true);
                                    }
                                } else {
                                    //
                                    // This cases should never happen
                                    //
                                    throw new SyncSourceException(
                                        "The syncFilterType is different from " +
                                        "SYNC_WITHOUT_FILTER but the SyncSource " +
                                        "isn't a FilterableSyncSource. This means " +
                                        "the engine hasn't handled correctly " + 
                                        "the filter type");
                                }
                            }

                        } catch (SyncSourceException e) {
                            log.error("Error executing sync operation", e);

                            status[0] = new Sync4jOperationStatusError(operation, sources[0],
                                cmd, e);
                            operation.setAOperation(false);
                            operation.setBOperation(false);
                        }
                        break ;
                    }
                case SyncItemState.DELETED:
                    if (stateItemB == SyncItemState.NOT_EXISTING) {
                        //
                        //client sent Delete item that not exist on server
                        //
                        operation.setSyncItemA(syncItemA);

                        status = new SyncOperationStatus[1];
                        status[0] = new Sync4jOperationStatusOK(
                            operation,
                            sources[1],
                            cmd,
                            StatusCode.ITEM_NOT_DELETED
                                    );
                        break;

                    } else if (stateItemB == SyncItemState.DELETED) {
                        //
                        //client sent Delete item that is deleted on the server
                        //
                        status = new SyncOperationStatus[1];
                        cmd = (ModificationCommand)((AbstractSyncItem)syncItemA).getPropertyValue(
                            AbstractSyncItem.PROPERTY_COMMAND);
                        status[0] = new Sync4jOperationStatusOK(operation, sources[1], cmd);
                        break;
                    }
            }
        }

        return status;
    }

    /**
     * Check if there are some items to delete on the client.
     * If the client specifies a exclusive filter, the server has to delete
     * on the client all items that don't satisfy the filter. The server
     * knows the item on the client using the mapping. In order to do this,
     * we call the method getAllSyncItemKeys to retrieve all items in the filter
     * criteria. Then, we check all items in the mapping with this set; if an
     * item isn't in the set, we have to delete it on the client
     *
     * @param sources the sources in sync
     * @param syncFilterType the filter type
     * @param allItemsKeys all items on the server inside the filter criteria
     * @param mapping the mapping
     * @param Bm the list of the changed items on the server. If an item not
     * satisfying the filter has to be deleted, it is added to the list.
     */
    private void checkForItemsToDelete(SyncSource[]  sources,
                                       int           syncFilterType,
                                       SyncItemKey[] allItemsKeys,
                                       ClientMapping mapping,
                                       List          Bm) {

        List guidluid = mapping.getMapping();

        if (syncFilterType == SYNC_WITH_EXCLUSIVE_FILTER) {

            if (log.isTraceEnabled()) {
                log.trace("Check for items on the client (seeing the mapping) " +
                           "in order to discovery the items to delete");
            }

            List        keysList = Arrays.asList(allItemsKeys);
            SyncItemKey tmp      = new SyncItemKey("tmp");

            Iterator it = guidluid.iterator();
            String guid = null;
            ClientMappingEntry mappingEntry = null;

            while (it.hasNext()) {
                mappingEntry = ( (ClientMappingEntry)it.next());
                if (!mappingEntry.isValid()) {
                    continue;
                }
                guid = mappingEntry.getGuid();

                tmp.setKeyValue(guid);

                if (keysList.contains(tmp)) {
                    //
                    // The item satisfies the filter
                    //
                    continue;
                } else {

                    //
                    // We simulate the item on the server has been deleted
                    //
                    AbstractSyncItem dummyItem = new InMemorySyncItem(sources[0],
                        guid,
                        SyncItemState.DELETED);

                    //
                    // If there is already an item with the same key in Bm,
                    // the add isn't required (the item is already handled)
                    //
                    if (!Bm.contains(dummyItem)) {
                        if (log.isTraceEnabled()) {
                            log.trace("The item '" + guid + "' is in the mapping but " +
                                       "it does not match the filter...deleting it");
                        }
                        Bm.add(dummyItem);
                    } else {
                        if (log.isTraceEnabled()) {
                            log.trace("The item '" + guid + "' is in the mapping but " +
                                       " it does not match the filter, should be deleted" +
                                       " but it is already in the Bm list");
                        }
                    }
                }
            }
        }
    }

    /**
     * Check if there are some items to add to the client because if the filter
     * changes some server have to added to the client.
     * So, an item could be not updated but the server has to send an Add command.
     * As borderline case, the client could not have specific any filter.
     *
     * @param sources the syncsources in sync
     * @param allItemsKeys all items on the server inside the filter criteria
     * @param mapping the mapping
     * @param Bm the list of the changed items on the server
     */
    private void checkForItemsToAdd(SyncSource[]  sources,
                                    SyncItemKey[] allItemsKeys,
                                    ClientMapping mapping,
                                    List          Bm) {

        List guidluid = mapping.getMapping();

        if (log.isTraceEnabled()) {
            log.trace("Check the items that satisfy the filter criteria in" +
                       " order to discovery the items to add on the client");
        }

        Iterator itMapping = null;
        String   guid      = null;
        boolean  itemFound = false;
        ClientMappingEntry mappingEntry = null;

        for (int i = 0; i < allItemsKeys.length; i++) {

            itMapping = guidluid.iterator();

            while (itMapping.hasNext()) {
                itemFound = false;
                mappingEntry = (ClientMappingEntry)itMapping.next();
                if (!mappingEntry.isValid()) {
                    continue;
                }
                guid = mappingEntry.getGuid();
                if (guid.equals(allItemsKeys[i].getKeyAsString())) {
                    //
                    // The item is already in the mapping (so it's already
                    // on the client)
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("The item '" + guid + "' is already on the client");
                    }
                    itemFound = true;
                    break;
                }
            }
            if (!itemFound) {
                //
                // The item is on the server and it satisfies the filter
                // but it isn't in the mapping (so it isn't on the client).
                // The server has to send the item with an Add command.
                // In order to do this, we create a Dummy new item
                //

                AbstractSyncItem dummyItem = new InMemorySyncItem(sources[0],
                    allItemsKeys[i].getKeyValue(),
                    SyncItemState.NEW);

                //
                // If there is already an item with the same key in Bm,
                // the addition isn't required (the item is already handled)
                //
                if (!Bm.contains(dummyItem)) {
                    if (log.isTraceEnabled()) {
                        log.trace("The item '" + allItemsKeys[i].getKeyAsString() +
                                   "' isn't on the client...adding it");
                    }

                    Bm.add(dummyItem);
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("The item '" + allItemsKeys[i].getKeyAsString() +
                                   "' isn't on the client" +
                                   " but it's already in the Bm list");
                    }
                }
            }
        }
    }


    /**
     * Resolves the conflict between the server item identifies by the given
     * serverKey and the given clientItem. In the given newServerItem is stored the
     * new server item content (for example if the conflict is resolved
     * with the client data - CONFLICT_RESOLUTION_CLIENT_WINS).
     * If the conflict is resolved using CONFLICT_RESOLUTION_SERVER_WINS or
     * with CONFLICT_RESOLUTION_MERGE_DATA, and the content of the client item is
     * changed, true is returned.
     *
     * @param serverSource the server syncsource
     * @param serverKey the key of the server's item
     * @param clientItem the client item
     * @param resolvingItem the new resolving item
     * @return true is the content of the client item is changed
     * @throws SyncSourceException if an error occurs
     */
    private boolean resolveConflict(SyncSource       serverSource,
                                    SyncItemKey      serverKey,
                                    AbstractSyncItem clientItem,
                                    AbstractSyncItem resolvingItem) throws SyncSourceException {

        if (log.isTraceEnabled()) {
            log.trace("Resolve conflict between server item '" +
                       serverKey.getKeyAsString() +  "' and client item '" +
                       ((AbstractSyncItem)clientItem).getMappedKey().getKeyAsString() +
                       "' for the source '" +
                       serverSource.getSourceURI() + "'");
        }

        int conflictResolution =
                getConflictResolution(serverSource);

        if (conflictResolution == CONFLICT_RESOLUTION_MERGE_DATA &&
            !(serverSource instanceof MergeableSyncSource)) {
            //
            // The strategy is configured to merge the items, but
            // this source doesn't support the merge
            //
            if (log.isWarningEnabled()) {
                log.warn("The strategy is configured to merge the items, but the source " +
                         serverSource.getSourceURI() + " is not a MergeableSyncSource "   +
                         " so the default conflict resolution is used");
            }

            conflictResolution = defaultConflictResolution;
        }

        if (log.isTraceEnabled()) {
            log.trace("Conflict resolution: " +
                      getConflictResolutionDescription(conflictResolution));
        }

        switch (conflictResolution) {

            case CONFLICT_RESOLUTION_CLIENT_WINS:

                SyncItem updatedItem = serverSource.updateSyncItem(clientItem);
                incrementUpdatedItemsServer(serverSource.getSourceURI());

                //
                // We update the newServerItem with the information returned by
                // the updateSyncItem method
                // and we set set the key and mapped key
                //
                resolvingItem.copy(updatedItem);
                resolvingItem.setKey(new SyncItemKey(clientItem.getKey().getKeyAsString()));
                resolvingItem.setMappedKey(clientItem.getMappedKey() != null ?
                    new SyncItemKey(clientItem.getMappedKey().getKeyAsString()) :
                    null);
                resolvingItem.setState(SyncItemState.SYNCHRONIZED);

                //
                // The client data isn't changed
                //
                return false;

            case CONFLICT_RESOLUTION_SERVER_WINS:
                SyncItem serverItem = null;
                try {
                    serverItem = serverSource.getSyncItemFromId(serverKey);
                } catch (SyncSourceException e) {
                    String msg = "Error getting the server item '"
                                 + serverKey
                                 + "' from source "
                                 + serverSource
                                 + ": "
                                 + e.getMessage();

                    log.error(msg, e);

                    throw e;
                }

                resolvingItem.copy(serverItem);
                resolvingItem.setKey(new SyncItemKey(clientItem.getKey().getKeyAsString()));
                resolvingItem.setMappedKey(clientItem.getMappedKey() != null ?
                    new SyncItemKey(clientItem.getMappedKey().getKeyAsString()) :
                    null);
                resolvingItem.setState(SyncItemState.UPDATED);

                //
                // The client data is changed
                //
                return true;

            case CONFLICT_RESOLUTION_MERGE_DATA:

                //
                // We keep the state of the clientItem because the ss
                // can change it (but we don't want it!!!)
                //
                char state = clientItem.getState();

                boolean itemChanged = false;

                try {
                    itemChanged = ((MergeableSyncSource) serverSource).mergeSyncItems(
                                      serverKey,
                                      clientItem
                                  );
                } catch (SyncSourceException e) {
                    String msg = "Error merging the client item "
                                 + clientItem.getKey()
                                 + " with server item "
                                 + serverKey.getKeyAsString()
                                 + " from source "
                                 + serverSource
                                 + ": "
                                 + e.getMessage();

                    log.error(msg, e);

                    throw e;
                }

                //
                // We restore the state of the item
                //
                clientItem.setState(state);

                // the clientItem is copied into the resolvingItem because
                // the mergeSyncItems method changes the clientItem that acually
                // represent the resolvingItem
                resolvingItem.copy(clientItem);

                // we don't count it as an update because in a NN conflict, as
                // often occurs in slow syncs, the items are actually not
                // updated, so we prefer to not count updated instead of
                // counting updates that are not actually updates
                // incrementUpdatedItemsServer(serverSource.getSourceURI());

                if (itemChanged) {
                    //
                    // We set the newServerItem state at 'U'
                    // so his content is sent to the client
                    //
                    resolvingItem.setState(SyncItemState.UPDATED);
                } else {
                    resolvingItem.setState(SyncItemState.SYNCHRONIZED);
                }

                return itemChanged;

            default:

        }

        return false;
    }

    /**
     * Returns a description of the given conflictResolution
     * @param conflictResolution the conflictResolution
     * @return a description of the given conflictResolution
     */
    protected String getConflictResolutionDescription(int conflictResolution) {
        switch (conflictResolution) {
            case CONFLICT_RESOLUTION_CLIENT_WINS:
                return "CONFLICT_RESOLUTION_CLIENT_WINS";
            case CONFLICT_RESOLUTION_SERVER_WINS:
                return "CONFLICT_RESOLUTION_SERVER_WINS";
            case CONFLICT_RESOLUTION_MERGE_DATA:
                return "CONFLICT_RESOLUTION_MERGE_DATA";
            default:
                return "Unknown conflict resolution (" + conflictResolution + ")";
        }
    }


    /**
     * Updates the mapping dispatching the real update to the right method
     * according with the operation type.
     * @param slowSync boolean
     * @param clientMapping ClientMapping
     * @param lastAnchor String
     * @param operation SyncOperation
     */
    protected void updateMapping(boolean       slowSync,
                               ClientMapping clientMapping,
                               String        lastAnchor,
                               SyncOperation operation) {

        char op = operation.getOperation();

        if (operation.isDeleteForced()) {

            String   guid  = null;
            SyncItem itemB = operation.getSyncItemB();

            guid = itemB.getKey().getKeyAsString();
            clientMapping.removeMappedValuesForGuid(guid, true);

       } else if (slowSync &&
                  (op != SyncOperation.DELETE) && (op != SyncOperation.NEW)) {
            //
            //this is the case of slow sync and without mappings into db
            //
            String   guid  = null;
            String   luid  = null;

            AbstractSyncItem itemA = ((AbstractSyncItem)operation.getSyncItemA());
            SyncItem     itemB = operation.getSyncItemB();

            if (itemA != null && itemB != null) {

                guid = itemB.getKey().getKeyAsString();

                SyncItemKey mappedKeyA = itemA.getMappedKey();
                if (mappedKeyA != null) {
                    luid = mappedKeyA.getKeyAsString();
                } else {
                    luid = itemA.getKey().getKeyAsString();
                }

                if (operation.isAOperation()) {
                    clientMapping.updateMapping(guid, luid, lastAnchor);
                } else if (operation.isBOperation()) {
                    clientMapping.updateMapping(guid, luid, lastAnchor);
                } else {
                    //
                    // This case happens when during a slow sync the client sends
                    // a item twin of a server item but the merging doesn't produce
                    // a new content. So, no operations are required on A (client)
                    // and no operations are required on B (server)
                    //
                    clientMapping.updateMapping(guid, luid, lastAnchor);
                }

                return;
            }

        } else if(op == SyncOperation.DELETE) {

            updateMappingDelete(clientMapping, lastAnchor, operation);

        } else if (op == SyncOperation.NEW) {

            updateMappingNew(clientMapping, lastAnchor, operation);

        } else if (op == SyncOperation.UPDATE) {

            updateMappingUpdate(clientMapping, lastAnchor, operation);

        } else if (op == SyncOperation.CONFLICT) {

            updateMappingConflict(clientMapping, lastAnchor, operation);

        }
    }

    /**
     * Updates the mapping for a 'New' operation
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    protected void updateMappingNew(ClientMapping clientMapping,
                                  String        lastAnchor,
                                  SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        if (operation.isBOperation()) {
            luid = itemA.getKey().getKeyAsString();
            guid = itemB.getKey().getKeyAsString();
            clientMapping.updateMapping(guid, luid, lastAnchor);
        }
        if (operation.isAOperation()) {
            guid = itemB.getKey().getKeyAsString();
            clientMapping.updateMapping(guid, guid, null);
        }
    }

    /**
     * Updates the mapping for a 'UPDATE' operation. This is required because an
     * update operation on the server can change the item's key (GUID)
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    protected void updateMappingUpdate(ClientMapping clientMapping,
                                     String        lastAnchor,
                                     SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        if (operation.isBOperation()) {
            luid = itemA.getMappedKey().getKeyAsString();
            guid = itemB.getKey().getKeyAsString();

            String oldGuid = clientMapping.getMappedValueForLuid(luid);
            if (oldGuid != null && !oldGuid.equals(guid)) {
                clientMapping.removeMappedValuesForGuid(oldGuid, false);
                clientMapping.updateMapping(guid, luid, lastAnchor);
            }
        }
    }

    /**
     * Updates the mapping for a 'Delete' operation
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    protected void updateMappingDelete(ClientMapping clientMapping,
                                     String        lastAnchor,
                                     SyncOperation operation) {

        AbstractSyncItem itemB = null;

        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String guid = null;

        guid = itemB.getKey().getKeyAsString();
        if (operation.isAOperation()) {
            clientMapping.removeMappedValuesForGuid(guid, true);
        } else {
            clientMapping.removeMappedValuesForGuid(guid, false);
        }
    }


    /**
     * Updates the mapping for a 'Conflict' operation dispatching the
     * real update to the right method according with the conflict type.
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    protected void updateMappingConflict(ClientMapping clientMapping,
                                       String        lastAnchor,
                                       SyncOperation operation) {

        String conflictType = ((SyncConflict)operation).getType();

        if (SyncConflict.STATE_DELETED_UPDATED.equals(conflictType) ||
            SyncConflict.STATE_DELETED_NEW.equals(conflictType) ) {

            updateMappingConflictDU(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_UPDATED_DELETED.equals(conflictType)) {

            updateMappingConflictUD(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_UPDATED_UPDATED.equals(conflictType)) {

            updateMappingConflictUU(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_NEW_NEW.equals(conflictType)) {

            updateMappingConflictNN(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_NEW_UPDATED.equals(conflictType)) {

            updateMappingConflictNU(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_UPDATED_NEW.equals(conflictType)) {

            updateMappingConflictUN(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_NEW_SYNCHRONIZED.equals(conflictType)) {

            updateMappingConflictNS(clientMapping, lastAnchor, operation);

        } else if (SyncConflict.STATE_DELETED_DELETED.equals(conflictType)) {

            updateMappingConflictDD(clientMapping, lastAnchor, operation);


        } else {

            //
            // These cases shuold never happen
            //
            assert (!SyncConflict.STATE_DELETED_CONFLICT.equals(conflictType));
            assert (!SyncConflict.STATE_UPDATED_NEW.equals(conflictType));
            assert (!SyncConflict.STATE_UPDATED_CONFLICT.equals(conflictType));
            assert (!SyncConflict.STATE_NEW_DELETED.equals(conflictType));
            assert (!SyncConflict.STATE_NEW_CONFLICT.equals(conflictType));
            assert (!SyncConflict.STATE_NONE_CONFLICT.equals(conflictType));
            assert (!SyncConflict.STATE_CONFLICT_DELETED.equals(conflictType));
            assert (!SyncConflict.STATE_CONFLICT_UPDATED.equals(conflictType));
            assert (!SyncConflict.STATE_CONFLICT_NEW.equals(conflictType));
            assert (!SyncConflict.STATE_CONFLICT_CONFLICT.equals(conflictType));
        }

    }


    /**
     * Updates the mapping for a 'DU' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictDU(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemB = null;

        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String guid = null;

        if (!operation.isAOperation() && !operation.isBOperation()) {
            //
            // This happen when there is a conflict DU and we are
            // using some filters so we have to remove the mapping
            // (see Sync4jStragegy, method execConflictDU)
            //
            guid = itemB.getKey().getKeyAsString();
            clientMapping.removeMappedValuesForGuid(guid, false);
        } else if (operation.isBOperation()) {
            //
            // This happen when the conflict resolution is CLIENT_WINS
            // (see Sync4jStragegy, method execConflictDU)
            //
            guid = itemB.getKey().getKeyAsString();
            clientMapping.removeMappedValuesForGuid(guid, false);
        } else {
            //
            // This happen when the conflict resolution is SERVER_WINS or
            // MERGE_DATA and the item is inside the filter criteria
            // (see Sync4jStragegy, method execConflictDU)
            //
            guid = itemB.getKey().getKeyAsString();
            clientMapping.updateMapping(guid, guid, null);
        }

    }

    /**
     * Updates the mapping for a 'UD' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictUD(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        SyncItemKey mappedKey = itemA.getMappedKey();
        if (mappedKey != null) {
            luid = mappedKey.getKeyAsString();
        } else {
            luid = itemA.getKey().getKeyAsString();
        }

        String oldGuid = itemA.getKey().getKeyAsString();

        if (operation.isAOperation()) {
            //
            // We are in this case if the strategy is configurated with
            // CONFLICT_RESOLUTION_SERVER_WINS: in this case we have deleted
            // the item on the client
            // (see Sync4jStragegy, method execConflictUD)
            //
            clientMapping.removeMappedValuesForGuid(oldGuid, true);
            return;
        }

        //
        // With CONFLICT_RESOLUTION_CLIENT_WINS or CONFLICT_RESOLUTION_MERGE_DATA,
        // the server calls the method addSyncItem on the server ss.
        // This method can create a new item with a new GUID.
        // But in the mapping we have LUID-Old GUID.
        // So, we have to remove it before to insert the new mapping
        //
        clientMapping.removeMappedValuesForGuid(oldGuid, false);

        //
        // Insert the new mapping
        //
        guid = itemB.getKey().getKeyAsString();
        clientMapping.updateMapping(guid, luid, lastAnchor);
    }


    /**
     * Updates the mapping for a 'UU' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictUU(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        SyncItemKey mappedKey = itemA.getMappedKey();
        if (mappedKey != null) {
            luid = mappedKey.getKeyAsString();
        } else {
            luid = itemA.getKey().getKeyAsString();
        }
        guid = itemB.getKey().getKeyAsString();
        if (operation.isAOperation()) {
            clientMapping.updateMapping(guid, luid, null);
        } else {
            clientMapping.updateMapping(guid, luid, lastAnchor);
        }
    }


    /**
     * Updates the mapping for a 'NN' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictNN(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        luid = itemA.getKey().getKeyAsString();
        guid = itemB.getKey().getKeyAsString();
        if (operation.isAOperation()) {
            clientMapping.updateMapping(guid, luid, null);
        } else {
            clientMapping.updateMapping(guid, luid, lastAnchor);
        }
    }


    /**
     * Updates the mapping for a 'NU' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictNU(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem)operation.getSyncItemA();
        itemB = (AbstractSyncItem)operation.getSyncItemB();

        String luid = null;
        String guid = null;

        luid = itemA.getMappedKey().getKeyAsString();
        guid = itemB.getKey().getKeyAsString();

        clientMapping.updateMapping(guid, luid, lastAnchor);

    }

    /**
     * Updates the mapping for a 'NN' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictUN(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem) operation.getSyncItemA();
        itemB = (AbstractSyncItem) operation.getSyncItemB();

        String luid = null;
        String guid = null;

        SyncItemKey mappedKey = itemA.getMappedKey();
        if (mappedKey != null) {
            luid = mappedKey.getKeyAsString();
        } else {
            luid = itemA.getKey().getKeyAsString();
        }
        guid = itemB.getKey().getKeyAsString();
        if (operation.isAOperation()) {
            clientMapping.updateMapping(guid, luid, null);
        } else {
            clientMapping.updateMapping(guid, luid, lastAnchor);
        }
    }

    /**
     * Updates the mapping for a 'NS' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictNS(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        AbstractSyncItem itemA = null;
        AbstractSyncItem itemB = null;

        itemA = (AbstractSyncItem) operation.getSyncItemA();
        itemB = (AbstractSyncItem) operation.getSyncItemB();

        String luid = null;
        String guid = null;

        SyncItemKey mappedKey = itemA.getMappedKey();
        if (mappedKey != null) {
            luid = mappedKey.getKeyAsString();
        } else {
            luid = itemA.getKey().getKeyAsString();
        }
        guid = itemB.getKey().getKeyAsString();
        if (operation.isAOperation()) {
            clientMapping.updateMapping(guid, luid, null);
        } else {
            clientMapping.updateMapping(guid, luid, lastAnchor);
        }
    }

    /**
     * Updates the mapping for a 'DD' conflict
     * @param clientMapping the current mapping
     * @param lastAnchor the last anchor of this sync
     * @param operation the operation
     */
    private void updateMappingConflictDD(ClientMapping clientMapping,
                                         String        lastAnchor,
                                         SyncOperation operation) {

        SyncItem itemB = null;

        itemB = operation.getSyncItemB();

        String guid = null;

        guid = itemB.getKey().getKeyAsString();
        clientMapping.removeMappedValuesForGuid(guid, false);
    }


    /**
     * Converts the parentKey of the given item from LUID to GUID
     * (the server syncsource needs the GUID) using the given mapping
     * @param mapping the mapping
     * @param item the item
     */
    protected void fixParent(Map      mapping,
                           SyncItem item) {

        SyncItemKey parentKey = item.getParentKey();
        if (mapping == null) {
            return;
        }
        if (parentKey != null) {
            String sParentKey = parentKey.getKeyAsString();
            //
            // We have to transform the LUID in GUID
            //
            if (mapping.containsValue(sParentKey)) {

                Iterator itMap = mapping.keySet().iterator();
                String   key   = null;
                String   value = null;
                while (itMap.hasNext()) {
                    key   = (String)itMap.next();      // GUID
                    value = (String)mapping.get(key);  // LUID
                    if (value.equals(sParentKey)) {
                        parentKey.setKeyValue(key);
                    }
                }
            }
        }
    }

    /**
     * Converts the parentKey of the itemA from LUID to GUID
     * (the server syncsource needs the GUID) using the given mapping
     * @param mapping the mapping
     * @param operation the operation
     */
    private void fixParent(ClientMapping   mapping,
                           SyncOperation operation) {

        SyncItem item = operation.getSyncItemA();

        SyncItemKey parentKey = item.getParentKey();
        if (parentKey != null) {
            //
            // We have to transform the LUID in GUID
            //
            String guid = mapping.getMappedValueForLuid(parentKey.getKeyAsString());
            if (guid != null) {
                parentKey.setKeyValue(guid);
            }
        }
    }

    /**
     * Returns the first key found in the given keys not in the given
     * mapping (as GUID).
     * @param mapping Map
     * @param keys SyncItemKey[]
     * @return the first key found in the given keys not in the given
     *         mapping as GUID. If no mapped key is found, <code>null</code> is
     *         returned.
     */
    protected SyncItemKey findNotMappedItem(Map mapping, SyncItemKey[] keys) {
        if (keys == null || keys.length == 0) {
            return null;
        }
        if (mapping == null) {
            if (keys.length > 0) {
                return keys[0];
            }
        }
        int numKeys = keys.length;
        String luid = null;
        for (int i=0; i<numKeys; i++) {
            luid = (String)mapping.get(keys[i].getKeyAsString());
            if (luid == null) {
                return keys[i];
            }
        }
        return null;
    }

    private void incrementAddedItemsOnClient(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementAddedItemsOnClient();
    }

    private void incrementAddedItemsOnServer(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementAddedItemsOnServer();
    }

    private void incrementDeletedItemsServer(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementDeletedItemsServer();
    }

    private void incrementDeletedItemsClient(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementDeletedItemsClient();
    }

    private void incrementUpdatedItemsClient(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementUpdatedItemsClient();
    }

    private void incrementUpdatedItemsServer(String uri) {
        SyncStatistic syncStatistic = syncStatistics.get(uri);
        if (syncStatistic == null) {
            syncStatistic = new SyncStatistic();
            syncStatistics.put(uri, syncStatistic);
        }
        syncStatistic.incrementUpdatedItemsServer();
    }
}

