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

import java.util.Map;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.dbunit.IDatabaseTester;

import com.funambol.common.media.file.FileDataObject;
import com.funambol.common.media.file.FileDataObjectBody;
import com.funambol.common.media.file.FileDataObjectMetadata;
import com.funambol.common.media.file.UploadStatus;

import com.funambol.foundation.items.model.FileDataObjectWrapper;
import com.funambol.foundation.util.TestDef;

import com.funambol.framework.engine.source.PartialContentException;
import com.funambol.framework.tools.beans.BeanException;

import com.funambol.server.config.Configuration;

import com.funambol.tools.database.DBHelper;

/**
 * Test cases for FileDataObjectSyncSource class.
 *
 * @version $Id$
 */
public class FileDataObjectSyncSourceTest extends TestCase implements TestDef {

    // --------------------------------------------------------------- Constants

    private static final String CONFIG_FILE_PATH = "foundation/foundation/fdo-foundation/PictureSource.xml";

    private static final String INITIAL_DB_DATASET_CORE =
        TEST_RESOURCE_BASEDIR + "/data/com/funambol/foundation/engine/source/FileDataObjectSyncSourceTest/dataset-coredb.xml";

    private static final String SOURCE_URI_PICTURE = "picture";

    static {
        try {
            boolean result = DBHelper.initDataSources(CORE_SCHEMA_SOURCE, USER_SCHEMA_SOURCE, true);

            // checks if the inizialization of database terminated correctly
            assertTrue("Error initializing the database", result);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Private data
    private IDatabaseTester coreDbTester = null;

    // ------------------------------------------------------------ Constructors

    public FileDataObjectSyncSourceTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods

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
        freeDatabaseResources();
    }

    // -------------------------------------------------------------- Test cases

    /**
     * Test of getMaxSize method, of class FileDataObjectSyncSource.
     */
    public void testGetMaxSize() throws Exception {
        long expResult = 50 * 1024 * 1024; // 50 Mb

        FileDataObjectSyncSource instance = getInstance();
        long result = instance.getMaxSizeAsLong();
        assertEquals(expResult, result);
    }

    /**
     * Test of getFileSysDirDepth method, of class FileDataObjectSyncSource.
     */
    public void testGetFileSysDirDepth() throws Exception {
        int expResult = 8;

        FileDataObjectSyncSource instance = getInstance();
        int result = instance.getFileSysDirDepth();
        assertEquals(expResult, result);
    }

    /**
     * Test if the datasource config file name for picture datasource is read correctly
     */
    public void testConfigurationFileName() throws Throwable {
        //the setup method already put data inside the table
        //fnbl_sync_source

        //retrieve the config name of the datasource
        String configFilePath = (String) PrivateAccessor.invoke(
                FileDataObjectSyncSource.class,
                "getSyncSourceFilePath",
                new Class[] { String.class },
                new String[] { SOURCE_URI_PICTURE });

        assertEquals(("Configuration file path mismatch"),
                CONFIG_FILE_PATH,
                configFilePath);
    }

    /**
     * Test if the configuration values for the PictureSource.xml are correctly
     * read from files
     */
    public void testPictureSourceConfigurationValues() throws Exception {

        FileDataObjectSyncSource instance = getInstance();

        assertEquals("wrong name", instance.getName(), "picture");
        assertEquals("wrong name", instance.getSourceURI(), SOURCE_URI_PICTURE);
        assertEquals("Wrong localRootPath", instance.getLocalRootPath(), "../../../ds-server/db/picture/fdo-container");
        assertEquals("Wrong jcloudsRootPath", instance.getStorageFilesystemRootPath(), "../../../ds-server/db/picture");
        assertEquals("Wrong jcloudsProvider", instance.getStorageProvider(), "filesystem");
        assertEquals("Wrong jcloudsContainerName", instance.getStorageContainerName(), "fdo-container");
        assertEquals("Wrong jcloudsIdentity", instance.getStorageIdentity(), "s3-accesskey");
        assertEquals("Wrong jcloudsCredential", instance.getStorageCredential(), "s3-secretkey");
        assertEquals("wrong maxSize", instance.getMaxSize(), "50M");
        Map<String, String> roleQuotas = instance.getRoleQuota();
        assertEquals("wrong role for demo", roleQuotas.get("demo"), "25M");
        assertEquals("wrong role for standard", roleQuotas.get("standard"), "50M");
        assertEquals("wrong role for premium", roleQuotas.get("premium"), "250M");
        assertEquals("wrong role for premiumplus", roleQuotas.get("premiumplus"), "1G");
        assertEquals("wrong role for ultimate", roleQuotas.get("ultimate"), "2G");
    }

    /**
     * Test if the configuration object for the PictureSource.xml sync source is correctly
     * created
     */
    public void testPictureSourceConfigurationObject() throws Exception {

        FileDataObjectSyncSourceConfig config =
            FileDataObjectSyncSource.getConfigInstance(SOURCE_URI_PICTURE);

        assertEquals("Wrong localRootPath", config.getLocalRootPath(), "../../../ds-server/db/picture/fdo-container");
        assertEquals("Wrong jcloudsRootPath", config.getStorageFilesystemRootPath(), "../../../ds-server/db/picture");
        assertEquals("Wrong jcloudsProvider", config.getStorageProvider(), "filesystem");
        assertEquals("Wrong jcloudsContainerName", config.getStorageContainerName(), "fdo-container");
        assertEquals("Wrong jcloudsIdentity", config.getStorageIdentity(), "s3-accesskey");
        assertEquals("Wrong jcloudsCredential", config.getStorageCredential(), "s3-secretkey");
        assertEquals("wrong maxSize", config.getMaxSize(), "50M");
        Map<String, String> roleQuotas = config.getRoleQuota();
        assertEquals("wrong role for demo", roleQuotas.get("demo"), "25M");
        assertEquals("wrong role for standard", roleQuotas.get("standard"), "50M");
        assertEquals("wrong role for premium", roleQuotas.get("premium"), "250M");
        assertEquals("wrong role for premiumplus", roleQuotas.get("premiumplus"), "1G");
        assertEquals("wrong role for ultimate", roleQuotas.get("ultimate"), "2G");
    }


    public void testCheckResumable() throws Throwable {
        FileDataObjectSyncSource source  = getInstance();
        FileDataObjectWrapper    wrapper = null;

        // invoking the checkresumable method on an empty wrapper
        invokeCheckResumable(source, wrapper);

        FileDataObject object = new FileDataObject(null, null);

        String id = "myid";
        wrapper = new FileDataObjectWrapper("myid", "userid", object);

        // invoking the checkresumable method on an empty wrapper
        invokeCheckResumable(source, wrapper);

        long receivedBytes = 1234L;
        long totalBytes    = 2344L;
        // inviking the checkresumable method
        FileDataObjectBody body = new FileDataObjectBody();
        FileDataObjectMetadata metadata = new FileDataObjectMetadata();
        
        metadata.setUploadStatus(UploadStatus.PARTIALLY_UPLOADED);
        object.setMetadata(metadata);
        object.setBody(body);
        
        // invoking the checkresumable method with null size
        metadata.setSize(null);
        wrapper.setSizeOnStorage(null);
        try {
            invokeCheckResumable(source, wrapper);
            fail("Expected null pointer exception");
        }catch(NullPointerException e) {
            assertEquals("Wrong exception message",
                         "Item with uid 'myid' has null size, unable to create data "+
                         "information about resumable item.",
                         e.getMessage());
        }

        // invoking the checkresumable method with null size on storage
        metadata.setSize(totalBytes);
        try {
            invokeCheckResumable(source, wrapper);
            fail("Expected null pointer exception");
        fail("Expected PartialContentException");
        } catch(PartialContentException e) {
            assertEqualsPartialException(0,totalBytes,id,e);
        }

        // invoking the checkresumable method with a resumable item
        metadata.setSize(totalBytes);
        wrapper.setSizeOnStorage(receivedBytes);
        try {
            invokeCheckResumable(source, wrapper);
            fail("Expected PartialContentException");
        } catch(PartialContentException e) {
            assertEqualsPartialException(receivedBytes,totalBytes,id,e);
        }

        // invoking the checkresumable method with a not resumable item
        metadata.setUploadStatus(UploadStatus.NOT_STARTED);
        try {
            invokeCheckResumable(source, wrapper);
        } catch(PartialContentException e) {
            fail("Item it's not resumable!");
        }
        
        // invoking the checkresumable method with a not resumable item
        metadata.setUploadStatus(UploadStatus.UPLOADED);
        try {
            invokeCheckResumable(source, wrapper);
        } catch(PartialContentException e) {
            fail("Item it's not resumable!");
        }
        
        // invoking the checkresumable method with a not resumable item
        metadata.setUploadStatus(UploadStatus.EXPORTED);
        try {
            invokeCheckResumable(source, wrapper);
        } catch(PartialContentException e) {
            fail("Item it's not resumable!");
        }

    }

    // --------------------------------------------------------- Private Methods

    private FileDataObjectSyncSource getInstance() throws BeanException {

        Configuration configuration = Configuration.getConfiguration();

        FileDataObjectSyncSource instance =
            (FileDataObjectSyncSource) configuration.getBeanInstanceByName(CONFIG_FILE_PATH);

        return instance;
    }

    private void freeDatabaseResources()  {
        DBHelper.closeConnection(coreDbTester);
    }

    private void invokeCheckResumable(FileDataObjectSyncSource source ,
                                      FileDataObjectWrapper fdow)
    throws Throwable {
        PrivateAccessor.invoke(source,
                               "checkResumableUpload" ,
                               new Class[]{FileDataObjectWrapper.class},
                               new Object[]{fdow});
    }

    private void assertEqualsPartialException(long receivedBytes,
                                              long itemSize,
                                              String id,
                                              Throwable exception) {

        if(exception==null) {
            fail("Caught exception is null");
        }

        if(! (exception instanceof PartialContentException)) {
            fail("Caught exception is not an instance of PartialContentException");
        }

        PartialContentException e = (PartialContentException) exception;
        assertEquals("Wrong message",
                     String.format("%s/%s",receivedBytes,itemSize), 
                     e.getMessage());
        assertEquals("Wrong item size",
                     itemSize,
                     e.getItemSize());
        assertEquals("Wrong status code",
                     206,
                     e.getStatusCode());
        assertEquals("Wrong already received bytes",
                     receivedBytes,
                     e.getReceivedBytes());
    }
}
