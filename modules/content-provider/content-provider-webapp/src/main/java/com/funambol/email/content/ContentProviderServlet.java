/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2008 Funambol, Inc.
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
package com.funambol.email.content;

import java.io.*;
import java.net.*;
import java.util.Iterator;
import java.util.List;

import javax.mail.Message;
import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.io.IOUtils;

import com.funambol.email.exception.EmailAccessException;
import com.funambol.email.exception.EntityException;
import com.funambol.email.items.manager.MessageParser;
import com.funambol.email.model.InternalPart;
import com.funambol.email.model.MailServerAccount;
import com.funambol.email.util.Utility;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;
import com.funambol.server.config.Configuration;

/**
 * Receives the HTTP request for the attachments.
 */
public class ContentProviderServlet extends HttpServlet {

    // --------------------------------------------------------------- Constants

    /**
     * The attachment request parameter used to specify the authorization token
     */
    public static final String PARAMETER_AUTH =  "AUTH";

    /**
     * The attachment request parameter used to specify the user name
     */
    public static final String PARAMETER_USER =  "USER";

    /**
     * The attachment request parameter used to specify the attachment index
     */
    public static final String PARAMETER_INDEX = "INDEX";

    /**
     * The attachment request optional parameter used to view a debug page
     */
    public static final String PARAMETER_DEBUG = "DEBUG";

    /**
     * The log name
     */
    public static final String LOGGER_NAME = "funambol.content-provider";

    // ------------------------------------------------------------ Private Data

    /**
     * The FunambolLogger
     */
    private static FunambolLogger log = FunambolLoggerFactory.getLogger(LOGGER_NAME);

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

        if (request.getParameter(PARAMETER_DEBUG) != null &&
                "yes".equals(request.getParameter(PARAMETER_DEBUG))) {
            printDebugPage(request, response);
            return;
        }

        replyAttachment(request, response);
        
    }

    /**
     *
     * @param request
     * @param response
     * @throws java.io.IOException
     */
    private void replyAttachment(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ContentProviderManager contentServiceManager = new ContentProviderManager();

        HttpSession session = request.getSession();
        
        try {
            if (log.isTraceEnabled()) {
                log.trace("Start Handling request.");
            }

            String authToken = request.getParameter(PARAMETER_AUTH);
            if (authToken == null || "".equals(authToken)) {
                printErrorPage(request, response, "The authorization parameter is empty", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Authorization token: " + authToken);
            }

            String username =  request.getParameter(PARAMETER_USER);
            if (username == null || "".equals(username)) {
                printErrorPage(request, response, "The user name parameter is empty", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("User name: " + username);
            }

            String attachIdx = request.getParameter(PARAMETER_INDEX);
            if (attachIdx == null || "".equals(attachIdx)) {
                printErrorPage(request, response, "The attachment index parameter is empty", null);
                return;
            }
            int attachmentIndex = 0;
            try {
                attachmentIndex = Integer.parseInt(attachIdx);
            } catch (NumberFormatException ex) {
                printErrorPage(request, response, "Parameter \"attachment index\" must be a valid number", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Attachment index: " + attachmentIndex);
            }

            MailServerAccount mailServerAccount = contentServiceManager.retrieveMailServerAccount(username);
            if (mailServerAccount == null) {
                printErrorPage(request, response, "No valid mail server account found for user '" + username + "'", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Retrieved MailServerAccount for user '" + username + "'");
            }

            String mailServerProtocol = mailServerAccount.getMailServer().getProtocol();
            if (mailServerProtocol == null || "".equals(mailServerProtocol)) {
                printErrorPage(request, response, "The mail server account protocol is not defined", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Mail server protocol: " + mailServerProtocol);
            }

            contentServiceManager.openConnection(mailServerAccount);

            String mailGUID = contentServiceManager.authorize(username, authToken);
            if (mailGUID == null || "".equals(mailGUID)) {
                printErrorPage(request, response, "Email retrieving is not authorized" +
                        " or the email is not present in the Inbox folder anymore.", null);
                return;
            }
            String messageid = Utility.getKeyPart(mailGUID,2);
            if (messageid == null || "".equals(messageid)) {
                printErrorPage(request, response, "Unable to retrieve the message id ", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Message ID: " + messageid);
            }

            Message message = contentServiceManager.getMessage(messageid);
            if (message == null) {
                printErrorPage(request, response, "The email is not present in " +
                        "the Inbox folder anymore (id "+ messageid +").", null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Retrieved mail from mail server with Message ID: " + messageid);
            }

            List partsList = MessageParser.getAllPartsOfMessage(message, false);
            if (partsList == null) {
                printErrorPage(request, response, "The email doesn't have attachments", null);
                return;
            }

            InternalPart part = getInternalPart(partsList, attachmentIndex);
            if (part == null) {
                printErrorPage(request, response, "The email doesn't have attachments" +
                        " with index " + attachmentIndex, null);
                return;
            }
            if (log.isTraceEnabled()) {
                log.trace("Retrieved part with index: " + attachmentIndex);
            }

            if (part == null || part.getDHandler() == null ||
                    part.getDHandler().getInputStream() == null) {
                printErrorPage(request, response, "Error while streaming the attachment.", null);
                return;
            }
            InputStream in = part.getDHandler().getInputStream();

            response.setContentType(ContentProviderUtil.createHttpContentType(part));
            response.setHeader("Content-Disposition", " filename=\"" + part.getFileName() + "\"");
            OutputStream out = response.getOutputStream();

            IOUtils.copy(in, out);
            out.flush();
            out.close();
            in.close();

        } catch (Exception ex) {
                printErrorPage(request, response, ex.toString(), ex);

        } finally {
            try {
                contentServiceManager.closeConnection();
                if (log.isTraceEnabled()) {
                    log.trace("Connection closed");
                }
            } catch (ContentProviderException ex) {
                log.error("Error closing connection ", ex);
            }
            if (log.isTraceEnabled()) {
                log.trace("End handling request.");
            }
            //
            // Since the session is not really useful, we force that a request is
            // served by a new session and that a session serves just one request.
            // In such way, we don't have useless sessions. As drawback for every
            // request a new session is created. 
            // Comparing advantages vs drawbacks, we prefer one session - one request.
            //
            session.invalidate();
        }
    }

    /**
     * Print an error page in the response
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param errorMessage The roor message to display
     * @param ex The Exception to display if not null
     * @throws java.io.IOException
     */
    private void printErrorPage(HttpServletRequest request,
            HttpServletResponse response,
            String errorMessage,
            Exception ex) throws IOException {

        if (ex == null) {
            log.error(errorMessage);
        } else {
            log.error(errorMessage, ex);
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Content Provider Error</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Error retrieving the attachment</h1>");
        out.println("<p><b>" + errorMessage + "</b></p>");
        if (ex != null) {
            out.println("<p>");
            ex.printStackTrace(out);
            out.println("</p>");
        }
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    /**
     * Print a debug page in the response
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws java.io.IOException
     */
    private void printDebugPage(HttpServletRequest request,
            HttpServletResponse response) throws IOException {

        ContentProviderManager contentServiceManager = new ContentProviderManager();

        PrintWriter out = response.getWriter();
        try {
            response.setContentType("text/html;charset=UTF-8");

            out.println("<html>");
            out.println("<head>");
            out.println("<title>Content Provider Errorr</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AttachmentProvider at " + request.getContextPath() + "</h1>");

            String authToken = request.getParameter(PARAMETER_AUTH);
            if (authToken == null || "".equals(authToken)) {
                out.println("<p>Empty authorization token name received</p>");
                return;
            }

            String username =  request.getParameter(PARAMETER_USER);
            if (username == null || "".equals(username)) {
                out.println("<p>Empty user name received</p>");
                return;
            }

            String attachIdx = request.getParameter(PARAMETER_INDEX);
            if (attachIdx == null || "".equals(attachIdx)) {
                out.println("<p>Empty Attachment id received</p>");
                return;
            }
            int attachmentIndex = 0;
            try {
                attachmentIndex = Integer.parseInt(attachIdx);
            } catch (NumberFormatException ex) {
                out.println("<p>NumberFormatException: " + ex.toString() + "</p>");
                return;
            }

            out.println("<p>Authorization token: " + authToken + "</p>");
            out.println("<p>User name: " + username + "</p>");
            out.println("<p>Attachment index: " + attachIdx + "</p>");

            MailServerAccount mailServerAccount = contentServiceManager.retrieveMailServerAccount(username);
            if (mailServerAccount == null) {
                out.println("<p>Unable to retrieve the mail server account for user: " +
                        username + "</p>");
                return;
            }
            out.println("<p>Retrieved mail server account</p>");

            String mailServerProtocol = mailServerAccount.getMailServer().getProtocol();
            if (mailServerProtocol == null || "".equals(mailServerProtocol)) {
                out.println("<p>Invalid mail server protocol</p>");
                return;
            }
            out.println("<p>Mail server protocol: " + mailServerProtocol + "</p>");

            contentServiceManager.openConnection(mailServerAccount);
            out.println("<p>Connection opened</p>");

            String mailGUID = contentServiceManager.authorize(username, authToken);
            if (mailGUID == null) {
                out.println("<p>Request not authorized</p>");
                return;
            }
            String messageid = Utility.getKeyPart(mailGUID,2);
            if (messageid == null || "".equals(messageid)) {
                out.println("<p>Unable to retrieve the message id from the GUID</p>");
                return;
            }
            out.println("<p>Mail message id: " + messageid +"</p>");

            Message message = contentServiceManager.getMessage(messageid);
            if (message == null) {
                out.println("<p>Unable to retrieve the mail message</p>");
                return;
            }
            out.println("<p>Mail message retrieved</p>");

            List partsList = MessageParser.getAllPartsOfMessage(message, false);
            if (partsList != null && partsList.size() > attachmentIndex) {
                InternalPart part = (InternalPart) partsList.get(attachmentIndex);

                if (part == null || part.getDHandler() == null ||
                        part.getDHandler().getInputStream() == null) {
                    out.println("<p>Unable to retrieve the mail part</p>");
                    return;
                }
            }
        } catch (EmailAccessException ex) {
            out.println("<p>EmailAccessException: " + ex.toString()+"</p>");
        } catch (EntityException ex) {
            out.println("<p>EntityException: ");
            ex.printStackTrace(out);
            out.println("</p>");
        } catch (Exception ex) {
            out.println("<p>Exception: " + ex.toString()+"</p>");
        } catch (Throwable ex) {
            out.println("<p>Throwable: " + ex.toString()+"</p>");
        } finally {
            try {
                contentServiceManager.closeConnection();
            } catch (ContentProviderException ex) {
                out.println("<p>AttachmentException: " + ex.toString()+"</p>");
            }
            out.println("<p>Connection closed</p>");
            out.println("</body>");
            out.println("</html>");
            out.flush();
            out.close();
            
            //
            // Since the session is not really useful, we force that a request is
            // served by a new session and that a session serves just one request.
            // In such way, we don't have useless sessions. As drawback for every
            // request a new session is created. 
            // Comparing advantages vs drawbacks, we prefer one session - one request.
            //
            request.getSession().invalidate();            
        }
    }

    /**
     * Retrieve the internal part from the list, given the index
     * @param partsList
     * @param attachmentIndex
     * @return The Internal Part with index attachmentIndex or null if not found
     */
    private InternalPart getInternalPart(List partsList, int attachmentIndex) {
        // Since the attachmentIndex not always correspond to the index int the
        // list we have to iterate the list and compare the index instead of using:
        // InternalPart part = (InternalPart) partsList.get(attachmentIndex);

        Iterator values = partsList.iterator();
        while (values.hasNext()) {
            InternalPart part = (InternalPart)values.next();

            if (part.getIndex() == attachmentIndex) {
                return part;
            }
        }
        return null;
    }


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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

    /**
     * Returns a short description of the servlet.
     * @return
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    /**
     * Initializes the servlet
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init() throws ServletException {
        String msg1 = "================================================================================";
        String sp   = "\n";
        String msg2 = "Funambol Content Provider Server started." ;

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
    }

    /**
     * Release the servlet resources
     */
    @Override
    public void destroy() {
        String msg1 = "================================================================================";
        String sp   = "\n";
        String msg2 = "Funambol Content Provider Server stopped." ;
        if (log.isInfoEnabled()) {
            log.info(msg1+sp);
            log.info(msg2+sp);
            log.info(msg1);
        }

        Configuration configuration = Configuration.getConfiguration();
        configuration.release();
    }

    // </editor-fold>
}
