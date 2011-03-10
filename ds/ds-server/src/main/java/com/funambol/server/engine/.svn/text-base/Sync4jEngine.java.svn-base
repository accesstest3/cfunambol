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

import java.lang.reflect.Method;

import java.security.Principal;

import java.sql.Timestamp;

import java.util.*;
import java.util.Date;
import java.util.Map;

import com.funambol.framework.config.ConfigurationException;
import com.funambol.framework.core.*;
import com.funambol.framework.database.Database;
import com.funambol.framework.engine.*;
import com.funambol.framework.engine.source.*;
import com.funambol.framework.engine.transformer.DataTransformerManager;
import com.funambol.framework.engine.transformer.TransformationInfo;
import com.funambol.framework.filter.*;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.logging.LogContext;
import com.funambol.framework.protocol.CommandIdGenerator;
import com.funambol.framework.security.Officer;
import com.funambol.framework.security.SecurityConstants;
import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.*;
import com.funambol.framework.server.inventory.DeviceInventory;
import com.funambol.framework.server.inventory.DeviceInventoryException;
import com.funambol.framework.server.store.*;
import com.funambol.framework.server.SyncEvent;
import com.funambol.framework.tools.Base64;
import com.funambol.framework.tools.MD5;

import com.funambol.server.admin.AdminException;
import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;
import com.funambol.server.config.ConfigurationConstants;
import com.funambol.server.config.ServerConfiguration;
import com.funambol.server.session.SessionHandler;
import com.funambol.server.session.SyncSourceState;

/**
 * This is the Funambol implementation of the synchronization engine.
 *
 * LOG NAME: funambol.engine
 *
 * @version $Id: Sync4jEngine.java,v 1.6 2008-06-18 10:24:37 testa Exp $
 */
public class Sync4jEngine
extends AbstractSyncEngine
implements java.io.Serializable, ConfigurationConstants {

    // --------------------------------------------------------------- Constants

    /**
     * The name of the used logger
     */
    public static final String LOG_NAME = "engine";

    /**
     * The name of the event logger
     */
    public static final String EVENT_LOG_NAME = "funambol.push";
    // ------------------------------------------------------------ Private data

    /**
     * The configuration properties
     */
    private Configuration configuration = null;

    /**
     * Returns the configured configuration object
     * @return the configuration object
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * The security officer
     */
    protected Officer officer = null;

    /**
     * Returns the officer
     * @return the officer
     */
    public Officer getOfficer() {
        return this.officer;
    }

    /**
     * The underlying persistent store
     */
    protected PersistentStore store = null;

    /** Getter for property store.
     * @return Value of property store.
     *
     */
    public PersistentStore getStore() {
        return store;
    }

    /** Setter for property store.
     * @param store New value of property store.
     *
     */
    public void setStore(PersistentStore store) {
        this.store = store;
    }

    /**
     * The underlying userManager
     */
    private UserManager userManager = null;

    /**
     * Getter for property store.
     * @return Value of property store.
     */
    public UserManager getUserManager() {
        return userManager;
    }

    /**
     * Setter for property userManager.
     * @param userManager New value of property userManager.
     */
    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * The device inventory
     */
    private DeviceInventory deviceInventory = null;

    /**
     * Returns the device inventory
     * @return the device inventory
     */
    public DeviceInventory getDeviceInventory() {
        return this.deviceInventory;
    }

    /**
     * The DataTransformerManager
     */
    protected DataTransformerManager dataTransformerManager = null;

    /**
     * Returns the data transformer manager
     * @return  the data transformer manager
     */
    public DataTransformerManager getDataTransformerManager() {
        return this.dataTransformerManager;
    }

    /**
     * The sources this engine deals with
     */
    protected List clientSources = new ArrayList();

    /**
     * The server sources
     */
    private Map serverSources  = new HashMap();

    /**
     * Contains the filter to use in the sync. The map's key is the source uri
     * and the value is a FilterClause
     */
    private Map  filters        = new HashMap();

    /**
     * The modification objects of the last synchronization
     */
    protected Map operations = null;

    /**
     * The status of the last synchronization modifications
     */
    protected List status = null;

    /**
     * Logger
     */
    public transient FunambolLogger log = null;

    /**
     * Sets the SyncML Protocol version. It is used to create the server
     * credential with MD5 authentication.
     */
    private String syncMLVerProto = null;

    /**
     * Sets the syncml protocol version used in the current sync
     * @param syncMLVerProto the protocol version
     */
    public void setSyncMLVerProto(String syncMLVerProto) {
        this.syncMLVerProto = syncMLVerProto;
    }

    /**
     * The principal that is syncing
     */
    protected Sync4jPrincipal principal = null;

    /**
     * Sets the principal that is syncing
     * @param principal the principal that is syncing
     */
    public void setPrincipal(Sync4jPrincipal principal) {
        this.principal = principal;
    }

    /**
     * Is the current message the last message ?
     */
    protected boolean isLastMessage  = false;

    /**
     * Sets isLastMessage
     * @param isLastMessage is the last message ?
     */
    public void setLastMessage(final boolean isLastMessage) {
        this.isLastMessage = isLastMessage;
    }

    /**
     * Is the last message?
     * @return is the last message ?
     */
    public boolean isLastMessage() {
        return this.isLastMessage;
    }

    /**
     * The SyncSoure states
     */
    protected SyncSourceState syncSourceState;

    /**
     * The existing LUID-GUID mapping. It is used and modify by sync()
     */
    protected Map clientMappings;


    private FunambolLogger logPush;

    /**
     * Holds the SyncItemFactoriy instances indexed by sourceURI
     */
    private Map<String, SyncItemFactory> syncItemFactoryCache =
            new HashMap<String, SyncItemFactory>();

    // ------------------------------------------------------------ Constructors

    /**
     * To allow deserializazion of subclasses.
     */
    public Sync4jEngine() {

        syncSourceState = new SyncSourceState();

        log = FunambolLoggerFactory.getLogger(LOG_NAME);
        logPush = FunambolLoggerFactory.getLogger(EVENT_LOG_NAME);

        this.configuration = Configuration.getConfiguration();

        //
        // Set the underlying persistent store
        //
        store = configuration.getStore();

        //
        // Set the user manager
        //
        userManager = configuration.getUserManager();

        //
        // Set the security officer
        //
        officer = configuration.getOfficer();

        //
        // Set the inventory
        //
        deviceInventory = configuration.getDeviceInventory();

        //
        // Set the dataTransformerManager
        //
        dataTransformerManager = configuration.getDataTransformerManager();

        //
        // Set the SyncStrategy object to use for comparisons
        //
        SyncStrategy strategy = configuration.getStrategy();

        setStrategy(strategy);

    }

    /**
     * To allow deserializazion of subclasses.
     */
    public Sync4jEngine(Configuration _configuration) {
        this();
    }

    // ------------------------------------------------ Configuration properties

     /**
     * The id generator
     */
    protected CommandIdGenerator cmdIdGenerator = null;

    /**
     * Set the id generator for commands
     *
     * @param cmdIdGenerator the id generator
     */
    public void setCommandIdGenerator(CommandIdGenerator cmdIdGenerator) {
        this.cmdIdGenerator = cmdIdGenerator;
    }


    /**
     * Return the id generator
     *
     * @return the id generator
     */
    public CommandIdGenerator getCommandIdGenerator() {
        return cmdIdGenerator;
    }

    // ------------------------------------------------------ Runtime properties

    /**
     * The database to be synchronized
     */
    protected Map dbs = new LinkedHashMap();

    /**
     * Sets the dbs
     * @param dbs the dbs to set
     */
    public void setDbs(Map dbs) {
        if (this.dbs != null) {
            this.dbs.clear();
        } else {
            this.dbs = new LinkedHashMap(dbs.size());
        }
        this.dbs.putAll(dbs);
    }

    /**
     * Sets the dbs
     * @param dbs the dbs to set
     */
    public void setDbs(Database[] dbs) {
        if (this.dbs == null) {
            this.dbs = new LinkedHashMap(dbs.length);
        }

        for (int i=0; ((dbs != null) && (i<dbs.length)); ++i) {
            this.dbs.put(dbs[i].getName(), dbs[i]);
        }
    }

    /**
     * Returns the dbs
     * @return the dbs
     */
    public Database[] getDbs() {
        if (this.dbs == null) {
            return new Database[0];
        }

        Iterator i = this.dbs.values().iterator();

        Database[] ret = new Database[this.dbs.size()];

        int j = 0;
        while(i.hasNext()) {
            ret[j++] = (Database)i.next();
        }

        return ret;
    }

    /**
     * Returns the Database with the given uri
     * @param uri String
     * @return Database
     */
    public Database getDb(String uri) {
        if (this.dbs == null) {
            return null;
        }
        return (Database)(dbs.get(uri));
    }

    /**
     * Timestamp of the beginning of this Sync
     */
    protected Timestamp syncTimestamp;

    /**
     * Setter for syncTimestamp
     *
     * @param syncTimestamp new value
     */
    public void setSyncTimestamp(Date syncTimestamp) {
        //
        // Just for now: we have to change everything to Date
        //
        this.syncTimestamp = new Timestamp(syncTimestamp.getTime());
    }


    // ---------------------------------------------------------- Public methods

    /**
     * Fires and manages the synchronization process.
     *
     * @param principal the principal who is requesting the sync
     *
     * @throws Sync4jException in case of error
     */
    public void sync(final Sync4jPrincipal principal)
    throws Sync4jException {

        if (log.isInfoEnabled()) {
            log.info("Starting synchronization ...");
        }

        SyncStrategy syncStrategy = getStrategy();

        //
        // Being this a concrete implementation of the Sync4j engine
        // we know already that client sources are of type MemorySyncSource
        //
        MemorySyncSource clientSource = null;
        SyncSource       serverSource = null;
        Database         db           = null;

        //
        // Now process clientSources
        //
        operations = new HashMap();
        status     = new ArrayList();

        FilterClause filter = null;
        String       uri    = null;
        ListIterator clientSourcesIterator = clientSources.listIterator();
        SyncContext  syncContext = null;
        while(clientSourcesIterator.hasNext()) {
            clientSource = (MemorySyncSource)clientSourcesIterator.next();
            uri          = clientSource.getSourceURI();
            db           = (Database)dbs.get(uri);

            LogContext.setSourceURI(uri);

            if (log.isTraceEnabled()) {
                log.trace("Synchronizing " + db.getName() + " (sync type: " + db.getMethod() + ")");

                log.trace("SyncSource state of '"
                          + uri
                          + "' is "
                          + syncSourceState.getStateName(uri)
                );
            }

            //
            // If the source state is the error state, skip it
            //
            if (syncSourceState.isError(uri)) {
                continue;
            }

            serverSource = getServerSource(uri);

            //
            // Before starting the real sync process, we have apply the required
            // transformations on the client item using the DataTransformerManager
            //
            applyDataTransformationsOnIncomingItems(clientSource.getNewSyncItems());
            applyDataTransformationsOnIncomingItems(clientSource.getUpdatedSyncItems());

            SyncSource[] sources = new SyncSource[] {
                serverSource, clientSource
            };

            int conflictResolution = syncStrategy.getConflictResolution(serverSource);

            //
            // I can't remember why this is done. If somebody reads this and
            // knows the reason, please UPDATE this comment!
            // Nick: I'm not sure...maybe because we can have different credential
            // for any db, so we have to set the id of the principal of the db.
            //
            Sync4jPrincipal p = (Sync4jPrincipal)db.getPrincipal();
            p.setId(principal.getId());


            Timestamp since = null;
            //
            // if the source is in the CONFIGURED state, call beginSync(). If
            // beginSync() fails, skip
            //
            if (syncSourceState.isConfigured(uri)) {
                try {
                    //
                    // Get the filter to use
                    //
                    filter = (FilterClause)filters.get(uri);
                    if (filter != null) {
                        //
                        // Replace the LUID in the IDClause with the GUID
                        //
                        fixIDClause(filter, getMapping(uri));
                    }

                    //
                    // We map the conflict resolution defined in the strategy
                    // in the value used in the SyncContext.
                    //
                    int contextConflictResolution = 0;
                    switch (conflictResolution) {
                        case SyncStrategy.CONFLICT_RESOLUTION_SERVER_WINS:
                            contextConflictResolution =
                                SyncContext.CONFLICT_RESOLUTION_SERVER_WINS;
                            break;
                        case SyncStrategy.CONFLICT_RESOLUTION_CLIENT_WINS:
                            contextConflictResolution =
                                SyncContext.CONFLICT_RESOLUTION_CLIENT_WINS;
                            break;
                        case SyncStrategy.CONFLICT_RESOLUTION_MERGE_DATA:
                            contextConflictResolution =
                                SyncContext.CONFLICT_RESOLUTION_MERGE_DATA;
                            break;
                    }

                    //
                    // Read the last timestamp from the persistent store
                    //
                    if (   (db.getMethod()     == AlertCode.SLOW)
                        || (db.getMethod()     == AlertCode.REFRESH_FROM_SERVER)
                        || (db.getMethod()     == AlertCode.REFRESH_FROM_CLIENT)
                        || (db.getStatusCode() == StatusCode.REFRESH_REQUIRED)
                        || (db.getMethod() == AlertCode.SMART_ONE_WAY_FROM_CLIENT)) {

                        // no 'since' settings required

                    } else {

                        // fast sync
                        since = new Timestamp(db.getSyncStartTimestamp());

                    }


                    syncContext = new SyncContext(p,
                                                  db.getMethod(),
                                                  filter,
                                                  db.getSourceQuery(),
                                                  contextConflictResolution,
                                                  since,
                                                  syncTimestamp);

                    serverSource.beginSync(syncContext);
                    syncSourceState.moveToSyncing(uri);

                    //
                    // Be carefully when you change this log because is
                    // used to notify the start sync session event.
                    //
                    if (logPush.isTraceEnabled()) {
                        logPush.trace(SyncEvent.createStartSyncEvent(
                                LogContext.getUserName(),
                                LogContext.getDeviceId(),
                                LogContext.getSessionId(),
                                String.valueOf(db.getMethod()),
                                db.getName(),
                                "Start '" + db.getName() + "' sync '" + db.getMethod() + "' " + "for '" + LogContext.getSessionId() + "' "));
                    }

                } catch (SyncSourceException e) {
                    log.error("Error starting the sync", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }
            }

            try {
                clientSource.commitSync();
            } catch (SyncSourceException e) {
                //
                // Hey, this should never happen!
                //
                syncSourceState.moveToError(uri);
                setDBError(db, e);
                continue;
            }

            int syncFilterType = -1;
            if (filter == null) {
               syncFilterType = SyncStrategy.SYNC_WITHOUT_FILTER;
            } else {
                if (filter.isInclusive()) {
                    syncFilterType = SyncStrategy.SYNC_WITH_INCLUSIVE_FILTER;
                } else {
                    syncFilterType = SyncStrategy.SYNC_WITH_EXCLUSIVE_FILTER;
                }
            }

            ClientMapping mapping = getMapping(uri);
            String lastAnchor = EngineHelper.getLastAnchor(uri, dbs);

            //
            // Note: in the case of fast sync, we do prepareSync and sync as
            // soon as we can. If instead the slow sync is the case, we do the
            // sync preparation and the sync only at the end of the process,
            // when all items from the client have been sent.
            // Plus, in the case of a slow sync, the status commands in response
            // of a modification command is usually 200, since no real handling
            // is done.
            //
            if (   (db.getMethod()     == AlertCode.SLOW)
                || (db.getMethod()     == AlertCode.REFRESH_FROM_SERVER)
                || (db.getMethod()     == AlertCode.REFRESH_FROM_CLIENT)
                || (db.getStatusCode() == StatusCode.REFRESH_REQUIRED)) {

                try {

                    operations.put(uri,
                                    syncStrategy.prepareSlowSync(sources,
                                                                 p,
                                                                 mapping,
                                                                 syncFilterType,
                                                                 syncTimestamp,
                                                                 isLastMessage)
                    );
                } catch (SyncException e) {
                    log.error("Error preparing slow sync", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }

                try {
                    status.addAll(
                        Arrays.asList(
                            syncStrategy.sync(sources,
                                              true,  // this is a slow sync
                                              mapping,
                                              lastAnchor,
                                              (SyncOperation[])operations.get(uri),
                                              syncFilterType)
                        )
                    );
                } catch (SyncException e) {
                    log.error("Error performing synchronization", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }

            } else if (db.getMethod() == AlertCode.SMART_ONE_WAY_FROM_CLIENT) {

                try {
                    SyncOperation[] operation = syncStrategy.prepareSmartOneWayFromClient(
                            sources,
                            p,
                            mapping,
                            syncFilterType,
                            syncTimestamp,
                            isLastMessage);
                    operations.put(uri, operation);
                } catch (SyncException e) {
                    log.error("Error preparing fast sync", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }

                //
                // synchronize the sources and get the statuses
                //
                try {
                    SyncOperationStatus[] statusArray = syncStrategy.sync(sources,
                            false, // this isn't a slow sync
                            mapping,
                            lastAnchor,
                            (SyncOperation[]) operations.get(uri),
                            syncFilterType);
                    status.addAll(Arrays.asList(statusArray));
                } catch (SyncException e) {
                    log.error("Error performing synchronization", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }

            } else {

                //
                // During a fast sync,  if the client specifies a
                // exclusive filter, the server has to delete on the client all
                // items that don't satisfy the filter. The server knows
                // the items on the client using the mapping; so, we have to
                // call the method prepareFastSync passing the mapping
                //

                try {
                    operations.put(uri,
                                   syncStrategy.prepareFastSync(sources,
                                                                p,
                                                                mapping,
                                                                syncFilterType,
                                                                since,
                                                                syncTimestamp,
                                                                EngineHelper.getLastAnchor(uri, dbs),
                                                                isLastMessage)
                    );
                } catch (SyncException e) {
                    log.error("Error preparing fast sync", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }

                try {
                    //
                    // Now synchronize the sources
                    //
                    status.addAll(
                        Arrays.asList(
                            syncStrategy.sync(sources,
                                              false,   // this isn't a slow sync
                                              mapping,
                                              lastAnchor,
                                              (SyncOperation[])operations.get(uri),
                                              syncFilterType)
                        )
                    );
                } catch (SyncException e) {
                    log.error("Error performing synchronization", e);
                    syncSourceState.moveToError(uri);
                    setDBError(db, e);
                    continue;
                }
            }

            //
            // Let's try to commit the sync now!
            //
            try {
                serverSource.commitSync();
                syncSourceState.moveToCommitted(uri);
            } catch (SyncSourceException e) {
                syncSourceState.moveToError(uri);
                setDBError(db, e);
                continue;
            }

            //
            // After processing, the database status code is set to OK
            //
            db.setStatusCode(StatusCode.OK);

        }  // next i (client source)
    }

    /**
     * Closes all synchronization.
     */
    public void endSync() {

        if (log.isTraceEnabled()) {
            log.trace("Closing synchronization process");
        }

        //
        // Call endSync for each SyncSource if it is in syncing or in commited
        // or in error state
        //
        Iterator   it           = clientSources.iterator();
        SyncSource serverSource = null;
        String     uri          = null;
        while (it.hasNext()) {
            uri = ((MemorySyncSource)it.next()).getSourceURI();

            LogContext.setSourceURI(uri);

            if (syncSourceState.isSyncing(uri)   ||
                syncSourceState.isCommitted(uri) ||
                syncSourceState.isError(uri)      )  {

                serverSource = getServerSource(uri);
                try {
                    if (log.isTraceEnabled()) {
                        log.trace("Ending sync for database '" + uri + "'");
                    }
                    serverSource.endSync();
                    syncSourceState.moveToIdle(uri);
                } catch (SyncSourceException e) {
                    log.error("Error ending the sync", e);
                }
            } else {
                if (log.isTraceEnabled()) {
                    log.trace("Database '" + uri + "' in state " + syncSourceState.getStateName(uri));
                }
            }
        }

        LogContext.setSourceURI(null);

        try {
            getStrategy().endSync();
        } catch (SyncException e) {
            log.error("Error calling Sync4jStrategy.endSync()", e);
        }
    }

    /**
     * Called to permanently commit the synchronization. It does the following:
     * <ul>
     *  <li>persists the <i>last</i> timestamp to the database for the sources
     *      successfully synchronized
     * </ul>
     */
    public void commit(int currentState, SyncTimestamp nextTimestamp, long id) {

        LastTimestamp last = null;

        Database databases[] = getDbs();

        for (int i = 0; (databases != null) && (i < databases.length); ++i) {
            Database db = databases[i];

            LogContext.setSourceURI(db.getName());

            if (db.getStatusCode() != StatusCode.OK &&
                db.getStatusCode() != AlertCode.SUSPEND) {
                //
                // This database is in error. Do not commit it.
                //
                continue;
            }
            if (currentState != SessionHandler.STATE_SESSION_SUSPENDED) {
                last = new LastTimestamp(
                        id,
                        db.getName(),
                        db.getMethod(), // syncType
                        db.getStatusCode(),
                        db.getAnchor().getNext(),
                        db.getServerAnchor().getNext(),
                        nextTimestamp.start,
                        nextTimestamp.end
                       );
                //
                // Be carefully when you change this log because is
                // used to notify the start sync session event.
                //
                if (logPush.isTraceEnabled()) {
                    int numAddedItems = 0;
                    int numDeletedItems = 0;
                    int numUpdatedItems = 0;

                    SyncStrategy strategy = getStrategy();
                    if (strategy instanceof Sync4jStrategy) {
                        SyncStatistic syncStatistics =
                                ((Sync4jStrategy) strategy).getSyncStatistics(last.database);
                        if (syncStatistics != null) {

                            numAddedItems +=   syncStatistics.getAddedItemsOnServer();
                            numDeletedItems += syncStatistics.getDeletedItemsServer();
                            numUpdatedItems += syncStatistics.getUpdatedItemsServer();

                            if (!AlertCode.isClientOnlyCode(db.getMethod())) {
                                numAddedItems +=   syncStatistics.getAddedItemsOnClient();
                                numDeletedItems += syncStatistics.getDeletedItemsClient() ;
                                numUpdatedItems += syncStatistics.getUpdatedItemsClient() ;
                            }
                        }
                    }
                    logPush.trace(SyncEvent.createEndSyncEvent(
                                            LogContext.getUserName(),
                                            LogContext.getDeviceId(),
                                            LogContext.getSessionId(),
                                            String.valueOf(last.syncType),
                                            last.database,
                                            String.valueOf(last.status),
                                            numAddedItems,
                                            numDeletedItems,
                                            numUpdatedItems,
                                            new Long(nextTimestamp.end - nextTimestamp.start).intValue(),
                                            "End '" + db.getName() + "' sync '" + db.getMethod() +
                                            "' " + "for '" + LogContext.getSessionId() + "' "));
                }

            } else {
                last = new LastTimestamp(
                        id,
                        db.getName(),
                        db.getMethod(), // syncType
                        db.getStatusCode(),
                        db.getAnchor().getLast(),
                        db.getServerAnchor().getLast(),
                        db.getSyncStartTimestamp(),
                        nextTimestamp.end
                       );
            }

            if (log.isTraceEnabled()) {
                log.trace("Committing database "
                        + db.getName()
                        + " ( "
                        + last
                        + " )"
                );
            }

            try {
                boolean stored = getStore().store(last);
                if (log.isTraceEnabled()) {
                    log.trace("LastTimeStamp stored: " + stored);
                }
            } catch (Sync4jException e) {
                log.error("Error saving persistent data", e);
            } finally {
                LogContext.setSourceURI(null);
            }
        }
    }


    /**
     * Handles the returned status by the client for the commands sent by the server.
     *
     *
     * @param status Status
     * @param sourceUri String
     */
    public void handleReceivedStatus(Status status, String sourceUri) {

        ClientMapping mapping = getMapping(sourceUri);
        if (mapping == null) {
            return;
        }

        SyncSource serverSource = getServerSource(sourceUri);
        String     lastAnchor = EngineHelper.getLastAnchor(sourceUri, dbs);

        List<String> guids = new ArrayList<String>();
        List<String> luids = new ArrayList<String>();
        getGuidsLuids(status, guids, luids, mapping);

        String cmd        = status.getCmd();
        int    statusCode = status.getStatusCode();

        if (Add.COMMAND_NAME.equalsIgnoreCase(cmd) ||
            Replace.COMMAND_NAME.equalsIgnoreCase(cmd) ||
            Delete.COMMAND_NAME.equalsIgnoreCase(cmd)) {

            // The setOperationStatus is always called using guids as parameter
            // for all types of command
            serverSource.setOperationStatus(cmd,
                    statusCode,
                    getSyncItemKeyFromIds(guids));
        }

        updateLastAnchors(status, guids, luids, mapping, lastAnchor);
    }

    /**
     * Updates the last anchor for all the mapped guids/luids depending on the
     * status received from the client
     * @param status The received status
     * @param guids The list of the item guids
     * @param luids The list of the item luids
     * @param mapping The ClientMapping to update
     * @param lastAnchor The last anchor value used to update the mapping
     */
    private void updateLastAnchors(Status status,
            List<String> guids,
            List<String> luids,
            ClientMapping mapping,
            String lastAnchor) {

        String cmd        = status.getCmd();
        int    statusCode = status.getStatusCode();

        if (Add.COMMAND_NAME.equalsIgnoreCase(cmd)) {

            if (StatusCode.CHUNKED_ITEM_ACCEPTED == statusCode) {
                //
                // If we receive the status CHUNKED_ITEM_ACCEPTED we haven't to
                // update the mapping
                //
                return;
            }

            if (StatusCode.OK == statusCode ||
                StatusCode.ITEM_ADDED == statusCode ||
                StatusCode.ALREADY_EXISTS == statusCode) {

                for (int i=0; i < guids.size(); ++i) {
                    mapping.updateLastAnchor(guids.get(i), luids.get(i), lastAnchor);
                }
            }

        } else if (Replace.COMMAND_NAME.equalsIgnoreCase(cmd)) {

            if (StatusCode.CHUNKED_ITEM_ACCEPTED == statusCode) {
                //
                // If we receive the status CHUNKED_ITEM_ACCEPTED we haven't to
                // update the mapping
                //
                return;
            }

            //
            // We have decided to store always the lastAnchor also
            // if the status is an error status code because it isn't the server
            // that has to resend a command that is failed but the syncsource
            //

            for (int i=0; i < guids.size(); ++i) {
                mapping.updateLastAnchor(guids.get(i), luids.get(i), lastAnchor);
            }

        } else if (Delete.COMMAND_NAME.equalsIgnoreCase(cmd)) {

            if (StatusCode.OK == statusCode ||
                StatusCode.DELETE_WITHOUT_ARCHIVE == statusCode ||
                StatusCode.ITEM_NOT_DELETED == statusCode ||
                StatusCode.COMMAND_FAILED == statusCode) {

                //
                // IMPORTANT NOTE: the status code 500 (COMMAND_FAILED) is handled
                // like a 211 (ITEM_NOT_DELETED). This is not really right because
                // if the client returns a 500 we are not sure that the item has
                // been deleted. If the item was already deleted on the client,
                // the client should return a 211 (ITEM_NOT_DELETED) (or also
                // a 200 (OK))
                //
                for (int i = 0; i < guids.size(); ++i) {
                    mapping.confirmRemoveMappedValuesForGuid(guids.get(i));
                }
            }
        }
    }

    /**
     * Stores in the given guids and luids list the guid and luid contained in
     * the given status
     * At the end the two lists must have the same size.
     * @param status Status
     * @param guids List
     * @param luids List
     * @param mapping
     */
    private void getGuidsLuids(Status status, List<String> guids,
            List<String> luids, ClientMapping mapping) {
        if (guids == null || luids == null || mapping == null) {
            return;
        }

        //
        // IMPORTANT NOTE: as specified by the protocol (see OMA-SyncML-RepPro-V1_2-20040601-C.pdf,
        // pg. 28), a Status should contain a SourceRef of a TargetRef and an Item
        // just if additional information is required. BTW, our plug-ins send the
        // status without SourceRef or TargetRef and with an Item specifing the
        // Source. In detail, V3 and V6 plug-ins send Status for Adds, Replaces, Deletes
        // specifing always an item with a Source. Supposing this is wrong because the
        // specs don't specify this case, the server is able to handle Status with
        // Item with Source and Target.
        //
        List   items = status.getItems();
        if (items == null || items.isEmpty()) {
            //
            // We get the luid and guid from the sourceRef and TargetRef
            //
            String luid  = null;
            String guid  = null;
            if (status.getSourceRef() != null &&
                status.getSourceRef().size() > 0) {

                guid = ( (SourceRef)status.getSourceRef().get(0)).getValue();
            }

            if (status.getTargetRef() != null &&
                status.getTargetRef().size() > 0) {

                luid = ( (TargetRef)status.getTargetRef().get(0)).getValue();
            }

            if ((guid == null || guid.length() == 0) &&
                (luid != null && luid.length() > 0)) {

                guid = mapping.getMappedValueForLuid(luid);
            }
            if ((luid == null || luid.length() == 0) &&
                (guid != null && guid.length() > 0)) {

                luid = mapping.getMappedValueForGuid(guid);
            }

            if ((guid != null && guid.length() > 0) &&
                (luid != null && luid.length() > 0) ){

                guids.add(guid);
                luids.add(luid);
            }
        } else {

            String cmd       = status.getCmd();

            Iterator itItems = items.iterator();
            while (itItems.hasNext()) {
                Item item = (Item)itItems.next();

                // The following code is very ugly because it handles Source/Target indistinctly
                // as LUID and GUID but in this way the server is more flexible. In the next
                // releases, when the client will fix their Status and the server must not
                // guarantee the backward compatibility, we can remove this code.
                //
                // See also SyncSessionHandler.checkCommandDescriptorAndStatus(....)
                //
                String uid = null;
                if (item.getTarget() != null) {
                    uid = item.getTarget().getLocURI();
                } else if (item.getSource() != null) {
                    uid = item.getSource().getLocURI();
                }

                // The generic UID is interpreted as a guid or as a luid
                // depending on the command
                if (uid != null && uid.length() > 0) {
                    String luid  = null;
                    String guid  = null;
                    if (Add.COMMAND_NAME.equalsIgnoreCase(cmd)) {
                        guid = uid;
                        luid = mapping.getMappedValueForGuid(guid);
                    } else if (Replace.COMMAND_NAME.equalsIgnoreCase(cmd)) {
                        luid = uid;
                        guid = mapping.getMappedValueForLuid(luid);
                    } else if (Delete.COMMAND_NAME.equalsIgnoreCase(cmd)) {
                        luid = uid;
                        guid = mapping.getMappedValueForLuid(luid);
                    }

                    if ((guid != null && guid.length() > 0) &&
                        (luid != null && luid.length() > 0) ){

                        guids.add(guid);
                        luids.add(luid);
                    }
                }
            }
        }
    }

    /**
     * Returns the operations of the last synchronization
     *
     * @param uri the URI of the source the operations are applied to
     *
     * @return the operations of the last synchronization
     */
    public SyncOperation[] getSyncOperations(String uri) {
        return (SyncOperation[])operations.get(uri);
    }

    /**
     * Resets the operations for the given source.
     *
     * @param uri source uri
     */
    public void resetSyncOperations(String uri) {
        operations.put(uri, new SyncOperation[0]);
    }

    /**
     * Returns the operation status of the operations executed in the last
     * synchronization
     *
     * @param msgId the id of the SyncML message
     *
     * @return the operations status of the operations executed in the last
               synchronization
     */
    public Status[] getModificationsStatusCommands(String msgId) {

        if ((status == null) || (status.size() == 0)) {
            return new Status[0];
        }

        SyncOperationStatus[] operationStatus =
            (SyncOperationStatus[])status.toArray(new SyncOperationStatus[status.size()]);

        return EngineHelper.generateStatusCommands(
            operationStatus,
            msgId,
            cmdIdGenerator
        );
    }

    /**
     * Adds a new client source to the list of the sources the engine deals with.
     *
     * @param source the source to be added
     */
    public void addClientSource(SyncSource source) {
        if (log.isTraceEnabled()) {
            log.trace("adding " + source);
        }

        clientSources.add(source);

        syncSourceState.moveToConfigured(source.getSourceURI());
    }

    /**
     * Returns the client sources
     *
     * @return the client sources
     */
    public List getClientSources() {
        return clientSources;
    }

    /**
     * Returns the client source with the given name if there is any.
     *
     * @param name the source name
     *
     * @return the client source with the given name if there is any, null otherwise
     */
    public SyncSource getClientSource(String name) {

        name = stripQueryString(name);

        Iterator i = clientSources.iterator();

        SyncSource s = null;
        while (i.hasNext()) {
            s = (SyncSource)i.next();

            if (s.getSourceURI().equals(name)) {
                return s;
            }
        }

        return null;
    }

    /**
     * Sets the filter to use in the sync of the syncsource with the given sourceUri
     * @param sourceUri String
     * @param filter FilterClause
     */
    public void setFilter(String sourceUri, FilterClause filter) {
        filters.put(sourceUri, filter);
    }

    /**
     * Returns the status of the give client source (as stored in the
     * corresponding database object).
     *
     * @param uri the source uri
     *
     * @return the status of the client source (as stored in the corresponding
     * database object)
     */
    public int getClientSourceStatus(String uri) {

        uri = stripQueryString(uri);

        Database db = (Database)dbs.get(uri);

        return db.getStatusCode();
    }

    /**
     * Returns the status message of the given client source (as stored in the
     * corresponding database object).
     *
     * @param uri the source uri
     *
     * @return the status message of the client source (as stored in the corresponding
     * database object)
     */
    public String getClientStatusMessage(String uri) {

        uri = stripQueryString(uri);

        Database db = (Database)dbs.get(uri);

        return db.getStatusMessage();
    }

    /**
     * Returns the server source with the given name if there is any.
     *
     * @param name the source name
     *
     * @return the server source with the given name if there is any, null otherwise
     */
    public SyncSource getServerSource(String name) {

        name = stripQueryString(name);

        if (serverSources.containsKey(name)) {
            return (SyncSource)serverSources.get(name);
        }

        Sync4jSource s = new Sync4jSource(name);

        try {
            store.read(s);
        } catch (PersistentStoreException e) {
            log.error("Error reading source configuration for source "
                      + name, e
                      );

        }

        SyncSource syncSource = null;
        try {
            syncSource = getServerSource(s);
            serverSources.put(name, syncSource);
        } catch (Sync4jException ex) {
            //
            // in error case we need to return null. exception is already
            // logged in getServerSource. nothing to do here.
            //
        }

        return syncSource;
    }

    /**
     * Returns the content type capabilities of this sync engine.
     *
     * @return the content type capabilities of this sync engine.
     */
    public CTCap[] getContentTypeCapabilities() {
        return new CTCap[0];
    }

    /**
     * Returns the extensions of this sync engine.
     *
     * @return the extensions of this sync engine.
     */
    public Ext[] getExtensions() {
        return new Ext[0];
    }

    /**
     * Returns the data store capabilities of the sync sources to synchronize.
     * @param verDTD the version of the DTD to use to encode the datastores
     *
     * @return the data store capabilities of the sync sources to synchronize.
     */
    public DataStore[] getDatastores(VerDTD verDTD, SyncSource[] serverSources) {
        DataStore[] ds = null;
        List al        = new ArrayList();

        for (SyncSource ss : serverSources) {
            if (ss == null) {
                continue;
            }
            
            al.add(EngineHelper.toDataStore(ss.getSourceURI(),
                                            ss.getName(),
                                            ss.getInfo(),
                                            verDTD)
            );

        }

        int size = al.size();
        if (size == 0) {
            ds = new DataStore[0];
        } else {
            ds = (DataStore[])al.toArray(new DataStore[size]);
        }

        return ds;
    }

    /**
     * Creates and returns a <i>DeviceInfo</i> object with the information
     * extracted from the configuration object.
     *
     *
     * @return the engine capabilities
     * @param verDTD the version of the DTD to use to encode the capabilities
     */
    public DevInf getServerCapabilities(VerDTD verDTD) {
        try {
            //
            // every time a new instance of the serverInfo is created
            // because for ver dtd 1.0 the UTC, SupportLargeObjects and
            // SupportNumberOfChanges must be set on false
            // in order not to be included in the response message
            //
             DevInf devInf = ((ServerConfiguration) configuration.
                    getBeanInstanceByName(BEAN_SERVER_CONFIGURATION, false)).
                    getServerInfo();
             devInf.setDataStores(this.getDatastores(verDTD, getAllServerSources()));
             devInf.setVerDTD(verDTD);
             if (Constants.DTD_1_0.equals(verDTD)) {
                //
                // set on false the values for:
                //     <UTC>
                //     <SupportLargeObjs>
                //     <SupportNumberOfChanges>
                // if the vers dtd is 1.0
                //
                devInf.setUTC(Boolean.FALSE);
                devInf.setSupportLargeObjs(Boolean.FALSE);
                devInf.setSupportNumberOfChanges(Boolean.FALSE);
             }
        return devInf;
        } catch (Exception e) {
            throw new ConfigurationException("Error in creating an instance of "
                    + BEAN_SERVER_CONFIGURATION
                    , e);
        }

    }

    /**
     * First of all, check the availablity and accessibility of the given
     * databases. The state of the given database will change as described below
     * (and in the same order):
     * <ul>
     *   <li>The database status code is set to <i>StatusCode.OK</i> if the
     *       database is accessible, <i>StatusCode.NOT_FOUND</i> if the database
     *       is not found and <i>StatusCode.FORBIDDEN</i> if the principal is
     *       not allowed to access the database.
     *   <li>If the currently set last anchor does not match the last tag
     *       retrieved from the database (DBMS) and the requested alert code is
     *       not a refresh, the synchronization method is set to
     *       <i>AlertCode.SLOW</i>
     *   <li>The database server sync anchor is set to the server-side sync anchor
     *
     * @param principal the principal the is requesting db preparation
     * @param dbs an array of <i>Database</i> objects - NULL
     * @param next the sync timestamp of the current synchronization
     *
     */
    public void prepareDatabases(Sync4jPrincipal principal,
                                 Database[]      dbs      ,
                                 SyncTimestamp   next     ) {

        boolean resumeRequired   = false;
        int     syncTypeToResume = 0;

        for (int i=0; ((dbs != null) && (i < dbs.length)); ++i) {

            resumeRequired = false;

            int statusCode = StatusCode.OK;
            if (!checkServerDatabase(dbs[i])) {
                statusCode = StatusCode.NOT_FOUND;
                if (logPush.isTraceEnabled()) {
                    logPush.trace(SyncEvent.createStartSyncEventOnError(
                            LogContext.getUserName(),
                            LogContext.getDeviceId(),
                            LogContext.getSessionId(),
                            String.valueOf(dbs[i].getMethod()),
                            dbs[i].getName(),
                            "SyncSource '" + dbs[i].getName() + "' not found [session '" + LogContext.getSessionId() + "']",
                            Integer.toString(StatusCode.NOT_FOUND)));
                }
            } else {

                switch (authorizeDatabase(dbs[i])) {
                    case AUTHORIZED:
                        statusCode = StatusCode.OK;
                        break;
                    case INVALID_RESOURCE:
                        statusCode = StatusCode.NOT_FOUND;
                        break;
                    case NOT_AUTHORIZED:
                        statusCode = StatusCode.FORBIDDEN;
                        break;
                    case PAYMENT_REQUIRED:
                        statusCode = StatusCode.PAYMENT_REQUIRED;
                        break;
                    case RESOURCE_NOT_AVAILABLE:
                        statusCode = StatusCode.NOT_FOUND;
                        break;
                    default:
                }
            }

            dbs[i].setStatusCode(statusCode);

            //
            // Now retrieve the last sync anchor
            //
            if (statusCode == StatusCode.OK) {
                LastTimestamp last = new LastTimestamp(
                    principal.getId(),
                    dbs[i].getName()
                );

                //
                // Into tagServer there is the Next anchor sent by client: now
                // this become a Last anchor of the server.
                // Into tagClient there is the Next anchor sent by server: now
                // this become a Last anchor of the client.
                // The server anchor has like Last the Next sent from the client
                // and like Next the Next generated at the start of sync.
                //
                try {
                    store.read(last);
                    dbs[i].setServerAnchor(new Anchor(last.tagClient, next.tagClient));
                    dbs[i].setSyncStartTimestamp(last.start);
                } catch (NotFoundException e) {
                    //
                    // No prev last sync! Create a new anchor that won't match
                    //
                    last.tagServer = next.tagClient;
                    dbs[i].setServerAnchor(new Anchor(last.tagServer, next.tagClient));
                } catch(PersistentStoreException e) {
                    log.error("Unable to retrieve timestamp from store", e);
                }

                if (dbs[i].getMethod() == AlertCode.RESUME) {
                    //
                    // The client wants to resume the session
                    //
                    resumeRequired   = true;

                    if (AlertCode.SUSPEND != last.status) {

                        if (log.isTraceEnabled()) {
                            log.trace("The client wants to resume a previous sync for " +
                                      "database '" + dbs[i].getName() + "' but there isn't " +
                                      "a previous sync to resume.");
                        }
                    } else {

                        syncTypeToResume = last.syncType;

                        if (log.isTraceEnabled()) {
                            log.trace("The client wants to resume a previous sync for " +
                                      "database '" + dbs[i].getName() + "' with syncType: " +
                                      syncTypeToResume + " (" +
                                      AlertCode.getAlertDescription(syncTypeToResume) +
                                      ")");
                        }
                        //
                        // Resuming a fast sync, we have to use the last anchor
                        // used in the previous sync
                        //
                        if (syncTypeToResume != AlertCode.SLOW &&
                            syncTypeToResume != AlertCode.REFRESH_FROM_SERVER &&
                            syncTypeToResume != AlertCode.REFRESH_FROM_CLIENT &&
                            syncTypeToResume != AlertCode.SMART_ONE_WAY_FROM_CLIENT ) {

                            dbs[i].getAnchor().setLast(last.tagServer);

                        } else {
                            last.tagServer = "";
                        }
                    }
                }

                if (last.tagServer == null) {
                    last.tagServer = "";
                }

                if (!(last.tagServer.equals(dbs[i].getAnchor().getLast())) &&
                    (dbs[i].getMethod() != AlertCode.REFRESH_FROM_SERVER)  &&
                    (dbs[i].getMethod() != AlertCode.REFRESH_FROM_CLIENT)  &&
                    (dbs[i].getMethod() != AlertCode.SMART_ONE_WAY_FROM_CLIENT)   ) {

                   if (resumeRequired) {
                       if (AlertCode.SUSPEND == last.status) {
                           //
                           // The client wants to resume a previous SLOW SYNC. In order to keep
                           // the client's db in a consistent state, we force
                           // a new slow sync and reject the resume request
                           //
                           if (log.isTraceEnabled()) {
                               log.trace("The client wants to resume a previous " +
                                         AlertCode.getAlertDescription(syncTypeToResume) +
                                         " for database '" + dbs[i].getName() +
                                         "'. In order to keep " +
                                         "the client's db in a consistent state, we force " +
                                         "a new slow sync and reject the resume request.");
                           }
                       }
                    } else {
                       if (log.isTraceEnabled()) {
                           log.trace("Last stored anchor '" + last.tagServer +
                                     "' is different from the current anchor sent by the client '" +
                                     dbs[i].getAnchor().getLast() + "'. A slow sync will be forced.");
                       }
                    }

                    if (dbs[i].getMethod() != AlertCode.SLOW) {
                        dbs[i].setStatusCode(StatusCode.REFRESH_REQUIRED);
                    }

                    dbs[i].setMethod(AlertCode.SLOW);

                } else {

                    if (resumeRequired) {
                        if (AlertCode.SUSPEND != last.status &&
                            (dbs[i].getMethod() != AlertCode.SMART_ONE_WAY_FROM_CLIENT)) {
                            if (log.isTraceEnabled()) {
                                log.trace(
                                        "The client wants to resume a previous fast sync for " +
                                        "database '" + dbs[i].getName() +
                                        "' but there isn't " +
                                        "a previous sync to resume. Resume rejected.");
                            }
                            dbs[i].setMethod(AlertCode.SLOW);
                            dbs[i].setStatusCode(StatusCode.REFRESH_REQUIRED);

                        } else {
                            if (log.isTraceEnabled()) {
                                log.trace(
                                        "The client wants to resume a previous sync " +
                                        "for database '" + dbs[i].getName() + "'. " +
                                        "Resume accepted.");
                            }
                            dbs[i].setMethod(syncTypeToResume);
                            //
                            // This means ok, we can resume the session
                            //
                            dbs[i].setStatusCode(StatusCode.OK);
                        }
                    }
                }
            }
            if (dbs[i].getMethod() == AlertCode.SLOW) {
                dbs[i].setLast(EngineHelper.LAST_ANCHOR_SLOW_SYNC);
            }
        }

    }

    /**
     * Authenticates the given credentials using <i>officer</i>.
     *
     * @param credentials the credentials to be authenticated
     *
     * @return the Sync4jUser if the credentials are autenticated, null otherwise
     */
    public Sync4jUser login(Cred credentials) {
        Sync4jUser user = null;

        try {
            user = officer.authenticateUser(credentials);
            if (user != null) {
                LogContext.setUserName(user.getUsername());
            } else {
                LogContext.setUserName(null);
            }
        } catch(java.lang.AbstractMethodError err) {
            //
            // Using reflection to handle previous officer version (v3)
            //
            user = handleOldOfficerInstance(credentials);
        }

        return user;
    }

    /**
     * Logs out the given user using <i>officer</i>.
     * The old version of officer must be supported and so is need to pass at
     * the logout method the credentials.
     *
     * @param user the user to be un-authenticated
     * @param credentials the credentials to be un-authenticated
     *
     */
    public void logout(Sync4jUser user, Cred credentials) {
        try {
            officer.unAuthenticate(user);
        } catch(java.lang.AbstractMethodError err) {

            //
            // Using reflection to handle previous officer version (v3)
            //
            Method method = null;
            try {
                method = officer.getClass().getMethod("unAuthenticate",
                                                      new Class[] {Cred.class}
                         );

                method.invoke(officer, new Object[] {credentials});
            } catch(Exception e) {
                log.error("Error invoking 'unAuthenticate' method", e);
            }
        }
    }

    /**
     * Authorizes the given principal to access the session using
     * <i>officer</i>.
     * <br/>
     * Note that this method must to handle also the previous version of the
     * officer; the one defined in v3 returned just a boolean so we have to
     * call it using reflection if an AbstractMethodError is thrown
     *
     * @param principal the entity to be authorized
     * @param sessionId the sessionId
     * @return an AuthStatus
     */
    public Officer.AuthStatus authorizeSession(Principal principal, String sessionId) {
        //
        // We have the logged principal only after the authentication process
        //
        StringBuilder sessionResource = new StringBuilder(SecurityConstants.RESOURCE_SESSION);
        sessionResource.append('/').append(sessionId);

        Officer.AuthStatus status = null;

        try {
            //
            // With old officers (previous to v6) this method doesn't return an
            // AuthStatus but just a boolean so, in order to guarantee backward
            // compatibility, we must catch AbstractMethodError and call it with
            // reflection
            //
            status = officer.authorize(principal, sessionResource.toString());
        } catch (AbstractMethodError e) {
            //
            // Calling old authorize method (the one defined in v3). Note that
            // in v3 the authorize method was called with just
            // SecurityConstants.RESOURCE_SESSION (without sessionId)
            //
            status = callAuthorizeMethodWithReflection(principal,
                                                       SecurityConstants.RESOURCE_SESSION);
        } catch(Exception e) {

            log.error("Error invoking 'authorize' method", e);
        }
        return status;
    }

    /**
     * Authorizes the given principal to access the given database using the
     * <i>officer</i>.
     * <br/>
     * Note that this method must to handle also the previous version of the
     * officer; the one defined in v3 returned just a boolean so we have to
     * call it using reflection if an AbstractMethodError is thrown
     *
     * @param db the database to authorize
     * @return an AuthStatus
     */
    public Officer.AuthStatus authorizeDatabase(Database  db) {
        Principal principal = db.getPrincipal();
        StringBuilder resource = new StringBuilder(SecurityConstants.RESOURCE_DATABASE);
        resource.append('/').append(db.getName());
        String sourceQuery = db.getSourceQuery();
        if (sourceQuery != null && sourceQuery.length() != 0) {
            resource.append('?').append(sourceQuery);
        }

        Officer.AuthStatus status = null;

        try {
            //
            // With old officers (previous to v6) this method doesn't return an
            // AuthStatus but just a boolean so, in order to guarantee backward
            // compatibility we must catch AbstractMethodError and call it with
            // reflection
            //
            status = officer.authorize(principal, resource.toString());
        } catch (AbstractMethodError e) {
            //
            // Calling old authorize method (the one defined in v3). Note that
            // in v3 the authorize method was called with just the resource name
            // (without sourceQuery)
            //
            status = callAuthorizeMethodWithReflection(principal, db.getName());
        } catch(Exception e) {

            log.error("Error invoking 'authorize' method", e);
        }
        return status;
    }

    /**
     * Translates an array of <i>SyncOperation</i> objects to an array of
     * <i>(Add,Delete,Replace)Command</i> objects. Only client side operations
     * are translated.
     *
     *
     * @return the commands corresponding to <i>operations</i>
     * @param operations the operations to be translated
     * @param uri the source uri
     * @param mimeType the mime type
     */
    public ItemizedCommand[] operationsToCommands(SyncOperation[]    operations,
                                                  String           uri       ,
                                                  String           mimeType) {

        // the Sync4jEngine doesn't save the Data in the syncItem list
        ItemizedCommand[] commands =
            EngineHelper.operationsToCommands(getMapping(uri),
                                              operations     ,
                                              uri            ,
                                              cmdIdGenerator ,
                                              false,
                                              dataTransformerManager,
                                              principal,
                                              mimeType);
        return commands;
    }

    /**
     * Completes the given item with the format, type and content calling the
     * corresponding syncsource
     * @param mimeType mimeType of the syncml session. It's needed because the
     * item's content must be encoded if the session is in xml.
     * @param sourceUri the source uri
     * @param item Item
     * @throws Sync4jException if an error occurs
     */
    public void completeItemInfo(String mimeType,
                                 String sourceUri,
                                 Item   item)
    throws Sync4jException {

        //
        // 1. Get the syncsource
        //
        SyncSource serverSyncSource = getServerSource(sourceUri); // server sync source

        if (serverSyncSource == null) {
            throw new Sync4jException("Error retriving data of the item: " + item +
                                      ". SyncSource with uri '" + sourceUri +
                                      "' not found");
        }

        //
        // 2. Get the mapping
        //
        ClientMapping mapping = (ClientMapping)clientMappings.get(sourceUri);

        //
        // 3. Get the item key
        //
        String guid = getGuid(mapping, item);
        if (log.isTraceEnabled()) {
            log.trace("Filling item with guid '" + guid + "' with all info");
        }
        SyncItemKey key = new SyncItemKey(guid);

        //
        // 3. get the SyncItem
        //
        AbstractSyncItem tmp = null;
        try {
            tmp = (AbstractSyncItem)serverSyncSource.getSyncItemFromId(key);
        } catch (SyncSourceException ex) {
            throw new Sync4jException("Error retriving data of the item with key: " +
                                      key, ex);
        }

        if (log.isTraceEnabled()) {
            log.trace("Retrieved item with key '" + key + "': " + tmp);
        }

        Object tmpProp = tmp.getPropertyValue(AbstractSyncItem.PROPERTY_OPAQUE_DATA);
        boolean opaqueData = Boolean.TRUE.equals(tmpProp);

        if (tmp == null) {
            throw new Sync4jException("The retrieved item with key '" + key + "' is null");
        }

        if (tmp.getContent() == null) {
            throw new Sync4jException("The content of the retrieved item with key '" + key + "' is null");
        }

        //
        // Setting the type
        //
        // The following rules apply:
        // - If the operation is an addition, Targer MUST NOT be included.
        // - If the operation is not an addition, Source MUST NOT be included.
        // - If the operation is not a deletion, Data element MUST be used to carry data ifself
        //

        //
        // set Meta
        // see also below
        //
        Meta meta = item.getMeta();
        if (meta == null) {
            meta = new Meta();
            item.setMeta(meta);
        }
        if (tmp.getFormat() != null) {
            meta.setFormat(tmp.getFormat());
        }
        if (tmp.getType() != null) {
            meta.setType(tmp.getType());
        } else {
            if (log.isWarningEnabled()) {
                log.warn("The SyncSource '" +
                        serverSyncSource.getSourceURI() +
                        "' returns items without the property 'type'; this is deprecated.");
            }

            String syncSourceType = callGetTypeWithReflection(serverSyncSource);
            if (syncSourceType != null && syncSourceType.length() != 0) {
                meta.setType(syncSourceType);
            } else {
                meta.setType(serverSyncSource.getInfo().getPreferredType().getType());
            }
        }

        //
        // apply data transformation
        //
        applyDataTransformationsOnOutgoingItem(tmp);

        if (Constants.MIMETYPE_SYNCMLDS_XML.equals(mimeType)) {
            //
            // StreamingSyncItem content is not escaped since we need to work with a 
            // stream without change it. Moreover, see below, using StreamingSyncItem
            // the data will be put in a CData section (in this way escaping is not
            // needed). All this things are done to improve memory usage (escaping
            // large data needs a lot of memory)
            //
            if (!opaqueData) {
                //
                // The mimeType is xml...escaping the item's content
                //
                String content = new String(tmp.getContent());
                content = SyncItemHelper.escapeData(content);
                tmp.setContent(content.getBytes());
            }
        }

        // set Data
        byte[] content = tmp.getContent();
        ComplexData data =  null;

        if (content != null) {
            //
            // Handling StreamingSyncItem we need to use CData in order to
            // avoid escaping (see above). We use binary data in order to avoid to
            // create a string without reason (string will be created when SyncML
            // is created byb jibx)
            //
            if (opaqueData) {
                data = new CData();
                data.setBinaryData(content);
            } else {
                data = new ComplexData(new String(content));
            }
            item.setData(data);
            item.setIncompleteInfo(false);

        } else {
            // content null
            data = new ComplexData();
            item.setData(data);
            item.setIncompleteInfo(true);
        }

        // set Meta
        String itemFormat = tmp.getFormat();
        String itemType   = tmp.getType();
        if (itemFormat != null || itemType != null) {
            meta = new Meta();
            if (itemFormat != null) {
                meta.setFormat(itemFormat);
            }
            if (itemType != null) {
                meta.setType(itemType);
            }
            item.setMeta(meta);
        }

        // set TargetParent with the mapped LUID
        if (tmp.getParentKey() != null) {
            TargetParent targetParent = null;
            SourceParent sourceParent = null;
            //
            // We have to use the mapping to find the LUID of the parentKey
            //
            if (mapping != null) {
                String parentGuid = tmp.getParentKey().getKeyAsString();
                ClientMappingEntry mappingEntry = mapping.getClientMappingEntry(parentGuid);
                String parentLuid   = null;
                if (mappingEntry != null && mappingEntry.isValid()) {
                    parentLuid = mappingEntry.getLuid();
                }
                if (parentLuid != null) {
                    targetParent = new TargetParent(parentLuid);
                } else if (parentGuid != null) {
                    sourceParent = new SourceParent(parentGuid);
                }
            }
            item.setTargetParent(targetParent);
            item.setSourceParent(sourceParent);
        }

        if (log.isTraceEnabled()) {
            log.trace("Item filled");
        }
    }

    /**
     * Converts an array of <i>Item</i> objects belonging to the same
     * <i>SyncSource</i> into an array of <i>SyncItem</i> objects.
     * <p>
     * The <i>Item</i>s created are enriched with an additional property
     * called as in SyncItemHelper.PROPERTY_COMMAND, which is used to bound the
     * newly created object with the original command.
     * @return an array of <i>SyncItem</i> objects
     * @param cmd the original command
     * @param timestamp the timestamp
     * @param syncSource the <i>SyncSource</i> items belong to - NOT NULL
     * @param state the state of the item as one of the values defined in
     *              <i>SyncItemState</i>
     */
    public SyncItem[] itemsToSyncItems(SyncSource          syncSource   ,
                                       ModificationCommand cmd          ,
                                       char                state        ,
                                       long                timestamp    ) {

        ClientMapping m = getMapping(syncSource.getSourceURI());
        SyncItemFactory syncItemFactory;
        try {
            syncItemFactory = getSyncItemFactory(syncSource.getSourceURI());
        } catch (SyncSourceException ex) {
            syncItemFactory = null;
        }
        return EngineHelper.itemsToSyncItems(m, syncSource, cmd, state,
                timestamp, syncItemFactory);
    }


        /**
     * Returns the server credentials in the format specified by the given Chal.
     *
     * @param chal the client Chal
     * @param device the device
     *
     * @return the server credentials
     */
    public Cred getServerCredentials(Chal chal, Sync4jDevice device) {
        String login = configuration.getServerConfig().getServerInfo().getDevID();

        if (login == null || login.equals("")) {
            return null;
        }

        String pwd    = device.getServerPassword();

        String type   = chal.getType();
        String data   = login + ':' + pwd;

        if (Cred.AUTH_TYPE_BASIC.equals(type)) {
            data = new String(Base64.encode(data.getBytes()));
        } else if (Cred.AUTH_TYPE_MD5.equals(type)) {

            if (this.syncMLVerProto.indexOf("1.0") != -1) {
                //
                // Steps to follow:
                // 1) decode nonce sent by client
                // 2) b64(H(username:password:nonce))
                //
                byte[] serverNonce = chal.getNextNonce().getValue();
                byte[] serverNonceDecode = null;
                if ((serverNonce == null) || (serverNonce.length == 0)) {
                    serverNonceDecode = new byte[0];
                } else {
                    serverNonceDecode = Constants.FORMAT_B64.equals(chal.getFormat())
                                        ? Base64.decode(serverNonce)
                                        : serverNonce
                                        ;
                }
                byte[] dataBytes = data.getBytes();
                byte[] buf       = new byte[dataBytes.length + 1 + serverNonceDecode.length];
                System.arraycopy(dataBytes, 0, buf, 0, dataBytes.length);
                buf[dataBytes.length] = (byte)':';
                System.arraycopy(serverNonceDecode, 0, buf, dataBytes.length+1, serverNonceDecode.length);

                byte[] digestNonce = MD5.digest(buf);
                data =  new String(Base64.encode(digestNonce));

            } else {
                //
                //Steps to follow:
                //1) H(username:password)
                //2) b64(H(username:password))
                //3) decode nonce sent by client
                //4) b64(H(username:password)):nonce
                //5) H(b64(H(username:password)):nonce)
                //6) b64(H(b64(H(username:password)):nonce))
                //
                byte[] serverNonce = chal.getNextNonce().getValue();
                byte[] serverNonceDecode = null;
                if ((serverNonce == null) || (serverNonce.length == 0)) {
                    serverNonceDecode = new byte[0];
                } else {
                    serverNonceDecode = Constants.FORMAT_B64.equals(chal.getFormat())
                                        ? Base64.decode(serverNonce)
                                        : serverNonce
                                        ;
                }
                byte[] digest = MD5.digest(data.getBytes()); // data = username:password
                byte[] b64    = Base64.encode(digest);
                byte[] buf    = new byte[b64.length + 1 + serverNonceDecode.length];

                System.arraycopy(b64, 0, buf, 0, b64.length);
                buf[b64.length] = (byte)':';
                System.arraycopy(serverNonceDecode, 0, buf, b64.length+1, serverNonceDecode.length);

                data = new String(Base64.encode(MD5.digest(buf)));
            }
        }

        Meta m = new Meta();
        m.setType(type);
        m.setNextNonce(null);
        return new Cred(new Authentication(m, data));
    }

    /**
     * Read the given principal from the store. The principal must be
     * already configured with username and device. The current implementation
     * reads the following additional information:
     * <ul>
     *  <li>principal id
     * </ul>
     * @param principal the principal to be read
     * @throws com.funambol.framework.server.store.PersistentStoreException if an error occurs
     */
    public void readPrincipal(Sync4jPrincipal principal)
    throws PersistentStoreException {
        assert(principal               != null);
        assert(principal.getUsername() != null);
        assert(principal.getDeviceId() != null);

        getStore().read(principal);

    }

    /**
     * Read the Sync4jUser identified by the given username.
     * @param user the user to read
     * @throws PersistentStoreException if an error occurs
     * @throws AdminException if an administration
     * error occurs
     */
    public void readUser(Sync4jUser user)
    throws PersistentStoreException, AdminException {

        userManager.getUser(user);
    }

    /**
     * Read the Sync4jDevice (including its capabilities),
     * identified by id of the given device from the store.
     * @return true is the device is found, false otherwise
     * @param device the device to read
     * @throws com.funambol.framework.server.inventory.DeviceInventoryException if an error occurs
     */
    public boolean readDevice(Sync4jDevice device)
    throws DeviceInventoryException {

        if (device == null) {
            throw new IllegalArgumentException("The device must be not null");
        }

        return getDeviceInventory().getDevice(device, true);
    }

    /**
     * Read the Sync4jDevice (excluding its capabilities),
     * identified by id of the given device from the store.
     * @return true is the device is found, false otherwise
     * @param device the device to read
     * @throws com.funambol.framework.server.inventory.DeviceInventoryException if an error occurs
     */
    public boolean readDeviceWithoutCapabilities(Sync4jDevice device)
    throws DeviceInventoryException {

        if (device == null) {
            throw new IllegalArgumentException("The device must be not null");
        }

        return getDeviceInventory().getDevice(device, false);
    }

    /**
     * Stores the given Sync4jDevice.
     *
     * @param device the device
     *
     */
    public void storeDevice(Sync4jDevice device) {
        try{
            getDeviceInventory().setDevice(device);
        } catch (DeviceInventoryException e) {
            log.error("Error storing the device", e);
        }
    }

    /**
     * Updates the given mapping for the given source
     * @param guid the guid
     * @param luid the luid
     * @param uri the source uri
     */
    public void updateMapping(String uri, String guid, String luid) {
        getMapping(uri).updateMapping(guid,
                                      luid,
                                      EngineHelper.getLastAnchor(uri, dbs));
    }


    /**
     * Returns the GUID-LUID mappings associated to the given source URI.
     * Mappings are cached internally so that they are read from the db only
     * the first time they are requested.
     * Note that in the case of a slow sync, the first time a mapping is
     * requested the mapping must be reset so that old values will be removed.
     *
     * @param principal principal - this is used only when the mapping was not
     *        cached yet; after caching it can be null
     * @param uri database URI
     * @param slow is a slow sync being performed?
     *
     * @return the LUID-GUID mapping associated to the give source uri
     */
    public ClientMapping getMapping(Sync4jPrincipal principal,
                                    String          uri      ,
                                    boolean         slow     ) {
        if (clientMappings == null) {
            clientMappings = new HashMap();
        }

        uri = stripQueryString(uri);

        ClientMapping ret = (ClientMapping)clientMappings.get(uri);

        if (ret == null) {

            PersistentStore ps = getStore();

            ret = new ClientMapping(principal, uri);

            if (slow) {
                try {
                    ps.delete(ret);
                } catch (PersistentStoreException ex) {
                    log.error("Unable to delete client mappings", ex);
                }
            } else {
                try {
                    ps.read(ret);
                } catch (PersistentStoreException ex) {
                    log.error("Unable to read client mappings", ex);
                }
            }

            clientMappings.put(uri, ret);
        }

        return ret;
    }

    /**
     * This is a shortcut for getMapping(null, uri, false). It can be used when
     * it is sure that a mapping was already retrieved from the db.
     *
     * @param uri the source uri
     *
     * @return the LUID-GUID mapping associated to the give source uri
     */
    public ClientMapping getMapping(String uri) {
        return getMapping(null, uri, false);
    }

    /**
     * Stores the LUIG-GUID mappings into the database
     */
    public void storeMappings() {
        if (clientMappings == null) {
            return;
        }

        try {
            PersistentStore ps = getStore();

            ClientMapping cm = null;
            Iterator i = clientMappings.keySet().iterator();
            while (i.hasNext()) {
                cm = (ClientMapping)clientMappings.get(i.next());

                LogContext.setSourceURI(cm.getDbURI());

                if (log.isTraceEnabled()) {
                    log.trace("Saving client mapping: " + cm);
                }
                ps.store(cm);

                cm.resetModifiedKeys();
            }
        } catch (PersistentStoreException e) {
            log.error("Unable to save clientMappings to the persistent store", e);
        } finally {
            LogContext.setSourceURI(null);
        }
    }


    /**
     * Suspends the current synchronization
     */
    public void suspendSynchronization() {
        if (dbs != null) {
            Iterator itDbs = dbs.keySet().iterator();
            Database db    = null;
            while (itDbs.hasNext()) {
                db = (Database)(dbs.get(itDbs.next()));
                db.setStatusCode(AlertCode.SUSPEND);
            }
        }
    }

    /**
     * Returns the last anchor of the database with the given uri searching it
     * in the cached map of databases
     *
     * @param sourceUri the source uri
     *
     * @return the last anchor
     */
    public String getLastAnchor(String sourceUri) {
        return EngineHelper.getLastAnchor(sourceUri, dbs);
    }

    /**
     * Get a SyncItemFactory if the syncsource provides them.
     * For the same sourceUri it returns the same instance
     * @param sourceUri
     * @return  a SyncItemFactory if the syncsource provides them or null
     * otherwise
     */
    public SyncItemFactory getSyncItemFactory(String sourceUri) throws SyncSourceException {

        SyncSource syncSource = getServerSource(sourceUri);
        if (syncSource == null) {
            return null;
        }

        return getSyncItemFactory(syncSource);
    }


    public SyncItemFactory getSyncItemFactory(SyncSource syncSource) throws SyncSourceException {

        SyncItemFactory syncItemFactory = syncItemFactoryCache.get(syncSource.getSourceURI());
        if (syncItemFactory == null ) {
            syncItemFactory = (syncSource instanceof StreamingSyncSource ?
                ((StreamingSyncSource)syncSource).getSyncItemFactory(principal.getUsername()) : null);
            if (syncItemFactory != null) {
                syncItemFactoryCache.put(syncSource.getSourceURI(), syncItemFactory);
            }
        }

        return syncItemFactory;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Applies the required data transformation on the go out items
     * @param item the syncitem to transform
     * @throws com.funambol.framework.core.Sync4jException if an error occurs
     */
    private void applyDataTransformationsOnOutgoingItem(SyncItem item) throws Sync4jException {
        if (item.getContent() == null) {
            return;
        }
        TransformationInfo info = new TransformationInfo();
        info.setCredentials (principal.getEncodedCredentials());
        info.setUsername    (principal.getUsername()          );
        info.setUserPassword(principal.getUser().getPassword());

        dataTransformerManager.transformOutgoingItem(info, item);
    }

    /**
     * Applies the required data transformation on the come in items
     * @param items list of items
     * @throws com.funambol.framework.core.Sync4jException if an error occurs
     */
    protected void applyDataTransformationsOnIncomingItems(SyncItem[] items) throws Sync4jException {
        if (items == null || items.length == 0) {
            return;
        }

        if (dataTransformerManager != null) {

            TransformationInfo info = new TransformationInfo();
            info.setCredentials (principal.getEncodedCredentials());
            info.setUsername    (principal.getUsername()          );
            info.setUserPassword(principal.getUser().getPassword());

            AbstractSyncItem item    = null;
            int          numItems    = items.length;

            for (int i=0; i<numItems; i++) {
                item = (AbstractSyncItem)items[i];
                if (item.getState() == SyncItemState.NEW ||
                    item.getState() == SyncItemState.UPDATED ||
                    item.getState() == SyncItemState.SYNCHRONIZED) {
                    dataTransformerManager.transformIncomingItem(info, item);
                    //
                    // We have to store the original content size because this size
                    // has to be checked and not the content size after the transformations.
                    //  See Sync4jStrategy.checkSize()
                    //
                    if (((AbstractSyncItem)item).getPropertyValue(AbstractSyncItem.PROPERTY_CONTENT_SIZE) == null) {
                        Long contentSize = new Long(item.getContent() != null ? item.getContent().length : 0);
                        item.setPropertyValue(AbstractSyncItem.PROPERTY_CONTENT_SIZE,
                                contentSize);
                    }
                }
            }
        }
    }


    /**
     * Checks if the given database is managed by this server.
     *
     * @param db the database to be checked
     *
     * @return true if the database is under control of this server, false otherwise
     *
     */
    protected boolean checkServerDatabase(Database db) {
        if (log.isTraceEnabled()) {
            log.trace( "Checking if the database "
                      + db
                      + " is in the server database list."
                      );
        }

        SyncSource s = getServerSource(db.getName());

        return (s != null);
    }

    /**
     * Sets the database error accordingly to the given exception.
     *
     * @param db the database
     * @param cause the throwable cause
     *
     */
    protected void setDBError(Database db, SyncException cause) {
        db.setStatusCode   (cause.getStatusCode());
        db.setStatusMessage(cause.getMessage()   );
        if(logPush.isTraceEnabled()) {
            logPush.trace(
                SyncEvent.createEndSyncEventOnError(LogContext.getUserName(),
                                                 LogContext.getDeviceId(),
                                                 LogContext.getSessionId(),
                                                 Integer.toString(db.getMethod()),
                                                 LogContext.getSourceURI(),
                                                 db.getStatusMessage(),
                                                 Integer.toString(db.getStatusCode())));
        }
    }

    /**
     * When a client send a filter with a IDClause, it sends a luid.
     * So we have to replace the luid with the guid.
     * @param filter FilterClause
     * @param mapping ClientMapping
     */
    private void fixIDClause(FilterClause filter, ClientMapping mapping) {
        if (filter == null || mapping == null) {
            return;
        }
        LogicalClause logicalClause = filter.getClause();
        if (logicalClause == null) {
            return;
        }
        Clause recordClause = logicalClause.getOperands()[1];
        if (recordClause == null) {
            return;
        }
        if (recordClause instanceof IDClause) {
            fixIDClause((IDClause)recordClause, mapping);
        } else if (recordClause instanceof LogicalClause) {
            fixLogicalClause( (LogicalClause)recordClause, mapping);
        }
    }

    /**
     * Checks if some operand of the given clause is an <code>IDClause</code>.
     * If this happen, the LUID has to be replaced with the GUID using the given mapping
     * @param clause the clause
     * @param mapping the GUID-LUID mapping
     */
    private void fixLogicalClause(LogicalClause clause, ClientMapping mapping) {

        Clause[] operands = clause.getOperands();
        if (operands == null) {
            return;
        }
        for (int i=0; i<operands.length; i++) {
            if (operands[i] instanceof IDClause) {
                fixIDClause((IDClause)operands[i], mapping);
            } else if (operands[i] instanceof LogicalClause) {
                fixLogicalClause((LogicalClause)operands[i], mapping);
            }
        }
    }

    /**
     * Checks if some operand of the given clause is an <code>IDClause</code>.
     * If this happen, the LUID has to be replaced with the GUID using the given mapping
     */

    /**
     * Replaces in the given <code>IDClause</code> the LUID with the GUID using
     * the given mapping.
     * @param clause IDClause
     * @param mapping ClientMapping
     */
    private void fixIDClause(IDClause clause, ClientMapping mapping) {

        String[] values = clause.getValues();

        if (values == null) {
            return;
        }
        ClientMappingEntry mappingEntry = null;
        for (int i=0; i<values.length; i++) {
            mappingEntry = mapping.getClientMappingEntryByLuid(values[i]);
            if (mappingEntry == null || !mappingEntry.isValid()) {
                values[i] = null;
            } else {
                values[i] = mappingEntry.getGuid();
            }
        }
    }


    /**
     * Converts the given list of ids in an array of SyncItemKey
     * @param ids List
     * @return SyncItemKey[]
     */
    protected SyncItemKey[] getSyncItemKeyFromIds(List ids) {
        if (ids == null || ids.size() == 0) {
            return new SyncItemKey[0];
        }
        int size = ids.size();
        SyncItemKey[] keys = new SyncItemKey[size];
        Iterator it = ids.iterator();
        int i = 0;
        while (it.hasNext()) {
            keys[i++] = new SyncItemKey(it.next());
        }
        return keys;
    }

    /**
     * This method handles old officer version (v3) calling the authentication
     * method using java reflection. It must be removed in the next server versions.
     *
     * @param credential the credential to check
     * @return user the user relative to the given credentials or null if the
     *         credentials are not authenticated
     */
    protected Sync4jUser handleOldOfficerInstance(Cred credential) {
        //
        // Using reflection to handle the old implementations
        // of the Officer
        //
        Sync4jUser      user = null;
        Sync4jPrincipal p    = null;

        Method  method         = null;
        Boolean isAuthenticate = null;
        try {
            method = officer.getClass().getMethod("authenticate",
                                                  new Class[] {Cred.class}
                     );

            isAuthenticate =
                (Boolean)method.invoke(officer, new Object[] {credential});
        } catch(Exception e) {
            log.error("Error invoking 'authenticate' method ", e);
            return null;
        }

        try {
            if (isAuthenticate.booleanValue()) {
                p = new Sync4jPrincipal(credential.getAuthentication().getPrincipalId());
                readPrincipal(p);
                user = new Sync4jUser();
                user.setUsername(p.getUsername());
                readUser(user);
            }
        } catch (Exception e) {
            log.error("Error reading the principal or the user", e);
        }
        return user;
    }

    /**
     * Calls getType method on the given source using reflection. This is done to handle
     * old syncsources (done before v6)
     * @param source the syncsource
     * @return the type returned calling getType
     */
    private String callGetTypeWithReflection(SyncSource source) {
        //
        // Using reflection to handle previous syncsource version (v3)
        //
        Method method = null;
        try {

            method = source.getClass().getMethod("getType", (Class)null);

            return (String)method.invoke(officer, (Object)null);

        } catch (AbstractMethodError e) {
            //
            // Nothing to do. Maybe there is not the method (new v6 syncsource)
            //
        } catch(Exception e) {

            log.error("Error invoking 'getType' method", e);
        }
        return null;
    }

    /**
     * Calls authorize method on the office using reflection. This is done to handle
     * old officer (done before v6)
     * @param principal the principal
     * @param resource the resource to authorize.
     * @return Officer.AuthStatus.AUTHORIZED if method authorize returns true
     *         (this method in v3 returned just a boolean),
     *         Officer.AuthStatus.NOT_AUTHORIZED otherwise
     *
     */
    private Officer.AuthStatus callAuthorizeMethodWithReflection(Principal principal, String resource) {
        //
        // Using reflection to handle previous officer version (v3)
        //
        Method method = null;
        Boolean isAuthorized = null;
        try {
            method = officer.getClass().getMethod(
                                            "authorize",
                                            new Class[] {
                                                Principal.class,
                                                String.class
                                            }
                     );

            isAuthorized =
                (Boolean)method.invoke(officer, new Object[] {principal, resource});

            if (isAuthorized.booleanValue()) {
                return Officer.AuthStatus.AUTHORIZED;
            }

        } catch(Exception e) {

            log.error("Error invoking 'authorize' method", e);
        }
        return Officer.AuthStatus.NOT_AUTHORIZED;
    }

    /**
     * NOTE: this method must be moved in the funambol:core-framework
     * Strips the query string from the given uri
     * @param uri the uri
     * @return the uri without the query string
     */
    protected String stripQueryString(String uri) {
        int qMark = uri.indexOf('?');
        if (qMark != -1) {
            uri = uri.substring(0, qMark);
        }
        return uri;
    }

    /**
     * Returns the Items's GUID
     * @param mapping the LUID/GUID mapping
     * @param item The Item
     * @return the Items's GUID
     * @throws com.funambol.framework.core.Sync4jException
     */
    protected String getGuid(ClientMapping mapping, Item item ) throws Sync4jException {
        String guid = null;

        if (item.getSource() != null) {
            guid = item.getSource().getLocURI();
        } else {
            if (item.getTarget() != null) {
                String luid = item.getTarget().getLocURI();
                //
                // We have to use the mapping to find the GUID of the item
                // on the server
                //
                if (mapping == null) {
                    throw new Sync4jException("Error retriving data of the item: " + item +
                                      ". The engine is unable to access to the mapping for " +
                                      "the database '" + mapping.getDbURI() + "'");
                }
                guid = mapping.getMappedValueForLuid(luid);

                if (guid == null) {
                    throw new Sync4jException("Error retriving data of the item: " + item +
                                              ". The GUID for LUID '" + luid + "' is null");
                }
            }
        }
        return guid;
    }

    /**
     * Returns an array with all the registered sync sources
     * @return an array with all the registered sync sources
     * @throws Sync4jException if an error occurs
     */
    private SyncSource[] getAllServerSources() throws Sync4jException {
        Sync4jSource source = new Sync4jSource();
        Sync4jSource[] sources = null;
        try {
            sources = (Sync4jSource[]) store.read(source, new AllClause());
        } catch (PersistentStoreException ex) {
            log.error("Error reading sources", ex);
            throw new Sync4jException("Error reading sources", ex);
        }

        SyncSource[] syncSources = new SyncSource[sources.length];

        int cont = 0;
        for (Sync4jSource sync4jSource : sources) {
            syncSources[cont++] = getServerSource(sync4jSource);
        }

        return syncSources;
    }

    /**
     * Returns the SyncSource related to the given Sync4jSource object
     * @param sync4jSource the Sync4jSource object
     * @return the SyncSource related to the given Sync4jSource object
     * @throws Sync4jException if an error occurs
     */
    private SyncSource getServerSource(Sync4jSource sync4jSource) throws Sync4jException {
        SyncSource syncSource = null;
        
        try {
            syncSource =
                (SyncSource)configuration.getBeanInstanceByName(sync4jSource.getConfig());

            return syncSource;
        } catch (Exception e){
            String msg = "Unable to create sync source "
                       + sync4jSource
                       + ": "
                       + e.getMessage();

            log.error(msg, e);
            throw new Sync4jException(msg, e);
        }
    }
}
