/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2006 - 2007 Funambol, Inc.
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
import java.io.IOException;

import java.util.StringTokenizer;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.funambol.framework.filter.LogicalClause;
import com.funambol.framework.filter.WhereClause;
import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.encryption.EncryptionTool;

import com.funambol.server.admin.UserManager;
import com.funambol.server.config.Configuration;

/**
 * Filter class used by servlet container to manage requests to resources under
 * application control that need authentication.
 *
 * @version $Id: LoginFilter.java,v 1.2 2008-06-23 13:22:15 piter_may Exp $
 *
 */
public class LoginFilter implements Filter {

    // --------------------------------------------------------------- Constants
    private static final String DELIMITER          = ",";
    private static final String PAGE_TO_FORWARD    = "login.jsp";

    //error message
    public static final String ERR_NOAUTH = "error_noauth";

    // ------------------------------------------------------------ Private Data

    /** Pages that must not be under filter control */
    private String skipPages = null;

    // ---------------------------------------------------------- Public Methods

    /**
     * The doFilter method is called every time a request is sent to application
     * resources, such as a jsp, an html file or stylesheet.
     * <p>
     * If requested resource is not under filter control (i.e. is contained in
     * skipPages), then request is passed to the next phase in the filter chain
     * and then eventually served.
     * <p>
     * This method checks if user informations are stored in current session. If
     * not, it redirects to the PAGE_TO_FORWARD which must stores user
     * informations in the current session and sets user as authenticated.
     * <p>
     * If user is authenticated, request is passed to the next phase in the
     * filter chain and then eventually served.
     *
     * @param req the request
     * @param res the response
     * @param chain the filter chain
     */
    public void doFilter(ServletRequest  req  ,
                         ServletResponse res  ,
                         FilterChain     chain)
    throws IOException, ServletException {

        HttpServletResponse response = (HttpServletResponse)res;
        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpSession         session  = request.getSession()    ;

        /**
         * Every time the request is called by the login.jsp, the user in the
         * session must be set to null in order to check the authentication of
         * the username and password set from web form.
         */
        String reqUri = request.getRequestURI();
        if (reqUri.endsWith("login.jsp")) {

            String logout = (String)request.getParameter("logout");
            if (logout != null && logout.equalsIgnoreCase("true")) {
                session.invalidate();
                session = request.getSession(true);
            }

            String error = (String)session.getAttribute(ERR_NOAUTH);

            //
            // If there is no authentication error message to show, then
            // invalidate the session to remove any objects (like contactDAO,
            // calendarDAO, ..) bound to it.
            //
            if (error != null && !error.equalsIgnoreCase("")) {
                session.setAttribute("user", null);
            } else {
                session.invalidate();
            }
        }

        //
        // Skip pages can be accessed freely
        //
        if (skipPages != null && !"".equals(skipPages)) {
            StringTokenizer sp = new StringTokenizer(skipPages, DELIMITER);
            String uri = request.getRequestURI();

            while (sp.hasMoreTokens()) {
                String spn = sp.nextToken().trim();
                if (uri.indexOf(spn) > 0) {
                    chain.doFilter(req, res);
                    return;
                }
            }
        }

        Sync4jUser user = (Sync4jUser)session.getAttribute("user");
        if (user == null) {
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            if (username == null && password == null) {
                request.getSession().setAttribute(ERR_NOAUTH, "");
                redirect(PAGE_TO_FORWARD, request, response);
                return;
            }

            try {
                UserManager um = Configuration.getConfiguration().getUserManager();

                WhereClause[] wcp = new WhereClause[2];
                wcp[0] = new WhereClause("username", new String[]{username},
                                         WhereClause.OPT_EQ, false);

                //
                // The password in the db is encrypted so we have to encrypt the
                // searched value
                //
                password = EncryptionTool.encrypt(password);

                wcp[1] = new WhereClause("password", new String[]{password},
                                         WhereClause.OPT_EQ, true);

                LogicalClause lc = new LogicalClause(LogicalClause.OPT_AND, wcp);

                Sync4jUser[] users = um.getUsers(lc);
                if (users.length == 1) {
                    session.setAttribute("user", users[0]);
                } else {
                    request.getSession().setAttribute(ERR_NOAUTH, "error");
                    redirect(PAGE_TO_FORWARD, request, response);
                    return;
                }

            } catch (Exception e) {
                request.getSession().setAttribute(ERR_NOAUTH, "error");
                redirect(PAGE_TO_FORWARD, request, response);
                return;
            }
        }

        request.getSession().setAttribute(ERR_NOAUTH, "");
        chain.doFilter(req, res);
    }

    /**
     * Initializes the filter.
     */
    public void init(FilterConfig filterConfig) {
        skipPages = filterConfig.getInitParameter("skip-pages");
    }

    /**
     * Destroys the filter.
     */
    public void destroy() {}

    // --------------------------------------------------------- Private Methods

    /**
     * Redirects the response.
     */
    private void redirect(String page,
                          HttpServletRequest request,
                          HttpServletResponse response)
    throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/" + page);
    }
}
