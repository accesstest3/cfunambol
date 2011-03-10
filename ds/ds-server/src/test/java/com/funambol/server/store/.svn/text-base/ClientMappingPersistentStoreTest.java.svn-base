/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import com.funambol.framework.security.Sync4jPrincipal;
import com.funambol.framework.server.ClientMapping;
import com.funambol.framework.server.ClientMappingEntry;
import com.funambol.framework.server.Sync4jDevice;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.DBTools;
import com.funambol.framework.tools.DataSourceTools;

import com.funambol.server.config.Configuration;
import com.funambol.server.db.DataSourceContextHelper;
import com.funambol.server.db.RoutingDataSource;

/**
 * ClientMappingPersistentStore's test cases and in particular about partitioning.
 * It works with 3 partitions and with 3 users (1 user per partition).
 *
 * @version $Id: ClientMappingPersistentStoreTest.java,v 1.3 2008-05-24 10:04:20 nichele Exp $
 */
public class ClientMappingPersistentStoreTest extends TestCase {

    private static final String DROP_FNBL_CLIENT_MAPPING = "drop table fnbl_client_mapping if exists";

    private static final String CREATE_FNBL_CLIENT_MAPPING =
        "create table fnbl_client_mapping (" +
        "principal   bigint       not null," +
        "sync_source varchar(128) not null," +
        "luid        varchar(200) not null," +
        "guid        varchar(200) not null," +
        "last_anchor varchar(20)  ," +
        "constraint pk_clientmapping primary key (principal, sync_source, luid, guid));";

    private static final String VERIFY_MAPPING =
        "select * from fnbl_client_mapping where principal=? and guid=? and luid=? and last_anchor=? and sync_source=?";

    private static final String JNDI_USER_DB = "jdbc/fnbluser";

    private static final String USER_PART1 = "user_part1";
    private static final String USER_PART2 = "user_part2";
    private static final String USER_PART3 = "user_part3";


    static {
        try {
            System.setProperty("java.naming.factory.initial", "org.apache.naming.java.javaURLContextFactory");

            DataSourceContextHelper.configureAndBindDataSources();

            createMappingTable(USER_PART1);
            createMappingTable(USER_PART2);
            createMappingTable(USER_PART3);

            verifyDataBasePartitioning();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Constructors
    public ClientMappingPersistentStoreTest(String testName) {
        super(testName);
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of delete method, of class ClientMappingPersistentStore.
     */
    public void testDelete_USER_PART_1() throws Exception {

        Sync4jUser user =new Sync4jUser();
        user.setUsername(USER_PART1);

        Sync4jDevice device = new Sync4jDevice("device1");

        Sync4jPrincipal principal = new Sync4jPrincipal(100, user, device);

        ClientMapping mapping = new ClientMapping(principal, "uri-test-1");

        mapping.updateMapping("guid-user1-0", "luid-0", "0");
        mapping.updateMapping("guid-user1-1", "luid-1", "1");
        mapping.updateMapping("guid-user1-2", "luid-2", "2");
        mapping.updateMapping("guid-user1-3", "luid-3", "3");

        Configuration.getConfiguration().getStore().store(mapping);

        // just to be sure the store works as expected
        verifyMapping(USER_PART1, mapping);

        // same principal, other source
        ClientMapping mapping2 = new ClientMapping(principal, "uri-test-2");

        mapping2.updateMapping("guid-user1-10", "luid-10", "10");
        mapping2.updateMapping("guid-user1-11", "luid-11", "11");
        mapping2.updateMapping("guid-user1-12", "luid-12", "12");
        mapping2.updateMapping("guid-user1-13", "luid-13", "13");

        Configuration.getConfiguration().getStore().store(mapping2);

        // just to be sure the store works as expected
        verifyMapping(USER_PART1, mapping2);

        ClientMapping deletedMapping = new ClientMapping(principal, "uri-test-1");
        boolean deleted = Configuration.getConfiguration().getStore().delete(deletedMapping);
        assertTrue(deleted);

        verifyNoMapping(USER_PART1, principal, "uri-test-1");

        // mapping2 must be still there !
        verifyMapping(USER_PART1, mapping2);

        ClientMapping readMapping = new ClientMapping(principal, "uri-test-1");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("No mapping expected",
                     deletedMapping.getMapping(), readMapping.getMapping());

    }

    /**
     * Test of store method, of class ClientMappingPersistentStore.
     */
    public void testStoreReadStoreRead_USER_PART_1() throws Exception {

        Sync4jUser user =new Sync4jUser();
        user.setUsername(USER_PART1);

        Sync4jDevice device = new Sync4jDevice("device1");

        Sync4jPrincipal principal = new Sync4jPrincipal(100, user, device);

        ClientMapping mapping = new ClientMapping(principal, "uri-test-1");

        mapping.updateMapping("guid-user1-0", "luid-0", "0");
        mapping.updateMapping("guid-user1-1", "luid-1", "1");
        mapping.updateMapping("guid-user1-2", "luid-2", "2");
        mapping.updateMapping("guid-user1-3", "luid-3", "3");
        mapping.updateMapping("guid-user1-4", "luid-4", "4");
        mapping.updateMapping("guid-user1-5", "luid-5", "5");
        mapping.updateMapping("guid-user1-6", "luid-6", "6");
        mapping.updateMapping("guid-user1-7", "luid-7", "7");
        mapping.updateMapping("guid-user1-8", "luid-8", "8");
        mapping.updateMapping("guid-user1-9", "luid-9", "9");

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART1, mapping);

        ClientMapping readMapping = new ClientMapping(principal, "uri-test-1");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

        mapping.resetModifiedKeys();
        //
        // Updating the mapping in order to verify the update
        //
        mapping.updateMapping("guid-user1-0", "luid-0", "up-0");
        mapping.updateMapping("guid-user1-1", "luid-1", "up-1");
        mapping.updateMapping("guid-user1-2", "luid-2", "up-2");
        mapping.updateMapping("guid-user1-3", "luid-3", "up-3");
        mapping.updateMapping("guid-user1-4", "luid-4", "up-4");
        mapping.removeMappedValuesForGuid("guid-user1-7", false);
        mapping.removeMappedValuesForGuid("guid-user1-8", false);
        mapping.removeMappedValuesForGuid("guid-user1-9", false);

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART1, mapping);

        readMapping = new ClientMapping(principal, "uri-test-1");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

    }

    /**
     * Test of store method, of class ClientMappingPersistentStore.
     */
    public void testStoreReadStoreRead_USER_PART_2() throws Exception {

        Sync4jUser user =new Sync4jUser();
        user.setUsername(USER_PART2);

        Sync4jDevice device = new Sync4jDevice("device2");

        Sync4jPrincipal principal = new Sync4jPrincipal(100, user, device);

        ClientMapping mapping = new ClientMapping(principal, "uri-test-2");
        mapping.updateMapping("guid-user2-0", "luid-0", "0");
        mapping.updateMapping("guid-user2-1", "luid-1", "1");
        mapping.updateMapping("guid-user2-2", "luid-2", "2");
        mapping.updateMapping("guid-user2-3", "luid-3", "3");
        mapping.updateMapping("guid-user2-4", "luid-4", "4");
        mapping.updateMapping("guid-user2-5", "luid-5", "5");
        mapping.updateMapping("guid-user2-6", "luid-6", "6");
        mapping.updateMapping("guid-user2-7", "luid-7", "7");
        mapping.updateMapping("guid-user2-8", "luid-8", "8");
        mapping.updateMapping("guid-user2-9", "luid-9", "9");

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART2, mapping);

        ClientMapping readMapping = new ClientMapping(principal, "uri-test-2");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

        mapping.resetModifiedKeys();
        //
        // Updating the mapping in order to verify the update
        //
        mapping.updateMapping("guid-user2-0", "luid-0", "up-0");
        mapping.updateMapping("guid-user2-1", "luid-1", "up-1");
        mapping.updateMapping("guid-user2-2", "luid-2", "up-2");
        mapping.removeMappedValuesForGuid("guid-user2-3", false);
        mapping.removeMappedValuesForGuid("guid-user2-4", false);
        mapping.removeMappedValuesForGuid("guid-user2-5", false);

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART2, mapping);

        readMapping = new ClientMapping(principal, "uri-test-2");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

    }

    /**
     * Test of store method, of class ClientMappingPersistentStore.
     */
    public void testStoreReadStoreRead_USER_PART_3() throws Exception {

        Sync4jUser user =new Sync4jUser();
        user.setUsername(USER_PART3);

        Sync4jDevice device = new Sync4jDevice("device3");

        Sync4jPrincipal principal = new Sync4jPrincipal(100, user, device);

        ClientMapping mapping = new ClientMapping(principal, "uri-test-3");
        mapping.updateMapping("guid-user3-0", "luid-0", "0");
        mapping.updateMapping("guid-user3-1", "luid-1", "1");
        mapping.updateMapping("guid-user3-2", "luid-2", "2");
        mapping.updateMapping("guid-user3-3", "luid-3", "3");
        mapping.updateMapping("guid-user3-4", "luid-4", "4");
        mapping.updateMapping("guid-user3-5", "luid-5", "5");
        mapping.updateMapping("guid-user3-6", "luid-6", "6");
        mapping.updateMapping("guid-user3-7", "luid-7", "7");
        mapping.updateMapping("guid-user3-8", "luid-8", "8");
        mapping.updateMapping("guid-user3-9", "luid-9", "9");

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART3, mapping);

        ClientMapping readMapping = new ClientMapping(principal, "uri-test-3");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

        mapping.resetModifiedKeys();
        //
        // Updating the mapping in order to verify the update
        //
        mapping.removeMappedValuesForGuid("guid-user3-0", false);
        mapping.removeMappedValuesForGuid("guid-user3-1", false);
        mapping.updateMapping("guid-user3-2", "luid-2", "up-2");
        mapping.updateMapping("guid-user3-3", "luid-3", "up-3");
        mapping.updateMapping("guid-user3-4", "luid-4", "up-4");
        mapping.updateMapping("guid-user3-5", "luid-5", "up-5");
        mapping.updateMapping("guid-user3-6", "luid-6", "up-6");

        Configuration.getConfiguration().getStore().store(mapping);

        verifyMapping(USER_PART3, mapping);

        readMapping = new ClientMapping(principal, "uri-test-3");
        Configuration.getConfiguration().getStore().read(readMapping);

        assertEquals("The read mapping is different from the stored one",
                     mapping.getMapping(), readMapping.getMapping());

    }


    // --------------------------------------------------------- Private methods

    private static void createMappingTable(String userId) throws Exception {
        //
        // fnbl_client_mapping is in the userdb
        //
        RoutingDataSource ds = (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = ds.getRoutedConnection(userId);

            stmt = conn.prepareStatement(DROP_FNBL_CLIENT_MAPPING);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

        try {
            conn = ds.getRoutedConnection(userId);
            stmt = conn.prepareStatement(CREATE_FNBL_CLIENT_MAPPING);
            stmt.execute();
            DBTools.close(null, stmt, null);
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            DBTools.close(conn, stmt, null);
        }

    }

    /**
     * Verifies that there are three partitions and that they contain the mapping table
     */
    private static void verifyDataBasePartitioning() throws Exception {

        RoutingDataSource ds = (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);
        assertTrue("The configured partitioning criteria is not a MockPartitioningCriteria",
                   ds.getPartitioningCriteria() instanceof MockPartitioningCriteria);

        Map<String, DataSource> dataSources =
                (Map<String, DataSource>) PrivateAccessor.getField(ds, "dataSources");

        assertEquals("The routing datasource is not configured with 3 datasources ["
                     + dataSources.size() + "]", 3, dataSources.size());

        Iterator<String> itDataSources = dataSources.keySet().iterator();
        while (itDataSources.hasNext()) {
            String dsName = itDataSources.next();
            DataSource dataSource = dataSources.get(dsName);
            boolean inError = false;
            Connection con = null;
            PreparedStatement pStmt = null;

            try {
                con = dataSource.getConnection();

                pStmt = con.prepareStatement("select * from fnbl_client_mapping");
                pStmt.execute();
            } catch (Exception e) {
                //e.printStackTrace();
                inError = true;
            } finally {
                DBTools.close(con, pStmt, null);
            }

            assertFalse("Partition " + dsName +
                        " doesn't contain the fnbl_client_mapping table", inError);
        }
    }

    /**
     * Verifies that in the expected partition for the given user there is not any
     * mapping. The partition is identified using the partitioning criteria of
     * the routiong datasource
     */
    private void verifyNoMapping(String user, Sync4jPrincipal principal, String uri) throws Exception {

        RoutingDataSource ds = (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);
        String targetPartition = ds.getPartitioningCriteria().getPartition(user).getName();

        Map<String, DataSource> dataSources =
                (Map<String, DataSource>) PrivateAccessor.getField(ds, "dataSources");

        Iterator itDataSources = dataSources.keySet().iterator();
        while (itDataSources.hasNext()) {
            String partitionName  = (String)itDataSources.next();
            DataSource dataSource = (DataSource)dataSources.get(partitionName);

            boolean mappingNotFound = checkNoMapping(dataSource, principal, uri);

            if (partitionName.equals(targetPartition)) {
                assertTrue("Mapping for the user " + user + " found in partition "
                           + partitionName + " and it was not expected", mappingNotFound);
            } 
        }
    }

    /**
     * Verifies that in the expected partition for the given user there is the 
     * mapping. The partition is identified using the partitioning criteria of
     * the routiong datasource
     */
    private void verifyMapping(String user, ClientMapping mapping) throws Exception {

        RoutingDataSource ds = (RoutingDataSource)DataSourceTools.lookupDataSource(JNDI_USER_DB);
        String targetPartition = ds.getPartitioningCriteria().getPartition(user).getName();

        Map<String, DataSource> dataSources =
                (Map<String, DataSource>) PrivateAccessor.getField(ds, "dataSources");

        Iterator itDataSources = dataSources.keySet().iterator();
        while (itDataSources.hasNext()) {
            String partitionName  = (String)itDataSources.next();
            DataSource dataSource = (DataSource)dataSources.get(partitionName);
            
            // System.out.println("Checking partition: " + partitionName + " for user: " + user);
            boolean mappingOk = checkMapping(partitionName, dataSource, mapping);

            if (partitionName.equals(targetPartition)) {
                assertTrue("Mapping for the user " + user + " not found (or it is wrong) in partition "
                           + partitionName + " as expected", mappingOk);
            } else {
                assertFalse("Not expected mapping found in partition " + partitionName +
                            " for the user " + user, mappingOk);
            }
        }
    }

    /**
     * Verifies that in the partition identified by the given DataSource contain
     * the given mapping for the given user.
     */
    private boolean checkMapping(String partitionName, DataSource ds, ClientMapping mapping) throws SQLException {

        long principalId = mapping.getPrincipal().getId();

        String uri = mapping.getDbURI();

        List mappingEntries = mapping.getMapping();
        for (int i = 0; i<mappingEntries.size(); i++) {
            ClientMappingEntry entry = (ClientMappingEntry)mappingEntries.get(i);
            String guid = entry.getGuid();
            String luid = entry.getLuid();
            String lastAnchor = entry.getLastAnchor();

            Connection con = ds.getConnection();
            PreparedStatement pStmt = null;
            ResultSet         rs    = null;
            try {
                /*
                System.out.println("Verifying mapping in " + partitionName + " for principal " + principalId + 
                                   ". GUID: " + guid + 
                                   ", LUID: " + luid + 
                                   ", last anchor: " + lastAnchor + 
                                   ", uri: " + uri);
                */
                pStmt = con.prepareStatement(VERIFY_MAPPING);
                pStmt.setLong(1, principalId);
                pStmt.setString(2, guid);
                pStmt.setString(3, luid);
                pStmt.setString(4, lastAnchor);
                pStmt.setString(5, uri);

                rs = pStmt.executeQuery();

                boolean mappingFound = false;
                while (rs.next()) {
                    //System.out.println("FOUND !");
                    mappingFound = true;
                    continue;
                }
                DBTools.close(con, pStmt, rs);
                //
                // An entry has not been found
                //
                if (!mappingFound) {
                    //System.out.println("NOT FOUND !");
                    return false;
                }

            } catch (Exception e) {
                fail("Error reading mapping (" + e + ")");
            } finally {
                DBTools.close(con, pStmt, rs);
            }
        }

        return true;
    }

    /**
     * Verifies that in the partition identified by the given DataSource doesn't contain
     * any mapping for the given user.
     * @return true if no mapping is found, false otherwise
     */
    private boolean checkNoMapping(DataSource ds, Sync4jPrincipal principal, String uri) throws SQLException {

        long principalId = principal.getId();
        
        Connection con = ds.getConnection();
        PreparedStatement pStmt = null;
        ResultSet rs = null;

        boolean mappingFound = false;

        try {
            pStmt = con.prepareStatement("select * from fnbl_client_mapping where principal=? and sync_source=?");
            pStmt.setLong(1, principalId);
            pStmt.setString(2, uri);

            rs = pStmt.executeQuery();

            while (rs.next()) {
                mappingFound = true;
            }

        } catch (Exception e) {
            fail("Error reading mapping (" + e + ")");
        } finally {
            DBTools.close(con, pStmt, rs);
        }
        return !mappingFound;
    }

}
