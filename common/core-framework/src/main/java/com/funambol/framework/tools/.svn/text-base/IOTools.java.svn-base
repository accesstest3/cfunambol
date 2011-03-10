/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2003 - 2007 Funambol, Inc.
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

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.ZipInputStream;

/**
 * Container of utility methods for io access.
 *
 * @version $Id: IOTools.java,v 1.2 2007-11-28 11:13:24 nichele Exp $
 */
public class IOTools {

    // --------------------------------------------------------------- Constants
    protected static final ReentrantLock directoryCreationLock = new ReentrantLock();

    public final static short DIRECTORY_ALREDY_EXISTS               = 0;
    public final static short DIRECTORY_CREATED                     = 1;
    public final static short DIRECTORY_CREATION_FAILED_BUT_EXISTS  = 2;

    // ---------------------------------------------------------- Public methods

    /**
     * Reads a file into a byte array given its filename
     *
     * @param file the filename (as java.io.File)
     *
     * @return the content of the file as a byte array
     * @throws IOException if an error occurs
     *
     */
    static public byte[] readFileBytes(File file)
    throws IOException {
        FileInputStream fis = null;

        byte[] buf = new byte[(int)file.length()];
        try {
            fis = new FileInputStream(file);
            fis.read(buf);
            fis.close();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }

        return buf;
    }

    /**
     * Reads a file into a byte array given its filename
     *
     * @param filename the filename (as java.lang.String)
     *
     * @return the content of the file as a byte array
     * @throws IOException if an error occurs
     *
     */
    static public byte[] readFileBytes(String filename)
    throws IOException {
        return readFileBytes(new File(filename));
    }

    /**
     * Reads a file into a String given its filename
     *
     * @param file the filename (as java.io.File)
     *
     * @return the content of the file as a string
     * @throws IOException if an error occurs
     *
     */
    static public String readFileString(File file)
    throws IOException {
        return new String(readFileBytes(file));
    }

    /**
     * Reads a file into a String given its filename
     *
     * @param filename the filename (as java.lang.String)
     *
     * @return the content of the file as a string
     * @throws IOException if an error occurs
     *
     */
    static public String readFileString(String filename)
    throws IOException {
        return readFileString(new File(filename));
    }

    /**
     * Reads a file into a String given an input stream. It is responsibility
     * of the caller to close the stream when not used anymore.
     *
     * @param is the InputStream to read
     *
     * @return the content of the stream
     * @throws IOException if an error occurs
     *
     */
    static public String readFileString(InputStream is)
    throws IOException {
        StringBuffer sb = new StringBuffer();

        byte[] buf = new byte[512]; int n = 0;
        while ((n = is.read(buf)) >= 0) {
            sb.append(new String(buf, 0, n));
        }
        return sb.toString();
    }

    /**
     * Writes the given string to the file with the given name
     *
     * @param str the string to write
     * @param file the file name as a java.io.File
     *
     * @throws java.io.IOException
     */
    static public void writeFile(String str, File file)
    throws IOException {
        writeFile(str.getBytes(), file);
    }

    /**
     * Writes the given string to the file with the given name
     *
     * @param str the string to write
     * @param filename the file name as a java.lang.String
     *
     * @throws java.io.IOException
     */
    static public void writeFile(String str, String filename)
    throws IOException {
        writeFile(str.getBytes(), new File(filename));
    }

    /**
     * Writes the given bytes to the file with the given name
     *
     * @param buf the bytes to write
     * @param filename the file name as a java.lang.String
     *
     * @throws java.io.IOException
     */
    static public void writeFile(byte[] buf, String filename)
    throws IOException {
        writeFile(buf, new File(filename));
    }

    /**
     * Writes the given bytes to the file with the given name
     *
     * @param buf the bytes to write
     * @param file the file name as a java.io.File
     *
     * @throws java.io.IOException
     */
    static public void writeFile(byte[] buf, File file)
    throws IOException {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(buf);
            fos.close();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
    }

    /**
     * Returns a <i>FilenameFilter</i> that accepts only the files of the given
     * type (extension).
     *
     * @param type the type (the file extension) of the files to select. NULL
     *             means all files, the empty string means files without extension
     *             The filtering is case-insensitive
     *
     * @return the filter
     * @deprecated since v9.0 uses commons.io.filefilter.SuffixFileFilter
     */
    public static FilenameFilter getFileTypeFilter(String type) {
        return new FileTypeFilter(type);
    }

    /**
     * Reads the request body without to known the length of the data.
     *
     * @param in the request InputStream
     * @param bufferSize the initial size of the buffer
     * @return an array of bytes
     * @throws IOException in case of errors
     */
    public static byte[] readContent(final InputStream in, int bufferSize)
    throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        byte[] buf = new byte[bufferSize];

        int c = 0;
        int b = 0;
        while ((c < buf.length) && (b = in.read(buf, c, buf.length-c)) >= 0) {
            c+=b;
            if (c == bufferSize) {
                bout.write(buf);
                buf = new byte[bufferSize];
                c = 0;
            }
        }
        if (c != 0) {
            bout.write(buf, 0, c);
        }
        return bout.toByteArray();
    }
    
    /**
     * Reads a zip files.
     * 
     * @param sourceFile the zip file to read
     * @return the array of read bytes
     * @throws IOException if an error occurs
     */
    public static byte[] readZIPFile(File sourceFile) throws IOException {

        ZipInputStream zin = 
            new ZipInputStream(new FileInputStream(sourceFile));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        
        int len = 0;
        while(zin.getNextEntry() != null){
            while ((len=zin.read(buf)) > 0) {
                out.write(buf,0,len);
            }
        }

        out.close();
        zin.close();
        return out.toByteArray();
    }

    /**
     * This method tries to create a directory represented by the given File object
     * after having checked that it doesn't exist.
     * The creation is handled in a synchronized way using a ReentrantLock on the
     * directory creation.
     * The invoking thread tryed to acquire the lock waiting for the given timeout,
     * expressed as unist. If the acquiring fails, an exception is thrown.
     * If, the synchronized calls to the mkdirs method return false and the folder
     * doesn't exist yet, an exception is thrown.
     *
     *
     * @param folder the File object representing the directory to create
     * @param timeout it's the amount of time that the invoking thread is available
     * to wait trying to acquire the directory creation lock
     * @param unit it's the unit of time that defines the given timeout
     * @return 0, 1 or 2 to specify the result of the creation
     * @throws IOException if the given file object represents a file, if the creation
     * of the directory fails and the directory doesn't exist yet, if the
     * invoking thread was unable to acquire the lock or if the invoking thread
     * is interrupted
     */
    public static short createDirectoryOnRaceCondition(File folder,
                                                       long timeout,
                                                       TimeUnit unit)
    throws IOException {
        
        if (folder.exists()) {
            // if the resource is a file an exception is thrown
            if(folder.isFile()) {
               throw new IOException(folder.getPath() + " is a file.");
            }

            return DIRECTORY_ALREDY_EXISTS;

        } else {
            return createNotExistingDirectoryOnRaceCondition(folder,
                                                             timeout,
                                                             unit);
        }
    }

    /**
     * This method tries to create a directory represented by the given File object
     * assuming it doesn't exist.
     * The creation is handled in a synchronized way using a ReentrantLock on the
     * directory creation.
     * The invoking thread tryed to acquire the lock waiting for the given timeout,
     * expressed in the given unit. If the acquiring fails, an exception is thrown.
     * If, the synchronized calls to the mkdirs method return false and the folder
     * doesn't exist yet, an exception is thrown.
     *
     *
     * @param folder the File object representing the directory to create
     * @param timeout it's the amount of time that the invoking thread is available
     * to wait trying to acquire the directory creation lock
     * @param unit it's the unit of time that defines the given timeout
     * @return 0, 1 or 2 to specify the result of the creation
     * @throws IOException if the creation  of the directory fails and the
     * directory doesn't exist yet, if the invoking thread was unable to acquire
     * the lock or if the invoking thread is interrupted
     */
    public static short createNotExistingDirectoryOnRaceCondition(File folder,
                                                                 long timeout,
                                                                 TimeUnit unit)
    throws IOException {
            String  folderPath   = folder.getAbsolutePath();

            
            boolean success = createNotExistingDirectorySynchronized(folder,
                                                                     timeout,
                                                                     unit);
            boolean exists = folder.exists();
            // Success...
            if(success) {
                return DIRECTORY_CREATED;
            // ...creation failed but the directory exist..
            // it's a (weird) success scenario..
            } else if(exists) {
                return DIRECTORY_CREATION_FAILED_BUT_EXISTS;
                //..no way to create the directory, raising an exception
            } else {
                throw new IOException("A new directory at "+
                                      folderPath+ " could not be created.");
            }
    }

    /**
     * Remove the directory represented by the given path recursively
     * @param directoryName the path of the directory to be erased
     * @throws IOException if any error occurs deleting the given directory
     */
    public static void removeDir(String directoryName) throws IOException {
        File directory = new File(directoryName);
        if(directory.exists()&&directory.isDirectory()) {
            if(!deleteDirectory(directory)) {
                throw new IOException("Unable to remove directory ["+directoryName+"].");
            }
        }
    }

    /**
     * this method can be used to normalize a path, i.e. removing properly from
     * the path string like ".." and ".".
     *
     * @param rootPath the path to normalize
     *
     * @return the normalized path
     *
     * @throws IOException if any error occurs
     */
    public static String normalize(String rootPath) throws IOException {
        if(rootPath!=null && rootPath.length()>0) {
            File toNormalize = new File(rootPath);
            return toNormalize.getCanonicalPath();
        }
        return rootPath;
    }


    /**
     * copy all the data contained in the input stream (up to 3M) into the given
     * output stream, flushing the output stream and closing both of them at the end
     *
     * @param source the input stream containing the data to copy
     * @param target the output stream where data will be copied to
     * @return the number of bytes copied
     * @throws IOException if any error occurs during the copy
     */
    public static long copyStream(InputStream source,
                                  OutputStream target) throws IOException {
        StreamCopy copier = new StreamCopy(source, target);
        return copier.performCopy();
    }

    /**
     * copy all the data contained in the input stream into the given output stream,
     * flushing the output stream and closing both of them at the end
     * @param source the input stream containing the data to copy
     * @param target the output stream where data will be copied to
     * @param maxDataTransferred the max number of bytes that will be copied, when
     * this value is reached the copy will be stopped
     * @return the number of bytes copied
     * @throws IOException if any error occurs during the copy
     */
    public static long copyStream(InputStream source,
                                  OutputStream target,
                                  long maxDataTransferred) throws IOException {
        StreamCopy copier = new StreamCopy(source, target,maxDataTransferred);
        return copier.performCopy();
    }

    /**
     * copy all the data contained in the input stream into the given output stream,
     * flushing the output stream and closing both of them at the end
     * @param source the input stream containing the data to copy
     * @param target the output stream where data will be copied to
     * @param maxDataTransferred the max number of bytes that will be copied, when
     * this value is reached the copy will be stopped
     * @param bufferSize is the size of the buffer of bytes used to perform the
     * copy
     * @return the number of bytes copied
     * @throws IOException if any error occurs during the copy
     */
    public static long copyStream(InputStream source,
                                  OutputStream target,
                                  long maxDataTransferred,
                                  int bufferSize) throws IOException {
        StreamCopy copier = new StreamCopy(source,
                                           target,
                                           maxDataTransferred,
                                           bufferSize);
        return copier.performCopy();
    }

   



    //-------------------------------------------------------- Protected Methods

   /**
    * This method tries to create a directory represented by the given File object
    * assuming it doesn't exist.
    * Keep in mind that an invokation of mkdirs of an existing directory return
    * false, so it's better to check if the directory exists in the case this
    * method return false.
    * The creation is handled in a synchronized way using a ReentrantLock on the
    * directory creation.
    * The invoking thread tryed to acquire the lock waiting for the given timeout,
    * expressed in the given unit. If the acquiring fails, an exception is thrown.
    *
    * @param folder the File object representing the directory to create
    * @param timeout it's the amount of time that the invoking thread is available
    * to wait trying to acquire the directory creation lock
    * @param unit it's the unit of time that defines the given timeout
    *
    * @return the same value returned by the underlying synchronized call to mkdirs.
    *
    * @throws IOException if the invoking thread was unable to acquire the lock
    * or if the invoking thread is interrupted
    *
    */


   protected static boolean createNotExistingDirectorySynchronized(File folder,
                                                                   long timeout,
                                                                   TimeUnit unit)
    throws IOException {
        boolean lockAcquired = false;
        try {
            lockAcquired = directoryCreationLock.tryLock(timeout, unit);
        } catch(InterruptedException e) {
            throw new IOException("Thread interrupted while acquiring the creation directory lock.");
        }

        if(!lockAcquired) {
            throw new IOException("Unable to acquire the lock needed to create " +
                                  "the desired directory in the given amount of time.");
        }

        try {
            return folder.mkdirs();
        } finally {
            if(directoryCreationLock.isHeldByCurrentThread()) {
                directoryCreationLock.unlock();
            }
        }
    }


   /**
    * this method allows to delete recursively all the files if any contained in
    * the file/directory represented by the given file object
    *
    * @param file the file/directory we want to delete recursively
    *
    * @return false if one of the resources contained in the given directory
    * cannot be deleted.
    */
   protected static boolean deleteDirectory(File file) {
       if(file.exists()) {
           if(file.isFile()) {
               return file.delete();
           } else if(file.isDirectory()) {
               File[] children = file.listFiles();
               boolean childrenDeleted = true;
               for(File child:children) {
                   childrenDeleted=childrenDeleted&&deleteDirectory(child);
                   if(!childrenDeleted) {
                       return false;
                   }
               }
               return childrenDeleted&&file.delete();
           }

       }
       return true;
   }

   /**
    * Removes recursively the tree of folders whose root is the input source folder.
    * Only files accepted by the input filter are deleted.
    * Since a filter is used to understand which files must be deleted, it may
    * happend that a folder still contains files after its content has been deleted.
    * So, after the removal of a directory, this method checks if the directory
    * is empty before deleting it.
    * If the input filter is null, then all the content will be deleted.
    * Not empty directories don't cause the method to return false.
    *
    * @param sourceFolder is the name of the folder that will be removed
    * @param filter is the filter used to understand which files must be removed
    *
    * @return false if one of the resources contained in the given directory
    * cannot be deleted (not empty directories won't cause false to be returned)
    */

    public static boolean deleteDirectory(String sourceFolder, FileFilter filter) {
        File folderToRemove = new File(sourceFolder);
        if(filter==null) {
            return deleteDirectory(folderToRemove);
        } else {
            return deleteDirectory(folderToRemove, filter);
        }
    }

   /**
    * Removes recursively the tree of folders whose root is the input source folder.
    * Only files accepted by the input filter are deleted.
    * Since a filter is used to understand which files must be deleted, it may
    * happend that a folder still contains files after its content has been deleted.
    * So, after the removal of a directory, this method checks if the directory
    * is empty before deleting it.
    * If the input filter is null, then all the content will be deleted.
    * Not empty directories don't cause the method to return false.
    *
    * @param folderToRemove is the name of the folder that will be removed
    * @param filter is the filter used to understand which files must be removed
    *
    * @return false if one of the resources contained in the given directory
    * cannot be deleted (not empty directories won't cause false to be returned)
    */
    public static boolean deleteDirectory(File folderToRemove, FileFilter filter) {
        if(folderToRemove.exists()) {
            if(folderToRemove.isFile()) {
                if(filter.accept(folderToRemove)) {
                    return folderToRemove.delete();
                }
                return true;
            } else if(folderToRemove.isDirectory()) {
               File[] children = folderToRemove.listFiles(filter);
               boolean childrenDeleted = true;
               for(File child:children) {
                   if(child.isFile()) {
                       // we don't need to apply the filter again, just deleting
                       // the file
                       childrenDeleted = childrenDeleted && child.delete();
                   } else {
                       childrenDeleted=childrenDeleted && deleteDirectory(child,filter);
                       if(!childrenDeleted) {
                           return false;
                       }
                   }
               }
               return childrenDeleted && 
                      ( !checkEmptyDirectory(folderToRemove) || folderToRemove.delete());

            }
        }
        return true;
    }

    /**
     * checks if the given file rapresents an empty directory
     * @param sourceFile the file object to check
     * @return true if the given file is an empty existing directory
     */
    public static boolean checkEmptyDirectory(File sourceFile) {
        if(checkDirectory(sourceFile)) {
            File[] files  = sourceFile.listFiles();
            return (files == null || files.length == 0);
        }
        return false;
    }

    /**
     * checks if the given file rapresents a directory
     * @param sourceFile the file object to check
     * @return true if the given file is a directory
     */
    public static boolean checkDirectory(File sourceFile) {
        return sourceFile != null &&
               sourceFile.exists() &&
               sourceFile.isDirectory();
    }

    /**
     * checks if the given file rapresents a file
     * @param sourceFile the file object to check
     * @return true if the given file is a file
     */
    public static boolean checkFile(File sourceFile) {
        return sourceFile != null &&
               sourceFile.exists() &&
               sourceFile.isFile();
    }

    /**
     * this method allows to zip recursively all the files and directory contained
     * in the input source folder, including it.
     * All items will be archived within the destination archive.
     * @param sourceFoldername it's the name of the input folder
     * @param destinationFilename it's the name of the destination archive
     * @param filter is the filter that establish which
     * @param eventHandler is the handler that will be notified when items are
     * added to the archive or skipped
     *
     * @throws FileArchiverException if any error occurs
     */
    public static void compressFolder(String sourceFoldername,
                                      String destinationFilename,
                                      FileFilter filter,
                                      ArchivationEventHandler eventHandler)
    throws FileArchiverException {

        FileArchiver zipper =
            new FileArchiver(sourceFoldername, destinationFilename, true, true);

        if(filter!=null) {
            zipper.setFilter(filter);
        }

        if(eventHandler!=null) {
            zipper.addEventHandler(eventHandler);
        }

        zipper.compress();
    }

    /**
     * This method can be used to list the files/directories contained into the
     * given directory, sorting them by last modification time
     * @param directory the directory we want to list the files contained into
     * @return an array of files contained in the given directory, a null value
     * if the given file object rapresents a file.
     */
    public static File[] listFilesSortingByTime(File directory) {
        if(directory==null) {
            throw new IllegalArgumentException("Unable to list directory files sorting them when the input directory is null.");
        }
        if(directory.exists() && directory.isDirectory()) {
            File[] children = directory.listFiles();
            if(children!=null && children.length>1) {
                Arrays.sort(children, new Comparator<File>() {

                    public int compare(File file1, File file2) {
                        return Long.valueOf(file1.lastModified()).compareTo(file2.lastModified());

                    }
                } );
            }
            return children;
        }
        return null;
    }


    // -------------------------------------------------------------------------

    /**
     * This class is a <i>FilenameFilter</i> that accepts only the files of the
     * specified type (extension). The filtering is case-insensitive.
     *
     * @deprecated since v9.0 uses commons.io.filefilter.SuffixFileFilter
     */
    public static class FileTypeFilter implements FilenameFilter {

        private String type;

        /**
         * Creates the filter on the given type.
         *
         * @param type the type (the file extension) of the files to select. NULL
         *             means all files, the empty string means files without
         *             extension. The filtering is case-insensitive
         */
        public FileTypeFilter(final String type) {
            this.type = type.toUpperCase();
        }

        public boolean accept(File dir, String name) {
            if (type == null) {
                return true;
            }

            if (type.length() == 0) {
                return (name.indexOf('.') < 0);
            }

            return (name.toUpperCase().endsWith(type));
        }
    }
}
