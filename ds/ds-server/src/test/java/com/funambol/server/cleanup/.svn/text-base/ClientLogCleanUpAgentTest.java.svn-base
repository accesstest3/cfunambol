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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import junitx.util.PrivateAccessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.funambol.framework.tools.IOTools;

/**
 * Test cases for ClientLogCleanUpAgent class.
 *
 * @version $Id$
 */
public class ClientLogCleanUpAgentTest extends TestCase {

    // --------------------------------------------------------------- Constants
    private static final String BASE_DIR =
        "src/test/data/com/funambol/server/cleanup/";
    private static final String TARGET_DIR =
        "target/test/data/com/funambol/server/cleanup/";

    private static final String ARCHIVE_DIR = TARGET_DIR + "archive-logs";
    private static final String EXCEEDED_ARCHIVE_DIR =
        TARGET_DIR + "exceeded-archive-logs";
    private static final String LOGS_DIR    = TARGET_DIR + "clients-log";

    // ------------------------------------------------------------ Private data
    private String clientsLogDir                 = LOGS_DIR;
    private String targetArchivationDirectory    = ARCHIVE_DIR;
    private int    activationThreshold           = 2;
    private int    maxNumberOfArchivedFiles      = 5;
    private int    numberOfArchivedFilesToDelete = 3;
    private long   timeToRest                    = 1000L;
    private String lockName                      = "lock";

    private ClientLogCleanUpAgent agent = null;

    // ------------------------------------------------------------ Constructors
    public ClientLogCleanUpAgentTest(String testName) {
        super(testName);
    }

    // ------------------------------------------------------- Protected methods
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        agent = new ClientLogCleanUpAgent(clientsLogDir,
                                          targetArchivationDirectory,
                                          activationThreshold,
                                          maxNumberOfArchivedFiles,
                                          numberOfArchivedFilesToDelete,
                                          timeToRest,
                                          lockName);

        try {
            FileUtils.forceMkdir(new File(TARGET_DIR));
            FileUtils.cleanDirectory(new File(TARGET_DIR));
            FileUtils.copyDirectory(new File(BASE_DIR), new File(TARGET_DIR),
                new NotFileFilter(new SuffixFileFilter(".svn")));
        } catch(IOException e) {
            assertTrue("Unable to handle target dir", true);
        }
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        FileUtils.cleanDirectory(new File(TARGET_DIR));
    }

    // -------------------------------------------------------------- Test cases
    public void testAgentInitialization_NullLogDir() {

        clientsLogDir = null;
        
        try {
            new ClientLogCleanUpAgent(clientsLogDir,
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);
            fail("Expected an IllegalArgumentException on null log dir");
        } catch(IllegalArgumentException e) {
            assertTrue("Thrown an IllegalArgumentException", true);
        }
    }

    public void testAgentInitialization_NegativeTimeToRest() {

        timeToRest = -1000L;

        try {
            new ClientLogCleanUpAgent(clientsLogDir,
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);
            fail("Expected an IllegalArgumentException on negative rest time");
        } catch(IllegalArgumentException e) {
            assertTrue("Thrown an IllegalArgumentException", true);
        }
    }

    public void testAgentInitialization_NegativeActivationThreshold() {

        activationThreshold = -2;

        try {
            new ClientLogCleanUpAgent(clientsLogDir,
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);
            fail("Expected an IllegalArgumentException on negative " +
                 "activation Threshold");
        } catch(IllegalArgumentException e) {
            assertTrue("Thrown an IllegalArgumentException", true);
        }
    }

    public void testAgentInitialization_NegativeMaxNumberOfArchivedFiles() {

        maxNumberOfArchivedFiles = -5;

        try {
            new ClientLogCleanUpAgent(clientsLogDir,
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);
            fail("Expected an IllegalArgumentException on negative " +
                 "max number of archived files");
        } catch(IllegalArgumentException e) {
            assertTrue("Thrown an IllegalArgumentException", true);
        }
    }

    public void testAgentInitialization_NegativeNumberOfArchivedFilesToDelete() {

        numberOfArchivedFilesToDelete = -3;

        try {
            new ClientLogCleanUpAgent(clientsLogDir,
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);
            fail("Expected an IllegalArgumentException on negative " +
                 "number of archived files to be deleted");
        } catch(IllegalArgumentException e) {
            assertTrue("Thrown an IllegalArgumentException", true);
        }
    }

    public void testArchivationNeeded_NotValidLogDir() throws Throwable {

        ClientLogCleanUpAgent instance = new ClientLogCleanUpAgent("file.txt",
                                      targetArchivationDirectory,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        assertFalse("Accepted a not valid log dir", invokeArchivationNeeded(instance));
    }

    public void testArchivationNeeded_ThresholdNotReached() throws Throwable {

        activationThreshold = 10;
        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        assertFalse("Not need to invoke the archivation", invokeArchivationNeeded(instance));
    }

    public void testArchivationNeeded_ThresholdReached() throws Throwable {

        activationThreshold = 5;
        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        assertTrue("Need to invoke the archivation", invokeArchivationNeeded(instance));
    }

    public void testInternalRun_ArchivationRequiredCleanup() throws Throwable {
        ClientLogCleanUpAgent instance = 
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      "",
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        File f = new File(LOGS_DIR);
        assertTrue("Log dir does not exist", f.exists());
        String[] childs = f.list();
        assertTrue("Log dir contains more children", childs.length == 1);
        assertTrue("Log dir contains only lock file", childs[0].endsWith(".lck"));
    }

    public void testInternalRun_ArchivationRequiredArchive() throws Throwable {
        //
        // archive dir:
        // - exists
        // - is a dir
        // - does not contain more children than the maxNumberOfArchivedFiles
        //
        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        File archive = new File(ARCHIVE_DIR);
        assertTrue("Archive dir does not exist", archive.exists());
        String[] childs = archive.list();
        assertTrue("Archive dir contains more children", childs.length == 1);
        assertTrue("Archive dir contains only lock file", childs[0].endsWith(".zip"));

        File log = new File(LOGS_DIR);
        assertTrue("Log dir does not exist", log.exists());
        String[] logChilds = log.list();
        assertTrue("Log dir contains more children", logChilds.length == 1);
        assertTrue("Log dir contains only lock file", logChilds[0].endsWith(".lck"));
    }

    public void testInternalRun_ArchivationRequiredArchive_ArchiveDirNotExit()
    throws Throwable {

        String archiveDir = TARGET_DIR + "notexist";

        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      archiveDir,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        File f = new File(archiveDir);
        assertTrue("Archive dir does not exist", f.exists());
        String[] childs = f.list();
        assertTrue("Archive dir contains more children", childs.length == 1);
        assertTrue("Archive dir contains only lock file", childs[0].endsWith(".zip"));
    }

    public void testInternalRun_ArchivationRequiredArchive_ArchiveDirIsAFile()
    throws Throwable {

        String archiveDir = TARGET_DIR + "notdir.txt";
        File test = new File(archiveDir);
        IOTools.writeFile("hello", test);

        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      archiveDir,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        try {
            PrivateAccessor.invoke(instance, "internalRun", null, null);
            fail("Expected an IllegalArgumentException");
        } catch(IOException e) {
            assertTrue("Unable to recognize the input as a file", true);
        }
    }

    public void testInternalRun_ArchivationRequiredArchive_ExceededMaxArchivedFile()
    throws Throwable {

        File f = new File(EXCEEDED_ARCHIVE_DIR);
        assertTrue("Archive dir does not exist", f.exists());
        File[] childs = f.listFiles();
        assertTrue("Wrong number of children contained", childs.length == 6);

        List<String> oldestFiles = new ArrayList<String>();
        oldestFiles.add("20101110_165640.zip");
        oldestFiles.add("20101110_165840.zip");
        oldestFiles.add("20101110_170540.zip");

        File child = null;
        String fileName = null;
        for (int i=0; i<childs.length; i++) {
            child = childs[i];

            fileName = child.getName();

            if (oldestFiles.contains(fileName)) {
                assertTrue("Unable to set last modified for '"+fileName+"'",
                           child.setLastModified(System.currentTimeMillis() - 1000));
            } else {
                assertTrue("Unable to set last modified for '"+fileName+"'",
                           child.setLastModified(System.currentTimeMillis()));
            }
        }

        //
        // archive dir contain more children than the maxNumberOfArchivedFiles
        //
        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      EXCEEDED_ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        f = new File(EXCEEDED_ARCHIVE_DIR);
        assertTrue("Archive dir does not exist anymore", f.exists());
        String[] childNames = f.list();
        // the old 3 plus the new one
        assertTrue("Archive dir contains more children", childNames.length == 4);
    }

    public void testInternalRun_ArchivationRequiredArchive_LogWithRecentTmp()
    throws Throwable {

        String userADir = LOGS_DIR + File.separator + 
                          "user-a" + File.separator +
                          "uploadinprogress.tmp";
        File tmpFile = new File(userADir);
        IOTools.writeFile("upload in progress..", tmpFile);

        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        File f = new File(ARCHIVE_DIR);
        assertTrue("Archive dir does not exist", f.exists());
        String[] childs = f.list();
        assertTrue("Archive dir contains more children", childs.length == 1);
        assertTrue("Archive dir contains only lock file", childs[0].endsWith(".zip"));

        File log = new File(LOGS_DIR);
        assertTrue("Log dir does not exist", log.exists());
        File[] logChilds = log.listFiles();
        // .lck file and user-a directory that contains the .tmp
        assertTrue("Log dir contains more children", logChilds.length == 2);
        assertTrue("The tmp file does not exist anymore", tmpFile.exists());
    }

    public void testInternalRun_ArchivationRequiredArchive_LogWithOldTmp()
    throws Throwable {

        String userBDir = LOGS_DIR + File.separator +
                          "user-b" + File.separator +
                          "uploadinprogress.tmp";
        File tmpFile = new File(userBDir);
        IOTools.writeFile("upload in progress..", tmpFile);
        assertTrue("Unable to set last modified time",
                   tmpFile.setLastModified(System.currentTimeMillis() - 4000000));

        ClientLogCleanUpAgent instance =
            new ClientLogCleanUpAgent(LOGS_DIR,
                                      ARCHIVE_DIR,
                                      activationThreshold,
                                      maxNumberOfArchivedFiles,
                                      numberOfArchivedFilesToDelete,
                                      timeToRest,
                                      lockName);

        PrivateAccessor.invoke(instance, "internalRun", null, null);

        File f = new File(ARCHIVE_DIR);
        assertTrue("Archive dir does not exist", f.exists());
        String[] childs = f.list();
        assertTrue("Archive dir contains more children", childs.length == 1);
        assertTrue("Archive dir contains only lock file", childs[0].endsWith(".zip"));

        File log = new File(LOGS_DIR);
        assertTrue("Log dir does not exist", log.exists());
        String[] logChilds = log.list();
        assertTrue("Log dir contains more children", logChilds.length == 1);
        assertFalse("The tmp file does not exist anymore", tmpFile.exists());
        assertTrue("Log dir contains only lock file", logChilds[0].endsWith(".lck"));
    }

    public void testBuildTargetFileName() throws Throwable {

        String directory = null;
        Date   when      = new Date(1289312162656L);
        String result    = invokeBuildName(agent,directory,when);
        String expected  = "20101109_151602.zip";
        assertEquals("Wrong file name",expected,result);

        directory = "mydir";
        result    = invokeBuildName(agent,directory,when);
        expected  = "mydir/20101109_151602.zip";
        assertEquals("Wrong file name",expected,result);

        directory = "anotherdir\\";
        result    = invokeBuildName(agent,directory,when);
        expected  = "anotherdir/20101109_151602.zip";
        assertEquals("Wrong file name",expected,result);

        directory = "seconddir/";
        result    = invokeBuildName(agent,directory,when);
        expected  = "seconddir/20101109_151602.zip";
        assertEquals("Wrong file name",expected,result);
    }

    // --------------------------------------------------------- Private methods
    private String invokeBuildName(ClientLogCleanUpAgent agent,
                                   String directory,
                                   Date when)
    throws Throwable {
        return (String) PrivateAccessor.invoke(agent,
                           "buildTargetFileName",
                           new Class[]{String.class,Date.class},
                           new Object[]{directory,when});
    }

    private boolean invokeArchivationNeeded(ClientLogCleanUpAgent agent)
    throws Throwable {
        Boolean result =
            (Boolean)PrivateAccessor.invoke(agent,"archivationNeeded", null, null);

        return result.booleanValue();
    }

}
