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

package com.funambol.server.sendlog.authentication;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.dbunit.database.DatabaseConnection;

import com.funambol.framework.security.Credential;
import com.funambol.tools.database.DBHelper;

import com.funambol.server.sendlog.Constants;

/**
 * Test cases for HttpBasicAuthenticator class.
 *
 * @version $Id: HttpBasicAuthenticatorTest.java 36098 2010-10-21 13:51:11Z luigiafassina $
 */
public class HttpBasicAuthenticatorTest extends TestCase implements Constants {
    // --------------------------------------------------------------- Constants
    private static final String DEVICE_ID = "fwm-123456789";

    private static final String INITIAL_DATASET =
        "src/test/resources/data/com/funambol/server/sendlog/authentication/initial-db-dataset.xml";
    private static final String STORE_ITEM_DATASET =
        "src/test/resources/data/com/funambol/server/sendlog/authentication/store-item-db-dataset.xml";

    private static final String TEST_RESOURCE_BASEDIR =
        System.getProperty("test.resources.dir");
    private static final String CORE_SCHEMA_SOURCE =
        TEST_RESOURCE_BASEDIR + "/sql/create-core-schema.sql";

    private DatabaseConnection coreDBConn = null;

    static {
        try {
            
            boolean result = 
                DBHelper.initDataSources(CORE_SCHEMA_SOURCE, null, false);
            assertTrue("Error initializing the database", result);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------------------ Constructors
    public HttpBasicAuthenticatorTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        DBHelper.createDatabaseTester(INITIAL_DATASET, true);

        coreDBConn = new DatabaseConnection(DBHelper.getCoreConnection());
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        DBHelper.closeConnection(coreDBConn);
    }

    // -------------------------------------------------------------- Test cases
    public void testParseCredentials_ValidCred_1() throws Throwable {

        String credentialHeader = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Credential expectedCredential = new Credential();
        expectedCredential.setUsername("Aladdin");
        expectedCredential.setDeviceId(DEVICE_ID);

        Credential resultCredential =
            (Credential)PrivateAccessor.invoke(instance,
                                               "parseCredentials",
                                               null,
                                               null);
        assertCredential(expectedCredential, resultCredential);

        assertEquals("Wrong username returned from instance",
                     "Aladdin", instance.getUsername());
    }

    public void testParseCredentials_ValidCred_2() throws Throwable {
        String credentialHeader = "Basic IDog";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Credential expectedCredential = new Credential();
        expectedCredential.setUsername(" ");
        expectedCredential.setDeviceId(DEVICE_ID);

        Credential resultCredential =
            (Credential)PrivateAccessor.invoke(instance,
                                               "parseCredentials",
                                               null,
                                               null);
        assertCredential(expectedCredential, resultCredential);
    }

    public void testParseCredentials_ValidCred_3() throws Throwable {
        String credentialHeader = "Basic Og==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Credential expectedCredential = new Credential();
        expectedCredential.setUsername("");
        expectedCredential.setDeviceId(DEVICE_ID);

        Credential resultCredential =
            (Credential)PrivateAccessor.invoke(instance,
                                               "parseCredentials",
                                               null,
                                               null);
        assertCredential(expectedCredential, resultCredential);
    }

    public void testParseCredentials_ValidCred_4() throws Throwable {
        String credentialHeader = "Basic anVsaWV0OmJ1cmtl";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Credential expectedCredential = new Credential();
        expectedCredential.setUsername("juliet");
        expectedCredential.setDeviceId(DEVICE_ID);

        Credential resultCredential =
            (Credential)PrivateAccessor.invoke(instance,
                                               "parseCredentials",
                                               null,
                                               null);
        assertCredential(expectedCredential, resultCredential);
    }

    public void testParseCredentials_WrongCred() throws Throwable {

        String credentialHeader = "Basic a==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);
        
        try {

            PrivateAccessor.invoke(instance, "parseCredentials", null, null);
            fail("Exception expected on wrong credential");

        } catch(UnauthorizedException e) {
            assertEquals("Wrong exception message", 
                         DECODING_TOKEN_ERRMSG, e.getMessage());
        }
    }

    public void testParseCredentials_WithoutBasic() throws Throwable {
        String credentialHeader = "dXNlcnB3ZA==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        try {

            PrivateAccessor.invoke(instance, "parseCredentials", null, null);
            fail("Exception expected on wrong credential");

        } catch(UnauthorizedException e) {
            assertEquals("Wrong exception message",
                         DECODING_TOKEN_ERRMSG, e.getMessage());
        }
    }

    public void testParseCredentials_EmptyCred() throws Throwable {
        String credentialHeader = "Basic  : ";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        try {
            PrivateAccessor.invoke(instance, "parseCredentials", null, null);
            fail("Exception expected on wrong credential");
        } catch (UnauthorizedException e) {
            assertEquals("Wrong exception message",
                         DECODING_TOKEN_ERRMSG, e.getMessage());
        }
    }

    public void testParseCredentials_WithoutColumns() throws Throwable {
        String credentialHeader = "Basic dXNlcnB3ZA==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Object result =
            PrivateAccessor.invoke(instance, "parseCredentials", null, null);
        assertNull(result);
    }

    public void testExtractCredentials_Null() throws Throwable {
        String credentialHeader = "Basic dXNlcnB3ZA=="; //without :
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        try {

            PrivateAccessor.invoke(instance, "extractCredentials", null, null);
            fail("Exception expected on wrong credential");
            
        } catch (UnauthorizedException e) {
            assertEquals("Wrong exception message",
                         AUTHORIZATION_BAD_FORMAT_ERRMSG, e.getMessage());
        }
    }

    public void testFillCredentialsWithDeviceId() throws Throwable {
        String credentialHeader = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, DEVICE_ID);

        Credential cred = new Credential();
        cred.setUsername("Alladin");
        PrivateAccessor.invoke(instance,
                               "fillCredentialsWithDeviceId",
                               new Class[] {Credential.class},
                               new Object[]{cred});
        assertNotNull("Returned a null deviceid", cred.getDeviceId());
        assertEquals("Wrond deviceid set", DEVICE_ID, cred.getDeviceId());

        DBHelper.assertEqualsDataSet(coreDBConn,
                                     "FNBL_DEVICE",
                                     INITIAL_DATASET);
    }

    public void testFillCredentialsWithDeviceId_NewDevice() throws Throwable {
        String credentialHeader = "Basic QWxhZGRpbjpvcGVuIHNlc2FtZQ==";
        HttpBasicAuthenticator instance =
            new HttpBasicAuthenticator(credentialHeader, "fwm-isanewdevice");

        Credential cred = new Credential();
        cred.setUsername("Alladin");
        PrivateAccessor.invoke(instance,
                               "fillCredentialsWithDeviceId",
                               new Class[] {Credential.class},
                               new Object[]{cred});

        DBHelper.assertEqualsDataSet(coreDBConn,
                                     "FNBL_DEVICE",
                                     STORE_ITEM_DATASET);
    }

    // --------------------------------------------------------- Private methods
    private void assertCredential(Credential expectedCredential,
                                  Credential resultCredential  ) {

        assertNotNull("Expected not null credential", resultCredential);
        assertEquals("Wrong username", expectedCredential.getUsername(),
                     resultCredential.getUsername());
        assertEquals("Wrong device id", expectedCredential.getDeviceId(),
                      resultCredential.getDeviceId());
    }

}
