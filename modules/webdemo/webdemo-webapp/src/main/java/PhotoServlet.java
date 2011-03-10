/*
 * Funambol is a mobile platform developed by Funambol, Inc.
 * Copyright (C) 2007 Funambol, Inc.
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

import javax.naming.NamingException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.sql.DataSource;

import com.funambol.common.pim.contact.Photo;

import com.funambol.foundation.exception.DAOException;
import com.funambol.foundation.items.dao.PIMContactDAO;

import com.funambol.framework.server.Sync4jUser;
import com.funambol.framework.tools.DataSourceTools;

/**
 * Servlet used to show the contact pohotos
 *
 * @version $Id: PhotoServlet.java,v 1.1 2008-03-29 18:55:09 stefano_fornari Exp $
  */
public class PhotoServlet extends HttpServlet {

    // --------------------------------------------------------------- Constants
    public final static String PARAM_DATASOURCE_NAME = "ds-name";

    // ------------------------------------------------------------ Private data
    private String dataSourceName = null;
    private DataSource dataSource = null;

    /**
     * Initializes the servlet.
     * @param config
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dataSourceName = config.getInitParameter(PARAM_DATASOURCE_NAME);
        try {
            dataSource = DataSourceTools.lookupDataSource(dataSourceName);
        } catch (NamingException ex) {
            throw new ServletException("Error looking up datasource: " +
                                       dataSourceName, ex);
        }
    }

    /**
     * Process the HTTP Get request
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response)
    throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * Process the HTTP Get request
     *
     * @param request the request
     * @param response the response
     *
     * @throws ServletException if an error occurs
     * @throws IOException if an I/O error occurs
     */
     @Override
    public void doPost(
                       final HttpServletRequest request,
                       final HttpServletResponse response
                      ) throws ServletException, IOException {
        String id = request.getParameter("id");

        Photo photo = null;

        try {
            photo = getContactDAO(request).getPhoto(Long.valueOf(id));
        } catch (DAOException ex) {
            throw new ServletException("Error retrieving photo for contact: " + id, ex);
        }

        if (photo == null) {
            return ;
        }
        byte[] img = photo.getImage();

        //response.setContentType(resultData.getMimeType());
        if (img != null) {
            response.setContentLength(img.length);

            ServletOutputStream out = response.getOutputStream();
            out.write(img);
            out.flush();
            out.close();
        } else {
            return;
        }
    }

    /**
     * Clean up resources
     */
    public void destroy() {
    }

    /**
     * Checks if the contact DAO is null: if it is null, then it will be created
     * and will be saved in the session.
     *
     * @param request the HttpServlet Request
     *
     * @return contactDAO an instace on PIMContactDAO
     */
    private PIMContactDAO getContactDAO(final HttpServletRequest request) {
        Sync4jUser user = getUser(request);

        PIMContactDAO contactDAO =
            (PIMContactDAO)request.getSession().getAttribute("contactDAO");

        if (contactDAO == null) {
            contactDAO = new PIMContactDAO(user.getUsername());
            request.getSession().setAttribute("contactDAO", contactDAO);
        }

        return contactDAO;
    }

    /**
     * Gets the logged user
     *
     * @param request the HttpServlet Request
     * @return logged user
     */
    private Sync4jUser getUser(HttpServletRequest request) {
        return (Sync4jUser)request.getSession().getAttribute("user");
    }
}
