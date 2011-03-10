/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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
package com.funambol.foundation.items.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;

import javax.sql.DataSource;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.id.DBIDGenerator;
import com.funambol.framework.tools.id.DBIDGeneratorException;
import com.funambol.framework.tools.id.DBIDGeneratorFactory;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.util.Def;

import com.funambol.server.db.RoutingDataSource;

/**
 * This class implements methods to access data in a data store.
 *
 * @version $Id:  $
 */
public abstract class EntityDAO {
    
    // --------------------------------------------------------------- Constants
    private static final String JNDI_NAME_CORE_DATASOURCE = "jdbc/fnblcore";
    private static final String JNDI_NAME_USER_DATASOURCE = "jdbc/fnbluser";

    // ------------------------------------------------------------ Private data
    
    protected static final FunambolLogger log =
            FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);
    
    protected DataSource        coreDataSource = null;
    protected RoutingDataSource userDataSource = null;
    
    protected DBIDGenerator dbIDGenerator;
    
    // -------------------------------------------------------------- Properties
    
    protected String userId;
    public String getUserId() {
        return userId;
    }
    
    // ------------------------------------------------------------ Constructors
    
    /**
     * Creates a new instance of EntityDAO, ready to be linked
     * to a given DataSource.
     *
     * @param userId corresponding to the "userid" field of the fun_pim_contact
     *               table
     * @param uidNameSpace The name space for the generated unique identifiers
     * @throws IllegalArgumentException if there are errors looking up the
     *         dataSource with the given jndiDataSourceName
     */
    public EntityDAO(String userId, String uidNameSpace) {
        this.userId = userId;
        
        try {
            this.coreDataSource =
                DataSourceTools.lookupDataSource(JNDI_NAME_CORE_DATASOURCE);
        } catch (NamingException ex) {
            throw new IllegalArgumentException(
                "Error looking up datasource: " +JNDI_NAME_CORE_DATASOURCE, ex);
        }

        try {
            this.userDataSource =
                (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_NAME_USER_DATASOURCE);
        } catch (NamingException ex) {
            throw new IllegalArgumentException(
                "Error looking up datasource: " +JNDI_NAME_USER_DATASOURCE, ex);
        }
        this.dbIDGenerator =
            DBIDGeneratorFactory.getDBIDGenerator(uidNameSpace, coreDataSource);
    }

    
    // ---------------------------------------------------------- Public methods
    
    /**
     * Retrieves the UID list of all items belonging to the user.
     *
     * @throws DAOException
     * @return a List of UIDs (as String objects)
     */
    public List getAllItems() throws DAOException {

        List<String> ids = new ArrayList<String>();

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(getAllItemsQuery());
            ps.setString(1, userId);

            rs = ps.executeQuery();
            while (rs.next()) {
                ids.add(Long.toString(rs.getLong(1)));
            }

        } catch (Exception e) {
            throw new DAOException("Error getting all the items.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return ids;
    }

    /**
     * Removes the item with given UID and sets its last_update field,
     * provided it has the same userId as this DAO.
     * The deletion is soft (reversible).
     *
     * @param uid corresponds to the id field 
     * @param lastUpdate if null, the present date is used. NB: if one uses the
     *                   current last update value for this argument, the
     *                   deletion won't be detected! this argument is meant to
     *                   allow the deletion registered moment to be forced.
     * @throws DAOException
     */
    public void removeItem(String uid, Timestamp lastUpdate)
    throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {

            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(getRemoveItemQuery());

            Timestamp lu = lastUpdate;
            if (lu == null) {
                lu = new Timestamp(System.currentTimeMillis());
            }
            ps.setLong(1, lu.getTime());
            ps.setLong(2, Long.parseLong(uid));
            ps.setString(3, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DAOException("Error deleting item " + uid, e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }
    
    /**
     * Deletes (reversibly) all items belonging to the user. 
     * The last_update field of the (soft-)deleted items will be set at a given 
     * timestamp.
     *
     * @param lastUpdate if null, the present date is used. NB: if one uses the
     *                   current last update value for this argument, the
     *                   deletion won't be detected! this argument is meant to
     *                   allow the deletion registered moment to be forced.
     * @throws DAOException
     */
    public void removeAllItems(Timestamp lastUpdate) throws DAOException {

        Connection con = null;
        PreparedStatement ps = null;

        try {
            con = getUserDataSource().getRoutedConnection(userId);

            ps = con.prepareStatement(getRemoveAllItemsQuery());
            
            Timestamp lu = lastUpdate;
            if (lu == null) {
                lu = new Timestamp(System.currentTimeMillis());
            }
            ps.setLong(1, lu.getTime());
            ps.setString(2, userId);

            ps.executeUpdate();

        } catch (Exception e) {
            throw new DAOException("Error deleting items.", e);
        } finally {
            DBTools.close(con, ps, null);
        }
    }
    
    /**
     * Retrieves the state of the given item, provided it's been modified after
     * a certain moment.
     *
     * @param uid the UID of the item to be checked (as a String object)
     * @param since the Timestamp that the item's lastUpdate field is checked
     *              against: if the item has been modified before that moment,
     *              an "unchanged" state marker is returned
     * @throws DAOException
     * @return a char identifying either one of the 3 standard states ("new",
     *         "deleted", "updated") or the special "unchanged" status, all of
     *         them as defined in com.funambol.pim.util.Def
     */
    public char getItemState(String uid, Timestamp since)
    throws DAOException {
        
        char status;

        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(getItemStateQuery());

            ps.setLong(1, Long.parseLong(uid));
            ps.setString(2, userId);
            ps.setLong(3, since.getTime());

            rs = ps.executeQuery();
            if (!rs.next()) {
                status = Def.PIM_STATE_UNCHANGED;
                if (log.isTraceEnabled()) {
                    log.trace("Item " + uid + "'s status wasn't retrieved " +
                            "because the item hasn't been modified since " +
                            since + " or the item doesn't exist");
                }
            } else {
                status = rs.getString(1) // It's the first and only column
                        .charAt(0); // It's the first and only character
                if (log.isTraceEnabled()) {
                    log.trace("Item " + uid + " has status \'" + status + "\'");
                }
            }

        } catch (Exception e) {
            throw new DAOException("Error retrieving item state.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }

        return status;
    }
    
    /**
     * Returns an array of Lists with the NEW, UPDATED, DELETED item ids.
     * <p>
     * <ui>
     * <li>list[0]: new item ids</li>
     * <li>list[1]: updated item ids</li>
     * <li>list[2]: deleted item ids</li>
     * </ui>
     *
     * @param since the earliest allowed last-update Timestamp
     * @param to the latest allowed last-update Timestamp
     * @return an array of List with the NEW, UPDATED, DELETED item ids.
     * @throws DAOException if an error occurs
     */
    public List<String>[] getChangedItemsByLastUpdate(Timestamp since, Timestamp to) throws DAOException {
        
        if (log.isTraceEnabled()) {
            log.trace("Seeking changed items "
                    + "in time interval ]" + since + "; " + to + "[");
        }
        
        List<String> newItems     = new ArrayList<String>();
        List<String> updatedItems = new ArrayList<String>();
        List<String> deletedItems = new ArrayList<String>();

        List<String>[] changedItems = new ArrayList[3];
        changedItems[0] = newItems;
        changedItems[1] = updatedItems;
        changedItems[2] = deletedItems;

        Connection con = null;
        PreparedStatement ps = null;

        ResultSet rs = null;

        try {
            // Looks up the data source when the first connection is created
            con = getUserDataSource().getRoutedConnection(userId);
            con.setReadOnly(true);

            ps = con.prepareStatement(getChangedItemsQuery());
            ps.setString(1, userId);
            ps.setLong  (2, since.getTime());
            ps.setLong  (3, to.getTime());

            rs = ps.executeQuery();
            while(rs.next()) {
                Long id = rs.getLong(1);
                String status = rs.getString(2);
                char s = status.charAt(0);
                if (s == 'N' || s == 'n') {
                    newItems.add(id.toString());
                } else if (s == 'U' || s == 'u') {
                    updatedItems.add(id.toString());
                } else if (s == 'D' || s == 'd') {
                    deletedItems.add(id.toString());
                }
            }

        } catch (Exception e) {
            throw new DAOException("Error getting changed (N,U,D) items.", e);
        } finally {
            DBTools.close(con, ps, rs);
        }
    
        return changedItems;
        
    }

    /**
     * Gets a unique identifier
     * @return the unique identifier
     * @throws com.funambol.foundation.exception.DAOException if an error
     * occurs computing the next unique identifier
     */
    public String getNextID() throws DAOException {
        try {
            return String.valueOf(dbIDGenerator.next());
        } catch (DBIDGeneratorException dbex) {
            throw new DAOException(dbex);
        }
    }

    /**
     * Removes all properties for the given item identifier.
     *
     * @param itemId the identifier for which remove all properties
     * @throws DAOException if an error occurs during deletion
     */
    public void removeAllProperties(String itemId) throws DAOException {
        // use this method to remove all properties associated to the item
    }

    /**
     * Removes all item properties belonging to the user.
     *
     * @throws DAOException if an error occurs during deletion
     */
    public void removeAllPropertiesByUserID() throws DAOException {
        // use this method to remove all properties associated to the item
        // for the given user id
    }

    //---------------------------------------------------------- Private methods
    
    /**
     * Return the query string to use to retrieve all the Items belonging to a user
     * @return the query string to use to retrieve all the Items belonging to a user
     */
    protected abstract String getAllItemsQuery();
    
    /**
     * Return the query string to use to remove the Item belonging to a user
     * @return the query string to use to remove the Item belonging to a user
     */
    protected abstract String getRemoveItemQuery();
    
    /**
     * Return the query string to use to remove the all Items belonging to a user
     * @return the query string to use to remove the all Items belonging to a user
     */
    protected abstract String getRemoveAllItemsQuery();
    
    /**
     * Return the query string to use to retrieve the status of an Items 
     * belonging to a user
     * @return the query string to use to retrieve the status of an Items 
     * belonging to a user
     */
    protected abstract String getItemStateQuery();
    
    /**
     * Return the query string to use to retrieve the lists of changed items
     * @return the query string to use to retrieve the lists of changed items 
     */
    protected abstract String getChangedItemsQuery();
    
    /**
     * Looks up the data source, making some guess attempts based on
     * jndiDataSourceName.
     *
     * @return the core datasource
     * @throws Exception
     */
    protected DataSource getCoreDataSource() throws Exception {
        return coreDataSource;
    }

    /**
     * Looks up the data source, making some guess attempts based on
     * jndiDataSourceName.
     *
     * @return the user datasource
     * @throws Exception
     */
    protected RoutingDataSource getUserDataSource() throws Exception {
        return userDataSource;
    }
}
