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

package com.funambol.server.tools.id;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.DBTools;

/**
 * This class works as an id generator that read their values from a datastore.
 * In order to create an instance a DataSource is needed
 * <P/>
 * Any db read is performed locking a row with a
 * <CODE>SELECT FOR UPDATE</CODE> (if the underlying db supports that).<br/>
 * Using Microsoft SQLServer, that doesn't support <CODE>SELECT FOR UPDATE</CODE>,
 * a specific syntax is used:<br/>
 * <CODE>select counter from fnbl_id (UPDLOCK) where idspace=?</CODE>
 * <p/>
 * If the requested name space is not in the database, a new one will be created.
 * <p/>
 * Note that for a easy usage, the
 * <code>com.funambol.server.tools.id.DBIDGeneratorFactory</code> should be used,
 * otherwise your class that needs a DBIDGenerator should keep a
 * static reference to it.
 *
 * @deprecated Use the one under com/funambol/framework/tools/id (available
 *             in the server-framework jar file)
 *
 * @version $Id: DBIDGenerator.java,v 1.1.1.1 2008-02-21 23:36:03 stefano_fornari Exp $
 */
public class DBIDGenerator implements java.io.Serializable {

    // --------------------------------------------------------------- Constants

    /**
     * The default number of cached id
     */
    private static final int DEFAULT_INCREMENT_BY = 100;

    /**
     * The name of the used logger
     */
    private static final String LOGGER_NAME = "funambol.server.tools.id";

    /**
     * Base query to get a value
     */
    private static final String SQL_GET_ID = "select counter, increment_by from fnbl_id where idspace=?";

    /**
     * The query to get a value using Microsoft SQL Server
     */
    private static final String SQL_GET_ID_SQLSERVER = "select counter, increment_by from fnbl_id (UPDLOCK) where idspace=?";

    /**
     * The query to update a value
     */
    private static final String SQL_UPDATE_ID = "update fnbl_id set counter=? where idspace=?";

    /**
     * The query to create a new counter
     */
    private static final String SQL_INSERT_ID = "insert into fnbl_id (idspace, counter, increment_by) values (?,?,?)";

    /**
     * The driver name used to check if the underlying database is a Microsoft SQL Server
     */
    private static final String DRIVER_NAME_SQLSERVER = "SQLServer";

    // ------------------------------------------------------------ Private Data

    /**
     * The used logger
     */
    private static transient FunambolLogger log = FunambolLoggerFactory.getLogger(LOGGER_NAME);

    /**
     * How many values are available without to perform a new db access ?
     */
    private int availableIds = 0;

    /**
     * The last value returned
     */
    private long currentValue = 0;

    /**
     * Does the underlying database support <CODE>SELECT FOR UPDATE</CODE> ?
     */
    private boolean supportsSelectForUpdate = false;

    /**
     * The driver name
     */
    private String dbDriverName  = null;

    /**
     * The query to read a value
     */
    private String selectQuery   = null;

    /**
     * The DataSource used to access to the db
     */
    private DataSource dataSource;

    /**
     * The incrementBy properties
     */
    public int incrementBy = DEFAULT_INCREMENT_BY;

    /**
     * The name space
     */
    private String nameSpace = null;

    /**
     * Is this instance already initialized ? This parameter is used in order to
     * initialized the instance the first time. We could initialize the instance in
     * the constructor, but in this case, the constructor should throw a
     * DBIDGeneratorException that complicates the use becase a static instance
     * can not be created easly
     */
    private boolean isInitialized = false;

    // -------------------------------------------------------------- Properties

    // ------------------------------------------------------------ Constructors

    /**
     * Create a new DBIDGenerator using the given DataSource. The increment value
     * is set to <code>DEFAULT_CACHE_SIZE</code>
     * @param nameSpace the name space (counter name)
     * @param ds the datasource to use
     */
    public DBIDGenerator(DataSource ds, String nameSpace) {

        if (nameSpace == null) {
            throw new IllegalArgumentException("The nameSpace must be not null");
        }

        this.nameSpace = nameSpace;

        setIncrementBy(DEFAULT_INCREMENT_BY);

        if (ds == null) {
            throw new IllegalArgumentException("The datasource must be not null");
        }
        dataSource = ds;

    }

    /**
     * Returns the next value of the given space. If there is a value available,
     * no new value is read from the datastore, otherwise a query is performed.
     * @return the next generated value
     * @throws IDGeneratorException if an error occurs
     */
    public synchronized long next() throws DBIDGeneratorException {
        if (availableIds > 0) {
            availableIds--;
            return (++currentValue);
        } else {
            currentValue = read();
        }

        availableIds = incrementBy - 1;

        return currentValue;
    }


    // ---------------------------------------------------------- Private method

    /**
     * Read a value from the data store. If no idSpace with the same name is
     * found, a new one is created.
     *
     * @return the read value
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private long read() throws DBIDGeneratorException {

        if (!isInitialized) {
            init();
        }

        Connection        conn         = null;
        PreparedStatement stmt         = null;
        ResultSet         rs           = null;
        boolean           isAutoCommit = false;

        long value = 0;

        try {

            conn = dataSource.getConnection();

            if (supportsSelectForUpdate) {
                //
                // Checking the autocommit because using the "select for update"
                // the autocommit must be disable.
                // After the queries the original value is set.
                //
                isAutoCommit = conn.getAutoCommit();
                conn.setAutoCommit(false);

                stmt = conn.prepareStatement(selectQuery);
            } else {
                stmt = conn.prepareStatement(selectQuery);
            }

            stmt.setString(1, nameSpace);
            rs = stmt.executeQuery();

            if (!rs.next()) {
                //
                // IDSpace not found. A new one will be created
                //
                DBTools.close(null, stmt, rs);

                stmt = conn.prepareStatement(SQL_INSERT_ID);
                stmt.setString(1, nameSpace);
                stmt.setLong  (2, 0);
                stmt.setInt   (3, incrementBy);
                stmt.executeUpdate();

                DBTools.close(null, stmt, null);

            } else {
                //
                // IDSpace found
                //
                value = rs.getLong(1);
                setIncrementBy(rs.getInt(2));

                DBTools.close(null, stmt, rs);
            }

            stmt = conn.prepareStatement(SQL_UPDATE_ID);
            stmt.setLong  (1, value + incrementBy);
            stmt.setString(2, nameSpace);
            stmt.executeUpdate();

            if (supportsSelectForUpdate) {
                conn.commit();
            }

        } catch (SQLException e) {

            try {
                if (conn != null && supportsSelectForUpdate) {
                    conn.rollback();
                }
            } catch (SQLException e1) {
                //
                // Nothing to do
                //
            }

            throw new DBIDGeneratorException("Error reading the counter: " +
                                             nameSpace, e);
        } finally {
            if (conn != null) {
                try {
                    if (supportsSelectForUpdate) {
                        //
                        // Setting the original value for the autocommit
                        //
                        conn.setAutoCommit(isAutoCommit);
                    }
                } catch (SQLException e) {
                    //
                    // Nothing to do
                    //
                }
            }
            DBTools.close(conn, stmt, rs);
        }

        return value;
    }

    /**
     * Initializes the datasource and the queries
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private void init() throws DBIDGeneratorException {
        if (!isInitialized) {
            initQueries();
        }
        isInitialized = true;
    }


    /**
     * Checking the underlying database, initializes the queries to use
     * @throws com.funambol.server.tools.id.DBIDGeneratorException if an error occurs
     */
    private void initQueries() throws DBIDGeneratorException {
        Connection conn = null;
        try {

            conn = dataSource.getConnection();

            java.sql.DatabaseMetaData dmd = conn.getMetaData();

            supportsSelectForUpdate = dmd.supportsSelectForUpdate();
            dbDriverName = dmd.getDriverName();

            if (DRIVER_NAME_SQLSERVER.equals(dbDriverName)) {
                //
                // Running on SQL Server we have to use SQL_GET_ID_SQLSERVER.
                // Moreover on SQL Server, dmd.supportsSelectForUpdate() returns
                // false but we need to work as the "select for update" (with
                // a different query:
                // select counter from fnbl_id (UPDLOCK) where idspace=?)
                //
                selectQuery = SQL_GET_ID_SQLSERVER;
                supportsSelectForUpdate = true;

            } else {
                if (supportsSelectForUpdate) {
                    selectQuery = SQL_GET_ID + " for update";
                } else {
                    selectQuery = SQL_GET_ID;
                }
            }

        } catch (SQLException e) {
            throw new DBIDGeneratorException(
                    "Error checking if the database supports 'select for update'",
                    e);
        } finally {
            DBTools.close(conn, null, null);
        }

    }

    /**
     * Sets the incrementBy property checking if it is bigger than 0
     * @param incr the incrementBy value
     */
    private void setIncrementBy(int incr) {
        if (incr < 1) {
            if (log.isWarningEnabled()) {
                log.warn("The increment value is smaller than 1 (" + incr + "). " +
                         "Setting it to 1");
            }
            incrementBy = 1;
        } else {
            incrementBy = incr;
        }

    }
}
