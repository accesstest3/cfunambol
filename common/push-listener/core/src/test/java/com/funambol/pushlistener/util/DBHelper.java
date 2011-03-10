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
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.text.MessageFormat;

import javax.sql.DataSource;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.pushlistener.service.registry.dao.RegistryDao;

/**
 *
 * @version $Id: DBHelper.java 35029 2010-07-12 14:21:06Z filmac $
 */
public class DBHelper {

    private final static Logger log = Logger.getLogger(DBHelper.class);

    /**
     * It's the sql file containing the script used to create the database schema.
     */
    public final static String DB_SCHEMA_SOURCE =
        "src/test/resources/sql/create_schema.sql";

    /**
     * loading the database schema
     */
    static {
        try {
            createDatabaseSchema();
        } catch (Exception ex) {
            log.error("An error occurred loading database schema.", ex);
        }
    }

    protected static void importScript(String fileName) throws Exception {
        Connection connection = getCoreConnection();
        importScript(connection, fileName);
    }

    protected static void importScript(Connection connection,
                                       String fileName) throws Exception {

        String script = loadFileAsString(fileName);

        executeStatement(connection,script);
    }

    /**
     * This method allows to create the database schema.
     * @throws Exception
     */
    protected static void createDatabaseSchema() throws Exception {
         importScript(DB_SCHEMA_SOURCE);
    }

    /**
     *
     * A connection taken from the core datasource.
     *
     * @return a jdbc connection.
     *
     * @throws java.lang.Exception if any error occurs.
     */
    public static Connection getCoreConnection() throws Exception {
        DataSource ds   =
            DataSourceTools.lookupDataSource(RegistryDao.DEFAULT_DATASOURCE_JNDI_NAME);
        Connection conn = null;
        try {
            conn = ds.getConnection();
            return conn;
        } catch (Exception e) {
            DBTools.close(conn, null, null);
            throw new Exception("An error occurred while obtaining core connection.",e);
        }
    }

    /**
     * This method is used to load a file into a string.
     * @param fileName is the name of the file to load.
     * @return it's a string containing the whole file.
     * @throws java.lang.Exception if the file doesn't exist or any error occurs
     * while loading the file
     */
    public static String loadFileAsString(String fileName)
    throws Exception {
        File sourceFile = new File(fileName);
        if(!sourceFile.exists() || !sourceFile.isFile()) {
            throw new Exception("No file for the given name ["
                                +fileName+"] is found.");
        }
        FileInputStream inputFile = new FileInputStream(sourceFile);
        String script = IOUtils.toString(inputFile);
        IOUtils.closeQuietly(inputFile);
        inputFile = null;
        return script;
    }

    /**
     * This method is used to execute the given script as statement on the
     * given connection. After the execution of the script, the connection
     * will be closed.
     * @param connection is the connection to use while executing statement.
     * @param script is the statement to execute.
     * @return the number of statements properly executed.
     * @throws java.lang.Exception if any error occurs.
     */
    protected static int executeStatement(Connection connection,
                                         String script) throws Exception {
        Statement stmt = null;
        try {
            stmt = connection.createStatement();
            return stmt.executeUpdate(script);
        } catch (Exception e) {
            throw new Exception("An error occurred while executing the statement ["
                                +script+"].",e);
        } finally {
            DBTools.close(connection, stmt, null);
        }
        
    }

    public static String replaceQueryPlaceholders(String query, Object...values) {
        if(query!=null) {
            return MessageFormat.format(query, values);
        }
        return query;
    }

}
