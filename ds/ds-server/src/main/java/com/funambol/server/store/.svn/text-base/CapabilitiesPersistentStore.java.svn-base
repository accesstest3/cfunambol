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
package com.funambol.server.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.funambol.framework.core.*;
import com.funambol.framework.filter.AllClause;
import com.funambol.framework.filter.Clause;
import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.server.Capabilities;
import com.funambol.framework.server.store.BasePersistentStore;
import com.funambol.framework.server.store.ConfigPersistentStoreException;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.framework.server.store.PersistentStoreException;
import com.funambol.framework.server.store.PreparedWhere;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.beans.BeanInitializationException;
import com.funambol.framework.tools.beans.LazyInitBean;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

/**
 * This is the store for capabilities information in the server.
 * Currently it persistes the following classes:
 * <ul>
 * <li>com.funambol.framework.server.Capabilities</li>
 * </ul>
 *
 * @version $Id: CapabilitiesPersistentStore.java,v 1.4 2008-06-25 15:11:18 luigiafassina Exp $
 */
public class CapabilitiesPersistentStore
extends BasePersistentStore
implements PersistentStore, java.io.Serializable, LazyInitBean {

    // --------------------------------------------------------------- Constants
    public static final int SQL_INSERT_CAPABILITIES                    = 0;
    public static final int SQL_UPDATE_CAPABILITIES                    = 1;

    public static final int SQL_SELECT_CAPABILITIES_BY_ID              = 2;

    public static final int SQL_INSERT_DATASTORE                       = 3;
    public static final int SQL_INSERT_DATASTORE_RX                    = 4;
    public static final int SQL_INSERT_DATASTORE_TX                    = 5;
    public static final int SQL_INSERT_DATASTORE_FILTER_RX             = 6;
    public static final int SQL_INSERT_DATASTORE_FILTER_CAPS           = 7;
    public static final int SQL_INSERT_DATASTORE_DSMEM                 = 8;
    public static final int SQL_INSERT_DATASTORE_CTCAPS_V1_2           = 9;
    public static final int SQL_INSERT_DATASTORE_CTCAPS_PROPERTY       = 10;
    public static final int SQL_INSERT_DATASTORE_CTCAPS_PROPERTY_PARAM = 11;

    public static final int SQL_INSERT_DATASTORE_CTCAPS_V1_1           = 12;

    public static final int SQL_UPDATE_DATASTORE                       = 13;
    public static final int SQL_DELETE_CAPABILITY                      = 14;

    public static final int SQL_SELECT_DATASTORES_BY_CAPID             = 15;
    public static final int SQL_SELECT_DATASTORES_RX                   = 16;
    public static final int SQL_SELECT_DATASTORES_TX                   = 17;
    public static final int SQL_SELECT_DATASTORES_FILTER_RX            = 18;
    public static final int SQL_SELECT_DATASTORES_FILTER_CAP           = 19;
    public static final int SQL_SELECT_DATASTORES_DSMEM                = 20;
    public static final int SQL_SELECT_DATASTORES_CTCAPS               = 21;
    public static final int SQL_SELECT_DATASTORES_CTCAPS_PROP          = 22;
    public static final int SQL_SELECT_DATASTORES_CTCAPS_PROP_PARAMS   = 23;
    public static final int SQL_SELECT_CAPS_BY_DEVINF                  = 24;

    public static final int SQL_SELECT_ALL_CAPABILTIES_ID              = 25;
    public static final int SQL_COUNT_ALL_CAPABILTIES                  = 26;

    public static final int SQL_INSERT_EXT                             = 27;
    public static final int SQL_SELECT_EXT_BY_CAPID                    = 28;

    /** Namespace to use getting a new id */
    public static final String NS_DATASTORE      = "datastore";

    /** Namespace to use getting a new id */
    public static final String NS_CAPABILITY     = "capability";

    /** Namespace to use getting a new id */
    public static final String NS_EXT            = "ext";

    /** Namespace to use getting a new id */
    public static final String NS_CTCAP          = "ctcap";

    /** Namespace to use getting a new id */
    public static final String NS_CTCAP_PROPERTY = "ctcap_property";

    //------------------------------------------------------------- Private data
    //Transient keyword will disable serialisation for this member
    private transient FunambolLogger log = FunambolLoggerFactory.getLogger("server.store.capabilities");

    private DBIDGenerator dataStoreIDGenerator     = null;
    private DBIDGenerator capabilityIDGenerator    = null;
    private DBIDGenerator extIDGenerator           = null;
    private DBIDGenerator ctCapIDGenerator         = null;
    private DBIDGenerator ctCapPropertyIDGenerator = null;

    // -------------------------------------------------------------- Properties
    protected String[] sql = null;

    public void setSql(String[] sql) {
        this.sql = sql;
    }

    public String[] getSql() {
        return this.sql;
    }

    // ---------------------------------------------------------- Public methods

    @Override
    public void init() throws BeanInitializationException {
        super.init();
        if (dataStoreIDGenerator == null) {
            
            dataStoreIDGenerator     = DBIDGeneratorFactory.getDBIDGenerator(NS_DATASTORE,      coreDataSource);
            capabilityIDGenerator    = DBIDGeneratorFactory.getDBIDGenerator(NS_CAPABILITY,     coreDataSource);
            extIDGenerator           = DBIDGeneratorFactory.getDBIDGenerator(NS_EXT,            coreDataSource);
            ctCapIDGenerator         = DBIDGeneratorFactory.getDBIDGenerator(NS_CTCAP,          coreDataSource);
            ctCapPropertyIDGenerator = DBIDGeneratorFactory.getDBIDGenerator(NS_CTCAP_PROPERTY, coreDataSource);

        }
    }

    /**
     * Read the Capabilities from the DataBase.
     *
     * @param o The object that must be a Capabilities object for this method
     * @return true if capabilities saved, false otherwise
     * @throws PersistentStoreException
     */
    public boolean store(Object o) throws PersistentStoreException {
        if (o instanceof Capabilities) {
            Capabilities cap = (Capabilities) o;
            if (log.isTraceEnabled()) {
                log.trace("Storing capabilities with id '"+cap.getId()+"'");
            }
            Connection conn = null;
            PreparedStatement stmt = null;
            ResultSet rs = null;
            int n = 0;

            try {
                conn = coreDataSource.getConnection();

                if (cap.getId() != null) {
                    //
                    // check if id already exists
                    //
                    conn.setReadOnly(true);

                    stmt = conn.prepareStatement(sql[SQL_SELECT_CAPABILITIES_BY_ID]);
                    stmt.setInt(1, cap.getId().intValue());

                    rs = stmt.executeQuery();
                    if (rs.next() == false) {
                        //
                        //not found, do ADD
                        //
                        if (log.isTraceEnabled()) {
                            log.trace("Capabilities with id '"+cap.getId().intValue()+
                                      "' not found in db.");
                        }
                        n = 0;

                        //
                        // We close the connection and a new one is created since
                        // the previous one was read-only
                        //
                        DBTools.close(conn, stmt, null);
                        conn = coreDataSource.getConnection();

                    } else {
                        //
                        //get capabilities from db with the cap.getId()
                        //
                        if (log.isTraceEnabled()) {
                            log.trace("Capabilities with id '"+cap.getId().intValue()+
                                      "' found in db. Merge will be done");
                        }
                        Capabilities dbCap = new Capabilities();
                        dbCap.setId(cap.getId());
                        this.readById(dbCap);
                        //
                        //delete capabilities from db
                        //
                        //
                        // We close the connection and a new one is created since
                        // the previous one was read-only
                        //
                        DBTools.close(conn, stmt, null);
                        conn = coreDataSource.getConnection();

                        stmt = conn.prepareStatement(sql[SQL_DELETE_CAPABILITY]);
                        stmt.setInt(1, cap.getId().intValue());
                        n = stmt.executeUpdate();

                        DBTools.close(null, stmt, null);

                        if (log.isTraceEnabled()) {
                            log.trace("Capabilities with id '"+cap.getId().intValue()+
                                      "' deleted from db.");
                        }
                        //
                        //merge capabilities retrieved from db with informations to be added
                        //
                        ArrayList dbDataStores = dbCap.getDevInf().getDataStores();
                        int sizeCapDS = cap.getDevInf().getDataStores().size();

                        for (int i = 0; i < sizeCapDS; i++) {
                            DataStore ds = (DataStore)cap.getDevInf().getDataStores().get(i);
                            boolean updated = false;
                            for (int j = 0; j < dbDataStores.size(); j++) {
                                DataStore dbDs = (DataStore) dbDataStores.get(j);
                                //
                                // Is not possibly to use the displayName to
                                // check if datastore already exist, because
                                // this field is not mandatory: so we have to
                                // use the SourceRef.
                                //
                                if (dbDs.getSourceRef().getValue().equals(
                                        ds.getSourceRef().getValue())) {
                                    //
                                    // If new caps contains a datastore that
                                    // already is in db overwrite in db the
                                    // new datastore
                                    //
                                    dbDataStores.set(j, ds);
                                    updated = true;
                                    break;
                                }
                            }
                            if (!updated) {
                                //if new cap contains a datastore that does not exist in db
                                //add in db the new datastore
                                dbDataStores.add(ds);
                            }
                        }
                        //
                        //add all the datastores in the object that will be saved in db
                        //
                        cap.getDevInf().getDataStores().clear();
                        cap.getDevInf().getDataStores().addAll(dbDataStores);

                        //
                        // Merge Ext retrieved from db with Exts into input
                        // Capabilities
                        //
                        ArrayList dbExts      = dbCap.getDevInf().getExts();
                        ArrayList newExts     = cap.getDevInf().getExts()  ;
                        int       sizeNewExts = newExts.size()             ;

                        Ext extNew = null;
                        Ext extOld = null;
                        boolean updated = false;
                        for (int i = 0; i < sizeNewExts; i++) {
                            extNew = (Ext)newExts.get(i);
                            updated = false;
                            for (int j = 0; j < dbExts.size(); j++) {
                                extOld = (Ext)dbExts.get(j);
                                if (extOld.getXNam().equals(extNew.getXNam())) {
                                    //
                                    // If already existing an Ext then it is
                                    // overwrite with the new informations
                                    //
                                    dbExts.set(j,extNew);
                                    updated = true;
                                    break;
                                }
                                if (!updated) {
                                    //
                                    // If new capabilities contains a new Ext,
                                    // then add it into list of Ext
                                    //
                                    dbExts.add(extNew);
                                }
                            }//for existing exts
                        }//for new exts

                        //
                        // Set all Ext in the Capabilities sent
                        //
                        cap.getDevInf().getExts().clear();
                        cap.getDevInf().getExts().addAll(dbExts);
                        n = 0;
                    }
                }
                if (n == 0) {
                    //
                    //ADD CAPABILITIES IN DB and setID to capabilities object
                    //
                    if (cap.getId() == null) {
                        long capId = getNextCapabilityId();
                        cap.setId(new Long(capId));
                    }
                    //
                    //ADD CAPABILITIES
                    //
                    stmt = conn.prepareStatement(sql[SQL_INSERT_CAPABILITIES]);
                    prepareAddDeviceCaps(stmt, cap);
                    stmt.executeUpdate();
                    DBTools.close(null, stmt, null);
                    //
                    //add to device_datastore table
                    //
                    DataStore dataStore = null;
                    int sizeDS = cap.getDevInf().getDataStores().size();

                    for (int i = 0; i < sizeDS; i++) {
                        dataStore = (DataStore) cap.getDevInf().getDataStores().get(i);
                        if (null == dataStore.getDisplayName()) {
                            dataStore.setDisplayName(dataStore.getSourceRef().getValue());
                        }
                        long dataStoreId = getNextDataStoreId();
                        stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE]);
                        prepareAddDeviceDatastore(stmt,
                                                  dataStore,
                                                  dataStoreId,
                                                  cap.getId().intValue());
                        stmt.executeUpdate();
                        DBTools.close(null, stmt, null);
                        //
                        //for each datastore add cttype_rxPref and cttype_rx list
                        //
                        CTInfo rxPref = dataStore.getRxPref();
                        if (rxPref != null) {
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_RX]);
                            prepareAddDeviceDatastoreRx(stmt, rxPref, dataStoreId, true);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }

                        ArrayList rxList = dataStore.getRxs();
                        CTInfo cti = null;
                        //
                        //the position on wich the rxPref is found in rx list
                        //
                        int positionRx=-1;
                        for (int j = 0; j < rxList.size(); j++) {
                            cti = (CTInfo) rxList.get(j);
                            //
                            //if current RX is identical to RxPref
                            // save the position on wich it is and do not add it in DB
                            //
                            if((rxPref!=null)                              &&
                               (rxPref.getCTType().equals(cti.getCTType()))&&
                               (rxPref.getVerCT()!=null)                   &&
                               (rxPref.getVerCT().equals(cti.getVerCT()))) {
                                    positionRx=j;
                                    continue;
                            }
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_RX]);
                            prepareAddDeviceDatastoreRx(stmt, cti, dataStoreId, false);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }
                        //
                        //remove from the list the rx identical to rxPref
                        //
                        if(positionRx != -1) {
                            rxList.remove(positionRx);
                        }
                        //
                        //for each datastore add cttype_txPref and cttype_tx list
                        //
                        CTInfo txPref = dataStore.getTxPref();
                        if (txPref != null) {
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_TX]);
                            prepareAddDeviceDatastoreTx(stmt, txPref, dataStoreId, true);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }

                        ArrayList txList = dataStore.getTxs();
                        cti = null;
                        //
                        //the position on wich the txPref is found in rx list
                        //
                        int positionTx=-1;
                        for (int j = 0; j < txList.size(); j++) {
                            cti = (CTInfo) txList.get(j);
                            //
                            //if current TX is identical to TxPref
                            // save the position on wich it is and do not add it in DB
                            //
                            if((txPref!=null)                              &&
                               (txPref.getCTType().equals(cti.getCTType()))&&
                               (txPref.getVerCT()!=null)                   &&
                               (txPref.getVerCT().equals(cti.getVerCT()))) {
                                    positionTx=j;
                                    continue;
                            }
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_TX]);
                            prepareAddDeviceDatastoreTx(stmt, cti, dataStoreId, false);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }
                        //
                        //remove from the list the tx identical to txPref
                        //
                        if(positionTx != -1) {
                            txList.remove(positionTx);
                        }
                        //
                        //for each datastore add filter_rx
                        //
                        ArrayList rxFilterList = dataStore.getFilterRxs();
                        cti = null;
                        for (int j = 0; j < rxFilterList.size(); j++) {
                            cti = (CTInfo) rxFilterList.get(j);
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_FILTER_RX]);
                            prepareAddDeviceDatastoreFilterRx(stmt, cti, dataStoreId);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }
                        //
                        //for each datastore add filter_caps
                        //
                        ArrayList capsFilterList = dataStore.getFilterCaps();
                        FilterCap filterCap = null;
                        for (int j = 0; j < capsFilterList.size(); j++) {
                            filterCap = (FilterCap) capsFilterList.get(j);
                            //prepare statement
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_FILTER_CAPS]);
                            prepareAddDeviceDatastoreFilterCaps(stmt, filterCap, dataStoreId);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }
                        //
                        //for each datastore add ds_mem
                        //
                        DSMem dsMem = dataStore.getDSMem();
                        if (dsMem != null) {
                            stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_DSMEM]);
                            prepareAddDeviceDatastoreDsMem(stmt, dsMem, dataStoreId);
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }
                        //
                        //for each datastore add ctcap if is version 1.2
                        //
                        if (cap.getDevInf().getVerDTD().getValue().equals("1.2")) {
                            ArrayList ctCaps = dataStore.getCTCaps();
                            CTCap ctCap = null;
                            for (int j = 0; j < ctCaps.size(); j++) {
                                ctCap = (CTCap) ctCaps.get(j);
                                long ctCapId = getNextCTCapId();
                                stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_CTCAPS_V1_2]);
                                prepareAddDeviceDatastoreCtcap(stmt, ctCap, ctCapId, dataStoreId);
                                stmt.executeUpdate();
                                DBTools.close(null, stmt, null);
                                ///
                                //for each ctCap add properties
                                //
                                ArrayList properties = ctCap.getProperties();
                                Property prop = null;
                                if( properties!=null ) {
                                    for (int k = 0; k < properties.size(); k++) {
                                        prop = (Property) properties.get(k);
                                        long propId = getNextCTCapPropertyId();
                                        stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_CTCAPS_PROPERTY]);

                                        prepareAddDeviceDatastoreCtcapProperty(stmt, prop, propId, ctCapId);
                                        stmt.executeUpdate();
                                        DBTools.close(null, stmt, null);
                                        //
                                        //for each property add params
                                        //
                                        if (prop.getPropParams() != null) {
                                            ArrayList propParams = prop.getPropParams();
                                            PropParam propParam = null;
                                            for (int idx = 0; idx < propParams.size(); idx++) {
                                                propParam = (PropParam) propParams.get(idx);
                                                stmt = conn.prepareStatement(sql[SQL_INSERT_DATASTORE_CTCAPS_PROPERTY_PARAM]);
                                                prepareAddDeviceDatastoreCtcapPropertyParam(stmt, propParam, propId);
                                                stmt.executeUpdate();
                                                DBTools.close(null, stmt, null);
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                    //
                    // Add to device_ext table
                    //
                    ArrayList exts = cap.getDevInf().getExts();
                    if (exts != null) {
                        int size = exts.size();
                        Ext ext = null;
                        long extId = 0;
                        for (int i = 0; i < size; i++) {
                            ext = (Ext)exts.get(i);
                            stmt = conn.prepareStatement(sql[SQL_INSERT_EXT]);
                            extId = getNextExtId();
                            prepareAddDeviceExt(stmt, ext, extId, cap.getId().intValue());
                            stmt.executeUpdate();
                            DBTools.close(null, stmt, null);
                        }//for on exts
                    }
                }
                return true;
            } catch (SQLException e) {
                throw new PersistentStoreException(
                    "Error storing capabilities " + e.getMessage(), e);
            } catch (Exception e) {
                throw new PersistentStoreException(
                    "Error storing capabilities " + e.getMessage(), e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
        }
        return false;
    }

    /**
     * Read the all Capabilities from the DataBase that match a given set of conditions.
     *
     * @param clause condition where for select
     * @return array with found Capabilities
     * @throws PersistentStoreException
     */
    public Capabilities[] read(Clause clause) throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_SELECT_ALL_CAPABILTIES_ID];
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " where " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 1, where.parameters[i]);
                }
            }
            rs = stmt.executeQuery();
            ArrayList capList = new ArrayList();
            while (rs.next()) {
                Capabilities cap = new Capabilities();
                cap.setId(new Long(rs.getLong(1)));
                readById(cap);
                capList.add(cap);
            }
            Capabilities[] result = new Capabilities[capList.size()];
            for (int i = 0; i < capList.size(); i++) {
                result[i] = (Capabilities) capList.get(i);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistentStoreException(
                "Error reading capabilities" + e.getMessage(), e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    /**
     * Counts the all Capabilities from the DataBase that match a given set of conditions.
     *
     * @param clause condition where for select
     * @return number of found Capabilities
     * @throws PersistentStoreException
     */
    public int count(Clause clause) throws PersistentStoreException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            String query = sql[SQL_COUNT_ALL_CAPABILTIES];
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                if (where.sql.length() > 0) {
                    query += " where " + where.sql;
                }
            }
            stmt = conn.prepareStatement(query);
            if ((clause != null) && (!(clause instanceof AllClause))) {
                PreparedWhere where = sqlHelperClause.getPreparedWhere(clause);
                for (int i = 0; i < where.parameters.length; ++i) {
                    stmt.setObject(i + 1, where.parameters[i]);
                }
            }
            rs = stmt.executeQuery();
            int result = 0;
            if (rs.next()) {
                result = rs.getInt(1);
            }
            return result;
        } catch (SQLException e) {
            throw new PersistentStoreException(
                "Error counting capabilities" + e.getMessage(), e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    public Object[] read(Class objClass) throws PersistentStoreException {
        return null;
    }

    public Object[] read(Object o, Clause clause)
    throws PersistentStoreException {
        return null;
    }

    public int count(Object o, Clause clause) throws PersistentStoreException {
        return 0;
    }

    public boolean store(Object o, String operation)
    throws PersistentStoreException {
        return false;
    }

    /**
     * Deletes a Capabilities object from db
     * @param o the Capabilities object to be deleted from db (it must have set the id)
     * @return true id obect was deleted, false otherwise
     * @throws PersistentStoreException
     */
    public boolean delete(Object o) throws PersistentStoreException {
        if (o instanceof Capabilities) {
            Capabilities caps = (Capabilities)o;
            if (caps == null || caps.getId() == null) {
                return false;
            }
            if (log.isTraceEnabled()) {
                log.trace("Deleting capabilities with id '"+caps.getId().intValue() +
                           "' from DB.");
            }
            Connection conn = null;
            PreparedStatement stmt = null;
            try {

                conn = coreDataSource.getConnection();
                stmt = conn.prepareStatement(sql[SQL_DELETE_CAPABILITY]);
                stmt.setInt(1, caps.getId().intValue());
                int result=stmt.executeUpdate();
                DBTools.close(null, stmt, null);
                if(result==1) {
                    if (log.isTraceEnabled()) {
                        log.trace("Capabilities with id '"+caps.getId().intValue() +
                                   "' deleted from DB.");
                    }
                    return true;
                } else {
                    if (log.isTraceEnabled()) {
                        log.trace("Capabilities with id '"+caps.getId().intValue() +
                                   "' was not found from DB.");
                    }
                    return false;
                }
            } catch (SQLException e) {
                throw new PersistentStoreException(
                    "Error deleting capabilities " + e.getMessage(), e);
            } catch (Exception e) {
                throw new PersistentStoreException(
                    "Error deleting capabilities " + e.getMessage(), e);
            } finally {
                DBTools.close(conn, stmt, null);
            }
        }
        return false;
    }

    /**
     * Read the Capabilities from the DataBase.
     *
     * @param o The object that must be a Capabilities object for this method
     * @return true if capabilities found, false otherwise
     * @throws PersistentStoreException
     */
    public boolean read(Object o) throws PersistentStoreException {
        if (o instanceof Capabilities) {
            Capabilities cap = (Capabilities) o;
            try {
                if (cap.getId() != null) {
                    //
                    //search by ID
                    //
                    return readById(cap);
                } else {
                    if (cap.getDevInf() != null) {
                        //
                        //search by DevInf
                        //
                        return readByDevInf(cap);
                    } else {
                        return false;
                    }
            }
            } catch (PersistentStoreException e) {
                throw e;
            }
        } else {
            return false;
        }
    }


    // --------------------------------------------------------- Private Methods
    /**
     * Prepares the statement for adding a capabilities
     *
     * @param stmt statement to be prepared
     * @param cap  object with the informations to be saved
     * @throws SQLException
     */
    private void prepareAddDeviceCaps(PreparedStatement stmt, Capabilities cap)
    throws SQLException {
        stmt.setInt(1, cap.getId().intValue());
        stmt.setString(2, cap.getDevInf().getVerDTD().getValue());
        stmt.setString(3, cap.getDevInf().getMan());
        stmt.setString(4, cap.getDevInf().getMod());
        stmt.setString(5, cap.getDevInf().getFwV());
        stmt.setString(6, cap.getDevInf().getSwV());
        stmt.setString(7, cap.getDevInf().getHwV());
        if ((cap.getDevInf().getUTC() != null)&&
            (cap.getDevInf().getUTC().booleanValue())) {
                stmt.setString(8,"Y");
        } else {
            stmt.setString(8, "N");
        }
        if ((cap.getDevInf().getSupportLargeObjs() != null)&&
            (cap.getDevInf().getSupportLargeObjs().booleanValue())){
            stmt.setString(9, "Y");
        } else {
            stmt.setString(9, "N");
        }
        if ((cap.getDevInf().getSupportNumberOfChanges() != null)&&
            (cap.getDevInf().getSupportNumberOfChanges().booleanValue())) {
            stmt.setString(10,"Y" );
        } else {
            stmt.setString(10, "N");
        }
    }

    /**
     * Prepares the statement for adding a datastore for a capabilities
     *
     * @param stmt        statement to be prepared
     * @param dataStore   object with the informations to be saved
     * @param dataStoreId the id this datastore will have in db
     * @param capsId      the id of the capabilities this datastore relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastore(PreparedStatement stmt       ,
                                           DataStore         dataStore  ,
                                           long              dataStoreId,
                                           long              capsId)
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        stmt.setLong(2, capsId);
        stmt.setString(3, dataStore.getSourceRef().getValue());
        stmt.setString(4, dataStore.getDisplayName());
        stmt.setLong(5, dataStore.getMaxGUIDSize());
        if ((dataStore.getDSMem() != null)&&
            (dataStore.getDSMem().isSharedMem())){
            stmt.setString(6, "Y");
        } else {
            stmt.setString(6, "N");
        }
        if(dataStore.isSupportHierarchicalSync()) {
            stmt.setString(7, "Y");
        } else {
            stmt.setString(7, "N");
        }

        String list = "";
        ArrayList syncTypes = dataStore.getSyncCap().getSyncType();
        SyncType sync = null;
        for (int i = 0; i < syncTypes.size(); i++) {
            sync = (SyncType) syncTypes.get(i);
            list = list + sync.getType();
            if (i < syncTypes.size() - 1) {
                list = list + ",";
            }

        }
        stmt.setString(8, list);
    }

    /**
     * Prepares the statement for adding a rx for a datastore
     *
     * @param stmt        statement to be prepared
     * @param cti         object with the informations to be saved
     * @param dataStoreId the id of the datastore this rx relates to
     * @param isPreffered boolean value if the rx to add is preffered or not
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreRx(PreparedStatement stmt       ,
                                             CTInfo            cti        ,
                                             long              dataStoreId,
                                             boolean           isPreffered)
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        stmt.setString(2, cti.getCTType());
        String vers= cti.getVerCT();
        if((vers==null)||(vers.equals(""))) {
            stmt.setString(3, "-");
        } else {
            stmt.setString(3, vers);
        }

        if(isPreffered) {
            stmt.setString(4, "Y");
        } else {
            stmt.setString(4, "N");
        }
    }

    /**
     * Prepares the statement for adding a tx for a datastore
     *
     * @param stmt        statement to be prepared
     * @param cti         object with the informations to be saved
     * @param dataStoreId the id of the datastore this tx relates to
     * @param isPreffered boolean value if the tx to add is preffered or not
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreTx(PreparedStatement stmt       ,
                                             CTInfo            cti        ,
                                             long              dataStoreId,
                                             boolean           isPreffered)
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        stmt.setString(2, cti.getCTType());
        String vers=cti.getVerCT();
        if((vers==null)||(vers.equals(""))) {
            stmt.setString(3, "-");
        } else {
            stmt.setString(3, vers);
        }
        if(isPreffered) {
            stmt.setString(4, "Y");
        } else {
            stmt.setString(4, "N");
        }
    }

    /**
     * Prepares the statement for adding a filter_rx for a datastore
     *
     * @param stmt        statement to be prepared
     * @param cti         object with the informations to be saved
     * @param dataStoreId the id of the datastore this filter_rx relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreFilterRx(PreparedStatement stmt,
                                                   CTInfo            cti ,
                                                   long              dataStoreId)
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        stmt.setString(2, cti.getCTType());
        String vers=cti.getVerCT();
        if((vers==null)||(vers.equals(""))) {
            stmt.setString(3, "-");
        } else {
            stmt.setString(3, vers);
        }
    }

    /**
     * Prepares the statement for adding a dsMem for a datastore
     *
     * @param stmt        statement to be prepared
     * @param dsMem       object with the informations to be saved
     * @param dataStoreId the id of the datastore this dsMem relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreDsMem(PreparedStatement stmt     ,
                                                DSMem             dsMem    ,
                                                long              dataStoreId)
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        if(dsMem.isSharedMem()) {
            stmt.setString(2, "Y");
        } else {
            stmt.setString(2, "N");
        }
        stmt.setLong(3, dsMem.getMaxMem());
        stmt.setLong(4, dsMem.getMaxID());
    }

    /**
     * Prepares the statement for adding a filter_cap for a datastore
     *
     * @param stmt        statement to be prepared
     * @param filterCap   object with the informations to be saved
     * @param dataStoreId the id of the datastore this filter_caps relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreFilterCaps(PreparedStatement stmt,
                                                     FilterCap filterCap  ,
                                                     long dataStoreId      )
    throws SQLException {
        stmt.setLong(1, dataStoreId);
        stmt.setString(2, filterCap.getCTInfo().getCTType());
        String vers=filterCap.getCTInfo().getVerCT();
        if((vers==null)||(vers.equals(""))) {
            stmt.setString(3, "-");
        } else {
            stmt.setString(3, vers);
        }

        String keywords = "";
        if (filterCap.getFilterKeywords() != null) {
            for (int i = 0; i < filterCap.getFilterKeywords().size(); i++) {
                keywords = keywords + filterCap.getFilterKeywords().get(i);
                if (i < filterCap.getFilterKeywords().size() - 1) {
                    keywords = keywords + ",";
                }
            }
        }
        stmt.setString(4, keywords);

        String properties = "";
        if (filterCap.getPropNames() != null) {
            for (int i = 0; i < filterCap.getPropNames().size(); i++) {
                properties = properties + filterCap.getPropNames().get(i);
                if (i < filterCap.getPropNames().size() - 1) {
                    properties = properties + ",";
                }
            }
        }
        stmt.setString(5, properties);
    }

    /**
     * Prepares the statement for adding a ctcap for a datastore
     *
     * @param stmt        statement to be prepared
     * @param ctCap       object with the informations to be saved
     * @param ctCapId     the id the ctcap will have in db
     * @param dataStoreId the id of the datastore this ctcap relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreCtcap(PreparedStatement stmt     ,
                                                CTCap             ctCap    ,
                                                long              ctCapId  ,
                                                long              dataStoreId)
    throws SQLException {
        stmt.setLong(1, ctCapId);
        stmt.setLong(2, dataStoreId);
        stmt.setString(3, ctCap.getCTInfo().getCTType());
        String vers= ctCap.getCTInfo().getVerCT();
        if((vers==null)||(vers.equals(""))) {
            stmt.setString(4, "-");
        } else {
            stmt.setString(4, vers);
        }
        if(ctCap.isFieldLevel()) {
            stmt.setString(5, "Y");
        } else {
            stmt.setString(5, "N");
        }


    }

    /**
     * Prepares the statement for adding a property for a ctcap
     *
     * @param stmt    statement to be prepared
     * @param prop    object with the informations to be saved
     * @param propId  the id the property will have in db
     * @param ctCapId the id of the ctcap this property relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreCtcapProperty(PreparedStatement stmt,
                                                        Property   prop   ,
                                                        long       propId ,
                                                        long       ctCapId)
    throws SQLException {
        stmt.setLong(1, propId);
        stmt.setLong(2, ctCapId);
        stmt.setString(3, prop.getPropName());
        stmt.setString(4, prop.getDisplayName());
        stmt.setString(5, prop.getDataType());
        stmt.setInt(6, prop.getMaxOccur());
        stmt.setInt(7, prop.getMaxSize());
        if(prop.isNoTruncate()) {
            stmt.setString(8, "Y");
        } else {
            stmt.setString(8, "N");
        }


        String list = "";
        if (prop.getValEnums() != null) {
            for (int i = 0; i < prop.getValEnums().size(); i++) {
                list = list + prop.getValEnums().get(i);
                if (i < prop.getValEnums().size() - 1) {
                    list = list + ",";
                }
            }
        }
        stmt.setString(9, list);
    }

    /**
     * Prepares the statement for adding a parameter for a ctcap property
     *
     * @param stmt      statement to be prepared
     * @param propParam object with the informations to be saved
     * @param propId    the id of the property this param relates to
     * @throws SQLException
     */
    private void prepareAddDeviceDatastoreCtcapPropertyParam(
            PreparedStatement stmt,
            PropParam         propParam,
            long              propId   )  throws SQLException {

        stmt.setLong(1, propId);
        stmt.setString(2, propParam.getParamName());
        stmt.setString(3, propParam.getDisplayName());
        stmt.setString(4, propParam.getDataType());
        String list = "";
        if (propParam.getValEnums() != null) {
            for (int i = 0; i < propParam.getValEnums().size(); i++) {
                list = list + propParam.getValEnums().get(i);
                if (i < propParam.getValEnums().size() - 1) {
                    list = list + ",";
                }
            }
        }
        stmt.setString(5, list);
    }

    /**
     * Prepares the statement for adding an Ext for a device
     *
     * @param stmt   the statement to be prepared
     * @param ext    the Ext object with the informations to be saved
     * @param idExt the ext identifier
     * @param idCap the id of the capabilities this Ext relates to
     *
     * @throws SQLException
     */
    private void prepareAddDeviceExt(PreparedStatement stmt,
                                     Ext ext,
                                     long idExt,
                                     long idCap)
    throws SQLException {

        stmt.setLong  (1, idExt);
        stmt.setLong  (2, idCap);
        stmt.setString(3, ext.getXNam());

        StringBuffer list = new StringBuffer("");
        if (ext.getXVal() != null) {
            int size = ext.getXVal().size();

            for (int i = 0; i < size; i++) {
                list.append((String)ext.getXVal().get(i)).append(',');
            }
        }

        String xvals = null;
        if (list.length() > 1) {
            xvals = list.substring(0, list.length() - 1);
        }
        stmt.setString(4, xvals);
    }

    /**
     * Read the Capabilities from the DataBase knowing capabilities's id.
     *
     * @param cap The object to be read from DataBase
     * @return true if capabilities found, false otherwise
     * @throws PersistentStoreException
     */
    private boolean readById(Capabilities cap) throws PersistentStoreException {
        //
        //connect to db and read capabilities byt id
        //
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = coreDataSource.getConnection();
            conn.setReadOnly(true);

            //
            //check if id exists in db
            //
            stmt = conn.prepareStatement(sql[SQL_SELECT_CAPABILITIES_BY_ID]);
            stmt.setInt(1, cap.getId().intValue());
            rs = stmt.executeQuery();
            if (rs.next() == false) {
                //
                //id not found in db
                //
                DBTools.close(null, stmt, null);
                return false;
            } else {
                //
                //id found in db
                //set to object's DevInf informations
                //note: if the DTD version is null or empty, the value of VerDTD
                //      object is set to 1.1 by default
                //
                String verDTD = rs.getString(2);
                if (verDTD == null || "".equals(verDTD)) {
                    verDTD = "1.1";
                }
                cap.setDevInf(new DevInf());
                cap.getDevInf().setVerDTD(new VerDTD(verDTD));
                cap.getDevInf().setMan(rs.getString(3));
                cap.getDevInf().setMod(rs.getString(4));
                cap.getDevInf().setFwV(rs.getString(5));
                cap.getDevInf().setSwV(rs.getString(6));
                cap.getDevInf().setHwV(rs.getString(7));
                String utc = rs.getString(8);
                if("Y".equals(utc)) {
                    cap.getDevInf().setUTC(true);
                } else {
                    cap.getDevInf().setUTC(false);
                }
                String lo = rs.getString(9);
                if("Y".equals(lo)) {
                    cap.getDevInf().setSupportLargeObjs(true);
                } else {
                    cap.getDevInf().setSupportLargeObjs(false);
                }
                String noc = rs.getString(10);
                if("Y".equals(noc)) {
                    cap.getDevInf().setSupportNumberOfChanges(true);
                } else {
                    cap.getDevInf().setSupportNumberOfChanges(false);
                }
                //
                //close initial statemnt and result set
                //
                DBTools.close(null, stmt, null);
                //
                //set object's datastores
                //
                ArrayList dataStores = new ArrayList();
                ResultSet rsDS = null;
                stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_BY_CAPID]);
                stmt.setInt(1, cap.getId().intValue());
                rsDS = stmt.executeQuery();
                String shs = null;
                String dsMem = null;
                while (rsDS.next()) {
                    //
                    //add each data store to capabilities object
                    //
                    DataStore ds = new DataStore();
                    int dataStoreID = rsDS.getInt(1);
                    ds.setSourceRef(new SourceRef(rsDS.getString(3)));
                    ds.setDisplayName(rsDS.getString(4));
                    ds.setMaxGUIDSize(rsDS.getLong(5));
                    dsMem = rsDS.getString(6);
                    if("Y".equals(dsMem)) {
                        ds.setDSMem(new DSMem(true));
                    } else {
                        ds.setDSMem(new DSMem(false));
                    }
                    shs = rsDS.getString(7);
                    if("Y".equals(shs)) {
                        ds.setSupportHierarchicalSync(new Boolean(true));
                    } else {
                        ds.setSupportHierarchicalSync(new Boolean(false));
                    }

                    String syncCaps = rsDS.getString(8);
                    String[] syncCapList = syncCaps.split(",");
                    SyncType[] tempList = new SyncType[syncCapList.length];

                    for (int i = 0; i < syncCapList.length; i++) {
                        SyncType st = new SyncType(Integer.parseInt(syncCapList[i]));
                        tempList[i] = st;
                    }
                    ds.setSyncCap(new SyncCap(tempList));
                    //
                    //for each datastore get cttype_rx
                    //
                    ArrayList rxs = new ArrayList();
                    ResultSet rsDsRx = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_RX]);
                    stmt.setInt(1, dataStoreID);

                    rsDsRx = stmt.executeQuery();
                    String rxPref = null;
                    String rxVersion=null;
                    while (rsDsRx.next()) {
                        CTInfo cti = new CTInfo();
                        cti.setCTType(rsDsRx.getString(2));
                        rxVersion=rsDsRx.getString(3);
                        if(rxVersion!=null) {
                            cti.setVerCT(rxVersion);
                        }

                        rxPref = rsDsRx.getString(4);
                        if ("Y".equals(rxPref)) {
                            ds.setRxPref(cti);
                            continue;
                        }
                        rxs.add(cti);
                    }
                    DBTools.close(null, stmt, rsDsRx);
                    stmt = null;
                    ds.setRxs((CTInfo[])rxs.toArray(new CTInfo[0]));
                    //
                    //for each datastore get cttype_tx
                    //
                    ArrayList txs = new ArrayList();
                    ResultSet rsDsTx = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_TX]);
                    stmt.setInt(1, dataStoreID);

                    rsDsTx = stmt.executeQuery();
                    String txPref = null;
                    String txVersion = null;
                    while (rsDsTx.next()) {
                        CTInfo cti = new CTInfo();
                        cti.setCTType(rsDsTx.getString(2));
                        txVersion=rsDsTx.getString(3);
                        if(txVersion!=null) {
                            cti.setVerCT(txVersion);
                        }

                        txPref = rsDsTx.getString(4);
                        if ("Y".equals(txPref)) {
                            ds.setTxPref(cti);
                            continue;
                        }
                        txs.add(cti);
                    }
                    DBTools.close(null, stmt, rsDsTx);

                    stmt = null;
                    ds.setTxs((CTInfo[])txs.toArray(new CTInfo[0]));
                    //
                    //for each datastore get filter_rx
                    //
                    ArrayList filterRxs = new ArrayList();
                    ResultSet rsDsFilterRx = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_FILTER_RX]);
                    stmt.setInt(1, dataStoreID);

                    rsDsFilterRx = stmt.executeQuery();
                    String filterRxVersion=null;
                    while (rsDsFilterRx.next()) {
                        CTInfo cti = new CTInfo();
                        cti.setCTType(rsDsFilterRx.getString(2));
                        filterRxVersion=rsDsFilterRx.getString(3);
                        if(filterRxVersion!=null) {
                            cti.setVerCT(filterRxVersion);
                        }
                        filterRxs.add(cti);
                    }
                    DBTools.close(null, stmt, rsDsFilterRx);

                    stmt = null;
                    ds.setFilterRxs((CTInfo[])filterRxs.toArray(new CTInfo[0]));
                    //
                    //for each datastore get filter_cap
                    //
                    ArrayList filterCaps = new ArrayList();
                    ResultSet rsDsFilterCap = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_FILTER_CAP]);
                    stmt.setInt(1, dataStoreID);

                    rsDsFilterCap = stmt.executeQuery();
                    String filterCapVers=null;
                    while (rsDsFilterCap.next()) {
                        FilterCap filterCap = new FilterCap();
                        CTInfo cti = new CTInfo();
                        cti.setCTType(rsDsFilterCap.getString(2));
                        filterCapVers=rsDsFilterCap.getString(3);
                        if(filterCapVers!=null) {
                            cti.setVerCT(filterCapVers);
                        }
                        filterCap.setCTInfo(cti);
                        String keywords = rsDsFilterCap.getString(4);
                        if (keywords != null) {
                            filterCap.setFilterKeywords(keywords.split(","));
                        } else {
                            filterCap.setFilterKeywords(new String[]{keywords});
                        }
                        String properties = rsDsFilterCap.getString(5);
                        if (properties != null) {
                            filterCap.setPropNames(properties.split(","));
                        } else {
                            filterCap.setPropNames(new String[]{properties});
                        }
                        filterCaps.add(filterCap);
                    }
                    DBTools.close(null, stmt, rsDsFilterCap);
                    stmt = null;
                    ds.setFilterCaps((FilterCap[])filterCaps.toArray(new FilterCap[0]));
                    //
                    //for each datastore ds_mem
                    //
                    ResultSet rsDsMem = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_DSMEM]);
                    stmt.setInt(1, dataStoreID);

                    rsDsMem = stmt.executeQuery();
                    if (rsDsMem.next()) {
                        DSMem dsMemObj = null;
                        String sharedMem = rsDsMem.getString(2);
                        if("Y".equals(sharedMem)) {
                            dsMemObj = new DSMem(true, rsDsMem.getLong(3), rsDsMem.getLong(4));
                        } else {
                            dsMemObj = new DSMem(false, rsDsMem.getLong(3), rsDsMem.getLong(4));
                        }
                        ds.setDSMem(dsMemObj);
                    }
                    DBTools.close(null, stmt, rsDsMem);

                    stmt = null;
                    //
                    //for each datastore add ctcaps
                    //
                    ResultSet rsDsCtCaps = null;
                    stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_CTCAPS]);
                    stmt.setInt(1, dataStoreID);

                    rsDsCtCaps = stmt.executeQuery();
                    ArrayList ctCaps = new ArrayList();
                    String fieldLevel = null;
                    String ctCapVers = null;
                    while (rsDsCtCaps.next()) {
                        int ctCapId = rsDsCtCaps.getInt(1);
                        CTCap ctCap = new CTCap();
                        CTInfo cti = new CTInfo();
                        cti.setCTType(rsDsCtCaps.getString(3));
                        ctCapVers= rsDsCtCaps.getString(4);
                        if(ctCapVers!=null) {
                            cti.setVerCT(ctCapVers);
                        }
                        ctCap.setCTInfo(cti);
                        fieldLevel = rsDsCtCaps.getString(5);
                        if("Y".equals(fieldLevel)) {
                            ctCap.setFieldLevel(new Boolean(true));
                        } else {
                            ctCap.setFieldLevel(new Boolean(false));
                        }
                        //
                        //for each ctCap get ctCap_properties
                        //
                        ResultSet rsCtCapProp = null;
                        stmt = conn.prepareStatement(sql[SQL_SELECT_DATASTORES_CTCAPS_PROP]);
                        stmt.setInt(1, ctCapId);

                        rsCtCapProp = stmt.executeQuery();
                        ArrayList ctCapProps = new ArrayList();
                        String noTruncate = null;
                        while (rsCtCapProp.next()) {
                            int propId = rsCtCapProp.getInt(1);
                            Property prop = new Property();
                            prop.setPropName(rsCtCapProp.getString(3));
                            prop.setDisplayName(rsCtCapProp.getString(4));
                            prop.setDataType(rsCtCapProp.getString(5));
                            prop.setMaxOccur(rsCtCapProp.getInt(6));
                            prop.setMaxSize(rsCtCapProp.getInt(7));
                            noTruncate = rsCtCapProp.getString(8);
                            if("Y".equals(noTruncate)) {
                                prop.setNoTruncate(new Boolean(false));
                            } else {
                                prop.setNoTruncate(new Boolean(false));
                            }
                            String valEnum = rsCtCapProp.getString(9);
                            if(valEnum != null) {
                                prop.setValEnums(valEnum.split(","));
                            }
                            //
                            //for each properties get properties parameters
                            //
                            ResultSet rsPropParam = null;
                            PreparedStatement stmtCtCapsPropParams =
                                conn.prepareStatement(sql[SQL_SELECT_DATASTORES_CTCAPS_PROP_PARAMS]);

                            stmtCtCapsPropParams.setInt(1, propId);

                            rsPropParam = stmtCtCapsPropParams.executeQuery();
                            ArrayList propParams = new ArrayList();
                            while (rsPropParam.next()) {
                                String name = rsPropParam.getString(2);
                                String label = rsPropParam.getString(3);
                                String type = rsPropParam.getString(4);
                                String valEnumProp =
                                        (rsPropParam.getString(5) == null ? "" : rsPropParam.getString(5));

                                propParams.add(new PropParam(name, type, valEnumProp.split(","), label));
                            }
                            DBTools.close(null, stmtCtCapsPropParams, rsPropParam);
                            prop.setPropParams((PropParam[])propParams.toArray(new PropParam[0]));

                            ctCapProps.add(prop);
                        }
                        DBTools.close(null, stmt, rsCtCapProp);

                        Property[] properties = (Property[])ctCapProps.toArray(new Property[0]);
                        if (properties.length > 0) {
                            ctCap.setProperties(properties);
                        }

                        ctCaps.add(ctCap);
                    }
                    DBTools.close(null, stmt, rsDsCtCaps);

                    ds.setCTCaps((CTCap[])ctCaps.toArray(new CTCap[0]));
                    //
                    //add data store to datastores list
                    //
                    dataStores.add(ds);
                }
                DBTools.close(null, stmt, rsDS);

                cap.getDevInf().setDataStores((DataStore[])dataStores.toArray(new DataStore[0]));

                //
                // Read Ext
                //
                ArrayList exts = new ArrayList();
                stmt = conn.prepareStatement(sql[SQL_SELECT_EXT_BY_CAPID]);
                stmt.setInt(1, cap.getId().intValue());
                ResultSet rsExt = stmt.executeQuery();
                Ext ext = null;
                while (rsExt.next()) {
                    //
                    // Add each Ext to DevInf
                    //
                    ext = new Ext();

                    ext.setXNam(rsExt.getString(3));
                    String xvals = rsExt.getString(4);
                    if (xvals != null) {
                        ext.setXVal(xvals.split(","));
                    }

                    exts.add(ext);
                }//while rs
                DBTools.close(null, stmt, rsExt);

                cap.getDevInf().setExts(exts);
                return true;
            }
        } catch (SQLException e) {
            throw new PersistentStoreException("Error storing capabilities " + cap, e);
        } catch (Exception e) {
            throw new PersistentStoreException("Error storing capabilities " + cap, e);
        } finally {
            DBTools.close(conn, stmt, rs);
        }
    }

    /**
     * Read the Capabilities from the DataBase knowing capabilities's model,
     * manufactory, firmware version and software version.
     *
     * @param cap The object to be read from DataBase
     *
     * @return true if capabilities found, false otherwise
     *
     * @throws PersistentStoreException
     */
    private boolean readByDevInf(Capabilities cap)
    throws PersistentStoreException {
        if ((cap.getDevInf().getMod() == null)
         || (cap.getDevInf().getMan() == null)
         || (cap.getDevInf().getFwV() == null)
         || (cap.getDevInf().getSwV() == null)) {
            if (log.isTraceEnabled()) {
                log.trace("Needed DevInf informations not found. " +
                          "Capabilities object was not read from DB");
            }
            return false;
        } else {
            //
            //search the id of the cap identified by
            //cap.getDevInf().getMod(),cap.getDevInf().getMan(),
            //cap.getDevInf().getFwV(),cap.getDevInf().getSwV()
            //
            if (log.isTraceEnabled()) {
                    log.trace("Read capabilities id for DevInf data: " +
                               " Mod="  + cap.getDevInf().getMod() +
                               ", Man=" + cap.getDevInf().getMan() +
                               ", FwV=" + cap.getDevInf().getFwV() +
                               ", SwV=" + cap.getDevInf().getSwV());
            }
            Connection        conn  = null;
            PreparedStatement stmt  = null;
            ResultSet         rs    = null;
            Long              capId = null;
            try {
                conn = coreDataSource.getConnection();
                conn.setReadOnly(true);

                stmt = conn.prepareStatement(sql[SQL_SELECT_CAPS_BY_DEVINF]);

                stmt.setString(1, cap.getDevInf().getMan());
                stmt.setString(2, cap.getDevInf().getMod());
                stmt.setString(3, cap.getDevInf().getFwV());
                stmt.setString(4, cap.getDevInf().getSwV());

                rs = stmt.executeQuery();

                if (rs.next()) {
                    //
                    //id found
                    //
                    capId = new Long(rs.getLong(1));
                    if (log.isTraceEnabled()) {
                        log.trace("Capabilities id found in DB is "+capId.intValue());
                    }

                } else {
                    //
                    //id not found
                    //
                    if (log.isTraceEnabled()) {
                        log.trace("Capabilities id not found in DB for given DevInf data");
                    }
                    return false;
                }
            } catch (SQLException e) {
                throw new PersistentStoreException("Error reading capabilities " + cap, e);
            } catch (Exception e) {
                throw new PersistentStoreException("Error reading capabilities " + cap, e);
            } finally {
                DBTools.close(conn, stmt, rs);
            }
            if (capId != null) {
                //
                //read capabilities by ID
                //
                cap.setId(capId);
                return readById(cap);
            }
        }
        return false;
    }

    /**
     * Returns the next id to use storing a datastore
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     * @return the next id to use storing a datastore
     */
    private long getNextDataStoreId() throws DBIDGeneratorException {
        return dataStoreIDGenerator.next();
    }

    /**
     * Returns the next id to use storing capabiltiies
     * @throws DBIDGeneratorException if an error occurs
     * @return the next id to use storing capabiltiies
     */
    private long getNextCapabilityId() throws DBIDGeneratorException {
        return capabilityIDGenerator.next();
    }

    /**
     * Returns the next id to use storing Exts
     * @throws DBIDGeneratorException if an error occurs
     * @return the next id to use storing Exts
     */
    private long getNextExtId() throws DBIDGeneratorException {
        return extIDGenerator.next();
    }

    /**
     * Returns the next id to use storing CTCap
     * @throws DBIDGeneratorException if an error occurs
     * @return the next id to use storing CTCap
     */
    private long getNextCTCapId() throws DBIDGeneratorException {
        return ctCapIDGenerator.next();
    }

    /**
     * Returns the next id to use storing CTCap properties
     * @throws DBIDGeneratorException if an error occurs
     * @return the next id to use storing CTCap properties
     */
    private long getNextCTCapPropertyId() throws DBIDGeneratorException {
        return ctCapPropertyIDGenerator.next();
    }


}
