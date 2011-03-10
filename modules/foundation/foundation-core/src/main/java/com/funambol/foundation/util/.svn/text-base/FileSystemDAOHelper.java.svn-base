/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2009 Funambol, Inc.
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

import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;

import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.IOTools;

/**
 * Responsibility: manage the directory tree on the local file system where the
 * files belonging to a user are stored.
 * @version $Id$
 */
public class FileSystemDAOHelper {

    // --------------------------------------------------------------- Constants

    protected static final FunambolLogger log
        = FunambolLoggerFactory.getLogger(Def.LOGGER_NAME);

    /**
     * The buffer size used to copy an InputStream to an OutputStream
     */
    private static final int COPY_BUFFER_SIZE = 1024;

    /**
     * The number of seconds representing the maximum time a thread is
     * available to wait while acquiring the lock needed to create a
     * directory.
     */
    private static final long DIRECTOTY_CREATION_TIMEOUT = 3;

    /** File separator */
    protected static final String FS = FileDataObjectNamingStrategy.FS;

    // -------------------------------------------------------- Member Variables

    /** User id. */
    private String userName;

    public String getUserName() {
        return userName;
    }

    /** The root folder of the file data objects subtree. */
    private String rootPath = null;

    public String getRootPath() {
        return rootPath;
    }

    /**
     * Provides the path and the name for the files to be stored
     */
    private final FileDataObjectNamingStrategy fileDataObjectNamingStrategy;

    /*
     * holds the user folder relative path once it has been computed, to compute
     * it only once instead of every time the relative path is requested.
     */
    private String userFolderRelativePath = null;

    // ------------------------------------------------------------ Constructors

    /**
     *
     * @param userName The user name to which the files belong
     * @param rootPath The root path where starts the directory sub-tree
     * are located
     * @param fileDataObjectNamingStrategy the FileDataObjectNamingStrategy
     * used to define the file's path and name.
     * Invariant Condition: the File returned by method getFolder is actually a
     * folder.
     * @throws IOException if an error occurs creating the user folder
     */
    public FileSystemDAOHelper(String userName,
                               String rootPath,
                               FileDataObjectNamingStrategy fileDataObjectNamingStrategy)
    throws IOException {

        checkStringNotNull(userName, "Invalid Username: %s");

        checkStringNotNull(rootPath, "Invalid Root path: %s");

        this.userName        = userName;
        this.rootPath        = IOTools.normalize(rootPath);
        this.fileDataObjectNamingStrategy = fileDataObjectNamingStrategy;

        createUserFolder();
    }

    /**
     *
     * @param userName
     * @param rootPath
     * @param fileSysDirDepth
     * @throws IOException
     */
    public FileSystemDAOHelper(String userName,
                               String rootPath,
                               int fileSysDirDepth)
    throws IOException {

        this(userName, rootPath, new FileDataObjectNamingStrategy(fileSysDirDepth));
    }

    // ---------------------------------------------------------- Public Methods

    /**
     * Given the file name, retrieves the file handler from the directory tree.
     * If the user's directory doesn't exist, it creates it.
     * @param fileName
     * @return the file corresponding to the parameter fileName
     */
    public File getFile(String fileName) {
        return new File(getFilePath(fileName));
    }

    /**
     * Returns the folder of the directory belonging to the user.
     * @return the folder of the directory belonging to the user.
     */
    public File getFolder() {
        return new File(getUserFolderPath());
    }

    /**
     * Returns the 'ext' folder corresponding to the file.
     * @param fileName
     * @return the 'ext' folder corresponding to the file.
     * @throws IOException if an error occurs
     */
    public File getEXTFolder(String fileName) throws IOException {
        File extFolder = new File(getEXTFolderPath(fileName));
        if (!extFolder.exists()) {
            boolean created = extFolder.mkdirs();
            if (!created) {
                throw new IOException("Unable to create ext folder '" + fileName + "'");
            }
        }
        return extFolder;
    }

    /**
     * Returns the file path including the path of the user's folder.
     * @param fileName the file name
     * @return the file path including the path of the user's folder.
     */
    public String getFilePath(String fileName) {
        checkStringNotNull(fileName, "File name is not valid: %s");

        return getPath(fileName);
    }

    /**
     * Returns the ext folder path including the path of the user's folder.
     * @param fileName the name of the file corresponding to the ext folder.
     * @return the ext folder path including the path of the user's folder.
     */
    public String getEXTFolderPath(String fileName) {
        checkStringNotNull(fileName, "File name is not valid: %s");

        return getPath(fileDataObjectNamingStrategy.getEXTFolderName(fileName));
    }

    /**
     * Returns the file path including the path of the user's folder.
     * @param fileName the file name
     * @return the file path including the path of the user's folder.
     */
    public String getFileRelativePath(String fileName) {
        checkStringNotNull(fileName, "File name is not valid: %s");

        return getRelativePath(fileName);
    }

    /**
     * Returns the ext folder path including the path of the user's folder.
     * @param fileName the name of the file corresponding to the ext folder.
     * @return the ext folder path including the path of the user's folder.
     */
    public String getEXTFolderRelativePath(String fileName) {
        checkStringNotNull(fileName, "File name is not valid: %s");

        return getRelativePath(fileDataObjectNamingStrategy.getEXTFolderName(fileName));
    }

    /**
     * Returns the size of a temp file, stored in the default path
     * @param fileName
     * @return
     */
    public long getTmpFileSize(String fileName) {
        checkStringNotNull(fileName, "File name is not valid: %s");

        File file = getFile(fileName);
        if (file.exists()) return file.length();
        return -1;
    }

    /**
     * Creates randomly the file name used to store the file data object on the
     * server local file system.
     * For example 1y2p0ij32e8e7
     * @return a random file name to be used to store the file locally on the
     * server file system.
     */
    public String createTmpFileName() {

        return fileDataObjectNamingStrategy.createTmpFileName();
    }

    /**
     * Writes into the file, overwriting the content
     * @param fileName The file name
     * @param content The content to write into the file
     * @return the file where the content has been saved
     * @throws IOException
     */
    public File writeItem(String fileName, byte[] content) throws IOException {

        boolean append = false;
        return writeItem(fileName, content, append);
    }

    /**
     * writes into the file
     * @param fileName
     * @param content
     * @param append
     * @return the file where the content has been saved
     * @throws IOException
     */
    public File writeItem(String fileName, byte[] content, boolean append)
            throws IOException {

        File file = getFile(fileName);
        writeItem(file, content, append);
        return file;
    }

    /**
     *
     * @param file
     * @param content
     * @param append
     * @throws IOException
     */
    public void writeItem(File file, byte[] content, boolean append)
            throws IOException {

        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(file, append);
            stream.write(content);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Writes into the file, overwriting the content
     * @param content The content to write into the file
     * @return the file where the content has been saved
     * @throws IOException
     */
    public File writeItem(byte[] content) throws IOException {

        String fileName = fileDataObjectNamingStrategy.createTmpFileName();
        return writeItem(fileName, content);
    }

    /**
     * Save the input stream into a tmp file in the user folder
     * @param in the input stream
     * @return The tmp file where the content has been saved
     * @throws FileNotFoundException
     * @throws IOException
     */
    public File saveTmpFile(InputStream in)
        throws FileNotFoundException, IOException {

        return saveTmpFile(in, -1L);
    }

    /**
     * Save the input stream into a tmp file in the user folder
     * @param in the input stream
     * @param maxSize The max number of bytes to copy. If -1 no limit is checked.
     * @return The tmp file where the content has been saved
     * @throws FileNotFoundException
     * @throws IOException
     */
    public File saveTmpFile(InputStream in, long maxSize)
        throws FileNotFoundException, IOException {

        String tmpFileName = createTmpFileName();
        File tmpBodyFile = getFile(tmpFileName);
        
        try {
            saveFile(in, tmpBodyFile, maxSize);
        } catch (IOException ex) {
            if (tmpBodyFile != null) {
                tmpBodyFile.delete();
            }
            throw ex;
        }

        return tmpBodyFile;
    }

    public File saveFile(InputStream in, File file, long maxSize)
        throws FileNotFoundException, IOException {

        if (!file.canWrite()) {
            new IOException("Unable to write on " + file.getPath());
        }

        OutputStream out = null;
        try {
            out = new FileOutputStream(file);
            copy(in, out, maxSize);
        } finally {
            IOUtils.closeQuietly(out);
        }

        return file;
    }

    /**
     * Saves a new empty file where the content should be saved.
     *
     * @return the tmp file where the content should be saved
     * @throws FileNotFoundException if it's not possible to write on file
     * @throws IOException if an error occurs
     */
    public File saveTmpBodyFile() throws FileNotFoundException, IOException {

        File tmpBodyFile = null;

        try {
            String tmpFileName = createTmpFileName();
            tmpBodyFile = getFile(tmpFileName);

            tmpBodyFile.createNewFile();

        } catch (IOException ex) {
            if (tmpBodyFile != null) {
                tmpBodyFile.delete();
            }
            throw new IOException("Unable to write on " + tmpBodyFile.getPath());
        }

        return tmpBodyFile;
    }

    /**
     * Renames file1 in file2
     * @param file1
     * @param file2
     * @return true if and only if the renaming succeeded
     * @throws IOException
     */
    public boolean renameFile(File file1, File file2) throws IOException {
        return file1.renameTo(file2);
    }

    /**
     * returns the bytes saved on the file
     * @param fileName
     * @return the file's bytes
     * @throws FileNotFoundException
     * @throws IOException
     */
    public byte[] readData(String fileName)
    throws FileNotFoundException, IOException {

        File file = getFile(fileName);
        return readData(file);
    }

    /**
     * returns the file's bytes
     * @param file
     * @return  the file's bytes
     * @throws FileNotFoundException
     * @throws IOException
     */
    public byte[] readData(File file)
    throws FileNotFoundException, IOException {

        FileInputStream stream = null;
        try {
            byte[] buffer = new byte[(int) file.length()];
            stream = new FileInputStream(file);
            stream.read(buffer);

            return buffer;
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    /**
     * Deletes a file in the user folder
     * @param fileName the file's name to delete
     * @return  <code>true</code> if and only if the file or directory is
     *          successfully deleted; <code>false</code> otherwise
     */
    public boolean delete(String fileName) {
        File file = getFile(fileName);
        return file.delete();
    }

    /**
     * Deletes all the tmp files older than day in the user folder
     */
    public void deleteOldTmpFiles() {
        File folder = getFolder();
        //may happens that user folder doesn't exist (for example, when the last
        //fdo for the user is deleted)
        if (!folder.exists()) return;
        for (File file : folder.listFiles(getFilterForOldTmpFiles()) ) {
            file.delete();
        }
    }

    /**
     * Removes the dir and its subfolders.
     *
     * @param folderPath the folder to delete
     * @throws IOException if an error occurs
     */
    public void removeDir(String folderPath) throws IOException {

        File folder = new File(folderPath);

        if (folder.isDirectory()) {
            //
            // Delete dir. recursively
            //
            IOTools.deleteDirectory(folder, null);
        }
    }

    /**
     * Copy from an InputStream to an OutputStream.
     *
     * @param input The InputStream
     * @param output The OutputStream
     * @param expectedSize The expected number of bytes to copy. If -1 the size
     * is not checked.
     * @return the number of bytes copied
     * @throws IOException if an error occurs
     */
    public long copy(InputStream input, OutputStream output, long expectedSize)
    throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        long count = 0;
        while (true) {
            int read = input.read(buffer);
            if (read < 0) {
                break;
            }
            count += read;
            if (expectedSize >= 0 && count > expectedSize) {
                throw new EOFException("More bytes (" + count + ") than expected (" + expectedSize + ")");
            }

            output.write(buffer, 0, read);
        }

        if (expectedSize >= 0 && count < expectedSize) {
            throw new EOFException("Less bytes (" + count + ") than expected (" + expectedSize + ")");
        }
        return count;
    }

    /**
     * Writes the InputStream in append to the file starting from the given
     * position. If the file size does not match the expected value an
     * EOFException is thrown.
     *
     * @param f the file to open in writing
     * @param in the InputStream to copy in the file
     * @param pos the file-pointer offset
     * @param expectedSize the expected file size
     * @throws IOException if an error occurs
     */
    public void copyInRandomAccessFile(File        f  ,
                                       InputStream in ,
                                       long        pos,
                                       long        expectedSize)
    throws IOException {

        //
        // open file for reading and writing: also its metadata will be updated
        //
        RandomAccessFile raf = new RandomAccessFile(f, "rws");

        raf.seek(pos);
        byte[] buffer = new byte[1024];
        long count = 0;
        while (true) {
            int read = in.read(buffer);
            if (read < 0) {
                break;
            }
            count += read;
            if (expectedSize >= 0 && count > expectedSize) {
                throw new EOFException("More bytes (" + count + ") than expected (" + expectedSize + ")");
            }
            raf.write(buffer, 0, read);
        }
        raf.close();

        if (expectedSize >= 0 && count < expectedSize) {
            throw new EOFException("Less bytes (" + count + ") than expected (" + expectedSize + ")");
        }
    }

    // --------------------------------------------------------- Private Methods

    /**
     * Creates the user folder to which store file data object content files if
     * it does not already exist.
     *
     * this method, <code>false</code> otherwise.
     * @throws IOException
     */
    private void createUserFolder() throws IOException {

        File folder = getFolder();

        if(log.isTraceEnabled()) {
            log.trace("Checking existence/create folder '"+
                      folder.getAbsolutePath()+"' for user '"+this.userName+"'.");
        }

        long  started       = System.currentTimeMillis();
        short result        = -1;
        Throwable exception = null;
        try {
           result= IOTools.createDirectoryOnRaceCondition(folder,
                                                          DIRECTOTY_CREATION_TIMEOUT,
                                                          TimeUnit.SECONDS);
        } catch(IOException t) {
          // Making the IOEXception available to
          exception = t;
          throw t;
        } finally {
            logFolderCreationInfo(folder,
                                  result,
                                  System.currentTimeMillis() - started,
                                  exception);
        }
   }

    /**
     * Returns the file path including the path of the user's folder.
     * @param fileName
     * An example of returned path:
     * "../../../ds-server/db/briefcase/om/fo/ly/hx/ko/ye/ff/ej/test-user1/1y2p0ij32e8e7-origfilename.ext"
     * @return
     */
    private String getPath(String fileName) {
        StringBuilder path = new StringBuilder(getUserFolderPath());
        path.append(FS).append(fileName);

        return path.toString();
    }

    /**
     * Returns the file path including the path of the user's folder.
     * @param fileName
     * An example of returned path:
     * "/om/fo/ly/hx/ko/ye/ff/ej/test-user1/1y2p0ij32e8e7-origfilename.ext"
     * @return the file path including the path of the user's folder.
     * @precondition the file name must be not null
     */
    private String getRelativePath(String fileName) {

        StringBuilder path = new StringBuilder(getUserFolderRelativePath());
        path.append(FS).append(fileName);

        return path.toString();
    }

    /**
     * returns the path of the user's folder.
     * An example of returned path:
     * "../../../ds-server/db/briefcase/om/fo/ly/hx/ko/ye/ff/ej/test-user1"
     * @return
     */
    private String getUserFolderPath() {
        StringBuilder path = new StringBuilder();
        path.append(rootPath).append(FS);
        path.append(getUserFolderRelativePath());

        return path.toString();
    }

    private String getUserFolderRelativePath() {

        if (userFolderRelativePath == null) {
            userFolderRelativePath = fileDataObjectNamingStrategy.getUserFolderPath(userName);
        }
        return userFolderRelativePath;
    }

    /**
     * We are interested in tmp files older than one day
     * @return the FileFilter for tmp files older than one day
     */
    private FileFilter getFilterForOldTmpFiles() {
        final long cutoff = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        FileFilter filter = new FileFilter() {

            public boolean accept(File file) {
                return (file.getName().endsWith(FileDataObjectNamingStrategy.TMP_FILE_SUFFIX) &&
                        file.lastModified() < cutoff);
            }
        };

        return filter;
    }


    /**
     * It's a facility method used to provide verbose log in the case the bug
     * 8813 came back.
     *
     * @param folder it's the folder we're trying to create
     * @param result the short returned when the creation of the directory is
     * performed used the proper method of the IOTools class.
     * @param elapsed the long representing the time the call to the IOTools method
     * takes
     * @param exception any exception thrown when calling the IOTools method.
     *
     */
    private void logFolderCreationInfo(File folder,
                                       short result,
                                       long elapsed,
                                       Throwable exception)  {

        // Nothing to do here if the trace log is not enabled and no error
        // occured during the creation of the folder
        if(!isLogForFolderCreationEnabled(exception)) {
            return;
        }

        String message = createLogMessageAboutFolderCreation(folder,
                                                             result,
                                                             elapsed,
                                                             exception);
        if(message == null) {
            log.trace("Unable to log info about directory creation, message is null.");
        } else if(exception==null) {
            log.trace(message);
        } else {
            log.error(message,exception);
        }
    }

    /**
     * It's a facility method added to create the log message about the creation
     * of the folder used to store user pictures.
     *
     * @param folder it's the folder we're trying to create
     * @param result the short returned when the creation of the directory is
     * performed used the proper method of the IOTools class.
     * @param elapsed the long representing the time the call to the IOTools method
     * takes
     * @param exception any exception thrown when calling the IOTools method.
     *
     * @return the message containing all information about the creation of the
     * folder we use to store user's pictures.
     */
    private String createLogMessageAboutFolderCreation(File folder,
                                                       short result,
                                                       long elapsed,
                                                       Throwable exception) {
        String path             = "Unable to retrieve it";
        boolean isFolderNull    = folder==null;
        StringBuffer message    = new StringBuffer();
        boolean      dumpFolderData    = false;
        try {
            path = isFolderNull?"null folder":folder.getCanonicalPath();
        } catch(IOException e) {
            log.trace("An error occurred retrieving folder path.",e);
        }
        if(exception!=null) {
            dumpFolderData = true;
            message.append("Creation of folder '").append(path)
                    .append("' failed due to an exception in '")
                    .append(elapsed).append("' ms. ");
        } else if(IOTools.DIRECTORY_CREATED==result) {
            message.append("Directory '").append(path).append("' created in '")
                    .append(elapsed).append("' ms.");
        } else if(IOTools.DIRECTORY_ALREDY_EXISTS==result) {
            message.append("Directory '").append(path)
                   .append("' already existed in '").append(elapsed)
                   .append("' ms.");
        } else if(IOTools.DIRECTORY_CREATION_FAILED_BUT_EXISTS==result) {
            message.append("Directory '").append(path)
                   .append("' creation failed in '").append(elapsed)
                   .append("' ms but the folder does exist.");
        } else {
            dumpFolderData = true;
            message.append("Creation of folder '").append(path)
                    .append("' failed due to an uknown result '")
                    .append(result).append("' in '")
                    .append(elapsed).append("' ms. ");
        }

        if(dumpFolderData) {
            if(isFolderNull) {
                message.append("Folder is null.");
            } else {
                message.append("Dumping folder data: exists '")
                        .append(folder.exists())
                        .append("', isFile '").append(folder.isFile())
                        .append("', isDirectory '").append(folder.isDirectory())
                        .append("'.");
            }
        }
        return message.toString();
    }

    /**
     * This method return true if it's worth building the log message to show after
     * the creation of the directory where to store the user's pictures.
     * A message will be logged in any case if an exception has been caught upon
     * user directory creation or, if no exception are thrown, in the case the
     * log level is lower than/equal to trace.
     * In the second case the message il logged at trace level.
     *
     * @param exception the exception occurred during the creation of the folder
     * if any.
     *
     * @return true if a log message will be logged, false otherwise.
     */
    private boolean isLogForFolderCreationEnabled(Throwable exception) {
        return log.isTraceEnabled() ||
               exception!=null;
    }

    /**
     * Check if the given string is not null and not empty
     * @param inputParameter it's the input parameter we want to check
     * @param errorMessage it's the string used to throw the IllegalArgumentException
     * in the case the input parameter is not valid
     *
     * @throws IllegalArgumentException in the case the input string is not valid
     *
     */
    private void checkStringNotNull(String inputParameter,
                                    String errorMessage) {
        if (inputParameter == null || inputParameter.length() == 0) {
            throw new IllegalArgumentException(String.format(errorMessage, inputParameter));
        }
    }

}
