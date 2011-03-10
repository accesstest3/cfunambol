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

package com.funambol.framework.server.store;

import junit.framework.*;

import com.funambol.framework.filter.*;

/**
 *
 * @version $Id: SQLHelperClauseTest.java,v 1.2 2008-05-15 05:19:26 nichele Exp $
 */
public class SQLHelperClauseTest extends TestCase {

    private static SQLHelperClause sqlHelperClause = null;

    public SQLHelperClauseTest(String testName) {
        super(testName);
        if (sqlHelperClause == null) {
            sqlHelperClause = new SQLHelperClause("postgres");
        }
    }

    protected void setUp() throws Exception {
    }

    protected void tearDown() throws Exception {
    }

    /**
     * Test of getPreparedWhere method, of class com.funambol.framework.server.store.SQLHelperClause.
     */
    public void testGetPreparedWhereCaseSensitive() {

        Clause clause = new WhereClause("deviceid",
                                        new String[] {"testvalue"},
                                        WhereClause.OPT_EQ,
                                        true);
        PreparedWhere expResult = new PreparedWhere();
        expResult.sql  = "(deviceid = ?)";
        expResult.parameters = new Object[] {"testvalue"};
        PreparedWhere result = sqlHelperClause.getPreparedWhere(clause);
        checkPreparedWhere(expResult, result);
    }


    /**
     * Test of getPreparedWhere method, of class com.funambol.framework.server.store.SQLHelperClause.
     */
    public void testGetPreparedWhereCaseInSensitive() {

        Clause clause = new WhereClause("deviceid",
                                        new String[] {"testvalue"},
                                        WhereClause.OPT_EQ,
                                        false);
        PreparedWhere expResult = new PreparedWhere();
        expResult.sql  = "( UPPER(deviceid) = upper(?))";
        expResult.parameters = new Object[] {"testvalue"};
        PreparedWhere result = sqlHelperClause.getPreparedWhere(clause);
        checkPreparedWhere(expResult, result);
    }

    /**
     * Test of getPreparedWhere method, of class com.funambol.framework.server.store.SQLHelperClause.
     */
    public void testGetPreparedWhereCaseSensitive2() {

        Clause clause1 = new WhereClause("deviceid",
                                        new String[] {"testvalue1"},
                                        WhereClause.OPT_EQ,
                                        true);
        Clause clause2 = new WhereClause("username",
                                        new String[] {"testvalue2"},
                                        WhereClause.OPT_EQ,
                                        false);
        Clause clause3 = new WhereClause("field2",
                                        new String[] {"testvalue3"},
                                        WhereClause.OPT_EQ,
                                        true);

        Clause clause4 = new LogicalClause(LogicalClause.OPT_AND, new Clause[] {clause1, clause2, clause3});

        PreparedWhere expResult = new PreparedWhere();
        expResult.sql  = "((deviceid = ?) and ( UPPER(username) = upper(?)) and (field2 = ?))";
        expResult.parameters = new Object[] {"testvalue1", "testvalue2", "testvalue3"};

        PreparedWhere result = sqlHelperClause.getPreparedWhere(clause4);
        checkPreparedWhere(expResult, result);
    }


    // --------------------------------------------------------- Private methods

    private void checkPreparedWhere(PreparedWhere expResult, PreparedWhere result) {

        assertEquals(expResult.sql, result.sql);

        assertEquals(expResult.parameters.length, result.parameters.length);

        for (int i=0; i<expResult.parameters.length; i++) {
            assertEquals(expResult.parameters[i], result.parameters[i]);
        }

    }
}
