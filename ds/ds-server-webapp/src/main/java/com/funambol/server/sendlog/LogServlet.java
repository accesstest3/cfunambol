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

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.lang.StringUtils;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.framework.tools.StringTools;
import com.funambol.server.config.Configuration;

import com.funambol.server.sendlog.authentication.HttpBasicAuthenticator;
import com.funambol.server.sendlog.authentication.UnauthorizedException;
import com.funambol.server.sendlog.upload.FileUploader;
import com.funambol.server.sendlog.upload.MimeTypeDictionary;
import com.funambol.server.sendlog.upload.LogInformationProvider;
import com.funambol.server.sendlog.upload.UploadFileException;

/**
 * Receives the HTTP request for the send log.
 *
 * @version $Id: LogServlet.java 36094 2010-10-20 15:34:59Z luigiafassina $
 */
public class LogServlet extends HttpServlet implements Constants {

    // ------------------------------------------------------------ Private Data
    private static FunambolLogger log =
        FunambolLoggerFactory.getLogger(SENDLOG_LOGGER_NAME);

    private long   clientsLogMaxSize = -1L;
    private String clientsLogBaseDir = null;

    private String credentialHeader = null;
    private String deviceId         = null;
    private String contentType      = null;

    //-------------------------------------------------------- Protected methods

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    protected void processRequest(HttpServletRequest request,
                                  HttpServletResponse response)
    throws ServletException, IOException {

        // Checks mandatory headers:
        if (!validateMandatoryHeaders(request, response)) {
            //
            // If the mandatory headers are missed or in a bad format, it's
            // not possible to continue..
            //
            return;
        }

        // Authorizes and authenticates the user
        HttpBasicAuthenticator authenticator = null;
        try {

            if(log.isTraceEnabled()) {
                log.trace("Authenticating the incoming request..");
            }
            authenticator =
                new HttpBasicAuthenticator(credentialHeader, deviceId);

            authenticator.authenticate();

        } catch (UnauthorizedException e) {
            log.error(AUTHENTICATE_REQUEST_ERRMSG, e);
            handleError(response, e);
            return;
        } catch (Throwable e) {
            log.error(AUTHENTICATE_REQUEST_UNEXPECTED_ERRMSG, e);
            handleError(response, e);
            return;
        }

        InputStream inputStream = null;
        try {
            inputStream = request.getInputStream();
        } catch (IOException e) {
            log.error(ACCESS_REQUEST_AS_INPUT_STREAM_ERRMSG, e);
            handleError(response, e);
            return;
        }

        LogInformationProvider provider = null;
        FileUploader           uploader = null;
        try {
            if(log.isTraceEnabled()) {
                log.trace("Handling the incoming request..");
            }
            String username = authenticator.getUsername();
            provider = new LogInformationProvider(inputStream,
                                                  username,
                                                  deviceId,
                                                  contentType,
                                                  clientsLogBaseDir,
                                                  clientsLogMaxSize);

            uploader = new FileUploader(provider);

            uploader.uploadFile();

        } catch(UploadFileException e) {
            log.error(PROCESSING_UPLOAD_ERRMSG, e);
            handleError(response,e);
            return;
        } catch(Throwable e) {
            log.error(HANDLE_REQUEST_UNEXPECTED_ERRMSG, e);
            handleError(response,e);
            return;
        }

        PrintWriter writer = null;
        try {
            response.setContentType(MimeTypeDictionary.TEXT_PLAIN);
            response.setStatus(HttpServletResponse.SC_OK);
            writer = response.getWriter();
            writer.write(UPLOAD_COMPLETED_MSG);
            writer.flush();

        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws javax.servlet.ServletException
     * @throws java.io.IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    //----------------------------------------------------------- Public Methods

    /**
     * Returns a short description of the servlet.
     * @return
     */
    @Override
    public String getServletInfo() {
        return "Funambol Send log Servlet";
    }

    /**
     * Initializes the servlet
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException {
        String msg1 = "================================================================================";
        String sp   = "\n";
        String msg2 = "Funambol Send log started." ;

        Configuration configuration = Configuration.getConfiguration();
        try {
            configuration.configureLogging();
        } catch (Exception e) {
            //
            //
            System.out.println(msg1);
            System.out.println(sp);
            System.out.println(msg2);
            System.out.println(sp);
            System.out.println(msg1);
        }

        if (log.isInfoEnabled()) {
            log.info(msg1+sp);
            log.info(msg2+sp);
            log.info(msg1);
        }

        ServletConfig config = getServletConfig();

        String value = config.getInitParameter(CLIENTS_LOG_BASEDIR);
        if (StringUtils.isEmpty(value)) {
            String msg = "The servlet configuration parameter '"
                       + CLIENTS_LOG_BASEDIR
                       + "' cannot be empty (" + value + ")";
            log(msg);
            throw new ServletException(msg);
        }
        clientsLogBaseDir = value;
        if(log.isTraceEnabled()) {
            log.trace("Read value '"+value+"' for key '"+
                      CLIENTS_LOG_BASEDIR+"'.");
        }

        value = config.getInitParameter(CLIENTS_LOG_MAX_SIZE);
        if (StringUtils.isEmpty(value)) {
            String msg = "The servlet configuration parameter '"
                       + CLIENTS_LOG_MAX_SIZE + "' cannot be empty (" + value + ")" ;
            log(msg);
            throw new ServletException(msg);
        }
        try {
            clientsLogMaxSize = StringTools.converterStringSizeInBytes(value);
        } catch (NumberFormatException e) {
            String msg = "Unable to parse value '"
                       + value + "' for '" + CLIENTS_LOG_MAX_SIZE + "'";
            log(msg);
            throw new ServletException(msg);
        }
        if(log.isTraceEnabled()) {
            log.trace("Read value '"+clientsLogMaxSize+"' for key '"+
                      CLIENTS_LOG_MAX_SIZE+"'.");
        }
    }

    /**
     * Release the servlet resources
     */
    @Override
    public void destroy() {
        if(log.isTraceEnabled()) {
            log.trace("Funambol Send log servlet has been destroyed.");
        }
        Configuration configuration = Configuration.getConfiguration();
        configuration.release();
    }

    // --------------------------------------------------------- Private methods
    /**
     * Validates the mandatory headers.
     *
     * @param request the HTTP request to check
     * @param response the HTTP response to return in case of error
     * @return true if all mandatory headers are set in the right way
     */
    private boolean validateMandatoryHeaders(HttpServletRequest  request ,
                                             HttpServletResponse response) {

        credentialHeader = request.getHeader(HTTP_BASIC_AUTH_HEADER);
        if (StringUtils.isEmpty(credentialHeader)) {
            handleError(response, MISSING_AUTHORIZATION_HEADER_ERRMSG, null);
            return false;
        }
        if (!credentialHeader.startsWith(HEADER_AUTHORIZATION_PREFIX)) {
            handleError(response, AUTHORIZATION_BAD_FORMAT_ERRMSG, null);
            return false;
        }

        deviceId = request.getHeader(DEVICE_HEADER);
        if (StringUtils.isEmpty(deviceId)) {
            handleError(response, MISSING_DEVICEID_HEADER_ERRMSG, null);
            return false;
        }

        contentType = request.getHeader(CONTENT_TYPE_HEADER);
        if (StringUtils.isEmpty(contentType)) {
            handleError(response, MISSING_CONTENT_TYPE_HEADER_ERRMSG, null);
            return false;
        }
        if(!MimeTypeDictionary.isTextPlain(contentType) &&
           !MimeTypeDictionary.isCompressed(contentType)) {
            handleError(response, CONTENT_TYPE_NOT_VALID_ERRMSG, null);
            return false;
        }

        return true;
    }

    /**
     * This method allows to send the error to the client, assuming is an
     * application.
     *
     * @param response the response object used to communicate with the client
     * @param cause the exception that caused the server failure
     */
    private void handleError(HttpServletResponse response, Throwable cause) {
        int statusCode = response.SC_INTERNAL_SERVER_ERROR;

        if (cause instanceof UnauthorizedException) {
            statusCode = ((UnauthorizedException) cause).getHttpStatusCode();
        }
        if (cause instanceof UploadFileException) {
            statusCode = ((UploadFileException) cause).getHttpStatusCode();
        }

        try {
            if(log.isTraceEnabled()) {
                log.trace("Returning status code '"+statusCode+"' to the client.");
            }
            response.sendError(statusCode, cause.getMessage());
        } catch(IOException e) {
            log.error("An error occurred while notifing an error to the client.",e);
        }
    }

    /**
     * Handles errors conditions returning an appropriate content to the client.
     *
     * @param response the response object
     * @param msg the error message
     * @param t the cause
     */
    private void handleError(final HttpServletResponse response,
                             String                    msg     ,
                             final Throwable           t       ) {
        handleError(response, msg, HttpServletResponse.SC_BAD_REQUEST, t);
    }

    /**
     * Handles errors conditions returning an appropriate content to the client.
     *
     * @param response the response object
     * @param msg the error message
     * @param t the cause
     */
    private void handleError(final HttpServletResponse response,
                             String                    msg     ,
                             int                       status  ,
                             final Throwable           t       ) {

        if (msg == null) {
            msg = "";
        }
        if (t == null) {
            log.error(msg);
        } else {
            log.error(msg, t);
        }
        try {
            response.sendError(status, msg);
        } catch (IOException e) {
            log.error("Error sending a '" + status + "' code to the client", e);
        }
    }
}
