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

package com.funambol.server.cleanup;

import junit.framework.TestCase;

import com.funambol.server.config.Configuration;

/**
 * Test cases for ClientLogCleanUpPlugin class.
 *
 * @version $Id: ClientLogCleanUpPluginTest.java 36200 2010-11-16 16:57:29Z filmac $
 */
public class ClientLogCleanUpPluginTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String PLUGIN_COMPLETE =
        "../com/funambol/server/config/ClientLogCleanUp/plugin/ClientLogCleanUpPlugin-complete.xml";
    private static final String PLUGIN_NODIRS =
        "../com/funambol/server/config/ClientLogCleanUp/plugin/ClientLogCleanUpPlugin-nodirs.xml";
    private static final String PLUGIN_ONLYDIRS =
        "../com/funambol/server/config/ClientLogCleanUp/plugin/ClientLogCleanUpPlugin-onlydirs.xml";
    // ------------------------------------------------------------ Constructors
    public ClientLogCleanUpPluginTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    // -------------------------------------------------------------- Test cases

    public void testInitializationPlugin_Complete() throws Exception {

        String pluginBeanFileName = PLUGIN_COMPLETE;

        ClientLogCleanUpPlugin instance = (ClientLogCleanUpPlugin)Configuration
            .getConfiguration()
            .getBeanInstanceByName(pluginBeanFileName);

        assertNotNull(instance);
        assertEquals("Wrong clientsLogBaseDir returned",
                     "clients-log/basedir", instance.getClientsLogBaseDir());
        assertEquals("Wrong clientsLogArchivationDir returned",
                     "clients-log/archivedir",
                     instance.getClientsLogArchivationDir());
        assertEquals("Wrong clientsLogMaxNumberOfArchivedFiles returned",
                     100, instance.getClientsLogMaxNumberOfArchivedFiles());
        assertEquals("Wrong clientsLogNumberOfArchivedFilesToBeDeleted returned",
                     50, instance.getClientsLogNumberOfArchivedFilesToBeDeleted());
        assertEquals("Wrong clientsLogMaxNumberOfFiles returned",
                     200, instance.getClientsLogMaxNumberOfFiles());
        assertEquals("Wrong clientsLogLockExpirationTime returned",
                     10000L, instance.getClientsLogLockExpirationTime());
        assertEquals("Wrong clientsLogTimeToRest returned",
                     2000L, instance.getClientsLogTimeToRest());
    }

    public void testInitializationPlugin_NoDirs() throws Exception {

        String pluginBeanFileName = PLUGIN_NODIRS;

        ClientLogCleanUpPlugin instance = (ClientLogCleanUpPlugin)Configuration
            .getConfiguration()
            .getBeanInstanceByName(pluginBeanFileName);

        assertNotNull(instance);
        assertNull("The clientsLogBaseDir is not null",
                   instance.getClientsLogBaseDir());
        assertNull("The clientsLogArchivationDir is not null",
                   instance.getClientsLogArchivationDir());
        assertEquals("Wrong clientsLogMaxNumberOfArchivedFiles returned",
                     100, instance.getClientsLogMaxNumberOfArchivedFiles());
        assertEquals("Wrong clientsLogNumberOfArchivedFilesToBeDeleted returned",
                     50, instance.getClientsLogNumberOfArchivedFilesToBeDeleted());
        assertEquals("Wrong clientsLogMaxNumberOfFiles returned",
                     200, instance.getClientsLogMaxNumberOfFiles());
        assertEquals("Wrong clientsLogLockExpirationTime returned",
                     10000L, instance.getClientsLogLockExpirationTime());
        assertEquals("Wrong clientsLogTimeToRest returned",
                     2000L, instance.getClientsLogTimeToRest());
    }

    public void testInitializationPlugin_OnlyDirs() throws Exception {

        String pluginBeanFileName = PLUGIN_ONLYDIRS;

        ClientLogCleanUpPlugin instance = (ClientLogCleanUpPlugin)Configuration
            .getConfiguration()
            .getBeanInstanceByName(pluginBeanFileName);

        assertNotNull(instance);
        assertEquals("Wrong clientsLogBaseDir returned",
                     "clients-log/basedir", instance.getClientsLogBaseDir());
        assertEquals("Wrong clientsLogArchivationDir returned",
                     "clients-log/archivedir",
                     instance.getClientsLogArchivationDir());
        assertEquals("Wrong clientsLogMaxNumberOfArchivedFiles returned",
                     30000, instance.getClientsLogMaxNumberOfArchivedFiles());
        assertEquals("Wrong clientsLogNumberOfArchivedFilesToBeDeleted returned",
                     8000, instance.getClientsLogNumberOfArchivedFilesToBeDeleted());
        assertEquals("Wrong clientsLogMaxNumberOfFiles returned",
                     16000, instance.getClientsLogMaxNumberOfFiles());
        assertEquals("Wrong clientsLogLockExpirationTime returned",
                     5*60*1000L, instance.getClientsLogLockExpirationTime());
        assertEquals("Wrong clientsLogTimeToRest returned",
                     1*60*60*1000, instance.getClientsLogTimeToRest());

    }

}
