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

package com.funambol.foundation.engine.source;

import junit.framework.TestCase;

import junitx.util.PrivateAccessor;

import org.dbunit.IDatabaseTester;

import com.funambol.foundation.exception.FileDataObjecySyncSourceConfigException;
import com.funambol.foundation.util.TestDef;

import com.funambol.framework.tools.beans.BeanException;

import com.funambol.tools.database.DBHelper;

/**
 *
 * @version $Id$
 */
public class FileDataObjectSyncSourceConfigTest extends TestCase {

    // --------------------------------------------------------------- Constants

    private static final String SOURCE_URI_PICTURE = "picture";

    // The database has to be initialiazed to retrieve from the fnbl_sync_source
    // the configuration file path
    private static final String INITIAL_DB_DATASET_CORE =
        TestDef.TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/engine/source/FileDataObjectSyncSourceTest/dataset-coredb.xml";

    static {
        try {
            boolean result = DBHelper.initDataSources(TestDef.CORE_SCHEMA_SOURCE, TestDef.USER_SCHEMA_SOURCE, true);
            //assertTrue checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private IDatabaseTester coreDbTester = null;

    // ------------------------------------------------------------ Constructors

    public FileDataObjectSyncSourceConfigTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------- setUp/tearDown methods

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        try {
            coreDbTester = DBHelper.createDatabaseTester(INITIAL_DB_DATASET_CORE, true);
        } catch(Exception ex) {
            freeDatabaseResources();
            throw new Exception("An error occurred while configuring database for the DBTest class.", ex);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // ------------------------------------------------------------ Test methods

    /**
     * Test of getQuotaPerUser method, of class FileDataObjectSyncSource.
     */
    public void testGetQuotaPerUser_DemoRole() throws Exception, Throwable {

        long expResult = 25 * 1024 * 1024; // 25 Mb
        String[] roles = new String[]{"demo"};

        FileDataObjectSyncSourceConfig instance = getInstance();

        long result = (Long) PrivateAccessor.invoke(
            instance,
            "getQuotaPerUser",
            new Class[]{String[].class},
            new Object[]{roles});

        assertEquals(expResult, result);
    }

    public void testGetQuotaPerUser_StandardRole() throws Exception, Throwable {

        long expResult = 50 * 1024 * 1024; // 50 Mb
        String[] roles = new String[]{"standard"};

        FileDataObjectSyncSourceConfig instance = getInstance();

        long result = (Long) PrivateAccessor.invoke(
            instance,
            "getQuotaPerUser",
            new Class[]{String[].class},
            new Object[]{roles});

        assertEquals(expResult, result);
    }

    public void testGetQuotaPerUser_PremiumRole() throws Exception, Throwable {

        long expResult = 250 * 1024 * 1024; // 250 Mb
        String[] roles = new String[]{"demo", "standard", "premium"};

        FileDataObjectSyncSourceConfig instance = getInstance();

        long result = (Long) PrivateAccessor.invoke(
            instance,
            "getQuotaPerUser",
            new Class[]{String[].class},
            new Object[]{roles});

        assertEquals(expResult, result);
    }

    public void testGetQuotaPerUser_PremiumPlusRole() throws Exception, Throwable {

        long expResult = 1024 * 1024 * 1024; // 1 Gb
        String[] roles = new String[]{"demo", "standard", "premium", "premiumplus"};

        FileDataObjectSyncSourceConfig instance = getInstance();

        long result = (Long) PrivateAccessor.invoke(
            instance,
            "getQuotaPerUser",
            new Class[]{String[].class},
            new Object[]{roles});

        assertEquals(expResult, result);
    }

    public void testGetQuotaPerUser_UltimateRole() throws Exception, Throwable {

        long expResult = 2 * 1024 * 1024 * 1024l; // 2 Gb
        String[] roles = new String[]{"ultimate"};

        FileDataObjectSyncSourceConfig instance = getInstance();

        long result = (Long) PrivateAccessor.invoke(
            instance,
            "getQuotaPerUser",
            new Class[]{String[].class},
            new Object[]{roles});

        assertEquals(expResult, result);
    }

    // --------------------------------------------------------- Private Methods

    private FileDataObjectSyncSourceConfig getInstance() throws BeanException, FileDataObjecySyncSourceConfigException {
        return FileDataObjectSyncSource.getConfigInstance(SOURCE_URI_PICTURE);
    }

    private void freeDatabaseResources()  {
        DBHelper.closeConnection(coreDbTester);
    }

}
