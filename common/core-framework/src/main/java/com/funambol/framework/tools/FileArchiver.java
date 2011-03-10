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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * It's an utility class you can use to zip recursively a directory tree within
 * a zip archive.
 *
 * @version $Id$
 */
public class FileArchiver {
    
    //---------------------------------------------------------------- Constants
    public static final String PATH_SEPARATOR   = "/";
    public static final String EMPTY_PATH       = "";
    protected final static String LOGGER_NAME   = "filezipper";
    //------------------------------------------------------------ Instance Data
    String destinationFilename                     = null;
    String sourceFilename                          = null;
    File   sourceFile                              = null;
    File   destinationFile                         = null;
    private FileFilter filter                      = null;
    boolean recursively                            = false;
    boolean includeRoot                            = false;
    private long elapsedTime                       = -1L;
    private long numberOfArchivedFiles             = 0;
    private long numberOfArchivedDirectories       = 0;
    private long numberOfSkippedFiles              = 0;
    private long numberOfSkippedDirectories        = 0;

    List<ArchivationEventHandler> eventHandlerList = null;

    //------------------------------------------------------------- Constructors

    /**
     * builds a new FileArchiver that will archive recursively all the directory tree
     * root in the file with the given name into the archive with the given name
     * @param source it's the name of the root of the tree that this zipper will
     * compress
     * @param dest is the name of the archive that will contain all the files and
     * directories
     */
    public FileArchiver(String source, String dest) {
        this(source, dest, false, false);
    }

    /**
     * builds a new FileArchiver that will archive all the directory tree root
     * in the file with the given name into the archive with the given name
     * @param source it's the name of the folder or file from which
     * @param dest it's the name of the archive that will contain data
     * @param recursively if true estates that the compression must be performed
     * recursively
     * @param includeRoot if true estates that the root fulder must be included
     * in the compression process
     */
    public FileArchiver(String source,
                        String dest,
                        boolean recursively,
                        boolean includeRoot) {

        this.sourceFilename      = source;
        this.destinationFilename = dest;
        this.sourceFile          = new File(sourceFilename);
        this.destinationFile     = new File(destinationFilename);
        this.recursively         = recursively;
        this.includeRoot         = includeRoot;
    }

    /**
     * builds a new FileArchiver that will archive all the directory tree root
     * in the given source file into the archive rapresented by the given file
     * @param sourceFile it's the file object representing the folder or file
     * from that will be archived
     * @param destFile it's the name of the archive that will contain data
     * @param recursively if true estates that the compression must be performed
     * recursively
     * @param includeRoot if true estates that the root folder must be included
     * in the compression process
     */
    public FileArchiver(File sourceFile,
                        File destFile,
                        boolean recursively,
                        boolean includeRoot) {

        this.sourceFilename         = sourceFile.getAbsolutePath();
        this.destinationFilename    = destFile.getAbsolutePath();
        this.sourceFile             = sourceFile;
        this.destinationFile        = destFile;
        this.recursively            = recursively;
        this.includeRoot            = includeRoot;
    }

    //----------------------------------------------------------- Public Methods

    /**
     * Compresses the tree recursively saving it in the destination file.
     *
     * @throws FileArchiverException
     */
    public void compress() throws FileArchiverException {

        if ( !sourceFile.exists() ||
             (sourceFile.listFiles().length == 0 && !includeRoot)) {

            throw new FileArchiverException(
                "Unable to compress the source file '" + sourceFile +
                "' since it does not exist or is empty.");
        }

        FileOutputStream outputStream = null;

        try {

            outputStream = new FileOutputStream(destinationFile, false);

            long startedAt = System.currentTimeMillis();
            internalCompress(outputStream);
            elapsedTime = System.currentTimeMillis() - startedAt;

        } catch(FileNotFoundException e) {
            throw new FileArchiverException(
                "Unable to open the destination file '"+destinationFilename+"'");
        } finally {
            if(outputStream!=null) {
                try {
                    outputStream.flush();
                    outputStream.close();
                } catch(IOException e) {
                    // do nothing..
                }
            }
        }
    }

    /**
     * @return the elapsedTime the compression took
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @return the numberOfArchivedFiles
     */
    public long getNumberOfArchivedFiles() {
        return numberOfArchivedFiles;
    }

    /**
     * @return the numberOfArchivedDirectories
     */
    public long getNumberOfArchivedDirectories() {
        return numberOfArchivedDirectories;
    }

    /**
     * @return the numberOfSkippedFiles
     */
    public long getNumberOfSkippedFiles() {
        return numberOfSkippedFiles;
    }

    /**
     * @return the numberOfSkippedDirectories
     */
    public long getNumberOfSkippedDirectories() {
        return numberOfSkippedDirectories;
    }

    /**
     * @return the filter
     */
    public FileFilter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(FileFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("source folder",this.sourceFilename);
        builder.append("archive file",this.destinationFilename);
        builder.append("number of archived files",this.numberOfArchivedFiles);
        builder.append("number of archived directories",this.numberOfArchivedDirectories);
        builder.append("number of skipped files", this.numberOfSkippedFiles);
        builder.append("number of skipped directories", this.numberOfSkippedDirectories);
        builder.append("elapsed time",this.elapsedTime);
        return builder.toString();
    }

    /**
     * Bound the given handlers to this FileArchiver
     * @param handlers the handlers that will be invoked when events have to be
     * triggered
     */
    public void addEventHandler(ArchivationEventHandler...handlers) {
        if(this.eventHandlerList == null) {
            this.eventHandlerList = new ArrayList<ArchivationEventHandler>();
        }
        eventHandlerList.addAll(Arrays.asList(handlers));
    }

    /**
     * remove all the handlers bound to this FileArchiver
     */
    public void removeHandlers() {
        if(this.eventHandlerList!=null) {
            this.eventHandlerList.clear();
        }
    }

    //-------------------------------------------------------- Protected Methods
    protected void directoryAdded(String path, File sourceFile) {
        this.numberOfArchivedDirectories++;
      
        if(externalHandlersConfigured()) {
            for(ArchivationEventHandler handler:eventHandlerList) {
                handler.directoryAdded(path, sourceFile);
            }
        }
    }

    protected void fileAdded(String path, File sourceFile, long size) {
        this.numberOfArchivedFiles++;
       
        if(externalHandlersConfigured()) {
            for(ArchivationEventHandler handler:eventHandlerList) {
                handler.fileAdded(path, sourceFile, size);
            }
        }
    }

    protected void directorySkipped(File sourceFile, String path) {
        this.numberOfSkippedDirectories++;
        
        if(externalHandlersConfigured()) {
            for(ArchivationEventHandler handler:eventHandlerList) {
                handler.directorySkipped(path, sourceFile);
            }
        }
    }

    protected void fileSkipped(File sourceFile, String path) {
        this.numberOfSkippedFiles++;
        
        if(externalHandlersConfigured()) {
            for(ArchivationEventHandler handler:eventHandlerList) {
                handler.fileSkipped(path, sourceFile);
            }
        }
    }

    //---------------------------------------------------------- Private Methods
    private boolean externalHandlersConfigured() {
        return eventHandlerList != null && !eventHandlerList.isEmpty();
    }

    private void internalCompress(FileOutputStream outputStream)
    throws FileArchiverException {

        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);

        if(sourceFile.exists()) {
            if(sourceFile.isFile()) {
                addFile(zipOutputStream,EMPTY_PATH,sourceFile);
            } else if(sourceFile.isDirectory()) {
                if(includeRoot) {
                    addDirectory(zipOutputStream,EMPTY_PATH,sourceFile);
                } else {
                    addDirectoryContent(zipOutputStream,EMPTY_PATH,sourceFile);
                }
            } else {
                throw new FileArchiverException("Unable to handle input file type.");
            }
        } else {
            throw new FileArchiverException("Unable to zip a not existing file");
        }

        try {
            zipOutputStream.flush();
            zipOutputStream.close();
        } catch(IOException e) {
            throw new FileArchiverException("Unable to close the zip destination file '"+destinationFilename+"'",e);
        }
    }

    private void addDirectory(ZipOutputStream zipOutputStream,
                              String path,
                              File sourceFile)
    throws FileArchiverException {

        String directoryName = buildName(sourceFile,path);

        if(!isFilterAppliable(sourceFile)){
            directorySkipped(sourceFile,path);
            return;
        }

        createEntry(zipOutputStream,directoryName);

        try {
            addDirectoryContent(zipOutputStream,directoryName, sourceFile);

            directoryAdded(directoryName,sourceFile);
        } finally {
            closeEntry(zipOutputStream,directoryName);
        }
    }

    private void addFile(ZipOutputStream zipOutputStream, 
                         String path,
                         File sourceFile)
    throws FileArchiverException {
        String realFileName = buildName(sourceFile,path);

        if(!isFilterAppliable(sourceFile)){
            fileSkipped(sourceFile,path);
            return;
        }

        createEntry(zipOutputStream, realFileName);
        try {
            addFileContent(zipOutputStream,path,sourceFile);
        } finally {
            closeEntry(zipOutputStream, realFileName);
        }
    }

    private void addDirectoryContent(ZipOutputStream zipOutputStream,
                                     String directoryName,
                                     File sourceFile)
    throws FileArchiverException {
        File[] files = sourceFile.listFiles();
        for(File file:files) {
            if(file.isFile()) {
                addFile(zipOutputStream,directoryName, file);
            } else if(recursively && file.isDirectory()) {
                if(recursively) {
                    addDirectory(zipOutputStream, 
                                 directoryName,
                                 file);
                }
            }
        }
    }

    private void createEntry(ZipOutputStream zipOutputStream, String entryName)
    throws FileArchiverException {
        ZipEntry entry   = new ZipEntry(entryName);
        try {
            zipOutputStream.putNextEntry(entry);
        } catch(IOException e) {
            throw new FileArchiverException("An error occurred adding entry '"+entryName+"' to the zip file.", e);
        }
    }

    private void closeEntry(ZipOutputStream zipOutputStream, String entryName)
    throws FileArchiverException {
        try {
            zipOutputStream.closeEntry();
        } catch(IOException e) {
            throw new FileArchiverException("An error occurred closing the entry '"+entryName+"'.",e);
        }
    }

    private void addFileContent(ZipOutputStream zipOutputStream,
                                String path,
                                File sourceFile)
    throws FileArchiverException {
        FileInputStream inputStream = null;
        String fileName = sourceFile.getName();
        try {
            inputStream = new FileInputStream(sourceFile);
        } catch(FileNotFoundException e) {
            closeInputStream(inputStream);
            throw new FileArchiverException("An error occurred opening file '"+fileName+"'",e);
        }

        StreamCopy copier = new StreamCopy(inputStream,
                                           zipOutputStream,
                                           Long.MAX_VALUE);

        try {
            long fileSize = copier.performCopy(true, false);
            fileAdded(path,sourceFile,fileSize);

        } catch(IOException e) {
            throw new FileArchiverException("An error occurred copy the file "+fileName+" into the archive.", e);
        }
    }

    private void closeInputStream(FileInputStream inputStream) {
        try {
            inputStream.close();
        } catch(IOException e) {
            
        }
    }

    private String buildName(File file,String...tokens) {
        StringBuilder builder = new StringBuilder();
        for(String token:tokens) {
            String cleanedToken = removePathSeparator(token);
            if(cleanedToken!= null && !"".equals(cleanedToken)){
                builder.append(cleanedToken);
                builder.append(PATH_SEPARATOR);
            }
        }
        builder.append(file.getName());
        if(file.isDirectory()) {
            builder.append(PATH_SEPARATOR);
        }

        return builder.toString();
    }

    private String removePathSeparator(String token) {
        if(token!=null && token.length()>0){
            // skipping the initial path separator if any
            int beginIndex = token.startsWith(PATH_SEPARATOR)?1:0;
            // skipping the ending path separator if any
            int endIndex   = token.endsWith(PATH_SEPARATOR)?token.length()-1:token.length();
            return token.substring(beginIndex, endIndex);
        }
        return token;
    }

    private boolean isFilterAppliable(File sourceFile) {
        if(getFilter()!=null) {
            return getFilter().accept(sourceFile);
        }
        return true;
    }

}
