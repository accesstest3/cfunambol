/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

package com.funambol.ctp.client.driver;

import com.funambol.ctp.client.driver.session.SessionManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Inserts users, devices and principals used for tests into the DB
 */
public class AddUsers {

    // --------------------------------------------------------------- Constants

    public static final String PROPERTY_DRIVER =     "jdbc.driver";
    public static final String PROPERTY_DBURL =      "jdbc.url";
    public static final String PROPERTY_USERNAME =   "jdbc.user";
    public static final String PROPERTY_PASSWORD =   "jdbc.password";
    public static final String PROPERTY_PREFIX =     "prefix";
    public static final String PROPERTY_INITIAL_ID = "principal.first.id";
    public static final String PROPERTY_FIRST =      "first.id";
    public static final String PROPERTY_LAST =       "last.id";

    public static final String INSERT_INTI_FNBL_USER =      "insert into fnbl_user (username,password,first_name,last_name) values (?,'fVTxodEYUKo+TCqfFhl8EQ==','First','Last')"; // username
    public static final String INSERT_INTI_FNBL_USER_ROLE = "insert into fnbl_user_role (username,role) values (?,'sync_user')"; // username
    public static final String INSERT_INTI_FNBL_DEVICE =    "insert into fnbl_device (id,convert_date, charset, notification_builder, notification_sender) values (?,'N', 'UTF-8', 'com/funambol/server/notification/DSNotificationBuilder.xml', 'com/funambol/server/notification/PushSender.xml')"; // deviceid
    public static final String INSERT_INTI_FNBL_PRINCIPAL = "insert into fnbl_principal (id, username, device) values (?, ?, ?)";// principalid, username, deviceid

    // ------------------------------------------------------------ Private data

    private Connection connection = null;

    private PreparedStatement insertInto_fnbl_user =      null;
    private PreparedStatement insertInto_fnbl_user_role = null;
    private PreparedStatement insertInto_fnbl_device =    null;
    private PreparedStatement insertInto_fnbl_principal = null;

    private String dbDriver = null;      // La stringa contenente il nome del driver
    private String dbURL = null;         // La stringa contenente la URL del DB
    private String dbUsername = null;    // UserID di connessione
    private String dbPassword = null;    // Password associata alla username

    private String prefix = null;
    private long initialId = 50000;

    private int first = 1;
    private int last = 10;

    /**
     *
     */
    private Properties  prop = new Properties();
    private String dirPath = "config";
    private String propertyFileName = "ctpaddusers.properties";

    // ------------------------------------------------------------ Constructors

    /**
     * Creates a new instance of <code>Class</code>.
     */
    public AddUsers() throws FileNotFoundException, IOException {
        readProperties();
        logProperties();
    }

    // ---------------------------------------------------------- Public methods

    public void connect() throws ClassNotFoundException, SQLException {
        Class.forName(dbDriver);
        connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);

        insertInto_fnbl_user =      (PreparedStatement) connection.prepareStatement(INSERT_INTI_FNBL_USER);
        insertInto_fnbl_user_role = (PreparedStatement) connection.prepareStatement(INSERT_INTI_FNBL_USER_ROLE);
        insertInto_fnbl_device =    (PreparedStatement) connection.prepareStatement(INSERT_INTI_FNBL_DEVICE);
        insertInto_fnbl_principal = (PreparedStatement) connection.prepareStatement(INSERT_INTI_FNBL_PRINCIPAL);
    }

    public void insertAll() {
        for (int i = first; i <= last; ++i) {
            executeStmts(i);
        }
    }

    public void executeStmts(int index) {

        String postfix = String.valueOf(index);

        String username = prefix + SessionManager.USER + postfix;    // $1user$i
        String deviceid = prefix + SessionManager.DEVICE + postfix;    // $1device$i
        long principalid = initialId + index;

        try {

            insertInto_fnbl_user.setString(1, username);

            insertInto_fnbl_user_role.setString(1, username);

            insertInto_fnbl_device.setString(1, deviceid);

            insertInto_fnbl_principal.setLong(1, principalid);
            insertInto_fnbl_principal.setString(2, username);
            insertInto_fnbl_principal.setString(3, deviceid);

            insertInto_fnbl_user.execute();
            //System.out.println("Successfully execute: " + insertInto_fnbl_user.toString());

            insertInto_fnbl_user_role.execute();
            //System.out.println("Successfully execute: " + insertInto_fnbl_user_role.toString());

            insertInto_fnbl_device.execute();
            //System.out.println("Successfully execute: " + insertInto_fnbl_user.toString());

            insertInto_fnbl_principal.execute();
            //System.out.println("Successfully execute: " + insertInto_fnbl_principal.toString());
        } catch (SQLException ex) {
            System.out.println("Unable to execute: " + insertInto_fnbl_user.toString());
            System.out.println("Unable to execute: " + insertInto_fnbl_user_role.toString());
            System.out.println("Unable to execute: " + insertInto_fnbl_user.toString());
            System.out.println("Unable to execute: " + insertInto_fnbl_principal.toString());
            ex.printStackTrace();
        }

    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    // --------------------------------------------------------- Private methods

    private void readProperties() throws FileNotFoundException, IOException {
        File propFile = new File(dirPath, propertyFileName);
        if (propFile.exists()) {
            prop.load(new FileInputStream(propFile));
        } else {
            prop.store(new FileOutputStream(propFile), "Add Users property file");
        }

        this.dbDriver =   prop.getProperty(PROPERTY_DRIVER);
        if (dbDriver == null || "".equals(dbDriver)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_DRIVER + " in " + propFile.getName());
        }

        this.dbURL =      prop.getProperty(PROPERTY_DBURL);
        if (dbURL == null || "".equals(dbURL)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_DBURL + " in " + propFile.getName());
        }

        this.dbUsername = prop.getProperty(PROPERTY_USERNAME);
        if (dbUsername == null || "".equals(dbUsername)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_USERNAME + " in " + propFile.getName());
        }

        this.dbPassword = prop.getProperty(PROPERTY_PASSWORD);
        if (dbPassword == null) {
            this.dbPassword = "";
        }

        this.prefix = prop.getProperty(PROPERTY_PREFIX);
        if (prefix == null || "".equals(prefix)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_PREFIX + " in " + propFile.getName());
        }

        String initialIdStr = prop.getProperty(PROPERTY_INITIAL_ID);
        if (initialIdStr == null || "".equals(initialIdStr)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_INITIAL_ID + " in " + propFile.getName());
        }
        initialId = Long.parseLong(initialIdStr);

        String firstStr = prop.getProperty(PROPERTY_FIRST);
        if (firstStr == null || "".equals(firstStr)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_FIRST + " in " + propFile.getName());
        }
        first = Integer.parseInt(firstStr);

        String lastStr = prop.getProperty(PROPERTY_LAST);
        if (lastStr == null || "".equals(lastStr)) {
            throw new IllegalArgumentException("Missing Parameter " +
                    PROPERTY_LAST + " in " + propFile.getName());
        }
        last = Integer.parseInt(lastStr);

    }

    private void logProperties() {
        System.out.println(">DbDriver: "  + this.dbDriver);
        System.out.println(">DbURL: "     + this.dbURL);
        System.out.println(">Username: "  + this.dbUsername);
        System.out.println(">Password: "  + this.dbPassword);
        System.out.println(">Prefix: "    + this.prefix);
        System.out.println(">InitialId: " + this.initialId);
        System.out.println(">first: "     + this.first);
        System.out.println(">last: "      + this.last);
    }

    // -------------------------------------------------------------------- Main

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            AddUsers addUsers;
            addUsers = new AddUsers();
            addUsers.connect();
            addUsers.insertAll();
            addUsers.closeConnection();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
