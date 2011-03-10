/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2010 Funambol, Inc.
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
package com.funambol.server.store;

import junit.framework.TestCase;

import org.dbunit.IDatabaseTester;

import com.funambol.framework.filter.Clause;
import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.server.Sync4jSource;
import com.funambol.framework.server.store.PersistentStore;
import com.funambol.server.config.Configuration;

import com.funambol.tools.database.DBHelper;

/**
 * Test cases for the SyncSourcePersistentStore class.
 *
 * @version $Id: SyncSourcePersistentStoreTest.java 34393 2010-05-04 16:45:30Z luigiafassina $
 */
public class SyncSourcePersistentStoreTest extends TestCase {

    // --------------------------------------------------------------- Constants
    public static final String CREATE_DB_CORE_SCHEMA =
        "src/test/sql/create-core-schema.sql";
    public static final String CREATE_DB_USER_SCHEMA =
        "src/test/sql/create-user-schema.sql";
    public static final String SYNC_SOURCE_TABLE = "fnbl_sync_source";

    public static final String INITIAL_DB_CORE_DATASET =
        "src/test/data/com/funambol/server/store/SyncSourcePersistentStore/initial-db-core-dataset.xml";


    // ------------------------------------------------------------ Private data
    private PersistentStore persistentStore;
    private IDatabaseTester dbTester;

    static {
        DBHelper.initDataSources(CREATE_DB_CORE_SCHEMA, CREATE_DB_USER_SCHEMA);
    }

    // ------------------------------------------------------------ Constructors
    public SyncSourcePersistentStoreTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        try {
            dbTester =
                DBHelper.createDatabaseTester(INITIAL_DB_CORE_DATASET, true);

            persistentStore = Configuration.getConfiguration().getStore();

        } catch(Throwable e) {
            throw new Exception("An error occurred while configuring " +
                "database for the SyncSourcePersistentStoreTest class.", e);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        DBHelper.closeConnection(dbTester);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of read method, of class SyncSourcePersistentStore.
     * @throws Exception
     */
    public void testRead_Class() throws Exception {

        Object[] expected = new Object[5];
        expected[0] = new Sync4jSource(
            "cal",
            "foundation/foundation/calendar-foundation/VCalendarSource.xml",
            null,
            null);
        expected[1] = new Sync4jSource(
            "card",
            "foundation/foundation/contact-foundation/VCardSource.xml",
            null,
            null);
        expected[2] = new Sync4jSource(
            "note",
            "foundation/foundation/calendar-foundation/PlainTextNoteSource.xml",
            null,
            null);
        expected[3] = new Sync4jSource(
            "scal",
            "foundation/foundation/calendar-foundation/SIFEventSource.xml",
            null,
            null);
        expected[4] = new Sync4jSource(
            "snote",
            "foundation/foundation/calendar-foundation/SIFNoteSource.xml",
            null,
            null);

        Object[] result = persistentStore.read(Sync4jSource.class);
        assertTrue(result.length == 5);

        for (int i = 0; i < result.length; i++) {
            compareSync4jSource(expected[i], result[i]);
        }
    }

    /**
     * Test of read method, of class SyncSourcePersistentStore.
     * @throws Exception 
     */
    public void testRead_Object_Clause() throws Exception {
        Sync4jSource object = new Sync4jSource();

        Sync4jSource expSyncSource = new Sync4jSource(
            "cal",
            "foundation/foundation/calendar-foundation/VCalendarSource.xml",
            "calendar-foundation",
            "cal");
        Clause clause = new WhereClause("name",
                                        new String[] {"cal"},
                                        WhereClause.OPT_EQ,
                                        true);

        Object[] result = persistentStore.read(object, clause);
        assertTrue(result.length == 1);
        compareSync4jSource(expSyncSource, result[0]);

        Object[] expSyncSources = new Object[2];
        expSyncSources[0] = new Sync4jSource(
            "cal",
            "foundation/foundation/calendar-foundation/VCalendarSource.xml",
            "calendar-foundation",
            "cal");
        expSyncSources[1] = new Sync4jSource(
            "scal",
            "foundation/foundation/calendar-foundation/SIFEventSource.xml",
            "calendar-foundation",
            "scal");
        clause = new WhereClause("name",
                                 new String[] {"cal"},
                                 WhereClause.OPT_CONTAINS,
                                 true);

        result = persistentStore.read(object, clause);
        assertTrue(result.length == 2);
        for (int i = 0; i < result.length; i++) {
            compareSync4jSource(expSyncSources[i], result[i]);
        }

        clause = new WhereClause("name",
                                 new String[] {"notexist"},
                                 WhereClause.OPT_EQ,
                                 true);
        result = persistentStore.read(object, clause);
        assertTrue(result.length == 0);

        expSyncSources = new Object[2];
        expSyncSources[0] = new Sync4jSource(
            "note",
            "foundation/foundation/calendar-foundation/PlainTextNoteSource.xml",
            "note-foundation",
            "note");
        expSyncSources[1] = new Sync4jSource(
            "snote",
            "foundation/foundation/calendar-foundation/SIFNoteSource.xml",
            "note-foundation",
            "snote");

        Clause[] clauses = new Clause[2];
        clauses[0] = new WhereClause("uri",
                                     new String[] {"note"},
                                     WhereClause.OPT_EQ,
                                     true);
        clauses[1] = new WhereClause("name",
                                     new String[] {"snote"},
                                     WhereClause.OPT_EQ,
                                     true);

        LogicalClause logicalClause =
            new LogicalClause(LogicalClause.OPT_OR, clauses);

        result = persistentStore.read(object, logicalClause);
        assertTrue(result.length == 2);
        for (int i = 0; i < result.length; i++) {
            compareSync4jSource(expSyncSources[i], result[i]);
        }

        expSyncSource = new Sync4jSource(
            "scal",
            "foundation/foundation/calendar-foundation/SIFEventSource.xml",
            "calendar-foundation",
            "scal");
        clauses = new Clause[2];
        clauses[0] = new WhereClause("uri",
                                     new String[] {"scal"},
                                     WhereClause.OPT_EQ,
                                     true);
        clauses[1] = new WhereClause("name",
                                     new String[] {"scal"},
                                     WhereClause.OPT_EQ,
                                     true);
        logicalClause = new LogicalClause(LogicalClause.OPT_OR, clauses);

        result = persistentStore.read(object, logicalClause);
        assertTrue(result.length == 1);
        compareSync4jSource(expSyncSource, result[0]);
        
    }

    // --------------------------------------------------------- Private methods
    private void compareSync4jSource(Object expected, Object result) {

        if (!(result instanceof Sync4jSource)) {
            fail("The result is not a Sync4jSource object.");
        }

        assertEquals("Wrong source uri",
                     ((Sync4jSource)expected).getUri(),
                     ((Sync4jSource)result).getUri());

        assertEquals("Wrong source name",
                     ((Sync4jSource)expected).getSourceName(),
                     ((Sync4jSource)result).getSourceName());

        assertEquals("Wrong source config",
                     ((Sync4jSource)expected).getConfig(),
                     ((Sync4jSource)result).getConfig());

        assertEquals("Wrong source type id",
                     ((Sync4jSource)expected).getSourceTypeId(),
                     ((Sync4jSource)result).getSourceTypeId());
    }

}
