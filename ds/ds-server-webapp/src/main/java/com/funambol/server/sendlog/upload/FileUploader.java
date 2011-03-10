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
package com.funambol.server.sendlog.upload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.IOTools;
import com.funambol.framework.tools.StreamCopy.MaxDataTransferredException;

import com.funambol.server.sendlog.Constants;

/**
 * This class is responsible for performing the file upload when a post request
 * is sent without the multipart encoding.
 * That makes http upload simple for mobile devices.
 * When performing such an upload you'd better put parameters into headers
 * 
 * @version $Id: FileUploader.java 36132 2010-11-03 14:11:31Z luigiafassina $
 */
public class FileUploader implements Constants {

    //---------------------------------------------------------------- Constants
    private static final FunambolLogger log =
        FunambolLoggerFactory.getLogger(UPLOAD_SENDLOG_LOGGER_NAME);

    // ----------------------------------------------------------- Instance Data
    LogInformationProvider provider = null;

    // ------------------------------------------------------------ Constructors
    public FileUploader(LogInformationProvider provider) {
        if(provider == null) {
            throw new IllegalStateException(NULL_PROVIDER_ERRMSG);
        }
        this.provider = provider;
    }

    // ---------------------------------------------------------- Public methods
    /**
     * this method allows to perform the real upload of the file
     * @throws UploadFileException if an error occurs
     */
    public void uploadFile() throws UploadFileException {

        InputStream inputStream = provider.getInputStream();

        if(inputStream==null) {
            throw new IllegalArgumentException(NULL_INPUTSTREAM_ERRMSG);
        }

        // removes the tmp target files for the given username and device id
        provider.removeTmpTargetFiles();

        String targetFileName = provider.getTargetFileName();
        String tmpTargetFileName = targetFileName.concat(".tmp");

        // create temporary file
        File tmpTargetFile = new File(tmpTargetFileName);

        OutputStream outputStream = getOutputStream(provider.getContentType(),
                                                    provider.getFileName(),
                                                    tmpTargetFileName);

        try {

            long result = IOTools.copyStream(inputStream,
                                             outputStream,
                                             provider.getMaxFileSize(),
                                             provider.getBufferSize());
            if(log.isTraceEnabled()) {
                log.trace("Uploaded successfully ended with '"+result+"' number of bytes.");
            }

            // renames the temporary file in the uploaded file name
            boolean renamed = tmpTargetFile.renameTo(new File(targetFileName));
            if (!renamed) {
                throw new UploadFileException("An error occurred while renaming temporary uploaded file.");
            }

        } catch (MaxDataTransferredException e) {
            // renames the temporary file in the uploaded file name
            boolean renamed = tmpTargetFile.renameTo(new File(targetFileName));
            if (!renamed) {
                throw new UploadFileException("An error occurred while renaming temporary uploaded file when max number of bytes has been reached.");
            }
            
            throw new UploadFileException("An error occurred while uploading content when max number of bytes has been reached", e, 413);
        } catch(IOException e) {
            throw new UploadFileException("An error occurred while uploading content.", e);
        }
    }

    // --------------------------------------------------------- Private methods
    private void checkTargetFile(File outputFile, String targetFileName)
    throws UploadFileException {
        if (outputFile.exists()) {
            throw new UploadFileException("The target file '" + targetFileName + "' already exists.");
        }
        if (outputFile.isDirectory()) {
            throw new UploadFileException("The target file '" + targetFileName + "' is a directory.");
        }
    }

    /**
     * Returns a FileOutputStream starting from the given filename.
     *
     * @param targetFileName the file in which store the input stream
     * @return the FileOutputStream
     * @throws UploadFileException if an error occurs
     */
    private FileOutputStream getSimpleOutputStream(String targetFileName)
    throws UploadFileException {

        File outputFile = new File(targetFileName);

        createNotExistingDirectory(outputFile.getParentFile());

        checkTargetFile(outputFile, targetFileName);

        try {
            return new FileOutputStream(outputFile);
        } catch (FileNotFoundException e) {
            throw new UploadFileException(OUTPUT_STREAM_CREATION_ERRMSG, e);
        }
    }

    /**
     * Returns a ZipOutputStream starting from the given zipped filename.
     *
     * @param targetFileName the file in which store the input stream
     * @param realFilename the file name of the fipped file to read
     * @return the ZipOutputStream
     * @throws UploadFileException if an error occurs
     */
    private OutputStream getZippedOutputStream(String targetFilename, String realFilename)
    throws UploadFileException {

        FileOutputStream fileOutputStream = getSimpleOutputStream(targetFilename);

        ZipOutputStream zippedOutputStream = new ZipOutputStream(fileOutputStream);

        ZipEntry entry = new ZipEntry(realFilename);

        try {
            zippedOutputStream.putNextEntry(entry);
        } catch(IOException e) {
            throw new UploadFileException(ZIP_FILE_CREATION_ERRMSG, e);
        }

        return zippedOutputStream;
    }

    /**
     * Creates the given directory (with all the needed tree) if it doesn't exist
     * taking care of any race condition
     * @param parent the directory to be created
     * @throws UploadFileException if any error occurs creating the directory
     */
    private synchronized void createNotExistingDirectory(File parent)
    throws UploadFileException {
        try {
            IOTools.createNotExistingDirectoryOnRaceCondition(parent,
                                                              CREATING_DIRECTORY_TIMEOUT,
                                                              TimeUnit.MILLISECONDS);
        } catch(IOException e) {
            String errMsg = "An error occurred creating the directory '"+parent.getAbsolutePath()+"'.";
            log.error(errMsg,e);
            throw new UploadFileException(errMsg,e);
        }

    }

    /**
     * 
     * @param targetFilename the name of the target file
     * @return a FileOutputStream opened on the given name
     * @throws UploadFileException if an error occurs
     */
    private OutputStream getOutputStream(String contentType,
                                         String realFilename,
                                         String targetFilename)
    throws UploadFileException {

        if(contentType==null || contentType.length()==0) {
            throw new IllegalArgumentException(NULL_CONTENT_TYPE_ERRMSG);
        }

        if(targetFilename==null || targetFilename.length()==0) {
            throw new IllegalArgumentException(NULL_TARGET_FILENAME_ERRMSG);
        }

        if(MimeTypeDictionary.isTextPlain(contentType)) {
            if(realFilename==null || realFilename.length()==0) {
                throw new IllegalArgumentException(NULL_REAL_FILENAME_ERRMSG);
            }
            return getZippedOutputStream(targetFilename,realFilename);
        } else if (!MimeTypeDictionary.isCompressible(contentType)) {
            return getSimpleOutputStream(targetFilename);
        }

        throw new UploadFileException("Unable to handle upload attempt for Content-Type '"+contentType+"'.");
    }

}
