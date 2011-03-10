/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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

package com.funambol.pushlistener.service.registry.dao;

import java.sql.*;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import com.funambol.pushlistener.service.registry.RegistryEntry;
import com.funambol.pushlistener.service.registry.RegistryEntryStatus;

/**
 * DAO to handle the registry entries.
 * A DAO instance can be instantiated with two variables that are used to segment the
 * entries in the database in a cluster environment in order to distribute the load
 * between the instances.
 * <p>These variables are:
 * <ui>
 * <li>clusterSize: number of the members in the cluster</li>
 * <li>serverIndex: unique index of the instance in the cluster</li>
 * </ui>
 * <br>A DAO works with the entries with <code>id % clusterSize == serverIndex</code>.
 * <br>In this way, an entry is handle just by an instance and all instances are
 * handled.
 * When you create a RegistryDao instance you can specify the name of the table to
 * use to store/load registry entries. The table is supposed to have a set of
 * basic columns that are required, they are:
 * - id
 * - period
 * - status
 * - active
 * - last_update
 * - task_bean_file
 * Besides, it's possible to add some custom columns in the push registry table.
 * Those columns will be loaded as properties in the RegistryEntry objects and
 * will be stored/updated while saving a RegistryEntry.
 * Since the name of the table is customizable and this dao is able to handle
 * dinamically the custom columns, a discovery phase is needed in order to build
 * the query used to load/store/update data.
 * In order to reduce the overhead of the discovery phase, when a RegistryDao is
 * created for a given table name, it checks whether the table name has already
 * been processed accessing a static query dictionary map.
 * This dictionary contains QueryDescriptor objects that hold queries for a given
 * table name and all information the RegistryDao needs to handle extra columns.
 * The discovery phase is arranged in order that one thread at time can process
 * a new table name, building the new QueryDescriptor object.
 *
 * @version $Id: RegistryDao.java,v 1.24 2008-05-18 16:23:42 nichele Exp $
 */
public class RegistryDao {

    // --------------------------------------------------------------- Constants
    /**
     * This map contains data about the queries used by this dao for each of the
     * table used as registry table.
     * The first time a RegistryDao is built for a push registry table, all the 
     * queries used by this dao are build and stored inside this dictionary.
     * The key used to store/retrieve data inside this dictionary is the name of
     * the table while the value is the QueryDescriptor object.
     * This map cannot be defined as static since we must have different
     * tables definition (different RegistryDAO creates with a different table 
     * name) and if the cluster information changes, it has to be reinitialized.
     */
    private final Map<String,QueryDescriptor> queryDictionary =
        new LinkedHashMap<String, QueryDescriptor>();

    private static final Set<String> basicColumns = new HashSet<String>();
    /**
     * It's the lock object used to handle properly concurrent access while
     * building the QueryDescriptor object for a given table name.
     */
    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * Setting the set of column that must be considered as basic while
     * building the QueryDescriptor object.
     */
    static {
        basicColumns.add("id");
        basicColumns.add("period");
        basicColumns.add("active");
        basicColumns.add("last_update");
        basicColumns.add("status");
        basicColumns.add("task_bean_file");
    }

    public static final String DEFAULT_DATASOURCE_JNDI_NAME = "jdbc/fnblcore";

    public static final String DEFAULT_ID_SPACE = "pushlistener.id";

    private static final String DEFAULT_REGISTRY_TABLE_NAME =
        "push_listener_registry";

    private static final String SQL_READ_ENTRIES = "select id, period, active, "
            + "task_bean_file, last_update, status {0} "
            + "from {1} where ";

    private static final String SQL_READ_ENTRY = "select id, period, active, "
            + "task_bean_file, last_update, status {0} "
            + "from {1} where id = ?";

    private static final String SQL_SEGMENTATION_CLAUSE = " (id % {0} = {1}) ";

    private static final String SQL_WHERECLAUSE_STATUS = " last_update >= ? "
            + "and status = ?";

    private static final String SQL_WHERECLAUSE_ACTIVE_ENTRIES =
        " status != 'D' and active = ?";

    private static final String SQL_DELETE_ENTRY =
        "delete from {0} where id = ? ";

    private static final String SQL_DELETE_ENTRIES =
        "delete from {0} where status = ''D'' ";

    private static final String SQL_UPDATE_ENTRY =
        "update {0} set period = ?, active = ?, task_bean_file = ?, " +
        "last_update = ?, status = ? {1} where id = ? ";

    private static final String SQL_UPDATE_STATUS =
        "update {0} set last_update = ?, status = ? where id = ? ";

    private static final String SQL_INSERT_ENTRY =
        "insert into {0} (id, period, active, task_bean_file, last_update, " +
        "status{1}) values (?,?,?,?,?,?{2}) ";

    private static final String SQL_DISCOVER_PROPERTIES =
        "select * from {0} where id=1";

  
    // -------------------------------------------------------------Private data
    private static final Logger log =
        Logger.getLogger("funambol.pushlistener.registrymonitor.dao");

    private DBIDGenerator dbIdGenerator = null;

    private DataSource dataSource = null;

    private String idSpace = DEFAULT_ID_SPACE;
    private StringBuilder columnSuffix;
    private StringBuilder placeholderSuffix;
    private StringBuilder updateSuffix;
    private Map<String, String> queries;
    private QueryDescriptor queryDesc;

    // ------------------------------------------------------------- Constructor

    /**
     * Creates a RegistryDao that works on the given table
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     */
    public RegistryDao(String registryTableName, String entriesIdSpace) {

        DataSource ds = null;
        try {
            ds = DataSourceTools.lookupDataSource(DEFAULT_DATASOURCE_JNDI_NAME);
        } catch (NamingException ex) {
            //
            // For backward compatibility, the error is just logged since the exception
            // can not be thrown
            //
            log.error("Error looking up '" +
                      DEFAULT_DATASOURCE_JNDI_NAME + "'", ex);
        }
        try {
            init(registryTableName, entriesIdSpace, ds, -1, -1);
        } catch (DataAccessException ex) {
              log.error("Error discovering extra properties.", ex);
        }
    }

    /**
     * Creates a RegistryDao that works on the given table
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     * @param dataSourceJNDIName the jndi name of the datasource to use
     * @throws DataAccessException if an error occurs looking up the datasource
     */
    public RegistryDao(String registryTableName, String entriesIdSpace, String dataSourceJNDIName)
    throws DataAccessException {

        DataSource ds = null;
        try {
            ds = DataSourceTools.lookupDataSource(dataSourceJNDIName);
        } catch (NamingException ex) {
            throw new DataAccessException("Error looking up '" +
                                          dataSourceJNDIName + "'", ex);
        }

        init(registryTableName, entriesIdSpace, ds, -1, -1);
    }

    /**
     * Creates a RegistryDao that works on the given table
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     * @param datasource the database reference
     */
    public RegistryDao(String registryTableName, String entriesIdSpace, DataSource datasource) {

        this(registryTableName, entriesIdSpace, datasource, -1, -1);

    }

    /**
     * Creates a RegistryDao that works on the given table. See also
     * class description for some information about clusterSize and serverIndex
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     * @param clusterSize number of the members in the cluster
     * @param serverIndex unique index of the instance in the cluster
     */
    public RegistryDao(String     registryTableName,
                       String     entriesIdSpace,
                       int        clusterSize,
                       int        serverIndex) {

        DataSource ds = null;
        try {
            ds = DataSourceTools.lookupDataSource(DEFAULT_DATASOURCE_JNDI_NAME);
        } catch (NamingException ex) {
            //
            // For backward compatibility, the error is just logged but the exception
            // can not be thrown
            //
            log.error("Error looking up '" +
                      DEFAULT_DATASOURCE_JNDI_NAME + "'", ex);
        }
        try {
            init(registryTableName, entriesIdSpace, ds, clusterSize, serverIndex);
        } catch (DataAccessException ex) {
              log.error("Error discovering extra properties.", ex);
        }

        }

    /**
     * Creates a RegistryDao that works on the given table. See also
     * class description for some information about clusterSize and serverIndex
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     * @param dataSource the database refernce
     * @param clusterSize number of the members in the cluster
     * @param serverIndex unique index of the instance in the cluster
     */
    public RegistryDao(String     registryTableName,
                       String     entriesIdSpace,
                       DataSource dataSource,
                       int        clusterSize,
                       int        serverIndex) {
                        
        try {
         init(registryTableName, entriesIdSpace, dataSource, clusterSize, serverIndex);
        } catch (DataAccessException ex) {
              log.error("Error discovering extra properties.", ex);
        }

    }



    // ---------------------------------------------------------- Public Methods

    /**
     * Returns the registry entry with the given id
     * @return the read entry
     * @param id the id of the pim registry entry
     * @throws DataAccessException if an error occurs
     */
    public RegistryEntry getEntryById(long id)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        RegistryEntry entry = null;

        try {

            con = getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(queryDesc.getReadEntryQuery());
            ps.setLong(1, id);
            rs = ps.executeQuery();

            while (rs.next()) {
                entry = resultSetToRegistryEntry(rs);
            }

        } catch (Exception e) {
            throw new DataAccessException("Error reading entry with id: " + id, e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return entry;
    }

    /**
     * Reads registry entries assigned to this server.
     * If <code>lastUpdate</code> is different than 0, just the changed entries
     * with last_update bigger than the given one and the given status are returned.
     * @param lastUpdate the lastUpdate. 0 if all entries must be returned
     * @param status the wanted status
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return the read entries
     */
    public List<RegistryEntry> getEntries(long lastUpdate,
                                          String status)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<RegistryEntry> entries = new ArrayList<RegistryEntry>();

        try {

            con = getConnection();
            con.setReadOnly(true);

            if (lastUpdate != 0) {
                //
                // Read only changed registry entries
                //
                if (log.isTraceEnabled()) {
                    log.trace("Executing '" + queryDesc.getReadChangedEntriesQuery() + "'");
                }

                ps = con.prepareStatement(queryDesc.getReadChangedEntriesQuery());
                ps.setLong  (1, lastUpdate);
                ps.setString(2, status);

            } else {
                //
                // Read all registry entries
                //
                if (log.isTraceEnabled()) {
                    log.trace("Executing '" + queryDesc.getReadActiveEntriesQuery() + "'");
                }

                ps = con.prepareStatement(queryDesc.getReadActiveEntriesQuery());
                ps.setString (1, "Y");
            }

            rs = ps.executeQuery();

            while (rs.next()) {
                entries.add(resultSetToRegistryEntry(rs));
            }

        } catch (Exception e) {

            throw new DataAccessException(e);

        } finally {
            DBTools.close(con, ps, rs);
        }

        return entries;
    }

    /**
     * Reads active registry entries assigned to this server.
     * @return the list of the active registry entries associated to this server
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public List<RegistryEntry> getActiveEntries()
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;

        List<RegistryEntry> entries = new ArrayList<RegistryEntry>();

        try {
            if (log.isTraceEnabled()) {
                log.trace("Executing '" + queryDesc.getReadActiveEntriesQuery() + "'");
            }

            con = getConnection();
            con.setReadOnly(true);

            ps = con.prepareStatement(queryDesc.getReadActiveEntriesQuery());

            ps.setString (1, "Y");
            rs = ps.executeQuery();

            while (rs.next()) {
                entries.add(resultSetToRegistryEntry(rs));
            }

        } catch (Exception e) {

            throw new DataAccessException(e);

        } finally {
            DBTools.close(con, ps, rs);
        }

        return entries;
    }



    /**
     * This method allows to establish how many extra property columns are specified
     * in the given ResultSet.
     * Besides, it builds the two strings used to complete the query.
     * 
     * @param rs it's the ResultSet to analyze.
     * @param basicColumnsSet it's the set of labels that must be considered as basic.
     * 
     * @throws java.sql.SQLException if any error occurs analyzing the ResultSet object.
     */
    protected void updateExtraPropertyInfo(ResultSet rs)
    throws SQLException {
        short numberOfExtraColumns = 0;
        String[] extraProperties = new String[0];
        columnSuffix      = new StringBuilder();
        updateSuffix      = new StringBuilder();
        placeholderSuffix = new StringBuilder();
        if(rs!=null) {
            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            numberOfExtraColumns = (short) (numberOfColumns - basicColumns.size());
            extraProperties = new String[numberOfExtraColumns];
            int j=0;
            if(numberOfExtraColumns>0) {
                for(int i=1;i<=numberOfColumns;i++) {
                    String columnName  = metaData.getColumnName(i);
                    if(columnName!=null) {
                        String columnKey = columnName.toLowerCase();
                        if(!basicColumns.contains(columnKey))  {
                              extraProperties[j] = columnKey;
                              columnSuffix.append(", ");
                              columnSuffix.append(columnName);
                              // create the suffix used to insert the extra properties
                              placeholderSuffix.append(", ");
                              placeholderSuffix.append("? ");
                              // create the suffix used to update the extra properties
                              updateSuffix.append(", ");
                              updateSuffix.append(columnName);
                              updateSuffix.append(" =? ");
                              j++;
                        }
                    }
                }
            }
        }
        queryDesc.setNumberOfExtraColumns(numberOfExtraColumns);
        queryDesc.setExtraProperties(extraProperties);
    }

    /**
     * Deletes the entry with the given id
     * @param id the id of the entry to delete
     * @return true if the entry is deleted, false otherwise
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public boolean deleteRegistryEntry(long id) throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsDeleted = 0;

        try {

            con = getConnection();

            ps = con.prepareStatement(queryDesc.getDeleteEntryQuery());
            ps.setLong(1, id);
            rowsDeleted = ps.executeUpdate();

            return (rowsDeleted > 0);

        } catch (Exception e) {

            throw new DataAccessException(e);

        } finally {
            DBTools.close(con, ps, null);
        }
    }

    /**
     * Removes the entry with status 'D' associated to this server
     * @return the number of the removed entries
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     */
    public int removeAllDeletedRegistryEntry() throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsDeleted = 0;

        try {

            con = getConnection();

            ps = con.prepareStatement(queryDesc.getDeleteEntriesQuery());
            rowsDeleted = ps.executeUpdate();
            return rowsDeleted;

        } catch (Exception e) {

            throw new DataAccessException(e);

        } finally {
            DBTools.close(con, ps, null);
        }

    }

    /**
     * Marks as deleted the entry with the given id
     * @param id the entry id
     * @throws DataAccessException if an error occurs
     * @return the number of updated entries
     */
    public int markAsDeleted(long id)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsUpdated = 0;

        try {

            con = getConnection();

            ps = con.prepareStatement(queryDesc.getUpdateStatusQuery());

            ps.setLong   (1, System.currentTimeMillis());
            ps.setString (2, RegistryEntryStatus.DELETED);
            ps.setLong   (3, id);

            rowsUpdated = ps.executeUpdate();

        } catch (Exception ex) {

            throw new DataAccessException("Error marking entry with id '" +
                                          id + "' as deleted", ex);

        } finally {
            DBTools.close(con, ps, null);
        }
        return rowsUpdated;
    }

    /**
     * Updates the given entry. If no error occurs, the number of updated rows
     * is returned.
     *
     * @param entry the entry to updated
     * @return the number of updated rows
     * @throws DataAccessException if an error occurs
     */
    public int updateRegistryEntry(RegistryEntry entry)
    throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;
        int               rowsUpdated = 0;

        try {

            con = getConnection();

            ps = con.prepareStatement(queryDesc.getUpdateEntryQuery());

            ps.setLong   (1, entry.getPeriod());
            ps.setString (2, entry.getActive() ? "Y" : "N" );
            ps.setString (3, entry.getTaskBeanFile());
            ps.setLong   (4, entry.getLastUpdate());
            ps.setString (5, RegistryEntryStatus.UPDATED);

            short numberOfExtraProperties = queryDesc.getNumberOfExtraColumns();
            String[] extraProperties = queryDesc.getExtraProperties();
            if(numberOfExtraProperties>0) {
                short j=0;
                for(;j<numberOfExtraProperties;j++) {
                    Object value = null;
                    if(entry.hasProperty(extraProperties[j])) {
                        value = entry.getProperty(extraProperties[j]);
                    }
                    ps.setObject(6+j, value);
                }
                ps.setLong(6+j,entry.getId());
            } else {
                ps.setLong(6,entry.getId());
            }
            rowsUpdated = ps.executeUpdate();

        } catch (Exception e) {

            throw new DataAccessException("Error updating registry entry '"
                                          + entry.getId() + "'", e);

        } finally {
            DBTools.close(con, ps, null);
        }
        return rowsUpdated;
    }

    /**
     * Inserts the given entry. If no error occurs, the entry id is returned and
     * set as id in the given entry object.
     *
     * @param entry the entry to updated
     * @return the entry id
     * @throws DataAccessException if an error occurs
     */
    public long insertRegistryEntry(RegistryEntry entry) throws DataAccessException {

        Connection        con = null;
        PreparedStatement ps  = null;

        long id = getNextEntryId();

        try {

            con = getConnection();

            ps = con.prepareStatement(queryDesc.getInsertEntryQuery());

            ps.setLong   (1, id);
            ps.setLong   (2, entry.getPeriod());
            ps.setString (3, entry.getActive() ? "Y" : "N" );
            ps.setString (4, entry.getTaskBeanFile());
            ps.setLong   (5, entry.getLastUpdate());
            ps.setString (6, RegistryEntryStatus.NEW);

            short numberOfExtraProperties = queryDesc.getNumberOfExtraColumns();
            String[] extraProperties = queryDesc.getExtraProperties();
            if(numberOfExtraProperties>0) {
                for(short j=0;j<numberOfExtraProperties;j++) {
                    Object value = null;
                    if(entry.hasProperty(extraProperties[j])) {
                        value = entry.getProperty(extraProperties[j]);
                    }
                    ps.setObject(7+j, value);
                }
            }

            ps.executeUpdate();

            entry.setId(id);

        } catch (Exception e) {

            throw new DataAccessException("Error inserting a registry entry", e);

        } finally {
            DBTools.close(con, ps, null);
        }

        return id;
    }

    /**
     * This method allows to create all queries used by the push listener framework
     *
     * @param registryTableName the name of the table storing information about
     * the push listener entries
     * @param clusterSize number of the members in the cluster
     * @param serverIndex unique index of the instance in the cluster
     */
    private void createQueries(String registryTableName,
                               int clusterSize,
                               int serverIndex) {
        String segmentationClause = null;

        String queryReadEntries = MessageFormat.format(SQL_READ_ENTRIES, new Object[]{columnSuffix, registryTableName});

        StringBuffer queryReadChangedEntries = new StringBuffer(queryReadEntries);
        StringBuffer queryReadActiveEntries = new StringBuffer(queryReadEntries);
        StringBuffer queryDeleteEntries = new StringBuffer(MessageFormat.format(SQL_DELETE_ENTRIES, new Object[]{registryTableName}));
        if (clusterSize > 1) {
            segmentationClause = MessageFormat.format(SQL_SEGMENTATION_CLAUSE, new Object[]{String.valueOf(clusterSize), String.valueOf(serverIndex)});
            queryReadChangedEntries.append(segmentationClause).append(" and ");
            queryReadActiveEntries.append(segmentationClause).append(" and ");
            queryDeleteEntries.append(" and ").append(segmentationClause);
        }
        queryReadChangedEntries.append(SQL_WHERECLAUSE_STATUS);
        queryDesc.setReadChangedEntriesQuery(queryReadChangedEntries.toString());
        queryReadActiveEntries.append(SQL_WHERECLAUSE_ACTIVE_ENTRIES);
        queryDesc.setReadActiveEntriesQuery(queryReadActiveEntries.toString());
        queryDesc.setReadEntryQuery(MessageFormat.format(SQL_READ_ENTRY, new Object[]{columnSuffix, registryTableName}));
        queryDesc.setDeleteEntryQuery(MessageFormat.format(SQL_DELETE_ENTRY, new Object[]{registryTableName}));
        queryDesc.setDeleteEntriesQuery(queryDeleteEntries.toString());
        queryDesc.setUpdateEntryQuery(MessageFormat.format(SQL_UPDATE_ENTRY, new Object[]{registryTableName, updateSuffix}));
        queryDesc.setUpdateStatusQuery(MessageFormat.format(SQL_UPDATE_STATUS, new Object[]{registryTableName}));
        queryDesc.setInsertEntryQuery(MessageFormat.format(SQL_INSERT_ENTRY, new Object[]{registryTableName, columnSuffix, placeholderSuffix}));
    }

    /**
     * This method is used to discover the extra properties defined in
     * the push registry table.
     * @param registryTableName 
     * @throws DataAccessException
     */
    private void discoverExtraProperties(String registryTableName)
    throws DataAccessException {
        Connection connection = null;
        Statement statement   = null;
        ResultSet rs          = null;
        try {
            connection = this.getConnection();
            statement  = connection.createStatement();
            String queryDiscoverExtraProperties =
                MessageFormat.format(SQL_DISCOVER_PROPERTIES,
                                     new Object[]{registryTableName});
            rs = statement.executeQuery(queryDiscoverExtraProperties);
            updateExtraPropertyInfo(rs);
        } catch (SQLException ex) {
            throw new DataAccessException(ex);
        } finally {
            DBTools.close(connection, statement, rs);
        }
    }

    /**
     * Fills the QueryDescriptor object that contains all queries used by the
     * push listener framework for the given registryTableName.
     *
     * @param registryTableName the name of the table storing information about
     * the push listener entries
     * @param clusterSize number of the members in the cluster
     * @param serverIndex unique index of the instance in the cluster
     * 
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException
     */
    private void fillQueryDescriptor(String registryTableName,
                                     int clusterSize,
                                     int serverIndex)
    throws DataAccessException {
        try {
            boolean myLock = lock.tryLock(5, TimeUnit.SECONDS);
            if(!myLock) {
                throw new DataAccessException("Timeout expired waiting for entering a QueryDescriptor filling phase.");
            }

            if(queryDictionary.containsKey(registryTableName))
                return;

            queryDesc = new QueryDescriptor();
        
            discoverExtraProperties(registryTableName);
        
            createQueries(registryTableName, clusterSize,serverIndex);

            queryDictionary.put(registryTableName, queryDesc);

            columnSuffix = null;
            placeholderSuffix = null;
            updateSuffix = null;
        } catch(Exception ex) {
            throw new DataAccessException("Error filling QueryDescriptor object.",ex);
        } finally {
            if(lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    // --------------------------------------------------------- Private methods

    /**
     * Creates a RegistryEntry reading the given ResultSet
     * @return the RegistryEntry
     * @param rs the resultSet
     * @throws java.sql.SQLException if an error occurs
     */
    private RegistryEntry resultSetToRegistryEntry(ResultSet rs)
    throws SQLException {

        RegistryEntry entry = new RegistryEntry();

        entry.setId            (rs.getLong(1));
        entry.setPeriod        (rs.getLong(2));
        entry.setActive        ("Y".equalsIgnoreCase(rs.getString(3)));
        entry.setTaskBeanFile  (rs.getString(4));
        entry.setLastUpdate    (rs.getLong(5));

        String s = rs.getString(6);
        if (s == null || s.length() == 0) {
            entry.setStatus(RegistryEntryStatus.UNKNOWN);
        }
        if ("N".equals(s)) {
            entry.setStatus(RegistryEntryStatus.NEW);
        } else if ("U".equals(s)) {
            entry.setStatus(RegistryEntryStatus.UPDATED);
        } else if ("D".equals(s)) {
            entry.setStatus(RegistryEntryStatus.DELETED);
        } else {
            entry.setStatus(RegistryEntryStatus.UNKNOWN);
        }

        short numberOfExtraProperties = queryDesc.getNumberOfExtraColumns();
        String[] extraProperties = queryDesc.getExtraProperties();

        if(numberOfExtraProperties>0) {
            Map<String,Object> extraPropertiesMap = new LinkedHashMap<String, Object>();
            for(short j=0;j<numberOfExtraProperties;j++) {
                extraPropertiesMap.put(extraProperties[j],
                                       rs.getObject(7+j));
            }
            entry.setProperties(extraPropertiesMap);
        }


        return entry;
    }

    /**
     * Returns the next id to use inserting an entry
     * @throws com.funambol.pushlistener.service.registry.dao.DataAccessException if an error occurs
     * @return the next id to use inserting an entry
     */
    private long getNextEntryId() throws DataAccessException {

        long next = 0;

        try {
            next = dbIdGenerator.next();
        } catch (Exception e) {
            throw new DataAccessException("Error getting the next entry id", e);
        }

        return next;
    }

    /**
     * Returns the database connection
     * @throws SQLException if an error occurs
     * @return the database connection
     */
    private Connection getConnection() throws SQLException {
        return  this.dataSource.getConnection();
    }

    /**
     * Initializes the RegistryDao that works on the given table. See also
     * class description for some information about clusterSize and serverIndex
     * @param registryTableName the registry table name
     * @param entriesIdSpace the idSpace to use creating the new entry ids
     * @param ds the datasource
     * @param clusterSize number of the members in the cluster
     * @param serverIndex unique index of the instance in the cluster
     */
    private void init(String     registryTableName,
                      String     entriesIdSpace,
                      DataSource ds,
                      int        clusterSize,
                      int        serverIndex)
    throws DataAccessException {

        if (ds == null) {
            throw new IllegalArgumentException("The datasource must be not null");
        } 
        this.dataSource = ds;

        if (registryTableName == null || registryTableName.length() == 0) {
            registryTableName = DEFAULT_REGISTRY_TABLE_NAME;
        }

        if (entriesIdSpace == null || entriesIdSpace.length() == 0) {
            this.idSpace = DEFAULT_ID_SPACE;
        } else {
            this.idSpace = entriesIdSpace;
        }

        fillQueryDescriptor(registryTableName, clusterSize, serverIndex);

        dbIdGenerator =
            DBIDGeneratorFactory.getDBIDGenerator(this.idSpace, this.dataSource);

    }
}
