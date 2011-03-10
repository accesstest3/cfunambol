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


package com.funambol.server.admin.ws;

import java.io.*;
import java.lang.Class;
import java.lang.ClassLoader;

import javax.servlet.*;
import javax.servlet.http.*;

import com.funambol.framework.logging.FunambolLogger;
import com.funambol.framework.logging.FunambolLoggerFactory;

/**
 * This servlet allows the downloading of a class as found by the servlet's
 * class loader. It can be used to remotely download classes deployed on the
 * server.
 * <p>
 * It accepts the following parameters:
 * <table>
 * <tr>
 * <td>class</td>
 * <td>the class to download in the dotted or slashed form and optionally ended
 *     by .class</td>
 * </tr>
 * </table>
 *
 *
 *
 *  @version $Id: ClassDownloadServlet.java,v 1.2 2008-06-14 18:13:58 nichele Exp $
 *
 */
public final class ClassDownloadServlet
extends javax.servlet.http.HttpServlet {

    // --------------------------------------------------------------- Constants

    public static final String LOG_NAME = "server.classdownload";

    // ------------------------------------------------------------ Private data

    private FunambolLogger log = FunambolLoggerFactory.getLogger(LOG_NAME);

    // ---------------------------------------------------------- Public methods

    public void service(final HttpServletRequest  request,
                        final HttpServletResponse response)
    throws ServletException, IOException {
        if (log.isTraceEnabled()) {
            log.trace(request.toString());
            log.trace(request.getRequestURI());
        }

        String className = request.getPathInfo();

        if (className == null) {
            className = "";
        } else {
            //
            // skip the initial '/'
            //
            className = className.substring(1);
        }

        if (log.isTraceEnabled()) {
            log.trace("Requesting class: " + className);
        }

        //
        // First of all normalize the class name in order to be retrieved
        // by getResourceAsStream()
        //
        className = className.trim();
        if (className.endsWith(".class")) {
            int l = className.length();

            if (l>6) {
                className = className.substring(0, l-6);
            } else {
                className = "";
            }
        }
        className = className.replace('.', '/');
        className = className + ".class";

        if (log.isTraceEnabled()) {
            log.trace("Looking for class: " + className);
        }

        ClassLoader cl = this.getClass().getClassLoader();

        InputStream is = cl.getResourceAsStream(className);

        if (is == null) {
            if (log.isTraceEnabled()) {
                log.trace("Class '" + className + "' not found");
            }
            //
            // Class not found!
            //
            response.sendError(response.SC_NOT_FOUND, className + " not found");
            return;
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Class '" + className + "' found");
            }
        }

        byte[] buf = new byte[4096];
        int i = 0;
        OutputStream os = null;
        try {
            os = response.getOutputStream();

            while ((i = is.read(buf)) > 0) {
                os.write(buf, 0, i);
            }
        } catch (IOException e) {
            log.error("Error processing the request", e);
        } finally {
            if (os != null) {
                os.close();
            }
            is.close();
        }
    }

    // --------------------------------------------------------- Private methods
}


