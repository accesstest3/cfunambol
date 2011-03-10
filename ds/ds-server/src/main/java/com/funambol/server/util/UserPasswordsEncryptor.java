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
package com.funambol.server.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.sql.*;

import javax.sql.DataSource;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.db.DataSourceContextHelper;


/**
 * This class is used to encrypt user passwords using Funambol encryption algorithm
 *
 * @version $Id: UserPasswordsEncryptor.java,v 1.3 2008-05-26 08:07:45 nichele Exp $
 */
public class UserPasswordsEncryptor {

    // --------------------------------------------------------------- Constants
    private static final String DATASOURCE_NAME = "jdbc/fnblcore";

    private static final String SQL_READ_USERS  = "select username, password from fnbl_user";

    private static final String SQL_UPDATE_USER = "update fnbl_user set password = ? where username = ?";

    // ------------------------------------------------------------ Private data
    private DataSource datasource = null;

    // ------------------------------------------------------------- Constructor

    public UserPasswordsEncryptor() throws Exception {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        
        DataSourceContextHelper.configureAndBindDataSourcesForSimpleUsage();

        datasource = DataSourceTools.lookupDataSource(DATASOURCE_NAME);
    }
    // ---------------------------------------------------------- Public Methods

    /**
     * Main methods
     * @param args the arguments
     * @throws java.lang.Exception if an error occurs
     */
    public static void main(String[] args)  throws Exception {

        String message = "Are you sure you want to encrypt all user passwords ?";
        boolean encrypt = readBoolean(message);
        if (!encrypt) {
            System.exit(0);
        }

        UserPasswordsEncryptor encr = new UserPasswordsEncryptor();
        encr.encryptUserPasswords();
    }


    // --------------------------------------------------------- Private Methods

    /**
     * Encrypts the user passwords stored in the database using EncryptionTool.
     * In order to access to the db, the given properties file is read
     * @throws java.lang.Exception if an error occurs
     */
    private void encryptUserPasswords() throws Exception {

        String username = null ;
        String pwdOld   = null ;

        Connection con = datasource.getConnection();

        PreparedStatement ps1 = null ;
        ResultSet         rs1 = null ;

        try {
            //
            // reading usernames and passwords
            //
            ps1 = con.prepareStatement(SQL_READ_USERS);

            rs1 = ps1.executeQuery();

            while(rs1.next()) {

                username = rs1.getString(1);
                pwdOld   = rs1.getString(2);

                try {
                    String decrypted = EncryptionTool.decrypt(pwdOld);
                    String message = "The password for user '" + username + "' seems already encrypted. " +
                                     "Are you sure you want to encrypt it ?";
                    boolean forceEncryption = readBoolean(message);
                    if (!forceEncryption) {
                        System.out.println("User '" + username + "' not updated");
                        continue;
                    }
                } catch (Throwable t) {
                    //
                    // ignoring...this means the password is not encrypted
                    //
                }

                //
                // replacing password by the encrypted version
                //
                boolean passwordUpdated = setPassword(username, pwdOld);

                if (passwordUpdated) {
                    System.out.println("User '" + username + "' updated successfully");
                } else {
                    System.out.println("User '" + username + "' not found");
                }

            }

            // DBTools.close(null, null, rs1);

        } catch (Exception e) {
            throw e;
        } finally {
            DBTools.close(con, ps1, rs1);
        }
    }

    /**
     * Reads a boolean value showing the given message. It accepts y (true) or
     * n (false)
     * @param message the message to show
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    private static boolean readBoolean(String message) throws Exception {

        message = message + " [y/n]: ";

        System.out.print(message);
        String value = null;

        while (true) {
            value = readLine();

            if ("Y".equalsIgnoreCase(value)){
                return true;
            } else if ("N".equalsIgnoreCase(value)) {
                return false;
            }
            System.out.println("Value not valid");
            System.out.print(message);
        }
    }

   /**
     * Read a String from System.in
     * @throws java.lang.Exception if an error occurs
     * @return the inserted value
     */
    private static String readLine() throws Exception {

        InputStreamReader iReader = new InputStreamReader(System.in);
        BufferedReader    indata  = new BufferedReader(iReader);
        String            value   = null;

        try {
            value = indata.readLine();
        } catch (Exception ex) {
            throw new Exception("Error reading input value (" + ex.getMessage() +
                                ")");
        }

        return value;
    }

    /**
     * Sets the password of the given user
     * @param username the username
     * @param password the password
     * @return true if the password has been updated, false otherwise
     * @throws java.lang.Exception if an error occurs
     */
    private boolean setPassword(String username, String password) throws Exception {

        Connection con = null;
        PreparedStatement ps1 = null;
        boolean passwordUpdated = false;

        try {
            //
            // setting password
            //
            con = datasource.getConnection();

            ps1 = con.prepareStatement(SQL_UPDATE_USER);

            ps1.setString(1, EncryptionTool.encrypt(password));
            ps1.setString(2, username);

            int i = ps1.executeUpdate();

            if (i > 0) {
                passwordUpdated = true;
            } else {
                passwordUpdated = false;
            }
            DBTools.close(null, ps1, null);


        } catch (Exception e) {
            throw e;
        } finally {
            DBTools.close(con, ps1, null);
        }

        return passwordUpdated;
    }
}