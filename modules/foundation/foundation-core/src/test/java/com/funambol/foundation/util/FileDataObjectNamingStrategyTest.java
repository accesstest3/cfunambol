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

package com.funambol.foundation.util;

import com.funambol.framework.tools.MD5;
import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

/**
 * Test class for {@link FileDataObjectNamingStrategy} class
 * @version $Id: FileDataObjectNamingStrategyTest.java 36662 2011-02-17 18:05:07Z luigiafassina $
 */
public class FileDataObjectNamingStrategyTest extends TestCase {

    // ------------------------------------------------------------ Private data
    FileDataObjectNamingStrategy strategy = null;
    private final static int fileSysDirDepth = 8;

    // ------------------------------------------------------------ Constructors
    public FileDataObjectNamingStrategyTest(String testName) {
        super(testName);
        strategy = new FileDataObjectNamingStrategy(fileSysDirDepth);
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
    public void testCreateFileName(){

        // define initial parameter values
        String itemId = "2342";

        // define expected results values
        String expectedFileNameSuffix = "-2342";

        // Call the method being tested.
        String currentName = strategy.createFileName(itemId);

        // Verify that the results are as expected
        // we test only that the last characters of the name correspond to the
        // item id, because the first 13 characters are random
        assertEquals("Wrong item name suffix", expectedFileNameSuffix, currentName.substring(13));
    }

    public void testGetFilePath() {

        // define initial parameter values
        String username = "test-user1";
        String localFilename = "1kbn0qfejhyjt-2342";

        // define expected results values
        String expectedPath = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==/" + localFilename;

        // Call the method being tested.
        String filePath = strategy.getFilePath(username, localFilename);

        // Verify that the results are as expected
        assertEquals("Wrong file path", expectedPath, filePath);
    }

    public void testGetEXTFolderPath() {

        // define initial parameter values
        String username = "test-user1";
        String localFilename = "1kbn0qfejhyjt-2342";

        // define expected results values
        String expectedPath = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==/" + localFilename + "-ext";

        // Call the method being tested.
        String filePath = strategy.getEXTFolderPath(username, localFilename);

        // Verify that the results are as expected
        assertEquals("Wrong ext folder path", expectedPath, filePath);

    }
    
    public void testGetEXTFolderName() {

        String username = "test-user1";

        String expected = "test-user1-ext";
        String result = strategy.getEXTFolderName(username);
        
        assertEquals("Wrong ext folder path", expected, result);

    }

    public void testCreateTmpFileName() {

        String result = strategy.createTmpFileName(null);
        assertNotNull(result);
        assertTrue(result.endsWith(".tmp"));

        result = strategy.createTmpFileName("user");
        assertNotNull(result);
        assertTrue(result.endsWith("-user.tmp"));
    }

    public void testGetUserFolderPath(){
        // define initial parameter values
        String username = "test-user1";

        // define expected results values
        String expectedPath = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==";

        // Call the method being tested.
        String userFolderPath = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals("Wrong user folder path", expectedPath, userFolderPath);
    }

    public void testGetUserFolderPath_FileSysDirDepth_8() throws Throwable {
        // define initial parameter values
        String username = "test-user1";

        // define expected results values
        String expected = "kb/as/qo/cs/xd/dn/lu/wf/dGVzdC11c2VyMQ==";

        // Call the method being tested.
        String result = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testGetUserFolderPath_SpecialCharacters() throws Throwable {
        // define initial parameter values
        String username = "t|e s:t\\u*s?e\"r<1>2|3";

        // define expected results values
        String expected = "dd/vs/eg/cf/gb/bl/jh/mx/dHxlIHM6dFx1KnM_ZSJyPDE+Mnwz";

        // Call the method being tested.
        String result = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testGetUserFolderPath_FileSysDirDepth_4() throws Throwable {
        // define initial parameter values
        String username = "test-user1";

        // define expected results values
        String expected = "kb/as/qo/cs/dGVzdC11c2VyMQ==";

        strategy = new FileDataObjectNamingStrategy(4);

        // Call the method being tested.
        String result = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testGetUserFolderPath_FileSysDirDepth_1() throws Throwable {
        // define initial parameter values
        String username = "test-user1";

        // define expected results values
        String expected = "kb/dGVzdC11c2VyMQ==";

        strategy = new FileDataObjectNamingStrategy(1);

        // Call the method being tested.
        String result = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testGetUserFolderPath_FileSysDirDepth_0() throws Throwable {
        // define initial parameter values
        String username = "test-user1";

        // define expected results values
        String expected = "dGVzdC11c2VyMQ==";
        
        strategy = new FileDataObjectNamingStrategy(0);

        // Call the method being tested.
        String result = strategy.getUserFolderPath(username);

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testComputeUserFolderPath() throws Throwable{

        String expected = "go/aj/eb/xv/es/xx/qn/tf/dGVzdC11c2VyMg==";

        String result = (String) PrivateAccessor.invoke(
            strategy,
            "computeUserFolderPath",
            new Class[]{String.class, int.class},
            new Object[]{"test-user2", fileSysDirDepth});

        assertEquals("Wrong computation of user folder path", expected, result);
    }

    public void testConvertToLowerCase() throws Throwable {
        byte[] digest = MD5.digest("YQ==".getBytes()); // 16 byte
        PrivateAccessor.invoke(
            strategy,
            "convertToLowerChar",
            new Class[]{byte[].class},
            new Object[]{digest});
        assertEquals("wbeefstwktegovqa", new String(digest));
    }

    public void testGetRandomString() throws Throwable {

        String result = (String) PrivateAccessor.invoke(
            strategy, "getRandomString", new Class[]{}, new Object[]{});

        assertNotNull(result);
        assertEquals(13, result.length());
    }

    public void testHide_normal() throws Throwable {
        // define initial parameter values
        String userName = "test-user1";

        // define expected results values
        String expected = "dGVzdC11c2VyMQ==";

        // Call the method being tested.
        String result = (String) PrivateAccessor.invoke(
            strategy,
            "hide",
            new Class[]{String.class},
            new Object[]{userName});

        // Verify that the results are as expected
        assertEquals(expected, result);
    }

    public void testHide_special() throws Throwable {
        // define initial parameter values
        String userName = "t|e s:t\\u*s?e\"r<1>2|3";

        // define expected results values
        String expected = "dHxlIHM6dFx1KnM_ZSJyPDE+Mnwz";

        // Call the method being tested.
        String result = (String) PrivateAccessor.invoke(
            strategy,
            "hide",
            new Class[]{String.class},
            new Object[]{userName});

        // Verify that the results are as expected
        assertEquals(expected, result);

    }

}
