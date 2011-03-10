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

package com.funambol.pushlistener.service;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;

import javax.sql.DataSource;


/**
 * Provides useful method to access to the jdbc/fnblcore datasource.
 *
 * @version $Id: DBConnectionProvider.java,v 1.9 2008-05-18 16:23:42 nichele Exp $
 */
public class DBConnectionProvider {

    // ------------------------------------------------------------ Private data

    /**
     * The jdbc/fnblcore datasource
     */
    private static DataSource basicDataSource = null;

    static {

        try {
            InitialContext context = new InitialContext();
            basicDataSource = (DataSource)context.lookup("jdbc/fnblcore");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // ------------------------------------------------------------ Constructors

    // ---------------------------------------------------------- Public methods

    /**
     * Returns an available connection
     * @throws java.sql.SQLException if an error occurs
     * @return an available connection
     */
    public static Connection getConnection() throws SQLException {
        return basicDataSource.getConnection();
    }

    // ------------------------------------------------------- Protected methods
    // --------------------------------------------------------- Private methods
}
