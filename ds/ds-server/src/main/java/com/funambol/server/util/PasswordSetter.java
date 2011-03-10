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

import java.io.IOException;

import java.sql.*;


import javax.sql.DataSource;

import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.db.DataSourceContextHelper;

/**
 *
 * @version $Id: PasswordSetter.java,v 1.3 2008-05-26 08:07:45 nichele Exp $
 */
public class PasswordSetter {

    // --------------------------------------------------------------- Constants

    private static final String DATASOURCE_NAME = "jdbc/fnblcore";

    private static final String SQL_UPDATE_USER = "update fnbl_user set password = ? where username = ?";

    private static final String SQL_PASSWORD_CHECK =
        "select fu.username from fnbl_user fu where fu.username=? and fu.password = ?";

    // -------------------------------------------------------------- Properties

    /**
     * The query used to update the password for a given user. It is configurable
     * so that is an external service wants to use this class, it can setting its
     * query that must be used to update a password. It must be in the form:
     * update TABLE set PASSWORD=? where USER=?
     */
    private String updateUserQuery    = SQL_UPDATE_USER;

    public String getUpdateUserQuery() {
        return updateUserQuery;
    }

    public void setUpdateUserQuery(String updateUserQuery) {
        this.updateUserQuery = updateUserQuery;
    }

    /**
     * The query used to verify the user credentials. It is configurable
     * so that is an external service wants to use this class, it can setting its
     * query that must be used to verify the credentials. It must be in the form:
     * select USER from TABLE where USER=? and PASSWORD=?
     */
    private String passwordCheckQuery = SQL_PASSWORD_CHECK;

    public String getPasswordCheckQuery() {
        return passwordCheckQuery;
    }

    public void setPasswordCheckQuery(String passwordCheckQuery) {
        this.passwordCheckQuery = passwordCheckQuery;
    }

    // ------------------------------------------------------------ Private data

    private DataSource datasource = null;

    // ------------------------------------------------------------- Constructor

    public PasswordSetter() throws Exception {
        System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");
        
        DataSourceContextHelper.configureAndBindDataSourcesForSimpleUsage();

        datasource = DataSourceTools.lookupDataSource(DATASOURCE_NAME);
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Main method
     * @param args the arguments
     */
    public static void main(String[] args)  {


        if (args.length < 1 || args.length > 3) {
            System.out.println("Usage: java com.funambol.server.util.PasswordSetter username <current-password> <new-password>");
            System.exit(1);
        }

        String username        = null;
        String currentPassword = null;
        String newPassword     = null;

        if (args.length >= 1) {
            username = args[0];
        }
        if (args.length >= 2) {
            currentPassword = args[1];
        }
        if (args.length >= 3) {
            newPassword = args[2];
        }

        try {
            PasswordSetter setter = new PasswordSetter();
            setter.changePassword(username, currentPassword, newPassword);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Sets the password of the given user
     * @param username the username
     * @param password the password
     * @return true if the password has been updated, false otherwise
     * @throws java.lang.Exception if an error occurs
     */
    public boolean setPassword(String username, String password) throws Exception {

        Connection con = null;
        PreparedStatement ps1 = null;
        boolean passwordUpdated = false;

        try {
            con = datasource.getConnection();

            //
            // setting password
            //
            ps1 = con.prepareStatement(updateUserQuery);

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

    /**
     * Changes the password of the given user to newPassword verifying that the
     * given currentPassword is valid
     * @param username the username
     * @param currentPassword the current password
     * @param newPassword the new password
     * @throws java.lang.Exception if an error occurs
     */
    public void changePassword(String username, String currentPassword, String newPassword)
    throws Exception {

        System.out.println("Changing password for " + username);
        String retypedNewPassword = null;

        if (currentPassword == null) {

            System.out.print("Current password: ");
            currentPassword  = readPassword();

            if (!verifyCredentials(username, currentPassword)) {
                System.out.println("Invalid password");
                System.exit(1);
            }

        } else {
            if (!verifyCredentials(username, currentPassword)) {
                System.out.println("Invalid password");
                System.exit(1);
            }
        }

        boolean newPasswordOK = true;
        if (newPassword == null) {
            newPasswordOK = false;
            for (int i=0; i<3; i++) {
                System.out.print("New password: ");
                newPassword = readPassword();
                if (!verifyPassword(newPassword)) {
                    continue;
                }
                System.out.print("Retype new password: ");
                retypedNewPassword = readPassword();

                if (newPassword.equals(retypedNewPassword)) {
                    newPasswordOK = true;
                    break;
                } else {
                    System.out.println("Sorry, passwords do not match");
                }
            }
        }

        if (newPasswordOK) {
            setPassword(username, newPassword);
            System.out.println("Password updated successfully");
        }

    }

    /**
     * Checks if the given credentials are valid
     * @param username the username
     * @param password the password
     * @return true if the given credentials are valid, false otherwise
     * @throws java.lang.Exception if an error occurs
     */
    public boolean verifyCredentials(String username, String password)
    throws Exception{

        Connection        con = null;
        PreparedStatement ps  = null;
        ResultSet         rs  = null;
        boolean           ok  = false;
        try {
            //
            // setting password
            //
            con = datasource.getConnection();
            ps = con.prepareStatement(passwordCheckQuery);
            ps.setString(1, username);
            ps.setString(2, EncryptionTool.encrypt(password));

            rs = ps.executeQuery();

            ok = false;
            if (rs.next()) {
                ok = true;
            }

        } catch (Exception e) {
            throw e;
        } finally {
            DBTools.close(con, ps, rs);
        }
        return ok;
    }

    // --------------------------------------------------------- Private methods

    /**
     * Verify if the password is acceptable (only ASCII chars)
     * @param password the password to check
     * @return true if the password is acceptable, false otherwise
     */
    private boolean verifyPassword(String password) {
        if (password == null || password.length() == 0) {
            System.out.println("The password can not be empty");
            return false;
        }
        int len = password.length();
        for (int i=0; i<len; i++) {
            char c = password.charAt(i);
            if (c < 32 || c > 127) {
                System.out.println("The password can contain only printable ascii chars");
                return false;
            }
        }
        return true;
    }

    /**
     * Read a password from System.in
     * @return the password
     * @throws java.io.IOException if an error occurs
     */
    private String readPassword() throws IOException  {
        String password = null;

        password = new jline.ConsoleReader().readLine(new Character(Character.MIN_VALUE));

        return password;
    }

}
