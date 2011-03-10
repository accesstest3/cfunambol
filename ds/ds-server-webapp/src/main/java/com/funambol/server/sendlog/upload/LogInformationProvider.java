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
import java.io.FilenameFilter;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

import com.funambol.server.sendlog.Constants;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * It's the provider used when uploading a log file.
 *
 * @version $Id: LogInformationProvider.java 36231 2010-11-23 14:57:08Z filmac $
 */
public class LogInformationProvider implements Constants {

     private static final String HELPER_CHAR = "-";
     private static final String DEVICE_ID_CHARACTERS_TO_BE_ESCAPED =
                                                              "[<>\\\\/:\"|?*]";

     private static final Pattern DEVICE_ID_PATTERN = Pattern.compile(DEVICE_ID_CHARACTERS_TO_BE_ESCAPED);

    // ----------------------------------------------------------- Instance Data
    InputStream inputStream = null;
    String username    = null;
    String deviceId    = null;
    String contentType = null;
    String baseDir     = null;
    long   maxFileSize = -1L;
    
    long   time        = System.currentTimeMillis();

    protected final static FunambolLogger log =
        FunambolLoggerFactory.getLogger(UPLOAD_SENDLOG_LOGGER_NAME);

    // ------------------------------------------------------------ Constructors

    /**
     * Builds a LogInformationProvider with the given data
     * @param inputStream the content of the log
     * @param username is the name of the user that is uploading the log
     * @param deviceId is the id of the device where the log has been recorded
     * @param contentType is the content type of the file that the client 
     *        is going to upload
     * @param baseDir is the directory that will hold the upload files
     * @param maxFileSize is the max size of the log storable
     * @throws UploadFileException if an error occurs
     */
    public LogInformationProvider(InputStream inputStream,
                                  String username,
                                  String deviceId,
                                  String contentType,
                                  String baseDir,
                                  long   maxFileSize)
    throws UploadFileException {

        if (inputStream == null) {
            throw new UploadFileException(ACCESS_REQUEST_AS_INPUT_STREAM_ERRMSG);
        }

        this.inputStream = inputStream;
        this.username    = username;
        this.deviceId    = escape(deviceId);
        this.contentType = contentType;
        this.baseDir     = baseDir;
        this.maxFileSize = maxFileSize;

        if(log.isTraceEnabled()) {
            log.trace("Handling file upload for user '"+username+
                      "' and device '"+deviceId+"' at time '"+time+
                      "', Content-Type header value is '"+contentType+
                      "', base dir is '"+baseDir+"' and " +
                      ", max file size is '" + maxFileSize +"' .");
        }
    }

    // ---------------------------------------------------------- Public Methods

    public String getFileName() throws UploadFileException {
        return buildFileName(INCOMING_FILE_EXTENSION);
    }

    public String getTargetFileName() throws UploadFileException {
        if(log.isTraceEnabled()) {
            log.trace("Extracting request information needed to handle file upload");
        }

        return createTargetFileName();

    }

    /**
     * Removes the tmp target files under username dir that starts with the
     * given device id.
     */
    public void removeTmpTargetFiles() {
        File userdir = getLogFilesDirectory();
        if (userdir.exists()) {

            FilenameFilter filenameFilter = new FilenameFilter() {

                public boolean accept(File dir, String name) {
                    return name.startsWith(deviceId) && name.endsWith(".tmp");
                }
            };

            boolean deleted = true;
            File[] tmpFiles = userdir.listFiles(filenameFilter);
            for (int i=0; i< tmpFiles.length; i++) {
                deleted = tmpFiles[i].delete();
                if (!deleted) {
                    tmpFiles[i].deleteOnExit();
                    log.warn("It was not possible to remove the '"+tmpFiles[i]+"'.");
                }
            }
        }
    }

    public String getContentType() {
        return contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public long getMaxFileSize() {
        return maxFileSize;
    }

    public int getBufferSize() {
        return -1;
    }

    // --------------------------------------------------------- Private methods

    /**
     * This method creates the file name where data read from the http request
     * are stored.
     *
     * @return the name of the target file
     */
    private String createTargetFileName() throws UploadFileException {
        String extension = guessExtension(contentType);

        if(log.isTraceEnabled()) {
            log.trace("Creating file name for incoming upload requested from user' "+
                      username+"' and device '"+deviceId+
                      "' with time '"+time+"' and extension '"+extension+"'.");
        }

        File directory  = getLogFilesDirectory();

        File logFile    = createLogFileFor(directory, extension);

        return logFile.getAbsolutePath();
    }

    private File createLogFileFor(File directory, String extension) {
    
        if(directory==null || directory.isFile()) {
            throw new IllegalArgumentException(NULL_DIRECTORY_TARGET_FILENAME_ERRMSG);
        }
        if(extension==null || extension.length()==0) {
            throw new IllegalArgumentException(NULL_EXTENSION_TARGET_FILENAME_ERRMSG);
        }

        String fileName = buildFileName(extension);
        if(log.isTraceEnabled()) {
            log.trace("Created new file name '"+fileName+"' for the file to upload.");
        }
        return new File(directory,fileName);
    }

    /**
     * Returns the directory based on username that will contain the log files.
     *
     * @return the directory that will contain the logs file
     */
    private File getLogFilesDirectory() {
        if(username==null || username.length()==0) {
            throw new IllegalArgumentException(NULL_USERNAME_TARGET_FILENAME_ERRMSG);
        }

        return new File(baseDir, username);
    }

    /**
     * builds the name of the file used to store
     * @param deviceid the deviceid id
     * @param extension the desired extension of the file (i.e. ".log")
     * @param time it's a long representing the time the file upload has started
     *
     * @return the target name of the file
     */
    private String buildFileName(String extension) {

        if(deviceId==null || deviceId.length()==0) {
            throw new IllegalArgumentException(NULL_DEVICEID_TARGET_FILENAME_ERRMSG);
        }

        //
        // DateFormats are not synchronized, and using them in a static context
        // can yield some bugs. It's better to create separate format instances
        // for each thread.
        //
        final DateFormat dateFormatter = new SimpleDateFormat(DATE_PATTERN);

        StringBuilder builder = new StringBuilder(deviceId);
        builder.append('_')
               .append(dateFormatter.format(new Date(time)))
               .append(extension);
        return builder.toString();
    }

    /**
     * this method returns the extension of the target file name using the information
     * provided by the content type header
     * @param contentType the value of the Content-Type header sent by the client
     * @return the extention guessed using the content type
     * @throws com.funambol.services.sendlog.FileUploader.UploadFileException
     */
    private String guessExtension(String contentType) throws UploadFileException {
        String extension = null;
        if(MimeTypeDictionary.isCompressible(contentType)) {
            extension = DEFAULT_TARGET_FILEEXTENSION;
        } else if(MimeTypeDictionary.isExtensionKnown(contentType)){
            extension = MimeTypeDictionary.getExtension(contentType);
        } else {
            throw new UploadFileException("Unable to guess target file extension for Content-Type '"+contentType+"'.");
        }
        return addDotPrefix(extension);
    }

    private String addDotPrefix(String extension) {
        if(extension!=null && !extension.startsWith(DOT)) {
            return DOT+extension;
        }
        return extension;
    }

    /**
     * this method allows to escape characters not allowed in file name in
     * order to avoid that special characters contained in the device id causes
     * the creation of wrong paths
     * @param token the string to be escaped
     * @return the device id with the desired characters escaped
     */
    private String escape(String token) {

        if(token!=null && token.length()>0) {
            // Create a matcher with an input string
            Matcher matcher = DEVICE_ID_PATTERN.matcher(token);
            StringBuffer result = new StringBuffer();

            // Loop through and create a new String
            // with the replacements
            while(matcher.find()) {
                matcher.appendReplacement(result, HELPER_CHAR);
            }
            // Add the last segment of input to
            // the new String
            matcher.appendTail(result);
            return result.toString();
        }
        return null;

    }

}
