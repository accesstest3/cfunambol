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
package com.funambol.framework.core;

import java.util.*;

/**
 * This class corresponds to the &lt;DataStore&gt; tag in the SyncML devinf DTD
 *
 * @version $Id: DataStore.java,v 1.3 2007/07/30 22:10:05 stefano_fornari Exp $
 */
public class DataStore implements java.io.Serializable {

    // ------------------------------------------------------------ Private data

    private SourceRef       sourceRef  ;
    private String          displayName;
    private long            maxGUIDSize;
    private CTInfo          rxPref;
    private ArrayList       rxs = new ArrayList();
    private CTInfo          txPref;
    private ArrayList       txs    = new ArrayList();
    private ArrayList       ctCaps = new ArrayList();
    private DSMem           dsMem;
    private Boolean         supportHierarchicalSync;
    private SyncCap         syncCap;
    private ArrayList       filterRxs  = new ArrayList();
    private ArrayList       filterCaps = new ArrayList();

    // ------------------------------------------------------------ Constructors

    /**
     * In order to expose the server configuration like WS this constructor
     * must be public
     */
    public DataStore() {}

    /**
     * Creates a new DataStore object with the given input information
     *
     * @param sourceRef   The reference URI for a local datastore - NOT NULL
     * @param displayName The display name of the datastore
     * @param maxGUIDSize The maximum GUID size. Set to -1 if the Maximum GUID
     *                    size is unknown or unspecified. Otherwise, this
     *                    parameter should be a positive number.
     * @param rxPref The preferred type and version of the content type
     *               received by the device - NOT NULL
     * @param rxs An array of the supported type and version of the content type
     *            received by the device
     * @param txPref The supported type and version of the content type
     *               transmitted by the device - NOT NULL
     * @param txs An array of the supported type and version of the content type
     *            transmitted by the device - NOT NULL
     * @param ctCaps An array of content type capabilities
     * @param dsMem  The maximum memory and item identifier for the datastore
     * @param supportHierarchicalSync The support for hierarchical sync
     * @param syncCap    The synchronization capabilities - NOT NULL
     * @param filterRxs  An array of supported filter grammars that can be
     *                   received by the datastore
     * @param filterCaps An array of filtering capabilities
     */
    public DataStore(final SourceRef sourceRef            ,
                     final String displayName             ,
                     final long maxGUIDSize               ,
                     final CTInfo rxPref                  ,
                     final CTInfo[] rxs                   ,
                     final CTInfo txPref                  ,
                     final CTInfo[] txs                   ,
                     final CTCap[] ctCaps                 ,
                     final DSMem dsMem                    ,
                     final boolean supportHierarchicalSync,
                     final SyncCap syncCap                ,
                     final CTInfo[] filterRxs             ,
                     final FilterCap[] filterCaps         ) {

        setSourceRef(sourceRef);
        this.displayName = displayName;
        this.maxGUIDSize = maxGUIDSize;
        setRxPref(rxPref);
        setRxs   (rxs)   ;
        setTxPref(txPref);
        setTxs   (txs)   ;
        setCTCaps(ctCaps);
        this.dsMem = dsMem;
        this.supportHierarchicalSync  = (supportHierarchicalSync)
                                      ? new Boolean(supportHierarchicalSync)
                                      : null;
        setSyncCap   (syncCap)   ;
        setFilterRxs (filterRxs) ;
        setFilterCaps(filterCaps);
    }


    /**
     * Creates a new DataStore object with the given input information
     *
     * @param sourceRef   The reference URI for a local datastore - NOT NULL
     * @param displayName The display name of the datastore
     * @param maxGUIDSize The maximum GUID size. Set to -1 if the Maximum GUID
     *                    size is unknown or unspecified. Otherwise, this
     *                    parameter should be a positive number.
     * @param rxPref The preferred type and version of the content type
     *               received by the device - NOT NULL
     * @param rxs An array of the supported type and version of the content type
     *            received by the device
     * @param txPref The supported type and version of the content type
     *               transmitted by the device - NOT NULL
     * @param txs An array of the supported type and version of the content type
     *            transmitted by the device - NOT NULL
     * @param dsMem  The maximum memory and item identifier for the datastore
     * @param syncCap    The synchronization capabilities - NOT NULL
     */
    public DataStore(final SourceRef sourceRef   ,
                     final String displayName    ,
                     final long maxGUIDSize      ,
                     final CTInfo rxPref         ,
                     final CTInfo[] rxs          ,
                     final CTInfo txPref         ,
                     final CTInfo[] txs          ,
                     final DSMem dsMem           ,
                     final SyncCap syncCap       ) {

        this(sourceRef, displayName, maxGUIDSize, rxPref, rxs, txPref, txs,
             null, dsMem, false, syncCap, null, null
        );
    }

    // ---------------------------------------------------------- Public methods

    /**
     * Gets the reference URI for a local datastore
     *
     * @return the sourceRef object
     */
    public SourceRef getSourceRef() {
        return sourceRef;
    }

    /**
     * Sets the reference URI for a local datastore
     *
     * @param sourceRef the reference URI
     */
    public void setSourceRef(SourceRef sourceRef) {
        if (sourceRef == null) {
            throw new IllegalArgumentException("sourceRef cannot be null");
        }
        this.sourceRef = sourceRef;
    }

    /**
     * Gets the display name of the datastore
     *
     * @return the display name of the datastore
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Sets the display name of the datastore
     *
     * @param displayName the display name of the datastore
     */
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Gets the maximum GUID size
     *
     * @return the maximum GUID size
     */
    public long getMaxGUIDSize() {
        return maxGUIDSize;
    }

    /**
     * Sets the maximum GUID size
     *
     * @param maxGUIDSize the maximum GUID size
     */
    public void setMaxGUIDSize(long maxGUIDSize) {
        if (maxGUIDSize < 0) {
            throw new IllegalArgumentException("illegal maxGUIDSize value");
        }
        this.maxGUIDSize = maxGUIDSize;
    }

    /**
     * Gets the CTInfo corresponds to &lt;Rx-Pref&gt; element
     *
     * @return the CTInfo corresponds to &l;tRx-Pref&gt; element
     */
    public CTInfo getRxPref() {
        return rxPref;
    }

    /**
     * Sets the preferred type and version of the content type received by
     * the device
     *
     * @param rxPref the preferred type and version of a content type
     */
    public void setRxPref(CTInfo rxPref) {
        if (rxPref == null) {
            throw new IllegalArgumentException("rxPref cannot be null");
        }
        this.rxPref = rxPref;
    }

    /**
     * Gets the array of the supported type and version of the content type
     * received by the device
     *
     * @return an array of CTInfo corresponds to &lt;Rx&gt; element
     */
    public ArrayList getRxs() {
        return rxs;
    }

    /**
     * Sets the supported type and version of a content type received by the
     * device
     *
     * @param rxsCTI an array of supported type and version of a content type
     */
    public void setRxs(CTInfo[] rxsCTI) {
        if (rxsCTI != null) {
            this.rxs.clear();
            this.rxs.addAll(Arrays.asList(rxsCTI));
        } else {
            this.rxs = null;
        }
    }

    /**
     * Sets the supported type and version of a content type received by the
     * device
     *
     * @param rxsCTI an array of supported type and version of a content type
     */
    public void setRxs(ArrayList rxsCTI) {
        if (rxsCTI != null) {
            this.rxs.clear();
            this.rxs.addAll(rxsCTI);
        } else {
            this.rxs = null;
        }
    }

    /**
     * Gets the CTInfo corresponds to &lt;Tx-Pref&gt; element
     *
     * @return the CTInfo corresponds to &lt;Tx-Pref&gt; element
     */
    public CTInfo getTxPref() {
        return txPref;
    }

    /**
     * Sets the supported type and version of the content type transmitted by
     * the device
     *
     * @param txPref the preferred type and version of a content type
     */
    public void setTxPref(CTInfo txPref) {
        if (txPref == null) {
            throw new IllegalArgumentException("txPref cannot be null");
        }
        this.txPref = txPref;
    }

    /**
     * Gets an array of the supported type and version of the content type
     * transmitted by the device
     *
     * @return an array of CTInfo corresponds to &lt;Tx&gt; element
     */
    public ArrayList getTxs() {
        return txs;
    }

    /**
     * Sets an array of supported type and version of the content type
     * transmitted by the device
     *
     * @param txsCTI an array of supported type and version of a content type
     */
    public void setTxs(CTInfo[] txsCTI) {
        if (txsCTI != null) {
            this.txs.clear();
            this.txs.addAll(Arrays.asList(txsCTI));
        } else {
            this.txs = null;
        }
    }

    /**
     * Sets an array of supported type and version of the content type
     * transmitted by the device
     *
     * @param txsCTI an array of supported type and version of a content type
     */
    public void setTxs(ArrayList txsCTI) {
        if (txsCTI != null) {
            this.txs.clear();
            this.txs.addAll(txsCTI);
        } else {
            this.txs = null;
        }
    }

    /**
     * Gets an array of content type capabilities
     *
     * @return an array of content type capabilities
     */
    public ArrayList getCTCaps() {
        return this.ctCaps;
    }

    /**
     * Sets the array of content type capabilities
     *
     * @param ctCaps the array of content type capabilities
     */
    public void setCTCaps(CTCap[] ctCaps) {
        if (ctCaps == null) {
            this.ctCaps = null;
        } else {
            this.ctCaps.clear();
            this.ctCaps.addAll(Arrays.asList(ctCaps));
        }
    }

    /**
     * Sets the array of content type capabilities
     *
     * @param ctCaps the array of content type capabilities
     */
    public void setCTCaps(ArrayList ctCaps) {
        if (ctCaps == null) {
            this.ctCaps = null;
        } else {
            this.ctCaps.clear();
            this.ctCaps.addAll(ctCaps);
        }
    }

    /**
     * Gets the maximum memory and item identifier for the datastore
     *
     * @return the maximum memory and item identifier for the datastore
     */
    public DSMem getDSMem() {
        return dsMem;
    }

    /**
     * Sets the maximum memory and item identifier for the datastore
     *
     * @param dsMem the maximum memory and item identifier for the datastore
     */
    public void setDSMem(DSMem dsMem) {
        this.dsMem = dsMem;
    }

    /**
     * Specify the support for hierarchical sync
     *
     * @param supportHierarchicalSync the Boolean value of
     *        SupportHierarchicalSync property
     */
    public void setSupportHierarchicalSync(Boolean supportHierarchicalSync) {
        this.supportHierarchicalSync = (supportHierarchicalSync.booleanValue())
                                     ? supportHierarchicalSync
                                     : null;
    }

    /**
     * Gets the value of SupportHierarchicalSync property
     *
     * @return true if hierarchical sync is supported, false otherwise
     */
    public boolean isSupportHierarchicalSync() {
        return (supportHierarchicalSync != null);
    }

    /**
     * Gets the value of SupportHierarchicalSync property
     *
     * @return true if hierarchical sync is supported, false otherwise
     */
    public Boolean getSupportHierarchicalSync() {
        if (supportHierarchicalSync == null || !supportHierarchicalSync.booleanValue()) {
            return null;
        }
        return supportHierarchicalSync;
    }

    /**
     * Gets the synchronization capabilities of a datastore.
     *
     * @return the synchronization capabilities of a datastore.
     */
    public SyncCap getSyncCap() {
        return syncCap;
    }

    /**
     * Sets the synchronization capabilities of a datastore.
     *
     * @param syncCap the synchronization capabilities of a datastore
     */
    public void setSyncCap(SyncCap syncCap) {
        if (syncCap == null) {
            throw new IllegalArgumentException("syncCap cannot be null");
        }
        this.syncCap = syncCap;
    }

    /**
     * Gets an array of supported filter grammars that can be received by the
     * datastore
     *
     * @return an array of CTInfo corresponds to &lt;Filter-Rx&gt;
     *         element
     */
    public ArrayList getFilterRxs() {
        return filterRxs;
    }

    /**
     * Sets an array of supported filter grammars that can be received by the
     * datastore
     *
     * @param filterRxsCTI an array of supported filter grammars
     */
    public void setFilterRxs(CTInfo[] filterRxsCTI) {
        if (filterRxsCTI != null) {
            this.filterRxs.clear();
            this.filterRxs.addAll(Arrays.asList(filterRxsCTI));
        } else {
            this.filterRxs = null;
        }
    }

    /**
     * Sets an array of supported filter grammars that can be received by the
     * datastore
     *
     * @param filterRxsCTI an array of supported filter grammars
     */
    public void setFilterRxs(ArrayList filterRxsCTI) {
        if (filterRxsCTI != null) {
            this.filterRxs.clear();
            this.filterRxs.addAll(filterRxsCTI);
        } else {
            this.filterRxs = null;
        }
    }

    /**
     * Gets an array of filtering capabilities
     *
     * @return an array of filtering capabilities
     */
    public ArrayList getFilterCaps() {
        return filterCaps;
    }

    /**
     * Sets an array of filtering capabilities
     *
     * @param filterCaps an array of filtering capabilities
     */
    public void setFilterCaps(FilterCap[] filterCaps) {
        if (filterCaps != null) {
            this.filterCaps.clear();
            this.filterCaps.addAll(Arrays.asList(filterCaps));
        } else {
            this.filterCaps = null;
        }
    }

    /**
     * Sets an array of filtering capabilities
     *
     * @param filterCaps an array of filtering capabilities
     */
    public void setFilterCaps(ArrayList filterCaps) {
        if (filterCaps != null) {
            this.filterCaps.clear();
            this.filterCaps.addAll(filterCaps);
        } else {
            this.filterCaps = null;
        }
    }
}
