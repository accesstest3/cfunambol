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

package com.funambol.pushlistener.util;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.DefaultDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;

import com.funambol.framework.tools.DBTools;

/**
 * It's an utility class used to wrap usage of DBUnit classes.
 * This class uses Assertion class to perform comparison, so it must be used
 * inside JUnit classes only.
 *
 * @version $Id: DBUnitHelper.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class DBUnitHelper {

    private static final Logger log = Logger.getLogger(DBUnitHelper.class);

    /**
     * This method is used to close connection used by the tester.
     * 
     * @param coreDatabaseTester
     */
    public static void closeConnection(IDatabaseTester coreDatabaseTester)
                                                              throws Exception {
        if(coreDatabaseTester!=null) {
            closeConnection(coreDatabaseTester.getConnection());
        }

    }


    /**
     * This method is used to compare the data
     * The core connection obtained using the DBHelper class will be used.
     * 
     * @param query it's the query to use to load the actual data set.
     * @param tableName it's the table name we're considering.
     * @param fileName it's the name of the file containing the expected data sete.
     * @param excludedColumns are the columns to be excled from both datasets.
     *
     * @throws java.lang.Exception if any error occurs.
     */
    public static void compareDataSet(String query,
                                      String tableName,
                                      String fileName,
                                      String[] excludedColumns) throws Exception {
        DatabaseConnection connection =
            new DatabaseConnection(DBHelper.getCoreConnection());

        try {
            assertEqualsDataSet(connection,
                                tableName,
                                query,
                                fileName,
                                excludedColumns);
        } finally {
            closeConnection(connection);
        }
    }


    /**
     *
     * @param connection it's the DatabaseConnection used to retrieve the actual
     * table.
     * @param tableName it's the name of the table we want to compare.
     * @param fileName it's the name of the file used to load the expected data
     *
     * @throws java.sql.SQLException if any error accessing database occurs.
     * @throws org.dbunit.dataset.DataSetException if any error building DataSet
     * occurs.
     * @throws java.io.IOException if any error accessing file system occurs.
     * @throws org.dbunit.DatabaseUnitException if any error comparing DataSet
     * occurs.
     */
    public static void assertEqualsDataSet(DatabaseConnection connection,
                                    String tableName,
                                    String fileName) throws  SQLException,
                                                             DataSetException,
                                                             IOException,
                                                             DatabaseUnitException{

        if(log.isDebugEnabled()) {
            log.debug("Extacting DataSet for table ["+tableName+"].");
        }
        IDataSet databaseDataSet = connection.createDataSet();
        ITable actualTable = databaseDataSet.getTable(tableName);

        if(log.isDebugEnabled()) {
            log.debug("Loading expected dataset from file ["+fileName+"].");
        }
        IDataSet expectedDataSet = new FlatXmlDataSet(new File(fileName));
        ITable expectedTable = expectedDataSet.getTable(tableName);

        Assertion.assertEquals(expectedTable, actualTable);
    }

    /**
     * This method is used to close a DatabaseConnection.
     * 
     * @param connection the connection to be closed.
     */
    private static void closeConnection(IDatabaseConnection connection) {
        if(connection!=null) {
            Connection realConnection;
            try {
                realConnection = connection.getConnection();
                if(realConnection!=null) {
                   DBTools.close(realConnection, null, null);
                }
            } catch (SQLException ex) {
                log.warn("An error occurred closing connection.",ex);
            }
            
        }
    }


    /**
     * This method is used to assert that two different DataSets are equals.
     * The first
     * It's possible to specify an array of columns to be excluded during the
     * comparison.
     * The connection must be closed by the caller logic.
     * @param connection it's the connection that will be used to load data from
     * the database.
     * @param tableName it's the name of the table we want to check.
     * @param query it's the query used to load content from the database.
     * @param fileName it's the name of the file containing the DataSet.
     * @param exlcudedColumns are the columns that must not be considered.
     * @throws java.sql.SQLException if any error accessing database occurs.
     * @throws org.dbunit.dataset.DataSetException if any error building DataSet
     * occurs.
     * @throws java.io.IOException if any error accessing file system occurs.
     * @throws org.dbunit.DatabaseUnitException if any error comparing DataSet
     * occurs.
    */
    public static void assertEqualsDataSet(DatabaseConnection connection,
                                    String tableName,
                                    String query,
                                    String fileName,
                                    String[] exlcudedColumns) 
    throws Exception {

        ITable actualTable    = createITableFromQuery(tableName, query, connection);
        ITable expectedTable  = loadITableFromFile(tableName, fileName);
        if(exlcudedColumns!=null) {
            actualTable = new SortedTable(
                DefaultColumnFilter.excludedColumnsTable(actualTable, exlcudedColumns)
            );
            expectedTable = new SortedTable(
                DefaultColumnFilter.excludedColumnsTable(expectedTable, exlcudedColumns)
            );


        }
        Assertion.assertEquals(expectedTable, actualTable);
    }



    /**
     * This method allows to create an ITable for the given table using the
     * given query.
     * This method it's not responsible for the closing of the connection.
     * @param tableName it's the name of the table we're creating an ITable for.
     * @param query it's the query used to create the ITable object.
     * @param connection it's the connection to use.
     * 
     * @return the desired ITable.
     * 
     * @throws java.sql.SQLException if any error accessing database occurs.
     * @throws org.dbunit.dataset.DataSetException if any error building DataSet
     * occurs.
     */

    public static ITable createITableFromQuery(String tableName,
                                                 String query,
                                                 DatabaseConnection connection)
                                                       throws DataSetException,
                                                       SQLException {
            if(log.isDebugEnabled()) {
                log.debug("Loading table ["+tableName+"] using ["+query+"].");
            }

            return connection.createQueryTable(tableName, query);
     }

        public static ITable loadITableFromFile(String tableName,
                                                String fileName) throws IOException,
                                                DataSetException {
            if(log.isDebugEnabled()) {
                log.debug("Loading table ["+tableName+"] from ["+fileName+"].");
            }

            IDataSet expectedDataSet = new FlatXmlDataSet(new File(fileName));
            return expectedDataSet.getTable(tableName);

        }

    public static IDatabaseTester createDatabaseTester(String source,
                                            boolean settingUp) throws Exception {
        IDatabaseTester databaseTester  = new DefaultDatabaseTester(new DatabaseConnection(DBHelper.getCoreConnection()));
        IDataSet        databaseDataSet = new FlatXmlDataSet(new File(source));
        databaseTester.setDataSet(databaseDataSet);
        databaseTester.setSetUpOperation(DatabaseOperation.CLEAN_INSERT);
        if(settingUp) {
            databaseTester.onSetup();
        }
        return databaseTester;
    }


}
