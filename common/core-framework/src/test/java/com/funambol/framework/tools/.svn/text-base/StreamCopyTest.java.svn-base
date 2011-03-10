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

package com.funambol.framework.tools;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import junitx.framework.FileAssert;

/**
 *
 * @version $Id$
 */
public class StreamCopyTest extends TestCase {

    public static String RESOURCES_DIR = "target/classes/com/funambol/framework/tools/";
    protected final List<File> resourcesToRemove         = new ArrayList<File>();
    
    public StreamCopyTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        if(!this.resourcesToRemove.isEmpty()) {
            for(File resourceToRemove:resourcesToRemove) {
                if(resourceToRemove.exists()) {
                    if(!resourceToRemove.delete()) {
                        fail("Error removing resource '"+resourceToRemove.getName()+"'.");
                    }
                }
            }
        }
    }


    public void testStreamCopy_NullSource() throws IOException {
        StreamCopy copy = new StreamCopy(null, null);
        try {
         copy.performCopy();
         fail("Illegal argument exception");
        } catch(IllegalStateException e) {
            assertEquals("Wrong error message",
                         StreamCopy.SOURCE_NULL_ERROR_MSG,
                         e.getMessage());
        }
    }

    public void testStreamCopy_NullTarget() throws IOException {
        StreamCopy copy = new StreamCopy(new ByteArrayInputStream(new byte[0]), null);
        try {
         copy.performCopy();
         fail("Illegal argument exception");
        } catch(IllegalStateException e) {
            assertEquals("Wrong error message",
                         StreamCopy.TARGET_NULL_ERROR_MSG,
                         e.getMessage());
        }
    }

    public void testPerformCopy() throws Exception {
        File inputFile  = createFakeFile("fake_file.in",(2048*1024L));
        File outputFile = new File(RESOURCES_DIR,"fake_file.out");
        deleteRequestOnTearDown(inputFile);
        deleteRequestOnTearDown(outputFile);
        FileInputStream inputStream = new FileInputStream(inputFile);
        FileOutputStream outputStream = new FileOutputStream(outputFile);

        StreamCopy copy = new StreamCopy(inputStream, outputStream);

        copy.performCopy();

        FileAssert.assertEquals("Wrong copy",
                                inputFile,
                                outputFile);

        outputFile = new File(RESOURCES_DIR,"fake_file2.out");
        deleteRequestOnTearDown(outputFile);
        outputStream = new FileOutputStream(outputFile);
        int limit = 1024*1024;
        inputStream = new FileInputStream(inputFile);
        StreamCopy copyWithLimit = new StreamCopy(inputStream, outputStream, limit);

        try {
            copyWithLimit.performCopy();
            fail("Expected MaxDataTransferredException");
        } catch(StreamCopy.MaxDataTransferredException e) {
            assertEquals("Wrong exception message",
                         StreamCopy.MAX_BYTES_TRANSFERRED_ERROR_MSG,
                         e.getMessage());
        }

        assertTrue("Expected file",     outputFile.isFile());
        assertTrue("File doesn't exist",outputFile.exists());
        assertEquals("Wrong size", limit, outputFile.length());
    }

    private File createFakeFile(String fileName,long fileSize) throws FileNotFoundException, IOException {
        File target = new File(RESOURCES_DIR,fileName);

        FileOutputStream targetStream = new FileOutputStream(target, false);
        int count = 0;
        while(count<fileSize) {
            targetStream.write(count++);
        }
        targetStream.flush();
        targetStream.close();
        return target;
    }

     /**
     * add the given file to the resources that must be deleted on the tear
     * down method
     * @param toRemove the resource to be deleted on the teardown
     */
    private void deleteRequestOnTearDown(File toRemove) {
        resourcesToRemove.add(toRemove);
    }

}
