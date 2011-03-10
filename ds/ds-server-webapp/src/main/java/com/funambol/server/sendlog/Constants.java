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

package com.funambol.server.sendlog;

/**
 * This interface holds some constants that can be used
 *
 * @version $Id: Constants.java 36222 2010-11-22 11:03:11Z testa $
 */
public interface Constants {

    public static final String RESOURCE_TO_AUTHORIZE = "sendlog";
    public static final String CLIENTS_LOG_BASEDIR   = "CLIENTS_LOG_BASEDIR";
    public static final String CLIENTS_LOG_MAX_SIZE  = "CLIENTS_LOG_MAX_SIZE";
    public static final String DEVICE_HEADER         = "x-funambol-syncdeviceid";

    public final static String HEADER_AUTHORIZATION_PREFIX = "Basic ";
    public final static int    HEADER_AUTHORIZATION_LENGHT =
        HEADER_AUTHORIZATION_PREFIX.length();
    public final static String HTTP_BASIC_AUTH_HEADER = "Authorization";

    //
    // It's the amount of time that the invoking thread is available to wait
    // trying to acquire the directory creation lock
    //
    public static long CREATING_DIRECTORY_TIMEOUT = 500;

    public static final String CONTENT_TYPE_HEADER           = "Content-Type";
    public static final String DATE_PATTERN                  = "yyyyMMdd_HHmmss";
    public static final String INCOMING_FILE_EXTENSION       = ".log";
    public static final String DEFAULT_TARGET_FILEEXTENSION  = ".zip";
    public static final String DOT                           = ".";

    /**
     * The name of the loggers
     */
    public static final String SENDLOG_LOGGER_NAME = "server.sendlog";
    public static final String UPLOAD_SENDLOG_LOGGER_NAME =
        "server.sendlog.upload";
    public static final String AUTHENTICATOR_SENDLOG_LOGGER_NAME =
        "server.sendlog.authentication";

    // Log messages

    // used by servlet
    public static final String MISSING_AUTHORIZATION_HEADER_ERRMSG =
        "Missing mandatory '" + HTTP_BASIC_AUTH_HEADER +
        "' header while performing http basic like authentication.";
    public static final String MISSING_DEVICEID_HEADER_ERRMSG =
        "Missing mandatory '" + DEVICE_HEADER + "'.";
    public static final String MISSING_CONTENT_TYPE_HEADER_ERRMSG =
        "Missing mandatory '" + CONTENT_TYPE_HEADER + "'.";
    public static final String AUTHORIZATION_BAD_FORMAT_ERRMSG =
        "The Authorization header is not well formatted.";
    public static final String CONTENT_TYPE_NOT_VALID_ERRMSG =
        "The Content-Type is not valid.";
    public static final String AUTHENTICATE_REQUEST_ERRMSG =
        "An error occurred while authenticating incoming request.";
    public static final String AUTHENTICATE_REQUEST_UNEXPECTED_ERRMSG =
        "An unexpected error occurred while authenticating the incoming request.";
    public static final String HANDLE_REQUEST_UNEXPECTED_ERRMSG =
        "An unexpected error occurred while handling the incoming request.";
    public static final String PROCESSING_UPLOAD_ERRMSG =
        "An error occurred performing the upload of the log file.";
    public static final String UPLOAD_COMPLETED_MSG =
        "Client log has been successfully uploaded.";
    
    // used by authenticator
    public static final String DECODING_TOKEN_ERRMSG =
        "An error occurred decoding the authorization header.";

    // used by provider
    public static final String ACCESS_REQUEST_AS_INPUT_STREAM_ERRMSG =
        "Error accessing request body as input stream.";

    // uploader
    public static final String OUTPUT_STREAM_CREATION_ERRMSG =
        "An error occurred opening file output stream.";
    public static final String ZIP_FILE_CREATION_ERRMSG =
        "Error creating zip file.";
    public static final String NULL_PROVIDER_ERRMSG =
        "Unable to upload a file given a null upload information provider.";
    public static final String NULL_CONTENT_TYPE_ERRMSG =
        "Unable to create a proper output stream providing a null contentType.";
    public static final String NULL_USERNAME_TARGET_FILENAME_ERRMSG =
        "Unable to create target filename with null username.";
    public static final String NULL_DEVICEID_TARGET_FILENAME_ERRMSG =
        "Unable to create target filename with null device id.";
    public static final String NULL_DIRECTORY_TARGET_FILENAME_ERRMSG =
        "Unable to create target filename with invalid (is null or is a file) parent directory.";
    public static final String NULL_EXTENSION_TARGET_FILENAME_ERRMSG =
        "Unable to create target filename with null file extension.";
    public static final String NULL_TARGET_FILENAME_ERRMSG =
        "Unable to create a proper output stream providing a null target file name.";
    public static final String NULL_REAL_FILENAME_ERRMSG =
        "Unable to create a proper output stream providing a null real file name.";
    public static final String NULL_INPUTSTREAM_ERRMSG =
        "Unable to create a proper output stream providing a null input stream.";

}
